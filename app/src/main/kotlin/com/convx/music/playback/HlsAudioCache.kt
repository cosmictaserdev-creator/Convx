package com.convx.music.playback

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import androidx.media3.common.C
import androidx.media3.common.MimeTypes
import androidx.media3.common.StreamKey
import androidx.media3.common.util.UriUtil
import androidx.media3.datasource.PlaceholderDataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.ContentMetadata
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import java.lang.ref.WeakReference

internal fun hlsAudioKeyFactory(mediaId: String) = CacheKeyFactory { spec ->
    hlsAudioCacheKey(mediaId, spec.uri.toString())
}

internal data class CachedHlsAudio(val streamKeys: List<StreamKey>)

internal object HlsAudioCache {
    private data class ResourceState(val key: String, val length: Long, val spans: List<Pair<Long, Long>>)
    private data class Verification(val cache: WeakReference<Cache>, val resources: List<ResourceState>, val selection: CachedHlsAudio?, val checkedAt: Long)
    private val verified = object : LinkedHashMap<String, Verification>(32, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Verification>?) = size > 32
    }

    private fun snapshot(mediaId: String, cache: Cache): List<ResourceState> = cache.keys.asSequence()
        .filter { it.startsWith("$mediaId#hls-audio:") }.sorted().map { key ->
            ResourceState(key, ContentMetadata.getContentLength(cache.getContentMetadata(key)),
                cache.getCachedSpans(key).map { it.position to it.length })
        }.toList()

    fun remember(context: Context, mediaId: String, manifestUrl: String, sourceKey: String) {
        context.getSharedPreferences("cached_hls_audio", Context.MODE_PRIVATE).edit()
            .putString(mediaId, manifestUrl).putString("$mediaId#source", sourceKey).apply()
    }

    fun savedUrl(context: Context, mediaId: String): String? =
        context.getSharedPreferences("cached_hls_audio", Context.MODE_PRIVATE).getString(mediaId, null)

    fun savedSource(context: Context, mediaId: String): String? =
        context.getSharedPreferences("cached_hls_audio", Context.MODE_PRIVATE).getString("$mediaId#source", null)

    fun forget(context: Context, mediaId: String) {
        context.getSharedPreferences("cached_hls_audio", Context.MODE_PRIVATE).edit()
            .remove(mediaId).remove("$mediaId#source").apply()
        synchronized(verified) { verified.keys.removeAll { it.startsWith("$mediaId#hls-audio:") } }
    }

    fun forgetAll(context: Context) {
        context.getSharedPreferences("cached_hls_audio", Context.MODE_PRIVATE).edit().clear().apply()
        synchronized(verified) { verified.clear() }
    }

    fun isComplete(mediaId: String, manifestUrl: String, cache: Cache): Boolean =
        completedSelection(mediaId, manifestUrl, cache) != null

    /** Find a complete finite rendition without opening the network. */
    fun completedSelection(mediaId: String, manifestUrl: String, cache: Cache): CachedHlsAudio? {
        if (!cache.isCachedToEnd(hlsAudioCacheKey(mediaId, manifestUrl), 0)) return null
        val key = hlsAudioCacheKey(mediaId, manifestUrl)
        val before = snapshot(mediaId, cache)
        synchronized(verified) {
            verified[key]?.takeIf { it.cache.get() === cache && it.resources == before &&
                (it.selection != null || SystemClock.elapsedRealtime() - it.checkedAt < FAILED_CHECK_RETRY_MS) }?.let { return it.selection }
        }
        val selection = runCatching {
            val source = CacheDataSource.Factory().setCache(cache)
                .setCacheKeyFactory(hlsAudioKeyFactory(mediaId))
                .setUpstreamDataSourceFactory(PlaceholderDataSource.FACTORY)
                .setCacheWriteDataSinkFactory(null)
            fun parse(uri: Uri, parent: HlsMultivariantPlaylist? = null): androidx.media3.exoplayer.hls.playlist.HlsPlaylist {
                if (!cache.isCachedToEnd(hlsAudioCacheKey(mediaId, uri.toString()), 0)) throw java.io.IOException("Incomplete HLS playlist")
                return DataSourceInputStream(source.createDataSource(), DataSpec(uri)).use {
                    (if (parent == null) HlsPlaylistParser() else HlsPlaylistParser(parent, null)).parse(uri, it)
                }
            }
            val playlist = parse(Uri.parse(manifestUrl))
            val checked = mutableSetOf<Triple<String, Long, Long>>()
            fun resource(uri: Uri, position: Long = 0, length: Long = C.LENGTH_UNSET.toLong()): Boolean {
                val resourceKey = hlsAudioCacheKey(mediaId, uri.toString())
                val identity = Triple(resourceKey, position, length)
                if (identity in checked) return true
                val complete = if (length == C.LENGTH_UNSET.toLong()) cache.isCachedToEnd(resourceKey, position)
                    else length > 0 && cache.isCached(resourceKey, position, length)
                if (complete) checked += identity
                return complete
            }
            fun completeMedia(media: HlsMediaPlaylist): Boolean {
                if (!media.hasEndTag || media.segments.none { !it.hasGapTag } || media.protectionSchemes != null) return false
                fun segment(item: HlsMediaPlaylist.Segment): Boolean {
                    if (item.drmInitData != null) return false
                    val key = item.fullSegmentEncryptionKeyUri
                    if (key != null && !resource(UriUtil.resolveToUri(media.baseUri, key))) return false
                    return resource(UriUtil.resolveToUri(media.baseUri, item.url), item.byteRangeOffset, item.byteRangeLength)
                }
                return media.segments.all { item ->
                    item.hasGapTag || ((item.initializationSegment?.let(::segment) ?: true) && segment(item))
                }
            }
            fun verify(keys: List<StreamKey>, mediaPlaylists: List<Uri>): CachedHlsAudio? = runCatching candidate@ {
                val parent = playlist as? HlsMultivariantPlaylist
                if (mediaPlaylists.any { (parse(it, parent) as? HlsMediaPlaylist)?.let(::completeMedia) != true }) return@candidate null
                CachedHlsAudio(keys)
            }.getOrNull()
            when (playlist) {
                is HlsMediaPlaylist -> if (completeMedia(playlist)) CachedHlsAudio(emptyList()) else null
                is HlsMultivariantPlaylist -> {
                    playlist.variants.withIndex().sortedWith(
                        compareBy<IndexedValue<HlsMultivariantPlaylist.Variant>> {
                            MimeTypes.getVideoMediaMimeType(it.value.format.codecs) != null
                        }.thenByDescending { it.value.format.bitrate },
                    ).firstNotNullOfOrNull { variant ->
                        val variantKey = StreamKey(HlsMultivariantPlaylist.GROUP_INDEX_VARIANT, variant.index)
                        val group = variant.value.audioGroupId
                        val renditions = playlist.audios.withIndex().filter { it.value.groupId == group }
                            .sortedWith(compareByDescending<IndexedValue<HlsMultivariantPlaylist.Rendition>> {
                                it.value.format.selectionFlags and C.SELECTION_FLAG_DEFAULT != 0
                            }.thenByDescending { it.value.format.bitrate })
                        if (renditions.isEmpty()) {
                            if (group != null && playlist.muxedAudioFormat == null) null
                            else verify(listOf(variantKey), listOf(variant.value.url))
                        } else {
                            renditions.firstNotNullOfOrNull { audio ->
                                val keys = listOf(variantKey, StreamKey(HlsMultivariantPlaylist.GROUP_INDEX_AUDIO, audio.index))
                                verify(keys, listOfNotNull(variant.value.url, audio.value.url))
                            }
                        }
                    }
                }
                else -> null
            }
        }.getOrNull()
        // Do not retain a result obtained while the cache was being edited or evicted.
        if (snapshot(mediaId, cache) != before) return null
        synchronized(verified) { verified[key] = Verification(WeakReference(cache), before, selection, SystemClock.elapsedRealtime()) }
        return selection
    }

    private const val FAILED_CHECK_RETRY_MS = 30_000L
}
