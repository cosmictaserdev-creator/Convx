package com.convx.music.utils

import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.NetworkEmbeddedArtworkUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okio.Buffer
import java.io.File
import java.io.IOException
import java.security.MessageDigest

/** Extracts remote embedded covers only when Coil needs them. Coil caches the result. */
class NetworkEmbeddedArtworkFetcher private constructor(
    private val mediaId: String,
    private val options: Options,
    private val repository: NetworkMusicRepository,
) : Fetcher {
    override suspend fun fetch(): FetchResult =
        locks[(mediaId.hashCode() and Int.MAX_VALUE) % locks.size].withLock { fetchCover() }

    private suspend fun fetchCover(): FetchResult {
        val file = cacheFile()
        val cached = withContext(Dispatchers.IO) {
            synchronized(diskLock) {
                try {
                    file.takeIf { it.isFile && it.length() in 1..EmbeddedArt.MAX_PICTURE_BYTES.toLong() }
                        ?.also { it.setLastModified(System.currentTimeMillis()) }?.readBytes()
                } catch (_: IOException) {
                    null
                }
            }
        }
        if (cached != null) return result(cached, null, DataSource.DISK)
        return gate.withPermit {
            withContext(Dispatchers.IO) {
                repository.loadServers()
                val picture = repository.open(mediaId).use { file ->
                    val context = coroutineContext
                    val reader = object : EmbeddedArt.Reader {
                        private var remaining = MAX_TOTAL_READ_BYTES
                        override val size: Long get() = file.length.takeIf { it >= 0 } ?: Long.MAX_VALUE
                        override fun read(offset: Long, length: Int): ByteArray {
                            require(offset >= 0 && length in 0..MAX_READ_BYTES)
                            context.ensureActive()
                            if (length > remaining) throw IOException("Embedded artwork read limit reached")
                            val bytes = ByteArray(length)
                            var read = 0
                            while (read < length) {
                                context.ensureActive()
                                val count = file.read(offset + read, bytes, read, length - read)
                                if (count <= 0) break
                                read += count
                                remaining -= count
                            }
                            return if (read == length) bytes else bytes.copyOf(read)
                        }
                    }
                    EmbeddedArt.extract(reader)
                } ?: throw IOException("No supported embedded artwork")
                coroutineContext.ensureActive()
                synchronized(diskLock) {
                    try {
                        file.parentFile?.mkdirs()
                        val tmp = File.createTempFile(file.name, ".tmp", file.parentFile)
                        try {
                            tmp.writeBytes(picture.bytes)
                            tmp.renameTo(file)
                        } finally {
                            tmp.delete()
                        }
                        val files = file.parentFile?.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }.orEmpty()
                        var total = files.sumOf { it.length() }
                        for (entry in files.sortedBy { it.lastModified() }) {
                            if (total <= MAX_CACHE_BYTES) break
                            val size = entry.length()
                            if (entry.delete()) total -= size
                        }
                    } catch (_: IOException) {
                        // The fetched artwork can still be displayed when storage is full.
                    }
                }
                result(picture.bytes, picture.mime, DataSource.NETWORK)
            }
        }
    }

    private fun cacheFile(): File {
        val hash = MessageDigest.getInstance("SHA-256").digest(mediaId.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(options.context.cacheDir, "network-embedded-art/$hash")
    }

    private fun result(bytes: ByteArray, mime: String?, origin: DataSource) = SourceFetchResult(
        source = ImageSource(Buffer().apply { write(bytes) }, options.fileSystem),
        mimeType = mime,
        dataSource = origin,
    )

    class Factory(private val repository: () -> NetworkMusicRepository) : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? =
            NetworkEmbeddedArtworkUri.mediaId(data.toString())?.let {
                NetworkEmbeddedArtworkFetcher(it, options, repository())
            }
    }

    private companion object {
        const val MAX_READ_BYTES = 16 * 1024 * 1024
        const val MAX_TOTAL_READ_BYTES = 32 * 1024 * 1024
        const val MAX_CACHE_BYTES = 100L * 1024 * 1024
        val gate = Semaphore(2)
        val locks = Array(32) { Mutex() }
        val diskLock = Any()
    }
}
