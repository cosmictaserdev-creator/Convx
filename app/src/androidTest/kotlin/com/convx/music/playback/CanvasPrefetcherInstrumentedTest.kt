package com.convx.music.playback

import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.convx.music.canvas.canvasCacheKey
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class CanvasPrefetcherInstrumentedTest {
    @Test fun storageLimitDoesNotMarkAnIncompleteCanvasAsReady() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("canvas-budget-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val policy = { AutomaticCachePolicy(limitBytes = 600_000) }
        val evictor = PolicyCacheEvictor(policy, { 0 }, { null })
        val playback = SimpleCache(root.resolve("playback"), evictor, provider)
        val downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
        val budget = CacheWriteBudget(playback.cacheSpace, policy, { 0 }) { key, bytes ->
            evictor.makeRoom(playback, key, bytes)
        }
        evictor.budget = budget
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArray(800_000) { 1 }) }
        val factory = CacheDataSource.Factory().setCache(playback)
            .setCacheWriteDataSinkFactory(budgetedCacheSinkFactory(playback, budget, prefetch = true))
            .setUpstreamDataSourceFactory(upstream)
        val url = "https://example.test/loop.mp4"
        try {
            runBlocking { assertFalse(CanvasPrefetcher(playback, downloads, factory).fill("song", url) { true }) }
            assertTrue(playback.cacheSpace <= 450_000)
            assertFalse(playback.isCachedToEnd(canvasCacheKey("song", url), 0))
        } finally {
            playback.release()
            downloads.release()
            root.deleteRecursively()
        }
    }

    @Test fun pausedCanvasResumesAndIsReusableWithoutNetwork() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("canvas-prefetch-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val playback = SimpleCache(root.resolve("playback"), NoOpCacheEvictor(), provider)
        val downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
        val url = "https://example.test/loop.mp4?signature=abc"
        val id = "song"
        var requests = 0
        val upstream = DataSource.Factory {
            requests++
            ByteArrayDataSource(ByteArray(800 * 1024) { it.toByte() })
        }
        val factory = canvasCacheDataSourceFactory(downloads, playback, upstream, prefetch = true)
        val prefetcher = CanvasPrefetcher(playback, downloads, factory)
        try {
            var checks = 0
            runBlocking { assertFalse(prefetcher.fill(id, url) { ++checks < 2 }) }
            assertFalse(playback.isCachedToEnd(canvasCacheKey(id, url), 0))
            runBlocking { assertTrue(prefetcher.fill(id, url) { true }) }
            assertTrue(playback.isCachedToEnd(canvasCacheKey(id, url), 0))
            val completedRequests = requests
            runBlocking { assertTrue(prefetcher.fill(id, url) { error("Already cached") }) }
            assertEquals(completedRequests, requests)
        } finally {
            playback.release()
            downloads.release()
            root.deleteRecursively()
        }
    }
}
