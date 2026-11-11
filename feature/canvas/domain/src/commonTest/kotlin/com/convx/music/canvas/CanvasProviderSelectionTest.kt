package com.convx.music.canvas

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CanvasProviderSelectionTest {
    @Test
    fun prefersHigherPriorityWithinWindow() = runTest {
        val winner = selectCanvasProvider(
            listOf({ delay(500); "first" }, { delay(20); "second" }),
            CanvasSelectionMode.PRIORITY,
        )
        assertEquals("first", winner)
    }

    @Test
    fun fallsBackAfterWindowWithoutWaitingForSlowProvider() = runTest {
        val winner = selectCanvasProvider(
            listOf({ delay(5_000); "first" }, { delay(20); "second" }),
            CanvasSelectionMode.PRIORITY,
        )
        assertEquals("second", winner)
        assertEquals(2_000, testScheduler.currentTime)
    }

    @Test
    fun firstAvailableDoesNotWaitForPriority() = runTest {
        val winner = selectCanvasProvider(
            listOf({ delay(500); "first" }, { delay(20); "second" }),
            CanvasSelectionMode.FIRST_AVAILABLE,
        )
        assertEquals("second", winner)
        assertEquals(20, testScheduler.currentTime)
    }

    @Test
    fun emptyProvidersHaveNoWinner() = runTest {
        assertNull(selectCanvasProvider<String>(emptyList(), CanvasSelectionMode.PRIORITY))
    }
}
