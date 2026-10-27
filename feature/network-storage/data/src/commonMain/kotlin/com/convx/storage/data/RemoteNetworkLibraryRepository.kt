package com.convx.storage.data

import com.convx.storage.domain.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/** Coordinates saved sources, protocol readers, scanning and bounded artwork reads. */
class RemoteNetworkLibraryRepository(
    private val store: NetworkServerStore,
    private val webDav: NetworkFileSource,
    private val smb: NetworkFileSource,
    private val io: CoroutineDispatcher = Dispatchers.Default,
) : NetworkLibraryRepository, NetworkMediaAccess, AutoCloseable {
    override val servers = store.servers
    override suspend fun loadServers() = store.load()
    override suspend fun save(server: NetworkServer) {
        store.save(server)
        invalidate(server.id)
    }
    override suspend fun delete(id: String) {
        store.delete(id)
        invalidate(id)
    }
    override suspend fun test(server: NetworkServer) { source(server).list(server, "") }
    override suspend fun browse(id: String, folder: String): List<NetworkEntry> {
        val server = findLoaded(id)
        return source(server).list(server, RemotePath.normalize(folder))
    }
    override suspend fun scan(id: String, maxTracks: Int): NetworkLibrary = withContext(io) {
        val server = findLoaded(id)
        NetworkLibraryScanner { folder -> source(server).list(server, folder) }.scan(id, maxTracks)
    }

    override fun open(uri: String): RemoteFile {
        val location = NetworkMediaUri.parse(uri) ?: throw StorageException(StorageError.INVALID_CONFIGURATION)
        val server = store.find(location.serverId) ?: throw StorageException(StorageError.NOT_FOUND)
        return source(server).open(server, location.path)
    }

    override suspend fun readArtwork(uri: String, maxBytes: Int): ByteArray = withContext(io) {
        require(maxBytes in 1..16 * 1024 * 1024)
        store.load()
        open(uri).use { file ->
            if (file.length > maxBytes) throw StorageException(StorageError.LIMIT_REACHED)
            val chunks = mutableListOf<ByteArray>()
            val buffer = ByteArray(minOf(64 * 1024, maxBytes + 1))
            var size = 0
            while (true) {
                coroutineContext.ensureActive()
                val count = file.read(size.toLong(), buffer, 0, minOf(buffer.size, maxBytes - size + 1))
                if (count <= 0) break
                if (count > maxBytes - size) throw StorageException(StorageError.LIMIT_REACHED)
                chunks += buffer.copyOf(count)
                size += count
            }
            ByteArray(size).also { result ->
                var offset = 0
                chunks.forEach { chunk -> chunk.copyInto(result, offset); offset += chunk.size }
            }
        }
    }

    override fun close() {
        try { webDav.close() } finally { smb.close() }
    }
    private suspend fun findLoaded(id: String): NetworkServer {
        store.load()
        return store.find(id) ?: throw StorageException(StorageError.NOT_FOUND)
    }
    private fun source(server: NetworkServer) = when (server) {
        is NetworkServer.WebDav -> webDav
        is NetworkServer.Smb -> smb
    }
    private fun invalidate(id: String) { webDav.invalidate(id); smb.invalidate(id) }
}
