/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

import com.convx.music.playback.audio.DjMixTier

enum class TransitionStyle {
    TRANSPARENT,

    REVERB_BLOOM,

    DELAY_BLEND,

    ECHO_FREEZE,

    TAPE_STOP,
}

object TransitionSelector {

    fun select(
        tier: DjMixTier,
        outgoing: TrackAnalysis?,
        incoming: TrackAnalysis?,
        outgoingDurationMs: Long,
        creativeEnabled: Boolean,
        recentStyles: List<TransitionStyle> = emptyList(),
        transitionIndex: Long = recentStyles.size.toLong(),
    ): TransitionStyle {
        if (!creativeEnabled || outgoing == null) return TransitionStyle.TRANSPARENT
        val selected = Sequence[transitionIndex.positiveIndex(Sequence.size)]
        if (selected == TransitionStyle.TAPE_STOP) {
            val confident = outgoing?.grid?.confidence ?: 0f
            val energeticIntro = incoming?.energyProfile?.let { EnergyProfile.introEnergy(it) } ?: 0.5f
            if (tier == DjMixTier.PLAIN_CROSSFADE || confident < 0.45f || energeticIntro < 0.4f) {
                return if (recentStyles.firstOrNull() == TransitionStyle.DELAY_BLEND) {
                    TransitionStyle.REVERB_BLOOM
                } else TransitionStyle.DELAY_BLEND
            }
        }
        return if (selected == recentStyles.firstOrNull()) {
            if (selected == TransitionStyle.REVERB_BLOOM) TransitionStyle.DELAY_BLEND else TransitionStyle.REVERB_BLOOM
        } else selected
    }

    private val Sequence = arrayOf(
        TransitionStyle.REVERB_BLOOM,
        TransitionStyle.DELAY_BLEND,
        TransitionStyle.ECHO_FREEZE,
        TransitionStyle.REVERB_BLOOM,
        TransitionStyle.DELAY_BLEND,
        TransitionStyle.TAPE_STOP,
    )
}

private fun Long.positiveIndex(size: Int): Int {
    val remainder = this % size
    return if (remainder < 0L) (remainder + size).toInt() else remainder.toInt()
}
