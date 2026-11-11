/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.utils

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.convx.music.designsystem.MotionTokens

/** Shared animation specs and motion tokens. Springs preserve velocity when interrupted; tweens are for fades. */
object Motion {
    fun <T> artwork(): FiniteAnimationSpec<T> = MotionTokens.artwork()

    /** List artwork expanding into a detail hero and returning to its source tile. */
    const val SharedArtworkMillis = MotionTokens.SharedArtworkMillis
    fun <T> sharedArtwork(): FiniteAnimationSpec<T> = MotionTokens.sharedArtwork()

    fun collectionPush(): FiniteAnimationSpec<androidx.compose.ui.unit.IntOffset> = MotionTokens.collectionPush()

    /** Symmetric easing that keeps lyrics near rest at the start and end of a line transition. */
    val LyricEasing = CubicBezierEasing(0.75f, 0f, 0.25f, 1f)

    /** Stiffness for the shared-element container morph. */
    const val MorphStiffness = MotionTokens.PlayerMorphStiffness

    fun <T> morph(): FiniteAnimationSpec<T> = MotionTokens.playerMorph()

    /**
     * Card growing OUT of its tile (push): 340ms on the iOS nav curve so the
     * morph and the push behind it stay on one clock. Body staggers 70ms after.
     * Value-duplicates PushEasing below: forward ref illegal in object init order.
     */
    const val MorphEnterMillis = MotionTokens.PlayerMorphEnterMillis
    // Fitted 2026-09-16 to the iOS 27 beta 5 capture (designRefrence/shared transition
    // animation.mp4), card edge per frame: 21/43/56/74/83/93/97% at 33..233ms open,
    // 32/55/73/85/93/98% at 33..200ms close. The old (0.32, 0.72, 0, 1) curve hit 69% at
    // 67ms -- front-loaded ~1.6x versus the reference. One easing, two durations.
    val MorphEnterEasing: Easing = MotionTokens.PlayerMorphEasing

    fun <T> morphEnter(): FiniteAnimationSpec<T> = MotionTokens.playerMorphEnter()

    /**
     * Card shrinking BACK into its tile (pop): body out first 90ms, hero holds
     * to midway then drops. Timed (album intercepts predictive back), so a
     * scrub-safe tween is fine. See SharedArtwork.MorphBoundsTransform.
     */
    const val MorphExitMillis = MotionTokens.PlayerMorphExitMillis
    val MorphExitEasing: Easing = MotionTokens.PlayerMorphEasing

    fun <T> morphExit(): FiniteAnimationSpec<T> = MotionTokens.playerMorphExit()

    /**
     * Selection moving between two committed positions — the tab puck, a segmented
     * control's thumb.
     */
    const val SelectStiffness = 750f

    fun <T> select(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.76f,
        stiffness = SelectStiffness,
    )

    /**
     * Press feedback — the scale-down under a finger and its release.
     */
    const val PressStiffness = 1220f

    fun <T> press(): FiniteAnimationSpec<T> = spring(
        dampingRatio = 0.75f,
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
    const val AppearExitMillis = 400

    fun <T> appearExit(): FiniteAnimationSpec<T> = tween(AppearExitMillis, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f))

    /**
     * Icon swap in place: play <-> pause. Single source of truth for every
     * small symbolic swap so all of them share one clock. Kept deliberately
     * snappy (200 in / 160 out) with a 0.3 scale pop: at 0.85 the scale delta
     * is indistinguishable from a cut and the swap reads as a different button
     * appearing rather than this one changing its mind.
     */
    const val SwapEnterMillis = 200
    const val SwapExitMillis = 160
    val SwapEasing: Easing = FastOutSlowInEasing
    const val SwapInitialScale = 0.3f

    fun <T> swapEnter(): FiniteAnimationSpec<T> = tween(SwapEnterMillis, easing = SwapEasing)

    fun <T> swapExit(): FiniteAnimationSpec<T> = tween(SwapExitMillis, easing = SwapEasing)

    /**
     * Event-driven reduce-motion gate: the single place that decides whether an
     * animation runs or snaps. Callers read the reduce-motion preference once as
     * an event ([reduceMotion]) and pass their [Motion] spec through here, so no
     * call site hand-rolls its own `if (reduce) snap() else tween(...)`.
     */
    fun <T> gate(reduceMotion: Boolean, spec: FiniteAnimationSpec<T>): FiniteAnimationSpec<T> =
        if (reduceMotion) snap() else spec

    // The overscroll bounce-back deliberately lives in `IosOverscroll.kt` instead of
    // here: it is velocity-adaptive (a hard flick gets a longer arc), which no single
    // constant can express.

    // Navigation push and pop.

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
    const val PushMillis = MotionTokens.PushMillis

    /**
     * UIKit's navigation curve. Not `FastOutSlowIn`: Material's curve pulls harder
     * out of rest and coasts into the end, which under a full-width slide reads as
     * the screen being thrown rather than pushed.
     */
    val PushEasing: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)

    fun <T> push(): FiniteAnimationSpec<T> = MotionTokens.push()

    /**
     * How far the outgoing screen travels under an incoming one, as a fraction of
     * width. The parallax — outgoing moving slower and shorter than incoming — is
     * what makes a push read as depth instead of two screens sliding in lockstep.
     */
    const val PushParallax = MotionTokens.PushParallax

    /** Alpha the outgoing screen dims to while it sits under the incoming one. */
    const val PushDimAlpha = MotionTokens.PushDimAlpha

    /** Outgoing screen's travel, for `slideOutHorizontally`/`slideInHorizontally`. */
    fun parallaxOffset(fullWidth: Int): Int = MotionTokens.parallaxOffset(fullWidth)
}
