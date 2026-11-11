package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogArtist
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.HomeSectionOrder
import com.convx.music.playback.HistoryPlay

/** Home shelves drawn from what the listener has played: Listen Again, Quick Picks, Top Songs, Forgotten Favorites and the Hero card. */
object HistorySections {
    private const val DAY_MS = 86_400_000L
    private const val MAX_ITEMS = 20

    fun sections(plays: List<HistoryPlay>, nowEpochMs: Long): List<CatalogSection> {
        if (plays.isEmpty()) return emptyList()
        val latest = HashMap<String, HistoryPlay>()
        for (play in plays) latest[play.songId] = play
        fun tracks(ids: Iterable<String>) = ids.mapNotNull(latest::get).map(::track)

        val recent = plays.asReversed().map { it.songId }.distinct()
        val monthAgo = nowEpochMs - 30 * DAY_MS
        val weekAgo = nowEpochMs - 7 * DAY_MS
        val counts = plays.groupingBy { it.songId }.eachCount()
        val newCounts = plays.filter { it.atEpochMs >= monthAgo }.groupingBy { it.songId }.eachCount()
        val oldCounts = plays.filter { it.atEpochMs < monthAgo }.groupingBy { it.songId }.eachCount()

        val topSongs = newCounts.entries.sortedByDescending { it.value }.map { it.key }.take(10)
        val forgotten = oldCounts.filter { (id, old) -> 0.2 * old > (newCounts[id] ?: 0) }
            .entries.sortedByDescending { it.value }.map { it.key }.take(MAX_ITEMS)
        val quickPicks = (recent.take(5) +
            plays.filter { it.atEpochMs >= weekAgo }.groupingBy { it.songId }.eachCount()
                .entries.sortedByDescending { it.value }.take(5).map { it.key } +
            counts.entries.sortedByDescending { it.value }.take(10).map { it.key })
            .distinct().take(MAX_ITEMS)

        return buildList {
            quickPicks.firstOrNull()?.let { add(CatalogSection(HomeSectionOrder.HERO_ID, "Picked for you", tracks(listOf(it)))) }
            if (quickPicks.isNotEmpty()) add(CatalogSection(HomeSectionOrder.QUICK_PICKS_ID, "Quick picks", tracks(quickPicks)))
            if (recent.isNotEmpty()) add(CatalogSection(HomeSectionOrder.LISTEN_AGAIN_ID, "Listen again", tracks(recent.take(MAX_ITEMS))))
            if (topSongs.isNotEmpty()) add(CatalogSection(HomeSectionOrder.TOP_SONGS_ID, "Top songs", tracks(topSongs)))
            if (forgotten.isNotEmpty()) add(CatalogSection(HomeSectionOrder.FORGOTTEN_ID, "Forgotten favorites", tracks(forgotten)))
        }
    }

    private fun track(play: HistoryPlay) = CatalogEntry.Track(
        id = CatalogId(play.providerId, play.songId),
        title = play.title,
        artworkUrls = listOfNotNull(play.artworkUrl),
        artists = play.artists.map { CatalogArtist(null, it) },
        album = null,
        durationMillis = play.durationMs,
        isExplicit = false,
        isVideo = false,
        isPlayable = true,
    )
}
