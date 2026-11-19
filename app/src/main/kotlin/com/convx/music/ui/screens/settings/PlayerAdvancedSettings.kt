/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.constants.ShowVideoSwitchKey
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.playback.audio.BitPerfectAudio
import com.convx.music.constants.AudioOffload
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.AutoLoadMoreKey
import com.convx.music.constants.CompactPlayerInTabViewKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.CrossfadeGaplessKey
import com.convx.music.constants.DisableLoadMoreWhenRepeatAllKey
import com.convx.music.constants.EnableSaavnStreamingKey
import com.convx.music.constants.HistoryDuration
import com.convx.music.constants.PersistentShuffleAcrossQueuesKey
import com.convx.music.constants.PreventDuplicateTracksInQueueKey
import com.convx.music.constants.SearchSongStartsRadioKey
import com.convx.music.constants.SaavnAudioQuality
import com.convx.music.constants.SaavnAudioQualityKey
import com.convx.music.constants.SeekExtraSeconds
import com.convx.music.constants.ShufflePlaylistFirstKey
import com.convx.music.constants.SkipSilenceInstantKey
import com.convx.music.constants.SkipSilenceKey
import com.convx.music.constants.StopMusicOnTaskClearKey
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.utils.appTopBarWindowInsets
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlin.math.roundToInt

/**
 * The power-user half of [PlayerSettings]: everything a casual listener never needs to
 * touch (technical playback tuning, niche display toggles, edge-case queue behaviour),
 * split out so the main Player & Audio page reads as a short, everyday list instead of
 * ~35 items deep. Reads the same preference keys [PlayerSettings] does — DataStore is
 * the single source of truth, so a value changed here or there agrees either way.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerAdvancedSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (crossfadeEnabled) = rememberPreference(CrossfadeEnabledKey, defaultValue = false)
    val (crossfadeGapless, onCrossfadeGaplessChange) = rememberPreference(
        CrossfadeGaplessKey,
        defaultValue = true
    )
    val (compactPlayerInTabView, onCompactPlayerInTabViewChange) = rememberPreference(
        CompactPlayerInTabViewKey,
        defaultValue = false
    )
    val (showVideoSwitch, onShowVideoSwitchChange) = rememberPreference(
        ShowVideoSwitchKey,
        defaultValue = true
    )
    val (historyDuration, onHistoryDurationChange) = rememberPreference(
        HistoryDuration,
        defaultValue = 30f
    )
    val (skipSilence) = rememberPreference(SkipSilenceKey, defaultValue = false)
    val (skipSilenceInstant, onSkipSilenceInstantChange) = rememberPreference(
        SkipSilenceInstantKey,
        defaultValue = false
    )
    val (audioOffload, onAudioOffloadChange) = rememberPreference(
        key = AudioOffload,
        defaultValue = false
    )
    val (bitPerfect, onBitPerfectChange) = rememberPreference(
        key = BitPerfectKey,
        defaultValue = false
    )
    val context = LocalContext.current
    val bitPerfectHardwareAvailable = remember { BitPerfectAudio.isHardwareBitPerfectAvailable(context) }
    val (seekExtraSeconds, onSeekExtraSeconds) = rememberPreference(
        SeekExtraSeconds,
        defaultValue = false
    )
    val (saavnEnabled, _) = rememberPreference(EnableSaavnStreamingKey, defaultValue = false)
    val (saavnQuality, _) = rememberEnumPreference(
        SaavnAudioQualityKey,
        defaultValue = SaavnAudioQuality.QUALITY_320
    )

    val (autoLoadMore, onAutoLoadMoreChange) = rememberPreference(
        AutoLoadMoreKey,
        defaultValue = true
    )
    val (disableLoadMoreWhenRepeatAll, onDisableLoadMoreWhenRepeatAllChange) = rememberPreference(
        DisableLoadMoreWhenRepeatAllKey,
        defaultValue = false
    )
    val (persistentShuffleAcrossQueues, onPersistentShuffleAcrossQueuesChange) = rememberPreference(
        PersistentShuffleAcrossQueuesKey,
        defaultValue = false
    )
    val (shufflePlaylistFirst, onShufflePlaylistFirstChange) = rememberPreference(
        ShufflePlaylistFirstKey,
        defaultValue = false
    )
    val (preventDuplicateTracksInQueue, onPreventDuplicateTracksInQueueChange) = rememberPreference(
        PreventDuplicateTracksInQueueKey,
        defaultValue = false
    )
    val (searchSongStartsRadio, onSearchSongStartsRadioChange) = rememberPreference(
        SearchSongStartsRadioKey,
        defaultValue = false
    )

    val (stopMusicOnTaskClear, onStopMusicOnTaskClearChange) = rememberPreference(
        StopMusicOnTaskClearKey,
        defaultValue = false
    )

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.advanced),
        pageColor = com.convx.music.ui.ios.compat.AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        item(key = "player_advanced_settings_content") {
            Column {
                IosSettingsGroup(
                    title = "Audio engine",
                    items = buildList {
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.graphic_eq),
                            title = { Text(stringResource(R.string.jiosaavn_settings)) },
                            description = {
                                Text(
                                    if (saavnEnabled) saavnQuality.toLabel()
                                    else stringResource(R.string.jiosaavn_streaming_disabled)
                                )
                            },
                            onClick = { navController.navigate("settings/player/jio") }
                        ))
                        if (crossfadeEnabled) {
                            add(IosSettingsItem(
                                icon = painterResource(R.drawable.album),
                                title = { Text(stringResource(R.string.crossfade_gapless)) },
                                description = { Text(stringResource(R.string.crossfade_gapless_desc)) },
                                trailingContent = {
                                    Switch(
                                        checked = crossfadeGapless,
                                        onCheckedChange = onCrossfadeGaplessChange
                                    )
                                },
                                onClick = { onCrossfadeGaplessChange(!crossfadeGapless) }
                            ))
                        }
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.library_music),
                            title = { Text(stringResource(R.string.show_video_switch)) },
                            description = { Text(stringResource(R.string.show_video_switch_desc)) },
                            trailingContent = {
                                Switch(checked = showVideoSwitch, onCheckedChange = onShowVideoSwitchChange)
                            },
                            onClick = { onShowVideoSwitchChange(!showVideoSwitch) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.crop),
                            title = { Text(stringResource(R.string.compact_player_tab_view)) },
                            description = { Text(stringResource(R.string.compact_player_tab_view_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = compactPlayerInTabView,
                                    onCheckedChange = onCompactPlayerInTabViewChange
                                )
                            },
                            onClick = { onCompactPlayerInTabViewChange(!compactPlayerInTabView) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.library_music),
                            title = { Text(stringResource(R.string.excluded_folders)) },
                            description = { Text(stringResource(R.string.excluded_folders_desc)) },
                            onClick = { navController.navigate("settings/player/local_folders") }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.library_music),
                            title = { Text(stringResource(R.string.network_storage)) },
                            description = { Text(stringResource(R.string.network_storage_desc)) },
                            onClick = { navController.navigate("settings/network_storage") }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.history),
                            title = { Text(stringResource(R.string.history_duration)) },
                            description = {
                                Column {
                                    Text(historyDuration.roundToInt().toString())
                                    Slider(
                                        value = historyDuration,
                                        onValueChange = onHistoryDurationChange,
                                        valueRange = 1f..100f
                                    )
                                }
                            }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.skip_next),
                            title = { Text(stringResource(R.string.skip_silence_instant)) },
                            description = { Text(stringResource(R.string.skip_silence_instant_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = skipSilenceInstant,
                                    onCheckedChange = { onSkipSilenceInstantChange(it) },
                                    enabled = skipSilence
                                )
                            },
                            onClick = { if (skipSilence) onSkipSilenceInstantChange(!skipSilenceInstant) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.graphic_eq),
                            title = { Text(stringResource(R.string.audio_offload)) },
                            description = {
                                Text(
                                    if (crossfadeEnabled) stringResource(R.string.audio_offload_disabled_by_crossfade)
                                    else stringResource(R.string.audio_offload_description)
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = if (crossfadeEnabled) false else audioOffload,
                                    onCheckedChange = onAudioOffloadChange,
                                    enabled = !crossfadeEnabled
                                )
                            },
                            onClick = { if (!crossfadeEnabled) onAudioOffloadChange(!audioOffload) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.graphic_eq),
                            title = { Text(stringResource(R.string.audio_bit_perfect)) },
                            description = {
                                Text(
                                    when {
                                        crossfadeEnabled -> stringResource(R.string.audio_bit_perfect_disabled_by_crossfade)
                                        bitPerfectHardwareAvailable -> stringResource(R.string.audio_bit_perfect_description_hw)
                                        else -> stringResource(R.string.audio_bit_perfect_description_sw)
                                    }
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = if (crossfadeEnabled) false else bitPerfect,
                                    onCheckedChange = onBitPerfectChange,
                                    enabled = !crossfadeEnabled
                                )
                            },
                            onClick = { if (!crossfadeEnabled) onBitPerfectChange(!bitPerfect) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.arrow_forward),
                            title = { Text(stringResource(R.string.seek_seconds_addup)) },
                            description = { Text(stringResource(R.string.seek_seconds_addup_description)) },
                            trailingContent = {
                                Switch(checked = seekExtraSeconds, onCheckedChange = onSeekExtraSeconds)
                            },
                            onClick = { onSeekExtraSeconds(!seekExtraSeconds) }
                        ))
                    }
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Queue rules",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.playlist_add),
                            title = { Text(stringResource(R.string.auto_load_more)) },
                            description = { Text(stringResource(R.string.auto_load_more_desc)) },
                            trailingContent = {
                                Switch(checked = autoLoadMore, onCheckedChange = onAutoLoadMoreChange)
                            },
                            onClick = { onAutoLoadMoreChange(!autoLoadMore) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.repeat),
                            title = { Text(stringResource(R.string.disable_load_more_when_repeat_all)) },
                            description = { Text(stringResource(R.string.disable_load_more_when_repeat_all_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = disableLoadMoreWhenRepeatAll,
                                    onCheckedChange = onDisableLoadMoreWhenRepeatAllChange
                                )
                            },
                            onClick = { onDisableLoadMoreWhenRepeatAllChange(!disableLoadMoreWhenRepeatAll) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.shuffle),
                            title = { Text(stringResource(R.string.persistent_shuffle_title)) },
                            description = { Text(stringResource(R.string.persistent_shuffle_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = persistentShuffleAcrossQueues,
                                    onCheckedChange = onPersistentShuffleAcrossQueuesChange
                                )
                            },
                            onClick = { onPersistentShuffleAcrossQueuesChange(!persistentShuffleAcrossQueues) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.shuffle),
                            title = { Text(stringResource(R.string.shuffle_playlist_first)) },
                            description = { Text(stringResource(R.string.shuffle_playlist_first_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = shufflePlaylistFirst,
                                    onCheckedChange = onShufflePlaylistFirstChange
                                )
                            },
                            onClick = { onShufflePlaylistFirstChange(!shufflePlaylistFirst) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.queue_music),
                            title = { Text(stringResource(R.string.prevent_duplicate_tracks_in_queue)) },
                            description = { Text(stringResource(R.string.prevent_duplicate_tracks_in_queue_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = preventDuplicateTracksInQueue,
                                    onCheckedChange = onPreventDuplicateTracksInQueueChange
                                )
                            },
                            onClick = { onPreventDuplicateTracksInQueueChange(!preventDuplicateTracksInQueue) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.radio),
                            title = { Text(stringResource(R.string.search_song_starts_radio)) },
                            description = { Text(stringResource(R.string.search_song_starts_radio_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = searchSongStartsRadio,
                                    onCheckedChange = onSearchSongStartsRadioChange
                                )
                            },
                            onClick = { onSearchSongStartsRadioChange(!searchSongStartsRadio) }
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Other",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.clear_all),
                            title = { Text(stringResource(R.string.stop_music_on_task_clear)) },
                            trailingContent = {
                                Switch(
                                    checked = stopMusicOnTaskClear,
                                    onCheckedChange = onStopMusicOnTaskClearChange
                                )
                            },
                            onClick = { onStopMusicOnTaskClearChange(!stopMusicOnTaskClear) }
                        ),
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    
    }
}
