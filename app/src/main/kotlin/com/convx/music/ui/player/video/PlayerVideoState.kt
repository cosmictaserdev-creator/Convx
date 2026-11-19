package com.convx.music.ui.player.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.models.MediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.VideoStreamLookup
import com.convx.music.ui.player.CanvasVideoController
import com.convx.music.ui.player.LocalFullscreenVideo
import com.convx.music.ui.player.rememberCanvasVideoController
import com.convx.music.utils.YTPlayerUtils

/** Playback presentation shared by both player styles. */
@Stable
internal class PlayerVideoState(
    val watch: Boolean,
    val canWatch: Boolean,
    val stream: YTPlayerUtils.VideoStream?,
    val unavailable: Boolean,
    val controller: CanvasVideoController,
    val select: (Boolean) -> Unit,
    val requestStream: () -> Unit,
)

@Composable
internal fun rememberPlayerVideo(
    connection: PlayerConnection,
    metadata: MediaMetadata?,
    isPlaying: Boolean,
    sheetCollapsed: Boolean,
    isPreviewOnly: Boolean,
): PlayerVideoState {
    val id = metadata?.id
    val lookup by connection.videoStreamLookup.collectAsStateWithLifecycle()
    val track by connection.canvasTrack.collectAsStateWithLifecycle()
    val available by connection.videoAvailability.collectAsStateWithLifecycle()
    val presentation = LocalFullscreenVideo.current
    var previewWatch by remember(id) { mutableStateOf(false) }
    val standalone = isPreviewOnly || presentation == null
    val watch = if (standalone) previewWatch else id != null && presentation?.selectedVideoId == id
    val select: (Boolean) -> Unit = { selected ->
        if (standalone) {
            previewWatch = selected
        } else {
            presentation?.selectVideo(id, selected)
            id?.let { connection.service.setVideoPlaybackMode(it, selected) }
        }
    }
    LaunchedEffect(id, presentation?.selectedVideoId) {
        val selectedId = presentation?.selectedVideoId
        if (selectedId != null && selectedId != id) presentation.selectVideo(selectedId, false)
    }

    LaunchedEffect(id, sheetCollapsed, isPreviewOnly) {
        if (!isPreviewOnly) connection.requestVideoAvailability(id.takeIf { !sheetCollapsed && !isPreviewOnly })
    }
    DisposableEffect(connection, isPreviewOnly) { onDispose { if (!isPreviewOnly) connection.requestVideoAvailability(null) } }
    LaunchedEffect(watch, track, isPreviewOnly) {
        if (isPreviewOnly) return@LaunchedEffect
        if (watch) id?.let { connection.requestVideoStream(it) } else connection.clearVideoStreamRequest()
    }

    val stream = (lookup as? VideoStreamLookup.Ready)?.takeIf { it.epoch == track.epoch }?.stream?.takeIf { it.mediaId == id }
    val controller = rememberCanvasVideoController(
        connection = connection,
        mediaMetadata = metadata,
        isPlaying = isPlaying,
        visible = !sheetCollapsed && !isPreviewOnly,
        videoStream = stream.takeIf { watch },
        videoStartPositionMs = connection.player.currentPosition,
    )
    return PlayerVideoState(
        watch = watch,
        canWatch = !isPreviewOnly && available != null && available?.songId == id,
        stream = stream,
        unavailable = lookup is VideoStreamLookup.Unavailable,
        controller = controller,
        select = select,
        requestStream = { id?.let { connection.requestVideoStream(it, forceRefresh = true) } },
    )
}
