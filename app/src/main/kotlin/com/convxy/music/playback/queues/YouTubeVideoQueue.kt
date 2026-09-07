/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.playback.queues

import androidx.media3.common.MediaItem
import com.music.innertube.YouTubeWeb
import com.music.innertube.models.WebVideo
import com.convxy.music.extensions.toMediaItem
import com.convxy.music.models.MediaMetadata
import com.convxy.music.models.toMediaMetadata
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * A queue rooted at one regular-YouTube video, extended with YouTube's related
 * videos so native autoplay behaves like the watch page: when the video ends,
 * the next related item keeps playing through Convxy's normal queue machinery
 * (mini player, media session, notification) with no separate player.
 *
 * Related loading is best-effort: if the watch page can't be fetched the queue
 * degrades to the single video instead of failing playback.
 */
class YouTubeVideoQueue(
    private val video: WebVideo,
    private val autoplayRelated: Boolean = true,
    /** Position (milliseconds) to resume the root video from. */
    private val startPositionMs: Long = 0L,
) : Queue {
    private var continuation: String? = null
    private var exhausted = false

    override val preloadItem: MediaMetadata? = null

    override suspend fun getInitialStatus(): Queue.Status = withContext(IO) {
        timber.log.Timber.tag("YouTubeVideo").i("queue initial status: root=%s autoplay=%s", video.id, autoplayRelated)
        val root = listOf(video.toMediaMetadata().toMediaItem())
        if (!autoplayRelated) {
            return@withContext Queue.Status(
                title = null,
                items = root,
                mediaItemIndex = 0,
                position = startPositionMs,
            )
        }
        runCatching {
            val page = YouTubeWeb.watch(video.id).getOrThrow()
            continuation = page.relatedContinuation
            val items = root + page.related.map { it.toMediaMetadata().toMediaItem() }
            Queue.Status(
                title = null,
                items = items,
                mediaItemIndex = 0,
                position = startPositionMs,
            )
        }.getOrElse {
            // Watch-page metadata is decorative for playback — never fail the queue on it.
            Queue.Status(
                title = null,
                items = root,
                mediaItemIndex = 0,
                position = startPositionMs,
            )
        }
    }

    override fun hasNextPage(): Boolean = !exhausted && continuation != null

    override suspend fun nextPage(): List<MediaItem> = withContext(IO) {
        val token = continuation ?: throw UnsupportedOperationException("No related continuation")
        val (videos, nextContinuation) = YouTubeWeb.relatedContinuation(token).getOrThrow()
        continuation = nextContinuation
        if (nextContinuation == null) exhausted = true
        videos.map { it.toMediaMetadata().toMediaItem() }
    }

    companion object {
        /** Convenience for playing a bare video id (deep links, restore flows). */
        suspend fun forVideoId(
            videoId: String,
            autoplayRelated: Boolean = true,
            startPositionMs: Long = 0L,
        ): YouTubeVideoQueue? {
            val video = runCatching { YouTubeWeb.watch(videoId).getOrNull()?.video }
                .getOrNull()
                ?: WebVideo(
                    id = videoId,
                    title = "",
                    channelId = null,
                    channelName = null,
                    thumbnail = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg",
                    durationSeconds = null,
                    viewsText = null,
                    publishedText = null,
                )
            return YouTubeVideoQueue(video, autoplayRelated, startPositionMs)
        }
    }
}
