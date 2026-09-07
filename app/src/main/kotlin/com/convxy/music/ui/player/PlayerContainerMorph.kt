/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.player

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import com.convxy.music.ui.component.PLAYER_LAYER_HANDOFF_PROGRESS

/**
 * Where the mini player's own pill sits on screen, in root coordinates.
 *
 * Module-level state for the same reason the artwork rects are: the pill lives in the
 * floating nav bar, the sheet lives in the player, they are far apart in the tree, and
 * only the overlay -- drawn once, in between -- reads it.
 */
private val miniContainerRect = mutableStateOf<Rect?>(null)

internal val miniPlayerContainerRect: Rect? get() = miniContainerRect.value

/**
 * Call on the mini player's outermost pill box. Purely observational: it records the
 * pill's bounds and changes nothing about how the pill renders.
 */
fun Modifier.registerMiniContainerRect(): Modifier = onGloballyPositioned {
    if (it.isAttached) miniContainerRect.value = it.boundsInRoot()
}

/** The mini player's recording fades out across the first quarter of the flight. */
fun playerSheetBarAlpha(progress: Float): Float =
    1f - easeOutCubic((progress / PLAYER_LAYER_HANDOFF_PROGRESS).coerceIn(0f, 1f))

/** ...and the full player's recording fades in over exactly the same slice. */
fun playerSheetPageAlpha(progress: Float): Float =
    easeInCubic((progress / PLAYER_LAYER_HANDOFF_PROGRESS).coerceIn(0f, 1f))

/**
 * The one surface the whole morph happens inside.
 *
 * Positioned at the interpolated container rect -- the pill's bounds at progress 0, the
 * whole screen at 1 -- it draws BOTH recordings:
 *
 * - the mini player's, at the container's own top-left, fading out over the first
 *   quarter, so its cover/title/buttons keep their offsets from the container's top edge
 *   while that edge travels;
 * - the full player's, scaled by `container width / full width`, so the entire player UI
 *   shrinks into the pill and unfolds back out of it.
 *
 * That second half is what a translating sheet cannot do, and the reason the old version
 * read as "the bar rises while the player drops" rather than as one thing folding into
 * another.
 *
 * ENTIRELY draw-phase. An earlier version sized a real Box from progress read in
 * composition; it composed once, at progress 0.0004, and never again -- so the container
 * sat at pill size for the whole gesture while the sheet it had replaced was no longer
 * drawing itself either. Reading progress here means the frame that changes it is the
 * frame that redraws this, with no recomposition at all.
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
                onDrawBehind {
                    val fraction = progress().coerceIn(0f, 1f)
                    if (fraction <= 0f || fraction >= 1f) return@onDrawBehind
                    val mini = miniContainerRect.value ?: return@onDrawBehind
                    // onGloballyPositioned can fire once with a degenerate size before layout
                    // settles.
                    if (mini.width <= 0f || mini.height <= 0f) return@onDrawBehind
                    val miniLayer = PlayerMorph.miniLayer ?: return@onDrawBehind
                    val fullLayer = PlayerMorph.fullLayer ?: return@onDrawBehind
                    if (fullLayer.size.width <= 0) return@onDrawBehind

                    // The full player is the recorded screen itself, so its rect IS this
                    // overlay's own size. Nothing to register at that end, nothing to go
                    // stale.
                    val full = Rect(0f, 0f, size.width, size.height)
                    val rect = sharedContainerRect(mini, full, fraction)
                    val radius = sharedContainerCornerRadius(
                        // The nav bar draws the pill as a 50%-rounded capsule, so its radius
                        // is half its height by definition. Derived rather than passed in, so
                        // restyling the pill cannot leave the two out of step.
                        collapsedCornerRadius = mini.height / 2f,
                        expandedCornerRadius = expandedCornerRadius.toPx(),
                        progress = fraction,
                        screenCornerExpansionProgress = 0f,
                    )

                    clipPath.reset()
                    clipPath.addRoundRect(RoundRect(rect, CornerRadius(radius, radius)))
                    clipPath(clipPath) {
                        // Under both recordings: the pill's own glass stayed behind in the
                        // nav bar, and the full player's background only reaches full opacity
                        // at the handoff, so without this the first frames are see-through.
                        drawRect(
                            color = color,
                            topLeft = Offset(rect.left, rect.top),
                            size = Size(rect.width, rect.height),
                        )
                        translate(left = rect.left, top = rect.top) {
                            if (miniLayer.size.width > 0 && fraction < PLAYER_LAYER_HANDOFF_PROGRESS) {
                                miniLayer.alpha = playerSheetBarAlpha(fraction)
                                drawLayer(miniLayer)
                            }
                            val scale = rect.width / fullLayer.size.width.toFloat()
                            fullLayer.alpha = playerSheetPageAlpha(fraction)
                            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) {
                                drawLayer(fullLayer)
                            }
                        }
                    }
                }
            },
    )
}
