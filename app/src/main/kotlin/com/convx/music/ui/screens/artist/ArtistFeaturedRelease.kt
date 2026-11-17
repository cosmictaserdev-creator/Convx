package com.convx.music.ui.screens.artist

import com.music.innertube.models.AlbumItem
import com.music.innertube.pages.ArtistSection

internal fun List<ArtistSection>.featuredRelease(artistId: String): Pair<String, AlbumItem>? {
    firstOrNull {
        it.title.contains("Latest", ignoreCase = true) && it.items.firstOrNull() is AlbumItem
    }?.let { return it.title to it.items.first() as AlbumItem }

    val releaseSections = filter { it.moreEndpoint?.browseId == artistId }
    val newest = releaseSections.flatMap { section ->
        section.items.filterIsInstance<AlbumItem>().mapNotNull { album ->
            album.year?.let { year -> Triple(section.title, album, year) }
        }
    }.maxByOrNull { it.third }
    if (newest != null) return newest.first to newest.second

    return firstNotNullOfOrNull { section ->
        (section.items.firstOrNull() as? AlbumItem)?.let { section.title to it }
    }
}
