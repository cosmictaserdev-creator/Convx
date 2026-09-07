/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Lossless (TIDAL) streaming via the open "hifi-api" family of public proxies.
 * Opt-in, off by default; the caller falls back to YouTube on ANY failure so
 * audio never breaks.
 *
 * Endpoints (all instances share this shape — responses are TIDAL's own JSON
 * wrapped under a top-level "data" key):
 *   - GET /search/?s={query}                    → data.items[] (or data.tracks.items[])
 *   - GET /track/?id={id}&quality={q}           → data.manifest (base64) + data.manifestMimeType
 *
 * For quality=LOSSLESS the manifest is base64 "application/vnd.tidal.bts" JSON:
 *   { "mimeType": "audio/flac", "urls": ["https://…tidal.com/…"] }
 * We return urls[0] — a direct FLAC URL ExoPlayer plays natively. Hi-res (DASH
 * MPD manifest) is not decoded here yet and returns null (caller falls back).
 */

package com.convxy.music.utils.tidal
import timber.log.Timber
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─── Data models (lenient — instances vary) ──────────────────────────────────

@Serializable
data class TidalArtist(
    @SerialName("id")   val id: Long? = null,
    @SerialName("name") val name: String = "",
)

@Serializable
data class TidalAlbum(
    @SerialName("id")    val id: Long? = null,
    @SerialName("title") val title: String? = null,
)

@Serializable
data class TidalTrack(
    @SerialName("id")           val id: Long = 0,
    @SerialName("title")        val title: String = "",
    @SerialName("duration")     val duration: Int? = null,
    @SerialName("artist")       val artist: TidalArtist? = null,
    @SerialName("artists")      val artists: List<TidalArtist> = emptyList(),
    @SerialName("album")        val album: TidalAlbum? = null,
    @SerialName("audioQuality") val audioQuality: String? = null,
) {
    /** Real artist names, preferring the list, falling back to the single artist. */
    val artistNames: List<String>
        get() = when {
            artists.isNotEmpty() -> artists.map { it.name }.filter { it.isNotBlank() }
            artist != null       -> listOfNotNull(artist.name.takeIf { it.isNotBlank() })
            else                 -> emptyList()
        }
}

@Serializable
private data class TidalTracksWrapper(
    @SerialName("items") val items: List<TidalTrack> = emptyList(),
)

@Serializable
private data class TidalSearchData(
    @SerialName("items")  val items: List<TidalTrack> = emptyList(),
    @SerialName("tracks") val tracks: TidalTracksWrapper? = null,
)

@Serializable
private data class TidalSearchResponse(
    @SerialName("data") val data: TidalSearchData? = null,
)

@Serializable
private data class TidalTrackData(
    @SerialName("manifest")         val manifest: String? = null,
    @SerialName("manifestMimeType") val manifestMimeType: String? = null,
    @SerialName("sampleRate")       val sampleRate: Int? = null,
    @SerialName("bitDepth")         val bitDepth: Int? = null,
    @SerialName("audioQuality")     val audioQuality: String? = null,
)

@Serializable
private data class TidalTrackResponse(
    @SerialName("data") val data: TidalTrackData? = null,
)

/** Decoded "bts" manifest — the direct-URL case (LOSSLESS / CD). */
@Serializable
private data class BtsManifest(
    @SerialName("mimeType") val mimeType: String? = null,
    @SerialName("urls")     val urls: List<String> = emptyList(),
)

// ─── Live-instance discovery (uptime worker) ─────────────────────────────────

@Serializable
private data class UptimeTarget(@SerialName("url") val url: String = "")

@Serializable
private data class UptimeResponse(
    @SerialName("api")       val api: List<UptimeTarget> = emptyList(),
    @SerialName("streaming") val streaming: List<UptimeTarget> = emptyList(),
)

// ─── Service ─────────────────────────────────────────────────────────────────

object TidalService {

    private const val TAG = "TidalService"

    /**
     * Last-resort fallback instances (hifi-api scheme). Public instances flap
     * constantly, so the live list is normally discovered at runtime from the
     * uptime workers below; these are only used if that discovery fails.
     */
    val BUNDLED_INSTANCES: List<String> = listOf(
        "https://api.monochrome.tf",
        "https://eu-central.monochrome.tf",
        "https://us-west.monochrome.tf",
        "https://monochrome-api.samidy.com",
    )

    /**
     * Uptime workers that publish the currently-online instances as
     * { "api": [{url}], "streaming": [{url}] }. Fetched at runtime and cached so
     * the feature self-heals as instances go up/down.
     */
    private val UPTIME_WORKERS = listOf(
        "https://tidal-uptime.props-76styles.workers.dev/",
        "https://tidal-uptime.jiffy-puffs-1j.workers.dev/",
    )

    // Browser-ish identity so instances that block bots/datacenter UAs still answer.
    private const val UA = "Mozilla/5.0 (compatible; ViviMusic/1.0)"

    @Volatile private var cachedLive: List<String> = emptyList()
    @Volatile private var cachedAt: Long = 0L
    private const val LIVE_TTL_MS = 10 * 60 * 1000L

    private val json = Json {
        isLenient         = true
        ignoreUnknownKeys = true
        explicitNulls     = false
    }

    private val client by lazy {
        HttpClient(OkHttp) {
            install(ContentNegotiation) { json(json) }
            install(HttpTimeout) {
                // Short — a slow instance must fall back to YouTube without a stall.
                requestTimeoutMillis = 5_000
                connectTimeoutMillis = 3_000
                socketTimeoutMillis  = 5_000
            }
            expectSuccess = false
        }
    }

    /** Currently-online instances from the uptime workers, cached for [LIVE_TTL_MS]. */
    private suspend fun liveInstances(): List<String> {
        val now = System.currentTimeMillis()
        if (now - cachedAt < LIVE_TTL_MS && cachedLive.isNotEmpty()) return cachedLive
        for (worker in UPTIME_WORKERS) {
            val hosts = runCatching {
                val resp = client.get(worker) {
                    headers.append(HttpHeaders.Accept, "application/json")
                    headers.append(HttpHeaders.UserAgent, UA)
                }
                if (resp.status != HttpStatusCode.OK) return@runCatching emptyList<String>()
                val body = resp.body<UptimeResponse>()
                (body.api + body.streaming).map { it.url.trimEnd('/') }.filter { it.startsWith("http") }
            }.getOrElse { emptyList() }
            if (hosts.isNotEmpty()) {
                Timber.tag(TAG).d("live instances (${hosts.size}) via $worker")
                cachedLive = hosts
                cachedAt = now
                return hosts
            }
        }
        return emptyList()
    }

    /** Ordered: user override first, then live-discovered, then bundled fallback. */
    private suspend fun orderedInstances(customBaseUrl: String?): List<String> {
        val custom = customBaseUrl?.trim()?.trimEnd('/')?.takeIf { it.startsWith("http") }
        return (listOfNotNull(custom) + liveInstances() + BUNDLED_INSTANCES).distinct()
    }

    /**
     * Search across instances (first that returns candidates wins). Returns [] on
     * total failure — never throws.
     */
    suspend fun search(query: String, customBaseUrl: String? = null): List<TidalTrack> {
        if (query.isBlank()) return emptyList()
        for (base in orderedInstances(customBaseUrl)) {
            val hits = runCatching {
                val resp = client.get("$base/search/") {
                    parameter("s", query)
                    headers.append(HttpHeaders.Accept, "application/json")
                    headers.append(HttpHeaders.UserAgent, UA)
                    headers.append(HttpHeaders.AcceptEncoding, "identity")
                }
                if (resp.status != HttpStatusCode.OK) return@runCatching emptyList<TidalTrack>()
                val body = resp.body<TidalSearchResponse>()
                val data = body.data ?: return@runCatching emptyList<TidalTrack>()
                data.items.ifEmpty { data.tracks?.items ?: emptyList() }
                    .filter { it.id != 0L && it.title.isNotBlank() }
            }.getOrElse {
                Timber.tag(TAG).d("search failed on $base: ${it.message}")
                emptyList()
            }
            if (hits.isNotEmpty()) {
                Timber.tag(TAG).d("search \"$query\" -> ${hits.size} hits via $base")
                return hits
            }
        }
        return emptyList()
    }

    /**
     * Resolve a direct FLAC stream URL for [trackId] at [quality] (e.g. "LOSSLESS").
     * Only the base64 "bts" (direct-URL) manifest is supported; hi-res DASH → null.
     * Returns null on any failure. Tries the same instance order as [search].
     */
    suspend fun streamUrl(
        trackId: Long,
        quality: String,
        customBaseUrl: String? = null,
    ): String? {
        for (base in orderedInstances(customBaseUrl)) {
            val url = runCatching {
                val resp = client.get("$base/track/") {
                    parameter("id", trackId)
                    parameter("quality", quality)
                    headers.append(HttpHeaders.Accept, "application/json")
                    headers.append(HttpHeaders.UserAgent, UA)
                    headers.append(HttpHeaders.AcceptEncoding, "identity")
                }
                if (resp.status != HttpStatusCode.OK) {
                    Timber.tag(TAG).d("track HTTP ${resp.status.value} on $base id=$trackId body=${resp.bodyAsText().take(200)}")
                    return@runCatching null
                }
                val data = resp.body<TidalTrackResponse>().data
                if (data?.manifest == null) {
                    Timber.tag(TAG).d("track no manifest on $base id=$trackId mime=${data?.manifestMimeType}")
                    return@runCatching null
                }
                decodeManifestUrl(data.manifest, data.manifestMimeType).also {
                    if (it == null) Timber.tag(TAG).d("track manifest not decodable on $base mime=${data.manifestMimeType} head=${data.manifest.take(24)}")
                }
            }.getOrElse {
                Timber.tag(TAG).d("streamUrl failed on $base: ${it.message}")
                null
            }
            if (!url.isNullOrBlank()) {
                Timber.tag(TAG).d("streamUrl track=$trackId quality=$quality resolved via $base")
                return url
            }
        }
        return null
    }

    /**
     * base64 manifest → first direct URL, only for the "bts" (non-DASH) case.
     * Returns null for hi-res DASH/MPD manifests (not decoded yet). `internal` so
     * the unit self-check can exercise it without a live server.
     */
    internal fun decodeManifestUrl(manifest: String?, mimeType: String?): String? {
        if (manifest.isNullOrBlank()) return null
        // Hi-res is DASH/MPD XML — not decoded here yet.
        if (mimeType?.contains("dash", ignoreCase = true) == true) return null
        val decoded = runCatching {
            String(java.util.Base64.getDecoder().decode(manifest), Charsets.UTF_8)
        }.getOrNull() ?: return null
        if (decoded.trimStart().startsWith("<")) return null // MPD XML, not JSON
        return runCatching {
            json.decodeFromString<BtsManifest>(decoded).urls.firstOrNull()
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }
}
