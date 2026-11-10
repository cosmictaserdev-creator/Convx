/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.library.PlaylistCommands
import com.convx.music.library.ObservePlaylist
import com.convx.music.library.PlaylistEntryReference
import com.convx.music.library.LocalPlaylistSongDataSource
import com.convx.music.library.LocalPlaylistSong
import com.convx.music.library.PlaylistDisplayPreferences
import com.convx.music.library.PlaylistDisplayPreferencesRepository
import com.convx.music.library.PendingPlaylistDeletionRepository
import com.convx.music.library.PlaylistTrack
import com.convx.music.library.PlaylistTrackOrder
import com.convx.music.library.orderPlaylistTracks
import com.convx.music.utils.SyncUtils
import com.music.innertube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

class LocalPlaylistViewModel(
    localPlaylistSongDataSource: LocalPlaylistSongDataSource,
    private val playlistDisplayPreferencesRepository: PlaylistDisplayPreferencesRepository,
    private val pendingPlaylistDeletionRepository: PendingPlaylistDeletionRepository,
    private val playlistCommands: PlaylistCommands,
    private val observePlaylist: ObservePlaylist,
    private val syncUtils: SyncUtils,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val playlistId = requireNotNull(savedStateHandle.get<String>("playlistId")) { "Missing playlistId" }
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }
    val playlistDetail = observePlaylist(playlistId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val playlistDisplayPreferences = playlistDisplayPreferencesRepository.observe()
        .stateIn(viewModelScope, SharingStarted.Lazily, PlaylistDisplayPreferences())
    val playlistSongs: StateFlow<List<LocalPlaylistSong>> =
        combine(
            localPlaylistSongDataSource.observeSongs(playlistId),
            playlistDisplayPreferences,
        ) { songs, preferences ->
            val collator = Collator.getInstance(Locale.getDefault()).apply {
                strength = Collator.PRIMARY
            }
            val orderedEntryIds = orderPlaylistTracks(
                tracks = songs.map(LocalPlaylistSong::track),
                order = preferences.order,
                descending = preferences.descending,
                hideVideos = preferences.hideVideos,
                compareText = { first, second -> collator.compare(first, second) },
            ).map(PlaylistTrack::entryId)
            val songsByEntryId = songs.associateBy { it.track.entryId }
            orderedEntryIds.mapNotNull(songsByEntryId::get)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setPlaylistTrackOrder(order: PlaylistTrackOrder) {
        viewModelScope.launch { playlistDisplayPreferencesRepository.setOrder(order) }
    }

    fun setPlaylistTrackDescending(descending: Boolean) {
        viewModelScope.launch { playlistDisplayPreferencesRepository.setDescending(descending) }
    }

    init {
        refresh()

        viewModelScope.launch(Dispatchers.IO) {
            playlistCommands.normalizePlaylistSongPositions(playlistId)
        }
    }

    fun updatePlaylist(name: String, description: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (playlistCommands.updatePlaylist(playlistId, name, description)) {
                observePlaylist(playlistId).first()?.browseId?.let { YouTube.renamePlaylist(it, name) }
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val browseId = observePlaylist(playlistId).first()?.browseId
            if (!playlistCommands.deletePlaylist(playlistId) || browseId == null) return@launch
            // Record remote deletion before the request so a concurrent saved-playlist
            // sync cannot restore the row while the request is in flight.
            pendingPlaylistDeletionRepository.recordPendingDelete(browseId)
            YouTube.deletePlaylist(browseId)
        }
    }

    fun clearPlaylistSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistCommands.clearPlaylistSongs(playlistId)
        }
    }

    fun movePlaylistSong(fromPosition: Int, toPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val order = playlistCommands.movePlaylistSong(playlistId, fromPosition, toPosition)
            val browseId = observePlaylist(playlistId).first()?.browseId ?: return@launch
            val successorIndex = if (fromPosition > toPosition) toPosition else toPosition + 1
            val successorSetVideoId = order.getOrNull(successorIndex)?.setVideoId
            order.getOrNull(fromPosition)?.setVideoId?.let { setVideoId ->
                YouTube.moveSongPlaylist(browseId, setVideoId, successorSetVideoId)
            }
        }
    }

    fun removePlaylistSong(entryId: Int, songId: String, position: Int, setVideoId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val removal = playlistCommands.removePlaylistSong(
                PlaylistEntryReference(
                    playlistId = playlistId,
                    id = entryId.toString(),
                    songId = songId,
                    position = position,
                    setVideoId = setVideoId,
                ),
            )
            if (!removal.removed) return@launch
            val browseId = observePlaylist(playlistId).first()?.browseId ?: return@launch
            removal.setVideoId?.let { setVideoId ->
                YouTube.removeFromPlaylist(browseId, songId, setVideoId)
            }
        }
    }

    /**
     * Re-syncs this playlist against YouTube. Bound to the pull-to-refresh gesture.
     * No-op for a purely local playlist, which has no browseId to sync against.
     */
    fun refresh() {
        viewModelScope.launch {
            observePlaylist(playlistId).first()?.browseId?.let { browseId ->
                syncUtils.syncPlaylist(browseId, playlistId)
            }
        }
    }
}
