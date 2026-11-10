package com.convx.music.client.desktop

import com.convx.music.playback.*
import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import kotlin.test.*

class DesktopPlaybackModesTest {
    @Test fun shuffleUsesItsOwnOrderForSkipAvailability() = runBlocking {
        val queue = List(5) { PlaybackRequestTrack("local", "$it", "Track $it", emptyList()) }
        val controller = controller(PlaybackCheckpoint(queue, currentIndex = queue.lastIndex))
        try {
            withTimeout(5_000) { controller.playbackSnapshot.first { it.track != null } }
            assertFalse(controller.playbackSnapshot.value.canSkipNext)
            controller.toggleShuffle()
            assertTrue(controller.playbackSnapshot.value.shuffleEnabled)
            assertTrue(controller.playbackSnapshot.value.canSkipNext, "The final original item starts a new shuffle cycle")
            assertFalse(controller.playbackSnapshot.value.canSkipPrevious)
            controller.toggleShuffle()
            assertFalse(controller.playbackSnapshot.value.canSkipNext)
            controller.cycleRepeatMode()
            assertEquals(PlaybackRepeatMode.ALL, controller.playbackSnapshot.value.repeatMode)
            assertTrue(controller.playbackSnapshot.value.canSkipNext)
            controller.cycleRepeatMode()
            assertEquals(PlaybackRepeatMode.ONE, controller.playbackSnapshot.value.repeatMode)
            controller.cycleRepeatMode()
            assertEquals(PlaybackRepeatMode.OFF, controller.playbackSnapshot.value.repeatMode)
        } finally { controller.close() }
    }

    @Test fun playbackModesRemainObservableWithoutAnActiveTrack() {
        val controller = controller(PlaybackCheckpoint())
        try {
            controller.setRepeatMode(PlaybackRepeatMode.ONE)
            controller.toggleShuffle()
            assertEquals(PlaybackRepeatMode.ONE, controller.playbackSnapshot.value.repeatMode)
            assertTrue(controller.playbackSnapshot.value.shuffleEnabled)
            controller.cycleRepeatMode()
            controller.toggleShuffle()
            assertEquals(PlaybackRepeatMode.OFF, controller.playbackSnapshot.value.repeatMode)
            assertFalse(controller.playbackSnapshot.value.shuffleEnabled)
        } finally { controller.close() }
    }

    private fun controller(checkpoint: PlaybackCheckpoint): DesktopPlaybackController = DesktopPlaybackController(
        streamResolver = object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> = error("These commands must not resolve audio")
        },
        playbackSettingsRepository = FixedPlaybackSettings(PlaybackSettings()),
        playbackCheckpointRepository = object : PlaybackCheckpointRepository {
            val state = MutableStateFlow(checkpoint)
            override fun observeCheckpoint() = state
            override suspend fun getCheckpoint() = state.value
            override suspend fun saveCheckpoint(checkpoint: PlaybackCheckpoint) { state.value = checkpoint }
        },
        trackAnalysisRepository = object : PlaybackTrackAnalysisRepository {
            override suspend fun get(trackIdentity: String): TrackAnalysis? = null
            override suspend fun save(trackIdentity: String, analysis: TrackAnalysis) = Unit
            override suspend fun remove(trackIdentity: String) = Unit
        },
    )
}
