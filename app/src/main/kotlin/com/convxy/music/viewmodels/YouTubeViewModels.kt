/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.YouTubeWeb
import com.music.innertube.models.SongItem
import com.music.innertube.models.WebChannelPage
import com.music.innertube.models.WebFeed
import com.music.innertube.models.WebFeedSection
import com.music.innertube.models.WebPlaylist
import com.music.innertube.models.WebPlaylistPage
import com.music.innertube.models.WebSearchFilter
import com.music.innertube.models.WebSearchPage
import com.music.innertube.models.WebVideo
import com.music.innertube.models.WebWatchPage
import com.music.innertube.pages.HomePage
import com.convxy.music.db.entities.YouTubeSavedVideoEntity
import com.convxy.music.db.entities.YouTubeWatchHistoryEntity
import com.convxy.music.db.MusicDatabase
import com.convxy.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// Home
// ─────────────────────────────────────────────────────────────────────────────

sealed interface YouTubeHomeUiState {
    data object Loading : YouTubeHomeUiState
    data class Ready(
        val feed: WebFeed,
        val isLoadingMore: Boolean = false,
        val endReached: Boolean = false,
    ) : YouTubeHomeUiState

    data class Error(val message: String) : YouTubeHomeUiState
}

@HiltViewModel
class YouTubeHomeViewModel
@Inject
constructor(
    private val database: MusicDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<YouTubeHomeUiState>(YouTubeHomeUiState.Loading)
    val uiState: StateFlow<YouTubeHomeUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    val continueWatching: StateFlow<List<YouTubeWatchHistoryEntity>> =
        database.youTubeDao.continueWatching().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentWatched: StateFlow<List<YouTubeWatchHistoryEntity>> =
        database.youTubeDao.watchHistory().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val savedVideos: StateFlow<List<YouTubeSavedVideoEntity>> =
        database.youTubeDao.savedVideos().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = YouTubeHomeUiState.Loading
            YouTubeWeb.home()
                .onSuccess { feed ->
                    if (feed.sections.isNotEmpty() || feed.shorts.isNotEmpty()) {
                        _uiState.value = YouTubeHomeUiState.Ready(feed = feed)
                    } else {
                        // Empty WEB feed (consent wall / parse miss) — try the
                        // music home shelf before giving up with an error.
                        loadMusicHomeFallback()
                    }
                }
                .onFailure { error ->
                    reportException(error)
                    loadMusicHomeFallback()
                }
        }
    }

    /**
     * Last-resort feed: the YouTube Music home (WEB_REMIX) usually works even
     * when the youtube-tab browse is region/consent-blocked, and its video
     * entries play through the same pipeline. Mapped into feed sections.
     */
    private fun loadMusicHomeFallback() {
        loadJob = viewModelScope.launch {
            YouTube.home()
                .onSuccess { page ->
                    val feed = musicHomeFeed(page)
                    _uiState.value = if (feed.sections.isEmpty()) {
                        YouTubeHomeUiState.Error(message = "Couldn't load the YouTube feed.")
                    } else {
                        YouTubeHomeUiState.Ready(feed = feed)
                    }
                }
                .onFailure { error ->
                    reportException(error)
                    _uiState.value = YouTubeHomeUiState.Error(message = youtubeErrorMessage(error))
                }
        }
    }

    private fun musicHomeFeed(page: HomePage): WebFeed {
        val videos = page.sections
            .flatMap { section -> section.items }
            .mapNotNull { item ->
                when (item) {
                    is SongItem ->
                        WebVideo(
                            id = item.id,
                            title = item.title,
                            channelId = item.artists.firstOrNull()?.id,
                            channelName = item.artists.firstOrNull()?.name,
                            thumbnail = item.thumbnail,
                            durationSeconds = item.duration,
                            viewsText = null,
                            publishedText = null,
                        )
                    else -> null
                }
            }
            .distinctBy { it.id }
        return if (videos.isEmpty()) {
            WebFeed(sections = emptyList(), shorts = emptyList(), continuation = null)
        } else {
            WebFeed(
                sections = listOf(WebFeedSection(title = "Recommended", videos = videos)),
                shorts = emptyList(),
                continuation = null,
            )
        }
    }

    fun loadMore() {
        val current = _uiState.value as? YouTubeHomeUiState.Ready ?: return
        val continuation = current.feed.continuation ?: return
        if (current.isLoadingMore || current.endReached || loadMoreJob?.isActive == true) return
        loadMoreJob = viewModelScope.launch {
            _uiState.value = current.copy(isLoadingMore = true)
            YouTubeWeb.home(continuation)
                .onSuccess { page ->
                    val state = _uiState.value as? YouTubeHomeUiState.Ready ?: return@onSuccess
                    val merged = WebFeed(
                        sections = state.feed.sections + page.sections,
                        shorts = (state.feed.shorts + page.shorts).distinctBy { it.id },
                        continuation = page.continuation,
                    )
                    _uiState.value = YouTubeHomeUiState.Ready(
                        feed = merged,
                        endReached = page.continuation == null,
                    )
                }
                .onFailure { error ->
                    reportException(error)
                    // Keep the already-loaded content; just stop the spinner.
                    val state = _uiState.value as? YouTubeHomeUiState.Ready ?: return@onFailure
                    _uiState.value = state.copy(isLoadingMore = false, endReached = true)
                }
        }
    }
}

internal fun youtubeErrorMessage(error: Throwable): String = when {
    error.message?.contains("unavailable", ignoreCase = true) == true ->
        "This content isn't available right now."
    error is java.io.IOException -> "Couldn't reach YouTube. Check your connection and try again."
    error is kotlinx.coroutines.CancellationException -> "Cancelled"
    else -> "Something went wrong while loading from YouTube."
}

// ─────────────────────────────────────────────────────────────────────────────
// Search
// ─────────────────────────────────────────────────────────────────────────────

data class YouTubeSearchUiState(
    val query: String = "",
    val filter: WebSearchFilter = WebSearchFilter.NONE,
    val page: WebSearchPage? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
) {
    val endReached: Boolean get() = page != null && page.continuation == null
}

@HiltViewModel
class YouTubeSearchViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val database: MusicDatabase,
) : ViewModel() {
    val searchHistory = database.youTubeDao.searchHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow(YouTubeSearchUiState())
    val uiState: StateFlow<YouTubeSearchUiState> = _uiState.asStateFlow()

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null
    private var loadMoreJob: Job? = null
    private var searchGeneration = 0

    init {
        // Deep links land here with a prefilled "?q=" query.
        val initialQuery = savedStateHandle.get<String>("q")?.trim().orEmpty()
        if (initialQuery.isNotEmpty()) {
            search(initialQuery)
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        scheduleSuggestions(query)
    }

    /** Debounced, cancelled-on-new-input suggestions. */
    private fun scheduleSuggestions(query: String) {
        suggestionJob?.cancel()
        if (query.isBlank() || query.startsWith("http")) {
            _suggestions.value = emptyList()
            return
        }
        suggestionJob = viewModelScope.launch {
            delay(180)
            YouTubeWeb.searchSuggestions(query)
                .onSuccess { _suggestions.value = it }
                .onFailure { _suggestions.value = emptyList() }
        }
    }

    fun search(query: String = _uiState.value.query) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        searchJob?.cancel()
        val generation = ++searchGeneration
        _uiState.value = _uiState.value.copy(
            query = trimmed,
            isLoading = true,
            error = null,
            page = null,
            filter = WebSearchFilter.NONE,
        )
        searchJob = viewModelScope.launch {
            // Persist as a recent search (recency-touched if it already exists).
            launch {
                runCatching {
                    database.youTubeDao.insertSearchQuery(
                        com.convxy.music.db.entities.YouTubeSearchHistoryEntity(query = trimmed)
                    )
                    database.youTubeDao.touchSearchQuery(trimmed, System.currentTimeMillis())
                }
            }
            YouTubeWeb.search(trimmed, WebSearchFilter.NONE)
                .onSuccess { page ->
                    if (generation != searchGeneration) return@onSuccess
                    _uiState.value = _uiState.value.copy(page = page, isLoading = false)
                }
                .onFailure { error ->
                    if (generation != searchGeneration) return@onFailure
                    reportException(error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = youtubeErrorMessage(error),
                    )
                }
        }
    }

    /** Switches result tab (Videos / Channels / Playlists) — refetches with the filter. */
    fun setFilter(filter: WebSearchFilter) {
        val state = _uiState.value
        if (state.query.isBlank() || filter == state.filter) return
        searchJob?.cancel()
        val generation = ++searchGeneration
        _uiState.value = state.copy(filter = filter, isLoading = true, error = null, page = null)
        searchJob = viewModelScope.launch {
            YouTubeWeb.search(state.query, filter)
                .onSuccess { page ->
                    if (generation != searchGeneration) return@onSuccess
                    _uiState.value = _uiState.value.copy(page = page, isLoading = false)
                }
                .onFailure { error ->
                    if (generation != searchGeneration) return@onFailure
                    reportException(error)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = youtubeErrorMessage(error))
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        val continuation = state.page?.continuation ?: return
        if (state.isLoading || state.isLoadingMore || loadMoreJob?.isActive == true) return
        loadMoreJob = viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            YouTubeWeb.searchContinuation(continuation)
                .onSuccess { page ->
                    val current = _uiState.value
                    val existing = current.page ?: return@onSuccess
                    _uiState.value = current.copy(
                        page = WebSearchPage(
                            videos = (existing.videos + page.videos).distinctBy { it.id },
                            channels = (existing.channels + page.channels).distinctBy { it.id },
                            playlists = (existing.playlists + page.playlists).distinctBy { it.id },
                            continuation = page.continuation,
                        ),
                        isLoadingMore = false,
                    )
                }
                .onFailure { error ->
                    reportException(error)
                    _uiState.value = _uiState.value.copy(isLoadingMore = false)
                }
        }
    }

    fun deleteSearchHistoryEntry(id: Long) {
        viewModelScope.launch { database.youTubeDao.deleteSearchQuery(id) }
    }

    fun clearSearchHistory() {
        viewModelScope.launch { database.youTubeDao.clearSearchHistory() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Watch
// ─────────────────────────────────────────────────────────────────────────────

sealed interface YouTubeWatchUiState {
    data object Loading : YouTubeWatchUiState
    data class Ready(val page: WebWatchPage) : YouTubeWatchUiState
    data class Error(val message: String) : YouTubeWatchUiState
}

@HiltViewModel
class YouTubeWatchViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val database: MusicDatabase,
) : ViewModel() {
    val videoId: String = savedStateHandle.get<String>("videoId").orEmpty()

    /** Resume position (milliseconds) passed as the "?position=" nav argument. */
    val startPositionMs: Long =
        savedStateHandle.get<Long>("position")
            ?: savedStateHandle.get<String>("position")?.toLongOrNull()
            ?: 0L

    private val _uiState = MutableStateFlow<YouTubeWatchUiState>(YouTubeWatchUiState.Loading)
    val uiState: StateFlow<YouTubeWatchUiState> = _uiState.asStateFlow()

    val historyEntry: kotlinx.coroutines.flow.Flow<YouTubeWatchHistoryEntity?> =
        database.youTubeDao.watchHistoryEntry(videoId)

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (videoId.isBlank()) {
            _uiState.value = YouTubeWatchUiState.Error("Invalid video.")
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = YouTubeWatchUiState.Loading
            YouTubeWeb.watch(videoId)
                .onSuccess { page ->
                    _uiState.value = YouTubeWatchUiState.Ready(page)
                    // History rows exist as soon as the video is opened; progress is
                    // written continuously by the watch screen while it plays.
                    launch {
                        runCatching {
                            val existing = database.youTubeDao.getWatchHistoryEntry(videoId)
                            if (existing == null) {
                                database.youTubeDao.upsertWatchHistory(
                                    YouTubeWatchHistoryEntity(
                                        videoId = videoId,
                                        title = page.video.title,
                                        channelId = page.video.channelId,
                                        channelName = page.video.channelName,
                                        thumbnailUrl = page.video.thumbnail,
                                        durationSeconds = page.video.durationSeconds ?: -1,
                                        positionSeconds = 0,
                                        lastWatchedAt = System.currentTimeMillis(),
                                    )
                                )
                            }
                        }
                    }
                }
                .onFailure { error ->
                    reportException(error)
                    _uiState.value = YouTubeWatchUiState.Error(message = youtubeErrorMessage(error))
                }
        }
    }

    /** Persist playback progress; called periodically and on lifecycle changes. */
    fun saveProgress(positionSeconds: Int, durationSeconds: Int, completed: Boolean) {
        if (videoId.isBlank()) return
        viewModelScope.launch {
            runCatching {
                val existing = database.youTubeDao.getWatchHistoryEntry(videoId)
                val readyPage = (_uiState.value as? YouTubeWatchUiState.Ready)?.page
                database.youTubeDao.upsertWatchHistory(
                    (existing ?: YouTubeWatchHistoryEntity(
                        videoId = videoId,
                        title = readyPage?.video?.title.orEmpty(),
                        channelId = readyPage?.video?.channelId,
                        channelName = readyPage?.video?.channelName,
                        thumbnailUrl = readyPage?.video?.thumbnail,
                        durationSeconds = durationSeconds,
                    )).copy(
                        positionSeconds = positionSeconds,
                        durationSeconds = if (durationSeconds > 0) durationSeconds else existing?.durationSeconds ?: -1,
                        completed = completed,
                        lastWatchedAt = System.currentTimeMillis(),
                    )
                )
            }
        }
    }

    fun toggleSaved(video: WebVideo?, currentlySaved: Boolean) {
        val video = video ?: return
        viewModelScope.launch {
            runCatching {
                if (currentlySaved) {
                    database.youTubeDao.unsaveVideo(video.id)
                } else {
                    database.youTubeDao.saveVideo(
                        YouTubeSavedVideoEntity(
                            videoId = video.id,
                            title = video.title,
                            channelId = video.channelId,
                            channelName = video.channelName,
                            thumbnailUrl = video.thumbnail,
                            durationSeconds = video.durationSeconds ?: -1,
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Channel
// ─────────────────────────────────────────────────────────────────────────────

data class YouTubeChannelTabContent(
    val videos: List<WebVideo> = emptyList(),
    val shorts: List<WebVideo> = emptyList(),
    val playlists: List<WebPlaylist> = emptyList(),
    val continuation: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface YouTubeChannelUiState {
    data object Loading : YouTubeChannelUiState
    data class Ready(
        val page: WebChannelPage,
        val selectedTabIndex: Int = 0,
        val tabContent: Map<Int, YouTubeChannelTabContent> = emptyMap(),
    ) : YouTubeChannelUiState

    data class Error(val message: String) : YouTubeChannelUiState
}

@HiltViewModel
class YouTubeChannelViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val channelId: String = savedStateHandle.get<String>("channelId").orEmpty()

    private val _uiState = MutableStateFlow<YouTubeChannelUiState>(YouTubeChannelUiState.Loading)
    val uiState: StateFlow<YouTubeChannelUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (channelId.isBlank()) {
            _uiState.value = YouTubeChannelUiState.Error("Invalid channel.")
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = YouTubeChannelUiState.Loading
            YouTubeWeb.channel(channelId)
                .onSuccess { page ->
                    _uiState.value = YouTubeChannelUiState.Ready(page)
                    // Land on the Videos tab (parser picks the first
                    // parametrized tab) — the Home tab alone reads as a broken
                    // page since most channels' home shelves are sparse.
                    selectTab(page.defaultTabIndex)
                }
                .onFailure { error ->
                    reportException(error)
                    _uiState.value = YouTubeChannelUiState.Error(message = youtubeErrorMessage(error))
                }
        }
    }

    fun selectTab(index: Int) {
        val state = _uiState.value as? YouTubeChannelUiState.Ready ?: return
        if (index == state.selectedTabIndex && state.tabContent[index] != null) return
        val tab = state.page.tabs.getOrNull(index) ?: return
        _uiState.value = state.copy(
            selectedTabIndex = index,
            tabContent = state.tabContent + (index to (state.tabContent[index] ?: YouTubeChannelTabContent(isLoading = true))),
        )
        viewModelScope.launch {
            val params = tab.params
            if (params == null) {
                // Home tab: its shelves arrive with the page itself — surface
                // them (an unparsed Home would render an empty, broken list).
                val home = state.page.homeContent
                _uiState.value = (_uiState.value as? YouTubeChannelUiState.Ready)?.let { current ->
                    current.copy(
                        tabContent = current.tabContent + (index to YouTubeChannelTabContent(
                            videos = home?.videos.orEmpty(),
                            shorts = home?.shorts.orEmpty(),
                            playlists = home?.playlists.orEmpty(),
                            isLoading = false,
                        )),
                    )
                } ?: return@launch
                return@launch
            }
            YouTubeWeb.channelTab(state.page.channel.id, params)
                .onSuccess { content ->
                    val current = _uiState.value as? YouTubeChannelUiState.Ready ?: return@onSuccess
                    if (current.selectedTabIndex != index) return@onSuccess
                    _uiState.value = current.copy(
                        tabContent = current.tabContent + (index to YouTubeChannelTabContent(
                            videos = content.videos,
                            shorts = content.shorts,
                            playlists = content.playlists,
                            continuation = content.continuation,
                        )),
                    )
                }
                .onFailure { error ->
                    reportException(error)
                    val current = _uiState.value as? YouTubeChannelUiState.Ready ?: return@onFailure
                    if (current.selectedTabIndex != index) return@onFailure
                    _uiState.value = current.copy(
                        tabContent = current.tabContent + (index to YouTubeChannelTabContent(
                            error = youtubeErrorMessage(error),
                        )),
                    )
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value as? YouTubeChannelUiState.Ready ?: return
        val index = state.selectedTabIndex
        val content = state.tabContent[index] ?: return
        val continuation = content.continuation ?: return
        if (content.isLoading || content.error != null) return
        viewModelScope.launch {
            YouTubeWeb.channelTabContinuation(continuation)
                .onSuccess { page ->
                    val current = _uiState.value as? YouTubeChannelUiState.Ready ?: return@onSuccess
                    if (current.selectedTabIndex != index) return@onSuccess
                    val existing = current.tabContent[index] ?: return@onSuccess
                    _uiState.value = current.copy(
                        tabContent = current.tabContent + (index to existing.copy(
                            videos = (existing.videos + page.videos).distinctBy { it.id },
                            shorts = (existing.shorts + page.shorts).distinctBy { it.id },
                            playlists = (existing.playlists + page.playlists).distinctBy { it.id },
                            continuation = page.continuation,
                        )),
                    )
                }
                .onFailure { error ->
                    reportException(error)
                    val current = _uiState.value as? YouTubeChannelUiState.Ready ?: return@onFailure
                    if (current.selectedTabIndex != index) return@onFailure
                    val existing = current.tabContent[index] ?: return@onFailure
                    _uiState.value = current.copy(
                        tabContent = current.tabContent + (index to existing.copy(continuation = null)),
                    )
                }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Playlist
// ─────────────────────────────────────────────────────────────────────────────

sealed interface YouTubePlaylistUiState {
    data object Loading : YouTubePlaylistUiState
    data class Ready(
        val page: WebPlaylistPage,
        val isLoadingMore: Boolean = false,
    ) : YouTubePlaylistUiState

    data class Error(val message: String) : YouTubePlaylistUiState
}

@HiltViewModel
class YouTubePlaylistViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val playlistId: String = savedStateHandle.get<String>("playlistId").orEmpty()

    private val _uiState = MutableStateFlow<YouTubePlaylistUiState>(YouTubePlaylistUiState.Loading)
    val uiState: StateFlow<YouTubePlaylistUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (playlistId.isBlank()) {
            _uiState.value = YouTubePlaylistUiState.Error("Invalid playlist.")
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = YouTubePlaylistUiState.Loading
            YouTubeWeb.playlist(playlistId)
                .onSuccess { page ->
                    _uiState.value = YouTubePlaylistUiState.Ready(page)
                }
                .onFailure { error ->
                    reportException(error)
                    _uiState.value = YouTubePlaylistUiState.Error(message = youtubeErrorMessage(error))
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value as? YouTubePlaylistUiState.Ready ?: return
        val continuation = state.page.continuation ?: return
        if (state.isLoadingMore || loadMoreJob?.isActive == true) return
        loadMoreJob = viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            YouTubeWeb.playlistContinuation(continuation)
                .onSuccess { (videos, nextContinuation) ->
                    val current = _uiState.value as? YouTubePlaylistUiState.Ready ?: return@onSuccess
                    _uiState.value = current.copy(
                        page = current.page.copy(
                            videos = (current.page.videos + videos).distinctBy { it.id },
                            continuation = nextContinuation,
                        ),
                        isLoadingMore = false,
                    )
                }
                .onFailure { error ->
                    reportException(error)
                    val current = _uiState.value as? YouTubePlaylistUiState.Ready ?: return@onFailure
                    _uiState.value = current.copy(isLoadingMore = false)
                }
        }
    }
}
