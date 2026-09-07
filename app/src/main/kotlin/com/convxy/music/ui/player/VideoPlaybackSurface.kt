/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player

import android.view.TextureView
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.convxy.music.LocalPlayerConnection

/**
 * Renders the video track of the shared music player (full YouTube video
 * mode). Same TextureView pattern as [CanvasArtworkPlayer] — a plain view
 * with no Compose state, attached to the service's ExoPlayer for exactly as
 * long as this composable is in the tree. Video only starts rendering once
 * the current stream actually carries a video track (muxed format selected
 * by the resolver when WatchVideoKey is on).
 */
@UnstableApi
@Composable
fun VideoPlaybackSurface(
    modifier: Modifier = Modifier,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val player = playerConnection.player
    val attachedView = remember { mutableStateOf<TextureView?>(null) }

    DisposableEffect(player) {
        onDispose {
            attachedView.value?.let { player.clearVideoTextureView(it) }
            attachedView.value = null
        }
    }

    AndroidView(
        factory = { viewContext ->
            AspectRatioFrameLayout(viewContext).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                val textureView = TextureView(viewContext).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
                addView(textureView)
                player.setVideoTextureView(textureView)
                attachedView.value = textureView
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        modifier = modifier,
    )
}

