package com.convx.music.canvas

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class CanvasArtworkCommonTest {
    @Test
    fun serializesArtworkAndPrefersAnimatedArtworkThenVideoFallback() {
        val artwork = CanvasArtwork(
            name = "Song",
            artist = "Artist",
            animated = "https://example.test/animated.m3u8",
            videoUrl = "https://example.test/fallback.mp4",
        )

        assertEquals(artwork, Json.decodeFromString<CanvasArtwork>(Json.encodeToString(artwork)))
        assertEquals("https://example.test/animated.m3u8", artwork.preferredAnimationUrl)
        assertEquals("https://example.test/fallback.mp4", artwork.copy(animated = null).preferredAnimationUrl)
    }
}
