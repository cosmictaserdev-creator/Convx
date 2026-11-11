package com.convx.music.lyrics

import kotlin.test.Test
import kotlin.test.assertEquals

class TimedLyricsParserCommonTest {
    @Test
    fun parsesStandardRowsAndAttachesFollowingWordTiming() {
        val lines = TimedLyricsParser.parse(
            "[offset:300]\n[00:01.00]Hello\n<Hello:1.0:1.4>\n[00:02.50]world",
        )

        assertEquals(listOf(1_000L, 2_500L), lines.map { it.time })
        assertEquals("Hello", lines[0].text)
        assertEquals("Hello", lines[0].words?.single()?.text)
        assertEquals(1.0, lines[0].words?.single()?.startTime ?: -1.0, 0.0001)
        assertEquals("world", lines[1].text)
    }

    @Test
    fun parsesRichWordTimingsAndUsesTheNextLineForTheLastWord() {
        val lines = TimedLyricsParser.parse(
            "[00:01.00]<00:01.00|00:01.70>Hello <00:01.80|00:02.40>world\n" +
                "[00:03.00]<00:03.00|00:03.50>again",
        )

        assertEquals("Hello world", lines[0].text)
        assertEquals(listOf("Hello", "world"), lines[0].words?.map { it.text })
        assertEquals(1.7, lines[0].words?.get(0)?.endTime ?: -1.0, 0.0001)
        assertEquals(2.4, lines[0].words?.get(1)?.endTime ?: -1.0, 0.0001)
        assertEquals(3.5, lines[1].words?.single()?.endTime ?: -1.0, 0.0001)
    }

    @Test
    fun decodesSupplementaryEntitiesAndExtractsVocalMarkers() {
        val line = TimedLyricsParser.parse(
            "[00:01.00]{agent:v1}{bg} Hi &#x1F3B5; &amp; you",
        ).single()

        assertEquals("v1", line.agent)
        assertEquals(true, line.isBackground)
        assertEquals(" Hi \uD83C\uDFB5 & you", line.text)
    }
}
