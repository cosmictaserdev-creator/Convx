/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.lyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for multi-singer lyric parsing: the `[singers:…]` registry header,
 * per-line agent markers, the `vN:` duet prefix dialect, shared vocals and
 * backward compatibility with plain LRC documents.
 */
class LyricsSingerParsingTest {

    @Test
    fun `parses singer registry header`() {
        val lyrics = listOf(
            "[singers:v1=Artist A|v2=Artist B]",
            "[00:01.00]{agent:v1}I remember all the things",
            "[00:05.00]{agent:v2}Nothing stays forever",
        ).joinToString("\n")

        val parsed = LyricsUtils.parseLyricsWithSingers(lyrics)

        assertEquals(2, parsed.singers.size)
        assertEquals("Artist A", parsed.singers.getValue("v1").name)
        assertEquals("Artist B", parsed.singers.getValue("v2").name)
        assertFalse(parsed.singers.getValue("v1").isGroup)
        assertTrue(parsed.hasMultipleSingers)
    }

    @Test
    fun `agent markers are extracted per line without header`() {
        val parsed = LyricsUtils.parseLyricsWithSingers(
            "[00:01.00]{agent:v1}Line one\n[00:05.00]{agent:v2}Line two"
        )

        assertEquals(listOf("v1", "v2"), parsed.entries.mapNotNull { it.agent })
        assertEquals(listOf("Line one", "Line two"), parsed.entries.map { it.text })
        assertTrue(parsed.singers.isEmpty())
        assertTrue(parsed.hasMultipleSingers)
    }

    @Test
    fun `v prefix duet dialect is recognized`() {
        val parsed = LyricsUtils.parseLyricsWithSingers("[00:12.00]v2: Nothing stays forever")

        val entry = parsed.entries.single()
        assertEquals("v2", entry.agent)
        assertEquals("Nothing stays forever", entry.text)
    }

    @Test
    fun `plain lrc without singer data stays backward compatible`() {
        val lyrics = "[00:01.00]First line\n[00:05.00]Second line"

        val parsed = LyricsUtils.parseLyricsWithSingers(lyrics)

        assertTrue(parsed.singers.isEmpty())
        assertFalse(parsed.hasMultipleSingers)
        assertNull(parsed.entries.first().agent)
        // The legacy entry point must keep returning plain entries
        assertEquals(listOf("First line", "Second line"), LyricsUtils.parseLyrics(lyrics).map { it.text })
    }

    @Test
    fun `single singer song is not multi singer`() {
        val parsed = LyricsUtils.parseLyricsWithSingers("[00:01.00]{agent:v1}A\n[00:05.00]{agent:v1}B")
        assertFalse(parsed.hasMultipleSingers)
    }

    @Test
    fun `group agent marks shared vocals`() {
        val parsed = LyricsUtils.parseLyricsWithSingers("[00:09.00]{agent:v1000}We'll sing together")

        val entry = parsed.entries.single()
        assertEquals("v1000", entry.agent)
        assertTrue(isSharedVocals(entry.agent))
        assertFalse(parsed.hasMultipleSingers)
    }

    @Test
    fun `composite agent forms normalize to group`() {
        val parsed = LyricsUtils.parseLyricsWithSingers("[00:09.00]{agent:v1+v2}We'll sing together")

        assertEquals("v1+v2", parsed.entries.single().agent)
        assertTrue(isSharedVocals("v1+v2"))
        assertTrue(isSharedVocals("v1, v2"))
        assertTrue(isSharedVocals("v1000"))
        assertFalse(isSharedVocals("v1"))
        assertFalse(isSharedVocals(null))
        assertEquals("v1000", primaryAgentId("v1+v2"))
        assertEquals("v1000", primaryAgentId("v1,v2"))
        assertEquals("v2", primaryAgentId("v2"))
    }

    @Test
    fun `malformed header falls back gracefully`() {
        val empty = LyricsUtils.parseLyricsWithSingers("[singers:]\n[00:01.00]Line")
        assertTrue(empty.singers.isEmpty())

        val bare = LyricsUtils.parseLyricsWithSingers("[singers:v1]\n[00:01.00]{agent:v1}Line")
        assertNull(bare.singers.getValue("v1").name)
    }

    @Test
    fun `header coexists with lrc metadata tags and word sidecar`() {
        val lyrics = listOf(
            "[ti:Song]",
            "[ar:Someone]",
            "[singers:v1=Ariana Grande|v2=Mac Miller|v1000=]",
            "[00:01.00]{agent:v1}Hello world",
            "<Hello:0.0:1.0|world:1.0:2.0>",
            "[00:05.00]{agent:v2}Every time",
        ).joinToString("\n")

        val parsed = LyricsUtils.parseLyricsWithSingers(lyrics)

        assertEquals(3, parsed.singers.size)
        assertEquals("Ariana Grande", parsed.singers.getValue("v1").name)
        assertEquals("Mac Miller", parsed.singers.getValue("v2").name)
        assertTrue(parsed.singers.getValue("v1000").isGroup)
        assertNull(parsed.singers.getValue("v1000").name)
        assertTrue(parsed.entries.all { it.agent != null })
        assertTrue(parsed.hasMultipleSingers)
        // Word sidecar still parsed and attached
        assertEquals(2, parsed.entries.first().words?.size)
    }

    @Test
    fun `three singers with rapid transitions`() {
        val lyrics = (1..12).joinToString("\n") { i ->
            val agent = when {
                i % 3 == 1 -> "v1"
                i % 3 == 2 -> "v2"
                else -> "v3"
            }
            "[00:${i.toString().padStart(2, '0')}.00]{agent:$agent}Line $i"
        }

        val parsed = LyricsUtils.parseLyricsWithSingers(lyrics)

        assertEquals(12, parsed.entries.size)
        assertEquals(listOf("v1", "v2", "v3"), parsed.entries.mapNotNull { it.agent }.distinct())
        assertTrue(parsed.hasMultipleSingers)
    }

    @Test
    fun `singer section start detection`() {
        val parsed = LyricsUtils.parseLyricsWithSingers(
            listOf(
                "[00:01.00]{agent:v1}A1",
                "[00:02.00]{agent:v1}A2",
                "[00:03.00]{agent:v2}B1",
                "[00:04.00]{bg}background",
                "[00:04.00]{agent:v1+v2}together",
            ).joinToString("\n")
        )
        val lines = listOf(LyricsEntry.HEAD_LYRICS_ENTRY) + parsed.entries

        assertTrue(isSingerSectionStart(lines, 1))   // first lead line after HEAD
        assertFalse(isSingerSectionStart(lines, 2))  // same singer continues
        assertTrue(isSingerSectionStart(lines, 3))   // singer switch
        assertFalse(isSingerSectionStart(lines, 4))  // background lines never start a section
        assertTrue(isSingerSectionStart(lines, 5))   // shared line; bg skipped when looking back
    }

    @Test
    fun `current line index tracks singer switches`() {
        val parsed = LyricsUtils.parseLyricsWithSingers(
            "[00:01.00]{agent:v1}A\n[00:02.00]{agent:v2}B\n[00:03.00]{agent:v1}C"
        )
        val lines = listOf(LyricsEntry.HEAD_LYRICS_ENTRY) + parsed.entries

        assertEquals(0, LyricsUtils.findCurrentLineIndex(lines, 700L))  // HEAD still active
        assertEquals(1, LyricsUtils.findCurrentLineIndex(lines, 1000L)) // first v1 line
        assertEquals(2, LyricsUtils.findCurrentLineIndex(lines, 2200L)) // v2 line
        assertEquals(3, LyricsUtils.findCurrentLineIndex(lines, 3300L)) // back to v1
    }

    @Test
    fun `lines without an agent inherit the previous singer`() {
        val parsed = LyricsUtils.parseLyricsWithSingers(
            "[00:01.00]{agent:v1}First\n[00:02.00]Continues\n[00:03.00]{agent:v2}Switch\n[00:04.00]Still two"
        )
        assertEquals(listOf("v1", "v1", "v2", "v2"), parsed.entries.map { it.agent })
        assertTrue(parsed.hasMultipleSingers)
    }

    @Test
    fun `rich sync lyrics keep agent metadata`() {
        val parsed = LyricsUtils.parseLyricsWithSingers(
            "[00:01.00]{agent:v1}<00:01.00>Hello <00:01.50>world\n" +
                "[00:03.00]{agent:v2}<00:03.00>Other <00:03.50>line"
        )

        assertEquals(listOf("v1", "v2"), parsed.entries.mapNotNull { it.agent })
        assertEquals("Hello world", parsed.entries[0].text)
        assertTrue(parsed.hasMultipleSingers)
    }
}
