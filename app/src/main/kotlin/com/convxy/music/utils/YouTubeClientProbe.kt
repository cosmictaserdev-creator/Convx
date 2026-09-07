/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.utils

import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

/**
 * Asks every InnerTube client, one at a time, whether it can still produce a playable
 * stream — and says which ones did.
 *
 * Which clients YouTube honours is a moving target: they are fingerprints, and YouTube
 * silently starts rejecting them. When playback breaks for a lot of people at once the
 * question is never "is our fallback chain correct" (it is) but "which of these eleven
 * still work today", and that cannot be answered by reading source. It needs a real
 * request against a real videoId from a real device on a real network — which is also why
 * this lives in the app rather than in a unit test: the answer depends on the user's IP,
 * their login state and their region, and it differs between them.
 *
 * Diagnostic only. Nothing calls this during playback; it is driven from the debug screen
 * so a user reporting "nothing plays" can produce a result worth acting on instead of a
 * guess.
 */
object YouTubeClientProbe {

    /** How each client fared. */
    data class Result(
        val clientName: String,
        val outcome: Outcome,
        val playabilityStatus: String?,
        /** Populated when YouTube explains itself, e.g. bot-detection copy. */
        val reason: String?,
        val formatCount: Int,
        val elapsedMillis: Long,
    ) {
        val line: String
            get() = buildString {
                append(outcome.symbol).append(' ').append(clientName.padEnd(28))
                append(outcome.name.lowercase().padEnd(16))
                append("${elapsedMillis}ms".padStart(7))
                playabilityStatus?.let { append("  ").append(it) }
                if (formatCount > 0) append("  formats=").append(formatCount)
                reason?.takeIf { it.isNotBlank() }?.let { append("  \"").append(it.take(80)).append('"') }
            }
    }

    enum class Outcome(val symbol: String) {
        /** Returned a stream URL that a HEAD request confirmed is actually serving. */
        PLAYABLE("OK "),
        /** Gave a stream URL, but fetching it failed — the usual shape of a stale fingerprint. */
        DEAD_URL("URL"),
        /** Player response came back, but with no usable audio format. */
        NO_FORMAT("FMT"),
        /** YouTube refused: bot detection, login required, age gate, region block. */
        REFUSED("NO "),
        /** Request itself failed or timed out. */
        ERROR("ERR"),
        /** Not attempted — needs a login the user does not have. */
        SKIPPED("-- "),
    }

    /**
     * Probe [clients] against [videoId] in order. Sequential on purpose: eleven parallel
     * player requests from one IP is itself a bot-detection signal, and a probe that
     * changes the thing it measures is worthless.
     */
    suspend fun run(
        videoId: String,
        clients: List<YouTubeClient>,
        isLoggedIn: Boolean,
    ): List<Result> = withContext(Dispatchers.IO) {
        clients.map { client -> probe(videoId, client, isLoggedIn) }
    }

    private suspend fun probe(
        videoId: String,
        client: YouTubeClient,
        isLoggedIn: Boolean,
    ): Result {
        if (client.loginRequired && !isLoggedIn) {
            return Result(client.label, Outcome.SKIPPED, null, "requires login", 0, 0)
        }

        var status: String? = null
        var reason: String? = null
        var formats = 0
        var outcome = Outcome.ERROR

        val elapsed = measureTimeMillis {
            runCatching {
                withTimeoutOrNull(PROBE_TIMEOUT_MILLIS) {
                    val response = YouTube.player(videoId, null, client, null, null).getOrThrow()
                    status = response.playabilityStatus.status
                    reason = response.playabilityStatus.reason
                    val audio = response.streamingData?.adaptiveFormats
                        ?.filter { it.isAudio }
                        .orEmpty()
                    formats = audio.size

                    outcome = when {
                        status != "OK" -> Outcome.REFUSED
                        audio.isEmpty() -> Outcome.NO_FORMAT
                        else -> {
                            val url = audio.firstOrNull { !it.url.isNullOrEmpty() }?.url
                            when {
                                // A signatureCipher-only response is not a failure, but this
                                // probe deliberately does not solve ciphers: it is measuring
                                // which fingerprints YouTube still serves directly.
                                url == null -> Outcome.NO_FORMAT
                                urlIsServing(url) -> Outcome.PLAYABLE
                                else -> Outcome.DEAD_URL
                            }
                        }
                    }
                } ?: run { outcome = Outcome.ERROR; reason = "timed out" }
            }.onFailure { error ->
                outcome = Outcome.ERROR
                reason = error.message?.take(120)
            }
        }

        return Result(client.label, outcome, status, reason, formats, elapsed)
    }

    /**
     * A stream URL can be handed back and still 403 immediately — that is what a rejected
     * fingerprint usually looks like, and it is invisible unless the URL is actually
     * fetched. Range-limited to one byte so this costs nothing.
     */
    private fun urlIsServing(url: String): Boolean = runCatching {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Range", "bytes=0-0")
            connectTimeout = URL_CHECK_TIMEOUT_MILLIS
            readTimeout = URL_CHECK_TIMEOUT_MILLIS
        }
        try {
            connection.responseCode in 200..299
        } finally {
            connection.disconnect()
        }
    }.getOrDefault(false)

    /** Render a run as something a user can paste into a bug report. */
    fun format(videoId: String, results: List<Result>): String = buildString {
        appendLine("Client probe for videoId=$videoId")
        appendLine("=".repeat(72))
        results.forEach { appendLine(it.line) }
        appendLine("=".repeat(72))
        val playable = results.count { it.outcome == Outcome.PLAYABLE }
        appendLine("$playable of ${results.size} clients returned a stream that is actually serving.")
        if (playable == 0) {
            appendLine(
                "None worked. That points at something shared -- network, region or " +
                    "account -- rather than at any one client being stale.",
            )
        }
    }

    private const val PROBE_TIMEOUT_MILLIS = 15_000L
    private const val URL_CHECK_TIMEOUT_MILLIS = 8_000
}

/** Clients carry an optional friendly name; fall back to the wire name so a row is never blank. */
private val YouTubeClient.label: String
    get() = friendlyName ?: clientName
