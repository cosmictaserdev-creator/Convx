/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt
import com.convx.music.ui.component.PLAYER_CONTENT_APPEAR_START
import com.convx.music.ui.component.PLAYER_MINI_FADE_END

/**
 * Where the mini player's own pill sits on screen, in root coordinates.
 *
 * Module-level state for the same reason the artwork rects are: the pill lives in the
 * floating nav bar, the sheet lives in the player, they are far apart in the tree, and
 * only the overlay -- drawn once, in between -- reads it.
 */
private val miniContainerSlot = MorphSlot<Rect>()

internal val miniPlayerContainerRect: Rect? get() = miniContainerSlot.value

/**
 * Call on the mini player's outermost pill box. Purely observational: it records the
 * pill's bounds and changes nothing about how the pill renders. Released when the pill
 * leaves composition (keyboard up, rail layout, a screen with no bar), so the morph falls
 * back to a plain slide instead of growing out of a pill that is not there.
 */
@androidx.compose.runtime.Composable
fun Modifier.miniContainerMorphSource(): Modifier {
    val owner = androidx.compose.runtime.remember { Any() }
    androidx.compose.runtime.DisposableEffect(owner) { onDispose { miniContainerSlot.release(owner) } }
    // No recording invalidation on resize: a size change always redraws (and so re-records)
    // the pill, while an invalidation with no redraw after it left the ready flag stuck false.
    return onGloballyPositioned {
        if (it.isAttached) miniContainerSlot.report(owner, it.boundsInRoot())
    }
}

internal enum class PlayerMorphElement {
    TITLE, PREVIOUS, PLAY, NEXT;

    val mini = MorphSlot<Rect>()
    val full = MorphSlot<Rect>()
}

@Composable
internal fun Modifier.playerMorphBounds(element: PlayerMorphElement, isMini: Boolean = false): Modifier {
    val slot = if (isMini) element.mini else element.full
    val owner = remember(slot) { Any() }
    DisposableEffect(slot, owner) { onDispose { slot.release(owner) } }
    return onGloballyPositioned {
        if (it.isAttached && (isMini || !it.boundsInWindow().isEmpty)) {
            slot.report(owner, Rect(it.positionInRoot(), it.size.toSize()))
        }
    }
}

/** Mini title and buttons fade as they move toward the full player. */
fun playerSheetMiniContentAlpha(progress: Float): Float =
    com.convx.music.playback.presentation.PlayerMorphGeometry.miniContentAlpha(progress)

/** Full-player recording fades in from a tenth to six tenths, ease-in. */
fun playerSheetPageAlpha(progress: Float): Float =
    com.convx.music.playback.presentation.PlayerMorphGeometry.pageAlpha(progress)

/** Full-player background leads across the first fifth, ease-in. */
fun playerSheetBackgroundAlpha(progress: Float): Float =
    com.convx.music.playback.presentation.PlayerMorphGeometry.backgroundAlpha(progress)

/**
 * Draws the mini and full player layers in one container during the sheet morph.
 * Progress is read during drawing so every frame updates without recomposing the player.
 */
@Composable
fun PlayerContainerMorphOverlay(
    progress: () -> Float,
    color: Color,
    expandedCornerRadius: Dp,
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val clipPath = Path()
                val miniContentCutouts = Path()
                val shadowLayer = obtainGraphicsLayer()
                var flightMini: Rect? = null
                // The sheet's first draw marks it ready; avoid forcing repeated full-frame redraws.
                onDrawBehind {
                    val fraction = progress().coerceIn(0f, 1f)
                    if (fraction <= 0f || fraction >= 1f || !PlayerMorph.sharedLayersReady) {
                        flightMini = null
                        return@onDrawBehind
                    }
                    if (flightMini == null) {
                        flightMini = miniContainerSlot.value
                    }
                    val mini = flightMini ?: return@onDrawBehind
                    // onGloballyPositioned can fire once with a degenerate size before layout
                    // settles.
                    if (mini.width <= 0f || mini.height <= 0f) return@onDrawBehind
                    val miniLayer = PlayerMorph.miniLayer ?: return@onDrawBehind
                    val fullBackgroundLayer = PlayerMorph.fullBackgroundLayer ?: return@onDrawBehind
                    val fullContentLayer = PlayerMorph.fullContentLayer ?: return@onDrawBehind
                    if (fullBackgroundLayer.size.width <= 0 || fullContentLayer.size.width <= 0) return@onDrawBehind

                    // The full player is the recorded screen itself, so its rect IS this
                    // overlay's own size. Nothing to register at that end, nothing to go
                    // stale.
                    val full = Rect(0f, 0f, size.width, size.height)
                    val rect = sharedContainerRect(mini, full, fraction)
                    val radius = sharedContainerCornerRadii(
                        // The nav bar draws the pill as a 50%-rounded capsule, so its radius
                        // is half its height by definition. Derived rather than passed in, so
                        // restyling the pill cannot leave the two out of step.
                        source = SharedContainerCornerRadii.uniform(mini.height / 2f),
                        target = SharedContainerCornerRadii.uniform(expandedCornerRadius.toPx()),
                        progress = fraction,
                    ).topStart

                    // Keep the card shadow during opening; the pill supplies its own shadow on close.
                    val shadowAlpha = if (PlayerMorph.targetOpen) 1f - playerSheetBackgroundAlpha(fraction) else 0f
                    if (shadowAlpha > 0f && PlayerMorph.miniShadowElevationPx > 0f) {
                        shadowLayer.topLeft = androidx.compose.ui.unit.IntOffset(
                            rect.left.roundToInt(),
                            rect.top.roundToInt(),
                        )
                        shadowLayer.setRoundRectOutline(Offset.Zero, rect.size, radius)
                        shadowLayer.shadowElevation = PlayerMorph.miniShadowElevationPx * shadowAlpha
                        shadowLayer.record(
                            androidx.compose.ui.unit.IntSize(rect.width.roundToInt(), rect.height.roundToInt()),
                        ) { }
                        drawLayer(shadowLayer)
                    }
                    val stay = PlayerMorph.stayInPlace
                    if (stay) {
                        // Hero Blend: the bleed does not travel. The recorded player is drawn
                        // at its own place and simply fades; the pill alone morphs below.
                        if (fraction > 0f) {
                            fullBackgroundLayer.alpha = playerSheetBackgroundAlpha(fraction)
                            fullBackgroundLayer.renderEffect = null
                            drawLayer(fullBackgroundLayer)
                        }
                        if (fraction > PLAYER_CONTENT_APPEAR_START) {
                            fullContentLayer.alpha = playerSheetPageAlpha(fraction)
                            fullContentLayer.renderEffect = null
                            drawLayer(fullContentLayer)
                        }
                    }
                    clipPath.reset()
                    clipPath.addRoundRect(RoundRect(rect, CornerRadius(radius, radius)))
                    clipPath(clipPath) {
                        // No flat plate (the reference app draws no plate either): the mini
                        // recording carries its own live glass for the first
                        // fifth, and the full recording is opaque from a tenth.
                        // A plate would sit behind translucent glass and read dead.
                        translate(left = rect.left, top = rect.top) {
                            // Fade the pill surface out as the player background fades in.
                            val plateAlpha = 1f - playerSheetBackgroundAlpha(fraction)
                            if (plateAlpha > 0f) {
                                val surface = PlayerMorph.miniSurfaceLayer
                                if (surface != null && surface.size.width > 0 && surface.size.height > 0) {
                                    // The pill's real glass, stretched to the card: it distorts
                                    // as it grows, the way the reference app's mini surface does.
                                    surface.alpha = plateAlpha
                                    scale(
                                        scaleX = rect.width / surface.size.width,
                                        scaleY = rect.height / surface.size.height,
                                        pivot = Offset.Zero,
                                    ) { drawLayer(surface) }
                                } else {
                                    drawRect(color = color, size = rect.size, alpha = plateAlpha)
                                }
                            }
                            // Background leads (first fifth), content follows (tenth
                            // to six tenths): two recordings, staged like the reference app.
                            val scale = rect.width / size.width
                            if (fraction > 0f && !stay) {
                                fullBackgroundLayer.alpha = playerSheetBackgroundAlpha(fraction)
                                fullBackgroundLayer.renderEffect = null
                                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                                    drawLayer(fullBackgroundLayer)
                                }
                            }
                            if (fraction > PLAYER_CONTENT_APPEAR_START && !stay) {
                                fullContentLayer.alpha = playerSheetPageAlpha(fraction)
                                fullContentLayer.renderEffect = null
                                // Reveal the controls at their resting size as the card grows.
                                translate(left = (rect.width - fullContentLayer.size.width) * 0.5f) {
                                    drawLayer(fullContentLayer)
                                }
                            }
                            if (miniLayer.size.width > 0 && fraction < PLAYER_MINI_FADE_END) {
                                miniLayer.alpha = playerSheetMiniContentAlpha(fraction)
                                miniLayer.renderEffect = null
                                drawTrackedMiniLayer(
                                    miniLayer = miniLayer,
                                    mini = mini,
                                    container = rect,
                                    progress = fraction,
                                    cutouts = miniContentCutouts,
                                )
                            }
                        }
                    }
                }
            },
    )
}

/** Each registered mini element keeps its own path while the rest of the pill follows the card. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrackedMiniLayer(
    miniLayer: androidx.compose.ui.graphics.layer.GraphicsLayer,
    mini: Rect,
    container: Rect,
    progress: Float,
    cutouts: Path,
) {
    cutouts.reset()
    for (element in PlayerMorphElement.entries) {
        val source = element.mini.value ?: continue
        val target = element.full.value ?: continue
        if (source.isEmpty || target.isEmpty) continue
        cutouts.addRect(source.translate(-mini.topLeft))
    }
    clipPath(cutouts, clipOp = ClipOp.Difference) { drawLayer(miniLayer) }

    for (element in PlayerMorphElement.entries) {
        val source = element.mini.value ?: continue
        val target = element.full.value ?: continue
        if (source.isEmpty || target.isEmpty) continue

        val isTitle = element == PlayerMorphElement.TITLE
        val from = if (isTitle) source.topLeft else source.center
        val to = if (isTitle) target.topLeft else target.center
        val left = lerp(from.x, to.x, progress) - from.x + mini.left - container.left
        val top = lerp(from.y, to.y, progress) - from.y + mini.top - container.top
        val local = source.translate(-mini.topLeft)
        val glyphScale = if (isTitle) 1f else 1f + progress
        withTransform({
            translate(left, top)
            scale(glyphScale, glyphScale, pivot = local.center)
        }) {
            clipRect(local.left, local.top, local.right, local.bottom) { drawLayer(miniLayer) }
        }
    }
}
