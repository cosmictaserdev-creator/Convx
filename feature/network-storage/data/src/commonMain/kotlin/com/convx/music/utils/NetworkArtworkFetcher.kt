/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.utils

import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.convx.storage.domain.NetworkLibraryRepository
import com.convx.storage.domain.NetworkMediaUri
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okio.Buffer

/** Loads `convx-network://` cover art through the SMB or WebDAV source it lives on. */
class NetworkArtworkFetcher(
    private val uri: String,
    private val options: Options,
    private val repository: NetworkLibraryRepository,
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        // A scrolling library asks for dozens of covers at once, and each read buffers the whole image in memory
        // twice over. Two at a time keeps memory flat; a cover too big to be a thumbnail is skipped, not loaded.
        val bytes = gate.withPermit { repository.readArtwork(uri, MAX_ARTWORK_BYTES) }
        return SourceFetchResult(
            source = ImageSource(Buffer().apply { write(bytes) }, options.fileSystem),
            mimeType = null,
            dataSource = DataSource.NETWORK,
        )
    }

    private companion object {
        const val MAX_ARTWORK_BYTES = 6 * 1024 * 1024
        val gate = Semaphore(2)
    }

    class Factory(private val repository: () -> NetworkLibraryRepository) : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.startsWith("${NetworkMediaUri.Scheme}://")) NetworkArtworkFetcher(data, options, repository()) else null
    }
}
