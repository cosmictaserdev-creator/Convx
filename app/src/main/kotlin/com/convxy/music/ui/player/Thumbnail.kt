/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import com.convxy.music.ui.utils.rememberEdgeAwareFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.coroutines.flow.drop
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size as CoilSize
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.CropAlbumArtKey
import com.convxy.music.constants.HidePlayerThumbnailKey
import com.convxy.music.constants.PlayerBackgroundStyle
import com.convxy.music.constants.PlayerBackgroundStyleKey
import com.convxy.music.constants.PlayerHorizontalPadding
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convxy.music.constants.PlayerArtworkStyle
import com.convxy.music.constants.PlayerArtworkStyleKey
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import com.convxy.music.constants.SeekExtraSeconds
import com.convxy.music.constants.SwipeThumbnailKey
import com.convxy.music.constants.ThumbnailCornerRadiusKey
import com.convxy.music.constants.WatchVideoKey
import com.convxy.music.constants.ThumbnailCornerRadius
import com.convxy.music.listentogether.RoomRole
import com.convxy.music.ui.component.CastButton
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.constants.CanvasSource
import com.convxy.music.constants.CanvasSourceKey
import com.convxy.music.constants.CanvasThumbnailAnimationKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.canvas.TidalCanvasProvider
import com.convxy.music.canvas.CanvasArtwork
import com.convxy.music.extensions.metadata
import com.convxy.music.ui.utils.resize
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import com.convxy.music.applecanvas.AppleMusicCanvasProvider
import com.convxy.music.vivimusiccanvas.ViviMusicCanvasProvider
import com.convxy.music.vivimusiccanvas.EchoMusicCanvasProvider
import java.util.Locale

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
 * This function is marked as @Stable to indicate it produces stable results.
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

/**
 * Get media items for the thumbnail carousel.
 * Calculates previous, current, and next items based on shuffle mode.
 */
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

/**
 * Get text color based on player background style.
 * Computed once per background style change.
 */
@Stable
@Composable
private fun getTextColor(playerBackground: PlayerBackgroundStyle): Color {
    return when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
        PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT, PlayerBackgroundStyle.GLOW_ANIMATED, PlayerBackgroundStyle.APPLE_MUSIC, PlayerBackgroundStyle.LIVE_MESH, PlayerBackgroundStyle.STATIC, PlayerBackgroundStyle.CUSTOM_GRADIENT -> Color.White
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

    // Collect states
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val error by playerConnection.error.collectAsState()
    val queueTitle by playerConnection.queueTitle.collectAsState()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    // Preferences - computed once
    // Disable swipe for Listen Together guests
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    val swipeThumbnail = swipeThumbnailPref && !isListenTogetherGuest
    val hidePlayerThumbnail by rememberPreference(HidePlayerThumbnailKey, false)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)
    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.APPLE_MUSIC
    )
    val thumbnailCornerRadius by rememberPreference(ThumbnailCornerRadiusKey, defaultValue = 3f)
    val watchVideo by rememberPreference(WatchVideoKey, false)
    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)
    // Full YouTube video mode: on when the toggle is set AND the stream the
    // resolver picked actually carries video (muxed format in the DB).
    val videoPlaybackActive = watchVideo && currentFormat?.mimeType?.startsWith("video") == true
    
    // Pre-calculate text color based on background style
    val textBackgroundColor = getTextColor(playerBackground)
    
    // Grid state
    val thumbnailLazyGridState = rememberLazyGridState()
    
    // Calculate media items data - memoized
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
        // drop(1): this effect restarts every time currentMediaIndex changes,
        // and snapshotFlow replays the CURRENT isScrollInProgress value
        // immediately on collection start — which is stale grid state from
        // before the index changed (the resync scroll below hasn't started
        // yet). Comparing that stale position against the new index misread
        // as a user swipe and fired a spurious seek, which changed the index
        // again, restarting this effect again — an infinite ping-pong at
        // queue boundaries (index 0/1) where the carousel window is 2 items
        // instead of 3. Only react to an actual transition, not the replay.
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

    // Update position when song changes
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
        if (videoPlaybackActive) {
            // Full-video mode replaces the artwork carousel with the song's
            // video, keeping the player's layout and gestures untouched.
            VideoPlaybackSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(thumbnailCornerRadius.dp)),
            )
            return@Box
        }
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

        // Main thumbnail view
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
                    // Calculate dimensions once per size change, considering landscape mode
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
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            } else {
                Text(
                    text = stringResource(R.string.now_playing),
                    style = MaterialTheme.typography.titleMedium,
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
                        style = MaterialTheme.typography.titleMedium,
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
    playerConnection: com.convxy.music.playback.PlayerConnection,
    context: android.content.Context,
    isLandscape: Boolean = false,
    isListenTogetherGuest: Boolean = false,
    currentMediaId: String? = null,
    currentMediaThumbnail: String? = null,
    playerBackground: PlayerBackgroundStyle = PlayerBackgroundStyle.DEFAULT,
    modifier: Modifier = Modifier,
) {
    val (artworkStyle) = rememberEnumPreference(PlayerArtworkStyleKey, defaultValue = PlayerArtworkStyle.CARD)
    val rotatingThumbnail = artworkStyle != PlayerArtworkStyle.CARD
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val isCurrentItem = item.mediaId == currentMediaId

    // An infiniteRepeatable never finishes, so pinning targetValue to 0f did not
    // stop this — the transition kept awaiting a frame forever and redrew the
    // whole app every vsync whenever the player was on screen. Only create it
    // while the artwork is actually spinning.
    val rotation: State<Float> = if (isPlaying && rotatingThumbnail && isCurrentItem) {
        val infiniteTransition = rememberInfiniteTransition(label = "ThumbnailRotation")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Restart
            ),
            label = "Rotation"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

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
                .size(dimensions.thumbnailSize)
                // Target end of the mini-to-full cover morph, registered on the node that
                // KNOWS the shape: a vinyl or clover is a circle for morph purposes (half
                // its size), a card is its own corner radius. Also the copy that has to be
                // hidden while the travelling one is in flight, or the cover is on screen
                // twice from the handoff onward.
                .registerFullArtworkRect(
                    with(artworkDensity) {
                        when (artworkStyle) {
                            PlayerArtworkStyle.CARD -> dimensions.cornerRadius.toPx()
                            else -> dimensions.thumbnailSize.toPx() / 2f
                        }
                    }
                )
                .hideWhileMorphing()
                .graphicsLayer {
                    rotationZ = rotation.value
                }
                .clip(
                    when (artworkStyle) {
                        PlayerArtworkStyle.VINYL -> CircleShape
                        PlayerArtworkStyle.CLOVER -> MaterialShapes.Clover8Leaf.toShape()
                        // Continuous (squircle) corners, matching the rest of the chrome.
                        PlayerArtworkStyle.CARD -> ContinuousRoundedRectangle(dimensions.cornerRadius)
                    }
                )
                // A vinyl spins with its label, so only the clover shape counter-rotates.
                .graphicsLayer {
                    rotationZ = if (artworkStyle == PlayerArtworkStyle.VINYL) 0f else -rotation.value
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

                ThumbnailImage(
                    artworkUri = artworkUriToUse?.resize(1200, 1200),
                    cropArtwork = cropAlbumArt
                )
            }
            
            if (artworkStyle == PlayerArtworkStyle.VINYL) {
                Canvas(Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    val c = Offset(size.width / 2f, size.height / 2f)
                    // Black record body drawn as a ring, so the art stays visible
                    // in the middle as the vinyl's paper label.
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.82f),
                        radius = r * 0.67f,
                        center = c,
                        style = Stroke(width = r * 0.66f),
                    )
                    for (i in 1..7) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = r * (0.40f + i * 0.083f),
                            center = c,
                            style = Stroke(width = 1.dp.toPx()),
                        )
                    }
                    drawCircle(Color.Black, radius = r * 0.055f, center = c)
                }
            }

            if (canvasThumbnailAnimation && item.mediaId == currentMediaId && !rotatingThumbnail && playerBackground != PlayerBackgroundStyle.APPLE_MUSIC) {
                val (canvasSource) = rememberEnumPreference(CanvasSourceKey, defaultValue = CanvasSource.AUTO)
                var canvasArtwork by remember(item.mediaId) { mutableStateOf<CanvasArtwork?>(null) }
                var canvasFetchInFlight by remember(item.mediaId) { mutableStateOf(false) }
                val storefront = remember {
                    val country = Locale.getDefault().country
                    if (country.length == 2) country.lowercase(Locale.ROOT) else "us"
                }

                LaunchedEffect(item.mediaId, canvasSource) {
                    val cacheKey = "${item.mediaId}:${canvasSource.name}"
                    CanvasArtworkPlaybackCache.get(cacheKey)?.let { cached ->
                        canvasArtwork = cached
                        return@LaunchedEffect
                    }

                    if (canvasFetchInFlight) return@LaunchedEffect
                    canvasFetchInFlight = true

                    val fetched = withContext(Dispatchers.IO) {
                        val metadata = item.metadata
                        val albumName = (metadata?.album?.title ?: item.mediaMetadata.albumTitle)?.toString()
                        val duration = metadata?.duration
                        
                        val songTitleRaw = item.mediaMetadata.title?.toString() ?: ""
                        val artistNameRaw = item.mediaMetadata.artist?.toString() ?: ""
                        
                        val songTitle = normalizeCanvasSongTitle(songTitleRaw)
                        val artistName = normalizeCanvasArtistName(artistNameRaw)
                        
                        println("CanvasFetch: Song='$songTitle' (raw='$songTitleRaw'), Artist='$artistName' (raw='$artistNameRaw'), Album='$albumName'")
                        
                        val searchTasks = listOf(
                            songTitleRaw to artistNameRaw
                        ).filter { (s, a) -> s.isNotBlank() && a.isNotBlank() }

                        when (canvasSource) {
                            CanvasSource.AUTO -> {
                                searchTasks.firstNotNullOfOrNull { (s, a) ->
                                    EchoMusicCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                        ?: AppleMusicCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a,
                                        album = albumName,
                                        storefront = storefront
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                        ?: ViviMusicCanvasProvider.getBySongArtist(
                                            song = s,
                                            artist = a
                                        )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                        ?: TidalCanvasProvider.getBySongArtist(
                                            song = s,
                                            artist = a,
                                            album = albumName
                                        )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                }
                            }
                            CanvasSource.ECHO_MUSIC -> {
                                searchTasks.firstNotNullOfOrNull { (s, a) ->
                                    EchoMusicCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                }
                            }
                            CanvasSource.APPLE_MUSIC -> {
                                searchTasks.firstNotNullOfOrNull { (s, a) ->
                                    AppleMusicCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a,
                                        album = albumName,
                                        storefront = storefront
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                }
                            }
                            CanvasSource.VIVIMUSIC -> {
                                searchTasks.firstNotNullOfOrNull { (s, a) ->
                                    ViviMusicCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                }
                            }
                            CanvasSource.TIDAL -> {
                                searchTasks.firstNotNullOfOrNull { (s, a) ->
                                    TidalCanvasProvider.getBySongArtist(
                                        song = s,
                                        artist = a,
                                        album = albumName
                                    )?.takeIf { !it.preferredAnimationUrl.isNullOrBlank() }
                                }
                            }
                        }
                    }
                    
                    // Client-side safety check: ensure the fetched canvas matches the requested song
                    // This prevents "wrong canvas" if a provider returned a generic or incorrect match
                    val requestedArtist = item.mediaMetadata.artist?.toString() ?: ""
                    val requestedTitle = item.mediaMetadata.title?.toString() ?: ""
                    
                    val validated = fetched?.let { artwork ->
                        val resultArtist = artwork.artist
                        val resultName = artwork.name
                        
                        // Check artist list overlaps
                        val artistMatches = if (resultArtist != null && requestedArtist.isNotBlank()) {
                            val requestedList = splitAndNormalizeArtists(requestedArtist)
                            val resultList = splitAndNormalizeArtists(resultArtist)
                            requestedList.any { req -> resultList.any { res -> res.contains(req) || req.contains(res) } }
                        } else true

                        // Check song title (raw comparison)
                        val songMatches = if (resultName != null && requestedTitle.isNotBlank()) {
                            resultName.trim().equals(requestedTitle.trim(), ignoreCase = true)
                        } else true
                        // Check album name (raw comparison)
                        val requestedAlbum = item.mediaMetadata.albumTitle?.toString() ?: ""
                        val canvasAlbumName = artwork.albumName

                        val albumMatches = if (canvasAlbumName != null && requestedAlbum.isNotBlank()) {
                            canvasAlbumName.trim().equals(requestedAlbum.trim(), ignoreCase = true)
                        } else false

                        val titleMatches = songMatches && albumMatches

                        println("CanvasValidation: artistMatches=$artistMatches, songMatches=$songMatches, albumMatches=$albumMatches")
                        println("  Requested: Title='$requestedTitle', Album='$requestedAlbum', Artists='$requestedArtist'")
                        println("  Returned: Title='$resultName', Album='$canvasAlbumName', Artists='$resultArtist'")

                        if (artistMatches && titleMatches) {
                            println("CanvasValidation: Match SUCCESS for '${artwork.name}'")
                            artwork
                        } else {
                            println("CanvasValidation: Match FAILED for '${artwork.name}' by '${artwork.artist}'")
                            null
                        }
                    }
                    
                    canvasArtwork = validated
                    if (validated != null) {
                        CanvasArtworkPlaybackCache.put("${item.mediaId}:${canvasSource.name}", validated)
                    }
                    canvasFetchInFlight = false
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

        // Non-rotating light overlay — stays fixed while the vinyl spins underneath
        if (artworkStyle == PlayerArtworkStyle.VINYL && !hidePlayerThumbnail) {
            Canvas(
                Modifier
                    .size(dimensions.thumbnailSize)
                    .clip(CircleShape)
            ) {
                val r = size.minDimension / 2f
                val c = Offset(size.width / 2f, size.height / 2f)

                // Edge rim light
                drawCircle(
                    color = Color.White.copy(alpha = 0.18f),
                    radius = r * 0.98f,
                    center = c,
                    style = Stroke(width = 1.2.dp.toPx()),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = r * 0.95f,
                    center = c,
                    style = Stroke(width = 0.6.dp.toPx()),
                )

                // Diagonal gloss shine — stays in place as if lit from above-right
                drawOval(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.09f),
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.09f),
                            Color.Transparent,
                        ),
                        start = Offset(size.width * 0.15f, 0f),
                        end = Offset(size.width * 0.85f, size.height),
                    ),
                    topLeft = Offset(size.width * 0.08f, size.height * 0.22f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.84f, size.height * 0.32f),
                )
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
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.convxy_logo),
            contentDescription = stringResource(R.string.hide_player_thumbnail),
            tint = textBackgroundColor.copy(alpha = 0.7f),
            modifier = Modifier.size(120.dp)
        )
    }
}

/**
 * Actual thumbnail image with caching and hardware layer rendering.
 */
@Composable
private fun ThumbnailImage(
    artworkUri: String?,
    cropArtwork: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        var currentUrl by remember(artworkUri) {
            mutableStateOf(artworkUri)
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(currentUrl)
                .size(CoilSize.ORIGINAL)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .build(),
            // Step DOWN the ladder, one rung at a time. This used to jump straight from
            // maxresdefault to hqdefault, and since maxresdefault (1280x720) simply does
            // not exist for most YouTube tracks, the common case was a 404 followed by a
            // 480x360 image blown up ~2.5x on a ~1200px player artwork -- which is the
            // soft, mushy cover people notice. sddefault (640x480) exists far more often
            // and is the rung that was being skipped.
            onError = { currentUrl = nextThumbnailFallback(currentUrl) },
            contentDescription = null,
            contentScale = if (cropArtwork) ContentScale.Crop else ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
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
