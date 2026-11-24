@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.convx.music.client.ios

import com.convx.music.client.ios.equalizeraudiotapcompat.convx_av_player_analysis_dropped_samples
import com.convx.music.client.ios.equalizeraudiotapcompat.convx_av_player_analysis_sample_rate
import com.convx.music.client.ios.equalizeraudiotapcompat.convx_av_player_read_analysis_samples
import com.convx.music.playback.PlaybackRequestTrack
import com.convx.music.playback.PlaybackTrackAnalysisCache
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.convx.music.playback.TrackAnalysisGenerator
import com.convx.music.playback.analysisIdentity
import kotlinx.atomicfu.atomic
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import platform.AVFoundation.AVPlayer
import kotlin.math.abs

/** Drains the audio tap away from the render callback and persists usable whole-track features. */
internal class IosPlaybackTrackAnalysisCollector(
    private val primaryPlayer: AVPlayer,
    private val secondaryPlayer: AVPlayer,
    private val trackAnalysisRepository: PlaybackTrackAnalysisRepository,
    private val trackAnalysisCache: PlaybackTrackAnalysisCache,
    private val scope: CoroutineScope,
) {
    private data class TrackContext(val identity: String, val durationMs: Long?)

    private class AnalysisRun(
        val id: Long,
        val track: TrackContext,
        val sampleRate: Int,
    ) {
        val invalidated = atomic(false)
        val pendingChunks = atomic(0)
    }

    private sealed interface Event {
        data class Begin(val run: AnalysisRun) : Event
        data class Samples(val run: AnalysisRun, val values: FloatArray) : Event
        data class Finish(val run: AnalysisRun) : Event
        data class Discard(val run: AnalysisRun) : Event
    }

    private val events = Channel<Event>(Channel.UNLIMITED)
    private val tracks = arrayOfNulls<TrackContext>(PLAYER_COUNT)
    private val runs = arrayOfNulls<AnalysisRun>(PLAYER_COUNT)
    private val processingInstalled = BooleanArray(PLAYER_COUNT)
    private val analysisBlocked = BooleanArray(PLAYER_COUNT)
    private var nextRunId = 0L
    private var released = false

    private val worker: Job = scope.launch(Dispatchers.Default) {
        val generators = mutableMapOf<Long, TrackAnalysisGenerator>()
        for (event in events) {
            when (event) {
                is Event.Begin -> {
                    val generator = runCatching {
                        TrackAnalysisGenerator(event.run.track.identity, event.run.sampleRate)
                    }.getOrNull()
                    if (generator == null) {
                        event.run.invalidated.value = true
                    } else {
                        generators[event.run.id] = generator
                    }
                }

                is Event.Samples -> {
                    try {
                        if (!event.run.invalidated.value) {
                            generators[event.run.id]?.acceptMonoSamples(event.values)
                        }
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (_: Exception) {
                        event.run.invalidated.value = true
                    } finally {
                        event.run.pendingChunks.decrementAndGet()
                    }
                }

                is Event.Finish -> try {
                    finishRun(event.run, generators.remove(event.run.id))
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    event.run.invalidated.value = true
                }

                is Event.Discard -> generators.remove(event.run.id)
            }
        }
    }

    fun setTrack(player: AVPlayer, track: PlaybackRequestTrack?) {
        val slot = playerSlot(player)
        finish(player)
        tracks[slot] = track?.let { TrackContext(it.analysisIdentity, it.durationMs) }
        processingInstalled[slot] = false
        analysisBlocked[slot] = false
        track?.analysisIdentity?.let(trackAnalysisCache::preload)
    }

    fun shouldCapture(player: AVPlayer): Boolean {
        val slot = playerSlot(player)
        val track = tracks[slot] ?: return false
        return !released && !analysisBlocked[slot] &&
            trackAnalysisCache.analysisFor(track.identity)?.isUsable != true
    }

    fun needsProcessing(player: AVPlayer): Boolean = !processingInstalled[playerSlot(player)]

    fun hasActiveRun(player: AVPlayer): Boolean = runs[playerSlot(player)] != null

    fun markProcessingInstalled(player: AVPlayer) {
        processingInstalled[playerSlot(player)] = true
    }

    fun ensureRun(player: AVPlayer) {
        val slot = playerSlot(player)
        if (released || analysisBlocked[slot] || runs[slot] != null) return
        val track = tracks[slot] ?: return
        if (trackAnalysisCache.analysisFor(track.identity)?.isUsable == true) return
        val sampleRate = convx_av_player_analysis_sample_rate(player).toInt()
        if (sampleRate !in MIN_SAMPLE_RATE..MAX_SAMPLE_RATE) return

        val run = AnalysisRun(++nextRunId, track, sampleRate)
        runs[slot] = run
        if (events.trySend(Event.Begin(run)).isFailure) {
            run.invalidated.value = true
            runs[slot] = null
        }
    }

    fun drain(player: AVPlayer) {
        val slot = playerSlot(player)
        val run = runs[slot]
        val samples = FloatArray(READ_CHUNK_SAMPLES)
        while (true) {
            val count = samples.usePinned { pinned ->
                convx_av_player_read_analysis_samples(player, pinned.addressOf(0), samples.size)
            }
            if (count <= 0) break
            if (run == null || run.invalidated.value) continue

            if (run.pendingChunks.incrementAndGet() > MAX_PENDING_CHUNKS) {
                run.pendingChunks.decrementAndGet()
                run.invalidated.value = true
                continue
            }
            val values = samples.copyOf(count)
            if (events.trySend(Event.Samples(run, values)).isFailure) {
                run.pendingChunks.decrementAndGet()
                run.invalidated.value = true
            }
        }
        if (run != null && convx_av_player_analysis_dropped_samples(player) > 0U) {
            run.invalidated.value = true
        }
    }

    fun invalidate(player: AVPlayer) {
        val slot = playerSlot(player)
        drain(player)
        val run = runs[slot]
        run?.invalidated?.value = true
        runs[slot] = null
        analysisBlocked[slot] = true
        if (run != null) events.trySend(Event.Discard(run))
    }

    fun finish(player: AVPlayer) {
        val slot = playerSlot(player)
        drain(player)
        analysisBlocked[slot] = true
        val run = runs[slot] ?: return
        runs[slot] = null
        if (events.trySend(Event.Finish(run)).isFailure) run.invalidated.value = true
    }

    fun release() {
        if (released) return
        finish(primaryPlayer)
        finish(secondaryPlayer)
        released = true
        events.close()
    }

    suspend fun awaitTermination() = worker.join()

    private suspend fun finishRun(run: AnalysisRun, generator: TrackAnalysisGenerator?) {
        if (run.invalidated.value || generator == null) return
        val observedDuration = generator.analyzedFrames.toDouble() / run.sampleRate
        val expectedDuration = run.track.durationMs?.takeIf { it > 0L }?.div(MILLIS_PER_SECOND)
        if (expectedDuration != null) {
            val durationDifference = expectedDuration - observedDuration
            if (durationDifference > MAX_UNPLAYED_TAIL_SECONDS || durationDifference < -MAX_DURATION_OVERRUN_SECONDS) return
        }
        val exactDuration = expectedDuration?.takeIf { abs(it - observedDuration) <= MAX_DURATION_OVERRUN_SECONDS }
        val analysis = generator.finish(expectedDurationSeconds = exactDuration) ?: return
        if (!analysis.isUsable) return

        trackAnalysisCache.update(run.track.identity, analysis)
        try {
            trackAnalysisRepository.save(run.track.identity, analysis)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            // Keep the in-memory result usable for this session when persistence is unavailable.
        }
    }

    private fun playerSlot(player: AVPlayer): Int = when {
        player === primaryPlayer -> PRIMARY_SLOT
        player === secondaryPlayer -> SECONDARY_SLOT
        else -> error("The iOS analysis collector received an unmanaged player.")
    }

    private companion object {
        const val PLAYER_COUNT = 2
        const val PRIMARY_SLOT = 0
        const val SECONDARY_SLOT = 1
        const val MIN_SAMPLE_RATE = 8_000
        const val MAX_SAMPLE_RATE = 192_000
        const val READ_CHUNK_SAMPLES = 8_192
        const val MAX_PENDING_CHUNKS = 32
        const val MAX_UNPLAYED_TAIL_SECONDS = 14.0
        const val MAX_DURATION_OVERRUN_SECONDS = 2.0
        const val MILLIS_PER_SECOND = 1_000.0
    }
}
