package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.StreamKey
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class HlsAudioCacheInstrumentedTest {
    @Test fun prefetchStopsWhenThePlaybackReservePreventsCompletingASegment() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("hls-prefetch-budget-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val mediaId = "track-${UUID.randomUUID()}"
        val url = "https://example.test/playlist.m3u8"
        val manifest = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-TARGETDURATION:10\n#EXTINF:10,\none.ts\n#EXTINF:10,\ntwo.ts\n#EXT-X-ENDLIST\n".toByteArray()
        val requests = mutableListOf<String>()
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
            val name = requireNotNull(uri.lastPathSegment)
            requests += name
            when (name) {
                "playlist.m3u8" -> manifest
                "one.ts", "two.ts" -> ByteArray(500) { 1 }
                else -> error("Unexpected network request $uri")
            }
        }) }
        val policy = { AutomaticCachePolicy(limitBytes = 600) }
        val evictor = PolicyCacheEvictor(policy, { 0 }, { null })
        val cache = SimpleCache(root, evictor, provider)
        val budget = CacheWriteBudget(cache.cacheSpace, policy, { 0 }) { key, bytes -> evictor.makeRoom(cache, key, bytes) }
        evictor.budget = budget
        try {
            val factory = CacheDataSource.Factory().setCache(cache).setCacheKeyFactory(hlsAudioKeyFactory(mediaId))
                .setCacheWriteDataSinkFactory(budgetedCacheSinkFactory(cache, budget, prefetch = true))
                .setUpstreamDataSourceFactory(upstream)
            val prefetcher = HlsAudioPrefetcher(mediaId, cache, factory)
            runBlocking { assertFalse(prefetcher.fill(url, 1, preferLow = false) { true }) }
            assertEquals(450L, cache.cacheSpace)
            assertEquals(listOf("playlist.m3u8", "one.ts"), requests)
            assertFalse(HlsAudioCache.isComplete(mediaId, url, cache))
        } finally { cache.release(); root.deleteRecursively() }
    }

    @Test fun byteRangesAndEncryptionKeysMustBeCachedBeforeAudioIsComplete() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("hls-ranges-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val mediaId = "track-${UUID.randomUUID()}"
        val url = "https://example.test/playlist.m3u8"
        val playlist = "#EXTM3U\n#EXT-X-VERSION:4\n#EXT-X-TARGETDURATION:10\n#EXT-X-KEY:METHOD=AES-128,URI=\"key.bin\",IV=0x00000000000000000000000000000001\n#EXTINF:10,\n#EXT-X-BYTERANGE:64@0\nshared.aac\n#EXTINF:10,\n#EXT-X-BYTERANGE:64@64\nshared.aac\n#EXT-X-ENDLIST\n".toByteArray()
        val files = mapOf("playlist.m3u8" to playlist, "key.bin" to ByteArray(16) { 7 }, "shared.aac" to ByteArray(128) { it.toByte() })
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
            files[uri.lastPathSegment] ?: error("Unexpected network request $uri")
        }) }
        val cache = SimpleCache(root, NoOpCacheEvictor(), provider)
        try {
            val factory = CacheDataSource.Factory().setCache(cache)
                .setCacheKeyFactory(hlsAudioKeyFactory(mediaId)).setUpstreamDataSourceFactory(upstream)
            val prefetcher = HlsAudioPrefetcher(mediaId, cache, factory)
            runBlocking { assertTrue(prefetcher.fill(url, 3, preferLow = false) { true }) }
            assertFalse(HlsAudioCache.isComplete(mediaId, url, cache))
            runBlocking { assertTrue(prefetcher.fill(url, 1, preferLow = false) { true }) }
            assertTrue(HlsAudioCache.isComplete(mediaId, url, cache))
            cache.removeResource(hlsAudioCacheKey(mediaId, "https://example.test/key.bin"))
            assertFalse(HlsAudioCache.isComplete(mediaId, url, cache))
        } finally { cache.release(); root.deleteRecursively() }
    }

    @Test fun prefetchCachesTheOpeningThirdBeforeFinishingTheSelectedRendition() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("hls-prefetch-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val mediaId = "track-${UUID.randomUUID()}"
        val masterUrl = "https://example.test/master.m3u8"
        val master = "#EXTM3U\n#EXT-X-VERSION:8\n#EXT-X-DEFINE:NAME=\"first\",VALUE=\"one.ts\"\n#EXT-X-STREAM-INF:BANDWIDTH=64000,CODECS=\"mp4a.40.2\"\nlow.m3u8\n#EXT-X-STREAM-INF:BANDWIDTH=192000,CODECS=\"mp4a.40.2\"\nhigh.m3u8\n#EXT-X-STREAM-INF:BANDWIDTH=900000,CODECS=\"avc1.42E01E,mp4a.40.2\"\nvideo.m3u8\n".toByteArray()
        val playlist = "#EXTM3U\n#EXT-X-VERSION:8\n#EXT-X-DEFINE:IMPORT=\"first\"\n#EXT-X-TARGETDURATION:10\n#EXTINF:10,\n{\$first}\n#EXTINF:10,\ntwo.ts\n#EXTINF:10,\nthree.ts\n#EXT-X-ENDLIST\n".toByteArray()
        val files = mapOf("master.m3u8" to master, "high.m3u8" to playlist, "video.m3u8" to playlist,
            "one.ts" to ByteArray(128) { 1 }, "two.ts" to ByteArray(128) { 2 }, "three.ts" to ByteArray(128) { 3 })
        val requests = mutableListOf<String>()
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
            val name = requireNotNull(uri.lastPathSegment)
            requests += name
            files[name] ?: error("Unexpected network request $uri")
        }) }
        val cache = SimpleCache(root, NoOpCacheEvictor(), provider)
        try {
            val factory = CacheDataSource.Factory().setCache(cache)
                .setCacheKeyFactory(hlsAudioKeyFactory(mediaId)).setUpstreamDataSourceFactory(upstream)
            val prefetcher = HlsAudioPrefetcher(mediaId, cache, factory)
            runBlocking { assertTrue(prefetcher.fill(masterUrl, 3, preferLow = false) { true }) }
            assertEquals(listOf("master.m3u8", "high.m3u8", "one.ts"), requests)
            assertFalse(HlsAudioCache.isComplete(mediaId, masterUrl, cache))
            var readinessChecks = 0
            runBlocking { assertFalse(prefetcher.fill(masterUrl, 1, preferLow = false) { ++readinessChecks < 4 }) }
            assertEquals(listOf("master.m3u8", "high.m3u8", "one.ts"), requests)
            runBlocking { assertTrue(prefetcher.fill(masterUrl, 1, preferLow = false) { true }) }
            assertEquals(listOf("master.m3u8", "high.m3u8", "one.ts", "two.ts", "three.ts"), requests)
            assertEquals(listOf(StreamKey(0, 1)), HlsAudioCache.completedSelection(mediaId, masterUrl, cache)?.streamKeys)
            val video = factory.createDataSource()
            try {
                video.open(DataSpec(Uri.parse("https://example.test/video.m3u8")))
                val buffer = ByteArray(512)
                while (video.read(buffer, 0, buffer.size) != C.RESULT_END_OF_INPUT) { }
            } finally { video.close() }
            // Prefer audio-only even if a higher-bitrate video rendition is also cached.
            assertEquals(listOf(StreamKey(0, 1)), HlsAudioCache.completedSelection(mediaId, masterUrl, cache)?.streamKeys)
        } finally { cache.release(); root.deleteRecursively() }
    }

    @Test fun oneCompleteVariantAndItsAudioRenditionAreEnoughForOfflinePlayback() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("hls-variant-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val mediaId = "track-${UUID.randomUUID()}"
        val masterUrl = "https://example.test/master.m3u8"
        val master = """
            #EXTM3U
            #EXT-X-VERSION:3
            #EXT-X-MEDIA:TYPE=AUDIO,GROUP-ID="audio",NAME="main",DEFAULT=YES,AUTOSELECT=YES,URI="audio.m3u8"
            #EXT-X-STREAM-INF:BANDWIDTH=64000,CODECS="mp4a.40.2",AUDIO="audio"
            low.m3u8
            #EXT-X-STREAM-INF:BANDWIDTH=192000,CODECS="mp4a.40.2",AUDIO="audio"
            high.m3u8
        """.trimIndent().toByteArray()
        fun playlist(segment: String) = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-TARGETDURATION:2\n#EXTINF:1.0,\n$segment\n#EXT-X-ENDLIST\n".toByteArray()
        val files = mapOf("master.m3u8" to master, "high.m3u8" to playlist("high.ts"),
            "audio.m3u8" to playlist("audio.ts"), "high.ts" to ByteArray(128) { 1 }, "audio.ts" to ByteArray(128) { 2 })
        val upstream = DataSource.Factory { ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
            files[uri.lastPathSegment] ?: error("Unexpected network request $uri")
        }) }
        val keys = listOf(StreamKey(0, 1), StreamKey(1, 0))
        try {
            var cache = SimpleCache(root, NoOpCacheEvictor(), provider)
            try {
                val factory = CacheDataSource.Factory().setCache(cache)
                    .setCacheKeyFactory(hlsAudioKeyFactory(mediaId)).setUpstreamDataSourceFactory(upstream)
                val item = MediaItem.Builder().setUri(masterUrl).setMimeType(MimeTypes.APPLICATION_M3U8).setStreamKeys(keys).build()
                HlsDownloader(item, factory).download(null)
                assertEquals(keys, HlsAudioCache.completedSelection(mediaId, masterUrl, cache)?.streamKeys)
            } finally { cache.release() }
            cache = SimpleCache(root, NoOpCacheEvictor(), provider)
            try {
                assertEquals(keys, HlsAudioCache.completedSelection(mediaId, masterUrl, cache)?.streamKeys)
                cache.removeResource(hlsAudioCacheKey(mediaId, "https://example.test/audio.ts"))
                assertNull(HlsAudioCache.completedSelection(mediaId, masterUrl, cache))
            } finally { cache.release() }
        } finally { root.deleteRecursively() }
    }

    @Test fun cachedPlaylistAndSegmentsSurviveReopenAndMissingSegmentsAreRejected() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("hls-audio-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val mediaId = "track-${UUID.randomUUID()}"
        val manifestUrl = "https://example.test/audio/playlist.m3u8?token=old"
        val segmentUrl = "https://example.test/audio/segment.ts"
        val manifest = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-TARGETDURATION:2\n#EXT-X-MEDIA-SEQUENCE:0\n#EXTINF:1.0,\nsegment.ts\n#EXT-X-ENDLIST\n".toByteArray()
        val segment = ByteArray(4096) { (it % 127).toByte() }
        val upstream = DataSource.Factory {
            ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
                when (uri.lastPathSegment) {
                    "playlist.m3u8" -> manifest
                    "live.m3u8" -> manifest.toString(Charsets.UTF_8).replace("#EXT-X-ENDLIST\n", "").toByteArray()
                    "segment.ts" -> segment
                    else -> error("Unexpected URI $uri")
                }
            })
        }
        try {
            var cache = SimpleCache(root, NoOpCacheEvictor(), provider)
            try {
                val factory = CacheDataSource.Factory().setCache(cache)
                    .setCacheKeyFactory(hlsAudioKeyFactory(mediaId)).setUpstreamDataSourceFactory(upstream)
                val item = MediaItem.Builder().setUri(manifestUrl).setMimeType(MimeTypes.APPLICATION_M3U8).build()
                HlsDownloader(item, factory).download(null)
                HlsAudioCache.remember(context, mediaId, manifestUrl, "preferred-source")
                // Flush the asynchronous preference write before reopening the cache.
                context.getSharedPreferences("cached_hls_audio", android.content.Context.MODE_PRIVATE).edit().commit()
                assertTrue(HlsAudioCache.isComplete(mediaId, manifestUrl, cache))
                val liveUrl = "https://example.test/audio/live.m3u8"
                val live = MediaItem.Builder().setUri(liveUrl).setMimeType(MimeTypes.APPLICATION_M3U8).build()
                HlsDownloader(live, factory).download(null)
                assertFalse(HlsAudioCache.isComplete(mediaId, liveUrl, cache))
            } finally { cache.release() }
            cache = SimpleCache(root, NoOpCacheEvictor(), provider)
            try {
                assertEquals(manifestUrl, HlsAudioCache.savedUrl(context, mediaId))
                assertEquals("preferred-source", HlsAudioCache.savedSource(context, mediaId))
                assertTrue(HlsAudioCache.isComplete(mediaId, manifestUrl.replace("old", "new"), cache))
                cache.removeResource(hlsAudioCacheKey(mediaId, segmentUrl))
                assertFalse(HlsAudioCache.isComplete(mediaId, manifestUrl, cache))
                HlsAudioCache.forget(context, mediaId)
                assertNull(HlsAudioCache.savedUrl(context, mediaId))
                assertNull(HlsAudioCache.savedSource(context, mediaId))
            } finally { cache.release() }
        } finally {
            context.getSharedPreferences("cached_hls_audio", android.content.Context.MODE_PRIVATE).edit()
                .remove(mediaId).remove("$mediaId#source").commit()
            root.deleteRecursively()
        }
    }
}
