/*
 * Copyright 2026, compose-miuix-ui contributors
 * SPDX-License-Identifier: Apache-2.0
 *
 * the reference app InteractiveHighlight behavior, shared by Convx glass controls. The optional
 * radiusScale retains existing component-specific sizing; the default is the reference app's 1.2x.
 */
package com.convx.music.ui.component.backdrop.catalog.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Draws a press-driven additive sheen. Drawing and gesture modifiers are separate so the
 * sheen can cover a recorded glass layer while its hit target remains on the moving indicator.
 */
class InteractiveHighlight(
    private val animationScope: CoroutineScope,
    private val position: (size: Size, offset: Offset) -> Offset = { _, offset -> offset },
    private val radiusScale: Float = 1.5f,
    // Overall lift and finger-spot strength. Menus (iOS 27) use a much brighter bloom.
    private val sheenAlpha: Float = 0.08f,
    private val spotAlpha: Float = 0.15f,
    /**
     * iOS 27 menus: letting go sends a soft glow ring out from the finger that fades as
     * it spreads (the "let go" pulse in the reference clip). Off for buttons.
     */
    private val releasePulse: Boolean = false,
) {
    private val pulse = Animatable(1f)
    private var pulseCenter = Offset.Zero
    private val pressProgressAnimationSpec = spring(0.5f, 300f, 0.001f)
    private val positionAnimationSpec = spring(0.5f, 300f, Offset.VisibilityThreshold)
    private val pressProgressAnimation = Animatable(0f, 0.001f)
    private val positionAnimation = Animatable(Offset.Zero, Offset.VectorConverter, Offset.VisibilityThreshold)
    private var startPosition = Offset.Zero

    val pressProgress: Float get() = pressProgressAnimation.value
    val offset: Offset get() = positionAnimation.value - startPosition

    val modifier: Modifier = Modifier.drawWithContent {
        val r = pulse.value
        if (releasePulse && r < 1f) {
            val base = (size.minDimension * radiusScale).coerceAtLeast(1f)
            val ring = base * (0.5f + 1.3f * r)
            val a = spotAlpha * 1.1f * (1f - r)
            drawRect(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to Color.White.copy(alpha = a * 0.35f),
                        0.6f to Color.White.copy(alpha = a),
                        1.0f to Color.White.copy(alpha = 0f),
                    ),
                    center = Offset(pulseCenter.x.coerceIn(0f, size.width), pulseCenter.y.coerceIn(0f, size.height)),
                    radius = ring,
                ),
                blendMode = BlendMode.Plus,
            )
        }
        val progress = pressProgressAnimation.value
        if (progress > 0f) {
            // Plus blending lifts sampled luminance without replacing backdrop detail.
            drawRect(
                color = Color.White.copy(alpha = sheenAlpha * progress),
                blendMode = BlendMode.Plus,
            )
            val pos = position(size, positionAnimation.value)
            val radius = (size.minDimension * radiusScale).coerceAtLeast(1f)
            val center = Offset(
                x = pos.x.coerceIn(0f, size.width),
                y = pos.y.coerceIn(0f, size.height),
            )
            val spotColor = Color.White.copy(alpha = spotAlpha * progress)
            drawRect(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.0f to spotColor,
                        0.5f to spotColor,
                        1.0f to Color.White.copy(alpha = 0f),
                    ),
                    center = center,
                    radius = radius,
                ),
                blendMode = BlendMode.Plus,
            )
        }
        drawContent()
    }

    val gestureModifier: Modifier = Modifier.pointerInput(animationScope) {
        inspectDragGestures(
            onDragStart = { down ->
                startPosition = down.position
                animationScope.launch {
                    launch { pressProgressAnimation.animateTo(1f, pressProgressAnimationSpec) }
                    launch { positionAnimation.snapTo(startPosition) }
                }
            },
            onDragEnd = { release() },
            onDragCancel = { release() },
        ) { change, _ ->
            animationScope.launch { positionAnimation.snapTo(change.position) }
        }
    }

    private fun release() {
        if (releasePulse) {
            pulseCenter = positionAnimation.value
            animationScope.launch {
                pulse.snapTo(0f)
                pulse.animateTo(1f, androidx.compose.animation.core.tween(460, easing = androidx.compose.animation.core.FastOutSlowInEasing))
            }
        }
        animationScope.launch {
            launch { pressProgressAnimation.animateTo(0f, pressProgressAnimationSpec) }
            launch { positionAnimation.animateTo(startPosition, positionAnimationSpec) }
        }
    }
}
