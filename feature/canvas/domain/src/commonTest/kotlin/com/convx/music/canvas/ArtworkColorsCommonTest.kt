package com.convx.music.canvas

import kotlin.test.*

class ArtworkColorsCommonTest {
    @Test fun darkArtworkKeepsItsHueAndBrightArtworkRemainsReadable() {
        assertEquals(0x201020, ArtworkColors.pageBackground(0x201020))
        val bright = ArtworkColors.pageBackground(0xF0C090)
        assertTrue(ArtworkColors.luminance(bright) < ArtworkColors.luminance(0xF0C090))
        assertTrue(ArtworkColors.red(bright) > ArtworkColors.blue(bright))
    }

    @Test fun fallbackSamplesArtworkTopEdgeRatherThanForegroundSubject() {
        val pixels = IntArray(64 * 64) { if (it / 64 < 16) 0x286060 else 0xD0A080 }
        assertEquals(0x286060, ArtworkColors.estimateDirect(pixels, 64, 64))
    }
}
