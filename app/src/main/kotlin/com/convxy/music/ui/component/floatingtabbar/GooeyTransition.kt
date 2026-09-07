/*
 * Liquid "gooey" merge/split effect for the inline↔expanded crossfade.
 *
 * Not ported from anywhere — Kyant0/AndroidLiquidGlass has no blob/metaball
 * utility. This is the standard "gooey" technique (blur, then a steep alpha
 * threshold) used by most liquid-blob UI effects: blurring bleeds nearby
 * shapes' alpha together, and the threshold snaps it back to a crisp edge,
 * which is what produces a connecting neck that thins and pinches off
 * instead of a flat crossfade. Built from primitives already vendored in
 * this app (see imports) rather than a new custom shader.
 */
package com.convxy.music.ui.component.floatingtabbar

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import com.convxy.music.ui.component.backdrop.internal.ColorFilterEffect
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported

// Alpha (0..255 scale, matching this app's other ColorMatrix usage in
// backdrop/effects/ColorFilter.kt) snaps from 0 to 1 across a narrow band
// centered on 0.5 — steep enough to read as a crisp edge, not more blur.
private const val ThresholdSlope = 10f
private const val ThresholdMidpoint255 = 0.5f * 255f

// The threshold never varies, so build the matrix filter once instead of
// allocating an identical one on every frame of every transition.
private val ThresholdColorFilter = ColorMatrixColorFilter(
    ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, ThresholdSlope, -ThresholdSlope * ThresholdMidpoint255
        )
    )
)

// Radius is quantised to whole pixels before the effect is built and cached, so a
// transition reuses one RenderEffect per distinct radius instead of allocating a
// fresh BlurEffect + ColorMatrixColorFilter every frame. Sub-pixel blur steps are
// not visible through an alpha threshold anyway. Bounded: the radius only ever
// ranges over 0..GooeyPeakBlur.
private val gooeyEffectCache = HashMap<Int, RenderEffect>()

private fun gooeyRenderEffect(blurRadiusPx: Float): RenderEffect {
    val key = blurRadiusPx.toInt()
    return gooeyEffectCache.getOrPut(key) {
        ColorFilterEffect(
            BlurEffect(key.toFloat(), key.toFloat(), TileMode.Decal),
            ThresholdColorFilter
        )
    }
}

/**
 * Composites this element into one offscreen layer and gooifies it: blurs by
 * [blurRadiusPx], then snaps the blur back to a crisp edge via an alpha
 * threshold. Offscreen compositing is what makes overlapping shapes' alpha
 * bleed together *before* the threshold — two independently-filtered layers
 * would just look like two blurred blobs with a seam, not a merged one.
 *
 * Zero cost at rest: when [blurRadiusPx] returns 0 (or RenderEffect isn't
 * supported pre-API 31), this is a no-op — no offscreen layer, no filter.
 */
fun Modifier.gooey(blurRadiusPx: () -> Float): Modifier {
    if (!isRenderEffectSupported()) return this
    return this.graphicsLayer {
        val radius = blurRadiusPx()
        if (radius > 0f) {
            compositingStrategy = CompositingStrategy.Offscreen
            renderEffect = gooeyRenderEffect(radius)
        } else {
            compositingStrategy = CompositingStrategy.Auto
            renderEffect = null
        }
    }
}
