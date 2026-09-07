/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.player

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

/*
 * Geometry for a mini-player <-> full-player transition treated as a shared element:
 * one container and one artwork travelling between two measured rects, rather than a
 * sheet sliding up over the top of a separate mini player.
 *
 * The reason this reads better than a straight interpolation is that nothing here moves
 * on a single curve — each axis of each element gets its own, and the
 * asymmetry is the whole effect:
 *
 * - The container's **height** tracks progress linearly while its **width** runs on
 *   [easeInCubic]. The sheet therefore grows tall first and only flares out to full width
 *   near the end, which is what makes it look like it is rising out of the bar instead of
 *   inflating in place.
 * - The artwork's **x** runs on [easeOutCubic] — it commits to its horizontal position
 *   early, so the cover appears to lead the motion — while its **y** is a blend of
 *   [easeInCubic] and linear ([ARTWORK_VERTICAL_LINEAR_WEIGHT]), keeping vertical travel
 *   closer to the finger during a drag.
 *
 * All of this is pure geometry: no Compose state, no animation. Feed it a progress value
 * from whatever already drives the sheet (a drag, a spring) and it returns where things
 * belong at that instant. Kept separate from the sheet itself so the curves can be
 * checked directly, which is the only practical way to verify motion this fiddly.
 */

/** Weight of the linear term in the artwork's vertical travel; the rest is [easeInCubic]. */
const val ARTWORK_VERTICAL_LINEAR_WEIGHT = 0.4f

/**
 * How long the screen corners take to open out to square once the sheet is fully
 * expanded. Deliberately short and deliberately separate from the sheet's own motion:
 * the corners resolve after the sheet arrives, not with it.
 */
const val SCREEN_CORNER_EXPANSION_MILLIS = 140

fun easeInCubic(value: Float): Float {
    val clamped = value.coerceIn(0f, 1f)
    return clamped * clamped * clamped
}

fun easeOutCubic(value: Float): Float {
    val clamped = value.coerceIn(0f, 1f)
    val inverse = 1f - clamped
    return 1f - inverse * inverse * inverse
}

/**
 * The sheet container's bounds at [progress], 0 being [source] (the mini player) and 1
 * being [target] (the full player).
 */
fun sharedContainerRect(source: Rect, target: Rect, progress: Float): Rect {
    val fraction = progress.coerceIn(0f, 1f)
    val centerX = lerp(source.center.x, target.center.x, fraction)
    val centerY = lerp(source.center.y, target.center.y, fraction)
    // Width lags behind height on purpose — see the class doc.
    val width = lerp(source.width, target.width, easeInCubic(fraction))
    val height = lerp(source.height, target.height, fraction)
    return Rect(
        left = centerX - width / 2f,
        top = centerY - height / 2f,
        right = centerX + width / 2f,
        bottom = centerY + height / 2f,
    )
}

/**
 * The artwork's bounds at [progress].
 *
 * Scaled uniformly from the source rather than interpolated per-edge, so a non-square
 * mini-player cover keeps its aspect ratio the whole way across instead of stretching
 * into the target's shape.
 */
fun sharedArtworkRect(source: Rect, target: Rect, progress: Float): Rect {
    val fraction = progress.coerceIn(0f, 1f)
    val centerX = lerp(source.center.x, target.center.x, easeOutCubic(fraction))
    val verticalFraction = lerp(
        easeInCubic(fraction),
        fraction,
        ARTWORK_VERTICAL_LINEAR_WEIGHT,
    )
    val centerY = lerp(source.center.y, target.center.y, verticalFraction)
    val sourceWidth = source.width.coerceAtLeast(1f)
    // coerceAtLeast(sourceWidth): a target narrower than the source would shrink the
    // cover mid-flight, which only happens from a bad measurement, never by design.
    val targetWidth = target.width.coerceAtLeast(sourceWidth)
    val scale = lerp(1f, targetWidth / sourceWidth, fraction)
    val width = sourceWidth * scale
    val height = source.height.coerceAtLeast(1f) * scale
    return Rect(
        left = centerX - width / 2f,
        top = centerY - height / 2f,
        right = centerX + width / 2f,
        bottom = centerY + height / 2f,
    )
}

/**
 * Width of the progress band over which the screen-corner expansion fades out.
 *
 * Gating the expansion on `progress == 1f` exactly is fine on the way
 * in — the corners open out after the sheet has settled — but it pops on the way out: the
 * first pixel of a downward drag takes progress off 1f, which kills the expansion in one
 * frame and snaps the radius from square back to the full screen radius. Fading the
 * expansion across the top slice of the drag removes the jump while still keeping the
 * corners square for the entire time the sheet is actually at rest.
 */
const val CORNER_EXPANSION_PROGRESS_BAND = 0.02f

/**
 * Corner radius for the sheet at [progress].
 *
 * Two stages. Through the transition the radius runs from the mini player's corner to
 * [expandedCornerRadius] — which should be the device's *physical* screen corner radius,
 * not a design value, so the sheet's corners sit exactly on the glass. Then, as the sheet
 * settles, [screenCornerExpansionProgress] takes the radius to zero so the player fills
 * the display edge to edge and the glass itself provides the rounding.
 *
 * The second stage is confined to the very top of the progress range (see
 * [CORNER_EXPANSION_PROGRESS_BAND]); through the body of a drag the radius stays on the
 * first curve, so the corners never go square during a gesture the user has not committed
 * to.
 */
fun sharedContainerCornerRadius(
    collapsedCornerRadius: Float,
    expandedCornerRadius: Float,
    progress: Float,
    screenCornerExpansionProgress: Float,
): Float {
    val fraction = progress.coerceIn(0f, 1f)
    val transitionRadius = lerp(collapsedCornerRadius, expandedCornerRadius, fraction)
    val settled = ((fraction - (1f - CORNER_EXPANSION_PROGRESS_BAND)) / CORNER_EXPANSION_PROGRESS_BAND)
        .coerceIn(0f, 1f)
    val expansion = screenCornerExpansionProgress.coerceIn(0f, 1f) * settled
    return lerp(transitionRadius, 0f, expansion)
}

/**
 * Whether the player is filling the physical display, and may therefore round its corners
 * to the device's own radius.
 *
 * In split screen or picture-in-picture the window's corners are not the screen's, so
 * matching the physical radius there would draw a curve that lines up with nothing.
 */
fun playerWindowUsesPhysicalScreenCorners(
    currentWidth: Int,
    currentHeight: Int,
    maximumWidth: Int,
    maximumHeight: Int,
    isInMultiWindowMode: Boolean,
    isInPictureInPictureMode: Boolean,
): Boolean = !isInMultiWindowMode &&
    !isInPictureInPictureMode &&
    currentWidth >= maximumWidth &&
    currentHeight >= maximumHeight
