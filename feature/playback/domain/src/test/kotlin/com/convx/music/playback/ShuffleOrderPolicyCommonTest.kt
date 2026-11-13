package com.convx.music.playback

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShuffleOrderPolicyCommonTest {
    @Test
    fun keepsTheCurrentItemFirstAndReturnsEveryQueueIndexOnce() {
        val order = ShuffleOrderPolicy.create(
            currentIndex = 4,
            totalCount = 7,
            originalQueueSize = 3,
            shufflePlaylistFirst = false,
            random = Random(202),
        )

        assertEquals(4, order.first())
        assertEquals((0 until 7).toSet(), order.toSet())
    }

    @Test
    fun keepsOriginalQueueIndicesAheadOfAddedIndices() {
        val order = ShuffleOrderPolicy.create(
            currentIndex = 5,
            totalCount = 7,
            originalQueueSize = 4,
            shufflePlaylistFirst = true,
            random = Random(303),
        )

        assertEquals(5, order.first())
        assertEquals(setOf(0, 1, 2, 3), order.drop(1).filter { it < 4 }.toSet())
        assertEquals(setOf(4, 6), order.drop(1).filter { it >= 4 }.toSet())
        assertTrue(order.drop(1).indexOfLast { it < 4 } < order.drop(1).indexOfFirst { it >= 4 })
    }
}
