/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player.backdrop

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.TextureView
import androidx.annotation.MainThread
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import com.convx.music.constants.CanvasSource
import kotlin.math.abs
import kotlin.math.roundToInt

internal enum class BackdropAnchor { TOP, HERO, LEFT, CENTER }

/** Owned by one player controller and accessed only on the UI thread. */
internal class PlayerBackdrop {
    private val frameVersion = mutableIntStateOf(0)
    private var stage: BackdropStage? = null
    private var history: FrameHistory? = null
    private var source: CanvasSource? = null
    private var officialVideo = false
    private var viewport = IntSize.Zero
    private var anchor = BackdropAnchor.TOP
    private var hasFrame = false
    private var hardwareAccelerated = false
    private var shaderFailed = false
    private val mainHandler = Handler(Looper.getMainLooper())
    private val renderCpuFallback = Runnable {
        if (hasFrame && stage?.outputNeedsShader == true) render()
    }

    var tier = PlayerBackdropTier.B
        @MainThread set(value) {
            if (field == value) return
            field = value
            if (value == PlayerBackdropTier.C) clear() else if (hasFrame) render()
        }

    @MainThread
    fun onFrame(texture: TextureView, source: CanvasSource?, isOfficialVideo: Boolean, timestampMs: Long) {
        if (tier == PlayerBackdropTier.C || !texture.isAvailable) return
        if (this.source != source || officialVideo != isOfficialVideo) {
            clear()
            this.source = source
            officialVideo = isOfficialVideo
        }

        val temporal = source == CanvasSource.APPLE_MUSIC && !isOfficialVideo
        val frames = history?.takeIf { it.temporal == temporal }
            ?: FrameHistory(temporal).also { history = it }
        if (!frames.capture(texture, timestampMs)) return
        hasFrame = true
        render()
    }

    @MainThread
    fun clear() {
        history?.clear()
        hasFrame = false
        frameVersion.intValue++
    }

    @MainThread
    fun setViewport(size: IntSize) {
        if (viewport == size) return
        viewport = size
        stage = null
        if (hasFrame) render()
    }

    @MainThread
    fun setAnchor(value: BackdropAnchor) {
        if (anchor == value) return
        anchor = value
        if (hasFrame) render()
    }

    @MainThread
    fun setHardwareAccelerated(value: Boolean) {
        if (hardwareAccelerated == value) return
        hardwareAccelerated = value
        if (hasFrame) render()
    }

    private fun render() {
        if (viewport.width == 0 || viewport.height == 0) return
        val frames = history ?: return
        val buffer = stage ?: BackdropStage(viewport).also { stage = it }
        val useShader = tier == PlayerBackdropTier.A && hardwareAccelerated && !shaderFailed
        if (!buffer.render(frames, anchor, officialVideo, useShader) && useShader) shaderFailed = true
        frameVersion.intValue++
    }

    /** State is read during draw so frames never invalidate composition or layout. */
    fun draw(scope: DrawScope) {
        frameVersion.intValue
        if (!hasFrame) return
        val buffer = stage ?: return
        if (buffer.drawShader(scope)) return
        if (buffer.outputNeedsShader) {
            if (!shaderFailed) {
                shaderFailed = true
                // A static or paused video may never send another frame.
                mainHandler.post(renderCpuFallback)
            }
            return
        }
        with(scope) {
            drawImage(
                buffer.image,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
                filterQuality = FilterQuality.Low,
            )
        }
    }
}

@Composable
internal fun ExtendedPlayerBackdrop(
    backdrop: PlayerBackdrop,
    modifier: Modifier,
    anchor: BackdropAnchor,
    extended: Boolean,
) {
    if (!extended) return
    val view = LocalView.current
    SideEffect {
        backdrop.setHardwareAccelerated(view.isHardwareAccelerated)
        backdrop.setAnchor(anchor)
    }
    Canvas(modifier.onSizeChanged(backdrop::setViewport)) { backdrop.draw(this) }
}

private const val FRAME_SIDE = 16
private const val HISTORY_SIZE = 10
private const val MAX_STAGE_SIDE = 56
private const val ANALYZE_INTERVAL_MS = 250L
private const val MAX_FRAME_GAP_MS = 125L
private const val STACK_LAYERS = 7
private const val SLICE_HEIGHT = 1f / 3f
private const val LAYER_WIDENING = 0.14f
private const val LAYER_GROWTH = 0.18f
private const val FLOW_SHIFT = 2
private const val CUT_LUMA_DELTA = 52
private const val BOX_RADIUS = 3

private class FrameSample {
    val bitmap = Bitmap.createBitmap(FRAME_SIDE, FRAME_SIDE, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(FRAME_SIDE * FRAME_SIDE)
    val rows = IntArray(FRAME_SIDE)
    val columns = IntArray(FRAME_SIDE)
    var timestampMs = 0L

    fun read(timestampMs: Long, temporal: Boolean) {
        bitmap.getPixels(pixels, 0, FRAME_SIDE, 0, 0, FRAME_SIDE, FRAME_SIDE)
        this.timestampMs = timestampMs
        if (!temporal) return
        rows.fill(0)
        columns.fill(0)
        for (y in 0 until FRAME_SIDE) {
            for (x in 0 until FRAME_SIDE) {
                val brightness = luma(pixels[y * FRAME_SIDE + x])
                rows[y] += brightness
                columns[x] += brightness
            }
        }
        for (i in 0 until FRAME_SIDE) {
            rows[i] /= FRAME_SIDE
            columns[i] /= FRAME_SIDE
        }
    }

    fun pixel(x: Float, y: Float): Int {
        val px = (x * (FRAME_SIDE - 1)).roundToInt().coerceIn(0, FRAME_SIDE - 1)
        val py = (y * (FRAME_SIDE - 1)).roundToInt().coerceIn(0, FRAME_SIDE - 1)
        return pixels[py * FRAME_SIDE + px]
    }
}

private class FrameHistory(val temporal: Boolean) {
    private val frames = Array(if (temporal) HISTORY_SIZE else 1) { FrameSample() }
    private var head = 0
    private var count = 0
    private var lastAnalysisMs = 0L
    private val colorBins = IntArray(64)
    var verticalConfidence = 0f
        private set
    var frameIntervalMs = 0L
        private set
    var salienceX = 0.5f
        private set
    var dominantColor = 0
        private set
    val live: FrameSample get() = frames[head]

    fun capture(texture: TextureView, timestampMs: Long): Boolean {
        val previous = live
        val nextIndex = (head + 1) % frames.size
        val next = frames[nextIndex]
        if (texture.getBitmap(next.bitmap) == null) return false
        next.read(timestampMs, temporal)
        val gap = timestampMs - previous.timestampMs
        val related = temporal && count > 0 && gap in 1..MAX_FRAME_GAP_MS &&
            !isCut(previous, next)
        head = nextIndex
        count = if (related) (count + 1).coerceAtMost(frames.size) else 1
        frameIntervalMs = if (related) gap else 0L
        verticalConfidence = if (related) {
            verticalFlowConfidence(previous.rows, next.rows, previous.columns, next.columns)
        } else {
            0f
        }

        if (lastAnalysisMs == 0L || (temporal && !related) ||
            timestampMs - lastAnalysisMs >= ANALYZE_INTERVAL_MS || timestampMs < lastAnalysisMs
        ) {
            analyze(next)
            lastAnalysisMs = timestampMs
        }
        return true
    }

    fun frameAt(timestampMs: Long): FrameSample {
        var closest = live
        for (lag in 1 until count) {
            val candidate = frames[(head - lag + frames.size) % frames.size]
            if (abs(timestampMs - candidate.timestampMs) > abs(timestampMs - closest.timestampMs)) break
            closest = candidate
        }
        return closest
    }

    fun clear() {
        count = 0
        lastAnalysisMs = 0
        verticalConfidence = 0f
        frameIntervalMs = 0
        salienceX = 0.5f
        dominantColor = 0
    }

    private fun analyze(frame: FrameSample) {
        colorBins.fill(0)
        var totalBrightness = 0
        for (pixel in frame.pixels) totalBrightness += luma(pixel)
        val averageBrightness = totalBrightness / frame.pixels.size
        var totalWeight = 0
        var weightedX = 0
        var dominantBin = 0
        for (i in frame.pixels.indices) {
            val pixel = frame.pixels[i]
            val red = pixel ushr 16 and 255
            val green = pixel ushr 8 and 255
            val blue = pixel and 255
            val saturation = maxOf(red, green, blue) - minOf(red, green, blue)
            val weight = abs(luma(pixel) - averageBrightness) + saturation / 2
            weightedX += (i % FRAME_SIDE) * weight
            totalWeight += weight

            val bin = ((red shr 6) shl 4) or ((green shr 6) shl 2) or (blue shr 6)
            colorBins[bin]++
            if (colorBins[bin] > colorBins[dominantBin]) dominantBin = bin
        }
        dominantColor = rgb(
            ((dominantBin shr 4) and 3) * 64 + 32,
            ((dominantBin shr 2) and 3) * 64 + 32,
            (dominantBin and 3) * 64 + 32,
        )
        salienceX = if (totalWeight == 0) 0.5f else weightedX.toFloat() / totalWeight / (FRAME_SIDE - 1)
    }

    private fun isCut(previous: FrameSample, next: FrameSample): Boolean {
        var difference = 0
        for (i in previous.pixels.indices) difference += abs(luma(previous.pixels[i]) - luma(next.pixels[i]))
        return difference > CUT_LUMA_DELTA * previous.pixels.size
    }
}

/** Measures only downward flow on rows and either horizontal direction on columns. */
internal fun verticalFlowConfidence(
    previousRows: IntArray,
    currentRows: IntArray,
    previousColumns: IntArray,
    currentColumns: IntArray,
): Float {
    val down = shiftImprovement(previousRows, currentRows, bidirectional = false)
    val across = shiftImprovement(previousColumns, currentColumns, bidirectional = true)
    return if (down < 2f) 0f else down / (down + across)
}

private fun shiftImprovement(previous: IntArray, current: IntArray, bidirectional: Boolean): Float {
    val margin = FLOW_SHIFT
    val end = current.size - margin
    if (end <= margin) return 0f
    var unchangedError = 0
    for (i in margin until end) unchangedError += abs(current[i] - previous[i])
    var bestError = unchangedError
    val firstShift = if (bidirectional) -FLOW_SHIFT else 1
    for (shift in firstShift..FLOW_SHIFT) {
        var error = 0
        for (i in margin until end) error += abs(current[i] - previous[i - shift])
        if (error < bestError) bestError = error
    }
    return (unchangedError - bestError).toFloat() / (end - margin)
}

private class BackdropStage(viewport: IntSize) {
    private val longSide = maxOf(viewport.width, viewport.height)
    private val width = (MAX_STAGE_SIDE * viewport.width.toFloat() / longSide).roundToInt().coerceAtLeast(1)
    private val height = (MAX_STAGE_SIDE * viewport.height.toFloat() / longSide).roundToInt().coerceAtLeast(1)
    private val aspect = viewport.width.toFloat() / viewport.height
    private val outputs = Array(2) { Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) }
    private val images = Array(2) { outputs[it].asImageBitmap() }
    private val pixels = IntArray(width * height)
    private val blurScratch = IntArray(pixels.size)
    private var shown = 0
    private var shader: BackdropShader? = null
    var outputNeedsShader = false
        private set
    private var left = 0f
    private var right = 1f
    private var top = 0f
    private var seam = 1f
    val image: ImageBitmap get() = images[shown]

    private val axis = ExtensionAxis(maxOf(width, height))

    fun render(history: FrameHistory, anchor: BackdropAnchor, reflection: Boolean, useShader: Boolean): Boolean {
        setAnchor(anchor)
        val live = history.live
        val dominant = history.dominantColor
        val brightnessScale = 0.72f - luma(dominant) / 255f * 0.15f
        val horizontalSeam = anchor == BackdropAnchor.LEFT
        val seamPosition = if (horizontalSeam) right else seam
        axis.update(history, if (horizontalSeam) width else height, seamPosition, horizontalSeam, reflection)

        for (y in 0 until height) {
            val screenY = (y + 0.5f) / height
            val normalY = (screenY - top) / (seam - top)
            for (x in 0 until width) {
                val screenX = (x + 0.5f) / width
                val normalX = (screenX - left) / (right - left)
                val index = if (horizontalSeam) x else y
                val distance = axis.distance[index]
                val color = when {
                    distance == 0f -> live.pixel(normalX, normalY)
                    reflection && horizontalSeam -> live.pixel(1f - distance / right, normalY)
                    reflection -> live.pixel(normalX, 1f - distance / (seam - top))
                    else -> {
                        val across = if (horizontalSeam) normalY else normalX
                        val stretched = (across - axis.center[index]) / axis.widen[index] + 0.5f
                        val depth = axis.depth[index]
                        val sampleX = if (horizontalSeam) depth else stretched
                        val sampleY = if (horizontalSeam) stretched else depth
                        val current = live.pixel(sampleX, sampleY)
                        val delayed = axis.delayed[index]
                        if (delayed == null) current else {
                            mix(current, delayed.pixel(sampleX, sampleY), history.verticalConfidence)
                        }
                    }
                }
                pixels[y * width + x] = dim(mix(color, dominant, 0.10f), brightnessScale)
            }
        }

        if (useShader && Build.VERSION.SDK_INT >= 33 && shader == null) {
            shader = BackdropShader.create(outputs)
        }
        shown = 1 - shown
        val bitmap = outputs[shown]
        val backdropShader = shader
        outputNeedsShader = if (useShader && Build.VERSION.SDK_INT >= 33 && backdropShader != null) {
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            backdropShader.record(shown, bitmap)
        } else false
        if (!outputNeedsShader) {
            blur(pixels, blurScratch, width, height)
            blur(blurScratch, pixels, height, width)
            blur(pixels, blurScratch, width, height)
            blur(blurScratch, pixels, height, width)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        }
        return outputNeedsShader
    }

    fun drawShader(scope: DrawScope): Boolean {
        if (!outputNeedsShader || Build.VERSION.SDK_INT < 33) return false
        val backdropShader = shader ?: return false
        val native = scope.drawContext.canvas.nativeCanvas
        if (!native.isHardwareAccelerated) return false
        return try {
            backdropShader.draw(native, shown, scope.size.width, scope.size.height)
            true
        } catch (_: RuntimeException) {
            false
        } catch (_: LinkageError) {
            false
        }
    }

    private fun setAnchor(anchor: BackdropAnchor) {
        left = 0f
        right = 1f
        top = 0f
        seam = 1f
        when (anchor) {
            BackdropAnchor.TOP -> seam = aspect.coerceAtMost(0.85f)
            BackdropAnchor.HERO -> seam = (aspect * (524f / 393f)).coerceAtMost(0.85f)
            BackdropAnchor.LEFT -> right = 0.5f
            BackdropAnchor.CENTER -> {
                val coverHeight = minOf(aspect * 0.40f, 0.55f)
                val coverWidth = coverHeight / aspect
                left = (1f - coverWidth) / 2
                right = (1f + coverWidth) / 2
                top = 0.38f - coverHeight / 2
                seam = 0.38f + coverHeight / 2
            }
        }
    }
}

/** The extension's geometry and temporal sample are shared across every pixel on an axis line. */
private class ExtensionAxis(size: Int) {
    val distance = FloatArray(size)
    val center = FloatArray(size)
    val widen = FloatArray(size)
    val depth = FloatArray(size)
    val delayed = arrayOfNulls<FrameSample>(size)

    fun update(history: FrameHistory, size: Int, seam: Float, horizontal: Boolean, reflection: Boolean) {
        val extension = (1f - seam).coerceAtLeast(0.001f)
        val stackLength = STACK_LAYERS + LAYER_GROWTH * STACK_LAYERS * (STACK_LAYERS - 1) / 2
        val firstLength = extension / stackLength
        val tintX = if (horizontal || !history.temporal) 0.5f else history.salienceX
        var layer = 0
        var layerStart = 0f
        var layerLength = firstLength
        for (index in 0 until size) {
            val offset = ((index + 0.5f) / size - seam).coerceAtLeast(0f)
            distance[index] = offset
            delayed[index] = null
            if (reflection || offset == 0f) continue

            if (history.temporal) {
                while (offset - layerStart > layerLength && layer < STACK_LAYERS - 1) {
                    layerStart += layerLength
                    layer++
                    layerLength = firstLength * (1f + layer * LAYER_GROWTH)
                }
                center[index] = 0.5f + (tintX - 0.5f) * layer * 0.06f
                widen[index] = 1f + layer * LAYER_WIDENING
                depth[index] = 1f - ((offset - layerStart) / layerLength).coerceIn(0f, 1f) * SLICE_HEIGHT
                if (layer > 0 && history.verticalConfidence > 0f) {
                    val delayMs = layer * history.frameIntervalMs
                    delayed[index] = history.frameAt(history.live.timestampMs - delayMs)
                }
            } else {
                val progress = offset / extension
                center[index] = 0.5f
                widen[index] = 1f + progress * (STACK_LAYERS - 1) * LAYER_WIDENING
                depth[index] = 1f - progress * SLICE_HEIGHT
            }
        }
    }
}

/** A sliding box filter that transposes its result for the perpendicular pass. */
private fun blur(source: IntArray, destination: IntArray, width: Int, height: Int) {
    val kernelWidth = BOX_RADIUS * 2 + 1
    for (y in 0 until height) {
        val row = y * width
        var red = 0
        var green = 0
        var blue = 0
        for (sample in -BOX_RADIUS..BOX_RADIUS) {
            val pixel = source[row + sample.coerceIn(0, width - 1)]
            red += pixel ushr 16 and 255
            green += pixel ushr 8 and 255
            blue += pixel and 255
        }
        for (x in 0 until width) {
            destination[x * height + y] = rgb(red / kernelWidth, green / kernelWidth, blue / kernelWidth)
            val leaving = source[row + (x - BOX_RADIUS).coerceAtLeast(0)]
            val entering = source[row + (x + BOX_RADIUS + 1).coerceAtMost(width - 1)]
            red += (entering ushr 16 and 255) - (leaving ushr 16 and 255)
            green += (entering ushr 8 and 255) - (leaving ushr 8 and 255)
            blue += (entering and 255) - (leaving and 255)
        }
    }
}

private fun luma(color: Int): Int =
    ((color ushr 16 and 255) * 54 + (color ushr 8 and 255) * 183 + (color and 255) * 19) shr 8

private fun rgb(red: Int, green: Int, blue: Int): Int =
    (255 shl 24) or (red shl 16) or (green shl 8) or blue

private fun mix(base: Int, tint: Int, weight: Float): Int {
    fun channel(shift: Int): Int {
        val from = base ushr shift and 255
        return (from + ((tint ushr shift and 255) - from) * weight).roundToInt()
    }
    return rgb(channel(16), channel(8), channel(0))
}

private fun dim(color: Int, brightnessScale: Float): Int = rgb(
    ((color ushr 16 and 255) * brightnessScale).roundToInt(),
    ((color ushr 8 and 255) * brightnessScale).roundToInt(),
    ((color and 255) * brightnessScale).roundToInt(),
)
