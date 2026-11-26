package com.convx.music.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.music.playback.DjDelayType
import com.convx.music.playback.DjReverbType
import com.convx.music.playback.PlaybackSettings as SharedPlaybackSettings
import com.convx.music.playback.PlaybackTransitionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackPreferenceMappingTest {
    @Test
    fun emptyStorePreservesShippingDefaults() {
        assertEquals(SharedPlaybackSettings(), emptyPreferences().readPlaybackTransitionSettings())
    }

    @Test
    fun readsExistingKeyNamesAndUnitsWithoutChangingStoredValues() {
        val preferences = preferencesOf(
            booleanPreferencesKey("crossfadeEnabled") to true,
            floatPreferencesKey("crossfadeDuration") to 7.5f,
            booleanPreferencesKey("crossfadeGapless") to true,
            booleanPreferencesKey("autoDjMixingEnabled") to true,
            booleanPreferencesKey("creativeTransitionsEnabled") to true,
            stringPreferencesKey("djReverbType") to "HALL",
            stringPreferencesKey("djDelayType") to "PING_PONG",
        )
        val settings = preferences.readPlaybackTransitionSettings()
        assertEquals(7_500L, settings.crossfadeDurationMs)
        assertEquals(PlaybackTransitionMode.AUTO_MIX, settings.transitionMode)
        assertTrue(settings.crossfadeGapless)
        assertTrue(settings.creativeTransitionsEnabled)
        assertEquals(DjReverbType.HALL, settings.reverbType)
        assertEquals(DjDelayType.PING_PONG, settings.delayType)
        assertEquals(7.5f, preferences[floatPreferencesKey("crossfadeDuration")])
    }

    @Test
    fun corruptAndLegacyDurationsKeepTheExistingFallbackAndBounds() {
        listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach { duration ->
            assertEquals(5_000L, preferencesOf(floatPreferencesKey("crossfadeDuration") to duration)
                .readPlaybackTransitionSettings().crossfadeDurationMs)
        }
        assertEquals(12_000L, preferencesOf(floatPreferencesKey("crossfadeDuration") to 15f)
            .readPlaybackTransitionSettings().crossfadeDurationMs)
        assertEquals(1_000L, preferencesOf(floatPreferencesKey("crossfadeDuration") to -1f)
            .readPlaybackTransitionSettings().crossfadeDurationMs)
    }

    @Test
    fun bitPerfectSuppressesTransitionsWithoutErasingUserPreferences() {
        val settings = preferencesOf(
            booleanPreferencesKey("crossfadeEnabled") to true,
            booleanPreferencesKey("autoDjMixingEnabled") to true,
            booleanPreferencesKey("bitPerfect") to true,
            stringPreferencesKey("djReverbType") to "future-program",
            stringPreferencesKey("djDelayType") to "future-program",
        ).readPlaybackTransitionSettings()
        assertEquals(PlaybackTransitionMode.OFF, settings.transitionMode)
        assertTrue(settings.crossfadeEnabled)
        assertTrue(settings.autoMixEnabled)
        assertEquals(DjReverbType.AUTO, settings.reverbType)
        assertEquals(DjDelayType.AUTO, settings.delayType)
    }
}
