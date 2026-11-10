package com.convx.music.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DashManifestTest {
    private val timelineMpd = """<?xml version='1.0' encoding='UTF-8'?><MPD xmlns="urn:mpeg:dash:schema:mpd:2011" type="static" mediaPresentationDuration="PT3M20.045S"><Period id="0"><AdaptationSet id="0" contentType="audio" mimeType="audio/mp4"><Representation id="FLAC_HIRES,44100,24" codecs="flac" bandwidth="1613387" audioSamplingRate="44100"><SegmentTemplate timescale="44100" initialization="https://cdn.example/t/0.mp4?token=a~b&amp;info=x" media="https://cdn.example/t/${'$'}Number${'$'}.mp4?token=a~b&amp;info=x" startNumber="1"><SegmentTimeline><S d="176128" r="49"/><S d="15621"/></SegmentTimeline></SegmentTemplate></Representation></AdaptationSet></Period></MPD>"""

    @Test
    fun `timeline template expands to the init and every media segment`() {
        val parsed = DashManifest.parse("https://addon.example/m.mpd", timelineMpd)
        assertEquals("https://cdn.example/t/0.mp4?token=a~b&info=x", parsed.init)
        assertEquals(51, parsed.segments.size)
        assertEquals("https://cdn.example/t/1.mp4?token=a~b&info=x", parsed.segments.first())
        assertEquals("https://cdn.example/t/51.mp4?token=a~b&info=x", parsed.segments.last())
    }

    @Test
    fun `fixed duration template counts segments from the presentation length`() {
        val mpd = """<MPD mediaPresentationDuration="PT10S"><Period><AdaptationSet contentType="audio"><Representation id="a" bandwidth="1"><SegmentTemplate timescale="1000" duration="4000" initialization="i-${'$'}RepresentationID${'$'}.mp4" media="s-${'$'}Number%03d${'$'}.m4s" startNumber="5"/></Representation></AdaptationSet></Period></MPD>"""
        val parsed = DashManifest.parse("https://h.example/dir/m.mpd", mpd)
        assertEquals("https://h.example/dir/i-a.mp4", parsed.init)
        assertEquals(listOf("s-005.m4s", "s-006.m4s", "s-007.m4s").map { "https://h.example/dir/$it" }, parsed.segments)
    }

    @Test
    fun `the highest bandwidth representation is played`() {
        fun rep(id: String, bandwidth: Int) =
            """<Representation id="$id" bandwidth="$bandwidth"><SegmentTemplate timescale="1000" duration="1000" media="$id-${'$'}Number${'$'}.mp4"/></Representation>"""
        val mpd = """<MPD mediaPresentationDuration="PT1S"><Period><AdaptationSet contentType="audio">${rep("lo", 96_996)}${rep("flac", 752_286)}${rep("mid", 321_712)}</AdaptationSet></Period></MPD>"""
        assertEquals(listOf("https://h/flac-1.mp4"), DashManifest.parse("https://h/m.mpd", mpd).segments)
    }

    @Test
    fun `an encrypted manifest is refused`() {
        val mpd = """<MPD><Period><AdaptationSet contentType="audio"><ContentProtection schemeIdUri="x"/><Representation id="a"><SegmentTemplate media="s.mp4" duration="1"/></Representation></AdaptationSet></Period></MPD>"""
        assertTrue(runCatching { DashManifest.parse("https://h/m.mpd", mpd) }.isFailure)
    }

    @Test
    fun `only xml bodies that hold an MPD are detected`() {
        assertTrue(DashManifest.looksLikeMpd(timelineMpd))
        assertFalse(DashManifest.looksLikeMpd("#EXTM3U\n#EXT-X-VERSION:3"))
    }
}
