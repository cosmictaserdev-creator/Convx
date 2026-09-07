/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player.comments

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.convxy.music.comments.CommentTimeline
import com.convxy.music.comments.TimestampedComment

/**
 * Comment markers laid over the player's own seek-bar track.
 *
 * Drawn into the `track` slot of the existing `Slider` rather than by replacing it, so the seek bar's
 * geometry, drag handling, thumb behaviour and animated height all stay exactly as they were — this
 * composable adds pixels and takes no input. There is deliberately no `pointerInput` anywhere in here,
 * which is the guarantee that dragging and seeking behave as they did before the feature existed.
 *
 * Only the SLIM slider style carries these. It is the default style, and the only one whose track is a
 * plain full-width `PlayerSliderTrack` canvas, so `matchParentSize()` lines the ticks up with the drawn
 * track exactly. DEFAULT and WAVY use Material's own track (inset by a visible thumb) and WAVEFORM
 * draws a scrolling bar of its own; bolting markers onto those would mean rewriting them, which is a
 * worse trade than leaving the spatial view to the comments sheet, where there is room to do it well.
 *
 * @param markers pre-clustered by [CommentTimeline.markers], so a comment-dense track paints a bounded
 *   number of ticks instead of a solid bar.
 */
@Composable
fun CommentTrackMarkers(
    markers: List<CommentTimeline.CommentMarker>,
    activeMarker: CommentTimeline.CommentMarker?,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
) {
    if (markers.isEmpty()) return
    Canvas(modifier) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val tickWidth = 1.7.dp.toPx()
        val activeWidth = 3.dp.toPx()
        for (marker in markers) {
            val isActive = marker == activeMarker
            val width = if (isActive) activeWidth else tickWidth
            // A cluster fills the track; a lone comment sits slightly shorter inside it. Both stay
            // within the track's own bounds so nothing can collide with the controls around it.
            val height = size.height * if (marker.count > 1) 1f else 0.72f
            val fraction = if (isRtl) 1f - marker.fraction else marker.fraction
            val x = size.width * fraction
            drawRoundRect(
                color = if (isActive) activeColor else inactiveColor,
                topLeft = Offset(x - width / 2f, (size.height - height) / 2f),
                size = Size(width, height),
                cornerRadius = CornerRadius(width / 2f, width / 2f),
            )
        }
    }
}

/**
 * The comments sheet's own timeline: the whole track laid out horizontally, every comment cluster as
 * a tick, the playhead moving across it, and the live cluster picked out. Tap or drag to seek.
 *
 * This is where the spatial view lives for the slider styles that do not carry markers, and it is the
 * more legible of the two — there is room here for tick height to encode how many comments are stacked
 * at a moment, and for the whole thing to be scrubbable.
 *
 * The playhead is painted in [drawBehind] and reads [positionProvider] *inside* the draw block.
 * Snapshot reads made during the draw phase invalidate drawing only, so the playhead glides at the
 * player's own 100ms cadence without recomposing this composable, the comment list below it, or the
 * sheet around them.
 *
 * @param onSeekFraction called with a 0..1 position on tap and continuously while dragging.
 */
@Composable
fun CommentTimelineBar(
    markers: List<CommentTimeline.CommentMarker>,
    comments: List<TimestampedComment>,
    durationMs: Long,
    positionProvider: () -> Long,
    activeColor: Color,
    inactiveColor: Color,
    trackColor: Color,
    onSeekFraction: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seekable = durationMs > 0L

    // Resolved in composition rather than read from the gesture scope: AwaitPointerEventScope gives
    // no layoutDirection. Taking it here also means an RTL change re-keys the detector below instead
    // of leaving it mirroring the direction the bar was first composed with.
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // pointerInput re-keys on durationMs alone, so the gesture blocks below capture this lambda once
    // and keep using it for the life of the bar. rememberUpdatedState is what stops that capture going
    // stale: the caller's callback closes over live state (whether this device may drive playback, for
    // one) that a remembered block would otherwise read as it was on first composition.
    val currentOnSeekFraction by rememberUpdatedState(onSeekFraction)

    // One gesture detector for both tap and scrub, rather than a tap detector layered over a drag
    // one. Two detectors would have to negotiate touch slop between them, and whichever won would
    // swallow the other's events half the time — a single `awaitEachGesture` that seeks on down and
    // keeps seeking while the finger moves has nothing to negotiate with itself.
    val seekModifier = if (seekable) {
        Modifier.pointerInput(durationMs, isRtl) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                currentOnSeekFraction(fractionAt(down.position.x, size.width, isRtl))
                down.consume()
                while (true) {
                    val change = awaitPointerEvent().changes.firstOrNull() ?: break
                    if (!change.pressed) break
                    currentOnSeekFraction(fractionAt(change.position.x, size.width, isRtl))
                    change.consume()
                }
            }
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .then(seekModifier)
            .drawBehind {
                if (durationMs <= 0L) return@drawBehind
                val isRtl = layoutDirection == LayoutDirection.Rtl
                val baselineY = size.height - 6.dp.toPx()
                val maxTickHeight = size.height - 16.dp.toPx()

                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(0f, baselineY - 1.dp.toPx()),
                    size = Size(size.width, 2.dp.toPx()),
                    cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx()),
                )

                // Read inside the draw block on purpose — see the doc on this composable.
                val position = positionProvider().coerceIn(0L, durationMs)
                val liveGroup = CommentTimeline.activeGroup(comments, position)

                val tickWidth = 2.dp.toPx()
                val activeTickWidth = 3.5.dp.toPx()
                for (marker in markers) {
                    val isLive = liveGroup != null &&
                        liveGroup.representativeIndex in marker.firstIndex..marker.lastIndexInclusive
                    val fraction = if (isRtl) 1f - marker.fraction else marker.fraction
                    val x = size.width * fraction
                    // Height encodes cluster size, capped so one busy moment cannot dominate the bar.
                    val weight = (0.42f + 0.14f * marker.count).coerceIn(0.42f, 1f)
                    val height = maxTickHeight * weight
                    val width = if (isLive) activeTickWidth else tickWidth
                    drawRoundRect(
                        color = if (isLive) activeColor else inactiveColor,
                        topLeft = Offset(x - width / 2f, baselineY - height),
                        size = Size(width, height),
                        cornerRadius = CornerRadius(width / 2f, width / 2f),
                    )
                }

                // Playhead last, so it stays legible over a dense run of ticks.
                val playheadFraction = position.toFloat() / durationMs.toFloat()
                val playheadX = size.width * (if (isRtl) 1f - playheadFraction else playheadFraction)
                val headTop = 3.dp.toPx()
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(playheadX - 1.dp.toPx(), headTop),
                    size = Size(2.dp.toPx(), baselineY - headTop),
                    cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx()),
                )
                drawCircle(color = activeColor, radius = 3.5.dp.toPx(), center = Offset(playheadX, headTop))
            },
    )
}

/** Maps an x pixel to a 0..1 track fraction, mirroring in RTL the same way the drawing does. */
private fun fractionAt(x: Float, widthPx: Int, isRtl: Boolean): Float {
    if (widthPx <= 0) return 0f
    val raw = (x / widthPx.toFloat()).coerceIn(0f, 1f)
    return if (isRtl) 1f - raw else raw
}
