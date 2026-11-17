package com.convx.music.playback

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CrossfadeEnvelopeCommonTest {
    @Test
    fun gainsAreEqualPowerAcrossTheFade() {
        val incoming = CrossfadeEnvelope.incomingGain(0.5f)
        val outgoing = CrossfadeEnvelope.outgoingGain(0.5f)

        assertTrue(abs(incoming * incoming + outgoing * outgoing - 1f) < 0.00001f)
    }

    @Test
    fun gainsReachTheirEndpointsAndClampProgress() {
        assertGain(0f, CrossfadeEnvelope.incomingGain(-1f))
        assertGain(1f, CrossfadeEnvelope.outgoingGain(-1f))
        assertGain(1f, CrossfadeEnvelope.incomingGain(2f))
        assertGain(0f, CrossfadeEnvelope.outgoingGain(2f))
    }

    @Test
    fun durationIsCappedToOneThirdOfKnownOutgoingTrackLength() {
        assertEquals(10_000L, CrossfadeEnvelope.durationMs(12_000L, 30_000L))
        assertEquals(5_000L, CrossfadeEnvelope.durationMs(5_000L, 30_000L))
        assertEquals(12_000L, CrossfadeEnvelope.durationMs(12_000L, -1L))
        assertEquals(0L, CrossfadeEnvelope.durationMs(-1L, 30_000L))
    }

    @Test
    fun progressClampsNegativeElapsedTimeAndCompletesForInvalidDuration() {
        assertEquals(0f, CrossfadeEnvelope.progress(-100L, 1_000L))
        assertEquals(0.5f, CrossfadeEnvelope.progress(500L, 1_000L))
        assertEquals(1f, CrossfadeEnvelope.progress(1_500L, 1_000L))
        assertEquals(1f, CrossfadeEnvelope.progress(0L, 0L))
    }

    private fun assertGain(expected: Float, actual: Float) {
        assertTrue(abs(expected - actual) < 0.00001f, "Expected gain $expected, got $actual")
    }
}
