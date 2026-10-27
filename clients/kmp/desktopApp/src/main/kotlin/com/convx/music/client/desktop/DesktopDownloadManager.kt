package com.convx.music.client.desktop

import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackRequestTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/** Bounded, resumable transfers. Signed stream URLs stay in memory and never enter the index. */
internal class DesktopDownloadManager(
    private val resolver: AudioStreamResolver,
    private val library: DesktopMediaLibrary,
    private val defaultDirectory: File,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
        library.report("Couldn't save download changes. Check the app's storage and try again.")
    })
    private val gate = Semaphore(2)
    private val jobs = ConcurrentHashMap<String, Job>()
    private val connections = ConcurrentHashMap<String, HttpURLConnection>()
    // Cancellation must close the partial file before a resumed writer opens it.
    private val transferLocks = ConcurrentHashMap<String, Mutex>()
    private val proxy = DesktopAudioHttpProxy()

    fun enqueue(tracks: List<PlaybackRequestTrack>) = scope.launch {
        library.awaitReady()
        val directory = File(library.state.value.index.downloadDirectory ?: defaultDirectory.path).canonicalFile
        val candidates = tracks.filter { it.providerId != LOCAL_MUSIC_PROVIDER }.distinctBy(::key)
        library.updateIndex { index ->
            val existing = index.downloads.mapTo(mutableSetOf()) { key(it.track) }
            index.copy(downloads = index.downloads + candidates.filter { key(it) !in existing }
                .map { DesktopDownload(it, fileName(it, "m4a"), directory = directory.path) })
        }
        candidates.forEach { track ->
            library.state.value.index.downloads.firstOrNull { key(it.track) == key(track) && it.status != DesktopDownloadStatus.COMPLETE }?.let { resume(key(track)) }
        }
    }

    fun resume(id: String) {
        if (jobs[id]?.isActive == true) return
        val job = scope.launch(start = CoroutineStart.LAZY) {
            try {
                transferLocks.computeIfAbsent(id) { Mutex() }.withLock {
                    try {
                        gate.withPermit { transfer(id) }
                    } catch (cancelled: CancellationException) {
                        withContext(NonCancellable) { update(id) { if (it.status == DesktopDownloadStatus.COMPLETE) it else it.copy(status = DesktopDownloadStatus.PAUSED, message = null) } }
                        throw cancelled
                    } catch (error: Exception) {
                        val message = when (error) {
                            is DownloadFailure -> error.message
                            is java.net.SocketTimeoutException -> "The audio server timed out. Resume to try again."
                            is java.nio.file.FileSystemException, is java.io.FileNotFoundException -> "Couldn't write the download. Check the folder and available space."
                            else -> "Download interrupted. You can resume it."
                        }
                        update(id) { it.copy(status = DesktopDownloadStatus.FAILED, message = message) }
                    }
                }
            } finally { jobs.remove(id, currentCoroutineContext().job) }
        }
        val previous = jobs.putIfAbsent(id, job)
        if (previous == null) job.start() else job.cancel()
    }

    fun pause(id: String) {
        connections.remove(id)?.disconnect()
        jobs.remove(id)?.cancel()
        scope.launch {
            transferLocks.computeIfAbsent(id) { Mutex() }.withLock {
                if (jobs[id]?.isActive != true) update(id) {
                    if (it.status == DesktopDownloadStatus.COMPLETE) it else it.copy(status = DesktopDownloadStatus.PAUSED, message = null)
                }
            }
        }
    }

    fun pauseAll() { jobs.keys.toList().forEach(::pause) }
    fun resumeAll() = scope.launch {
        library.awaitReady()
        library.state.value.index.downloads.filter { it.status != DesktopDownloadStatus.COMPLETE }.forEach { resume(key(it.track)) }
    }

    fun remove(id: String, deleteFile: Boolean = false) = scope.launch {
        val job = jobs.remove(id)
        connections.remove(id)?.disconnect()
        job?.cancelAndJoin()
        transferLocks.computeIfAbsent(id) { Mutex() }.withLock {
            val download = library.state.value.index.downloads.firstOrNull { key(it.track) == id } ?: return@launch
            val target = target(download)
            if (owned(download)) {
                Files.deleteIfExists(File(target.path + ".part").toPath())
                if (deleteFile) Files.deleteIfExists(target.toPath())
            }
            library.updateIndex { index -> index.copy(downloads = index.downloads.filterNot { key(it.track) == id },
                tracks = if (deleteFile) index.tracks.filterNot { it.filePath == target.path } else index.tracks) }
        }
    }

    fun setDirectory(directory: File) = scope.launch {
        if (!directory.isDirectory || !directory.canWrite()) {
            library.report("Choose a writable folder for your downloads.")
            return@launch
        }
        library.updateIndex { it.copy(downloadDirectory = directory.canonicalPath) }
    }

    private suspend fun transfer(id: String) {
        library.awaitReady()
        var lastError: Exception? = null
        repeat(3) { attempt ->
            currentCoroutineContext().ensureActive()
            var download = library.state.value.index.downloads.firstOrNull { key(it.track) == id } ?: return
            if (download.status == DesktopDownloadStatus.COMPLETE) return
            try {
                val stream = try { resolver.resolve(download.track).getOrThrow() }
                catch (cancelled: CancellationException) { throw cancelled }
                catch (error: Exception) { throw DownloadFailure("YouTube couldn't provide this song's audio. Try again or choose another version.", error) }
                val extension = if (stream.mimeType.contains("webm", true)) "webm" else "m4a"
                val name = fileName(download.track, extension)
                if (download.fileName != name || (download.bitrate > 0 && download.bitrate != stream.bitrate)) {
                    if (owned(download)) Files.deleteIfExists(File(target(download).path + ".part").toPath())
                    download = download.copy(fileName = name, receivedBytes = 0, totalBytes = null)
                }
                download = download.copy(status = DesktopDownloadStatus.DOWNLOADING, mimeType = stream.mimeType,
                    bitrate = stream.bitrate, message = null)
                update(id) { download }
                val destination = target(download)
                destination.parentFile.mkdirs()
                File(destination.parentFile, ".convx-downloads").writeText("Convx managed downloads\n")
                val partial = File(destination.path + ".part")
                val source = proxy.createMediaSource(stream.url, stream.mimeType)
                RandomAccessFile(partial, "rw").use { output ->
                    var offset = output.length()
                    var total: Long? = download.totalBytes
                    if (total != null && offset > total) { output.setLength(0); offset = 0; total = null }
                    var progressTime = 0L
                    while (total == null || offset < total) {
                        currentCoroutineContext().ensureActive()
                        val end = minOf(offset + CHUNK_BYTES - 1, total?.minus(1) ?: Long.MAX_VALUE)
                        val connection = URI(source).toURL().openConnection() as HttpURLConnection
                        connections[id] = connection
                        connection.connectTimeout = 15_000
                        connection.readTimeout = 20_000
                        connection.setRequestProperty("Range", "bytes=$offset-$end")
                        try {
                            val status = connection.responseCode
                            if (status == HttpURLConnection.HTTP_OK) {
                                if (connection.contentType.orEmpty().startsWith("text/")) throw DownloadFailure("The audio server returned a page instead of music. Resume to refresh the stream.")
                                output.setLength(0); output.seek(0); offset = 0
                                val expected = connection.getHeaderFieldLong("Content-Length", -1).takeIf { it > 0 }
                                connection.inputStream.use { input ->
                                    val buffer = ByteArray(32 * 1024)
                                    while (true) {
                                        currentCoroutineContext().ensureActive()
                                        val count = input.read(buffer)
                                        if (count < 0) break
                                        output.write(buffer, 0, count); offset += count
                                        if (System.currentTimeMillis() - progressTime >= 1_000) {
                                            update(id) { it.copy(receivedBytes = offset, totalBytes = expected) }
                                            progressTime = System.currentTimeMillis()
                                        }
                                    }
                                }
                                if (offset == 0L || (expected != null && offset != expected)) throw IOException("Incomplete audio stream")
                                total = offset
                                break
                            }
                            if (status != HttpURLConnection.HTTP_PARTIAL) throw DownloadFailure("The audio server returned HTTP $status. Resume to refresh the stream.")
                            val range = CONTENT_RANGE.matchEntire(connection.getHeaderField("Content-Range").orEmpty()) ?: throw IOException("Missing range")
                            val start = range.groupValues[1].toLong()
                            val last = range.groupValues[2].toLong()
                            val length = range.groupValues[3].toLong()
                            if (start != offset || last < start || length <= last) throw IOException("Invalid range")
                            if (total != null && total != length) { output.setLength(0); offset = 0; total = null; throw IOException("Audio source changed") }
                            total = length
                            output.seek(offset)
                            val expected = last - start + 1
                            var copied = 0L
                            connection.inputStream.use { input ->
                                val buffer = ByteArray(32 * 1024)
                                while (copied < expected) {
                                    currentCoroutineContext().ensureActive()
                                    val read = input.read(buffer, 0, minOf(buffer.size.toLong(), expected - copied).toInt())
                                    if (read < 0) throw IOException("Incomplete audio range")
                                    output.write(buffer, 0, read)
                                    copied += read
                                }
                            }
                            offset += copied
                            if (System.currentTimeMillis() - progressTime >= 1_000L || offset == total) {
                                update(id) { it.copy(status = DesktopDownloadStatus.DOWNLOADING, receivedBytes = offset, totalBytes = total) }
                                progressTime = System.currentTimeMillis()
                            }
                        } finally {
                            connections.remove(id, connection)
                            connection.disconnect()
                        }
                    }
                }
                currentCoroutineContext().ensureActive()
                Files.move(partial.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
                val storedTrack = download.track.copy(durationMs = download.track.durationMs ?: stream.durationMs)
                val localTrack = DesktopLibraryTrack(destination.toURI().toString(), destination.path, storedTrack.title, storedTrack.artists,
                    durationMs = storedTrack.durationMs, modifiedAt = destination.lastModified(),
                    bytes = destination.length(), downloadedFrom = storedTrack)
                library.updateIndex { index -> index.copy(
                    downloads = index.downloads.map { if (key(it.track) == id) it.copy(status = DesktopDownloadStatus.COMPLETE,
                        receivedBytes = destination.length(), totalBytes = destination.length(), message = null) else it },
                    tracks = (index.tracks.filterNot { it.filePath == destination.path } + localTrack)) }
                try {
                    library.cacheTracks(listOf(localTrack))
                    library.cacheArtwork(storedTrack.artworkUrl)?.let { artwork ->
                        library.updateIndex { index -> index.copy(tracks = index.tracks.map {
                            if (it.id == localTrack.id) it.copy(artworkPath = artwork) else it
                        }) }
                        library.cacheTracks(listOf(localTrack.copy(artworkPath = artwork)))
                    }
                } catch (cancelled: CancellationException) { throw cancelled }
                catch (_: Exception) { library.report("The audio was downloaded. Artwork or playlist metadata couldn't be saved; rescan to try again.") }
                return
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { lastError = error; if (attempt < 2) delay(750L * (attempt + 1)) }
        }
        throw lastError ?: IOException("Download failed")
    }

    private suspend fun update(id: String, transform: (DesktopDownload) -> DesktopDownload) = library.updateIndex { index ->
        index.copy(downloads = index.downloads.map { if (key(it.track) == id) transform(it) else it })
    }

    private fun target(download: DesktopDownload): File {
        require(download.fileName == File(download.fileName).name && download.fileName !in listOf(".", "..") && '/' !in download.fileName && '\\' !in download.fileName)
        return File(download.directory?.let(::File) ?: defaultDirectory, download.fileName).canonicalFile
    }

    private fun owned(download: DesktopDownload): Boolean {
        val file = target(download)
        return File(file.parentFile, ".convx-downloads").isFile && file.name == download.fileName
    }

    fun close() {
        connections.values.forEach(HttpURLConnection::disconnect)
        scope.cancel()
        proxy.close()
    }

    companion object {
        private const val CHUNK_BYTES = 1_048_576L
        private val CONTENT_RANGE = Regex("bytes (\\d+)-(\\d+)/(\\d+)")
        fun key(track: PlaybackRequestTrack) = "${track.providerId}:${track.id}"
        private fun fileName(track: PlaybackRequestTrack, extension: String): String {
            val title = track.title.map { if (it in "<>:\"/\\|?*" || it.code < 32) '_' else it }.joinToString("").trim(' ', '.').take(64).ifBlank { "Track" }
            val hash = MessageDigest.getInstance("SHA-256").digest(key(track).toByteArray()).take(6).joinToString("") { "%02x".format(it) }
            return "$title-$hash.$extension"
        }
    }
}

private class DownloadFailure(message: String, cause: Throwable? = null) : IOException(message, cause)
