package com.convx.music.playback

import kotlin.math.exp
import kotlin.math.max

/**
 * Rules behind generated mixes, kept apart from where the listening data comes from: which of a
 * listener's artists go together, how a mix blends known songs with new ones, and how a mix is named.
 */
object MadeForYou {
    const val RADAR = "radar"
    const val TIME = "time"
    const val MAX_DAILY = 6

    fun daily(n: Int) = "daily:$n"

    data class Play(val songId: String, val artist: String, val atEpochMs: Long, val weight: Double = 1.0)

    /** Artists that go together, heaviest first; [weight] is the group's share of all listening. */
    data class Cluster(val artists: List<String>, val weight: Double)

    /**
     * Groups the [top] most played artists into at most [max] clusters by shared genre and by being played
     * one after the other more often than chance. Groups with too small a share of listening are dropped
     * unless a single artist is big enough alone.
     */
    fun clusters(
        plays: List<Play>,
        genres: Map<String, String?>,
        nowEpochMs: Long,
        max: Int = MAX_DAILY,
        top: Int = 40,
    ): List<Cluster> {
        val usable = usablePlays(plays)
        val weights = artistWeights(usable, nowEpochMs)
        val artists = weights.entries.sortedByDescending { it.value }.take(top.coerceAtLeast(0)).map { it.key }
        val total = weights.values.sum()
        if (artists.isEmpty() || max <= 0 || total <= 0.0) return emptyList()

        val indexByKey = artists.withIndex().associate { (index, name) -> key(name) to index }
        val genreByKey = genres.entries.associate { key(it.key) to it.value?.trim()?.lowercase()?.ifEmpty { null } }
        val similarity = similarities(artists, genreByKey, coListening(usable, indexByKey, artists.size))

        return merge(similarity)
            .map { group ->
                val names = group.sortedByDescending { weights.getValue(artists[it]) }.map { artists[it] }
                Cluster(names, group.sumOf { weights.getValue(artists[it]) } / total)
            }
            .filter { it.weight >= if (it.artists.size == 1) SOLO_MIN else GROUP_MIN }
            .sortedByDescending { it.weight }
            .take(max)
    }

    /** What one play counts for: its weight, less the older it is (a month's decay). */
    fun playWeight(play: Play, nowEpochMs: Long): Double =
        play.weight * exp(-max(0L, nowEpochMs - play.atEpochMs) / (DECAY_DAYS * DAY_MS))

    /** Each artist's listening with recent plays counting most; keyed by first-seen spelling. */
    fun artistWeights(plays: List<Play>, nowEpochMs: Long): Map<String, Double> {
        val names = HashMap<String, String>()
        val weights = LinkedHashMap<String, Double>()
        for (play in usablePlays(plays)) {
            val name = names.getOrPut(key(play.artist)) { play.artist.trim() }
            weights[name] = (weights[name] ?: 0.0) + playWeight(play, nowEpochMs)
        }
        return weights
    }

    fun topArtists(plays: List<Play>, nowEpochMs: Long, top: Int = 40): List<String> =
        artistWeights(plays, nowEpochMs).entries.sortedByDescending { it.value }.take(top.coerceAtLeast(0)).map { it.key }

    /**
     * Orders a mix: known songs with new ones between them (two new in every five, so it stays mostly
     * familiar), and the same artist never twice in a row where that can be avoided.
     */
    fun <T> interleave(known: List<T>, fresh: List<T>, size: Int, artistOf: (T) -> String): List<T> {
        if (size <= 0) return emptyList()
        val out = ArrayList<T>(minOf(size, known.size + fresh.size))
        var nextKnown = 0
        var nextFresh = 0
        while (out.size < size && (nextKnown < known.size || nextFresh < fresh.size)) {
            val wantFresh = out.size % 5 == 2 || out.size % 5 == 4
            out += when {
                wantFresh && nextFresh < fresh.size -> fresh[nextFresh++]
                nextKnown < known.size -> known[nextKnown++]
                else -> fresh[nextFresh++]
            }
        }
        separateRepeats(out, artistOf)
        return out
    }

    /** "A, B and C"; more than three names end "and more". Blank and repeated names are left out. */
    fun namesLine(names: List<String>): String {
        val shown = names.map(String::trim).filter(String::isNotEmpty).distinctBy(::key)
        return when (shown.size) {
            0 -> ""
            1 -> shown[0]
            2 -> "${shown[0]} and ${shown[1]}"
            3 -> "${shown[0]}, ${shown[1]} and ${shown[2]}"
            else -> "${shown[0]}, ${shown[1]}, ${shown[2]} and more"
        }
    }

    enum class TimeBucket(val label: String, val hours: IntRange) {
        MORNING("morning", 5..10),
        AFTERNOON("afternoon", 11..16),
        EVENING("evening", 17..20),
        NIGHT("night", 21..23),
        LATE("late-night", 0..4);

        companion object {
            fun of(hour: Int): TimeBucket {
                val wrapped = ((hour % 24) + 24) % 24
                return entries.first { wrapped in it.hours }
            }
        }
    }

    fun timeTitle(weekday: String, bucket: TimeBucket, genre: String?): String =
        "Your $weekday ${bucket.label} ${genre?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: "mix"}"

    /**
     * The genre a mix can be named after: more than half of the songs with a known genre, and at least
     * half of all songs known. Null otherwise, so a plurality of 15 rock, 10 indie and 3 hip-hop songs
     * is not called "indie".
     */
    fun mainGenre(genres: List<String?>): String? {
        val known = genres.mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
        if (known.isEmpty() || known.size * 2 < genres.size) return null
        val counts = LinkedHashMap<String, Int>()
        val spelling = HashMap<String, String>()
        for (genre in known) {
            val genreKey = genre.lowercase()
            spelling.getOrPut(genreKey) { genre }
            counts[genreKey] = (counts[genreKey] ?: 0) + 1
        }
        val (topKey, count) = counts.maxByOrNull { it.value } ?: return null
        return spelling.getValue(topKey).takeIf { count * 2 > known.size }
    }

    private fun key(name: String) = name.trim().lowercase()

    private fun usablePlays(plays: List<Play>) =
        plays.filter { it.artist.isNotBlank() && it.weight.isFinite() && it.weight > 0.0 }

    private class CoListening(val adjacent: Array<DoubleArray>, val ends: DoubleArray, val pairs: Double)

    /**
     * Counts artists played close together in one sitting: each visit to an artist (a run of its songs) pairs
     * with the visits just before it, up to [LOOKBACK_VISITS], so an artist is not cut off from the one two
     * visits back. An artist outside the top list still takes up a place in the sequence.
     */
    private fun coListening(plays: List<Play>, indexByKey: Map<String, Int>, n: Int): CoListening {
        val adjacent = Array(n) { DoubleArray(n) }
        val ends = DoubleArray(n)
        var pairs = 0.0
        val recent = ArrayDeque<Int?>()
        var lastKey: String? = null
        var lastAt = 0L
        for (play in plays.sortedBy { it.atEpochMs }) {
            val playKey = key(play.artist)
            if (lastKey == null || play.atEpochMs - lastAt > SITTING_GAP_MS) {
                recent.clear()
                lastKey = null
            }
            lastAt = play.atEpochMs
            if (playKey == lastKey) continue
            lastKey = playKey
            val artist = indexByKey[playKey]
            if (artist != null) {
                for (other in recent) {
                    if (other == null || other == artist) continue
                    adjacent[artist][other] += 1.0
                    adjacent[other][artist] += 1.0
                    ends[artist] += 1.0
                    ends[other] += 1.0
                    pairs += 1.0
                }
            }
            recent.addLast(artist)
            if (recent.size > LOOKBACK_VISITS) recent.removeFirst()
        }
        return CoListening(adjacent, ends, pairs)
    }

    /**
     * How close two artists are, from 0 to 1. Genre leads because a shuffled library puts every artist in
     * the same sitting; listening together only counts what is above chance.
     */
    private fun similarities(artists: List<String>, genreByKey: Map<String, String?>, co: CoListening): Array<DoubleArray> {
        val n = artists.size
        return Array(n) { a ->
            DoubleArray(n) { b ->
                if (a == b) return@DoubleArray 1.0
                val genreA = genreByKey[key(artists[a])]
                val genreB = genreByKey[key(artists[b])]
                val genre = when {
                    genreA == null || genreB == null -> UNKNOWN_GENRE
                    genreA == genreB -> 1.0
                    else -> 0.0
                }
                val expected = if (co.pairs == 0.0) 0.0 else co.ends[a] * co.ends[b] / (2 * co.pairs)
                val lift = if (expected <= 0.0) 0.0 else co.adjacent[a][b] / expected
                // A single meeting proves nothing; an artist with too little evidence gets the benefit of the doubt.
                val measured = if (co.adjacent[a][b] < 2) 0.0 else ((lift - 1) / (LIFT_FULL - 1)).coerceIn(0.0, 1.0)
                val listening = if (minOf(co.ends[a], co.ends[b]) < THIN_EVIDENCE) maxOf(measured, BENEFIT_OF_DOUBT) else measured
                GENRE_WEIGHT * genre + (1 - GENRE_WEIGHT) * listening
            }
        }
    }

    /** Average-linkage clustering: merges the two closest groups until none are close enough. */
    private fun merge(similarity: Array<DoubleArray>): List<List<Int>> {
        val groups = similarity.indices.map { mutableListOf(it) }.toMutableList()
        while (groups.size > 1) {
            var best = -1.0
            var first = -1
            var second = -1
            for (i in groups.indices) for (j in i + 1 until groups.size) {
                var sum = 0.0
                for (a in groups[i]) for (b in groups[j]) sum += similarity[a][b]
                val average = sum / (groups[i].size * groups[j].size)
                if (average > best) {
                    best = average
                    first = i
                    second = j
                }
            }
            if (best < MERGE_AT) break
            groups[first].addAll(groups[second])
            groups.removeAt(second)
        }
        return groups
    }

    /**
     * Reorders as little as possible so no artist plays twice in a row: each place takes the earliest song
     * by a different artist, except that an artist holding more than half of what is left goes first.
     * A repeat remains only when one artist has more than half of all songs.
     */
    private fun <T> separateRepeats(items: MutableList<T>, artistOf: (T) -> String) {
        val remaining = items.toMutableList()
        val counts = HashMap<String, Int>()
        for (item in remaining) counts[key(artistOf(item))] = (counts[key(artistOf(item))] ?: 0) + 1
        items.clear()
        var previous: String? = null
        while (remaining.isNotEmpty()) {
            val dominant = counts.entries.firstOrNull { it.value * 2 > remaining.size }?.key
            var index = remaining.indexOfFirst {
                val artist = key(artistOf(it))
                artist != previous && (dominant == null || dominant == previous || artist == dominant)
            }
            if (index < 0) index = remaining.indexOfFirst { key(artistOf(it)) != previous }
            if (index < 0) index = 0
            val picked = remaining.removeAt(index)
            val pickedKey = key(artistOf(picked))
            counts[pickedKey] = (counts[pickedKey] ?: 0) - 1
            previous = pickedKey
            items += picked
        }
    }

    private const val DAY_MS = 86_400_000.0
    private const val DECAY_DAYS = 30.0
    private const val SITTING_GAP_MS = 30 * 60_000L
    private const val LOOKBACK_VISITS = 2
    private const val GENRE_WEIGHT = 0.45
    private const val UNKNOWN_GENRE = 0.3
    private const val MERGE_AT = 0.5
    private const val LIFT_FULL = 2.0
    private const val THIN_EVIDENCE = 6.0
    private const val BENEFIT_OF_DOUBT = 0.2
    private const val GROUP_MIN = 0.04
    private const val SOLO_MIN = 0.08
}
