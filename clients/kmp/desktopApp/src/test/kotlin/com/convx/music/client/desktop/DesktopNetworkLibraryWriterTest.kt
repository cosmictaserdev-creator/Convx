package com.convx.music.client.desktop

import com.convx.storage.domain.ImportedSong
import com.convx.storage.domain.NetworkMediaUri
import com.convx.storage.domain.NetworkTrack
import com.convx.storage.domain.SongArtworkState
import com.convx.storage.presentation.NETWORK_MUSIC_PROVIDER
import androidx.room.Room
import com.convx.music.library.data.MusicLibraryDatabase
import com.convx.music.library.data.RoomPlaylistRepository
import com.convx.music.library.data.buildMusicLibraryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DesktopNetworkLibraryWriterTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun importsPersistAndSurviveLocalFolderRescan() = runBlocking {
        val index = File(temporary.root, "index.json")
        val writer = RecordingLibraryWriter()
        val library = DesktopMediaLibrary(index, File(temporary.root, "art"), writer)
        val adapter = DesktopNetworkLibraryWriter(library)
        val id = NetworkMediaUri.create("nas", "Album/song.flac")
        val cover = File(temporary.root, "cover.png").apply { writeBytes(byteArrayOf(1)) }
        val track = NetworkTrack(id, "Song", "Artist", "Album", null, 100, discNumber = 2, trackNumber = 3)
        assertEquals(SongArtworkState.NEW, adapter.artworkState(id))
        adapter.write(listOf(ImportedSong(track, "album", 0, cover.toURI().toString())), emptyList())
        assertEquals(SongArtworkState.PRESENT, adapter.artworkState(id))
        assertEquals(NETWORK_MUSIC_PROVIDER, library.state.value.index.tracks.single().request().providerId)
        withTimeout(10_000) { library.rescan().join() }
        assertEquals(id, library.state.value.index.tracks.single().id)
        library.close()

        val reopened = DesktopMediaLibrary(index, File(temporary.root, "art"), writer)
        reopened.awaitReady()
        val saved = assertNotNull(reopened.state.value.index.tracks.singleOrNull())
        assertEquals(2, saved.discNumber)
        assertEquals(3, saved.trackNumber)
        assertEquals(NETWORK_MUSIC_PROVIDER, saved.request().providerId)
        reopened.close()
    }

    @Test fun importedTrackCanBeAddedToAReopenedPlaylist() = runBlocking {
        val databaseFile = File(temporary.root, "library.db")
        val database = buildMusicLibraryDatabase(Room.databaseBuilder<MusicLibraryDatabase>(databaseFile.absolutePath))
        val playlists = RoomPlaylistRepository(database)
        val index = File(temporary.root, "index.json")
        val library = DesktopMediaLibrary(index, File(temporary.root, "art"), playlists)
        val id = NetworkMediaUri.create("nas", "Album/song.flac")
        val track = NetworkTrack(id, "Song", "Artist", "Album", null, 100)
        DesktopNetworkLibraryWriter(library).write(listOf(ImportedSong(track, "album", 0, null)), emptyList())
        val playlistId = assertNotNull(playlists.createLocalPlaylist("Network songs"))
        assertTrue(playlists.addSongs(playlistId, listOf(id)))
        library.close()
        database.close()

        val reopened = buildMusicLibraryDatabase(Room.databaseBuilder<MusicLibraryDatabase>(databaseFile.absolutePath))
        try {
            val entry = RoomPlaylistRepository(reopened).observePlaylistTracks(playlistId).first().single()
            assertEquals(id, entry.songId)
            assertEquals("Song", entry.title)
        } finally {
            reopened.close()
        }
    }
}
