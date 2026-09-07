package com.convxy.music.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the pure geometry and gesture-decision maths behind
 * [LiquidGlassSlider].
 *
 * These are the parts that cannot be checked by eye on a CI machine but that
 * break the control completely when they are wrong: the thumb landing somewhere
 * other than the fill boundary, RTL scrubbing moving the playhead backwards, a
 * zero-duration track producing NaN, and a vertical sheet drag seeking.
 */
class LiquidGlassSliderTest {

    private val totalWidth = 1000f
    private val thumbWidth = 40f

    // A quarter of the thumb, matching the reference implementation's clamp.
    private val inset = 10f
    private val span = 980f

    @Test
    fun `rail inset is a quarter of the thumb width`() {
        assertEquals(10f, liquidRailInsetPx(40f), 0.0001f)
        assertEquals(6f, liquidRailInsetPx(24f), 0.0001f)
    }

    @Test
    fun `rail span removes both insets`() {
        assertEquals(span, liquidRailSpanPx(totalWidth, thumbWidth), 0.0001f)
    }

    @Test
    fun `rail span never goes negative on a control narrower than its thumb`() {
        assertEquals(0f, liquidRailSpanPx(12f, thumbWidth), 0.0001f)
        assertEquals(0f, liquidRailSpanPx(0f, thumbWidth), 0.0001f)
    }

    @Test
    fun `thumb centre sits on the rail start and end at the extremes`() {
        assertEquals(inset, liquidThumbCenterXPx(0f, totalWidth, thumbWidth, isLtr = true), 0.0001f)
        assertEquals(
            totalWidth - inset,
            liquidThumbCenterXPx(1f, totalWidth, thumbWidth, isLtr = true),
            0.0001f
        )
    }

    @Test
    fun `thumb centre tracks progress linearly`() {
        assertEquals(inset + span * 0.5f, liquidThumbCenterXPx(0.5f, totalWidth, thumbWidth, true), 0.0001f)
        assertEquals(inset + span * 0.25f, liquidThumbCenterXPx(0.25f, totalWidth, thumbWidth, true), 0.0001f)
    }

    @Test
    fun `thumb centre is clamped to the rail for out of range progress`() {
        assertEquals(inset, liquidThumbCenterXPx(-3f, totalWidth, thumbWidth, true), 0.0001f)
        assertEquals(totalWidth - inset, liquidThumbCenterXPx(9f, totalWidth, thumbWidth, true), 0.0001f)
    }

    @Test
    fun `thumb centre mirrors in rtl`() {
        val ltr = liquidThumbCenterXPx(0.3f, totalWidth, thumbWidth, isLtr = true)
        val rtl = liquidThumbCenterXPx(0.3f, totalWidth, thumbWidth, isLtr = false)
        assertEquals(totalWidth, ltr + rtl, 0.0001f)
    }

    @Test
    fun `translation x centres the thumb box on the rail position`() {
        assertEquals(
            liquidThumbCenterXPx(0.4f, totalWidth, thumbWidth, true) - thumbWidth / 2f,
            liquidThumbTranslationXPx(0.4f, totalWidth, thumbWidth, true),
            0.0001f
        )
    }

    @Test
    fun `value at x round trips against thumb centre`() {
        for (progress in floatArrayOf(0f, 0.1f, 0.37f, 0.5f, 0.83f, 1f)) {
            val x = liquidThumbCenterXPx(progress, totalWidth, thumbWidth, true)
            assertEquals(
                "ltr progress $progress",
                progress,
                liquidProgressAtXPx(x, totalWidth, thumbWidth, true),
                0.0001f
            )
            val xRtl = liquidThumbCenterXPx(progress, totalWidth, thumbWidth, false)
            assertEquals(
                "rtl progress $progress",
                progress,
                liquidProgressAtXPx(xRtl, totalWidth, thumbWidth, false),
                0.0001f
            )
        }
    }

    @Test
    fun `touching outside the rail clamps to the nearest end`() {
        assertEquals(0f, liquidProgressAtXPx(-50f, totalWidth, thumbWidth, true), 0.0001f)
        assertEquals(1f, liquidProgressAtXPx(totalWidth + 50f, totalWidth, thumbWidth, true), 0.0001f)
    }

    @Test
    fun `value at x maps into the caller range, not 0 to 1`() {
        // A 3 minute track in milliseconds.
        val v = liquidValueAtXPx(
            xPx = inset + span * 0.5f,
            totalWidthPx = totalWidth,
            thumbWidthPx = thumbWidth,
            rangeStart = 0f,
            rangeSpan = 180_000f,
            isLtr = true
        )
        assertEquals(90_000f, v, 1f)
    }

    @Test
    fun `value at x honours a non zero range start`() {
        val v = liquidValueAtXPx(
            xPx = inset,
            totalWidthPx = totalWidth,
            thumbWidthPx = thumbWidth,
            rangeStart = 1000f,
            rangeSpan = 5000f,
            isLtr = true
        )
        assertEquals(1000f, v, 0.0001f)
    }

    @Test
    fun `rtl scrubbing moves the value the other way`() {
        val left = liquidValueAtXPx(inset, totalWidth, thumbWidth, 0f, 100f, isLtr = false)
        val right = liquidValueAtXPx(totalWidth - inset, totalWidth, thumbWidth, 0f, 100f, isLtr = false)
        assertEquals(100f, left, 0.0001f)
        assertEquals(0f, right, 0.0001f)
    }

    @Test
    fun `progress is guarded against a zero range`() {
        assertEquals(0f, liquidSliderProgress(0f, 0f, 0f), 0.0001f)
        assertEquals(0f, liquidSliderProgress(1234f, 0f, 0f), 0.0001f)
    }

    @Test
    fun `progress is guarded against a zero width control`() {
        assertEquals(
            0f,
            liquidProgressAtXPx(0f, 0f, thumbWidth, true),
            0.0001f
        )
    }

    @Test
    fun `scrub arms only past slop and only when horizontal wins`() {
        val slop = 20f
        assertFalse(liquidScrubArmed(10f, 0f, slop))
        assertFalse(liquidScrubArmed(20f, 0f, slop))
        assertTrue(liquidScrubArmed(21f, 0f, slop))
        assertTrue(liquidScrubArmed(21f, 15f, slop))
        // The player sheet's own vertical drag: well past slop, but vertical.
        assertFalse(liquidScrubArmed(15f, 300f, slop))
        assertFalse(liquidScrubArmed(25f, 26f, slop))
    }

    @Test
    fun `a gesture counts as a tap only when it never armed and never moved`() {
        val slop = 20f
        assertTrue(liquidIsTap(armed = false, 0f, 0f, slop))
        assertTrue(liquidIsTap(armed = false, 5f, -8f, slop))
        // Armed means it scrubbed, so the value already moved: not a tap.
        assertFalse(liquidIsTap(armed = true, 0f, 0f, slop))
        // A vertical sheet drag must not be mistaken for a tap and seek.
        assertFalse(liquidIsTap(armed = false, 4f, 300f, slop))
        assertFalse(liquidIsTap(armed = false, 300f, 4f, slop))
    }

    @Test
    fun `blur scale is one at the shipped preference default`() {
        assertEquals(1f, liquidThumbBlurScale(LiquidGlassTokens.DefaultBlurRadiusDp), 0.0001f)
    }

    @Test
    fun `blur scale is zero when the user turns blur off and caps at one and a half`() {
        assertEquals(0f, liquidThumbBlurScale(0f), 0.0001f)
        assertEquals(1.5f, liquidThumbBlurScale(100f), 0.0001f)
        assertEquals(0.5f, liquidThumbBlurScale(1f), 0.0001f)
    }

    @Test
    fun `lens scales are one at the shipped preference defaults`() {
        assertEquals(1f, liquidThumbLensHeightScale(LiquidGlassTokens.DefaultLensHeight), 0.0001f)
        assertEquals(1f, liquidThumbLensAmountScale(LiquidGlassTokens.DefaultLensAmount), 0.0001f)
    }

    @Test
    fun `lens scales clamp instead of inverting at zero`() {
        assertEquals(0f, liquidThumbLensAmountScale(0f), 0.0001f)
        assertEquals(1.5f, liquidThumbLensAmountScale(5f), 0.0001f)
    }

    @Test
    fun `the thumb is the catalog's full capsule at rest and swells pressed`() {
        // The reference LiquidSlider runs DampedDragAnimation(initialScale = 1f,
        // pressedScale = 1.5f) over a 40x24dp thumb: a full capsule at rest, half
        // again as big under the finger. A port that instead shrank the thumb to a
        // 12dp dot at rest and morphed it out on press read as a squashed pill --
        // these numbers are the ones that keep it looking like the library's.
        assertEquals(1f, LiquidGlassTokens.SliderThumbInitialScale, 0.0001f)
        assertEquals(1.5f, LiquidGlassTokens.SliderThumbPressedScale, 0.0001f)
        assertEquals(40f, LiquidGlassTokens.SliderThumbSize.width.value, 0.0001f)
        assertEquals(24f, LiquidGlassTokens.SliderThumbSize.height.value, 0.0001f)
    }

    @Test
    fun `velocity stretch deforms along the drag axis and never inverts a scale`() {
        // No velocity: the factor is 1, so the thumb keeps its pressed swell.
        assertEquals(1f, liquidThumbVelocityFactor(0f, 0.75f), 0.0001f)
        // Dragging forward drops the factor below 1: X is divided by it, so the
        // thumb lengthens along the drag, and Y is multiplied by it, so it narrows
        // across. Dragging backwards does the opposite.
        assertTrue(liquidThumbVelocityFactor(100f, 0.75f) < 1f)
        assertTrue(liquidThumbVelocityFactor(-100f, 0.75f) > 1f)
        // Clamped: an absurd velocity cannot take the factor to zero or negative,
        // which would flip the thumb inside out.
        assertTrue(liquidThumbVelocityFactor(100000f, 0.75f) >= 0.8f)
        assertTrue(liquidThumbVelocityFactor(-100000f, 0.75f) <= 1.2f)
    }

    @Test
    fun `an empty backdrop is not a live glass backdrop`() {
        assertFalse(
            com.convxy.music.ui.component.backdrop.backdrops.emptyBackdrop()
                .isLiveGlassBackdrop()
        )
        assertFalse((null as com.convxy.music.ui.component.backdrop.Backdrop?).isLiveGlassBackdrop())
    }

    // region stepped sliders

    @Test
    fun `a continuous slider reports the value the finger landed on`() {
        assertEquals(0.375f, liquidSnapToStep(0.375f, 0f, 1f, 0), 0.0001f)
        assertEquals(7.3f, liquidSnapToStep(7.3f, 1f, 14f, -1), 0.0001f)
    }

    @Test
    fun `steps means detents between the endpoints, as in Material`() {
        // Four choices over 0f..3f is steps = 2: three detents plus both ends, so
        // a segment is span / (steps + 1) = 1. The grid-columns row in Appearance
        // settings is exactly this shape.
        assertEquals(0f, liquidSnapToStep(0.4f, 0f, 3f, 2), 0.0001f)
        assertEquals(1f, liquidSnapToStep(0.6f, 0f, 3f, 2), 0.0001f)
        assertEquals(1f, liquidSnapToStep(1.4f, 0f, 3f, 2), 0.0001f)
        assertEquals(2f, liquidSnapToStep(1.6f, 0f, 3f, 2), 0.0001f)
        assertEquals(3f, liquidSnapToStep(2.9f, 0f, 3f, 2), 0.0001f)
    }

    @Test
    fun `every detent is reachable and lands exactly on itself`() {
        val steps = 5
        val start = 2f
        val span = 10f
        val segment = span / (steps + 1)
        for (i in 0..steps + 1) {
            val detent = start + i * segment
            assertEquals(detent, liquidSnapToStep(detent, start, span, steps), 0.0001f)
        }
    }

    @Test
    fun `snapping never leaves the range`() {
        // A finger dragged past the end of the track asks for a value beyond it;
        // the detent it commits to must still be the endpoint.
        assertEquals(15f, liquidSnapToStep(900f, 1f, 14f, 14), 0.0001f)
        assertEquals(1f, liquidSnapToStep(-900f, 1f, 14f, 14), 0.0001f)
        for (v in listOf(-5f, 0f, 3f, 8f, 15f, 40f)) {
            val snapped = liquidSnapToStep(v, 1f, 14f, 14)
            assertTrue(snapped >= 1f && snapped <= 15f)
        }
    }

    @Test
    fun `snapping preserves direction of travel`() {
        // Dragging right must never move the value left, which a wrong rounding
        // mode would do on the way past a detent.
        val steps = 14
        var previous = Float.NEGATIVE_INFINITY
        var x = 0f
        while (x <= 1000f) {
            val snapped = liquidSnapToStep(1f + 14f * (x / 1000f), 1f, 14f, steps)
            assertTrue(snapped >= previous)
            previous = snapped
            x += 7f
        }
    }

    @Test
    fun `a fractional detent spacing still snaps to the nearest one`() {
        // Crossfade duration: 1f..15f with 14 steps is a segment of 14/15, not 1.
        val segment = 14f / 15f
        assertEquals(1f, liquidSnapToStep(1f + segment * 0.4f, 1f, 14f, 14), 0.0001f)
        assertEquals(1f + segment, liquidSnapToStep(1f + segment * 0.6f, 1f, 14f, 14), 0.0001f)
    }

    @Test
    fun `an empty range passes the value through instead of dividing by zero`() {
        // A slider whose duration is still unknown reports 0f..0f.
        assertEquals(0f, liquidSnapToStep(0f, 0f, 0f, 3), 0.0001f)
    }

    // endregion
}
