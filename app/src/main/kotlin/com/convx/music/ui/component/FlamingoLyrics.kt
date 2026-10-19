package com.convx.music.ui.component

import androidx.compose.runtime.*
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
    wordGlowEnabled: Boolean = true,
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
        }
    }
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
            currentLyricIndex = current,
            modifier = modifier,
            textStyle = MainTextStyle.copy(fontFamily = fontFamily, fontSize = fontSize.sp, lineHeight = (fontSize * lineSpacing).sp),
            onBackClick = {},
            wordGlowEnabled = wordGlowEnabled,
        )
    }
}
