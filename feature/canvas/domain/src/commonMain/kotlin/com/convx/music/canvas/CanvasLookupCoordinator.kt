package com.convx.music.canvas

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shares one provider lookup across player surfaces without tying it to any one surface's lifecycle. */
class CanvasLookupCoordinator<K, V : Any>(
    private val scope: CoroutineScope,
    private val nowMillis: () -> Long,
    private val positiveTtlMillis: Long,
    private val negativeTtlMillis: Long,
    private val maxEntries: Int = 256,
    private val lookup: suspend (K) -> V?,
) {
    private data class Entry<V>(val value: V?, val expiresAt: Long)

    private val mutex = Mutex()
    private val entries = linkedMapOf<K, Entry<V>>()
    private val inFlight = mutableMapOf<K, Deferred<V?>>()
    private val waiters = mutableMapOf<Deferred<V?>, Int>()

    suspend fun resolve(key: K): V? {
        val task = mutex.withLock {
            val existing = entries.remove(key)
            if (existing != null && existing.expiresAt > nowMillis()) {
                entries[key] = existing
                return existing.value
            }
            val shared = inFlight[key] ?: scope.async {
                val job = currentCoroutineContext()[Job]
                try {
                    val result = lookup(key)
                    mutex.withLock {
                        if (inFlight[key] === job) {
                            val ttl = if (result == null) negativeTtlMillis else positiveTtlMillis
                            if (ttl > 0 && maxEntries > 0) {
                                entries[key] = Entry(result, nowMillis() + ttl)
                                while (entries.size > maxEntries) entries.remove(entries.keys.first())
                            }
                            inFlight.remove(key)
                        }
                    }
                    result
                } catch (error: Throwable) {
                    withContext(NonCancellable) {
                        mutex.withLock { if (inFlight[key] === job) inFlight.remove(key) }
                    }
                    throw error
                }
            }.also { inFlight[key] = it }
            waiters[shared] = (waiters[shared] ?: 0) + 1
            shared
        }
        return try {
            task.await()
        } finally {
            withContext(NonCancellable) {
                mutex.withLock {
                    val remaining = (waiters[task] ?: 1) - 1
                    if (remaining > 0) waiters[task] = remaining
                    else {
                        waiters.remove(task)
                        // Keep work alive for another surface, but not after every caller
                        // has left the song or a stale prefetch plan has been cancelled.
                        if (!task.isCompleted && inFlight[key] === task) {
                            inFlight.remove(key)
                            task.cancel()
                        }
                    }
                }
            }
        }
    }

    suspend fun invalidate(key: K) {
        mutex.withLock {
            entries.remove(key)
            inFlight.remove(key)?.cancel()
        }
    }

    /** Finds a still-valid positive result that another compatible request can reuse. */
    suspend fun findCached(matches: (K, V) -> Boolean): V? = mutex.withLock {
        val now = nowMillis()
        entries.entries.removeAll { it.value.expiresAt <= now }
        entries.entries.lastOrNull { (key, entry) -> entry.value?.let { matches(key, it) } == true }
            ?.value?.value
    }
}
