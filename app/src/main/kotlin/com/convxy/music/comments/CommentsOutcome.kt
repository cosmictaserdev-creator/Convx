/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import androidx.compose.runtime.Immutable

/**
 * What one [CommentsDataSource] came back with.
 *
 * The split between "no comments" and "cannot tell you" is the whole point of this type. A track
 * that genuinely has no timed comments and a track whose provider is not configured look identical
 * on screen if they collapse into one empty list, and the honest answer is different in each case —
 * so each gets its own branch and its own string in the UI.
 */
sealed interface CommentsOutcome {

    /** The source answered; [comments] may be empty, which is a real answer, not a failure. */
    @Immutable
    data class Found(val comments: List<TimestampedComment>) : CommentsOutcome

    /**
     * The source exists but is not usable right now — no API credentials, or a revoked token.
     * Not an error the user caused by playing the wrong song; the sheet explains how to enable it.
     */
    data object NotConfigured : CommentsOutcome

    /** The source is configured and reachable but has no track matching this one. */
    data object NoMatchingTrack : CommentsOutcome

    /**
     * A transport/parse/rate-limit failure. Carries an optional human-readable reason for the
     * error state; never a stack trace, and never fatal — playback is untouched either way.
     */
    @Immutable
    data class Failed(val reason: String? = null) : CommentsOutcome
}

/** True when the outcome carries a comment list (possibly empty) rather than an explanation. */
val CommentsOutcome.isAnswer: Boolean
    get() = this is CommentsOutcome.Found
