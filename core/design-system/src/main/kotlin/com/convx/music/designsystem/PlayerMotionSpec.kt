package com.convx.music.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/** Motion shared by the player sheet, its artwork, and the mini player dock. */
object PlayerMotionSpec {
    // Keeping damping fixed, spring time scales approximately with the inverse square root of stiffness.
    private const val SheetStiffness = 500f
    private const val SheetLandingDamping = 0.72f
    private const val DockStiffness = 120f
    private const val DockOpeningDamping = 0.95f
    private const val DockClosingDamping = 0.88f
    private const val CornerStiffness = 760f
    private const val LandingStiffness = 194f
    private const val LandingDamping = 0.5f
    private const val PillShadowReturnMillis = 272
    private const val ArtworkEndpointFade = 0.18f
    private const val PosterFadeStart = 0.35f
    private const val PosterFadeEnd = 0.90f

    const val LandingMaxDipDp = 22f
    const val LandingSquash = 0.14f
    const val LandingSpread = 0.04f

    val SeamlessEasing: Easing = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

    fun <T> sheetOpen(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = SheetStiffness,
    )

    fun <T> sheetClose(landing: Boolean): FiniteAnimationSpec<T> = spring(
        dampingRatio = if (landing) SheetLandingDamping else Spring.DampingRatioNoBouncy,
        stiffness = SheetStiffness,
    )

    fun <T> dockTransition(opening: Boolean): FiniteAnimationSpec<T> = spring(
        dampingRatio = if (opening) DockOpeningDamping else DockClosingDamping,
        stiffness = DockStiffness,
    )

    fun <T> screenCorner(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = CornerStiffness,
    )

    fun <T> pillLanding(): FiniteAnimationSpec<T> = spring(
        dampingRatio = LandingDamping,
        stiffness = LandingStiffness,
    )

    fun <T> pillShadowReturn(): FiniteAnimationSpec<T> =
        tween(PillShadowReturnMillis, easing = SeamlessEasing)

    fun miniArtworkAlpha(progress: Float): Float = 1f - smoothstep(progress, 0f, ArtworkEndpointFade)

    fun fullArtworkAlpha(progress: Float): Float = smoothstep(progress, 1f - ArtworkEndpointFade, 1f)

    fun posterAlpha(progress: Float): Float = smoothstep(progress, PosterFadeStart, PosterFadeEnd)

    private fun smoothstep(progress: Float, start: Float, end: Float): Float {
        val t = ((progress - start) / (end - start)).coerceIn(0f, 1f)
        return t * t * (3f - 2f * t)
    }
}
