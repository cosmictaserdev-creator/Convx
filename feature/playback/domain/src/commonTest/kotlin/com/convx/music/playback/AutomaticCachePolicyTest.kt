package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals

class AutomaticCachePolicyTest {
    @Test
    fun reserveProtectsPlaybackWithoutCappingIt() {
        val policy = AutomaticCachePolicy(limitBytes = 1_000, playbackReservePercent = 25)
        assertEquals(750L, policy.availablePrefetchBytes(usedBytes = 0, prefetchBytes = 0))
        assertEquals(0L, policy.availablePrefetchBytes(usedBytes = 750, prefetchBytes = 750))
        assertEquals(250L, policy.availableBytes(usedBytes = 750))
        assertEquals(50L, policy.availablePrefetchBytes(usedBytes = 950, prefetchBytes = 200))
    }

    @Test
    fun accountsForInFlightWritesWithoutOverflow() {
        val policy = AutomaticCachePolicy(limitBytes = Long.MAX_VALUE)
        assertEquals(0L, policy.availableBytes(Long.MAX_VALUE - 2, 10))
        assertEquals(0L, policy.availableBytes(Long.MAX_VALUE, Long.MAX_VALUE))
        assertEquals(Long.MAX_VALUE, policy.availableBytes(0))
    }
}
