/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.os.Build
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.convxy.music.R
import com.convxy.music.constants.HomeCardCornerRadiusOverrideKey
import com.convxy.music.constants.HomeHeroCardHeightOverrideKey
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.utils.rememberPreference

/**
 * The "star of the day" card that opens Home.
 *
 * The lower band is the artwork itself, blurred and darkened, rather than a flat black
 * scrim — the same frosted treatment [SpotlightCard] uses, so the title sits in the
 * cover's own colour instead of under a grey wash.
 *
 * Still cheap: the blurred band is a second draw of the image Coil already decoded for
 * the sharp one, so there is no extra decode and no backdrop sampling. Home's frame cost
 * is dominated by how many rich rows compose at once, so the hero has to earn its place
 * by replacing several rows of tiles, not by adding to them.
 */
@Composable
fun HomeHeroCard(
    title: String,
    subtitle: String,
    thumbnailUrl: String?,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (heightOverride) = rememberPreference(HomeHeroCardHeightOverrideKey, 0)
    val (cornerOverride) = rememberPreference(HomeCardCornerRadiusOverrideKey, 0)
    val cornerRadius = if (cornerOverride > 0) cornerOverride.dp else AppleTokens.CardCornerLarge
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (heightOverride > 0) Modifier.height(heightOverride.dp) else Modifier.aspectRatio(4f / 3f)
            )
            .clip(ContinuousRoundedRectangle(cornerRadius))
            // Under the artwork, not decoration: a song whose thumbnailUrl is null or
            // fails to load left the card fully transparent, so the section read as
            // "the hero card never rendered" rather than as a card with no cover.
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .bounceClick(onClick = onClick),
    ) {
        // Bound the decode to the card's real on-screen pixels and skip the crossfade:
        // the hero is a full-bleed artwork card, so a full-size decode + fade costs GPU
        // time for zero visual gain on every scroll frame where the card re-enters view.
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val density = LocalDensity.current
            // LocalContext/LocalDensity are composable reads, so they can't live inside
            // remember's calculation lambda — read them here, capture into the key set.
            val request = remember(thumbnailUrl, maxWidth, maxHeight, context, density) {
                with(density) {
                    ImageRequest.Builder(context)
                        .data(thumbnailUrl)
                        .size(maxWidth.roundToPx(), maxHeight.roundToPx())
                        .crossfade(false)
                        .build()
                }
            }
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            // Frosted band over the lower portion: the cover again, blurred, revealed by
            // an alpha ramp so it fades in rather than starting at a hard edge. Offscreen
            // compositing is required, not cosmetic — the ramp is applied with DstIn,
            // which needs its own layer to blend against or it would punch through the
            // sharp artwork underneath as well.
            //
            // Inside this BoxWithConstraints so it can share `request` directly. Hoisting
            // the request out through a state write would be a write during composition,
            // which is how you get a recomposition loop.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0.3f to Color.Transparent,
                                    0.55f to Color.White.copy(alpha = 0.65f),
                                    0.75f to Color.White,
                                ),
                                blendMode = BlendMode.DstIn,
                            )
                        },
                ) {
                    AsyncImage(
                        model = request,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(HeroFrostBlurRadius),
                    )
                }
            }
        }

        // Darkening pass, kept separate from the frost so the text keeps its contrast on
        // a bright cover, and so pre-API-31 (where Modifier.blur no-ops) still gets a
        // legible title from the gradient alone.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.62f),
                    )
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(AppleTokens.Gutter),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppleTokens.ItemGap),
                modifier = Modifier.padding(top = AppleTokens.ItemGap),
            ) {
                HeroPill(
                    iconRes = R.drawable.play,
                    label = stringResource(R.string.play),
                    onClick = onPlay,
                )
                HeroPill(
                    iconRes = R.drawable.shuffle,
                    label = stringResource(R.string.shuffle),
                    onClick = onShuffle,
                )
            }
        }
    }
}

@Composable
private fun HeroPill(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.22f))
            .bounceClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            maxLines = 1,
        )
    }
}

/** Radius of the hero's frosted lower band. Matches [SpotlightCard]'s reflection. */
private val HeroFrostBlurRadius = 24.dp
