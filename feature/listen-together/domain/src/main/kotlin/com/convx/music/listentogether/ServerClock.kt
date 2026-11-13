/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.listentogether

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/**
 * Estimates how far this device's clock sits from the room server's, so a guest can answer
 * "how long ago did the server stamp this action" without trusting the phone's wall clock.
 *
 * Each frame that carries a server timestamp gives `receivedAt - serverTime`, which is the clock
 * offset plus that frame's delay in transit. The smallest delta seen recently has the least
 * transit in it, so it is the best estimate of the offset alone (the minimum-delay filter NTP
 * uses). Frames that arrive late then show up as extra elapsed time, which is what
 * moves a guest further along the track.
 */
class ServerClock(
    private val windowMs: Long = 60_000L,
    private val maxSamples: Int = 32,
) {
    private data class Sample(val receivedAt: Long, val delta: Long)

    private val samples = ArrayDeque<Sample>()
    private val lock = SynchronizedObject()

    /** Record a frame stamped [serverTime] (server ms) that arrived at [receivedAt] (device ms). */
    fun observe(serverTime: Long, receivedAt: Long) {
        if (serverTime <= 0L) return
        synchronized(lock) {
            samples.addLast(Sample(receivedAt, receivedAt - serverTime))
            trim(receivedAt)
        }
    }

    /** Best estimate of `deviceNow - serverNow`, or null before any stamped frame has arrived. */
    fun offsetMs(now: Long): Long? = synchronized(lock) { offsetLocked(now) }

    /**
     * Milliseconds of playback that have passed since the server stamped [serverTime], measured on
     * the server's timeline. Falls back to the raw device clock until an offset is known.
     */
    fun elapsedSince(serverTime: Long, now: Long): Long {
        val offset = synchronized(lock) { offsetLocked(now) } ?: return maxOf(0L, now - serverTime)
        return maxOf(0L, now - offset - serverTime)
    }

    fun reset() = synchronized(lock) { samples.clear() }

    private fun offsetLocked(now: Long): Long? {
        trim(now)
        return samples.minOfOrNull { it.delta }
    }

    private fun trim(now: Long) {
        while (samples.isNotEmpty() && (now - samples.first().receivedAt > windowMs || samples.size > maxSamples)) {
            samples.removeFirst()
        }
    }
}
