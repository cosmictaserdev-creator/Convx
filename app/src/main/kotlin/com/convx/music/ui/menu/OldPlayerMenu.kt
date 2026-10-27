package com.convx.music.ui.menu

import com.convx.music.ui.component.DownloadRingIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.menuBottomInset
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import com.convx.music.ui.ios.compat.HorizontalDivider
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFBellFill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import com.music.innertube.YouTube
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.ListItemHeight
import com.convx.music.listentogether.RoomRole
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.ExoDownloadService
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.ui.component.BottomSheetState
import com.convx.music.ui.component.ListDialog
import com.convx.music.ui.component.IosMenuGroup
import com.convx.music.ui.component.IosMenuItemData
import com.convx.music.ui.component.NewAction
import com.convx.music.ui.component.NewActionGrid
import com.convx.music.ui.component.VolumeSlider
import com.convx.music.ui.theme.rememberGlobalAccentColors
import com.convx.music.constants.EnableSaavnStreamingKey
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OldPlayerMenu(
    mediaMetadata: MediaMetadata?,
    navController: NavController,
    playerBottomSheetState: BottomSheetState,
    onShowDetailsDialog: () -> Unit,
    onDismiss: () -> Unit,
) {
    mediaMetadata ?: return
    val context = LocalContext.current
    val ringtoneViewModel = com.convx.music.LocalRingtoneViewModel.current
    val database = LocalDatabase.current.get()
    val playerConnection = LocalPlayerConnection.current ?: return
    val coroutineScope = rememberCoroutineScope()
    val playerVolume = playerConnection.service.playerVolume.collectAsStateWithLifecycle()

    // Cast state for volume control
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castVolume by castHandler?.castVolume?.collectAsStateWithLifecycle() ?: remember { mutableFloatStateOf(1f) }
    val castDeviceName by castHandler?.castDeviceName?.collectAsStateWithLifecycle() ?: remember { mutableStateOf<String?>(null) }

    val download by LocalDownloadUtil.current.get().getDownload(mediaMetadata.id).collectAsState(initial = null)

    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsState(initial = RoomRole.NONE)
    // See Player.kt: gated on control mode, not on role.
    val canControlTogether = listenTogetherManager?.canControl?.collectAsState(initial = true)
    val isListenTogetherGuest = canControlTogether?.value == false

    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val librarySong by database.song(mediaMetadata.id).collectAsState(initial = null)
    val playbackSnapshot by playerConnection.playbackSnapshot.collectAsStateWithLifecycle()
    val repeatMode = playbackSnapshot.repeatMode
    val shuffleModeEnabled = playbackSnapshot.shuffleEnabled
    val (saavnEnabled) = rememberPreference(EnableSaavnStreamingKey, defaultValue = false)

    val artists = remember(mediaMetadata.artists) {
        mediaMetadata.artists.filter { it.id != null }
    }

    var showChoosePlaylistDialog by rememberSaveable { mutableStateOf(false) }
    var showListenTogetherDialog by rememberSaveable { mutableStateOf(false) }
    var showSelectArtistDialog by rememberSaveable { mutableStateOf(false) }
    var showPitchTempoDialog by rememberSaveable { mutableStateOf(false) }

    AddToPlaylistDialog(
        isVisible = showChoosePlaylistDialog,
        onGetSong = { playlist ->
            database.transaction { insert(mediaMetadata) }
            coroutineScope.launch(Dispatchers.IO) {
                playlist.playlist.browseId?.let { YouTube.addToPlaylist(it, mediaMetadata.id) }
            }
            onDismiss()
            listOf(mediaMetadata.id)
        },
        onDismiss = { showChoosePlaylistDialog = false }
    )

    ListenTogetherDialog(
        visible = showListenTogetherDialog,
        mediaMetadata = mediaMetadata,
        onDismiss = { showListenTogetherDialog = false }
    )

    if (showSelectArtistDialog) {
        ListDialog(onDismiss = { showSelectArtistDialog = false }) {
            items(artists, key = { it.id!! }) { artist ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(ListItemHeight)
                        .clickable {
                            navController.navigate("artist/${artist.id}")
                            showSelectArtistDialog = false
                            playerBottomSheetState.collapseSoft()
                            onDismiss()
                        }
                        .padding(horizontal = 24.dp),
                ) {
                    Text(
                        text = artist.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }

    if (showPitchTempoDialog) {
        TempoPitchDialog(onDismiss = { showPitchTempoDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 6.dp),
    ) {
        // Show Cast indicator when casting
        if (isCasting && castDeviceName != null) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                SymbolIcon(symbol = sf("airplayvideo"), contentDescription = null, size = 24.dp, tint = AppTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.casting_to, castDeviceName ?: ""),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.primary
                )
            }
        }

        // "Hide Volume Bar" only affects the player's own inline volume row
        // now — this menu copy always shows regardless of the setting.
        VolumeSlider(
            value = if (isCasting) castVolume else playerVolume.value,
            onValueChange = { volume ->
                if (isCasting) {
                    castHandler?.setVolume(volume)
                } else {
                    playerConnection.service.playerVolume.value = volume
                }
            },
            modifier = Modifier.fillMaxWidth(),
            accentColor = rememberGlobalAccentColors().first
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    HorizontalDivider()

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
        contentPadding = PaddingValues(
            start = 0.dp,
            top = 0.dp,
            end = 0.dp,
            bottom = 8.dp + menuBottomInset(),
        ),
    ) {
        // Share & Playlist Add
        item {
            val startingRadioText = stringResource(R.string.starting_radio)
            NewActionGrid(
                actions = listOfNotNull(
                    if (!isListenTogetherGuest) {
                        NewAction(
                            icon = {
                                SymbolIcon(symbol = sf("dot.radiowaves.left.and.right"), contentDescription = null, size = 32.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                            },
                            text = stringResource(R.string.start_an_radio),
                            onClick = {
                                Toast.makeText(context, startingRadioText, Toast.LENGTH_SHORT).show()
                                playerConnection.startRadioSeamlessly()
                                onDismiss()
                            }
                        )
                    } else null,
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("text.badge.plus"), contentDescription = null, size = 32.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.add_to_an_playlist),
                        onClick = { showChoosePlaylistDialog = true }
                    ),
                    NewAction(
                        icon = {
                            SymbolIcon(symbol = sf("square.and.arrow.up"), contentDescription = null, size = 32.dp, tint = AppTheme.colorScheme.onSurfaceVariant)
                        },
                        text = stringResource(R.string.share),
                        onClick = {
                            val intent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "https://music.youtube.com/watch?v=${mediaMetadata.id}"
                                )
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, null))
                            onDismiss()
                        }
                    )
                ),
                columns = if (isListenTogetherGuest) 2 else 3,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            IosMenuGroup(
                items = buildList {
                    if (!isListenTogetherGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(stringResource(R.string.shuffle)) },
                                icon = {
                                    SymbolIcon(symbol = sf("shuffle"), contentDescription = null, size = 24.dp, tint = if (shuffleModeEnabled) AppTheme.colorScheme.primary else androidx.compose.material3.LocalContentColor.current)
                                },
                                onClick = {
                                    playerConnection.toggleShuffle()
                                    onDismiss()
                                }
                            )
                        )
                    }

                    when (download?.state) {
                        Download.STATE_COMPLETED -> {
                            add(
                                IosMenuItemData(
                                    title = { Text(stringResource(R.string.remove_download)) },
                                    icon = {
                                        SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 24.dp)
                                    },
                                    onClick = {
                                        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, mediaMetadata.id, false)
                                        onDismiss()
                                    }
                                )
                            )
                        }
                        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                            add(
                                IosMenuItemData(
                                    title = { Text(stringResource(R.string.downloading)) },
                                    icon = {
                                        DownloadRingIcon(download)
                                    },
                                    onClick = {
                                        DownloadService.sendRemoveDownload(context, ExoDownloadService::class.java, mediaMetadata.id, false)
                                        onDismiss()
                                    }
                                )
                            )
                        }
                        else -> {
                            add(
                                IosMenuItemData(
                                    title = { Text(stringResource(R.string.action_download)) },
                                    icon = {
                                        SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 24.dp)
                                    },
                                    onClick = {
                                        database.transaction { insert(mediaMetadata) }
                                        val downloadRequest = DownloadRequest.Builder(mediaMetadata.id, mediaMetadata.id.toUri())
                                            .setCustomCacheKey(mediaMetadata.id)
                                            .setData(mediaMetadata.title.toByteArray())
                                            .build()
                                        DownloadService.sendAddDownload(context, ExoDownloadService::class.java, downloadRequest, false)
                                        onDismiss()
                                    }
                                )
                            )
                        }
                    }

                    val isLiked = currentSong?.song?.liked == true
                    add(
                        IosMenuItemData(
                            title = { Text(stringResource(if (isLiked) R.string.liked else R.string.like)) },
                            icon = {
                                SymbolIcon(symbol = if (isLiked) sf("heart.fill") else sf("heart"), contentDescription = null, size = 24.dp, tint = if (isLiked) AppTheme.colorScheme.error else androidx.compose.material3.LocalContentColor.current)
                            },
                            onClick = {
                                playerConnection.toggleLike()
                                onDismiss()
                            }
                        )
                    )

                    if (!isListenTogetherGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(stringResource(R.string.repeat)) },
                                icon = {
                                    Icon(
                                        painter = painterResource(
                                            when (repeatMode) {
                                                PlaybackRepeatMode.OFF, PlaybackRepeatMode.ALL -> R.drawable.repeat
                                                PlaybackRepeatMode.ONE -> R.drawable.repeat_one
                                                else -> R.drawable.repeat
                                            }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (repeatMode != PlaybackRepeatMode.OFF) AppTheme.colorScheme.primary else androidx.compose.material3.LocalContentColor.current
                                    )
                                },
                                onClick = {
                                    playerConnection.cycleRepeatMode()
                                }
                            )
                        )
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Artist & Add to library
        item {
            IosMenuGroup(
                items = buildList {
                    // Always visible. Re-triggers the full resolution chain.
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.retry_stream)) },
                            description = {
                                Text(
                                    text = if (saavnEnabled)
                                        stringResource(R.string.retry_stream_desc_saavn)
                                    else
                                        stringResource(R.string.retry_stream_desc_yt)
                                )
                            },
                            icon = {
                                SymbolIcon(symbol = sf("arrow.counterclockwise"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                val player = playerConnection.player
                                player.seekTo(0)
                                player.prepare()
                                player.play()
                                onDismiss()
                            }
                        )
                    )

                    if (artists.isNotEmpty()) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.view_artist)) },
                                description = {
                                    Text(
                                        text = mediaMetadata.artists.joinToString { it.name },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                icon = {
                                    SymbolIcon(symbol = sf("music.mic"), contentDescription = null, size = 24.dp)
                                },
                                onClick = {
                                    if (mediaMetadata.artists.size == 1) {
                                        navController.navigate("artist/${mediaMetadata.artists[0].id}")
                                        playerBottomSheetState.collapseSoft()
                                        onDismiss()
                                    } else {
                                        showSelectArtistDialog = true
                                    }
                                }
                            )
                        )
                    }

                    if (mediaMetadata.album != null) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.view_album)) },
                                description = {
                                    Text(
                                        text = mediaMetadata.album.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                icon = {
                                    SymbolIcon(symbol = sf("square.stack"), contentDescription = null, size = 24.dp)
                                },
                                onClick = {
                                    navController.navigate("album/${mediaMetadata.album.id}")
                                    playerBottomSheetState.collapseSoft()
                                    onDismiss()
                                }
                            )
                        )
                    }

                    val isInLibrary = librarySong?.song?.inLibrary != null
                    add(
                        IosMenuItemData(
                            title = { 
                                Text(
                                    text = stringResource(
                                        if (isInLibrary) R.string.remove_from_library
                                        else R.string.add_to_library
                                    )
                                )
                            },
                            icon = {
                                SymbolIcon(symbol = 
                                        if (isInLibrary) sf("text.badge.checkmark")
                                        else sf("text.badge.plus")
                                    , contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                playerConnection.toggleLibrary()
                                onDismiss()
                            }
                        )
                    )
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // Listen together
        item {
            IosMenuGroup(
                items = buildList {
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.listen_together)) },
                            icon = {
                                SymbolIcon(symbol = sf("person.2.fill"), contentDescription = null, size = 24.dp)
                            },
                            onClick = { onDismiss(); navController.navigate("listen_together/party") }
                        )
                    )
                    if (isListenTogetherGuest) {
                        add(
                            IosMenuItemData(
                                title = { Text(text = stringResource(R.string.resync)) },
                                icon = {
                                    SymbolIcon(symbol = sf("arrow.counterclockwise"), contentDescription = null, size = 24.dp)
                                },
                                onClick = {
                                    listenTogetherManager.requestSync()
                                    onDismiss()
                                }
                            )
                        )
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // details, eq, adv
        item {
            IosMenuGroup(
                items = buildList {
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.details)) },
                            description = { Text(text = stringResource(R.string.details_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("info.circle"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                onShowDetailsDialog()
                                onDismiss()
                            }
                        )
                    )

                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.equalizer)) },
                            description = { Text(text = stringResource(R.string.equalizer_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("slider.vertical.3"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                navController.navigate("settings/equalizer")
                                onDismiss()
                            }
                        )
                    )

                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.advanced)) },
                            description = { Text(text = stringResource(R.string.advanced_desc)) },
                            icon = {
                                SymbolIcon(symbol = sf("slider.horizontal.3"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                showPitchTempoDialog = true
                            }
                        )
                    )
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.set_as_ringtone)) },
                            icon = {
                                SymbolIcon(symbol = sf("SFBellFill"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                ringtoneViewModel.showTrimmer(
                                    mediaMetadata.id,
                                    mediaMetadata.title,
                                    mediaMetadata.artists.joinToString { it.name },
                                    mediaMetadata.duration
                                )
                                onDismiss()
                            }
                        )
                    )
                    add(
                        IosMenuItemData(
                            title = { Text(text = stringResource(R.string.ambient_mode)) },
                            icon = {
                                SymbolIcon(symbol = sf("arrow.up.left.and.arrow.down.right"), contentDescription = null, size = 24.dp)
                            },
                            onClick = {
                                navController.navigate("ambient_mode")
                                playerBottomSheetState.collapseSoft()
                                onDismiss()
                            }
                        )
                    )
                }
            )
        }
    }
}
