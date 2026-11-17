package com.convx.music.ui.screens.artist

import com.music.innertube.models.AlbumItem
import com.music.innertube.pages.ArtistSection

internal fun List<ArtistSection>.featuredRelease(artistId: String): Pair<String, AlbumItem>? {
    fun AlbumItem.belongsToArtist(): Boolean {
        val releaseArtists = artists
        return releaseArtists.isNullOrEmpty() || releaseArtists.any { it.id == artistId }
    }

    firstOrNull {
        it.title.contains("Latest", ignoreCase = true) &&
            (it.items.firstOrNull() as? AlbumItem)?.belongsToArtist() == true
    }?.let { return it.title to it.items.first() as AlbumItem }

    val candidates = flatMap { section ->
        section.items.filterIsInstance<AlbumItem>()
            .filter { it.belongsToArtist() }
            .map { section.title to it }
    }
    candidates.filter { it.second.year != null }
        .maxByOrNull { it.second.year!! }
        ?.let { return it }

    return firstNotNullOfOrNull { section ->
        (section.items.firstOrNull() as? AlbumItem)
            ?.takeIf { it.belongsToArtist() }
            ?.let { section.title to it }
    }
}
