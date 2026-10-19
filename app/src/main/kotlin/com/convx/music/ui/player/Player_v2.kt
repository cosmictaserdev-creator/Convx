/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI adapted from an open-source (GPL-3.0) base.

package com.convx.music.ui.player

import androidx.lifecycle.repeatOnLifecycle

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.heroSideLiveBlur
import com.convx.music.ui.utils.VibrantChromeInk
import com.convx.music.ui.utils.vibrantChrome
import com.convx.music.ui.ios.compat.AppTheme
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFAirplayAudio
import com.composables.sfsymbols.monochrome.SFHeadphones
import com.composables.sfsymbols.monochrome.SFSpeakerFill
import com.composables.sfsymbols.monochrome.SFSpeakerWave3Fill

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import androidx.media3.common.C
import com.convx.music.LocalPlayerConnection
import com.convx.music.playback.PlayerConnection
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.PlayerBackgroundStyleKey
import com.convx.music.constants.PlayerLook
import com.convx.music.constants.PlayerLookKey
import com.convx.music.constants.PlayerStaticColorKey
import com.convx.music.constants.PlayerGradientStopsKey
import com.convx.music.ui.theme.decodeGradientStops
import androidx.compose.ui.graphics.luminance
import com.convx.music.constants.ShowPlayerThumbnailShadowKey
import com.convx.music.constants.PlayerThumbnailShadowElevationKey
import com.convx.music.constants.EnableGoogleCastKey
import com.convx.music.models.MediaMetadata
import com.convx.music.ui.component.BottomSheetState
import com.convx.music.ui.component.DjReadout
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalPooledGlass
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import androidx.compose.runtime.saveable.rememberSaveable
import com.convx.music.ui.component.heroFootLiveBlur
import com.convx.music.ui.component.PlayerSliderTrack
import com.convx.music.ui.theme.PlayerSliderColors
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.extensions.togglePlayPause
import com.convx.music.extensions.metadata
import com.convx.music.ui.component.Lyrics
import com.convx.music.ui.component.rememberBottomSheetState
import com.convx.music.ui.component.expandedAnchor
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import com.convx.music.constants.SwipeSensitivityKey
import com.convx.music.constants.SwipeThumbnailKey
import kotlin.math.roundToInt
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.flow.first
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.menu.LyricsMenu
import com.convx.music.ui.menu.PlayerMenu
import com.convx.music.ui.utils.ShowMediaInfo
import com.convx.music.ui.utils.ShowOffsetDialog
import com.convx.music.ui.utils.resize
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size as CoilSize
import com.convx.music.core.AudioDeviceMenu
import com.convx.music.core.AudioDeviceType
import com.convx.music.core.audioDeviceSymbol
import com.convx.music.core.ActiveAudioOutput
import com.convx.music.core.getActiveAudioOutput
import com.convx.music.ui.component.CastButton
import com.convx.music.BuildConfig
import com.convx.music.R
import com.convx.music.utils.rememberPreference
import com.convx.music.constants.CompactPlayerInTabViewKey
import com.convx.music.LocalTabView
import com.convx.music.constants.ShowUpNextKey
import com.convx.music.constants.HideVolumeBarKey
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.convx.music.ui.player.customize.AbsoluteFrameDiyStickers
import com.convx.music.ui.player.customize.PlayerGlyph
import androidx.compose.ui.graphics.graphicsLayer
import com.convx.music.ui.utils.flamingoChrome
import com.convx.music.ui.player.customize.AnimatedPlayerGlyph
import com.convx.music.ui.player.customize.PlayerIconSlot
import com.convx.music.ui.player.customize.rememberDiyLayout
import com.convx.music.ui.player.customize.DiyOrientation
import com.convx.music.LocalPlayerConnection
import kotlinx.coroutines.launch
import com.convx.music.ui.utils.Motion

enum class PlayerInternalState { COVER, LYRICS, QUEUE }

/** Mirrors Player.kt's PARENT_SHEET_SETTLE_TIMEOUT_MS (private to that file). */
private const val V2_PARENT_SHEET_SETTLE_TIMEOUT_MS = 1_000L

// enabled flips instantly on hero-look/pause-state changes; animating the elevation
// (rather than adding/removing the shadow modifier on the raw boolean) is what makes
// it fade in/out instead of popping on in a single frame.
@Composable
private fun Modifier.customSoftShadow(elevation: androidx.compose.ui.unit.Dp, cornerRadius: androidx.compose.ui.unit.Dp, enabled: Boolean): Modifier {
    val animatedElevation by animateDpAsState(
        targetValue = if (enabled) elevation else 0.dp,
        label = "playerThumbnailShadowElevation",
    )
    return if (animatedElevation > 0.dp) shadow(animatedElevation, RoundedCornerShape(cornerRadius)) else this
}
@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayerV2(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    showDiyStickers: Boolean = true,
    forcedOrientation: DiyOrientation? = null,
    isPreviewOnly: Boolean = false,
    diyRootCoordinates: androidx.compose.ui.layout.LayoutCoordinates? = null,
    onCoverShowingChange: ((Boolean) -> Unit)? = null,
    /** Classic player uses these same squared panes, retaining its own cover. */
    auxiliaryPageState: PlayerAuxiliaryPageState? = null,
) {
    val deviceDensity = androidx.compose.ui.platform.LocalDensity.current
    BoxWithConstraints(modifier.fillMaxSize()) {
        val wide = forcedOrientation?.let { it == DiyOrientation.LANDSCAPE } ?: (maxWidth > maxHeight)
        val referenceWidth = if (wide) 852f else 393f
        val referenceHeight = if (wide) 393f else 852f
        val scale = minOf(maxWidth.value / referenceWidth, maxHeight.value / referenceHeight).coerceAtLeast(0.1f)
        // One uniform viewport scale keeps glyphs, type and spacing in the same proportions.
        // Menus and the quality card float over this backdrop, which records the whole
        // player (artwork too) -- see V2Overlay.kt.
        val overlayBackdrop = com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop()
        val overlaySlot = remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
        CompositionLocalProvider(
            androidx.compose.ui.platform.LocalDensity provides
                androidx.compose.ui.unit.Density(deviceDensity.density * scale, deviceDensity.fontScale),
            LocalV2Overlay provides overlaySlot,
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    // Recorded only while a panel is up: no extra layer pass otherwise.
                    Modifier.fillMaxSize().then(
                        if (overlaySlot.value != null) {
                            Modifier.layerBackdrop(overlayBackdrop)
                        } else Modifier
                    )
                ) {
                    PlayerV2Content(state, navController, Modifier, showDiyStickers, forcedOrientation,
                        isPreviewOnly, diyRootCoordinates, onCoverShowingChange, auxiliaryPageState)
                }
                V2OverlayHost(overlaySlot, overlayBackdrop)
            }
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalSharedTransitionApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun PlayerV2Content(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    showDiyStickers: Boolean = true,
    forcedOrientation: DiyOrientation? = null,
    isPreviewOnly: Boolean = false,
    /** See AbsoluteFrameDiyStickers' doc -- shared with classic Player.kt's own z < 0
     *  sticker band so both skins resolve stickers against the same frame. */
    diyRootCoordinates: androidx.compose.ui.layout.LayoutCoordinates? = null,
    /** Reports whether the cover is showing so Player.kt can hide its sticker overlay on Lyrics and Queue. */
    onCoverShowingChange: ((Boolean) -> Unit)? = null,
    auxiliaryPageState: PlayerAuxiliaryPageState? = null,
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val playerConnection = LocalPlayerConnection.current ?: return
    val ui = rememberPlayerUiState(playerConnection)
    val mediaMetadata by ui.mediaMetadata
    val videoLookup by ui.rememberVideoStreamLookup()
    val availableVideo by playerConnection.videoAvailability.collectAsStateWithLifecycle()
    val canWatchVideo = availableVideo?.songId == mediaMetadata?.id && availableVideo != null
    val videoStream = (videoLookup as? com.convx.music.playback.VideoStreamLookup.Ready)
        ?.stream?.takeIf { it.mediaId == mediaMetadata?.id }
    val videoPresentation = LocalFullscreenVideo.current
    var previewWatchVideo by remember(mediaMetadata?.id) { mutableStateOf(false) }
    val watchVideo = if (isPreviewOnly || videoPresentation == null) previewWatchVideo else
        mediaMetadata?.id != null && videoPresentation.selectedVideoId == mediaMetadata?.id
    val selectVideo: (Boolean) -> Unit = { selected ->
        if (isPreviewOnly || videoPresentation == null) previewWatchVideo = selected
        else {
            videoPresentation.selectVideo(mediaMetadata?.id, selected)
            mediaMetadata?.id?.let { playerConnection.service.setVideoPlaybackMode(it, selected) }
        }
    }
    LaunchedEffect(mediaMetadata?.id, state.isCollapsed, isPreviewOnly) {
        if (auxiliaryPageState != null) return@LaunchedEffect
        playerConnection.requestVideoAvailability(mediaMetadata?.id.takeIf { !state.isCollapsed && !isPreviewOnly })
    }
    DisposableEffect(playerConnection) {
        onDispose { if (auxiliaryPageState == null) playerConnection.requestVideoAvailability(null) }
    }
    LaunchedEffect(watchVideo, mediaMetadata?.id) {
        if (auxiliaryPageState != null) return@LaunchedEffect
        if (watchVideo) mediaMetadata?.id?.let(playerConnection::requestVideoStream)
        else playerConnection.clearVideoStreamRequest()
    }
    val isPlaying by ui.isPlaying
    // Delay pause transitions to avoid a false pause/play flash during player swaps or focus changes.
    var glyphPlaying by remember { mutableStateOf(isPlaying) }
    LaunchedEffect(isPlaying) {
        if (!isPlaying) delay(GLYPH_PAUSE_SETTLE_MS)
        glyphPlaying = isPlaying
    }
    // One canvas decoder for the whole V2 player, shared by the cover and the lyrics/queue
    // header (see PlayerV2Canvas.kt). Paused whenever the sheet is collapsed.
    val v2CrossfadeMs by ui.crossfadeDurationMs
    // Track-change ladder (TrackChoreography): cover -> words -> details -> ambience.
    val trackChoreography = rememberTrackChoreography(mediaMetadata)
    val artworkMetadata = trackChoreography.artwork.value
    val paneArtworkStore = remember { com.convx.artwork.ArtworkBitmapStore() }
    val textMetadata = trackChoreography.text.value
    val detailMetadata = trackChoreography.detail.value
    val canvasVideo = rememberCanvasVideoController(
        mediaMetadata = trackChoreography.ambient.value,
        isPlaying = isPlaying && !state.isCollapsed,
        trackFadeMillis = ambientTransitionMillis(v2CrossfadeMs),
        videoStream = videoStream.takeIf { watchVideo },
        videoStartPositionMs = playerConnection.player.currentPosition,
    )
    val videoLifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    LaunchedEffect(canvasVideo, watchVideo, videoStream?.videoId, state.isCollapsed, videoLifecycleOwner) {
        if (!watchVideo || videoStream == null || state.isCollapsed) return@LaunchedEffect
        videoLifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            while (isActive) {
                canvasVideo.synchronizeTo(playerConnection.player)
                delay(500L)
            }
        }
    }
    // For the hero artwork's live swipe peek below: the neighbour thumbnail slides in from
    // whichever side the finger is dragging toward, tracking the drag itself rather than
    // only appearing once the gesture commits.
    val queueWindows by ui.queueWindows
    // Play-order index, not timeline order: queueWindows is built in play order
    // (PlayerExt.getQueueWindows), so ±1 here stays faithful on shuffle.
    val currentQueueIndex by ui.currentWindowIndex
    val nextMediaMetadata = queueWindows.getOrNull(currentQueueIndex + 1)?.mediaItem?.metadata
    val previousMediaMetadata = queueWindows.getOrNull(currentQueueIndex - 1)?.mediaItem?.metadata
    // Preload adjacent covers at the size ThumbnailImage will display.
    val heroPreloadUrls = remember(nextMediaMetadata?.thumbnailUrl, previousMediaMetadata?.thumbnailUrl) {
        listOfNotNull(
            nextMediaMetadata?.thumbnailUrl?.resize(1200, 1200),
            previousMediaMetadata?.thumbnailUrl?.resize(1200, 1200),
        )
    }
    val currentSong by ui.currentSong
    // Keep the previous like state until the database row for the new track arrives.
    val resolvedLike = currentSong?.song?.takeIf { it.id == detailMetadata?.id }?.let { it.id to it.liked }
    var lastResolvedLike by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val shownLike = resolvedLike ?: lastResolvedLike
    SideEffect { if (resolvedLike != null) lastResolvedLike = resolvedLike }
    val currentLyrics by ui.currentLyrics
    val menuState = LocalMenuState.current
    var showV2Options by remember { mutableStateOf(false) }
    var showRoutePopover by remember { mutableStateOf(false) }
    // Same condition as LyricsMenu's "Translate with AI" entry.
    val lyricsAiProvider by rememberPreference(com.convx.music.constants.AiProviderKey, "OpenRouter")
    val lyricsOpenRouterKey by rememberPreference(com.convx.music.constants.OpenRouterApiKey, "")
    val lyricsDeeplKey by rememberPreference(com.convx.music.constants.DeeplApiKey, "")
    val hasLyricsAiKey = if (lyricsAiProvider == "DeepL") lyricsDeeplKey.isNotBlank() else lyricsOpenRouterKey.isNotBlank()
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val paneState: androidx.compose.runtime.MutableState<PlayerInternalState> =
        auxiliaryPageState ?: remember { mutableStateOf(PlayerInternalState.COVER) }
    var playerState by paneState
    // Mounted from the classic player the page has no inner transition to play (it starts on the
    // page), so lift its list in the way V2's own page change does.
    val auxMount = remember { Animatable(if (auxiliaryPageState != null) 0f else 1f) }
    LaunchedEffect(Unit) { auxMount.animateTo(1f, Motion.appear()) }
    val videoActive = watchVideo && videoStream != null && playerState == PlayerInternalState.COVER

    LaunchedEffect(playerState) {
        onCoverShowingChange?.invoke(playerState == PlayerInternalState.COVER)
    }

    // Shared by the swipe gesture and the neighboring-cover preview.
    val heroDragOffset = remember { Animatable(0f) }
    // Freeze the previewed cover during a skip so it cannot switch to the next neighbor.
    var committedPeek by remember { mutableStateOf<com.convx.music.models.MediaMetadata?>(null) }
    // Track that was current when the skip began; null means no skip is settling.
    var committedFromId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(committedFromId, artworkMetadata?.id) {
        val fromId = committedFromId ?: return@LaunchedEffect
        if ((artworkMetadata?.id ?: "") == fromId) {
            // If the skip fails, return the cover to its resting position.
            kotlinx.coroutines.delay(3_000)
            if (committedFromId == fromId) {
                committedPeek = null
                committedFromId = null
                heroDragOffset.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = 300f))
            }
            return@LaunchedEffect
        }
        // Let the new cover draw for two frames before returning the card to rest.
        androidx.compose.runtime.withFrameNanos { }
        androidx.compose.runtime.withFrameNanos { }
        heroDragOffset.snapTo(0f)
        committedPeek = null
        committedFromId = null
    }

    // Mini-player actions arrive through PlayerConnection.
    var lyricsIntroKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.isCollapsed) {
        if (!state.isCollapsed) lyricsIntroKey++
    }
    // Only the selected player skin should collect these requests; skip the editor preview.
    LaunchedEffect(isPreviewOnly) {
        if (isPreviewOnly || auxiliaryPageState != null) return@LaunchedEffect
        playerConnection.requestShowLyrics.collect { requested ->
            if (requested) {
                if (!state.isExpanded) {
                    withTimeoutOrNull(V2_PARENT_SHEET_SETTLE_TIMEOUT_MS) {
                        snapshotFlow { state.isExpanded }.first { it }
                    }
                }
                playerState = PlayerInternalState.LYRICS
                playerConnection.requestShowLyrics.value = false
            }
        }
    }
    LaunchedEffect(isPreviewOnly) {
        if (isPreviewOnly || auxiliaryPageState != null) return@LaunchedEffect
        playerConnection.requestShowQueue.collect { requested ->
            if (requested) {
                if (!state.isExpanded) {
                    withTimeoutOrNull(V2_PARENT_SHEET_SETTLE_TIMEOUT_MS) {
                        snapshotFlow { state.isExpanded }.first { it }
                    }
                }
                playerState = PlayerInternalState.QUEUE
                playerConnection.requestShowQueue.value = false
            }
        }
    }

    val listenTogetherManager = com.convx.music.LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsStateWithLifecycle(initialValue = com.convx.music.listentogether.RoomRole.NONE)
    val isListenTogetherGuest = listenTogetherRoleState?.value == com.convx.music.listentogether.RoomRole.GUEST
    val isMuted by ui.isMuted
    val canSkipPrevious by ui.canSkipPrevious
    val canSkipNext by ui.canSkipNext

    // Re-register while expanded so this handler takes precedence over the sheet's back handler.
    androidx.compose.runtime.key(state.isExpanded) {
        androidx.activity.compose.BackHandler(enabled = playerState != PlayerInternalState.COVER) {
            playerState = PlayerInternalState.COVER
        }
    }

    var controlsVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(playerState, lastInteractionTime, showV2Options, menuState.isVisible, bottomSheetPageState.isVisible) {
        if (playerState == PlayerInternalState.LYRICS) {
            controlsVisible = true
            if (showV2Options || menuState.isVisible || bottomSheetPageState.isVisible) return@LaunchedEffect
            delay(3000)
            controlsVisible = false
        } else if (playerState != PlayerInternalState.QUEUE) {
            controlsVisible = true
        }
    }
    // Hide controls while scrolling down the queue and show them when scrolling up.
    LaunchedEffect(playerState) {
        if (playerState == PlayerInternalState.QUEUE) controlsVisible = true
    }
    // Require sustained movement to prevent finger jitter from toggling the controls.
    val queueControlsFlipPx = with(androidx.compose.ui.platform.LocalDensity.current) { 48.dp.toPx() }
    val queueControlsScroll = remember(queueControlsFlipPx) {
        object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
            var travel = 0f
            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: androidx.compose.ui.input.nestedscroll.NestedScrollSource,
            ): androidx.compose.ui.geometry.Offset {
                if (source == androidx.compose.ui.input.nestedscroll.NestedScrollSource.UserInput) {
                    val dy = consumed.y + available.y
                    val continuesCurrentRun = travel == 0f || (dy < 0f) == (travel < 0f)
                    travel = if (dy != 0f && continuesCurrentRun) travel + dy else dy
                    if (travel <= -queueControlsFlipPx) {
                        if (controlsVisible) controlsVisible = false
                        travel = 0f
                    } else if (travel >= queueControlsFlipPx) {
                        if (!controlsVisible) controlsVisible = true
                        travel = 0f
                    }
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }

    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var sliderPosition by remember(mediaMetadata?.id) { mutableStateOf<Long?>(null) }

    val audioManager = remember { context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager }
    val maxSystemVolume = remember { audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat() }

    var systemVolume by remember { mutableFloatStateOf(audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat() / maxSystemVolume) }
    val animatedVolume by androidx.compose.animation.core.animateFloatAsState(
        targetValue = systemVolume,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = 150,
            easing = androidx.compose.animation.core.FastOutLinearInEasing
        ),
        label = "volumeAnimation"
    )
    // Refresh on resume because the system volume broadcast may be missed in the background.
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(context, lifecycleOwner) {
        fun refresh() {
            systemVolume = audioManager.getStreamVolume(
                android.media.AudioManager.STREAM_MUSIC
            ).toFloat() / maxSystemVolume
        }
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: android.content.Context?, intent: android.content.Intent?) {
                if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") refresh()
            }
        }
        androidx.core.content.ContextCompat.registerReceiver(
            context,
            receiver,
            android.content.IntentFilter("android.media.VOLUME_CHANGED_ACTION"),
            androidx.core.content.ContextCompat.RECEIVER_EXPORTED,
        )
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        refresh()
        onDispose {
            context.unregisterReceiver(receiver)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val selectedLook by rememberEnumPreference(key = PlayerLookKey, defaultValue = PlayerLook.SQUARED)
    val playerLook = if (auxiliaryPageState != null) PlayerLook.SQUARED else selectedLook
    val isHeroLook = playerLook == PlayerLook.HERO
    androidx.compose.runtime.DisposableEffect(Unit) {
        PlayerMorph.stayInPlace = false
        onDispose { PlayerMorph.stayInPlace = false }
    }
    val heroVideoFrame = remember(canvasVideo) { mutableIntStateOf(0) }
    val selectedBackground by rememberEnumPreference(PlayerBackgroundStyleKey, PlayerBackgroundStyle.GRADIENT)
    // Hero owns its artwork gradient. Squared follows the same background choice
    // as Player.kt, including contrast on static and custom gradient backgrounds.
    val playerBackground = if (isHeroLook) PlayerBackgroundStyle.GRADIENT else selectedBackground
    val showPlayerThumbnailShadow by rememberPreference(ShowPlayerThumbnailShadowKey, defaultValue = true)
    val playerThumbnailShadowElevation by rememberPreference(PlayerThumbnailShadowElevationKey, defaultValue = 8f)
    val enableGoogleCast by rememberPreference(EnableGoogleCastKey, defaultValue = true)
    val (showUpNext) = rememberPreference(ShowUpNextKey, defaultValue = false)
    val (hideVolumeBar) = rememberPreference(HideVolumeBarKey, defaultValue = false)
    val diyLayout = rememberDiyLayout()
    // Use the preview's forced orientation when present; otherwise follow the device.
    val diyOrientation = forcedOrientation ?: if (
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    ) DiyOrientation.LANDSCAPE else DiyOrientation.PORTRAIT

    var showAudioDeviceBottomSheet by remember { mutableStateOf(false) }
    val sleepTimer = rememberSleepTimerState(playerConnection)
    SleepTimerDialogHost(playerConnection, sleepTimer)

    val audioDeviceIcons = com.convx.music.core.rememberAudioDeviceIcons()
    val audioDeviceNames = com.convx.music.core.rememberAudioDeviceNames()
    val audioOutput by produceState<ActiveAudioOutput?>(initialValue = getActiveAudioOutput(context)) {
        val am = context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
        val scope = this
        val updateDeviceState: () -> Unit = {
            scope.value = getActiveAudioOutput(context)
            scope.launch {
                kotlinx.coroutines.delay(500)
                scope.value = getActiveAudioOutput(context)
            }
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: android.content.Context, intent: Intent) {
                updateDeviceState()
            }
        }
        val callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            object : android.media.AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out android.media.AudioDeviceInfo>?) {
                    updateDeviceState()
                }
                override fun onAudioDevicesRemoved(removedDevices: Array<out android.media.AudioDeviceInfo>?) {
                    updateDeviceState()
                }
            }
        } else null

        val filter = IntentFilter().apply {
            addAction(AudioManager.ACTION_HEADSET_PLUG)
            addAction("android.bluetooth.adapter.action.STATE_CHANGED")
            addAction("android.bluetooth.device.action.ACL_CONNECTED")
            addAction("android.bluetooth.device.action.ACL_DISCONNECTED")
            addAction("android.media.AUDIO_BECOMING_NOISY")
        }

        androidx.core.content.ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            androidx.core.content.ContextCompat.RECEIVER_EXPORTED,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && callback != null) {
            am.registerAudioDeviceCallback(callback, Handler(Looper.getMainLooper()))
        }

        awaitDispose {
            context.unregisterReceiver(receiver)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && callback != null) {
                am.unregisterAudioDeviceCallback(callback)
            }
        }
    }

    // Poll only while expanded; the mini player tracks playback separately.
    LaunchedEffect(isPlaying, state.isCollapsed, mediaMetadata?.id) {
        if (state.isCollapsed) return@LaunchedEffect
        while (isActive) {
            val rawDuration = playerConnection.player.duration
            position = playerConnection.player.currentPosition.coerceAtLeast(0L)
            // A file whose length ExoPlayer cannot work out (some MP3s) falls back to the library's duration,
            // so the seek bar stays usable.
            duration = if (rawDuration == C.TIME_UNSET || rawDuration < 0) {
                (mediaMetadata?.duration?.takeIf { it > 0 } ?: 0) * 1000L
            } else rawDuration
            delay(if (isPlaying) 50L else 500L)
        }
    }

    val staticColorInt by rememberPreference(PlayerStaticColorKey, defaultValue = 0xFF1A1A1A.toInt())
    val gradientStopsRaw by rememberPreference(PlayerGradientStopsKey, defaultValue = "")
    val gradientStops = remember(gradientStopsRaw) { decodeGradientStops(gradientStopsRaw) }
    val flatOnColor = remember(playerBackground, staticColorInt, gradientStops) {
        val luminance = when (playerBackground) {
            PlayerBackgroundStyle.CUSTOM_GRADIENT ->
                gradientStops.map { it.luminance() }.average().toFloat()
            else -> Color(staticColorInt).luminance()
        }
        if (luminance > 0.5f) Color.Black else Color.White
    }

    val adaptivePrimary by animateColorAsState(
        targetValue = when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.onSurface
            PlayerBackgroundStyle.BLUR,
            PlayerBackgroundStyle.GRADIENT,
            PlayerBackgroundStyle.GLOW_ANIMATED,
            PlayerBackgroundStyle.APPLE_MUSIC,
            PlayerBackgroundStyle.LIVE_MESH,
            PlayerBackgroundStyle.FLUID_MESH,
            PlayerBackgroundStyle.GLASS -> Color.White
            PlayerBackgroundStyle.STATIC,
            PlayerBackgroundStyle.CUSTOM_GRADIENT -> flatOnColor
        },
        label = "adaptivePrimary"
    )
    val additiveChrome = adaptivePrimary.luminance() > 0.5f
    val referenceBackground = playerBackground == PlayerBackgroundStyle.APPLE_MUSIC ||
        playerBackground == PlayerBackgroundStyle.GRADIENT
    val referenceGradient by rememberNowPlayingGradient(
        artworkMetadata?.thumbnailUrl?.takeIf { referenceBackground }, isHeroLook,
        CanvasVideoLooks.forMedia(artworkMetadata?.id).takeIf { videoActive && referenceBackground },
    )
    // Avoid color dodge on nearly black artwork, where it produces no visible contrast.
    // Chrome stays on while the gradient is still loading; waiting for it dropped the seek bar to plain
    // white for a second every time the controls came back.
    val vibrantTimeline = additiveChrome && (!referenceBackground || (referenceGradient?.let {
        Color(it.top).luminance() > 0.025f && Color(it.bottom).luminance() > 0.008f
    } ?: true))
    val adaptiveSecondary by animateColorAsState(
        targetValue = when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.onSurfaceVariant
            PlayerBackgroundStyle.BLUR -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.GRADIENT -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.GLOW_ANIMATED -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.APPLE_MUSIC -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.LIVE_MESH -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.FLUID_MESH -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.GLASS -> Color.White.copy(alpha = FlamingoSecondaryAlpha)
            PlayerBackgroundStyle.STATIC,
            PlayerBackgroundStyle.CUSTOM_GRADIENT -> flatOnColor.copy(alpha = FlamingoSecondaryAlpha)
        },
        label = "adaptiveSecondary"
    )
    // Use lower contrast for playback time labels than for track metadata.
    val adaptiveTime = if (additiveChrome) {
        Color.White.copy(alpha = FlamingoTimeAlpha)
    } else {
        adaptiveSecondary
    }
    // Artist text is real metadata, not decorative chrome: color-dodge on the
    // grey ink disappears over dark artwork. Keep a strong contrast-safe tier.
    val artistLabelColor = if (additiveChrome) adaptivePrimary.copy(alpha = 0.78f) else adaptiveSecondary
    val adaptiveSurface by animateColorAsState(
        targetValue = when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.surfaceVariant
            PlayerBackgroundStyle.BLUR,
            PlayerBackgroundStyle.GRADIENT,
            PlayerBackgroundStyle.GLOW_ANIMATED,
            PlayerBackgroundStyle.APPLE_MUSIC,
            PlayerBackgroundStyle.LIVE_MESH,
            PlayerBackgroundStyle.FLUID_MESH,
            PlayerBackgroundStyle.GLASS -> Color.White.copy(alpha = 0.2f)
            PlayerBackgroundStyle.STATIC,
            PlayerBackgroundStyle.CUSTOM_GRADIENT -> flatOnColor.copy(alpha = 0.2f)
        },
        label = "adaptiveSurface"
    )

    val (compactPlayerInTabView) = rememberPreference(CompactPlayerInTabViewKey, false)
    val isWidePlayerLayout =
        diyOrientation == DiyOrientation.LANDSCAPE &&
            !(LocalTabView.current && compactPlayerInTabView)
    // The wide layout has room for the controls beside the pane, so lyrics and the queue never hide them.
    val controlsShown = controlsVisible || isWidePlayerLayout

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                        if (event.changes.any { it.pressed }) {
                            lastInteractionTime = System.currentTimeMillis()
                            if (playerState != PlayerInternalState.QUEUE) controlsVisible = true
                        }
                    }
                }
            }
    ) {
        CompositionLocalProvider(
            LocalPooledGlass provides null,
        ) {
        val systemBarsTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        val wideLyrics = isWidePlayerLayout && playerState == PlayerInternalState.LYRICS
        val trackMetaRow: @Composable RowScope.(Modifier, Modifier, Modifier) -> Unit =
            { titleModifier, artistModifier, actionsModifier ->
        Column(modifier = Modifier.weight(1f)) {
            FadeSwapText(
                textMetadata?.title ?: "Unknown",
                style = AppTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 22.sp, letterSpacing = (-0.43).sp),
                color = adaptivePrimary,
                fontWeight = FontWeight.SemiBold,
                scroll = !state.isCollapsed,
                modifier = titleModifier.clickable {
                    state.collapseSoft()
                    mediaMetadata?.album?.id?.let { navController.navigate("album/$it") }
                }
            )
            FadeSwapText(
                textMetadata?.artists?.joinToString { it.name }.orEmpty(),
                style = AppTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 22.sp, letterSpacing = (-0.43).sp),
                color = artistLabelColor,
                fontWeight = FontWeight.Normal,
                scroll = !state.isCollapsed,
                modifier = artistModifier.clickable {
                    state.collapseSoft()
                    mediaMetadata?.artists?.firstOrNull()?.id?.let { navController.navigate("artist/$it") }
                }
            )
            DjReadout(
                modifier = Modifier.padding(top = 2.dp)
                    .flamingoChrome(additiveChrome)
                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                color = adaptiveSecondary,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = actionsModifier
                .flamingoChrome(additiveChrome)
                .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
        ) {
            IconButton(
                onClick = playerConnection::toggleLike,
                modifier = Modifier.size(40.dp),
            ) {
                LikeGlyphSwap(shownLike) { isLiked, songId ->
                    PlayerGlyph(
                        slot = if (isLiked) PlayerIconSlot.V2_LIKED else PlayerIconSlot.V2_LIKE,
                        fallback = if (isLiked) R.drawable.flamingo_favorited else R.drawable.flamingo_favorite,
                        // Show like state with the filled or outline glyph, not a color change.
                        tint = adaptivePrimary,
                        modifier = Modifier.size(28.dp).likePop(isLiked, songId),
                        contentDescription = "Like",
                    )
                }
            }


            IconButton(
                modifier = Modifier.size(40.dp),
                // Keep the player visible by using a popover instead of a sheet.
                onClick = { showV2Options = true }
            ) {
                mediaMetadata?.let {
                    V2OptionsMenuHost(
                        expanded = showV2Options,
                        onDismiss = { showV2Options = false },
                        mediaMetadata = it,
                        isLiked = shownLike?.second == true,
                        playerConnection = playerConnection,
                        navController = navController,
                        playerBottomSheetState = state,
                        onViewCredits = {
                            bottomSheetPageState.showFloating { ShowMediaInfo(it.id) }
                        },
                        onShareLyrics = { playerState = PlayerInternalState.LYRICS },
                        onOpenSleepTimer = { sleepTimer.showDialog.value = true },
                        onOpenAudioDevices = { showAudioDeviceBottomSheet = true },
                        onMore = {
                            menuState.show {
                                PlayerMenu(
                                    mediaMetadata = mediaMetadata,
                                    navController = navController,
                                    playerBottomSheetState = state,
                                    onShowDetailsDialog = {
                                        mediaMetadata?.id?.let { id -> bottomSheetPageState.showFloating { ShowMediaInfo(id) } }
                                    },
                                    onDismiss = menuState::dismiss
                                )
                            }
                        },
                    )
                }
                PlayerGlyph(
                    slot = PlayerIconSlot.V2_MORE,
                    fallback = R.drawable.flamingo_more,
                    tint = adaptivePrimary,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "Options",
                )
            }
        }
            }
        PlayerV2Frame(
            isWide = isWidePlayerLayout,
            // Hero Blend reaches the top edge, so the handle floats over artwork instead of reserving an inset.
            overlayHandle = isHeroLook && !videoActive,
            bleedPane = isHeroLook && !videoActive,
            fillPane = isWidePlayerLayout && playerState == PlayerInternalState.QUEUE,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isHeroLook && !videoActive) 0.dp else systemBarsTopPadding)
                // Wide Hero Blend reaches the bottom edge, so center controls across the full height.
                .padding(bottom = if (isHeroLook && isWidePlayerLayout && !videoActive) 0.dp
                    else WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                // Landscape: keep content out of side gesture bars/cutouts,
                // like the classic player's landscape branch does.
                .then(
                    if (isWidePlayerLayout) Modifier.windowInsetsPadding(
                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                    ) else Modifier
                ),
            handle = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (isHeroLook && !videoActive) systemBarsTopPadding + 16.dp else 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // The song / video switch replaces the grab pill whenever a video exists for the track.
                val videoSwitchEnabled by rememberPreference(com.convx.music.constants.ShowVideoSwitchKey, true)
                val showVideoSwitch = canWatchVideo && videoSwitchEnabled && playerState == PlayerInternalState.COVER
                if (!showVideoSwitch) Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(adaptivePrimary.copy(alpha = 0.3f))
                        .clickable { state.collapseSoft() }
                )
                val partyViewModel: com.convx.music.viewmodels.JamViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val party by partyViewModel.session.collectAsStateWithLifecycle()
                party?.let { PartyChip(it.code, it.members.size, adaptivePrimary) { state.collapseSoft(); navController.navigate("listen_together/party") } }
                if (showVideoSwitch) {
                    SongVideoModeControl(watchVideo, adaptivePrimary) { selected ->
                        if (selected && watchVideo && videoLookup is com.convx.music.playback.VideoStreamLookup.Unavailable) {
                            mediaMetadata?.id?.let(playerConnection::requestVideoStream)
                        }
                        selectVideo(selected)
                    }
                    if (watchVideo && videoLookup is com.convx.music.playback.VideoStreamLookup.Unavailable) {
                        Text(stringResource(R.string.video_unavailable), color = adaptiveSecondary, fontSize = 12.sp)
                    }
                }
                }
            }
            },
            pane = { paneModifier ->
            Box(paneModifier.fillMaxSize()) {
            val paneVideoStream = videoStream.takeIf { watchVideo }
            if (paneVideoStream != null) {
                YouTubeVideoPane(
                    stream = paneVideoStream,
                    connection = playerConnection,
                    videoController = canvasVideo,
                    visible = videoActive && !state.isCollapsed,
                    modifier = Modifier.fillMaxSize(),
                    onClose = { selectVideo(false) },
                )
            }
            // Always composed: in video mode the cover page holds only a frame-shaped anchor, so the
            // lyrics and queue headers morph out of the picture exactly like they do from artwork.
            val videoCover = watchVideo && videoStream != null
            run {
            androidx.compose.animation.SharedTransitionLayout(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AnimatedContent(
                        // In wide layout, keep artwork in the pane and show lyrics in the controls column.
                        targetState = if (wideLyrics) PlayerInternalState.COVER else playerState,
                        transitionSpec = {
                            // Use one slow, critically damped curve so pane changes feel continuous.
                            val enterFade = fadeIn(animationSpec = Motion.artwork())
                            val exitFade = fadeOut(animationSpec = Motion.artwork())
                            val enter = if (isHeroLook) enterFade else enterFade +
                                scaleIn(initialScale = 0.95f, animationSpec = Motion.artwork())
                            val exit = if (isHeroLook) exitFade else exitFade +
                                scaleOut(targetScale = 0.97f, animationSpec = Motion.artwork())
                            enter.togetherWith(exit).using(
                                androidx.compose.animation.SizeTransform(clip = false)
                            ).apply {
                                targetContentZIndex = if (targetState == PlayerInternalState.LYRICS || targetState == PlayerInternalState.QUEUE) 1f else 0f
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        label = "InternalWindow"
                    ) { targetState ->
                        if (targetState == PlayerInternalState.COVER && videoCover) {
                            val frameRatio = videoStream?.let { it.width.coerceAtLeast(1).toFloat() / it.height.coerceAtLeast(1) } ?: (16f / 9f)
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(frameRatio)
                                        .sharedElement(
                                            rememberSharedContentState(key = "coverArt"),
                                            animatedVisibilityScope = this@AnimatedContent,
                                            boundsTransform = { _, _ -> Motion.artwork() },
                                        ),
                                )
                            }
                        } else if (targetState == PlayerInternalState.COVER) {
                            // Use the shared root coordinates so stickers align across both player skins.
                            Box(modifier = Modifier.fillMaxSize()) {
                            if (showDiyStickers) {
                                AbsoluteFrameDiyStickers(
                                    rootCoordinates = diyRootCoordinates,
                                    orientation = diyOrientation,
                                    layout = diyLayout,
                                    zFilter = { it < 0 },
                                )
                            }
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Hero Blend starts flush from the top of the pane; Squared
                                // centres its card with a spacer above and below.
                                if (!isHeroLook) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                // Cache the multi-stop fade brush because drawWithContent runs every frame.
                                // In wide layouts, artwork fades toward the controls column.
                                val heroFadeBrush = remember(isWidePlayerLayout) {
                                    if (isWidePlayerLayout) androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colorStops = com.convx.music.ui.component.heroFadeOutStops(0.6f, 1f).toTypedArray(),
                                    ) else com.convx.music.ui.component.heroFadeOutBrush()
                                }
                                Box(
                                    modifier = if (isHeroLook) {
                                        Modifier
                                            .fillMaxWidth()
                                            .then(if (isWidePlayerLayout) {
                                                Modifier.fillMaxHeight()
                                            } else Modifier.layout { measurable, constraints ->
                                                val overlap = (constraints.maxWidth * 33f / 393f).roundToInt()
                                                // Reserve exactly the title row (8 + two 22pt lines + 16);
                                                // larger text grows it with fontScale.
                                                val artworkHeight = minOf(
                                                    (constraints.maxWidth * 524f / 393f).roundToInt(),
                                                    (constraints.maxHeight - (24.dp + 44.dp * fontScale).roundToPx() + overlap).coerceAtLeast(1),
                                                )
                                                val cover = measurable.measure(constraints.copy(
                                                    minHeight = artworkHeight,
                                                    maxHeight = artworkHeight,
                                                ))
                                                layout(cover.width, (artworkHeight - overlap).coerceAtLeast(0)) {
                                                    cover.placeRelative(0, 0)
                                                }
                                            })
                                            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                                            .drawWithContent {
                                                drawContent()
                                                drawRect(
                                                    brush = heroFadeBrush,
                                                    blendMode = BlendMode.DstIn
                                                )
                                            }
                                    } else if (isWidePlayerLayout) {
                                        // In side-by-side layouts, size the artwork from height to keep it square.
                                        Modifier
                                            .fillMaxHeight()
                                            .padding(31.dp)
                                            .aspectRatio(1f, matchHeightConstraintsFirst = true)
                                            .align(Alignment.CenterHorizontally)
                                    } else {
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp)
                                            .aspectRatio(1f)
                                    }
                                ) {
                                    // Read only the drag direction here; the animated offset is read during placement.
                                    val peekSign by remember {
                                        androidx.compose.runtime.derivedStateOf { kotlin.math.sign(heroDragOffset.value) }
                                    }
                                    // Keep the committed preview fixed until the new track arrives.
                                    val peekMetadata = if (committedFromId != null) committedPeek
                                        else if (peekSign > 0f) previousMediaMetadata else nextMediaMetadata
                                    if (peekMetadata != null && peekSign != 0f) {
                                        val peekDirection = peekSign
                                        val screenWidthPx = LocalConfiguration.current.screenWidthDp *
                                            androidx.compose.ui.platform.LocalDensity.current.density
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .graphicsLayer {
                                                    carouselCard(heroDragOffset.value - screenWidthPx * peekDirection, screenWidthPx)
                                                }
                                                .customSoftShadow(
                                                    elevation = playerThumbnailShadowElevation.dp,
                                                    cornerRadius = if (isHeroLook) 0.dp else 12.dp,
                                                    enabled = showPlayerThumbnailShadow && !isHeroLook
                                                )
                                                .background(adaptiveSurface, RoundedCornerShape(if (isHeroLook) 0.dp else 12.dp))
                                                .clip(RoundedCornerShape(if (isHeroLook) 0.dp else 12.dp))
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(peekMetadata.thumbnailUrl?.resize(1200, 1200))
                                                    .size(CoilSize.ORIGINAL)
                                                    .build(),
                                                contentDescription = "Cover Art",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .sharedElement(
                                                rememberSharedContentState(key = "coverArt"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ -> Motion.artwork() },
                                            )
                                            // Squared morphs into this card. Hero Bleed uses
                                            // its separate thumbnail beside the track title.
                                            .then(if (!isHeroLook) Modifier.fullArtworkMorphTarget(
                                                cornerRadiusPx = with(androidx.compose.ui.platform.LocalDensity.current) {
                                                    12.dp.toPx()
                                                },
                                                shadowPx = with(androidx.compose.ui.platform.LocalDensity.current) {
                                                    if (showPlayerThumbnailShadow) playerThumbnailShadowElevation.dp.toPx() else 0f
                                                },
                                                pauseShrinks = true,
                                            ) else Modifier.fullArtworkMorphTarget(
                                                cornerRadiusPx = 0f,
                                                travels = false,
                                                pauseShrinks = false,
                                                fadeOnly = true,
                                            ))
                                            // Keep this as a stable morph anchor; the animated child draws and scales the card.
                                            .then(
                                                rememberSwipeCardModifier(
                                                    enabled = (playerState == PlayerInternalState.COVER && !isHeroLook && playerBackground != PlayerBackgroundStyle.APPLE_MUSIC && !isListenTogetherGuest && committedFromId == null),
                                                    playerConnection = playerConnection,
                                                    offsetXAnimatable = heroDragOffset,
                                                    onCommit = { forward ->
                                                        committedFromId = artworkMetadata?.id ?: ""
                                                        committedPeek = if (forward) nextMediaMetadata else previousMediaMetadata
                                                    },
                                                )
                                            )
                                            .then(
                                                rememberDragToQueueModifier(
                                                    enabled = (playerState == PlayerInternalState.COVER && !isListenTogetherGuest),
                                                    state = state,
                                                    onOpenQueue = { playerState = PlayerInternalState.QUEUE },
                                                )
                                            )
                                    ) {
                                    // Hero Blend is edge-to-edge and full-screen from the top --
                                    // shrinking it on pause would tear it away from that fixed
                                    // frame, so only Squared's floating card does the pause-shrink.
                                    val heroScale by animateFloatAsState(
                                        // Use more damping on pause to avoid overshoot.
                                        targetValue = if (isPlaying || isHeroLook) 1f else 0.729f,
                                        animationSpec = spring(
                                            dampingRatio = if (isPlaying || isHeroLook) 0.6f else 0.95f,
                                            stiffness = 160f,
                                        ),
                                        label = "heroScale",
                                    )
                                    // Use the shared artwork morph for DJ crossfades and a short dissolve otherwise.
                                    val heroCrossfadeMs by playerConnection.crossfadeDurationMs.collectAsStateWithLifecycle()
                                    // Publish the resting scale without recomposing this player on every animation frame.
                                    androidx.compose.runtime.LaunchedEffect(Unit) {
                                        snapshotFlow { heroScale }.collect {
                                            if (!PlayerMorph.active) artworkRestScaleState.floatValue = it
                                        }
                                    }
                                    // Re-apply the target after the sheet settles in case playback state lagged.
                                    var heroWasUnsettled by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                                    androidx.compose.runtime.LaunchedEffect(state.isExpanded, state.isCollapsed, state.isDragging) {
                                        val settled = !state.isDragging && (state.isExpanded || state.isCollapsed)
                                        if (!settled) {
                                            heroWasUnsettled = true
                                        } else if (heroWasUnsettled && !PlayerMorph.active) {
                                            heroWasUnsettled = false
                                            artworkRestScaleState.floatValue =
                                                if (isHeroLook || playerConnection.player.playWhenReady) 1f else 0.729f
                                        }
                                    }
                                    // Bounds morph separately from scale, so animate scale on the
                                    // same curve to avoid a visible jump when opening a pane paused.
                                    val coverMorphFrac = this@AnimatedContent.transition.animateFloat(
                                        transitionSpec = {
                                            Motion.artwork()
                                        },
                                        label = "coverMorphFrac",
                                    ) { if (it == EnterExitState.Visible) 0f else 1f }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                // Read the resting scale at draw time; freeze it during the morph.
                                                val rest = artworkRestScaleState.floatValue
                                                val s = rest + (1f - rest) * coverMorphFrac.value
                                                scaleX = s
                                                scaleY = s
                                            }
                                            .customSoftShadow(
                                                elevation = playerThumbnailShadowElevation.dp,
                                                cornerRadius = if (isHeroLook) 0.dp else 12.dp,
                                                enabled = showPlayerThumbnailShadow && !isHeroLook
                                            )
                                            .background(adaptiveSurface, RoundedCornerShape(if (isHeroLook) 0.dp else 12.dp))
                                            .clip(RoundedCornerShape(if (isHeroLook) 0.dp else 12.dp))
                                            // Hero Blend: the artist page's live progressive foot over the
                                            // art AND the canvas video (one blur of the real pixels), then
                                            // the frame's outer fade hands it to the gradient, which starts
                                            // from this art's own bottom-edge colour.
                                            .then(if (isHeroLook && isWidePlayerLayout) Modifier.heroSideLiveBlur()
                                                else if (isHeroLook) Modifier.heroFootLiveBlur(boxExtent = 1f,
                                                videoFrame = { heroVideoFrame.intValue }) else Modifier)
                                    ) {
                                        ThumbnailImage(
                                            artworkUri = artworkMetadata?.thumbnailUrl?.resize(1200, 1200),
                                            cropArtwork = true,
                                            crossfadeMillis = heroCrossfadeMs.takeIf { it > 0 }?.toInt() ?: 200,
                                            // Same-song URL rung change (fallback ladder) must not
                                            // melt the image into itself.
                                            contentKey = artworkMetadata?.id,
                                            preloadUrls = heroPreloadUrls,
                                            morphOnChange = committedFromId == null,
                                            bitmapStore = paneArtworkStore,
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                        CanvasVideoSurface(
                                            controller = canvasVideo,
                                            modifier = Modifier.fillMaxSize(),
                                            onFrame = if (isHeroLook) ({ heroVideoFrame.intValue++ }) else null,
                                        )
                                    }
                                    }
                                }

                                // Keep leftover pane height above the title to prevent overlap with the timeline.
                                Spacer(modifier = Modifier.weight(1f))

                                if (!isWidePlayerLayout) Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                                        .padding(top = if (isHeroLook) 8.dp else 0.dp, bottom = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    trackMetaRow(
                                        Modifier.sharedBounds(rememberSharedContentState(key = "title"), animatedVisibilityScope = this@AnimatedContent, boundsTransform = { _, _ -> Motion.artwork() }),
                                        // In place, not in the overlay: the colour-dodge ink needs the page behind it to blend with.
                                        Modifier.sharedBounds(rememberSharedContentState(key = "artist"), animatedVisibilityScope = this@AnimatedContent, boundsTransform = { _, _ -> Motion.artwork() }, renderInOverlayDuringTransition = false),
                                        Modifier.sharedBounds(rememberSharedContentState(key = "actionButtons"), animatedVisibilityScope = this@AnimatedContent, boundsTransform = { _, _ -> Motion.artwork() }),
                                    )
                                    }
                                }
                            // The z >= 0 sticker band renders from Player.kt's overlayContent
                            // slot instead, a sibling of both player skins -- see that call
                            // site's comment.
                            }
                        } else if (targetState == PlayerInternalState.LYRICS || targetState == PlayerInternalState.QUEUE) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                AnimatedVisibility(
                                    visible = targetState != PlayerInternalState.LYRICS || controlsShown,
                                    enter = fadeIn(ControlsShowSpec()) +
                                        slideInVertically(ControlsShowSpec()) { -it / 3 } +
                                        androidx.compose.animation.expandVertically(ControlsShowSpec(), expandFrom = Alignment.Top),
                                    exit = fadeOut(ControlsHideSpec()) +
                                        slideOutVertically(ControlsHideSpec()) { -it / 3 } +
                                        androidx.compose.animation.shrinkVertically(ControlsHideSpec(), shrinkTowards = Alignment.Top),
                                ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer {
                                            translationY = (1f - auxMount.value) * -24.dp.toPx()
                                            alpha = auxMount.value
                                        }
                                        .padding(
                                            top = if (isHeroLook) {
                                                systemBarsTopPadding + 51.dp
                                            } else {
                                                24.dp
                                            },
                                            start = 30.dp,
                                            end = 30.dp,
                                            bottom = 8.dp,
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val headerMorphFrac = this@AnimatedContent.transition.animateFloat(
                                        transitionSpec = {
                                            Motion.artwork()
                                        },
                                        label = "headerMorphFrac",
                                    ) { if (it == EnterExitState.Visible) 1f else 0f }
                                    // Hero Bleed keeps its cover anchored and fades in a copy.
                                    // Squared shares the cover bounds with the page header.
                                    val headerArtworkModifier = if (!isHeroLook || targetState == PlayerInternalState.COVER) {
                                        Modifier.sharedElement(
                                                rememberSharedContentState(key = "coverArt"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ -> Motion.artwork() },
                                            )
                                    } else {
                                        Modifier
                                    }
                                    Box(
                                        modifier = headerArtworkModifier
                                            .size(60.dp)
                                            .then(if (!isHeroLook || targetState == PlayerInternalState.COVER) Modifier.fullArtworkMorphTarget(
                                                cornerRadiusPx = with(androidx.compose.ui.platform.LocalDensity.current) { 7.dp.toPx() },
                                                travels = !isHeroLook,
                                                shadowPx = with(androidx.compose.ui.platform.LocalDensity.current) {
                                                    if (showPlayerThumbnailShadow) (playerThumbnailShadowElevation / 2f).dp.toPx() else 0f
                                                },
                                                videoPoster = !isHeroLook,
                                                fadeEndpoints = !isHeroLook,
                                                fadeOnly = isHeroLook,
                                            ) else Modifier)
                                            .then(rememberDragToQueueModifier(
                                                enabled = true,
                                                state = state,
                                                onOpenQueue = {},
                                            ))
                                            .graphicsLayer {
                                                val rest = artworkRestScaleState.floatValue
                                                val s = rest + (1f - rest) * headerMorphFrac.value
                                                scaleX = s
                                                scaleY = s
                                            }
                                            .customSoftShadow(
                                                elevation = playerThumbnailShadowElevation.dp / 2f,
                                                cornerRadius = 7.dp,
                                                enabled = showPlayerThumbnailShadow
                                            )
                                            .background(adaptiveSurface, RoundedCornerShape(7.dp))
                                            .clip(RoundedCornerShape(7.dp))
                                            .then(
                                                if (targetState == PlayerInternalState.QUEUE) {
                                                    Modifier.clickable { playerState = PlayerInternalState.COVER }
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    ) {
                                        ThumbnailImage(
                                            artworkUri = artworkMetadata?.thumbnailUrl?.resize(1200, 1200),
                                            cropArtwork = true,
                                            crossfadeMillis = 450,
                                            contentKey = artworkMetadata?.id,
                                            bitmapStore = paneArtworkStore,
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                        CanvasVideoSurface(
                                            controller = canvasVideo,
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            textMetadata?.title ?: "Unknown",
                                            style = AppTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 22.sp, letterSpacing = (-0.43).sp),
                                            color = adaptivePrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = if (auxiliaryPageState != null && auxiliaryArtworkTransitionRunning()) {
                                                Modifier.auxiliaryBoundsMorph("title")
                                            } else Modifier.sharedBounds(
                                                rememberSharedContentState(key = "title"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ -> Motion.artwork() },
                                            )
                                        )
                                        Text(
                                            textMetadata?.artists?.joinToString { it.name }.orEmpty(),
                                            style = AppTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 22.sp, letterSpacing = (-0.43).sp),
                                            fontWeight = FontWeight.Normal,
                                            color = artistLabelColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = (if (auxiliaryPageState != null && auxiliaryArtworkTransitionRunning()) {
                                                Modifier.auxiliaryBoundsMorph("artist", inOverlay = false)
                                            } else Modifier.sharedBounds(
                                                rememberSharedContentState(key = "artist"),
                                                animatedVisibilityScope = this@AnimatedContent,
                                                boundsTransform = { _, _ -> Motion.artwork() },
                                                renderInOverlayDuringTransition = false,
                                            ))
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.sharedBounds(
                                            rememberSharedContentState(key = "actionButtons"),
                                            animatedVisibilityScope = this@AnimatedContent,
                                            boundsTransform = { _, _ -> Motion.artwork() },
                                        )
                                            .flamingoChrome(additiveChrome)
                                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                                    ) {
                                        IconButton(
                                            onClick = playerConnection::toggleLike,
                                            modifier = Modifier.size(40.dp),
                                        ) {
                                            LikeGlyphSwap(shownLike) { isLiked, songId ->
                                                PlayerGlyph(
                                                    slot = if (isLiked) PlayerIconSlot.V2_LIKED else PlayerIconSlot.V2_LIKE,
                                                    fallback = if (isLiked) R.drawable.flamingo_favorited else R.drawable.flamingo_favorite,
                                                    tint = adaptivePrimary,
                                                    modifier = Modifier.size(28.dp).likePop(isLiked, songId),
                                                    contentDescription = "Like",
                                                )
                                            }
                                        }

                                        IconButton(
                                            modifier = Modifier.size(40.dp),
                                            onClick = {
                                                if (targetState == PlayerInternalState.QUEUE) {
                                                    showV2Options = true
                                                } else {
                                                    mediaMetadata?.let { currentMetadata ->
                                                        menuState.show {
                                                            LyricsMenu(
                                                                lyricsProvider = { currentLyrics },
                                                                songProvider = { currentSong?.song },
                                                                mediaMetadataProvider = { currentMetadata },
                                                                onDismiss = menuState::dismiss,
                                                                onShowOffsetDialog = {
                                                                    bottomSheetPageState.show {
                                                                        ShowOffsetDialog(
                                                                            songProvider = { currentSong?.song }
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            PlayerGlyph(
                                                slot = PlayerIconSlot.V2_MORE,
                                                fallback = R.drawable.flamingo_more,
                                                tint = adaptivePrimary,
                                                modifier = Modifier.size(28.dp),
                                                contentDescription = "Options",
                                            )
                                            if (targetState == PlayerInternalState.QUEUE) mediaMetadata?.let {
                                                V2OptionsMenuHost(
                                                    expanded = showV2Options,
                                                    onDismiss = { showV2Options = false },
                                                    mediaMetadata = it,
                                                    isLiked = shownLike?.second == true,
                                                    playerConnection = playerConnection,
                                                    navController = navController,
                                                    playerBottomSheetState = state,
                                                    onViewCredits = {
                                                        bottomSheetPageState.showFloating { ShowMediaInfo(it.id) }
                                                    },
                                                    onShareLyrics = { playerState = PlayerInternalState.LYRICS },
                        onOpenSleepTimer = { sleepTimer.showDialog.value = true },
                        onOpenAudioDevices = { showAudioDeviceBottomSheet = true },
                                                    onMore = {
                                                        menuState.show {
                                                            PlayerMenu(
                                                                mediaMetadata = mediaMetadata,
                                                                navController = navController,
                                                                playerBottomSheetState = state,
                                                                onShowDetailsDialog = {
                                                                    mediaMetadata?.id?.let { id -> bottomSheetPageState.showFloating { ShowMediaInfo(id) } }
                                                                },
                                                                onDismiss = menuState::dismiss
                                                            )
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .graphicsLayer {
                                            translationY = (1f - auxMount.value) * size.height
                                            alpha = auxMount.value
                                        }
                                        .animateEnterExit(
                                            enter = slideInVertically(animationSpec = Motion.appear()) { it } + fadeIn(Motion.appear()),
                                            exit = fadeOut(animationSpec = Motion.appearExit()) + slideOutVertically(animationSpec = Motion.appearExit()) { it }
                                        )
                                        .then(
                                            if (targetState == PlayerInternalState.QUEUE) {
                                                Modifier.nestedScroll(queueControlsScroll)
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {
                                    if (targetState == PlayerInternalState.LYRICS) {
                                        Box(Modifier.fillMaxSize()) {
                                            Lyrics(
                                                sliderPositionProvider = { sliderPosition ?: position },
                                                showLyrics = true,
                                                introKey = lyricsIntroKey,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            // Shares the lyrics menu setting; shown only when an AI provider is configured.
                                            if (hasLyricsAiKey) {
                                                val translationsOn by com.convx.music.lyrics.LyricsTranslationHelper.hasActiveTranslations.collectAsStateWithLifecycle()
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(start = 32.dp, bottom = 12.dp)
                                                        .size(34.dp)
                                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                                        .background(Color.White.copy(alpha = if (translationsOn) 0.6f else 0.32f))
                                                        .clickable {
                                                            if (translationsOn) {
                                                                com.convx.music.lyrics.LyricsTranslationHelper.triggerClearTranslations()
                                                            } else {
                                                                com.convx.music.lyrics.LyricsTranslationHelper.triggerManualTranslation()
                                                            }
                                                        },
                                                ) {
                                                    SymbolIcon(symbol = sf("character.bubble"), contentDescription = stringResource(R.string.ai_lyrics_translation), size = 18.dp, tint = Color(0xFFF5F5F5))
                                                }
                                            }
                                        }
                                    } else {
                                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                        val queueSheetState = rememberBottomSheetState(
                                            dismissedBound = 0.dp,
                                            expandedBound = maxHeight,
                                            initialAnchor = expandedAnchor,
                                            animateOnMount = false,
                                        )
                                        // Ignore a restored dismissed state until the queue has opened once.
                                        var queueOpened by remember { mutableStateOf(false) }
                                        LaunchedEffect(queueSheetState.isExpanded) {
                                            if (queueSheetState.isExpanded) queueOpened = true
                                        }
                                        LaunchedEffect(queueSheetState.isDismissed) {
                                            if (queueOpened && queueSheetState.isDismissed) {
                                                playerState = PlayerInternalState.COVER
                                            }
                                        }
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            Queue(
                                                state = queueSheetState,
                                                playerBottomSheetState = state,
                                                navController = navController,
                                                background = Color.Transparent,
                                                onBackgroundColor = adaptivePrimary,
                                                TextBackgroundColor = adaptivePrimary,
                                                textButtonColor = adaptivePrimary,
                                                iconButtonColor = adaptivePrimary,
                                                pureBlack = false,
                                                showInlineLyrics = false,
                                                playerBackground = playerBackground,
                                                showHeaderRow = false,
                                                // Not draggable: v2's queue is reached and left through
                                                // the header thumbnail / queue toggle only, never a
                                                // drag gesture nested inside the already-gestural
                                                // player sheet.
                                                sheetDraggable = false,
                                                handleBack = false,
                                                applyTopSystemInset = false,
                                                modifier = Modifier.fillMaxSize(),
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
            }
            },
            controls = {
            if (videoActive || isWidePlayerLayout) {
                AnimatedVisibility(
                    visible = !wideLyrics || controlsShown,
                    enter = fadeIn(ControlsShowSpec()) +
                        slideInVertically(ControlsShowSpec()) { -it / 3 } +
                        androidx.compose.animation.expandVertically(ControlsShowSpec(), expandFrom = Alignment.Top),
                    exit = fadeOut(ControlsHideSpec()) +
                        slideOutVertically(ControlsHideSpec()) { -it / 3 } +
                        androidx.compose.animation.shrinkVertically(ControlsHideSpec(), shrinkTowards = Alignment.Top),
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(top = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    trackMetaRow(Modifier, Modifier, Modifier)
                }
                }
            }
            // Lyrics and the transport controls trade places by crossfade in one slot; the old
            // expand/shrink pair animated two weights against each other and stuttered.
            androidx.compose.animation.AnimatedContent(
                targetState = wideLyrics,
                transitionSpec = {
                    (fadeIn(androidx.compose.animation.core.tween(240)))
                        .togetherWith(fadeOut(androidx.compose.animation.core.tween(160)))
                        .using(androidx.compose.animation.SizeTransform(clip = false))
                },
                modifier = Modifier.fillMaxWidth().then(if (isWidePlayerLayout) Modifier.weight(1f, fill = false) else Modifier),
                label = "wide-lyrics-swap",
            ) { lyricsShown ->
            if (lyricsShown) {
                Box(Modifier.fillMaxSize()) {
                    com.convx.music.ui.utils.ProvideUnscaledDensity {
                        Lyrics(
                            sliderPositionProvider = { sliderPosition ?: position },
                            showLyrics = true,
                            introKey = lyricsIntroKey,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            } else
            AnimatedVisibility(
                    visible = controlsShown,
                    enter = fadeIn(animationSpec = ControlsShowSpec()) +
                            slideInVertically(animationSpec = ControlsShowSpec()) { it / 2 } +
                            androidx.compose.animation.expandVertically(animationSpec = ControlsShowSpec()),
                    exit = fadeOut(animationSpec = ControlsHideSpec()) +
                           slideOutVertically(animationSpec = ControlsHideSpec()) { it / 2 } +
                           androidx.compose.animation.shrinkVertically(animationSpec = ControlsHideSpec())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            )
                            .padding(horizontal = 32.dp)
                    ) {
                        // Apple Music Timeline Slider -- its own composable so the 20Hz position ticker
                    // recomposes only the bar and the two time labels, not the whole controls
                    // column (transport, volume, bottom row) on every tick. 120Hz panels.
                    V2SeekSection(
                        positionProvider = { sliderPosition ?: position },
                        durationProvider = { duration },
                        enabled = !isListenTogetherGuest,
                        onScrub = { sliderPosition = it },
                        onScrubEnd = {
                            sliderPosition?.let { pos ->
                                playerConnection.player.seekTo(pos)
                                position = pos
                                sliderPosition = null
                            }
                        },
                        additiveChrome = vibrantTimeline,
                        activeColor = adaptivePrimary,
                        timeColor = if (vibrantTimeline) adaptiveTime else adaptivePrimary.copy(alpha = 0.65f),
                        playerConnection = playerConnection,
                        onOpenQualitySettings = {
                            state.collapseSoft()
                            navController.navigate("settings/player")
                        },
                    )

                    if (showUpNext) {
                        UpNextSong(
                            playerConnection = playerConnection,
                            titleColor = adaptivePrimary,
                            subtitleColor = adaptiveSecondary,
                            modifier = Modifier.padding(vertical = 4.dp),
                            additiveChrome = additiveChrome,
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp)) // Space between the timeline and transport controls.

                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .widthIn(max = 302.dp) // 292pt glyph frame + 2x5: 61pt targets centred on 51pt glyphs
                            .fillMaxWidth()
                            .flamingoChrome(additiveChrome)
                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val previousInteraction = remember { MutableInteractionSource() }
                        IconButton(
                            onClick = { if (!isListenTogetherGuest && canSkipPrevious) playerConnection.requestSkip(forward = false) },
                            enabled = !isListenTogetherGuest && canSkipPrevious,
                            interactionSource = previousInteraction,
                            modifier = Modifier
                                .size(61.dp)
                                .transportPressDisc(previousInteraction, adaptivePrimary.copy(alpha = 0.16f))
                                .alpha(if (isListenTogetherGuest || !canSkipPrevious) 0.4f else 1f)
                        ) {
                            PlayerGlyph(
                                slot = PlayerIconSlot.V2_PREVIOUS,
                                fallback = R.drawable.flamingo_rewind,
                                tint = adaptivePrimary,
                                modifier = Modifier.size(44.dp).skipGlyphPress(previousInteraction, direction = -1f),
                                contentDescription = "Previous",
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        val playInteraction = remember { MutableInteractionSource() }
                        IconButton(
                            onClick = {
                                if (isListenTogetherGuest) {
                                    playerConnection.toggleMute()
                                } else {
                                    playerConnection.player.togglePlayPause()
                                }
                            },
                            interactionSource = playInteraction,
                            modifier = Modifier
                                .size(72.dp)
                                .transportPressDisc(playInteraction, adaptivePrimary.copy(alpha = 0.16f))
                        ) {
                            if (isListenTogetherGuest) {
                                SymbolIcon(symbol = if (isMuted) sf("speaker.slash.fill") else sf("speaker.wave.3.fill"), contentDescription = if (isMuted) "Unmute" else "Mute", size = 38.5.dp, tint = adaptivePrimary)
                            } else {
                                AnimatedPlayerGlyph(
                                    modifier = Modifier.playGlyphPress(playInteraction),
                                    isPlaying = glyphPlaying,
                                    playSlot = PlayerIconSlot.V2_PLAY,
                                    pauseSlot = PlayerIconSlot.V2_PAUSE,
                                    playFallback = R.drawable.flamingo_play,
                                    pauseFallback = R.drawable.flamingo_pause,
                                    tint = adaptivePrimary,
                                    playSize = 44.dp,
                                    pauseSize = 44.dp,
                                    playDescription = "Play",
                                    pauseDescription = "Pause",
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        val nextInteraction = remember { MutableInteractionSource() }
                        IconButton(
                            onClick = { if (!isListenTogetherGuest && canSkipNext) playerConnection.requestSkip(forward = true) },
                            enabled = !isListenTogetherGuest && canSkipNext,
                            interactionSource = nextInteraction,
                            modifier = Modifier
                                .size(61.dp)
                                .transportPressDisc(nextInteraction, adaptivePrimary.copy(alpha = 0.16f))
                                .alpha(if (isListenTogetherGuest || !canSkipNext) 0.4f else 1f)
                        ) {
                            PlayerGlyph(
                                slot = PlayerIconSlot.V2_NEXT,
                                fallback = R.drawable.flamingo_fforward,
                                tint = adaptivePrimary,
                                modifier = Modifier.size(44.dp).skipGlyphPress(nextInteraction, direction = 1f),
                                contentDescription = "Next",
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(23.dp)) // Leave room for the volume control's touch target.

                    // Fade the volume slider behind menus without removing its layout space.
                    val volumeCovered = menuState.isVisible || bottomSheetPageState.isVisible
                    val volumeAlpha by androidx.compose.animation.core.animateFloatAsState(
                        if (volumeCovered) 0f else 1f, label = "volumeCovered",
                    )
                    Row(
                        modifier = Modifier
                            // 28dp of speaker + gap on each side leaves a 285dp track matching
                            // the visible edges of the previous/next transport glyphs.
                            .widthIn(max = 341.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .graphicsLayer { alpha = volumeAlpha }
                            // Keep the row's layout space when hidden so DIY stickers do not jump.
                            .alpha(if (hideVolumeBar) 0f else 1f)
                            .then(if (hideVolumeBar) Modifier.clearAndSetSemantics {} else Modifier)
                            .flamingoChrome(additiveChrome, bleed = 8.dp)
                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val volumeInteraction = remember { MutableInteractionSource() }
                        val volumeDragged by volumeInteraction.collectIsDraggedAsState()
                        val volumePressed by volumeInteraction.collectIsPressedAsState()
                        val volumeActive = volumeDragged || volumePressed
                        var dragVolume by remember { mutableFloatStateOf(animatedVolume) }
                        LaunchedEffect(animatedVolume) { if (!volumeActive) dragVolume = animatedVolume }
                        val shownVolume = if (volumeActive) dragVolume else animatedVolume
                        val volumeTrackHeight by animateDpAsState(
                            targetValue = if (volumeActive) 14.dp else 10.dp,
                            animationSpec = spring(dampingRatio = 0.7f, stiffness = 600f),
                            label = "v2VolumeTrackHeight",
                        )
                        // The glyphs keep their slot size while the track grows; scaling them clipped the row edges.
                        PlayerQuietVolumeGlyph(
                            level = shownVolume,
                            tint = adaptivePrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                        Slider(
                            value = shownVolume,
                            onValueChange = { newValue ->
                                dragVolume = newValue
                                systemVolume = newValue
                                val targetVolume = (newValue * maxSystemVolume).toInt()
                                audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, targetVolume, 0)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !hideVolumeBar,
                            interactionSource = volumeInteraction,
                            thumb = { Spacer(Modifier.size(0.dp)) },
                            track = { sliderState ->
                                PlayerSliderTrack(
                                    sliderState = sliderState,
                                    colors = SliderDefaults.colors(
                                        activeTrackColor = adaptivePrimary.copy(alpha = 0.7f),
                                        inactiveTrackColor = adaptivePrimary.copy(alpha = 0.15f),
                                    ),
                                    trackHeight = volumeTrackHeight,
                                    showAutomix = false,
                                )
                            },
                        )
                        Spacer(Modifier.width(8.dp))
                        PlayerVolumeGlyph(
                            level = shownVolume,
                            tint = adaptivePrimary,
                        )
                    }
                    }
                }
            }
            // (Controls AnimatedVisibility closed above)

            // Bottom Utility Action Bar
            val bottomBarPad = if (isWidePlayerLayout) 0.dp else with(androidx.compose.ui.platform.LocalDensity.current) {
                (31.dp - WindowInsets.navigationBars.getBottom(this).toDp()).coerceAtLeast(4.dp)
            }
            AnimatedVisibility(
                visible = controlsShown,
                enter = fadeIn(animationSpec = ControlsShowSpec()) +
                        slideInVertically(animationSpec = ControlsShowSpec()) { it / 2 } +
                        androidx.compose.animation.expandVertically(animationSpec = ControlsShowSpec()),
                exit = fadeOut(animationSpec = ControlsHideSpec()) +
                       slideOutVertically(animationSpec = ControlsHideSpec()) { it / 2 } +
                       androidx.compose.animation.shrinkVertically(animationSpec = ControlsHideSpec())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        // Account for the volume row's touch target and the bottom navigation inset.
                        // The utility icons sit on the screen's thirds; only the volume
                        // slider uses the 32dp content gutter above.
                        .padding(top = 12.dp, bottom = bottomBarPad),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                // Use a lighter tint for icons over the vibrant artwork.
                val bottomTint = if (additiveChrome) adaptivePrimary.copy(alpha = 0.62f) else adaptiveSecondary
                // Chrome is applied per tab, not on the whole row: an additive layer would turn the selected
                // tab's dark glyph into a hole.
                val tabChrome = Modifier
                    .flamingoChrome(additiveChrome)
                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                val isLyricsActive = playerState == PlayerInternalState.LYRICS
                IconButton(
                    onClick = { playerState = if (isLyricsActive) PlayerInternalState.COVER else PlayerInternalState.LYRICS }
                ) {
                    V2TabGlyph(active = isLyricsActive, tint = bottomTint, chrome = tabChrome) { m, c ->
                        PlayerGlyph(
                            slot = PlayerIconSlot.V2_LYRICS,
                            fallbackSymbol = sf("quote.bubble"),
                            size = 24.dp,
                            bounce = true,
                            tint = c,
                            modifier = m,
                            contentDescription = "Lyrics",
                        )
                    }
                }
                }

                Box(Modifier.weight(1f).then(tabChrome), contentAlignment = Alignment.Center) {
                    if (showRoutePopover) V2RoutePopover(
                        onDismiss = { showRoutePopover = false },
                        title = textMetadata?.title.orEmpty(),
                        artist = textMetadata?.artists?.joinToString { it.name }.orEmpty(),
                        artworkUrl = artworkMetadata?.thumbnailUrl,
                        onMore = { showAudioDeviceBottomSheet = true },
                    )
                    if (audioOutput != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { showRoutePopover = true },
                                modifier = Modifier.background(Color.Transparent, RoundedCornerShape(12.dp))
                            ) {
                                SymbolIcon(symbol = sf(audioDeviceIcons.symbol(audioOutput?.name, audioOutput?.type)), bounceOnEnter = true, bounceOnChange = true, bounceOnTouch = true, contentDescription = "Audio Device", size = 22.dp, tint = bottomTint)
                            }
                            Text(
                                text = audioDeviceNames.displayName(audioOutput?.name),
                                style = AppTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                color = bottomTint,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.widthIn(max = 110.dp)
                            )
                        }
                    } else if (BuildConfig.CAST_AVAILABLE && enableGoogleCast) {
                        CastButton(
                            modifier = Modifier.size(48.dp),
                            tintColor = bottomTint,
                        )
                    } else {
                        IconButton(
                            onClick = { showRoutePopover = true },
                            modifier = Modifier.background(Color.Transparent, RoundedCornerShape(12.dp))
                        ) {
                            PlayerGlyph(
                                slot = PlayerIconSlot.V2_VOLUME_DOWN,
                                fallbackSymbol = sf("airplayaudio"),
                                tint = bottomTint,
                                modifier = Modifier.size(22.dp),
                                size = 20.dp,
                                contentDescription = "Speaker",
                            )
                        }
                    }
                }

                val isQueueActive = playerState == PlayerInternalState.QUEUE
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                IconButton(
                    onClick = { playerState = if (isQueueActive) PlayerInternalState.COVER else PlayerInternalState.QUEUE }
                ) {
                    V2TabGlyph(active = isQueueActive, tint = bottomTint, chrome = tabChrome) { m, c ->
                        PlayerGlyph(
                            slot = PlayerIconSlot.V2_QUEUE,
                            fallbackSymbol = sf("list.bullet"),
                            size = 24.dp,
                            bounce = true,
                            tint = c,
                            modifier = m,
                            contentDescription = "Queue",
                        )
                    }
                }
                }
                }
            }
            },
        )

        if (showAudioDeviceBottomSheet) {
            AudioDeviceMenu(onDismiss = { showAudioDeviceBottomSheet = false })
        }
        }
    }
}

/**
 * Seek bar, playback times, and quality control for Player V2.
 *
 * Position is read inside this scope so playback progress does not recompose the parent.
 * [V2TimeText] only re-lays out when the displayed second changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun V2SeekSection(
    positionProvider: () -> Long,
    durationProvider: () -> Long,
    enabled: Boolean,
    onScrub: (Long) -> Unit,
    onScrubEnd: () -> Unit,
    additiveChrome: Boolean,
    activeColor: Color,
    timeColor: Color,
    playerConnection: PlayerConnection,
    onOpenQualitySettings: () -> Unit,
) {
    val trackInteractionSource = remember { MutableInteractionSource() }
    val isTrackDragged by trackInteractionSource.collectIsDraggedAsState()
    val isTrackPressed by trackInteractionSource.collectIsPressedAsState()
    val trackHeight by animateDpAsState(
        targetValue = if (isTrackDragged || isTrackPressed) 12.dp else 7.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "trackScale",
    )
    val duration = durationProvider().coerceAtLeast(0)
    // Keep a 23dp visual slot while the Slider retains its 48dp touch target.
    Box(Modifier.fillMaxWidth().height(23.dp), contentAlignment = Alignment.Center) {
    Slider(
        value = positionProvider().coerceIn(0, duration.coerceAtLeast(1)).toFloat(),
        valueRange = 0f..duration.coerceAtLeast(1).toFloat(),
        onValueChange = { if (enabled) onScrub(it.toLong()) },
        onValueChangeFinished = { if (enabled) onScrubEnd() },
        enabled = enabled && duration > 0,
        interactionSource = trackInteractionSource,
        thumb = { Spacer(modifier = Modifier.size(0.dp)) },
        track = { sliderState ->
            PlayerSliderTrack(
                sliderState = sliderState,
                trackHeight = trackHeight,
                colors = PlayerSliderColors.getSliderColors(
                    activeColor = activeColor,
                    inactiveAlpha = if (additiveChrome) 0.05f else 0.3f,
                ),
                modifier = Modifier.flamingoChrome(additiveChrome).graphicsLayer {
                    if (additiveChrome) alpha = 0.45f
                },
            )
        },
        modifier = Modifier.fillMaxWidth().requiredHeight(48.dp),
    )
    }
    // Keep the quality badge centered between elapsed and remaining time.
    val ink = timeColor
    val timeModifier = Modifier.flamingoChrome(additiveChrome)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp), // Align with internal slider padding
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V2TimeText({ elapsedPlaybackSeconds(positionProvider()) }, prefix = "", color = ink, modifier = timeModifier, animate = isTrackDragged || isTrackPressed)
        AudioQualityBadge(
            playerConnection,
            activeColor,
            chrome = additiveChrome,
            onOpenQualitySettings = onOpenQualitySettings,
        )
        V2TimeText(
            { remainingPlaybackSeconds(durationProvider(), positionProvider()) },
            prefix = "-",
            color = ink,
            modifier = timeModifier,
            animate = isTrackDragged || isTrackPressed,
        )
    }
}

/** Shown at the top of the player while this device is in a listening party; tapping opens it. */
@Composable
private fun PartyChip(code: String, members: Int, color: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(color.copy(alpha = 0.14f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        SymbolIcon(symbol = sf("person.2.fill"), contentDescription = null, size = 14.dp, tint = color)
        Spacer(Modifier.width(6.dp))
        Text("$code · $members", color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** Draws a selected tab as a readable, softly tinted disc. */
@Composable
internal fun V2TabGlyph(active: Boolean, tint: Color, chrome: Modifier = Modifier, glyph: @Composable (Modifier, Color) -> Unit) {
    if (!active) {
        glyph(Modifier.size(32.dp).then(chrome), tint)
        return
    }
    // A real translucent fill stays visible over very dark art, where color-dodge
    // on mid-grey can collapse back into the background.
    val lightInk = tint.luminance() > 0.5f
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(41.dp)) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    if (lightInk) Color.White.copy(alpha = 0.34f) else tint.copy(alpha = 0.86f),
                    androidx.compose.foundation.shape.CircleShape,
                ),
        )
        glyph(Modifier.size(28.dp), if (lightInk) Color.Black.copy(alpha = 0.82f) else Color.White)
    }
}

/** Height the wide control stack is laid out for; shorter screens scale it down to fit. */
private val WideControlsDesignHeight = 380.dp

/** Elapsed or remaining time that updates once per displayed second. */
@Composable
private fun V2TimeText(seconds: () -> Long, prefix: String, color: Color, modifier: Modifier, animate: Boolean = false) {
    RollingPlaybackTime(seconds, prefix, color, modifier, animate = animate)
}

/**
 * The V17 player's outer frame, in one of two arrangements.
 *
 * Portrait stacks the pane over the controls, exactly as before. Landscape (and tab
 * view, unless it was set to stay compact) puts them side by side, which is what the
 * classic player already does at `Player.kt`'s `isLandscapeLayout` branch: artwork or
 * lyrics on one half, the whole control stack centred on the other. Stacking them on a
 * short, wide screen left the cover a letterbox strip and pushed the transport row off
 * the bottom.
 *
 * [pane] is handed the modifier that gives it its share of the space, because that is
 * `weight` in one arrangement and `fillMaxSize` inside a weighted Box in the other.
 */
@Composable
private fun PlayerV2Frame(
    isWide: Boolean,
    modifier: Modifier,
    handle: @Composable ColumnScope.() -> Unit,
    pane: @Composable (Modifier) -> Unit,
    controls: @Composable ColumnScope.() -> Unit,
    // Hero Blend: the handle floats over the pane's own top edge instead of pushing it
    // down, so the artwork can bleed all the way to the true top of the screen.
    overlayHandle: Boolean = false,
    /** Wide Hero Blend: the pane covers the whole left side instead of a square. */
    bleedPane: Boolean = false,
    /** Lyrics and the queue are text, so they use the whole half instead of a cover-sized square. */
    fillPane: Boolean = false,
) {
    val widePane = if (bleedPane || fillPane) Modifier.fillMaxSize() else Modifier.fillMaxHeight().aspectRatio(1f, matchHeightConstraintsFirst = true)
    BoxWithConstraints(modifier = modifier) {
    val controlsWidth = (maxWidth * 0.49f).coerceAtMost(417.dp)
    if (overlayHandle) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isWide) {
                // Right-align artwork beside controls, which are capped at 417dp.
                Row(
                    modifier = Modifier.fillMaxSize().padding(end = 21.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        pane(widePane)
                    }
                    Column(
                        modifier = Modifier.width(controlsWidth).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        com.convx.music.ui.utils.ProvideFittedDensity(this@BoxWithConstraints.maxHeight - 48.dp, WideControlsDesignHeight) {
                            this@Column.controls()
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    pane(Modifier.weight(1f))
                    controls()
                }
            }
            Column(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(), content = handle)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            handle()
            if (isWide) {
                // Right-align artwork beside controls, which are capped at 417dp.
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(end = 21.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                ) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        pane(widePane)
                    }
                    Column(
                        modifier = Modifier.width(controlsWidth).fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        com.convx.music.ui.utils.ProvideFittedDensity(this@BoxWithConstraints.maxHeight - 48.dp, WideControlsDesignHeight) {
                            this@Column.controls()
                        }
                    }
                }
            } else {
                pane(Modifier.weight(1f))
                controls()
            }
        }
    }
}

}

/** Tracks horizontal cover swipes and animates the next or previous card into place. */
@Composable
private fun rememberSwipeCardModifier(
    enabled: Boolean,
    playerConnection: PlayerConnection,
    // Hoisted so the caller can read the live drag position too, to slide the next/previous
    // song's own thumbnail in as a preview while the finger is still moving.
    offsetXAnimatable: Animatable<Float, AnimationVector1D>,
    onCommit: (forward: Boolean) -> Unit,
): Modifier {
    val (swipeThumbnail) = rememberPreference(SwipeThumbnailKey, true)
    val (swipeSensitivity) = rememberPreference(SwipeSensitivityKey, 0.73f)
    val coroutineScope = rememberCoroutineScope()
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    val configuration = LocalConfiguration.current
    val layoutDirection = androidx.compose.ui.platform.LocalLayoutDirection.current
    val travelPx = configuration.screenWidthDp * density
    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, density) {
        travelPx * (0.45f - 0.33f * swipeSensitivity.coerceIn(0f, 1f))
    }
    val currentOnCommit by rememberUpdatedState(onCommit)

    return Modifier
        .graphicsLayer { carouselCard(offsetXAnimatable.value, travelPx) }
        .then(
            if (enabled && swipeThumbnail) {
                Modifier.pointerInput(Unit) {
                    val velocityTracker = androidx.compose.ui.input.pointer.util.VelocityTracker()
                    var rawX = 0f
                    detectHorizontalDragGestures(
                        onDragStart = {
                            velocityTracker.resetTracking()
                            rawX = offsetXAnimatable.value
                        },
                        onDragCancel = {
                            coroutineScope.launch { offsetXAnimatable.animateTo(0f, SwipeHomeSpring) }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            velocityTracker.addPointerInputChange(change)
                            val delta = if (layoutDirection == LayoutDirection.Rtl) -dragAmount else dragAmount
                            rawX += delta
                            val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                            val canSkipNext = playerConnection.player.nextMediaItemIndex != -1
                            // Nothing that way: follow at a third, like pulling on elastic.
                            val blocked = (rawX > 0f && !canSkipPrevious) || (rawX < 0f && !canSkipNext)
                            val shown = if (blocked) rawX * 0.3f else rubberBand(rawX, travelPx)
                            coroutineScope.launch(start = kotlinx.coroutines.CoroutineStart.UNDISPATCHED) {
                                offsetXAnimatable.animateTo(shown, SwipeFollowSpring)
                            }
                        },
                        onDragEnd = {
                            val velocityX = velocityTracker.calculateVelocity().x.let {
                                if (layoutDirection == LayoutDirection.Rtl) -it else it
                            }
                            val current = offsetXAnimatable.value
                            val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                            val canSkipNext = playerConnection.player.nextMediaItemIndex != -1
                            val flung = kotlin.math.abs(velocityX) > SwipeFlingDpPerSecond * density &&
                                kotlin.math.sign(velocityX) == kotlin.math.sign(current)
                            val far = kotlin.math.abs(current) > autoSwipeThreshold
                            val forward = current < 0f
                            val allowed = if (forward) canSkipNext else canSkipPrevious
                            coroutineScope.launch {
                                if ((far || flung) && allowed && current != 0f) {
                                    currentOnCommit(forward)
                                    // Incoming card to centre = this card one travel away.
                                    offsetXAnimatable.animateTo(
                                        targetValue = if (forward) -travelPx else travelPx,
                                        animationSpec = SwipeLandSpring,
                                        initialVelocity = velocityX,
                                    )
                                    playerConnection.jumpBy(if (forward) 1 else -1)
                                } else {
                                    offsetXAnimatable.animateTo(0f, SwipeHomeSpring, initialVelocity = velocityX)
                                }
                            }
                        },
                    )
                }
            } else {
                Modifier
            }
        )
}

/** Past half a travel the card resists a little -- it feels held, not dragged on a rail. */
private fun rubberBand(x: Float, travelPx: Float): Float {
    val knee = travelPx * 0.5f
    val ax = kotlin.math.abs(x)
    return if (ax <= knee) x else kotlin.math.sign(x) * (knee + (ax - knee) * 0.55f)
}

/**
 * One card on the carousel wheel at horizontal offset [xPx]: a shallow arc -- dips, tilts
 * and shrinks a little with distance from centre. Visible, not theatrical.
 */
private fun androidx.compose.ui.graphics.GraphicsLayerScope.carouselCard(xPx: Float, travelPx: Float) {
    val t = if (travelPx > 0f) (xPx / travelPx).coerceIn(-1.4f, 1.4f) else 0f
    translationX = xPx
    translationY = CarouselDip.toPx() * t * t
    rotationZ = CarouselTiltDegrees * t
    val scale = 1f - 0.06f * kotlin.math.abs(t)
    scaleX = scale
    scaleY = scale
}

private const val GLYPH_PAUSE_SETTLE_MS = 120L
private val CarouselDip = 28.dp
private const val CarouselTiltDegrees = 7f
private const val SwipeFlingDpPerSecond = 450f
private val SwipeFollowSpring = spring<Float>(dampingRatio = 0.85f, stiffness = 1800f)
// Finish the landing when the card looks home instead of creeping through its last pixels.
private val SwipeLandSpring = spring<Float>(dampingRatio = 0.66f, stiffness = 240f, visibilityThreshold = 0.5f)
private val SwipeHomeSpring = spring<Float>(dampingRatio = 0.6f, stiffness = 380f)

/**
 * Opens the queue on an upward cover drag and moves the player sheet on a downward drag.
 * Separate horizontal and vertical detectors keep skip swipes from opening the queue.
 */
@Composable
private fun rememberDragToQueueModifier(
    enabled: Boolean,
    state: BottomSheetState,
    onOpenQueue: () -> Unit,
): Modifier {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val openThresholdPx = remember(density) { with(density) { 80.dp.toPx() } }
    val openQueue by rememberUpdatedState(onOpenQueue)
    return if (enabled) {
        Modifier.pointerInput(state, openThresholdPx) {
            var dragged = 0f
            var closing = false
            val velocityTracker = androidx.compose.ui.input.pointer.util.VelocityTracker()
            detectVerticalDragGestures(
                onDragStart = {
                    dragged = 0f
                    closing = false
                    velocityTracker.resetTracking()
                },
                onDragCancel = {
                    if (closing) {
                        state.dragClockNs[0] = 0L
                        state.performFling(0f, onDismiss = null)
                    }
                    dragged = 0f
                },
                onVerticalDrag = { change, dragAmount ->
                    dragged += dragAmount
                    velocityTracker.addPointerInputChange(change)
                    if (!closing && dragged > 0f) {
                        closing = true
                        state.onDragStart(fromMiniPlayer = false)
                        state.dragClockNs[0] = System.nanoTime()
                        state.dispatchRawDelta(dragged)
                    } else if (closing) {
                        state.dragClockNs[0] = System.nanoTime()
                        state.dispatchRawDelta(dragAmount)
                    }
                    change.consume()
                },
                onDragEnd = {
                    if (closing) {
                        state.dragClockNs[0] = 0L
                        state.performFling(-velocityTracker.calculateVelocity().y, onDismiss = null)
                    } else if (dragged <= -openThresholdPx) {
                        openQueue()
                    }
                    dragged = 0f
                },
            )
        }
    } else {
        Modifier
    }
}

/** Reference utility icons and elapsed/remaining labels use the secondary tier. */
private const val FlamingoSecondaryAlpha = 0.35f

/** Elapsed/remaining under the seek bar — quieter again than the artist line. */
private const val FlamingoTimeAlpha = 0.3f

/**
 * Preserve the reference's content alpha when applying the additive treatment;
 * secondary text already carries its own opacity and must not be dimmed twice.
 */
private const val AdditiveChromeContentAlpha = 1f

/** Shows a translucent disc while pressed, then fades it after release. */
@Composable
private fun Modifier.transportPressDisc(
    interactionSource: MutableInteractionSource,
    color: androidx.compose.ui.graphics.Color,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val disc by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
            // Apply press feedback immediately, then ease it away after release.
        animationSpec = if (pressed) tween(60) else spring(dampingRatio = 1f, stiffness = 260f),
        label = "transportDisc",
    )
    return drawBehind {
        if (disc > 0f) {
            drawCircle(
                color = color,
                radius = size.minDimension / 2f * (0.85f + 0.15f * disc),
                alpha = disc,
            )
        }
    }
}

/**
 * Play/pause glyph under the finger: shrinks toward 0.2x over ~200ms while held; on release
 * the swapped glyph springs back up (AnimatedPlayerGlyph's enter). Same capture as above.
 */
@Composable
private fun Modifier.playGlyphPress(interactionSource: MutableInteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.2f else 1f,
        animationSpec = if (pressed) {
            tween(220, easing = FastOutSlowInEasing)
        } else {
            // A touch more give on the rebound: playful, still settles inside ~350ms.
            spring(dampingRatio = 0.6f, stiffness = 420f)
        },
        label = "playGlyphPress",
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Skip glyph: dips slightly under the finger, and on release nudges in the skip direction
 * and back -- the reference's arrows step forward rather than scale.
 */
@Composable
private fun Modifier.skipGlyphPress(interactionSource: MutableInteractionSource, direction: Float): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 1f, stiffness = 900f),
        label = "skipGlyphScale",
    )
    val nudge = remember { androidx.compose.animation.core.Animatable(0f) }
    var wasPressed by remember { mutableStateOf(false) }
    LaunchedEffect(pressed) {
        if (wasPressed && !pressed) {
            nudge.animateTo(0f, keyframes {
                durationMillis = 340
                0f at 0
                1f at 130 using SeamlessEasing
                0f at 340 using SeamlessEasing
            })
        }
        wasPressed = pressed
    }
    val nudgePx = with(androidx.compose.ui.platform.LocalDensity.current) { 8.dp.toPx() }
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
        translationX = nudge.value * nudgePx * direction
    }
}

/**
 * Apple Music's favourite pop, keyframed off 60fps.design apple-music-animated-favourite-icon
 * (60fps, glyph bbox per frame, outline star = 25px):
 *  - 0..100ms: glyph crossfades outline -> filled, nearly invisible at ~60ms, 0.88x -> 1.08x
 *  - 100..283ms: swells to 1.56x, ease-out (27,31,32,34,35,36,36,38,38,39px)
 *  - 283..517ms: holds
 *  - 517..700ms: settles back to 1x
 * The same timeline plays on unliking (user ask), onto the outline glyph. The first
 * composition, and a song change, stay still.
 */
@Composable
private fun Modifier.likePop(isLiked: Boolean, songId: String?): Modifier {
    val scale = remember { androidx.compose.animation.core.Animatable(1f) }
    val alpha = remember { androidx.compose.animation.core.Animatable(1f) }
    // Re-keyed per song: skipping to a song with a different liked state is not a tap.
    var previous by remember(songId) { mutableStateOf(isLiked) }
    LaunchedEffect(isLiked, songId) {
        val changed = previous != isLiked
        previous = isLiked
        if (!changed) return@LaunchedEffect
        launch {
            scale.animateTo(1f, keyframes {
                durationMillis = 700
                0.88f at 0
                1.08f at 100 using LinearEasing
                1.56f at 283 using CubicBezierEasing(0.2f, 0.8f, 0.3f, 1f)
                1.56f at 517 using LinearEasing
                1f at 700 using FastOutSlowInEasing
            })
        }
        launch {
            alpha.animateTo(1f, keyframes {
                durationMillis = 110
                1f at 0
                0.08f at 60 using LinearEasing
                1f at 110 using LinearEasing
            })
        }
    }
    return graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        this.alpha = alpha.value
    }
}

/**
 * The like glyph across a track change: crossfades (eased, no visible edges) only when the
 * SONG changes. Keyed on the song id, so tapping like on the same song recomposes in place
 * and [likePop] plays exactly as before.
 */
@Composable
private fun LikeGlyphSwap(
    like: Pair<String, Boolean>?,
    content: @Composable (isLiked: Boolean, songId: String?) -> Unit,
) {
    androidx.compose.animation.AnimatedContent(
        targetState = like,
        contentKey = { it?.first },
        transitionSpec = {
            fadeIn(tween(320, easing = SeamlessEasing)) togetherWith fadeOut(tween(200, easing = SeamlessEasing))
        },
        label = "likeGlyphSwap",
    ) { state ->
        content(state?.second == true, state?.first)
    }
}

// Transport controls leaving / returning (queue scroll, lyrics idle), timed off
// 60fps.design apple-music-player-controls-collapse (60fps): hide runs ~217ms, show ~383ms.
// Slowed a little and eased at both ends so the tuck never reads as a snap.
internal fun <T> ControlsHideSpec(): FiniteAnimationSpec<T> = tween(300, easing = SeamlessEasing)
internal fun <T> ControlsShowSpec(): FiniteAnimationSpec<T> = spring(dampingRatio = 1f, stiffness = 200f)

/** Fades between track titles and scrolls text that does not fit on one line. */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun FadeSwapText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    color: androidx.compose.ui.graphics.Color,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
    // False while the sheet is collapsed: the player stays composed (alpha 0) under the
    // mini, and an unbounded marquee there kept a frame request alive forever -- the whole
    // app redrew every vsync on every screen, paused or not (traced: ~90fps idle).
    scroll: Boolean = true,
) {
    androidx.compose.animation.AnimatedContent(
        targetState = text,
        transitionSpec = {
            // Old line out, a short blank beat, new line in -- eased so neither edge shows.
            fadeIn(tween(380, delayMillis = 120, easing = SeamlessEasing)) togetherWith
                fadeOut(tween(180, easing = SeamlessEasing))
        },
        contentAlignment = Alignment.CenterStart,
        label = "fadeSwapText",
        modifier = modifier,
    ) { value ->
        Text(
            value,
            style = style,
            color = color,
            fontWeight = fontWeight,
            maxLines = 1,
            modifier = Modifier.basicMarquee(
                iterations = if (scroll) Int.MAX_VALUE else 0,
                initialDelayMillis = 1200,
            ),
        )
    }
}
