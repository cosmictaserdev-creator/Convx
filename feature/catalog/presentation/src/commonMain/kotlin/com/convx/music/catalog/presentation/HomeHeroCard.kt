package com.convx.music.catalog.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle

private val HeroTitleStyle = TextStyle(color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
private val HeroSubtitleStyle = TextStyle(color = Color.White.copy(alpha = 0.75f), fontSize = 15.sp)
private val HeroPillStyle = TextStyle(color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)

/**
 * The "star of the day" card that opens Home: full-bleed artwork, darkened toward the bottom where the title,
 * artist and the Play and Shuffle pills sit. What a platform does differently is a parameter: [artwork] is the
 * picture (Android sizes and animates it), [overlay] sits between the picture and the darkening (Android's frost
 * blur and mesh), [decoration] carries press behaviour and rim, [pillClick] how the pills react to a press.
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
    aspectRatio: Float = 16f / 9f,
    sizing: Modifier = Modifier.heightIn(max = 460.dp).aspectRatio(aspectRatio),
    shape: Shape = ContinuousRoundedRectangle(24.dp),
    cardColor: Color = Color(0xFF2A2A2E),
    decoration: Modifier = Modifier.clickable(onClick = onClick),
    contentPadding: Dp = 24.dp,
    titleStyle: TextStyle = HeroTitleStyle,
    subtitleStyle: TextStyle = HeroSubtitleStyle,
    pillStyle: TextStyle = HeroPillStyle,
    playLabel: String = "Play",
    shuffleLabel: String = "Shuffle",
    playIcon: (@Composable () -> Unit)? = null,
    shuffleIcon: (@Composable () -> Unit)? = null,
    pillClick: Modifier.(onClick: () -> Unit) -> Modifier = { clickable(onClick = it) },
    artwork: @Composable BoxScope.() -> Unit = { RemoteArtwork(thumbnailUrl, Modifier.fillMaxSize()) },
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier
            .fillMaxWidth()
            .then(sizing)
            .clip(shape)
            .background(cardColor)
            .then(decoration),
    ) {
        artwork()
        overlay()
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.35f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.62f)),
            ),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(contentPadding), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BasicText(title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = titleStyle)
            BasicText(subtitle, maxLines = 1, overflow = TextOverflow.Ellipsis, style = subtitleStyle)
            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroPill(playLabel, pillStyle, playIcon, pillClick, onPlay)
                HeroPill(shuffleLabel, pillStyle, shuffleIcon, pillClick, onShuffle)
            }
        }
    }
}

@Composable
private fun HeroPill(
    label: String,
    style: TextStyle,
    icon: (@Composable () -> Unit)?,
    pillClick: Modifier.(onClick: () -> Unit) -> Modifier,
    onClick: () -> Unit,
) {
    Row(
        Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.22f)).pillClick(onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        icon?.invoke()
        BasicText(label, style = style, maxLines = 1)
    }
}
