package com.convx.music.client.desktop

import com.convx.music.library.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.lang.reflect.Proxy
import kotlin.test.*

class DesktopLocalNavigationTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun previousOnlineSearchCannotHideSongsWhenOpeningLocalMusic() = runBlocking {
        val writer = RecordingLibraryWriter()
        val media = DesktopMediaLibrary(File(temporary.root, "index.json"), File(temporary.root, "art"), writer)
        media.awaitReady()
        media.updateIndex { it.copy(tracks = listOf(DesktopLibraryTrack("local:first", "first.wav", "My local song"),
            DesktopLibraryTrack("local:second", "second.wav", "Another song"))) }
        val playlists = Proxy.newProxyInstance(PlaylistRepository::class.java.classLoader, arrayOf(PlaylistRepository::class.java)) { _, method, _ ->
            when (method.name) {
                "observePlaylists" -> flowOf(emptyList<PlaylistSummary>())
                "observePlaylistTracks" -> flowOf(emptyList<PlaylistTrack>())
                else -> error("Unexpected playlist operation: ${method.name}")
            }
        } as PlaylistRepository
        val holder = DesktopLocalLibraryStateHolder(media, playlists, writer, Dispatchers.Default)
        try {
            holder.show(DesktopDestination.LOCAL_MUSIC, "an unrelated online artist")
            val library = withTimeout(5_000) { holder.state.first { it.songs.size == 2 } }
            assertEquals("", library.query)
            holder.show(DesktopDestination.SEARCH, "Another")
            val search = withTimeout(5_000) { holder.state.first { it.query == "Another" } }
            assertEquals(listOf("Another song"), search.songs.map { it.track.title })
            holder.show(DesktopDestination.ALBUMS, "Another")
            withTimeout(5_000) { holder.state.first { it.title == "Albums" && it.query.isEmpty() } }
            Unit
        } finally { holder.close(); media.close() }
    }
}
