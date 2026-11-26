package com.convx.music.playback.data

import com.convx.music.playback.ReleaseRadarRules.Kind
import com.convx.music.playback.ReleaseRadarSnapshot
import com.convx.music.playback.TrackedRelease
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.ArtistItem
import com.music.innertube.pages.ArtistPage
import com.music.innertube.pages.ArtistSection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReleaseRadarSharedSourcesTest {
    private val directory = createTempDirectory("release-radar").toFile()

    @AfterTest
    fun cleanUp() {
        directory.deleteRecursively()
    }

    private fun release(id: String) = TrackedRelease(id, "a1", "Artist", "T$id", Kind.SINGLE, 2026, null, 0L, baseline = false, announced = false)

    @Test
    fun aMissingFileReadsAsAnEmptySnapshot() = runTest {
        assertEquals(ReleaseRadarSnapshot(), JsonFileReleaseStore(File(directory, "none.json")).read())
    }

    @Test
    fun aSavedSnapshotSurvivesANewStoreOnTheSameFile() = runTest {
        val file = File(directory, "radar.json")
        val snapshot = ReleaseRadarSnapshot().withReleases(listOf(release("r1"))).withChecked("a1")
        JsonFileReleaseStore(file).save(snapshot)
        assertEquals(snapshot, JsonFileReleaseStore(file).read())
    }

    @Test
    fun savingCreatesMissingParentFoldersAndLeavesNoTemporaryFile() = runTest {
        val file = File(directory, "nested/deeper/radar.json")
        JsonFileReleaseStore(file).save(ReleaseRadarSnapshot(lastRunEpochMs = 5))
        assertTrue(file.isFile)
        assertFalse(File(file.parentFile, "radar.json.tmp").exists())
    }

    @Test
    fun aCorruptFileReadsAsEmptyInsteadOfFailing() = runTest {
        val file = File(directory, "radar.json").apply { writeText("{not json") }
        assertEquals(ReleaseRadarSnapshot(), JsonFileReleaseStore(file).read())
    }

    @Test
    fun savingOverACorruptFileReplacesIt() = runTest {
        val file = File(directory, "radar.json").apply { writeText("garbage") }
        val store = JsonFileReleaseStore(file)
        store.save(ReleaseRadarSnapshot(lastRunEpochMs = 9))
        assertEquals(9L, JsonFileReleaseStore(file).read().lastRunEpochMs)
    }

    @Test
    fun observeStartsWithTheStoredSnapshotAndSeesLaterSaves() = runTest {
        val store = JsonFileReleaseStore(File(directory, "radar.json"))
        store.save(ReleaseRadarSnapshot(lastRunEpochMs = 1))
        assertEquals(1L, store.observe().first().lastRunEpochMs)
        store.save(ReleaseRadarSnapshot(lastRunEpochMs = 2))
        assertEquals(2L, store.observe().first().lastRunEpochMs)
    }

    private fun album(id: String, title: String, year: Int?, credited: List<Artist>?) =
        AlbumItem(browseId = id, playlistId = "OLAK$id", title = title, artists = credited, year = year, thumbnail = "t$id")

    private fun page(vararg sections: ArtistSection) = ArtistPage(
        artist = ArtistItem("a1", "Artist", null, null, null, null, null),
        sections = sections.toList(), description = null, subscriberCountText = null,
    )

    @Test
    fun albumSinglesAndEpSectionsBecomeReleasesWithTheirKinds() {
        val seen = InnertubeReleaseArtistSource.seenReleases(
            page(
                ArtistSection("Albums", listOf(album("b1", "First", 2024, null)), null),
                ArtistSection("Singles & EPs", listOf(album("b2", "Hit", 2026, null)), null),
            ),
            "a1",
        )
        assertEquals(listOf(Kind.ALBUM, Kind.SINGLE), seen.map { it.kind })
        assertEquals(listOf(2024, 2026), seen.map { it.year })
        assertEquals("tb2", seen[1].thumbnailUrl)
    }

    @Test
    fun albumsCreditedOnlyToOtherArtistsAreLeftOut() {
        val seen = InnertubeReleaseArtistSource.seenReleases(
            page(
                ArtistSection(
                    "Albums",
                    listOf(
                        album("own", "Own", 2025, listOf(Artist("Artist", "a1"))),
                        album("other", "Other", 2025, listOf(Artist("Someone", "zz"))),
                        album("uncredited", "Uncredited", 2025, emptyList()),
                    ),
                    null,
                ),
            ),
            "a1",
        )
        assertEquals(listOf("own", "uncredited"), seen.map { it.id })
    }

    @Test
    fun sectionsThatAreNotOnlyAlbumsAndRepeatedAlbumsAreIgnored() {
        val seen = InnertubeReleaseArtistSource.seenReleases(
            page(
                ArtistSection("Albums", listOf(album("b1", "First", 2024, null), album("b1", "First again", 2024, null)), null),
                ArtistSection("Fans might also like", emptyList(), null),
            ),
            "a1",
        )
        assertEquals(listOf("b1"), seen.map { it.id })
    }

    @Test
    fun aMissingYearIsZero() {
        val seen = InnertubeReleaseArtistSource.seenReleases(page(ArtistSection("Albums", listOf(album("b1", "X", null, null)), null)), "a1")
        assertEquals(0, seen.single().year)
    }
}
