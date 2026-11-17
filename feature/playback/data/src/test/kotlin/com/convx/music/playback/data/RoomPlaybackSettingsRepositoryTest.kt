package com.convx.music.playback.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.playback.DjDelayType
import com.convx.music.playback.DjReverbType
import com.convx.music.playback.EqualizerBand
import com.convx.music.playback.EqualizerProfile
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackTransitionMode
import com.convx.music.playback.PlaybackCheckpoint
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.PlaybackQueueOrigin
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoomPlaybackSettingsRepositoryTest {
    @Test
    fun defaultSettingsCanBeUpdatedAndObserved() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackSettingsRepository(database.playbackSettingsDao())
            assertEquals(PlaybackSettings(), repository.observeSettings().first())

            val updated = repository.updateSettings {
                it.copy(
                    crossfadeEnabled = true,
                    autoMixEnabled = true,
                    crossfadeDurationMs = 7_500L,
                    creativeTransitionsEnabled = true,
                    reverbType = DjReverbType.PLATE,
                    delayType = DjDelayType.PING_PONG,
                    equalizerEnabled = true,
                    equalizerProfile = EqualizerProfile(
                        preamp = -2.5,
                        bands = listOf(EqualizerBand(frequency = 1_000.0, gain = 3.0)),
                    ),
                )
            }

            assertEquals(PlaybackTransitionMode.AUTO_MIX, updated.transitionMode)
            assertEquals(updated, repository.getSettings())
            assertEquals(updated, repository.observeSettings().first())
        } finally {
            database.close()
        }
    }

    @Test
    fun corruptStoredSettingsRecoverToDefaultsOnNextUpdate() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackSettingsRepository(database.playbackSettingsDao())
            database.playbackSettingsDao().upsert(PlaybackSettingsRecord(settingsJson = "{"))
            assertEquals(PlaybackSettings(), repository.getSettings())

            val recovered = repository.updateSettings { it.copy(crossfadeEnabled = true) }
            assertEquals(PlaybackTransitionMode.CROSSFADE, recovered.transitionMode)
            assertEquals(recovered, repository.getSettings())
        } finally {
            database.close()
        }
    }

    @Test
    fun checkpointRoundTripsQueueAndNormalizesInvalidSelection() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackCheckpointRepository(database.playbackCheckpointDao())
            val checkpoint = PlaybackCheckpoint(
                queue = listOf(
                    PlaybackRequestTrack(
                        providerId = "youtube_music",
                        id = "track-1",
                        title = "One",
                        artists = listOf("Artist"),
                        durationMs = 200_000L,
                        artworkUrl = "https://example.test/cover.jpg",
                        queueOrigin = PlaybackQueueOrigin.AUTO_MIX,
                    ),
                ),
                currentIndex = 9,
                positionMs = 42_000L,
                repeatMode = PlaybackRepeatMode.ALL,
                shuffleEnabled = true,
            )

            repository.saveCheckpoint(checkpoint)

            assertEquals(checkpoint.copy(currentIndex = -1, positionMs = 0L), repository.getCheckpoint())
            assertEquals(repository.getCheckpoint(), repository.observeCheckpoint().first())
        } finally {
            database.close()
        }
    }

    @Test
    fun cancelledCheckpointSaveDoesNotWrite() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackCheckpointRepository(database.playbackCheckpointDao())
            val checkpoint = PlaybackCheckpoint(
                queue = listOf(
                    PlaybackRequestTrack(
                        providerId = "youtube_music",
                        id = "track-1",
                        title = "One",
                        artists = emptyList(),
                    ),
                ),
                currentIndex = 0,
                positionMs = 1_000L,
            )
            val save = launch(start = CoroutineStart.LAZY) { repository.saveCheckpoint(checkpoint) }

            save.cancel()
            save.join()

            assertEquals(PlaybackCheckpoint(), repository.getCheckpoint())
        } finally {
            database.close()
        }
    }

    @Test
    fun corruptStoredCheckpointRecoversToEmptyCheckpoint() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackCheckpointRepository(database.playbackCheckpointDao())
            database.playbackCheckpointDao().upsert(PlaybackCheckpointRecord(checkpointJson = "{"))

            assertEquals(PlaybackCheckpoint(), repository.getCheckpoint())
        } finally {
            database.close()
        }
    }

    private fun inMemoryDatabase(): PlaybackSettingsDatabase =
        Room.inMemoryDatabaseBuilder<PlaybackSettingsDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
}
