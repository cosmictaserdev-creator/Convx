package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class UpcomingCachePlanTest {
    @Test fun stagesOpeningsBeforeCompletingTracks() {
        assertEquals(
            listOf(UpcomingCacheWork("b", 3), UpcomingCacheWork("c", 3), UpcomingCacheWork("d", 3),
                UpcomingCacheWork("b", 1), UpcomingCacheWork("c", 1), UpcomingCacheWork("d", 1)),
            upcomingCachePlan("a", listOf("a", "b", "b", "c", "d", "e")),
        )
    }

    @Test fun targetRoundsUpAndRejectsUnknownLengths() {
        assertEquals(4L, upcomingCacheTarget(10, 3))
        assertEquals(Long.MAX_VALUE, upcomingCacheTarget(Long.MAX_VALUE, 1))
        assertNull(upcomingCacheTarget(-1, 3))
        assertNull(upcomingCacheTarget(10, 0))
    }

    @Test fun retriesInterruptedWorkBeforeMovingToTheNextStage() = runTest {
        val plan = upcomingCachePlan("a", listOf("b", "c"))
        val attempts = mutableListOf<UpcomingCacheWork>()
        var readyChecks = 0
        runUpcomingCachePlan(plan, isRelevant = { true }, isReady = { ++readyChecks > 1 }, cache = { work ->
            attempts += work
            attempts.size != 1
        }, retryDelayMillis = 1)
        assertEquals(listOf(plan[0], plan[0], plan[1], plan[2], plan[3]), attempts)
        assertEquals(6, readyChecks)
    }

    @Test fun stopsWhenTheQueueSnapshotIsNoLongerRelevant() = runTest {
        var relevant = true
        val attempts = mutableListOf<UpcomingCacheWork>()
        runUpcomingCachePlan(upcomingCachePlan("a", listOf("b", "c")), { relevant }, { true }, { work ->
            attempts += work
            relevant = false
            true
        }, retryDelayMillis = 1)
        assertEquals(listOf(UpcomingCacheWork("b", 3)), attempts)
    }

    @Test fun doesNotStartAStaleTransferAfterReadinessChangesTheQueue() = runTest {
        var relevant = true
        var started = false
        runUpcomingCachePlan(listOf(UpcomingCacheWork("b", 3)), { relevant }, {
            relevant = false
            true
        }, {
            started = true
            true
        }, retryDelayMillis = 1)
        assertEquals(false, started)
    }
}
