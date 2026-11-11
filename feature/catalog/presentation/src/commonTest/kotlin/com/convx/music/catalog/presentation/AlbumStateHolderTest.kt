package com.convx.music.catalog.presentation

import com.convx.music.catalog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumStateHolderTest {
    private val albumId = CatalogId("p", "album")

    private fun track(n: Int, title: String, artist: String = "A", ms: Long = 1_000L * n, explicit: Boolean = false, video: Boolean = false) =
        CatalogEntry.Track(
            CatalogId("p", "t$n"), title, emptyList(), listOf(CatalogArtist(null, artist)), null, ms, explicit, video, true,
        )

    private fun page(vararg tracks: CatalogEntry.Track) = CatalogAlbumPage(
        CatalogEntry.Album(albumId, "Album", emptyList(), emptyList(), "2024", tracks.size), tracks.toList(),
    )

    private fun holder(testScope: TestScope, result: CatalogResult<CatalogAlbumPage>): AlbumStateHolder {
        val dispatcher = StandardTestDispatcher(testScope.testScheduler)
        return AlbumStateHolder(FakeCatalog(result), albumId, dispatcher, dispatcher)
    }

    @Test fun loadedPublishesTracksAndDuration() = runTest {
        val holder = holder(this, CatalogResult.Success(page(track(1, "One"), track(2, "Two"))))
        holder.load()
        assertTrue(holder.state.value.isLoading)
        advanceUntilIdle()
        val state = holder.state.value
        assertFalse(state.isLoading)
        assertEquals(3_000L, state.totalDurationMillis)
        assertEquals(listOf(1, 2), state.visibleTracks.map { it.number })
        holder.close()
    }

    @Test fun filtersAndSortKeepTheAlbumNumbering() = runTest {
        val holder = holder(this, CatalogResult.Success(page(
            track(1, "Zebra"), track(2, "Apple", explicit = true), track(3, "Mango", artist = "Neil"), track(4, "Clip", video = true),
        )))
        holder.load()
        advanceUntilIdle()
        holder.setHideExplicit(true)
        holder.setHideVideos(true)
        assertEquals(listOf(1, 3), holder.state.value.visibleTracks.map { it.number })
        holder.setSort(AlbumSort.TITLE)
        assertEquals(listOf("Mango", "Zebra"), holder.state.value.visibleTracks.map { it.track.title })
        holder.setQuery("neil")
        assertEquals(listOf(3), holder.state.value.visibleTracks.map { it.number })
        holder.close()
    }

    @Test fun anEmptyOrFailedAlbumShowsTheErrorPage() = runTest {
        val empty = holder(this, CatalogResult.Success(page()))
        empty.load()
        advanceUntilIdle()
        assertTrue(empty.state.value.showError)
        assertEquals(CatalogError.Unavailable, empty.state.value.error)
        val failed = holder(this, CatalogResult.Failure(CatalogError.Offline))
        failed.load()
        advanceUntilIdle()
        assertEquals(CatalogError.Offline, failed.state.value.error)
        empty.close()
        failed.close()
    }

    @Test fun aFailedRefreshKeepsTheTracksAlreadyShown() = runTest {
        val catalog = FakeCatalog(CatalogResult.Success(page(track(1, "One"))))
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = AlbumStateHolder(catalog, albumId, dispatcher, dispatcher)
        holder.load()
        advanceUntilIdle()
        catalog.result = CatalogResult.Failure(CatalogError.RateLimited(null))
        holder.load()
        advanceUntilIdle()
        assertFalse(holder.state.value.showError)
        assertEquals(1, holder.state.value.tracks.size)
        holder.close()
    }

    @Test fun selectionFollowsTheFilterAndSelectAllToggles() = runTest {
        val holder = holder(this, CatalogResult.Success(page(track(1, "One"), track(2, "Two", explicit = true), track(3, "Three"))))
        holder.load()
        advanceUntilIdle()
        holder.startSelection("t1")
        holder.toggleSelected("t2")
        assertEquals(setOf("t1", "t2"), holder.state.value.selectedIds)
        holder.setHideExplicit(true)
        assertEquals(setOf("t1"), holder.state.value.selectedIds)
        holder.toggleSelectAll()
        assertTrue(holder.state.value.allVisibleSelected)
        holder.toggleSelectAll()
        assertTrue(holder.state.value.selectedIds.isEmpty())
        holder.clearSelection()
        assertFalse(holder.state.value.selecting)
        holder.close()
    }

    @Test fun downloadButtonDownloadsCancelsOrAsksBeforeRemoving() = runTest {
        val downloads = FakeDownloads()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = AlbumStateHolder(FakeCatalog(CatalogResult.Success(page(track(1, "One")))), albumId, dispatcher, dispatcher, downloads)
        holder.load()
        advanceUntilIdle()
        holder.onDownloadClick()
        assertEquals(listOf("download"), downloads.calls)
        downloads.state.value = AlbumDownloadState.DOWNLOADING
        advanceUntilIdle()
        holder.onDownloadClick()
        assertEquals(listOf("download", "cancel"), downloads.calls)
        downloads.state.value = AlbumDownloadState.DOWNLOADED
        advanceUntilIdle()
        holder.onDownloadClick()
        assertTrue(holder.state.value.confirmRemoveDownload)
        assertEquals(2, downloads.calls.size)
        holder.confirmRemoveDownload()
        assertEquals(listOf("download", "cancel", "remove"), downloads.calls)
        assertFalse(holder.state.value.confirmRemoveDownload)
        holder.close()
    }

    @Test fun discHeadersAppearOnlyForMultiDiscAlbumsInAlbumOrder() = runTest {
        val holder = holder(this, CatalogResult.Success(page(
            track(1, "A1").copy(discNumber = 1), track(2, "A2").copy(discNumber = 1), track(3, "B1").copy(discNumber = 2),
        )))
        holder.load()
        advanceUntilIdle()
        val items = holder.state.value.listItems
        assertEquals(
            listOf("disc1", "t1", "t2", "disc2", "t3"),
            items.map { if (it is AlbumListItem.DiscHeader) "disc${it.disc}" else (it as AlbumListItem.Track).row.track.id.value },
        )
        holder.setSort(AlbumSort.TITLE)
        assertTrue(holder.state.value.listItems.none { it is AlbumListItem.DiscHeader })
        holder.setSort(AlbumSort.ALBUM_ORDER)
        holder.setQuery("b1")
        assertTrue(holder.state.value.listItems.none { it is AlbumListItem.DiscHeader })
        holder.close()
    }

    @Test fun singleDiscAlbumsGetNoHeaders() = runTest {
        val holder = holder(this, CatalogResult.Success(page(track(1, "One"), track(2, "Two"))))
        holder.load()
        advanceUntilIdle()
        assertTrue(holder.state.value.listItems.all { it is AlbumListItem.Track })
        holder.close()
    }

    private class FakeDownloads : AlbumDownloads {
        val calls = mutableListOf<String>()
        val state = kotlinx.coroutines.flow.MutableStateFlow(AlbumDownloadState.NONE)
        override fun state(tracks: List<CatalogEntry.Track>) = state
        override fun download(tracks: List<CatalogEntry.Track>) { calls += "download" }
        override fun cancel(tracks: List<CatalogEntry.Track>) { calls += "cancel" }
        override fun remove(tracks: List<CatalogEntry.Track>) { calls += "remove" }
    }

    private class FakeCatalog(var result: CatalogResult<CatalogAlbumPage>) : MusicCatalogRepository {
        override suspend fun album(id: CatalogId) = result
        override suspend fun home(continuationToken: String?) = unused<CatalogHomePage>()
        override suspend fun explore() = unused<CatalogHomePage>()
        override suspend fun charts(continuationToken: String?) = unused<CatalogHomePage>()
        override suspend fun browse(id: CatalogId, params: String?) = unused<CatalogHomePage>()
        override suspend fun search(query: String, filter: CatalogSearchFilter, continuationToken: String?) = unused<CatalogSearchPage>()
        override suspend fun playlist(id: CatalogId) = unused<CatalogPlaylistPage>()
        override suspend fun playlistContinuation(id: CatalogId, continuationToken: String) = unused<CatalogTrackPage>()
        override suspend fun artist(id: CatalogId) = unused<CatalogArtistPage>()
        override suspend fun library(collection: CatalogLibraryCollection, continuationToken: String?) = unused<CatalogLibraryPage>()
        private fun <T> unused(): CatalogResult<T> = CatalogResult.Failure(CatalogError.Unavailable)
    }
}
