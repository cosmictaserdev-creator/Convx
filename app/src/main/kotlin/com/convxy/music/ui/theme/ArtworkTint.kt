/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import android.util.LruCache
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Extracted tints keyed by artwork URL (the image's stable id): palette + Coil
 * decode is expensive, so the same artwork never recomputes on revisit or
 * recomposition. Bounded so it can't grow unbounded across a long session.
 */
private val artworkTintCache = LruCache<String, List<Color>>(128)

/**
 * Extracts the same [PlayerColorExtractor] gradient (primary vibrant color,
 * darker variant, black) from an arbitrary artwork URL — the "single color
 * from the image" background tint used by the Artist/Album/Playlist detail
 * screens. Same Coil load + [Palette] pipeline as
 * [com.convxy.music.ui.component.AlbumGradient], copied rather than composed
 * since callers need the raw colors for buttons/text too, not just a painted
 * gradient box.
 *
 * Returns an empty list until extraction finishes (or if [thumbnailUrl] is
 * null) — callers should fall back to a theme color for that case, e.g. via
 * `animateColorAsState`.
 */
@Composable
fun rememberArtworkTint(thumbnailUrl: String?): List<Color> {
    val context = LocalContext.current
    // Neutral dark fallback (NOT the red-derived primaryContainer) so artwork
    // that yields no palette color reads as a dark card, never red.
    val fallbackColorInt = PlayerColorExtractor.NeutralFallbackColor.toArgb()
    // Seed from cache so a revisited image paints its tint immediately (no flash,
    // no re-extract).
    var extractedColors by remember(thumbnailUrl) {
        mutableStateOf(thumbnailUrl?.let { artworkTintCache.get(it) } ?: emptyList())
    }

    LaunchedEffect(thumbnailUrl) {
        if (thumbnailUrl == null) return@LaunchedEffect
        if (artworkTintCache.get(thumbnailUrl) != null) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .size(100, 100)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                val bitmap = result.image?.toBitmap()

                if (bitmap != null) {
                    val palette = withContext(Dispatchers.Default) {
                        Palette.from(bitmap)
                            .maximumColorCount(8)
                            .resizeBitmapArea(100 * 100)
                            .generate()
                    }
                    val colors = PlayerColorExtractor.extractGradientColors(
                        palette = palette,
                        fallbackColor = fallbackColorInt
                    )
                    artworkTintCache.put(thumbnailUrl, colors)
                    extractedColors = colors
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return extractedColors
}
