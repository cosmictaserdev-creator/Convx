/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.dj

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.SongAnalysisEntity
import com.convx.music.playback.audio.DelayAudioProcessor
import com.convx.music.playback.audio.DjFilterAudioProcessor
import com.convx.music.playback.audio.DjAmbienceAudioProcessor
import com.convx.music.playback.audio.ReverbType
import com.convx.music.playback.audio.DelayType
import com.convx.music.playback.audio.DjTailAudioProcessor
import com.convx.music.playback.audio.DjMixPlan
import com.convx.music.playback.audio.DjMixPlanner
import com.convx.music.playback.audio.DjMixTier
import com.convx.music.playback.audio.TrackAnalyzerAudioProcessor
import kotlin.math.PI
import kotlin.math.cos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

data class DjState(
    val enabled: Boolean = false,
    val currentBpm: Float = 0f,
    val currentKey: String? = null,
    val nextBpm: Float = 0f,
    val nextKey: String? = null,
    val tier: DjMixTier? = null,
    val style: TransitionStyle? = null,
)

@UnstableApi
class DjEngine(
    private val database: MusicDatabase,
    private val createProbePlayer: () -> ExoPlayer,
    private val releaseProbePlayer: (ExoPlayer) -> Unit = { it.release() },
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Volatile
    var enabled: Boolean = false
        set(value) {
            field = value
            if (!value) releaseProbe()
        }

    @Volatile var reverbType = ReverbType.AUTO
    @Volatile var delayType = DelayType.AUTO

    @Volatile
    private var creativePref: Boolean = false

    var creativeEnabled: Boolean
        get() = creativePref && enabled
        set(value) { creativePref = value }

    private val analyses = java.util.concurrent.ConcurrentHashMap<String, TrackAnalysis>()

    /** Model-based beat grid for a track, preferred over the envelope tracker's when present. */
    @Volatile var smartGrid: (String) -> BeatGrid? = { null }
    private val analyzers = java.util.concurrent.ConcurrentHashMap<Player, TrackAnalyzerAudioProcessor>()
    private val filters = java.util.concurrent.ConcurrentHashMap<Player, DjFilterAudioProcessor>()
    private val tails = java.util.concurrent.ConcurrentHashMap<Player, DjTailAudioProcessor>()
    private val analyzingMediaId = java.util.concurrent.ConcurrentHashMap<Player, String>()
    private val delays = java.util.concurrent.ConcurrentHashMap<Player, DelayAudioProcessor>()
    private val ambience = java.util.concurrent.ConcurrentHashMap<Player, DjAmbienceAudioProcessor>()

    private val recentStyles = ArrayDeque<TransitionStyle>()
    private var transitionIndex = 0L
    private var echoFrozen = false
    private var tapeStarted = false
    private var transitionBeatMs = 500L
    private var transitionDurationMs = 0L
    private var effectsActive = false

    var activeStyle: TransitionStyle = TransitionStyle.TRANSPARENT
        private set

    /** True while the outgoing track's wash is the thing being heard, so it must outlive the fade. */
    val holdsTail: Boolean get() = effectsActive && activeStyle in TailStyles

    private val _state = MutableStateFlow(DjState())
    val state: StateFlow<DjState> = _state.asStateFlow()

    private var probeJob: Job? = null
    private var probePlayer: ExoPlayer? = null
    private var probeGuardMediaId: String? = null

    var activePlan: DjMixPlan? = null
        private set

    fun registerPlayer(
        player: Player,
        analyzer: TrackAnalyzerAudioProcessor,
        filter: DjFilterAudioProcessor,
        tail: DjTailAudioProcessor,
        delay: DelayAudioProcessor,
        ambience: DjAmbienceAudioProcessor,
    ) {
        analyzers[player] = analyzer
        filters[player] = filter
        tails[player] = tail
        delays[player] = delay
        this.ambience[player] = ambience
    }

    fun unregisterPlayer(player: Player) {
        analyzers.remove(player)
        filters.remove(player)
        tails.remove(player)
        delays.remove(player)
        ambience.remove(player)
        analyzingMediaId.remove(player)
    }

    fun filterFor(player: Player?): DjFilterAudioProcessor? = player?.let { filters[it] }

    fun driveFilterSweep(outgoing: Player?, incoming: Player?, progress: Float, bassSwapFraction: Float = 0.6f) {
        val plan = activePlan ?: return
        if (plan.tier != DjMixTier.FULL_DJ) return
        val swap = bassSwapFraction.coerceIn(0.2f, 0.8f)
        val outgoingAmount = ((progress - (swap - 0.2f)) / 0.28f).coerceIn(0f, 1f)
        val incomingAmount = ((progress - (swap - 0.02f)) / 0.3f).coerceIn(0f, 1f)
        filterFor(outgoing)?.let {
            it.lowPassHz = DjFilterAudioProcessor.OPEN_LOW_PASS_HZ
            it.highPassHz = logLerp(DjFilterAudioProcessor.OPEN_HIGH_PASS_HZ, 600f, outgoingAmount)
        }
        filterFor(incoming)?.let {
            it.lowPassHz = DjFilterAudioProcessor.OPEN_LOW_PASS_HZ
            it.highPassHz = logLerp(800f, DjFilterAudioProcessor.OPEN_HIGH_PASS_HZ, incomingAmount)
        }
    }

    fun armIncomingFilter(incoming: Player?) {
        val filter = filterFor(incoming) ?: return
        if (activePlan?.tier != DjMixTier.FULL_DJ) {
            filter.open()
            return
        }
        filter.highPassHz = 800f
        filter.lowPassHz = DjFilterAudioProcessor.OPEN_LOW_PASS_HZ
        filter.snapToTargets()
    }

    fun openFilters(vararg players: Player?) {
        players.forEach { filterFor(it)?.open() }
    }

    private fun logLerp(from: Float, to: Float, t: Float): Float =
        Math.exp(
            Math.log(from.toDouble()) + (Math.log(to.toDouble()) - Math.log(from.toDouble())) * t
        ).toFloat()

    fun onTrackChanged(player: Player, newMediaId: String?) {
        if (player !== probePlayer && probeGuardMediaId != null && newMediaId != probeGuardMediaId) releaseProbe()
        analyzingMediaId[player]?.let { captureEnergyProfile(player, it) }
        analyzers[player]?.resetForTrack(mediaId = newMediaId)
        if (newMediaId != null) {
            analyzingMediaId[player] = newMediaId
        } else {
            analyzingMediaId.remove(player)
        }
        publishState(newMediaId, nextMediaId = null)
    }

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

    fun onAnalysisReady(analyzer: TrackAnalyzerAudioProcessor) {
        val input = analyzer.captureAnalysisInput() ?: return
        scope.launch {
            val owner = analyzers.entries.find { it.value === analyzer }?.key ?: return@launch
            val snapshot = withContext(Dispatchers.Default) { analyzer.computeAnalysis(input) }
            if (!analyzer.isCurrentAnalysis(snapshot.generation)) return@launch
            val mediaId = snapshot.mediaId ?: owner.currentMediaItem?.mediaId ?: return@launch
            if (owner.currentMediaItem?.mediaId != mediaId) return@launch
            val grid = snapshot.grid
            val key = snapshot.key
            if (grid.bpm <= 0f && key.pitchClass < 0) {
                if (owner === probePlayer) releaseProbe()
                return@launch
            }
            val analysis = TrackAnalysis(grid, key.takeIf { it.pitchClass >= 0 }, snapshot.energyProfile)
            analyses[mediaId] = analysis
            persist(mediaId, analysis)
            analyzingMediaId[owner] = mediaId
            if (owner.currentMediaItem?.mediaId == mediaId) publishState(mediaId, null)
            if (owner === probePlayer) releaseProbe()
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

    fun captureEnergyProfile(player: Player, mediaId: String) {
        val analyzer = analyzers[player] ?: return
        val snapshot = analyzer.energySnapshot() ?: return
        if (snapshot.mediaId != null && snapshot.mediaId != mediaId) return
        val profile = snapshot.energyProfile
        if (profile.isEmpty()) return
        val existing = analyses[mediaId] ?: return
        if (profile.size <= existing.energyProfile.size) return

        val updated = existing.copy(energyProfile = profile)
        analyses[mediaId] = updated
        persist(mediaId, updated)
    }

    fun hydrate(mediaIds: List<String>) {
        val missing = mediaIds.filter { it.isNotEmpty() && !analyses.containsKey(it) }
        if (missing.isEmpty()) return
        scope.launch {
            runCatching {
                database.songAnalyses(missing, SongAnalysisEntity.CURRENT_ANALYSIS_VERSION)
            }.onSuccess { rows ->
                for (row in rows) {
                    analyses.putIfAbsent(row.id, TrackAnalysis(
                        grid = BeatGrid(row.bpm, row.anchorBeatMs, row.bpmConfidence),
                        key = if (row.keyIndex >= 0) {
                            MusicalKey(row.keyIndex, row.keyIsMinor, row.keyConfidence)
                        } else {
                            null
                        },
                        energyProfile = row.energyProfile,
                    ))
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

    fun hasAnalysis(mediaId: String?): Boolean = mediaId != null && (analyses.containsKey(mediaId) || smartGrid(mediaId) != null)

    fun analysisFor(mediaId: String?): TrackAnalysis? {
        if (mediaId == null) return null
        val own = analyses[mediaId]
        val grid = smartGrid(mediaId) ?: return own
        return own?.copy(grid = grid) ?: TrackAnalysis(grid)
    }

    fun echoPointMs(mediaId: String?): Long? {
        val profile = analysisFor(mediaId)?.energyProfile ?: return null
        if (profile.size < MIN_ECHO_POINT_PROFILE_SECONDS) return null
        val start = (profile.size * ECHO_POINT_WINDOW_START).toInt()
        val end = (profile.size * ECHO_POINT_WINDOW_END).toInt().coerceAtLeast(start + 1)
        var bestIndex = start
        var bestValue = -1
        for (i in start until end) {
            val value = profile[i].toInt() and 0xFF
            if (value > bestValue) {
                bestValue = value
                bestIndex = i
            }
        }
        return bestIndex * 1000L
    }

    fun schedulePreAnalysis(
        item: MediaItem,
        afterMs: Long,
        guardMediaId: String?,
        currentMediaId: () -> String?,
    ) {
        if (!enabled || afterMs <= 0L || hasAnalysis(item.mediaId)) return
        releaseProbe()
        probeGuardMediaId = guardMediaId
        probeJob = scope.launch {
            var probe: ExoPlayer? = null
            try {
                delay(afterMs)
                if (!enabled || currentMediaId() != guardMediaId || hasAnalysis(item.mediaId)) return@launch
                probe = createProbePlayer()
                probePlayer = probe
                probe.setMediaItem(item)
                probe.volume = 0f
                probe.playbackParameters = PlaybackParameters(PROBE_SPEED, 1f)
                probe.prepare()
                probe.playWhenReady = true
                delay(PROBE_TIMEOUT_MS)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Timber.tag(TAG).w(error, "DJ pre-analysis probe failed")
            } finally {
                if (probe != null && probePlayer === probe) releaseProbe()
            }
        }
    }

    fun releaseProbe() {
        probeJob?.cancel()
        probeJob = null
        val probe = probePlayer
        probePlayer = null
        probeGuardMediaId = null
        probe?.let {
            unregisterPlayer(probe)
            runCatching {
                probe.stop()
                probe.clearMediaItems()
                releaseProbePlayer(probe)
            }
        }
    }

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
            transitionIndex = transitionIndex,
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

    fun startTransitionEffects(outgoing: Player?, incoming: Player?, crossfadeMs: Long) {
        stopTailEffects(outgoing, incoming)
        if (!creativeEnabled || outgoing == null || incoming == null) return
        effectsActive = true
        echoFrozen = false
        tapeStarted = false
        transitionDurationMs = crossfadeMs
        transitionBeatMs = measuredBeatMs(outgoing) ?: FALLBACK_BEAT_MS
        val resolvedReverb = if (reverbType == ReverbType.AUTO) {
            ReverbPrograms[(transitionIndex % ReverbPrograms.size).toInt()]
        } else reverbType
        val resolvedDelay = if (delayType == DelayType.AUTO) {
            DelayPrograms[(transitionIndex % DelayPrograms.size).toInt()]
        } else delayType
        // The wash goes on the outgoing track and rings out into the next one; the
        // incoming stays dry so its downbeat or vocal lands clean.
        ambience[outgoing]?.reverbType = resolvedReverb
        delays[outgoing]?.let { echo ->
            echo.delayType = if (activeStyle == TransitionStyle.ECHO_FREEZE && resolvedDelay == DelayType.SLAPBACK) {
                DelayType.FILTERED
            } else resolvedDelay
            echo.frozen = false
            echo.clearTail()
            echo.setDelayMs(
                if (activeStyle != TransitionStyle.ECHO_FREEZE && resolvedDelay == DelayType.SLAPBACK) 110L
                else transitionBeatMs / 2
            )
            if (activeStyle == TransitionStyle.ECHO_FREEZE) {
                echo.freezeFeedback = 0.72f
                echo.wetMix = 0.25f
            }
        }
    }

    fun driveTransitionEffects(outgoing: Player?, incoming: Player?, progress: Float) {
        if (!effectsActive || outgoing == null || incoming == null) return
        if (!creativeEnabled) {
            stopTailEffects(outgoing, incoming)
            return
        }
        val position = progress.coerceIn(0f, 1f)
        if (activeStyle !in TailStyles) filters[outgoing]?.stereoWidth = 1f - 0.65f * position
        // Only a ringing wash fades the dry here; every other style fades through player volume.
        if (activeStyle in TailStyles) delays[outgoing]?.dryLevel = cos(position * (PI / 2)).toFloat()
        filters[incoming]?.pan = orbitPan(position)
        val wash = (position / WASH_PEAK_AT).coerceIn(0f, 1f).let { it * it * (3f - 2f * it) }
        ambience[outgoing]?.wetMix = wash * if (activeStyle == TransitionStyle.REVERB_BLOOM) 0.5f else 0.15f
        if (activeStyle != TransitionStyle.ECHO_FREEZE) {
            delays[outgoing]?.wetMix = wash * if (activeStyle == TransitionStyle.DELAY_BLEND) 0.45f else 0.12f
        }
        if (activeStyle == TransitionStyle.ECHO_FREEZE && !echoFrozen && position >= ECHO_FREEZE_AT) {
            delays[outgoing]?.frozen = true
            echoFrozen = true
        }
        if (activeStyle == TransitionStyle.TAPE_STOP && !tapeStarted && position >= TAPE_START_AT) {
            val remaining = (transitionDurationMs * (1f - position)).toLong().coerceAtLeast(100L)
            tails[outgoing]?.startTapeStop(minOf(remaining, (transitionBeatMs * TAPE_STOP_BEATS).toLong()))
            tapeStarted = true
        }
    }

    /**
     * The incoming track enters hard on one side and circles the head, the circling
     * slowing as it goes, until it settles in the centre as the transition ends.
     */
    private fun orbitPan(progress: Float): Float {
        val side = if (transitionIndex % 2 == 0L) -1f else 1f
        val remaining = 1f - progress
        val turns = ORBIT_TURNS * (1f - remaining * remaining)
        return side * cos(2.0 * PI * turns).toFloat() * remaining
    }

    private fun measuredBeatMs(player: Player): Long? = analysisFor(player.currentMediaItem?.mediaId)?.grid
        ?.takeIf { it.bpm > 0f && it.confidence >= 0.2f }
        ?.beatMs?.toLong()?.coerceIn(250L, 1_500L)

    fun stopTailEffects(vararg players: Player?) {
        if (activeStyle != TransitionStyle.TRANSPARENT) {
            Timber.tag(TAG).d("EFFECT END $activeStyle")
        }
        effectsActive = false
        players.forEach { player ->
            if (player == null) return@forEach
            tails[player]?.stop()
            filters[player]?.open()
            ambience[player]?.stop()
            delays[player]?.let {
                it.frozen = false
                it.wetMix = 0f
                it.dryLevel = 1f
                it.clearTail()
            }
        }
    }

    fun clearActivePlan() {
        if (activePlan == null) return
        recentStyles.addFirst(activeStyle)
        while (recentStyles.size > STYLE_HISTORY) recentStyles.removeLast()
        activePlan = null
        activeStyle = TransitionStyle.TRANSPARENT
        transitionIndex++
        _state.value = _state.value.copy(tier = null, style = null)
    }

    fun refreshState(currentMediaId: String?) = publishState(currentMediaId, null)

    val activePlanNeedsSpeedReset: Boolean
        get() = activePlan?.let { it.tier != DjMixTier.PLAIN_CROSSFADE } == true

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
                if (dropMs in (flat - MAX_OUTRO_PULL_MS)..(flat + crossfadeMs)) target = dropMs
            }
        }

        val snapped = grid.phraseAtOrBeforeMs(target).toLong()
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

    fun incomingStartMs(mediaId: String?): Long {
        if (!enabled) return 0L
        val analysis = analysisFor(mediaId) ?: return 0L
        val grid = analysis.grid
        if (grid.bpm <= 0f || grid.confidence <= 0f) return 0L
        return grid.beatAtOrAfter(0L).toLong().coerceAtLeast(0L)
    }

    fun release() {
        releaseProbe()
        scope.coroutineContext.cancelChildren()
        analyzers.clear()
        filters.clear()
        tails.clear()
        delays.clear()
        ambience.clear()
        analyzingMediaId.clear()
        recentStyles.clear()
        analyses.clear()
        activePlan = null
        activeStyle = TransitionStyle.TRANSPARENT
        effectsActive = false
        transitionIndex = 0L
    }

    companion object {
        private const val TAG = "DjEngine"

        private const val MIN_ECHO_POINT_PROFILE_SECONDS = 40
        private const val ECHO_POINT_WINDOW_START = 0.25
        private const val ECHO_POINT_WINDOW_END = 0.70

        const val PROBE_SPEED = 4f

        const val PROBE_LEAD_MS = 20_000L
        private const val PROBE_TIMEOUT_MS = 20_000L
        private const val FALLBACK_BEAT_MS = 500L

        private const val MAX_OUTRO_PULL_MS = 25_000L

        private const val MIN_TAIL_MS = 1_000L

        private const val TAPE_STOP_BEATS = 1.5f

        private const val ECHO_FREEZE_AT = 0.6f
        private const val WASH_PEAK_AT = 0.75f
        private const val ORBIT_TURNS = 1.5f
        private const val TAPE_START_AT = 0.82f
        private val ReverbPrograms = arrayOf(ReverbType.ROOM, ReverbType.PLATE, ReverbType.HALL)
        private val DelayPrograms = arrayOf(DelayType.STEREO, DelayType.PING_PONG, DelayType.TAPE, DelayType.FILTERED, DelayType.SLAPBACK)

        private const val STYLE_HISTORY = 6
        private val TailStyles = setOf(
            TransitionStyle.REVERB_BLOOM, TransitionStyle.DELAY_BLEND, TransitionStyle.ECHO_FREEZE,
        )
    }
}
