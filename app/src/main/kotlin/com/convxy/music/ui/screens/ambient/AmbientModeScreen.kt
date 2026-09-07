/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.ambient

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.constants.AmbientAutoHideBackButtonEnabledKey
import com.convxy.music.constants.AmbientCanvasAnchorSide
import com.convxy.music.constants.AmbientCanvasAnchorSideKey
import com.convxy.music.constants.AmbientCanvasEdgeFeatherKey
import com.convxy.music.constants.AmbientCanvasFarVeilKey
import com.convxy.music.constants.AmbientCanvasFitMode
import com.convxy.music.constants.AmbientCanvasFitModeKey
import com.convxy.music.constants.AmbientCanvasGradientSpreadKey
import com.convxy.music.constants.AmbientCanvasSideFitEnabledKey
import com.convxy.music.constants.AmbientCanvasSideGradientKey
import com.convxy.music.constants.AmbientCanvasSideWidthKey
import com.convxy.music.constants.AmbientLyricsTextSizeKey
import com.convxy.music.constants.AmbientProgressRingEnabledKey
import com.convxy.music.constants.AmbientPlaybackFeedbackEnabledKey
import com.convxy.music.constants.AmbientSeekHapticsEnabledKey
import com.convxy.music.constants.AmbientSeekTimeEnabledKey
import com.convxy.music.constants.AmbientSwipeNavigationEnabledKey
import com.convxy.music.constants.AmbientTapToPlayPauseEnabledKey
import com.convxy.music.constants.AmbientTrackInfoEnabledKey
import com.convxy.music.constants.AmbientTrackTransitionsEnabledKey
import com.convxy.music.constants.AmbientVideoCanvasBlurKey
import com.convxy.music.constants.AmbientVideoCanvasDimKey
import com.convxy.music.constants.AmbientVideoCanvasEnabledKey
import com.convxy.music.constants.AmbientCanvasSourceKey
import com.convxy.music.constants.CanvasSource
import com.convxy.music.constants.LyricsTextSizeKey
import com.convxy.music.models.MediaMetadata
import com.convxy.music.playback.PlayerConnection
import com.convxy.music.ui.component.AnimatedPlayPauseIcon
import com.convxy.music.ui.player.InlineLyricsView
import com.convxy.music.utils.makeTimeString
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor

@Composable
fun AmbientModeScreen(navController: NavController) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val ambientVideoCanvasEnabled by rememberPreference(
        AmbientVideoCanvasEnabledKey,
        defaultValue = false,
    )
    val ambientCanvasSource by rememberEnumPreference(
        AmbientCanvasSourceKey,
        defaultValue = CanvasSource.AUTO,
    )
    val ambientCanvasBlur by rememberPreference(
        AmbientVideoCanvasBlurKey,
        defaultValue = 12f,
    )
    val ambientCanvasDim by rememberPreference(
        AmbientVideoCanvasDimKey,
        defaultValue = 0.42f,
    )
    // Canvas Position & Fit: portrait canvases hug one side of the 16:9 layout behind a
    // stronger gradient, instead of being cropped to cover the whole screen.
    val ambientCanvasSideFitEnabled by rememberPreference(
        AmbientCanvasSideFitEnabledKey,
        defaultValue = false,
    )
    val ambientCanvasAnchorSide by rememberEnumPreference(
        AmbientCanvasAnchorSideKey,
        defaultValue = AmbientCanvasAnchorSide.AUTO,
    )
    val ambientCanvasFitMode by rememberEnumPreference(
        AmbientCanvasFitModeKey,
        defaultValue = AmbientCanvasFitMode.FIT,
    )
    val ambientCanvasSideWidth by rememberPreference(
        AmbientCanvasSideWidthKey,
        defaultValue = AmbientCanvasFitDefaults.SideWidth,
    )
    val ambientCanvasSideGradient by rememberPreference(
        AmbientCanvasSideGradientKey,
        defaultValue = AmbientCanvasFitDefaults.SideGradient,
    )
    val ambientCanvasGradientSpread by rememberPreference(
        AmbientCanvasGradientSpreadKey,
        defaultValue = AmbientCanvasFitDefaults.GradientSpread,
    )
    val ambientCanvasFarVeil by rememberPreference(
        AmbientCanvasFarVeilKey,
        defaultValue = AmbientCanvasFitDefaults.FarVeil,
    )
    val ambientCanvasFeather by rememberPreference(
        AmbientCanvasEdgeFeatherKey,
        defaultValue = AmbientCanvasFitDefaults.EdgeFeather,
    )
    val globalLyricsTextSize by rememberPreference(
        LyricsTextSizeKey,
        defaultValue = 30f,
    )
    val ambientLyricsTextSize by rememberPreference(
        AmbientLyricsTextSizeKey,
        defaultValue = globalLyricsTextSize,
    )
    val progressRingEnabled by rememberPreference(
        AmbientProgressRingEnabledKey,
        defaultValue = true,
    )
    val playbackFeedbackEnabled by rememberPreference(
        AmbientPlaybackFeedbackEnabledKey,
        defaultValue = true,
    )
    val seekTimeEnabled by rememberPreference(
        AmbientSeekTimeEnabledKey,
        defaultValue = true,
    )
    val seekHapticsEnabled by rememberPreference(
        AmbientSeekHapticsEnabledKey,
        defaultValue = true,
    )
    val trackInfoEnabled by rememberPreference(
        AmbientTrackInfoEnabledKey,
        defaultValue = true,
    )
    val tapToPlayPauseEnabled by rememberPreference(
        AmbientTapToPlayPauseEnabledKey,
        defaultValue = true,
    )
    val swipeNavigationEnabled by rememberPreference(
        AmbientSwipeNavigationEnabledKey,
        defaultValue = true,
    )
    val trackTransitionsEnabled by rememberPreference(
        AmbientTrackTransitionsEnabledKey,
        defaultValue = true,
    )
    val autoHideBackButtonEnabled by rememberPreference(
        AmbientAutoHideBackButtonEnabledKey,
        defaultValue = true,
    )
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        val window = activity?.window
        var windowInsetsController: WindowInsetsControllerCompat? = null
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            activity?.requestedOrientation = originalOrientation
            if (window != null) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    BackHandler {
        navController.popBackStack()
    }

    // Positive means a rightward finger movement: the previous track enters from the
    // left. Negative means a leftward movement: the next track enters from the right.
    var transitionDirection by remember { mutableIntStateOf(1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var ringSeekPreview by remember { mutableStateOf<Float?>(null) }
    var showPlaybackFeedback by remember { mutableStateOf(false) }
    var feedbackIsPlaying by remember { mutableStateOf(false) }
    var showBackButton by remember { mutableStateOf(true) }
    var chromeInteractionToken by remember { mutableIntStateOf(0) }
    var showTrackInfo by remember { mutableStateOf(false) }
    var canvasReady by remember { mutableStateOf(false) }
    // Reported by the canvas player once the decoder knows the frame geometry. Position &
    // Fit sizes the side panel from it, so a 9:16 canvas is hugged rather than guessed at.
    var canvasVideoAspect by remember { mutableFloatStateOf(0f) }
    var ringSeekPreviewPosition by remember { mutableLongStateOf(0L) }
    var ringSeekPreviewDuration by remember { mutableLongStateOf(0L) }
    var trackInfoMediaId by remember { mutableStateOf<String?>(null) }
    var trackInfoWasPlaying by remember { mutableStateOf(isPlaying) }
    val latestIsPlayingState = rememberUpdatedState(isPlaying)
    val latestMediaMetadata = rememberUpdatedState(mediaMetadata)
    val latestBackButtonVisible = rememberUpdatedState(showBackButton)
    var feedbackResetJob by remember { mutableStateOf<Job?>(null) }

    fun toggleWithFeedback() {
        feedbackIsPlaying = !latestIsPlayingState.value
        showPlaybackFeedback = true
        feedbackResetJob?.cancel()
        feedbackResetJob = coroutineScope.launch {
            delay(650L)
            showPlaybackFeedback = false
        }
    }

    LaunchedEffect(chromeInteractionToken, autoHideBackButtonEnabled) {
        showBackButton = true
        if (autoHideBackButtonEnabled) {
            delay(3000L)
            showBackButton = false
        }
    }

    LaunchedEffect(mediaMetadata?.id, ambientVideoCanvasEnabled, ambientCanvasSource) {
        canvasReady = false
        canvasVideoAspect = 0f
    }

    LaunchedEffect(mediaMetadata?.id) {
        ringSeekPreview = null
        ringSeekPreviewPosition = 0L
        ringSeekPreviewDuration = 0L
        if (mediaMetadata != null) {
            showBackButton = true
            chromeInteractionToken++
        }
    }

    // Track information stays visible while paused, but returns to the approved
    // temporary lower-third behavior during playback. A resume hides it immediately;
    // a newly selected playing track gets the normal short reveal.
    LaunchedEffect(mediaMetadata?.id, isPlaying, trackInfoEnabled) {
        val currentId = mediaMetadata?.id
        if (currentId == null || !trackInfoEnabled) {
            showTrackInfo = false
            return@LaunchedEffect
        }

        val trackChanged = currentId != trackInfoMediaId
        val resumed = !trackChanged && !trackInfoWasPlaying && isPlaying
        trackInfoMediaId = currentId
        trackInfoWasPlaying = isPlaying

        when {
            !isPlaying -> {
                showTrackInfo = true
            }

            resumed -> {
                showTrackInfo = false
            }

            else -> {
                showTrackInfo = true
                delay(2400L)
                if (
                    latestIsPlayingState.value &&
                    latestMediaMetadata.value?.id == currentId &&
                    trackInfoEnabled
                ) {
                    showTrackInfo = false
                }
            }
        }
    }

    val ringHitSlop = with(density) { 36.dp.toPx() }
    val ringInset = with(density) { 2.dp.toPx() }
    val ringCornerRadius = with(density) { 24.dp.toPx() }
    val touchSlop = with(density) { 18.dp.toPx() }
    val canvasBlurRadius = ambientCanvasBlur.coerceIn(0f, 24f)
    val canvasDimOpacity = ambientCanvasDim.coerceIn(0f, 0.75f)

    Box(modifier = Modifier.fillMaxSize()) {
        // Keep the existing ambient glow as the lightweight fallback while a canvas
        // is loading or when the current track has no canvas artwork. With Position & Fit
        // it also stays behind the whole layout, because it is what the feathered edge of
        // the side panel dissolves into.
        if (!ambientVideoCanvasEnabled || !canvasReady || ambientCanvasSideFitEnabled) {
            AmbientGlowBackground(
                mediaMetadata = mediaMetadata,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (ambientVideoCanvasEnabled) {
            // The panel is expressed as a share of the screen width, so the screen's own
            // aspect ratio is needed to size it against the canvas' aspect ratio.
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenAspect =
                    if (maxHeight > 0.dp) maxWidth.value / maxHeight.value else 16f / 9f
                val anchoredRight = ambientCanvasAnchoredRight(
                    anchor = ambientCanvasAnchorSide,
                    isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl,
                )
                val panelFraction = ambientCanvasPanelFraction(
                    videoAspect = canvasVideoAspect,
                    screenAspect = screenAspect,
                    requestedFraction = ambientCanvasSideWidth,
                    fitMode = ambientCanvasFitMode,
                )
                // Landscape canvases keep the ordinary full-width background even with the
                // option on — Position & Fit only re-seats canvases that lose too much to a
                // 16:9 crop.
                val useSidePanel = ambientCanvasSideFitEnabled &&
                    ambientCanvasUsesSidePanel(panelFraction)
                val animatedPanelWidth by animateFloatAsState(
                    targetValue = if (useSidePanel) panelFraction else 1f,
                    animationSpec = tween(280),
                    label = "ambientCanvasPanelWidth",
                )

                AmbientVideoCanvas(
                    mediaMetadata = mediaMetadata,
                    isPlaying = isPlaying,
                    canvasSource = ambientCanvasSource,
                    fitMode = if (useSidePanel) ambientCanvasFitMode else AmbientCanvasFitMode.ZOOM,
                    onReady = { canvasReady = true },
                    onExhausted = { canvasReady = false },
                    onVideoAspectRatio = { canvasVideoAspect = it },
                    modifier = Modifier
                        .then(
                            if (useSidePanel) {
                                Modifier
                                    .align(
                                        if (anchoredRight) Alignment.CenterEnd
                                        else Alignment.CenterStart
                                    )
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedPanelWidth)
                            } else {
                                Modifier.fillMaxSize()
                            }
                        )
                        .then(
                            if (canvasBlurRadius > 0f) Modifier.blur(canvasBlurRadius.dp) else Modifier
                        )
                        .ambientCanvasEdgeFeather(
                            fraction = if (useSidePanel) ambientCanvasFeather else 0f,
                            anchoredRight = anchoredRight,
                        ),
                )

                if (canvasReady) {
                    if (useSidePanel) {
                        // The asymmetric veil: heavier on the side the canvas occupies, so
                        // the moving picture stays subordinate to the artwork, and lighter
                        // over the lyrics so they keep a calm ground. Both ends, and how far
                        // the strong side reaches, are adjustable in Ambient Mode settings.
                        val (nearAlpha, farAlpha) = ambientCanvasVeilAlphas(
                            dim = canvasDimOpacity,
                            sideGradient = ambientCanvasSideGradient,
                            farVeil = ambientCanvasFarVeil,
                        )
                        AmbientCanvasVeil(
                            nearAlpha = nearAlpha,
                            farAlpha = farAlpha,
                            spread = ambientCanvasGradientSpread,
                            anchoredRight = anchoredRight,
                        )
                    } else {
                        // Keep the motion immersive but subordinate to the album art and
                        // lyrics. The opacity is adjustable from Ambient Mode settings.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = canvasDimOpacity))
                        )
                    }
                }
            }
        }

        // This is deliberately drawn before the content. It is a bezel detail, not a
        // player control, and the touch handling below only claims its narrow edge
        // hit area when the player reports a seekable duration.
        if (progressRingEnabled) {
            AmbientProgressRing(
                playerConnection = playerConnection,
                mediaId = mediaMetadata?.id,
                isPlaying = isPlaying,
                seekPreviewProgress = ringSeekPreview,
                modifier = Modifier.fillMaxSize()
            )
        }

        val animatedDragOffset by androidx.compose.animation.core.animateFloatAsState(
            targetValue = dragOffset,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = 1100f
            ),
            label = "ambientDragOffset"
        )

        // A single gesture arena prevents a tap, a horizontal skip, and a ring seek
        // from all reacting to the same pointer sequence. Vertical movement is left
        // available to the lyrics surface.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(
                    playerConnection,
                    mediaMetadata?.id,
                    progressRingEnabled,
                    playbackFeedbackEnabled,
                    seekHapticsEnabled,
                    swipeNavigationEnabled,
                    tapToPlayPauseEnabled,
                    ringHitSlop,
                    ringInset,
                    ringCornerRadius,
                    touchSlop,
                ) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val wasBackButtonVisible = latestBackButtonVisible.value
                        val startedInBackButtonArea = isAmbientBackButtonTouch(down.position, ringHitSlop)
                        showBackButton = true
                        chromeInteractionToken++

                        // When the back button is hidden, the first tap in its
                        // former area only reveals it. This prevents an accidental
                        // play/pause toggle while bringing navigation back.
                        if (!wasBackButtonVisible && startedInBackButtonArea) {
                            down.consume()
                            return@awaitEachGesture
                        }

                        val pointerId = down.id
                        val downPosition = down.position
                        val gestureSize = Size(
                            width = size.width.toFloat(),
                            height = size.height.toFloat()
                        )
                        val ringDuration = if (progressRingEnabled &&
                            !startedInBackButtonArea &&
                            isNearBezel(
                                downPosition,
                                gestureSize,
                                ringHitSlop
                            )
                        ) {
                            runCatching {
                                val player = playerConnection.player
                                if (player.isCommandAvailable(
                                        androidx.media3.common.Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
                                    )
                                ) {
                                    player.duration
                                } else {
                                    -1L
                                }
                            }.getOrDefault(-1L)
                        } else {
                            -1L
                        }
                        val isRingGesture = ringDuration > 0L
                        var totalX = 0f
                        var totalY = 0f
                        var axis: GestureAxis? = null
                        var lastRingFraction = -1f
                        var lastHapticMarker = -1
                        var tapWasConsumed = false

                        fun seekFromRing(position: Offset, force: Boolean = false) {
                            if (!isRingGesture) return
                            val fraction = bezelProgressFor(
                                position = position,
                                size = gestureSize,
                                inset = ringInset,
                                cornerRadius = ringCornerRadius
                            )
                            ringSeekPreview = fraction
                            ringSeekPreviewPosition = (ringDuration * fraction).toLong()
                            ringSeekPreviewDuration = ringDuration

                            // Give the user a quiet tactile cue at each quarter of
                            // the track while dragging, without buzzing on touch-down.
                            val hapticMarker = (floor(fraction * 4f)).toInt().coerceIn(0, 3)
                            if (seekHapticsEnabled &&
                                lastHapticMarker >= 0 &&
                                hapticMarker != lastHapticMarker
                            ) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            lastHapticMarker = hapticMarker

                            // Avoid flooding Media3 with effectively identical seek
                            // commands while still making the ring feel immediate.
                            if (force || lastRingFraction < 0f || abs(fraction - lastRingFraction) >= 0.003f) {
                                playerConnection.seekTo((ringDuration * fraction).toLong())
                                lastRingFraction = fraction
                            }
                        }

                        if (isRingGesture) {
                            down.consume()
                            seekFromRing(downPosition)
                        }

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                            val delta = change.position - change.previousPosition
                            totalX += delta.x
                            totalY += delta.y

                            if (isRingGesture) {
                                change.consume()
                                seekFromRing(change.position, force = !change.pressed)
                                if (!change.pressed) break
                                continue
                            }

                            if (axis == null && (abs(totalX) > touchSlop || abs(totalY) > touchSlop)) {
                                axis = if (abs(totalX) >= abs(totalY)) {
                                    GestureAxis.Horizontal
                                } else {
                                    GestureAxis.Vertical
                                }
                            }

                            when (axis) {
                                GestureAxis.Horizontal -> {
                                    change.consume()
                                    // Keep the current scene attached to the finger.
                                    // The directional AnimatedContent transition takes
                                    // over when the skip is committed on release.
                                    dragOffset = totalX.coerceIn(
                                        -gestureSize.width * 0.42f,
                                        gestureSize.width * 0.42f
                                    )
                                }

                                GestureAxis.Vertical -> Unit

                                null -> Unit
                            }

                            if (!change.pressed) {
                                tapWasConsumed = change.isConsumed
                                break
                            }
                        }

                        if (isRingGesture) {
                            ringSeekPreview = null
                            ringSeekPreviewPosition = 0L
                            ringSeekPreviewDuration = 0L
                        } else {
                            when (axis) {
                                GestureAxis.Horizontal -> {
                                    if (swipeNavigationEnabled &&
                                        abs(totalX) >= 150f &&
                                        abs(totalX) > abs(totalY)
                                    ) {
                                        val direction = if (totalX > 0f) 1 else -1
                                        transitionDirection = direction
                                        dragOffset = 0f
                                        if (direction > 0) {
                                            // Keep Ambient Mode's established behavior: a
                                            // right swipe always moves to the previous
                                            // queue item rather than using the normal
                                            // "restart after three seconds" button rule.
                                            playerConnection.player.seekToPreviousMediaItem()
                                        } else {
                                            playerConnection.player.seekToNext()
                                        }
                                    } else {
                                        dragOffset = 0f
                                    }
                                }

                                GestureAxis.Vertical -> {
                                    dragOffset = 0f
                                }

                                // A short, non-directional release is the only gesture
                                // that toggles playback. Horizontal swipes never fall
                                // through to this branch.
                                null -> {
                                    // Lyrics lines have their own tap-to-seek
                                    // behavior. If that child consumed the tap,
                                    // do not also toggle playback.
                                    if (tapToPlayPauseEnabled &&
                                        !tapWasConsumed &&
                                        !startedInBackButtonArea
                                    ) {
                                        playerConnection.togglePlayPause()
                                        if (playbackFeedbackEnabled) {
                                            toggleWithFeedback()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            AnimatedContent(
                targetState = mediaMetadata,
                transitionSpec = {
                    if (!trackTransitionsEnabled) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        val direction = transitionDirection
                        (
                            slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = 850f
                                ),
                                initialOffsetX = { fullWidth -> -direction * fullWidth }
                            ) + fadeIn(tween(90)) + scaleIn(tween(180), initialScale = 0.985f)
                        ).togetherWith(
                            slideOutHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = 850f
                                ),
                                targetOffsetX = { fullWidth -> direction * fullWidth }
                            ) + fadeOut(tween(120)) + scaleOut(tween(180), targetScale = 0.985f)
                        ).using(SizeTransform(clip = false))
                    }
                },
                contentKey = { it?.id },
                label = "ambientTrackTransition",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationX = animatedDragOffset }
            ) { currentMetadata ->
                AmbientForeground(
                    mediaMetadata = currentMetadata,
                    playerConnection = playerConnection,
                    lyricsTextSize = ambientLyricsTextSize,
                    showTrackInfo = trackInfoEnabled &&
                        showTrackInfo &&
                        currentMetadata?.id == mediaMetadata?.id,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Show the ring's exact seek target while dragging instead of making the
        // user estimate it from a very thin bezel line.
        androidx.compose.animation.AnimatedVisibility(
            visible = seekTimeEnabled && ringSeekPreview != null,
            enter = fadeIn(tween(120)) + scaleIn(tween(120), initialScale = 0.92f),
            exit = fadeOut(tween(160)) + scaleOut(tween(160), targetScale = 0.92f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(18.dp),
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = "${makeTimeString(ringSeekPreviewPosition)} / " +
                        makeTimeString(ringSeekPreviewDuration),
                    color = Color.White.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        // Feedback is transient only; there is intentionally no persistent playback
        // button in Ambient Mode.
        androidx.compose.animation.AnimatedVisibility(
            visible = playbackFeedbackEnabled && showPlaybackFeedback,
            enter = fadeIn(tween(120)) + scaleIn(tween(120), initialScale = 0.8f),
            exit = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 0.8f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AnimatedPlayPauseIcon(
                    isPlaying = feedbackIsPlaying,
                    tint = Color.White.copy(alpha = 0.88f),
                    size = 28.dp
                )
            }
        }

        // This sits outside the gesture arena so a back-button tap cannot also be
        // interpreted as a play/pause tap. It fades away after idle time and is
        // revealed again by any interaction.
        androidx.compose.animation.AnimatedVisibility(
            visible = showBackButton,
            enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.86f),
            exit = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 0.86f),
            modifier = Modifier.align(Alignment.TopStart),
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .safeDrawingPadding()
                    .padding(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        }
    }
}

private enum class GestureAxis {
    Horizontal,
    Vertical,
}

private fun isAmbientBackButtonTouch(position: Offset, hitSlop: Float): Boolean =
    position.x <= hitSlop * 2.6f && position.y <= hitSlop * 2.6f

@Composable
private fun AmbientForeground(
    mediaMetadata: MediaMetadata?,
    playerConnection: PlayerConnection,
    lyricsTextSize: Float,
    showTrackInfo: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.safeDrawingPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Side: Album Art
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
            ) {
                AsyncImage(
                    model = mediaMetadata?.thumbnailUrl,
                    contentDescription = "Album Art",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = showTrackInfo && mediaMetadata != null,
                    enter = fadeIn(tween(260)) +
                        slideInVertically(
                            animationSpec = tween(260),
                            initialOffsetY = { it / 4 },
                        ),
                    exit = fadeOut(tween(220)) +
                        slideOutVertically(
                            animationSpec = tween(220),
                            targetOffsetY = { it / 4 },
                        ),
                    modifier = Modifier.align(Alignment.BottomStart),
                ) {
                    mediaMetadata?.let { metadata ->
                        val artistText = metadata.artists
                            .joinToString(", ") { it.name }
                            .ifBlank { "Unknown artist" }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.48f)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.72f),
                                        ),
                                    ),
                                )
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = metadata.title,
                                    color = Color.White.copy(alpha = 0.96f),
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = artistText,
                                    color = Color.White.copy(alpha = 0.76f),
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Side: Lyrics
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 16.dp, end = 32.dp, top = 32.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            InlineLyricsView(
                mediaMetadata = mediaMetadata,
                showLyrics = true,
                positionProvider = { playerConnection.player.currentPosition },
                lyricsTextSize = lyricsTextSize,
            )
        }
    }
}
