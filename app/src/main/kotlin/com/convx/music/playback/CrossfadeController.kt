package com.convx.music.playback

import android.os.SystemClock
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.convx.music.data.settings.AppSettings
import com.convx.music.data.settings.SmartAnalysis
import com.convx.music.data.settings.TrackAnalysisState
import com.convx.music.data.settings.TransitionWindow
import com.convx.music.playback.smart.CrossfadeMode
import com.convx.music.playback.smart.TrackAnalysis
import com.convx.music.playback.smart.TransitionPlan
import com.convx.music.playback.smart.TransitionStyle
import com.convx.music.playback.smart.TransitionTrackInfo
import com.convx.music.playback.smart.planTransition
import com.convx.music.playback.audio.DjMixTier
import com.convx.music.playback.audio.DjFilterAudioProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin

@UnstableApi
class CrossfadeController(
    private val scope: CoroutineScope,
    private val active: () -> ExoPlayer,
    private val standby: () -> ExoPlayer,
    private val onHandoff: (outgoing: ExoPlayer, incoming: ExoPlayer, fadeMs: Long) -> Unit,
    private val analysisFor: (MediaItem) -> TrackAnalysis = { TrackAnalysis() },
    private val requestAnalysis: (MediaItem, Long) -> Unit = { _, _ -> },
    private val filters: TransitionFilters = TransitionFilters.None,
    private val analysisRunningFor: (MediaItem) -> Boolean = { false },
    private val djEngine: com.convx.music.playback.dj.DjEngine? = null,
    private val warmUpcoming: () -> Unit = {},
    private val allowed: () -> Boolean = { true },
) {

    private enum class Phase {
        IDLE,

        ARMING,

        FADING,

        BAILING,
    }

    private var phase = Phase.IDLE

    private var outgoing: ExoPlayer? = null

    private var incoming: ExoPlayer? = null

    private var handedOff = false

    private var queuedItemCount = 0

    private var listeningTo: ExoPlayer? = null

    private var fadeMs = 0L

    private var fadeEndMs = 0L

    private var smartFadeActive = false

    private var incomingCueTimeMs: Long = 0L

    private var incomingPlaybackRate: Double = 1.0
    private var lastTempoUpdateAt = 0L

    private val transitionFilters = object : TransitionFilters {
        override fun incoming(lowPassHz: Float, highPassHz: Float) {
            filters.incoming(lowPassHz, highPassHz)
            applyCutoffs(this@CrossfadeController.incoming, lowPassHz, highPassHz)
        }

        override fun outgoing(lowPassHz: Float, highPassHz: Float) {
            filters.outgoing(lowPassHz, highPassHz)
            applyCutoffs(this@CrossfadeController.outgoing, lowPassHz, highPassHz)
        }

        override fun open() {
            filters.open()
            applyCutoffs(this@CrossfadeController.incoming, TransitionFilterProcessor.OPEN_HZ, TransitionFilterProcessor.OFF_HZ)
            applyCutoffs(this@CrossfadeController.outgoing, TransitionFilterProcessor.OPEN_HZ, TransitionFilterProcessor.OFF_HZ)
        }
    }

    private fun applyCutoffs(player: ExoPlayer?, lowPassHz: Float, highPassHz: Float) {
        val filter = djEngine?.filterFor(player) ?: return
        filter.lowPassHz = lowPassHz
        filter.highPassHz = if (highPassHz <= TransitionFilterProcessor.OFF_HZ) {
            DjFilterAudioProcessor.OPEN_HIGH_PASS_HZ
        } else highPassHz
    }

    private var render = Render()

    private data class Render(
        val style: TransitionStyle = TransitionStyle.EQUAL_POWER,
        val bassSwap: Boolean = false,
        val bassSwapFraction: Double = 0.7,
        val filterSweep: Double = 0.0,
        val vocalOverlap: Double = 0.0,
    )

    private var bailStartedAt = 0L
    private var armDeadline = 0L
    private var lastStreamWarmAt = 0L

    private var settledAt = 0L

    private var bailFromGain = 0f

    private var lastPlanVerdict = ""
    private var lastLoggedPlan: TransitionPlan? = null
    private var lastAnalysisPublishAt = 0L
    private val timelineWindow = Timeline.Window()

    fun isTransitioning(): Boolean = phase != Phase.IDLE

    fun msSinceTransition(): Long? =
        settledAt.takeIf { it != 0L }?.let { SystemClock.elapsedRealtime() - it }

    private val listener = object : Player.Listener {
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) bail()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            when (reason) {
                Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED -> bail()
                Player.MEDIA_ITEM_TRANSITION_REASON_SEEK -> bail()
            }
        }

        override fun onPlayerError(error: PlaybackException) = bail()
    }

    private fun listenTo(target: ExoPlayer) {
        if (listeningTo === target) return
        listeningTo?.removeListener(listener)
        target.addListener(listener)
        listeningTo = target
    }

    private var tickJob: Job? = null

    private var tailPlayer: ExoPlayer? = null
    private var tailJob: Job? = null

    /** Lets the outgoing player keep running silently so its reverb and echo decay instead of being cut. */
    private fun ringOut(player: ExoPlayer) {
        endTail()
        tailPlayer = player
        tailJob = scope.launch {
            delay(TAIL_RING_MS)
            endTail()
        }
    }

    private fun endTail() {
        val player = tailPlayer ?: return
        tailPlayer = null
        tailJob?.cancel()
        tailJob = null
        djEngine?.stopTailEffects(player)
        retire(player)
    }

    fun start() {
        listenTo(active())
        if (tickJob?.isActive == true) return
        tickJob = scope.launch {
            while (isActive) {
                tick()
                delay(
                    when (phase) {
                        Phase.IDLE -> if (AppSettings.smartFadeEnabled.value || configuredFadeMs() > 0L) IDLE_STEP_MS else DISABLED_STEP_MS
                        Phase.ARMING -> ARM_STEP_MS
                        Phase.FADING -> if (incoming?.isPlaying == true) FADE_STEP_MS else IDLE_STEP_MS
                        Phase.BAILING -> BAIL_STEP_MS
                    },
                )
            }
        }
    }

    fun release() {
        endTail()
        tickJob?.cancel()
        tickJob = null
        listeningTo?.removeListener(listener)
        listeningTo = null
        finish()
    }

    fun onSkipRequested() {
        if (phase != Phase.IDLE) bail()
    }

    private fun tick() {
        if (!allowed()) {
            if (phase != Phase.IDLE) finish()
            endTail()
            return
        }
        if (tailPlayer != null && !active().playWhenReady) endTail()
        if (phase == Phase.FADING || phase == Phase.BAILING) {
            outgoing?.playWhenReady = incoming?.playWhenReady ?: true
        }

        publishAnalysisState()

        when (phase) {
            Phase.IDLE -> considerAutoTransition()
            Phase.ARMING -> driveArming()
            Phase.FADING -> driveFade()
            Phase.BAILING -> driveBail()
        }
    }

    private fun considerAutoTransition() {
        val player = active()
        if (!player.isPlaying) return
        if (player.repeatMode == Player.REPEAT_MODE_ONE) return
        if (!player.hasNextMediaItem()) {
            AppSettings.smartTransitionWindow.value = null
            return
        }

        val duration = player.duration
        if (duration == C.TIME_UNSET || duration <= 0L) return
        val now = SystemClock.elapsedRealtime()
        if (duration - player.currentPosition <= 30_000L && now - lastStreamWarmAt >= 20_000L) {
            lastStreamWarmAt = now
            warmUpcoming()
        }

        if (AppSettings.smartFadeEnabled.value) {
            considerSmartTransition(duration)
            return
        }

        if (configuredFadeMs() <= 0L) return
        val fade = fadeFor(duration)
        if (fade <= 0L) return

        val remaining = duration - player.currentPosition
        if (remaining > fade + ARM_LEAD_MS) return

        begin(fade, endMs = duration, smart = false)
    }

    private fun considerSmartTransition(duration: Long) {
        val player = active()
        val currentItem = player.currentMediaItem ?: return
        val nextIndex = player.nextMediaItemIndex
        if (nextIndex == C.INDEX_UNSET) return
        val nextItem = player.getMediaItemAt(nextIndex)
        val nextDuration = nextItemDurationMs(nextIndex, nextItem)

        requestAnalysis(currentItem, duration)
        requestAnalysis(nextItem, nextDuration)

        val fallbackSeconds = configuredFadeMs().takeIf { it > 0L }
            ?.div(1000.0)
            ?: DEFAULT_SMART_FALLBACK_SECONDS

        val currentAnalysis = analysisFor(currentItem)
        val nextAnalysis = analysisFor(nextItem)
        val analysisState = AppSettings.smartAnalysis.value

        val plan = planTransition(
            analysis = currentAnalysis,
            nextAnalysis = nextAnalysis,
            currentTrack = currentItem.toTransitionInfo(duration),
            nextTrack = nextItem.toTransitionInfo(nextDuration),
            currentTime = player.currentPosition / 1000.0,
            duration = duration / 1000.0,
            fadeSeconds = fallbackSeconds,
            mode = CrossfadeMode.SMART,
            albumSequential = !player.shuffleModeEnabled && nextIndex == player.currentMediaItemIndex + 1,
        )
        logPlanIfChanged(plan, currentItem, nextItem, currentAnalysis, nextAnalysis)

        val markable = !plan.blocked &&
            plan.markerVisible &&
            duration > 0L &&
            analysisState.current == TrackAnalysisState.ANALYSED &&
            analysisState.next in MEASURED_ENOUGH_TO_ENTER_ON
        AppSettings.smartTransitionWindow.value = if (markable) {
            TransitionWindow(
                plan = plan,
                startMs = (plan.transitionStart * 1000).roundToLong(),
                endMs = (plan.transitionEnd * 1000).roundToLong(),
                totalMs = duration,
            )
        } else {
            null
        }

        if (plan.blocked) return

        val fade = plan.fadeMs
        if (fade <= 0L) return

        val transitionStartMs = (plan.transitionStart * 1000).roundToLong()
        val remaining = transitionStartMs - player.currentPosition
        if (remaining > ARM_LEAD_MS) return

        begin(
            fade,
            endMs = (plan.transitionEnd * 1000).roundToLong(),
            smart = true,
            cueTimeMs = (plan.incomingCueTime * 1000).roundToLong(),
            playbackRate = plan.incomingPlaybackRate,
            renderStyle = Render(
                style = plan.transitionStyle,
                bassSwap = plan.bassSwap,
                bassSwapFraction = plan.bassSwapFraction,
                filterSweep = plan.filterSweep,
                vocalOverlap = plan.vocalOverlap,
            ),
        )
    }

    private fun logPlanIfChanged(
        plan: TransitionPlan,
        currentItem: MediaItem,
        nextItem: MediaItem,
        currentAnalysis: TrackAnalysis,
        nextAnalysis: TrackAnalysis,
    ) {
        if (plan == lastLoggedPlan) return
        lastLoggedPlan = plan
        val verdict = "${plan.reason}|${plan.transitionStyle}|fade=${plan.fadeMs}" +
            "|cue=${plan.incomingCueTime}|rate=${plan.incomingPlaybackRate}" +
            "|vocalOverlap=${"%.2f".format(plan.vocalOverlap)}" +
            "|blocked=${plan.blocked}|policy=${plan.policyReasons.joinToString(",")}"
        if (verdict == lastPlanVerdict) return

        lastPlanVerdict = verdict
        Log.d(
            TAG,
            "plan ${currentItem.mediaId}->${nextItem.mediaId}: $verdict " +
                "bpm=${currentAnalysis.bpm}/${nextAnalysis.bpm} " +
                "conf=${currentAnalysis.beatConfidence}/${nextAnalysis.beatConfidence}",
        )
    }

    private fun publishAnalysisState() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastAnalysisPublishAt < IDLE_STEP_MS) return
        lastAnalysisPublishAt = now
        val player = active()
        val currentItem = player.currentMediaItem
        val nextIndex = player.nextMediaItemIndex
        val nextItem = if (nextIndex == C.INDEX_UNSET) null else player.getMediaItemAt(nextIndex)
        val currentState = currentItem?.let { stateOf(it, analysisFor(it)) } ?: TrackAnalysisState.WAITING
        val nextState = nextItem?.let { stateOf(it, analysisFor(it)) } ?: TrackAnalysisState.WAITING
        val previous = AppSettings.smartAnalysis.value
        if (previous.current != currentState || previous.next != nextState) {
            AppSettings.smartAnalysis.value = SmartAnalysis(currentState, nextState)
        }
    }

    private fun stateOf(item: MediaItem, analysis: TrackAnalysis): TrackAnalysisState = when {
        analysis.isUsable ->
            if (analysisRunningFor(item)) TrackAnalysisState.REFINING else TrackAnalysisState.ANALYSED
        analysisRunningFor(item) -> TrackAnalysisState.ANALYSING
        analysis.status == TrackAnalysis.STATUS_READY -> TrackAnalysisState.FAILED
        else -> TrackAnalysisState.WAITING
    }

    private fun nextItemDurationMs(nextIndex: Int, item: MediaItem): Long {
        val timeline = active().currentTimeline
        if (!timeline.isEmpty) {
            timeline.getWindow(nextIndex, timelineWindow).durationMs
                .takeIf { it != C.TIME_UNSET && it > 0 }
                ?.let { return it }
        }
        return queuedDurationMs(item)
    }

    private fun queuedDurationMs(item: MediaItem): Long {
        val uri = item.localConfiguration?.uri ?: return 0L
        val seconds = runCatching { uri.getQueryParameter("d") }.getOrNull()?.toLongOrNull() ?: return 0L
        return if (seconds > 0) seconds * 1000L else 0L
    }

    private fun MediaItem.toTransitionInfo(durationMs: Long) = TransitionTrackInfo(
        id = mediaId,
        durationMs = durationMs,
        title = mediaMetadata.title?.toString().orEmpty(),
        artist = mediaMetadata.artist?.toString().orEmpty(),
        album = mediaMetadata.albumTitle?.toString().orEmpty(),
    )

    private fun begin(
        fade: Long,
        endMs: Long,
        smart: Boolean,
        cueTimeMs: Long = 0L,
        playbackRate: Double = 1.0,
        renderStyle: Render = Render(),
    ): Boolean {
        endTail()
        val out = active()
        val into = standby()
        if (out === into) return false
        val nextIndex = out.nextMediaItemIndex
        if (nextIndex == C.INDEX_UNSET) return false

        val outgoingMediaId = out.currentMediaItem?.mediaId
        val incomingMediaId = out.getMediaItemAt(nextIndex).mediaId
        val djPlan = djEngine?.planTransition(
            outgoingMediaId,
            incomingMediaId,
            out.duration.takeIf { it != C.TIME_UNSET } ?: 0L,
        )
        fadeMs = fade
        fadeEndMs = endMs
        smartFadeActive = smart
        incomingCueTimeMs = cueTimeMs.coerceAtLeast(0L)
        val requestedRate = djPlan?.takeIf { it.tier != DjMixTier.PLAIN_CROSSFADE }
            ?.incomingSpeedAdjustment?.toDouble() ?: playbackRate
        incomingPlaybackRate = requestedRate.takeIf { it.isFinite() && it in 0.94..1.06 } ?: 1.0
        lastTempoUpdateAt = 0L
        render = renderStyle
        armDeadline = SystemClock.elapsedRealtime() + ARM_TIMEOUT_MS
        handedOff = false
        outgoing = out
        incoming = into

        val items = (0 until out.mediaItemCount).map { out.getMediaItemAt(it) }
        queuedItemCount = items.size

        Log.d(
            TAG,
            "arm ${if (smart) "smart" else "standard"} fade=${fade}ms end=${endMs}ms " +
                "cue=${incomingCueTimeMs}ms rate=$incomingPlaybackRate at=${out.currentPosition}ms " +
                "style=${render.style} bassSwap=${render.bassSwap}@${render.bassSwapFraction} " +
                "sweep=${render.filterSweep}",
        )

        into.skipSilenceEnabled = out.skipSilenceEnabled
        into.repeatMode = out.repeatMode
        into.shuffleModeEnabled = out.shuffleModeEnabled
        into.playbackParameters = PlaybackParameters((AppSettings.playbackSpeed.value * incomingPlaybackRate).toFloat(), 1f)
        into.volume = 0f
        into.setMediaItems(items, nextIndex, incomingCueTimeMs)
        into.playWhenReady = false
        into.prepare()

        phase = Phase.ARMING
        return true
    }

    private fun driveArming() {
        val out = outgoing ?: return bail()
        val into = incoming ?: return bail()
        if (!stillWorthFading()) return bail()
        if (!out.playWhenReady) return bail()

        val expired = SystemClock.elapsedRealtime() > armDeadline
        val ready = into.playbackState == Player.STATE_READY

        if (expired && !ready) return bail()

        val atFadePoint = fadeEndMs <= 0L || fadeEndMs - out.currentPosition <= fadeMs
        if (!atFadePoint) return
        if (ready) startFade()
    }

    private fun startFade() {
        val outgoingPlayer = outgoing ?: return bail()
        val incomingPlayer = incoming ?: return bail()

        djEngine?.startTransitionEffects(outgoingPlayer, incomingPlayer, fadeMs)
        rideFilters(0f)
        djEngine?.filterFor(incomingPlayer)?.snapToTargets()

        reconcileQueue(outgoingPlayer, incomingPlayer)

        incomingPlayer.volume = 0f
        incomingPlayer.playWhenReady = true

        Log.d(TAG, "handoff at cue=${incomingPlayer.currentPosition}ms out=${outgoingPlayer.currentPosition}ms")

        listenTo(incomingPlayer)
        handedOff = true
        onHandoff(outgoingPlayer, incomingPlayer, fadeMs)

        if (outgoingPlayer.mediaItemCount > outgoingPlayer.currentMediaItemIndex + 1) {
            outgoingPlayer.removeMediaItems(
                outgoingPlayer.currentMediaItemIndex + 1,
                outgoingPlayer.mediaItemCount,
            )
        }

        AppSettings.smartMixInProgress.value = isRealMix()
        AppSettings.smartTransitionWindow.value = null
        phase = Phase.FADING
    }

    private fun reconcileQueue(out: ExoPlayer, into: ExoPlayer) {
        val appended = (queuedItemCount until out.mediaItemCount).map { out.getMediaItemAt(it) }
        if (appended.isEmpty()) return
        into.addMediaItems(appended)
        queuedItemCount = out.mediaItemCount
        Log.d(TAG, "reconciled ${appended.size} appended item(s) onto the incoming player")
    }

    private fun driveFade() {
        val outgoingPlayer = outgoing ?: return bail()
        val incomingPlayer = incoming ?: return bail()
        val remainingIncomingMs = incomingPlayer.duration
            .takeIf { it != C.TIME_UNSET && it > 0L }
            ?.minus(incomingCueTimeMs)
            ?.coerceAtLeast(0L)
        val incomingFadeCapMs = remainingIncomingMs?.div(3) ?: Long.MAX_VALUE
        val fadeDurationMs = minOf(fadeMs, incomingFadeCapMs).coerceAtLeast(1L)
        val elapsedMs = (incomingPlayer.currentPosition - incomingCueTimeMs).coerceAtLeast(0L)
        val fadeProgress = (elapsedMs.toFloat() / fadeDurationMs).coerceIn(0f, 1f)

        incomingPlayer.volume = riseGain(fadeProgress)
        outgoingPlayer.volume = if (djEngine?.holdsTail == true) 1f else fallGain(fadeProgress)
        rideFilters(fadeProgress)
        djEngine?.driveTransitionEffects(outgoingPlayer, incomingPlayer, fadeProgress)
        recoverTempo(incomingPlayer, fadeProgress)

        val settingSwitchedOff = if (smartFadeActive) {
            !AppSettings.smartFadeEnabled.value
        } else {
            configuredFadeMs() <= 0L
        }
        val done = fadeProgress >= 1f ||
            outgoingPlayer.playbackState == Player.STATE_ENDED ||
            outgoingPlayer.playbackState == Player.STATE_IDLE ||
            settingSwitchedOff
        if (done) finish()
    }

    private fun recoverTempo(player: ExoPlayer, progress: Float) {
        if (incomingPlaybackRate == 1.0 || progress < TEMPO_RECOVERY_FROM || !player.isPlaying) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastTempoUpdateAt < TEMPO_UPDATE_MS && progress < 1f) return
        val recovery = ((progress - TEMPO_RECOVERY_FROM) / (1f - TEMPO_RECOVERY_FROM)).coerceIn(0f, 1f)
        val correction = incomingPlaybackRate + (1.0 - incomingPlaybackRate) * recovery
        val speed = (AppSettings.playbackSpeed.value * correction).toFloat()
        val current = player.playbackParameters
        if (abs(current.speed - speed) >= MIN_TEMPO_CHANGE || current.pitch != 1f) {
            player.playbackParameters = PlaybackParameters(speed, 1f)
        }
        lastTempoUpdateAt = now
    }

    private fun driveBail() {
        val out = outgoing
        if (out == null) {
            finish()
            return
        }
        val progress = (SystemClock.elapsedRealtime() - bailStartedAt).toFloat() / BAIL_MS
        if (progress < 1f) {
            out.volume = bailFromGain * fallGain(progress)
            return
        }
        finish()
    }

    private fun bail() {
        if (phase == Phase.IDLE || phase == Phase.BAILING) return
        Log.d(TAG, "bail from $phase")
        AppSettings.smartMixInProgress.value = false
        if (!handedOff) {
            finish()
            return
        }
        transitionFilters.open()
        djEngine?.stopTailEffects(outgoing, incoming)
        incoming?.volume = 1f
        bailFromGain = outgoing?.volume ?: 0f
        bailStartedAt = SystemClock.elapsedRealtime()
        phase = Phase.BAILING
    }

    private fun finish() {
        if (phase != Phase.IDLE) {
            Log.d(TAG, "finish from $phase")
            settledAt = SystemClock.elapsedRealtime()
        }
        AppSettings.smartMixInProgress.value = false
        transitionFilters.open()
        val ringing = handedOff && djEngine?.holdsTail == true
        djEngine?.stopTailEffects(*(if (ringing) arrayOf(incoming) else arrayOf(outgoing, incoming)))
        render = Render()

        if (handedOff) {
            incoming?.let {
                it.volume = 1f
                it.playbackParameters = PlaybackParameters(AppSettings.playbackSpeed.value, 1f)
            }
            outgoing?.let { if (ringing) ringOut(it) else retire(it) }
        } else {
            outgoing?.volume = 1f
            incoming?.let(::retire)
        }

        outgoing = null
        incoming = null
        handedOff = false
        queuedItemCount = 0
        incomingCueTimeMs = 0L
        incomingPlaybackRate = 1.0
        djEngine?.clearActivePlan()
        phase = Phase.IDLE
    }

    private fun stillWorthFading(): Boolean {
        val stillOn = if (smartFadeActive) AppSettings.smartFadeEnabled.value else configuredFadeMs() > 0L
        return stillOn && (outgoing ?: active()).hasNextMediaItem()
    }

    private fun retire(player: ExoPlayer) {
        player.stop()
        player.clearMediaItems()
        player.volume = 1f
        player.playbackParameters = PlaybackParameters(AppSettings.playbackSpeed.value, 1f)
    }

    private fun configuredFadeMs(): Long = (AppSettings.crossfadeSeconds.value * 1000f).toLong()

    private fun fadeFor(duration: Long): Long {
        val configured = configuredFadeMs()
        if (duration == C.TIME_UNSET || duration <= 0L) return configured
        return minOf(configured, duration / 3).coerceAtLeast(0L)
    }

    private fun rideFilters(progress: Float) {
        when (render.style) {
            TransitionStyle.DJ_FILTER -> rideFilterSweep(progress)
            TransitionStyle.DJ_BLEND ->
                if (render.bassSwap) rideBassSwap(progress) else rideVocalSeparation(progress)
            TransitionStyle.GAPLESS -> transitionFilters.open()
            TransitionStyle.EQUAL_POWER ->
                if (djEngine?.activePlan?.tier == DjMixTier.FULL_DJ) rideBassSwap(progress) else rideVocalSeparation(progress)
        }
    }

    private fun rideVocalSeparation(progress: Float) {
        val amount = render.vocalOverlap.coerceIn(0.0, 1.0)
        if (amount <= 0.0) {
            transitionFilters.open()
            return
        }
        val open = TransitionFilterProcessor.OPEN_HZ.toDouble()
        val floor = glide(open, VOCAL_SEPARATION_FLOOR_HZ, amount)
        transitionFilters.outgoing(
            glide(open, floor, progress.toDouble().pow(FILTER_SWEEP_SHAPE)).toFloat(),
            TransitionFilterProcessor.OFF_HZ,
        )
        transitionFilters.incoming(
            TransitionFilterProcessor.OPEN_HZ,
            entryHighPass(progress, amount, VOCAL_SEPARATION_HIGH_PASS_HZ, ENTRY_OPEN_BY),
        )
    }

    private fun rideFilterSweep(progress: Float) {
        val sweep = render.filterSweep.coerceIn(0.0, 1.0)
        if (sweep <= 0.0) {
            transitionFilters.open()
            return
        }
        val open = TransitionFilterProcessor.OPEN_HZ.toDouble()
        val entry = glide(open, FILTER_ENTRY_HZ, sweep)
        val floor = glide(open, FILTER_FLOOR_HZ, sweep)
        val cutoff = glide(entry, floor, progress.toDouble().pow(FILTER_SWEEP_SHAPE))
        transitionFilters.outgoing(cutoff.toFloat(), TransitionFilterProcessor.OFF_HZ)
        transitionFilters.incoming(
            TransitionFilterProcessor.OPEN_HZ,
            entryHighPass(progress, sweep, ENTRY_HIGH_PASS_HZ, ENTRY_OPEN_BY),
        )
    }

    private fun entryHighPass(progress: Float, amount: Double, topHz: Double, openBy: Double): Float {
        val remaining = (1.0 - progress / openBy).coerceIn(0.0, 1.0)
        return glide(TransitionFilterProcessor.OFF_HZ.toDouble(), topHz, amount * remaining.pow(ENTRY_SHAPE))
            .toFloat()
    }

    private fun glide(from: Double, to: Double, amount: Double): Double =
        from * (to / from).pow(amount.coerceIn(0.0, 1.0))

    private fun rideBassSwap(progress: Float) {
        val swapAt = render.bassSwapFraction.coerceIn(0.05, 0.95)
        val handover = ((progress - swapAt) / BASS_SWAP_WIDTH * 0.5 + 0.5).coerceIn(0.0, 1.0)
        val clash = render.vocalOverlap.coerceIn(0.0, 1.0)
        val entry = maxOf(
            bassCutoff(1.0 - ((handover - 0.15) / 0.85).coerceIn(0.0, 1.0)),
            entryHighPass(
                progress,
                1.0,
                glide(BLEND_ENTRY_HIGH_PASS_HZ, BLEND_ENTRY_CLASH_HIGH_PASS_HZ, clash),
                BLEND_ENTRY_OPEN_BY + (BLEND_ENTRY_CLASH_OPEN_BY - BLEND_ENTRY_OPEN_BY) * clash,
            ),
        )
        transitionFilters.incoming(TransitionFilterProcessor.OPEN_HZ, entry)
        transitionFilters.outgoing(blendExitLowPass(progress, clash), bassCutoff((handover / 0.85).coerceIn(0.0, 1.0)))
    }

    private fun blendExitLowPass(progress: Float, clash: Double): Float {
        val from = BLEND_EXIT_FROM + (BLEND_EXIT_CLASH_FROM - BLEND_EXIT_FROM) * clash
        val amount = ((progress - from) / (1.0 - from)).coerceIn(0.0, 1.0)
        val floor = glide(BLEND_EXIT_LOW_PASS_HZ, BLEND_EXIT_CLASH_LOW_PASS_HZ, clash)
        return glide(TransitionFilterProcessor.OPEN_HZ.toDouble(), floor, amount).toFloat()
    }

    private fun bassCutoff(amount: Double): Float =
        glide(TransitionFilterProcessor.OFF_HZ.toDouble(), BASS_SWAP_HZ, amount).toFloat()

    private fun isRealMix(): Boolean = smartFadeActive && (
        render.style == TransitionStyle.DJ_BLEND ||
            render.style == TransitionStyle.DJ_FILTER ||
            incomingCueTimeMs > 0L ||
            incomingPlaybackRate != 1.0
        )

    private fun riseGain(progress: Float): Float =
        sin(progress.coerceIn(0f, 1f) * PI.toFloat() / 2f)

    private fun fallGain(progress: Float): Float =
        cos(progress.coerceIn(0f, 1f) * PI.toFloat() / 2f)

    private companion object {
        const val TAG = "ConvxCrossfade"

        const val DEFAULT_SMART_FALLBACK_SECONDS = 6.0

        const val BAIL_MS = 120L

        const val TAIL_RING_MS = 8_000L

        const val ARM_LEAD_MS = 4_000L

        val MEASURED_ENOUGH_TO_ENTER_ON = setOf(
            TrackAnalysisState.ANALYSED,
            TrackAnalysisState.REFINING,
        )

        const val ARM_TIMEOUT_MS = 12_000L

        const val FILTER_ENTRY_HZ = 7_000.0

        const val FILTER_FLOOR_HZ = 300.0

        const val BASS_SWAP_HZ = 200.0

        const val BASS_SWAP_WIDTH = 0.10

        const val FILTER_SWEEP_SHAPE = 0.75

        const val ENTRY_HIGH_PASS_HZ = 1_200.0

        const val ENTRY_OPEN_BY = 0.6

        const val ENTRY_SHAPE = 0.35

        const val VOCAL_SEPARATION_FLOOR_HZ = 1_600.0

        const val VOCAL_SEPARATION_HIGH_PASS_HZ = 700.0

        const val BLEND_ENTRY_HIGH_PASS_HZ = 520.0

        const val BLEND_ENTRY_OPEN_BY = 0.45

        const val BLEND_ENTRY_CLASH_HIGH_PASS_HZ = 950.0
        const val BLEND_ENTRY_CLASH_OPEN_BY = 0.7

        const val BLEND_EXIT_CLASH_FROM = 0.12
        const val BLEND_EXIT_CLASH_LOW_PASS_HZ = 1_100.0

        const val BLEND_EXIT_FROM = 0.3

        const val BLEND_EXIT_LOW_PASS_HZ = 2_200.0

        const val IDLE_STEP_MS = 250L
        const val DISABLED_STEP_MS = 2_000L
        const val TEMPO_RECOVERY_FROM = 0.8f
        const val TEMPO_UPDATE_MS = 100L
        const val MIN_TEMPO_CHANGE = 0.001f

        const val ARM_STEP_MS = 40L
        const val FADE_STEP_MS = 30L
        const val BAIL_STEP_MS = 15L
    }
}
