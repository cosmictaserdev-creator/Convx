package com.convxy.music.vivimusiccanvas

import com.convxy.music.canvas.CanvasArtwork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class EchoMusicCanvasManifest(
    val items: List<EchoMusicCanvasItem> = emptyList()
)

@Serializable
data class EchoMusicCanvasItem(
    val song: String,
    val artist: String,
    val url: String
)

private object EchoCanvasLogger {
    fun d(msg: String) = println("EchoMusicCanvas: D: $msg")
    fun e(t: Throwable, msg: String) {
        println("EchoMusicCanvas: E: $msg")
        t.printStackTrace()
    }
}

/**
 * https://canvas.echomusic.fun/developer — GPL-3.0, community-contributed
 * song+artist -> looping canvas video map, served off GitHub's raw CDN.
 * Docs ask for infrequent polling (they suggest ~30 min), hence the long TTL.
 */
object EchoMusicCanvasProvider {
    private const val BASE_URL = "https://canvas.echomusic.fun/canvas.json"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                connectTimeoutMillis = 12_000
                requestTimeoutMillis = 18_000
                socketTimeoutMillis = 18_000
            }
            install(ContentEncoding) {
                gzip()
                deflate()
            }
            install(HttpCache)
            expectSuccess = false
        }
    }

    private data class CacheEntry(
        val value: EchoMusicCanvasManifest?,
        val expiresAtMs: Long,
    )

    private var manifestCache: CacheEntry? = null
    private val ttlMs = 30 * 60_000L
    // Guards the check-then-fetch below so two callers racing on a cold/expired
    // cache (e.g. the full player and mini player both fetching for the same
    // song at once) await the same network call instead of both firing it.
    private val fetchMutex = Mutex()

    private suspend fun fetchManifest(): EchoMusicCanvasManifest? = fetchMutex.withLock {
        val currentCache = manifestCache
        if (currentCache != null && currentCache.expiresAtMs > System.currentTimeMillis()) {
            return@withLock currentCache.value
        }

        try {
            val response = client.get(BASE_URL)
            val manifest: EchoMusicCanvasManifest = response.body()
            EchoCanvasLogger.d("fetched manifest: status=${response.status} items=${manifest.items.size}")

            manifestCache = CacheEntry(
                value = manifest,
                expiresAtMs = System.currentTimeMillis() + ttlMs
            )
            manifest
        } catch (e: Exception) {
            EchoCanvasLogger.e(e, "fetchManifest failed")
            null
        }
    }

    suspend fun getBySongArtist(
        song: String,
        artist: String,
    ): CanvasArtwork? {
        if (song.isBlank() || artist.isBlank()) return null

        val manifest = fetchManifest()
        if (manifest == null) {
            EchoCanvasLogger.d("getBySongArtist('$song', '$artist'): no manifest (fetch failed)")
            return null
        }

        val target = manifest.items.firstOrNull { item ->
            val matchSong = song.contains(item.song, ignoreCase = true) || item.song.contains(song, ignoreCase = true)
            val matchArtist = artist.contains(item.artist, ignoreCase = true) || item.artist.contains(artist, ignoreCase = true)
            matchSong && matchArtist
        }
        EchoCanvasLogger.d("getBySongArtist('$song', '$artist'): ${manifest.items.size} items, match=${target != null}")

        return target?.let {
            CanvasArtwork(
                name = it.song,
                artist = it.artist,
                videoUrl = it.url,
                animated = it.url
            )
        }
    }
}
