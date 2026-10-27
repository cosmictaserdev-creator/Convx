package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.DialogSize
import dev.nucleusframework.macoscompose.components.SmallDialog as NucleusDialog
import dev.nucleusframework.macoscompose.components.ColumnWidth
import dev.nucleusframework.macoscompose.components.IconButton as NucleusIconButton
import dev.nucleusframework.macoscompose.components.IconButtonStyle
import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.Scaffold as NucleusScaffold
import dev.nucleusframework.macoscompose.components.Sidebar as NucleusSidebar
import dev.nucleusframework.macoscompose.components.SidebarItem as NucleusSidebarItem
import dev.nucleusframework.macoscompose.components.Switch as NucleusSwitch
import dev.nucleusframework.macoscompose.components.TextField as NucleusTextField
import dev.nucleusframework.macoscompose.components.TitleBar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.BasicText as ComposeText
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import com.convx.music.lyrics.LyricsQuery
import com.convx.music.playback.presentation.LyricsStateHolder
import com.convx.music.playback.presentation.PlaybackLyrics
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.collect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.onFocusChanged
import coil3.compose.AsyncImage
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackPhase
import com.convx.music.playback.PlaybackSnapshot
import com.convx.music.playback.PlaybackTrackSnapshot
import com.convx.music.playback.PlaybackQueueController
import com.convx.music.playback.PlaybackQueueSnapshot
import com.convx.music.account.MusicAccountState
import com.convx.music.account.presentation.MusicAccountPresentationState
import com.convx.music.account.presentation.MusicAccountStateHolder
import com.convx.music.designsystem.ConvxDesignTokens
import zone.ien.hig.glass.LiquidGlassMaterials
import zone.ien.hig.glass.liquidGlass
import androidx.compose.animation.core.EaseOutCubic
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogSearchFilter
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.YOUTUBE_MUSIC_PROVIDER
import com.convx.music.catalog.presentation.CatalogLibraryActionState
import com.convx.music.catalog.presentation.CatalogLibraryActionStateHolder
import com.convx.music.catalog.presentation.CatalogPlaylistActionState
import com.convx.music.catalog.presentation.CatalogPlaylistActionStateHolder
import com.convx.music.catalog.presentation.CatalogScreenState
import com.convx.music.catalog.presentation.CatalogStateHolder
import com.convx.music.catalog.presentation.CatalogSidebarStateHolder
import com.convx.music.catalog.presentation.RemoteArtwork
import com.convx.music.catalog.presentation.CatalogSharedTransitionPage
import com.convx.music.catalog.presentation.sharedCatalogArtwork
import com.convx.music.catalog.presentation.sharedCatalogTitle
import com.convx.music.playback.presentation.PlaybackPlayerMorph
import com.convx.music.ui.ios.foundation.ProvideIosTheme
import com.convx.music.ui.ios.foundation.lightIosColors
import com.convx.music.ui.ios.foundation.darkIosColors
import com.convx.music.ui.ios.foundation.iosTypography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.convx.music.playback.presentation.PlaybackFullPlayer
import com.convx.music.playback.presentation.PlaybackEqualizerSheet
import com.convx.music.playback.presentation.PlaybackSharedElements
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import com.convx.music.playback.presentation.PlaybackSleepTimerSheet
import com.composables.sfsymbols.SfRenderingMode
import com.composables.sfsymbols.SfSymbol
import com.composables.sfsymbols.SfSymbolIcon
import com.composables.sfsymbols.SfWeight
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.compose.koinInject
import java.awt.EventQueue
import java.awt.Frame
import java.awt.MouseInfo
import java.awt.Window as AwtWindow
import kotlin.math.roundToInt

private val DesktopSidebarWidth = 260.dp
private val WindowsWorkspaceRailWidth = 190.dp
private val WindowsFeatureRailWidth = 80.dp
private val SidePanelShape = RoundedCornerShape(ConvxDesignTokens.Metrics.SidebarCorner)
private val SideRowShape = RoundedCornerShape(7.dp)
private val PlayerShape = RoundedCornerShape(percent = 50)

private data class DesktopNowPlaying(
    val trackId: String? = null,
    val isPlaying: Boolean = false,
)

internal data class DesktopCompactPlayback(
    val track: PlaybackTrackSnapshot? = null,
    val phase: PlaybackPhase = PlaybackPhase.IDLE,
    val playWhenReady: Boolean = false,
    val isPlaying: Boolean = false,
    val mediaItemCount: Int = 0,
    val repeatMode: PlaybackRepeatMode = PlaybackRepeatMode.OFF,
    val shuffleEnabled: Boolean = false,
    val canSkipPrevious: Boolean = false,
    val canSkipNext: Boolean = false,
    val errorMessage: String? = null,
)

private fun PlaybackSnapshot.toDesktopCompactPlayback() = DesktopCompactPlayback(
    track = track,
    phase = phase,
    playWhenReady = playWhenReady,
    isPlaying = isPlaying,
    mediaItemCount = mediaItemCount,
    repeatMode = repeatMode,
    shuffleEnabled = shuffleEnabled,
    canSkipPrevious = canSkipPrevious,
    canSkipNext = canSkipNext,
    errorMessage = errorMessage,
)

internal enum class DesktopDestination(val title: String, val icon: DesktopGlyphKind) {
    HOME("Home", DesktopGlyphKind.HOUSE),
    SEARCH("Search", DesktopGlyphKind.SEARCH),
    EXPLORE("New", DesktopGlyphKind.GRID),
    RADIO("Radio", DesktopGlyphKind.RADIO),
    SONGS("Songs", DesktopGlyphKind.MUSIC_NOTE),
    LIKED("Favourite Songs", DesktopGlyphKind.FAVORITED),
    PLAYLISTS("All Playlists", DesktopGlyphKind.PLAYLIST),
    CHARTS("Charts", DesktopGlyphKind.CHARTS),
    LIBRARY("Recently Added", DesktopGlyphKind.HISTORY),
    ALBUMS("Albums", DesktopGlyphKind.ALBUM),
    ARTISTS("Artists", DesktopGlyphKind.ARTIST),
    UPLOADS("Uploads", DesktopGlyphKind.MUSIC_NOTE),
    HISTORY("History", DesktopGlyphKind.HISTORY),
    LOCAL_MUSIC("Local Music", DesktopGlyphKind.FOLDER),
    DOWNLOADS("Downloads", DesktopGlyphKind.DOWNLOAD),
    SETTINGS("Settings", DesktopGlyphKind.SETTINGS),
}

private enum class DesktopSidePanel { NONE, QUEUE, LYRICS }

private data class DesktopPageSnapshot(
    val destination: DesktopDestination,
    val query: String,
    val catalog: CatalogScreenState,
    val library: CatalogLibraryActionState,
    val tracks: List<DesktopTrack>,
    val signedIn: Boolean,
)

@Composable
fun ConvxDesktopShell(
    windowsNativeSidebar: Boolean = false,
    micaBackdropActive: Boolean = false,
    windowsHostWindow: AwtWindow? = null,
    onCloseWindow: () -> Unit = {},
    onMinimizeWindow: () -> Unit = {},
    onZoomWindow: () -> Unit = {},
) {
    val holder = koinInject<PlaybackSettingsStateHolder>()
    val preferences by holder.state.collectAsState()
    val appearance = preferences.appearance
    val dark = if (appearance.followSystemTheme) isSystemInDarkTheme() else preferences.darkTheme
    val font = rememberDesktopFont(appearance)
    val base = if (dark) darkIosColors(Color(appearance.accentArgb)) else lightIosColors(Color(appearance.accentArgb))
    val background = (if (dark) appearance.darkBackgroundArgb else appearance.lightBackgroundArgb)?.let { Color(it) } ?: if (dark) Color(0xFF1F1F1F) else Color.White
    val foreground = (if (dark) appearance.darkTextArgb else appearance.lightTextArgb)?.let { Color(it) } ?: if (dark) Color(0xFFE6E6E6) else Color(0xFF242424)
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, density.fontScale * appearance.fontScale),
        LocalDesktopReducedMotion provides preferences.reducedMotion) {
        ProvideIosTheme(base.copy(systemBackground = background, label = foreground,
            secondarySystemBackground = if (dark) Color(0xFF292929) else Color(0xFFF2F2F7)), iosTypography(font)) {
            DesktopNucleusTheme(appearance.glassEnabled, preferences.reducedMotion, micaBackdropActive) {
                DesktopWorkspace(windowsNativeSidebar, micaBackdropActive, windowsHostWindow, onCloseWindow, onMinimizeWindow, onZoomWindow)
            }
        }
    }
}

@Composable
private fun DesktopWorkspace(
    windowsNativeSidebar: Boolean,
    micaBackdropActive: Boolean,
    windowsHostWindow: AwtWindow?,
    onCloseWindow: () -> Unit,
    onMinimizeWindow: () -> Unit,
    onZoomWindow: () -> Unit,
) {
    val palette = desktopPalette()
    val playbackController = koinInject<DesktopMusicController>()
    val settings = koinInject<PlaybackSettingsStateHolder>()
    val preferences by settings.state.collectAsState()
    val localLibrary: DesktopMediaLibrary = koinInject()
    val downloads: DesktopDownloadManager = koinInject()
    val diy: DesktopDiyStateHolder = koinInject()
    val localPages: DesktopLocalLibraryStateHolder = koinInject()
    val localPage by localPages.state.collectAsState()
    val nowPlaying by remember(playbackController) {
        playbackController.playbackSnapshot.map { DesktopNowPlaying(it.track?.id, it.isPlaying) }.distinctUntilChanged()
    }.collectAsState(DesktopNowPlaying())
    val catalog = koinInject<CatalogStateHolder>()
    val search = koinInject<com.convx.music.catalog.presentation.CatalogSearchStateHolder>()
    val searchState by search.state.collectAsState()
    val sidebarCatalog = koinInject<CatalogSidebarStateHolder>()
    val playlists by sidebarCatalog.playlists.collectAsState()
    val localPlaylists by sidebarCatalog.localPlaylists.collectAsState()
    val catalogState by catalog.state.collectAsState()
    val libraryActions = koinInject<CatalogLibraryActionStateHolder>()
    val libraryState by libraryActions.state.collectAsState()
    val playlistActions = koinInject<CatalogPlaylistActionStateHolder>()
    val playlistState by playlistActions.state.collectAsState()
    val accountHolder = koinInject<MusicAccountStateHolder>()
    val account by accountHolder.state.collectAsState()
    val signedIn = account.account is MusicAccountState.SignedIn
    var destination by remember { mutableStateOf(DesktopDestination.HOME) }
    val localScope = preferences.localMode || destination == DesktopDestination.LOCAL_MUSIC || destination == DesktopDestination.DOWNLOADS
    var history by remember { mutableStateOf(emptyList<DesktopDestination>()) }
    var forwardStack by remember { mutableStateOf(emptyList<DesktopDestination>()) }
    val query = searchState.query
    var pendingLocalPlaylist by remember { mutableStateOf<String?>(null) }
    var showSignIn by remember { mutableStateOf(false) }
    var sidePanel by remember { mutableStateOf(DesktopSidePanel.NONE) }
    val backdrop = rememberLayerBackdrop()
    val tracks = remember(catalogState.sections, libraryState.savedOverrides) {
        catalogState.sections.flatMap { it.entries.filterIsInstance<CatalogEntry.Track>() }.distinctBy { it.id }
            .mapIndexed { index, track -> track.copy(isInLibrary = libraryState.savedOverrides[track.id] ?: track.isInLibrary).toDesktopTrack(index) }
    }
    fun navigate(next: DesktopDestination) {
        if (next == destination) return
        search.dismissSuggestions()
        history = history + destination
        forwardStack = emptyList()
        catalog.clear(next.title)
        destination = next
    }
    fun back() {
        if (localScope && localPage.canGoBack) localPages.goBack()
        else if (catalogState.canGoBack && destination != DesktopDestination.SETTINGS) catalog.goBack()
        else if (history.isNotEmpty()) {
            val previous = history.last()
            history = history.dropLast(1)
            forwardStack = forwardStack + destination
            catalog.clear(previous.title)
            destination = previous
        }
    }
    fun forward() {
        if (forwardStack.isEmpty()) return
        val next = forwardStack.last()
        forwardStack = forwardStack.dropLast(1)
        search.dismissSuggestions()
        history = history + destination
        catalog.clear(next.title)
        destination = next
    }
    fun reload() {
        if (localScope && destination != DesktopDestination.SETTINGS) {
            catalog.clear(destination.title)
            localPages.show(destination, if (destination == DesktopDestination.SEARCH) query else "")
            return
        }
        when (destination) {
            DesktopDestination.HOME -> catalog.loadHome(includeRecentActivity = signedIn)
            DesktopDestination.SEARCH -> if (query.isBlank()) catalog.clear("Search") else catalog.search(query, immediately = true)
            DesktopDestination.EXPLORE -> catalog.loadExplore()
            DesktopDestination.RADIO -> catalog.loadRadio()
            DesktopDestination.CHARTS -> catalog.loadCharts()
            DesktopDestination.SONGS -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.LIKED_TRACKS)
            DesktopDestination.LIKED -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.LIKED_TRACKS)
            DesktopDestination.PLAYLISTS -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.PLAYLISTS)
            DesktopDestination.LIBRARY -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.RECENT_ACTIVITY)
            DesktopDestination.ALBUMS -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.LIKED_ALBUMS)
            DesktopDestination.ARTISTS -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.ARTISTS)
            DesktopDestination.UPLOADS -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.UPLOADED_TRACKS)
            DesktopDestination.HISTORY -> if (signedIn) catalog.loadLibrary(CatalogLibraryCollection.HISTORY)
            DesktopDestination.SETTINGS -> Unit
            DesktopDestination.LOCAL_MUSIC, DesktopDestination.DOWNLOADS -> Unit
        }
    }
    LaunchedEffect(destination, signedIn, preferences.localMode) {
        reload()
        pendingLocalPlaylist?.let { localPages.openPlaylist(it); pendingLocalPlaylist = null }
    }
    LaunchedEffect(preferences.localMode) { search.setOnline(!preferences.localMode) }
    LaunchedEffect(preferences.localMode) { if (preferences.localMode) downloads.pauseAll() }
    LaunchedEffect(playbackController, localLibrary) {
        var last: String? = null
        playbackController.playbackSnapshot.collect { snapshot ->
            val key = snapshot.track?.let { "${it.providerId}:${it.id}" }
            if (snapshot.isPlaying && key != null && key != last) {
                last = key
                snapshot.track?.let { localLibrary.recordPlayed(com.convx.music.playback.PlaybackRequestTrack(it.providerId, it.id, it.title, it.artists, snapshot.durationMs, it.artworkUrl)) }
            }
        }
    }
    LaunchedEffect(account.account, preferences.localMode) {
        sidebarCatalog.refresh(signedIn && !preferences.localMode)
        if (account.account is MusicAccountState.SignedIn || account.account is MusicAccountState.ChoosingChannel) showSignIn = false
    }
    SideEffect { playbackController.setCatalogTracks(tracks) }
    DisposableEffect(playbackController, catalog, accountHolder, libraryActions, playlistActions, settings) {
        onDispose {
            playbackController.close(); catalog.close(); sidebarCatalog.close(); accountHolder.close()
            libraryActions.close(); playlistActions.close(); settings.close()
            localPages.close(); downloads.close(); localLibrary.close()
            diy.close()
            search.close()
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val captionControls = dev.nucleusframework.macoscompose.components.LocalWindowControlTrailingInset.current
        val frameTop = if (captionControls != Dp.Unspecified && captionControls > 0.dp) 52.dp else 0.dp
        val outer = 0.dp
        val sidebarWidth = if (preferences.appearance.sidebarWidth == com.convx.music.playback.AppearanceSettings().sidebarWidth)
            if (maxWidth < 1100.dp) 200.dp else 246.dp
        else preferences.appearance.sidebarWidth.dp
        val panelWidth = minOf(300.dp, maxWidth * .34f)
        val panelMotion = androidx.compose.animation.core.CubicBezierEasing(.215f, .61f, .355f, 1f)
        val panelSpace by animateDpAsState(if (sidePanel == DesktopSidePanel.NONE) 0.dp else panelWidth + outer,
            tween(if (preferences.reducedMotion) 0 else 300, easing = panelMotion), label = "DesktopSidePanelSpace")
        val contentOrigin = sidebarWidth + outer * 2
        val playerGutter = if (maxWidth - contentOrigin - panelSpace < 600.dp) 16.dp else 40.dp
        val contentWidth = (maxWidth - contentOrigin - outer - panelSpace - playerGutter * 2).coerceAtLeast(0.dp)
        val playerWidth = minOf(668.dp, (contentWidth - 24.dp).coerceAtLeast(420.dp), contentWidth)
        val playerStart = contentOrigin + playerGutter + (contentWidth - playerWidth) / 2f
        val container = RoundedCornerShape(preferences.appearance.containerCornerRadius.coerceAtLeast(18f).dp)
        NucleusScaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = palette.background,
            sidebarWidth = ColumnWidth.Fixed(sidebarWidth),
            sidebar = {
                DesktopSidebar(
                    selected = destination, onDestinationSelected = ::navigate, tracks = tracks, playlists = playlists,
                    accountState = account, backdrop = backdrop, windowsNativeSidebar = windowsNativeSidebar,
                    onTrackSelected = playbackController::selectTrack, onEntrySelected = catalog::openEntry,
                    onSignInRequested = { showSignIn = true }, onSignOut = accountHolder::signOut,
                    micaBackdropActive = micaBackdropActive, darkTheme = palette.dark, onThemeChanged = settings::setDarkTheme,
                    window = windowsHostWindow,
                    localPlaylists = localPlaylists.filter { it.isLocal },
                    onLocalPlaylistSelected = { id ->
                        if (destination == DesktopDestination.LOCAL_MUSIC) localPages.openPlaylist(id)
                        else { pendingLocalPlaylist = id; navigate(DesktopDestination.LOCAL_MUSIC) }
            },
            localMode = preferences.localMode, onLocalModeChanged = settings::setLocalMode,
            modifier = Modifier.width(sidebarWidth).fillMaxHeight(),
        )
            },
            titleBar = null,
        ) { insets ->
            Box(Modifier.fillMaxSize().padding(insets).background(palette.background)) {
                val screen = DesktopPageSnapshot(destination, query, catalogState, libraryState, tracks, signedIn)
                AnimatedContent(
                    targetState = screen,
                    contentKey = { it.destination to it.catalog.currentEntry?.id },
                    transitionSpec = {
                        ((fadeIn(tween(if (preferences.reducedMotion) 0 else 180)) +
                            slideInVertically(tween(if (preferences.reducedMotion) 0 else 180)) { if (preferences.reducedMotion) 0 else 10 })
                            togetherWith fadeOut(tween(if (preferences.reducedMotion) 0 else 100))).using(null)
                    },
                    modifier = Modifier.fillMaxSize().layerBackdrop(backdrop),
                    label = "DesktopPageTransition",
                ) { page ->
                    DesktopPage(
                        destination = page.destination, searchQuery = page.query,
                        onSearchQueryChange = search::editQuery,
                        catalogState = page.catalog, libraryActionState = page.library, isSignedIn = page.signedIn,
                        tracks = page.tracks, playbackTrackId = nowPlaying.trackId, playbackIsPlaying = nowPlaying.isPlaying,
                        onTrackSelected = { id ->
                            val index = page.tracks.indexOfFirst { it.request.id == id }
                            if (index >= 0) playbackController.playQueue(page.tracks.map { it.request }, index)
                        },
                        onEntrySelected = catalog::openEntry, onBack = ::back, onLoadMore = catalog::loadMore, onSignInRequested = { showSignIn = true },
                        onSearchFilterSelected = { search.submit(); catalog.search(query, it, immediately = true) },
                        onHomeFilterSelected = catalog::selectHomeFilter, onRetry = ::reload,
                        onPlaySelection = { selection, shuffle ->
                            val requests = selection.mapIndexed { index, track -> track.toDesktopTrack(index).request }
                            if (!shuffle && selection.any { it.id.value == nowPlaying.trackId }) playbackController.togglePlayPause()
                            else if (requests.isNotEmpty()) {
                                playbackController.setShuffleEnabled(shuffle)
                                playbackController.playQueue(requests, if (shuffle) requests.indices.random() else 0)
                            }
                        },
                        onSetTrackSaved = libraryActions::setSaved,
                        onAddTrackToPlaylist = { if (signedIn) playlistActions.openPicker(it) else showSignIn = true },
                        contentInset = 64.dp,
                        modifier = Modifier.fillMaxSize(),
                        localPages = if (localScope) localPages else null,
                        search = search,
                        playlists = playlists,
                        onSearchSubmit = { value ->
                            val submitted = search.submit(value)
                            if (localScope) localPages.show(DesktopDestination.SEARCH, submitted)
                            else catalog.search(submitted, immediately = true)
                        },
                        onSuggestedEntry = { entry ->
                            if (entry is CatalogEntry.Track) playbackController.playTrack(entry.toDesktopTrack(0).request)
                            else catalog.openEntry(entry)
                        },
                    )
                }
                DesktopSearchToolbar(query,
                    onQueryChange = { value ->
                        if (destination != DesktopDestination.SEARCH) navigate(DesktopDestination.SEARCH)
                        search.editQuery(value)
                    },
                    onSubmit = {
                        if (destination != DesktopDestination.SEARCH) navigate(DesktopDestination.SEARCH)
                        val submitted = search.submit(query)
                        if (localScope) localPages.show(DesktopDestination.SEARCH, submitted)
                        else catalog.search(submitted, immediately = true)
                    },
                    canGoBack = (localScope && localPage.canGoBack) || catalogState.canGoBack || history.isNotEmpty(),
                    onBack = ::back, canGoForward = forwardStack.isNotEmpty(), onForward = ::forward,
                    onReload = ::reload, backdrop = backdrop,
                    onCloseWindow = onCloseWindow, onMinimizeWindow = onMinimizeWindow, onZoomWindow = onZoomWindow)
            }
        }
        DesktopPlaybackOverlay(
            playbackController, settings, account, { showSignIn = true }, accountHolder::signOut,
            playerStart, playerWidth, backdrop, Modifier.fillMaxSize(),
            sidePanel = sidePanel, onSidePanelChange = { sidePanel = it }, panelWidth = panelWidth, topInset = frameTop,
            panelSpace = panelSpace, window = windowsHostWindow, onCloseWindow = onCloseWindow,
            micaBackdropActive = micaBackdropActive,
        )
        if (windowsNativeSidebar && windowsHostWindow != null) {
            DesktopWindowResizeEdges(windowsHostWindow)
        }
        if (playlistState.isPickerVisible) DesktopPlaylistPickerDialog(playlistState, playlistActions::dismissPicker,
            playlistActions::addToPlaylist, playlistActions::createPlaylistAndAdd, playlistActions::loadMorePlaylists, playlistActions::retryPlaylists)
        if (showSignIn) DesktopSignInDialog(account.isWorking, account.message,
            { cookie, visitor, sync -> accountHolder.completeWebSignIn(cookie, visitor, sync) }, accountHolder::clearMessage,
            { accountHolder.cancelSignIn(); showSignIn = false })
        val channels = (account.account as? MusicAccountState.ChoosingChannel)?.channels.orEmpty()
        if (channels.isNotEmpty()) DesktopChannelDialog(channels, accountHolder::selectChannel, accountHolder::cancelSignIn)
    }
}

@Composable
private fun DesktopPlaybackOverlay(
    playbackController: DesktopMusicController,
    settingsStateHolder: PlaybackSettingsStateHolder,
    account: MusicAccountPresentationState,
    onSignInRequested: () -> Unit,
    onSignOut: () -> Unit,
    playerStart: Dp,
    playerWidth: Dp,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    sidePanel: DesktopSidePanel,
    onSidePanelChange: (DesktopSidePanel) -> Unit,
    panelWidth: Dp,
    topInset: Dp,
    panelSpace: Dp,
    window: AwtWindow?,
    onCloseWindow: () -> Unit,
    micaBackdropActive: Boolean,
) {
    val palette = desktopPalette()
    val lyrics: LyricsStateHolder = koinInject()
    val settings by settingsStateHolder.state.collectAsState()
    val lyricPosition = remember { mutableLongStateOf(0L) }
    LaunchedEffect(playbackController, lyrics, settings.localMode, settings.lyricsProviderId) {
        var lastQuery: LyricsQuery? = null
        playbackController.playbackSnapshot.collectLatest { snapshot ->
            val track = snapshot.track
            val nextQuery = track?.let { LyricsQuery(it.id, it.title, it.artists.joinToString(), snapshot.durationMs ?: 0L) }
            if (nextQuery != lastQuery) {
                lastQuery = nextQuery
                lyrics.load(nextQuery, if (settings.localMode) DesktopLocalLyricsSource.ID else settings.lyricsProviderId)
            }
            lyricPosition.longValue = snapshot.positionMs
            if (snapshot.isPlaying) {
                var startFrame = 0L
                while (true) withFrameNanos { frame ->
                    if (startFrame == 0L) startFrame = frame
                    lyricPosition.longValue = (snapshot.positionMs + (frame - startFrame) / 1_000_000L)
                        .coerceAtMost(snapshot.durationMs ?: Long.MAX_VALUE)
                }
            }
        }
    }
    DisposableEffect(lyrics) { onDispose(lyrics::close) }
    val compactPlayback by remember(playbackController) {
        playbackController.playbackSnapshot
            .map(PlaybackSnapshot::toDesktopCompactPlayback)
            .distinctUntilChanged()
    }.collectAsState(DesktopCompactPlayback())
    val likes: DesktopTrackLikeStateHolder = koinInject()
    val likeState by likes.state.collectAsState()
    val media: DesktopMediaLibrary = koinInject()
    val mediaState by media.state.collectAsState()
    val currentTrack = compactPlayback.track
    val localTrack = mediaState.index.tracks.firstOrNull {
        val request = it.request()
        request.id == currentTrack?.id && request.providerId == currentTrack.providerId
    }
    val localLike = currentTrack?.providerId == LOCAL_MUSIC_PROVIDER || settings.localMode
    val liked = if (localLike) localTrack?.favorite == true else currentTrack?.id in likeState.likedIds
    val likeEnabled = currentTrack != null && if (localLike) localTrack != null
        else !likeState.loading && currentTrack.id !in likeState.pendingIds
    val toggleLike: () -> Unit = {
        if (localLike) localTrack?.let { media.setFavorite(it.id, !it.favorite) }
        else if (account.account !is MusicAccountState.SignedIn) onSignInRequested()
        else currentTrack?.let { likes.setLiked(it.id, !liked) }
    }
    LaunchedEffect(account.account, settings.localMode) {
        likes.setAccount(account.account.takeIf { !settings.localMode })
    }
    DisposableEffect(likes) { onDispose(likes::close) }
    val settingsError by settingsStateHolder.errorMessage.collectAsState()
    val sleepTimer = remember(playbackController) { requireNotNull(playbackController.sleepTimer) }
    val sleepTimerState by sleepTimer.state.collectAsState()
    val queue by playbackController.queueSnapshot.collectAsState()
    val showQueue = sidePanel == DesktopSidePanel.QUEUE
    val showLyrics = sidePanel == DesktopSidePanel.LYRICS
    var showSleepTimer by remember { mutableStateOf(false) }
    var showFullPlayer by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }

    Box(modifier) {
        if (compactPlayback.track == null) {
            DesktopMiniPlayer(compactPlayback, playbackController, backdrop, false, {}, {}, PlaybackSharedElements(),
                {}, {}, false, {}, settings.appearance, settings.reducedMotion, false, false, {},
                modifier = Modifier.align(Alignment.BottomStart).padding(start = playerStart, bottom = 18.dp).width(playerWidth).height(56.dp))
        }
        if (compactPlayback.track != null) {
            PlaybackPlayerMorph(
                trackId = compactPlayback.track?.id,
                expanded = showFullPlayer,
                onExpand = { showFullPlayer = true },
                onCollapse = { showFullPlayer = false },
                modifier = Modifier.fillMaxSize().padding(top = topInset),
                animationsEnabled = !settings.reducedMotion,
                dragGesturesEnabled = false,
                compactContent = { elements, expand ->
                    DesktopMiniPlayer(
                        playback = compactPlayback, controller = playbackController, backdrop = backdrop,
                        crossfadeEnabled = settings.transitionMode != com.convx.music.playback.PlaybackTransitionMode.OFF,
                        onToggleCrossfade = { settingsStateHolder.setCrossfadeEnabled(!settings.crossfadeEnabled) },
                        onExpand = expand, sharedElements = elements,
                        onQueueClick = { onSidePanelChange(if (showQueue) DesktopSidePanel.NONE else DesktopSidePanel.QUEUE) },
                        onLyricsClick = { onSidePanelChange(if (showLyrics) DesktopSidePanel.NONE else DesktopSidePanel.LYRICS) },
                        sleepTimerActive = sleepTimerState.isActive, onSleepTimerClick = { showSleepTimer = true },
                        appearance = settings.appearance,
                        reducedMotion = settings.reducedMotion,
                        liked = liked,
                        likeEnabled = likeEnabled,
                        likeMessage = likeState.message,
                        onToggleLike = toggleLike,
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = playerStart, bottom = 18.dp).width(playerWidth).height(56.dp),
                    )
                },
                expandedContent = { elements, collapse ->
                    DesktopFullPlayerOverlay(
                        controller = playbackController, sleepTimerActive = sleepTimerState.isActive,
                        crossfadeEnabled = settings.transitionMode == com.convx.music.playback.PlaybackTransitionMode.CROSSFADE,
                        autoMixEnabled = settings.transitionMode == com.convx.music.playback.PlaybackTransitionMode.AUTO_MIX,
                        crossfadeAvailable = true, crossfadeLocked = settings.bitPerfectEnabled,
                        equalizerEnabled = settings.equalizerEnabled, equalizerLocked = settings.bitPerfectEnabled,
                        playbackSettingsError = settingsError, sharedElements = elements,
                        onToggleCrossfade = { settingsStateHolder.setCrossfadeEnabled(!settings.crossfadeEnabled) },
                        onToggleAutoMix = { settingsStateHolder.setAutoMixEnabled(!settings.autoMixEnabled) },
                        onOpenEqualizer = { showEqualizer = true }, onCollapse = collapse,
                        onOpenQueue = { onSidePanelChange(if (showQueue) DesktopSidePanel.NONE else DesktopSidePanel.QUEUE) },
                        onOpenLyrics = { onSidePanelChange(if (showLyrics) DesktopSidePanel.NONE else DesktopSidePanel.LYRICS) },
                        onOpenSleepTimer = { showSleepTimer = true },
                        liked = liked, likeEnabled = likeEnabled, onToggleLike = toggleLike,
                        modifier = Modifier.fillMaxSize().padding(end = panelSpace, top = if (window != null) 44.dp else 0.dp),
                        headerModifier = Modifier.desktopWindowDrag(window),
                    )
                },
            )
        }

        if (showFullPlayer && window != null) {
            Row(Modifier.align(Alignment.TopStart).fillMaxWidth().height(44.dp)
                .background(palette.background).desktopWindowDrag(window).padding(start = 20.dp, end = 154.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Convx", color = palette.muted, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                DesktopIconButton(DesktopGlyphKind.LYRICS, "Show lyrics", selected = showLyrics, onClick = { onSidePanelChange(if (showLyrics) DesktopSidePanel.NONE else DesktopSidePanel.LYRICS) })
            }
        }
        AnimatedVisibility(
            visible = showQueue,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = topInset),
            enter = if (settings.reducedMotion) fadeIn(tween(0)) else slideInHorizontally(tween(300, easing = androidx.compose.animation.core.CubicBezierEasing(.215f, .61f, .355f, 1f))) { it } + fadeIn(tween(180)),
            exit = if (settings.reducedMotion) fadeOut(tween(0)) else slideOutHorizontally(tween(300, easing = androidx.compose.animation.core.CubicBezierEasing(.215f, .61f, .355f, 1f))) { it } + fadeOut(tween(160)),
        ) {
            DesktopQueuePanel(
                queue = queue,
                controller = playbackController,
                onDismiss = { onSidePanelChange(DesktopSidePanel.NONE) },
                backdrop = backdrop,
                micaBackdropActive = micaBackdropActive,
                modifier = Modifier.width(panelWidth).fillMaxHeight(),
            )
        }
        AnimatedVisibility(
            visible = showLyrics,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = topInset),
            enter = if (settings.reducedMotion) fadeIn(tween(0)) else slideInHorizontally(tween(300, easing = androidx.compose.animation.core.CubicBezierEasing(.215f, .61f, .355f, 1f))) { it } + fadeIn(tween(180)),
            exit = if (settings.reducedMotion) fadeOut(tween(0)) else slideOutHorizontally(tween(300, easing = androidx.compose.animation.core.CubicBezierEasing(.215f, .61f, .355f, 1f))) { it } + fadeOut(tween(160)),
        ) {
            Column(
                Modifier.width(panelWidth).fillMaxHeight()
                    .desktopGlass(backdrop, RoundedCornerShape(0.dp), LiquidGlassMaterials.MiniPlayer.copy(tint = palette.surface.copy(alpha = .6f),
                        blurRadius = 16.dp, saturation = 2.2f, refractionAmount = 0.dp))
                    .drawBehind { drawLine(palette.separator, Offset.Zero, Offset(0f, size.height), 1.dp.toPx()) }.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PlaybackLyrics(lyrics, { com.convx.music.playback.presentation.lyricDisplayPosition(lyricPosition.longValue, settings.lyricsOffsetMs) },
                    playbackController::seekTo, Modifier.weight(1f), fontSize = settings.lyricsFontSize,
                    animationsEnabled = !settings.reducedMotion, textColor = palette.foreground,
                    onProviderSelected = settingsStateHolder::setLyricsProvider, providersEnabled = !settings.localMode,
                    inactiveBlur = 1.3f, offsetMs = settings.lyricsOffsetMs, headerAction = { DesktopIconButton(DesktopGlyphKind.REMOVE, "Close lyrics", controlSize = 26.dp,
                        onClick = { onSidePanelChange(DesktopSidePanel.NONE) }) })
            }
        }

        if (showSleepTimer) {
            DesktopSleepTimerOverlay(
                controller = playbackController,
                timer = sleepTimer,
                onDismissRequest = { showSleepTimer = false },
            )
        }

        if (showEqualizer) {
            DesktopEqualizerDialog(settings, settingsStateHolder, settingsError) { showEqualizer = false }
        }


    }
}

@Composable
private fun DesktopFullPlayerOverlay(
    controller: DesktopMusicController,
    sleepTimerActive: Boolean,
    crossfadeEnabled: Boolean,
    autoMixEnabled: Boolean,
    crossfadeAvailable: Boolean,
    crossfadeLocked: Boolean,
    equalizerEnabled: Boolean,
    equalizerLocked: Boolean,
    playbackSettingsError: String?,
    sharedElements: PlaybackSharedElements,
    onToggleCrossfade: () -> Unit,
    onToggleAutoMix: () -> Unit,
    onOpenEqualizer: () -> Unit,
    onCollapse: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenLyrics: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    liked: Boolean,
    likeEnabled: Boolean,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier,
    headerModifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    val playback by controller.playbackSnapshot.collectAsState()
    DesktopExpandedPlayer(
        controller = controller, shared = sharedElements,
        crossfade = crossfadeEnabled, autoMix = autoMixEnabled, sleepTimer = sleepTimerActive,
        onCrossfade = onToggleCrossfade, onAutoMix = onToggleAutoMix,
        onEqualizer = onOpenEqualizer, onSleepTimer = onOpenSleepTimer,
        onQueue = onOpenQueue, onCollapse = onCollapse,
        onLyrics = onOpenLyrics,
        liked = liked, likeEnabled = likeEnabled, onToggleLike = onToggleLike,
        headerModifier = headerModifier, modifier = modifier,
    )
}

@Composable
private fun DesktopSleepTimerOverlay(
    controller: DesktopMusicController,
    timer: com.convx.music.playback.PlaybackSleepTimer,
    onDismissRequest: () -> Unit,
) {
    DesktopSleepTimerDialog(timer, onDismissRequest)
}

@Composable
private fun DesktopChannelDialog(
    channels: List<com.convx.music.account.MusicAccount>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    NucleusDialog(visible = true, onDismissRequest = onDismiss, title = "Choose a YouTube channel", confirmText = "Cancel", onConfirm = onDismiss, cancelText = null) {
        channels.forEach { channel ->
            PushButton(channel.name, { onSelect(channel.dataSyncId) }, modifier = Modifier.fillMaxWidth(), style = PushButtonStyle.Neutral)
        }
    }
}

@Composable
private fun DesktopPlaylistPickerDialog(
    state: CatalogPlaylistActionState,
    onDismiss: () -> Unit,
    onSelectPlaylist: (CatalogEntry.Playlist) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
) {
    var name by remember(state.selectedTrack?.id) { mutableStateOf("") }
    NucleusDialog(visible = true, onDismissRequest = onDismiss, title = "Add to Playlist", confirmText = "Done", onConfirm = onDismiss,
        cancelText = null, size = DialogSize.Large) {
        state.selectedTrack?.let { Text(it.title, color = DesktopMuted, fontSize = 14.sp, maxLines = 1) }
        NucleusTextField(name, { name = it }, singleLine = true, enabled = !state.isCreatingPlaylist, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("New playlist name", color = DesktopMuted, fontSize = 13.sp) })
        PushButton(if (state.isCreatingPlaylist) "Creating?" else "Create and add", { onCreatePlaylist(name.trim()) },
            enabled = name.isNotBlank() && !state.isCreatingPlaylist && state.pendingPlaylistIds.isEmpty())
        if (state.isLoadingPlaylists) Text("Loading playlists?", color = DesktopMuted, fontSize = 13.sp)
        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(state.playlists, key = { _, playlist -> playlist.id.value }) { _, playlist ->
                PushButton(playlist.title, { onSelectPlaylist(playlist) }, modifier = Modifier.fillMaxWidth(),
                    enabled = state.pendingPlaylistIds.isEmpty() && !state.isCreatingPlaylist, style = PushButtonStyle.Neutral)
            }
        }
        state.playlistError?.let { Text(it, color = DesktopMuted, fontSize = 13.sp); PushButton("Try again", onRetry, style = PushButtonStyle.Borderless) }
        if (state.continuationToken != null) PushButton(if (state.isLoadingMorePlaylists) "Loading?" else "More playlists", onLoadMore,
            enabled = !state.isLoadingMorePlaylists, style = PushButtonStyle.Borderless)
        state.message?.let { Text(it, color = DesktopMuted, fontSize = 13.sp) }
    }
}

@Composable
private fun DesktopBackdrop() {
    val palette = desktopPalette()
    Box(Modifier.fillMaxSize().background(palette.background))
}

private fun Modifier.desktopGlass(
    backdrop: LayerBackdrop,
    shape: RoundedCornerShape,
    material: zone.ien.hig.glass.LiquidGlassMaterial,
): Modifier = clip(shape).liquidGlass(backdrop, shape, material)

@Composable
private fun DesktopSidebar(
    selected: DesktopDestination,
    onDestinationSelected: (DesktopDestination) -> Unit,
    tracks: List<DesktopTrack>,
    playlists: List<CatalogEntry.Playlist>,
    accountState: MusicAccountPresentationState,
    backdrop: LayerBackdrop,
    windowsNativeSidebar: Boolean,
    onTrackSelected: (String) -> Unit,
    onEntrySelected: (CatalogEntry) -> Unit,
    onSignInRequested: () -> Unit,
    onSignOut: () -> Unit,
    micaBackdropActive: Boolean,
    darkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    window: AwtWindow?,
    localPlaylists: List<com.convx.music.library.PlaylistSummary>,
    onLocalPlaylistSelected: (String) -> Unit,
    localMode: Boolean,
    onLocalModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    val navigation = if (localMode) listOf(DesktopDestination.SEARCH, DesktopDestination.HOME)
        else listOf(DesktopDestination.SEARCH, DesktopDestination.HOME, DesktopDestination.EXPLORE, DesktopDestination.RADIO)
    val library = listOf(DesktopDestination.LIBRARY, DesktopDestination.ARTISTS, DesktopDestination.ALBUMS, DesktopDestination.SONGS)
    val sidebarMaterial = dev.nucleusframework.macoscompose.components.LocalSidebarGlassRegionFactory.current
    val sidebarBackdrop = rememberLayerBackdrop()
    val sidebarHazeState = rememberHazeState()
    Column(modifier.then(sidebarMaterial?.invoke(0.dp) ?: Modifier).background(if (micaBackdropActive) Color.Transparent
        else if (palette.dark) Color(0xFF27262B).copy(alpha = .84f) else Color(0xFFEDE9E6).copy(alpha = .78f))
        .drawBehind { drawLine(palette.separator.copy(alpha = .4f), Offset(size.width, 0f), Offset(size.width, size.height), .5.dp.toPx()) }) {
        Row(Modifier.fillMaxWidth().height(66.dp).desktopWindowDrag(window).padding(start = 16.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            androidx.compose.foundation.text.BasicText("Convx", style = androidx.compose.ui.text.TextStyle(
                fontFamily = com.convx.music.designsystem.convxWordmarkFontFamily(), color = palette.foreground,
                fontSize = 23.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-1).sp))
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.fillMaxSize().layerBackdrop(sidebarBackdrop).hazeSource(sidebarHazeState).verticalScroll(rememberScrollState()).padding(start = 10.dp, end = 10.dp, bottom = 144.dp)) {
                navigation.forEach { item -> SidebarItem(item, selected == item) { onDestinationSelected(item) } }
                SidebarSectionTitle("Library")
                library.forEach { item -> SidebarItem(item, selected == item) { onDestinationSelected(item) } }
                SidebarSectionTitle("Playlists")
                listOf(DesktopDestination.PLAYLISTS, DesktopDestination.LIKED).forEach { item ->
                    SidebarItem(item, selected == item) { onDestinationSelected(item) }
                }
                if (!localMode) playlists.forEach { playlist ->
                    Row(Modifier.fillMaxWidth().height(34.dp).clip(SideRowShape)
                        .desktopInteractive(hoverColor = palette.foreground.copy(alpha = .06f)) { onEntrySelected(playlist) }
                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        RemoteArtwork(playlist.artworkUrls.firstOrNull(), Modifier.size(23.dp).clip(RoundedCornerShape(4.dp)).background(palette.separator))
                        Text(playlist.title, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                localPlaylists.forEach { playlist ->
                    Row(Modifier.fillMaxWidth().height(34.dp).clip(SideRowShape)
                        .desktopInteractive(hoverColor = palette.foreground.copy(alpha = .06f)) { onLocalPlaylistSelected(playlist.id) }
                        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        LocalCover(playlist.artworkUrls.firstOrNull(), Modifier.size(23.dp))
                        Text(playlist.name, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Spacer(Modifier.height(16.dp))
                (listOf(DesktopDestination.HISTORY) + if (localMode) emptyList() else listOf(DesktopDestination.UPLOADS, DesktopDestination.CHARTS)).forEach { item ->
                    SidebarItem(item, selected == item) { onDestinationSelected(item) }
                }
            }
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                // Progressive blur under the glass edge: sharp where items scroll
                // out, ramping to the full glass further down.
                val hazeStyle = HazeMaterials.ultraThin().then {
                    backgroundColor(palette.surface)
                    noiseFactor(0f)
                    progressive(HazeProgressive.verticalGradient(
                        startIntensity = 0f, endIntensity = 1f, easing = EaseOutCubic))
                }
                Box(Modifier.matchParentSize().hazeBlur(input = HazeInput.Sources(sidebarHazeState), style = hazeStyle))
                Box(Modifier.matchParentSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(Brush.verticalGradient(0f to Color.Transparent, .25f to Color.White, 1f to Color.White), blendMode = BlendMode.DstIn)
                    }
                    .desktopGlass(sidebarBackdrop, RoundedCornerShape(0.dp), LiquidGlassMaterials.MiniPlayer.copy(
                        tint = palette.surface.copy(alpha = .78f), blurRadius = 24.dp, saturation = 1.5f,
                        refractionAmount = 0.dp, chromaticAberration = false)))
                Column(Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)) {
                        listOf(DesktopDestination.LOCAL_MUSIC, DesktopDestination.DOWNLOADS).forEach { item -> SidebarItem(item, selected == item) { onDestinationSelected(item) } }
                    }
                    Box(Modifier.fillMaxWidth().height(.5.dp).background(palette.separator))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f)) {
                            DesktopAccountButton((accountState.account as? MusicAccountState.SignedIn)?.account,
                                onClick = if (accountState.account !is MusicAccountState.SignedIn) onSignInRequested
                                    else ({ onDestinationSelected(DesktopDestination.SETTINGS) }))
                        }
                        DesktopIconButton(DesktopGlyphKind.SETTINGS, "Settings", controlSize = 30.dp, modifier = Modifier.padding(end = 12.dp),
                            onClick = { onDestinationSelected(DesktopDestination.SETTINGS) })
                    }
                }
            }
        }
    }
}

@Composable
internal fun DesktopAccountButton(account: com.convx.music.account.MusicAccount?, onClick: () -> Unit) {
    val palette = desktopPalette()
    val thumbnail = account?.thumbnailUrl?.takeIf { it.isNotBlank() }
    val imageModel = remember(thumbnail) {
        if (thumbnail?.startsWith("file:") == true) runCatching { java.io.File(java.net.URI(thumbnail)) }.getOrNull() ?: thumbnail
        else thumbnail
    }
    var imageFailed by remember(thumbnail) { mutableStateOf(false) }
    PushButton(onClick = onClick, style = PushButtonStyle.Borderless,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp).height(42.dp).semantics {
            contentDescription = account?.let { "Open profile for ${it.name}" } ?: "Sign in"
        }) {
        Box(Modifier.size(27.dp).clip(CircleShape).background(palette.foreground.copy(alpha = .10f)), contentAlignment = Alignment.Center) {
            if (thumbnail != null && !imageFailed) AsyncImage(
                model = imageModel, contentDescription = "Profile photo", modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop, onError = { imageFailed = true },
            ) else if (account != null) Text(account.name.trim().take(1).uppercase(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            else DesktopGlyph(DesktopGlyphKind.PROFILE, palette.muted, 20.dp)
        }
        Text(account?.name ?: "Sign in", fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(start = 9.dp))
    }
}

@Composable
private fun SidebarSectionTitle(text: String) {
    val palette = desktopPalette()
    Text(
        text,
        color = palette.foreground.copy(alpha = 0.58f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 7.dp, top = 14.dp, bottom = 5.dp),
    )
}

@Composable
private fun SidebarItem(
    destination: DesktopDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val palette = desktopPalette()
    val reduced = LocalDesktopReducedMotion.current
    val fill by animateColorAsState(if (selected) palette.accent.copy(alpha = .14f) else Color.Transparent,
        tween(if (reduced) 0 else 150), label = "Sidebar selection")
    Row(Modifier.fillMaxWidth().height(31.dp).clip(SideRowShape).background(fill)
        .desktopInteractive(scale = .99f, hoverColor = palette.foreground.copy(alpha = .03f), onClick = onClick)
        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        DesktopGlyph(destination.icon, palette.accent, 18.dp, selected = selected)
        Text(destination.title, color = palette.foreground, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
private fun DesktopPage(
    destination: DesktopDestination,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    catalogState: CatalogScreenState,
    libraryActionState: CatalogLibraryActionState,
    isSignedIn: Boolean,
    tracks: List<DesktopTrack>,
    playbackTrackId: String?,
    playbackIsPlaying: Boolean,
    onTrackSelected: (String) -> Unit,
    onEntrySelected: (CatalogEntry) -> Unit,
    onBack: () -> Unit,
    onLoadMore: () -> Unit,
    onSignInRequested: () -> Unit,
    onSetTrackSaved: (CatalogId, Boolean, Boolean) -> Unit,
    onAddTrackToPlaylist: (CatalogEntry.Track) -> Unit,
    onSearchFilterSelected: (CatalogSearchFilter) -> Unit,
    onHomeFilterSelected: (CatalogEntry.Browse?) -> Unit,
    onRetry: () -> Unit,
    onPlaySelection: (List<CatalogEntry.Track>, Boolean) -> Unit,
    search: com.convx.music.catalog.presentation.CatalogSearchStateHolder,
    playlists: List<CatalogEntry.Playlist>,
    onSearchSubmit: (String) -> Unit,
    onSuggestedEntry: (CatalogEntry) -> Unit,
    contentInset: Dp,
    modifier: Modifier = Modifier,
    localPages: DesktopLocalLibraryStateHolder? = null,
) {
    if (destination == DesktopDestination.SETTINGS) {
        DesktopSettingsPage(koinInject(), koinInject(), koinInject(), onSignInRequested, modifier.padding(top = contentInset))
        return
    }
    val searchState by search.state.collectAsState()
    val searchRoot = destination == DesktopDestination.SEARCH && catalogState.currentEntry == null
    val pageState = if (searchRoot && searchState.query.isBlank()) catalogState.copy(
        sections = listOfNotNull(playlists.takeIf { it.isNotEmpty() }?.let {
            com.convx.music.catalog.CatalogSection("search-playlists", "Your Playlists", it)
        }) + searchState.landing,
    ) else catalogState
    val content: @Composable () -> Unit = {
    if (localPages != null) DesktopLocalLibraryScreen(localPages, Modifier.fillMaxSize(), showSearchHeader = !searchRoot, topInset = if (searchRoot) 0.dp else contentInset)
    else DesktopCatalogPage(
        destination = destination, state = pageState, tracks = tracks,
        playingId = playbackTrackId, isPlaying = playbackIsPlaying, signedIn = isSignedIn,
        library = libraryActionState, onTrackSelected = onTrackSelected, onEntrySelected = onEntrySelected,
        onPlaySelection = onPlaySelection, onSearchFilterSelected = onSearchFilterSelected,
        onHomeFilterSelected = onHomeFilterSelected, onLoadMore = onLoadMore, onRetry = onRetry,
        onSignIn = onSignInRequested, onSetSaved = onSetTrackSaved, onAddToPlaylist = onAddTrackToPlaylist,
        modifier = Modifier.fillMaxSize(), showPageHeader = !searchRoot, topInset = if (searchRoot) 0.dp else contentInset,
    )
    }
    if (searchRoot) DesktopSearchPage(search, catalogState.searchFilter, onSearchSubmit, onSearchFilterSelected,
        onSuggestedEntry, modifier, results = content)
    else Box(modifier) { content() }
}

@Composable
internal fun DesktopTrackLibraryAction(
    track: DesktopTrack,
    state: CatalogLibraryActionState,
    isSignedIn: Boolean,
    onSignInRequested: () -> Unit,
    onSetTrackSaved: (CatalogId, Boolean, Boolean) -> Unit,
    onAddTrackToPlaylist: (CatalogEntry.Track) -> Unit,
) {
    if (track.request.providerId != YOUTUBE_MUSIC_PROVIDER) return
    val id = CatalogId(track.request.providerId, track.request.id)
    val isPending = id in state.pendingTrackIds
    val media: DesktopMediaLibrary = koinInject()
    val downloads: DesktopDownloadManager = koinInject()
    val player: DesktopMusicController = koinInject()
    val status by remember(track.request.id, track.request.providerId) {
        media.state.map { it.index.downloads.firstOrNull { item -> item.track.id == track.request.id && item.track.providerId == track.request.providerId }?.status }.distinctUntilChanged()
    }.collectAsState(null)
    var menu by remember { mutableStateOf(false) }
    Box {
        DesktopIconButton(DesktopGlyphKind.MORE, "More options for ${track.snapshot.title}", controlSize = 30.dp, onClick = { menu = true })
        dev.nucleusframework.macoscompose.components.DropdownMenu(menu, { menu = false }) {
            dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { player.appendQueue(listOf(track.request)); menu = false }) { Text("Add to Queue") }
            dev.nucleusframework.macoscompose.components.DropdownMenuItem(enabled = status != DesktopDownloadStatus.COMPLETE, onClick = {
                when (status) {
                    DesktopDownloadStatus.QUEUED, DesktopDownloadStatus.DOWNLOADING -> downloads.pause(DesktopDownloadManager.key(track.request))
                    DesktopDownloadStatus.PAUSED, DesktopDownloadStatus.FAILED -> downloads.resume(DesktopDownloadManager.key(track.request))
                    else -> downloads.enqueue(listOf(track.request))
                }
                menu = false
            }) { Text(when (status) {
                DesktopDownloadStatus.COMPLETE -> "Downloaded"
                DesktopDownloadStatus.QUEUED, DesktopDownloadStatus.DOWNLOADING -> "Pause Download"
                DesktopDownloadStatus.PAUSED, DesktopDownloadStatus.FAILED -> "Resume Download"
                else -> "Download"
            }) }
            dev.nucleusframework.macoscompose.components.DropdownMenuItem(enabled = !isPending, onClick = {
                menu = false
                if (isSignedIn) onSetTrackSaved(id, track.isInLibrary, !track.isInLibrary) else onSignInRequested()
            }) { Text(if (isSignedIn && track.isInLibrary) "Remove from Library" else "Add to Library") }
            dev.nucleusframework.macoscompose.components.DropdownMenuItem(enabled = !isPending, onClick = {
                menu = false
                if (isSignedIn) onAddTrackToPlaylist(track.catalogTrack) else onSignInRequested()
            }) { Text("Add to Playlist?") }
        }
    }
}

@Composable
private fun TrackCover(
    track: DesktopTrack,
    modifier: Modifier = Modifier,
    index: Int,
    editorial: Boolean = false,
) {
    val palette = desktopPalette()
    Box(
        modifier = modifier
            .clip(if (editorial) RoundedCornerShape(topStart = ConvxDesignTokens.Metrics.LargeCardCorner, bottomStart = ConvxDesignTokens.Metrics.LargeCardCorner) else RoundedCornerShape(ConvxDesignTokens.Metrics.ArtworkCorner))
            .background(Brush.linearGradient(listOf(track.artStart, track.artEnd))),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val unit = size.minDimension
            val pale = palette.foreground.copy(alpha = 0.18f)
            val soft = palette.foreground.copy(alpha = 0.11f)
            when (index % 5) {
                0 -> {
                    drawCircle(pale, unit * 0.4f, Offset(size.width * 0.68f, size.height * 0.34f))
                    drawCircle(soft, unit * 0.32f, Offset(size.width * 0.32f, size.height * 0.72f))
                    drawCircle(palette.foreground.copy(alpha = 0.12f), unit * 0.18f, Offset(size.width * 0.72f, size.height * 0.36f), style = Stroke(unit * 0.025f))
                }
                1 -> {
                    drawCircle(Color(0xFFFFE3BD).copy(alpha = 0.72f), unit * 0.23f, Offset(size.width * 0.69f, size.height * 0.3f))
                    repeat(4) { row ->
                        drawLine(soft, Offset(0f, size.height * (0.59f + row * 0.09f)), Offset(size.width, size.height * (0.46f + row * 0.09f)), unit * 0.035f)
                    }
                }
                2 -> {
                    drawCircle(pale, unit * 0.38f, Offset(size.width * 0.5f, size.height * 0.52f), style = Stroke(unit * 0.06f))
                    drawCircle(soft, unit * 0.23f, Offset(size.width * 0.5f, size.height * 0.52f))
                }
                3 -> {
                    drawRect(Color.Black.copy(alpha = 0.12f), Offset(0f, size.height * 0.63f), Size(size.width, size.height * 0.37f))
                    drawCircle(Color(0xFFFFD6A7).copy(alpha = 0.68f), unit * 0.22f, Offset(size.width * 0.52f, size.height * 0.37f))
                    drawArc(palette.foreground.copy(alpha = 0.2f), 190f, 160f, false, Offset(size.width * 0.1f, size.height * 0.55f), Size(size.width * 0.8f, size.height * 0.48f), style = Stroke(unit * 0.045f))
                }
                else -> {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.72f)
                        cubicTo(size.width * 0.24f, size.height * 0.44f, size.width * 0.48f, size.height * 0.95f, size.width, size.height * 0.45f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, palette.foreground.copy(alpha = 0.13f))
                    drawCircle(palette.foreground.copy(alpha = 0.2f), unit * 0.13f, Offset(size.width * 0.72f, size.height * 0.29f))
                }
            }
        }
        RemoteArtwork(track.artworkUrl, Modifier.fillMaxSize())
        if (editorial) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f))),
                ),
            )
        }
    }
}

@Composable
private fun PlaylistArtwork(track: DesktopTrack?, index: Int) {
    Box(
        Modifier.size(34.dp)
            .clip(RoundedCornerShape(ConvxDesignTokens.Metrics.ArtworkCorner))
            .background(Brush.linearGradient(listOf(Color(0xFF574A60), Color(0xFF303947)))),
    ) {
        track?.let { TrackCover(it, Modifier.fillMaxSize(), index) }
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)))
    }
}

@Composable
private fun SectionTitle(title: String, detail: String) {
    val palette = desktopPalette()
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(title, color = palette.foreground, fontSize = ConvxDesignTokens.Type.SectionHeader, fontWeight = FontWeight.Bold, letterSpacing = (-0.35).sp)
        if (detail.isNotEmpty()) {
            Text(detail, color = palette.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp)
        }
    }
}

@Composable
internal fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit = {},
    placeholder: String = "Search songs, artists, albums, and playlists",
    label: String = "Search music",
    leadingGlyph: DesktopGlyphKind = DesktopGlyphKind.SEARCH,
    compact: Boolean = false,
) {
    val palette = desktopPalette()
    var focused by remember { mutableStateOf(false) }
    androidx.compose.foundation.text.BasicTextField(
        value = value, onValueChange = onValueChange, singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = DesktopWebFont, color = palette.foreground, fontSize = 14.sp),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(palette.accent),
        modifier = modifier.then(dev.nucleusframework.macoscompose.components.LocalNoWindowDragModifier.current).onFocusChanged { focused = it.isFocused }
            .height(if (compact) 34.dp else 44.dp)
            .clip(CircleShape)
            .background(palette.foreground.copy(alpha = .065f))
            .border(if (focused) 2.dp else .5.dp, if (focused) palette.accent.copy(alpha = .65f) else palette.separator, CircleShape)
            .onPreviewKeyEvent {
                if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) { onSubmit(); true } else false
            }.semantics { contentDescription = label },
        decorationBox = { input ->
            Row(Modifier.fillMaxSize().padding(horizontal = if (compact) 11.dp else 14.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DesktopGlyph(leadingGlyph, palette.muted, 16.dp)
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, color = palette.muted, fontSize = 14.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    input()
                }
                if (value.isNotEmpty()) DesktopIconButton(DesktopGlyphKind.REMOVE, "Clear search", controlSize = 22.dp,
                    onClick = { onValueChange("") })
            }
        },
    )
}

@Composable
internal fun DesktopMiniPlayer(
    playback: DesktopCompactPlayback,
    controller: DesktopMusicController,
    backdrop: LayerBackdrop,
    crossfadeEnabled: Boolean,
    onToggleCrossfade: () -> Unit,
    onExpand: () -> Unit,
    sharedElements: PlaybackSharedElements,
    onQueueClick: () -> Unit,
    onLyricsClick: () -> Unit,
    sleepTimerActive: Boolean,
    onSleepTimerClick: () -> Unit,
    appearance: com.convx.music.playback.AppearanceSettings,
    reducedMotion: Boolean,
    liked: Boolean,
    likeEnabled: Boolean,
    onToggleLike: () -> Unit,
    likeMessage: String? = null,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    val track = playback.track
    BoxWithConstraints(modifier) {
    val compact = maxWidth < 500.dp
    Row(Modifier.fillMaxSize()
        .shadow(14.dp, PlayerShape)
        .clip(PlayerShape)
        .then(if (appearance.glassEnabled) Modifier.desktopGlass(backdrop, PlayerShape, LiquidGlassMaterials.MiniPlayer.copy(
            tint = palette.surface.copy(alpha = appearance.glassOpacity), blurRadius = appearance.glassBlurRadius.dp,
            saturation = appearance.glassSaturation, refractionHeight = 8.dp, refractionAmount = appearance.glassLensAmount.dp,
            chromaticAberration = appearance.glassChromaticAberration, depthEffect = appearance.glassDepthEffect)) else Modifier.background(palette.surface))
        .border(0.5.dp, palette.separator, PlayerShape)
        .semantics { contentDescription = "Mini player" }
        .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(sharedElements.controls, verticalAlignment = Alignment.CenterVertically) {
                if (!compact) DesktopMiniPlayerButton(DesktopGlyphKind.SHUFFLE, if (playback.shuffleEnabled) "Turn shuffle off" else "Turn shuffle on", enabled = track != null, selected = playback.shuffleEnabled, onClick = controller::toggleShuffle)
                DesktopMiniPlayerButton(DesktopGlyphKind.PREVIOUS, "Previous track", enabled = playback.canSkipPrevious, onClick = { controller.requestSkip(false) })
                DesktopMiniPlayerButton(if (playback.isPlaying || playback.playWhenReady) DesktopGlyphKind.PAUSE else DesktopGlyphKind.PLAY,
                    if (playback.isPlaying || playback.playWhenReady) "Pause" else "Play", modifier = sharedElements.playPause, enabled = track != null, onClick = controller::togglePlayPause)
                DesktopMiniPlayerButton(DesktopGlyphKind.NEXT, "Next track", enabled = playback.canSkipNext, onClick = { controller.requestSkip(true) })
                if (!compact) DesktopMiniPlayerButton(if (playback.repeatMode == PlaybackRepeatMode.ONE) DesktopGlyphKind.REPEAT_ONE else DesktopGlyphKind.REPEAT,
                    "Repeat: ${playback.repeatMode.name.lowercase()}", enabled = track != null, selected = playback.repeatMode != PlaybackRepeatMode.OFF, onClick = controller::cycleRepeatMode)
            }
            Column(Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Row(Modifier.fillMaxWidth().height(34.dp).desktopInteractive(scale = .995f, enabled = track != null, onClick = onExpand),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RemoteArtwork(track?.artworkUrl, sharedElements.artwork.size(34.dp).clip(RoundedCornerShape(5.dp)))
                    Column(Modifier.weight(1f)) {
                        Text(track?.title ?: "Nothing Playing", modifier = sharedElements.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(playback.errorMessage ?: likeMessage ?: if (playback.phase == PlaybackPhase.BUFFERING && playback.playWhenReady) "Preparing audio…" else track?.artists?.joinToString().orEmpty(),
                            modifier = sharedElements.artist, color = palette.muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                DesktopPlayerProgress(controller, compact = true, reducedMotion = reducedMotion)
            }
            PushButton(onClick = onToggleLike, enabled = likeEnabled, style = PushButtonStyle.BorderlessBezel,
                modifier = Modifier.size(36.dp).semantics {
                    contentDescription = if (liked) "Remove from Liked Music" else "Add to Liked Music"
                    stateDescription = if (liked) "Liked" else "Not liked"
                }) {
                DesktopGlyph(if (liked) DesktopGlyphKind.FAVORITED else DesktopGlyphKind.FAVORITE,
                    if (liked) palette.accent else palette.foreground, 19.dp)
            }
            DesktopMiniPlayerButton(DesktopGlyphKind.LYRICS, "Lyrics", enabled = track != null, onClick = onLyricsClick)
            DesktopMiniPlayerButton(DesktopGlyphKind.QUEUE, "Queue", modifier = sharedElements.queue, enabled = playback.mediaItemCount > 0, onClick = onQueueClick)
            Box {
                var volumeOpen by remember { mutableStateOf(false) }
                DesktopMiniPlayerButton(DesktopGlyphKind.VOLUME, "Volume", onClick = { volumeOpen = !volumeOpen })
                dev.nucleusframework.macoscompose.components.DropdownMenu(volumeOpen, { volumeOpen = false }) {
                    Box(Modifier.width(180.dp).padding(12.dp)) { DesktopPlayerVolume(controller) }
                }
            }
    }
    }
}

@Composable
private fun DesktopMiniPlayerButton(
    kind: DesktopGlyphKind,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = desktopPalette()
    Box(modifier.size(30.dp).clip(CircleShape)
        .semantics { contentDescription = label }
        .desktopInteractive(enabled = enabled, scale = .9f, hoverColor = palette.foreground.copy(alpha = .08f), onClick = onClick),
        contentAlignment = Alignment.Center) {
        DesktopGlyph(kind, (if (selected) palette.accent else palette.foreground).copy(alpha = if (enabled) 1f else .4f),
            if (kind == DesktopGlyphKind.PLAY || kind == DesktopGlyphKind.PAUSE) 21.dp else 16.dp)
    }
}

@Composable
private fun DesktopQueuePanel(
    queue: PlaybackQueueSnapshot,
    controller: PlaybackQueueController,
    onDismiss: () -> Unit,
    backdrop: LayerBackdrop,
    micaBackdropActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    val keys = remember(queue.entries) {
        val occurrences = mutableMapOf<String, Int>()
        queue.entries.map { entry ->
            val occurrence = occurrences.getOrDefault(entry.id, 0)
            occurrences[entry.id] = occurrence + 1
            "${entry.id}:$occurrence"
        }
    }
    val listState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(listState) { from, to ->
        val fromIndex = keys.indexOf(from.key)
        val toIndex = keys.indexOf(to.key)
        if (fromIndex >= 0 && toIndex >= 0) controller.moveQueueItem(fromIndex, toIndex)
    }
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxHeight()
            .desktopGlass(backdrop, RoundedCornerShape(0.dp), LiquidGlassMaterials.MiniPlayer.copy(tint = palette.surface.copy(alpha = .6f),
                blurRadius = 16.dp, saturation = 2.2f, refractionAmount = 0.dp))
            .drawBehind { drawLine(palette.separator, Offset.Zero, Offset(0f, size.height), 1.dp.toPx()) }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("Continue Playing", color = palette.foreground, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(queue.entries.getOrNull(queue.currentIndex)?.let { "From ${it.title}" } ?: "${queue.entries.size} songs", color = palette.muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Clear", Modifier.desktopInteractive(enabled = queue.currentIndex >= 0, onClick = controller::removeQueueItemsAfterCurrent), color = palette.accent, fontSize = 13.sp)
                DesktopIconButton(
                    kind = DesktopGlyphKind.CLOSE,
                    description = "Close queue",
                    onClick = onDismiss,
                )
            }
        }
        if (queue.entries.isEmpty()) {
            Text("Your queue is empty.", color = palette.muted, fontSize = 13.sp, modifier = Modifier.padding(vertical = 22.dp))
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(queue.entries, key = { index, _ -> keys[index] }) { index, entry ->
                    ReorderableItem(reorderState, key = keys[index]) { dragging ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind { drawLine(palette.separator, Offset(50.dp.toPx(), size.height), Offset(size.width, size.height), .5.dp.toPx()) }
                            .background(if (index == queue.currentIndex) palette.foreground.copy(alpha = .055f) else Color.Transparent)
                            .desktopInteractive(scale = 1f, hoverColor = palette.foreground.copy(alpha = .03f)) { controller.playQueueItem(index) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(Modifier.size(24.dp, 40.dp).draggableHandle()
                            .semantics { contentDescription = "Reorder ${entry.title}" }, contentAlignment = Alignment.Center) {
                            DesktopGlyph(DesktopGlyphKind.SIDEBAR, if (dragging) palette.accent else palette.muted, 15.dp)
                        }
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(5.dp)).background(palette.foreground.copy(alpha = 0.08f))) {
                            RemoteArtwork(entry.artworkUrl, Modifier.fillMaxSize())
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                entry.title,
                                color = palette.foreground,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(entry.artists.joinToString(), color = palette.muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Box {
                            var open by remember { mutableStateOf(false) }
                            DesktopIconButton(DesktopGlyphKind.MORE, "Queue options for ${entry.title}", controlSize = 26.dp, onClick = { open = true })
                            dev.nucleusframework.macoscompose.components.DropdownMenu(open, { open = false }) {
                                dev.nucleusframework.macoscompose.components.DropdownMenuItem(enabled = index > 0, onClick = { controller.moveQueueItem(index, index - 1); open = false }) { Text("Move Up") }
                                dev.nucleusframework.macoscompose.components.DropdownMenuItem(enabled = index < queue.entries.lastIndex, onClick = { controller.moveQueueItem(index, index + 1); open = false }) { Text("Move Down") }
                                dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { controller.removeQueueItem(index); open = false }) { Text("Remove from Queue") }
                            }
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DesktopWindowControlPill(
    onCloseWindow: () -> Unit,
    onMinimizeWindow: () -> Unit,
    onZoomWindow: () -> Unit,
) {
    val palette = desktopPalette()
    Row(Modifier.shadow(5.dp, CircleShape).clip(CircleShape)
        .background(palette.surface.copy(alpha = .8f)).border(.5.dp, palette.separator, CircleShape)
        .padding(horizontal = 7.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        DesktopIconButton(DesktopGlyphKind.WINDOW_MINIMIZE, "Minimize window", controlSize = 24.dp, glyphSize = 13.dp, onClick = onMinimizeWindow)
        DesktopIconButton(DesktopGlyphKind.WINDOW_ZOOM, "Zoom window", controlSize = 24.dp, glyphSize = 13.dp, onClick = onZoomWindow)
        DesktopIconButton(DesktopGlyphKind.WINDOW_CLOSE, "Close window", controlSize = 24.dp, glyphSize = 13.dp, onClick = onCloseWindow)
    }
}

@Composable
private fun DesktopIconButton(
    kind: DesktopGlyphKind,
    description: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    prominent: Boolean = false,
    clipContent: Boolean = true,
    controlSize: Dp = 35.dp,
    glyphSize: Dp = 17.dp,
    onClick: () -> Unit,
) {
    val palette = desktopPalette()
    Box(modifier.size(controlSize).clip(CircleShape)
        .background(if (selected || prominent) palette.foreground.copy(alpha = .1f) else Color.Transparent)
        .semantics { contentDescription = description }
        .desktopInteractive(enabled = enabled, scale = .9f, hoverColor = palette.foreground.copy(alpha = .08f), onClick = onClick),
        contentAlignment = Alignment.Center) {
        DesktopGlyph(kind, (if (selected) palette.accent else palette.foreground).copy(alpha = if (enabled) 1f else .3f), glyphSize)
    }
}

internal enum class DesktopGlyphKind(val symbolName: String) {
    FOLDER("folder"),
    DOWNLOAD("arrow.down.circle"),
    VOLUME("speaker.wave.2.fill"),
    MUTE("speaker.slash.fill"),
    COLLAPSE("chevron.down"),
    AUTOMIX("sparkles"),
    EQUALIZER("slider.horizontal.3"),
    INFO("info.circle"),
    FAVORITE("star"),
    FAVORITED("star.fill"),
    APPEARANCE("circle.lefthalf.filled"),
    SETTINGS("gearshape"),
    HOUSE("house"),
    SEARCH("magnifyingglass"),
    GRID("square.grid.2x2"),
    MUSIC_NOTE("music.note"),
    CHARTS("chart.bar.fill"),
    RADIO("dot.radiowaves.left.and.right"),
    LIBRARY("music.note.square.stack.fill"),
    PLAYLIST("music.note.list"),
    ALBUM("square.stack"),
    ARTIST("mic"),
    PROFILE("person.crop.circle"),
    FILTER("line.3.horizontal.decrease"),
    SIDEBAR("line.3.horizontal"),
    SHUFFLE("shuffle"),
    REPEAT_ONE("repeat.1"),
    PREVIOUS("backward.fill"),
    PLAY("play.fill"),
    PAUSE("pause.fill"),
    NEXT("forward.fill"),
    REPEAT("repeat"),
    CROSSFADE("arrow.left.arrow.right"),
    QUEUE("list.bullet"),
    MOVE_UP("arrow.up"),
    MOVE_DOWN("arrow.down"),
    REMOVE("xmark"),
    CLEAR_UP_NEXT("text.badge.minus"),
    CLOSE("xmark.circle.fill"),
    LYRICS("quote.bubble"),
    TIMER("moon.fill"),
    MORE("ellipsis"),
    HISTORY("clock.arrow.circlepath"),
    SAVE("plus"),
    SAVED("checkmark"),
    ADD_TO_PLAYLIST("text.badge.plus"),
    BACK("chevron.left"),
    FORWARD("chevron.right"),
    RELOAD("arrow.clockwise"),
    MINIMIZE("minus"),
    MAXIMIZE("square"),
    WINDOW_MINIMIZE("minus"),
    WINDOW_ZOOM("arrow.up.left.and.arrow.down.right"),
    WINDOW_CLOSE("xmark");
}

@Composable
internal fun DesktopGlyph(kind: DesktopGlyphKind, color: Color, size: Dp, selected: Boolean = false) {
    val symbol = remember(kind, selected) {
        val filled = if (selected) when (kind) {
            DesktopGlyphKind.HOUSE, DesktopGlyphKind.GRID, DesktopGlyphKind.FOLDER, DesktopGlyphKind.ALBUM,
            DesktopGlyphKind.ARTIST, DesktopGlyphKind.PROFILE, DesktopGlyphKind.SETTINGS, DesktopGlyphKind.LYRICS,
            DesktopGlyphKind.DOWNLOAD -> SfSymbol.named("${kind.symbolName}.fill")
            else -> null
        } else null
        filled ?: SfSymbol.named(kind.symbolName) ?: error("Unknown SF Symbol: ${kind.symbolName}")
    }
    SfSymbolIcon(
        symbol = symbol,
        modifier = Modifier.size(size),
        size = size,
        weight = SfWeight.Bold,
        mode = SfRenderingMode.Monochrome,
        tint = color,
        contentDescription = null,
    )
}

@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DesktopForeground,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: androidx.compose.ui.text.style.TextAlign? = null,
) {
    ComposeText(
        text = text,
        modifier = modifier,
        style = TextStyle(fontFamily = DesktopWebFont, color = color, fontSize = fontSize, fontWeight = fontWeight, letterSpacing = letterSpacing, lineHeight = lineHeight, textAlign = textAlign ?: androidx.compose.ui.text.style.TextAlign.Unspecified),
        maxLines = maxLines,
        overflow = overflow,
    )
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds.coerceAtLeast(0L) / 1_000L
    return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}
