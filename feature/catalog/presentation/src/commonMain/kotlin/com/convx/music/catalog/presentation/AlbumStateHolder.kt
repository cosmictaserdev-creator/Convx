package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.MusicCatalogRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AlbumSort { ALBUM_ORDER, TITLE, DURATION }

enum class AlbumDownloadState { NONE, DOWNLOADING, DOWNLOADED }

/** Offline copies of an album's tracks; the host owns where they are stored. */
interface AlbumDownloads {
    fun state(tracks: List<CatalogEntry.Track>): Flow<AlbumDownloadState>
    fun download(tracks: List<CatalogEntry.Track>)
    fun cancel(tracks: List<CatalogEntry.Track>)
    fun remove(tracks: List<CatalogEntry.Track>)
}

/** What the album list draws: tracks, and a header wherever a multi-disc album starts a new disc. */
sealed interface AlbumListItem {
    data class DiscHeader(val disc: Int) : AlbumListItem
    data class Track(val row: AlbumTrackRow) : AlbumListItem
}

/** A track as listed on the album page; [number] is its place on the album, whatever the filter or sort. */
data class AlbumTrackRow(val number: Int, val track: CatalogEntry.Track)

data class AlbumScreenState(
    val album: CatalogEntry.Album? = null,
    val tracks: List<CatalogEntry.Track> = emptyList(),
    val related: List<CatalogEntry.Album> = emptyList(),
    val isLoading: Boolean = true,
    val error: CatalogError? = null,
    val query: String = "",
    val hideExplicit: Boolean = false,
    val hideVideos: Boolean = false,
    val sort: AlbumSort = AlbumSort.ALBUM_ORDER,
    val selecting: Boolean = false,
    val selectedIds: Set<String> = emptySet(),
    val download: AlbumDownloadState = AlbumDownloadState.NONE,
    val confirmRemoveDownload: Boolean = false,
) {
    /** A failed refresh keeps the tracks already shown; the error page is only for a page with nothing to show. */
    val showError: Boolean get() = error != null && tracks.isEmpty()

    val totalDurationMillis: Long get() = tracks.sumOf { it.durationMillis ?: 0L }

    /** Computed once per state: the list reads it several times per frame. */
    val visibleTracks: List<AlbumTrackRow> by lazy(LazyThreadSafetyMode.NONE) {
        val needle = query.trim()
        val rows = tracks.mapIndexed { index, track -> AlbumTrackRow(index + 1, track) }.filter { (_, track) ->
            (!hideExplicit || !track.isExplicit) &&
                (!hideVideos || !track.isVideo) &&
                (needle.isEmpty() || track.title.contains(needle, ignoreCase = true) ||
                    track.artists.any { it.name.contains(needle, ignoreCase = true) })
        }
        when (sort) {
            AlbumSort.ALBUM_ORDER -> rows
            AlbumSort.TITLE -> rows.sortedBy { it.track.title.lowercase() }
            AlbumSort.DURATION -> rows.sortedByDescending { it.track.durationMillis ?: 0L }
        }
    }

    /** Disc headers only show in album order and without a search, where they still mean something. */
    val listItems: List<AlbumListItem> by lazy(LazyThreadSafetyMode.NONE) {
        val rows = visibleTracks
        val discs = rows.map { it.track.discNumber ?: 1 }.toSet()
        if (sort != AlbumSort.ALBUM_ORDER || query.isNotBlank() || discs.size < 2) {
            rows.map { AlbumListItem.Track(it) }
        } else {
            buildList {
                var current: Int? = null
                for (row in rows) {
                    val disc = row.track.discNumber ?: 1
                    if (disc != current) add(AlbumListItem.DiscHeader(disc)).also { current = disc }
                    add(AlbumListItem.Track(row))
                }
            }
        }
    }

    val selectedTracks: List<CatalogEntry.Track>
        get() = visibleTracks.map { it.track }.filter { it.id.value in selectedIds }

    val allVisibleSelected: Boolean
        get() = visibleTracks.isNotEmpty() && selectedTracks.size == visibleTracks.size
}

/** One album page for every client; the screen only draws [state] and forwards taps. */
class AlbumStateHolder(
    private val catalog: MusicCatalogRepository,
    private val id: CatalogId,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val downloads: AlbumDownloads? = null,
) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutableState = MutableStateFlow(AlbumScreenState())
    private var request: Job? = null
    private var downloadWatch: Job? = null

    val state: StateFlow<AlbumScreenState> = mutableState.asStateFlow()

    fun load() {
        request?.cancel()
        mutableState.update { it.copy(isLoading = true, error = null) }
        request = scope.launch {
            val result = try {
                withContext(workDispatcher) { catalog.album(id) }
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                CatalogResult.Failure(CatalogError.Unknown)
            }
            mutableState.update { current ->
                when {
                    result is CatalogResult.Failure -> current.copy(isLoading = false, error = result.error)
                    result is CatalogResult.Success && result.value.tracks.isEmpty() ->
                        current.copy(isLoading = false, error = CatalogError.Unavailable)
                    result is CatalogResult.Success -> current.copy(
                        album = result.value.album,
                        tracks = result.value.tracks,
                        related = result.value.related,
                        isLoading = false,
                        error = null,
                    ).pruned()
                    else -> current
                }
            }
            watchDownloads()
        }
    }

    fun setQuery(query: String) = mutableState.update { it.copy(query = query).pruned() }
    fun setHideExplicit(hide: Boolean) = mutableState.update { it.copy(hideExplicit = hide).pruned() }
    fun setHideVideos(hide: Boolean) = mutableState.update { it.copy(hideVideos = hide).pruned() }
    fun setSort(sort: AlbumSort) = mutableState.update { it.copy(sort = sort) }

    fun startSelection(trackId: String) = mutableState.update { it.copy(selecting = true, selectedIds = setOf(trackId)) }

    fun toggleSelected(trackId: String) = mutableState.update {
        it.copy(selectedIds = if (trackId in it.selectedIds) it.selectedIds - trackId else it.selectedIds + trackId)
    }

    fun toggleSelectAll() = mutableState.update {
        it.copy(selectedIds = if (it.allVisibleSelected) emptySet() else it.visibleTracks.map { row -> row.track.id.value }.toSet())
    }

    fun clearSelection() = mutableState.update { it.copy(selecting = false, selectedIds = emptySet()) }

    /** Download, cancel or (after a confirm) remove, depending on where the album's offline copy stands. */
    fun onDownloadClick() {
        val current = mutableState.value
        val playable = current.tracks.filter { it.isPlayable }
        val host = downloads ?: return
        if (playable.isEmpty()) return
        when (current.download) {
            AlbumDownloadState.DOWNLOADED -> mutableState.update { it.copy(confirmRemoveDownload = true) }
            AlbumDownloadState.DOWNLOADING -> host.cancel(playable)
            AlbumDownloadState.NONE -> host.download(playable)
        }
    }

    fun confirmRemoveDownload() {
        val playable = mutableState.value.tracks.filter { it.isPlayable }
        mutableState.update { it.copy(confirmRemoveDownload = false) }
        downloads?.remove(playable)
    }

    fun dismissRemoveDownload() = mutableState.update { it.copy(confirmRemoveDownload = false) }

    private fun watchDownloads() {
        val host = downloads ?: return
        val playable = mutableState.value.tracks.filter { it.isPlayable }
        downloadWatch?.cancel()
        if (playable.isEmpty()) return
        downloadWatch = scope.launch {
            host.state(playable).collect { download -> mutableState.update { it.copy(download = download) } }
        }
    }

    /** A filter or reload can hide a selected row; it must not stay selected out of sight. */
    private fun AlbumScreenState.pruned(): AlbumScreenState {
        if (selectedIds.isEmpty()) return this
        val visible = visibleTracks.map { it.track.id.value }.toSet()
        return copy(selectedIds = selectedIds.filterTo(HashSet()) { it in visible })
    }

    override fun close() {
        scope.cancel()
    }
}
