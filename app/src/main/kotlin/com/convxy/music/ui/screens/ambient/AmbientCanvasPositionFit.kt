/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.ambient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import com.convxy.music.constants.AmbientCanvasAnchorSide
import com.convxy.music.constants.AmbientCanvasFitMode

/**
 * Canvas Position & Fit for Ambient Mode.
 *
 * Ambient Mode is a 16:9 layout: album artwork on one side, lyrics on the other. A canvas
 * clipped from a phone-shaped source is 3:4 or 9:16, so filling the whole screen with it
 * (the default) throws away most of the frame — a 9:16 canvas loses roughly two thirds of
 * its height to the crop.
 *
 * Position & Fit instead keeps the *whole* canvas inside a panel hugging one side of the
 * 16:9 layout, and swaps the flat dim for an asymmetric veil: stronger on the canvas' own
 * side, where the moving picture sits behind the artwork, and lighter on the opposite side,
 * where the lyrics and the progress ring need a calm background. A canvas that is not
 * portrait-ish falls back to the ordinary full-width behaviour, so enabling the option never
 * makes a landscape canvas look worse.
 *
 * The geometry and gradient math lives here as plain functions so it can be unit tested
 * without a Compose runtime; the composables below only translate those numbers into
 * brushes.
 *
 * Defaults and the slider ranges/steps below are shared by Settings → Ambient Mode and the
 * Ambient screen itself, so the two can never drift apart — and every default lands on its
 * slider's step grid, so a value never snaps the first time the slider is touched.
 */
object AmbientCanvasFitDefaults {
    /** Fraction of the screen width the side panel may claim. */
    const val SideWidth = 0.48f

    /** Extra veil added on the canvas' side, on top of the ordinary canvas dim. */
    const val SideGradient = 0.35f

    /** Fraction of the screen width across which the strong veil decays to its floor. */
    const val GradientSpread = 0.60f

    /** Veil left over on the opposite (lyrics) side. */
    const val FarVeil = 0.10f

    /** Fraction of the panel width that fades out, so its edge does not read as a hard seam. */
    const val EdgeFeather = 0.15f

    /**
     * Canvases at or above this width/height ratio keep the default full-width behaviour.
     * 1.15 keeps 3:4 and 9:16 side-panelled while leaving 16:9 and 4:3 full width.
     */
    const val PortraitAspectLimit = 1.15f

    val SideWidthRange = 0.28f..0.72f
    val SideGradientRange = 0f..0.8f
    val GradientSpreadRange = 0.2f..1f
    val FarVeilRange = 0f..0.6f
    val EdgeFeatherRange = 0f..0.4f

    const val SideWidthSteps = 11
    const val SideGradientSteps = 15
    const val GradientSpreadSteps = 15
    const val FarVeilSteps = 11
    const val EdgeFeatherSteps = 7
}

/**
 * Width of the side panel as a fraction of the screen width.
 *
 * In [AmbientCanvasFitMode.FIT] the panel shrinks to exactly hug the canvas at full height,
 * so there is no dead letterboxing inside it; ZOOM and STRETCH use the configured width as
 * given. A canvas that is not portrait-ish returns `1f`, meaning "take the whole screen",
 * which is Ambient Mode's default behaviour.
 *
 * [videoAspect] and [screenAspect] are width / height ratios; a non-positive [videoAspect]
 * means the decoder has not reported a size yet, in which case the configured width is used
 * as the best guess (the canvas fades in over ~300 ms, so the settle is not visible).
 */
fun ambientCanvasPanelFraction(
    videoAspect: Float,
    screenAspect: Float,
    requestedFraction: Float,
    fitMode: AmbientCanvasFitMode,
): Float {
    val requested = requestedFraction.coerceIn(0.2f, 0.85f)
    if (videoAspect <= 0f) return requested
    if (videoAspect >= AmbientCanvasFitDefaults.PortraitAspectLimit) return 1f
    if (fitMode != AmbientCanvasFitMode.FIT) return requested
    if (screenAspect <= 0f) return requested
    // A canvas shown at the full panel height is (videoAspect / screenAspect) of the width.
    val hugFraction = (videoAspect / screenAspect).coerceIn(0.2f, 1f)
    return minOf(requested, hugFraction)
}

/** True when the canvas is narrow enough to be placed in a side panel instead of the screen. */
fun ambientCanvasUsesSidePanel(panelFraction: Float): Boolean = panelFraction < 0.999f

/** Resolves AUTO against the layout direction, since Ambient Mode mirrors its art/lyrics row. */
fun ambientCanvasAnchoredRight(anchor: AmbientCanvasAnchorSide, isRtl: Boolean): Boolean =
    when (anchor) {
        AmbientCanvasAnchorSide.LEFT -> false
        AmbientCanvasAnchorSide.RIGHT -> true
        // AUTO keeps the picture on the artwork side, i.e. the side opposite the lyrics.
        AmbientCanvasAnchorSide.AUTO -> isRtl
    }

/**
 * Alphas at the two edges of the screen: the stronger one on the canvas' side, the lighter
 * one over the lyrics. The configured canvas dim still applies — it becomes the base the
 * side gradient is added to — while the far side keeps only its own veil, which is what
 * makes the lyrics side visibly lighter than the full-width background.
 */
fun ambientCanvasVeilAlphas(dim: Float, sideGradient: Float, farVeil: Float): Pair<Float, Float> {
    val near = (dim.coerceIn(0f, 0.75f) + sideGradient.coerceIn(0f, 0.8f)).coerceAtMost(0.94f)
    val far = farVeil.coerceIn(0f, near)
    return near to far
}

/**
 * The veil as (x position from the left, alpha) pairs in screen space, for a horizontal
 * gradient. The falloff is smoothstepped so neither edge of the panel gets a visible band
 * boundary. With [anchoredRight] the profile is mirrored so the strong end sits on the right.
 */
fun ambientCanvasVeilStops(
    nearAlpha: Float,
    farAlpha: Float,
    spread: Float,
    anchoredRight: Boolean,
    samples: Int = 6,
): List<Pair<Float, Float>> {
    val near = nearAlpha.coerceIn(0f, 1f)
    val far = farAlpha.coerceIn(0f, near)
    val reach = spread.coerceIn(0.05f, 1f)
    val steps = samples.coerceAtLeast(1)

    val profile = (0..steps).map { index ->
        val fromCanvasSide = index.toFloat() / steps
        val eased = (fromCanvasSide / reach).coerceIn(0f, 1f)
        val shaped = eased * eased * (3f - 2f * eased)
        fromCanvasSide to (near + (far - near) * shaped)
    }

    return if (anchoredRight) {
        profile.map { (1f - it.first) to it.second }.reversed()
    } else {
        profile
    }
}

/**
 * The asymmetric veil drawn over the canvas: strongest at the edge the canvas is anchored to,
 * settling to its floor toward the lyrics. Replaces the flat full-screen dim while
 * Position & Fit is in effect.
 */
@Composable
fun AmbientCanvasVeil(
    nearAlpha: Float,
    farAlpha: Float,
    spread: Float,
    anchoredRight: Boolean,
    modifier: Modifier = Modifier,
) {
    val stops = ambientCanvasVeilStops(
        nearAlpha = nearAlpha,
        farAlpha = farAlpha,
        spread = spread,
        anchoredRight = anchoredRight,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val colorStops: Array<Pair<Float, Color>> = stops
                    .map { (position, alpha) -> position to Color.Black.copy(alpha = alpha) }
                    .toTypedArray()
                val brush = Brush.horizontalGradient(*colorStops)
                onDrawBehind { drawRect(brush = brush) }
            },
    )
}

/**
 * Fades the trailing edge of the side panel away so the canvas blends into the ambient glow
 * instead of ending on a straight line. The mask has to run on its own offscreen layer:
 * `DstIn` can only erase what is inside the layer it is drawn into, and the canvas itself is
 * a TextureView hosted by an AndroidView.
 */
fun Modifier.ambientCanvasEdgeFeather(
    fraction: Float,
    anchoredRight: Boolean,
): Modifier {
    val feather = fraction.coerceIn(0f, 0.9f)
    if (feather <= 0.001f) return this

    return this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithCache {
            val clear = Color.Transparent
            val keep = Color.White
            // Only the trailing edge — the edge facing the lyrics — is faded, so the panel
            // stays opaque under the artwork and settles into the glow where it ends.
            val brush = if (anchoredRight) {
                Brush.horizontalGradient(0f to clear, feather to keep, 1f to keep)
            } else {
                Brush.horizontalGradient(0f to keep, 1f - feather to keep, 1f to clear)
            }
            onDrawWithContent {
                drawContent()
                drawRect(brush = brush, blendMode = BlendMode.DstIn)
            }
        }
}
