package com.convx.music.playback

import kotlinx.coroutines.flow.StateFlow

/** Portable read and intent surface implemented by each platform playback adapter. */
interface PlaybackController : PlaybackQueueController {
    val playbackSnapshot: StateFlow<PlaybackSnapshot>

    /** Shared timer policy; adapters expose it when they provide sleep-timer support. */
    val sleepTimer: PlaybackSleepTimer? get() = null

    fun togglePlayPause()

    fun play()

    fun pause()

    fun seekTo(positionMs: Long)

    fun requestSkip(forward: Boolean)

    fun setRepeatMode(mode: PlaybackRepeatMode)

    fun setShuffleEnabled(enabled: Boolean)

    /** Optional queue handoff for platform clients that own their playback queue. */
    fun updateQueue(tracks: List<PlaybackRequestTrack>) = Unit

    /** Appends tracks without replacing the active playback context. */
    fun appendQueue(tracks: List<PlaybackRequestTrack>) = Unit

    /** Replaces the current queue with a playback context and starts its selected item. */
    fun playQueue(tracks: List<PlaybackRequestTrack>, startIndex: Int) {
        updateQueue(tracks)
        playQueueItem(startIndex)
    }

    /** Implemented by platform clients that can resolve and play provider tracks. */
    fun playTrack(track: PlaybackRequestTrack) = Unit

    /** Release any platform media resources when the owning client closes. */
    fun release() = Unit

    fun cycleRepeatMode() {
        setRepeatMode(playbackSnapshot.value.repeatMode.next())
    }

    fun toggleShuffle() {
        setShuffleEnabled(!playbackSnapshot.value.shuffleEnabled)
    }
}
