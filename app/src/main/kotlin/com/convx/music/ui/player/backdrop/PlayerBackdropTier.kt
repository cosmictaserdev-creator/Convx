/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.backdrop

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

internal enum class PlayerBackdropTier { A, B, C }

private const val RECOVERY_MS = 12_000L
private const val FRAME_SAMPLE_SIZE = 12
private const val SLOW_FRAME_COUNT = 4
private const val SLOW_FRAME_NANOS = 25_000_000L
private const val PAUSED_FRAME_NANOS = 200_000_000L
private const val FRAME_SAMPLE_PAUSE_MS = 700L

@Composable
internal fun rememberPlayerBackdropTier(
    extended: Boolean,
    visible: Boolean,
    reducedMotion: Boolean,
): PlayerBackdropTier {
    val context = LocalContext.current
    val view = LocalView.current
    val power = remember(context) { context.getSystemService(PowerManager::class.java) }
    val lowRam = remember(context) {
        context.getSystemService(ActivityManager::class.java)?.isLowRamDevice == true
    }
    val active = extended && visible && !reducedMotion
    var powerSaver by remember(power) { mutableStateOf(power?.isPowerSaveMode == true) }
    var thermal by remember(power) { mutableIntStateOf(currentThermalStatus(power)) }

    DisposableEffect(context, power, active) {
        if (!active || power == null) {
            onDispose {}
        } else {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    powerSaver = power.isPowerSaveMode
                }
            }
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            powerSaver = power.isPowerSaveMode
            thermal = currentThermalStatus(power)
            val stopThermal = if (Build.VERSION.SDK_INT >= 29) {
                observeThermalStatus(context, power) { thermal = it }
            } else null
            onDispose {
                stopThermal?.invoke()
                context.unregisterReceiver(receiver)
            }
        }
    }

    val suggested = when {
        powerSaver || thermal >= PowerManager.THERMAL_STATUS_SEVERE -> PlayerBackdropTier.C
        thermal >= PowerManager.THERMAL_STATUS_MODERATE ||
            lowRam || Build.VERSION.SDK_INT < 33 || !view.isHardwareAccelerated -> PlayerBackdropTier.B
        else -> PlayerBackdropTier.A
    }
    var environmentLimit by remember { mutableStateOf(suggested) }
    LaunchedEffect(suggested) {
        if (suggested < environmentLimit) delay(RECOVERY_MS)
        environmentLimit = suggested
    }
    val environmentTier = maxOf(suggested, environmentLimit)

    var frameLimit by remember { mutableStateOf(PlayerBackdropTier.A) }
    LaunchedEffect(active, environmentTier) {
        if (!active || environmentTier == PlayerBackdropTier.C) return@LaunchedEffect
        var healthySince = 0L
        while (isActive) {
            if (frameLimit == PlayerBackdropTier.C) {
                delay(RECOVERY_MS)
                frameLimit = PlayerBackdropTier.B
                healthySince = 0L
            }
            var previous = withFrameNanos { it }
            var slowFrames = 0
            repeat(FRAME_SAMPLE_SIZE) {
                val now = withFrameNanos { it }
                val interval = now - previous
                if (interval in SLOW_FRAME_NANOS..PAUSED_FRAME_NANOS) slowFrames++
                previous = now
            }
            if (slowFrames >= SLOW_FRAME_COUNT) {
                frameLimit = if (maxOf(environmentTier, frameLimit) == PlayerBackdropTier.A) {
                    PlayerBackdropTier.B
                } else {
                    PlayerBackdropTier.C
                }
                healthySince = 0L
            } else if (frameLimit == PlayerBackdropTier.B) {
                if (healthySince == 0L) healthySince = previous
                if (previous - healthySince >= RECOVERY_MS * 1_000_000L) {
                    frameLimit = PlayerBackdropTier.A
                }
            }
            delay(FRAME_SAMPLE_PAUSE_MS)
        }
    }

    return if (active) maxOf(environmentTier, frameLimit) else PlayerBackdropTier.C
}

private fun currentThermalStatus(power: PowerManager?): Int =
    if (Build.VERSION.SDK_INT >= 29 && power != null) power.currentThermalStatus
    else PowerManager.THERMAL_STATUS_NONE

@RequiresApi(29)
private fun observeThermalStatus(context: Context, power: PowerManager, update: (Int) -> Unit): () -> Unit {
    val listener = PowerManager.OnThermalStatusChangedListener { update(it) }
    power.addThermalStatusListener(ContextCompat.getMainExecutor(context), listener)
    return { power.removeThermalStatusListener(listener) }
}
