/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Radius the expanding player sheet should round its corners to at the moment it fills
 * the screen, so those corners sit exactly on the device's glass instead of near it.
 *
 * Read from the window's actual [RoundedCorner] insets where the platform exposes them
 * (API 31+). Below that, and on devices that report no rounded corners at all, this
 * falls back to [FallbackScreenCornerRadius] — a wrong-but-plausible curve reads far
 * better than a square corner on a rounded phone.
 *
 * Returns 0.dp when the player is not filling the display (split screen, PiP): the
 * window's corners are not the screen's there, so matching the physical radius would
 * draw a curve that lines up with nothing.
 */
@Composable
fun rememberScreenCornerRadius(): Dp {
    val view = LocalView.current
    val context = LocalContext.current
    val density = androidx.compose.ui.platform.LocalDensity.current

    // Keyed on the view: a configuration change that could alter any of this (rotation,
    // entering split screen) recreates it, and nothing else can change these values
    // underneath us mid-composition.
    return remember(view, context) {
        val activity = context.findActivity()
        val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && activity != null) {
            activity.windowManager.currentWindowMetrics.bounds to
                activity.windowManager.maximumWindowMetrics.bounds
        } else {
            null
        }

        val fillsScreen = if (metrics == null) {
            true
        } else {
            val (current, maximum) = metrics
            playerWindowUsesPhysicalScreenCorners(
                currentWidth = current.width(),
                currentHeight = current.height(),
                maximumWidth = maximum.width(),
                maximumHeight = maximum.height(),
                isInMultiWindowMode = activity?.isInMultiWindowMode == true,
                isInPictureInPictureMode = activity?.isInPictureInPictureMode == true,
            )
        }
        if (!fillsScreen) return@remember 0.dp

        val physicalPx = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val insets = view.rootWindowInsets
            listOfNotNull(
                insets?.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius,
                insets?.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius,
            ).maxOrNull()
        } else {
            null
        }

        if (physicalPx != null && physicalPx > 0) {
            with(density) { physicalPx.toDp() }
        } else {
            FallbackScreenCornerRadius
        }
    }
}

/**
 * Used when the platform will not tell us the real corner radius. Sized to a typical
 * modern phone rather than to any particular device — it is only ever on screen for the
 * final stretch of the expand animation before the corners open out to square.
 */
val FallbackScreenCornerRadius = 28.dp

private fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
