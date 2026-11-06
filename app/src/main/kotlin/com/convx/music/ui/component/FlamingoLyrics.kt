package com.convx.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.wordsWithSpacing
import com.convx.music.lyrics.primaryLyricLineIndex
import com.convx.music.ui.component.flamingo.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.combine

/** Adapts persisted timing and text to the scrolling word renderer. */
@Composable
fun FlamingoLyrics(
    lines: List<LyricsEntry>,
    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,
    fontSize: Float = 30f,
    lineSpacing: Float = 1.3f,
    showTranslation: Boolean = false,
    positionMs: () -> Long,
    useBlur: Boolean,
    isSynced: Boolean = true,
    autoScrollEnabled: Boolean = true,
    glow: Boolean = false,
    artistName: String? = null,
    modifier: Modifier = Modifier,
    onSeek: (Int) -> Unit,
) {
    val entries = remember(lines) {
        lines.map { line ->
            val words = line.wordsWithSpacing()
            val runs = listOf(line.time.toFloat() to "") +
                if (words.isEmpty()) listOf(line.time.toFloat() to line.text) else
                    words.map { word ->
                        (word.endTime * 1000).toFloat() to word.text
                    }
            // Segment pairs carry end times; the final pair is secondary text.
            runs + listOf(line.time.toFloat() to "")
        }
    }
    val ends = remember(lines) {
        lines.mapIndexed { index, line ->
            line.words?.lastOrNull()?.endTime?.times(1000)?.toFloat()
                ?: (lines.getOrNull(index + 1)?.time ?: (line.time + 5000)).toFloat()
        }
    }
    val sides = remember(lines) { lines.map { it.agent == "v2" } }
    val backgrounds = remember(lines) { lines.map { it.isBackground } }
    val exposureTarget = remember { mutableFloatStateOf(0f) }
    val exposure = animateFloatAsState(exposureTarget.floatValue, tween(350), label = "lyric_exposure")
    val subtitles by remember(lines) { combine(lines.map { it.translatedTextFlow }) { it.toList() } }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val transliterations by remember(lines) { combine(lines.map { it.romanizedTextFlow }) { it.toList() } }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val wordTimed = remember(lines) { lines.any { !it.words.isNullOrEmpty() } }
    val current = remember(lines, isSynced) { mutableIntStateOf(if (isSynced) primaryLyricLineIndex(lines, positionMs()) else -1) }
    val playhead by rememberUpdatedState(positionMs)
    LaunchedEffect(lines, isSynced) {
        if (!isSynced) return@LaunchedEffect
        while (true) {
            withFrameNanos { }
            val position = playhead()
            current.intValue = primaryLyricLineIndex(lines, position)
            exposureTarget.floatValue = heldWordExposure(lines.getOrNull(current.intValue), position)
        }
    }
    Box(modifier) {
    key(lines) {
        YosLyricView(
            lrcEntriesLambda = { entries },
            lineEndTimesLambda = { ends },
            lineSubtitlesLambda = { subtitles },
            lineTransliterationsLambda = { transliterations },
            isTtmlLyricsLambda = { wordTimed },
            liveTimeLambda = { positionMs().coerceIn(0, Int.MAX_VALUE.toLong()).toInt() },
            mediaEvent = remember(onSeek) { object : YosMediaEvent {
                override fun onSeek(position: Int) = onSeek(position)
            } },
            translationLambda = { showTranslation },
            blurLambda = { useBlur && isSynced },
            isSynced = isSynced,
            autoScrollEnabled = autoScrollEnabled,
            weightLambda = { false },
            otherSideForLines = sides,
            backgroundLines = backgrounds,
            glow = glow && isSynced,
            exposure = { exposure.value },
            currentLyricIndex = current,
            modifier = Modifier.fillMaxSize(),
            textStyle = MainTextStyle.copy(fontFamily = fontFamily, fontSize = fontSize.sp, lineHeight = (fontSize * lineSpacing).sp),
            onBackClick = {},
        )
    }
    if (!artistName.isNullOrBlank()) {
        LyricsArtistIntro(artistName, positionMs, Modifier.align(Alignment.TopCenter).padding(top = 72.dp))
    }
    }
}

/** Longer held words burn brighter: 0 for a quick word, 1 for one held over about 1.5 s. */
private fun heldWordExposure(line: LyricsEntry?, positionMs: Long): Float {
    val seconds = positionMs / 1000.0
    val word = line?.words?.firstOrNull { seconds >= it.startTime && seconds <= it.endTime } ?: return 0f
    return (((word.endTime - word.startTime) * 1000 - 250) / 1200).toFloat().coerceIn(0f, 1f)
}
