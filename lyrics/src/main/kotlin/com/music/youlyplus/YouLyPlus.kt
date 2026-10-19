package com.music.youlyplus

import com.music.youlyplus.models.LyricsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.select
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicReference

/**
 * YouLyPlus / LyricsPlus KPoe API client.
 *
 * This replicates the multi-server fetch strategy from the YouLyPlus browser
 * extension (ibratabian17/YouLyPlus), querying community-hosted instances of
 * the open-source LyricsPlus backend (ibratabian17/lyricsplus).
 *
 * API endpoint: GET {server}/v2/lyrics/get?title=...&artist=...&duration=...
 */
object YouLyPlus {

    /** Mirror of YouLyPlus extension's KPOE_SERVERS constant. */
    private val BASE_SERVERS = listOf(
        "https://lyricsplus.prjktla.my.id",       // youly's server
        "https://lyricsplus.atomix.one",          // meow's mirror
        "https://lyricsplus.binimum.org",         // binimum's server
        "https://lyricsplus.prjktla.workers.dev", // ibra's cf worker
        "https://lyricsplus-seven.vercel.app",    // jigen's mirror
        "https://lyrics-plus-backend.vercel.app", // ibra's vercel
    )

    /**
     * Remembers the last server that returned a valid result so it is tried
     * first on the next call, giving a fast path on repeated fetches.
     */
    private val lastWorkingServer = AtomicReference<String?>(null)

    /** Returns the server list with the last-working server promoted to front. */
    private val servers: List<String>
        get() {
            val lws = lastWorkingServer.get() ?: return BASE_SERVERS
            return listOf(lws) + BASE_SERVERS.filter { it != lws }
        }

    private val client by lazy {
        HttpClient(OkHttp) {
            install(HttpTimeout) {
                connectTimeoutMillis = 3_000
                requestTimeoutMillis = 8_000
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            expectSuccess = true
        }
    }

    /**
     * Fetch lyrics by racing all servers in parallel.
     * Returns the first non-blank result; records the winning server so future
     * calls skip the slow ones.
     */
    suspend fun getLyrics(
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
        id: String? = null,
        isrc: String? = null,
    ): Result<String> = try {
        Result.success(coroutineScope {
            val pending = servers.map { server ->
                server to async(Dispatchers.IO) {
                    fetchFromServer(server, title, artist, duration, album, id, isrc)
                }
            }.toMutableList()
            while (pending.isNotEmpty()) {
                val (server, response) = select {
                    pending.forEach { (host, request) ->
                        request.onAwait { host to it }
                    }
                }
                pending.removeAll { it.first == server }
                val lyrics = response.bestAvailableLyrics()
                if (!lyrics.isNullOrBlank()) {
                    lastWorkingServer.set(server)
                    return@coroutineScope lyrics
                }
            }
            throw IllegalStateException("No lyrics found from any YouLyPlus server")
        })
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }

    /**
     * Collect all lyrics options across servers; invokes [callback] for each
     * distinct non-blank result. Each server is queried in parallel; callbacks
     * are delivered as results arrive.
     */
    suspend fun getAllLyrics(
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
        id: String? = null,
        isrc: String? = null,
        callback: (String) -> Unit,
    ) {
        coroutineScope {
            val jobs = servers.map { server ->
                async(Dispatchers.IO) {
                    try {
                        fetchFromServer(server, title, artist, duration, album, id, isrc)
                            .bestAvailableLyrics()
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        null
                    }
                }
            }
            val remaining = jobs.toMutableList()
            while (remaining.isNotEmpty()) {
                val lrc = select {
                    remaining.forEach { deferred -> deferred.onAwait { it } }
                }
                remaining.removeAll { it.isCompleted }
                if (!lrc.isNullOrBlank()) callback(lrc)
            }
        }
    }

    private fun LyricsResponse?.bestAvailableLyrics(): String? {
        val wordTimed = this?.lyrics
            ?.takeIf { lines -> lines.any { line -> line.syllabus.orEmpty().any { it.time != null } } }
            ?.convertToLrc()
            ?.takeIf { it.isNotBlank() }
        return wordTimed
            ?: this?.syncedLyrics?.takeIf { it.isNotBlank() }
            ?: this?.lyrics?.convertToLrc()?.takeIf { it.isNotBlank() }
            ?: this?.plainLyrics?.takeIf { it.isNotBlank() }
    }

    /**
     * Converts a list of LyricsItem (with millisecond 'time') to a standard 
     * [mm:ss.xxx]LRC string. Supports word-by-word rich sync if syllables are present.
     */
    private fun List<com.music.youlyplus.models.LyricsItem>.convertToLrc(): String? {
        if (isEmpty()) return null
        return joinToString("\n") { item ->
            val lineTime = item.time ?: 0L
            val isBg = item.syllabus?.any { it.isBackground == true } == true
            val bgMarker = if (isBg) "{bg}" else ""
            val words = item.syllabus.orEmpty().toTimedWords()
            val start = words.firstOrNull()?.startMs?.let { if (lineTime > 0) minOf(lineTime, it) else it } ?: lineTime
            if (words.isEmpty()) return@joinToString formatTime(start) + bgMarker + item.text.orEmpty()

            buildString {
                append(formatTime(start)).append(bgMarker)
                words.forEachIndexed { index, word ->
                    if (index > 0 && (words[index - 1].spaceAfter || word.spaceBefore)) append(' ')
                    append(formatWordTime(word.startMs, word.endMs))
                    append(word.text)
                }
            }
        }
    }

    private data class TimedWord(
        val text: String,
        val startMs: Long,
        val endMs: Long,
        val spaceBefore: Boolean,
        val spaceAfter: Boolean,
    )

    private fun List<com.music.youlyplus.models.Syllable>.toTimedWords(): List<TimedWord> {
        val words = mutableListOf<TimedWord>()
        val text = StringBuilder()
        var start = 0L
        var end = 0L
        var spaceBefore = false
        var spaceAfter = false
        var pendingSpaceBefore = false

        fun flush() {
            if (text.isNotEmpty()) {
                words += TimedWord(text.toString(), start, maxOf(start, end), spaceBefore, spaceAfter)
            }
            text.clear()
            spaceBefore = false
            spaceAfter = false
        }

        forEach { syllable ->
            val raw = syllable.text ?: return@forEach
            val content = raw.trim()
            if (content.isEmpty()) {
                flush()
                if (words.isNotEmpty()) {
                    words[words.lastIndex] = words.last().copy(spaceAfter = true)
                }
                pendingSpaceBefore = true
                return@forEach
            }
            val time = syllable.time ?: return@forEach
            val joins = text.isNotEmpty() && !raw.first().isWhitespace() &&
                !text.last().isUnspacedScript() && !content.first().isUnspacedScript()
            if (!joins) flush()
            if (text.isEmpty()) {
                start = time
                end = time
                spaceBefore = pendingSpaceBefore || raw.first().isWhitespace()
                pendingSpaceBefore = false
            }
            text.append(content)
            end = maxOf(end, time + (syllable.duration ?: 0L))
            spaceAfter = raw.last().isWhitespace()
            if (spaceAfter) flush()
        }
        flush()
        return words
    }

    private fun Char.isUnspacedScript(): Boolean =
        this in '\u3400'..'\u9FFF' || this in '\u3040'..'\u30FF' ||
            this in '\u3000'..'\u303F' || this in '\uFF00'..'\uFFEF' ||
            this in '\u0E00'..'\u0E7F'


    private fun formatTime(timeMs: Long): String = "[${formatTimestamp(timeMs)}]"

    private fun formatWordTime(startMs: Long, endMs: Long): String =
        "<${formatTimestamp(startMs)}|${formatTimestamp(endMs)}>"

    private fun formatTimestamp(timeMs: Long): String {
        val minutes = (timeMs / 1000) / 60
        val seconds = (timeMs / 1000) % 60
        val millis = timeMs % 1000
        return "%02d:%02d.%03d".format(minutes, seconds, millis)
    }


    private suspend fun fetchFromServer(
        baseUrl: String,
        title: String,
        artist: String,
        duration: Int,
        album: String? = null,
        id: String? = null,
        isrc: String? = null,
    ): LyricsResponse? = try {
        client.get(baseUrl.let { if (it.endsWith("/")) it else "$it/" } + "v2/lyrics/get") {
            parameter("title", title)
            parameter("artist", artist)
            parameter("duration", duration)
            if (album != null) parameter("album", album)
            if (id != null) parameter("id", id)
            if (isrc != null) parameter("isrc", isrc)
        }.body<LyricsResponse>()
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        null
    }
}
