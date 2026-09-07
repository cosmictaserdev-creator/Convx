/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import timber.log.Timber

/**
 * The native YouTube watch screen's video surface: a media3 [PlayerView]
 * (SurfaceView-backed) hosting the shared MusicService player.
 *
 * This mirrors what the proven open-source YouTube clients (Flow's
 * VideoPlayerSurface, NewPipe's player UI) do instead of manually calling
 * player.setVideo*View from Compose: PlayerView owns the entire surface
 * lifecycle — surface (re)creation, keep-content-on-reset, aspect-ratio
 * changes from VideoSize, and graceful degradation when the system destroys
 * the surface (backgrounding) — which is exactly the area where hand-rolled
 * TextureView/SurfaceView wiring dies natively on some devices.
 *
 * Playback itself is untouched: the player keeps rendering audio through the
 * service when this composable leaves composition (mini player), the same
 * as every other screen.
 */
@UnstableApi
@Composable
fun VideoSurface(
    player: Player?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val playerView = remember(context) {
        PlayerView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            // Gesture/overlay controls are Compose-side; PlayerView only renders video.
            useController = false
            setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
            // Show the last frame (black shutter otherwise) across player resets
            // instead of flashing black on every seek/track change.
            setKeepContentOnPlayerReset(true)
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            setShutterBackgroundColor(android.graphics.Color.BLACK)
            subtitleView?.visibility = android.view.View.GONE
        }
    }

    AndroidView(
        factory = { playerView },
        update = { view ->
            if (view.player !== player) {
                Timber.tag("YouTubeVideo").i("PlayerView player attach/detach")
                view.player = player
            }
        },
        modifier = modifier,
    )

    // Unhook the surface when the watch screen leaves composition so the video
    // output releases cleanly (playback continues audio-only via the mini
    // player, same as every other screen in the app).
    androidx.compose.runtime.DisposableEffect(playerView) {
        onDispose {
            playerView.player = null
        }
    }
}
