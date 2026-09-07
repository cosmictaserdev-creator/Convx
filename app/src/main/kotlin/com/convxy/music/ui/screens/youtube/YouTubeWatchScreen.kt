/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens.youtube

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.convxy.music.LocalDatabase
import com.convxy.music.constants.VideoQualityCapKey
import com.convxy.music.LocalPlayerConnection
import com.convxy.music.R
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import com.convxy.music.extensions.metadata
import com.convxy.music.playback.queues.YouTubeVideoQueue
import com.convxy.music.ui.component.ExpandableText
import com.convxy.music.ui.component.LocalMenuState
import com.convxy.music.ui.menu.YouTubeVideoMenu
import com.convxy.music.ui.player.VideoSurface
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.utils.YouTubePlaybackState
import com.convxy.music.utils.rememberPreference
import com.convxy.music.utils.makeTimeString
import com.convxy.music.viewmodels.YouTubeWatchUiState
import com.convxy.music.viewmodels.YouTubeWatchViewModel
import com.music.innertube.models.WebVideo
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * The native watch screen: a responsive player (the shared MusicService
 * ExoPlayer rendered through [VideoSurface]), metadata, actions, and
 * the related queue below. Leaving the screen never stops playback — the
 * global mini player takes over, and tapping it returns here.
 */
@Composable
fun YouTubeWatchScreen(
    navController: NavController,
    viewModel: YouTubeWatchViewModel = hiltViewModel(),
) {
    val videoId = viewModel.videoId
    val playerConnection = LocalPlayerConnection.current ?: return
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val uiState by viewModel.uiState.collectAsState()
    val historyEntry by viewModel.historyEntry.collectAsState(initial = null)
    val isSaved by database.youTubeDao.isVideoSaved(videoId).collectAsState(initial = false)

    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val playbackState by playerConnection.playbackState.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val queueWindows by playerConnection.queueWindows.collectAsState()
    val currentQueueIndex by playerConnection.currentWindowIndex.collectAsState()
    val currentTimelineIndex by playerConnection.currentMediaItemIndex.collectAsState()
    val playerError by playerConnection.error.collectAsState()
    val isBuffering = playbackState == Player.STATE_BUFFERING

    val startPositionMs = viewModel.startPositionMs

    var isFullscreen by rememberSaveable { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var playbackSpeed by rememberSaveable { mutableFloatStateOf(1f) }
    var audioOnly by rememberSaveable { mutableStateOf(false) }
    // Double-tap seek feedback: +1 = forward 10s, -1 = back 10s, 0 = hidden.
    var seekFeedback by remember { mutableStateOf(0) }
    var videoQualityCap by rememberPreference(VideoQualityCapKey, 1080)

    val player = playerConnection.player
    val watchPage = (uiState as? YouTubeWatchUiState.Ready)?.page
    val displayVideo = watchPage?.video
        ?: bareVideo(videoId, historyEntry)
    val currentVideoTitle = mediaMetadata?.title?.takeIf { it.isNotBlank() } ?: displayVideo.title
    val currentChannelName = mediaMetadata?.artists?.firstOrNull()?.name ?: displayVideo.channelName.orEmpty()

    // ── Start playback (once the watch page resolves) ────────────────────────
    // Waiting for the page (not playing blind) means the queue root carries the
    // real title/thumbnail into the notification and mini player; if the page
    // can't be fetched we still play from the bare video so playback never dies
    // on a metadata outage.
    LaunchedEffect(videoId, uiState) {
        Timber.tag("YouTubeVideo").i("watch start effect: videoId=%s state=%s playerCurrent=%s", videoId, uiState::class.simpleName, playerConnection.mediaMetadata.value?.id)
        val currentId = playerConnection.mediaMetadata.value?.id
        YouTubePlaybackState.begin(videoId)
        if (currentId == videoId) {
            // Already playing this video (queue autoplay or re-entry): take over
            // the stream, re-resolving with video if it was started audio-only.
            Timber.tag("YouTubeVideo").i("already current — reload for video mode")
            if (startPositionMs > 0L) player.seekTo(startPositionMs)
            playerConnection.service.reloadCurrentStreamForVideoMode(videoId)
            return@LaunchedEffect
        }
        val state = uiState
        // The tapped card's own metadata starts playback INSTANTLY (no watch-page
        // wait); the page only refines the UI from here. Deep links and re-entries
        // without a handoff fall back to watch-page/history metadata.
        val pending = YouTubePlaybackState.pendingVideo?.takeIf { it.id == videoId }
        YouTubePlaybackState.pendingVideo = null
        val rootVideo = pending
            ?: (state as? YouTubeWatchUiState.Ready)?.page?.video
            ?: if (state is YouTubeWatchUiState.Error) bareVideo(videoId, historyEntry) else null
        when {
            rootVideo != null -> {
                Timber.tag("YouTubeVideo").i("playQueue(YouTubeVideoQueue) for %s (pending=%b)", videoId, pending != null)
                playerConnection.playQueue(
                    YouTubeVideoQueue(
                        video = rootVideo,
                        autoplayRelated = true,
                        startPositionMs = startPositionMs,
                    )
                )
            }
            // Page in flight with no handoff (deep link) — wait for it so the
            // queue root carries real metadata into the notification/mini player.
            else -> Unit
        }
    }

    // Keep the session marker in step when the queue auto-advances into a
    // related video so the mini player restores the right watch page later.
    LaunchedEffect(mediaMetadata?.id) {
        val id = mediaMetadata?.id
        if (id != null && YouTubePlaybackState.isActive()) {
            YouTubePlaybackState.begin(id)
        }
    }

    // Switch to audio-only on demand (re-resolves the stream). The watch screen
    // itself keeps the video-mode session alive, so toggling audio-only has to
    // drop the flag first for the reload to resolve an audio-only stream — and
    // toggling back re-raises it. The first composition is a no-op: the
    // start-playback effect above has already established the right mode.
    var audioOnlyInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(audioOnly) {
        if (!audioOnlyInitialized) {
            audioOnlyInitialized = true
            return@LaunchedEffect
        }
        val id = mediaMetadata?.id ?: videoId
        if (audioOnly && YouTubePlaybackState.isActive()) {
            YouTubePlaybackState.end()
            playerConnection.service.reloadCurrentStreamForVideoMode(id)
        } else if (!audioOnly && !YouTubePlaybackState.isActive()) {
            YouTubePlaybackState.begin(id)
            playerConnection.service.reloadCurrentStreamForVideoMode(id)
        }
    }

    // ── Fullscreen / screen-on / back handling ──────────────────────────────
    LaunchedEffect(isFullscreen) {
        // Activity-level chrome (floating nav bar, mini player dock) reads this.
        YouTubePlaybackState.isFullscreenActive.value = isFullscreen
        val window = activity?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (isFullscreen) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            val window = activity?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            YouTubePlaybackState.isFullscreenActive.value = false
            // Final progress snapshot so Continue Watching never trails by the poll interval.
            persistProgress(viewModel, player)
            // Playback keeps going in the mini player; just drop the video-mode session.
            YouTubePlaybackState.end()
        }
    }
    BackHandler(enabled = isFullscreen) { isFullscreen = false }

    // Screen stays on while the watch screen is visible.
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    // ── Live position + periodic history persistence ────────────────────────
    var positionMs by remember { mutableLongStateOf(startPositionMs.coerceAtLeast(0L)) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var seekPreviewMs by remember { mutableLongStateOf(-1L) }
    LaunchedEffect(isPlaying, mediaMetadata?.id) {
        while (true) {
            positionMs = player.currentPosition.coerceAtLeast(0L)
            durationMs = player.duration.coerceAtLeast(0L)
            persistProgress(viewModel, player)
            delay(if (isPlaying) 4000L else 1000L)
        }
    }

    // Auto-hide the double-tap seek feedback pill.
    LaunchedEffect(seekFeedback) {
        if (seekFeedback != 0) {
            delay(700)
            seekFeedback = 0
        }
    }

    // Double-tap on the video: right half seeks +10s, left half −10s (YouTube-style).
    val onDoubleTapSeek: (Boolean) -> Unit = { forward ->
        val target = (positionMs + if (forward) 10_000L else -10_000L)
            .coerceIn(0L, durationMs.takeIf { it > 0 } ?: Long.MAX_VALUE)
        playerConnection.seekTo(target)
        seekFeedback = if (forward) 1 else -1
    }

    // Auto-hide the overlay controls while playing.
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(3000)
            controlsVisible = false
        }
    }

    if (isFullscreen) {
        FullscreenPlayer(
            player = player,
            title = currentVideoTitle,
            channelName = currentChannelName,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            positionMs = positionMs,
            durationMs = durationMs,
            seekPreviewMs = seekPreviewMs,
            onSeekPreview = { seekPreviewMs = it },
            onSeek = { playerConnection.seekTo(it); seekPreviewMs = -1L },
            onPlayPause = playerConnection::togglePlayPause,
            onNext = playerConnection::seekToNext,
            onPrevious = playerConnection::seekToPrevious,
            onExitFullscreen = { isFullscreen = false },
            onToggleControls = { controlsVisible = !controlsVisible },
            controlsVisible = controlsVisible,
            onDoubleTapSeek = onDoubleTapSeek,
            isWaitingForStream = playbackState == Player.STATE_IDLE && uiState is YouTubeWatchUiState.Loading,
            playbackSpeed = playbackSpeed,
            onSpeedChange = { speed ->
                playbackSpeed = speed
                runCatching { player.setPlaybackSpeed(speed) }
            },
        )
        return
    }

    // ── Portrait layout ──────────────────────────────────────────────────────
    // The player stays PINNED at the top; only the content below it (metadata,
    // related/queue rows) scrolls. Scrolling the playing video off-screen read
    // as a bug — this is the standard watch-page behaviour.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { controlsVisible = !controlsVisible },
                                onDoubleTap = { offset ->
                                    onDoubleTapSeek(offset.x > size.width / 2f)
                                },
                            )
                        },
                ) {
                    // PlayerView-hosted SurfaceView (the Flow/NewPipe pattern):
                    // PlayerView owns surface lifecycle, aspect ratio and
                    // keep-content-on-reset, which is where hand-rolled video
                    // surfaces die natively on some devices.
                    VideoSurface(
                        player = player,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Waiting for the watch page to resolve before the queue
                    // starts (deep links) — show why nothing is moving.
                    if (!isBuffering && playbackState == Player.STATE_IDLE && uiState is YouTubeWatchUiState.Loading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(42.dp),
                            color = Color.White,
                        )
                    }

                    if (isBuffering) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(42.dp),
                            color = Color.White,
                        )
                    }

                    playerError?.let { error ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    Color.Black.copy(alpha = 0.75f),
                                    RoundedCornerShape(12.dp),
                                )
                                .padding(16.dp),
                        ) {
                            Text(
                                text = friendlyPlaybackError(error),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = "Retry",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .clickable {
                                        playerConnection.player.let { p ->
                                            p.prepare()
                                            p.play()
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                            )
                        }
                    }

                    // Double-tap seek feedback.
                    if (seekFeedback != 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(if (seekFeedback > 0) Alignment.CenterEnd else Alignment.CenterStart)
                                .padding(horizontal = 20.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (seekFeedback > 0) R.drawable.navigate_next else R.drawable.arrow_back
                                ),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = " 10s",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    // Fullscreen entry point: always available, not only while the
                    // transient control overlay is up.
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(34.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .clickable { isFullscreen = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.slow_motion_video),
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    if (controlsVisible) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                        ) {
                            IconButton(
                                onClick = { navController.navigateUp() },
                                modifier = Modifier.align(Alignment.TopStart),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "Back",
                                    tint = Color.White,
                                )
                            }
                            IconButton(
                                onClick = { isFullscreen = true },
                                modifier = Modifier.align(Alignment.TopEnd),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.slow_motion_video),
                                    contentDescription = "Fullscreen",
                                    tint = Color.White,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.Center),
                            ) {
                                IconButton(onClick = playerConnection::seekToPrevious, modifier = Modifier.size(52.dp)) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_widget_skip_previous),
                                        contentDescription = "Previous",
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                                IconButton(onClick = playerConnection::togglePlayPause, modifier = Modifier.size(68.dp)) {
                                    Icon(
                                        painter = painterResource(
                                            if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                                        ),
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(44.dp),
                                    )
                                }
                                IconButton(onClick = playerConnection::seekToNext, modifier = Modifier.size(52.dp)) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_widget_skip_next),
                                        contentDescription = "Next",
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp),
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = makeTimeString(if (seekPreviewMs >= 0) seekPreviewMs else positionMs),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                                Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                                    VideoSeekBar(
                                        positionMs = if (seekPreviewMs >= 0) seekPreviewMs else positionMs,
                                        durationMs = durationMs,
                                        onSeek = { playerConnection.seekTo(it) },
                                        onSeekPreview = { seekPreviewMs = it },
                                    )
                                }
                                Text(
                                    text = if (durationMs > 0) makeTimeString(durationMs) else "LIVE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }

                // Secondary control row.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = makeTimeString(if (seekPreviewMs >= 0) seekPreviewMs else positionMs) +
                            " / " + (if (durationMs > 0) makeTimeString(durationMs) else "LIVE"),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { playerConnection.toggleMute() }) {
                        Icon(
                            painter = painterResource(R.drawable.volume_up),
                            contentDescription = "Mute or unmute",
                        )
                    }
                    IconButton(onClick = { audioOnly = !audioOnly }) {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = if (audioOnly) "Switch to video" else "Audio only",
                            tint = if (audioOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    SpeedButton(
                        speed = playbackSpeed,
                        onSpeedChange = { speed ->
                            playbackSpeed = speed
                            runCatching { player.setPlaybackSpeed(speed) }
                        },
                    )
                    QualityButton(
                        cap = videoQualityCap,
                        onCapChange = { newCap ->
                            videoQualityCap = newCap
                            // Re-resolve the current stream under the new cap
                            // (drops the cached URL/bytes, restarts at position).
                            if (mediaMetadata?.id == videoId) {
                                playerConnection.service.reloadCurrentStreamForVideoMode(videoId)
                            }
                        },
                    )
                }
            }

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
        item(key = "meta") {
            MetadataSection(
                uiState = uiState,
                title = currentVideoTitle,
                channelName = currentChannelName,
                channelId = displayVideo.channelId,
                isSaved = isSaved,
                onToggleSave = { viewModel.toggleSaved(displayVideo, isSaved) },
                onOpenChannel = { channelId ->
                    if (channelId.isNotBlank()) navController.navigate("youtube_channel/$channelId")
                },
                onShare = { shareVideo(context, videoId) },
                onOpenMenu = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuState.show {
                        YouTubeVideoMenu(
                            video = displayVideo,
                            navController = navController,
                            onDismiss = menuState::dismiss,
                        )
                    }
                },
            )
        }

        item(key = "related_title") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Up next",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${(queueWindows.size - currentQueueIndex - 1).coerceAtLeast(0)} related",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // The related videos ARE the rest of the queue, so this list stays in
        // sync with next/previous through the normal queue machinery.
        val upcoming = queueWindows.drop(currentQueueIndex + 1)
        // Index in the key: related-video pages are only deduped within a page,
        // so a video can appear twice across the queue.
        itemsIndexed(upcoming, key = { index, window -> "upcoming_${index}_${window.mediaItem.mediaId}" }) { offset, window ->
            val itemMetadata = window.mediaItem.metadata ?: return@itemsIndexed
            val video = WebVideo(
                id = itemMetadata.id,
                title = itemMetadata.title,
                channelId = itemMetadata.artists.firstOrNull()
                    ?.id?.takeIf { it.startsWith("ytch_") }?.removePrefix("ytch_"),
                channelName = itemMetadata.artists.firstOrNull()?.name,
                thumbnail = itemMetadata.thumbnailUrl,
                durationSeconds = itemMetadata.duration.takeIf { it > 0 },
                viewsText = null,
                publishedText = null,
            )
            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                YouTubeVideoRow(
                    video = video,
                                        onChannelClick = { video.channelId?.let { id -> navController.navigate("youtube_channel/$id") } },
                    onClick = {
                        // Shuffle off ⇒ queue order == timeline order, offset from current.
                        playerConnection.player.seekTo(currentTimelineIndex + offset + 1, 0)
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuState.show {
                            YouTubeVideoMenu(
                                video = video,
                                navController = navController,
                                onDismiss = menuState::dismiss,
                            )
                        }
                    },
                    onOverflowClick = {
                        menuState.show {
                            YouTubeVideoMenu(
                                video = video,
                                navController = navController,
                                onDismiss = menuState::dismiss,
                            )
                        }
                    },
                )
            }
        }

        item(key = "bottom_space") { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun MetadataSection(
    uiState: YouTubeWatchUiState,
    title: String,
    channelName: String,
    channelId: String?,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onOpenChannel: (String) -> Unit,
    onShare: () -> Unit,
    onOpenMenu: () -> Unit,
) {
    val page = (uiState as? YouTubeWatchUiState.Ready)?.page
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        val stats = listOfNotNull(page?.video?.viewsText, page?.video?.publishedText)
        if (stats.isNotEmpty()) {
            Text(
                text = stats.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            ActionChip(
                iconRes = if (isSaved) R.drawable.bookmark_star_library else R.drawable.library_add,
                label = if (isSaved) "Saved" else "Save",
                accent = isSaved,
                onClick = onToggleSave,
            )
            ActionChip(iconRes = R.drawable.share, label = "Share", onClick = onShare)
            ActionChip(iconRes = R.drawable.more_vert, label = "More", onClick = onOpenMenu)
        }

        // Channel row.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .combinedBounceClick(onClick = { channelId?.let(onOpenChannel) })
                .padding(vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = page?.channelAvatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
            ) {
                Text(
                    text = channelName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = page?.likeCountText?.let { likes -> "$likes likes" } ?: "View channel",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        page?.description?.takeIf { it.isNotBlank() }?.let { description ->
            ExpandableText(
                text = description,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun ActionChip(
    iconRes: Int,
    label: String,
    accent: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (accent) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainer
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (accent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (accent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Muxed video quality cap for the watch screen. Auto (1080) picks the best
 * available progressive stream; picking a height re-resolves capped.
 */
@Composable
private fun QualityButton(
    cap: Int,
    onCapChange: (Int) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Text(
                text = when (cap) {
                    720 -> "720p"
                    480 -> "480p"
                    360 -> "360p"
                    else -> "Auto"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
        }
        if (open) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.width(170.dp),
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    listOf(
                        1080 to "Auto (up to 1080p)",
                        720 to "720p",
                        480 to "480p",
                        360 to "360p (data saver)",
                    ).forEach { (value, label) ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (value == cap) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCapChange(value)
                                    open = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

/** Compact speed selector (0.5x–2x). */
@Composable
private fun SpeedButton(
    speed: Float,
    onSpeedChange: (Float) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Text(
                text = "${speed}x",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            )
        }
        if (open) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.width(120.dp),
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { option ->
                        Text(
                            text = "${option}x",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (option == speed) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSpeedChange(option)
                                    open = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Slim seek bar for the video overlay. The drag value is tracked locally so
 * "finished" commits exactly where the thumb is, not where the last recomposed
 * fraction pointed.
 */
@Composable
private fun VideoSeekBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
    onSeekPreview: (Long) -> Unit,
) {
    var draggingValueMs by remember { mutableLongStateOf(-1L) }
    val fraction = when {
        draggingValueMs >= 0 && durationMs > 0 ->
            (draggingValueMs.toFloat() / durationMs).coerceIn(0f, 1f)
        durationMs > 0 -> (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
        else -> 0f
    }
    Slider(
        value = fraction,
        onValueChange = { newFraction ->
            val target = (newFraction * durationMs).toLong()
            draggingValueMs = target
            onSeekPreview(target)
        },
        onValueChangeFinished = {
            if (draggingValueMs >= 0) onSeek(draggingValueMs)
            draggingValueMs = -1L
        },
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
    )
}

/** Landscape immersive player. */
@Composable
private fun FullscreenPlayer(
    player: Player?,
    title: String,
    channelName: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    positionMs: Long,
    durationMs: Long,
    seekPreviewMs: Long,
    onSeekPreview: (Long) -> Unit,
    onSeek: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onExitFullscreen: () -> Unit,
    onToggleControls: () -> Unit,
    controlsVisible: Boolean,
    onDoubleTapSeek: (Boolean) -> Unit,
    isWaitingForStream: Boolean,
    playbackSpeed: Float,
    onSpeedChange: (Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onToggleControls() },
                    onDoubleTap = { offset ->
                        onDoubleTapSeek(offset.x > size.width / 2f)
                    },
                )
            },
    ) {
        VideoSurface(
            player = player,
            modifier = Modifier.fillMaxSize(),
        )

        if (isWaitingForStream && !isBuffering) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Color.White,
            )
        }

        if (isBuffering) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Color.White,
            )
        }

        if (controlsVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    IconButton(onClick = onExitFullscreen) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Exit fullscreen",
                            tint = Color.White,
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = channelName,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    SpeedButtonFullscreen(playbackSpeed, onSpeedChange)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    IconButton(onClick = onPrevious, modifier = Modifier.size(56.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_widget_skip_previous),
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    IconButton(onClick = onPlayPause, modifier = Modifier.size(76.dp)) {
                        Icon(
                            painter = painterResource(
                                if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_widget_skip_next),
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = makeTimeString(if (seekPreviewMs >= 0) seekPreviewMs else positionMs),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        VideoSeekBar(
                            positionMs = if (seekPreviewMs >= 0) seekPreviewMs else positionMs,
                            durationMs = durationMs,
                            onSeek = onSeek,
                            onSeekPreview = onSeekPreview,
                        )
                    }
                    Text(
                        text = if (durationMs > 0) makeTimeString(durationMs) else "LIVE",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedButtonFullscreen(speed: Float, onSpeedChange: (Float) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = !open }) {
            Text(
                text = "${speed}x",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (open) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.width(110.dp),
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { option ->
                        Text(
                            text = "${option}x",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (option == speed) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSpeedChange(option)
                                    open = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun bareVideo(videoId: String, historyEntry: YouTubeWatchHistoryEntity?): WebVideo =
    WebVideo(
        id = videoId,
        title = historyEntry?.title.orEmpty(),
        channelId = historyEntry?.channelId,
        channelName = historyEntry?.channelName,
        thumbnail = historyEntry?.thumbnailUrl ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
        durationSeconds = historyEntry?.durationSeconds?.takeIf { it > 0 },
        viewsText = null,
        publishedText = null,
    )

/** Snapshot writer for history — reads the player once, delegates to the VM. */
private fun persistProgress(viewModel: YouTubeWatchViewModel, player: Player) {
    // The queue can auto-advance to a related video while this screen is still
    // mounted on the old one; never write the new item's position into the old
    // video's history row.
    if (player.currentMediaItem?.mediaId != viewModel.videoId) return
    val duration = player.duration
    val position = player.currentPosition
    if (duration <= 0) {
        viewModel.saveProgress((position / 1000).toInt(), -1, completed = false)
    } else {
        val completed = position >= duration - 5000
        viewModel.saveProgress((position / 1000).toInt(), (duration / 1000).toInt(), completed)
    }
}

/** Maps media3 errors onto short, human-readable messages. */
private fun friendlyPlaybackError(error: androidx.media3.common.PlaybackException): String = when {
    error.errorCode == androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
        error.errorCode == androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
        "No connection. Check your network and retry."
    else -> "Playback failed. Try again, or come back later."
}

private fun shareVideo(context: Context, videoId: String) {
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(
            android.content.Intent.EXTRA_TEXT,
            "https://www.youtube.com/watch?v=$videoId",
        )
    context.startActivity(android.content.Intent.createChooser(intent, null))
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
