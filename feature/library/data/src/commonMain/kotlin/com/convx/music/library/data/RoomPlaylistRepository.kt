package com.convx.music.library.data

import com.convx.music.library.PlaylistDetail
import com.convx.music.library.PlaylistEntryReference
import com.convx.music.library.MusicLibraryWriter
import com.convx.music.library.PlaylistOrder
import com.convx.music.library.PlaylistOrderEntry
import com.convx.music.library.PlaylistRepository
import com.convx.music.library.PlaylistSongRemoval
import com.convx.music.library.PlaylistSummary
import com.convx.music.library.PlaylistSyncTrack
import com.convx.music.library.PlaylistTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Room-backed implementation used by the new iOS and desktop clients. */
class RoomPlaylistRepository(
    private val database: MusicLibraryDatabase,
) : PlaylistRepository, MusicLibraryWriter {
    private val dao = database.musicLibraryDao()

    override fun observePlaylists(order: PlaylistOrder, descending: Boolean): Flow<List<PlaylistSummary>> =
        dao.observePlaylists().map { rows ->
            val comparator = when (order) {
                PlaylistOrder.CREATED -> compareBy<MusicLibraryPlaylistWithCount> { it.playlist.createdAtEpochMillis }
                PlaylistOrder.NAME -> Comparator { first, second ->
                    first.playlist.name.compareTo(second.playlist.name, ignoreCase = true)
                }
                PlaylistOrder.SONG_COUNT -> compareBy { it.trackCount }
                PlaylistOrder.LAST_UPDATED -> compareBy { it.playlist.lastUpdatedAtEpochMillis }
            }.thenBy { it.playlist.id }
            rows.sortedWith(if (descending) comparator.reversed() else comparator).map(MusicLibraryPlaylistWithCount::toSummary)
        }

    override fun observePlaylist(playlistId: String): Flow<PlaylistDetail?> =
        dao.observePlaylist(playlistId).map { it?.toDetail() }

    override fun observePlaylistTracks(playlistId: String): Flow<List<PlaylistTrack>> =
        dao.observeEntriesWithTracks(playlistId).map { entries -> entries.map(MusicLibraryEntryWithTrack::toPlaylistTrack) }

    override suspend fun playlistSongIds(playlistId: String): List<String> = dao.getSongIds(playlistId)

    override suspend fun savePlaylist(playlist: PlaylistDetail) {
        val existing = dao.getPlaylist(playlist.id)
        dao.upsertPlaylist(
            MusicLibraryPlaylistRecord(
                id = playlist.id,
                name = playlist.name,
                browseId = playlist.browseId,
                createdAtEpochMillis = existing?.createdAtEpochMillis ?: nowEpochMillis(),
                lastUpdatedAtEpochMillis = nowEpochMillis(),
                isEditable = playlist.isEditable,
                bookmarkedAtEpochMillis = if (playlist.isBookmarked) {
                    existing?.bookmarkedAtEpochMillis ?: nowEpochMillis()
                } else {
                    null
                },
                remoteSongCount = playlist.remoteSongCount,
                playEndpointParams = playlist.playEndpointParams,
                thumbnailUrl = playlist.sourceThumbnailUrl,
                artworkUrlsJson = playlist.artworkUrls.encodeStoredStringList(),
                description = playlist.description,
                shuffleEndpointParams = playlist.shuffleEndpointParams,
                radioEndpointParams = playlist.radioEndpointParams,
                isLocal = playlist.isLocal,
                isAutoSync = existing?.isAutoSync ?: false,
                pinnedAtEpochMillis = playlist.pinnedAtEpochMillis ?: existing?.pinnedAtEpochMillis,
            ),
        )
    }

    override suspend fun createLocalPlaylist(name: String, description: String?): String? {
        val normalizedName = name.trim()
        if (normalizedName.isEmpty()) return null
        var id: String
        do {
            id = "LP${kotlin.random.Random.nextLong().toULong().toString(36)}"
        } while (dao.getPlaylist(id) != null)
        val now = nowEpochMillis()
        dao.upsertPlaylist(
            MusicLibraryPlaylistRecord(
                id = id,
                name = normalizedName,
                browseId = null,
                createdAtEpochMillis = now,
                lastUpdatedAtEpochMillis = now,
                isEditable = true,
                bookmarkedAtEpochMillis = null,
                remoteSongCount = null,
                playEndpointParams = null,
                thumbnailUrl = null,
                artworkUrlsJson = emptyList<String>().encodeStoredStringList(),
                description = description,
                shuffleEndpointParams = null,
                radioEndpointParams = null,
                isLocal = true,
                isAutoSync = false,
                pinnedAtEpochMillis = null,
            ),
        )
        return id
    }

    override suspend fun saveTracks(tracks: List<PlaylistSyncTrack>) {
        require(tracks.all { it.id.isNotBlank() && it.title.isNotBlank() })
        dao.insertTracks(tracks.map(PlaylistSyncTrack::toRecord))
    }

    override suspend fun replacePlaylistSongs(playlistId: String, tracks: List<PlaylistSyncTrack>): Boolean {
        if (tracks.any { it.id.isBlank() || it.title.isBlank() }) return false
        return dao.replacePlaylistTracks(playlistId, tracks)
    }

    override suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String> {
        if (songIds.isEmpty()) return emptyList()
        val duplicates = dao.getDuplicateSongIds(playlistId, songIds).toSet()
        return songIds.distinct().filter(duplicates::contains)
    }

    override suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean =
        dao.appendSongs(playlistId, songIds)

    override suspend fun togglePin(playlistId: String): Boolean {
        return dao.togglePlaylistPin(playlistId, nowEpochMillis())
    }

    override suspend fun toggleBookmark(playlistId: String): Boolean? =
        dao.togglePlaylistBookmark(playlistId, nowEpochMillis())

    override suspend fun updatePlaylist(playlistId: String, name: String, description: String?): Boolean {
        if (name.isBlank()) return false
        val playlist = dao.getPlaylist(playlistId) ?: return false
        dao.updatePlaylist(
            playlist.copy(
                name = name,
                description = description,
                lastUpdatedAtEpochMillis = nowEpochMillis(),
            ),
        )
        return true
    }

    override suspend fun linkPlaylistToRemote(playlistId: String, browseId: String): Boolean {
        if (browseId.isBlank()) return false
        val playlist = dao.getPlaylist(playlistId) ?: return false
        dao.updatePlaylist(playlist.copy(browseId = browseId))
        return true
    }

    override suspend fun updatePlaylistThumbnail(playlistId: String, thumbnailUrl: String): Boolean {
        if (thumbnailUrl.isBlank()) return false
        val playlist = dao.getPlaylist(playlistId) ?: return false
        dao.updatePlaylist(
            playlist.copy(
                thumbnailUrl = thumbnailUrl,
                artworkUrlsJson = listOf(thumbnailUrl).encodeStoredStringList(),
            ),
        )
        return true
    }

    override suspend fun mergePlaylist(sourcePlaylistId: String, targetPlaylistId: String): Boolean {
        if (sourcePlaylistId == targetPlaylistId) return false
        return dao.mergeEntries(sourcePlaylistId, targetPlaylistId)
    }

    override suspend fun deletePlaylist(playlistId: String): Boolean {
        if (dao.getPlaylist(playlistId) == null) return false
        dao.deletePlaylist(playlistId)
        return true
    }

    override suspend fun clearPlaylistSongs(playlistId: String) {
        dao.clearEntries(playlistId)
    }

    override suspend fun normalizePlaylistSongPositions(playlistId: String) {
        dao.normalizeEntryPositions(playlistId)
    }

    override suspend fun movePlaylistSong(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    ): List<PlaylistOrderEntry> {
        return dao.moveEntry(playlistId, fromPosition, toPosition)
            .map { PlaylistOrderEntry(it.songId, it.setVideoId) }
    }

    override suspend fun removePlaylistSong(entry: PlaylistEntryReference): PlaylistSongRemoval {
        val entryId = entry.id.toLongOrNull()
            ?: return PlaylistSongRemoval(removed = false, setVideoId = null)
        val storedEntry = dao.removeEntryAndNormalize(entry.playlistId, entryId, entry.songId)
            ?: return PlaylistSongRemoval(removed = false, setVideoId = null)
        return PlaylistSongRemoval(removed = true, setVideoId = entry.setVideoId ?: storedEntry.setVideoId)
    }
}

private fun MusicLibraryPlaylistWithCount.toSummary(): PlaylistSummary = PlaylistSummary(
    id = playlist.id,
    name = playlist.name,
    artworkUrls = playlist.artworkUrlsJson.decodeStoredStringList(),
    songCount = trackCount,
    isEditable = playlist.isEditable,
    isBookmarked = playlist.bookmarkedAtEpochMillis != null,
    isLocal = playlist.isLocal,
    remoteSongCount = playlist.remoteSongCount,
    browseId = playlist.browseId,
    sourceThumbnailUrl = playlist.thumbnailUrl,
    pinnedAtEpochMillis = playlist.pinnedAtEpochMillis,
)

private fun MusicLibraryPlaylistWithCount.toDetail(): PlaylistDetail = PlaylistDetail(
    id = playlist.id,
    name = playlist.name,
    artworkUrls = playlist.artworkUrlsJson.decodeStoredStringList(),
    songCount = trackCount,
    remoteSongCount = playlist.remoteSongCount,
    browseId = playlist.browseId,
    sourceThumbnailUrl = playlist.thumbnailUrl,
    isEditable = playlist.isEditable,
    isBookmarked = playlist.bookmarkedAtEpochMillis != null,
    isLocal = playlist.isLocal,
    pinnedAtEpochMillis = playlist.pinnedAtEpochMillis,
    description = playlist.description,
    playEndpointParams = playlist.playEndpointParams,
    shuffleEndpointParams = playlist.shuffleEndpointParams,
    radioEndpointParams = playlist.radioEndpointParams,
)

private fun MusicLibraryEntryWithTrack.toPlaylistTrack(): PlaylistTrack = PlaylistTrack(
    entryId = entry.id.toString(),
    creationOrder = entry.id,
    songId = track.id,
    position = entry.position,
    setVideoId = entry.setVideoId,
    title = track.title,
    artistNames = track.artistNamesJson.decodeStoredStringList(),
    albumTitle = track.albumTitle,
    durationMillis = track.durationMillis,
    thumbnailUrl = track.thumbnailUrl,
    totalPlayTimeMillis = track.totalPlayTimeMillis,
    isExplicit = track.isExplicit,
    isVideo = track.isVideo,
    isPodcastEpisode = track.isPodcastEpisode,
)

internal fun PlaylistSyncTrack.toRecord(): MusicLibraryTrackRecord = MusicLibraryTrackRecord(
    id = id,
    title = title,
    artistNamesJson = artists.map { it.name }.encodeStoredStringList(),
    albumTitle = album?.title,
    durationMillis = durationSeconds?.takeIf { it >= 0 }?.toLong()?.times(1_000),
    thumbnailUrl = thumbnailUrl,
    totalPlayTimeMillis = 0,
    isExplicit = isExplicit,
    isVideo = musicVideoType != null,
    isPodcastEpisode = isPodcastEpisode,
    musicVideoType = musicVideoType,
    libraryAddToken = libraryAddToken,
    libraryRemoveToken = libraryRemoveToken,
)
