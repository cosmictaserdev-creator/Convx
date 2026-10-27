package com.convx.music.playback

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Equal-power gains and duration rules shared by platform audio engines. */
object CrossfadeEnvelope {
    fun incomingGain(progress: Float): Float = sin(safeProgress(progress) * PI.toFloat() / 2f)

    fun outgoingGain(progress: Float): Float = cos(safeProgress(progress) * PI.toFloat() / 2f)

    fun progress(elapsedMs: Long, durationMs: Long): Float {
        if (durationMs <= 0L) return 1f
        return (elapsedMs.coerceAtLeast(0L).toFloat() / durationMs).coerceIn(0f, 1f)
    }

    fun durationMs(configuredMs: Long, outgoingDurationMs: Long): Long {
        val configured = configuredMs.coerceAtLeast(0L)
        if (outgoingDurationMs <= 0L) return configured
        return minOf(configured, outgoingDurationMs / 3L)
    }

    private fun safeProgress(progress: Float): Float =
        if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)
}
