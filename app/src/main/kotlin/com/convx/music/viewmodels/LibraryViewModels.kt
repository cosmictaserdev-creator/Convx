/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalCoroutinesApi::class)

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.convx.music.constants.AlbumFilter
import com.convx.music.constants.AlbumFilterKey
import com.convx.music.constants.AlbumSortDescendingKey
import com.convx.music.constants.AlbumSortType
import com.convx.music.constants.AlbumSortTypeKey
import com.convx.music.constants.ArtistFilter
import com.convx.music.constants.ArtistFilterKey
import com.convx.music.constants.ArtistSongSortDescendingKey
import com.convx.music.constants.ArtistSongSortType
import com.convx.music.constants.ArtistSongSortTypeKey
import com.convx.music.constants.ArtistSortDescendingKey
import com.convx.music.constants.ArtistSortType
import com.convx.music.constants.ArtistSortTypeKey
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideYoutubeShortsKey
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.constants.PlaylistSortDescendingKey
import com.convx.music.constants.PlaylistSortType
import com.convx.music.constants.PlaylistSortTypeKey
import com.convx.music.constants.SongFilter
import com.convx.music.constants.SongFilterKey
import com.convx.music.constants.SongSortDescendingKey
import com.convx.music.constants.SongSortType
import com.convx.music.constants.SongSortTypeKey
import com.convx.music.constants.TopSize
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Song
import com.convx.music.extensions.filterExplicit
import com.convx.music.extensions.filterExplicitAlbums
import com.convx.music.extensions.filterVideoSongs
import com.convx.music.extensions.filterYoutubeShorts
import com.convx.music.extensions.toEnum
import com.convx.music.library.ObserveLibraryPlaylists
import com.convx.music.library.ObservePlaylist
import com.convx.music.library.PlaylistOrder
import com.convx.music.library.PlaylistCommands
import com.convx.music.playback.DownloadUtil
import com.convx.music.utils.LocalAudioScanner
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.convx.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

private data class SongLibraryOptions(
    val filter: SongFilter,
    val sortType: SongSortType,
    val descending: Boolean,
    val hideExplicit: Boolean,
    val hideVideoSongs: Boolean,
)

data class LibrarySongsSnapshot(
    val filter: SongFilter,
    val sortType: SongSortType,
    val descending: Boolean,
    val hideExplicit: Boolean,
    val hideVideoSongs: Boolean,
    val songs: List<Song>,
)

private data class AlbumLibraryOptions(
    val filter: AlbumFilter,
    val sortType: AlbumSortType,
    val descending: Boolean,
    val hideExplicit: Boolean,
)

private data class PlaylistLibraryOptions(
    val sortType: PlaylistSortType,
    val descending: Boolean,
    val hideYoutubeShorts: Boolean,
    val localOnly: Boolean,
)

private data class ArtistSongOptions(
    val sortType: ArtistSongSortType,
    val descending: Boolean,
    val hideExplicit: Boolean,
    val hideVideoSongs: Boolean,
)

class LibrarySongsViewModel(
    private val context: Context,
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
            .map { preferences ->
                SongLibraryOptions(
                    // Keep the saved filter so it returns when local-only mode is turned off.
                    filter = if (preferences[LocalOnlyModeKey] == true) SongFilter.LOCAL
                    else preferences[SongFilterKey].toEnum(SongFilter.LIKED),
                    sortType = preferences[SongSortTypeKey].toEnum(SongSortType.CREATE_DATE),
                    descending = preferences[SongSortDescendingKey] ?: true,
                    hideExplicit = preferences[HideExplicitKey] ?: false,
                    hideVideoSongs = (preferences[HideVideoSongsKey] ?: false) ||
                        (preferences[DataSaverEnabledKey] ?: false),
                )
            }.distinctUntilChanged()
            .flatMapLatest { options ->
                val songs = when (options.filter) {
                    SongFilter.LIBRARY -> database.songs(options.sortType, options.descending)
                    SongFilter.LIKED -> database.likedSongs(options.sortType, options.descending)
                    SongFilter.DOWNLOADED -> database.downloadedSongs(options.sortType, options.descending)
                    SongFilter.UPLOADED -> database.uploadedSongs(options.sortType, options.descending)
                    SongFilter.LOCAL -> database.localSongs(options.sortType, options.descending)
                }
                songs
                    .map {
                        LibrarySongsSnapshot(
                            filter = options.filter,
                            sortType = options.sortType,
                            descending = options.descending,
                            hideExplicit = options.hideExplicit,
                            hideVideoSongs = options.hideVideoSongs,
                            songs = it.filterExplicit(options.hideExplicit)
                                .filterVideoSongs(options.hideVideoSongs),
                        )
                    }
                    .onStart {
                        // Clear the previous query as soon as a filter or sort changes.
                        // The UI also checks this snapshot's options before exposing rows,
                        // so a stale list can never be played during the preference handoff.
                        emit(
                            LibrarySongsSnapshot(
                                filter = options.filter,
                                sortType = options.sortType,
                                descending = options.descending,
                                hideExplicit = options.hideExplicit,
                                hideVideoSongs = options.hideVideoSongs,
                                songs = emptyList(),
                            ),
                        )
                    }
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                LibrarySongsSnapshot(
                    filter = SongFilter.LIKED,
                    sortType = SongSortType.CREATE_DATE,
                    descending = true,
                    hideExplicit = false,
                    hideVideoSongs = false,
                    songs = emptyList(),
                ),
            )

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

class LibraryArtistsViewModel(
    context: Context,
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
class LibraryAlbumsViewModel(
    context: Context,
    database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    private val albumOptions =
        context.dataStore.data
            .map { preferences ->
                AlbumLibraryOptions(
                    // Keep the saved filter so it returns when local-only mode is turned off.
                    filter = if (preferences[LocalOnlyModeKey] == true) AlbumFilter.LOCAL
                    else preferences[AlbumFilterKey].toEnum(AlbumFilter.LIKED),
                    sortType = preferences[AlbumSortTypeKey].toEnum(AlbumSortType.CREATE_DATE),
                    descending = preferences[AlbumSortDescendingKey] ?: true,
                    hideExplicit = preferences[HideExplicitKey] ?: false,
                )
            }.distinctUntilChanged()

    /** Device albums, listed under the online ones with a divider. */
    val localAlbums =
        albumOptions
            .flatMapLatest { options ->
                database.albumsLocal(options.sortType, options.descending)
                    .map { it.filterExplicitAlbums(options.hideExplicit) }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allAlbums =
        albumOptions
            .flatMapLatest { options ->
                val albums = when (options.filter) {
                    AlbumFilter.LIKED -> database.albumsLiked(options.sortType, options.descending)
                    AlbumFilter.LIBRARY -> database.albums(options.sortType, options.descending)
                    AlbumFilter.UPLOADED -> database.albumsUploaded(options.sortType, options.descending)
                    AlbumFilter.LOCAL -> database.albumsLocal(options.sortType, options.descending)
                }
                albums.map { it.filterExplicitAlbums(options.hideExplicit) }
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

class LibraryPlaylistsViewModel(
    context: Context,
    private val observeLibraryPlaylists: ObserveLibraryPlaylists,
    private val observePlaylist: ObservePlaylist,
    private val playlistCommands: PlaylistCommands,
    private val syncUtils: SyncUtils,
) : ViewModel() {
    val allPlaylists =
        context.dataStore.data
            .map { preferences ->
                PlaylistLibraryOptions(
                    sortType = preferences[PlaylistSortTypeKey].toEnum(PlaylistSortType.CREATE_DATE),
                    descending = preferences[PlaylistSortDescendingKey] ?: true,
                    hideYoutubeShorts = preferences[HideYoutubeShortsKey] ?: false,
                    localOnly = preferences[LocalOnlyModeKey] == true,
                )
            }.distinctUntilChanged()
            .flatMapLatest { options ->
                observeLibraryPlaylists(
                    order = options.sortType.toPlaylistOrder(),
                    descending = options.descending,
                    hideYoutubeShorts = options.hideYoutubeShorts,
                    localOnly = options.localOnly,
                )
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun sync() {
        viewModelScope.launch(Dispatchers.IO) { syncUtils.syncSavedPlaylists() }
    }

    suspend fun loadPlaylistForMenu(playlistId: String) =
        observePlaylist(playlistId).first()

    suspend fun togglePin(playlistId: String): Boolean =
        playlistCommands.togglePin(playlistId)

    val topValue =
        context.dataStore.data
            .map { it[TopSize] ?: "50" }
            .distinctUntilChanged()

    private fun PlaylistSortType.toPlaylistOrder(): PlaylistOrder = when (this) {
        PlaylistSortType.CREATE_DATE -> PlaylistOrder.CREATED
        PlaylistSortType.NAME -> PlaylistOrder.NAME
        PlaylistSortType.SONG_COUNT -> PlaylistOrder.SONG_COUNT
        PlaylistSortType.LAST_UPDATED -> PlaylistOrder.LAST_UPDATED
    }
}

class ArtistSongsViewModel(
    context: Context,
    database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val artistId = requireNotNull(savedStateHandle.get<String>("artistId")) { "Missing artistId" }
    val artist =
        database
            .artist(artistId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val songs =
        context.dataStore.data
            .map { preferences ->
                ArtistSongOptions(
                    sortType = preferences[ArtistSongSortTypeKey].toEnum(ArtistSongSortType.CREATE_DATE),
                    descending = preferences[ArtistSongSortDescendingKey] ?: true,
                    hideExplicit = preferences[HideExplicitKey] ?: false,
                    hideVideoSongs = (preferences[HideVideoSongsKey] ?: false) ||
                        (preferences[DataSaverEnabledKey] ?: false),
                )
            }.distinctUntilChanged()
            .flatMapLatest { options ->
                database.artistSongs(artistId, options.sortType, options.descending).map {
                    it.filterExplicit(options.hideExplicit).filterVideoSongs(options.hideVideoSongs)
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

class LibraryMixViewModel(
    context: Context,
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
