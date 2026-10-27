/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.convx.music.R
import com.convx.music.constants.AutoDjMixingEnabledKey
import com.convx.music.constants.CreativeTransitionsEnabledKey
import com.convx.music.constants.CrossfadeDurationKey
import com.convx.music.constants.CrossfadeEnabledKey
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.DjReverbTypeKey
import com.convx.music.constants.DjDelayTypeKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.playback.dj.LibraryAnalysis
import com.convx.music.playback.smart.DemucsInstall
import com.convx.music.playback.smart.DemucsModel
import com.convx.music.playback.smart.StemQuality
import com.convx.music.playback.smart.StemStore
import com.convx.music.constants.DemucsModelUrlKey
import com.convx.music.constants.DemucsAutoDownloadKey
import com.convx.music.playback.smart.StemLab
import androidx.compose.runtime.LaunchedEffect
import com.convx.music.constants.StemQualityKey
import com.convx.music.ui.component.TextFieldDialog
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.convx.music.playback.audio.ReverbType
import com.convx.music.playback.audio.DelayType
import com.convx.music.ui.component.EnumDialog
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Slider
import com.convx.music.ui.ios.compat.Switch
import com.convx.music.ui.ios.compat.Text
import com.convx.music.utils.rememberPreference
import com.convx.music.utils.rememberEnumPreference

/** Apple's slider runs 1s to 12s; older installs may have stored up to 15s, so clamp on read. */
private const val MinFadeSeconds = 1f
private const val MaxFadeSeconds = 12f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongTransitionsScreen(navController: NavController) {
    val (enabled, onEnabledChange) = rememberPreference(CrossfadeEnabledKey, defaultValue = false)
    val (autoMix, onAutoMixChange) = rememberPreference(AutoDjMixingEnabledKey, defaultValue = false)
    val (duration, onDurationChange) = rememberPreference(CrossfadeDurationKey, defaultValue = 5f)
    val (creative, onCreativeChange) = rememberPreference(CreativeTransitionsEnabledKey, defaultValue = false)
    val (bitPerfect) = rememberPreference(BitPerfectKey, false)
    val (reverb, onReverbChange) = rememberEnumPreference(DjReverbTypeKey, ReverbType.AUTO)
    val (delay, onDelayChange) = rememberEnumPreference(DjDelayTypeKey, DelayType.AUTO)
    var showReverbDialog by rememberSaveable { mutableStateOf(false) }
    var showDelayDialog by rememberSaveable { mutableStateOf(false) }
    val transitionsAvailable = enabled && !bitPerfect
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = com.convx.music.LocalDatabase.current.get()
    val analysis by LibraryAnalysis.progress.collectAsStateWithLifecycle()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var stemBytes by remember { mutableStateOf(StemStore.totalBytes(context)) }
    val (stemQuality, onStemQualityChange) = rememberEnumPreference(StemQualityKey, StemQuality.FAST)
    val (modelUrl, onModelUrlChange) = rememberPreference(DemucsModelUrlKey, DemucsModel.DEFAULT_URL)
    val (autoDownload, onAutoDownloadChange) = rememberPreference(DemucsAutoDownloadKey, false)
    var showStemQualityDialog by rememberSaveable { mutableStateOf(false) }
    var showLibraryStemNotice by rememberSaveable { mutableStateOf(false) }
    var libraryStemBytes by remember { mutableStateOf(0L) }
    var showModelDialog by rememberSaveable { mutableStateOf(false) }
    var modelInstalled by remember { mutableStateOf(DemucsModel.isInstalled(context)) }
    // Shared with the automatic download, which may be running without this screen having started it.
    val modelProgress by DemucsModel.progress.collectAsStateWithLifecycle()
    LaunchedEffect(modelProgress) { if (modelProgress == null) modelInstalled = DemucsModel.isInstalled(context) }
    val stemLab by StemLab.state.collectAsStateWithLifecycle()
    val playerConnection = com.convx.music.LocalPlayerConnection.current
    val nowPlaying by (playerConnection?.mediaMetadata ?: kotlinx.coroutines.flow.MutableStateFlow(null)).collectAsStateWithLifecycle()

    stemLab?.takeIf { it !is StemLab.State.Running }?.let { result ->
        com.convx.music.ui.ios.compat.AlertDialog(
            onDismissRequest = { StemLab.dismiss() },
            title = { Text(stringResource(R.string.stem_lab)) },
            text = {
                Text(
                    when (result) {
                        is StemLab.State.Done -> (listOf(stringResource(R.string.stem_lab_saved, result.folder)) + result.report).joinToString("\n")
                        is StemLab.State.Failed -> result.reason
                        is StemLab.State.Running -> ""
                    }
                )
            },
            confirmButton = {
                com.convx.music.ui.ios.compat.TextButton(onClick = { StemLab.dismiss() }) { Text(stringResource(android.R.string.ok)) }
            },
        )
    }

    if (showLibraryStemNotice) {
        com.convx.music.ui.component.StemNoticeDialog(
            bytes = libraryStemBytes,
            onDismiss = { showLibraryStemNotice = false },
            onConfirm = {
                showLibraryStemNotice = false
                scope.launch(Dispatchers.IO) { LibraryAnalysis.analyzeLibrary(context, database, stems = true) }
            },
        )
    }
    if (showStemQualityDialog) {
        EnumDialog(
            title = stringResource(R.string.stem_quality),
            current = stemQuality,
            values = StemQuality.entries,
            valueText = {
                stringResource(if (it == StemQuality.FAST) R.string.stem_quality_fast else R.string.stem_quality_demucs)
            },
            onSelect = {
                onStemQualityChange(it)
                showStemQualityDialog = false
            },
            onDismiss = { showStemQualityDialog = false },
        )
    }
    if (showModelDialog) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.demucs_model)) },
            initialTextFieldValue = TextFieldValue(modelUrl.ifBlank { DemucsModel.DEFAULT_URL }),
            placeholder = { Text("https://…/htdemucs.onnx") },
            extraContent = {
                Text(
                    stringResource(
                        R.string.demucs_notice_default,
                        android.text.format.Formatter.formatShortFileSize(context, DemucsModel.DEFAULT_BYTES),
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                )
            },
            onDone = { url ->
                val chosen = url.trim().ifBlank { DemucsModel.DEFAULT_URL }
                onModelUrlChange(chosen)
                showModelDialog = false
                scope.launch {
                    val result = DemucsModel.install(context, chosen)
                    modelInstalled = DemucsModel.isInstalled(context)
                    val message = when (result) {
                        DemucsInstall.OK -> R.string.demucs_ready
                        DemucsInstall.DOWNLOAD_FAILED -> R.string.demucs_download_failed
                        DemucsInstall.CORRUPT -> R.string.demucs_corrupt
                        DemucsInstall.INCOMPATIBLE -> R.string.demucs_incompatible
                    }
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                }
            },
            onDismiss = { showModelDialog = false },
        )
    }
    if (showReverbDialog) {
        EnumDialog(
            title = stringResource(R.string.dj_reverb_type),
            current = reverb,
            values = ReverbType.entries,
            valueText = { it.displayName() },
            onSelect = {
                onReverbChange(it)
                showReverbDialog = false
            },
            onDismiss = { showReverbDialog = false },
        )
    }
    if (showDelayDialog) {
        EnumDialog(
            title = stringResource(R.string.dj_delay_type),
            current = delay,
            values = DelayType.entries,
            valueText = { it.displayName() },
            onSelect = {
                onDelayChange(it)
                showDelayDialog = false
            },
            onDismiss = { showDelayDialog = false },
        )
    }

    SettingsLazyPage(
        navController = navController,
        title = stringResource(R.string.song_transitions),
        horizontalPadding = 16.dp,
    ) {
        item(key = "song_transitions_content") {
            Column {
                IosSettingsGroup(
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.ic_queue_crossfade),
                            title = { Text(stringResource(R.string.song_transitions)) },
                            trailingContent = { Switch(checked = enabled, onCheckedChange = onEnabledChange) },
                            onClick = { onEnabledChange(!enabled) },
                        ),
                    ),
                )
                Footnote(stringResource(R.string.song_transitions_desc))
                if (bitPerfect) Footnote(stringResource(R.string.dj_bit_perfect_note))

                Spacer(Modifier.height(8.dp))
                IosSettingsGroup(
                    title = stringResource(R.string.transition_style),
                    items = listOf(
                        IosSettingsItem(
                            icon = painterResource(R.drawable.equalizer),
                            title = { Text(stringResource(R.string.transition_automix)) },
                            description = { Text(stringResource(R.string.transition_automix_desc)) },
                            enabled = transitionsAvailable,
                            trailingContent = { if (transitionsAvailable && autoMix) CheckMark() else Spacer(Modifier.size(24.dp)) },
                            onClick = if (transitionsAvailable) {
                                { onAutoMixChange(true) }
                            } else null,
                        ),
                        IosSettingsItem(
                            icon = painterResource(R.drawable.ic_queue_crossfade),
                            title = { Text(stringResource(R.string.crossfade)) },
                            description = { Text(stringResource(R.string.transition_crossfade_desc)) },
                            enabled = transitionsAvailable,
                            trailingContent = { if (transitionsAvailable && !autoMix) CheckMark() else Spacer(Modifier.size(24.dp)) },
                            onClick = if (transitionsAvailable) {
                                { onAutoMixChange(false) }
                            } else null,
                        ),
                    ),
                )

                if (transitionsAvailable && autoMix) {
                    Spacer(Modifier.height(16.dp))
                    IosSettingsGroup(
                        items = listOf(
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.creative_transitions)) },
                                description = { Text(stringResource(R.string.creative_transitions_desc)) },
                                trailingContent = { Switch(checked = creative, onCheckedChange = onCreativeChange) },
                                onClick = { onCreativeChange(!creative) },
                            ),
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    IosSettingsGroup(
                        items = listOf(
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.analyze_library)) },
                                description = {
                                    Text(
                                        if (analysis.running) {
                                            stringResource(R.string.analyze_library_progress, analysis.done, analysis.total)
                                        } else stringResource(R.string.analyze_library_desc)
                                    )
                                },
                                onClick = {
                                    if (analysis.running) LibraryAnalysis.cancel()
                                    else scope.launch(Dispatchers.IO) { LibraryAnalysis.analyzeLibrary(context, database) }
                                },
                            ),
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.split_stems_library)) },
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        val stems = if (stemQuality == StemQuality.DEMUCS && modelInstalled) 4 else 2
                                        libraryStemBytes = database.localDurationTotal() * stems * 96_000L / 8L
                                        showLibraryStemNotice = true
                                    }
                                },
                            ),
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.stem_quality)) },
                                description = {
                                    Text(stringResource(if (stemQuality == StemQuality.FAST) R.string.stem_quality_fast else R.string.stem_quality_demucs))
                                },
                                onClick = { showStemQualityDialog = true },
                            ),
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.demucs_model)) },
                                description = {
                                    Text(
                                        modelProgress?.let { stringResource(R.string.demucs_downloading, (it * 100).toInt()) }
                                            ?: if (modelInstalled) {
                                                stringResource(R.string.demucs_installed, android.text.format.Formatter.formatShortFileSize(context, DemucsModel.sizeBytes(context)))
                                            } else stringResource(R.string.demucs_missing),
                                    )
                                },
                                onClick = {
                                    if (modelProgress == null) {
                                        if (modelInstalled) {
                                            DemucsModel.delete(context)
                                            modelInstalled = false
                                        } else showModelDialog = true
                                    }
                                },
                            ),
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.demucs_auto_download)) },
                                description = { Text(stringResource(R.string.demucs_auto_download_desc)) },
                                trailingContent = {
                                    Switch(
                                        checked = autoDownload,
                                        onCheckedChange = { on ->
                                            onAutoDownloadChange(on)
                                            if (on) scope.launch(Dispatchers.IO) { DemucsModel.autoDownloadIfWanted(context, enabled = true) }
                                        },
                                    )
                                },
                                onClick = {
                                    onAutoDownloadChange(!autoDownload)
                                    if (!autoDownload) scope.launch(Dispatchers.IO) { DemucsModel.autoDownloadIfWanted(context, enabled = true) }
                                },
                            ),
                            IosSettingsItem(
                                icon = painterResource(R.drawable.equalizer),
                                title = { Text(stringResource(R.string.stems_storage)) },
                                description = { Text(android.text.format.Formatter.formatShortFileSize(context, stemBytes)) },
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        StemStore.clear(context)
                                        stemBytes = StemStore.totalBytes(context)
                                    }
                                },
                            ),
                        ),
                    )
                    if (com.convx.music.BuildConfig.DEBUG) {
                        Spacer(Modifier.height(16.dp))
                        IosSettingsGroup(
                            title = stringResource(R.string.stem_lab),
                            items = listOf(
                                IosSettingsItem(
                                    icon = painterResource(R.drawable.equalizer),
                                    title = { Text(stringResource(R.string.stem_lab_split_now_playing)) },
                                    description = {
                                        Text(
                                            (stemLab as? StemLab.State.Running)?.step
                                                ?: nowPlaying?.title?.let { stringResource(R.string.stem_lab_desc, it) }
                                                ?: stringResource(R.string.stem_lab_nothing_playing)
                                        )
                                    },
                                    onClick = {
                                        val song = nowPlaying
                                        when {
                                            stemLab is StemLab.State.Running -> StemLab.cancel()
                                            song != null -> StemLab.run(context, song.id, song.title)
                                        }
                                    },
                                ),
                            ),
                        )
                    }
                    if (creative) {
                        Spacer(Modifier.height(16.dp))
                        IosSettingsGroup(
                            items = listOf(
                                IosSettingsItem(
                                    icon = painterResource(R.drawable.equalizer),
                                    title = { Text(stringResource(R.string.dj_reverb_type)) },
                                    description = { Text(reverb.displayName()) },
                                    onClick = { showReverbDialog = true },
                                ),
                                IosSettingsItem(
                                    icon = painterResource(R.drawable.equalizer),
                                    title = { Text(stringResource(R.string.dj_delay_type)) },
                                    description = { Text(delay.displayName()) },
                                    onClick = { showDelayDialog = true },
                                ),
                            ),
                        )
                        Footnote(stringResource(R.string.dj_effect_programs_desc))
                    }
                }

                if (transitionsAvailable && !autoMix) {
                    Spacer(Modifier.height(16.dp))
                    val seconds = duration.coerceIn(MinFadeSeconds, MaxFadeSeconds)
                    IosSettingsGroup(
                        title = stringResource(R.string.crossfade_duration),
                        items = listOf(
                            IosSettingsItem(
                                title = {
                                    Text(pluralStringResource(R.plurals.seconds, seconds.toInt(), seconds.toInt()))
                                },
                                description = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text("${MinFadeSeconds.toInt()}s", color = AppTheme.colorScheme.onSurfaceVariant)
                                        Slider(
                                            value = seconds,
                                            onValueChange = onDurationChange,
                                            valueRange = MinFadeSeconds..MaxFadeSeconds,
                                            steps = (MaxFadeSeconds - MinFadeSeconds).toInt() - 1,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 8.dp),
                                        )
                                        Text("${MaxFadeSeconds.toInt()}s", color = AppTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                            ),
                        ),
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReverbType.displayName(): String = stringResource(
    when (this) {
        ReverbType.AUTO -> R.string.dj_effect_auto
        ReverbType.ROOM -> R.string.dj_reverb_room
        ReverbType.PLATE -> R.string.dj_reverb_plate
        ReverbType.HALL -> R.string.dj_reverb_hall
    },
)

@Composable
private fun DelayType.displayName(): String = stringResource(
    when (this) {
        DelayType.AUTO -> R.string.dj_effect_auto
        DelayType.STEREO -> R.string.dj_delay_stereo
        DelayType.PING_PONG -> R.string.dj_delay_ping_pong
        DelayType.TAPE -> R.string.dj_delay_tape
        DelayType.FILTERED -> R.string.dj_delay_filtered
        DelayType.SLAPBACK -> R.string.dj_delay_slapback
    },
)

@Composable
private fun CheckMark() {
    SymbolIcon(symbol = sf("checkmark"), contentDescription = null, tint = AppTheme.colorScheme.primary)
}

@Composable
private fun Footnote(text: String) {
    Text(
        text = text,
        color = AppTheme.colorScheme.onSurfaceVariant,
        style = AppTheme.typography.bodySmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
