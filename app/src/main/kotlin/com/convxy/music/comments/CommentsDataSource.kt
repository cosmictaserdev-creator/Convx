/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

/**
 * A place timestamped comments can come from.
 *
 * The player UI never sees one of these: it talks to [CommentsRepository], which fans out over the
 * injected list in priority order and returns the first real answer. That indirection is the reason
 * the feature can exist at all before a provider is wired up — an app whose tracks come from
 * YouTube/InnerTube, local files, JioSaavn and Spotify has no single catalogue a comments service
 * could serve, so "which source, if any, can answer for this track" has to be a pluggable decision
 * rather than something baked into a composable.
 *
 * Contract every implementation must honour:
 *  - [isConfigured] performs NO network I/O. It is called on the hot path before anything else and
 *    is what keeps an unconfigured provider from costing a request per track change.
 *  - [fetchComments] never throws. Everything comes back as a [CommentsOutcome], including failure,
 *    so a comments problem can never propagate into playback.
 *  - Returned timestamps are milliseconds from track start, non-negative, and already sorted.
 */
interface CommentsDataSource {

    /**
     * Which entry in [CommentSource] this is. The stable identity the user's priority preference
     * ranks and switches on/off; [name] is the human-facing label that ends up on screen and on each
     * comment, and the two are kept separate so a display string can change without invalidating
     * anybody's saved ordering.
     */
    val source: CommentSource

    /** Human-readable provider name, surfaced in the sheet header and stored on each comment. */
    val name: String

    /**
     * Whether this source could possibly answer right now — credentials present, feature reachable.
     * Cheap and local; a `false` here short-circuits before any I/O.
     */
    suspend fun isConfigured(): Boolean

    /**
     * Whether this source can even attempt [track]. Distinct from [isConfigured]: a source can be
     * fully configured and still have no way to map, say, a local file with no title onto its
     * catalogue, in which case saying so up front beats burning a search request.
     */
    fun supports(track: CommentTrackRef): Boolean

    /**
     * Fetches the timed comments for [track]. Must not throw; must not block the caller's thread
     * beyond its own network timeout.
     */
    suspend fun fetchComments(track: CommentTrackRef): CommentsOutcome
}
