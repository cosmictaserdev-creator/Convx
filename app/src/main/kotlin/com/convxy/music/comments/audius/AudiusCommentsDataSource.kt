/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.audius

import com.convxy.music.comments.CommentSource
import com.convxy.music.comments.CommentTrackRef
import com.convxy.music.comments.CommentsDataSource
import com.convxy.music.comments.CommentsOutcome
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Timestamped comments from Audius, via its public discovery API.
 *
 * This is the source that makes the feature work for most people. Audius needs no account, no
 * registered application and no credentials of any kind — a caller identifies itself with an
 * `app_name` parameter and that is the whole of the agreement — so unlike SoundCloud it is available
 * on a fresh install, which is why it is first in the default priority.
 *
 * Its comments are timed natively: every comment carries `track_timestamp_s`, the commenter's playback
 * position when they posted. Nothing is parsed out of free text and nothing is inferred, so a comment
 * sits at the moment its author actually meant. Audius also exposes like counts, reply counts and
 * threading, which the official SoundCloud API does not, so [com.convxy.music.comments.TimestampedComment]
 * fields that are always null from SoundCloud carry real numbers here.
 *
 * The trade is coverage. Audius is a smaller catalogue than SoundCloud and skews electronic, so plenty
 * of Convxy tracks simply are not on it. That is expected and handled: [resolveTrack] returns null,
 * this reports [CommentsOutcome.NoMatchingTrack], and the repository asks the next source in priority
 * order. A source being right about *not knowing* is what makes the priority chain trustworthy.
 *
 * Matching is conservative for the same reason SoundCloud's is — see [AudiusTrackMatcher]. Attaching
 * real people's timed reactions to the wrong recording is the one failure mode this feature must not
 * have, because the result looks exactly like a correct answer.
 */
@Singleton
class AudiusCommentsDataSource @Inject constructor(
    private val api: AudiusApi,
) : CommentsDataSource {

    override val source: CommentSource = CommentSource.AUDIUS

    override val name: String = SOURCE_NAME

    override suspend fun isConfigured(): Boolean = api.isConfigured()

    /**
     * Worth attempting when the track carries an Audius id already, or enough metadata to search with.
     *
     * An artist is required, not just a title, and that is deliberately as strict as the SoundCloud
     * source: a bare-title search for something as common as a cover song's name returns every version
     * of it, and title similarity plus a duration window is not enough on its own to tell two artists'
     * recordings apart. Saying "cannot attempt" here costs nothing; guessing wrong later costs the
     * feature's credibility.
     */
    override fun supports(track: CommentTrackRef): Boolean =
        !track.externalTrackId.isNullOrBlank() ||
            (track.hasUsableTitle && track.artistNames.any { it.isNotBlank() })

    override suspend fun fetchComments(track: CommentTrackRef): CommentsOutcome {
        if (!api.isConfigured()) return CommentsOutcome.NotConfigured

        val candidate = try {
            resolveTrack(track)
        } catch (noMatch: NoMatchException) {
            return CommentsOutcome.NoMatchingTrack
        } catch (e: Exception) {
            // Transport, rate limit, parse. Reported, never propagated: playback is untouched by
            // anything that happens in here.
            Timber.tag(TAG).d(e, "Audius comment lookup failed for ${track.id}")
            return CommentsOutcome.Failed(humanReadable(e))
        }

        // The artist turned comments off: a true empty, not a failure and not "no match".
        if (candidate.commentsDisabled == true) return CommentsOutcome.Found(emptyList())

        // The track payload already tells us nobody has commented. Skipping the second request is not
        // a micro-optimisation: this runs on every track change, and most tracks have no comments.
        if (candidate.commentCount == 0) return CommentsOutcome.Found(emptyList())

        val durationMs = candidate.durationMs.takeIf { it > 0L }
            ?: track.durationSeconds.takeIf { it > 0 }?.times(1000L)

        val page = api.comments(candidate.id).getOrElse {
            return CommentsOutcome.Failed(humanReadable(it))
        }
        val comments = AudiusCommentParser.toComments(
            page = page,
            trackId = candidate.id,
            sourceName = name,
            trackDurationMs = durationMs,
            trackPermalink = candidate.permalink ?: page.track?.permalink,
        )
        return CommentsOutcome.Found(comments)
    }

    /**
     * Maps a Convxy track onto an Audius one.
     *
     * @throws NoMatchException when nothing clears the matcher's bar — a distinct signal from a
     *   transport failure, because the two mean different things to the user and are cached
     *   differently by the repository.
     */
    private suspend fun resolveTrack(track: CommentTrackRef): AudiusTrackCandidate {
        // An id we were handed beats any amount of guessing.
        track.externalTrackId?.takeIf { it.isNotBlank() }?.let { handle ->
            api.track(handle).getOrNull()
                ?.let(AudiusCommentParser::toCandidate)
                ?.let { return it }
            // A stale or wrong handle is not a reason to give up — fall through to search.
        }

        val query = AudiusTrackMatcher.searchQuery(track.title, track.artistNames)
        if (query.isBlank()) throw NoMatchException("no searchable title or artist")

        val results = api.searchTracks(query).getOrThrow()
        val candidates = results.mapNotNull(AudiusCommentParser::toCandidate)
        return AudiusTrackMatcher.bestMatch(
            candidates = candidates,
            title = track.title,
            artistNames = track.artistNames,
            durationSeconds = track.durationSeconds,
        ) ?: throw NoMatchException("no confident Audius match for \"$query\"")
    }

    /** Trims an exception down to something worth putting on screen. */
    private fun humanReadable(e: Throwable): String = when (e) {
        is AudiusApiException -> e.message ?: "Audius request failed"
        is java.net.UnknownHostException -> "no connection to Audius"
        is java.net.SocketTimeoutException -> "Audius took too long to respond"
        else -> e.message ?: e.javaClass.simpleName
    }

    /** Internal signal: reachable and enabled, but this track is not on Audius (or not provably). */
    private class NoMatchException(reason: String) : Exception(reason)

    companion object {
        const val SOURCE_NAME = "Audius"
        private const val TAG = "AudiusComments"
    }
}
