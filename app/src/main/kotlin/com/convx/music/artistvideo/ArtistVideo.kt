package com.convx.music.artistvideo

import android.content.Context
import android.view.TextureView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.convx.music.playback.secureCanvasUrl
import com.convx.music.ui.player.video.PlayerVideoBuffers
import com.convx.music.ui.player.video.PlayerVideoDecoder
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.io.IOException

/** Owns the artist hero's muted video while its surface is visible. */
@Stable
class ArtistVideoPlayer internal constructor(context: Context, videoUrl: String) {
    private val mediaItem = secureCanvasUrl(videoUrl)?.let { MediaItem.fromUri(it) }

    internal val exoPlayer: ExoPlayer = PlayerVideoDecoder.build(
        context,
        DefaultMediaSourceFactory(OkHttpDataSource.Factory(sharedClient)),
        PlayerVideoDecoder.Use.BACKGROUND,
    )

    internal var isReady by mutableStateOf(false)
        private set

    var active by mutableStateOf(true)

    private var videoFailed = false
    private var visibleViews by mutableStateOf(0)
    internal val shouldDecode: Boolean get() = active && visibleViews > 0

    init {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    if (PlayerVideoDecoder.owns(exoPlayer)) isReady = true
                }

                override fun onPlayerError(error: PlaybackException) {
                    if (!PlayerVideoDecoder.owns(exoPlayer)) return
                    videoFailed = true
                    PlayerVideoDecoder.release(exoPlayer)
                }

                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    if (!PlayerVideoDecoder.owns(exoPlayer)) return
                    if (videoSize.width <= 0 || videoSize.height <= 0) return

                    val portrait = videoSize.height > videoSize.width
                    val width = if (portrait) 720 else 1_280
                    val height = if (portrait) 1_280 else 720
                    val parameters = exoPlayer.trackSelectionParameters
                    if (parameters.maxVideoWidth == width && parameters.maxVideoHeight == height) return
                    exoPlayer.trackSelectionParameters = parameters.buildUpon()
                        .setMaxVideoSize(width, height)
                        .build()
                }
            },
        )
    }

    internal fun visibilityChanged(visible: Boolean) {
        visibleViews += if (visible) 1 else -1
        if (visibleViews == 0) PlayerVideoDecoder.release(exoPlayer)
    }

    internal fun prepareIfAvailable() {
        val item = mediaItem ?: return
        if (!shouldDecode || videoFailed || PlayerVideoDecoder.owns(exoPlayer)) return
        val claimed = PlayerVideoDecoder.claim(exoPlayer, PlayerVideoDecoder.PRIORITY_BACKGROUND) {
            isReady = false
        }
        if (!claimed) return

        exoPlayer.setMediaItem(item)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    internal fun stop() {
        PlayerVideoDecoder.release(exoPlayer)
    }

    internal fun release() {
        stop()
        exoPlayer.release()
    }

    private companion object {
        val sharedClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .followSslRedirects(false)
                .addInterceptor { chain ->
                    val request = chain.request()
                    if (!request.url.isHttps) throw IOException("Artist video transport requires HTTPS")
                    chain.proceed(request)
                }
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                    if (!request.url.isHttps || request.url.username.isNotEmpty() || request.url.password.isNotEmpty()) {
                        throw IOException("Artist video transport requires an HTTPS URL without credentials")
                    }
                    chain.proceed(request)
                }
                .build()
        }
    }
}

@Composable
fun rememberArtistVideoPlayer(videoUrl: String): ArtistVideoPlayer {
    val context = LocalContext.current.applicationContext
    val player = remember(context, videoUrl) { ArtistVideoPlayer(context, videoUrl) }
    val shouldDecode = player.shouldDecode
    LaunchedEffect(player, shouldDecode) {
        if (!shouldDecode) return@LaunchedEffect

        delay(PlayerVideoBuffers.SETTLE_MS.toLong())
        player.prepareIfAvailable()
        PlayerVideoDecoder.available.collect { available ->
            if (available) player.prepareIfAvailable()
        }
    }
    DisposableEffect(player, shouldDecode) {
        if (!shouldDecode) player.stop()
        onDispose {}
    }
    DisposableEffect(player) { onDispose { player.release() } }
    return player
}

@Composable
fun ArtistVideo(
    videoUrl: String,
    modifier: Modifier = Modifier,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
    onClick: () -> Unit,
) = ArtistVideo(rememberArtistVideoPlayer(videoUrl), modifier, resizeMode, onClick = onClick)

@Composable
fun ArtistVideo(
    player: ArtistVideoPlayer,
    modifier: Modifier = Modifier,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
    onFrame: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(player, lifecycle) {
        var visible = false
        fun updateVisibility() {
            val started = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            if (visible == started) return
            visible = started
            player.visibilityChanged(started)
        }
        val observer = LifecycleEventObserver { _, _ -> updateVisibility() }
        lifecycle.addObserver(observer)
        updateVisibility()
        onDispose {
            lifecycle.removeObserver(observer)
            if (visible) player.visibilityChanged(false)
        }
    }

    val videoAlpha by animateFloatAsState(
        targetValue = if (player.isReady) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "videoAlpha"
    )

    // Match the still image's edges so the hero blur does not show a seam.
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { onClick() },
    ) {
        key(player) {
            AndroidView(
                factory = { viewContext ->
                    AspectRatioFrameLayout(viewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        this.resizeMode = resizeMode

                        val textureView = VideoFrameTextureView(viewContext).apply {
                            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            this.onFrame = onFrame
                        }
                        addView(textureView)
                        player.exoPlayer.setVideoTextureView(textureView)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                },
                update = { view ->
                    (view.getChildAt(0) as? VideoFrameTextureView)?.onFrame = onFrame
                    if (view.resizeMode != resizeMode) {
                        view.resizeMode = resizeMode
                    }
                },
                onRelease = { view ->
                    (view.getChildAt(0) as? TextureView)?.let(player.exoPlayer::clearVideoTextureView)
                },
                modifier = Modifier
                    .matchParentSize()
                    .alpha(videoAlpha),
            )
        }
    }
}
