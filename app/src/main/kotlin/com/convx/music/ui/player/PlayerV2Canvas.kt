/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI adapted from an open-source (GPL-3.0) base.

package com.convx.music.ui.player

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
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.AspectRatioFrameLayout
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.LocalDatabase
import com.convx.music.constants.CanvasBackdropMode
import com.convx.music.constants.CanvasBackdropModeKey
import com.convx.music.constants.CanvasMode
import com.convx.music.constants.CanvasModeKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.ReduceDynamicBlurKey
import com.convx.music.db.entities.CanvasDecisionEntity
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CanvasDecision
import com.convx.music.playback.CanvasSession
import com.convx.music.playback.CanvasTrack
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.secureCanvasUrl
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.ui.player.video.PlayerVideoDecoder
import com.convx.music.ui.player.video.PlayerVideoBuffers
import com.convx.music.ui.player.video.letterboxCropZoom
import com.convx.music.ui.player.backdrop.PlayerBackdrop
import com.convx.music.ui.player.backdrop.PlayerBackdropTier
import com.convx.music.ui.player.backdrop.rememberPlayerBackdropTier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext

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
    internal val epoch: Long? = null,
)

/**
 * Per-track video looks. Colours are extracted ONCE per video from [SAMPLE_COUNT] tiny
 * readbacks spread over its first seconds -- never per frame -- and cached, so a replay or
 * a revisit is instant and the background eases between two fixed palettes.
 */
internal object CanvasVideoLooks {
    private val latest = mutableStateOf<CanvasVideoLook?>(null)
    private val extendedTrack = mutableStateOf<CanvasTrack?>(null)

    fun forMedia(track: CanvasTrack, mediaId: String?): CanvasVideoLook? =
        latest.value?.takeIf { it.mediaId == mediaId && mediaId == track.mediaId && it.epoch == track.epoch }

    fun hasExtendedBackdrop(track: CanvasTrack): Boolean = extendedTrack.value == track

    fun setExtendedBackdrop(track: CanvasTrack?, active: Boolean) {
        if (active) extendedTrack.value = track
        else if (extendedTrack.value == track) extendedTrack.value = null
    }

    fun publish(look: CanvasVideoLook) {
        latest.value = look
    }

    fun clear(track: CanvasTrack?) {
        setExtendedBackdrop(track, false)
        val look = latest.value ?: return
        if (look.mediaId == track?.mediaId && look.epoch == track?.epoch) latest.value = null
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
internal suspend fun sampleCanvasVideoLook(
    track: CanvasTrack,
    isCurrent: () -> Boolean,
    texture: () -> TextureView?,
) {
    val mediaId = track.mediaId ?: return
    if (CanvasVideoLooks.forMedia(track, mediaId) != null) return
    val frames = ArrayList<Bitmap>(SAMPLE_COUNT)
    delay(SAMPLE_START_MS)
    for (i in 0 until SAMPLE_COUNT) {
        if (i > 0) delay(SAMPLE_GAP_MS)
        if (!isCurrent()) return
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
    if (isCurrent()) CanvasVideoLooks.publish(CanvasVideoLook(mediaId, stitched, frames[frames.size / 2], epoch = track.epoch))
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
    internal val trackState: StateFlow<CanvasTrack>,
    private val downloadCache: androidx.media3.datasource.cache.Cache,
) {
    private fun currentTrack() = trackState.value
    /** Loop-level dissolve: out on a track change, in on the first frame. */
    internal val fade = Animatable(0f)
    internal var videoAspect by mutableFloatStateOf(1f)
        private set
    /** The texture that has received a frame since it was bound; surfaces dissolve on it. */
    internal var surfaceWithFrame by mutableStateOf<TextureView?>(null)
        private set

    internal var session by mutableStateOf(CanvasSession(currentTrack()))
        private set
    internal var decision by mutableStateOf<CanvasDecision?>(null)
        private set
    internal var loadGeneration by mutableStateOf(0L)
        private set
    internal val backdrop = PlayerBackdrop()
    internal var canvasMode by mutableStateOf(CanvasMode.OFF)
    internal var canvasOverride by mutableStateOf<Boolean?>(null)
    internal var canvasToggle by mutableStateOf<((Boolean) -> Unit)?>(null)
    internal val canvasEnabled get() = canvasMode != CanvasMode.OFF && (canvasOverride ?: (canvasMode == CanvasMode.AUTO))
    internal var hasRenderableMedia by mutableStateOf(false)
        private set
    internal var letterboxZoom by mutableFloatStateOf(1f)
        private set
    private var letterboxSample: Bitmap? = null
    private var letterboxPixels: IntArray? = null
    private var letterboxCandidate = 1f
    private var letterboxHits = 0
    private var lastLetterboxMs = 0L
    private var loadingTrack: CanvasTrack? = null
    private val loadingMediaId get() = loadingTrack?.mediaId
    private var loadingUrl: String? = null
    private var primary: String? = null
    private var fallback: String? = null
    private var loadingVideoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null
    private var frameTrack: CanvasTrack? = null
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
        if (frameTrack != currentTrack() || boundTexture !== view) return
        if (surfaceWithFrame !== view) surfaceWithFrame = view
        val official = loadingVideoStream != null
        val now = android.os.SystemClock.uptimeMillis()
        backdrop.onFrame(view, decision?.source, official, now)
        if (backdrop.tier != PlayerBackdropTier.C) CanvasVideoLooks.setExtendedBackdrop(frameTrack, true)
        if (official && letterboxHits < 3 && now - lastLetterboxMs >= 250L) sampleLetterbox(view, now)
    }

    private fun sampleLetterbox(view: TextureView, now: Long) {
        lastLetterboxMs = now
        val bitmap = letterboxSample ?: Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).also { letterboxSample = it }
        val pixels = letterboxPixels ?: IntArray(32 * 32).also { letterboxPixels = it }
        if (view.getBitmap(bitmap) == null) return
        bitmap.getPixels(pixels, 0, 32, 0, 0, 32, 32)
        val zoom = letterboxCropZoom(pixels, 32, 32)
        letterboxHits = if (kotlin.math.abs(zoom - letterboxCandidate) < 0.035f) letterboxHits + 1 else 1
        letterboxCandidate = zoom
        if (letterboxHits == 3) letterboxZoom = zoom
    }

    private fun bind(view: TextureView?) {
        if (boundTexture === view) return
        boundTexture = view
        surfaceWithFrame = null
        if (view == null) exoPlayer.clearVideoSurface() else exoPlayer.setVideoTextureView(view)
    }

    internal fun onFirstFrame(generation: Long) {
        if (generation != loadGeneration) return
        val track = loadingTrack ?: return
        if (track != currentTrack()) return
        frameTrack = track
        surfaceWithFrame = boundTexture
        session = session.firstFrame(track)
        if (loadingVideoStream != null) {
            videoReady = true
            videoFailed = false
            videoLoading = false
        }
        fadeInJob?.cancel()
        fadeInJob = scope.launch { fade.animateTo(1f, tween(VIDEO_FADE_IN_MS, easing = SeamlessEasing)) }
        liveSampleJob?.cancel()
        val isCurrent = { loadingTrack == track && currentTrack() == track && frameTrack == track }
        liveSampleJob = scope.launch { sampleCanvasVideoLook(track, isCurrent) { boundTexture } }
    }

    internal fun onVideoSize(size: VideoSize) {
        size.toAspectRatio().takeIf { it > 0f }?.let { videoAspect = it }
    }

    internal fun onPlaybackStateChanged(state: Int) {
        videoLoading = loadingVideoStream != null && videoReady && state == Player.STATE_BUFFERING
    }

    internal fun onError() {
        if (loadingTrack != currentTrack()) return
        if (loadingVideoStream != null) {
            clearFrame()
            videoFailed = true
            return
        }
        val next = if (loadingUrl == primary) fallback else null
        val id = loadingMediaId
        if (id != null && !next.isNullOrBlank() && next != loadingUrl) {
            clearFrame()
            loadingUrl = next
            hasRenderableMedia = true
            exoPlayer.setMediaItem(mediaItem(id, next))
            exoPlayer.prepare()
        } else {
            clearFrame()
            decision = null
            session = CanvasSession(currentTrack())
            loadingUrl = null
            PlayerVideoDecoder.release(exoPlayer)
        }
    }

    internal suspend fun reset(track: CanvasTrack, resolving: Boolean, enabled: Boolean) {
        if (loadingTrack != track || loadingUrl != null) {
            clearFrame()
            loadingTrack = track
            loadingVideoStream = null
            loadingUrl = null
            PlayerVideoDecoder.release(exoPlayer)
            exoPlayer.clearMediaItems()
            fade.snapTo(0f)
        }
        decision = null
        session = CanvasSession(track)
        if (resolving || !enabled) session = session.resolve(track, enabled)
    }

    internal fun found(track: CanvasTrack, result: CanvasDecision?) {
        if (currentTrack() != track) return
        session = session.resolved(track, result != null)
        if (session.phase == com.convx.music.playback.CanvasPhase.Found) decision = result
    }

    internal suspend fun show(track: CanvasTrack, decision: CanvasDecision) {
        val url = decision.primaryUrl ?: decision.fallbackUrl ?: return
        if (track != currentTrack() || track != loadingTrack || url == loadingUrl) return
        val mediaId = track.mediaId ?: return
        if (!claim(PlayerVideoDecoder.PRIORITY_LOOP)) {
            PlayerVideoDecoder.available.first { it }
            if (track != currentTrack() || track != loadingTrack || !claim(PlayerVideoDecoder.PRIORITY_LOOP)) return
        }
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
            .setMaxVideoFrameRate(30).setMaxVideoBitrate(2_500_000).build()
        exoPlayer.setPlaybackSpeed(1f)
        primary = decision.primaryUrl
        fallback = decision.fallbackUrl
        loadingUrl = url
        hasRenderableMedia = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.setMediaItem(mediaItem(mediaId, url))
        exoPlayer.prepare()
    }

    internal fun loadVideo(
        track: CanvasTrack,
        stream: com.convx.music.utils.YTPlayerUtils.VideoStream,
        startPositionMs: Long,
    ) {
        if (track != currentTrack() || track.mediaId != stream.mediaId) return
        val url = secureCanvasUrl(stream.url) ?: return
        if (!claim(PlayerVideoDecoder.PRIORITY_OFFICIAL)) return
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
            .setMaxVideoFrameRate(Int.MAX_VALUE).setMaxVideoBitrate(Int.MAX_VALUE).build()
        loadingVideoStream = stream
        loadingTrack = track
        loadingUrl = url
        primary = url
        fallback = null
        videoLoading = false
        hasRenderableMedia = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        exoPlayer.setMediaItem(
            MediaItem.Builder()
                .setMediaId(loadGeneration.toString())
                .setUri(url)
                .setMimeType(stream.mimeType)
                .setCustomCacheKey("convx-video-picture:${stream.videoId}")
                .build(),
            startPositionMs.coerceAtLeast(0L),
        )
        exoPlayer.prepare()
    }

    private fun claim(priority: Int): Boolean = PlayerVideoDecoder.claim(exoPlayer, priority) {
        clearFrame()
        loadingUrl = null
        loadingVideoStream = null
    }

    internal fun setBackdropTier(tier: PlayerBackdropTier) {
        backdrop.tier = tier
        if (tier == PlayerBackdropTier.C && frameTrack != null) CanvasVideoLooks.setExtendedBackdrop(frameTrack, false)
    }

    internal fun close() {
        PlayerVideoDecoder.release(exoPlayer)
        clearFrame()
        exoPlayer.release()
    }

    private fun clearFrame() {
        CanvasVideoLooks.clear(frameTrack)
        backdrop.clear()
        letterboxZoom = 1f
        letterboxCandidate = 1f
        letterboxHits = 0
        lastLetterboxMs = 0L
        hasRenderableMedia = false
        loadGeneration++
        fadeInJob?.cancel()
        liveSampleJob?.cancel()
        frameTrack = null
        surfaceWithFrame = null
        videoReady = false
        videoFailed = false
        videoLoading = false
    }

    internal fun seekTo(positionMs: Long) {
        if (loadingVideoStream != null) exoPlayer.seekTo(positionMs.coerceAtLeast(0L))
    }

    internal fun synchronizeTo(audioPlayer: Player) {
        if (loadingVideoStream == null || videoFailed || loadingTrack != currentTrack()) return
        exoPlayer.playWhenReady = audioPlayer.isPlaying && audioPlayer.playbackState == Player.STATE_READY
        if (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.playbackState == Player.STATE_ENDED) {
            val drift = exoPlayer.currentPosition - audioPlayer.currentPosition
            if (com.convx.music.ui.player.video.shouldSeekVideo(drift)) {
                exoPlayer.seekTo(audioPlayer.currentPosition.coerceAtLeast(0L))
            } else {
                val speed = com.convx.music.ui.player.video.synchronizedVideoSpeed(audioPlayer.playbackParameters.speed, drift)
                if (exoPlayer.playbackParameters.speed != speed) exoPlayer.setPlaybackSpeed(speed)
            }
        }
    }

    private fun mediaItem(mediaId: String, url: String): MediaItem {
        val normalized = url.trim()
        return MediaItem.Builder()
            .setMediaId(loadGeneration.toString())
            .setUri(normalized)
            .setMimeType(canvasMimeType(normalized, primary, fallback))
            // Same key DownloadUtil pre-caches canvas loops under.
            .setCustomCacheKey(canvasReadCacheKey(downloadCache, mediaId, normalized))
            .build()
    }

    internal fun canDraw(generation: Long, track: CanvasTrack): Boolean =
        generation == loadGeneration && frameTrack == track && track == currentTrack()

    /** A still of the frame on screen, <= 720px, or null when no video is showing. */
    internal fun capturePoster(mediaId: String?): Bitmap? {
        if (mediaId == null || frameTrack?.mediaId != mediaId || frameTrack != currentTrack() || fade.value < 0.99f) return null
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
private const val POSTER_MAX_PX = 720f

/**
 * V2's single canvas decoder. [isPlaying] should already fold in "sheet not collapsed";
 * [trackFadeMillis] is the ambient transition length (the DJ crossfade while one runs).
 */
@Composable
internal fun rememberCanvasVideoController(
    connection: PlayerConnection,
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    visible: Boolean,
    videoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null,
    videoStartPositionMs: Long = 0L,
    allowCanvas: Boolean = true,
): CanvasVideoController {
    val context = LocalContext.current
    val mediaSourceFactory = rememberCanvasMediaSourceFactory()
    val scope = rememberCoroutineScope()
    val downloadCache = com.convx.music.LocalDownloadUtil.current.get().downloadCache
    val controller = remember(connection, mediaSourceFactory) {
        val player = PlayerVideoDecoder.build(context, mediaSourceFactory, PlayerVideoDecoder.Use.OFFICIAL)
        CanvasVideoController(player, scope, connection.canvasTrack, downloadCache)
    }
    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) = controller.onPlaybackStateChanged(playbackState)
        }
        val frames = object : AnalyticsListener {
            private val window = Timeline.Window()

            private fun generation(eventTime: AnalyticsListener.EventTime): Long? =
                if (eventTime.windowIndex in 0 until eventTime.timeline.windowCount) {
                    eventTime.timeline.getWindow(eventTime.windowIndex, window).mediaItem.mediaId.toLongOrNull()
                } else null

            override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, output: Any, renderTimeMs: Long) {
                generation(eventTime)?.let(controller::onFirstFrame)
            }

            override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, videoSize: VideoSize) {
                if (generation(eventTime) == controller.loadGeneration) controller.onVideoSize(videoSize)
            }

            override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) {
                if (generation(eventTime) == controller.loadGeneration) controller.onError()
            }
        }
        controller.exoPlayer.addListener(listener)
        controller.exoPlayer.addAnalyticsListener(frames)
        onDispose {
            controller.exoPlayer.removeListener(listener)
            controller.exoPlayer.removeAnalyticsListener(frames)
            controller.close()
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
    val fullscreen = LocalFullscreenVideo.current?.session?.videoController === controller
    val active = visible || fullscreen
    DisposableEffect(controller, active) {
        if (active) CanvasPosterSource.controller = controller
        onDispose { if (CanvasPosterSource.controller === controller) CanvasPosterSource.controller = null }
    }
    val shouldPlay = isPlaying && inForeground && active
    val (backdropMode) = rememberEnumPreference(CanvasBackdropModeKey, CanvasBackdropMode.EXTENDED_VIDEO)
    val reduceMotion by rememberPreference(ReduceDynamicBlurKey, false)
    val tier = rememberPlayerBackdropTier(
        extended = backdropMode == CanvasBackdropMode.EXTENDED_VIDEO,
        visible = active && inForeground && isPlaying &&
            (controller.videoReady || controller.session.phase == com.convx.music.playback.CanvasPhase.Showing),
        reducedMotion = reduceMotion || !android.animation.ValueAnimator.areAnimatorsEnabled(),
    )
    SideEffect { controller.setBackdropTier(tier) }
    LaunchedEffect(controller, shouldPlay) { controller.exoPlayer.playWhenReady = shouldPlay }

    val legacyEnabled by rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    val dataSaver by rememberPreference(DataSaverEnabledKey, defaultValue = false)
    val (mode) = rememberEnumPreference(CanvasModeKey, if (legacyEnabled) CanvasMode.AUTO else CanvasMode.OFF)
    val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
    val track by connection.canvasTrack.collectAsStateWithLifecycle()
    val database = LocalDatabase.current.get()
    val mediaId = track.mediaId
    val choices = remember(database, mediaId) {
        if (mediaId == null) kotlinx.coroutines.flow.flowOf(null)
        else database.canvasDecisionDao.observe(mediaId).map { mediaId to it?.enabled }
    }
    val choice by choices.collectAsStateWithLifecycle(initialValue = null)
    val canChange = visible && inForeground && allowCanvas && mode != CanvasMode.OFF && !dataSaver &&
        mediaId != null && choice?.first == mediaId
    val metadata = mediaMetadata?.takeIf { it.id == mediaId }
    val stream = videoStream?.takeIf { it.mediaId == mediaId }
    val trackChoice = choice?.takeIf { it.first == mediaId }?.second
    val canResolve = canChange && trackChoice != false
    val permitted = trackChoice ?: (mode == CanvasMode.AUTO)
    val toggle = remember(canChange, track, database, controller) {
        if (canChange && mediaId != null) {
            { enabled: Boolean ->
                scope.launch {
                    if (connection.canvasTrack.value == track) database.canvasDecisionDao.upsert(CanvasDecisionEntity(mediaId, enabled))
                }
                Unit
            }
        } else null
    }
    SideEffect {
        controller.canvasMode = if (canChange) mode else CanvasMode.OFF
        controller.canvasOverride = trackChoice
        controller.canvasToggle = toggle
    }
    LaunchedEffect(controller, track, metadata, canvasSource, canResolve, stream, permitted, active, inForeground) {
        controller.reset(track, canResolve && metadata != null, enabled = canResolve)
        if (active && inForeground && stream != null) {
            delay(PlayerVideoBuffers.SETTLE_MS.toLong())
            controller.loadVideo(track, stream, connection.player.currentPosition.takeIf { it >= 0L } ?: videoStartPositionMs)
        } else if (canResolve && metadata != null) {
            val decision = connection.canvasResolver.resolve(metadata, canvasSource)
            controller.found(track, decision)
            if (decision != null && permitted) {
                delay(PlayerVideoBuffers.SETTLE_MS.toLong())
                controller.show(track, decision)
            }
        }
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
    if (controller == null || !controller.hasRenderableMedia) return
    val generation = controller.loadGeneration
    key(controller, generation) {
        BoundCanvasVideoSurface(controller, generation, modifier, crop, onFrame)
    }
}

@Composable
private fun BoundCanvasVideoSurface(
    controller: CanvasVideoController,
    generation: Long,
    modifier: Modifier,
    crop: Boolean,
    onFrame: (() -> Unit)?,
) {
    val token = remember { Any() }
    var texture by remember { mutableStateOf<TextureView?>(null) }
    val surfaceAlpha = remember { Animatable(0f) }
    val drawTrack = controller.trackState.collectAsStateWithLifecycle()
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
            if (!crop) {
                scaleX = controller.letterboxZoom
                scaleY = controller.letterboxZoom
            }
            alpha = if (controller.canDraw(generation, drawTrack.value)) controller.fade.value * surfaceAlpha.value else 0f
        },
    )
}

private const val SURFACE_FADE_IN_MS = 420
