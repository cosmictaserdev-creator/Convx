package com.convx.music.lyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedLyricsParserTest {
    @Test
    fun parsesStandardRowsAndAttachesWordTimings() {
        val lines = TimedLyricsParser.parse(
            "[offset:300]\n[00:01.00]Hello\n<Hello:1.0:1.4>\n[00:02.50]world",
        )

        assertEquals(listOf(1_000L, 2_500L), lines.map { it.time })
        assertEquals("Hello", lines[0].text)
        assertEquals(WordTimestamp("Hello", 1.0, 1.4), lines[0].words?.single())
        assertEquals("world", lines[1].text)
    }

    @Test
    fun parsesRichWordEndsAndUsesTheNextLineForItsLastWord() {
        val lines = TimedLyricsParser.parse(
            "[00:01.00]<00:01.00|00:01.70>Hello <00:01.80|00:02.40>world\n" +
                "[00:03.00]<00:03.00|00:03.50>again",
        )

        assertEquals("Hello world", lines[0].text)
        assertEquals(listOf("Hello", "world"), lines[0].words?.map { it.text })
        assertEquals(1.7, lines[0].words?.get(0)?.endTime ?: -1.0, 0.0)
        assertEquals(2.4, lines[0].words?.get(1)?.endTime ?: -1.0, 0.0)
        assertEquals(3.5, lines[1].words?.single()?.endTime ?: -1.0, 0.0)
    }

    @Test
    fun decodesSupplementaryEntitiesAndExtractsVocalMarkers() {
        val line = TimedLyricsParser.parse(
            "[00:01.00]{agent:v1}{bg} Hi &#x1F3B5; &amp; you",
        ).single()

        assertEquals("v1", line.agent)
        assertTrue(line.isBackground)
        assertEquals(" Hi \uD83C\uDFB5 & you", line.text)
    }
}
