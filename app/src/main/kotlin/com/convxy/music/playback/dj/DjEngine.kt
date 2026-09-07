/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.dj

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.convxy.music.db.MusicDatabase
import com.convxy.music.db.entities.SongAnalysisEntity
import com.convxy.music.playback.audio.DelayAudioProcessor
import com.convxy.music.playback.audio.DjFilterAudioProcessor
import com.convxy.music.playback.audio.DjTailAudioProcessor
import com.convxy.music.playback.audio.DjMixPlan
import com.convxy.music.playback.audio.DjMixPlanner
import com.convxy.music.playback.audio.DjMixTier
import com.convxy.music.playback.audio.TrackAnalyzerAudioProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/** Everything known about one track for mixing purposes. */
data class TrackAnalysis(
    val grid: BeatGrid,
    val key: MusicalKey? = null,
    /** Per-second energy curve; may be empty or cover only the start of the
     *  track when it came from a probe rather than a full play-through. */
    val energyProfile: ByteArray = ByteArray(0),
) {
    // ByteArray makes the generated equals/hashCode identity-based. Nothing
    // compares these; they are looked up by media id.
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}

/**
 * What the player surfaces about DJ mixing. Deliberately reports the tier and
 * style that actually fired rather than what was hoped for, so a fallback is
 * visible instead of leaving the listener wondering why a transition sounded
 * plain.
 */
data class DjState(
    val enabled: Boolean = false,
    val currentBpm: Float = 0f,
    val currentKey: String? = null,
    val nextBpm: Float = 0f,
    val nextKey: String? = null,
    val tier: DjMixTier? = null,
    val style: TransitionStyle? = null,
)

/**
 * Owns everything the DJ mode knows and decides: per-track analysis, its
 * persistence, the pre-analysis probe player, and what a given transition
 * should do.
 *
 * Deliberately does NOT own the players or the crossfade mechanics — MusicService
 * keeps those, because swapping the active player, the MediaSession and the
 * listener set is service lifecycle work, not DJ work. The split is: this class
 * answers *when* to transition and *how*, MusicService carries it out.
 */
@UnstableApi
class DjEngine(
    private val database: MusicDatabase,
    /** Creates a throwaway, non-published player for silent pre-analysis. */
    private val createProbePlayer: () -> ExoPlayer,
) {

    // Own scope rather than MusicService's: that one is a `var` that gets
    // replaced when the service restarts (MusicService.kt:1427), so a captured
    // reference would quietly become a cancelled scope and every persist and
    // probe would stop firing with no error.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /** Mirrors AutoDjMixingEnabledKey, kept fresh by MusicService's collector so
     *  the crossfade path never has to block on a DataStore read. */
    @Volatile
    var enabled: Boolean = false

    /** Mirrors CreativeTransitionsEnabledKey. Off by default: Auto-DJ on its own
     *  means beatmatching and a transparent mix, which is what most listeners
     *  actually want.
     *
     *  Subordinate to [enabled]: creative transitions are a DJ-mode feature, so with
     *  Auto-DJ off they must not fire even if the user left this preference on. The
     *  two prefs are independent in DataStore, and reading them independently let
     *  filter sweeps and tail effects run on every PLAIN crossfade with DJ mode off. */
    @Volatile
    private var creativePref: Boolean = false

    var creativeEnabled: Boolean
        get() = creativePref && enabled
        set(value) { creativePref = value }

    private val analyses = HashMap<String, TrackAnalysis>()
    private val analyzers = HashMap<Player, TrackAnalyzerAudioProcessor>()
    private val filters = HashMap<Player, DjFilterAudioProcessor>()
    private val tails = HashMap<Player, DjTailAudioProcessor>()
    /** Which track each player's analyzer is currently accumulating for.
     *  Per-player, not a single field on the service: the crossfade swaps which
     *  ExoPlayer object is "the" player, so one shared "previous media id"
     *  would end up filing the new player's energy profile under the old
     *  track. */
    private val analyzingMediaId = HashMap<Player, String>()
    private val delays = HashMap<Player, DelayAudioProcessor>()

    /** Most-recent-first, so the selector can avoid repeating itself. */
    private val recentStyles = ArrayDeque<TransitionStyle>()
    private var tailJob: Job? = null

    /** Style chosen for the transition in progress. */
    var activeStyle: TransitionStyle = TransitionStyle.TRANSPARENT
        private set

    private val _state = MutableStateFlow(DjState())
    val state: StateFlow<DjState> = _state.asStateFlow()

    private var probeJob: Job? = null
    private var probePlayer: ExoPlayer? = null

    /** Plan for the transition in progress, so MusicService knows whether it
     *  has speed to unwind when the fade completes. */
    var activePlan: DjMixPlan? = null
        private set

    // --- player registration ------------------------------------------------

    fun registerPlayer(
        player: Player,
        analyzer: TrackAnalyzerAudioProcessor,
        filter: DjFilterAudioProcessor,
        tail: DjTailAudioProcessor,
        delay: DelayAudioProcessor,
    ) {
        analyzers[player] = analyzer
        filters[player] = filter
        tails[player] = tail
        delays[player] = delay
    }

    fun unregisterPlayer(player: Player) {
        analyzers.remove(player)
        filters.remove(player)
        tails.remove(player)
        delays.remove(player)
        analyzingMediaId.remove(player)
    }

    fun filterFor(player: Player?): DjFilterAudioProcessor? = player?.let { filters[it] }

    /**
     * Bass swap. The outgoing track loses its low end while the incoming one
     * gains it, so the two never fight over the same frequencies — this is the
     * difference between a mix sounding *mixed* and sounding like two songs
     * playing at once.
     *
     * [progress] runs 0..1 across the crossfade. Targets are stepped rather
     * than jumped; the processor glides between the steps.
     */
    fun driveFilterSweep(outgoing: Player?, incoming: Player?, progress: Float) {
        val plan = activePlan ?: return
        if (plan.tier != DjMixTier.FULL_DJ) return
        val eased = progress.coerceIn(0f, 1f)

        filterFor(outgoing)?.lowPassHz = logLerp(
            DjFilterAudioProcessor.OPEN_LOW_PASS_HZ,
            DjFilterAudioProcessor.BASS_KILL_LOW_PASS_HZ,
            eased,
        )
        filterFor(incoming)?.highPassHz = logLerp(
            DjFilterAudioProcessor.BASS_KILL_HIGH_PASS_HZ,
            DjFilterAudioProcessor.OPEN_HIGH_PASS_HZ,
            eased,
        )
    }

    /** Puts the incoming player's filter where the sweep starts, before it
     *  makes a sound, so the first thing heard is already bass-cut rather than
     *  full-range for a buffer. */
    fun armIncomingFilter(incoming: Player?) {
        val filter = filterFor(incoming) ?: return
        if (activePlan?.tier != DjMixTier.FULL_DJ) {
            filter.open()
            return
        }
        filter.highPassHz = DjFilterAudioProcessor.BASS_KILL_HIGH_PASS_HZ
        filter.lowPassHz = DjFilterAudioProcessor.OPEN_LOW_PASS_HZ
        filter.snapToTargets()
    }

    /** Filters must not outlive the transition — the incoming player is the
     *  primary player afterwards and has to be full-range. */
    fun openFilters(vararg players: Player?) {
        players.forEach { filterFor(it)?.open() }
    }

    private fun logLerp(from: Float, to: Float, t: Float): Float =
        Math.exp(
            Math.log(from.toDouble()) + (Math.log(to.toDouble()) - Math.log(from.toDouble())) * t
        ).toFloat()

    /** A player moved to a new track: flush whatever the old one accumulated
     *  first, then restart analysis. */
    fun onTrackChanged(player: Player, newMediaId: String?) {
        analyzingMediaId[player]?.let { captureEnergyProfile(player, it) }
        analyzers[player]?.resetForTrack()
        if (newMediaId != null) {
            analyzingMediaId[player] = newMediaId
        } else {
            analyzingMediaId.remove(player)
        }
        publishState(newMediaId, nextMediaId = null)
    }

    /** Refreshes the published readout. [nextMediaId] is null when the upcoming
     *  track isn't known or isn't analyzed yet. */
    fun publishState(currentMediaId: String?, nextMediaId: String?) {
        val current = analysisFor(currentMediaId)
        val next = analysisFor(nextMediaId)
        _state.value = DjState(
            enabled = enabled,
            currentBpm = current?.grid?.bpm ?: 0f,
            currentKey = current?.key?.takeIf { it.confidence >= MusicalKey.MIN_USABLE_CONFIDENCE }?.camelot,
            nextBpm = next?.grid?.bpm ?: 0f,
            nextKey = next?.key?.takeIf { it.confidence >= MusicalKey.MIN_USABLE_CONFIDENCE }?.camelot,
            tier = activePlan?.tier,
            style = activePlan?.let { activeStyle },
        )
    }

    /** Fired from the audio renderer thread once a track has had enough audio
     *  to analyze. Resolved against whichever player owns this analyzer's
     *  CURRENT media item — looked up at call time, not capture time, since the
     *  estimate belongs to whatever is playing when it completes. */
    fun onAnalysisReady(analyzer: TrackAnalyzerAudioProcessor) {
        scope.launch {
            val owner = analyzers.entries.find { it.value === analyzer }?.key ?: return@launch
            val mediaId = owner.currentMediaItem?.mediaId ?: return@launch
            // Off the main thread as well as off the audio thread: the tempo
            // search is heavy enough to drop a frame if it ran on either.
            val (grid, key) = withContext(Dispatchers.Default) { analyzer.computeAnalysis() }
            if (grid.bpm <= 0f && key.pitchClass < 0) return@launch
            val analysis = TrackAnalysis(grid, key.takeIf { it.pitchClass >= 0 }, analyzer.energyProfile)
            analyses[mediaId] = analysis
            persist(mediaId, analysis)
            // Seeds the map for a player that never fired a transition event —
            // the crossfade's secondary player is started before it is listened
            // to, so this is its first chance to be recorded.
            analyzingMediaId[owner] = mediaId
            if (owner.currentMediaItem?.mediaId == mediaId) publishState(mediaId, null)
            Timber.tag(TAG).d(
                "ANALYSED $mediaId | %.1f BPM conf=%.2f | anchor=%.0fms | key=%s | profile=%ds"
                    .format(
                        grid.bpm,
                        grid.confidence,
                        grid.anchorBeatMs,
                        analysis.key?.camelot ?: "?",
                        analysis.energyProfile.size,
                    )
            )
        }
    }

    /**
     * Re-read the energy profile for a track that has been playing and store the
     * fuller version. The grid is final after the analysis window, but the
     * profile grows for the whole track, and the outro — the part that decides
     * how to mix out — only exists once the track has actually got there.
     */
    fun captureEnergyProfile(player: Player, mediaId: String) {
        val analyzer = analyzers[player] ?: return
        val profile = analyzer.energyProfile
        if (profile.isEmpty()) return
        val existing = analyses[mediaId] ?: return
        if (profile.size <= existing.energyProfile.size) return

        val updated = existing.copy(energyProfile = profile)
        analyses[mediaId] = updated
        persist(mediaId, updated)
    }

    // --- persistence --------------------------------------------------------

    /** Load cached analysis for upcoming queue items so a track that has been
     *  heard before needs no probe at all. */
    fun hydrate(mediaIds: List<String>) {
        val missing = mediaIds.filter { it.isNotEmpty() && !analyses.containsKey(it) }
        if (missing.isEmpty()) return
        scope.launch {
            runCatching {
                database.songAnalyses(missing, SongAnalysisEntity.CURRENT_ANALYSIS_VERSION)
            }.onSuccess { rows ->
                for (row in rows) {
                    analyses[row.id] = TrackAnalysis(
                        grid = BeatGrid(row.bpm, row.anchorBeatMs, row.bpmConfidence),
                        key = if (row.keyIndex >= 0) {
                            MusicalKey(row.keyIndex, row.keyIsMinor, row.keyConfidence)
                        } else {
                            null
                        },
                        energyProfile = row.energyProfile,
                    )
                }
                if (rows.isNotEmpty()) Timber.tag(TAG).d("Hydrated ${rows.size} cached analyses")
            }.onFailure { Timber.tag(TAG).w(it, "Analysis hydrate failed") }
        }
    }

    private fun persist(mediaId: String, analysis: TrackAnalysis) {
        database.query {
            runCatching {
                upsert(
                    SongAnalysisEntity(
                        id = mediaId,
                        bpm = analysis.grid.bpm,
                        anchorBeatMs = analysis.grid.anchorBeatMs,
                        bpmConfidence = analysis.grid.confidence,
                        keyIndex = analysis.key?.pitchClass ?: -1,
                        keyIsMinor = analysis.key?.isMinor == true,
                        keyConfidence = analysis.key?.confidence ?: 0f,
                        energyProfile = analysis.energyProfile,
                    )
                )
            }.onFailure { Timber.tag(TAG).w(it, "Analysis persist failed for $mediaId") }
        }
    }

    // --- pre-analysis probe -------------------------------------------------

    fun hasAnalysis(mediaId: String?): Boolean = mediaId != null && analyses.containsKey(mediaId)

    fun analysisFor(mediaId: String?): TrackAnalysis? = mediaId?.let { analyses[it] }

    /**
     * Silently pre-plays [item] through a throwaway muted player at
     * [PROBE_SPEED]x so the analyzer can cache a grid for it well before the
     * real crossfade needs one.
     *
     * The probe rides the normal ResolvingDataSource chain, so YouTube
     * resolution, lossless namespacing, Spine modules and the cache all work
     * exactly as they do for real playback — which is why this exists instead
     * of a separate MediaExtractor decode path.
     *
     * [guardMediaId] is the track that was playing when this was scheduled; if
     * it has changed by the time the probe fires, the transition it was for is
     * no longer happening.
     */
    fun schedulePreAnalysis(
        item: MediaItem,
        afterMs: Long,
        guardMediaId: String?,
        currentMediaId: () -> String?,
    ) {
        if (afterMs <= 0L || hasAnalysis(item.mediaId)) return
        probeJob = scope.launch {
            delay(afterMs)
            if (!isActive || currentMediaId() != guardMediaId) return@launch
            if (hasAnalysis(item.mediaId)) {
                Timber.tag(TAG).d("PROBE skipped, ${item.mediaId} already analysed")
                return@launch
            }
            runCatching {
                val probe = createProbePlayer()
                probePlayer = probe
                probe.setMediaItem(item)
                probe.volume = 0f
                probe.playbackParameters = PlaybackParameters(PROBE_SPEED, 1f)
                probe.prepare()
                probe.playWhenReady = true
                Timber.tag(TAG).d("Pre-analysing ${item.mediaId} at ${PROBE_SPEED}x")
            }.onFailure {
                Timber.tag(TAG).w(it, "DJ pre-analysis probe failed to start")
                releaseProbe()
            }
        }
    }

    fun releaseProbe() {
        probeJob?.cancel()
        probeJob = null
        probePlayer?.let { probe ->
            unregisterPlayer(probe)
            runCatching {
                probe.stop()
                probe.clearMediaItems()
                probe.release()
            }
        }
        probePlayer = null
    }

    // --- decisions ----------------------------------------------------------

    /** Builds the plan for a transition, or null when DJ mixing is off. */
    fun planTransition(
        outgoingMediaId: String?,
        incomingMediaId: String?,
        outgoingDurationMs: Long,
    ): DjMixPlan? {
        if (!enabled) {
            activePlan = null
            return null
        }
        val outgoing = analysisFor(outgoingMediaId)
        val incoming = analysisFor(incomingMediaId)
        val plan = DjMixPlanner.plan(outgoing?.grid, incoming?.grid, outgoing?.key, incoming?.key)
        activePlan = plan
        activeStyle = TransitionSelector.select(
            tier = plan.tier,
            outgoing = outgoing,
            incoming = incoming,
            outgoingDurationMs = outgoingDurationMs,
            creativeEnabled = creativeEnabled,
            recentStyles = recentStyles.toList(),
        )
        Timber.tag(TAG).d(
            "PLAN ${outgoingMediaId ?: "?"} -> ${incomingMediaId ?: "?"} | ${plan.tier} | $activeStyle | " +
                "out=%.1fBPM/%s in=%.1fBPM/%s | speed=%.4f pitch=%+d st | creative=%b"
                    .format(
                        outgoing?.grid?.bpm ?: 0f,
                        outgoing?.key?.camelot ?: "?",
                        incoming?.grid?.bpm ?: 0f,
                        incoming?.key?.camelot ?: "?",
                        plan.incomingSpeedAdjustment,
                        plan.incomingPitchShiftSemitones,
                        creativeEnabled,
                    )
        )
        publishState(outgoingMediaId, incomingMediaId)
        return plan
    }

    /**
     * Arms whatever the selector chose on the outgoing player, for the length of
     * the crossfade. All three effects are things a DJ does to the track that is
     * *leaving* — the incoming one is supposed to arrive clean.
     */
    fun startTailEffect(outgoing: Player?, crossfadeMs: Long) {
        tailJob?.cancel()
        tailJob = null
        if (!creativeEnabled || outgoing == null) return

        // No measured beat, no effect. Guessing a tempo here means looping or
        // freezing on a grid that has nothing to do with the music.
        val beatMs = analysisFor(outgoing.currentMediaItem?.mediaId)?.grid?.beatMs
            ?.takeIf { it > 0f } ?: return

        when (activeStyle) {
            TransitionStyle.TRANSPARENT -> Unit

            TransitionStyle.TAPE_STOP -> {
                Timber.tag(TAG).d("Tape stop over ${(beatMs * TAPE_STOP_BEATS).toLong()}ms")
                tails[outgoing]?.startTapeStop((beatMs * TAPE_STOP_BEATS).toLong())
            }

            TransitionStyle.ECHO_FREEZE -> {
                val echo = delays[outgoing] ?: return
                Timber.tag(TAG).d("Echo freeze at ${(crossfadeMs * ECHO_FREEZE_AT).toLong()}ms into the fade")
                // Two stages, and the timing matters as much as the order.
                // Freezing an empty delay line produces silence, so the line is
                // fed first; and freezing kills the dry signal outright, so it
                // has to happen late in the fade. Freezing a beat in — as this
                // first did — removed the outgoing track four seconds before
                // the crossfade had finished handing over, leaving a hole.
                echo.frozen = false
                echo.wetMix = ECHO_WET_MIX
                tailJob = scope.launch {
                    delay((crossfadeMs * ECHO_FREEZE_AT).toLong().coerceAtLeast(beatMs.toLong()))
                    echo.frozen = true
                }
            }
        }
    }

    /** Effects must never outlive their transition. */
    fun stopTailEffects(vararg players: Player?) {
        if (activeStyle != TransitionStyle.TRANSPARENT) {
            Timber.tag(TAG).d("EFFECT END $activeStyle")
        }
        tailJob?.cancel()
        tailJob = null
        players.filterNotNull().forEach { player ->
            tails[player]?.stop()
            delays[player]?.let {
                it.frozen = false
                it.wetMix = 0f
            }
        }
    }

    fun clearActivePlan() {
        // Recorded even when transparent, so a run of transparent mixes doesn't
        // leave a stale creative move sitting at the head of the history.
        recentStyles.addFirst(activeStyle)
        while (recentStyles.size > STYLE_HISTORY) recentStyles.removeLast()
        activePlan = null
        activeStyle = TransitionStyle.TRANSPARENT
    }

    /** Keeps the readout honest when the feature is toggled at runtime. */
    fun refreshState(currentMediaId: String?) = publishState(currentMediaId, null)

    val activePlanNeedsSpeedReset: Boolean
        get() = activePlan?.let { it.tier != DjMixTier.PLAIN_CROSSFADE } == true

    /**
     * Where the crossfade should actually start, in track time.
     *
     * Two refinements over a flat `duration - crossfadeMs`:
     * 1. If the energy profile shows the track dropping into an outro near that
     *    point, mix out from the drop — that is where a human would.
     * 2. Snap to a phrase boundary. Starting mid-phrase is the most audible
     *    tell that a machine chose the moment.
     *
     * Falls back to the flat point whenever DJ mixing is off or the track has no
     * usable grid, so non-DJ crossfades behave exactly as before.
     */
    fun transitionPointMs(mediaId: String?, durationMs: Long, crossfadeMs: Long): Long {
        val flat = durationMs - crossfadeMs
        if (!enabled || durationMs <= 0L) return flat

        val analysis = analysisFor(mediaId) ?: return flat
        val grid = analysis.grid
        if (grid.bpm <= 0f || grid.confidence <= 0f) return flat

        var target = flat
        val profile = analysis.energyProfile
        if (EnergyProfile.coversOutro(profile, durationMs)) {
            val dropSecond = EnergyProfile.outroDropSecond(profile)
            if (dropSecond >= 0) {
                val dropMs = dropSecond * 1000L
                // Only honour a drop that is genuinely near the end; an early
                // breakdown is not an invitation to cut the track short.
                if (dropMs in (flat - MAX_OUTRO_PULL_MS)..(flat + crossfadeMs)) target = dropMs
            }
        }

        // Snap DOWN, never to the nearest boundary. Rounding to nearest can
        // push the mix up to half a phrase (~3.7 s at 128 BPM) *later* than
        // `duration - crossfadeMs`, which leaves less track than the fade needs
        // and the outgoing song simply ends partway through the crossfade. The
        // clamp enforces the same rule: the transition may start earlier than
        // the flat point, never later.
        val snapped = grid.phraseAtOrBeforeMs(target).toLong()
        // flat already leaves crossfadeMs of tail, but a short configured
        // crossfade (e.g. well under a second) could otherwise let the
        // transition land with almost nothing left to play after it.
        // MIN_TAIL_MS is an independent floor for that case.
        val maxResult = minOf(flat, durationMs - MIN_TAIL_MS).coerceAtLeast(0L)
        val minResult = (flat - MAX_OUTRO_PULL_MS).coerceAtMost(maxResult)
        val result = snapped.coerceIn(minResult, maxResult)
        Timber.tag(TAG).d(
            "MIXOUT $mediaId | duration=${durationMs}ms flat=${flat}ms " +
                "outroDrop=${if (target != flat) "${target}ms" else "none"} -> ${result}ms " +
                "(${flat - result}ms early)"
        )
        return result
    }

    /**
     * Where the incoming track should start playing, in its own track time.
     *
     * This has to land *on the incoming track's own beat grid*. Releasing the
     * incoming player exactly on an outgoing beat only aligns the two if the
     * incoming is also starting on one of its beats — start it at position 0
     * and the grids stay offset by whatever the incoming's own phase happens to
     * be, which is the very error the beat tracker exists to measure.
     *
     * That costs less than one beat of the track, and stays inside audio the
     * player has already buffered.
     *
     * Deliberately does NOT skip a quiet intro, though mixing into 30 s of pad
     * is a poor mix: seeking a freshly created player up to 16 s in means
     * fetching an unbuffered byte range with only the crossfade's few seconds to
     * do it, and an incoming track that never arrives is far worse than one that
     * arrives over its own intro. Worth revisiting once transitions are proven
     * solid, gated on the region already being buffered.
     */
    fun incomingStartMs(mediaId: String?): Long {
        if (!enabled) return 0L
        val analysis = analysisFor(mediaId) ?: return 0L
        val grid = analysis.grid
        if (grid.bpm <= 0f || grid.confidence <= 0f) return 0L
        return grid.beatAtOrAfter(0L).toLong().coerceAtLeast(0L)
    }

    /** Cancels in-flight work and drops all state. Cancels the scope's
     *  children rather than the scope itself, so the engine still works if the
     *  service is recreated. */
    fun release() {
        releaseProbe()
        scope.coroutineContext.cancelChildren()
        analyzers.clear()
        filters.clear()
        tails.clear()
        delays.clear()
        analyzingMediaId.clear()
        recentStyles.clear()
        analyses.clear()
        activePlan = null
    }

    companion object {
        private const val TAG = "DjEngine"

        /** Probe playback rate. 4x provides ample lead time while keeping background
         *  CPU audio decoding overhead low. */
        const val PROBE_SPEED = 4f

        /** How far ahead of the crossfade to start pre-analysing. 40 s of track
         *  at 6x is ~7 s of wall clock; the rest is slack for buffering. */
        const val PROBE_LEAD_MS = 20_000L

        /** How far earlier than the flat point an outro drop may pull the mix. */
        private const val MAX_OUTRO_PULL_MS = 25_000L

        /** Always leave this much track after the transition point. */
        private const val MIN_TAIL_MS = 1_000L

        private const val TAPE_STOP_BEATS = 1.5f
        private const val ECHO_WET_MIX = 0.55f

        /** Fraction of the crossfade at which the echo stops taking new input.
         *  Late, so the outgoing track is already most of the way faded out by
         *  the time its dry signal disappears. */
        private const val ECHO_FREEZE_AT = 0.6f

        /** Long enough to serve the selector's effect cooldown. */
        private const val STYLE_HISTORY = 6
    }
}
