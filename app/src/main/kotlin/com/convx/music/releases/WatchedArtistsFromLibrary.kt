/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.releases

import com.convx.music.constants.ArtistSortType
import com.convx.music.db.MusicDatabase
import com.convx.music.playback.WatchedArtist
import com.convx.music.playback.WatchedArtists
import kotlinx.coroutines.flow.first

/** The artists the listener saved or plays most; only saved ones are announced. */
class WatchedArtistsFromLibrary(private val database: MusicDatabase) {
    suspend operator fun invoke(nowEpochMs: Long): WatchedArtists {
        val bookmarked = database.artistsBookmarked(ArtistSortType.NAME, false).first().map { it.artist }
            .filter { it.isYouTubeArtist }
        val played = database.mostPlayedArtists(nowEpochMs - PLAYED_WINDOW_MS, PLAYED_ARTISTS, 0, nowEpochMs).first()
            .map { it.artist }
            .filter { it.isYouTubeArtist }
        val artists = (bookmarked + played).distinctBy { it.id }.take(MAX_ARTISTS).map { WatchedArtist(it.id, it.name) }
        return WatchedArtists(artists, bookmarked.mapTo(HashSet()) { it.id })
    }

    private companion object {
        const val DAY_MS = 86_400_000L
        const val PLAYED_WINDOW_MS = 90 * DAY_MS
        const val PLAYED_ARTISTS = 30
        const val MAX_ARTISTS = 150
    }
}
