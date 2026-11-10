package com.convx.music.client.desktop

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun DesktopSkeletonBlock(modifier: Modifier) {
    val palette = desktopPalette()
    val reduced = LocalDesktopReducedMotion.current
    val phase = if (reduced) remember { mutableFloatStateOf(-1f) } else {
        rememberInfiniteTransition(label = "Loading shimmer").animateFloat(-1f, 2f,
            infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart), label = "Shimmer sweep")
    }
    Box(modifier.clip(RoundedCornerShape(6.dp)).drawWithCache {
        val width = size.width.coerceAtLeast(1f)
        val brush = Brush.linearGradient(listOf(Color.Transparent, palette.foreground.copy(alpha = .09f), Color.Transparent),
            start = Offset.Zero, end = Offset(width, size.height))
        onDrawBehind {
            drawRect(palette.foreground.copy(alpha = .055f))
            if (!reduced) clipRect {
                translate(left = phase.value * width) { drawRect(brush, size = Size(width, size.height)) }
            }
        }
    })
}

@Composable
internal fun DesktopPageSkeleton() {
    Column(Modifier.fillMaxWidth().semantics { contentDescription = "Loading music" }, verticalArrangement = Arrangement.spacedBy(28.dp)) {
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DesktopSkeletonBlock(Modifier.width(160.dp).height(18.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    items(5) {
                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            DesktopSkeletonBlock(Modifier.size(212.dp))
                            DesktopSkeletonBlock(Modifier.width(142.dp).height(13.dp))
                            DesktopSkeletonBlock(Modifier.width(90.dp).height(11.dp))
                        }
                    }
                }
            }
        }
    }
}
