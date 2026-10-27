package com.convx.music.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

enum class PlaylistOrder {
    CREATED,
    NAME,
    SONG_COUNT,
    LAST_UPDATED,
}

enum class PlaylistTrackOrder {
    CUSTOM,
    CREATED,
    NAME,
    ARTIST,
    PLAY_TIME,
}

/** Client-independent playlist entry and the track metadata needed by library screens. */
data class PlaylistTrack(
    val entryId: String,
    val creationOrder: Long,
    val songId: String,
    val position: Int,
    val setVideoId: String?,
    val title: String,
    val artistNames: List<String>,
    val albumTitle: String?,
    val durationMillis: Long?,
    val thumbnailUrl: String?,
    val totalPlayTimeMillis: Long,
    val isExplicit: Boolean,
    val isVideo: Boolean,
    val isPodcastEpisode: Boolean,
)

data class PlaylistSyncArtist(
    val id: String?,
    val name: String,
)

data class PlaylistSyncAlbum(
    val id: String,
    val title: String,
)

/** Remote playlist song data mapped into the portable persistence boundary. */
data class PlaylistSyncTrack(
    val id: String,
    val title: String,
    val artists: List<PlaylistSyncArtist>,
    val durationSeconds: Int?,
    val thumbnailUrl: String?,
    val album: PlaylistSyncAlbum?,
    val setVideoId: String?,
    val musicVideoType: String?,
    val isExplicit: Boolean,
    val libraryAddToken: String?,
    val libraryRemoveToken: String?,
    val isPodcastEpisode: Boolean,
)

/** Applies the shared playlist track ordering rules using the platform's text collation. */
fun orderPlaylistTracks(
    tracks: List<PlaylistTrack>,
    order: PlaylistTrackOrder,
    descending: Boolean,
    hideVideos: Boolean,
    compareText: (String, String) -> Int,
): List<PlaylistTrack> {
    val visibleTracks = if (hideVideos) tracks.filterNot(PlaylistTrack::isVideo) else tracks
    val orderedTracks = when (order) {
        PlaylistTrackOrder.CUSTOM -> visibleTracks
        PlaylistTrackOrder.CREATED -> visibleTracks.sortedBy(PlaylistTrack::creationOrder)
        PlaylistTrackOrder.NAME -> visibleTracks.sortedWith { first, second ->
            compareText(first.title, second.title)
        }
        PlaylistTrackOrder.ARTIST -> visibleTracks
            .sortedWith { first, second ->
                compareText(first.artistNames.joinToString(""), second.artistNames.joinToString(""))
            }
            .groupBy(PlaylistTrack::albumTitle)
            .values
            .flatMap { albumTracks -> albumTracks.sortedBy { it.artistNames.joinToString("") } }
        PlaylistTrackOrder.PLAY_TIME -> visibleTracks.sortedBy(PlaylistTrack::totalPlayTimeMillis)
    }

    return if (descending && order != PlaylistTrackOrder.CUSTOM) orderedTracks.reversed() else orderedTracks
}

/** Platform-neutral playlist fields used by library rows, navigation, and grouping. */
interface PlaylistCardData {
    val id: String
    val name: String
    val artworkUrls: List<String>
    val songCount: Int
    val remoteSongCount: Int?
    val browseId: String?
    val sourceThumbnailUrl: String?
    val isEditable: Boolean
    val isBookmarked: Boolean
    val isLocal: Boolean
    val pinnedAtEpochMillis: Long?
}

/** A client-independent playlist row for library navigation and presentation. */
data class PlaylistSummary(
    override val id: String,
    override val name: String,
    override val artworkUrls: List<String>,
    override val songCount: Int,
    override val isEditable: Boolean,
    override val isBookmarked: Boolean,
    override val isLocal: Boolean,
    override val remoteSongCount: Int? = null,
    override val browseId: String? = null,
    override val sourceThumbnailUrl: String? = null,
    override val pinnedAtEpochMillis: Long? = null,
) : PlaylistCardData

/** Portable metadata used by a playlist detail screen and its platform actions. */
data class PlaylistDetail(
    override val id: String,
    override val name: String,
    override val artworkUrls: List<String>,
    override val songCount: Int,
    override val remoteSongCount: Int?,
    override val browseId: String?,
    override val sourceThumbnailUrl: String?,
    override val isEditable: Boolean,
    override val isBookmarked: Boolean,
    override val isLocal: Boolean,
    override val pinnedAtEpochMillis: Long?,
    val description: String?,
    val playEndpointParams: String?,
    val shuffleEndpointParams: String?,
    val radioEndpointParams: String?,
) : PlaylistCardData {
    val shareLink: String?
        get() = browseId?.let { "https://music.youtube.com/playlist?list=$it" }
}

/** Opaque, portable reference to one playlist entry. Duplicate songs remain distinct. */
data class PlaylistEntryReference(
    val playlistId: String,
    val id: String,
    val songId: String,
    val position: Int,
    val setVideoId: String? = null,
)

/** Remote identifiers in the current local order, used by platform sync adapters. */
data class PlaylistOrderEntry(
    val songId: String,
    val setVideoId: String?,
)

data class PlaylistSongRemoval(
    val removed: Boolean,
    val setVideoId: String?,
)

/** Playlist access. Storage implementations map their own rows to these models. */
interface PlaylistRepository {
    fun observePlaylists(order: PlaylistOrder, descending: Boolean): Flow<List<PlaylistSummary>>
    fun observePlaylist(playlistId: String): Flow<PlaylistDetail?>
    fun observePlaylistTracks(playlistId: String): Flow<List<PlaylistTrack>>
    suspend fun playlistSongIds(playlistId: String): List<String>
    suspend fun replacePlaylistSongs(playlistId: String, tracks: List<PlaylistSyncTrack>): Boolean
    suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String>
    suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean
    suspend fun togglePin(playlistId: String): Boolean
    suspend fun toggleBookmark(playlistId: String): Boolean?
    suspend fun updatePlaylist(playlistId: String, name: String, description: String?): Boolean
    suspend fun linkPlaylistToRemote(playlistId: String, browseId: String): Boolean
    suspend fun updatePlaylistThumbnail(playlistId: String, thumbnailUrl: String): Boolean
    suspend fun mergePlaylist(sourcePlaylistId: String, targetPlaylistId: String): Boolean
    suspend fun deletePlaylist(playlistId: String): Boolean
    suspend fun clearPlaylistSongs(playlistId: String)
    suspend fun normalizePlaylistSongPositions(playlistId: String)
    suspend fun movePlaylistSong(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    ): List<PlaylistOrderEntry>
    suspend fun removePlaylistSong(entry: PlaylistEntryReference): PlaylistSongRemoval
}

/** Writes portable library data without exposing Room entities to clients or feature modules. */
interface MusicLibraryWriter {
    suspend fun savePlaylist(playlist: PlaylistDetail)
    suspend fun createLocalPlaylist(name: String, description: String? = null): String?
    suspend fun saveTracks(tracks: List<PlaylistSyncTrack>)
}

class ObservePlaylists(
    private val repository: PlaylistRepository,
) {
    operator fun invoke(order: PlaylistOrder, descending: Boolean): Flow<List<PlaylistSummary>> =
        repository.observePlaylists(order, descending)
}

class ObservePlaylist(
    private val repository: PlaylistRepository,
) {
    operator fun invoke(playlistId: String): Flow<PlaylistDetail?> =
        repository.observePlaylist(playlistId)
}

class ObservePlaylistTracks(
    private val repository: PlaylistRepository,
) {
    operator fun invoke(playlistId: String): Flow<List<PlaylistTrack>> =
        repository.observePlaylistTracks(playlistId)
}

enum class PlaylistSyncResult {
    CLEARED,
    UP_TO_DATE,
    REPLACED,
    PLAYLIST_MISSING,
}

class SyncPlaylistTracks(
    private val repository: PlaylistRepository,
) {
    suspend operator fun invoke(
        playlistId: String,
        remoteTracks: List<PlaylistSyncTrack>,
    ): PlaylistSyncResult {
        if (remoteTracks.isEmpty()) {
            repository.clearPlaylistSongs(playlistId)
            return PlaylistSyncResult.CLEARED
        }

        val remoteIds = remoteTracks.map(PlaylistSyncTrack::id)
        if (repository.playlistSongIds(playlistId) == remoteIds) return PlaylistSyncResult.UP_TO_DATE

        return if (repository.replacePlaylistSongs(playlistId, remoteTracks)) {
            PlaylistSyncResult.REPLACED
        } else {
            PlaylistSyncResult.PLAYLIST_MISSING
        }
    }
}

class ObserveLibraryPlaylists(
    private val repository: PlaylistRepository,
) {
    operator fun invoke(
        order: PlaylistOrder,
        descending: Boolean,
        hideYoutubeShorts: Boolean,
        localOnly: Boolean,
    ): Flow<List<PlaylistSummary>> =
        repository.observePlaylists(order, descending).map { playlists ->
            playlists
                .distinctBy(PlaylistSummary::id)
                .filter { !localOnly || it.browseId == null }
                .filterNot { hideYoutubeShorts && it.browseId?.startsWith("SS") == true }
        }
}

class PlaylistCommands(
    private val repository: PlaylistRepository,
) {
    suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String> =
        repository.duplicateSongIds(playlistId, songIds)

    suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean =
        repository.addSongs(playlistId, songIds)

    suspend fun togglePin(playlistId: String): Boolean =
        repository.togglePin(playlistId)

    suspend fun toggleBookmark(playlistId: String): Boolean? =
        repository.toggleBookmark(playlistId)

    suspend fun updatePlaylist(playlistId: String, name: String, description: String?): Boolean =
        repository.updatePlaylist(playlistId, name, description)

    suspend fun linkPlaylistToRemote(playlistId: String, browseId: String): Boolean =
        repository.linkPlaylistToRemote(playlistId, browseId)

    suspend fun updatePlaylistThumbnail(playlistId: String, thumbnailUrl: String): Boolean =
        repository.updatePlaylistThumbnail(playlistId, thumbnailUrl)

    suspend fun mergePlaylist(sourcePlaylistId: String, targetPlaylistId: String): Boolean =
        repository.mergePlaylist(sourcePlaylistId, targetPlaylistId)

    suspend fun deletePlaylist(playlistId: String): Boolean =
        repository.deletePlaylist(playlistId)

    suspend fun clearPlaylistSongs(playlistId: String) =
        repository.clearPlaylistSongs(playlistId)

    suspend fun normalizePlaylistSongPositions(playlistId: String) =
        repository.normalizePlaylistSongPositions(playlistId)

    suspend fun movePlaylistSong(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    ): List<PlaylistOrderEntry> =
        repository.movePlaylistSong(playlistId, fromPosition, toPosition)

    suspend fun removePlaylistSong(
        entry: PlaylistEntryReference,
    ): PlaylistSongRemoval =
        repository.removePlaylistSong(entry)
}

/** Common Koin bindings consumed by Android, iOS, and desktop composition roots. */
val libraryCoreModule = module {
    factoryOf(::ObservePlaylists)
    factoryOf(::ObservePlaylist)
    factoryOf(::ObservePlaylistTracks)
    factoryOf(::SyncPlaylistTracks)
    factoryOf(::ObserveLibraryPlaylists)
    factoryOf(::PlaylistCommands)
    factoryOf(::SetMusicLibraryTrackSaved)
    factoryOf(::AddSongToMusicPlaylist)
    factoryOf(::CreateMusicPlaylist)
}
