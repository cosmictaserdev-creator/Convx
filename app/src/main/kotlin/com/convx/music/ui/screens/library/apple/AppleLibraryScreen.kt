/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library.apple

import com.convx.music.ui.component.sf
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.composables.sfsymbols.SfSymbol
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowDownCircle
import com.composables.sfsymbols.monochrome.SFArrowDownToLine
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFMusicNote
import com.composables.sfsymbols.monochrome.SFIphone
import com.composables.sfsymbols.monochrome.SFHeart
import com.composables.sfsymbols.monochrome.SFArrowDownCircleDotted
import com.composables.sfsymbols.monochrome.SFChartBar
import com.composables.sfsymbols.monochrome.SFIcloudAndArrowUp
import com.composables.sfsymbols.monochrome.SFClock
import com.composables.sfsymbols.monochrome.SFMusicNoteList
import com.composables.sfsymbols.monochrome.SFPin
import com.composables.sfsymbols.monochrome.SFPlay
import com.composables.sfsymbols.monochrome.SFShuffle
import com.composables.sfsymbols.monochrome.SFSquareAndArrowUp
import com.composables.sfsymbols.monochrome.SFSquareStack
import com.composables.sfsymbols.monochrome.SFStar
import com.composables.sfsymbols.monochrome.SFStarFill
import com.composables.sfsymbols.monochrome.SFTextBadgePlus
import com.composables.sfsymbols.monochrome.SFTextLineFirstAndArrowtriangleForward
import com.composables.sfsymbols.monochrome.SFTextLineLastAndArrowtriangleForward
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.LocalItem
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.library.PlaylistSummary
import com.convx.music.library.LocalPlaylistSongDataSource
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.component.AppleContextMenu
import com.convx.music.ui.ios.component.rememberAppleMenuController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import sh.calvin.reorderable.ReorderableItem
import androidx.compose.foundation.lazy.grid.items as gridItems
import sh.calvin.reorderable.rememberReorderableLazyGridState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import com.convx.music.ui.ios.component.IosGlyph
import com.convx.music.ui.ios.component.IosMenuItem
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.menu.AddToPlaylistDialog
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.localPlaylistRoute
import com.convx.music.ui.utils.sharedArtworkSource
import com.convx.music.ui.theme.SfProFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Apple Music's Library destinations, plus the app's Local, Liked, Cached, Most Played,
 * Uploaded, and History lists, which can be added to the same list via Edit.
 */
enum class AppleLibraryDestination { Playlists, Artists, Albums, Songs, Local, Downloaded, Liked, Podcasts, Cached, Top, Uploaded, History }

/**
 * Library home with pinned items, category links, and recently added content.
 * Long-pressing a cover opens its [AppleContextMenu]. The title, menu, and avatar
 * come from the shared app bar.
 */
@Composable
fun AppleLibraryScreen(
    navController: NavController,
    onOpen: (AppleLibraryDestination) -> Unit,
    viewModel: AppleLibraryViewModel = koinViewModel(),
) {
    val pinned by viewModel.pinned.collectAsStateWithLifecycle()
    val recent by viewModel.recentlyAdded.collectAsStateWithLifecycle()
    val localOnly by viewModel.localOnly.collectAsStateWithLifecycle()
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    // Online library needs no storage permission; only local-only mode is gated on it.
    val needsAccess = localOnly && !permission.granted
    LaunchedEffect(permission.granted) { if (permission.granted) viewModel.scanDevice() }
    val colors = IosTheme.colors

    val open: (LocalItem) -> Unit = { item ->
        when (item) {
            is Album -> navController.navigate(albumRoute(item.id, item.thumbnailUrl))
            is Playlist -> navController.navigate(localPlaylistRoute(item.id, item.thumbnails.firstOrNull()))
            else -> Unit
        }
    }

    val pageInsets = LocalPlayerAwareWindowInsets.current.asPaddingValues()
    val tabletLayout = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600
    val firstItemGap = if (tabletLayout) 8.dp else {
        maxOf(pageInsets.calculateTopPadding(), 115.dp) - pageInsets.calculateTopPadding()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.systemBackground),
        contentPadding = pageInsets,
    ) {
        // Reference: 13pt between the large title and the first row.
        item(key = "top_gap", contentType = "gap") { Spacer(Modifier.height(firstItemGap)) }
        if (!needsAccess && pinned.isNotEmpty()) {
            item(key = "pinned", contentType = "pinned") {
                PinnedGrid(pinned, onOpen = open, onReorder = viewModel::reorderPinned)
            }
        }

        items(Rows.filter { !localOnly || it.destination in DeviceLibraryDestinations }, key = { it.destination }, contentType = { "row" }) { row ->
            CategoryRow(stringResource(row.label), row.icon) { onOpen(row.destination) }
        }

        if (needsAccess) {
            item(key = "library_access", contentType = "placeholder") { LocalAccessCard(permission.blocked, permission.request) }
        } else if (recent.isNotEmpty()) {
            item(key = "recent_title", contentType = "title") {
                Text(
                    text = stringResource(R.string.recently_added),
                    style = SfStyle(22.sp, 28.sp, FontWeight.Bold, (-0.26).sp),
                    color = colors.label,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 10.dp),
                )
            }
            items(recent.chunked(2), key = { "recent_" + it.first().id }, contentType = { "recent_row" }) { pair ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 18.dp),
                ) {
                    pair.forEach { item ->
                        LibraryItemMenu(item, onOpen = { open(item) }, modifier = Modifier.weight(1f)) {
                            RecentTile(item)
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        } else {
            item(key = "library_empty", contentType = "placeholder") {
                com.convx.music.ui.component.LocalMusicEmptyState(onScan = viewModel::scanDevice)
            }
        }
    }
}

private class CategoryRowSpec(val destination: AppleLibraryDestination, val label: Int, val icon: SfSymbol)

private val DeviceLibraryDestinations = setOf(
    AppleLibraryDestination.Playlists, AppleLibraryDestination.Artists,
    AppleLibraryDestination.Albums, AppleLibraryDestination.Songs, AppleLibraryDestination.Local,
)

private val Rows = listOf(
    CategoryRowSpec(AppleLibraryDestination.Playlists, R.string.filter_playlists, sf("SFMusicNoteList")),
    CategoryRowSpec(AppleLibraryDestination.Artists, R.string.filter_artists, sf("microphone.dynamic.on.stand")),
    CategoryRowSpec(AppleLibraryDestination.Albums, R.string.filter_albums, sf("SFSquareStack")),
    CategoryRowSpec(AppleLibraryDestination.Songs, R.string.filter_songs, sf("SFMusicNote")),
    CategoryRowSpec(AppleLibraryDestination.Local, R.string.filter_local, sf("SFIphone")),
    CategoryRowSpec(AppleLibraryDestination.Downloaded, R.string.filter_downloaded, sf("SFArrowDownCircle")),
    CategoryRowSpec(AppleLibraryDestination.Liked, R.string.filter_liked, sf("SFHeart")),
    CategoryRowSpec(AppleLibraryDestination.Podcasts, R.string.filter_podcasts, sf("microphone")),
    CategoryRowSpec(AppleLibraryDestination.Cached, R.string.cached_playlist, sf("SFArrowDownCircleDotted")),
    CategoryRowSpec(AppleLibraryDestination.Top, R.string.my_top, sf("SFChartBar")),
    CategoryRowSpec(AppleLibraryDestination.Uploaded, R.string.uploaded_playlist, sf("SFIcloudAndArrowUp")),
    CategoryRowSpec(AppleLibraryDestination.History, R.string.history, sf("SFClock")),
)

/** SF Pro at an explicit size — never the theme's font pick. */
internal fun SfStyle(size: androidx.compose.ui.unit.TextUnit, line: androidx.compose.ui.unit.TextUnit, weight: FontWeight = FontWeight.Normal, tracking: androidx.compose.ui.unit.TextUnit = 0.sp) =
    TextStyle(fontFamily = SfProFontFamily, fontSize = size, lineHeight = line, fontWeight = weight, letterSpacing = tracking)

/** Library category row with an icon, label, and divider. */
@Composable
private fun CategoryRow(label: String, icon: SfSymbol, onClick: () -> Unit) {
    val colors = IosTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .iosRowPress(onClick = onClick),
    ) {
        Box(Modifier.padding(start = 16.dp, end = 8.dp).width(28.dp), contentAlignment = Alignment.Center) {
            IosGlyph(icon, tint = IosTheme.tint, box = 23.dp, enterOnly = true)
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 16.dp),
        ) {
            Text(
                text = label,
                style = SfStyle(17.sp, 22.sp, tracking = (-0.43).sp),
                color = colors.label,
                maxLines = 1,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            Chevron(Modifier.align(Alignment.CenterEnd))
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(IosMetrics.SeparatorThickness)
                    .background(colors.separator),
            )
        }
    }
}

/** SF chevron.right, 17pt semibold, tertiaryLabel: ~7x12. */
@Composable
internal fun Chevron(modifier: Modifier = Modifier) {
    val color = IosTheme.colors.tertiaryLabel
    Canvas(modifier.size(width = 7.dp, height = 12.dp)) {
        val w = 2.dp.toPx()
        drawPath(
            Path().apply {
                moveTo(w / 2, w / 2)
                lineTo(size.width - w / 2, size.height / 2)
                lineTo(w / 2, size.height - w / 2)
            },
            color = color,
            style = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
internal fun Cover(model: Any?, radius: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    val shape = ContinuousRoundedRectangle(radius)
    AsyncImage(
        model = model,
        onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
            com.convx.music.ui.utils.rememberArtworkSourceOwner(), model as? String,
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(IosTheme.colors.secondarySystemFill)
            .border(0.5.dp, IosTheme.colors.separator.copy(alpha = 0.25f), shape),
    )
}

private val LocalItem.cover: String?
    get() = when (this) {
        is Album -> thumbnailUrl
        is Playlist -> thumbnails.firstOrNull()
        else -> thumbnailUrl
    }

@Composable
private fun LocalItem.subtitle(): String = when (this) {
    is Album -> artists.joinToString { it.name }
    is Playlist -> pluralStringResource(R.plurals.n_song, songCount, songCount)
    else -> ""
}

/**
 * Pinned items, three across (reference clip A), rearranged with sh.calvin.reorderable.
 *
 * One hold does both, as on iOS: the hold lifts the item and pops its menu; moving the
 * finger past touch slop drops the menu and the library carries the drag — the item
 * rides the finger and the others make room. Letting go saves the order. A tap opens.
 */
@Composable
private fun PinnedGrid(
    pinned: List<LocalItem>,
    onOpen: (LocalItem) -> Unit,
    onReorder: (List<LocalItem>) -> Unit,
) {
    val columns = 3
    val gap = 12.dp
    val rowGap = 16.dp
    // Local order while a drag is live; follows the database otherwise.
    var order by remember { mutableStateOf(pinned) }
    var dragging by remember { mutableStateOf(false) }
    if (!dragging && order != pinned) order = pinned

    val gridState = rememberLazyGridState()
    val reorderState = rememberReorderableLazyGridState(gridState) { from, to ->
        order = order.toMutableList().apply { add(to.index, removeAt(from.index)) }
    }

    BoxWithConstraints(Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp)) {
        // Not scrollable, and sized to all its rows: it lives inside the page's list.
        val tileW = (maxWidth - gap * (columns - 1)) / columns
        val rows = (order.size + columns - 1) / columns
        val height = (tileW + PinnedLabelHeight) * rows + rowGap * (rows - 1).coerceAtLeast(0)
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalArrangement = Arrangement.spacedBy(rowGap),
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
        ) {
            gridItems(order, key = { it.id }) { item ->
                ReorderableItem(reorderState, key = item.id) { isDragging ->
                    val controller = rememberAppleMenuController()
                    val lift by animateFloatAsState(if (isDragging && !controller.isOpen) 1.08f else 1f, label = "lift")
                    val slop = LocalViewConfiguration.current.touchSlop
                    val longPress = LocalViewConfiguration.current.longPressTimeoutMillis
                    val haptics = LocalHapticFeedback.current
                    LibraryItemMenu(
                        item,
                        onOpen = { onOpen(item) },
                        controller = controller,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = lift
                                scaleY = lift
                            }
                            .longPressDraggableHandle(
                                onDragStarted = {
                                    dragging = true
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    controller.open()
                                },
                                onDragStopped = {
                                    dragging = false
                                    if (order.map { it.id } != pinned.map { it.id }) onReorder(order)
                                },
                            )
                            // Watches (never consumes) the same press: a quick lift is a
                            // tap; travel past slop once the menu is up means "drag".
                            .pointerInput(item.id) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val start = down.uptimeMillis
                                    while (true) {
                                        val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull() ?: break
                                        val moved = (change.position - down.position).getDistance() > slop
                                        if (moved && controller.isOpen) controller.cancel()
                                        if (!change.pressed) {
                                            if (!moved && change.uptimeMillis - start < longPress) onOpen(item)
                                            break
                                        }
                                    }
                                }
                            },
                    ) { PinnedTile(item) }
                }
            }
        }
    }
}

/** Space under a pinned cover for its one-line title. */
private val PinnedLabelHeight = 24.dp

/** Pinned tile (reference clip): cover, 10dp corners, title centred under it. */
@Composable
private fun PinnedTile(item: LocalItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Cover(item.cover, 10.dp, Modifier.sharedArtworkSource(item.id, item.cover))
        Text(
            text = item.title,
            style = SfStyle(13.sp, 18.sp, tracking = (-0.08).sp).copy(textAlign = TextAlign.Center),
            color = IosTheme.colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** Recently added library card with artwork, title, and artist. */
@Composable
internal fun RecentTile(item: LocalItem) {
    Column {
        Cover(item.cover, 10.dp, Modifier.sharedArtworkSource(item.id, item.cover))
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.title,
            style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp),
            color = IosTheme.colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = item.subtitle(),
            style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp),
            color = IosTheme.colors.secondaryLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun RecentTile(item: PlaylistSummary) {
    val cover = item.artworkUrls.firstOrNull()
    Column {
        Cover(cover, 10.dp, Modifier.sharedArtworkSource(item.id, cover))
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.name,
            style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp),
            color = IosTheme.colors.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = pluralStringResource(R.plurals.n_song, item.songCount, item.songCount),
            style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp),
            color = IosTheme.colors.secondaryLabel,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** The preview card content: large cover, title, and supporting text. */
@Composable
private fun PreviewCard(cover: String?, title: String, subtitle: String) {
    Cover(cover, 12.dp)
    Text(
        text = title,
        style = SfStyle(15.sp, 20.sp, FontWeight.SemiBold, (-0.23).sp),
        color = IosTheme.colors.label,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 10.dp, start = 2.dp),
    )
    Text(
        text = subtitle,
        style = SfStyle(13.sp, 18.sp, tracking = (-0.08).sp),
        color = IosTheme.colors.secondaryLabel,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
    )
}

/**
 * Press-and-hold on a Library cover, with the reference menu: Download · Favourite ·
 * Share across the top, then Play / Shuffle, Pin / Add to a Playlist…, Play Next /
 * Play Last.
 */
@Composable
internal fun LibraryItemMenu(
    item: PlaylistSummary,
    onOpen: () -> Unit,
    onTogglePin: suspend () -> Unit,
    modifier: Modifier = Modifier,
    controller: com.convx.music.ui.ios.component.AppleMenuController? = null,
    content: @Composable () -> Unit,
) {
    val localPlaylistSongDataSource = koinInject<LocalPlaylistSongDataSource>()
    LibraryItemMenu(
        item = LibraryMenuItem(
            title = item.name,
            cover = item.artworkUrls.firstOrNull(),
            subtitle = pluralStringResource(R.plurals.n_song, item.songCount, item.songCount),
            loadSongs = { localPlaylistSongDataSource.loadSongs(item.id).map { it.song } },
            shareUrl = item.browseId?.let { "https://music.youtube.com/playlist?list=$it" },
            isPinned = item.pinnedAtEpochMillis != null,
            pinLabel = R.string.pin_playlist,
            unpinLabel = R.string.unpin_playlist,
            onTogglePin = onTogglePin,
        ),
        onOpen = onOpen,
        modifier = modifier,
        controller = controller,
        content = content,
    )
}

@Composable
internal fun LibraryItemMenu(
    item: LocalItem,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    controller: com.convx.music.ui.ios.component.AppleMenuController? = null,
    content: @Composable () -> Unit,
) {
    val database = LocalDatabase.current.get()
    val localPlaylistSongDataSource = koinInject<LocalPlaylistSongDataSource>()
    val album = item as? Album
    val playlist = item as? Playlist
    val onTogglePin: (suspend () -> Unit)? = when {
        playlist != null -> suspend { database.query { update(playlist.playlist.togglePin()) } }
        album != null -> suspend { database.query { update(album.album.togglePin()) } }
        else -> null
    }
    LibraryItemMenu(
        item = LibraryMenuItem(
            title = item.title,
            cover = item.cover,
            subtitle = item.subtitle(),
            loadSongs = {
                when (item) {
                    is Album -> withContext(Dispatchers.IO) { database.albumSongs(item.id).first() }
                    is Playlist -> localPlaylistSongDataSource.loadSongs(item.id).map { it.song }
                    else -> emptyList()
                }
            },
            shareUrl = when (item) {
                is Album -> item.album.playlistId?.let { "https://music.youtube.com/playlist?list=$it" }
                is Playlist -> item.playlist.shareLink
                else -> null
            },
            isFavourite = album?.album?.bookmarkedAt != null,
            onToggleFavourite = album?.let { { database.query { update(it.album.toggleLike()) } } },
            isPinned = when (item) {
                is Album -> item.album.pinnedAt != null
                is Playlist -> item.playlist.pinnedAt != null
                else -> null
            },
            pinLabel = when (item) {
                is Album -> R.string.pin_album
                is Playlist -> R.string.pin_playlist
                else -> null
            },
            unpinLabel = when (item) {
                is Album -> R.string.unpin_album
                is Playlist -> R.string.unpin_playlist
                else -> null
            },
            onTogglePin = onTogglePin,
        ),
        onOpen = onOpen,
        modifier = modifier,
        controller = controller,
        content = content,
    )
}

private data class LibraryMenuItem(
    val title: String,
    val cover: String?,
    val subtitle: String,
    val loadSongs: suspend () -> List<Song>,
    val shareUrl: String? = null,
    val isFavourite: Boolean? = null,
    val onToggleFavourite: (() -> Unit)? = null,
    val isPinned: Boolean? = null,
    val pinLabel: Int? = null,
    val unpinLabel: Int? = null,
    val onTogglePin: (suspend () -> Unit)? = null,
)

@Composable
private fun LibraryItemMenu(
    item: LibraryMenuItem,
    onOpen: () -> Unit,
    modifier: Modifier,
    controller: com.convx.music.ui.ios.component.AppleMenuController?,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val downloadUtil = LocalDownloadUtil.current.get()
    val scope = rememberCoroutineScope()
    var addToPlaylist by remember { mutableStateOf(false) }

    fun withSongs(block: (List<Song>) -> Unit) {
        scope.launch { item.loadSongs().takeIf { it.isNotEmpty() }?.let(block) }
    }
    fun glyph(v: SfSymbol): @Composable (Color) -> Unit = { tint -> IosGlyph(v, tint = tint, box = 20.dp) }

    val quick = listOfNotNull(
        IosMenuItem(stringResource(R.string.action_download), icon = glyph(sf("SFArrowDownToLine"))) {
            withSongs { songs -> downloadSongs(context, songs.map { DownloadTarget(it.id, it.song.title) }, downloadUtil.downloads.value) }
        },
        item.onToggleFavourite?.let { toggleFavourite ->
            IosMenuItem(
                stringResource(R.string.favorite),
                icon = glyph(if (item.isFavourite == true) sf("SFStarFill") else sf("SFStar")),
            ) { toggleFavourite() }
        },
        item.shareUrl?.let { url ->
            IosMenuItem(stringResource(R.string.share), icon = glyph(sf("SFSquareAndArrowUp"))) {
                context.startActivity(
                    Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, url), null),
                )
            }
        },
    )
    val pinAction = item.onTogglePin?.let { togglePin ->
        val label = if (item.isPinned == true) item.unpinLabel else item.pinLabel
        label?.let {
            IosMenuItem(stringResource(it), icon = glyph(sf("SFPin"))) { scope.launch { togglePin() } }
        }
    }
    val sections = listOf(
        IosMenuSection(
            listOf(
                IosMenuItem(stringResource(R.string.play), icon = glyph(sf("SFPlay"))) {
                    withSongs { songs -> playerConnection?.playQueue(ListQueue(title = item.title, items = songs.map(Song::toMediaItem))) }
                },
                IosMenuItem(stringResource(R.string.shuffle), icon = glyph(sf("SFShuffle"))) {
                    withSongs { songs -> playerConnection?.playQueue(ListQueue(title = item.title, items = songs.shuffled().map(Song::toMediaItem))) }
                },
            ),
        ),
        IosMenuSection(
            listOfNotNull(
                pinAction,
                IosMenuItem(stringResource(R.string.add_to_playlist_ellipsis), icon = glyph(sf("SFTextBadgePlus"))) {
                    addToPlaylist = true
                },
            ),
        ),
        IosMenuSection(
            listOf(
                IosMenuItem(stringResource(R.string.play_next), icon = glyph(sf("SFTextLineFirstAndArrowtriangleForward"))) {
                    withSongs { songs -> playerConnection?.playNext(songs.map(Song::toMediaItem)) }
                },
                IosMenuItem(stringResource(R.string.play_last), icon = glyph(sf("SFTextLineLastAndArrowtriangleForward"))) {
                    withSongs { songs -> playerConnection?.addToQueue(songs.map(Song::toMediaItem)) }
                },
            ),
        ),
    )

    com.convx.music.ui.utils.ArtworkSourceHost(modifier, fromLibrary = true) { sourceModifier ->
        AppleContextMenu(
            sections = sections,
            quickActions = quick,
            preview = { PreviewCard(item.cover, item.title, item.subtitle) },
            onClick = onOpen,
            modifier = sourceModifier,
            controller = controller,
            content = content,
        )
    }

    if (addToPlaylist) {
        AddToPlaylistDialog(
            isVisible = true,
            onGetSong = { item.loadSongs().map { it.id } },
            onDismiss = { addToPlaylist = false },
        )
    }
}
