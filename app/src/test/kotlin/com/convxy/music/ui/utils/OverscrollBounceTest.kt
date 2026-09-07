package com.convxy.music.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the bounce tuning shared by the plain-list overscroll and the hero-header
 * pull. Both used to carry their own unrelated numbers -- the hero screens sprang
 * back ~2.5x faster than every other scroller and seeded no velocity at all.
 */
class OverscrollBounceTest {

    @Test
    fun `full leftover velocity reaches the spring`() {
        // Was scaled to 0.4 of the flick, which read as a muted nudge.
        assertEquals(3000f, bounceSeedVelocity(3000f), 0.001f)
        assertEquals(-3000f, bounceSeedVelocity(-3000f), 0.001f)
    }

    @Test
    fun `the seed clamp sits clear of the high-velocity threshold`() {
        // A 4000f ceiling clamped every fling below the threshold that selects the
        // softer arc, so the adaptive period below could never engage.
        assertTrue(MaxBounceVelocity > HighVelocityThreshold)
        assertEquals(MaxBounceVelocity, bounceSeedVelocity(50_000f), 0.001f)
        assertEquals(-MaxBounceVelocity, bounceSeedVelocity(-50_000f), 0.001f)
    }

    @Test
    fun `a hard flick settles on the longer softer arc`() {
        assertEquals(BounceSpringStiffness, bounceStiffnessFor(1000f), 0.001f)
        assertEquals(HighVelocityBounceStiffness, bounceStiffnessFor(6000f), 0.001f)
        assertEquals(HighVelocityBounceStiffness, bounceStiffnessFor(-6000f), 0.001f)
        assertTrue(HighVelocityBounceStiffness < BounceSpringStiffness)
    }

    @Test
    fun `stiffnesses match the Miuix natural periods they are derived from`() {
        // stiffness = (2*pi/period)^2, periods 0.4s and 0.55s.
        fun stiffnessFor(period: Float) = ((2 * Math.PI / period) * (2 * Math.PI / period)).toFloat()
        assertEquals(stiffnessFor(0.4f), BounceSpringStiffness, 1f)
        assertEquals(stiffnessFor(0.55f), HighVelocityBounceStiffness, 1f)
    }

    @Test
    fun `rubber band starts near one to one and self-limits`() {
        val viewport = 2000f
        // Immediate near the edge -- iOS never feels mushy on the first few px.
        assertEquals(10f, rubberBand(10f, viewport), 1f)
        // Asymptotes toward dim/c rather than slamming into a hard cap.
        val far = rubberBand(100_000f, viewport)
        assertTrue(far < viewport / 0.55f)
        assertTrue(far > viewport)
        // Symmetric.
        assertEquals(-rubberBand(500f, viewport), rubberBand(-500f, viewport), 0.001f)
    }
}
