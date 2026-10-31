/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.LruCache
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
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.convx.music.R
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.MediaSessionConstants
import com.convx.music.constants.SongSortType
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.extensions.toggleRepeatMode
import com.convx.music.models.toMediaMetadata
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@androidx.annotation.OptIn(UnstableApi::class)
class MediaLibrarySessionCallback
@Inject
constructor(
    @ApplicationContext val context: Context,
    val database: MusicDatabase,
    val downloadUtil: DownloadUtil,
    private val items: MediaBrowseItemFactory,
) : MediaLibrarySession.Callback {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var service: MusicService
    var toggleLike: () -> Unit = {}
    var toggleStartRadio: () -> Unit = {}
    var toggleLibrary: () -> Unit = {}

    private val onlineSections = LruCache<String, CachedSections>(ONLINE_CACHE_SIZE)

    // Songs listed for a search query, so a tapped result queues exactly what was shown.
    private val searchSongs = LruCache<String, List<Song>>(SEARCH_CACHE_SIZE)

    // Lets onGetItem answer for anything a browser was shown, including online items.
    private val shownItems = LruCache<String, MediaItem>(SHOWN_ITEMS_CACHE_SIZE)

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
        if (session.isAutoCompanionController(controller) || session.isAutomotiveController(controller)) {
            CarArtworkProvider.grantReadAccess(context, controller.packageName)
        }
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

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): ListenableFuture<MediaItemsWithStartPosition> =
        scope.future {
            service.resumeSavedPlayback() ?: throw UnsupportedOperationException("No saved queue to resume")
        }

    /** Tells subscribed browsers to reload lists whose content changed outside the car. */
    fun observeLibraryChanges(session: MediaLibrarySession) {
        scope.launch {
            combine(
                database.likedSongsCount(),
                database.playlistsByCreateDateAsc().map { playlists ->
                    playlists.map { Triple(it.id, it.playlist.name, it.songCount) }
                },
                downloadUtil.downloads.map { downloads -> downloads.count { it.value.state == Download.STATE_COMPLETED } },
            ) { likedCount, playlists, downloadedCount -> Triple(likedCount, playlists, downloadedCount) }
                .notifyOnChange { (_, playlists, _) ->
                    session.notifyChildrenChanged(MusicService.PLAYLIST, playlists.size + 2, null)
                    session.notifyChildrenChanged(likedPlaylistId, Int.MAX_VALUE, null)
                    session.notifyChildrenChanged(downloadedPlaylistId, Int.MAX_VALUE, null)
                    playlists.forEach { (id) ->
                        session.notifyChildrenChanged("${MusicService.PLAYLIST}/$id", Int.MAX_VALUE, null)
                    }
                }
        }
        scope.launch {
            database.recentEvents(1)
                .map { events -> events.firstOrNull()?.event?.id }
                .notifyOnChange { session.notifyChildrenChanged(MusicService.HOME, Int.MAX_VALUE, null) }
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun <T> Flow<T>.notifyOnChange(notify: (T) -> Unit) =
        distinctUntilChanged()
            .drop(1)
            .debounce(LIBRARY_CHANGE_DEBOUNCE_MS)
            .collect(notify)

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val root = if (params?.isRecent == true) {
            items.folder(MusicService.RECENT, context.getString(R.string.car_recently_played))
        } else {
            items.folder(MusicService.ROOT, context.getString(R.string.app_name))
        }
        return Futures.immediateFuture(LibraryResult.ofItem(root, params.withContentStyleHints()))
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        scope.future(Dispatchers.IO) {
            val children = loadChildren(parentId, params).also(::rememberShown)

            LibraryResult.ofItemList(
                children.paginate(page, pageSize),
                params.withContentStyleHints(),
            )
        }

    private suspend fun loadChildren(
        parentId: String,
        params: MediaLibraryService.LibraryParams?,
    ): List<MediaItem> =
        when (parentId) {
            MusicService.ROOT -> rootChildren().take(params.rootChildrenLimit())
            MusicService.RECENT -> recentResumptionChildren()
            MusicService.HOME -> homeChildren()
            MusicService.LIBRARY -> libraryChildren()
            MusicService.PLAYLIST -> playlistRootChildren()
            MusicService.EXPLORE -> exploreChildren()
            MusicService.SONG -> database.songsByCreateDateAsc().first().map { items.song(it, parentId) }
            MusicService.ARTIST -> artistChildren()
            MusicService.ALBUM -> albumChildren()
            else -> nestedChildren(parentId)
        }

    private fun rootChildren() = listOf(
        items.folder(
            id = MusicService.HOME,
            title = context.getString(R.string.home),
            iconUri = items.drawableUri(R.drawable.home_outlined),
            childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        ),
        items.folder(
            id = MusicService.LIBRARY,
            title = context.getString(R.string.car_tab_library),
            iconUri = items.drawableUri(R.drawable.library_music),
        ),
        items.folder(
            id = MusicService.PLAYLIST,
            title = context.getString(R.string.playlists),
            iconUri = items.drawableUri(R.drawable.queue_music),
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS,
            childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        ),
        items.folder(
            id = MusicService.EXPLORE,
            title = context.getString(R.string.explore),
            iconUri = items.drawableUri(R.drawable.explore_outlined),
            childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        ),
    )

    private fun libraryChildren() = listOf(
        items.folder(
            id = MusicService.SONG,
            title = context.getString(R.string.songs),
            iconUri = items.drawableUri(R.drawable.music_note),
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
        ),
        items.folder(
            id = MusicService.ARTIST,
            title = context.getString(R.string.artists),
            iconUri = items.drawableUri(R.drawable.artist),
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS,
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
        ),
        items.folder(
            id = MusicService.ALBUM,
            title = context.getString(R.string.albums),
            iconUri = items.drawableUri(R.drawable.album),
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
            childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        ),
    )

    private suspend fun homeChildren(): List<MediaItem> {
        val recentTitle = context.getString(R.string.car_recently_played)
        val quickPicksTitle = context.getString(R.string.quick_picks)
        val recent = recentlyPlayedSongs().map { items.song(it, MusicService.HOME_RECENT, recentTitle) }
        val quickPicks = quickPickSongs().map { items.song(it, MusicService.QUICK_PICKS, quickPicksTitle) }
        val online = onlineSections(MusicService.HOME)
            ?.let { renderOnlineSections(MusicService.HOME, it) }
            .orEmpty()
        return recent + quickPicks + online
    }

    private suspend fun recentlyPlayedSongs(): List<Song> =
        database.recentEvents(RECENT_EVENTS_SCANNED).first()
            .map { it.song }
            .distinctBy { it.id }
            .take(MAX_RECENT_SONGS)

    private suspend fun quickPickSongs(): List<Song> =
        database.quickPicks().first().take(MAX_QUICK_PICKS)

    private suspend fun recentResumptionChildren(): List<MediaItem> {
        val savedQueue = service.savedPlaybackQueue() ?: return emptyList()
        val current = savedQueue.mediaItems.getOrNull(savedQueue.startIndex) ?: return emptyList()
        return listOf(current.buildUpon().setMediaId("${MusicService.RECENT}/${current.mediaId}").build())
    }

    private suspend fun exploreChildren(): List<MediaItem> {
        val charts = items.folder(
            id = MusicService.CHARTS,
            title = context.getString(R.string.charts),
            iconUri = items.drawableUri(R.drawable.trending_up),
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
            childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        )
        val page = onlineOrNull { YouTube.explore().getOrNull() } ?: return listOf(charts)

        val newReleasesTitle = context.getString(R.string.new_release_albums)
        val newReleases = page.newReleaseAlbums
            .filterExplicit(hideExplicit())
            .map { onlineAlbumItem(it, newReleasesTitle) }

        val moodsTitle = context.getString(R.string.mood_and_genres)
        val moods = page.moodAndGenres.map { mood ->
            items.folder(
                id = moodId(mood.endpoint.browseId, mood.endpoint.params),
                title = mood.title,
                itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
                childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
                groupTitle = moodsTitle,
            )
        }
        return listOf(charts) + newReleases + moods
    }

    private suspend fun artistChildren() = database.artistsByCreateDateAsc().first().map { artist ->
        items.folder(
            id = "${MusicService.ARTIST}/${artist.id}",
            title = artist.artist.name,
            subtitle = context.resources.getQuantityString(R.plurals.n_song, artist.songCount, artist.songCount),
            iconUri = artist.artist.thumbnailUrl?.toUri(),
            mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
        )
    }

    private suspend fun albumChildren() = database.albumsByCreateDateAsc().first().map { album ->
        items.folder(
            id = "${MusicService.ALBUM}/${album.id}",
            title = album.album.title,
            subtitle = album.artists.joinToString { it.name },
            iconUri = album.album.thumbnailUrl?.toUri(),
            mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        )
    }

    private suspend fun playlistRootChildren(): List<MediaItem> {
        val likedSongCount = database.likedSongsCount().first()
        val downloadedSongCount = downloadUtil.downloads.value.count { it.value.state == Download.STATE_COMPLETED }
        return listOf(
            likedSongsMediaItem(likedSongCount),
            downloadedSongsMediaItem(downloadedSongCount),
        ) + database.playlistsByCreateDateAsc().first().map { playlistFolder(it) }
    }

    private suspend fun nestedChildren(parentId: String): List<MediaItem> = when {
        parentId.startsWith("${MusicService.ARTIST}/") ->
            database.artistSongsByCreateDateAsc(parentId.removePrefix("${MusicService.ARTIST}/"))
                .first().map { items.song(it, parentId) }

        parentId.startsWith("${MusicService.ALBUM}/") ->
            database.albumSongs(parentId.removePrefix("${MusicService.ALBUM}/"))
                .first().map { items.song(it, parentId) }

        parentId.startsWith("${MusicService.PLAYLIST}/") -> playlistChildren(parentId)
        parentId.startsWith("${MusicService.YOUTUBE_PLAYLIST}/") -> youtubePlaylistChildren(parentId)
        else -> onlineSections(parentId)?.let { renderOnlineSections(parentId, it) }.orEmpty()
    }

    private suspend fun playlistChildren(parentId: String): List<MediaItem> {
        val playlistId = parentId.removePrefix("${MusicService.PLAYLIST}/")
        val songs = playlistSongs(playlistId)
        if (songs.isEmpty()) return emptyList()
        return listOf(items.shuffle(parentId)) + songs.map { items.song(it, parentId) }
    }

    private suspend fun playlistSongs(playlistId: String): List<Song> = when (playlistId) {
        PlaylistEntity.LIKED_PLAYLIST_ID -> database.likedSongs(SongSortType.CREATE_DATE, descending = true)
        PlaylistEntity.DOWNLOADED_PLAYLIST_ID -> downloadedSongs()
        else -> database.playlistSongs(playlistId).map { entries -> entries.map { it.song } }
    }.first()

    private suspend fun youtubePlaylistChildren(parentId: String): List<MediaItem> {
        val songs = youtubePlaylistSongs(parentId.removePrefix("${MusicService.YOUTUBE_PLAYLIST}/"))
        if (songs.isEmpty()) return emptyList()
        return listOf(items.shuffle(parentId)) + songs.map { items.song(it, parentId) }
    }

    private suspend fun youtubePlaylistSongs(playlistId: String): List<SongItem> = onlineOrNull {
        YouTube.playlist(playlistId).getOrNull()?.songs
            ?.take(MAX_ONLINE_PLAYLIST_SONGS)
            ?.filterExplicit(hideExplicit())
            ?.filterVideoSongs(hideVideoSongs())
    }.orEmpty()

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

    // Online content (home feed, charts, artist pages, moods, albums) is fetched once per
    // parent and cached, so a tapped song can be queued with the section it was shown in.
    private suspend fun onlineSections(parentId: String): List<OnlineSection>? {
        onlineSections[parentId]
            ?.takeIf { SystemClock.elapsedRealtime() - it.loadedAtMs < ONLINE_CACHE_TTL_MS }
            ?.let { return it.sections }

        val sections = onlineOrNull { loadOnlineSections(parentId) }
            ?.map { section ->
                section.copy(items = section.items.filterExplicit(hideExplicit()).filterVideoSongs(hideVideoSongs()))
            }
            ?.filter { it.items.isNotEmpty() }
            ?: return null
        onlineSections.put(parentId, CachedSections(SystemClock.elapsedRealtime(), sections))
        return sections
    }

    private suspend fun loadOnlineSections(parentId: String): List<OnlineSection>? {
        val path = parentId.split("/")
        val id = path.getOrNull(1)
        return when (path.first()) {
            MusicService.HOME -> YouTube.home().getOrNull()?.sections
                ?.take(MAX_HOME_SECTIONS)
                ?.map { OnlineSection(it.title, it.items.take(MAX_HOME_SECTION_ITEMS)) }

            MusicService.CHARTS -> YouTube.getChartsPage().getOrNull()?.sections
                ?.map { OnlineSection(it.title, it.items) }

            MusicService.YOUTUBE_ALBUM -> id?.let { YouTube.album(it).getOrNull() }
                ?.let { listOf(OnlineSection(null, it.songs)) }

            MusicService.YOUTUBE_ARTIST -> id?.let { YouTube.artist(it).getOrNull() }
                ?.sections?.map { OnlineSection(it.title, it.items) }

            MusicService.MOOD -> id?.let { browseId ->
                val params = path.getOrNull(2)?.let(Uri::decode)?.takeIf { it.isNotEmpty() }
                YouTube.browse(Uri.decode(browseId), params).getOrNull()
            }?.items?.map { OnlineSection(it.title, it.items) }

            else -> null
        }
    }

    private fun renderOnlineSections(parentId: String, sections: List<OnlineSection>): List<MediaItem> {
        val offerShuffle = parentId.startsWith("${MusicService.YOUTUBE_ALBUM}/") &&
            sections.any { section -> section.items.any { it is SongItem } }
        val shuffle = sections.firstOrNull()
            ?.takeIf { offerShuffle }
            ?.let { listOf(items.shuffle(onlineSongParentId(parentId, it.title))) }
            .orEmpty()

        return shuffle + sections.flatMap { section ->
            val songParentId = onlineSongParentId(parentId, section.title)
            section.items.map { onlineItem(it, songParentId, section.title) }
        }
    }

    private fun onlineItem(item: YTItem, songParentId: String, groupTitle: String?): MediaItem = when (item) {
        is SongItem -> items.song(item, songParentId, groupTitle)
        is AlbumItem -> onlineAlbumItem(item, groupTitle)
        is PlaylistItem -> onlinePlaylistItem(item, groupTitle)
        is ArtistItem -> onlineArtistItem(item, groupTitle)
    }

    private fun onlineArtistItem(artist: ArtistItem, groupTitle: String?) = items.folder(
        id = "${MusicService.YOUTUBE_ARTIST}/${artist.id}",
        title = artist.title,
        iconUri = artist.thumbnail?.toUri(),
        mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
        childBrowsableStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        groupTitle = groupTitle,
    )

    private fun onlineAlbumItem(album: AlbumItem, groupTitle: String?) = items.folder(
        id = "${MusicService.YOUTUBE_ALBUM}/${album.browseId}",
        title = album.title,
        subtitle = album.artists?.joinToString { it.name },
        iconUri = album.thumbnail.toUri(),
        mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
        itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        groupTitle = groupTitle,
    )

    private fun onlinePlaylistItem(playlist: PlaylistItem, groupTitle: String?) = items.folder(
        id = "${MusicService.YOUTUBE_PLAYLIST}/${playlist.id}",
        title = playlist.title,
        subtitle = playlist.author?.name ?: context.getString(R.string.car_online_source),
        iconUri = playlist.thumbnail?.toUri(),
        mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
        itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
        groupTitle = groupTitle,
    )

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> =
        scope.future(Dispatchers.IO) {
            getMediaItem(mediaId)?.let {
                LibraryResult.ofItem(it, null)
            } ?: LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
        }

    // Collection folders resolve only after a browser was shown them; songs resolve from the database.
    private suspend fun getMediaItem(mediaId: String): MediaItem? {
        shownItems[mediaId]?.let { return it }
        if (mediaId == MusicService.ROOT) return items.folder(MusicService.ROOT, context.getString(R.string.app_name))
        (rootChildren() + libraryChildren()).firstOrNull { it.mediaId == mediaId }?.let { return it }

        val songId = mediaId.substringAfterLast('/')
        val parentId = mediaId.substringBeforeLast('/', missingDelimiterValue = "").ifEmpty { null }
        return songMediaItem(songId, parentId)
    }

    private suspend fun songMediaItem(songId: String, parentId: String?): MediaItem? {
        val song = database.song(songId).first() ?: return null
        return if (parentId == null) song.toMediaItem() else items.song(song, parentId)
    }

    override fun onSearch(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        session.notifySearchResultChanged(browser, query, Int.MAX_VALUE, params)
        return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetSearchResult(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
        scope.future(Dispatchers.IO) {
            val results = if (query.isBlank()) emptyList() else searchResults(query).also(::rememberShown)
            LibraryResult.ofItemList(results.paginate(page, pageSize), params.withContentStyleHints())
        }

    private suspend fun searchResults(query: String): List<MediaItem> = coroutineScope {
        val onlineAlbums = async { onlineSearch(query, SearchFilter.FILTER_ALBUM).filterIsInstance<AlbumItem>() }
        val onlineArtists = async { onlineSearch(query, SearchFilter.FILTER_ARTIST).filterIsInstance<ArtistItem>() }
        val songsTitle = context.getString(R.string.songs)
        val songResultParent = "${MusicService.SEARCH}/$query"
        val songs = searchSongs(query).map { items.song(it, songResultParent, songsTitle) }

        val albumsTitle = context.getString(R.string.albums)
        val albums = database.searchAlbums(query, MAX_SEARCH_COLLECTIONS).first().map { album ->
            items.folder(
                id = "${MusicService.ALBUM}/${album.id}",
                title = album.album.title,
                subtitle = album.artists.joinToString { it.name },
                iconUri = album.album.thumbnailUrl?.toUri(),
                mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
                groupTitle = albumsTitle,
            )
        } + onlineAlbums.await().take(MAX_SEARCH_COLLECTIONS).map { onlineAlbumItem(it, albumsTitle) }

        val artistsTitle = context.getString(R.string.artists)
        val artists = database.searchArtists(query, MAX_SEARCH_COLLECTIONS).first().map { artist ->
            items.folder(
                id = "${MusicService.ARTIST}/${artist.id}",
                title = artist.artist.name,
                iconUri = artist.artist.thumbnailUrl?.toUri(),
                mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
                groupTitle = artistsTitle,
            )
        } + onlineArtists.await().take(MAX_SEARCH_COLLECTIONS).map { onlineArtistItem(it, artistsTitle) }

        val playlistsTitle = context.getString(R.string.playlists)
        val playlists = database.searchPlaylists(query, MAX_SEARCH_COLLECTIONS).first()
            .map { playlistFolder(it, playlistsTitle) }

        songs + albums + artists + playlists
    }

    private suspend fun searchSongs(query: String): List<Song> {
        searchSongs[query]?.let { return it }
        val localSongs = findLocalSearchSongs(query).take(MAX_LOCAL_SEARCH_SONGS)
        val songs = localSongs + findOnlineSearchSongs(query, localSongs)
        if (songs.isNotEmpty()) searchSongs.put(query, songs)
        return songs
    }

    private suspend fun onlineSearch(query: String, filter: SearchFilter): List<YTItem> = onlineOrNull {
        YouTube.search(query, filter).getOrNull()?.items
            ?.filterExplicit(hideExplicit())
            ?.filterVideoSongs(hideVideoSongs())
    }.orEmpty()

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long,
    ): ListenableFuture<MediaItemsWithStartPosition> {
        if (mediaItems.isNotEmpty() && mediaItems.all { it.localConfiguration != null }) {
            return Futures.immediateFuture(MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs))
        }
        return scope.future(Dispatchers.IO) {
            val request = mediaItems.firstOrNull()
                ?: throw UnsupportedOperationException("Empty media request")
            val searchQuery = request.requestMetadata.searchQuery
            if (searchQuery != null) {
                playFromSearch(mediaSession, searchQuery, request.requestMetadata.extras)
            } else {
                resolveMediaId(request.mediaId, startPositionMs)
                    ?: throw UnsupportedOperationException("Unknown media id ${request.mediaId}")
            }
        }
    }

    private suspend fun resolveMediaId(mediaId: String, startPositionMs: Long): MediaItemsWithStartPosition? {
        val path = mediaId.split("/")
        val songId = path.lastOrNull() ?: return null
        return when (path.first()) {
            MusicService.RECENT -> withContext(Dispatchers.Main) { service.resumeSavedPlayback() }
            MusicService.SONG ->
                queueStartingAt(database.songsByCreateDateAsc().first(), songId, startPositionMs)

            MusicService.HOME_RECENT -> queueStartingAt(recentlyPlayedSongs(), songId, startPositionMs)
            MusicService.QUICK_PICKS -> queueStartingAt(quickPickSongs(), songId, startPositionMs)

            MusicService.ARTIST -> path.getOrNull(1)?.takeIf { path.size == 3 }?.let { artistId ->
                queueStartingAt(database.artistSongsByCreateDateAsc(artistId).first(), songId, startPositionMs)
            }

            MusicService.ALBUM -> path.getOrNull(1)?.takeIf { path.size == 3 }?.let { albumId ->
                database.albumWithSongs(albumId).first()?.let { queueStartingAt(it.songs, songId, startPositionMs) }
            }

            MusicService.PLAYLIST -> path.getOrNull(1)?.takeIf { path.size == 3 }?.let { playlistId ->
                val songs = playlistSongs(playlistId)
                if (songId == MusicService.SHUFFLE_ACTION) shuffledQueue(songs.map { it.toMediaItem() })
                else queueStartingAt(songs, songId, startPositionMs)
            }

            MusicService.YOUTUBE_PLAYLIST -> path.getOrNull(1)?.takeIf { path.size == 3 }?.let { playlistId ->
                onlineQueue(youtubePlaylistSongs(playlistId), songId)
            }

            MusicService.ONLINE_SONG -> resolveOnlineSong(path)
            MusicService.SEARCH -> resolveSearchItems(mediaId, songId)
            else -> null
        }?.takeIf { it.mediaItems.isNotEmpty() }
            ?: singleSongQueue(songId, startPositionMs)
    }

    private suspend fun resolveOnlineSong(path: List<String>): MediaItemsWithStartPosition? {
        if (path.size != 4) return null
        val parentId = Uri.decode(path[1])
        val sectionTitle = Uri.decode(path[2])
        val songId = path[3]
        val songs = onlineSections(parentId)
            ?.firstOrNull { it.title.orEmpty() == sectionTitle }
            ?.items?.filterIsInstance<SongItem>()
            .orEmpty()
        return onlineQueue(songs, songId)
    }

    private fun onlineQueue(songs: List<SongItem>, songId: String): MediaItemsWithStartPosition? {
        if (songs.isEmpty()) return null
        val mediaItems = songs.map { it.toMediaItem() }
        if (songId == MusicService.SHUFFLE_ACTION) return shuffledQueue(mediaItems)
        val startIndex = songs.indexOfFirst { it.id == songId }.takeIf { it >= 0 } ?: return null
        return MediaItemsWithStartPosition(mediaItems, startIndex, C.TIME_UNSET)
    }

    private fun shuffledQueue(mediaItems: List<MediaItem>) =
        MediaItemsWithStartPosition(mediaItems.shuffled(), 0, C.TIME_UNSET)

    // Null when the list changed and no longer holds the tapped song.
    private fun queueStartingAt(
        songs: List<Song>,
        songId: String,
        startPositionMs: Long,
    ): MediaItemsWithStartPosition? {
        val startIndex = songs.indexOfFirst { it.id == songId }.takeIf { it >= 0 } ?: return null
        return MediaItemsWithStartPosition(songs.map { it.toMediaItem() }, startIndex, startPositionMs)
    }

    private suspend fun singleSongQueue(songId: String, startPositionMs: Long): MediaItemsWithStartPosition? =
        database.song(songId).first()?.let {
            MediaItemsWithStartPosition(listOf(it.toMediaItem()), 0, startPositionMs)
        }

    private suspend fun resolveSearchItems(mediaId: String, songId: String): MediaItemsWithStartPosition? {
        val searchQuery = mediaId
            .removePrefix("${MusicService.SEARCH}/")
            .removeSuffix("/$songId")
            .takeIf { it.isNotBlank() }
            ?: return null
        return queueStartingAt(searchSongs(searchQuery), songId, C.TIME_UNSET)
    }

    /** Handles "play … on Convx" voice requests, which arrive as a search query instead of a media id. */
    private suspend fun playFromSearch(
        session: MediaSession,
        query: String,
        extras: Bundle?,
    ): MediaItemsWithStartPosition {
        val songs = if (query.isBlank()) {
            withContext(Dispatchers.Main) { service.resumeSavedPlayback() }?.let { return it }
            quickPickSongs().ifEmpty { playlistSongs(PlaylistEntity.LIKED_PLAYLIST_ID) }.shuffled()
        } else {
            voiceSearchSongs(query, extras)
        }

        if (songs.isEmpty()) {
            withContext(Dispatchers.Main) {
                session.mediaNotificationControllerInfo?.let { notificationController ->
                    session.sendError(
                        notificationController,
                        SessionError(
                            SessionError.ERROR_BAD_VALUE,
                            context.getString(R.string.car_voice_no_results, query),
                        ),
                    )
                }
            }
            throw UnsupportedOperationException("No results for voice query")
        }
        return MediaItemsWithStartPosition(songs.map { it.toMediaItem() }, 0, C.TIME_UNSET)
    }

    // Voice assistants still send the playlist focus constants deprecated in API 31.
    @Suppress("DEPRECATION")
    private suspend fun voiceSearchSongs(query: String, extras: Bundle?): List<Song> {
        when (extras?.getString(MediaStore.EXTRA_MEDIA_FOCUS)) {
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                val albumName = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM) ?: query
                database.searchAlbums(albumName, 1).first().firstOrNull()
                    ?.let { album -> return database.albumSongs(album.id).first() }
                onlineAlbumSongs(albumName).takeIf { it.isNotEmpty() }?.let { return it }
            }

            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                val artistName = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST) ?: query
                database.searchArtists(artistName, 1).first().firstOrNull()
                    ?.let { artist -> return database.artistSongsByCreateDateAsc(artist.id).first().shuffled() }
            }

            MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE -> {
                val playlistName = extras.getString(MediaStore.EXTRA_MEDIA_PLAYLIST) ?: query
                database.searchPlaylists(playlistName, 1).first().firstOrNull()
                    ?.let { playlist -> return playlistSongs(playlist.id) }
            }
        }

        val localSongs = findLocalSearchSongs(query)
        val exactLocalMatches = localSongs.filter { it.song.title.equals(query, ignoreCase = true) }
        val onlineSongs = findOnlineSearchSongs(query, exactLocalMatches)
        return (exactLocalMatches + onlineSongs).ifEmpty { localSongs }
    }

    private suspend fun onlineAlbumSongs(albumName: String): List<Song> {
        val album = onlineSearch(albumName, SearchFilter.FILTER_ALBUM).filterIsInstance<AlbumItem>().firstOrNull()
            ?: return emptyList()
        val songItems = onlineOrNull { YouTube.album(album.browseId).getOrNull()?.songs }.orEmpty()
        return songItems.mapNotNull { storeOnlineSong(it) }
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
        val localSongIds = localSongs.mapTo(mutableSetOf()) { it.id }
        return onlineSearch(query, SearchFilter.FILTER_SONG)
            .filterIsInstance<SongItem>()
            .filterNot { onlineSong -> isAlreadyInLocalLibrary(onlineSong, localSongs, localSongIds) }
            .mapNotNull { storeOnlineSong(it) }
    }

    private suspend fun storeOnlineSong(songItem: SongItem): Song? = onlineOrNull {
        database.query { insert(songItem.toMediaMetadata()) }
        database.song(songItem.id).first()
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

    private val likedPlaylistId = "${MusicService.PLAYLIST}/${PlaylistEntity.LIKED_PLAYLIST_ID}"
    private val downloadedPlaylistId = "${MusicService.PLAYLIST}/${PlaylistEntity.DOWNLOADED_PLAYLIST_ID}"

    private fun likedSongsMediaItem(songCount: Int) = items.folder(
        id = likedPlaylistId,
        title = context.getString(R.string.liked_songs),
        subtitle = context.resources.getQuantityString(R.plurals.n_song, songCount, songCount),
        iconUri = items.drawableUri(R.drawable.favorite),
        mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
        itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
    )

    private fun downloadedSongsMediaItem(songCount: Int) = items.folder(
        id = downloadedPlaylistId,
        title = context.getString(R.string.downloaded_songs),
        subtitle = context.resources.getQuantityString(R.plurals.n_song, songCount, songCount),
        iconUri = items.drawableUri(R.drawable.download),
        mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
        itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM,
    )

    // Saved online playlists without local entries are browsed from their online source.
    private fun playlistFolder(playlist: Playlist, groupTitle: String? = null): MediaItem {
        val onlineId = playlist.playlist.browseId?.takeIf { playlist.songCount == 0 }
        return items.folder(
            id = if (onlineId != null) "${MusicService.YOUTUBE_PLAYLIST}/$onlineId" else "${MusicService.PLAYLIST}/${playlist.id}",
            title = playlist.playlist.name,
            subtitle = if (onlineId != null) {
                context.getString(R.string.car_online_source)
            } else {
                context.resources.getQuantityString(R.plurals.n_song, playlist.songCount, playlist.songCount)
            },
            iconUri = playlist.thumbnails.firstOrNull()?.toUri(),
            mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
            itemStyle = MediaConstants.EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM,
            groupTitle = groupTitle,
        )
    }

    private fun rememberShown(mediaItems: List<MediaItem>) {
        mediaItems.forEach { shownItems.put(it.mediaId, it) }
    }

    private fun hideExplicit() = context.dataStore.get(HideExplicitKey, false)

    private fun hideVideoSongs() =
        context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)

    private inline fun <T> onlineOrNull(block: () -> T?): T? =
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            reportException(e)
            null
        }

    private fun MediaLibraryService.LibraryParams?.rootChildrenLimit(): Int =
        this?.extras?.getInt(MediaConstants.EXTRAS_KEY_ROOT_CHILDREN_LIMIT, Int.MAX_VALUE)
            ?.takeIf { it > 0 }
            ?: Int.MAX_VALUE

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

    private data class OnlineSection(val title: String?, val items: List<YTItem>)

    private class CachedSections(val loadedAtMs: Long, val sections: List<OnlineSection>)

    companion object {
        private const val SHOWN_ITEMS_CACHE_SIZE = 1_000
        private const val ONLINE_CACHE_SIZE = 50
        private const val SEARCH_CACHE_SIZE = 5
        private const val ONLINE_CACHE_TTL_MS = 15 * 60_000L
        private const val LIBRARY_CHANGE_DEBOUNCE_MS = 1_000L
        private const val RECENT_EVENTS_SCANNED = 60
        private const val MAX_RECENT_SONGS = 10
        private const val MAX_QUICK_PICKS = 20
        private const val MAX_HOME_SECTIONS = 8
        private const val MAX_HOME_SECTION_ITEMS = 12
        private const val MAX_ONLINE_PLAYLIST_SONGS = 100
        private const val MAX_LOCAL_SEARCH_SONGS = 50
        private const val MAX_SEARCH_COLLECTIONS = 5

        /** Parent id for songs shown inside an online section; segments are encoded to stay slash-free. */
        private fun onlineSongParentId(parentId: String, sectionTitle: String?) =
            "${MusicService.ONLINE_SONG}/${Uri.encode(parentId)}/${Uri.encode(sectionTitle.orEmpty())}"

        private fun moodId(browseId: String, params: String?) =
            "${MusicService.MOOD}/${Uri.encode(browseId)}/${Uri.encode(params.orEmpty())}"
    }
}

internal fun <T> List<T>.paginate(page: Int, pageSize: Int): List<T> {
    if (page < 0 || pageSize < 1) return emptyList()

    val fromIndex = page.toLong() * pageSize
    if (fromIndex >= size) return emptyList()

    val toIndex = minOf(fromIndex + pageSize, size.toLong())
    return subList(fromIndex.toInt(), toIndex.toInt())
}
