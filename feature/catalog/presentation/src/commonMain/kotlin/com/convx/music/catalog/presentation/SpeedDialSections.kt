package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogArtist
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.HomeSectionOrder
import com.convx.music.playback.SpeedDialPin
import com.convx.music.playback.SpeedDialSnapshot

/** Home's Speed Dial: the songs, albums, artists and playlists the listener pinned. */
object SpeedDialSections {
    fun pinOf(entry: CatalogEntry): SpeedDialPin? {
        val (kind, subtitle) = when (entry) {
            is CatalogEntry.Track -> "track" to entry.artists.joinToString { it.name }
            is CatalogEntry.Album -> "album" to entry.artists.joinToString { it.name }
            is CatalogEntry.Artist -> "artist" to entry.subtitle
            is CatalogEntry.Playlist -> "playlist" to entry.description
            is CatalogEntry.Browse -> return null
        }
        return SpeedDialPin(kind, entry.id.providerId, entry.id.value, entry.title, subtitle?.takeIf { it.isNotBlank() }, entry.artworkUrls.firstOrNull())
    }

    fun keyOf(entry: CatalogEntry): String? = pinOf(entry)?.key

    fun sections(snapshot: SpeedDialSnapshot): List<CatalogSection> {
        val entries = snapshot.pins.mapNotNull(::entryOf)
        return if (entries.isEmpty()) emptyList() else listOf(CatalogSection(HomeSectionOrder.SPEED_DIAL_ID, "Speed dial", entries))
    }

    private fun entryOf(pin: SpeedDialPin): CatalogEntry? {
        val id = CatalogId(pin.providerId, pin.id)
        val art = listOfNotNull(pin.artworkUrl)
        val artists = pin.subtitle?.let { listOf(CatalogArtist(null, it)) }.orEmpty()
        return when (pin.kind) {
            "track" -> CatalogEntry.Track(id, pin.title, art, artists, null, null, isExplicit = false, isVideo = false, isPlayable = true)
            "album" -> CatalogEntry.Album(id, pin.title, art, artists, year = null, trackCount = null)
            "artist" -> CatalogEntry.Artist(id, pin.title, art, pin.subtitle)
            "playlist" -> CatalogEntry.Playlist(id, pin.title, art, pin.subtitle, trackCount = null, isEditable = false)
            else -> null
        }
    }
}
