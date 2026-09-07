/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convxy.music.constants.HideVideoSongsKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.db.MusicDatabase
import com.convxy.music.db.entities.Album
import com.convxy.music.db.entities.Artist
import com.convxy.music.db.entities.LocalItem
import com.convxy.music.db.entities.Playlist
import com.convxy.music.db.entities.Song
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.fuzzyScore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject

/**
 * The exact substring search (a typo returns nothing) is the fast path; only when it comes
 * back empty do we fall back to scanning the full [all] list and ranking it by [fuzzyScore]
 * against each item's title. Local libraries are small enough that this full scan is cheap,
 * and it only runs on the rare "typed something with no exact match" case.
 */
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
@HiltViewModel
class LocalSearchViewModel
@Inject
constructor(
    @ApplicationContext context: Context,
    database: MusicDatabase,
) : ViewModel() {
    val query = MutableStateFlow("")
    val filter = MutableStateFlow(LocalFilter.ALL)

    val result =
        combine(
            query,
            filter,
            context.dataStore.data.map { (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false) }.distinctUntilChanged()
        ) { query, filter, hideVideoSongs ->
            Triple(query, filter, hideVideoSongs)
        }.flatMapLatest { (query, filter, hideVideoSongs) ->
            if (query.isEmpty()) {
                flowOf(LocalSearchResult("", filter, emptyMap()))
            } else {
                when (filter) {
                    LocalFilter.ALL ->
                        combine(
                            exactOrFuzzy(query, database.searchSongs(query, PREVIEW_SIZE), database.allSearchableSongs(), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchAlbums(query, PREVIEW_SIZE), database.allSearchableAlbums(), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchArtists(query, PREVIEW_SIZE), database.allSearchableArtists(), PREVIEW_SIZE),
                            exactOrFuzzy(query, database.searchPlaylists(query, PREVIEW_SIZE), database.allSearchablePlaylists(), PREVIEW_SIZE),
                        ) { songs, albums, artists, playlists ->
                            val filteredSongs = if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                            filteredSongs + albums + artists + playlists
                        }

                    LocalFilter.SONG -> exactOrFuzzy(query, database.searchSongs(query), database.allSearchableSongs()).map { songs ->
                        if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                    }
                    LocalFilter.ALBUM -> exactOrFuzzy(query, database.searchAlbums(query), database.allSearchableAlbums())
                    LocalFilter.ARTIST -> exactOrFuzzy(query, database.searchArtists(query), database.allSearchableArtists())
                    LocalFilter.PLAYLIST -> exactOrFuzzy(query, database.searchPlaylists(query), database.allSearchablePlaylists())
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
