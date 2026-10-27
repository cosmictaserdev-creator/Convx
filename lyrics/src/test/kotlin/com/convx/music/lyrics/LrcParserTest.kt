package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.LrcLib
import com.convx.music.lyrics.bitchord.LrcParser
import com.convx.music.lyrics.bitchord.LyricAlignment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LrcParserTest {
    @Test
    fun parsesAndSortsCentisecondAndMillisecondTimestamps() {
        val lines = LrcParser.parse(
            """
            [ar:artist]
            [00:02.34]Second
            [00:01.005]First
            """.trimIndent(),
        )

        assertEquals(listOf(1_005L, 2_340L), lines.map { it.timeMs })
        assertEquals(listOf("First", "Second"), lines.map { it.text })
        assertTrue(lines.none { it.isGap })
    }

    @Test
    fun filtersShortGapsAndKeepsLongIntroBreakAndTrailingStamp() {
        val lines = LrcParser.parse(
            """
            [00:04.00]First
            [00:04.20]
            [00:05.00]Second
            [00:08.50]
            [00:12.50]Third
            [00:16.50]
            """.trimIndent(),
        )

        assertEquals(listOf(0L, 4_000L, 5_000L, 8_500L, 12_500L, 16_500L), lines.map { it.timeMs })
        assertEquals(listOf("", "First", "Second", "", "Third", ""), lines.map { it.text })
    }

    @Test
    fun parsesWordTimingAndAlignmentAndPreservesProviderFacade() {
        val lrc = "[00:01.00]<R><00:01.00>Hello <00:01.50>world<00:02.00>"
        val lines = LrcLib.parseLrc(lrc)

        assertEquals(1, lines.size)
        val line = lines.single()
        assertEquals("Hello world", line.text)
        assertEquals(LyricAlignment.End, line.alignment)
        assertTrue(line.isWordSynced)
        assertEquals(listOf("Hello", "world"), line.words.map { it.text })
        assertEquals(listOf(1_000L, 1_500L), line.words.map { it.startMs })
        assertEquals(listOf(1_500L, 2_000L), line.words.map { it.endMs })
        assertFalse(line.isGap)
    }

    @Test
    fun ignoresMetadataAndRowsWithoutAValidTimestamp() {
        assertTrue(LrcParser.parse("[ar:artist]\nnot an LRC row").isEmpty())
    }
}
