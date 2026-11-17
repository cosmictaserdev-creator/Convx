package com.convx.music.playback

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaybackQueueNavigatorCommonTest {
    @Test
    fun shuffleCycleVisitsEveryQueueItemBeforeStopping() {
        val navigator = PlaybackQueueNavigator(Random(17))
        navigator.setShuffleEnabled(enabled = true, itemCount = 5, currentIndex = 0)
        val visited = mutableListOf(0)
        var currentIndex = 0

        repeat(4) {
            currentIndex = navigator.nextIndex(
                currentIndex = currentIndex,
                itemCount = 5,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = true,
            ) ?: error("Shuffle order ended before visiting every track")
            visited += currentIndex
        }

        assertEquals(5, visited.distinct().size)
        assertNull(
            navigator.nextIndex(
                currentIndex = currentIndex,
                itemCount = 5,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = true,
            ),
        )
    }

    @Test
    fun previousRetracesTheSameShuffleOrder() {
        val navigator = PlaybackQueueNavigator(Random(31))
        navigator.setShuffleEnabled(enabled = true, itemCount = 6, currentIndex = 2)
        val first = navigator.nextIndex(2, 6, forward = true, PlaybackRepeatMode.OFF, shuffleEnabled = true)!!
        val second = navigator.nextIndex(first, 6, forward = true, PlaybackRepeatMode.OFF, shuffleEnabled = true)!!

        assertEquals(first, navigator.nextIndex(second, 6, forward = false, PlaybackRepeatMode.OFF, shuffleEnabled = true))
    }

    @Test
    fun repeatAllWrapsAcrossTheStableShuffleOrder() {
        val navigator = PlaybackQueueNavigator(Random(59))
        navigator.setShuffleEnabled(enabled = true, itemCount = 4, currentIndex = 1)
        var currentIndex = 1
        repeat(3) {
            currentIndex = navigator.nextIndex(
                currentIndex,
                4,
                forward = true,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = true,
            )!!
        }

        assertEquals(1, navigator.nextIndex(currentIndex, 4, true, PlaybackRepeatMode.ALL, true))
    }

    @Test
    fun appendRemoveAndMoveKeepShuffleOrderAsAPermutation() {
        val navigator = PlaybackQueueNavigator(Random(73))
        navigator.setShuffleEnabled(enabled = true, itemCount = 4, currentIndex = 0)
        navigator.append(previousCount = 4, addedCount = 2, shuffleEnabled = true)
        navigator.move(fromIndex = 1, toIndex = 4, itemCount = 6, shuffleEnabled = true)
        navigator.remove(index = 2, newItemCount = 5, shuffleEnabled = true)

        val visited = mutableSetOf(0)
        var currentIndex = 0
        repeat(4) {
            currentIndex = navigator.nextIndex(
                currentIndex,
                itemCount = 5,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = true,
            ) ?: error("Shuffle order ended before visiting every retained track")
            visited += currentIndex
        }

        assertEquals(setOf(0, 1, 2, 3, 4), visited)
        assertTrue(visited.all { it in 0 until 5 })
    }
}
