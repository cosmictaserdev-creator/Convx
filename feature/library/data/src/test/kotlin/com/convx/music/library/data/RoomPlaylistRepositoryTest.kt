package com.convx.music.library.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.library.MusicLibraryWriter
import com.convx.music.library.PlaylistDetail
import com.convx.music.library.PlaylistEntryReference
import com.convx.music.library.PlaylistOrder
import com.convx.music.library.PlaylistRepository
import com.convx.music.library.PlaylistSyncAlbum
import com.convx.music.library.PlaylistSyncArtist
import com.convx.music.library.PlaylistSyncTrack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoomPlaylistRepositoryTest {
    @Test
    fun localPlaylistPersistsTrackMetadataAndSupportsQueueOrderChanges() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaylistRepository(database)
            val writer: MusicLibraryWriter = repository
            val playlistId = assertNotNull(writer.createLocalPlaylist("  Road mix  ", "Driving"))
            writer.saveTracks(listOf(track("one"), track("two")))

            assertTrue(repository.addSongs(playlistId, listOf("one", "two", "one")))
            val initial = repository.observePlaylistTracks(playlistId).first()
            assertEquals(listOf("one", "two", "one"), initial.map { it.songId })
            assertEquals(listOf("Artist one"), initial.first().artistNames)
            assertEquals("Album one", initial.first().albumTitle)

            repository.movePlaylistSong(playlistId, fromPosition = 0, toPosition = 2)
            assertEquals(listOf("two", "one", "one"), repository.observePlaylistTracks(playlistId).first().map { it.songId })
            assertEquals(listOf("one"), repository.duplicateSongIds(playlistId, listOf("one", "missing")))
        } finally {
            database.close()
        }
    }

    @Test
    fun replacingRemotePlaylistTracksIsAtomicAndRetainsTrackPlayTime() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaylistRepository(database)
            val writer: MusicLibraryWriter = repository
            val playlistId = "remote-playlist"
            writer.savePlaylist(remotePlaylist(playlistId))
            database.musicLibraryDao().insertTrack(track("song-1").toRecord().copy(totalPlayTimeMillis = 18_000))

            assertTrue(repository.replacePlaylistSongs(playlistId, listOf(track("song-1"), track("song-2"))))
            val entries = repository.observePlaylistTracks(playlistId).first()
            assertEquals(listOf("song-1", "song-2"), entries.map { it.songId })
            assertEquals(18_000, entries.first().totalPlayTimeMillis)
            assertEquals(listOf("cover-song-1", "cover-song-2"), repository.observePlaylist(playlistId).first()?.artworkUrls)
            assertEquals(2, repository.observePlaylist(playlistId).first()?.remoteSongCount)

            assertFalse(repository.replacePlaylistSongs("missing", listOf(track("song-3"))))
            assertEquals(listOf("song-1", "song-2"), repository.playlistSongIds(playlistId))
        } finally {
            database.close()
        }
    }

    @Test
    fun playlistCommandsKeepEntriesConsistentAndDeleteCascades() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaylistRepository(database)
            val writer: MusicLibraryWriter = repository
            val playlistId = assertNotNull(writer.createLocalPlaylist("Queue"))
            writer.saveTracks(listOf(track("a"), track("b")))
            repository.addSongs(playlistId, listOf("a", "b"))
            val first = repository.observePlaylistTracks(playlistId).first().first()

            val removed = repository.removePlaylistSong(
                PlaylistEntryReference(
                    playlistId = playlistId,
                    id = first.entryId,
                    songId = first.songId,
                    position = first.position,
                    setVideoId = first.setVideoId,
                ),
            )
            assertTrue(removed.removed)
            assertEquals(listOf("b"), repository.observePlaylistTracks(playlistId).first().map { it.songId })
            assertTrue(repository.togglePin(playlistId))
            assertTrue(repository.observePlaylists(PlaylistOrder.CREATED, descending = false).first().single().pinnedAtEpochMillis != null)
            assertEquals(true, repository.toggleBookmark(playlistId))
            assertEquals(false, repository.toggleBookmark(playlistId))
            assertTrue(repository.deletePlaylist(playlistId))
            assertNull(repository.observePlaylist(playlistId).first())
        } finally {
            database.close()
        }
    }

    private fun inMemoryDatabase(): MusicLibraryDatabase =
        Room.inMemoryDatabaseBuilder<MusicLibraryDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()

    private fun track(id: String) = PlaylistSyncTrack(
        id = id,
        title = "Track $id",
        artists = listOf(PlaylistSyncArtist(id = "artist-$id", name = "Artist $id")),
        durationSeconds = 181,
        thumbnailUrl = "cover-$id",
        album = PlaylistSyncAlbum(id = "album-$id", title = "Album $id"),
        setVideoId = "set-$id",
        musicVideoType = null,
        isExplicit = false,
        libraryAddToken = null,
        libraryRemoveToken = null,
        isPodcastEpisode = false,
    )

    private fun remotePlaylist(id: String) = PlaylistDetail(
        id = id,
        name = "Remote",
        artworkUrls = emptyList(),
        songCount = 0,
        remoteSongCount = null,
        browseId = id,
        sourceThumbnailUrl = null,
        isEditable = true,
        isBookmarked = false,
        isLocal = false,
        pinnedAtEpochMillis = null,
        description = null,
        playEndpointParams = null,
        shuffleEndpointParams = null,
        radioEndpointParams = null,
    )
}
