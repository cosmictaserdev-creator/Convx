/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.models

import com.music.innertube.models.WebVideo
import com.convxy.music.ui.utils.resize

/**
 * Maps a regular-YouTube video onto Convxy's shared [MediaMetadata] queue-item
 * model. The channel becomes the artist (tapping it opens the YouTube channel
 * page), and `musicVideoType` stays null: these are not "video songs", so the
 * music-side HideVideoSongs/DataSaver filters must not swallow them.
 */
fun WebVideo.toMediaMetadata(): MediaMetadata =
    MediaMetadata(
        id = id,
        title = title,
        artists = listOfNotNull(
            channelName?.let { name ->
                MediaMetadata.Artist(
                    id = channelId?.let { "ytch_$it" },
                    name = name,
                )
            }
        ).ifEmpty { listOf(MediaMetadata.Artist(id = null, name = "YouTube")) },
        duration = durationSeconds ?: -1,
        thumbnailUrl = thumbnail?.resize(544, 544),
        album = null,
        musicVideoType = null,
        explicit = false,
        suggestedBy = null,
    )

/** True when the artist id marks a YouTube channel opened through the YouTube section. */
fun MediaMetadata.Artist.youtubeChannelId(): String? = id?.takeIf { it.startsWith("ytch_") }?.removePrefix("ytch_")
