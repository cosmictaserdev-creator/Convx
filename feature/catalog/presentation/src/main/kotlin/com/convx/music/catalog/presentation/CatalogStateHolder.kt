package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.catalog.CatalogHomePage
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.CatalogSearchFilter
import com.convx.music.catalog.MusicCatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class CatalogScreenState(
    val sections: List<CatalogSection> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val message: String? = null,
    val continuationError: String? = null,
    val continuationToken: String? = null,
    val pageTitle: String = "Listen Now",
    val canGoBack: Boolean = false,
    val currentEntry: CatalogEntry? = null,
    val searchFilter: CatalogSearchFilter = CatalogSearchFilter.ALL,
    val homeFilters: List<CatalogEntry.Browse> = emptyList(),
    val selectedHomeFilter: CatalogEntry.Browse? = null,
)

private data class CatalogContinuationPage(
    val sections: List<CatalogSection>,
    val continuationToken: String?,
)

private data class OpenedCatalogPage(
    val sections: List<CatalogSection>,
    val entry: CatalogEntry? = null,
    val continuationToken: String? = null,
    val continuationLoader: CatalogContinuationLoader? = null,
)

private data class CatalogNavigationSnapshot(
    val state: CatalogScreenState,
    val continuationLoader: CatalogContinuationLoader?,
    val continuationPageIndex: Int,
)

private typealias CatalogContinuationLoader = suspend (String) -> CatalogResult<CatalogContinuationPage>

class CatalogStateHolder(
    private val catalog: MusicCatalogRepository,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutableState = MutableStateFlow(CatalogScreenState())
    private val navigationStack = mutableListOf<CatalogNavigationSnapshot>()
    private var request: Job? = null
    private var continuationRequest: Job? = null
    private var continuationLoader: CatalogContinuationLoader? = null
    private var continuationPageIndex = 0
    private var homeRoot: CatalogNavigationSnapshot? = null

    val state: StateFlow<CatalogScreenState> = mutableState.asStateFlow()

    fun clear(title: String) {
        cancelRequests()
        navigationStack.clear()
        continuationLoader = null
        continuationPageIndex = 0
        mutableState.value = CatalogScreenState(pageTitle = title)
    }

    fun loadHome(includeRecentActivity: Boolean = false) {
        cancelRequests()
        navigationStack.clear()
        homeRoot = null
        continuationLoader = { token -> catalog.home(token).map(CatalogHomePage::toContinuationPage) }
        continuationPageIndex = 0
        mutableState.value = CatalogScreenState(isLoading = true, pageTitle = "Listen Now")
        request = scope.launch {
            coroutineScope {
                val ready = kotlinx.coroutines.CompletableDeferred<Unit>()
                val extras = mutableMapOf<Int, List<CatalogSection>>()
                suspend fun publish(rank: Int, sections: List<CatalogSection>) {
                    ready.await()
                    extras[rank] = sections.filter { it.entries.isNotEmpty() }.mapIndexed { index, section ->
                        section.copy(id = "home-extra-$rank-$index")
                    }
                    val current = mutableState.value
                    val base = current.sections.filterNot { it.id.startsWith("home-extra-") }
                    mutableState.value = current.copy(sections = extras.entries.filter { it.key < 100 }.sortedBy { it.key }.flatMap { it.value } +
                        base + extras.entries.filter { it.key >= 100 }.sortedBy { it.key }.flatMap { it.value })
                }
                launch {
                    val page = onWorkDispatcher { catalog.explore() } as? CatalogResult.Success ?: return@launch
                    publish(20, page.value.sections.filter { it.entries.any { entry -> entry is CatalogEntry.Album } })
                    publish(120, page.value.sections.filterNot { it.entries.any { entry -> entry is CatalogEntry.Album } })
                }
                launch {
                    val charts = onWorkDispatcher { catalog.charts() } as? CatalogResult.Success ?: return@launch
                    val top = charts.value.sections.firstOrNull { it.id.startsWith("charts-top-") } ?: charts.value.sections.firstOrNull()
                    if (top != null) publish(0, listOf(top.copy(title = "Top Picks for You")))
                    charts.value.sections.firstOrNull { it.id.startsWith("charts-trending-") }?.let { publish(30, listOf(it.copy(title = "Trending"))) }
                }
                if (includeRecentActivity) {
                    launch {
                        val history = onWorkDispatcher { catalog.library(CatalogLibraryCollection.HISTORY) } as? CatalogResult.Success ?: return@launch
                        val recent = history.value.entries.distinctBy { it.id }.take(20)
                        if (recent.isNotEmpty()) publish(40, listOf(CatalogSection("recent", "Listen Again", recent)))
                        recent.filterIsInstance<CatalogEntry.Track>().firstOrNull()?.let { seed ->
                            val recommendations = onWorkDispatcher { catalog.related(seed.id) } as? CatalogResult.Success ?: return@let
                            publish(130, recommendations.value.sections.map { section ->
                                if (section.entries.all { it is CatalogEntry.Track }) section.copy(title = "Daily Discover")
                                else section.copy(subtitle = "Because you listened to ${seed.title}")
                            })
                        }
                    }
                    launch {
                        val playlists = onWorkDispatcher { catalog.library(CatalogLibraryCollection.PLAYLISTS) } as? CatalogResult.Success ?: return@launch
                        publish(110, listOf(CatalogSection("account-playlists", "Your Playlists", playlists.value.entries)))
                    }
                }
                when (val result = onWorkDispatcher { catalog.home() }) {
                    is CatalogResult.Success -> mutableState.value = CatalogScreenState(sections = result.value.sections,
                        continuationToken = result.value.continuationToken, homeFilters = result.value.filters, pageTitle = "Listen Now")
                    is CatalogResult.Failure -> mutableState.value = CatalogScreenState(pageTitle = "Listen Now", message = result.error.toMessage())
                }
                ready.complete(Unit)
            }
        }
    }

    fun loadExplore() = loadCatalogPage("Explore", catalog::explore)

    fun loadRadio() = loadCatalogPage("Radio", load = {
        when (val result = catalog.search("music radio", com.convx.music.catalog.CatalogSearchFilter.PLAYLISTS)) {
            is CatalogResult.Success -> CatalogResult.Success(CatalogHomePage(result.value.sections))
            is CatalogResult.Failure -> result
        }
    })

    fun selectHomeFilter(filter: CatalogEntry.Browse?) {
        if (filter == mutableState.value.selectedHomeFilter) return
        cancelRequests()
        navigationStack.clear()
        if (filter == null) {
            homeRoot?.let { previous ->
                continuationLoader = previous.continuationLoader
                continuationPageIndex = previous.continuationPageIndex
                mutableState.value = previous.state
            } ?: loadHome()
            return
        }
        val previous = mutableState.value
        if (previous.selectedHomeFilter == null) homeRoot = navigationSnapshot()
        continuationLoader = null
        continuationPageIndex = 0
        mutableState.value = CatalogScreenState(isLoading = true, pageTitle = "Listen Now",
            homeFilters = previous.homeFilters, selectedHomeFilter = filter)
        request = scope.launch {
            when (val result = onWorkDispatcher { catalog.browse(filter.id, filter.params) }) {
                is CatalogResult.Success -> mutableState.value = mutableState.value.copy(
                    sections = result.value.sections, isLoading = false)
                is CatalogResult.Failure -> mutableState.value = mutableState.value.copy(
                    isLoading = false, message = result.error.toMessage())
            }
        }
    }

    fun loadCharts() = loadCatalogPage(
        title = "Charts",
        load = { catalog.charts() },
        loadContinuation = { token -> catalog.charts(token).map(CatalogHomePage::toContinuationPage) },
    )

    private fun loadCatalogPage(
        title: String,
        load: suspend () -> CatalogResult<CatalogHomePage>,
        loadContinuation: CatalogContinuationLoader? = null,
    ) {
        cancelRequests()
        navigationStack.clear()
        continuationLoader = loadContinuation
        continuationPageIndex = 0
        request = scope.launch {
            mutableState.value = CatalogScreenState(isLoading = true, pageTitle = title)
            when (val result = onWorkDispatcher(load)) {
                is CatalogResult.Success -> {
                    val page = result.value
                    mutableState.value = CatalogScreenState(
                        sections = page.sections,
                        continuationToken = page.continuationToken,
                        pageTitle = title,
                        homeFilters = page.filters,
                    )
                }
                is CatalogResult.Failure -> mutableState.value = CatalogScreenState(
                    message = result.error.toMessage(),
                    pageTitle = title,
                )
            }
        }
    }

    fun search(query: String, filter: CatalogSearchFilter = mutableState.value.searchFilter, immediately: Boolean = false) {
        cancelRequests()
        navigationStack.clear()
        continuationPageIndex = 0
        if (query.isBlank()) {
            continuationLoader = null
            mutableState.value = CatalogScreenState(
                searchQuery = query,
                pageTitle = "Search",
                searchFilter = filter,
            )
            return
        }

        val normalizedQuery = query.trim()
        continuationLoader = { token ->
            catalog.search(normalizedQuery, filter, token).map { page ->
                CatalogContinuationPage(page.sections, page.continuationToken)
            }
        }
        mutableState.value = CatalogScreenState(
            isLoading = true,
            searchQuery = query,
            pageTitle = "Search",
            searchFilter = filter,
        )
        request = scope.launch {
            if (!immediately) delay(350)
            when (val result = onWorkDispatcher {
                catalog.search(normalizedQuery, filter)
            }) {
                is CatalogResult.Success -> mutableState.value = mutableState.value.copy(
                    sections = result.value.sections,
                    isLoading = false,
                    continuationToken = result.value.continuationToken,
                )
                is CatalogResult.Failure -> mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    message = result.error.toMessage(),
                )
            }
        }
    }

    fun loadLibrary(collection: CatalogLibraryCollection = CatalogLibraryCollection.RECENT_ACTIVITY) {
        cancelRequests()
        navigationStack.clear()
        continuationPageIndex = 0
        continuationLoader = { token ->
            catalog.library(collection, token).map { page ->
                CatalogContinuationPage(
                    sections = listOf(librarySection(collection, page.entries)),
                    continuationToken = page.continuationToken,
                )
            }
        }
        val title = collection.title()
        request = scope.launch {
            mutableState.value = CatalogScreenState(isLoading = true, pageTitle = title)
            when (val result = onWorkDispatcher { catalog.library(collection) }) {
                is CatalogResult.Success -> mutableState.value = CatalogScreenState(
                    sections = listOf(librarySection(collection, result.value.entries)),
                    continuationToken = result.value.continuationToken,
                    pageTitle = title,
                )
                is CatalogResult.Failure -> mutableState.value = CatalogScreenState(
                    message = result.error.toMessage(),
                    pageTitle = title,
                )
            }
        }
    }

    fun loadMore() {
        val current = mutableState.value
        val token = current.continuationToken ?: return
        val loadContinuation = continuationLoader ?: return
        if (current.isLoading || current.isLoadingMore) return

        continuationRequest?.cancel()
        continuationRequest = scope.launch {
            mutableState.value = current.copy(isLoadingMore = true, continuationError = null)
            when (val result = onWorkDispatcher { loadContinuation(token) }) {
                is CatalogResult.Success -> {
                    if (result.value.continuationToken == token) {
                        mutableState.value = mutableState.value.copy(
                            isLoadingMore = false,
                            continuationError = "Couldn't load more items. Try again.",
                        )
                        return@launch
                    }
                    val pageIndex = continuationPageIndex + 1
                    val appendedSections = result.value.sections.mapIndexed { index, section ->
                        section.copy(id = "${section.id}-page-$pageIndex-$index")
                    }
                    continuationPageIndex = pageIndex
                    mutableState.value = mutableState.value.copy(
                        sections = mutableState.value.sections + appendedSections,
                        isLoadingMore = false,
                        continuationToken = result.value.continuationToken,
                        continuationError = null,
                    )
                }
                is CatalogResult.Failure -> mutableState.value = mutableState.value.copy(
                    isLoadingMore = false,
                    continuationError = result.error.toMessage(),
                )
            }
        }
    }

    fun openEntry(entry: CatalogEntry) {
        val id = when (entry) {
            is CatalogEntry.Track -> return
            is CatalogEntry.Album -> entry.id
            is CatalogEntry.Artist -> entry.id
            is CatalogEntry.Playlist -> entry.id
            is CatalogEntry.Browse -> entry.id
        }
        val previous = navigationSnapshot()
        cancelRequests()
        continuationLoader = null
        continuationPageIndex = 0
        request = scope.launch {
            mutableState.value = previous.state.copy(isLoading = true, message = null)
            val page = when (entry) {
                is CatalogEntry.Track -> return@launch
                is CatalogEntry.Album -> onWorkDispatcher {
                    catalog.album(id).map { album ->
                        OpenedCatalogPage(
                            sections = listOf(CatalogSection("album-${id.value}", album.album.title, album.tracks)) +
                                if (album.related.isNotEmpty()) listOf(CatalogSection("album-related-${id.value}", "Other Versions", album.related)) else emptyList(),
                            entry = album.album,
                        )
                    }
                }
                is CatalogEntry.Artist -> onWorkDispatcher {
                    catalog.artist(id).map { artist ->
                        OpenedCatalogPage(artist.sections, entry = artist.artist)
                    }
                }
                is CatalogEntry.Playlist -> onWorkDispatcher {
                    catalog.playlist(id).map { playlist ->
                        OpenedCatalogPage(
                            entry = playlist.playlist,
                            sections = listOf(
                                CatalogSection("playlist-${id.value}", playlist.playlist.title, playlist.tracks),
                            ) + if (playlist.related.isNotEmpty()) listOf(CatalogSection("playlist-related-${id.value}", "You Might Also Like", playlist.related)) else emptyList(),
                            continuationToken = playlist.continuationToken,
                            continuationLoader = { token ->
                                catalog.playlistContinuation(id, token).map { continuation ->
                                    CatalogContinuationPage(
                                        sections = listOf(
                                            CatalogSection(
                                                "playlist-${id.value}-tracks",
                                                playlist.playlist.title,
                                                continuation.tracks,
                                            ),
                                        ),
                                        continuationToken = continuation.continuationToken,
                                    )
                                }
                            },
                        )
                    }
                }
                is CatalogEntry.Browse -> onWorkDispatcher {
                    catalog.browse(id, entry.params).map { browse ->
                        OpenedCatalogPage(browse.sections)
                    }
                }
            }
            when (page) {
                is CatalogResult.Success -> {
                    navigationStack += previous
                    continuationLoader = page.value.continuationLoader
                    continuationPageIndex = 0
                    mutableState.value = CatalogScreenState(
                        sections = page.value.sections,
                        continuationToken = page.value.continuationToken,
                        pageTitle = entry.title,
                        canGoBack = true,
                        currentEntry = page.value.entry ?: entry,
                    )
                }
                is CatalogResult.Failure -> {
                    continuationLoader = previous.continuationLoader
                    continuationPageIndex = previous.continuationPageIndex
                    mutableState.value = previous.state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        message = page.error.toMessage(),
                    )
                }
            }
        }
    }

    fun goBack() {
        cancelRequests()
        val previous = navigationStack.removeLastOrNull() ?: return
        continuationLoader = previous.continuationLoader
        continuationPageIndex = previous.continuationPageIndex
        mutableState.value = previous.state.copy(
            isLoading = false,
            isLoadingMore = false,
            canGoBack = navigationStack.isNotEmpty(),
        )
    }

    fun close() {
        cancelRequests()
        scope.cancel()
    }

    private fun navigationSnapshot() = CatalogNavigationSnapshot(
        state = mutableState.value,
        continuationLoader = continuationLoader,
        continuationPageIndex = continuationPageIndex,
    )

    private fun cancelRequests() {
        request?.cancel()
        request = null
        continuationRequest?.cancel()
        continuationRequest = null
    }

    private suspend fun <T> onWorkDispatcher(block: suspend () -> T): T =
        withContext(workDispatcher) { block() }
}

private fun CatalogHomePage.toContinuationPage() = CatalogContinuationPage(sections, continuationToken)

private fun librarySection(collection: CatalogLibraryCollection, entries: List<CatalogEntry>) = CatalogSection(
    id = "library-${collection.name.lowercase()}",
    title = collection.title(),
    entries = entries,
)

private fun CatalogLibraryCollection.title(): String = when (this) {
    CatalogLibraryCollection.HISTORY -> "History"
    CatalogLibraryCollection.RECENT_ACTIVITY -> "Recent activity"
    CatalogLibraryCollection.PLAYLISTS -> "Playlists"
    CatalogLibraryCollection.LIKED_TRACKS -> "Liked songs"
    CatalogLibraryCollection.LIKED_ALBUMS -> "Albums"
    CatalogLibraryCollection.ARTISTS -> "Artists"
    CatalogLibraryCollection.UPLOADED_TRACKS -> "Uploaded songs"
}

fun CatalogError.toMessage(): String = when (this) {
    CatalogError.Offline -> "You appear to be offline."
    CatalogError.Unauthorized -> "Sign in to load this content."
    is CatalogError.RateLimited -> "The music service is busy. Try again shortly."
    CatalogError.NotFound -> "This item is no longer available."
    CatalogError.Unavailable -> "The music service is temporarily unavailable."
    CatalogError.InvalidResponse -> "The music service returned an unsupported response."
    CatalogError.Unknown -> "Couldn't load music right now."
}

fun CatalogScreenState.visibleEntries(): List<CatalogEntry> = sections.flatMap(CatalogSection::entries)

private inline fun <T, R> CatalogResult<T>.map(transform: (T) -> R): CatalogResult<R> = when (this) {
    is CatalogResult.Success -> CatalogResult.Success(transform(value))
    is CatalogResult.Failure -> this
}
