package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSleepTimer
import com.convx.music.playback.presentation.PlaybackEqualizerPreset
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import dev.nucleusframework.macoscompose.components.*
import kotlinx.coroutines.delay

@Composable
internal fun DesktopEqualizerDialog(
    preferences: PlaybackSettings,
    settings: PlaybackSettingsStateHolder,
    error: String?,
    onDismiss: () -> Unit,
) {
    val profile = preferences.equalizerProfile.takeIf { it.bands.isNotEmpty() } ?: PlaybackEqualizerPreset.FLAT.profile()
    val enabled = preferences.equalizerEnabled && !preferences.bitPerfectEnabled
    SmallDialog(visible = true, onDismissRequest = onDismiss, title = "Equalizer", confirmText = "Done",
        onConfirm = onDismiss, cancelText = null, size = DialogSize.Large) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(if (preferences.bitPerfectEnabled) "Unavailable during bit-perfect playback" else "Enable equalizer", Modifier.weight(1f), fontSize = 14.sp)
            Switch(preferences.equalizerEnabled, settings::setEqualizerEnabled, enabled = !preferences.bitPerfectEnabled)
        }
        SegmentedControl(listOf("Custom") + PlaybackEqualizerPreset.entries.map { it.title },
            PlaybackEqualizerPreset.entries.indexOfFirst { it.matches(profile) } + 1,
            { if (it > 0) settings.setEqualizerPreset(PlaybackEqualizerPreset.entries[it - 1]) }, enabled = !preferences.bitPerfectEnabled)
        Column(Modifier.fillMaxWidth().heightIn(max = 430.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            profile.bands.forEachIndexed { index, band ->
                var gain by remember(index, band.gain) { mutableFloatStateOf(band.gain.toFloat()) }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (band.frequency >= 1_000) "${band.frequency.toInt() / 1_000}k Hz" else "${band.frequency.toInt()} Hz", Modifier.width(62.dp), fontSize = 12.sp)
                    Slider(gain, { gain = it }, Modifier.weight(1f), enabled = enabled, valueRange = -12f..12f,
                        onValueChangeFinished = { settings.setEqualizerBandGain(index, gain.toDouble()) })
                    Text("${gain.toInt()} dB", Modifier.width(45.dp), fontSize = 12.sp)
                }
            }
        }
        error?.let { Text(it, color = DesktopAccent, fontSize = 13.sp) }
    }
}

@Composable
internal fun DesktopSleepTimerDialog(timer: PlaybackSleepTimer, onDismiss: () -> Unit) {
    val state by timer.state.collectAsState()
    var minutes by remember { mutableFloatStateOf(30f) }
    var remaining by remember { mutableLongStateOf(timer.remainingMillis() ?: 0L) }
    LaunchedEffect(state) {
        while (state.isActive && !state.pauseWhenTrackEnds) { remaining = timer.remainingMillis() ?: 0L; delay(1_000) }
    }
    SmallDialog(visible = true, onDismissRequest = onDismiss, title = "Sleep Timer", confirmText = "Start timer",
        onConfirm = { timer.start(minutes.toInt()); onDismiss() },
        destructiveText = if (state.isActive) "Stop timer" else null,
        onDestructive = { timer.clear(); onDismiss() }) {
        if (state.isActive) Text(if (state.pauseWhenTrackEnds) "Pauses after this track" else "Pauses in ${remaining / 60_000}:${((remaining / 1_000) % 60).toString().padStart(2, '0')}", fontSize = 14.sp)
        Text("${minutes.toInt()} minutes", fontSize = 28.sp)
        Slider(minutes, { minutes = it }, valueRange = 1f..180f)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 30, 45, 60).forEach { value -> PushButton("$value min", { minutes = value.toFloat() }, style = PushButtonStyle.Neutral) }
        }
        PushButton("After this track", { timer.start(-1); onDismiss() }, style = PushButtonStyle.Borderless)
    }
}
