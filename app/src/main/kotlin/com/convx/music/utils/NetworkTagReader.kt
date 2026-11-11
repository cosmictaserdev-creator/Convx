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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull

/** Reads tags and the embedded picture through the platform retriever, straight off the share. */
class AndroidNetworkTagReader : NetworkTagReader {

    // A stalled share blocks the retriever on a thread nothing can interrupt, so the wait is abandoned
    // after a while and that one file goes untagged instead of freezing the whole import.
    private val reads = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun read(file: RemoteFile, size: Long): NetworkTags? {
        val pending = reads.async { readBlocking(file, size) }
        return withTimeoutOrNull(ReadTimeoutMs) { pending.await() }.also { if (it == null) pending.cancel() }
    }

    private fun readBlocking(file: RemoteFile, size: Long): NetworkTags? {
        val retriever = MediaMetadataRetriever()
        return try {
            // WebDAV listings need not provide getcontentlength. The first ranged
            // read lets the response supply it; MediaDataSource also accepts -1.
            if (size <= 0 && file.length <= 0) file.read(0, ByteArray(1), 0, 1)
            val length = size.takeIf { it > 0 } ?: file.length.takeIf { it > 0 } ?: -1L
            retriever.setDataSource(RemoteMediaDataSource(file, length))
            fun text(key: Int) = retriever.extractMetadata(key)?.trim()?.takeIf(String::isNotEmpty)
            // "3/12" and "1/2" are the usual shape for both.
            fun number(key: Int) = text(key)?.substringBefore('/')?.trim()?.toIntOrNull()
            NetworkTags(
                title = text(MediaMetadataRetriever.METADATA_KEY_TITLE),
                artist = text(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                albumArtist = text(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST),
                album = text(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                trackNumber = number(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER),
                discNumber = number(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER),
                year = text(MediaMetadataRetriever.METADATA_KEY_YEAR)?.take(4)?.toIntOrNull(),
                durationSeconds = text(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?.let { (it / 1000).toInt() },
                picture = retriever.embeddedPicture,
            )
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    private companion object {
        const val ReadTimeoutMs = 20_000L
    }

    private class RemoteMediaDataSource(private val file: RemoteFile, private val total: Long) : MediaDataSource() {
        override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int =
            if (total >= 0 && position >= total) -1 else file.read(position, buffer, offset, size)

        override fun getSize(): Long = total

        // The importer owns the file and closes it.
        override fun close() = Unit
    }
}
