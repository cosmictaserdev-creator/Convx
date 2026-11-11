package com.convx.music.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin

private const val MeshCycleMillis = 32_000
private val MeshCycleRadians = (2 * PI).toFloat()
private val MeshAnchors = floatArrayOf(0.12f, 0.5f, 0.88f)
private val MeshSpeeds = floatArrayOf(1f, 1f, 2f)
private val MeshOffsets = floatArrayOf(0f, 2.1f, 4.2f)

/**
 * Three soft colour blobs drifting sideways, faded out toward the top or bottom edge. Each blob moves on its own
 * phase so the mix never repeats visibly; one slow cycle takes 32 seconds. The phase is read only while drawing,
 * so a running mesh redraws without recomposing.
 */
@Composable
fun AnimatedMesh(
    primary: Color,
    secondary: Color,
    modifier: Modifier = Modifier,
    alpha: Float = 0.2f,
    fadeAtBottom: Boolean = true,
    enabled: Boolean = true,
) {
    if (!enabled || alpha <= 0f) return
    val phase by rememberInfiniteTransition(label = "mesh").animateFloat(
        initialValue = 0f,
        targetValue = MeshCycleRadians,
        animationSpec = infiniteRepeatable(tween(MeshCycleMillis, easing = LinearEasing), RepeatMode.Restart),
        label = "meshPhase",
    )
    val opacity = alpha.coerceIn(0f, 1f)
    val colors = arrayOf(primary, secondary, lerp(primary, secondary, 0.5f))
    Box(
        modifier
            .clipToBounds()
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                val stops = if (fadeAtBottom) arrayOf(0f to Color.White, 0.22f to Color.White, 1f to Color.Transparent)
                else arrayOf(0f to Color.Transparent, 0.7f to Color.White, 1f to Color.White)
                drawRect(Brush.verticalGradient(*stops), blendMode = BlendMode.DstIn)
            }
            .drawBehind {
                val radius = max(size.width * 0.58f, size.height * 1.35f).coerceAtLeast(1f)
                val centerY = size.height * if (fadeAtBottom) 0.2f else 0.75f
                colors.forEachIndexed { index, color ->
                    val centerX = size.width * MeshAnchors[index] +
                        size.width * 0.12f * sin(phase * MeshSpeeds[index] + MeshOffsets[index])
                    drawCircle(
                        brush = Brush.radialGradient(
                            0f to color.copy(alpha = color.alpha * opacity),
                            0.35f to color.copy(alpha = color.alpha * opacity * 0.7f),
                            1f to color.copy(alpha = 0f),
                            center = Offset(centerX, centerY),
                            radius = radius,
                        ),
                        radius = radius,
                        center = Offset(centerX, centerY),
                    )
                }
            },
    )
}
