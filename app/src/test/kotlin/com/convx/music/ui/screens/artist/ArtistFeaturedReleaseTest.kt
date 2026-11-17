package com.convx.music.ui.screens.artist

import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.pages.ArtistSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArtistFeaturedReleaseTest {
    private fun album(id: String, year: Int? = null, artist: String = "owner") =
        AlbumItem(id, "playlist-$id", title = id, artists = listOf(Artist(artist, artist)), year = year, thumbnail = "")

    private fun shelf(title: String, vararg albums: AlbumItem) = ArtistSection(title, albums.toList(), null)

    @Test fun latestShelfWins() {
        val latest = album("latest", 2020)
        assertEquals("Latest release" to latest, listOf(shelf("Albums", album("newer", 2026)), shelf("Latest release", latest)).featuredRelease("owner"))
    }

    @Test fun newerSingleWinsAcrossShelves() {
        val single = album("single", 2026)
        assertEquals("Singles" to single, listOf(shelf("Albums", album("old", 2010)), shelf("Singles", single)).featuredRelease("owner"))
    }

    @Test fun equalYearsKeepPageOrder() {
        val first = album("first", 2026)
        assertEquals("Albums" to first, listOf(shelf("Albums", first, album("second", 2026)), shelf("Singles", album("third", 2026))).featuredRelease("owner"))
    }

    @Test fun unknownYearsKeepOriginalLeadItem() {
        val first = album("first")
        assertEquals("Albums" to first, listOf(shelf("Empty"), shelf("Albums", first), shelf("Singles", album("second"))).featuredRelease("owner"))
    }

    @Test fun otherArtistsAreExcluded() {
        val own = album("own", 2020)
        assertEquals("Albums" to own, listOf(shelf("Albums", own), shelf("Appears on", album("other", 2026, "other"))).featuredRelease("owner"))
    }

    @Test fun knownYearBeatsUnknownYear() {
        val dated = album("dated", 2020)
        assertEquals("Singles" to dated, listOf(shelf("Albums", album("unknown")), shelf("Singles", dated)).featuredRelease("owner"))
    }

    @Test fun missingArtistMetadataIsAllowed() {
        val unknown = album("unknown", 2026).copy(artists = null)
        assertEquals("Albums" to unknown, listOf(shelf("Albums", unknown)).featuredRelease("owner"))
    }

    @Test fun emptyPageHasNoRelease() {
        assertNull(emptyList<ArtistSection>().featuredRelease("owner"))
    }
}
