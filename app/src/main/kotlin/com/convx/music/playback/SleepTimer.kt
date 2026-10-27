/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.convx.music.playback.PlaybackSleepTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SleepTimer(
    private val scope: CoroutineScope,
    var player: Player,
) : Player.Listener {
    private val policy = PlaybackSleepTimer(
        scope = scope,
        nowMillis = System::currentTimeMillis,
        pausePlayback = { player.pause() },
    )
    val sharedPolicy: PlaybackSleepTimer get() = policy

    var triggerTime by mutableLongStateOf(-1L)
        private set
    var pauseWhenSongEnd by mutableStateOf(false)
        private set
    val isActive: Boolean
        get() = policy.state.value.isActive

    init {
        scope.launch {
            policy.state.collect { state ->
                triggerTime = state.triggerTimeMs
                pauseWhenSongEnd = state.pauseWhenTrackEnds
            }
        }
    }

    fun start(minute: Int) {
        policy.start(minute)
    }

    /** Also called when crossfade swaps the session player. */
    fun notifySongTransition() = pauseAtSongEnd()

    fun clear() = policy.clear()

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        pauseAtSongEnd()
    }

    override fun onPlaybackStateChanged(
        @Player.State playbackState: Int,
    ) {
        if (playbackState == Player.STATE_ENDED) pauseAtSongEnd()
    }

    private fun pauseAtSongEnd() {
        policy.onTrackEnded()
    }
}
