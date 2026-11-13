package com.convx.music.jam

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/** Bounds retries for one item and stops a failing upstream from being hammered across many items. */
class JamRecoveryBudget {
    private val attempts = linkedMapOf<String, Int>()
    private val recentFailures = ArrayDeque<Long>()
    private var openUntilMs = 0L
    private val lock = SynchronizedObject()

    fun recordFailure(mediaId: String, nowMs: Long): Int? = synchronized(lock) { recordFailureLocked(mediaId, nowMs) }

    fun resetTrack(mediaId: String) { synchronized(lock) { attempts.remove(mediaId) } }

    fun remainingBackoffMs(nowMs: Long): Long = synchronized(lock) { (openUntilMs - nowMs).coerceAtLeast(0L) }

    private fun recordFailureLocked(mediaId: String, nowMs: Long): Int? {
        if (openUntilMs - nowMs > 0) return null
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
}
