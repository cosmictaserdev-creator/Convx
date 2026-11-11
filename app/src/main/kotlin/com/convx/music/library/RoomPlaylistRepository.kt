package com.convx.music.library

import com.convx.music.constants.PlaylistSortType
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.PlaylistSong
import com.convx.music.models.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId

class RoomPlaylistRepository(
    private val database: MusicDatabase,
) : PlaylistRepository, LocalPlaylistSongDataSource {

    override fun observeSongs(playlistId: String): Flow<List<LocalPlaylistSong>> =
        database.playlistSongs(playlistId).map { songs -> songs.map(PlaylistSong::toLocalPlaylistSong) }

    override suspend fun loadSongs(playlistId: String): List<LocalPlaylistSong> = withContext(Dispatchers.IO) {
        database.playlistSongs(playlistId).firstOrNull().orEmpty().map(PlaylistSong::toLocalPlaylistSong)
    }

    override fun observePlaylists(order: PlaylistOrder, descending: Boolean): Flow<List<PlaylistSummary>> =
        database.playlists(order.toRoomSortType(), descending).map { playlists ->
            playlists.map { playlist ->
                PlaylistSummary(
                    id = playlist.id,
                    name = playlist.playlist.name,
                    artworkUrls = playlist.thumbnails,
                    songCount = playlist.songCount,
                    remoteSongCount = playlist.playlist.remoteSongCount,
                    isEditable = playlist.playlist.isEditable,
                    isBookmarked = playlist.playlist.bookmarkedAt != null,
                    isLocal = playlist.playlist.isLocal,
                    browseId = playlist.playlist.browseId,
                    sourceThumbnailUrl = playlist.playlist.thumbnailUrl,
                    pinnedAtEpochMillis = playlist.playlist.pinnedAt
                        ?.atZone(ZoneId.systemDefault())
                        ?.toInstant()
                        ?.toEpochMilli(),
                )
            }
        }

    override fun observePlaylist(playlistId: String): Flow<PlaylistDetail?> =
        database.playlist(playlistId).map { it?.toPlaylistDetail() }

    override fun observePlaylistTracks(playlistId: String): Flow<List<PlaylistTrack>> =
        database.playlistSongs(playlistId).map { songs -> songs.map(PlaylistSong::toPlaylistTrack) }

    override suspend fun playlistSongIds(playlistId: String): List<String> = withContext(Dispatchers.IO) {
        database.playlistSongIds(playlistId)
    }

    override suspend fun replacePlaylistSongs(
        playlistId: String,
        tracks: List<PlaylistSyncTrack>,
    ): Boolean = withContext(Dispatchers.IO) {
        if (database.playlist(playlistId).firstOrNull() == null) return@withContext false
        database.withTransaction {
            database.clearPlaylist(playlistId)
            tracks.forEachIndexed { position, track ->
                database.insert(track.toMediaMetadata())
                database.insert(
                    PlaylistSongMap(
                        songId = track.id,
                        playlistId = playlistId,
                        position = position,
                        setVideoId = track.setVideoId,
                    ),
                )
            }
        }
        true
    }

    override suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String> =
        withContext(Dispatchers.IO) {
            if (songIds.isEmpty()) emptyList() else database.playlistDuplicates(playlistId, songIds).distinct()
        }

    override suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean =
        withContext(Dispatchers.IO) {
            val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
            if (songIds.isNotEmpty()) database.addSongToPlaylist(playlist, songIds)
            true
        }

    override suspend fun togglePin(playlistId: String): Boolean = withContext(Dispatchers.IO) {
        val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
        database.query { update(playlist.playlist.togglePin()) }
        true
    }

    override suspend fun toggleBookmark(playlistId: String): Boolean? = withContext(Dispatchers.IO) {
        val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext null
        val isBookmarked = playlist.playlist.bookmarkedAt == null
        database.withTransaction { update(playlist.playlist.localToggleLike()) }
        isBookmarked
    }

    override suspend fun updatePlaylist(
        playlistId: String,
        name: String,
        description: String?,
    ): Boolean = withContext(Dispatchers.IO) {
        val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
        database.withTransaction {
            update(
                playlist.playlist.copy(
                    name = name,
                    description = description,
                    lastUpdateTime = LocalDateTime.now(),
                ),
            )
        }
        true
    }

    override suspend fun linkPlaylistToRemote(playlistId: String, browseId: String): Boolean =
        withContext(Dispatchers.IO) {
            val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
            database.query { update(playlist.playlist.copy(browseId = browseId)) }
            true
        }

    override suspend fun updatePlaylistThumbnail(playlistId: String, thumbnailUrl: String): Boolean =
        withContext(Dispatchers.IO) {
            val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
            database.query { update(playlist.playlist.copy(thumbnailUrl = thumbnailUrl)) }
            true
        }

    override suspend fun mergePlaylist(sourcePlaylistId: String, targetPlaylistId: String): Boolean =
        withContext(Dispatchers.IO) {
            if (sourcePlaylistId == targetPlaylistId) return@withContext false
            val source = database.playlist(sourcePlaylistId).firstOrNull()
                ?: return@withContext false
            val target = database.playlist(targetPlaylistId).firstOrNull()
                ?: return@withContext false
            if (!source.playlist.isEditable || !target.playlist.isEditable) return@withContext false
            database.mergePlaylists(target, listOf(sourcePlaylistId))
            true
        }

    override suspend fun deletePlaylist(playlistId: String): Boolean = withContext(Dispatchers.IO) {
        val playlist = database.playlist(playlistId).firstOrNull() ?: return@withContext false
        database.withTransaction { delete(playlist.playlist) }
        true
    }

    override suspend fun clearPlaylistSongs(playlistId: String) = withContext(Dispatchers.IO) {
        database.withTransaction { clearPlaylist(playlistId) }
    }

    override suspend fun normalizePlaylistSongPositions(playlistId: String) = withContext(Dispatchers.IO) {
        val songs = database.playlistSongMaps(playlistId, 0)
            .sortedWith(compareBy({ it.position }, { it.id }))
        database.withTransaction {
            songs.forEachIndexed { index, song ->
                if (song.position != index) update(song.copy(position = index))
            }
        }
    }

    override suspend fun movePlaylistSong(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    ): List<PlaylistOrderEntry> = withContext(Dispatchers.IO) {
        database.withTransaction { move(playlistId, fromPosition, toPosition) }
        database.playlistSongMaps(playlistId, 0).map { entry ->
            PlaylistOrderEntry(songId = entry.songId, setVideoId = entry.setVideoId)
        }
    }

    override suspend fun removePlaylistSong(
        entry: PlaylistEntryReference,
    ): PlaylistSongRemoval = withContext(Dispatchers.IO) {
        val mapId = entry.id.toIntOrNull()
            ?: return@withContext PlaylistSongRemoval(removed = false, setVideoId = null)
        val map = database.playlistSongMaps(entry.songId)
            .firstOrNull { it.id == mapId && it.playlistId == entry.playlistId }
            ?: return@withContext PlaylistSongRemoval(removed = false, setVideoId = null)
        val setVideoId = entry.setVideoId ?: database.getSetVideoId(entry.songId)?.setVideoId
        database.withTransaction {
            move(entry.playlistId, map.position, Int.MAX_VALUE)
            delete(map.copy(position = Int.MAX_VALUE))
        }
        PlaylistSongRemoval(removed = true, setVideoId = setVideoId)
    }

    private fun PlaylistOrder.toRoomSortType(): PlaylistSortType = when (this) {
        PlaylistOrder.CREATED -> PlaylistSortType.CREATE_DATE
        PlaylistOrder.NAME -> PlaylistSortType.NAME
        PlaylistOrder.SONG_COUNT -> PlaylistSortType.SONG_COUNT
        PlaylistOrder.LAST_UPDATED -> PlaylistSortType.LAST_UPDATED
    }
}

internal fun Playlist.toPlaylistDetail(): PlaylistDetail = PlaylistDetail(
    id = id,
    name = playlist.name,
    artworkUrls = thumbnails,
    songCount = songCount,
    remoteSongCount = playlist.remoteSongCount,
    browseId = playlist.browseId,
    sourceThumbnailUrl = playlist.thumbnailUrl,
    isEditable = playlist.isEditable,
    isBookmarked = playlist.bookmarkedAt != null,
    isLocal = playlist.isLocal,
    pinnedAtEpochMillis = playlist.pinnedAt
        ?.atZone(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli(),
    description = playlist.description,
    playEndpointParams = playlist.playEndpointParams,
    shuffleEndpointParams = playlist.shuffleEndpointParams,
    radioEndpointParams = playlist.radioEndpointParams,
)

private fun PlaylistSong.toPlaylistTrack(): PlaylistTrack = PlaylistTrack(
    entryId = map.id.toString(),
    creationOrder = map.id.toLong(),
    songId = song.id,
    position = map.position,
    setVideoId = map.setVideoId,
    title = song.title,
    artistNames = song.artists.map { it.name },
    albumTitle = song.album?.title,
    durationMillis = song.song.duration.takeIf { it >= 0 }?.toLong()?.times(1_000L),
    thumbnailUrl = song.song.thumbnailUrl,
    totalPlayTimeMillis = song.song.totalPlayTime,
    isExplicit = song.song.explicit,
    isVideo = song.song.isVideo,
    isPodcastEpisode = song.song.isPodcastEpisode,
)

private fun PlaylistSong.toLocalPlaylistSong(): LocalPlaylistSong = LocalPlaylistSong(
    track = toPlaylistTrack(),
    map = map,
    song = song,
)

private fun PlaylistSyncTrack.toMediaMetadata(): MediaMetadata = MediaMetadata(
    id = id,
    title = title,
    artists = artists.map { MediaMetadata.Artist(id = it.id, name = it.name) },
    duration = durationSeconds ?: -1,
    thumbnailUrl = thumbnailUrl,
    album = album?.let { MediaMetadata.Album(id = it.id, title = it.title) },
    setVideoId = setVideoId,
    musicVideoType = musicVideoType,
    explicit = isExplicit,
    libraryAddToken = libraryAddToken,
    libraryRemoveToken = libraryRemoveToken,
    isPodcastEpisode = isPodcastEpisode,
)
