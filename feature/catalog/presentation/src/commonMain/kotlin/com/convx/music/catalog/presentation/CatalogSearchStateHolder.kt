package com.convx.music.catalog.presentation

import com.convx.music.catalog.*
import com.convx.music.playback.PlaybackSettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class CatalogSearchState(
    val query: String = "",
    val isEditing: Boolean = false,
    val suggestions: List<String> = emptyList(),
    val suggestedEntries: List<CatalogEntry> = emptyList(),
    val recentQueries: List<String> = emptyList(),
    val landing: List<CatalogSection> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val online: Boolean = true,
)

/** Suggestion requests are independent of result navigation and never survive a scope change. */
class CatalogSearchStateHolder(
    private val catalog: MusicCatalogRepository,
    private val settings: PlaybackSettingsRepository,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutable = MutableStateFlow(CatalogSearchState())
    val state = mutable.asStateFlow()
    private var request: Job? = null
    private var landingRequest: Job? = null

    init {
        scope.launch {
            settings.observeSettings().collect { preferences ->
                mutable.update { it.copy(recentQueries = if (preferences.recordSearchHistory) preferences.recentSearches else emptyList()) }
            }
        }
    }

    fun setOnline(online: Boolean) {
        request?.cancel()
        landingRequest?.cancel()
        mutable.update { it.copy(online = online, suggestions = emptyList(), suggestedEntries = emptyList(), isLoadingSuggestions = false) }
        if (!online || mutable.value.landing.isNotEmpty()) return
        landingRequest = scope.launch {
            val result = withContext(workDispatcher) { catalog.explore() }
            if (result is CatalogResult.Success && mutable.value.online) mutable.update { it.copy(landing = result.value.sections) }
        }
    }

    fun editQuery(query: String) {
        request?.cancel()
        val key = query.trim()
        val online = mutable.value.online
        mutable.update { it.copy(query = query, isEditing = true, suggestions = emptyList(), suggestedEntries = emptyList(), isLoadingSuggestions = online && key.isNotEmpty()) }
        if (!online || key.isEmpty()) return
        request = scope.launch {
            delay(250)
            val result = withContext(workDispatcher) { catalog.searchSuggestions(key) }
            if (mutable.value.query.trim() != key || !mutable.value.isEditing || !mutable.value.online) return@launch
            mutable.update { current -> current.copy(
                isLoadingSuggestions = false,
                suggestions = (result as? CatalogResult.Success)?.value?.queries.orEmpty().filter(String::isNotBlank).distinctBy(String::lowercase).take(8),
                suggestedEntries = (result as? CatalogResult.Success)?.value?.entries.orEmpty().distinctBy { it.id }.take(5),
            ) }
        }
    }

    fun submit(query: String = mutable.value.query): String {
        request?.cancel()
        val key = query.trim()
        mutable.update { it.copy(query = key, isEditing = false, suggestions = emptyList(), suggestedEntries = emptyList(), isLoadingSuggestions = false) }
        if (key.isNotEmpty()) scope.launch {
            settings.updateSettings { preferences ->
                if (!preferences.recordSearchHistory) preferences else preferences.copy(
                    recentSearches = (listOf(key) + preferences.recentSearches.filterNot { it.equals(key, true) }).take(50),
                )
            }
        }
        return key
    }

    fun dismissSuggestions() {
        request?.cancel()
        mutable.update { it.copy(isEditing = false, suggestions = emptyList(), suggestedEntries = emptyList(), isLoadingSuggestions = false) }
    }

    fun removeRecent(query: String) { scope.launch { settings.updateSettings { it.copy(recentSearches = it.recentSearches.filterNot { recent -> recent == query }) } } }
    fun clearHistory() { scope.launch { settings.updateSettings { it.copy(recentSearches = emptyList()) } } }
    fun close() = scope.cancel()
}
