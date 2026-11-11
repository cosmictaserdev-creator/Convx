package com.convx.music.playback.presentation

import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

internal actual fun fisheyeLens(size: IntSize, strength: Float, radius: Float): androidx.compose.ui.graphics.RenderEffect? {
    if (size.width <= 0 || size.height <= 0) return null
    return RuntimeEffect.makeForShader(FISHEYE_AGSL).use { effect ->
        RuntimeShaderBuilder(effect).use { builder ->
            builder.uniform("size", size.width.toFloat(), size.height.toFloat())
            builder.uniform("strength", strength)
            builder.uniform("radiusScale", radius)
            ImageFilter.makeRuntimeShader(builder, "content", null).asComposeRenderEffect()
        }
    }
}
