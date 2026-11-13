package com.convx.music.canvas

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/** Parses portable catalog fields without depending on HTTP, caching, or filesystem APIs. */
object AppleMusicDataMapper {
    fun findKeyColor(element: JsonElement?): Int? = when (element) {
        is JsonObject -> element["keyColor"]?.let { runCatching { it.jsonPrimitive.contentOrNull }.getOrNull() }
            ?.let(::hexColor)
            ?: element.values.firstNotNullOfOrNull(::findKeyColor)
        is JsonArray -> element.firstNotNullOfOrNull(::findKeyColor)
        else -> null
    }

    fun artwork(element: JsonElement): AppleArtwork? {
        val value = element as? JsonObject ?: return null
        val url = value["url"]?.jsonPrimitive?.contentOrNull ?: return null
        return AppleArtwork(
            url = url,
            width = value["width"]?.jsonPrimitive?.intOrNull ?: 0,
            height = value["height"]?.jsonPrimitive?.intOrNull ?: 0,
            bgColor = value.string("bgColor").let(::hexColor),
            textColor1 = value.string("textColor1").let(::hexColor),
            textColor2 = value.string("textColor2").let(::hexColor),
            textColor3 = value.string("textColor3").let(::hexColor),
            textColor4 = value.string("textColor4").let(::hexColor),
        )
    }

    fun hexColor(value: String?): Int? =
        value?.removePrefix("#")?.takeIf { it.length == 6 }?.toIntOrNull(16)

    private fun JsonObject.string(key: String): String = this[key]?.let {
        runCatching { it.jsonPrimitive.contentOrNull }.getOrNull()
    }.orEmpty()
}
