package com.convx.music.playback

import org.junit.Assert.assertEquals
import org.junit.Test

class CacheWriteBudgetTest {
    @Test
    fun prefetchLeavesSpaceForPlaybackWithoutEvicting() {
        var evictions = 0
        val budget = CacheWriteBudget(0, { AutomaticCachePolicy(limitBytes = 100, evictWhenFull = true) }, { 0 }) { _, _ -> evictions++ }
        val prefetch = budget.open("next")
        assertEquals(75, budget.reserve(prefetch, 100, prefetch = true))
        val anotherPrefetch = budget.open("next-two")
        assertEquals(0, budget.reserve(anotherPrefetch, 10, prefetch = true))
        assertEquals(0, evictions)
        val playback = budget.open("current")
        assertEquals(25, budget.reserve(playback, 100))
        budget.close(prefetch)
        budget.close(anotherPrefetch)
        budget.close(playback)
    }
    @Test
    fun concurrentWritersCannotReserveTheSameSpace() {
        val budget = CacheWriteBudget(0, { AutomaticCachePolicy(limitBytes = 10) }, { 0 })
        val first = budget.open("one")
        val second = budget.open("two")
        assertEquals(8, budget.reserve(first, 8))
        assertEquals(2, budget.reserve(second, 8))
        budget.onAdded("one", 0, 8)
        assertEquals(0, budget.reserve(second, 1))
        budget.close(second)
        assertEquals(2, budget.reserve(first, 2))
        budget.close(first)
    }

    @Test
    fun artworkUsageIsIncludedAndRemovedSpansFreeSpace() {
        val budget = CacheWriteBudget(5, { AutomaticCachePolicy(limitBytes = 10) }, { 3 })
        val writer = budget.open("song")
        assertEquals(2, budget.reserve(writer, 5))
        budget.onAdded("song", 0, 2)
        budget.onRemoved(4)
        assertEquals(4, budget.reserve(writer, 5))
        budget.close(writer)
    }

    @Test
    fun committedRangesReleaseOnlyTheirOwnWriter() {
        val budget = CacheWriteBudget(0, { AutomaticCachePolicy(limitBytes = 10) }, { 0 })
        val later = budget.open("song", 5)
        val earlier = budget.open("song", 0)
        assertEquals(5, budget.reserve(later, 5))
        assertEquals(5, budget.reserve(earlier, 5))
        budget.onAdded("song", 0, 5)
        budget.close(earlier)
        assertEquals(0, budget.reserve(later, 1))
        budget.onAdded("song", 5, 5)
        budget.close(later)
    }
}
