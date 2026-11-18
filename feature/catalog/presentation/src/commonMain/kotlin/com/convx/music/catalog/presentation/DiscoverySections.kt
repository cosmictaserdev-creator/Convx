package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogArtist
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogSection
import com.convx.music.playback.DailyMix
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.TrackedRelease

/** Home shelves built from the listener's own history: Daily Mixes and new releases from their artists. */
object DiscoverySections {
    const val MIX_PROVIDER = "convx-mix"
    const val MIXES_SECTION_ID = "discovery-mixes"
    const val RELEASES_SECTION_ID = "discovery-releases"
    val FEATURED_IDS = setOf(MIXES_SECTION_ID, RELEASES_SECTION_ID)
    private const val YOUTUBE_MUSIC = "youtube-music"
    private const val MAX_RELEASES = 20

    fun sections(mixes: List<DailyMix>, releases: List<TrackedRelease>): List<CatalogSection> = buildList {
        if (mixes.isNotEmpty()) add(
            CatalogSection(
                id = MIXES_SECTION_ID,
                title = "Made for you",
                entries = mixes.mapIndexed { index, mix ->
                    CatalogEntry.Playlist(
                        id = CatalogId(MIX_PROVIDER, index.toString()),
                        title = mix.title,
                        artworkUrls = mix.plays.mapNotNull { it.artworkUrl }.distinct().take(4),
                        description = mix.subtitle,
                        trackCount = mix.plays.size,
                        isEditable = false,
                    )
                },
            ),
        )
        val fresh = releases.distinctBy { it.id }.take(MAX_RELEASES)
        if (fresh.isNotEmpty()) add(
            CatalogSection(
                id = RELEASES_SECTION_ID,
                title = "New from your artists",
                entries = fresh.map { release ->
                    CatalogEntry.Album(
                        id = CatalogId(YOUTUBE_MUSIC, release.id),
                        title = release.title,
                        artworkUrls = listOfNotNull(release.thumbnailUrl),
                        artists = listOf(CatalogArtist(CatalogId(YOUTUBE_MUSIC, release.artistId), release.artistName)),
                        year = release.year.takeIf { it > 0 }?.toString(),
                        trackCount = null,
                    )
                },
            ),
        )
    }

    /** The small line above a featured card's title, or null for entries that are not discovery shelves'. */
    fun kicker(entry: CatalogEntry): String? = when {
        entry.id.providerId == MIX_PROVIDER -> "Made for You"
        entry is CatalogEntry.Album && entry.id.providerId == YOUTUBE_MUSIC && entry.year != null -> "New Release"
        else -> null
    }

    /** The tracks of the Daily Mix a shelf entry stands for, or null when [id] is not a mix of [mixes]. */
    fun mixTracks(mixes: List<DailyMix>, id: CatalogId): List<PlaybackRequestTrack>? {
        if (id.providerId != MIX_PROVIDER) return null
        val mix = id.value.toIntOrNull()?.let(mixes::getOrNull) ?: return null
        return mix.plays.map { PlaybackRequestTrack(it.providerId, it.songId, it.title, it.artists, it.durationMs, it.artworkUrl) }
    }
}
