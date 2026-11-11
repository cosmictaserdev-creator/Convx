package com.convx.music.ui.component

import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.WordTimestamp
import org.junit.Assert.assertEquals
import org.junit.Test

class StarCrawlLyricsTest {
    @Test fun spreadsAnUntimedLineOverItsTimeByLength() {
        val words = crawlWords(LyricsEntry(1_000, "go home"), nextLineMs = 3_000)
        assertEquals(listOf("go ", "home"), words.map { it.text })
        assertEquals(1_000L, words.first().startMs)
        assertEquals(words.first().endMs, words.last().startMs)
        assertEquals(true, words.last().endMs <= 3_000L)
    }

    @Test fun usesRealWordTimesWhenTheLineHasThem() {
        val entry = LyricsEntry(0, "hi there", words = listOf(WordTimestamp("hi ", 0.5, 0.9), WordTimestamp("there", 1.0, 1.6)))
        val words = crawlWords(entry, nextLineMs = null)
        assertEquals(CrawlWord("there", 1_000, 1_600), words[1])
    }

    @Test fun timedWordsKeepTheSpacesOfTheirLine() {
        val entry = LyricsEntry(0, "hold on tight", words = listOf(WordTimestamp("hold", 0.0, 0.4), WordTimestamp("on", 0.4, 0.8), WordTimestamp("tight", 0.8, 1.2)))
        assertEquals("hold on tight", crawlWords(entry, nextLineMs = null).joinToString("") { it.text })
    }

    @Test fun aWordBrightensAsThePlayheadCrossesIt() {
        val word = CrawlWord("b", 1_000, 2_000)
        assertEquals(0f, wordProgress(word, positionMs = 500), 0f)
        assertEquals(0.5f, wordProgress(word, positionMs = 1_500), 0.001f)
        assertEquals(1f, wordProgress(word, positionMs = 2_500), 0f)
    }
}
