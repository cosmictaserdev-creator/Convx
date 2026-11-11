/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors

 */

package com.convx.music.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.convx.music.ui.ios.foundation.IosTheme

/** How long one highlight sweep takes to cross a placeholder. */
private const val SHIMMER_PERIOD_MS = 1400

private val BlockShape = RoundedCornerShape(6.dp)
private val LineShape = RoundedCornerShape(4.dp)

// Ragged widths, so a run of rows reads as text rather than as a barcode.
private val TitleWidths = listOf(0.68f, 0.46f, 0.58f, 0.74f, 0.52f)
private val SubtitleWidths = listOf(0.34f, 0.44f, 0.27f, 0.38f, 0.31f)

/**
 * One placeholder block, with a highlight sweeping across it.
 *
 * The sweep is read inside the draw block rather than the composable body: a
 * screenful of these would otherwise recompose on every animation frame, and
 * all any of them needs per frame is a fresh gradient.
 */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape = BlockShape) {
    val base = IosTheme.colors.secondarySystemGroupedBackground
    val highlight = IosTheme.colors.secondaryLabel
        .copy(alpha = 0.16f)
        .compositeOver(base)
    val sweep = rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_PERIOD_MS, easing = LinearEasing)),
        label = "sweep",
    )
    Box(
        modifier
            .clip(shape)
            .drawWithCache {
                val band = size.width * 0.5f
                val startX = -band + sweep.value * (size.width + band * 2)
                val brush = Brush.horizontalGradient(
                    colors = listOf(base, highlight, base),
                    startX = startX,
                    endX = startX + band,
                )
                onDrawBehind { drawRect(brush) }
            },
    )
}

/** A placeholder for one line of text, sized as a fraction of its parent. */
@Composable
fun ShimmerLine(fraction: Float, height: Dp, modifier: Modifier = Modifier) {
    ShimmerBox(
        modifier = modifier.fillMaxWidth(fraction).height(height),
        shape = LineShape,
    )
}

/**
 * Stands in for one song/related-item row: artwork-sized block plus a
 * title/subtitle pair. Used wherever a list is paging in more results.
 */
@Composable
fun SongRowSkeleton(index: Int = 0, circular: Boolean = false, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBox(Modifier.size(52.dp), if (circular) CircleShape else RoundedCornerShape(8.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.fillMaxWidth()) {
            ShimmerLine(fraction = TitleWidths[index % TitleWidths.size], height = 14.dp)
            Spacer(Modifier.height(7.dp))
            ShimmerLine(fraction = SubtitleWidths[index % SubtitleWidths.size], height = 11.dp)
        }
    }
}

/** A couple of ragged text-line placeholders, for a paragraph still loading. */
@Composable
fun TextBlockSkeleton(lines: Int = 3, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        repeat(lines) { index ->
            if (index > 0) Spacer(Modifier.height(8.dp))
            ShimmerLine(
                fraction = if (index == lines - 1) SubtitleWidths[index % SubtitleWidths.size] else TitleWidths[index % TitleWidths.size],
                height = 13.dp,
            )
        }
    }
}
