package com.convx.music.playback.queues

import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.AlbumWithSongs
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class LocalAlbumRadioTest {
    private val songs = listOf(
        Song(SongEntity("hidden", "Hidden", explicit = true), emptyList()),
        Song(SongEntity("second", "Second"), emptyList()),
        Song(SongEntity("third", "Third"), emptyList()),
    )
    private val album = AlbumWithSongs(
        AlbumEntity("album", title = "Album", songCount = 3, duration = 60, isLocal = true),
        emptyList(), songs,
    )

    @Test fun filteredTrackTapStartsTheVisibleSongInTheFilteredQueue() = runBlocking {
        val queue = LocalAlbumRadio(album, startIndex = 1, initialSongs = songs.drop(1))
        val status = queue.getInitialStatus()
        assertEquals(listOf("second", "third"), status.items.map { it.mediaId })
        assertEquals("third", status.items[status.mediaItemIndex].mediaId)
    }

    @Test fun shuffledInitialSongsKeepTheirOrderAndLocalAlbumsDoNotRequestRadio() = runBlocking {
        val queue = LocalAlbumRadio(album, initialSongs = songs.drop(1).reversed())
        assertEquals(listOf("third", "second"), queue.getInitialStatus().items.map { it.mediaId })
        assertFalse(queue.hasNextPage())
        assertTrue(queue.nextPage().isEmpty())
    }
}
