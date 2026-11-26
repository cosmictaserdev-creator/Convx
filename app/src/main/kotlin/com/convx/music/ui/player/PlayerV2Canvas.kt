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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CanvasSelectionRepository
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/*
 * The cover and lyrics/queue surfaces share a muted canvas player. A second decoder is
 * used briefly on track changes so the outgoing loop can keep playing until the next frame.
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
    var controller by mutableStateOf<CanvasVideoController?>(null)
    // Called from composition at flight start; reads nothing into the caller's snapshot
    // scope, so the overlay does not recompose along with the video's fades.
    fun capture(mediaId: String?): Bitmap? =
        androidx.compose.runtime.snapshots.Snapshot.withoutReadObservation { controller?.capturePoster(mediaId) }
}

@Stable
class CanvasVideoController internal constructor(
    player: ExoPlayer,
    private val scope: CoroutineScope,
    private val createStandby: (() -> ExoPlayer)? = null,
) {
    internal var exoPlayer: ExoPlayer = player
        private set
    /** Loop-level dissolve: out on a track change, in on the first frame. */
    internal val fade = Animatable(0f)
    internal var videoAspect by mutableFloatStateOf(1f)
        private set
    /** The texture that has received a frame since it was bound; surfaces dissolve on it. */
    internal var surfaceWithFrame by mutableStateOf<TextureView?>(null)
        private set
    /** Read in a draw phase: all surfaces invalidate the same live blur without parent recomposition. */
    internal var renderedFrame by mutableIntStateOf(0)
        private set

    internal val loopKey: String?
        get() = exoPlayer.currentMediaItem?.let { "${it.mediaId}:${it.localConfiguration?.uri}" }
    internal val loopPositionMs: Long get() = exoPlayer.currentPosition.coerceAtLeast(0L)
    internal val loopDurationMs: Long get() = exoPlayer.duration.coerceAtLeast(0L)

    private var loadingMediaId: String? = null
    private var loadingUrl: String? = null
    private var primary: String? = null
    private var fallback: String? = null
    private var loadingVideoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null
    internal var frameMediaId: String? = null
        private set
    private var boundTexture: TextureView? = null
    internal var hasSurface by mutableStateOf(false)
        private set
    private var playbackRequested = false
    private val previewHolds = mutableSetOf<Any>()
    internal fun holdForPreview(token: Any) {
        if (released) return
        previewHolds.add(token)
        updatePlayback()
    }
    internal fun releasePreviewHold(token: Any) {
        if (released) return
        previewHolds.remove(token)
        updatePlayback()
    }
    private var released = false
    private var loadRevision = 0L
    internal var blurWorkload by mutableStateOf(com.convx.music.canvas.CanvasBlurWorkload())
        private set
    private var blurSettleJob: Job? = null

    internal fun observeCanvasChange(mediaId: String?) {
        val next = blurWorkload.transition(mediaId, android.os.SystemClock.uptimeMillis())
        if (next == blurWorkload) return
        blurWorkload = next
        blurSettleJob?.cancel()
        if (next.reduced) blurSettleJob = scope.launch {
            delay(2_500)
            blurWorkload = blurWorkload.settled()
        }
    }
    private data class AttachedSurface(val first: TextureView, val second: TextureView?) {
        fun at(slot: Int): TextureView? = if (slot == 0) first else second
    }
    private val attached = com.convx.music.canvas.CanvasSurfaceOwnership<AttachedSurface>()
    private var boundSurface: AttachedSurface? = null
    private var activeSlot = 0
    private data class PendingCanvas(val player: ExoPlayer, val mediaId: String, var url: String,
        val primary: String?, val fallback: String?, val fadeMillis: Int, var aspect: Float = 1f, var started: Boolean = false,
        var timing: com.convx.music.canvas.CanvasTransitionTiming? = null)
    private var pending by mutableStateOf<PendingCanvas?>(null)
    private var transitionJob: Job? = null
    private var pendingLoadTimeout: Job? = null
    private var fadeInJob: Job? = null
    private var liveSampleJob: Job? = null
    private var lookSampleJob: Job? = null
    internal var videoReady by mutableStateOf(false)
        private set
    internal var videoFailed by mutableStateOf(false)
        private set
    internal var videoLoading by mutableStateOf(false)
        private set

    init { player.addListener(listenerFor(player)) }

    private fun listenerFor(player: ExoPlayer) = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            if (player === exoPlayer) onFirstFrame() else if (pending?.player === player) onPendingFrame()
        }
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            if (player === exoPlayer) onVideoSize(videoSize)
            else if (pending?.player === player && videoSize.width > 0 && videoSize.height > 0)
                pending?.aspect = videoSize.width.toFloat() / videoSize.height
        }
        override fun onPlayerError(error: PlaybackException) {
            if (player === exoPlayer) onError() else if (pending?.player === player) onPendingError()
        }
        override fun onIsPlayingChanged(isPlaying: Boolean) { if (player === exoPlayer) updateSampling() }
    }

    internal fun attach(token: Any, view: TextureView, priority: Int = 0, alternate: TextureView? = null) {
        if (released) return
        bind(attached.attach(token, AttachedSurface(view, alternate), priority))
    }

    internal fun detach(token: Any) {
        if (released) return
        bind(attached.detach(token))
    }

    internal fun onSurfaceFrame(view: TextureView) {
        if (boundTexture === view || (pending != null && boundSurface?.at(1 - activeSlot) === view)) renderedFrame++
        if (boundTexture === view && surfaceWithFrame !== view) surfaceWithFrame = view
        if (pending != null && boundSurface?.at(1 - activeSlot) === view) onPendingFrame()
    }

    private fun bind(surface: AttachedSurface?) {
        if (surface != null && surface.second == null && activeSlot == 1) {
            cancelPending()
            activeSlot = 0
        }
        val view = surface?.at(activeSlot) ?: surface?.first
        if (boundTexture === view && boundSurface?.second === surface?.second) return
        boundSurface?.first?.animate()?.cancel()
        boundSurface?.second?.animate()?.cancel()
        boundSurface = surface
        boundTexture = view
        hasSurface = view != null
        surfaceWithFrame = null
        if (view == null) exoPlayer.clearVideoSurface() else exoPlayer.setVideoTextureView(view)
        pending?.let { staged ->
            if (staged.started) {
                transitionJob?.cancel()
                transitionJob = null
                staged.started = false
            }
            val target = surface?.at(1 - activeSlot)
            if (target == null) cancelPending()
            else {
                target.alpha = staged.timing?.progress(android.os.SystemClock.uptimeMillis()) ?: 0f
                target.bringToFront()
                staged.player.setVideoTextureView(target)
                armPendingTimeout(staged)
            }
        }
        // The outgoing video stays opaque beneath the incoming one. Fading both
        // layers exposes the thumbnail underneath halfway through a transition.
        view?.alpha = 1f
        updatePlayback()
        updateSampling()
    }

    internal fun setPlaybackRequested(requested: Boolean) {
        if (released) return
        playbackRequested = requested
        updatePlayback()
    }

    internal fun release() {
        if (released) return
        released = true
        blurSettleJob?.cancel()
        liveSampleJob?.cancel()
        lookSampleJob?.cancel()
        fadeInJob?.cancel()
        cancelPending()
        attached.clear()
        boundTexture = null
        hasSurface = false
        surfaceWithFrame = null
        exoPlayer.release()
    }

    private fun updatePlayback() {
        val play = playbackRequested && boundTexture != null && previewHolds.isEmpty()
        exoPlayer.playWhenReady = play
        pending?.player?.playWhenReady = play
    }

    internal fun onFirstFrame() {
        if (released) return
        frameMediaId = loadingMediaId
        surfaceWithFrame = boundTexture
        if (loadingVideoStream != null) {
            videoReady = true
            videoFailed = false
            videoLoading = false
        }
        fadeInJob?.cancel()
        fadeInJob = scope.launch { fade.animateTo(1f, tween(VIDEO_FADE_IN_MS, easing = SeamlessEasing)) }
        updateSampling()
    }

    internal fun updateSampling() {
        val id = loadingMediaId
        if (!exoPlayer.isPlaying || boundTexture == null || id == null || frameMediaId != id) {
            liveSampleJob?.cancel()
            liveSampleJob = null
            lookSampleJob?.cancel()
            lookSampleJob = null
            return
        }
        if (loadingVideoStream != null) {
            if (liveSampleJob?.isActive != true) liveSampleJob = scope.launch { sampleLiveCanvasVideoLook(id) { boundTexture } }
        } else if (!CanvasVideoLooks.has(id) && lookSampleJob?.isActive != true) {
            lookSampleJob = scope.launch { sampleCanvasVideoLook(id) { boundTexture } }
        }
    }

    internal fun onVideoSize(size: VideoSize) {
        if (size.width > 0 && size.height > 0) videoAspect = size.width.toFloat() / size.height
    }

    internal fun onError() {
        if (released) return
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

    private fun cancelPending() {
        pendingLoadTimeout?.cancel()
        pendingLoadTimeout = null
        transitionJob?.cancel()
        transitionJob = null
        boundSurface?.first?.animate()?.cancel()
        boundSurface?.second?.animate()?.cancel()
        boundTexture?.alpha = 1f
        boundSurface?.at(1 - activeSlot)?.alpha = 0f
        pending?.player?.release()
        pending = null
    }

    private fun onPendingError() {
        val staged = pending ?: return
        pendingLoadTimeout?.cancel()
        pendingLoadTimeout = null
        val next = if (staged.url == staged.primary) staged.fallback else null
        if (!next.isNullOrBlank() && next != staged.url) {
            transitionJob?.cancel()
            transitionJob = null
            boundSurface?.first?.animate()?.cancel()
            boundSurface?.second?.animate()?.cancel()
            boundTexture?.alpha = 1f
            boundSurface?.at(1 - activeSlot)?.alpha = 0f
            staged.started = false
            staged.timing = null
            staged.url = next
            staged.player.setMediaItem(mediaItem(staged.mediaId, next))
            staged.player.prepare()
            armPendingTimeout(staged)
        } else {
            val failedRevision = loadRevision
            cancelPending()
            scope.launch {
                if (!released && loadRevision == failedRevision) load(staged.mediaId, null, null, staged.fadeMillis)
            }
        }
    }

    private fun armPendingTimeout(staged: PendingCanvas) {
        pendingLoadTimeout?.cancel()
        pendingLoadTimeout = scope.launch {
            // Pausing playback must not turn a deliberately paused decoder into a failure.
            var activeWait = 0
            while (pending === staged && !staged.started && activeWait < 15) {
                delay(1_000)
                if (playbackRequested && previewHolds.isEmpty()) activeWait++
            }
            if (pending === staged && !staged.started) {
                pendingLoadTimeout = null
                onPendingError()
            }
        }
    }

    private fun onPendingFrame() {
        val staged = pending ?: return
        if (staged.started) return
        val incoming = boundSurface?.at(1 - activeSlot) ?: return
        val outgoing = boundTexture ?: return
        pendingLoadTimeout?.cancel()
        pendingLoadTimeout = null
        staged.started = true
        incoming.bringToFront()
        val now = android.os.SystemClock.uptimeMillis()
        val timing = staged.timing ?: com.convx.music.canvas.CanvasTransitionTiming(now, staged.fadeMillis.coerceAtLeast(300).toLong())
            .also { staged.timing = it }
        incoming.alpha = timing.progress(now)
        outgoing.alpha = 1f
        val duration = timing.remainingMillis(now)
        incoming.animate().alpha(1f).setInterpolator(android.view.animation.LinearInterpolator()).setDuration(duration).start()
        transitionJob = scope.launch {
            delay(duration)
            if (pending !== staged || released) return@launch
            val old = exoPlayer
            exoPlayer = staged.player
            pending = null
            activeSlot = 1 - activeSlot
            boundTexture = boundSurface?.at(activeSlot)
            loadingMediaId = staged.mediaId
            loadingUrl = staged.url
            primary = staged.primary
            fallback = staged.fallback
            frameMediaId = staged.mediaId
            surfaceWithFrame = boundTexture
            videoAspect = staged.aspect
            old.release()
            updatePlayback()
            updateSampling()
        }
    }

    internal suspend fun load(mediaId: String?, primary: String?, fallback: String?, fadeOutMillis: Int) {
        if (released) return
        observeCanvasChange(mediaId)
        loadRevision++
        if (loadingVideoStream != null) {
            loadingVideoStream = null
            liveSampleJob?.cancel()
            liveSampleJob = null
        }
        val url = primary ?: fallback
        pending?.let { if (mediaId == it.mediaId && url == it.url) return }
        // Finish an already-visible blend instead of snapping back to its first
        // video. The caller's cancellation keeps only the latest waiting request.
        if (pending?.started == true) {
            transitionJob?.join()
            currentCoroutineContext().ensureActive()
            if (released) return
        }
        if (mediaId == loadingMediaId && url == loadingUrl) { cancelPending(); updateSampling(); return }
        cancelPending()
        lookSampleJob?.cancel()
        lookSampleJob = null
        fadeInJob?.cancel()
        val target = boundSurface?.at(1 - activeSlot)
        if (mediaId != null && url != null && frameMediaId != null && playbackRequested &&
            target != null && createStandby != null) {
            if (fade.value < 1f) fadeInJob = scope.launch { fade.animateTo(1f, tween(200, easing = SeamlessEasing)) }
            val next = createStandby.invoke()
            val staged = PendingCanvas(next, mediaId, url, primary, fallback, fadeOutMillis)
            pending = staged
            next.addListener(listenerFor(next))
            target.alpha = 0f
            target.bringToFront()
            next.setVideoTextureView(target)
            next.repeatMode = Player.REPEAT_MODE_ONE
            next.setMediaItem(mediaItem(mediaId, url))
            next.playWhenReady = previewHolds.isEmpty()
            next.prepare()
            armPendingTimeout(staged)
            return
        }
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
        if (released) return
        observeCanvasChange(stream.mediaId)
        loadRevision++
        if (loadingVideoStream?.videoId == stream.videoId && loadingVideoStream?.url == stream.url) return
        cancelPending()
        fadeInJob?.cancel()
        liveSampleJob?.cancel()
        liveSampleJob = null
        lookSampleJob?.cancel()
        lookSampleJob = null
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
                .setMediaId(stream.mediaId)
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
                .setMediaId(stream.mediaId)
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
            .setMediaId(mediaId)
            .setUri(normalized)
            .setMimeType(canvasMimeType(normalized, primary, fallback))
            // Same key DownloadUtil pre-caches canvas loops under.
            .setCustomCacheKey(com.convx.music.canvas.canvasCacheKey(mediaId, normalized))
            .build()
    }

    /** A still of the frame on screen, <= 720px, or null when no video is showing. */
    internal fun isShowing(mediaId: String?): Boolean = mediaId != null && boundTexture != null &&
        (frameMediaId == mediaId || (frameMediaId != null && pending?.mediaId == mediaId))

    internal fun capturePoster(mediaId: String?): Bitmap? {
        if (!isShowing(mediaId) || fade.value <= 0f) return null
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
    isVisible: Boolean = true,
    registerAsShared: Boolean = true,
    videoStream: com.convx.music.utils.YTPlayerUtils.VideoStream? = null,
    videoStartPositionMs: Long = 0L,
): CanvasVideoController {
    val context = LocalContext.current
    val mediaSourceFactory = rememberCanvasMediaSourceFactory()
    val scope = rememberCoroutineScope()
    val controller = remember {
        fun newPlayer() = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                trackSelectionParameters = trackSelectionParameters
                    .buildUpon()
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
        CanvasVideoController(newPlayer(), scope, ::newPlayer)
    }
    DisposableEffect(controller) {
        onDispose { controller.release() }
    }
    DisposableEffect(controller, registerAsShared) {
        if (registerAsShared) CanvasPosterSource.controller = controller
        onDispose {
            if (registerAsShared && CanvasPosterSource.controller === controller) CanvasPosterSource.controller = null
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
    val shouldPlay = isPlaying && inForeground && isVisible
    LaunchedEffect(shouldPlay) { controller.setPlaybackRequested(shouldPlay) }

    val enableCanvas by rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
    val disabledProviders by rememberPreference(com.convx.music.constants.CanvasDisabledProvidersKey, defaultValue = "")
    val manualRevision by CanvasSelectionRepository.selectionRevision.collectAsState()
    val mediaId = mediaMetadata?.id
    LaunchedEffect(mediaId) { controller.observeCanvasChange(mediaId) }
    val latestMetadata by rememberUpdatedState(mediaMetadata)
    // produceState keeps its previous value while keys change, so tag each result with its track ID.
    val fetched by produceState<Pair<String?, CanvasArtwork?>?>(null, mediaId, canvasSource, enableCanvas, disabledProviders, manualRevision, inForeground, isVisible, controller.hasSurface) {
        if (!inForeground || !isVisible || !controller.hasSurface) return@produceState
        val metadata = latestMetadata
        value = mediaId to if (!enableCanvas || metadata == null || mediaId == null) null else {
            CanvasSelectionRepository.resolve(
                context, mediaId, metadata.title, metadata.artists.firstOrNull()?.name.orEmpty(),
                metadata.album?.title, canvasSource,
            )
        }
    }
    val artwork = fetched?.takeIf { it.first == mediaId }?.second
    val fadeOut = trackFadeMillis.coerceAtMost(VIDEO_FADE_OUT_MAX_MS)
    val primary = artwork?.preferredAnimationUrl?.takeIf { it.isNotBlank() }
    val fallback = artwork?.animated?.takeIf { it.isNotBlank() }
    LaunchedEffect(mediaId, fetched?.first, primary, fallback, videoStream) {
        if (videoStream != null) controller.loadVideo(videoStream, videoStartPositionMs, fadeOut)
        // A pending lookup is not a decision that the next song has no canvas.
        // Keep the current loop until the next song has a positive or negative result.
        else if (mediaId == null || fetched?.first == mediaId) controller.load(mediaId, primary, fallback, fadeOut)
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
    priority: Int = 0,
    onFrame: (() -> Unit)? = null,
) {
    if (controller == null) return
    val token = remember { Any() }
    var texture by remember { mutableStateOf<TextureView?>(null) }
    var alternateTexture by remember { mutableStateOf<TextureView?>(null) }
    val surfaceAlpha = remember { Animatable(0f) }
    val hasFrame = controller.surfaceWithFrame?.let { it === texture || it === alternateTexture } == true
    LaunchedEffect(hasFrame) {
        // Only ever dissolves IN: a surface that loses the output keeps its last frame
        // (TextureView retains it), and loop-level fades belong to controller.fade.
        // The controller already fades a newly loaded video. A second long fade on
        // every surface handoff exposed the thumbnail during mini/full transitions.
        if (hasFrame) surfaceAlpha.snapTo(1f)
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
                    val alternate = com.convx.music.artistvideo.VideoFrameTextureView(viewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        alpha = 0f
                    }
                    alternate.onFrame = {
                        controller.onSurfaceFrame(alternate)
                        onFrame?.invoke()
                    }
                    addView(alternate)
                    texture = view
                    alternateTexture = alternate
                    controller.attach(token, view, priority, alternate)
                }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER))
            }
        },
        update = {
            val frame = it.getChildAt(0) as AspectRatioFrameLayout
            frame.setAspectRatio(aspect)
            frame.resizeMode = if (crop) AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
            for (index in 0 until frame.childCount) {
                val view = frame.getChildAt(index) as? com.convx.music.artistvideo.VideoFrameTextureView ?: continue
                view.onFrame = { controller.onSurfaceFrame(view); onFrame?.invoke() }
            }
        },
        modifier = modifier.graphicsLayer {
            clip = true
            alpha = controller.fade.value * surfaceAlpha.value
        },
    )
}
