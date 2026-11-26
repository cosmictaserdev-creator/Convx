package com.convx.music.playback

import com.convx.music.playback.MadeForYou.Play
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DailyMixPlannerTest {
    private val now = 1_800_000_000_000L
    private val minute = 60_000L
    private val day = 86_400_000L

    /** Sittings of back-to-back songs: each artist plays [songsPerArtist] distinct songs three times. */
    private fun listen(vararg sittings: List<String>, songsPerArtist: Int = 3, daysAgo: Int = 1): List<Play> {
        val out = ArrayList<Play>()
        var t = now - daysAgo * day
        for (sitting in sittings) {
            for (artist in sitting) for (song in 0 until songsPerArtist) {
                out += Play("$artist-$song", artist, t)
                t += 4 * minute
            }
            t += 5 * 60 * minute
        }
        return out
    }

    private val rap = listOf("Rap1", "Rap2", "Rap3")
    private val rock = listOf("Rock1", "Rock2", "Rock3")
    private val history = listen(rap, rock, rap, rock, rap, rock)

    private fun plan(plays: List<Play> = history, minSongs: Int = 1, mixSize: Int = 25) =
        DailyMixPlanner.plan(plays, now, minSongs = minSongs, mixSize = mixSize)

    @Test
    fun noHistoryGivesNoMixes() {
        assertEquals(emptyList(), plan(emptyList()))
    }

    @Test
    fun artistsPlayedTogetherShareAMix() {
        val mixes = plan()
        assertEquals(2, mixes.size)
        val groups = mixes.map { it.artists.toSet() }
        assertTrue(rap.toSet() in groups)
        assertTrue(rock.toSet() in groups)
    }

    @Test
    fun aMixHoldsOnlySongsOfItsArtists() {
        for (mix in plan()) {
            val prefixes = mix.artists.toSet()
            assertTrue(mix.songIds.all { id -> prefixes.any { id.startsWith("$it-") } }, "mix ${mix.artists}")
        }
    }

    @Test
    fun noSongIsInTwoMixesOrTwiceInOne() {
        val all = plan().flatMap { it.songIds }
        assertEquals(all.size, all.toSet().size)
    }

    @Test
    fun aMixIsCappedAndNoArtistPlaysTwiceInARowWhenAvoidable() {
        val mix = plan(mixSize = 6).first { "Rap1" in it.artists }
        assertEquals(6, mix.songIds.size)
        val artists = mix.songIds.map { it.substringBefore('-') }
        assertTrue(artists.zipWithNext().none { (a, b) -> a == b }, "order: $artists")
    }

    @Test
    fun mixesUnderTheMinimumAreDropped() {
        assertEquals(emptyList(), plan(minSongs = 50))
        assertEquals(2, plan(minSongs = 9).size)
    }

    @Test
    fun theMostPlayedLatelySongsComeFirstIntoTheCut() {
        val plays = history + List(40) { Play("Rap1-fav", "Rap1", now - minute) }
        val mix = plan(plays, mixSize = 3).first { "Rap1" in it.artists }
        assertTrue("Rap1-fav" in mix.songIds)
    }

    @Test
    fun aSongPlayedUnderTwoArtistsLandsInOneMixOnly() {
        val plays = history + Play("shared", "Rap1", now - 2 * minute) + Play("shared", "Rock1", now - minute)
        assertEquals(1, plan(plays).count { "shared" in it.songIds })
    }

    @Test
    fun blankArtistsAndSongIdsAreIgnored() {
        val noisy = history + Play("", "Rap1", now) + Play("x", " ", now)
        assertEquals(plan(history), plan(noisy))
    }

    @Test
    fun nonPositiveSizeGivesNoMixes() {
        assertEquals(emptyList(), plan(mixSize = 0))
    }

    @Test
    fun theSameHistoryGivesTheSameMixes() {
        assertEquals(plan(), plan())
    }
}
