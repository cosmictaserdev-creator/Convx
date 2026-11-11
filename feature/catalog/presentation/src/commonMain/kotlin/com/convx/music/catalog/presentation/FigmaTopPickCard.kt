package com.convx.music.catalog.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle

private val CardWidth = 242.dp
private val CardHeight = 323.dp
private val FooterHeight = CardHeight - CardWidth

/**
 * Home's "Top Picks" card: a square cover over a footer that holds the cover's own reflection, frosted,
 * with the title and two detail lines centred in white.
 */
@Composable
fun FigmaTopPickCard(
    thumbnailUrl: String?,
    title: String,
    label: String?,
    detail: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = ContinuousRoundedRectangle(20.dp)
    Box(
        modifier
            .width(CardWidth)
            .height(CardHeight)
            .shadow(20.dp, shape, ambientColor = Color.Black.copy(alpha = 0.16f), spotColor = Color.Black.copy(alpha = 0.16f))
            .clip(shape)
            .background(Color(0xFF2A2A2E))
            .clickable(onClick = onClick),
    ) {
        RemoteArtwork(thumbnailUrl, Modifier.size(CardWidth))
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(FooterHeight).clipToBounds()) {
            // The cover mirrored about its bottom edge, so the footer reads as the cover's reflection.
            RemoteArtwork(
                thumbnailUrl,
                Modifier.size(CardWidth)
                    .graphicsLayer { scaleY = -1f; transformOrigin = TransformOrigin(0.5f, 0f) }
                    .blur(24.dp),
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.22f)))
            Column(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicText(
                    title,
                    style = TextStyle(Color.White, 15.sp, FontWeight.SemiBold, lineHeight = 20.sp, letterSpacing = (-0.23).sp, textAlign = TextAlign.Center),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                listOfNotNull(label, detail).forEach { line ->
                    BasicText(
                        line,
                        style = TextStyle(Color.White, 13.sp, lineHeight = 18.sp, letterSpacing = (-0.08).sp, textAlign = TextAlign.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
