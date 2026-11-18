package com.convx.music.client.desktop

import com.convx.storage.data.RemoteNetworkLibraryRepository
import com.convx.storage.data.NetworkMediaHttpServer
import com.convx.storage.data.StoredNetworkServerStore
import com.convx.storage.data.WebDavFileSource
import com.convx.storage.domain.*
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetSocketAddress
import java.net.HttpURLConnection
import java.net.URI
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class DesktopWebDavImportTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun importsFromAuthenticatedWebDavUsingRangeRequests() = runBlocking {
        val audio = ByteArrayOutputStream().also { output ->
            val format = AudioFormat(44_100f, 16, 1, true, false)
            AudioInputStream(ByteArrayInputStream(ByteArray(176_400)), format, 88_200).use {
                AudioSystem.write(it, AudioFileFormat.Type.WAVE, output)
            }
        }.toByteArray()
        val artwork = ByteArrayOutputStream().also { ImageIO.write(BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB), "png", it) }.toByteArray()
        val rangeRequests = AtomicInteger()
        val expectedAuth = "Basic " + Base64.getEncoder().encodeToString("user:secret".toByteArray())
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/music/") { exchange ->
            try {
                if (exchange.requestHeaders.getFirst("Authorization") != expectedAuth) {
                    exchange.sendResponseHeaders(401, -1)
                } else if (exchange.requestMethod == "PROPFIND") {
                    val xml = """<d:multistatus xmlns:d="DAV:">
                        <d:response><d:href>/music/song.wav</d:href><d:propstat><d:prop><d:displayname>song.wav</d:displayname><d:resourcetype/><d:getcontentlength>${audio.size}</d:getcontentlength></d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat></d:response>
                        <d:response><d:href>/music/cover.png</d:href><d:propstat><d:prop><d:displayname>cover.png</d:displayname><d:resourcetype/><d:getcontentlength>${artwork.size}</d:getcontentlength></d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat></d:response>
                        </d:multistatus>""".toByteArray()
                    exchange.sendResponseHeaders(207, xml.size.toLong())
                    exchange.responseBody.write(xml)
                } else {
                    val bytes = if (exchange.requestURI.path.endsWith("song.wav")) audio else artwork
                    val start = exchange.requestHeaders.getFirst("Range")?.substringAfter("bytes=")?.substringBefore('-')?.toIntOrNull() ?: 0
                    if (start >= bytes.size) {
                        exchange.responseHeaders.set("Content-Range", "bytes */${bytes.size}")
                        exchange.sendResponseHeaders(416, -1)
                    } else {
                        rangeRequests.incrementAndGet()
                        exchange.responseHeaders.set("Content-Range", "bytes $start-${bytes.lastIndex}/${bytes.size}")
                        exchange.sendResponseHeaders(206, (bytes.size - start).toLong())
                        exchange.responseBody.write(bytes, start, bytes.size - start)
                    }
                }
            } finally { exchange.close() }
        }
        server.start()
        var savedSnapshot: String? = null
        val store = StoredNetworkServerStore(object : NetworkSnapshotStore {
            override suspend fun read() = savedSnapshot
            override suspend fun write(snapshot: String) { savedSnapshot = snapshot }
        })
        val dav = WebDavFileSource()
        val repository = RemoteNetworkLibraryRepository(store, dav, dav)
        val library = DesktopMediaLibrary(File(temporary.root, "index.json"), File(temporary.root, "local-art"), RecordingLibraryWriter())
        try {
            repository.save(NetworkServer.create(NetworkProtocol.WEBDAV, "Test", "http://127.0.0.1:${server.address.port}/music/", SourceCredentials("user", "secret"), "dav"))
            val importer = NetworkLibraryImporter(repository, repository, DesktopNetworkTagReader(),
                DesktopNetworkArtworkCache(temporary.root.toPath().resolve("network-art")), DesktopNetworkLibraryWriter(library))
            assertEquals(1, importer.import("dav").added)
            val track = library.state.value.index.tracks.single()
            assertEquals(2_000L, track.durationMs)
            assertTrue(File(requireNotNull(track.artworkPath)).isFile)
            NetworkMediaHttpServer(repository).use { bridge ->
                val url = bridge.createMediaSource(track.id, "audio/wav")
                val connection = URI(url).toURL().openConnection() as HttpURLConnection
                try {
                    connection.setRequestProperty("Range", "bytes=8-31")
                    assertEquals(206, connection.responseCode)
                    assertContentEquals(audio.copyOfRange(8, 32), connection.inputStream.use { it.readBytes() })
                } finally { connection.disconnect() }
            }
            assertTrue(rangeRequests.get() > 0)
        } finally {
            library.close()
            repository.close()
            server.stop(0)
        }
    }
}
