package com.convxy.music.ui.player

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Guards the rung-by-rung walk. The bug this replaces jumped maxresdefault straight to
 * hqdefault, so the common case (maxresdefault missing, which is most tracks) landed on
 * 480x360 for a ~1200px player artwork.
 */
class ThumbnailFallbackTest {

    private val base = "https://i.ytimg.com/vi/abc123/"

    @Test
    fun `walks down one rung at a time`() {
        var url: String? = base + "maxresdefault.jpg"
        url = nextThumbnailFallback(url)
        assertEquals(base + "sddefault.jpg", url)
        url = nextThumbnailFallback(url)
        assertEquals(base + "hqdefault.jpg", url)
        url = nextThumbnailFallback(url)
        assertEquals(base + "mqdefault.jpg", url)
    }

    @Test
    fun `stops at the bottom rung instead of looping`() {
        assertNull(nextThumbnailFallback(base + "mqdefault.jpg"))
    }

    @Test
    fun `leaves non-ladder urls alone`() {
        // googleusercontent sizes are a parameter, not a filename ladder — retrying a
        // different rung there would just request the same image again.
        assertNull(nextThumbnailFallback("https://lh3.googleusercontent.com/x=w1200-h1200-p-l90-rj"))
        assertNull(nextThumbnailFallback(null))
    }
}
