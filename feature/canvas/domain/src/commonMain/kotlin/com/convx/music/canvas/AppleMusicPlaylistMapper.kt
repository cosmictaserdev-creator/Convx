package com.convx.music.canvas

import kotlinx.serialization.json.*

/** Maps Apple catalog playlist responses into the import model without owning HTTP transport. */
object AppleMusicPlaylistMapper {
    suspend fun fetch(
        link: ApplePlaylistLink,
        request: suspend (String) -> JsonObject?,
    ): ApplePlaylist {
        val base = "/v1/catalog/${link.storefront}/playlists/${link.id}"
        val root = request(base)
            ?: error("Playlist is unavailable")
        val playlist = root["data"]?.jsonArray?.firstOrNull()?.jsonObject
            ?: error("Playlist is unavailable")
        val attributes = playlist["attributes"]?.jsonObject ?: error("Playlist has no metadata")
        val title = attributes["name"]?.jsonPrimitive?.contentOrNull
            ?: error("Playlist has no name")
        val artworkUrl = attributes["artwork"]?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull
            ?.replace("{w}", "600")
            ?.replace("{h}", "600")
            ?.replace("{f}", "jpg")
        val tracks = mutableListOf<ApplePlaylistTrack>()
        var unsupported = 0
        var page: String? = "$base/tracks?limit=100"
        val visited = mutableSetOf<String>()
        while (page != null) {
            check(visited.add(page) && visited.size <= 1000) { "Invalid playlist pagination" }
            val response = request(page)
                ?: error("Playlist tracks are unavailable")
            val data = response["data"] as? JsonArray ?: error("Invalid playlist response")
            data.forEach { element ->
                val item = element.jsonObject
                val attrs = item["attributes"] as? JsonObject
                val name = attrs?.get("name")?.jsonPrimitive?.contentOrNull
                val artist = attrs?.get("artistName")?.jsonPrimitive?.contentOrNull
                if (item["type"]?.jsonPrimitive?.contentOrNull != "songs" || name.isNullOrBlank() || artist.isNullOrBlank()) {
                    unsupported++
                } else {
                    tracks += ApplePlaylistTrack(name, artist, attrs["durationInMillis"]?.jsonPrimitive?.intOrNull ?: 0)
                }
            }
            page = response["next"]?.jsonPrimitive?.contentOrNull?.let { next ->
                ApplePlaylistPagination.validateNextPath(base, next)
            }
        }
        return ApplePlaylist(title, tracks, unsupported, artworkUrl)
    }
}
