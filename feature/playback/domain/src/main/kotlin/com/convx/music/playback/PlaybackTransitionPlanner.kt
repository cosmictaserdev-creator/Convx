package com.convx.music.playback

import com.convx.music.playback.smart.CrossfadeMode
import com.convx.music.playback.smart.TrackAnalysis
import com.convx.music.playback.smart.TransitionTrackInfo
import com.convx.music.playback.smart.TransitionStyle
import com.convx.music.playback.smart.planTransition
import kotlin.math.roundToLong

/** Platform-neutral timing selected for one outgoing/incoming playback pair. */
data class PlaybackTransitionWindow(
    val startAtMs: Long,
    val endAtMs: Long,
    val durationMs: Long,
    val incomingCuePositionMs: Long,
    val incomingPlaybackRate: Double,
    val style: TransitionStyle,
)

/** Plans the transition window identically for each KMP audio engine. */
fun planPlaybackTransitionWindow(
    settings: PlaybackSettings,
    outgoing: PlaybackRequestTrack,
    incoming: PlaybackRequestTrack,
    currentPositionMs: Long,
    outgoingDurationMs: Long,
    albumSequential: Boolean = false,
    outgoingAnalysis: TrackAnalysis? = null,
    incomingAnalysis: TrackAnalysis? = null,
): PlaybackTransitionWindow? {
    if (settings.transitionMode == PlaybackTransitionMode.OFF) return null

    val durationMs = outgoingDurationMs.takeIf { it > 0L }
        ?: outgoing.durationMs?.takeIf { it > 0L }
        ?: return null
    val durationSeconds = durationMs / MILLIS_PER_SECOND
    val outgoingIdentity = outgoing.analysisIdentity
    val incomingIdentity = incoming.analysisIdentity
    val outgoingInfo = outgoing.toTransitionInfo(outgoingIdentity, durationMs)
    val incomingInfo = incoming.toTransitionInfo(incomingIdentity, incoming.durationMs ?: 0L)
    val plan = planTransition(
        analysis = outgoingAnalysis ?: TrackAnalysis(
            status = ANALYSIS_PENDING,
            trackId = outgoingIdentity,
            duration = durationSeconds,
        ),
        nextAnalysis = incomingAnalysis ?: TrackAnalysis(
            status = ANALYSIS_PENDING,
            trackId = incomingIdentity,
            duration = incoming.durationMs?.takeIf { it > 0L }?.div(MILLIS_PER_SECOND) ?: 0.0,
        ),
        currentTrack = outgoingInfo,
        nextTrack = incomingInfo,
        currentTime = currentPositionMs.coerceAtLeast(0L) / MILLIS_PER_SECOND,
        duration = durationSeconds,
        fadeSeconds = settings.crossfadeDurationMs / MILLIS_PER_SECOND,
        mode = if (settings.transitionMode == PlaybackTransitionMode.AUTO_MIX) {
            CrossfadeMode.SMART
        } else {
            CrossfadeMode.STANDARD
        },
        albumSequential = albumSequential,
    )
    if (plan.blocked || plan.transitionEnd <= plan.transitionStart) return null

    val endAtMs = (plan.transitionEnd * MILLIS_PER_SECOND)
        .roundToLong()
        .coerceIn(0L, durationMs)
    val requestedFadeMs = plan.fadeMs.coerceAtLeast(0L)
    val fadeDurationMs = CrossfadeEnvelope.durationMs(requestedFadeMs, durationMs)
    if (fadeDurationMs <= 0L || endAtMs <= 0L) return null

    return PlaybackTransitionWindow(
        startAtMs = (endAtMs - fadeDurationMs).coerceAtLeast(0L),
        endAtMs = endAtMs,
        durationMs = fadeDurationMs,
        incomingCuePositionMs = (plan.incomingCueTime * MILLIS_PER_SECOND).roundToLong().coerceAtLeast(0L),
        incomingPlaybackRate = plan.incomingPlaybackRate.takeIf { it.isFinite() }?.coerceIn(0.9, 1.1) ?: 1.0,
        style = plan.transitionStyle,
    )
}

private fun PlaybackRequestTrack.toTransitionInfo(identity: String, durationMs: Long) = TransitionTrackInfo(
    id = identity,
    durationMs = durationMs,
    title = title,
    artist = artists.joinToString(),
)

val PlaybackRequestTrack.analysisIdentity: String
    get() = "$providerId:$id"

private const val MILLIS_PER_SECOND = 1_000.0
private const val ANALYSIS_PENDING = "pending"
