/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.queues

import androidx.media3.common.MediaItem
import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import com.convx.music.db.entities.AlbumWithSongs
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.MediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class LocalAlbumRadio(
    private val albumWithSongs: AlbumWithSongs,
    private val startIndex: Int = 0,
    @Volatile private var playlistId: String? = null,
    @Volatile private var continuation: String? = null,
    @Volatile private var firstTimeLoaded: Boolean = false,
    private var restoredStatus: Queue.Status? = null,
    private val initialSongs: List<com.convx.music.db.entities.Song>? = null,
) : Queue {
    override val preloadItem: MediaMetadata? = null

    private val endpoint: WatchEndpoint
        get() = WatchEndpoint(
            playlistId = requireNotNull(playlistId)
        )

    override suspend fun getInitialStatus(): Queue.Status {
        restoredStatus?.let {
            restoredStatus = null
            return it
        }

        return withContext(IO) {
            Queue.Status(
                title = albumWithSongs.album.title,
                items = (initialSongs ?: albumWithSongs.songs).map { it.toMediaItem() },
                mediaItemIndex = startIndex
            )
        }
    }

    override fun hasNextPage(): Boolean = !albumWithSongs.album.isLocal && (!firstTimeLoaded || continuation != null)

    fun persistenceData() = QueueState(
        albumId = albumWithSongs.album.id,
        startIndex = startIndex,
        playlistId = playlistId,
        continuation = continuation,
        firstTimeLoaded = firstTimeLoaded,
    )

    override suspend fun nextPage(): List<MediaItem> = withContext(IO) {
        if (albumWithSongs.album.isLocal) return@withContext emptyList()
        if (!firstTimeLoaded) {
            playlistId = playlistId ?: YouTube.album(albumWithSongs.album.id).getOrThrow().album.playlistId
            val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
            continuation = nextResult.continuation
            firstTimeLoaded = true
            return@withContext nextResult.items.subList(
                albumWithSongs.songs.size.coerceAtMost(nextResult.items.size),
                nextResult.items.size
            ).map { it.toMediaItem() }
        }
        val nextResult = YouTube.next(endpoint, continuation).getOrThrow()
        continuation = nextResult.continuation
        nextResult.items.map { it.toMediaItem() }
    }

    data class QueueState(
        val albumId: String,
        val startIndex: Int,
        val playlistId: String?,
        val continuation: String?,
        val firstTimeLoaded: Boolean,
    )
}
