package com.convx.music.jam

/** Bounds retries for one item and stops a failing upstream from being hammered across many items. */
internal class JamRecoveryBudget {
    private val attempts = linkedMapOf<String, Int>()
    private val recentFailures = ArrayDeque<Long>()
    private var openUntilMs = 0L

    @Synchronized
    fun recordFailure(mediaId: String, nowMs: Long): Int? {
        if (remainingBackoffMs(nowMs) > 0) return null
        while (recentFailures.isNotEmpty() && nowMs - recentFailures.first() >= 60_000L) recentFailures.removeFirst()
        recentFailures.addLast(nowMs)
        if (recentFailures.size >= 8) {
            openUntilMs = nowMs + 60_000L
            recentFailures.clear()
            return null
        }
        val attempt = (attempts[mediaId] ?: 0) + 1
        attempts[mediaId] = attempt
        while (attempts.size > 32) attempts.remove(attempts.keys.first())
        return attempt.takeIf { it <= 3 }
    }

    @Synchronized
    fun resetTrack(mediaId: String) { attempts.remove(mediaId) }

    @Synchronized
    fun remainingBackoffMs(nowMs: Long): Long = (openUntilMs - nowMs).coerceAtLeast(0L)
}
