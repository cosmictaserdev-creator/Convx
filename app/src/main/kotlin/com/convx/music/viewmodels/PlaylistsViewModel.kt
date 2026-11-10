/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.AddToPlaylistSortDescendingKey
import com.convx.music.constants.AddToPlaylistSortTypeKey
import com.convx.music.constants.PlaylistSortType
import com.convx.music.extensions.toEnum
import com.convx.music.library.ObservePlaylists
import com.convx.music.library.PlaylistCommands
import com.convx.music.library.PlaylistOrder
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlaylistsViewModel(
    context: Context,
    private val observePlaylists: ObservePlaylists,
    private val playlistCommands: PlaylistCommands,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val allPlaylists =
        context.dataStore.data
            .map {
                it[AddToPlaylistSortTypeKey].toEnum(PlaylistSortType.CREATE_DATE) to (it[AddToPlaylistSortDescendingKey]
                    ?: true)
            }.distinctUntilChanged()
            .flatMapLatest { (sortType, descending) ->
                observePlaylists(sortType.toPlaylistOrder(), descending)
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    suspend fun sync() {
        syncUtils.syncSavedPlaylists()
    }

    suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String> =
        playlistCommands.duplicateSongIds(playlistId, songIds)

    suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean =
        playlistCommands.addSongs(playlistId, songIds)

    private fun PlaylistSortType.toPlaylistOrder(): PlaylistOrder = when (this) {
        PlaylistSortType.CREATE_DATE -> PlaylistOrder.CREATED
        PlaylistSortType.NAME -> PlaylistOrder.NAME
        PlaylistSortType.SONG_COUNT -> PlaylistOrder.SONG_COUNT
        PlaylistSortType.LAST_UPDATED -> PlaylistOrder.LAST_UPDATED
    }
}
