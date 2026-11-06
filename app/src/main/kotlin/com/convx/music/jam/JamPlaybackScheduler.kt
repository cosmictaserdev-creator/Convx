package com.convx.music.jam

import kotlinx.coroutines.delay

/** Waiting never starts a player. The caller rechecks ownership and projects the latest timeline afterward. */
internal class JamPlaybackScheduler(
    private val nowMs: () -> Long,
    private val sleep: suspend (Long) -> Unit = { delay(it) },
) {
    suspend fun awaitReady(
        current: () -> Boolean,
        ready: () -> Boolean,
        timeoutMs: Long = 30_000L,
    ): Boolean {
        val began = nowMs()
        while (current()) {
            if (ready()) return true
            if (nowMs() - began >= timeoutMs) return false
            sleep(minOf(50L, timeoutMs - (nowMs() - began)))
        }
        return false
    }

    suspend fun awaitStart(current: () -> Boolean, startDelayMs: () -> Long): Boolean {
        while (current()) {
            val remaining = startDelayMs()
            if (remaining <= 0L) return true
            sleep(minOf(remaining, 250L))
        }
        return false
    }
}
