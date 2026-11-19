/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size as CoilSize
import com.convx.music.designsystem.PlayerMotionSpec
import com.convx.music.playback.presentation.PlayerMorphGeometry
import com.convx.music.ui.component.heroFadeOutStops
import com.convx.music.ui.component.rememberNoArtworkPainter
import com.convx.music.ui.utils.resize
import kotlin.math.roundToInt

internal enum class HeroArtworkFade { BOTTOM, END }

/** One measured endpoint of the artwork flight. */
internal data class MorphArtTarget(
    val rect: Rect,
    val radiusPx: Float,
    val heroFade: HeroArtworkFade? = null,
    val shadowPx: Float = 0f,
    /** V2 Squared's pause-shrink applies to this target's painted size. */
    val pauseShrinks: Boolean = false,
    /** A separate album-cover thumbnail must not dissolve into the hero's motion-video poster. */
    val videoPoster: Boolean = true,
    val fadeEndpoints: Boolean = false,
)

/**
 * A registration slot several live elements can report into (bar inline + expanded
 * accessories mid-transition, V2 cover + lyrics header mid-transition, pager pages).
 *
 * The latest report wins. When an element leaves composition, the slot restores the most
 * recent remaining report or clears itself when no elements remain.
 */
internal class MorphSlot<T : Any> {
    private val owners = LinkedHashMap<Any, T>()
    private val state = mutableStateOf<T?>(null)
    val value: T? get() = state.value

    fun report(owner: Any, value: T) {
        owners.remove(owner)
        owners[owner] = value
        state.value = value
    }

    fun release(owner: Any) {
        owners.remove(owner) ?: return
        state.value = owners.values.lastOrNull()
    }
}

internal val miniArtworkSlot = MorphSlot<MorphArtTarget>()
internal val fullArtworkSlot = MorphSlot<MorphArtTarget>()

/**
 * Q6 lock: V2's pause-shrink (0.729x), live at rest, frozen in flight.
 *
 * Snapshot state (not a plain field): the overlay reads it in composition and
 * must see the settled pause value the moment a flight starts. Written only from a
 * snapshotFlow collector; never written while [PlayerMorph.active].
 */
internal val artworkRestScaleState = androidx.compose.runtime.mutableFloatStateOf(1f)

/** Source end: the mini player's own cover. Released when the pill leaves composition. */
@Composable
fun Modifier.miniArtworkMorphSource(cornerRadiusPx: Float): Modifier {
    val owner = remember { Any() }
    DisposableEffect(owner) { onDispose { miniArtworkSlot.release(owner) } }
    return this
        .onGloballyPositioned {
            if (it.isAttached) miniArtworkSlot.report(owner, MorphArtTarget(it.boundsInRoot(), cornerRadiusPx))
        }
        .graphicsLayer {
            alpha = if (!PlayerMorph.active) 1f else when (fullArtworkSlot.value?.fadeEndpoints) {
                null -> 1f
                true -> miniArtworkEndpointAlpha(PlayerMorph.progress)
                false -> 0f
            }
        }
}

/**
 * Target end: a full player's cover or lyrics/queue header. The overlay carries the cover
 * while the target hides or crossfades into its fully rendered shape.
 */
@Composable
internal fun Modifier.fullArtworkMorphTarget(
    cornerRadiusPx: Float,
    heroFade: HeroArtworkFade? = null,
    shadowPx: Float = 0f,
    pauseShrinks: Boolean = false,
    videoPoster: Boolean = true,
    fadeEndpoints: Boolean = false,
): Modifier {
    val owner = remember { Any() }
    DisposableEffect(owner) { onDispose { fullArtworkSlot.release(owner) } }
    return this
        .onGloballyPositioned {
            // Off-window reports are ignored, not released: the resident player sits a full
            // sheet height below the screen while collapsed, and its last on-screen rect is
            // the one the next open has to fly to. Pager neighbours stay silent the same way.
            if (!it.isAttached || it.boundsInWindow().isEmpty) return@onGloballyPositioned
            fullArtworkSlot.report(
                owner,
                MorphArtTarget(
                    rect = Rect(it.positionInRoot(), it.size.toSize()),
                    radiusPx = cornerRadiusPx,
                    heroFade = heroFade,
                    shadowPx = shadowPx,
                    pauseShrinks = pauseShrinks,
                    videoPoster = videoPoster,
                    fadeEndpoints = fadeEndpoints,
                ),
            )
        }
        .graphicsLayer {
            alpha = if (!PlayerMorph.active) {
                1f
            } else if (fadeEndpoints) {
                fullArtworkEndpointAlpha(PlayerMorph.progress)
            } else {
                0f
            }
        }
}

/**
 * Draws ONE artwork growing from the mini cover to the full player's cover as
 * [progress] runs 0..1.
 *
 * The image is laid out once as a square of side max(target w, h) centred on the
 * target -- exactly what ContentScale.Crop shows at the target -- and scaled down, never
 * up, so it stays sharp. A rounded clip window then interpolates per axis from the mini
 * rect to the target rect, which is what lets a square pill cover
 * open into V2's tall Hero and land on its true shape, and a square card land exactly on
 * itself. Both ends are frozen for the flight (the reference app freezeArtworkBounds).
 */
@Composable
fun PlayerArtworkMorphOverlay(
    thumbnailUrl: String?,
    mediaId: String?,
    progress: () -> Float,
) {
    val flying = PlayerMorph.active
    // Observed only in flight. At rest the mini cover's rect moves on every frame of the
    // bar's inline/expand morph (and every pill stretch), and an observed read here
    // recomposed this overlay on each of those frames -- scroll-triggered jank. The
    // `flying` flip recomposes anyway, so the flight still starts from fresh rects.
    val (liveMini, liveFull) = if (flying) {
        miniArtworkSlot.value to fullArtworkSlot.value
    } else {
        androidx.compose.runtime.snapshots.Snapshot.withoutReadObservation {
            miniArtworkSlot.value to fullArtworkSlot.value
        }
    }
    val frozen = remember(flying) { if (flying) liveMini to liveFull else null }
    // Canvas video on the target: one still of the frame on screen, grabbed in the first
    // flight frame's composition (before its draw). Collapsed, the loop is paused, so this
    // is also the exact frame an open lands on.
    val mini = frozen?.first ?: liveMini
    val full = frozen?.second ?: liveFull
    if (mini == null || full == null) return
    val poster = remember(flying, mediaId, full.videoPoster) {
        if (flying && full.videoPoster) CanvasPosterSource.capture(mediaId) else null
    }
    // onGloballyPositioned can fire once with a degenerate size before layout settles, and
    // coil3.size.Dimension throws on any px <= 0 (measured crash, Samsung).
    if (mini.rect.width <= 0f || mini.rect.height <= 0f || full.rect.width <= 0f || full.rect.height <= 0f) return

    val shrink = if (full.pauseShrinks) artworkRestScaleState.floatValue.coerceIn(0.1f, 1f) else 1f
    val target = full.rect.scaledAboutCenter(shrink)
    val side = maxOf(target.width, target.height)
    val density = LocalDensity.current
    val heroFade = full.heroFade
    val fadeStops = remember(heroFade) {
        heroFade?.let {
            heroFadeOutStops(start = if (it == HeroArtworkFade.END) 0.6f else 0.78f)
                .map { (position, color) -> position to Color.Black.copy(alpha = 1f - color.alpha) }
                .toTypedArray()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .layout { measurable, constraints ->
                // A tall Hero crop needs a square drawing layer wider than the viewport.
                // Measure it at its real extent so its transform origin stays at the cover center.
                val extent = side.roundToInt().coerceAtLeast(1)
                val layer = measurable.measure(Constraints.fixed(extent, extent))
                layout(constraints.constrainWidth(extent), constraints.constrainHeight(extent)) {
                    layer.place(
                        (target.center.x - extent / 2f).roundToInt(),
                        (target.center.y - extent / 2f).roundToInt(),
                    )
                }
            }
            .graphicsLayer {
                val fraction = progress().coerceIn(0f, 1f)
                if (fraction <= 0f || fraction >= 1f || !PlayerMorph.active) {
                    alpha = 0f
                    shadowElevation = 0f
                    return@graphicsLayer
                }
                alpha = if (full.fadeEndpoints) flyingArtworkEndpointAlpha(fraction) else 1f
                val edgeToEdge = heroFade != null
                val window = PlayerMorphGeometry.artworkWindow(mini.rect, target, fraction, edgeToEdge)
                val growth = PlayerMorphGeometry.artworkGrowth(fraction, edgeToEdge)
                // Image square side at this instant: covers the window at every fraction
                // (lerp of the max >= max of the lerps), full image at 0, Crop at 1.
                val imageSide = lerp(maxOf(mini.rect.width, mini.rect.height), side, growth)
                val scale = imageSide / size.width
                transformOrigin = TransformOrigin.Center
                scaleX = scale
                scaleY = scale
                translationX = window.center.x - target.center.x
                translationY = window.center.y - target.center.y
                val radius = lerp(mini.radiusPx, full.radiusPx * shrink, growth)
                shape = CenteredWindowShape(
                    windowWidth = window.width / scale,
                    windowHeight = window.height / scale,
                    radius = radius / scale,
                )
                clip = true
                shadowElevation = full.shadowPx * growth
                compositingStrategy = if (heroFade != null) CompositingStrategy.Offscreen else CompositingStrategy.Auto
            }
            .then(
                if (heroFade != null && fadeStops != null) {
                    Modifier.drawWithCache {
                        // Use the full player's mask within its crop window in this layer.
                        val mask = if (heroFade == HeroArtworkFade.END) {
                            val left = (size.width - target.width) / 2f
                            Brush.horizontalGradient(colorStops = fadeStops, startX = left, endX = left + target.width)
                        } else {
                            val top = (size.height - target.height) / 2f
                            Brush.verticalGradient(colorStops = fadeStops, startY = top, endY = top + target.height)
                        }
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = mask,
                                alpha = sanitizedProgress(progress()),
                                blendMode = BlendMode.DstOut,
                            )
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        val artwork = thumbnailUrl?.resize(1200, 1200)
        val placeholder = rememberNoArtworkPainter()
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                // Same URL AND decode size as every real player artwork -- Coil serves the
                // identical cached bitmap at the handoff, no second decode, no landing pop.
                .data(artwork)
                .size(CoilSize.ORIGINAL)
                .placeholderMemoryCacheKey(artwork)
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            modifier = Modifier.matchParentSize(),
        )
        if (poster != null) {
            val targetWidthDp = with(density) { target.width.toDp() }
            val targetHeightDp = with(density) { target.height.toDp() }
            val posterBitmap = remember(poster) { poster.asImageBitmap() }
            Image(
                bitmap = posterBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(targetWidthDp, targetHeightDp)
                    .graphicsLayer { alpha = posterAlpha(progress()) },
            )
        }
    }
}

/**
 * Still -> video dissolve across the back half of the flight, smoothstepped so neither end
 * is visible. The same function both ways: an open ends on the video, a close starts on it.
 */
internal fun posterAlpha(progress: Float): Float = PlayerMotionSpec.posterAlpha(sanitizedProgress(progress))

internal fun miniArtworkEndpointAlpha(progress: Float): Float =
    PlayerMotionSpec.miniArtworkAlpha(sanitizedProgress(progress))

internal fun fullArtworkEndpointAlpha(progress: Float): Float =
    PlayerMotionSpec.fullArtworkAlpha(sanitizedProgress(progress))

internal fun flyingArtworkEndpointAlpha(progress: Float): Float =
    (1f - miniArtworkEndpointAlpha(progress) - fullArtworkEndpointAlpha(progress)).coerceIn(0f, 1f)

private fun Rect.scaledAboutCenter(factor: Float): Rect =
    if (factor >= 1f) this else Rect(
        left = center.x - width * factor / 2f,
        top = center.y - height * factor / 2f,
        right = center.x + width * factor / 2f,
        bottom = center.y + height * factor / 2f,
    )

/** Rounded window of the given size centred in the layer (layer-local px). */
private class CenteredWindowShape(
    private val windowWidth: Float,
    private val windowHeight: Float,
    private val radius: Float,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val left = (size.width - windowWidth) / 2f
        val top = (size.height - windowHeight) / 2f
        val r = radius.coerceIn(0f, minOf(windowWidth, windowHeight) / 2f)
        return Outline.Rounded(
            RoundRect(left, top, left + windowWidth, top + windowHeight, CornerRadius(r, r)),
        )
    }
}

fun Modifier.hideWhileArtworkMorphing(progress: () -> Float): Modifier = graphicsLayer {
    // Lambda, not a Float: read in the draw phase so the V2 subtree does not recompose per
    // frame of every open/close drag ("measure is called on a deactivated node").
    val fraction = progress()
    alpha = if (fraction > 0f && fraction < 1f && PlayerMorph.active) 0f else 1f
}
