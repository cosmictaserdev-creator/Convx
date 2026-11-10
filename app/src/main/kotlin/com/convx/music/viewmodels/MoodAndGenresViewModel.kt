/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.pages.MoodAndGenres
import com.convx.music.utils.reportException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MoodAndGenresViewModel : ViewModel() {
    val moodAndGenres = MutableStateFlow<List<MoodAndGenres>?>(null)

    init {
        refresh()
    }

    /** Re-fetches the mood/genre list. Bound to the pull-to-refresh gesture. */
    fun refresh() {
        viewModelScope.launch {
            YouTube
                .moodAndGenres()
                .onSuccess {
                    moodAndGenres.value = it
                }.onFailure {
                    reportException(it)
                }
        }
    }
}
