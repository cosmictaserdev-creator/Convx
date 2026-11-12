package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class PlayerCacheBudgetInstrumentedTest {
    @Test
    fun hlsAnalysisHeadRespectsTheSamePrefetchReserve() {
        withCache(AutomaticCachePolicy(limitBytes = 80_000)) { cache, _, budget ->
            val manifest = """
                #EXTM3U
                #EXT-X-VERSION:7
                #EXT-X-TARGETDURATION:10
                #EXT-X-MAP:URI="init.mp4"
                #EXTINF:10,
                segment.m4s
                #EXT-X-ENDLIST
            """.trimIndent().toByteArray()
            val files = mapOf(
                "https://example.test/audio.m3u8" to manifest,
                "https://example.test/init.mp4" to ByteArray(128) { 1 },
                "https://example.test/segment.m4s" to ByteArray(128_000) { 2 },
            )
            runBlocking {
                HlsAnalysisHead.prefetch(
                    "song", Uri.parse("https://example.test/audio.m3u8"), cache,
                    DataSource.Factory { FixtureDataSource(files) },
                    budgetedCacheSinkFactory(cache, budget, prefetch = true),
                )
            }
            assertEquals(60_000L, cache.cacheSpace)
            assertTrue(cache.isCached("song#hls-analysis", 0, 60_000))
        }
    }

    @Test
    fun prefetchLeavesThePlaybackReserveAvailable() {
        withCache(AutomaticCachePolicy(limitBytes = 10_000)) { cache, _, budget ->
            val bytes = ByteArray(8_000) { 3 }
            assertArrayEquals(bytes, read(cache, budget, "next", bytes, prefetch = true))
            assertEquals(7_500L, cache.cacheSpace)
            assertArrayEquals(bytes, read(cache, budget, "current", bytes))
            assertEquals(10_000L, cache.cacheSpace)
            assertTrue(cache.isCached("current", 0, 2_500))
        }
    }

    @Test
    fun prefetchDoesNotEvictExistingPlaybackWhenReplacementIsEnabled() {
        withCache(AutomaticCachePolicy(limitBytes = 10_000, evictWhenFull = true)) { cache, _, budget ->
            read(cache, budget, "old", ByteArray(7_000) { 1 })
            read(cache, budget, "next", ByteArray(8_000) { 2 }, prefetch = true)
            assertTrue(cache.isCached("old", 0, 7_000))
            assertEquals(7_500L, cache.cacheSpace)
        }
    }

    @Test
    fun fullCacheStopsWritingWithoutInterruptingPlayback() {
        withCache(AutomaticCachePolicy(limitBytes = 10_000)) { cache, _, budget ->
            val bytes = ByteArray(20_000) { (it % 251).toByte() }
            assertArrayEquals(bytes, read(cache, budget, "song-a", bytes))
            assertEquals(10_000L, cache.cacheSpace)
            assertTrue(cache.isCached("song-a", 0, 10_000))
            assertArrayEquals(bytes, read(cache, budget, "song-b", bytes))
            assertEquals(10_000L, cache.cacheSpace)
            assertFalse(cache.isCached("song-b", 0, 1))
        }
    }

    @Test
    fun evictionRemovesTheOldestSongAndProtectsThePlayingSong() {
        var policy = AutomaticCachePolicy(limitBytes = 12_000, evictWhenFull = false)
        withCache(policy, { policy }, activeId = { "song-b" }) { cache, _, budget ->
            val bytes = ByteArray(4_000) { 7 }
            read(cache, budget, "song-a", bytes)
            read(cache, budget, "song-a#canvas-v2:https://example.test/a", bytes)
            read(cache, budget, "song-b", bytes)
            policy = policy.copy(evictWhenFull = true)
            assertArrayEquals(bytes, read(cache, budget, "song-c", bytes))
            assertFalse(cache.isCached("song-a", 0, 1))
            assertFalse(cache.isCached("song-a#canvas-v2:https://example.test/a", 0, 1))
            assertTrue(cache.isCached("song-b", 0, 4_000))
            assertTrue(cache.isCached("song-c", 0, 4_000))
            assertTrue(cache.cacheSpace <= 12_000)
        }
    }

    private fun withCache(
        initial: AutomaticCachePolicy,
        policy: () -> AutomaticCachePolicy = { initial },
        activeId: () -> String? = { null },
        block: (SimpleCache, PolicyCacheEvictor, CacheWriteBudget) -> Unit,
    ) {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("player-budget-test-${UUID.randomUUID()}")
        val evictor = PolicyCacheEvictor(policy, { 0 }, activeId)
        val cache = SimpleCache(root, evictor, StandaloneDatabaseProvider(context))
        val budget = CacheWriteBudget(cache.cacheSpace, policy, { 0 }) { key, bytes -> evictor.makeRoom(cache, key, bytes) }
        evictor.budget = budget
        try {
            block(cache, evictor, budget)
        } finally {
            cache.release()
            root.deleteRecursively()
        }
    }

    private fun read(cache: SimpleCache, budget: CacheWriteBudget, key: String, bytes: ByteArray, prefetch: Boolean = false): ByteArray {
        val source = CacheDataSource.Factory().setCache(cache)
            .setCacheWriteDataSinkFactory(budgetedCacheSinkFactory(cache, budget, prefetch))
            .setUpstreamDataSourceFactory(DataSource.Factory { ByteArrayDataSource(bytes) })
            .createDataSource()
        val output = ByteArrayOutputStream()
        try {
            source.open(DataSpec.Builder().setUri(Uri.parse("https://example.test/$key")).setKey(key).build())
            val buffer = ByteArray(4096)
            while (true) {
                val count = source.read(buffer, 0, buffer.size)
                if (count == C.RESULT_END_OF_INPUT) break
                output.write(buffer, 0, count)
            }
        } finally {
            source.close()
        }
        return output.toByteArray()
    }

    private class FixtureDataSource(private val files: Map<String, ByteArray>) : DataSource {
        private var delegate: ByteArrayDataSource? = null
        override fun addTransferListener(transferListener: TransferListener) = Unit
        override fun open(dataSpec: DataSpec): Long = ByteArrayDataSource(requireNotNull(files[dataSpec.uri.toString()]))
            .also { delegate = it }.open(dataSpec)
        override fun read(buffer: ByteArray, offset: Int, length: Int): Int = requireNotNull(delegate).read(buffer, offset, length)
        override fun getUri(): Uri? = delegate?.uri
        override fun close() { delegate?.close(); delegate = null }
    }
}
