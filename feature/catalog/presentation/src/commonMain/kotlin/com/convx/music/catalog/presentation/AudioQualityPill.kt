package com.convx.music.catalog.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.playback.AudioQuality
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle

private val WaveHeights = floatArrayOf(0.4f, 0.8f, 1f, 0.6f, 0.9f, 0.5f, 0.3f)

/** The track's audio quality as a small tinted pill; lossless tiers lead with a waveform glyph. */
@Composable
fun AudioQualityPill(quality: AudioQuality, tint: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(ContinuousRoundedRectangle(5.dp))
            .background(tint.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (quality.lossless) {
            Canvas(Modifier.width(15.dp).height(8.dp)) {
                val barWidth = size.width / (WaveHeights.size * 2f - 1f)
                WaveHeights.forEachIndexed { index, fraction ->
                    val barHeight = size.height * fraction
                    drawRoundRect(
                        color = tint,
                        topLeft = Offset(index * barWidth * 2f, (size.height - barHeight) / 2f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(barWidth / 2f),
                    )
                }
            }
        }
        BasicText(
            text = quality.label,
            style = TextStyle(color = tint, fontSize = 10.sp, lineHeight = 12.sp),
            maxLines = 1,
        )
    }
}
