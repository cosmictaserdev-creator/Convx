package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogAlbumPage
import com.convx.music.catalog.CatalogArtistPage
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.catalog.CatalogHomePage
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogLibraryPage
import com.convx.music.catalog.CatalogPlaylistPage
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.CatalogSearchFilter
import com.convx.music.catalog.CatalogSearchPage
import com.convx.music.catalog.CatalogTrackPage
import com.convx.music.catalog.MusicCatalogRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CatalogStateHolderTest {
    @Test
    fun loadMoreAppendsTheNextPageAndAdvancesTheCursor() = runTest {
        val repository = FakeCatalogRepository().apply {
            homePages[null] = CatalogResult.Success(page("home", "cursor-1", track("one")))
            homePages["cursor-1"] = CatalogResult.Success(page("home", null, track("two")))
        }
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)

        holder.loadHome()
        advanceUntilIdle()
        holder.loadMore()
        advanceUntilIdle()

        assertEquals(listOf("one", "two"), holder.state.value.sections.flatMap { it.entries }.map { it.id.value })
        assertNull(holder.state.value.continuationToken)
        assertFalse(holder.state.value.isLoadingMore)
        holder.close()
    }

    @Test
    fun repeatedContinuationTokenStopsAutomaticPagination() = runTest {
        val repository = FakeCatalogRepository().apply {
            homePages[null] = CatalogResult.Success(page("home", "cursor-1", track("one")))
            homePages["cursor-1"] = CatalogResult.Success(page("home", "cursor-1", track("duplicate-page")))
        }
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)

        holder.loadHome()
        advanceUntilIdle()
        holder.loadMore()
        advanceUntilIdle()

        assertEquals(listOf("one"), holder.state.value.sections.flatMap { it.entries }.map { it.id.value })
        assertEquals("cursor-1", holder.state.value.continuationToken)
        assertTrue(holder.state.value.continuationError != null)
        assertFalse(holder.state.value.isLoadingMore)
        holder.close()
    }

    @Test
    fun failedDetailNavigationRestoresThePreviousPaginationLoader() = runTest {
        val repository = FakeCatalogRepository().apply {
            homePages[null] = CatalogResult.Success(page("home", "cursor-1", track("one")))
            homePages["cursor-1"] = CatalogResult.Success(page("home", null, track("two")))
        }
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)

        holder.loadHome()
        advanceUntilIdle()
        holder.openEntry(album("album"))
        advanceUntilIdle()
        holder.loadMore()
        advanceUntilIdle()

        assertTrue(holder.state.value.message != null)
        assertEquals(listOf("one", "two"), holder.state.value.sections.flatMap { it.entries }.map { it.id.value })
        assertNull(holder.state.value.continuationToken)
        holder.close()
    }

    @Test
    fun searchDebouncesAndTrimsTheSubmittedQuery() = runTest {
        val repository = FakeCatalogRepository()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)

        holder.search("  jazz  ")
        advanceTimeBy(349)
        runCurrent()
        assertTrue(repository.searchQueries.isEmpty())

        advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(listOf("jazz"), repository.searchQueries)
        assertEquals("  jazz  ", holder.state.value.searchQuery)
        holder.close()
    }

    @Test
    fun submittingSearchImmediatelyCancelsThePendingQueryAndUsesTheSelectedCategory() = runTest {
        val repository = FakeCatalogRepository()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)
        holder.search("old query")
        holder.search("radiohead", CatalogSearchFilter.ARTISTS, immediately = true)
        runCurrent()
        assertEquals(listOf("radiohead"), repository.searchQueries)
        assertEquals(listOf(CatalogSearchFilter.ARTISTS), repository.searchFilters)
        assertEquals(CatalogSearchFilter.ARTISTS, holder.state.value.searchFilter)
        holder.close()
    }

    @Test
    fun homeCombinesHighlightsWithProviderShelvesAndKeepsItsPagination() = runTest {
        val repository = FakeCatalogRepository().apply {
            homePages[null] = CatalogResult.Success(page("recommendations", "next", track("personal")))
            explorePage = CatalogResult.Success(page("new-releases", null, album("new-album")))
        }
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = CatalogStateHolder(repository, dispatcher, dispatcher)
        holder.loadHome()
        advanceUntilIdle()
        assertEquals(listOf("new-album", "personal"), holder.state.value.sections.flatMap { it.entries }.map { it.id.value })
        assertEquals("next", holder.state.value.continuationToken)
        holder.close()
    }

    private class FakeCatalogRepository : MusicCatalogRepository {
        val homePages = mutableMapOf<String?, CatalogResult<CatalogHomePage>>()
        val searchQueries = mutableListOf<String>()
        val searchFilters = mutableListOf<CatalogSearchFilter>()
        var explorePage: CatalogResult<CatalogHomePage> = failure()

        override suspend fun home(continuationToken: String?) =
            homePages[continuationToken] ?: failure()

        override suspend fun explore() = explorePage

        override suspend fun charts(continuationToken: String?) = failure<CatalogHomePage>()

        override suspend fun browse(id: CatalogId, params: String?) = failure<CatalogHomePage>()

        override suspend fun search(
            query: String,
            filter: CatalogSearchFilter,
            continuationToken: String?,
        ): CatalogResult<CatalogSearchPage> {
            searchQueries += query
            searchFilters += filter
            return CatalogResult.Success(CatalogSearchPage(query, emptyList()))
        }

        override suspend fun playlist(id: CatalogId) = failure<CatalogPlaylistPage>()

        override suspend fun playlistContinuation(id: CatalogId, continuationToken: String) =
            failure<CatalogTrackPage>()

        override suspend fun album(id: CatalogId) = failure<CatalogAlbumPage>()

        override suspend fun artist(id: CatalogId) = failure<CatalogArtistPage>()

        override suspend fun library(
            collection: CatalogLibraryCollection,
            continuationToken: String?,
        ) = failure<CatalogLibraryPage>()

        private fun <T> failure(): CatalogResult<T> = CatalogResult.Failure(CatalogError.Unknown)
    }

    private companion object {
        fun page(sectionId: String, continuationToken: String?, vararg entries: CatalogEntry) = CatalogHomePage(
            sections = listOf(CatalogSection(sectionId, "Home", entries.toList())),
            continuationToken = continuationToken,
        )

        fun track(id: String) = CatalogEntry.Track(
            id = CatalogId("youtube-music", id),
            title = id,
            artworkUrls = emptyList(),
            artists = emptyList(),
            album = null,
            durationMillis = null,
            isExplicit = false,
            isVideo = false,
            isPlayable = true,
        )

        fun album(id: String) = CatalogEntry.Album(
            id = CatalogId("youtube-music", id),
            title = id,
            artworkUrls = emptyList(),
            artists = emptyList(),
            year = null,
            trackCount = null,
        )
    }
}
