package com.convx.music.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals

class LyricTimelineCommonTest {
    @Test
    fun skipsBlankAndBackgroundRowsWhenFindingCurrentMainLine() {
        val lines = listOf(
            LyricsEntry(time = 1_000L, text = "First"),
            LyricsEntry(time = 2_000L, text = "  "),
            LyricsEntry(time = 2_500L, text = "Echo", isBackground = true),
            LyricsEntry(time = 3_000L, text = "Next"),
        )

        assertEquals(-1, primaryIndex(lines, positionMs = 999L))
        assertEquals(0, primaryIndex(lines, positionMs = 2_999L))
        assertEquals(3, primaryIndex(lines, positionMs = 3_000L))
    }

    @Test
    fun usesFirstMainRowWhenSeveralRowsShareTheCurrentTimestamp() {
        val lines = listOf(
            LyricsEntry(time = 1_000L, text = "Lead"),
            LyricsEntry(time = 1_000L, text = "Duet"),
            LyricsEntry(time = 1_200L, text = "Backing", isBackground = true),
        )

        assertEquals(0, primaryIndex(lines, positionMs = 1_500L))
    }

    @Test
    fun returnsNoLineForEmptyOrOnlyNonPrimaryRows() {
        assertEquals(-1, primaryIndex(emptyList(), positionMs = 5_000L))
        assertEquals(
            -1,
            primaryIndex(
                listOf(LyricsEntry(time = 1_000L, text = "", isBackground = true)),
                positionMs = 5_000L,
            ),
        )
    }

    private fun primaryIndex(lines: List<LyricsEntry>, positionMs: Long) =
        findPrimaryLyricLineIndex(
            lines = lines,
            positionMs = positionMs,
            timeMs = LyricsEntry::time,
            isBackground = LyricsEntry::isBackground,
            text = LyricsEntry::text,
        )
}
