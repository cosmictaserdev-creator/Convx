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

    @Test fun artistsThatStartWithADigitAreNotTrackNumbers() {
        for ((file, artist, title) in listOf(
            Triple("Flat/50 Cent - In da Club.mp3", "50 Cent", "In da Club"),
            Triple("Flat/21 Savage - a lot.mp3", "21 Savage", "a lot"),
            Triple("Flat/3 Doors Down - Kryptonite.mp3", "3 Doors Down", "Kryptonite"),
            Triple("Flat/5 Seconds of Summer - Youngblood.mp3", "5 Seconds of Summer", "Youngblood"),
        )) {
            val track = NetworkMedia.track("nas", song(file), emptyList())
            assertEquals(artist, track.artist, file)
            assertEquals(title, track.title, file)
            assertNull(track.trackNumber, file)
        }
    }

    @Test fun zeroPaddedAndDashedNumbersAreStillTrackNumbers() {
        assertEquals(1, NetworkMedia.track("nas", song("Album/01 Intro.flac"), emptyList()).trackNumber)
        assertEquals("Intro", NetworkMedia.track("nas", song("Album/01 Intro.flac"), emptyList()).title)
        assertEquals(5, NetworkMedia.track("nas", song("Album/5. Song.flac"), emptyList()).trackNumber)
        assertEquals(12, NetworkMedia.track("nas", song("Album/12 - Song.flac"), emptyList()).trackNumber)
    }
}
