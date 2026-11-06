/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * 
 */

package com.convx.music.ui.player

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.convx.music.ui.component.BlurredBackdropImage
import com.convx.music.ui.component.ScrollingWaveformSeekBar
import com.convx.music.ui.component.rememberNoArtworkPainter
import com.convx.music.ui.component.rememberPlaybackFraction
import com.convx.music.ui.component.thumbnailPx
import com.convx.music.constants.MiniPlayerWaveformKey
import com.convx.music.constants.PlayerGradientAngleKey
import com.convx.music.constants.PlayerGradientStopsKey
import com.convx.music.ui.theme.decodeGradientStops
import com.convx.music.ui.theme.tiltedGradient
import com.convx.music.constants.PlayerStaticColorKey
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State as ComposeState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFAirplayAudio
import com.composables.sfsymbols.monochrome.SFArrowCounterclockwise
import com.composables.sfsymbols.monochrome.SFBackwardFill
import com.composables.sfsymbols.monochrome.SFForwardFill
import com.composables.sfsymbols.monochrome.SFInfoCircle
import com.composables.sfsymbols.monochrome.SFPlayFill
import com.composables.sfsymbols.monochrome.SFSpeakerSlashFill
import com.composables.sfsymbols.monochrome.SFSpeakerWave2Fill
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.size.Size as CoilSize
import coil3.toBitmap
import com.convx.music.LocalDatabase
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.player.customize.PlayerGlyph
import com.convx.music.ui.player.customize.PlayerIconSlot
import com.convx.music.constants.CropAlbumArtKey
import com.convx.music.constants.DarkModeKey
import com.convx.music.constants.MiniPlayerBackgroundStyleKey
import com.convx.music.constants.MiniPlayerHeight
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.PureBlackMiniPlayerKey
import com.convx.music.constants.SwipeSensitivityKey
import com.convx.music.constants.SwipeThumbnailKey
import com.convx.music.constants.ThumbnailCornerRadius
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.constants.UseNewMiniPlayerDesignKey
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.listentogether.ListenTogetherManager
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CastConnectionHandler
import com.convx.music.playback.PlayerConnection
import com.convx.music.ui.screens.settings.DarkMode
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import com.convx.music.ui.component.GlassComponent
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.resolvedTextColor
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.elasticStretchLayerBlock
import com.convx.music.ui.component.isGlassAllowed
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.LocalAppMotionFrozen
import com.convx.music.ui.theme.PlayerColorExtractor
import com.convx.music.ui.theme.colorengine.ColorEngine
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import com.convx.music.core.AudioDeviceMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.convx.music.core.rememberHeadphoneConnected
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.ui.draw.drawBehind
import kotlin.math.absoluteValue
import com.convx.music.ui.component.Icon as MIcon

/**
 * Stable wrapper for progress state - reads values only during draw phase
 * This prevents recomposition when position/duration change
 */
@Stable
class ProgressState(
    private val positionState: MutableLongState,
    private val durationState: MutableLongState
) {
    val progress: Float
        get() {
            val duration = durationState.longValue
            return if (duration > 0) (positionState.longValue.toFloat() / duration).coerceIn(0f, 1f) else 0f
        }
}

@Composable
fun MiniPlayer(
    positionState: MutableLongState,
    durationState: MutableLongState,
    modifier: Modifier = Modifier
) {
    val useNewMiniPlayerDesign by rememberPreference(UseNewMiniPlayerDesignKey, true)
    
    val progressState = remember { ProgressState(positionState, durationState) }

    if (useNewMiniPlayerDesign) {
        NewMiniPlayer(
            progressState = progressState,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            LegacyMiniPlayer(
                progressState = progressState,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Sensitivity scales the release distance from 45% to 12% of the screen width.
private fun calculateSwipeThresholdPx(screenWidthPx: Float, sensitivity: Float): Int {
    val widthFraction = 0.45f - 0.33f * sensitivity.coerceIn(0f, 1f)
    return (screenWidthPx * widthFraction).roundToInt()
}

@Composable
private fun NewMiniPlayer(
    progressState: ProgressState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    
    val pureBlack by rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val preferredDark = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    val useDarkTheme = com.convx.music.ui.theme.LocalForcedDark.current || preferredDark
    
    val miniPlayerBackground by rememberEnumPreference(MiniPlayerBackgroundStyleKey, defaultValue = PlayerBackgroundStyle.DEFAULT)
    val miniPlayerWaveform by rememberPreference(MiniPlayerWaveformKey, defaultValue = false)
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val waveFraction = rememberPlaybackFraction(playerConnection.player, isPlaying)
    
    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    
    // Casting can be unavailable while the service initializes.
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    val isBluetoothConnected by rememberHeadphoneConnected()
    var showAudioDeviceBottomSheet by remember { mutableStateOf(false) }
    var showRoutePopover by remember { mutableStateOf(false) }

    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    
    // Disable swipe for Listen Together guests
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    // Convert swipe thresholds to density-independent pixels so taps are not treated as swipes.
    val density = LocalDensity.current.density

    val configuration = LocalConfiguration.current
    // Keep the player compact on tablets in either orientation.
    val isTablet = remember(configuration.screenWidthDp) {
        configuration.screenWidthDp >= 600
    }

    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    val animationSpec = remember {
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    }

    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, density) {
        calculateSwipeThresholdPx(configuration.screenWidthDp * density, swipeSensitivity)
    }

    val (gradientColors, onGradientColorsChange) = remember { mutableStateOf<List<Color>>(emptyList()) }

    MiniPlayerColorExtractor(
        mediaMetadata = mediaMetadata,
        miniPlayerBackground = miniPlayerBackground,
        onGradientColorsChange = onGradientColorsChange
    )
    
    val miniPlayerGlass = LocalGlassEffectConfig.current
    val usesGlassSurface =
        miniPlayerGlass.isEnabledFor(GlassComponent.MINI_PLAYER) && isGlassAllowed()
    // Keep glass transparent; without glass, let artwork show through the elevated surface.
    val backgroundColor = when {
        usesGlassSurface -> Color.Transparent
        pureBlack && useDarkTheme -> Color.Black.copy(alpha = 0.82f)
        else -> AppTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f)
    }
    val isDynamicBackground = miniPlayerBackground != PlayerBackgroundStyle.DEFAULT

    val glassConfig = LocalGlassEffectConfig.current
    val dynamicContentColor = if (
        glassConfig.isEnabledFor(GlassComponent.MINI_PLAYER) &&
        isGlassAllowed()
    ) {
        glassConfig.resolvedTextColor()
    } else {
        Color.White
    }
    val primaryColor = if (isDynamicBackground) dynamicContentColor else AppTheme.colorScheme.primary
    val outlineColor = if (isDynamicBackground) dynamicContentColor.copy(alpha = 0.5f) else AppTheme.colorScheme.outline
    val onSurfaceColor = if (isDynamicBackground) dynamicContentColor else AppTheme.colorScheme.onSurface
    val errorColor = AppTheme.colorScheme.error

    // This highlight does not consume pointer events, so it can share the swipe gesture.
    val interactiveHighlight = remember(coroutineScope) { InteractiveHighlight(animationScope = coroutineScope) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MiniPlayerHeight)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .padding(horizontal = 12.dp)
            .let { baseModifier ->
                if (swipeThumbnail) {
                    baseModifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                dragStartTime = System.currentTimeMillis()
                                totalDragDistance = 0f
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    offsetXAnimatable.animateTo(0f, animationSpec)
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val adjustedDragAmount =
                                    if (layoutDirection == LayoutDirection.Rtl) -dragAmount else dragAmount
                                val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNext = playerConnection.player.nextMediaItemIndex != -1
                                val tryingToSwipeRight = adjustedDragAmount > 0
                                val tryingToSwipeLeft = adjustedDragAmount < 0
                                val allowLeft = tryingToSwipeLeft && canSkipNext
                                val allowRight = tryingToSwipeRight && canSkipPrevious

                                val canReturnToCenter =
                                    (tryingToSwipeRight && !canSkipPrevious && offsetXAnimatable.value < 0) ||
                                            (tryingToSwipeLeft && !canSkipNext && offsetXAnimatable.value > 0)

                                if (allowLeft || allowRight || canReturnToCenter) {
                                    totalDragDistance += kotlin.math.abs(adjustedDragAmount)
                                    coroutineScope.launch {
                                        offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount)
                                    }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                // Average velocity separates a quick flick from a slow drag.
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val dragged = kotlin.math.abs(currentOffset)

                                // Let a fast flick skip without crossing the full distance.
                                val shouldChangeSong = dragged > autoSwipeThreshold ||
                                    (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f)

                                if (shouldChangeSong) {
                                    // The connection handles casting, ended players, and skip callbacks.
                                    if (currentOffset > 0 && canSkipPrevious) {
                                        playerConnection.seekToPrevious()
                                    } else if (currentOffset <= 0 && canSkipNext) {
                                        playerConnection.seekToNext()
                                    }
                                }
                                coroutineScope.launch {
                                    offsetXAnimatable.animateTo(0f, animationSpec)
                                }
                            }
                        )
                    }
                } else baseModifier
            }
    ) {
        Box(
            modifier = Modifier
                .then(if (isTablet) Modifier.width(500.dp).align(Alignment.Center) else Modifier.fillMaxWidth())
                .height(64.dp)
                .offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }
                // Apply stretch before clipping so the whole pill deforms.
                .graphicsLayer(elasticStretchLayerBlock(interactiveHighlight))
                .clip(RoundedCornerShape(32.dp))
                .background(color = backgroundColor)
                .border(1.dp, outlineColor.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                .then(interactiveHighlight.modifier)
                .then(interactiveHighlight.gestureModifier)
        ) {
            MiniPlayerBackgroundLayer(
                style = miniPlayerBackground,
                mediaMetadata = mediaMetadata,
                gradientColors = gradientColors,
                isPlaying = isPlaying,
            )

            val waveColor = LocalGlassEffectConfig.current.textColor

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(0.5.dp, outlineColor.copy(alpha = 0.3f), CircleShape)
                ) {
                    mediaMetadata?.let { metadata ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(metadata.thumbnailUrl)
                                .size(CoilSize(thumbnailPx(36.dp), thumbnailPx(36.dp)))
                                .crossfade(false)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = rememberNoArtworkPainter(),
                            error = rememberNoArtworkPainter(),
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    NewMiniPlayerSongInfo(
                        mediaMetadata = mediaMetadata,
                        onSurfaceColor = onSurfaceColor,
                        errorColor = errorColor
                    )
                    if (miniPlayerWaveform) {
                        Spacer(modifier = Modifier.height(2.dp))
                        ScrollingWaveformSeekBar(
                            progress = { waveFraction.value },
                            onSeek = { f ->
                                val d = playerConnection.player.duration
                                if (d > 0L) playerConnection.player.seekTo((f * d).toLong())
                            },
                            playedColor = waveColor,
                            trackColor = waveColor.copy(alpha = 0.25f),
                            seed = mediaMetadata?.id?.hashCode() ?: 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                NewMiniPlayerPlayButton(
                    progressState = progressState,
                    playbackState = playbackState,
                    isCasting = isCasting,
                    castHandler = castHandler,
                    playerConnection = playerConnection,
                    mediaMetadata = mediaMetadata,
                    primaryColor = primaryColor,
                    outlineColor = outlineColor,
                    listenTogetherManager = listenTogetherManager,
                    compact = true
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    // Wrapper, not the raw player — same reason as the swipe above:
                    // this button did nothing while casting, and did not re-prepare
                    // a player that had reached the end of the queue.
                    onClick = { if (canSkipNext) playerConnection.seekToNext() },
                    enabled = canSkipNext
                ) {
                    SymbolIcon(symbol = sf("SFForwardFill"), contentDescription = null, tint = if (canSkipNext) onSurfaceColor else onSurfaceColor.copy(alpha = 0.4f))
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (isCasting) {
                    SymbolIcon(symbol = sf("SFAirplayAudio"), contentDescription = "Casting", size = 20.dp, tint = primaryColor)
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color = primaryColor.copy(alpha = 0.1f))
                        .clickable { showRoutePopover = true }
                ) {
                    SymbolIcon(symbol = if (isBluetoothConnected) sf("headphones") else sf("hifispeaker.fill"), contentDescription = stringResource(R.string.audio_devices), size = 16.dp, tint = primaryColor)
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Favorite button
                mediaMetadata?.let {
                    FavoriteButton(
                        songId = it.id,
                        onSurfaceColor = onSurfaceColor,
                        errorColor = errorColor,
                        outlineColor = outlineColor
                    )
                }
            }
        }
    }

    if (showRoutePopover) {
        V2RoutePopover(
            onDismiss = { showRoutePopover = false },
            title = mediaMetadata?.title.orEmpty(),
            artist = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
            artworkUrl = mediaMetadata?.thumbnailUrl,
            onMore = { showAudioDeviceBottomSheet = true },
        )
    }
    if (showAudioDeviceBottomSheet) {
        AudioDeviceMenu(onDismiss = { showAudioDeviceBottomSheet = false })
    }
}

/**
 * Play button with circular progress indicator
 * Uses drawWithContent to update progress without recomposition
 */
@Composable
private fun NewMiniPlayerPlayButton(
    progressState: ProgressState,
    playbackState: Int,
    isCasting: Boolean,
    castHandler: CastConnectionHandler?,
    playerConnection: PlayerConnection,
    mediaMetadata: MediaMetadata?,
    primaryColor: Color,
    outlineColor: Color,
    listenTogetherManager: ListenTogetherManager?,
    compact: Boolean = false
) {
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()

    
    val trackColor = outlineColor.copy(alpha = 0.2f)
    val strokeWidth = if (compact) 2.dp else 3.dp
    val buttonSize = if (compact) 36.dp else 48.dp
    val innerSize = if (compact) 36.dp else 40.dp
    val iconSize = if (compact) 18.dp else 20.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(buttonSize)
            .drawWithContent {
                drawContent()
                val progress = progressState.progress
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                val startAngle = -90f
                val sweepAngle = 360f * progress
                val diameter = size.minDimension
                val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = stroke
                )
                drawArc(
                    color = primaryColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = stroke
                )
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .then(
                    if (compact) Modifier.background(primaryColor.copy(alpha = 0.1f))
                    else Modifier.border(1.dp, outlineColor.copy(alpha = 0.3f), CircleShape)
                )
                .clickable {
                    if (isListenTogetherGuest) {
                        playerConnection.toggleMute()
                        return@clickable
                    }
                    if (isCasting) {
                        if (castIsPlaying) castHandler?.pause() else castHandler?.play()
                    } else if (playbackState == Player.STATE_ENDED) {
                        playerConnection.player.seekTo(0, 0)
                        playerConnection.player.playWhenReady = true
                    } else {
                        playerConnection.togglePlayPause()
                    }
                }
        ) {
            if (compact) {
                val iconVector = when {
                    isListenTogetherGuest -> if (isMuted) SfSymbols.Monochrome.SFSpeakerSlashFill else SfSymbols.Monochrome.SFSpeakerWave2Fill
                    playbackState == Player.STATE_ENDED -> SfSymbols.Monochrome.SFArrowCounterclockwise
                    else -> null
                }
                if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(iconSize)
                    )
                } else {
                    AnimatedPlayPauseIcon(
                        isPlaying = effectiveIsPlaying,
                        tint = primaryColor,
                        size = iconSize
                    )
                }
            } else {
                mediaMetadata?.let { metadata ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(metadata.thumbnailUrl)
                            .size(CoilSize(thumbnailPx(innerSize), thumbnailPx(innerSize)))
                            .crossfade(false)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberNoArtworkPainter(),
                        error = rememberNoArtworkPainter(),
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                }

                // Overlay for paused state or muted (guest)
                if (isListenTogetherGuest && isMuted || (!isListenTogetherGuest && (!effectiveIsPlaying || playbackState == Player.STATE_ENDED))) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    )
                    PlayerGlyph(
                        slot = when {
                            isListenTogetherGuest -> null
                            playbackState == Player.STATE_ENDED -> PlayerIconSlot.REPLAY
                            else -> PlayerIconSlot.PLAY
                        },
                        fallbackVector = if (isListenTogetherGuest) {
                            if (isMuted) SfSymbols.Monochrome.SFSpeakerSlashFill else SfSymbols.Monochrome.SFSpeakerWave2Fill
                        } else if (playbackState == Player.STATE_ENDED) {
                            SfSymbols.Monochrome.SFArrowCounterclockwise
                        } else {
                            SfSymbols.Monochrome.SFPlayFill
                        },
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Song info display - title and artist
 */
@Composable
private fun NewMiniPlayerSongInfo(
    mediaMetadata: MediaMetadata?,
    onSurfaceColor: Color,
    errorColor: Color,
    modifier: Modifier = Modifier
) {
    val error by LocalPlayerConnection.current?.error?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        mediaMetadata?.let { metadata ->
            Text(
                text = metadata.title,
                color = onSurfaceColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (metadata.explicit) MIcon.Explicit()
                if (metadata.artists.any { it.name.isNotBlank() }) {
                    Text(
                        text = metadata.artists.joinToString { it.name },
                        color = onSurfaceColor.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp),
                    )
                }
            }

            AnimatedVisibility(visible = error != null, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = stringResource(R.string.error_playing),
                    color = errorColor,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// Legacy mini player design.

@Composable
private fun LegacyMiniPlayer(
    progressState: ProgressState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val pureBlack by rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)
    
    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    
    // Disable swipe for Listen Together guests
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current.density

    val configuration = LocalConfiguration.current
    val isTablet = remember(configuration.screenWidthDp) {
        configuration.screenWidthDp >= 600
    }

    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    val animationSpec = remember {
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    }

    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, density) {
        calculateSwipeThresholdPx(configuration.screenWidthDp * density, swipeSensitivity)
    }

    val primaryColor = AppTheme.colorScheme.primary
    val trackColor = AppTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .then(if (isTablet) Modifier.width(500.dp) else Modifier.fillMaxWidth())
            .height(MiniPlayerHeight)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(
                if (pureBlack && isSystemInDarkTheme()) Color.Black
                else AppTheme.colorScheme.surfaceContainer
            )
            .let { baseModifier ->
                if (swipeThumbnail) {
                    baseModifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                dragStartTime = System.currentTimeMillis()
                                totalDragDistance = 0f
                            },
                            onDragCancel = {
                                coroutineScope.launch { offsetXAnimatable.animateTo(0f, animationSpec) }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val adjustedDragAmount =
                                    if (layoutDirection == LayoutDirection.Rtl) -dragAmount else dragAmount
                                val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNext = playerConnection.player.nextMediaItemIndex != -1
                                val tryingToSwipeRight = adjustedDragAmount > 0
                                val tryingToSwipeLeft = adjustedDragAmount < 0
                                val allowLeft = tryingToSwipeLeft && canSkipNext
                                val allowRight = tryingToSwipeRight && canSkipPrevious

                                val canReturnToCenter =
                                    (tryingToSwipeRight && !canSkipPrevious && offsetXAnimatable.value < 0) ||
                                            (tryingToSwipeLeft && !canSkipNext && offsetXAnimatable.value > 0)

                                if (allowLeft || allowRight || canReturnToCenter) {
                                    totalDragDistance += kotlin.math.abs(adjustedDragAmount)
                                    coroutineScope.launch {
                                        offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount)
                                    }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                // Average velocity separates a quick flick from a slow drag.
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val dragged = kotlin.math.abs(currentOffset)

                                // Let a fast flick skip without crossing the full distance.
                                val shouldChangeSong = dragged > autoSwipeThreshold ||
                                    (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f)

                                if (shouldChangeSong) {
                                    // The connection handles casting, ended players, and skip callbacks.
                                    if (currentOffset > 0 && canSkipPrevious) {
                                        playerConnection.seekToPrevious()
                                    } else if (currentOffset <= 0 && canSkipNext) {
                                        playerConnection.seekToNext()
                                    }
                                }
                                coroutineScope.launch { offsetXAnimatable.animateTo(0f, animationSpec) }
                            }
                        )
                    }
                } else baseModifier
            }
    ) {
        // Progress bar - uses drawWithContent to avoid recomposition
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .drawWithContent {
                    val progress = progressState.progress
                    drawRect(trackColor)
                    drawRect(primaryColor, size = Size(size.width * progress, size.height))
                }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }
                .padding(end = 12.dp),
        ) {
            Box(Modifier.weight(1f)) {
                mediaMetadata?.let {
                    LegacyMiniMediaInfo(
                        mediaMetadata = it,
                        pureBlack = pureBlack,
                        modifier = Modifier.padding(horizontal = 6.dp),
                    )
                }
            }

            LegacyPlayPauseButton(
                playbackState = playbackState,
                isCasting = isCasting,
                castHandler = castHandler,
                playerConnection = playerConnection,
                listenTogetherManager = listenTogetherManager
            )

            IconButton(
                    enabled = canSkipNext && !isListenTogetherGuest,
                    onClick = if (isListenTogetherGuest) ({}) else ({ playerConnection.seekToNext() }),
            ) {
                SymbolIcon(symbol = sf("SFForwardFill"), contentDescription = null)
            }
        }

        // Swipe indicator
        if (offsetXAnimatable.value.absoluteValue > 50f) {
            Box(
                modifier = Modifier
                    .align(if (offsetXAnimatable.value > 0) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
            ) {
                SymbolIcon(symbol = if (offsetXAnimatable.value > 0) sf("SFBackwardFill") else sf("SFForwardFill"), contentDescription = null, size = 24.dp, tint = primaryColor.copy(
                        alpha = (offsetXAnimatable.value.absoluteValue / autoSwipeThreshold).coerceIn(0f, 1f)
                    ))
            }
        }
    }
}

@Composable
private fun LegacyPlayPauseButton(
    playbackState: Int,
    isCasting: Boolean,
    castHandler: CastConnectionHandler?,
    playerConnection: PlayerConnection,
    listenTogetherManager: ListenTogetherManager?
) {
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()


    IconButton(
        onClick = {
            if (isListenTogetherGuest) {
                playerConnection.toggleMute()
                return@IconButton
            }
            if (isCasting) {
                if (castIsPlaying) castHandler?.pause() else castHandler?.play()
            } else if (playbackState == Player.STATE_ENDED) {
                playerConnection.player.seekTo(0, 0)
                playerConnection.player.playWhenReady = true
            } else {
                playerConnection.togglePlayPause()
            }
        },
    ) {
        when {
            isListenTogetherGuest -> SymbolIcon(symbol = if (isMuted) sf("SFSpeakerSlashFill") else sf("SFSpeakerWave2Fill"), contentDescription = null)
            playbackState == Player.STATE_ENDED -> SymbolIcon(symbol = sf("SFArrowCounterclockwise"), contentDescription = null)
            else -> AnimatedPlayPauseIcon(isPlaying = effectiveIsPlaying, size = 24.dp)
        }
    }
}

@Composable
private fun LegacyMiniMediaInfo(
    mediaMetadata: MediaMetadata,
    pureBlack: Boolean,
    modifier: Modifier = Modifier,
) {
    val error by LocalPlayerConnection.current?.error?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .padding(6.dp)
                .size(48.dp)
                .clip(ContinuousRoundedRectangle(ThumbnailCornerRadius))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colorScheme.surfaceVariant)
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaMetadata.thumbnailUrl)
                    .size(CoilSize(thumbnailPx(48.dp), thumbnailPx(48.dp)))
                    .crossfade(false)
                    .build(),
                contentDescription = null,
                contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
                placeholder = rememberNoArtworkPainter(),
                error = rememberNoArtworkPainter(),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ContinuousRoundedRectangle(ThumbnailCornerRadius)),
            )

            androidx.compose.animation.AnimatedVisibility(visible = error != null, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            color = if (pureBlack) Color.Black else Color.Black.copy(alpha = 0.6f),
                            shape = ContinuousRoundedRectangle(ThumbnailCornerRadius),
                        ),
                ) {
                    SymbolIcon(symbol = sf("SFInfoCircle"), contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = AppTheme.colorScheme.error)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
        ) {
            Text(
                text = mediaMetadata.title,
                color = AppTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(),
            )

            if (mediaMetadata.artists.any { it.name.isNotBlank() }) {
                Text(
                    text = mediaMetadata.artists.joinToString { it.name },
                    color = AppTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// Isolated button composables to prevent parent recomposition.


@Composable
private fun FavoriteButton(
    songId: String,
    onSurfaceColor: Color,
    errorColor: Color,
    outlineColor: Color
) {
    val database = LocalDatabase.current.get()
    val playerConnection = LocalPlayerConnection.current ?: return
    val librarySongFlow = remember(songId) { database.song(songId) }
    val librarySong by librarySongFlow.collectAsStateWithLifecycle(initialValue =null)
    val isLiked = librarySong?.song?.liked == true
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (isLiked) errorColor.copy(alpha = 0.5f) else outlineColor.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .background(
                color = if (isLiked) errorColor.copy(alpha = 0.1f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { playerConnection.service.toggleLike() }
    ) {
        SymbolIcon(symbol = if (isLiked) sf("heart.fill") else sf("heart"), contentDescription = null, size = 20.dp, tint = if (isLiked) errorColor else onSurfaceColor.copy(alpha = 0.7f))
    }
}
@Composable
internal fun MiniPlayerColorExtractor(
    mediaMetadata: MediaMetadata?,
    miniPlayerBackground: PlayerBackgroundStyle,
    onGradientColorsChange: (List<Color>) -> Unit
) {
    val context = LocalContext.current
    val fallbackColor = AppTheme.colorScheme.surfaceContainer.toArgb()

    LaunchedEffect(mediaMetadata?.id, miniPlayerBackground) {
        if (miniPlayerBackground == PlayerBackgroundStyle.GRADIENT || miniPlayerBackground == PlayerBackgroundStyle.GLOW_ANIMATED) {
            val currentMetadata = mediaMetadata
            if (currentMetadata?.thumbnailUrl != null) {
                // Keyed on id + style: the two styles extract different color sets,
                // so the same song must cache one entry per style.
                val cacheKey = "${currentMetadata.id}_$miniPlayerBackground"
                miniPlayerGradientCache.get(cacheKey)?.let {
                    onGradientColorsChange(it)
                    return@LaunchedEffect
                }
                withContext(Dispatchers.IO) {
                    val request = ImageRequest.Builder(context)
                        .data(currentMetadata.thumbnailUrl)
                        .size(200, 200)
                        .allowHardware(false)
                        .build()

                    val result = runCatching { context.imageLoader.execute(request) }.getOrNull()
                    if (result != null) {
                        val bitmap = result.image?.toBitmap()
                        if (bitmap != null) {
                            val palette = withContext(Dispatchers.Default) {
                                Palette.from(bitmap)
                                    .maximumColorCount(8)
                                    .resizeBitmapArea(100 * 100)
                                    .generate()
                            }
                            val extractedColors = if (miniPlayerBackground == PlayerBackgroundStyle.GLOW_ANIMATED) {
                                listOfNotNull(
                                    palette.getVibrantColor(fallbackColor).let { Color(it) },
                                    palette.getLightVibrantColor(fallbackColor).let { Color(it) },
                                    palette.getDarkVibrantColor(fallbackColor).let { Color(it) },
                                    palette.getMutedColor(fallbackColor).let { Color(it) },
                                    palette.getLightMutedColor(fallbackColor).let { Color(it) },
                                    palette.getDarkMutedColor(fallbackColor).let { Color(it) }
                                ).distinct()
                            } else {
                                ColorEngine.analyze(bitmap, cacheKey = currentMetadata.thumbnailUrl)
                                    ?.let { result ->
                                        val dominant = result.palette.dominant
                                        listOf(
                                            dominant,
                                            dominant.copy(
                                                red = (dominant.red * 0.6f).coerceAtLeast(0f),
                                                green = (dominant.green * 0.6f).coerceAtLeast(0f),
                                                blue = (dominant.blue * 0.6f).coerceAtLeast(0f),
                                            ),
                                            Color.Black,
                                        )
                                    }
                                    ?: PlayerColorExtractor.extractGradientColors(
                                        palette = palette,
                                        fallbackColor = fallbackColor
                                    )
                            }
                            miniPlayerGradientCache.put(cacheKey, extractedColors)
                            withContext(Dispatchers.Main) { onGradientColorsChange(extractedColors) }
                        }
                    }
                }
            }
        } else {
            onGradientColorsChange(emptyList())
        }
    }
}

/**
 * Mini-player gradient colors keyed by "songId_style": palette extraction is
 * expensive and the mini player re-runs it on every song change, so a bounded
 * cache skips repeats within a session.
 */
private val miniPlayerGradientCache = android.util.LruCache<String, List<Color>>(64)

@Composable
internal fun MiniPlayerBackgroundLayer(
    style: PlayerBackgroundStyle,
    mediaMetadata: MediaMetadata?,
    gradientColors: List<Color>,
    isPlaying: Boolean = true,
) {
    val context = LocalContext.current
    
    when (style) {
        PlayerBackgroundStyle.BLUR -> {
            // Baked blur on the 128px decode (see Player.kt BLUR branch): same
            // look as Modifier.blur(3.dp), zero per-frame RenderEffect cost.
            BlurredBackdropImage(
                model = mediaMetadata?.thumbnailUrl,
                screenBlurRadiusDp = 3f,
                decodeSize = 128,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )
        }
        PlayerBackgroundStyle.GRADIENT -> {
            if (gradientColors.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(gradientColors))
                        .background(Color.Black.copy(alpha = 0.2f))
                )
            }
        }
        PlayerBackgroundStyle.GLOW_ANIMATED -> {
            if (gradientColors.isNotEmpty()) {
                // Gate on app motion freeze: this mini sits over Home while its
                // infinite transition would otherwise invalidate every frame of
                // every scroll. One recomposition per scroll start/end.
                val motionFrozen = LocalAppMotionFrozen.current
                val progress: ComposeState<Float> = if (isPlaying && !motionFrozen.value) {
                    val infiniteTransition = rememberInfiniteTransition(label = "GlowAnimation")
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

                val colors = gradientColors
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val p = progress.value
                            val width = size.width
                            val height = size.height
                            
                            fun rotatedColorAt(index: Int): Color {
                                val size = colors.size
                                val idx = index.toFloat() + p * size
                                val a = kotlin.math.floor(idx).toInt() % size
                                val b = (a + 1) % size
                                val frac = idx - kotlin.math.floor(idx)
                                return lerp(colors[a], colors[b], frac)
                            }

                            fun oscillate(min: Float, max: Float, phase: Float): Float {
                                val v = kotlin.math.sin(2.0 * kotlin.math.PI * (p + phase).toDouble()).toFloat()
                                return min + (max - min) * ((v + 1f) * 0.5f)
                            }

                            val c1 = rotatedColorAt(0)
                            val c2 = rotatedColorAt(1)

                            val o1x = oscillate(0.0f, 1.0f, 0.0f)
                            val o1y = oscillate(0.0f, 0.5f, 0.1f)
                            val o2x = oscillate(1.0f, 0.0f, 0.2f)
                            val o2y = oscillate(0.5f, 1.0f, 0.3f)

                            val b1 = Brush.radialGradient(
                                colors = listOf(c1.copy(alpha = 0.8f), Color.Transparent),
                                center = Offset(width * o1x, height * o1y),
                                radius = width * 1.2f
                            )
                            val b2 = Brush.radialGradient(
                                colors = listOf(c2.copy(alpha = 0.7f), Color.Transparent),
                                center = Offset(width * o2x, height * o2y),
                                radius = width * 1.0f
                            )
                            
                            drawRect(Color(0xFF050505))
                            drawRect(b1)
                            drawRect(b2)
                        }
                )
            }
        }

        PlayerBackgroundStyle.LIVE_MESH -> {
            val motionFrozen = LocalAppMotionFrozen.current
            val rotation: ComposeState<Float> = if (isPlaying && !motionFrozen.value) {
                val infiniteTransition = rememberInfiniteTransition(label = "liveMesh")
                infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(60000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )
            } else {
                remember { mutableFloatStateOf(0f) }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.5f
                        scaleY = 1.5f
                    }
            ) {
                // Baked blur on the 128px decode (see Player.kt BLUR branch note).
                val matrix = remember { ColorMatrix().apply { setToSaturation(1.6f) } }
                BlurredBackdropImage(
                    model = mediaMetadata?.thumbnailUrl,
                    screenBlurRadiusDp = 3f,
                    decodeSize = 128,
                    colorFilter = ColorFilter.colorMatrix(matrix),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = rotation.value },
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }
        }
        PlayerBackgroundStyle.FLUID_MESH -> {
            MeshGradientBackground(
                palette = rememberArtworkMeshPalette(mediaMetadata?.thumbnailUrl),
                trackKey = mediaMetadata?.id,
                modifier = Modifier.fillMaxSize(),
            )
        }

        PlayerBackgroundStyle.STATIC -> {
            val staticColor by rememberPreference(PlayerStaticColorKey, defaultValue = 0xFF1A1A1A.toInt())
            Box(modifier = Modifier.fillMaxSize().background(Color(staticColor)))
        }

        PlayerBackgroundStyle.CUSTOM_GRADIENT -> {
            val stopsRaw by rememberPreference(PlayerGradientStopsKey, defaultValue = "")
            val angle by rememberPreference(PlayerGradientAngleKey, defaultValue = 90f)
            val stops = remember(stopsRaw) { decodeGradientStops(stopsRaw) }
            Box(modifier = Modifier.fillMaxSize().tiltedGradient(stops, angle))
        }

        else -> {}
    }
}

