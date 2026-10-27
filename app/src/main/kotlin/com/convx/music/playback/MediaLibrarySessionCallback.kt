/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaConstants
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.convx.music.R
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.MediaSessionConstants
import com.convx.music.constants.SongSortType
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.extensions.toggleRepeatMode
import com.convx.music.models.toMediaMetadata
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.guava.future

@androidx.annotation.OptIn(UnstableApi::class)
class MediaLibrarySessionCallback(
    val context: Context,
    val database: MusicDatabase,
    val downloadUtil: DownloadUtil,
) : MediaLibrarySession.Callback {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var service: MusicService
    var toggleLike: () -> Unit = {}
    var toggleStartRadio: () -> Unit = {}
    var toggleLibrary: () -> Unit = {}

    fun release() {
        scope.cancel()
        toggleLike = {}
        toggleStartRadio = {}
        toggleLibrary = {}
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        return MediaSession.ConnectionResult.accept(
            connectionResult.availableSessionCommands
                .buildUpon()
                .add(MediaSessionConstants.CommandToggleLike)
                .add(MediaSessionConstants.CommandToggleStartRadio)
                .add(MediaSessionConstants.CommandToggleLibrary)
                .add(MediaSessionConstants.CommandToggleShuffle)
                .add(MediaSessionConstants.CommandToggleRepeatMode)
                .build(),
            connectionResult.availablePlayerCommands,
        )
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            MediaSessionConstants.ACTION_TOGGLE_LIKE -> toggleLike()
            MediaSessionConstants.ACTION_TOGGLE_START_RADIO -> toggleStartRadio()
            MediaSessionConstants.ACTION_TOGGLE_LIBRARY -> toggleLibrary()
            MediaSessionConstants.ACTION_TOGGLE_SHUFFLE -> session.player.shuffleModeEnabled =
                !session.player.shuffleModeEnabled

            MediaSessionConstants.ACTION_TOGGLE_REPEAT_MODE -> session.player.toggleRepeatMode()
        }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    @Deprecated("Deprecated in MediaLibrarySession.Callback")
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaItemsWithStartPosition> {
        return SettableFuture.create<MediaItemsWithStartPosition>()
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        Futures.immediateFuture(
            LibraryResult.ofItem(
                rootMediaItem(),
                params.withContentStyleHints(),
            ),
        )

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        scope.future(Dispatchers.IO) {
            val children = loadChildren(parentId)

            LibraryResult.ofItemList(
                children.paginate(page, pageSize),
                params.withContentStyleHints(),
            )
        }

    private suspend fun loadChildren(parentId: String): List<MediaItem> =
        when (parentId) {
            MusicService.ROOT -> rootChildren()
            MusicService.SONG -> database.songsByCreateDateAsc().first().map { it.toMediaItem(parentId) }
            MusicService.ARTIST -> artistChildren()
            MusicService.ALBUM -> albumChildren()
            MusicService.PLAYLIST -> playlistRootChildren()
            else -> nestedChildren(parentId)
        }

    private suspend fun artistChildren() = database.artistsByCreateDateAsc().first().map { artist ->
        browsableMediaItem(
            id = "${MusicService.ARTIST}/${artist.id}",
            title = artist.artist.name,
            subtitle = context.resources.getQuantityString(R.plurals.n_song, artist.songCount, artist.songCount),
            iconUri = artist.artist.thumbnailUrl?.toUri(),
            mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
        )
    }

    private suspend fun albumChildren() = database.albumsByCreateDateAsc().first().map { album ->
        browsableMediaItem(
            id = "${MusicService.ALBUM}/${album.id}",
            title = album.album.title,
            subtitle = album.artists.joinToString { it.name },
            iconUri = album.album.thumbnailUrl?.toUri(),
            mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        )
    }

    private suspend fun playlistRootChildren(): List<MediaItem> {
        val likedSongCount = database.likedSongsCount().first()
        val downloadedSongCount = downloadUtil.downloads.value.size
        val youtubePlaylists = try {
            YouTube.home().getOrNull()?.sections
                ?.flatMap { it.items }
                ?.filterIsInstance<PlaylistItem>()
                ?.take(10)
                ?: emptyList()
        } catch (e: Exception) {
            reportException(e)
            emptyList()
        }

        return listOf(
            likedSongsMediaItem(likedSongCount),
            downloadedSongsMediaItem(downloadedSongCount),
        ) + database.playlistsByCreateDateAsc().first().map { it.toBrowsableMediaItem() } +
            youtubePlaylists.map { it.toBrowsableMediaItem() }
    }

    private suspend fun nestedChildren(parentId: String): List<MediaItem> = when {
        parentId.startsWith("${MusicService.ARTIST}/") ->
            database.artistSongsByCreateDateAsc(parentId.removePrefix("${MusicService.ARTIST}/"))
                .first().map { it.toMediaItem(parentId) }

        parentId.startsWith("${MusicService.ALBUM}/") ->
            database.albumSongs(parentId.removePrefix("${MusicService.ALBUM}/"))
                .first().map { it.toMediaItem(parentId) }

        parentId.startsWith("${MusicService.PLAYLIST}/") -> playlistChildren(parentId)
        parentId.startsWith("${MusicService.YOUTUBE_PLAYLIST}/") -> youtubePlaylistChildren(parentId)
        else -> emptyList()
    }

    private suspend fun playlistChildren(parentId: String): List<MediaItem> {
        val playlistId = parentId.removePrefix("${MusicService.PLAYLIST}/")
        val songs = when (playlistId) {
            PlaylistEntity.LIKED_PLAYLIST_ID -> database.likedSongs(SongSortType.CREATE_DATE, true)
            PlaylistEntity.DOWNLOADED_PLAYLIST_ID -> downloadedSongs()
            else -> database.playlistSongs(playlistId).map { entries -> entries.map { it.song } }
        }.first()

        return listOf(shuffleMediaItem(parentId)) + songs.map { it.toMediaItem(parentId) }
    }

    private suspend fun youtubePlaylistChildren(parentId: String): List<MediaItem> {
        val playlistId = parentId.removePrefix("${MusicService.YOUTUBE_PLAYLIST}/")
        return try {
            val songs = YouTube.playlist(playlistId).getOrNull()?.songs
                ?.take(100)
                ?.filterExplicit(context.dataStore.get(HideExplicitKey, false))
                ?.filterVideoSongs(
                    context.dataStore.get(HideVideoSongsKey, false) ||
                        context.dataStore.get(DataSaverEnabledKey, false),
                )
                .orEmpty()

            listOf(shuffleMediaItem(parentId)) + songs.map { it.toMediaItem(parentId) }
        } catch (e: Exception) {
            reportException(e)
            emptyList()
        }
    }

    private fun downloadedSongs() = run {
        val downloads = downloadUtil.downloads.value
        database.allSongs()
            .flowOn(Dispatchers.IO)
            .map { songs -> songs.filter { downloads[it.id]?.state == Download.STATE_COMPLETED } }
            .map { songs ->
                songs.map { it to downloads[it.id] }
                    .sortedBy { it.second?.updateTimeMs ?: 0L }
                    .map { it.first }
            }
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        scope.future(Dispatchers.IO) {
            getMediaItem(mediaId)?.let {
                LibraryResult.ofItem(it, null)
            } ?: LibraryResult.ofError(SessionError.ERROR_UNKNOWN)
        }

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        session.notifySearchResultChanged(browser, query, 1, params)
        return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        return scope.future(Dispatchers.IO) {
            if (query.isEmpty()) {
                return@future LibraryResult.ofItemList(emptyList(), params.withContentStyleHints())
            }

            try {
                val localSongs = findLocalSearchSongs(query)
                val searchResults = localSongs.map { song ->
                    song.toMediaItem(
                        path = "${MusicService.SEARCH}/$query",
                        isPlayable = true,
                        isBrowsable = false,
                    )
                }.toMutableList()

                appendOnlineSearchResults(query, localSongs, searchResults)

                LibraryResult.ofItemList(searchResults.paginate(page, pageSize), params.withContentStyleHints())
            } catch (e: Exception) {
                reportException(e)
                LibraryResult.ofItemList(emptyList(), params.withContentStyleHints())
            }
        }
    }

    private suspend fun appendOnlineSearchResults(
        query: String,
        localSongs: List<Song>,
        searchResults: MutableList<MediaItem>,
    ) {
        try {
            val localSongIds = localSongs.mapTo(mutableSetOf()) { it.id }
            val onlineSongs = YouTube.search(query, SearchFilter.FILTER_SONG)
                .getOrNull()
                ?.items
                ?.filterIsInstance<SongItem>()
                ?.filterExplicit(context.dataStore.get(HideExplicitKey, false))
                ?.filterVideoSongs(
                    context.dataStore.get(HideVideoSongsKey, false) ||
                        context.dataStore.get(DataSaverEnabledKey, false),
                )
                ?.filterNot { song -> isAlreadyInLocalLibrary(song, localSongs, localSongIds) }
                .orEmpty()

            onlineSongs.forEach { song ->
                try {
                    database.query { insert(song.toMediaMetadata()) }
                } catch (e: Exception) {
                    reportException(e)
                }
                searchResults.add(song.toMediaItem("${MusicService.SEARCH}/$query"))
            }
        } catch (e: Exception) {
            reportException(e)
        }
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long,
    ): ListenableFuture<MediaItemsWithStartPosition> =
        scope.future(Dispatchers.IO) {
            val defaultResult = MediaItemsWithStartPosition(emptyList(), startIndex, startPositionMs)
            val path = mediaItems.firstOrNull()?.mediaId?.split("/")
                ?: return@future defaultResult

            when (path.firstOrNull()) {
                MusicService.SONG -> {
                    val songId = path.getOrNull(1) ?: return@future defaultResult
                    val allSongs = database.songsByCreateDateAsc().first()
                    queueStartingAt(allSongs, songId, startPositionMs)
                }

                MusicService.ARTIST -> {
                    val songId = path.getOrNull(2) ?: return@future defaultResult
                    val artistId = path.getOrNull(1) ?: return@future defaultResult
                    val songs = database.artistSongsByCreateDateAsc(artistId).first()
                    queueStartingAt(songs, songId, startPositionMs)
                }

                MusicService.ALBUM -> {
                    val songId = path.getOrNull(2) ?: return@future defaultResult
                    val albumId = path.getOrNull(1) ?: return@future defaultResult
                    val albumWithSongs = database.albumWithSongs(albumId).first() ?: return@future defaultResult
                    queueStartingAt(albumWithSongs.songs, songId, startPositionMs)
                }

                MusicService.PLAYLIST -> {
                    val songId = path.getOrNull(2) ?: return@future defaultResult
                    val playlistId = path.getOrNull(1) ?: return@future defaultResult
                    val songs = when (playlistId) {
                        PlaylistEntity.LIKED_PLAYLIST_ID -> database.likedSongs(SongSortType.CREATE_DATE, descending = true)
                        PlaylistEntity.DOWNLOADED_PLAYLIST_ID -> downloadedSongs()
                        else -> database.playlistSongs(playlistId).map { list ->
                            list.map { it.song }
                        }
                    }.first()

                    if (songId == MusicService.SHUFFLE_ACTION) {
                        MediaItemsWithStartPosition(
                            songs.shuffled().map { it.toMediaItem() },
                            0,
                            C.TIME_UNSET
                        )
                    } else {
                        queueStartingAt(songs, songId, startPositionMs)
                    }
                }

                MusicService.YOUTUBE_PLAYLIST -> {
                    val songId = path.getOrNull(2) ?: return@future defaultResult
                    val playlistId = path.getOrNull(1) ?: return@future defaultResult

                    val songs = try {
                        YouTube.playlist(playlistId).getOrNull()?.songs?.map {
                            it.toMediaItem()
                        } ?: emptyList()
                    } catch (e: Exception) {
                        reportException(e)
                        return@future defaultResult
                    }

                    if (songId == MusicService.SHUFFLE_ACTION) {
                        MediaItemsWithStartPosition(
                            songs.shuffled(),
                            0,
                            C.TIME_UNSET
                        )
                    } else {
                        MediaItemsWithStartPosition(
                            songs,
                            songs.indexOfFirst { it.mediaId.endsWith(songId) }.takeIf { it != -1 } ?: 0,
                            C.TIME_UNSET
                        )
                    }
                }

                MusicService.SEARCH -> resolveSearchItems(path, mediaItems, defaultResult)

                else -> defaultResult
            }
        }

    private fun queueStartingAt(
        songs: List<Song>,
        songId: String,
        startPositionMs: Long,
    ) = MediaItemsWithStartPosition(
        songs.map { it.toMediaItem() },
        songs.indexOfFirst { it.id == songId }.coerceAtLeast(0),
        startPositionMs,
    )

    private suspend fun resolveSearchItems(
        path: List<String>,
        mediaItems: List<MediaItem>,
        defaultResult: MediaItemsWithStartPosition,
    ): MediaItemsWithStartPosition {
        val songId = path.lastOrNull() ?: return defaultResult
        val searchQuery = mediaItems.firstOrNull()?.mediaId
            ?.removePrefix("${MusicService.SEARCH}/")
            ?.removeSuffix("/$songId")
            ?.takeIf { it.isNotBlank() }
            ?: return defaultResult

        val localSongs = findLocalSearchSongs(searchQuery)
        val onlineSongs = findOnlineSearchSongs(searchQuery, localSongs)
        val searchResults = localSongs + onlineSongs

        if (searchResults.isEmpty()) {
            return defaultResult
        }

        val targetIndex = searchResults.indexOfFirst { it.id == songId }

        return MediaItemsWithStartPosition(
            searchResults.map { it.toMediaItem() },
            if (targetIndex >= 0) targetIndex else 0,
            C.TIME_UNSET
        )
    }

    private suspend fun findLocalSearchSongs(query: String): List<Song> {
        val matchingSongs = database.allSongs().first().filter { song ->
            song.song.title.contains(query, ignoreCase = true) ||
                song.artists.any { it.name.contains(query, ignoreCase = true) } ||
                song.album?.title?.contains(query, ignoreCase = true) == true
        }
        val artistSongs = database.searchArtists(query).first().flatMap { artist ->
            database.artistSongsByCreateDateAsc(artist.id).first()
        }
        val albumSongs = database.searchAlbums(query).first().flatMap { album ->
            database.albumSongs(album.id).first()
        }
        val playlistSongs = database.searchPlaylists(query).first().flatMap { playlist ->
            database.playlistSongs(playlist.id).first().map { it.song }
        }

        return (matchingSongs + artistSongs + albumSongs + playlistSongs).distinctBy { it.id }
    }

    private suspend fun findOnlineSearchSongs(query: String, localSongs: List<Song>): List<Song> {
        val matchingSongs = mutableListOf<Song>()
        val localSongIds = localSongs.mapTo(mutableSetOf()) { it.id }
        try {
            val onlineResults = YouTube.search(query, SearchFilter.FILTER_SONG)
                .getOrNull()
                ?.items
                ?.filterIsInstance<SongItem>()
                ?.filterExplicit(context.dataStore.get(HideExplicitKey, false))
                ?.filterVideoSongs(
                    context.dataStore.get(HideVideoSongsKey, false) ||
                        context.dataStore.get(DataSaverEnabledKey, false),
                )
                ?.filterNot { onlineSong -> isAlreadyInLocalLibrary(onlineSong, localSongs, localSongIds) }
                .orEmpty()

            onlineResults.forEach { songItem ->
                try {
                    database.query { insert(songItem.toMediaMetadata()) }
                    database.song(songItem.id).first()?.let(matchingSongs::add)
                } catch (e: Exception) {
                    reportException(e)
                }
            }
        } catch (e: Exception) {
            reportException(e)
        }
        return matchingSongs
    }

    private fun isAlreadyInLocalLibrary(
        onlineSong: SongItem,
        localSongs: List<Song>,
        localSongIds: Set<String>,
    ): Boolean {
        if (onlineSong.id in localSongIds) return true

        return localSongs.any { localSong ->
            localSong.song.title.equals(onlineSong.title, ignoreCase = true) &&
                localSong.artists.any { localArtist ->
                    onlineSong.artists.any { onlineArtist ->
                        onlineArtist.name.equals(localArtist.name, ignoreCase = true)
                    }
                }
        }
    }

    private fun drawableUri(
        @DrawableRes id: Int,
    ) = Uri
        .Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(id))
        .appendPath(context.resources.getResourceTypeName(id))
        .appendPath(context.resources.getResourceEntryName(id))
        .build()

    private fun rootMediaItem() = MediaItem
        .Builder()
        .setMediaId(MusicService.ROOT)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(context.getString(R.string.app_name))
                .setIsPlayable(false)
                .setIsBrowsable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                .build(),
        ).build()

    private fun rootChildren() = listOf(
        browsableMediaItem(
            MusicService.SONG,
            context.getString(R.string.songs),
            null,
            drawableUri(R.drawable.music_note),
            MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_GRID_ITEM,
        ),
        browsableMediaItem(
            MusicService.ARTIST,
            context.getString(R.string.artists),
            null,
            drawableUri(R.drawable.artist),
            MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_GRID_ITEM,
        ),
        browsableMediaItem(
            MusicService.ALBUM,
            context.getString(R.string.albums),
            null,
            drawableUri(R.drawable.album),
            MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_GRID_ITEM,
        ),
        browsableMediaItem(
            MusicService.PLAYLIST,
            context.getString(R.string.playlists),
            null,
            drawableUri(R.drawable.queue_music),
            MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_GRID_ITEM,
        ),
    )

    private suspend fun getMediaItem(mediaId: String): MediaItem? {
        val path = mediaId.split("/")
        val type = path.firstOrNull() ?: return null

        return when (type) {
            MusicService.ROOT -> rootMediaItem()
            MusicService.SONG -> if (path.size == 1) {
                rootChildren().first { it.mediaId == MusicService.SONG }
            } else {
                songMediaItem(path[1], MusicService.SONG)
            }
            MusicService.ARTIST -> artistMediaItem(path)
            MusicService.ALBUM -> albumMediaItem(path)
            MusicService.PLAYLIST -> playlistChildMediaItem(path)
            MusicService.YOUTUBE_PLAYLIST -> youtubePlaylistChildMediaItem(path)

            MusicService.SEARCH -> {
                val songId = path.lastOrNull() ?: return null
                songMediaItem(songId, mediaId.removeSuffix("/$songId"))
            }

            else -> songMediaItem(mediaId)
        }
    }

    private suspend fun artistMediaItem(path: List<String>): MediaItem? {
        val artistId = path.getOrNull(1) ?: return null
        if (path.size != 2) {
            val songId = path.lastOrNull() ?: return null
            return songMediaItem(songId, "${MusicService.ARTIST}/$artistId")
        }

        val artist = database.artist(artistId).first() ?: return null
        return browsableMediaItem(
            "${MusicService.ARTIST}/${artist.id}",
            artist.artist.name,
            context.resources.getQuantityString(R.plurals.n_song, artist.songCount, artist.songCount),
            artist.artist.thumbnailUrl?.toUri(),
            MediaMetadata.MEDIA_TYPE_ARTIST,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
        )
    }

    private suspend fun albumMediaItem(path: List<String>): MediaItem? {
        val albumId = path.getOrNull(1) ?: return null
        if (path.size != 2) {
            val songId = path.lastOrNull() ?: return null
            return songMediaItem(songId, "${MusicService.ALBUM}/$albumId")
        }

        val album = database.album(albumId).first() ?: return null
        return browsableMediaItem(
            "${MusicService.ALBUM}/${album.id}",
            album.album.title,
            album.artists.joinToString { it.name },
            album.album.thumbnailUrl?.toUri(),
            MediaMetadata.MEDIA_TYPE_ALBUM,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        )
    }

    private suspend fun playlistChildMediaItem(path: List<String>): MediaItem? {
        val playlistId = path.getOrNull(1) ?: return null
        if (path.size == 2) return playlistMediaItem(playlistId)

        val songId = path.lastOrNull() ?: return null
        val playlistPath = "${MusicService.PLAYLIST}/$playlistId"
        return if (songId == MusicService.SHUFFLE_ACTION) {
            shuffleMediaItem(playlistPath)
        } else {
            songMediaItem(songId, playlistPath)
        }
    }

    private suspend fun youtubePlaylistChildMediaItem(path: List<String>): MediaItem? {
        val playlistId = path.getOrNull(1) ?: return null
        if (path.size == 2) return youtubePlaylistMediaItem(playlistId)

        val songId = path.lastOrNull() ?: return null
        val playlistPath = "${MusicService.YOUTUBE_PLAYLIST}/$playlistId"
        if (songId == MusicService.SHUFFLE_ACTION) return shuffleMediaItem(playlistPath)

        return songMediaItem(songId, playlistPath)
            ?: youtubePlaylistSongMediaItem(playlistId, songId)
    }

    private suspend fun songMediaItem(songId: String, path: String? = null): MediaItem? {
        val song = database.song(songId).first() ?: return null
        return if (path == null) song.toMediaItem() else song.toMediaItem(path)
    }

    private suspend fun playlistMediaItem(playlistId: String): MediaItem? =
        when (playlistId) {
            PlaylistEntity.LIKED_PLAYLIST_ID -> likedSongsMediaItem(database.likedSongsCount().first())
            PlaylistEntity.DOWNLOADED_PLAYLIST_ID -> downloadedSongsMediaItem(downloadUtil.downloads.value.size)
            else -> database.playlist(playlistId).first()?.toBrowsableMediaItem()
        }

    private suspend fun youtubePlaylistMediaItem(playlistId: String): MediaItem =
        try {
            YouTube.playlist(playlistId).getOrNull()?.playlist?.toBrowsableMediaItem()
        } catch (e: Exception) {
            reportException(e)
            null
        } ?: browsableMediaItem(
            "${MusicService.YOUTUBE_PLAYLIST}/$playlistId",
            playlistId,
            "YouTube Music",
            null,
            MediaMetadata.MEDIA_TYPE_PLAYLIST,
            singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        )

    private suspend fun youtubePlaylistSongMediaItem(
        playlistId: String,
        songId: String,
    ): MediaItem? =
        try {
            YouTube.playlist(playlistId).getOrNull()?.songs
                ?.firstOrNull { it.id == songId }
                ?.toMediaItem("${MusicService.YOUTUBE_PLAYLIST}/$playlistId")
        } catch (e: Exception) {
            reportException(e)
            null
        }

    private fun likedSongsMediaItem(songCount: Int) = browsableMediaItem(
        "${MusicService.PLAYLIST}/${PlaylistEntity.LIKED_PLAYLIST_ID}",
        context.getString(R.string.liked_songs),
        context.resources.getQuantityString(
            R.plurals.n_song,
            songCount,
            songCount
        ),
        drawableUri(R.drawable.favorite),
        MediaMetadata.MEDIA_TYPE_PLAYLIST,
        singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
    )

    private fun downloadedSongsMediaItem(songCount: Int) = browsableMediaItem(
        "${MusicService.PLAYLIST}/${PlaylistEntity.DOWNLOADED_PLAYLIST_ID}",
        context.getString(R.string.downloaded_songs),
        context.resources.getQuantityString(
            R.plurals.n_song,
            songCount,
            songCount
        ),
        drawableUri(R.drawable.download),
        MediaMetadata.MEDIA_TYPE_PLAYLIST,
        singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
    )

    private fun com.convx.music.db.entities.Playlist.toBrowsableMediaItem() = browsableMediaItem(
        "${MusicService.PLAYLIST}/$id",
        playlist.name,
        context.resources.getQuantityString(
            R.plurals.n_song,
            songCount,
            songCount
        ),
        thumbnails.firstOrNull()?.toUri(),
        MediaMetadata.MEDIA_TYPE_PLAYLIST,
        singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
    )

    private fun PlaylistItem.toBrowsableMediaItem() = browsableMediaItem(
        "${MusicService.YOUTUBE_PLAYLIST}/$id",
        title,
        author?.name ?: "YouTube Music",
        thumbnail?.toUri(),
        MediaMetadata.MEDIA_TYPE_PLAYLIST,
        singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
    )

    private fun shuffleMediaItem(parentId: String) = MediaItem.Builder()
        .setMediaId("$parentId/${MusicService.SHUFFLE_ACTION}")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(context.getString(R.string.shuffle))
                .setArtworkUri(drawableUri(R.drawable.shuffle))
                .setIsPlayable(true)
                .setIsBrowsable(false)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setExtras(playableMediaItemExtras())
                .build()
        ).build()

    private fun browsableMediaItem(
        id: String,
        title: String,
        subtitle: String?,
        iconUri: Uri?,
        mediaType: Int = MediaMetadata.MEDIA_TYPE_MUSIC,
        singleItemStyle: Int = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
    ) = MediaItem
        .Builder()
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setArtist(subtitle)
                .setArtworkUri(iconUri)
                .setIsPlayable(false)
                .setIsBrowsable(true)
                .setMediaType(mediaType)
                .setExtras(
                    contentStyleExtras(
                        browsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
                        playableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
                        singleItemStyle = singleItemStyle,
                    )
                )
                .build(),
        ).build()

    private fun Song.toMediaItem(path: String, isPlayable: Boolean = true, isBrowsable: Boolean = false): MediaItem {
        return MediaItem
            .Builder()
            .setMediaId("$path/$id")
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setTitle(song.title)
                    .setSubtitle(artists.joinToString { it.name })
                    .setArtist(artists.joinToString { it.name })
                    .setArtworkUri(song.thumbnailUrl?.toUri())
                    .setIsPlayable(isPlayable)
                    .setIsBrowsable(isBrowsable)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .setExtras(playableMediaItemExtras())
                    .build(),
            ).build()
    }

    private fun SongItem.toMediaItem(path: String) = MediaItem.Builder()
        .setMediaId("$path/$id")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setSubtitle(artists.joinToString(", ") { it.name })
                .setArtist(artists.joinToString(", ") { it.name })
                .setArtworkUri(thumbnail.toUri())
                .setIsPlayable(true)
                .setIsBrowsable(false)
                .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                .setExtras(playableMediaItemExtras())
                .build()
        )
        .build()

    private fun MediaLibraryService.LibraryParams?.withContentStyleHints(): MediaLibraryService.LibraryParams {
        val extras = Bundle(this?.extras ?: Bundle()).apply {
            putInt(
                MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
                MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM
            )
            putInt(
                MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
                MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM
            )
        }

        return MediaLibraryService.LibraryParams.Builder()
            .setOffline(this?.isOffline ?: false)
            .setRecent(this?.isRecent ?: false)
            .setSuggested(this?.isSuggested ?: false)
            .setExtras(extras)
            .build()
    }

    private fun playableMediaItemExtras() = contentStyleExtras(
        singleItemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM,
    )

    private fun contentStyleExtras(
        browsableStyle: Int? = null,
        playableStyle: Int? = null,
        singleItemStyle: Int? = null,
    ) = Bundle().apply {
        browsableStyle?.let {
            putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_BROWSABLE, it)
        }
        playableStyle?.let {
            putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_PLAYABLE, it)
        }
        singleItemStyle?.let {
            putInt(MediaConstants.EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM, it)
        }
    }

    private fun <T> List<T>.paginate(page: Int, pageSize: Int): List<T> {
        if (page < 0 || pageSize < 1 || isEmpty()) return emptyList()
        if (pageSize == Int.MAX_VALUE) return this

        val fromIndex = page.toLong() * pageSize
        if (fromIndex >= size) return emptyList()

        return subList(fromIndex.toInt(), minOf(fromIndex.toInt() + pageSize, size))
    }
}
