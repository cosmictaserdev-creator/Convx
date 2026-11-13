package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaybackSnapshotCommonTest {
    @Test
    fun emptySnapshotHasNoCurrentTrackOrQueueItem() {
        val snapshot = PlaybackSnapshot.EMPTY

        assertNull(snapshot.track)
        assertEquals(PlaybackPhase.IDLE, snapshot.phase)
        assertFalse(snapshot.playWhenReady)
        assertFalse(snapshot.isPlaying)
        assertEquals(0L, snapshot.positionMs)
        assertNull(snapshot.durationMs)
        assertEquals(-1, snapshot.mediaItemIndex)
        assertEquals(0, snapshot.mediaItemCount)
        assertEquals(PlaybackRepeatMode.OFF, snapshot.repeatMode)
        assertFalse(snapshot.shuffleEnabled)
        assertFalse(snapshot.canSkipPrevious)
        assertFalse(snapshot.canSkipNext)
    }

    @Test
    fun snapshotCarriesPortableTrackAndQueueState() {
        val snapshot = PlaybackSnapshot(
            track = PlaybackTrackSnapshot("track-1", "Song", listOf("Artist")),
            phase = PlaybackPhase.READY,
            playWhenReady = true,
            isPlaying = true,
            positionMs = 1_250L,
            durationMs = 180_000L,
            mediaItemIndex = 2,
            mediaItemCount = 5,
            repeatMode = PlaybackRepeatMode.ALL,
            shuffleEnabled = true,
            canSkipPrevious = true,
            canSkipNext = false,
        )

        assertEquals("track-1", snapshot.track?.id)
        assertEquals(listOf("Artist"), snapshot.track?.artists)
        assertTrue(snapshot.isPlaying)
        assertEquals(1_250L, snapshot.positionMs)
        assertEquals(2, snapshot.mediaItemIndex)
        assertEquals(5, snapshot.mediaItemCount)
        assertTrue(snapshot.shuffleEnabled)
        assertTrue(snapshot.canSkipPrevious)
    }

    @Test
    fun repeatModeCyclesOffAllOneOff() {
        assertEquals(PlaybackRepeatMode.ALL, PlaybackRepeatMode.OFF.next())
        assertEquals(PlaybackRepeatMode.ONE, PlaybackRepeatMode.ALL.next())
        assertEquals(PlaybackRepeatMode.OFF, PlaybackRepeatMode.ONE.next())
    }

    @Test
    fun toggleUsesPlaybackIntentWhileBuffering() {
        val starting = PlaybackSnapshot.EMPTY.copy(
            phase = PlaybackPhase.BUFFERING,
            playWhenReady = true,
        )
        val pausedBeforeReady = starting.copy(playWhenReady = false)

        assertTrue(starting.shouldPauseOnToggle())
        assertFalse(pausedBeforeReady.shouldPauseOnToggle())
    }

    @Test
    fun toggleUsesActualPlaybackOnceReady() {
        val waitingForAudioDevice = PlaybackSnapshot.EMPTY.copy(
            phase = PlaybackPhase.READY,
            playWhenReady = true,
            isPlaying = false,
        )

        assertFalse(waitingForAudioDevice.shouldPauseOnToggle())
        assertTrue(waitingForAudioDevice.copy(isPlaying = true).shouldPauseOnToggle())
    }
}
