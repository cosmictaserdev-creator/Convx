package com.convx.music.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.UriUtil
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.hls.playlist.HlsMediaPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsMultivariantPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Populates a finite HLS rendition, one whole segment at a time. */
@UnstableApi
internal class HlsAudioPrefetcher(
    private val mediaId: String,
    private val cache: Cache,
    private val factory: DataSource.Factory,
    private val keyForUrl: (String) -> String = { hlsAudioCacheKey(mediaId, it) },
    private val canvasVideo: Boolean = false,
) {
    private class Paused : Exception()

    suspend fun fill(manifestUrl: String, denominator: Int, preferLow: Boolean, canContinue: suspend () -> Boolean): Boolean {
        suspend fun checkReady() {
            currentCoroutineContext().ensureActive()
            if (!canContinue()) throw Paused()
        }
        suspend fun playlist(uri: Uri, parent: HlsMultivariantPlaylist? = null): HlsPlaylist {
            checkReady()
            val result = DataSourceInputStream(factory.createDataSource(), DataSpec(uri)).use {
                (if (parent == null) HlsPlaylistParser() else HlsPlaylistParser(parent, null)).parse(uri, it)
            }
            if (!cache.isCachedToEnd(keyForUrl(uri.toString()), 0)) throw Paused()
            return result
        }
        val seen = mutableSetOf<Triple<String, Long, Long>>()
        val buffer = ByteArray(64 * 1024)
        suspend fun resource(spec: DataSpec) {
            val identity = Triple(spec.uri.toString(), spec.position, spec.length)
            if (!seen.add(identity)) return
            checkReady()
            val source = factory.createDataSource()
            var readBytes = 0L
            try {
                source.open(spec)
                var sinceCheck = 0
                while (true) {
                    if (sinceCheck >= 512 * 1024) { checkReady(); sinceCheck = 0 }
                    val count = source.read(buffer, 0, buffer.size)
                    if (count == C.RESULT_END_OF_INPUT) break
                    if (count == 0) throw java.io.IOException("Empty HLS segment read")
                    readBytes += count
                    sinceCheck += count
                }
            } finally { source.close() }
            if (readBytes <= 0 || (spec.length != C.LENGTH_UNSET.toLong() && readBytes != spec.length)) {
                throw java.io.IOException("Incomplete HLS resource")
            }
            if (!cache.isCached(keyForUrl(spec.uri.toString()), spec.position, readBytes)) throw Paused()
        }
        suspend fun fillMedia(media: HlsMediaPlaylist) {
            if (!media.hasEndTag || media.durationUs <= 0 || media.protectionSchemes != null ||
                media.segments.any { it.drmInitData != null || it.initializationSegment?.drmInitData != null }) return
            val indices = hlsPrefetchSegmentIndices(media.segments.map { it.relativeStartTimeUs }, media.durationUs, denominator)
            suspend fun segment(segment: HlsMediaPlaylist.Segment) {
                segment.fullSegmentEncryptionKeyUri?.let {
                    resource(DataSpec(UriUtil.resolveToUri(media.baseUri, it)))
                }
                resource(DataSpec.Builder().setUri(UriUtil.resolveToUri(media.baseUri, segment.url))
                    .setPosition(segment.byteRangeOffset).setLength(segment.byteRangeLength).build())
            }
            for (index in indices) {
                val item = media.segments[index]
                if (item.hasGapTag) continue
                item.initializationSegment?.let { segment(it) }
                segment(item)
            }
        }
        return try {
            when (val root = playlist(Uri.parse(manifestUrl))) {
                is HlsMediaPlaylist -> fillMedia(root)
                is HlsMultivariantPlaylist -> {
                    if (canvasVideo) {
                        val variants = root.variants.filter { MimeTypes.getAudioMediaMimeType(it.format.codecs) == null ||
                            MimeTypes.getVideoMediaMimeType(it.format.codecs) != null }
                        val variantIndex = hlsPrefetchVariantIndex(variants.map { it.format.bitrate }, preferLow) ?: return true
                        (playlist(variants[variantIndex].url, root) as? HlsMediaPlaylist)?.let { fillMedia(it) }
                        return true
                    }
                    // Do not prefetch a video stream merely to warm audio playback.
                    val audioVariants = root.variants.filter { MimeTypes.getVideoMediaMimeType(it.format.codecs) == null }
                    val variantIndex = hlsPrefetchVariantIndex(audioVariants.map { it.format.bitrate }, preferLow) ?: return true
                    val variant = audioVariants[variantIndex]
                    val audio = root.audios.filter { it.groupId == variant.audioGroupId }
                        .sortedWith(compareByDescending<HlsMultivariantPlaylist.Rendition> {
                            it.format.selectionFlags and C.SELECTION_FLAG_DEFAULT != 0
                        }.thenByDescending { it.format.bitrate }).firstOrNull()
                    if (variant.audioGroupId != null && audio == null && root.muxedAudioFormat == null) return true
                    (playlist(variant.url, root) as? HlsMediaPlaylist)?.let { fillMedia(it) } ?: return true
                    audio?.url?.let { (playlist(it, root) as? HlsMediaPlaylist)?.let { media -> fillMedia(media) } }
                }
            }
            true
        } catch (_: Paused) {
            false
        } catch (error: java.io.IOException) {
            if (error.isCacheBudgetExhausted()) false else throw error
        }
    }
}
