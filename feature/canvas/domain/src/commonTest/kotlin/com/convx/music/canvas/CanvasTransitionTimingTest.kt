package com.convx.music.canvas

import kotlin.test.Test
import kotlin.test.assertEquals

class CanvasTransitionTimingTest {
    @Test fun changingSurfaceRetainsProgressAndRemainingTime() {
        val timing = CanvasTransitionTiming(1_000, 600)
        assertEquals(0.5f, timing.progress(1_300))
        assertEquals(300L, timing.remainingMillis(1_300))
        assertEquals(1f, timing.progress(1_900))
        assertEquals(0L, timing.remainingMillis(1_900))
        assertEquals(0f, timing.progress(900))
    }
}
