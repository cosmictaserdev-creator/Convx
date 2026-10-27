package com.convx.music.jam

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JamRecoveryBudgetTest {
    @Test
    fun failingItemStopsAfterThreeAutomaticRetries() {
        val budget = JamRecoveryBudget()
        assertEquals(1, budget.recordFailure("a", 0))
        assertEquals(2, budget.recordFailure("a", 1_000))
        assertEquals(3, budget.recordFailure("a", 3_000))
        assertNull(budget.recordFailure("a", 7_000))
        assertEquals(1, budget.recordFailure("b", 8_000))
    }

    @Test
    fun failuresAcrossTracksOpenACircuitThatManualTrackResetCannotBypass() {
        val budget = JamRecoveryBudget()
        repeat(7) { assertEquals(1, budget.recordFailure("track$it", it * 100L)) }
        assertNull(budget.recordFailure("track7", 700))
        budget.resetTrack("track7")
        assertNull(budget.recordFailure("track7", 1_000))
        assertTrue(budget.remainingBackoffMs(1_000) > 0)
        assertEquals(1, budget.recordFailure("track7", 60_701))
    }

    @Test
    fun oldFailuresDoNotAccumulateIntoAnOutage() {
        val budget = JamRecoveryBudget()
        repeat(20) { assertEquals(1, budget.recordFailure("track$it", it * 61_000L)) }
        assertEquals(0L, budget.remainingBackoffMs(2_000_000))
    }
}
