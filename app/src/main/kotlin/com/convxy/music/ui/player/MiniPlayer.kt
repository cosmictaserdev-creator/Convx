/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * 
 * Performance optimized MiniPlayer - prevents unnecessary recomposition
 */

package com.convxy.music.ui.player

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
import com.convxy.music.ui.component.ScrollingWaveformSeekBar
import com.convxy.music.ui.component.rememberPlaybackFraction
import com.convxy.music.ui.component.thumbnailPx
import com.convxy.music.constants.MiniPlayerWaveformKey
import com.convxy.music.constants.PlayerGradientAngleKey
import com.convxy.music.constants.PlayerGradientStopsKey
import com.convxy.music.ui.theme.decodeGradientStops
import com.convxy.music.ui.theme.tiltedGradient
import com.convxy.music.constants.PlayerStaticColorKey
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.blur
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
import com.convxy.music.LocalDatabase
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.player.customize.PlayerGlyph
import com.convxy.music.ui.player.customize.PlayerIconSlot
import com.convxy.music.constants.CropAlbumArtKey
import com.convxy.music.constants.DarkModeKey
import com.convxy.music.constants.MiniPlayerBackgroundStyleKey
import com.convxy.music.constants.MiniPlayerHeight
import com.convxy.music.constants.PlayerBackgroundStyle
import com.convxy.music.constants.PureBlackMiniPlayerKey
import com.convxy.music.constants.SwipeSensitivityKey
import com.convxy.music.constants.SwipeThumbnailKey
import com.convxy.music.constants.ThumbnailCornerRadius
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.constants.UseNewMiniPlayerDesignKey
import com.convxy.music.db.entities.ArtistEntity
import com.convxy.music.listentogether.ListenTogetherManager
import com.convxy.music.models.MediaMetadata
import com.convxy.music.playback.CastConnectionHandler
import com.convxy.music.playback.PlayerConnection
import com.convxy.music.ui.screens.settings.DarkMode
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.GlassComponent
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convxy.music.ui.component.isGlassAllowed
import com.convxy.music.ui.component.liquidGlass
import com.convxy.music.ui.theme.PlayerColorExtractor
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import com.convxy.music.vivimusic.AudioDeviceBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.convxy.music.vivimusic.isBluetoothHeadphoneConnected
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.convxy.music.ui.component.Icon as MIcon

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
    
    // Create stable progress state - doesn't cause recomposition on position changes
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

// ============================================================================
// NEW MINI PLAYER DESIGN
// ============================================================================

@Composable
private fun NewMiniPlayer(
    progressState: ProgressState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    
    // Theme settings - these rarely change
    val pureBlack by rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    
    val miniPlayerBackground by rememberEnumPreference(MiniPlayerBackgroundStyleKey, defaultValue = PlayerBackgroundStyle.DEFAULT)
    val miniPlayerWaveform by rememberPreference(MiniPlayerWaveformKey, defaultValue = true)
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val waveFraction = rememberPlaybackFraction(playerConnection.player, isPlaying)
    
    // Player states - only collect what's needed at this level
    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    
    // Cast state - safely access castConnectionHandler to prevent crashes during service lifecycle changes
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    // Audio Output State
    val context = LocalContext.current
    val isBluetoothConnected = isBluetoothHeadphoneConnected(context)
    var showAudioDeviceBottomSheet by remember { mutableStateOf(false) }

    // Swipe settings
    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    
    // Disable swipe for Listen Together guests
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isInRoom && !it.isHost } ?: false
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    // Swipe thresholds below were tuned as raw pixel counts, which is only ~14dp
    // on a dense (~3.5x) screen — well within a tap's incidental finger drift,
    // so plain taps on the mini player were misread as deliberate swipes and
    // skipped the track. Scaling them by density makes them density-independent.
    val density = LocalDensity.current.density

    val configuration = LocalConfiguration.current
    // Cap regardless of orientation — portrait tablets used to get a
    // full-width stretched bar with unchanged small fixed-dp icons, which
    // read as broken rather than intentional.
    val isTablet = remember(configuration.screenWidthDp) {
        configuration.screenWidthDp >= 600
    }

    // Swipe animation state
    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    val animationSpec = remember {
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    }

    // How far the card must travel before releasing changes track.
    //
    // This was a logistic curve that produced ~400dp — about 1200px at 3x, on a
    // 1080px screen. It could never be reached, so releasing the card never
    // changed the song no matter how far it was dragged. Expressed as a fraction
    // of the screen instead, so it is reachable on every device: sensitivity 0
    // needs 45% of the width, sensitivity 1 needs 12%.
    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, density) {
        val screenWidthPx = configuration.screenWidthDp * density
        (screenWidthPx * (0.45f - 0.33f * swipeSensitivity.coerceIn(0f, 1f))).roundToInt()
    }

    val (gradientColors, onGradientColorsChange) = remember { mutableStateOf<List<Color>>(emptyList()) }

    MiniPlayerColorExtractor(
        mediaMetadata = mediaMetadata,
        miniPlayerBackground = miniPlayerBackground,
        onGradientColorsChange = onGradientColorsChange
    )
    
    // Memoize colors
    val miniPlayerGlass = LocalGlassEffectConfig.current
    val usesGlassSurface =
        miniPlayerGlass.isEnabledFor(GlassComponent.MINI_PLAYER) && isGlassAllowed()
    // With glass on, liquidGlass paints the surface and this must stay clear or the
    // capture is hidden behind an opaque fill. With glass off it used to paint a
    // flat surfaceContainer, which over artwork reads as a black blob pasted on the
    // screen — so fall back to a translucent elevated surface that still lets the
    // content behind show, rather than an opaque one.
    val backgroundColor = when {
        usesGlassSurface -> Color.Transparent
        pureBlack && useDarkTheme -> Color.Black.copy(alpha = 0.82f)
        else -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f)
    }
    val isDynamicBackground = miniPlayerBackground != PlayerBackgroundStyle.DEFAULT

    val glassConfig = LocalGlassEffectConfig.current
    val dynamicContentColor = if (
        glassConfig.isEnabledFor(GlassComponent.MINI_PLAYER) &&
        isGlassAllowed()
    ) {
        glassConfig.textColor
    } else {
        Color.White
    }
    val primaryColor = if (isDynamicBackground) dynamicContentColor else MaterialTheme.colorScheme.primary
    val outlineColor = if (isDynamicBackground) dynamicContentColor.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
    val onSurfaceColor = if (isDynamicBackground) dynamicContentColor else MaterialTheme.colorScheme.onSurface
    val errorColor = MaterialTheme.colorScheme.error

    // Same finger-tracking glow as the nav bar's drag puck (ported from Kyant0's
    // catalog). Its gesture tracking never consumes pointer events, so it's safe
    // stacked alongside the swipe-to-skip drag detector.
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
                                // Mean velocity across the gesture, px/ms. The old threshold
                                // was ~7.4px/ms against a mean that is 1-3px/ms for a real
                                // swipe, so this branch never fired either.
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val dragged = kotlin.math.abs(currentOffset)

                                // Either drag past the commit distance, or flick: a short
                                // fast throw should not have to cross the full distance.
                                val shouldChangeSong = dragged > autoSwipeThreshold ||
                                    (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f)

                                if (shouldChangeSong) {
                                    // Through the connection, not player directly: the wrapper
                                    // is what routes to Cast when casting, re-prepares a player
                                    // sitting in ENDED/IDLE, and fires the skip callbacks. The
                                    // raw calls silently did nothing in all of those states.
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
                .clip(RoundedCornerShape(32.dp))
                .background(color = backgroundColor)
                .border(1.dp, outlineColor.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                .then(interactiveHighlight.modifier)
                .then(interactiveHighlight.gestureModifier)
        ) {
            // Background Layers
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
                // Thumbnail album art
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
                            placeholder = painterResource(R.drawable.thumbnail_fallback),
                            error = painterResource(R.drawable.thumbnail_fallback),
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Center: song info + seek bar below
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

                // Play button (compact - no album art)
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

                // Skip next (Accord icon)
                IconButton(
                    // Wrapper, not the raw player — same reason as the swipe above:
                    // this button did nothing while casting, and did not re-prepare
                    // a player that had reached the end of the queue.
                    onClick = { if (canSkipNext) playerConnection.seekToNext() },
                    enabled = canSkipNext
                ) {
                    Icon(
                        painter = painterResource(R.drawable.apple_skip_next),
                        contentDescription = null,
                        tint = if (canSkipNext) onSurfaceColor else onSurfaceColor.copy(alpha = 0.4f),
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Cast indicator
                if (isCasting) {
                    Icon(
                        painter = painterResource(R.drawable.cast_connected),
                        contentDescription = "Casting",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Audio Device Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color = primaryColor.copy(alpha = 0.1f))
                        .clickable { showAudioDeviceBottomSheet = true }
                ) {
                    Icon(
                        imageVector = if (isBluetoothConnected) Icons.Default.Headphones else Icons.Default.Speaker,
                        contentDescription = stringResource(R.string.audio_devices),
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
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

    if (showAudioDeviceBottomSheet) {
        AudioDeviceBottomSheet(onDismiss = { showAudioDeviceBottomSheet = false })
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
    val isListenTogetherGuest = listenTogetherManager?.let { it.isInRoom && !it.isHost } ?: false
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
                // Draw progress arc - this reads progressState.progress during draw phase only
                val progress = progressState.progress
                val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                val startAngle = -90f
                val sweepAngle = 360f * progress
                val diameter = size.minDimension
                val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                
                // Draw track
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = stroke
                )
                // Draw progress
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
                val iconRes = when {
                    isListenTogetherGuest -> if (isMuted) R.drawable.volume_off else R.drawable.volume_up
                    playbackState == Player.STATE_ENDED -> R.drawable.replay
                    else -> null
                }
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
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
                        placeholder = painterResource(R.drawable.thumbnail_fallback),
                        error = painterResource(R.drawable.thumbnail_fallback),
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
                        fallback = if (isListenTogetherGuest) {
                            if (isMuted) R.drawable.volume_off else R.drawable.volume_up
                        } else if (playbackState == Player.STATE_ENDED) {
                            R.drawable.replay
                        } else {
                            R.drawable.play
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

// ============================================================================
// LEGACY MINI PLAYER DESIGN
// ============================================================================

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
    val isListenTogetherGuest = listenTogetherManager?.let { it.isInRoom && !it.isHost } ?: false
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

    // How far the card must travel before releasing changes track.
    //
    // This was a logistic curve that produced ~400dp — about 1200px at 3x, on a
    // 1080px screen. It could never be reached, so releasing the card never
    // changed the song no matter how far it was dragged. Expressed as a fraction
    // of the screen instead, so it is reachable on every device: sensitivity 0
    // needs 45% of the width, sensitivity 1 needs 12%.
    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, density) {
        val screenWidthPx = configuration.screenWidthDp * density
        (screenWidthPx * (0.45f - 0.33f * swipeSensitivity.coerceIn(0f, 1f))).roundToInt()
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .then(if (isTablet) Modifier.width(500.dp) else Modifier.fillMaxWidth())
            .height(MiniPlayerHeight)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(
                if (pureBlack && isSystemInDarkTheme()) Color.Black
                else MaterialTheme.colorScheme.surfaceContainer
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
                                // Mean velocity across the gesture, px/ms. The old threshold
                                // was ~7.4px/ms against a mean that is 1-3px/ms for a real
                                // swipe, so this branch never fired either.
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val dragged = kotlin.math.abs(currentOffset)

                                // Either drag past the commit distance, or flick: a short
                                // fast throw should not have to cross the full distance.
                                val shouldChangeSong = dragged > autoSwipeThreshold ||
                                    (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f)

                                if (shouldChangeSong) {
                                    // Through the connection, not player directly: the wrapper
                                    // is what routes to Cast when casting, re-prepares a player
                                    // sitting in ENDED/IDLE, and fires the skip callbacks. The
                                    // raw calls silently did nothing in all of those states.
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
                Icon(painter = painterResource(R.drawable.apple_skip_next), contentDescription = null)
            }
        }

        // Swipe indicator
        if (offsetXAnimatable.value.absoluteValue > 50f) {
            Box(
                modifier = Modifier
                    .align(if (offsetXAnimatable.value > 0) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (offsetXAnimatable.value > 0) R.drawable.apple_skip_previous else R.drawable.apple_skip_next
                    ),
                    contentDescription = null,
                    tint = primaryColor.copy(
                        alpha = (offsetXAnimatable.value.absoluteValue / autoSwipeThreshold).coerceIn(0f, 1f)
                    ),
                    modifier = Modifier.size(24.dp)
                )
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
    val isListenTogetherGuest = listenTogetherManager?.let { it.isInRoom && !it.isHost } ?: false
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
            isListenTogetherGuest -> Icon(
                painter = painterResource(if (isMuted) R.drawable.volume_off else R.drawable.volume_up),
                contentDescription = null,
            )
            playbackState == Player.STATE_ENDED -> Icon(
                painter = painterResource(R.drawable.replay),
                contentDescription = null,
            )
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
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaMetadata.thumbnailUrl)
                    .size(CoilSize(thumbnailPx(48.dp), thumbnailPx(48.dp)))
                    .crossfade(false)
                    .build(),
                contentDescription = null,
                contentScale = if (cropAlbumArt) ContentScale.Crop else ContentScale.Fit,
                placeholder = painterResource(R.drawable.thumbnail_fallback),
                error = painterResource(R.drawable.thumbnail_fallback),
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
                    Icon(
                        painter = painterResource(R.drawable.info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(),
            )

            if (mediaMetadata.artists.any { it.name.isNotBlank() }) {
                Text(
                    text = mediaMetadata.artists.joinToString { it.name },
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ============================================================================
// ISOLATED BUTTON COMPOSABLES - Prevent parent recomposition
// ============================================================================


@Composable
private fun FavoriteButton(
    songId: String,
    onSurfaceColor: Color,
    errorColor: Color,
    outlineColor: Color
) {
    val database = LocalDatabase.current
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
        Icon(
            painter = painterResource(if (isLiked) R.drawable.favorite else R.drawable.favorite_border),
            contentDescription = null,
            tint = if (isLiked) errorColor else onSurfaceColor.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
    }
}
@Composable
internal fun MiniPlayerColorExtractor(
    mediaMetadata: MediaMetadata?,
    miniPlayerBackground: PlayerBackgroundStyle,
    onGradientColorsChange: (List<Color>) -> Unit
) {
    val context = LocalContext.current
    val fallbackColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()

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
                        .size(100, 100)
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
                                PlayerColorExtractor.extractGradientColors(
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(mediaMetadata?.thumbnailUrl)
                        .size(128, 128)
                        .allowHardware(false)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(30.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            } else {
                // Modifier.blur no-ops below API 31 — without this fallback the
                // gate above left nothing drawn at all. Solid black instead.
                Box(modifier = Modifier.fillMaxSize().background(Color.Black))
            }
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
                val progress: ComposeState<Float> = if (isPlaying) {
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
            val rotation: ComposeState<Float> = if (isPlaying) {
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
                // Modifier.blur no-ops below API 31 — an unblurred rotating 128x128
                // crop is glitchy, so fall back to solid black there instead.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val matrix = remember { ColorMatrix().apply { setToSaturation(1.6f) } }
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(mediaMetadata?.thumbnailUrl)
                            .size(128, 128)
                            .allowHardware(false)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.colorMatrix(matrix),
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(40.dp)
                            .graphicsLayer { rotationZ = rotation.value }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                }
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }
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

