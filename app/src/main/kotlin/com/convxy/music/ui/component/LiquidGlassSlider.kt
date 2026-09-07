/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.BackdropEffectScope
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberCombinedBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.backdrop.catalog.utils.DampedDragAnimation
import com.convxy.music.ui.component.backdrop.catalog.utils.inspectDragGestures
import com.convxy.music.ui.component.backdrop.drawBackdrop
import com.convxy.music.ui.component.backdrop.effects.blur
import com.convxy.music.ui.component.backdrop.effects.colorControls
import com.convxy.music.ui.component.backdrop.effects.lens
import com.convxy.music.ui.component.backdrop.highlight.Highlight
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported
import com.convxy.music.ui.component.backdrop.isRuntimeShaderSupported
import com.convxy.music.ui.component.backdrop.shadow.InnerShadow
import com.convxy.music.ui.component.backdrop.shadow.Shadow
import kotlin.math.min
import kotlinx.coroutines.flow.collectLatest

/**
 * A liquid glass slider: a capsule rail with a thumb that is a live lens over it.
 *
 * This is the app's real seek bar / value control, built on the vendored Kyant0
 * backdrop engine rather than on Material's `Slider`. It replaces the pattern the
 * app had before — a Material `Slider` whose thumb slot was an empty `Spacer` and
 * whose track was a `Canvas` line — which could not refract, could not morph and
 * had no press physics at all.
 *
 * ## How it renders
 *
 * Exactly one surface in this control samples a backdrop: the thumb. That is
 * deliberate. A `drawBackdrop` per surface costs a capture plus a RenderEffect
 * chain, so a slider that made its rail, its fill *and* its thumb glass would pay
 * three times for one small control. Instead:
 *
 *  - the two rails are plain coloured boxes recorded into a [rememberLayerBackdrop]
 *    layer, which is cheap and, importantly, is *content* rather than glass;
 *  - the thumb draws a [rememberCombinedBackdrop] of the app backdrop and that
 *    rail layer, so it refracts both what is behind the player and the rail it
 *    sits on;
 *  - the rail layer is drawn into the thumb scaled by the press progress
 *    (`scaleX` 2/3 -> 1, `scaleY` 0 -> 1), so at rest the thumb shows none of the
 *    rail and as the press completes the rail appears to swell inside it. This is
 *    the reference implementation's own trick and what sells the "the thumb picks
 *    up the track" reading.
 *
 * ## How it behaves
 *
 * Press physics come from [DampedDragAnimation], the same vendored driver the
 * floating tab bar's puck uses, so the thumb's springs match the rest of the
 * chrome instead of being a second set of numbers:
 *
 *  - press: the thumb swells uniformly, 1 to 1.5 — the catalog's own
 *    initialScale/pressedScale, so it is the full [thumbSize] capsule at rest and
 *    half again as big under the finger — while its rest blur fades out, its lens
 *    refraction fades in, and its specular rim and inner shadow appear;
 *  - drag: the tracked velocity stretches the thumb along the drag axis and
 *    squeezes it across, clamped so a flick cannot tear it apart;
 *  - release: the surface waits for the value to settle before un-pressing, so a
 *    tap "plucks" — the thumb swells, springs to the tapped position, then shrinks.
 *
 * Gestures are absolute, not delta-accumulated onto the previous value: pressing
 * anywhere on the control seeks to that point, and a drag scrubs from there. A
 * drag only starts scrubbing once it is past touch slop and predominantly
 * horizontal (see [liquidScrubArmed]), so dragging the player sheet up by its seek
 * bar moves the sheet and leaves the playhead alone.
 *
 * ## Fallback
 *
 * With glass unavailable — below API 31, a low-RAM device, [GlassStyle.TRANSPARENT],
 * or no live [LocalAppBackdrop] in scope — the same geometry and the same springs
 * are painted with [Canvas] instead: capsule rail, fill, and a thumb that grows on
 * press. Nothing about the interaction changes, only the material, so the control
 * is never a hole in the UI and never crashes on a missing backdrop.
 *
 * @param value the current value, read as a lambda so the control can follow a
 * 10Hz position poll without recomposing on every sample.
 * @param onValueChange called continuously while scrubbing, and once on a tap.
 * @param onValueChangeFinished called when a gesture that changed the value ends.
 * Map this to the actual `seekTo`.
 * @param activeColor filled rail colour; [Color.Unspecified] takes the reference
 * implementation's iOS blue.
 * @param inactiveColor unfilled rail colour; [Color.Unspecified] takes the
 * reference implementation's theme-dependent translucent grey.
 * @param thumbColor the thumb's opaque rest fill, which fades out as the press
 * completes and the glass takes over.
 * @param component which per-component glass switch in [GlassEffectConfig] gates
 * this surface.
 */
@Composable
fun LiquidGlassSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    /** Discrete points between the endpoints, Material's meaning of `steps`: 0 is
     *  continuous, 1 adds a single detent in the middle. Values are snapped
     *  before they are reported, so the thumb only ever rests on a detent. */
    steps: Int = 0,
    activeColor: Color = Color.Unspecified,
    inactiveColor: Color = Color.Unspecified,
    thumbColor: Color = Color.White,
    config: GlassEffectConfig = LocalGlassEffectConfig.current,
    backdrop: Backdrop? = LocalAppBackdrop.current,
    trackHeight: Dp = LiquidGlassTokens.SliderTrackHeight,
    thumbSize: DpSize = LiquidGlassTokens.SliderThumbSize,
    controlHeight: Dp = LiquidGlassTokens.SliderControlHeight,
    component: GlassComponent = GlassComponent.PLAYER,
) {
    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val animationScope = rememberCoroutineScope()

    val rangeStart = valueRange.start
    val rangeSpanRaw = valueRange.endInclusive - valueRange.start
    // An unknown duration gives a 0..0 range. Keep a non-zero span for the
    // animation driver (its progress divides by it) and take no gestures, so the
    // control renders as an empty rail rather than producing NaN.
    val rangeSpan = if (rangeSpanRaw > 0f) rangeSpanRaw else 1f
    val interactive = enabled && rangeSpanRaw > 0f

    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentSteps by rememberUpdatedState(steps)
    val currentOnFinished by rememberUpdatedState(onValueChangeFinished)

    val isDark = isSystemInDarkTheme()
    val active =
        if (activeColor.isSpecified) activeColor
        else if (isDark) Color(0xFF0091FF) else Color(0xFF0088FF)
    val inactive =
        if (inactiveColor.isSpecified) inactiveColor
        else if (isDark) Color(0xFF787880).copy(alpha = 0.36f)
        else Color(0xFF787878).copy(alpha = 0.2f)

    // The inherited backdrop is not a precondition for glass here, unlike every
    // other surface: the thumb refracts a track this slider records itself, so it
    // has something to bend even where the inherited backdrop has to be skipped
    // (see [outer] below). That is what makes it usable on screens inside the
    // recorded root, where an inherited layer backdrop would be a cycle.
    val useGlass =
        config.isEnabledFor(component) &&
                isGlassAllowed() &&
                !shouldUseTranslucentGlassFallback(config.style, isRenderEffectSupported())

    // Skips the inherited backdrop when this slider sits inside the node recording
    // it, which is the case on every settings screen and in the search overlay.
    val outer = rememberOuterBackdropSampler(backdrop)

    val thumbWidthPx = with(density) { thumbSize.width.toPx() }
    val thumbHeightPx = with(density) { thumbSize.height.toPx() }
    val insetDp = with(density) { liquidRailInsetPx(thumbWidthPx).toDp() }

    // Read by the layout and draw lambdas below, which run outside composition.
    // A plain holder rather than snapshot state on purpose: these only change on a
    // resize, and as state they would invalidate composition — the whole slider —
    // for values only the draw phase needs.
    val geometry = remember { LiquidSliderGeometry() }

    val damped = remember(animationScope, valueRange, rangeSpan) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = currentValue().coerceIn(valueRange),
            valueRange = valueRange,
            // Half a thousandth of the range: tight enough that a 3 minute track
            // settles to within ~45ms of the target, loose enough that the spring
            // stops instead of chasing float noise forever.
            visibilityThreshold = (rangeSpan * 0.0005f).coerceAtLeast(0.001f),
            // The catalog's own numbers: the thumb is its full capsule at rest and
            // swells by half again while pressed. These drive the layer block
            // directly -- the port once left them inert at 1f and morphed a 12dp
            // dot into the capsule per axis instead, which read as a squashed pill.
            initialScale = LiquidGlassTokens.SliderThumbInitialScale,
            pressedScale = LiquidGlassTokens.SliderThumbPressedScale,
            onDragStarted = {},
            onDragStopped = {},
            onDrag = { _, _ -> },
            // Convx's own note on this parameter: the reference implementation's
            // 0.5f is underdamped, so velocity rings after every change of speed
            // and the stretch wobbles instead of following the finger.
            velocityDampingRatio = 1f,
        )
    }

    // One collector for the whole lifetime of the control. At rest it snaps, so
    // the thumb tracks playback position exactly instead of trailing a spring
    // behind a 10Hz poll; mid-gesture it springs, which is what gives the drag its
    // damped feel and feeds the velocity tracker the thumb stretches with.
    LaunchedEffect(damped, interactive) {
        snapshotFlow { currentValue() }
            .collectLatest { sampled ->
                val target = sampled.coerceIn(valueRange)
                if (damped.targetValue != target) {
                    if (interactive && damped.pressProgress > 0f) {
                        damped.updateValue(target)
                    } else {
                        damped.snapValue(target)
                    }
                }
            }
    }

    val gestureModifier =
        if (interactive) {
            Modifier.pointerInput(damped) {
                val slop = viewConfiguration.touchSlop
                var downX = 0f
                var accumulatedX = 0f
                var accumulatedY = 0f
                var armed = false
                var changed = false

                fun valueAt(x: Float) = liquidSnapToStep(
                    value = liquidValueAtXPx(
                        xPx = x,
                        totalWidthPx = geometry.totalWidthPx,
                        thumbWidthPx = geometry.thumbWidthPx,
                        rangeStart = geometry.rangeStart,
                        rangeSpan = geometry.rangeSpan,
                        isLtr = geometry.isLtr,
                    ),
                    rangeStart = geometry.rangeStart,
                    rangeSpan = geometry.rangeSpan,
                    steps = currentSteps,
                )

                fun commit() {
                    if (liquidIsTap(armed, accumulatedX, accumulatedY, slop)) {
                        // A tap seeks to where it landed. A gesture that was neither
                        // a tap nor a horizontal scrub — the sheet drag this control
                        // lives inside — must not move the playhead.
                        changed = true
                        currentOnValueChange(valueAt(downX))
                    }
                    if (changed) currentOnFinished?.invoke()
                    damped.release()
                }

                inspectDragGestures(
                    onDragStart = { down ->
                        downX = down.position.x
                        accumulatedX = 0f
                        accumulatedY = 0f
                        armed = false
                        changed = false
                        damped.press()
                    },
                    onDragEnd = { commit() },
                    onDragCancel = { commit() },
                ) { change, dragAmount ->
                    accumulatedX += dragAmount.x
                    accumulatedY += dragAmount.y
                    if (!armed && liquidScrubArmed(accumulatedX, accumulatedY, slop)) {
                        armed = true
                    }
                    if (armed) {
                        change.consume()
                        val next = valueAt(downX + accumulatedX)
                        if (next != currentValue()) {
                            changed = true
                            currentOnValueChange(next)
                        }
                    }
                }
            }
        } else {
            Modifier
        }

    val reportedValue = if (interactive) currentValue().coerceIn(valueRange) else rangeStart

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = controlHeight)
            .onSizeChanged { geometry.totalWidthPx = it.width.toFloat() }
            .then(outer.measureModifier)
            .then(gestureModifier)
            .semantics {
                this[SemanticsProperties.ProgressBarRangeInfo] =
                    ProgressBarRangeInfo(reportedValue, valueRange)
                if (!enabled) disabled()
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        SideEffect {
            geometry.isLtr = isLtr
            geometry.rangeStart = rangeStart
            geometry.rangeSpan = rangeSpan
            geometry.thumbWidthPx = thumbWidthPx
        }

        if (useGlass) {
            val capsule = remember { RoundedCornerShape(percent = 50) }
            val trackBackdrop = rememberLayerBackdrop()

            // The rails: plain colour, recorded once into a layer so the thumb has
            // something to refract. No backdrop capture, no RenderEffect.
            Box(Modifier.layerBackdrop(trackBackdrop)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = insetDp),
                    contentAlignment = if (isLtr) Alignment.CenterStart else Alignment.CenterEnd,
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(trackHeight)
                            .clip(capsule)
                            .background(inactive)
                    )
                    Box(
                        Modifier
                            .clip(capsule)
                            .background(active)
                            .height(trackHeight)
                            // Innermost on purpose: reporting the reduced width here
                            // is what makes the outer clip and background draw only
                            // the filled part of the rail.
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val width =
                                    (constraints.maxWidth * damped.progress).fastRoundToInt()
                                layout(width, placeable.height) {
                                    placeable.place(0, 0)
                                }
                            }
                    )
                }
            }

            // The rail layer as seen from inside the thumb: squashed to nothing at
            // rest, full size at full press, so the rail appears to swell into the
            // thumb as it is pressed. Remembered on the animation alone — reading
            // pressProgress here would rebuild it every frame and, with it, the
            // combined backdrop and the whole effect chain.
            val trackDraw: DrawScope.(DrawScope.() -> Unit) -> Unit = remember(damped) {
                { drawBackdrop ->
                    val press = damped.pressProgress
                    scale(
                        lerp(2f / 3f, 1f, press),
                        lerp(0f, 1f, press),
                    ) {
                        drawBackdrop()
                    }
                }
            }

            val saturation = glassSaturation(config.vibrancy)
            val restBlurPx = with(density) {
                (LiquidGlassTokens.SliderThumbRestBlurDp * liquidThumbBlurScale(config.blurRadius))
                    .dp.toPx()
            }
            val lensHeightPx = with(density) {
                (LiquidGlassTokens.SliderThumbLensHeightDp *
                        liquidThumbLensHeightScale(config.lensHeight)).dp.toPx()
            }
            val lensAmountPx = with(density) {
                (LiquidGlassTokens.SliderThumbLensAmountDp *
                        liquidThumbLensAmountScale(config.lensAmount)).dp.toPx()
            }
            val plainBlur = config.style == GlassStyle.BLUR
            val depthEffect = config.depthEffect
            val chromaticAberration = config.chromaticAberration

            // Every block below is remembered on exactly what it reads, so the
            // drawBackdrop element compares equal across recompositions and the
            // engine keeps its capture. Press-driven values are read inside the
            // blocks, at draw time, which is what animates them.
            val shapeBlock: () -> Shape = remember(capsule) { { capsule } }

            val effectsBlock: BackdropEffectScope.() -> Unit = remember(
                damped,
                saturation,
                restBlurPx,
                lensHeightPx,
                lensAmountPx,
                plainBlur,
                depthEffect,
                chromaticAberration,
            ) {
                {
                    val press = damped.pressProgress
                    if (saturation != 1f) {
                        colorControls(saturation = saturation)
                    }
                    // Frosted at rest, clearing as the press completes — the blur
                    // is what makes an unpressed thumb read as a material rather
                    // than a hole punched in the artwork.
                    val blurPx = restBlurPx * (1f - press)
                    if (blurPx > 0f) {
                        blur(blurPx)
                    }
                    // Refraction is the pressed state: none at rest, full at the end
                    // of the press, so the thumb only bends light while it is held.
                    if (!plainBlur && press > 0f && isRuntimeShaderSupported()) {
                        lens(
                            refractionHeight = lensHeightPx * press,
                            refractionAmount = lensAmountPx * press,
                            depthEffect = depthEffect,
                            chromaticAberration = chromaticAberration,
                        )
                    }
                }
            }

            val highlightBlock: (() -> Highlight?)? = remember(damped) {
                {
                    val press = damped.pressProgress
                    if (press <= 0f) {
                        null
                    } else {
                        // The reference implementation narrows and dims the ambient
                        // rim for a thumb this small: a full-width ambient highlight
                        // on 24dp of height reads as a bright band, not lit glass.
                        // Note this deliberately ignores GlassEffectConfig.highlightColor:
                        // HighlightStyle.Ambient derives its colour from its own
                        // intensity and exposes no colour parameter, so honouring a
                        // custom rim colour here would mean switching the thumb to a
                        // directional HighlightStyle.Default and losing the ambient
                        // wrap that makes it read as a lens.
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = press,
                        )
                    }
                }
            }

            val shadowBlock: (() -> Shadow?)? = remember {
                {
                    Shadow(
                        radius = LiquidGlassTokens.ThumbShadowRadius,
                        color = Color.Black.copy(alpha = 0.05f),
                    )
                }
            }

            val innerShadowBlock: (() -> InnerShadow?)? = remember(damped) {
                {
                    val press = damped.pressProgress
                    if (press <= 0f) {
                        null
                    } else {
                        InnerShadow(
                            radius = LiquidGlassTokens.ThumbInnerShadowRadius * press,
                            alpha = press,
                        )
                    }
                }
            }

            val layerBlock: GraphicsLayerScope.() -> Unit = remember(damped) {
                {
                    // Uniform press swell straight from the animation driver, then
                    // the catalog's velocity stretch: the thumb lengthens along a
                    // fast drag and narrows across it, clamped so a flick cannot
                    // invert a scale. Divided on X and multiplied on Y, which is
                    // the asymmetry the reference implementation uses.
                    val velocity = damped.velocity
                    scaleX = damped.scaleX /
                            liquidThumbVelocityFactor(velocity, LiquidGlassTokens.VelocityStretchMaxX)
                    scaleY = damped.scaleY *
                            liquidThumbVelocityFactor(velocity, LiquidGlassTokens.VelocityStretchMaxY)
                }
            }

            val surfaceBlock: DrawScope.() -> Unit = remember(damped, thumbColor) {
                {
                    val press = damped.pressProgress
                    // Opaque at rest, clear at full press: the white fill is the
                    // thumb's resting material, and it fades out exactly as the lens
                    // fades in, so the surface hands over from painted to refracted.
                    if (press < 1f) {
                        drawRect(thumbColor.copy(alpha = 1f - press))
                    }
                }
            }

            Box(
                Modifier
                    .graphicsLayer {
                        translationX = liquidThumbTranslationXPx(
                            progress = damped.progress,
                            totalWidthPx = geometry.totalWidthPx,
                            thumbWidthPx = geometry.thumbWidthPx,
                            isLtr = geometry.isLtr,
                        )
                    }
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(
                            outer.effective,
                            rememberBackdrop(trackBackdrop, trackDraw),
                        ),
                        shape = shapeBlock,
                        effects = effectsBlock,
                        highlight = highlightBlock,
                        shadow = shadowBlock,
                        innerShadow = innerShadowBlock,
                        layerBlock = layerBlock,
                        onDrawSurface = surfaceBlock,
                        // Full resolution: the thumb is 40x24dp, so the capture is
                        // bounded by the thumb rather than the screen, and unlike
                        // the big frosted surfaces it is *clear* while pressed —
                        // downscaling it would show as pixelation with no blur left
                        // to hide it.
                        backdropScale = 1f,
                        loopBucket = LocalBackdropLoopBucket.current,
                    )
                    .size(thumbSize.width, thumbSize.height)
            )
        } else {
            // Same geometry, same springs, painted. Reads the animation inside the
            // draw block so the fallback animates identically to the glass one.
            Canvas(Modifier.matchParentSize()) {
                val total = size.width
                val railSpan = liquidRailSpanPx(total, thumbWidthPx)
                val railInset = liquidRailInsetPx(thumbWidthPx)
                val trackPx = trackHeight.toPx()
                val radius = trackPx / 2f
                val centerY = size.height / 2f
                val progress = damped.progress
                val press = damped.pressProgress
                val railStart = if (isLtr) railInset else total - railInset - railSpan
                val corner = CornerRadius(radius, radius)

                drawRoundRect(
                    color = inactive,
                    topLeft = Offset(railStart, centerY - radius),
                    size = Size(railSpan, trackPx),
                    cornerRadius = corner,
                )
                val fillWidth = railSpan * progress
                if (fillWidth > 0f) {
                    val fillStart =
                        if (isLtr) railStart else railStart + railSpan - fillWidth
                    drawRoundRect(
                        color = active,
                        topLeft = Offset(fillStart, centerY - radius),
                        size = Size(fillWidth, trackPx),
                        cornerRadius = corner,
                    )
                }

                val thumbCenter = liquidThumbCenterXPx(progress, total, thumbWidthPx, isLtr)
                // Same swell and stretch as the glass thumb above, painted.
                val velocity = damped.velocity
                val thumbW = thumbWidthPx * damped.scaleX /
                        liquidThumbVelocityFactor(velocity, LiquidGlassTokens.VelocityStretchMaxX)
                val thumbH = thumbHeightPx * damped.scaleY *
                        liquidThumbVelocityFactor(velocity, LiquidGlassTokens.VelocityStretchMaxY)
                val thumbCorner = CornerRadius(min(thumbW, thumbH) / 2f)
                val thumbTopLeft = Offset(thumbCenter - thumbW / 2f, centerY - thumbH / 2f)
                drawRoundRect(
                    color = thumbColor,
                    topLeft = thumbTopLeft,
                    size = Size(thumbW, thumbH),
                    cornerRadius = thumbCorner,
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = lerp(0.18f, 0.45f, press)),
                    topLeft = thumbTopLeft,
                    size = Size(thumbW, thumbH),
                    cornerRadius = thumbCorner,
                    style = Stroke(width = 0.8f.dp.toPx()),
                )
            }
        }
    }
}

/**
 * Layout-phase geometry for [LiquidGlassSlider], shared with its gesture handler.
 *
 * Mutable and deliberately outside the snapshot system: the values change only on
 * a resize or a range change, and both the draw lambdas and the pointer input need
 * to read them without subscribing composition to a resize.
 */
private class LiquidSliderGeometry {
    var totalWidthPx: Float = 0f
    var thumbWidthPx: Float = 0f
    var rangeStart: Float = 0f
    var rangeSpan: Float = 1f
    var isLtr: Boolean = true
}
