/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.wordsWithSpacing
import com.convx.music.lyrics.WordTimestamp
import com.mocharealm.accompanist.lyrics.core.model.ISyncedLine
import com.mocharealm.accompanist.lyrics.core.model.SyncedLyrics
import com.mocharealm.accompanist.lyrics.core.model.karaoke.KaraokeAlignment
import com.mocharealm.accompanist.lyrics.core.model.karaoke.KaraokeLine
import com.mocharealm.accompanist.lyrics.core.model.karaoke.KaraokeSyllable
import com.mocharealm.accompanist.lyrics.core.model.synced.SyncedLine
import com.mocharealm.accompanist.lyrics.ui.composable.lyrics.KaraokeLyricsView
import kotlin.math.roundToInt

/**
 * Renders syllable highlighting, line depth, and duet alignment with the shared karaoke renderer.
 * TTML `v2` lines appear on the right; background-vocal lines join the line above them.
 */
@Composable
fun KaraokeLyrics(
    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,
    lines: List<LyricsEntry>,
    /**
     * The playhead, read as a lambda rather than passed as a value on purpose: the
     * renderer samples it in its own frame loop, so the position can tick without
     * recomposing anything here.
     */
    positionMs: () -> Long,
    textColor: Color,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    fontSize: Float = 30f,
    lineSpacing: Float = 1.3f,
    /** Blurs the lines either side of the active one. Off is cheaper and flatter. */
    useBlur: Boolean = true,
    onLineClick: (startMs: Int) -> Unit = {},
) {
    val synced = remember(lines) { lines.toSyncedLyrics() }

    val normalStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * lineSpacing).sp,
        fontWeight = FontWeight.Bold,
        // Keep tracking proportional to the lyric size.
        letterSpacing = (fontSize * 0.012f).sp,
    )
    // Background vocals use smaller, bold text.
    val accompanimentStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = (fontSize * 0.82f).sp,
        lineHeight = (fontSize * 0.82f * lineSpacing).sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (fontSize * 0.82f * 0.012f).sp,
    )
    val phoneticStyle = TextStyle(
        fontFamily = fontFamily,
        fontSize = (fontSize * 0.5f).sp,
        lineHeight = (fontSize * 0.5f * lineSpacing).sp,
        fontWeight = FontWeight.Medium,
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val viewportOffset = maxHeight * 0.5f - with(androidx.compose.ui.platform.LocalDensity.current) {
            (fontSize * lineSpacing / 2f).sp.toDp()
        }

        // Reset scroll and per-line animation state when the lyrics change.
        key(synced) {
        KaraokeLyricsView(
            listState = listState,
            lyrics = synced,
            currentPosition = { positionMs().toInt() },
            onLineClicked = { line -> if (line.start > 0) onLineClick(line.start) },
            onLinePressed = {},
            textColor = textColor,
            normalLineTextStyle = normalStyle,
            accompanimentLineTextStyle = accompanimentStyle,
            phoneticTextStyle = phoneticStyle,
            blendMode = BlendMode.SrcOver,
            useBlurEffect = useBlur,
            showTranslation = false,
            showPhonetic = false,
            offset = viewportOffset,
            keepAliveZone = 72.dp,
            modifier = Modifier.fillMaxSize(),
        )
        }
    }
}

/**
 * Whether this set of lines has enough timing to be worth handing to the karaoke view.
 *
 * With no word timings anywhere it degrades to plain line-synced text, which every other
 * style already does at a fraction of the cost, so the caller should not switch to it.
 */
fun List<LyricsEntry>.hasKaraokeTimings(): Boolean =
    any { !it.words.isNullOrEmpty() }

/**
 * Convx's own [LyricsEntry] list, in the shape the renderer wants.
 *
 * Entries flagged `isBackground` are not emitted as lines. They are attached to the last
 * main line as accompaniment, which is where they belong — a background vocal is sung
 * *over* the line above it, not after it.
 */
internal fun List<LyricsEntry>.toSyncedLyrics(): SyncedLyrics {
    val usable = filter { it.time >= 0L && (it.text.isNotBlank() || !it.words.isNullOrEmpty()) }
    if (usable.isEmpty()) return SyncedLyrics(emptyList())

    val out = mutableListOf<ISyncedLine>()
    var index = 0
    while (index < usable.size) {
        val entry = usable[index]

        if (entry.isBackground) {
            // Nothing above it to ride on — first in the file, or the provider left the
            // main line out. Shown on its own rather than silently dropped, which would
            // read as a gap over a record that is audibly still singing.
            entry.toLine(entry.endMs(usable.getOrNull(index + 1)), emptyList())?.let { out += it }
            index++
            continue
        }

        // In TTML a background line *follows* the line it is sung under, so this one
        // takes every background entry between it and the next main line.
        var cursor = index + 1
        val background = mutableListOf<LyricsEntry>()
        while (cursor < usable.size && usable[cursor].isBackground) {
            background += usable[cursor]
            cursor++
        }

        entry.toLine(entry.endMs(usable.getOrNull(cursor)), background)?.let { out += it }
        index = cursor
    }

    return SyncedLyrics(lines = out.sortedBy { it.start })
}

/**
 * Where a line stops.
 *
 * Word timings say so exactly. Without them the line runs until the next one starts,
 * capped at [MaxLineHoldMs] — an LRC line before a long instrumental would otherwise
 * stay lit for the whole break, which reads as the app having lost its place.
 */
private fun LyricsEntry.endMs(next: LyricsEntry?): Int {
    words?.lastOrNull()?.let { return (it.endTime * 1000.0).roundToInt() }
    val gapEnd = next?.time?.minus(1L) ?: (time + MaxLineHoldMs)
    return minOf(gapEnd, time + MaxLineHoldMs).coerceAtLeast(time + 1L).toInt()
}

private fun LyricsEntry.toLine(
    endMs: Int,
    background: List<LyricsEntry>,
): ISyncedLine? {
    val alignment = if (agent?.lowercase() == "v2") KaraokeAlignment.End else KaraokeAlignment.Start
    val syllables = wordsWithSpacing().takeIf { it.isNotEmpty() }?.toSyllables()

    if (syllables.isNullOrEmpty()) {
        // No word timings: a plain synced line still renders, it just does not light up
        // syllable by syllable.
        if (text.isBlank()) return null
        return SyncedLine(content = text, translation = null, start = time.toInt(), end = endMs)
    }

    val start = syllables.first().start
    val end = syllables.last().end
    if (end <= start) return null

    val accompaniment = background.mapNotNull { entry ->
        val bg = entry.wordsWithSpacing().takeIf { it.isNotEmpty() }?.toSyllables() ?: return@mapNotNull null
        val bgStart = bg.first().start
        val bgEnd = bg.last().end
        if (bgEnd <= bgStart) return@mapNotNull null
        KaraokeLine.AccompanimentKaraokeLine(
            syllables = bg,
            translation = null,
            alignment = alignment,
            start = bgStart,
            end = bgEnd,
            phonetic = null,
        )
    }.ifEmpty { null }

    return KaraokeLine.MainKaraokeLine(
        syllables = syllables,
        translation = null,
        alignment = alignment,
        start = start,
        end = end,
        phonetic = null,
        accompanimentLines = accompaniment,
    )
}

/**
 * Word timings, clamped so no syllable outlives the one after it.
 *
 * Providers routinely publish an end time past the next word's start — a held vowel, or
 * just a sloppy export. Left alone the highlight of two syllables overlaps and the line
 * looks like it is lighting in two places at once.
 */
private fun List<WordTimestamp>.toSyllables(): List<KaraokeSyllable> =
    mapIndexed { index, word ->
        val start = (word.startTime * 1000.0).roundToInt().coerceAtLeast(0)
        val nextStart = getOrNull(index + 1)?.let { (it.startTime * 1000.0).roundToInt() }
        val rawEnd = (word.endTime * 1000.0).roundToInt().coerceAtLeast(0)
        val end = nextStart?.let { minOf(rawEnd, it) } ?: rawEnd
        KaraokeSyllable(
            content = word.text,
            start = start,
            end = end.coerceAtLeast(start + MinSyllableMs),
            phonetic = null,
        )
    }

/** A syllable shorter than this cannot be seen to animate at all. */
private const val MinSyllableMs = 1

/** How long a word-less line may stay lit before the next one arrives. */
private const val MaxLineHoldMs = 4_000L
