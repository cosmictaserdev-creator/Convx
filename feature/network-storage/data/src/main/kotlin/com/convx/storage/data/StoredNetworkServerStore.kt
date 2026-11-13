package com.convx.storage.data

import com.convx.storage.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Publishes a saved source only after its credential-protected snapshot commits. */
class StoredNetworkServerStore(private val snapshot: NetworkSnapshotStore) : NetworkServerStore {
    private val state = MutableStateFlow<List<NetworkServer>>(emptyList())
    override val servers = state.asStateFlow()
    private val lock = Mutex()
    private var loaded = false
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun load(): List<NetworkServer> = lock.withLock { loadLocked() }
    override fun find(id: String): NetworkServer? = state.value.firstOrNull { it.id == id }

    override suspend fun save(server: NetworkServer) = lock.withLock {
        // Validate even callers that construct a source directly instead of using the form.
        val validated = NetworkServer.create(server.protocol, server.name, server.address, server.credentials, server.id)
        val current = loadLocked()
        require(current.size < 16 || current.any { it.id == validated.id }) { "Too many network sources" }
        persist(current.filterNot { it.id == validated.id } + validated)
    }

    override suspend fun delete(id: String) = lock.withLock { persist(loadLocked().filterNot { it.id == id }) }

    private suspend fun loadLocked(): List<NetworkServer> {
        if (loaded) return state.value
        val decoded = try {
            snapshot.read()?.let { value ->
                val sources = json.decodeFromString<List<ServerRecord>>(value).map { it.toServer() }
                require(sources.size <= 16 && sources.map { it.id }.distinct().size == sources.size)
                sources
            }.orEmpty()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            throw StorageException(StorageError.CREDENTIALS_UNAVAILABLE, failure)
        }
        state.value = decoded
        loaded = true
        return decoded
    }

    private suspend fun persist(next: List<NetworkServer>) {
        snapshot.write(json.encodeToString(ListSerializer, next.map(ServerRecord::from)))
        state.value = next
        loaded = true
    }

    // Matches Android's existing encrypted source-record fields. Plaintext never goes
    // to a filesystem from this class; the platform snapshot adapter owns encryption.
    @Serializable
    private data class ServerRecord(
        val id: String, val name: String, val protocol: String, val address: String,
        val username: String, val password: String, val domain: String,
    ) {
        fun toServer() = NetworkServer.create(NetworkProtocol.valueOf(protocol), name, address,
            SourceCredentials(username, password, domain), id)
        companion object {
            fun from(server: NetworkServer) = ServerRecord(server.id, server.name, server.protocol.name,
                server.address, server.credentials.username, server.credentials.password, server.credentials.domain)
        }
    }

    private companion object {
        val ListSerializer = kotlinx.serialization.builtins.ListSerializer(ServerRecord.serializer())
    }
}
