/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.backdrop

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.RuntimeShader
import androidx.annotation.RequiresApi

/**
 * Keep the shader inside a stage-sized render node. The parent canvas scales the resulting
 * colour field to player bounds. Nodes and their input bitmaps are reused across frames.
 */
@RequiresApi(33)
internal class BackdropShader private constructor(
    private val nodes: Array<RenderNode>,
    private val width: Int,
    private val height: Int,
    private val paint: Paint,
) {
    fun record(index: Int, bitmap: Bitmap): Boolean = try {
        val node = nodes[index]
        val canvas = node.beginRecording()
        try {
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
        } finally {
            node.endRecording()
        }
        true
    } catch (_: RuntimeException) {
        false
    } catch (_: LinkageError) {
        false
    }

    fun draw(canvas: Canvas, index: Int, width: Float, height: Float) {
        val checkpoint = canvas.save()
        try {
            canvas.scale(width / this.width, height / this.height)
            canvas.drawRenderNode(nodes[index])
        } finally {
            canvas.restoreToCount(checkpoint)
        }
    }

    companion object {
        fun create(inputs: Array<Bitmap>): BackdropShader? = try {
            val width = inputs[0].width
            val height = inputs[0].height
            val shader = RuntimeShader(BackdropBlurProgram)
            shader.setFloatUniform("bounds", width - 0.5f, height - 0.5f)
            val effect = RenderEffect.createRuntimeShaderEffect(shader, "stage")
            val nodes = Array(inputs.size) {
                RenderNode("Convx backdrop").apply {
                    setPosition(0, 0, width, height)
                    setClipToBounds(true)
                    setRenderEffect(effect)
                }
            }
            BackdropShader(nodes, width, height, Paint(Paint.FILTER_BITMAP_FLAG))
        } catch (_: RuntimeException) {
            null
        } catch (_: LinkageError) {
            null
        }
    }
}

private const val BackdropBlurProgram = """
    uniform shader stage;
    uniform float2 bounds;

    half4 sampleStage(float2 point) {
        return stage.eval(clamp(point, float2(0.5), bounds));
    }

    half4 main(float2 point) {
        half4 color = sampleStage(point) * 0.20;
        color += (sampleStage(point + float2(4.0, 0.0))
               + sampleStage(point - float2(4.0, 0.0))
               + sampleStage(point + float2(0.0, 4.0))
               + sampleStage(point - float2(0.0, 4.0))) * 0.11;
        color += (sampleStage(point + float2(3.0, 3.0))
               + sampleStage(point + float2(3.0, -3.0))
               + sampleStage(point + float2(-3.0, 3.0))
               + sampleStage(point - float2(3.0, 3.0))) * 0.09;
        return color;
    }
"""
