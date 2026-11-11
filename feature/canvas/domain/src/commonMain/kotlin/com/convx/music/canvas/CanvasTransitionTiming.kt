package com.convx.music.canvas

/** Shared timing survives a change of rendering surface without restarting a transition. */
data class CanvasTransitionTiming(val startedAtMillis: Long, val durationMillis: Long) {
    fun progress(nowMillis: Long): Float =
        ((nowMillis - startedAtMillis).coerceAtLeast(0).toDouble() / durationMillis.coerceAtLeast(1)).toFloat().coerceIn(0f, 1f)

    fun remainingMillis(nowMillis: Long): Long =
        (durationMillis.coerceAtLeast(1) - (nowMillis - startedAtMillis).coerceAtLeast(0)).coerceAtLeast(0)
}
