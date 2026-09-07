package com.convxy.music.viewmodels

import android.content.Context
import android.util.LruCache
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.YTItem
import com.music.innertube.models.filterVideoSongs
import com.convxy.music.constants.HideVideoSongsKey
import com.convxy.music.constants.DataSaverEnabledKey
import com.convxy.music.db.MusicDatabase
import com.convxy.music.utils.dataStore
import com.convxy.music.utils.get
import com.convxy.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CachedPlaylistData(
    val playlist: PlaylistItem,
    val songs: List<SongItem>,
    val related: List<YTItem> = emptyList(),
    val continuation: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

object PlaylistMemoryCache {
    private const val MAX_ENTRIES = 30
    private const val TTL_MILLIS = 30 * 60 * 1000L // 30 minutes in RAM

    private val cache = LruCache<String, CachedPlaylistData>(MAX_ENTRIES)

    @Synchronized
    fun get(playlistId: String): CachedPlaylistData? {
        val entry = cache.get(playlistId) ?: return null
        if (System.currentTimeMillis() - entry.timestamp > TTL_MILLIS) {
            cache.remove(playlistId)
            return null
        }
        return entry
    }

    @Synchronized
    fun put(
        playlistId: String,
        playlist: PlaylistItem,
        songs: List<SongItem>,
        related: List<YTItem> = emptyList(),
        continuation: String? = null,
    ) {
        cache.put(
            playlistId,
            CachedPlaylistData(
                playlist = playlist,
                songs = songs,
                related = related,
                continuation = continuation,
                timestamp = System.currentTimeMillis(),
            )
        )
    }

    @Synchronized
    fun clear() {
        cache.evictAll()
    }
}

@HiltViewModel
class OnlinePlaylistViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    database: MusicDatabase
) : ViewModel() {
    private val playlistId = savedStateHandle.get<String>("playlistId")!!

    val playlist = MutableStateFlow<PlaylistItem?>(null)
    val playlistSongs = MutableStateFlow<List<SongItem>>(emptyList())
    val relatedItems = MutableStateFlow<List<YTItem>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    val dbPlaylist = database.playlistByBrowseId(playlistId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    var continuation: String? = null
        private set

    private var proactiveLoadJob: Job? = null

    init {
        // Fast sync restore from memory cache
        val cached = PlaylistMemoryCache.get(playlistId)
        if (cached != null) {
            playlist.value = cached.playlist
            playlistSongs.value = applySongFilters(cached.songs)
            relatedItems.value = cached.related
            continuation = cached.continuation
            _isLoading.value = false
        }
        fetchInitialPlaylistData()
    }

    private fun fetchInitialPlaylistData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (playlistSongs.value.isEmpty() && playlist.value == null) {
                _isLoading.value = true
            }
            _error.value = null
            proactiveLoadJob?.cancel() // Cancel any ongoing proactive load

            YouTube.playlist(playlistId)
                .onSuccess { playlistPage ->
                    playlist.value = playlistPage.playlist
                    val filteredSongs = applySongFilters(playlistPage.songs)
                    playlistSongs.value = filteredSongs
                    val related = playlistPage.related ?: emptyList()
                    relatedItems.value = related
                    continuation = playlistPage.songsContinuation
                    _isLoading.value = false

                    PlaylistMemoryCache.put(
                        playlistId = playlistId,
                        playlist = playlistPage.playlist,
                        songs = playlistPage.songs,
                        related = related,
                        continuation = playlistPage.songsContinuation,
                    )

                    if (continuation != null) {
                        startProactiveBackgroundLoading()
                    }
                }.onFailure { throwable ->
                    if (playlistSongs.value.isEmpty()) {
                        _error.value = throwable.message ?: "Failed to load playlist"
                    }
                    _isLoading.value = false
                    reportException(throwable)
                }
        }
    }

    private fun startProactiveBackgroundLoading() {
        proactiveLoadJob?.cancel() // Cancel previous job if any
        proactiveLoadJob = viewModelScope.launch(Dispatchers.IO) {
            var currentProactiveToken = continuation
            while (currentProactiveToken != null && isActive) {
                // If a manual loadMore is happening, pause proactive loading
                if (_isLoadingMore.value) {
                    break 
                }

                YouTube.playlistContinuation(currentProactiveToken)
                    .onSuccess { playlistContinuationPage ->
                        val currentSongs = playlistSongs.value.toMutableList()
                        currentSongs.addAll(playlistContinuationPage.songs)
                        val filteredSongs = applySongFilters(currentSongs)
                        playlistSongs.value = filteredSongs
                        currentProactiveToken = playlistContinuationPage.continuation
                        this@OnlinePlaylistViewModel.continuation = currentProactiveToken

                        playlist.value?.let { currentPlaylist ->
                            PlaylistMemoryCache.put(
                                playlistId = playlistId,
                                playlist = currentPlaylist,
                                songs = currentSongs,
                                related = relatedItems.value,
                                continuation = currentProactiveToken,
                            )
                        }
                    }.onFailure { throwable ->
                        reportException(throwable)
                        currentProactiveToken = null // Stop proactive loading on error
                    }
            }
        }
    }

    fun loadMoreSongs() {
        if (_isLoadingMore.value) return // Already loading more (manually)
        
        val tokenForManualLoad = continuation ?: return // No more songs to load

        proactiveLoadJob?.cancel() // Cancel proactive loading to prioritize manual scroll
        _isLoadingMore.value = true

        viewModelScope.launch(Dispatchers.IO) {
            YouTube.playlistContinuation(tokenForManualLoad)
                .onSuccess { playlistContinuationPage ->
                    val currentSongs = playlistSongs.value.toMutableList()
                    currentSongs.addAll(playlistContinuationPage.songs)
                    val filteredSongs = applySongFilters(currentSongs)
                    playlistSongs.value = filteredSongs
                    continuation = playlistContinuationPage.continuation

                    playlist.value?.let { currentPlaylist ->
                        PlaylistMemoryCache.put(
                            playlistId = playlistId,
                            playlist = currentPlaylist,
                            songs = currentSongs,
                            related = relatedItems.value,
                            continuation = continuation,
                        )
                    }
                }.onFailure { throwable ->
                    reportException(throwable)
                }.also {
                    _isLoadingMore.value = false
                    // Resume proactive loading if there's still a continuation
                    if (continuation != null && isActive) {
                        startProactiveBackgroundLoading()
                    }
                }
        }
    }

    fun retry() {
        proactiveLoadJob?.cancel()
        fetchInitialPlaylistData() // This will also restart proactive loading if applicable
    }

    private fun applySongFilters(songs: List<SongItem>): List<SongItem> {
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        return songs
            .distinctBy { it.id }
            .filterVideoSongs(hideVideoSongs)
    }

    override fun onCleared() {
        super.onCleared()
        proactiveLoadJob?.cancel()
    }
}
