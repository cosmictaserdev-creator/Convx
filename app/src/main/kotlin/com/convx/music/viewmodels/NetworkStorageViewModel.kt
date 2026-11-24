/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.R
import com.convx.music.db.MusicDatabase
import com.convx.music.utils.NetworkLibraryImporter
import com.convx.storage.data.NetworkMusicRepository
import com.convx.storage.domain.NetworkProtocol
import com.convx.storage.domain.NetworkServer
import com.convx.storage.domain.SourceCredentials
import com.convx.storage.domain.StorageError
import com.convx.storage.domain.StorageException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetworkStorageViewModel(
    private val context: Context,
    private val repository: NetworkMusicRepository,
    private val database: MusicDatabase,
) : ViewModel() {

    val servers: StateFlow<List<NetworkServer>> = repository.servers

    private val _busyId = MutableStateFlow<String?>(null)
    val busyId: StateFlow<String?> = _busyId.asStateFlow()

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()

    fun load() {
        viewModelScope.launch { runCatching { repository.loadServers() } }
    }

    fun add(protocol: NetworkProtocol, name: String, address: String, user: String, password: String, domain: String) {
        viewModelScope.launch {
            try {
                val server = NetworkServer.create(protocol, name, address, SourceCredentials(user.trim(), password, domain.trim()))
                _busyId.value = server.id
                _status.value = context.getString(R.string.network_storage_testing)
                repository.test(server)
                repository.save(server)
                _status.value = context.getString(R.string.network_storage_saved, server.name)
            } catch (e: IllegalArgumentException) {
                _status.value = e.message
            } catch (e: Exception) {
                _status.value = describe(e)
            } finally {
                _busyId.value = null
            }
        }
    }

    fun test(server: NetworkServer) = run(server) {
        repository.test(server)
        context.getString(R.string.network_storage_ok, server.name)
    }

    fun scan(server: NetworkServer) = run(server) {
        _status.value = context.getString(R.string.network_storage_scanning, server.name)
        val result = NetworkLibraryImporter.import(database, repository, server.id)
        context.getString(R.string.network_storage_scanned, result.added, result.skipped) +
            if (result.truncated) context.getString(R.string.network_storage_truncated) else ""
    }

    fun delete(server: NetworkServer) {
        viewModelScope.launch {
            runCatching { repository.delete(server.id) }
                .onFailure { _status.value = describe(it) }
        }
    }

    private fun run(server: NetworkServer, block: suspend () -> String) {
        viewModelScope.launch {
            _busyId.value = server.id
            _status.value = try {
                block()
            } catch (e: Exception) {
                describe(e)
            } finally {
                _busyId.value = null
            }
        }
    }

    private fun describe(error: Throwable): String = when ((error as? StorageException)?.error) {
        StorageError.AUTHENTICATION -> context.getString(R.string.network_storage_err_auth)
        StorageError.NOT_FOUND -> context.getString(R.string.network_storage_err_not_found)
        StorageError.CONNECTION -> context.getString(R.string.network_storage_err_connection)
        StorageError.LIMIT_REACHED -> context.getString(R.string.network_storage_err_limit)
        else -> error.message ?: context.getString(R.string.network_storage_err_connection)
    }
}
