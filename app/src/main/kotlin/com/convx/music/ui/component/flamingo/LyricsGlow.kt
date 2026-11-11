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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.roundToInt

private const val BRIGHT_PASS = """
    uniform shader content;
    uniform float threshold;
    uniform float gain;
    uniform float4 glowColor;
    half4 main(float2 coord) {
        half4 p = content.eval(coord);
        float peak = max(p.r, max(p.g, p.b));
        float k = smoothstep(threshold, 1.0, peak) * gain;
        return half4(glowColor.rgb * k * p.a, p.a * k * glowColor.a);
    }
"""

private const val THRESHOLD = 0.5f
private const val TIGHT_RADIUS_DP = 4f
private const val WIDE_RADIUS_DP = 16f
private const val BASE_GAIN = 1.8f
private const val EXPOSURE_GAIN = 0.7f
private const val WIDE_GAIN = 0.65f
private const val EXPOSURE_DIM = 0.4f
private const val EXPOSURE_STEPS = 16
private const val STRENGTH_STEPS = 32

/**
 * Bloom for the sung words: only pixels near full brightness are blurred at two
 * radii and added back, so the glow keeps the colour of the sung words and spills
 * past the line. Apply it to the sung line alone: on a whole list every frame of
 * the sweep re-blurs the full screen. [exposure] (0..1) works like a camera: a
 * stronger light raises the glow. [preserveBrightness] keeps the letters at the
 * same brightness while singing; disabling it restores the camera-style dimming.
 * Needs RenderEffect (API 31).
 */
@Composable
internal fun Modifier.lyricGlow(
    enabled: Boolean,
    exposure: () -> Float,
    preserveBrightness: Boolean = true,
    glowColor: Color = Color.White,
    strength: () -> Float = { 1f },
): Modifier {
    if (!enabled || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this
    val density = LocalDensity.current.density
    val cache = remember(density, preserveBrightness, glowColor) { GlowCache(density, preserveBrightness, glowColor) }
    return graphicsLayer {
        val light = strength().coerceIn(0f, 1f)
        renderEffect = if (light > 0f) cache.effectFor(exposure(), light) else null
    }
}

/** Effects are rebuilt only when the quantized exposure moves to a new step. */
private class GlowCache(
    private val density: Float,
    private val preserveBrightness: Boolean,
    private val glowColor: Color,
) {
    private var step = -1
    private var effect: androidx.compose.ui.graphics.RenderEffect? = null

    fun effectFor(exposure: Float, strength: Float): androidx.compose.ui.graphics.RenderEffect {
        val exposureStep = (exposure.coerceIn(0f, 1f) * EXPOSURE_STEPS).roundToInt()
        val strengthStep = (strength.coerceIn(0f, 1f) * STRENGTH_STEPS).roundToInt()
        val next = exposureStep * (STRENGTH_STEPS + 1) + strengthStep
        return effect.takeIf { next == step } ?: build(
            exposureStep / EXPOSURE_STEPS.toFloat(),
            strengthStep / STRENGTH_STEPS.toFloat(),
        ).also {
            step = next
            effect = it
        }
    }

    private fun build(exposure: Float, strength: Float): androidx.compose.ui.graphics.RenderEffect {
        // A bright edge with a faint surrounding halo follows the glyphs. Feeding
        // a word shadow into a second, wide bloom turns the sweep into a soft blob.
        val gain = (BASE_GAIN + EXPOSURE_GAIN * exposure) * strength
        val tight = RenderEffect.createChainEffect(blur(TIGHT_RADIUS_DP * density), brightPass(gain, glowColor))
        val wide = RenderEffect.createChainEffect(blur(WIDE_RADIUS_DP * density), brightPass(gain * WIDE_GAIN, glowColor))
        val halo = RenderEffect.createBlendModeEffect(wide, tight, BlendMode.PLUS)
        val scene = RenderEffect.createChainEffect(
            dim(if (preserveBrightness) 1f else 1f - EXPOSURE_DIM * exposure * strength),
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

private fun brightPass(gain: Float, glowColor: Color): RenderEffect =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shader = RuntimeShader(BRIGHT_PASS).apply {
            setFloatUniform("threshold", THRESHOLD)
            setFloatUniform("gain", gain)
            setFloatUniform("glowColor", glowColor.red, glowColor.green, glowColor.blue, glowColor.alpha)
        }
        RenderEffect.createRuntimeShaderEffect(shader, "content")
    } else {
        // API 31-32 have no AGSL: a linear color matrix approximates the same cut-off.
        val scale = 2.5f * gain
        val offset = -THRESHOLD * scale * 255f
        val matrix = ColorMatrix(
            floatArrayOf(
                glowColor.red * scale, 0f, 0f, 0f, glowColor.red * offset,
                0f, glowColor.green * scale, 0f, 0f, glowColor.green * offset,
                0f, 0f, glowColor.blue * scale, 0f, glowColor.blue * offset,
                0f, 0f, 0f, glowColor.alpha, 0f,
            ),
        )
        RenderEffect.createColorFilterEffect(ColorMatrixColorFilter(matrix))
    }
