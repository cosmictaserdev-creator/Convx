package com.convx.music.catalog.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle

val SpotlightCardArtworkSize = 240.dp
val SpotlightCardInfoHeight = 70.dp
val SpotlightCardSpacing = 12.dp

/** Width/height above which artwork counts as wide (a video frame) and is shown whole instead of cropped. */
private const val WideArtRatio = 1.15f

private const val HeroCardFraction = 0.70f
private val HeroCardMaxWidth = 320.dp

/** Each card is 70% of the row wide (at most 320dp) so the next one peeks in. */
fun spotlightCardWidth(available: Dp): Dp = minOf(available * HeroCardFraction, HeroCardMaxWidth)

/**
 * Artwork card with the title and subtitle in white over a strip below it. Everything a platform
 * does differently is a parameter: [decoration] carries the press behaviour and rim, [reflection]
 * the blurred mirror under the title (Android only), [wideBackdrop] the blurred fill behind wide art.
 */
@Composable
fun SpotlightCard(
    thumbnailUrl: String?,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    artworkSize: Dp = SpotlightCardArtworkSize,
    infoHeight: Dp = SpotlightCardInfoHeight,
    cardColor: Color = Color(0xFF2A2A2E),
    shape: Shape = ContinuousRoundedRectangle(20.dp),
    decoration: Modifier = Modifier,
    /** A 2x2 collage of these instead of one image, for chart branding. No reflection in that case. */
    collageUrls: List<String> = emptyList(),
    reflection: (@Composable BoxScope.() -> Unit)? = null,
    wideBackdrop: (@Composable BoxScope.() -> Unit)? = null,
) {
    val isCollage = collageUrls.size >= 2
    Box(
        modifier
            .width(artworkSize)
            .height(artworkSize + infoHeight)
            .clip(shape)
            .background(cardColor)
            .then(decoration),
    ) {
        if (isCollage) {
            ChartCollageArt(collageUrls, Modifier.align(Alignment.TopStart).size(artworkSize))
        } else {
            // A 16:9 frame centre-cropped into a square loses over 40% of the image, so wide art
            // is shown whole over a blurred copy of itself. Square album art is unchanged.
            var wideArt by remember(thumbnailUrl) { mutableStateOf(false) }
            if (wideArt) wideBackdrop?.invoke(this)
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = if (wideArt) ContentScale.Fit else ContentScale.Crop,
                onSuccess = { state -> wideArt = state.result.image.let { it.width > it.height * WideArtRatio } },
                modifier = Modifier.align(Alignment.TopStart).size(artworkSize),
            )
            reflection?.invoke(this)
        }
        Column(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            BasicText(
                title,
                style = TextStyle(Color.White, 17.sp, FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                subtitle,
                style = TextStyle(Color.White.copy(alpha = 0.68f), 13.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChartCollageArt(urls: List<String>, modifier: Modifier = Modifier) {
    val cells = remember(urls) { List(4) { i -> urls.getOrNull(i) ?: urls.last() } }
    Column(modifier) {
        for (row in 0..1) Row(Modifier.weight(1f)) {
            for (col in 0..1) AsyncImage(cells[row * 2 + col], null, Modifier.weight(1f).fillMaxSize(), contentScale = ContentScale.Crop)
        }
    }
}

/** A sideways row of [SpotlightCard]s; [content] gets the card width to hand to each card's `artworkSize`. */
@Composable
fun SpotlightRow(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    spacing: Dp = SpotlightCardSpacing,
    infoHeight: Dp = SpotlightCardInfoHeight,
    rowModifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.(cardWidth: Dp) -> Unit,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val cardWidth = spotlightCardWidth(maxWidth)
        LazyRow(
            state = state,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = rowModifier.fillMaxWidth().height(cardWidth + infoHeight),
        ) { content(cardWidth) }
    }
}
