/*
 * Spotify Canvas lookup. Ported from the reference app (2026, © Rukamori, GPL-3.0,
 * github.com/rukamori) canvas/SpotifyCanvasProvider.kt.
 */

package com.music.spotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.text.Normalizer
import java.util.Base64
import java.util.Locale
import java.util.UUID

/**
 * Resolves a Spotify Canvas (looping vertical mp4) for a song. Needs a logged-in
 * web-player token: [SpotifyInternalToken.accessToken] + [SpotifyInternalToken.clientId].
 */
object SpotifyCanvas {
    private const val CANVAS_URL = "https://spclient.wg.spotify.com/canvaz-cache/v0/canvases"
    private const val SEARCH_URL = "https://api-partner.spotify.com/pathfinder/v2/query"
    private const val CLIENT_TOKEN_URL = "https://clienttoken.spotify.com/v1/clienttoken"
    private const val SEARCH_OPERATION = "searchDesktop"
    private const val WEB_USER_AGENT =
        "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
    private const val APP_USER_AGENT = "Spotify/9.0.34.593 iOS/18.4 (iPhone15,3)"
    private val trackUriPattern = Regex("spotify:track:[A-Za-z0-9]{22}")
    private val configPattern = Regex("""<script[^>]*id="appServerConfig"[^>]*>([^<]+)</script>""")
    private val json = Json { ignoreUnknownKeys = true }
    private val clientTokenMutex = Mutex()
    private var cachedClientToken: ClientToken? = null

    internal data class ClientAuthorization(
        val token: String,
        val clientVersion: String,
    )

    class RequestException(val statusCode: Int) : IOException("Spotify Canvas request failed: HTTP $statusCode")

    /** Returns the canvas mp4 URL for the best title+primary-artist match, or null. */
    suspend fun getBySongArtist(song: String, artist: String, accessToken: String, clientId: String): String? =
        withContext(Dispatchers.IO) {
            val candidates = searchTracks("$song $artist", accessToken)
                .filter { (_, title, trackArtist) -> matchesSongIdentity(song, artist, title, trackArtist) }
                .map { it.first }
                .distinct()
                .take(10)
            if (candidates.isEmpty()) return@withContext null
            val urls = getCanvases(candidates, accessToken, clientId)
            candidates.firstNotNullOfOrNull { urls[it] }
        }

    /** (uri, title, artists) for search hits. */
    private fun searchTracks(query: String, accessToken: String): List<Triple<String, String, String>> {
        val variables = buildJsonObject {
            put("searchTerm", query)
            put("offset", 0)
            put("limit", 10)
            put("numberOfTopResults", 5)
            put("includeAudiobooks", false)
            put("includeArtistHasConcertsField", false)
            put("includePreReleases", false)
            put("includeLocalConcertsField", false)
            put("includeAuthors", false)
        }
        val hashes = listOfNotNull(
            SpotifyHashProvider.getHash(SEARCH_OPERATION),
            SpotifyHashProvider.getPreviousHash(SEARCH_OPERATION),
        ).distinct()
        for (hash in hashes) {
            val body = buildJsonObject {
                put("variables", variables)
                put("operationName", SEARCH_OPERATION)
                putJsonObject("extensions") {
                    putJsonObject("persistedQuery") {
                        put("version", 1)
                        put("sha256Hash", hash)
                    }
                }
            }
            val (status, bytes) = request(
                url = SEARCH_URL,
                body = body.toString().toByteArray(Charsets.UTF_8),
                headers = mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "App-Platform" to "WebPlayer",
                    "User-Agent" to WEB_USER_AGENT,
                    "Origin" to "https://open.spotify.com",
                    "Referer" to "https://open.spotify.com/",
                    "Accept" to "application/json",
                    "Content-Type" to "application/json; charset=UTF-8",
                ),
            )
            val text = bytes.toString(Charsets.UTF_8)
            // Stale persisted-query hash: try the previous one.
            if (status == 412 || text.contains("PersistedQueryNotFound", ignoreCase = true)) continue
            if (status != 200) throw RequestException(status)
            val root = json.parseToJsonElement(text) as? JsonObject
                ?: throw IOException("Spotify search response is invalid")
            if (root.array("errors").isNotEmpty()) throw IOException("Spotify search returned an error")
            return root.obj("data").obj("searchV2").obj("tracksV2").array("items").mapNotNull { item ->
                val wrapper = (item as? JsonObject).obj("item")
                val track = wrapper.obj("data")
                val uri = wrapper.string("_uri") ?: wrapper.string("uri") ?: track.string("uri")
                    ?: track.string("id")?.let { "spotify:track:$it" }
                    ?: return@mapNotNull null
                if (!trackUriPattern.matches(uri)) return@mapNotNull null
                val title = track.string("name") ?: return@mapNotNull null
                val artists = track.obj("artists").array("items")
                    .mapNotNull { (it as? JsonObject).obj("profile").string("name") }
                    .joinToString(", ")
                Triple(uri, title, artists)
            }
        }
        throw RequestException(412)
    }

    /** trackUri -> canvas mp4 URL. At most 10 URIs per call. */
    suspend fun getCanvases(trackUris: List<String>, accessToken: String, clientId: String): Map<String, String> =
        withContext(Dispatchers.IO) {
            require(trackUris.size <= 10)
            require(trackUris.all { trackUriPattern.matches(it) })
            val (status, bytes) = request(
                url = CANVAS_URL,
                body = encodeRequest(trackUris),
                headers = mapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Client-Token" to clientToken(clientId),
                    "User-Agent" to APP_USER_AGENT,
                    "Accept" to "application/protobuf",
                    "Content-Type" to "application/protobuf",
                    "Accept-Language" to "en",
                ),
            )
            if (status != 200) throw RequestException(status)
            if (bytes.size > 1_048_576) throw IOException("Spotify Canvas response is too large")
            decodeResponse(bytes).filterKeys { it in trackUris }
        }

    internal suspend fun pathfinderAuthorization(clientId: String): ClientAuthorization = clientTokenMutex.withLock {
        require(clientId.isNotBlank())
        val now = System.nanoTime()
        cachedClientToken?.takeIf { it.clientId == clientId && now < it.expiresAtNanos }?.let {
            return@withLock ClientAuthorization(it.value, it.clientVersion)
        }

        val page = URL("https://open.spotify.com/").openConnection() as HttpURLConnection
        val (version, deviceId) = try {
            page.connectTimeout = 12_000
            page.readTimeout = 18_000
            page.setRequestProperty("User-Agent", WEB_USER_AGENT)
            if (page.responseCode != 200) throw RequestException(page.responseCode)
            val html = page.inputStream.bufferedReader().use { it.readText() }
            val encoded = configPattern.find(html)?.groupValues?.get(1)
                ?: throw IOException("Spotify client configuration is missing")
            val config = json.parseToJsonElement(String(Base64.getDecoder().decode(encoded), Charsets.UTF_8)) as? JsonObject
            val version = config.string("clientVersion") ?: throw IOException("Spotify client version is missing")
            val deviceId = page.headerFields.entries
                .filter { it.key.equals("Set-Cookie", ignoreCase = true) }
                .flatMap { it.value }
                .firstNotNullOfOrNull { cookie ->
                    cookie.substringBefore(';').takeIf { it.startsWith("sp_t=") }?.substringAfter('=')
                } ?: UUID.randomUUID().toString()
            version to deviceId
        } finally {
            page.disconnect()
        }

        val payload = buildJsonObject {
            putJsonObject("client_data") {
                put("client_version", version)
                put("client_id", clientId)
                putJsonObject("js_sdk_data") {
                    put("device_brand", "unknown")
                    put("device_model", "unknown")
                    put("os", "android")
                    put("os_version", "14")
                    put("device_id", deviceId)
                    put("device_type", "smartphone")
                }
            }
        }
        val (status, bytes) = request(
            url = CLIENT_TOKEN_URL,
            body = payload.toString().toByteArray(Charsets.UTF_8),
            headers = mapOf(
                "User-Agent" to WEB_USER_AGENT,
                "Accept" to "application/json",
                "Content-Type" to "application/json",
            ),
        )
        if (status != 200) throw RequestException(status)
        val granted = (json.parseToJsonElement(bytes.toString(Charsets.UTF_8)) as? JsonObject).obj("granted_token")
        val value = granted.string("token")?.takeIf(String::isNotBlank)
            ?: throw IOException("Spotify client token was not granted")
        val ttlSeconds = (granted?.get("expires_after_seconds") as? JsonPrimitive)?.longOrNull?.coerceIn(0, 86_400) ?: 0
        cachedClientToken = ClientToken(
            clientId = clientId,
            value = value,
            clientVersion = version,
            expiresAtNanos = now + (ttlSeconds - 30).coerceAtLeast(0) * 1_000_000_000,
        )
        ClientAuthorization(value, version)
    }

    private suspend fun clientToken(clientId: String): String = pathfinderAuthorization(clientId).token

    private fun request(url: String, body: ByteArray, headers: Map<String, String>): Pair<Int, ByteArray> {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 12_000
            connection.readTimeout = 18_000
            connection.doOutput = true
            headers.forEach { (k, v) -> connection.setRequestProperty(k, v) }
            connection.outputStream.use { it.write(body) }
            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            return status to (stream?.use { it.readBytes() } ?: ByteArray(0))
        } finally {
            connection.disconnect()
        }
    }

    // Minimal protobuf: request = repeated Track{1: uri}; response = repeated Canvas{2: url, 5: uri}.

    internal fun encodeRequest(trackUris: List<String>): ByteArray {
        val out = ByteArrayOutputStream()
        for (uri in trackUris) {
            val track = ByteArrayOutputStream().apply { writeLengthDelimited(1, uri.toByteArray(Charsets.UTF_8)) }
            out.writeLengthDelimited(1, track.toByteArray())
        }
        return out.toByteArray()
    }

    internal fun decodeResponse(bytes: ByteArray): Map<String, String> {
        val result = LinkedHashMap<String, String>()
        val outer = ProtoReader(bytes)
        while (!outer.atEnd) {
            val tag = outer.varint().toInt()
            if (tag == 10) {
                val canvas = ProtoReader(outer.bytes())
                var uri: String? = null
                var url: String? = null
                while (!canvas.atEnd) {
                    when (val field = canvas.varint().toInt()) {
                        18 -> url = canvas.bytes().toString(Charsets.UTF_8)
                        42 -> uri = canvas.bytes().toString(Charsets.UTF_8)
                        else -> canvas.skip(field)
                    }
                }
                if (uri != null && url != null && isCanvasUrl(url)) result[uri] = url
            } else {
                outer.skip(tag)
            }
        }
        return result
    }

    private fun ByteArrayOutputStream.writeVarint(value: Long) {
        var v = value
        while (v and 0x7FL.inv() != 0L) {
            write(((v and 0x7F) or 0x80).toInt())
            v = v ushr 7
        }
        write(v.toInt())
    }

    private fun ByteArrayOutputStream.writeLengthDelimited(field: Int, data: ByteArray) {
        writeVarint(((field shl 3) or 2).toLong())
        writeVarint(data.size.toLong())
        write(data)
    }

    private class ProtoReader(private val buf: ByteArray) {
        private var pos = 0
        val atEnd get() = pos >= buf.size

        fun varint(): Long {
            var result = 0L
            var shift = 0
            while (shift < 64) {
                if (pos >= buf.size) throw IOException("Truncated Spotify Canvas response")
                val b = buf[pos++].toInt()
                result = result or ((b and 0x7F).toLong() shl shift)
                if (b and 0x80 == 0) return result
                shift += 7
            }
            throw IOException("Malformed varint in Spotify Canvas response")
        }

        fun bytes(): ByteArray {
            val len = varint().toInt()
            if (len < 0 || pos + len > buf.size) throw IOException("Truncated Spotify Canvas response")
            return buf.copyOfRange(pos, pos + len).also { pos += len }
        }

        fun skip(tag: Int) {
            when (tag and 7) {
                0 -> varint()
                1 -> pos += 8
                2 -> bytes()
                5 -> pos += 4
                else -> throw IOException("Invalid Spotify Canvas field")
            }
        }
    }

    private fun isCanvasUrl(value: String): Boolean = runCatching {
        val uri = URI(value)
        uri.scheme == "https" && uri.host == "canvaz.scdn.co" && uri.path.endsWith(".mp4")
    }.getOrDefault(false)

    internal fun matchesSongIdentity(song: String, artist: String, resolvedSong: String, resolvedArtist: String): Boolean {
        val requestedSong = song.toSongIdentity()
        val requestedArtist = artist.toPrimaryArtistIdentity()
        return requestedSong.isNotEmpty() && requestedArtist.isNotEmpty() &&
            resolvedSong.toSongIdentity() == requestedSong &&
            resolvedArtist.toPrimaryArtistIdentity() == requestedArtist
    }

    private fun String.toSongIdentity() =
        replace(featuringRegex, "").replace(presentationRegex, "").toIdentity()

    private fun String.toPrimaryArtistIdentity() =
        split(artistSeparatorRegex, limit = 2).firstOrNull().orEmpty().toIdentity()

    private fun String.toIdentity() =
        Normalizer.normalize(this, Normalizer.Form.NFKD)
            .replace(combiningMarkRegex, "")
            .lowercase(Locale.ROOT)
            .replace(identitySeparatorRegex, " ")
            .trim()

    private val featuringRegex =
        Regex("""\s*[\[(](?:feat\.?|ft\.?|featuring|with)\s+[^\])]*[\])]""", RegexOption.IGNORE_CASE)
    private val presentationRegex = Regex(
        """\s*(?:[-–—:]\s*)?[\[(]?(?:official\s+)?(?:music\s+)?(?:lyric\s+video|video|audio|lyrics?|visualizer|visualiser)[\])]?\s*$""",
        RegexOption.IGNORE_CASE,
    )
    private val artistSeparatorRegex =
        Regex("""(?:\s*,\s*|\s*&\s*|\s+x\s+|\s+(?:feat\.?|ft\.?|featuring|with)\s+)""", RegexOption.IGNORE_CASE)
    private val combiningMarkRegex = Regex("\\p{M}+")
    private val identitySeparatorRegex = Regex("[^\\p{L}\\p{N}]+")

    private fun JsonObject?.obj(key: String): JsonObject? = this?.get(key) as? JsonObject
    private fun JsonObject?.string(key: String): String? = (this?.get(key) as? JsonPrimitive)?.contentOrNull
    private fun JsonObject?.array(key: String): JsonArray = this?.get(key) as? JsonArray ?: JsonArray(emptyList())

    private data class ClientToken(
        val clientId: String,
        val value: String,
        val clientVersion: String,
        val expiresAtNanos: Long,
    )
}
