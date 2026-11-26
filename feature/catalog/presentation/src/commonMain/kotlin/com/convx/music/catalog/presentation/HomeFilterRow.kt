package com.convx.music.catalog.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

/** Chip fill, label and rim. Android passes its glass tokens, desktop passes its palette, so one row serves both. */
class HomeChipColors(
    val container: Color,
    val selectedContainer: Color,
    val label: Color,
    val selectedLabel: Color,
    val border: Color,
)

/** The pill row under Home's title: the unselected chip is a small rounded rect, the selected one grows into a full pill. */
@Composable
fun <T> HomeFilterRow(
    chips: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    colors: HomeChipColors,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    gutter: Dp = 20.dp,
    /** Width of a floating side bar the chips may scroll under; the first chip still starts clear of it. */
    sideInset: Dp = 0.dp,
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp),
    ) {
        Spacer(Modifier.width(gutter + sideInset))
        chips.forEach { (value, label) ->
            val isSelected = value == selected
            val corner by animateDpAsState(
                if (isSelected) 100.dp else 8.dp,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                label = "homeChipCorner",
            )
            // The bouncy spring overshoots past the 8dp target; a negative corner throws.
            val shape = RoundedCornerShape(corner.coerceAtLeast(0.dp))
            Box(
                Modifier
                    .animateContentSize(spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
                    .clip(shape)
                    .background(if (isSelected) colors.selectedContainer else colors.container)
                    .border(BorderStroke(1.dp, colors.border), shape)
                    .clickable(role = Role.Button) { onSelect(value) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    label,
                    style = TextStyle(
                        color = if (isSelected) colors.selectedLabel else colors.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

/**
 * The image YouTube Music pairs with a chip, pinned to the top and fading through [tone]
 * into the page. [scrolledPx] fades it out and drifts it up as the feed scrolls.
 */
@Composable
fun HomeChipBackdrop(url: String?, tone: Color, fadeTo: Color, scrolledPx: () -> Float, modifier: Modifier = Modifier) {
    Crossfade(targetState = url, label = "homeChipBackdrop") { current ->
        if (current == null) return@Crossfade
        // Light pages keep dark text, so the veil leans toward the page colour to stay readable.
        val veil = if (fadeTo.luminance() < 0.5f) tone else lerp(tone, fadeTo, 0.6f)
        Box(
            modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .graphicsLayer {
                    val scrolled = scrolledPx()
                    alpha = (1f - scrolled / size.height).coerceIn(0f, 1f)
                    translationY = -scrolled / 2f
                },
        ) {
            AsyncImage(
                model = current,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.matchParentSize(),
            )
            Box(
                Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        0f to veil.copy(alpha = 0.2f),
                        0.45f to veil.copy(alpha = 0.55f),
                        0.75f to veil,
                        1f to fadeTo,
                    ),
                ),
            )
        }
    }
}
