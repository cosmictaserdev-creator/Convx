package com.convx.music.playback

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackCheckpointWriterTest {
    @Test
    fun flushWaitsForAnInFlightWriteAndPersistsTheFinalCheckpoint() = runTest {
        val repository = BlockingCheckpointRepository()
        val writer = PlaybackCheckpointWriter(backgroundScope, repository)
        val initial = checkpoint("initial", positionMs = 2_000L)
        val final = checkpoint("final", positionMs = 17_000L)

        writer.save(initial, force = true)
        runCurrent()
        assertTrue(repository.initialWriteStarted.isCompleted)

        val flush = async { writer.flush(final) }
        runCurrent()
        assertFalse(flush.isCompleted)
        assertFalse(repository.finalCheckpoint.isCompleted)

        repository.allowInitialWriteToFinish.complete(Unit)
        flush.await()

        assertEquals(final.normalized(), repository.finalCheckpoint.await())
    }

    private fun checkpoint(id: String, positionMs: Long) = PlaybackCheckpoint(
        queue = listOf(
            PlaybackRequestTrack(
                providerId = "test",
                id = id,
                title = id,
                artists = listOf("Test Artist"),
                durationMs = 30_000L,
            ),
        ),
        currentIndex = 0,
        positionMs = positionMs,
    )

    private class BlockingCheckpointRepository : PlaybackCheckpointRepository {
        val initialWriteStarted = CompletableDeferred<Unit>()
        val allowInitialWriteToFinish = CompletableDeferred<Unit>()
        val finalCheckpoint = CompletableDeferred<PlaybackCheckpoint>()

        override fun observeCheckpoint(): Flow<PlaybackCheckpoint> = emptyFlow()

        override suspend fun getCheckpoint(): PlaybackCheckpoint = PlaybackCheckpoint()

        override suspend fun saveCheckpoint(checkpoint: PlaybackCheckpoint) {
            when (checkpoint.queue.singleOrNull()?.id) {
                "initial" -> {
                    initialWriteStarted.complete(Unit)
                    allowInitialWriteToFinish.await()
                }

                "final" -> finalCheckpoint.complete(checkpoint)
            }
        }
    }
}
