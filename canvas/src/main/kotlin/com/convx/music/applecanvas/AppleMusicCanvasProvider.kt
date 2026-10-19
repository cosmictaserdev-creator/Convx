/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Ported from cosmictaserdev-creator/convx (https://github.com/cosmictaserdev-creator/Convx), GPL-3.0.

package com.convx.music.applecanvas

import com.convx.music.canvas.CanvasArtwork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private object AppleCanvasLogger {
    fun d(msg: String) = println("AppleMusicCanvas: D: $msg")
    fun w(msg: String) = println("AppleMusicCanvas: W: $msg")
    fun e(t: Throwable, msg: String) {
        println("AppleMusicCanvas: E: $msg")
        t.printStackTrace()
    }
}

/** Finds Apple Motion artwork from album metadata, falling back to music-video tracks. Results are cached for 24 hours. */
object AppleMusicCanvasProvider {

    // Seed token only; getToken refreshes it before expiry.
    private const val APPLE_MUSIC_TOKEN =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IldlYlBsYXlLaWQifQ" +
        ".eyJpc3MiOiJBTVBXZWJQbGF5IiwiaWF0IjoxNzc0NDU2MzgyLCJleHAiOjE3ODE3" +
        "MTM5ODIsInJvb3RfaHR0cHNfb3JpZ2luIjpbImFwcGxlLmNvbSJdfQ" +
        ".4n8qYF4qa18sL1E0G9A3qX35cD8wQ-IJcS9Bh8ZT8JV_yLBtVq46B-9-2ZS3EvWHuw3yK9BYFYAhAdTaDm38vQ"

    private const val ITUNES_SEARCH_URL = "https://itunes.apple.com/search"
    private const val AMP_BASE_URL = "https://amp-api.music.apple.com"
    private val ARTIST_DELIMITERS_REGEX = Regex(
        "(?:\\s*,\\s*|\\s*&\\s*|\\s+×\\s+|\\s+x\\s+|\\bfeat\\.?\\b|\\bft\\.?\\b|\\bfeaturing\\b|\\bwith\\b)",
        RegexOption.IGNORE_CASE,
    )
    private val WHITESPACE_REGEX = Regex("\\s+")
    private val EDITORIAL_VIDEO_KEYS = listOf(
        "motionDetailRaw",
        "motionDetailSquare",
        "motionDetailTall",
        "motionDetailStatic",
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    private val editionWords = listOf("deluxe", "expanded", "remastered", "remix", "version", "edit", "mix", "bonus")

    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
                // iTunes returns text/javascript for JSON responses
                register(ContentType.Text.JavaScript, KotlinxSerializationConverter(json))
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 15_000
                requestTimeoutMillis = 25_000
                socketTimeoutMillis = 25_000
            }
            install(ContentEncoding) {
                gzip()
                deflate()
            }
            install(HttpCache)
            expectSuccess = false
        }
    }

    // Fetch and cache the web player's current token so requests survive token rotation.
    private var cachedToken: String = APPLE_MUSIC_TOKEN
    private var cachedTokenExpEpochSec: Long = jwtExpEpochSeconds(APPLE_MUSIC_TOKEN) ?: 0L
    private val tokenMutex = Mutex()
    private val jwtPattern = Regex("eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}")

    private fun base64UrlDecode(segment: String): String {
        val padded = segment + "=".repeat((4 - segment.length % 4) % 4)
        return String(java.util.Base64.getUrlDecoder().decode(padded))
    }

    private fun jwtExpEpochSeconds(token: String): Long? = runCatching {
        val payload = token.split(".").getOrNull(1) ?: return null
        json.parseToJsonElement(base64UrlDecode(payload)).jsonObject["exp"]?.jsonPrimitive?.long
    }.getOrNull()

    /** The web player signs its catalog-read JWT with this key id — distinguishes it
     * from any other JWT (analytics, etc.) that might also appear on the page. */
    private fun isWebPlayToken(token: String): Boolean = runCatching {
        val header = token.split(".").getOrNull(0) ?: return false
        base64UrlDecode(header).contains("WebPlayKid")
    }.getOrDefault(false)

    private suspend fun getToken(forceRefresh: Boolean = false): String = tokenMutex.withLock {
        val nowSec = System.currentTimeMillis() / 1000
        // Refreshes an hour ahead of actual expiry rather than waiting for it to lapse.
        if (!forceRefresh && cachedTokenExpEpochSec > nowSec + 3600) {
            return@withLock cachedToken
        }

        runCatching {
            val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
            val html = client.get("https://music.apple.com/us/browse") {
                header("User-Agent", userAgent)
            }.bodyAsText()

            // The bundle hash changes between deploys, so find its current path in the page HTML.
            val bundlePath = Regex("<script[^>]+src=\"(/assets/index~[^\"]*\\.js)\"")
                .find(html)?.groupValues?.get(1)

            if (bundlePath == null) {
                AppleCanvasLogger.w("token refresh: couldn't find the main JS bundle's script tag")
                return@runCatching
            }

            val bundleJs = client.get("https://music.apple.com$bundlePath") {
                header("User-Agent", userAgent)
            }.bodyAsText()

            val fresh = jwtPattern.findAll(bundleJs).map { it.value }.firstOrNull { isWebPlayToken(it) }
            val freshExp = fresh?.let { jwtExpEpochSeconds(it) }

            if (fresh != null && freshExp != null && freshExp > nowSec) {
                cachedToken = fresh
                cachedTokenExpEpochSec = freshExp
                AppleCanvasLogger.d("refreshed Apple Music web token, valid until epoch $freshExp")
            } else {
                AppleCanvasLogger.w("token refresh: no valid WebPlayKid JWT found in bundle $bundlePath (len=${bundleJs.length})")
            }
        }.onFailure {
            if (it is CancellationException) throw it
            AppleCanvasLogger.e(it, "token refresh request failed, keeping previous token")
        }

        cachedToken
    }

    /** GET with the live token; on a 401 (token rotated ahead of our cached expiry),
     * forces one refresh and retries once before giving up. */
    private suspend fun authorizedGet(url: String, block: HttpRequestBuilder.() -> Unit): HttpResponse {
        suspend fun attempt(token: String): HttpResponse = client.get(url) {
            header("Authorization", "Bearer $token")
            header("Origin", "https://music.apple.com")
            header("Referer", "https://music.apple.com/")
            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            block()
        }

        val first = attempt(getToken())
        if (first.status != HttpStatusCode.Unauthorized) return first
        AppleCanvasLogger.w("got 401 with cached token, forcing refresh and retrying once")
        return attempt(getToken(forceRefresh = true))
    }

    private data class CacheEntry(
        val value: CanvasArtwork?,
        val expiresAtMs: Long,
    )

    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private const val CACHE_TTL_MS = 1000L * 60 * 60 * 24 // 24 hours
    // Serializes cache-check-then-fetch so concurrent callers (e.g. full
    // player + mini player fetching for the same song at once) await one
    // network call instead of both firing it.
    private val fetchMutex = Mutex()

    suspend fun getByAlbumArtist(
        album: String,
        artist: String,
        storefront: String = "us",
    ): CanvasArtwork? = fetchMutex.withLock {
        AppleCanvasLogger.d("getByAlbumArtist: album='$album', artist='$artist'")
        val key = cacheKey("sa", album, artist, storefront)
        cache[key]?.takeIf { it.expiresAtMs > System.currentTimeMillis() }?.let { return@withLock it.value }

        val result = searchAndFetchMotion(album, artist, album, storefront, "albums")
        cache[key] = CacheEntry(result, System.currentTimeMillis() + CACHE_TTL_MS)
        result
    }

    suspend fun getBySongArtist(
        song: String,
        artist: String,
        album: String? = null,
        storefront: String = "us",
    ): CanvasArtwork? = fetchMutex.withLock {
        val key = cacheKey("song", song, artist, album ?: "", storefront)
        cache[key]?.takeIf { it.expiresAtMs > System.currentTimeMillis() }?.let { return@withLock it.value }

        val result = searchAndFetchMotion(song, artist, album, storefront, "songs")
        cache[key] = CacheEntry(result, System.currentTimeMillis() + CACHE_TTL_MS)
        result
    }

    suspend fun getByAlbumId(
        albumId: String,
        storefront: String = "us",
    ): CanvasArtwork? = fetchMutex.withLock {
        val key = cacheKey("id", albumId, storefront)
        cache[key]?.takeIf { it.expiresAtMs > System.currentTimeMillis() }?.let { return@withLock it.value }

        val result = fetchMotionArtwork(albumId, storefront, null)
        cache[key] = CacheEntry(result, System.currentTimeMillis() + CACHE_TTL_MS)
        result
    }

    /**
     * Searches via AMP API and tries to fetch motion artwork.
     * This is faster than iTunes search + AMP lookup.
     */
    private suspend fun searchAndFetchMotion(
        term: String,
        artist: String,
        album: String?,
        storefront: String,
        type: String, // "albums" or "songs"
    ): CanvasArtwork? {
        return runCatching {
            AppleCanvasLogger.d("searching for $type: $term (album: $album) in $storefront")
            var query = if (term.contains(artist, ignoreCase = true)) term else "$artist $term"
            if (!album.isNullOrBlank() && !query.contains(album, ignoreCase = true)) {
                query = "$query $album"
            }
            val url = "$AMP_BASE_URL/v1/catalog/$storefront/search"
            val response = authorizedGet(url) {
                parameter("term", query)
                parameter("types", type)
                parameter("limit", "10")
                parameter("extend", "editorialVideo")
                parameter("include", "albums")
            }
            if (response.status != HttpStatusCode.OK) {
                AppleCanvasLogger.w("search failed with status ${response.status}")
                return@runCatching null
            }

            val root = response.body<JsonObject>()
            val results = root["results"]?.jsonObject?.get(type)?.jsonObject?.get("data")?.jsonArray ?: return@runCatching null
            
            val scoredResults = results.mapNotNull { item ->
                scoreSearchResult(item.jsonObject, term, artist, album)?.let { it to item }
            }.sortedByDescending { it.first }
            AppleCanvasLogger.d("Found ${scoredResults.size} scored results for term '$term'")
            
            for ((score, item) in scoredResults) {
                if (score < 12) {
                    AppleCanvasLogger.d("skipping result with low score: $score")
                    continue
                }
                val obj = item.jsonObject
                val attributes = obj["attributes"]?.jsonObject ?: continue
                val resultName = attributes["name"]?.jsonPrimitive?.contentOrNull ?: ""
                val resultArtistName = attributes["artistName"]?.jsonPrimitive?.contentOrNull ?: ""

                val type = obj["type"]?.jsonPrimitive?.contentOrNull
                val targetAlbumId = resolveAlbumId(obj, attributes)

                if (targetAlbumId == null || targetAlbumId.startsWith("pl.")) {
                    AppleCanvasLogger.d("skipping null or playlist albumId ($targetAlbumId) for $resultName ($resultArtistName)")
                    continue
                }

                AppleCanvasLogger.d("trying resolve for $targetAlbumId (from ${obj["type"]?.jsonPrimitive?.contentOrNull})")

                val ev = attributes["editorialVideo"]?.jsonObject
                if (ev != null) {
                    val hlsUrl = extractEditorialVideoUrl(ev)
                    if (!hlsUrl.isNullOrBlank()) {
                        val name = attributes["name"]?.jsonPrimitive?.contentOrNull
                        val collName = attributes["collectionName"]?.jsonPrimitive?.contentOrNull
                        val resolvedAlbumName = if (type == "songs") collName else name
                        AppleCanvasLogger.d("Found direct editorialVideo for $name (ID: $targetAlbumId)")
                        return@runCatching CanvasArtwork(name, resultArtistName, targetAlbumId, albumName = resolvedAlbumName, animated = hlsUrl)
                    }
                }

                val fetched = fetchMotionArtwork(
                    albumId = targetAlbumId,
                    storefront = storefront,
                    fallbackArtist = resultArtistName,
                    titleOverride = if (type == "songs") attributes["name"]?.jsonPrimitive?.contentOrNull else null,
                    artistOverride = if (type == "songs") resultArtistName else null
                )
                if (fetched != null) return@runCatching fetched
            }
            AppleCanvasLogger.d("no canvas found in resolution/lookup for $term after ${scoredResults.size} results")
            null
        }.onFailure {
            if (it is CancellationException) throw it
            AppleCanvasLogger.e(it, "error in searchAndFetchMotion for $term")
        }.getOrNull()
    }

    private fun scoreSearchResult(
        result: JsonObject,
        term: String,
        artist: String,
        album: String?,
    ): Int? {
        val attributes = result["attributes"]?.jsonObject ?: return null
        val resultArtist = attributes["artistName"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val name = attributes["name"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val collection = attributes["collectionName"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val nameLower = name.lowercase(Locale.ROOT)
        val collectionLower = collection.lowercase(Locale.ROOT)
        val isBlacklisted = nameLower.contains("playlist") || nameLower.contains("set list") ||
            collectionLower.contains("playlist") || collectionLower.contains("set list") ||
            nameLower.contains("essentials") || collectionLower.contains("essentials") ||
            collectionLower.contains("dj mix") || collectionLower.contains("mixed") ||
            collectionLower.contains("apple music") || collectionLower.contains("today's hits") ||
            nameLower.contains("session") || collectionLower.contains("session")

        if (isBlacklisted) {
            AppleCanvasLogger.d("Skipping blacklisted result: '$name' (Album: '$collection')")
            return null
        }
        if (!artistMatches(artist, resultArtist)) return null

        var score = if (resultArtist.equals(artist, ignoreCase = true)) 10 else 5
        score += when {
            name.equals(term, ignoreCase = true) -> 15
            name.contains(term, ignoreCase = true) || term.contains(name, ignoreCase = true) -> 7
            else -> -10
        }
        for (word in editionWords) {
            val inTerm = term.contains(word, ignoreCase = true)
            val inResult = name.contains(word, ignoreCase = true)
            if (inTerm && inResult) score += 5
            else if (!inTerm && inResult) score -= 3
        }
        if (!album.isNullOrBlank() && collection.isNotBlank()) {
            score += when {
                collection.equals(album, ignoreCase = true) -> 20
                collection.contains(album, ignoreCase = true) || album.contains(collection, ignoreCase = true) -> 10
                else -> 0
            }
        }

        AppleCanvasLogger.d("Result: '$name' by '$resultArtist' (Album: '$collection', ID: ${result["id"]}) -> Score: $score")
        return score
    }

    private fun resolveAlbumId(result: JsonObject, attributes: JsonObject): String? {
        val resultType = result["type"]?.jsonPrimitive?.contentOrNull
        if (resultType == "albums") return result["id"]?.jsonPrimitive?.contentOrNull
        if (resultType != "songs") return null

        val relationships = result["relationships"]?.jsonObject
        val relatedAlbumId = relationships?.get("albums")?.jsonObject?.get("data")?.jsonArray
            ?.firstOrNull()?.jsonObject?.get("id")?.jsonPrimitive?.contentOrNull
        val collectionId = attributes["collectionId"]?.jsonPrimitive?.contentOrNull
        val albumId = relatedAlbumId ?: collectionId ?: albumIdFromUrl(attributes)
        if (albumId == null) {
            val name = attributes["name"]?.jsonPrimitive?.contentOrNull.orEmpty()
            AppleCanvasLogger.d("relationships keys for $name: ${relationships?.keys}")
        }
        return albumId
    }

    private fun albumIdFromUrl(attributes: JsonObject): String? {
        val url = attributes["url"]?.jsonPrimitive?.contentOrNull ?: return null
        val albumPath = url.substringAfter("/album/", "").substringBefore("?")
        val id = albumPath.substringAfterLast("/", "")
        return id.takeIf { it.isNotBlank() && it.all { character -> character.isDigit() } }
    }

    private suspend fun fetchMotionArtwork(
        albumId: String,
        storefront: String,
        fallbackArtist: String?,
        titleOverride: String? = null,
        artistOverride: String? = null,
    ): CanvasArtwork? {
        if (albumId.startsWith("pl.")) {
            AppleCanvasLogger.d("fetchMotionArtwork: ignoring playlist id $albumId")
            return null
        }
        return runCatching {
            AppleCanvasLogger.d("fetching album $albumId")
            val url = "$AMP_BASE_URL/v1/catalog/$storefront/albums/$albumId"
            val response = authorizedGet(url) {
                parameter("extend", "editorialVideo")
                parameter("include", "tracks")
            }
            if (response.status != HttpStatusCode.OK) {
                AppleCanvasLogger.w("album fetch failed for $albumId: ${response.status}")
                return@runCatching null
            }

            val root = response.body<JsonObject>()
            val data = root["data"]?.jsonArray
            if (data.isNullOrEmpty()) return@runCatching null
            
            val albumObj = data.firstOrNull()?.jsonObject ?: return@runCatching null
            val attributes = albumObj["attributes"]?.jsonObject
            val albumName = attributes?.get("name")?.jsonPrimitive?.contentOrNull ?: ""
            val artistName = attributes?.get("artistName")?.jsonPrimitive?.contentOrNull ?: fallbackArtist
            
            val nameLower = albumName.lowercase(Locale.ROOT)
            val isBlacklisted = nameLower.contains("playlist") || nameLower.contains("set list") ||
                    nameLower.contains("essentials") || nameLower.contains("dj mix") ||
                    nameLower.contains("mixed") || nameLower.contains("apple music") ||
                    nameLower.contains("today's hits") || nameLower.contains("session")
            
            if (isBlacklisted) {
                AppleCanvasLogger.d("fetchMotionArtwork: ignoring blacklisted album '$albumName' ($albumId)")
                return@runCatching null
            }

            // Song searches use the override title while keeping the album name on the result.
            val finalTitle = titleOverride ?: albumName
            val finalArtist = artistOverride ?: artistName

            val ev = attributes?.get("editorialVideo")?.jsonObject
            if (ev != null) {
                val url = extractEditorialVideoUrl(ev)
                if (!url.isNullOrBlank()) {
                    AppleCanvasLogger.d("found editorialVideo for $finalTitle (album: $albumName, id: $albumId)")
                    return@runCatching CanvasArtwork(finalTitle, finalArtist, albumId, albumName = albumName, animated = url)
                }
            }

            AppleCanvasLogger.d("no editorialVideo for $albumId (available keys: ${attributes?.keys})")
            null
        }.onFailure {
            if (it is CancellationException) throw it
            AppleCanvasLogger.e(it, "error in fetchMotionArtwork for $albumId")
        }.getOrNull()
    }

    private fun extractEditorialVideoUrl(ev: JsonObject): String? {
        val assets = EDITORIAL_VIDEO_KEYS.mapNotNull { key -> ev[key]?.jsonObject }
        for (asset in assets) {
            val video = asset["video"]?.jsonPrimitive?.contentOrNull
                ?: asset["videoUrl"]?.jsonPrimitive?.contentOrNull
                ?: asset["hlsUrl"]?.jsonPrimitive?.contentOrNull
                ?: asset["url"]?.jsonPrimitive?.contentOrNull

            if (!video.isNullOrBlank()) return video
        }

        AppleCanvasLogger.d("editorialVideo found but no video link in assets: ${ev.keys}")
        return null
    }

    private fun cacheKey(prefix: String, vararg parts: String): String {
        return "$prefix|" + parts.joinToString("|") { it.trim().lowercase(Locale.ROOT) }
    }

    private fun artistMatches(requested: String, returned: String): Boolean {
        val requestedList = requested.split(ARTIST_DELIMITERS_REGEX)
            .map { it.replace(WHITESPACE_REGEX, " ").trim().lowercase(Locale.ROOT) }
            .filter { it.isNotBlank() }
        val returnedList = returned.split(ARTIST_DELIMITERS_REGEX)
            .map { it.replace(WHITESPACE_REGEX, " ").trim().lowercase(Locale.ROOT) }
            .filter { it.isNotBlank() }
        return requestedList.any { req -> returnedList.any { res -> res.contains(req) || req.contains(res) } }
    }
}
