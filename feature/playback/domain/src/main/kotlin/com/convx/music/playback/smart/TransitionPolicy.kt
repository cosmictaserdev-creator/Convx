/*
 * Ported from Orchard (https://github.com/SFG5453/Orchard).
 *
 * Copyright (C) 2026 SFG545 (original Orchard implementation)
 * Copyright (C) 2026 Convx contributors (adapted from an open-source base)
 *
 * Orchard's original source is licensed under the GNU Affero General Public
 * License, version 3 or later. Per AGPLv3 section 13, this file is combined
 * here into the reference app -- a work licensed under the GNU General Public
 * License, version 3 or later -- and remains itself governed by the AGPLv3
 * as part of that combination.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.convx.music.playback.smart

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** Minimum confidence required before tempo matching or beat alignment. */
const val MIN_BEATMATCH_CONFIDENCE = 0.55

/** Minimum confidence required for an adaptive crossfade. */
const val MIN_DJ_CONFIDENCE = 0.2

/** One octave either side of a typical dance tempo; outside this the analysis is noise. */
const val MIN_BPM = 40.0
const val MAX_BPM = 220.0

/** How far a tempo pairing may drift from unity and still be considered transparent to stretch. */
const val MAX_STRETCH_DEVIATION = 0.04

/** Vocal-mask values at or above this threshold count as singing. */
const val VOCAL_ACTIVE_THRESHOLD = 0.6

/** Maximum amount of audible music a transition may skip. */
const val MAX_DISCARDED_MUSIC_SECONDS = 12.0

/**
 * Fraction of a track's own loud-end reference below which a sample counts as
 * silence rather than music, so a genuine gap costs nothing against the budget.
 */
private const val AUDIBLE_ENERGY_FRACTION = 0.1

/** Base ranking by entry type; candidate scores adjust the ranking. */
private val MIX_IN_TYPE_WEIGHT = mapOf(
    "main_drop" to 0.5,
    "intro_drop" to 0.4,
    "pickup" to 0.15,
    "phrase" to 0.1,
)

/**
 * Mirrors the analyzer's own scoring of mix-out candidates, used when an
 * analysis carries the scalar fields but not the candidate list.
 */
private val MIX_OUT_TYPE_SCORE = mapOf(
    "energy_cliff" to 0.95,
    "interior_mix_out" to 0.95,
    "outro_start" to 0.9,
    "content_end" to 0.75,
)

/** Non-finite guards, matching the desktop planner's coercion of `NaN`/`Infinity` to zero. */
fun Double.orZero(): Double = if (isFinite()) this else 0.0

fun Double?.orZero(): Double = if (this != null && isFinite()) this else 0.0

internal fun clamp(value: Double, min: Double, max: Double): Double =
    if (value.isFinite()) max(min, min(max, value)) else min

/**
 * Halves or doubles [incomingBpm] until it is as close as possible to
 * [outgoingBpm], the way a DJ counts a 63 BPM track against a 126 BPM one.
 */
fun alignTempoOctave(outgoingBpm: Double, incomingBpm: Double): Double {
    if (outgoingBpm <= 0 || incomingBpm <= 0) return incomingBpm
    var aligned = incomingBpm
    while (aligned / outgoingBpm > 1.5) aligned /= 2
    while (aligned / outgoingBpm < 0.67) aligned *= 2
    return aligned
}

/**
 * Mean vocal activity over [start]..[end] on a track's own timeline, or null
 * when the analysis carries no usable mask there. The mask is indexed against
 * [TrackAnalysis.energyCurve] times.
 */
fun vocalActivityBetween(analysis: TrackAnalysis, start: Double, end: Double): Double? {
    val mask = analysis.vocalActivityMask
    val curve = analysis.energyCurve
    if (mask.isEmpty() || mask.size != curve.size || end <= start) return null
    var sum = 0.0
    var count = 0
    for (index in mask.indices) {
        val time = curve[index].time
        if (!time.isFinite() || time < start || time > end) continue
        val value = mask[index]
        if (!value.isFinite()) continue
        sum += value
        count += 1
    }
    return if (count > 0) sum / count else null
}

/**
 * Both windows measurably singing at once. Null means "no evidence", which
 * never blocks; absence of a mask is not absence of a vocal, but acting on it
 * would punish every track a fallback analyzer handled.
 */
fun isVocalClash(outgoingActivity: Double?, incomingActivity: Double?): Boolean =
    outgoingActivity != null &&
        incomingActivity != null &&
        outgoingActivity >= VOCAL_ACTIVE_THRESHOLD &&
        incomingActivity >= VOCAL_ACTIVE_THRESHOLD

/**
 * Returns a graded vocal-clash amount for rendering. It uses the quieter side so
 * one vocal over an instrumental is not treated as a clash. Missing masks return 0.
 */
fun vocalOverlapAmount(outgoingActivity: Double?, incomingActivity: Double?): Double {
    if (outgoingActivity == null || incomingActivity == null) return 0.0
    val both = min(outgoingActivity, incomingActivity)
    if (both <= VOCAL_ACTIVE_THRESHOLD) return 0.0
    return ((both - VOCAL_ACTIVE_THRESHOLD) / (1.0 - VOCAL_ACTIVE_THRESHOLD)).coerceIn(0.0, 1.0)
}

/**
 * Returns the fraction of the overlap where both tracks are singing, or null
 * when either track has no usable vocal mask.
 *
 * Samples the outgoing curve and maps each time onto the incoming track using
 * [rate]. Both curves are time-ordered, so the incoming index advances in one
 * pass. Unmeasured mask values remain below [VOCAL_ACTIVE_THRESHOLD].
 */
fun simultaneousVocalFraction(
    outgoing: TrackAnalysis,
    incoming: TrackAnalysis,
    outStart: Double,
    outEnd: Double,
    inStart: Double,
    rate: Double,
): Double? {
    val outMask = outgoing.vocalActivityMask
    val outCurve = outgoing.energyCurve
    val inMask = incoming.vocalActivityMask
    val inCurve = incoming.energyCurve
    if (outMask.isEmpty() || outMask.size != outCurve.size) return null
    if (inMask.isEmpty() || inMask.size != inCurve.size) return null
    if (outEnd <= outStart) return null
    val step = if (rate.isFinite() && rate > 0) rate else 1.0

    var inIndex = 0
    var both = 0
    var total = 0
    for (index in outMask.indices) {
        val time = outCurve[index].time
        if (!time.isFinite() || time < outStart) continue
        if (time > outEnd) break
        total += 1
        if (outMask[index] < VOCAL_ACTIVE_THRESHOLD) continue
        val target = inStart + (time - outStart) * step
        while (inIndex + 1 < inCurve.size && inCurve[inIndex + 1].time <= target) inIndex += 1
        if (inMask[inIndex] >= VOCAL_ACTIVE_THRESHOLD) both += 1
    }
    return if (total > 0) both.toDouble() / total else null
}

/** Tolerates about one mask sample at phrase boundaries before shortening a vocal clash. */
const val VOCAL_CLASH_TOLERANCE = 0.05

/**
 * Seconds of audible music in [start]..[end] on a track's own timeline,
 * judged against the track's own loud-end reference so the measure is
 * independent of how the analyzer scales energy. Returns null when there is
 * no usable curve.
 */
fun audibleSecondsBetween(analysis: TrackAnalysis, start: Double, end: Double): Double? {
    val curve = analysis.energyCurve
    if (curve.size < 2 || end <= start) return null
    val energies = curve.map { it.energy }.filter { it.isFinite() && it >= 0 }.sorted()
    if (energies.isEmpty()) return null
    val reference = energies[floor((energies.size - 1) * 0.85).toInt()].orZero()
    if (reference <= 0) return 0.0
    val threshold = reference * AUDIBLE_ENERGY_FRACTION
    val first = curve.first().time
    val last = curve.last().time
    if (!first.isFinite() || !last.isFinite() || last <= first) return null
    val sampleSeconds = (last - first) / (curve.size - 1)
    var audible = 0.0
    for (point in curve) {
        if (!point.time.isFinite() || point.time < start || point.time > end) continue
        if (point.energy >= threshold) audible += sampleSeconds
    }
    return audible
}

/**
 * The earliest point the analysis claims the track makes sound.
 *
 * [TrackAnalysis.firstBeat] is not nullable the way the other two are, and
 * the analyzer uses 0.0 as its "nothing was measured" fallback. Counting that
 * zero as a real audible start pins this to 0 for any track without a beat
 * grid, which silently overrides a measured `audibleStartTime` and tells the
 * planner the whole head of the track is intro it can fade across.
 */
internal fun audibleStartOf(analysis: TrackAnalysis): Double {
    val firstBeat = analysis.firstBeat.takeIf { it.isFinite() && it > 0 }
    val candidates = listOfNotNull(analysis.audibleStartTime, analysis.pickupTime, firstBeat)
        .filter { it.isFinite() && it >= 0 }
    return candidates.minOrNull() ?: 0.0
}

/** The value in [values] closest to [target] within [tolerance], or null when none qualifies. */
internal fun nearestValue(values: List<Double>, target: Double, tolerance: Double): Double? {
    var nearest: Double? = null
    var nearestDistance = Double.POSITIVE_INFINITY

    for (value in values) {
        if (!value.isFinite()) continue

        val distance = abs(value - target)
        if (distance <= tolerance && (nearest == null || distance < nearestDistance)) {
            nearest = value
            nearestDistance = distance
        }
    }

    return nearest
}

/** Ranks valid mix-in candidates by score, type, beat alignment, intro length, and vocal level. */
fun rankMixInCandidates(analysis: TrackAnalysis): List<RankedMixCandidate> {
    val candidates = analysis.mixInCandidates.filter { it.time.isFinite() && it.time >= 0 }
    if (candidates.isEmpty()) return emptyList()
    val beatSeconds = analysis.beatInterval.orZero()
        .takeIf { it > 0 }
        ?: if (analysis.bpm.orZero() > 0) 60 / analysis.bpm else 0.5
    val audibleStart = audibleStartOf(analysis)
    return candidates.map { candidate ->
        var rankScore = candidate.score.orZero() + (MIX_IN_TYPE_WEIGHT[candidate.type] ?: 0.0)
        if (nearestValue(analysis.downbeats, candidate.time, beatSeconds / 2) != null) rankScore += 0.1
        // A cold open: nothing before the point to play underneath the outgoing track, so entering
        // here means starting the blend on the arrangement.
        if (candidate.time - audibleStart < beatSeconds * 4) rankScore -= 0.2
        // Prefer entries whose run-up is instrumental; an intro that already sings will sing over
        // the outgoing track for the whole pre-roll.
        val vocal = vocalActivityBetween(
            analysis,
            max(audibleStart, candidate.time - beatSeconds * 16),
            candidate.time,
        )
        if (vocal != null) rankScore += (0.5 - vocal) * 0.4
        RankedMixCandidate(
            time = candidate.time,
            score = candidate.score.orZero(),
            type = candidate.type,
            rankScore = rankScore,
        )
    }.sortedByDescending { it.rankScore }
}

/**
 * The phrase start, anywhere in the track, where the energy steps up and stays up.
 * The native analysis only proposes entries from the intro, so without this every
 * track is cued from near its beginning even when a later section blends better.
 */
internal fun sectionEntryCandidate(analysis: TrackAnalysis): MixCandidate? {
    val curve = analysis.energyCurve
    val peak = curve.maxOfOrNull { it.energy }?.takeIf { it > 0 } ?: return null
    val covered = curve.last().time
    val length = analysis.duration.orZero().takeIf { it > 0 } ?: covered
    val earliest = audibleStartOf(analysis) + SECTION_MIN_AFTER_START
    val latest = min(covered - SECTION_WINDOW, length * SECTION_LATEST_FRACTION)

    fun level(from: Double, to: Double): Double? =
        curve.filter { it.time in from..to }.takeIf { it.isNotEmpty() }?.let { window -> window.sumOf { it.energy } / window.size / peak }

    return analysis.phraseBoundaries
        .filter { it.isFinite() && it in earliest..latest }
        .mapNotNull { time ->
            val before = level(time - SECTION_WINDOW, time) ?: return@mapNotNull null
            val after = level(time, time + SECTION_WINDOW) ?: return@mapNotNull null
            val step = after - before
            if (after < SECTION_MIN_LEVEL || step < SECTION_MIN_STEP) return@mapNotNull null
            MixCandidate(time, 0.6 * min(step, 1.0) + 0.4 * after, "section_entry")
        }
        .maxByOrNull { it.score }
}

/** Cues the track from its best section entry when one exists, instead of from its intro. */
internal fun TrackAnalysis.withSectionEntry(): TrackAnalysis {
    val entry = sectionEntryCandidate(this) ?: return this
    return copy(mixInTime = entry.time, mixInCandidates = listOf(entry))
}

private const val SECTION_WINDOW = 8.0
private const val SECTION_MIN_AFTER_START = 10.0
private const val SECTION_LATEST_FRACTION = 0.7
private const val SECTION_MIN_LEVEL = 0.5
private const val SECTION_MIN_STEP = 0.1

/** Falls back to the scalar mix-out fields when the analysis carries no candidate list. */
private fun mixOutCandidatesOf(analysis: TrackAnalysis, contentEnd: Double): List<MixCandidate> {
    val supplied = analysis.mixOutCandidates.filter { it.time.isFinite() && it.time > 0 }
    val candidates = supplied.map {
        MixCandidate(time = it.time, score = it.score.orZero(), type = it.type)
    }.toMutableList()
    if (supplied.isEmpty()) {
        val mixOut = analysis.mixOutTime.orZero()
        val outroStart = analysis.outroStartTime.orZero()
        if (mixOut > 0 && mixOut < contentEnd - 1) {
            candidates += MixCandidate(mixOut, 0.95, "energy_cliff")
        }
        if (outroStart > 0 && outroStart < contentEnd - 1) {
            candidates += MixCandidate(outroStart, 0.9, "outro_start")
        }
    }
    // Vocal activity shapes the overlap, not the exit; content end is always available as a fallback.
    if (candidates.none { abs(it.time - contentEnd) < 0.05 }) {
        candidates += MixCandidate(contentEnd, 0.75, "content_end")
    }
    return candidates
}

/** Resolves the content end from the analysis and the caller's overrides, in priority order. */
private fun resolveContentEnd(analysis: TrackAnalysis, contentEnd: Double, duration: Double): Double =
    contentEnd.orZero().takeIf { it != 0.0 }
        ?: analysis.contentEndTime.orZero().takeIf { it != 0.0 }
        ?: duration.orZero().takeIf { it != 0.0 }
        ?: analysis.duration.orZero()

/** Ranks exit candidates while limiting how much audible music a transition can skip. */
fun rankMixOutCandidates(
    analysis: TrackAnalysis,
    contentEnd: Double = 0.0,
    duration: Double = 0.0,
): List<RankedMixCandidate> {
    val end = resolveContentEnd(analysis, contentEnd, duration)
    if (end <= 0) return emptyList()
    return mixOutCandidatesOf(analysis, end)
        .map { candidate ->
            val measured = audibleSecondsBetween(analysis, candidate.time, end)
            // With no energy curve there is no way to tell skipped music from skipped silence, so
            // the raw gap is charged in full and the budget errs toward playing the track.
            RankedMixCandidate(
                time = candidate.time,
                score = candidate.score,
                type = candidate.type,
                rankScore = candidate.score + (MIX_OUT_TYPE_SCORE[candidate.type] ?: 0.0),
                discardedMusicSeconds = measured ?: max(0.0, end - candidate.time),
                measured = measured != null,
            )
        }
        .filter { it.discardedMusicSeconds <= MAX_DISCARDED_MUSIC_SECONDS }
        .sortedWith(compareByDescending<RankedMixCandidate> { it.rankScore }.thenByDescending { it.time })
}

/**
 * Where the outgoing track's transition ends: the best-ranked mix-out
 * candidate that stays inside the discarded-music budget, or the end of
 * content when none does.
 */
fun resolveMixOutAnchor(
    analysis: TrackAnalysis,
    contentEnd: Double = 0.0,
    duration: Double = 0.0,
): MixOutAnchor {
    val end = resolveContentEnd(analysis, contentEnd, duration)
    val best = rankMixOutCandidates(analysis, end, duration).firstOrNull()
    return MixOutAnchor(
        time = best?.time ?: end,
        type = best?.type ?: "content_end",
        discardedMusicSeconds = best?.discardedMusicSeconds ?: 0.0,
    )
}

/**
 * Decides how ambitious a transition the stored analysis supports.
 *
 * Reasons are ordered most-disqualifying first so callers can surface
 * `reasons.first()` as the routing verdict.
 */
fun assessTransitionTier(
    analysis: TrackAnalysis,
    nextAnalysis: TrackAnalysis,
): TransitionPolicyVerdict {
    val outgoingBpm = analysis.bpm.orZero()
    val incomingBpm = nextAnalysis.bpm.orZero()
    val outgoingConfidence = analysis.beatConfidence.orZero()
    val incomingConfidence = nextAnalysis.beatConfidence.orZero()
    val floorConfidence = min(outgoingConfidence, incomingConfidence)
    val reasons = mutableListOf<String>()

    if (outgoingBpm < MIN_BPM || outgoingBpm > MAX_BPM) reasons += "outgoing-tempo"
    if (incomingBpm < MIN_BPM || incomingBpm > MAX_BPM) reasons += "incoming-tempo"
    if (reasons.isNotEmpty()) {
        return TransitionPolicyVerdict(TransitionTier.PLAIN_CROSSFADE, reasons, floorConfidence)
    }

    if (outgoingConfidence < MIN_DJ_CONFIDENCE && incomingConfidence < MIN_DJ_CONFIDENCE) {
        return TransitionPolicyVerdict(
            TransitionTier.PLAIN_CROSSFADE,
            listOf("beat-confidence"),
            floorConfidence,
        )
    }

    val stretchRatio = outgoingBpm / alignTempoOctave(outgoingBpm, incomingBpm)
    if (abs(stretchRatio - 1) > MAX_STRETCH_DEVIATION) reasons += "tempo-distance"
    if (outgoingConfidence < MIN_BEATMATCH_CONFIDENCE || incomingConfidence < MIN_BEATMATCH_CONFIDENCE) {
        reasons += "beat-confidence"
    }

    return TransitionPolicyVerdict(
        tier = if (reasons.isEmpty()) TransitionTier.BEATMATCHED else TransitionTier.DJ_ASSISTED,
        reasons = reasons,
        beatConfidence = floorConfidence,
    )
}
