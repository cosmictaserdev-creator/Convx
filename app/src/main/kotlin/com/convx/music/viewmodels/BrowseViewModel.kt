/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels
 
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.YTItem
import com.convx.music.utils.reportException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowseViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val browseId: String? = savedStateHandle.get<String>("browseId")
 
    val items = MutableStateFlow<List<YTItem>?>(null)
    val title = MutableStateFlow<String?>("")
    val isLoading = MutableStateFlow(true)
    val hasError = MutableStateFlow(false)
    private var loadJob: Job? = null
 
    init {
        retry()
    }

    fun retry() {
        val id = browseId ?: return
        loadJob?.cancel()
        val job = viewModelScope.launch {
            isLoading.value = true
            hasError.value = false
            try {
                val result = withContext(Dispatchers.IO) { YouTube.browse(id, null) }
                result.onSuccess { page ->
                    title.value = page.title
                    items.value = page.items.flatMap { it.items }.distinctBy { it.id }
                }.onFailure {
                    hasError.value = true
                    reportException(it)
                }
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
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
