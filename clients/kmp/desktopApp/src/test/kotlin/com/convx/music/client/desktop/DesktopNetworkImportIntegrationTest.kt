package com.convx.music.client.desktop

import com.convx.music.playback.AudioStreamResolver
import com.convx.storage.data.NetworkMediaHttpServer
import com.convx.storage.data.RemoteNetworkLibraryRepository
import com.convx.storage.data.StoredNetworkServerStore
import com.convx.storage.domain.*
import com.convx.storage.presentation.NetworkAudioStreamResolver
import com.convx.storage.presentation.NetworkPlaybackEndpoint
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import javax.imageio.ImageIO
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DesktopNetworkImportIntegrationTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun scanImportReopenAndStream() = runBlocking {
        val wav = ByteArrayOutputStream().also { output ->
            val format = AudioFormat(44_100f, 16, 1, true, false)
            AudioInputStream(ByteArrayInputStream(ByteArray(176_400)), format, 88_200).use {
                AudioSystem.write(it, AudioFileFormat.Type.WAVE, output)
            }
        }.toByteArray()
        val image = ByteArrayOutputStream().also { ImageIO.write(BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB), "png", it) }.toByteArray()
        val source = MemorySource(mapOf("Album/01 - Song.wav" to wav, "Album/cover.png" to image))
        var savedSnapshot: String? = null
        val store = StoredNetworkServerStore(object : NetworkSnapshotStore {
            override suspend fun read() = savedSnapshot
            override suspend fun write(snapshot: String) { savedSnapshot = snapshot }
        })
        val repository = RemoteNetworkLibraryRepository(store, source, source)
        repository.save(NetworkServer.create(NetworkProtocol.WEBDAV, "NAS", "https://example.test/music/", SourceCredentials(), "nas"))
        val index = File(temporary.root, "index.json")
        val savedTracks = RecordingLibraryWriter()
        val library = DesktopMediaLibrary(index, File(temporary.root, "local-art"), savedTracks)
        val importer = NetworkLibraryImporter(repository, repository, DesktopNetworkTagReader(),
            DesktopNetworkArtworkCache(temporary.root.toPath().resolve("network-art")), DesktopNetworkLibraryWriter(library))
        assertEquals(1, importer.import("nas").added)
        assertEquals(1, importer.import("nas").skipped)
        val imported = library.state.value.index.tracks.single()
        assertEquals("Song", imported.title)
        assertEquals(2_000L, imported.durationMs)
        assertTrue(File(assertNotNull(imported.artworkPath)).isFile)
        assertEquals(1, savedTracks.tracks.size)
        library.close()

        val reopened = DesktopMediaLibrary(index, File(temporary.root, "local-art"), savedTracks)
        reopened.awaitReady()
        NetworkMediaHttpServer(repository).use { bridge ->
            val resolver = NetworkAudioStreamResolver(repository, NetworkPlaybackEndpoint { id, mime ->
                bridge.createMediaSource(id, mime)
            }, object : AudioStreamResolver {
                override suspend fun resolve(track: com.convx.music.playback.PlaybackRequestTrack) =
                    Result.failure<com.convx.music.playback.PlaybackAudioStream>(IllegalStateException("unexpected fallback"))
            })
            val stream = resolver.resolve(reopened.state.value.index.tracks.single().request()).getOrThrow()
            val firstBytes = URI(stream.url).toURL().openStream().use { it.readNBytes(32) }
            assertContentEquals(wav.copyOfRange(0, 32), firstBytes)
        }
        reopened.close()
        repository.close()
    }

    private class MemorySource(private val files: Map<String, ByteArray>) : NetworkFileSource {
        override suspend fun list(server: NetworkServer, folder: String): List<NetworkEntry> = when (folder) {
            "" -> listOf(NetworkEntry("Album", "Album", true))
            "Album" -> files.map { (path, bytes) -> NetworkEntry(path, path.substringAfterLast('/'), false, bytes.size.toLong()) }
            else -> emptyList()
        }
        override fun open(server: NetworkServer, path: String): RemoteFile {
            val bytes = files.getValue(path)
            return object : RemoteFile {
                override val length = bytes.size.toLong()
                override fun read(position: Long, buffer: ByteArray, offset: Int, length: Int): Int {
                    if (position >= bytes.size) return -1
                    val count = minOf(length, bytes.size - position.toInt())
                    bytes.copyInto(buffer, offset, position.toInt(), position.toInt() + count)
                    return count
                }
                override fun close() = Unit
            }
        }
        override fun close() = Unit
    }
}
