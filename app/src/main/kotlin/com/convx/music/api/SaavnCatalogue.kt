/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * JioSaavn as a searchable catalogue, the way Echo's Saavn extension exposes it:
 * a free-form query in, playable items out. The network and URL decryption live in
 * SaavnService (integrations); this only maps its models onto Convx's own item
 * types, so the existing list rows, menus and queue code work unchanged.
 */

package com.convx.music.api

import com.music.innertube.models.Album
import com.music.innertube.models.Artist
import com.music.innertube.models.SongItem
import com.music.jiosaavn.RawSongItem
import com.music.jiosaavn.SAAVN_SONG_ID_PREFIX
import com.music.jiosaavn.SaavnService
import com.music.jiosaavn.saavnThumbnail

object SaavnCatalogue {
    /**
     * Songs matching [query]. Empty on any network or parse failure — a Saavn
     * outage must not take the YouTube results down with it.
     */
    suspend fun searchSongs(query: String, limit: Int = 10): List<SongItem> {
        if (query.isBlank()) return emptyList()
        return SaavnService.searchSongs(query)
            .mapNotNull { it.toSongItem() }
            .distinctBy { it.id }
            .take(limit)
    }
}

internal fun RawSongItem.toSongItem(): SongItem? {
    if (id.isBlank() || title.isBlank()) return null
    val primaryArtists = moreInfo.artistMap.primaryArtists.mapNotNull { artist ->
        artist.name.takeIf { it.isNotBlank() }?.let { Artist(name = saavnUnescape(it), id = null) }
    }
    return SongItem(
        id = SAAVN_SONG_ID_PREFIX + id,
        title = saavnUnescape(title),
        artists = primaryArtists,
        album = moreInfo.album.takeIf { it.isNotBlank() }?.let {
            Album(name = saavnUnescape(it), id = moreInfo.album_id)
        },
        duration = moreInfo.duration.trim().toIntOrNull(),
        thumbnail = saavnThumbnail(image),
        explicit = false,
    )
}

/** Saavn returns HTML-escaped names ("&quot;", "&amp;"). Decode the handful it uses. */
private fun saavnUnescape(value: String): String = value
    .replace("&quot;", "\"")
    .replace("&#39;", "'")
    .replace("&apos;", "'")
    .replace("&amp;", "&")
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .trim()
