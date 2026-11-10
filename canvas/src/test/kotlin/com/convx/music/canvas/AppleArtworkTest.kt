package com.convx.music.canvas

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class AppleArtworkTest {
    @Test
    fun artworkSizingAndNestedArtistSerializationPreserveTheModel() {
        val artwork = AppleArtwork(
            url = "https://example.test/{w}x{h}/image.jpg",
            width = 1200,
            height = 800,
        )
        val artist = AppleArtist("artist-1", "Artist", artwork, null, 0x123456, null)

        assertEquals("https://example.test/600x400/image.jpg", artwork.sized(600))
        assertEquals("https://example.test/600x400/image.png", artwork.sized(600, png = true))
        assertEquals(artist, Json.decodeFromString<AppleArtist>(Json.encodeToString(artist)))
    }
}
