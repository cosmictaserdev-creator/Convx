/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.nexamusic.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.os.IBinder
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.nexamusic.app.ui.component.backdrop.backdrops.layerBackdrop
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberBackdropFreeze
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberNavTransitionFreeze
import com.nexamusic.app.ui.component.backdrop.backdrops.rememberPagerTransitionFreeze
import com.nexamusic.app.ui.screens.search.SearchScreen
import com.nexamusic.app.ui.utils.Motion
import com.nexamusic.app.ui.utils.LocalSharedTransitionScope
import com.nexamusic.app.ui.utils.markSharedArtworkSource
import com.nexamusic.app.ui.utils.markMorphDirection
import com.nexamusic.app.ui.utils.morphArtworkId
import com.nexamusic.app.ui.utils.isMorphRoute
import com.nexamusic.app.ui.component.prewarmHeroTint
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.nexamusic.app.constants.AppBarHeight
import com.nexamusic.app.constants.AppLanguageKey
import com.nexamusic.app.constants.DarkModeKey
import com.nexamusic.app.constants.DefaultOpenTabKey
import com.nexamusic.app.constants.DisableScreenshotKey
import com.nexamusic.app.constants.DynamicThemeKey
import com.nexamusic.app.constants.SearchSource
import com.nexamusic.app.constants.LocalOnlyModeKey
import com.nexamusic.app.constants.SearchSourceKey
import com.nexamusic.app.constants.EnableHighRefreshRateKey
import com.nexamusic.app.constants.EnableSettingsPopupKey
import com.nexamusic.app.constants.ListenTogetherInTopBarKey
import com.nexamusic.app.constants.ListenTogetherUsernameKey
import com.nexamusic.app.constants.MiniPlayerBottomSpacing
import com.nexamusic.app.constants.MiniPlayerHeight
import com.nexamusic.app.constants.DockedAccessoryHeight
import com.nexamusic.app.constants.NavigationBarAnimationSpec
import com.nexamusic.app.constants.NavigationBarHeight
import com.nexamusic.app.constants.OverlayMenuStyleKey
import com.nexamusic.app.constants.PauseListenHistoryKey
import com.nexamusic.app.constants.LiquidGlassGlobalEnabledKey
import com.nexamusic.app.constants.LiquidGlassPlayerEnabledKey
import com.nexamusic.app.constants.LiquidGlassMiniPlayerEnabledKey
import com.nexamusic.app.constants.LiquidGlassNavBarEnabledKey
import com.nexamusic.app.constants.LiquidGlassSidePanelEnabledKey
import com.nexamusic.app.constants.LiquidGlassSidePanelVibrancyKey
import com.nexamusic.app.constants.LiquidGlassSidePanelBlurRadiusKey
import com.nexamusic.app.constants.LiquidGlassSidePanelLensHeightKey
import com.nexamusic.app.constants.LiquidGlassSidePanelLensAmountKey
import com.nexamusic.app.constants.LiquidGlassSidePanelColorKey
import com.nexamusic.app.constants.LiquidGlassSidePanelSurfaceOpacityKey
import com.nexamusic.app.constants.LiquidGlassSidePanelTextColorKey
import com.nexamusic.app.constants.SideBarCollapsedKey
import com.nexamusic.app.constants.LiquidGlassVibrancyKey
import com.nexamusic.app.constants.LiquidGlassBlurRadiusKey
import com.nexamusic.app.constants.LiquidGlassLensHeightKey
import com.nexamusic.app.constants.LiquidGlassLensAmountKey
import com.nexamusic.app.constants.LiquidGlassChromaticAberrationKey
import com.nexamusic.app.constants.LiquidGlassDepthEffectKey
import com.nexamusic.app.constants.LiquidGlassSurfaceTintColorKey
import com.nexamusic.app.constants.LiquidGlassPuckColorKey
import com.nexamusic.app.constants.LiquidGlassPuckOpacityKey
import com.nexamusic.app.constants.LiquidGlassStyleKey
import com.nexamusic.app.constants.LiquidGlassHighlightColorKey
import com.nexamusic.app.constants.LiquidGlassHighlightOpacityKey
import com.nexamusic.app.constants.LiquidGlassSurfaceOpacityKey
import com.nexamusic.app.constants.LiquidGlassAdaptiveContrastKey
import com.nexamusic.app.constants.LiquidGlassTextColorKey
import com.nexamusic.app.constants.ShowHistoryButtonKey
import com.nexamusic.app.constants.ShowStatsButtonKey
import com.nexamusic.app.constants.AppleMusicUiKey
import com.nexamusic.app.constants.PauseSearchHistoryKey
import com.nexamusic.app.constants.AppBackgroundColorKey
import com.nexamusic.app.constants.PureBlackKey
import com.nexamusic.app.constants.SYSTEM_DEFAULT
import com.nexamusic.app.constants.SelectedThemeColorKey
import com.nexamusic.app.constants.SlimNavBarHeight
import com.nexamusic.app.constants.ForceTabletLayoutKey
import com.nexamusic.app.constants.PlaylistSortType
import com.nexamusic.app.constants.SlimNavBarKey
import com.nexamusic.app.constants.StopMusicOnTaskClearKey
import com.nexamusic.app.constants.UseNewMiniPlayerDesignKey
import com.nexamusic.app.db.MusicDatabase
import com.nexamusic.app.db.entities.SearchHistory
import com.nexamusic.app.extensions.toEnum
import com.nexamusic.app.models.toMediaMetadata
import com.nexamusic.app.playback.DownloadUtil
import com.nexamusic.app.playback.MusicService
import com.nexamusic.app.playback.MusicService.MusicBinder
import com.nexamusic.app.playback.PlayerConnection
import com.nexamusic.app.playback.queues.YouTubeQueue
import com.nexamusic.app.ui.component.AppFloatingNavBar
import com.nexamusic.app.ui.component.LocalDownloads
import com.nexamusic.app.ui.component.LocalItemPrefs
import com.nexamusic.app.ui.component.LocalNavSearchState
import com.nexamusic.app.ui.component.rememberAppBackgroundTint
import com.nexamusic.app.ui.component.rememberItemPrefs
import com.nexamusic.app.ui.component.NavSearchState
import com.nexamusic.app.ui.component.GlassEffectConfig
import com.nexamusic.app.ui.component.LocalGlassEffectConfig
import com.nexamusic.app.ui.component.LocalAppBackdrop
import com.nexamusic.app.ui.component.GlassStyle
import com.nexamusic.app.ui.component.glassContentColorFor
import com.nexamusic.app.ui.component.LocalAppleMusicUi
import com.nexamusic.app.ui.component.isGlassAllowed
import com.nexamusic.app.ui.component.AppFloatingNowPlayingPill
import com.nexamusic.app.ui.component.FloatingMiniPlayerWidthFraction
import com.nexamusic.app.ui.component.NavBarSearchInputBar
import com.nexamusic.app.ui.component.GlassCircleButton
import com.nexamusic.app.ui.component.AppFloatingSideBar
import com.nexamusic.app.ui.component.SideBarAccountRow
import com.nexamusic.app.ui.component.SideBarContentInset
import com.nexamusic.app.ui.component.SideBarMargin
import com.nexamusic.app.ui.component.SideBarCollapsedWidth
import com.nexamusic.app.ui.component.SideBarLink
import com.nexamusic.app.ui.component.SideBarSection
import com.nexamusic.app.ui.component.TabletWidthThreshold
import com.nexamusic.app.ui.component.AppNavigationRail
import com.nexamusic.app.ui.component.BottomSheetMenu
import com.nexamusic.app.ui.component.OverlayMenu
import com.nexamusic.app.ui.component.BottomSheetPage
import com.nexamusic.app.ui.component.ListenTogetherOverlay
import com.nexamusic.app.ui.component.LocalBottomSheetPageState
import com.nexamusic.app.ui.component.LocalMenuState
import com.nexamusic.app.ui.component.rememberBottomSheetState
import com.nexamusic.app.ui.component.shimmer.ShimmerTheme
import com.nexamusic.app.ui.menu.YouTubeSongMenu
import com.nexamusic.app.ui.player.BottomSheetPlayer
import com.nexamusic.app.ui.player.InstallPlayerMorphLayers
import com.nexamusic.app.ui.player.PlayerMorph
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import com.nexamusic.app.ui.screens.Screens
import com.nexamusic.app.ui.screens.SettingDialoge
import com.nexamusic.app.ui.screens.navigationBuilder
import com.nexamusic.app.ui.screens.settings.DarkMode
import com.nexamusic.app.ui.screens.settings.NavigationTab
import com.nexamusic.app.ui.theme.ColorSaver
import com.nexamusic.app.ui.theme.DefaultThemeColor
import com.nexamusic.app.ui.theme.BrandName
import com.nexamusic.app.ui.theme.LocalAccentTextColor
import com.nexamusic.app.ui.theme.rememberBrandFontFamily
import com.nexamusic.app.ui.theme.extractThemeColor
import com.nexamusic.app.ui.theme.extractThemeColorFromVideoFrame
import com.nexamusic.app.ui.theme.vivimusicTheme
import com.nexamusic.app.ui.utils.appBarScrollBehavior
import com.nexamusic.app.ui.utils.resetHeightOffset
import com.nexamusic.app.utils.SyncUtils
import com.nexamusic.app.utils.dataStore
import com.nexamusic.app.utils.get
import com.nexamusic.app.utils.rememberEnumPreference
import com.nexamusic.app.constants.CanvasSource
import com.nexamusic.app.constants.CanvasSourceKey
import com.nexamusic.app.ui.player.CanvasArtworkPlaybackCache
import com.nexamusic.app.utils.rememberPreference
import com.nexamusic.app.constants.IosOverscrollKey
import com.nexamusic.app.ui.utils.rememberIosOverscrollFactory
import com.nexamusic.app.utils.reportException
import com.nexamusic.app.utils.setAppLocale
import com.nexamusic.app.viewmodels.HistoryViewModel
import com.nexamusic.app.ui.component.floatingtabbar.rememberFloatingTabBarScrollConnection
import com.nexamusic.app.viewmodels.HomeViewModel
import com.nexamusic.app.vivimusic.UpdateNotificationHelper
import com.nexamusic.app.vivimusic.updater.checkForUpdate
import com.nexamusic.app.vivimusic.updater.getAutoUpdateCheckSetting
import com.nexamusic.app.vivimusic.updater.getUpdateNotificationsSetting
import com.nexamusic.app.vivimusic.updater.saveUpdateAvailableState
import com.valentinilk.shimmer.LocalShimmerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@Suppress("DEPRECATION", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
/**
 * Dynamic-theme colors keyed by song id: [extractThemeColor] decodes + runs
 * Palette on the full artwork for every song, so a bounded cache skips the
 * repeat work when a song recurs in a session.
 */
private val themeColorCache = android.util.LruCache<String, androidx.compose.ui.graphics.Color>(64)

// DIAGNOSTIC ONLY â€” keep false. Bypasses the full-screen layerBackdrop pass so the cost of
// the glass pipeline can be measured against the cost of the content itself. Glass surfaces
// fall back to their translucent look while this is true, so it must never ship enabled.
// Measured 2026-08-05 on SM-M346B, Home scroll p50: true => record 1.9ms / issue 4.6ms,
// false => record 16.9ms / issue 19.7ms. The gap is the forced full-tree re-record in
// LayerBackdropNode.draw(); the fix is to give the subtree RenderNodes, not to drop glass.
private val TabRootRoutes = setOf(Screens.Home.route, Screens.Library.route, Screens.Settings.route)

private const val DIAG_DISABLE_BACKDROP = false

/**
 * Routes whose whole screen is an `AndroidView` WebView. They opt out of the app
 * backdrop capture â€” see the NavHost modifier for why.
 */
private val WebViewRoutes = setOf(
    "login",
    "switch_channel",
    "settings/spotify",
    "settings/discord/login",
)

/**
 * Drives whether the top bar's blur/scrim strip is drawn at all: shown while the
 * list is at the top and while the user is scrolling back up, hidden once they
 * scroll down into the content. Without this the darkened strip sits over every
 * screen permanently.
 *
 * MainActivity doesn't own any screen's list state â€” the nested scroll stream is
 * the only app-level scroll signal available here, so direction is tracked from
 * the deltas rather than read off a LazyListState.
 */
@Stable
private class TopBarChromeVisibility {
    var visible by mutableStateOf(true)
        private set

    /** True while the content is against its top edge. */
    private var atTop = true

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Finger moving down (positive y) scrolls the content back up.
            val dy = available.y
            if (dy > ScrollSlopPx) {
                visible = true
            } else if (dy < -ScrollSlopPx && !atTop) {
                visible = false
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            // Leftover downward delta the list could not consume means it is
            // already pinned at its top edge.
            if (available.y > 0f) {
                atTop = true
                visible = true
            } else if (consumed.y != 0f) {
                atTop = false
            }
            return Offset.Zero
        }
    }

    private companion object {
        /** Ignore sub-pixel jitter so the strip doesn't flicker while settling. */
        const val ScrollSlopPx = 1.5f
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val ACTION_SEARCH = "com.nexamusic.app.action.SEARCH"
        private const val ACTION_LIBRARY = "com.nexamusic.app.action.LIBRARY"

        // Ignore a repeat bottom-nav navigate() to the same route within this
        // window, so the floating tab bar's predictive (press/drag) fire can't
        // double-navigate against its own release-fire.
        private const val NavDebounceMs = 250L

        // How long the nav bar's shrink/expand-to-pill animation takes before the
        // actual navigate()/navigateUp() call for entering/exiting search â€” keeps
        // the animation and the route's own screen transition from overlapping.
        // Comfortably past the crossfade's own ~300ms so it always finishes first.
        private const val SearchNavTransitionDelayMs = 360L
    }

    @Inject
    lateinit var database: MusicDatabase

    @Inject
    lateinit var downloadUtil: DownloadUtil

    @Inject
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var listenTogetherManager: com.nexamusic.app.listentogether.ListenTogetherManager

    private var pendingIntent: Intent? = null

    private var playerConnection by mutableStateOf<PlayerConnection?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MusicBinder) {
                try {
                    playerConnection = PlayerConnection(this@MainActivity, service, database, lifecycleScope)
                    Timber.tag("MainActivity").d("PlayerConnection created successfully")
                    // Connect Listen Together manager to player
                    listenTogetherManager.setPlayerConnection(playerConnection)
                } catch (e: Exception) {
                    Timber.tag("MainActivity").e(e, "Failed to create PlayerConnection")
                    // Retry after a delay of 500ms
                    lifecycleScope.launch {
                        delay(500.milliseconds)
                        try {
                            playerConnection = PlayerConnection(this@MainActivity, service, database, lifecycleScope)
                            listenTogetherManager.setPlayerConnection(playerConnection)
                        } catch (e2: Exception) {
                            Timber.tag("MainActivity").e(e2, "Failed to create PlayerConnection on retry")
                        }
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Disconnect Listen Together manager
            listenTogetherManager.setPlayerConnection(null)
            playerConnection?.dispose()
            playerConnection = null
        }
    }

    override fun onStart() {
        super.onStart()
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }

        // On Android 12+, we can't start foreground services from background
        // Use BIND_AUTO_CREATE which will create the service if needed
        // The service will call startForeground() in onCreate() when bound
        bindService(
            Intent(this, MusicService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Covers a deliberate back-out, where the activity really is finishing. The swipe
        // -from-recents case is handled in MusicService.onTaskRemoved instead: there
        // isFinishing is false and this branch never ran, which is why the setting looked
        // broken to everyone who used the gesture rather than the back button.
        if (dataStore.get(StopMusicOnTaskClearKey, false) &&
            playerConnection?.isPlaying?.value == true &&
            isFinishing
        ) {
            // onStop() (always called before onDestroy()) already unbound
            // serviceConnection â€” unbinding again throws IllegalArgumentException
            // ("Service not registered"), tearing the audio session down via a
            // crash instead of a clean stop.
            stopService(Intent(this, MusicService::class.java))
            playerConnection = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Live handling goes through the addOnNewIntentListener registered once
        // composition mounts. This is a safety-net capture for the (unlikely)
        // window before that listener is registered; the listener will already
        // have handled the common case, so this is otherwise inert.
        pendingIntent = intent
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Listen Together temporarily disabled — same "needs more work before shipping"
        // treatment PlayerSettings.kt already gives DJ mixing/creative transitions. Skipping
        // initialize() keeps the client from ever connecting or reconnecting to a persisted
        // room in the background; the CompositionLocal below is also nulled so every UI entry
        // point falls back to its existing "manager not available" state instead.
        // listenTogetherManager.initialize()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val locale = dataStore[AppLanguageKey]
                ?.takeUnless { it == SYSTEM_DEFAULT }
                ?.let { Locale.forLanguageTag(it) }
                ?: Locale.getDefault()
            setAppLocale(this, locale)
        }

        lifecycleScope.launch {
            dataStore.data
                .map { it[DisableScreenshotKey] ?: false }
                .distinctUntilChanged()
                .collectLatest {
                    if (it) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
        }

        setContent {
            vivimusicApp(
                playerConnection = playerConnection,
                database = database,
                downloadUtil = downloadUtil,
                syncUtils = syncUtils,
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalLayoutApi::class,
        ExperimentalSharedTransitionApi::class,
    )
    @Composable
    private fun vivimusicApp(
        playerConnection: PlayerConnection?,
        database: MusicDatabase,
        downloadUtil: DownloadUtil,
        syncUtils: SyncUtils,
    ) {
        val enableDynamicTheme by rememberPreference(DynamicThemeKey, defaultValue = true)
        val enableHighRefreshRate by rememberPreference(EnableHighRefreshRateKey, defaultValue = true)
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if (getAutoUpdateCheckSetting(context)) {
                // Delay to not block app startup
                delay(2000L.milliseconds)
                checkForUpdate(
                    context = context,
                    onSuccess = { latestVersion, isAvailable, _, _, _, _, _, _ ->
                        val currentVersion = BuildConfig.VERSION_NAME
                        Timber.tag("UpdateCheck").d("Startup check success. Latest: $latestVersion, Current: $currentVersion, isAvailable: $isAvailable")
                        saveUpdateAvailableState(context, isAvailable)
                        
                        if (isAvailable && getUpdateNotificationsSetting(context)) {
                            Timber.tag("UpdateCheck").d("Posting update notification for $latestVersion")
                            UpdateNotificationHelper.showUpdateNotification(context, latestVersion)
                        }
                    },
                    onError = {
                        Timber.tag("UpdateCheck").e("Startup check failed")
                        // Do not clear the state on error, in case of offline launch
                    }
                )
            }
        }

        LaunchedEffect(enableHighRefreshRate) {
            val window = this@MainActivity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val layoutParams = window.attributes
                val modes = window.windowManager.defaultDisplay.supportedModes
                val currentMode = window.windowManager.defaultDisplay.mode
                if (enableHighRefreshRate) {
                    // preferredDisplayModeId = 0 means "no preference", i.e. exactly what
                    // the window already had. It does not ask for anything, so with the
                    // setting ON the app was leaving the choice to the platform, and an
                    // adaptive-refresh panel showing mostly-static content will happily
                    // settle at 60. Ask for the fastest mode explicitly instead.
                    //
                    // Restricted to modes at the current resolution: supportedModes also
                    // lists lower-resolution modes, and some devices offer their highest
                    // refresh rates only there. Switching resolution to chase a refresh
                    // rate would reflow the whole UI.
                    val fastest = modes
                        .filter {
                            it.physicalWidth == currentMode.physicalWidth &&
                                it.physicalHeight == currentMode.physicalHeight
                        }
                        .maxByOrNull { it.refreshRate }
                    layoutParams.preferredDisplayModeId = fastest?.modeId ?: 0
                } else {
                    val mode60 = modes.firstOrNull { kotlin.math.abs(it.refreshRate - 60f) < 1f }
                        ?: modes.minByOrNull { kotlin.math.abs(it.refreshRate - 60f) }

                    if (mode60 != null) {
                        layoutParams.preferredDisplayModeId = mode60.modeId
                    }
                }
                window.attributes = layoutParams
            } else {
                val params = window.attributes
                params.preferredRefreshRate = if (enableHighRefreshRate) {
                    // Same fix as the R+ branch above: 0f is "no preference", not "fastest".
                    window.windowManager.defaultDisplay.supportedRefreshRates
                        .maxOrNull()
                        ?: 0f
                } else {
                    60f
                }
                window.attributes = params
            }
        }

        val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
            if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

        LaunchedEffect(useDarkTheme) {
            setSystemBarAppearance(useDarkTheme)
        }

        val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
        val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
            pureBlackEnabled && useDarkTheme
        }

        val (selectedThemeColorInt) = rememberPreference(SelectedThemeColorKey, defaultValue = DefaultThemeColor.toArgb())
        val selectedThemeColor = Color(selectedThemeColorInt)
        val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)

        var themeColor by rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(selectedThemeColor)
        }

        LaunchedEffect(selectedThemeColor) {
            if (!enableDynamicTheme) {
                themeColor = selectedThemeColor
            }
        }

        // A guest in an owner-controlled room has every "play this" refused at
        // PlayerConnection. Refusing silently reads as the app being broken, and the
        // taps come from anywhere in the app (Home tiles, search, menus), so the
        // explanation belongs here rather than on each screen.
        LaunchedEffect(playerConnection) {
            val connection = playerConnection ?: return@LaunchedEffect
            connection.playbackBlockedByRoom.collect { blocked ->
                if (!blocked) return@collect
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.listen_together_host_controls),
                    Toast.LENGTH_SHORT,
                ).show()
                connection.playbackBlockedByRoom.value = false
            }
        }

        LaunchedEffect(playerConnection, enableDynamicTheme, selectedThemeColor) {
            val playerConnection = playerConnection
            if (!enableDynamicTheme || playerConnection == null) {
                themeColor = selectedThemeColor
                return@LaunchedEffect
            }

            playerConnection.service.currentMediaMetadata.collectLatest { song ->
                if (song?.thumbnailUrl != null) {
                    val cached = themeColorCache.get(song.id)
                    if (cached != null) {
                        themeColor = cached
                    } else withContext(Dispatchers.IO) {
                        try {
                            // Prefer a frame from the song's canvas video over the static
                            // cover art when one's already been fetched/confirmed playing
                            // (see Player.kt) â€” canvas videos are often more colorful/
                            // representative than the plain album art.
                            val canvasVideoUrl = CanvasArtworkPlaybackCache
                                .get("${song.id}:${canvasSource.name}")
                                ?.preferredAnimationUrl

                            val extracted = canvasVideoUrl?.let { extractThemeColorFromVideoFrame(it) }
                                ?: imageLoader.execute(
                                    ImageRequest.Builder(this@MainActivity)
                                        .data(song.thumbnailUrl)
                                        .allowHardware(false)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .networkCachePolicy(CachePolicy.ENABLED)
                                        .crossfade(false)
                                        .build()
                                ).image?.toBitmap()?.extractThemeColor()

                            if (extracted != null) {
                                themeColorCache.put(song.id, extracted)
                                themeColor = extracted
                            } else {
                                themeColor = selectedThemeColor
                            }
                        } catch (e: Exception) {
                            // Fallback to default on error
                            themeColor = selectedThemeColor
                        }
                    }
                } else {
                    themeColor = selectedThemeColor
                }
            }
        }

        vivimusicTheme(
            darkTheme = useDarkTheme,
            pureBlack = pureBlack,
            themeColor = themeColor,
        ) {
            val (appBackgroundColorInt) = rememberPreference(AppBackgroundColorKey, defaultValue = 0)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    // The base canvas behind every screen, tab-view's side panel and
                    // main content pane included — HomeImageBackground and similar
                    // per-screen overlays paint on top of this, but this is what shows
                    // through wherever they don't, so it must follow the user's global
                    // background color too, not just pureBlack vs the raw theme surface.
                    .background(
                        when {
                            appBackgroundColorInt != 0 -> Color(appBackgroundColorInt)
                            pureBlack -> Color.Black
                            else -> MaterialTheme.colorScheme.surface
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

                // Nominates the artwork morph's source from the destination being opened,
                // rather than from each of the dozens of tap handlers that can open one.
                // The id in the route arguments IS the id of the tile that was tapped, so
                // one listener covers every screen -- including ones added later, which a
                // per-call-site approach would keep missing.
                //
                // If the tile is not on screen (deep link, restored back stack, or the
                // same id opened from somewhere with no artwork) nothing claims the source
                // key and the destination simply cross-fades in, which is the pre-existing
                // behaviour.
                DisposableEffect(navController) {
                    // -1 sentinel: the very first destination change (app launch) is
                    // neither a push nor a pop, and comparing against an unset size would
                    // otherwise read as a pop (0 < -1 is false, so it's actually safe
                    // either way, but the explicit sentinel makes that not an accident).
                    var previousBackStackSize = -1
                    val listener = NavController.OnDestinationChangedListener { controller, _, args ->
                        val currentSize = controller.currentBackStack.value.size
                        val isPop = previousBackStackSize != -1 && currentSize < previousBackStackSize
                        previousBackStackSize = currentSize
                        markMorphDirection(isPop)

                        val thumbnailUrl = markSharedArtworkSource(args.morphArtworkId())
                        // Extract the hero tint from the tile's own artwork now, while the
                        // morph is still playing, rather than when the destination first
                        // composes -- that is what makes the colour already be there as the
                        // screen finishes growing instead of washing in a beat later.
                        prewarmHeroTint(this@MainActivity, thumbnailUrl)
                    }
                    navController.addOnDestinationChangedListener(listener)
                    onDispose { navController.removeOnDestinationChangedListener(listener) }
                }

                val homeViewModel: HomeViewModel = hiltViewModel()
                // Pre-warm HistoryViewModel at Activity scope so history data loads
                // in background immediately â€” zero lag when user taps the history icon
                hiltViewModel<HistoryViewModel>()
                val accountImageUrl by homeViewModel.accountImageUrl.collectAsStateWithLifecycle()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val (previousTab, setPreviousTab) = rememberSaveable { mutableStateOf("home") }

                val (listenTogetherInTopBar) = rememberPreference(ListenTogetherInTopBarKey, defaultValue = true)
                // Read here rather than at its old spot further down: the tab row now
                // depends on it, and a second rememberPreference for the same key would
                // mean a second DataStore collector for one boolean.
                val (localOnlyMode) = rememberPreference(LocalOnlyModeKey, false)
                val navigationItems = remember(listenTogetherInTopBar, localOnlyMode) {
                    // Local-only mode adds the flat all-songs tab after Home.
                    val screens = Screens.mainScreens(localOnlyMode)
                    if (listenTogetherInTopBar) {
                        screens.filter { it != Screens.ListenTogether }
                    } else {
                        screens
                    }
                }
                val (slimNav) = rememberPreference(SlimNavBarKey, defaultValue = false)
                val (appleMusicUi) = rememberPreference(AppleMusicUiKey, defaultValue = true)
                // The Settings tab is exclusive to the floating (iOS-style) tab bar â€”
                // the classic nav bar keeps settings behind the top bar icon.
                val floatingNavigationItems = remember(navigationItems) {
                    navigationItems + Screens.Settings
                }
                val floatingNavBarScrollConnection = rememberFloatingTabBarScrollConnection()
                val (liquidGlassGlobalEnabled) = rememberPreference(LiquidGlassGlobalEnabledKey, defaultValue = true)
                val (liquidGlassVibrancy) = rememberPreference(LiquidGlassVibrancyKey, defaultValue = 1.2f)
                val (liquidGlassBlurRadius) = rememberPreference(LiquidGlassBlurRadiusKey, defaultValue = 2f)
                val (liquidGlassLensHeight) = rememberPreference(LiquidGlassLensHeightKey, defaultValue = 0.4f)
                val (liquidGlassLensAmount) = rememberPreference(LiquidGlassLensAmountKey, defaultValue = 0.6f)
                val (liquidGlassChromaticAberration) = rememberPreference(LiquidGlassChromaticAberrationKey, defaultValue = false)
                val (liquidGlassDepthEffect) = rememberPreference(LiquidGlassDepthEffectKey, defaultValue = false)
                // 0 (fully transparent, unreachable from the color picker) marks the
                // theme-adaptive default tint.
                // 0 = theme-adaptive tint (a lighter frosted grey) rather than a
                // fixed dark chip â€” the dark default read as a near-black bar (and
                // an invisible button surface over a dark hero, e.g. the playlist
                // action buttons).
                val (liquidGlassSurfaceTintColorInt) = rememberPreference(LiquidGlassSurfaceTintColorKey, defaultValue = 0)
                val (liquidGlassSurfaceOpacity) = rememberPreference(LiquidGlassSurfaceOpacityKey, defaultValue = 0.5f)
                val liquidGlassStyle by rememberEnumPreference(LiquidGlassStyleKey, defaultValue = GlassStyle.LIQUID)
                val (liquidGlassPuckColorInt) = rememberPreference(LiquidGlassPuckColorKey, defaultValue = 0)
                val (liquidGlassPuckOpacity) = rememberPreference(LiquidGlassPuckOpacityKey, defaultValue = 0.8f)
                val (liquidGlassHighlightColorInt) = rememberPreference(LiquidGlassHighlightColorKey, defaultValue = 0)
                val (liquidGlassHighlightOpacity) = rememberPreference(LiquidGlassHighlightOpacityKey, defaultValue = 0.55f)
                // 0 (fully transparent, unreachable from the color picker) marks the
                // theme-adaptive default, same convention as the surface tint above.
                // A hardcoded white default left the mini player and nav bar text
                // invisible against light-mode glass.
                val (liquidGlassTextColorInt) = rememberPreference(LiquidGlassTextColorKey, defaultValue = 0)
                val (liquidGlassAdaptiveContrast) = rememberPreference(
                    LiquidGlassAdaptiveContrastKey,
                    defaultValue = true,
                )
                // Theme-only fallback. Correct for an untinted pill, but blind to the
                // tint the user actually picked â€” a dark tint at high opacity reads
                // dark even in light mode, which is where white-on-glass disappears.
                val themeGlassTextColor = if (useDarkTheme) Color.White else Color(0xFF1A1A1A)
                val (liquidGlassPlayerEnabled) = rememberPreference(LiquidGlassPlayerEnabledKey, defaultValue = true)
                val (liquidGlassMiniPlayerEnabled) = rememberPreference(LiquidGlassMiniPlayerEnabledKey, defaultValue = true)
                val (liquidGlassNavBarEnabled) = rememberPreference(LiquidGlassNavBarEnabledKey, defaultValue = true)
                val (liquidGlassSidePanelEnabled) = rememberPreference(LiquidGlassSidePanelEnabledKey, defaultValue = true)
                val (liquidGlassSidePanelVibrancy) = rememberPreference(LiquidGlassSidePanelVibrancyKey, defaultValue = 1.2f)
                val (liquidGlassSidePanelBlurRadius) = rememberPreference(LiquidGlassSidePanelBlurRadiusKey, defaultValue = 2f)
                val (liquidGlassSidePanelLensHeight) = rememberPreference(LiquidGlassSidePanelLensHeightKey, defaultValue = 0.4f)
                val (liquidGlassSidePanelLensAmount) = rememberPreference(LiquidGlassSidePanelLensAmountKey, defaultValue = 0.6f)
                val (liquidGlassSidePanelColorInt) = rememberPreference(LiquidGlassSidePanelColorKey, defaultValue = 0)
                val (liquidGlassSidePanelSurfaceOpacity) = rememberPreference(LiquidGlassSidePanelSurfaceOpacityKey, defaultValue = 0.5f)
                val (liquidGlassSidePanelTextColorInt) = rememberPreference(LiquidGlassSidePanelTextColorKey, defaultValue = 0)

                // Content colour derived from what the pill actually composites to â€”
                // the surface behind it blended with the chosen tint at its opacity â€”
                // rather than from the theme alone. Keeps chrome legible for any
                // tint/opacity/theme combination instead of only the untinted ones.
                val resolvedGlassTint = if (liquidGlassSurfaceTintColorInt == 0) {
                    Color.Unspecified
                } else {
                    Color(liquidGlassSurfaceTintColorInt)
                }
                val resolvedSidePanelTint = if (liquidGlassSidePanelColorInt == 0) {
                    Color.Unspecified
                } else {
                    Color(liquidGlassSidePanelColorInt)
                }
                val glassSurfaceBehind = MaterialTheme.colorScheme.surface
                val adaptiveGlassTextColor = if (liquidGlassAdaptiveContrast) {
                    glassContentColorFor(glassSurfaceBehind, resolvedGlassTint, liquidGlassSurfaceOpacity)
                } else {
                    themeGlassTextColor
                }
                val adaptiveSidePanelTextColor = if (liquidGlassAdaptiveContrast) {
                    glassContentColorFor(
                        glassSurfaceBehind,
                        resolvedSidePanelTint,
                        liquidGlassSidePanelSurfaceOpacity,
                    )
                } else {
                    themeGlassTextColor
                }

                val glassEffectConfig = remember(
                    liquidGlassGlobalEnabled, liquidGlassVibrancy, liquidGlassBlurRadius,
                    liquidGlassLensHeight, liquidGlassLensAmount, liquidGlassChromaticAberration,
                    liquidGlassDepthEffect, liquidGlassSurfaceTintColorInt,
                    liquidGlassSurfaceOpacity, liquidGlassTextColorInt, liquidGlassPlayerEnabled,
                    liquidGlassHighlightColorInt, liquidGlassHighlightOpacity, liquidGlassStyle,
                    liquidGlassPuckColorInt, liquidGlassPuckOpacity,
                    liquidGlassMiniPlayerEnabled, liquidGlassNavBarEnabled, liquidGlassSidePanelEnabled,
                    liquidGlassSidePanelVibrancy, liquidGlassSidePanelBlurRadius,
                    liquidGlassSidePanelLensHeight, liquidGlassSidePanelLensAmount,
                    liquidGlassSidePanelColorInt, liquidGlassSidePanelSurfaceOpacity, liquidGlassSidePanelTextColorInt,
                    adaptiveGlassTextColor, adaptiveSidePanelTextColor,
                ) {
                    // The sliders in Glass settings are always the source of truth: the
                    // Apple Music UI toggle just writes a starting preset into them once
                    // when switched on, so the user can keep tuning from there afterward.
                    GlassEffectConfig(
                        // The glass look is part of the floating nav bar experience, so it
                        // only activates when that bar is enabled too.
                        globalEnabled = liquidGlassGlobalEnabled,
                        vibrancy = liquidGlassVibrancy,
                        blurRadius = liquidGlassBlurRadius,
                        lensHeight = liquidGlassLensHeight,
                        lensAmount = liquidGlassLensAmount,
                        chromaticAberration = liquidGlassChromaticAberration,
                        depthEffect = liquidGlassDepthEffect,
                        surfaceTintColor = if (liquidGlassSurfaceTintColorInt == 0) {
                            Color.Unspecified
                        } else {
                            Color(liquidGlassSurfaceTintColorInt)
                        },
                        surfaceOpacity = liquidGlassSurfaceOpacity,
                        highlightColor = if (liquidGlassHighlightColorInt == 0) Color.Unspecified else Color(liquidGlassHighlightColorInt),
                        highlightOpacity = liquidGlassHighlightOpacity,
                        style = liquidGlassStyle,
                        puckColor = if (liquidGlassPuckColorInt == 0) Color.Unspecified else Color(liquidGlassPuckColorInt),
                        puckOpacity = liquidGlassPuckOpacity,
                        textColor = if (liquidGlassTextColorInt == 0) adaptiveGlassTextColor else Color(liquidGlassTextColorInt),
                        playerEnabled = liquidGlassPlayerEnabled,
                        miniPlayerEnabled = liquidGlassMiniPlayerEnabled,
                        navBarEnabled = liquidGlassNavBarEnabled,
                        sidePanelEnabled = liquidGlassSidePanelEnabled,
                        sidePanelVibrancy = liquidGlassSidePanelVibrancy,
                        sidePanelBlurRadius = liquidGlassSidePanelBlurRadius,
                        sidePanelLensHeight = liquidGlassSidePanelLensHeight,
                        sidePanelLensAmount = liquidGlassSidePanelLensAmount,
                        sidePanelColor = if (liquidGlassSidePanelColorInt == 0) Color.Unspecified else Color(liquidGlassSidePanelColorInt),
                        sidePanelSurfaceOpacity = liquidGlassSidePanelSurfaceOpacity,
                        sidePanelTextColor = if (liquidGlassSidePanelTextColorInt == 0) adaptiveSidePanelTextColor else Color(liquidGlassSidePanelTextColorInt),
                    )
                }
                // API level + low-RAM gate, hoisted so the backdrop capture below can
                // be skipped entirely on devices that can never render glass.
                val glassAllowed = isGlassAllowed()

                val (useNewMiniPlayerDesign) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = true)
                val defaultOpenTab = remember {
                    dataStore[DefaultOpenTabKey].toEnum(defaultValue = NavigationTab.HOME)
                }
                val tabOpenedFromShortcut = remember {
                    when (intent?.action) {
                        ACTION_SEARCH -> NavigationTab.LIBRARY
                        ACTION_LIBRARY -> NavigationTab.SEARCH
                        else -> null
                    }
                }

                // Home/Search/Library/ListenTogether/Settings render as pages of one
                // pager (see MainTabsPager.kt) instead of five separate NavHost
                // destinations, so switching between them is a pager scroll rather
                // than a destination swap -- no AnimatedContent transition, no
                // backdrop re-record. The initial page still honors a launch
                // shortcut / the user's default-open-tab preference exactly like the
                // old startDestination selection did.
                // Search is not a NavHost destination -- opening on it means opening
                // Home and raising the search overlay over it below.
                val openSearchOnLaunch = remember {
                    (tabOpenedFromShortcut ?: defaultOpenTab) == NavigationTab.SEARCH
                }
                // NavHost's startDestination is read once at first composition, so this
                // is where "open on a specific tab" (launch shortcut / default-open-tab
                // preference) is actually decided -- see the NavHost call below.
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
                        Screens.ListenTogether.route,
                        "settings",
                    )
                }

                val (query, onQueryChange) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
                    mutableStateOf(TextFieldValue())
                }
                // Whether the nav bar's search field currently owns the keyboard â€” the
                // one thing that isn't route-derived (search_input/search/{query} don't
                // change across the first-tap/second-tap boundary). Reset below whenever
                // navigation leaves both search routes.
                var searchKeyboardActive by rememberSaveable { mutableStateOf(false) }
                var storedSearchSource by rememberEnumPreference(SearchSourceKey, SearchSource.ONLINE)
                // Local-only mode pins search to the on-device library; the stored
                // preference is left alone so it returns when the mode is turned off.
                val searchSource = if (localOnlyMode) SearchSource.LOCAL else storedSearchSource
                val searchFocusRequester = remember { FocusRequester() }
                // Non-null while entering/exiting search overrides the route-derived
                // visual state, so the shrink/expand animation plays out before the
                // actual navigation call lands (see enterSearch/exitSearch below).
                var searchVisualOverride by remember { mutableStateOf<Boolean?>(null) }

                val onSearch: (String) -> Unit = remember(localOnlyMode) {
                    { searchQuery ->
                        if (searchQuery.isNotEmpty()) {
                            // search/{query} is the YouTube results screen. In local-only
                            // mode the results are already on screen (search_input renders
                            // LocalSearchScreen live), so submitting just records history.
                            if (!localOnlyMode) navController.navigate("search/${URLEncoder.encode(searchQuery, "UTF-8")}") {
                                // No launchSingleTop: it compares destination id, not
                                // resolved args, so re-submitting a new query while
                                // already on search/{oldQuery} could get silently
                                // treated as "already there" and dropped. popUpTo
                                // below still prevents stacking a new entry per edit.
                                popUpTo("search/{query}") { inclusive = true }
                            }

                            if (dataStore[PauseSearchHistoryKey] != true) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    database.query {
                                        insert(SearchHistory(query = searchQuery))
                                    }
                                }
                            }
                        }
                    }
                }

                // Use derivedStateOf to avoid unnecessary recompositions
                val currentRoute by remember {
                    derivedStateOf { navBackStackEntry?.destination?.route }
                }
                LaunchedEffect(currentRoute) {
                    Timber.tag("Navigation").d("route -> $currentRoute")
                }

                /**
                 * Search is an overlay drawn above the pager, not one of its pages.
                 *
                 * It was a page, at index 1 -- between Home and Library -- while the bar
                 * has always drawn it as the standalone circle on the far right. Opening
                 * search therefore slid the pager sideways past a tab, and every real tab
                 * switch slid past search. Neither matched what the bar showed. Making it
                 * a page at the END instead would have cost two intervening pages'
                 * composition on the way there, so it is not a page at all: the pager is
                 * exactly the three tabs the bar draws, and search opens in place over
                 * them for zero page travel.
                 */
                var searchOverlayOpen by rememberSaveable { mutableStateOf(openSearchOnLaunch) }

                // Home/Library/Settings are real NavHost destinations again, so
                // currentRoute already IS the tab route -- no pager-page lookup needed.
                // The only thing layered on top is search, which is an overlay rather
                // than a destination, so nothing in the back stack reports it; every
                // "which tab is this" check downstream (search-mode detection, top-bar
                // visibility, the nav bar's puck) still wants to hear "we are in
                // search" while the overlay is up, so answer for it here.
                val effectiveRoute by remember {
                    derivedStateOf {
                        if (searchOverlayOpen) Screens.Search.route else currentRoute
                    }
                }

                val inSearchScreen by remember {
                    derivedStateOf {
                        currentRoute?.startsWith("search/") == true ||
                            effectiveRoute == Screens.Search.route
                    }
                }
                val inSearchInputScreen by remember {
                    derivedStateOf { effectiveRoute == Screens.Search.route }
                }
                LaunchedEffect(inSearchScreen) {
                    if (!inSearchScreen) searchKeyboardActive = false
                }
                // The floating nav bar keeps Settings as one of its own tabs (see
                // floatingNavigationItems above), so it should stay visible there too â€”
                // only the classic nav bar treats Settings as a top-bar-only destination.
                val navigationItemRoutes = remember(floatingNavigationItems) {
                    floatingNavigationItems.map { it.route }.toSet()
                }

                val isKeyboardOpen = WindowInsets.isImeVisible
                val shouldShowNavigationBar = remember(currentRoute) {
                    when {
                        // By design: nav bar shows only at Settings root ("settings"
                        // bare route); every drilled-in sub-page ("settings/...") is a
                        // focused detail/editor context and hides it, same as the other
                        // full-screen flows below. Reaching Home from a sub-page is via
                        // one back-tap to root (where the bar reappears) or the back
                        // arrow's long-press (backToMain()) shortcut.
                        currentRoute?.startsWith("settings/") == true -> false
                        currentRoute in setOf("login", "channel_picker", "equalizer", "wrapped", "update", "listen_together/chat") -> false
                        else -> true
                    }
                }

                val isLandscape = configuration.containerDpSize.width > configuration.containerDpSize.height

                // The floating liquid-glass nav bar is a centered bottom pill that
                // works the same in landscape, so don't swap it for the side rail
                // there â€” only the classic nav bar falls back to the rail.
                //
                // "Tab view" forces the same side layout on any device, including a
                // phone in portrait, so the tablet sidebar can be tried out without
                // a tablet.
                val (forceTabletLayout) = rememberPreference(ForceTabletLayoutKey, defaultValue = false)
                // A real tablet gets the side bar on its own; the toggle only exists
                // to force it onto a device that wouldn't otherwise qualify.
                val isWideScreen = configuration.containerDpSize.width >= TabletWidthThreshold
                val showRail = forceTabletLayout || isWideScreen
                val (sideBarCollapsed, onSideBarCollapsedChange) = rememberPreference(SideBarCollapsedKey, defaultValue = false)
                val sideBarContentInset by animateDpAsState(
                    targetValue = if (sideBarCollapsed) SideBarCollapsedWidth + SideBarMargin * 2 else SideBarContentInset,
                    animationSpec = spring(0.9f, 400f),
                    label = "sideBarContentInset",
                )

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
                // With the floating nav bar the mini player docks into the tab bar as an
                // accessory, and in tab view it docks into the sidebar footer, so in both
                // cases the sheet's collapsed state coincides with dismissed and the
                // standalone collapsed mini player never shows. Without this, turning tab
                // view on swapped the docked accessory for the sheet's own mini player,
                // which is a different component and reads as a different app.
                val playerBottomSheetState = rememberBottomSheetState(
                    dismissedBound = 0.dp,
                    // The mini player always docks into the tab bar (or, in tab view, the
                    // sidebar footer), so the sheet's collapsed state always coincides with
                    // dismissed and the standalone collapsed mini player never shows.
                    collapsedBound = 0.dp,
                    expandedBound = maxHeight,
                )

                // Both ends of the mini-to-full morph are recorded into GraphicsLayers
                // owned here, above both the player and the nav bar the pill docks into --
                // remembering them any lower would release them mid-gesture.
                InstallPlayerMorphLayers()

                // See the zIndex on the player below.
                val playerAboveBars by remember(playerBottomSheetState) {
                    derivedStateOf { playerBottomSheetState.progress > 0f }
                }

                // Only reserve space for the docked player accessory on screens where the
                // floating tab bar is actually visible; other screens (e.g. settings) get
                // the full height. In search mode the mini player still docks (now part of
                // the search-expanded/search-inline chrome) â€” it only drops out once the
                // keyboard takes over the bar entirely.
                val hasDockedPlayerAccessory =
                    playerMediaMetadata != null && !showRail && shouldShowNavigationBar &&
                        (!inSearchScreen || !searchKeyboardActive)
                val playerAwareWindowInsets = remember(
                    bottomInset,
                    shouldShowNavigationBar,
                    playerBottomSheetState.isDismissed,
                    showRail,
                    hasDockedPlayerAccessory,
                    sideBarContentInset,
                    inSearchInputScreen,
                ) {
                    var bottom = bottomInset
                    if (shouldShowNavigationBar && !showRail) {
                        bottom += NavigationBarHeight
                    }
                    if (!playerBottomSheetState.isDismissed && !inSearchInputScreen) bottom += MiniPlayerHeight
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

                // Shows the top bar's blur/scrim strip only at the top of a list
                // or while scrolling back up â€” see TopBarChromeVisibility.
                val topBarChrome = remember { TopBarChromeVisibility() }

                // Navigation tracking. Keyed on effectiveRoute too, not just
                // navBackStackEntry -- a pager tab switch (Home/Search/Library/
                // ListenTogether/Settings) no longer changes the real back stack
                // entry at all, so navBackStackEntry alone would miss it entirely.
                // Still keyed on navBackStackEntry as well: resubmitting a new
                // search query pushes a new entry on the SAME destination template
                // ("search/{query}"), which would not otherwise register as a change.
                LaunchedEffect(navBackStackEntry, effectiveRoute) {
                    // Only the results route (search/{query}) carries a query arg;
                    // the search_input landing does not â€” guard against a null arg
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
                    } else if (navigationItems.fastAny { it.route == effectiveRoute }) {
                        onQueryChange(TextFieldValue())
                    }

                    // Reset scroll behavior for main navigation items
                    if (navigationItems.fastAny { it.route == effectiveRoute }) {
                        if (navigationItems.fastAny { it.route == previousTab }) {
                            topAppBarScrollBehavior.state.resetHeightOffset()
                        }
                    }

                    topAppBarScrollBehavior.state.resetHeightOffset()

                    // Track previous tab for animations
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

                // Keyed on effectiveRoute (not navBackStackEntry) so this reacts to a
                // pager tab switch too -- the real NavHost entry no longer changes
                // when switching between Home/Search/Library/ListenTogether/Settings.
                LaunchedEffect(effectiveRoute, listenTogetherInTopBar, showRail) {
                    val isListenTogetherScreen = effectiveRoute == Screens.ListenTogether.route ||
                        currentRoute == "listen_together_from_topbar"
                    shouldShowTopBar = !showRail &&
                        effectiveRoute in topLevelScreens &&
                        effectiveRoute != "settings" &&
                        !(isListenTogetherScreen && listenTogetherInTopBar)
                }

                val coroutineScope = rememberCoroutineScope()
                var sharedSong: SongItem? by remember {
                    mutableStateOf(null)
                }
                val snackbarHostState = remember { SnackbarHostState() }
                var showSettingDialoge by remember { mutableStateOf(false) }
                val (enableSettingsPopup) = rememberPreference(EnableSettingsPopupKey, defaultValue = false)

                val ringtoneViewModel: com.nexamusic.app.ui.screens.settings.RingtoneViewModel = viewModel()
                val ringtoneUiState by ringtoneViewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    if (pendingIntent != null) {
                        handleDeepLinkIntent(pendingIntent!!, navController)
                        pendingIntent = null
                    } else {
                        handleDeepLinkIntent(intent, navController)
                    }
                }

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { intent ->
                        handleDeepLinkIntent(intent, navController)
                    }

                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                val currentTitleRes = remember(effectiveRoute) {
                    when (effectiveRoute) {
                        Screens.Home.route -> R.string.music
                        Screens.Search.route -> R.string.search
                        Screens.Library.route -> R.string.filter_library
                        Screens.ListenTogether.route -> R.string.together
                        else -> null
                    }
                }



                val pauseListenHistory by rememberPreference(PauseListenHistoryKey, defaultValue = false)
                val eventCount by database.eventCount().collectAsStateWithLifecycle(initialValue = 0)
                val (historyButtonEnabled) = rememberPreference(ShowHistoryButtonKey, defaultValue = true)
                val (showStatsButton) = rememberPreference(ShowStatsButtonKey, defaultValue = true)
                val showHistoryButton = remember(pauseListenHistory, eventCount, historyButtonEnabled) {
                    historyButtonEnabled && !(pauseListenHistory && eventCount == 0)
                }

                val baseBg = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer

                // Remember the draw lambda so rememberLayerBackdrop (which keys on it)
                // isn't recreated on every app recomposition, forcing a full backdrop
                // re-record.
                val appBackdrop = rememberLayerBackdrop(
                    onDraw = remember(baseBg) {
                        val bg = baseBg
                        {
                            drawRect(bg)
                            drawContent()
                        }
                    }
                )

                // While the user is scrolling, stop re-recording the backdrop source.
                // layer.record { drawContent() } cannot reuse unchanged child RenderNodes,
                // so it re-issues the entire screen every frame â€” measured at 47ms of the
                // ~57ms Home scroll frame, against 1.9ms for Compose's ordinary draw path.
                // The content keeps scrolling live; only the blur sampled by the nav/top
                // bar holds its last capture until the gesture settles.
                // The frozen flag is a PLAIN timestamp, deliberately not snapshot state: the
                // provider below is called during the draw phase, so a snapshot read there
                // registers a draw dependency and every write re-invalidates the frame. A
                // first attempt did exactly that and left the app redrawing forever after
                // any scroll (266 frames/7s at rest). Same trap as LayerBackdrop.contentVersion.
                val backdropFreeze = rememberBackdropFreeze()
                val backdropFreezeConnection = backdropFreeze.connection
                // A plain nav transition (slide+fade between two screens) is neither a
                // scroll nor a fling, so backdropFreeze never caught it — the backdrop
                // re-recorded every frame of every navigation for the whole ~200ms
                // transition, at the same per-frame cost already measured and fixed for
                // scroll. OR'd in here rather than folded into BackdropFreeze itself,
                // which many other screens also use for their own local backdrops.
                val navTransitionFreeze = rememberNavTransitionFreeze(currentRoute)
                // Home/Library/Settings are real NavHost destinations again, so a tab
                // switch IS a route change and navTransitionFreeze above already covers
                // it -- the separate pager-motion freeze this used to need doesn't
                // apply once there is no pager motion to miss.
                val backdropFrozenProvider = {
                    backdropFreeze.frozen() ||
                        navTransitionFreeze.frozen()
                }

                // One provider replaces Android's stretch/glow edge effect with the
                // iOS rubber-band for every scroll container in the app.
                val iosOverscroll by rememberPreference(IosOverscrollKey, defaultValue = true)
                val iosOverscrollFactory = rememberIosOverscrollFactory()

                // Entering/exiting search: hold the nav bar's visual state at the
                // target (search-expanded / normal) for one animation beat before the
                // actual navigation call, so the shrink/expand-to-pill animation plays
                // out first instead of racing the route's own screen transition.
                val enterSearch: () -> Unit = remember(navController, coroutineScope) {
                    {
                        searchVisualOverride = true
                        coroutineScope.launch {
                            delay(SearchNavTransitionDelayMs)
                            // Search opens over whatever tab is showing. Drilled into a
                            // detail screen, pop back to the tab root first so the overlay
                            // does not sit on top of an album page the user can no longer
                            // see the back button for.
                            if (navController.currentDestination?.route !in TabRootRoutes) {
                                navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                            }
                            searchOverlayOpen = true
                            searchVisualOverride = null
                        }
                    }
                }
                val exitSearch: () -> Unit = remember(navController, coroutineScope) {
                    {
                        searchKeyboardActive = false
                        searchVisualOverride = false
                        coroutineScope.launch {
                            delay(SearchNavTransitionDelayMs)
                            // search/{query} (the results screen) is still a real pushed
                            // NavHost destination -- leave it the normal way. The input
                            // overlay itself just closes, and whatever tab was underneath
                            // is still exactly where it was: no page to scroll back to.
                            if (navController.currentDestination?.route?.startsWith("search/") == true) {
                                navController.navigateUp()
                            }
                            searchOverlayOpen = false
                            searchVisualOverride = null
                        }
                    }
                }

                // The nav bar (rendered outside/above search_input and search/{query} in
                // the tree below) owns the actual search text field now â€” both screens
                // just read this to filter/display results.
                val navSearchState = NavSearchState(
                    visualActive = searchVisualOverride ?: inSearchScreen,
                    keyboardActive = searchKeyboardActive,
                    query = query,
                    onQueryChange = onQueryChange,
                    onSubmit = onSearch,
                    searchSource = searchSource,
                    onToggleSource = {
                        storedSearchSource =
                            if (storedSearchSource == SearchSource.ONLINE) SearchSource.LOCAL else SearchSource.ONLINE
                    },
                    canToggleSource = !localOnlyMode,
                    onTapSearchIcon = enterSearch,
                    onTapBar = {
                        if (inSearchScreen && !inSearchInputScreen) {
                            // Tapping the bar again from a results screen (search/{query})
                            // pops that screen and reopens the search overlay's keyboard
                            // instead of trying to resubmit in place.
                            if (navController.currentDestination?.route !in TabRootRoutes) {
                                navController.popBackStack(navController.graph.startDestinationId, inclusive = false)
                            }
                        }
                        searchKeyboardActive = true
                    },
                    onExit = exitSearch,
                    onCloseKeyboard = { searchKeyboardActive = false },
                    focusRequester = searchFocusRequester,
                )

                CompositionLocalProvider(
                    LocalOverscrollFactory provides
                        if (iosOverscroll) iosOverscrollFactory else LocalOverscrollFactory.current,
                    LocalDatabase provides database,
                    LocalNavSearchState provides navSearchState,
                    // onSurface already carries the accent-contrast treatment (see
                    // ColorScheme.accentText), so pure black must not force plain white
                    // back over it.
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                    LocalPlayerConnection provides playerConnection,
                    LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
                    LocalDownloadUtil provides downloadUtil,
                    LocalShimmerTheme provides ShimmerTheme,
                    LocalSyncUtils provides syncUtils,
                    // Temporarily disabled — see the initialize() comment in onCreate. Every
                    // current entry point (PlayerMenu's ListenTogetherDialog, ListenTogetherScreen)
                    // already has a null-manager fallback, so this alone turns the whole feature off.
                    LocalListenTogetherManager provides null,
                    LocalGlassEffectConfig provides glassEffectConfig,
                    LocalTabView provides showRail,
                    LocalAppBackdrop provides appBackdrop,
                    LocalAppleMusicUi provides appleMusicUi,
                    LocalRingtoneViewModel provides ringtoneViewModel,
                    // Read once here instead of inside every list row and grid tile —
                    // see ItemPrefs for what that was costing on scroll.
                    LocalItemPrefs provides rememberItemPrefs(),
                    // One collector for the whole app instead of one per list row —
                    // see LocalDownloads.
                    LocalDownloads provides downloadUtil.downloads.collectAsState().value,
                ) {

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            AnimatedVisibility(
                                visible = shouldShowTopBar,
                                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 200))
                            ) {
                                Row {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Scrim only â€” no backdrop capture. This was a blurred
                                        // capture of the screen behind it (once two stacked
                                        // captures, then one at 0.45 scale), which meant the top
                                        // bar sampled and re-blurred the full-screen backdrop on
                                        // top of everything else the scroll frame was already
                                        // paying for. The gradient below carried most of the
                                        // legibility anyway, and it fades out on its own, so
                                        // dropping the blur also drops the DstIn mask and the
                                        // Offscreen buffer that mask required.
                                        //
                                        // Plain alpha rather than AnimatedVisibility: this sits in
                                        // a Box nested in a Row, so AnimatedVisibility resolves
                                        // against RowScope and does not compile here. Animating
                                        // alpha in graphicsLayer keeps the fade off the
                                        // recomposition path anyway, and the `if` drops the
                                        // backdrop capture entirely once it is fully hidden.
                                        //
                                        // The fade is asymmetric on purpose: coming back has
                                        // to feel immediate, because it trails the scroll
                                        // that asked for it. Going away can be lazy.
                                        val chromeAlpha by animateFloatAsState(
                                            targetValue = if (topBarChrome.visible) 1f else 0f,
                                            animationSpec = tween(
                                                durationMillis = if (topBarChrome.visible) 90 else 200
                                            ),
                                            label = "topBarChromeAlpha",
                                        )
                                        // The scrim is the screen's OWN background faded
                                        // out, not a fixed black: hardcoded black read as
                                        // a dark band across the top of every light-theme
                                        // screen. Same expression the top-level screens
                                        // paint behind their content, so it honours the
                                        // picked theme colour when dynamic colour is off.
                                        val scrimColor = rememberAppBackgroundTint(
                                            MaterialTheme.colorScheme.background
                                        )
                                        if (chromeAlpha > 0.01f) {
                                            // Replaces the blur that used to sit here: one drawn
                                            // rect, no capture, no layer of its own beyond the
                                            // alpha fade. Weighted toward the top and held near
                                            // full opacity across the whole bar rather than
                                            // falling off immediately — the bar's title and icons
                                            // sit in the first two thirds, and with the blur gone
                                            // there is nothing but this gradient separating them
                                            // from whatever artwork scrolls underneath.
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(AppBarHeight * 1.9f)
                                                    .align(Alignment.TopCenter)
                                                    .graphicsLayer { alpha = chromeAlpha }
                                                    .background(
                                                        Brush.verticalGradient(
                                                            0f to scrimColor.copy(alpha = 0.82f),
                                                            0.42f to scrimColor.copy(alpha = 0.70f),
                                                            0.68f to scrimColor.copy(alpha = 0.38f),
                                                            0.86f to scrimColor.copy(alpha = 0.14f),
                                                            1f to Color.Transparent,
                                                        )
                                                    )
                                            )
                                        }
                                        TopAppBar(
                                        title = {
                                            // Home shows the wordmark; every other tab keeps
                                            // its own title.
                                            val isHome = effectiveRoute == Screens.Home.route
                                            Text(
                                                text = if (isHome) {
                                                    BrandName
                                                } else {
                                                    currentTitleRes?.let { stringResource(it) } ?: ""
                                                },
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    // Display face is for the wordmark only;
                                                    // other tab titles keep the app typeface.
                                                    fontFamily = if (isHome) rememberBrandFontFamily() else null,
                                                    fontWeight = if (isHome) FontWeight.SemiBold else FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                    letterSpacing = if (isHome) 1.5.sp else 0.sp,
                                                ),
                                                // The bar is a Scaffold slot outside the screen
                                                // content, so this is the app-wide accent text
                                                // colour, not any screen's hero tint.
                                                color = LocalAccentTextColor.current,
                                                modifier = Modifier.padding(start = 4.dp),
                                            )
                                        },
                                        actions = {
                                            // History/Stats/Together moved to Settings (see
                                            // SettingsScreen.kt's ACTIVITY section + the
                                            // existing ACCOUNT entry) â€” the top bar now
                                            // shows only the wordmark and this settings/
                                            // profile pill, per the simplified-chrome pass.
                                             IconButton(onClick = {
                                                  if (enableSettingsPopup) {
                                                      showSettingDialoge = true
                                                  } else {
                                                      navController.navigate(Screens.Settings.route) {
                                                          popUpTo(navController.graph.startDestinationId) {
                                                              saveState = true
                                                          }
                                                          launchSingleTop = true
                                                          restoreState = true
                                                      }
                                                  }
                                              }) {
                                                BadgedBox(badge = {}) {
                                                    if (accountImageUrl != null) {
                                                        AsyncImage(
                                                            model = accountImageUrl,
                                                            contentDescription = stringResource(R.string.account),
                                                            modifier = Modifier
                                                                .size(34.dp)
                                                                .clip(CircleShape)
                                                        )
                                                    } else {
                                                        val composition by rememberLottieComposition(
                                                            LottieCompositionSpec.RawRes(R.raw.setting)
                                                        )
                                                        val progress by animateLottieCompositionAsState(
                                                            composition = composition,
                                                            isPlaying = true,
                                                            iterations = 1,
                                                            speed = 1.5f
                                                        )

                                                        LottieAnimation(
                                                            composition = composition,
                                                            progress = { progress },
                                                            modifier = Modifier.size(50.dp),
                                                            contentScale = ContentScale.Fit
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        scrollBehavior = topAppBarScrollBehavior,
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            // Floating: transparent bar over the content/hero background.
                                            containerColor = Color.Transparent,
                                            scrolledContainerColor = Color.Transparent,
                                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                                            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier
                                            .windowInsetsPadding(
                                                cutoutInsets.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                                            )
                                            // The side bar now spans most of the screen
                                            // height, not the small centered capsule this
                                            // used to be — it reaches the top bar's row,
                                            // so the bar needs the same start inset every
                                            // screen's own content already gets.
                                            .padding(start = if (showRail) sideBarContentInset else 0.dp)
                                    )
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            val onNavItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState, enterSearch) {
                                var lastNavRoute: String? = null
                                var lastNavTimeMs = 0L
                                { screen: Screens, isSelected: Boolean ->
                                    if (playerBottomSheetState.isExpanded) {
                                        playerBottomSheetState.collapseSoft()
                                    }

                                    // Search is an overlay, not a NavHost destination -- the
                                    // fallback below would navigate("search_input"), a route that
                                    // doesn't exist in the graph. The tablet side bar still lists
                                    // Search as an item, so this cannot live only on the phone
                                    // bar's search circle.
                                    if (screen == Screens.Search) {
                                        enterSearch()
                                    } else if (isSelected) {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                        coroutineScope.launch {
                                            topAppBarScrollBehavior.state.resetHeightOffset()
                                        }
                                    } else {
                                        val now = SystemClock.elapsedRealtime()
                                        // The debounce below only suppresses a genuine duplicate
                                        // fire for a tab we are still sitting on. Once the user
                                        // has moved anywhere else (tapped a song, opened an
                                        // artist), the remembered route is stale â€” leaving it set
                                        // meant a tab tap within the debounce window was silently
                                        // swallowed and you stayed on the previous screen, which
                                        // is why Home sometimes did nothing.
                                        // effectiveRoute (not the raw destination route): it also
                                        // reflects the search overlay, which the raw route can't.
                                        if (effectiveRoute != lastNavRoute) {
                                            lastNavRoute = null
                                        }
                                        // Guards against a double-fire from the floating tab bar's
                                        // predictive (press/drag-threshold) nav landing on top of the
                                        // puck's own release-fire for the same target.
                                        if (screen.route != lastNavRoute || now - lastNavTimeMs >= NavDebounceMs) {
                                            lastNavRoute = screen.route
                                            lastNavTimeMs = now
                                            // Plain navigate() with the standard multi-back-stack
                                            // pattern, same as every other route.
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
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

                            // Pre-calculate values for graphicsLayer to avoid reading state during composition
                            val navBarTotalHeight = bottomInset + NavigationBarHeight

                            if (!showRail && !showSettingDialoge && currentRoute?.startsWith("settings/") != true && currentRoute !in setOf("wrapped", "update", "listen_together/chat", "login", "equalizer", "ambient_mode")) {
                                Box {
                                    // Apple Music-style progressive scrim: content fades out under
                                    // the floating glass bar instead of hard-clipping, so the bar
                                    // stays legible over bright artwork.  The horizontal gradient
                                    // on the left/right edges mimics Apple Music's edge blur.
                                    if (appleMusicUi && isGlassAllowed()) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(navBarTotalHeight + 56.dp)
                                                .background(
                                                    Brush.verticalGradient(
                                                        0f to Color.Transparent,
                                                        0.35f to baseBg.copy(alpha = 0.15f),
                                                        0.6f to baseBg.copy(alpha = 0.4f),
                                                        0.85f to baseBg.copy(alpha = 0.7f),
                                                        1f to baseBg,
                                                    )
                                                )
                                        )
                                    }

                                    Box(
                                        // Above the bars for as long as the player is anything
                                        // other than fully collapsed, so the growing container
                                        // covers them instead of the bars having to get out of
                                        // its way. Melox's own full player does exactly this.
                                        // Derived, so it flips twice a gesture rather than
                                        // recomposing per frame.
                                        modifier = Modifier.zIndex(if (playerAboveBars) 1f else 0f),
                                    ) {
                                        BottomSheetPlayer(
                                            state = playerBottomSheetState,
                                            navController = navController,
                                            pureBlack = pureBlack
                                        )
                                    }

                                    // Use graphicsLayer instead of offset to avoid recomposition
                                    // graphicsLayer runs during draw phase, not composition phase
                                    val navBarGraphicsLayer: Modifier = Modifier.graphicsLayer {
                                        val navBarHeightPx = navigationBarHeight.toPx()
                                        val totalHeightPx = navBarTotalHeight.toPx()

                                        translationY = if (navBarHeightPx == 0f) {
                                            totalHeightPx
                                        } else {
                                            // Read progress only during draw phase
                                            val progress = playerBottomSheetState.progress.coerceIn(0f, 1f)
                                            val slideOffset = totalHeightPx * progress
                                            val hideOffset = totalHeightPx * (1 - navBarHeightPx / NavigationBarHeight.toPx())
                                            slideOffset + hideOffset
                                        }

                                    }

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
                                        // tabPosition omitted (defaults to null): there is no
                                        // pager to follow now that the tabs are real NavHost
                                        // destinations again, so the puck goes back to its
                                        // normal selectedTabKey-driven spring. The parameter and
                                        // its plumbing through FloatingTabBar/FloatingNavBar are
                                        // left in place, unused, rather than torn out.
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            // Keep the bar compact/portrait-width in landscape
                                            // instead of stretching across the wide screen.
                                            .widthIn(max = 500.dp)
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = bottomInset + 8.dp)
                                            .graphicsLayer {
                                                // The floating bar (especially with the docked
                                                // player accessory) is taller than the classic
                                                // nav bar, so hide it by its own measured height
                                                // instead of the fixed nav bar height.
                                                val hiddenOffset =
                                                    size.height + (bottomInset + 8.dp).toPx()
                                                val navBarHeightPx = navigationBarHeight.toPx()
                                                // Held still for the whole gesture -- see the
                                                // classic bar above. The pill docks in here, so
                                                // a bar that slides drags the morph's source
                                                // rect along with it.
                                                translationY = if (navBarHeightPx == 0f) {
                                                    hiddenOffset
                                                } else {
                                                    hiddenOffset * (1 - navBarHeightPx / NavigationBarHeight.toPx())
                                                }

                                            }
                                    )


                                }
                            } else {
                                if (currentRoute != "wrapped" && currentRoute != "update" && currentRoute != "listen_together/chat" && currentRoute != "ambient_mode") {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .height(bottomInsetDp)
                                        // Use graphicsLayer for background color changes
                                        .graphicsLayer {
                                            val progress = playerBottomSheetState.progress
                                            alpha = if (progress > 0f || (useNewMiniPlayerDesign && !shouldShowNavigationBar)) 0f else 1f
                                        }
                                        .background(baseBg)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            val onRailItemClick: (Screens, Boolean) -> Unit = remember(navController, coroutineScope, topAppBarScrollBehavior, playerBottomSheetState, enterSearch) {
                                var lastNavRoute: String? = null
                                var lastNavTimeMs = 0L
                                { screen: Screens, isSelected: Boolean ->
                                    if (playerBottomSheetState.isExpanded) {
                                        playerBottomSheetState.collapseSoft()
                                    }

                                    // Search is an overlay, not a NavHost destination -- the
                                    // fallback below would navigate("search_input"), a route that
                                    // doesn't exist in the graph. The tablet side bar still lists
                                    // Search as an item, so this cannot live only on the phone
                                    // bar's search circle.
                                    if (screen == Screens.Search) {
                                        enterSearch()
                                    } else if (isSelected) {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                        coroutineScope.launch {
                                            topAppBarScrollBehavior.state.resetHeightOffset()
                                        }
                                    } else {
                                        val now = SystemClock.elapsedRealtime()
                                        if (screen.route != lastNavRoute || now - lastNavTimeMs >= NavDebounceMs) {
                                            lastNavRoute = screen.route
                                            lastNavTimeMs = now
                                            // Plain navigate() with the standard multi-back-stack
                                            // pattern, same as every other route.
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                            }

                            val onRailSearchLongClick: () -> Unit = remember(navController) {
                                {
                                    navController.navigate("recognition") {
                                        launchSingleTop = true
                                    }
                                }
                            }

                            Box(Modifier.fillMaxSize()) {
                                // Everything the NavHost renders sits inside one shared
                                // transition scope, so a tile's artwork on one destination
                                // and the hero on the next are two ends of the same element
                                // rather than two images that cross-fade. Destinations opt
                                // in individually -- see sharedComposable in
                                // NavigationBuilder.kt and Modifier.sharedArtwork.
                                SharedTransitionLayout {
                                CompositionLocalProvider(
                                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                                ) {
                                // Carries layerBackdrop now -- see the comment on the Search
                                // AnimatedVisibility below for why this moved off NavHost's
                                // own modifier and up to wrap both NavHost and Search.
                                Box(
                                    modifier = Modifier
                                        // Skipped on WebView routes: layerBackdrop records
                                        // this whole subtree into a GraphicsLayer so glass
                                        // can sample it, which means the subtree is drawn
                                        // twice per frame. A WebView renders on its own
                                        // hardware canvas and does not survive that second
                                        // pass intact â€” the page tears and flickers while
                                        // still being usable, exactly the reported symptom.
                                        // Those screens are plain full-bleed WebViews with
                                        // their own Material top bar, so they have no glass
                                        // to feed anyway.
                                        // Also skipped when nothing would sample the backdrop:
                                        // every glass component switched off, or a device where
                                        // glass can't render at all (pre-Android 12, or low-RAM).
                                        // Recording the screen into a GraphicsLayer costs the
                                        // same whether or not anyone reads it, and on those
                                        // devices nobody ever does â€” measured at ~25ms of
                                        // display-list recording per frame on a Galaxy M34, on
                                        // every screen including a plain settings list.
                                        .then(
                                            if (DIAG_DISABLE_BACKDROP ||
                                                currentRoute in WebViewRoutes ||
                                                !glassEffectConfig.anyComponentEnabled ||
                                                !glassAllowed
                                            ) {
                                                Modifier
                                            } else {
                                                // graphicsLayer BEFORE layerBackdrop, not
                                                // after: the layer must enclose the backdrop
                                                // node, otherwise that node's draw still
                                                // re-runs every time anything else in the
                                                // window redraws. The mini player and nav bar
                                                // are siblings of this Box, yet their
                                                // per-frame ticks were forcing a full
                                                // re-record of this subtree â€” 117ms/frame at
                                                // idle with a song playing. Defaults only, so
                                                // nothing about the rendered result changes.
                                                Modifier
                                                    // OUTER layer: isolates this subtree from
                                                    // sibling redraws (mini player waveform,
                                                    // 10Hz position poll), which were forcing
                                                    // a full re-record. 117ms -> ~48ms idle
                                                    // while playing.
                                                    .graphicsLayer()
                                                    .layerBackdrop(
                                                        appBackdrop,
                                                        frozen = backdropFrozenProvider,
                                                    )
                                                    // INNER layer: the content becomes ONE
                                                    // cached RenderNode, so the backdrop's
                                                    // layer.record { drawContent() } records a
                                                    // single drawRenderNode instead of
                                                    // re-issuing every op in the tree. Compose
                                                    // keeps that node up to date incrementally
                                                    // on its normal draw path.
                                                    .graphicsLayer()
                                            }
                                        )
                                        .nestedScroll(backdropFreezeConnection)
                                        .nestedScroll(topBarChrome.connection)
                                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                                        .nestedScroll(floatingNavBarScrollConnection)
                                ) {
                                // NavHost with animations (Material 3 Expressive style)
                                NavHost(
                                    navController = navController,
                                    // Which tab this opens on (launch shortcut / the
                                    // default-open-tab preference) -- see initialMainTabRoute.
                                    startDestination = initialMainTabRoute,
                                    // iOS parallax push. The incoming screen travels the FULL
                                    // width; the outgoing one slides a third of that and dims,
                                    // and the difference between the two speeds is what reads
                                    // as depth. Both share Motion.push()'s curve so they stay
                                    // locked together.
                                    //
                                    // This replaced an index-based direction scheme that had
                                    // been dead since the pager migration and was actively
                                    // wrong: it looked routes up in `navigationItems`, but
                                    // every tab now lives inside the `main_tabs` pager, so the
                                    // outgoing route was ALWAYS `main_tabs` and both lookups
                                    // returned -1. `exitTransition`'s `-1 > -1` is false, so
                                    // the else branch ran and the outgoing screen slid RIGHT
                                    // while the incoming screen also slid in from the right —
                                    // both travelling the same way. That is what made every
                                    // non-morph push (settings sub-pages most visibly) look
                                    // broken. Pop happened to be correct by accident.
                                    enterTransition = {
                                        slideInHorizontally(Motion.push()) { it }
                                    },
                                    exitTransition = {
                                        // A destination that grows out of one of this
                                        // screen's tiles must not slide it away underneath:
                                        // the tile is the thing the next screen is coming
                                        // out of, so this one holds still and dims.
                                        if (isMorphRoute(targetState.destination.route)) {
                                            fadeOut(tween(200), targetAlpha = 0.7f)
                                        } else {
                                            slideOutHorizontally(Motion.push()) {
                                                Motion.parallaxOffset(it)
                                            } + fadeOut(Motion.push(), targetAlpha = Motion.PushDimAlpha)
                                        }
                                    },
                                    // Pop is a plain crossfade -- both directions, every
                                    // route, morph or not. Predictive back scrubs whatever
                                    // spec is here by the GESTURE's live drag progress rather
                                    // than running it on a timer, and only a plain alpha tween
                                    // scrubs cleanly: a fraction of a fade is still a smaller
                                    // fade, but a fraction of a slide-with-parallax-offset (or
                                    // the morph's spring-driven shrink-to-tile) has no clean
                                    // meaning at partial progress, which is what was showing up
                                    // as the outgoing and incoming screens seemingly both
                                    // sliding into place at once during a back-gesture drag.
                                    // Push keeps its slide/morph above -- only pop needed to
                                    // change, since only pop is ever gesture-driven.
                                    popEnterTransition = {
                                        fadeIn(tween(220))
                                    },
                                    popExitTransition = {
                                        fadeOut(tween(220))
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    navigationBuilder(
                                        navController = navController,
                                        scrollBehavior = topAppBarScrollBehavior,
                                        activity = this@MainActivity,
                                        snackbarHostState = snackbarHostState,
                                    )
                                }
                                }

                                // Search, over the tabs rather than beside them. This box
                                // (not NavHost's own modifier) is what carries layerBackdrop
                                // now -- see the comment on it below. Moved OUT of NavHost's
                                // modifier chain and made the shared parent of both NavHost
                                // and this AnimatedVisibility specifically so glass sampling
                                // covers search too.
                                //
                                // It didn't before: layerBackdrop lived on NavHost's own
                                // modifier, and this AnimatedVisibility was a SIBLING call
                                // after NavHost's closing brace -- outside the element that
                                // modifier was attached to, not inside it. appBackdrop's
                                // recorded layer therefore never contained search's content
                                // at all; it kept showing whatever the tab underneath had
                                // last drawn, frozen, for as long as search stayed open. That
                                // is the "search's backdrop still holds the previous page"
                                // bug -- not a freeze/throttle timing issue, a structural one.
                                AnimatedVisibility(
                                    visible = searchOverlayOpen,
                                    enter = fadeIn(Motion.appear()),
                                    exit = fadeOut(Motion.appear()),
                                ) {
                                    SearchScreen(
                                        navController = navController,
                                        pureBlack = pureBlack,
                                    )
                                }
                                } // CompositionLocalProvider(LocalSharedTransitionScope)
                                } // SharedTransitionLayout
                            }

                            com.nexamusic.app.ui.component.RingtoneTrimmerDialog(
                                isVisible = ringtoneUiState.showTrimmer,
                                songId = ringtoneUiState.targetSongId,
                                songTitle = ringtoneUiState.targetSongTitle,
                                duration = ringtoneUiState.targetSongDuration,
                                onDismiss = { ringtoneViewModel.hideTrimmer() },
                                onResolveStreamUrl = { ringtoneViewModel.getStreamUrl(this@MainActivity, it) },
                                onConfirm = { start, end -> ringtoneViewModel.setAsRingtone(this@MainActivity, start, end) }
                            )

                            if (ringtoneUiState.showProgress) {
                                com.nexamusic.app.ui.component.RingtoneProgressDialog(
                                    isVisible = ringtoneUiState.showProgress,
                                    progress = ringtoneUiState.progress,
                                    statusMessage = ringtoneUiState.statusMessage,
                                    isComplete = ringtoneUiState.isComplete,
                                    isSuccess = ringtoneUiState.isSuccess,
                                    onDismiss = { ringtoneViewModel.dismissProgress() },
                                    onOpenSettings = { ringtoneViewModel.openRingtoneSettings(this@MainActivity) }
                                )
                            }

                            com.nexamusic.app.ui.component.DonationPromptHost()

                            // Both float OVER the full-width NavHost, exactly as the
                            // bottom bar does on a phone: nothing reserves layout
                            // width for them, so content runs underneath instead of
                            // being clipped short of them.
                            //
                            // Ambient mode forces landscape orientation (see AmbientModeScreen's
                            // DisposableEffect), which alone flips showRail true on a phone that
                            // would never otherwise qualify for the rail layout — excluded here
                            // the same way ambient_mode is already excluded from the bottom/
                            // floating nav bar above, or the rail floats over its full-screen
                            // visualizer with no route-based reason to.
                            if (showRail && currentRoute != "ambient_mode" && currentRoute != "settings/appearance/diy") {
                                // No global floating Back here anymore: each screen's own
                                // back button (TopAppBar nav icon or floating chrome row)
                                // now insets clear of the side panel, so a second global
                                // one just duplicated it.

                                // Everything the top bar carries on a phone moves in
                                // here, plus the user's pinned playlists â€” the DAO's
                                // playlists() already filters to bookmarkedAt, so this
                                // is the pinned set, not every local playlist.
                                val sidebarPlaylists by database
                                    .playlists(PlaylistSortType.NAME, descending = false)
                                    .collectAsStateWithLifecycle(initialValue = emptyList())
                                val sidebarSections = remember(
                                    showHistoryButton,
                                    showStatsButton,
                                    listenTogetherInTopBar,
                                    sidebarPlaylists,
                                ) {
                                    buildList {
                                        val quick = buildList {
                                            if (showHistoryButton) {
                                                add(
                                                    SideBarLink(
                                                        label = getString(R.string.history),
                                                        iconRes = R.drawable.music_history,
                                                        onClick = { navController.navigate("history") },
                                                    )
                                                )
                                            }
                                            if (showStatsButton) {
                                                add(
                                                    SideBarLink(
                                                        label = getString(R.string.stats),
                                                        iconRes = R.drawable.stats,
                                                        onClick = { navController.navigate("stats") },
                                                    )
                                                )
                                            }
                                            if (listenTogetherInTopBar) {
                                                add(
                                                    SideBarLink(
                                                        label = getString(R.string.together),
                                                        iconRes = R.drawable.group_outlined,
                                                        onClick = {
                                                            navController.navigate("listen_together_from_topbar")
                                                        },
                                                    )
                                                )
                                            }
                                        }
                                        if (quick.isNotEmpty()) add(SideBarSection(links = quick))

                                        if (sidebarPlaylists.isNotEmpty()) {
                                            add(
                                                SideBarSection(
                                                    title = getString(R.string.playlists),
                                                    links = sidebarPlaylists.map { playlist ->
                                                        SideBarLink(
                                                            label = playlist.playlist.name,
                                                            thumbnailUrl = playlist.thumbnails.firstOrNull(),
                                                            onClick = {
                                                                navController.navigate(
                                                                    "local_playlist/${playlist.id}"
                                                                )
                                                            },
                                                        )
                                                    },
                                                )
                                            )
                                        }
                                    }
                                }

                                AppFloatingSideBar(
                                    navigationItems = navigationItems,
                                    currentRoute = effectiveRoute,
                                    onItemClick = onRailItemClick,
                                    sections = sidebarSections,
                                    pureBlack = pureBlack,
                                    collapsed = sideBarCollapsed,
                                    onToggleCollapsed = { onSideBarCollapsedChange(!sideBarCollapsed) },
                                    footer = { footerCollapsed ->
                                        SideBarAccountRow(
                                            accountImageUrl = accountImageUrl,
                                            collapsed = footerCollapsed,
                                            onClick = {
                                                if (enableSettingsPopup) {
                                                    showSettingDialoge = true
                                                } else {
                                                    navController.navigate(Screens.Settings.route) {
                                                        popUpTo(navController.graph.startDestinationId) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                        )
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .windowInsetsPadding(
                                            windowsInsets.only(WindowInsetsSides.Vertical)
                                        )
                                        .padding(start = SideBarMargin, top = 8.dp, bottom = 8.dp),
                                )

                                // The phone bar's docked accessory, floating free on
                                // the opposite edge â€” same pill, same glass, bottom
                                // right rather than centred. Swaps for the real
                                // search input bar while searching, same slide+fade
                                // the phone's own AppFloatingNavBar uses to hide its
                                // mini player for the keyboard.
                                if (playerMediaMetadata != null || inSearchScreen) {
                                    // Centred on the content area, not the screen:
                                    // the side bar's space is padded out first, and
                                    // the pill takes 80% of whatever is left.
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

                    // One preference decides the presentation for every long-press menu
                    // in the app. Both hosts read the same MenuState, so the ~30 call
                    // sites are untouched either way.
                    val (overlayMenuStyle) = rememberPreference(OverlayMenuStyleKey, defaultValue = true)
                    if (overlayMenuStyle) {
                        OverlayMenu(state = LocalMenuState.current)
                    } else {
                        BottomSheetMenu(
                            state = LocalMenuState.current,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    BottomSheetPage(
                        state = LocalBottomSheetPageState.current,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )

                    // Hosted at the root, not on the Listen Together screen: a
                    // join request is useless if it only shows on the one screen
                    // the host is probably not looking at.
                    ListenTogetherOverlay(
                        manager = LocalListenTogetherManager.current,
                        playerConnection = playerConnection,
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
                                if (route in TabRootRoutes) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                } else {
                                    navController.navigate(route)
                                }
                            },
                            homeViewModel = homeViewModel
                        )
                    }
                }
            }
        }
    }

    private fun handleDeepLinkIntent(intent: Intent, navController: NavHostController) {
        val uri = intent.data ?: intent.extras?.getString(Intent.EXTRA_TEXT)?.toUri() ?: return
        intent.data = null
        intent.removeExtra(Intent.EXTRA_TEXT)
        val coroutineScope = lifecycle.coroutineScope

        val listenCode = uri.getQueryParameter("code")
            ?: uri.getQueryParameter("room")
            ?: uri.pathSegments.getOrNull(1)
        val isListenLink = uri.pathSegments.firstOrNull() == "listen" || uri.host?.equals("listen", ignoreCase = true) == true
        if (!listenCode.isNullOrBlank() && isListenLink) {
            val username = dataStore.get(ListenTogetherUsernameKey, "").ifBlank { "Guest" }
            listenTogetherManager.joinRoom(listenCode, username)
            return
        }

        when (val path = uri.pathSegments.firstOrNull()) {
            "playlist" -> uri.getQueryParameter("list")?.let { playlistId ->
                if (playlistId.startsWith("OLAK5uy_")) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.albumSongs(playlistId).onSuccess { songs ->
                            songs.firstOrNull()?.album?.id?.let { browseId ->
                                withContext(Dispatchers.Main) {
                                    navController.navigate("album/$browseId") { launchSingleTop = true }
                                }
                            }
                        }.onFailure { reportException(it) }
                    }
                } else {
                    navController.navigate("online_playlist/$playlistId") { launchSingleTop = true }
                }
            }

            "browse" -> uri.lastPathSegment?.let { browseId ->
                navController.navigate("album/$browseId") { launchSingleTop = true }
            }

            "channel", "c" -> uri.lastPathSegment?.let { artistId ->
                navController.navigate("artist/$artistId") { launchSingleTop = true }
            }

            "search" -> {
                uri.getQueryParameter("q")?.let {
                    navController.navigate("search/${URLEncoder.encode(it, "UTF-8")}") { launchSingleTop = true }
                }
            }

            else -> {
                val videoId = when {
                    path == "watch" -> uri.getQueryParameter("v")
                    uri.host == "youtu.be" -> uri.pathSegments.firstOrNull()
                    else -> null
                }

                val playlistId = uri.getQueryParameter("list")

                if (videoId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(listOf(videoId), playlistId).onSuccess { queue ->
                            withContext(Dispatchers.Main) {
                                playerConnection?.playQueue(
                                    YouTubeQueue(
                                        WatchEndpoint(videoId = queue.firstOrNull()?.id, playlistId = playlistId),
                                        queue.firstOrNull()?.toMediaMetadata()
                                    )
                                )
                            }
                        }.onFailure {
                            reportException(it)
                        }
                    }
                } else if (playlistId != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(null, playlistId).onSuccess { queue ->
                            val firstItem = queue.firstOrNull()
                            withContext(Dispatchers.Main) {
                                playerConnection?.playQueue(
                                    YouTubeQueue(
                                        WatchEndpoint(videoId = firstItem?.id, playlistId = playlistId),
                                        firstItem?.toMediaMetadata()
                                    )
                                )
                            }
                        }.onFailure {
                            reportException(it)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setSystemBarAppearance(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView.rootView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor = (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
    }
}

val LocalDatabase = staticCompositionLocalOf<MusicDatabase> { error("No database provided") }
val LocalRingtoneViewModel = staticCompositionLocalOf<com.nexamusic.app.ui.screens.settings.RingtoneViewModel> { error("No RingtoneViewModel provided") }
val LocalPlayerConnection = staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
val LocalPlayerAwareWindowInsets = compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalDownloadUtil = staticCompositionLocalOf<DownloadUtil> { error("No DownloadUtil provided") }
val LocalSyncUtils = staticCompositionLocalOf<SyncUtils> { error("No SyncUtils provided") }
val LocalListenTogetherManager = staticCompositionLocalOf<com.nexamusic.app.listentogether.ListenTogetherManager?> { null }
val LocalIsPlayerExpanded = compositionLocalOf { false }

/**
 * True while the app is laid out for tab view â€” the vertical floating side bar
 * instead of the bottom bar. Screens read this to switch their header to the
 * wide arrangement (hero as a card with its title beside it) rather than the
 * phone's full-bleed hero.
 */
val LocalTabView = compositionLocalOf { false }



