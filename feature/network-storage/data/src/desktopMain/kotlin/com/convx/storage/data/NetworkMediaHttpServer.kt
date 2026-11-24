package com.convx.storage.data

import com.convx.storage.domain.*
import com.convx.storage.playback.ReadWindow
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.io.EOFException
import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.Executors

/** Bridges remote readers to desktop decoders; credentials never enter the local URL. */
class NetworkMediaHttpServer(private val media: NetworkMediaAccess) : AutoCloseable {
    private data class Entry(val uri: String, val mimeType: String)
    private val entries = object : LinkedHashMap<String, Entry>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>?) = size > 64
    }
    private val executor = Executors.newFixedThreadPool(4) { job ->
        Thread(job, "convx-network-media").apply { isDaemon = true }
    }
    private var server: HttpServer? = null
    private var closed = false

    @Synchronized fun createMediaSource(uri: String, mimeType: String): String {
        check(!closed) { "Network playback has been closed" }
        requireNotNull(NetworkMediaUri.parse(uri)) { "Invalid network media identity" }
        val active = server ?: HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).also {
            it.executor = executor
            it.createContext("/network/", ::handle)
            it.start()
            server = it
        }
        val token = UUID.randomUUID().toString()
        entries[token] = Entry(uri, mimeType)
        return "http://127.0.0.1:${active.address.port}/network/$token"
    }

    @Synchronized override fun close() {
        if (closed) return
        closed = true
        entries.clear()
        server?.stop(0)
        server = null
        executor.shutdownNow()
    }

    private fun handle(exchange: HttpExchange) {
        var sent = false
        try {
            val entry = synchronized(this) { entries[exchange.requestURI.rawPath.removePrefix("/network/")] }
            if (entry == null || exchange.requestURI.rawQuery != null) {
                exchange.sendResponseHeaders(404, -1); return
            }
            if (exchange.requestMethod !in setOf("GET", "HEAD")) {
                exchange.responseHeaders.set("Allow", "GET, HEAD")
                exchange.sendResponseHeaders(405, -1); return
            }
            exchange.responseHeaders.set("Content-Type", entry.mimeType)
            exchange.responseHeaders.set("Accept-Ranges", "bytes")
            exchange.responseHeaders.set("Cache-Control", "no-store")
            if (exchange.requestMethod == "HEAD") {
                val total = media.open(entry.uri).use { file ->
                    if (file.length < 0) file.read(0, ByteArray(1), 0, 1)
                    file.length
                }
                if (total >= 0) exchange.responseHeaders.set("Content-Length", total.toString())
                exchange.sendResponseHeaders(200, -1)
                return
            }
            media.open(entry.uri).use { file ->
                // DAV lengths become known once the first range response arrives.
                if (file.length < 0) file.read(0, ByteArray(1), 0, 1)
                val total = file.length
                val requested = exchange.requestHeaders.getFirst("Range")
                val range = requested?.let { parseRange(it, total) }
                if (requested != null && range == null) {
                    if (total >= 0) exchange.responseHeaders.set("Content-Range", "bytes */$total")
                    exchange.sendResponseHeaders(416, -1); return
                }
                val start = range?.first ?: 0
                val length = range?.second ?: -1
                if (total >= 0 && (start > total || requested != null && start == total)) {
                    exchange.responseHeaders.set("Content-Range", "bytes */$total")
                    exchange.sendResponseHeaders(416, -1); return
                }
                val window = ReadWindow(start, length, total)
                if (window.remaining >= 0) exchange.responseHeaders.set("Content-Length", window.remaining.toString())
                if (requested != null) {
                    if (window.remaining < 0) {
                        exchange.sendResponseHeaders(502, -1); return
                    }
                    exchange.responseHeaders.set("Content-Range", "bytes $start-${start + window.remaining - 1}/${if (total < 0) "*" else total}")
                }
                exchange.sendResponseHeaders(if (requested == null) 200 else 206,
                    if (window.remaining == 0L) -1 else window.remaining.coerceAtLeast(0))
                sent = true
                val buffer = ByteArray(64 * 1024)
                while (window.readLength(buffer.size) > 0) {
                    if (Thread.currentThread().isInterrupted) throw InterruptedException()
                    val count = file.read(window.position, buffer, 0, window.readLength(buffer.size))
                    if (count <= 0) {
                        if (window.remaining > 0) throw EOFException("Remote media ended before its advertised length")
                        break
                    }
                    exchange.responseBody.write(buffer, 0, count)
                    window.advance(count)
                }
            }
        } catch (failure: Exception) {
            if (!sent) runCatching {
                val code = if (failure is StorageException && failure.error == StorageError.NOT_FOUND) 404 else 502
                exchange.sendResponseHeaders(code, -1)
            }
        } finally {
            exchange.close()
        }
    }

    private fun parseRange(value: String, total: Long): Pair<Long, Long>? {
        val match = Regex("bytes=(\\d*)-(\\d*)").matchEntire(value) ?: return null
        val first = match.groupValues[1]
        val last = match.groupValues[2]
        if (first.isEmpty()) {
            val suffix = last.toLongOrNull()?.takeIf { it > 0 } ?: return null
            if (total <= 0) return null
            return (total - minOf(total, suffix)) to minOf(total, suffix)
        }
        val start = first.toLongOrNull() ?: return null
        if (last.isEmpty()) return start to -1
        val end = last.toLongOrNull()?.takeIf { it >= start } ?: return null
        val boundedEnd = if (total >= 0) minOf(end, total - 1) else end
        if (boundedEnd < start || boundedEnd - start == Long.MAX_VALUE) return null
        return start to (boundedEnd - start + 1)
    }
}
