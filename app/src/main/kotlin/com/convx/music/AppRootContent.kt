/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.convx.music.constants.AppBackgroundColorKey
import com.convx.music.constants.AppBarHeight
import com.convx.music.constants.AppleMusicUiKey
import com.convx.music.constants.DarkModeKey
import com.convx.music.constants.DefaultOpenTabKey
import com.convx.music.constants.DockedAccessoryHeight
import com.convx.music.constants.EnableSettingsPopupKey
import com.convx.music.constants.ForceTabletLayoutKey
import com.convx.music.constants.IosOverscrollKey
import com.convx.music.constants.MiniPlayerHeight
import com.convx.music.constants.NavigationBarAnimationSpec
import com.convx.music.constants.NavigationBarHeight
import com.convx.music.constants.PureBlackKey
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.PlayerBackgroundStyleKey
import com.convx.music.constants.SelectedThemeColorKey
import com.convx.music.constants.ShowHistoryButtonKey
import com.convx.music.constants.PauseListenHistoryKey
import com.convx.music.constants.ShowStatsButtonKey
import com.convx.music.constants.SideBarCollapsedKey
import com.convx.music.constants.SlimNavBarHeight
import com.convx.music.constants.SlimNavBarKey
import com.convx.music.constants.UseNewMiniPlayerDesignKey
import com.convx.music.db.MusicDatabase
import com.convx.music.history.HistoryLocalDataSource
import com.convx.music.extensions.toEnum
import com.convx.music.playback.DownloadUtil
import com.convx.music.playback.PlayerConnection
import com.convx.music.ui.component.AppFloatingNavBar
import com.convx.music.ui.component.AppFloatingNowPlayingPill
import com.convx.music.ui.component.AppFloatingSideBar
import com.convx.music.ui.component.BottomSheetPage
import com.convx.music.ui.component.FloatingMiniPlayerWidthFraction
import com.convx.music.ui.component.ListenTogetherOverlay
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalAppBackdropFrozen
import com.convx.music.ui.component.LocalAppMotionFrozen
import com.convx.music.ui.component.LocalAppleMusicUi
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.LocalDownloads
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.LocalItemPrefs
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.LocalNavSearchState
import com.convx.music.ui.component.LocalPooledGlass
import com.convx.music.ui.component.LocalSheetPresentation
import com.convx.music.ui.component.NavBarSearchInputBar
import com.convx.music.ui.component.OverlayMenu
import com.convx.music.ui.component.PooledGlass
import com.convx.music.ui.component.QueueConflictDialog
import com.convx.music.ui.component.QueueReplacedUndoSnackbar
import com.convx.music.ui.component.SheetPresentationState
import com.convx.music.ui.component.SideBarAccountRow
import com.convx.music.ui.component.AppTabViewTopPill
import com.convx.music.ui.component.NavMorph
import com.convx.music.ui.component.SideBarCollapsedWidth
import com.convx.music.ui.component.SideBarContentInset
import com.convx.music.ui.component.SideBarMargin
import com.convx.music.ui.component.TabletWidthThreshold
import com.convx.music.ui.component.backdrop.BackdropEffectScope
import com.convx.music.ui.component.backdrop.GlassPoolScale
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.colorControls
import com.convx.music.ui.component.backdrop.glassPool
import com.convx.music.ui.component.backdrop.rememberGlassPool
import com.convx.music.ui.component.floatingtabbar.FloatingTabBarInlineBehavior
import com.convx.music.ui.component.floatingtabbar.rememberFloatingTabBarScrollConnection
import com.convx.music.ui.component.glassSaturation
import com.convx.music.ui.component.iosSheetPresentation
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.rememberBottomSheetState
import com.convx.music.ui.component.rememberItemPrefs
import com.convx.music.ui.component.rememberSheetPresentation
import com.convx.music.ui.component.shimmer.ShimmerTheme
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.Scaffold
import com.convx.music.ui.ios.compat.SnackbarHost
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.foundation.LocalIosBackdrop
import com.convx.music.ui.menu.PlayerMenu
import com.convx.music.ui.menu.YouTubeSongMenu
import com.convx.music.ui.player.BottomSheetPlayer
import com.convx.music.ui.player.InstallPlayerMorphLayers
import com.convx.music.ui.screens.Screens
import com.convx.music.ui.screens.SettingDialoge
import com.convx.music.ui.screens.settings.DarkMode
import com.convx.music.ui.screens.settings.NavigationTab
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.ConvxTheme
import com.convx.music.ui.theme.ForcedDarkScope
import com.convx.music.ui.theme.DefaultThemeColor
import com.convx.music.ui.utils.LocalSharedTransitionScope
import com.convx.music.ui.utils.ShowMediaInfo
import com.convx.music.ui.utils.appBarScrollBehavior
import com.convx.music.ui.utils.rememberIosOverscrollFactory
import com.convx.music.ui.utils.resetHeightOffset
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.viewmodels.AccountImageState
import com.music.innertube.models.SongItem
import com.valentinilk.shimmer.LocalShimmerTheme
import dev.chrisbanes.haze.HazeState
import com.convx.music.constants.TopBarBlurStyle
import com.convx.music.constants.TopBarBlurStyleKey
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLDecoder

private const val DIAG_DISABLE_BACKDROP = false
private const val USE_SHARED_GLASS_POOL = true

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalSharedTransitionApi::class,
)
@Composable
internal fun AppRootContent(
    activity: MainActivity,
    intentRouter: AppIntentRouter,
    playerConnection: PlayerConnection?,
    database: dagger.Lazy<MusicDatabase>,
    downloadUtil: dagger.Lazy<DownloadUtil>,
    syncUtils: dagger.Lazy<SyncUtils>,
    historyDataSource: dagger.Lazy<HistoryLocalDataSource>,
) {
    AppStartupEffects(activity, playerConnection)

    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val playerBackgroundStyle by rememberEnumPreference(
        PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.GRADIENT,
    )
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }

    val artworkChromeDark = useDarkTheme || LocalAmbientDarkChrome.current.value
    LaunchedEffect(artworkChromeDark) {
        com.convx.music.utils.setSystemBarAppearance(activity.window, artworkChromeDark)
    }

    val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
    val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
        pureBlackEnabled && useDarkTheme
    }

    // Keep artwork tint local so track changes do not recompute the app theme.
    val (selectedThemeColorInt) = rememberPreference(SelectedThemeColorKey, defaultValue = DefaultThemeColor.toArgb())
    val themeColor = Color(selectedThemeColorInt)

    ConvxTheme(
        darkTheme = useDarkTheme,
        pureBlack = pureBlack,
        themeColor = themeColor,
    ) {
        val (appBackgroundColorInt) = rememberPreference(AppBackgroundColorKey, defaultValue = 0)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                // Base color visible wherever a screen does not draw its own background.
                .background(
                    when {
                        appBackgroundColorInt != 0 -> Color(appBackgroundColorInt)
                        pureBlack -> Color.Black
                        else -> AppTheme.colorScheme.surface
                    }
                )
        ) {
            val focusManager = LocalFocusManager.current
            val density = LocalDensity.current
            val configuration = LocalWindowInfo.current
            val cutoutInsets = WindowInsets.displayCutout
            val windowsInsets = WindowInsets.systemBars
            val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }
            val bottomInsetDp = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

            val navController = rememberNavController()

            val navigationArtwork = remember(navController) { com.convx.music.ui.utils.NavigationArtworkState() }
            InstallMorphNavListener(navController, navigationArtwork)

            val accountImageUrl by AccountImageState.url.collectAsStateWithLifecycle()
            val (sidebarAccountName) = rememberPreference(com.convx.music.constants.AccountNameKey, "")
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val (previousTab, setPreviousTab) = rememberSaveable { mutableStateOf("home") }

            val navItems = rememberAppNavItems()
            val listenTogetherInTopBar = navItems.listenTogetherInTopBar
            val localOnlyMode = navItems.localOnlyMode
            val navigationItems = navItems.navigationItems
            val (slimNav) = rememberPreference(SlimNavBarKey, defaultValue = false)
            val (appleMusicUi) = rememberPreference(AppleMusicUiKey, defaultValue = true)
            val floatingNavigationItems = navItems.floatingNavigationItems
            // Expand only at the list top; collapse as soon as the list scrolls away.
            val floatingNavBarScrollConnection = rememberFloatingTabBarScrollConnection(
                inlineBehavior = FloatingTabBarInlineBehavior.ExpandedAtTopOnly,
            )
            val glassConfigState = rememberGlassConfigState(artworkChromeDark)
            val glassEffectConfig = glassConfigState.config
            val darkGlassEffectConfig = rememberGlassConfigState(artworkChromeDark = true).config
            // Gate backdrop capture once for devices that cannot render glass.
            val glassAllowed = isGlassAllowed()

            // Share one blurred backdrop across glass surfaces to avoid duplicate effect passes.
            val glassPoolBackdrop = rememberGlassPool()
            val poolSaturation = glassSaturation(glassEffectConfig.vibrancy)
            val poolBlurPx = with(LocalDensity.current) {
                glassEffectConfig.blurRadius.dp.toPx()
            } * GlassPoolScale
            val poolEffects: BackdropEffectScope.() -> Unit =
                remember(poolSaturation, poolBlurPx) {
                    {
                        if (poolSaturation != 1f) colorControls(saturation = poolSaturation)
                        if (poolBlurPx > 0f) blur(poolBlurPx)
                    }
                }
            val pooledGlass = remember(
                glassPoolBackdrop,
                glassEffectConfig.blurRadius,
                glassEffectConfig.vibrancy,
                glassAllowed,
                glassEffectConfig.anyComponentEnabled,
            ) {
                if (USE_SHARED_GLASS_POOL && glassAllowed && glassEffectConfig.anyComponentEnabled) {
                    PooledGlass(
                        backdrop = glassPoolBackdrop,
                        blurRadiusDp = glassEffectConfig.blurRadius,
                        vibrancy = glassEffectConfig.vibrancy,
                    )
                } else {
                    null
                }
            }

            val (useNewMiniPlayerDesign) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
            val defaultOpenTab = remember {
                activity.dataStore[DefaultOpenTabKey].toEnum(defaultValue = NavigationTab.HOME)
            }
            val tabOpenedFromShortcut = remember {
                when (activity.intent?.action) {
                    MainActivity.ACTION_SEARCH -> NavigationTab.SEARCH
                    MainActivity.ACTION_LIBRARY -> NavigationTab.LIBRARY
                    else -> null
                }
            }

            val openSearchOnLaunch = remember {
                (tabOpenedFromShortcut ?: defaultOpenTab) == NavigationTab.SEARCH
            }
            val initialMainTabRoute = remember {
                when (tabOpenedFromShortcut ?: defaultOpenTab) {
                    NavigationTab.HOME, NavigationTab.SEARCH -> Screens.Home.route
                    NavigationTab.LIBRARY -> Screens.Library.route
                }
            }

            val topLevelScreens = remember {
                listOf(
                    Screens.Home.route,
                    Screens.Library.route,
                    Screens.Radio.route,
                    Screens.Songs.route,
                    Screens.Explore.route,
                    Screens.ListenTogether.route,
                    "settings",
                )
            }

            val search = rememberAppSearchController(navController, database, localOnlyMode, activity.lifecycleScope)
            val (query, onQueryChange) = search.query
            var searchKeyboardActive by search.keyboardActive

            val currentRoute by remember {
                derivedStateOf { navBackStackEntry?.destination?.route }
            }
            LaunchedEffect(currentRoute) {
                Timber.tag("Navigation").d("route -> $currentRoute")
            }

            // Keep search as a NavHost destination so it uses the normal transition and backdrop handling.
            LaunchedEffect(Unit) {
                if (openSearchOnLaunch) {
                    navController.navigate(Screens.Search.route) { launchSingleTop = true }
                }
            }

            val effectiveRoute = currentRoute

            var downloads by remember { mutableStateOf<Map<String, androidx.media3.exoplayer.offline.Download>>(emptyMap()) }
            LaunchedEffect(currentRoute) {
                if (!AppRoutePolicy.needsDownloadState(currentRoute)) {
                    downloads = emptyMap()
                    return@LaunchedEffect
                }

                withFrameNanos { }
                val util = downloadUtil.get()
                activity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    util.downloads.collect { downloads = it }
                }
            }

            val inSearchScreen by remember {
                derivedStateOf {
                    currentRoute?.startsWith("search/") == true ||
                        currentRoute == Screens.Search.route
                }
            }
            val inSearchInputScreen by remember {
                derivedStateOf { currentRoute == Screens.Search.route }
            }
            LaunchedEffect(inSearchScreen) {
                if (!inSearchScreen) searchKeyboardActive = false
            }
            // The floating nav bar keeps Settings as one of its own tabs (see
            // floatingNavigationItems above), so it should stay visible there too —
            // only the classic nav bar treats Settings as a top-bar-only destination.
            val navigationItemRoutes = remember(floatingNavigationItems) {
                floatingNavigationItems.map { it.route }.toSet()
            }
            LaunchedEffect(currentRoute, navigationItems) {
                val route = currentRoute ?: return@LaunchedEffect
                if (route in Screens.MainRoutes && navigationItems.none { it.route == route }) {
                    navController.navigate(navigationItems.first().route) {
                        popUpTo(route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            val isKeyboardOpen = WindowInsets.isImeVisible
            val shouldShowNavigationBar = remember(currentRoute) {
                when {
                    // Settings owns a bottom-docked search field; keep the app tab bar out of its way.
                    currentRoute == Screens.Settings.route -> false
                    // Hide navigation on Settings detail pages as well as the Settings root.
                    currentRoute?.startsWith("settings/") == true -> false
                    !AppRoutePolicy.showsNavigation(currentRoute) -> false
                    else -> true
                }
            }

            val isLandscape = configuration.containerDpSize.width > configuration.containerDpSize.height

            // The floating bar stays at the bottom in landscape; only classic navigation switches to the rail.
            // The forced layout toggle lets phone users preview the tablet sidebar.

            val (forceTabletLayout) = rememberPreference(ForceTabletLayoutKey, defaultValue = false)
            // A real tablet gets the side bar on its own; the toggle only exists
            // to force it onto a device that wouldn't otherwise qualify.
            val isWideScreen = configuration.containerDpSize.width >= TabletWidthThreshold
            val showRail = forceTabletLayout || isWideScreen
            // Settings owns the whole window on tablets too: no side bar, no mini player.
            val isSettingsRoute = currentRoute == Screens.Settings.route || currentRoute?.startsWith("settings/") == true
            val (sideBarCollapsed, onSideBarCollapsedChange) = rememberPreference(SideBarCollapsedKey, defaultValue = false)
            // The artist page is full-bleed: its navigation moves up into a pill and the page keeps the whole width.
            val topNavigation = showRail && currentRoute?.startsWith("artist/{artistId}?thumb=") == true
            var topNavMenuOpen by remember { mutableStateOf(false) }
            LaunchedEffect(currentRoute) { topNavMenuOpen = false }
            // A spring overshoots past its target; an inset below zero would be negative padding.
            val sideBarContentInsetRaw by animateDpAsState(
                targetValue = when {
                    topNavigation && !topNavMenuOpen -> 0.dp
                    isSettingsRoute -> 0.dp
                    sideBarCollapsed && !topNavigation -> SideBarCollapsedWidth + SideBarMargin * 2
                    else -> SideBarContentInset
                },
                animationSpec = spring(0.9f, 400f),
                label = "sideBarContentInset",
            )
            val sideBarContentInset = sideBarContentInsetRaw.coerceAtLeast(0.dp)

            val navPadding = if (shouldShowNavigationBar && !showRail) {
                if (slimNav) SlimNavBarHeight else NavigationBarHeight
            } else {
                0.dp
            }

            val navigationBarHeight by animateDpAsState(
                targetValue = if (shouldShowNavigationBar && !showRail) NavigationBarHeight else 0.dp,
                animationSpec = NavigationBarAnimationSpec,
                label = "navBarHeight",
            )

            val playerMediaMetadata by playerConnection?.mediaMetadata?.collectAsStateWithLifecycle()
                ?: remember { mutableStateOf(null) }
            val playerBottomSheetState = rememberBottomSheetState(
                dismissedBound = 0.dp,
                // Keep the sheet's own mini player hidden; the nav surface hosts it.
                collapsedBound = 0.dp,
                expandedBound = maxHeight,
            )

            // Both ends of the mini-to-full morph are recorded into GraphicsLayers
            // owned here, above both the player and the nav bar the pill docks into --
            // remembering them any lower would release them mid-gesture.
            InstallPlayerMorphLayers()

            val playerAboveBars by remember(playerBottomSheetState) {
                derivedStateOf { playerBottomSheetState.progress > 0f }
            }

            // Reserve space only while the floating bar and its player accessory are visible.
            // Search keeps the accessory until the keyboard replaces the bar.

            val hasDockedPlayerAccessory =
                playerMediaMetadata != null && !showRail && shouldShowNavigationBar &&
                    (!inSearchScreen || !searchKeyboardActive)

            // Record the backdrop only when a visible component or expanded player needs it.
            // isCollapsed changes only when opening or closing, unlike per-frame progress.
            val backdropHasConsumer =
                (glassEffectConfig.navBarEnabled && shouldShowNavigationBar && !showRail) ||
                    (glassEffectConfig.sidePanelEnabled && showRail) ||
                    (glassEffectConfig.miniPlayerEnabled && hasDockedPlayerAccessory) ||
                    // The expanded player always samples the shared backdrop.
                    !playerBottomSheetState.isCollapsed

            // Reserve a separate row only when the collapsed mini player sits above the dismissed anchor.
            val reservesStandaloneMini = playerBottomSheetState.collapsedBound > playerBottomSheetState.dismissedBound &&
                !playerBottomSheetState.isDismissed
            val playerAwareWindowInsets = remember(
                bottomInset,
                shouldShowNavigationBar,
                reservesStandaloneMini,
                showRail,
                hasDockedPlayerAccessory,
                sideBarContentInset,
                inSearchInputScreen,
            ) {
                var bottom = bottomInset
                if (shouldShowNavigationBar && !showRail) {
                    bottom += NavigationBarHeight
                }
                if (reservesStandaloneMini && !inSearchInputScreen) bottom += MiniPlayerHeight
                if (hasDockedPlayerAccessory) bottom += DockedAccessoryHeight
                windowsInsets
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    .add(WindowInsets(top = AppBarHeight, bottom = bottom))
                    // Space for the floating side bar, carried the same way the
                    // bottom bar's space is: screens spend this as list content
                    // padding, so rows start clear of the panel but the list
                    // still spans the full width and scrolls under its glass.
                    .add(
                        WindowInsets(left = if (showRail) sideBarContentInset else 0.dp)
                    )
            }
            appBarScrollBehavior(
                canScroll = {
                    !inSearchScreen &&
                        (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                }
            )

            val topAppBarScrollBehavior = appBarScrollBehavior(
                canScroll = {
                    !inSearchScreen &&
                        (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                },
            )

            // Track pager tab changes through effectiveRoute; they do not update the NavHost entry.
            // Keep the back-stack key too, because a new search query uses the same route template.
            LaunchedEffect(navBackStackEntry, effectiveRoute) {
                // Only the results route (search/{query}) carries a query arg;
                // the search_input landing does not — guard against a null arg
                // so tapping search never NPEs.
                val rawQuery = navBackStackEntry?.arguments?.getString("query")
                if (inSearchScreen && rawQuery != null) {
                    val searchQuery = withContext(Dispatchers.IO) {
                        try {
                            URLDecoder.decode(rawQuery, "UTF-8")
                        } catch (e: IllegalArgumentException) {
                            rawQuery
                        }
                    }
                    onQueryChange(
                        TextFieldValue(
                            searchQuery,
                            TextRange(searchQuery.length)
                        )
                    )
                } else if (!inSearchScreen && navigationItems.fastAny { it.route == effectiveRoute }) {
                    onQueryChange(TextFieldValue())
                }

                topAppBarScrollBehavior.state.resetHeightOffset()

                effectiveRoute?.let {
                    setPreviousTab(it)
                }
            }

            LaunchedEffect(playerConnection) {
                val player = playerConnection?.player ?: return@LaunchedEffect
                if (player.currentMediaItem == null) {
                    if (!playerBottomSheetState.isDismissed) {
                        playerBottomSheetState.dismiss()
                    }
                } else {
                    if (playerBottomSheetState.isDismissed) {
                        playerBottomSheetState.collapseSoft()
                    }
                }
            }

            DisposableEffect(playerConnection, playerBottomSheetState) {
                val player = playerConnection?.player ?: return@DisposableEffect onDispose { }
                val listener = object : Player.Listener {
                    override fun onMediaItemTransition(
                        mediaItem: MediaItem?,
                        reason: Int,
                    ) {
                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED &&
                            mediaItem != null &&
                            playerBottomSheetState.isDismissed
                        ) {
                            playerBottomSheetState.collapseSoft()
                        }
                    }
                }
                player.addListener(listener)
                onDispose {
                    player.removeListener(listener)
                }
            }

            var shouldShowTopBar by rememberSaveable { mutableStateOf(false) }

            // React to pager tab changes and leave Library subpages to draw their own toolbar.
            val (libraryFilter, onLibraryFilterChange) = rememberEnumPreference(
                com.convx.music.constants.ChipSortTypeKey,
                com.convx.music.constants.LibraryFilter.LIBRARY,
            )
            LaunchedEffect(effectiveRoute, listenTogetherInTopBar, showRail, libraryFilter) {
                val isListenTogetherScreen = effectiveRoute == Screens.ListenTogether.route ||
                    currentRoute == "listen_together_from_topbar"
                shouldShowTopBar = effectiveRoute in topLevelScreens &&
                    effectiveRoute != "settings" &&
                    !(effectiveRoute == Screens.Library.route && libraryFilter != com.convx.music.constants.LibraryFilter.LIBRARY) &&
                    !(isListenTogetherScreen && listenTogetherInTopBar)
            }

            val coroutineScope = rememberCoroutineScope()
            var sharedSong: SongItem? by remember {
                mutableStateOf(null)
            }
            val snackbarHostState = remember { SnackbarHostState() }
            var showSettingDialoge by remember { mutableStateOf(false) }
            val (enableSettingsPopup) = rememberPreference(EnableSettingsPopupKey, defaultValue = false)

            // One handler for the three avatar/profile entry points (top bar, side bar, nav graph).
            val onAccountClick: () -> Unit = remember(navController, enableSettingsPopup) {
                {
                    if (enableSettingsPopup) {
                        showSettingDialoge = true
                    } else {
                        navController.navigate(Screens.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }

            val ringtoneViewModel: com.convx.music.ui.screens.settings.RingtoneViewModel = viewModel()
            val ringtoneUiState by ringtoneViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) {
                val queuedIntent = activity.takePendingIntent()
                if (queuedIntent != null) {
                    intentRouter.handleDeepLinkIntent(queuedIntent, navController)
                } else {
                    intentRouter.handleDeepLinkIntent(activity.intent, navController)
                }
            }

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    intentRouter.handleDeepLinkIntent(intent, navController)
                }

                activity.addOnNewIntentListener(listener)
                onDispose { activity.removeOnNewIntentListener(listener) }
            }

            val currentTitleRes = remember(effectiveRoute) {
                when (effectiveRoute) {
                    Screens.Home.route -> com.convx.music.R.string.home
                    Screens.Search.route -> com.convx.music.R.string.search
                    Screens.Library.route -> com.convx.music.R.string.filter_library
                    Screens.Radio.route -> com.convx.music.R.string.radio
                    Screens.Songs.route -> com.convx.music.R.string.songs
                    Screens.ListenTogether.route -> com.convx.music.R.string.together
                    Screens.Explore.route -> com.convx.music.R.string.tab_explore
                    else -> null
                }
            }

            val pauseListenHistory by rememberPreference(PauseListenHistoryKey, defaultValue = false)
            val (historyButtonEnabled) = rememberPreference(ShowHistoryButtonKey, defaultValue = true)
            val (showStatsButton) = rememberPreference(ShowStatsButtonKey, defaultValue = true)
            var hasHistory by remember { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(pauseListenHistory, historyButtonEnabled) {
                if (!pauseListenHistory || !historyButtonEnabled) {
                    hasHistory = null
                    return@LaunchedEffect
                }

                withFrameNanos { }
                historyDataSource.get().observeEventCount().collect { hasHistory = it > 0 }
            }
            val showHistoryButton =
                historyButtonEnabled && (!pauseListenHistory || hasHistory == true)

            val backdropState = rememberAppBackdropState(pureBlack, currentRoute, floatingNavBarScrollConnection)
            val baseBg = backdropState.baseBg
            val appBackdrop = backdropState.appBackdrop
            val backdropFrozenProvider = backdropState.frozenProvider
            val appMotionFrozen = backdropState.motionFrozen
            val backdropFreezeConnection = backdropState.scrollConnection

            // One provider replaces Android's stretch/glow edge effect with the
            // iOS rubber-band for every scroll container in the app.
            val iosOverscroll by rememberPreference(IosOverscrollKey, defaultValue = true)
            val iosOverscrollFactory = rememberIosOverscrollFactory()

            val enterSearch = search.enterSearch
            val navSearchState = search.navSearchState(inSearchScreen, inSearchInputScreen, navController)

            // Counts the sheets currently on screen, so the presentation below holds
            // until the last one closes rather than the first. Every sheet registers
            // itself through the shared presenter -- see PresentingSheetEffect.
            val sheetPresentationState = remember { SheetPresentationState() }

            val fullscreenVideo = remember { com.convx.music.ui.player.FullscreenVideoState() }
            CompositionLocalProvider(
                com.convx.music.ui.player.LocalFullscreenVideo provides fullscreenVideo,
                LocalOverscrollFactory provides
                    if (iosOverscroll) iosOverscrollFactory else LocalOverscrollFactory.current,
                LocalDatabase provides database,
                LocalLocalOnlyMode provides localOnlyMode,
                LocalNavSearchState provides navSearchState,
                LocalSheetPresentation provides sheetPresentationState,
                // onSurface already carries the accent-contrast treatment (see
                // ColorScheme.accentText), so pure black must not force plain white
                // back over it.
                LocalContentColor provides AppTheme.colorScheme.onSurface,
                LocalPlayerConnection provides playerConnection,
                LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
                LocalDownloadUtil provides downloadUtil,
                LocalShimmerTheme provides ShimmerTheme,
                LocalSyncUtils provides syncUtils,
                // Disable Listen Together until ready; current entry points handle a missing manager.
                LocalListenTogetherManager provides null,
                LocalGlassEffectConfig provides glassEffectConfig,
                com.convx.music.ui.theme.LocalDarkGlassEffectConfig provides darkGlassEffectConfig,
                LocalAppleMusicUi provides appleMusicUi,
                LocalTabView provides showRail,
                LocalAppBackdrop provides appBackdrop,
                LocalIosBackdrop provides appBackdrop,
                LocalAppBackdropFrozen provides backdropFrozenProvider,
                LocalAppMotionFrozen provides appMotionFrozen,
                LocalRingtoneViewModel provides ringtoneViewModel,
                // Read preferences once for all list rows and grid tiles.
                LocalItemPrefs provides rememberItemPrefs(),
                // Share one downloads collector across the app.
                LocalDownloads provides downloads,
            ) {

                // Sheet pages present as cards; popup menus leave the page in place.
                val menuPresentation = rememberSheetPresentation(
                    LocalBottomSheetPageState.current.isVisible,
                )

                val scaffoldPresentation = { menuPresentation }
                val playerLayerPresentation = { menuPresentation }

                val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

                val onNavItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState, enterSearch) {
                    { screen: Screens, _: Boolean ->
                        if (playerBottomSheetState.isExpanded) playerBottomSheetState.collapseSoft()
                        if (screen == Screens.Search) {
                            enterSearch()
                        } else if (navController.currentDestination?.route == screen.route) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                            coroutineScope.launch { topAppBarScrollBehavior.state.resetHeightOffset() }
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                    }
                }

                val onSearchLongClick: () -> Unit = remember(navController) {
                    {
                        navController.navigate("recognition") {
                            launchSingleTop = true
                        }
                    }
                }

                val navBarTotalHeight = bottomInset + NavigationBarHeight

                // Top-level tabs and routes without local chrome share this haze source.

                val topBarHazeState = remember { HazeState() }
                // On API 33+ glass-capable devices, the top bar blurs appBackdrop directly.
                val topBarBlurStyle by rememberEnumPreference(TopBarBlurStyleKey, TopBarBlurStyle.HAZE_PROGRESSIVE)
                val directTopBlur = glassAllowed &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    topBarBlurStyle == TopBarBlurStyle.SINGLE_PASS
                val showFallbackTopHaze = !shouldShowTopBar &&
                    AppRoutePolicy.showsFallbackTopHaze(currentRoute)
                val menuBackdrop = rememberLayerBackdrop()
                val playerBackdropHolder = remember { com.convx.music.ui.player.PlayerBackdropHolder() }
                val menuSourceBackdrop = (if (playerBottomSheetState.isExpanded) playerBackdropHolder.backdrop else null) ?: menuBackdrop

                // Capture the page and chrome behind the menu. Keep the menu as a sibling
                // to avoid a RenderNode cycle.
                Box(
                    Modifier.fillMaxSize().then(
                        if (AppRoutePolicy.isWebViewRoute(currentRoute)) Modifier else Modifier.layerBackdrop(menuBackdrop),
                    ),
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AppTopBar(
                                visible = shouldShowTopBar || showFallbackTopHaze,
                                chromeVisible = shouldShowTopBar,
                                showRail = showRail,
                                startInset = if (showRail) sideBarContentInset +
                                    (if (sideBarCollapsed) 0.dp else 10.dp) else 0.dp,
                                hazeState = topBarHazeState,
                                backdrop = if (directTopBlur && shouldShowTopBar) appBackdrop else null,
                                cutoutInsets = cutoutInsets,
                                statusBarTop = statusBarTop,
                                effectiveRoute = effectiveRoute,
                                titleRes = currentTitleRes,
                                accountImageUrl = accountImageUrl,
                                accountName = sidebarAccountName,
                                navController = navController,
                                onAccountClick = onAccountClick,
                            )
                        },
                    bottomBar = {
                        // Keep player and navigation outside Scaffold's shrink animation.
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                        .iosSheetPresentation(scaffoldPresentation, statusBarTop)
                ) {
                    // The top bar overlays content, and screens already apply their own
                    // top inset. Scaffold padding here would add that clearance twice.
                    // Keep the rail's playlist covers and NavHost destinations in one
                    // shared-transition tree so sidebar entry points can morph too.
                    SharedTransitionLayout(Modifier.fillMaxSize()) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this@SharedTransitionLayout,
                        com.convx.music.ui.utils.LocalNavigationArtwork provides navigationArtwork,
                    ) {
                    Box(Modifier.fillMaxSize()) {
                        val onRailItemClick = onNavItemClick
                        com.convx.music.ui.component.AnalysisBanner(
                            Modifier.align(Alignment.TopCenter).zIndex(10f),
                        )

                        val onRailSearchLongClick: () -> Unit = remember(navController) {
                            {
                                navController.navigate("recognition") {
                                    launchSingleTop = true
                                }
                            }
                        }

                        Box(Modifier.fillMaxSize()) {
                            // Keep shared backdrop capture and transitions around navigation content.
                            Box(
                                modifier = Modifier
                                    // Keep an opaque background under both screens during predictive-back fades.
                                    .background(AppTheme.colorScheme.background)
                                    // Settings pages provide their own Haze source. Nested sources are ignored by Haze.
                                    .then(
                                        if ((directTopBlur && shouldShowTopBar) || currentRoute == "settings" || currentRoute?.startsWith("settings/") == true) {
                                            Modifier
                                        } else {
                                            Modifier.hazeSource(topBarHazeState)
                                        },
                                    )
                                    // WebViews render on a separate hardware canvas and flicker when
                                    // recorded into the backdrop layer. Skip recording when WebView
                                    // routes are active or glass is unavailable or unused.
                                    .then(
                                        if (DIAG_DISABLE_BACKDROP ||
                                            AppRoutePolicy.isWebViewRoute(currentRoute) ||
                                            !glassAllowed ||
                                            // The direct top blur samples it too (instead
                                            // of a separate Haze recording of this subtree).
                                            !((glassEffectConfig.anyComponentEnabled && backdropHasConsumer) ||
                                                (directTopBlur && shouldShowTopBar))
                                        ) {
                                            Modifier
                                        } else {
                                            // Keep one backdrop source. Extra full-screen layers force
                                            // Compose to redraw the NavHost during scroll, even when
                                            // backdrop recording is frozen.
                                            Modifier.layerBackdrop(
                                                appBackdrop,
                                                frozen = backdropFrozenProvider,
                                            )
                                        }
                                    )
                                    .nestedScroll(backdropFreezeConnection)
                                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                                    .nestedScroll(floatingNavBarScrollConnection)
                            ) {
                                AppNavHost(
                                navController = navController,
                                startDestination = initialMainTabRoute,
                                scrollBehavior = topAppBarScrollBehavior,
                                activity = activity,
                                snackbarHostState = snackbarHostState,
                                accountImageUrl = accountImageUrl,
                                accountName = sidebarAccountName,
                                onAccountClick = onAccountClick,
                                )
                            }
                        }

                        com.convx.music.ui.component.RingtoneTrimmerDialog(
                            isVisible = ringtoneUiState.showTrimmer,
                            songId = ringtoneUiState.targetSongId,
                            songTitle = ringtoneUiState.targetSongTitle,
                            duration = ringtoneUiState.targetSongDuration,
                            onDismiss = { ringtoneViewModel.hideTrimmer() },
                            onResolveStreamUrl = { ringtoneViewModel.getStreamUrl(activity, it) },
                            onConfirm = { start, end -> ringtoneViewModel.setAsRingtone(activity, start, end) }
                        )

                        if (ringtoneUiState.showProgress) {
                            com.convx.music.ui.component.RingtoneProgressDialog(
                                isVisible = ringtoneUiState.showProgress,
                                progress = ringtoneUiState.progress,
                                statusMessage = ringtoneUiState.statusMessage,
                                isComplete = ringtoneUiState.isComplete,
                                isSuccess = ringtoneUiState.isSuccess,
                                onDismiss = { ringtoneViewModel.dismissProgress() },
                                onOpenSettings = { ringtoneViewModel.openRingtoneSettings(activity) }
                            )
                        }

                        com.convx.music.ui.component.DonationPromptHost()
                        // Keep full-screen tools clear of the rail overlay.

                        if (showRail && !isSettingsRoute && currentRoute != "ambient_mode") {
                            // Each screen owns its back button; a global one would duplicate it.
                            // Sidebar sections include phone top-bar items and playlists with bookmarkedAt set.
                            val sidebarSections = rememberSideBarSections(
                                database, showHistoryButton, showStatsButton,
                                listenTogetherInTopBar, navController, onLibraryFilterChange,
                            )

                            val railItems = listOf(Screens.Search, Screens.Home) +
                                navigationItems.filter { it == Screens.Songs } +
                                listOf(Screens.Explore) +
                                navigationItems.filter { it == Screens.Radio } +
                                listOf(Screens.Library)
                            val visibleRailItems = railItems.filter { it in floatingNavigationItems }
                            val onRailClick: (Screens, Boolean) -> Unit = { screen, selected ->
                                if (screen == Screens.Library) {
                                    onLibraryFilterChange(com.convx.music.constants.LibraryFilter.LIBRARY)
                                }
                                onRailItemClick(screen, selected)
                            }

                            // Side panel and top pill are one shared element: tab labels and the glass
                            // container glide between the two layouts instead of cross-fading.
                                AnimatedContent(
                                    targetState = topNavigation && !topNavMenuOpen,
                                    modifier = Modifier.fillMaxSize(),
                                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                                    label = "navMorph",
                                ) { asPill ->
                                    CompositionLocalProvider(
                                        com.convx.music.ui.utils.LocalNavAnimatedVisibilityScope provides this,
                                    ) {
                                    val morph = NavMorph(this@SharedTransitionLayout, this)
                                    Box(Modifier.fillMaxSize()) {
                                        if (asPill) {
                                            AppTabViewTopPill(
                                                navigationItems = visibleRailItems,
                                                currentRoute = effectiveRoute,
                                                onItemClick = onRailClick,
                                                onOpenSidebar = { topNavMenuOpen = true },
                                                pureBlack = pureBlack,
                                                morph = morph,
                                                modifier = Modifier
                                                    .align(Alignment.TopCenter)
                                                    .windowInsetsPadding(windowsInsets.only(WindowInsetsSides.Top))
                                                    .padding(top = 6.dp),
                                            )
                                        } else {
                                            AppFloatingSideBar(
                                                navigationItems = visibleRailItems,
                                                currentRoute = effectiveRoute,
                                                onItemClick = onRailClick,
                                                sections = sidebarSections,
                                                pureBlack = pureBlack,
                                                collapsed = sideBarCollapsed && !topNavigation,
                                                onToggleCollapsed = {
                                                    if (topNavigation) topNavMenuOpen = false
                                                    else onSideBarCollapsedChange(!sideBarCollapsed)
                                                },
                                                onEditClick = { navController.navigate("settings/appearance") },
                                                puckColorIsDefault = glassConfigState.puckColorIsDefault,
                                                footer = { footerCollapsed ->
                                                    SideBarAccountRow(
                                                        accountImageUrl = accountImageUrl,
                                                        accountName = sidebarAccountName,
                                                        collapsed = footerCollapsed,
                                                        onClick = onAccountClick,
                                                    )
                                                },
                                                morph = morph,
                                                modifier = Modifier
                                                    .align(Alignment.CenterStart)
                                                    .windowInsetsPadding(
                                                        windowsInsets.only(WindowInsetsSides.Vertical)
                                                    )
                                                    .padding(start = SideBarMargin, top = 12.dp, bottom = 12.dp),
                                            )
                                        }
                                    }
                                    }
                                }

                            // In rail mode, dock the mini player at the right edge.
                            // Show the search field there while the user is searching.
                            if (playerMediaMetadata != null || inSearchScreen) {
                                // Center in the remaining content area after the sidebar inset.
                                BoxWithConstraints(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(start = sideBarContentInset),
                                ) {
                                    val pillWidth = maxWidth * FloatingMiniPlayerWidthFraction
                                    AnimatedContent(
                                        targetState = inSearchScreen,
                                        transitionSpec = {
                                            if (targetState) {
                                                (slideInVertically(
                                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                                                ) { it / 2 } + fadeIn()) togetherWith
                                                    (slideOutVertically(
                                                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                                                    ) { it } + fadeOut())
                                            } else {
                                                (slideInVertically(
                                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                                                ) { it } + fadeIn()) togetherWith
                                                    (slideOutVertically(
                                                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
                                                    ) { it / 2 } + fadeOut())
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .windowInsetsPadding(
                                                windowsInsets.only(WindowInsetsSides.Bottom)
                                            )
                                            .padding(bottom = 12.dp),
                                        label = "tabViewSearchPill",
                                    ) { searching ->
                                        if (searching) {
                                            NavBarSearchInputBar(
                                                state = navSearchState,
                                                pureBlack = pureBlack,
                                                modifier = Modifier.width(pillWidth),
                                            )
                                        } else if (playerMediaMetadata != null) {
                                            AppFloatingNowPlayingPill(
                                                onClick = { playerBottomSheetState.expandSoft() },
                                                onLyricsClick = {
                                                    playerBottomSheetState.expandSoft()
                                                    playerConnection?.requestShowLyrics?.value = true
                                                },
                                                onQueueClick = {
                                                    playerBottomSheetState.expandSoft()
                                                    playerConnection?.requestShowQueue?.value = true
                                                },
                                                pureBlack = pureBlack,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                    }
                }

                // Overlay the player and navigation above the Scaffold. Only menu sheets
                // shrink this layer; player expansion is handled inside it.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .iosSheetPresentation(playerLayerPresentation, statusBarTop)
                ) {
                    if (!showRail && !showSettingDialoge && currentRoute?.startsWith("settings/") != true && AppRoutePolicy.showsPlayer(currentRoute)) {
                        Box(Modifier.fillMaxSize()) {
                            // Apple Music-style progressive scrim
                            if (appleMusicUi && isGlassAllowed()) {
                                val chromeBackground = if (LocalAmbientDarkChrome.current.value) {
                                    LocalAmbientBackgroundColor.current.value
                                } else baseBg
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(navBarTotalHeight + 56.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                0f to Color.Transparent,
                                                0.35f to chromeBackground.copy(alpha = 0.15f),
                                                0.6f to chromeBackground.copy(alpha = 0.4f),
                                                0.85f to chromeBackground.copy(alpha = 0.7f),
                                                1f to chromeBackground,
                                            )
                                        )
                                )
                            }

                            if (pooledGlass != null) {
                                // Draws nothing: it only keeps the shared blur current,
                                // and must run after the content records and before the
                                // chrome samples it.
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .glassPool(
                                            source = appBackdrop,
                                            pooled = glassPoolBackdrop,
                                            effects = poolEffects,
                                            scale = GlassPoolScale,
                                        )
                                )
                            }

                            Box(
                                modifier = Modifier.zIndex(if (playerAboveBars) 1f else 0f),
                            ) {
                                ForcedDarkScope {
                                    CompositionLocalProvider(com.convx.music.ui.player.LocalPlayerBackdropHolder provides playerBackdropHolder) {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )
                                    }
                                }
                            }

                            // Album, artist and playlist routes keep the floating nav bar and its mini player in the dark palette.
                val darkRouteActive = com.convx.music.ui.theme.isForcedDarkRoute(currentRoute)
                // Keep glass chrome outside its captured content to avoid a recursive RenderNode draw.
                            ForcedDarkScope(enabled = darkRouteActive) {
                            CompositionLocalProvider(LocalPooledGlass provides pooledGlass) {
                                val menuState = LocalMenuState.current
                                val bottomSheetPageState = LocalBottomSheetPageState.current
                                AppFloatingNavBar(
                                    navigationItems = floatingNavigationItems,
                                    currentRoute = effectiveRoute,
                                    onItemClick = onNavItemClick,
                                    scrollConnection = floatingNavBarScrollConnection,
                                    pureBlack = pureBlack,
                                    showPlayerAccessory = hasDockedPlayerAccessory,
                                    onAccessoryClick = { playerBottomSheetState.expandSoft() },
                                    onAccessoryLyricsClick = {
                                        playerBottomSheetState.expandSoft()
                                        playerConnection?.requestShowLyrics?.value = true
                                    },
                                    onAccessoryQueueClick = {
                                        playerBottomSheetState.expandSoft()
                                        playerConnection?.requestShowQueue?.value = true
                                    },
                                    // Long-press the mini bar for the same quick-actions menu a
                                    // long-pressed row opens elsewhere (reference: menu pop up.jpg).
                                    onAccessoryLongClick = {
                                        val metadata = playerConnection?.mediaMetadata?.value
                                        if (metadata != null) {
                                            menuState.show {
                                                PlayerMenu(
                                                    mediaMetadata = metadata,
                                                    navController = navController,
                                                    playerBottomSheetState = playerBottomSheetState,
                                                    onShowDetailsDialog = {
                                                        bottomSheetPageState.showFloating { ShowMediaInfo(metadata.id) }
                                                    },
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        }
                                    },
                                    tabContextMenu = { screen -> navTabContextMenu(screen, navController) },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .widthIn(max = 500.dp)
                                        .padding(horizontal = AppleTokens.Gutter)
                                        .padding(bottom = bottomInset + 8.dp)
                                        .graphicsLayer {
                                            val hiddenOffset =
                                                size.height + (bottomInset + 8.dp).toPx()
                                            val navBarHeightPx = navigationBarHeight.toPx()
                                            translationY = if (navBarHeightPx == 0f) {
                                                hiddenOffset
                                            } else {
                                                hiddenOffset * (1 - navBarHeightPx / NavigationBarHeight.toPx())
                                            }

                                        }
                                )
                            }
                            }
                        }
                    } else {
                        if (AppRoutePolicy.showsBottomSheetPlayer(currentRoute) && !isSettingsRoute) {
                            ForcedDarkScope {
                                CompositionLocalProvider(com.convx.music.ui.player.LocalPlayerBackdropHolder provides playerBackdropHolder) {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(bottomInsetDp)
                                .graphicsLayer {
                                    val progress = playerBottomSheetState.progress
                                    alpha = if (progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f
                                }
                                .background(baseBg)
                        )
                    }
                }
            } // Box end

                com.convx.music.core.updater.FloatingUpdaterHost(
                    visible = currentRoute == "home" && playerBottomSheetState.progress == 0f &&
                        !LocalMenuState.current.isVisible && !LocalBottomSheetPageState.current.isVisible,
                    backdrop = menuBackdrop,
                    onDetails = { navController.navigate("update") { launchSingleTop = true } },
                )

            // menuBackdrop records the entire preceding Box, including the player.
            // Keep the same glass material over the expanded player as over Home.
                // Menus on album, artist, playlist and player surfaces use the dark palette too.
                val menuDark = com.convx.music.ui.theme.isForcedDarkRoute(currentRoute) || currentRoute?.startsWith("search") == true ||
                    playerBottomSheetState.progress > 0f
                ForcedDarkScope(enabled = menuDark) {
                OverlayMenu(
                    state = LocalMenuState.current,
                    glass = true,
                    backdrop = menuSourceBackdrop,
                    liquid = true,
                )
                }

                ForcedDarkScope(enabled = menuDark) {
                BottomSheetPage(
                    state = LocalBottomSheetPageState.current,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    liveBackdrop = menuSourceBackdrop,
                    liquidGlassPlayerMode = playerBackgroundStyle == PlayerBackgroundStyle.GLASS,
                )
                }

                // Show join requests at the app root; the host may be on another screen.
                ListenTogetherOverlay(
                    manager = LocalListenTogetherManager.current,
                    playerConnection = playerConnection,
                )

                // Queue actions share one entry point, so host the conflict prompt here.
                QueueConflictDialog(playerConnection = playerConnection)

                // Player design and Lyrics style open as full-screen page sheets from anywhere.
                com.convx.music.ui.screens.settings.SettingsSheetHost(navController)
                QueueReplacedUndoSnackbar(
                    playerConnection = playerConnection,
                    snackbarHostState = snackbarHostState,
                )

                sharedSong?.let { song ->
                    playerConnection?.let {
                        Dialog(
                            onDismissRequest = { sharedSong = null },
                            properties = DialogProperties(usePlatformDefaultWidth = false),
                        ) {
                            Surface(
                                modifier = Modifier.padding(24.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = AlertDialogDefaults.containerColor,
                                tonalElevation = AlertDialogDefaults.TonalElevation,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    YouTubeSongMenu(
                                        song = song,
                                        navController = navController,
                                        onDismiss = { sharedSong = null },
                                    )
                                }
                            }
                        }
                    }
                }

                if (showSettingDialoge) {
                    SettingDialoge(
                        onDismissRequest = { showSettingDialoge = false },
                        onNavigate = { route ->
                            showSettingDialoge = false
                            navController.navigateToAppRoute(route)
                        },
                    )
                }
                com.convx.music.ui.player.FullscreenVideoOverlay(fullscreenVideo)
            }
        }
    }
}
