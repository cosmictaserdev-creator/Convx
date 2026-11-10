/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.convx.music.ui.utils.stackBlur
import com.convx.music.utils.ArtworkProcessing
import kotlin.math.roundToInt
import kotlinx.coroutines.withContext

/**
 * Bounded session cache of baked blurred backdrops, keyed by
 * "source|decodeSize|radiusPx|finishKey". One entry per track per background style; a
 * 128-entry ceiling covers hours of listening.
 */
private val blurredBackdropCache = object : android.util.LruCache<String, Bitmap>(32 * 1024 * 1024) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.allocationByteCount
}

/** Bakes and caches a low-resolution artwork blur once per source instead of blurring the full screen every frame. */
@Composable
fun BlurredBackdropImage(
    model: Any?,
    screenBlurRadiusDp: Float,
    modifier: Modifier = Modifier,
    decodeSize: Int = 160,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    colorFilter: ColorFilter? = null,
    /** Called when pixels are ready, including on a cache hit. */
    onReady: (() -> Unit)? = null,
    /** Optional CPU finish pass applied during the existing bitmap bake. */
    finish: ((Bitmap) -> Bitmap)? = null,
    finishKey: String = "",
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    // Project the radius using the actual decoded width; Coil may return a narrower non-square image.
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val screenRadiusPx = with(density) { screenBlurRadiusDp.dp.toPx() }
    val cacheKey = "$model|$decodeSize|$screenBlurRadiusDp|${configuration.screenWidthDp}x${configuration.screenHeightDp}|$finishKey"
    var baked by remember(cacheKey) { mutableStateOf(blurredBackdropCache.get(cacheKey)) }

    LaunchedEffect(cacheKey) {
        if (baked != null) return@LaunchedEffect
        val source = model ?: return@LaunchedEffect
        baked = ArtworkProcessing.cached("blur|$cacheKey", { blurredBackdropCache.get(cacheKey) }) {
            val request = ImageRequest.Builder(context)
                .data(source)
                .size(decodeSize)
                .allowHardware(false) // pixel readback for the bake
                .build()
            val bmp = context.imageLoader.execute(request).image?.toBitmap() ?: return@cached null
            val upscale = maxOf(
                screenWidthPx / bmp.width.coerceAtLeast(1),
                screenHeightPx / bmp.height.coerceAtLeast(1),
            ).coerceAtLeast(1f)
            val sourceRadius = (screenRadiusPx / upscale).roundToInt()
            // LaunchedEffect runs on AndroidUiDispatcher, i.e. the MAIN thread inside a frame
            // callback -- a pixel copy plus three blur passes there is a dropped frame every
            // time a new backdrop is baked. The bake is pure CPU over a private bitmap, so it
            // belongs on Default; only the state write below has to come back.
            val blurred = withContext(ArtworkProcessing.dispatcher) {
                bmp.copy(Bitmap.Config.ARGB_8888, true).stackBlur(sourceRadius)
                    .let { finish?.invoke(it) ?: it }
            }
            blurredBackdropCache.put(cacheKey, blurred)
            blurred
        }
    }

    // Covers both paths in one place: the bake finishing above, and the cache hit that
    // seeded `baked` before the effect ever ran.
    val readyCallback by rememberUpdatedState(onReady)
    LaunchedEffect(baked) {
        if (baked != null) readyCallback?.invoke()
    }

    // A cache hit shows at once; a bake that lands after first composition eases in instead
    // of popping -- it usually lands mid-way through a track-change crossfade.
    val reveal = remember(cacheKey) { androidx.compose.animation.core.Animatable(if (baked != null) 1f else 0f) }
    LaunchedEffect(baked) {
        if (baked != null && reveal.value < 1f) {
            reveal.animateTo(1f, androidx.compose.animation.core.tween(600, easing = com.convx.music.ui.player.SeamlessEasing))
        }
    }

    baked?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            alignment = alignment,
            colorFilter = colorFilter,
            modifier = modifier.graphicsLayer { alpha = reveal.value },
        )
    }
}

/** Flips [src] vertically and multiplies it by a flat [tint] — the CPU equivalent of a
 *  `scaleY = -1f` graphicsLayer under a `drawBehind { drawRect(tint, Multiply) }`. */
internal fun flipAndMultiply(src: Bitmap, tint: Int): Bitmap {
    val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    canvas.save()
    canvas.scale(1f, -1f, src.width / 2f, src.height / 2f)
    canvas.drawBitmap(src, 0f, 0f, null)
    canvas.restore()
    canvas.drawColor(tint, android.graphics.PorterDuff.Mode.MULTIPLY)
    return out
}

/** Centre-crop dimensions of a w×h source to [aspect] (w/h), pure for tests. */
internal fun cropSize(w: Int, h: Int, aspect: Float): Pair<Int, Int> =
    if (w.toFloat() / h > aspect) (h * aspect).roundToInt() to h
    else w to (w / aspect).roundToInt()

/**
 * Centre-crops [src] to [aspect] (w/h), optionally flips it vertically, applies [matrix],
 * then multiplies alpha by a vertical ramp ([rampAlphas] at [rampStops], 0..1 of height),
 * i.e. the CPU equivalent of an Offscreen layer + DstIn gradient.
 */
internal fun bakeRamp(
    src: Bitmap,
    aspect: Float,
    flip: Boolean,
    matrix: android.graphics.ColorMatrix?,
    rampStops: FloatArray,
    rampAlphas: FloatArray,
): Bitmap {
    val (cw, ch) = cropSize(src.width, src.height, aspect)
    val out = Bitmap.createBitmap(cw.coerceAtLeast(1), ch.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(out)
    val sx = (src.width - cw) / 2
    val sy = (src.height - ch) / 2
    canvas.save()
    if (flip) canvas.scale(1f, -1f, cw / 2f, ch / 2f)
    canvas.drawBitmap(
        src,
        android.graphics.Rect(sx, sy, sx + cw, sy + ch),
        android.graphics.Rect(0, 0, cw, ch),
        android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG).apply {
            matrix?.let { colorFilter = android.graphics.ColorMatrixColorFilter(it) }
        },
    )
    canvas.restore()
    val colors = IntArray(rampAlphas.size) {
        android.graphics.Color.argb((rampAlphas[it] * 255).roundToInt(), 255, 255, 255)
    }
    canvas.drawRect(
        0f, 0f, cw.toFloat(), ch.toFloat(),
        android.graphics.Paint().apply {
            shader = android.graphics.LinearGradient(
                0f, 0f, 0f, ch.toFloat(), colors, rampStops,
                android.graphics.Shader.TileMode.CLAMP,
            )
            xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN)
        },
    )
    return out
}
