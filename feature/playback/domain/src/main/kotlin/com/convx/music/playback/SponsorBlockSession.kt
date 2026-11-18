package com.convx.music.playback

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun interface SponsorSegmentsSource {
    /** Null on failure; an empty list is a successful lookup with no matching segments. */
    suspend fun segments(videoId: String, categories: Set<SponsorCategory>): List<SponsorSegment>?
}

fun isSponsorVideoId(value: String): Boolean = value.length == 11 && value.all {
    it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' || it == '_' || it == '-'
}

/** A null video ID disables lookup and skipping (offline, local file, unsupported provider, or setting off). */
data class SponsorPlaybackState(
    val videoId: String?,
    val positionMs: Long = 0,
    val durationMs: Long? = null,
    val isPlaying: Boolean = false,
    val canSkipNext: Boolean = false,
    val generation: Long = 0,
)

sealed interface SponsorSkipAction {
    data class Seek(val positionMs: Long) : SponsorSkipAction
    data object NextTrack : SponsorSkipAction
}

/** Keeps seek targets inside the current track and preserves pause-at-track-end when next is disallowed. */
fun sponsorSkipAction(state: SponsorPlaybackState, skipper: SponsorBlockSkipper): SponsorSkipAction? {
    if (!state.isPlaying || state.positionMs < 0) return null
    val target = skipper.skipTarget(state.positionMs) ?: return null
    val duration = state.durationMs?.takeIf { it > 0 }
    return when {
        duration == null || target < duration - 500L -> SponsorSkipAction.Seek(target)
        state.canSkipNext -> SponsorSkipAction.NextTrack
        else -> SponsorSkipAction.Seek(duration).takeIf { duration > state.positionMs }
    }
}

/**
 * Shared polling, cancellation and stale-result protection. The adapter reads and applies state on its
 * playback dispatcher; [apply] must check the expected generation before changing a concurrent engine.
 */
class SponsorBlockSession(private val source: SponsorSegmentsSource) {
    suspend fun run(
        read: () -> SponsorPlaybackState,
        apply: (SponsorPlaybackState, SponsorSkipAction) -> Unit,
        categories: Set<SponsorCategory> = DefaultSponsorCategories,
    ): Unit = coroutineScope {
        val resultLock = Mutex()
        var identity: Pair<String?, Long>? = null
        var fetch: Job? = null
        var skipper: SponsorBlockSkipper? = null
        var lastCommand: Pair<SponsorPlaybackState, SponsorSkipAction>? = null
        try {
            while (isActive) {
                val state = read()
                val nextIdentity = state.videoId to state.generation
                if (nextIdentity != identity) {
                    identity = nextIdentity
                    lastCommand = null
                    fetch?.cancel()
                    resultLock.withLock { skipper = null }
                    val id = state.videoId?.takeIf(::isSponsorVideoId)
                    if (id != null && categories.isNotEmpty()) {
                        fetch = launch {
                            val segments = try { source.segments(id, categories) }
                            catch (cancelled: CancellationException) { throw cancelled }
                            catch (_: Exception) { null }
                            if (segments != null) resultLock.withLock {
                                // A cancelled or non-cooperative lookup must never install a previous song's segments.
                                if (isActive && identity == nextIdentity) skipper = SponsorBlockSkipper(segments, categories)
                            }
                        }
                    }
                }
                val action = resultLock.withLock { skipper?.let { sponsorSkipAction(state, it) } }
                if (action != null) {
                    val command = state to action
                    if (command != lastCommand) {
                        lastCommand = command
                        apply(state, action)
                    }
                } else lastCommand = null
                delay(POLL_MS)
            }
        } finally { fetch?.cancel() }
    }

    companion object { const val POLL_MS = 250L }
}
