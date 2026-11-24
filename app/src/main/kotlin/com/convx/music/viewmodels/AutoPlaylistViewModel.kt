/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.SongSortDescendingKey
import com.convx.music.constants.SongSortType
import com.convx.music.constants.SongSortTypeKey
import com.convx.music.db.MusicDatabase
import com.convx.music.extensions.filterExplicit
import com.convx.music.extensions.filterVideoSongs
import com.convx.music.extensions.toEnum
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class AutoPlaylistViewModel(
    context: Context,
    private val database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val playlist = requireNotNull(savedStateHandle.get<String>("playlist")) { "Missing playlist" }
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val likedSongs =
        context.dataStore.data
            .map {
                Triple(
                    it[SongSortTypeKey].toEnum(SongSortType.CREATE_DATE) to (it[SongSortDescendingKey]
                        ?: true),
                    it[HideExplicitKey] ?: false,
                    (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false)
                )
            }
            .distinctUntilChanged()
            .flatMapLatest { (sortDesc, hideExplicit, hideVideoSongs) ->
                val (sortType, descending) = sortDesc
                when (playlist) {
                    "liked" -> database.likedSongs(sortType, descending)
                        .map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }

                    "podcasts" -> database.likedSongs(sortType, descending)
                        .map { songs -> songs.filter { it.song.isPodcastEpisode }.filterExplicit(hideExplicit) }

                    "downloaded" -> database.downloadedSongs(sortType, descending)
                        .map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }

                    "uploaded" -> database.uploadedSongs(sortType, descending)
                        .map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }

                    "local" -> database.localSongs(sortType, descending)
                        .map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }

                    else -> kotlinx.coroutines.flow.flowOf(emptyList())
                }
            }
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    fun syncLikedSongs() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncLikedSongs() }
    }

    fun syncUploadedSongs() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncUploadedSongs() }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            when (playlist) {
                "liked" -> syncUtils.syncLikedSongsSuspend()
                "podcasts" -> syncUtils.syncLikedSongsSuspend()
                "uploaded" -> syncUtils.syncUploadedSongsSuspend()
            }
            _isRefreshing.value = false
        }
    }

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    /** Scans device audio into the library (isLocal songs). Used by the "local" auto playlist. */
    fun scanLocal(context: Context) {
        if (_isScanning.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            try {
                com.convx.music.utils.LocalAudioScanner.scanAndInsert(context, database)
            } catch (e: Exception) {
                Timber.tag("AutoPlaylistViewModel").e(e, "local scan failed")
            } finally {
                _isScanning.value = false
            }
        }
    }
}
