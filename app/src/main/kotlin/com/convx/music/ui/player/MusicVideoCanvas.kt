package com.convx.music.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import com.convx.music.utils.YTPlayerUtils
import com.music.innertube.OfficialVideo
import kotlinx.coroutines.delay

/** Let a track settle before resolving a decorative video, and discard old results on skip. */
@Composable
internal fun rememberMusicVideoCanvasStream(
    mediaId: String?,
    video: OfficialVideo?,
    enabled: Boolean,
): YTPlayerUtils.VideoStream? {
    val result by produceState<YTPlayerUtils.VideoStream?>(null, mediaId, video?.videoId, enabled) {
        value = null
        if (!enabled || mediaId == null || video?.songId != mediaId) return@produceState
        delay(700)
        value = YTPlayerUtils.videoStream(video.videoId)?.copy(mediaId = mediaId)
    }
    return result?.takeIf { it.mediaId == mediaId && enabled }
}
