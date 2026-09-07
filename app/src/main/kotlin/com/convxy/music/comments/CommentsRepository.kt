/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.comments

import androidx.compose.runtime.Immutable
import com.convxy.music.comments.audius.AudiusCommentsDataSource
import com.convxy.music.comments.soundcloud.SoundCloudCommentsDataSource
import com.convxy.music.comments.youtube.YouTubeCommentsDataSource
import javax.inject.Inject
import javax.inject.Singleton

/** Why the sheet is showing what it is showing. One branch per distinct thing the UI must say. */
enum class CommentsStatus {
    /** Comments are on screen. */
    LOADED,

    /** The provider answered and the track really has no timed comments. */
    EMPTY,

    /** A provider could serve this track, but none is set up on this device. */
    NOT_CONFIGURED,

    /** Providers are set up, and none of them has a track matching this one. */
    NO_MATCH,

    /** No injected source can map this track's catalogue at all. */
    UNSUPPORTED,

    /** A provider was reached and failed. [TrackComments.message] may carry a reason. */
    FAILED,
}

/**
 * The repository's answer for one track. Immutable and self-describing, so the ViewModel can hand it
 * straight to Compose and so a late-arriving answer can be compared against what is on screen.
 */
@Immutable
data class TrackComments(
    val trackId: String,
    val status: CommentsStatus,
    val comments: List<TimestampedComment> = emptyList(),
    /** Which provider produced [comments], for the sheet header. Never invented when unknown. */
    val sourceName: String? = null,
    val fromCache: Boolean = false,
    val message: String? = null,
) {
    val hasComments: Boolean get() = comments.isNotEmpty()
}

/**
 * Fans a track out over the injected [CommentsDataSource]s and returns the first real answer,
 * caching it on the way through.
 *
 * This is the only comments type the presentation layer is allowed to know about: it has no idea
 * whether the answer came from SoundCloud, from a future second provider, or from disk.
 *
 * Failure policy: nothing here throws. Every branch resolves to a [TrackComments] with a status, so
 * a comments outage degrades to a message in a bottom sheet and can never reach the player.
 */
@Singleton
class CommentsRepository internal constructor(
    /** Every source the app knows about, in whatever order Hilt handed them over. */
    private val allSources: List<CommentsDataSource>,
    /** Reads the user's priority list at call time, so a settings change takes effect immediately. */
    private val orderProvider: () -> List<CommentSource>,
    private val cache: CommentsCache,
) {

    /**
     * The production wiring: three concrete sources plus the preference that ranks them.
     *
     * Taking each source as its own parameter rather than a Dagger multibinding `List<CommentsDataSource>`
     * is deliberate. Multibinding through a Kotlin generic collection is the one part of this feature
     * that would fail at *compile* time for the whole app if the wildcard annotations ever drifted, and
     * three named parameters cost nothing that a list would have saved. The abstraction still holds
     * where it matters: every line of logic below only ever sees the [CommentsDataSource] interface, so
     * a fourth provider is one more parameter here and one more entry in [allSources] — no change to the
     * cache, the ViewModel or any composable.
     *
     * The primary constructor is `internal` so the repository's decision table can be unit-tested
     * against fake sources; `internal` is visible to this module's test source set and to nothing else.
     */
    @Inject
    constructor(
        audius: AudiusCommentsDataSource,
        youTube: YouTubeCommentsDataSource,
        soundCloud: SoundCloudCommentsDataSource,
        preferences: CommentSourcePreferences,
        cache: CommentsCache,
    ) : this(listOf(audius, youTube, soundCloud), { preferences.orderedSources() }, cache)

    /**
     * The sources to ask, in the user's priority order, excluding any they switched off.
     *
     * Resolved per call rather than held as state: the order is a preference, and making it live means
     * reordering sources in Settings takes effect on the next track change with no invalidation, no
     * restart and no observer to keep in sync.
     *
     * An empty result means the feature is off, or every source is — in which case [commentsFor]
     * reports [CommentsStatus.UNSUPPORTED] without touching the network. The player hides its comments
     * button while the feature is off, so reaching that branch normally means the user disabled all
     * three sources individually and then opened the sheet anyway.
     */
    private fun currentSources(): List<CommentsDataSource> {
        val order = orderProvider()
        if (order.isEmpty()) return emptyList()
        val bySource = allSources.associateBy { it.source }
        return order.mapNotNull { bySource[it] }
    }

    /**
     * @param forceRefresh bypasses the cache and overwrites it — the retry/refresh path.
     */
    suspend fun commentsFor(
        track: CommentTrackRef,
        forceRefresh: Boolean = false,
    ): TrackComments {
        // Resolved once. As a property this re-read the preference and rebuilt the list on each of the
        // three accesses below; one read per track change is also what makes the ordering atomic, so a
        // settings change landing mid-call cannot swap sources halfway through the fan-out.
        val sources = currentSources()
        if (sources.isEmpty()) {
            return TrackComments(track.id, CommentsStatus.UNSUPPORTED)
        }

        // Cheap, local, per-source. Doing this before touching the cache or the network is what
        // makes an unconfigured provider cost nothing on every track change.
        val configured = ArrayList<CommentsDataSource>(sources.size)
        var anySupports = false
        for (source in sources) {
            if (!source.supports(track)) continue
            anySupports = true
            if (runCatchingSuspend { source.isConfigured() }.getOrDefault(false)) configured.add(source)
        }
        if (configured.isEmpty()) {
            return TrackComments(
                trackId = track.id,
                status = if (anySupports) CommentsStatus.NOT_CONFIGURED else CommentsStatus.UNSUPPORTED,
            )
        }

        if (!forceRefresh) {
            cached(track)?.let { return it }
        } else {
            cache.invalidate(track)
        }

        val durationMs = track.durationSeconds.takeIf { it > 0 }?.times(1000L)
        var sawNoMatch = false
        var sawFailure = false
        var lastFailure: String? = null

        for (source in configured) {
            val outcome = runCatchingSuspend { source.fetchComments(track) }
                .getOrElse { CommentsOutcome.Failed(it.message) }

            when (outcome) {
                is CommentsOutcome.Found -> {
                    val comments = CommentTimeline.normalize(outcome.comments, durationMs)
                    cache.write(track, CommentsOutcome.Found(comments), source.name)
                    return TrackComments(
                        trackId = track.id,
                        status = if (comments.isEmpty()) CommentsStatus.EMPTY else CommentsStatus.LOADED,
                        comments = comments,
                        sourceName = source.name,
                    )
                }

                CommentsOutcome.NoMatchingTrack -> sawNoMatch = true

                CommentsOutcome.NotConfigured -> Unit // raced with a settings change; try the next one

                is CommentsOutcome.Failed -> {
                    sawFailure = true
                    lastFailure = outcome.reason
                }
            }
        }

        // Nobody answered. Say why.
        return if (sawNoMatch && !sawFailure) {
            // A no-match IS cached, on the short negative TTL: it is a statement about this track
            // ("nobody configured has a recording matching it"), and re-running the same search on
            // every player open would spend rate limit to reach the same answer. The one-hour expiry
            // is what keeps it from being permanent — a track that gets uploaded to Audius tomorrow
            // is found tomorrow.
            //
            // But only when *every* source that was asked came back with a definite no. If one said
            // no-match and another failed, the failure is the open question: caching "no match" for an
            // hour would freeze out a source that might well have answered once the network recovered.
            // A retryable FAILED costs one extra attempt on the next player open and keeps that door
            // open, which is the right trade when the answer is genuinely unknown.
            cache.write(track, CommentsOutcome.NoMatchingTrack, configured.first().name)
            TrackComments(track.id, CommentsStatus.NO_MATCH, sourceName = configured.first().name)
        } else {
            // A transport failure is NOT cached. It says something about the network or the quota,
            // not about the track, and caching it would turn one bad second into an hour of "could
            // not load" for a song that is perfectly fetchable.
            TrackComments(
                trackId = track.id,
                status = CommentsStatus.FAILED,
                sourceName = configured.first().name,
                message = lastFailure,
            )
        }
    }

    private fun cached(track: CommentTrackRef): TrackComments? {
        val entry = cache.read(track) ?: return null
        return when (entry.kind) {
            CommentsCache.KIND_FOUND -> TrackComments(
                trackId = track.id,
                status = CommentsStatus.LOADED,
                comments = entry.comments,
                sourceName = entry.sourceName,
                fromCache = true,
            )

            CommentsCache.KIND_EMPTY -> TrackComments(
                trackId = track.id,
                status = CommentsStatus.EMPTY,
                sourceName = entry.sourceName,
                fromCache = true,
            )

            CommentsCache.KIND_NO_MATCH -> TrackComments(
                trackId = track.id,
                status = CommentsStatus.NO_MATCH,
                sourceName = entry.sourceName,
                fromCache = true,
            )

            else -> null
        }
    }
}
