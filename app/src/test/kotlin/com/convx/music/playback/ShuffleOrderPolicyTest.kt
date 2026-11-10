package com.convx.music.playback

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShuffleOrderPolicyTest {
    @Test
    fun keepsTheCurrentItemFirstInAFullPermutation() {
        val totalCount = 8
        val currentIndex = 5
        val order = ShuffleOrderPolicy.create(
            currentIndex = currentIndex,
            totalCount = totalCount,
            originalQueueSize = 3,
            shufflePlaylistFirst = false,
            random = Random(45),
        )

        assertEquals(currentIndex, order.first())
        assertPermutation(order, totalCount)
    }

    @Test
    fun keepsOriginalPlaylistItemsBeforeAddedItemsWhenCurrentIsOriginal() {
        val totalCount = 7
        val originalQueueSize = 4
        val currentIndex = 1
        val order = ShuffleOrderPolicy.create(
            currentIndex = currentIndex,
            totalCount = totalCount,
            originalQueueSize = originalQueueSize,
            shufflePlaylistFirst = true,
            random = Random(72),
        )

        assertEquals(currentIndex, order.first())
        assertPlaylistSegments(order, totalCount, originalQueueSize, setOf(0, 2, 3), setOf(4, 5, 6))
    }

    @Test
    fun keepsOriginalPlaylistItemsBeforeRemainingAdditionsWhenCurrentIsAdded() {
        val totalCount = 7
        val originalQueueSize = 4
        val currentIndex = 5
        val order = ShuffleOrderPolicy.create(
            currentIndex = currentIndex,
            totalCount = totalCount,
            originalQueueSize = originalQueueSize,
            shufflePlaylistFirst = true,
            random = Random(103),
        )

        assertEquals(currentIndex, order.first())
        assertPlaylistSegments(order, totalCount, originalQueueSize, setOf(0, 1, 2, 3), setOf(4, 6))
    }

    @Test
    fun fallsBackToOnePermutationWhenThereIsNoOriginalAndAddedSplit() {
        val totalCount = 5
        val currentIndex = 2
        val order = ShuffleOrderPolicy.create(
            currentIndex = currentIndex,
            totalCount = totalCount,
            originalQueueSize = totalCount,
            shufflePlaylistFirst = true,
            random = Random(14),
        )

        assertEquals(currentIndex, order.first())
        assertPermutation(order, totalCount)
    }

    @Test
    fun emptyQueueProducesAnEmptyOrder() {
        assertTrue(
            ShuffleOrderPolicy.create(
                currentIndex = 0,
                totalCount = 0,
                originalQueueSize = 0,
                shufflePlaylistFirst = true,
                random = Random(9),
            ).isEmpty(),
        )
    }

    private fun assertPlaylistSegments(
        order: IntArray,
        totalCount: Int,
        originalQueueSize: Int,
        expectedOriginal: Set<Int>,
        expectedAdded: Set<Int>,
    ) {
        assertPermutation(order, totalCount)
        val remaining = order.drop(1)
        assertEquals(expectedOriginal, remaining.filter { it < originalQueueSize }.toSet())
        assertEquals(expectedAdded, remaining.filter { it >= originalQueueSize }.toSet())
        val lastOriginalPosition = remaining.indexOfLast { it < originalQueueSize }
        val firstAddedPosition = remaining.indexOfFirst { it >= originalQueueSize }
        assertTrue(lastOriginalPosition < firstAddedPosition)
    }

    private fun assertPermutation(order: IntArray, totalCount: Int) {
        assertEquals(totalCount, order.size)
        assertEquals((0 until totalCount).toSet(), order.toSet())
    }
}
