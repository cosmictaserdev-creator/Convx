/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Connects Convx's lyrics and player to the lyrics panel ported from BitChord (GPL-3.0). See NOTICE.
 */
package com.convx.music.ui.component.bitchord

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.bitchord.LyricAlignment
import com.convx.music.lyrics.bitchord.LyricLine
import com.convx.music.lyrics.bitchord.LyricWord
import kotlinx.coroutines.delay
import kotlin.math.abs

/** How far apart two readings may be beyond the time that passed before it counts as a deliberate jump. */
private const val SEEK_JUMP_MS = 1_200L

/** About twice a second, which is what the panel's own clock is built to be steered by. */
private const val READING_INTERVAL_MS = 250L

/**
 * BitChord's lyrics panel, driven by Convx's lyrics.
 *
 * [positionMs] is the raw player position; [offsetMs] is the song's Convx lyric offset (added to the position,
 * the opposite sign to BitChord's own). Seeks go back to the app through [onSeekMs] with the offset already
 * undone.
 */
@Composable
fun BitChordLyricsView(
    lines: List<LyricsEntry>,
    trackKey: String,
    isPlaying: Boolean,
    isSynced: Boolean,
    positionMs: () -> Long,
    offsetMs: Int,
    blur: Boolean,
    onSeekMs: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bitChordLines = remember(lines) { lines.toBitChordLines() }

    // The panel reads these as flows; keep them in step with what Convx has chosen.
    LaunchedEffect(offsetMs, blur) {
        PlayerPlatform.host.settings.update(blur = blur, offsetMs = -offsetMs)
    }

    val position = remember(trackKey) { PlaybackPosition() }
    val readPosition = rememberUpdatedState(positionMs)
    val playing = rememberUpdatedState(isPlaying)
    LaunchedEffect(position) {
        var last = readPosition.value()
        var lastAt = System.nanoTime()
        position.report(last, lastAt)
        while (true) {
            delay(READING_INTERVAL_MS)
            val now = System.nanoTime()
            val reading = readPosition.value()
            val expected = last + if (playing.value) (now - lastAt) / 1_000_000L else 0L
            // A reading far from where playback should have got to is a seek, which the panel must not smooth over.
            if (abs(reading - expected) > SEEK_JUMP_MS) position.seeks++
            position.report(reading, now)
            last = reading
            lastAt = now
        }
    }

    LyricsPanel(
        lines = bitChordLines,
        trackKey = trackKey,
        playhead = rememberLyricPlayhead(position),
        looking = false,
        isPlaying = isPlaying,
        onSeekToLine = { lineMs -> if (isSynced) onSeekMs(adjustedLyricsSeekTarget(lineMs, -offsetMs)) },
        controlsOpen = false,
        onRevealControls = {},
        onHideControls = {},
        modifier = modifier,
    )
}

/** Convx's entries to BitChord's lines: background vocals attach to the line they sing under, and a second voice sits at the end. */
internal fun List<LyricsEntry>.toBitChordLines(): List<LyricLine> {
    val firstAgent = firstOrNull { !it.isBackground && it.agent != null }?.agent
    val lines = ArrayList<LyricLine>(size)
    for (entry in this) {
        val line = LyricLine(
            timeMs = entry.time,
            text = entry.text,
            words = entry.words.orEmpty().map { word ->
                LyricWord(
                    startMs = (word.startTime * 1000).toLong(),
                    endMs = (word.endTime * 1000).toLong(),
                    text = word.text,
                )
            },
            alignment = if (firstAgent != null && entry.agent != null && entry.agent != firstAgent) {
                LyricAlignment.End
            } else {
                LyricAlignment.Start
            },
        )
        if (entry.isBackground && lines.isNotEmpty() && lines.last().background == null) {
            lines[lines.lastIndex] = lines.last().copy(background = line)
        } else {
            lines += line
        }
    }
    return lines
}
