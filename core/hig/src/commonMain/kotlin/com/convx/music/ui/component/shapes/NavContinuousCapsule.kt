/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component.shapes

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs

/**
 * Apple's continuous (G2) capsule, for the nav bar only (user ask).
 *
 * App-wide shapes emit a plain rrect because a path outline costs a CPU mask + upload
 * whenever a clipped surface moves (see [ContinuousRoundedRectangle]). The bar's pills are
 * the exception the user asked for; they are few, and static except during their own
 * transitions. A square (the search / inline tab circle) stays a TRUE circle, as on iOS --
 * a G2 capsule of a square would read as a squircle -- and stays on the cheap rrect path.
 *
 * Keep the number of clipped surfaces small; measure additional clips during scroll.
 * the bar ever grows more clipped surfaces.
 */
@Immutable
object NavContinuousCapsule : CornerBasedShape(
    topStart = CornerSize(50),
    topEnd = CornerSize(50),
    bottomEnd = CornerSize(50),
    bottomStart = CornerSize(50),
) {
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline {
        val radius = size.minDimension / 2f
        if (abs(size.width - size.height) < 1f || radius <= 0f) {
            return Outline.Rounded(RoundRect(0f, 0f, size.width, size.height, CornerRadius(radius, radius)))
        }
        return Continuity.Default.createCapsuleOutline(size)
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize,
    ): CornerBasedShape = this
}

private val CircularCapsule = RoundedCornerShape(50)

/**
 * Outline for a pill's elevation shadow. HWUI draws an rrect shadow analytically, but a path
 * outline goes through Skia's shadow utils, which rasterized the blurred G2 capsule on the CPU
 * and uploaded it on every frame the pill resized (measured: 785x163 / 969x142 uploads per
 * frame through bar collapse, tab switch and menu open). Under a 6-12dp blur the G2 and
 * circular ends are indistinguishable.
 */
val Shape.shadowShape: Shape get() = if (this === NavContinuousCapsule) CircularCapsule else this
