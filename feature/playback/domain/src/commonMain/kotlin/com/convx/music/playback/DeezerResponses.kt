package com.convx.music.playback

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** Reads Deezer's public API answers; anything malformed reads as "nothing found". */
object DeezerResponses {
    /** The id of the artist with exactly [name] and the most fans. */
    fun artistId(json: String, name: String): String? {
        val wanted = ReleaseRadarRules.normalize(name)
        if (wanted.isEmpty()) return null
        return data(json)
            .filter { ReleaseRadarRules.normalize(it["name"].text().orEmpty()) == wanted }
            .maxByOrNull { it["nb_fan"].text()?.toLongOrNull() ?: 0L }
            ?.get("id").text()
            ?.takeIf { it.isNotBlank() }
    }

    /** (title, release date) of every album listed. */
    fun albums(json: String): List<Pair<String, String>> = data(json).mapNotNull {
        val title = it["title"].text()?.takeIf(String::isNotBlank) ?: return@mapNotNull null
        val date = it["release_date"].text() ?: return@mapNotNull null
        title to date
    }

    private fun data(json: String): List<JsonObject> {
        val root = try {
            Json.parseToJsonElement(json) as? JsonObject
        } catch (_: SerializationException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
        return (root?.get("data") as? JsonArray).orEmpty().mapNotNull { it as? JsonObject }
    }

    private fun JsonElement?.text(): String? = (this as? JsonPrimitive)?.contentOrNull
}
