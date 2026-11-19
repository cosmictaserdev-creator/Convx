/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import android.media.MediaDataSource
import android.media.MediaMetadataRetriever
import com.convx.storage.domain.NetworkTagReader
import com.convx.storage.domain.NetworkTags
import com.convx.storage.domain.RemoteFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeoutOrNull

/** Reads tags and the embedded picture through the platform retriever, straight off the share. */
class AndroidNetworkTagReader : NetworkTagReader {

    override suspend fun read(file: RemoteFile, size: Long): NetworkTags? =
        withTimeoutOrNull(ReadTimeoutMs) {
            val job = currentCoroutineContext()[Job]
            runInterruptible(Dispatchers.IO) { readBlocking(file, size, job) }
        }

    private fun readBlocking(file: RemoteFile, size: Long, job: Job?): NetworkTags? {
        val retriever = MediaMetadataRetriever()
        return try {
            // WebDAV listings need not provide getcontentlength. The first ranged
            // read lets the response supply it; MediaDataSource also accepts -1.
            if (size <= 0 && file.length <= 0) file.read(0, ByteArray(1), 0, 1)
            val length = size.takeIf { it > 0 } ?: file.length.takeIf { it > 0 } ?: -1L
            retriever.setDataSource(RemoteMediaDataSource(file, length, job))
            fun text(key: Int) = retriever.extractMetadata(key)?.trim()?.takeIf(String::isNotEmpty)
            // "3/12" and "1/2" are the usual shape for both.
            fun number(key: Int) = text(key)?.substringBefore('/')?.trim()?.toIntOrNull()?.takeIf { it > 0 }
            NetworkTags(
                title = text(MediaMetadataRetriever.METADATA_KEY_TITLE),
                artist = text(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                albumArtist = text(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
                album = text(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                trackNumber = number(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER),
                discNumber = number(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER),
                year = text(MediaMetadataRetriever.METADATA_KEY_YEAR)?.take(4)?.toIntOrNull(),
                durationSeconds = text(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?.let { (it / 1000).coerceIn(0, Int.MAX_VALUE.toLong()).toInt() },
                picture = retriever.embeddedPicture?.takeIf { it.size in 1..EmbeddedArt.MAX_PICTURE_BYTES },
            )
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    private companion object {
        const val ReadTimeoutMs = 20_000L
        const val MaxTagReadBytes = 16 * 1024 * 1024
    }

    private class RemoteMediaDataSource(
        private val file: RemoteFile,
        private val total: Long,
        private val job: Job?,
    ) : MediaDataSource() {
        private var remaining = MaxTagReadBytes

        @Synchronized
        override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
            job?.ensureActive()
            if (remaining == 0 || (total >= 0 && position >= total)) return -1
            val read = file.read(position, buffer, offset, minOf(size, remaining))
            if (read > 0) remaining -= read
            return read
        }

        override fun getSize(): Long = total

        // The importer owns the file and closes it.
        override fun close() = Unit
    }
}
