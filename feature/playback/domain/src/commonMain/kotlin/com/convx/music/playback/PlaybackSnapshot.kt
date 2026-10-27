package com.convx.music.playback

import kotlinx.serialization.Serializable

/** Portable identity for the track currently exposed by a playback adapter. */
@Serializable
data class PlaybackTrackSnapshot(
    val id: String,
    val title: String,
    val artists: List<String>,
    val providerId: String = "youtube_music",
    val artworkUrl: String? = null,
)

@Serializable
enum class PlaybackQueueOrigin {
    USER,
    AUTO_MIX,
}

/** Track metadata required to start playback on a platform-specific audio engine. */
@Serializable
data class PlaybackRequestTrack(
    val providerId: String,
    val id: String,
    val title: String,
    val artists: List<String>,
    val durationMs: Long? = null,
    val artworkUrl: String? = null,
    val queueOrigin: PlaybackQueueOrigin = PlaybackQueueOrigin.USER,
)

@Serializable
enum class PlaybackPhase {
    IDLE,
    BUFFERING,
    READY,
    ENDED,
}

@Serializable
enum class PlaybackRepeatMode {
    OFF,
    ONE,
    ALL,
}

fun PlaybackRepeatMode.next(): PlaybackRepeatMode = when (this) {
    PlaybackRepeatMode.OFF -> PlaybackRepeatMode.ALL
    PlaybackRepeatMode.ALL -> PlaybackRepeatMode.ONE
    PlaybackRepeatMode.ONE -> PlaybackRepeatMode.OFF
}

/** Read-only playback state without Media3, Android, or database types. */
data class PlaybackSnapshot(
    val track: PlaybackTrackSnapshot?,
    val phase: PlaybackPhase,
    val playWhenReady: Boolean,
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long?,
    val mediaItemIndex: Int,
    val mediaItemCount: Int,
    val repeatMode: PlaybackRepeatMode,
    val shuffleEnabled: Boolean,
    val canSkipPrevious: Boolean,
    val canSkipNext: Boolean,
    val errorMessage: String? = null,
    val audioOutput: PlaybackAudioOutput? = null,
) {
    companion object {
        val EMPTY = PlaybackSnapshot(
            track = null,
            phase = PlaybackPhase.IDLE,
            playWhenReady = false,
            isPlaying = false,
            positionMs = 0L,
            durationMs = null,
            mediaItemIndex = -1,
            mediaItemCount = 0,
            repeatMode = PlaybackRepeatMode.OFF,
            shuffleEnabled = false,
            canSkipPrevious = false,
            canSkipNext = false,
        )
    }
}

/** Uses playback intent while buffering, when the engine is not making sound yet. */
fun PlaybackSnapshot.shouldPauseOnToggle(): Boolean =
    if (phase == PlaybackPhase.BUFFERING) playWhenReady else isPlaying
