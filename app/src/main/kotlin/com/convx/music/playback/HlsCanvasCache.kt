package com.convx.music.playback

import android.net.Uri
import android.os.SystemClock
import androidx.media3.common.C
import androidx.media3.common.MimeTypes
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UriUtil
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.PlaceholderDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.ContentMetadata
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import com.convx.music.canvas.canvasCacheKey
import java.lang.ref.WeakReference

/** Verifies one complete video rendition with a cache-only source; never fetches missing bytes. */
internal object HlsCanvasCache {
    private data class Resource(val key: String, val length: Long, val spans: List<Pair<Long, Long>>)
    private data class Verification(val cache: WeakReference<Cache>, val resources: List<Resource>,
        val keys: List<StreamKey>?, val checkedAt: Long)
    private val verified = object : LinkedHashMap<String, Verification>(32, .75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Verification>?) = size > 32
    }
    private fun id(mediaId: String, url: String) = canvasCacheKey(mediaId, url)
    private fun snapshot(mediaId: String, cache: Cache): List<Resource> = cache.keys.asSequence()
        .filter { it.startsWith("$mediaId#canvas-v2:") }.sorted().map { key ->
            Resource(key, ContentMetadata.getContentLength(cache.getContentMetadata(key)),
                cache.getCachedSpans(key).map { it.position to it.length })
        }.toList()

    /** Called only after an IO-thread verification. This lookup does not touch the disk. */
    fun preparedSelection(mediaId: String, url: String): List<StreamKey>? = synchronized(verified) {
        verified[id(mediaId, url)]?.takeIf { it.cache.get() != null }?.keys
    }

    fun completedSelection(mediaId: String, url: String, cache: Cache): List<StreamKey>? {
        val id = id(mediaId, url)
        if (!cache.isCachedToEnd(canvasCacheKey(mediaId, url), 0)) {
            synchronized(verified) { if (verified[id]?.cache?.get() === cache) verified.remove(id) }
            return null
        }
        val before = snapshot(mediaId, cache)
        synchronized(verified) {
            verified[id]?.takeIf { it.cache.get() === cache && it.resources == before &&
                (it.keys != null || SystemClock.elapsedRealtime() - it.checkedAt < 30_000) }?.let { return it.keys }
        }
        val result = runCatching {
            val source = CacheDataSource.Factory().setCache(cache).setCacheKeyFactory(canvasSegmentKeyFactory(mediaId))
                .setUpstreamDataSourceFactory(PlaceholderDataSource.FACTORY).setCacheWriteDataSinkFactory(null)
            fun parse(uri: Uri, parent: HlsMultivariantPlaylist? = null): androidx.media3.exoplayer.hls.playlist.HlsPlaylist {
                if (!cache.isCachedToEnd(canvasCacheKey(mediaId, uri.toString()), 0)) throw java.io.IOException("Incomplete HLS playlist")
                return DataSourceInputStream(source.createDataSource(), DataSpec(uri)).use {
                    (if (parent == null) HlsPlaylistParser() else HlsPlaylistParser(parent, null)).parse(uri, it)
                }
            }
            fun resource(uri: Uri, position: Long = 0, length: Long = C.LENGTH_UNSET.toLong()): Boolean {
                val key = canvasCacheKey(mediaId, uri.toString())
                return if (length == C.LENGTH_UNSET.toLong()) cache.isCachedToEnd(key, position)
                else length > 0 && cache.isCached(key, position, length)
            }
            fun completeMedia(media: HlsMediaPlaylist): Boolean {
                if (!media.hasEndTag || media.segments.none { !it.hasGapTag } || media.protectionSchemes != null) return false
                fun segment(item: HlsMediaPlaylist.Segment): Boolean {
                    if (item.drmInitData != null) return false
                    if (item.fullSegmentEncryptionKeyUri?.let { !resource(UriUtil.resolveToUri(media.baseUri, it)) } == true) return false
                    return resource(UriUtil.resolveToUri(media.baseUri, item.url), item.byteRangeOffset, item.byteRangeLength)
                }
                return media.segments.all { it.hasGapTag || ((it.initializationSegment?.let(::segment) ?: true) && segment(it)) }
            }
            when (val root = parse(Uri.parse(url))) {
                is HlsMediaPlaylist -> if (completeMedia(root)) emptyList() else null
                is HlsMultivariantPlaylist -> root.variants.withIndex().sortedByDescending { it.value.format.bitrate }
                    .firstNotNullOfOrNull { variant ->
                        val codecs = variant.value.format.codecs
                        if (MimeTypes.getAudioMediaMimeType(codecs) != null && MimeTypes.getVideoMediaMimeType(codecs) == null) null
                        else runCatching {
                            if ((parse(variant.value.url, root) as? HlsMediaPlaylist)?.let(::completeMedia) == true)
                                listOf(StreamKey(HlsMultivariantPlaylist.GROUP_INDEX_VARIANT, variant.index)) else null
                        }.getOrNull()
                    }
                else -> null
            }
        }.getOrNull()
        if (snapshot(mediaId, cache) != before) return null
        synchronized(verified) { verified[id] = Verification(WeakReference(cache), before, result, SystemClock.elapsedRealtime()) }
        return result
    }
}
