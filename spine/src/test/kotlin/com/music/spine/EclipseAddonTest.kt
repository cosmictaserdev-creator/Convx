package com.music.spine

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EclipseAddonTest {
    private val json = Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false }

    @Test fun forwardsSettingsNegotiatesQualityAndPreservesTokenPath() {
        val manifest = EclipseAddon.moduleManifest(json, """{"id":"addon","settings":[{"key":"country","default":"IN"},{"key":"quality","default":"low","options":[{"value":"low"},{"value":"high"},{"value":"flac"}]}]}""")
        val parameters = EclipseAddon.parameters(manifest, mapOf("quality" to "LOSSLESS", "dolbyAtmos" to "true"))
        assertEquals("IN", parameters["country"])
        assertEquals("flac", parameters["quality"])
        assertEquals("auto", parameters["atmos"])
        val url = EclipseAddon.streamUrl("https://addon.example/token", "track/a?b", parameters)
        assertTrue(url.startsWith("https://addon.example/token/stream/track%2Fa%3Fb?"))
        assertTrue(url.contains("quality=flac"))
    }

    @Test fun acceptsDecimalMetadataInlineStreamsAndTransportAliases() {
        val search = EclipseAddon.parseSearch(json, """{"tracks":[{"id":"t","duration":213.5,"albumArtworkURL":"cover","streamURL":"https://cdn/audio"}]}""", 10)
        assertEquals(213, search.tracks.single().duration)
        assertEquals("cover", search.tracks.single().albumCover)
        assertEquals("https://cdn/audio", search.tracks.single().streamUrl)
        val stream = EclipseAddon.parseStream(json, "t", """{"url":"https://cdn/opaque","format":"DASH","fileCodec":"flac","bitDepth":24.0,"sampleRate":96000.0,"audioModes":["STEREO"]}""")
        assertEquals("dash", stream.streamType)
        assertEquals("flac", stream.track?.codec)
        assertEquals(24, stream.track?.bitDepth)
    }

    @Test
    fun manifestBecomesOneModuleWithTokenisedBase() {
        val body = """{"id":"com.x.addon","name":"X","version":"1.0.0","resources":["search","stream"],"types":["track"]}"""
        assertTrue(EclipseAddon.isManifest(json, body))
        assertFalse(EclipseAddon.isManifest(json, """{"category:modules":[]}"""))
        val m = EclipseAddon.toModule(json, "https://a.io/tok123/manifest.json", body)
        assertEquals(EclipseAddon.TYPE, m.type)
        assertEquals("https://a.io/tok123", m.download)
        assertEquals("https://a.io/tok123/search?q=a%20b%26c", EclipseAddon.searchUrl(m.download, "a b&c"))
    }

    @Test
    fun searchAndStreamMapOntoModuleContract() {
        val s = EclipseAddon.parseSearch(json, """{"tracks":[{"id":"t1","title":"T","artist":"A","duration":240,"artworkURL":"u","format":"flac"}],"albums":[]}""", 8)
        assertEquals("t1", s.tracks.single().id)
        assertEquals(240, s.tracks.single().duration)
        assertEquals("u", s.tracks.single().albumCover)
        val st = EclipseAddon.parseStream(json, "t1", """{"url":"https://cdn/x.m3u8","codec":"flac","manifest":"hls","expiresAt":99}""")
        assertEquals("https://cdn/x.m3u8", st.streamUrl)
        assertEquals("hls", st.streamType)
        assertEquals("flac", st.track?.codec)
        assertEquals(99L, st.expiresAt)
        assertEquals(null, EclipseAddon.parseStream(json, "t1", """{"url":"u","manifest":"none"}""").streamType)
        assertTrue(EclipseAddon.isAllowedUrl("http://10.0.2.2:7000/manifest.json"))
        assertFalse(EclipseAddon.isAllowedUrl("http://evil.com/manifest.json"))
    }
}
