/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.convx.music.db.MusicDatabase
import com.convx.music.extensions.toMediaItem
import com.convx.music.utils.NetworkLibraryImporter
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.NetworkEntry
import com.convx.storage.domain.NetworkMedia
import com.convx.storage.domain.NetworkServer
import com.convx.storage.domain.RemotePath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Folder-by-folder browsing of one SMB or WebDAV source; songs are added to the library as they are played. */
class NetworkBrowserViewModel(
    private val repository: NetworkMusicRepository,
    private val database: MusicDatabase,
) : ViewModel() {

    data class Listing(
        val folder: String = "",
        val entries: List<NetworkEntry> = emptyList(),
        val loading: Boolean = true,
        val failed: Boolean = false,
    )

    val servers: StateFlow<List<NetworkServer>> = repository.servers

    private val _listing = MutableStateFlow(Listing())
    val listing: StateFlow<Listing> = _listing.asStateFlow()

    init {
        viewModelScope.launch { runCatching { repository.loadServers() } }
    }

    fun open(serverId: String, folder: String) {
        viewModelScope.launch {
            _listing.value = _listing.value.copy(loading = true, failed = false)
            _listing.value = runCatching { repository.browse(serverId, folder) }.fold(
                onSuccess = { entries ->
                    Listing(
                        folder = RemotePath.normalize(folder),
                        entries = entries.sortedWith(compareBy({ !it.directory }, { it.name.lowercase() })),
                        loading = false,
                    )
                },
                onFailure = { _listing.value.copy(loading = false, failed = true) },
            )
        }
    }

    /** Adds [files] to the library, if new, and returns them as queue items in the same order. */
    suspend fun mediaItems(serverId: String, files: List<NetworkEntry>): List<MediaItem> = files.mapNotNull { entry ->
        val track = NetworkMedia.track(serverId, entry, emptyList())
        NetworkLibraryImporter.insert(database, track)
        database.getSongById(track.id)?.toMediaItem()
    }
}
