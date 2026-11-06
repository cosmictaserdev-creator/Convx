/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.jam

import kotlin.math.max

/**
 * This device's offset from the party server's clock, measured the way NTP does it.
 *
 * Send/receive readings use device monotonic time; the echoed wire timestamp only identifies
 * the request. With round trip `rtt`, offset is `serverMs - (t0 + t1) / 2`. The lowest-latency
 * sample minimizes network-delay uncertainty, though asymmetric paths still introduce error.
 */
class JamClock(private val maxSamples: Int = 12, private val maxAgeMs: Long = 60_000L) {
    private data class Sample(val rtt: Long, val offset: Long, val receivedMs: Long)

    private val samples = ArrayDeque<Sample>()

    @Synchronized
    fun observe(clientSentMs: Long, serverMs: Long, receivedMs: Long) {
        val rtt = receivedMs - clientSentMs
        if (rtt < 0 || rtt > 10_000L) return
        expire(receivedMs)
        samples.addLast(Sample(rtt, serverMs - (clientSentMs + receivedMs) / 2, receivedMs))
        while (samples.size > maxSamples) samples.removeFirst()
    }

    /** `serverNow - deviceNow`; 0 until the first pong lands. */
    @Synchronized
    fun offsetMs(): Long = samples.minByOrNull { it.rtt }?.offset ?: 0L

    /** False until the first pong: a position computed before then rests on the device's own clock. */
    val synced: Boolean
        @Synchronized get() = samples.isNotEmpty()

    /** The server's current time according to the device clock plus the measured offset. */
    @Synchronized
    fun serverNow(deviceNow: Long): Long {
        expire(deviceNow)
        return deviceNow + offsetMs()
    }

    @Synchronized
    fun isFresh(deviceNow: Long): Boolean {
        expire(deviceNow)
        return samples.isNotEmpty()
    }

    @Synchronized
    fun uncertaintyMs(): Long = samples.minByOrNull { it.rtt }?.rtt?.div(2) ?: Long.MAX_VALUE

    private fun expire(now: Long) {
        while (samples.isNotEmpty() && now - samples.first().receivedMs > maxAgeMs) samples.removeFirst()
    }

    @Synchronized
    fun reset() = samples.clear()
}

object JamSync {
    /**
     * Where the party is right now. [PartyPlayback.positionMs] is true at [PartyPlayback.anchorMs];
     * while playing, the time since then is added, so a late frame still lands in the right place.
     * A resume is anchored slightly in the future, and until then the position holds.
     */
    fun positionAt(playback: PartyPlayback, serverNow: Long): Long =
        if (playback.isPlaying) playback.positionMs + max(0L, serverNow - playback.anchorMs) else playback.positionMs

    /** How long to wait before a resume anchored at [PartyPlayback.anchorMs] should actually start. */
    fun startDelayMs(playback: PartyPlayback, serverNow: Long): Long =
        if (playback.isPlaying) max(0L, playback.anchorMs - serverNow) else 0L
}

/** The smallest server action that turns the party's upcoming songs into this device's. */
sealed interface QueueChange {
    data object None : QueueChange
    data object Clear : QueueChange

    /** [localFrom] until [localTo] of the local upcoming list are new; [playNext] puts them right after the current song. */
    data class Add(val localFrom: Int, val localTo: Int, val playNext: Boolean) : QueueChange

    /** The song at [index] of the party's upcoming list is gone. */
    data class Remove(val index: Int, val videoId: String) : QueueChange

    /** One song moved within the upcoming list, both indices into it. */
    data class Move(val from: Int, val to: Int, val videoId: String) : QueueChange

    /** Anything else: send the whole list. */
    data object Replace : QueueChange
}

object JamQueueDiff {
    /** Compares video ids of the songs after the current one: [party] is what the server holds, [local] what this device has. */
    fun between(party: List<String>, local: List<String>): QueueChange {
        if (party == local) return QueueChange.None
        if (local.isEmpty()) return QueueChange.Clear

        if (local.size > party.size) {
            val extra = local.size - party.size
            if (local.subList(0, party.size) == party) return QueueChange.Add(party.size, local.size, playNext = false)
            if (local.subList(extra, local.size) == party) return QueueChange.Add(0, extra, playNext = true)
        }

        if (local.size == party.size - 1) {
            val at = party.indices.firstOrNull { it >= local.size || party[it] != local[it] } ?: return QueueChange.Replace
            val id = party[at]
            // The server removes the first copy of an id, so a repeated one cannot be removed unambiguously.
            val removed = party.toMutableList().also { it.removeAt(at) }
            if (removed == local && party.count { it == id } == 1) return QueueChange.Remove(at, id)
        }

        if (local.size == party.size) {
            val a = party.indices.first { party[it] != local[it] }
            val b = party.indices.last { party[it] != local[it] }
            if (b > a) {
                if (local[b] == party[a] && local.subList(a, b) == party.subList(a + 1, b + 1) && party.count { it == party[a] } == 1) {
                    return QueueChange.Move(a, b, party[a])
                }
                if (local[a] == party[b] && local.subList(a + 1, b + 1) == party.subList(a, b) && party.count { it == party[b] } == 1) {
                    return QueueChange.Move(b, a, party[b])
                }
            }
        }
        return QueueChange.Replace
    }
}
