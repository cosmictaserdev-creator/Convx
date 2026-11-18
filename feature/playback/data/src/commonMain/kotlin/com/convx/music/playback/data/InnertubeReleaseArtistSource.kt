package com.convx.music.playback.data

import com.convx.music.playback.ReleaseArtistSource
import com.convx.music.playback.ReleaseRadarRules
import com.convx.music.playback.SeenRelease
import com.convx.music.playback.WatchedArtists
import com.music.innertube.InnertubeRepository
import com.music.innertube.models.AlbumItem
import com.music.innertube.pages.ArtistPage

/** Reads each artist's albums, singles and EPs from YouTube Music; the host decides which artists to watch. */
class InnertubeReleaseArtistSource(
    private val innertube: InnertubeRepository,
    private val watched: suspend (nowEpochMs: Long) -> WatchedArtists,
) : ReleaseArtistSource {
    override suspend fun watchedArtists(nowEpochMs: Long): WatchedArtists = watched(nowEpochMs)

    override suspend fun releases(artistId: String): List<SeenRelease>? =
        innertube.artist(artistId).getOrNull()?.let { seenReleases(it, artistId) }

    companion object {
        /** Albums credited only to other artists, such as "Appears on" shelves, are left out. */
        fun seenReleases(page: ArtistPage, artistId: String): List<SeenRelease> = page.sections
            .filter { section -> section.items.isNotEmpty() && section.items.all { it is AlbumItem } }
            .flatMap { section ->
                val kind = ReleaseRadarRules.kindOfSection(section.title)
                section.items.filterIsInstance<AlbumItem>()
                    .filter { album ->
                        val credited = album.artists
                        credited.isNullOrEmpty() || credited.any { it.id == artistId }
                    }
                    .map { SeenRelease(it.browseId, it.title, kind, it.year ?: 0, it.thumbnail) }
            }
            .distinctBy { it.id }
    }
}
