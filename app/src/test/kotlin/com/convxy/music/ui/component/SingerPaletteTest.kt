/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import com.convxy.music.lyrics.LyricsEntry
import com.convxy.music.lyrics.SingerInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for the multi-singer color palette and the singer name resolver
 * (registry names, artist inference, shared vocals).
 */
class SingerPaletteTest {

    private fun entry(agent: String?, background: Boolean = false) =
        LyricsEntry(time = 0L, text = "x", agent = agent, isBackground = background)

    private fun displays(
        agents: List<String>,
        singers: Map<String, SingerInfo> = emptyMap(),
        artists: List<String> = emptyList(),
    ) = resolveSingerDisplays(agents.map { entry(it) }, singers, artists)

    @Test
    fun `colors assigned by first appearance and only for lead voices`() {
        val entries = listOf(entry("v1"), entry("v2"), entry("v1"), entry("v1000"), entry(null))

        val colors = SingerPalette.assignColors(entries)

        assertEquals(2, colors.size)
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[0], colors["v1"])
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[1], colors["v2"])
        // Group ("both") lines keep the global accent instead of a palette slot
        assertFalse(colors.containsKey("v1000"))
    }

    @Test
    fun `single voice yields no palette`() {
        assertTrue(SingerPalette.assignColors(listOf(entry("v1"), entry("v1"))).isEmpty())
        assertTrue(SingerPalette.assignColors(listOf(entry(null))).isEmpty())
        assertTrue(SingerPalette.assignColors(emptyList()).isEmpty())
    }

    @Test
    fun `background vocals do not consume palette slots`() {
        val entries = listOf(entry("v1"), entry("v2", background = true), entry("v2"))

        val colors = SingerPalette.assignColors(entries)

        assertEquals(SingerPalette.LEAD_SINGER_COLORS[0], colors["v1"])
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[1], colors["v2"])
    }

    @Test
    fun `palette cycles beyond eight singers`() {
        val entries = (1..10).map { entry("v$it") }

        val colors = SingerPalette.assignColors(entries)

        assertEquals(10, colors.size)
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[0], colors["v1"])
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[8 % 8], colors["v9"])
        assertEquals(SingerPalette.LEAD_SINGER_COLORS[9 % 8], colors["v10"])
        // The palette cycles, so only 8 distinct colors exist for 10 singers
        assertEquals(SingerPalette.LEAD_SINGER_COLORS.size, colors.values.toSet().size)
    }

    @Test
    fun `registry names win over inference`() {
        val singers = mapOf(
            "v1" to SingerInfo("v1", "Ryan Gosling"),
            "v2" to SingerInfo("v2", "Emma Stone"),
            "v1000" to SingerInfo("v1000", null, isGroup = true),
        )
        val result = displays(
            agents = listOf("v1", "v2", "v1000"),
            singers = singers,
            artists = listOf("Someone Else", "Another"),
        )

        assertEquals("Ryan Gosling", result.getValue("v1").name)
        assertFalse(result.getValue("v1").isGroup)
        assertTrue(result.getValue("v1000").isGroup)
        assertNull(result.getValue("v1000").name)
    }

    @Test
    fun `generic registry names are ignored`() {
        val singers = mapOf(
            "v1" to SingerInfo("v1", "Singer 1"),
            "v2" to SingerInfo("v2", "Voice 2"),
        )
        val result = displays(
            agents = listOf("v1", "v1", "v1", "v2"),
            singers = singers,
            artists = listOf("Ariana Grande", "Mac Miller"),
        )
        // Generic labels discarded; dominant voice gets the first-billed artist.
        assertEquals("Ariana Grande", result.getValue("v1").name)
        assertEquals("Mac Miller", result.getValue("v2").name)
    }

    @Test
    fun `registry name is rewritten to the matching track artist`() {
        val singers = mapOf(
            "v1" to SingerInfo("v1", "Ariana"),
            "v2" to SingerInfo("v2", "Mac"),
        )
        val result = displays(
            agents = listOf("v1", "v2"),
            singers = singers,
            artists = listOf("Ariana Grande", "Mac Miller"),
        )
        assertEquals("Ariana Grande", result.getValue("v1").name)
        assertEquals("Mac Miller", result.getValue("v2").name)
    }

    @Test
    fun `names inferred from track artists by vocal dominance not position`() {
        // Featured artist sings first (v1) but the billed artist sings more lines.
        val entries = listOf(
            entry("v1"),
            entry("v2"), entry("v2"), entry("v2"), entry("v2"),
        )
        val result = resolveSingerDisplays(
            entries,
            emptyMap(),
            listOf("Ariana Grande", "Mac Miller"),
        )
        assertEquals("Ariana Grande", result.getValue("v2").name)
        assertEquals("Mac Miller", result.getValue("v1").name)
    }

    @Test
    fun `close line counts do not guess names`() {
        val entries = listOf(entry("v1"), entry("v1"), entry("v2"), entry("v2"))
        val result = resolveSingerDisplays(
            entries,
            emptyMap(),
            listOf("Ariana Grande", "Mac Miller"),
        )
        assertNull(result.getValue("v1").name)
        assertNull(result.getValue("v2").name)
    }

    @Test
    fun `solo artist with two voices gets no guessed names`() {
        val result = displays(
            agents = listOf("v1", "v2", "v1", "v2"),
            artists = listOf("Taylor Swift"),
        )
        assertNull(result["v1"]?.name)
        assertNull(result["v2"]?.name)
    }

    @Test
    fun `named group agent keeps its name`() {
        val singers = mapOf("v1000" to SingerInfo("v1000", "Whole Choir", isGroup = true))
        val result = displays(
            agents = listOf("v1", "v2", "v1000"),
            singers = singers,
        )
        assertEquals("Whole Choir", result.getValue("v1000").name)
        assertTrue(result.getValue("v1000").isGroup)
    }

    @Test
    fun `usableSingerName rejects placeholders`() {
        assertNull(usableSingerName("Singer 1"))
        assertNull(usableSingerName("v2"))
        assertNull(usableSingerName("Male"))
        assertEquals("Emma Stone", usableSingerName("Emma Stone"))
    }
}
