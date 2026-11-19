/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.convx.music.ui.component.floatingtabbar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.shapes.shadowShape
import kotlinx.coroutines.flow.drop

/**
 * The tab pill and the inline Home circle are one shape to the eye: on collapse the pill's
 * right edge retracts into the circle, on expand the circle grows back out. Sharing their
 * bounds makes that a continuous resize; before, they were two unrelated trees that
 * cross-faded (pill vanished, circle popped in).
 *
 * Both backgrounds sit inside the shared node, so the fades are short and overlap: the
 * outgoing labels have to clear in about two frames (the reference blurs them out first)
 * without the glass thinning out between the two backgrounds.
 */
@Composable
internal fun SharedTransitionScope.tabPillBounds(animatedVisibilityScope: AnimatedVisibilityScope): Modifier =
    Modifier.sharedBounds(
        sharedContentState = rememberSharedContentState("tabPill"),
        animatedVisibilityScope = animatedVisibilityScope,
        enter = fadeIn(tween(60)),
        exit = fadeOut(tween(70)),
        boundsTransform = LocalBarBoundsTransforms.current.bar,
        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
        zIndexInOverlay = 1f,
    )

/**
 * Inline/expand choreography, reverted to the pre-yesterday behaviour: one
 * fixed-duration tween drives the outer SizeTransform, the fades AND every
 * sharedElement bounds, zero delay, both directions. Tweens share one end
 * frame, so the overlay hands back to real layout on the same frame -- no
 * spring settle-threshold drift between the container and the overlay copy,
 * which is what read as the icon pop. Stagger/bounce sacrificed on purpose:
 * each was its own clock on the same pixels.
 */
internal class BarBoundsTransforms(
    /** Surfaces (tab pill, standalone circle, search field): body bounces, centre does not. */
    val bar: BoundsTransform,
    /** Items riding the morph (the travelling tab glyph): no overshoot at all. */
    val item: BoundsTransform,
) {
    companion object {
        val Collapse = BarBoundsTransforms(
            bar = BoundsTransform { _, _ -> BarMotion.surface(CollapseMorphMs, BarMotion.BarSurfaceCollapseSpring) },
            item = BoundsTransform { _, _ -> tween(CollapseMorphMs, easing = BarMotion.Travel) },
        )
        val Expand = BarBoundsTransforms(
            bar = BoundsTransform { _, _ -> BarMotion.surface(ExpandMorphMs, BarMotion.BarSurfaceExpandSpring, BarMotion.BarSurfaceExpandWidthSpring) },
            item = BoundsTransform { _, _ -> tween(ExpandMorphMs, easing = BarMotion.Travel) },
        )
    }
}

internal val LocalBarBoundsTransforms = staticCompositionLocalOf { BarBoundsTransforms.Collapse }

/**
 * The docked mini pill's drop shadow, scaled by [com.convx.music.ui.player.PlayerMorph.pillShadow]
 * so it fades back in after the player lands instead of popping. Same as `.shadow()`
 * (elevation + shape + clip), with the elevation read in the draw phase.
 */
internal fun Modifier.pillShadow(shape: Shape, elevation: Dp): Modifier = graphicsLayer {
    shadowElevation = elevation.toPx() * com.convx.music.ui.player.PlayerMorph.pillShadow.value
    this.shape = shape.shadowShape
    // No clip: the mini's G2 capsule is a path, and a path clip is a CPU mask uploaded every
    // frame its glass re-renders. Fill and glass follow the outline themselves.
    clip = false
}

/**
 * Clips to [shape] only while [glow] is pressed: its sheen is a full-rect Plus draw that
 * needs the silhouette, but a permanent path clip costs a CPU mask every frame.
 */
internal fun Modifier.clipWhilePressed(
    glow: com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight,
    shape: Shape,
): Modifier = graphicsLayer {
    this.shape = shape
    clip = glow.pressProgress > 0f
}
