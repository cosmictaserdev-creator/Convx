/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

/**
 * Generation counter that decides whether a comment response is still wanted.
 *
 * The race this exists for: the user skips three tracks in two seconds. Three fetches go out, they
 * come back in whatever order the network feels like, and without a gate the *first* one to land
 * wins — so the sheet can end up showing comments for a song that stopped playing two skips ago.
 * Cancelling the coroutine is not enough on its own either, because a fetch that already completed
 * its network call but has not yet been dispatched still delivers.
 *
 * Every load takes a token from [begin]; every result is checked with [isCurrent] before it is
 * allowed to touch state. A result for a superseded generation is dropped on the floor.
 *
 * Plain and synchronous on purpose — this is the part of the feature that most needs to be provably
 * correct, and it is only provable if it can be tested without a dispatcher.
 */
class CommentRequestGate {

    private var generation: Int = 0
    private var trackId: String? = null

    /** Generation this gate is currently on. Exposed for diagnostics and tests. */
    val currentGeneration: Int
        @Synchronized get() = generation

    /** The track the gate is currently pointed at, or null when nothing is playing. */
    val currentTrackId: String?
        @Synchronized get() = trackId

    /**
     * Points the gate at [newTrackId] and returns the token the caller must present with its result.
     *
     * Re-binding to the track that is *already* current returns the same token rather than bumping
     * the generation: recomposition, sheet re-opening and configuration changes all re-bind, and
     * each of those bumping the generation would restart an in-flight fetch or discard a result that
     * had already arrived. Only an actual track change invalidates.
     *
     * [force] bumps unconditionally — the manual refresh path, where re-requesting the same track is
     * the whole point.
     */
    @Synchronized
    fun begin(newTrackId: String?, force: Boolean = false): Int {
        if (!force && newTrackId == trackId) return generation
        trackId = newTrackId
        generation += 1
        return generation
    }

    /**
     * True when a result carrying ([token], [resultTrackId]) is still the one the UI is waiting for.
     *
     * Both halves are checked. The generation catches "a newer track superseded this one"; the track
     * id catches the token being reused across a bind that did not bump (same-track rebind) but was
     * subsequently pointed elsewhere.
     */
    @Synchronized
    fun isCurrent(token: Int, resultTrackId: String?): Boolean =
        token == generation && resultTrackId != null && resultTrackId == trackId

    /**
     * Releases the gate — playback stopped or the player was torn down. Any in-flight result is
     * rejected afterwards, because [isCurrent] requires a non-null track id.
     */
    @Synchronized
    fun clear() {
        trackId = null
        generation += 1
    }
}
