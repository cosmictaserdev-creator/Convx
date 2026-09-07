/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * 
 * Optimized for minimal recomposition during navigation
 */

package com.convxy.music.ui.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.Download.STATE_COMPLETED
import androidx.media3.exoplayer.offline.Download.STATE_DOWNLOADING
import androidx.media3.exoplayer.offline.Download.STATE_QUEUED
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.request.crossfade
import coil3.size.Size as CoilSize
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalDownloadUtil
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.GridItemSize
import com.convxy.music.constants.GridThumbnailHeight
import com.convxy.music.constants.ListItemHeight
import com.convxy.music.constants.ListThumbnailSize
import com.convxy.music.constants.SmallGridThumbnailHeight
import com.convxy.music.constants.ThumbnailCornerRadius
import com.convxy.music.constants.ThumbnailRoundedShape
import com.convxy.music.db.entities.Album
import com.convxy.music.db.entities.Artist
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.Song
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.MediaMetadata
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.marqueeWhenVisible
import com.convxy.music.ui.utils.rememberGridSpacing
import com.convxy.music.ui.utils.resize
import com.convxy.music.ui.utils.sharedArtworkSource
import com.convxy.music.utils.joinByBullet
import com.convxy.music.utils.makeTimeString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

const val ActiveBoxAlpha = 0.6f

@Composable
fun currentGridThumbnailHeight(): Dp {
    val prefs = LocalItemPrefs.current
    if (prefs.gridCardHeightOverrideDp > 0) return prefs.gridCardHeightOverrideDp.dp
    return if (prefs.gridItemSize == GridItemSize.BIG) GridThumbnailHeight else SmallGridThumbnailHeight
}

// Basic list item - optimized with inline to reduce recomposition
@Composable
inline fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    noinline subtitle: (@Composable RowScope.() -> Unit)? = null,
    thumbnailContent: @Composable () -> Unit,
    crossinline trailingContent: @Composable RowScope.() -> Unit = {},
    isSelected: Boolean? = false,
    isActive: Boolean = false,
    isAvailable: Boolean = true,
    shape: Shape = RectangleShape,
    drawHighlight: Boolean = true,
    flat: Boolean = false,
) {
    val onSurface = LocalContentColor.current
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .height(ListItemHeight)
                .padding(horizontal = AppleTokens.Gutter)
                .clip(if (flat) RectangleShape else shape)
                .background(
                    color = when {
                        isActive -> onSurface.copy(alpha = 0.15f)
                        isSelected == true && drawHighlight -> onSurface.copy(alpha = 0.1f)
                        else -> Color.Transparent
                    }
                )
        ) {
            Box(
                modifier = Modifier.padding(start = 12.dp, top = 6.dp, end = 6.dp, bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                thumbnailContent()
                if (!isAvailable) {
                    Box(
                        modifier = Modifier
                            .size(ListThumbnailSize)
                            .align(Alignment.Center)
                            .background(
                                Color.Black.copy(alpha = 0.25f),
                                ThumbnailRoundedShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.offline),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(ListThumbnailSize / 2)
                                .align(Alignment.Center)
                                .graphicsLayer { alpha = 1f }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            ) {
                Text(
                    text = title,
                    fontSize = AppleTokens.ItemTitle,
                    lineHeight = AppleTokens.ItemTitleLineHeight,
                    color = onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (subtitle != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Metadata is a fixed grey, not an alpha of the title colour:
                        // over artwork-tinted screens an alpha step reads as a faded
                        // version of the tint rather than as a second tier.
                        CompositionLocalProvider(LocalContentColor provides AppleTokens.Metadata) {
                            subtitle()
                        }
                    }
                }
            }

            CompositionLocalProvider(LocalContentColor provides onSurface.copy(alpha = 0.7f)) {
                trailingContent()
            }
        }
        if (flat) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = AppleTokens.Gutter),
                color = AppleTokens.divider,
                thickness = 0.5.dp,
            )
        }
    }
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: AnnotatedString?,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailContent: @Composable () -> Unit,
    trailingContent: @Composable RowScope.() -> Unit = {},
    isSelected: Boolean? = false,
    isActive: Boolean = false,
    shape: Shape = RectangleShape,
    drawHighlight: Boolean = true,
    flat: Boolean = false,
) = ListItem(
    title = title,
    subtitle = {
        badges()
        if (subtitle != null) {
            // Colour comes from the 0.6-alpha LocalContentColor the base
            // ListItem already provides around its subtitle slot.
            Text(
                text = subtitle,
                fontSize = AppleTokens.ItemSubtitle,
                lineHeight = AppleTokens.ItemSubtitleLineHeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    },
    thumbnailContent = thumbnailContent,
    trailingContent = trailingContent,
    modifier = modifier,
    isSelected = isSelected,
    isActive = isActive,
    shape = shape,
    drawHighlight = drawHighlight,
    flat = flat
)

// merge badges and subtitle text and pass to basic list item
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String?,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailContent: @Composable () -> Unit,
    trailingContent: @Composable RowScope.() -> Unit = {},
    isSelected: Boolean? = false,
    isActive: Boolean = false,
    shape: Shape = RectangleShape,
    drawHighlight: Boolean = true,
    flat: Boolean = false,
) = ListItem(
    title = title,
    subtitle = {
        badges()

        if (!subtitle.isNullOrEmpty()) {
            // Colour inherited — see the AnnotatedString overload above.
            Text(
                text = subtitle,
                fontSize = AppleTokens.ItemSubtitle,
                lineHeight = AppleTokens.ItemSubtitleLineHeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    },
    thumbnailContent = thumbnailContent,
    trailingContent = trailingContent,
    modifier = modifier,
    isSelected = isSelected,
    isActive = isActive,
    shape = shape,
    drawHighlight = drawHighlight,
    flat = flat
)

@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    subtitle: @Composable () -> Unit,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailContent: @Composable BoxWithConstraintsScope.() -> Unit,
    thumbnailRatio: Float = 1f,
    fillMaxWidth: Boolean = false,
    // Artists are round and centred; everything else is square and left-aligned.
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val gridHeight = currentGridThumbnailHeight()
    val gridSpacing = rememberGridSpacing()

    val sizeModifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier.width(gridHeight * thumbnailRatio)

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .then(sizeModifier)
            // The tile owns half the gap; the grid's contentPadding owns the
            // other half at the screen edge. Together: AppleTokens.Gutter at the
            // edge, gridSpacing between neighbours.
            .padding(gridSpacing / 2)
    ) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = if (fillMaxWidth) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.height(gridHeight)
            }
                .aspectRatio(thumbnailRatio)
                .clip(ThumbnailRoundedShape)
        ) {
            thumbnailContent()
        }

        Spacer(modifier = Modifier.height(gridSpacing / 2))

        title()

        Row(verticalAlignment = Alignment.CenterVertically) {
            badges()

            subtitle()
        }
    }
}

@Composable
fun GridItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailContent: @Composable BoxWithConstraintsScope.() -> Unit,
    thumbnailRatio: Float = 1f,
    fillMaxWidth: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) = GridItem(
    modifier = modifier,
    title = {
        Text(
            text = title,
            fontSize = AppleTokens.ItemTitle,
            lineHeight = AppleTokens.ItemTitleLineHeight,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) {
                TextAlign.Center
            } else {
                TextAlign.Start
            },
            modifier = Modifier.fillMaxWidth()
        )
    },
    subtitle = {
        Text(
            text = subtitle,
            fontSize = AppleTokens.ItemSubtitle,
            lineHeight = AppleTokens.ItemSubtitleLineHeight,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
    thumbnailContent = thumbnailContent,
    thumbnailRatio = thumbnailRatio,
    fillMaxWidth = fillMaxWidth,
    horizontalAlignment = horizontalAlignment,
)

@Composable
fun SongListItem(
    song: Song,
    modifier: Modifier = Modifier,
    albumIndex: Int? = null,
    showLikedIcon: Boolean = true,
    showInLibraryIcon: Boolean = false,
    showDownloadIcon: Boolean = true,
    badges: @Composable RowScope.() -> Unit = {
        if (showLikedIcon && song.song.liked) {
            Icon.Favorite()
        }
        if (song.song.explicit) {
            Icon.Explicit()
        }
        if (showInLibraryIcon && song.song.inLibrary != null) {
            Icon.Library()
        }
        if (showDownloadIcon) {
            // One hoisted map, no per-row Flow and no per-row collector. See
            // [LocalDownloads] for what this replaced.
            Icon.Download(LocalDownloads.current[song.song.id]?.state)
        }
    },
    isSelected: Boolean = false,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    isSwipeable: Boolean = true,
    onSelectionChange: (Boolean) -> Unit = {},
    trailingContent: @Composable RowScope.() -> Unit = {},
    drawHighlight: Boolean = true,
    shape: Shape = RectangleShape,
    flat: Boolean = false,
    showIconOnly: Boolean = false,
) {
    val swipeEnabled = LocalItemPrefs.current.swipeToSong

    val content: @Composable () -> Unit = {
        ListItem(
            title = song.song.title,
            subtitle = joinByBullet(
                song.artists.joinToString { it.name },
                makeTimeString(song.song.duration * 1000L)
            ),
            badges = badges,
            thumbnailContent = {
                if (showIconOnly) {
                    Box(
                        modifier = Modifier
                            .size(ListThumbnailSize)
                            .clip(ThumbnailRoundedShape)
                            .background(LocalContentColor.current.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = LocalContentColor.current.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    ItemThumbnail(
                        thumbnailUrl = song.song.thumbnailUrl,
                        albumIndex = albumIndex,
                        isSelected = isSelected,
                        isActive = isActive,
                        isPlaying = isPlaying,
                        shape = ThumbnailRoundedShape,
                        targetSizePx = thumbnailPx(ListThumbnailSize),
                        modifier = Modifier.size(ListThumbnailSize)
                    )
                }
            },
            trailingContent = trailingContent,
            modifier = modifier,
            isSelected = isSelected,
            isActive = isActive,
            shape = shape,
            drawHighlight = drawHighlight,
            flat = flat
        )
    }

    if (isSwipeable && swipeEnabled) {
        val mediaItem = remember(song.song.id) { song.toMediaItem() }
        SwipeToSongBox(
            mediaItem = mediaItem,
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
fun SongGridItem(
    song: Song,
    modifier: Modifier = Modifier,
    showLikedIcon: Boolean = true,
    showInLibraryIcon: Boolean = false,
    showDownloadIcon: Boolean = true,
    badges: @Composable RowScope.() -> Unit = {
        if (showLikedIcon && song.song.liked) {
            Icon.Favorite()
        }
        if (showInLibraryIcon && song.song.inLibrary != null) {
            Icon.Library()
        }
        if (showDownloadIcon) {
            val download = LocalDownloads.current[song.id]
            Icon.Download(download?.state)
        }
    },
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
    showIconOnly: Boolean = false,
) = GridItem(
    title = {
        Text(
            text = song.song.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.marqueeWhenVisible().fillMaxWidth()
        )
    },
    subtitle = {
        Text(
            text = joinByBullet(
                song.artists.joinToString { it.name },
                makeTimeString(song.song.duration * 1000L)
            ),
            style = MaterialTheme.typography.bodySmall,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    },
    badges = badges,
    thumbnailContent = {
        if (showIconOnly) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier.size(maxWidth / 2.5f),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            val gridHeight = currentGridThumbnailHeight()
            ItemThumbnail(
                thumbnailUrl = song.song.thumbnailUrl,
                isActive = isActive,
                isPlaying = isPlaying,
                shape = ThumbnailRoundedShape,
                // A fixed size here ignored the surrounding GridItem's own
                // fillMaxWidth switch — in a wide tab-view cell the outer square
                // stretched correctly, but this stayed pinned small and centered
                // inside it, leaving a gap with the title pushed down below the
                // whole (mostly empty) square. Match the parent instead.
                modifier = if (fillMaxWidth) Modifier.fillMaxSize() else Modifier.size(gridHeight)
            )
        }
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun ArtistListItem(
    artist: Artist,
    modifier: Modifier = Modifier,
    flat: Boolean = false,
    badges: @Composable RowScope.() -> Unit = {
        if (artist.artist.bookmarkedAt != null) {
            Icon(
                painter = painterResource(R.drawable.favorite),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 2.dp),
            )
        }
    },
    trailingContent: @Composable RowScope.() -> Unit = {},
    showIconOnly: Boolean = false,
) = ListItem(
    title = artist.artist.name,
    subtitle = pluralStringResource(R.plurals.n_song, artist.songCount, artist.songCount),
    badges = badges,
    thumbnailContent = {
        if (showIconOnly) {
            Box(
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape)
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.artist),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            val context = LocalContext.current
            val imageRequest = remember(artist.artist.thumbnailUrl) {
                ImageRequest.Builder(context)
                    .data(artist.artist.thumbnailUrl?.resize(544, 544))
                    .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .build()
            }
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape),
            )
        }
    },
    trailingContent = trailingContent,
    modifier = modifier,
    flat = flat
)

@Composable
fun ArtistGridItem(
    artist: Artist,
    modifier: Modifier = Modifier,
    badges: @Composable RowScope.() -> Unit = {
        if (artist.artist.bookmarkedAt != null) {
            Icon.Favorite()
        }
    },
    fillMaxWidth: Boolean = false,
    showIconOnly: Boolean = false,
) = GridItem(
    title = artist.artist.name,
    subtitle = pluralStringResource(R.plurals.n_song, artist.songCount, artist.songCount),
    horizontalAlignment = Alignment.CenterHorizontally,
    badges = badges,
    thumbnailContent = {
        if (showIconOnly) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ThumbnailRoundedShape)
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.artist),
                    contentDescription = null,
                    modifier = Modifier.size(ListThumbnailSize / 2),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            val context = LocalContext.current
            val imageRequest = remember(artist.artist.thumbnailUrl) {
                ImageRequest.Builder(context)
                    .data(artist.artist.thumbnailUrl?.resize(544, 544))
                    .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .build()
            }
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ThumbnailRoundedShape)
            )
        }
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun AlbumListItem(
    album: Album,
    modifier: Modifier = Modifier,
    flat: Boolean = false,
    showLikedIcon: Boolean = true,
    badges: @Composable RowScope.() -> Unit = {
        val database = LocalDatabase.current

        val songs by produceState<List<Song>>(initialValue = emptyList(), album.id) {
            withContext(Dispatchers.IO) {
                value = database.albumSongs(album.id).first()
            }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                if (songs.isEmpty()) {
                    Download.STATE_STOPPED
                } else {
                    when {
                        songs.all { allDownloads[it.id]?.state == STATE_COMPLETED } -> STATE_COMPLETED
                        songs.any { allDownloads[it.id]?.state in listOf(STATE_QUEUED, STATE_DOWNLOADING) } -> STATE_DOWNLOADING
                        // Distinguishing this from STOPPED is what makes a failed
                        // download visible here instead of the row silently looking
                        // like it was never downloaded at all.
                        songs.any { allDownloads[it.id]?.state == Download.STATE_FAILED } -> Download.STATE_FAILED
                        else -> Download.STATE_STOPPED
                    }
                }
            }
        }

        if (showLikedIcon && album.album.bookmarkedAt != null) {
            Icon.Favorite()
        }
        if (album.album.explicit) {
            Icon.Explicit()
        }
        Icon.Download(downloadState)
    },
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    trailingContent: @Composable RowScope.() -> Unit = {},
    showIconOnly: Boolean = false,
) = ListItem(
    title = album.album.title,
    subtitle = joinByBullet(
        album.artists.joinToString { it.name },
        pluralStringResource(R.plurals.n_song, album.album.songCount, album.album.songCount),
        album.album.year?.toString()
    ),
    badges = badges,
    thumbnailContent = {
        if (showIconOnly) {
            Box(
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape)
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.album),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            ItemThumbnail(
                thumbnailUrl = album.album.thumbnailUrl,
                isActive = isActive,
                isPlaying = isPlaying,
                shape = ThumbnailRoundedShape,
                targetSizePx = thumbnailPx(ListThumbnailSize),
                sharedArtworkId = album.id,
                modifier = Modifier.size(ListThumbnailSize)
            )
        }
    },
    trailingContent = trailingContent,
    modifier = modifier,
    flat = flat
)

@Composable
fun AlbumGridItem(
    album: Album,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    showLikedIcon: Boolean = true,
    badges: @Composable RowScope.() -> Unit = {
        val database = LocalDatabase.current

        val songs by produceState<List<Song>>(initialValue = emptyList(), album.id) {
            withContext(Dispatchers.IO) {
                value = database.albumSongs(album.id).first()
            }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                if (songs.isEmpty()) {
                    Download.STATE_STOPPED
                } else {
                    when {
                        songs.all { allDownloads[it.id]?.state == STATE_COMPLETED } -> STATE_COMPLETED
                        songs.any { allDownloads[it.id]?.state in listOf(STATE_QUEUED, STATE_DOWNLOADING) } -> STATE_DOWNLOADING
                        // Distinguishing this from STOPPED is what makes a failed
                        // download visible here instead of the row silently looking
                        // like it was never downloaded at all.
                        songs.any { allDownloads[it.id]?.state == Download.STATE_FAILED } -> Download.STATE_FAILED
                        else -> Download.STATE_STOPPED
                    }
                }
            }
        }

        if (showLikedIcon && album.album.bookmarkedAt != null) {
            Icon.Favorite()
        }
        if (album.album.explicit) {
            Icon.Explicit()
        }
        Icon.Download(downloadState)
    },
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
    showIconOnly: Boolean = false,
) = GridItem(
    title = {
        Text(
            text = album.album.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.marqueeWhenVisible().fillMaxWidth()
        )
    },
    subtitle = {
        Text(
            text = album.artists.joinToString { it.name },
            style = MaterialTheme.typography.bodySmall,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    badges = badges,
    thumbnailContent = {
        if (showIconOnly) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ThumbnailRoundedShape)
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.album),
                    contentDescription = null,
                    modifier = Modifier.size(ListThumbnailSize / 2),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            ItemThumbnail(
                thumbnailUrl = album.album.thumbnailUrl,
                isActive = isActive,
                isPlaying = isPlaying,
                shape = ThumbnailRoundedShape,
                sharedArtworkId = album.id,
            )
        }
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
private fun getAutoPlaylistIcon(name: String): Int = when {
    name == stringResource(R.string.liked) -> R.drawable.favorite_border
    name == stringResource(R.string.offline) -> R.drawable.offline
    name == stringResource(R.string.cached_playlist) -> R.drawable.cached
    name == stringResource(R.string.uploaded_playlist) -> R.drawable.backup
    name == stringResource(R.string.filter_local) -> R.drawable.local_songs
    name.startsWith(stringResource(R.string.my_top)) -> R.drawable.trending_up
    else -> R.drawable.queue_music
}

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    autoPlaylist: Boolean = false,
    // When set, the card shows this image (a user-picked content:// URI or a
    // first-song artwork URL) instead of the playlist thumbnails / auto icon.
    thumbnailOverrideUrl: String? = null,
    flat: Boolean = false,
    badges: @Composable RowScope.() -> Unit = {
        val database = LocalDatabase.current

        val songs by produceState<List<Song>>(initialValue = emptyList(), playlist.id) {
            withContext(Dispatchers.IO) {
                value = database.playlistSongs(playlist.id).first().map { it.song }
            }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                if (songs.isEmpty()) {
                    Download.STATE_STOPPED
                } else {
                    when {
                        songs.all { allDownloads[it.id]?.state == STATE_COMPLETED } -> STATE_COMPLETED
                        songs.any { allDownloads[it.id]?.state in listOf(STATE_QUEUED, STATE_DOWNLOADING) } -> STATE_DOWNLOADING
                        // Distinguishing this from STOPPED is what makes a failed
                        // download visible here instead of the row silently looking
                        // like it was never downloaded at all.
                        songs.any { allDownloads[it.id]?.state == Download.STATE_FAILED } -> Download.STATE_FAILED
                        else -> Download.STATE_STOPPED
                    }
                }
            }
        }

        Icon.Download(downloadState)
    },
    trailingContent: @Composable RowScope.() -> Unit = {},
    shape: Shape = androidx.compose.ui.graphics.RectangleShape,
    showIconOnly: Boolean = false,
) = ListItem(
    title = playlist.playlist.name,
    subtitle = if (autoPlaylist) {
        ""
    } else {
        if (playlist.songCount == 0 && playlist.playlist.remoteSongCount != null) {
            pluralStringResource(
                R.plurals.n_song,
                playlist.playlist.remoteSongCount,
                playlist.playlist.remoteSongCount
            )
        } else {
            pluralStringResource(
                R.plurals.n_song,
                playlist.songCount,
                playlist.songCount
            )
        }
    },
    badges = badges,
    thumbnailContent = {
        if (thumbnailOverrideUrl != null) {
            AsyncImage(
                model = thumbnailOverrideUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape),
            )
        } else if (showIconOnly) {
            Box(
                modifier = Modifier
                    .size(ListThumbnailSize)
                    .clip(ThumbnailRoundedShape)
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(getAutoPlaylistIcon(playlist.playlist.name)),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
            PlaylistThumbnail(
                thumbnails = playlist.thumbnails,
                size = ListThumbnailSize,
                placeHolder = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LocalContentColor.current.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(getAutoPlaylistIcon(playlist.playlist.name)),
                            contentDescription = null,
                            tint = LocalContentColor.current.copy(alpha = 0.6f),
                            modifier = Modifier.size(ListThumbnailSize / 2)
                        )
                    }
                },
                shape = ThumbnailRoundedShape
            )
        }
    },
    trailingContent = trailingContent,
    modifier = modifier,
    shape = shape,
    flat = flat
)

@Composable
fun PlaylistGridItem(
    playlist: Playlist,
    modifier: Modifier = Modifier,
    autoPlaylist: Boolean = false,
    // When set, the card shows this image (a user-picked content:// URI or a
    // first-song artwork URL) instead of the playlist thumbnails / auto icon.
    thumbnailOverrideUrl: String? = null,
    badges: @Composable RowScope.() -> Unit = {
        val database = LocalDatabase.current

        val songs by produceState<List<Song>>(initialValue = emptyList(), playlist.id) {
            withContext(Dispatchers.IO) {
                value = database.playlistSongs(playlist.id).first().map { it.song }
            }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                if (songs.isEmpty()) {
                    Download.STATE_STOPPED
                } else {
                    when {
                        songs.all { allDownloads[it.id]?.state == STATE_COMPLETED } -> STATE_COMPLETED
                        songs.any { allDownloads[it.id]?.state in listOf(STATE_QUEUED, STATE_DOWNLOADING) } -> STATE_DOWNLOADING
                        // Distinguishing this from STOPPED is what makes a failed
                        // download visible here instead of the row silently looking
                        // like it was never downloaded at all.
                        songs.any { allDownloads[it.id]?.state == Download.STATE_FAILED } -> Download.STATE_FAILED
                        else -> Download.STATE_STOPPED
                    }
                }
            }
        }

        Icon.Download(downloadState)
    },
    fillMaxWidth: Boolean = false,
    showIconOnly: Boolean = false,
) = GridItem(
    title = {
        Text(
            text = playlist.playlist.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.marqueeWhenVisible().fillMaxWidth()
        )
    },
    subtitle = {
        val subtitle = if (autoPlaylist) {
            ""
        } else {
            if (playlist.songCount == 0 && playlist.playlist.remoteSongCount != null) {
                pluralStringResource(
                    R.plurals.n_song,
                    playlist.playlist.remoteSongCount,
                    playlist.playlist.remoteSongCount
                )
            } else {
                pluralStringResource(
                    R.plurals.n_song,
                    playlist.songCount,
                    playlist.songCount
                )
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    badges = badges,
    thumbnailContent = {
        val width = maxWidth
        if (thumbnailOverrideUrl != null) {
            AsyncImage(
                model = thumbnailOverrideUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (showIconOnly) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalContentColor.current.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(getAutoPlaylistIcon(playlist.playlist.name)),
                    contentDescription = null,
                    modifier = Modifier.size(width / 3),
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        } else {
        PlaylistThumbnail(
            thumbnails = playlist.thumbnails,
            size = width,
            placeHolder = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalContentColor.current.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getAutoPlaylistIcon(playlist.playlist.name)),
                        contentDescription = null,
                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                        modifier = Modifier.size(width / 2)
                    )
                }
            },
            shape = ThumbnailRoundedShape
        )
        }
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun MediaMetadataListItem(
    mediaMetadata: MediaMetadata,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    shape: Shape = RectangleShape,
    flat: Boolean = false,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    ListItem(
        title = mediaMetadata.title,
        subtitle = if (mediaMetadata.suggestedBy != null) {
            buildAnnotatedString {
                append(mediaMetadata.artists.joinToString { it.name })
                append(" • ")
                append(makeTimeString(mediaMetadata.duration * 1000L))
                append(" • ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(mediaMetadata.suggestedBy)
                }
            }
        } else {
            AnnotatedString(
                joinByBullet(
                    mediaMetadata.artists.joinToString { it.name },
                    makeTimeString(mediaMetadata.duration * 1000L)
                )
            )
        },
        badges = { if (mediaMetadata.explicit) Icon.Explicit()},
        thumbnailContent = {
            ItemThumbnail(
                thumbnailUrl = mediaMetadata.thumbnailUrl,
                albumIndex = null,
                isSelected = isSelected,
                isActive = isActive,
                isPlaying = isPlaying,
                shape = ThumbnailRoundedShape,
                targetSizePx = thumbnailPx(ListThumbnailSize),
                modifier = Modifier.size(ListThumbnailSize)
            )
        },
        trailingContent = trailingContent,
        modifier = modifier,
        isActive = isActive,
        shape = shape,
        flat = flat
    )
}


private val rowSongCache = android.util.LruCache<String, Song>(256)
private val rowAlbumCache = android.util.LruCache<String, Album>(128)

/** Per-row DB lookup for a badge, cached in memory so fast scroll does not spam SQLite. */
@Composable
private fun rememberRowSong(item: YTItem): Song? {
    if (item !is SongItem) return null
    val cached = rowSongCache.get(item.id)
    if (cached != null) return cached

    val database = LocalDatabase.current
    val song by produceState<Song?>(initialValue = null, item.id) {
        val result = database.song(item.id).firstOrNull()
        if (result != null) {
            rowSongCache.put(item.id, result)
        }
        value = result
    }
    return song
}

@Composable
private fun rememberRowAlbum(item: YTItem): Album? {
    if (item !is AlbumItem) return null
    val cached = rowAlbumCache.get(item.id)
    if (cached != null) return cached

    val database = LocalDatabase.current
    val album by produceState<Album?>(initialValue = null, item.id) {
        val result = database.album(item.id).firstOrNull()
        if (result != null) {
            rowAlbumCache.put(item.id, result)
        }
        value = result
    }
    return album
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeListItem(
    item: YTItem,
    modifier: Modifier = Modifier,
    albumIndex: Int? = null,
    isSelected: Boolean = false,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    isSwipeable: Boolean = true,
    trailingContent: @Composable RowScope.() -> Unit = {},
    /** Pass these from a map the screen already collected to avoid a per-row DB
     *  query. Left null, the row looks itself up as before — dropping the lookup
     *  outright silently removed the liked/bookmarked badge everywhere. */
    song: Song? = null,
    album: Album? = null,
    badges: @Composable RowScope.() -> Unit = {
        val resolvedSong = song ?: rememberRowSong(item)
        val resolvedAlbum = album ?: rememberRowAlbum(item)
        if ((item is SongItem && resolvedSong?.song?.liked == true) ||
            (item is AlbumItem && resolvedAlbum?.album?.bookmarkedAt != null)
        ) {
            Icon.Favorite()
        }
        if (item.explicit) Icon.Explicit()
        if (item is SongItem) {
            // Flow is remembered; collectAsState is composable and stays outside.
            val downloadUtil = LocalDownloadUtil.current
            val download = LocalDownloads.current[item.id]
            Icon.Download(download?.state)
        }
    },
    shape: Shape = RectangleShape,
    drawHighlight: Boolean = true,
    flat: Boolean = false,
) {
    val swipeEnabled = LocalItemPrefs.current.swipeToSong

    val content: @Composable () -> Unit = {
        ListItem(
            title = item.title,
            subtitle = when (item) {
                is SongItem -> joinByBullet(item.artists.joinToString { it.name }, makeTimeString(item.duration?.times(1000L)))
                is AlbumItem -> joinByBullet(item.artists?.joinToString { it.name }, item.year?.toString())
                is ArtistItem -> null
                is PlaylistItem -> joinByBullet(item.author?.name, item.songCountText)
            },
            badges = badges,
            thumbnailContent = {
                ItemThumbnail(
                    thumbnailUrl = item.thumbnail,
                    albumIndex = albumIndex,
                    isSelected = isSelected,
                    isActive = isActive,
                    isPlaying = isPlaying,
                    // List rows keep one artwork shape for every item type; the circle
                    // stays on artist tiles and heroes, where it reads as a portrait.
                    shape = ThumbnailRoundedShape,
                    targetSizePx = thumbnailPx(ListThumbnailSize),
                    // Songs open the player, not a detail screen with a hero, so they
                    // stay out of the artwork morph -- the sheet has its own transition.
                    sharedArtworkId = if (item is SongItem) null else item.id,
                    modifier = Modifier.size(ListThumbnailSize)
                )
            },
            trailingContent = trailingContent,
            modifier = modifier,
            isSelected = isSelected,
            isActive = isActive,
            shape = shape,
            drawHighlight = drawHighlight,
            flat = flat
        )
    }

    if (item is SongItem && isSwipeable && swipeEnabled) {
        val mediaItem = remember(item.id) { item.copy(thumbnail = item.thumbnail.resize(544,544)).toMediaItem() }
        SwipeToSongBox(
            mediaItem = mediaItem,
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
fun YouTubeGridItem(
    item: YTItem,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope? = null,
    /** See [YouTubeListItem]: hoisted when the screen already has the data, looked
     *  up per row otherwise so the badge does not silently disappear. */
    song: Song? = null,
    album: Album? = null,
    badges: @Composable RowScope.() -> Unit = {
        val resolvedSong = song ?: rememberRowSong(item)
        val resolvedAlbum = album ?: rememberRowAlbum(item)
        if (item is SongItem && resolvedSong?.song?.liked == true ||
            item is AlbumItem && resolvedAlbum?.album?.bookmarkedAt != null
        ) {
            Icon.Favorite()
        }
        if (item.explicit) Icon.Explicit()
        // if (item is SongItem && song?.song?.inLibrary != null) Icon.Library()
        if (item is SongItem) {
            // Flow is remembered; collectAsState is composable and stays outside.
            val downloadUtil = LocalDownloadUtil.current
            val download = LocalDownloads.current[item.id]
            Icon.Download(download?.state)
        }
    },
    // Square like every other tile. Video-backed songs used to come through at
    // 16:9, which made a single row of results ragged.
    thumbnailRatio: Float = 1f,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
) = GridItem(
    title = {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (item is ArtistItem) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.marqueeWhenVisible().fillMaxWidth()
        )
    },
    subtitle = {
        val subtitle = when (item) {
            is SongItem -> joinByBullet(item.artists.joinToString { it.name }, makeTimeString(item.duration?.times(1000L)))
            is AlbumItem -> joinByBullet(item.artists?.joinToString { it.name }, item.year?.toString())
            is ArtistItem -> null
            is PlaylistItem -> joinByBullet(item.author?.name, item.songCountText)
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppleTokens.Metadata,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    },
    badges = badges,
    thumbnailContent = {
        ItemThumbnail(
            thumbnailUrl = item.thumbnail,
            isActive = isActive,
            isPlaying = isPlaying,
            shape = ThumbnailRoundedShape,
            sharedArtworkId = if (item is SongItem) null else item.id,
        )
    },
    thumbnailRatio = thumbnailRatio,
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun LocalSongsGrid(
    title: String,
    subtitle: String,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailUrl: String?,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
    modifier: Modifier = Modifier
) = GridItem(
    title = title,
    subtitle = subtitle,
    badges = badges,
    thumbnailContent = {
        LocalThumbnail(
            thumbnailUrl = thumbnailUrl,
            isActive = isActive,
            isPlaying = isPlaying,
            shape = ThumbnailRoundedShape,
            modifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier,
            showCenterPlay = true,
            playButtonVisible = false
        )
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun LocalArtistsGrid(
    title: String,
    subtitle: String,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailUrl: String?,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
    modifier: Modifier = Modifier
) = GridItem(
    title = title,
    subtitle = subtitle,
    badges = badges,
    thumbnailContent = {
        LocalThumbnail(
            thumbnailUrl = thumbnailUrl,
            isActive = false,
            isPlaying = false,
            shape = ThumbnailRoundedShape,
            modifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier,
            showCenterPlay = false,
            playButtonVisible = false
        )
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

@Composable
fun LocalAlbumsGrid(
    title: String,
    subtitle: String,
    badges: @Composable RowScope.() -> Unit = {},
    thumbnailUrl: String?,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    fillMaxWidth: Boolean = false,
    modifier: Modifier = Modifier
) = GridItem(
    title = title,
    subtitle = subtitle,
    badges = badges,
    thumbnailContent = {
        LocalThumbnail(
            thumbnailUrl = thumbnailUrl,
            isActive = isActive,
            isPlaying = isPlaying,
            shape = ThumbnailRoundedShape,
            modifier = if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier,
            showCenterPlay = false,
            playButtonVisible = true
        )
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)

/** Real pixel size of a [dp] box on this device, for sizing an image decode to what
 *  the layout can actually show. */
@Composable
fun thumbnailPx(dp: Dp): Int = with(LocalDensity.current) { dp.roundToPx() }

@Composable
fun ItemThumbnail(
    thumbnailUrl: String?,
    isActive: Boolean,
    isPlaying: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    albumIndex: Int? = null,
    isSelected: Boolean = false,
    thumbnailRatio: Float = 1f,
    /** Decode size. Grid tiles keep the large default; list rows pass their real
     *  on-screen size via [thumbnailPx], because decoding a 544px bitmap for a
     *  48dp row is ~10x the pixels the row can show. Do NOT lower this default:
     *  the same composable draws the big tiles, and they need every pixel. */
    targetSizePx: Int = 544,
    /** Overrides the global CropAlbumArtKey preference when set — for tiles that
     *  should always fill edge-to-edge (Apple Music style) regardless of the
     *  user's general album-art setting, e.g. Home's Speed Dial. */
    forceContentScale: ContentScale? = null,
    showPausedPlayIcon: Boolean = true,
    /** Id of the album/artist/playlist/song this artwork belongs to. Supplying it makes
     *  the thumbnail the source of the artwork morph into that item's detail screen --
     *  but only for the tile actually being opened, so the same album appearing in two
     *  rails does not put two claimants on one key. See [sharedArtworkSource]. */
    sharedArtworkId: String? = null,
) {
    val cropAlbumArtPref = LocalItemPrefs.current.cropAlbumArt
    val cropAlbumArt = forceContentScale == ContentScale.Crop || (forceContentScale == null && cropAlbumArtPref)
    val context = LocalContext.current
    val actualTargetSizePx = targetSizePx
    val imageRequest = remember(thumbnailUrl, actualTargetSizePx) {
        ImageRequest.Builder(context)
            .data(thumbnailUrl?.resize(actualTargetSizePx, actualTargetSizePx))
            // Keyed on the raw URL, not the resized one Coil would otherwise
            // default to: DownloadUtil pre-caches a downloaded song's thumbnail
            // under this same raw-URL key, so this is what lets that entry
            // actually be found offline instead of missing under a different
            // key for every distinct decode size requested across the app.
            .diskCacheKey(thumbnailUrl)
            .size(CoilSize(actualTargetSizePx, actualTargetSizePx))
            .allowHardware(true)
            .bitmapConfig(android.graphics.Bitmap.Config.HARDWARE)
            .crossfade(false)
            .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
            .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
            .build()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(thumbnailRatio)
            // Before the clip, so the overlay animates the rounded artwork rather than a
            // square that gets clipped at each end of the flight.
            .sharedArtworkSource(sharedArtworkId, thumbnailUrl)
            .clip(shape)
    ) {
        if (albumIndex == null) {
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
                placeholder = painterResource(R.drawable.thumbnail_fallback),
                error = painterResource(R.drawable.thumbnail_fallback),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (albumIndex != null) {
            AnimatedVisibility(
                visible = !isActive,
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
            ) {
                Text(
                    text = albumIndex.toString(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (isSelected) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .clip(shape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    painter = painterResource(R.drawable.done),
                    contentDescription = null
                )
            }
        }

        PlayingIndicatorBox(
            isActive = isActive,
            playWhenReady = isPlaying,
            color = if (albumIndex != null) MaterialTheme.colorScheme.onBackground else Color.White,
            showPausedIcon = showPausedPlayIcon,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (albumIndex != null)
                        Color.Transparent
                    else
                        Color.Black.copy(alpha = ActiveBoxAlpha),
                    shape = shape
                )
        )
    }
}

@Composable
fun LocalThumbnail(
    thumbnailUrl: String?,
    isActive: Boolean,
    isPlaying: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    showCenterPlay: Boolean = false,
    playButtonVisible: Boolean = false,
    thumbnailRatio: Float = 1f
) {
    val cropAlbumArt = LocalItemPrefs.current.cropAlbumArt
    val context = LocalContext.current
    val imageRequest = remember(thumbnailUrl) {
        ImageRequest.Builder(context)
            .data(thumbnailUrl?.resize(544, 544))
            // See ItemThumbnail above: keyed on the raw URL so a thumbnail
            // pre-cached at download time (also keyed on the raw URL) is found.
            .diskCacheKey(thumbnailUrl)
            .size(CoilSize(544, 544))
            .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
            .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
            .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
            .build()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(thumbnailRatio)
            .clip(shape)
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = isActive,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f), shape)
            ) {
                if (isPlaying) {
                    PlayingIndicator(
                        color = Color.White,
                        modifier = Modifier.height(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        if (showCenterPlay) {
            AnimatedVisibility(
                visible = !(isActive && isPlaying),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        if (playButtonVisible) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = ActiveBoxAlpha))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistThumbnail(
    thumbnails: List<String>,
    size: Dp,
    placeHolder: @Composable () -> Unit,
    shape: Shape,
    cacheKey: String? = null
) {
    val cropAlbumArt = LocalItemPrefs.current.cropAlbumArt
    val context = LocalContext.current

    when (thumbnails.size) {
        0 -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            placeHolder()
        }
        1 -> AsyncImage(
            model = remember(thumbnails) {
                ImageRequest.Builder(context)
                    .data(thumbnails[0].resize(544, 544))
                    // Same raw-URL keying as ItemThumbnail/LocalThumbnail above:
                    // DownloadUtil pre-caches a downloaded song's art under its
                    // raw (unresized) URL, so this needs the same key to find it
                    // offline instead of missing under the resized URL's default.
                    .diskCacheKey(thumbnails[0])
                    .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .build()
            },
            contentDescription = null,
            contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
            placeholder = painterResource(R.drawable.thumbnail_fallback),
            error = painterResource(R.drawable.thumbnail_fallback),
            modifier = Modifier
                .size(size)
                .clip(shape)
        )
        else -> Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
        ) {
            listOf(
                Alignment.TopStart,
                Alignment.TopEnd,
                Alignment.BottomStart,
                Alignment.BottomEnd
            ).fastForEachIndexed { index, alignment ->
                AsyncImage(
                    model = remember(thumbnails, index) {
                        ImageRequest.Builder(context)
                            .data(thumbnails.getOrNull(index)?.resize(544, 544))
                            .diskCacheKey(thumbnails.getOrNull(index))
                            .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                            .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                            .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                            .build()
                    },
                    contentDescription = null,
                    contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
                    placeholder = painterResource(R.drawable.thumbnail_fallback),
                    error = painterResource(R.drawable.thumbnail_fallback),
                    modifier = Modifier
                        .align(alignment)
                        .size(size / 2)
                )
            }
        }
    }
}

@Composable
fun BoxScope.OverlayEditButton(
    visible: Boolean,
    onClick: () -> Unit,
    alignment: Alignment = Alignment.Center,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(alignment)
            .then(if (alignment == Alignment.BottomEnd) Modifier.padding(8.dp) else Modifier)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = ActiveBoxAlpha))
                .padding(0.dp)
                .clickable(onClick = onClick)
        ) {
            Icon(
                painter = painterResource(R.drawable.edit),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun SwipeToSongBox(
    modifier: Modifier = Modifier,
    mediaItem: MediaItem,
    content: @Composable BoxScope.() -> Unit
) {
    val ctx = LocalContext.current
    val player = LocalPlayerConnection.current
    val scope = rememberCoroutineScope()
    val offset = remember { mutableFloatStateOf(0f) }
    val threshold = 300f

    val dragState = rememberDraggableState { delta ->
        offset.floatValue = (offset.floatValue + delta).coerceIn(-threshold, threshold)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                state = dragState,
                onDragStopped = {
                    when {
                        offset.floatValue >= threshold -> {
                            player?.playNext(listOf(mediaItem))
                            Toast.makeText(ctx, R.string.play_next, Toast.LENGTH_SHORT).show()
                            reset(offset, scope)
                        }

                        offset.floatValue <= -threshold -> {
                            player?.addToQueue(listOf(mediaItem))
                            Toast.makeText(ctx, R.string.add_to_queue, Toast.LENGTH_SHORT).show()
                            reset(offset, scope)
                        }

                        else -> reset(offset, scope)
                    }
                }
            )
    ) {
        if (offset.floatValue != 0f) {
            val (iconRes, bg, tint, align) = if (offset.floatValue > 0)
                Quadruple(
                    R.drawable.playlist_play,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.onSecondary,
                    Alignment.CenterStart
                ) else
                Quadruple(
                    R.drawable.queue_music,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary,
                    Alignment.CenterEnd
                )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ListItemHeight)
                    .align(Alignment.Center)
                    .background(bg),
                contentAlignment = align
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .size(30.dp)
                        .alpha(0.9f),
                    tint = tint
                )
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offset.floatValue.roundToInt(), 0) }
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            content = content
        )
    }
}

// Helper to animate reset of swipe offset
private fun reset(offset: MutableState<Float>, scope: CoroutineScope) {
    scope.launch {
        animate(
            initialValue = offset.value,
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300)
        ) { value, _ -> offset.value = value }
    }
}

// Data holder for swipe visuals
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

object Icon {
    @Composable
    fun Favorite() {
        Icon(
            painter = painterResource(R.drawable.favorite),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(18.dp)
                .padding(end = 2.dp)
        )
    }

    @Composable
    fun Library() {
        Icon(
            painter = painterResource(R.drawable.library_add_check),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .padding(end = 2.dp)
        )
    }

    @Composable
    fun Download(state: Int?) {
        when (state) {
            STATE_COMPLETED -> Icon(
                painter = painterResource(R.drawable.offline),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 2.dp)
            )
            STATE_QUEUED, STATE_DOWNLOADING -> CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 2.dp)
            )
            // A failed download used to fall into the same "no icon" bucket as
            // "never downloaded" — the row just silently reverted to looking like
            // nothing had happened, with no notification either, so a failure was
            // completely invisible. Surfacing it distinctly is the whole fix.
            Download.STATE_FAILED -> Icon(
                painter = painterResource(R.drawable.error),
                contentDescription = stringResource(R.string.download_failed),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(18.dp)
                    .padding(end = 2.dp)
            )
            else -> { /* no icon */ }
        }
    }

    @Composable
    fun Explicit() {
        Icon(
            painter = painterResource(R.drawable.explicit),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .padding(end = 2.dp)
        )
    }
}
