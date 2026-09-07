/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.media3.common.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.floor
import kotlin.random.Random

/**
 * Deterministic pseudo-waveform: [count] bar heights in 0..1, stable for a given
 * [seed] (e.g. a song's mediaId hash) so every play of the same song draws the
 * same shape. Not real amplitude data — a synthetic SoundCloud-style envelope.
 */
fun waveformBars(seed: Int, count: Int): FloatArray {
    val rnd = Random(seed)
    var prev = 0.5f
    return FloatArray(count) {
        // Random-walk so neighbouring bars relate (reads like audio, not white noise).
        prev = (prev + (rnd.nextFloat() - 0.5f) * 0.6f).coerceIn(0.12f, 1f)
        prev
    }
}

/**
 * A scrolling waveform that stays centered on the current playback position.
 * Shows only a fixed [visibleBars] window — bars scroll as the song plays.
 * Both edges fade out progressively via a horizontal alpha mask, so bars
 * dissolve into the background instead of popping in and out.
 * Bars are rounded pill shape.
 */
@Composable
fun ScrollingWaveformSeekBar(
    progress: () -> Float,
    onSeek: (Float) -> Unit,
    playedColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    totalBars: Int = 80,
    visibleBars: Int = 18,
    seed: Int = 0,
    edgeFade: Float = 0.22f,
) {
    val heights = remember(seed, totalBars) { waveformBars(seed, totalBars) }
    var dragOffset by remember { mutableStateOf<Float?>(null) }

    Canvas(
        modifier = modifier
            // Offscreen layer + DstIn gradient = progressive fade on both sides.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        edgeFade to Color.Black,
                        1f - edgeFade to Color.Black,
                        1f to Color.Transparent,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { o ->
                    val frac = (o.x / size.width).coerceIn(0f, 1f)
                    val shift = (frac - 0.5f) * visibleBars / totalBars
                    onSeek(((dragOffset ?: progress()) + shift).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { o ->
                        val frac = (o.x / size.width).coerceIn(0f, 1f)
                        dragOffset = ((dragOffset ?: progress()) + (frac - 0.5f) * visibleBars / totalBars).coerceIn(0f, 1f)
                    },
                    onDragEnd = { dragOffset?.let(onSeek); dragOffset = null },
                    onDragCancel = { dragOffset = null },
                ) { change, _ ->
                    val delta = -change.previousPosition.x + change.position.x
                    val shiftPerPx = visibleBars.toFloat() / totalBars / size.width
                    dragOffset = ((dragOffset ?: progress()) + delta * shiftPerPx).coerceIn(0f, 1f)
                }
            }
    ) {
        val n = heights.size
        if (n == 0 || size.width <= 0f || size.height <= 0f) return@Canvas

        val currentProgress = (dragOffset ?: progress()).coerceIn(0f, 1f)
        val midY = size.height / 2f
        val minH = size.height * 0.15f

        // One fixed-width slot per visible bar. Bars are positioned from a
        // FRACTIONAL first index, so the window glides continuously instead of
        // jumping a whole bar at a time — that snapping is what read as lag.
        val slot = size.width / visibleBars
        val barWidth = slot * 0.55f
        val centerBar = currentProgress * (n - 1)
        val firstBar = centerBar - visibleBars / 2f
        val startIndex = floor(firstBar).toInt()

        for (i in startIndex..(startIndex + visibleBars + 1)) {
            if (i < 0 || i >= n) continue

            val x = (i - firstBar) * slot + slot / 2f
            if (x < -slot || x > size.width + slot) continue

            val h = (heights[i] * size.height * 0.9f).coerceAtLeast(minH)
            val color = if (i <= centerBar) playedColor else trackColor

            drawRoundRect(
                color = color,
                topLeft = Offset(x - barWidth / 2f, midY - h / 2f),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(barWidth / 2f),
            )
        }
    }
}

/**
 * Playback position as a 0..1 fraction. While playing it refreshes once per frame
 * straight off the player's clock — polling on a timer makes the waveform advance in
 * visible steps; this keeps it gliding. Backed by a float state, so callers that read
 * it inside a draw lambda repaint without recomposing.
 *
 * While paused it drops to a slow poll. withFrameMillis keeps Compose's frame clock
 * running for as long as it is awaited, so leaving the per-frame loop on while paused
 * kept the whole app rendering at display rate with nothing on screen changing — and
 * anything sampling a backdrop (the floating tab bar) then re-recorded the screen on
 * every one of those ticks. The slow poll still tracks a scrub made while paused;
 * writing an unchanged float doesn't invalidate, so it costs nothing when idle.
 */
@Composable
fun rememberPlaybackFraction(player: Player, isPlaying: Boolean): State<Float> {
    val fraction = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(player, isPlaying) {
        fun sample() {
            val duration = player.duration
            fraction.floatValue = if (duration > 0L) {
                (player.currentPosition.toFloat() / duration).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
        if (isPlaying) {
            while (isActive) {
                withFrameMillis { sample() }
            }
        } else {
            while (isActive) {
                sample()
                delay(PausedPollIntervalMs)
            }
        }
    }
    return fraction
}

/** Slow enough to keep the app idle, fast enough that a scrub while paused tracks. */
private const val PausedPollIntervalMs = 250L
