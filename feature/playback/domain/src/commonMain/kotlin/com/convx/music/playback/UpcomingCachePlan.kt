package com.convx.music.playback

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

/** Work is ordered so every upcoming song gets an opening before any is completed. */
data class UpcomingCacheWork(val mediaId: String, val denominator: Int)

fun upcomingCachePlan(currentMediaId: String?, upcomingMediaIds: List<String>): List<UpcomingCacheWork> {
    val tracks = upcomingMediaIds.asSequence()
        .filter { it.isNotBlank() && it != currentMediaId }
        .distinct()
        .take(3)
        .toList()
    return tracks.map { UpcomingCacheWork(it, 3) } + tracks.map { UpcomingCacheWork(it, 1) }
}

/** Byte target for a known progressive stream. Unknown lengths must not be guessed. */
fun upcomingCacheTarget(contentLength: Long, denominator: Int): Long? {
    if (contentLength <= 0 || denominator <= 0) return null
    return contentLength / denominator + if (contentLength % denominator == 0L) 0 else 1
}

/** Runs one stage at a time; an interrupted stage is retried before advancing. */
suspend fun runUpcomingCachePlan(
    plan: List<UpcomingCacheWork>,
    isRelevant: () -> Boolean,
    isReady: suspend () -> Boolean,
    cache: suspend (UpcomingCacheWork) -> Boolean,
    retryDelayMillis: Long = 5_000,
) {
    for (work in plan) {
        while (true) {
            currentCoroutineContext().ensureActive()
            if (!isRelevant()) return
            if (isReady()) {
                // Readiness can suspend while the listener changes the queue.
                currentCoroutineContext().ensureActive()
                if (!isRelevant()) return
                if (cache(work)) break
            }
            delay(retryDelayMillis.coerceAtLeast(1))
        }
    }
}
