package com.convx.music.playback

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Portable sleep-timer state exposed by playback clients. */
data class PlaybackSleepTimerState(
    val triggerTimeMs: Long = -1L,
    val pauseWhenTrackEnds: Boolean = false,
) {
    val isActive: Boolean get() = triggerTimeMs >= 0L || pauseWhenTrackEnds

    companion object {
        val INACTIVE = PlaybackSleepTimerState()
    }
}

/**
 * Owns the sleep-timer policy shared by platform playback adapters.
 * Timekeeping and pausing stay injectable so this remains common code.
 */
class PlaybackSleepTimer(
    private val scope: CoroutineScope,
    private val nowMillis: () -> Long,
    private val pausePlayback: () -> Unit,
) {
    private val lock = SynchronizedObject()
    private val mutableState = MutableStateFlow(PlaybackSleepTimerState.INACTIVE)
    private var timerJob: Job? = null
    private var generation = 0L

    val state: StateFlow<PlaybackSleepTimerState> = mutableState.asStateFlow()

    fun remainingMillis(): Long? = mutableState.value.triggerTimeMs
        .takeIf { it >= 0L }
        ?.let { (it - nowMillis()).coerceAtLeast(0L) }

    /** Use -1 to pause at the next track boundary; non-negative values are minutes. */
    fun start(minutes: Int) {
        require(minutes == -1 || minutes >= 0) { "Sleep timer minutes must be -1 or non-negative." }
        synchronized(lock) {
            generation += 1L
            val timerGeneration = generation
            timerJob?.cancel()
            timerJob = null
            if (minutes == -1) {
                mutableState.value = PlaybackSleepTimerState(pauseWhenTrackEnds = true)
                return
            }

            val durationMs = minutes.toLong() * MILLIS_PER_MINUTE
            mutableState.value = PlaybackSleepTimerState(triggerTimeMs = nowMillis() + durationMs)
            timerJob = scope.launch {
                delay(durationMs)
                val shouldPause = synchronized(lock) {
                    if (generation != timerGeneration) {
                        false
                    } else {
                        timerJob = null
                        mutableState.value = PlaybackSleepTimerState.INACTIVE
                        true
                    }
                }
                if (shouldPause) pausePlayback()
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            generation += 1L
            timerJob?.cancel()
            timerJob = null
            mutableState.value = PlaybackSleepTimerState.INACTIVE
        }
    }

    /** Returns true when a pause-at-track-end timer consumed this transition. */
    fun onTrackEnded(): Boolean {
        val shouldPause = synchronized(lock) {
            if (!mutableState.value.pauseWhenTrackEnds) {
                false
            } else {
                generation += 1L
                timerJob?.cancel()
                timerJob = null
                mutableState.value = PlaybackSleepTimerState.INACTIVE
                true
            }
        }
        if (shouldPause) pausePlayback()
        return shouldPause
    }

    fun release() = clear()

    private companion object {
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
