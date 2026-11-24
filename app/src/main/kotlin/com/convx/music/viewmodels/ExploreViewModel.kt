/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.YTItem
import com.music.innertube.models.filterExplicit
import com.music.innertube.pages.ExplorePage
import com.convx.music.constants.HideExplicitKey
import com.convx.music.db.MusicDatabase
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ExploreViewModel(
    val context: Context,
    val database: MusicDatabase,
) : ViewModel() {
    // Seeded from the last fetch so a return visit paints immediately; a refresh then replaces it.
    val explorePage = MutableStateFlow<ExplorePage?>(ExploreCache.page)

    /** Related items seeded from the most recently played track. */
    val recommendedForYou = MutableStateFlow<List<YTItem>?>(ExploreCache.recommended)

    private suspend fun loadRecommendedForYou() {
        val hideExplicit = context.dataStore.get(HideExplicitKey, false)
        val seedSong = database.latestEvent().first()?.song ?: return
        val endpoint = YouTube.next(WatchEndpoint(videoId = seedSong.id)).getOrNull()?.relatedEndpoint ?: return
        YouTube.related(endpoint).onSuccess { page ->
            recommendedForYou.value = (page.songs + page.albums + page.artists + page.playlists)
                .distinctBy { it.id }
                .filterExplicit(hideExplicit)
                .shuffled()
                .take(15)
            recommendedForYou.value?.let(ExploreCache::putRecommended)
        }
    }

    private suspend fun load() {
        YouTube
            .explore()
            .onSuccess { page ->
                val artists: MutableMap<Int, String> = mutableMapOf()
                val favouriteArtists: MutableMap<Int, String> = mutableMapOf()
                database.allArtistsByPlayTime().first().let { list ->
                    var favIndex = 0
                    for ((artistsIndex, artist) in list.withIndex()) {
                        artists[artistsIndex] = artist.id
                        if (artist.artist.bookmarkedAt != null) {
                            favouriteArtists[favIndex] = artist.id
                            favIndex++
                        }
                    }
                }
                explorePage.value =
                    page.copy(
                        newReleaseAlbums =
                        page.newReleaseAlbums
                            .sortedBy { album ->
                                val artistIds = album.artists.orEmpty().mapNotNull { it.id }
                                val firstArtistKey =
                                    artistIds.firstNotNullOfOrNull { artistId ->
                                        if (artistId in favouriteArtists.values) {
                                            favouriteArtists.entries.firstOrNull { it.value == artistId }?.key
                                        } else {
                                            artists.entries.firstOrNull { it.value == artistId }?.key
                                        }
                                    } ?: Int.MAX_VALUE
                                firstArtistKey
                            }.filterExplicit(context.dataStore.get(HideExplicitKey, false)),
                    )
                explorePage.value?.let(ExploreCache::put)
            }.onFailure {
                reportException(it)
            }
    }

    init {
        // Fresh data is already on screen; only fetch when there is none or it has gone stale.
        if (!ExploreCache.isFresh) refresh()
        else viewModelScope.launch(Dispatchers.IO) { if (recommendedForYou.value == null) loadRecommendedForYou() }
    }

    /** Re-fetches the explore page. Bound to the pull-to-refresh gesture. */
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            load()
            loadRecommendedForYou()
        }
    }
}
