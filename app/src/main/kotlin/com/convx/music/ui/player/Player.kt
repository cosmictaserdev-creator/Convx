/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import com.convx.music.ui.component.volumeBars
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.ui.graphics.luminance
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.content.Intent
import android.view.WindowManager
import android.widget.Toast
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.AlertDialog
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import com.convx.music.ui.ios.compat.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import com.convx.music.ui.ios.compat.Icon
import androidx.compose.material3.IconButtonDefaults
import com.convx.music.ui.ios.compat.LocalContentColor
import com.convx.music.ui.ios.compat.AppTheme
import androidx.compose.material3.OutlinedIconButton
import com.convx.music.ui.ios.compat.ProvideTextStyle
import androidx.compose.runtime.produceState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.convx.music.ui.theme.ForcedDarkScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.toArgb
import coil3.size.Size as CoilSize
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.convx.music.LocalDatabase
import com.convx.music.LocalDownloadUtil
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.LocalTabView
import com.convx.music.R
import com.convx.music.constants.AudioQuality
import com.convx.music.constants.AudioQualityKey
import com.convx.music.constants.AppTextColorKey
import com.convx.music.constants.CropAlbumArtKey
import com.convx.music.constants.DarkModeKey
import com.convx.music.constants.CompactPlayerInTabViewKey
import com.convx.music.constants.CompactPlayerMaxWidth
import com.convx.music.constants.HidePlayerThumbnailKey
import com.convx.music.constants.HideVolumeBarKey
import com.convx.music.constants.EnableGoogleCastKey
import com.convx.music.constants.EnableLyricsThumbnailPlayPauseKey
import com.convx.music.constants.KeepScreenOn
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.PlayerBackgroundStyleKey
import com.convx.music.constants.PlayerGradientAngleKey
import com.convx.music.constants.PlayerGradientStopsKey
import com.convx.music.ui.theme.decodeGradientStops
import com.convx.music.ui.theme.tiltedGradient
import com.convx.music.constants.PlayerStaticColorKey
import com.convx.music.constants.PlayerButtonsStyle
import com.convx.music.constants.OneTapFullscreenLyricsKey
import com.convx.music.constants.FullscreenLyricsCollapseTopKey
import com.convx.music.constants.PlayerButtonsStyleKey
import com.convx.music.constants.PlayerHorizontalPadding
import com.convx.music.constants.QueuePeekHeight
import com.convx.music.constants.ShowUpNextKey
import com.convx.music.constants.SliderStyle
import com.convx.music.constants.SliderStyleKey
import com.convx.music.constants.SwipeLyricsKey
import com.convx.music.constants.ThumbnailCornerRadius
import com.convx.music.constants.ThumbnailRoundedShape
import com.convx.music.constants.UseAppleMusicPlayerKey
import com.convx.music.constants.UseNewPlayerDesignKey
import com.convx.music.constants.ShowAudioQualityBadgeKey
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.extensions.SwipeGesture
import com.convx.music.extensions.togglePlayPause
import com.convx.music.extensions.toggleRepeatMode
import com.convx.music.listentogether.RoomRole
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.ExoDownloadService
import com.convx.music.core.AudioDeviceMenu
import com.convx.music.core.AudioDeviceType
import com.convx.music.core.audioDeviceSymbol
import com.convx.music.ui.component.CastButton
import com.convx.music.BuildConfig
import com.convx.music.ui.component.DjReadout
import com.convx.music.ui.component.BlurredBackdropImage
import com.convx.music.ui.component.BottomSheet
import com.convx.music.ui.component.BottomSheetState
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.component.LocalMenuState
import com.convx.music.ui.component.Lyrics
import com.convx.music.ui.component.PlayerSliderTrack
import com.convx.music.ui.component.ResizableIconButton
import com.convx.music.ui.utils.resize
import com.convx.music.ui.utils.Motion
import com.convx.music.ui.player.customize.DiyDesignCanvas
import com.convx.music.ui.player.customize.DiyOrientation
import com.convx.music.ui.player.customize.DiyStickerLayer
import com.convx.music.ui.utils.flamingoChrome
import com.convx.music.ui.component.heroFootLiveBlur
import com.convx.music.ui.player.customize.AnimatedPlayerGlyph
import com.convx.music.ui.player.customize.PlayerGlyph
import com.convx.music.ui.player.customize.PlayerIconSlot
import com.convx.music.ui.player.customize.rememberDiyLayout
import com.convx.music.ui.player.customize.AbsoluteFrameDiyStickers
import com.convx.music.ui.player.customize.rememberPlayerIcon
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.PLAYER_BLUR_MULTIPLIER
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.ScrollingWaveformSeekBar
import com.convx.music.ui.component.rememberPlaybackFraction
import com.convx.music.ui.component.WavySlider
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.LocalAppBackdrop
import com.convx.music.ui.component.LocalBackdropLoopBucket
import com.convx.music.ui.component.backdrop.backdrops.backdropStaticRegion
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.rememberBottomSheetState
import com.convx.music.ui.component.rememberHeroTint
import com.convx.music.ui.menu.OldPlayerMenu
import com.convx.music.ui.menu.PlayerMenu
import com.convx.music.ui.component.VolumeSlider
import com.convx.music.ui.screens.settings.DarkMode
import com.convx.music.ui.theme.AppleTokens
import com.convx.music.ui.theme.PlayerColorExtractor
import com.convx.music.ui.utils.ShowMediaInfo
import com.convx.music.ui.utils.ShowOffsetDialog
import com.convx.music.utils.makeTimeString
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.max
import kotlin.math.roundToInt
import com.convx.music.ui.component.Icon as MIcon
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.DefaultDataSource
import android.view.TextureView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.extensions.metadata
import com.convx.music.ui.player.CanvasArtworkPlaybackCache
import com.convx.music.ui.player.normalizeCanvasArtistName
import com.convx.music.ui.player.normalizeCanvasSongTitle
import com.convx.music.ui.theme.PlayerSliderColors
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.utils.SpotifyCanvasLookup
import java.util.Locale

/** Poll interval for VideoLoopClock and the bucket width glass surfaces cache
 *  a looping canvas video's blur/lens output at (see LocalBackdropLoopBucket). */
private const val VideoLoopBucketMs = 100L

/** Upper bound on waiting for the player sheet before opening the queue inside it. */
private const val PARENT_SHEET_SETTLE_TIMEOUT_MS = 1_000L

/** Glow background redraw steps per 20s cycle: 600 = one step per ~33ms. */
private const val GLOW_STEPS = 600

/** Strength of white content added onto the background under `additiveChrome`. Below full so
 *  the blend doesn't clip to plain white and wash out the background's colour. */
private const val AdditiveChromeContentAlpha = 1f

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    pureBlack: Boolean,
    /** Editor-only: the DIY editor mounts this composable as its live canvas and draws its own
     *  sticker layer — off there so the saved layout isn't double-rendered underneath. */
    showDiyStickers: Boolean = true,
    /** Editor-only: previews the orientation the user is toggling, not the device's real one. */
    forcedOrientation: DiyOrientation? = null,
    /** Editor-only: mounted there a second time alongside the real instance, so window-level
     *  effects (status bar icon color, keep-screen-on) are suppressed to keep the two copies
     *  from fighting over that global state. */
    isPreviewOnly: Boolean = false,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    // Both player styles use the same sheet and drag gestures.
    val (useAppleMusicPlayer) = rememberPreference(UseAppleMusicPlayerKey, defaultValue = true)
    val v2Look by rememberEnumPreference(com.convx.music.constants.PlayerLookKey, com.convx.music.constants.PlayerLook.SQUARED)

    // Shared between the background slot (BackgroundVideoView polls into it) and the
    // control pills below — see VideoLoopClock's doc for why this is a plain clock.
    val videoLoopClock = rememberVideoLoopClock()

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) = rememberPreference(
        UseNewPlayerDesignKey,
        defaultValue = false
    )
    val (showAudioQualityBadge) = rememberPreference(
        ShowAudioQualityBadgeKey,
        defaultValue = true
    )
    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(HidePlayerThumbnailKey, false)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)
    val (compactPlayerInTabView) = rememberPreference(CompactPlayerInTabViewKey, false)
    // Mini player is unaffected — only the expanded content gets width-capped, and only
    // when the side rail is actually showing (tab view / wide screen).
    val playerContentMaxWidth = if (LocalTabView.current && compactPlayerInTabView) {
        CompactPlayerMaxWidth
    } else {
        Dp.Unspecified
    }
    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.GRADIENT
    )
    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val preferredDark = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    val useDarkTheme = com.convx.music.ui.theme.LocalForcedDark.current || preferredDark

    val dataSaverEnabled by rememberPreference(DataSaverEnabledKey, false)
    val enableCanvasPref by rememberPreference(CanvasThumbnailAnimationKey, true)
    // Canvas videos are the app's single largest data consumer; DataSaver and
    // battery saver both get them disabled.
    val powerSaverActive = remember {
        context.getSystemService(android.os.PowerManager::class.java)?.isPowerSaveMode ?: false
    }
    val enableCanvas = if (dataSaverEnabled || powerSaverActive) false else enableCanvasPref
    val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)

    val shouldUseDarkButtonColors = remember(playerBackground, useDarkTheme) {
        playerBackground != PlayerBackgroundStyle.DEFAULT || useDarkTheme
    }

    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val swipeLyrics by rememberPreference(SwipeLyricsKey, false)
    val enableLyricsThumbnailPlayPause by rememberPreference(EnableLyricsThumbnailPlayPauseKey, false)
    val isKeepScreenOn by rememberPreference(KeepScreenOn, false)
    val keepScreenOn = isPlaying && isKeepScreenOn

    PlayerWindowEffects(state, isPreviewOnly, playerBackground, useDarkTheme, keepScreenOn)
    val staticColor by rememberPreference(PlayerStaticColorKey, defaultValue = 0xFF1A1A1A.toInt())
    val gradientStopsRaw by rememberPreference(PlayerGradientStopsKey, defaultValue = "")
    val gradientStops = remember(gradientStopsRaw) { decodeGradientStops(gradientStopsRaw) }
    val gradientAngle by rememberPreference(PlayerGradientAngleKey, defaultValue = 90f)

    val onBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.secondary
        else -> AppTheme.colorScheme.onSurface
    }
    val useBlackBackground = remember(useDarkTheme, pureBlack) { useDarkTheme && pureBlack }

    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val currentSong by playerConnection.currentSong.collectAsStateWithLifecycle(initialValue =null)
    val automix by playerConnection.service.automixItems.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()
    val playerVolume by playerConnection.service.playerVolume.collectAsStateWithLifecycle()

    val (audioQuality) = rememberEnumPreference(
        AudioQualityKey,
        defaultValue = AudioQuality.AUTO
    )
    val currentFormat by playerConnection.currentFormat.collectAsStateWithLifecycle(initialValue =null)
    
    val playerFormat by rememberPlayerAudioFormat(playerConnection)

    val isLosslessStream = currentFormat?.mimeType?.contains("flac", ignoreCase = true) == true || 
                          playerFormat?.sampleMimeType?.contains("flac", ignoreCase = true) == true
    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.SLIM)

    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsStateWithLifecycle(initialValue =RoomRole.NONE)
    // "Guest" here means "may not drive playback", not merely "is not host":
    // with the room set to everyone-control a member drives playback too, so
    // keying the UI off the role alone left their controls greyed out.
    val canControlTogether = listenTogetherManager?.canControl?.collectAsStateWithLifecycle(initialValue = true)
    val isListenTogetherGuest = canControlTogether?.value == false
    
    // Cast state can be absent while the service is starting or stopping.
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castPosition by castHandler?.castPosition?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castDuration by castHandler?.castDuration?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castVolume by castHandler?.castVolume?.collectAsStateWithLifecycle() ?: remember { mutableFloatStateOf(1f) }
    
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying

    val positionState = remember { mutableLongStateOf(0L) }
    val durationState = remember { mutableLongStateOf(0L) }
    
    var position by positionState
    var duration by durationState
    
    val effectivePosition by remember {
        derivedStateOf {
            if (isCasting) {
                castPosition
            } else {
                position
            }
        }
    }
    
    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }
    var lastManualSeekTime by remember { mutableLongStateOf(0L) }
    
    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    // Keep the queue topped up from automix without mutating the player mid-composition.
    // isPreviewOnly: the DIY editor mounts a second copy of this composable: without this
    // guard both instances race to append the same automix item to the real queue.
    LaunchedEffect(canSkipNext, automix, isPreviewOnly) {
        if (isPreviewOnly) return@LaunchedEffect
        if (!canSkipNext && automix.isNotEmpty()) {
            playerConnection.service.addToQueueAutomix(automix[0], 0)
        }
    }

    val audioOutput by rememberActiveAudioOutput()
    val audioDeviceIcons = com.convx.music.core.rememberAudioDeviceIcons()
    val audioDeviceNames = com.convx.music.core.rememberAudioDeviceNames()

    val systemVolumeHolder = rememberSystemVolume()
    val audioManager = systemVolumeHolder.audioManager
    val maxSystemVolume = systemVolumeHolder.max
    val systemVolume by systemVolumeHolder.volume

    val defaultGradientColors = listOf(AppTheme.colorScheme.surface, AppTheme.colorScheme.surfaceVariant)
    val fallbackColor = AppTheme.colorScheme.surface.toArgb()

    // Match backdrop timing to the audio crossfade; use a slower default otherwise.
    val crossfadeMs by playerConnection.crossfadeDurationMs.collectAsStateWithLifecycle()
    val bgFadeMs = ambientTransitionMillis(crossfadeMs)
    // Sample video colors once per video, then transition the backdrop to them.
    // Transition the backdrop after the cover and lyrics during a track change.
    val trackChoreography = rememberTrackChoreography(mediaMetadata)
    val ambientMetadata = trackChoreography.ambient.value
    val videoLook = CanvasVideoLooks.forMedia(ambientMetadata?.id)

    suspend fun extractGradientColors(source: Any, style: PlayerBackgroundStyle): List<Color>? =
        withContext(Dispatchers.IO) {
            val bitmap = when (source) {
                is android.graphics.Bitmap -> source
                else -> {
                    val request = ImageRequest.Builder(context)
                        .data(source)
                        .size(200, 200)
                        .allowHardware(false)
                        .build()
                    runCatching { context.imageLoader.execute(request) }.getOrNull()?.image?.toBitmap()
                }
            } ?: return@withContext null
            val palette = withContext(Dispatchers.Default) {
                Palette.from(bitmap)
                    .maximumColorCount(8)
                    .resizeBitmapArea(100 * 100)
                    .generate()
            }
            if (style == PlayerBackgroundStyle.GLOW_ANIMATED) {
                listOf(
                    Color(palette.getVibrantColor(fallbackColor)),
                    Color(palette.getLightVibrantColor(fallbackColor)),
                    Color(palette.getDarkVibrantColor(fallbackColor)),
                    Color(palette.getMutedColor(fallbackColor)),
                    Color(palette.getLightMutedColor(fallbackColor)),
                    Color(palette.getDarkMutedColor(fallbackColor)),
                ).distinct()
            } else {
                PlayerColorExtractor.extractGradientColors(palette = palette, fallbackColor = fallbackColor)
            }
        }

    LaunchedEffect(ambientMetadata?.id, playerBackground, videoLook) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT || playerBackground == PlayerBackgroundStyle.GLOW_ANIMATED) {
            val currentMetadata = ambientMetadata ?: return@LaunchedEffect
            val look = videoLook
            val key = if (look != null) {
                "${currentMetadata.id}#video:$playerBackground:${look.liveFrameVersion}"
            } else {
                "${currentMetadata.id}:$playerBackground"
            }
            if (look != null) {
                val liveVideoPrefix = "${currentMetadata.id}#video:$playerBackground:"
                gradientColorsCache.keys.removeAll { it.startsWith(liveVideoPrefix) && it != key }
            }
            gradientColorsCache[key]?.let {
                gradientColors = it
                return@LaunchedEffect
            }
            val source: Any = look?.stitched ?: currentMetadata.thumbnailUrl ?: return@LaunchedEffect
            extractGradientColors(source, playerBackground)?.let {
                gradientColorsCache[key] = it
                gradientColors = it
            }
        } else {
            gradientColors = emptyList()
        }
    }

    // Precompute the next track's colors so its backdrop can transition immediately.
    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        val player = playerConnection.player
        val nextIndex = player.nextMediaItemIndex
        if (nextIndex == C.INDEX_UNSET || nextIndex >= player.mediaItemCount) return@LaunchedEffect
        val next = player.getMediaItemAt(nextIndex)
        val nextUrl = next.mediaMetadata.artworkUri?.toString() ?: return@LaunchedEffect
        when (playerBackground) {
            PlayerBackgroundStyle.FLUID_MESH -> prewarmMeshPalette(context, nextUrl)
            PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.GLOW_ANIMATED -> {
                val key = "${next.mediaId}:$playerBackground"
                if (gradientColorsCache[key] == null) {
                    extractGradientColors(nextUrl, playerBackground)?.let { gradientColorsCache[key] = it }
                }
            }
            else -> Unit
        }
    }

    val (showUpNext) = rememberPreference(ShowUpNextKey, defaultValue = false)
    val (appTextColorInt) = rememberPreference(AppTextColorKey, defaultValue = 0)
    val TextBackgroundColor by animateColorAsState(
        targetValue = if (appTextColorInt != 0) Color(appTextColorInt) else when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.onBackground
            PlayerBackgroundStyle.BLUR -> Color.White
            PlayerBackgroundStyle.GRADIENT -> Color.White
            PlayerBackgroundStyle.GLOW_ANIMATED -> Color.White
            PlayerBackgroundStyle.APPLE_MUSIC -> Color.White
            PlayerBackgroundStyle.LIVE_MESH -> Color.White
            PlayerBackgroundStyle.FLUID_MESH -> Color.White
            PlayerBackgroundStyle.GLASS -> Color.White
            PlayerBackgroundStyle.STATIC, PlayerBackgroundStyle.CUSTOM_GRADIENT -> Color.White
        },
        label = "TextBackgroundColor"
    )

    val icBackgroundColor by animateColorAsState(
        targetValue = when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.surface
            PlayerBackgroundStyle.BLUR -> Color.Black
            PlayerBackgroundStyle.GRADIENT -> Color.Black
            PlayerBackgroundStyle.GLOW_ANIMATED -> Color.Black
            PlayerBackgroundStyle.APPLE_MUSIC -> Color.Black
            PlayerBackgroundStyle.LIVE_MESH -> Color.Black
            PlayerBackgroundStyle.FLUID_MESH -> Color.Black
            PlayerBackgroundStyle.GLASS -> Color.Black
            PlayerBackgroundStyle.STATIC, PlayerBackgroundStyle.CUSTOM_GRADIENT -> Color.Black
        },
        label = "icBackgroundColor"
    )

    // Keep all valid candidates so playback can try the next one after a video error.
    val classicCanvas = rememberClassicCanvasState(mediaMetadata, playerBackground, canvasSource, useAppleMusicPlayer, enableCanvas)
    var canvasCandidates by classicCanvas.candidates
    var canvasCandidateIndex by classicCanvas.index
    val canvasArtwork = canvasCandidates.getOrNull(canvasCandidateIndex)

    val (textButtonColor, iconButtonColor) = when {
        playerBackground == PlayerBackgroundStyle.BLUR ||
        playerBackground == PlayerBackgroundStyle.GRADIENT ||
        playerBackground == PlayerBackgroundStyle.GLOW_ANIMATED ||
        playerBackground == PlayerBackgroundStyle.APPLE_MUSIC ||
        playerBackground == PlayerBackgroundStyle.LIVE_MESH ||
        playerBackground == PlayerBackgroundStyle.FLUID_MESH ||
        playerBackground == PlayerBackgroundStyle.GLASS -> {
            when (playerButtonsStyle) {
                PlayerButtonsStyle.DEFAULT -> Pair(Color.White, Color.Black)
                PlayerButtonsStyle.PRIMARY -> Pair(
                    AppTheme.colorScheme.primary,
                    AppTheme.colorScheme.onPrimary
                )
                PlayerButtonsStyle.TERTIARY -> Pair(
                    AppTheme.colorScheme.tertiary,
                    AppTheme.colorScheme.onTertiary
                )
            }
        }
        else -> {
            when (playerButtonsStyle) {
                PlayerButtonsStyle.DEFAULT ->
                    if (useDarkTheme) Pair(Color.White, Color.Black)
                    else Pair(Color.Black, Color.White)
                PlayerButtonsStyle.PRIMARY -> Pair(
                    AppTheme.colorScheme.primary,
                    AppTheme.colorScheme.onPrimary
                )
                PlayerButtonsStyle.TERTIARY -> Pair(
                    AppTheme.colorScheme.tertiary,
                    AppTheme.colorScheme.onTertiary
                )
            }
        }
    }

    val seekBarActiveColor = if (appTextColorInt != 0) Color(appTextColorInt) else textButtonColor
    val additiveChrome = textButtonColor.luminance() > 0.5f

    val (sideButtonContainerColor, sideButtonContentColor) = when {
        playerBackground == PlayerBackgroundStyle.BLUR ||
        playerBackground == PlayerBackgroundStyle.GRADIENT ||
        playerBackground == PlayerBackgroundStyle.GLASS -> {
            when (playerButtonsStyle) {
                PlayerButtonsStyle.DEFAULT -> Pair(
                    Color.White.copy(alpha = 0.2f),
                    Color.White
                )
                PlayerButtonsStyle.PRIMARY -> Pair(
                    AppTheme.colorScheme.primaryContainer,
                    AppTheme.colorScheme.onPrimaryContainer
                )
                PlayerButtonsStyle.TERTIARY -> Pair(
                    AppTheme.colorScheme.tertiaryContainer,
                    AppTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        playerBackground == PlayerBackgroundStyle.GLOW_ANIMATED -> {
            when (playerButtonsStyle) {
                PlayerButtonsStyle.DEFAULT -> Pair(
                    Color.White.copy(alpha = 0.2f),
                    Color.White
                )
                PlayerButtonsStyle.PRIMARY -> Pair(
                    AppTheme.colorScheme.primaryContainer,
                    AppTheme.colorScheme.onPrimaryContainer
                )
                PlayerButtonsStyle.TERTIARY -> Pair(
                    AppTheme.colorScheme.tertiaryContainer,
                    AppTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        else -> {
            when (playerButtonsStyle) {
                PlayerButtonsStyle.DEFAULT -> Pair(
                    AppTheme.colorScheme.surfaceContainerHighest,
                    AppTheme.colorScheme.onSurface
                )
                PlayerButtonsStyle.PRIMARY -> Pair(
                    AppTheme.colorScheme.primaryContainer,
                    AppTheme.colorScheme.onPrimaryContainer
                )
                PlayerButtonsStyle.TERTIARY -> Pair(
                    AppTheme.colorScheme.tertiaryContainer,
                    AppTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }

    val download by LocalDownloadUtil.current.get().getDownload(mediaMetadata?.id ?: "")
        .collectAsStateWithLifecycle(initialValue =null)

    val sleepTimer = rememberSleepTimerState(playerConnection)
    val sleepTimerEnabled = sleepTimer.enabled
    val sleepTimerTimeLeft by sleepTimer.timeLeft
    var showSleepTimerDialog by sleepTimer.showDialog
    SleepTimerDialogHost(playerConnection, sleepTimer)

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val classicPages = remember { PlayerAuxiliaryPageState() }
    val classicVideo = rememberClassicVideo(
        connection = playerConnection,
        metadata = mediaMetadata,
        isPlaying = isPlaying && !classicPages.showing,
        sheetCollapsed = state.isCollapsed,
        isPreviewOnly = isPreviewOnly,
    )
    val classicVideoStream = classicVideo.stream.takeIf { classicVideo.watch }
    val videoSwitchEnabled by rememberPreference(com.convx.music.constants.ShowVideoSwitchKey, true)
    var showRoutePopover by remember { mutableStateOf(false) }
    var showAudioDeviceBottomSheet by remember { mutableStateOf(false) }
    val enableGoogleCast by rememberPreference(EnableGoogleCastKey, defaultValue = true)
    var showInlineLyrics by remember {
        mutableStateOf(false)
    }

    var isFullScreen by remember {
        mutableStateOf(false)
    }
    val (oneTapFullscreenLyrics) = rememberPreference(OneTapFullscreenLyricsKey, defaultValue = false)
    val (fullscreenLyricsCollapseTop) = rememberPreference(FullscreenLyricsCollapseTopKey, defaultValue = true)
    val (hideVolumeBar) = rememberPreference(HideVolumeBarKey, defaultValue = false)
    // Poll local playback while playing, less often when the player is collapsed.
    LaunchedEffect(isPlaying, isCasting, state.isCollapsed) {
        if (!isCasting && isPlaying) {
            while (isActive) {
                delay(if (state.isCollapsed) 250L else 100L)
                if (sliderPosition == null) { // Only update if user isn't dragging
                    position = playerConnection.player.currentPosition
                    duration = playerConnection.player.duration
                }
            }
        }
    }
    
    LaunchedEffect(playbackState, mediaMetadata?.id) {
        if (!isCasting) {
            position = playerConnection.player.currentPosition
            duration = playerConnection.player.duration
        }
    }
    
    LaunchedEffect(isCasting, castPosition, castDuration) {
        if (isCasting && sliderPosition == null) {
            val timeSinceManualSeek = System.currentTimeMillis() - lastManualSeekTime
            if (timeSinceManualSeek > 1500) {
                position = castPosition
                if (castDuration > 0) duration = castDuration
            }
        }
    }

    // Mini-player actions arrive through PlayerConnection; previews have their own player instance.
    LaunchedEffect(useAppleMusicPlayer, isPreviewOnly) {
        if (useAppleMusicPlayer || isPreviewOnly) return@LaunchedEffect
        playerConnection.requestShowLyrics.collect { requested ->
            if (requested) {
                if (!state.isExpanded) {
                    withTimeoutOrNull(PARENT_SHEET_SETTLE_TIMEOUT_MS) {
                        snapshotFlow { state.isExpanded }.first { it }
                    }
                }
                classicPages.value = PlayerInternalState.LYRICS
                playerConnection.requestShowLyrics.value = false
            }
        }
    }
    LaunchedEffect(useAppleMusicPlayer, isPreviewOnly) {
        if (useAppleMusicPlayer || isPreviewOnly) return@LaunchedEffect
        playerConnection.requestShowQueue.collect { requested ->
            if (requested) {
                if (!state.isExpanded) {
                    withTimeoutOrNull(PARENT_SHEET_SETTLE_TIMEOUT_MS) {
                        snapshotFlow { state.isExpanded }.first { it }
                    }
                }
                classicPages.value = PlayerInternalState.QUEUE
                playerConnection.requestShowQueue.value = false
            }
        }
    }

    val bottomSheetBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.GLOW_ANIMATED, PlayerBackgroundStyle.APPLE_MUSIC, PlayerBackgroundStyle.GLASS ->
            AppTheme.colorScheme.surfaceContainer
        PlayerBackgroundStyle.LIVE_MESH, PlayerBackgroundStyle.FLUID_MESH ->
            Color.Black
        else ->
            if (useBlackBackground) Color.Black
            else AppTheme.colorScheme.surfaceContainer
    }

    // Read animated progress in graphicsLayer lambdas to avoid recomposing the whole player.
    // Keep this layer opaque during the shared morph; the overlay animates its fade.
    val backgroundAlpha = { if (PlayerMorph.active) 1f else state.progress.coerceIn(0f, 1f) }
    // Use a derived flag for composition so only threshold changes trigger recomposition.
    val backgroundVisible by remember(state) { derivedStateOf { state.progress > 0.01f } }

    // Capture this player's background so foreground glass buttons do not sample the screen behind it.
    val playerBackdrop = rememberLayerBackdrop()
    // Menus opened from the expanded player sample this layer, not the whole app.
    val backdropHolder = LocalPlayerBackdropHolder.current
    androidx.compose.runtime.SideEffect { backdropHolder?.backdrop = playerBackdrop }

    val diyLayout = rememberDiyLayout()
    // Shared by the sticker canvas below AND the thumbnail/controls layout branch
    // further down, so the editor's orientation toggle (forcedOrientation) previews
    // both consistently instead of stickers repositioning while the background
    // layout stays pinned to the device's real, physical orientation.
    val isLandscapeLayout = forcedOrientation?.let { it == DiyOrientation.LANDSCAPE }
        ?: (LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
    val diyOrientation = if (isLandscapeLayout) DiyOrientation.LANDSCAPE else DiyOrientation.PORTRAIT

    // Captured from a plain sibling Box in `overlayContent` below and read by the z < 0
    // sticker band (here and in Player_v2.kt) so both player skins resolve stickers
    // against the same frame -- see AbsoluteFrameDiyStickers' doc for why.
    var diyRootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // PlayerV2 reports its page so the sticker overlay can hide on Lyrics and Queue.
    var v17ShowingCover by remember { mutableStateOf(true) }

    PlayerMorphHostEffects(state, isPreviewOnly)

    BottomSheet(
        state = state,
        modifier = modifier,
        // iOS 27 container morph: the player grows out of the mini player as one card.
        morphEnabled = PlayerMorph.enabled,
        // Keep content composed while collapsed so it is ready when the opening morph starts.
        keepContentComposed = true,
        // The editor preview controls its own anchor; prevent its gestures from collapsing
        // or dismissing this sheet and stopping playback.
        isExpandable = !isPreviewOnly,
        contentMaxWidth = playerContentMaxWidth,
        // The sheet rounds to the device's real screen radius as it reaches full size, so
        // its corners sit on the glass rather than near it, then opens out to square once
        // settled. 0.dp on a device that reports no rounded corners, or in split screen.
        expandedCornerRadius = rememberScreenCornerRadius(),
        // Mini-to-full morph flight (see PlayerMorphHost): container owns the
        // card, artwork rides above it.
        overlayContent = {
            PlayerMorphHost(
                progress = { state.progress },
                thumbnailUrl = mediaMetadata?.thumbnailUrl,
                mediaId = mediaMetadata?.id,
                containerColor = AppTheme.colorScheme.surfaceContainer,
                expandedCornerRadius = rememberScreenCornerRadius(),
            )

            // Shared unshifted frame for positioning the z < 0 sticker band across
            // both player skins. PlayerV2 receives this frame as well.
            Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { diyRootCoordinates = it })

            // This band draws over artwork and controls. It fades out in Lyrics and
            // Queue views because it sits outside each skin's AnimatedContent.
            val stickersHidden = if (useAppleMusicPlayer) !v17ShowingCover else classicPages.showing
            AnimatedVisibility(
                visible = showDiyStickers && !stickersHidden && !state.isCollapsed,
                enter = fadeIn(Motion.appear()),
                exit = fadeOut(Motion.appear()),
            ) {
                DiyDesignCanvas(
                    orientation = diyOrientation,
                    modifier = Modifier
                        .fillMaxSize()
                        // Read progress in the draw phase to avoid recomposition on each
                        // frame. Follow the queue drag so stickers fade out with the sheet.
                        .graphicsLayer {
                            val p = state.progress
                            stickerMorphLayer(p, 1f, (state.expandedBound - state.value).toPx())
                        },
                ) {
                    DiyStickerLayer(
                        layout = diyLayout,
                        orientation = diyOrientation,
                        zFilter = { it >= 0 },
                        entranceProgress = { state.progress },
                    )
                }
            }
        },
        background = {
            val glassConfig = LocalGlassEffectConfig.current
            // Only GLASS uses the full-screen capture and blur chain.
            // The V2 player uses a dark artwork tint to keep its bright controls legible.
            val v2HeroBackground = useAppleMusicPlayer && playerBackground in listOf(
                PlayerBackgroundStyle.APPLE_MUSIC, PlayerBackgroundStyle.GRADIENT,
            )
            val glassActive = !v2HeroBackground && playerBackground == PlayerBackgroundStyle.GLASS && isGlassAllowed()
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Isolate this layer so sibling updates do not recapture the whole background.
                    .graphicsLayer()
                    .layerBackdrop(playerBackdrop, frozen = state.backdropFrozen)
                    .graphicsLayer()
                    .then(
                        if (glassActive) {
                            // GLASS samples the root backdrop with a stronger blur than the pills.
                            Modifier.liquidGlass(
                                config = glassConfig,
                                applyEdgeEffects = false,
                                blurRadiusDp = (glassConfig.blurRadius * PLAYER_BLUR_MULTIPLIER)
                                    .coerceAtMost(100f),
                            )
                        } else {
                            Modifier.background(bottomSheetBackgroundColor)
                        }
                    )
            ) {
                // V2 hero owns its art-edge gradient. Squared retains the selected
                // background, without a second hero cover beneath its own square.
                if (v2HeroBackground) {
                    NowPlayingBackground(
                        artworkUrl = ambientMetadata?.thumbnailUrl,
                        hero = v2Look == com.convx.music.constants.PlayerLook.HERO,
                        videoLook = CanvasVideoLooks.forMedia(ambientMetadata?.id),
                        transitionMillis = maxOf(bgFadeMs, 800),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = backgroundAlpha() }
                            .backdropStaticRegion(playerBackdrop),
                    )
                } else
                // Secondary dynamic layers (Artwork Blur, Mesh, Canvas) render ON TOP
                // of the base fill — except GLASS, whose liquid-glass base above
                // IS the look, so it adds nothing here.
                when (playerBackground) {
                    PlayerBackgroundStyle.GLASS -> {}
                    PlayerBackgroundStyle.BLUR -> {
                        AnimatedContent(
                            targetState = videoLook?.representative ?: ambientMetadata?.thumbnailUrl,
                            transitionSpec = {
                                fadeIn(tween(bgFadeMs, easing = SeamlessEasing))
                                    .togetherWith(fadeOut(tween(bgFadeMs, easing = SeamlessEasing)))
                            },
                            label = "blurBackground"
                        ) { thumbnailUrl ->
                            if (thumbnailUrl != null) {
                                // Track art stays static while seeking, avoiding repeated backdrop captures.
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer { alpha = backgroundAlpha() }
                                        .backdropStaticRegion(playerBackdrop)
                                ) {
                                    // Blur the cached artwork instead of the full screen on every frame.
                                    BlurredBackdropImage(
                                        model = thumbnailUrl,
                                        screenBlurRadiusDp = if (useDarkTheme) 150f else 100f,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f))
                                    )
                                }
                            }
                        }
                    }
                    PlayerBackgroundStyle.GRADIENT -> {
                        AnimatedContent(
                            targetState = gradientColors,
                            transitionSpec = {
                                fadeIn(tween(bgFadeMs, easing = SeamlessEasing))
                                    .togetherWith(fadeOut(tween(bgFadeMs, easing = SeamlessEasing)))
                            },
                            label = "gradientBackground"
                        ) { colors ->
                            if (colors.isNotEmpty()) {
                                val gradientColorStops = if (colors.size >= 3) {
                                    arrayOf(
                                        0.0f to colors[0],
                                        0.5f to colors[1],
                                        1.0f to colors[2]
                                    )
                                } else {
                                    arrayOf(
                                        0.0f to colors[0],
                                        0.6f to colors[0].copy(alpha = 0.7f),
                                        1.0f to Color.Black
                                    )
                                }
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = backgroundAlpha() }
                                        .backdropStaticRegion(playerBackdrop)
                                        .background(Brush.verticalGradient(colorStops = gradientColorStops))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }
                    PlayerBackgroundStyle.GLOW_ANIMATED -> {
                        AnimatedContent(
                            targetState = gradientColors,
                            transitionSpec = {
                                fadeIn(tween(bgFadeMs, easing = SeamlessEasing)) togetherWith
                                    fadeOut(tween(bgFadeMs, easing = SeamlessEasing))
                            },
                            label = "GlowAnimatedContent"
                        ) { colors ->
                            if (colors.isNotEmpty()) {
                                val progress: androidx.compose.runtime.State<Float> = if (isPlaying && !state.isCollapsed) {
                                    val infiniteTransition =
                                        rememberInfiniteTransition(label = "GlowAnimation")

                                    infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(20000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "glowProgress"
                                    )
                                } else {
                                    remember { mutableFloatStateOf(0f) }
                                }
                                // The 20s cycle barely moves per vsync: redraw ~30fps (6 shaders a
                                // frame) instead of every frame on 90-120Hz panels.
                                val glowStep = remember(progress) {
                                    derivedStateOf { (progress.value * GLOW_STEPS).toInt() }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = backgroundAlpha() }
                                        .drawBehind {
                                            val p = glowStep.value / GLOW_STEPS.toFloat()
                                            val width = size.width
                                            val height = size.height
                                            val baseColor = Color(0xFF050505)

                                            fun rotatedColorAt(index: Int): Color {
                                                val size = colors.size
                                                val idx = index.toFloat() + p * size
                                                val a = kotlin.math.floor(idx).toInt() % size
                                                val b = (a + 1) % size
                                                val frac = idx - kotlin.math.floor(idx)
                                                return androidx.compose.ui.graphics.lerp(
                                                    colors.getOrElse(a) { Color.DarkGray },
                                                    colors.getOrElse(b) { Color.DarkGray },
                                                    frac
                                                )
                                            }

                                            fun oscillate(
                                                min: Float,
                                                max: Float,
                                                phase: Float,
                                                speed: Float = 1f
                                            ): Float {
                                                val v = kotlin.math.sin(
                                                    2f * kotlin.math.PI.toFloat() * (p * speed + phase)
                                                )
                                                return min + (max - min) * ((v + 1f) * 0.5f)
                                            }

                                            val color1 = rotatedColorAt(0)
                                            val color2 = rotatedColorAt(1)
                                            val color3 = rotatedColorAt(2)
                                            val color4 = rotatedColorAt(3)
                                            val color5 = rotatedColorAt(4)
                                            val color6 = rotatedColorAt(5)

                                            val o1x = oscillate(0.0f, 1.0f, 0.00f, 1.0f)
                                            val o1y = oscillate(0.0f, 0.5f, 0.07f, 1.0f)
                                            val r1 = oscillate(0.8f, 1.6f, 0.12f, 1.0f)

                                            val o2x = oscillate(1.0f, 0.0f, 0.2f, 1.0f)
                                            val o2y = oscillate(0.5f, 1.0f, 0.25f, 1.0f)
                                            val r2 = oscillate(0.7f, 1.5f, 0.18f, 1.0f)

                                            val o3x = oscillate(0.2f, 0.8f, 0.33f, 1.0f)
                                            val o3y = oscillate(0.8f, 0.2f, 0.36f, 1.0f)
                                            val r3 = oscillate(0.6f, 1.4f, 0.29f, 1.0f)

                                            val o4x = oscillate(0.3f, 0.7f, 0.44f, 1.0f)
                                            val o4y = oscillate(0.2f, 0.8f, 0.41f, 1.0f)
                                            val r4 = oscillate(0.9f, 1.7f, 0.47f, 1.0f)

                                            val o5x = oscillate(0.4f, 0.6f, 0.55f, 1.0f)
                                            val o5y = oscillate(0.0f, 1.0f, 0.51f, 1.0f)
                                            val r5 = oscillate(0.7f, 1.5f, 0.58f, 1.0f)

                                            val o6x = oscillate(0.0f, 1.0f, 0.66f, 1.0f)
                                            val o6y = oscillate(0.5f, 0.7f, 0.62f, 1.0f)
                                            val r6 = oscillate(0.8f, 1.8f, 0.69f, 1.0f)

                                            val brush1 = Brush.radialGradient(
                                                colors = listOf(
                                                    color1.copy(alpha = 0.85f),
                                                    color1.copy(alpha = 0.5f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o1x, height * o1y),
                                                radius = width * r1
                                            )
                                            val brush2 = Brush.radialGradient(
                                                colors = listOf(
                                                    color2.copy(alpha = 0.8f),
                                                    color2.copy(alpha = 0.45f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o2x, height * o2y),
                                                radius = width * r2
                                            )
                                            val brush3 = Brush.radialGradient(
                                                colors = listOf(
                                                    color3.copy(alpha = 0.75f),
                                                    color3.copy(alpha = 0.4f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o3x, height * o3y),
                                                radius = width * r3
                                            )
                                            val brush4 = Brush.radialGradient(
                                                colors = listOf(
                                                    color4.copy(alpha = 0.7f),
                                                    color4.copy(alpha = 0.35f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o4x, height * o4y),
                                                radius = width * r4
                                            )
                                            val brush5 = Brush.radialGradient(
                                                colors = listOf(
                                                    color5.copy(alpha = 0.65f),
                                                    color5.copy(alpha = 0.3f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o5x, height * o5y),
                                                radius = width * r5
                                            )
                                            val brush6 = Brush.radialGradient(
                                                colors = listOf(
                                                    color6.copy(alpha = 0.6f),
                                                    color6.copy(alpha = 0.25f),
                                                    Color.Transparent
                                                ),
                                                center = Offset(width * o6x, height * o6y),
                                                radius = width * r6
                                            )

                                            drawRect(color = baseColor)
                                            drawRect(brush = brush1)
                                            drawRect(brush = brush2)
                                            drawRect(brush = brush3)
                                            drawRect(brush = brush4)
                                            drawRect(brush = brush5)
                                            drawRect(brush = brush6)
                                        }
                                )
                            }
                        }
                    }
                    PlayerBackgroundStyle.APPLE_MUSIC -> {
                        AnimatedContent(
                            targetState = ambientMetadata?.thumbnailUrl,
                            transitionSpec = {
                                fadeIn(tween(bgFadeMs, easing = SeamlessEasing))
                                    .togetherWith(fadeOut(tween(bgFadeMs, easing = SeamlessEasing)))
                            },
                            label = "appleMusicBackground"
                        ) { thumbnailUrl ->
                            if (thumbnailUrl != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = backgroundAlpha() }
                                        .backdropStaticRegion(playerBackdrop)
                                ) {
                                    // Squared retains its selected frost. The classic hero shares
                                    // Hero Bleed's artwork/video colour-matched gradient.
                                    if (useAppleMusicPlayer) androidx.compose.animation.Crossfade(
                                        targetState = videoLook?.representative ?: thumbnailUrl,
                                        animationSpec = tween(bgFadeMs, easing = SeamlessEasing),
                                        label = "appleMusicFrost",
                                    ) { frostModel ->
                                        BlurredBackdropImage(
                                            model = frostModel,
                                            screenBlurRadiusDp = 150f,
                                            decodeSize = 48,
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                    }
                                    else NowPlayingBackground(
                                        artworkUrl = thumbnailUrl,
                                        hero = true,
                                        videoLook = videoLook,
                                        transitionMillis = maxOf(bgFadeMs, 800),
                                        modifier = Modifier.fillMaxSize(),
                                    )

                                    // Layer 2: Clear Artwork (top 65%) with the shared foot knee 0.78:
                                    // opaque to 0.78, eased out by 1.0 — same seam as artist/V2 heroes.
                                    val clearArtworkAlpha by animateFloatAsState(
                                        // The lyrics and queue pages sit on the blurred background alone, as V2 shows them.
                                        targetValue = if (showInlineLyrics || classicPages.showing) 0f else 1f,
                                        animationSpec = tween(500),
                                        label = "clearArtworkAlpha"
                                    )

                                    // Classic clear-art frame: full width, 65% of screen height.
                                    // Built once: heroFadeOutBrush() allocates a 26-stop gradient,
                                    // and drawWithContent re-runs every draw pass -- unremembered,
                                    // this rebuilt it every frame the hero redraws (drag, fades).
                                    val heroFadeBrush = remember { com.convx.music.ui.component.heroFadeOutBrush() }
                                    val heroVideoFrame = remember { mutableIntStateOf(0) }
                                    if (!useAppleMusicPlayer) Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.65f) // Occupies top 65%
                                            .graphicsLayer {
                                                alpha = clearArtworkAlpha
                                                compositingStrategy = CompositingStrategy.Offscreen
                                            }
                                            .drawWithContent {
                                                drawContent()
                                                drawRect(
                                                    brush = heroFadeBrush,
                                                    blendMode = BlendMode.DstIn
                                                )
                                            }
                                            .heroFootLiveBlur(boxExtent = 1f, videoFrame = { heroVideoFrame.intValue })
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(thumbnailUrl.resize(1200, 1200))
                                                .size(CoilSize.ORIGINAL)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        if (enableCanvas && canvasArtwork != null && backgroundVisible) {
                                            BackgroundVideoView(
                                                mediaId = ambientMetadata?.id ?: "",
                                                videoUrl = canvasArtwork.animated ?: canvasArtwork.videoUrl.orEmpty(),
                                                isPlaying = isPlaying,
                                                onError = {
                                                    if (canvasCandidateIndex < canvasCandidates.lastIndex) {
                                                        canvasCandidateIndex++
                                                    } else {
                                                        canvasCandidateIndex = canvasCandidates.size
                                                    }
                                                },
                                                onReady = {
                                                    CanvasArtworkPlaybackCache.put(
                                                        "${ambientMetadata?.id}:${canvasSource.name}",
                                                        canvasArtwork
                                                    )
                                                },
                                                modifier = Modifier.fillMaxSize(),
                                                loopClock = videoLoopClock,
                                                onFrame = { heroVideoFrame.intValue++ },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    PlayerBackgroundStyle.LIVE_MESH -> {
                        val shouldAnimateLiveMesh = isPlaying && !state.isCollapsed
                        val anchorRotation: androidx.compose.runtime.State<Float>
                        val fastRotation: androidx.compose.runtime.State<Float>
                        val slowRotation: androidx.compose.runtime.State<Float>

                        if (shouldAnimateLiveMesh) {
                            val infiniteTransition = rememberInfiniteTransition(label = "liveMeshRotation")
                            
                            anchorRotation = infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = -360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(80000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "anchorRotation"
                            )
                            
                            fastRotation = infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(40000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "fastRotation"
                            )
                            
                            slowRotation = infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(60000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "slowRotation"
                            )
                        } else {
                            val zero = remember { mutableFloatStateOf(0f) }
                            anchorRotation = zero
                            fastRotation = zero
                            slowRotation = zero
                        }

                        AnimatedContent(
                            targetState = videoLook?.representative ?: ambientMetadata?.thumbnailUrl,
                            transitionSpec = {
                                val ms = maxOf(bgFadeMs, 1500)
                                fadeIn(tween(ms, easing = SeamlessEasing))
                                    .togetherWith(fadeOut(tween(ms, easing = SeamlessEasing)))
                            },
                            label = "liveMeshBackground"
                        ) { thumbnailUrl ->
                            if (thumbnailUrl != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = backgroundAlpha() }
                                        .graphicsLayer {
                                            // Scale up to avoid showing edges during rotation
                                            scaleX = 1.7f
                                            scaleY = 1.7f
                                        }
                                        // Single melt pass over the three baked-blur layers so the
                                        // rotating shapes read as mesh blobs. One pass on the
                                        // container, not per layer. No-op below API 31, where the
                                        // baked blur underneath still holds.
                                        .blur(32.dp)
                                ) {
                                    val matrix = remember { 
                                        val m = ColorMatrix()
                                        m.setToSaturation(1.8f) // Reduced to avoid neon look
                                        m
                                    }
                                    val colorFilter = remember(matrix) {
                                        ColorFilter.colorMatrix(matrix)
                                    }

                                    // Blur once at decode size, then rotate the small bitmap each frame.
                                    // Layer 1: The Anchor (Full Image, Counter-Clockwise)
                                    BlurredBackdropImage(
                                        model = thumbnailUrl,
                                        screenBlurRadiusDp = 100f,
                                        decodeSize = 160,
                                        colorFilter = colorFilter,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer { rotationZ = anchorRotation.value },
                                    )

                                    // Layer 2: Fast Rotating Crop (Top-Left)
                                    BlurredBackdropImage(
                                        model = thumbnailUrl,
                                        screenBlurRadiusDp = 120f,
                                        decodeSize = 160,
                                        alignment = Alignment.TopStart,
                                        colorFilter = colorFilter,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                rotationZ = fastRotation.value
                                                alpha = 0.6f
                                            },
                                    )

                                    // Layer 3: Slow Rotating Crop (Bottom-Right)
                                    BlurredBackdropImage(
                                        model = thumbnailUrl,
                                        screenBlurRadiusDp = 120f,
                                        decodeSize = 160,
                                        alignment = Alignment.BottomEnd,
                                        colorFilter = colorFilter,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer {
                                                rotationZ = slowRotation.value
                                                alpha = 0.5f
                                            },
                                    )

                                    // Global dark tint to prevent neon look + vertical gradient for depth
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.2f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.25f)
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        }
                    }
                    PlayerBackgroundStyle.FLUID_MESH -> {
                        // Four colour blobs off the sleeve rather than the sleeve itself.
                        // No AnimatedContent wrapper: the palette crossfades its own
                        // colours over 1.4s, so fading two whole meshes past each other
                        // would be the same transition drawn twice at double the cost.
                        val meshPalette = rememberArtworkMeshPalette(
                            ambientMetadata?.thumbnailUrl,
                            canvasFrame = videoLook?.stitched,
                        )
                        MeshGradientBackground(
                            palette = meshPalette,
                            trackKey = ambientMetadata?.id,
                            transitionMillis = maxOf(bgFadeMs, 1400),
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = backgroundAlpha() },
                        )
                    }
                    PlayerBackgroundStyle.STATIC -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = backgroundAlpha() }
                                .backdropStaticRegion(playerBackdrop)
                                .background(Color(staticColor))
                        )
                    }
                    PlayerBackgroundStyle.CUSTOM_GRADIENT -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = backgroundAlpha() }
                                .backdropStaticRegion(playerBackdrop)
                                .tiltedGradient(gradientStops, gradientAngle)
                        )
                    }
                    PlayerBackgroundStyle.DEFAULT -> Unit
                }

                // Show the app mark when the selected background needs artwork but none is available.
                if (ambientMetadata?.thumbnailUrl == null && !useAppleMusicPlayer &&
                    playerBackground !in setOf(
                        PlayerBackgroundStyle.STATIC,
                        PlayerBackgroundStyle.CUSTOM_GRADIENT,
                        PlayerBackgroundStyle.DEFAULT,
                        PlayerBackgroundStyle.GLASS,
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.convx_logo),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(if (useDarkTheme) Color.White else Color.Black),
                            modifier = Modifier.size(96.dp).alpha(0.4f),
                        )
                    }
                }

                // Status-bar scrim: black tint ramping from 0 at its own bottom edge
                // up to fully dark at the top, so the status bar icons stay legible
                // over bright artwork — same treatment as the app's own top bar.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent,
                                ),
                            )
                        )
                )

                // Keep stickers above the artwork and video, but below player controls.
                // Use shared root coordinates so both player layouts place them consistently.
                // Hide them during inline lyrics to keep them from covering the text.
                AnimatedVisibility(
                    visible = !classicPages.showing && showDiyStickers,
                    enter = fadeIn(Motion.appear()),
                    exit = fadeOut(Motion.appear()),
                ) {
                    AbsoluteFrameDiyStickers(
                        rootCoordinates = diyRootCoordinates,
                        orientation = diyOrientation,
                        layout = diyLayout,
                        zFilter = { it < 0 },
                    )
                }
            }
        },
        onDismiss = {
            playerConnection.service.clearAutomix()
            playerConnection.player.stop()
            playerConnection.player.clearMediaItems()
        },
        collapsedContent = {
            if (useAppleMusicPlayer) {
                AppleMiniPlayer(
                    progressState = remember(positionState, durationState) {
                        ProgressState(positionState, durationState)
                    }
                )
            } else {
                MiniPlayer(
                    positionState = positionState,
                    durationState = durationState
                )
            }
        },
    ) {
        if (useAppleMusicPlayer) {
            // Menus/popovers inside the player sample the player's own backdrop, not the
            // app root under the sheet (that showed the Home screen through the glass).
            androidx.compose.runtime.CompositionLocalProvider(
                com.convx.music.ui.ios.foundation.LocalIosBackdrop provides playerBackdrop,
                LocalAppBackdrop provides playerBackdrop,
                com.convx.music.ui.component.LocalPooledGlass provides null,
            ) {
            PlayerV2(
                state = state,
                navController = navController,
                modifier = Modifier,
                showDiyStickers = showDiyStickers,
                forcedOrientation = forcedOrientation,
                isPreviewOnly = isPreviewOnly,
                diyRootCoordinates = diyRootCoordinates,
                onCoverShowingChange = { v17ShowingCover = it },
            )
            }
            return@BottomSheet
        }
        PlayerAuxiliaryPages(
            pages = classicPages,
            state = state,
            navController = navController,
            forcedOrientation = forcedOrientation,
            isPreviewOnly = isPreviewOnly,
        ) {
        // Queue and lyrics open inside the classic layout, keeping its background and controls.
        val (coverArtworkStyle) = com.convx.music.utils.rememberEnumPreference(
            com.convx.music.constants.PlayerArtworkStyleKey,
            defaultValue = com.convx.music.constants.PlayerArtworkStyle.CARD,
        )
        val coverStyle = coverArtworkStyle.coverStyle()
        androidx.activity.compose.BackHandler(enabled = classicPages.showing) {
            classicPages.value = PlayerInternalState.COVER
        }
        // Scrolling the queue down hides the controls and scrolling up brings them back, like V2.
        var classicControlsVisible by remember { mutableStateOf(true) }
        LaunchedEffect(classicPages.showing, classicPages.value) { classicControlsVisible = true }
        val classicQueueFlipPx = with(LocalDensity.current) { 48.dp.toPx() }
        val classicQueueScroll = remember(classicQueueFlipPx) {
            object : androidx.compose.ui.input.nestedscroll.NestedScrollConnection {
                var travel = 0f
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: androidx.compose.ui.input.nestedscroll.NestedScrollSource,
                ): Offset {
                    if (source == androidx.compose.ui.input.nestedscroll.NestedScrollSource.UserInput) {
                        val dy = consumed.y + available.y
                        val continuesRun = travel == 0f || (dy < 0f) == (travel < 0f)
                        travel = if (dy != 0f && continuesRun) travel + dy else dy
                        if (travel <= -classicQueueFlipPx) {
                            classicControlsVisible = false
                            travel = 0f
                        } else if (travel >= classicQueueFlipPx) {
                            classicControlsVisible = true
                            travel = 0f
                        }
                    }
                    return Offset.Zero
                }
            }
        }
        val classicHeader: @Composable () -> Unit = {
            ClassicPageHeader(
                artworkUrl = mediaMetadata?.thumbnailUrl,
                mediaId = mediaMetadata?.id,
                coverStyle = coverStyle,
                isPlaying = isPlaying,
                cropArtwork = cropAlbumArt,
                title = mediaMetadata?.title.orEmpty(),
                artist = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                textColor = TextBackgroundColor,
                onClick = { classicPages.value = PlayerInternalState.COVER },
                actions = {
                    ForcedDarkScope {
                        GlassCircleButton(
                            onClick = {
                                menuState.show {
                                    val onShowDetails: () -> Unit = {
                                        mediaMetadata?.id?.let { id ->
                                            bottomSheetPageState.showFloating { ShowMediaInfo(id) }
                                        }
                                        Unit
                                    }
                                    if (useNewPlayerDesign) {
                                        PlayerMenu(
                                            mediaMetadata = mediaMetadata,
                                            navController = navController,
                                            playerBottomSheetState = state,
                                            onShowDetailsDialog = onShowDetails,
                                            onDismiss = menuState::dismiss,
                                        )
                                    } else {
                                        OldPlayerMenu(
                                            mediaMetadata = mediaMetadata,
                                            navController = navController,
                                            playerBottomSheetState = state,
                                            onShowDetailsDialog = onShowDetails,
                                            onDismiss = menuState::dismiss,
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp),
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                            PlayerGlyph(
                                slot = PlayerIconSlot.MORE,
                                fallback = R.drawable.more_horiz,
                                tint = TextBackgroundColor,
                                modifier = Modifier.size(23.dp),
                                contentDescription = "More options",
                            )
                        }
                    }
                    ForcedDarkScope {
                        GlassCircleButton(
                                onClick = playerConnection::toggleLike,
                                modifier = Modifier.size(40.dp),
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                            val liked = currentSong?.song?.liked == true
                            PlayerGlyph(
                                slot = if (liked) PlayerIconSlot.LIKED else PlayerIconSlot.LIKE,
                                fallback = if (liked) R.drawable.favorite else R.drawable.favorite_border,
                                tint = if (liked) AppTheme.colorScheme.error else TextBackgroundColor,
                                modifier = Modifier.size(23.dp),
                                contentDescription = "Like",
                            )
                        }
                    }
                },
            )
        }
        val classicPage: @Composable (PlayerInternalState) -> Unit = { page ->
            if (page == PlayerInternalState.LYRICS) {
                InlineLyricsView(
                    mediaMetadata = mediaMetadata,
                    showLyrics = true,
                    positionProvider = { effectivePosition },
                )
            } else {
                androidx.compose.foundation.layout.BoxWithConstraints(Modifier.fillMaxSize().nestedScroll(classicQueueScroll)) {
                    val queueSheetState = rememberBottomSheetState(
                        dismissedBound = 0.dp,
                        expandedBound = maxHeight,
                        initialAnchor = com.convx.music.ui.component.expandedAnchor,
                        animateOnMount = false,
                    )
                    // Ignore a restored dismissed state until the queue has opened once.
                    var queueOpened by remember { mutableStateOf(false) }
                    LaunchedEffect(queueSheetState.isExpanded) {
                        if (queueSheetState.isExpanded) queueOpened = true
                    }
                    LaunchedEffect(queueSheetState.isDismissed) {
                        if (queueOpened && queueSheetState.isDismissed) classicPages.value = PlayerInternalState.COVER
                    }
                    Queue(
                        state = queueSheetState,
                        playerBottomSheetState = state,
                        navController = navController,
                        background = Color.Transparent,
                        onBackgroundColor = onBackgroundColor,
                        TextBackgroundColor = TextBackgroundColor,
                        textButtonColor = textButtonColor,
                        iconButtonColor = iconButtonColor,
                        pureBlack = false,
                        showInlineLyrics = false,
                        playerBackground = playerBackground,
                        showHeaderRow = false,
                        sheetDraggable = false,
                        handleBack = false,
                        applyTopSystemInset = false,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
        val controlsContent: @Composable ColumnScope.(MediaMetadata) -> Unit = { mediaMetadata ->
            val playPauseRoundness by animateDpAsState(
                targetValue = if (isPlaying) 24.dp else 36.dp,
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 1100f),
                label = "playPauseRoundness",
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlayerHorizontalPadding),
            ) {
                AnimatedContent(
                    targetState = showInlineLyrics,
                    transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(140)) },
                    label = "ThumbnailAnimation"
                ) { showLyrics ->
                    if (showLyrics) {
                        Row {
                            if (hidePlayerThumbnail) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(ThumbnailRoundedShape)
                                        .background(AppTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SymbolIcon(symbol = sf("music.note"), contentDescription = null, size = 32.dp, tint = textButtonColor.copy(alpha = 0.7f))
                                }
                            } else {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(ThumbnailRoundedShape)
                                        .clickable(enabled = isFullScreen && enableLyricsThumbnailPlayPause) {
                                            playerConnection.togglePlayPause()
                                        }
                                ) {
                                    AsyncImage(
                                        model = mediaMetadata.thumbnailUrl,
                                        contentDescription = null,
                                        contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
                                        placeholder = painterResource(R.drawable.thumbnail_fallback),
                                        error = painterResource(R.drawable.thumbnail_fallback),
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    if (isFullScreen && enableLyricsThumbnailPlayPause && !showInlineLyrics) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = if (isPlaying) 0f else 0.4f))
                                        )

                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = !isPlaying,
                                            enter = fadeIn(),
                                            exit = fadeOut()
                                        ) {
                                            PlayerGlyph(
                                                slot = if (playbackState == Player.STATE_ENDED)
                                                    PlayerIconSlot.REPLAY else PlayerIconSlot.PLAY,
                                                fallback = if (playbackState == Player.STATE_ENDED)
                                                    R.drawable.replay else R.drawable.play_applemusic,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(0.dp))
                    }
                }
                // Title and artist travel up into the page header while a page is open, like V2.
                androidx.compose.animation.AnimatedVisibility(
                    visible = !classicPages.showing,
                    enter = fadeIn(Motion.artwork()) + androidx.compose.animation.expandVertically(Motion.artwork()),
                    exit = fadeOut(Motion.artwork()) + shrinkVertically(Motion.artwork()),
                    modifier = Modifier.weight(1f),
                ) {
                CompositionLocalProvider(LocalAuxiliaryVisibilityScope provides this) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .SwipeGesture(
                            enabled = isFullScreen && swipeLyrics,
                            onSwipeRight = { playerConnection.seekToPrevious() },
                            onSwipeLeft = { playerConnection.seekToNext() }
                        )
                ) {
                    AnimatedContent(
                        targetState = mediaMetadata.title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "",
                        modifier = Modifier.auxiliaryBoundsMorph("title"),
                    ) { title ->
                        Text(
                            text = title,
                            style = AppTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextBackgroundColor,
                            modifier =
                            Modifier
                                .basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                                .combinedClickable(
                                    enabled = true,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (mediaMetadata.album != null) {
                                            navController.navigate("album/${mediaMetadata.album.id}") { launchSingleTop = true }
                                            state.collapseSoft()
                                        }
                                    },
                                    onLongClick = {
                                        val clip = ClipData.newPlainText(context.getString(R.string.copied_title), title)
                                        clipboardManager.setPrimaryClip(clip)
                                        Toast
                                            .makeText(context, context.getString(R.string.copied_title), Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                )
                            ,
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (mediaMetadata.explicit) MIcon.Explicit()

                        if (mediaMetadata.artists.any { it.name.isNotBlank() }) {
                            val annotatedString = buildAnnotatedString {
                                mediaMetadata.artists.forEachIndexed { index, artist ->
                                    val tag = "artist_${artist.id.orEmpty()}"
                                    pushStringAnnotation(tag = tag, annotation = artist.id.orEmpty())
                                    withStyle(SpanStyle(color = TextBackgroundColor, fontSize = 16.sp)) {
                                        append(artist.name)
                                    }
                                    pop()
                                    if (index != mediaMetadata.artists.lastIndex) append(", ")
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .auxiliaryBoundsMorph("artist", inOverlay = false)
                                    .fillMaxWidth()
                                    .basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                                    .padding(end = 12.dp)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                            ) {
                                var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                                var clickOffset by remember { mutableStateOf<Offset?>(null) }
                                Text(
                                    text = annotatedString,
                                    style = AppTheme.typography.titleMedium.copy(color = TextBackgroundColor),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    onTextLayout = { layoutResult = it },
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            awaitPointerEventScope {
                                                while (true) {
                                                    val event = awaitPointerEvent()
                                                    val tapPosition = event.changes.firstOrNull()?.position
                                                    if (tapPosition != null) {
                                                        clickOffset = tapPosition
                                                    }
                                                }
                                            }
                                        }
                                        .combinedClickable(
                                            enabled = true,
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                            onClick = {
                                                val tapPosition = clickOffset
                                                val layout = layoutResult
                                                if (tapPosition != null && layout != null) {
                                                    val offset = layout.getOffsetForPosition(tapPosition)
                                                    annotatedString
                                                        .getStringAnnotations(offset, offset)
                                                        .firstOrNull()
                                                        ?.let { ann ->
                                                            val artistId = ann.item
                                                            if (artistId.isNotBlank()) {
                                                                navController.navigate("artist/$artistId") { launchSingleTop = true }
                                                                state.collapseSoft()
                                                            }
                                                        }
                                                }
                                            },
                                            onLongClick = {
                                                val clip =
                                                    ClipData.newPlainText(
                                                        context.getString(R.string.copied_artist),
                                                        annotatedString
                                                    )
                                                clipboardManager.setPrimaryClip(clip)
                                                Toast
                                                    .makeText(
                                                        context,
                                                        context.getString(R.string.copied_artist),
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        )
                                )
                            }
                        }
                    }

                    DjReadout(
                        modifier = Modifier.padding(top = 2.dp)
                            .flamingoChrome(additiveChrome)
                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                    )
                }
                }
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (useNewPlayerDesign) {
                    val shareShape = RoundedCornerShape(
                        topStart = 50.dp, bottomStart = 50.dp,
                        topEnd = 3.dp, bottomEnd = 3.dp
                    )

                    val favShape = RoundedCornerShape(
                        topStart = 3.dp, bottomStart = 3.dp,
                        topEnd = 50.dp, bottomEnd = 50.dp
                    )

                    val middleShape = RoundedCornerShape(3.dp)

                    Row(
                        modifier = Modifier.auxiliaryBoundsMorph("actionButtons"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(targetState = showInlineLyrics, label = "DownloadButton") { showLyrics ->
                            if (showLyrics) {
                                FilledIconButton(
                                    onClick = {
                                        isFullScreen = !isFullScreen
                                        // One-tap mode: collapsing out of fullscreen from this
                                        // button exits lyrics entirely (symmetric with the Queue
                                        // lyrics button opening straight to fullscreen), instead of
                                        // stopping at half. Classic two-tap mode is unaffected.
                                        if (oneTapFullscreenLyrics && !isFullScreen) showInlineLyrics = false
                                    },
                                    shape = shareShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = textButtonColor,
                                        contentColor = iconButtonColor,
                                    ),
                                    modifier = Modifier.size(42.dp)
                                        .flamingoChrome(additiveChrome)
                                        .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                ) {
                                    SymbolIcon(symbol = sf("arrow.up.left.and.arrow.down.right"), contentDescription = null, size = 24.dp)
                                }
                            } else {
                                FilledIconButton(
                                    onClick = {
                                        when (download?.state) {
                                            Download.STATE_COMPLETED, Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                                                DownloadService.sendRemoveDownload(
                                                    context,
                                                    ExoDownloadService::class.java,
                                                    mediaMetadata.id,
                                                    false,
                                                )
                                            }
                                            else -> {
                                                database.transaction {
                                                    insert(mediaMetadata)
                                                }
                                                val downloadRequest =
                                                    DownloadRequest
                                                        .Builder(mediaMetadata.id, mediaMetadata.id.toUri())
                                                        .setCustomCacheKey(mediaMetadata.id)
                                                        .setData(mediaMetadata.title.toByteArray())
                                                        .build()
                                                DownloadService.sendAddDownload(
                                                    context,
                                                    ExoDownloadService::class.java,
                                                    downloadRequest,
                                                    false,
                                                )
                                            }
                                        }
                                    },
                                    shape = shareShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = textButtonColor,
                                        contentColor = iconButtonColor,
                                    ),
                                    modifier = Modifier.size(42.dp)
                                        .flamingoChrome(additiveChrome)
                                        .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                ) {
                                    when (download?.state) {
                                        Download.STATE_COMPLETED -> {
                                            SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 24.dp)
                                        }
                                        Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                                            CircularWavyProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                            )
                                        }
                                        else -> {
                                            SymbolIcon(symbol = sf("arrow.down.circle"), contentDescription = null, size = 24.dp)
                                        }
                                    }
                                }
                            }
                        }

                        AnimatedContent(targetState = showInlineLyrics, label = "LikeButton") { showLyrics ->
                            if (showLyrics) {
                                val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue =null)
                                FilledIconButton(
                                    onClick = {
                                        menuState.show {
                                            com.convx.music.ui.menu.LyricsMenu(
                                                lyricsProvider = { currentLyrics },
                                                songProvider = { currentSong?.song },
                                                mediaMetadataProvider = { mediaMetadata },
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
                                    },
                                    shape = favShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = textButtonColor,
                                        contentColor = iconButtonColor,
                                    ),
                                    modifier = Modifier.size(42.dp)
                                        
                                        .flamingoChrome(additiveChrome)
                                        .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                ) {
                                    PlayerGlyph(
                                        slot = PlayerIconSlot.MORE,
                                        fallback = R.drawable.more_horiz,
                                        tint = LocalContentColor.current,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                ForcedDarkScope {
                                    GlassCircleButton(
                                            onClick = playerConnection::toggleLike,
                                            modifier = Modifier.size(42.dp),
                                            size = 42.dp,
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                                        PlayerGlyph(
                                            slot = if (currentSong?.song?.liked == true)
                                                PlayerIconSlot.LIKED else PlayerIconSlot.LIKE,
                                            fallback = if (currentSong?.song?.liked == true)
                                                R.drawable.favorite else R.drawable.favorite_border,
                                            tint = LocalContentColor.current,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Sample the player background and keep this provider stable across recompositions.
                    val loopBucketProvider = remember(videoLoopClock) {
                        { videoLoopClock.bucket(VideoLoopBucketMs) }
                    }
                    val videoCanvasActive = enableCanvas && canvasArtwork != null && backgroundVisible
                    Row(
                        modifier = Modifier.auxiliaryBoundsMorph("actionButtons"),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                    CompositionLocalProvider(
                        LocalAppBackdrop provides playerBackdrop,
                        LocalBackdropLoopBucket provides if (videoCanvasActive) loopBucketProvider else null,
                    ) {
                    AnimatedContent(targetState = showInlineLyrics, label = "DownloadButton") { showLyrics ->
                        if (showLyrics) {
                            GlassCircleButton(
                                onClick = {
                                    isFullScreen = !isFullScreen
                                    if (oneTapFullscreenLyrics && !isFullScreen) showInlineLyrics = false
                                },
                            ) {
                                SymbolIcon(symbol = sf("arrow.up.left.and.arrow.down.right"), contentDescription = null, size = 24.dp)
                            }
                        } else {
                            ForcedDarkScope {
                                GlassCircleButton(
                                    onClick = {
                                        menuState.show {
                                            OldPlayerMenu(
                                                mediaMetadata = mediaMetadata,
                                                navController = navController,
                                                playerBottomSheetState = state,
                                                onShowDetailsDialog = {
                                                    mediaMetadata.id.let {
                                                        bottomSheetPageState.showFloating {
                                                           ShowMediaInfo(it)
                                                        }
                                                    }
                                                },
                                                onDismiss = menuState::dismiss
                                            )
                                        }
                                    },
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                                    PlayerGlyph(
                                        slot = PlayerIconSlot.MORE,
                                        fallback = R.drawable.more_horiz,
                                        tint = LocalContentColor.current,
                                        modifier = Modifier.size(24.dp)
                                            .flamingoChrome(additiveChrome)
                                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                                    )
                                }
                            }
                        }
                    }

                    AnimatedContent(targetState = showInlineLyrics, label = "LikeButton") { showLyrics ->
                        if (showLyrics) {
                            val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue =null)
                            ForcedDarkScope {
                                GlassCircleButton(
                                    onClick = {
                                        menuState.show {
                                            com.convx.music.ui.menu.LyricsMenu(
                                                lyricsProvider = { currentLyrics },
                                                songProvider = { currentSong?.song },
                                                mediaMetadataProvider = { mediaMetadata },
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
                                    },
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                                    PlayerGlyph(
                                        slot = PlayerIconSlot.MORE,
                                        fallback = R.drawable.more_horiz,
                                        tint = LocalContentColor.current,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        } else {
                            ForcedDarkScope {
                                GlassCircleButton(
                                        onClick = playerConnection::toggleLike,
                                        modifier = Modifier,
    forceFlat = auxiliaryArtworkTransitionRunning(),
) {
                                    PlayerGlyph(
                                        slot = if (currentSong?.song?.liked == true)
                                            PlayerIconSlot.LIKED else PlayerIconSlot.LIKE,
                                        fallback = if (currentSong?.song?.liked == true)
                                            R.drawable.favorite else R.drawable.favorite_border,
                                        tint = if (currentSong?.song?.liked == true) AppTheme.colorScheme.error else LocalContentColor.current,
                                        modifier = Modifier.size(24.dp)
                                            .flamingoChrome(additiveChrome)
                                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                    )
                                }
                            }
                        }
                    }
                    }
                    }
                }
            }

            Spacer(Modifier.height(if (useNewPlayerDesign) 24.dp else 8.dp))

            // Keep progress polling recompositions inside the seek controls.
            val seekControls: @Composable () -> Unit = {
            when (sliderStyle) {
                SliderStyle.DEFAULT -> {
                    Slider(
                        value = (sliderPosition ?: effectivePosition).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = {
                            if (!isListenTogetherGuest) {
                                sliderPosition = it.toLong()
                            }
                        },
                        onValueChangeFinished = {
                            if (!isListenTogetherGuest) {
                                sliderPosition?.let {
                                    if (isCasting) {
                                        castHandler?.seekTo(it)
                                        lastManualSeekTime = System.currentTimeMillis()
                                    } else {
                                        playerConnection.player.seekTo(it)
                                    }
                                    position = it
                                }
                                sliderPosition = null
                            }
                        },
                        enabled = !isListenTogetherGuest,
                        colors = PlayerSliderColors.getSliderColors(
                            activeColor = if (useNewPlayerDesign) seekBarActiveColor else seekBarActiveColor.copy(alpha = 0.7f),
                            // A hairline, not a grey rail: it only reads this faint because
                            // the bar is added onto the artwork rather than laid over it.
                            inactiveAlpha = if (additiveChrome) 0.05f else 0.3f,
                        ),
                        modifier = Modifier
                            .padding(horizontal = PlayerHorizontalPadding)
                            .flamingoChrome(additiveChrome)
                            .graphicsLayer { if (additiveChrome) alpha = 0.45f },
                    )
                }

                SliderStyle.WAVY -> {
                    run {
                        WavySlider(
                            value = (sliderPosition ?: effectivePosition).toFloat(),
                            valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                            enabled = !isListenTogetherGuest,
                            onValueChange = {
                                if (!isListenTogetherGuest) sliderPosition = it.toLong()
                            },
                            onValueChangeFinished = {
                                if (!isListenTogetherGuest) {
                                    sliderPosition?.let {
                                        if (isCasting) {
                                            castHandler?.seekTo(it)
                                            lastManualSeekTime = System.currentTimeMillis()
                                        } else {
                                            playerConnection.player.seekTo(it)
                                        }
                                        position = it
                                    }
                                    sliderPosition = null
                                }
                            },
                            colors = PlayerSliderColors.getSliderColors(
                                activeColor = if (useNewPlayerDesign) seekBarActiveColor else seekBarActiveColor.copy(alpha = 0.7f),
                            // A hairline, not a grey rail: it only reads this faint because
                            // the bar is added onto the artwork rather than laid over it.
                            inactiveAlpha = if (additiveChrome) 0.05f else 0.3f,
                            ),
                            modifier = Modifier.padding(horizontal = PlayerHorizontalPadding)
                                .flamingoChrome(additiveChrome)
                                .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                            isPlaying = effectiveIsPlaying
                        )
                    }
                }

                SliderStyle.SLIM -> {
                    val trackInteractionSource = remember { MutableInteractionSource() }
                    val isTrackDragged by trackInteractionSource.collectIsDraggedAsState()
                    val isTrackPressed by trackInteractionSource.collectIsPressedAsState()
                    val isTrackActive = (isTrackDragged || isTrackPressed) && !useNewPlayerDesign

                    val trackHeight by animateDpAsState(
                        targetValue = if (isTrackActive) 16.dp else 10.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "trackHeight"
                    )

                    Slider(
                        value = (sliderPosition ?: effectivePosition).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = {
                            if (!isListenTogetherGuest) {
                                sliderPosition = it.toLong()
                            }
                        },
                        onValueChangeFinished = {
                            if (!isListenTogetherGuest) {
                                sliderPosition?.let {
                                    if (isCasting) {
                                        castHandler?.seekTo(it)
                                        lastManualSeekTime = System.currentTimeMillis()
                                    } else {
                                        playerConnection.player.seekTo(it)
                                    }
                                    position = it
                                }
                                sliderPosition = null
                            }
                        },
                        enabled = !isListenTogetherGuest,
                        interactionSource = trackInteractionSource,
                        // The stock design has no visible handle; a custom one only appears
                        // once the user actually supplies an image for the slot.
                        thumb = {
                            val seekThumb = rememberPlayerIcon(PlayerIconSlot.SEEK_THUMB)
                            if (seekThumb.isCustom) {
                                Image(
                                    painter = seekThumb.painter,
                                    contentDescription = null,
                                    colorFilter = seekThumb.colorFilterFor(textButtonColor),
                                    modifier = Modifier.size(18.dp),
                                )
                            } else {
                                Spacer(modifier = Modifier.size(0.dp))
                            }
                        },
                        track = { sliderState ->
                            PlayerSliderTrack(
                                sliderState = sliderState,
                                trackHeight = trackHeight,
                                colors = PlayerSliderColors.getSliderColors(
                                    activeColor = if (useNewPlayerDesign) seekBarActiveColor else seekBarActiveColor.copy(alpha = 0.7f),
                            // A hairline, not a grey rail: it only reads this faint because
                            // the bar is added onto the artwork rather than laid over it.
                            inactiveAlpha = if (additiveChrome) 0.05f else 0.3f,
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = PlayerHorizontalPadding)
                            .flamingoChrome(additiveChrome)
                            // After additiveChrome, never before: alpha applied first
                            // is folded into the saved layer and the add then runs at
                            // full strength.
                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                    )
                }

                SliderStyle.WAVEFORM -> {
                    val waveColors = PlayerSliderColors.getSliderColors(
                        activeColor = if (useNewPlayerDesign) seekBarActiveColor else seekBarActiveColor.copy(alpha = 0.7f),
                        // A hairline, not a grey rail: it only reads this faint because the
                        // bar is added onto the artwork rather than laid over it.
                        inactiveAlpha = if (additiveChrome) 0.05f else 0.3f,
                    )
                    // Per-frame off the player clock, so the waveform glides rather
                    // than stepping with the once-a-second position state.
                    val waveFraction = rememberPlaybackFraction(playerConnection.player, effectiveIsPlaying)
                    ScrollingWaveformSeekBar(
                        progress = {
                            val dragged = sliderPosition
                            if (dragged != null && duration > 0L && duration != C.TIME_UNSET) {
                                dragged.toFloat() / duration
                            } else {
                                waveFraction.value
                            }
                        },
                        onSeek = { f ->
                            if (!isListenTogetherGuest && duration > 0L && duration != C.TIME_UNSET) {
                                val target = (f * duration).toLong()
                                if (isCasting) {
                                    castHandler?.seekTo(target)
                                    lastManualSeekTime = System.currentTimeMillis()
                                } else {
                                    playerConnection.player.seekTo(target)
                                }
                                position = target
                            }
                        },
                        playedColor = waveColors.activeTrackColor,
                        trackColor = waveColors.inactiveTrackColor,
                        seed = mediaMetadata.id.hashCode(),
                        totalBars = 160,
                        visibleBars = 44,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = PlayerHorizontalPadding)
                            .height(40.dp)
                            .flamingoChrome(additiveChrome)
                            .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlayerHorizontalPadding + 4.dp)
                    .flamingoChrome(additiveChrome)
                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
            ) {
                Text(
                    text = makeTimeString(sliderPosition ?: effectivePosition),
                    style = AppTheme.typography.labelMedium,
                    color = TextBackgroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (showAudioQualityBadge || sleepTimerEnabled) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TextBackgroundColor.copy(alpha = 0.08f))
                            .border(
                                width = 0.5.dp,
                                color = TextBackgroundColor.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable {
                                if (sleepTimerEnabled) {
                                    showSleepTimerDialog = true
                                } else {
                                    mediaMetadata.id.let {
                                        bottomSheetPageState.showFloating {
                                            ShowMediaInfo(it)
                                        }
                                    }
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        AnimatedContent(
                            targetState = sleepTimerEnabled,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                        fadeOut(animationSpec = tween(300))
                            },
                            label = "QualityTimerSwitcher"
                        ) { isTimerActive ->
                            if (isTimerActive) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    SymbolIcon(symbol = sf("timer"), contentDescription = null, size = 12.dp, tint = TextBackgroundColor.copy(alpha = 0.8f))
                                    Text(
                                        text = makeTimeString(sleepTimerTimeLeft.coerceAtLeast(0)),
                                        style = AppTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.5.sp
                                        ),
                                        color = TextBackgroundColor.copy(alpha = 0.8f),
                                        maxLines = 1,
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // An infiniteRepeatable never finishes, so this decorative
                                    // sweep kept awaiting a frame for as long as the player was
                                    // on screen — the whole app redrew every vsync, paused
                                    // included. Spin it only while something is actually playing.
                                    val animatedRotation = if (isPlaying && !state.isCollapsed) {
                                        val infiniteTransition =
                                            rememberInfiniteTransition(label = "QualityIconTransition")
                                        infiniteTransition.animateFloat(
                                            initialValue = 0f,
                                            targetValue = 360f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(2000, easing = LinearEasing),
                                                repeatMode = RepeatMode.Restart
                                            ),
                                            label = "QualityIconRotation"
                                        )
                                    } else {
                                        remember { mutableFloatStateOf(0f) }
                                    }

                                    val iconBrush = Brush.sweepGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            TextBackgroundColor.copy(alpha = 1.0f),
                                            Color.Transparent
                                        )
                                    )

                                    Icon(
                                        painter = painterResource(R.drawable.stream_old_player),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .graphicsLayer(alpha = 0.99f)
                                            .drawWithCache {
                                                onDrawWithContent {
                                                    drawContent()
                                                    rotate(animatedRotation.value) {
                                                        drawRect(iconBrush, blendMode = BlendMode.SrcIn)
                                                    }
                                                }
                                            }
                                    )
                                    Text(
                                        text = run {
                                            val format = playerFormat ?: currentFormat?.let {
                                                // Fallback to DB info if live format not yet available
                                                androidx.media3.common.Format.Builder()
                                                    .setSampleMimeType(it.mimeType)
                                                    .setCodecs(it.codecs)
                                                    .setAverageBitrate(it.bitrate)
                                                    .setSampleRate(it.sampleRate ?: 0)
                                                    .build()
                                            }
                                            
                                            val codecLabel = format?.let { f ->
                                                val mime = f.sampleMimeType?.lowercase() ?: ""
                                                val codecs = f.codecs?.lowercase() ?: ""
                                                when {
                                                    mime.contains("flac") || codecs.contains("flac") -> "FLAC"
                                                    mime.contains("opus") || codecs.contains("opus") -> "OPUS"
                                                    mime.contains("mp4a") || mime.contains("aac") || codecs.contains("mp4a") || codecs.contains("aac") -> "AAC"
                                                    mime.contains("mp3") || mime.contains("mpeg") || codecs.contains("mp3") || codecs.contains("mpeg") -> "MP3"
                                                    mime.contains("vorbis") || codecs.contains("vorbis") -> "OGG"
                                                    mime.contains("webm") || codecs.contains("webm") -> "WEBM"
                                                    else -> null
                                                }
                                            }
                                            
                                            val bitrate = format?.bitrate?.takeIf { it > 0 } ?: format?.averageBitrate?.takeIf { it > 0 }
                                            val bitrateLabel = bitrate?.let { "${it / 1000}kbps" }
                                            
                                            val sampleRate = format?.sampleRate?.takeIf { it > 0 }
                                            val sampleRateLabel = sampleRate?.let { "${it / 1000}.${(it % 1000) / 100}kHz" }

                                            buildString {
                                                append(codecLabel ?: if (isLosslessStream) "LOSSLESS" else "AUTO")
                                                if (bitrateLabel != null) append(" • $bitrateLabel")
                                                if (sampleRateLabel != null) append(" • $sampleRateLabel")
                                            }
                                        },
                                        style = AppTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.0.sp
                                        ),
                                        color = TextBackgroundColor.copy(alpha = 0.8f),
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = if (duration != C.TIME_UNSET) makeTimeString(duration) else "",
                    style = AppTheme.typography.labelMedium,
                    color = TextBackgroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            }
            seekControls()

            Spacer(Modifier.height(if (useNewPlayerDesign) 24.dp else 8.dp))

            // Place this below the seek bar and align it independently of the title row.
            if (showUpNext) {
                UpNextSong(
                    playerConnection = playerConnection,
                    titleColor = TextBackgroundColor,
                    subtitleColor = TextBackgroundColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = PlayerHorizontalPadding),
                    additiveChrome = additiveChrome,
                )
                Spacer(Modifier.height(8.dp))
            }

            AnimatedVisibility(
                // fullscreenLyricsCollapseTop repurposed: on, the controls stay
                // visible (and get reordered above the lyrics — see the two
                // controlsContent call sites below) instead of hiding entirely.
                visible = !isFullScreen || (fullscreenLyricsCollapseTop && !showInlineLyrics),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) +
                    slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Column {
                    // The transport controls below are already inert for a guest, but
                    // greyed-out buttons alone read as a bug. Say why, once, above both
                    // control layouts.
                    if (isListenTogetherGuest) {
                        Text(
                            text = stringResource(R.string.listen_together_locked),
                            style = AppTheme.typography.labelMedium,
                            color = textButtonColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PlayerHorizontalPadding, vertical = 4.dp),
                        )
                    }
                    if (useNewPlayerDesign) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PlayerHorizontalPadding)
                        ) {
                            val backInteractionSource = remember { MutableInteractionSource() }
                            val nextInteractionSource = remember { MutableInteractionSource() }
                            val playPauseInteractionSource = remember { MutableInteractionSource() }

                            val isPlayPausePressed by playPauseInteractionSource.collectIsPressedAsState()
                            val isBackPressed by backInteractionSource.collectIsPressedAsState()
                            val isNextPressed by nextInteractionSource.collectIsPressedAsState()

                            val playPauseWeight by animateFloatAsState(
                                targetValue = if (isPlayPausePressed) 1.9f else if (isBackPressed || isNextPressed) 1.1f else 1.3f,
                                animationSpec = spring(
                                    dampingRatio = 0.6f,
                                    stiffness = 500f
                                ),
                                label = "playPauseWeight"
                            )

                            val backButtonWeight by animateFloatAsState(
                                targetValue = if (isBackPressed) 0.65f else if (isPlayPausePressed) 0.35f else 0.45f,
                                animationSpec = spring(
                                    dampingRatio = 0.6f,
                                    stiffness = 500f
                                ),
                                label = "backButtonWeight"
                            )

                            val nextButtonWeight by animateFloatAsState(
                                targetValue = if (isNextPressed) 0.65f else if (isPlayPausePressed) 0.35f else 0.45f,
                                animationSpec = spring(
                                    dampingRatio = 0.6f,
                                    stiffness = 500f
                                ),
                                label = "nextButtonWeight"
                            )

                            FilledIconButton(
                                onClick = playerConnection::seekToPrevious,
                                enabled = canSkipPrevious && !isListenTogetherGuest,
                                shape = RoundedCornerShape(50),
                                interactionSource = backInteractionSource,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = sideButtonContainerColor,
                                    contentColor = sideButtonContentColor,
                                ),
                                modifier = Modifier
                                    .height(68.dp)
                                    .weight(backButtonWeight)
                                    .widthIn(min = 44.dp)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                            ) {
                                PlayerGlyph(
                                    slot = PlayerIconSlot.PREVIOUS,
                                    fallback = R.drawable.skip_previous_legacy,
                                    tint = LocalContentColor.current,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = {
                                    if (isListenTogetherGuest) {
                                        playerConnection.toggleMute()
                                        return@FilledIconButton
                                    }
                                    if (isCasting) {
                                        if (castIsPlaying) {
                                            castHandler?.pause()
                                        } else {
                                            castHandler?.play()
                                        }
                                    } else if (playbackState == STATE_ENDED) {
                                        playerConnection.player.seekTo(0, 0)
                                        playerConnection.player.playWhenReady = true
                                    } else {
                                        playerConnection.togglePlayPause()
                                    }
                                },
                                shape = RoundedCornerShape(50),
                                interactionSource = playPauseInteractionSource,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = textButtonColor,
                                    contentColor = iconButtonColor,
                                ),
                                modifier = Modifier
                                    .height(68.dp)
                                    .weight(playPauseWeight)
                                    .widthIn(min = 44.dp)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (isListenTogetherGuest) {
                                        // Guests get a mute toggle in this position rather than
                                        // transport, and that button is not a customisable slot
                                        // (nor a play/pause pair to animate between).
                                        PlayerGlyph(
                                            slot = null,
                                            fallback = if (isMuted) R.drawable.volume_off else R.drawable.volume_down,
                                            tint = LocalContentColor.current,
                                            contentDescription = if (isMuted) stringResource(R.string.unmute) else stringResource(R.string.mute),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    } else {
                                        AnimatedPlayerGlyph(
                                            isPlaying = effectiveIsPlaying,
                                            playSlot = PlayerIconSlot.PLAY,
                                            pauseSlot = PlayerIconSlot.PAUSE,
                                            playFallback = R.drawable.play_applemusic,
                                            pauseFallback = R.drawable.pause_applemusic,
                                            tint = LocalContentColor.current,
                                            playDescription = stringResource(R.string.play),
                                            pauseDescription = stringResource(R.string.pause),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isListenTogetherGuest) {
                                            if (isMuted) stringResource(R.string.unmute) else stringResource(R.string.mute)
                                        } else {
                                            if (effectiveIsPlaying) stringResource(R.string.pause) else stringResource(R.string.play)
                                        },
                                        style = AppTheme.typography.titleMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = playerConnection::seekToNext,
                                enabled = canSkipNext && !isListenTogetherGuest,
                                shape = RoundedCornerShape(50),
                                interactionSource = nextInteractionSource,
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = sideButtonContainerColor,
                                    contentColor = sideButtonContentColor,
                                ),
                                modifier = Modifier
                                    .height(68.dp)
                                    .weight(nextButtonWeight)
                                    .widthIn(min = 44.dp)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                            ) {
                                PlayerGlyph(
                                    slot = PlayerIconSlot.NEXT,
                                    fallback = R.drawable.fast_forward,
                                    tint = LocalContentColor.current,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PlayerHorizontalPadding),
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ResizableIconButton(
                                    slot = PlayerIconSlot.PREVIOUS,
                                    icon = R.drawable.skip_previous_legacy,
                                    enabled = canSkipPrevious && !isListenTogetherGuest,
                                    color = TextBackgroundColor,
                                    modifier =
                                Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                                    .alpha(if (isListenTogetherGuest) 0.5f else 1f)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                    onClick = playerConnection::seekToPrevious,
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(
                                modifier =
                                Modifier
                                    .size(100.dp) //100
                                    .clip(RoundedCornerShape(playPauseRoundness))
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                                    .clickable {
                                        if (isListenTogetherGuest) {
                                            playerConnection.toggleMute()
                                            return@clickable
                                        }
                                        if (isCasting) {
                                            if (castIsPlaying) {
                                                castHandler?.pause()
                                            } else {
                                                castHandler?.play()
                                            }
                                        } else if (playbackState == STATE_ENDED) {
                                            playerConnection.player.seekTo(0, 0)
                                            playerConnection.player.playWhenReady = true
                                        } else {
                                            playerConnection.player.togglePlayPause()
                                        }
                                    },
                            ) {
                                // Mute (guest) and replay (ended) are single states with no
                                // partner to animate between, so only the transport pair gets
                                // the pop.
                                if (isListenTogetherGuest || playbackState == STATE_ENDED) {
                                    PlayerGlyph(
                                        slot = if (isListenTogetherGuest) null else PlayerIconSlot.REPLAY,
                                        fallback = if (isListenTogetherGuest) {
                                            if (isMuted) R.drawable.volume_mute else R.drawable.volume_down
                                        } else {
                                            R.drawable.replay
                                        },
                                        tint = TextBackgroundColor,
                                        modifier =
                                        Modifier
                                            .align(Alignment.Center)
                                            .size(72.dp),
                                    )
                                } else {
                                    AnimatedPlayerGlyph(
                                        isPlaying = effectiveIsPlaying,
                                        playSlot = PlayerIconSlot.PLAY,
                                        pauseSlot = PlayerIconSlot.PAUSE,
                                        playFallback = R.drawable.play_applemusic,
                                        pauseFallback = R.drawable.pause_applemusic,
                                        tint = TextBackgroundColor,
                                        playDescription = stringResource(R.string.play),
                                        pauseDescription = stringResource(R.string.pause),
                                        playSize = 72.dp,
                                        pauseSize = 72.dp,
                                        modifier =
                                        Modifier
                                            .align(Alignment.Center),
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                ResizableIconButton(
                                    slot = PlayerIconSlot.NEXT,
                                    icon = R.drawable.fast_forward,
                                    enabled = canSkipNext && !isListenTogetherGuest,
                                    color = TextBackgroundColor,
                                    modifier =
                                Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                                    .alpha(if (isListenTogetherGuest) 0.5f else 1f)
                                    .flamingoChrome(additiveChrome)
                                    .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha },
                                    onClick = playerConnection::seekToNext,
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(23.dp)) // Match V2's transport-to-volume spacing.

                        // Hidden means invisible + non-interactive, not removed — the row still
                        // reserves its layout space so a DIY sticker can be placed over it.
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PlayerHorizontalPadding)
                                .alpha(if (hideVolumeBar) 0f else 1f)
                                .then(if (hideVolumeBar) Modifier.clearAndSetSemantics {} else Modifier)
                                .flamingoChrome(additiveChrome, bleed = 8.dp)
                                .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                        ) {
                            val volumeInteractionSource = remember { MutableInteractionSource() }
                            val isVolumeDragged by volumeInteractionSource.collectIsDraggedAsState()
                            val isVolumePressed by volumeInteractionSource.collectIsPressedAsState()
                            val isVolumeActive = isVolumeDragged || isVolumePressed

                            // Internal state to track drag value and avoid system feedback lag
                            var dragVolume by remember { mutableFloatStateOf(systemVolume) }
                            
                            // Use a coroutine to update system volume to avoid UI blocking on fast swipes
                            val scope = rememberCoroutineScope()
                            
                            LaunchedEffect(systemVolume) {
                                if (!isVolumeActive) dragVolume = systemVolume
                            }

                            val animatedSystemVolume by animateFloatAsState(
                                targetValue = systemVolume,
                                animationSpec = tween(150, easing = LinearOutSlowInEasing),
                                label = "animatedSystemVolume"
                            )
                            
                            val volume = if (isCasting) castVolume else {
                                if (isVolumeActive) dragVolume else animatedSystemVolume
                            }
                            
                            val volumeTrackHeight by animateDpAsState(
                                targetValue = if (isVolumeActive) 12.dp else 8.dp,
                                animationSpec = spring(
                                    dampingRatio = 0.7f, // Slightly more stable damping
                                    stiffness = 600f // Balanced stiffness for high-speed stability
                                ),
                                label = "volumeTrackHeight"
                            )

                            // The glyphs keep their slot size while the track grows; scaling them clipped the row edges.
                            PlayerQuietVolumeGlyph(
                                level = volume,
                                tint = textButtonColor,
                            )

                            Spacer(Modifier.width(8.dp))

                            Slider(
                                value = volume,
                                onValueChange = { newVolume ->
                                    dragVolume = newVolume
                                    if (isCasting) {
                                        castHandler?.setVolume(newVolume)
                                    } else {
                                        // Non-blocking update to prevent "fast swipe" lag
                                        scope.launch(Dispatchers.Default) {
                                            val newStep = (newVolume * maxSystemVolume).roundToInt()
                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newStep, 0)
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !hideVolumeBar,
                                interactionSource = volumeInteractionSource,
                                thumb = {},
                                track = { sliderState ->
                                    PlayerSliderTrack(
                                        sliderState = sliderState,
                                        colors = SliderDefaults.colors(
                                            activeTrackColor = seekBarActiveColor.copy(alpha = 0.7f),
                                            inactiveTrackColor = seekBarActiveColor.copy(alpha = 0.15f)
                                        ),
                                        trackHeight = volumeTrackHeight,
                                        // Volume, not position: the mix window means nothing here.
                                        showAutomix = false
                                    )
                                }
                            )

                            Spacer(Modifier.width(8.dp))

                            PlayerVolumeGlyph(
                                level = volume,
                                tint = textButtonColor,
                            )
                        }

                        val bottomBarPad = if (isLandscapeLayout) 0.dp else with(LocalDensity.current) {
                            (31.dp - WindowInsets.navigationBars.getBottom(this).toDp()).coerceAtLeast(4.dp)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                // Place lyrics, audio output, and queue at the screen's thirds.
                                .padding(top = 12.dp, bottom = bottomBarPad)
                                .height(48.dp),
                        ) {
                            val bottomTint = textButtonColor
                            val tabChrome = Modifier
                                .flamingoChrome(additiveChrome)
                                .graphicsLayer { if (additiveChrome) alpha = AdditiveChromeContentAlpha }
                            val isLyricsActive = classicPages.showing && classicPages.value == PlayerInternalState.LYRICS
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = {
                                        classicPages.value = if (isLyricsActive) PlayerInternalState.COVER else PlayerInternalState.LYRICS
                                    },
                                ) {
                                    V2TabGlyph(active = isLyricsActive, tint = bottomTint, chrome = tabChrome) { iconModifier, tint ->
                                        PlayerGlyph(
                                            slot = PlayerIconSlot.V2_LYRICS,
                                            fallbackSymbol = sf("quote.bubble"),
                                            size = 28.dp,
                                            bounce = true,
                                            tint = tint,
                                            modifier = iconModifier,
                                            contentDescription = "Lyrics",
                                        )
                                    }
                                }
                            }

                            Box(Modifier.weight(1f).fillMaxSize().then(tabChrome), contentAlignment = Alignment.Center) {
                                if (showRoutePopover) V2RoutePopover(
                                    onDismiss = { showRoutePopover = false },
                                    title = mediaMetadata.title.orEmpty(),
                                    artist = mediaMetadata.artists?.joinToString { it.name }.orEmpty(),
                                    artworkUrl = mediaMetadata.thumbnailUrl,
                                    onMore = { showAudioDeviceBottomSheet = true },
                                )
                                if (audioOutput != null) {
                                    IconButton(
                                        onClick = { showRoutePopover = true },
                                        modifier = Modifier.align(Alignment.Center),
                                    ) {
                                        SymbolIcon(
                                            symbol = sf(audioDeviceIcons.symbol(audioOutput?.name, audioOutput?.type)),
                                            bounceOnEnter = true,
                                            bounceOnChange = true,
                                            bounceOnTouch = true,
                                            contentDescription = "Audio Device",
                                            size = 22.dp,
                                            tint = bottomTint,
                                        )
                                    }
                                    Text(
                                        text = audioDeviceNames.displayName(audioOutput?.name),
                                        style = AppTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                        color = bottomTint,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .absoluteOffset(y = 30.dp)
                                            .widthIn(max = 110.dp),
                                    )
                                } else if (BuildConfig.CAST_AVAILABLE && enableGoogleCast) {
                                    CastButton(
                                        modifier = Modifier.size(48.dp),
                                        tintColor = bottomTint,
                                    )
                                } else {
                                    IconButton(
                                        onClick = { showRoutePopover = true },
                                        modifier = Modifier.align(Alignment.Center),
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

                            val isQueueActive = classicPages.showing && classicPages.value == PlayerInternalState.QUEUE
                            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                IconButton(
                                    onClick = {
                                        classicPages.value = if (isQueueActive) PlayerInternalState.COVER else PlayerInternalState.QUEUE
                                    },
                                ) {
                                    V2TabGlyph(active = isQueueActive, tint = bottomTint, chrome = tabChrome) { iconModifier, tint ->
                                        PlayerGlyph(
                                            slot = PlayerIconSlot.V2_QUEUE,
                                            fallbackSymbol = sf("list.bullet"),
                                            size = 28.dp,
                                            bounce = true,
                                            tint = tint,
                                            modifier = iconModifier,
                                            contentDescription = "Queue",
                                        )
                                    }
                                }
                            }
                        }
                        // Shown through the shared menu host, so it slides up and glides like the other player menus.
                        val audioMenuState = LocalMenuState.current
                        LaunchedEffect(showAudioDeviceBottomSheet) {
                            if (showAudioDeviceBottomSheet) {
                                showAudioDeviceBottomSheet = false
                                audioMenuState.show { AudioDeviceMenu(onDismiss = audioMenuState::dismiss) }
                            }
                        }
                    }
                }
            }
        }

        // Compact tab view forces the stacked (mobile-style) branch below even on a
        // physically-landscape/wide screen — that's the whole point of the setting:
        // no side-by-side thumbnail, controls centered underneath like on phone.
        val forceStackedPlayerLayout = LocalTabView.current && compactPlayerInTabView
        when {
            !forceStackedPlayerLayout && isLandscapeLayout -> {
                // Calculate vertical padding like the reference app
                val density = LocalDensity.current
                val verticalPadding = max(
                    WindowInsets.systemBars.getTop(density),
                    WindowInsets.systemBars.getBottom(density)
                )
                val verticalPaddingDp = with(density) { verticalPadding.toDp() }
                val verticalWindowInsets = WindowInsets(left = 0.dp, top = verticalPaddingDp, right = 0.dp, bottom = verticalPaddingDp)
                var landscapeHeight by remember { mutableStateOf(androidx.compose.ui.unit.Dp.Infinity) }

                Row(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).add(verticalWindowInsets)
                        )
                        .padding(bottom = 24.dp)
                        .fillMaxSize()
                        .onSizeChanged { landscapeHeight = with(density) { it.height.toDp() } }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(state.preUpPostDownNestedScrollConnection)
                    ) {
                        val currentSliderPosition by rememberUpdatedState(sliderPosition)
                        val sliderPositionProvider = remember { { currentSliderPosition } }
                        val isExpandedProvider = remember(state) { { state.isExpanded } }
                        ClassicPageRegion(
                            pages = classicPages,
                            fadeOnly = playerBackground == PlayerBackgroundStyle.APPLE_MUSIC,
                            cover = {
                                AnimatedContent(
                                targetState = showInlineLyrics,
                                label = "Lyrics",
                                transitionSpec = {
                                    (fadeIn(spring(dampingRatio = 0.82f, stiffness = 550f)) +
                                        scaleIn(initialScale = 0.96f, animationSpec = spring(dampingRatio = 0.78f, stiffness = 550f)))
                                        .togetherWith(
                                            fadeOut(spring(dampingRatio = 1.0f, stiffness = 650f)) +
                                            scaleOut(targetScale = 0.96f, animationSpec = spring(dampingRatio = 1.0f, stiffness = 650f))
                                        )
                                }
                            ) { showLyrics ->
                                if (showLyrics) {
                                    InlineLyricsView(
                                        mediaMetadata = mediaMetadata,
                                        showLyrics = showLyrics,
                                        positionProvider = { effectivePosition }
                                    )
                                } else {
                                    if (classicVideoStream != null) {
                                        YouTubeVideoPane(
                                            stream = classicVideoStream,
                                            connection = playerConnection,
                                            videoController = classicVideo.controller,
                                            visible = !classicPages.showing,
                                            modifier = Modifier.fillMaxSize(),
                                            onClose = { classicVideo.select(false) },
                                        )
                                    } else Thumbnail(
                                        sliderPositionProvider = sliderPositionProvider,
                                        modifier = Modifier.animateContentSize(),
                                        isPlayerExpanded = isExpandedProvider,
                                        isLandscape = true,
                                        isListenTogetherGuest = isListenTogetherGuest
                                    )
                                }
                            }
                            },
                            header = classicHeader,
                            page = classicPage,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(if (showInlineLyrics) 0.65f else 1f, false)
                            .animateContentSize()
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    ) {
                        Spacer(Modifier.weight(1f))

                        if (classicVideo.canWatch && videoSwitchEnabled && !showInlineLyrics) {
                            Box(Modifier.fillMaxWidth().padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                                SongVideoModeControl(classicVideo.watch, textButtonColor) { selected ->
                                    if (selected && classicVideo.unavailable) classicVideo.requestStream()
                                    classicVideo.select(selected)
                                }
                            }
                        }

                        mediaMetadata?.let {
                            // A short, wide screen scales the control stack down instead of clipping it.
                            AnimatedVisibility(
                                visible = classicControlsVisible ||
                                    !(classicPages.showing && classicPages.value == PlayerInternalState.QUEUE),
                                enter = fadeIn(ControlsShowSpec()) +
                                    slideInVertically(ControlsShowSpec()) { it / 2 } +
                                    expandVertically(ControlsShowSpec()),
                                exit = fadeOut(ControlsHideSpec()) +
                                    slideOutVertically(ControlsHideSpec()) { it / 2 } +
                                    shrinkVertically(ControlsHideSpec()),
                            ) {
                                com.convx.music.ui.utils.ProvideFittedDensity(landscapeHeight, LandscapeControlsDesignHeight) {
                                    // AnimatedVisibility lays its children out as a Box; without this
                                    // Column every control row stacked on the same line.
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) { controlsContent(it) }
                                }
                            }
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                    Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                        .animateContentSize(),
                ) {
                    // Controls normally render below the lyrics/thumbnail area.
                    // While fullscreen lyrics are showing with the "collapse
                    // top" setting on, they move above it instead of hiding.
                    val controlsAtTop = isFullScreen && fullscreenLyricsCollapseTop
                    if (controlsAtTop) {
                        mediaMetadata?.let {
                            controlsContent(it)
                        }
                    }

                    if (classicVideo.canWatch && videoSwitchEnabled && !showInlineLyrics) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                            SongVideoModeControl(classicVideo.watch, textButtonColor) { selected ->
                                if (selected && classicVideo.unavailable) classicVideo.requestStream()
                                classicVideo.select(selected)
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        // Register the artwork bounds for the player morph overlay.
                        modifier = Modifier.weight(1f),
                    ) {
                        // Remember lambdas to prevent unnecessary recomposition
                        val currentSliderPosition by rememberUpdatedState(sliderPosition)
                        val sliderPositionProvider = remember { { currentSliderPosition } }
                        val isExpandedProvider = remember(state) { { state.isExpanded } }
                        ClassicPageRegion(
                            pages = classicPages,
                            fadeOnly = playerBackground == PlayerBackgroundStyle.APPLE_MUSIC,
                            cover = {
                                AnimatedContent(
                                targetState = showInlineLyrics,
                                label = "Lyrics",
                                transitionSpec = {
                                    (fadeIn(spring(dampingRatio = 0.82f, stiffness = 550f)) +
                                        scaleIn(initialScale = 0.96f, animationSpec = spring(dampingRatio = 0.78f, stiffness = 550f)))
                                        .togetherWith(
                                            fadeOut(spring(dampingRatio = 1.0f, stiffness = 650f)) +
                                            scaleOut(targetScale = 0.96f, animationSpec = spring(dampingRatio = 1.0f, stiffness = 650f))
                                        )
                                }
                            ) { showLyrics ->
                                if (showLyrics) {
                                    InlineLyricsView(
                                        mediaMetadata = mediaMetadata,
                                        showLyrics = showLyrics,
                                        positionProvider = { effectivePosition }
                                    )
                                } else {
                                    if (classicVideoStream != null) {
                                        YouTubeVideoPane(
                                            stream = classicVideoStream,
                                            connection = playerConnection,
                                            videoController = classicVideo.controller,
                                            visible = !classicPages.showing,
                                            modifier = Modifier.fillMaxSize(),
                                            onClose = { classicVideo.select(false) },
                                        )
                                    } else Thumbnail(
                                        sliderPositionProvider = sliderPositionProvider,
                                        modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                                        isPlayerExpanded = isExpandedProvider,
                                        isListenTogetherGuest = isListenTogetherGuest
                                    )
                                }
                            }
                            },
                            header = classicHeader,
                            page = classicPage,
                        )
                    }

                    if (!controlsAtTop) {
                        mediaMetadata?.let {
                            androidx.compose.animation.AnimatedVisibility(
                                visible = classicControlsVisible ||
                                    !(classicPages.showing && classicPages.value == PlayerInternalState.QUEUE),
                                enter = fadeIn(ControlsShowSpec()) +
                                    slideInVertically(ControlsShowSpec()) { it / 2 } +
                                    expandVertically(ControlsShowSpec()),
                                exit = fadeOut(ControlsHideSpec()) +
                                    slideOutVertically(ControlsHideSpec()) { it / 2 } +
                                    shrinkVertically(ControlsHideSpec()),
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) { controlsContent(it) }
                            }
                        }
                    }

                    Spacer(Modifier.height(if (useNewPlayerDesign) 30.dp else 8.dp))
                }
            }
        }

        }
    }
}

/** Height the classic landscape control stack is laid out for; shorter screens scale it down. */
private val LandscapeControlsDesignHeight = 340.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InlineLyricsView(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    positionProvider: () -> Long,
    lyricsTextSize: Float? = null,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue =null)
    val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null && currentLyrics == null) {
            delay(500)
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val lyricsHelper = org.koin.core.context.GlobalContext.get().get<com.convx.music.lyrics.LyricsHelper>()
                    val fetchedLyricsWithProvider = lyricsHelper.getLyrics(mediaMetadata)
                    database.withTransaction {
                        if (this.lyrics(mediaMetadata.id).first() == null)
                            upsert(LyricsEntity(mediaMetadata.id, fetchedLyricsWithProvider.lyrics, fetchedLyricsWithProvider.provider))
                    }
                } catch (_: Exception) {
                    // Lyrics are optional; a provider failure should not interrupt playback.
                }
            }
        }
    }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            lyrics == null -> {
                ContainedLoadingIndicator()
            }
            lyrics == LyricsEntity.LYRICS_NOT_FOUND -> {
                Text(
                    text = stringResource(R.string.lyrics_not_found),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
            else -> {
                val lyricsContent: @Composable () -> Unit = {
                    Lyrics(
                        sliderPositionProvider = positionProvider,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        showLyrics = showLyrics,
                        textSizeOverride = lyricsTextSize,
                    )
                }
                ProvideTextStyle(
                    value = AppTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                ) {
                    lyricsContent()
                }
            }
        }
    }
}


@Composable
fun MoreActionsButton(
    mediaMetadata: MediaMetadata,
    navController: NavController,
    state: BottomSheetState,
    textButtonColor: Color,
    iconButtonColor: Color
) {
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(textButtonColor)
            .clickable {
                menuState.show {
                    PlayerMenu(
                        mediaMetadata = mediaMetadata,
                        navController = navController,
                        playerBottomSheetState = state,
                        onShowDetailsDialog = {
                            mediaMetadata.id.let {
                                bottomSheetPageState.showFloating {
                                    ShowMediaInfo(it)
                                }
                            }
                        },
                        onDismiss = menuState::dismiss
                    )
                }
            }
    ) {
        // Routed through PlayerGlyph like every other control: this one drew the stock
        // glyph straight from resources, so the More slot in player-icon settings did
        // nothing here even though the sibling PlayerMoreMenuButton honoured it.
        PlayerGlyph(
            slot = PlayerIconSlot.MORE,
            fallback = R.drawable.more_horiz,
            tint = iconButtonColor,
        )
    }
}

@Composable
private fun PlayerMoreMenuButton(
    mediaMetadata: MediaMetadata,
    navController: NavController,
    state: BottomSheetState,
    textButtonColor: Color,
    iconButtonColor: Color,
) {
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    Box(
        contentAlignment = Alignment.Center,
        modifier =
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(textButtonColor)
            .clickable {
                menuState.show {
                    PlayerMenu(
                        mediaMetadata = mediaMetadata,
                        navController = navController,
                        playerBottomSheetState = state,
                        onShowDetailsDialog = {
                            mediaMetadata.id.let {
                                bottomSheetPageState.showFloating {
                                    ShowMediaInfo(it)
                                }
                            }
                        },
                        onDismiss = menuState::dismiss,
                    )
                }
            },
    ) {
        // PlayerGlyph, not a raw Image with an unconditional tint: a user's own
        // image for this slot was being flattened to a solid colour regardless of
        // the slot's per-icon tint flag, so a photo came out as a coloured blob.
        PlayerGlyph(
            slot = PlayerIconSlot.MORE,
            fallback = R.drawable.more_horiz,
            tint = iconButtonColor,
        )
    }
}

@Composable
private fun BackgroundVideoView(
    mediaId: String,
    videoUrl: String,
    isPlaying: Boolean,
    onError: () -> Unit = {},
    onReady: () -> Unit = {},
    modifier: Modifier = Modifier,
    // Polled into periodically below so glass surfaces sampling this video's
    // backdrop can cache their blur/lens output per loop-position bucket
    // instead of recomputing every frame of every loop — see VideoLoopClock.
    loopClock: VideoLoopClock? = null,
    onFrame: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val downloadUtil = LocalDownloadUtil.current.get()
    var isVideoReady by remember(videoUrl) { mutableStateOf(false) }
    // The reused player listener must see the latest callbacks and media ID.
    val currentOnError by rememberUpdatedState(onError)
    val currentOnReady by rememberUpdatedState(onReady)
    val currentMediaId by rememberUpdatedState(mediaId)
    val currentOnFrame by rememberUpdatedState(onFrame)
    // The clip's colours, sampled once (CanvasVideoLooks) so the frost/mesh can follow it.
    val sampleScope = rememberCoroutineScope()
    val textureHolder = remember { arrayOfNulls<TextureView>(1) }

    val trackSelector = remember {
        DefaultTrackSelector(context).apply {
            parameters = buildUponParameters()
                .setMaxVideoSize(4096, 4096)
                .setForceHighestSupportedBitrate(true)
                .build()
        }
    }

    val mediaSourceFactory = remember<DefaultMediaSourceFactory>(downloadUtil) {
        DefaultMediaSourceFactory(
            CacheDataSource.Factory()
                .setCache(downloadUtil.downloadCache)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setCacheWriteDataSinkFactory(null)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR),
            androidx.media3.extractor.DefaultExtractorsFactory()
        )
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setTrackSelector(trackSelector)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setTargetBufferBytes(20 * 1024 * 1024) // 20MB buffer for 4K
                    .build()
            )
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                playWhenReady = isPlaying
            }
    }

    if (loopClock != null) {
        LaunchedEffect(exoPlayer, loopClock) {
            while (isActive) {
                loopClock.update(exoPlayer.currentPosition, exoPlayer.duration)
                delay(VideoLoopBucketMs)
            }
        }
    }

    val aspectRatioFrameLayout = remember {
        AspectRatioFrameLayout(context).apply {
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    aspectRatioFrameLayout.setAspectRatio(videoSize.width.toFloat() / videoSize.height)
                }
            }
            override fun onRenderedFirstFrame() {
                isVideoReady = true
                currentOnReady()
                val id = currentMediaId
                if (id.isNotBlank()) sampleScope.launch { sampleCanvasVideoLook(id) { textureHolder[0] } }
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Log.e("BackgroundVideoView", "Video playback failed: ${error.errorCodeName} ${error.message}")
                currentOnError()
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    LaunchedEffect(videoUrl, mediaId) {
        isVideoReady = false
        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setCustomCacheKey("$mediaId#canvas")
            .setMimeType(if (videoUrl.contains("m3u8")) MimeTypes.APPLICATION_M3U8 else MimeTypes.VIDEO_MP4)
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVideoReady) 1f else 0f,
        animationSpec = tween(800, easing = SeamlessEasing),
        label = "videoAlpha"
    )

    AndroidView(
        factory = { _ ->
            aspectRatioFrameLayout.apply {
                // Ensure the view doesn't capture touches intended for other sections
                isEnabled = false
                isClickable = false
                isFocusable = false

                // Ensure TextureView is added only once
                if (childCount == 0) {
                    val textureView = com.convx.music.artistvideo.VideoFrameTextureView(context).apply {
                        this.onFrame = { currentOnFrame?.invoke() }
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                    addView(textureView)
                    textureHolder[0] = textureView
                    exoPlayer.setVideoTextureView(textureView)
                }
            }
        },
        modifier = modifier.graphicsLayer { this.alpha = alpha; clip = true }
    )
}


/**
 * The z >= 0 sticker band as part of the player, not floating over it: while the morph owns
 * the frame it rides the card (same origin, same uniform scale as the full-player recording,
 * clipped to the card's rounded rect), shrinking into the pill and growing back out with it;
 * otherwise it follows the sheet's own slide. The bounce is per sticker, see
 * DiyStickerLayer's entrance.
 */
private fun androidx.compose.ui.graphics.GraphicsLayerScope.stickerMorphLayer(
    progress: Float,
    queueFade: Float,
    sheetOffsetPx: Float,
) {
    val mini = miniPlayerContainerRect
    if (!PlayerMorph.active || mini == null || size.width <= 0f || size.height <= 0f) {
        // Each sticker fades itself in with its staggered pop (DiyStickerLayer entrance).
        alpha = queueFade
        translationY = sheetOffsetPx
        scaleX = 1f
        scaleY = 1f
        translationX = 0f
        clip = false
        return
    }
    // Each sticker fades and pops itself in (DiyStickerLayer entrance); the band only rides.
    alpha = queueFade
    val q = progress.coerceIn(0f, 1f)
    val cardWidth = androidx.compose.ui.util.lerp(mini.width, size.width, q)
    val cardHeight = androidx.compose.ui.util.lerp(mini.height, size.height, q)
    val scale = cardWidth / size.width
    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
    scaleX = scale
    scaleY = scale
    translationX = androidx.compose.ui.util.lerp(mini.left, 0f, q)
    translationY = androidx.compose.ui.util.lerp(mini.top, 0f, q)
    // Card rect in this layer's own (pre-scale) space: full width, the card's height.
    val clipHeight = cardHeight / scale
    val radius = androidx.compose.ui.util.lerp(mini.height / 2f, 0f, q) / scale
    shape = object : androidx.compose.ui.graphics.Shape {
        override fun createOutline(
            size: androidx.compose.ui.geometry.Size,
            layoutDirection: androidx.compose.ui.unit.LayoutDirection,
            density: androidx.compose.ui.unit.Density,
        ) = androidx.compose.ui.graphics.Outline.Rounded(
            androidx.compose.ui.geometry.RoundRect(
                0f, 0f, size.width, clipHeight,
                androidx.compose.ui.geometry.CornerRadius(radius, radius),
            ),
        )
    }
    clip = true
}
