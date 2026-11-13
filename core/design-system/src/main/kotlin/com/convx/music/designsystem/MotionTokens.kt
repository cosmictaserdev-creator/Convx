package com.convx.music.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import kotlin.math.exp

/** Shared timing and curves for Convx navigation, artwork morphs, and selection motion. */
object MotionTokens {
    const val ArtworkMillis = 350
    const val SharedArtworkMillis = 500
    const val PushMillis = 260
    const val PageEnterMillis = 300
    const val PageExitMillis = 220
    const val PlayerMorphEnterMillis = 340
    const val PlayerMorphExitMillis = 260
    const val PlayerMorphStiffness = 950f
    const val PlayerSheetStiffness = 360f
    const val PlayerSheetLandingDamping = 0.72f
    const val PlayerLandingDamping = 0.5f
    const val PlayerLandingStiffness = 140f
    const val PlayerLandingMaxDipDp = 22f
    const val PlayerLandingSquash = 0.14f
    const val PlayerLandingSpread = 0.04f
    const val PushParallax = 0.30f
    const val PushDimAlpha = 0.85f

    private const val ArtworkEndpoint = 9f
    private val ArtworkNormalization = 1f / (1f - (1f + ArtworkEndpoint) * exp(-ArtworkEndpoint))
    private val ArtworkEasing: Easing = Easing(::artworkProgress)
    private val PushEasing: Easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)
    val PlayerMorphEasing: Easing = CubicBezierEasing(0.3f, 0.5f, 0.4f, 1f)

    fun <T> artwork(): FiniteAnimationSpec<T> = tween(ArtworkMillis, easing = ArtworkEasing)

    fun <T> sharedArtwork(): FiniteAnimationSpec<T> = tween(SharedArtworkMillis, easing = ArtworkEasing)

    fun <T> push(): FiniteAnimationSpec<T> = tween(PushMillis, easing = PushEasing)

    fun <T> playerMorph(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = PlayerMorphStiffness,
    )

    fun <T> playerMorphEnter(): FiniteAnimationSpec<T> =
        tween(PlayerMorphEnterMillis, easing = PlayerMorphEasing)

    fun <T> playerMorphExit(): FiniteAnimationSpec<T> =
        tween(PlayerMorphExitMillis, easing = PlayerMorphEasing)

    fun <T> playerSheetOpen(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = PlayerSheetStiffness,
    )

    fun <T> playerSheetClose(landing: Boolean = false): FiniteAnimationSpec<T> = spring(
        dampingRatio = if (landing) PlayerSheetLandingDamping else Spring.DampingRatioNoBouncy,
        stiffness = PlayerSheetStiffness,
    )

    fun collectionPush(): FiniteAnimationSpec<IntOffset> = spring(
        dampingRatio = 1f,
        stiffness = 380f,
        visibilityThreshold = IntOffset(1, 1),
    )

    fun <T> tabMorph(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.85f, stiffness = 380f)

    fun <T> tabPuck(): FiniteAnimationSpec<T> = spring(dampingRatio = 0.6f, stiffness = 380f)

    fun parallaxOffset(fullWidth: Int): Int = -(fullWidth * PushParallax).toInt()

    fun artworkProgress(fraction: Float): Float {
        if (!fraction.isFinite()) return 1f
        if (fraction <= 0f) return 0f
        if (fraction >= 1f) return 1f
        val position = ArtworkEndpoint * fraction
        return (1f - (1f + position) * exp(-position)) * ArtworkNormalization
    }
}
