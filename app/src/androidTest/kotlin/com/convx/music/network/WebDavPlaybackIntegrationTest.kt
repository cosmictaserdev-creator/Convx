package com.convx.music.network

import android.os.Looper
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.convx.music.db.InternalDatabase
import com.convx.music.db.MusicDatabase
import com.convx.music.extensions.toMediaItem
import com.convx.music.utils.AndroidNetworkTagReader
import com.convx.music.utils.RoomNetworkLibraryWriter
import com.convx.music.utils.NetworkEmbeddedArtworkFetcher
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.CachePolicy
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.io.ByteArrayOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class WebDavPlaybackIntegrationTest {
    @Test fun importedTrackPlaysAndSeeksThroughAuthenticatedWebDav() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        DavServer().use { server ->
            val saved = NetworkServer.create(NetworkProtocol.WEBDAV, "Test", "http://127.0.0.1:${server.port}/music/",
                SourceCredentials("user", "secret"), "nas")
            val store = MemoryStore(saved)
            val repository = NetworkMusicRepository(store)
            val db = Room.inMemoryDatabaseBuilder(context, InternalDatabase::class.java).build()
            try {
                val library = MusicDatabase(db)
                val id = NetworkMediaUri.create("nas", "song.wav")
                val item = runBlocking {
                    val result = NetworkLibraryImporter(repository, repository, AndroidNetworkTagReader(),
                        { _, _ -> null }, RoomNetworkLibraryWriter(library)).import("nas")
                    assertEquals(1, result.added)
                    assertTrue("Missing listing size should not prevent WAV metadata", library.getSongById(id)!!.song.duration > 0)
                    val writer = RoomNetworkLibraryWriter(library)
                    val original = library.getSongById(id)!!.song
                    library.update(original.copy(thumbnailUrl = "file:///missing-convx-nas-cover.jpg"))
                    assertEquals(SongArtworkState.MISSING, writer.artworkState(id))
                    writer.updateMetadata(id, NetworkTags(title = "Correct title", artist = "Correct artist",
                        album = "Correct album", albumArtist = "Correct artist", trackNumber = 2))
                    val repaired = library.getSongById(id)!!
                    assertEquals("Correct title", repaired.song.title)
                    assertEquals("Correct artist", repaired.artists.single().name)
                    assertEquals("Correct album", repaired.song.albumName)
                    val album = library.albumWithSongs(repaired.song.albumId!!).first()!!
                    assertEquals("Correct artist", album.artists.single().name)
                    assertEquals(id, album.songs.single().song.id)
                    library.getSongById(id)!!.toMediaItem()
                }
                val ready = CountDownLatch(1)
                val failure = arrayOfNulls<PlaybackException>(1)
                lateinit var player: ExoPlayer
                val instrumentation = InstrumentationRegistry.getInstrumentation()
                instrumentation.runOnMainSync {
                    check(Looper.myLooper() == Looper.getMainLooper())
                    player = ExoPlayer.Builder(context).setMediaSourceFactory(
                        DefaultMediaSourceFactory(repository.dataSourceFactory(DefaultDataSource.Factory(context)))
                    ).build()
                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) { if (state == Player.STATE_READY) ready.countDown() }
                        override fun onPlayerError(error: PlaybackException) { failure[0] = error; ready.countDown() }
                    })
                    player.setMediaItem(item)
                    player.prepare()
                    player.play()
                }
                try {
                    assertTrue("Player did not prepare", ready.await(20, TimeUnit.SECONDS))
                    assertNull("Player failed: ${failure[0]}", failure[0])
                    instrumentation.runOnMainSync { player.seekTo(3_000) }
                    Thread.sleep(500)
                    instrumentation.runOnMainSync { assertTrue("Seek did not advance", player.currentPosition >= 3_000) }
                    assertTrue("Authenticated GET was not received", server.authenticatedGets > 0)
                } finally { instrumentation.runOnMainSync { player.release() } }
                runBlocking { store.save((saved as NetworkServer.WebDav).copy(credentials = SourceCredentials("user", "wrong"))) }
                val source = repository.dataSourceFactory(DefaultDataSource.Factory(context)).createDataSource()
                try {
                    source.open(DataSpec.Builder().setUri(id).build())
                    fail("Expected an authentication error")
                } catch (error: HttpDataSource.InvalidResponseCodeException) {
                    assertEquals(401, error.responseCode)
                } finally { source.close() }
            } finally { db.close() }
        }
    }

    @Test fun coilLoadsEmbeddedArtOnDemandFromAuthenticatedWebDav() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bitmap = android.graphics.Bitmap.createBitmap(2, 2, android.graphics.Bitmap.Config.ARGB_8888)
        val image = ByteArrayOutputStream().use { out -> bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out); out.toByteArray() }
        bitmap.recycle()
        val picture = byteArrayOf(0) + "image/png".toByteArray() + byteArrayOf(0, 3, 0) + image
        fun int32(value: Int) = byteArrayOf((value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte())
        val frame = "APIC".toByteArray() + int32(picture.size) + byteArrayOf(0, 0) + picture
        val length = frame.size
        val syncsafe = byteArrayOf((length ushr 21).toByte(), ((length ushr 14) and 127).toByte(), ((length ushr 7) and 127).toByte(), (length and 127).toByte())
        val data = "ID3".toByteArray() + byteArrayOf(3, 0, 0) + syncsafe + frame
        DavServer(data).use { server ->
            val serverId = "nas${server.port}"
            val saved = NetworkServer.create(NetworkProtocol.WEBDAV, "Test", "http://127.0.0.1:${server.port}/music/",
                SourceCredentials("user", "secret"), serverId)
            val repository = NetworkMusicRepository(MemoryStore(saved))
            val loader = ImageLoader.Builder(context).components { add(NetworkEmbeddedArtworkFetcher.Factory { repository }) }.build()
            val model = NetworkEmbeddedArtworkUri.create(NetworkMediaUri.create(serverId, "song.wav"))!!
            val request = ImageRequest.Builder(context).data(model).memoryCachePolicy(CachePolicy.DISABLED).build()
            val result = loader.execute(request)
            assertTrue("Expected embedded cover: $result", result is SuccessResult)
            assertTrue(server.authenticatedGets > 0)
            val reads = server.authenticatedGets
            val cached = loader.execute(request)
            assertTrue("Expected cached cover: $cached", cached is SuccessResult)
            assertEquals(reads, server.authenticatedGets)
            loader.shutdown()
        }
    }

    private class MemoryStore(server: NetworkServer) : NetworkServerStore {
        override val servers = MutableStateFlow(listOf(server))
        override suspend fun load() = servers.value
        override suspend fun save(server: NetworkServer) { servers.value = listOf(server) }
        override suspend fun delete(id: String) { servers.value = servers.value.filterNot { it.id == id } }
        override fun find(id: String) = servers.value.firstOrNull { it.id == id }
    }

    private class DavServer(private val audio: ByteArray = wav()) : AutoCloseable {
        private val socket = ServerSocket(0, 20, java.net.InetAddress.getByName("127.0.0.1"))
        val port get() = socket.localPort
        @Volatile var authenticatedGets = 0
        private val worker = thread(isDaemon = true) {
            while (!socket.isClosed) {
                val client = try { socket.accept() } catch (_: Exception) { break }
                thread(isDaemon = true) {
                    client.use {
                        val input = it.getInputStream().bufferedReader(Charsets.US_ASCII)
                        val request = input.readLine() ?: return@use
                        val headers = mutableMapOf<String, String>()
                        while (true) {
                            val line = input.readLine() ?: break
                            if (line.isEmpty()) break
                            headers[line.substringBefore(':').lowercase()] = line.substringAfter(':').trim()
                        }
                        val authorized = headers["authorization"] == "Basic dXNlcjpzZWNyZXQ="
                        val isListing = request.startsWith("PROPFIND ")
                        val body = if (!authorized) ByteArray(0) else if (isListing) listing().toByteArray() else audio
                        val start = if (!isListing) headers["range"]?.substringAfter("bytes=")?.substringBefore('-')?.toIntOrNull() ?: 0 else 0
                        val end = if (!isListing) headers["range"]?.substringAfter('-')?.toIntOrNull()?.coerceAtMost(body.lastIndex) ?: body.lastIndex else body.lastIndex
                        val partial = authorized && !isListing && headers["range"] != null
                        val payload = if (start in 0..end && end < body.size) body.copyOfRange(start, end + 1) else body
                        if (authorized && !isListing) authenticatedGets++
                        val status = if (!authorized) "401 Unauthorized" else if (isListing) "207 Multi-Status" else if (partial) "206 Partial Content" else "200 OK"
                        val range = if (partial) "Content-Range: bytes $start-$end/${body.size}\r\n" else ""
                        val response = "HTTP/1.1 $status\r\nContent-Length: ${payload.size}\r\n${range}Connection: close\r\n\r\n"
                        it.getOutputStream().apply { write(response.toByteArray()); write(payload); flush() }
                    }
                }
            }
        }
        private fun listing() = """<?xml version="1.0"?><d:multistatus xmlns:d="DAV:"><d:response><d:href>/music/song.wav</d:href><d:propstat><d:prop><d:displayname>song.wav</d:displayname><d:resourcetype/></d:prop><d:status>HTTP/1.1 200 OK</d:status></d:propstat></d:response></d:multistatus>"""
        override fun close() { socket.close(); worker.join(1000) }
        companion object { private fun wav(): ByteArray {
            val samples = 8_000 * 8 * 2
            return ByteBuffer.allocate(44 + samples).order(ByteOrder.LITTLE_ENDIAN).apply {
                put("RIFF".toByteArray()); putInt(36 + samples); put("WAVEfmt ".toByteArray()); putInt(16)
                putShort(1); putShort(1); putInt(8_000); putInt(16_000); putShort(2); putShort(16)
                put("data".toByteArray()); putInt(samples)
            }.array()
        } }
    }
}
