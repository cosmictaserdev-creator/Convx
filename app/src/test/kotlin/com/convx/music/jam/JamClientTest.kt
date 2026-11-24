package com.convx.music.jam

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class JamClientTest {
    /** A real local peer that accepts HTTP but withholds its response. No deployed room is touched. */
    private class Peer : AutoCloseable {
        val server = ServerSocket(0)
        val accepted = CountDownLatch(1)
        val disconnected = CountDownLatch(1)
        val socket = AtomicReference<Socket?>()
        val url = "http://127.0.0.1:${server.localPort}"
        private val worker = thread(isDaemon = true) {
            try {
                server.accept().use { connection ->
                    socket.set(connection)
                    connection.soTimeout = 5_000
                    val input = connection.getInputStream()
                    var header = ""
                    while (!header.endsWith("\r\n\r\n")) {
                        val byte = input.read()
                        if (byte == -1) return@thread
                        header = (header + byte.toChar()).takeLast(4)
                    }
                    accepted.countDown()
                    if (input.read() == -1) disconnected.countDown()
                }
            } catch (_: Exception) { }
        }
        override fun close() {
            socket.get()?.close()
            server.close()
            worker.join(1_000)
        }
    }

    @Test
    fun cancellingARequestClosesTheUnderlyingSocket() = runBlocking {
        Peer().use { peer ->
            val client = JamClient()
            val job = launch(kotlinx.coroutines.Dispatchers.Default) { client.stats(peer.url) }
            assertTrue("request reached peer", peer.accepted.await(3, TimeUnit.SECONDS))
            job.cancelAndJoin()
            assertTrue("cancel closed socket", peer.disconnected.await(3, TimeUnit.SECONDS))
        }
    }

    @Test
    fun aServerThatNeverRepliesHasABoundedRequest() = runBlocking {
        Peer().use { peer ->
            val client = JamClient(OkHttpClient.Builder().readTimeout(250, TimeUnit.MILLISECONDS)
                .callTimeout(1, TimeUnit.SECONDS).build())
            val began = System.nanoTime()
            assertEquals(null, client.stats(peer.url))
            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - began) < 3_000)
        }
    }
}
