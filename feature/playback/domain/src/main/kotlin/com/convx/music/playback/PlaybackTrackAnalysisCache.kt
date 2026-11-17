package com.convx.music.playback

import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

/** Asynchronously warms the analysis used by transition planning without touching the playback tick. */
class PlaybackTrackAnalysisCache(
    private val repository: PlaybackTrackAnalysisRepository,
    private val scope: CoroutineScope,
) {
    private val state = MutableStateFlow(State())

    fun analysisFor(trackIdentity: String): TrackAnalysis? = state.value.values[trackIdentity]

    fun preload(trackIdentity: String) {
        if (trackIdentity.isBlank()) return
        while (true) {
            val current = state.value
            if (trackIdentity in current.values || trackIdentity in current.loading) return
            if (state.compareAndSet(current, current.copy(loading = current.loading + trackIdentity))) break
        }
        scope.launch {
            val analysis = try {
                repository.get(trackIdentity)
            } catch (cancellation: CancellationException) {
                clearLoading(trackIdentity)
                throw cancellation
            } catch (_: Exception) {
                null
            }
            cache(trackIdentity, analysis)
        }
    }

    /** Makes newly generated analysis available immediately to the transition planner. */
    fun update(trackIdentity: String, analysis: TrackAnalysis?) {
        if (trackIdentity.isBlank()) return
        cache(trackIdentity, analysis)
    }

    fun invalidate(trackIdentity: String) {
        while (true) {
            val current = state.value
            if (trackIdentity !in current.values) return
            if (state.compareAndSet(current, current.copy(values = current.values - trackIdentity))) return
        }
    }

    private fun cache(trackIdentity: String, analysis: TrackAnalysis?) {
        while (true) {
            val current = state.value
            val updatedValues = current.values + (trackIdentity to analysis)
            val boundedValues = if (updatedValues.size <= MAX_ENTRIES) {
                updatedValues
            } else {
                updatedValues.entries.drop(updatedValues.size - MAX_ENTRIES).associate { it.key to it.value }
            }
            val next = current.copy(
                values = boundedValues,
                loading = current.loading - trackIdentity,
            )
            if (state.compareAndSet(current, next)) return
        }
    }

    private fun clearLoading(trackIdentity: String) {
        while (true) {
            val current = state.value
            if (trackIdentity !in current.loading) return
            if (state.compareAndSet(current, current.copy(loading = current.loading - trackIdentity))) return
        }
    }

    private data class State(
        val values: Map<String, TrackAnalysis?> = emptyMap(),
        val loading: Set<String> = emptySet(),
    )

    private companion object {
        const val MAX_ENTRIES = 128
    }
}
