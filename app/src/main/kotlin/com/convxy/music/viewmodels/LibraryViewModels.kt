/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.convxy.music.viewmodels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.convxy.music.constants.AlbumFilter
import com.convxy.music.constants.AlbumFilterKey
import com.convxy.music.constants.AlbumSortDescendingKey
import com.convxy.music.constants.AlbumSortType
import com.convxy.music.constants.AlbumSortTypeKey
import com.convxy.music.constants.ArtistFilter
import com.convxy.music.constants.ArtistFilterKey
import com.convxy.music.constants.ArtistSongSortDescendingKey
import com.convxy.music.constants.ArtistSongSortType
import com.convxy.music.constants.ArtistSongSortTypeKey
import com.convxy.music.constants.ArtistSortDescendingKey
import com.convxy.music.constants.ArtistSortType
import com.convxy.music.constants.ArtistSortTypeKey
import com.convxy.music.constants.HideExplicitKey
import com.convxy.music.constants.HideVideoSongsKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.constants.HideYoutubeShortsKey
import com.convxy.music.constants.LibraryFilter
import com.convxy.music.constants.LocalOnlyModeKey
import com.convxy.music.constants.PlaylistSortDescendingKey
import com.convxy.music.constants.PlaylistSortType
import com.convxy.music.constants.PlaylistSortTypeKey
import com.convxy.music.constants.SongFilter
import com.convxy.music.constants.SongFilterKey
import com.convxy.music.constants.SongSortDescendingKey
import com.convxy.music.constants.SongSortType
import com.convxy.music.constants.SongSortTypeKey
import com.convxy.music.constants.TopSize
import com.convxy.music.db.MusicDatabase
import com.convxy.music.extensions.filterExplicit
import com.convxy.music.extensions.filterExplicitAlbums
import com.convxy.music.extensions.filterVideoSongs
import com.convxy.music.extensions.filterYoutubeShorts
import com.convxy.music.extensions.toEnum
import com.convxy.music.playback.DownloadUtil
import com.convxy.music.utils.LocalAudioScanner
import com.convxy.music.utils.SyncUtils
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LibrarySongsViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    downloadUtil: DownloadUtil,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<LocalAudioScanner.ScanResult?>(null)
    val scanResult = _scanResult.asStateFlow()

    val allSongs =
        context.dataStore.data
            .map {
                Triple(
                    Triple(
                        // Local-only mode pins every library tab to its LOCAL filter.
                        // Doing it here rather than in the screen means the stored
                        // filter is left untouched and comes back when it's turned off.
                        if (it[LocalOnlyModeKey] == true) SongFilter.LOCAL
                        else it[SongFilterKey].toEnum(SongFilter.LIKED),
                        it[SongSortTypeKey].toEnum(SongSortType.CREATE_DATE),
                        (it[SongSortDescendingKey] ?: true),
                    ),
                    it[HideExplicitKey] ?: false,
                    (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false)
                )
            }.distinctUntilChanged()
            .flatMapLatest { (filterSort, hideExplicit, hideVideoSongs) ->
                val (filter, sortType, descending) = filterSort
                when (filter) {
                    SongFilter.LIBRARY -> database.songs(sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
                    SongFilter.LIKED -> database.likedSongs(sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
                    SongFilter.DOWNLOADED -> database.downloadedSongs(sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
                    SongFilter.UPLOADED -> database.uploadedSongs(sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
                    SongFilter.LOCAL -> database.localSongs(sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun scanLocalFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            _scanResult.value = null
            try {
                val result = LocalAudioScanner.scanAndInsert(context, database)
                _scanResult.value = result
            } catch (e: Exception) {
                _scanResult.value = LocalAudioScanner.ScanResult(0, 0, 0)
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
    }

    fun syncLikedSongs() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncLikedSongs() }
    }

    fun syncLibrarySongs() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncLibrarySongs() }
    }

    fun syncUploadedSongs() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncUploadedSongs() }
    }
}

@HiltViewModel
class LibraryArtistsViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val allArtists =
        context.dataStore.data
            .map {
                Triple(
                    if (it[LocalOnlyModeKey] == true) ArtistFilter.LOCAL
                    else it[ArtistFilterKey].toEnum(ArtistFilter.LIKED),
                    it[ArtistSortTypeKey].toEnum(ArtistSortType.CREATE_DATE),
                    it[ArtistSortDescendingKey] ?: true,
                )
            }.distinctUntilChanged()
            .flatMapLatest { (filter, sortType, descending) ->
                when (filter) {
                    ArtistFilter.LIKED -> database.artistsBookmarked(sortType, descending)
                    ArtistFilter.LIBRARY -> database.artists(sortType, descending)
                    ArtistFilter.LOCAL -> database.artistsLocal(sortType, descending)
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sync() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncArtistsSubscriptions() }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            allArtists.collect { artists ->
                artists
                    .map { it.artist }
                    // A local artist has no YouTube page to fetch — asking for one is
                    // a guaranteed round trip to nothing, and in local-only mode it is
                    // network traffic the mode exists to avoid.
                    .filter { !it.isLocal }
                    .filter {
                        it.thumbnailUrl == null || Duration.between(
                            it.lastUpdateTime,
                            LocalDateTime.now()
                        ) > Duration.ofDays(10)
                    }.forEach { artist ->
                        YouTube.artist(artist.id).onSuccess { artistPage ->
                            database.query {
                                update(artist, artistPage)
                            }
                        }
                    }
            }
        }
    }
}

@HiltViewModel
class LibraryAlbumsViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val allAlbums =
        context.dataStore.data
            .map {
                Pair(
                    Triple(
                        if (it[LocalOnlyModeKey] == true) AlbumFilter.LOCAL
                        else it[AlbumFilterKey].toEnum(AlbumFilter.LIKED),
                        it[AlbumSortTypeKey].toEnum(AlbumSortType.CREATE_DATE),
                        it[AlbumSortDescendingKey] ?: true,
                    ),
                    it[HideExplicitKey] ?: false
                )
            }.distinctUntilChanged()
            .flatMapLatest { (filterSort, hideExplicit) ->
                val (filter, sortType, descending) = filterSort
                when (filter) {
                    AlbumFilter.LIKED -> database.albumsLiked(sortType, descending).map { it.filterExplicitAlbums(hideExplicit) }
                    AlbumFilter.LIBRARY -> database.albums(sortType, descending).map { it.filterExplicitAlbums(hideExplicit) }
                    AlbumFilter.UPLOADED -> database.albumsUploaded(sortType, descending).map { it.filterExplicitAlbums(hideExplicit) }
                    AlbumFilter.LOCAL -> database.albumsLocal(sortType, descending).map { it.filterExplicitAlbums(hideExplicit) }
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sync() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncLikedAlbums() }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            allAlbums.collect { albums ->
                albums
                    .filter {
                        it.album.songCount == 0 && !it.album.isLocal
                    }.forEach { album ->
                        YouTube
                            .album(album.id)
                            .onSuccess { albumPage ->
                                database.query {
                                    update(album.album, albumPage, album.artists)
                                }
                            }.onFailure {
                                reportException(it)
                                if (it.message?.contains("NOT_FOUND") == true) {
                                    database.query {
                                        delete(album.album)
                                    }
                                }
                            }
                    }
            }
        }
    }
}

@HiltViewModel
class LibraryPlaylistsViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val allPlaylists =
        context.dataStore.data
            .map {
                Pair(
                    Triple(
                        it[PlaylistSortTypeKey].toEnum(PlaylistSortType.CREATE_DATE),
                        it[PlaylistSortDescendingKey] ?: true,
                        it[HideYoutubeShortsKey] ?: false
                    ),
                    it[LocalOnlyModeKey] == true,
                )
            }.distinctUntilChanged()
            .flatMapLatest { (sortAndHide, localOnly) ->
                val (sortType, descending, hideYoutubeShorts) = sortAndHide
                database.playlists(sortType, descending).map { playlists ->
                    // A synced YouTube playlist carries a browseId; the ones the user
                    // made here don't, and those are the only ones local-only shows.
                    playlists
                        .filter { !localOnly || it.playlist.browseId == null }
                        .filterYoutubeShorts(hideYoutubeShorts)
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sync() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncSavedPlaylists() }
    }

    val topValue =
        context.dataStore.data
            .map { it[TopSize] ?: "50" }
            .distinctUntilChanged()
}

@HiltViewModel
class ArtistSongsViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val artistId = savedStateHandle.get<String>("artistId")!!
    val artist =
        database
            .artist(artistId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val songs =
        context.dataStore.data
            .map {
                Triple(
                    it[ArtistSongSortTypeKey].toEnum(ArtistSongSortType.CREATE_DATE) to (it[ArtistSongSortDescendingKey]
                        ?: true),
                    it[HideExplicitKey] ?: false,
                    (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false)
                )
            }.distinctUntilChanged()
            .flatMapLatest { (sortDesc, hideExplicit, hideVideoSongs) ->
                val (sortType, descending) = sortDesc
                database.artistSongs(artistId, sortType, descending).map { it.filterExplicit(hideExplicit).filterVideoSongs(hideVideoSongs) }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

@HiltViewModel
class LibraryMixViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            syncUtils.performFullSyncSuspend()
            _isRefreshing.value = false
        }
    }

    val topValue =
        context.dataStore.data
            .map { it[TopSize] ?: "50" }
            .distinctUntilChanged()
    // The mixed Library view is built from liked/bookmarked YouTube rows; in
    // local-only mode the same three shelves read the on-device library instead.
    private val localOnly = context.dataStore.data
        .map { it[LocalOnlyModeKey] == true }
        .distinctUntilChanged()

    var artists = localOnly
        .flatMapLatest { local ->
            if (local) database.artistsLocal(ArtistSortType.NAME, false)
            else database.artistsBookmarked(ArtistSortType.CREATE_DATE, true)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    var albums = context.dataStore.data
        .map { (it[HideExplicitKey] ?: false) to (it[LocalOnlyModeKey] == true) }
        .distinctUntilChanged()
        .flatMapLatest { (hideExplicit, local) ->
            val source = if (local) database.albumsLocal(AlbumSortType.NAME, false)
            else database.albumsLiked(AlbumSortType.CREATE_DATE, true)
            source.map { it.filterExplicitAlbums(hideExplicit) }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    var playlists = context.dataStore.data
        .map { (it[HideYoutubeShortsKey] ?: false) to (it[LocalOnlyModeKey] == true) }
        .distinctUntilChanged()
        .flatMapLatest { (hideYoutubeShorts, local) ->
            database.playlists(PlaylistSortType.CREATE_DATE, true).map { playlists ->
                playlists
                    .filter { !local || it.playlist.browseId == null }
                    .filterYoutubeShorts(hideYoutubeShorts)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            albums.collect { albums ->
                albums
                    .filter {
                        it.album.songCount == 0 && !it.album.isLocal
                    }.forEach { album ->
                        YouTube
                            .album(album.id)
                            .onSuccess { albumPage ->
                                database.query {
                                    update(album.album, albumPage, album.artists)
                                }
                            }.onFailure {
                                reportException(it)
                                if (it.message?.contains("NOT_FOUND") == true) {
                                    database.query {
                                        delete(album.album)
                                    }
                                }
                            }
                    }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            artists.collect { artists ->
                artists
                    .map { it.artist }
                    .filter { !it.isLocal }
                    .filter {
                        it.thumbnailUrl == null ||
                                Duration.between(
                                    it.lastUpdateTime,
                                    LocalDateTime.now(),
                                ) > Duration.ofDays(10)
                    }.forEach { artist ->
                        YouTube.artist(artist.id).onSuccess { artistPage ->
                            database.query {
                                update(artist, artistPage)
                            }
                        }
                    }
            }
        }
    }
}

@HiltViewModel
class LibraryViewModel
@Inject
constructor() : ViewModel() {
    private val curScreen = mutableStateOf(LibraryFilter.LIBRARY)
    val filter: MutableState<LibraryFilter> = curScreen
}
