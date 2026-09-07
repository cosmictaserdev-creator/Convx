/*
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Tracks a looping video's playback position as a plain (non-snapshot-state)
 * clock, so glass surfaces sampling its backdrop can compute a bucket for
 * `Modifier.liquidGlass`'s `loopBucket` (via `LocalBackdropLoopBucket`)
 * without the draw-phase snapshot-read trap documented at
 * `BackdropFreeze.kt:21-23` and `MainActivity.kt:1260-1264` — a snapshot read
 * during the draw phase registers a draw dependency, and every write then
 * re-invalidates the frame forever.
 *
 * [update] is meant to be called periodically (see `BackgroundVideoView`)
 * rather than on every position change, since the bucket width is coarser
 * than a video frame anyway.
 */
class VideoLoopClock {
    private val state = longArrayOf(0L, 0L) // [0] = position ms, [1] = duration ms

    fun update(positionMs: Long, durationMs: Long) {
        state[0] = positionMs
        state[1] = durationMs
    }

    /**
     * Quantizes the current position into a bucket [bucketMs] wide. Stable
     * across loop repeats: the same position in the loop always maps to the
     * same bucket, which is what makes it safe to cache glass output by.
     */
    fun bucket(bucketMs: Long): Int {
        val duration = state[1]
        if (duration <= 0L) return 0
        val position = state[0].coerceIn(0L, duration)
        return (position / bucketMs).toInt()
    }
}

@Composable
fun rememberVideoLoopClock(): VideoLoopClock = remember { VideoLoopClock() }
