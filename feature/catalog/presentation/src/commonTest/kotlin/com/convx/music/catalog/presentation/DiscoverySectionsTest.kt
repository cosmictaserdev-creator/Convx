package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.playback.DailyMix
import com.convx.music.playback.HistoryPlay
import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.TrackedRelease
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DiscoverySectionsTest {
    private fun play(id: String, art: String? = "art-$id") = HistoryPlay(id, "youtube-music", "Song $id", listOf("Artist"), art, 200_000, 1L)

    private fun mix(n: Int, vararg ids: String) = DailyMix("Daily Mix $n", "Artist and more", ids.map { play(it) })

    private fun release(id: String, year: Int = 2026, thumbnail: String? = "t$id") =
        TrackedRelease(id, "a1", "Artist", "Title $id", Kind.SINGLE, year, null, 0L, baseline = false, announced = false, thumbnailUrl = thumbnail)

    @Test
    fun nothingToShowGivesNoSections() {
        assertTrue(DiscoverySections.sections(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun mixesBecomeAShelfOfPlaylistEntriesWithUpToFourCovers() {
        val section = DiscoverySections.sections(listOf(mix(1, "a", "b", "c", "d", "e", "f"), mix(2, "g")), emptyList()).single()
        assertEquals("Made for you", section.title)
        val first = section.entries.first() as CatalogEntry.Playlist
        assertEquals("Daily Mix 1", first.title)
        assertEquals(4, first.artworkUrls.size)
        assertEquals(6, first.trackCount)
        assertEquals(CatalogId(DiscoverySections.MIX_PROVIDER, "0"), first.id)
    }

    @Test
    fun releasesBecomeAlbumEntriesThatOpenOnYouTubeMusic() {
        val section = DiscoverySections.sections(emptyList(), listOf(release("r1"), release("r2", year = 0, thumbnail = null))).single()
        assertEquals("New from your artists", section.title)
        val first = section.entries.first() as CatalogEntry.Album
        assertEquals(CatalogId("youtube-music", "r1"), first.id)
        assertEquals("2026", first.year)
        assertEquals("Artist", first.artists.single().name)
        val second = section.entries[1] as CatalogEntry.Album
        assertNull(second.year)
        assertTrue(second.artworkUrls.isEmpty())
    }

    @Test
    fun repeatedReleasesAreShownOnceAndTheShelfIsCapped() {
        val many = (1..30).map { release("r$it") } + release("r1")
        assertEquals(20, DiscoverySections.sections(emptyList(), many).single().entries.size)
    }

    @Test
    fun aMixEntryPlaysItsOwnTracks() {
        val mixes = listOf(mix(1, "a", "b"), mix(2, "c"))
        val tracks = DiscoverySections.mixTracks(mixes, CatalogId(DiscoverySections.MIX_PROVIDER, "1"))!!
        assertEquals(listOf("c"), tracks.map { it.id })
        assertEquals("youtube-music", tracks.single().providerId)
        assertEquals("art-c", tracks.single().artworkUrl)
    }

    @Test
    fun entriesThatAreNotMixesGiveNoTracks() {
        val mixes = listOf(mix(1, "a"))
        assertNull(DiscoverySections.mixTracks(mixes, CatalogId("youtube-music", "0")))
        assertNull(DiscoverySections.mixTracks(mixes, CatalogId(DiscoverySections.MIX_PROVIDER, "9")))
        assertNull(DiscoverySections.mixTracks(mixes, CatalogId(DiscoverySections.MIX_PROVIDER, "x")))
        assertNull(DiscoverySections.mixTracks(mixes, CatalogId(DiscoverySections.MIX_PROVIDER, "-1")))
    }

    @Test
    fun kickersNameWhyAShelfEntryIsFeatured() {
        val mixes = listOf(mix(1, "a"))
        val mixEntry = DiscoverySections.sections(mixes, emptyList()).single().entries.single()
        assertEquals("Made for You", DiscoverySections.kicker(mixEntry))
        val album = DiscoverySections.sections(emptyList(), listOf(release("r1"))).single().entries.single()
        assertEquals("New Release", DiscoverySections.kicker(album))
        assertNull(DiscoverySections.kicker(DiscoverySections.sections(emptyList(), listOf(release("r2", year = 0))).single().entries.single()))
    }
}
