/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.soundcloud

import com.convxy.music.comments.CommentSource
import com.convxy.music.comments.CommentTrackRef
import com.convxy.music.comments.CommentsDataSource
import com.convxy.music.comments.CommentsOutcome
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Timestamped comments from SoundCloud, via its official public API.
 *
 * SoundCloud's comment model *is* this feature: a comment carries a `timestamp` in milliseconds from
 * the start of the track, which is the exact unit Media3 seeks in, so there is nothing to infer and
 * nothing to parse out of free text. What it needs in return is a registered application, which is why
 * it is the one source here that can report [CommentsOutcome.NotConfigured] — and why it sits last in
 * the default priority despite having the cleanest data. [com.convxy.music.comments.audius.AudiusCommentsDataSource]
 * and [com.convxy.music.comments.youtube.YouTubeCommentsDataSource] both answer with no credentials at
 * all, so on a device where nobody has entered SoundCloud keys they are what actually runs.
 *
 * Getting onto a SoundCloud track from a Convxy one is the crux, and it is deliberately conservative:
 * search by artist + title, then require title, uploader and duration to agree
 * ([SoundCloudTrackMatcher]). A wrong match is worse than no match — it pins real people's comments to
 * the wrong moment of the wrong song — so an ambiguous result returns [CommentsOutcome.NoMatchingTrack]
 * and the UI says there is nothing for this track.
 *
 * Only the documented `api.soundcloud.com` endpoints are used, with credentials the user supplies. The
 * internal `api-v2.soundcloud.com` API the web player uses is not touched: it needs a `client_id`
 * scraped out of SoundCloud's own JavaScript bundles, is not licensed for third-party use, changes
 * without notice, and would put one shared, revocable credential behind every install of this app.
 */
@Singleton
class SoundCloudCommentsDataSource @Inject constructor(
    private val api: SoundCloudApi,
) : CommentsDataSource {

    override val source: CommentSource = CommentSource.SOUNDCLOUD

    override val name: String = SOURCE_NAME

    override suspend fun isConfigured(): Boolean = api.isConfigured()

    /**
     * A track is worth attempting when it carries either a SoundCloud handle already or enough
     * metadata to search with. A local file with an empty title and no artist cannot be matched by
     * anything, and saying so here costs nothing; saying it after a wasted search request does.
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
            // Transport, auth, rate limit, parse. Reported, never propagated: playback is untouched
            // by anything that happens in here.
            Timber.tag(TAG).d(e, "SoundCloud comment lookup failed for ${track.id}")
            return CommentsOutcome.Failed(humanReadable(e))
        }

        // The uploader disabled comments: a true empty, not a failure and not "no match".
        if (candidate.commentable == false) return CommentsOutcome.Found(emptyList())

        val durationMs = candidate.durationMs.takeIf { it > 0L }
            ?: track.durationSeconds.takeIf { it > 0 }?.times(1000L)

        val raw = api.comments(candidate.id).getOrElse {
            return CommentsOutcome.Failed(humanReadable(it))
        }
        val comments = SoundCloudCommentParser.toComments(
            dtos = raw,
            trackId = candidate.id,
            sourceName = name,
            trackDurationMs = durationMs,
            trackPermalinkUrl = candidate.permalinkUrl,
        )
        return CommentsOutcome.Found(comments)
    }

    /**
     * Maps a Convxy track onto a SoundCloud one.
     *
     * @throws NoMatchException when nothing clears the matcher's bar — a distinct signal from a
     *   transport failure, because the two mean different things to the user.
     */
    private suspend fun resolveTrack(track: CommentTrackRef): SoundCloudTrackCandidate {
        // A handle we were handed beats any amount of guessing.
        track.externalTrackId?.takeIf { it.isNotBlank() }?.let { handle ->
            api.track(handle).getOrNull()
                ?.let(SoundCloudCommentParser::toCandidate)
                ?.let { return it }
            // A stale or wrong handle is not a reason to give up — fall through to search.
        }

        val query = SoundCloudTrackMatcher.searchQuery(track.title, track.artistNames)
        if (query.isBlank()) throw NoMatchException("no searchable title or artist")

        val results = api.searchTracks(query).getOrThrow()
        val candidates = results.mapNotNull(SoundCloudCommentParser::toCandidate)
        return SoundCloudTrackMatcher.bestMatch(
            candidates = candidates,
            title = track.title,
            artistNames = track.artistNames,
            durationSeconds = track.durationSeconds,
        ) ?: throw NoMatchException("no confident SoundCloud match for \"$query\"")
    }

    /** Trims an exception down to something worth putting on screen. */
    private fun humanReadable(e: Throwable): String = when (e) {
        is SoundCloudApiException -> e.message ?: "SoundCloud request failed"
        is java.net.UnknownHostException -> "no connection to SoundCloud"
        is java.net.SocketTimeoutException -> "SoundCloud took too long to respond"
        else -> e.message ?: e.javaClass.simpleName
    }

    /** Internal signal: reachable, configured, but this track is not on SoundCloud (or not provably). */
    private class NoMatchException(reason: String) : Exception(reason)

    companion object {
        const val SOURCE_NAME = "SoundCloud"
        private const val TAG = "SoundCloudComments"
    }
}
