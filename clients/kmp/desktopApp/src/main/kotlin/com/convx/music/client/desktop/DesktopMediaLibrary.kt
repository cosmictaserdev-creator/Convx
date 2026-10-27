package com.convx.music.client.desktop

import com.convx.music.catalog.*
import com.convx.music.library.*
import com.convx.music.playback.PlaybackRequestTrack
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bytedeco.javacv.FFmpegFrameGrabber
import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest

internal const val LOCAL_MUSIC_PROVIDER = "local"

@Serializable
internal data class DesktopLibraryTrack(
    val id: String,
    val filePath: String,
    val title: String,
    val artists: List<String> = emptyList(),
    val album: String? = null,
    val durationMs: Long? = null,
    val artworkPath: String? = null,
    val modifiedAt: Long = 0,
    val bytes: Long = 0,
    val addedAt: Long = System.currentTimeMillis(),
    val favorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayedAt: Long? = null,
    val downloadedFrom: PlaybackRequestTrack? = null,
    val lyricsText: String? = null,
) {
    fun request() = downloadedFrom ?: PlaybackRequestTrack(LOCAL_MUSIC_PROVIDER, id, title, artists, durationMs,
        artworkPath?.let { File(it).toURI().toString() })

    fun catalogEntry() = CatalogEntry.Track(CatalogId(LOCAL_MUSIC_PROVIDER, id), title,
        listOfNotNull(artworkPath?.let { File(it).toURI().toString() }), artists.map { CatalogArtist(null, it) },
        album?.let { CatalogAlbumReference(null, it) }, durationMs, false, false, true, favorite)
}

@Serializable
internal enum class DesktopDownloadStatus { QUEUED, DOWNLOADING, PAUSED, COMPLETE, FAILED }

@Serializable
internal data class DesktopDownload(
    val track: PlaybackRequestTrack,
    val fileName: String,
    val status: DesktopDownloadStatus = DesktopDownloadStatus.QUEUED,
    val receivedBytes: Long = 0,
    val totalBytes: Long? = null,
    val mimeType: String? = null,
    val message: String? = null,
    val directory: String? = null,
    val bitrate: Int = 0,
)

@Serializable
internal data class DesktopMediaIndex(
    val folders: List<String> = emptyList(),
    val tracks: List<DesktopLibraryTrack> = emptyList(),
    val downloads: List<DesktopDownload> = emptyList(),
    val downloadDirectory: String? = null,
    val files: List<String> = emptyList(),
)

internal data class DesktopMediaLibraryState(
    val index: DesktopMediaIndex = DesktopMediaIndex(),
    val scanning: Boolean = false,
    val scannedFiles: Int = 0,
    val totalFiles: Int = 0,
    val message: String? = null,
)

/** Filesystem adapter; folder removal only updates the index, never the user's media files. */
internal class DesktopMediaLibrary(
    private val indexFile: File,
    private val artworkDirectory: File,
    private val writer: MusicLibraryWriter,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val ready = CompletableDeferred<Unit>()
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = true }
    private val mutableState = MutableStateFlow(DesktopMediaLibraryState())
    val state = mutableState.asStateFlow()
    private var scanning: Job? = null
    private val scanGeneration = java.util.concurrent.atomic.AtomicInteger()

    init {
        scope.launch {
            val index = readIndex(indexFile) ?: readIndex(File(indexFile.path + ".bak")) ?: DesktopMediaIndex()
            mutableState.value = DesktopMediaLibraryState(index.copy(downloads = index.downloads.map {
                if (it.status in listOf(DesktopDownloadStatus.DOWNLOADING, DesktopDownloadStatus.QUEUED)) it.copy(status = DesktopDownloadStatus.PAUSED) else it
            }))
            ready.complete(Unit)
        }
    }

    private fun readIndex(file: File): DesktopMediaIndex? = try {
        if (file.isFile) json.decodeFromString<DesktopMediaIndex>(file.readText()) else null
    } catch (_: Exception) { null }

    suspend fun awaitReady() = ready.await()
    fun report(message: String) { mutableState.update { it.copy(message = message) } }

    suspend fun cacheArtwork(url: String?): String? = withContext(Dispatchers.IO) {
        if (url.isNullOrBlank() || !url.startsWith("https://")) return@withContext null
        val digest = MessageDigest.getInstance("SHA-256").digest(url.toByteArray()).joinToString("") { "%02x".format(it) }
        val destination = File(artworkDirectory, "download-$digest.jpg")
        if (destination.isFile) return@withContext destination.path
        for (candidate in com.convx.music.designsystem.artworkUrlCandidates(url, 768)) {
            ensureActive()
            val connection = try { java.net.URI(candidate).toURL().openConnection() as java.net.HttpURLConnection } catch (_: Exception) { continue }
            try {
                connection.connectTimeout = 5_000
                connection.readTimeout = 5_000
                if (connection.responseCode != 200 || !connection.contentType.orEmpty().startsWith("image/")) continue
                val bytes = connection.inputStream.use { it.readNBytes(4 * 1024 * 1024 + 1) }
                if (bytes.isEmpty() || bytes.size > 4 * 1024 * 1024) continue
                artworkDirectory.mkdirs()
                destination.writeBytes(bytes)
                return@withContext destination.path
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { }
            finally { connection.disconnect() }
        }
        null
    }

    suspend fun cacheTracks(tracks: List<DesktopLibraryTrack>) {
        writer.saveTracks(tracks.map { track -> PlaylistSyncTrack(track.id, track.title,
            track.artists.map { PlaylistSyncArtist(null, it) }, track.durationMs?.div(1_000)?.toInt(),
            track.artworkPath?.let { File(it).toURI().toString() }, track.album?.let { PlaylistSyncAlbum("local:${it.lowercase()}", it) },
            null, null, false, null, null, false) })
    }

    suspend fun updateIndex(transform: (DesktopMediaIndex) -> DesktopMediaIndex) {
        ready.await()
        mutex.withLock {
            val next = transform(mutableState.value.index)
            indexFile.parentFile.mkdirs()
            val temporary = File(indexFile.path + ".tmp")
            temporary.writeText(json.encodeToString(next))
            if (indexFile.isFile) Files.copy(indexFile.toPath(), File(indexFile.path + ".bak").toPath(), StandardCopyOption.REPLACE_EXISTING)
            try {
                Files.move(temporary.toPath(), indexFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temporary.toPath(), indexFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            mutableState.update { it.copy(index = next) }
        }
    }

    fun addFolder(folder: File) = scope.launch {
        try {
            val path = folder.canonicalFile
            if (!path.isDirectory || !path.canRead()) {
                report("This folder is unavailable. Choose a readable music folder.")
                return@launch
            }
            updateIndex { it.copy(folders = (it.folders + path.path).distinct()) }
            rescan()
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { report("Couldn't save your music folder. Check the app's storage and try again.") }
    }

    suspend fun importFiles(files: List<File>): List<DesktopLibraryTrack> = withContext(Dispatchers.IO) {
        ready.await()
        val imported = files.asSequence().take(256).map { it.canonicalFile }
            .filter { it.isFile && it.canRead() && it.extension.lowercase() in AUDIO_EXTENSIONS }
            .distinctBy { it.path }.map { file -> ensureActive(); readTrack(file) }.toList()
        if (imported.isEmpty()) return@withContext emptyList()
        updateIndex { current ->
            val previous = current.tracks.associateBy { it.id }
            current.copy(files = (current.files + imported.map { it.filePath }).distinct(),
                tracks = (current.tracks + imported.map { track -> previous[track.id] ?: track }).distinctBy { it.id })
        }
        cacheTracks(imported)
        imported
    }

    fun removeFolder(folder: String) = scope.launch {
        try {
            updateIndex { it.copy(folders = it.folders - folder) }
            rescan()
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { report("Couldn't remove this folder from your library. Try again.") }
    }

    fun setFavorite(id: String, favorite: Boolean) = scope.launch {
        try {
            updateIndex { it.copy(tracks = it.tracks.map { track -> if (track.id == id) track.copy(favorite = favorite) else track }) }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { report("Couldn't update Liked Music. Try again.") }
    }

    fun recordPlayed(track: PlaybackRequestTrack) = scope.launch {
        ready.await()
        if (mutableState.value.index.tracks.none { it.request().id == track.id && it.request().providerId == track.providerId }) return@launch
        updateIndex { it.copy(tracks = it.tracks.map { item ->
            if (item.request().id == track.id && item.request().providerId == track.providerId)
                item.copy(playCount = item.playCount + 1, lastPlayedAt = System.currentTimeMillis()) else item
        }) }
    }

    fun downloadedFile(track: PlaybackRequestTrack): File? = mutableState.value.index.tracks.firstOrNull {
        it.downloadedFrom?.id == track.id && it.downloadedFrom.providerId == track.providerId
    }?.filePath?.let(::File)?.takeIf { it.isFile }

    @Synchronized
    fun rescan() {
        scanning?.cancel()
        val generation = scanGeneration.incrementAndGet()
        scanning = scope.launch {
            ready.await()
            mutableState.update { it.copy(scanning = true, scannedFiles = 0, totalFiles = 0, message = null) }
            try {
                val initial = mutableState.value.index
                val files = initial.files.map(::File).filter { it.isFile && it.canRead() }.toMutableList()
                val unavailable = mutableListOf<Path>()
                initial.folders.forEach { root ->
                    val path = Path.of(root)
                    if (!Files.isDirectory(path) || !Files.isReadable(path)) { unavailable.add(path); return@forEach }
                    Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                        override fun visitFile(file: Path, attributes: BasicFileAttributes): FileVisitResult {
                            ensureActive()
                            if (attributes.isRegularFile && file.fileName.toString().substringAfterLast('.').lowercase() in AUDIO_EXTENSIONS) files.add(file.toFile())
                            return FileVisitResult.CONTINUE
                        }
                        override fun visitFileFailed(file: Path, error: java.io.IOException) = FileVisitResult.CONTINUE
                    })
                }
                val uniqueFiles = files.distinctBy { it.canonicalPath }
                mutableState.update { it.copy(totalFiles = uniqueFiles.size) }
                val existing = initial.tracks.associateBy { it.filePath }
                val scanned = ArrayList<DesktopLibraryTrack>(uniqueFiles.size)
                var lastPublish = System.currentTimeMillis()
                uniqueFiles.forEachIndexed { index, file ->
                    ensureActive()
                    val old = existing[file.path]
                    val result = if (old != null && old.modifiedAt == file.lastModified() && old.bytes == file.length()) old else readTrack(file)
                    scanned.add(result)
                    mutableState.update { it.copy(scannedFiles = index + 1) }
                    if (index == 0 || (index + 1) % 20 == 0 || System.currentTimeMillis() - lastPublish >= 1_000) {
                        val batch = scanned.toList()
                        updateIndex { current ->
                            val byId = current.tracks.associateBy { it.id }
                            current.copy(tracks = (current.tracks + batch.map { track -> byId[track.id]?.let { previous ->
                                track.copy(favorite = previous.favorite, playCount = previous.playCount, lastPlayedAt = previous.lastPlayedAt, addedAt = previous.addedAt)
                            } ?: track }).associateBy { it.id }.values.toList())
                        }
                        lastPublish = System.currentTimeMillis()
                    }
                }
                updateIndex { current ->
                    val byId = current.tracks.associateBy { it.id }
                    val retained = current.tracks.filter { it.downloadedFrom != null || it.filePath in current.files || unavailable.any { root -> Path.of(it.filePath).startsWith(root) } }
                    current.copy(tracks = (retained + scanned.map { track -> byId[track.id]?.let { old ->
                        track.copy(favorite = old.favorite, playCount = old.playCount, lastPlayedAt = old.lastPlayedAt, addedAt = old.addedAt)
                    } ?: track }).distinctBy { it.id })
                }
                cacheTracks(mutableState.value.index.tracks)
                mutableState.update { it.copy(message = if (unavailable.isNotEmpty()) "Some music folders are unavailable. Their library entries have been kept." else null) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(message = "Couldn't finish scanning. Check the selected folders and try again.") } }
            finally { if (generation == scanGeneration.get()) mutableState.update { it.copy(scanning = false) } }
        }
    }

    private fun readTrack(file: File): DesktopLibraryTrack {
        val id = file.toURI().toString()
        val base = DesktopLibraryTrack(id, file.path, file.nameWithoutExtension, modifiedAt = file.lastModified(), bytes = file.length())
        return try {
            FFmpegFrameGrabber(file).use { grabber ->
                grabber.start()
                val tags = grabber.metadata.entries.associate { it.key.lowercase() to it.value }
                val cover = embeddedArtwork(grabber, file) ?: listOf("cover.jpg", "folder.jpg", "cover.png", "folder.png")
                    .map { File(file.parentFile, it) }.firstOrNull { it.isFile }?.path
                base.copy(title = tags["title"]?.takeIf { it.isNotBlank() } ?: base.title,
                    artists = (tags["artist"] ?: tags["album_artist"])?.split(';')?.map(String::trim)?.filter(String::isNotBlank).orEmpty(),
                    album = tags["album"]?.takeIf { it.isNotBlank() }, durationMs = (grabber.lengthInTime / 1_000).takeIf { it > 0 }, artworkPath = cover,
                    lyricsText = (tags["lyrics"] ?: tags["unsyncedlyrics"])?.take(100_000))
            }
        } catch (error: Exception) {
            if (Thread.currentThread().isInterrupted) throw CancellationException("Scanning cancelled", error)
            base
        }
    }

    private fun embeddedArtwork(grabber: FFmpegFrameGrabber, file: File): String? {
        val context = grabber.formatContext ?: return null
        for (index in 0 until context.nb_streams()) {
            val stream = context.streams(index)
            val packet = stream.attached_pic()
            val size = packet.size()
            if (size <= 0 || size > 4 * 1024 * 1024 || packet.data() == null) continue
            val bytes = ByteArray(size)
            packet.data().get(bytes)
            artworkDirectory.mkdirs()
            val digest = MessageDigest.getInstance("SHA-256").digest("${file.path}:${file.lastModified()}".toByteArray()).joinToString("") { "%02x".format(it) }
            val cover = File(artworkDirectory, "$digest.${if (bytes.firstOrNull() == 0x89.toByte()) "png" else "jpg"}")
            cover.writeBytes(bytes)
            return cover.path
        }
        return null
    }

    fun close() = scope.cancel()

    companion object {
        val AUDIO_EXTENSIONS = setOf("mp3", "m4a", "aac", "flac", "wav", "webm", "ogg", "opus", "wma", "aiff", "aif", "alac", "ape")
    }
}
