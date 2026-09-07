/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.rememberArtworkTint
import com.convxy.music.ui.utils.bounceClick

/** Square artwork edge. The card is this wide; one page of the spotlight row. */
val SpotlightCardArtworkSize = 240.dp

/** Strip below the artwork that the title and subtitle sit in. */
val SpotlightCardInfoHeight = 70.dp

/** Total card height, artwork plus the info strip. */
val SpotlightCardHeight = SpotlightCardArtworkSize + SpotlightCardInfoHeight

/** Gap between cards in the spotlight row. */
val SpotlightCardSpacing = 12.dp

/**
 * Reference art darkens the reflection with two black washes at 0.302; one pass at the
 * composited value (1 - 0.698^2) is the same result with one fewer draw.
 */
private const val ReflectionDarken = 0.51f

/** The reflection is a mirror of the art, so it wants more colour, not less. */
private const val ReflectionSaturation = 1.5f

private val ReflectionBlurRadius = 24.dp

/**
 * A tall artwork card whose lower portion is the same artwork mirrored, blurred and
 * darkened, with the title reading over it. The artwork appears to pour off its own
 * bottom edge and the text sits in the pour rather than on a separate label strip.
 *
 * Reflection geometry: the mirror is drawn from halfway down the artwork and revealed by
 * an alpha ramp running from two thirds of the artwork height to its bottom edge, so the
 * top two thirds stay completely clear.
 *
 * Drawn on the GPU from the image already decoded for the artwork — a second AsyncImage
 * flipped in a graphics layer — rather than built on the CPU as a blurred, mesh-warped,
 * disk-cached derivative per track. Coil hands back the same cache entry, so there is no
 * second decode, no cache of our own to maintain, and no bitmap held alive per card.
 *
 * Below API 31 Modifier.blur is a no-op, which would leave a sharp upside-down copy of
 * the artwork. The reflection is dropped there and the tinted card colour carries the
 * text instead.
 */
@Composable
fun SpotlightCard(
    thumbnailUrl: String?,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    artworkSize: Dp = SpotlightCardArtworkSize,
    infoHeight: Dp = SpotlightCardInfoHeight,
) {
    val tints = rememberArtworkTint(thumbnailUrl)
    // Well short of black: the card colour still has to read as the artwork colour
    // where the reflection fades out over it.
    val cardColor = remember(tints) {
        lerp(tints.firstOrNull() ?: Color.Black, Color.Black, 0.48f)
    }
    val reflectionsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Box(
        modifier = modifier
            .width(artworkSize)
            .height(artworkSize + infoHeight)
            .clip(ContinuousRoundedRectangle(AppleTokens.CardCorner))
            .background(cardColor)
            .bounceClick(onClick = onClick),
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(artworkSize),
        )

        if (reflectionsSupported) {
            SpotlightReflection(
                thumbnailUrl = thumbnailUrl,
                artworkSize = artworkSize,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.68f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * The mirrored lower portion. Offscreen compositing is required rather than cosmetic:
 * the alpha ramp is applied with BlendMode.DstIn, which needs a layer of its own to
 * blend against or it would punch through the clear artwork underneath it too.
 */
@Composable
private fun BoxScope.SpotlightReflection(
    thumbnailUrl: String?,
    artworkSize: Dp,
) {
    val saturate = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(ReflectionSaturation) })
    }
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .fillMaxWidth()
            .height(artworkSize)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                // Ramp expressed in artwork heights so it tracks the card at any size:
                // nothing until two thirds down the artwork, fully opaque by its edge.
                val artworkPx = artworkSize.toPx()
                val rampEnd = size.height - artworkPx / 3f
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.3f to Color.White.copy(alpha = 0.3f),
                        0.75f to Color.White.copy(alpha = 0.8f),
                        1f to Color.White,
                        startY = (rampEnd - artworkPx / 3f).coerceAtLeast(0f),
                        endY = rampEnd,
                    ),
                    blendMode = BlendMode.DstIn,
                )
            },
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = saturate,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleY = -1f }
                .blur(ReflectionBlurRadius),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = ReflectionDarken)),
        )
    }
}
