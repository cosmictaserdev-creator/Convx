/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Visual weight of a material surface, from the clearest to the most opaque. */
enum class IosMaterial { UltraThin, Thin, Regular, Thick }

fun IosMaterial.fillAlpha(): Float = when (this) {
    IosMaterial.UltraThin -> 0.28f
    IosMaterial.Thin -> 0.45f
    IosMaterial.Regular -> 0.65f
    IosMaterial.Thick -> 0.85f
}

/** Selects a label color and avoids quaternary text on the thinnest materials. */
fun IosColors.vibrantLabel(tier: Int, material: IosMaterial = IosMaterial.Regular): Color {
    val thinMaterial = material == IosMaterial.Thin || material == IosMaterial.UltraThin
    val safeTier = if (tier >= 3 && thinMaterial) 2 else tier.coerceIn(0, 3)
    return when (safeTier) {
        0 -> label
        1 -> secondaryLabel
        2 -> tertiaryLabel
        else -> quaternaryLabel
    }
}

/** Returns whether the foreground and background meet the requested WCAG contrast ratio. */
fun meetsContrast(foreground: Color, background: Color, ratio: Float = 4.5f): Boolean {
    val foregroundLuminance = foreground.luminance().toDouble() + 0.05
    val backgroundLuminance = background.luminance().toDouble() + 0.05
    return maxOf(foregroundLuminance, backgroundLuminance) /
        minOf(foregroundLuminance, backgroundLuminance) >= ratio.toDouble()
}

val chromeOnProminent: Color get() = Color.White
