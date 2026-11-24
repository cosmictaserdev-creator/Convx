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
import okio.Buffer

/** Loads `convx-network://` cover art through the SMB or WebDAV source it lives on. */
class NetworkArtworkFetcher(
    private val uri: String,
    private val options: Options,
    private val repository: NetworkLibraryRepository,
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        val bytes = repository.readArtwork(uri)
        return SourceFetchResult(
            source = ImageSource(Buffer().apply { write(bytes) }, options.fileSystem),
            mimeType = null,
            dataSource = DataSource.NETWORK,
        )
    }

    class Factory(private val repository: () -> NetworkLibraryRepository) : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.startsWith("${NetworkMediaUri.Scheme}://")) NetworkArtworkFetcher(data, options, repository()) else null
    }
}
