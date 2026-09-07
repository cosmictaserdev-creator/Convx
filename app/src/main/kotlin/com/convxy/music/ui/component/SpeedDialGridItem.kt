package com.convxy.music.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.innertube.models.YTItem
import com.convxy.music.R
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.ui.theme.AppleTokens

@Composable
fun SpeedDialGridItem(
    item: YTItem,
    isPinned: Boolean,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    thumbnailSizePx: Int = 544,
    cornerRadiusDp: Int = 0,
) {
    // Same rounded-rectangle artwork every other tile in the app uses, with the
    // caption underneath rather than text burned into a scrim. These were circles,
    // which made Speed Dial the one shelf on Home with its own corner vocabulary.
    val shape =
        if (cornerRadiusDp > 0) ContinuousRoundedRectangle(cornerRadiusDp.dp) else ThumbnailRoundedShape
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(shape)
        ) {
            ItemThumbnail(
                thumbnailUrl = item.thumbnail,
                isActive = isActive,
                isPlaying = isPlaying,
                shape = shape,
                modifier = Modifier.fillMaxSize(),
                targetSizePx = thumbnailSizePx,
                // Always fill the tile edge-to-edge, like Apple Music's browse tiles —
                // independent of the user's general CropAlbumArtKey preference, which
                // otherwise defaults to Fit and left the art visibly inset/letterboxed.
                forceContentScale = ContentScale.Crop,
                // No static paused-play glyph on the tile — just the animated bars
                // while it's actually playing.
                showPausedPlayIcon = false,
            )

            if (isPinned) {
                Icon(
                    painter = painterResource(R.drawable.ic_push_pin),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppleTokens.ItemGap / 2)
                        .size(16.dp)
                )
            }
        }

        Text(
            text = item.title,
            fontSize = AppleTokens.Caption,
            lineHeight = AppleTokens.CaptionLineHeight,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = AppleTokens.TextGap * 3)
                .fillMaxWidth(),
        )
    }
}
