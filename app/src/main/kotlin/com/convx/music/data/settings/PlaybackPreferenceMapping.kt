package com.convx.music.data.settings

import androidx.datastore.preferences.core.Preferences
import com.convx.music.constants.AutoDjMixingEnabledKey
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.CreativeTransitionsEnabledKey
import com.convx.music.constants.CrossfadeDurationKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.CrossfadeGaplessKey
import com.convx.music.constants.DjDelayTypeKey
import com.convx.music.constants.DjReverbTypeKey
import com.convx.music.extensions.toEnum
import com.convx.music.playback.DjDelayType
import com.convx.music.playback.DjReverbType
import com.convx.music.playback.PlaybackSettings as SharedPlaybackSettings

internal fun Preferences.readPlaybackTransitionSettings(): SharedPlaybackSettings {
    val defaults = SharedPlaybackSettings()
    val seconds = this[CrossfadeDurationKey]
        ?.takeIf(Float::isFinite)
        ?: (defaults.crossfadeDurationMs / 1_000f)
    return defaults.copy(
        crossfadeEnabled = this[CrossfadeEnabledKey] ?: defaults.crossfadeEnabled,
        crossfadeDurationMs = (seconds.coerceIn(
            SharedPlaybackSettings.MIN_CROSSFADE_DURATION_MS / 1_000f,
            SharedPlaybackSettings.MAX_CROSSFADE_DURATION_MS / 1_000f,
        ) * 1_000).toLong(),
        crossfadeGapless = this[CrossfadeGaplessKey] ?: defaults.crossfadeGapless,
        autoMixEnabled = this[AutoDjMixingEnabledKey] ?: defaults.autoMixEnabled,
        creativeTransitionsEnabled = this[CreativeTransitionsEnabledKey] ?: defaults.creativeTransitionsEnabled,
        reverbType = this[DjReverbTypeKey].toEnum(DjReverbType.AUTO),
        delayType = this[DjDelayTypeKey].toEnum(DjDelayType.AUTO),
        bitPerfectEnabled = this[BitPerfectKey] ?: defaults.bitPerfectEnabled,
    ).normalized()
}
