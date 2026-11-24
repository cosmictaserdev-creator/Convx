/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.library.apple

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.SongSortType
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Song
import com.convx.music.utils.LocalAudioScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Songs stored on this phone (MediaStore), for Library's Local page. */
class AppleLocalSongsViewModel(
    private val database: MusicDatabase,
) : ViewModel() {
    private val order = MutableStateFlow(SongSortType.NAME to false)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val songs: StateFlow<List<Song>> = order
        .flatMapLatest { (type, desc) -> database.localSongs(type, desc) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _scanning = MutableStateFlow(false)
    val scanning = _scanning.asStateFlow()

    fun sort(type: SongSortType, descending: Boolean) {
        order.value = type to descending
    }

    /** Reads new or changed audio files into the library; cheap when nothing changed. */
    fun scan(context: Context) {
        if (_scanning.value) return
        _scanning.value = true
        viewModelScope.launch {
            runCatching { LocalAudioScanner.scanAndInsert(context.applicationContext, database) }
            _scanning.value = false
        }
    }
}
