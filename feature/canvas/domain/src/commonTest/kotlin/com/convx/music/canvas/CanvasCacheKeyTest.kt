package com.convx.music.canvas

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CanvasCacheKeyTest {
    @Test
    fun changesInSignaturesDoNotDuplicateVideo() {
        assertEquals(canvasCacheKey("song", "https://cdn.example/a.mp4?token=old"), canvasCacheKey("song", "https://cdn.example/a.mp4?token=new"))
    }

    @Test
    fun differentVideosAndRenditionsNeverShareTheSameKey() {
        assertNotEquals(canvasCacheKey("song", "https://cdn.example/a.mp4"), canvasCacheKey("song", "https://cdn.example/b.mp4"))
        assertNotEquals(canvasCacheKey("song", "https://cdn.example/videoplayback?id=a&itag=1"), canvasCacheKey("song", "https://cdn.example/videoplayback?id=b&itag=1"))
        assertNotEquals(canvasCacheKey("song", "https://cdn.example/video?variant=low"), canvasCacheKey("song", "https://cdn.example/video?variant=high"))
        assertNotEquals(canvasCacheKey("song", "https://cdn.example/a.mp4"), canvasCacheKey("other", "https://cdn.example/a.mp4"))
    }
}
