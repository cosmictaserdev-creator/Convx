@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.convx.music.client.ios

import com.convx.music.playback.PlaybackController
import com.convx.music.playback.CrossfadeEnvelope
import com.convx.music.playback.PlaybackQueueEntry
import com.convx.music.playback.PlaybackQueueNavigator
import com.convx.music.playback.PlaybackQueueSnapshot
import com.convx.music.playback.moveQueueIndices
import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackPhase
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.EqualizerFilterType
import com.convx.music.playback.PlaybackCheckpoint
import com.convx.music.playback.PlaybackCheckpointRepository
import com.convx.music.playback.PlaybackCheckpointWriter
import com.convx.music.playback.PlaybackSleepTimer
import com.convx.music.playback.PlaybackSnapshot
import com.convx.music.playback.PlaybackTrackAnalysisCache
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.convx.music.playback.PlaybackTrackSnapshot
import com.convx.music.playback.analysisIdentity
import com.convx.music.playback.planPlaybackTransitionWindow
import com.convx.music.playback.shouldPauseOnToggle
import com.convx.music.playback.smart.TransitionStyle
import com.convx.music.client.ios.avplayercompat.convx_av_player_current_time_seconds
import com.convx.music.client.ios.avplayercompat.convx_av_player_duration_seconds
import com.convx.music.client.ios.avplayercompat.convx_av_player_item_status
import com.convx.music.client.ios.avplayercompat.convx_av_player_pause
import com.convx.music.client.ios.avplayercompat.convx_av_player_play
import com.convx.music.client.ios.avplayercompat.convx_av_player_play_at_rate
import com.convx.music.client.ios.avplayercompat.convx_av_player_replace_item
import com.convx.music.client.ios.avplayercompat.convx_av_player_set_rate
import com.convx.music.client.ios.avplayercompat.convx_av_player_seek_to_seconds
import com.convx.music.client.ios.avplayercompat.convx_av_player_set_volume
import com.convx.music.client.ios.equalizeraudiotapcompat.convx_av_player_set_equalizer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.time.Clock
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.Foundation.NSURL

internal class IosPlaybackController(
    private val streamResolver: AudioStreamResolver,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val playbackCheckpointRepository: PlaybackCheckpointRepository,
    private val trackAnalysisRepository: PlaybackTrackAnalysisRepository,
) : PlaybackController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val checkpointWriter = PlaybackCheckpointWriter(scope, playbackCheckpointRepository)
    private val trackAnalysisCache = PlaybackTrackAnalysisCache(trackAnalysisRepository, scope)
    private val primaryPlayer = AVPlayer()
    private val secondaryPlayer = AVPlayer()
    private val trackAnalysisCollector = IosPlaybackTrackAnalysisCollector(
        primaryPlayer = primaryPlayer,
        secondaryPlayer = secondaryPlayer,
        trackAnalysisRepository = trackAnalysisRepository,
        trackAnalysisCache = trackAnalysisCache,
        scope = scope,
    )
    private var activePlayer = primaryPlayer
    private var standbyPlayer = secondaryPlayer
    private val _playbackSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY)
    override val playbackSnapshot: StateFlow<PlaybackSnapshot> = _playbackSnapshot.asStateFlow()
    override val sleepTimer = PlaybackSleepTimer(
        scope = scope,
        nowMillis = { Clock.System.now().toEpochMilliseconds() },
        pausePlayback = ::pause,
    )
    private val mutableQueueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
    override val queueSnapshot: StateFlow<PlaybackQueueSnapshot> = mutableQueueSnapshot.asStateFlow()

    private var queue: List<PlaybackRequestTrack> = emptyList()
    private val queueNavigator = PlaybackQueueNavigator()
    private var currentIndex = -1
    private var currentRequest: PlaybackRequestTrack? = null
    private var resolveJob: Job? = null
    private var startupTimeoutJob: Job? = null
    private var crossfadePreloadJob: Job? = null
    private var crossfadeJob: Job? = null
    private var crossfadePreloadKey: String? = null
    private var standbyCueKey: String? = null
    private var generation = 0L
    private var primaryGain = 1.0
    private var secondaryGain = 1.0
    private var playbackSettings = PlaybackSettings()
    private var repeatMode = PlaybackRepeatMode.OFF
    private var shuffleEnabled = false
    private var phase = PlaybackPhase.IDLE
    private var shouldPlayAfterResolve = false
    private var isPlaying = false
    private var positionMs = 0L
    private var durationMs: Long? = null
    private var errorMessage: String? = null
    private var playbackMonitor: Job? = null
    private var hasUserPlaybackChanges = false
    private var restoredPositionPendingMs: Long? = null
    private var released = false

    private fun setDeckGain(player: AVPlayer, gain: Double) {
        if (player === primaryPlayer) primaryGain = gain else secondaryGain = gain
        val master = if (playbackSettings.bitPerfectEnabled) 1.0 else playbackSettings.volume.toDouble()
        convx_av_player_set_volume(player, (gain * master).coerceIn(0.0, 1.0))
    }

    private fun applyMasterVolume() {
        setDeckGain(primaryPlayer, primaryGain)
        setDeckGain(secondaryPlayer, secondaryGain)
    }

    init {
        setDeckGain(primaryPlayer, 1.0)
        setDeckGain(secondaryPlayer, 1.0)
        scope.launch {
            playbackSettingsRepository.observeSettings().collect { settings ->
                playbackSettings = settings.normalized()
                applyMasterVolume()
                applyEqualizerToReadyPlayers(playbackSettings)
            }
        }
        scope.launch {
            val checkpoint = try {
                playbackCheckpointRepository.getCheckpoint()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                return@launch
            }
            if (hasUserPlaybackChanges) return@launch
            queue = checkpoint.queue
            currentIndex = checkpoint.currentIndex
            currentRequest = queue.getOrNull(currentIndex)
            repeatMode = checkpoint.repeatMode
            shuffleEnabled = checkpoint.shuffleEnabled
            positionMs = checkpoint.positionMs
            restoredPositionPendingMs = checkpoint.positionMs.takeIf { currentRequest != null && it > 0L }
            queueNavigator.replaceQueue(queue.size, currentIndex, shuffleEnabled)
            publishQueueSnapshot()
            publish()
        }
    }

    override fun updateQueue(tracks: List<PlaybackRequestTrack>) {
        if (queue == tracks) return
        hasUserPlaybackChanges = true
        val current = currentRequest
        queue = tracks.toList()
        currentIndex = current?.let { currentTrack -> queue.indexOfFirst { it.sameTrack(currentTrack) } } ?: -1
        queueNavigator.replaceQueue(queue.size, currentIndex, shuffleEnabled)
        publishQueueSnapshot()
        publish()
    }

    override fun appendQueue(tracks: List<PlaybackRequestTrack>) {
        val existing = queue.mapTo(mutableSetOf()) { it.providerId to it.id }
        val additions = tracks.filter { (it.providerId to it.id) !in existing }
        if (additions.isEmpty()) return
        hasUserPlaybackChanges = true
        val previousCount = queue.size
        queue = queue + additions
        queueNavigator.append(previousCount, additions.size, shuffleEnabled)
        publishQueueSnapshot()
        publish()
    }

    override fun playTrack(track: PlaybackRequestTrack) {
        hasUserPlaybackChanges = true
        restoredPositionPendingMs = null
        playTrackAt(track, queue.indexOfFirst { it.sameTrack(track) })
    }

    override fun playQueueItem(index: Int) {
        val track = queue.getOrNull(index) ?: return
        hasUserPlaybackChanges = true
        restoredPositionPendingMs = null
        playTrackAt(track, index)
    }

    override fun playQueue(tracks: List<PlaybackRequestTrack>, startIndex: Int) {
        hasUserPlaybackChanges = true
        updateQueue(tracks)
        queueNavigator.replaceQueue(queue.size, startIndex, shuffleEnabled)
        playQueueItem(startIndex)
    }

    private fun playTrackAt(track: PlaybackRequestTrack, queueIndex: Int) {
        val previousIndex = currentIndex
        resolveJob?.cancel()
        generation += 1L
        val requestGeneration = generation
        currentRequest = track
        currentIndex = queueIndex.takeIf { queue.getOrNull(it)?.sameTrack(track) == true }
            ?: queue.indexOfFirst { it.sameTrack(track) }
        queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
        positionMs = restoredPositionPendingMs ?: 0L
        durationMs = track.durationMs
        phase = PlaybackPhase.BUFFERING
        shouldPlayAfterResolve = true
        isPlaying = false
        errorMessage = null
        stopCrossfade(clearPrepared = true)
        trackAnalysisCollector.setTrack(activePlayer, track)
        trackAnalysisCollector.setTrack(standbyPlayer, null)
        convx_av_player_pause(activePlayer)
        convx_av_player_replace_item(activePlayer, null)
        convx_av_player_pause(standbyPlayer)
        convx_av_player_replace_item(standbyPlayer, null)
        publishQueueSnapshot()
        publish()

        startupTimeoutJob?.cancel()
        startupTimeoutJob = scope.launch {
            delay(STARTUP_TIMEOUT_MS)
            if (generation == requestGeneration && phase == PlaybackPhase.BUFFERING) {
                generation += 1L
                resolveJob?.cancel()
                fail("Playback timed out while opening the audio stream.")
            }
        }
        resolveJob = scope.launch {
            try {
                val stream = streamResolver.resolve(track).getOrElse { throw it }
                if (generation != requestGeneration) return@launch
                val url = NSURL.URLWithString(stream.url)
                if (url == null) {
                    fail("The audio stream returned an invalid URL.")
                    return@launch
                }
                setDeckGain(activePlayer, 1.0)
                convx_av_player_replace_item(activePlayer, AVPlayerItem(uRL = url))
                isPlaying = false
                phase = PlaybackPhase.BUFFERING
                publish()
                monitorPlayback(requestGeneration)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                if (generation == requestGeneration) {
                    fail(error.message ?: "Couldn't start playback.")
                }
            }
        }
    }

    override fun togglePlayPause() {
        if (playbackSnapshot.value.shouldPauseOnToggle()) pause() else play()
    }

    override fun play() {
        val current = currentRequest ?: return
        if (phase == PlaybackPhase.IDLE || phase == PlaybackPhase.ENDED || errorMessage != null) {
            hasUserPlaybackChanges = true
            playTrackAt(current, currentIndex)
        } else {
            hasUserPlaybackChanges = true
            shouldPlayAfterResolve = true
            if (phase == PlaybackPhase.READY) {
                convx_av_player_play(activePlayer)
                if (crossfadeJob != null) convx_av_player_play(standbyPlayer)
            }
            isPlaying = phase == PlaybackPhase.READY
            errorMessage = null
            publish()
        }
    }

    override fun pause() {
        hasUserPlaybackChanges = true
        shouldPlayAfterResolve = false
        convx_av_player_pause(activePlayer)
        if (crossfadeJob != null) convx_av_player_pause(standbyPlayer)
        isPlaying = false
        publish()
        persistCheckpoint(force = true)
    }

    override fun seekTo(positionMs: Long) {
        hasUserPlaybackChanges = true
        val position = positionMs.coerceAtLeast(0L)
        stopCrossfade(clearPrepared = true)
        trackAnalysisCollector.invalidate(activePlayer)
        convx_av_player_seek_to_seconds(activePlayer, position / 1_000.0)
        applyEqualizer(activePlayer, playbackSettings)
        this.positionMs = durationMs?.let(position::coerceAtMost) ?: position
        publish()
        persistCheckpoint(force = true)
    }

    override fun requestSkip(forward: Boolean) {
        val nextIndex = queueNavigator.nextIndex(
            currentIndex = currentIndex,
            itemCount = queue.size,
            forward = forward,
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
        ) ?: return
        playQueueItem(nextIndex)
    }

    override fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        val order = moveQueueIndices(queue.indices.toList(), fromIndex, toIndex)
        if (order == queue.indices.toList()) return
        hasUserPlaybackChanges = true
        val oldCurrentIndex = currentIndex
        queue = order.map(queue::get)
        currentIndex = order.indexOf(oldCurrentIndex).takeIf { oldCurrentIndex >= 0 } ?: -1
        queueNavigator.move(fromIndex, toIndex, queue.size, shuffleEnabled)
        publishQueueSnapshot()
        publish()
    }

    override fun removeQueueItem(index: Int) {
        if (index !in queue.indices) return
        hasUserPlaybackChanges = true
        val removedCurrent = index == currentIndex
        queue = queue.toMutableList().apply { removeAt(index) }
        queueNavigator.remove(index, queue.size, shuffleEnabled)
        if (!removedCurrent) {
            if (index < currentIndex) currentIndex -= 1
            publishQueueSnapshot()
            publish()
            return
        }

        if (queue.isEmpty()) {
            resolveJob?.cancel()
            startupTimeoutJob?.cancel()
            startupTimeoutJob = null
            generation += 1L
            stopCrossfade(clearPrepared = true)
            trackAnalysisCollector.setTrack(activePlayer, null)
            trackAnalysisCollector.setTrack(standbyPlayer, null)
            convx_av_player_pause(activePlayer)
            convx_av_player_replace_item(activePlayer, null)
            convx_av_player_pause(standbyPlayer)
            convx_av_player_replace_item(standbyPlayer, null)
            currentRequest = null
            currentIndex = -1
            phase = PlaybackPhase.IDLE
            shouldPlayAfterResolve = false
            isPlaying = false
            positionMs = 0L
            durationMs = null
            errorMessage = null
            publishQueueSnapshot()
            publish()
        } else {
            playQueueItem(index.coerceAtMost(queue.lastIndex))
        }
    }

    override fun removeQueueItemsAfterCurrent() {
        val retainedCount = (currentIndex + 1).coerceAtLeast(0)
        if (retainedCount >= queue.size) return
        hasUserPlaybackChanges = true
        queue = queue.take(retainedCount)
        queueNavigator.truncate(retainedCount, shuffleEnabled)
        publishQueueSnapshot()
        publish()
    }

    override fun setRepeatMode(mode: PlaybackRepeatMode) {
        hasUserPlaybackChanges = true
        repeatMode = mode
        publish()
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        if (shuffleEnabled == enabled) return
        hasUserPlaybackChanges = true
        shuffleEnabled = enabled
        queueNavigator.setShuffleEnabled(enabled, queue.size, currentIndex)
        publish()
    }

    override fun release() {
        if (released) return
        released = true
        val finalCheckpoint = currentCheckpoint()
        resolveJob?.cancel()
        startupTimeoutJob?.cancel()
        startupTimeoutJob = null
        playbackMonitor?.cancel()
        crossfadePreloadJob?.cancel()
        crossfadeJob?.cancel()
        playbackMonitor = null
        sleepTimer.release()
        trackAnalysisCollector.release()
        convx_av_player_pause(primaryPlayer)
        convx_av_player_replace_item(primaryPlayer, null)
        convx_av_player_pause(secondaryPlayer)
        convx_av_player_replace_item(secondaryPlayer, null)
        scope.launch {
            try {
                checkpointWriter.flush(finalCheckpoint)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // The synchronous release API has no result channel for persistence failures.
            } finally {
                trackAnalysisCollector.awaitTermination()
                scope.cancel()
            }
        }
    }

    private fun fail(message: String) {
        startupTimeoutJob?.cancel()
        startupTimeoutJob = null
        stopCrossfade(clearPrepared = true)
        trackAnalysisCollector.setTrack(activePlayer, null)
        convx_av_player_pause(activePlayer)
        shouldPlayAfterResolve = false
        isPlaying = false
        phase = PlaybackPhase.IDLE
        errorMessage = message
        publish()
    }

    private fun updateCrossfade() {
        if (crossfadeJob != null) return
        val target = createCrossfadeTarget()
        if (target == null) {
            clearCrossfadePreload()
            return
        }

        if (crossfadePreloadKey != target.key) {
            crossfadePreloadJob?.cancel()
            crossfadePreloadKey = target.key
            val targetPlayer = standbyPlayer
            convx_av_player_pause(targetPlayer)
            trackAnalysisCollector.setTrack(targetPlayer, null)
            convx_av_player_replace_item(targetPlayer, null)
            setDeckGain(targetPlayer, 0.0)
            crossfadePreloadJob = scope.launch {
                try {
                    val stream = streamResolver.resolve(target.incoming)
                        .getOrElse { throw it }
                    if (!isCrossfadeTargetCurrent(target) || standbyPlayer !== targetPlayer) return@launch
                    val url = NSURL.URLWithString(stream.url) ?: return@launch
                    trackAnalysisCollector.setTrack(targetPlayer, target.incoming)
                    convx_av_player_replace_item(targetPlayer, AVPlayerItem(uRL = url))
                    setDeckGain(targetPlayer, 0.0)
                    standbyCueKey = null
                    crossfadePreloadJob = null
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    // Keep the outgoing deck playing; end-of-track handling will perform a hard handoff.
                }
            }
        }

        val standbyReady = convx_av_player_item_status(standbyPlayer) == AVPlayerItemStatusReadyToPlay
        if (standbyReady && trackAnalysisCollector.needsProcessing(standbyPlayer)) {
            applyEqualizer(standbyPlayer, playbackSettings)
        }
        if (isPlaying && positionMs >= target.startAtMs && standbyReady) {
            if (target.incomingCuePositionMs > 0L) {
                if (standbyCueKey != target.key) {
                    trackAnalysisCollector.invalidate(standbyPlayer)
                    convx_av_player_seek_to_seconds(
                        standbyPlayer,
                        target.incomingCuePositionMs / 1_000.0,
                    )
                    applyEqualizer(standbyPlayer, playbackSettings)
                    standbyCueKey = target.key
                    return
                }
                val actualCueMs = (convx_av_player_current_time_seconds(standbyPlayer) * 1_000.0).toLong()
                if (kotlin.math.abs(actualCueMs - target.incomingCuePositionMs) > CUE_SEEK_TOLERANCE_MS) return
            }
            startCrossfade(target)
        }
    }

    private fun createCrossfadeTarget(): CrossfadeTarget? {
        if (repeatMode == PlaybackRepeatMode.ONE || sleepTimer.state.value.pauseWhenTrackEnds) return null
        val outgoing = currentRequest ?: return null
        val outgoingDurationMs = durationMs?.takeIf { it > 0L } ?: return null
        val incomingIndex = queueNavigator.nextIndex(
            currentIndex = currentIndex,
            itemCount = queue.size,
            forward = true,
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
        ) ?: return null
        if (incomingIndex == currentIndex) return null
        val incoming = queue.getOrNull(incomingIndex) ?: return null
        val outgoingIdentity = outgoing.analysisIdentity
        val incomingIdentity = incoming.analysisIdentity
        trackAnalysisCache.preload(outgoingIdentity)
        trackAnalysisCache.preload(incomingIdentity)
        val window = planPlaybackTransitionWindow(
            settings = playbackSettings,
            outgoing = outgoing,
            incoming = incoming,
            currentPositionMs = positionMs.coerceAtLeast(0L),
            outgoingDurationMs = outgoingDurationMs,
            outgoingAnalysis = trackAnalysisCache.analysisFor(outgoingIdentity),
            incomingAnalysis = trackAnalysisCache.analysisFor(incomingIdentity),
        ) ?: return null
        val prepareAtMs = (window.startAtMs - CROSSFADE_PRELOAD_LEAD_MS).coerceAtLeast(0L)
        if (positionMs < prepareAtMs) return null
        return CrossfadeTarget(
            generation = generation,
            outgoing = outgoing,
            incoming = incoming,
            incomingIndex = incomingIndex,
            durationMs = window.durationMs,
            startAtMs = window.startAtMs,
            incomingCuePositionMs = window.incomingCuePositionMs,
            incomingPlaybackRate = window.incomingPlaybackRate,
            transitionStyle = window.style,
            key = "$generation:$incomingIndex:${incoming.providerId}:${incoming.id}:${window.startAtMs}:${window.incomingCuePositionMs}:${window.incomingPlaybackRate}:${window.style}",
        )
    }

    private fun startCrossfade(target: CrossfadeTarget) {
        if (crossfadeJob != null || !isPlaying || !isCrossfadeTargetCurrent(target)) return
        if (convx_av_player_item_status(standbyPlayer) != AVPlayerItemStatusReadyToPlay) return
        if (trackAnalysisCollector.needsProcessing(standbyPlayer)) {
            applyEqualizer(standbyPlayer, playbackSettings)
        }
        val outgoing = activePlayer
        val incoming = standbyPlayer
        activePlayer = incoming
        standbyPlayer = outgoing
        if (!onCrossfadeHandoff(target)) {
            activePlayer = outgoing
            standbyPlayer = incoming
            return
        }
        setDeckGain(incoming, 0.0)
        convx_av_player_play_at_rate(incoming, target.incomingPlaybackRate)
        crossfadeJob = scope.launch {
            var elapsedMs = 0L
            while (elapsedMs < target.durationMs && activePlayer === incoming && isActive) {
                val progress = CrossfadeEnvelope.progress(elapsedMs, target.durationMs)
                setDeckGain(incoming, CrossfadeEnvelope.incomingGain(progress).toDouble())
                setDeckGain(outgoing, CrossfadeEnvelope.outgoingGain(progress).toDouble())
                if (isPlaying) elapsedMs = (elapsedMs + CROSSFADE_FRAME_MS).coerceAtMost(target.durationMs)
                delay(CROSSFADE_FRAME_MS)
            }
            if (activePlayer === incoming) {
                setDeckGain(incoming, 1.0)
                setDeckGain(outgoing, 1.0)
                convx_av_player_set_rate(incoming, 1.0)
                convx_av_player_pause(outgoing)
                trackAnalysisCollector.setTrack(outgoing, null)
                convx_av_player_replace_item(outgoing, null)
                crossfadeJob = null
                crossfadePreloadKey = null
            }
        }
    }

    private fun onCrossfadeHandoff(target: CrossfadeTarget): Boolean {
        if (!isCrossfadeTargetCurrent(target)) return false
        val previousIndex = currentIndex
        currentRequest = target.incoming
        currentIndex = target.incomingIndex
        queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        positionMs = 0L
        durationMs = target.incoming.durationMs
        isPlaying = true
        phase = PlaybackPhase.READY
        errorMessage = null
        publishQueueSnapshot()
        publish()
        return true
    }

    private fun isCrossfadeTargetCurrent(target: CrossfadeTarget): Boolean =
        generation == target.generation &&
            crossfadePreloadKey == target.key &&
            currentRequest?.sameTrack(target.outgoing) == true &&
            queue.getOrNull(target.incomingIndex)?.sameTrack(target.incoming) == true

    private fun clearCrossfadePreload() {
        if (crossfadePreloadKey == null) return
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        standbyCueKey = null
        convx_av_player_pause(standbyPlayer)
        trackAnalysisCollector.setTrack(standbyPlayer, null)
        convx_av_player_replace_item(standbyPlayer, null)
        setDeckGain(standbyPlayer, 1.0)
    }

    private fun stopCrossfade(clearPrepared: Boolean) {
        crossfadeJob?.cancel()
        crossfadeJob = null
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        setDeckGain(activePlayer, 1.0)
        setDeckGain(standbyPlayer, 1.0)
        if (clearPrepared) {
            standbyCueKey = null
            convx_av_player_pause(standbyPlayer)
            trackAnalysisCollector.setTrack(standbyPlayer, null)
            convx_av_player_replace_item(standbyPlayer, null)
        }
    }

    private data class CrossfadeTarget(
        val generation: Long,
        val outgoing: PlaybackRequestTrack,
        val incoming: PlaybackRequestTrack,
        val incomingIndex: Int,
        val durationMs: Long,
        val startAtMs: Long,
        val incomingCuePositionMs: Long,
        val incomingPlaybackRate: Double,
        val transitionStyle: TransitionStyle,
        val key: String,
    )

    private fun publish() {
        val track = currentRequest
        _playbackSnapshot.value = if (track == null) PlaybackSnapshot.EMPTY else PlaybackSnapshot(
            track = PlaybackTrackSnapshot(
                id = track.id,
                title = track.title,
                artists = track.artists,
                providerId = track.providerId,
                artworkUrl = track.artworkUrl,
            ),
            phase = phase,
            playWhenReady = shouldPlayAfterResolve,
            isPlaying = isPlaying,
            positionMs = positionMs,
            durationMs = durationMs,
            mediaItemIndex = currentIndex,
            mediaItemCount = queue.size,
            repeatMode = repeatMode,
            shuffleEnabled = shuffleEnabled,
            canSkipPrevious = currentIndex > 0 || repeatMode == PlaybackRepeatMode.ALL,
            canSkipNext = currentIndex in queue.indices &&
                (currentIndex < queue.lastIndex || repeatMode == PlaybackRepeatMode.ALL),
            errorMessage = errorMessage,
        )
        persistCheckpoint()
    }

    private fun persistCheckpoint(force: Boolean = false) {
        if (released) return
        checkpointWriter.save(currentCheckpoint(), force = force)
    }

    private fun currentCheckpoint() = PlaybackCheckpoint(
        queue = queue,
        currentIndex = currentIndex,
        positionMs = positionMs,
        repeatMode = repeatMode,
        shuffleEnabled = shuffleEnabled,
    )

    private fun publishQueueSnapshot() {
        mutableQueueSnapshot.value = PlaybackQueueSnapshot(
            entries = queue.map { track ->
                PlaybackQueueEntry(
                    id = track.id,
                    title = track.title,
                    artists = track.artists,
                    artworkUrl = track.artworkUrl,
                    providerId = track.providerId,
                    durationMs = track.durationMs,
                    origin = track.queueOrigin,
                )
            },
            currentIndex = currentIndex,
        )
    }

    private fun PlaybackRequestTrack.sameTrack(other: PlaybackRequestTrack): Boolean =
        providerId == other.providerId && id == other.id

    private fun monitorPlayback(requestGeneration: Long) {
        playbackMonitor?.cancel()
        playbackMonitor = scope.launch {
            while (isActive && generation == requestGeneration) {
                delay(if (phase == PlaybackPhase.BUFFERING) BUFFERING_POLL_MS else PLAYBACK_POLL_MS)
                if (generation != requestGeneration) break

                val itemStatus = convx_av_player_item_status(activePlayer)
                if (itemStatus == AVPlayerItemStatusFailed) {
                    fail("The audio stream could not be played.")
                    break
                }

                val positionSeconds = convx_av_player_current_time_seconds(activePlayer)
                if (positionSeconds.isFinite() && positionSeconds >= 0.0) {
                    positionMs = (positionSeconds * 1_000.0).toLong()
                }
                val durationSeconds = convx_av_player_duration_seconds(activePlayer)
                if (durationSeconds.isFinite() && durationSeconds > 0.0) {
                    durationMs = (durationSeconds * 1_000.0).toLong()
                }
                if (itemStatus == AVPlayerItemStatusReadyToPlay && phase == PlaybackPhase.BUFFERING) {
                    applyEqualizer(activePlayer, playbackSettings)
                    phase = PlaybackPhase.READY
                    isPlaying = shouldPlayAfterResolve
                    startupTimeoutJob?.cancel()
                    startupTimeoutJob = null
                    restoredPositionPendingMs?.let { restoredPosition ->
                        trackAnalysisCollector.invalidate(activePlayer)
                        convx_av_player_seek_to_seconds(activePlayer, restoredPosition / 1_000.0)
                        applyEqualizer(activePlayer, playbackSettings)
                        positionMs = restoredPosition.coerceAtMost(durationMs ?: restoredPosition)
                        restoredPositionPendingMs = null
                    }
                    if (shouldPlayAfterResolve) convx_av_player_play(activePlayer)
                }
                updateCrossfade()
                trackAnalysisCollector.ensureRun(activePlayer)
                trackAnalysisCollector.drain(activePlayer)
                trackAnalysisCollector.ensureRun(standbyPlayer)
                trackAnalysisCollector.drain(standbyPlayer)
                publish()

                val totalDuration = durationMs
                if (totalDuration != null && positionMs >= totalDuration && isPlaying) {
                    trackAnalysisCollector.finish(activePlayer)
                    if (sleepTimer.onTrackEnded()) {
                        phase = PlaybackPhase.ENDED
                        shouldPlayAfterResolve = false
                        isPlaying = false
                        publish()
                        break
                    }
                    if (repeatMode == PlaybackRepeatMode.ONE) {
                        seekTo(0L)
                        convx_av_player_play(activePlayer)
                    } else {
                        phase = PlaybackPhase.ENDED
                        shouldPlayAfterResolve = false
                        isPlaying = false
                        publish()
                        requestSkip(forward = true)
                        break
                    }
                }
            }
        }
    }

    private companion object {
        const val CUE_SEEK_TOLERANCE_MS = 150L
        const val CROSSFADE_FRAME_MS = 50L
        const val CROSSFADE_PRELOAD_LEAD_MS = 8_000L
        const val STARTUP_TIMEOUT_MS = 60_000L
        const val BUFFERING_POLL_MS = 100L
        const val PLAYBACK_POLL_MS = 500L
    }

    private fun applyEqualizerToReadyPlayers(settings: PlaybackSettings) {
        applyEqualizer(primaryPlayer, settings)
        applyEqualizer(secondaryPlayer, settings)
    }

    private fun applyEqualizer(player: AVPlayer, settings: PlaybackSettings) {
        if (convx_av_player_item_status(player) != AVPlayerItemStatusReadyToPlay) return
        if (trackAnalysisCollector.hasActiveRun(player)) {
            trackAnalysisCollector.invalidate(player)
        }
        if (settings.bitPerfectEnabled) {
            trackAnalysisCollector.invalidate(player)
        } else {
            trackAnalysisCollector.drain(player)
        }
        val profile = settings.equalizerProfile
        val bands = profile.bands.filter { band ->
            band.enabled && (band.gain != 0.0 || band.filterType == EqualizerFilterType.LPQ || band.filterType == EqualizerFilterType.HPQ)
        }
        val arraySize = bands.size.coerceAtLeast(1)
        val frequencies = DoubleArray(arraySize)
        val gains = DoubleArray(arraySize)
        val qualities = DoubleArray(arraySize)
        val filterTypes = IntArray(arraySize)
        bands.forEachIndexed { index, band ->
            frequencies[index] = band.frequency
            gains[index] = band.gain
            qualities[index] = band.q
            filterTypes[index] = band.filterType.ordinal
        }
        val captureAnalysis = !settings.bitPerfectEnabled && trackAnalysisCollector.shouldCapture(player)
        val applied = frequencies.usePinned { pinnedFrequencies ->
            gains.usePinned { pinnedGains ->
                qualities.usePinned { pinnedQualities ->
                    filterTypes.usePinned { pinnedFilterTypes ->
                        convx_av_player_set_equalizer(
                            player,
                            if (settings.equalizerEnabled && !settings.bitPerfectEnabled) 1 else 0,
                            profile.preamp,
                            pinnedFrequencies.addressOf(0),
                            pinnedGains.addressOf(0),
                            pinnedQualities.addressOf(0),
                            pinnedFilterTypes.addressOf(0),
                            bands.size,
                            if (captureAnalysis) 1 else 0,
                        )
                    }
                }
            }
        }
        trackAnalysisCollector.markProcessingInstalled(player)
        if (captureAnalysis && applied == 0) {
            trackAnalysisCollector.invalidate(player)
        } else if (captureAnalysis) {
            trackAnalysisCollector.ensureRun(player)
        }
    }
}
