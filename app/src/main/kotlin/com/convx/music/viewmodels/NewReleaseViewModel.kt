/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.filterExplicit
import com.convx.music.constants.HideExplicitKey
import com.convx.music.db.MusicDatabase
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewReleaseViewModel(
    val context: Context,
    private val database: MusicDatabase,
) : ViewModel() {
    private val _newReleaseAlbums = MutableStateFlow<List<AlbumItem>>(emptyList())
    val newReleaseAlbums = _newReleaseAlbums.asStateFlow()
    private var refreshJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            YouTube
                .newReleaseAlbums()
                .onSuccess { albums ->
                    val libraryArtists = database.allArtistsByPlayTime().first()
                    val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                    _newReleaseAlbums.value = withContext(Dispatchers.Default) {
                        val artistRanks = HashMap<String, Int>(libraryArtists.size)
                        val favouriteArtistRanks = HashMap<String, Int>()
                        var favouriteIndex = 0
                        for ((artistIndex, artist) in libraryArtists.withIndex()) {
                            artistRanks.putIfAbsent(artist.id, artistIndex)
                            if (artist.artist.bookmarkedAt != null) {
                                favouriteArtistRanks.putIfAbsent(artist.id, favouriteIndex)
                                favouriteIndex++
                            }
                        }

                        albums
                            .map { album ->
                                val rank = album.artists.orEmpty().firstNotNullOfOrNull { artist ->
                                    artist.id?.let { artistId ->
                                        favouriteArtistRanks[artistId] ?: artistRanks[artistId]
                                    }
                                } ?: Int.MAX_VALUE
                                album to rank
                            }
                            .sortedBy { it.second }
                            .map { it.first }
                            .filterExplicit(hideExplicit)
                    }
                }.onFailure {
                    reportException(it)
                }
        }
    }
}
