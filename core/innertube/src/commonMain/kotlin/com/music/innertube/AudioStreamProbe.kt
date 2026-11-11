package com.music.innertube

import com.music.innertube.models.YouTubeClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull

enum class AudioStreamProbeResult { RANGE_READABLE, PROGRESSIVE_READABLE, REFUSED, UNAVAILABLE }

/** Checks real media bytes beyond the initial authorization window without buffering the response. */
internal suspend fun probeAudioStream(client: HttpClient, url: String, contentLength: Long?): AudioStreamProbeResult =
    withTimeoutOrNull(PROBE_TIMEOUT_MS) {
        try {
            val length = contentLength?.takeIf { it > 0 }
            val start = length?.let { minOf(PROBE_OFFSET, (it - PROBE_READ_BYTES).coerceAtLeast(0)) } ?: PROBE_OFFSET
            val end = minOf(start + PROBE_RANGE_BYTES - 1, length?.minus(1) ?: Long.MAX_VALUE)
            client.prepareGet(url) {
                expectSuccess = false
                timeout {
                    requestTimeoutMillis = PROBE_TIMEOUT_MS
                    connectTimeoutMillis = PROBE_TIMEOUT_MS
                    socketTimeoutMillis = PROBE_TIMEOUT_MS
                }
                header(HttpHeaders.UserAgent, YouTubeClient.USER_AGENT_WEB)
                header(HttpHeaders.Origin, YouTubeClient.ORIGIN_YOUTUBE_MUSIC)
                header(HttpHeaders.Referrer, YouTubeClient.REFERER_YOUTUBE_MUSIC)
                header(HttpHeaders.Accept, "*/*")
                header(HttpHeaders.AcceptEncoding, "identity")
                header(HttpHeaders.Range, "bytes=$start-$end")
            }.execute { response ->
                if (response.status.value in setOf(401, 403, 404, 410, 416)) return@execute AudioStreamProbeResult.REFUSED
                if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.PartialContent) {
                    return@execute AudioStreamProbeResult.UNAVAILABLE
                }
                val mime = response.headers[HttpHeaders.ContentType].orEmpty().substringBefore(';').trim().lowercase()
                if (!mime.startsWith("audio/") && mime !in setOf("video/mp4", "video/webm", "application/octet-stream")) {
                    return@execute AudioStreamProbeResult.REFUSED
                }
                val ranged = response.status == HttpStatusCode.PartialContent
                val available = if (ranged) {
                    val range = CONTENT_RANGE.matchEntire(response.headers[HttpHeaders.ContentRange].orEmpty())
                        ?: return@execute AudioStreamProbeResult.REFUSED
                    val first = range.groupValues[1].toLongOrNull() ?: return@execute AudioStreamProbeResult.REFUSED
                    val last = range.groupValues[2].toLongOrNull() ?: return@execute AudioStreamProbeResult.REFUSED
                    val total = range.groupValues[3].toLongOrNull()
                    if (first != start || last < first || last > end || (total != null && total <= last)) {
                        return@execute AudioStreamProbeResult.REFUSED
                    }
                    last - first + 1
                } else response.headers[HttpHeaders.ContentLength]?.toLongOrNull()?.takeIf { it > 0 } ?: length
                val required = minOf(PROBE_READ_BYTES, available ?: PROBE_READ_BYTES).toInt()
                val buffer = ByteArray(required)
                val channel = response.bodyAsChannel()
                var received = 0
                while (received < required) {
                    val count = channel.readAvailable(buffer, received, required - received)
                    if (count < 0) return@execute AudioStreamProbeResult.UNAVAILABLE
                    received += count
                }
                if (ranged) AudioStreamProbeResult.RANGE_READABLE else AudioStreamProbeResult.PROGRESSIVE_READABLE
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            AudioStreamProbeResult.UNAVAILABLE
        }
    } ?: AudioStreamProbeResult.UNAVAILABLE

private const val PROBE_TIMEOUT_MS = 6_000L
private const val PROBE_OFFSET = 1_572_864L
private const val PROBE_RANGE_BYTES = 512 * 1024L
private const val PROBE_READ_BYTES = 16 * 1024L
private val CONTENT_RANGE = Regex("bytes (\\d+)-(\\d+)/(\\d+|\\*)", RegexOption.IGNORE_CASE)
