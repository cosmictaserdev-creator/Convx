package com.convx.music.ui.player.backdrop

import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerBackdropMotionTest {
    private val stripe = IntArray(16) { if (it in 5..8) 180 else 20 }
    private val flat = IntArray(16) { 60 }

    @Test
    fun downwardChangeUsesHistory() {
        assertEquals(1f, verticalFlowConfidence(stripe, shifted(stripe, 1), flat, flat), 0.001f)
    }

    @Test
    fun horizontalChangeDoesNotUseHistoryInEitherDirection() {
        assertEquals(0f, verticalFlowConfidence(flat, flat, stripe, shifted(stripe, 1)), 0.001f)
        assertEquals(0f, verticalFlowConfidence(flat, flat, stripe, shifted(stripe, -1)), 0.001f)
    }

    @Test
    fun upwardChangeAndStaticFramesDoNotUseHistory() {
        assertEquals(0f, verticalFlowConfidence(stripe, shifted(stripe, -1), flat, flat), 0.001f)
        assertEquals(0f, verticalFlowConfidence(stripe, stripe, stripe, stripe), 0.001f)
    }

    @Test
    fun mixedChangeBlendsLiveAndDelayedFrames() {
        assertEquals(
            0.5f,
            verticalFlowConfidence(stripe, shifted(stripe, 1), stripe, shifted(stripe, -1)),
            0.001f,
        )
    }

    private fun shifted(profile: IntArray, offset: Int): IntArray =
        IntArray(profile.size) { profile[(it - offset).coerceIn(profile.indices)] }
}
