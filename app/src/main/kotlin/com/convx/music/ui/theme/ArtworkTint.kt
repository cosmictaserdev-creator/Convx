/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.util.LruCache
import com.convx.music.ui.theme.colorengine.ColorEngine
import com.convx.music.utils.reportException
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Extracted tints keyed by artwork URL (the image's stable id): palette + Coil
 * decode is expensive, so the same artwork never recomputes on revisit or
 * recomposition. Bounded so it can't grow unbounded across a long session.
 */
private val artworkTintCache = LruCache<String, List<Color>>(128)

/**
 * Extracts a three-stop gradient (primary color, darker variant, black) from an
 * arbitrary artwork URL via [ColorEngine] — the "single color from the image"
 * background tint used by the Artist/Album/Playlist detail screens, and by
 * [com.convx.music.ui.component.AlbumGradient], which delegates here rather than
 * re-extracting the same artwork a second time.
 *
 * Returns an empty list until extraction finishes, if [thumbnailUrl] is null, or
 * if the engine can't produce a result — callers fall back to a theme color for
 * that case, e.g. via `animateColorAsState`.
 */
@Composable
fun rememberArtworkTint(thumbnailUrl: String?): List<Color> {
    val context = LocalContext.current
    // Seed from cache so a revisited image paints its tint immediately (no flash,
    // no re-extract).
    var extractedColors by remember(thumbnailUrl) {
        mutableStateOf(thumbnailUrl?.let { artworkTintCache.get(it) } ?: emptyList())
    }

    LaunchedEffect(thumbnailUrl) {
        if (thumbnailUrl == null) return@LaunchedEffect
        artworkTintCache.get(thumbnailUrl)?.let {
            extractedColors = it
            return@LaunchedEffect
        }
        withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .size(200, 200)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                val bitmap = result.image?.toBitmap()

                if (bitmap != null) {
                    // No Material Palette fallback: when the engine can't produce a
                    // result (recycled/corrupt bitmap, or clustering found nothing),
                    // extractedColors just stays empty and callers fall back to their
                    // own neutral theme color, same as the null-thumbnailUrl case.
                    ColorEngine.analyze(bitmap, cacheKey = thumbnailUrl)
                        ?.let { result -> gradientStopsFrom(result.palette.dominant) }
                        ?.let { colors ->
                            artworkTintCache.put(thumbnailUrl, colors)
                            extractedColors = colors
                        }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: Exception) {
                reportException(e)
            }
        }
    }

    return extractedColors
}

/**
 * The three-stop gradient [rememberArtworkTint] callers expect, from one primary colour.
 *
 * Identical arithmetic to [PlayerColorExtractor.extractGradientColors]' own tail, so the
 * engine path and the Palette fallback hand back the same shape and the same middle stop.
 */
private fun gradientStopsFrom(primary: Color): List<Color> = listOf(
    primary,
    primary.copy(
        red = (primary.red * 0.6f).coerceAtLeast(0f),
        green = (primary.green * 0.6f).coerceAtLeast(0f),
        blue = (primary.blue * 0.6f).coerceAtLeast(0f),
    ),
    Color.Black,
)
