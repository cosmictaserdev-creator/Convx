/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.ui.component.BottomSheetState

/** Status-bar icon colour and keep-screen-on while the sheet is expanded; skipped for the DIY preview copy. */
@Composable
fun PlayerWindowEffects(
    state: BottomSheetState,
    isPreviewOnly: Boolean,
    playerBackground: PlayerBackgroundStyle,
    useDarkTheme: Boolean,
    keepScreenOn: Boolean,
) {
    val context = LocalContext.current
    DisposableEffect(playerBackground, state.isExpanded, useDarkTheme, keepScreenOn, isPreviewOnly) {
        // The editor mounts a second copy; without this guard both would fight over
        // the same real window's status bar icon color / keep-screen-on flag.
        if (isPreviewOnly) return@DisposableEffect onDispose {}
        val window = (context as? android.app.Activity)?.window
        if (window != null && state.isExpanded) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        
            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.GLOW_ANIMATED, PlayerBackgroundStyle.APPLE_MUSIC, PlayerBackgroundStyle.LIVE_MESH, PlayerBackgroundStyle.FLUID_MESH, PlayerBackgroundStyle.STATIC, PlayerBackgroundStyle.CUSTOM_GRADIENT, PlayerBackgroundStyle.GLASS -> {
                    insetsController.isAppearanceLightStatusBars = false
                }
                PlayerBackgroundStyle.DEFAULT -> {
                    insetsController.isAppearanceLightStatusBars = !useDarkTheme
                }
            }

            if (keepScreenOn && state.isExpanded)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    
        onDispose {
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !useDarkTheme
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}

/** Wires this sheet's drag into the shared container morph; skipped for the DIY preview copy. */
@Composable
fun PlayerMorphHostEffects(state: BottomSheetState, isPreviewOnly: Boolean) {
    // Publishes this sheet's progress to the shared container morph. Read only in the
    // draw phase (see PlayerMorph), so nothing recomposes per frame because of it.
    DisposableEffect(state, isPreviewOnly) {
        // The DIY editor mounts a second BottomSheetPlayer alongside the real one. PlayerMorph
        // is a global singleton (one morph, shared across the whole app) -- if the preview
        // instance wired these hooks too, its onDispose (editor closed) would reset the REAL
        // player's hooks to no-ops, leaving the mini-pill drag dead until the next recompose.
        if (isPreviewOnly) return@DisposableEffect onDispose {}
        PlayerMorph.progressProvider = { state.progress }
        PlayerMorph.onDragStart = { fromMini -> state.onDragStart(fromMini) }
        PlayerMorph.onDragDelta = { state.dispatchRawDelta(it) }
        PlayerMorph.onDragEnd = { state.performFling(it, null) }
        // Settle to the nearest anchor when the system cancels a drag.
        PlayerMorph.onDragCancel = { state.performFling(0f, null) }
        onDispose {
            PlayerMorph.progressProvider = { 0f }
            PlayerMorph.onDragStart = {}
            PlayerMorph.onDragDelta = {}
            PlayerMorph.onDragEnd = {}
            PlayerMorph.onDragCancel = {}
        }
    }
}
