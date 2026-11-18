/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil3.request.allowHardware
import coil3.toBitmap
import com.convx.music.ui.utils.resize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Top and bottom of the Now Playing gradient, ARGB. */
data class NowPlayingGradient(val top: Int, val bottom: Int)

private val gradientCache = android.util.LruCache<String, NowPlayingGradient>(64)

/**
 * iOS 26 Now Playing background: no blur, no mesh -- one vertical gradient from the artwork's
 * top-edge colour to its bottom-edge colour, the bottom pulled down toward black so the
 * transport reads (Resident Evil 2: sky teal over fire maroon; Flying Lotus: lit yellow over
 * olive). [hero]: the artwork itself fills the top of the screen and fades out at its foot, so
 * the gradient starts from the art's BOTTOM edge and the fade lands on its own colour.
 *
 * [pixels] is a small ARGB decode, [w] × [h].
 */
fun nowPlayingGradient(pixels: IntArray, w: Int, h: Int, hero: Boolean): NowPlayingGradient {
    require(w > 0 && h > 0 && w.toLong() * h <= pixels.size) { "Invalid artwork dimensions" }
    val band = (h * 0.15f).toInt().coerceAtLeast(1)
    val topEdge = averageRows(pixels, w, 0, band)
    val bottomEdge = averageRows(pixels, w, h - band, h)
    return if (hero) {
        NowPlayingGradient(shade(bottomEdge, maxValue = 0.47f, scale = 0.85f), shade(bottomEdge, maxValue = 0.28f, scale = 0.45f))
    } else {
        // Keep the gradient dark enough for white controls to stay readable on pale covers.
        NowPlayingGradient(shade(topEdge, maxValue = 0.47f, scale = 0.80f), shade(bottomEdge, maxValue = 0.28f, scale = 0.50f))
    }
}

private fun averageRows(pixels: IntArray, w: Int, from: Int, to: Int): Int {
    var r = 0L
    var g = 0L
    var b = 0L
    for (y in from until to) for (x in 0 until w) {
        val c = pixels[y * w + x]
        r += (c shr 16) and 0xFF
        g += (c shr 8) and 0xFF
        b += c and 0xFF
    }
    val n = ((to - from) * w).coerceAtLeast(1)
    return (0xFF shl 24) or ((r / n).toInt() shl 16) or ((g / n).toInt() shl 8) or (b / n).toInt()
}

/** Value scaled by [scale] and capped at [maxValue]; saturation nudged up so dark ends stay coloured. */
private fun shade(argb: Int, maxValue: Float, scale: Float): Int {
    val channels = intArrayOf((argb shr 16) and 255, (argb shr 8) and 255, argb and 255)
    val high = channels.max().toFloat()
    val low = channels.min().toFloat()
    val saturation = if (high == 0f) 0f else ((high - low) / high * 1.15f).coerceAtMost(1f)
    val value = (high * scale).coerceAtMost(maxValue * 255f)
    fun channel(index: Int): Int {
        val hueComponent = if (high == low) 0f else (channels[index] - low) / (high - low)
        return kotlin.math.round(value * (1f - saturation + saturation * hueComponent)).toInt().coerceIn(0, 255)
    }
    var red = channel(0)
    var green = channel(1)
    var blue = channel(2)
    fun linear(c: Int): Double {
        val normalized = c / 255.0
        return if (normalized <= 0.04045) normalized / 12.92
        else Math.pow((normalized + 0.055) / 1.055, 2.4)
    }
    // Preserve saturated reference colours; pale neutral artwork needs a slightly lower cap.
    while (0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue) > 0.183) {
        red = (red * 0.98f).toInt()
        green = (green * 0.98f).toInt()
        blue = (blue * 0.98f).toInt()
    }
    return (0xFF shl 24) or (red shl 16) or (green shl 8) or blue
}

@Composable
internal fun rememberNowPlayingGradient(
    artworkUrl: String?,
    hero: Boolean,
    videoLook: CanvasVideoLook? = null,
): androidx.compose.runtime.State<NowPlayingGradient?> {
    val context = LocalContext.current
    val key = if (videoLook != null) {
        "video:${videoLook.mediaId}:${videoLook.epoch}:${videoLook.liveFrameVersion}|$hero"
    } else artworkUrl?.let { "$it|$hero" }
    return produceState(key?.let { gradientCache.get(it) }, key) {
        if (key == null) {
            value = null
            return@produceState
        }
        // produceState retains its last value when its key changes. Check this
        // track's cache, rather than mistaking the previous gradient for a hit.
        gradientCache.get(key)?.let {
            value = it
            return@produceState
        }
        value = withContext(Dispatchers.Default) {
            val bitmap = videoLook?.representative ?: run {
                val request = coil3.request.ImageRequest.Builder(context)
                    .data(artworkUrl?.resize(120, 120))
                    .size(48)
                    .allowHardware(false)
                    .build()
                (coil3.SingletonImageLoader.get(context).execute(request) as? coil3.request.SuccessResult)
                    ?.image?.toBitmap() ?: return@withContext null
            }
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            nowPlayingGradient(pixels, bitmap.width, bitmap.height, hero).also { gradientCache.put(key, it) }
        } ?: value
    }
}

@Composable
fun NowPlayingBackground(
    artworkUrl: String?,
    hero: Boolean,
    modifier: Modifier = Modifier,
    transitionMillis: Int = 800,
    videoLook: CanvasVideoLook? = null,
) {
    val gradient by rememberNowPlayingGradient(artworkUrl, hero, videoLook)
    val top by animateColorAsState(
        Color(gradient?.top ?: 0xFF2C2C2E.toInt()),
        tween(transitionMillis),
        label = "nowPlayingTop",
    )
    val bottom by animateColorAsState(
        Color(gradient?.bottom ?: 0xFF121214.toInt()),
        tween(transitionMillis),
        label = "nowPlayingBottom",
    )
    // Colours read in the draw phase: a track change redraws, never recomposes.
    Box(modifier.drawBehind {
        drawRect(if (hero) Brush.verticalGradient(0f to top, 0.62f to top, 1f to bottom)
            else Brush.verticalGradient(0f to top, 0.606f to top, 1f to bottom))
    })
}
