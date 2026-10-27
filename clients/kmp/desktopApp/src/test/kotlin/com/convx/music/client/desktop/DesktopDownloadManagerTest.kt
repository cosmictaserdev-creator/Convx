package com.convx.music.client.desktop

import com.convx.music.playback.*
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.InetSocketAddress
import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.*

class DesktopDownloadManagerTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun downloadsProgressiveAudioWhenServerDoesNotSupportRanges() = runBlocking {
        val bytes = ByteArray(512 * 1024) { (it % 127).toByte() }
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            createContext("/audio") { exchange ->
                exchange.responseHeaders.set("Content-Type", "audio/mp4")
                exchange.sendResponseHeaders(200, 0)
                exchange.responseBody.use { it.write(bytes) }
            }
            start()
        }
        val resolver = object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack) = Result.success(PlaybackAudioStream(
                "http://127.0.0.1:${server.address.port}/audio", "audio/mp4", 30_000, 128_000))
        }
        val library = DesktopMediaLibrary(File(temporary.root, "index.json"), File(temporary.root, "art"), RecordingLibraryWriter())
        val manager = DesktopDownloadManager(resolver, library, temporary.newFolder("Downloads"))
        try {
            val track = PlaybackRequestTrack("youtube_music", "progressive", "Progressive fixture", emptyList())
            manager.enqueue(listOf(track)).join()
            withTimeout(20_000) { library.state.first { it.index.downloads.singleOrNull()?.status == DesktopDownloadStatus.COMPLETE } }
            assertContentEquals(bytes, requireNotNull(library.downloadedFile(track)).readBytes())
        } finally { manager.close(); library.close(); server.stop(0) }
    }

    @Test
    fun pausedDownloadResumesFromItsPartialFileAndBecomesAnOfflineSource() = runBlocking {
        val bytes = ByteArray(8 * 1024 * 1024) { (it % 251).toByte() }
        val offsets = Collections.synchronizedList(mutableListOf<Long>())
        val executor = Executors.newCachedThreadPool()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            this.executor = executor
            createContext("/audio") { exchange ->
                try {
                    val range = Regex("bytes=(\\d+)-(\\d*)").matchEntire(exchange.requestHeaders.getFirst("Range").orEmpty())
                    val start = range?.groupValues?.get(1)?.toInt() ?: 0
                    val end = minOf(range?.groupValues?.get(2)?.toIntOrNull() ?: bytes.lastIndex, bytes.lastIndex)
                    offsets += start.toLong()
                    exchange.responseHeaders.set("Content-Type", "audio/mp4")
                    exchange.responseHeaders.set("Content-Range", "bytes $start-$end/${bytes.size}")
                    exchange.sendResponseHeaders(206, (end - start + 1).toLong())
                    exchange.responseBody.use { output ->
                        var cursor = start
                        while (cursor <= end) {
                            val count = minOf(8192, end - cursor + 1)
                            output.write(bytes, cursor, count)
                            cursor += count
                            Thread.sleep(2)
                        }
                    }
                } catch (_: Exception) { exchange.close() }
            }
            start()
        }
        val source = "http://127.0.0.1:${server.address.port}/audio"
        val requests = AtomicInteger()
        val online = object : AudioStreamResolver {
            override suspend fun resolve(track: PlaybackRequestTrack): Result<PlaybackAudioStream> {
                requests.incrementAndGet()
                return Result.success(PlaybackAudioStream(source, "audio/mp4", 10_000, 128_000))
            }
        }
        val index = File(temporary.root, "index.json")
        val library = DesktopMediaLibrary(index, File(temporary.root, "art"), RecordingLibraryWriter())
        val manager = DesktopDownloadManager(online, library, temporary.newFolder("Downloads"))
        try {
            val track = PlaybackRequestTrack("youtube_music", "fixture", "Resume fixture", listOf("Test"), 10_000)
            val key = DesktopDownloadManager.key(track)
            manager.enqueue(listOf(track)).join()
            withTimeout(20_000) { library.state.first { it.index.downloads.any { it.receivedBytes >= 1_048_576 && it.status == DesktopDownloadStatus.DOWNLOADING } } }
            manager.pause(key)
            withTimeout(10_000) { library.state.first { it.index.downloads.single().status == DesktopDownloadStatus.PAUSED } }
            manager.resume(key)
            repeat(3) { manager.pause(key); manager.resume(key) }
            withTimeout(30_000) { library.state.first { it.index.downloads.single().status == DesktopDownloadStatus.COMPLETE } }
            val saved = requireNotNull(library.downloadedFile(track))
            assertContentEquals(bytes, saved.readBytes())
            assertTrue(requests.get() >= 2, "Resume must resolve a fresh expiring stream URL")
            assertTrue(offsets.any { it > 0 }, "Resume must use validated byte ranges")
            assertFalse(index.readText().contains(source), "Signed stream URLs must not enter persistent state")
            val offline = DesktopLocalAudioStreamResolver(online, library, FixedPlaybackSettings(PlaybackSettings(localMode = true)))
            val before = requests.get()
            assertTrue(offline.resolve(track).getOrThrow().url.startsWith("file:"))
            assertEquals(before, requests.get())
            manager.remove(key, deleteFile = true).join()
            assertFalse(saved.exists())
            assertTrue(library.state.value.index.downloads.isEmpty())
        } finally { manager.close(); library.close(); server.stop(0); executor.shutdownNow() }
    }
}
