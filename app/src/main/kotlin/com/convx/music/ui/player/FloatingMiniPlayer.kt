/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.player

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.IconButton
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
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
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.convx.music.LocalAmbientBackgroundColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFBackwardFill
import com.composables.sfsymbols.monochrome.SFForwardFill
import com.composables.sfsymbols.monochrome.SFListBullet
import com.composables.sfsymbols.monochrome.SFQuoteBubble
import com.composables.sfsymbols.monochrome.SFRepeat
import com.composables.sfsymbols.monochrome.SFRepeat1
import com.composables.sfsymbols.monochrome.SFShuffle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size as CoilSize
import androidx.compose.ui.platform.LocalContext
import com.convx.music.ui.component.rememberNoArtworkPainter
import com.convx.music.LocalListenTogetherManager
import com.convx.music.LocalPlayerConnection
import com.convx.music.R
import com.convx.music.ui.player.customize.PlayerGlyph
import com.convx.music.ui.player.customize.PlayerIconSlot
import com.convx.music.constants.MiniBarTabStyleKey
import com.convx.music.constants.MiniPlayerWaveformKey
import com.convx.music.constants.SwipeSensitivityKey
import com.convx.music.constants.SwipeThumbnailKey
import androidx.media3.common.Player
import com.convx.music.extensions.togglePlayPause
import com.convx.music.extensions.toggleRepeatMode
import com.convx.music.ui.component.AnimatedPlayPauseIcon
import com.convx.music.ui.component.ScrollingWaveformSeekBar
import com.convx.music.ui.component.floatingtabbar.SearchBarRowHeight
import com.convx.music.ui.component.floatingtabbar.clipWhilePressed
import com.convx.music.ui.component.rememberPlaybackFraction
import androidx.compose.ui.graphics.isSpecified
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.ui.component.asSoftIconTint
import com.convx.music.ui.component.elasticStretchLayerBlock
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.utils.flamingoChrome
import com.convx.music.ui.utils.resize
import com.convx.music.utils.rememberPreference
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** Slightly enlarges the lyrics glyph to match the queue icon visually. */
private const val LyricsIconScale = 1.18f

/** Mini-player controls shown in the floating bar or side panel. */
// Width range where the waveform and skip button fade out.
private const val MiniExtrasFullWidth = 320f
private const val MiniExtrasGoneWidth = 262f

/** Scales [width] by [fraction], and optionally applies the same fraction to alpha. */
private fun Modifier.scaledWidth(width: Dp, fade: Boolean = false, fraction: () -> Float): Modifier = layout { measurable, constraints ->
    val f = fraction()
    val w = (width.roundToPx() * f).roundToInt()
    val placeable = measurable.measure(constraints.copy(minWidth = w, maxWidth = w))
    layout(w, placeable.height) {
        if (fade) placeable.placeWithLayer(0, 0) { alpha = f } else placeable.place(0, 0)
    }
}

/** Soft edge used by the title marquee. */
private val MarqueeFade = 10.dp

/** Adds a soft fade to both edges of the scrolling title. */
private fun Modifier.marqueeEdgeFade(fade: Dp): Modifier = this
    .layout { measurable, constraints ->
        val f = fade.roundToPx()
        val placeable = measurable.measure(
            constraints.copy(
                minWidth = constraints.minWidth + f,
                maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth + f else constraints.maxWidth,
            ),
        )
        layout(placeable.width - f, placeable.height) { placeable.place(-f, 0) }
    }
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val f = (fade.toPx() / size.width).coerceIn(0f, 0.5f)
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                f to Color.Black,
                1f - f to Color.Black,
                1f to Color.Transparent,
            ),
            blendMode = BlendMode.DstIn,
        )
    }

@Composable
fun FloatingMiniPlayer(
    isInline: Boolean,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Gates additiveChrome on every icon here the same way the nav bar's own tab
    // icons are gated -- additive only reads right over glass, not a flat surface.
    useGlass: Boolean = true,
    onLyricsClick: (() -> Unit)? = null,
    onQueueClick: (() -> Unit)? = null,
    // Long-press anywhere on the bar opens the current song's menu, same as long-pressing
    // a row elsewhere in the app (reference: menu pop up.jpg's quick-actions sheet).
    onLongClick: (() -> Unit)? = null,
    // Fires on release after a real drag (never a tap) so the host bar can
    // answer the snap with its own motion.
    onSnapRelease: () -> Unit = {},
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val ambientColor = LocalAmbientBackgroundColor.current.value
    val isDark = com.convx.music.ui.component.glassUsesDarkAppearance()
    val playTint = com.convx.music.ui.theme.GlassTintTokens.glassContentColor(
        isDark = isDark,
        activeTint = ambientColor,
        isSelected = false,
    ).copy(alpha = 1.0f)
    val transportTint = com.convx.music.ui.theme.GlassTintTokens.glassContentColor(
        isDark = isDark,
        activeTint = ambientColor,
        isSelected = false,
    )
    val context = LocalContext.current
    val mediaMetadata by playerConnection.mediaMetadata.collectAsStateWithLifecycle()
    val isPlaying by playerConnection.isPlaying.collectAsStateWithLifecycle()
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()

    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnailPref by rememberPreference(SwipeThumbnailKey, true)
    val (tabStyle) = rememberPreference(MiniBarTabStyleKey, defaultValue = false)
    val miniPlayerWaveform by rememberPreference(MiniPlayerWaveformKey, defaultValue = false)
    val listenTogetherManager = LocalListenTogetherManager.current
    val isListenTogetherGuest = listenTogetherManager?.let { it.isGuest } ?: false
    val swipeEnabled = swipeThumbnailPref && !isListenTogetherGuest

    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    // Scale pixel thresholds by density so the swipe feels consistent across screens.
    val densityScale = density.density

    val offsetXAnimatable = remember { Animatable(0f) }
    // Stretch visual comes from the shared search-pill elastic formula
    // (elasticStretchLayerBlock + glow); skip slide stays on offsetX below.
    // Center drag stretches only, edge flick skips -- see pointerInput.
    val glowScope = rememberCoroutineScope()
    val glow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }
    val viewConfig = LocalViewConfiguration.current
    // Natural iOS rubber-band return: settles with subtle micro-bounce past center.
    val animationSpec = remember {
        spring<Float>(dampingRatio = 0.73f, stiffness = 620f)
    }
    // Express the commit distance as a screen-width fraction so sensitivity scales predictably.
    val configuration = LocalConfiguration.current
    val autoSwipeThreshold = remember(swipeSensitivity, configuration.screenWidthDp, densityScale) {
        val screenWidthPx = configuration.screenWidthDp * densityScale
        (screenWidthPx * (0.45f - 0.33f * swipeSensitivity.coerceIn(0f, 1f))).roundToInt()
    }

    // Use Box because the parent relies on intrinsic sizing; BoxWithConstraints uses
    // subcomposition and cannot provide that measurement. onSizeChanged gives tablet sizing.
    // Seed with the 64dp resting height to avoid a one-frame size pop before measurement.
    var measuredHeightPx by remember { mutableIntStateOf(with(density) { 64.dp.roundToPx() }) }
    // Ignore taps while the morph animation owns input.
    val tryOpenPlayer = { if (PlayerMorph.miniAcceptsInput) onClick() }
    // Match the tab bar shadow while the morph card is still the pill.
    val miniShadowPx = with(density) { (if (isInline) 6.dp else 12.dp).toPx() }
    androidx.compose.runtime.SideEffect { PlayerMorph.miniShadowElevationPx = miniShadowPx }
    LaunchedEffect(Unit) {
        androidx.compose.runtime.snapshotFlow { PlayerMorph.active || PlayerMorph.landingActive }.collect { flying ->
            if (flying) {
                PlayerMorph.pillShadow.snapTo(0f)
            } else if (PlayerMorph.pillShadow.value < 1f) {
                PlayerMorph.pillShadow.animateTo(
                    1f,
                    androidx.compose.animation.core.tween(PILL_SHADOW_RETURN_MS, easing = SeamlessEasing),
                )
            }
        }
    }
    // Clear stale morph bounds when switching layout branches.
    LaunchedEffect(tabStyle, isInline) {
        clearMiniContentControlsRects()
    }
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            // Same tanh-damped stretch as the search pill, draw-only.
            // Wide pills (expanded accessory, tab-view pill) run 2x so the
            // travel reads at pill size -- same as bar pills elsewhere.
            // Toned down from 1x/2x (user: touch stretch "way too much").
            .graphicsLayer(elasticStretchLayerBlock(glow, magnitude = if (isInline) 0.6f else 1.2f))
            // Hide the whole pill while the shared card draws its plate during the morph.
            .graphicsLayer {
                // Hidden through the close landing too: the morph card carries the landing
                // (dip + squash) and hands back only once it has settled on the slot.
                alpha = if (PlayerMorph.active || PlayerMorph.landingActive) 0f else 1f
            }
            // Capture the pill surface separately; the morph card stretches it as its starting skin.
            .recordPlayerLayer(
                layer = { if (PlayerMorph.enabled) PlayerMorph.miniSurfaceLayer else null },
                drawInPlace = { true },
            )
            .then(modifier)
            // Record the pill bounds where the navigation bar places it.
            .miniContainerMorphSource()
            // Keep content at its target size during overshoot to prevent layout jitter.
            .noOvershootContent()
            // Record content for the overlay so it can draw the morph outside the bar.
            .recordPlayerLayer(
                // Avoid layer capture when morphing is disabled.
                layer = { if (PlayerMorph.enabled) PlayerMorph.miniLayer else null },
                drawInPlace = { !PlayerMorph.active && !PlayerMorph.landingActive },
                markReady = { PlayerMorph.miniFrameReady = true },
            )
            .onSizeChanged { measuredHeightPx = it.height }
            // Finger-following glow, same as the search pill.
            .then(glow.gestureModifier)
            // Clip the sheen to the pill while pressed.
            .clipWhilePressed(glow, com.convx.music.ui.component.shapes.NavContinuousCapsule)
            .then(glow.modifier)
            // Edge-flick skip only: center drag stretches (via glow above)
            // but never slides or commits. Snaps hop to the card scope.
            .pointerInput(swipeEnabled) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val pid = down.id
                    val start = down.position
                    val edgePx = with(density) { 64.dp.toPx() }
                    val verticalCapPx = with(density) { 36.dp.toPx() }
                    val isEdgeZone = start.x < edgePx || start.x > size.width - edgePx
                    val allowSwipe = swipeEnabled && isEdgeZone
                    dragStartTime = System.currentTimeMillis()
                    totalDragDistance = 0f
                    var lastAdjustedX = 0f
                    var maxAbsY = 0f
                    // Track drag distance locally; the animation updates asynchronously.
                    var slideX = offsetXAnimatable.value
                    // An upward drag hands control to the player sheet until release.
                    var verticalDrag = false
                    var lastY = start.y
                    val velocityTracker = VelocityTracker()
                    try {
                    while (true) {
                        val change = awaitPointerEvent().changes.firstOrNull { it.id == pid }
                        if (change == null || !change.pressed) break
                        val raw = change.position - start
                        velocityTracker.addPointerInputChange(change)
                        if (!verticalDrag && abs(slideX) < viewConfig.touchSlop &&
                            -raw.y > viewConfig.touchSlop && abs(raw.y) > abs(raw.x) * 1.2f &&
                            PlayerMorph.miniAcceptsInput
                        ) {
                            verticalDrag = true
                            lastY = change.position.y
                            PlayerMorph.onDragStart(true)
                        }
                        if (verticalDrag) {
                            PlayerMorph.onDragDelta(change.position.y - lastY)
                            lastY = change.position.y
                            change.consume()
                            continue
                        }
                        maxAbsY = maxOf(maxAbsY, abs(raw.y))
                        val adjustedX =
                            if (layoutDirection == LayoutDirection.Rtl) -raw.x else raw.x
                        val delta = adjustedX - lastAdjustedX
                        lastAdjustedX = adjustedX
                        totalDragDistance += abs(delta)
                        if (allowSwipe) {
                            val canSkipPrevious =
                                playerConnection.player.previousMediaItemIndex != -1
                            val canSkipNext =
                                playerConnection.player.nextMediaItemIndex != -1
                            val allowLeft = delta < 0 && canSkipNext
                            val allowRight = delta > 0 && canSkipPrevious
                            val canReturnToCenter =
                                (delta > 0 && !canSkipPrevious && slideX < 0) ||
                                    (delta < 0 && !canSkipNext && slideX > 0)
                            if (allowLeft || allowRight || canReturnToCenter) {
                                if (abs(slideX) > viewConfig.touchSlop) {
                                    change.consume()
                                }
                                slideX += delta
                                coroutineScope.launch { offsetXAnimatable.snapTo(slideX) }
                            }
                        } else if (totalDragDistance > viewConfig.touchSlop) {
                            // Consume center drags so they cannot trigger the tap handler.
                            change.consume()
                        }
                    }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        // Settle the sheet if the pill is removed during a drag.
                        if (verticalDrag) PlayerMorph.onDragCancel()
                        throw e
                    }
                    if (verticalDrag) {
                        // Sheet convention: positive = toward expanded.
                        PlayerMorph.onDragEnd(-velocityTracker.calculateVelocity().y)
                        return@awaitEachGesture
                    }
                    val duration = System.currentTimeMillis() - dragStartTime
                    // Average gesture velocity in pixels per millisecond.
                    val velocity = if (duration > 0) totalDragDistance / duration else 0f
                    val currentOffset = slideX
                    val dragged = abs(currentOffset)
                    val canSkipPrevious =
                        playerConnection.player.previousMediaItemIndex != -1
                    val canSkipNext =
                        playerConnection.player.nextMediaItemIndex != -1
                    // A quick edge flick or a drag past the threshold skips a track.
                    val shouldChangeSong = allowSwipe &&
                        maxAbsY < verticalCapPx &&
                        (dragged > autoSwipeThreshold ||
                            (velocity > 0.55f && dragged > autoSwipeThreshold * 0.25f))
                    if (shouldChangeSong) {
                        // Use the connection to preserve cast routing and skip callbacks.
                        if (currentOffset > 0 && canSkipPrevious) {
                            playerConnection.seekToPrevious()
                        } else if (currentOffset < 0 && canSkipNext) {
                            playerConnection.seekToNext()
                        }
                    }
                    if (totalDragDistance > viewConfig.touchSlop) onSnapRelease()
                    coroutineScope.launch {
                        offsetXAnimatable.animateTo(0f, animationSpec)
                    }
                }
            }
            // Taps open the player and long presses open the song menu. Drag handlers run first.
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = tryOpenPlayer,
                onLongClick = onLongClick,
            )
    ) {
        // Only the tablet pill changes height, so only it scales content to its measured height.
        val animatedHeightPx by animateIntAsState(
            targetValue = if (tabStyle) measuredHeightPx else with(density) { 64.dp.roundToPx() },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "miniPlayerMeasuredHeight",
        )
        val measuredHeight = with(density) { animatedHeightPx.toDp() }
        val artSize = if (tabStyle) (measuredHeight * 0.6f).coerceIn(36.dp, 60.dp) else 38.dp
        val artCornerRadius = artSize * 0.22f
        val controlSize = if (tabStyle) (measuredHeight * 0.55f).coerceIn(36.dp, 52.dp) else 36.dp

        // Keep one layout during the morph; read animated width in layout/draw to avoid recomposition.
        // The row writes this field before measuring children, so they use the same frame's width.
        val pillWidthPx = remember { IntArray(1) }
        val extras = {
            val dp = pillWidthPx[0] / density.density
            ((dp - MiniExtrasGoneWidth) / (MiniExtrasFullWidth - MiniExtrasGoneWidth)).coerceIn(0f, 1f)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    pillWidthPx[0] = constraints.maxWidth
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }
                // Shrunk height pinned to the search pill's row height, so the
                // mini and search pills always sit at the same height.
                .then(if (isInline) Modifier.height(SearchBarRowHeight) else Modifier)
                .offset { IntOffset(offsetXAnimatable.value.roundToInt(), 0) }
                .padding(
                    horizontal = 12.dp,
                    vertical = if (isInline) 6.dp else 8.dp,
                ),
        ) {
            if (!tabStyle) {
                val iconSize = controlSize * 0.5f
                val playIconSize = controlSize * 0.6f
                // Keep playback progress as State so the seek bar reads it during drawing.
                val playbackFraction = rememberPlaybackFraction(playerConnection.player, isPlaying)
                val waveformSeed = remember(mediaMetadata?.id) { mediaMetadata?.id?.hashCode() ?: 0 }

                // Left-side tap consumer: tapping thumbnail or song info exposes player
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = tryOpenPlayer,
                        ),
                ) {
                    AsyncImage(
                        // Same URL AND decode size as PlayerArtworkMorphOverlay and the
                        // full player's own artwork -- one shared Coil cache entry for
                        // all three, so the pill, the flying cover, and the handoff
                        // target are pixel-identical (no re-decode pop between them).
                        model = ImageRequest.Builder(context)
                            .data(mediaMetadata?.thumbnailUrl?.resize(1200, 1200))
                            .size(CoilSize.ORIGINAL)
                            // A fresh AsyncImage mounts on every bar state change (inline and
                            // expanded are separate trees); without this its first frame can be
                            // the empty placeholder -- the thumbnail "vanishing" mid-transition.
                            .placeholderMemoryCacheKey(mediaMetadata?.thumbnailUrl?.resize(1200, 1200))
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberNoArtworkPainter(),
                        error = rememberNoArtworkPainter(),
                        modifier = Modifier
                            .size(artSize)
                            .miniArtworkMorphSource(with(density) { artCornerRadius.toPx() })
                            .clip(RoundedCornerShape(artCornerRadius)),
                    )

                    Spacer(Modifier.width(10.dp))

                    Column(
                        // Combine both lines into one layer; their glyphs do not overlap.
                        modifier = Modifier
                            .weight(1f)
                            .flamingoChrome(useGlass && isDark),
                    ) {
                        // Scroll long titles only during playback; paused titles stay still.
                        Box(Modifier.marqueeEdgeFade(MarqueeFade)) {
                            Text(
                                text = mediaMetadata?.title.orEmpty(),
                                style = AppTheme.typography.bodyMedium,
                                fontSize = 15.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.23).sp,
                                color = contentColor,
                                maxLines = 1,
                                modifier = Modifier
                                    .basicMarquee(
                                        iterations = if (isPlaying) Int.MAX_VALUE else 0,
                                        repeatDelayMillis = 3000,
                                        initialDelayMillis = 3000,
                                        spacing = MarqueeSpacing(40.dp),
                                        velocity = 40.dp,
                                    )
                                    .padding(start = MarqueeFade),
                            )
                        }
                        Text(
                            text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                            style = AppTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            letterSpacing = 0.06.sp,
                            color = contentColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (miniPlayerWaveform) {
                    Spacer(Modifier.scaledWidth(8.dp, fraction = extras))
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
                            .scaledWidth(64.dp, fade = true, fraction = extras)
                            .height(22.dp)
                            .flamingoChrome(useGlass && isDark),
                    )
                }

                Spacer(Modifier.width(10.dp))

                IconButton(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier
                        .size(controlSize)
                        .registerMiniControlsPlayRect(),
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlaying,
                        tint = playTint,
                        size = playIconSize,
                    )
                }
                IconButton(
                    onClick = { playerConnection.requestSkip(forward = true) },
                    enabled = canSkipNext,
                    modifier = Modifier
                        .scaledWidth(controlSize, fade = true, fraction = extras)
                        .height(controlSize)
                        .registerMiniControlsNextRect(),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.NEXT,
                        fallbackVector = SfSymbols.Monochrome.SFForwardFill,
                        tint = if (canSkipNext) transportTint else transportTint.copy(alpha = 0.4f),
                        modifier = Modifier.size(playIconSize).flamingoChrome(useGlass && isDark),
                    )
                }
            } else {
                // Scale the icon itself as well as the tablet pill's tap targets.
                val iconSize = controlSize * 0.5f
                val playIconSize = controlSize * 0.6f
                IconButton(
                    onClick = { playerConnection.player.shuffleModeEnabled = !shuffleModeEnabled },
                    modifier = Modifier.size(controlSize),
                ) {
                    SymbolIcon(symbol = sf("SFShuffle"), contentDescription = stringResource(R.string.shuffle_label), modifier = Modifier.size(iconSize).flamingoChrome(useGlass && isDark), tint = transportTint)
                }
                IconButton(
                    onClick = { playerConnection.requestSkip(forward = false) },
                    enabled = canSkipPrevious,
                    modifier = Modifier.size(controlSize),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.PREVIOUS,
                        fallbackVector = SfSymbols.Monochrome.SFBackwardFill,
                        tint = if (canSkipPrevious) transportTint else transportTint.copy(alpha = 0.4f),
                        modifier = Modifier.size(iconSize).flamingoChrome(useGlass && isDark),
                    )
                }
                IconButton(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.size(controlSize),
                ) {
                    AnimatedPlayPauseIcon(
                        isPlaying = isPlaying,
                        tint = playTint,
                        size = playIconSize,
                    )
                }
                IconButton(
                    onClick = { playerConnection.requestSkip(forward = true) },
                    enabled = canSkipNext,
                    modifier = Modifier.size(controlSize),
                ) {
                    PlayerGlyph(
                        slot = PlayerIconSlot.NEXT,
                        fallbackVector = SfSymbols.Monochrome.SFForwardFill,
                        tint = if (canSkipNext) transportTint else transportTint.copy(alpha = 0.4f),
                        modifier = Modifier.size(playIconSize).flamingoChrome(useGlass && isDark),
                    )
                }
                IconButton(
                    onClick = { playerConnection.player.toggleRepeatMode() },
                    modifier = Modifier.size(controlSize),
                ) {
                    SymbolIcon(symbol = if (repeatMode == Player.REPEAT_MODE_ONE) sf("SFRepeat1") else sf("SFRepeat"), contentDescription = stringResource(R.string.repeat_mode_all), modifier = Modifier.size(iconSize).flamingoChrome(useGlass && isDark), tint = if (repeatMode != Player.REPEAT_MODE_OFF) transportTint else transportTint.copy(alpha = 0.4f))
                }

                Spacer(Modifier.width(8.dp))

                // Tap consumer for thumbnail + song info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = tryOpenPlayer,
                        ),
                ) {
                    AsyncImage(
                        // Same URL AND decode size as PlayerArtworkMorphOverlay and the
                        // full player's own artwork -- one shared Coil cache entry for
                        // all three, so the pill, the flying cover, and the handoff
                        // target are pixel-identical (no re-decode pop between them).
                        model = ImageRequest.Builder(context)
                            .data(mediaMetadata?.thumbnailUrl?.resize(1200, 1200))
                            .size(CoilSize.ORIGINAL)
                            // A fresh AsyncImage mounts on every bar state change (inline and
                            // expanded are separate trees); without this its first frame can be
                            // the empty placeholder -- the thumbnail "vanishing" mid-transition.
                            .placeholderMemoryCacheKey(mediaMetadata?.thumbnailUrl?.resize(1200, 1200))
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberNoArtworkPainter(),
                        error = rememberNoArtworkPainter(),
                        modifier = Modifier
                            .size(artSize)
                            .miniArtworkMorphSource(with(density) { artCornerRadius.toPx() })
                            .clip(RoundedCornerShape(artCornerRadius)),
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(
                        // Combine both lines into one layer; their glyphs do not overlap.
                        modifier = Modifier.weight(1f).flamingoChrome(useGlass && isDark),
                    ) {
                        Text(
                            text = mediaMetadata?.title.orEmpty(),
                            style = AppTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.23).sp,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = mediaMetadata?.artists?.joinToString { it.name }.orEmpty(),
                            style = AppTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            letterSpacing = 0.06.sp,
                            color = contentColor.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onLyricsClick ?: onClick,
                    modifier = Modifier.size(controlSize),
                ) {
                    SymbolIcon(symbol = sf("SFQuoteBubble"), contentDescription = stringResource(R.string.lyrics), modifier = Modifier.size(iconSize * LyricsIconScale).flamingoChrome(useGlass && isDark), tint = transportTint)
                }
                IconButton(
                    onClick = onQueueClick ?: onClick,
                    modifier = Modifier.size(controlSize),
                ) {
                    SymbolIcon(symbol = sf("SFListBullet"), contentDescription = stringResource(R.string.queue), modifier = Modifier.size(iconSize).flamingoChrome(useGlass && isDark), tint = transportTint)
                }
            }
        }
    }
}

/** Measures content at its settled size and centres it in the animated bounds. */
@androidx.compose.runtime.Composable
internal fun Modifier.noOvershootContent(): Modifier {
    // Follow animated width within the target range to avoid reflow during overshoot.
    val hostSpec = com.convx.music.ui.component.floatingtabbar.LocalAccessoryContentSpec.current
    if (hostSpec != null) return layout { measurable, constraints ->
        val w = if (hostSpec.maxWidth > 0) {
            constraints.maxWidth.coerceIn(hostSpec.minWidth, hostSpec.maxWidth)
        } else {
            constraints.maxWidth
        }
        val placeable = measurable.measure(androidx.compose.ui.unit.Constraints(minWidth = w, maxWidth = w))
        if (hostSpec.reportsNaturalHeight) hostSpec.onNaturalHeight(placeable.height)
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        layout(width, height) {
            placeable.placeRelative((width - placeable.width) / 2, (height - placeable.height) / 2)
        }
    }
    val target = androidx.compose.runtime.remember { IntArray(2) { Int.MAX_VALUE } }
    return layout { measurable, constraints ->
        if (isLookingAhead) {
            target[0] = constraints.maxWidth
            target[1] = constraints.maxHeight
        }
        val child = constraints.copy(
            minWidth = minOf(constraints.minWidth, target[0]),
            maxWidth = minOf(constraints.maxWidth, target[0]),
            minHeight = minOf(constraints.minHeight, target[1]),
            maxHeight = minOf(constraints.maxHeight, target[1]),
        )
        val placeable = measurable.measure(child)
        val width = constraints.constrainWidth(placeable.width)
        val height = constraints.constrainHeight(placeable.height)
        layout(width, height) {
            placeable.place((width - placeable.width) / 2, (height - placeable.height) / 2)
        }
    }
}

/** How long the docked pill's shadow takes to come back after the player lands in it. */
internal const val PILL_SHADOW_RETURN_MS = 320
