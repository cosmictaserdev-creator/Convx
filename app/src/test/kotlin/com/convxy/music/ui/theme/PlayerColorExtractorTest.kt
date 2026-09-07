package com.convxy.music.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the guard that keeps greyscale artwork greyscale. The bug it exists for:
 * a black and white cover picked up whatever few degrees of colour cast its JPEG
 * carried, and the saturation boost downstream turned that into a decisive tint.
 */
class PlayerColorExtractorTest {

    @Test
    fun `pure greyscale artwork is achromatic`() {
        val achromatic = PlayerColorExtractor.isAchromatic(
            saturations = floatArrayOf(0f, 0f, 0f),
            values = floatArrayOf(0.2f, 0.5f, 0.8f),
            populations = intArrayOf(500, 900, 300),
        )
        assertTrue(achromatic)
    }

    @Test
    fun `faint jpeg colour cast still counts as greyscale`() {
        // The real failure mode: everything slightly off-neutral, nothing saturated.
        val achromatic = PlayerColorExtractor.isAchromatic(
            saturations = floatArrayOf(0.06f, 0.04f, 0.09f),
            values = floatArrayOf(0.3f, 0.5f, 0.7f),
            populations = intArrayOf(800, 1200, 400),
        )
        assertTrue(achromatic)
    }

    @Test
    fun `a genuinely coloured cover is not achromatic`() {
        val achromatic = PlayerColorExtractor.isAchromatic(
            saturations = floatArrayOf(0.72f, 0.55f, 0.1f),
            values = floatArrayOf(0.5f, 0.6f, 0.4f),
            populations = intArrayOf(1000, 600, 200),
        )
        assertFalse(achromatic)
    }

    @Test
    fun `a few stray saturated pixels do not colour a monochrome cover`() {
        // Palette will hand back a "vibrant" swatch built from a handful of pixels.
        // Population weighting is what stops that swatch deciding the whole tint.
        val achromatic = PlayerColorExtractor.isAchromatic(
            saturations = floatArrayOf(0.02f, 0.03f, 0.95f),
            values = floatArrayOf(0.45f, 0.55f, 0.5f),
            populations = intArrayOf(9000, 7000, 40),
        )
        assertTrue(achromatic)
    }

    @Test
    fun `near-black and near-white swatches do not decide the outcome`() {
        // Erratic hue at the value extremes must be weighted out: these carry huge
        // populations and nonsense saturation, the mid-tone is the real signal.
        val achromatic = PlayerColorExtractor.isAchromatic(
            saturations = floatArrayOf(0.9f, 0.85f, 0.7f),
            values = floatArrayOf(0.01f, 0.99f, 0.5f),
            populations = intArrayOf(5000, 5000, 300),
        )
        assertFalse(achromatic)
    }

    @Test
    fun `empty palette counts as achromatic`() {
        assertTrue(
            PlayerColorExtractor.isAchromatic(FloatArray(0), FloatArray(0), IntArray(0))
        )
    }
}
