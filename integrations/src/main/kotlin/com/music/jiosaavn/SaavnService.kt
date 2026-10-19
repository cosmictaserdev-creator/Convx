/**
 * convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * JioSaavn audio streaming service.
 * Talks to jiosaavn.com's own private API directly (the same one the official
 * app uses) rather than a third-party proxy — search returns a song id, a
 * detail call returns a DES-encrypted CDN URL for it.
 *
 * API endpoints used (all through api.php with an __call parameter):
 *   - search.getResults  → search songs by free-form query
 *   - song.getDetails    → song details incl. encrypted_media_url
 */

package com.music.jiosaavn

import android.util.Base64
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private val STREAM_QUALITY_SUFFIX = Regex("_(48|96|160|320)\\.(mp4|aac|mp3)$")

// Data models

@Serializable
data class RawArtistMapItem(
    val id: String = "",
    val name: String = "",
)

@Serializable
data class RawArtistMap(
    @SerialName("primary_artists") val primaryArtists: List<RawArtistMapItem> = emptyList(),
)

@Serializable
data class RawMoreInfo(
    val album_id: String = "",
    val album: String = "",
    @SerialName("encrypted_media_url") val encryptedMediaUrl: String = "",
    val duration: String = "",
    /**
     * Whether a 320kbps rendition exists, as `"true"`/`"false"`.
     *
     * The catalogue states this per track and it is frequently false. Asking
     * the CDN for `_320` anyway does not produce one.
     */
    @SerialName("320kbps") val has320: String = "",
    val artistMap: RawArtistMap = RawArtistMap(),
) {
    val supports320: Boolean get() = has320.equals("true", ignoreCase = true)
}

/** A decoded CDN URL and the bitrate it will really deliver. */
data class SaavnStream(val url: String, val kbps: Int?)

@Serializable
data class RawSongItem(
    val id: String = "",
    val title: String = "",
    val image: String = "",
    @SerialName("more_info") val moreInfo: RawMoreInfo = RawMoreInfo(),
)

@Serializable
data class RawSearchResponse(
    val results: List<RawSongItem> = emptyList(),
)

@Serializable
data class RawSongsResponse(
    val songs: List<RawSongItem> = emptyList(),
)

object SaavnService {
    private const val TAG = "SaavnService"

    // https://www.jiosaavn.com/api.php
    private val BASE_URL = String(
        Base64.decode("aHR0cHM6Ly93d3cuamlvc2Fhdm4uY29tL2FwaS5waHA=", Base64.DEFAULT),
        Charsets.UTF_8,
    )

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                requestTimeoutMillis = 6_000
                connectTimeoutMillis = 4_000
                socketTimeoutMillis = 6_000
            }
            defaultRequest {
                url(BASE_URL)
                headers.append(HttpHeaders.Accept, "application/json")
                headers.append(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36",
                )
                headers.append("X-Forwarded-For", "49.36.0.1")
                headers.append("X-Real-IP", "49.36.0.1")
                headers.append("Accept-Language", "en-IN,en;q=0.9")
                headers.append(HttpHeaders.Cookie, "explicit_content=1")
            }
            expectSuccess = false
        }
    }

    private fun decryptUrl(encryptedUrl: String): String {
        if (encryptedUrl.isBlank()) return ""
        return try {
            val key = "38346591" // DES 8-byte key
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "DES")
            val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(encryptedUrl, Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8).trim()
        } catch (e: Exception) {
            Log.e(TAG, "JioSaavn URL decryption failed", e)
            ""
        }
    }

    /** Returns the best available URL without requesting a bitrate the track does not support. */
    private fun bestStream(encryptedUrl: String, supports320: Boolean): SaavnStream? {
        val decryptedUrl = decryptUrl(encryptedUrl)
        if (decryptedUrl.isBlank()) return null

        val suffix = STREAM_QUALITY_SUFFIX.find(decryptedUrl)
            // No recognisable rung in the name, so there is nothing to rewrite
            // and nothing to claim: the bitrate goes up as unknown rather than
            // as a guess.
            ?: return SaavnStream(decryptedUrl, if (supports320) 320 else null)

        val offered = suffix.groupValues[1].toIntOrNull()
        val extension = suffix.groupValues[2]
        return if (supports320) {
            SaavnStream(decryptedUrl.replaceRange(suffix.range, "_320.$extension"), 320)
        } else {
            SaavnStream(decryptedUrl, offered)
        }
    }

    /**
     * Search for songs on JioSaavn by a free-form query (title + artist recommended).
     */
    suspend fun searchSongs(query: String): List<RawSongItem> = runCatching {
        Log.d(TAG, "searchSongs: query=\"$query\"")
        val response = client.get("") {
            parameter("__call", "search.getResults")
            parameter("_format", "json")
            parameter("_marker", "0")
            parameter("api_version", "4")
            parameter("ctx", "android")
            parameter("q", query)
            parameter("p", "1")
            parameter("n", "10")
        }

        if (response.status != HttpStatusCode.OK) {
            Log.w(TAG, "Saavn search failed: HTTP ${response.status.value}")
            return@runCatching emptyList()
        }

        val body = json.decodeFromString<RawSearchResponse>(response.bodyAsText())
        Log.d(TAG, "searchSongs: results.size=${body.results.size}")
        body.results
    }.getOrElse {
        Log.w(TAG, "Saavn search error: ${it.message}")
        emptyList()
    }

    /**
     * Fetch the song detail for a known Saavn song id and decrypt its best
     * available stream URL. Returns null only if no usable stream could be
     * resolved at all.
     */
    suspend fun getStreamUrl(saavnSongId: String): SaavnStream? {
        val result = runCatching {
            Log.d(TAG, "getStreamUrl: saavnSongId=$saavnSongId")
            val response = client.get("") {
                parameter("__call", "song.getDetails")
                parameter("_format", "json")
                parameter("_marker", "0")
                parameter("api_version", "4")
                parameter("ctx", "android")
                parameter("pids", saavnSongId)
            }

            if (response.status != HttpStatusCode.OK) {
                Log.w(TAG, "Saavn getDetails failed: HTTP ${response.status.value}")
                return@runCatching null
            }

            // Details may be keyed by song ID or returned in a songs array; accept both shapes.
            val root = json.parseToJsonElement(response.bodyAsText()) as? JsonObject
                ?: return@runCatching null
            val songElement = (root["songs"] as? JsonArray)?.firstOrNull()
                ?: root.values.firstOrNull { it is JsonObject }
                ?: run {
                    Log.w(TAG, "Saavn getDetails held no song for $saavnSongId")
                    return@runCatching null
                }
            val rawSong = json.decodeFromJsonElement(RawSongItem.serializer(), songElement)

            bestStream(rawSong.moreInfo.encryptedMediaUrl, rawSong.moreInfo.supports320)
        }
        return result.onFailure { Log.w(TAG, "Saavn getDetails error: ${it.message}") }.getOrNull()
    }

    /** Reads the stream size with a HEAD request, or returns null when unavailable. */
    suspend fun getContentLength(url: String): Long? = runCatching {
        Log.d(TAG, "getContentLength: url=$url")
        // Use short timeouts so this metadata request does not delay playback.
        val headClient = HttpClient(OkHttp) {
            install(HttpTimeout) {
                requestTimeoutMillis = 3_000
                connectTimeoutMillis = 2_000
                socketTimeoutMillis = 3_000
            }
            expectSuccess = false
            followRedirects = true
        }
        headClient.use { c ->
            val response = c.head(url)
            Log.d(TAG, "getContentLength: HTTP status=${response.status}")
            if (response.status == HttpStatusCode.OK ||
                response.status == HttpStatusCode.PartialContent
            ) {
                val len = response.headers[HttpHeaders.ContentLength]?.toLongOrNull()
                Log.d(TAG, "getContentLength: resolved length=$len")
                len
            } else {
                null
            }
        }
    }.onFailure {
        Log.e(TAG, "getContentLength failed for url=$url", it)
    }.getOrNull()
}
