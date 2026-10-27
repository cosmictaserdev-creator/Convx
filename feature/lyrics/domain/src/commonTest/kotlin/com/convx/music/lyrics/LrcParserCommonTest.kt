package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.LrcParser
import com.convx.music.lyrics.bitchord.LyricAlignment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LrcParserCommonTest {
    @Test
    fun parsesPortableLyricsModelsAndPreservesWordTiming() {
        val lines = LrcParser.parse("[00:01.00]<R><00:01.00>Hello <00:01.50>world<00:02.00>")

        assertEquals(1, lines.size)
        val line = lines.single()
        assertEquals("Hello world", line.text)
        assertEquals(LyricAlignment.End, line.alignment)
        assertEquals(listOf("Hello", "world"), line.words.map { it.text })
        assertEquals(listOf(1_000L, 1_500L), line.words.map { it.startMs })
        assertEquals(listOf(1_500L, 2_000L), line.words.map { it.endMs })
    }

    @Test
    fun keepsAnIntroAndAClosingInstrumentalStamp() {
        val lines = LrcParser.parse("[00:04.00]First\n[00:08.00]")

        assertEquals(listOf(0L, 4_000L, 8_000L), lines.map { it.timeMs })
        assertTrue(lines.first().isGap)
        assertTrue(lines.last().isGap)
    }
}
