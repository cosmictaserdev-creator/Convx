package com.music.spotify

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream

class SpotifyCanvasTest {
    private val uri = "spotify:track:0VjIjW4GlUZAMYd2vXMi3b"

    @Test
    fun encodesTrackRequest() {
        val bytes = SpotifyCanvas.encodeRequest(listOf(uri))
        // outer field 1 (LD) wrapping inner field 1 (LD) = uri
        assertEquals(0x0A, bytes[0].toInt())
        assertEquals(uri.length + 2, bytes[1].toInt())
        assertEquals(0x0A, bytes[2].toInt())
        assertEquals(uri, String(bytes, 4, uri.length))
    }

    @Test
    fun decodesCanvasResponseAndRejectsForeignHosts() {
        fun ld(field: Int, data: ByteArray) = ByteArrayOutputStream().apply {
            write((field shl 3) or 2); write(data.size); write(data)
        }.toByteArray()
        fun canvas(url: String, u: String) = ld(1, ByteArrayOutputStream().apply {
            write(byteArrayOf(0x08, 0x01)) // varint field 1, skipped
            write(ld(2, url.toByteArray()))
            write(ld(5, u.toByteArray()))
        }.toByteArray())

        val good = "https://canvaz.scdn.co/upload/x.cnvs.mp4"
        val other = "spotify:track:1111111111111111111111"
        val payload = canvas(good, uri) + canvas("https://evil.example/x.mp4", other)
        assertEquals(mapOf(uri to good), SpotifyCanvas.decodeResponse(payload))
    }

    @Test
    fun matchesNormalizedTitleAndPrimaryArtist() {
        assertTrue(SpotifyCanvas.matchesSongIdentity("Blinding Lights (Official Video)", "The Weeknd", "Blinding Lights", "The Weeknd, Someone"))
        assertTrue(SpotifyCanvas.matchesSongIdentity("Café", "Beyoncé feat. X", "Cafe", "Beyonce"))
        assertFalse(SpotifyCanvas.matchesSongIdentity("Blinding Lights", "The Weeknd", "Save Your Tears", "The Weeknd"))
    }
}
