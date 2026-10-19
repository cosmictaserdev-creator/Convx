package com.convx.music.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/** Adds the white player controls over dark artwork with additive blending. Apply alpha after this modifier. */
// Repeating the additive pass strengthens the glow; saturating the gray source layer has no effect.
private const val AdditiveChromePasses = 2

/** Shared strength of the additive chrome on menu and tab-bar glyphs. */
const val MenuIconChrome = 0.8f

/**
 * Menu rows and tiles: a light additive lift only, so the glyph stays near-white and readable on the
 * glass instead of taking the heavier vibrant look the tab bar uses.
 */
const val MenuItemChrome = 0.25f

fun Modifier.additiveChrome(enabled: Boolean = true, strength: Float = 1f, bleed: Dp = 0.dp, passes: Int = AdditiveChromePasses): Modifier =
    if (!enabled) this else drawWithCache {
        // Record once and composite the same layer twice.
        val layer = obtainGraphicsLayer().apply { blendMode = BlendMode.Plus; alpha = strength }
        // The layer only holds what is drawn inside it, so [bleed] widens it past the node's own box:
        // a glyph that scales up (a bounce) is then not cut off at the edge of its square.
        val pad = bleed.toPx()
        val layerSize = IntSize(
            (size.width + 2 * pad).toInt().coerceAtLeast(1),
            (size.height + 2 * pad).toInt().coerceAtLeast(1),
        )
        onDrawWithContent {
            layer.record(layerSize) { translate(pad, pad) { this@onDrawWithContent.drawContent() } }
            translate(-pad, -pad) { repeat(passes) { drawLayer(layer) } }
        }
    }

/** Flamingo's overlayEffect uses one Plus blend pass, with opacity supplied by each control. */
fun Modifier.flamingoChrome(enabled: Boolean = true, strength: Float = 1f, bleed: Dp = 0.dp): Modifier =
    additiveChrome(enabled, strength, bleed, passes = 1)

/**
 * Apple's "vibrant" secondary fill -- the Now Playing unplayed track and time labels.
 * Content is drawn mid-grey ([VibrantChromeInk]) and colour-dodged onto whatever is
 * behind it: dodge by 50% grey doubles the backdrop, so a dark green tint comes back
 * as a brighter, *more saturated* green rather than a washed-out white.
 *
 * Draw the content in [VibrantChromeInk]; transparent pixels leave the backdrop alone.
 */
fun Modifier.vibrantChrome(enabled: Boolean = true, bleed: Dp = 0.dp): Modifier =
    if (!enabled) this else drawWithCache {
        val layer = obtainGraphicsLayer().apply { blendMode = BlendMode.ColorDodge }
        val pad = bleed.toPx()
        val layerSize = IntSize(
            (size.width + 2 * pad).toInt().coerceAtLeast(1),
            (size.height + 2 * pad).toInt().coerceAtLeast(1),
        )
        onDrawWithContent {
            layer.record(layerSize) { translate(pad, pad) { this@onDrawWithContent.drawContent() } }
            translate(-pad, -pad) { drawLayer(layer) }
        }
    }

/** Ink for [vibrantChrome] content: 50% grey, the dodge amount that doubles the backdrop. */
val VibrantChromeInk = androidx.compose.ui.graphics.Color(0xFF808080)

/**
 * Springy "wobble" on press for an existing [interactionSource]: the target scales down
 * while held and overshoots back on release (low damping = bouncy). Use on components that
 * already own an interaction source — nav bar items, glass buttons/pills — where
 * [bounceClick] can't wrap the click. Icon/label only; doesn't affect layout.
 */
fun Modifier.pressWobble(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.86f,
) = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.38f,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "pressWobble",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// Both click wrappers share this scale and the node-based press indication below.

private const val BouncePressedScale = 0.94f

private object BounceIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        BounceIndicationNode(interactionSource)

    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = 0x426F756E
}

private class BounceIndicationNode(
    private val interactionSource: InteractionSource,
) : Modifier.Node(), DrawModifierNode {
    private val scaleAnimation = Animatable(1f, 0.001f)
    private val activePresses = mutableSetOf<PressInteraction.Press>()
    private var interactionJob: Job? = null
    private var animationJob: Job? = null

    private fun animatePressed(pressed: Boolean) {
        animationJob?.cancel()
        animationJob = coroutineScope.launch {
            scaleAnimation.animateTo(
                targetValue = if (pressed) BouncePressedScale else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = 0.001f,
                ),
            )
        }
    }

    override fun onAttach() {
        interactionJob = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        activePresses += interaction
                        animatePressed(true)
                    }
                    is PressInteraction.Release -> {
                        activePresses -= interaction.press
                        animatePressed(activePresses.isNotEmpty())
                    }
                    is PressInteraction.Cancel -> {
                        activePresses -= interaction.press
                        animatePressed(activePresses.isNotEmpty())
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val scaleFactor = scaleAnimation.value
        if (scaleFactor == 1f) {
            drawContent()
        } else {
            scale(scaleFactor, pivot = center) { this@draw.drawContent() }
        }
    }

    override fun onDetach() {
        interactionJob?.cancel()
        animationJob?.cancel()
        interactionJob = null
        animationJob = null
        activePresses.clear()
    }
}

/** Clickable with no ripple by default. */
fun Modifier.bounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    onClick: () -> Unit
): Modifier = clickable(
    interactionSource = interactionSource,
    indication = if (enabled) BounceIndication else indication,
    enabled = enabled,
    onClick = onClick
)

/** [combinedClickable] with no ripple by default. */
fun Modifier.combinedBounceClick(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = combinedClickable(
    interactionSource = interactionSource,
    indication = if (enabled) BounceIndication else indication,
    enabled = enabled,
    onLongClick = onLongClick,
    onDoubleClick = onDoubleClick,
    onClick = onClick
)

/**
 * Apple's specular edge: a hairline that catches light along the top of a surface and
 * has faded out by its equator, so a sheet, pill or button reads as a raised material
 * instead of a flat fill.
 *
 * Traced from [shape]'s own outline rather than a `border()`, so it follows continuous
 * (superellipse) corners exactly instead of approximating them with circular arcs. Drawn
 * over the content, which is what makes it read as light on the surface rather than a
 * line under it.
 *
 * This is the *plain* rim. Surfaces that already sample the backdrop through
 * `Modifier.liquidGlass` get theirs from the glass system's own `Highlight` instead —
 * do not stack both.
 */
fun Modifier.rimLight(
    shape: Shape,
    color: Color = Color.White,
    /** Alpha at the very top edge. Fades to nothing by halfway down. */
    strength: Float = 0.55f,
    width: Dp = 1.dp,
): Modifier = drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val stroke = Stroke(width = width.toPx())
    val brush = Brush.verticalGradient(
        0f to color.copy(alpha = strength),
        0.5f to color.copy(alpha = strength * 0.12f),
        1f to Color.Transparent,
    )
    onDrawWithContent {
        drawContent()
        // Plus rather than the default alpha blend: same additiveChrome boost as the
        // nav bar's icons, so the hairline takes its glow off the surface it sits on
        // instead of just laying flat white over it.
        drawOutline(
            outline = outline,
            brush = brush,
            style = stroke,
            blendMode = BlendMode.Plus,
        )
    }
}

/**
 * Dark counterpart to the specular rim: a thin black outline traced on the
 * surface's own edge, drawn WITHOUT clipping so half the stroke sits outside
 * the bounds — the outermost layer, wider than the light rim.
 *
 * Mirrors the light rim's wide-on-edge / slim-at-ends read on TWO sides: the
 * ring is drawn twice, once per diagonal axis, each pass black at both ends
 * fading to nothing at its middle — so every side gets a dark outer edge
 * that swells and slims around the shape instead of a flat uniform line.
 *
 * Normal alpha blend, not Plus: Plus adds zero for black, so a Plus black rim
 * would be a no-op. Applied by [Modifier.liquidGlass] itself; do not stack
 * manually on glass surfaces.
 */
fun Modifier.outerDarkRim(
    shape: Shape,
    width: Dp = 1.6.dp,
    alpha: Float = 0.32f,
    /** Light rim's angle, degrees — the dark axis is derived from it. */
    angle: Float = 45f,
    /**
     * Draw only the inner half of the stroke, inset so clipping preserves the outline width.
     */
    insideOnly: Boolean = false,
): Modifier = drawWithCache {
    val inset = if (insideOnly) width.toPx() / 4f else 0f
    val outline = shape.createOutline(
        androidx.compose.ui.geometry.Size(size.width - inset * 2f, size.height - inset * 2f),
        layoutDirection,
        this,
    )
    val stroke = Stroke(width = if (insideOnly) width.toPx() / 2f else width.toPx())
    // One gradient per diagonal axis (canvas coords, y down — same convention
    // as the highlight shader's cos/sin normal). Each is black at both ends
    // fading out at its middle, so the two passes swell on opposite sides.
    val center = Offset(size.width / 2f, size.height / 2f)
    fun flankBrush(axisDeg: Float): Brush {
        val a = axisDeg * (PI / 180f).toFloat()
        val dir = Offset(cos(a), sin(a))
        // Matched so the shape's own corners land on the black ends.
        val reach = dir * (abs(dir.x) * size.width + abs(dir.y) * size.height) / 2f
        return Brush.linearGradient(
            0f to Color.Black.copy(alpha = alpha),
            0.5f to Color.Transparent,
            1f to Color.Black.copy(alpha = alpha),
            start = center - reach,
            end = center + reach,
        )
    }
    val firstBrush = flankBrush(angle)
    val secondBrush = flankBrush(angle + 90f)
    onDrawWithContent {
        drawContent()
        translate(inset, inset) {
            drawOutline(outline = outline, brush = firstBrush, style = stroke)
            drawOutline(outline = outline, brush = secondBrush, style = stroke)
        }
    }
}
/**
 * The radius an inner element needs for its curve to stay concentric with the [outer]
 * one it sits [inset] inside. Apple's rule, and the reason a card inside a sheet looks
 * wrong at the same radius as the sheet: parallel curves must differ by the gap between
 * them. Floors at 4dp so a large inset cannot square the corners off entirely.
 */
fun concentricRadius(outer: Dp, inset: Dp): Dp = (outer - inset).coerceAtLeast(4.dp)
