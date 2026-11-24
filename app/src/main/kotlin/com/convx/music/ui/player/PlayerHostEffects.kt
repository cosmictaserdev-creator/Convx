/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import android.view.WindowManager
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.ui.component.BottomSheetState
import com.convx.music.designsystem.MotionTokens
import kotlinx.coroutines.launch

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

/** Wires this sheet's drag and landing into the shared container morph; skipped for the DIY preview copy. */
@Composable
fun PlayerMorphHostEffects(state: BottomSheetState, isPreviewOnly: Boolean) {
    // Publishes this sheet's progress to the shared container morph. Read only in the
    // draw phase (see PlayerMorph), so nothing recomposes per frame because of it.
    val landingScope = rememberCoroutineScope()
    val landingDensity = LocalDensity.current
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
        // Close lands with speed: carry it into the pill as a dip below its slot + settle.
        // Armed from the start of every close, so the card is still drawn on the frame the
        // sheet reaches its bound; disarmed if the close never lands.
        state.onCollapseArmed = { armed ->
            if (armed && PlayerMorph.sharedLayersReady) {
                landingGeneration++
                PlayerMorph.landingActive = true
            } else if (!armed) {
                PlayerMorph.landingActive = false
            }
        }
        state.onCollapseLanded = { velocityDp ->
            // Start in this frame so the landing animation stays in sync with the card.
            landingScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                playPillLanding(velocityDp, state, landingDensity)
            }
        }
        onDispose {
            PlayerMorph.progressProvider = { 0f }
            PlayerMorph.onDragStart = {}
            PlayerMorph.onDragDelta = {}
            PlayerMorph.onDragEnd = {}
            PlayerMorph.onDragCancel = {}
            state.onCollapseLanded = null
            state.onCollapseArmed = null
            PlayerMorph.landingActive = false
        }
    }
}

/**
 * Carries the close spring's arrival speed through the landing. The sheet stops on its bound
 * at [sheetVelocityDp] (sheet dp/s, negative = closing); [PlayerMorph.landing] picks the
 * motion up in the same progress units at the same speed, so the card sails past the pill's
 * slot and springs back as ONE motion -- a fast flick dips further, a tap a little.
 */
internal suspend fun playPillLanding(
    sheetVelocityDp: Float,
    state: BottomSheetState,
    density: androidx.compose.ui.unit.Density,
) {
    // The close armed this landing (see onCollapseArmed); a newer close re-arms and takes
    // over, and only the newest may clear the flag.
    val generation = landingGeneration
    val pill = miniPlayerContainerRect
    val rangeDp = (state.expandedBound - state.collapsedBound).value
    val progressVelocity = if (rangeDp > 0f) sheetVelocityDp / rangeDp else 0f
    if (pill == null || !PlayerMorph.landingActive || progressVelocity >= 0f) {
        // Nothing to carry: hand straight back to the docked pill (never leave it hidden).
        if (landingGeneration == generation) PlayerMorph.landingActive = false
        return
    }
    val fullHeightPx = with(density) { state.expandedBound.toPx() }
    // Card centre per unit of progress near the pill end: screen centre -> pill centre.
    PlayerMorph.landingTravelPx = pill.center.y - fullHeightPx / 2f
    PlayerMorph.landingMaxDipPx = with(density) { LANDING_MAX_DIP.toPx() }
    try {
        PlayerMorph.landing.snapTo(0f)
        PlayerMorph.landing.animateTo(
            targetValue = 0f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = LANDING_DAMPING,
                stiffness = LANDING_STIFFNESS,
            ),
            initialVelocity = progressVelocity,
        )
    } finally {
        if (landingGeneration == generation) PlayerMorph.landingActive = false
    }
}

internal var landingGeneration = 0

/** Slow landing rebound with one soft secondary bounce. */
private const val LANDING_DAMPING = MotionTokens.PlayerLandingDamping
private const val LANDING_STIFFNESS = MotionTokens.PlayerLandingStiffness
private val LANDING_MAX_DIP = MotionTokens.PlayerLandingMaxDipDp.dp
