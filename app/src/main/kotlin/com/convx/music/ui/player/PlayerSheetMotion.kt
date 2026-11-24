/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.ui.geometry.Rect
import com.convx.music.playback.presentation.PLAYER_MORPH_ARTWORK_VERTICAL_LINEAR_WEIGHT
import com.convx.music.playback.presentation.PLAYER_MORPH_CORNER_EXPANSION_PROGRESS_BAND
import com.convx.music.playback.presentation.PLAYER_MORPH_INPUT_MINI_END_PROGRESS
import com.convx.music.playback.presentation.PLAYER_MORPH_SCREEN_CORNER_EXPANSION_MILLIS
import com.convx.music.playback.presentation.PlayerMorphCornerRadii
import com.convx.music.playback.presentation.PlayerMorphGeometry
import com.convx.music.playback.presentation.PlayerMorphInputOwner

const val ARTWORK_VERTICAL_LINEAR_WEIGHT = PLAYER_MORPH_ARTWORK_VERTICAL_LINEAR_WEIGHT
const val SCREEN_CORNER_EXPANSION_MILLIS = PLAYER_MORPH_SCREEN_CORNER_EXPANSION_MILLIS
const val CORNER_EXPANSION_PROGRESS_BAND = PLAYER_MORPH_CORNER_EXPANSION_PROGRESS_BAND
internal const val PLAYER_INPUT_MINI_END_PROGRESS = PLAYER_MORPH_INPUT_MINI_END_PROGRESS

internal typealias PlayerInputOwner = PlayerMorphInputOwner
internal typealias SharedContainerCornerRadii = PlayerMorphCornerRadii

internal fun sanitizedProgress(progress: Float): Float = PlayerMorphGeometry.sanitizeProgress(progress)

fun easeInCubic(value: Float): Float = PlayerMorphGeometry.easeInCubic(value)

fun easeOutCubic(value: Float): Float = PlayerMorphGeometry.easeOutCubic(value)

fun sharedContainerRect(source: Rect, target: Rect, progress: Float): Rect =
    PlayerMorphGeometry.containerRect(source, target, progress)

internal fun playerSheetInputOwner(
    targetOpen: Boolean,
    isDragging: Boolean,
    dragStartedFromMiniPlayer: Boolean,
    progress: Float,
    sharedLayersReady: Boolean,
): PlayerInputOwner = PlayerMorphGeometry.inputOwner(
    targetOpen = targetOpen,
    isDragging = isDragging,
    dragStartedFromMiniPlayer = dragStartedFromMiniPlayer,
    progress = progress,
    sharedLayersReady = sharedLayersReady,
)

fun sharedMiniPlayerTitleShift(sourcePlayerBounds: Rect, animatedPlayerBounds: Rect): Float =
    PlayerMorphGeometry.miniTitleShift(sourcePlayerBounds, animatedPlayerBounds)

fun sharedMiniPlayerControlsShift(
    sourcePlayerBounds: Rect,
    animatedPlayerBounds: Rect,
    controlsBounds: Rect,
): Float = PlayerMorphGeometry.miniControlsShift(sourcePlayerBounds, animatedPlayerBounds, controlsBounds)

fun sharedArtworkRect(source: Rect, target: Rect, progress: Float): Rect =
    PlayerMorphGeometry.artworkRect(source, target, progress)

fun sharedArtworkWindow(source: Rect, target: Rect, progress: Float): Rect =
    PlayerMorphGeometry.artworkWindow(source, target, progress)

internal fun sharedContainerCornerRadii(
    source: SharedContainerCornerRadii,
    target: SharedContainerCornerRadii,
    progress: Float,
): SharedContainerCornerRadii = PlayerMorphGeometry.cornerRadii(source, target, progress)

fun sharedContainerCornerRadius(
    collapsedCornerRadius: Float,
    expandedCornerRadius: Float,
    progress: Float,
    screenCornerExpansionProgress: Float,
): Float = PlayerMorphGeometry.cornerRadius(
    collapsedCornerRadius = collapsedCornerRadius,
    expandedCornerRadius = expandedCornerRadius,
    progress = progress,
    screenCornerExpansionProgress = screenCornerExpansionProgress,
)

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

val SeamlessEasing = androidx.compose.animation.core.CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

const val AMBIENT_TRANSITION_DEFAULT_MS = 1_100

fun ambientTransitionMillis(crossfadeMs: Long): Int =
    if (crossfadeMs > 0L) crossfadeMs.coerceIn(900L, 8_000L).toInt() else AMBIENT_TRANSITION_DEFAULT_MS
