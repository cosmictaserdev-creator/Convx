/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.convx.music.ui.utils.stackBlur
import com.convx.music.utils.ArtworkProcessing
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlinx.coroutines.withContext

/** Tiny on purpose: upscaling a 24px bitmap gives a smooth gradient with no visible texture or edge. */
private const val MESH_DECODE_PX = 24
private const val MESH_BLUR_RADIUS = 5

private val meshBitmapCache = android.util.LruCache<String, Bitmap>(64)

/** Artwork reduced to a cached low-resolution field for a static mesh-like card footer. */
@Composable
fun ArtworkBlurMesh(
    model: Any?,
    modifier: Modifier = Modifier,
    finishKey: String = "",
    finish: ((Bitmap) -> Bitmap)? = null,
) {
    val context = LocalContext.current
    val cacheKey = "$model|$finishKey"
    var baked by remember(cacheKey) { mutableStateOf(meshBitmapCache.get(cacheKey)) }
    LaunchedEffect(cacheKey) {
        if (baked != null) return@LaunchedEffect
        val source = model ?: return@LaunchedEffect
        baked = ArtworkProcessing.cached("mesh|$cacheKey", { meshBitmapCache.get(cacheKey) }) {
            val request = ImageRequest.Builder(context).data(source).size(MESH_DECODE_PX).allowHardware(false).build()
            val bitmap = context.imageLoader.execute(request).image?.toBitmap() ?: return@cached null
            val field = withContext(ArtworkProcessing.dispatcher) {
                bitmap.copy(Bitmap.Config.ARGB_8888, true).stackBlur(MESH_BLUR_RADIUS).let { finish?.invoke(it) ?: it }
            }
            meshBitmapCache.put(cacheKey, field)
            field
        }
    }

    val paint = remember { Paint(Paint.FILTER_BITMAP_FLAG) }
    val matrix = remember { Matrix() }

    Box(
        modifier
            .clipToBounds()
            .graphicsLayer()
            .drawBehind {
                val bitmap = baked ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    val native = canvas.nativeCanvas
                    val cover = max(size.width / bitmap.width, size.height / bitmap.height)
                    for (layer in Layers) {
                        val angle = layer.offset
                        matrix.reset()
                        matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
                        matrix.postScale(
                            cover * layer.scale * (if (layer.mirror) -1f else 1f),
                            cover * layer.scale,
                        )
                        matrix.postRotate(layer.spin * 40f * sin(angle))
                        matrix.postTranslate(
                            size.width / 2f,
                            size.height / 2f,
                        )
                        paint.alpha = layer.alpha
                        native.drawBitmap(bitmap, matrix, paint)
                    }
                }
            },
    )
}

private class MeshLayer(
    val scale: Float,
    val alpha: Int,
    val spin: Float,
    val offset: Float,
    val mirror: Boolean,
)

private val Layers = arrayOf(
    MeshLayer(scale = 1.6f, alpha = 255, spin = 0.25f, offset = 0f, mirror = false),
    MeshLayer(scale = 1.9f, alpha = 150, spin = -0.35f, offset = 2.1f, mirror = true),
    MeshLayer(scale = 2.3f, alpha = 110, spin = 0.5f, offset = 4.2f, mirror = false),
)
