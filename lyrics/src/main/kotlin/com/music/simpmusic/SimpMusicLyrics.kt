package com.music.simpmusic

import com.music.simpmusic.models.LyricsData
import com.music.simpmusic.models.SimpMusicApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.math.abs

object SimpMusicLyrics {
    private const val BASE_URL = "https://api-lyrics.simpmusic.org/v1/"
    private const val FALLBACK_URL = "https://vivi-yt-music-server.onrender.com/v1/"

    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 15000
            }

            defaultRequest {
                url(BASE_URL)
                header(HttpHeaders.Accept, "application/json")
                header(HttpHeaders.UserAgent, "SimpMusicLyrics/1.0")
                header(HttpHeaders.ContentType, "application/json")
            }

            expectSuccess = false
        }
    }

    suspend fun getLyricsByVideoId(videoId: String): List<LyricsData> {
        return fetchLyrics(BASE_URL + videoId)
            ?: fetchLyrics(FALLBACK_URL + videoId).orEmpty()
    }

    private suspend fun fetchLyrics(url: String): List<LyricsData>? = runCatching {
        val response = client.get(url)
        if (response.status != HttpStatusCode.OK) return@runCatching null

        val apiResponse = response.body<SimpMusicApiResponse>()
        if (apiResponse.success) apiResponse.data else emptyList()
    }.getOrNull()

    suspend fun getLyrics(
        videoId: String,
        duration: Int = 0,
    ): Result<String> = runCatching {
        val tracks = getLyricsByVideoId(videoId)

        val validTracks = if (duration > 0) {
            tracks.filter { track ->
                abs((track.duration ?: 0) - duration) <= 10
            }
        } else {
            tracks
        }

        if (validTracks.isEmpty()) {
            throw IllegalStateException("Lyrics unavailable")
        }

        val bestMatch = if (duration > 0 && validTracks.size > 1) {
            validTracks.minByOrNull { track ->
                abs((track.duration ?: 0) - duration)
            }
        } else {
            validTracks.firstOrNull()
        }

        // Prioritize richSyncLyrics for word-by-word sync, then syncedLyrics, then plainLyrics
        val lyrics = bestMatch?.richSyncLyrics?.takeIf { it.isNotBlank() }
            ?: bestMatch?.syncedLyrics?.takeIf { it.isNotBlank() }
            ?: bestMatch?.plainLyrics?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Lyrics unavailable")
        
        lyrics
    }

    suspend fun getAllLyrics(
        videoId: String,
        duration: Int = 0,
        callback: (String) -> Unit,
    ) {
        val tracks = getLyricsByVideoId(videoId)
        var resultCount = 0
        var hasEmittedPlainLyrics = false

        val sortedTracks = if (duration > 0) {
            tracks.sortedBy { abs((it.duration ?: 0) - duration) }
        } else {
            tracks
        }

        for (track in sortedTracks) {
            if (resultCount > 4) break

            val durationMatches = duration <= 0 || abs((track.duration ?: 0) - duration) <= 10
            if (!durationMatches) continue

            val syncedLyrics = track.richSyncLyrics?.takeIf(String::isNotBlank)
                ?: track.syncedLyrics?.takeIf(String::isNotBlank)
            if (syncedLyrics != null) {
                resultCount++
                callback(syncedLyrics)
            }

            val plainLyrics = track.plainLyrics?.takeIf(String::isNotBlank)
            if (plainLyrics != null && !hasEmittedPlainLyrics) {
                resultCount++
                hasEmittedPlainLyrics = true
                callback(plainLyrics)
            }
        }
    }
}
