/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.ambient

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.convx.music.playback.PlayerConnection
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * A quiet bezel treatment for Ambient Mode. Playback is sampled rather than exposed as
 * a new player flow: this keeps the existing playback architecture intact and limits
 * the invalidations to this tiny canvas instead of the animated visualizer tree.
 */
@Composable
fun AmbientProgressRing(
    playerConnection: PlayerConnection,
    mediaId: String?,
    isPlaying: Boolean,
    seekPreviewProgress: Float? = null,
    modifier: Modifier = Modifier,
) {
    var playbackProgress by remember(mediaId) { mutableFloatStateOf(0f) }
    var hasValidDuration by remember(mediaId) { mutableStateOf(false) }
    val latestIsPlayingState = rememberUpdatedState(isPlaying)

    LaunchedEffect(playerConnection, mediaId) {
        playbackProgress = 0f
        hasValidDuration = false

        while (true) {
            val player = runCatching { playerConnection.player }.getOrNull()
            val duration = player?.duration ?: -1L
            val position = player?.currentPosition ?: 0L
            if (duration > 0L && position >= 0L) {
                playbackProgress = (position.toDouble() / duration.toDouble())
                    .toFloat()
                    .coerceIn(0f, 1f)
                hasValidDuration = true
            } else {
                // C.TIME_UNSET, live media, and malformed durations all land here.
                // Leaving the ring as a faint bezel is less distracting than showing
                // a misleading fraction.
                playbackProgress = 0f
                hasValidDuration = false
            }
            delay(if (latestIsPlayingState.value) 200L else 500L)
        }
    }

    val visibleProgress = seekPreviewProgress ?: playbackProgress
    val showProgress = hasValidDuration || seekPreviewProgress != null
    val progressColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val strokeWidth = 1.35.dp.toPx()
        val inset = max(strokeWidth / 2f, 2.dp.toPx())
        val radius = min(
            24.dp.toPx(),
            min(size.width, size.height) / 2f - inset
        ).coerceAtLeast(1f)
        val bezelPath = createBezelPath(size, inset, radius)

        // The unfilled outline makes the control discoverable without turning the
        // fullscreen artwork into a conventional player UI.
        drawPath(
            path = bezelPath,
            color = Color.White.copy(alpha = 0.10f),
            style = Stroke(width = strokeWidth)
        )

        if (showProgress && visibleProgress > 0f) {
            val measure = PathMeasure()
            measure.setPath(bezelPath, false)
            val progressPath = Path()
            measure.getSegment(
                startDistance = 0f,
                stopDistance = measure.length * visibleProgress.coerceIn(0f, 1f),
                destination = progressPath,
                startWithMoveTo = true
            )
            drawPath(
                path = progressPath,
                color = progressColor.copy(alpha = if (seekPreviewProgress != null) 0.9f else 0.68f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

private fun createBezelPath(size: Size, inset: Float, radius: Float): Path {
    val left = inset
    val top = inset
    val right = size.width - inset
    val bottom = size.height - inset
    val safeRadius = min(radius, min(right - left, bottom - top) / 2f).coerceAtLeast(1f)
    val path = Path()

    path.moveTo(left + safeRadius, top)
    path.lineTo(right - safeRadius, top)
    path.arcTo(
        rect = Rect(right - 2f * safeRadius, top, right, top + 2f * safeRadius),
        startAngleDegrees = -90f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )
    path.lineTo(right, bottom - safeRadius)
    path.arcTo(
        rect = Rect(right - 2f * safeRadius, bottom - 2f * safeRadius, right, bottom),
        startAngleDegrees = 0f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )
    path.lineTo(left + safeRadius, bottom)
    path.arcTo(
        rect = Rect(left, bottom - 2f * safeRadius, left + 2f * safeRadius, bottom),
        startAngleDegrees = 90f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )
    path.lineTo(left, top + safeRadius)
    path.arcTo(
        rect = Rect(left, top, left + 2f * safeRadius, top + 2f * safeRadius),
        startAngleDegrees = 180f,
        sweepAngleDegrees = 90f,
        forceMoveTo = false
    )
    path.close()
    return path
}

private data class PerimeterCandidate(
    val distanceSquared: Float,
    val distanceAlong: Float,
)

/**
 * Maps a touch on the rounded rectangle to the same clockwise path used to draw the
 * ring. Corners use the actual quarter-circle arc, so dragging over a corner does not
 * jump to an unrelated point in the song.
 */
fun bezelProgressFor(
    position: Offset,
    size: Size,
    inset: Float,
    cornerRadius: Float,
): Float {
    val left = inset
    val top = inset
    val right = size.width - inset
    val bottom = size.height - inset
    val radius = min(cornerRadius, min(right - left, bottom - top) / 2f).coerceAtLeast(1f)
    val topLength = max(0f, right - left - 2f * radius)
    val sideLength = max(0f, bottom - top - 2f * radius)
    val cornerLength = (PI.toFloat() / 2f) * radius
    val totalLength = 2f * topLength + 2f * sideLength + 4f * cornerLength

    var best = PerimeterCandidate(Float.POSITIVE_INFINITY, 0f)

    fun considerLine(
        start: Offset,
        end: Offset,
        distanceAlong: Float,
    ) {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val lengthSquared = dx * dx + dy * dy
        val fraction = if (lengthSquared > 0f) {
            (((position.x - start.x) * dx + (position.y - start.y) * dy) / lengthSquared)
                .coerceIn(0f, 1f)
        } else {
            0f
        }
        val nearest = Offset(start.x + dx * fraction, start.y + dy * fraction)
        val distanceSquared = distanceSquared(position, nearest)
        if (distanceSquared < best.distanceSquared) {
            best = PerimeterCandidate(
                distanceSquared = distanceSquared,
                distanceAlong = distanceAlong + fraction * kotlin.math.sqrt(lengthSquared)
            )
        }
    }

    fun considerArc(
        center: Offset,
        startAngle: Float,
        sweep: Float,
        distanceAlong: Float,
    ) {
        var angle = atan2(position.y - center.y, position.x - center.x)
        while (angle < startAngle) angle += (2f * PI).toFloat()
        while (angle > startAngle + sweep) angle -= (2f * PI).toFloat()
        angle = angle.coerceIn(startAngle, startAngle + sweep)
        val nearest = Offset(
            center.x + radius * cos(angle),
            center.y + radius * sin(angle)
        )
        val distanceSquared = distanceSquared(position, nearest)
        if (distanceSquared < best.distanceSquared) {
            best = PerimeterCandidate(
                distanceSquared = distanceSquared,
                distanceAlong = distanceAlong + (angle - startAngle) * radius
            )
        }
    }

    var cursor = 0f
    considerLine(Offset(left + radius, top), Offset(right - radius, top), cursor)
    cursor += topLength
    considerArc(Offset(right - radius, top + radius), -PI.toFloat() / 2f, PI.toFloat() / 2f, cursor)
    cursor += cornerLength
    considerLine(Offset(right, top + radius), Offset(right, bottom - radius), cursor)
    cursor += sideLength
    considerArc(Offset(right - radius, bottom - radius), 0f, PI.toFloat() / 2f, cursor)
    cursor += cornerLength
    considerLine(Offset(right - radius, bottom), Offset(left + radius, bottom), cursor)
    cursor += topLength
    considerArc(Offset(left + radius, bottom - radius), PI.toFloat() / 2f, PI.toFloat() / 2f, cursor)
    cursor += cornerLength
    considerLine(Offset(left, bottom - radius), Offset(left, top + radius), cursor)
    cursor += sideLength
    considerArc(Offset(left + radius, top + radius), PI.toFloat(), PI.toFloat() / 2f, cursor)

    return if (totalLength > 0f) {
        (best.distanceAlong / totalLength).coerceIn(0f, 1f)
    } else {
        0f
    }
}

private fun distanceSquared(first: Offset, second: Offset): Float {
    val dx = first.x - second.x
    val dy = first.y - second.y
    return dx * dx + dy * dy
}

fun isNearBezel(position: Offset, size: Size, hitSlop: Float): Boolean {
    return min(
        min(position.x, position.y),
        min(size.width - position.x, size.height - position.y)
    ) <= hitSlop
}
