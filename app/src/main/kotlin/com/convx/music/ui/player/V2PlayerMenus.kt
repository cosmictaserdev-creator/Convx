/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import com.convx.music.ui.ios.foundation.iosMenuSurface
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.width
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.SfSymbol
import com.composables.sfsymbols.monochrome.SFDotRadiowavesLeftAndRight
import com.composables.sfsymbols.monochrome.SFInfoCircle
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFPlusCircle
import com.composables.sfsymbols.monochrome.SFQuoteBubble
import com.composables.sfsymbols.monochrome.SFSquareAndArrowUp
import com.composables.sfsymbols.monochrome.SFSquareStack
import com.composables.sfsymbols.monochrome.SFStar
import com.composables.sfsymbols.monochrome.SFStarFill
import com.composables.sfsymbols.monochrome.SFTextBadgePlus
import com.convx.music.R
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.component.IosMenu
import com.convx.music.ui.ios.component.IosMenuItem
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.ios.foundation.IosMenuMaterial
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.LocalIosContentColor
import com.convx.music.ui.ios.foundation.LocalIosTextStyle

/** Panel, no sheet (a sheet ducked the player), drawn in-window over the player -- see [V2Floating]. */
@Composable
private fun V2MenuPopup(
    onDismiss: () -> Unit,
    sections: List<IosMenuSection>,
    quickActions: List<IosMenuItem> = emptyList(),
) {
    V2Floating(onDismiss = onDismiss, placement = AboveEnd) {
        // Liquid glass, like every other floating player control.
        CompositionLocalProvider(com.convx.music.ui.ios.foundation.LocalMenuLiquid provides true) {
            CompositionLocalProvider(
                LocalIosTextStyle provides IosTheme.typography.body,
                LocalIosContentColor provides IosMenuMaterial.colors.label,
            ) {
                IosMenu(sections = sections, quickActions = quickActions, onDismissRequest = onDismiss)
            }
        }
    }
}

private fun glyph(vector: ImageVector): @Composable (Color) -> Unit = { tint ->
    Icon(vector, null, tint = tint, modifier = Modifier.size(17.dp))
}

private fun symbolGlyph(symbol: SfSymbol): @Composable (Color) -> Unit = { tint ->
    com.convx.music.ui.component.SymbolIcon(symbol, null, size = 17.dp, tint = tint)
}

/**
 * The Now Playing ellipsis menu: Add / Favourite / Share quick actions,
 * then Add to a Playlist, Create Station, Go to (submenu), View Credits, Share Lyrics.
 * "More" keeps every other player action one tap away in the full sheet.
 */
@Composable
fun V2OptionsMenu(
    isLiked: Boolean,
    isInLibrary: Boolean,
    onDismiss: () -> Unit,
    onToggleLibrary: () -> Unit,
    onToggleLike: () -> Unit,
    /** Null hides the action, e.g. for a file on the device that has no YouTube link or station. */
    onShare: (() -> Unit)?,
    onAddToPlaylist: () -> Unit,
    onCreateStation: (() -> Unit)?,
    onGoTo: () -> Unit,
    onViewCredits: () -> Unit,
    onShareLyrics: () -> Unit,
    onMore: () -> Unit,
    sleepTimerLabel: () -> String?,
    onOpenSleepTimer: () -> Unit,
    onOpenAudioDevices: () -> Unit,
) {
    V2MenuPopup(
        onDismiss = onDismiss,
        quickActions = listOfNotNull(
            IosMenuItem(
                title = stringResource(if (isInLibrary) R.string.v2_menu_added else R.string.v2_menu_add),
                icon = glyph(SfSymbols.Monochrome.SFPlusCircle),
                onClick = onToggleLibrary,
            ),
            IosMenuItem(
                title = stringResource(R.string.v2_menu_favourite),
                icon = glyph(if (isLiked) SfSymbols.Monochrome.SFStarFill else SfSymbols.Monochrome.SFStar),
                onClick = onToggleLike,
            ),
            onShare?.let {
                IosMenuItem(
                    title = stringResource(R.string.share),
                    icon = glyph(SfSymbols.Monochrome.SFSquareAndArrowUp),
                    onClick = it,
                )
            },
        ),
        sections = listOf(
            IosMenuSection(listOf(
                IosMenuItem(stringResource(R.string.v2_menu_add_to_playlist), icon = glyph(SfSymbols.Monochrome.SFTextBadgePlus), onClick = onAddToPlaylist),
            )),
            IosMenuSection(listOfNotNull(
                onCreateStation?.let {
                    IosMenuItem(stringResource(R.string.v2_menu_create_station), icon = glyph(SfSymbols.Monochrome.SFDotRadiowavesLeftAndRight), onClick = it)
                },
            )),
            IosMenuSection(listOf(
                IosMenuItem(stringResource(R.string.v2_menu_go_to), icon = glyph(SfSymbols.Monochrome.SFSquareStack), onClick = onGoTo),
                IosMenuItem(stringResource(R.string.v2_menu_view_credits), icon = glyph(SfSymbols.Monochrome.SFInfoCircle), onClick = onViewCredits),
                IosMenuItem(stringResource(R.string.v2_menu_share_lyrics), icon = glyph(SfSymbols.Monochrome.SFQuoteBubble), onClick = onShareLyrics),
            )),
            IosMenuSection(listOf(
                IosMenuItem(stringResource(R.string.sleep_timer), description = sleepTimerLabel(), icon = symbolGlyph(com.convx.music.ui.component.sf("moon.zzz.fill")), onClick = { onDismiss(); onOpenSleepTimer() }),
                IosMenuItem(stringResource(R.string.audio_devices), icon = symbolGlyph(com.convx.music.ui.component.sf("airplayaudio")), onClick = { onDismiss(); onOpenAudioDevices() }),
            )),
            IosMenuSection(listOf(
                IosMenuItem(stringResource(R.string.v2_menu_more), onClick = onMore),
            )),
        ).filter { it.items.isNotEmpty() },
    )
}

/** Offers album and artist destinations for the current track. */
@Composable
fun V2GoToMenu(
    albumTitle: String?,
    artistName: String?,
    onDismiss: () -> Unit,
    onGoToAlbum: (() -> Unit)?,
    onGoToArtist: (() -> Unit)?,
) {
    V2MenuPopup(
        onDismiss = onDismiss,
        sections = listOf(
            IosMenuSection(buildList {
                if (onGoToAlbum != null) add(IosMenuItem(
                    title = stringResource(R.string.v2_menu_go_to_album),
                    description = albumTitle,
                    icon = glyph(SfSymbols.Monochrome.SFSquareStack),
                    onClick = onGoToAlbum,
                ))
                if (onGoToArtist != null) add(IosMenuItem(
                    title = stringResource(R.string.v2_menu_go_to_artist),
                    description = artistName,
                    icon = glyph(SfSymbols.Monochrome.SFMusicMicrophone),
                    onClick = onGoToArtist,
                ))
            }),
        ),
    )
}

/**
 * Everything behind V2's ellipsis: the options panel, its Go to submenu and the
 * add-to-playlist dialog. Owns its own state so each call site is one line plus an
 * `expanded` flag. Place it inside the ellipsis button's content: the popup anchors to
 * that button's bounds.
 */
@Composable
fun V2OptionsMenuHost(
    expanded: Boolean,
    onDismiss: () -> Unit,
    mediaMetadata: com.convx.music.models.MediaMetadata,
    isLiked: Boolean,
    playerConnection: com.convx.music.playback.PlayerConnection,
    navController: androidx.navigation.NavController,
    playerBottomSheetState: com.convx.music.ui.component.BottomSheetState,
    onViewCredits: () -> Unit,
    onShareLyrics: () -> Unit,
    onMore: () -> Unit,
    sleepTimerLabel: () -> String?,
    onOpenSleepTimer: () -> Unit,
    onOpenAudioDevices: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = com.convx.music.LocalDatabase.current.get()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val librarySong by database.song(mediaMetadata.id).collectAsState(initial = null)
    var showGoTo by remember { mutableStateOf(false) }
    var showAddToPlaylist by remember { mutableStateOf(false) }

    if (expanded) {
        V2OptionsMenu(
            isLiked = isLiked,
            isInLibrary = librarySong?.song?.inLibrary != null,
            onDismiss = onDismiss,
            onToggleLibrary = playerConnection::toggleLibrary,
            onToggleLike = playerConnection::toggleLike,
            onShare = if (mediaMetadata.isLocalFile) null else {
                {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${mediaMetadata.id}")
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, null))
                }
            },
            onAddToPlaylist = { showAddToPlaylist = true },
            onCreateStation = if (mediaMetadata.isLocalFile) null else playerConnection::startRadioSeamlessly,
            onGoTo = { showGoTo = true },
            onViewCredits = onViewCredits,
            onShareLyrics = onShareLyrics,
            onMore = onMore,
            sleepTimerLabel = sleepTimerLabel,
            onOpenSleepTimer = onOpenSleepTimer,
            onOpenAudioDevices = onOpenAudioDevices,
        )
    }
    if (showGoTo) {
        V2GoToMenu(
            albumTitle = mediaMetadata.album?.title,
            artistName = mediaMetadata.artists.firstOrNull()?.name,
            onDismiss = { showGoTo = false },
            onGoToAlbum = mediaMetadata.album?.let { album ->
                {
                    navController.navigate("album/${album.id}")
                    playerBottomSheetState.collapseSoft()
                }
            },
            onGoToArtist = mediaMetadata.artists.firstOrNull()?.id?.let { id ->
                {
                    navController.navigate("artist/$id")
                    playerBottomSheetState.collapseSoft()
                }
            },
        )
    }
    com.convx.music.ui.menu.AddToPlaylistDialog(
        isVisible = showAddToPlaylist,
        onGetSong = { playlist ->
            database.transaction { insert(mediaMetadata) }
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                playlist.playlist.browseId?.let { com.music.innertube.YouTube.addToPlaylist(it, mediaMetadata.id) }
            }
            listOf(mediaMetadata.id)
        },
        onDismiss = { showAddToPlaylist = false },
    )
}

/**
 * Output picker: a dark glass panel over the route button, with a now-playing header and
 * one capsule per output. The active output shows the current volume and a check mark.
 */
@Composable
fun V2RoutePopover(
    onDismiss: () -> Unit,
    title: String,
    artist: String,
    artworkUrl: String?,
    onMore: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val service = com.convx.music.LocalPlayerConnection.current?.service
    val deviceIcons = com.convx.music.core.rememberAudioDeviceIcons()
    val deviceNames = com.convx.music.core.rememberAudioDeviceNames()
    val audioManager = remember { context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager }
    var devices by remember { mutableStateOf<List<com.convx.music.core.AudioDevice>>(emptyList()) }
    fun reload() = com.convx.music.core.loadDevices(context, service?.preferredDeviceId, onSuccess = { devices = it }, onError = {})
    androidx.compose.runtime.LaunchedEffect(Unit) { reload() }
    val maxSystemVolume = remember { audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).coerceAtLeast(1) }
    var routeVolume by remember {
        mutableFloatStateOf(audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat())
    }
    val menuStretch = androidx.compose.runtime.rememberCoroutineScope().let { scope ->
        remember(scope) { com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight(animationScope = scope, radiusScale = 0.6f, sheenAlpha = 0.06f, spotAlpha = 0.32f, releasePulse = true) }
    }
    V2Floating(onDismiss = onDismiss, placement = AboveCenter) {
        CompositionLocalProvider(com.convx.music.ui.ios.foundation.LocalMenuLiquid provides true) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .width(300.dp)
                .iosMenuSurface(com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(34.dp), menuStretch)
                .padding(14.dp),
        ) {
            androidx.compose.foundation.layout.Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp),
            ) {
                coil3.compose.AsyncImage(
                    model = artworkUrl,
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(7.dp)),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                androidx.compose.foundation.layout.Column {
                    com.convx.music.ui.ios.compat.Text(title, color = Color.White, fontSize = 17.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, maxLines = 1)
                    com.convx.music.ui.ios.compat.Text(artist, color = Color.White, fontSize = 17.sp, maxLines = 1)
                }
            }
            val automatic = service?.preferredDeviceId == null
            androidx.compose.foundation.layout.Box(
                contentAlignment = androidx.compose.ui.Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(percent = 50))
                    .background(Color.White.copy(alpha = if (automatic) 0.35f else 0.12f))
                    .clickable {
                        service?.setPreferredAudioDevice(null)
                        reload()
                        onDismiss()
                    },
            ) {
                if (automatic) {
                    androidx.compose.foundation.layout.Box(
                        Modifier.fillMaxWidth((routeVolume / maxSystemVolume).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(Color.White),
                    )
                }
                androidx.compose.foundation.layout.Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                ) {
                    com.convx.music.ui.component.SymbolIcon(
                        symbol = com.convx.music.ui.component.sf("speaker.wave.2.fill"),
                        contentDescription = null,
                        size = 20.dp,
                        tint = if (automatic) Color.Black else Color.White.copy(alpha = 0.82f),
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                    com.convx.music.ui.ios.compat.Text(
                        "Automatic",
                        color = if (automatic) Color.Black else Color.White,
                        fontSize = 17.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    if (automatic) com.convx.music.ui.ios.component.IosCheckmark(color = Color.Black)
                }
            }
            devices.forEach { device ->
                val active = device.isActive
                val selected = active && !automatic
                androidx.compose.foundation.layout.Box(
                    contentAlignment = androidx.compose.ui.Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(percent = 50))
                        .background(Color.White.copy(alpha = if (selected) 0.35f else 0.12f))
                        .clickable {
                            service?.setPreferredAudioDevice(device.deviceId)
                            onDismiss()
                        },
                ) {
                    if (selected) {
                        androidx.compose.foundation.layout.Box(
                            Modifier.fillMaxWidth((routeVolume / maxSystemVolume).coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(Color.White)
                        )
                    }
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    ) {
                        com.convx.music.ui.component.SymbolIcon(
                            symbol = com.convx.music.ui.component.sf(deviceIcons.symbol(device.name, device.type)),
                            contentDescription = null,
                            size = 20.dp,
                            tint = if (selected) Color.Black else Color.White.copy(alpha = 0.82f),
                        )
                        androidx.compose.foundation.layout.Spacer(Modifier.width(10.dp))
                        com.convx.music.ui.ios.compat.Text(
                            deviceNames.displayName(device.name),
                            color = if (selected) Color.Black else Color.White,
                            fontSize = 17.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                        )
                        if (selected) com.convx.music.ui.ios.component.IosCheckmark(color = Color.Black)
                    }
                }
            }
            com.convx.music.core.VolumeControlRow(
                label = stringResource(R.string.volume),
                icon = androidx.compose.material.icons.Icons.Filled.VolumeUp,
                volume = routeVolume,
                maxVolume = maxSystemVolume,
                onVolumeChange = { newVolume ->
                    routeVolume = newVolume
                    audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, newVolume.toInt(), 0)
                },
            )
            com.convx.music.ui.ios.compat.Text(
                stringResource(R.string.v2_route_more),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { onDismiss(); onMore() }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            )
        }
        }
    }
}

