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

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import zone.ien.hig.utils.rememberDefaultBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.Gray
import zone.ien.hig.theme.systemGreen
import zone.ien.hig.utils.DampedDragAnimation

/** Cupertino-style switch with optional thumb content. */

@OptIn(InternalCupertinoApi::class)
@Composable
fun CupertinoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbContent: @Composable (() -> Unit)? = null,
    colors: CupertinoSwitchColors = CupertinoSwitchDefaults.colors(),
    enabled: Boolean = true,
    backdrop: Backdrop = rememberDefaultBackdrop(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val updatedChecked by rememberUpdatedState(checked)
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(0) {
        snapshotFlow {
            updatedChecked
        }.drop(1).collect {
            haptic.performHapticFeedback(CupertinoHapticFeedback.ImpactLight)
        }
    }

    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val dragWidth = with(density) { 20.dp.toPx() }
    val animationScope = rememberCoroutineScope()
    var didDrag by remember { mutableStateOf(false) }
    var fraction by remember { mutableFloatStateOf(if (updatedChecked) 1f else 0f) }
    val dampedDragAnimation = remember(animationScope) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = fraction,
            valueRange = 0f..1f,
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 1.5f,
            onDragStarted = {},
            onDragStopped = {
                if (didDrag) {
                    fraction = if (targetValue >= 0.5f) 1f else 0f
                    onCheckedChange(fraction == 1f)
                    didDrag = false
                } else {
                    fraction = if (updatedChecked) 0f else 1f
                    onCheckedChange(fraction == 1f)
                }
            },
            onDrag = { _, dragAmount ->
                if (!didDrag) {
                    didDrag = dragAmount.x != 0f
                }
                val delta = dragAmount.x / dragWidth
                fraction =
                    if (isLtr) (fraction + delta).fastCoerceIn(0f, 1f)
                    else (fraction - delta).fastCoerceIn(0f, 1f)
            }
        )
    }
    LaunchedEffect(dampedDragAnimation) {
        snapshotFlow { fraction }
            .collectLatest { fraction ->
                dampedDragAnimation.updateValue(fraction)
            }
    }
    LaunchedEffect(updatedChecked) {
        snapshotFlow { updatedChecked }
            .collectLatest { isSelected ->
                val target = if (isSelected) 1f else 0f
                if (target != fraction) {
                    fraction = target
                    dampedDragAnimation.animateToValue(target)
                }
            }
    }

    val trackBackdrop = rememberLayerBackdrop()

    Box(
        modifier
        ,
        contentAlignment = Alignment.CenterStart
    ) {
        val checkedTrackColor by colors.trackColor(enabled, true)
        val uncheckedTrackColor by colors.trackColor(enabled, false)

        Box(
            Modifier
                .layerBackdrop(trackBackdrop)
                .clip(ContinuousCapsule())
                .drawBehind {
                    val fraction = dampedDragAnimation.value
                    drawRect(lerp(uncheckedTrackColor, checkedTrackColor, fraction))
                }
                .size(64.dp, 28.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer {
                    val fraction = dampedDragAnimation.value
                    val padding = 2.dp.toPx()
                    translationX =
                        if (isLtr) lerp(padding, padding + dragWidth, fraction)
                        else lerp(-padding, -(padding + dragWidth), fraction)
                }
                .semantics {
                    role = Role.Switch
                }
                .then(if (enabled) dampedDragAnimation.modifier else Modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(
                        backdrop,
                        rememberBackdrop(trackBackdrop) { drawBackdrop ->
                            val progress = dampedDragAnimation.pressProgress
                            val scaleX = lerp(2f / 3f, 0.75f, progress)
                            val scaleY = lerp(0f, 0.75f, progress)
                            scale(scaleX, scaleY) {
                                drawBackdrop()
                            }
                        }
                    ),
                    shape = { ContinuousCapsule() },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        // The idle thumb's opaque surface fully covers the backdrop.
                        if (progress != 0f) {
                            blur(8.dp.toPx() * (1f - progress))
                            lens(
                                5.dp.toPx() * progress,
                                10.dp.toPx() * progress,
                                chromaticAberration = true
                            )
                        }
                    },
                    onDrawBackdrop = { drawBackdrop ->
                        if (dampedDragAnimation.pressProgress != 0f) {
                            drawBackdrop()
                        }
                    },
                    highlight = {
                        val progress = dampedDragAnimation.pressProgress
                        Highlight.Ambient.copy(
                            width = Highlight.Ambient.width / 1.5f,
                            blurRadius = Highlight.Ambient.blurRadius / 1.5f,
                            alpha = progress
                        )
                    },
                    shadow = {
                        Shadow(
                            radius = 4.dp,
                            color = Color.Black.copy(alpha = 0.05f)
                        )
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 4.dp * progress,
                            alpha = progress
                        )
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 50f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(Color.White.copy(alpha = 1f - progress))
                    }
                )
                .size(40.dp, 24.dp)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides colors.iconColor(enabled, checked).value,
            ) {
                thumbContent?.invoke()
            }
        }
    }
}

/** Colors for enabled and disabled switch states. */

@Immutable
class CupertinoSwitchColors internal constructor(
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val checkedTrackColor: Color,
    private val checkedIconColor: Color,
    private val uncheckedTrackColor: Color,
    private val uncheckedIconColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledCheckedIconColor: Color,
    private val disabledUncheckedTrackColor: Color,
    private val disabledUncheckedIconColor: Color,
) {

    @Composable
    internal fun thumbColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                thumbColor
            } else {
                disabledThumbColor
            },
        )


    @Composable
    internal fun trackColor(
        enabled: Boolean,
        checked: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (checked) checkedTrackColor else uncheckedTrackColor
            } else {
                if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
            },
        )


    @Composable
    internal fun iconColor(
        enabled: Boolean,
        checked: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            if (enabled) {
                if (checked) checkedIconColor else uncheckedIconColor
            } else {
                if (checked) disabledCheckedIconColor else disabledUncheckedIconColor
            },
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoSwitchColors) return false

        if (thumbColor != other.thumbColor) return false
        if (checkedTrackColor != other.checkedTrackColor) return false
        if (checkedIconColor != other.checkedIconColor) return false
        if (uncheckedTrackColor != other.uncheckedTrackColor) return false
        if (uncheckedIconColor != other.uncheckedIconColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledCheckedTrackColor != other.disabledCheckedTrackColor) return false
        if (disabledCheckedIconColor != other.disabledCheckedIconColor) return false
        if (disabledUncheckedTrackColor != other.disabledUncheckedTrackColor) return false
        if (disabledUncheckedIconColor != other.disabledUncheckedIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + checkedTrackColor.hashCode()
        result = 31 * result + checkedIconColor.hashCode()
        result = 31 * result + uncheckedTrackColor.hashCode()
        result = 31 * result + uncheckedIconColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledCheckedTrackColor.hashCode()
        result = 31 * result + disabledCheckedIconColor.hashCode()
        result = 31 * result + disabledUncheckedTrackColor.hashCode()
        result = 31 * result + disabledUncheckedIconColor.hashCode()
        return result
    }
}

@Immutable
object CupertinoSwitchDefaults {
    internal val EnabledThumbElevation = 4.dp

    val Width: Dp = 51.dp

    val Height: Dp = 31.dp

    internal val Shape: CornerBasedShape = CircleShape

    @Composable
    @ReadOnlyComposable
    fun colors(
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = thumbColor,
        checkedTrackColor: Color = CupertinoColors.systemGreen,
        checkedIconColor: Color = CupertinoTheme.colorScheme.opaqueSeparator,
        uncheckedTrackColor: Color =
            CupertinoColors.Gray.copy(
                alpha = .33f,
            ),
        uncheckedIconColor: Color = checkedIconColor,
        disabledCheckedTrackColor: Color = checkedTrackColor.copy(alpha = .33f),
        disabledCheckedIconColor: Color = checkedIconColor,
        disabledUncheckedTrackColor: Color = uncheckedTrackColor,
        disabledUncheckedIconColor: Color = checkedIconColor,
    ): CupertinoSwitchColors =
        CupertinoSwitchColors(
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            checkedTrackColor = checkedTrackColor,
            checkedIconColor = checkedIconColor,
            uncheckedTrackColor = uncheckedTrackColor,
            uncheckedIconColor = uncheckedIconColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledCheckedIconColor = disabledCheckedIconColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
            disabledUncheckedIconColor = disabledUncheckedIconColor,
        )
}

private val ThumbPadding = 2.dp

private val AspectRationAnimationSpec = cupertinoTween<Float>(durationMillis = 300)
private val ColorAnimationSpec = cupertinoTween<Color>(durationMillis = 300)
private val AlignmentAnimationSpec = AspectRationAnimationSpec
