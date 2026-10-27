package com.convx.music.client.desktop

import androidx.compose.ui.graphics.Color
import com.convx.music.catalog.CatalogEntry
import com.convx.music.playback.AudioStreamResolver
import com.convx.music.playback.PlaybackController
import com.convx.music.playback.PlaybackAudioOutput
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
import com.convx.music.playback.moveQueueIndices
import com.convx.music.playback.planPlaybackTransitionWindow
import com.convx.music.playback.smart.TransitionStyle
import com.convx.music.playback.shouldPauseOnToggle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.file.Path

internal data class DesktopTrack(
    val catalogTrack: CatalogEntry.Track,
    val request: PlaybackRequestTrack,
    val album: String,
    val durationMs: Long,
    val isInLibrary: Boolean,
    val artStart: Color,
    val artEnd: Color,
    val artworkUrl: String?,
) {
    val snapshot = PlaybackTrackSnapshot(
        id = request.id,
        title = request.title,
        artists = request.artists,
        providerId = request.providerId,
        artworkUrl = request.artworkUrl,
    )
}

internal fun CatalogEntry.Track.toDesktopTrack(index: Int): DesktopTrack {
    val palette = listOf(
        Color(0xFF745B9C) to Color(0xFFE49E9B),
        Color(0xFF245E77) to Color(0xFF8BC0B7),
        Color(0xFF885E66) to Color(0xFFF0B889),
        Color(0xFF466C57) to Color(0xFFCFD98B),
        Color(0xFF394574) to Color(0xFFAC8DD0),
    )
    val colors = palette[index.mod(palette.size)]
    val duration = durationMillis
    return DesktopTrack(
        catalogTrack = this,
        request = PlaybackRequestTrack(
            providerId = id.providerId,
            id = id.value,
            title = title,
            artists = artists.map { it.name },
            durationMs = duration,
            artworkUrl = artworkUrls.firstOrNull(),
        ),
        album = album?.title.orEmpty(),
        durationMs = duration ?: 0L,
        isInLibrary = isInLibrary ?: false,
        artStart = colors.first,
        artEnd = colors.second,
        artworkUrl = artworkUrls.firstOrNull(),
    )
}

internal interface DesktopMusicController : PlaybackController {
    fun setVolume(volume: Float)
    fun selectTrack(trackId: String)
    fun setCatalogTracks(tracks: List<DesktopTrack>)
    fun close()
}

internal class DesktopPlaybackController(
    private val streamResolver: AudioStreamResolver,
    private val playbackSettingsRepository: PlaybackSettingsRepository,
    private val playbackCheckpointRepository: PlaybackCheckpointRepository,
    private val trackAnalysisRepository: PlaybackTrackAnalysisRepository,
    private val audioProxy: DesktopAudioHttpProxy = DesktopAudioHttpProxy(),
    private val audioTranscoder: DesktopAudioTranscoder = DesktopAudioTranscoder(),
) : DesktopMusicController {
    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val checkpointWriter = PlaybackCheckpointWriter(scope, playbackCheckpointRepository)
    private val trackAnalysisCache = PlaybackTrackAnalysisCache(trackAnalysisRepository, scope)
    private val analysesInFlight = mutableSetOf<String>()
    private val _playbackSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY)
    override val playbackSnapshot: StateFlow<PlaybackSnapshot> = _playbackSnapshot.asStateFlow()
    override val sleepTimer = PlaybackSleepTimer(scope, System::currentTimeMillis, ::pause)
    private val mutableQueueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
    override val queueSnapshot: StateFlow<PlaybackQueueSnapshot> = mutableQueueSnapshot.asStateFlow()
    private val audio = DesktopStreamPlayer(
        onReady = ::onAudioReady,
        onProgress = ::onAudioProgress,
        onStalled = ::onAudioStalled,
        onPlaying = ::onAudioPlaying,
        onEnded = ::onAudioEnded,
        onError = ::onAudioError,
        onOutputChanged = { value -> synchronized(lock) { audioOutput = value; publishLocked() } },
        releaseOwnedFile = audioTranscoder::delete,
    )

    private var catalogTracks: List<DesktopTrack> = emptyList()
    private var queue: List<PlaybackRequestTrack> = emptyList()
    private val queueNavigator = PlaybackQueueNavigator()
    private var currentIndex = -1
    private var currentRequest: PlaybackRequestTrack? = null
    private var resolutionJob: Job? = null
    private var startupTimeoutJob: Job? = null
    private var stallTimeoutJob: Job? = null
    private var crossfadePreloadJob: Job? = null
    private var crossfadePreloadKey: String? = null
    private var generation = 0L
    private var playbackSettings = PlaybackSettings()
    private var repeatMode = PlaybackRepeatMode.OFF
    private var shuffleEnabled = false
    private var playWhenReady = false
    private var isPlaying = false
    private var phase = PlaybackPhase.IDLE
    private var positionMs = 0L
    private var lastPublishedProgressMs = 0L
    private var durationMs: Long? = null
    private var errorMessage: String? = null
    private var audioOutput: PlaybackAudioOutput? = null
    private var hasUserPlaybackChanges = false
    private var restoredPositionPendingMs: Long? = null
    private var closed = false

    init {
        scope.launch {
            playbackSettingsRepository.observeSettings().collect { settings ->
                val normalizedSettings = settings.normalized()
                synchronized(lock) {
                    playbackSettings = normalizedSettings
                }
                audio.updatePlaybackSettings(normalizedSettings)
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
            synchronized(lock) {
                if (hasUserPlaybackChanges) return@synchronized
                queue = checkpoint.queue
                currentIndex = checkpoint.currentIndex
                currentRequest = queue.getOrNull(currentIndex)
                repeatMode = checkpoint.repeatMode
                shuffleEnabled = checkpoint.shuffleEnabled
                durationMs = currentRequest?.durationMs
                positionMs = checkpoint.positionMs
                restoredPositionPendingMs = checkpoint.positionMs.takeIf { currentRequest != null && it > 0L }
                queueNavigator.replaceQueue(queue.size, currentIndex, shuffleEnabled)
                publishQueueLocked()
                publishLocked()
            }
        }
    }

    override fun setCatalogTracks(tracks: List<DesktopTrack>) {
        synchronized(lock) {
            catalogTracks = tracks.toList()
        }
    }

    override fun updateQueue(tracks: List<PlaybackRequestTrack>) {
        synchronized(lock) {
            if (queue == tracks) return
            hasUserPlaybackChanges = true
            val currentTrack = currentRequest
            queue = tracks.toList()
            currentIndex = currentTrack?.let { current -> queue.indexOfFirst { it.sameTrack(current) } } ?: -1
            queueNavigator.replaceQueue(queue.size, currentIndex, shuffleEnabled)
            publishQueueLocked()
            publishLocked()
        }
    }

    override fun appendQueue(tracks: List<PlaybackRequestTrack>) {
        synchronized(lock) {
            val existing = queue.mapTo(mutableSetOf()) { it.providerId to it.id }
            val additions = tracks.filter { (it.providerId to it.id) !in existing }
            if (additions.isEmpty()) return
            hasUserPlaybackChanges = true
            val previousCount = queue.size
            queue = queue + additions
            queueNavigator.append(previousCount, additions.size, shuffleEnabled)
            publishQueueLocked()
            publishLocked()
        }
    }

    override fun togglePlayPause() {
        if (playbackSnapshot.value.shouldPauseOnToggle()) pause() else play()
    }

    override fun setVolume(volume: Float) { audio.setVolume(volume) }

    override fun play() {
        val track = synchronized(lock) { currentRequest } ?: return
        val snapshot = playbackSnapshot.value
        if (snapshot.phase == PlaybackPhase.IDLE || snapshot.phase == PlaybackPhase.ENDED || snapshot.errorMessage != null) {
            val index = synchronized(lock) {
                hasUserPlaybackChanges = true
                currentIndex
            }
            startTrack(track, index)
        } else {
            synchronized(lock) {
                hasUserPlaybackChanges = true
                playWhenReady = true
                isPlaying = phase != PlaybackPhase.BUFFERING
                errorMessage = null
                publishLocked()
            }
            audio.play()
        }
    }

    override fun pause() {
        synchronized(lock) {
            hasUserPlaybackChanges = true
            playWhenReady = false
            isPlaying = false
            publishLocked()
        }
        audio.pause()
        persistCheckpoint(force = true)
    }

    override fun seekTo(positionMs: Long) {
        val duration = synchronized(lock) { durationMs } ?: return
        val position = positionMs.coerceIn(0L, duration)
        audio.seekTo(position)
        synchronized(lock) {
            hasUserPlaybackChanges = true
            this.positionMs = position
            lastPublishedProgressMs = position
            publishLocked()
        }
    }

    override fun requestSkip(forward: Boolean) {
        val targetIndex = synchronized(lock) {
            queueNavigator.nextIndex(
                currentIndex = currentIndex,
                itemCount = queue.size,
                forward = forward,
                repeatMode = repeatMode,
                shuffleEnabled = shuffleEnabled,
            ) ?: return
        }
        playQueueItem(targetIndex)
    }

    override fun setRepeatMode(mode: PlaybackRepeatMode) {
        synchronized(lock) {
            hasUserPlaybackChanges = true
            repeatMode = mode
            publishLocked()
        }
        clearCrossfadePreload()
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        synchronized(lock) {
            if (shuffleEnabled == enabled) return
            hasUserPlaybackChanges = true
            shuffleEnabled = enabled
            queueNavigator.setShuffleEnabled(enabled, queue.size, currentIndex)
            publishLocked()
        }
        clearCrossfadePreload()
    }

    override fun selectTrack(trackId: String) {
        val context = synchronized(lock) {
            val startIndex = catalogTracks.indexOfFirst { it.request.id == trackId }
            if (startIndex < 0) return
            catalogTracks.map { it.request } to startIndex
        }
        playQueue(context.first, context.second)
    }

    override fun playTrack(track: PlaybackRequestTrack) {
        val targetIndex = synchronized(lock) { queue.indexOfFirst { it.sameTrack(track) } }
        markUserPlaybackChange()
        synchronized(lock) { restoredPositionPendingMs = null }
        startTrack(track, targetIndex)
    }

    override fun playQueueItem(index: Int) {
        val track = synchronized(lock) { queue.getOrNull(index) } ?: return
        markUserPlaybackChange()
        synchronized(lock) { restoredPositionPendingMs = null }
        startTrack(track, index)
    }

    override fun playQueue(tracks: List<PlaybackRequestTrack>, startIndex: Int) {
        markUserPlaybackChange()
        updateQueue(tracks)
        synchronized(lock) {
            queueNavigator.replaceQueue(queue.size, startIndex, shuffleEnabled)
        }
        playQueueItem(startIndex)
    }

    private fun startTrack(track: PlaybackRequestTrack, requestedIndex: Int) {
        audio.pause()
        audio.seekTo(0L)
        val requestGeneration = synchronized(lock) {
            val previousIndex = currentIndex
            resolutionJob?.cancel()
            startupTimeoutJob?.cancel()
            stallTimeoutJob?.cancel()
            cancelCrossfadePreloadLocked()
            generation += 1L
            currentRequest = track
            currentIndex = requestedIndex.takeIf { queue.getOrNull(it)?.sameTrack(track) == true }
                ?: queue.indexOfFirst { it.sameTrack(track) }
            queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
            positionMs = restoredPositionPendingMs ?: 0L
            lastPublishedProgressMs = 0L
            durationMs = track.durationMs
            playWhenReady = true
            isPlaying = false
            phase = PlaybackPhase.BUFFERING
            errorMessage = null
            publishQueueLocked()
            publishLocked()
            generation
        }
        resolutionJob = scope.launch {
            var decodedAudio: Path? = null
            try {
                scheduleStartupTimeout(track.id, requestGeneration, AUDIO_DECODE_STARTUP_TIMEOUT_MS)
                val audioFile = resolveAndTranscode(track)
                if (!isCurrent(requestGeneration)) return@launch
                decodedAudio = audioFile
                if (!isCurrent(requestGeneration)) {
                    audioTranscoder.delete(audioFile)
                    decodedAudio = null
                    return@launch
                }
                audio.play(
                    track.id,
                    audioFile.toUri().toString(),
                    ownedAudioFile = audioFile,
                    initialPositionMs = synchronized(lock) { restoredPositionPendingMs ?: 0L },
                    shouldStartPlayback = {
                        synchronized(lock) {
                            generation == requestGeneration && currentRequest?.id == track.id && playWhenReady
                        }
                    },
                )
                scheduleTrackAnalysis(track, audioFile)
                decodedAudio = null
            } catch (cancellation: CancellationException) {
                decodedAudio?.let(audioTranscoder::delete)
                throw cancellation
            } catch (error: LinkageError) {
                decodedAudio?.let(audioTranscoder::delete)
                synchronized(lock) {
                    if (generation != requestGeneration) return@synchronized
                    phase = PlaybackPhase.IDLE
                    playWhenReady = false
                    isPlaying = false
                    errorMessage = "The desktop audio decoder isn't available for this platform."
                    publishLocked()
                }
            } catch (error: Exception) {
                decodedAudio?.let(audioTranscoder::delete)
                synchronized(lock) {
                    if (generation != requestGeneration) return@synchronized
                    phase = PlaybackPhase.IDLE
                    playWhenReady = false
                    isPlaying = false
                    errorMessage = error.message ?: "Couldn't start playback."
                    publishLocked()
                }
            }
        }
    }

    private suspend fun resolveAndTranscode(track: PlaybackRequestTrack): Path {
        var lastFailure: IOException? = null
        for (attempt in 1..MAX_AUDIO_SOURCE_ATTEMPTS) {
            try {
                val stream = streamResolver.resolve(track).getOrElse { throw it }
                val sourceUrl = if (stream.url.startsWith("file:")) java.io.File(java.net.URI(stream.url)).path
                    else if (track.providerId == com.convx.storage.presentation.NETWORK_MUSIC_PROVIDER) stream.url
                    else audioProxy.createMediaSource(stream.url, stream.mimeType)
                return audioTranscoder.transcode(sourceUrl, expectedDurationMs = track.durationMs)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                lastFailure = error
                if (attempt == MAX_AUDIO_SOURCE_ATTEMPTS) throw error
                System.err.println(
                    "Desktop playback source attempt $attempt/$MAX_AUDIO_SOURCE_ATTEMPTS failed: " +
                        "${error::class.simpleName}: ${error.message ?: "no details"}; resolving a fresh stream.",
                )
                delay(AUDIO_SOURCE_RETRY_DELAY_MS * attempt)
            }
        }
        throw lastFailure ?: IOException("No desktop audio stream could be resolved.")
    }

    override fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        synchronized(lock) {
            val order = moveQueueIndices(queue.indices.toList(), fromIndex, toIndex)
            if (order == queue.indices.toList()) return
            hasUserPlaybackChanges = true
            val oldCurrentIndex = currentIndex
            queue = order.map(queue::get)
            currentIndex = order.indexOf(oldCurrentIndex).takeIf { oldCurrentIndex >= 0 } ?: -1
            queueNavigator.move(fromIndex, toIndex, queue.size, shuffleEnabled)
            publishQueueLocked()
            publishLocked()
        }
    }

    override fun removeQueueItem(index: Int) {
        var nextQueueIndex: Int? = null
        var clearCurrent = false
        synchronized(lock) {
            if (index !in queue.indices) return
            hasUserPlaybackChanges = true
            val removedCurrent = index == currentIndex
            queue = queue.toMutableList().apply { removeAt(index) }
            queueNavigator.remove(index, queue.size, shuffleEnabled)
            when {
                removedCurrent && queue.isEmpty() -> {
                    resolutionJob?.cancel()
                    generation += 1L
                    currentRequest = null
                    currentIndex = -1
                    positionMs = 0L
                    durationMs = null
                    playWhenReady = false
                    phase = PlaybackPhase.IDLE
                    isPlaying = false
                    errorMessage = null
                    clearCurrent = true
                    publishQueueLocked()
                    publishLocked()
                }
                removedCurrent -> nextQueueIndex = index.coerceAtMost(queue.lastIndex)
                else -> {
                    if (index < currentIndex) currentIndex -= 1
                    publishQueueLocked()
                    publishLocked()
                }
            }
        }
        if (clearCurrent) {
            audio.pause()
            audio.seekTo(0L)
        } else {
            nextQueueIndex?.let(::playQueueItem)
        }
    }

    override fun removeQueueItemsAfterCurrent() {
        synchronized(lock) {
            val retainedCount = (currentIndex + 1).coerceAtLeast(0)
            if (retainedCount >= queue.size) return
            hasUserPlaybackChanges = true
            queue = queue.take(retainedCount)
            queueNavigator.truncate(retainedCount, shuffleEnabled)
            publishQueueLocked()
            publishLocked()
        }
    }

    override fun close() {
        val finalCheckpoint = synchronized(lock) {
            if (closed) return
            closed = true
            playWhenReady = false
            currentCheckpointLocked()
        }
        resolutionJob?.cancel()
        startupTimeoutJob?.cancel()
        stallTimeoutJob?.cancel()
        crossfadePreloadJob?.cancel()
        sleepTimer.release()
        audio.close(onClosed = audioTranscoder::close)
        audioProxy.close()
        try {
            kotlinx.coroutines.runBlocking(Dispatchers.IO) {
                kotlinx.coroutines.withTimeout(5_000L) {
                checkpointWriter.flush(finalCheckpoint)
                }
            }
        } catch (_: Exception) {
            System.err.println("Convx could not save the final playback checkpoint.")
        } finally {
            scope.cancel()
        }
    }

    override fun release() = close()

    private fun onAudioReady(trackId: String, durationMs: Long?) {
        synchronized(lock) {
            if (currentRequest?.id != trackId || phase != PlaybackPhase.BUFFERING) return
            startupTimeoutJob?.cancel()
            startupTimeoutJob = null
            stallTimeoutJob?.cancel()
            stallTimeoutJob = null
            phase = PlaybackPhase.READY
            isPlaying = playWhenReady
            this.durationMs = durationMs?.takeIf { it > 0L } ?: this.durationMs
            restoredPositionPendingMs?.let { restoredPosition ->
                positionMs = restoredPosition.coerceAtMost(this.durationMs ?: restoredPosition)
                restoredPositionPendingMs = null
            }
            errorMessage = null
            publishLocked()
        }
    }

    private fun onAudioProgress(trackId: String, positionMs: Long, durationMs: Long?) {
        val target = synchronized(lock) {
            if (currentRequest?.id != trackId) return
            val nextPositionMs = positionMs.coerceAtLeast(0L)
            this.positionMs = nextPositionMs
            val previousDurationMs = this.durationMs
            this.durationMs = durationMs?.takeIf { it > 0L } ?: this.durationMs
            if (
                nextPositionMs < lastPublishedProgressMs ||
                nextPositionMs - lastPublishedProgressMs >= UI_PROGRESS_INTERVAL_MS ||
                previousDurationMs != this.durationMs
            ) {
                lastPublishedProgressMs = nextPositionMs
                publishLocked()
            }
            crossfadeTargetLocked(this.positionMs)
        }
        if (target == null) {
            clearCrossfadePreload()
        } else {
            prepareOrStartCrossfade(target, positionMs.coerceAtLeast(0L))
        }
    }

    private fun onAudioStalled(trackId: String) {
        val requestGeneration = synchronized(lock) {
            if (currentRequest?.id != trackId || phase != PlaybackPhase.READY || !playWhenReady) return
            phase = PlaybackPhase.BUFFERING
            isPlaying = false
            publishLocked()
            generation
        }
        stallTimeoutJob?.cancel()
        stallTimeoutJob = scope.launch {
            delay(STREAM_STALL_TIMEOUT_MS)
            val stillStalled = synchronized(lock) {
                generation == requestGeneration &&
                    currentRequest?.id == trackId &&
                    phase == PlaybackPhase.BUFFERING &&
                    playWhenReady
            }
            if (stillStalled) onAudioError(trackId, "The audio stream stalled.")
        }
    }

    private fun onAudioPlaying(trackId: String) {
        synchronized(lock) {
            if (currentRequest?.id != trackId) return
            stallTimeoutJob?.cancel()
            stallTimeoutJob = null
            if (phase == PlaybackPhase.BUFFERING) {
                phase = PlaybackPhase.READY
                isPlaying = playWhenReady
                startupTimeoutJob?.cancel()
                startupTimeoutJob = null
                publishLocked()
            } else if (isPlaying != playWhenReady) {
                isPlaying = playWhenReady
                publishLocked()
            }
        }
    }

    private fun crossfadeTargetLocked(positionMs: Long): CrossfadeTarget? {
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

    private fun prepareOrStartCrossfade(target: CrossfadeTarget, positionMs: Long) {
        val shouldPrepare = synchronized(lock) {
            if (!isCrossfadeTargetCurrentLocked(target) || crossfadePreloadKey == target.key) {
                false
            } else {
                cancelCrossfadePreloadLocked()
                crossfadePreloadKey = target.key
                true
            }
        }
        if (shouldPrepare) {
            audio.clearPrepared()
            crossfadePreloadJob = scope.launch {
                var decodedAudio: Path? = null
                try {
                    val audioFile = resolveAndTranscode(target.incoming)
                    decodedAudio = audioFile
                    val shouldInstall = synchronized(lock) {
                        isCrossfadeTargetCurrentLocked(target, requireCurrentPlan = true)
                    }
                    if (shouldInstall) {
                        scheduleTrackAnalysis(target.incoming, audioFile)
                        audio.prepare(
                            target.incoming.id,
                            audioFile.toUri().toString(),
                            ownedAudioFile = audioFile,
                            initialPositionMs = target.incomingCuePositionMs,
                            playbackRate = target.incomingPlaybackRate,
                        )
                        decodedAudio = null
                    } else {
                        audioTranscoder.delete(audioFile)
                        decodedAudio = null
                    }
                } catch (cancellation: CancellationException) {
                    decodedAudio?.let(audioTranscoder::delete)
                    throw cancellation
                } catch (_: LinkageError) {
                    decodedAudio?.let(audioTranscoder::delete)
                    // Normal end-of-track handling will retry the next item.
                } catch (_: Exception) {
                    decodedAudio?.let(audioTranscoder::delete)
                    // Keep the current deck playing; normal end-of-track handling will retry the next item.
                }
            }
        }

        val canStart = synchronized(lock) {
            isPlaying && isCrossfadeTargetCurrentLocked(target, requireCurrentPlan = true)
        }
        if (positionMs >= target.startAtMs && canStart) {
            audio.crossfadeToPrepared(
                outgoingTrackId = target.outgoing.id,
                incomingTrackId = target.incoming.id,
                durationMs = target.durationMs,
                onHandoff = { onAudioHandoff(target) },
            )
        }
    }

    private fun scheduleTrackAnalysis(track: PlaybackRequestTrack, audioFile: Path) {
        val identity = track.analysisIdentity
        val shouldStart = synchronized(lock) {
            !closed && analysesInFlight.add(identity)
        }
        if (!shouldStart) return

        trackAnalysisCache.preload(identity)
        scope.launch {
            try {
                val cached = trackAnalysisCache.analysisFor(identity) ?: trackAnalysisRepository.get(identity)
                if (cached?.isUsable == true) {
                    trackAnalysisCache.update(identity, cached)
                    return@launch
                }

                val analysis = audioTranscoder.analyze(audioFile, identity, track.durationMs) ?: return@launch
                trackAnalysisRepository.save(identity, analysis)
                trackAnalysisCache.update(identity, analysis)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // Analysis is optional; playback continues with the planner's safe fade fallback.
            } finally {
                synchronized(lock) { analysesInFlight.remove(identity) }
            }
        }
    }

    private fun onAudioHandoff(target: CrossfadeTarget): Boolean = synchronized(lock) {
            if (!isPlaying || !isCrossfadeTargetCurrentLocked(target, requireCurrentPlan = true)) {
                return@synchronized false
            }
            val previousIndex = currentIndex
            currentRequest = target.incoming
            currentIndex = target.incomingIndex
            queueNavigator.trackSelected(currentIndex, previousIndex, queue.size, shuffleEnabled)
            crossfadePreloadJob = null
            crossfadePreloadKey = null
            generation += 1L
            positionMs = 0L
            lastPublishedProgressMs = 0L
            durationMs = target.incoming.durationMs
            isPlaying = true
            phase = PlaybackPhase.READY
            errorMessage = null
            publishQueueLocked()
            publishLocked()
            true
    }

    private fun isCrossfadeTargetCurrentLocked(
        target: CrossfadeTarget,
        requireCurrentPlan: Boolean = false,
    ): Boolean =
        generation == target.generation &&
            (!requireCurrentPlan || crossfadePreloadKey == target.key) &&
            currentRequest?.sameTrack(target.outgoing) == true &&
            queue.getOrNull(target.incomingIndex)?.sameTrack(target.incoming) == true

    private fun cancelCrossfadePreloadLocked() {
        crossfadePreloadJob?.cancel()
        crossfadePreloadJob = null
        crossfadePreloadKey = null
    }

    private fun clearCrossfadePreload() {
        val shouldClear = synchronized(lock) {
            if (crossfadePreloadKey == null) {
                false
            } else {
                cancelCrossfadePreloadLocked()
                true
            }
        }
        if (shouldClear) audio.clearPrepared()
    }

    private fun onAudioEnded(trackId: String) {
        synchronized(lock) {
            if (currentRequest?.id != trackId) return
            playWhenReady = false
            if (sleepTimer.onTrackEnded()) {
                phase = PlaybackPhase.ENDED
                isPlaying = false
                publishLocked()
                return
            }
            if (repeatMode == PlaybackRepeatMode.ONE) {
                positionMs = 0L
                playWhenReady = true
                isPlaying = true
                phase = PlaybackPhase.READY
                publishLocked()
                audio.replay()
                return
            }
            phase = PlaybackPhase.ENDED
            isPlaying = false
            publishLocked()
        }
        requestSkip(forward = true)
    }

    private fun onAudioError(trackId: String, message: String) {
        synchronized(lock) {
            if (currentRequest?.id != trackId) return
            startupTimeoutJob?.cancel()
            startupTimeoutJob = null
            stallTimeoutJob?.cancel()
            stallTimeoutJob = null
            phase = PlaybackPhase.IDLE
            playWhenReady = false
            isPlaying = false
            errorMessage = message
            publishLocked()
        }
    }

    private fun isCurrent(requestGeneration: Long): Boolean = synchronized(lock) {
        generation == requestGeneration
    }

    private fun scheduleStartupTimeout(
        trackId: String,
        requestGeneration: Long,
        timeoutMs: Long,
    ) {
        startupTimeoutJob?.cancel()
        startupTimeoutJob = scope.launch {
            delay(timeoutMs)
            val didTimeOut = synchronized(lock) {
                if (generation != requestGeneration || currentRequest?.id != trackId || phase != PlaybackPhase.BUFFERING) {
                    false
                } else {
                    generation += 1L
                    resolutionJob?.cancel()
                    phase = PlaybackPhase.IDLE
                    playWhenReady = false
                    isPlaying = false
                    errorMessage = "Playback timed out while preparing the audio stream."
                    startupTimeoutJob = null
                    publishLocked()
                    true
                }
            }
            if (didTimeOut) {
                audio.pause()
            }
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

    private fun publishLocked() {
        val track = currentRequest
        _playbackSnapshot.value = if (track == null) {
            PlaybackSnapshot.EMPTY.copy(repeatMode = repeatMode, shuffleEnabled = shuffleEnabled, audioOutput = audioOutput)
        } else {
            PlaybackSnapshot(
                track = PlaybackTrackSnapshot(
                    id = track.id,
                    title = track.title,
                    artists = track.artists,
                    providerId = track.providerId,
                    artworkUrl = track.artworkUrl,
                ),
                phase = phase,
                playWhenReady = playWhenReady,
                isPlaying = isPlaying,
                positionMs = positionMs,
                durationMs = durationMs,
                mediaItemIndex = currentIndex,
                mediaItemCount = queue.size,
                repeatMode = repeatMode,
                shuffleEnabled = shuffleEnabled,
                canSkipPrevious = queueNavigator.nextIndex(currentIndex, queue.size, false, repeatMode, shuffleEnabled) != null,
                canSkipNext = queueNavigator.nextIndex(currentIndex, queue.size, true, repeatMode, shuffleEnabled) != null,
                errorMessage = errorMessage,
                audioOutput = audioOutput,
            )
        }
        persistCheckpoint()
    }

    private fun markUserPlaybackChange() {
        synchronized(lock) { hasUserPlaybackChanges = true }
    }

    private fun persistCheckpoint(force: Boolean = false) {
        val checkpoint = synchronized(lock) {
            if (closed) null else currentCheckpointLocked()
        } ?: return
        checkpointWriter.save(checkpoint, force)
    }

    private fun currentCheckpointLocked() = PlaybackCheckpoint(
        queue = queue,
        currentIndex = currentIndex,
        positionMs = positionMs,
        repeatMode = repeatMode,
        shuffleEnabled = shuffleEnabled,
    )

    private fun publishQueueLocked() {
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
        const val CROSSFADE_PRELOAD_LEAD_MS = 180_000L
        const val AUDIO_DECODE_STARTUP_TIMEOUT_MS = 180_000L
        const val STREAM_STALL_TIMEOUT_MS = 15_000L
        const val UI_PROGRESS_INTERVAL_MS = 250L
        const val MAX_AUDIO_SOURCE_ATTEMPTS = 3
        const val AUDIO_SOURCE_RETRY_DELAY_MS = 350L
    }
}
