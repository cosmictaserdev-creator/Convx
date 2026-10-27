package com.convx.storage

import com.convx.storage.data.WebDavFileSource
import com.convx.storage.domain.NetworkProtocol
import com.convx.storage.domain.NetworkServer
import com.convx.storage.domain.SourceCredentials
import okhttp3.Credentials
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class WebDavPlaybackTest {
    private fun source(server: MockWebServer) = NetworkServer.create(
        NetworkProtocol.WEBDAV, "Test WebDAV", server.url("/music/").toString(), SourceCredentials("user", "pass"), "webdav",
    )

    @Test fun seeksAfterRedirectAndPreservesAuthenticationOnTheSameOrigin() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(302).setHeader("Location", "/media/track.mp3"))
            server.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 5-9/10").setBody("56789"))
            WebDavFileSource().open(source(server), "track.mp3").use { file ->
                val buffer = ByteArray(5)
                assertEquals(5, file.read(5, buffer, 0, buffer.size))
                assertEquals("56789", buffer.toString(Charsets.UTF_8))
                assertEquals(10L, file.length)
            }
            val first = server.takeRequest(2, TimeUnit.SECONDS)!!
            val redirected = server.takeRequest(2, TimeUnit.SECONDS)!!
            assertEquals("bytes=5-", first.getHeader("Range"))
            assertEquals("bytes=5-", redirected.getHeader("Range"))
            assertEquals(Credentials.basic("user", "pass"), redirected.getHeader("Authorization"))
        }
    }

    @Test fun redirectsToAnotherOriginWithoutForwardingTheServerPassword() {
        MockWebServer().use { server ->
            MockWebServer().use { cdn ->
                server.enqueue(MockResponse().setResponseCode(302).setHeader("Location", cdn.url("/signed/track.mp3")))
                cdn.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 5-9/10").setBody("56789"))
                WebDavFileSource().open(source(server), "track.mp3").use { file ->
                    assertEquals(5, file.read(5, ByteArray(5), 0, 5))
                }
                val redirected = cdn.takeRequest(2, TimeUnit.SECONDS)!!
                assertNull(redirected.getHeader("Authorization"))
                assertEquals("bytes=5-", redirected.getHeader("Range"))
            }
        }
    }

    @Test fun seeksCorrectlyWhenTheServerIgnoresRangeAndReturnsTheWholeFile() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setBody("0123456789"))
            WebDavFileSource().open(source(server), "track.mp3").use { file ->
                val buffer = ByteArray(4)
                assertEquals(4, file.read(4, buffer, 0, 4))
                assertEquals("4567", buffer.toString(Charsets.UTF_8))
                assertEquals(10L, file.length)
            }
        }
    }

    @Test fun independentReadersKeepTheirOwnSeekPositions() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 2-9/10").setBody("23456789"))
            server.enqueue(MockResponse().setResponseCode(206).setHeader("Content-Range", "bytes 7-9/10").setBody("789"))
            val dav = WebDavFileSource()
            dav.open(source(server), "track.mp3").use { first ->
                dav.open(source(server), "track.mp3").use { second ->
                    val a = ByteArray(2)
                    val b = ByteArray(2)
                    assertEquals(2, first.read(2, a, 0, 2))
                    assertEquals(2, second.read(7, b, 0, 2))
                    assertEquals("23", a.toString(Charsets.UTF_8))
                    assertEquals("78", b.toString(Charsets.UTF_8))
                    assertEquals(2, first.read(4, a, 0, 2))
                    assertEquals("45", a.toString(Charsets.UTF_8))
                }
            }
        }
    }
}
