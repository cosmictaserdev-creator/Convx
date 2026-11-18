/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import android.net.Uri
import android.view.TextureView
import android.view.ViewGroup
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.player.video.PlayerVideoBuffers
import com.convx.music.ui.player.video.PlayerVideoDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/** Muted Home background for a video copied into the app's private files directory. */
@Composable
fun BoxScope.HomeVideoBackground(
    path: String,
    blur: Float,
    dim: Float,
    withGradient: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var inForeground by remember(lifecycle) {
        mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            inForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val exoPlayer = remember(context, path) {
        PlayerVideoDecoder.build(
            context,
            ProgressiveMediaSource.Factory(FileDataSource.Factory()),
            PlayerVideoDecoder.Use.BACKGROUND,
        )
    }
    var isReady by remember(exoPlayer) { mutableStateOf(false) }
    var failed by remember(exoPlayer) { mutableStateOf(false) }
    var isVisible by remember(exoPlayer) { mutableStateOf(false) }
    val shouldPlay = isVisible && inForeground

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() {
                if (PlayerVideoDecoder.owns(exoPlayer)) isReady = true
            }

            override fun onPlayerError(error: PlaybackException) {
                if (!PlayerVideoDecoder.owns(exoPlayer)) return
                failed = true
                PlayerVideoDecoder.release(exoPlayer)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            PlayerVideoDecoder.release(exoPlayer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(exoPlayer, shouldPlay) {
        if (!shouldPlay) {
            PlayerVideoDecoder.release(exoPlayer)
            return@LaunchedEffect
        }
        if (failed) return@LaunchedEffect
        delay(PlayerVideoBuffers.SETTLE_MS.toLong())
        val file = withContext(Dispatchers.IO) {
            homeVideoFile(path, context.filesDir)
        } ?: return@LaunchedEffect
        val mediaItem = MediaItem.fromUri(Uri.fromFile(file))

        fun prepareIfAvailable() {
            if (failed || !isVisible || !inForeground || !PlayerVideoDecoder.available.value) return
            val claimed = PlayerVideoDecoder.claim(exoPlayer, PlayerVideoDecoder.PRIORITY_BACKGROUND) {
                isReady = false
            }
            if (!claimed) return

            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        PlayerVideoDecoder.available.collect { available ->
            if (available) prepareIfAvailable()
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isReady && shouldPlay) 1f else 0f,
        animationSpec = tween(600),
        label = "homeVideoAlpha",
    )
    key(exoPlayer) {
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    isEnabled = false
                    isClickable = false
                    isFocusable = false
                    exoPlayer.setVideoTextureView(this)
                }
            },
            modifier = Modifier
                .matchParentSize()
                .onVisibilityChanged(minFractionVisible = 0f) { isVisible = it }
                .graphicsLayer {
                    this.alpha = alpha
                    val overscan = if (blur > 0f) 1.12f else 1f
                    scaleX = overscan
                    scaleY = overscan
                }
                .blur(blur.dp),
        )
    }
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = dim)),
    )
    if (withGradient) {
        val primary = AppTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.55f to Color.Transparent,
                        1f to primary.copy(alpha = 0.55f),
                    ),
                ),
        )
    }
}

private fun homeVideoFile(path: String, filesDir: File): File? = try {
    File(path).canonicalFile.takeIf { file ->
        file.parentFile == filesDir.canonicalFile && file.isFile
    }
} catch (_: IOException) {
    null
}
