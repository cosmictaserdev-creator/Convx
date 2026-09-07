package com.convxy.music.lyrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [LyricsUtils.hasSingerMetadata], the cheap pre-parse check the
 * provider preference uses to keep searching for multi-singer lyrics.
 */
class HasSingerMetadataTest {

    @Test
    fun `plain LRC has no singer metadata`() {
        val lrc = """
            [00:05.02]Who's callin' my phone?
            [00:09.47]Is it Stacy?
        """.trimIndent()
        assertFalse(LyricsUtils.hasSingerMetadata(lrc))
    }

    @Test
    fun `singers registry header is detected`() {
        val lrc = "[singers:v1=Artist A|v2=Artist B|v1000=]\n[00:05.02]{agent:v1}Hello"
        assertTrue(LyricsUtils.hasSingerMetadata(lrc))
    }

    @Test
    fun `agent marker alone is detected`() {
        assertTrue(LyricsUtils.hasSingerMetadata("[00:05.02]{agent:v2}Hey"))
    }

    @Test
    fun `background marker alone is detected`() {
        assertTrue(LyricsUtils.hasSingerMetadata("[00:05.02]{bg}ooh"))
    }

    @Test
    fun `line-prefix dialect vN is not a false positive`() {
        // The vN: line-prefix dialect alone must not trigger the provider
        // preference — the header carries the registry for it.
        assertFalse(LyricsUtils.hasSingerMetadata("[00:05.02]v1: Hello"))
    }
}
