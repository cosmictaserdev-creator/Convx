package com.convx.music.playback

import android.media.MediaDataSource
import android.net.Uri
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.ContentMetadata
import androidx.media3.datasource.cache.SimpleCache
import com.convx.music.playback.smart.TrackAnalyzer
import java.io.IOException

/** Gives [TrackAnalyzer] access to the same cache used by the player. */
@UnstableApi
object AudioCache {

    private const val TAG = "ConvxAudioCache"

    private lateinit var cache: SimpleCache

    /** Finished downloads live in their own cache; analysis reads them too, never writes or evicts. */
    private var downloadCache: SimpleCache? = null

    val keyFactory = CacheKeyFactory { spec ->
        spec.key ?: spec.uri.getQueryParameter("v") ?: spec.uri.toString()
    }

    fun init(playerCache: SimpleCache, downloads: SimpleCache? = null) {
        cache = playerCache
        downloadCache = downloads
    }

    data class Rendition(
        val key: String,
        val isComplete: Boolean,
        val cachedPrefix: Long,
        val contentLength: Long = 0L,
        val fromDownload: Boolean = false,
    )

    fun renditionsOf(uri: Uri): List<Rendition> {
        if (!::cache.isInitialized) return emptyList()
        val videoId = uri.getQueryParameter("v") ?: return emptyList()
        return buildList {
            for ((source, fromDownload) in listOfNotNull(cache to false, downloadCache?.let { it to true })) {
                for (key in source.keys) {
                    // Canvas and other sidecars share a song prefix but are not audio.
                    if (!isAnalysisAudioCacheKey(key, videoId)) continue
                    val contentLength = ContentMetadata.getContentLength(source.getContentMetadata(key))
                    val prefix = source.getCachedLength(key, 0, Long.MAX_VALUE).coerceAtLeast(0L)
                    add(
                        Rendition(
                            key = key,
                            isComplete = contentLength > 0 && prefix >= contentLength,
                            cachedPrefix = prefix,
                            contentLength = contentLength.coerceAtLeast(0L),
                            fromDownload = fromDownload,
                        )
                    )
                }
            }
        }
    }

    /** Null keeps analysis cache-only. */
    var analyzerUpstream: DataSource.Factory? = null

    /** [fetchUri] must be the playable URL; all fills retain the rendition's cache key. */
    fun renditionDataSource(uri: Uri, rendition: Rendition, fetchUri: Uri? = null): MediaDataSource {
        val selectedCache = if (rendition.fromDownload) downloadCache ?: cache else cache
        val factory = CacheDataSource.Factory()
            .setCache(selectedCache)
            .setCacheKeyFactory(keyFactory)
            .setCacheWriteDataSinkFactory(
                if (rendition.fromDownload) null else PlayerCacheBudgetRegistry.sinkFactory(selectedCache, prefetch = true)
            )
        if (fetchUri != null) {
            factory.setUpstreamDataSourceFactory(analyzerUpstream)
        }
        val canFetch = fetchUri != null && analyzerUpstream != null
        val readableLength = rendition.contentLength.takeIf { it > 0 && (rendition.isComplete || canFetch) }
            ?: C.LENGTH_UNSET.toLong()
        return CacheMediaDataSource(factory.createDataSource(), fetchUri ?: uri, rendition.key, readableLength)
    }

    fun cacheKeyOf(uri: Uri): String = keyFactory.buildCacheKey(DataSpec(uri))

    var prefetchHead: ((Uri) -> Unit)? = null

    fun requestAnalysisHead(uri: Uri) {
        val hook = prefetchHead
        if (hook != null) {
            hook(uri)
        } else {
            Log.d(TAG, "requestAnalysisHead: no prefetch hook installed, ${uri.getQueryParameter("v") ?: uri}")
        }
    }

    fun discardBadRendition(uri: Uri, key: String): Boolean {
        if (!::cache.isInitialized) return false
        val currentKey = cacheKeyOf(uri)
        if (key == currentKey) {
            Log.d(TAG, "Refusing to discard active rendition $key")
            return false
        }
        return runCatching { cache.removeResource(key) }
            .onSuccess { Log.d(TAG, "Discarded bad rendition $key") }
            .onFailure { Log.d(TAG, "Could not discard $key: ${it.message}") }
            .isSuccess
    }

    private class CacheMediaDataSource(
        private val delegate: DataSource,
        private val uri: Uri,
        private val cacheKey: String,
        private val readableLength: Long,
    ) : MediaDataSource() {
        private var opened = false
        private var nextPosition = 0L
        private var closed = false

        @Synchronized
        override fun readAt(position: Long, buffer: ByteArray?, offset: Int, size: Int): Int {
            if (buffer == null || size == 0) return 0
            if (position < 0) return -1
            if (closed) throw IOException("Audio cache source is closed")
            if (readableLength != C.LENGTH_UNSET.toLong() && position >= readableLength) return -1
            try {
                if (!opened || position != nextPosition) {
                    closeReader()
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setKey(cacheKey)
                        .setPosition(position)
                        .build()
                    opened = true
                    delegate.open(dataSpec)
                    nextPosition = position
                }
                val bytesRead = delegate.read(buffer, offset, size)
                if (bytesRead > 0) nextPosition += bytesRead
                return bytesRead
            } catch (error: Exception) {
                runCatching { closeReader() }
                throw error
            }
        }

        override fun getSize(): Long = readableLength

        @Synchronized
        @Throws(IOException::class)
        override fun close() {
            closed = true
            closeReader()
        }

        private fun closeReader() {
            if (!opened) return
            opened = false
            delegate.close()
        }
    }
}
