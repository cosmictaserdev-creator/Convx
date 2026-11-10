package com.convx.music.client.desktop

import com.convx.music.library.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

internal sealed interface DesktopLocalRoute {
    data class Collection(val destination: DesktopDestination) : DesktopLocalRoute
    data class Album(val key: String) : DesktopLocalRoute
    data class Artist(val name: String) : DesktopLocalRoute
    data class Playlist(val id: String) : DesktopLocalRoute
    data class Folder(val path: String) : DesktopLocalRoute
}

private data class LocalNavigation(val route: DesktopLocalRoute, val history: List<DesktopLocalRoute> = emptyList())
internal data class DesktopLocalGroup(val key: String, val title: String, val subtitle: String, val artwork: String?, val count: Int)
internal data class DesktopLocalSong(val track: DesktopLibraryTrack, val playlistEntry: PlaylistEntryReference? = null, val position: Int = 0)
internal data class DesktopLocalLibraryPage(
    val route: DesktopLocalRoute = DesktopLocalRoute.Collection(DesktopDestination.LOCAL_MUSIC),
    val title: String = "Local Music",
    val canGoBack: Boolean = false,
    val query: String = "",
    val songs: List<DesktopLocalSong> = emptyList(),
    val groups: List<DesktopLocalGroup> = emptyList(),
    val playlists: List<PlaylistSummary> = emptyList(),
    val media: DesktopMediaLibraryState = DesktopMediaLibraryState(),
    val message: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class DesktopLocalLibraryStateHolder(
    private val media: DesktopMediaLibrary,
    private val playlists: PlaylistRepository,
    private val writer: MusicLibraryWriter,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val navigation = MutableStateFlow(LocalNavigation(DesktopLocalRoute.Collection(DesktopDestination.LOCAL_MUSIC)))
    private val query = MutableStateFlow("")
    private val message = MutableStateFlow<String?>(null)
    private val playlistData = combine(
        playlists.observePlaylists(PlaylistOrder.NAME, false),
        navigation.map { (it.route as? DesktopLocalRoute.Playlist)?.id }.distinctUntilChanged().flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else playlists.observePlaylistTracks(id)
        },
    ) { all, songs -> all.filter { it.isLocal } to songs }
    val state = combine(media.state, navigation, query, playlistData, message) { library, nav, search, playlist, error ->
        buildPage(library, nav, search, playlist.first, playlist.second, error)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), DesktopLocalLibraryPage())

    fun show(destination: DesktopDestination, search: String = "") {
        val target = DesktopLocalRoute.Collection(destination)
        if (navigation.value.route != target) navigation.value = LocalNavigation(target)
        query.value = if (destination == DesktopDestination.SEARCH) search else ""
        message.value = null
    }

    fun search(value: String) { query.value = value }
    fun openAlbum(key: String) = open(DesktopLocalRoute.Album(key))
    fun openArtist(name: String) = open(DesktopLocalRoute.Artist(name))
    fun openPlaylist(id: String) = open(DesktopLocalRoute.Playlist(id))
    fun openFolder(path: String) = open(DesktopLocalRoute.Folder(path))
    private fun open(route: DesktopLocalRoute) {
        val current = navigation.value
        navigation.value = LocalNavigation(route, current.history + current.route)
        query.value = ""
        message.value = null
    }
    fun goBack() {
        val current = navigation.value
        val previous = current.history.lastOrNull() ?: return
        navigation.value = LocalNavigation(previous, current.history.dropLast(1))
        query.value = ""
    }

    fun createPlaylist(name: String) = action {
        val id = writer.createLocalPlaylist(name) ?: return@action false
        openPlaylist(id)
        true
    }
    fun renamePlaylist(id: String, name: String) = action { playlists.updatePlaylist(id, name, null) }
    fun deletePlaylist(id: String) = action {
        val removed = playlists.deletePlaylist(id)
        if (removed) show(DesktopDestination.PLAYLISTS)
        removed
    }
    fun addToPlaylist(playlistId: String, trackId: String) = action { playlists.addSongs(playlistId, listOf(trackId)) }
    fun removeSong(entry: PlaylistEntryReference) = action { playlists.removePlaylistSong(entry); true }
    fun moveSong(id: String, from: Int, to: Int) = action { playlists.movePlaylistSong(id, from, to); true }
    private fun action(block: suspend () -> Boolean) = scope.launch {
        try { message.value = if (block()) null else "Couldn't update your playlist. Try again." }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { message.value = "Couldn't update your playlist. Try again." }
    }

    fun close() = scope.cancel()

    private fun buildPage(media: DesktopMediaLibraryState, nav: LocalNavigation, query: String, playlists: List<PlaylistSummary>, entries: List<PlaylistTrack>, error: String?): DesktopLocalLibraryPage {
        val all = media.index.tracks
        val collection = (nav.route as? DesktopLocalRoute.Collection)?.destination
        val rootSongs = when (val route = nav.route) {
            is DesktopLocalRoute.Album -> all.filter { albumKey(it) == route.key }
            is DesktopLocalRoute.Artist -> all.filter { route.name in it.artists }
            is DesktopLocalRoute.Playlist -> emptyList()
            is DesktopLocalRoute.Folder -> all.filter { java.nio.file.Path.of(it.filePath).startsWith(java.nio.file.Path.of(route.path)) }
            is DesktopLocalRoute.Collection -> when (route.destination) {
                DesktopDestination.HISTORY -> all.filter { it.lastPlayedAt != null }.sortedByDescending { it.lastPlayedAt }
                DesktopDestination.LIKED -> all.filter { it.favorite }
                DesktopDestination.LIBRARY -> all.sortedByDescending { it.addedAt }
                else -> all.sortedBy { it.title.lowercase() }
            }
        }
        val songs = if (nav.route is DesktopLocalRoute.Playlist) entries.mapNotNull { entry ->
            all.firstOrNull { it.id == entry.songId || it.downloadedFrom?.id == entry.songId }?.let {
                DesktopLocalSong(it, PlaylistEntryReference(nav.route.id, entry.entryId, entry.songId, entry.position), entry.position)
            }
        } else rootSongs.map { DesktopLocalSong(it) }
        val filtered = songs.filter { query.isBlank() || "${it.track.title} ${it.track.artists.joinToString()} ${it.track.album.orEmpty()}".contains(query, true) }
        val groups = when (collection) {
            DesktopDestination.ALBUMS -> all.groupBy(::albumKey).map { (key, tracks) -> DesktopLocalGroup(key,
                tracks.first().album ?: "Unknown Album", tracks.first().artists.joinToString().ifBlank { "Unknown Artist" },
                tracks.firstNotNullOfOrNull { it.artworkPath }, tracks.size) }
            DesktopDestination.ARTISTS -> all.flatMap { track -> track.artists.map { it to track } }.groupBy({ it.first }, { it.second }).map { (name, tracks) ->
                DesktopLocalGroup(name, name, "${tracks.size} songs", tracks.firstNotNullOfOrNull { it.artworkPath }, tracks.size) }
            else -> emptyList()
        }.filter { query.isBlank() || "${it.title} ${it.subtitle}".contains(query, true) }.sortedBy { it.title.lowercase() }
        val title = when (val route = nav.route) {
            is DesktopLocalRoute.Album -> all.firstOrNull { albumKey(it) == route.key }?.album ?: "Album"
            is DesktopLocalRoute.Artist -> route.name
            is DesktopLocalRoute.Playlist -> playlists.firstOrNull { it.id == route.id }?.name ?: "Playlist"
            is DesktopLocalRoute.Folder -> java.io.File(route.path).name.ifBlank { route.path }
            is DesktopLocalRoute.Collection -> if (route.destination == DesktopDestination.HOME) "Your Music" else route.destination.title
        }
        return DesktopLocalLibraryPage(nav.route, title, nav.history.isNotEmpty(), query, filtered, groups,
            playlists.filter { query.isBlank() || it.name.contains(query, true) }, media, error)
    }

    companion object {
        fun albumKey(track: DesktopLibraryTrack) = "${track.artists.firstOrNull().orEmpty().lowercase()}\u0000${track.album.orEmpty().lowercase()}"
    }
}
