/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.dj

import com.convxy.music.playback.audio.DjMixTier

/** What a transition actually does, beyond the volume fade. */
enum class TransitionStyle {
    /** Nothing but the beatmatch, tempo lock and bass swap. The listener should
     *  not be able to tell where one track ended. */
    TRANSPARENT,

    /** Cut the outgoing track into a delay line and let the tail ring out. */
    ECHO_FREEZE,

    /** Brake the outgoing track to a standstill, pitch falling with it. */
    TAPE_STOP,
}

/**
 * Picks a transition style per track pair from what was actually measured,
 * rather than cycling through effects on a timer.
 *
 * The governing idea: transparency and showmanship are opposite tools. Two
 * tracks that match get an invisible mix, because that is the better mix. An
 * effect exists to *mask* a seam that would otherwise be exposed — so effects
 * are what the lower tiers do instead of giving up, not decoration sprinkled
 * over the good ones.
 *
 * Pure Kotlin and unit-tested, because this is a decision that will be wrong in
 * ways that are hard to hear and easy to assert.
 */
object TransitionSelector {

    /**
     * [recentStyles] is most-recent-first, and governs variety: over a long
     * queue the same move landing every time sounds as mechanical as no move at
     * all. See [applyVariety] for exactly how far that goes — notably, it never
     * overrides leaving a well-matched pair alone.
     */
    fun select(
        tier: DjMixTier,
        outgoing: TrackAnalysis?,
        incoming: TrackAnalysis?,
        outgoingDurationMs: Long,
        creativeEnabled: Boolean,
        recentStyles: List<TransitionStyle> = emptyList(),
    ): TransitionStyle {
        if (!creativeEnabled) return TransitionStyle.TRANSPARENT

        // An effect needs something measured to justify it. With no usable
        // analysis every score below would be computed from UNKNOWN_ENERGY
        // placeholders, and the "safe fallback" would win on the strength of
        // pure guesswork — cutting a track into a delay line on the basis of
        // nothing is exactly the glitch these effects are supposed to prevent.
        val hasSignal = outgoing != null &&
            (outgoing.grid.confidence > 0f || EnergyProfile.isUsable(outgoing.energyProfile))
        if (!hasSignal) return TransitionStyle.TRANSPARENT

        // An effect on every transition is not what a DJ sounds like; it is what
        // a plugin sounds like. Most mixes are plain, and the occasional flourish
        // only reads as deliberate because the ones around it were not. So a
        // creative move stays locked out until several transparent transitions
        // have gone by.
        val sinceLastEffect = recentStyles.indexOfFirst { it != TransitionStyle.TRANSPARENT }
        if (sinceLastEffect in 0 until EFFECT_COOLDOWN) return TransitionStyle.TRANSPARENT

        val outroEnergy = outgoing.energyProfile
            .takeIf { EnergyProfile.coversOutro(it, outgoingDurationMs) }
            ?.let { EnergyProfile.outroEnergy(it) }
            ?: UNKNOWN_ENERGY
        val introEnergy = incoming?.energyProfile
            ?.takeIf { EnergyProfile.isUsable(it) }
            ?.let { EnergyProfile.introEnergy(it) }
            ?: UNKNOWN_ENERGY

        val scores = mutableMapOf<TransitionStyle, Float>()

        // Transparent is the default and wins outright when the tracks genuinely
        // fit. It is never penalised for repetition — "don't do anything clever"
        // getting boring is not a problem worth solving.
        scores[TransitionStyle.TRANSPARENT] = when (tier) {
            DjMixTier.FULL_DJ -> 1.0f
            DjMixTier.SMART_CROSSFADE -> 0.42f
            DjMixTier.PLAIN_CROSSFADE -> 0.20f
        }

        // A frozen delay loop only reads as a deliberate move over a matched
        // beat grid — FULL_DJ is the only tier with one. Scoring it at
        // SMART_CROSSFADE/PLAIN_CROSSFADE too (it used to) meant it beat
        // TRANSPARENT's deliberately-low score there on every mismatched pair,
        // which is most real-world transitions — heard as the outgoing track
        // stuttering/repeating instead of an intentional effect. DjMixPlanner's
        // own tier doc already says SMART_CROSSFADE carries no EQ/effects
        // camouflage; this was the one place that contract wasn't enforced.
        if (tier == DjMixTier.FULL_DJ) {
            scores[TransitionStyle.ECHO_FREEZE] = 0.58f + (1f - outroEnergy) * 0.25f
        }

        // A brake is an ending, not a blend. It earns its place when the tempos
        // could never have matched anyway — and only if the incoming track
        // arrives with something, or the brake just lands in silence.
        if (tier == DjMixTier.PLAIN_CROSSFADE && introEnergy >= MIN_TAPE_STOP_INTRO_ENERGY) {
            scores[TransitionStyle.TAPE_STOP] = 0.55f + introEnergy * 0.35f
        }

        applyVariety(scores, recentStyles)

        return scores.maxByOrNull { it.value }?.key ?: TransitionStyle.TRANSPARENT
    }

    /**
     * The same creative move never lands twice in a row — a hard rule, not a
     * nudge. A soft penalty does not work here: when a track ends at full energy
     * one effect can outscore everything else by a wide margin, so back-to-back
     * tracks like that would always produce the identical move, which is exactly
     * the mechanical feel the effects exist to avoid.
     *
     * TRANSPARENT is exempt, and so is the case where nothing else is eligible:
     * leaving a matched pair alone must never be overruled in the name of
     * variety, and a forced effect is worse than a repeated one.
     */
    private fun applyVariety(
        scores: MutableMap<TransitionStyle, Float>,
        recentStyles: List<TransitionStyle>,
    ) {
        val previous = recentStyles.firstOrNull()
        if (previous != null && previous != TransitionStyle.TRANSPARENT && scores.size > 1) {
            scores.remove(previous)
        }
        val beforeThat = recentStyles.getOrNull(1)
        if (beforeThat != null && beforeThat != TransitionStyle.TRANSPARENT) {
            scores[beforeThat]?.let { scores[beforeThat] = it - REPEAT_PENALTY_OLDER }
        }
    }

    /** Used when a track has no usable energy profile: assume nothing, so the
     *  decision falls back to the tier and the beat confidence. */
    private const val UNKNOWN_ENERGY = 0.5f

    private const val MIN_TAPE_STOP_INTRO_ENERGY = 0.6f

    private const val REPEAT_PENALTY_OLDER = 0.10f

    /** Transparent transitions that must pass before another effect is allowed.
     *  Effects land roughly once every five transitions. */
    private const val EFFECT_COOLDOWN = 4
}
