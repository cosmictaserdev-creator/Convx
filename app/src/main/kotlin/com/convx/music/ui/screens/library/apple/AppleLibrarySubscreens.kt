/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library.apple

import com.convx.music.ui.utils.sharedArtworkSource

import com.convx.music.ui.component.sf
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import com.composables.sfsymbols.SfSymbol
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowDownToLine
import com.composables.sfsymbols.monochrome.SFChevronLeft
import com.composables.sfsymbols.monochrome.SFMusicNoteList
import com.composables.sfsymbols.monochrome.SFMusicMicrophone
import com.composables.sfsymbols.monochrome.SFMusicNoteHouse
import com.composables.sfsymbols.monochrome.SFPlayFill
import com.composables.sfsymbols.monochrome.SFPlus
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
import com.convx.music.constants.AlbumSortDescendingKey
import com.convx.music.constants.AlbumSortType
import com.convx.music.constants.AlbumSortTypeKey
import com.convx.music.constants.ArtistSortDescendingKey
import com.convx.music.constants.ArtistSortType
import com.convx.music.constants.ArtistSortTypeKey
import com.convx.music.constants.LibraryViewType
import com.convx.music.constants.PlaylistSortDescendingKey
import com.convx.music.constants.PlaylistSortType
import com.convx.music.constants.PlaylistSortTypeKey
import com.convx.music.constants.PlaylistViewTypeKey
import com.convx.music.constants.SongSortDescendingKey
import com.convx.music.constants.SongSortType
import com.convx.music.constants.SongSortTypeKey
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.LocalItem
import com.convx.music.db.entities.Song
import com.convx.music.extensions.toMediaItem
import com.convx.music.playback.DownloadTarget
import com.convx.music.playback.downloadSongs
import com.convx.music.playback.queues.ListQueue
import com.convx.music.ui.component.CreatePlaylistDialog
import com.convx.music.ui.component.TopFadeBlur
import com.convx.music.ui.component.ListScrollRail
import com.convx.music.ui.component.rememberTitleCollapseProgressState
import com.convx.music.ui.ios.compat.DropdownMenu
import com.convx.music.ui.ios.compat.DropdownMenuItem
import com.convx.music.ui.ios.compat.IosMenuDivider
import com.convx.music.ui.ios.compat.menuTrigger
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.component.AppleContextMenu
import com.convx.music.ui.ios.component.IosCheckmark
import com.convx.music.ui.ios.component.IosGlyph
import com.convx.music.ui.ios.component.IosMenuItem
import com.convx.music.ui.ios.component.IosMenuSection
import com.convx.music.ui.ios.foundation.IosMenuMaterial
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.foundation.iosGlass
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosRowPress
import com.convx.music.ui.menu.AddToPlaylistDialog
import com.convx.music.ui.utils.albumRoute
import com.convx.music.ui.utils.artistRoute
import com.convx.music.ui.utils.localPlaylistRoute
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.LibraryAlbumsViewModel
import com.convx.music.viewmodels.LibraryArtistsViewModel
import com.convx.music.viewmodels.LibraryPlaylistsViewModel
import com.convx.music.viewmodels.LibrarySongsViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import kotlinx.coroutines.launch

// Shared subpage header: the large title scrolls beneath the toolbar and fades into its
// compact title. The toolbar also provides a back button and optional trailing actions.

@Composable
internal fun AppleSubpage(
    title: String,
    onBack: () -> Unit,
    backLabel: String = stringResource(R.string.filter_library),
    state: LazyListState = rememberLazyListState(),
    trailing: @Composable RowScope.() -> Unit = {},
    overlay: @Composable BoxScope.() -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    BackHandler(onBack = onBack)
    val colors = IosTheme.colors
    val haze = remember { HazeState() }
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottom = LocalPlayerAwareWindowInsets.current.asPaddingValues().calculateBottomPadding()
    val sideInset = LocalPlayerAwareWindowInsets.current.asPaddingValues()
        .calculateStartPadding(androidx.compose.ui.platform.LocalLayoutDirection.current)
    val titleGutter = if (sideInset > 0.dp) 22.dp else 16.dp
    val collapse = rememberTitleCollapseProgressState(state, titleItemKey = LargeTitleKey)
    // Sample this page's list instead of the app root. The toolbar sits inside the root's
    // captured layer, so sampling the root would recurse through the toolbar and crash RenderNode.
    val pageColor = colors.systemBackground
    val pageBackdrop = com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop(
        onDraw = remember(pageColor) { { drawRect(pageColor); drawContent() } },
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.systemBackground),
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer()
                .layerBackdrop(pageBackdrop)
                .graphicsLayer()
                .hazeSource(haze),
            // The side inset is padding, not a narrower list, so rows scroll under the side bar.
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = sideInset,
                top = statusTop + com.convx.music.ui.component.TopBarContentHeight,
                bottom = bottom + 16.dp,
            ),
        ) {
            item(key = LargeTitleKey, contentType = "large_title") {
                Text(
                    text = title,
                    style = SfStyle(34.sp, 41.sp, FontWeight.Bold, (-0.6).sp),
                    color = colors.label,
                    maxLines = 1,
                    modifier = Modifier.padding(start = titleGutter, end = 16.dp, top = 10.dp, bottom = 10.dp),
                )
            }
            content()
        }

        // Only as deep as the toolbar row (+8dp of fade): the large title sits right
        // under it and must be sharp until it actually scrolls up.
        TopFadeBlur(
            hazeState = haze,
            pageColor = colors.systemBackground,
            modifier = Modifier.align(Alignment.TopCenter),
            heightOverride = statusTop + com.convx.music.ui.component.TopBarContentHeight + 8.dp,
        )

        androidx.compose.runtime.CompositionLocalProvider(com.convx.music.ui.ios.foundation.LocalIosBackdrop provides pageBackdrop) {
        com.convx.music.ui.component.FrostedTopBar(
            title = title,
            titleAlphaProvider = { collapse.value },
            titleColor = colors.label,
            leadingInset = sideInset + titleGutter,
            leading = {
                ToolbarCapsule(onClick = onBack, clearBackdrop = true) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp, end = 14.dp)) {
                        IosGlyph(sf("SFChevronLeft"), ToolbarGlyphColor(), 18.dp)
                        Spacer(Modifier.width(3.dp))
                        Text(backLabel, style = SfStyle(17.sp, 22.sp, FontWeight.Medium), color = ToolbarGlyphColor(), maxLines = 1)
                    }
                }
            },
            actions = { Row(verticalAlignment = Alignment.CenterVertically) { trailing() } },
        )
        }
        overlay()
    }
}

private const val LargeTitleKey = "apple_large_title"

@Composable
private fun ToolbarGlyphColor(): Color = if (IosTheme.colors.isDark) Color(0xFFE6E6E6) else Color(0xFF404040)

/** Glass capsule for toolbar controls, with refraction and press feedback. */
@Composable
private fun ToolbarCapsule(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    clearBackdrop: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = IosTheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(44.dp)
            .iosGlass(
                shape = CircleShape,
                spec = if (clearBackdrop) {
                    com.convx.music.ui.ios.foundation.IosGlassSpec.Control.copy(blurRadius = 0.dp)
                } else {
                    com.convx.music.ui.ios.foundation.IosGlassSpec.Control
                },
                interactive = true,
                wash = if (clearBackdrop) colors.systemBackground.copy(alpha = 0.62f) else Color.Transparent,
            )
            .then(if (onClick != null) Modifier.clickable(interactionSource = null, indication = null, onClick = onClick) else Modifier),
    ) { content() }
}

/** One 36dp glyph button inside a toolbar capsule, anchoring its own dropdown. */
@Composable
private fun ToolbarButton(icon: SfSymbol, menu: (@Composable (dismiss: () -> Unit) -> Unit)? = null, onClick: (() -> Unit)? = null) {
    var open by remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .then(
                if (menu != null) Modifier.menuTrigger { open = true }
                else Modifier.clickable(interactionSource = null, indication = null) { onClick?.invoke() },
            ),
    ) {
        IosGlyph(icon, ToolbarGlyphColor(), 20.dp)
        if (menu != null) {
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) { menu { open = false } }
        }
    }
}

@Composable
private fun ToolbarButtons(vararg buttons: @Composable () -> Unit) {
    ToolbarCapsule {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(horizontal = 4.dp)) {
            buttons.forEach { it() }
        }
    }
}

private val SortGlyph: SfSymbol by lazy { sf("line.3.horizontal.decrease") }

/** A dropdown row with Apple's leading checkmark column (picker menus). */
@Composable
private fun CheckRow(label: String, checked: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label) },
        leadingIcon = {
            Box(Modifier.size(19.dp), contentAlignment = Alignment.Center) {
                if (checked) IosCheckmark(color = LocalContentColor.current)
            }
        },
        onClick = onClick,
    )
}

// Rows

/** Library row with artwork, title, optional subtitle, and optional trailing content. */
@Composable
private fun ArtRow(
    art: Any?,
    artSize: Dp,
    rowHeight: Dp,
    title: String,
    subtitle: String?,
    circle: Boolean = false,
    radius: Dp = 5.dp,
    chevron: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    artworkId: String? = null,
) {
    val colors = IosTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .then(if (onClick != null) Modifier.iosRowPress(onClick = onClick) else Modifier)
            .padding(start = 16.dp),
    ) {
        val shape = if (circle) CircleShape else com.convx.music.ui.component.shapes.ContinuousRoundedRectangle(radius)
        AsyncImage(
            model = art,
            onSuccess = com.convx.music.ui.utils.sharedArtworkImageListener(
                com.convx.music.ui.utils.rememberArtworkSourceOwner(), art as? String,
            ),
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .sharedArtworkSource(artworkId, art as? String)
                .size(artSize)
                .clip(shape)
                .background(colors.secondarySystemFill),
        )
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).fillMaxHeight()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterStart).padding(end = 16.dp),
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = SfStyle(17.sp, 22.sp, tracking = (-0.43).sp), color = colors.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (subtitle != null) {
                        Text(subtitle, style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp), color = colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                trailing?.invoke()
                if (chevron) {
                    Spacer(Modifier.width(8.dp))
                    Chevron()
                }
            }
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

// Playlists

@Composable
fun ApplePlaylistsPage(navController: NavController, onBack: () -> Unit, viewModel: LibraryPlaylistsViewModel = hiltViewModel()) {
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val localOnly = com.convx.music.LocalLocalOnlyMode.current
    val playlists = allPlaylists.filter { !localOnly || it.playlist.browseId == null }
    val ytmSync by rememberPreference(com.convx.music.constants.YtmSyncKey, true)
    androidx.compose.runtime.LaunchedEffect(ytmSync, localOnly) { if (ytmSync && !localOnly) viewModel.sync() }
    var viewType by rememberEnumPreference(PlaylistViewTypeKey, LibraryViewType.LIST)
    var sortType by rememberEnumPreference(PlaylistSortTypeKey, PlaylistSortType.CREATE_DATE)
    var descending by rememberPreference(PlaylistSortDescendingKey, true)
    var newPlaylist by remember { mutableStateOf(false) }
    val colors = IosTheme.colors
    val liked = stringResource(R.string.liked_songs)

    AppleSubpage(
        title = stringResource(R.string.filter_playlists),
        onBack = onBack,
        trailing = {
            ToolbarButtons(
                { ToolbarButton(sf("SFPlus"), onClick = { newPlaylist = true }) },
                {
                    ToolbarButton(SortGlyph, menu = { dismiss ->
                        CheckRow(stringResource(R.string.apple_view_list), viewType == LibraryViewType.LIST) { viewType = LibraryViewType.LIST; dismiss() }
                        CheckRow(stringResource(R.string.apple_view_grid), viewType == LibraryViewType.GRID) { viewType = LibraryViewType.GRID; dismiss() }
                        IosMenuDivider()
                        CheckRow(stringResource(R.string.apple_sort_title), sortType == PlaylistSortType.NAME) { sortType = PlaylistSortType.NAME; descending = false; dismiss() }
                        CheckRow(stringResource(R.string.recently_added), sortType == PlaylistSortType.CREATE_DATE) { sortType = PlaylistSortType.CREATE_DATE; descending = true; dismiss() }
                        CheckRow(stringResource(R.string.apple_sort_recently_updated), sortType == PlaylistSortType.LAST_UPDATED) { sortType = PlaylistSortType.LAST_UPDATED; descending = true; dismiss() }
                        CheckRow(stringResource(R.string.apple_sort_song_count), sortType == PlaylistSortType.SONG_COUNT) { sortType = PlaylistSortType.SONG_COUNT; descending = true; dismiss() }
                    })
                },
            )
        },
    ) {
        if (playlists.isEmpty()) {
            item(key = "empty_playlists", contentType = "placeholder") {
                Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    IosGlyph(sf("SFMusicNoteList"), colors.secondaryLabel, 44.dp)
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.no_playlists), color = colors.label, style = SfStyle(22.sp, 28.sp, FontWeight.Bold))
                    com.convx.music.ui.ios.compat.TextButton(onClick = { newPlaylist = true }) {
                        Text(stringResource(R.string.create_playlist), color = IosTheme.tint)
                    }
                }
            }
        }
        if (viewType == LibraryViewType.LIST) {
            items(playlists, key = { it.id }, contentType = { "row" }) { playlist ->
                LibraryItemMenu(playlist, onOpen = { navController.navigate(localPlaylistRoute(playlist.id, playlist.thumbnails.firstOrNull())) }) {
                    ArtRow(
                        art = playlist.thumbnails.firstOrNull(),
                        artworkId = playlist.id,
                        artSize = 60.dp,
                        rowHeight = 76.dp,
                        title = playlist.title,
                        subtitle = pluralStringResource(R.plurals.n_song, playlist.songCount, playlist.songCount),
                    )
                }
            }
        } else {
            gridItems(playlists) { item ->
                LibraryItemMenu(item, onOpen = { navController.navigate(localPlaylistRoute(item.id, (item as com.convx.music.db.entities.Playlist).thumbnails.firstOrNull())) }) {
                    RecentTile(item)
                }
            }
        }
    }

    if (newPlaylist) CreatePlaylistDialog(onDismiss = { newPlaylist = false })
}

/** Places library items in two-column rows. */
private fun LazyListScope.gridItems(list: List<LocalItem>, tile: @Composable (LocalItem) -> Unit) {
    items(list.chunked(2), key = { "grid_" + it.first().id }, contentType = { "grid_row" }) { pair ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 10.dp),
        ) {
            pair.forEach { Box(Modifier.weight(1f)) { tile(it) } }
            if (pair.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

// Artists

@Composable
fun AppleArtistsPage(navController: NavController, onBack: () -> Unit, viewModel: AppleLibraryViewModel = hiltViewModel()) {
    val artists by viewModel.localArtists.collectAsStateWithLifecycle()
    val localOnly by viewModel.localOnly.collectAsStateWithLifecycle()
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    val needsAccess = localOnly && !permission.granted
    androidx.compose.runtime.LaunchedEffect(permission.granted) { if (permission.granted) viewModel.scanDevice() }
    var sortType by rememberEnumPreference(ArtistSortTypeKey, ArtistSortType.NAME)
    var descending by rememberPreference(ArtistSortDescendingKey, false)

    AppleSubpage(
        title = stringResource(R.string.filter_artists),
        onBack = onBack,
        trailing = {
            ToolbarCapsule {
                Box(Modifier.padding(horizontal = 4.dp)) {
                    ToolbarButton(SortGlyph, menu = { dismiss ->
                        CheckRow(stringResource(R.string.apple_sort_title), sortType == ArtistSortType.NAME) { sortType = ArtistSortType.NAME; descending = false; dismiss() }
                        CheckRow(stringResource(R.string.recently_added), sortType == ArtistSortType.CREATE_DATE) { sortType = ArtistSortType.CREATE_DATE; descending = true; dismiss() }
                        CheckRow(stringResource(R.string.apple_sort_song_count), sortType == ArtistSortType.SONG_COUNT) { sortType = ArtistSortType.SONG_COUNT; descending = true; dismiss() }
                    })
                }
            }
        },
    ) {
        if (needsAccess || artists.isEmpty()) {
            item(key = "artist_access", contentType = "placeholder") {
                if (needsAccess) LocalAccessCard(permission.blocked, permission.request)
                else com.convx.music.ui.component.LocalMusicEmptyState(onScan = viewModel::scanDevice)
            }
            return@AppleSubpage
        }
        items(artists, key = { it.id }, contentType = { "row" }) { artist: Artist ->
            ArtRow(
                art = artist.artist.thumbnailUrl,
                artSize = 48.dp,
                rowHeight = 56.dp,
                title = artist.artist.name,
                subtitle = null,
                circle = true,
                onClick = { navController.navigate(artistRoute(artist.id, artist.artist.thumbnailUrl)) },
            )
        }
    }
}

// Albums

@Composable
fun AppleAlbumsPage(navController: NavController, onBack: () -> Unit, viewModel: AppleLibraryViewModel = hiltViewModel()) {
    val albums by viewModel.localAlbums.collectAsStateWithLifecycle()
    val localOnly by viewModel.localOnly.collectAsStateWithLifecycle()
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    val needsAccess = localOnly && !permission.granted
    androidx.compose.runtime.LaunchedEffect(permission.granted) { if (permission.granted) viewModel.scanDevice() }
    var sortType by rememberEnumPreference(AlbumSortTypeKey, AlbumSortType.NAME)
    var descending by rememberPreference(AlbumSortDescendingKey, false)

    AppleSubpage(
        title = stringResource(R.string.filter_albums),
        onBack = onBack,
        trailing = {
            ToolbarCapsule {
                Box(Modifier.padding(horizontal = 4.dp)) {
                    ToolbarButton(SortGlyph, menu = { dismiss ->
                        CheckRow(stringResource(R.string.apple_sort_title), sortType == AlbumSortType.NAME) { sortType = AlbumSortType.NAME; descending = false; dismiss() }
                        CheckRow(stringResource(R.string.recently_added), sortType == AlbumSortType.CREATE_DATE) { sortType = AlbumSortType.CREATE_DATE; descending = true; dismiss() }
                        CheckRow(stringResource(R.string.artist), sortType == AlbumSortType.ARTIST) { sortType = AlbumSortType.ARTIST; descending = false; dismiss() }
                        CheckRow(stringResource(R.string.sort_by_year), sortType == AlbumSortType.YEAR) { sortType = AlbumSortType.YEAR; descending = true; dismiss() }
                    })
                }
            }
        },
    ) {
        if (needsAccess || albums.isEmpty()) {
            item(key = "album_access", contentType = "placeholder") {
                if (needsAccess) LocalAccessCard(permission.blocked, permission.request)
                else com.convx.music.ui.component.LocalMusicEmptyState(onScan = viewModel::scanDevice)
            }
            return@AppleSubpage
        }
        gridItems(albums) { item ->
            LibraryItemMenu(item, onOpen = { navController.navigate(albumRoute(item.id, item.thumbnailUrl)) }) {
                RecentTile(item)
            }
        }
    }
}

// Standard and local songs share the same list screen.

@Composable
fun AppleSongsPage(navController: NavController, onBack: () -> Unit, viewModel: AppleLibraryViewModel = hiltViewModel()) {
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { }
    val songs by viewModel.localSongs.collectAsStateWithLifecycle()
    val localOnly by viewModel.localOnly.collectAsStateWithLifecycle()
    androidx.compose.runtime.LaunchedEffect(permission.granted) { if (permission.granted) viewModel.scanDevice() }
    var sortType by rememberEnumPreference(SongSortTypeKey, SongSortType.CREATE_DATE)
    var descending by rememberPreference(SongSortDescendingKey, true)
    AppleSongList(
        title = stringResource(R.string.filter_songs),
        songs = songs,
        sortType = sortType,
        onSort = { type, desc -> sortType = type; descending = desc },
        navController = navController,
        onBack = onBack,
        placeholder = when {
            localOnly && !permission.granted -> ({ LocalAccessCard(permission.blocked, permission.request) })
            songs.isEmpty() -> ({ com.convx.music.ui.component.LocalMusicEmptyState(onScan = viewModel::scanDevice) })
            else -> null
        },
    )
}

/** Songs saved on this phone: the same list, behind a permission prompt when needed. */
@Composable
fun AppleLocalSongsPage(navController: NavController, onBack: () -> Unit, viewModel: AppleLocalSongsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val scanning by viewModel.scanning.collectAsStateWithLifecycle()
    var granted by remember { mutableStateOf(com.convx.music.ui.component.hasLocalAudioPermission(context)) }
    val permission = com.convx.music.ui.component.rememberLocalAudioPermission { ok ->
        granted = ok
        if (ok) viewModel.scan(context)
    }
    // Picks up new files every time the page opens with access.
    androidx.compose.runtime.LaunchedEffect(granted) { if (granted) viewModel.scan(context) }
    var sortType by remember { mutableStateOf(SongSortType.NAME) }

    AppleSongList(
        title = stringResource(R.string.filter_local),
        songs = songs,
        sortType = sortType,
        onSort = { type, desc -> sortType = type; viewModel.sort(type, desc) },
        navController = navController,
        onBack = onBack,
        header = if (granted) ({
            com.convx.music.ui.screens.library.LocalMusicFoldersEntry(count = null, navController = navController)
        }) else null,
        placeholder = when {
            !granted -> ({ LocalAccessCard(blocked = permission.blocked, onAllow = permission.request) })
            songs.isEmpty() && !scanning -> ({ com.convx.music.ui.component.LocalMusicEmptyState(onScan = { viewModel.scan(context) }) })
            else -> null
        },
    )
}

/**
 * Apple's "allow access" card: a tinted glyph in a soft circle, a title, why it is
 * asked for, and one capsule button — "Allow Access", or "Open Settings" once Android
 * has stopped showing the system dialog.
 */
@Composable
internal fun LocalAccessCard(blocked: Boolean, onAllow: () -> Unit) {
    val colors = IosTheme.colors
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(IosTheme.tint.copy(alpha = 0.14f)),
        ) { IosGlyph(sf("SFMusicNoteHouse"), IosTheme.tint, 36.dp) }
        Spacer(Modifier.height(18.dp))
        Text(
            stringResource(R.string.apple_local_access_title),
            style = SfStyle(22.sp, 28.sp, FontWeight.Bold, (-0.26).sp).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            color = colors.label,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (blocked) R.string.local_permission_blocked_body else R.string.apple_local_access_body),
            style = SfStyle(15.sp, 20.sp, tracking = (-0.23).sp).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
            color = colors.secondaryLabel,
        )
        Spacer(Modifier.height(22.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(IosTheme.tint)
                .iosRowPress(onClick = {
                    if (blocked) {
                        context.startActivity(
                            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.fromParts("package", context.packageName, null))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        )
                    } else {
                        onAllow()
                    }
                }),
        ) {
            Text(
                stringResource(if (blocked) R.string.open_settings else R.string.apple_local_allow),
                style = SfStyle(17.sp, 22.sp, FontWeight.SemiBold, (-0.43).sp),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun EmptyNote(text: String) {
    Text(
        text,
        style = SfStyle(15.sp, 20.sp).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
        color = IosTheme.colors.secondaryLabel,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 48.dp),
    )
}

/**
 * The shared Songs list: Play / Shuffle, then rows — under letter headers when sorted
 * by title — with the app's A–Z scrub rail on the trailing edge. [placeholder], when
 * set, stands in for the buttons and rows (no access yet, nothing found).
 */
@Composable
private fun AppleSongList(
    title: String,
    songs: List<Song>,
    sortType: SongSortType,
    onSort: (SongSortType, Boolean) -> Unit,
    navController: NavController,
    onBack: () -> Unit,
    placeholder: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
) {
    val playerConnection = LocalPlayerConnection.current
    val state = rememberLazyListState()

    // By title, Apple groups under letter headers.
    val sections = remember(songs, sortType) {
        if (sortType != SongSortType.NAME) null
        else songs.groupBy { sectionLetter(it.song.title) }.toList()
    }
    // Rail map: letter -> index among the rail's items (headers + rows), which are the
    // last items of the list, as ListScrollRail expects.
    val railIndex = remember(sections, songs) {
        if (sections == null) com.convx.music.ui.component.buildAlphabetSectionIndex(songs) { it.song.title }
        else {
            var i = 0
            sections.associate { (letter, list) -> letter to i.also { i += 1 + list.size } }
        }
    }
    val railCount = if (sections == null) songs.size else songs.size + sections.size

    fun play(start: Int, shuffle: Boolean = false) {
        val items = songs.map(Song::toMediaItem)
        if (items.isEmpty()) return
        playerConnection?.playQueue(
            if (shuffle) ListQueue(title = title, items = items.shuffled()) else ListQueue(title = title, items = items, startIndex = start),
        )
    }

    AppleSubpage(
        title = title,
        onBack = onBack,
        state = state,
        trailing = {
            ToolbarCapsule {
                Box(Modifier.padding(horizontal = 4.dp)) {
                    ToolbarButton(SortGlyph, menu = { dismiss ->
                        CheckRow(stringResource(R.string.apple_sort_title), sortType == SongSortType.NAME) { onSort(SongSortType.NAME, false); dismiss() }
                        CheckRow(stringResource(R.string.recently_added), sortType == SongSortType.CREATE_DATE) { onSort(SongSortType.CREATE_DATE, true); dismiss() }
                        CheckRow(stringResource(R.string.artist), sortType == SongSortType.ARTIST) { onSort(SongSortType.ARTIST, false); dismiss() }
                    })
                }
            }
        },
        overlay = {
            if (placeholder == null) {
                ListScrollRail(
                    lazyListState = state,
                    itemCount = railCount,
                    sectionIndexMap = railIndex,
                )
            }
        },
    ) {
        if (placeholder != null) {
            item(key = "placeholder", contentType = "placeholder") { placeholder() }
            return@AppleSubpage
        }
        if (header != null) item(key = "folders", contentType = "folders") { header() }
        // Keep play and shuffle actions together above the song list.
        item(key = "buttons", contentType = "buttons") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                PlayButton(sf("SFPlayFill"), stringResource(R.string.play), Modifier.weight(1f)) { play(0) }
                PlayButton(sf("SFShuffle"), stringResource(R.string.shuffle), Modifier.weight(1f)) { play(0, shuffle = true) }
            }
        }
        if (sections != null) {
            var offset = 0
            sections.forEach { (letter, list) ->
                val base = offset
                item(key = "h_$letter", contentType = "header") { LetterHeader(letter) }
                itemsIndexed(list, key = { _, s -> s.id }, contentType = { _, _ -> "song" }) { i, song ->
                    SongRow(song, navController) { play(base + i) }
                }
                offset += list.size
            }
        } else {
            itemsIndexed(songs, key = { _, s -> s.id }, contentType = { _, _ -> "song" }) { i, song ->
                SongRow(song, navController) { play(i) }
            }
        }
    }
}

private fun sectionLetter(title: String): String {
    val c = title.trim().firstOrNull()?.uppercaseChar() ?: return "#"
    return if (c in 'A'..'Z') c.toString() else "#"
}

@Composable
private fun PlayButton(icon: SfSymbol, label: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(50.dp)
            .clip(CircleShape)
            .background(Color(0xFF767680).copy(alpha = 0.12f))
            .iosRowPress(onClick = onClick),
    ) {
        IosGlyph(icon, IosTheme.tint, 17.dp)
        Spacer(Modifier.width(4.dp))
        Text(label, style = SfStyle(17.sp, 22.sp, tracking = (-0.43).sp), color = IosTheme.tint)
    }
}

@Composable
private fun LetterHeader(letter: String) {
    val colors = IosTheme.colors
    Column(Modifier.fillMaxWidth().padding(start = 16.dp, top = 6.dp)) {
        Text(letter, style = SfStyle(15.sp, 20.sp, FontWeight.SemiBold), color = colors.secondaryLabel)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(IosMetrics.SeparatorThickness).background(colors.separator))
    }
}

@Composable
private fun SongRow(song: Song, navController: NavController, onPlay: () -> Unit) {
    val entries = songMenu(song, navController)
    var dots by remember { mutableStateOf(false) }
    com.convx.music.ui.utils.ArtworkSourceHost(Modifier) { sourceModifier ->
    AppleContextMenu(
        sections = entries.sections,
        quickActions = entries.quick,
        preview = {
            Cover(song.song.thumbnailUrl, 12.dp)
            Text(song.song.title, style = SfStyle(15.sp, 20.sp, FontWeight.SemiBold, (-0.23).sp), color = IosTheme.colors.label, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 10.dp, start = 2.dp))
            Text(song.artists.joinToString { it.name }, style = SfStyle(13.sp, 18.sp), color = IosTheme.colors.secondaryLabel, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 2.dp, bottom = 2.dp))
        },
        onClick = onPlay,
        modifier = sourceModifier,
    ) {
        ArtRow(
            art = song.song.thumbnailUrl,
            artworkId = song.album?.id ?: song.song.albumId,
            artSize = 48.dp,
            rowHeight = 64.dp,
            title = song.song.title,
            subtitle = song.artists.joinToString { it.name },
            chevron = false,
            trailing = {
                Box {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .menuTrigger { dots = true },
                    ) { IosGlyph(sf("ellipsis"), IosTheme.colors.label, 20.dp) }
                    DropdownMenu(expanded = dots, onDismissRequest = { dots = false }) {
                        (entries.quick + entries.sections.flatMap { it.items }).forEachIndexed { i, item ->
                            if (i == entries.quick.size && i > 0) IosMenuDivider()
                            DropdownMenuItem(
                                text = { Text(item.title) },
                                leadingIcon = { item.icon?.invoke(LocalContentColor.current) },
                                onClick = { dots = false; item.onClick() },
                            )
                        }
                    }
                }
            },
        )
    }
    }
    if (entries.addToPlaylist.value) {
        AddToPlaylistDialog(isVisible = true, onGetSong = { listOf(song.id) }, onDismiss = { entries.addToPlaylist.value = false })
    }
}

internal class SongMenuEntries(
    val quick: List<IosMenuItem>,
    val sections: List<IosMenuSection>,
    val addToPlaylist: androidx.compose.runtime.MutableState<Boolean>,
)

/** Apple Music's song menu: Download · Favorite · Share, then queue, playlist and navigation. */
@Composable
internal fun songMenu(song: Song, navController: NavController): SongMenuEntries {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val playerConnection = LocalPlayerConnection.current
    val downloadUtil = LocalDownloadUtil.current.get()
    val addToPlaylist = remember { mutableStateOf(false) }
    fun glyph(v: SfSymbol): @Composable (Color) -> Unit = { tint -> IosGlyph(v, tint, 20.dp) }

    // A file on the device has no YouTube video to download or link to.
    val isLocal = song.song.isLocal
    val quick = listOfNotNull(
        if (isLocal) null else IosMenuItem(stringResource(R.string.action_download), icon = glyph(sf("SFArrowDownToLine"))) {
            downloadSongs(context, listOf(DownloadTarget(song.id, song.song.title)), downloadUtil.downloads.value)
        },
        IosMenuItem(
            stringResource(R.string.favorite),
            icon = glyph(if (song.song.liked) sf("SFStarFill") else sf("SFStar")),
        ) { database.query { update(song.song.toggleLike()) } },
        if (isLocal) null else IosMenuItem(stringResource(R.string.share), icon = glyph(sf("SFSquareAndArrowUp"))) {
            context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${song.id}"),
                    null,
                ),
            )
        },
    )
    val sections = listOf(
        IosMenuSection(
            listOf(
                IosMenuItem(stringResource(R.string.play_next), icon = glyph(sf("SFTextLineFirstAndArrowtriangleForward"))) {
                    playerConnection?.playNext(listOf(song.toMediaItem()))
                },
                IosMenuItem(stringResource(R.string.play_last), icon = glyph(sf("SFTextLineLastAndArrowtriangleForward"))) {
                    playerConnection?.addToQueue(listOf(song.toMediaItem()))
                },
            ),
        ),
        IosMenuSection(
            listOf(
                IosMenuItem(stringResource(R.string.add_to_playlist_ellipsis), icon = glyph(sf("SFTextBadgePlus"))) { addToPlaylist.value = true },
            ),
        ),
        IosMenuSection(
            listOfNotNull(
                song.song.albumId?.let { albumId ->
                    IosMenuItem(stringResource(R.string.apple_go_to_album), icon = glyph(sf("SFSquareStack"))) {
                        navController.navigate(albumRoute(albumId, song.song.thumbnailUrl))
                    }
                },
                song.artists.firstOrNull()?.let { artist ->
                    IosMenuItem(stringResource(R.string.apple_go_to_artist), icon = glyph(sf("microphone.dynamic.on.stand"))) {
                        navController.navigate(artistRoute(artist.id, artist.thumbnailUrl))
                    }
                },
            ),
        ),
    ).filter { it.items.isNotEmpty() }
    return SongMenuEntries(quick, sections, addToPlaylist)
}
