package com.convx.music.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.koin.dsl.module
import org.koin.dsl.koinApplication

class PlaylistRepositoryCommonTest {
    @Test
    fun sharedKoinModuleResolvesPlaylistUseCaseAgainstPlatformRepository() = runTest {
        val repository = FakePlaylistRepository()
        val app = koinApplication {
            modules(
                libraryCoreModule,
                module { single<PlaylistRepository> { repository } },
            )
        }

        try {
            val useCase = app.koin.get<ObservePlaylists>()
            app.koin.get<SyncPlaylistTracks>()
            val result = useCase(PlaylistOrder.NAME, descending = false)
            val commands = app.koin.get<PlaylistCommands>()
            val duplicateIds = commands.duplicateSongIds("playlist-1", listOf("song-1", "song-2"))
            val added = commands.addSongs("playlist-1", listOf("song-3"))
            val pinUpdated = commands.togglePin("playlist-1")
            val bookmarkUpdated = commands.toggleBookmark("playlist-1")
            val renamed = commands.updatePlaylist("playlist-1", "Renamed", "Description")
            val linked = commands.linkPlaylistToRemote("playlist-1", "VLPL1")
            val merged = commands.mergePlaylist("playlist-1", "playlist-2")
            val deleted = commands.deletePlaylist("playlist-2")
            commands.clearPlaylistSongs("playlist-1")
            commands.normalizePlaylistSongPositions("playlist-1")
            val order = commands.movePlaylistSong("playlist-1", fromPosition = 1, toPosition = 3)
            val removal = commands.removePlaylistSong(
                PlaylistEntryReference(
                    playlistId = "playlist-1",
                    id = "entry-1",
                    songId = "song-2",
                    position = 3,
                ),
            )

            assertSame(repository.rows, result)
            assertEquals(PlaylistOrder.NAME, repository.lastOrder)
            assertEquals(false, repository.lastDescending)
            assertEquals(listOf("song-1"), duplicateIds)
            assertEquals("playlist-1", repository.checkedPlaylistId)
            assertEquals(listOf("song-1", "song-2"), repository.checkedSongIds)
            assertEquals(true, added)
            assertEquals("playlist-1", repository.addedPlaylistId)
            assertEquals(listOf("song-3"), repository.addedSongIds)
            assertEquals(true, pinUpdated)
            assertEquals("playlist-1", repository.pinnedPlaylistId)
            assertEquals(true, bookmarkUpdated)
            assertEquals("playlist-1", repository.bookmarkedPlaylistId)
            assertEquals(true, renamed)
            assertEquals("playlist-1", repository.updatedPlaylistId)
            assertEquals("Renamed", repository.updatedName)
            assertEquals("Description", repository.updatedDescription)
            assertEquals(true, linked)
            assertEquals("playlist-1", repository.linkedPlaylistId)
            assertEquals("VLPL1", repository.linkedBrowseId)
            assertEquals(true, merged)
            assertEquals("playlist-1", repository.mergedSourcePlaylistId)
            assertEquals("playlist-2", repository.mergedTargetPlaylistId)
            assertEquals(true, deleted)
            assertEquals("playlist-2", repository.deletedPlaylistId)
            assertEquals("playlist-1", repository.clearedPlaylistId)
            assertEquals("playlist-1", repository.normalizedPlaylistId)
            assertEquals("playlist-1", repository.movedPlaylistId)
            assertEquals(1 to 3, repository.movedPositions)
            assertEquals(listOf(PlaylistOrderEntry("song-1", "set-1")), order)
            assertEquals("playlist-1", repository.removedPlaylistId)
            assertEquals("entry-1", repository.removedEntry?.id)
            assertEquals(PlaylistSongRemoval(removed = true, setVideoId = "set-2"), removal)
        } finally {
            app.close()
        }
    }

    @Test
    fun libraryObservationFiltersShortsAndHonorsLocalOnly() = runTest {
        val repository = FakePlaylistRepository(
            listOf(
                playlistSummary("remote", browseId = "VLPL123"),
                playlistSummary("shorts", browseId = "SS123"),
                playlistSummary("local"),
            ),
        )
        val app = koinApplication {
            modules(
                libraryCoreModule,
                module { single<PlaylistRepository> { repository } },
            )
        }

        try {
            val observeLibraryPlaylists = app.koin.get<ObserveLibraryPlaylists>()
            val visible = observeLibraryPlaylists(
                order = PlaylistOrder.NAME,
                descending = false,
                hideYoutubeShorts = true,
                localOnly = false,
            ).first()
            val localOnly = observeLibraryPlaylists(
                order = PlaylistOrder.NAME,
                descending = false,
                hideYoutubeShorts = true,
                localOnly = true,
            ).first()

            assertEquals(listOf("remote", "local"), visible.map(PlaylistSummary::id))
            assertEquals(listOf("local"), localOnly.map(PlaylistSummary::id))
            assertEquals(PlaylistOrder.NAME, repository.lastOrder)
            assertEquals(false, repository.lastDescending)
        } finally {
            app.close()
        }
    }

    @Test
    fun playlistSyncSkipsAnIdenticalOrderAndClearsAnEmptyRemotePlaylist() = runTest {
        val repository = FakePlaylistRepository().apply {
            currentPlaylistSongIds = listOf("song-1", "song-2")
        }
        val sync = SyncPlaylistTracks(repository)

        val unchanged = sync("playlist-1", listOf(syncTrack("song-1"), syncTrack("song-2")))
        val cleared = sync("playlist-1", emptyList())

        assertEquals(PlaylistSyncResult.UP_TO_DATE, unchanged)
        assertEquals(PlaylistSyncResult.CLEARED, cleared)
        assertEquals("playlist-1", repository.clearedPlaylistId)
        assertEquals(null, repository.replacementTracks)
    }

    @Test
    fun playlistSyncReplacesTracksWhenIdsOrOrderDiffer() = runTest {
        val repository = FakePlaylistRepository().apply {
            currentPlaylistSongIds = listOf("song-1", "song-2")
        }
        val remoteTracks = listOf(syncTrack("song-2"), syncTrack("song-1"))

        val result = SyncPlaylistTracks(repository)("playlist-1", remoteTracks)

        assertEquals(PlaylistSyncResult.REPLACED, result)
        assertEquals("playlist-1", repository.replacedPlaylistId)
        assertEquals(remoteTracks, repository.replacementTracks)
    }

    @Test
    fun playlistSyncReportsWhenTheLocalPlaylistWasDeleted() = runTest {
        val repository = FakePlaylistRepository().apply {
            currentPlaylistSongIds = listOf("song-1")
            replacementSucceeds = false
        }

        val result = SyncPlaylistTracks(repository)("playlist-1", listOf(syncTrack("song-2")))

        assertEquals(PlaylistSyncResult.PLAYLIST_MISSING, result)
    }

    private class FakePlaylistRepository(
        playlists: List<PlaylistSummary> = emptyList(),
    ) : PlaylistRepository {
        val rows = flowOf(playlists)
        var lastOrder: PlaylistOrder? = null
        var lastDescending: Boolean? = null
        var checkedPlaylistId: String? = null
        var checkedSongIds: List<String>? = null
        var addedPlaylistId: String? = null
        var addedSongIds: List<String>? = null
        var pinnedPlaylistId: String? = null
        var bookmarkedPlaylistId: String? = null
        var updatedPlaylistId: String? = null
        var updatedName: String? = null
        var updatedDescription: String? = null
        var linkedPlaylistId: String? = null
        var linkedBrowseId: String? = null
        var mergedSourcePlaylistId: String? = null
        var mergedTargetPlaylistId: String? = null
        var deletedPlaylistId: String? = null
        var clearedPlaylistId: String? = null
        var normalizedPlaylistId: String? = null
        var movedPlaylistId: String? = null
        var movedPositions: Pair<Int, Int>? = null
        var removedPlaylistId: String? = null
        var removedEntry: PlaylistEntryReference? = null
        var currentPlaylistSongIds: List<String> = emptyList()
        var replacedPlaylistId: String? = null
        var replacementTracks: List<PlaylistSyncTrack>? = null
        var replacementSucceeds = true

        override fun observePlaylists(order: PlaylistOrder, descending: Boolean): Flow<List<PlaylistSummary>> {
            lastOrder = order
            lastDescending = descending
            return rows
        }

        override fun observePlaylist(playlistId: String): Flow<PlaylistDetail?> = flowOf(null)

        override fun observePlaylistTracks(playlistId: String): Flow<List<PlaylistTrack>> = flowOf(emptyList())

        override suspend fun playlistSongIds(playlistId: String): List<String> = currentPlaylistSongIds

        override suspend fun replacePlaylistSongs(playlistId: String, tracks: List<PlaylistSyncTrack>): Boolean {
            replacedPlaylistId = playlistId
            replacementTracks = tracks
            return replacementSucceeds
        }

        override suspend fun duplicateSongIds(playlistId: String, songIds: List<String>): List<String> {
            checkedPlaylistId = playlistId
            checkedSongIds = songIds
            return listOf("song-1")
        }

        override suspend fun addSongs(playlistId: String, songIds: List<String>): Boolean {
            addedPlaylistId = playlistId
            addedSongIds = songIds
            return true
        }

        override suspend fun togglePin(playlistId: String): Boolean {
            pinnedPlaylistId = playlistId
            return true
        }

        override suspend fun toggleBookmark(playlistId: String): Boolean? {
            bookmarkedPlaylistId = playlistId
            return true
        }

        override suspend fun updatePlaylist(playlistId: String, name: String, description: String?): Boolean {
            updatedPlaylistId = playlistId
            updatedName = name
            updatedDescription = description
            return true
        }

        override suspend fun linkPlaylistToRemote(playlistId: String, browseId: String): Boolean {
            linkedPlaylistId = playlistId
            linkedBrowseId = browseId
            return true
        }

        override suspend fun updatePlaylistThumbnail(playlistId: String, thumbnailUrl: String): Boolean = true

        override suspend fun mergePlaylist(sourcePlaylistId: String, targetPlaylistId: String): Boolean {
            mergedSourcePlaylistId = sourcePlaylistId
            mergedTargetPlaylistId = targetPlaylistId
            return true
        }

        override suspend fun deletePlaylist(playlistId: String): Boolean {
            deletedPlaylistId = playlistId
            return true
        }

        override suspend fun clearPlaylistSongs(playlistId: String) {
            clearedPlaylistId = playlistId
        }

        override suspend fun normalizePlaylistSongPositions(playlistId: String) {
            normalizedPlaylistId = playlistId
        }

        override suspend fun movePlaylistSong(
            playlistId: String,
            fromPosition: Int,
            toPosition: Int,
        ): List<PlaylistOrderEntry> {
            movedPlaylistId = playlistId
            movedPositions = fromPosition to toPosition
            return listOf(PlaylistOrderEntry("song-1", "set-1"))
        }

        override suspend fun removePlaylistSong(
            entry: PlaylistEntryReference,
        ): PlaylistSongRemoval {
            removedPlaylistId = entry.playlistId
            removedEntry = entry
            return PlaylistSongRemoval(removed = true, setVideoId = "set-2")
        }
    }

    private fun playlistSummary(id: String, browseId: String? = null) = PlaylistSummary(
        id = id,
        name = id,
        artworkUrls = emptyList(),
        songCount = 0,
        isEditable = true,
        isBookmarked = false,
        isLocal = browseId == null,
        browseId = browseId,
    )

    private fun syncTrack(id: String) = PlaylistSyncTrack(
        id = id,
        title = "Title $id",
        artists = listOf(PlaylistSyncArtist(id = null, name = "Artist")),
        durationSeconds = 120,
        thumbnailUrl = null,
        album = null,
        setVideoId = null,
        musicVideoType = null,
        isExplicit = false,
        libraryAddToken = null,
        libraryRemoveToken = null,
        isPodcastEpisode = false,
    )
}
