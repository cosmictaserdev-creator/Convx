/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** What a file says about itself, read from its embedded tags rather than guessed from its name. */
data class NetworkTags(
    val title: String?,
    val artist: String?,
    val albumArtist: String?,
    val album: String?,
    val trackNumber: Int?,
    val durationSeconds: Int?,
    val artwork: ByteArray?,
)

/**
 * Reads ID3, Vorbis/FLAC and MP4 tags (and the embedded cover) from an SMB or WebDAV file.
 *
 * It goes through [dataSourceFactory], the same network-aware factory playback uses, so a
 * `convx-network://` URI resolves exactly as it does when the song is played. ExoPlayer's
 * extractors only fetch the header bytes they need, so this does not download whole files.
 */
@UnstableApi
class NetworkTagReader(
    private val context: Context,
    private val dataSourceFactory: DataSource.Factory,
) {
    /** Null when the file has no readable tags, cannot be reached, or takes longer than [timeoutMs]. */
    suspend fun read(uri: String, timeoutMs: Long = 20_000): NetworkTags? = withTimeoutOrNull(timeoutMs) {
        try {
            readInternal(uri)
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun readInternal(uri: String): NetworkTags? {
        val retriever = MetadataRetriever.Builder(context, MediaItem.fromUri(uri))
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
        retriever.use { source ->
            val groups = source.retrieveTrackGroups().awaitResult()
            val durationUs = runCatching { source.retrieveDurationUs().awaitResult() }.getOrNull()
            val builder = MediaMetadata.Builder()
            for (group in 0 until groups.length) {
                val trackGroup = groups[group]
                for (track in 0 until trackGroup.length) {
                    val metadata = trackGroup.getFormat(track).metadata ?: continue
                    for (entry in 0 until metadata.length()) metadata[entry].populateMediaMetadata(builder)
                }
            }
            val tags = builder.build()
            val duration = durationUs?.takeIf { it > 0 && it != androidx.media3.common.C.TIME_UNSET }
                ?.let { (it / 1_000_000L).toInt() }
            val result = NetworkTags(
                title = tags.title?.toString()?.trim()?.takeIf(String::isNotEmpty),
                artist = tags.artist?.toString()?.trim()?.takeIf(String::isNotEmpty),
                albumArtist = tags.albumArtist?.toString()?.trim()?.takeIf(String::isNotEmpty),
                album = tags.albumTitle?.toString()?.trim()?.takeIf(String::isNotEmpty),
                trackNumber = tags.trackNumber?.takeIf { it > 0 },
                durationSeconds = duration,
                artwork = tags.artworkData?.takeIf { it.isNotEmpty() },
            )
            val empty = result.title == null && result.artist == null && result.album == null &&
                result.albumArtist == null && result.artwork == null && result.trackNumber == null && duration == null
            return if (empty) null else result
        }
    }
}

private val DirectExecutor = Executor { it.run() }

private suspend fun <T> ListenableFuture<T>.awaitResult(): T = suspendCancellableCoroutine { continuation ->
    addListener({
        try {
            continuation.resume(get())
        } catch (failure: Exception) {
            continuation.resumeWithException(failure.cause ?: failure)
        }
    }, DirectExecutor)
    continuation.invokeOnCancellation { cancel(true) }
}
