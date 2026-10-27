package com.convx.music.client.androidhost

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.DefaultAudioSink
import com.convx.music.playback.AudioStreamResolver
import com.convx.music.modules.isModuleProvider
import com.convx.music.playback.CrossfadeEnvelope
import com.convx.music.playback.PlaybackController
import com.convx.music.playback.PlaybackQueueEntry
import com.convx.music.playback.PlaybackQueueNavigator
import com.convx.music.playback.PlaybackQueueSnapshot
import com.convx.music.playback.PlaybackPhase
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.PlaybackCheckpoint
import com.convx.music.playback.PlaybackCheckpointRepository
import com.convx.music.playback.PlaybackCheckpointWriter
import com.convx.music.playback.PlaybackSleepTimer
import com.convx.music.playback.PlaybackSnapshot
import com.convx.music.playback.PlaybackTrackAnalysisCache
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.convx.music.playback.PlaybackTrackSnapshot
import com.convx.music.playback.analysisIdentity
import com.convx.music.playback.shouldPauseOnToggle
import com.convx.music.playback.planPlaybackTransitionWindow
import com.convx.music.playback.moveQueueIndices
import com.convx.music.playback.smart.TrackAnalysis
import com.convx.music.playback.smart.TransitionStyle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect

/** Android media adapter for the KMP host; all catalog and stream lookup stays behind domain ports. */
@UnstableApi
internal class AndroidPlaybackController(
    context: Context,
    private val streamResolver: AudioStreamResolver,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val playbackCheckpointRepository: PlaybackCheckpointRepository,
    private val trackAnalysisRepository: PlaybackTrackAnalysisRepository,
) : PlaybackController {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val checkpointWriter = PlaybackCheckpointWriter(scope, playbackCheckpointRepository)
    private val trackAnalysisCache = PlaybackTrackAnalysisCache(trackAnalysisRepository, scope)
    private val primaryAnalysisProcessor = createTrackAnalysisProcessor()
    private val secondaryAnalysisProcessor = createTrackAnalysisProcessor()
    private val primaryEqualizerProcessor = PlaybackEqualizerAudioProcessor()
    private val secondaryEqualizerProcessor = PlaybackEqualizerAudioProcessor()
    private val primaryPlayer = createPlayer(context, primaryAnalysisProcessor, primaryEqualizerProcessor)
    private val secondaryPlayer = createPlayer(context, secondaryAnalysisProcessor, secondaryEqualizerProcessor)
    private var activePlayer = primaryPlayer
    private var standbyPlayer = secondaryPlayer
    private val mutableSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY)
    override val playbackSnapshot: StateFlow<PlaybackSnapshot> = mutableSnapshot.asStateFlow()
    override val sleepTimer = PlaybackSleepTimer(scope, System::currentTimeMillis, ::pause)
    private val mutableQueueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
    override val queueSnapshot: StateFlow<PlaybackQueueSnapshot> = mutableQueueSnapshot.asStateFlow()

    private var queue: List<PlaybackRequestTrack> = emptyList()
    private val queueNavigator = PlaybackQueueNavigator()
    private var currentTrack: PlaybackRequestTrack? = null
    private var currentIndex = -1
    private var resolveJob: Job? = null
    private var startupTimeoutJob: Job? = null
    private var progressJob: Job? = null
    private var crossfadePreloadJob: Job? = null
    private var crossfadeJob: Job? = null
    private var crossfadePreloadKey: String? = null
    private var generation = 0L
    private var playbackSettings = PlaybackSettings()
    private var repeatMode = PlaybackRepeatMode.OFF
    private var shuffleEnabled = false
    private var phase = PlaybackPhase.IDLE
    private var shouldPlayAfterResolve = false
    private var errorMessage: String? = null
    private var hasUserPlaybackChanges = false
    private var restoredPositionPendingMs: Long? = null
    private var released = false

    init {
        addPlayerListener(primaryPlayer)
        addPlayerListener(secondaryPlayer)
        scope.launch {
            playbackSettingsRepository.observeSettings().collect { settings ->
                val normalizedSettings = settings.normalized()
                playbackSettings = normalizedSettings
                primaryEqualizerProcessor.updateSettings(normalizedSettings)
                secondaryEqualizerProcessor.updateSettings(normalizedSettings)
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
            currentTrack = queue.getOrNull(currentIndex)
            repeatMode = checkpoint.repeatMode
            shuffleEnabled = checkpoint.shuffleEnabled
            restoredPositionPendingMs = checkpoint.positionMs.takeIf { currentTrack != null && it > 0L }
            queueNavigator.replaceQueue(queue.size, currentIndex, shuffleEnabled)
            publishQueueSnapshot()
            publish()
        }
        progressJob = scope.launch {
            while (isActive) {
                delay(PROGRESS_INTERVAL_MS)
                updateCrossfade()
                publish()
            }
        }
    }

    private fun createTrackAnalysisProcessor() = PlaybackTrackAnalysisAudioProcessor(scope, ::saveTrackAnalysis)

    private fun createPlayer(
        context: Context,
        analysisProcessor: PlaybackTrackAnalysisAudioProcessor,
        equalizerProcessor: PlaybackEqualizerAudioProcessor,
    ): ExoPlayer {
        val appContext = context.applicationContext
        val renderersFactory = object : DefaultRenderersFactory(appContext) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioTrackPlaybackParams: Boolean,
            ) = DefaultAudioSink.Builder(context)
                .setEnableFloatOutput(false)
                .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
                .setAudioProcessorChain(
                    DefaultAudioSink.DefaultAudioProcessorChain(analysisProcessor, equalizerProcessor),
                )
                .build()
        }
        return ExoPlayer.Builder(appContext, renderersFactory).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true,
            )
            setHandleAudioBecomingNoisy(true)
        }
    }

    private fun analysisProcessorFor(player: ExoPlayer): PlaybackTrackAnalysisAudioProcessor =
        if (player === primaryPlayer) primaryAnalysisProcessor else secondaryAnalysisProcessor

    private fun clearMedia(player: ExoPlayer) {
        analysisProcessorFor(player).resetForTrack(null, null)
        player.pause()
        player.clearMediaItems()
        player.setPlaybackParameters(PlaybackParameters(1f, 1f))
    }

    private fun saveTrackAnalysis(
        trackIdentity: String,
        analysis: TrackAnalysis,
    ) {
        if (released) return
        trackAnalysisCache.update(trackIdentity, analysis)
        scope.launch {
            if (released) return@launch
            try {
                trackAnalysisRepository.save(trackIdentity, analysis)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // A failed cache write does not interrupt playback or discard the in-memory result.
            }
        }
    }

    private fun addPlayerListener(source: ExoPlayer) {
        source.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (source !== activePlayer) return
                when (playbackState) {
                    Player.STATE_BUFFERING -> phase = PlaybackPhase.BUFFERING
                    Player.STATE_READY -> {
                        phase = PlaybackPhase.READY
                        restoredPositionPendingMs?.let { restoredPosition ->
                            source.seekTo(restoredPosition.coerceAtMost(source.duration.takeIf { it > 0L } ?: restoredPosition))
                            restoredPositionPendingMs = null
                            if (shouldPlayAfterResolve) source.play()
                        }
                        startupTimeoutJob?.cancel()
                        startupTimeoutJob = null
                    }
                    Player.STATE_ENDED -> {
                        if (sleepTimer.onTrackEnded()) {
                            shouldPlayAfterResolve = false
                            source.pause()
                            phase = PlaybackPhase.ENDED
                            publish()
                            return
                        }
                        if (repeatMode == PlaybackRepeatMode.ONE) {
                            source.seekTo(0L)
                            source.play()
                            return
                        }
                        shouldPlayAfterResolve = false
                        phase = PlaybackPhase.ENDED
                    }
                    Player.STATE_IDLE -> if (currentTrack == null) phase = PlaybackPhase.IDLE
                }
                publish()
                if (playbackState == Player.STATE_ENDED) requestSkip(forward = true)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (source !== activePlayer) return
                if (isPlaying && phase == PlaybackPhase.BUFFERING) phase = PlaybackPhase.READY
                publish()
            }

            override fun onPlayerError(error: PlaybackException) {
                if (source === activePlayer) {
                    fail(error.localizedMessage ?: "The audio stream could not be played.")
                } else {
                    clearMedia(source)
                }
            }
        })
    }

    override fun updateQueue(tracks: List<PlaybackRequestTrack>) {
        if (queue == tracks) return
        hasUserPlaybackChanges = true
        val current = currentTrack
        queue = tracks
        currentIndex = current?.let { track -> queue.indexOfFirst { it.sameTrack(track) } } ?: -1
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
        queue.getOrNull(index)?.let { track ->
            hasUserPlaybackChanges = true
            restoredPositionPendingMs = null
            playTrackAt(track, index)
        }
    }

    override fun playQueue(tracks: List<PlaybackRequestTrack>, startIndex: Int) {
        hasUserPlaybackChanges = true
        updateQueue(tracks)
        queueNavigator.replaceQueue(queue.size, startIndex, shuffleEnabled)
        playQueueItem(startIndex)
    }

    private fun playTrackAt(track: PlaybackRequestTrack, requestedIndex: Int) {
        if (track.providerId != YOUTUBE_MUSIC_PROVIDER && !isModuleProvider(track.providerId)) {
            fail("This music provider isn't supported by the Android host yet.")
            return
        }
        resolveJob?.cancel()
        generation += 1
        val requestGeneration = generation
        val previousIndex = currentIndex
        currentTrack = track
        currentIndex = requestedIndex.takeIf { queue.getOrNull(it)?.sameTrack(track) == true }
            ?: queue.indexOfFirst { it.sameTrack(track) }
        queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
        phase = PlaybackPhase.BUFFERING
        errorMessage = null
        stopCrossfade(clearPrepared = true)
        clearMedia(activePlayer)
        clearMedia(standbyPlayer)
        shouldPlayAfterResolve = true
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
                val stream = withContext(Dispatchers.IO) {
                    streamResolver.resolve(track).getOrElse { throw it }
                }
                if (generation != requestGeneration) return@launch
                analysisProcessorFor(activePlayer).resetForTrack(track.analysisIdentity, track.durationMs)
                activePlayer.setMediaItem(MediaItem.fromUri(stream.url))
                activePlayer.prepare()
                if (shouldPlayAfterResolve && restoredPositionPendingMs == null) activePlayer.play()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                if (generation == requestGeneration) fail(error.message ?: "Couldn't start playback.")
            }
        }
    }

    override fun togglePlayPause() {
        if (playbackSnapshot.value.shouldPauseOnToggle()) pause() else play()
    }

    override fun play() {
        val track = currentTrack ?: return
        if (phase == PlaybackPhase.IDLE || phase == PlaybackPhase.ENDED || errorMessage != null) {
            hasUserPlaybackChanges = true
            playTrackAt(track, currentIndex)
        } else {
            hasUserPlaybackChanges = true
            shouldPlayAfterResolve = true
            errorMessage = null
            activePlayer.play()
            if (crossfadeJob != null) standbyPlayer.play()
            publish()
        }
    }

    override fun pause() {
        hasUserPlaybackChanges = true
        shouldPlayAfterResolve = false
        activePlayer.pause()
        if (crossfadeJob != null) standbyPlayer.pause()
        publish()
        persistCheckpoint(force = true)
    }

    override fun seekTo(positionMs: Long) {
        hasUserPlaybackChanges = true
        val duration = durationMs() ?: return
        stopCrossfade(clearPrepared = true)
        activePlayer.seekTo(positionMs.coerceIn(0L, duration))
        publish()
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
        if (removedCurrent) {
            if (queue.isEmpty()) {
                resolveJob?.cancel()
                generation += 1L
                stopCrossfade(clearPrepared = true)
                clearMedia(activePlayer)
                clearMedia(standbyPlayer)
                currentTrack = null
                currentIndex = -1
                phase = PlaybackPhase.IDLE
                shouldPlayAfterResolve = false
                startupTimeoutJob?.cancel()
                startupTimeoutJob = null
                errorMessage = null
                publishQueueSnapshot()
                publish()
            } else {
                playQueueItem(index.coerceAtMost(queue.lastIndex))
            }
            return
        }
        if (index < currentIndex) currentIndex -= 1
        publishQueueSnapshot()
        publish()
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
            clearMedia(targetPlayer)
            targetPlayer.volume = 0f
            crossfadePreloadJob = scope.launch {
                try {
                    val stream = withContext(Dispatchers.IO) {
                        streamResolver.resolve(target.incoming).getOrElse { throw it }
                    }
                    if (!isCrossfadeTargetCurrent(target) || standbyPlayer !== targetPlayer) return@launch
                    analysisProcessorFor(targetPlayer).resetForTrack(
                        target.incoming.analysisIdentity,
                        target.incoming.durationMs,
                    )
                    targetPlayer.setMediaItem(MediaItem.fromUri(stream.url))
                    targetPlayer.seekTo(target.incomingCuePositionMs)
                    targetPlayer.setPlaybackParameters(
                        PlaybackParameters(target.incomingPlaybackRate.toFloat(), 1f),
                    )
                    targetPlayer.prepare()
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    if (standbyPlayer === targetPlayer) clearMedia(targetPlayer)
                }
            }
        }

        if (activePlayer.isPlaying && activePlayer.currentPosition >= target.startAtMs &&
            standbyPlayer.playbackState == Player.STATE_READY
        ) {
            startCrossfade(target)
        }
    }

    private fun createCrossfadeTarget(): CrossfadeTarget? {
        if (repeatMode == PlaybackRepeatMode.ONE || sleepTimer.state.value.pauseWhenTrackEnds) return null
        val outgoing = currentTrack ?: return null
        val outgoingDurationMs = durationMs() ?: return null
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
        val positionMs = activePlayer.currentPosition.coerceAtLeast(0L)
        val window = planPlaybackTransitionWindow(
            settings = playbackSettings,
            outgoing = outgoing,
            incoming = incoming,
            currentPositionMs = positionMs,
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
        if (crossfadeJob != null || !activePlayer.isPlaying || !isCrossfadeTargetCurrent(target)) return
        val outgoing = activePlayer
        val incoming = standbyPlayer
        if (incoming.playbackState != Player.STATE_READY) return
        activePlayer = incoming
        standbyPlayer = outgoing
        if (!onCrossfadeHandoff(target)) {
            activePlayer = outgoing
            standbyPlayer = incoming
            return
        }
        incoming.volume = 0f
        incoming.play()
        crossfadeJob = scope.launch {
            var elapsedMs = 0L
            while (elapsedMs < target.durationMs && activePlayer === incoming && isActive) {
                val progress = CrossfadeEnvelope.progress(elapsedMs, target.durationMs)
                incoming.volume = CrossfadeEnvelope.incomingGain(progress)
                outgoing.volume = CrossfadeEnvelope.outgoingGain(progress)
                if (incoming.isPlaying) elapsedMs = (elapsedMs + CROSSFADE_FRAME_MS).coerceAtMost(target.durationMs)
                delay(CROSSFADE_FRAME_MS)
            }
            if (activePlayer === incoming) {
                incoming.volume = 1f
                outgoing.volume = 1f
                incoming.setPlaybackParameters(PlaybackParameters(1f, 1f))
                clearMedia(outgoing)
                crossfadeJob = null
                crossfadePreloadKey = null
            }
        }
    }

    private fun onCrossfadeHandoff(target: CrossfadeTarget): Boolean {
        if (!isCrossfadeTargetCurrent(target)) return false
        val previousIndex = currentIndex
        currentTrack = target.incoming
        currentIndex = target.incomingIndex
        queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        generation += 1L
        phase = PlaybackPhase.READY
        errorMessage = null
        publishQueueSnapshot()
        publish()
        return true
    }

    private fun isCrossfadeTargetCurrent(target: CrossfadeTarget): Boolean =
        generation == target.generation &&
            crossfadePreloadKey == target.key &&
            currentTrack?.sameTrack(target.outgoing) == true &&
            queue.getOrNull(target.incomingIndex)?.sameTrack(target.incoming) == true

    private fun clearCrossfadePreload() {
        if (crossfadePreloadKey == null) return
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        clearMedia(standbyPlayer)
    }

    private fun stopCrossfade(clearPrepared: Boolean) {
        crossfadeJob?.cancel()
        crossfadeJob = null
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
        activePlayer.volume = 1f
        standbyPlayer.volume = 1f
        if (clearPrepared) {
            clearMedia(standbyPlayer)
        }
    }

    override fun release() {
        if (released) return
        released = true
        val finalCheckpoint = currentCheckpoint()
        resolveJob?.cancel()
        startupTimeoutJob?.cancel()
        startupTimeoutJob = null
        progressJob?.cancel()
        crossfadePreloadJob?.cancel()
        crossfadeJob?.cancel()
        sleepTimer.release()
        primaryAnalysisProcessor.release()
        secondaryAnalysisProcessor.release()
        primaryPlayer.release()
        secondaryPlayer.release()
        scope.launch {
            try {
                checkpointWriter.flush(finalCheckpoint)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // The synchronous release API has no result channel for persistence failures.
            } finally {
                scope.cancel()
            }
        }
    }

    private fun fail(message: String) {
        startupTimeoutJob?.cancel()
        startupTimeoutJob = null
        stopCrossfade(clearPrepared = true)
        activePlayer.pause()
        shouldPlayAfterResolve = false
        phase = PlaybackPhase.IDLE
        errorMessage = message
        publish()
    }

    private fun durationMs(): Long? = activePlayer.duration.takeIf { it > 0L && it != C.TIME_UNSET }
        ?: currentTrack?.durationMs?.takeIf { it > 0L }

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
        val track = currentTrack ?: run {
            mutableSnapshot.value = PlaybackSnapshot.EMPTY
            return
        }
        val position = restoredPositionPendingMs ?: activePlayer.currentPosition.coerceAtLeast(0L)
        mutableSnapshot.value = PlaybackSnapshot(
            track = PlaybackTrackSnapshot(
                id = track.id,
                title = track.title,
                artists = track.artists,
                providerId = track.providerId,
                artworkUrl = track.artworkUrl,
            ),
            phase = phase,
            playWhenReady = activePlayer.playWhenReady ||
                (phase == PlaybackPhase.BUFFERING && shouldPlayAfterResolve),
            isPlaying = activePlayer.isPlaying,
            positionMs = position,
            durationMs = durationMs(),
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
        positionMs = restoredPositionPendingMs ?: activePlayer.currentPosition.coerceAtLeast(0L),
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

    private companion object {
        const val YOUTUBE_MUSIC_PROVIDER = "youtube_music"
        const val PROGRESS_INTERVAL_MS = 500L
        const val CROSSFADE_FRAME_MS = 50L
        const val CROSSFADE_PRELOAD_LEAD_MS = 8_000L
        const val STARTUP_TIMEOUT_MS = 60_000L
    }
}
