package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals

class PlaybackQueueControllerCommonTest {
    @Test
    fun movingAnItemPreservesTheQueueOrderAndMembership() {
        val order = listOf(0, 2, 1, 3)

        assertEquals(listOf(0, 1, 3, 2), moveQueueIndices(order, fromIndex = 1, toIndex = 3))
        assertEquals(listOf(0, 2, 1, 3), order)
    }

    @Test
    fun invalidOrUnchangedMovesKeepTheExistingOrder() {
        val order = listOf(0, 1, 2)

        assertEquals(order, moveQueueIndices(order, fromIndex = -1, toIndex = 2))
        assertEquals(order, moveQueueIndices(order, fromIndex = 0, toIndex = 3))
        assertEquals(order, moveQueueIndices(order, fromIndex = 1, toIndex = 1))
    }
}
