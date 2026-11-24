package com.music.innertube

import com.sun.net.httpserver.HttpServer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AudioStreamProbeTest {
    @Test fun probesBeyondTheInitialMegabyteAndReadsMediaBytes() = runBlocking {
        serve { server, client ->
            server.createContext("/audio") { exchange ->
                assertEquals("bytes=1572864-2097151", exchange.requestHeaders.getFirst("Range"))
                exchange.responseHeaders.set("Content-Type", "audio/mp4")
                exchange.responseHeaders.set("Content-Range", "bytes 1572864-2097151/4194304")
                exchange.sendResponseHeaders(206, 524288)
                try { exchange.responseBody.use { it.write(ByteArray(524288) { 7 }) } }
                catch (_: Exception) { exchange.close() }
            }
            assertEquals(AudioStreamProbeResult.RANGE_READABLE, probeAudioStream(client, server.url("audio"), 4194304))
        }
    }

    @Test fun successfulHeadersWithoutEnoughBodyBytesAreNotPlayable() = runBlocking {
        serve { server, client ->
            server.createContext("/truncated") { exchange ->
                exchange.responseHeaders.set("Content-Type", "audio/mp4")
                exchange.responseHeaders.set("Content-Range", "bytes 1572864-2097151/4194304")
                exchange.sendResponseHeaders(206, 0)
                exchange.responseBody.use { it.write(byteArrayOf(1)) }
            }
            assertEquals(AudioStreamProbeResult.UNAVAILABLE, probeAudioStream(client, server.url("truncated"), 4194304))
        }
    }

    @Test fun refusedUrlsHtmlPagesAndIncorrectRangesAreRejected() = runBlocking {
        serve { server, client ->
            server.createContext("/refused") { exchange -> exchange.sendResponseHeaders(403, -1); exchange.close() }
            server.createContext("/html") { exchange ->
                exchange.responseHeaders.set("Content-Type", "text/html")
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.use { it.write("<html>Sign in</html>".toByteArray()) }
            }
            server.createContext("/wrong-range") { exchange ->
                exchange.responseHeaders.set("Content-Type", "audio/webm")
                exchange.responseHeaders.set("Content-Range", "bytes 0-1023/4194304")
                exchange.sendResponseHeaders(206, 1024)
                try { exchange.responseBody.use { it.write(ByteArray(1024)) } }
                catch (_: Exception) { exchange.close() }
            }
            for (path in listOf("refused", "html", "wrong-range")) {
                assertEquals(AudioStreamProbeResult.REFUSED, probeAudioStream(client, server.url(path), 4194304), path)
            }
        }
    }

    @Test fun shortProgressiveAudioIsAcceptedOnlyAfterItsBodyArrives() = runBlocking {
        serve { server, client ->
            server.createContext("/progressive") { exchange ->
                exchange.responseHeaders.set("Content-Type", "audio/mp4")
                exchange.sendResponseHeaders(200, 4096)
                exchange.responseBody.use { it.write(ByteArray(4096) { 4 }) }
            }
            assertEquals(AudioStreamProbeResult.PROGRESSIVE_READABLE, probeAudioStream(client, server.url("progressive"), 4096))
        }
    }

    @Test fun cancellationWhileTheResponseBodyIsStalledIsPreserved() = runBlocking {
        serve { server, client ->
            val headersSent = CompletableDeferred<Unit>()
            val release = java.util.concurrent.CountDownLatch(1)
            server.createContext("/stalled") { exchange ->
                exchange.responseHeaders.set("Content-Type", "audio/mp4")
                exchange.responseHeaders.set("Content-Range", "bytes 1572864-2097151/4194304")
                exchange.sendResponseHeaders(206, 524288)
                headersSent.complete(Unit)
                try { release.await(10, java.util.concurrent.TimeUnit.SECONDS) }
                finally { exchange.close() }
            }
            val work = async { probeAudioStream(client, server.url("stalled"), 4194304) }
            try {
                withTimeout(5000) { headersSent.await() }
                withTimeout(2000) { work.cancelAndJoin() }
                assertTrue(work.isCancelled)
            } finally { release.countDown(); work.cancel() }
        }
    }

    private suspend fun serve(block: suspend (HttpServer, HttpClient) -> Unit) {
        val executor = Executors.newCachedThreadPool()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            this.executor = executor
            start()
        }
        val client = HttpClient(CIO) { install(HttpTimeout) }
        try { block(server, client) }
        finally { client.close(); server.stop(0); executor.shutdownNow() }
    }

    private fun HttpServer.url(path: String) = "http://127.0.0.1:${address.port}/$path"
}
