package com.convx.music.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convx.music.constants.PlayerArtworkStyle
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle

/** How a cover joins a page change: its bounds travel into the header, or both dissolve in place. */
internal enum class CoverMorph { SHARED, FADE }

/**
 * One cover presentation (card, vinyl, clover, anything added later). The cover, the page header's
 * small cover and the transition between them all read the style, so a new look needs only a new
 * implementation here and is animated like the rest: pick [morph] and the page change does the work.
 */
@Stable
internal interface PlayerCoverStyle {
    val morph: CoverMorph

    /** Turns while playing. */
    val spins: Boolean

    /** The art turns with the shape (a record's label); false keeps the art upright under a turning shape. */
    val artSpinsWithShape: Boolean

    @Composable
    fun shape(cornerRadius: Dp): Shape

    /** Drawn over the art, e.g. a record's grooves. */
    @Composable
    fun Decoration(modifier: Modifier)
}

internal object CardCoverStyle : PlayerCoverStyle {
    override val morph = CoverMorph.SHARED
    override val spins = false
    override val artSpinsWithShape = true
    @Composable
    override fun shape(cornerRadius: Dp): Shape = ContinuousRoundedRectangle(cornerRadius)

    @Composable
    override fun Decoration(modifier: Modifier) = Unit
}

internal object VinylCoverStyle : PlayerCoverStyle {
    override val morph = CoverMorph.SHARED
    override val spins = true
    override val artSpinsWithShape = true
    @Composable
    override fun shape(cornerRadius: Dp): Shape = CircleShape

    @Composable
    override fun Decoration(modifier: Modifier) {
        Canvas(modifier) {
            val r = size.minDimension / 2f
            val c = Offset(size.width / 2f, size.height / 2f)
            // Black record body drawn as a ring, so the art stays visible in the middle as the paper label.
            drawCircle(
                color = Color.Black.copy(alpha = 0.82f),
                radius = r * 0.67f,
                center = c,
                style = Stroke(width = r * 0.66f),
            )
            for (i in 1..7) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = r * (0.40f + i * 0.083f),
                    center = c,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
            drawCircle(Color.Black, radius = r * 0.055f, center = c)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal object CloverCoverStyle : PlayerCoverStyle {
    override val morph = CoverMorph.SHARED
    override val spins = true
    override val artSpinsWithShape = false
    @Composable
    override fun shape(cornerRadius: Dp): Shape = MaterialShapes.Clover8Leaf.toShape()

    @Composable
    override fun Decoration(modifier: Modifier) = Unit
}

internal fun PlayerArtworkStyle.coverStyle(): PlayerCoverStyle = when (this) {
    PlayerArtworkStyle.CARD -> CardCoverStyle
    PlayerArtworkStyle.VINYL -> VinylCoverStyle
    PlayerArtworkStyle.CLOVER -> CloverCoverStyle
}

/** Joins the cover to the page header only for styles that travel; the others dissolve in place. */
@Composable
internal fun Modifier.coverMorph(style: PlayerCoverStyle): Modifier =
    if (style.morph == CoverMorph.SHARED) auxiliaryArtworkMorph() else this

/**
 * Shared by the big cover and the header cover so a record keeps turning as it shrinks.
 *
 * The angle comes from the frame clock, which every caller reads, not from a transition each
 * caller starts for itself: two transitions begin at different moments, so swapping the big
 * cover for the header one (or back) jumped the record to a different angle.
 */
@Composable
internal fun rememberCoverRotation(style: PlayerCoverStyle, isPlaying: Boolean): State<Float> =
    if (isPlaying && style.spins) {
        val angle = remember { mutableFloatStateOf(0f) }
        androidx.compose.runtime.LaunchedEffect(Unit) {
            while (true) {
                androidx.compose.runtime.withFrameMillis { frame ->
                    angle.floatValue = (frame % CoverRotationMillis) / CoverRotationMillis.toFloat() * 360f
                }
            }
        }
        angle
    } else {
        remember { mutableFloatStateOf(0f) }
    }

private const val CoverRotationMillis = 20_000L
