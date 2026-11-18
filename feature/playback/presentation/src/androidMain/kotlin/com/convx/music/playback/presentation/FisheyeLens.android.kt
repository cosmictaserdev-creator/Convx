package com.convx.music.playback.presentation

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.IntSize

internal actual fun fisheyeLens(size: IntSize, strength: Float, radius: Float): androidx.compose.ui.graphics.RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || size.width <= 0 || size.height <= 0) return null
    val shader = RuntimeShader(FISHEYE_AGSL).apply {
        setFloatUniform("size", size.width.toFloat(), size.height.toFloat())
        setFloatUniform("strength", strength)
        setFloatUniform("radiusScale", radius)
    }
    return RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
}
