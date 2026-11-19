/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI adapted from an open-source (GPL-3.0) base.

package com.convx.music.ui.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.convx.music.LocalDownloadUtil
import com.convx.music.playback.secureCanvasUrl
import com.convx.music.ui.player.video.PlayerVideoBuffers
import com.convx.music.ui.player.video.PlayerVideoDecoder
import com.convx.music.utils.toJavaProxy
import com.music.innertube.YouTube
import com.music.innertube.models.YouTubeClient
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.io.IOException
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
    // Matches the "$mediaId#canvas" key DownloadUtil.kt pre-caches the canvas
    // video under. Without it, this player has no way to hit that cache entry
    // even when one exists — it always re-fetches over the network, which
    // fails outright offline and can flicker/drop the overlay on a hiccup.
    mediaId: String? = null,
    onExhausted: () -> Unit = {},
    onReady: () -> Unit = {},
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    onVideoAspectRatio: (Float) -> Unit = {},
) {
    val context = LocalContext.current
    val downloadCache = LocalDownloadUtil.current.get().downloadCache
    val primary = secureCanvasUrl(primaryUrl)
    val fallback = secureCanvasUrl(fallbackUrl)?.takeIf { it != primary }
    val initial = primary ?: fallback ?: return

    // Stop the decorative video when the app is backgrounded to save battery and heat.
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
    var currentUrl by remember(initial, mediaId) { mutableStateOf(initial) }
    var isVideoReady by remember(initial, mediaId) { mutableStateOf(false) }
    var exhausted by remember(initial, fallback, mediaId) { mutableStateOf(false) }
    val currentOnExhausted by rememberUpdatedState(onExhausted)
    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnVideoAspectRatio by rememberUpdatedState(onVideoAspectRatio)

    val mediaSourceFactory = rememberCanvasMediaSourceFactory()
    val exoPlayer = remember(context, mediaSourceFactory, initial, mediaId) {
        PlayerVideoDecoder.build(context, mediaSourceFactory, PlayerVideoDecoder.Use.LOOP)
    }

    DisposableEffect(exoPlayer, primary, fallback) {
        val listener =
            object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    if (!PlayerVideoDecoder.owns(exoPlayer)) return
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
                        exhausted = true
                        PlayerVideoDecoder.release(exoPlayer)
                        currentOnExhausted()
                    }
                }

                override fun onRenderedFirstFrame() {
                    if (!PlayerVideoDecoder.owns(exoPlayer)) return
                    isVideoReady = true
                    currentOnReady()
                    exoPlayer.videoSize.toAspectRatio().takeIf { it > 0f }?.let(currentOnVideoAspectRatio)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    if (!PlayerVideoDecoder.owns(exoPlayer)) return
                    videoSize.toAspectRatio().takeIf { it > 0f }?.let(currentOnVideoAspectRatio)
                }
            }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    LaunchedEffect(currentUrl, primary, fallback, exoPlayer, shouldPlay) {
        if (!shouldPlay) return@LaunchedEffect
        if (!PlayerVideoDecoder.owns(exoPlayer)) delay(PlayerVideoBuffers.SETTLE_MS.toLong())
        val mediaItem = MediaItem.Builder()
            .setUri(currentUrl)
            .setMimeType(canvasMimeType(currentUrl, primary, fallback))
            .apply {
                // DownloadUtil uses this key for the offline copy.
                if (!mediaId.isNullOrBlank()) setCustomCacheKey(canvasReadCacheKey(downloadCache, mediaId, currentUrl))
            }
            .build()

        fun prepare() {
            if (exhausted) return
            val claimed = PlayerVideoDecoder.claim(exoPlayer, PlayerVideoDecoder.PRIORITY_LOOP) {
                isVideoReady = false
            }
            if (!claimed) return
            exoPlayer.stop()
            isVideoReady = false
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        prepare()
        PlayerVideoDecoder.available.collect { available ->
            if (available) prepare()
        }
    }

    DisposableEffect(exoPlayer, shouldPlay) {
        if (!shouldPlay) PlayerVideoDecoder.release(exoPlayer)
        onDispose {}
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            PlayerVideoDecoder.release(exoPlayer)
            exoPlayer.release()
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVideoReady) 1f else 0f,
        // Use a longer fade to blend the loop into the still.
        animationSpec = tween(durationMillis = 650, easing = SeamlessEasing),
        label = "canvasAlpha"
    )

    key(exoPlayer) {
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
                if (view.resizeMode != resizeMode) view.resizeMode = resizeMode
            },
            modifier = modifier.graphicsLayer { this.alpha = alpha },
        )
    }
}

internal fun VideoSize.toAspectRatio(): Float {
    if (width <= 0 || height <= 0) return 0f
    val effectiveWidth = width * pixelWidthHeightRatio
    return if (effectiveWidth <= 0f) 0f else effectiveWidth / height
}


private val youtubeMediaDomains = listOf(
    "googlevideo.com",
    "googleusercontent.com",
    "youtube.com",
    "youtube-nocookie.com",
    "ytimg.com",
)

/** Canvas loops are served by several CDNs; YouTube-hosted ones need the client UA. */
@Composable
internal fun rememberCanvasMediaSourceFactory(): DefaultMediaSourceFactory {
    val downloadUtil = LocalDownloadUtil.current.get()
    val okHttpClient =
        remember {
            OkHttpClient
                .Builder()
                .proxy(YouTube.proxy.toJavaProxy())
                .followSslRedirects(false)
                .addInterceptor { chain ->
                    val request = chain.request()
                    if (!request.url.isHttps) throw IOException("Canvas transport requires HTTPS")
                    chain.proceed(request)
                }
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                    if (!request.url.isHttps || request.url.username.isNotEmpty() || request.url.password.isNotEmpty()) {
                        throw IOException("Canvas transport requires an HTTPS URL without credentials")
                    }
                    val host = request.url.host
                    if (youtubeMediaDomains.none { domain -> host == domain || host.endsWith(".$domain") }) {
                        return@addNetworkInterceptor chain.proceed(
                            request.newBuilder()
                                .removeHeader("User-Agent")
                                .removeHeader("Origin")
                                .removeHeader("Referer")
                                .build(),
                        )
                    }

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

                    val builder = request.newBuilder()
                        .header("User-Agent", userAgent)
                        .removeHeader("Origin")
                        .removeHeader("Referer")
                    if (isWeb) {
                        builder.header("Origin", YouTubeClient.ORIGIN_YOUTUBE_MUSIC)
                        builder.header("Referer", YouTubeClient.REFERER_YOUTUBE_MUSIC)
                    }

                    chain.proceed(builder.build())
                }
                .build()
        }
    return remember(okHttpClient, downloadUtil) {
            DefaultMediaSourceFactory(
                CacheDataSource.Factory()
                    .setCache(downloadUtil.downloadCache)
                    .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(okHttpClient))
                    .setCacheWriteDataSinkFactory(null)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
            )
        }
}

/** HLS vs MP4 for a canvas URL; unknown extensions on the primary default to HLS (Apple). */
internal fun canvasMimeType(normalized: String, primary: String?, fallback: String?): String =
    when {
        normalized.contains(".m3u8", ignoreCase = true) ||
            normalized.lowercase(Locale.ROOT).split('?').first().endsWith(".m3u8") -> MimeTypes.APPLICATION_M3U8
        normalized.lowercase(Locale.ROOT).contains(".mp4") -> MimeTypes.VIDEO_MP4
        primary != null && normalized == primary.trim() ->
            if (normalized.contains("apple.com") || normalized.contains("music.apple") || !normalized.contains(".mp4")) {
                MimeTypes.APPLICATION_M3U8
            } else {
                MimeTypes.VIDEO_MP4
            }
        fallback != null && normalized == fallback.trim() -> MimeTypes.VIDEO_MP4
        else -> MimeTypes.APPLICATION_M3U8
    }
