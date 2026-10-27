package com.convx.music.canvas

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AppleArtworkCommonTest {
    @Test
    fun sizesArtworkWithoutExceedingSourceDimensionsAndSupportsPng() {
        val artwork = AppleArtwork(
            url = "https://example.test/{w}x{h}/image.jpg",
            width = 1200,
            height = 800,
        )

        assertEquals("https://example.test/600x400/image.jpg", artwork.sized(600))
        assertEquals("https://example.test/1200x800/image.jpg", artwork.sized(2000))
        assertEquals("https://example.test/600x400/image.png", artwork.sized(600, png = true))
        assertEquals(artwork, Json.decodeFromString<AppleArtwork>(Json.encodeToString(artwork)))
    }

    @Test
    fun artistModelSerializesNestedArtworkAndOptionalCapabilities() {
        val artist = AppleArtist(
            id = "artist-1",
            name = "Artist",
            hero = AppleArtwork("https://example.test/hero.jpg"),
            logo = null,
            keyColor = 0x123456,
            motionVideoUrl = null,
        )

        assertEquals(artist, Json.decodeFromString<AppleArtist>(Json.encodeToString(artist)))
    }
}
