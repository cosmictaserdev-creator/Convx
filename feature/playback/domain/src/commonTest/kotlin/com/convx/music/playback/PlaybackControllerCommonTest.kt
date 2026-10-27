package com.convx.music.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class PlaybackControllerCommonTest {
    @Test
    fun repeatCommandCyclesThroughSharedModes() {
        val controller = FakePlaybackController()

        controller.cycleRepeatMode()
        assertEquals(PlaybackRepeatMode.ALL, controller.playbackSnapshot.value.repeatMode)

        controller.cycleRepeatMode()
        assertEquals(PlaybackRepeatMode.ONE, controller.playbackSnapshot.value.repeatMode)

        controller.cycleRepeatMode()
        assertEquals(PlaybackRepeatMode.OFF, controller.playbackSnapshot.value.repeatMode)
    }

    @Test
    fun shuffleCommandTogglesFromCurrentSnapshot() {
        val controller = FakePlaybackController()

        controller.toggleShuffle()
        assertEquals(true, controller.playbackSnapshot.value.shuffleEnabled)

        controller.toggleShuffle()
        assertEquals(false, controller.playbackSnapshot.value.shuffleEnabled)
    }

    @Test
    fun playingAQueueContextReplacesItsTracksAndStartsTheSelectedIndex() {
        val controller = FakePlaybackController()
        val tracks = listOf(
            PlaybackRequestTrack("youtube_music", "first", "First", listOf("Artist")),
            PlaybackRequestTrack("youtube_music", "second", "Second", listOf("Artist")),
        )

        controller.playQueue(tracks, startIndex = 1)

        assertEquals(tracks, controller.queuedTracks)
        assertEquals(1, controller.lastPlayedQueueIndex)
    }

    private class FakePlaybackController : PlaybackController {
        private val mutableSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY)
        override val playbackSnapshot: StateFlow<PlaybackSnapshot> = mutableSnapshot
        private val mutableQueueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
        override val queueSnapshot: StateFlow<PlaybackQueueSnapshot> = mutableQueueSnapshot
        var queuedTracks = emptyList<PlaybackRequestTrack>()
            private set
        var lastPlayedQueueIndex = -1
            private set

        override fun updateQueue(tracks: List<PlaybackRequestTrack>) {
            queuedTracks = tracks
        }

        override fun playQueueItem(index: Int) {
            lastPlayedQueueIndex = index
        }

        override fun moveQueueItem(fromIndex: Int, toIndex: Int) = Unit

        override fun removeQueueItem(index: Int) = Unit

        override fun removeQueueItemsAfterCurrent() = Unit

        override fun togglePlayPause() = Unit

        override fun play() = Unit

        override fun pause() = Unit

        override fun seekTo(positionMs: Long) = Unit

        override fun requestSkip(forward: Boolean) = Unit

        override fun setRepeatMode(mode: PlaybackRepeatMode) {
            mutableSnapshot.value = mutableSnapshot.value.copy(repeatMode = mode)
        }

        override fun setShuffleEnabled(enabled: Boolean) {
            mutableSnapshot.value = mutableSnapshot.value.copy(shuffleEnabled = enabled)
        }
    }
}
