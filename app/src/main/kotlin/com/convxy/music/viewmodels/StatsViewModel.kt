/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.convxy.music.constants.HideVideoSongsKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.constants.statToPeriod
import com.convxy.music.db.MusicDatabase
import com.convxy.music.ui.screens.OptionStats
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

// Upper bound for the stats leaderboards. The UI shows the full counts of the
// returned lists, so 1000 keeps the visible "N songs/artists/albums" intact for
// any realistic library while bounding the query result size and list recomposition.
private const val STATS_LIMIT = 1000

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel
@Inject
constructor(
    @ApplicationContext private val context: Context,
    val database: MusicDatabase,
) : ViewModel() {
    val selectedOption = MutableStateFlow(OptionStats.CONTINUOUS)
    val indexChips = MutableStateFlow(0)

    val mostPlayedSongsStats =
        combine(
            selectedOption,
            indexChips,
            context.dataStore.data.map { (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false) }.distinctUntilChanged()
        ) { first, second, third -> Triple(first, second, third) }
            .flatMapLatest { (selection, t, hideVideoSongs) ->
                database
                    .mostPlayedSongsStats(
                        fromTimeStamp = statToPeriod(selection, t),
                        limit = STATS_LIMIT,
                        toTimeStamp =
                        if (selection == OptionStats.CONTINUOUS || t == 0) {
                            LocalDateTime
                                .now()
                                .toInstant(
                                    ZoneOffset.UTC,
                                ).toEpochMilli()
                        } else {
                            statToPeriod(selection, t - 1)
                        },
                    ).map { songs ->
                        if (hideVideoSongs) songs.filter { !it.isVideo } else songs
                    }
            }
                .distinctUntilChanged()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mostPlayedSongs =
        combine(
            selectedOption,
            indexChips,
            context.dataStore.data.map { (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false) }.distinctUntilChanged()
        ) { first, second, third -> Triple(first, second, third) }
            .flatMapLatest { (selection, t, hideVideoSongs) ->
                database
                    .mostPlayedSongs(
                        fromTimeStamp = statToPeriod(selection, t),
                        limit = STATS_LIMIT,
                        toTimeStamp =
                        if (selection == OptionStats.CONTINUOUS || t == 0) {
                            LocalDateTime
                                .now()
                                .toInstant(
                                    ZoneOffset.UTC,
                                ).toEpochMilli()
                        } else {
                            statToPeriod(selection, t - 1)
                        },
                    ).map { songs ->
                        if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                    }
            }
                .distinctUntilChanged()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mostPlayedArtists =
        combine(
            selectedOption,
            indexChips,
        ) { first, second -> Pair(first, second) }
            .flatMapLatest { (selection, t) ->
                database
                    .mostPlayedArtists(
                        statToPeriod(selection, t),
                        limit = STATS_LIMIT,
                        toTimeStamp =
                        if (selection == OptionStats.CONTINUOUS || t == 0) {
                            LocalDateTime
                                .now()
                                .toInstant(
                                    ZoneOffset.UTC,
                                ).toEpochMilli()
                        } else {
                            statToPeriod(selection, t - 1)
                        },
                    ).map { artists ->
                        artists.filter { it.artist.isYouTubeArtist }
                    }
            }
                .distinctUntilChanged()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mostPlayedAlbums =
        combine(
            selectedOption,
            indexChips,
        ) { first, second -> Pair(first, second) }
            .flatMapLatest { (selection, t) ->
                database.mostPlayedAlbums(
                    statToPeriod(selection, t),
                    limit = STATS_LIMIT,
                    toTimeStamp =
                    if (selection == OptionStats.CONTINUOUS || t == 0) {
                        LocalDateTime
                            .now()
                            .toInstant(
                                ZoneOffset.UTC,
                            ).toEpochMilli()
                    } else {
                        statToPeriod(selection, t - 1)
                    },
                )
            }
                .distinctUntilChanged()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * ArchiveTune-style live statistics overview: listening time, play count
     * and unique-song count for the currently selected stats range.
     */
    private fun statsRange(selection: OptionStats, t: Int): Pair<Long, Long> =
        Pair(
            statToPeriod(selection, t),
            if (selection == OptionStats.CONTINUOUS || t == 0) {
                LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
            } else {
                statToPeriod(selection, t - 1)
            },
        )

    val listeningTimeMs =
        combine(selectedOption, indexChips) { selection, t -> selection to t }
            .flatMapLatest { (selection, t) ->
                val (from, to) = statsRange(selection, t)
                database.getTotalPlayTimeInRange(from, to).map { it ?: 0L }
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    val totalPlays =
        combine(selectedOption, indexChips) { selection, t -> selection to t }
            .flatMapLatest { (selection, t) ->
                val (from, to) = statsRange(selection, t)
                database.getPlayCountInRange(from, to)
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val uniqueSongs =
        combine(selectedOption, indexChips) { selection, t -> selection to t }
            .flatMapLatest { (selection, t) ->
                val (from, to) = statsRange(selection, t)
                database.getUniqueSongCountInRange(from, to)
            }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val firstEvent =
        database
            .firstEvent()
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            mostPlayedArtists.collect { artists ->
                artists
                    .map { it.artist }
                    .filter {
                        it.thumbnailUrl == null || Duration.between(
                            it.lastUpdateTime,
                            LocalDateTime.now()
                        ) > Duration.ofDays(10)
                    }.forEach { artist ->
                        YouTube.artist(artist.id).onSuccess { artistPage ->
                            database.query {
                                update(artist, artistPage)
                            }
                        }
                    }
            }
        }
        viewModelScope.launch {
            mostPlayedAlbums.collect { albums ->
                albums
                    .filter {
                        it.album.songCount == 0
                    }.forEach { album ->
                        YouTube
                            .album(album.id)
                            .onSuccess { albumPage ->
                                database.query {
                                    update(album.album, albumPage, album.artists)
                                }
                            }.onFailure {
                                reportException(it)
                                if (it.message?.contains("NOT_FOUND") == true) {
                                    database.query {
                                        delete(album.album)
                                    }
                                }
                            }
                    }
            }
        }
    }
}
