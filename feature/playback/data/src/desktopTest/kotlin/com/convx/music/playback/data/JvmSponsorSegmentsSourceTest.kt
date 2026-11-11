package com.convx.music.playback.data

import com.convx.music.playback.DefaultSponsorCategories
import com.convx.music.playback.SponsorCategory
import com.convx.music.playback.SponsorSegment
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import kotlin.test.*

class JvmSponsorSegmentsSourceTest {
    private val id = "abcdefghijk"
    private val payload = """[{"videoID":"$id","segments":[{"category":"sponsor","actionType":"skip","segment":[1,5]}]}]"""

    @Test fun hashPrefixRequestsDoNotSendTheVideoIdAndSuccessfulResultsAreCachedByCategory() = runBlocking {
        val requests = ConcurrentLinkedQueue<String>()
        withSource(handler = { exchange ->
            requests += exchange.requestURI.toString()
            val bytes = payload.toByteArray()
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }) { source ->
            val expected = listOf(SponsorSegment(1_000, 5_000, SponsorCategory.SPONSOR))
            assertEquals(expected, source.segments(id, DefaultSponsorCategories))
            assertEquals(expected, source.segments(id, DefaultSponsorCategories))
            assertEquals(1, requests.size)
            assertTrue(requests.single().startsWith("/api/skipSegments/ca2f?"))
            assertFalse(requests.single().contains(id))
            assertTrue(requests.single().contains("actionType=skip"))
            source.segments(id, setOf(SponsorCategory.INTRO))
            assertEquals(2, requests.size, "A category change must not reuse an incomplete lookup")
        }
    }

    @Test fun failedAndMalformedResponsesAreNotCachedButNotFoundIs() = runBlocking {
        val replies = ConcurrentLinkedQueue(listOf(503 to "unavailable", 200 to "not JSON", 404 to "", 200 to payload))
        withSource(handler = { exchange ->
            val (status, body) = replies.remove()
            val bytes = body.toByteArray()
            exchange.sendResponseHeaders(status, if (bytes.isEmpty()) -1 else bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }) { source ->
            assertNull(source.segments(id, DefaultSponsorCategories))
            assertNull(source.segments(id, DefaultSponsorCategories))
            assertEquals(emptyList(), source.segments(id, DefaultSponsorCategories))
            assertEquals(emptyList(), source.segments(id, DefaultSponsorCategories))
            assertEquals(1, replies.size)
        }
    }

    @Test fun bothKnownAndChunkedOversizedBodiesAreRejected() = runBlocking {
        var request = 0
        val oversized = ("[]" + " ".repeat(1_048_576)).toByteArray()
        withSource(handler = { exchange ->
            exchange.sendResponseHeaders(200, if (++request == 1) oversized.size.toLong() else 0L)
            try { exchange.responseBody.use { it.write(oversized) } } catch (_: java.io.IOException) { }
        }) { source ->
            assertNull(source.segments(id, DefaultSponsorCategories))
            assertNull(source.segments(id, DefaultSponsorCategories))
            assertEquals(2, request)
        }
    }

    @Test fun cancellationCancelsTheNativeHttpCall() = runBlocking {
        val arrived = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val release = CountDownLatch(1)
        val client = OkHttpClient.Builder().eventListener(object : EventListener() {
            override fun canceled(call: Call) { cancelled.complete(Unit) }
        }).build()
        try {
            withSource(client, handler = { exchange ->
                arrived.complete(Unit)
                release.await(3, TimeUnit.SECONDS)
                try { exchange.sendResponseHeaders(404, -1) } catch (_: java.io.IOException) { }
            }) { source ->
                val lookup = async(Dispatchers.IO) { source.segments(id, DefaultSponsorCategories) }
                withTimeout(3_000) { arrived.await() }
                lookup.cancelAndJoin()
                withTimeout(3_000) { cancelled.await() }
                release.countDown()
            }
        } finally { release.countDown(); client.dispatcher.executorService.shutdownNow(); client.connectionPool.evictAll() }
    }

    private suspend fun withSource(
        client: OkHttpClient = OkHttpClient(),
        handler: (com.sun.net.httpserver.HttpExchange) -> Unit,
        block: suspend (JvmSponsorSegmentsSource) -> Unit,
    ) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/api/skipSegments/") { exchange -> exchange.use { handler(it) } }
        server.start()
        try { block(JvmSponsorSegmentsSource(client, "http://127.0.0.1:${server.address.port}/api/skipSegments/".toHttpUrl())) }
        finally { server.stop(0); client.dispatcher.executorService.shutdownNow(); client.connectionPool.evictAll() }
    }
}
