package com.convx.music.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shared CPU budget for decorative artwork; leaves cores available to UI and playback. */
internal object ArtworkProcessing {
    val dispatcher = Dispatchers.Default.limitedParallelism(2)

    // Bounded locks coalesce simultaneous requests for the same treatment without
    // retaining artwork URLs or creating a process-lifetime job for every image.
    private val locks = Array(32) { Mutex() }

    suspend fun <T : Any> cached(
        key: String,
        lookup: () -> T?,
        compute: suspend () -> T?,
    ): T? {
        lookup()?.let { return it }
        return locks[(key.hashCode() and Int.MAX_VALUE) % locks.size].withLock {
            lookup() ?: compute()
        }
    }
}
