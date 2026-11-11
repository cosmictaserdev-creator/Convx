package com.convx.music.ui.player

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt

/**
 * Portrait Spotify canvas: the 9:16 video fills the screen as the player's background, as Spotify
 * shows it. Outside the cover's [window] it is blurred live on the GPU (no frame readback); the
 * window shows it sharp, so the cover reads as a pane of clear glass onto the same video. A soft
 * blur always sits under the controls. [clear] (0..1) dissolves the outside blur so the whole video
 * plays clean; [controlsHidden] (0..1) lowers the blur under the controls to the title row.
 */
@Composable
fun SpotifyCanvasStage(
    controller: CanvasVideoController,
    window: () -> Rect?,
    windowCornerPx: Density.() -> Float,
    clear: () -> Float,
    controlsHidden: () -> Float,
    modifier: Modifier = Modifier,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        // No RenderEffect: sharp video with a dark foot keeps the controls readable.
        Box(modifier.drawWithContent {
            drawContent()
            drawRect(Brush.verticalGradient(FOOT_START to Color.Transparent, 1f to Color.Black.copy(alpha = 0.6f)))
        }) { CanvasVideoSurface(controller = controller, modifier = Modifier.fillMaxSize()) }
        return
    }
    val video = rememberGraphicsLayer()
    val blurred = rememberGraphicsLayer()
    val foot = rememberGraphicsLayer()
    val hole = remember { Path() }
    Box(
        modifier.drawWithContent {
            video.record { this@drawWithContent.drawContent() }
            drawLayer(video)
            // Everything blurred or masked happens at 1/8 size and is drawn upscaled: no
            // full-screen offscreen layer runs per frame, which is what cost the frame rate.
            val smallSize = IntSize(
                (size.width / BLUR_DOWNSCALE).roundToInt().coerceAtLeast(1),
                (size.height / BLUR_DOWNSCALE).roundToInt().coerceAtLeast(1),
            )
            val radius = BLUR_RADIUS.toPx() / BLUR_DOWNSCALE
            val blur = BlurEffect(radius, radius, TileMode.Clamp)
            blurred.renderEffect = blur
            blurred.record(smallSize) {
                scale(1f / BLUR_DOWNSCALE, pivot = Offset.Zero) { drawLayer(video) }
            }
            val outside = 1f - clear()
            if (outside > 0.001f) {
                blurred.alpha = outside
                val window = window()
                val upscaled: DrawScope.() -> Unit = { scale(BLUR_DOWNSCALE, pivot = Offset.Zero) { drawLayer(blurred) } }
                if (window == null) upscaled() else {
                    hole.reset()
                    hole.addRoundRect(RoundRect(window, CornerRadius(windowCornerPx())))
                    clipPath(hole, ClipOp.Difference) { upscaled() }
                }
            }
            // The foot is its own small layer, so it shows inside the window too (and with the
            // window grown to the whole screen); masked at small size, then upscaled.
            val footStart = lerp(FOOT_START, FOOT_START_IMMERSIVE, controlsHidden())
            foot.renderEffect = blur
            foot.compositingStrategy = CompositingStrategy.Offscreen
            foot.record(smallSize) {
                scale(1f / BLUR_DOWNSCALE, pivot = Offset.Zero) { drawLayer(video) }
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        footStart to Color.Transparent,
                        (footStart + FOOT_RAMP).coerceAtMost(1f) to Color.Black,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
            scale(BLUR_DOWNSCALE, pivot = Offset.Zero) { drawLayer(foot) }
        }
    ) {
        CanvasVideoSurface(controller = controller, modifier = Modifier.fillMaxSize())
    }
}

/**
 * The cover card's rectangle on the stage while it grows into the screen: each edge moves from the
 * card's resting bounds toward the player's by its own progress, as the card itself resizes, and
 * the resting pause-shrink scale lifts as it grows. Window coordinates in, stage coordinates out.
 */
fun grownCardRect(
    card: Rect,
    frame: Rect,
    top: Float,
    sides: Float,
    bottom: Float,
    pauseScale: Float,
): Rect {
    val grown = Rect(
        left = lerp(card.left, frame.left, sides),
        top = lerp(card.top, frame.top, top),
        right = lerp(card.right, frame.right, sides),
        bottom = lerp(card.bottom, frame.bottom, bottom),
    )
    val scale = pauseScale + (1f - pauseScale) * maxOf(top, sides, bottom)
    val center = grown.center
    return Rect(
        left = center.x + (grown.left - center.x) * scale,
        top = center.y + (grown.top - center.y) * scale,
        right = center.x + (grown.right - center.x) * scale,
        bottom = center.y + (grown.bottom - center.y) * scale,
    ).translate(-frame.left, -frame.top)
}

/** Above the controls' block: the foot blur starts here, as a fraction of the screen's height. */
private const val FOOT_START = 0.5f
/** With the controls lowered away only the title row remains, so the foot starts lower. */
private const val FOOT_START_IMMERSIVE = 0.74f
private const val FOOT_RAMP = 0.16f
private const val BLUR_DOWNSCALE = 8f
private val BLUR_RADIUS = 48.dp
