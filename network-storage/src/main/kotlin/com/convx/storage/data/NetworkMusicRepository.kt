package com.convx.storage.data

import androidx.media3.datasource.DataSource
import com.convx.storage.domain.*
import com.convx.storage.playback.NetworkDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.coroutineContext

class NetworkMusicRepository(
    private val store: NetworkServerStore,
    private val webDav: WebDavFileSource = WebDavFileSource(),
    private val smb: SmbFileSource = SmbFileSource(),
) : NetworkLibraryRepository {
    override val servers = store.servers
    override suspend fun loadServers() = store.load()
    override suspend fun save(server: NetworkServer) { store.save(server); smb.invalidate(server.id) }
    override suspend fun delete(id: String) { store.delete(id); smb.invalidate(id) }
    override suspend fun test(server: NetworkServer) { source(server).list(server, "") }
    override suspend fun browse(id: String, folder: String): List<NetworkEntry> {
        val server = store.find(id) ?: throw StorageException(StorageError.NOT_FOUND)
        return source(server).list(server, RemotePath.normalize(folder))
    }

    override suspend fun scan(id: String, maxTracks: Int): NetworkLibrary = withContext(Dispatchers.IO) {
        require(maxTracks in 1..100_000)
        val server = store.find(id) ?: throw StorageException(StorageError.NOT_FOUND)
        val files = LinkedHashMap<String, NetworkEntry>()
        val images = LinkedHashMap<String, NetworkEntry>()
        val visited = HashSet<String>()
        val folders = ArrayDeque<String>().apply { add("") }
package com.convx.storage.data

import androidx.media3.datasource.DataSource
import com.convx.storage.domain.*
import com.convx.storage.playback.NetworkDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.coroutineContext

class NetworkMusicRepository(
    private val store: NetworkServerStore,
    private val webDav: WebDavFileSource = WebDavFileSource(),
    private val smb: SmbFileSource = SmbFileSource(),
    private val navidrome: NavidromeFileSource = NavidromeFileSource(),
) : NetworkLibraryRepository {
    override val servers = store.servers
    override suspend fun loadServers() = store.load()
    override suspend fun save(server: NetworkServer) { store.save(server); smb.invalidate(server.id) }
    override suspend fun delete(id: String) { store.delete(id); smb.invalidate(id) }
    override suspend fun test(server: NetworkServer) { source(server).list(server, "") }
    override suspend fun browse(id: String, folder: String): List<NetworkEntry> {
        val server = store.find(id) ?: throw StorageException(StorageError.NOT_FOUND)
        return source(server).list(server, RemotePath.normalize(folder))
    }

    override suspend fun scan(id: String, maxTracks: Int): NetworkLibrary = withContext(Dispatchers.IO) {
        require(maxTracks in 1..100_000)
        val server = store.find(id) ?: throw StorageException(StorageError.NOT_FOUND)
        if (server is NetworkServer.Navidrome) return@withContext navidrome.scan(server, maxTracks)
        val files = LinkedHashMap<String, NetworkEntry>()
        val images = LinkedHashMap<String, NetworkEntry>()
        val visited = HashSet<String>()
        val folders = ArrayDeque<String>().apply { add("") }
        var truncated = false
        while (folders.isNotEmpty()) {
            coroutineContext.ensureActive()
            val folder = folders.removeFirst()
            if (!visited.add(folder)) continue
            if (visited.size > 10_000) { truncated = true; break }
            for (entry in source(server).list(server, folder)) {
                coroutineContext.ensureActive()
                when {
                    entry.directory -> if (entry.path !in visited) folders.add(entry.path)
                    NetworkMedia.isAudio(entry.name) -> {
                        if (files.size >= maxTracks) { truncated = true; break }
                        files[entry.path] = entry
                    }
                    NetworkMedia.isImage(entry.name) -> if (images.size < 10_000) images[entry.path] = entry
                }
            }
            if (truncated) break
        }
        val byFolder = images.values.groupBy { RemotePath.parent(it.path) }
        NetworkLibrary(files.values.map { NetworkMedia.track(id, it, byFolder[RemotePath.parent(it.path)].orEmpty()) }, truncated)
    }

    override suspend fun readArtwork(uri: String, maxBytes: Int): ByteArray = withContext(Dispatchers.IO) {
        require(maxBytes in 1..16 * 1024 * 1024)
        val location = NetworkMediaUri.parse(uri) ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        val server = store.find(location.serverId) ?: throw StorageException(StorageError.NOT_FOUND)
        source(server).open(server, location.path).use { file ->
            if (file.length > maxBytes) throw StorageException(StorageError.LIMIT_REACHED)
            val bytes = ByteArrayOutputStream()
            val buffer = ByteArray(64 * 1024)
            var position = 0L
            while (true) {
                coroutineContext.ensureActive()
                val count = file.read(position, buffer, 0, minOf(buffer.size.toLong(), maxBytes - position + 1).toInt())
                if (count < 0 || count == 0) break
                position += count
                if (position > maxBytes) throw StorageException(StorageError.LIMIT_REACHED)
                bytes.write(buffer, 0, count)
            }
            bytes.toByteArray()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun dataSourceFactory(downstream: DataSource.Factory): DataSource.Factory = NetworkDataSourceFactory(downstream, store, webDav, smb, navidrome)

    private fun source(server: NetworkServer): NetworkFileSource = when (server) {
        is NetworkServer.WebDav -> webDav
        is NetworkServer.Smb -> smb
        is NetworkServer.Navidrome -> navidrome
    }
}
while (true) {
                coroutineContext.ensureActive()
                val count = file.read(position, buffer, 0, minOf(buffer.size.toLong(), maxBytes - position + 1).toInt())
                if (count < 0 || count == 0) break
                position += count
                if (position > maxBytes) throw StorageException(StorageError.LIMIT_REACHED)
                bytes.write(buffer, 0, count)
            }
            bytes.toByteArray()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun dataSourceFactory(downstream: DataSource.Factory): DataSource.Factory = NetworkDataSourceFactory(downstream, store, webDav, smb)

    private fun source(server: NetworkServer): NetworkFileSource = when (server) {
        is NetworkServer.WebDav -> webDav
        is NetworkServer.Smb -> smb
    }
}
