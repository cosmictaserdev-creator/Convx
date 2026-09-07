/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.dj

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Tempo *and phase* of a track's beat grid. Phase is what makes beatmatching
 * possible at all — knowing two tracks are both 128 BPM says nothing about
 * where their downbeats fall.
 */
data class BeatGrid(
    val bpm: Float,
    /** Absolute track time of one real detected beat, in ms. The grid is
     *  `anchorBeatMs + n * beatMs` for any integer n, positive or negative.
     *
     *  Deliberately NOT reduced into `[0, beatMs)`: the anchor sits inside the
     *  analysis window, so extrapolating to a transition point later in the
     *  track spans a few dozen beats. Reducing it to position 0 first would
     *  multiply the tempo error by however many beats separate 0 from the
     *  analysis window — at ~0.15% tempo error and 60 beats that is already
     *  half a beat of phase error, i.e. an inverted mix. */
    val anchorBeatMs: Float,
    /** 0..1 normalized autocorrelation at the detected period, gated by how
     *  percussive the material is. A real correlation coefficient, so the
     *  thresholds in [DjMixPlanner] mean something: ~0 is "no periodicity
     *  found", ~1 is "perfectly periodic". */
    val confidence: Float,
) {
    val beatMs: Float get() = if (bpm > 0f) 60_000f / bpm else 0f

    /** First beat of the grid at or after [positionMs]. */
    fun beatAtOrAfter(positionMs: Long): Float {
        val beat = beatMs
        if (beat <= 0f) return positionMs.toFloat()
        val beatsAway = Math.ceil((positionMs - anchorBeatMs) / beat.toDouble())
        return anchorBeatMs + (beatsAway * beat).toFloat()
    }

    /**
     * Phrase boundary nearest [positionMs]. Popular music is built in 8- and
     * 16-bar phrases, and starting a mix mid-phrase is the single most audible
     * "a machine did this" tell — more so than being a few ms off the beat.
     *
     * The grid has no downbeat detection, so phrase boundaries are counted from
     * [anchorBeatMs] and are only guaranteed to be *a* consistent 16-beat
     * lattice, not necessarily bar 1 of the phrase the producer wrote. Landing
     * consistently every 16 beats is what matters here; both sides of the mix
     * use the same lattice.
     */
    fun nearestPhraseMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = Math.round((positionMs - anchorBeatMs) / phrase.toDouble())
        return anchorBeatMs + phrasesAway * phrase
    }

    /** Last phrase boundary at or before [positionMs]. */
    fun phraseAtOrBeforeMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = Math.floor((positionMs - anchorBeatMs) / phrase.toDouble())
        return anchorBeatMs + (phrasesAway * phrase).toFloat()
    }

    /** First phrase boundary at or after [positionMs]. */
    fun phraseAtOrAfterMs(positionMs: Long, beatsPerPhrase: Int = BEATS_PER_PHRASE): Float {
        val phrase = beatMs * beatsPerPhrase
        if (phrase <= 0f) return positionMs.toFloat()
        val phrasesAway = Math.ceil((positionMs - anchorBeatMs) / phrase.toDouble())
        return anchorBeatMs + (phrasesAway * phrase).toFloat()
    }

    companion object {
        const val BEATS_PER_PHRASE = 16
        val NONE = BeatGrid(0f, 0f, 0f)
    }
}

/**
 * Beat tracker: onset-strength envelope -> autocorrelation tempo -> phase.
 *
 * Pure Kotlin on purpose — no media3, no ByteBuffer, no Android. All the DSP
 * that can actually be wrong lives here so it can be unit-tested on the JVM
 * (see BeatTrackerTest); [TrackAnalyzerAudioProcessor] is only the plumbing
 * that unpacks PCM and feeds this.
 *
 * Replaces the previous pairwise inter-onset-interval histogram, whose
 * confidence measure (winning bin's share of all pairwise votes) topped out
 * around 0.5 even on a perfect metronome and so could never clear the
 * FULL_DJ threshold.
 */
class BeatTracker(private val sampleRate: Int) {

    private val hopFrames = max(1, sampleRate / 100) // ~10 ms
    private val hopMs = hopFrames * 1000f / sampleRate

    // ponytail: one-pole low-pass for the kick band, not a biquad. BiquadFilter
    // in eq/audio only implements PK/LSC/HSC (no low-pass) and would drag the
    // eq.data types into this otherwise dependency-free class. A 6 dB/oct
    // rolloff is plenty to bias the envelope toward the kick. Upgrade to a
    // proper 2nd-order low-pass if band bleed ever misleads the tempo pick.
    private val lowPassAlpha: Double = run {
        val rc = 1.0 / (2.0 * PI * KICK_BAND_HZ)
        val dt = 1.0 / sampleRate
        dt / (rc + dt)
    }
    private var lowPassState = 0.0

    private var fullAccumulator = 0.0
    private var lowAccumulator = 0.0
    private var framesInHop = 0
    private var previousFullRms = 0.0
    private var previousLowRms = 0.0

    // Raw per-hop RMS, not flux. Onset flux is derived at estimate() time so
    // the RMS series can be smoothed with a *centered* window first — see
    // combinedEnvelope().
    private val fullRms = FloatArray(MAX_ENVELOPE_FRAMES)
    private val lowRms = FloatArray(MAX_ENVELOPE_FRAMES)
    private var envelopeSize = 0

    /** Track position the first envelope frame corresponds to. */
    private var startPositionMs = 0L

    // Coarse energy curve, one entry per second, accumulated for as long as
    // audio keeps arriving — unlike the beat envelope above, which is capped.
    // Beat detection only needs a window; "does this track end at full energy
    // or decay away" needs the whole thing, and the cheapest place to get that
    // is the audio the user is already playing.
    private val secondEnergy = FloatArray(MAX_PROFILE_SECONDS)
    private var secondAccumulator = 0.0
    private var hopsInSecond = 0
    private var profileSize = 0

    /**
     * True once the beat envelope is full, which is both when the analysis is
     * worth running and when its arrays stop being written to.
     *
     * Deliberately a frame count, not a duration comparison: `hopFrames` is an
     * integer division of the sample rate, so at any rate not divisible by 100
     * (22050, say) the derived millisecond total caps *below* the nominal
     * window and a `>= windowMs` test never becomes true — analysis would
     * silently never complete.
     */
    val isAnalysisComplete: Boolean get() = envelopeSize >= MAX_ENVELOPE_FRAMES

    /** Track-time duration accepted so far. Unaffected by playback speed —
     *  this counts audio frames, so a probe player running at 6x still needs
     *  the same amount of *track* to analyze, in a sixth of the wall clock. */
    val analyzedMs: Long get() = (envelopeSize * hopMs).toLong()

    /** Track position at which the next accepted sample sits. Call before the
     *  first [accept] of a fresh analysis run. */
    fun beginAt(positionMs: Long) {
        reset()
        startPositionMs = positionMs
    }

    /** One mono frame, normalized to roughly [-1, 1]. */
    fun accept(sample: Float) {
        // Keep consuming past the beat-envelope cap: the energy profile runs
        // for the whole track even though tempo analysis is done after 45 s.
        if (profileSize >= MAX_PROFILE_SECONDS && envelopeSize >= MAX_ENVELOPE_FRAMES) return

        val value = sample.toDouble()
        lowPassState += lowPassAlpha * (value - lowPassState)

        fullAccumulator += value * value
        lowAccumulator += lowPassState * lowPassState
        framesInHop++

        if (framesInHop < hopFrames) return

        val hopFullRms = Math.sqrt(fullAccumulator / framesInHop).toFloat()
        if (envelopeSize < MAX_ENVELOPE_FRAMES) {
            fullRms[envelopeSize] = hopFullRms
            lowRms[envelopeSize] = Math.sqrt(lowAccumulator / framesInHop).toFloat()
            envelopeSize++
        }

        secondAccumulator += hopFullRms
        hopsInSecond++
        if (hopsInSecond >= HOPS_PER_SECOND && profileSize < MAX_PROFILE_SECONDS) {
            secondEnergy[profileSize++] = (secondAccumulator / hopsInSecond).toFloat()
            secondAccumulator = 0.0
            hopsInSecond = 0
        }

        fullAccumulator = 0.0
        lowAccumulator = 0.0
        framesInHop = 0
    }

    /**
     * Energy curve as one unsigned byte per second, scaled so the track's
     * loudest second is 255. Relative shape is all the transition selector
     * needs — whether the track ends at full energy or decays, whether it opens
     * on drums or on a long quiet intro — so absolute level is thrown away and
     * the whole curve fits in a few hundred bytes.
     *
     * Only meaningful when analysis started at track position 0, which is why
     * [startPositionMs] is checked: a profile that silently began 90 s in would
     * describe the wrong part of the song.
     */
    fun energyProfile(): ByteArray {
        if (profileSize == 0 || startPositionMs != 0L) return ByteArray(0)

        var peak = 0f
        for (i in 0 until profileSize) if (secondEnergy[i] > peak) peak = secondEnergy[i]
        if (peak <= 0f) return ByteArray(0)

        return ByteArray(profileSize) { i ->
            ((secondEnergy[i] / peak) * 255f).roundToInt().coerceIn(0, 255).toByte()
        }
    }

    fun estimate(): BeatGrid {
        if (envelopeSize < MIN_ENVELOPE_FRAMES) return BeatGrid.NONE

        val envelope = combinedEnvelope()
        val n = envelope.size
        val maxLag = min(MAX_LAG_FRAMES, n / 2)
        if (maxLag <= MIN_LAG_FRAMES) return BeatGrid.NONE

        val mean = envelope.average()
        var zeroLagEnergy = 0.0
        for (value in envelope) {
            val centered = value - mean
            zeroLagEnergy += centered * centered
        }
        zeroLagEnergy /= n
        if (zeroLagEnergy <= 1e-12) return BeatGrid.NONE

        // Normalized autocorrelation, biased estimator (divide by n, not by the
        // overlap count) so the result is a true correlation coefficient capped
        // at 1 — otherwise confidence saturates and stops discriminating.
        val correlation = DoubleArray(maxLag + 1)
        for (lag in MIN_LAG_FRAMES..maxLag) {
            var sum = 0.0
            for (i in 0 until n - lag) {
                sum += (envelope[i] - mean) * (envelope[i + lag] - mean)
            }
            correlation[lag] = sum / n / zeroLagEnergy
        }

        // Peak picking runs on an *enhanced* score: adding the correlation at
        // 2x and 4x the lag rewards a period that also has structure at its
        // multiples, which is what separates the true beat from its half-time
        // reading. The tempo preference weight then resolves what's left the
        // way listeners hear it (a log-normal centered near 120 BPM).
        var bestLag = -1
        var bestScore = Double.NEGATIVE_INFINITY
        for (lag in MIN_LAG_FRAMES..maxLag) {
            var score = correlation[lag]
            if (2 * lag <= maxLag) score += 0.5 * correlation[2 * lag]
            if (4 * lag <= maxLag) score += 0.25 * correlation[4 * lag]
            score *= tempoPreference(lagToBpm(lag.toDouble()))
            if (score > bestScore) {
                bestScore = score
                bestLag = lag
            }
        }
        if (bestLag < 0) return BeatGrid.NONE

        // Sub-frame refinement. At a 10 ms hop, integer lags near 128 BPM are
        // ~2.7 BPM apart — coarse enough that a mix would audibly drift over a
        // few seconds. Parabolic interpolation on the peak fixes that for free.
        val refinedLag = refinePeak(correlation, bestLag, maxLag)

        var coarseBpm = lagToBpm(refinedLag)
        // Safety net below the preference weight: never report a tempo outside
        // the range a mix could actually use. Folded before the fine search so
        // the refined period and phase describe the tempo we actually report.
        while (coarseBpm > 0.0 && coarseBpm < MIN_REPORTED_BPM) coarseBpm *= 2.0
        while (coarseBpm > MAX_REPORTED_BPM) coarseBpm /= 2.0
        if (coarseBpm <= 0.0 || !coarseBpm.isFinite()) return BeatGrid.NONE

        // Joint tempo + phase refinement. The ACF peak, even interpolated, is
        // still ~0.15% off, and phase is found by combing the envelope at the
        // beat period — 0.15% compounds to several frames of drift across a
        // 40 s window, so the comb walks off the beat and the anchor lands
        // between beats. Searching a narrow tempo band jointly with phase
        // optimizes exactly the thing that matters (do the beats line up?)
        // instead of approximating it.
        val (period, offset) = refineTempoAndPhase(envelope, 60_000.0 / coarseBpm / hopMs)
        val bpm = (60_000.0 / (period * hopMs)).toFloat()
        if (bpm <= 0f || !bpm.isFinite()) return BeatGrid.NONE

        // Periodicity AND transients. Either alone is a false positive
        // generator: see transientDensity().
        val density = transientDensity()
        val rhythmic = ((density - MIN_TRANSIENT_DENSITY) /
            (FULL_TRANSIENT_DENSITY - MIN_TRANSIENT_DENSITY)).coerceIn(0.0, 1.0)

        return BeatGrid(
            bpm = bpm,
            anchorBeatMs = startPositionMs + offset * hopMs,
            confidence = (correlation[bestLag].coerceIn(0.0, 1.0) * rhythmic).toFloat(),
        )
    }

    fun reset() {
        lowPassState = 0.0
        fullAccumulator = 0.0
        lowAccumulator = 0.0
        framesInHop = 0
        previousFullRms = 0.0
        previousLowRms = 0.0
        envelopeSize = 0
        startPositionMs = 0L
        secondAccumulator = 0.0
        hopsInSecond = 0
        profileSize = 0
    }

    /** Weighted mix of the kick band and the full band, each normalized to unit
     *  mean first so a bass-light track still contributes a usable envelope
     *  instead of being drowned out by whichever band happens to be louder. */
    private fun combinedEnvelope(): FloatArray {
        val n = envelopeSize
        val lowOnsets = onsetStrength(lowRms, n)
        val fullOnsets = onsetStrength(fullRms, n)

        var lowMean = 0.0
        var fullMean = 0.0
        for (i in 0 until n) {
            lowMean += lowOnsets[i]
            fullMean += fullOnsets[i]
        }
        lowMean /= n
        fullMean /= n

        val lowWeight = if (lowMean > 1e-9) LOW_BAND_WEIGHT / lowMean else 0.0
        val fullWeight = if (fullMean > 1e-9) FULL_BAND_WEIGHT / fullMean else 0.0
        if (lowWeight == 0.0 && fullWeight == 0.0) return FloatArray(0)

        return FloatArray(n) { i -> (lowOnsets[i] * lowWeight + fullOnsets[i] * fullWeight).toFloat() }
    }

    /**
     * Onset strength = rising energy only, half-wave rectified. That is what
     * marks note and drum attacks; the old "RMS above a rolling average" test
     * also fired on any sustained loud passage.
     */
    private fun onsetStrength(rms: FloatArray, n: Int): FloatArray {
        val onsets = FloatArray(n)
        for (i in 1 until n) {
            onsets[i] = max(0f, rms[i] - rms[i - 1])
        }
        return onsets
    }

    /**
     * Fraction of frames carrying a real transient: a jump in level of at least
     * [TRANSIENT_JUMP] times the track's mean level within one ~10 ms frame.
     *
     * This is the gate that separates "periodic" from "rhythmic", and it is not
     * optional. Normalized autocorrelation is scale-invariant, so it cannot see
     * modulation *depth* — a held detuned chord whose partials beat against each
     * other produces a perfectly periodic envelope with no attacks anywhere and
     * scored 0.91 confidence, enough to hand ambient and classical the full DJ
     * treatment. Percussive material scores 0.03-0.11 here; sustained tonal
     * material and white noise both score 0.
     */
    private fun transientDensity(): Double {
        val n = envelopeSize

        // Smoothed *only here*. A per-hop RMS is a 100 Hz sampling of the
        // amplitude envelope with no anti-aliasing, so modulation above 50 Hz —
        // partials beating against each other, which any sustained chord
        // produces — folds down and masquerades as frame-to-frame transients.
        // A centered average kills that. It is deliberately not applied to the
        // onset envelope used for tempo and phase, where it smears attacks and
        // costs ~20 ms of anchor accuracy.
        val level = FloatArray(n)
        for (i in 0 until n) {
            var sum = 0.0
            var count = 0
            for (k in -SMOOTH_RADIUS..SMOOTH_RADIUS) {
                val j = i + k
                if (j in 0 until n) {
                    sum += fullRms[j]
                    count++
                }
            }
            level[i] = (sum / count).toFloat()
        }

        var meanLevel = 0.0
        for (value in level) meanLevel += value
        meanLevel /= n
        if (meanLevel <= 1e-9) return 0.0

        var hits = 0
        for (i in 1 until n) {
            if ((level[i] - level[i - 1]) / meanLevel > TRANSIENT_JUMP) hits++
        }
        return hits.toDouble() / n
    }

    /** Grid search over a narrow band around [coarsePeriodFrames] for the
     *  (period, phase) pair whose comb collects the most onset energy per hit.
     *  Returns both, in envelope frames. */
    private fun refineTempoAndPhase(envelope: FloatArray, coarsePeriodFrames: Double): Pair<Double, Int> {
        var bestPeriod = coarsePeriodFrames
        var bestOffset = 0
        var bestScore = -1.0

        for (step in -REFINE_STEPS..REFINE_STEPS) {
            val period = coarsePeriodFrames * (1.0 + step * REFINE_STEP_FRACTION)
            if (period < 1.0) continue
            val offsetRange = max(1, period.roundToInt())
            for (offset in 0 until offsetRange) {
                var energy = 0.0
                var hits = 0
                var position = offset.toDouble()
                while (position < envelope.size) {
                    energy += envelope[position.roundToInt().coerceAtMost(envelope.size - 1)]
                    hits++
                    position += period
                }
                if (hits == 0) continue
                val score = energy / hits
                if (score > bestScore) {
                    bestScore = score
                    bestPeriod = period
                    bestOffset = offset
                }
            }
        }
        return bestPeriod to bestOffset
    }

    private fun refinePeak(correlation: DoubleArray, lag: Int, maxLag: Int): Double {
        if (lag <= MIN_LAG_FRAMES || lag >= maxLag) return lag.toDouble()
        val left = correlation[lag - 1]
        val center = correlation[lag]
        val right = correlation[lag + 1]
        val denominator = left - 2.0 * center + right
        if (abs(denominator) < 1e-12) return lag.toDouble()
        val delta = 0.5 * (left - right) / denominator
        return if (abs(delta) > 1.0) lag.toDouble() else lag + delta
    }

    private fun lagToBpm(lag: Double): Double = 60_000.0 / (lag * hopMs)

    /** Log-normal preference around [PREFERRED_BPM]: how listeners disambiguate
     *  the octave when 85 and 170 are equally periodic. Deliberately wide, so a
     *  genuine 170 BPM track isn't dragged down to half time. */
    private fun tempoPreference(bpm: Double): Double {
        if (bpm <= 0.0) return 0.0
        val octaves = ln(bpm / PREFERRED_BPM) / LN_2
        return exp(-0.5 * (octaves / TEMPO_PREFERENCE_OCTAVES) * (octaves / TEMPO_PREFERENCE_OCTAVES))
    }

    companion object {
        private const val KICK_BAND_HZ = 200.0
        private const val LOW_BAND_WEIGHT = 0.65
        private const val FULL_BAND_WEIGHT = 0.35

        /** Centered moving-average radius, in ~10 ms frames, applied to the RMS
         *  series before transient counting only. */
        private const val SMOOTH_RADIUS = 2

        /** A frame counts as a transient when the level jumps by this much of
         *  the track's mean level in one ~10 ms frame. */
        private const val TRANSIENT_JUMP = 0.5

        /** Transient density below which material is treated as having no
         *  rhythm at all, and above which it counts as fully percussive.
         *  Calibrated against the fixtures in BeatTrackerTest: percussive
         *  material lands at 0.03-0.11, tonal and noise material at 0. */
        private const val MIN_TRANSIENT_DENSITY = 0.004
        private const val FULL_TRANSIENT_DENSITY = 0.015

        /** 10 ms hop, so these are lags of 0.3 s .. 1.0 s = 200 .. 60 BPM. */
        private const val MIN_LAG_FRAMES = 30
        private const val MAX_LAG_FRAMES = 100

        /**
         * 10 s of audio before an estimate is worth anything, 25 s cap.
         *
         * 25 rather than 45: nothing is reported until the cap is reached, so
         * the cap is also how long a track must play before it has a grid at
         * all, and how much audio the 6x probe has to pull down before a
         * transition. 25 s still gives the autocorrelation ~2500 envelope
         * frames, far more than it needs.
         */
        private const val MIN_ENVELOPE_FRAMES = 1000
        private const val MAX_ENVELOPE_FRAMES = 2500

        private const val HOPS_PER_SECOND = 100

        /** 15 minutes of energy profile, one byte per second when serialized. */
        private const val MAX_PROFILE_SECONDS = 900

        private const val MIN_REPORTED_BPM = 70.0
        private const val MAX_REPORTED_BPM = 190.0

        /** Fine search: +/- 2 % of the coarse tempo in 0.05 % steps. Wide
         *  enough to cover the ACF peak's interpolation error, fine enough
         *  that residual drift over a transition stays inside a few ms. */
        private const val REFINE_STEPS = 40
        private const val REFINE_STEP_FRACTION = 0.0005

        private const val PREFERRED_BPM = 120.0
        private const val TEMPO_PREFERENCE_OCTAVES = 0.9
        private val LN_2 = ln(2.0)
    }
}
