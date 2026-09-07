package com.convxy.music.ui.screens.ambient

import com.convxy.music.constants.AmbientCanvasAnchorSide
import com.convxy.music.constants.AmbientCanvasFitMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Ambient Mode's Canvas Position & Fit is all arithmetic: how wide the side panel has to be
 * for a portrait canvas to fit uncropped, which edge it sits on, and where the asymmetric
 * veil lands. Each of those is a plain function, so the behaviour worth pinning is the
 * numbers — in particular that a landscape canvas is left completely alone.
 */
class AmbientCanvasPositionFitTest {

    private val screen16x9 = 16f / 9f
    private val portrait9x16 = 9f / 16f
    private val portrait3x4 = 3f / 4f
    private val requestedWidth = AmbientCanvasFitDefaults.SideWidth

    @Test
    fun `fit hugs a nine by sixteen canvas instead of leaving dead space in the panel`() {
        val fraction = ambientCanvasPanelFraction(
            videoAspect = portrait9x16,
            screenAspect = screen16x9,
            requestedFraction = requestedWidth,
            fitMode = AmbientCanvasFitMode.FIT,
        )
        // At full screen height a 9:16 frame only needs 9/16 of the height in width, which
        // is ~32% of a 16:9 screen — well inside the 48% the user asked for.
        assertEquals(portrait9x16 / screen16x9, fraction, 0.001f)
        assertTrue("a portrait canvas must not claim the whole screen", fraction < 1f)
    }

    @Test
    fun `fit never makes the panel wider than the requested share`() {
        val square = ambientCanvasPanelFraction(
            videoAspect = 1.1f,
            screenAspect = screen16x9,
            requestedFraction = 0.30f,
            fitMode = AmbientCanvasFitMode.FIT,
        )
        // A 1.1:1 canvas would want ~62% of the width to hug it; the user's cap wins.
        assertEquals(0.30f, square, 0.001f)
    }

    @Test
    fun `fill and stretch keep the configured panel width`() {
        listOf(AmbientCanvasFitMode.ZOOM, AmbientCanvasFitMode.STRETCH).forEach { mode ->
            val fraction = ambientCanvasPanelFraction(
                videoAspect = portrait3x4,
                screenAspect = screen16x9,
                requestedFraction = requestedWidth,
                fitMode = mode,
            )
            assertEquals("$mode", requestedWidth, fraction, 0.001f)
        }
    }

    @Test
    fun `a landscape canvas keeps the default full-width background`() {
        listOf(1.15f, 4f / 3f, 16f / 9f, 2f).forEach { aspect ->
            val fraction = ambientCanvasPanelFraction(
                videoAspect = aspect,
                screenAspect = screen16x9,
                requestedFraction = requestedWidth,
                fitMode = AmbientCanvasFitMode.FIT,
            )
            assertEquals("$aspect", 1f, fraction, 0.001f)
            assertFalse("$aspect", ambientCanvasUsesSidePanel(fraction))
        }
    }

    @Test
    fun `portrait canvases are the ones that get a side panel`() {
        listOf(portrait9x16, portrait3x4, 1f).forEach { aspect ->
            val fraction = ambientCanvasPanelFraction(
                videoAspect = aspect,
                screenAspect = screen16x9,
                requestedFraction = requestedWidth,
                fitMode = AmbientCanvasFitMode.FIT,
            )
            assertTrue("$aspect should be panelled", ambientCanvasUsesSidePanel(fraction))
        }
    }

    @Test
    fun `an unknown canvas size falls back to the requested share`() {
        val fraction = ambientCanvasPanelFraction(
            videoAspect = 0f,
            screenAspect = screen16x9,
            requestedFraction = requestedWidth,
            fitMode = AmbientCanvasFitMode.FIT,
        )
        assertEquals(requestedWidth, fraction, 0.001f)
    }

    @Test
    fun `requested widths are clamped into the drawable range`() {
        assertEquals(0.85f, ambientCanvasPanelFraction(0.5f, screen16x9, 0.95f, AmbientCanvasFitMode.ZOOM), 0.001f)
        assertEquals(0.2f, ambientCanvasPanelFraction(0.5f, screen16x9, 0.05f, AmbientCanvasFitMode.ZOOM), 0.001f)
    }

    @Test
    fun `auto puts the canvas on the artwork side of the layout`() {
        // Ambient Mode lays artwork then lyrics out, so AUTO follows the layout direction.
        assertFalse(ambientCanvasAnchoredRight(AmbientCanvasAnchorSide.AUTO, isRtl = false))
        assertTrue(ambientCanvasAnchoredRight(AmbientCanvasAnchorSide.AUTO, isRtl = true))
        // An explicit choice is never overridden by direction.
        assertFalse(ambientCanvasAnchoredRight(AmbientCanvasAnchorSide.LEFT, isRtl = true))
        assertTrue(ambientCanvasAnchoredRight(AmbientCanvasAnchorSide.RIGHT, isRtl = false))
    }

    @Test
    fun `the canvas side carries the dim plus its own gradient and the far side stays lighter`() {
        val (near, far) = ambientCanvasVeilAlphas(dim = 0.42f, sideGradient = 0.35f, farVeil = 0.10f)
        assertEquals(0.77f, near, 0.001f)
        assertEquals(0.10f, far, 0.001f)
        assertTrue("the lyrics side has to be the lighter one", far < near)

        // The strong end can never be pushed to solid black, and the far end can never
        // out-run it, however the sliders are set.
        val (cappedNear, cappedFar) = ambientCanvasVeilAlphas(dim = 0.75f, sideGradient = 0.8f, farVeil = 0.6f)
        assertEquals(0.94f, cappedNear, 0.001f)
        assertEquals(0.6f, cappedFar, 0.001f)

        val (invertedNear, invertedFar) = ambientCanvasVeilAlphas(dim = 0.1f, sideGradient = 0f, farVeil = 0.6f)
        assertEquals(invertedNear, invertedFar, 0.001f)
    }

    @Test
    fun `veil stops run left to right and decay from the strong edge`() {
        val stops = ambientCanvasVeilStops(nearAlpha = 0.8f, farAlpha = 0.1f, spread = 0.6f, anchoredRight = false)
        assertTrue(
            "positions must ascend for a gradient brush",
            stops.zipWithNext().all { (a, b) -> b.first > a.first },
        )
        assertEquals(0f, stops.first().first, 0.001f)
        assertEquals(1f, stops.last().first, 0.001f)
        assertEquals(0.8f, stops.first().second, 0.001f)
        assertEquals(0.1f, stops.last().second, 0.001f)
        assertTrue(
            "the veil must only get lighter away from the canvas",
            stops.zipWithNext().all { (a, b) -> b.second <= a.second + 0.0001f },
        )
    }

    @Test
    fun `mirroring keeps the stronger end on the right without flipping the axis`() {
        val left = ambientCanvasVeilStops(0.8f, 0.1f, 0.6f, anchoredRight = false)
        val right = ambientCanvasVeilStops(0.8f, 0.1f, 0.6f, anchoredRight = true)

        assertTrue(right.zipWithNext().all { (a, b) -> b.first > a.first })
        assertEquals(0.1f, right.first().second, 0.001f)
        assertEquals(0.8f, right.last().second, 0.001f)
        // Same profile, read from the other edge.
        left.forEachIndexed { index, (position, alpha) ->
            val mirror = right[right.lastIndex - index]
            assertEquals(1f - position, mirror.first, 0.001f)
            assertEquals(alpha, mirror.second, 0.001f)
        }
    }

    @Test
    fun `the spread decides where the veil finishes`() {
        // Reaching only 20% of the way across, the veil has already settled by mid-screen.
        val tight = ambientCanvasVeilStops(0.8f, 0.1f, 0.2f, anchoredRight = false)
        assertEquals(0.1f, tight.single { it.first == 0.5f }.second, 0.001f)

        // A full-width spread is still fading at mid-screen.
        val wide = ambientCanvasVeilStops(0.8f, 0.1f, 1f, anchoredRight = false)
        val mid = wide.single { it.first == 0.5f }.second
        assertTrue("mid-screen ($mid) should still be darker than the floor", mid > 0.1f + 0.05f)
    }
}
