package com.convx.music.canvas

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder

/** Optional artist wordmark, shared by the Android and desktop clients. */
object ArtistSignature {
    suspend fun findUrl(artistName: String): String? = withTimeoutOrNull(7_000L) {
        if (artistName.isBlank()) return@withTimeoutOrNull null
        try {
            val name = URLEncoder.encode(artistName.trim(), Charsets.UTF_8).replace("+", "%20")
            val response = AppleMusicCatalog.client.get("https://artist-signature.onrender.com/v1/name/$name?format=svg&type=handwritten")
            if (response.status.value !in 200..299) return@withTimeoutOrNull null
            Json.parseToJsonElement(response.bodyAsText()).jsonObject["signatures"]?.jsonArray
                ?.firstNotNullOfOrNull { item -> item.jsonObject["asset"]?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull
                    ?.takeIf { it.startsWith("https://") } }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) { null }
    }
}
