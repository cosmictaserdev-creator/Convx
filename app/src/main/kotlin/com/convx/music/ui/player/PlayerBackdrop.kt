package com.convx.music.ui.player

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.TextureView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.convx.music.ui.utils.stackBlur
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/** Where the artwork sits on screen; the backdrop places the picture there and spreads it outward. */
enum class BackdropAnchor {
    /** Portrait Squared: a square across the top. */
    TOP,
    /** Portrait Hero: the taller bled cover across the top. */
    HERO_TOP,
    /** Phone landscape, and tablets with lyrics or the queue open: the left half. */
    LEFT_HALF,
    /** Tablet cover: the card in the centred column. */
    CENTER,
    /** A Spotify canvas, made to fill a screen: crop-filled and blurred, nothing stretched. */
    FILL,
}

/**
 * The player's backdrop: the artwork, or the canvas video while it plays, placed where the cover
 * sits and extended outward by stacking strips of its own edges, then blurred until only colour
 * and motion remain. Everything is built in a tiny screen-shaped bitmap, blurred there on the CPU
 * and drawn upscaled, so no full-screen blur ever runs. Each result eases into the last, so cuts,
 * loop seams, track changes and layout changes all blend instead of jumping.
 */
@Composable
fun PlayerBackdrop(
    controller: CanvasVideoController,
    artworkUrl: String?,
    mediaId: String?,
    anchor: BackdropAnchor,
    live: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // A clip still fading out from the previous track must not tint this one.
    val texture = controller.surfaceWithFrame.takeIf { controller.frameMediaId == mediaId }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val stage = remember(screenSize) {
        if (screenSize.width <= 0 || screenSize.height <= 0) null
        else BackdropStage(STAGE_WIDTH_PX, (STAGE_WIDTH_PX * screenSize.height / screenSize.width).coerceAtLeast(1))
    }
    // Tagged with its url: produceState keeps the last value while the next loads, and building
    // from that stale bitmap tinted the new song with the previous one's colours.
    val loadedStill by produceState<Pair<String?, Bitmap?>>(null to null, artworkUrl) {
        value = artworkUrl to artworkUrl?.let { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .size(FRAME_PX, FRAME_PX)
                .allowHardware(false)
                .build()
            runCatching { context.imageLoader.execute(request).image?.toBitmap() }.getOrNull()
        }
    }
    val stillReady = loadedStill.first == artworkUrl
    val still = loadedStill.second.takeIf { stillReady }
    val tone = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply {
            setToSaturation(SATURATION)
            timesAssign(ColorMatrix().apply { setToScale(DIM, DIM, DIM, 1f) })
        })
    }
    var frameVersion by remember { mutableIntStateOf(0) }
    var hasFrame by remember { mutableStateOf(false) }
    LaunchedEffect(stage, texture, live, still, stillReady, anchor) {
        if (stage == null) return@LaunchedEffect
        stage.anchor = anchor
        val video = texture
        if (video == null) {
            // Still loading: hold the current picture. Loaded but none (or it failed): fade out
            // rather than keep showing the previous song's canvas indefinitely.
            if (!stillReady) return@LaunchedEffect
            val picture = still ?: run { hasFrame = false; return@LaunchedEffect }
            stage.composeTarget(picture, picture.width.toFloat() / picture.height)
        }
        // New targets arrive at the video's ~30 fps, but the shown picture eases toward the latest
        // one on every display frame, so it moves continuously instead of stepping each capture.
        var lastFrameNs = 0L
        var lastCaptureNs = 0L
        var settleNs = 0L
        var changeStartNs = 0L
        while (isActive) {
            val now = withFrameNanos { it }
            if (changeStartNs == 0L) changeStartNs = now
            val frameDeltaMs = if (lastFrameNs == 0L) 0f else (now - lastFrameNs) / 1_000_000f
            lastFrameNs = now
            val due = if (live) now - lastCaptureNs >= CAPTURE_INTERVAL_NS else lastCaptureNs == 0L
            if (video != null && video.isAvailable && due) {
                stage.captureVideo(video, controller.videoAspect, controller.loopKey, controller.loopPositionMs, controller.loopDurationMs)
                lastCaptureNs = now
                settleNs = 0L
            }
            // A new song, the video starting or the layout moving restarts this effect: crossfade
            // slowly into the new picture, then speed up to track the video's own motion.
            val changeProgress = ((now - changeStartNs) / CHANGE_NS.toFloat()).coerceIn(0f, 1f)
            stage.blendTarget(frameDeltaMs, CHANGE_EASE_TIME_MS + (EASE_TIME_MS - CHANGE_EASE_TIME_MS) * changeProgress)
            hasFrame = true
            frameVersion++
            // Paused, collapsed or still: run only until the ease has landed, then stop drawing.
            if (video == null || !live) {
                settleNs += (frameDeltaMs * 1_000_000).toLong()
                if (settleNs >= SETTLE_NS) break
            }
        }
    }
    val shown by animateFloatAsState(
        targetValue = if (hasFrame) 1f else 0f,
        animationSpec = tween(FADE_MS, easing = SeamlessEasing),
        label = "playerBackdrop",
    )
    Canvas(
        modifier
            .onSizeChanged { screenSize = it }
            .graphicsLayer { alpha = shown }
    ) {
        // Read so every tick invalidates this draw; the stage bitmap is mutated in place.
        frameVersion
        if (stage == null || !stage.primed) return@Canvas
        drawImage(
            stage.image,
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
            filterQuality = FilterQuality.Low,
            colorFilter = tone,
        )
    }
}

/** Main-thread only: video ticks read the texture, which TextureView requires. */
private class BackdropStage(width: Int, height: Int) {
    private val frame = Bitmap.createBitmap(FRAME_PX, FRAME_PX, Bitmap.Config.ARGB_8888)
    private val framePixels = IntArray(FRAME_PX * FRAME_PX)
    private val target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val targetCanvas = android.graphics.Canvas(target)
    private val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val canvas = android.graphics.Canvas(bitmap)
    private val ease = Paint(Paint.FILTER_BITMAP_FLAG)
    private val scale = Paint(Paint.FILTER_BITMAP_FLAG)
    private val drawRect = RectF()
    private val anchorRect = RectF()
    private val content = Rect()
    private val visible = Rect()
    private val slice = Rect()
    val image = bitmap.asImageBitmap()
    var primed = false
        private set
    var anchor: BackdropAnchor = BackdropAnchor.TOP
        set(value) {
            if (field != value) loopFrames = emptyArray()
            field = value
        }
    private var loopKey: String? = null
    private var loopFrames: Array<IntArray?> = emptyArray()

    /**
     * A looping clip's finished targets are kept by position in the loop, so after the first pass
     * the texture readback (a GPU sync) and the blur are skipped.
     */
    fun captureVideo(texture: TextureView, videoAspect: Float, key: String?, positionMs: Long, durationMs: Long) {
        val slots = if (key != null && durationMs > 0) (durationMs / CAPTURE_MS + 1).toInt() else 0
        if (slots == 0 || slots > MAX_LOOP_SLOTS) {
            texture.getBitmap(frame)
            composeTarget(frame, videoAspect)
        } else {
            if (key != loopKey || loopFrames.size != slots) {
                loopKey = key
                loopFrames = arrayOfNulls(slots)
            }
            val slot = ((positionMs % durationMs) / CAPTURE_MS).toInt().coerceIn(0, slots - 1)
            val cached = loopFrames[slot]
            if (cached != null) {
                target.setPixels(cached, 0, target.width, 0, 0, target.width, target.height)
            } else {
                texture.getBitmap(frame)
                composeTarget(frame, videoAspect)
                loopFrames[slot] = IntArray(target.width * target.height).also {
                    target.getPixels(it, 0, target.width, 0, 0, target.width, target.height)
                }
            }
        }
    }

    /** Moves the shown picture toward the target by a share that depends only on elapsed time. */
    fun blendTarget(frameDeltaMs: Float, easeTimeMs: Float) {
        if (!primed) {
            canvas.drawBitmap(target, 0f, 0f, null)
            primed = true
            return
        }
        val share = 1f - kotlin.math.exp(-frameDeltaMs / easeTimeMs)
        ease.alpha = (255 * share).roundToInt().coerceIn(1, 255)
        canvas.drawBitmap(target, 0f, 0f, ease)
    }

    /**
     * Builds the blurred target from [source]: the picture at the anchor, its edges stretched
     * outward. [sourceAspect] is the picture's real shape; a video frame arrives squeezed square.
     */
    fun composeTarget(source: Bitmap, sourceAspect: Float) {
        val w = target.width.toFloat()
        val h = target.height.toFloat()
        anchorRect.setAnchor(anchor, w, h)
        trimDarkBorders(source, content)
        coverCrop(content, anchorRect, sourceAspect / (source.width.toFloat() / source.height), visible)
        // Whole picture stretched underneath, so no corner the strips miss is ever empty.
        drawRect.set(0f, 0f, w, h)
        targetCanvas.drawBitmap(source, visible, drawRect, scale)
        val sliceRows = (visible.height() * SLICE).roundToInt().coerceAtLeast(1)
        val sliceCols = (visible.width() * SLICE).roundToInt().coerceAtLeast(1)
        slice.set(visible.left, visible.bottom - sliceRows, visible.right, visible.bottom)
        extend(source, anchorRect.bottom, h, anchorRect.left, anchorRect.right, vertical = true)
        slice.set(visible.left, visible.top, visible.right, visible.top + sliceRows)
        extend(source, anchorRect.top, 0f, anchorRect.left, anchorRect.right, vertical = true)
        slice.set(visible.right - sliceCols, visible.top, visible.right, visible.bottom)
        extend(source, anchorRect.right, w, anchorRect.top, anchorRect.bottom, vertical = false)
        slice.set(visible.left, visible.top, visible.left + sliceCols, visible.bottom)
        extend(source, anchorRect.left, 0f, anchorRect.top, anchorRect.bottom, vertical = false)
        targetCanvas.drawBitmap(source, visible, anchorRect, scale)
        // Twice: one stack blur leaves soft ridges; two approach a Gaussian with no visible edges.
        target.stackBlur(BLUR_RADIUS_PX).stackBlur(BLUR_RADIUS_PX)
    }

    /**
     * Repeats [slice] from [start] to [end] in strips that grow longer and wider away from the
     * picture, so its edge colour carries on to the screen's edge as one continuous spread.
     */
    private fun extend(source: Bitmap, start: Float, end: Float, crossFrom: Float, crossTo: Float, vertical: Boolean) {
        val span = end - start
        if (abs(span) < 0.5f) return
        val direction = sign(span)
        val firstLength = abs(span) / (STRIPS + STRIP_GROW * STRIPS * (STRIPS - 1) / 2)
        val crossCenter = (crossFrom + crossTo) / 2
        val crossHalf = (crossTo - crossFrom) / 2
        var position = start
        for (strip in 0 until STRIPS) {
            val length = firstLength * (1f + strip * STRIP_GROW)
            val half = crossHalf * (1f + strip * STRIP_WIDEN)
            // One pixel of overlap so rounding never leaves a seam between strips.
            val near = position - direction
            val far = position + direction * length
            val lo = minOf(near, far)
            val hi = maxOf(near, far)
            if (vertical) drawRect.set(crossCenter - half, lo, crossCenter + half, hi)
            else drawRect.set(lo, crossCenter - half, hi, crossCenter + half)
            targetCanvas.drawBitmap(source, slice, drawRect, scale)
            position += direction * length
        }
    }

    /** Letterbox bars and black mattes would otherwise be the edge that gets stretched. */
    private fun trimDarkBorders(source: Bitmap, out: Rect) {
        val w = source.width
        val h = source.height
        val pixels = if (w * h <= framePixels.size) framePixels else IntArray(w * h)
        source.getPixels(pixels, 0, w, 0, 0, w, h)
        fun rowLit(y: Int) = (0 until w).sumOf { luma(pixels[y * w + it]) } / w > DARK_LUMA
        fun columnLit(x: Int) = (0 until h).sumOf { luma(pixels[it * w + x]) } / h > DARK_LUMA
        var top = 0
        var bottom = h
        var left = 0
        var right = w
        while (top < h / 4 && !rowLit(top)) top++
        while (bottom > h - h / 4 && !rowLit(bottom - 1)) bottom--
        while (left < w / 4 && !columnLit(left)) left++
        while (right > w - w / 4 && !columnLit(right - 1)) right--
        out.set(left, top, right, bottom)
    }
}

private fun luma(argb: Int): Int =
    ((argb shr 16 and 0xFF) * 299 + (argb shr 8 and 0xFF) * 587 + (argb and 0xFF) * 114) / 1000

/** Where [anchor] puts the artwork on a [w] x [h] stage. */
private fun RectF.setAnchor(anchor: BackdropAnchor, w: Float, h: Float) {
    when (anchor) {
        BackdropAnchor.TOP -> set(0f, 0f, w, w)
        BackdropAnchor.HERO_TOP -> set(0f, 0f, w, w * HERO_COVER_ASPECT)
        BackdropAnchor.LEFT_HALF -> set(0f, 0f, w / 2, h)
        BackdropAnchor.FILL -> set(0f, 0f, w, h)
        BackdropAnchor.CENTER -> {
            val side = minOf(w * 0.4f, h * 0.55f)
            val centerY = h * 0.38f
            set((w - side) / 2, centerY - side / 2, (w + side) / 2, centerY + side / 2)
        }
    }
}

/**
 * The part of [content] that fills [dst] without distortion, as the cover crops it on screen.
 * [pixelAspect] is how much wider than tall one source pixel really is.
 */
private fun coverCrop(content: Rect, dst: RectF, pixelAspect: Float, out: Rect) {
    val srcAspect = content.width() * pixelAspect / content.height().coerceAtLeast(1)
    val dstAspect = dst.width() / dst.height().coerceAtLeast(1f)
    if (srcAspect > dstAspect) {
        val width = (content.height() * dstAspect / pixelAspect).roundToInt().coerceAtLeast(1)
        val left = content.centerX() - width / 2
        out.set(left, content.top, left + width, content.bottom)
    } else {
        val height = (content.width() * pixelAspect / dstAspect).roundToInt().coerceAtLeast(1)
        val top = content.centerY() - height / 2
        out.set(content.left, top, content.right, top + height)
    }
}

/** Hero's portrait cover: 524pt tall on a 393pt-wide reference screen (see Player_v2.kt). */
private const val HERO_COVER_ASPECT = 524f / 393f
/** About 15 s of loop at a few KB a slot; longer clips are read back live rather than cached. */
private const val MAX_LOOP_SLOTS = 450
/** Tiny on purpose: the backdrop carries colour and motion, never detail. */
private const val FRAME_PX = 24
/** Width of the stage the picture is spread and blurred in; height follows the screen's shape. */
private const val STAGE_WIDTH_PX = 48
/** Over a quarter of the 48px stage, applied twice: no edge or shape survives, only colour. */
private const val BLUR_RADIUS_PX = 14
/** Video captures at ~30 fps, matching canvas clips; the readback and the 48px blur cost well under a millisecond. */
private const val CAPTURE_MS = 33L
private const val CAPTURE_INTERVAL_NS = CAPTURE_MS * 1_000_000L - 2_000_000L
/** Time constant of the ease toward each new target: long enough to hide capture steps, short enough to track motion. */
private const val EASE_TIME_MS = 70f
/** Ease time constant at the start of a change: a soft crossfade rather than a cut. */
private const val CHANGE_EASE_TIME_MS = 450f
/** How long the ease takes to speed back up from a change's crossfade to tracking motion. */
private const val CHANGE_NS = 900_000_000L
/** Long enough for a change's crossfade to land: the previous picture is gone to the eye by then. */
private const val SETTLE_NS = 1_600_000_000L
private const val FADE_MS = 600
/** A touch above neutral; blurring greys colours out, but more reads as raised contrast. */
private const val SATURATION = 1.1f
/** Slightly darker than the picture, so white text and glyphs stay readable on bright artwork. */
private const val DIM = 0.85f
/** Below this average brightness (0-255) an edge row or column counts as a black bar. */
private const val DARK_LUMA = 12
/** Share of the picture, from each edge, that the strips beyond that edge repeat. */
private const val SLICE = 0.15f
private const val STRIPS = 6
/** Per strip further out: each is this much longer and wider than the one before. */
private const val STRIP_GROW = 0.25f
private const val STRIP_WIDEN = 0.15f
