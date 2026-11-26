package com.convx.music.playback

import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.ReleaseRadarRules.epochDayOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseRadarPlannerTest {
    private val today = epochDayOf("2026-10-18")!!
    private val now = 1_800_000_000_000L

    private fun seen(id: String, title: String, kind: Kind = Kind.ALBUM, year: Int = 2026) = SeenRelease(id, title, kind, year)

    private fun classify(
        vararg releases: SeenRelease,
        known: Set<String> = emptySet(),
        firstLook: Boolean = false,
        dates: Map<String, String> = emptyMap(),
    ) = ReleaseRadarPlanner.classify("a1", "Artist", releases.toList(), known, firstLook, dates, today, now)

    private fun tracked(id: String, artistId: String = "a1", name: String = "Artist", title: String = "T", kind: Kind = Kind.SINGLE,
                         baseline: Boolean = false, announced: Boolean = false) =
        TrackedRelease(id, artistId, name, title, kind, 2026, null, now, baseline, announced)

    @Test
    fun knownReleasesAreNotNewAgain() {
        assertEquals(listOf("b"), classify(seen("a", "A"), seen("b", "B"), known = setOf("a")).map { it.id })
    }

    @Test
    fun repeatedIdsOnAPageCountOnce() {
        assertEquals(1, classify(seen("a", "A"), seen("a", "A again")).size)
    }

    @Test
    fun anArtistAlreadyLookedAtGetsNewReleasesAnnouncable() {
        val release = classify(seen("a", "A")).single()
        assertFalse(release.baseline)
        assertFalse(release.announced)
    }

    @Test
    fun aFirstLookKeepsTheBackCatalogueQuietly() {
        val release = classify(seen("a", "Old", year = 2019), firstLook = true).single()
        assertTrue(release.baseline)
        assertTrue(release.announced)
    }

    @Test
    fun aFirstLookStillShowsAReleaseOutWithinTheLastMonthButDoesNotAnnounceIt() {
        val dates = DeezerDates.index(listOf("Fresh" to "2026-10-05"))
        val release = classify(seen("a", "Fresh"), firstLook = true, dates = dates).single()
        assertFalse(release.baseline)
        assertTrue(release.announced)
        assertEquals(epochDayOf("2026-10-05"), release.releaseEpochDay)
    }

    @Test
    fun aFirstLookTreatsAReleaseOverAMonthOldAsBackCatalogue() {
        val dates = DeezerDates.index(listOf("Stale" to "2026-09-01"))
        assertTrue(classify(seen("a", "Stale"), firstLook = true, dates = dates).single().baseline)
    }

    @Test
    fun aDateFromAnotherYearThanTheReleaseIsIgnored() {
        val dates = DeezerDates.index(listOf("Reissue" to "2015-03-04"))
        val release = classify(seen("a", "Reissue", year = 2026), dates = dates).single()
        assertNull(release.releaseEpochDay)
    }

    @Test
    fun anUnknownYearAcceptsAnyDate() {
        val dates = DeezerDates.index(listOf("X" to "2015-03-04"))
        assertEquals(epochDayOf("2015-03-04"), classify(seen("a", "X", year = 0), dates = dates).single().releaseEpochDay)
    }

    @Test
    fun aReleaseAnnouncedForTheFutureIsNotBackCatalogue() {
        val dates = DeezerDates.index(listOf("Soon" to "2026-10-23"))
        assertFalse(classify(seen("a", "Soon"), firstLook = true, dates = dates).single().baseline)
    }

    @Test
    fun deezerPlaceholderAndInvalidDatesAreDropped() {
        val dates = DeezerDates.index(listOf("A" to "0000-00-00", "B" to "2026-02-30", "C" to "garbage", "D" to "2026-10-01"))
        assertNull(DeezerDates.dateFor(dates, "A"))
        assertNull(DeezerDates.dateFor(dates, "B"))
        assertNull(DeezerDates.dateFor(dates, "C"))
        assertEquals("2026-10-01", DeezerDates.dateFor(dates, "D"))
    }

    @Test
    fun editionsSharingATitleKeepTheEarliestDate() {
        val dates = DeezerDates.index(listOf("Album (Deluxe)" to "2026-05-01", "Album" to "2026-03-01", "Album (Acoustic)" to "2026-08-01"))
        assertEquals("2026-03-01", DeezerDates.dateFor(dates, "Album - EP"))
    }

    @Test
    fun anExactTitleBeatsALooseOne() {
        val dates = DeezerDates.index(listOf("Song" to "2026-01-01", "Song (Live)" to "2026-06-01"))
        assertEquals("2026-06-01", DeezerDates.dateFor(dates, "Song (Live)"))
    }

    @Test
    fun noAnnouncementWithoutStarredNewReleases() {
        assertNull(ReleaseRadarPlanner.announcement(emptyList(), setOf("a1")))
        assertNull(ReleaseRadarPlanner.announcement(listOf(tracked("1")), emptySet()))
        assertNull(ReleaseRadarPlanner.announcement(listOf(tracked("1", announced = true)), setOf("a1")))
        assertNull(ReleaseRadarPlanner.announcement(listOf(tracked("1", baseline = true)), setOf("a1")))
    }

    @Test
    fun announcementNamesUpToTwoArtistsThenCountsTheRest() {
        val one = ReleaseRadarPlanner.announcement(listOf(tracked("1", title = "Espresso")), setOf("a1"))!!
        assertEquals("New from Artist", one.title)
        assertEquals("Espresso (single)", one.text)

        val many = (1..5).map { tracked("$it", artistId = "x$it", name = "Artist$it") }
        val title = ReleaseRadarPlanner.announcement(many, many.map { it.artistId }.toSet())!!.title
        assertEquals("New from Artist1, Artist2 and 3 more", title)
        val two = ReleaseRadarPlanner.announcement(many.take(2), many.take(2).map { it.artistId }.toSet())!!.title
        assertEquals("New from Artist1 and Artist2", two)
    }

    @Test
    fun announcementTextShowsAtMostFourReleasesAndSkipsBlankArtistNames() {
        val releases = (1..6).map { tracked("$it", title = "T$it", kind = Kind.EP) }
        assertEquals("T1 (ep) · T2 (ep) · T3 (ep) · T4 (ep)", ReleaseRadarPlanner.announcement(releases, setOf("a1"))!!.text)
        assertNull(ReleaseRadarPlanner.announcement(listOf(tracked("1", name = "  ")), setOf("a1")))
    }

    @Test
    fun anArtistCountedOnceNoMatterHowManyReleases() {
        val releases = listOf(tracked("1"), tracked("2"))
        assertEquals("New from Artist", ReleaseRadarPlanner.announcement(releases, setOf("a1"))!!.title)
    }

    private fun radar(kind: Kind, vararg tracks: Pair<String, Long>) =
        RadarRelease(tracked("r", kind = kind), tracks.map { RadarTrack(it.first, it.second) })

    @Test
    fun singlesAndEpsContributeEverySong() {
        val songs = ReleaseRadarPlanner.playlistSongs(listOf(radar(Kind.EP, "a" to 1, "b" to 2, "c" to 3, "d" to 4)))
        assertEquals(listOf("a", "b", "c", "d"), songs)
    }

    @Test
    fun anAlbumContributesItsThreeMostPlayedSongs() {
        val songs = ReleaseRadarPlanner.playlistSongs(listOf(radar(Kind.ALBUM, "a" to 5, "b" to 50, "c" to 10, "d" to 40)))
        assertEquals(listOf("b", "d", "c"), songs)
    }

    @Test
    fun theSameSongIsNeverListedTwiceAndTheCapIsRespected() {
        val a = radar(Kind.SINGLE, "x" to 0, "y" to 0)
        val b = radar(Kind.SINGLE, "y" to 0, "z" to 0)
        assertEquals(listOf("x", "y", "z"), ReleaseRadarPlanner.playlistSongs(listOf(a, b)))
        val many = radar(Kind.EP, *(1..60).map { "s$it" to 0L }.toTypedArray())
        assertEquals(40, ReleaseRadarPlanner.playlistSongs(listOf(many)).size)
        assertEquals(2, ReleaseRadarPlanner.playlistSongs(listOf(many), limit = 2).size)
    }

    @Test
    fun noReleasesGiveAnEmptyPlaylist() {
        assertEquals(emptyList(), ReleaseRadarPlanner.playlistSongs(emptyList()))
        assertEquals(emptyList(), ReleaseRadarPlanner.playlistSongs(listOf(radar(Kind.EP))))
        assertEquals(emptyList(), ReleaseRadarPlanner.playlistSongs(listOf(radar(Kind.EP, "a" to 1)), limit = 0))
    }

    @Test
    fun theWeekStartsOnTheFridayOnOrBefore() {
        val friday = epochDayOf("2026-10-16")!!
        assertEquals(friday, ReleaseRadarPlanner.weekStart(friday))
        assertEquals(friday, ReleaseRadarPlanner.weekStart(epochDayOf("2026-10-18")!!))
        assertEquals(friday, ReleaseRadarPlanner.weekStart(epochDayOf("2026-10-22")!!))
        assertEquals(friday - 7, ReleaseRadarPlanner.weekStart(epochDayOf("2026-10-15")!!))
    }

    @Test
    fun theWeekStartIsAlwaysAFridayWithinSixDaysEvenBeforeTheEpoch() {
        for (day in -20L..20L) {
            val start = ReleaseRadarPlanner.weekStart(day)
            assertTrue(day - start in 0..6, "day=$day start=$start")
            assertEquals(4L, (start + 3).mod(7L), "day=$day start=$start is not a Friday")
        }
    }
}
