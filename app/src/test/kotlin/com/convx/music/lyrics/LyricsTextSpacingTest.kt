package com.convx.music.lyrics

import org.junit.Assert.assertEquals
import org.junit.Test

class LyricsTextSpacingTest {
    private fun line(text: String, vararg tokens: String) = LyricsEntry(0, text,
        tokens.mapIndexed { index, token -> WordTimestamp(token, index.toDouble(), index + 1.0) })

    @Test fun preservesWordSpacesAndPunctuation() {
        val runs = line("Hello, world!", "Hello", "world").wordsWithSpacing()
        assertEquals(listOf("Hello, ", "world!"), runs.map { it.text })
        assertEquals(1.0, runs.first().endTime, 0.0)
    }

    @Test fun preservesSyllablesAndContractions() {
        assertEquals(listOf("I ", "don", "'t ", "know"),
            line("I don't know", "I", "don", "'t", "know").wordsWithSpacing().map { it.text })
    }

    @Test fun doesNotInsertSpacesIntoUnspacedScripts() {
        assertEquals("\u4f60\u597d", line("\u4f60\u597d", "\u4f60", "\u597d").wordsWithSpacing().joinToString("") { it.text })
    }

    @Test fun repairsMissingBoundariesWhenProviderTextDoesNotMatch() {
        assertEquals("Hello world", line("Different text", "Hello", "world").wordsWithSpacing().joinToString("") { it.text })
    }

    @Test fun richSyncRoundTripKeepsSpacesForConcatenatingRenderers() {
        val parsed = LyricsUtils.parseLyrics("[00:01.00]<00:01.00>Hello <00:02.00>world").single()
        assertEquals("Hello world", parsed.wordsWithSpacing().joinToString("") { it.text })
    }

    @Test fun backgroundAndDuetRowsDoNotChangeThePrimaryScrollAnchor() {
        val lines = listOf(LyricsEntry(0, "First"), LyricsEntry(1000, "Main"),
            LyricsEntry(1000, "Duet", agent = "v2"), LyricsEntry(1200, "Backing", isBackground = true))
        assertEquals(1, primaryLyricLineIndex(lines, 1500))
        assertEquals(1, primaryLyricLineIndex(lines, 1100))
        assertEquals(-1, primaryLyricLineIndex(lines, -1))
    }

    @Test fun leadingSpacerDoesNotStealTheFirstTimestamp() {
        val lines = listOf(LyricsEntry.HEAD_LYRICS_ENTRY, LyricsEntry(0, "First line"), LyricsEntry(1000, "Next"))
        assertEquals(1, primaryLyricLineIndex(lines, 0))
        assertEquals(1, primaryLyricLineIndex(lines, 500))
    }

    @Test fun blankRowsNeverBecomeTheFollowTarget() {
        val lines = listOf(LyricsEntry(0, "First line"), LyricsEntry(1000, "  "), LyricsEntry(2000, "Next"))
        assertEquals(0, primaryLyricLineIndex(lines, 1500))
        assertEquals(-1, primaryLyricLineIndex(listOf(LyricsEntry.HEAD_LYRICS_ENTRY), 1000))
    }
}
