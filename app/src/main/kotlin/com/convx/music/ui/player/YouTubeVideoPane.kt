package com.convx.music.ui.player

import android.app.PictureInPictureParams
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.convx.music.R
import com.convx.music.playback.PlayerConnection
import com.convx.music.ui.component.GlassCircleButton
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.utils.Motion
import com.convx.music.utils.YTPlayerUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

@Composable
internal fun YouTubeVideoPane(
    stream: YTPlayerUtils.VideoStream,
    connection: PlayerConnection,
    videoController: CanvasVideoController,
    visible: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    fullscreenOnly: Boolean = false,
    onLeaveVideo: () -> Unit = onClose,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.videoActivity() }
    val fullscreenHost = LocalFullscreenVideo.current
    val coveredByFullscreen = !fullscreenOnly && fullscreenHost?.session?.stream?.mediaId == stream.mediaId
    val metadata by connection.mediaMetadata.collectAsStateWithLifecycle()
    val reduceMotion by com.convx.music.utils.rememberPreference(com.convx.music.constants.ReduceDynamicBlurKey, false)
    var inPictureInPicture by remember(activity) { mutableStateOf(activity?.isInPictureInPictureMode == true) }
    var cropVideo by rememberSaveable(stream.mediaId) { mutableStateOf(false) }
    var controlsVisible by remember(stream.mediaId) { mutableStateOf(true) }
    var controlsInteracting by remember { mutableStateOf(false) }
    var interactionEpoch by remember { mutableIntStateOf(0) }
    var inlineControls by remember(stream.mediaId) { mutableStateOf(false) }
    val playing by connection.isPlaying.collectAsStateWithLifecycle()
    val firstFrame = videoController.videoReady
    val failed = videoController.videoFailed
    val paneVideoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(if (reduceMotion) 1 else 260),
        label = "video-thumbnail-crossfade",
    )

    DisposableEffect(activity) {
        val componentActivity = activity as? ComponentActivity
        val listener = Consumer<PictureInPictureModeChangedInfo> { inPictureInPicture = it.isInPictureInPictureMode }
        componentActivity?.addOnPictureInPictureModeChangedListener(listener)
        onDispose { componentActivity?.removeOnPictureInPictureModeChangedListener(listener) }
    }

    LaunchedEffect(fullscreenOnly, playing, controlsInteracting, interactionEpoch, firstFrame, failed, controlsVisible) {
        if (!fullscreenOnly || !playing || failed || !firstFrame) {
            controlsVisible = true
        } else if (controlsVisible && !controlsInteracting) {
            delay(3_000)
            controlsVisible = false
        }
    }

    // The owning player pauses its decoder when its own sheet reports collapsed; fullscreen must
    // follow playback itself or it opens on a frozen first frame.
    LaunchedEffect(fullscreenOnly, playing) {
        if (fullscreenOnly) videoController.exoPlayer.playWhenReady = playing
    }

    if (fullscreenOnly) LaunchedEffect(videoController) {
        while (true) {
            val exo = videoController.exoPlayer
            android.util.Log.d(
                "FsVideo",
                "state=${exo.playbackState} pwr=${exo.playWhenReady} playing=${exo.isPlaying} pos=${exo.currentPosition} " +
                    "size=${exo.videoSize.width}x${exo.videoSize.height} ready=$firstFrame failed=$failed err=${exo.playerError?.errorCodeName}",
            )
            delay(1_000)
        }
    }

    LaunchedEffect(inlineControls, interactionEpoch) {
        if (inlineControls) {
            delay(3_000)
            inlineControls = false
        }
    }

    val videoSurface: @Composable (Modifier) -> Unit = { bounds ->
        Box(bounds.background(Color.Black), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = metadata?.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            CanvasVideoSurface(videoController, Modifier.fillMaxSize().graphicsLayer { alpha = paneVideoAlpha }, crop = cropVideo, priority = if (fullscreenOnly) 3 else 0)
            if (visible && failed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.video_unavailable), color = Color.White)
                    TextButton(onClick = videoController::retryVideo) {
                        Text(stringResource(R.string.video_retry), color = Color.White)
                    }
                }
            } else if (visible && !firstFrame) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }

    // Only the picture is black; the rest of the pane keeps the player's own background, and the
    // whole pane fades with [visible] so lyrics and queue never sit on a video-coloured layer.
    if (!fullscreenOnly) Box(modifier.graphicsLayer { alpha = paneVideoAlpha }, contentAlignment = Alignment.Center) {
        if (!coveredByFullscreen) Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(stream.width.coerceAtLeast(1).toFloat() / stream.height.coerceAtLeast(1))
                .pointerInput(visible) { detectTapGestures { if (visible) inlineControls = !inlineControls } },
        ) {
            videoSurface(Modifier.fillMaxSize())
            // Corner controls ride the picture's own edges and only appear after a tap.
            AnimatedVisibility(
                visible = visible && inlineControls,
                enter = fadeIn(Motion.gate(reduceMotion, Motion.swapEnter())),
                exit = fadeOut(Motion.gate(reduceMotion, Motion.swapExit())),
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(Modifier.fillMaxSize().padding(12.dp)) {
                    GlassCircleButton(
                        onClick = onClose,
                        applyAdditiveChrome = false,
                        modifier = Modifier.align(Alignment.TopStart),
                    ) {
                        SymbolIcon(symbol = sf("xmark"), contentDescription = stringResource(R.string.video_close), tint = Color.White)
                    }
                    GlassCircleButton(
                        onClick = { fullscreenHost?.open(stream, connection, videoController, onLeaveVideo) },
                        applyAdditiveChrome = false,
                        modifier = Modifier.align(Alignment.TopEnd),
                    ) {
                        SymbolIcon(symbol = sf("arrow.up.left.and.arrow.down.right"), contentDescription = stringResource(R.string.video_fullscreen), tint = Color.White)
                    }
                }
            }
        }
    }

    if (fullscreenOnly) {
        BackHandler(onBack = onClose)
        FullscreenVideoOrientation()
        val videoBackdrop = com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop()
        Box(
            modifier.background(Color.Black)
                .pointerInput(stream.mediaId) {
                    awaitEachGesture { awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial); interactionEpoch++ }
                }
                .pointerInput(stream.mediaId) { detectTapGestures { controlsVisible = !controlsVisible } },
        ) {
            Box(Modifier.fillMaxSize().layerBackdrop(videoBackdrop)) { videoSurface(Modifier.fillMaxSize()) }
            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val ratio = stream.width.coerceAtLeast(1).toFloat() / stream.height.coerceAtLeast(1)
                val frameWidth = if (cropVideo) maxWidth else minOf(maxWidth, maxHeight * ratio)
                val frameHeight = if (cropVideo) maxHeight else frameWidth / ratio
                Box(Modifier.size(frameWidth, frameHeight))
            }
            CompositionLocalProvider(com.convx.music.ui.component.LocalAppBackdrop provides videoBackdrop) {
                AnimatedVisibility(
                    visible = controlsVisible && !inPictureInPicture,
                    enter = fadeIn(Motion.gate(reduceMotion, Motion.swapEnter())),
                    exit = fadeOut(Motion.gate(reduceMotion, Motion.swapExit())),
                ) {
                    FullscreenVideoControls(
                        connection = connection,
                        videoController = videoController,
                        playbackReady = firstFrame && !failed,
                        cropVideo = cropVideo,
                        onCropChange = { cropVideo = it },
                        onInteractionChange = { controlsInteracting = it },
                        onPictureInPicture = {
                            val aspectRatio = (stream.width.toFloat() / stream.height.coerceAtLeast(1)).coerceIn(1f / 2.39f, 2.39f)
                            activity?.enterPictureInPictureMode(PictureInPictureParams.Builder()
                                .setAspectRatio(Rational((aspectRatio * 1000).roundToInt(), 1000)).build())
                        },
                        onClose = onClose,
                        onLeaveVideo = onLeaveVideo,
                    )
                }
            }
        }
    }
}
