package com.convx.storage

import com.convx.storage.data.NetworkMediaHttpServer
import com.convx.storage.domain.*
import com.convx.storage.data.RemoteNetworkLibraryRepository
import com.convx.storage.data.StoredNetworkServerStore
import com.convx.storage.data.WebDavFileSource
import com.convx.storage.data.SmbFileSource
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.concurrent.TimeUnit
import java.net.HttpURLConnection
import java.net.URI
import org.junit.Test
import org.junit.Assert.*

class NetworkMediaHttpServerTest {
    private val data = "0123456789".toByteArray()
    private val closes = java.util.concurrent.LinkedBlockingQueue<Unit>()
    private val access = NetworkMediaAccess {
        object : RemoteFile {
            override val length = data.size.toLong()
            override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
                if (position >= data.size) return -1
                val count = minOf(length, data.size - position.toInt())
                data.copyInto(buffer, offset, position.toInt(), position.toInt() + count)
                return count
            }
            override fun close() { closes.add(Unit) }
        }
    }

    @Test fun servesIndependentFullAndSeekRequestsWithoutPuttingSourceDetailsInTheUrl() {
        NetworkMediaHttpServer(access).use { server ->
            val url = server.createMediaSource(NetworkMediaUri.create("private-nas", "Private Album/song.flac"), "audio/flac")
            assertFalse(url.contains("private-nas"))
            assertFalse(url.contains("Private"))
            assertEquals("0123456789", read(url).second)
            val seek = read(url, "bytes=5-7")
            assertEquals(206, seek.first)
            assertEquals("567", seek.second)
            assertEquals("89", read(url, "bytes=-2").second)
            assertEquals("123456789", read(url, "bytes=1-").second)
            assertClosed(4)
        }
    }

    @Test fun rejectsEofMalformedRangesAndUnknownTokensAndClosesEveryHandle() {
        NetworkMediaHttpServer(access).use { server ->
            val url = server.createMediaSource(NetworkMediaUri.create("nas", "song.mp3"), "audio/mpeg")
            for (range in listOf("bytes=10-", "bytes=7-2", "bytes=0-1,3-4", "bytes=99999999999999999999999-")) {
                assertEquals(416, read(url, range).first)
            }
            assertEquals(404, read(url + "missing").first)
            assertClosed(4)
        }
    }

    @Test fun headReportsTheFileLengthAndDoesNotConsumeARangeBody() {
        NetworkMediaHttpServer(access).use { server ->
            val url = server.createMediaSource(NetworkMediaUri.create("nas", "song.mp3"), "audio/mpeg")
            val connection = URI(url).toURL().openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "HEAD"
                connection.setRequestProperty("Range", "bytes=5-7")
                assertEquals(200, connection.responseCode)
                assertEquals(10, connection.contentLengthLong)
                assertEquals("", connection.inputStream.bufferedReader().readText())
            } finally { connection.disconnect() }
            assertClosed(1)
        }
    }

    @Test fun authenticatedWebDavCanSeekThroughTheActualRepositoryAndDesktopBridge() = runBlocking {
        MockWebServer().use { upstream ->
            upstream.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 0-9/10").setBody("0123456789"))
            upstream.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 5-9/10").setBody("56789"))
            val store = StoredNetworkServerStore(MemoryNetworkSnapshot())
            store.save(NetworkServer.create(NetworkProtocol.WEBDAV, "DAV", upstream.url("/music/").toString(), SourceCredentials("user", "secret"), "dav"))
            RemoteNetworkLibraryRepository(store, WebDavFileSource(), SmbFileSource()).use { repository ->
                NetworkMediaHttpServer(repository).use { bridge ->
                    val localUrl = bridge.createMediaSource(NetworkMediaUri.create("dav", "song.mp3"), "audio/mpeg")
                    assertEquals(206 to "567", read(localUrl, "bytes=5-7"))
                    val probe = upstream.takeRequest(2, TimeUnit.SECONDS)!!
                    val seek = upstream.takeRequest(2, TimeUnit.SECONDS)!!
                    assertEquals("bytes=0-", probe.getHeader("Range"))
                    assertEquals("bytes=5-", seek.getHeader("Range"))
                    assertEquals(Credentials.basic("user", "secret"), seek.getHeader("Authorization"))
                    assertFalse(localUrl.contains("secret"))
                }
            }
        }
    }

    private fun read(url: String, range: String? = null): Pair<Int, String> {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        range?.let { connection.setRequestProperty("Range", it) }
        return try {
            val status = connection.responseCode
            status to if (status < 400) connection.inputStream.bufferedReader().use { it.readText() } else ""
        } finally { connection.disconnect() }
    }

    private fun assertClosed(count: Int) {
        repeat(count) { assertNotNull("Remote handle was not closed", closes.poll(3, TimeUnit.SECONDS)) }
    }
}
