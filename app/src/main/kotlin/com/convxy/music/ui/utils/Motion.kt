/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.utils

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * The app's motion language, in one place.
 *
 * Every animation in the app is supposed to be one of these. Before this existed
 * there were 44 hand-written `spring(`s, 169 `tween(`s and 153 `animate*` call
 * sites across 120 files, each picking its own timing — which is why two things
 * that move together (a nav bar and the page behind it, a puck and the tab it
 * marks) so often moved on visibly different clocks.
 *
 * The numbers are not taste. [MorphStiffness] is regressed off the iOS 27 reference
 * capture in `designRefrence/shared transition animation.mp4`; the rest derive from it
 * so that everything in the app reads as the same physical material.
 *
 * Springs, not tweens, wherever a value can be interrupted mid-flight — a spring
 * carries its velocity across the interruption and a tween restarts. Tweens are
 * kept only for pure cross-fades, where there is no velocity to preserve.
 */
object Motion {

    /**
     * Container morph: a tile opening into a detail screen, and back.
     *
     * Measured off the reference capture by recovering the morphing card's scale per
     * frame (multi-scale template match against the settled detail screen), over two
     * independent takes of each direction. Push reaches 99% in ~265ms, pop in ~230ms,
     * neither overshoots.
     *
     * Fitted to the SETTLE TIME, not to the per-frame shape. At 30fps a card crossing
     * the screen in a quarter of a second is heavily motion-blurred in exactly the
     * mid-transition frames, and blur biases a template match's scale estimate — the
     * measured mid-frames are not trustworthy enough to regress a curve shape from,
     * and pretending otherwise would be fitting the capture's blur, not Apple's
     * animation. Duration and the absence of overshoot are solid; both directions
     * average to a critically damped spring with a ~0.24s natural period.
     *
     * stiffness = (2*pi / 0.24)^2 ~= 700.
     *
     * The previous value was 195 (a 0.45s period) — ~3.6x slower to settle than the
     * reference. That one number was most of why the morph did not read as iOS: the
     * curve shape was already close, the clock was wrong.
     */
    const val MorphStiffness = 950f

    fun <T> morph(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = MorphStiffness,
    )

    /**
     * Card growing OUT of its tile (push) with Melox 320ms FastOutSlowIn curve.
     */
    const val MorphEnterMillis = 320
    val MorphEnterEasing: Easing = FastOutSlowInEasing

    fun <T> morphEnter(): FiniteAnimationSpec<T> = tween(MorphEnterMillis, easing = MorphEnterEasing)

    /**
     * Card shrinking BACK into its tile (pop) with 240ms FastOutSlowIn curve.
     */
    const val MorphExitMillis = 240
    val MorphExitEasing: Easing = FastOutSlowInEasing

    fun <T> morphExit(): FiniteAnimationSpec<T> = tween(MorphExitMillis, easing = MorphExitEasing)

    /**
     * Selection moving between two committed positions — the tab puck, a segmented
     * control's thumb.
     */
    const val SelectStiffness = 750f

    fun <T> select(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = SelectStiffness,
    )

    /**
     * Press feedback — the scale-down under a finger and its release.
     */
    const val PressStiffness = 1220f

    fun <T> press(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = PressStiffness,
    )

    /**
     * Something arriving or leaving in place: a sheet, a chip row, an inline error.
     */
    const val AppearStiffness = 450f

    fun <T> appear(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = AppearStiffness,
    )

    /**
     * [appear], but for the leaving half of a transition that predictive back can
     * gesture-scrub.
     */
    const val AppearExitMillis = 240

    fun <T> appearExit(): FiniteAnimationSpec<T> = tween(AppearExitMillis, easing = MorphExitEasing)

    // The overscroll bounce-back deliberately lives in `IosOverscroll.kt` instead of
    // here: it is velocity-adaptive (a hard flick gets a longer arc), which no single
    // constant can express.

    // ---- Navigation push/pop -------------------------------------------------

    /**
     * Duration of a screen push/pop.
     *
     * UIKit's own is ~0.35s and the reference agrees, but the app's glass chrome
     * samples a RECORDED backdrop layer that freezes for the duration of a push to
     * avoid re-recording the whole tree every frame (see NavTransitionFreeze) --
     * freezing is invisible for a scroll or pager slide, where the backdrop only
     * changes incrementally, but a push swaps the ENTIRE screen, so the longer the
     * freeze window the longer the glass blur behind the chrome visibly shows the
     * wrong (stale) content while the actual foreground slides underneath it. 350ms
     * made that gap obvious. 260ms is the tradeoff: still slower and smoother than
     * the original 200ms tween, short enough that the stale-backdrop window reads
     * as a blur settling rather than a broken transition.
     */
    const val PushMillis = 260

    /**
     * UIKit's navigation curve. Not `FastOutSlowIn`: Material's curve pulls harder
     * out of rest and coasts into the end, which under a full-width slide reads as
     * the screen being thrown rather than pushed.
     */
    val PushEasing: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

    fun <T> push(): FiniteAnimationSpec<T> = tween(PushMillis, easing = PushEasing)

    /**
     * How far the outgoing screen travels under an incoming one, as a fraction of
     * width. The parallax — outgoing moving slower and shorter than incoming — is
     * what makes a push read as depth instead of two screens sliding in lockstep.
     */
    const val PushParallax = 0.30f

    /** Alpha the outgoing screen dims to while it sits under the incoming one. */
    const val PushDimAlpha = 0.85f

    /** Outgoing screen's travel, for `slideOutHorizontally`/`slideInHorizontally`. */
    fun parallaxOffset(fullWidth: Int): Int = -(fullWidth * PushParallax).toInt()
}
