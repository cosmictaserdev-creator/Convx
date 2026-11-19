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
    private const val DockOpeningStiffness = 80f
    private const val DockClosingStiffness = 190f
    private const val DockOpeningDamping = 0.96f
    private const val CornerStiffness = 760f
    private const val PillShadowReturnMillis = 272
    private const val ArtworkEndpointFade = 0.18f
    private const val PosterFadeStart = 0.35f
    private const val PosterFadeEnd = 0.90f

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
        dampingRatio = if (opening) DockOpeningDamping else Spring.DampingRatioNoBouncy,
        stiffness = if (opening) DockOpeningStiffness else DockClosingStiffness,
    )

    fun <T> screenCorner(): FiniteAnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = CornerStiffness,
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
