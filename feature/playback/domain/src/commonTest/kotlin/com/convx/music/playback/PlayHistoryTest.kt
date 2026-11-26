package com.convx.music.playback

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayHistoryTest {
    private var clock = 0L

    /** Plays for [seconds] with a snapshot every second, positions running from [startSec]. */
    private fun listen(recorder: PlayRecorder, id: String?, startSec: Long, seconds: Int, duration: Long? = 200_000, playing: Boolean = true): Int {
        var counted = 0
        for (i in 0..seconds) {
            if (recorder.onSnapshot(id, playing, (startSec + i) * 1_000, duration, clock)) counted++
            clock += 1_000
        }
        return counted
    }

    @Test
    fun aSongCountsOnceAfterThirtySecondsOfListening() {
        val recorder = PlayRecorder()
        assertEquals(0, listen(recorder, "a", 0, 29))
        assertEquals(1, listen(recorder, "a", 30, 90))
    }

    @Test
    fun aShortSongCountsAtHalfItsLength() {
        assertEquals(1, listen(PlayRecorder(), "short", 0, 20, duration = 24_000))
    }

    @Test
    fun anUnknownLengthUsesTheFullThreshold() {
        val recorder = PlayRecorder()
        assertEquals(0, listen(recorder, "x", 0, 20, duration = null))
        assertEquals(1, listen(recorder, "x", 21, 20, duration = null))
    }

    @Test
    fun pausedTimeDoesNotCount() {
        assertEquals(0, listen(PlayRecorder(), "a", 0, 100, playing = false))
    }

    @Test
    fun timeBetweenSparseSnapshotsCountsWhileItWasPlaying() {
        val recorder = PlayRecorder()
        assertFalse(recorder.onSnapshot("a", true, 0, 200_000, 0))
        assertTrue(recorder.onSnapshot("a", false, 60_000, 200_000, 60_000))
    }

    @Test
    fun aMachineThatSleptForHoursOnlyCountsUpToTheCap() {
        val recorder = PlayRecorder(thresholdMs = 700_000)
        recorder.onSnapshot("b", true, 0, 2_000_000, 0)
        assertFalse(recorder.onSnapshot("b", true, 5_000, 2_000_000, 8 * 3_600_000L))
    }

    @Test
    fun changingSongStartsAgain() {
        val recorder = PlayRecorder()
        assertEquals(0, listen(recorder, "a", 0, 20))
        assertEquals(0, listen(recorder, "b", 0, 20))
        assertEquals(1, listen(recorder, "b", 21, 20))
    }

    @Test
    fun jumpingBackToTheStartAfterCountingCountsAgain() {
        val recorder = PlayRecorder()
        assertEquals(1, listen(recorder, "a", 0, 60))
        assertEquals(1, listen(recorder, "a", 0, 60))
    }

    @Test
    fun seekingBackALittleDoesNotCountAgain() {
        val recorder = PlayRecorder()
        assertEquals(1, listen(recorder, "a", 0, 60))
        assertEquals(0, listen(recorder, "a", 50, 30))
    }

    @Test
    fun noTrackNeverCounts() {
        assertEquals(0, listen(PlayRecorder(), null, 0, 100))
    }

    @Test
    fun aSnapshotRoundTripsAndKeepsOnlyTheNewestPlays() {
        fun play(i: Int) = HistoryPlay("s$i", "p", "T$i", listOf("A"), null, null, i.toLong())
        val full = (0 until PlayHistorySnapshot.MAX_PLAYS + 50).fold(PlayHistorySnapshot()) { s, i -> s.with(play(i)) }
        assertEquals(PlayHistorySnapshot.MAX_PLAYS, full.plays.size)
        assertEquals("s50", full.plays.first().songId)
        assertEquals(full, PlayHistorySnapshot.parse(full.toJson()))
    }

    @Test
    fun corruptOrEmptyTextGivesAnEmptyHistory() {
        assertTrue(PlayHistorySnapshot.parse(null).plays.isEmpty())
        assertTrue(PlayHistorySnapshot.parse("{nope").plays.isEmpty())
        assertTrue(PlayHistorySnapshot.parse("[1]").plays.isEmpty())
    }

    @Test
    fun dailyMixesComeFromHistoryWithTheirArtistsAndNoRepeatedSongs() {
        val now = 1_800_000_000_000L
        val sittings = listOf(listOf("R1", "R2", "R3"), listOf("K1", "K2", "K3"))
        val history = ArrayList<HistoryPlay>()
        var t = now - 86_400_000L
        repeat(3) {
            for (sitting in sittings) {
                for (artist in sitting) for (song in 0 until 3) {
                    history += HistoryPlay("$artist-$song", "youtube-music", "$artist song $song", listOf(artist), null, null, t)
                    t += 4 * 60_000L
                }
                t += 5 * 60 * 60_000L
            }
        }
        val mixes = DailyMixes.build(history, now)
        assertEquals(listOf("Daily Mix 1", "Daily Mix 2"), mixes.map { it.title })
        val songs = mixes.flatMap { mix -> mix.plays.map { it.songId } }
        assertEquals(songs.size, songs.toSet().size)
        assertTrue(mixes.all { it.subtitle.isNotBlank() && it.plays.isNotEmpty() })
    }

    @Test
    fun noHistoryGivesNoMixes() {
        assertTrue(DailyMixes.build(emptyList(), 0L).isEmpty())
    }
}
