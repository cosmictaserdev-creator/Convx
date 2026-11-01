/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.BuildConfig
import com.convx.music.R
import com.convx.music.constants.AskBeforeReplacingQueueKey
import com.convx.music.constants.AudioNormalizationKey
import com.convx.music.constants.AudioQuality
import com.convx.music.constants.AudioQualityKey
import com.convx.music.constants.AutoDjMixingEnabledKey
import com.convx.music.constants.AutoDownloadOnLikeKey
import com.convx.music.constants.AutoSkipNextOnErrorKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.EnableGoogleCastKey
import com.convx.music.constants.KeepScreenOn
import com.convx.music.constants.MusicHapticsKey
import com.convx.music.constants.MusicHapticsSensitivityKey
import com.convx.music.constants.MusicHapticsStrengthKey
import com.convx.music.constants.PauseOnMute
import com.convx.music.constants.PersistentQueueKey
import com.convx.music.constants.RememberShuffleAndRepeatKey
import com.convx.music.constants.ResumeOnBluetoothConnectKey
import com.convx.music.constants.SimilarContent
import com.convx.music.constants.SkipSilenceKey
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (audioQuality, onAudioQualityChange) = rememberEnumPreference(
        AudioQualityKey,
        defaultValue = AudioQuality.AUTO
    )
    val (crossfadeEnabled) = rememberPreference(
        CrossfadeEnabledKey,
        defaultValue = false
    )
    val (autoDjMixingEnabled) = rememberPreference(
        AutoDjMixingEnabledKey,
        defaultValue = false
    )
    val (persistentQueue, onPersistentQueueChange) = rememberPreference(
        PersistentQueueKey,
        defaultValue = true
    )
    val (skipSilence, onSkipSilenceChange) = rememberPreference(
        SkipSilenceKey,
        defaultValue = false
    )
    val (audioNormalization, onAudioNormalizationChange) = rememberPreference(
        AudioNormalizationKey,
        defaultValue = true
    )

    val (enableGoogleCast, onEnableGoogleCastChange) = rememberPreference(
        key = EnableGoogleCastKey,
        defaultValue = true
    )

    val (autoDownloadOnLike, onAutoDownloadOnLikeChange) = rememberPreference(
        AutoDownloadOnLikeKey,
        defaultValue = false
    )
    val (saveDownloadsToMusic, onSaveDownloadsToMusicChange) = rememberPreference(
        com.convx.music.constants.SaveDownloadsToMusicFolderKey,
        defaultValue = true
    )
    val (similarContentEnabled, similarContentEnabledChange) = rememberPreference(
        key = SimilarContent,
        defaultValue = true
    )
    val (autoSkipNextOnError, onAutoSkipNextOnErrorChange) = rememberPreference(
        AutoSkipNextOnErrorKey,
        defaultValue = false
    )
    val (rememberShuffleAndRepeat, onRememberShuffleAndRepeatChange) = rememberPreference(
        RememberShuffleAndRepeatKey,
        defaultValue = true
    )
    val (askBeforeReplacingQueue, onAskBeforeReplacingQueueChange) = rememberPreference(
        AskBeforeReplacingQueueKey,
        defaultValue = false
    )
    val (pauseOnMute, onPauseOnMuteChange) = rememberPreference(
        PauseOnMute,
        defaultValue = false
    )
    val (resumeOnBluetoothConnect, onResumeOnBluetoothConnectChange) = rememberPreference(
        ResumeOnBluetoothConnectKey,
        defaultValue = false
    )
    val (keepScreenOn, onKeepScreenOnChange) = rememberPreference(
        KeepScreenOn,
        defaultValue = false
    )
    val (musicHaptics, onMusicHapticsChange) = rememberPreference(
        MusicHapticsKey,
        defaultValue = false
    )
    val (hapticSensitivity, onHapticSensitivityChange) = rememberPreference(
        MusicHapticsSensitivityKey,
        defaultValue = 0.5f
    )
    val (hapticStrength, onHapticStrengthChange) = rememberPreference(
        MusicHapticsStrengthKey,
        defaultValue = 0.5f
    )
    var showAudioQualityDialog by remember {
        mutableStateOf(false)
    }

    if (showAudioQualityDialog) {
        EnumDialog(
            onDismiss = { showAudioQualityDialog = false },
            onSelect = {
                onAudioQualityChange(it)
                showAudioQualityDialog = false
            },
            title = stringResource(R.string.audio_quality),
            current = audioQuality,
            values = AudioQuality.values().toList(),
            valueText = {
                when (it) {
                    AudioQuality.AUTO -> stringResource(R.string.audio_quality_auto)
                    AudioQuality.HIGH -> stringResource(R.string.audio_quality_high)
                    AudioQuality.LOW -> stringResource(R.string.audio_quality_low)
                }
            }
        )
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.player_and_audio),
        horizontalPadding = 16.dp,
    ) {
        item(key = "player_settings_content") {
            Column {
                IosSettingsGroup(
                    title = "Sound",
                    items = buildList {
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.graphic_eq),
                            title = { Text(stringResource(R.string.audio_quality)) },
                            description = {
                                Text(
                                    when (audioQuality) {
                                        AudioQuality.AUTO -> stringResource(R.string.audio_quality_auto)
                                        AudioQuality.HIGH -> stringResource(R.string.audio_quality_high)
                                        AudioQuality.LOW -> stringResource(R.string.audio_quality_low)
                                    }
                                )
                            },
                            onClick = { showAudioQualityDialog = true }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.volume_up),
                            title = { Text(stringResource(R.string.audio_normalization)) },
                            trailingContent = {
                                Switch(
                                    checked = audioNormalization,
                                    onCheckedChange = onAudioNormalizationChange
                                )
                            },
                            onClick = { onAudioNormalizationChange(!audioNormalization) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.fast_forward),
                            title = { Text(stringResource(R.string.skip_silence)) },
                            description = { Text(stringResource(R.string.skip_silence_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = skipSilence,
                                    onCheckedChange = onSkipSilenceChange
                                )
                            },
                            onClick = { onSkipSilenceChange(!skipSilence) }
                        ))
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.convx_equalizer_icon),
                            title = { Text(stringResource(R.string.convx_equalizer)) },
                            description = { Text(stringResource(R.string.convx_equalizer_desc)) },
                            onClick = { navController.navigate("settings/equalizer") }
                        ))
                    }
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Transitions & output",
                    items = buildList {
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.ic_queue_crossfade),
                            title = { Text(stringResource(R.string.song_transitions)) },
                            description = {
                                Text(
                                    stringResource(
                                        when {
                                            !crossfadeEnabled -> R.string.transition_off
                                            autoDjMixingEnabled -> R.string.transition_automix
                                            else -> R.string.crossfade
                                        }
                                    )
                                )
                            },
                            onClick = { navController.navigate("settings/song_transitions") }
                        ))
                        // Only show Cast setting in GMS builds (not in F-Droid/FOSS)
                        if (BuildConfig.CAST_AVAILABLE) {
                            add(IosSettingsItem(
                                icon = painterResource(R.drawable.cast),
                                title = { Text(stringResource(R.string.google_cast)) },
                                description = { Text(stringResource(R.string.google_cast_description)) },
                                trailingContent = {
                                    Switch(
                                        checked = enableGoogleCast,
                                        onCheckedChange = onEnableGoogleCastChange
                                    )
                                },
                                onClick = { onEnableGoogleCastChange(!enableGoogleCast) }
                            ))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Queue & autoplay",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.queue_music),
                            title = { Text(stringResource(R.string.persistent_queue)) },
                            description = { Text(stringResource(R.string.persistent_queue_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = persistentQueue,
                                    onCheckedChange = onPersistentQueueChange
                                )
                            },
                            onClick = { onPersistentQueueChange(!persistentQueue) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.similar),
                            title = { Text(stringResource(R.string.enable_similar_content)) },
                            description = { Text(stringResource(R.string.similar_content_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = similarContentEnabled,
                                    onCheckedChange = similarContentEnabledChange
                                )
                            },
                            onClick = { similarContentEnabledChange(!similarContentEnabled) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.shuffle),
                            title = { Text(stringResource(R.string.remember_shuffle_and_repeat)) },
                            description = { Text(stringResource(R.string.remember_shuffle_and_repeat_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = rememberShuffleAndRepeat,
                                    onCheckedChange = onRememberShuffleAndRepeatChange
                                )
                            },
                            onClick = { onRememberShuffleAndRepeatChange(!rememberShuffleAndRepeat) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.queue_music),
                            title = { Text(stringResource(R.string.ask_before_replacing_queue)) },
                            description = { Text(stringResource(R.string.ask_before_replacing_queue_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = askBeforeReplacingQueue,
                                    onCheckedChange = onAskBeforeReplacingQueueChange
                                )
                            },
                            onClick = { onAskBeforeReplacingQueueChange(!askBeforeReplacingQueue) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.skip_next),
                            title = { Text(stringResource(R.string.auto_skip_next_on_error)) },
                            description = { Text(stringResource(R.string.auto_skip_next_on_error_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = autoSkipNextOnError,
                                    onCheckedChange = onAutoSkipNextOnErrorChange
                                )
                            },
                            onClick = { onAutoSkipNextOnErrorChange(!autoSkipNextOnError) }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Downloads",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.download),
                            title = { Text(stringResource(R.string.auto_download_on_like)) },
                            description = { Text(stringResource(R.string.auto_download_on_like_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = autoDownloadOnLike,
                                    onCheckedChange = onAutoDownloadOnLikeChange
                                )
                            },
                            onClick = { onAutoDownloadOnLikeChange(!autoDownloadOnLike) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.download),
                            title = { Text(stringResource(R.string.save_downloads_to_music)) },
                            description = { Text(stringResource(R.string.save_downloads_to_music_desc)) },
                            trailingContent = {
                                Switch(
                                    checked = saveDownloadsToMusic,
                                    onCheckedChange = onSaveDownloadsToMusicChange
                                )
                            },
                            onClick = { onSaveDownloadsToMusicChange(!saveDownloadsToMusic) }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Local music",
                    items = buildList {
                        add(IosSettingsItem(
                            icon = painterResource(R.drawable.music_note),
                            title = { Text(stringResource(R.string.scan_local_files)) },
                            description = { Text(stringResource(R.string.scan_local_files_desc)) },
                            onClick = { navController.navigate("settings/scan_music") }
                        ))
                    }
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Music haptics",
                    items = listOf(
                        IosSettingsItem(
                            title = { Text("Feel the beat") },
                            description = { Text("Vibrate to the beat of songs stored on this device") },
                            trailingContent = {
                                Switch(
                                    checked = musicHaptics,
                                    onCheckedChange = onMusicHapticsChange
                                )
                            },
                            onClick = { onMusicHapticsChange(!musicHaptics) }
                        ),
                        IosSettingsItem(
                            title = { Text("Sensitivity") },
                            description = {
                                Slider(
                                    value = hapticSensitivity,
                                    onValueChange = onHapticSensitivityChange,
                                    valueRange = 0f..1f
                                )
                            },
                            enabled = musicHaptics
                        ),
                        IosSettingsItem(
                            title = { Text("Strength") },
                            description = {
                                Slider(
                                    value = hapticStrength,
                                    onValueChange = onHapticStrengthChange,
                                    valueRange = 0f..1f
                                )
                            },
                            enabled = musicHaptics
                        )
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = "Behaviour",
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.volume_off_pause),
                            title = { Text(stringResource(R.string.pause_music_when_media_is_muted)) },
                            trailingContent = {
                                Switch(
                                    checked = pauseOnMute,
                                    onCheckedChange = onPauseOnMuteChange
                                )
                            },
                            onClick = { onPauseOnMuteChange(!pauseOnMute) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.bluetooth),
                            title = { Text(stringResource(R.string.resume_on_bluetooth_connect)) },
                            trailingContent = {
                                Switch(
                                    checked = resumeOnBluetoothConnect,
                                    onCheckedChange = onResumeOnBluetoothConnectChange
                                )
                            },
                            onClick = { onResumeOnBluetoothConnectChange(!resumeOnBluetoothConnect) }
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.screenshot),
                            title = { Text(stringResource(R.string.keep_screen_on_when_player_is_expanded)) },
                            trailingContent = {
                                Switch(
                                    checked = keepScreenOn,
                                    onCheckedChange = onKeepScreenOnChange
                                )
                            },
                            onClick = { onKeepScreenOnChange(!keepScreenOn) }
                        )
                    )
                )

                Spacer(modifier = Modifier.height(27.dp))

                IosSettingsGroup(
                    title = stringResource(R.string.advanced),
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.tune),
                            title = { Text(stringResource(R.string.advanced)) },
                            description = { Text(stringResource(R.string.advanced_settings_desc)) },
                            onClick = { navController.navigate("settings/player/advanced") }
                        )
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
