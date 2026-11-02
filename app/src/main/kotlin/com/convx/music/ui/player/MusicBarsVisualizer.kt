/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.withFrameNanos
import com.convx.music.constants.VisualizerDelayKey
import com.convx.music.constants.VisualizerEnabledKey
import com.convx.music.playback.audio.LiveSpectrum
import com.convx.music.utils.rememberPreference
import kotlin.math.pow

/** Bars that bounce with the music being played; draws nothing unless it is turned on in settings. */
@Composable
fun MusicBarsVisualizer(
    modifier: Modifier = Modifier,
    color: Color,
) {
    val (enabled) = rememberPreference(VisualizerEnabledKey, defaultValue = false)
    val (delayMs) = rememberPreference(VisualizerDelayKey, defaultValue = 220f)
    if (!enabled) return

    DisposableEffect(Unit) {
        LiveSpectrum.active = true
        onDispose { LiveSpectrum.active = false }
    }

    var shown by remember { mutableStateOf(FloatArray(LiveSpectrum.BANDS)) }
    LaunchedEffect(delayMs) {
        val target = FloatArray(LiveSpectrum.BANDS)
        val smooth = FloatArray(LiveSpectrum.BANDS)
        while (true) {
            withFrameNanos { }
            LiveSpectrum.sample(delayMs.toLong(), target)
            for (i in smooth.indices) {
                // Rise quickly and fall slowly, so the bars read as bounces and not flicker.
                smooth[i] = if (target[i] > smooth[i]) {
                    smooth[i] + (target[i] - smooth[i]) * 0.6f
                } else {
                    smooth[i] * 0.86f + target[i] * 0.14f
                }
            }
            shown = smooth.copyOf()
        }
    }

    Canvas(modifier = modifier) {
        val count = shown.size
        val gap = size.width * 0.012f
        val barWidth = (size.width - gap * (count - 1)) / count
        for (i in 0 until count) {
            val barHeight = (shown[i].pow(1.4f) * size.height).coerceAtLeast(barWidth)
            drawRoundRect(
                color = color.copy(alpha = 0.30f),
                topLeft = Offset(i * (barWidth + gap), size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f),
            )
        }
    }
}
