/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback.queues

import com.convx.music.extensions.toMediaItem
import com.convx.music.models.toMediaMetadata
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem

/**
 * Plays the tapped song in list order, or starts YouTube radio from it when [startRadio] is set
 * or no surrounding list exists.
 */
fun searchResultQueue(
    song: SongItem,
    siblings: List<YTItem>,
    title: String? = null,
    startRadio: Boolean = false,
): Queue {
    if (startRadio) return radioQueue(song)
    val songs = siblings.filterIsInstance<SongItem>().distinctBy { it.id }
    val startIndex = songs.indexOfFirst { it.id == song.id }
    return if (songs.size > 1 && startIndex >= 0) {
        ListQueue(
            title = title,
            items = songs.map { it.toMediaItem() },
            startIndex = startIndex,
        )
    } else {
        radioQueue(song)
    }
}

private fun radioQueue(song: SongItem): Queue =
    YouTubeQueue(WatchEndpoint(videoId = song.id), song.toMediaMetadata())
