package com.convx.storage.presentation

import com.convx.storage.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update

data class NetworkLibraryState(
    val sources: List<NetworkServer> = emptyList(),
    val selectedId: String? = null,
    val folder: String = "",
    val entries: List<NetworkEntry> = emptyList(),
    val tracks: List<NetworkTrack> = emptyList(),
    val busy: Boolean = false,
    val ready: Boolean = false,
    val truncated: Boolean = false,
    val message: String? = null,
)

class NetworkLibraryStateHolder(private val library: NetworkLibraryRepository, private val scope: CoroutineScope) : AutoCloseable {
    private val mutableState = MutableStateFlow(NetworkLibraryState())
    val state = mutableState.asStateFlow()
    private var generation = 0L
    private var operation: Job? = null
    private val sourcesJob = scope.launch {
        library.servers.collect { sources ->
            mutableState.update { current ->
                if (current.selectedId != null && sources.none { it.id == current.selectedId })
                    current.copy(sources = sources, selectedId = null, entries = emptyList(), tracks = emptyList())
                else current.copy(sources = sources)
            }
        }
    }

    fun load() = action { token ->
        library.loadServers()
        publish(token) { it.copy(ready = true) }
    }
    fun browse(id: String, folder: String = "") {
        mutableState.update { it.copy(selectedId = id, folder = folder, tracks = emptyList(), entries = emptyList(), truncated = false) }
        action { token ->
            val entries = library.browse(id, folder)
            val images = entries.filter { !it.directory && NetworkMedia.isImage(it.name) }
            publish(token) { it.copy(entries = entries, tracks = entries.filter { !it.directory && NetworkMedia.isAudio(it.name) }
                .map { entry -> NetworkMedia.track(id, entry, images) }) }
        }
    }
    fun scan(id: String) {
        mutableState.update { it.copy(selectedId = id, folder = "", entries = emptyList(), tracks = emptyList()) }
        action { token ->
            val result = library.scan(id)
            publish(token) { it.copy(tracks = result.tracks, truncated = result.truncated) }
        }
    }
    fun save(protocol: NetworkProtocol, name: String, address: String, credentials: SourceCredentials,
        id: String? = null, onSaved: () -> Unit = {}) = action { token ->
        val server = if (id == null) NetworkServer.create(protocol, name, address, credentials)
            else NetworkServer.create(protocol, name, address, credentials, id)
        library.save(server)
        publish(token) { it.copy(message = "Source saved") }
        if (generation == token) onSaved()
    }
    fun remove(id: String) = action { token ->
        library.delete(id)
        publish(token) { it.copy(message = "Source removed") }
    }
    fun test(server: NetworkServer) = action { token ->
        library.test(server)
        publish(token) { it.copy(message = "Connected") }
    }
    fun cancel() {
        generation++
        operation?.cancel()
        mutableState.update { it.copy(busy = false) }
    }
    override fun close() { cancel(); sourcesJob.cancel() }

    private fun action(block: suspend (Long) -> Unit) {
        operation?.cancel()
        val token = ++generation
        mutableState.update { it.copy(busy = true, message = null) }
        operation = scope.launch {
            try { block(token) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) { publish(token) { it.copy(message = failure.storageMessage()) } }
            finally { publish(token) { it.copy(busy = false) } }
        }
    }
    private fun publish(token: Long, update: (NetworkLibraryState) -> NetworkLibraryState) {
        if (generation == token) mutableState.update(update)
    }
}

private fun Exception.storageMessage(): String = when ((this as? StorageException)?.error) {
    StorageError.AUTHENTICATION -> "Login failed. Check the username, password and domain."
    StorageError.CREDENTIALS_UNAVAILABLE -> "Could not access saved credentials. Your saved sources have been preserved."
    StorageError.NOT_FOUND -> "This source or file is no longer available."
    StorageError.LIMIT_REACHED -> "The source response exceeds the supported size."
    StorageError.INVALID_RESPONSE -> "The server returned an unsupported response."
    StorageError.INVALID_CONFIGURATION -> "Check the server address and share or folder."
    else -> if (this is IllegalArgumentException) "Check the source name, address and login fields."
        else "Could not connect to this source. Check the server and network connection."
}
