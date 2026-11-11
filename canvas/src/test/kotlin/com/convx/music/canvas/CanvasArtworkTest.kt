package com.convx.music.canvas

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class CanvasArtworkTest {
    @Test
    fun artworkRoundTripsThroughTheExistingJsonSerializer() {
        val artwork = CanvasArtwork(
            name = "Song",
            artist = "Artist",
            albumId = "123",
            albumName = "Album",
            static = "https://example.test/static.jpg",
            animated = "https://example.test/animated.m3u8",
            videoUrl = "https://example.test/fallback.mp4",
        )

        assertEquals(artwork, Json.decodeFromString<CanvasArtwork>(Json.encodeToString(artwork)))
    }

    @Test
    fun preferredAnimationUsesTheVideoUrlWhenAnimatedUrlIsMissing() {
        val fallback = "https://example.test/fallback.mp4"

        assertEquals("https://example.test/animated.m3u8", CanvasArtwork(animated = "https://example.test/animated.m3u8", videoUrl = fallback).preferredAnimationUrl)
        assertEquals(fallback, CanvasArtwork(videoUrl = fallback).preferredAnimationUrl)
        assertEquals(null, CanvasArtwork().preferredAnimationUrl)
    }
}
