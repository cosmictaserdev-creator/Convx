package com.convx.music.playback.presentation

import com.convx.music.lyrics.LyricsEntry
import com.convx.music.lyrics.WordTimestamp
import com.convx.music.ui.component.flamingo.HeldWordGrowth
import kotlin.test.*

class FisheyeScoreTest {
    @Test fun heldWordsStandAloneAndTheScoreIsStableAcrossReopening() {
        val lines = listOf(LyricsEntry(0, "I love this world", listOf(
            WordTimestamp("I", 0.0, .2), WordTimestamp("love", .2, 3.2),
            WordTimestamp("this", 3.2, 3.4), WordTimestamp("world", 3.4, 3.8),
        )))
        val first = composeFisheyeScore(lines)
        val next = composeFisheyeScore(lines)
        assertEquals(listOf("I", "love", "this", "world"), first.rows.flatMap { it.words.map(StackWord::text) })
        assertTrue(first.rows.single { row -> row.words.any { it.text == "love" } }.words.size == 1)
        assertEquals(first.rows.map { Triple(it.style, it.tilt, it.shift) }, next.rows.map { Triple(it.style, it.tilt, it.shift) })
    }

    @Test fun mixedLineAndWordTimingKeepsEveryLyricAndBackingVocal() {
        val score = composeFisheyeScore(listOf(
            LyricsEntry(0, "Opening line"),
            LyricsEntry(2_000, "hello", listOf(WordTimestamp("hello", 2.0, 3.5))),
            LyricsEntry(2_100, "backing", isBackground = true),
            LyricsEntry(4_000, "Closing line"),
        ))
        assertEquals(listOf("Opening", "line", "hello", "Closing", "line"), score.rows.flatMap { it.words.map(StackWord::text) })
        assertEquals("backing", score.backingAt(2_200)?.text)
    }

    @Test fun heldLetterGrowthRejectsInvalidTimingAndJoinedOrSurrogateScripts() {
        assertFalse(HeldWordGrowth.canGrow("love", Float.POSITIVE_INFINITY))
        assertFalse(HeldWordGrowth.canGrow("love", Float.NaN))
        assertFalse(HeldWordGrowth.canGrow("你好", 3_000f))
        assertFalse(HeldWordGrowth.canGrow("مرحبا", 3_000f))
        assertFalse(HeldWordGrowth.canGrow("😀", 3_000f))
        assertTrue(HeldWordGrowth.canGrow("love", 3_000f))
    }
}
