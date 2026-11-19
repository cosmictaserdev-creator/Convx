package com.convx.music.ui.player

import android.animation.ValueAnimator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.R
import com.convx.music.constants.PlayerInlineLyricsKey
import com.convx.music.constants.ReduceDynamicBlurKey
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.wordsWithSpacing
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.PlayerLyricsPreview
import com.convx.music.playback.currentPreviewLine
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberPreference

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
    val mediaId = metadata?.id ?: return
    if (preview.mediaId != mediaId || preview.lines.isEmpty()) return

    val song by connection.currentSong.collectAsStateWithLifecycle(null)
    val playing by connection.isEffectivelyPlaying.collectAsStateWithLifecycle()
    val reduceEffects by rememberPreference(ReduceDynamicBlurKey, false)
    val animate = !reduceEffects && ValueAnimator.areAnimatorsEnabled()
    val currentPosition by rememberUpdatedState(positionMs)
    val framePosition by rememberUpdatedState(framePositionMs)
    val clock = remember(mediaId) { mutableLongStateOf(positionMs()) }
    LaunchedEffect(playing, mediaId) {
        if (playing) {
            clock.longValue = framePosition()
            while (true) withFrameNanos { clock.longValue = framePosition() }
        }
    }

    val offset = song?.takeIf { it.song.id == mediaId }?.song?.lyricsOffset?.toLong() ?: 0L
    val leads = remember(preview) { preview.lines.filter { !it.isBackground && it.text.isNotBlank() } }
    val backings = remember(preview) { preview.lines.filter { it.isBackground && it.text.isNotBlank() } }
    val position = remember(clock, offset, playing) {
        { (if (playing) clock.longValue else currentPosition()) + offset }
    }
    val index by remember(leads, position) {
        derivedStateOf { currentPreviewLine(leads, position()) }
    }
    val backing by remember(backings, position) {
        derivedStateOf {
            val now = position()
            backings.lastOrNull { it.time <= now && now <= it.backingEndMs() }
        }
    }
    if (index !in leads.indices) return

    key(mediaId) {
        Column(
            modifier.fillMaxWidth()
                .clickable(onClickLabel = stringResource(R.string.lyrics), role = Role.Button, onClick = onOpenLyrics)
                .padding(vertical = 8.dp),
        ) {
            AnimatedContent(
                targetState = leads[index],
                transitionSpec = {
                    val enter = if (animate) fadeIn(tween(340)) + slideInVertically(tween(340)) { it / 3 } else EnterTransition.None
                    val exit = if (animate) fadeOut(tween(340)) + slideOutVertically(tween(340)) { -it / 3 } else ExitTransition.None
                    (enter togetherWith exit).using(if (animate) SizeTransform(clip = false) else null)
                },
                label = "inlineLyrics",
                modifier = Modifier.fillMaxWidth(),
            ) { line ->
                SweptInlineLyric(entry = line, positionMs = position, tint = tint, animate = animate)
            }

            AnimatedContent(
                targetState = backing,
                transitionSpec = {
                    val enter = if (animate) fadeIn(tween(320)) + slideInHorizontally(tween(320)) { it / 4 } else EnterTransition.None
                    val exit = if (animate) fadeOut(tween(320)) + slideOutHorizontally(tween(320)) { it / 4 } else ExitTransition.None
                    (enter togetherWith exit).using(if (animate) SizeTransform(clip = false) else null)
                },
                label = "inlineBacking",
            ) { line ->
                if (line != null) {
                    Text(
                        text = line.text,
                        style = AppTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = tint.copy(alpha = 0.6f),
                    )
                }
            }
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
                val bounds = boxes.reduceOrNull { a, b -> Rect(
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
