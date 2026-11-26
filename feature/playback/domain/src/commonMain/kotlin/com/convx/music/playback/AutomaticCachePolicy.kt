package com.convx.music.playback

/** Policy for automatic media; explicit downloads are outside this budget. */
data class AutomaticCachePolicy(
    val limitBytes: Long = 2L * 1024 * 1024 * 1024,
    val evictWhenFull: Boolean = false,
    val playbackReservePercent: Int = 25,
) {
    fun availableBytes(usedBytes: Long, reservedBytes: Long = 0): Long {
        val limit = limitBytes.coerceAtLeast(0)
        val used = usedBytes.coerceAtLeast(0).coerceAtMost(limit)
        val remaining = limit - used
        return remaining - reservedBytes.coerceAtLeast(0).coerceAtMost(remaining)
    }

    /** Prefetch cannot consume the part reserved for songs the listener actually plays. */
    fun availablePrefetchBytes(usedBytes: Long, prefetchBytes: Long, reservedBytes: Long = 0): Long {
        val limit = limitBytes.coerceAtLeast(0)
        val percent = playbackReservePercent.coerceIn(0, 100)
        val protected = (limit / 100) * percent + (limit % 100) * percent / 100
        val prefetchLimit = limit - protected
        val beforeReservations = prefetchLimit - prefetchBytes.coerceAtLeast(0).coerceAtMost(prefetchLimit)
        val prefetchRemaining = beforeReservations - reservedBytes.coerceAtLeast(0).coerceAtMost(beforeReservations)
        return minOf(availableBytes(usedBytes, reservedBytes), prefetchRemaining)
    }
}
