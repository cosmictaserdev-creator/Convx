/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.musixmatch

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

/**
 * Independent client against Musixmatch's public web API (apic.musixmatch.com).
 * Not derived from any third-party Musixmatch client implementation.
 */
object MusixmatchClient {
    private const val TAG = "MusixmatchClient"
    private const val BASE_URL = "https://apic.musixmatch.com/ws/1.1"

    // Musixmatch's web client signs every request with an HMAC-SHA256 signature over
    // "<full request url><UTC date as yyyyMMdd>", base64-encoded, and appends it as the
    // `signature`/`signature_protocol` query params. This is a long-published, widely
    // reimplemented convention (many independent open-source Musixmatch clients across
    // languages use the same technique), reimplemented here from that general description.
    private const val SIGNING_SECRET = "RJDefUswhwjkZDeM"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Volatile
    private var client: HttpClient? = null

    fun init(context: Context) {
        if (client != null) return
        synchronized(this) {
            if (client != null) return
            client = HttpClient(CIO) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 15000
                    connectTimeoutMillis = 10000
                }
                install(ContentNegotiation) { json(this@MusixmatchClient.json) }
                defaultRequest {
                    header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                }
                expectSuccess = false
            }
            Timber.tag(TAG).d("Musixmatch client initialized")
        }
    }

    private val httpClient: HttpClient
        get() = client ?: throw IllegalStateException("MusixmatchClient.init() must be called before use")

    private val tokenMutex = Mutex()
    private var cachedToken: String? = null

    /** Signs a full request URL the way Musixmatch's own web client does. */
    private fun sign(urlWithParams: String): String {
        val dateStr = SimpleDateFormat("yyyyMMdd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date())
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SIGNING_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val raw = mac.doFinal("$urlWithParams$dateStr".toByteArray(Charsets.UTF_8))
        val signature = android.util.Base64.encodeToString(raw, android.util.Base64.NO_WRAP)
        return "$urlWithParams&signature=${URLEncoder.encode(signature, "UTF-8")}&signature_protocol=sha256"
    }

    private suspend fun fetchNewToken(): String {
        val response = httpClient.get(sign("$BASE_URL/token.get?app_id=web-desktop-app-v1.0"))
        val body = response.body<MxmEnvelope<MxmTokenBody>>()
        return body.message.body?.userToken
            ?: throw IllegalStateException("Musixmatch token.get failed: ${body.message.header.statusCode}")
    }

    private suspend fun getToken(): String = tokenMutex.withLock {
        cachedToken ?: fetchNewToken().also {
            cachedToken = it
            Timber.tag(TAG).d("Fetched new Musixmatch token")
        }
    }

    private fun clearToken() {
        cachedToken = null
    }

    /** Issues a signed GET built from [buildPath]; on 401/402 clears the token and retries once. */
    private suspend fun signedGet(buildPath: (token: String) -> String): HttpResponse {
        val token = getToken()
        val response = httpClient.get(sign(buildPath(token)))
        if (response.status.value != 401 && response.status.value != 402) return response

        Timber.tag(TAG).w("Musixmatch auth failed (${response.status.value}), retrying with a fresh token")
        clearToken()
        val freshToken = getToken()
        return httpClient.get(sign(buildPath(freshToken)))
    }

    private suspend fun searchTrack(title: String, artist: String): List<MxmTrack> {
        val response = signedGet { token ->
            "$BASE_URL/track.search?app_id=web-desktop-app-v1.0" +
                "&q_track=${URLEncoder.encode(title, "UTF-8")}" +
                "&q_artist=${URLEncoder.encode(artist, "UTF-8")}" +
                "&f_has_lyrics=1&s_track_rating=desc&quorum_factor=1&page_size=10&page=1" +
                "&usertoken=${URLEncoder.encode(token, "UTF-8")}"
        }
        val body = response.body<MxmEnvelope<MxmTrackSearchBody>>()
        return body.message.body?.trackList?.map { it.track } ?: emptyList()
    }

    private fun scoreTrack(track: MxmTrack, title: String, artist: String, duration: Int): Double {
        var score = 0.0
        val trackName = track.trackName.trim().lowercase()
        val targetTitle = title.trim().lowercase()
        score += when {
            trackName == targetTitle -> 80.0
            trackName.contains(targetTitle) || targetTitle.contains(trackName) -> 40.0
            else -> 0.0
        }
        if (track.artistName.trim().lowercase().contains(artist.trim().lowercase())) score += 40.0

        track.trackLength?.let { len ->
            val diff = abs(len - duration)
            score += when {
                diff <= 2 -> 30.0
                diff <= 5 -> 15.0
                diff <= 10 -> 5.0
                else -> -20.0
            }
        }
        return score
    }

    private suspend fun fetchSubtitle(trackId: Long): String? {
        val response = signedGet { token ->
            "$BASE_URL/track.subtitle.get?app_id=web-desktop-app-v1.0" +
                "&track_id=$trackId&subtitle_format=mxm" +
                "&usertoken=${URLEncoder.encode(token, "UTF-8")}"
        }
        return response.body<MxmEnvelope<MxmSubtitleBody>>().message.body?.subtitle?.subtitleBody
    }

    private suspend fun fetchPlainLyrics(trackId: Long): String? {
        val response = signedGet { token ->
            "$BASE_URL/track.lyrics.get?app_id=web-desktop-app-v1.0" +
                "&track_id=$trackId" +
                "&usertoken=${URLEncoder.encode(token, "UTF-8")}"
        }
        return response.body<MxmEnvelope<MxmLyricsBody>>().message.body?.lyrics?.lyricsBody
    }

    /** Converts Musixmatch's `mxm` subtitle JSON (list of {text, time:{total}}) into LRC. */
    private fun subtitleToLrc(subtitleBody: String): String {
        val lines = json.decodeFromString<List<MxmSubtitleLine>>(subtitleBody)
        return buildString {
            for (line in lines) {
                if (line.text.isBlank()) continue
                val totalMs = (line.time.total * 1000).toLong()
                val minutes = totalMs / 1000 / 60
                val seconds = (totalMs / 1000) % 60
                val millis = totalMs % 1000
                appendLine(String.format(Locale.US, "[%02d:%02d.%03d]%s", minutes, seconds, millis, line.text))
            }
        }.trim()
    }

    suspend fun getLyrics(title: String, artist: String, duration: Int, album: String? = null): Result<String> =
        runCatching {
            val tracks = searchTrack(title, artist)
            if (tracks.isEmpty()) throw IllegalStateException("No tracks found on Musixmatch")

            val best = tracks.maxByOrNull { scoreTrack(it, title, artist, duration) }
                ?: throw IllegalStateException("No tracks found on Musixmatch")

            // Synced first, plain as fallback (2-tier; richsync skipped, not worth the added complexity).
            val subtitle = if (best.hasSubtitles == 1) {
                runCatching { fetchSubtitle(best.trackId) }.getOrNull()
            } else null

            val lrc = subtitle?.let { subtitleToLrc(it) }?.takeIf { it.isNotBlank() }
                ?: fetchPlainLyrics(best.trackId)?.trim()?.takeIf { it.isNotBlank() }

            lrc ?: throw IllegalStateException("No lyrics available from Musixmatch")
        }
}
