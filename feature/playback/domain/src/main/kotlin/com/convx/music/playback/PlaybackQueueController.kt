package com.convx.music.playback

import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

data class PlaybackQueueEntry(
    val id: String,
    val title: String,
    val artists: List<String>,
    val artworkUrl: String? = null,
    val providerId: String = "youtube_music",
    val durationMs: Long? = null,
    val origin: PlaybackQueueOrigin = PlaybackQueueOrigin.USER,
)

data class PlaybackQueueSnapshot(
    val entries: List<PlaybackQueueEntry>,
    val currentIndex: Int,
) {
    companion object {
        val EMPTY = PlaybackQueueSnapshot(entries = emptyList(), currentIndex = -1)
    }
}

/** Portable queue-edit commands implemented by each platform playback adapter. */
interface PlaybackQueueController {
    val queueSnapshot: StateFlow<PlaybackQueueSnapshot>

    fun playQueueItem(index: Int)

    fun moveQueueItem(fromIndex: Int, toIndex: Int)

    fun removeQueueItem(index: Int)

    fun removeQueueItemsAfterCurrent()
}

/** Shared next/previous selection for client-owned playback queues. */
object PlaybackQueuePolicy {
    fun nextIndex(
        currentIndex: Int,
        itemCount: Int,
        forward: Boolean,
        repeatMode: PlaybackRepeatMode,
        shuffleEnabled: Boolean,
        random: Random = Random.Default,
    ): Int? {
        if (itemCount <= 0) return null
        if (shuffleEnabled && itemCount > 1) {
            val candidate = random.nextInt(itemCount - 1)
            return if (currentIndex in 0 until itemCount && candidate >= currentIndex) {
                candidate + 1
            } else {
                candidate
            }
        }

        val candidate = currentIndex + if (forward) 1 else -1
        return when {
            candidate in 0 until itemCount -> candidate
            repeatMode == PlaybackRepeatMode.ALL && forward -> 0
            repeatMode == PlaybackRepeatMode.ALL -> itemCount - 1
            else -> null
        }
    }
}

/** Reorders queue indices while retaining every item exactly once. Invalid indices leave the order intact. */
fun moveQueueIndices(indices: List<Int>, fromIndex: Int, toIndex: Int): List<Int> {
    if (fromIndex !in indices.indices || toIndex !in indices.indices || fromIndex == toIndex) {
        return indices
    }

    return indices.toMutableList().apply {
        add(toIndex, removeAt(fromIndex))
    }
}
