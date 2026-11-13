package com.convx.music.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals

class ArtworkSizeInterceptorTest {
    @Test
    fun albumArtworkRequestsEnoughPixelsForCardsAndHighDensityPlayer() {
        val original = "https://lh3.googleusercontent.com/album=w120-h120-l90-rj"
        assertEquals("https://lh3.googleusercontent.com/album=w384-h384-l90-rj", artworkUrlCandidates(original, 314).first())
        assertEquals("https://lh3.googleusercontent.com/album=w1200-h1200-l90-rj", artworkUrlCandidates(original, 1040).first())
        assertEquals(original, artworkUrlCandidates(original, 1040).last())
    }

    @Test
    fun avatarsPreserveTheirCropAndFormat() {
        val original = "https://yt3.ggpht.com/artist=s88-c-k-c0x00ffffff-no-rj"
        assertEquals("https://yt3.ggpht.com/artist=s384-c-k-c0x00ffffff-no-rj", artworkUrlCandidates(original, 260).first())
    }

    @Test
    fun missingHighResolutionVideoArtworkFallsBackThroughSmallerVersions() {
        val original = "https://i.ytimg.com/vi/video/hqdefault.jpg?token=preserved"
        assertEquals(listOf("maxresdefault", "sddefault", "hqdefault", "mqdefault", "default"),
            artworkUrlCandidates(original, 520).map { it.substringAfterLast('/').substringBefore('.') })
        assertEquals(true, artworkUrlCandidates(original, 520).all { it.endsWith("?token=preserved") })
        assertEquals("https://i.ytimg.com/vi/video/mqdefault.jpg?token=preserved", artworkUrlCandidates(original, 52).first())
    }

    @Test
    fun unsupportedAndUnmeasuredImagesAreUnchanged() {
        val original = "https://images.example.com/album=w120-h120.jpg"
        assertEquals(listOf(original), artworkUrlCandidates(original, 900))
        assertEquals(listOf(original), artworkUrlCandidates(original, 0))
        assertEquals(listOf("file:///music/cover.jpg"), artworkUrlCandidates("file:///music/cover.jpg", 520))
    }
}
