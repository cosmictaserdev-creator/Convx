/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt
import com.convx.music.ui.component.PLAYER_CONTENT_APPEAR_END
import com.convx.music.ui.component.PLAYER_CONTENT_APPEAR_START
import com.convx.music.ui.component.PLAYER_LAYER_HANDOFF_PROGRESS
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

/**
 * Mini transport cluster, in root coordinates (the reference app miniPlayerControlsBounds).
 *
 * The overlay draws the mini recording twice -- title side riding the
 * travelling corner, controls shifted to keep their right inset -- so both
 * visibly travel instead of crossfading. Controls report per-button and read
 * back the union: variants mount exclusively, so at most one set is live.
 */
private val miniControlsPlayRect = mutableStateOf<Rect?>(null)
private val miniControlsNextRect = mutableStateOf<Rect?>(null)

internal val miniPlayerControlsRect: Rect?
    get() {
        val play = miniControlsPlayRect.value
        val next = miniControlsNextRect.value
        if (play == null) return next
        if (next == null) return play
        return Rect(
            left = minOf(play.left, next.left),
            top = minOf(play.top, next.top),
            right = maxOf(play.right, next.right),
            bottom = maxOf(play.bottom, next.bottom),
        )
    }

/** Clears stale variant bounds when the mini switches layout (tab style, inline). */
internal fun clearMiniContentControlsRects() {
    miniControlsPlayRect.value = null
    miniControlsNextRect.value = null
}

fun Modifier.registerMiniControlsPlayRect(): Modifier = onGloballyPositioned {
    if (it.isAttached) miniControlsPlayRect.value = it.boundsInRoot()
}

fun Modifier.registerMiniControlsNextRect(): Modifier = onGloballyPositioned {
    if (it.isAttached) miniControlsNextRect.value = it.boundsInRoot()
}

/** Mini title/buttons track across the first three tenths, then hand over. */
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
 * The mini title stays anchored while its controls travel; the full player scales in as
 * the mini hands off. Progress is read during drawing so every frame updates without
 * recomposing the player.
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
                val shadowLayer = obtainGraphicsLayer()
                // Both pill-side rects frozen for the flight, like the artwork's ends: the
                // pill's own stretch and the bar's inline/expanded resize move the live rect
                // mid-gesture, which walked the card's source edge away from the flying cover.
                var flightMini: Rect? = null
                var flightControls: Rect? = null
                // The sheet's first draw marks it ready; avoid forcing repeated full-frame redraws.
                onDrawBehind {
                    val fraction = progress().coerceIn(0f, 1f)
                    // Close landing: the sheet has stopped on its bound, the card has not.
                    val landing = fraction <= 0f && PlayerMorph.landingActive
                    if ((fraction <= 0f && !landing) || fraction >= 1f || !PlayerMorph.sharedLayersReady) {
                        flightMini = null
                        flightControls = null
                        return@onDrawBehind
                    }
                    if (flightMini == null) {
                        flightMini = miniContainerSlot.value
                        flightControls = miniPlayerControlsRect
                    }
                    // Opening: the frozen source (the pill stretches under the finger). Closing:
                    // the LIVE pill, so the card lands exactly on the size the pill has now -- a
                    // frozen rect from flight start landed bigger, then snapped to size.
                    val mini = (if (PlayerMorph.targetOpen) flightMini else miniContainerSlot.value ?: flightMini)
                        ?: return@onDrawBehind
                    val controlsNow = if (PlayerMorph.targetOpen) flightControls else miniPlayerControlsRect ?: flightControls
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
                    val rect = if (landing) landingCardRect(mini) else sharedContainerRect(mini, full, fraction)
                    val radius = if (landing) rect.height / 2f else sharedContainerCornerRadii(
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
                            if (miniLayer.size.width > 0 && fraction < PLAYER_MINI_FADE_END) {
                                miniLayer.alpha = playerSheetMiniContentAlpha(fraction)
                                miniLayer.renderEffect = null
                                if (landing) {
                                    // The whole pill squashes as one body while it lands.
                                    scale(
                                        scaleX = rect.width / miniLayer.size.width,
                                        scaleY = rect.height / miniLayer.size.height,
                                        pivot = Offset.Zero,
                                    ) { drawLayer(miniLayer) }
                                } else {
                                    drawTrackedMiniLayer(
                                        miniLayer = miniLayer,
                                        mini = mini,
                                        container = rect,
                                        controls = controlsNow,
                                    )
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
                                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                                    drawLayer(fullContentLayer)
                                }
                            }
                        }
                    }
                }
            },
    )
}

/** How much the landing pill flattens at the bottom of its dip, and widens with it. */
private const val LANDING_SQUASH = com.convx.music.designsystem.MotionTokens.PlayerLandingSquash
private const val LANDING_SPREAD = com.convx.music.designsystem.MotionTokens.PlayerLandingSpread

/**
 * The card during a close landing: the pill's own rect carried [PlayerMorph.landingDipPx]
 * below its slot, flattened and slightly widened in proportion (liquid squash), springing
 * back to exactly [mini] as the landing settles.
 */
private fun landingCardRect(mini: Rect): Rect {
    val dip = PlayerMorph.landingDipPx()
    val maxDip = PlayerMorph.landingMaxDipPx
    val squash = if (maxDip > 0f) (dip / maxDip).coerceIn(-1f, 1f) else 0f
    val height = mini.height * (1f - LANDING_SQUASH * squash)
    val width = mini.width * (1f + LANDING_SPREAD * squash)
    val bottom = mini.bottom + dip
    val centerX = mini.center.x
    return Rect(centerX - width / 2f, bottom - height, centerX + width / 2f, bottom)
}

/**
 * Mini recording drawn as two tracked halves (the reference app clip split, pill-origin
 * layer, inside the caller's translate to the container origin).
 *
 * Title side compensates the corner travel so it stays absolute while the
 * card grows around it; the controls half adds the edge shift, keeping its
 * right inset and breaking away rightward. Falls back to one plain draw
 * until the controls bounds report.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrackedMiniLayer(
    miniLayer: androidx.compose.ui.graphics.layer.GraphicsLayer,
    mini: Rect,
    container: Rect,
    controls: Rect?,
) {
    if (controls == null || controls.width <= 0f || controls.height <= 0f) {
        drawLayer(miniLayer)
        return
    }
    // Layer-space x of the split: controls' left edge relative to the pill.
    val split = controls.left - mini.left
    val titleShift = sharedMiniPlayerTitleShift(
        sourcePlayerBounds = mini,
        animatedPlayerBounds = container,
    )
    val edgeShift = sharedMiniPlayerControlsShift(
        sourcePlayerBounds = mini,
        animatedPlayerBounds = container,
        controlsBounds = controls,
    )
    clipRect(right = titleShift + split) {
        withTransform({
            translate(left = titleShift, top = 0f)
        }) {
            drawLayer(miniLayer)
        }
    }
    clipRect(left = titleShift + split + edgeShift) {
        withTransform({
            translate(left = titleShift + edgeShift, top = 0f)
        }) {
            drawLayer(miniLayer)
        }
    }
}
