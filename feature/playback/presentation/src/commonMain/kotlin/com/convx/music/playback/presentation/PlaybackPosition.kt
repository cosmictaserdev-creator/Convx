package com.convx.music.playback.presentation

import androidx.compose.runtime.*
import com.convx.music.playback.PlaybackSnapshot
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

/** Each visible player surface owns a frame clock, including a fullscreen window beside a minimized main window. */
@Composable
fun rememberPlaybackPosition(snapshots: StateFlow<PlaybackSnapshot>): LongState {
    val position = remember(snapshots) { mutableLongStateOf(snapshots.value.positionMs) }
    LaunchedEffect(snapshots) {
        snapshots.collectLatest { snapshot ->
            position.longValue = snapshot.positionMs
            if (snapshot.isPlaying) {
                var firstFrame = 0L
                while (true) withFrameNanos { frame ->
                    if (firstFrame == 0L) firstFrame = frame
                    position.longValue = (snapshot.positionMs + (frame - firstFrame) / 1_000_000L)
                        .coerceAtMost(snapshot.durationMs?.takeIf { it > 0 } ?: Long.MAX_VALUE)
                }
            }
        }
    }
    return position
}
