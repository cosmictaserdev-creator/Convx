/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.convx.music.db.MusicDatabase
import com.convx.music.di.ValueProvider
import com.convx.music.playback.DownloadUtil
import com.convx.music.playback.PlayerConnection
import com.convx.music.ui.screens.settings.RingtoneViewModel
import com.convx.music.utils.SyncUtils

val LocalDatabase = staticCompositionLocalOf<ValueProvider<MusicDatabase>> { error("No database provided") }
val LocalRingtoneViewModel = staticCompositionLocalOf<RingtoneViewModel> { error("No RingtoneViewModel provided") }
val LocalPlayerConnection = staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
val LocalPlayerAwareWindowInsets = compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalDownloadUtil = staticCompositionLocalOf<ValueProvider<DownloadUtil>> { error("No DownloadUtil provided") }
val LocalSyncUtils = staticCompositionLocalOf<ValueProvider<SyncUtils>> { error("No SyncUtils provided") }
val LocalListenTogetherManager = staticCompositionLocalOf<com.convx.music.listentogether.ListenTogetherManager?> { null }
val LocalIsPlayerExpanded = compositionLocalOf { false }

/**
 * Current screen artwork tint shared with the navigation bar and mini player.
 * Screens without a hero should publish [Color.Unspecified] to avoid reusing a stale tint.
 *
 * This state is process-wide because these readers are siblings of the NavHost.
 */
private val ambientBackgroundColorState = mutableStateOf(Color.Unspecified)
val LocalAmbientBackgroundColor = staticCompositionLocalOf { ambientBackgroundColorState }
// Artwork pages can keep dark chrome while the rest of the app uses light mode.
// Shared with the sibling tab bar, mini player and system bars.
private val ambientDarkChromeState = mutableStateOf(false)
val LocalAmbientDarkChrome = staticCompositionLocalOf { ambientDarkChromeState }

/** Publishes the screen tint used by the navigation bar and resets it when the screen leaves. */
@Composable
fun PublishAmbientBackgroundColor(color: Color, darkChrome: Boolean = false) {
    val ambient = LocalAmbientBackgroundColor.current
    val ambientDark = LocalAmbientDarkChrome.current
    // A disposed screen must not clear a tint already published by the next screen.
    val owner = LocalLifecycleOwner.current
    val latest by rememberUpdatedState(color)
    val latestDark by rememberUpdatedState(darkChrome)
    LaunchedEffect(color, darkChrome) {
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            ambient.value = color
            ambientDark.value = darkChrome
        }
    }
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    ambient.value = latest
                    ambientDark.value = latestDark
                }
                Lifecycle.Event.ON_PAUSE ->
                    if (ambient.value == latest) {
                        ambient.value = Color.Unspecified
                        ambientDark.value = false
                    }
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
            if (ambient.value == latest) {
                ambient.value = Color.Unspecified
                ambientDark.value = false
            }
        }
    }
}

/**
 * True while the app is laid out for tab view — the vertical floating side bar
 * instead of the bottom bar. Screens read this to switch their header to the
 * wide arrangement (hero as a card with its title beside it) rather than the
 * phone's full-bleed hero.
 */
val LocalTabView = compositionLocalOf { false }
