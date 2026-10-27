package com.convx.music.playback.presentation

import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.PlaybackTransitionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaybackSettingsStateHolderTest {
    @Test
    fun enablingCrossfadeClearsAutoMixAndPublishesPersistedSettings() = runBlocking {
        val repository = FakePlaybackSettingsRepository(
            PlaybackSettings(crossfadeEnabled = true, autoMixEnabled = true),
        )
        val holder = PlaybackSettingsStateHolder(repository)

        try {
            holder.setCrossfadeEnabled(true)
            val updated = withTimeout(5_000L) {
                holder.state.first { it.transitionMode == PlaybackTransitionMode.CROSSFADE }
            }

            assertTrue(updated.crossfadeEnabled)
            assertFalse(updated.autoMixEnabled)
            assertEquals(PlaybackTransitionMode.CROSSFADE, updated.transitionMode)
        } finally {
            holder.close()
        }
    }

    @Test
    fun enablingAutoMixAlsoEnablesCrossfadeAndDisablingItKeepsRegularCrossfade() = runBlocking {
        val repository = FakePlaybackSettingsRepository(PlaybackSettings())
        val holder = PlaybackSettingsStateHolder(repository)

        try {
            holder.setAutoMixEnabled(true)
            val autoMix = withTimeout(5_000L) {
                holder.state.first { it.transitionMode == PlaybackTransitionMode.AUTO_MIX }
            }
            assertTrue(autoMix.crossfadeEnabled)
            assertTrue(autoMix.autoMixEnabled)

            holder.setAutoMixEnabled(false)
            val crossfade = withTimeout(5_000L) {
                holder.state.first { it.transitionMode == PlaybackTransitionMode.CROSSFADE }
            }
            assertTrue(crossfade.crossfadeEnabled)
            assertFalse(crossfade.autoMixEnabled)
        } finally {
            holder.close()
        }
    }

    @Test
    fun failedSettingsWriteIsReportedWithoutCancellingTheStateHolder() = runBlocking {
        val repository = FakePlaybackSettingsRepository(PlaybackSettings(), failUpdates = true)
        val holder = PlaybackSettingsStateHolder(repository)

        try {
            holder.setCrossfadeEnabled(true)
            val error = withTimeout(5_000L) { holder.errorMessage.first { it != null } }

            assertEquals("Couldn't save playback settings.", error)
            assertFalse(holder.state.value.crossfadeEnabled)
        } finally {
            holder.close()
        }
    }

    @Test
    fun selectingPresetAndEditingBandPersistsAnEnabledEqualizerProfile() = runBlocking {
        val repository = FakePlaybackSettingsRepository(PlaybackSettings())
        val holder = PlaybackSettingsStateHolder(repository)

        try {
            holder.setEqualizerPreset(PlaybackEqualizerPreset.BASS_BOOST)
            val preset = withTimeout(5_000L) {
                holder.state.first { it.equalizerProfile.bands.size == PlaybackEqualizerPreset.FLAT.profile().bands.size }
            }
            assertTrue(preset.equalizerEnabled)
            assertEquals(5.0, preset.equalizerProfile.bands.first().gain)

            holder.setEqualizerBandGain(index = 1, gainDb = 11.4)
            val edited = withTimeout(5_000L) {
                holder.state.first { it.equalizerProfile.bands.getOrNull(1)?.gain == 11.4 }
            }
            assertTrue(edited.equalizerEnabled)
            assertEquals(11.4, edited.equalizerProfile.bands[1].gain)
        } finally {
            holder.close()
        }
    }

    @Test
    fun enablingEqualizerCreatesFlatProfileAndInvalidBandIndexIsIgnored() = runBlocking {
        val repository = FakePlaybackSettingsRepository(PlaybackSettings())
        val holder = PlaybackSettingsStateHolder(repository)

        try {
            holder.setEqualizerEnabled(true)
            val enabled = withTimeout(5_000L) {
                holder.state.first { it.equalizerEnabled && it.equalizerProfile.bands.isNotEmpty() }
            }
            assertEquals(10, enabled.equalizerProfile.bands.size)
            assertTrue(enabled.equalizerProfile.bands.all { it.gain == 0.0 })

            holder.setEqualizerBandGain(index = 20, gainDb = 4.0)
            repository.awaitUpdateCount(2)
            val unchanged = holder.state.value
            assertEquals(enabled.equalizerProfile, unchanged.equalizerProfile)
        } finally {
            holder.close()
        }
    }

    private class FakePlaybackSettingsRepository(
        initial: PlaybackSettings,
        private val failUpdates: Boolean = false,
    ) : PlaybackSettingsRepository {
        private val settings = MutableStateFlow(initial)
        private val updateCount = MutableStateFlow(0)

        override fun observeSettings(): Flow<PlaybackSettings> = settings

        override suspend fun getSettings(): PlaybackSettings = settings.value

        override suspend fun updateSettings(
            transform: (PlaybackSettings) -> PlaybackSettings,
        ): PlaybackSettings {
            check(!failUpdates) { "Simulated settings persistence failure." }
            val updated = transform(settings.value).normalized()
            settings.value = updated
            updateCount.value += 1
            return updated
        }

        suspend fun awaitUpdateCount(count: Int) {
            withTimeout(5_000L) { updateCount.first { it >= count } }
        }
    }
}
