/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.player

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size as CoilSize

/**
 * Registered bounds of the mini player's own artwork and the full player's own artwork,
 * in root/window coordinates -- updated from `onGloballyPositioned` on each, read once
 * by [PlayerArtworkMorphOverlay].
 *
 * Plain module-level state, not a hoisted parameter, for the same reason
 * `SharedArtwork.kt`'s `placedArtwork` map is plain state: the two artworks live in
 * composables that are far apart in the tree (`MiniPlayer.kt`'s collapsed content,
 * `Thumbnail.kt`'s expanded content, orchestrated from `BottomSheet.kt` in between) and
 * neither needs to compose off the other's position -- only the overlay, drawn once,
 * reads both.
 */
private val miniArtworkRect = mutableStateOf<Rect?>(null)
private val fullArtworkRect = mutableStateOf<Rect?>(null)

/**
 * Call on the mini player's own artwork box. Purely observational: records where the
 * artwork is on screen and changes nothing about how the mini player itself renders.
 */
/**
 * Corner radius of each end's cover, in px.
 *
 * The flight used to clip to [CircleShape] the whole way, so a rounded-square pill cover
 * snapped to a circle the instant it left the bar and snapped back to the player's own
 * shape on arrival. The radius has to travel with the rect.
 *
 * A vinyl or clover player artwork registers half its size here, i.e. a circle; the
 * clover's petals are not reproduced mid-flight, which reads as a circle growing into a
 * clover at the very end.
 */
private val miniArtworkRadius = androidx.compose.runtime.mutableFloatStateOf(0f)
private val fullArtworkRadius = androidx.compose.runtime.mutableFloatStateOf(0f)

fun Modifier.registerMiniArtworkRect(cornerRadiusPx: Float = 0f): Modifier = onGloballyPositioned {
    miniArtworkRadius.floatValue = cornerRadiusPx
    if (it.isAttached) miniArtworkRect.value = it.boundsInRoot()
}

/** Call on the full player's own artwork container. Same contract as
 *  [registerMiniArtworkRect]. */
fun Modifier.registerFullArtworkRect(cornerRadiusPx: Float = 0f): Modifier = onGloballyPositioned {
    // The player's artwork lives in a pager, so every neighbouring page reports bounds
    // too -- and the last one to report would win. Only the page actually on screen may
    // claim the target rect; an off-screen page's window bounds are empty.
    if (it.boundsInWindow().isEmpty) return@onGloballyPositioned
    fullArtworkRadius.floatValue = cornerRadiusPx
    if (it.isAttached) fullArtworkRect.value = it.boundsInRoot()
}

/**
 * Draws ONE artwork image growing from the mini player's registered rect to the full
 * player's, as [progress] runs 0..1.
 *
 * Sized to the FULL (target) rect, not the mini one -- scaling DOWN from there to the
 * mini size at fraction 0, then back up to 1:1 (its native size) at the handoff. This
 * is the opposite of the first version, which sized to the small mini rect and scaled
 * UP: scaling up stretches whatever resolution the underlying image happens to be,
 * which read as soft mid-growth even after requesting a larger decode, because the
 * COMPOSE LAYOUT SIZE (what a bitmap gets stretched across) was still the tiny mini
 * box the whole time. Sizing to full and scaling down means the on-screen image is
 * never asked to be bigger than the pixels it actually has -- downsampling stays
 * sharp at any fraction, which is exactly the technique Melox's own
 * `PlayerSheetArtworkOverlay` uses (see the scratchpad clone read for this fix).
 *
 * `transformOrigin` pinned to the top-left (not the layer's default center) so a
 * plain `translationX/Y = rect.left/top` places the SCALED result exactly at
 * [sharedArtworkRect]'s interpolated rect, with no extra center-offset math needed.
 *
 * A fresh [AsyncImage] of the same URL, not a captured GraphicsLayer of the mini
 * player's own rendering (Melox's technique) -- both artworks show identical content,
 * so a pixel capture buys nothing a positioned image doesn't, and Coil's cache is
 * already warm from the mini player's own load.
 *
 Its corner radius travels with it, from the pill cover's to whatever the player's own
 * artwork uses (a card's radius, or half its size for the round vinyl), so the cover
 * grows into the real shape instead of snapping to a circle on the way.
 */
@Composable
fun PlayerArtworkMorphOverlay(
    thumbnailUrl: String?,
    progress: () -> Float,
) {
    val mini = miniArtworkRect.value
    val full = fullArtworkRect.value
    if (mini == null || full == null || thumbnailUrl == null) return
    // Tied to the container morph, not to raw progress: on a layout with no pill to fly
    // out of (classic nav bar, rail, tab view) the sheet still just slides, and a cover
    // flying across it would be flying out of nowhere.
    if (!PlayerMorph.active) return
    // Both rects are registered from onGloballyPositioned, which can fire once with
    // a degenerate (zero or negative) size before the real layout pass settles --
    // measured a crash on a Samsung device: coil3.size.Dimension throws on any px
    // <= 0, and full.width/height fed CoilSize directly with no floor.
    if (mini.width <= 0f || mini.height <= 0f || full.width <= 0f || full.height <= 0f) return

    val density = LocalDensity.current
    val boxWidthDp = with(density) { full.width.toDp() }
    val boxHeightDp = with(density) { full.height.toDp() }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    full.left.roundToInt(),
                    full.top.roundToInt(),
                )
            }
            .size(boxWidthDp, boxHeightDp)
            .graphicsLayer {
                val fraction = progress().coerceIn(0f, 1f)
                if (fraction <= 0f || fraction >= 1f) {
                    alpha = 0f
                    return@graphicsLayer
                }
                alpha = 1f
                val rect = sharedArtworkRect(mini, full, fraction)
                transformOrigin = TransformOrigin(0f, 0f)
                translationX = rect.left - full.left
                translationY = rect.top - full.top
                val artworkScaleX = rect.width / full.width.coerceAtLeast(1f)
                val artworkScaleY = rect.height / full.height.coerceAtLeast(1f)
                scaleX = artworkScaleX
                scaleY = artworkScaleY
                val radius = androidx.compose.ui.util.lerp(
                    miniArtworkRadius.floatValue,
                    fullArtworkRadius.floatValue,
                    fraction,
                )
                val localRadius = if (artworkScaleX > 0f) (radius / artworkScaleX).toDp() else radius.toDp()
                shape = RoundedCornerShape(localRadius)
                clip = true
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailUrl)
                .size(CoilSize(full.width.toInt(), full.height.toInt()))
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(boxWidthDp, boxHeightDp),
        )
    }
}

/**
 * Hides the full player's own artwork while [PlayerArtworkMorphOverlay] is in flight.
 *
 * The overlay draws the cover for the whole 0..1 journey, but the sheet's content is
 * already opaque from the handoff onward -- so without this, both are on screen from a
 * quarter of the way in: the real one sitting at its final position while the travelling
 * one is still climbing towards it.
 */
fun Modifier.hideWhileArtworkMorphing(progress: () -> Float): Modifier = graphicsLayer {
    // Lambda, not a Float: this modifier sits deep inside the player's content (under
    // AnimatedContent and a shared-element LookaheadScope on the V2 player). Taking the
    // value meant the caller read the sheet's progress in COMPOSITION, so that whole
    // subtree recomposed on every frame of every open/close drag -- which is what put
    // measure requests on nodes the same frame was deactivating ("measure is called on
    // a deactivated node"). Reading it here keeps it to the draw phase.
    val fraction = progress()
    alpha = if (fraction > 0f && fraction < 1f) 0f else 1f
}
