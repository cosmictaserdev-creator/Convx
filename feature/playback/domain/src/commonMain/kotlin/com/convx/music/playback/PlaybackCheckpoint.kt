package com.convx.music.playback

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

/** Restorable player context. Playback intent is deliberately omitted so a restart never starts audio. */
@Serializable
data class PlaybackCheckpoint(
    val queue: List<PlaybackRequestTrack> = emptyList(),
    val currentIndex: Int = -1,
    val positionMs: Long = 0L,
    val repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.OFF,
    val shuffleEnabled: Boolean = false,
) {
    fun normalized(): PlaybackCheckpoint {
        val safeQueue = queue.take(MAX_QUEUE_SIZE)
        val safeIndex = currentIndex.takeIf { it in safeQueue.indices } ?: -1
        val selectedDuration = safeQueue.getOrNull(safeIndex)?.durationMs?.takeIf { it > 0L }
        return copy(
            queue = safeQueue,
            currentIndex = safeIndex,
            positionMs = if (safeIndex >= 0) {
                positionMs.coerceAtLeast(0L).let { position -> selectedDuration?.let(position::coerceAtMost) ?: position }
            } else {
                0L
            },
        )
    }

    companion object {
        const val MAX_QUEUE_SIZE = 10_000
    }
}

/** Persistence port for the selected queue and playhead shared by KMP client adapters. */
interface PlaybackCheckpointRepository {
    fun observeCheckpoint(): Flow<PlaybackCheckpoint>

    suspend fun getCheckpoint(): PlaybackCheckpoint

    suspend fun saveCheckpoint(checkpoint: PlaybackCheckpoint)
}
