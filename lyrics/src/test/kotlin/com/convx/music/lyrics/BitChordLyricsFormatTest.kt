package com.convx.music.lyrics

import com.convx.music.lyrics.bitchord.*
import org.junit.Assert.*
import org.junit.Test

class BitChordLyricsFormatTest {
    @Test fun preservesWordEndsDuetsAndBackgrounds() {
        val line = LyricLine(timeMs = 1250, text = "Stay here", words = listOf(
            LyricWord(1250, 1800, "Stay"), LyricWord(2000, 2500, "here")),
            alignment = LyricAlignment.End,
            background = LyricLine(2000, "Oh", words = listOf(LyricWord(2000, 2400, "Oh"))))
        val encoded = listOf(line).toConvxLyrics()
        assertTrue(encoded.contains("{agent:v2}"))
        assertTrue(encoded.contains("<00:01.250|00:01.800>Stay"))
        assertTrue(encoded.contains("<00:02.000|00:02.500>here"))
        assertTrue(encoded.contains("[00:02.000]{bg}"))
    }
}
