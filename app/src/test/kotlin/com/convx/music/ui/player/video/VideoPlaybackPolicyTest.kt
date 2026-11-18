package com.convx.music.ui.player.video

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VideoPlaybackPolicyTest {
    @Test
    fun driftInsideDeadZoneKeepsAudioSpeed() {
        assertEquals(1.25f, synchronizedVideoSpeed(1.25f, -80), 0f)
        assertEquals(1.25f, synchronizedVideoSpeed(1.25f, 0), 0f)
        assertEquals(1.25f, synchronizedVideoSpeed(1.25f, 80), 0f)
    }

    @Test
    fun driftCorrectionScalesLinearlyFromAudioSpeed() {
        assertEquals(1.25f * 0.985f, synchronizedVideoSpeed(1.25f, 640), 0.0001f)
        assertEquals(1.25f * 1.015f, synchronizedVideoSpeed(1.25f, -640), 0.0001f)
        assertEquals(0.97f, synchronizedVideoSpeed(1f, Long.MAX_VALUE), 0.0001f)
        assertEquals(1.03f, synchronizedVideoSpeed(1f, Long.MIN_VALUE), 0.0001f)
    }

    @Test
    fun seeksOnlyBeyondTheSyncThreshold() {
        assertFalse(shouldSeekVideo(-1_200))
        assertFalse(shouldSeekVideo(1_200))
        assertTrue(shouldSeekVideo(-1_201))
        assertTrue(shouldSeekVideo(1_201))
        assertTrue(shouldSeekVideo(Long.MIN_VALUE))
        assertTrue(shouldSeekVideo(Long.MAX_VALUE))
    }

    @Test
    fun symmetricNearBlackBordersZoomIntoThePicture() {
        assertEquals(24f / 18f, zoomForFrame(top = 3, bottom = 3), 0.0001f)
        assertEquals(24f / 20f, zoomForFrame(top = 2, bottom = 3), 0.0001f)
        assertEquals(1.35f, zoomForFrame(top = 4, bottom = 4), 0f)
    }

    @Test
    fun rejectsAsymmetricBordersAndFramesWithoutPictureContrast() {
        assertEquals(1f, zoomForFrame(top = 0, bottom = 0), 0f)
        assertEquals(1f, zoomForFrame(top = 0, bottom = 3), 0f)
        assertEquals(1f, zoomForFrame(top = 2, bottom = 4), 0f)
        assertEquals(1f, zoomForFrame(top = 3, bottom = 3, picture = 0xff191919.toInt()), 0f)
        assertEquals(1f, zoomForFrame(top = 3, bottom = 3, picture = 0xff000000.toInt()), 0f)
    }

    @Test
    fun rejectsInvalidSamples() {
        assertEquals(1f, letterboxCropZoom(IntArray(0), 0, 24), 0f)
        assertEquals(1f, letterboxCropZoom(IntArray(32), Int.MAX_VALUE, Int.MAX_VALUE), 0f)
        assertEquals(1f, letterboxCropZoom(IntArray(8 * 16 - 1), 8, 16), 0f)
        assertEquals(1f, letterboxCropZoom(IntArray(24 * 24), 24, 24), 0f)
    }

    private fun zoomForFrame(top: Int, bottom: Int, picture: Int = 0xff458ad2.toInt()): Float {
        val width = 24
        val height = 24
        val pixels = IntArray(width * height) { index ->
            val row = index / width
            if (row < top || row >= height - bottom) 0xff141414.toInt() else picture
        }
        return letterboxCropZoom(pixels, width, height)
    }
}
