/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.music.innertube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.music.innertube.models.MediaInfo
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.utils.LocalAudioProperties
import com.convxy.music.utils.readLocalAudioProperties
import com.convxy.music.db.entities.FormatEntity
import com.convxy.music.db.entities.Song
import com.convxy.music.ui.component.LocalBottomSheetPageState
import com.convxy.music.ui.component.shimmer.ShimmerHost
import com.convxy.music.ui.component.shimmer.TextPlaceholder

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShowMediaInfo(videoId: String) {
    if (videoId.isBlank() || videoId.isEmpty()) return

    val windowInsets = WindowInsets.systemBars
    var info by remember { mutableStateOf<MediaInfo?>(null) }
    val database = LocalDatabase.current
    var song by remember { mutableStateOf<Song?>(null) }
    var currentFormat by remember { mutableStateOf<FormatEntity?>(null) }
    val playerConnection = LocalPlayerConnection.current
    val context = LocalContext.current
    val sheetState = LocalBottomSheetPageState.current
    val clipboardManager = LocalClipboard.current

    LaunchedEffect(Unit, videoId) {
        info = YouTube.getMediaInfo(videoId).getOrNull()
    }
    LaunchedEffect(Unit, videoId) {
        database.song(videoId).collect { song = it }
    }
    LaunchedEffect(Unit, videoId) {
        database.format(videoId).collect { currentFormat = it }
    }

    // Local songs never get a FormatEntity row — only the YouTube playback/download path
    // writes one — so contentLength was always null for them and the field read "N/A".
    // A local song's id IS its MediaStore content URI, so the size can just be read off
    // the file. Done on demand rather than stored: no schema change, and it stays right
    // if the file is replaced.
    var localFileSize by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(song?.song?.isLocal, videoId) {
        localFileSize = if (song?.song?.isLocal != true) {
            null
        } else {
            withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver
                        .openAssetFileDescriptor(videoId.toUri(), "r")
                        ?.use { it.length }
                        ?.takeIf { it >= 0 }
                }.getOrNull()
            }
        }
    }

    // Same reasoning as localFileSize above: only the YouTube playback and download
    // paths write a FormatEntity, so for an on-device file every one of the format rows
    // below (codec, bitrate, sample rate) read "N/A". These come off the file itself.
    var localProperties by remember { mutableStateOf<LocalAudioProperties?>(null) }
    LaunchedEffect(song?.song?.isLocal, videoId) {
        localProperties = if (song?.song?.isLocal != true) {
            null
        } else {
            readLocalAudioProperties(context, videoId)
        }
    }

    // Shapes
    val albumArtShape = RoundedCornerShape(24.dp)

    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = windowInsets.asPaddingValues().calculateBottomPadding())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header: "Song Info" title + Done button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.song_info),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { sheetState.dismiss() }) {
                    Text(stringResource(R.string.done))
                }
            }
        }

        // Large Album Art Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(albumArtShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val imageUrl = song?.thumbnailUrl
                    ?: "https://i.ytimg.com/vi/$videoId/maxresdefault.jpg"

                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Info Grid
        if (song != null || info != null) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Row 1: Title / Artist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.song_info_title_label),
                            value = song?.title ?: info?.title ?: stringResource(R.string.unknown),
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.artist),
                            value = song?.artists?.joinToString { it.name }
                                ?: info?.author
                                ?: stringResource(R.string.unknown),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Duration / Media ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val duration = song?.song?.duration?.let { totalSeconds ->
                            val minutes = totalSeconds / 60
                            val seconds = totalSeconds % 60
                            "%d:%02d".format(minutes, seconds)
                        } ?: stringResource(R.string.unknown)
                        InfoItem(
                            label = stringResource(R.string.song_info_duration_label),
                            value = duration,
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.media_id),
                            value = song?.id ?: videoId,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Views / Likes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val viewCount = info?.viewCount?.toInt()?.let { shortNumberFormatter(it) } ?: "N/A"
                        InfoItem(
                            label = stringResource(R.string.views),
                            value = stringResource(R.string.song_info_views_count, viewCount),
                            modifier = Modifier.weight(1f)
                        )
                        val likeCount = info?.like?.toInt()?.let { shortNumberFormatter(it) } ?: "N/A"
                        InfoItem(
                            label = stringResource(R.string.likes),
                            value = stringResource(R.string.song_info_likes_count, likeCount),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 4: Dislikes / Subscribers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.dislikes),
                            value = info?.dislike?.let { shortNumberFormatter(it.toInt()) } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.subscribers),
                            value = info?.subscribers ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 5: Itag / Loudness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.song_info_itag_label),
                            value = currentFormat?.itag?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.loudness),
                            value = currentFormat?.loudnessDb?.let { "$it dB" } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 6: Format / Bitrate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.mime_type),
                            value = currentFormat?.mimeType?.substringBefore(";")
                                ?: localProperties?.mimeType
                                ?: stringResource(R.string.song_info_standard),
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.bitrate),
                            value = (currentFormat?.bitrate ?: localProperties?.bitrateBps)
                                ?.let { "${it / 1000} Kbps" } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 7: Codecs / Sample Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.codecs),
                            // A local file has no "codecs" string of its own, so the
                            // container subtype stands in for it, with the bit depth
                            // appended when the format declares one.
                            value = currentFormat?.codecs
                                ?: localProperties?.let { props ->
                                    val codec = props.mimeType?.substringAfterLast('/')?.uppercase()
                                    val depth = props.bitsPerSample?.let { "${'$'}it-bit " }.orEmpty()
                                    codec?.let { depth + it }
                                }
                                ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.sample_rate),
                            value = (currentFormat?.sampleRate ?: localProperties?.sampleRateHz)
                                ?.let { rate ->
                                    val channels = localProperties?.channelCount
                                        ?.let { " \u00b7 ${it}ch" }.orEmpty()
                                    "$rate Hz$channels"
                                } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 8: File Size / Volume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = stringResource(R.string.file_size),
                            value = (localFileSize ?: currentFormat?.contentLength)?.let {
                                Formatter.formatShortFileSize(context, it)
                            } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        InfoItem(
                            label = stringResource(R.string.volume),
                            value = if (playerConnection != null)
                                "${(playerConnection.player.volume * 100).toInt()}%"
                            else "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Description (full width at bottom)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.description),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (info == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    } else {
                        Text(
                            text = info?.description ?: stringResource(R.string.song_info_no_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

        } else {
            // Loading state before song/info data arrives
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ShimmerHost {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextPlaceholder()
                            TextPlaceholder()
                            TextPlaceholder()
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(end = 8.dp)
            .clickable {
                val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText(label, value))
                Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
            },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

fun shortNumberFormatter(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 1_000_000 -> String.format("%.1fk", count / 1000.0)
        else -> String.format("%.1fM", count / 1_000_000.0)
    }
}
