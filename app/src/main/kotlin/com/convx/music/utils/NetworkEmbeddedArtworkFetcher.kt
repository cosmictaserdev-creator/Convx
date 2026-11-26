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
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okio.Buffer
import java.io.IOException
import java.io.File
import java.security.MessageDigest

/** Extracts remote embedded covers only when Coil needs them. Coil caches the result. */
class NetworkEmbeddedArtworkFetcher private constructor(
    private val mediaId: String,
    private val options: Options,
    private val repository: NetworkMusicRepository,
) : Fetcher {
    override suspend fun fetch(): FetchResult = locks[(mediaId.hashCode() and Int.MAX_VALUE) % locks.size].withLock {
        val file = cacheFile()
        val cached = withContext(Dispatchers.IO) {
            file.takeIf { it.isFile && it.length() in 1..EmbeddedArt.MAX_PICTURE_BYTES.toLong() }
                ?.also { it.setLastModified(System.currentTimeMillis()) }?.readBytes()
        }
        if (cached != null) return@withLock result(cached, null, DataSource.DISK)
        gate.withPermit {
        withContext(Dispatchers.IO) {
            repository.loadServers()
            val picture = repository.open(mediaId).use { file ->
                val reader = object : EmbeddedArt.Reader {
                    override val size: Long get() = file.length.takeIf { it >= 0 } ?: Long.MAX_VALUE
                    override fun read(offset: Long, length: Int): ByteArray {
                        require(offset >= 0 && length in 0..MAX_READ_BYTES)
                        val bytes = ByteArray(length)
                        var read = 0
                        while (read < length) {
                            val count = file.read(offset + read, bytes, read, length - read)
                            if (count <= 0) break
                            read += count
                        }
                        return if (read == length) bytes else bytes.copyOf(read)
                    }
                }
                EmbeddedArt.extract(reader)
            } ?: throw IOException("No supported embedded artwork")
            synchronized(diskLock) {
                runCatching {
                    file.parentFile?.mkdirs()
                    val tmp = File(file.parentFile, "${file.name}.${System.nanoTime()}.tmp")
                    try { tmp.writeBytes(picture.bytes); if (!tmp.renameTo(file)) tmp.delete() }
                    finally { if (tmp.exists()) tmp.delete() }
                    val files = file.parentFile?.listFiles()?.filter { it.isFile && !it.name.endsWith(".tmp") }.orEmpty()
                    var total = files.sumOf { it.length() }
                    for (entry in files.sortedBy { it.lastModified() }) {
                        if (total <= MAX_CACHE_BYTES) break
                        val size = entry.length()
                        if (entry.delete()) total -= size
                    }
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
        const val MAX_CACHE_BYTES = 100L * 1024 * 1024
        val gate = Semaphore(2)
        val locks = Array(32) { Mutex() }
        val diskLock = Any()
    }
}
