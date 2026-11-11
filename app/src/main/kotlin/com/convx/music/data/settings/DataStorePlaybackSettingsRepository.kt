package com.convx.music.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.convx.music.constants.AutoDjMixingEnabledKey
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.CreativeTransitionsEnabledKey
import com.convx.music.constants.CrossfadeDurationKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.CrossfadeGaplessKey
import com.convx.music.constants.DjDelayTypeKey
import com.convx.music.constants.DjReverbTypeKey
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.constants.LyricsTextSizeKey
import com.convx.music.constants.LyricsAnimationStyleKey
import com.convx.music.playback.LyricsAnimationStyle
import com.convx.music.constants.PauseSearchHistoryKey
import com.convx.music.constants.PlayerVolumeKey
import com.convx.music.constants.SponsorBlockEnabledKey
import com.convx.music.playback.PlaybackSettings as SharedPlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStorePlaybackSettingsRepository(
    private val store: DataStore<Preferences>,
) : PlaybackSettingsRepository {
    override fun observeSettings() = store.data.map(::readSettings).distinctUntilChanged()

    override suspend fun getSettings() = readSettings(store.data.first())

    override suspend fun updateSettings(
        transform: (SharedPlaybackSettings) -> SharedPlaybackSettings,
    ): SharedPlaybackSettings {
        val saved = store.edit { preferences ->
            val current = readSettings(preferences)
            val updated = transform(current).normalized()
            checkSupportedChanges(current, updated)
            preferences.writeIfChanged(CrossfadeEnabledKey, current.crossfadeEnabled, updated.crossfadeEnabled)
            preferences.writeIfChanged(CrossfadeDurationKey, current.crossfadeDurationMs / 1_000f, updated.crossfadeDurationMs / 1_000f)
            preferences.writeIfChanged(CrossfadeGaplessKey, current.crossfadeGapless, updated.crossfadeGapless)
            preferences.writeIfChanged(AutoDjMixingEnabledKey, current.autoMixEnabled, updated.autoMixEnabled)
            preferences.writeIfChanged(CreativeTransitionsEnabledKey, current.creativeTransitionsEnabled, updated.creativeTransitionsEnabled)
            preferences.writeIfChanged(DjReverbTypeKey, current.reverbType.name, updated.reverbType.name)
            preferences.writeIfChanged(DjDelayTypeKey, current.delayType.name, updated.delayType.name)
            preferences.writeIfChanged(BitPerfectKey, current.bitPerfectEnabled, updated.bitPerfectEnabled)
            preferences.writeIfChanged(LyricsTextSizeKey, current.lyricsFontSize, updated.lyricsFontSize)
            preferences.writeIfChanged(LyricsAnimationStyleKey, current.lyricsAnimationStyle.name, updated.lyricsAnimationStyle.name)
            preferences.writeIfChanged(PlayerVolumeKey, current.volume, updated.volume)
            preferences.writeIfChanged(LocalOnlyModeKey, current.localMode, updated.localMode)
            preferences.writeIfChanged(PauseSearchHistoryKey, !current.recordSearchHistory, !updated.recordSearchHistory)
            preferences.writeIfChanged(SponsorBlockEnabledKey, current.sponsorBlockEnabled, updated.sponsorBlockEnabled)
        }
        return readSettings(saved)
    }

    private fun readSettings(preferences: Preferences): SharedPlaybackSettings {
        val defaults = preferences.readPlaybackTransitionSettings()
        return defaults.copy(
            lyricsFontSize = preferences[LyricsTextSizeKey] ?: defaults.lyricsFontSize,
            lyricsAnimationStyle = preferences[LyricsAnimationStyleKey]?.let { runCatching { LyricsAnimationStyle.valueOf(it) }.getOrNull() } ?: defaults.lyricsAnimationStyle,
            volume = preferences[PlayerVolumeKey] ?: defaults.volume,
            localMode = preferences[LocalOnlyModeKey] ?: defaults.localMode,
            recordSearchHistory = !(preferences[PauseSearchHistoryKey] ?: !defaults.recordSearchHistory),
            sponsorBlockEnabled = preferences[SponsorBlockEnabledKey] ?: defaults.sponsorBlockEnabled,
        ).normalized()
    }

    private fun checkSupportedChanges(current: SharedPlaybackSettings, updated: SharedPlaybackSettings) {
        // Other stores need their own adapters before shared controls can mutate them.
        val unsupportedChanges = updated.copy(
            crossfadeEnabled = current.crossfadeEnabled,
            crossfadeDurationMs = current.crossfadeDurationMs,
            crossfadeGapless = current.crossfadeGapless,
            autoMixEnabled = current.autoMixEnabled,
            creativeTransitionsEnabled = current.creativeTransitionsEnabled,
            reverbType = current.reverbType,
            delayType = current.delayType,
            bitPerfectEnabled = current.bitPerfectEnabled,
            lyricsFontSize = current.lyricsFontSize,
            lyricsAnimationStyle = current.lyricsAnimationStyle,
            volume = current.volume,
            localMode = current.localMode,
            recordSearchHistory = current.recordSearchHistory,
            sponsorBlockEnabled = current.sponsorBlockEnabled,
        )
        if (unsupportedChanges != current) {
            throw UnsupportedOperationException("The requested setting has no Android storage adapter")
        }
    }
}

private fun <T> MutablePreferences.writeIfChanged(key: Preferences.Key<T>, previous: T, updated: T) {
    if (previous != updated) this[key] = updated
}
