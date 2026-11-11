package com.convx.music.playback

data class PlannedMix(val artists: List<String>, val songIds: List<String>)

/** Chooses the songs of each Daily Mix from the listener's play history. */
object DailyMixPlanner {
    const val MIX_COUNT = 3
    const val MIX_SIZE = 25
    const val MIN_SONGS = 5

    /**
     * One mix per cluster of artists that go together: their songs, the most played lately first, ordered so
     * no artist plays twice in a row where that can be avoided. No song appears in two mixes, and a mix with
     * fewer than [minSongs] songs is dropped.
     */
    fun plan(
        plays: List<MadeForYou.Play>,
        nowEpochMs: Long,
        mixCount: Int = MIX_COUNT,
        mixSize: Int = MIX_SIZE,
        minSongs: Int = MIN_SONGS,
    ): List<PlannedMix> {
        if (mixSize <= 0) return emptyList()
        val usable = plays.filter { it.artist.isNotBlank() && it.songId.isNotBlank() }
        val used = HashSet<String>()
        return MadeForYou.clusters(usable, emptyMap(), nowEpochMs, max = mixCount).mapNotNull { cluster ->
            val members = cluster.artists.mapTo(HashSet()) { it.trim().lowercase() }
            val ranked = usable
                .filter { it.artist.trim().lowercase() in members && it.songId !in used }
                .groupBy { it.songId }
                .map { (songId, songPlays) ->
                    Triple(songId, songPlays.first().artist, songPlays.sumOf { MadeForYou.playWeight(it, nowEpochMs) })
                }
                .sortedByDescending { it.third }
                .take(mixSize)
            val ordered = MadeForYou.interleave(ranked.map { it.first to it.second }, emptyList(), mixSize) { it.second }
            if (ordered.size < minSongs) return@mapNotNull null
            ordered.forEach { used += it.first }
            PlannedMix(cluster.artists, ordered.map { it.first })
        }
    }
}
