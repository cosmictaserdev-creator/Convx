/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zone.ien.hig

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import kotlinx.coroutines.flow.collectLatest
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.White
import zone.ien.hig.utils.DampedDragAnimation
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/** Single-value slider with optional discrete steps and liquid-glass backdrop. */

@Composable
fun CupertinoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    showStepIndicator: Boolean = steps > 0,
    backdrop: Backdrop,
    onValueChangeFinished: (() -> Unit)? = null,
    visibilityThreshold: Float = 0.01f,
    colors: CupertinoSliderColors = CupertinoLiquidSliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    require(steps >= 0) { "steps should be >= 0" }

    SliderImpl(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        showStepIndicator = showStepIndicator,
        value = value,
        valueRange = valueRange,
        visibilityThreshold = visibilityThreshold,
        thumb = { dampedDragAnimation, trackBackdrop ->
            CupertinoLiquidSliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = colors,
                enabled = enabled,
                dampedDragAnimation = dampedDragAnimation,
                backdrop = backdrop,
                trackBackdrop = trackBackdrop,
            )
        },
        track = { dampedDragAnimation, trackBackdrop ->
            CupertinoLiquidSliderDefaults.Track(
                colors = colors,
                enabled = enabled,
                dampedDragAnimation = dampedDragAnimation,
                trackBackdrop = trackBackdrop
            )
        },
    )
}

/** Single-value slider with a customizable thumb and track. */

@Composable
fun CupertinoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    showStepIndicator: Boolean = steps > 0,
    backdrop: Backdrop,
    onValueChangeFinished: (() -> Unit)? = null,
    visibilityThreshold: Float = 0.01f,
    colors: CupertinoSliderColors = CupertinoLiquidSliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit = { dampedDragAnimation, trackBackdrop ->
        CupertinoLiquidSliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
            dampedDragAnimation = dampedDragAnimation,
            backdrop = backdrop,
            trackBackdrop = trackBackdrop,
        )
    },
    track: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit = { dampedDragAnimation, trackBackdrop ->
        CupertinoLiquidSliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            dampedDragAnimation = dampedDragAnimation,
            trackBackdrop = trackBackdrop
        )
    }
) {
    require(steps >= 0) { "steps should be >= 0" }

    SliderImpl(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        showStepIndicator = showStepIndicator,
        value = value,
        valueRange = valueRange,
        visibilityThreshold = visibilityThreshold,
        thumb = thumb,
        track = track,
    )
}

/** Range slider with optional discrete steps and liquid-glass backdrop. */

@Composable
fun CupertinoRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    showStepIndicator: Boolean = steps > 0,
    backdrop: Backdrop,
    onValueChangeFinished: (() -> Unit)? = null,
    visibilityThreshold: Float = 0.01f,
    colors: CupertinoSliderColors = CupertinoLiquidSliderDefaults.colors(),
) {
    val startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    require(steps >= 0) { "steps should be >= 0" }

    RangeSliderImpl(
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        showStepIndicator = showStepIndicator,
        value = value,
        valueRange = valueRange,
        visibilityThreshold = visibilityThreshold,
        startThumb = { dampedDragAnimation, trackBackdrop ->
            CupertinoLiquidSliderDefaults.Thumb(
                interactionSource = startInteractionSource,
                colors = colors,
                enabled = enabled,
                dampedDragAnimation = dampedDragAnimation,
                backdrop = backdrop,
                trackBackdrop = trackBackdrop,
            )
        },
        endThumb = { dampedDragAnimation, trackBackdrop ->
            CupertinoLiquidSliderDefaults.Thumb(
                interactionSource = endInteractionSource,
                colors = colors,
                enabled = enabled,
                dampedDragAnimation = dampedDragAnimation,
                backdrop = backdrop,
                trackBackdrop = trackBackdrop,
            )
        },
        track = { startDampedDragAnimation, endDampedDragAnimation, trackBackdrop ->
            CupertinoLiquidSliderDefaults.Track(
                colors = colors,
                enabled = enabled,
                startDampedDragAnimation = startDampedDragAnimation,
                endDampedDragAnimation = endDampedDragAnimation,
                trackBackdrop = trackBackdrop
            )
        },
    )
}

@OptIn(InternalCupertinoApi::class)
@Composable
private fun SliderImpl(
    modifier: Modifier,
    enabled: Boolean,
    colors: CupertinoSliderColors,
    interactionSource: MutableInteractionSource,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    steps: Int,
    showStepIndicator: Boolean = steps > 0,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    visibilityThreshold: Float,
    thumb: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit,
    track: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit,
) {
    val updatedValue by rememberUpdatedState(value)
    val updatedEnabled by rememberUpdatedState(enabled)
    val trackBackdrop = rememberLayerBackdrop()
    val inactiveTickColor by colors.tickColor(enabled, active = false)
    val activeTickColor by colors.tickColor(enabled, active = true)

    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    var thumbWidth by remember { mutableStateOf(0f) }
    var trackWidth by remember { mutableStateOf(0) }
    var trackHeight by remember { mutableStateOf(0) }

    val getSnappedValue: (Float) -> Float = { currentVal ->
        if (steps <= 0) currentVal
        else {
            val gap = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            val stepIndex = ((currentVal - valueRange.start) / gap).roundToInt()
            (valueRange.start + stepIndex * gap).coerceIn(valueRange)
        }
    }

    BoxWithConstraints(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.fillMaxWidth()
    ) {
        trackWidth = constraints.maxWidth

        val isLtr by rememberUpdatedState(LocalLayoutDirection.current == LayoutDirection.Ltr)
        val animationScope = rememberCoroutineScope()
        var didDrag by remember { mutableStateOf(false) }

        var currentVirtualValue by remember { mutableStateOf(0f) }
        var prevValue by remember(value) { mutableStateOf(value) }

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = updatedValue,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {
                    currentVirtualValue = targetValue
                },
                onDragStopped = {
                    if (updatedEnabled) {
                        if (didDrag) {
                            val snapped = getSnappedValue(targetValue)
                            animateToValue(snapped)
                            onValueChange(snapped)
                            onValueChangeFinished?.invoke()
                        }
                    }
                    didDrag = false
                },
                onDrag = { _, dragAmount ->
                    if (updatedEnabled) {
                        if (!didDrag) {
                            didDrag = dragAmount.x != 0f
                        }

                        val rangeLength = valueRange.endInclusive - valueRange.start
                        val delta = rangeLength * (dragAmount.x / trackWidth) * if (isLtr) 1f else -1f

                        currentVirtualValue = (currentVirtualValue + delta).coerceIn(valueRange)

                        val snappedValue = getSnappedValue(currentVirtualValue)
                        val stepGap = rangeLength / (steps + 1)
                        val magneticThreshold = stepGap * 0.35f

                        val finalTargetValue = if (abs(currentVirtualValue - snappedValue) < magneticThreshold) {
                            if (steps > 0) {
                                if (prevValue != snappedValue) {
                                    prevValue = snappedValue
                                    haptic.performHapticFeedback(CupertinoHapticFeedback.SelectionChanged)
                                }
                            }
                            snappedValue
                        } else {
                            currentVirtualValue
                        }

                        updateValue(finalTargetValue)
                        onValueChange(finalTargetValue)
                    }
                }
            )
        }

        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { updatedValue }
                .collectLatest { value ->
                    if (dampedDragAnimation.targetValue != value) {
                        dampedDragAnimation.updateValue(value)
                    }
                }
        }

        Box(
            modifier = Modifier
                .onGloballyPositioned { trackHeight = it.size.height }
                .fillMaxWidth()

        ) {
            Box(
                modifier = Modifier
                    .pointerInput(animationScope) {
                        detectTapGestures { position ->
                            if (updatedEnabled) {
                                val rawDelta = position.x / trackWidth
                                val rawValue = if (isLtr) {
                                    valueRange.start + (valueRange.endInclusive - valueRange.start) * rawDelta
                                } else {
                                    valueRange.endInclusive - (valueRange.endInclusive - valueRange.start) * rawDelta
                                }

                                val snapped = getSnappedValue(rawValue.coerceIn(valueRange))
                                dampedDragAnimation.animateToValue(snapped)
                                onValueChange(snapped)
                                onValueChangeFinished?.invoke()
                            }
                        }
                    }
                    .height(with (density) { trackHeight.toDp() })
                    .fillMaxWidth()
            )

            if (showStepIndicator) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    repeat(steps + 2) { index ->
                        val dotHeight = TickSize

                        val progress = index.toFloat() / (steps + 1)
                        val actualDotX = -thumbWidth / 2f + size.width * progress

                        val finalDotX = when (actualDotX) {
                            in -thumbWidth / 2 .. thumbWidth / 4 -> {
                                (actualDotX + thumbWidth / 2) / 3
                            }
                            in size.width - thumbWidth * 5 / 4 .. size.width - thumbWidth / 2 -> {
                                size.width - thumbWidth * 5 / 4 + (actualDotX - (size.width - thumbWidth * 5 / 4)) / 3
                            }
                            else -> {
                                actualDotX
                            }
                        }

                        drawCircle(
                            color = inactiveTickColor,
                            radius = dotHeight.toPx(),
                            center = Offset(
                                x = finalDotX + thumbWidth / 2f,
                                y = trackHeight / 2f + (dotHeight + 2.dp).toPx()
                            )
                        )
                    }
                }
            }

            track(dampedDragAnimation, trackBackdrop)
        }

        Box(
            modifier = Modifier.graphicsLayer {
                thumbWidth = size.width

                val actualTranslationX = -thumbWidth / 2f + trackWidth * dampedDragAnimation.progress

                translationX = when (actualTranslationX) {
                    in -thumbWidth / 2..thumbWidth / 4 -> {
                        (actualTranslationX + thumbWidth / 2) / 3
                    }
                    in trackWidth - thumbWidth * 5 / 4 .. trackWidth - thumbWidth / 2 -> {
                        trackWidth - thumbWidth * 5 / 4 + (actualTranslationX - (trackWidth - thumbWidth * 5 / 4)) / 3
                    }
                    else -> {
                        actualTranslationX
                    }
                }
            }
        ) {
            thumb(dampedDragAnimation, trackBackdrop)
        }
    }
}

@OptIn(InternalCupertinoApi::class)
@Composable
private fun RangeSliderImpl(
    modifier: Modifier,
    enabled: Boolean,
    colors: CupertinoSliderColors,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    steps: Int = 0,
    showStepIndicator: Boolean = steps > 0,
    value: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    visibilityThreshold: Float,
    startThumb: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit,
    endThumb: @Composable (DampedDragAnimation, LayerBackdrop) -> Unit,
    track: @Composable (DampedDragAnimation, DampedDragAnimation, LayerBackdrop) -> Unit,
) {
    val updatedStartValue by rememberUpdatedState(value.start.absoluteValue)
    val updatedEndValue by rememberUpdatedState(value.endInclusive.absoluteValue)

    val updatedEnabled by rememberUpdatedState(enabled)
    val trackBackdrop = rememberLayerBackdrop()
    val inactiveTickColor by colors.tickColor(enabled, active = false)
    val activeTickColor by colors.tickColor(enabled, active = true)

    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current

    var startThumbWidth by remember { mutableStateOf(0f) }
    var endThumbWidth by remember { mutableStateOf(0f) }
    var trackWidth by remember { mutableStateOf(0) }
    var trackHeight by remember { mutableStateOf(0) }

    val getSnappedValue: (Float) -> Float = { currentVal ->
        if (steps <= 0) currentVal
        else {
            val gap = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            val stepIndex = ((currentVal - valueRange.start) / gap).roundToInt()
            (valueRange.start + stepIndex * gap).coerceIn(valueRange)
        }
    }

    BoxWithConstraints(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.fillMaxWidth()
    ) {
        trackWidth = constraints.maxWidth

        val isLtr by rememberUpdatedState(LocalLayoutDirection.current == LayoutDirection.Ltr)
        val animationScope = rememberCoroutineScope()
        var startDidDrag by remember { mutableStateOf(false) }
        var endDidDrag by remember { mutableStateOf(false) }

        var currentVirtualStartValue by remember { mutableStateOf(0f) }
        var currentVirtualEndValue by remember { mutableStateOf(0f) }
        var prevStartValue by remember(value.start.absoluteValue) { mutableStateOf(value.start.absoluteValue) }
        var prevEndValue by remember(value.endInclusive.absoluteValue) { mutableStateOf(value.endInclusive.absoluteValue) }

        val startDampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = updatedStartValue,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {
                    currentVirtualStartValue = targetValue
                },
                onDragStopped = {
                    if (updatedEnabled) {
                        if (startDidDrag) {
                            val snapped = getSnappedValue(targetValue)
                            animateToValue(snapped)
                            onValueChange(snapped..updatedEndValue)
                            onValueChangeFinished?.invoke()
                        }
                    }
                    startDidDrag = false
                },
                onDrag = { _, dragAmount ->
                    if (updatedEnabled) {
                        if (!startDidDrag) {
                            startDidDrag = dragAmount.x != 0f
                        }

                        val rangeLength = valueRange.endInclusive - valueRange.start
                        val delta = rangeLength * (dragAmount.x / trackWidth) * if (isLtr) 1f else -1f

                        currentVirtualStartValue = (currentVirtualStartValue + delta).coerceIn(valueRange)

                        val snappedValue = getSnappedValue(currentVirtualStartValue)
                        val stepGap = rangeLength / (steps + 1)
                        val magneticThreshold = stepGap * 0.35f

                        val finalTargetValue = if (abs(currentVirtualStartValue - snappedValue) < magneticThreshold) {
                            if (steps > 0) {
                                if (prevStartValue != snappedValue) {
                                    prevStartValue = snappedValue
                                    haptic.performHapticFeedback(CupertinoHapticFeedback.SelectionChanged)
                                }
                            }
                            snappedValue
                        } else {
                            currentVirtualStartValue
                        }

                        if (finalTargetValue <= updatedEndValue) {
                            updateValue(finalTargetValue)
                            onValueChange(finalTargetValue..updatedEndValue)
                        }
                    }
                }
            )
        }
        val endDampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = updatedEndValue,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.5f,
                onDragStarted = {
                    currentVirtualEndValue = targetValue
                },
                onDragStopped = {
                    if (updatedEnabled) {
                        if (endDidDrag) {
                            val snapped = getSnappedValue(targetValue)
                            animateToValue(snapped)
                            onValueChange(updatedStartValue..snapped)
                            onValueChangeFinished?.invoke()
                        }
                    }
                    endDidDrag = false
                },
                onDrag = { _, dragAmount ->
                    if (updatedEnabled) {
                        if (!endDidDrag) {
                            endDidDrag = dragAmount.x != 0f
                        }

                        val rangeLength = valueRange.endInclusive - valueRange.start
                        val delta = rangeLength * (dragAmount.x / trackWidth) * if (isLtr) 1f else -1f

                        currentVirtualEndValue = (currentVirtualEndValue + delta).coerceIn(valueRange)

                        val snappedValue = getSnappedValue(currentVirtualEndValue)
                        val stepGap = rangeLength / (steps + 1)
                        val magneticThreshold = stepGap * 0.35f

                        val finalTargetValue = if (abs(currentVirtualEndValue - snappedValue) < magneticThreshold) {
                            if (steps > 0) {
                                if (prevEndValue != snappedValue) {
                                    prevEndValue = snappedValue
                                    haptic.performHapticFeedback(CupertinoHapticFeedback.SelectionChanged)
                                }
                            }
                            snappedValue
                        } else {
                            currentVirtualEndValue
                        }

                        if (finalTargetValue >= updatedStartValue) {
                            updateValue(finalTargetValue)
                            onValueChange(updatedStartValue..finalTargetValue)
                        }
                    }
                }
            )
        }

        LaunchedEffect(startDampedDragAnimation) {
            snapshotFlow { updatedStartValue }
                .collectLatest { value ->
                    if (startDampedDragAnimation.targetValue != value) {
                        startDampedDragAnimation.updateValue(value)
                    }
                }
        }

        LaunchedEffect(endDampedDragAnimation) {
            snapshotFlow { updatedEndValue }
                .collectLatest { value ->
                    if (endDampedDragAnimation.targetValue != value) {
                        endDampedDragAnimation.updateValue(value)
                    }
                }
        }

        Box(
            modifier = Modifier
                .onGloballyPositioned { trackHeight = it.size.height }
                .fillMaxWidth()

        ) {
            Box(
                modifier = Modifier
                    .pointerInput(animationScope) {
                        detectTapGestures { position ->
                            if (updatedEnabled) {
                                val rawDelta = position.x / trackWidth
                                val rawValue = if (isLtr) {
                                    valueRange.start + (valueRange.endInclusive - valueRange.start) * rawDelta
                                } else {
                                    valueRange.endInclusive - (valueRange.endInclusive - valueRange.start) * rawDelta
                                }

                                val snapped = getSnappedValue(rawValue.coerceIn(valueRange))
                                val startGap = abs(updatedStartValue - snapped)
                                val endGap = abs(updatedEndValue - snapped)

                                if (startGap < endGap) {
                                    startDampedDragAnimation.animateToValue(snapped)
                                    onValueChange(snapped..updatedEndValue)
                                } else {
                                    endDampedDragAnimation.animateToValue(snapped)
                                    onValueChange(updatedStartValue..snapped)
                                }
                                onValueChangeFinished?.invoke()
                            }
                        }
                    }
                    .height(with (density) { trackHeight.toDp() })
                    .fillMaxWidth()
            )

            if (showStepIndicator) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                    repeat(steps + 2) { index ->
                        val dotHeight = TickSize

                        val progress = index.toFloat() / (steps + 1)
                        val actualDotX = -startThumbWidth / 2f + size.width * progress

                        val finalDotX = when (actualDotX) {
                            in -startThumbWidth / 2 .. startThumbWidth / 4 -> {
                                (actualDotX + startThumbWidth / 2) / 3
                            }
                            in size.width - startThumbWidth * 5 / 4 .. size.width - startThumbWidth / 2 -> {
                                size.width - startThumbWidth * 5 / 4 + (actualDotX - (size.width - startThumbWidth * 5 / 4)) / 3
                            }
                            else -> {
                                actualDotX
                            }
                        }

                        drawCircle(
                            color = inactiveTickColor,
                            radius = dotHeight.toPx(),
                            center = Offset(
                                x = finalDotX + startThumbWidth / 2f,
                                y = trackHeight / 2f + (dotHeight + 2.dp).toPx()
                            )
                        )
                    }
                }
            }

            track(startDampedDragAnimation, endDampedDragAnimation, trackBackdrop)
        }

        Box(
            modifier = Modifier.graphicsLayer {
                startThumbWidth = size.width

                val actualTranslationX = -startThumbWidth / 2f + trackWidth * startDampedDragAnimation.progress

                translationX = when (actualTranslationX) {
                    in -startThumbWidth / 2..startThumbWidth / 4 -> {
                        (actualTranslationX + startThumbWidth / 2) / 3
                    }
                    in trackWidth - endThumbWidth * 5 / 4 .. trackWidth - endThumbWidth / 2 -> {
                        trackWidth - endThumbWidth * 5 / 4 + (actualTranslationX - (trackWidth - endThumbWidth * 5 / 4)) / 3
                    }
                    else -> {
                        actualTranslationX
                    }
                }
            }
        ) {
            startThumb(startDampedDragAnimation, trackBackdrop)
        }

        Box(
            modifier = Modifier.graphicsLayer {
                endThumbWidth = size.width

                val actualTranslationX = -startThumbWidth / 2f + trackWidth * endDampedDragAnimation.progress

                translationX = when (actualTranslationX) {
                    in -startThumbWidth / 2..startThumbWidth / 4 -> {
                        (actualTranslationX + startThumbWidth / 2) / 3
                    }
                    in trackWidth - endThumbWidth * 5 / 4 .. trackWidth - endThumbWidth / 2 -> {
                        trackWidth - endThumbWidth * 5 / 4 + (actualTranslationX - (trackWidth - endThumbWidth * 5 / 4)) / 3
                    }
                    else -> {
                        actualTranslationX
                    }
                }
            }
        ) {
            endThumb(endDampedDragAnimation, trackBackdrop)
        }
    }
}

/**
 * Object to hold defaults used by [CupertinoSlider]
 */
object CupertinoLiquidSliderDefaults {
    /**
     * Creates a [CupertinoSliderColors] that represents the different colors used in parts of the
     * [CupertinoSlider] in different states.
     *
     * For the name references below the words "active" and "inactive" are used. Active part of
     * the slider is filled with progress, so if slider's progress is 30% out of 100%, left (or
     * right in RTL) 30% of the track will be active, while the rest is inactive.
     *
     * @param thumbColor thumb color when enabled
     * @param activeTrackColor color of the track in the part that is "active", meaning that the
     * thumb is ahead of it
     * @param activeTickColor colors to be used to draw tick marks on the active track, if `steps`
     * is specified
     * @param inactiveTrackColor color of the track in the part that is "inactive", meaning that the
     * thumb is before it
     * @param inactiveTickColor colors to be used to draw tick marks on the inactive track, if
     * `steps` are specified on the Slider is specified
     * @param disabledThumbColor thumb colors when disabled
     * @param disabledActiveTrackColor color of the track in the "active" part when the Slider is
     * disabled
     * @param disabledActiveTickColor colors to be used to draw tick marks on the active track
     * when Slider is disabled and when `steps` are specified on it
     * @param disabledInactiveTrackColor color of the track in the "inactive" part when the
     * Slider is disabled
     * @param disabledInactiveTickColor colors to be used to draw tick marks on the inactive part
     * of the track when Slider is disabled and when `steps` are specified on it
     */
    @Composable
    @ReadOnlyComposable
    fun colors(
        thumbColor: Color = CupertinoColors.White,
        activeTrackColor: Color = CupertinoTheme.colorScheme.accent,
        activeTickColor: Color = CupertinoTheme.colorScheme.separator,
        inactiveTrackColor: Color = CupertinoTheme.colorScheme.separator,
        inactiveTickColor: Color = activeTickColor,
        disabledThumbColor: Color = thumbColor,
        disabledActiveTrackColor: Color = activeTrackColor.copy(alpha = .5f),
        disabledActiveTickColor: Color = activeTickColor,
        disabledInactiveTrackColor: Color = inactiveTrackColor.copy(alpha = .5f),
        disabledInactiveTickColor: Color = activeTickColor,
    ): CupertinoSliderColors =
        CupertinoSliderColors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            activeTickColor = activeTickColor,
            inactiveTrackColor = inactiveTrackColor,
            inactiveTickColor = inactiveTickColor,
            disabledThumbColor = disabledThumbColor,
            disabledActiveTrackColor = disabledActiveTrackColor,
            disabledActiveTickColor = disabledActiveTickColor,
            disabledInactiveTrackColor = disabledInactiveTrackColor,
            disabledInactiveTickColor = disabledInactiveTickColor,
        )

    /**
     * The Default thumb for [CupertinoSlider] and [CupertinoRangeSlider]
     *
     * @param interactionSource the [MutableInteractionSource] representing the stream of
     * [Interaction]s for this thumb. You can create and pass in your own `remember`ed
     * instance to observe
     * @param modifier the [Modifier] to be applied to the thumb.
     * @param colors [CupertinoSliderColors] that will be used to resolve the colors used for this thumb in
     * different states. See [CupertinoLiquidSliderDefaults.colors].
     * @param enabled controls the enabled state of this slider. When `false`, this component will
     * not respond to user input, and it will appear visually disabled and disabled to
     * accessibility services.
     * @param dampedDragAnimation The controller handling the slider's physical behavior.
     * It provides real-time animation data such as pressProgress (used for lens and blur transitions),
     * velocity (used for dynamic scale distortion), and scale (used for interactive feedback during dragging).
     * @param backdrop The primary [Backdrop] used for the thumb itself.
     * It defines the base "Liquid Glass" effect applied to the thumb's surface,
     * acting as a container for lens and chromatic aberration effects.
     * @param trackBackdrop The [LayerBackdrop] inherited from the slider's track.
     * This is combined with the thumb's backdrop to achieve a seamless "liquid fusion" effect,
     * where the track's visual information is dynamically scaled and blurred into the thumb
     * as it moves or is pressed.
     */
    @Composable
    fun Thumb(
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        colors: CupertinoSliderColors = colors(),
        enabled: Boolean = true,
        thumbSize: DpSize = LiquidThumbSize,
        dampedDragAnimation: DampedDragAnimation,
        backdrop: Backdrop = rememberLayerBackdrop(),
        trackBackdrop: LayerBackdrop
    ) {
        val thumbColor by colors.thumbColor(enabled)

        Box(
            modifier
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        backdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = if (enabled) dampedDragAnimation.pressProgress else 0f
                            val scaleX = lerp(2f / 3f, 1f, progress)
                            val scaleY = lerp(0f, 1f, progress)
                            scale(scaleX, scaleY) {
                                drawBackdrop()
                            }
                        }
                    ),
                    shape = { ThumbShape },
                    effects = {
                        val progress = if (enabled) dampedDragAnimation.pressProgress else 0f
                        blur(8.dp.toPx() * (1f - progress))
                        lens(
                            10.dp.toPx() * progress,
                            14.dp.toPx() * progress,
                            chromaticAberration = true
                        )
                    },
                    highlight = {
                        val progress = if (enabled) dampedDragAnimation.pressProgress else 0f
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = progress
                        )
                    },
                    shadow = {
                        Shadow(
                            radius = ThumbElevation,
                            color = Color.Black.copy(alpha = if (enabled) 0.15f else 0.05f)
                        )
                    },
                    innerShadow = {
                        val progress = if (enabled) dampedDragAnimation.pressProgress else 0f
                        InnerShadow(
                            radius = ThumbInnerElevation * progress,
                            alpha = progress
                        )
                    },
                    layerBlock = {
                        if (enabled) {
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            val velocity = dampedDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        }
                    },
                    onDrawSurface = {
                        val progress = if (enabled) dampedDragAnimation.pressProgress else 0f
                        drawRect(thumbColor.copy(alpha = 1f - progress))
                    }
                )
                .size(thumbSize)
        )
    }

    /**
     * The Default track for [CupertinoSlider]
     *
     * @param modifier the [Modifier] to be applied to the track.
     * @param colors [CupertinoSliderColors] that will be used to resolve the colors used for this track in
     * different states. See [CupertinoLiquidSliderDefaults.colors].
     * @param enabled controls the enabled state of this slider. When `false`, this component will
     * not respond to user input, and it will appear visually disabled and disabled to
     * accessibility services.
     * @param dampedDragAnimation The controller providing the real-time physical state of the slider.
     * It is primarily used to calculate the progress value, which determines the width of the
     * active track relative to the total available width.
     * @param trackBackdrop The [LayerBackdrop] used to capture and render the background behind the track.
     * This backdrop ensures that the "Liquid Glass" effects applied to the thumb remain visually synchronized
     * with the track's background information.
     */
    @Composable
    fun Track(
        modifier: Modifier = Modifier,
        colors: CupertinoSliderColors = colors(),
        enabled: Boolean = true,
        dampedDragAnimation: DampedDragAnimation,
        trackBackdrop: LayerBackdrop
    ) {
        val inactiveTrackColor by colors.trackColor(true, active = false)
        val activeTrackColor by colors.trackColor(true, active = true)

        Box(
            modifier = modifier
                .layerBackdrop(trackBackdrop)
                .alpha(if (enabled) 1f else 0.5f)
        ) {
            Box(
                Modifier
                    .clip(ThumbShape)
                    .background(inactiveTrackColor)
                    .height(TrackHeight)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .clip(ThumbShape)
                    .background(activeTrackColor)
                    .height(TrackHeight)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val width = (constraints.maxWidth * dampedDragAnimation.progress).fastRoundToInt()
                        layout(width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
            )
        }
    }
    /**
     * The Default track for [CupertinoRangeSlider]
     *
     * @param modifier the [Modifier] to be applied to the track.
     * @param colors [CupertinoSliderColors] that will be used to resolve the colors used for this track in
     * different states. See [CupertinoLiquidSliderDefaults.colors].
     * @param enabled controls the enabled state of this slider. When `false`, this component will
     * not respond to user input, and it will appear visually disabled and disabled to
     * accessibility services.
     * @param startDampedDragAnimation The controller providing the real-time physical state for the
     * lower bound (start thumb) of the slider. It determines the starting position of the active track gauge.
     * @param endDampedDragAnimation The controller providing the real-time physical state for the
     * upper bound (end thumb) of the slider. It determines the ending position of the active track gauge.
     * @param trackBackdrop The [LayerBackdrop] used to capture and render the background behind the track.
     * This backdrop ensures that the "Liquid Glass" effects applied to the thumb remain visually synchronized
     * with the track's background information.
     */
    @Composable
    fun Track(
        modifier: Modifier = Modifier,
        colors: CupertinoSliderColors = colors(),
        enabled: Boolean = true,
        startDampedDragAnimation: DampedDragAnimation,
        endDampedDragAnimation: DampedDragAnimation,
        trackBackdrop: LayerBackdrop
    ) {
        val density = LocalDensity.current
        val inactiveTrackColor by colors.trackColor(true, active = false)
        val activeTrackColor by colors.trackColor(true, active = true)

        Box(
            modifier = modifier
                .layerBackdrop(trackBackdrop)
                .alpha(if (enabled) 1f else 0.5f)
        ) {
            var totalWidth by remember { mutableStateOf(0) }
            Box(
                Modifier
                    .clip(ThumbShape)
                    .onGloballyPositioned { totalWidth = it.size.width }
                    .background(inactiveTrackColor)
                    .height(TrackHeight)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .offset(
                        x = with (density) { (totalWidth * startDampedDragAnimation.progress).toDp() }
                    )
                    .clip(ThumbShape)
                    .background(activeTrackColor)
                    .height(TrackHeight)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val width = (constraints.maxWidth * (endDampedDragAnimation.progress - startDampedDragAnimation.progress)).fastRoundToInt()
                        layout(width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
            )
        }
    }
}

@Immutable
class CupertinoSliderColors internal constructor(
    private val thumbColor: Color,
    private val activeTrackColor: Color,
    private val activeTickColor: Color,
    private val inactiveTrackColor: Color,
    private val inactiveTickColor: Color,
    private val disabledThumbColor: Color,
    private val disabledActiveTrackColor: Color,
    private val disabledActiveTickColor: Color,
    private val disabledInactiveTrackColor: Color,
    private val disabledInactiveTickColor: Color,
) {
    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> = rememberUpdatedState(if (enabled) thumbColor else disabledThumbColor)

    @Composable
    internal fun trackColor(
        enabled: Boolean,
        active: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (active) activeTrackColor else inactiveTrackColor
            } else {
                if (active) disabledActiveTrackColor else disabledInactiveTrackColor
            },
        )

    @Composable
    internal fun tickColor(
        enabled: Boolean,
        active: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (active) activeTickColor else inactiveTickColor
            } else {
                if (active) disabledActiveTickColor else disabledInactiveTickColor
            },
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoSliderColors) return false

        if (thumbColor != other.thumbColor) return false
        if (activeTrackColor != other.activeTrackColor) return false
        if (activeTickColor != other.activeTickColor) return false
        if (inactiveTrackColor != other.inactiveTrackColor) return false
        if (inactiveTickColor != other.inactiveTickColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledActiveTrackColor != other.disabledActiveTrackColor) return false
        if (disabledActiveTickColor != other.disabledActiveTickColor) return false
        if (disabledInactiveTrackColor != other.disabledInactiveTrackColor) return false
        if (disabledInactiveTickColor != other.disabledInactiveTickColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + activeTrackColor.hashCode()
        result = 31 * result + activeTickColor.hashCode()
        result = 31 * result + inactiveTrackColor.hashCode()
        result = 31 * result + inactiveTickColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledActiveTrackColor.hashCode()
        result = 31 * result + disabledActiveTickColor.hashCode()
        result = 31 * result + disabledInactiveTrackColor.hashCode()
        result = 31 * result + disabledInactiveTickColor.hashCode()
        return result
    }
}

private val LiquidThumbWidth = SliderTokens.ThumbWidth
private val LiquidThumbHeight = SliderTokens.ThumbHeight
private val LiquidThumbSize = DpSize(LiquidThumbWidth, LiquidThumbHeight)
private val ThumbShape = SliderTokens.ThumbShape
private val ThumbElevation = SliderTokens.ThumbElevation
private val ThumbInnerElevation = SliderTokens.ThumbInnerElevation
private val TickSize = SliderTokens.TickMarksContainerSize

// Internal to be referred to in tests
internal val TrackHeight = SliderTokens.InactiveTrackHeight

internal object SliderTokens {
    val ThumbElevation = 6.dp
    val ThumbInnerElevation = 4.dp
    val ThumbWidth = 40.0.dp
    val ThumbHeight = 24.0.dp
    val ThumbShape = ContinuousCapsule()
    val InactiveTrackHeight = 6.0.dp
    val TickMarksContainerSize = 1.5.dp
}
