@file:OptIn(zone.ien.hig.ExperimentalCupertinoApi::class)

package com.convx.music.playback.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.designsystem.ConvxDesignTokens
import com.convx.music.playback.EqualizerBand
import com.convx.music.playback.PlaybackSettings
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.math.roundToInt
import zone.ien.hig.CupertinoButton
import zone.ien.hig.CupertinoButtonDefaults.plainButtonColors
import zone.ien.hig.CupertinoModalBottomSheet
import zone.ien.hig.CupertinoSlider
import zone.ien.hig.CupertinoSwitch
import zone.ien.hig.CupertinoText
import zone.ien.hig.PresentationDetent
import zone.ien.hig.PresentationStyle
import zone.ien.hig.rememberCupertinoSheetState
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme

@Composable
fun PlaybackEqualizerSheet(
    settings: PlaybackSettings,
    darkTheme: Boolean,
    errorMessage: String? = null,
    onDismissRequest: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onPresetSelected: (PlaybackEqualizerPreset) -> Unit,
    onBandGainChange: (Int, Double) -> Unit,
) {
    val sheetState = rememberCupertinoSheetState(
        presentationStyle = PresentationStyle.Modal(detents = setOf(PresentationDetent.Large)),
    )
    val sliderBackdrop = rememberLayerBackdrop()
    val profile = settings.equalizerProfile.takeIf { it.bands.isNotEmpty() }
        ?: PlaybackEqualizerPreset.FLAT.profile()
    val selectedPreset = PlaybackEqualizerPreset.values().firstOrNull { it.matches(profile) }

    LaunchedEffect(sheetState) { sheetState.show() }

    CupertinoTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
        CupertinoModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = CupertinoTheme.colorScheme.secondarySystemGroupedBackground,
            contentColor = CupertinoTheme.colorScheme.label,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 760.dp).padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CupertinoText("Equalizer", fontSize = 21.sp)
                    Box(Modifier.weight(1f))
                    CupertinoButton(onClick = onDismissRequest, colors = plainButtonColors()) {
                        CupertinoText("Done")
                    }
                }

                errorMessage?.let { message ->
                    CupertinoText(message, color = ConvxDesignTokens.Colors.AccentRed, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        CupertinoText("Enable EQ")
                        CupertinoText(
                            if (settings.bitPerfectEnabled) "Unavailable while bit-perfect playback is enabled"
                            else if (settings.equalizerEnabled) "Audio shaping is on" else "Audio shaping is off",
                            color = CupertinoTheme.colorScheme.secondaryLabel,
                            fontSize = 13.sp,
                        )
                    }
                    CupertinoSwitch(
                        checked = settings.equalizerEnabled,
                        onCheckedChange = onEnabledChange,
                        enabled = !settings.bitPerfectEnabled,
                        backdrop = sliderBackdrop,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlaybackEqualizerPreset.values().forEach { preset ->
                        EqualizerPresetChip(
                            preset = preset,
                            selected = selectedPreset == preset,
                            enabled = !settings.bitPerfectEnabled,
                            onClick = { onPresetSelected(preset) },
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    itemsIndexed(profile.bands, key = { _, band -> band.frequency }) { index, band ->
                        EqualizerBandSlider(
                            band = band,
                            enabled = settings.equalizerEnabled && !settings.bitPerfectEnabled,
                            backdrop = sliderBackdrop,
                            onGainChange = { onBandGainChange(index, it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EqualizerPresetChip(
    preset: PlaybackEqualizerPreset,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val foreground = if (selected) Color.White else CupertinoTheme.colorScheme.label
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ConvxDesignTokens.Colors.AccentRed else CupertinoTheme.colorScheme.tertiarySystemGroupedBackground)
            .semantics {
                contentDescription = "${preset.title} equalizer preset${if (selected) ", selected" else ""}"
                role = Role.Button
            }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            preset.title,
            style = TextStyle(
                color = if (enabled) foreground else CupertinoTheme.colorScheme.secondaryLabel,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun EqualizerBandSlider(
    band: EqualizerBand,
    enabled: Boolean,
    backdrop: Backdrop,
    onGainChange: (Double) -> Unit,
) {
    var gain by remember(band.frequency) { mutableFloatStateOf(band.gain.toFloat().coerceIn(-12f, 12f)) }
    LaunchedEffect(band.gain) { gain = band.gain.toFloat().coerceIn(-12f, 12f) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CupertinoText(equalizerFrequencyLabel(band.frequency), modifier = Modifier.width(42.dp), fontSize = 12.sp)
        CupertinoSlider(
            value = gain,
            onValueChange = { gain = it },
            modifier = Modifier.weight(1f).heightIn(min = 34.dp),
            enabled = enabled,
            valueRange = -12f..12f,
            steps = 239,
            backdrop = backdrop,
            onValueChangeFinished = { onGainChange(gain.toDouble()) },
        )
        CupertinoText("${gain.roundToInt()} dB", modifier = Modifier.width(42.dp), fontSize = 12.sp)
    }
}

private fun equalizerFrequencyLabel(frequency: Double): String = when {
    frequency >= 1_000.0 -> "${(frequency / 1_000.0).toInt()}k"
    else -> "${frequency.toInt()}"
}
