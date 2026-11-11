package com.convx.music.playback

import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.ReleaseRadarRules.epochDayOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReleaseRadarSyncTest {
    private val day = 86_400_000L
    private val now = epochDayOf("2026-10-18")!! * day

    private class Fakes(
        val artists: List<WatchedArtist>,
        val bookmarked: Set<String> = emptySet(),
        var pages: Map<String, List<SeenRelease>?> = emptyMap(),
        var dates: Map<String, String> = emptyMap(),
        var announcerResult: Boolean = true,
    ) {
        var snapshot = ReleaseRadarSnapshot()
        var saves = 0
        var dateCalls = 0
        val announced = mutableListOf<Announcement>()
        var onPage: suspend (String) -> Unit = {}

        val source = object : ReleaseArtistSource {
            override suspend fun watchedArtists(nowEpochMs: Long) = WatchedArtists(artists, bookmarked)
            override suspend fun releases(artistId: String): List<SeenRelease>? {
                onPage(artistId)
                return pages[artistId]
            }
        }
        val dateSource = object : ReleaseDateSource {
            override suspend fun releaseDates(artistName: String): Map<String, String> {
                dateCalls++
                return dates
            }
        }
        val store = object : ReleaseStore {
            override suspend fun read() = snapshot
            override suspend fun save(snapshot: ReleaseRadarSnapshot) {
                this@Fakes.snapshot = snapshot
                saves++
            }
        }
        val announcer = ReleaseAnnouncer { announcement ->
            announced += announcement
            announcerResult
        }

        fun sync() = ReleaseRadarSync(source, dateSource, store, announcer)
    }

    private fun release(id: String, title: String = "T$id", year: Int = 2026) = SeenRelease(id, title, Kind.SINGLE, year)

    private val oneArtist = listOf(WatchedArtist("a1", "Artist"))

    @Test
    fun nothingToWatchDoesNothing() = runTest {
        val fakes = Fakes(emptyList())
        assertEquals(0, fakes.sync().run(now))
        assertEquals(0, fakes.saves)
    }

    @Test
    fun aFirstLookKeepsTheBackCatalogueQuietlyAndMarksTheArtistChecked() = runTest {
        val fakes = Fakes(oneArtist, setOf("a1"), pages = mapOf("a1" to listOf(release("old", year = 2015))))
        assertEquals(0, fakes.sync().run(now))
        assertTrue(fakes.snapshot.releases.single().baseline)
        assertTrue("a1" in fakes.snapshot.checkedArtistIds)
        assertTrue(fakes.announced.isEmpty())
        assertEquals(now, fakes.snapshot.lastRunEpochMs)
    }

    @Test
    fun aLaterLookAnnouncesANewReleaseByASavedArtistOnce() = runTest {
        val fakes = Fakes(oneArtist, setOf("a1"), pages = mapOf("a1" to listOf(release("old", year = 2015))))
        fakes.sync().run(now)
        fakes.pages = mapOf("a1" to listOf(release("old", year = 2015), release("new")))
        assertEquals(1, fakes.sync().run(now + day))
        assertEquals(listOf("New from Artist"), fakes.announced.map { it.title })
        assertEquals(1, fakes.sync().run(now + 2 * day).let { 1 })
        assertEquals(1, fakes.announced.size)
    }

    @Test
    fun aNewReleaseByAnArtistThatIsNotSavedIsShownButNotAnnounced() = runTest {
        val fakes = Fakes(oneArtist, emptySet(), pages = mapOf("a1" to emptyList()))
        fakes.sync().run(now)
        fakes.pages = mapOf("a1" to listOf(release("new")))
        assertEquals(1, fakes.sync().run(now + day))
        assertTrue(fakes.announced.isEmpty())
        assertTrue(fakes.snapshot.releases.single().announced)
    }

    @Test
    fun anAnnouncementThatCannotBeShownStillCountsAsAnnounced() = runTest {
        val fakes = Fakes(oneArtist, setOf("a1"), pages = mapOf("a1" to emptyList()), announcerResult = false)
        fakes.sync().run(now)
        fakes.pages = mapOf("a1" to listOf(release("new")))
        fakes.sync().run(now + day)
        assertTrue(fakes.snapshot.releases.single().announced)
    }

    @Test
    fun aPageThatCannotBeLoadedIsSkippedAndNotMarkedChecked() = runTest {
        val fakes = Fakes(oneArtist, pages = mapOf("a1" to null))
        assertEquals(0, fakes.sync().run(now))
        assertFalse("a1" in fakes.snapshot.checkedArtistIds)
    }

    @Test
    fun deezerIsOnlyAskedWhenARecentReleaseNeedsADate() = runTest {
        val old = Fakes(oneArtist, pages = mapOf("a1" to listOf(release("old", year = 2010))))
        old.sync().run(now)
        assertEquals(0, old.dateCalls)
        val recent = Fakes(oneArtist, pages = mapOf("a1" to listOf(release("new", year = 2026))))
        recent.sync().run(now)
        assertEquals(1, recent.dateCalls)
        val lastYear = Fakes(oneArtist, pages = mapOf("a1" to listOf(release("late", year = 2025))))
        lastYear.sync().run(now)
        assertEquals(1, lastYear.dateCalls)
    }

    @Test
    fun deezerDatesCanMakeAFirstLookReleaseCountAsNews() = runTest {
        val fakes = Fakes(
            oneArtist, pages = mapOf("a1" to listOf(release("fresh", title = "Fresh"))),
            dates = DeezerDates.index(listOf("Fresh" to "2026-10-10")),
        )
        assertEquals(1, fakes.sync().run(now))
    }

    @Test
    fun progressIsSavedEveryTenArtistsAndAtTheEnd() = runTest {
        val artists = (1..25).map { WatchedArtist("a$it", "Artist$it") }
        val fakes = Fakes(artists, pages = artists.associate { it.id to emptyList<SeenRelease>() })
        fakes.sync().run(now)
        assertEquals(3, fakes.saves)
        assertEquals(25, fakes.snapshot.checkedArtistIds.size)
    }

    @Test
    fun pagesAreRequestedWithAGapBetweenThem() = runTest {
        val artists = (1..4).map { WatchedArtist("a$it", "Artist$it") }
        val fakes = Fakes(artists, pages = artists.associate { it.id to emptyList<SeenRelease>() })
        fakes.sync().run(now)
        assertEquals(3_000L, currentTime)
    }

    @Test
    fun cancellingMidwayStopsWithoutRecordingTheRun() = runTest {
        val artists = listOf(WatchedArtist("a1", "A"), WatchedArtist("a2", "B"))
        val fakes = Fakes(artists, pages = mapOf("a1" to emptyList(), "a2" to emptyList()))
        fakes.onPage = { id -> if (id == "a2") awaitCancellation() }
        var cancelled = false
        val job = launch {
            try {
                fakes.sync().run(now)
            } catch (e: CancellationException) {
                cancelled = true
                throw e
            }
        }
        advanceUntilIdle()
        job.cancel()
        advanceUntilIdle()
        assertTrue(cancelled)
        assertEquals(0L, fakes.snapshot.lastRunEpochMs)
    }

    @Test
    fun yearsAreComputedFromEpochDays() {
        assertEquals(1970, ReleaseRadarRules.yearOf(0))
        assertEquals(1969, ReleaseRadarRules.yearOf(-1))
        assertEquals(2000, ReleaseRadarRules.yearOf(epochDayOf("2000-01-01")!!))
        assertEquals(1999, ReleaseRadarRules.yearOf(epochDayOf("1999-12-31")!!))
        assertEquals(2024, ReleaseRadarRules.yearOf(epochDayOf("2024-02-29")!!))
        assertEquals(2026, ReleaseRadarRules.yearOf(epochDayOf("2026-12-31")!!))
        assertEquals(2027, ReleaseRadarRules.yearOf(epochDayOf("2027-01-01")!!))
    }
}
