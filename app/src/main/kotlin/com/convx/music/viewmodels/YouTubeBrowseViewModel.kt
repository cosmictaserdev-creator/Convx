/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.pages.BrowseResult
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideYoutubeShortsKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class YouTubeBrowseViewModel(
    val context: Context,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val browseId = requireNotNull(savedStateHandle.get<String>("browseId")) { "Missing browseId" }
    private val params = savedStateHandle.get<String>("params")

    val result = MutableStateFlow<BrowseResult?>(null)
    val isLoading = MutableStateFlow(true)
    val hasError = MutableStateFlow(false)
    private var loadJob: Job? = null

    init {
        refresh()
    }

    /** Re-fetches this browse page. Bound to the pull-to-refresh gesture. */
    fun refresh() {
        loadJob?.cancel()
        val job = viewModelScope.launch {
            isLoading.value = true
            hasError.value = false
            try {
                val page = withContext(Dispatchers.IO) {
                    val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                    val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
                    val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
                    Triple(
                        YouTube.browse(browseId, params),
                        hideExplicit,
                        hideVideoSongs to hideYoutubeShorts,
                    )
                }
                page.first.onSuccess {
                    result.value = it
                        .filterExplicit(page.second)
                        .filterVideoSongs(page.third.first)
                        .filterYoutubeShorts(page.third.second)
                }.onFailure {
                    hasError.value = true
                    reportException(it)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                hasError.value = true
                reportException(error)
            } finally {
                if (loadJob === currentCoroutineContext()[Job]) isLoading.value = false
            }
        }
        loadJob = job
    }
}
