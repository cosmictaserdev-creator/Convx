/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library.apple

import com.convx.music.ui.component.sf
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.composables.sfsymbols.SfSymbol
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFDotRadiowavesLeftAndRight
import com.composables.sfsymbols.monochrome.SFEllipsis
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFPlay
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFSquareAndArrowUp
import com.composables.sfsymbols.monochrome.SFSquareStack
import com.composables.sfsymbols.monochrome.SFTextLineFirstAndArrowtriangleForward
import com.composables.sfsymbols.monochrome.SFTextLineLastAndArrowtriangleForward
import com.convx.music.R
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.YouTubeAlbumRadio
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.component.AppleContextMenu
import com.convx.music.ui.ios.component.IosGlyph
import com.convx.music.ui.ios.component.IosMenuItem
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem

/** Menu for a YouTube Music tile: queue/station actions, navigation, share, and the full sheet. */
@Composable
internal fun ytItemMenu(
    item: YTItem,
    navController: NavController,
    player: PlayerConnection,
    onMore: () -> Unit,
): Pair<List<IosMenuItem>, List<IosMenuSection>> {
    val context = LocalContext.current
    fun glyph(v: SfSymbol): @Composable (Color) -> Unit = { tint -> IosGlyph(v, tint, 20.dp) }

    val shareUrl = item.shareLink
    val quick = listOf(
        IosMenuItem(stringResource(R.string.share), icon = glyph(sf("SFSquareAndArrowUp"))) {
            context.startActivity(
                Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, shareUrl), null),
            )
        },
    )
    val playing = when (item) {
        is SongItem -> listOf(
            IosMenuItem(stringResource(R.string.play_next), icon = glyph(sf("SFTextLineFirstAndArrowtriangleForward"))) {
                player.playNext(item.toMediaMetadata().toMediaItem())
            },
            IosMenuItem(stringResource(R.string.play_last), icon = glyph(sf("SFTextLineLastAndArrowtriangleForward"))) {
                player.addToQueue(item.toMediaMetadata().toMediaItem())
            },
            IosMenuItem(stringResource(R.string.start_radio), icon = glyph(sf("SFDotRadiowavesLeftAndRight"))) {
                player.playQueue(YouTubeQueue.radio(item.toMediaMetadata()))
            },
        )
        is AlbumItem -> listOf(
            IosMenuItem(stringResource(R.string.start_radio), icon = glyph(sf("SFDotRadiowavesLeftAndRight"))) {
                player.playQueue(YouTubeAlbumRadio(item.playlistId))
            },
        )
        is PlaylistItem -> listOfNotNull(
            item.playEndpoint?.let { ep ->
                IosMenuItem(stringResource(R.string.play), icon = glyph(sf("SFPlay"))) { player.playQueue(YouTubeQueue(ep)) }
            },
            item.shuffleEndpoint?.let { ep ->
                IosMenuItem(stringResource(R.string.shuffle), icon = glyph(sf("SFShuffle"))) { player.playQueue(YouTubeQueue(ep)) }
            },
        )
        is ArtistItem -> listOfNotNull(
            item.shuffleEndpoint?.let { ep ->
                IosMenuItem(stringResource(R.string.shuffle), icon = glyph(sf("SFShuffle"))) { player.playQueue(YouTubeQueue(ep)) }
            },
            item.radioEndpoint?.let { ep ->
                IosMenuItem(stringResource(R.string.start_radio), icon = glyph(sf("SFDotRadiowavesLeftAndRight"))) { player.playQueue(YouTubeQueue(ep)) }
            },
        )
    }
    val going = when (item) {
        is SongItem -> listOfNotNull(
            item.album?.let { album ->
                IosMenuItem(stringResource(R.string.apple_go_to_album), icon = glyph(sf("SFSquareStack"))) {
                    navController.navigate(albumRoute(album.id, item.thumbnail))
                }
            },
            item.artists.firstOrNull()?.id?.let { id ->
                IosMenuItem(stringResource(R.string.apple_go_to_artist), icon = glyph(sf("microphone.dynamic.on.stand"))) {
                    navController.navigate(artistRoute(id, null))
                }
            },
        )
        is AlbumItem -> listOfNotNull(
            item.artists?.firstOrNull()?.id?.let { id ->
                IosMenuItem(stringResource(R.string.apple_go_to_artist), icon = glyph(sf("microphone.dynamic.on.stand"))) {
                    navController.navigate(artistRoute(id, null))
                }
            },
        )
        else -> emptyList()
    }
    val more = listOf(
        IosMenuItem(stringResource(R.string.more_options), icon = glyph(sf("SFEllipsis"))) { onMore() },
    )
    return quick to listOf(playing, going, more).filter { it.isNotEmpty() }.map { IosMenuSection(it) }
}


/**
 * A YouTube Music tile with Apple's hold-to-pop: the tile lifts into a preview card with
 * [ytItemMenu] under it; a tap still runs [onClick]. [onMore] opens the full sheet menu.
 */
@Composable
internal fun YtPopTile(
    item: YTItem,
    navController: NavController,
    player: PlayerConnection,
    subtitle: String,
    onClick: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val (quick, sections) = ytItemMenu(item, navController, player, onMore)
    AppleContextMenu(
        sections = sections,
        quickActions = quick,
        preview = {
            Cover(item.thumbnail, 12.dp)
            Text(
                text = item.title,
                style = SfStyle(15.sp, 20.sp, FontWeight.SemiBold, (-0.23).sp),
                color = IosTheme.colors.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp, start = 2.dp),
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = SfStyle(13.sp, 18.sp),
                    color = IosTheme.colors.secondaryLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
                )
            }
        },
        onClick = onClick,
        modifier = modifier,
        content = content,
    )
}
