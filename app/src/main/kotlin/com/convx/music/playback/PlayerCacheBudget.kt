package com.convx.music.playback

import android.content.Context
import androidx.media3.datasource.DataSink
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheEvictor
import androidx.media3.datasource.cache.CacheSpan
import coil3.disk.DiskCache
import com.convx.music.constants.CacheEvictWhenFullKey
import com.convx.music.constants.CachePlaybackReservePercentKey
import com.convx.music.constants.MaxSongCacheSizeKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import java.util.TreeSet

/** The image cache participates in the same admission calculation as player media. */
internal object AutomaticCacheAccounting {
    @Volatile private var imageCache: DiskCache? = null
    fun registerImageCache(cache: DiskCache) { imageCache = cache }
    fun imageBytes(): Long = runCatching { imageCache?.size ?: 0L }.getOrDefault(0L)
}

internal fun cachePolicy(context: Context): AutomaticCachePolicy {
    val mb = context.dataStore[MaxSongCacheSizeKey] ?: 2048
    return AutomaticCachePolicy(
        limitBytes = if (mb < 0) Long.MAX_VALUE else mb.toLong() * 1024 * 1024,
        evictWhenFull = context.dataStore[CacheEvictWhenFullKey] ?: false,
        playbackReservePercent = context.dataStore[CachePlaybackReservePercentKey] ?: 25,
    )
}

/** Tracks bytes currently being written as well as already committed spans. */
internal class CacheWriteBudget(
    initialCommitted: Long,
    private val policy: () -> AutomaticCachePolicy,
    private val otherBytes: () -> Long,
    private val makeRoom: (String, Long) -> Unit = { _, _ -> },
) {
    internal class Reservation(val key: String, var position: Long, var bytes: Long = 0)
    private val reservations = mutableListOf<Reservation>()
    private var committed = initialCommitted.coerceAtLeast(0)
    private var reserved = 0L

    @Synchronized fun open(key: String, position: Long = 0): Reservation = Reservation(key, position).also(reservations::add)

    fun reserve(reservation: Reservation, requested: Int, prefetch: Boolean = false): Int {
        val currentPolicy = policy()
        if (currentPolicy.evictWhenFull && !prefetch) {
            val inFlight = synchronized(this) { reserved }
            val required = if (Long.MAX_VALUE - inFlight < requested) Long.MAX_VALUE else inFlight + requested
            makeRoom(reservation.key, required)
        }
        val other = otherBytes().coerceAtLeast(0)
        return synchronized(this) {
            val used = if (Long.MAX_VALUE - committed < other) Long.MAX_VALUE else committed + other
            // Conservatively count all automatic media against the prefetch allowance.
            // This leaves the playback reserve intact even after an app restart.
            val allowance = if (prefetch) currentPolicy.availablePrefetchBytes(used, used, reserved)
                else currentPolicy.availableBytes(used, reserved)
            val granted = minOf(requested.toLong(), allowance).toInt()
            reservation.bytes += granted
            reserved += granted
            granted
        }
    }

    @Synchronized fun onAdded(key: String, position: Long, length: Long) {
        committed = if (Long.MAX_VALUE - committed < length) Long.MAX_VALUE else committed + length
        val spanEnd = if (Long.MAX_VALUE - position < length) Long.MAX_VALUE else position + length
        reservations.asSequence().filter { it.key == key }.forEach { reservation ->
            val reservationEnd = if (Long.MAX_VALUE - reservation.position < reservation.bytes) Long.MAX_VALUE
                else reservation.position + reservation.bytes
            if (reservation.position >= position && reservation.position < spanEnd) {
                val released = (minOf(reservationEnd, spanEnd) - reservation.position).coerceAtLeast(0)
                reservation.position += released
                reservation.bytes -= released
                reserved -= released
            }
        }
    }

    @Synchronized fun onRemoved(length: Long) { committed = (committed - length).coerceAtLeast(0) }

    @Synchronized fun isWriting(songId: String): Boolean =
        reservations.any { it.bytes > 0 && cacheKeyBelongsToSong(it.key, songId) }

    @Synchronized fun close(reservation: Reservation) {
        if (reservations.remove(reservation)) {
            reserved -= reservation.bytes
            reservation.bytes = 0
        }
    }
}

/** Switches between no eviction and LRU without rebuilding the live Media3 cache. */
internal class PolicyCacheEvictor(
    private val policy: () -> AutomaticCachePolicy,
    private val otherBytes: () -> Long = AutomaticCacheAccounting::imageBytes,
    private val activeMediaId: () -> String? = { PlayerCacheBudgetRegistry.activeMediaId },
) : CacheEvictor {
    private val spans = TreeSet<CacheSpan> { a, b ->
        val byTime = a.lastTouchTimestamp.compareTo(b.lastTouchTimestamp)
        if (byTime != 0) byTime else a.compareTo(b)
    }
    private var currentSize = 0L
    @Volatile var budget: CacheWriteBudget? = null

    override fun requiresCacheSpanTouches() = true
    override fun onCacheInitialized() = Unit
    // The budget reserves exact bytes before writing. Evicting for the sink's
    // advertised fragment length here would also evict songs for prefetches.
    override fun onStartFile(cache: Cache, key: String, position: Long, length: Long) = Unit
    override fun onSpanAdded(cache: Cache, span: CacheSpan) {
        if (spans.add(span)) {
            currentSize += span.length
            budget?.onAdded(span.key, span.position, span.length)
        }
        evict(cache, 0, span.key)
    }
    override fun onSpanRemoved(cache: Cache, span: CacheSpan) {
        if (spans.remove(span)) {
            currentSize -= span.length
            budget?.onRemoved(span.length)
        }
    }
    override fun onSpanTouched(cache: Cache, oldSpan: CacheSpan, newSpan: CacheSpan) {
        spans.remove(oldSpan)
        spans.add(newSpan)
    }

    fun makeRoom(cache: Cache, key: String, incoming: Long) {
        synchronized(cache) { evict(cache, incoming, key) }
    }

    private fun evict(cache: Cache, incoming: Long, protectedKey: String) {
        val currentPolicy = policy()
        if (!currentPolicy.evictWhenFull) return
        val capacity = currentPolicy.availableBytes(otherBytes())
        while (spans.isNotEmpty() && (currentSize > capacity || incoming > capacity - currentSize)) {
            val activeId = activeMediaId()
            val protectedId = protectedKey.substringBefore('#')
            val songId = leastRecentlyUsedCacheBundle(spans.asSequence().filter { span ->
                val songId = span.key.substringBefore('#')
                !cacheKeyBelongsToSong(span.key, protectedId) && budget?.isWriting(songId) != true &&
                    (activeId == null || !cacheKeyBelongsToSong(span.key, activeId))
            }.map { it.key.substringBefore('#') to it.lastTouchTimestamp }) ?: break
            val keys = cache.keys.filter { cacheKeyBelongsToSong(it, songId) }
            val before = currentSize
            try {
                keys.forEach(cache::removeResource)
            } catch (_: Cache.CacheException) {
                break
            }
            if (currentSize >= before) break
        }
    }
}

/** Registry keeps every player-cache writer on the same admission ledger. */
internal object PlayerCacheBudgetRegistry {
    @Volatile private var registeredCache: Cache? = null
    @Volatile private var budget: CacheWriteBudget? = null
    @Volatile internal var activeMediaId: String? = null

    fun canAdmitArtwork(): Boolean {
        val ledger = budget ?: return false
        val reservation = ledger.open("artwork-admission-probe")
        return try { ledger.reserve(reservation, 1, prefetch = true) == 1 }
        finally { ledger.close(reservation) }
    }

    fun register(context: Context, cache: Cache, evictor: PolicyCacheEvictor) {
        val ledger = CacheWriteBudget(cache.cacheSpace, { cachePolicy(context) }, AutomaticCacheAccounting::imageBytes) { key, size ->
            evictor.makeRoom(cache, key, size)
        }
        evictor.budget = ledger
        registeredCache = cache
        budget = ledger
    }

    fun sinkFactory(cache: Cache, prefetch: Boolean = false): DataSink.Factory {
        val ledger = budget.takeIf { registeredCache === cache }
        return budgetedCacheSinkFactory(cache, ledger, prefetch)
    }

    fun canPrefetch(cache: Cache): Boolean {
        val ledger = budget.takeIf { registeredCache === cache } ?: return false
        val reservation = ledger.open("prefetch-capacity-probe")
        return try { ledger.reserve(reservation, 1, prefetch = true) == 1 }
        finally { ledger.close(reservation) }
    }
}

internal fun budgetedCacheSinkFactory(cache: Cache, budget: CacheWriteBudget?, prefetch: Boolean = false): DataSink.Factory {
    val delegate = CacheDataSink.Factory().setCache(cache).setFragmentSize(512 * 1024L)
    return if (budget == null) delegate else DataSink.Factory { BudgetedCacheSink(delegate.createDataSink(), budget, prefetch) }
}

/** Stops caching at the limit while continuing to deliver all upstream bytes to playback. */
internal class CacheBudgetExhaustedException : java.io.IOException("Automatic cache budget exhausted")

internal fun Throwable.isCacheBudgetExhausted(): Boolean =
    generateSequence(this) { it.cause?.takeIf { cause -> cause !== it } }
        .take(16).any { it is CacheBudgetExhaustedException }

internal class BudgetedCacheSink(
    private val delegate: DataSink,
    private val budget: CacheWriteBudget,
    private val prefetch: Boolean = false,
) : DataSink {
    private var spec: DataSpec? = null
    private var reservation: CacheWriteBudget.Reservation? = null
    private var opened = false
    private var stopped = false

    override fun open(dataSpec: DataSpec) {
        spec = dataSpec
        reservation = budget.open(dataSpec.key ?: dataSpec.uri.toString(), dataSpec.position)
        opened = false
        stopped = false
    }

    override fun write(buffer: ByteArray, offset: Int, length: Int) {
        if (stopped || length <= 0) return
        val token = reservation ?: return
        val allowed = budget.reserve(token, length, prefetch)
        if (allowed > 0) {
            try {
                if (!opened) {
                    delegate.open(requireNotNull(spec))
                    opened = true
                }
                delegate.write(buffer, offset, allowed)
            } catch (_: Exception) {
                stop()
                return
            }
        }
        if (allowed < length) {
            stop()
            // Playback must continue uncached; prefetch has no consumer for discarded bytes.
            if (prefetch) throw CacheBudgetExhaustedException()
        }
    }

    private fun stop() {
        stopped = true
        if (opened) runCatching { delegate.close() }
        opened = false
        reservation?.let(budget::close)
        reservation = null
    }

    override fun close() = stop()
}
