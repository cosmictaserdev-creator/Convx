package com.convx.music.ui.component.flamingo

import android.graphics.BlendMode
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

private const val BRIGHT_PASS = """
    uniform shader content;
    uniform float threshold;
    uniform float gain;
    half4 main(float2 coord) {
        half4 p = content.eval(coord);
        float peak = max(p.r, max(p.g, p.b));
        float k = smoothstep(threshold, 1.0, peak) * gain;
        return half4(p.rgb * k, p.a * k);
    }
"""

private const val THRESHOLD = 0.5f
private const val TIGHT_RADIUS_DP = 10f
private const val WIDE_RADIUS_DP = 44f
private const val BASE_GAIN = 1.2f
private const val EXPOSURE_GAIN = 1.2f
private const val EXPOSURE_DIM = 0.4f
private const val EXPOSURE_STEPS = 16

/**
 * Bloom for the sung words: only pixels near full brightness are blurred at two
 * radii and added back, so the glow keeps the colour of the sung words and spills
 * past the line. Apply it to the sung line alone: on a whole list every frame of
 * the sweep re-blurs the full screen. [exposure] (0..1) works like a camera: a
 * stronger light raises the glow while the line itself is exposed darker.
 * Needs RenderEffect (API 31).
 */
@Composable
internal fun Modifier.lyricGlow(enabled: Boolean, exposure: () -> Float): Modifier {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this
    val density = LocalDensity.current.density
    val cache = remember(density) { GlowCache(density) }
    return graphicsLayer { renderEffect = cache.effectFor(exposure()) }
}

/** Effects are rebuilt only when the quantized exposure moves to a new step. */
private class GlowCache(private val density: Float) {
    private var step = -1
    private var effect: androidx.compose.ui.graphics.RenderEffect? = null

    fun effectFor(exposure: Float): androidx.compose.ui.graphics.RenderEffect {
        val next = (exposure.coerceIn(0f, 1f) * EXPOSURE_STEPS).roundToInt()
        return effect.takeIf { next == step } ?: build(next / EXPOSURE_STEPS.toFloat()).also {
            step = next
            effect = it
        }
    }

    private fun build(exposure: Float): androidx.compose.ui.graphics.RenderEffect {
        val bright = brightPass(BASE_GAIN + EXPOSURE_GAIN * exposure)
        val tight = RenderEffect.createChainEffect(blur(TIGHT_RADIUS_DP * density), bright)
        val wide = RenderEffect.createChainEffect(blur(WIDE_RADIUS_DP * density), bright)
        val halo = RenderEffect.createBlendModeEffect(wide, tight, BlendMode.PLUS)
        val scene = RenderEffect.createChainEffect(
            dim(1f - EXPOSURE_DIM * exposure),
            RenderEffect.createOffsetEffect(0f, 0f),
        )
        return RenderEffect.createBlendModeEffect(halo, scene, BlendMode.PLUS).asComposeRenderEffect()
    }
}

private fun blur(radius: Float) = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.DECAL)

private fun dim(level: Float): RenderEffect {
    val matrix = ColorMatrix().apply { setScale(level, level, level, 1f) }
    return RenderEffect.createColorFilterEffect(ColorMatrixColorFilter(matrix))
}

private fun brightPass(gain: Float): RenderEffect =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shader = RuntimeShader(BRIGHT_PASS).apply {
            setFloatUniform("threshold", THRESHOLD)
            setFloatUniform("gain", gain)
        }
        RenderEffect.createRuntimeShaderEffect(shader, "content")
    } else {
        // API 31-32 have no AGSL: a linear color matrix approximates the same cut-off.
        val scale = 2.5f * gain
        val offset = -THRESHOLD * scale * 255f
        val matrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, offset,
                0f, scale, 0f, 0f, offset,
                0f, 0f, scale, 0f, offset,
                0f, 0f, 0f, 1f, 0f,
            ),
        )
        RenderEffect.createColorFilterEffect(ColorMatrixColorFilter(matrix))
    }
