package com.convx.music.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.constants.HideExplicitKey
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RadioStationViewModel(
    private val context: Context,
) : ViewModel() {
    private val _tracks = MutableStateFlow<List<SongItem>?>(null)
    val tracks = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError = _hasError.asStateFlow()

    private var requestedSeedId: String? = null
    private var loadJob: Job? = null

    fun load(seedId: String) {
        if (seedId == requestedSeedId && (_tracks.value != null || _isLoading.value)) return
        requestedSeedId = seedId
        loadJob?.cancel()
        _tracks.value = null
        _hasError.value = false
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val next = YouTube.next(WatchEndpoint(videoId = seedId)).getOrThrow()
                var tracks = next.items.filterNot { it.id == seedId }
                val relatedEndpoint = next.relatedEndpoint
                if (tracks.isEmpty() && relatedEndpoint != null) {
                    tracks = YouTube.related(relatedEndpoint).getOrThrow().songs
                }
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                _tracks.value = tracks.asSequence()
                    .filter { !hideExplicit || !it.explicit }
                    .distinctBy { it.id }
                    .take(24)
                    .toList()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}
