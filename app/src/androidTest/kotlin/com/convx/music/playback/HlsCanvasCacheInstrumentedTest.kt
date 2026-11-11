package com.convx.music.playback

import androidx.media3.common.StreamKey
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class HlsCanvasCacheInstrumentedTest {
    @Test fun selectedVideoSurvivesCacheReopenAndMissingResourcesInvalidateIt() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("canvas-hls-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val id = "song-${UUID.randomUUID()}"
        val url = "https://example.test/master.m3u8"
        val master = "#EXTM3U\n#EXT-X-VERSION:8\n#EXT-X-DEFINE:NAME=\"first\",VALUE=\"one.ts\"\n" +
            "#EXT-X-STREAM-INF:BANDWIDTH=100000,CODECS=\"avc1.42E01E\"\nlow.m3u8\n" +
            "#EXT-X-STREAM-INF:BANDWIDTH=500000,CODECS=\"avc1.42E01E\"\nhigh.m3u8\n"
        val playlist = "#EXTM3U\n#EXT-X-VERSION:8\n#EXT-X-DEFINE:IMPORT=\"first\"\n#EXT-X-TARGETDURATION:10\n" +
            "#EXT-X-KEY:METHOD=AES-128,URI=\"key.bin\",IV=0x00000000000000000000000000000001\n" +
            "#EXTINF:10,\n{\$first}\n#EXTINF:10,\ntwo.ts\n#EXT-X-ENDLIST\n"
        val files = mapOf("master.m3u8" to master.toByteArray(), "high.m3u8" to playlist.toByteArray(),
            "key.bin" to ByteArray(16) { 7 }, "one.ts" to ByteArray(128) { 1 }, "two.ts" to ByteArray(128) { 2 })
        val requests = mutableListOf<String>()
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
            val name = requireNotNull(uri.lastPathSegment)
            requests += name
            files[name] ?: error("Unexpected request $uri")
        }) }
        var cache = SimpleCache(root, NoOpCacheEvictor(), provider)
        try {
            val factory = CacheDataSource.Factory().setCache(cache).setCacheKeyFactory(canvasSegmentKeyFactory(id))
                .setUpstreamDataSourceFactory(upstream)
            val prefetcher = HlsAudioPrefetcher(id, cache, factory, { canvasCacheKey(id, it) }, canvasVideo = true)
            runBlocking { assertTrue(prefetcher.fill(url, 3, preferLow = false) { true }) }
            assertNull(HlsCanvasCache.completedSelection(id, url, cache))
            runBlocking { assertTrue(prefetcher.fill(url, 1, preferLow = false) { true }) }
            val selected = listOf(StreamKey(0, 1))
            assertEquals(selected, HlsCanvasCache.completedSelection(id, url, cache))
            assertEquals(listOf("master.m3u8", "high.m3u8", "key.bin", "one.ts", "two.ts"), requests)
            cache.release()
            cache = SimpleCache(root, NoOpCacheEvictor(), provider)
            assertEquals(selected, HlsCanvasCache.completedSelection(id, url, cache))
            assertEquals(selected, HlsCanvasCache.preparedSelection(id, url))
            cache.removeResource(canvasCacheKey(id, "https://example.test/key.bin"))
            assertNull(HlsCanvasCache.completedSelection(id, url, cache))
            assertNull(HlsCanvasCache.preparedSelection(id, url))
            assertFalse(cache.isCachedToEnd(canvasCacheKey(id, "https://example.test/key.bin"), 0))
        } finally { cache.release(); root.deleteRecursively() }
    }
}
