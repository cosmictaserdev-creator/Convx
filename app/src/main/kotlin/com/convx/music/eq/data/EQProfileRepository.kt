package com.convx.music.eq.data

import android.content.Context
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Saved EQ Profile with metadata
 */
@Serializable
data class SavedEQProfile(
    val id: String,                       // Unique identifier
    val name: String,                     // Display name
    val deviceModel: String,              // e.g., "Sony WH-1000XM4"
    val bands: List<ParametricEQBand>,    // EQ bands
    val preamp: Double = 0.0,             // Preamp gain in dB
    val isCustom: Boolean = false,        // Whether this is a custom imported profile
    val isActive: Boolean = false,        // Whether this profile is currently active
    val addedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Repository for managing EQ profiles
 * Handles saving, loading, and activating EQ profiles
 */
class EQProfileRepository(
    private val context: Context
) {
    private val prefs: MMKV? = MMKV.mmkvWithID(
        "nanosonic_eq_profiles"
    )

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val _profiles = MutableStateFlow<List<SavedEQProfile>>(emptyList())
    val profiles: StateFlow<List<SavedEQProfile>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<SavedEQProfile?>(null)
    private val mutationMutex = Mutex()
    val activeProfile: StateFlow<SavedEQProfile?> = _activeProfile.asStateFlow()

    companion object {
        private const val KEY_PROFILES = "eq_profiles"
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
        private const val KEY_OUTPUT_PREFIX = "output_profile:"
        private const val OUTPUT_OFF = "__off__"
    }

    /**
     * The audio output in use (see MusicService.currentOutputKey). Each output restores
     * the EQ profile last selected for it when it reconnects.
     */
    @Volatile
    private var currentOutput: String? = null

    private fun recordForOutput(profileId: String?) {
        currentOutput?.let { prefs?.encode(KEY_OUTPUT_PREFIX + it, profileId ?: OUTPUT_OFF) }
    }

    /** Output changed: restore what was last used on it. Unknown outputs keep the current EQ. */
    suspend fun switchOutput(key: String) {
        if (key == currentOutput) return
        currentOutput = key
        val saved = prefs?.decodeString(KEY_OUTPUT_PREFIX + key) ?: return
        when {
            saved == OUTPUT_OFF -> setActiveProfile(null)
            _profiles.value.any { it.id == saved } -> setActiveProfile(saved)
        }
    }

    init {
        loadProfiles()
    }

    /** Restores saved profiles and the active profile from SharedPreferences. */
    private fun loadProfiles() {
        try {
            val profilesJson = prefs?.decodeString(KEY_PROFILES)
            if (profilesJson != null) {
                val loadedProfiles = json.decodeFromString<List<SavedEQProfile>>(profilesJson)
                _profiles.value = loadedProfiles

                val activeId = prefs.decodeString(KEY_ACTIVE_PROFILE_ID)
                _activeProfile.value = loadedProfiles.find { it.id == activeId }
            }
        } catch (e: Exception) {
            println("Error loading EQ profiles: ${e.message}")
            _profiles.value = emptyList()
            _activeProfile.value = null
        }
    }

    suspend fun saveProfile(profile: SavedEQProfile) = withContext(Dispatchers.IO) {
        mutationMutex.withLock {
            val currentProfiles = _profiles.value.toMutableList()

            // Replace a saved profile with the same ID.
            val existingIndex = currentProfiles.indexOfFirst { it.id == profile.id }

            if (existingIndex >= 0) {
                currentProfiles[existingIndex] = profile
            } else {
                currentProfiles.add(profile)
            }

            val profilesJson = json.encodeToString<List<SavedEQProfile>>(currentProfiles)
            prefs?.encode(KEY_PROFILES, profilesJson)

            _profiles.value = currentProfiles
            if (_activeProfile.value?.id == profile.id) _activeProfile.value = profile
        }
    }

    suspend fun deleteProfile(profileId: String) = withContext(Dispatchers.IO) {
        mutationMutex.withLock {
            val currentProfiles = _profiles.value.toMutableList()
            currentProfiles.removeAll { it.id == profileId }

            val profilesJson = json.encodeToString<List<SavedEQProfile>>(currentProfiles)
            prefs?.encode(KEY_PROFILES, profilesJson)

            // Clear the active setting if the deleted profile was selected.
            if (_activeProfile.value?.id == profileId) {
                _activeProfile.value = null
                prefs?.removeValueForKey(KEY_ACTIVE_PROFILE_ID)
            }

            _profiles.value = currentProfiles
        }
    }

    /**
     * Set a profile as active (only one profile can be active at a time)
     * Pass null to deactivate all profiles
     */
    suspend fun setActiveProfile(profileId: String?) = withContext(Dispatchers.IO) {
        mutationMutex.withLock {
            val currentProfiles = _profiles.value

            if (profileId == null) {
                _activeProfile.value = null
                prefs?.removeValueForKey(KEY_ACTIVE_PROFILE_ID)
            } else {
                val profile = currentProfiles.find { it.id == profileId }
                _activeProfile.value = profile
                prefs?.encode(KEY_ACTIVE_PROFILE_ID, profileId)
            }
            recordForOutput(profileId)
        }
    }

    /** Save the live tuning and its enabled state in one serialized mutation. */
    suspend fun saveAndActivate(profile: SavedEQProfile?) = withContext(Dispatchers.IO) {
        mutationMutex.withLock {
            recordForOutput(profile?.id)
            if (profile == null) {
                prefs?.removeValueForKey(KEY_ACTIVE_PROFILE_ID)
                _activeProfile.value = null
            } else {
                val profiles = _profiles.value.filterNot { it.id == profile.id } + profile
                prefs?.encode(KEY_PROFILES, json.encodeToString(profiles))
                prefs?.encode(KEY_ACTIVE_PROFILE_ID, profile.id)
                _profiles.value = profiles
                _activeProfile.value = profile
            }
        }
    }

    fun getAllProfiles(): List<SavedEQProfile> = _profiles.value

    fun getActiveProfile(): SavedEQProfile? = _activeProfile.value

    /**
     * Import a custom EQ profile from ParametricEQ data
     */
    suspend fun importCustomProfile(
        name: String,
        parametricEQ: ParametricEQ
    ) = withContext(Dispatchers.IO) {
        val id = "custom_${System.currentTimeMillis()}_${name.hashCode()}"

        val customProfile = SavedEQProfile(
            id = id,
            name = name,
            deviceModel = name,
            bands = parametricEQ.bands,
            preamp = parametricEQ.preamp,
            isActive = false,
            isCustom = true
        )

        saveProfile(customProfile)
    }

    /** Returns custom profiles with the newest first. */
    fun getSortedProfiles(): List<SavedEQProfile> {
        // Only custom profiles are supported now
        return _profiles.value
            .filter { it.isCustom }
            .sortedByDescending { it.addedTimestamp }
    }
}
