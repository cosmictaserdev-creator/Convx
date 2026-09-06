package com.convx.music.modulehost

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.R
import com.convx.modulehost.DeclarativeModuleHostSnapshot
import com.convx.modulehost.ModuleValidationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * An error worth showing the user: a localized message plus the underlying
 * engine detail (kept secondary, useful for a bug report).
 */
data class ModuleHostError(
    val messageRes: Int,
    val detail: String,
)

@HiltViewModel
class DeclarativeModuleHostViewModel @Inject constructor(
    private val moduleHost: ConvxDeclarativeModuleHost,
) : ViewModel() {
    private val _snapshot = MutableStateFlow(moduleHost.snapshot())
    val snapshot: StateFlow<DeclarativeModuleHostSnapshot> = _snapshot.asStateFlow()

    /** Failure surfaced by the screen (e.g. picker read, validation). */
    private val _error = MutableStateFlow<ModuleHostError?>(null)
    val error: StateFlow<ModuleHostError?> = _error.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    fun installPackage(resolver: ContentResolver, uri: Uri) {
        mutate {
            val bytes = withContext(Dispatchers.IO) {
                resolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IllegalStateException("cannot read the selected file")
            }
            moduleHost.install(bytes)
        }
    }

    fun enable(moduleId: String) = mutate { moduleHost.enable(moduleId) }

    fun disable(moduleId: String) = mutate { moduleHost.disable(moduleId) }

    fun remove(moduleId: String) = mutate { moduleHost.remove(moduleId) }

    fun dismissError() {
        _error.value = null
    }

    private fun mutate(operation: suspend () -> DeclarativeModuleHostSnapshot) {
        viewModelScope.launch {
            _busy.value = true
            try {
                _snapshot.value = operation()
            } catch (error: ModuleAlreadyInstalledException) {
                _error.value = ModuleHostError(R.string.module_host_error_already_installed, error.message ?: "")
            } catch (error: ModuleValidationException) {
                _error.value = ModuleHostError(R.string.module_host_error_invalid_package, error.message ?: "")
            } catch (error: Exception) {
                _error.value = ModuleHostError(R.string.module_host_error_install, error.message ?: "")
            } finally {
                _busy.value = false
            }
        }
    }
}
