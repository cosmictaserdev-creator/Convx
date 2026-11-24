/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.MyTopFilter
import com.convx.music.db.MusicDatabase
import com.convx.music.utils.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TopPlaylistViewModel(
    context: Context,
    database: MusicDatabase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val top = requireNotNull(savedStateHandle.get<String>("top")) { "Missing top" }
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }

    val topPeriod = MutableStateFlow(MyTopFilter.ALL_TIME)

    @OptIn(ExperimentalCoroutinesApi::class)
    val topSongs =
        combine(
            topPeriod,
            context.dataStore.data.map { (it[HideVideoSongsKey] ?: false) || (it[DataSaverEnabledKey] ?: false) }.distinctUntilChanged()
        ) { period, hideVideoSongs -> period to hideVideoSongs }
            .flatMapLatest { (period, hideVideoSongs) ->
                database.mostPlayedSongs(period.toTimeMillis(), top.toInt()).map { songs ->
                    if (hideVideoSongs) songs.filter { !it.song.isVideo } else songs
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
