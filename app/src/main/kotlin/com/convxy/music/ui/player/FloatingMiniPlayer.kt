/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.height
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.convxy.music.LocalListenTogetherManager
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.ui.player.customize.PlayerGlyph
import com.convxy.music.ui.player.customize.PlayerIconSlot
import com.convxy.music.constants.MiniBarTabStyleKey
import com.convxy.music.constants.MiniPlayerWaveformKey
import com.convxy.music.constants.SwipeSensitivityKey
import com.convxy.music.constants.SwipeThumbnailKey
import androidx.media3.common.Player
import com.convxy.music.extensions.togglePlayPause
import com.convxy.music.extensions.toggleRepeatMode
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.component.ScrollingWaveformSeekBar
import com.convxy.music.ui.component.rememberPlaybackFraction
import com.convxy.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convxy.music.utils.rememberPreference
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * Optical size correction for the lyrics glyph beside the queue one.
 *
 * `lyrics` is drawn in a 28dp viewport with real padding around the artwork;
 * `queue_music` is drawn in a 24dp viewport with its strokes running nearly edge
 * to edge. Rendered at the same box size the lyrics mark therefore reads visibly
 * smaller, so it gets a slightly larger box to match by eye rather than by number.
 */
private const val LyricsIconScale = 1.18f

/**
 * Compact now playing controls docked inside the floating tab bar, mirroring the
 * iOS 26 Apple Music accessory: a wide pill above the tabs when the bar is
 * expanded, and a slim strip between the tab pill and the search tab when inline.
 *
 * Swiping the artwork/title area horizontally changes the song, honoring the same
 * swipe preferences and thresholds as [MiniPlayer]. Tapping the row invokes
 * [onClick] (which opens the full player).
 */
@Composable
fun FloatingMiniPlayer(
    isInline: Boolean,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLyricsClick: (() -> Unit)? = null,
    onQueueClick: (() -> Unit)? = null,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()

    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    val (tabStyle) = rememberPreference(MiniBarTabStyleKey, defaultValue = false)
    val miniPlayerWaveform by rememberPreference(MiniPlayerWaveformKey, defaultValue = true)
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isInRoom && !it.isHost } ?: false
    val swipeEnabled = swipeThumbnailPref && !isListenTogetherGuest

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    // Swipe thresholds below were tuned as raw pixel counts, which is only
    // ~14dp on a dense (~3.5x) screen — well within a tap's incidental finger
    // drift, so plain taps were misread as deliberate swipes and skipped the
    // track. Scaling by density.density makes them density-independent.
    val densityScale = density.density

    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }
    val animationSpec = remember {
        spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    }
    // Commit distance for the swipe. The logistic curve this replaces produced
    // ~400dp — about 1200px at 3x, on a screen 1080px wide — so releasing the pill
    // never changed track no matter how far it was dragged. Expressed as a fraction
    // of screen width instead: sensitivity 0 needs 45%, sensitivity 1 needs 12%.
    val configuration = LocalConfiguration.current
    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, densityScale) {
        val screenWidthPx = configuration.screenWidthDp * densityScale
        (screenWidthPx * (0.45f - 0.33f * swipeSensitivity.coerceIn(0f, 1f))).roundToInt()
    }

    // iOS 26 style press response: the whole glass pill grows slightly while touched.
    val pressInteractionSource = remember { MutableInteractionSource() }
    val isPressed by pressInteractionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 1.04f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "accessoryPressScale",
    )

    // Same finger-tracking glow as the nav bar's drag puck (ported from Kyant0's
    // catalog). Its gesture tracking never consumes pointer events, so it's safe
    // stacked alongside the swipe-to-skip drag detector and the click below.
    val interactiveHighlight = remember(coroutineScope) { InteractiveHighlight(animationScope = coroutineScope) }

    // Same structure as MiniPlayer: the drag detector sits on the outermost
    // container so the whole accessory is swipeable, and the entire content row
    // slides with the drag. A plain Box (not BoxWithConstraints) — this sits
    // inside FloatingTabBar's Row, which sizes itself via
    // .height(IntrinsicSize.Max); BoxWithConstraints is a SubcomposeLayout,
    // and SubcomposeLayout can't answer intrinsic-measurement queries, which
    // crashes the whole bar. onSizeChanged gets the same measured height for
    // the !isInline tablet pill's icon/thumbnail scaling without that problem.
    var measuredHeightPx by remember { mutableIntStateOf(0) }
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .then(modifier)
            // Source end of the mini-to-full container morph. Outside .then(modifier)
            // so it measures the pill as the nav bar actually places it, and after
            // the press-scale layer so a half-pressed pill still reports its real
            // resting bounds rather than the squashed ones.
            .registerMiniContainerRect()
            // Source end of the shared container morph. Recorded here and drawn by the
            // overlay instead of in place, so the pill does not sit in the nav bar while
            // a copy of it is expanding across the screen.
            .recordPlayerLayer(
                layer = { PlayerMorph.miniLayer },
                drawInPlace = { !PlayerMorph.active },
            )
            .onSizeChanged { measuredHeightPx = it.height }
            .then(interactiveHighlight.modifier)
            .clipToBounds()
            .then(interactiveHighlight.gestureModifier)
            .then(
                if (swipeEnabled) {
                    Modifier.pointerInput(Unit) {
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
                                    totalDragDistance += abs(adjustedDragAmount)
                                    coroutineScope.launch {
                                        offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount)
                                    }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                // Mean velocity over the gesture, px/ms. The old gate wanted
                                // ~7.4px/ms against a mean of 1-3 for a real swipe, so that
                                // branch never fired either.
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value
                                val dragged = abs(currentOffset)
                                val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNext = playerConnection.player.nextMediaItemIndex != -1

                                // Drag past the commit distance, or flick: a short fast throw
                                // should not have to cross the whole distance.
                                val shouldChangeSong = dragged > autoSwipeThreshold ||
                                    (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f)

                                if (shouldChangeSong) {
                                    // Through the connection, not the raw player: the wrapper
                                    // routes to Cast while casting, re-prepares an ENDED
                                    // player, and fires the skip callbacks.
                                    if (currentOffset > 0 && canSkipPrevious) {
                                        playerConnection.seekToPrevious()
                                    } else if (currentOffset <= 0 && canSkipNext) {
                                        playerConnection.seekToNext()
                                    }
                                }
                                coroutineScope.launch {
                                    offsetXAnimatable.animateTo(0f, animationSpec)
                                }
                            },
                        )
                    }
                } else {
                    Modifier
                }
            ),
    ) {
        // Fixed sizes for the phone accessory strip (isInline) — it's a slim,
        // constant-height dock, not meant to scale. The tablet pill's own
        // height varies (FloatingMiniPlayerMinHeight..MaxHeight in
        // FloatingSideBar), so its icons/thumbnail scale with it instead of
        // looking tiny in a tall pill or cramped in a short one.
        val measuredHeight = with(density) { measuredHeightPx.toDp() }
        val artSize = if (isInline) 36.dp else (measuredHeight * 0.6f).coerceIn(36.dp, 60.dp)
        val artCornerRadius = if (isInline) 9.dp else artSize * 0.22f
        val controlSize = if (isInline) 36.dp else (measuredHeight * 0.55f).coerceIn(36.dp, 52.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }
                .clickable(
                    interactionSource = pressInteractionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(
                    horizontal = if (isInline) 10.dp else 12.dp,
                    vertical = if (isInline) 6.dp else 8.dp,
                ),
        ) {
            if (isInline) {
                AsyncImage(
                    model = mediaMetadata?.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.thumbnail_fallback),
                    error = painterResource(R.drawable.thumbnail_fallback),
                    modifier = Modifier
                        .size(artSize)
                        // Registers this box's on-screen bounds for
                        // PlayerArtworkMorphOverlay -- this is the real, visible mini
                        // player (docked in the floating nav bar); MiniPlayer.kt's own
                        // collapsedContent renders at collapsedBound = 0.dp now and is
                        // never actually on screen.
                        .registerMiniArtworkRect(with(density) { artCornerRadius.toPx() })
                        // The cover is flown by PlayerArtworkMorphOverlay for the whole
                        // flight; without this the copy baked into the pill's recording
                        // rides along underneath it.
                        .hideWhileMorphing()
                        .clip(RoundedCornerShape(artCornerRadius)),
                )

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaMetadata?.title.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = contentColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.size(controlSize),
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlaying,
                        tint = contentColor,
                        size = 20.dp,
                    )
                }
            } else if (!tabStyle) {
                // iOS-style mini bar (default): thumbnail, title/artist, a wave
                // seek bar, play/pause, and forward (skip next).
                val iconSize = controlSize * 0.5f
                val playIconSize = controlSize * 0.6f
                // Keep the State, don't unwrap it: rememberPlaybackFraction samples
                // withFrameMillis, so a `by` delegate read here recomposes the whole
                // mini player every frame while playing. Only the seek bar's progress
                // lambda below needs the value, and that reads it in the draw phase.
                val playbackFraction = rememberPlaybackFraction(playerConnection.player, isPlaying)
                val waveformSeed = remember(mediaMetadata?.id) { mediaMetadata?.id?.hashCode() ?: 0 }

                AsyncImage(
                    model = mediaMetadata?.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.thumbnail_fallback),
                    error = painterResource(R.drawable.thumbnail_fallback),
                    modifier = Modifier
                        .size(artSize)
                        // Registers this box's on-screen bounds for
                        // PlayerArtworkMorphOverlay -- this is the real, visible mini
                        // player (docked in the floating nav bar); MiniPlayer.kt's own
                        // collapsedContent renders at collapsedBound = 0.dp now and is
                        // never actually on screen.
                        .registerMiniArtworkRect(with(density) { artCornerRadius.toPx() })
                        // The cover is flown by PlayerArtworkMorphOverlay for the whole
                        // flight; without this the copy baked into the pill's recording
                        // rides along underneath it.
                        .hideWhileMorphing()
                        .clip(RoundedCornerShape(artCornerRadius)),
                )

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaMetadata?.title.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (miniPlayerWaveform) {
                    Spacer(Modifier.width(8.dp))
                    ScrollingWaveformSeekBar(
                        progress = { playbackFraction.value },
                        onSeek = { frac ->
                            val duration = playerConnection.player.duration
                            if (duration > 0) {
                                playerConnection.player.seekTo((frac * duration).toLong())
                            }
                        },
                        playedColor = contentColor,
                        trackColor = contentColor.copy(alpha = 0.3f),
                        seed = waveformSeed,
                        visibleBars = 14,
                        modifier = Modifier
                            .width(64.dp)
                            .height(22.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                IconButton(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.size(controlSize),
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlaying,
                        tint = contentColor,
                        size = playIconSize,
                    )
                }
                IconButton(
                    onClick = { playerConnection.seekToNext() },
                    enabled = canSkipNext,
                    modifier = Modifier.size(controlSize),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.NEXT,
                        fallback = R.drawable.fast_forward,
                        tint = if (canSkipNext) contentColor else contentColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(playIconSize),
                    )
                }
            } else {
                // Tablet floating pill: matches the Apple Music iPad now-playing
                // bar exactly — shuffle, prev, play/pause, next, repeat, then
                // artwork+title/artist, then lyrics and queue on the far end.
                // Icons scale with controlSize — IconButton's own size modifier
                // only resizes the tap target, the Icon inside stays at a fixed
                // default unless given an explicit size too.
                val iconSize = controlSize * 0.5f
                val playIconSize = controlSize * 0.6f
                IconButton(
                    onClick = { playerConnection.player.shuffleModeEnabled = !shuffleModeEnabled },
                    modifier = Modifier.size(controlSize),
                ) {
                    Icon(
                        painter = painterResource(if (shuffleModeEnabled) R.drawable.shuffle_on else R.drawable.shuffle),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(iconSize),
                    )
                }
                IconButton(
                    onClick = { playerConnection.seekToPrevious() },
                    enabled = canSkipPrevious,
                    modifier = Modifier.size(controlSize),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.PREVIOUS,
                        fallback = R.drawable.skip_previous_legacy,
                        tint = if (canSkipPrevious) contentColor else contentColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(iconSize),
                    )
                }
                IconButton(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.size(controlSize),
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlaying,
                        tint = contentColor,
                        size = playIconSize,
                    )
                }
                IconButton(
                    onClick = { playerConnection.seekToNext() },
                    enabled = canSkipNext,
                    modifier = Modifier.size(controlSize),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.NEXT,
                        fallback = R.drawable.fast_forward,
                        tint = if (canSkipNext) contentColor else contentColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(iconSize),
                    )
                }
                IconButton(
                    onClick = { playerConnection.player.toggleRepeatMode() },
                    modifier = Modifier.size(controlSize),
                ) {
                    Icon(
                        painter = painterResource(
                            if (repeatMode == Player.REPEAT_MODE_ONE) R.drawable.repeat_one else R.drawable.repeat
                        ),
                        contentDescription = null,
                        tint = if (repeatMode != Player.REPEAT_MODE_OFF) contentColor else contentColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(iconSize),
                    )
                }

                Spacer(Modifier.width(8.dp))

                AsyncImage(
                    model = mediaMetadata?.thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.thumbnail_fallback),
                    error = painterResource(R.drawable.thumbnail_fallback),
                    modifier = Modifier
                        .size(artSize)
                        // Registers this box's on-screen bounds for
                        // PlayerArtworkMorphOverlay -- this is the real, visible mini
                        // player (docked in the floating nav bar); MiniPlayer.kt's own
                        // collapsedContent renders at collapsedBound = 0.dp now and is
                        // never actually on screen.
                        .registerMiniArtworkRect(with(density) { artCornerRadius.toPx() })
                        // The cover is flown by PlayerArtworkMorphOverlay for the whole
                        // flight; without this the copy baked into the pill's recording
                        // rides along underneath it.
                        .hideWhileMorphing()
                        .clip(RoundedCornerShape(artCornerRadius)),
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mediaMetadata?.title.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onLyricsClick ?: onClick,
                    modifier = Modifier.size(controlSize),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lyrics),
                        contentDescription = stringResource(R.string.lyrics),
                        tint = contentColor,
                        modifier = Modifier.size(iconSize * LyricsIconScale),
                    )
                }
                IconButton(
                    onClick = onQueueClick ?: onClick,
                    modifier = Modifier.size(controlSize),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue_music),
                        contentDescription = stringResource(R.string.queue),
                        tint = contentColor,
                        modifier = Modifier.size(iconSize),
                    )
                }
            }
        }
    }
}
