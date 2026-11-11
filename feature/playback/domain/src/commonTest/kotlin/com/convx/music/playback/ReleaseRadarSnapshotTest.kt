package com.convx.music.playback

import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.ReleaseRadarRules.epochDayOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ReleaseRadarSnapshotTest {
    private val day = 86_400_000L
    private val today = epochDayOf("2026-10-18")!!
    private val nowMs = today * day

    private fun release(
        id: String,
        artist: String = "a1",
        date: String? = null,
        seenDaysAgo: Long = 0,
        baseline: Boolean = false,
        announced: Boolean = false,
    ) = TrackedRelease(id, artist, "Artist", "T$id", Kind.SINGLE, 2026, date?.let(::epochDayOf), nowMs - seenDaysAgo * day, baseline, announced)

    @Test
    fun emptyCorruptAndForeignTextGivesAnEmptySnapshot() {
        val empty = ReleaseRadarSnapshot()
        assertEquals(empty, ReleaseRadarSnapshot.parse(null))
        assertEquals(empty, ReleaseRadarSnapshot.parse(""))
        assertEquals(empty, ReleaseRadarSnapshot.parse("   "))
        assertEquals(empty, ReleaseRadarSnapshot.parse("{not json"))
        assertEquals(empty, ReleaseRadarSnapshot.parse("[1,2,3]"))
        assertEquals(empty, ReleaseRadarSnapshot.parse("""{"releases":"oops"}"""))
    }

    @Test
    fun aSnapshotSurvivesAJsonRoundTrip() {
        val snapshot = ReleaseRadarSnapshot(setOf("a1", "a2"), 123L, 20_000L, listOf(release("r1", date = "2026-10-10")))
        assertEquals(snapshot, ReleaseRadarSnapshot.parse(snapshot.toJson()))
    }

    @Test
    fun unknownKeysFromANewerVersionAreIgnored() {
        val parsed = ReleaseRadarSnapshot.parse("""{"lastRunEpochMs":5,"somethingNew":true}""")
        assertEquals(5L, parsed.lastRunEpochMs)
    }

    @Test
    fun releasesAlreadyHeldKeepTheirFirstSeenState() {
        val first = ReleaseRadarSnapshot().withReleases(listOf(release("r1", baseline = true, announced = true)))
        val again = first.withReleases(listOf(release("r1", baseline = false, announced = false)))
        assertSame(first, again)
        assertTrue(again.releases.single().baseline)
    }

    @Test
    fun duplicatesWithinOneBatchAreHeldOnce() {
        assertEquals(1, ReleaseRadarSnapshot().withReleases(listOf(release("r1"), release("r1"))).releases.size)
    }

    @Test
    fun knownIdsAreTrackedPerArtist() {
        val snapshot = ReleaseRadarSnapshot().withReleases(listOf(release("r1", artist = "a1"), release("r2", artist = "a2")))
        assertEquals(setOf("r1"), snapshot.knownIds("a1"))
        assertEquals(emptySet(), snapshot.knownIds("zzz"))
    }

    @Test
    fun anArtistIsOnFirstLookUntilChecked() {
        val snapshot = ReleaseRadarSnapshot()
        assertTrue(snapshot.isFirstLook("a1"))
        assertFalse(snapshot.withChecked("a1").isFirstLook("a1"))
        assertTrue(snapshot.withChecked("a1").isFirstLook("a2"))
    }

    @Test
    fun announcingMarksOnlyTheGivenIds() {
        val snapshot = ReleaseRadarSnapshot().withReleases(listOf(release("r1"), release("r2")))
        val announced = snapshot.withAnnounced(setOf("r1")).releases.associate { it.id to it.announced }
        assertEquals(mapOf("r1" to true, "r2" to false), announced)
        assertSame(snapshot, snapshot.withAnnounced(emptySet()))
    }

    @Test
    fun aLookIsDueOnlyAfterThreeDays() {
        assertTrue(ReleaseRadarSnapshot().isDue(nowMs))
        assertFalse(ReleaseRadarSnapshot().withRun(nowMs - 2 * day).isDue(nowMs))
        assertTrue(ReleaseRadarSnapshot().withRun(nowMs - 4 * day).isDue(nowMs))
    }

    @Test
    fun aClockSetBackDoesNotMakeALookDue() {
        assertFalse(ReleaseRadarSnapshot().withRun(nowMs + 5 * day).isDue(nowMs))
    }

    @Test
    fun recentReleasesAreNewestFirstAndExcludeBackCatalogue() {
        val snapshot = ReleaseRadarSnapshot().withReleases(
            listOf(
                release("old", date = "2026-08-01"),
                release("new", date = "2026-10-16"),
                release("catalogue", date = "2026-10-15", baseline = true),
                release("undated", seenDaysAgo = 2),
            ),
        )
        assertEquals(listOf("new", "undated"), snapshot.recent(today, days = 30).map { it.id })
    }

    @Test
    fun theWindowAppliesToTheReleaseDateElseWhenItWasFirstSeen() {
        val snapshot = ReleaseRadarSnapshot().withReleases(
            listOf(release("dated", date = "2026-01-01", seenDaysAgo = 1), release("seen", seenDaysAgo = 100)),
        )
        assertEquals(emptyList(), snapshot.recent(today, days = 14).map { it.id })
    }

    @Test
    fun storageIsBoundedByDroppingTheOldestFirstSeen() {
        val many = (0 until 3_100).map { release("r$it", seenDaysAgo = it.toLong()) }
        val held = ReleaseRadarSnapshot().withReleases(many).releases
        assertEquals(3_000, held.size)
        assertTrue(held.none { it.id == "r3099" })
        assertTrue(held.any { it.id == "r0" })
    }

    @Test
    fun sectionTitlesGiveTheKind() {
        assertEquals(Kind.ALBUM, ReleaseRadarRules.kindOfSection("Albums"))
        assertEquals(Kind.SINGLE, ReleaseRadarRules.kindOfSection("Singles & EPs"))
        assertEquals(Kind.SINGLE, ReleaseRadarRules.kindOfSection("Singles"))
        assertEquals(Kind.EP, ReleaseRadarRules.kindOfSection("EPs"))
        assertEquals(Kind.ALBUM, ReleaseRadarRules.kindOfSection("Alben"))
        assertEquals(Kind.ALBUM, ReleaseRadarRules.kindOfSection("Deep cuts"))
        assertEquals(Kind.ALBUM, ReleaseRadarRules.kindOfSection(""))
    }

    private val searchJson = """{"data":[
        {"id":1,"name":"SZA","nb_fan":10},
        {"id":2,"name":"sza","nb_fan":5000},
        {"id":3,"name":"SZA Tribute","nb_fan":99999},
        {"id":"4","name":"S.Z.A","nb_fan":"300"}]}"""

    @Test
    fun theArtistWithTheExactNameAndMostFansIsChosen() {
        assertEquals("2", DeezerResponses.artistId(searchJson, "SZA"))
    }

    @Test
    fun artistLookupIsNullWhenNothingMatchesOrTheAnswerIsBroken() {
        assertNull(DeezerResponses.artistId(searchJson, "Nobody"))
        assertNull(DeezerResponses.artistId(searchJson, ""))
        assertNull(DeezerResponses.artistId("""{"error":{"type":"Exception"}}""", "SZA"))
        assertNull(DeezerResponses.artistId("nonsense", "SZA"))
        assertNull(DeezerResponses.artistId("[]", "SZA"))
    }

    @Test
    fun albumsListTitleAndDateAndSkipIncompleteRows() {
        val json = """{"data":[
            {"title":"Good","release_date":"2026-10-16"},
            {"title":"","release_date":"2026-01-01"},
            {"release_date":"2026-01-01"},
            {"title":"NoDate"},
            "junk"]}"""
        assertEquals(listOf("Good" to "2026-10-16"), DeezerResponses.albums(json))
        assertEquals(emptyList(), DeezerResponses.albums("garbage"))
    }
}
