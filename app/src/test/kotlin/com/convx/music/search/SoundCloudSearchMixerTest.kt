package com.convx.music.search

import com.music.innertube.models.Artist
import com.music.innertube.models.SongItem
import org.junit.Assert.assertEquals
import org.junit.Test

class SoundCloudSearchMixerTest {
    private fun song(id: String, title: String = id, artist: String = "Artist") =
        SongItem(id, title, listOf(Artist(artist, null)), thumbnail = "")
    private fun youtube(count: Int) = (1..count).map { song("yt$it") }
    private fun source(count: Int) = (1..count).map { song("sc:artist/track$it") }

    @Test fun insertsAfterEveryThirdSongAndPreservesBothOrders() {
        val yt = youtube(7)
        val sc = source(3)
        assertEquals(listOf(yt[0], yt[1], yt[2], sc[0], yt[3], yt[4], yt[5], sc[1], yt[6]), mixSoundCloud(yt, sc))
    }

    @Test fun capsSourceTracksAtFive() {
        val result = mixSoundCloud(youtube(30), source(10))
        assertEquals(5, result.count { it.id.startsWith("sc:") })
        assertEquals(youtube(30), result.filterNot { it.id.startsWith("sc:") })
    }

    @Test fun shortListsAppendAtMostTwoTracks() {
        assertEquals(youtube(2) + source(2), mixSoundCloud(youtube(2), source(5)))
        assertEquals(youtube(1) + source(2), mixSoundCloud(youtube(1), source(5)))
    }

    @Test fun sourceOnlyResultsRemainVisibleWhenYoutubeIsEmpty() {
        assertEquals(source(2), mixSoundCloud(emptyList(), source(5)))
    }

    @Test fun emptySourceLeavesYoutubeUntouched() {
        assertEquals(youtube(4), mixSoundCloud(youtube(4), emptyList()))
    }

    @Test fun bothEmptyReturnsEmpty() {
        assertEquals(emptyList<SongItem>(), mixSoundCloud(emptyList(), emptyList()))
    }

    @Test fun duplicateTitleAndArtistAreNormalized() {
        val yt = listOf(song("yt", "  Song   Title ", "ARTIST"))
        val duplicate = song("sc:artist/song", "song title", " artist ")
        assertEquals(yt, mixSoundCloud(yt, listOf(duplicate)))
    }

    @Test fun sameTitleByDifferentArtistRemains() {
        val yt = listOf(song("yt", "Song", "First"))
        val sc = song("sc:second/song", "Song", "Second")
        assertEquals(yt + sc, mixSoundCloud(yt, listOf(sc)))
    }

    @Test fun duplicateSourceIdsAreNotInsertedTwice() {
        val sc = source(1).single()
        assertEquals(1, mixSoundCloud(youtube(6), listOf(sc, sc)).count { it.id == sc.id })
    }

    @Test fun urlAndLongQueryLikeTitlesAreHandledAsText() {
        val title = "https://example.com/" + "long".repeat(1000)
        val yt = listOf(song("yt", title))
        assertEquals(yt, mixSoundCloud(yt, listOf(song("sc:artist/track", title))))
    }
}
