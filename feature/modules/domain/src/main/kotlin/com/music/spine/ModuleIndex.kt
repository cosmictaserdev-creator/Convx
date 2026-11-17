package com.music.spine

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object ModuleIndex {
    private val excludedCategories = setOf("category:artworks", "category:testing")
    private val listSerializer = ListSerializer(SpineModule.serializer())

    /** Parses modules from every category key except artwork and testing lists. */
    fun parseModules(json: Json, body: String): List<SpineModule> {
        val obj = json.decodeFromString(JsonObject.serializer(), body)
        return obj.entries
            .filter { it.key.startsWith("category:") && it.key !in excludedCategories }
            .flatMap { (_, value) ->
                runCatching { json.decodeFromJsonElement(listSerializer, value) }.getOrElse { emptyList() }
            }
            .distinctBy { it.id }
    }
}
