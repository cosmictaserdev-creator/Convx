/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.wrapped

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject


sealed class WrappedScreenType {
    object Welcome : WrappedScreenType()
    object MinutesTease : WrappedScreenType()
    object MinutesReveal : WrappedScreenType()
    object TotalSongs : WrappedScreenType()
    object TopSongReveal : WrappedScreenType()
    object Top5Songs : WrappedScreenType()
    object TotalAlbums : WrappedScreenType()
    object TopAlbumReveal : WrappedScreenType()
    object Top5Albums : WrappedScreenType()
    object TotalArtists : WrappedScreenType()
    object TopArtistReveal : WrappedScreenType()
    object Top5Artists : WrappedScreenType()
    object Playlist : WrappedScreenType()
    object Conclusion : WrappedScreenType()
}

@Composable
fun WrappedScreen(navController: NavController) {
    WrappedScreenContent(navController = navController)
}

@Composable
fun WrappedScreenContent(navController: NavController) {
    val onClose: () -> Unit = {
        navController.previousBackStackEntry?.savedStateHandle?.set("wrapped_seen", true)
        navController.popBackStack()
    }
    BackHandler(onBack = onClose)

    val manager = koinInject<WrappedManager>()
    val view = LocalView.current
    val audioService = remember { WrappedAudioService(view.context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val window = (view.context as android.app.Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> audioService.pause()
                Lifecycle.Event.ON_RESUME -> audioService.resume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
            lifecycleOwner.lifecycle.removeObserver(observer)
            audioService.release()
        }
    }

    val state by manager.state.collectAsStateWithLifecycle()
    val isMuted by audioService.isMuted.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        manager.prepare()
    }

    ReplayStory(
        state = state,
        manager = manager,
        isMuted = isMuted,
        onToggleMute = { audioService.toggleMute() },
        onPageAudio = { screen -> audioService.playTrack(state.trackMap[screen]) },
        onClose = onClose,
        onShare = {
            val nf = java.text.NumberFormat.getIntegerInstance()
            val text = "My Replay: ${nf.format(state.totalMinutes)} minutes, " +
                "${nf.format(state.uniqueSongCount)} songs" +
                (state.topArtists.firstOrNull()?.let { ", top artist ${it.artist.name}" } ?: "") +
                " — Convx"
            val send = android.content.Intent(android.content.Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(android.content.Intent.EXTRA_TEXT, text)
            view.context.startActivity(android.content.Intent.createChooser(send, null))
        },
    )
}
