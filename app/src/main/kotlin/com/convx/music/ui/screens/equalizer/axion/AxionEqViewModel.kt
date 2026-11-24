package com.convx.music.ui.screens.equalizer.axion

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.eq.EqualizerService
import com.convx.music.eq.data.EQProfileRepository
import com.convx.music.eq.data.ParametricEQBand
import com.convx.music.eq.data.SavedEQProfile
import com.convx.music.eq.data.EQProfileImport
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AxionEqViewModel(
    private val context: Context,
    private val equalizerService: EqualizerService,
    private val eqProfileRepository: EQProfileRepository,
) : ViewModel() {
    private val prefs = MMKV.mmkvWithID("vivi_eq_prefs")
    private val defaultFrequencies = doubleArrayOf(31.0, 62.0, 125.0, 250.0, 500.0, 1000.0, 2000.0, 4000.0, 8000.0, 16000.0)
    private val restored = eqProfileRepository.getActiveProfile()
    private val _enabled = MutableStateFlow(restored != null || prefs.decodeBool("enabled", false))
    val enabled = _enabled.asStateFlow()
    private val _bands = MutableStateFlow(restored?.bands ?: List(10) { index ->
        ParametricEQBand(
            frequency = prefs.decodeDouble("frequency_$index", defaultFrequencies[index]),
            gain = prefs.decodeFloat("band_$index", 0f).toDouble() / 50.0,
            q = prefs.decodeDouble("q_$index", 1.41),
        )
    })
    val bands = _bands.asStateFlow()
    private val _bandGains = MutableStateFlow(gains())
    val bandGains = _bandGains.asStateFlow()
    private val _preamp = MutableStateFlow(restored?.preamp ?: prefs.decodeDouble("preamp", 0.0))
    val preamp = _preamp.asStateFlow()
    private val _mode = MutableStateFlow(prefs.decodeInt("mode", 0))
    val mode = _mode.asStateFlow()
    private val _isDirty = MutableStateFlow(false)
    val isDirty = _isDirty.asStateFlow()
    private val _isImporting = MutableStateFlow(false)
    val isImporting = _isImporting.asStateFlow()
    private val _importSucceeded = MutableStateFlow<Boolean?>(null)
    val importSucceeded = _importSucceeded.asStateFlow()
    val customProfiles = eqProfileRepository.profiles.map { profiles ->
        profiles.filter { it.isCustom && it.id != "vivi_tuning" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val desiredProfile = MutableStateFlow(if (_enabled.value) tuningProfile() else null)

    init {
        if (_enabled.value) equalizerService.applyProfile(restored ?: tuningProfile())
        viewModelScope.launch {
            desiredProfile.collectLatest { eqProfileRepository.saveAndActivate(it) }
        }
    }

    private fun gains() = FloatArray(10) { (_bands.value.getOrNull(it)?.gain ?: 0.0).toFloat() * 50f }

    fun setEnabled(enabled: Boolean) {
        _enabled.value = enabled
        prefs.encode("enabled", enabled)
        if (enabled) applyToService() else {
            equalizerService.disable()
            desiredProfile.value = null
        }
    }

    fun setMode(mode: Int) {
        _mode.value = mode
        prefs.encode("mode", mode)
    }

    fun setBandGain(index: Int, gain: Float) {
        if (!gain.isFinite()) return
        updateBand(index) { it.copy(gain = gain.coerceIn(-600f, 600f).toDouble() / 50.0) }
    }

    fun setBandParameters(index: Int, frequency: Double, q: Double) {
        if (!frequency.isFinite() || frequency !in 20.0..20000.0 || !q.isFinite() || q !in 0.1..20.0) return
        updateBand(index) { it.copy(frequency = frequency, q = q) }
    }

    private fun updateBand(index: Int, update: (ParametricEQBand) -> ParametricEQBand) {
        if (index !in _bands.value.indices) return
        _bands.value = _bands.value.mapIndexed { i, band -> if (i == index) update(band) else band }
        changed(true)
    }

    fun setPreamp(value: Double) {
        if (!value.isFinite()) return
        _preamp.value = value.coerceIn(-24.0, 0.0)
        changed(true)
    }

    fun setBandsGains(gains: FloatArray, fromUser: Boolean = false) {
        if (gains.size != 10 || gains.any { !it.isFinite() }) return
        _bands.value = List(10) { ParametricEQBand(defaultFrequencies[it], gains[it].coerceIn(-600f, 600f).toDouble() / 50.0) }
        _preamp.value = -(_bands.value.maxOfOrNull { it.gain.coerceAtLeast(0.0) } ?: 0.0)
        changed(fromUser)
    }

    fun reset() {
        setBandsGains(FloatArray(10), fromUser = true)
    }

    fun selectProfile(profile: SavedEQProfile) {
        _bands.value = profile.bands
        _preamp.value = profile.preamp
        changed(false)
    }

    private fun changed(dirty: Boolean) {
        _bandGains.value = gains()
        _isDirty.value = dirty
        _bands.value.forEachIndexed { index, band ->
            prefs.encode("band_$index", band.gain.toFloat() * 50f)
            prefs.encode("frequency_$index", band.frequency)
            prefs.encode("q_$index", band.q)
        }
        prefs.encode("preamp", _preamp.value)
        if (_enabled.value) applyToService()
    }

    private fun tuningProfile() = SavedEQProfile(
        id = "vivi_tuning", name = "Custom tuning", deviceModel = "Convx Equalizer",
        bands = _bands.value, preamp = _preamp.value,
    )

    private fun applyToService() {
        val profile = tuningProfile()
        equalizerService.applyProfile(profile)
        desiredProfile.value = profile
    }

    fun saveCustomProfile(name: String) {
        if (name.isBlank()) return
        val profile = tuningProfile().copy(id = "custom_${UUID.randomUUID()}", name = name.trim(), isCustom = true)
        viewModelScope.launch {
            eqProfileRepository.saveProfile(profile)
            _isDirty.value = false
        }
    }

    fun deleteProfiles(ids: List<String>) {
        viewModelScope.launch { ids.forEach { eqProfileRepository.deleteProfile(it) } }
    }

    fun dismissImportResult() { _importSucceeded.value = null }

    fun importProfile(uri: Uri) {
        if (_isImporting.value) return
        _isImporting.value = true
        _importSucceeded.value = null
        viewModelScope.launch {
            try {
                val profile = withContext(Dispatchers.IO) {
                    val name = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                        ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
                        ?.substringBeforeLast('.')?.trim()?.take(120)?.takeIf { it.isNotBlank() }
                        ?: context.getString(com.convx.music.R.string.import_profile)
                    val parsed = requireNotNull(context.contentResolver.openInputStream(uri)).use(EQProfileImport::read)
                    SavedEQProfile(
                        id = "custom_${UUID.randomUUID()}", name = name, deviceModel = name,
                        bands = parsed.bands, preamp = parsed.preamp, isCustom = true,
                    ).also { eqProfileRepository.saveProfile(it) }
                }
                selectProfile(profile)
                _mode.value = 1
                prefs.encode("mode", 1)
                _importSucceeded.value = true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _importSucceeded.value = false
            } finally {
                _isImporting.value = false
            }
        }
    }
}
