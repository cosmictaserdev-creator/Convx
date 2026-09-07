/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments.youtube

import com.convxy.music.comments.CommentSource
import com.convxy.music.comments.CommentSourcePreferences
import com.convxy.music.comments.CommentTrackRef
import com.convxy.music.comments.CommentsDataSource
import com.convxy.music.comments.CommentsOutcome
import com.music.innertube.YouTube
import com.music.innertube.models.comment.CommentThreadRenderer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Timestamped comments read from the YouTube video Convxy is already playing.
 *
 * No credentials, and no new network surface: this goes through the same `YouTube.comments` InnerTube
 * call that `ui/screens/CommentSheet.kt` has always used, so the app is reading comments from a service
 * it is already a client of rather than adding a second provider to reverse-engineer. The video id is
 * exact — no title/artist search, no similarity threshold — which removes the one genuinely uncertain
 * step every other source has to take.
 *
 * What it gives up is signal density. YouTube has no timed-comment field; the timestamp is a viewing
 * convention that [YouTubeCommentParser] recovers from a linked run or from the comment text. Most
 * comments on a music video are not about a moment, so this source finds comments for far more tracks
 * than Audius and pins far fewer of them. That trade is why it sits second in the default priority
 * rather than first, and why it is still worth having: for the large middle of the catalogue that is on
 * neither Audius nor a credential-holder's SoundCloud, it is the only source that can answer at all.
 */
@Singleton
class YouTubeCommentsDataSource @Inject constructor(
    private val preferences: CommentSourcePreferences,
) : CommentsDataSource {

    override val source: CommentSource = CommentSource.YOUTUBE

    override val name: String = SOURCE_NAME

    /**
     * Nothing to configure — no key, no account. This reports whether the user has left the source
     * switched on, and does no I/O, as the interface requires.
     */
    override suspend fun isConfigured(): Boolean =
        preferences.isFeatureEnabled && preferences.isSourceEnabled(CommentSource.YOUTUBE)

    /**
     * Only tracks whose id is actually a YouTube video id can be looked up, so this is an id-shape
     * test rather than a metadata test — see [YouTubeCommentParser.looksLikeVideoId] for why the shape
     * is the only available signal.
     *
     * Rejecting here is what stops every local file in the library costing a failing InnerTube round
     * trip on each track change.
     */
    override fun supports(track: CommentTrackRef): Boolean =
        YouTubeCommentParser.looksLikeVideoId(track.id)

    override suspend fun fetchComments(track: CommentTrackRef): CommentsOutcome {
        if (!isConfigured()) return CommentsOutcome.NotConfigured
        val videoId = track.id

        val threads = try {
            collect(videoId)
        } catch (noSection: NoCommentSectionException) {
            // Comments are turned off, or the video has none. A true empty rather than an outage:
            // showing "could not load" for a video the uploader disabled would be blaming the network
            // for something that is not the network's doing.
            return CommentsOutcome.Found(emptyList())
        } catch (e: Exception) {
            Timber.tag(TAG).d(e, "YouTube comment lookup failed for $videoId")
            return CommentsOutcome.Failed(humanReadable(e))
        }

        val durationMs = track.durationSeconds.takeIf { it > 0 }?.times(1000L)
        return CommentsOutcome.Found(
            YouTubeCommentParser.toComments(
                threads = threads,
                videoId = videoId,
                sourceName = name,
                trackDurationMs = durationMs,
            )
        )
    }

    /**
     * The first [MAX_PAGES] pages of comments.
     *
     * Bounded, and deliberately more than one page: InnerTube returns roughly twenty comments per page,
     * and since only a small minority of them carry a timestamp, a single page would often produce an
     * empty timeline for a video that does have timed comments further down. Three pages is the same
     * ceiling the SoundCloud source uses, and stops at whatever the continuation token runs out first.
     *
     * @throws NoCommentSectionException when the video exposes no comment section to fetch from.
     */
    private suspend fun collect(videoId: String): List<CommentThreadRenderer> {
        val (first, token) = YouTube.comments(videoId).getOrElse { failure ->
            if (isNoCommentSection(failure)) throw NoCommentSectionException(videoId)
            throw failure
        }
        val out = ArrayList<CommentThreadRenderer>(first)

        var next = token
        var pages = 1
        while (!next.isNullOrBlank() && pages < MAX_PAGES) {
            val (more, further) = YouTube.commentContinuation(next).getOrElse { break }
            out += more
            next = further
            pages++
        }
        return out
    }

    /**
     * Whether a failure means "this video has no comment section".
     *
     * `YouTube.comments` signals that by throwing when it cannot find a continuation token, which is
     * the only signal it gives — the InnerTube payload for a disabled comment section and for a broken
     * response look the same from here. Matching on the message is fragile in principle; in practice
     * the cost of it drifting is that a disabled-comment video shows an error state instead of an empty
     * one, which is a worse message but never a crash and never the wrong comments.
     */
    private fun isNoCommentSection(failure: Throwable): Boolean =
        failure.message?.contains("No comment continuation token") == true

    private fun humanReadable(e: Throwable): String = when (e) {
        is java.net.UnknownHostException -> "no connection to YouTube"
        is java.net.SocketTimeoutException -> "YouTube took too long to respond"
        else -> e.message ?: e.javaClass.simpleName
    }

    /** Internal signal: the video is reachable but has no comments to read. */
    private class NoCommentSectionException(videoId: String) : Exception("no comment section for $videoId")

    companion object {
        const val SOURCE_NAME = "YouTube"
        private const val TAG = "YouTubeComments"
        private const val MAX_PAGES = 3
    }
}
