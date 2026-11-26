/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import android.content.Context
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.convx.music.R
import com.convx.music.ui.component.backdrop.internal.recordLayer
import com.convx.music.ui.utils.resize
import com.convx.music.ui.utils.sharedArtworkTarget
import com.valentinilk.shimmer.shimmer
import dev.chrisbanes.haze.ExperimentalHazeApi
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Artist header whose square artwork extends into a blurred tail that blends into [pageColor]. */
@OptIn(ExperimentalHazeApi::class)
@Composable
fun ArtistHeroImage(
    artworkUrl: String?,
    pageColor: Color,
    modifier: Modifier = Modifier,
    /** Pull-to-zoom scale, read during drawing. */
    heroScale: () -> Float = { 1f },
    /** Optional resolved artwork drawn over [artworkUrl]. */
    appleArtworkUrl: String? = null,
    /** Shows a shimmer while artwork lookup is unresolved. */
    loading: Boolean = false,
    /** Scroll progress that moves the blur from the artwork bottom to the top. */
    scrollBlur: () -> Float = { 0f },
    /** Draw-phase value used to refresh the blur when a TextureView presents a frame. */
    videoFrame: () -> Int = { 0 },
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    val context = LocalContext.current
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val side = maxWidth
            Box(
                Modifier
                    .width(side)
                    .height(side * ArtistHeroExtent)
                    .graphicsLayer {
                        val zoom = heroScale()
                        scaleX = zoom
                        scaleY = zoom
                    },
            ) {
                Box(
                    Modifier
                        .matchParentSize()
                        // Blur the artwork and mirrored tail together using one per-row radius curve.
                        .heroLiveBlur(enabled = !loading, progress = scrollBlur, pageColor = pageColor, videoFrame = videoFrame),
                ) {
                Box(
                    Modifier
                        .sharedArtworkTarget()
                        .fillMaxWidth()
                        .height(side * ArtistArtworkExtent),
                ) {
                if (loading) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .shimmer()
                            .background(Color.White.copy(alpha = 0.08f)),
                    )
                } else if (!artworkUrl.isNullOrBlank()) {
                    ThumbThenFullHero(
                        thumbUrl = artworkUrl,
                        fullUrl = artworkUrl,
                        fullPx = 1200,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Keep the base artwork visible while Apple's image loads, then crossfade it over.
                    if (!appleArtworkUrl.isNullOrBlank() && !com.convx.music.ui.utils.artworkTransitionRunning()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(appleArtworkUrl)
                                .diskCacheKey(appleArtworkUrl)
                                .crossfade(600)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    overlay()
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(0f to Color(0xFF3A3A3C), 1f to pageColor)),
                    )
                    Image(
                        painter = painterResource(R.drawable.music_note),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize(0.3f)
                            .align(Alignment.Center)
                            .graphicsLayer { alpha = 0.3f },
                    )
                }
            }
            }
            // On older devices the same colour ramp still closes the join. On API 33+
            // the live blur composites this ramp itself, in the same colour space as
            // its image/video input, and reaches the exact page colour before its edge.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || loading) {
                Box(
                    Modifier
                        .matchParentSize()
                        .background(Brush.verticalGradient(colorStops = artistWashStops(pageColor))),
                )
            }
        }
    }
}

/** Full-width artist photo for tab view: the picture fills the box and melts into [pageColor] at its foot. */
@Composable
fun ArtistWideHero(
    artworkUrl: String?,
    pageColor: Color,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier) {
        if (!artworkUrl.isNullOrBlank()) {
            ThumbThenFullHero(
                thumbUrl = artworkUrl,
                fullUrl = artworkUrl,
                fullPx = 1600,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            overlay()
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0f to Color(0xFF3A3A3C), 1f to pageColor)),
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = Array(WideWashSteps + 1) { i ->
                            val position = i / WideWashSteps.toFloat()
                            position to pageColor.copy(alpha = smoothstep(WideWashStart, 1f, position))
                        },
                    ),
                ),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent))),
        )
    }
}

private const val WideWashSteps = 32
private const val WideWashStart = 0.55f

/** Draws a cached progressive blur under a player artwork box. */
internal val playerFootCache = android.util.LruCache<String, ImageBitmap>(6)

@Composable
fun BakedFootBlur(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
    fadeStart: Float = 0.50f,
    fadeEnd: Float = 0.85f,
) {
    if (artworkUrl.isNullOrBlank()) return
    val context = LocalContext.current
    val foot by produceState(playerFootCache.get(artworkUrl), artworkUrl) {
        if (value != null) return@produceState
        value = withContext(Dispatchers.Default) {
            runCatching { bakeArtistFoot(context, artworkUrl, extent = 1f) }.getOrNull()
                ?.also { playerFootCache.put(artworkUrl, it) }
        }
    }
    // Keep the blur aligned with the art and avoid a continuous animation when the player is idle.
    foot?.let { baked ->
        Image(
            bitmap = baked,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.heroBandMask(fadeStart, fadeEnd),
        )
    }
}

/** Hero column height as a multiple of its width: the square photo plus the smeared tail below it. */
const val ArtistHeroExtent = 495f / 393f
internal const val ArtistArtworkExtent = 405f / 393f

/** Smoothly blends the artwork tail into the page color. */
fun artistWash(fraction: Float): Float = smoothstep(307f / 393f, 1.21f, fraction)

internal fun artistWashStops(pageColor: Color): Array<Pair<Float, Color>> {
    val steps = 32
    return Array(steps + 1) { i ->
        val position = i / steps.toFloat()
        position to pageColor.copy(alpha = artistWash(position * ArtistHeroExtent))
    }
}

internal fun smoothstep(edge0: Float, edge1: Float, x: Float): Float {
    val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

/** Kept for the preview's header offset math. */
const val ArtistBlendStart = 0.85f

/** Returns the progressive blur radius at a normalized height in the hero. */
fun artistFootBlurPt(fraction: Float): Float =
    if (fraction <= ArtistBlurKnee) 0f else minOf(48f, 207f * (fraction - ArtistBlurKnee).pow(1.855f))

internal const val ArtistBlurKnee = 0.78f

// Keep the source large enough to avoid visible upscaling artifacts near the blur knee.
internal const val ArtistFootPx = 320
internal val ArtistFootLevelsPt = floatArrayOf(0f, 2f, 4f, 8f, 16f, 32f, 48f)
internal val artistFootCache = android.util.LruCache<String, ImageBitmap>(6)

/** Stack blur radius whose triangle kernel has standard deviation [sigmaPx] (variance r(r+2)/6). */
internal fun stackRadiusFor(sigmaPx: Float): Int =
    (-1.0 + kotlin.math.sqrt(1.0 + 6.0 * sigmaPx * sigmaPx)).roundToInt()

/** Builds the artwork's mirrored, progressively blurred tail and caches it by [extent]. */
private suspend fun bakeArtistFoot(context: Context, url: String, extent: Float = ArtistHeroExtent): ImageBitmap? {
    // Reuse the full-resolution image request so Coil can share its fetch and decode.
    val heroFullUrl = url.resize(1200, 1200)
    val request = ImageRequest.Builder(context)
        .data(heroFullUrl)
        .diskCacheKey(heroFullUrl)
        .size(1200)
        .allowHardware(false)
        .build()
    val decoded = (coil3.SingletonImageLoader.get(context).execute(request) as? coil3.request.SuccessResult)
        ?.image?.toBitmap() ?: return null
    val w = ArtistFootPx
    val h = (w * extent).roundToInt().coerceAtLeast(w)
    val crop = minOf(decoded.width, decoded.height)
    val square = android.graphics.Bitmap.createBitmap(
        decoded, (decoded.width - crop) / 2, (decoded.height - crop) / 2, crop, crop,
    )
    val scaled = android.graphics.Bitmap.createScaledBitmap(square, w, w, true)
    val base = IntArray(w * h)
    scaled.getPixels(base, 0, w, 0, 0, w, w)
    for (y in w until h) System.arraycopy(base, (w - 1) * w, base, y * w, w)

    val pxPerPt = w / 402f
    val levels = Array(ArtistFootLevelsPt.size) { i ->
        val sigma = ArtistFootLevelsPt[i]
        if (sigma == 0f) base else base.copyOf().also {
            com.convx.music.ui.utils.stackBlurPixels(it, w, h, stackRadiusFor(sigma * pxPerPt).coerceAtLeast(1))
        }
    }
    val out = IntArray(w * h)
    val squash = ArtistHeroExtent / extent
    for (y in 0 until h) {
        val sigma = artistFootBlurPt(y.toFloat() / w * squash)
        var hi = ArtistFootLevelsPt.indexOfFirst { it >= sigma }
        if (hi < 0) hi = ArtistFootLevelsPt.lastIndex
        val lo = maxOf(hi - 1, 0)
        val t = if (hi == lo) 1f else ((sigma - ArtistFootLevelsPt[lo]) / (ArtistFootLevelsPt[hi] - ArtistFootLevelsPt[lo])).coerceIn(0f, 1f)
        val a = levels[lo]
        val b = levels[hi]
        val row = y * w
        for (x in 0 until w) {
            val ca = a[row + x]
            val cb = b[row + x]
            val r = (((ca shr 16) and 0xFF) * (1 - t) + ((cb shr 16) and 0xFF) * t).roundToInt()
            val g = (((ca shr 8) and 0xFF) * (1 - t) + ((cb shr 8) and 0xFF) * t).roundToInt()
            val bl = ((ca and 0xFF) * (1 - t) + (cb and 0xFF) * t).roundToInt()
            out[row + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or bl
        }
    }
    return android.graphics.Bitmap.createBitmap(out, w, h, android.graphics.Bitmap.Config.ARGB_8888)
        .asImageBitmap()
}

/** Vertical alpha ramp with smooth transitions at [start] and [end]. */
internal fun smoothRamp(start: Float, end: Float): Brush {
    val steps = 12
    return Brush.verticalGradient(
        colorStops = Array(steps + 2) { i ->
            when (i) {
                0 -> 0f to Color.Transparent
                steps + 1 -> 1f to Color.Black
                else -> {
                    val t = (i - 1) / (steps - 1f)
                    val eased = t * t * (3f - 2f * t)
                    (start + (end - start) * t).coerceIn(0f, 1f) to Color.Black.copy(alpha = eased)
                }
            }
        },
    )
}

/** Fades player hero artwork into its background with a smooth horizontal ramp. */
fun heroFadeOutBrush(start: Float = 0.78f, end: Float = 1f, steps: Int = 24): Brush {
    return Brush.verticalGradient(colorStops = heroFadeOutStops(start, end, steps).toTypedArray())
}

internal fun heroFadeOutStops(start: Float = 0.78f, end: Float = 1f, steps: Int = 24): List<Pair<Float, Color>> {
    require(steps > 0 && start >= 0f && end <= 1f && start < end)
    val stops = ArrayList<Pair<Float, Color>>(steps + 2)
    stops.add(0f to Color.Black)
    stops.add(start to Color.Black)
    for (i in 0..steps) {
        val t = i / steps.toFloat()
        val eased = t * t * (3f - 2f * t)
        stops.add((start + (end - start) * t) to Color.Black.copy(alpha = 1f - eased))
    }
    return stops
}

/** Fades this layer in over the vertical range [start] to [end]. */
internal fun Modifier.heroBandMask(start: Float, end: Float): Modifier =
    graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        // Cache the gradient until size or ramp bounds change.
        .drawWithCache {
            val brush = smoothRamp(start, end)
            onDrawWithContent {
                drawContent()
                drawRect(brush = brush, blendMode = BlendMode.DstIn)
            }
        }

/** Combines the resting blur with the scroll-driven blur at a normalized height. */
fun artistHeroBlurPt(fraction: Float, progress: Float): Float {
    val p = progress.coerceIn(0f, 1f)
    val riseStart = 1.1f + (-0.6f - 1.1f) * p
    val riseEnd = 1.3f + (-0.05f - 1.3f) * p
    return maxOf(artistFootBlurPt(fraction), ArtistScrollBlurPt * smoothstep(riseStart, riseEnd, fraction))
}

/** Draws a quarter-resolution, per-row blur over artwork or video without recomposition. */
internal fun Modifier.heroLiveBlur(
    enabled: Boolean,
    progress: () -> Float,
    sideFromHeight: Float = 0f,
    pageColor: Color? = null,
    videoFrame: () -> Int = { 0 },
    downsample: Int = HeroBlurDown,
): Modifier =
    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this then HeroBlurElement(progress, sideFromHeight, pageColor, videoFrame, downsample)
    } else {
        this
    }

/** Applies progressive blur to a full artwork box without adding a mirrored tail. */
@Composable
fun Modifier.heroFootLiveBlur(boxExtent: Float, videoFrame: () -> Int = { 0 }, rapidChanges: Boolean = false): Modifier {
    val (quality) = com.convx.music.utils.rememberEnumPreference(
        com.convx.music.constants.CanvasBlurQualityKey, com.convx.music.canvas.CanvasBlurQuality.HIGH)
    return heroLiveBlur(enabled = true, progress = { 0f }, sideFromHeight = boxExtent,
        videoFrame = videoFrame, downsample = if (rapidChanges) com.convx.music.canvas.CanvasBlurQuality.LOW.downsample else quality.downsample)
}

internal data class HeroBlurElement(
    val progress: () -> Float,
    val sideFromHeight: Float,
    val pageColor: Color?,
    val videoFrame: () -> Int,
    val downsample: Int,
) : androidx.compose.ui.node.ModifierNodeElement<HeroBlurNode>() {
    override fun create(): HeroBlurNode = HeroBlurNode(progress, sideFromHeight, pageColor, videoFrame, downsample)
    override fun update(node: HeroBlurNode) {
        node.progress = progress
        node.sideFromHeight = sideFromHeight
        node.pageColor = pageColor
        node.videoFrame = videoFrame
        node.downsample = downsample
    }
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class HeroBlurNode(
    var progress: () -> Float,
    /** > 0: the curve's reference side is height / this and nothing is mirrored (player box). */
    var sideFromHeight: Float,
    var pageColor: Color?,
    var videoFrame: () -> Int,
    var downsample: Int,
) : Modifier.Node(), androidx.compose.ui.node.DrawModifierNode {
    private var sharp: androidx.compose.ui.graphics.layer.GraphicsLayer? = null
    private var small: androidx.compose.ui.graphics.layer.GraphicsLayer? = null
    private val horizontal = android.graphics.RuntimeShader(HeroBlurShader.replace("AXIS", "float2(1.0, 0.0)"))
    private val vertical = android.graphics.RuntimeShader(HeroBlurShader.replace("AXIS", "float2(0.0, 1.0)"))
    private var effectKey: HeroBlurEffectKey? = null

    private data class HeroBlurEffectKey(
        val width: Int,
        val height: Int,
        val side: Float,
        val mirrorAt: Float,
        val progress: Float,
        val pageArgb: Int?,
    )

    override fun onAttach() {
        val context = requireGraphicsContext()
        sharp = context.createGraphicsLayer()
        small = context.createGraphicsLayer()
    }

    override fun onDetach() {
        val context = requireGraphicsContext()
        sharp?.let { context.releaseGraphicsLayer(it) }
        small?.let { context.releaseGraphicsLayer(it) }
        sharp = null
        small = null
        effectKey = null
    }

    override fun ContentDrawScope.draw() {
        // Register the frame dependency even when geometry and blur uniforms stay fixed.
        // TextureView updates do not otherwise reliably invalidate captured Compose layers.
        videoFrame()
        val sharp = sharp
        val small = small
        val size = size.toIntSize()
        if (sharp == null || small == null || size.width == 0 || size.height == 0) {
            drawContent()
            return
        }
        val factor = downsample.coerceIn(2, 4)
        val sw = (size.width + factor - 1) / factor
        val sh = (size.height + factor - 1) / factor
        val sx = sw / size.width.toFloat()
        val sy = sh / size.height.toFloat()
        // Re-recorded every draw: both are display lists (cheap), and this node only draws when
        // its own layer is invalidated -- scroll progress, a new image, the video fading in.
        recordLayer(this@HeroBlurNode, sharp, size) { this@draw.drawContent() }
        recordLayer(this@HeroBlurNode, small, androidx.compose.ui.unit.IntSize(sw, sh)) {
            scale(sx, sy, pivot = androidx.compose.ui.geometry.Offset.Zero) { drawLayer(sharp) }
        }
        val side = if (sideFromHeight > 0f) sh / sideFromHeight else size.width * sy
        val mirrorAt = if (sideFromHeight > 0f) sh.toFloat() else side * ArtistArtworkExtent
        val pxPerPt = sw / 402f
        val p = progress().coerceIn(0f, 1f)
        val nextKey = HeroBlurEffectKey(sw, sh, side, mirrorAt, p, pageColor?.toArgb())
        if (effectKey != nextKey) {
            fun configure(shader: android.graphics.RuntimeShader) {
                shader.setFloatUniform("size", sw.toFloat(), sh.toFloat())
                shader.setFloatUniform("side", side)
                shader.setFloatUniform("mirrorAt", mirrorAt)
                shader.setFloatUniform("pxPerPt", pxPerPt)
                shader.setFloatUniform("progress", p)
                shader.setFloatUniform("washEnabled", if (pageColor != null) 1f else 0f)
                shader.setColorUniform("pageColor", nextKey.pageArgb ?: android.graphics.Color.TRANSPARENT)
            }
            configure(horizontal)
            configure(vertical)
            // RuntimeShader effects snapshot uniforms. Rebuild when the ramp changes,
            // but reuse the chain for live video frames with unchanged uniforms.
            small.renderEffect = android.graphics.RenderEffect.createChainEffect(
                android.graphics.RenderEffect.createRuntimeShaderEffect(vertical, "content"),
                android.graphics.RenderEffect.createRuntimeShaderEffect(horizontal, "content"),
            ).asComposeRenderEffect()
            effectKey = nextKey
        }
        drawLayer(sharp)
        scale(1f / sx, 1f / sy, pivot = androidx.compose.ui.geometry.Offset.Zero) { drawLayer(small) }
    }
}

/** Blur layer downscale. At 1/4 the 48pt peak is ~32px of radius: 81 taps covers ±3σ. */
internal const val HeroBlurDown = 4

/** Blur strength of the scroll climb once it has covered the whole photo. */
internal const val ArtistScrollBlurPt = 28f

/**
 * One separable pass of the variable gaussian; AXIS is substituted per pass. The radius curve
 * follows [artistHeroBlurPt]. Rows below the square sample it mirrored (Apple's extension).
 * The vertical pass fades the copy in over the first 1.5px of radius and adds ±0.5/255
 * dither so the dark tail doesn't band.
 */
internal const val HeroBlurShader = """
uniform shader content;
uniform float2 size;
uniform float side;
uniform float mirrorAt;
uniform float pxPerPt;
uniform float progress;
uniform float washEnabled;
layout(color) uniform half4 pageColor;

float ease(float a, float b, float x) {
    float t = clamp((x - a) / (b - a), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

float sigmaAt(float y) {
    float fr = y / side;
    float k = max(fr - 0.78, 0.0);
    float foot = fr <= 0.78 ? 0.0 : min(48.0, 207.0 * pow(k, 1.855));
    float rs = mix(1.1, -0.6, progress);
    float re = mix(1.3, -0.05, progress);
    float climb = 28.0 * ease(rs, re, fr);
    return max(foot, climb) * pxPerPt;
}

half4 main(float2 c) {
    float2 axis = AXIS;
    float s = sigmaAt(c.y);
    // The horizontal pass mirrors the square below its foot; the vertical pass reads
    // that output, which already fills the tail.
    float my = c.y < mirrorAt ? c.y : max(2.0 * mirrorAt - c.y, 0.5);
    float2 base = axis.x > 0.5 ? float2(c.x, min(my, mirrorAt - 0.5)) : c;
    if (s < 0.05) {
        if (axis.x > 0.5) return content.eval(base);
        return pageColor * half(washEnabled * ease(307.0 / 393.0, 1.21, c.y / side));
    }
    float stepPx = max(1.0, s * 3.0 / 40.0);
    float4 acc = float4(0.0);
    float wsum = 0.0;
    for (int i = -40; i <= 40; i++) {
        float d = float(i) * stepPx;
        float w = exp(-0.5 * d * d / (s * s));
        float2 q = clamp(base + axis * d, float2(0.5), size - 0.5);
        acc += float4(content.eval(q)) * w;
        wsum += w;
    }
    float4 col = acc / wsum;
    if (axis.y > 0.5) {
        float n = fract(sin(dot(c, float2(12.9898, 78.233))) * 43758.5453) - 0.5;
        col.rgb += n / 255.0;
        col *= ease(0.0, 1.5, s);
        // Premultiplied compositing closes on the SAME page colour at every x.
        // No independent tinted rectangle ending underneath the release card.
        float wash = washEnabled * ease(307.0 / 393.0, 1.21, c.y / side);
        col = col * (1.0 - wash) + float4(pageColor) * wash;
    }
    return half4(col);
}
"""

/**
 * Wide Hero Blend's right edge: the portrait foot's blur-then-colour fade turned on its side.
 * The art is drawn blurred underneath and the sharp art over it, masked out toward the right,
 * so the edge melts into blur; the caller's outer horizontal fade then hands the blur to the
 * art-tinted page colour. Display lists only, re-recorded when the art redraws. API 31+ for
 * the blur; older devices keep the sharp art and the outer fade.
 */
@Composable
fun Modifier.heroSideLiveBlur(videoFrame: () -> Int = { 0 }, rapidChanges: Boolean = false): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return this
    val (quality) = com.convx.music.utils.rememberEnumPreference(
        com.convx.music.constants.CanvasBlurQualityKey, com.convx.music.canvas.CanvasBlurQuality.HIGH)
    val factor = if (rapidChanges) com.convx.music.canvas.CanvasBlurQuality.LOW.downsample else quality.downsample
    val base = androidx.compose.ui.graphics.rememberGraphicsLayer()
    val blurred = androidx.compose.ui.graphics.rememberGraphicsLayer()
    val sharp = androidx.compose.ui.graphics.rememberGraphicsLayer()
    val radiusPx = with(androidx.compose.ui.platform.LocalDensity.current) { 40.dp.toPx() } / factor
    val blurEffect = remember(radiusPx) {
        androidx.compose.ui.graphics.BlurEffect(radiusPx, radiusPx, androidx.compose.ui.graphics.TileMode.Clamp)
    }
    val mask = remember {
        Brush.horizontalGradient(colorStops = heroFadeOutStops(0.42f, 0.86f).toTypedArray())
    }
    return drawWithContent {
        videoFrame()
        base.record { this@drawWithContent.drawContent() }
        blurred.renderEffect = blurEffect
        val full = size.toIntSize()
        if (full.width <= 0 || full.height <= 0) return@drawWithContent
        val reduced = androidx.compose.ui.unit.IntSize((full.width + factor - 1) / factor, (full.height + factor - 1) / factor)
        val sx = reduced.width / size.width
        val sy = reduced.height / size.height
        blurred.record(size = reduced) {
            scale(sx, sy, pivot = androidx.compose.ui.geometry.Offset.Zero) { drawLayer(base) }
        }
        sharp.compositingStrategy = androidx.compose.ui.graphics.layer.CompositingStrategy.Offscreen
        sharp.record {
            drawLayer(base)
            drawRect(mask, blendMode = androidx.compose.ui.graphics.BlendMode.DstIn)
        }
        scale(1f / sx, 1f / sy, pivot = androidx.compose.ui.geometry.Offset.Zero) { drawLayer(blurred) }
        drawLayer(sharp)
    }
}
