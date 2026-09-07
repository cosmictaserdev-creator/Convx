package com.convx.music.modulehost

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.R
import com.convx.modulehost.DeclarativeModuleHostSnapshot
import com.convx.modulehost.ModuleRelease
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

/** A newer published release found by the update check, offered for confirmation. */
data class UpdateOffer(
    val release: ModuleRelease,
    val installedVersion: String,
    val moduleName: String,
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

    private val coordinator = ModuleUpdateCoordinator(moduleHost)

    private val _updateOffer = MutableStateFlow<UpdateOffer?>(null)
    val updateOffer: StateFlow<UpdateOffer?> = _updateOffer.asStateFlow()

    /** Message resource for a neutral OK-only info dialog (e.g. up to date). */
    private val _updateInfo = MutableStateFlow<Int?>(null)
    val updateInfo: StateFlow<Int?> = _updateInfo.asStateFlow()

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

    fun dismissUpdateOffer() {
        _updateOffer.value = null
    }

    fun dismissUpdateInfo() {
        _updateInfo.value = null
    }

    /** Check the module's release feed and surface an offer or an info dialog. */
    fun checkForUpdates(moduleId: String) {
        viewModelScope.launch {
            _busy.value = true
            try {
                val result = withContext(Dispatchers.IO) { coordinator.checkForUpdates(moduleId) }
                when (result) {
                    is ModuleUpdateCoordinator.CheckResult.Newer -> {
                        val installed = moduleHost.snapshot().modules
                            .firstOrNull { it.manifest.id == moduleId }
                        _updateOffer.value = UpdateOffer(
                            release = result.release,
                            installedVersion = result.installedVersion,
                            moduleName = installed?.manifest?.name ?: moduleId,
                        )
                    }
                    ModuleUpdateCoordinator.CheckResult.UpToDate ->
                        _updateInfo.value = R.string.module_host_up_to_date
                    ModuleUpdateCoordinator.CheckResult.NoCompatibleRelease ->
                        _updateInfo.value = R.string.module_host_no_compatible
                    ModuleUpdateCoordinator.CheckResult.ModuleNotInstalled -> Unit
                }
            } catch (error: Exception) {
                _error.value = ModuleHostError(R.string.module_host_update_failed, error.message ?: "")
            } finally {
                _busy.value = false
            }
        }
    }

    /** Confirm an offered update: download, verify the digest, upgrade through the host. */
    fun applyUpdate() {
        val offer = _updateOffer.value ?: return
        viewModelScope.launch {
            _busy.value = true
            try {
                _snapshot.value = withContext(Dispatchers.IO) { coordinator.applyUpdate(offer.release) }
                _updateOffer.value = null
            } catch (error: ModuleDowngradeRejectedException) {
                _error.value = ModuleHostError(R.string.module_host_error_downgrade, error.message ?: "")
            } catch (error: Exception) {
                _error.value = ModuleHostError(R.string.module_host_update_failed, error.message ?: "")
            } finally {
                _busy.value = false
            }
        }
    }

    private fun mutate(operation: suspend () -> DeclarativeModuleHostSnapshot) {
        viewModelScope.launch {
            _busy.value = true
            try {
                _snapshot.value = operation()
            } catch (error: ModuleDowngradeRejectedException) {
                _error.value = ModuleHostError(R.string.module_host_error_downgrade, error.message ?: "")
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
