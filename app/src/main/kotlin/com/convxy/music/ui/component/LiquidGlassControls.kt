/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.lerp
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.backdrops.LayerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.emptyBackdrop
import com.convxy.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tanh

/**
 * Geometry and material constants shared by every liquid glass control.
 *
 * The point of keeping these in one place is that a slider thumb, a toggle knob,
 * a pill and an icon button should all read as the same physical material — the
 * same rest size, the same growth on press, the same blur-to-refraction ramp.
 * When each component picked its own numbers they drifted apart and the UI read
 * as several approximations of glass instead of one.
 *
 * Provenance, so the numbers can be re-derived rather than trusted:
 *
 *  - Thumb/rail geometry and the press morph come from the vendored Kyant0
 *    catalog `LiquidSlider` (`ui/component/backdrop/catalog/components/`), which
 *    is the reference implementation the app already ships: a 40x24dp capsule
 *    thumb over a 6dp capsule rail, blurred and opaque at rest, clearing into a
 *    refracting lens as the press completes.
 *  - The growth ratios come from the same source's `DampedDragAnimation`
 *    (`pressedScale`) and the nav bar puck, which Convx tunes to 78/56.
 *  - Rest blur (8dp) and pressed lens (10dp height / 14dp amount) are the
 *    catalog's own values, kept as the 1.0 point of the user-preference scaling
 *    in [liquidThumbBlurScale] / [liquidThumbLensScale].
 */
object LiquidGlassTokens {

    /** Resting rail thickness. The catalog slider's 6dp capsule. */
    val SliderTrackHeight: Dp = 6f.dp

    /**
     * Thumb bounds: the catalog slider's capsule, and its layout box at every
     * press depth — the layer block scales the drawing, never the layout, so the
     * rail maths and the touch target do not move while the thumb swells.
     */
    val SliderThumbSize: DpSize = DpSize(40f.dp, 24f.dp)

    /**
     * Thumb scale at rest and at full press — the catalog slider's uniform
     * `DampedDragAnimation(initialScale = 1f, pressedScale = 1.5f)`.
     *
     * The port used to shrink the thumb to a 12dp dot at rest and morph it into
     * the capsule on press, reasoning that iOS 26 does something like that. What
     * it produced was a squashed, undersized pill that read as a broken version
     * of the library's slider rather than a variation on it, so the catalog's own
     * numbers are back: full capsule at rest, swelled by half again while pressed.
     */
    const val SliderThumbInitialScale: Float = 1f
    const val SliderThumbPressedScale: Float = 1.5f

    /**
     * Minimum height of a slider control. Small enough to sit in the player's
     * existing rhythm, large enough that the 24dp thumb has room to grow without
     * being clipped by a neighbour. Callers can raise it (44dp+) for a bigger
     * touch target; the rails and thumb stay vertically centred.
     */
    val SliderControlHeight: Dp = 32f.dp

    /** Rail blur at rest, in dp — the catalog slider's value. */
    const val SliderThumbRestBlurDp: Float = 8f

    /** Lens refraction height at full press, in dp — the catalog slider's value. */
    const val SliderThumbLensHeightDp: Float = 10f

    /** Lens refraction amount at full press, in dp — the catalog slider's value. */
    const val SliderThumbLensAmountDp: Float = 14f

    /** Drop shadow under a pressed thumb — the catalog slider's value. */
    val ThumbShadowRadius: Dp = 4f.dp

    /** Inner shadow radius at full press — the catalog slider's value. */
    val ThumbInnerShadowRadius: Dp = 4f.dp

    /**
     * Blur radius (dp) that [GlassEffectConfig] ships with. Used as the 1.0
     * reference point when scaling the catalog's fixed effect sizes by the
     * user's preference, so the defaults reproduce the reference slider exactly
     * and moving the preference moves it proportionally.
     */
    const val DefaultBlurRadiusDp: Float = 2f

    /** Lens amount (0..1) that [GlassEffectConfig] ships with — see above. */
    const val DefaultLensAmount: Float = 0.6f

    /** Lens height (0..1) that [GlassEffectConfig] ships with — see above. */
    const val DefaultLensHeight: Float = 0.4f

    /**
     * How much the thumb leans into a fast drag, relative to its own size. The
     * catalog slider divides the tracked velocity by 10 and clamps the resulting
     * stretch to +-0.2, which is what keeps a flick from tearing the thumb apart.
     */
    const val VelocityStretchDivisor: Float = 10f
    const val VelocityStretchMaxX: Float = 0.75f
    const val VelocityStretchMaxY: Float = 0.25f
    const val VelocityStretchClamp: Float = 0.2f
}

/**
 * True when [this] is a real recorded backdrop rather than the "nothing provided"
 * sentinel returned by [emptyBackdrop].
 *
 * Glass controls must check this before drawing: sampling an empty backdrop
 * yields a surface with no content behind it, which renders as a flat dark slab
 * — indistinguishable from the bug this replaced. Falling back to the control's
 * non-glass renderer is both cheaper and correct.
 */
fun Backdrop?.isLiveGlassBackdrop(): Boolean = this != null && this !== emptyBackdrop()

/**
 * True when [this] is a layer backdrop that is actually attached — recording
 * something through `Modifier.layerBackdrop`.
 *
 * The house pattern for "glass material with nothing behind it to refract" is an
 * UNATTACHED screen-local backdrop: `DiyPlayerMockup`, `DiyEditorScreen` and
 * `HideOnScrollFAB` all hand one down so the surfaces under them get their tint
 * and rim without a capture, and, crucially, without any chance of sampling the
 * layer they are themselves being recorded into — which is a RenderNode cycle and
 * a native SIGSEGV in `RenderNode::prepareTreeImpl`, not a rendering glitch.
 *
 * Code that would hand those surfaces a *different*, attached backdrop has to
 * respect the choice rather than override it. That is what this is for: a caller
 * can tell "nothing is recording here, deliberately" from "the live app backdrop".
 */
fun Backdrop?.isAttachedGlassBackdrop(): Boolean =
    this is LayerBackdrop && layerCoordinates != null

/** Horizontal inset of the rail from each edge, in px: a quarter of the thumb. */
internal fun liquidRailInsetPx(thumbWidthPx: Float): Float = thumbWidthPx * 0.25f

/** Usable rail span in px once both insets are removed. Never negative. */
internal fun liquidRailSpanPx(totalWidthPx: Float, thumbWidthPx: Float): Float =
    (totalWidthPx - 2f * liquidRailInsetPx(thumbWidthPx)).coerceAtLeast(0f)

/** 0..1 position along the rail, guarded against a zero-width or zero-range control. */
internal fun liquidSliderProgress(value: Float, rangeStart: Float, rangeSpan: Float): Float =
    if (rangeSpan <= 0f) 0f else ((value - rangeStart) / rangeSpan).coerceIn(0f, 1f)

/**
 * Thumb centre, in px from the control's left edge.
 *
 * The centre travels the rail span exactly, so the thumb sits on the boundary
 * between filled and unfilled rail at every progress value — including 0 and 1,
 * where the inset keeps it from hanging off the end. Mirrored for RTL.
 */
internal fun liquidThumbCenterXPx(
    progress: Float,
    totalWidthPx: Float,
    thumbWidthPx: Float,
    isLtr: Boolean
): Float {
    val inset = liquidRailInsetPx(thumbWidthPx)
    val span = liquidRailSpanPx(totalWidthPx, thumbWidthPx)
    val p = progress.coerceIn(0f, 1f)
    return if (isLtr) inset + span * p else totalWidthPx - inset - span * p
}

/** `translationX` for a thumb whose layout box starts at the control's left edge. */
internal fun liquidThumbTranslationXPx(
    progress: Float,
    totalWidthPx: Float,
    thumbWidthPx: Float,
    isLtr: Boolean
): Float = liquidThumbCenterXPx(progress, totalWidthPx, thumbWidthPx, isLtr) - thumbWidthPx / 2f

/** Rail progress at an x position, mirroring [liquidThumbCenterXPx]. */
internal fun liquidProgressAtXPx(
    xPx: Float,
    totalWidthPx: Float,
    thumbWidthPx: Float,
    isLtr: Boolean
): Float {
    val inset = liquidRailInsetPx(thumbWidthPx)
    val span = liquidRailSpanPx(totalWidthPx, thumbWidthPx)
    if (span <= 0f) return 0f
    val fromStart = if (isLtr) xPx - inset else totalWidthPx - inset - xPx
    return (fromStart / span).coerceIn(0f, 1f)
}

/** The value a touch at [xPx] maps to — the inverse of [liquidThumbCenterXPx]. */
internal fun liquidValueAtXPx(
    xPx: Float,
    totalWidthPx: Float,
    thumbWidthPx: Float,
    rangeStart: Float,
    rangeSpan: Float,
    isLtr: Boolean
): Float =
    rangeStart + rangeSpan * liquidProgressAtXPx(xPx, totalWidthPx, thumbWidthPx, isLtr)

/**
 * The nearest detent to [value], for a slider with [steps] discrete points
 * between its endpoints — Material's own meaning, so `steps = choices.size - 2`
 * over a `0f..(choices.size - 1)f` range lands one detent per choice.
 *
 * Snapping happens here rather than in the caller's `onValueChange` so a stepped
 * control cannot report a value between detents even for one frame: the thumb is
 * drawn from the value the caller holds, and an unsnapped intermediate would put
 * it visually between two choices while the choice it commits is one of them.
 *
 * [steps] of 0 or less is a continuous slider and passes the value through.
 */
internal fun liquidSnapToStep(value: Float, rangeStart: Float, rangeSpan: Float, steps: Int): Float {
    if (steps <= 0 || rangeSpan <= 0f) return value
    val segment = rangeSpan / (steps + 1)
    val index = ((value - rangeStart) / segment).roundToInt()
    return (rangeStart + index * segment).coerceIn(rangeStart, rangeStart + rangeSpan)
}

/**
 * Whether a drag in progress should start scrubbing.
 *
 * A slider inside the player sheet competes with the sheet's own vertical drag.
 * Arming only once the gesture is past touch slop *and* predominantly horizontal
 * means dragging the sheet up from the seek bar never seeks, while a horizontal
 * scrub still starts immediately.
 */
internal fun liquidScrubArmed(accumulatedX: Float, accumulatedY: Float, touchSlop: Float): Boolean =
    abs(accumulatedX) > touchSlop && abs(accumulatedX) > abs(accumulatedY)

/**
 * Whether a completed gesture was a tap, and so should seek to its press point.
 *
 * Requires the gesture never to have armed *and* to have stayed within slop on
 * both axes: a vertical sheet drag that started on the slider is neither a scrub
 * nor a tap, and must not move the playhead.
 */
internal fun liquidIsTap(
    armed: Boolean,
    accumulatedX: Float,
    accumulatedY: Float,
    touchSlop: Float
): Boolean =
    !armed && abs(accumulatedX) <= touchSlop && abs(accumulatedY) <= touchSlop

/**
 * Scales the catalog's fixed rest blur by the user's blur preference.
 *
 * At the shipped default (2dp) this returns 1.0, so the thumb blurs by exactly
 * the catalog's 8dp. Turning blur down to 0 removes it; turning it up caps at
 * 1.5x so a heavy preference cannot smear the thumb into its own rail.
 */
internal fun liquidThumbBlurScale(configuredBlurRadiusDp: Float): Float =
    (configuredBlurRadiusDp / LiquidGlassTokens.DefaultBlurRadiusDp).coerceIn(0f, 1.5f)

/** Lens height scale — see [liquidThumbBlurScale]. */
internal fun liquidThumbLensHeightScale(configuredLensHeight: Float): Float =
    (configuredLensHeight / LiquidGlassTokens.DefaultLensHeight).coerceIn(0f, 1.5f)

/** Lens amount scale — see [liquidThumbBlurScale]. */
internal fun liquidThumbLensAmountScale(configuredLensAmount: Float): Float =
    (configuredLensAmount / LiquidGlassTokens.DefaultLensAmount).coerceIn(0f, 1.5f)

/**
 * Non-uniform rest scale for a capsule thumb box, so it reads as a circle of
 * [restSize] while pressed geometry stays a [thumbSize] capsule.
 */
/**
 * The catalog slider's velocity stretch factor for one axis: how much a fast drag
 * deforms the thumb along it, before the caller applies it (divided on the drag
 * axis, multiplied across it — the asymmetry is the reference implementation's).
 *
 * Clamped so a flick cannot invert a scale and turn the thumb inside out.
 */
internal fun liquidThumbVelocityFactor(velocity: Float, axisMax: Float): Float {
    val clamped = (velocity / LiquidGlassTokens.VelocityStretchDivisor * axisMax)
        .coerceIn(-LiquidGlassTokens.VelocityStretchClamp, LiquidGlassTokens.VelocityStretchClamp)
    return 1f - clamped
}

/**
 * Whether a surface should render as real liquid glass right now.
 *
 * One definition, because four things have to agree before a surface can refract
 * and every component was re-deriving them slightly differently: the user's
 * per-component switch, platform capability (API 31 + not low-RAM), the chosen
 * [GlassStyle], and whether anything is actually recorded behind the surface.
 *
 * Components use this to pick between their glass and flat renderers. Note that
 * [Modifier.liquidGlass] already degrades on its own — this exists so a component
 * can also change its *content* colour and skip gesture-only-glass work, which
 * requires knowing the answer before building the modifier chain.
 */
@Composable
fun rememberLiquidGlassActive(
    component: GlassComponent,
    config: GlassEffectConfig = LocalGlassEffectConfig.current,
    backdrop: Backdrop? = LocalAppBackdrop.current,
): Boolean =
    config.isEnabledFor(component) &&
            isGlassAllowed() &&
            !shouldUseTranslucentGlassFallback(config.style, isRenderEffectSupported()) &&
            backdrop.isLiveGlassBackdrop()

/**
 * How much a pressed surface swells, as a scale factor on a surface [heightPx]
 * tall.
 *
 * [growthPx] is a length rather than a ratio — 4dp at the current density —
 * because "grow by 4dp" is the same visual amount on a 24dp chip and on a 100dp
 * play button, while "grow by 4%" is not. Expressed against the surface's own
 * height, one constant serves both.
 */
internal fun liquidPressGrowth(growthPx: Float, heightPx: Float, progress: Float): Float =
    if (heightPx <= 0f) 1f else lerp(1f, 1f + growthPx / heightPx, progress)

/**
 * How far a pressed surface leans toward the finger, in px, bounded by
 * [maxOffsetPx].
 *
 * Through a tanh rather than a clamp: the derivative at the centre is
 * [initialDerivative], so small finger movements move the surface almost
 * one-to-one, and the lean saturates as the finger nears the edge instead of
 * stopping dead at a hard limit. Clamping would freeze the motion while the
 * finger keeps travelling.
 */
internal fun liquidPressTranslationPx(
    offsetPx: Float,
    maxOffsetPx: Float,
    initialDerivative: Float = 0.05f,
): Float =
    if (maxOffsetPx <= 0f) 0f else maxOffsetPx * tanh(initialDerivative * offsetPx / maxOffsetPx)

/**
 * How much of a drag may stretch a surface along one axis: all of it along the
 * long one, and the sides' ratio of that along the short one.
 *
 * A square takes the full stretch on both axes. A 330x121 pill takes it
 * horizontally and 121/330 of it vertically, so dragging a pill sideways reads as
 * the pill being pulled along its length rather than as a blob inflating.
 *
 * Sizes are Float because the layer scope's own size is a Float geometry Size.
 * Dividing them as Ints would quantise the ratio to "wider" or "not wider" and
 * throw away the fraction that makes an off-square surface stretch unevenly.
 */
internal fun liquidPressAnisotropy(widthPx: Float, heightPx: Float, horizontal: Boolean): Float =
    if (widthPx <= 0f || heightPx <= 0f) 0f
    else if (horizontal) (widthPx / heightPx).fastCoerceAtMost(1f)
    else (heightPx / widthPx).fastCoerceAtMost(1f)

/**
 * Extra scale from dragging, on top of [liquidPressGrowth].
 *
 * [axisUnit] is cos(angle) on the horizontal axis and sin(angle) on the vertical
 * one, both from the finger's offset angle, so a drag straight across a surface
 * stretches it sideways and a drag at 45° splits the stretch between the two.
 */
internal fun liquidPressStretch(
    growthPx: Float,
    heightPx: Float,
    axisUnit: Float,
    offsetPx: Float,
    maxDimensionPx: Float,
    anisotropy: Float,
): Float =
    if (heightPx <= 0f || maxDimensionPx <= 0f) 0f
    else growthPx / heightPx * abs(axisUnit * offsetPx / maxDimensionPx) * anisotropy

/**
 * The press transform shared by every liquid glass button: a small growth, a lean
 * toward the finger, and extra stretch along the axis the finger drags.
 *
 * Returned as a [GraphicsLayerScope] block so it runs in the draw phase — the
 * values it reads ([InteractiveHighlight.pressProgress] and its offset) are
 * animated every frame, and reading them here re-invalidates only this layer
 * rather than recomposing the button.
 */
fun liquidGlassPressLayerBlock(
    interaction: InteractiveHighlight
): GraphicsLayerScope.() -> Unit = {
    val progress = interaction.pressProgress
    val offset = interaction.offset
    // Width as well as height: the anisotropy divides by both, and a zero-width
    // layer would produce NaN rather than a transform.
    if (progress > 0f && size.height > 0f && size.width > 0f) {
        val growthPx = 4f.dp.toPx()
        val angle = atan2(offset.y, offset.x)
        val anisotropyX = liquidPressAnisotropy(size.width, size.height, horizontal = true)
        val anisotropyY = liquidPressAnisotropy(size.width, size.height, horizontal = false)

        val growth = liquidPressGrowth(growthPx, size.height, progress)
        scaleX = growth + liquidPressStretch(
            growthPx = growthPx,
            heightPx = size.height,
            axisUnit = cos(angle),
            offsetPx = offset.x,
            maxDimensionPx = size.maxDimension,
            anisotropy = anisotropyX,
        )
        scaleY = growth + liquidPressStretch(
            growthPx = growthPx,
            heightPx = size.height,
            axisUnit = sin(angle),
            offsetPx = offset.y,
            maxDimensionPx = size.maxDimension,
            anisotropy = anisotropyY,
        )
        translationX = liquidPressTranslationPx(offset.x, size.minDimension)
        translationY = liquidPressTranslationPx(offset.y, size.minDimension)
    }
}
