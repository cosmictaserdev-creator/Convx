package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.hls.offline.HlsDownloader
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.convx.music.canvas.canvasCacheKey
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class CanvasCacheInstrumentedTest {
    @Test
    fun progressiveCanvasSurvivesCacheReopenWithoutUpstream() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("canvas-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val expected = ByteArray(96_000) { (it % 251).toByte() }
        val oldUrl = "https://example.test/canvas.mp4?token=old"
        val newUrl = "https://example.test/canvas.mp4?token=new"
        val key = canvasCacheKey("track", oldUrl)
        try {
            var playback = SimpleCache(root.resolve("player"), NoOpCacheEvictor(), provider)
            var downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
            try {
                val factory = canvasCacheDataSourceFactory(downloads, playback, DataSource.Factory { ByteArrayDataSource(expected) })
                assertArrayEquals(expected, read(factory.createDataSource(), oldUrl, key))
                assertTrue(playback.isCachedToEnd(key, 0))
                assertEquals(0L, downloads.cacheSpace)
            } finally {
                playback.release()
                downloads.release()
            }
            playback = SimpleCache(root.resolve("player"), NoOpCacheEvictor(), provider)
            downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
            try {
                val offline = canvasCacheDataSourceFactory(downloads, playback, DataSource.Factory { ByteArrayDataSource(byteArrayOf(0)) })
                assertArrayEquals(expected, read(offline.createDataSource(), newUrl, canvasCacheKey("track", newUrl)))
            } finally {
                playback.release()
                downloads.release()
            }
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun segmentedDownloadIsAvailableAfterCacheReopen() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val root = context.cacheDir.resolve("canvas-hls-test-${UUID.randomUUID()}")
        val provider = StandaloneDatabaseProvider(context)
        val manifest = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-TARGETDURATION:2\n#EXT-X-MEDIA-SEQUENCE:0\n#EXTINF:1.0,\nsegment.ts\n#EXT-X-ENDLIST\n".toByteArray()
        val segment = ByteArray(4096) { (it % 127).toByte() }
        val manifestUrl = "https://example.test/loop/playlist.m3u8?token=old"
        val segmentUrl = "https://example.test/loop/segment.ts"
        val keyFactory = canvasSegmentKeyFactory("track")
        val upstream = DataSource.Factory {
            ByteArrayDataSource(ByteArrayDataSource.UriResolver { uri ->
                when (uri.lastPathSegment) {
                    "playlist.m3u8" -> manifest
                    "segment.ts" -> segment
                    else -> error("Unexpected URI $uri")
                }
            })
        }
        try {
            var downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
            var playback = SimpleCache(root.resolve("player"), NoOpCacheEvictor(), provider)
            try {
                val factory = CacheDataSource.Factory().setCache(downloads)
                    .setCacheKeyFactory(keyFactory).setUpstreamDataSourceFactory(upstream)
                val item = MediaItem.Builder().setUri(manifestUrl).setMimeType(MimeTypes.APPLICATION_M3U8).build()
                HlsDownloader(item, factory).download(null)
                assertTrue(downloads.isCachedToEnd(canvasCacheKey("track", manifestUrl), 0))
                assertFalse(downloads.getCachedSpans(canvasCacheKey("track", segmentUrl)).isEmpty())
                CanvasSelectionRepository.markHlsDownloaded(context, "track", manifestUrl, downloads)
                assertTrue(CanvasSelectionRepository.hasCompleteHls(context, "track", manifestUrl, downloads))
                assertTrue(CanvasSelectionRepository.hasCompleteCachedHls("track", manifestUrl, downloads))
            } finally {
                downloads.release()
                playback.release()
            }
            downloads = SimpleCache(root.resolve("downloads"), NoOpCacheEvictor(), provider)
            playback = SimpleCache(root.resolve("player"), NoOpCacheEvictor(), provider)
            try {
                val offline = canvasCacheDataSourceFactory(downloads, playback, DataSource.Factory { ByteArrayDataSource(byteArrayOf(0)) }, keyFactory)
                assertArrayEquals(manifest, read(offline.createDataSource(), manifestUrl, canvasCacheKey("track", manifestUrl)))
                assertArrayEquals(segment, read(offline.createDataSource(), segmentUrl, canvasCacheKey("track", segmentUrl)))
                downloads.removeResource(canvasCacheKey("track", segmentUrl))
                assertFalse(CanvasSelectionRepository.hasCompleteHls(context, "track", manifestUrl, downloads))
                assertFalse(CanvasSelectionRepository.hasCompleteCachedHls("track", manifestUrl, downloads))
            } finally {
                downloads.release()
                playback.release()
            }
        } finally {
            CanvasSelectionRepository.clearDownloadedHls(context, "track")
            root.deleteRecursively()
        }
    }

    private fun read(source: DataSource, url: String, key: String): ByteArray {
        val output = ByteArrayOutputStream()
        try {
            source.open(DataSpec.Builder().setUri(Uri.parse(url)).setKey(key).build())
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
}
