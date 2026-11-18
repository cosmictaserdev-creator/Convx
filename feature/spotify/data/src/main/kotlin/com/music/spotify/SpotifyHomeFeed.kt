package com.music.spotify

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

enum class SpotifyHomeItemType {
    PLAYLIST,
    ALBUM,
    ARTIST,
}

data class SpotifyHomeItem(
    val uri: String,
    val type: SpotifyHomeItemType,
    val title: String,
    val artworkUrl: String?,
) {
    val playlistId: String?
        get() = uri.substringAfterLast(':').takeIf { type == SpotifyHomeItemType.PLAYLIST && it.isNotBlank() }
}

data class SpotifyHomeShelf(
    val title: String,
    val items: List<SpotifyHomeItem>,
    val sectionUri: String = "",
)

object SpotifyHomeFeed {
    private const val BASELINE_SECTION = "HomeFeedBaselineSectionData"
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(response: String): List<SpotifyHomeShelf> = runCatching {
        val sections = (json.parseToJsonElement(response) as? JsonObject)
            ?.obj("data")?.obj("home")?.obj("sectionContainer")
            ?.obj("sections")?.array("items") ?: return emptyList()

        val shelves = mutableListOf<SpotifyHomeShelf>()
        val gathered = linkedMapOf<String, SpotifyHomeShelf>()

        sections.forEach { element ->
            val section = element as? JsonObject ?: return@forEach
            val data = section.obj("data") ?: return@forEach
            val shelf = parseShelf(section, data) ?: return@forEach
            val isBaseline = data.string("__typename") == BASELINE_SECTION
            if (!isBaseline) {
                shelves += shelf
                return@forEach
            }

            val artistUri = data.obj("headerEntity")?.obj("data")?.string("uri")
                ?.takeIf { it.startsWith("spotify:artist:") }
            val key = artistUri ?: "title:${shelf.title}"
            val previous = gathered[key]
            gathered[key] = if (previous == null) {
                shelf.copy(sectionUri = "")
            } else {
                previous.copy(items = (previous.items + shelf.items).distinctBy(SpotifyHomeItem::uri))
            }
        }

        shelves + gathered.values
    }.getOrDefault(emptyList())

    private fun parseShelf(section: JsonObject, data: JsonObject): SpotifyHomeShelf? {
        val title = data.obj("title")?.string("transformedLabel")?.takeIf(String::isNotBlank)
            ?: return null
        val items = section.obj("sectionItems")?.array("items")
            ?.mapNotNull(::parseItem).orEmpty()
        if (items.isEmpty()) return null
        return SpotifyHomeShelf(title = title, items = items, sectionUri = section.string("uri").orEmpty())
    }

    private fun parseItem(element: kotlinx.serialization.json.JsonElement): SpotifyHomeItem? {
        val item = element as? JsonObject ?: return null
        val uri = item.string("uri")?.takeIf(String::isNotBlank) ?: return null
        val data = item.obj("content")?.obj("data") ?: return null

        val type: SpotifyHomeItemType
        val title: String
        val artworkSources: JsonArray?
        when (data.string("__typename")) {
            "Playlist" -> {
                type = SpotifyHomeItemType.PLAYLIST
                title = data.string("name").orEmpty()
                artworkSources = data.obj("images")?.array("items")?.firstOrNull()
                    ?.let { it as? JsonObject }?.array("sources")
            }
            "Album" -> {
                type = SpotifyHomeItemType.ALBUM
                title = data.string("name").orEmpty()
                artworkSources = data.obj("coverArt")?.array("sources")
            }
            "Artist" -> {
                type = SpotifyHomeItemType.ARTIST
                title = data.obj("profile")?.string("name").orEmpty()
                artworkSources = data.obj("visuals")?.obj("avatarImage")?.array("sources")
            }
            else -> return null
        }

        if (title.isBlank()) return null
        return SpotifyHomeItem(
            uri = uri,
            type = type,
            title = title,
            artworkUrl = artworkSources?.mapNotNull { source ->
                val sourceObject = source as? JsonObject ?: return@mapNotNull null
                val url = sourceObject.string("url")?.takeIf(String::isNotBlank)
                    ?: return@mapNotNull null
                url to (sourceObject.int("width") ?: 0)
            }?.maxByOrNull { it.second }?.first,
        )
    }

    private fun JsonObject.obj(key: String): JsonObject? = this[key] as? JsonObject

    private fun JsonObject.array(key: String): JsonArray? = this[key] as? JsonArray

    private fun JsonObject.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull

    private fun JsonObject.int(key: String): Int? =
        (this[key] as? JsonPrimitive)?.intOrNull
}
