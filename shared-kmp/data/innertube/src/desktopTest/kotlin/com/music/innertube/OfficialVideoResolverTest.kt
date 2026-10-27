package com.music.innertube

import com.music.innertube.models.Artist
import com.music.innertube.models.SongItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class OfficialVideoResolverTest {
    private val video = SongItem("abcdefghijk", "Calling (Official Music Video)", listOf(Artist("mamomo", null)), duration = 352, musicVideoType = "MUSIC_VIDEO_TYPE_OMV", thumbnail = "")

    @Test
    fun rejectsCoverArtAndOtherArtistsOrUnrelatedLengths() {
        assertFalse(OfficialVideoResolver.matchesOfficialVideo(video.copy(musicVideoType = "MUSIC_VIDEO_TYPE_ATV"), "Calling", listOf("mamomo"), 352))
        assertFalse(OfficialVideoResolver.matchesOfficialVideo(video.copy(artists = listOf(Artist("Another artist", null))), "Calling", listOf("mamomo"), 352))
        assertFalse(OfficialVideoResolver.matchesOfficialVideo(video.copy(duration = 30), "Calling", listOf("mamomo"), 352))
        assertTrue(OfficialVideoResolver.matchesOfficialVideo(video, "Calling", listOf("mamomo"), 352))
        assertTrue(OfficialVideoResolver.matchesOfficialVideo(video, "Calling (feat. marok)", listOf("mamomo", "marok"), 352))
    }

    @Test
    fun currentVideoDoesNotSearchAndLocalTracksCannotGainAYoutubeVideo() = runBlocking {
        val resolver = OfficialVideoResolver(search = { error("No search expected") })
        assertEquals(OfficialVideo(video.id, video.id), resolver.resolve(video.id, video.title, listOf("mamomo"), 352, video.musicVideoType).getOrThrow())
        assertNull(resolver.resolve("content://music/1", "Calling", listOf("mamomo"), 352, null).getOrThrow())
        assertNull(resolver.resolve("spotify:track:test", "Calling", listOf("mamomo"), 352, null).getOrThrow())
    }

    @Test
    fun transientFailuresAreNotCachedAndConfirmedAbsenceExpires() = runBlocking {
        var attempts = 0
        var now = 0L
        val resolver = OfficialVideoResolver(search = {
            attempts++
            if (attempts == 1) Result.failure(IllegalStateException("offline")) else Result.success(emptyList())
        }, nowMillis = { now })
        assertTrue(resolver.resolve("12345678901", "Calling", listOf("mamomo"), 352, null).isFailure)
        assertNull(resolver.resolve("12345678901", "Calling", listOf("mamomo"), 352, null).getOrThrow())
        resolver.resolve("12345678901", "Calling", listOf("mamomo"), 352, null)
        assertEquals(2, attempts)
        now = 300_001
        resolver.resolve("12345678901", "Calling", listOf("mamomo"), 352, null)
        assertEquals(3, attempts)
    }
}
