/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
// Apple Music player UI adapted from an open-source (GPL-3.0) base.

package com.convx.music.ui.player

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFAirplayAudio
import com.composables.sfsymbols.monochrome.SFArrowCounterclockwise
import com.composables.sfsymbols.monochrome.SFForwardFill
import com.composables.sfsymbols.monochrome.SFPauseFill
import com.composables.sfsymbols.monochrome.SFPlayFill
import com.composables.sfsymbols.monochrome.SFSpeakerSlashFill
import com.composables.sfsymbols.monochrome.SFSpeakerWave2Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.*
import com.convx.music.listentogether.ListenTogetherManager
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.CastConnectionHandler
import com.convx.music.playback.PlayerConnection
import com.convx.music.ui.screens.settings.DarkMode
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import com.convx.music.ui.component.Icon as MIcon
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppleMiniPlayer(
    progressState: ProgressState,
    modifier: Modifier = Modifier
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    
    // Theme settings
    val pureBlack by rememberPreference(PureBlackMiniPlayerKey, defaultValue = false)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val preferredDark = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    val useDarkTheme = com.convx.music.ui.theme.LocalForcedDark.current || preferredDark
    
    val miniPlayerBackground by rememberEnumPreference(MiniPlayerBackgroundStyleKey, defaultValue = PlayerBackgroundStyle.DEFAULT)
    
    // Player states
    val playbackState by playerConnection.playbackState.collectAsStateWithLifecycle()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    
    // Cast state
    val castHandler = remember(playerConnection) {
        try {
            playerConnection.service.castConnectionHandler
        } catch (e: Exception) {
            null
        }
    }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }

    // Swipe settings
    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    
    // Disable swipe for Listen Together guests
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest
    
    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    
    val configuration = LocalConfiguration.current
    val isTabletLandscape = remember(configuration.screenWidthDp, configuration.orientation) {
        configuration.screenWidthDp >= 600 && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    // Swipe animation state
    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    val animationSpec = remember {
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    }

    val autoSwipeThreshold = remember(swipeSensitivity) {
        (600 / (1f + kotlin.math.exp(-(-11.44748 * swipeSensitivity + 9.04945)))).roundToInt()
    }

    val (gradientColors, onGradientColorsChange) = remember { mutableStateOf<List<Color>>(emptyList()) }

    MiniPlayerColorExtractor(
        mediaMetadata = mediaMetadata,
        miniPlayerBackground = miniPlayerBackground,
        onGradientColorsChange = onGradientColorsChange
    )
    
    // Memoize colors
    val backgroundColor = if (pureBlack && useDarkTheme) Color.Black else AppTheme.colorScheme.surface
    val isDynamicBackground = miniPlayerBackground != PlayerBackgroundStyle.DEFAULT
    
    val primaryColor = if (isDynamicBackground) Color.White else AppTheme.colorScheme.primary
    val outlineColor = if (isDynamicBackground) Color.White.copy(alpha = 0.5f) else AppTheme.colorScheme.outline
    val onSurfaceColor = if (isDynamicBackground) Color.White else AppTheme.colorScheme.onSurface
    val errorColor = AppTheme.colorScheme.error

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
                                val canSkipPreviousLocal = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNextLocal = playerConnection.player.nextMediaItemIndex != -1
                                val tryingToSwipeRight = adjustedDragAmount > 0
                                val tryingToSwipeLeft = adjustedDragAmount < 0
                                val allowLeft = tryingToSwipeLeft && canSkipNextLocal
                                val allowRight = tryingToSwipeRight && canSkipPreviousLocal

                                val canReturnToCenter =
                                    (tryingToSwipeRight && !canSkipPreviousLocal && offsetXAnimatable.value < 0) ||
                                            (tryingToSwipeLeft && !canSkipNextLocal && offsetXAnimatable.value > 0)

                                if (allowLeft || allowRight || canReturnToCenter) {
                                    totalDragDistance += kotlin.math.abs(adjustedDragAmount)
                                    coroutineScope.launch {
                                        offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount)
                                    }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val minDistanceThreshold = 50f
                                val velocityThreshold = (swipeSensitivity * -8.25f) + 8.5f

                                val canSkipPreviousLocal = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNextLocal = playerConnection.player.nextMediaItemIndex != -1

                                val shouldChangeSong = (kotlin.math.abs(currentOffset) > minDistanceThreshold && velocity > velocityThreshold) ||
                                    (kotlin.math.abs(currentOffset) > autoSwipeThreshold)

                                if (shouldChangeSong) {
                                    if (currentOffset > 0 && canSkipPreviousLocal) {
                                        playerConnection.player.seekToPreviousMediaItem()
                                    } else if (currentOffset <= 0 && canSkipNextLocal) {
                                        playerConnection.player.seekToNext()
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
                .then(if (isTabletLandscape) Modifier.width(500.dp).align(Alignment.Center) else Modifier.fillMaxWidth())
                // Reserve MiniPlayerHeight even though the visible bar is 48 dp tall.
                .height(48.dp)
                .align(Alignment.Center)
                .offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }
                .clip(RoundedCornerShape(24.dp))
                .background(color = backgroundColor)
        ) {
            MiniPlayerBackgroundLayer(
                style = miniPlayerBackground,
                mediaMetadata = mediaMetadata,
                gradientColors = gradientColors
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter)
                    .drawWithContent {
                        val progress = progressState.progress
                        val trackColor = outlineColor.copy(alpha = 0.2f)
                        drawRect(trackColor)
                        drawRect(primaryColor, size = Size(size.width * progress, size.height))
                    }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = outlineColor.copy(alpha = 0.2f))
                ) {
                    mediaMetadata?.let { metadata ->
                        AsyncImage(
                            model = metadata.thumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    val error by LocalPlayerConnection.current?.error?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }
                    
                    mediaMetadata?.let { metadata ->
                        Text(
                            text = metadata.title,
                            color = onSurfaceColor,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.23).sp,
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
                                    fontSize = 11.sp,
                                    lineHeight = 13.sp,
                                    letterSpacing = 0.06.sp,
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

                Spacer(modifier = Modifier.width(8.dp))

                if (isCasting) {
                    SymbolIcon(symbol = sf("SFAirplayAudio"), contentDescription = "Casting", size = 20.dp, tint = primaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (isListenTogetherGuest) {
                                playerConnection.toggleMute()
                                return@clickable
                            }
                            if (isCasting) {
                                val castIsPlaying = castHandler?.castIsPlaying?.value ?: false
                                if (castIsPlaying) castHandler?.pause() else castHandler?.play()
                            } else if (playbackState == Player.STATE_ENDED) {
                                playerConnection.player.seekTo(0, 0)
                                playerConnection.player.playWhenReady = true
                            } else {
                                playerConnection.togglePlayPause()
                            }
                        }
                ) {
                    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
                    val castIsPlaying by castHandler?.castIsPlaying?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
                    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying
                    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()

                    SymbolIcon(symbol = when {
                            isListenTogetherGuest -> if (isMuted) sf("SFSpeakerSlashFill") else sf("SFSpeakerWave2Fill")
                            playbackState == Player.STATE_ENDED -> sf("SFArrowCounterclockwise")
                            effectiveIsPlaying -> sf("SFPauseFill")
                            else -> sf("SFPlayFill")
                        }, contentDescription = null, size = 17.dp, tint = onSurfaceColor)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable(enabled = canSkipNext && !isListenTogetherGuest) {
                            playerConnection.seekToNext()
                        }
                ) {
                    SymbolIcon(symbol = sf("SFForwardFill"), contentDescription = "Next", size = 17.dp, tint = if (canSkipNext && !isListenTogetherGuest) onSurfaceColor else onSurfaceColor.copy(alpha = 0.3f))
                }
            }
        }
    }
}
