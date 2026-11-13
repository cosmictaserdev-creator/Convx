package com.convx.music.catalog.presentation

import com.convx.music.catalog.*
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogSearchStateHolderTest {
    @Test fun rapidEditsCancelOldSuggestionsAndOnlyPublishTheLatestQuery() = runTest {
        val repository = SuggestionCatalog()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogSearchStateHolder(repository, SearchPreferences(), dispatcher, dispatcher)
        holder.editQuery("old")
        advanceTimeBy(260)
        holder.editQuery("new")
        advanceUntilIdle()
        assertEquals(listOf("old", "new"), repository.requests)
        assertEquals("new", holder.state.value.query)
        assertEquals(listOf("new suggestion"), holder.state.value.suggestions)
        holder.close()
    }

    @Test fun offlineModeCancelsSuggestionsAndDoesNotContactTheProvider() = runTest {
        val repository = SuggestionCatalog()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogSearchStateHolder(repository, SearchPreferences(), dispatcher, dispatcher)
        holder.editQuery("unfinished")
        advanceTimeBy(260)
        holder.setOnline(false)
        holder.editQuery("local song")
        advanceUntilIdle()
        assertEquals(listOf("unfinished"), repository.requests)
        assertTrue(holder.state.value.suggestions.isEmpty())
        assertFalse(holder.state.value.isLoadingSuggestions)
        holder.close()
    }

    @Test fun submittedQueriesPersistOnceAndRespectTheHistoryPreference() = runTest {
        val preferences = SearchPreferences(PlaybackSettings(recentSearches = listOf("Jazz", "Rock")))
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogSearchStateHolder(SuggestionCatalog(), preferences, dispatcher, dispatcher)
        holder.submit("  jazz  ")
        advanceUntilIdle()
        assertEquals(listOf("jazz", "Rock"), preferences.value.value.recentSearches)
        preferences.updateSettings { it.copy(recordSearchHistory = false) }
        holder.submit("private search")
        advanceUntilIdle()
        assertFalse("private search" in preferences.value.value.recentSearches)
        assertTrue(holder.state.value.recentQueries.isEmpty())
        holder.clearHistory()
        advanceUntilIdle()
        assertTrue(preferences.value.value.recentSearches.isEmpty())
        holder.close()
    }

    @Test fun submittingOrClearingTheQueryDismissesOutstandingSuggestions() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogSearchStateHolder(SuggestionCatalog(), SearchPreferences(), dispatcher, dispatcher)
        holder.editQuery("hello")
        advanceTimeBy(260)
        assertEquals("hello", holder.submit())
        advanceUntilIdle()
        assertTrue(holder.state.value.suggestions.isEmpty())
        assertFalse(holder.state.value.isEditing)
        holder.editQuery("")
        advanceUntilIdle()
        assertFalse(holder.state.value.isLoadingSuggestions)
        holder.close()
    }
}

private class SearchPreferences(initial: PlaybackSettings = PlaybackSettings()) : PlaybackSettingsRepository {
    val value = MutableStateFlow(initial)
    override fun observeSettings() = value
    override suspend fun getSettings() = value.value
    override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings =
        transform(value.value).normalized().also { value.value = it }
}

private class SuggestionCatalog : MusicCatalogRepository {
    val requests = mutableListOf<String>()
    override suspend fun searchSuggestions(query: String): CatalogResult<CatalogSearchSuggestions> {
        requests += query
        delay(1_000)
        return CatalogResult.Success(CatalogSearchSuggestions(listOf("$query suggestion")))
    }
    override suspend fun home(continuationToken: String?) = unavailable<CatalogHomePage>()
    override suspend fun explore() = unavailable<CatalogHomePage>()
    override suspend fun charts(continuationToken: String?) = unavailable<CatalogHomePage>()
    override suspend fun browse(id: CatalogId, params: String?) = unavailable<CatalogHomePage>()
    override suspend fun search(query: String, filter: CatalogSearchFilter, continuationToken: String?) = unavailable<CatalogSearchPage>()
    override suspend fun playlist(id: CatalogId) = unavailable<CatalogPlaylistPage>()
    override suspend fun playlistContinuation(id: CatalogId, continuationToken: String) = unavailable<CatalogTrackPage>()
    override suspend fun album(id: CatalogId) = unavailable<CatalogAlbumPage>()
    override suspend fun artist(id: CatalogId) = unavailable<CatalogArtistPage>()
    override suspend fun library(collection: CatalogLibraryCollection, continuationToken: String?) = unavailable<CatalogLibraryPage>()
    private fun <T> unavailable(): CatalogResult<T> = CatalogResult.Failure(CatalogError.Unavailable)
}
