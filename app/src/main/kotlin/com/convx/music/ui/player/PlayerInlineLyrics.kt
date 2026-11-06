package com.convx.music.ui.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.snap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.constants.PlayerInlineLyricsKey
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.PlayerLyricsPreview
import com.convx.music.playback.currentPreviewLine
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberPreference
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.wordsWithSpacing

@Composable
fun PlayerInlineLyrics(
    connection: PlayerConnection,
    positionMs: () -> Long,
    tint: Color,
    onOpenLyrics: () -> Unit,
    modifier: Modifier = Modifier,
    framePositionMs: () -> Long = positionMs,
    active: Boolean = true,
) {
    val enabled by rememberPreference(PlayerInlineLyricsKey, false)
    if (!enabled || !active) return
    val preview by connection.lyricsPreview.collectAsStateWithLifecycle(PlayerLyricsPreview(null, emptyList()))
    val metadata by connection.mediaMetadata.collectAsStateWithLifecycle()
    val song by connection.currentSong.collectAsStateWithLifecycle(null)
    val playing by connection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val animate = android.animation.ValueAnimator.areAnimatorsEnabled()
    val currentPosition by rememberUpdatedState(positionMs)
    val framePosition by rememberUpdatedState(framePositionMs)
    val clock = remember { mutableLongStateOf(0L) }
    LaunchedEffect(playing, metadata?.id) {
        if (playing) while (true) {
            withFrameNanos { clock.longValue = framePosition() }
        }
    }
    val offset = song?.takeIf { it.song.id == metadata?.id }?.song?.lyricsOffset?.toLong() ?: 0L
    // Backing vocals are their own entries; the main line is the last lead line to start, and a
    // backing line rides under it while sung instead of taking its place.
    val leads = remember(preview) { preview.lines.filterNot { it.isBackground } }
    val backings = remember(preview) { preview.lines.filter { it.isBackground } }
    val index by remember(leads, offset, playing) {
        derivedStateOf { currentPreviewLine(leads, (if (playing) clock.longValue else currentPosition()) + offset) }
    }
    val backing by remember(backings, offset, playing) {
        derivedStateOf {
            val now = (if (playing) clock.longValue else currentPosition()) + offset
            backings.lastOrNull { it.time <= now && now <= it.backingEndMs() }
        }
    }
    if (preview.mediaId != metadata?.id || index !in leads.indices) return
    androidx.compose.foundation.layout.Column(modifier.fillMaxWidth().clickable(onClick = onOpenLyrics).padding(vertical = 8.dp)) {
    // Only the line being sung: a preview of the next one competed with it for the eye.
    AnimatedContent(
        targetState = leads[index],
        transitionSpec = {
            val timing = if (animate) tween<Float>(340) else snap<Float>()
            val movement = if (animate) tween<androidx.compose.ui.unit.IntOffset>(340) else snap<androidx.compose.ui.unit.IntOffset>()
            ((fadeIn(timing) + slideInVertically(movement) { it / 3 }) togetherWith
                (fadeOut(timing) + slideOutVertically(movement) { -it / 3 }))
                .using(SizeTransform(clip = false))
        },
        label = "inlineLyrics",
        modifier = Modifier.fillMaxWidth(),
    ) { line ->
        SweptInlineLyric(
            entry = line,
            positionMs = { (if (playing) clock.longValue else currentPosition()) + offset },
            tint = tint,
            animate = animate,
        )
    }
    // The answering voice slides in from the side and back out, like a backing line in Convx Lyrics.
    AnimatedContent(
        targetState = backing,
        transitionSpec = {
            val timing = if (animate) tween<Float>(320) else snap<Float>()
            val movement = if (animate) tween<androidx.compose.ui.unit.IntOffset>(320) else snap<androidx.compose.ui.unit.IntOffset>()
            ((fadeIn(timing) + androidx.compose.animation.slideInHorizontally(movement) { it / 4 }) togetherWith
                (fadeOut(timing) + androidx.compose.animation.slideOutHorizontally(movement) { it / 4 }))
                .using(SizeTransform(clip = false))
        },
        label = "inlineBacking",
    ) { line ->
        if (line != null) Text(
            line.text,
            style = AppTheme.typography.bodyMedium,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = tint.copy(alpha = 0.6f),
        )
    }
    }
}

/** When a backing line is done: its last word's end, or a short hold for line-timed lyrics. */
private fun LyricsEntry.backingEndMs(): Long =
    words?.lastOrNull()?.let { (it.endTime * 1000).toLong() } ?: (time + BACKING_FALLBACK_MS)

private const val BACKING_FALLBACK_MS = 2_500L

@Composable
private fun SweptInlineLyric(entry: LyricsEntry, positionMs: () -> Long, tint: Color, animate: Boolean) {
    val words = remember(entry) { entry.wordsWithSpacing() }
    val text = remember(entry, words) { if (words.isEmpty()) entry.text else words.joinToString("") { it.text } }
    var layout by remember(entry) { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text = text,
        style = AppTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = tint.copy(alpha = if (words.isEmpty()) 1f else 0.4f),
        onTextLayout = { layout = it },
        modifier = Modifier.drawWithCache {
            val measured = layout
            var offset = 0
            val spans = if (measured == null) emptyList() else words.map { word ->
                val start = offset
                offset += word.text.length
                val visibleEnd = measured.getLineEnd(measured.lineCount - 1, visibleEnd = true)
                val boxes = (start until minOf(offset, visibleEnd)).map(measured::getBoundingBox)
                val bounds = boxes.reduceOrNull { a, b -> androidx.compose.ui.geometry.Rect(
                    minOf(a.left, b.left), minOf(a.top, b.top), maxOf(a.right, b.right), maxOf(a.bottom, b.bottom),
                ) }
                Triple(word, bounds, start < text.length && measured.getBidiRunDirection(start) == ResolvedTextDirection.Rtl)
            }
            val path = Path()
            onDrawWithContent {
                drawContent()
                if (measured != null) {
                    path.reset()
                    val position = positionMs() / 1000.0
                    spans.forEach { (word, bounds, rtl) ->
                        if (bounds != null) {
                            val duration = (word.endTime - word.startTime).coerceAtLeast(0.001)
                            val progress = if (!animate) {
                                if (position >= word.startTime) 1f else 0f
                            } else ((position - word.startTime) / duration).toFloat().coerceIn(0f, 1f)
                            if (progress > 0f) path.addRect(if (rtl) bounds.copy(left = bounds.right - bounds.width * progress)
                                else bounds.copy(right = bounds.left + bounds.width * progress))
                        }
                    }
                    clipPath(path) { drawText(measured, color = tint) }
                }
            }
        },
    )
}
