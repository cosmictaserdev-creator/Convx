package com.convx.music.playback

import com.convx.music.lyrics.LyricsEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerLyricsPreviewTest {
    private val lines = listOf(LyricsEntry(1000, "First"), LyricsEntry(3000, "Second"))

    @Test fun silenceBeforeFirstLineHasNoCurrentLyric() {
        assertEquals(-1, currentPreviewLine(lines, 999))
        assertEquals(-1, currentPreviewLine(emptyList(), 1000))
    }

    @Test fun seekingInEitherDirectionSelectsTheTimedLine() {
        assertEquals(1, currentPreviewLine(lines, 3000))
        assertEquals(0, currentPreviewLine(lines, 2999))
        assertEquals(0, currentPreviewLine(lines, 1000))
        assertEquals(1, currentPreviewLine(lines, Long.MAX_VALUE))
    }
}
