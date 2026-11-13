package com.convx.storage

import com.convx.storage.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class NetworkLibraryScannerTest {
    @Test fun scansFoldersOnceAndUsesArtworkFromTheTracksOwnFolder() = runTest {
        val requested = mutableListOf<String>()
        val scanner = NetworkLibraryScanner { folder ->
            requested += folder
            when (folder) {
                "" -> listOf(NetworkEntry("Album", "Album", true), NetworkEntry("Album", "Album", true),
                    NetworkEntry("cover.jpg", "cover.jpg", false))
                "Album" -> listOf(NetworkEntry("", "root", true),
                    NetworkEntry("Album/Artist - Song.FLAC", "Artist - Song.FLAC", false, 100),
                    NetworkEntry("Album/cover.jpg", "cover.jpg", false))
                else -> error("Unexpected folder")
            }
        }
        val result = scanner.scan("nas")
        assertEquals(listOf("", "Album"), requested)
        assertFalse(result.truncated)
        val track = result.tracks.single()
        assertEquals("Song", track.title)
        assertEquals("Artist", track.artist)
        assertEquals("Album", track.album)
        assertEquals(NetworkMediaUri.create("nas", "Album/cover.jpg"), track.artwork)
        assertEquals(100, track.size)
    }

    @Test fun enforcesTheTrackLimitWithoutCountingDuplicatesTwice() = runTest {
        val first = NetworkEntry("song.mp3", "song.mp3", false)
        val scanner = NetworkLibraryScanner { listOf(first, first, NetworkEntry("next.mp3", "next.mp3", false)) }
        val result = scanner.scan("nas", maxTracks = 1)
        assertEquals(1, result.tracks.size)
        assertTrue(result.truncated)
        assertFalse(NetworkLibraryScanner { listOf(first, first) }.scan("nas", 1).truncated)
    }

    @Test fun cancellationPropagatesAndDoesNotPublishAPartialLibrary() = runTest {
        val failure = CancellationException("User cancelled scan")
        assertSame(failure, assertFailsWith<CancellationException> {
            NetworkLibraryScanner { throw failure }.scan("nas")
        })
    }
}
