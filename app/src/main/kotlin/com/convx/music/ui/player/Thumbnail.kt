/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import com.convx.music.ui.utils.rememberEdgeAwareFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import androidx.compose.material3.MaterialShapes
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.drop
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Bitmap
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.core.graphics.createBitmap
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Size as CoilSize
import coil3.toBitmap
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.constants.CropAlbumArtKey
import com.convx.music.constants.HidePlayerThumbnailKey
import com.convx.music.constants.PlayerBackgroundStyle
import com.convx.music.constants.PlayerBackgroundStyleKey
import com.convx.music.constants.PlayerHorizontalPadding
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.constants.PlayerArtworkStyle
import com.convx.music.constants.PlayerArtworkStyleKey
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.convx.music.constants.SeekExtraSeconds
import com.convx.music.constants.SwipeThumbnailKey
import com.convx.music.constants.ThumbnailCornerRadiusKey
import com.convx.music.constants.ThumbnailCornerRadius
import com.convx.music.listentogether.RoomRole
import com.convx.music.ui.component.CastButton
import com.convx.music.utils.rememberEnumPreference
import com.convx.music.constants.CanvasSource
import com.convx.music.constants.CanvasSourceKey
import com.convx.music.playback.CanvasSelectionRepository
import com.convx.music.constants.CanvasThumbnailAnimationKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.canvas.TidalCanvasProvider
import com.convx.music.canvas.CanvasArtwork
import com.convx.music.extensions.metadata
import com.convx.music.ui.utils.resize
import com.convx.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import com.convx.music.applecanvas.AppleMusicCanvasProvider
import com.convx.music.convxcanvas.ConvxCanvasProvider
import com.convx.music.utils.SpotifyCanvasLookup
import java.util.Locale
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Pre-calculated thumbnail dimensions to avoid repeated calculations during recomposition.
 * All values are computed once and cached.
 */
@Immutable
data class ThumbnailDimensions(
    val itemWidth: Dp,
    val containerSize: Dp,
    val thumbnailSize: Dp,
    val cornerRadius: Dp
)

/**
 * Cached media items data to prevent recalculation on every recomposition.
 */
@Immutable
data class MediaItemsData(
    val items: List<MediaItem>,
    val currentIndex: Int
)

/**
 * Calculate thumbnail dimensions once based on container size.
 * In landscape mode, uses the smaller dimension (height) to ensure square thumbnail fits.
 */
@Stable
private fun calculateThumbnailDimensions(
    containerWidth: Dp,
    containerHeight: Dp = containerWidth,
    horizontalPadding: Dp = PlayerHorizontalPadding,
    cornerRadius: Dp = ThumbnailCornerRadius,
    isLandscape: Boolean = false
): ThumbnailDimensions {
    // In landscape, use height as the constraining dimension for a square thumbnail
    val effectiveSize = if (isLandscape) {
        minOf(containerWidth, containerHeight) - (horizontalPadding * 2)
    } else {
        containerWidth - (horizontalPadding * 2)
    }
    return ThumbnailDimensions(
        itemWidth = containerWidth,
        containerSize = containerWidth,
        thumbnailSize = effectiveSize,
        cornerRadius = cornerRadius * 2
    )
}

/** Builds the swipe preview from current, previous, and next items in shuffle order. */
@Stable
private fun getMediaItems(
    player: Player,
    swipeThumbnail: Boolean
): MediaItemsData {
    val timeline = player.currentTimeline
    val currentIndex = player.currentMediaItemIndex
    val shuffleModeEnabled = player.shuffleModeEnabled
    
    val currentMediaItem = try {
        player.currentMediaItem
    } catch (e: Exception) { null }
    
    val previousMediaItem = if (swipeThumbnail && !timeline.isEmpty) {
        val previousIndex = timeline.getPreviousWindowIndex(
            currentIndex,
            Player.REPEAT_MODE_OFF,
            shuffleModeEnabled
        )
        if (previousIndex != C.INDEX_UNSET) {
            try { player.getMediaItemAt(previousIndex) } catch (e: Exception) { null }
        } else null
    } else null

    val nextMediaItem = if (swipeThumbnail && !timeline.isEmpty) {
        val nextIndex = timeline.getNextWindowIndex(
            currentIndex,
            Player.REPEAT_MODE_OFF,
            shuffleModeEnabled
        )
        if (nextIndex != C.INDEX_UNSET) {
            try { player.getMediaItemAt(nextIndex) } catch (e: Exception) { null }
        } else null
    } else null

    val items = listOfNotNull(previousMediaItem, currentMediaItem, nextMediaItem)
    val currentMediaIndex = items.indexOf(currentMediaItem)
    
    return MediaItemsData(items, currentMediaIndex)
}

@Stable
@Composable
private fun getTextColor(playerBackground: PlayerBackgroundStyle): Color {
    return when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> AppTheme.colorScheme.onBackground
        PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.GLOW_ANIMATED, PlayerBackgroundStyle.APPLE_MUSIC, PlayerBackgroundStyle.LIVE_MESH, PlayerBackgroundStyle.FLUID_MESH, PlayerBackgroundStyle.STATIC, PlayerBackgroundStyle.CUSTOM_GRADIENT, PlayerBackgroundStyle.GLASS -> Color.White
    }
}


object CanvasArtworkPlaybackCache {
    private const val defaultMaxSize = 256
    private val map = LinkedHashMap<String, CanvasArtwork>(defaultMaxSize, 0.75f, true)
    @Volatile private var maxSize = defaultMaxSize

    @Synchronized
    fun get(mediaId: String): CanvasArtwork? {
        if (maxSize <= 0) return null
        return map[mediaId]
    }

    @Synchronized
    fun put(mediaId: String, artwork: CanvasArtwork) {
        val limit = maxSize
        if (limit <= 0) return
        if (mediaId.isBlank()) return
        map[mediaId] = artwork
        while (map.size > limit) {
            val it = map.entries.iterator()
            if (it.hasNext()) {
                it.next()
                it.remove()
            }
        }
    }

    @Synchronized
    fun clear() {
        map.clear()
    }

    @Synchronized
    fun setMaxSize(value: Int) {
        maxSize = value.coerceAtLeast(0)
        if (maxSize == 0) {
            map.clear()
            return
        }
        while (map.size > maxSize) {
            val it = map.entries.iterator()
            if (it.hasNext()) {
                it.next()
                it.remove()
            } else {
                break
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Thumbnail(
    sliderPositionProvider: () -> Long?,
    modifier: Modifier = Modifier,
    isPlayerExpanded: () -> Boolean = { true },
    isLandscape: Boolean = false,
    isListenTogetherGuest: Boolean = false,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current

    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val error by playerConnection.error.collectAsStateWithLifecycle()
    val queueTitle by playerConnection.queueTitle.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()

    // Disable swipe for Listen Together guests
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest
    val hidePlayerThumbnail by rememberPreference(HidePlayerThumbnailKey, false)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)
    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.GRADIENT
    )
    val thumbnailCornerRadius by rememberPreference(ThumbnailCornerRadiusKey, defaultValue = 3f)
    
    // Pre-calculate text color based on background style
    val textBackgroundColor = getTextColor(playerBackground)
    
    // Grid state
    val thumbnailLazyGridState = rememberLazyGridState()
    
    val mediaItemsData by remember(
        playerConnection.player.currentMediaItemIndex,
        playerConnection.player.shuffleModeEnabled,
        swipeThumbnail,
        mediaMetadata
    ) {
        derivedStateOf {
            getMediaItems(playerConnection.player, swipeThumbnail)
        }
    }
    
    val mediaItems = mediaItemsData.items
    val currentMediaIndex = mediaItemsData.currentIndex

    // Snap behavior - created once per grid state
    val thumbnailSnapLayoutInfoProvider = remember(thumbnailLazyGridState) {
        ThumbnailSnapLayoutInfoProvider(
            lazyGridState = thumbnailLazyGridState,
            positionInLayout = { layoutSize, itemSize ->
                (layoutSize / 2f - itemSize / 2f)
            },
            velocityThreshold = 500f
        )
    }

    // Guards the swipe detector below from mistaking the position-resync
    // scroll (also below) for a user swipe — that scroll also flips
    // isScrollInProgress, which is otherwise indistinguishable from a drag.
    var isSyncingPosition by remember { mutableStateOf(false) }

    // Handle swipe to change song. Watch the scroll end (snapshotFlow) instead of
    // keying on the scroll offset, so per-pixel scroll changes don't recompose
    // the whole thumbnail carousel.
    LaunchedEffect(swipeThumbnail, currentMediaIndex, canSkipNext, canSkipPrevious) {
        if (!swipeThumbnail || currentMediaIndex < 0) return@LaunchedEffect
        // snapshotFlow emits its current value on collection start. Ignore it so a
        // position-resync scroll is not mistaken for a user swipe after this effect restarts.
        snapshotFlow { thumbnailLazyGridState.isScrollInProgress }
            .drop(1)
            .collect { scrolling ->
                if (!scrolling && !isSyncingPosition && thumbnailLazyGridState.firstVisibleItemScrollOffset == 0) {
                    val index = thumbnailLazyGridState.firstVisibleItemIndex
                    if (index > currentMediaIndex && canSkipNext) {
                        playerConnection.player.seekToNext()
                    } else if (index < currentMediaIndex && canSkipPrevious) {
                        playerConnection.player.seekToPreviousMediaItem()
                    }
                }
            }
    }

    LaunchedEffect(mediaMetadata, canSkipPrevious, canSkipNext) {
        val index = maxOf(0, currentMediaIndex)
        if (index >= 0 && index < mediaItems.size) {
            isSyncingPosition = true
            try {
                thumbnailLazyGridState.animateScrollToItem(index)
            } catch (e: Exception) {
                thumbnailLazyGridState.scrollToItem(index)
            } finally {
                isSyncingPosition = false
            }
        }
    }

    // Prefetch high-quality thumbnails for the carousel items (previous, current, and next)
    LaunchedEffect(mediaItems) {
        mediaItems.forEach { item ->
            val artworkUri = item.mediaMetadata.artworkUri?.toString()?.resize(1200, 1200) ?: return@forEach
            val request = ImageRequest.Builder(context)
                .data(artworkUri)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
            SingletonImageLoader.get(context).enqueue(request)
        }
    }

    // Seek effect state
    var showSeekEffect by remember { mutableStateOf(false) }
    var seekDirection by remember { mutableStateOf("") }

    Box(
        modifier = modifier
    ) {
        // Error view
        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.Center),
        ) {
            error?.let { playbackError ->
                PlaybackError(
                    error = playbackError,
                    retry = playerConnection.player::prepare,
                )
            }
        }

        AnimatedVisibility(
            visible = error == null && !(playerBackground == PlayerBackgroundStyle.APPLE_MUSIC && !isLandscape),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .then(if (!isLandscape) Modifier.statusBarsPadding() else Modifier),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = if (isLandscape) Arrangement.Center else Arrangement.Top
            ) {
                // Now Playing header - hide in landscape mode
                if (!isLandscape) {
                    ThumbnailHeader(
                        queueTitle = queueTitle,
                        albumTitle = mediaMetadata?.album?.title,
                        textColor = textBackgroundColor
                    )
                }
                
                // Thumbnail content
                BoxWithConstraints(
                    contentAlignment = Alignment.Center,
                    modifier = if (isLandscape) {
                        Modifier.weight(1f, false)
                    } else {
                        Modifier.fillMaxSize()
                    }
                ) {
                    val dimensions = remember(maxWidth, maxHeight, isLandscape, thumbnailCornerRadius) {
                        calculateThumbnailDimensions(
                            containerWidth = maxWidth,
                            containerHeight = maxHeight,
                            cornerRadius = thumbnailCornerRadius.dp,
                            isLandscape = isLandscape
                        )
                    }

                    // Remember the onSeek callback to prevent recomposition
                    val onSeekCallback = remember {
                        { direction: String, showEffect: Boolean ->
                            seekDirection = direction
                            showSeekEffect = showEffect
                        }
                    }
                    
                    // Derive scroll enabled state to prevent unnecessary recomposition
                    val isScrollEnabled by remember(swipeThumbnail) {
                        derivedStateOf { swipeThumbnail && isPlayerExpanded() }
                    }
                    
                    LazyHorizontalGrid(
                        state = thumbnailLazyGridState,
                        rows = GridCells.Fixed(1),
                        flingBehavior = rememberEdgeAwareFlingBehavior(
                            thumbnailLazyGridState,
                            rememberSnapFlingBehavior(thumbnailSnapLayoutInfoProvider),
                        ),
                        userScrollEnabled = isScrollEnabled,
                        modifier = if (isLandscape) {
                            Modifier.size(dimensions.thumbnailSize + (PlayerHorizontalPadding * 2))
                        } else {
                            Modifier.fillMaxSize()
                        }
                    ) {
                        items(
                            items = mediaItems,
                            key = { item -> 
                                item.mediaId.ifEmpty { "unknown_${item.hashCode()}" }
                            }
                        ) { item ->
                            ThumbnailItem(
                                item = item,
                                dimensions = dimensions,
                                hidePlayerThumbnail = hidePlayerThumbnail,
                                cropAlbumArt = cropAlbumArt,
                                textBackgroundColor = textBackgroundColor,
                                layoutDirection = layoutDirection,
                                onSeek = onSeekCallback,
                                playerConnection = playerConnection,
                                context = context,
                                isLandscape = isLandscape,
                                isListenTogetherGuest = isListenTogetherGuest,
                                currentMediaId = mediaMetadata?.id,
                                currentMediaThumbnail = mediaMetadata?.thumbnailUrl,
                                playerBackground = playerBackground
                            )
                }
            }
        }

    }
}

        // Seek effect
        LaunchedEffect(showSeekEffect) {
            if (showSeekEffect) {
                delay(1000)
                showSeekEffect = false
            }
        }

        AnimatedVisibility(
            visible = showSeekEffect,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            SeekEffectOverlay(seekDirection = seekDirection)
        }
    }
}

/**
 * Header component showing "Now Playing" and queue/album title.
 */
@Composable
private fun ThumbnailHeader(
    queueTitle: String?,
    albumTitle: String?,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val listenTogetherManager = LocalListenTogetherManager.current
    val listenTogetherRoleState = listenTogetherManager?.role?.collectAsState(initial = RoomRole.NONE)
    // See Player.kt: gated on control mode, not on role.
    val canControlTogether = listenTogetherManager?.canControl?.collectAsState(initial = true)
    val isListenTogetherGuest = canControlTogether?.value == false
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 48.dp)
        ) {
            // Listen Together indicator
            if (listenTogetherRoleState?.value != RoomRole.NONE) {
                Text(
                    text = if (listenTogetherRoleState?.value == RoomRole.HOST) "Hosting Listen Together" else "Listening Together",
                    style = AppTheme.typography.titleMedium,
                    color = textColor
                )
            } else {
                Text(
                    text = stringResource(R.string.now_playing),
                    style = AppTheme.typography.titleMedium,
                    color = textColor
                )
            }
            val playingFrom = albumTitle ?: queueTitle // Prioritize album title
            androidx.compose.animation.AnimatedContent(
                targetState = playingFrom,
                transitionSpec = { androidx.compose.animation.fadeIn() togetherWith androidx.compose.animation.fadeOut() },
                label = "NowPlayingAnimation"
            ) { text ->
                if (!text.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = text,
                        style = AppTheme.typography.titleMedium,
                        color = textColor.copy(alpha = 0.8f),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
        }

        CastButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp),
            tintColor = textColor
        )
    }
}

/**
 * Individual thumbnail item in the carousel.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ThumbnailItem(
    item: MediaItem,
    dimensions: ThumbnailDimensions,
    hidePlayerThumbnail: Boolean,
    cropAlbumArt: Boolean,
    textBackgroundColor: Color,
    layoutDirection: LayoutDirection,
    onSeek: (String, Boolean) -> Unit,
    playerConnection: com.convx.music.playback.PlayerConnection,
    context: android.content.Context,
    isLandscape: Boolean = false,
    isListenTogetherGuest: Boolean = false,
    currentMediaId: String? = null,
    currentMediaThumbnail: String? = null,
    playerBackground: PlayerBackgroundStyle = PlayerBackgroundStyle.DEFAULT,
    modifier: Modifier = Modifier,
) {
    val (artworkStyle) = rememberEnumPreference(PlayerArtworkStyleKey, defaultValue = PlayerArtworkStyle.CARD)
    val coverStyle = artworkStyle.coverStyle()
    val rotatingThumbnail = coverStyle.spins
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val isCurrentItem = item.mediaId == currentMediaId

    // InfiniteRepeatable keeps ticking even when paused, so create it only while spinning.
    val rotation: State<Float> = rememberCoverRotation(coverStyle, isPlaying && isCurrentItem)

    val incrementalSeekSkipEnabled by rememberPreference(SeekExtraSeconds, defaultValue = false)
    var skipMultiplier by remember { mutableIntStateOf(1) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    val dataSaverEnabled by rememberPreference(DataSaverEnabledKey, defaultValue = false)
    val canvasThumbnailAnimationPref by rememberPreference(CanvasThumbnailAnimationKey, defaultValue = true)
    val canvasThumbnailAnimation = if (dataSaverEnabled) false else canvasThumbnailAnimationPref

    Box(
        modifier = modifier
            .then(
                if (isLandscape) {
                    Modifier.size(dimensions.thumbnailSize + (PlayerHorizontalPadding * 2))
                } else {
                    Modifier
                        .width(dimensions.itemWidth)
                        .fillMaxSize()
                }
            )
            .padding(horizontal = PlayerHorizontalPadding)
            .graphicsLayer {
                // Render entire thumbnail item on separate hardware layer for smooth animations
                // Offscreen compositing removed to prevent interference with AndroidView
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        if (isListenTogetherGuest) return@detectTapGestures

                        val currentPosition = playerConnection.player.currentPosition
                        val duration = playerConnection.player.duration

                        val now = System.currentTimeMillis()
                        if (incrementalSeekSkipEnabled && now - lastTapTime < 1000) {
                            skipMultiplier++
                        } else {
                            skipMultiplier = 1
                        }
                        lastTapTime = now

                        val skipAmount = 5000 * skipMultiplier

                        val isLeftSide = (layoutDirection == LayoutDirection.Ltr && offset.x < size.width / 2) ||
                                (layoutDirection == LayoutDirection.Rtl && offset.x > size.width / 2)

                        if (isLeftSide) {
                            playerConnection.player.seekTo((currentPosition - skipAmount).coerceAtLeast(0))
                            onSeek(context.getString(R.string.seek_backward_dynamic, skipAmount / 1000), true)
                        } else {
                            playerConnection.player.seekTo((currentPosition + skipAmount).coerceAtMost(duration))
                            onSeek(context.getString(R.string.seek_forward_dynamic, skipAmount / 1000), true)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val artworkDensity = androidx.compose.ui.platform.LocalDensity.current
        Box(
            modifier = Modifier
                .coverMorph(coverStyle)
                .size(dimensions.thumbnailSize)
                // Register the morph target shape and hide its traveling copy during handoff.
                .fullArtworkMorphTarget(
                    cornerRadiusPx = with(artworkDensity) {
                        when (artworkStyle) {
                            PlayerArtworkStyle.CARD -> dimensions.cornerRadius.toPx()
                            else -> dimensions.thumbnailSize.toPx() / 2f
                        }
                    },
                    // Cards travel between players; vinyl and clover covers resolve in place.
                    travels = artworkStyle == PlayerArtworkStyle.CARD,
                )
                .graphicsLayer {
                    rotationZ = rotation.value
                }
                .clip(coverStyle.shape(dimensions.cornerRadius))
                // A record spins with its label, so only a style whose art stays upright counter-rotates.
                .graphicsLayer {
                    rotationZ = if (coverStyle.artSpinsWithShape) 0f else -rotation.value
                }
        ) {
            if (hidePlayerThumbnail) {
                HiddenThumbnailPlaceholder(textBackgroundColor = textBackgroundColor)
            } else {
                val artworkUriToUse = if (item.mediaId == currentMediaId && !currentMediaThumbnail.isNullOrBlank()) {
                    currentMediaThumbnail
                } else {
                    item.mediaMetadata.artworkUri?.toString()
                }

                val crossfadeDurationMs by playerConnection.crossfadeDurationMs.collectAsStateWithLifecycle()
                ThumbnailImage(
                    artworkUri = artworkUriToUse?.resize(1200, 1200),
                    cropArtwork = cropAlbumArt,
                    crossfadeMillis = crossfadeDurationMs.takeIf { isCurrentItem && it > 0 }?.toInt() ?: 200,
                    contentKey = item.mediaId,
                )
            }
            
            coverStyle.Decoration(Modifier.fillMaxSize())

            if (canvasThumbnailAnimation && item.mediaId == currentMediaId && !rotatingThumbnail && playerBackground != PlayerBackgroundStyle.APPLE_MUSIC) {
                val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
                val disabledCanvasProviders by rememberPreference(com.convx.music.constants.CanvasDisabledProvidersKey, defaultValue = "")
                val manualCanvasRevision by CanvasSelectionRepository.selectionRevision.collectAsState()
                var canvasArtwork by remember(item.mediaId) { mutableStateOf<CanvasArtwork?>(null) }
                LaunchedEffect(item.mediaId, canvasSource, disabledCanvasProviders, manualCanvasRevision) {
                    val metadata = item.metadata
                    canvasArtwork = CanvasSelectionRepository.resolve(
                        context = context,
                        mediaId = item.mediaId,
                        title = metadata?.title ?: item.mediaMetadata.title?.toString().orEmpty(),
                        artist = metadata?.artists?.firstOrNull()?.name
                            ?: item.mediaMetadata.artist?.toString().orEmpty(),
                        album = metadata?.album?.title ?: item.mediaMetadata.albumTitle?.toString(),
                        source = canvasSource,
                    )
                }

                canvasArtwork?.let { artwork ->
                    CanvasArtworkPlayer(
                        primaryUrl = artwork.animated,
                        fallbackUrl = artwork.videoUrl,
                        isPlaying = isPlaying,
                        mediaId = item.mediaId,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Placeholder shown when thumbnail is hidden.
 */
@Composable
private fun HiddenThumbnailPlaceholder(
    textBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.convx_logo),
            contentDescription = stringResource(R.string.hide_player_thumbnail),
            tint = textBackgroundColor.copy(alpha = 0.7f),
            modifier = Modifier.size(120.dp)
        )
    }
}

/** Mesh resolution for the API<33 ripple-warp fallback. */
private const val MESH_COLS = 8
private const val MESH_ROWS = 8

/** How far a mesh vertex moves at the peak of the ripple (fallback path). */
private val RIPPLE_MAX_DISPLACEMENT = 14.dp

/** How many ripple rings fit across the artwork's radius (fallback path). */
private const val RIPPLE_RINGS = 3.0

/** Keep only the outgoing and current covers; let GC reclaim bitmaps after drawing ends. */
private const val MAX_CACHED_COVERS = 2

/**
 * Cached artwork transition. API 33+ uses an AGSL noise-field melt; older
 * devices use a bitmap-mesh ripple. The warp is content-agnostic because album
 * covers do not share features that a correspondence-based morph could align.
 */
@Composable
internal fun ThumbnailImage(
    artworkUri: String?,
    cropArtwork: Boolean,
    modifier: Modifier = Modifier,
    // Match DJ handoffs to the audio crossfade; the resolved duration is clamped below.
    crossfadeMillis: Int = 200,
    // Keep fallback URL changes for the same song from triggering a self-transition.
    contentKey: String? = null,
    // Decode these URLs ahead of time so transitions start with loaded bitmaps.
    preloadUrls: List<String> = emptyList(),
    /** Skip the transition when another animation has already moved the incoming cover into place. */
    morphOnChange: Boolean = true,
    bitmapStore: com.convx.artwork.ArtworkBitmapStore? = null,
) {
    // Keep the transition visible without letting long audio crossfades make it drag on.
    val resolvedCrossfadeMillis = crossfadeMillis.coerceIn(450, 8_000)
    val context = LocalContext.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val shaderMorphSupported = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU

    var boxSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var currentUrl by remember { mutableStateOf(artworkUri) }
    var currentKey by remember { mutableStateOf(contentKey) }
    var outgoingUrl by remember { mutableStateOf<String?>(null) }
    val morph = remember { Animatable(1f) }
    // Update shader time only during a transition to avoid idle frame updates.
    var timeSeconds by remember { mutableFloatStateOf(0f) }

    // Raw decoded covers, keyed by resolved URL (a fallback rung gets its own
    // slot rather than overwriting the URL that failed).
    val ownedStore = remember { com.convx.artwork.ArtworkBitmapStore() }
    val store = bitmapStore ?: ownedStore
    val rawBitmaps = store.images
    // The API<33 mesh fallback needs a bitmap matching the destination size; the shader uses raw art.
    val scaledBitmaps = remember { androidx.compose.runtime.mutableStateMapOf<String, Bitmap>() }

    suspend fun ensureLoaded(url: String?) {
        var candidate = url
        while (candidate != null && !rawBitmaps.containsKey(candidate)) {
            val bitmap = store.load(candidate) { loadArtworkBitmap(context, it) }
            if (bitmap != null) {
                rawBitmaps[candidate] = bitmap
                return
            }
            candidate = nextThumbnailFallback(candidate)
        }
    }

    LaunchedEffect(artworkUri) {
        if (artworkUri == currentUrl) return@LaunchedEffect
        if (!morphOnChange) {
            outgoingUrl = null
            currentUrl = artworkUri
            currentKey = contentKey
            morph.snapTo(1f)
            return@LaunchedEffect
        }
        if (contentKey != null && contentKey == currentKey) {
            // Same song, new URL rung: adopt silently, no self-melt.
            currentUrl = artworkUri
            return@LaunchedEffect
        }
        ensureLoaded(artworkUri)
        // On interruption, continue from the cover that was mostly visible to avoid a jump.
        val interrupted = outgoingUrl != null
        if (!interrupted || morph.value >= 0.5f) outgoingUrl = currentUrl
        currentUrl = artworkUri
        currentKey = contentKey
        morph.snapTo(0f)
        coroutineScope {
            val timeJob = if (shaderMorphSupported) {
                launch {
                    val start = withFrameNanos { it }
                    while (isActive) {
                        timeSeconds = (withFrameNanos { it } - start) / 1_000_000_000f
                    }
                }
            } else {
                null
            }
            // Eased at both ends: linear progress made the melt visibly start and stop.
            morph.animateTo(1f, animationSpec = tween(resolvedCrossfadeMillis, easing = SeamlessEasing))
            timeJob?.cancel()
        }
        outgoingUrl = null
        // Only the just-landed cover matters from here — this is a
        // per-composable cache for morph-ready bitmaps, not a general
        // image cache (Coil already owns that). Preloaded neighbours are
        // spared so the next skip starts warm.
        if (bitmapStore == null && rawBitmaps.size > MAX_CACHED_COVERS) {
            rawBitmaps.keys.filterNot { it == currentUrl || it in preloadUrls }.forEach { rawBitmaps.remove(it) }
        }
        val keep = currentUrl
        scaledBitmaps.keys.filterNot { keep != null && it.startsWith("$keep@") }
            .forEach { scaledBitmaps.remove(it) }
    }
    LaunchedEffect(Unit) { ensureLoaded(currentUrl) }
    // Prerender: decode neighbour covers ahead of the skip so both melt
    // endpoints are resident when the transition fires.
    LaunchedEffect(preloadUrls) {
        preloadUrls.forEach { ensureLoaded(it) }
    }

    if (!shaderMorphSupported) {
        fun scaledKeyFor(url: String) = "$url@${boxSize.width}x${boxSize.height}x$cropArtwork"
        LaunchedEffect(rawBitmaps.keys.toSet(), boxSize, cropArtwork) {
            if (boxSize.width <= 0 || boxSize.height <= 0) return@LaunchedEffect
            for ((url, bitmap) in rawBitmaps) {
                val key = scaledKeyFor(url)
                if (!scaledBitmaps.containsKey(key)) {
                    scaledBitmaps[key] = scaleForMesh(bitmap, boxSize.width, boxSize.height, cropArtwork)
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppTheme.colorScheme.surfaceVariant)
                .onSizeChanged { boxSize = it }
        ) {
            val outgoingBitmap = outgoingUrl?.let { scaledBitmaps[scaledKeyFor(it)] }
            val currentBitmap = currentUrl?.let { scaledBitmaps[scaledKeyFor(it)] }
            if (currentBitmap != null || outgoingBitmap != null) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxDisplacementPx = with(density) { RIPPLE_MAX_DISPLACEMENT.toPx() }
                    val warpAmount = kotlin.math.sin((morph.value * Math.PI).toFloat())
                    drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        outgoingBitmap?.let { bitmap ->
                            val verts = rippleMesh(size.width, size.height, warpAmount, maxDisplacementPx)
                            val paint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                alpha = ((1f - morph.value) * 255).toInt().coerceIn(0, 255)
                            }
                            nativeCanvas.drawBitmapMesh(bitmap, MESH_COLS, MESH_ROWS, verts, 0, null, 0, paint)
                        }
                        currentBitmap?.let { bitmap ->
                            val verts = rippleMesh(size.width, size.height, warpAmount, maxDisplacementPx)
                            val paint = android.graphics.Paint().apply {
                                isAntiAlias = true
                                alpha = if (outgoingBitmap != null) {
                                    (morph.value * 255).toInt().coerceIn(0, 255)
                                } else {
                                    255
                                }
                            }
                            nativeCanvas.drawBitmapMesh(bitmap, MESH_COLS, MESH_ROWS, verts, 0, null, 0, paint)
                        }
                    }
                }
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.surfaceVariant)
    ) {
        // The resting frame: a plain image respecting cropArtwork exactly like
        // before. The shader only ever draws on top of this during the brief
        // transition window below, so the steady state pays nothing extra.
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(currentUrl)
                .size(CoilSize.ORIGINAL)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .build(),
            onError = onError@{
                val fallback = nextThumbnailFallback(currentUrl) ?: return@onError
                currentUrl = fallback
            },
            contentDescription = null,
            contentScale = if (cropArtwork) ContentScale.Crop else ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        val outgoingBitmap = outgoingUrl?.let { rawBitmaps[it] }
        val currentBitmap = currentUrl?.let { rawBitmaps[it] }
        if (outgoingBitmap != null && currentBitmap != null) {
            // Lambdas, read in the draw phase: passing the values recomposed this whole
            // composable every frame of the melt -- up to the full length of a DJ crossfade.
            MeltMorphOverlay(
                outgoing = outgoingBitmap,
                incoming = currentBitmap,
                progress = { morph.value },
                timeSeconds = { timeSeconds },
            )
        }
    }
}

/**
 * The morph itself: one shader draw call blending [outgoing] into [incoming]
 * as [progress] runs 0→1. See [MELT_SHADER_SOURCE] for the actual displacement
 * and reveal math — this composable only binds the two bitmaps and uniforms.
 */
@androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.TIRAMISU)
@Composable
private fun MeltMorphOverlay(outgoing: Bitmap, incoming: Bitmap, progress: () -> Float, timeSeconds: () -> Float) {
    val shader = remember { android.graphics.RuntimeShader(MELT_SHADER_SOURCE) }
    val outgoingShader = remember(outgoing) {
        android.graphics.BitmapShader(outgoing, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP)
    }
    val incomingShader = remember(incoming) {
        android.graphics.BitmapShader(incoming, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP)
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        shader.setInputShader("tCurrent", outgoingShader)
        shader.setInputShader("tNext", incomingShader)
        shader.setFloatUniform("uResolution", size.width, size.height)
        shader.setFloatUniform("uCurrentSize", outgoing.width.toFloat(), outgoing.height.toFloat())
        shader.setFloatUniform("uNextSize", incoming.width.toFloat(), incoming.height.toFloat())
        shader.setFloatUniform("uProgress", progress())
        shader.setFloatUniform("uIntensity", MeltIntensity)
        shader.setFloatUniform("uScale", MeltScale)
        shader.setFloatUniform("uAberration", MeltAberration)
        shader.setFloatUniform("uTime", timeSeconds())
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply { this.shader = shader }
            canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint)
        }
    }
}

/** Displacement strength — how far the melt pushes pixels around. */
private const val MeltIntensity = 0.55f

/** Noise-field frequency — higher reads as finer, more turbulent melt cells. */
private const val MeltScale = 2.4f

/** RGB-split strength at the midpoint of the transition, fading to none at
 *  both ends. */
private const val MeltAberration = 0.35f

/**
 * AGSL port of React Bits' MorphSlider "melt" fragment shader
 * (github.com/DavidHDev/react-bits): a shared fbm noise field displaces both
 * the outgoing and incoming cover, and the SAME noise value gates a
 * per-pixel reveal threshold against [progress] — so different regions of the
 * frame cross over at different moments instead of a uniform fade, which is
 * what actually reads as one image melting into the next rather than two
 * images cross-faded on top of each other. Chromatic aberration rides
 * alongside, peaking at the transition's midpoint.
 */
private const val MELT_SHADER_SOURCE = """
uniform shader tCurrent;
uniform shader tNext;
uniform float2 uResolution;
uniform float2 uCurrentSize;
uniform float2 uNextSize;
uniform float uProgress;
uniform float uIntensity;
uniform float uScale;
uniform float uAberration;
uniform float uTime;

float hash21(float2 p) {
    float3 p3 = fract(p.xyx * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + float2(1.0, 0.0));
    float c = hash21(i + float2(0.0, 1.0));
    float d = hash21(i + float2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

float2 coverUV(float2 uv, float2 res, float2 img) {
    float rA = res.x / max(res.y, 1.0);
    float iA = img.x / max(img.y, 1.0);
    float2 s = float2(1.0, 1.0);
    float ratio = rA / max(iA, 0.0001);
    if (ratio > 1.0) {
        s.y = 1.0 / ratio;
    } else {
        s.x = ratio;
    }
    return (uv - 0.5) * s + 0.5;
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / uResolution;
    float p = clamp(uProgress, 0.0, 1.0);
    float env = sin(p * 3.14159265359);

    float nn = fbm(uv * uScale + uTime * 0.03);
    float warp = fbm(uv * uScale * 1.7 - uTime * 0.02);
    float2 g = float2(nn, warp) - 0.5;

    float2 uvC = uv + g * uIntensity * 0.5 * p;
    float2 uvN = uv - g * uIntensity * 0.5 * (1.0 - p);
    float m = smoothstep(nn - 0.15, nn + 0.15, p);

    float2 sC = coverUV(uvC, uResolution, uCurrentSize);
    float2 sN = coverUV(uvN, uResolution, uNextSize);

    float ca = uAberration * env * 0.03;

    float3 colC = float3(
        tCurrent.eval((sC + float2(ca, 0.0)) * uCurrentSize).r,
        tCurrent.eval(sC * uCurrentSize).g,
        tCurrent.eval((sC - float2(ca, 0.0)) * uCurrentSize).b
    );
    float3 colN = float3(
        tNext.eval((sN + float2(ca, 0.0)) * uNextSize).r,
        tNext.eval(sN * uNextSize).g,
        tNext.eval((sN - float2(ca, 0.0)) * uNextSize).b
    );

    float3 col = mix(colC, colN, m);
    return half4(half3(col), 1.0);
}
"""

/** Destination vertices for an (MESH_COLS+1) x (MESH_ROWS+1) grid, each pushed
 *  radially from center by a ring pattern scaled by [warpAmount] (0 = flat). */
private fun rippleMesh(widthPx: Float, heightPx: Float, warpAmount: Float, maxDisplacementPx: Float): FloatArray {
    val verts = FloatArray((MESH_COLS + 1) * (MESH_ROWS + 1) * 2)
    var index = 0
    for (row in 0..MESH_ROWS) {
        val v = row.toFloat() / MESH_ROWS
        for (col in 0..MESH_COLS) {
            val u = col.toFloat() / MESH_COLS
            val dx = u - 0.5f
            val dy = v - 0.5f
            val radius = kotlin.math.sqrt(dx * dx + dy * dy)
            val ripple = kotlin.math.sin((radius * RIPPLE_RINGS * 2 * Math.PI).toFloat()) * warpAmount
            val displacement = ripple * maxDisplacementPx
            val nx = if (radius < 0.0001f) 0f else dx / radius
            val ny = if (radius < 0.0001f) 0f else dy / radius
            verts[index++] = u * widthPx + nx * displacement
            verts[index++] = v * heightPx + ny * displacement
        }
    }
    return verts
}

/** Center-crops or pads [bitmap] to the exact size required by drawBitmapMesh. */
private fun scaleForMesh(bitmap: Bitmap, targetWidth: Int, targetHeight: Int, crop: Boolean): Bitmap {
    val srcW = bitmap.width
    val srcH = bitmap.height
    if (srcW <= 0 || srcH <= 0) return bitmap
    val scale = if (crop) {
        kotlin.math.max(targetWidth.toFloat() / srcW, targetHeight.toFloat() / srcH)
    } else {
        kotlin.math.min(targetWidth.toFloat() / srcW, targetHeight.toFloat() / srcH)
    }
    val scaledW = (srcW * scale).toInt().coerceAtLeast(1)
    val scaledH = (srcH * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true)
    val output = createBitmap(targetWidth, targetHeight)
    val canvas = android.graphics.Canvas(output)
    canvas.drawBitmap(scaled, (targetWidth - scaledW) / 2f, (targetHeight - scaledH) / 2f, null)
    if (scaled !== bitmap) scaled.recycle()
    return output
}

private suspend fun loadArtworkBitmap(context: android.content.Context, url: String): Bitmap? {
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()
    return when (val result = context.imageLoader.execute(request)) {
        is coil3.request.SuccessResult -> runCatching { result.image.toBitmap() }.getOrNull()
        else -> null
    }
}

/**
 * Seek effect overlay showing seek direction.
 */
@Composable
private fun SeekEffectOverlay(
    seekDirection: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = seekDirection,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    )
}

internal fun normalizeCanvasSongTitle(raw: String): String {
    val stripped =
        raw
            .replace(Regex("\\s*\\[[^]]*]"), "")
            .replace(
                Regex(
                    "\\s*\\((?:feat\\.?|ft\\.?|featuring|with)\\b[^)]*\\)",
                    RegexOption.IGNORE_CASE,
                ),
                "",
            )
            .replace(
                Regex(
                    "\\s*\\((?:official\\s*)?(?:music\\s*)?(?:video|mv|lyrics?|audio|visualizer|live|remaster(?:ed)?|version|edit|mix|remix)[^)]*\\)",
                    RegexOption.IGNORE_CASE,
                ),
                "",
            )
            .replace(
                Regex(
                    "\\s*-\\s*(?:official\\s*)?(?:music\\s*)?(?:video|mv|lyrics?|audio|visualizer|live|remaster(?:ed)?|version|edit|mix|remix)\\b.*$",
                    RegexOption.IGNORE_CASE,
                ),
                "",
            )
            .replace(Regex("\\s+"), " ")
            .trim()

    return stripped
        .trim('-')
        .replace(Regex("\\s+"), " ")
        .trim()
}

internal fun normalizeCanvasArtistName(raw: String): String {
    val first =
        raw
            .split(
                Regex(
                    "(?:\\s*,\\s*|\\s*&\\s*|\\s+×\\s+|\\s+x\\s+|\\bfeat\\.?\\b|\\bft\\.?\\b|\\bfeaturing\\b|\\bwith\\b)",
                    RegexOption.IGNORE_CASE,
                ),
                limit = 2,
                ).firstOrNull().orEmpty()

    return first.replace(Regex("\\s+"), " ").trim()
}

internal fun splitAndNormalizeArtists(raw: String): List<String> {
    return raw.split(
        Regex(
            "(?:\\s*,\\s*|\\s*&\\s*|\\s+×\\s+|\\s+x\\s+|\\bfeat\\.?\\b|\\bft\\.?\\b|\\bfeaturing\\b|\\bwith\\b)",
            RegexOption.IGNORE_CASE,
        )
    ).map { it.replace(Regex("\\s+"), " ").trim().lowercase(Locale.ROOT) }
        .filter { it.isNotBlank() }
}

/**
 * Next-lower YouTube thumbnail filename, or null once there is nothing left to try.
 *
 * i.ytimg.com serves a fixed ladder of filenames rather than arbitrary sizes, and which
 * rungs exist varies per video: maxresdefault is frequently absent, sddefault usually is
 * not. Walking down one rung per failure keeps the best available image instead of
 * collapsing to the smallest on the first miss.
 */
internal fun nextThumbnailFallback(url: String?): String? = when {
    url == null -> null
    url.contains("maxresdefault.jpg") -> url.replace("maxresdefault.jpg", "sddefault.jpg")
    url.contains("sddefault.jpg") -> url.replace("sddefault.jpg", "hqdefault.jpg")
    url.contains("hqdefault.jpg") -> url.replace("hqdefault.jpg", "mqdefault.jpg")
    // Either already at the bottom rung, or not an i.ytimg.com URL at all -- a
    // googleusercontent size parameter has no ladder to walk.
    else -> null
}
