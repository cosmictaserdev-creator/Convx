/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI adapted from an open-source (GPL-3.0) base.

package com.convx.music.ui.player

import android.content.Context
import android.graphics.Bitmap
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.view.Gravity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.models.MediaMetadata
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.utils.SpotifyCanvasLookup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.util.Locale

/*
 * One muted canvas player and decoder are shared by the cover and lyrics/queue surfaces.
 * The latest attached surface receives video; each surface fades in on its first frame.
 * Track changes fade out the current loop before loading and fading in the next one.
 */

/** A canvas video's colour identity: a few small frames sampled once, stitched. */
@Immutable
class CanvasVideoLook(
    val mediaId: String,
    /** 2x2 grid of sampled frames -- palette extraction reads the whole clip's colours. */
    val stitched: Bitmap,
    /** One representative frame, for the blurred-artwork backgrounds. */
    val representative: Bitmap,
    val liveFrameVersion: Long = 0L,
)

/**
 * Per-track video looks. Colours are extracted ONCE per video from [SAMPLE_COUNT] tiny
 * readbacks spread over its first seconds -- never per frame -- and cached, so a replay or
 * a revisit is instant and the background eases between two fixed palettes.
 */
object CanvasVideoLooks {
    private val cache = android.util.LruCache<String, CanvasVideoLook>(24)
    private val latest = mutableStateOf<CanvasVideoLook?>(null)

    /** Composition-safe: re-reads when a new look lands, never per frame. */
    fun forMedia(mediaId: String?): CanvasVideoLook? {
        if (mediaId == null) return null
        val newest = latest.value
        return if (newest?.mediaId == mediaId) newest else cache.get(mediaId)
    }

    internal fun has(mediaId: String): Boolean = cache.get(mediaId) != null

    internal fun publish(look: CanvasVideoLook) {
        cache.put(look.mediaId, look)
        latest.value = look
    }

    internal fun publishLiveFrame(mediaId: String, frame: Bitmap, stitched: Bitmap) {
        val look = CanvasVideoLook(mediaId, stitched, frame, android.os.SystemClock.uptimeMillis())
        cache.put(mediaId, look)
        latest.value = look
    }
}

private const val SAMPLE_COUNT = 4
private const val SAMPLE_SIZE_PX = 96
private const val SAMPLE_START_MS = 450L
private const val SAMPLE_GAP_MS = 1_100L

/**
 * Samples [mediaId]'s look off [texture]. Main thread (TextureView.getBitmap), but only
 * four 96px readbacks across ~4s; the stitch runs on Default.
 */
internal suspend fun sampleCanvasVideoLook(mediaId: String, texture: () -> TextureView?) {
    if (CanvasVideoLooks.has(mediaId)) return
    val frames = ArrayList<Bitmap>(SAMPLE_COUNT)
    delay(SAMPLE_START_MS)
    for (i in 0 until SAMPLE_COUNT) {
        if (i > 0) delay(SAMPLE_GAP_MS)
        val view = texture()?.takeIf { it.isAvailable } ?: break
        view.getBitmap(SAMPLE_SIZE_PX, SAMPLE_SIZE_PX)?.let(frames::add)
    }
    if (frames.isEmpty()) return
    val stitched = withContext(Dispatchers.Default) {
        val out = Bitmap.createBitmap(SAMPLE_SIZE_PX * 2, SAMPLE_SIZE_PX * 2, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(out)
        // Short clips or an early stop repeat frames rather than leave black quadrants,
        // which would drag every palette toward black.
        for (slot in 0 until 4) {
            canvas.drawBitmap(
                frames[slot % frames.size],
                (slot % 2) * SAMPLE_SIZE_PX.toFloat(),
                (slot / 2) * SAMPLE_SIZE_PX.toFloat(),
                null,
            )
        }
        out
    }
    CanvasVideoLooks.publish(CanvasVideoLook(mediaId, stitched, frames[frames.size / 2]))
}

private const val LIVE_FRAME_SAMPLE_MS = 1_200L
private const val LIVE_FRAME_SAMPLE_PX = 48

internal suspend fun sampleLiveCanvasVideoLook(mediaId: String, texture: () -> TextureView?) {
    while (currentCoroutineContext().isActive) {
        delay(LIVE_FRAME_SAMPLE_MS)
        val frame = texture()?.takeIf { it.isAvailable }?.getBitmap(LIVE_FRAME_SAMPLE_PX, LIVE_FRAME_SAMPLE_PX) ?: continue
        val stitched = withContext(Dispatchers.Default) {
            Bitmap.createBitmap(LIVE_FRAME_SAMPLE_PX * 2, LIVE_FRAME_SAMPLE_PX * 2, Bitmap.Config.ARGB_8888).also { bitmap ->
                val canvas = android.graphics.Canvas(bitmap)
                for (y in 0..1) for (x in 0..1) {
                    canvas.drawBitmap(frame, x * LIVE_FRAME_SAMPLE_PX.toFloat(), y * LIVE_FRAME_SAMPLE_PX.toFloat(), null)
                }
            }
        }
        CanvasVideoLooks.publishLiveFrame(mediaId, frame, stitched)
    }
}

/**
 * The morph's hook into the live video: at flight start the overlay grabs one poster of
 * the frame on screen (paused while collapsed, so it is also the frame the open lands on)
 * and dissolves the travelling cover into it, so mini -> player lands ON the video.
 */
internal object CanvasPosterSource {
    @JvmField var controller: CanvasVideoController? = null
    // Called from composition at flight start; reads nothing into the caller's snapshot
    // scope, so the overlay does not recompose along with the video's fades.
    fun capture(mediaId: String?): Bitmap? =
        androidx.compose.runtime.snapshots.Snapshot.withoutReadObservation { controller?.capturePoster(mediaId) }
}

@Stable
class CanvasVideoController internal constructor(
    internal val exoPlayer: ExoPlayer,
    private val scope: CoroutineScope,
) {
    /** Loop-level dissolve: out on a track change, in on the first frame. */
    internal val fade = Animatable(0f)
    internal var videoAspect by mutableFloatStateOf(1f)
        private set
    /** The texture that has received a frame since it was bound; surfaces dissolve on it. */
    internal var surfaceWithFrame by mutableStateOf<TextureView?>(null)
        private set

    private var loadingMediaId: String? = null
    private var loadingUrl: String? = null
    private var primary: String? = null
    private var fallback: String? = null
    private var loadingVideoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null
    private var frameMediaId: String? = null
    private var boundTexture: TextureView? = null
    private val attached = ArrayList<Pair<Any, TextureView>>()
    private var fadeInJob: Job? = null
    private var liveSampleJob: Job? = null
    internal var videoReady by mutableStateOf(false)
        private set
    internal var videoFailed by mutableStateOf(false)
        private set
    internal var videoLoading by mutableStateOf(false)
        private set

    internal fun attach(token: Any, view: TextureView) {
        attached.removeAll { it.first === token }
        attached.add(token to view)
        bind(view)
    }

    internal fun detach(token: Any) {
        val wasTop = attached.lastOrNull()?.first === token
        attached.removeAll { it.first === token }
        if (wasTop) bind(attached.lastOrNull()?.second)
    }

    internal fun onSurfaceFrame(view: TextureView) {
        if (boundTexture === view && surfaceWithFrame !== view) surfaceWithFrame = view
    }

    private fun bind(view: TextureView?) {
        if (boundTexture === view) return
        boundTexture = view
        surfaceWithFrame = null
        if (view == null) exoPlayer.clearVideoSurface() else exoPlayer.setVideoTextureView(view)
    }

    internal fun onFirstFrame() {
        frameMediaId = loadingMediaId
        surfaceWithFrame = boundTexture
        if (loadingVideoStream != null) {
            videoReady = true
            videoFailed = false
            videoLoading = false
        }
        fadeInJob?.cancel()
        fadeInJob = scope.launch { fade.animateTo(1f, tween(VIDEO_FADE_IN_MS, easing = SeamlessEasing)) }
        val id = loadingMediaId ?: return
        if (loadingVideoStream != null) {
            if (liveSampleJob?.isActive != true) liveSampleJob = scope.launch { sampleLiveCanvasVideoLook(id) { boundTexture } }
        } else {
            scope.launch { sampleCanvasVideoLook(id) { boundTexture } }
        }
    }

    internal fun onVideoSize(size: VideoSize) {
        if (size.width > 0 && size.height > 0) videoAspect = size.width.toFloat() / size.height
    }

    internal fun onError() {
        if (loadingVideoStream != null) {
            videoReady = false
            videoFailed = true
            videoLoading = false
            return
        }
        val next = if (loadingUrl == primary) fallback else null
        val id = loadingMediaId
        if (id != null && !next.isNullOrBlank() && next != loadingUrl) {
            loadingUrl = next
            exoPlayer.setMediaItem(mediaItem(id, next))
            exoPlayer.prepare()
        }
    }

    internal suspend fun load(mediaId: String?, primary: String?, fallback: String?, fadeOutMillis: Int) {
        if (loadingVideoStream != null) {
            loadingVideoStream = null
            liveSampleJob?.cancel()
            liveSampleJob = null
        }
        val url = primary ?: fallback
        if (mediaId == loadingMediaId && url == loadingUrl) return
        fadeInJob?.cancel()
        if (fade.value > 0f) fade.animateTo(0f, tween(fadeOutMillis, easing = SeamlessEasing))
        loadingMediaId = mediaId
        loadingUrl = url
        videoReady = false
        videoFailed = false
        videoLoading = false
        this.primary = primary
        this.fallback = fallback
        frameMediaId = null
        surfaceWithFrame = null
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        if (mediaId == null || url == null) return
        exoPlayer.setMediaItem(mediaItem(mediaId, url))
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.prepare()
    }

    internal suspend fun loadVideo(
        stream: com.convx.music.utils.YTPlayerUtils.VideoStream,
        startPositionMs: Long,
        fadeOutMillis: Int,
    ) {
        if (loadingVideoStream?.videoId == stream.videoId && loadingVideoStream?.url == stream.url) return
        fadeInJob?.cancel()
        liveSampleJob?.cancel()
        liveSampleJob = null
        if (fade.value > 0f) fade.animateTo(0f, tween(fadeOutMillis, easing = SeamlessEasing))
        loadingVideoStream = stream
        loadingMediaId = stream.mediaId
        loadingUrl = stream.url
        primary = stream.url
        fallback = null
        frameMediaId = null
        surfaceWithFrame = null
        videoReady = false
        videoFailed = false
        videoLoading = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(
            MediaItem.Builder()
                .setUri(stream.url.trim())
                .setMimeType(stream.mimeType)
                .setCustomCacheKey("convx-video-picture:${stream.videoId}")
                .build(),
            startPositionMs.coerceAtLeast(0L),
        )
        exoPlayer.prepare()
    }

    internal fun seekTo(positionMs: Long) {
        if (loadingVideoStream != null) exoPlayer.seekTo(positionMs.coerceAtLeast(0L))
    }

    internal fun synchronizeTo(audioPlayer: Player) {
        if (loadingVideoStream == null || videoFailed) return
        exoPlayer.playWhenReady = audioPlayer.isPlaying
        if (exoPlayer.playbackParameters.speed != audioPlayer.playbackParameters.speed) {
            exoPlayer.setPlaybackSpeed(audioPlayer.playbackParameters.speed)
        }
        if (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.playbackState == Player.STATE_ENDED) {
            if (kotlin.math.abs(exoPlayer.currentPosition - audioPlayer.currentPosition) > 500L) {
                exoPlayer.seekTo(audioPlayer.currentPosition.coerceAtLeast(0L))
            }
        }
    }

    internal fun retryVideo() {
        val stream = loadingVideoStream ?: return
        videoReady = false
        videoFailed = false
        videoLoading = true
        exoPlayer.setMediaItem(
            MediaItem.Builder()
                .setUri(stream.url.trim())
                .setMimeType(stream.mimeType)
                .setCustomCacheKey("convx-video-picture:${stream.videoId}")
                .build(),
            exoPlayer.currentPosition.coerceAtLeast(0L),
        )
        exoPlayer.prepare()
    }

    private fun mediaItem(mediaId: String, url: String): MediaItem {
        val normalized = url.trim()
        return MediaItem.Builder()
            .setUri(normalized)
            .setMimeType(canvasMimeType(normalized, primary, fallback))
            // Same key DownloadUtil pre-caches canvas loops under.
            .setCustomCacheKey("$mediaId#canvas")
            .build()
    }

    /** A still of the frame on screen, <= 720px, or null when no video is showing. */
    internal fun capturePoster(mediaId: String?): Bitmap? {
        if (mediaId == null || frameMediaId != mediaId || fade.value < 0.99f) return null
        val view = boundTexture?.takeIf { it.isAvailable && it === surfaceWithFrame } ?: return null
        val w = view.width
        val h = view.height
        if (w <= 0 || h <= 0) return null
        val scale = minOf(1f, POSTER_MAX_PX / maxOf(w, h))
        return runCatching {
            view.getBitmap((w * scale).toInt().coerceAtLeast(1), (h * scale).toInt().coerceAtLeast(1))
        }.getOrNull()
    }
}

private const val VIDEO_FADE_IN_MS = 750
private const val VIDEO_FADE_OUT_MAX_MS = 1_400
private const val POSTER_MAX_PX = 720f

/**
 * V2's single canvas decoder. [isPlaying] should already fold in "sheet not collapsed";
 * [trackFadeMillis] is the ambient transition length (the DJ crossfade while one runs).
 */
@Composable
fun rememberCanvasVideoController(
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    trackFadeMillis: Int,
    videoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null,
    videoStartPositionMs: Long = 0L,
): CanvasVideoController {
    val context = LocalContext.current
    val mediaSourceFactory = rememberCanvasMediaSourceFactory()
    val scope = rememberCoroutineScope()
    val controller = remember {
        val player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                trackSelectionParameters = trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoFrameRate(30)
                    .setMaxVideoBitrate(2_500_000)
                    .build()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    false,
                )
                volume = 0f
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = false
            }
        CanvasVideoController(player, scope)
    }
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onRenderedFirstFrame() = controller.onFirstFrame()
            override fun onVideoSizeChanged(videoSize: VideoSize) = controller.onVideoSize(videoSize)
            override fun onPlayerError(error: PlaybackException) = controller.onError()
        }
        controller.exoPlayer.addListener(listener)
        CanvasPosterSource.controller = controller
        onDispose {
            if (CanvasPosterSource.controller === controller) CanvasPosterSource.controller = null
            controller.exoPlayer.removeListener(listener)
            controller.exoPlayer.release()
        }
    }

    // Decorative: never decode with the app in the background.
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
    LaunchedEffect(shouldPlay) { controller.exoPlayer.playWhenReady = shouldPlay }

    val enableCanvas by rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
    val mediaId = mediaMetadata?.id
    val latestMetadata by rememberUpdatedState(mediaMetadata)
    // produceState keeps its previous value while keys change, so tag each result with its track ID.
    val fetched by produceState<Pair<String?, CanvasArtwork?>?>(null, mediaId, canvasSource, enableCanvas) {
        val metadata = latestMetadata
        value = mediaId to if (!enableCanvas || metadata == null) null else fetchCanvasArtwork(context, metadata, canvasSource)
    }
    val artwork = fetched?.takeIf { it.first == mediaId }?.second
    val fadeOut = trackFadeMillis.coerceAtMost(VIDEO_FADE_OUT_MAX_MS)
    val primary = artwork?.preferredAnimationUrl?.takeIf { it.isNotBlank() }
    val fallback = artwork?.animated?.takeIf { it.isNotBlank() }
    LaunchedEffect(mediaId, primary, fallback, videoStream) {
        if (videoStream != null) controller.loadVideo(videoStream, videoStartPositionMs, fadeOut)
        else controller.load(mediaId, primary, fallback, fadeOut)
    }
    return controller
}

/**
 * A surface showing [controller]'s video. Crop-fills its bounds (the poster the morph
 * captures is drawn the same way), and dissolves in on its own first frame.
 */
@Composable
fun CanvasVideoSurface(
    controller: CanvasVideoController?,
    modifier: Modifier = Modifier,
    crop: Boolean = true,
    onFrame: (() -> Unit)? = null,
) {
    if (controller == null) return
    val token = remember { Any() }
    var texture by remember { mutableStateOf<TextureView?>(null) }
    val surfaceAlpha = remember { Animatable(0f) }
    val hasFrame = texture != null && controller.surfaceWithFrame === texture
    LaunchedEffect(hasFrame) {
        // Only ever dissolves IN: a surface that loses the output keeps its last frame
        // (TextureView retains it), and loop-level fades belong to controller.fade.
        if (hasFrame) surfaceAlpha.animateTo(1f, tween(SURFACE_FADE_IN_MS, easing = SeamlessEasing))
    }
    DisposableEffect(token) { onDispose { controller.detach(token) } }
    val aspect = controller.videoAspect
    AndroidView(
        factory = { viewContext ->
            FrameLayout(viewContext).apply {
                clipChildren = true
                clipToPadding = true
                addView(AspectRatioFrameLayout(viewContext).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    resizeMode = if (crop) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    val view = com.convx.music.artistvideo.VideoFrameTextureView(viewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    view.onFrame = {
                        controller.onSurfaceFrame(view)
                        onFrame?.invoke()
                    }
                    addView(view)
                    texture = view
                    controller.attach(token, view)
                }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER))
            }
        },
        update = {
            val frame = it.getChildAt(0) as AspectRatioFrameLayout
            frame.setAspectRatio(aspect)
            frame.resizeMode = if (crop) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
            val frameView = frame.getChildAt(0) as? com.convx.music.artistvideo.VideoFrameTextureView
            frameView?.let { view ->
                view.onFrame = {
                    controller.onSurfaceFrame(view)
                    onFrame?.invoke()
                }
            }
        },
        modifier = modifier.graphicsLayer {
            clip = true
            alpha = controller.fade.value * surfaceAlpha.value
        },
    )
}

private const val SURFACE_FADE_IN_MS = 420

private suspend fun fetchCanvasArtwork(context: Context, metadata: MediaMetadata, canvasSource: CanvasSource): CanvasArtwork? {
    val cacheKey = "${metadata.id}:${canvasSource.name}"
    CanvasArtworkPlaybackCache.get(cacheKey)?.let { return it }
    val countryCode = Locale.getDefault().country
    val storefront = if (countryCode.length == 2) countryCode.lowercase(Locale.ROOT) else "us"
    val fetched = withContext(Dispatchers.IO) {
        runCatching {
            val songTitle = metadata.title
            val artistName = metadata.artists.firstOrNull()?.name ?: ""
            val albumName = metadata.album?.title ?: ""
            when (canvasSource) {
                CanvasSource.AUTO ->
                    SpotifyCanvasLookup.getBySongArtist(context, songTitle, artistName)
                        ?: AppleMusicCanvasProvider.getBySongArtist(songTitle, artistName, albumName, storefront)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                        ?: TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                            ?.takeIf { !it.animated.isNullOrBlank() }
                        ?: ConvxCanvasProvider.getBySongArtist(songTitle, artistName)
                            ?.takeIf { !it.animated.isNullOrBlank() }
                CanvasSource.APPLE_MUSIC ->
                    AppleMusicCanvasProvider.getBySongArtist(songTitle, artistName, albumName, storefront)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                CanvasSource.CONVX ->
                    ConvxCanvasProvider.getBySongArtist(songTitle, artistName)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                CanvasSource.TIDAL ->
                    TidalCanvasProvider.getBySongArtist(songTitle, artistName, albumName)
                        ?.takeIf { !it.animated.isNullOrBlank() }
                CanvasSource.SPOTIFY ->
                    SpotifyCanvasLookup.getBySongArtist(context, songTitle, artistName)
            }
        }.getOrNull()
    }
    if (fetched != null) CanvasArtworkPlaybackCache.put(cacheKey, fetched)
    return fetched
}
