/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.convx.music.R
import com.convx.music.constants.GridCardArtShape
import com.convx.music.constants.ListRowArtShape
import com.convx.music.constants.ListThumbnailSize
import com.convx.music.db.entities.Song
import com.convx.music.library.PlaylistCardData
import com.convx.music.library.LocalPlaylistSongDataSource
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.playback.displayDownloadState
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.utils.resize
import com.convx.music.ui.utils.sharedArtworkSource
import com.convx.music.ui.utils.sharedTitleSource
import org.koin.compose.koinInject

@Composable
internal fun getAutoPlaylistIcon(name: String): Int = when {
    name == stringResource(R.string.liked) -> R.drawable.favorite_border
    name == stringResource(R.string.offline) -> R.drawable.offline
    name == stringResource(R.string.cached_playlist) -> R.drawable.cached
    name == stringResource(R.string.uploaded_playlist) -> R.drawable.backup
    name == stringResource(R.string.filter_local) -> R.drawable.music_note
    name.startsWith(stringResource(R.string.my_top)) -> R.drawable.trending_up
    else -> R.drawable.queue_music
}

@Composable
private fun playlistSongCountSubtitle(playlist: PlaylistCardData, autoPlaylist: Boolean): String {
    if (autoPlaylist) return ""

    val remoteSongCount = playlist.remoteSongCount
    return if (playlist.songCount == 0 && remoteSongCount != null) {
        pluralStringResource(R.plurals.n_song, remoteSongCount, remoteSongCount)
    } else {
        pluralStringResource(R.plurals.n_song, playlist.songCount, playlist.songCount)
    }
}

@Composable
fun PlaylistListItem(
    playlist: PlaylistCardData,
    modifier: Modifier = Modifier,
    autoPlaylist: Boolean = false,
    sharedArtworkId: String? = null,
    // When set, the card shows this image (a user-picked content:// URI or a
    // first-song artwork URL) instead of the playlist thumbnails / auto icon.
    thumbnailOverrideUrl: String? = null,
    flat: Boolean = false,
    badges: @Composable RowScope.() -> Unit = {
        val localPlaylistSongDataSource = koinInject<LocalPlaylistSongDataSource>()

        val songs by produceState<List<Song>>(initialValue = emptyList(), playlist.id) {
            value = localPlaylistSongDataSource.loadSongs(playlist.id).map { it.song }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                displayDownloadState(songs, allDownloads) { it.id }
            }
        }

        Icon.Download(downloadState)
    },
    trailingContent: @Composable RowScope.() -> Unit = {},
    shape: Shape = androidx.compose.ui.graphics.RectangleShape,
    showIconOnly: Boolean = false,
) = ListItem(
    title = playlist.name,
    subtitle = playlistSongCountSubtitle(playlist, autoPlaylist),
    badges = badges,
    thumbnailContent = {
        Box(
            modifier = Modifier.sharedArtworkSource(
                sharedArtworkId ?: if (autoPlaylist) null else playlist.id,
                thumbnailOverrideUrl ?: playlist.artworkUrls.firstOrNull(),
            ),
            propagateMinConstraints = true,
        ) {
            if (thumbnailOverrideUrl != null) {
                AsyncImage(
                    model = thumbnailOverrideUrl,
                    onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
                        com.convx.music.ui.utils.rememberArtworkSourceOwner(), thumbnailOverrideUrl,
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(ListThumbnailSize)
                        .clip(ListRowArtShape),
                )
            } else if (showIconOnly) {
                Box(
                    modifier = Modifier
                        .size(ListThumbnailSize)
                        .clip(ListRowArtShape)
                        .background(LocalContentColor.current.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getAutoPlaylistIcon(playlist.name)),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            } else {
                PlaylistThumbnail(
                    thumbnails = playlist.artworkUrls,
                    size = ListThumbnailSize,
                    placeHolder = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LocalContentColor.current.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(getAutoPlaylistIcon(playlist.name)),
                                contentDescription = null,
                                tint = LocalContentColor.current.copy(alpha = 0.6f),
                                modifier = Modifier.size(ListThumbnailSize / 2)
                            )
                        }
                    },
                    shape = ListRowArtShape
                )
            }
        }
    },
    trailingContent = trailingContent,
    modifier = modifier,
    shape = shape,
    flat = flat,
    titleFontWeight = FontWeight.Normal,
    titleLetterSpacing = (-0.43).sp,
    titleFontSize = 17.sp,
    titleLineHeight = 22.sp,
    subtitleFontSize = 15.sp,
    subtitleLineHeight = 20.sp,
)

@Composable
fun PlaylistGridItem(
    playlist: PlaylistCardData,
    modifier: Modifier = Modifier,
    autoPlaylist: Boolean = false,
    sharedArtworkId: String? = null,
    // When set, the card shows this image (a user-picked content:// URI or a
    // first-song artwork URL) instead of the playlist thumbnails / auto icon.
    thumbnailOverrideUrl: String? = null,
    badges: @Composable RowScope.() -> Unit = {
        val localPlaylistSongDataSource = koinInject<LocalPlaylistSongDataSource>()

        val songs by produceState<List<Song>>(initialValue = emptyList(), playlist.id) {
            value = localPlaylistSongDataSource.loadSongs(playlist.id).map { it.song }
        }

        val allDownloads = LocalDownloads.current

        val downloadState by remember(songs, allDownloads) {
            derivedStateOf {
                displayDownloadState(songs, allDownloads) { it.id }
            }
        }

        Icon.Download(downloadState)
    },
    fillMaxWidth: Boolean = false,
    showIconOnly: Boolean = false,
) = GridItem(
    title = {
        Text(
            text = playlist.name,
            style = AppTheme.typography.bodyMedium,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = (-0.23).sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.sharedTitleSource(playlist.name).fillMaxWidth()
        )
    },
    subtitle = {
        Text(
            text = playlistSongCountSubtitle(playlist, autoPlaylist),
            style = AppTheme.typography.bodySmall,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = AppleTokens.Metadata,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    badges = badges,
    thumbnailContent = {
        val width = maxWidth
        Box(
            modifier = Modifier.fillMaxSize().sharedArtworkSource(
                sharedArtworkId ?: if (autoPlaylist) null else playlist.id,
                thumbnailOverrideUrl ?: playlist.artworkUrls.firstOrNull(),
            ),
            propagateMinConstraints = true,
        ) {
            if (thumbnailOverrideUrl != null) {
                AsyncImage(
                    model = thumbnailOverrideUrl,
                    onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
                        com.convx.music.ui.utils.rememberArtworkSourceOwner(), thumbnailOverrideUrl,
                    ),
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
                        painter = painterResource(getAutoPlaylistIcon(playlist.name)),
                        contentDescription = null,
                        modifier = Modifier.size(width / 3),
                        tint = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
            } else {
            PlaylistThumbnail(
                thumbnails = playlist.artworkUrls,
                size = width,
                placeHolder = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LocalContentColor.current.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(getAutoPlaylistIcon(playlist.name)),
                            contentDescription = null,
                            tint = LocalContentColor.current.copy(alpha = 0.6f),
                            modifier = Modifier.size(width / 3)
                        )
                    }
                },
                shape = GridCardArtShape
            )
            }
        }
    },
    fillMaxWidth = fillMaxWidth,
    modifier = modifier
)


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
    // The real on-screen size, not a flat constant -- [size] is however big this
    // particular caller actually draws the tile, quartered tiles included.
    val targetPx = thumbnailPx(size)

    when (thumbnails.size) {
        0 -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(AppTheme.colorScheme.surfaceContainer)
        ) {
            placeHolder()
        }
        1 -> AsyncImage(
            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
                com.convx.music.ui.utils.rememberArtworkSourceOwner(), thumbnails[0],
            ),
            model = remember(thumbnails, targetPx) {
                val resolvedUrl = thumbnails[0].resize(targetPx, targetPx)
                ImageRequest.Builder(context)
                    .data(resolvedUrl)
                    // See ItemThumbnail: keyed on the resolved URL so this decode
                    // size can't collide on disk with a different one requested
                    // elsewhere for the same song.
                    .diskCacheKey(resolvedUrl)
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
                    model = remember(thumbnails, index, targetPx) {
                        val resolvedUrl = thumbnails.getOrNull(index)?.resize(targetPx, targetPx)
                        ImageRequest.Builder(context)
                            .data(resolvedUrl)
                            .diskCacheKey(resolvedUrl)
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
