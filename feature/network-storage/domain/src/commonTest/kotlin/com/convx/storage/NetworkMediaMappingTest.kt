package com.convx.storage

import com.convx.storage.domain.NetworkEntry
import com.convx.storage.domain.NetworkMedia
import com.convx.storage.domain.NetworkMediaUri
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NetworkMediaMappingTest {
    private fun song(path: String) = NetworkEntry(path, path.substringAfterLast('/'), false, 10)
    private fun image(path: String) = NetworkEntry(path, path.substringAfterLast('/'), false, 10)

    @Test fun artistAndTitleAreSplitWhenThePrefixLooksLikeAnArtist() {
        val track = NetworkMedia.track("nas", song("Music/Daft Punk - One More Time.flac"), emptyList())
        assertEquals("Daft Punk", track.artist)
        assertEquals("One More Time", track.title)
    }

    @Test fun aRemasterSuffixIsPartOfTheTitleNotATitle() {
        val track = NetworkMedia.track("nas", song("Flat/Song Name - Remastered 2011.flac"), emptyList())
        assertNull(track.artist)
        assertEquals("Song Name - Remastered 2011", track.title)
    }

    @Test fun liveAndYearSuffixesAreNotMistakenForTitles() {
        assertNull(NetworkMedia.track("nas", song("Flat/Another Tune - Live at Wembley.flac"), emptyList()).artist)
        assertNull(NetworkMedia.track("nas", song("Flat/Some Song - 2009 Mix.mp3"), emptyList()).artist)
    }

    @Test fun realArtistTitlePairsThatMerelyStartWithQualifierWordsKeepTheirArtist() {
        assertEquals("Queen", NetworkMedia.track("nas", song("Flat/Queen - Radio Ga Ga.mp3"), emptyList()).artist)
        assertEquals("Oasis", NetworkMedia.track("nas", song("Flat/Oasis - Live Forever.mp3"), emptyList()).artist)
        assertEquals("Prince", NetworkMedia.track("nas", song("Flat/Prince - 1999.mp3"), emptyList()).artist)
        assertEquals("Bruce Springsteen", NetworkMedia.track("nas", song("Flat/Bruce Springsteen - Cover Me.mp3"), emptyList()).artist)
    }

    @Test fun trackNumbersAreNeverArtists() {
        val dashed = NetworkMedia.track("nas", song("Flat/1-01 - Intro.flac"), emptyList())
        assertNull(dashed.artist)
        assertEquals("Intro", dashed.title)
        val plain = NetworkMedia.track("nas", song("Flat/01 - Intro.flac"), emptyList())
        assertNull(plain.artist)
        assertEquals("Intro", plain.title)
    }

    @Test fun severalUnrelatedImagesDoNotBecomeEveryTracksCover() {
        val images = listOf(image("Flat/IMG_0001.jpg"), image("Flat/booklet_p3.jpg"))
        assertNull(NetworkMedia.track("nas", song("Flat/A.flac"), images).artwork)
    }

    @Test fun aLoneImageIsTreatedAsTheCover() {
        val images = listOf(image("Album/scan.jpg"))
        assertEquals(NetworkMediaUri.create("nas", "Album/scan.jpg"), NetworkMedia.track("nas", song("Album/A.flac"), images).artwork)
    }

    @Test fun aWellKnownCoverNameWinsOverOtherImages() {
        val images = listOf(image("Album/IMG_1.jpg"), image("Album/Folder.jpg"), image("Album/back.jpg"))
        assertEquals(NetworkMediaUri.create("nas", "Album/Folder.jpg"), NetworkMedia.track("nas", song("Album/A.flac"), images).artwork)
    }

    @Test fun sameNamedFoldersKeepTheirOwnPathSoAlbumsCanBeToldApart() {
        val queen = NetworkMedia.track("nas", song("Queen/Greatest Hits/01 Track.mp3"), emptyList())
        val abba = NetworkMedia.track("nas", song("ABBA/Greatest Hits/01 Track.mp3"), emptyList())
        assertEquals("Greatest Hits", queen.album)
        assertEquals(queen.album, abba.album)
        assertEquals("Queen/Greatest Hits", queen.folder)
        assertEquals("ABBA/Greatest Hits", abba.folder)
    }
}
