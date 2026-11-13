package com.convx.music.playback

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class PlaybackQueuePolicyCommonTest {
    @Test
    fun emptyQueueHasNoSkipTarget() {
        assertNull(
            PlaybackQueuePolicy.nextIndex(
                currentIndex = -1,
                itemCount = 0,
                forward = true,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = true,
            ),
        )
    }

    @Test
    fun sequentialSkipHonorsQueueEdgesAndRepeatAll() {
        assertEquals(
            1,
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 0,
                itemCount = 3,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = false,
            ),
        )
        assertNull(
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 2,
                itemCount = 3,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = false,
            ),
        )
        assertEquals(
            0,
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 2,
                itemCount = 3,
                forward = true,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = false,
            ),
        )
        assertEquals(
            2,
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 0,
                itemCount = 3,
                forward = false,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = false,
            ),
        )
    }

    @Test
    fun shuffleDoesNotRepeatCurrentItemAndCanBeDeterministic() {
        val first = PlaybackQueuePolicy.nextIndex(
            currentIndex = 1,
            itemCount = 4,
            forward = true,
            repeatMode = PlaybackRepeatMode.OFF,
            shuffleEnabled = true,
            random = Random(42),
        )
        val second = PlaybackQueuePolicy.nextIndex(
            currentIndex = 1,
            itemCount = 4,
            forward = false,
            repeatMode = PlaybackRepeatMode.OFF,
            shuffleEnabled = true,
            random = Random(42),
        )

        assertEquals(first, second)
        assertFalse(first == 1)
    }

    @Test
    fun oneItemQueueOnlyRepeatsWhenRepeatAllIsEnabled() {
        assertNull(
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 0,
                itemCount = 1,
                forward = true,
                repeatMode = PlaybackRepeatMode.OFF,
                shuffleEnabled = true,
            ),
        )
        assertEquals(
            0,
            PlaybackQueuePolicy.nextIndex(
                currentIndex = 0,
                itemCount = 1,
                forward = true,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = true,
            ),
        )
    }
}
