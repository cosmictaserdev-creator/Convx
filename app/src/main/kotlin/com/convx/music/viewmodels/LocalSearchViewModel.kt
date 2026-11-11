/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.LocalOnlyModeKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.LocalItem
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.Song
import com.convx.music.utils.dataStore
import com.convx.music.utils.fuzzyScore
import com.convx.music.utils.LocalAudioScanner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Uses substring matches first; ranks the full list only when exact search returns nothing. */
@OptIn(ExperimentalCoroutinesApi::class)
private fun <T : LocalItem> exactOrFuzzy(
    query: String,
    exact: Flow<List<T>>,
    all: Flow<List<T>>,
    limit: Int = Int.MAX_VALUE,
): Flow<List<T>> = exact.flatMapLatest { exactResults ->
    if (exactResults.isNotEmpty()) {
        flowOf(exactResults)
    } else {
        all.map { items ->
            items.mapNotNull { item -> fuzzyScore(query, item.title)?.let { item to it } }
                .sortedByDescending { it.second }
                .take(limit)
                .map { it.first }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocalSearchViewModel(
    private val context: Context,
    private val database: MusicDatabase,
) : ViewModel() {
    val query = MutableStateFlow("")
    val filter = MutableStateFlow(LocalFilter.ALL)
    private var scanJob: Job? = null

    fun scanLocalFiles() {
        if (scanJob?.isActive == true) return
        scanJob = viewModelScope.launch {
            runCatching { LocalAudioScanner.scanAndInsert(context, database) }
        }
    }

    val result =
        combine(
            query,
            filter,
            context.dataStore.data.map {
                ((it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false)) to (it[LocalOnlyModeKey] == true)
            }.distinctUntilChanged()
        ) { query, filter, preferences ->
            Triple(query, filter, preferences)
        }.flatMapLatest { (query, filter, preferences) ->
            val (hideVideoSongs, localOnly) = preferences
            if (query.isEmpty()) {
                flowOf(LocalSearchResult("", filter, emptyMap()))
            } else {
                when (filter) {
                    LocalFilter.ALL ->
                        combine(
                            exactOrFuzzy(query, database.searchSongs(query, PREVIEW_SIZE, localOnly), database.allSearchableSongs(localOnly), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchAlbums(query, PREVIEW_SIZE, localOnly), database.allSearchableAlbums(localOnly), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchArtists(query, PREVIEW_SIZE, localOnly), database.allSearchableArtists(localOnly), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchPlaylists(query, PREVIEW_SIZE, localOnly), database.allSearchablePlaylists(localOnly), PREVIEW_SIZE),
                        ) { songs, albums, artists, playlists ->
                            val filteredSongs = if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                            filteredSongs + albums + artists + playlists
                        }

                    LocalFilter.SONG -> exactOrFuzzy(query, database.searchSongs(query, localOnly = localOnly), database.allSearchableSongs(localOnly)).map { songs ->
                        if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                    }
                    LocalFilter.ALBUM -> exactOrFuzzy(query, database.searchAlbums(query, localOnly = localOnly), database.allSearchableAlbums(localOnly))
                    LocalFilter.ARTIST -> exactOrFuzzy(query, database.searchArtists(query, localOnly = localOnly), database.allSearchableArtists(localOnly))
                    LocalFilter.PLAYLIST -> exactOrFuzzy(query, database.searchPlaylists(query, localOnly = localOnly), database.allSearchablePlaylists(localOnly))
                }.map { list ->
                    LocalSearchResult(
                        query = query,
                        filter = filter,
                        map =
                        list.groupBy {
                            when (it) {
                                is Song -> LocalFilter.SONG
                                is Album -> LocalFilter.ALBUM
                                is Artist -> LocalFilter.ARTIST
                                is Playlist -> LocalFilter.PLAYLIST
                            }
                        },
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            LocalSearchResult("", filter.value, emptyMap())
        )

    companion object {
        const val PREVIEW_SIZE = 3
    }
}

enum class LocalFilter {
    ALL,
    SONG,
    ALBUM,
    ARTIST,
    PLAYLIST,
}

data class LocalSearchResult(
    val query: String,
    val filter: LocalFilter,
    val map: Map<LocalFilter, List<LocalItem>>,
)
