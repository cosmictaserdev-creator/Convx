package com.convx.music.client.desktop

import com.music.innertube.models.YouTubeClient
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.UUID

/** Adapts YouTube's bounded range requests to the media engine's full-resource HTTP reads. */
internal class DesktopAudioHttpProxy : AutoCloseable {
    private val streams = object : LinkedHashMap<String, String>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean =
            size > MAX_CACHED_STREAMS
    }

    private var server: HttpServer? = null
    private var executor: ExecutorService? = null

    @Synchronized
    fun createMediaSource(streamUrl: String, mimeType: String): String {
        val httpServer = server ?: startServer().also { server = it }
        val token = UUID.randomUUID().toString()
        synchronized(streams) { streams[token] = streamUrl }
        val extension = when {
            mimeType.startsWith("audio/mp4", ignoreCase = true) -> "m4a"
            mimeType.startsWith("audio/webm", ignoreCase = true) -> "webm"
            mimeType.startsWith("video/mp4", ignoreCase = true) -> "mp4"
            else -> "audio"
        }
        return "http://127.0.0.1:${httpServer.address.port}/stream/$token.$extension"
    }

    @Synchronized
    override fun close() {
        server?.stop(0)
        server = null
        executor?.shutdownNow()
        executor = null
        synchronized(streams) { streams.clear() }
    }

    private fun startServer(): HttpServer {
        val httpServer = HttpServer.create(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0)
        val requestExecutor = Executors.newCachedThreadPool { task ->
            Thread(task, "convx-desktop-audio-proxy").apply { isDaemon = true }
        }
        executor = requestExecutor
        httpServer.executor = requestExecutor
        httpServer.createContext("/stream/") { exchange -> handleRequest(exchange) }
        httpServer.start()
        return httpServer
    }

    private fun handleRequest(exchange: HttpExchange) {
        if (exchange.requestMethod != "GET" && exchange.requestMethod != "HEAD") {
            exchange.responseHeaders.set("Allow", "GET, HEAD")
            exchange.sendResponseHeaders(405, -1)
            exchange.close()
            return
        }

        val token = exchange.requestURI.path.substringAfterLast('/').substringBeforeLast('.')
        val sourceUrl = synchronized(streams) { streams[token] }
        if (sourceUrl == null) {
            exchange.sendResponseHeaders(404, -1)
            exchange.close()
            return
        }

        var headersSent = false
        try {
            if (exchange.requestMethod == "HEAD") {
                writeHeadResponse(exchange, sourceUrl) { headersSent = true }
            } else {
                val range = exchange.requestHeaders.getFirst("Range")
                if (range == null) {
                    streamWholeResource(exchange, sourceUrl) { headersSent = true }
                } else {
                    forwardRange(exchange, sourceUrl, range) { headersSent = true }
                }
            }
        } catch (error: Exception) {
            System.err.println(
                "Desktop audio proxy ${exchange.requestMethod} failed " +
                    "(range=${exchange.requestHeaders.getFirst("Range") ?: "full"}, headersSent=$headersSent): " +
                    "${error::class.simpleName}: ${error.message ?: "no details"}",
            )
            if (!headersSent) {
                runCatching { exchange.sendResponseHeaders(502, -1) }
            }
        } finally {
            exchange.close()
        }
    }

    private fun writeHeadResponse(exchange: HttpExchange, sourceUrl: String, onHeadersSent: () -> Unit) {
        val connection = openRange(sourceUrl, "bytes=0-$METADATA_RANGE_END")
        try {
            val status = connection.responseCode
            val contentRange = connection.getHeaderField("Content-Range")?.let(::parseContentRange)
            copyMetadata(connection, exchange)
            if (status >= 400) {
                exchange.sendResponseHeaders(status, -1)
                onHeadersSent()
                return
            }
            val totalLength = contentRange?.totalLength
                ?: connection.getHeaderFieldLong("Content-Length", -1L)
            if (totalLength >= 0L) exchange.responseHeaders.set("Content-Length", totalLength.toString())
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1)
            onHeadersSent()
        } finally {
            connection.disconnect()
        }
    }

    private fun forwardRange(exchange: HttpExchange, sourceUrl: String, requestedRange: String, onHeadersSent: () -> Unit) {
        val range = parseRequestedRange(requestedRange)
        if (range == null) {
            exchange.sendResponseHeaders(416, -1)
            onHeadersSent()
            return
        }
        if (range.end == null) {
            forwardOpenEndedRange(exchange, sourceUrl, range.start, onHeadersSent)
            return
        }
        var retries = 0
        while (true) {
            val connection = openRange(sourceUrl, range.toHeaderValue())
            try {
                exchange.requestHeaders.getFirst("If-Range")?.let { connection.setRequestProperty("If-Range", it) }
                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_OK) {
                    connection.disconnect()
                    streamWholeResource(exchange, sourceUrl, onHeadersSent)
                    return
                }
                if (status >= 400) {
                    if (status in RETRYABLE_HTTP_STATUSES && retries < MAX_RANGE_RETRIES) {
                        retries += 1
                        delayBeforeRetry(retries)
                        continue
                    }
                    copyMetadata(connection, exchange)
                    connection.getHeaderField("Content-Range")?.let { exchange.responseHeaders.set("Content-Range", it) }
                    exchange.sendResponseHeaders(
                        status,
                        responseLength(status, connection.getHeaderFieldLong("Content-Length", -1L)),
                    )
                    onHeadersSent()
                    responseBody(connection, status)?.use { input ->
                        exchange.responseBody.use { output -> input.copyTo(output) }
                    }
                    return
                }
                if (status != HttpURLConnection.HTTP_PARTIAL) {
                    throw IOException("Audio server returned unsupported HTTP $status for a byte range.")
                }
                val contentRange = connection.getHeaderField("Content-Range")?.let(::parseContentRange)
                    ?: throw EOFException("Audio server omitted its content range.")
                if (contentRange.start != range.start || contentRange.end < range.start || contentRange.totalLength <= contentRange.end) {
                    throw EOFException("Audio server returned an invalid content range.")
                }
                if (range.start >= contentRange.totalLength) {
                    exchange.responseHeaders.set("Content-Range", "bytes */${contentRange.totalLength}")
                    exchange.sendResponseHeaders(416, -1)
                    onHeadersSent()
                    return
                }
                val lastByte = minOf(range.end, contentRange.end, contentRange.totalLength - 1L)
                val expectedBytes = lastByte - range.start + 1L
                if (expectedBytes <= 0L) throw EOFException("Audio server returned an empty range.")
                val chunk = connection.inputStream.use { input -> readExactly(input, expectedBytes) }
                copyMetadata(connection, exchange)
                exchange.responseHeaders.set("Content-Range", "bytes ${range.start}-$lastByte/${contentRange.totalLength}")
                exchange.responseHeaders.set("Content-Length", expectedBytes.toString())
                exchange.responseHeaders.set("Accept-Ranges", "bytes")
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_PARTIAL, expectedBytes)
                onHeadersSent()
                exchange.responseBody.write(chunk)
                exchange.responseBody.flush()
                return
            } catch (error: IOException) {
                if (retries >= MAX_RANGE_RETRIES) throw error
                retries += 1
                delayBeforeRetry(retries)
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun forwardOpenEndedRange(
        exchange: HttpExchange,
        sourceUrl: String,
        start: Long,
        onHeadersSent: () -> Unit,
    ) = streamResource(exchange, sourceUrl, start, rangeRequested = true, onHeadersSent)

    /** Fetches bounded upstream ranges because Googlevideo rejects unbounded GETs from desktop media stacks. */
    private fun streamWholeResource(exchange: HttpExchange, sourceUrl: String, onHeadersSent: () -> Unit) =
        streamResource(exchange, sourceUrl, start = 0L, rangeRequested = false, onHeadersSent)

    /** Streams validated chunks and resumes an interrupted upstream response at the last byte sent. */
    private fun streamResource(
        exchange: HttpExchange,
        sourceUrl: String,
        start: Long,
        rangeRequested: Boolean,
        onHeadersSent: () -> Unit,
    ) {
        var offset = start
        var totalLength: Long? = null
        var headersSent = false
        var retriesAtOffset = 0

        while (totalLength == null || offset < totalLength) {
            val knownLength = totalLength
            val requestedEnd = (offset + CHUNK_SIZE_BYTES - 1L).let { end ->
                knownLength?.let { minOf(end, it - 1L) } ?: end
            }
            val connection = openRange(sourceUrl, "bytes=$offset-$requestedEnd")
            try {
                val status = connection.responseCode
                if (status >= 400) {
                    if (status in RETRYABLE_HTTP_STATUSES && retriesAtOffset < MAX_RANGE_RETRIES) {
                        retriesAtOffset += 1
                        delayBeforeRetry(retriesAtOffset)
                        continue
                    }
                    if (!headersSent) {
                        copyMetadata(connection, exchange)
                        exchange.sendResponseHeaders(
                            status,
                            responseLength(status, connection.getHeaderFieldLong("Content-Length", -1L)),
                        )
                        headersSent = true
                        onHeadersSent()
                        responseBody(connection, status)?.use { input ->
                            exchange.responseBody.use { output -> input.copyTo(output) }
                        }
                        return
                    }
                    throw IllegalStateException(
                        "Audio stream range request failed with HTTP $status at byte $offset " +
                            "of ${totalLength ?: "an unknown length"}.",
                    )
                }

                if (status == HttpURLConnection.HTTP_PARTIAL) {
                    val contentRange = connection.getHeaderField("Content-Range")?.let(::parseContentRange)
                        ?: throw EOFException("Audio server omitted its content range.")
                    if (contentRange.start != offset || contentRange.end < offset || contentRange.totalLength <= contentRange.end) {
                        throw EOFException("Audio server returned an invalid content range.")
                    }
                    if (totalLength == null) {
                        totalLength = contentRange.totalLength
                        if (contentRange.totalLength <= start) throw EOFException("Audio server omitted the stream length.")
                    } else if (contentRange.totalLength != totalLength) {
                        throw EOFException("Audio stream length changed while reading.")
                    }
                    val resolvedLength = requireNotNull(totalLength)
                    if (!headersSent) {
                        copyMetadata(connection, exchange)
                        if (rangeRequested) {
                            exchange.responseHeaders.set("Content-Range", "bytes $start-${resolvedLength - 1L}/$resolvedLength")
                            exchange.responseHeaders.set("Content-Length", (resolvedLength - start).toString())
                            exchange.responseHeaders.set("Accept-Ranges", "bytes")
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_PARTIAL, resolvedLength - start)
                        } else {
                            exchange.responseHeaders.set("Content-Length", resolvedLength.toString())
                            exchange.responseHeaders.set("Accept-Ranges", "bytes")
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, resolvedLength)
                        }
                        headersSent = true
                        onHeadersSent()
                    }

                    val lastByte = minOf(contentRange.end, requestedEnd, resolvedLength - 1L)
                    val expectedBytes = lastByte - offset + 1L
                    if (expectedBytes <= 0L) throw EOFException("Audio server returned an empty range.")
                    val chunk = connection.inputStream.use { input -> readExactly(input, expectedBytes) }
                    exchange.responseBody.write(chunk)
                    exchange.responseBody.flush()
                    offset += expectedBytes
                    retriesAtOffset = 0
                } else if (status == HttpURLConnection.HTTP_OK) {
                    val contentLength = connection.getHeaderFieldLong("Content-Length", -1L)
                    if (contentLength < 0L) {
                        if (offset != start || totalLength != null) {
                            throw EOFException("Audio server returned an unbounded response during resume.")
                        }
                        copyMetadata(connection, exchange)
                        exchange.responseHeaders.remove("Accept-Ranges")
                        exchange.responseHeaders.remove("Content-Range")
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0L)
                        headersSent = true
                        onHeadersSent()
                        connection.inputStream.use { input ->
                            exchange.responseBody.use { output -> input.copyTo(output) }
                        }
                        return
                    }
                    if (contentLength <= offset) throw EOFException("Audio server returned a response shorter than the requested offset.")
                    if (totalLength != null && contentLength != totalLength) {
                        throw EOFException("Audio stream length changed while reading.")
                    }
                    totalLength = contentLength
                    if (!headersSent) {
                        copyMetadata(connection, exchange)
                        if (rangeRequested) {
                            exchange.responseHeaders.set("Content-Range", "bytes $start-${contentLength - 1L}/$contentLength")
                            exchange.responseHeaders.set("Content-Length", (contentLength - start).toString())
                            exchange.responseHeaders.set("Accept-Ranges", "bytes")
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_PARTIAL, contentLength - start)
                        } else {
                            exchange.responseHeaders.set("Content-Length", contentLength.toString())
                            exchange.responseHeaders.set("Accept-Ranges", "bytes")
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength)
                        }
                        headersSent = true
                        onHeadersSent()
                    }

                    connection.inputStream.use { input ->
                        if (offset > 0L) skipExactly(input, offset)
                        copyExactlyTracking(input, exchange.responseBody, contentLength - offset) { copied -> offset += copied }
                    }
                    retriesAtOffset = 0
                } else {
                    throw EOFException("Audio server returned unsupported HTTP $status.")
                }
            } catch (error: IOException) {
                if (retriesAtOffset >= MAX_RANGE_RETRIES) {
                    System.err.println(
                        "Desktop audio proxy exhausted range retries at byte $offset/$totalLength: " +
                            "${error::class.simpleName}: ${error.message ?: "no details"}",
                    )
                    throw error
                }
                retriesAtOffset += 1
                System.err.println(
                    "Desktop audio proxy retrying byte $offset/$totalLength " +
                        "(attempt ${retriesAtOffset + 1} of ${MAX_RANGE_RETRIES + 1}): " +
                        "${error::class.simpleName}: ${error.message ?: "no details"}",
                )
                delayBeforeRetry(retriesAtOffset)
            } finally {
                connection.disconnect()
            }
        }
        exchange.responseBody.flush()
    }

    private fun openRange(sourceUrl: String, range: String): HttpURLConnection =
        (URI.create(sourceUrl).toURL().openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECTION_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", YouTubeClient.USER_AGENT_WEB)
            setRequestProperty("Referer", MUSIC_REFERER)
            setRequestProperty("Origin", MUSIC_ORIGIN)
            setRequestProperty("Accept", "*/*")
            setRequestProperty("Accept-Encoding", "identity")
            setRequestProperty("Range", range)
        }

    private fun copyMetadata(connection: HttpURLConnection, exchange: HttpExchange) {
        listOf("Content-Type", "Accept-Ranges", "Cache-Control", "ETag", "Last-Modified").forEach { name ->
            connection.getHeaderField(name)?.let { exchange.responseHeaders.set(name, it) }
        }
    }

    private fun responseBody(connection: HttpURLConnection, status: Int): InputStream? = when {
        status == HttpURLConnection.HTTP_NO_CONTENT || status == HttpURLConnection.HTTP_NOT_MODIFIED -> null
        status >= 400 -> connection.errorStream
        else -> connection.inputStream
    }

    private fun responseLength(status: Int, contentLength: Long, contentRangeHeader: String? = null): Long = when {
        status == HttpURLConnection.HTTP_NO_CONTENT || status == HttpURLConnection.HTTP_NOT_MODIFIED -> -1L
        contentLength >= 0L -> contentLength
        contentRangeHeader != null -> contentRangeHeader.let(::parseContentRange)?.let { it.end - it.start + 1L } ?: 0L
        else -> 0L
    }

    private fun parseRequestedRange(value: String): RequestedRange? {
        val match = REQUESTED_RANGE_PATTERN.matchEntire(value.trim()) ?: return null
        val start = match.groupValues[1].toLongOrNull() ?: return null
        if (start < 0L) return null
        val requestedEnd = match.groupValues[2].takeIf(String::isNotEmpty)?.toLongOrNull()
        if (requestedEnd != null && requestedEnd < start) return null
        val end = requestedEnd?.let { minOf(it, start + CHUNK_SIZE_BYTES - 1L) }
        return RequestedRange(start, end)
    }

    private data class RequestedRange(val start: Long, val end: Long?) {
        fun toHeaderValue(): String = "bytes=$start-${end ?: ""}"
    }

    private fun readExactly(input: InputStream, expectedBytes: Long): ByteArray {
        require(expectedBytes in 1L..CHUNK_SIZE_BYTES.toLong())
        val bytes = ByteArray(expectedBytes.toInt())
        var offset = 0
        while (offset < bytes.size) {
            val count = input.read(bytes, offset, bytes.size - offset)
            if (count < 0) throw EOFException("Audio stream ended before its advertised range.")
            offset += count
        }
        return bytes
    }

    private fun skipExactly(input: InputStream, expectedBytes: Long) {
        var remaining = expectedBytes
        val scratch = ByteArray(COPY_BUFFER_BYTES)
        while (remaining > 0L) {
            val count = input.read(scratch, 0, minOf(scratch.size.toLong(), remaining).toInt())
            if (count < 0) throw EOFException("Audio stream ended before the requested offset.")
            remaining -= count
        }
    }

    private fun copyExactlyTracking(
        input: InputStream,
        output: java.io.OutputStream,
        expectedBytes: Long,
        onBytesCopied: (Long) -> Unit,
    ) {
        val buffer = ByteArray(COPY_BUFFER_BYTES)
        var remaining = expectedBytes
        while (remaining > 0L) {
            val count = input.read(buffer, 0, minOf(buffer.size.toLong(), remaining).toInt())
            if (count < 0) throw EOFException("Audio stream ended before its advertised range.")
            output.write(buffer, 0, count)
            onBytesCopied(count.toLong())
            remaining -= count
        }
        output.flush()
    }

    private fun delayBeforeRetry(retry: Int) {
        try {
            Thread.sleep(RETRY_DELAY_MS * retry)
        } catch (interrupted: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Audio stream fetch was interrupted.", interrupted)
        }
    }

    private fun parseContentRange(value: String): ContentRange? {
        val match = CONTENT_RANGE_PATTERN.matchEntire(value.trim()) ?: return null
        return ContentRange(
            start = match.groupValues[1].toLongOrNull() ?: return null,
            end = match.groupValues[2].toLongOrNull() ?: return null,
            totalLength = match.groupValues[3].toLongOrNull() ?: return null,
        )
    }

    private data class ContentRange(val start: Long, val end: Long, val totalLength: Long)

    private companion object {
        const val MAX_CACHED_STREAMS = 64
        const val CHUNK_SIZE_BYTES = 512 * 1024
        const val COPY_BUFFER_BYTES = 64 * 1024
        const val MAX_RANGE_RETRIES = 3
        const val RETRY_DELAY_MS = 150L
        const val METADATA_RANGE_END = 1_023
        const val CONNECTION_TIMEOUT_MS = 20_000
        const val READ_TIMEOUT_MS = 60_000
        const val MUSIC_REFERER = "https://music.youtube.com/"
        const val MUSIC_ORIGIN = "https://music.youtube.com"
        val CONTENT_RANGE_PATTERN = Regex("bytes (\\d+)-(\\d+)/(\\d+)", RegexOption.IGNORE_CASE)
        val REQUESTED_RANGE_PATTERN = Regex("bytes=(\\d+)-(\\d*)", RegexOption.IGNORE_CASE)
        val RETRYABLE_HTTP_STATUSES = setOf(408, 429, 500, 502, 503, 504)
    }
}
