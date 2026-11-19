/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.player

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Q2 lock: the single host owning the mini <-> full morph flight.
 *
 * One composition driven by one [progress] source, composing (in z order):
 * the interpolated container ([PlayerContainerMorphOverlay]) with its title and transport
 * handoff, followed by a travelling artwork window ([PlayerArtworkMorphOverlay]).
 * The overlay contract is progress in, draw-phase reads only.
 */
@Composable
fun BoxScope.PlayerMorphHost(
    progress: () -> Float,
    thumbnailUrl: String?,
    mediaId: String?,
    containerColor: Color,
    expandedCornerRadius: Dp,
) {
    // Container first, artwork on top of it: the artwork travels across the
    // same window and has to ride above the surface it is growing out of.
    PlayerContainerMorphOverlay(
        progress = progress,
        color = containerColor,
        expandedCornerRadius = expandedCornerRadius,
    )
    PlayerArtworkMorphOverlay(
        thumbnailUrl = thumbnailUrl,
        mediaId = mediaId,
        progress = progress,
    )
}
