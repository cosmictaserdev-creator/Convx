package com.convx.music.playback.presentation

import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.dsl.module

class PlaybackSettingsStateHolder(
    private val repository: PlaybackSettingsRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutableState = MutableStateFlow(PlaybackSettings())
    private val mutableErrorMessage = MutableStateFlow<String?>(null)

    val state: StateFlow<PlaybackSettings> = mutableState.asStateFlow()
    val errorMessage: StateFlow<String?> = mutableErrorMessage.asStateFlow()

    init {
        scope.launch {
            repository.observeSettings().collect { mutableState.value = it }
        }
    }

    fun setCrossfadeEnabled(enabled: Boolean) {
        scope.launch {
            try {
                repository.updateSettings { current ->
                    current.copy(crossfadeEnabled = enabled, autoMixEnabled = false)
                }
                mutableErrorMessage.value = null
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableErrorMessage.value = "Couldn't save playback settings."
            }
        }
    }

    fun setAutoMixEnabled(enabled: Boolean) {
        updateSettings { current ->
            current.copy(
                crossfadeEnabled = if (enabled) true else current.crossfadeEnabled,
                autoMixEnabled = enabled,
            )
        }
    }

    fun setCrossfadeDuration(durationMs: Long) = updateSettings { it.copy(crossfadeDurationMs = durationMs).normalized() }
    fun setLyricsFontSize(size: Float) = updateSettings { it.copy(lyricsFontSize = size).normalized() }
    fun setLyricsOffset(offsetMs: Long) = updateSettings { it.copy(lyricsOffsetMs = offsetMs).normalized() }
    fun setLyricsProvider(sourceId: String?) = updateSettings { it.copy(lyricsProviderId = sourceId) }
    fun setReducedMotion(enabled: Boolean) = updateSettings { it.copy(reducedMotion = enabled) }
    fun setDarkTheme(enabled: Boolean) = updateSettings { it.copy(darkTheme = enabled, appearance = it.appearance.copy(followSystemTheme = false)) }
    fun setRecordSearchHistory(enabled: Boolean) = updateSettings { it.copy(recordSearchHistory = enabled) }
    fun clearSearchHistory() = updateSettings { it.copy(recentSearches = emptyList()) }
    fun setVolume(volume: Float) = updateSettings { it.copy(volume = volume).normalized() }
    fun setBitPerfectEnabled(enabled: Boolean) = updateSettings { it.copy(bitPerfectEnabled = enabled) }
    fun setAudioOutputDevice(id: String?) = updateSettings { it.copy(audioOutputDeviceId = id).normalized() }
    fun setLocalMode(enabled: Boolean) = updateSettings { it.copy(localMode = enabled) }
    fun updatePlayerCustomization(transform: (com.convx.music.playback.PlayerCustomization) -> com.convx.music.playback.PlayerCustomization) =
        updateSettings { it.copy(playerCustomization = transform(it.playerCustomization).normalized()) }
    fun updateAppearance(transform: (com.convx.music.playback.AppearanceSettings) -> com.convx.music.playback.AppearanceSettings) =
        updateSettings { it.copy(appearance = transform(it.appearance).normalized()) }

    fun setEqualizerEnabled(enabled: Boolean) {
        updateSettings { current ->
            current.copy(
                equalizerEnabled = enabled,
                equalizerProfile = current.equalizerProfile.takeIf { it.bands.isNotEmpty() }
                    ?: PlaybackEqualizerPreset.FLAT.profile(),
            )
        }
    }

    fun setEqualizerPreset(preset: PlaybackEqualizerPreset) {
        updateSettings { current ->
            current.copy(equalizerEnabled = true, equalizerProfile = preset.profile())
        }
    }

    fun setEqualizerBandGain(index: Int, gainDb: Double) {
        updateSettings { current ->
            val bands = current.equalizerProfile.bands.ifEmpty { PlaybackEqualizerPreset.FLAT.profile().bands }
            if (index !in bands.indices) return@updateSettings current

            val updatedBands = bands.toMutableList()
            updatedBands[index] = updatedBands[index].copy(
                gain = gainDb.takeIf(Double::isFinite)?.coerceIn(EQUALIZER_MIN_GAIN_DB, EQUALIZER_MAX_GAIN_DB) ?: 0.0,
            )
            current.copy(
                equalizerEnabled = true,
                equalizerProfile = current.equalizerProfile.copy(bands = updatedBands),
            )
        }
    }

    private fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings) {
        scope.launch {
            try {
                repository.updateSettings(transform)
                mutableErrorMessage.value = null
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableErrorMessage.value = "Couldn't save playback settings."
            }
        }
    }

    fun close() {
        scope.cancel()
    }
}

private const val EQUALIZER_MIN_GAIN_DB = -12.0
private const val EQUALIZER_MAX_GAIN_DB = 12.0

fun playbackPresentationModule(): Module = module {
    single { PlaybackSettingsStateHolder(get()) }
}
