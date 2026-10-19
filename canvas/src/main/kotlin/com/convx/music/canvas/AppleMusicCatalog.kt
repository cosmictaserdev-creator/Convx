package com.convx.music.canvas

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.net.URLDecoder
import java.security.MessageDigest
import java.text.Normalizer
import java.util.Base64
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/** One Apple Music image plus the colours Apple's servers computed for it (0xRRGGBB, null when absent). */
@Serializable
data class AppleArtwork(
    val url: String,
    val width: Int = 0,
    val height: Int = 0,
    val bgColor: Int? = null,
    val textColor1: Int? = null,
    val textColor2: Int? = null,
    val textColor3: Int? = null,
    val textColor4: Int? = null,
) {
    /**
     * Concrete URL for a [px]-wide request. Apple scales server-side, capped at the source size.
     * [png] keeps transparency (logos): the templates hard-code a `bb.jpg` suffix.
     */
    fun sized(px: Int, png: Boolean = false): String {
        val w = if (width > 0) minOf(px, width) else px
        val h = if (width > 0 && height > 0) w * height / width else w
        val concrete = url.replace("{w}", w.toString()).replace("{h}", h.toString()).replace("{f}", if (png) "png" else "jpg")
        return if (png) concrete.replace(Regex("""\.jpg$"""), ".png") else concrete
    }
}

@Serializable
data class AppleArtist(
    val id: String,
    val name: String,
    /** What iOS shows as the artist hero: motion preview frame > vipSquare > profile artwork. */
    val hero: AppleArtwork?,
    /** Artist's uploaded wordmark, white variant preferred. Transparent PNG. */
    val logo: AppleArtwork?,
    /** Artist's chosen button colour (info/play/favourite); null means white. */
    val keyColor: Int?,
    /** Square motion artwork (HLS) that plays in the hero, when the artist has one. */
    val motionVideoUrl: String?,
)

/** Looks up Apple artwork and colors by title and artist, with cached results and bounded concurrency. */
object AppleMusicCatalog {
    private const val AMP = "https://amp-api.music.apple.com"
    private const val OFFICIAL_API = "https://api.music.apple.com"
    private const val MAX_PARALLEL = 4
    private const val HIT_TTL_MS = 7L * 24 * 60 * 60 * 1000
    private const val MISS_TTL_MS = 24L * 60 * 60 * 1000
    private const val STALE_GRACE_MS = 30L * 24 * 60 * 60 * 1000

    /** Set once at app start; null keeps the cache in memory only. */
    @Volatile
    var cacheDir: File? = null

    var storefront: String = "us"

    /** Optional backend token provider; true forces refresh. Keep the signing key off-device. */
    @Volatile
    var developerTokenProvider: (suspend (forceRefresh: Boolean) -> String)? = null

    private val json = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }

    internal val client by lazy {
        HttpClient(OkHttp) {
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }
            install(ContentEncoding) { gzip(); deflate() }
            expectSuccess = false
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gate = Semaphore(MAX_PARALLEL)

    /** Keeps page-critical artwork lookups ahead of Home's lower-priority thumbnail requests. */
    private val pageGate = Semaphore(2)
    private val inFlight = ConcurrentHashMap<String, Deferred<String?>>()

    @Serializable
    private data class Entry(val value: String?, val expiresAt: Long)

    private val memory = object : LinkedHashMap<String, Entry>(256, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>) = size > 2000
    }

    // Public lookups

    /** [priority]: an album page waiting on this, not a list row; uses the page lane. */
    suspend fun album(title: String, artist: String?, priority: Boolean = false): AppleArtwork? =
        cached("album|$storefront|${normalize(title)}|${normalize(artist.orEmpty())}") {
            findAlbum(title, artist, priority)?.let { json.encodeToString(it) }
        }?.let { json.decodeFromString<AppleArtwork>(it) }

    suspend fun song(title: String, artist: String?): AppleArtwork? =
        cached("song|$storefront|${normalize(title)}|${normalize(artist.orEmpty())}") {
            findSong(title, artist)?.let { json.encodeToString(it) }
        }?.let { json.decodeFromString<AppleArtwork>(it) }

    suspend fun artist(name: String, priority: Boolean = true): AppleArtist? =
        cached("artist|$storefront|${normalize(name)}") {
            findArtist(name, priority)?.let { json.encodeToString(it) }
        }?.let { json.decodeFromString<AppleArtist>(it) }

    /** A memory-cache answer: [value] null means Apple was asked and has no match. */
    data class Cached<T>(val value: T?)

    /** Memory-only peek: null when this item has not been looked up this session, so callers can wait for it. */
    fun peekAlbum(title: String, artist: String?): Cached<AppleArtwork>? =
        peek("album|$storefront|${normalize(title)}|${normalize(artist.orEmpty())}")
            ?.let { Cached(it.value?.let { v -> json.decodeFromString<AppleArtwork>(v) }) }

    fun peekSong(title: String, artist: String?): Cached<AppleArtwork>? =
        peek("song|$storefront|${normalize(title)}|${normalize(artist.orEmpty())}")
            ?.let { Cached(it.value?.let { v -> json.decodeFromString<AppleArtwork>(v) }) }

    fun peekArtist(name: String): Cached<AppleArtist>? =
        peek("artist|$storefront|${normalize(name)}")
            ?.let { Cached(it.value?.let { v -> json.decodeFromString<AppleArtist>(v) }) }

    // Matching

    private val bracketed = Regex("""\s*[(\[][^)\]]*(feat|ft\.|with |remaster|version|edit|deluxe|explicit|clean)[^)\]]*[)\]]""")
    private val trailingKind = Regex("""\s+-\s+(single|ep)$""")
    private val marks = Regex("""\p{Mn}+""")
    private val nonWord = Regex("""[^\p{L}\p{N}]+""")

    /** Lowercase, accent-free, punctuation-free; drops "(feat. …)"/"- Single" style decorations. */
    fun normalize(s: String): String {
        val base = s.lowercase(Locale.ROOT).replace(bracketed, "").replace(trailingKind, "").replace("&", " and ")
        return Normalizer.normalize(base, Normalizer.Form.NFD).replace(marks, "").replace(nonWord, " ").trim()
    }

    /** True when either side's normalised artist string contains the other's primary artist. */
    fun artistMatches(expected: String?, candidate: String): Boolean {
        if (expected.isNullOrBlank()) return true
        val e = normalize(expected.split(",", " & ", " x ", " feat").first())
        val c = normalize(candidate)
        val cFirst = normalize(candidate.split(",", " & ").first())
        // Whole-word containment: "Drake" must not match "Drakeo the Ruler".
        return e.isNotEmpty() && (" $c ".contains(" $e ") || (cFirst.isNotEmpty() && " $e ".contains(" $cFirst ")))
    }

    private suspend fun findAlbum(title: String, artist: String?, priority: Boolean = false): AppleArtwork? {
        val term = listOfNotNull(title, artist).joinToString(" ")
        val items = search(term, "albums", priority) ?: return null
        return items.firstOrNull { attrs ->
            normalize(attrs.str("name")) == normalize(title) && artistMatches(artist, attrs.str("artistName"))
        }?.get("artwork")?.let(::artwork)
    }

    private suspend fun findSong(title: String, artist: String?): AppleArtwork? {
        val term = listOfNotNull(title, artist).joinToString(" ")
        val items = search(term, "songs") ?: return null
        return items.firstOrNull { attrs ->
            normalize(attrs.str("name")) == normalize(title) && artistMatches(artist, attrs.str("artistName"))
        }?.get("artwork")?.let(::artwork)
    }

    /** Lightweight lookup for background prefetch; page lookups share this persisted cache. */
    suspend fun artistId(name: String, priority: Boolean = false): String? =
        cached("artist-id|$storefront|${normalize(name)}", hitTtlMs = 30L * 24 * 60 * 60 * 1000) {
            val root = request(
                "/v1/catalog/$storefront/search", "term" to name, "types" to "artists", "limit" to "5",
                priority = priority,
            ) ?: return@cached null
            val data = root.path("results", "artists", "data") as? JsonArray ?: return@cached null
            data.singleOrNull { normalize(it.jsonObject.path("attributes", "name").string()) == normalize(name) }
                ?.jsonObject?.get("id")?.jsonPrimitive?.contentOrNull
        }

    private suspend fun findArtist(name: String, priority: Boolean): AppleArtist? {
        val id = artistId(name, priority) ?: return null
        val detail = request(
            "/v1/catalog/$storefront/artists/$id",
            "extend" to "artwork,editorialArtwork,editorialVideo",
            priority = priority,
        ) ?: return null
        val attrs = (detail["data"] as? JsonArray)?.firstOrNull()?.jsonObject?.get("attributes") as? JsonObject
            ?: return null
        val editorial = attrs["editorialArtwork"] as? JsonObject
        val video = attrs["editorialVideo"] as? JsonObject
        val motionSquare = video?.get("motionArtistSquare1x1") as? JsonObject
        val hero = (motionSquare?.get("previewFrame") as? JsonObject)?.let(::artwork)
            ?: (editorial?.get("vipSquare") as? JsonObject)?.let(::artwork)
            ?: (attrs["artwork"] as? JsonObject)?.let(::artwork)
        val logo = (editorial?.get("musicContentWhiteLogoTrimmed") as? JsonObject)?.let(::artwork)
            ?: (editorial?.get("musicContentColorLogoTrimmed") as? JsonObject)?.let(::artwork)
        log("artist '$name' -> $id hero=${hero != null} logo=${logo != null} keyColor=${findKeyColor(attrs)}")
        return AppleArtist(
            id = id,
            name = attrs.str("name").ifEmpty { name },
            hero = hero,
            logo = logo,
            keyColor = findKeyColor(attrs),
            motionVideoUrl = motionSquare?.get("video")?.jsonPrimitive?.contentOrNull,
        )
    }

    /** Returns Apple's artist button color when a `keyColor` field is present. */
    internal fun findKeyColor(element: JsonElement?): Int? = when (element) {
        is JsonObject -> element["keyColor"]?.let { runCatching { it.jsonPrimitive.contentOrNull }.getOrNull() }
            ?.let(::hexColor)
            ?: element.values.firstNotNullOfOrNull(::findKeyColor)
        is JsonArray -> element.firstNotNullOfOrNull(::findKeyColor)
        else -> null
    }

    internal fun artwork(element: JsonElement): AppleArtwork? {
        val o = element as? JsonObject ?: return null
        val url = o["url"]?.jsonPrimitive?.contentOrNull ?: return null
        return AppleArtwork(
            url = url,
            width = o["width"]?.jsonPrimitive?.intOrNull ?: 0,
            height = o["height"]?.jsonPrimitive?.intOrNull ?: 0,
            bgColor = o.str("bgColor").let(::hexColor),
            textColor1 = o.str("textColor1").let(::hexColor),
            textColor2 = o.str("textColor2").let(::hexColor),
            textColor3 = o.str("textColor3").let(::hexColor),
            textColor4 = o.str("textColor4").let(::hexColor),
        )
    }

    internal fun hexColor(s: String?): Int? =
        s?.removePrefix("#")?.takeIf { it.length == 6 }?.toIntOrNull(16)

    private suspend fun search(term: String, type: String, priority: Boolean = false): List<JsonObject>? {
        val root = request(
            "/v1/catalog/$storefront/search", "term" to term, "types" to type, "limit" to "10",
            priority = priority,
        ) ?: return null
        val data = root.path("results", type, "data") as? JsonArray ?: return emptyList()
        return data.mapNotNull { it.jsonObject["attributes"] as? JsonObject }
    }

    // Transport

    internal suspend fun request(
        path: String,
        vararg params: Pair<String, String>,
        priority: Boolean = false,
    ): JsonObject? = (if (priority) pageGate else gate).withPermit {
        try {
            val provider = developerTokenProvider
            val api = if (provider != null) OFFICIAL_API else AMP
            var token = provider?.invoke(false) ?: AppleMusicToken.get()
            if (token.isEmpty()) throw LookupFailed("no token")
            var response = get(api, path, token, params)
            if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
                log("$path -> ${response.status.value}, refreshing token")
                if (provider == null) AppleMusicToken.invalidate(token)
                token = provider?.invoke(true) ?: AppleMusicToken.get()
                if (token.isEmpty()) throw LookupFailed("no token")
                response = get(api, path, token, params)
            }
            if (response.status == HttpStatusCode.NotFound) return@withPermit null
            if (response.status != HttpStatusCode.OK) throw LookupFailed("$path -> ${response.status.value}")
            json.parseToJsonElement(response.bodyAsText()).jsonObject
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: LookupFailed) {
            throw e
        } catch (e: Exception) {
            throw LookupFailed("$path failed: ${e::class.simpleName}: ${e.message}")
        }
    }

    /** A lookup that could not run (auth, network, server) — distinct from "Apple has no match", so it is never cached. */
    private class LookupFailed(message: String) : Exception(message)

    /** Status only (paths, HTTP codes, match results) — never the token. Shows as System.out in logcat. */
    internal fun log(message: String) = println("AppleCatalog: $message")

    private suspend fun get(api: String, path: String, token: String, params: Array<out Pair<String, String>>): HttpResponse =
        client.get(api + path) {
            header("Authorization", "Bearer $token")
            header("Origin", "https://music.apple.com")
            header("Referer", "https://music.apple.com/")
            header("User-Agent", AppleMusicToken.USER_AGENT)
            params.forEach { (k, v) -> parameter(k, v) }
        }

    // Cache

    private fun peek(key: String): Entry? = synchronized(memory) {
        memory[key]?.takeIf { it.expiresAt > System.currentTimeMillis() }
    }

    internal suspend fun cached(key: String, hitTtlMs: Long = HIT_TTL_MS, load: suspend () -> String?): String? {
        val now = System.currentTimeMillis()
        val previous = synchronized(memory) { memory[key] }
        previous?.takeIf { it.expiresAt > now }?.let { return it.value }
        // LAZY: the body (and its finally-remove) must not run before the map holds it.
        val deferred = inFlight.computeIfAbsent(key) {
            scope.async(start = CoroutineStart.LAZY) {
                try {
                    val saved = readDisk(key)
                    saved?.takeIf { it.expiresAt > System.currentTimeMillis() }?.let { entry ->
                        synchronized(memory) { memory[key] = entry }
                        return@async entry.value
                    }
                    val value = try {
                        load()
                    } catch (e: LookupFailed) {
                        log("not cached: ${e.message}")
                        // Serve a recent successful lookup during outages, without extending its TTL
                        // or treating auth/network failure as a confirmed missing artist.
                        return@async listOfNotNull(previous, saved)
                            .filter { it.value != null && System.currentTimeMillis() - it.expiresAt <= STALE_GRACE_MS }
                            .maxByOrNull { it.expiresAt }?.value
                    }
                    val entry = Entry(value, System.currentTimeMillis() + if (value != null) hitTtlMs else MISS_TTL_MS)
                    synchronized(memory) { memory[key] = entry }
                    writeDisk(key, entry)
                    value
                } finally {
                    inFlight.remove(key)
                }
            }
        }
        return deferred.await()
    }

    private fun fileFor(key: String): File? = cacheDir?.let { dir ->
        val digest = MessageDigest.getInstance("SHA-1").digest(key.toByteArray())
        File(dir, digest.joinToString("") { "%02x".format(it) })
    }

    private fun readDisk(key: String): Entry? = fileFor(key)?.takeIf { it.isFile }?.let { file ->
        runCatching { json.decodeFromString<Entry>(file.readText()) }.getOrNull()
    }

    private fun writeDisk(key: String, entry: Entry) {
        val file = fileFor(key) ?: return
        runCatching {
            file.parentFile?.mkdirs()
            file.writeText(json.encodeToString(entry))
        }
    }

    private fun JsonObject.str(key: String): String = this[key]?.let {
        runCatching { it.jsonPrimitive.contentOrNull }.getOrNull()
    } ?: ""

    private fun JsonElement.path(vararg keys: String): JsonElement? =
        keys.fold(this as JsonElement?) { acc, k -> (acc as? JsonObject)?.get(k) }

    private fun JsonElement?.string(): String =
        runCatching { this?.jsonPrimitive?.contentOrNull }.getOrNull().orEmpty()
}

/** Retrieves and caches Apple Music's web player token. Only `AMPWebPlay` tokens are accepted. */
object AppleMusicToken {
    const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"

    private const val SEED =
        "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IldlYlBsYXlLaWQifQ" +
            ".eyJpc3MiOiJBTVBXZWJQbGF5IiwiaWF0IjoxNzc0NDU2MzgyLCJleHAiOjE3ODE3" +
            "MTM5ODIsInJvb3RfaHR0cHNfb3JpZ2luIjpbImFwcGxlLmNvbSJdfQ" +
            ".4n8qYF4qa18sL1E0G9A3qX35cD8wQ-IJcS9Bh8ZT8JV_yLBtVq46B-9-2ZS3EvWHuw3yK9BYFYAhAdTaDm38vQ"

    // Any JWT shape: the header's key order changes between web player builds ("typ" or "alg" first).
    private val jwt = Regex("""eyJ[\w-]+\.eyJ[\w-]+\.[\w-]+""")
    private val issuerClaim = Regex(""""iss"\s*:\s*"([^"]+)"""")
    private val expirationClaim = Regex(""""exp"\s*:\s*(\d+)""")
    private const val WEB_PLAY_ISSUER = "AMPWebPlay"
    private val envMeta = Regex("""name="desktop-music-app/config/environment"\s+content="([^"]+)"""")
    private val indexJs = Regex("""/assets/index[\w~.-]*\.js""")
    private val mutex = Mutex()

    @Volatile
    private var token: String = SEED
    @Volatile
    private var rejectedToken: String? = null

    /** After a failed scrape every lookup would otherwise re-download the web player; wait instead. */
    private const val SCRAPE_BACKOFF_MS = 10L * 60 * 1000

    @Volatile
    private var nextScrapeAt = 0L

    @Volatile
    private var diskChecked = false

    /** A usable token, or "" when none is available right now (callers skip the network). */
    suspend fun get(): String {
        if (!isExpired(token)) return token
        return mutex.withLock {
            if (!isExpired(token)) return@withLock token
            if (!diskChecked) {
                diskChecked = true
                readSaved()?.let { token = it }
                if (!isExpired(token)) return@withLock token
            }
            if (System.currentTimeMillis() >= nextScrapeAt) {
                val fresh = scrape()
                if (fresh != null) {
                    token = fresh
                    save(fresh)
                } else {
                    nextScrapeAt = System.currentTimeMillis() + SCRAPE_BACKOFF_MS
                }
            }
            token.takeUnless { isExpired(it) }.orEmpty()
        }
    }

    private fun tokenFile() = AppleMusicCatalog.cacheDir?.let { File(it, "webplay.token") }

    private fun readSaved(): String? = runCatching {
        tokenFile()?.takeIf { it.isFile }?.readText()?.trim()?.takeIf { isWebPlayToken(it) && !isExpired(it) }
    }.getOrNull()

    private fun save(value: String) {
        runCatching { tokenFile()?.apply { parentFile?.mkdirs() }?.writeText(value) }
    }

    private fun payload(jwtToken: String): String? = runCatching {
        val part = jwtToken.split('.').getOrNull(1) ?: return@runCatching null
        String(Base64.getUrlDecoder().decode(part.padEnd((part.length + 3) / 4 * 4, '=')))
    }.getOrNull()

    internal fun isWebPlayToken(jwtToken: String): Boolean =
        payload(jwtToken)?.let { issuerClaim.find(it)?.groupValues?.get(1) } == WEB_PLAY_ISSUER

    /** First unexpired web-player token in [text] (a page or script body), if any. */
    internal fun pickWebPlayToken(text: String, nowSeconds: Long = System.currentTimeMillis() / 1000): String? =
        jwt.findAll(text).map { it.value }.firstOrNull {
            it != rejectedToken && isWebPlayToken(it) && !isExpired(it, nowSeconds)
        }

    fun invalidate(rejected: String) {
        rejectedToken = rejected
        if (token == rejected) {
            token = ""
            // Do not restore the same rejected token from disk during renewal.
            diskChecked = true
        }
    }

    /** JWT `exp` check with a day of margin. Unparseable counts as expired. */
    internal fun isExpired(jwtToken: String, nowSeconds: Long = System.currentTimeMillis() / 1000): Boolean {
        val exp = payload(jwtToken)?.let { expirationClaim.find(it)?.groupValues?.get(1)?.toLongOrNull() }
            ?: return true
        return exp - 86_400 <= nowSeconds
    }

    private suspend fun scrape(): String? = runCatching {
        AppleMusicCatalog.log("token expired or rejected, scraping music.apple.com")
        val html = AppleMusicCatalog.client.get("https://music.apple.com/us/browse") {
            header("User-Agent", USER_AGENT)
        }.bodyAsText()
        envMeta.find(html)?.groupValues?.get(1)
            ?.let { pickWebPlayToken(URLDecoder.decode(it, "UTF-8")) }
            ?.let { return@runCatching it }
        // Modern bundle first: the legacy one is ~40% larger and only needed if the modern one
        // stops carrying the token (measured: both downloaded made a cold renewal take ~14s).
        val assets = indexJs.findAll(html).map { it.value }.distinct().sortedBy { "legacy" in it }.toList()
        AppleMusicCatalog.log("no token in meta tag, ${assets.size} index scripts")
        assets.firstNotNullOfOrNull { asset ->
            val js = AppleMusicCatalog.client.get("https://music.apple.com$asset") {
                header("User-Agent", USER_AGENT)
            }.bodyAsText()
            pickWebPlayToken(js)
        }
    }.onFailure {
        if (it is kotlinx.coroutines.CancellationException) throw it
        AppleMusicCatalog.log("token scrape failed: ${it::class.simpleName}: ${it.message}")
    }
        .getOrNull()
        .also { AppleMusicCatalog.log("token scrape ${if (it != null) "found a token" else "found nothing"}") }
}
