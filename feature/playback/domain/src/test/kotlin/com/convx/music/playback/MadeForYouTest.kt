package com.convx.music.playback

import com.convx.music.playback.MadeForYou.Play
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MadeForYouTest {
    private val now = 1_800_000_000_000L
    private val minute = 60_000L
    private val day = 86_400_000L

    private fun listen(vararg sittings: List<String>, daysAgo: Int = 1): List<Play> {
        val out = ArrayList<Play>()
        var t = now - daysAgo * day
        for (sitting in sittings) {
            for (artist in sitting) repeat(3) { out += Play("$artist-$it", artist, t); t += 4 * minute }
            t += 5 * 60 * minute
        }
        return out
    }

    private fun adjacentRepeats(mix: List<Pair<String, String>>) =
        mix.zipWithNext().count { (a, b) -> a.second.equals(b.second, ignoreCase = true) }

    @Test
    fun artistsThatGoTogetherShareAMix() {
        val rap = listOf("Kendrick Lamar", "SZA", "J. Cole")
        val rock = listOf("Nirvana", "Foo Fighters", "Pearl Jam")
        val genres = (rap.map { it to "Hip-hop" } + rock.map { it to "Rock" }).toMap()
        val clusters = MadeForYou.clusters(listen(rap, rock, rap, rock, rap, rock), genres, now)
        assertEquals(2, clusters.size)
        assertEquals(rap.toSet(), clusters.first { "SZA" in it.artists }.artists.toSet())
        assertEquals(rock.toSet(), clusters.first { "Nirvana" in it.artists }.artists.toSet())
    }

    @Test
    fun playedTogetherCountsWithoutGenres() {
        val a = listOf("A1", "A2", "A3")
        val b = listOf("B1", "B2", "B3")
        val clusters = MadeForYou.clusters(listen(a, b, a, b, a, b, a, b), emptyMap(), now)
        assertTrue(clusters.size >= 2)
        assertTrue(clusters.none { c -> c.artists.any { it.startsWith("A") } && c.artists.any { it.startsWith("B") } })
    }

    @Test
    fun artistsTwoVisitsApartInASittingStillShareAMixWithoutGenres() {
        val trio = listOf("T1", "T2", "T3")
        val other = listOf("U1", "U2", "U3")
        val clusters = MadeForYou.clusters(listen(trio, other, trio, other, trio, other), emptyMap(), now)
        assertTrue(clusters.any { it.artists.toSet() == trio.toSet() }, "clusters: $clusters")
        assertTrue(clusters.any { it.artists.toSet() == other.toSet() }, "clusters: $clusters")
    }

    @Test
    fun artistsInDifferentSittingsAreNotPaired() {
        val clusters = MadeForYou.clusters(listen(listOf("A1", "A2"), listOf("B1", "B2"), listOf("A1", "A2"), listOf("B1", "B2")), emptyMap(), now)
        assertTrue(clusters.none { c -> c.artists.any { it.startsWith("A") } && c.artists.any { it.startsWith("B") } })
    }

    @Test
    fun aRarelyPlayedArtistDoesNotGetAMix() {
        val sitting = listOf("Big", "Big2")
        val plays = listen(sitting, sitting, sitting, sitting) + Play("x", "Once", now - day)
        val clusters = MadeForYou.clusters(plays, mapOf("Big" to "Pop", "Big2" to "Pop", "Once" to "Jazz"), now)
        assertTrue(clusters.none { "Once" in it.artists })
    }

    @Test
    fun noPlaysOrNonPositiveLimitsGiveNoClusters() {
        assertEquals(emptyList(), MadeForYou.clusters(emptyList(), emptyMap(), now))
        val plays = listen(listOf("A", "B"))
        assertEquals(emptyList(), MadeForYou.clusters(plays, emptyMap(), now, max = 0))
        assertEquals(emptyList(), MadeForYou.clusters(plays, emptyMap(), now, top = 0))
    }

    @Test
    fun blankArtistsAndBadWeightsAreIgnored() {
        val plays = listOf(
            Play("1", "  ", now - day),
            Play("2", "A", now - day, weight = Double.NaN),
            Play("3", "A", now - day, weight = -1.0),
            Play("4", "A", now - day, weight = Double.POSITIVE_INFINITY),
        )
        assertEquals(emptyMap(), MadeForYou.artistWeights(plays, now))
        assertEquals(emptyList(), MadeForYou.clusters(plays, emptyMap(), now))
    }

    @Test
    fun aPlayDatedInTheFutureWeighsAsToday() {
        val future = MadeForYou.artistWeights(listOf(Play("1", "A", now + 400 * day)), now)
        val today = MadeForYou.artistWeights(listOf(Play("1", "A", now)), now)
        assertEquals(today.getValue("A"), future.getValue("A"), 1e-12)
        assertEquals(1.0, future.getValue("A"), 1e-12)
    }

    @Test
    fun recentPlaysOutweighOldOnes() {
        val plays = (0 until 10).map { Play("o$it", "Old", now - 400 * day) } + Play("n", "New", now - day)
        val weights = MadeForYou.artistWeights(plays, now)
        assertTrue(weights.getValue("New") > weights.getValue("Old"))
    }

    @Test
    fun artistSpellingsThatDifferOnlyInCaseOrSpacesAreOneArtist() {
        val plays = listOf(Play("1", "SZA", now), Play("2", "sza", now), Play("3", " Sza ", now))
        assertEquals(mapOf("SZA" to 3.0), MadeForYou.artistWeights(plays, now))
    }

    @Test
    fun genresMatchIgnoringCaseAndSpaces() {
        val a = listOf("A1", "A2")
        val b = listOf("B1", "B2")
        val genres = mapOf("a1" to " Rock ", "A2" to "rock", "B1" to "Jazz", "b2" to "JAZZ")
        val clusters = MadeForYou.clusters(listen(a, b, a, b, a, b), genres, now)
        assertEquals(2, clusters.size)
        assertEquals(a.toSet(), clusters.first { "A1" in it.artists }.artists.toSet())
    }

    @Test
    fun clustersAreHeaviestFirstAndSharesSumToAtMostOne() {
        val plays = listen(listOf("A1", "A2"), listOf("B1", "B2"), listOf("A1", "A2"), listOf("B1", "B2"), listOf("A1", "A2"))
        val genres = mapOf("A1" to "x", "A2" to "x", "B1" to "y", "B2" to "y")
        val clusters = MadeForYou.clusters(plays, genres, now)
        assertEquals(clusters.sortedByDescending { it.weight }, clusters)
        assertTrue(clusters.sumOf { it.weight } <= 1.0 + 1e-9)
    }

    @Test
    fun interleaveMixesTwoNewInFiveAndSplitsArtists() {
        val known = (1..30).map { "k$it" to "artist${it % 3}" }
        val fresh = (1..20).map { "f$it" to "new$it" }
        val mix = MadeForYou.interleave(known, fresh, 50) { it.second }
        assertEquals(50, mix.size)
        assertEquals(20, mix.count { it.first.startsWith("f") })
        assertEquals(0, adjacentRepeats(mix))
        assertEquals(20, MadeForYou.interleave(known, fresh.take(2), 20) { it.second }.size)
    }

    @Test
    fun interleaveNeverCreatesARepeatWhileFixingOne() {
        val known = listOf("1" to "a", "2" to "a", "3" to "b", "4" to "a", "5" to "c", "6" to "b")
        val mix = MadeForYou.interleave(known, emptyList(), 6) { it.second }
        assertEquals(known.map { it.first }.toSet(), mix.map { it.first }.toSet())
        assertEquals(0, adjacentRepeats(mix))
    }

    @Test
    fun interleaveTreatsArtistSpellingsCaseInsensitively() {
        val mix = MadeForYou.interleave(listOf("1" to "SZA", "2" to "sza", "3" to "Other"), emptyList(), 3) { it.second }
        assertEquals(0, adjacentRepeats(mix))
    }

    @Test
    fun interleaveHandlesEmptyAndNonPositiveSizes() {
        assertEquals(emptyList(), MadeForYou.interleave(listOf("a" to "x"), emptyList(), 0) { it.second })
        assertEquals(emptyList(), MadeForYou.interleave(listOf("a" to "x"), emptyList(), -3) { it.second })
        assertEquals(emptyList(), MadeForYou.interleave(emptyList<Pair<String, String>>(), emptyList(), 5) { it.second })
    }

    @Test
    fun interleaveKeepsAnUnavoidableRepeatInsteadOfLosingSongs() {
        val mix = MadeForYou.interleave(listOf("1" to "a", "2" to "a", "3" to "a"), emptyList(), 3) { it.second }
        assertEquals(3, mix.size)
    }

    @Test
    fun timeOfDay() {
        assertEquals(MadeForYou.TimeBucket.LATE, MadeForYou.TimeBucket.of(2))
        assertEquals(MadeForYou.TimeBucket.MORNING, MadeForYou.TimeBucket.of(7))
        assertEquals(MadeForYou.TimeBucket.NIGHT, MadeForYou.TimeBucket.of(23))
        (0..23).forEach { MadeForYou.TimeBucket.of(it) }
        assertEquals("Your Tuesday late-night hip-hop", MadeForYou.timeTitle("Tuesday", MadeForYou.TimeBucket.LATE, "Hip-hop"))
        assertEquals("Your Sunday morning mix", MadeForYou.timeTitle("Sunday", MadeForYou.TimeBucket.MORNING, null))
        assertEquals("Your Sunday morning mix", MadeForYou.timeTitle("Sunday", MadeForYou.TimeBucket.MORNING, "  "))
    }

    @Test
    fun anOutOfRangeHourWrapsInsteadOfThrowing() {
        assertEquals(MadeForYou.TimeBucket.LATE, MadeForYou.TimeBucket.of(24))
        assertEquals(MadeForYou.TimeBucket.NIGHT, MadeForYou.TimeBucket.of(-1))
        assertEquals(MadeForYou.TimeBucket.MORNING, MadeForYou.TimeBucket.of(29))
    }

    @Test
    fun namesLineListsUpToThreeThenMore() {
        assertEquals("", MadeForYou.namesLine(emptyList()))
        assertEquals("A", MadeForYou.namesLine(listOf("A")))
        assertEquals("A and B", MadeForYou.namesLine(listOf("A", "B")))
        assertEquals("A, B and C", MadeForYou.namesLine(listOf("A", "B", "C")))
        assertEquals("A, B, C and more", MadeForYou.namesLine(listOf("A", "B", "C", "D")))
    }

    @Test
    fun namesLineSkipsBlankAndRepeatedNames() {
        assertEquals("A and B", MadeForYou.namesLine(listOf("A", " ", "a", "B", "")))
    }

    @Test
    fun aMixIsNamedByItsGenreOnlyWhenMostOfItIsThatGenre() {
        val friday = List(15) { "Rock" } + List(10) { "Indie & alternative" } + List(3) { "Hip-hop" } + listOf(null, null)
        assertEquals("Rock", MadeForYou.mainGenre(friday))
        assertNull(MadeForYou.mainGenre(List(10) { "Rock" } + List(10) { "Indie & alternative" } + List(5) { "Pop" }))
        assertNull(MadeForYou.mainGenre(List(4) { "Rock" } + List(10) { null }))
        assertNull(MadeForYou.mainGenre(emptyList()))
    }

    @Test
    fun genreCountingIgnoresCaseAndBlanksCountAsUnknown() {
        assertEquals("Rock", MadeForYou.mainGenre(listOf("Rock", "rock", " ROCK ", "Jazz")))
        assertNull(MadeForYou.mainGenre(listOf("Rock", " ", " ", " ", " ")))
    }
}
