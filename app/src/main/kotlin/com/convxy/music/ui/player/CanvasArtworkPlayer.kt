/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI ported from vivizzz007/vivi-music (https://github.com/vivizzz007/vivi-music), GPL-3.0.

package com.convxy.music.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.AspectRatioFrameLayout
import com.convxy.music.LocalDownloadUtil
import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import okhttp3.OkHttpClient
import java.util.Locale
import android.view.ViewGroup
import android.view.TextureView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT

@Composable
fun CanvasArtworkPlayer(
    primaryUrl: String?,
    fallbackUrl: String?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    // Matches the "$mediaId#canvas" key DownloadUtil.kt pre-caches the canvas
    // video under. Without it, this player has no way to hit that cache entry
    // even when one exists — it always re-fetches over the network, which
    // fails outright offline and can flicker/drop the overlay on a hiccup.
    mediaId: String? = null,
    onExhausted: () -> Unit = {},
    onReady: () -> Unit = {},
    // Reported once the decoder knows the frame geometry, as width / height (0 when it is
    // still unknown). Ambient Mode's Canvas Position & Fit needs it to size the side panel
    // to the canvas instead of guessing, and no other caller cares about it.
    onVideoAspectRatio: (Float) -> Unit = {},
) {
    val context = LocalContext.current
    val downloadUtil = LocalDownloadUtil.current
    val primary = primaryUrl?.takeIf { it.isNotBlank() }
    val fallback = fallbackUrl?.takeIf { it.isNotBlank() }
    val initial = primary ?: fallback ?: return

    // A canvas loop is decorative, so it must never outlive the user looking at it.
    // Gated here rather than at each call site: this composable stays composed while
    // the app is in the background, and AlbumScreen passes isPlaying = true
    // unconditionally, so a hardware video decoder kept running at the forced highest
    // bitrate with the screen off — battery and heat, not just frames.
    val lifecycleOwner = LocalLifecycleOwner.current
    var inForeground by remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> inForeground = true
                Lifecycle.Event.ON_STOP -> inForeground = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val shouldPlay = isPlaying && inForeground
    var currentUrl by remember(initial) { mutableStateOf(initial) }
    var isVideoReady by remember(initial) { mutableStateOf(false) }
    val currentOnExhausted by rememberUpdatedState(onExhausted)
    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnVideoAspectRatio by rememberUpdatedState(onVideoAspectRatio)

    val okHttpClient =
        remember {
            OkHttpClient
                .Builder()
                .proxy(YouTube.proxy)
                .addInterceptor { chain ->
                    val request = chain.request()
                    val host = request.url.host
                    val isYouTubeMediaHost =
                        host.endsWith("googlevideo.com") ||
                            host.endsWith("googleusercontent.com") ||
                            host.endsWith("youtube.com") ||
                            host.endsWith("youtube-nocookie.com") ||
                            host.endsWith("ytimg.com")

                    if (!isYouTubeMediaHost) return@addInterceptor chain.proceed(request)

                    val clientParam = request.url.queryParameter("c")?.trim().orEmpty()
                    val isWeb =
                        clientParam.startsWith("WEB", ignoreCase = true) ||
                            clientParam.startsWith("WEB_REMIX", ignoreCase = true) ||
                            request.url.toString().contains("c=WEB", ignoreCase = true)

                    val userAgent =
                        when {
                            clientParam.startsWith("WEB", ignoreCase = true) ||
                                clientParam.startsWith("WEB_REMIX", ignoreCase = true) -> YouTubeClient.USER_AGENT_WEB

                            clientParam.startsWith("IOS", ignoreCase = true) -> YouTubeClient.IOS.userAgent

                            clientParam.startsWith("ANDROID_VR", ignoreCase = true) -> YouTubeClient.ANDROID_VR_NO_AUTH.userAgent

                            clientParam.startsWith("ANDROID", ignoreCase = true) -> YouTubeClient.MOBILE.userAgent

                            else -> YouTubeClient.USER_AGENT_WEB
                        }

                    val builder = request.newBuilder().header("User-Agent", userAgent)
                    if (isWeb) {
                        builder.header("Origin", YouTubeClient.ORIGIN_YOUTUBE_MUSIC)
                        builder.header("Referer", YouTubeClient.REFERER_YOUTUBE_MUSIC)
                    }

                    chain.proceed(builder.build())
                }
                .build()
        }
    val mediaSourceFactory =
        remember(okHttpClient, downloadUtil) {
            DefaultMediaSourceFactory(
                CacheDataSource.Factory()
                    .setCache(downloadUtil.downloadCache)
                    .setUpstreamDataSourceFactory(
                        DefaultDataSource.Factory(
                            context,
                            OkHttpDataSource.Factory(okHttpClient),
                        ),
                    )
                    .setCacheWriteDataSinkFactory(null)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
            )
        }
    val exoPlayer =
        remember(initial) {
            ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()
                .apply {
                trackSelectionParameters = trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoFrameRate(30)
                    .setMaxVideoBitrate(2_500_000)
                    .build()
                setAudioAttributes(
                    AudioAttributes
                        .Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    false,
                )
                volume = 0f
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false
            }
        }

    LaunchedEffect(shouldPlay) {
        if (exoPlayer.playWhenReady != shouldPlay) {
            exoPlayer.playWhenReady = shouldPlay
        }
    }

    DisposableEffect(exoPlayer, primary, fallback) {
        val listener =
            object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    val next =
                        when (currentUrl) {
                            primary -> fallback
                            else -> null
                        }
                    if (!next.isNullOrBlank() && next != currentUrl) {
                        currentUrl = next
                        isVideoReady = false
                    } else {
                        // Both this artwork's own URLs are dead — let the caller
                        // try a different candidate (a different provider's match)
                        // instead of just leaving the screen blank.
                        currentOnExhausted()
                    }
                }

                override fun onRenderedFirstFrame() {
                    isVideoReady = true
                    currentOnReady()
                    // Not every build reports a size change before the first frame, and a
                    // retry with a different candidate URL can change it, so read it here too.
                    val aspect = exoPlayer.videoSize.toAspectRatio()
                    if (aspect > 0f) currentOnVideoAspectRatio(aspect)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    val aspect = videoSize.toAspectRatio()
                    if (aspect > 0f) currentOnVideoAspectRatio(aspect)
                }
            }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    LaunchedEffect(currentUrl, exoPlayer) {
        val normalized = currentUrl.trim()
        val mimeType =
            when {
                normalized.contains(".m3u8", ignoreCase = true) || 
                normalized.lowercase(Locale.ROOT).split('?').first().endsWith(".m3u8") -> MimeTypes.APPLICATION_M3U8
                normalized.lowercase(Locale.ROOT).contains(".mp4") -> MimeTypes.VIDEO_MP4
                primary != null && currentUrl == primary -> {
                    // Fallback: if it's the primary URL and we can't tell from extension,
                    // check if its a known HLS provider or default to HLS for Apple Music compatibility
                    if (normalized.contains("apple.com") || normalized.contains("music.apple") || !normalized.contains(".mp4")) {
                        MimeTypes.APPLICATION_M3U8
                    } else {
                        MimeTypes.VIDEO_MP4
                    }
                }
                fallback != null && currentUrl == fallback -> MimeTypes.VIDEO_MP4
                else -> MimeTypes.APPLICATION_M3U8
            }

        val mediaItem =
            MediaItem.Builder()
                .setUri(normalized)
                .setMimeType(mimeType)
                .apply {
                    // Same key DownloadUtil.kt pre-caches this canvas video
                    // under — without it CacheDataSource has no way to find
                    // an already-downloaded copy and always hits the network.
                    if (!mediaId.isNullOrBlank()) setCustomCacheKey("$mediaId#canvas")
                }
                .build()

        exoPlayer.stop()
        isVideoReady = false
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = shouldPlay
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVideoReady) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "canvasAlpha"
    )

    AndroidView(
        factory = { viewContext ->
            AspectRatioFrameLayout(viewContext).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                this.resizeMode = resizeMode

                val textureView = TextureView(viewContext).apply {
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                }
                addView(textureView)
                exoPlayer.setVideoTextureView(textureView)
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { view ->
            // AspectRatioFrameLayout handles its own relayout, but the mode is captured in
            // the factory otherwise — Ambient Mode can change it without recreating the view.
            if (view.resizeMode != resizeMode) {
                view.resizeMode = resizeMode
            }
        },
        modifier = modifier.graphicsLayer { this.alpha = alpha },
    )
}

/**
 * Width / height ratio of a decoded canvas frame, or 0 when the size is not known yet.
 *
 * Pixel aspect ratio is applied because a canvas stream can legally carry non-square pixels;
 * ignoring it would skew Ambient Mode's side panel for those few sources. Unapplied container
 * rotation is deliberately not folded in: AspectRatioFrameLayout sizes the texture from the
 * same bitmap ratio, so the panel and the rendered frame agree either way.
 */
internal fun VideoSize.toAspectRatio(): Float {
    if (width <= 0 || height <= 0) return 0f
    val effectiveWidth = width * pixelWidthHeightRatio
    return if (effectiveWidth <= 0f) 0f else effectiveWidth / height
}
