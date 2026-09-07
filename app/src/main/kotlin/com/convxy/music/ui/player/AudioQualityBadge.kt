package com.convxy.music.ui.player

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.media3.exoplayer.ExoPlayer
import com.convxy.music.R
import com.convxy.music.playback.PlayerConnection

@Composable
fun AudioQualityBadge(
    playerConnection: PlayerConnection,
    tint: Color,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)

    val activePlayer by playerConnection.service.playerFlow.collectAsState()
    val playerFormat = remember(activePlayer) {
        (activePlayer as? ExoPlayer)?.audioFormat
    }

    val isLosslessStream = currentFormat?.mimeType?.contains("flac", ignoreCase = true) == true ||
            playerFormat?.sampleMimeType?.contains("flac", ignoreCase = true) == true

    val animatedRotation = if (isPlaying) {
        val infiniteTransition = rememberInfiniteTransition(label = "QualityIconTransition")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "QualityIconRotation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val iconBrush = Brush.sweepGradient(
        colors = listOf(
            Color.Transparent,
            tint.copy(alpha = 1.0f),
            Color.Transparent
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(R.drawable.stream_old_player),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(12.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        rotate(animatedRotation.value) {
                            drawRect(iconBrush, blendMode = BlendMode.SrcIn)
                        }
                    }
                }
        )
        Text(
            text = run {
                val format = playerFormat ?: currentFormat?.let {
                    androidx.media3.common.Format.Builder()
                        .setSampleMimeType(it.mimeType)
                        .setCodecs(it.codecs)
                        .setAverageBitrate(it.bitrate ?: 0)
                        .setSampleRate(it.sampleRate ?: 0)
                        .build()
                }

                val codecLabel = format?.let { f ->
                    val mime = f.sampleMimeType?.lowercase() ?: ""
                    val codecs = f.codecs?.lowercase() ?: ""
                    when {
                        mime.contains("flac") || codecs.contains("flac") -> "FLAC"
                        mime.contains("opus") || codecs.contains("opus") -> "OPUS"
                        mime.contains("mp4a") || mime.contains("aac") || codecs.contains("mp4a") || codecs.contains("aac") -> "AAC"
                        mime.contains("mp3") || mime.contains("mpeg") || codecs.contains("mp3") || codecs.contains("mpeg") -> "MP3"
                        mime.contains("vorbis") || codecs.contains("vorbis") -> "OGG"
                        mime.contains("webm") || codecs.contains("webm") -> "WEBM"
                        else -> null
                    }
                }

                val bitrate = format?.bitrate?.takeIf { it > 0 } ?: format?.averageBitrate?.takeIf { it > 0 }
                val bitrateLabel = bitrate?.let { "${it / 1000}kbps" }

                val sampleRate = format?.sampleRate?.takeIf { it > 0 }
                val sampleRateLabel = sampleRate?.let { "${it / 1000}.${(it % 1000) / 100}kHz" }

                buildString {
                    append(codecLabel ?: if (isLosslessStream) "LOSSLESS" else "AUTO")
                    if (bitrateLabel != null) append(" • $bitrateLabel")
                    if (sampleRateLabel != null) append(" • $sampleRateLabel")
                }
            },
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.0.sp
            ),
            color = tint.copy(alpha = 0.8f),
            maxLines = 1,
        )
    }
}
