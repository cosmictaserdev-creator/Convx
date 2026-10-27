package com.convx.music.ui.screens.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.eq.EqualizerService
import com.convx.music.eq.data.EQProfileRepository
import com.convx.music.eq.data.ParametricEQParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * ViewModel for EQ Screen
 * Manages EQ profiles and applies them to the EqualizerService
 */
class EQViewModel(
    private val eqProfileRepository: EQProfileRepository,
    private val equalizerService: EqualizerService
) : ViewModel() {

    private val _state = MutableStateFlow(EQState())
    val state: StateFlow<EQState> = _state.asStateFlow()

    init {
        loadProfiles()
    }

    /** Observes saved and active profile changes. */
    private fun loadProfiles() {
        viewModelScope.launch {
            eqProfileRepository.profiles.collect { _ ->
                val sortedProfiles = eqProfileRepository.getSortedProfiles()
                _state.update {
                    it.copy(profiles = sortedProfiles)
                }
            }
        }

        viewModelScope.launch {
            eqProfileRepository.activeProfile.collect { activeProfile ->
                _state.update {
                    it.copy(activeProfileId = activeProfile?.id)
                }
            }
        }
    }

    /**
     * Select and apply an EQ profile
     * Pass null to disable EQ
     */
    fun selectProfile(profileId: String?) {
        viewModelScope.launch {
            if (profileId == null) {
                equalizerService.disable()
                eqProfileRepository.setActiveProfile(null)
                return@launch
            }

            val profile = _state.value.profiles.find { it.id == profileId } ?: return@launch
            val result = equalizerService.applyProfile(profile)
            result.onSuccess {
                eqProfileRepository.setActiveProfile(profileId)
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Unknown error") }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * Delete an EQ profile
     */
    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            eqProfileRepository.deleteProfile(profileId)
        }
    }

    /**
     * Import a custom EQ profile from a file
     */
    fun importCustomProfile(
        fileName: String,
        inputStream: InputStream,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val content = inputStream.bufferedReader().use { it.readText() }

                val parametricEQ = ParametricEQParser.parseText(content)

                val validationErrors = ParametricEQParser.validate(parametricEQ)
                if (validationErrors.isNotEmpty()) {
                    onError(Exception("Invalid EQ file: ${validationErrors.first()}"))
                    return@launch
                }

                val profileName = fileName.removeSuffix(".txt")

                eqProfileRepository.importCustomProfile(profileName, parametricEQ)

                _state.update { it.copy(importStatus = "Successfully imported $profileName") }
                onSuccess()
            } catch (e: Exception) {
                onError(Exception("Failed to import EQ profile: ${e.message}"))
            }
        }
    }
}
