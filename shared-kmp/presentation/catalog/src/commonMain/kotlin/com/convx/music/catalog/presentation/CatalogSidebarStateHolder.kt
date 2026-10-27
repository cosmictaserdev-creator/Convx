package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.MusicCatalogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Account navigation stays independent of the currently browsed catalog page. */
class CatalogSidebarStateHolder(private val catalog: MusicCatalogRepository, library: com.convx.music.library.PlaylistRepository? = null) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutablePlaylists = MutableStateFlow<List<CatalogEntry.Playlist>>(emptyList())
    private var request: Job? = null
    val playlists = mutablePlaylists.asStateFlow()
    val localPlaylists = (library?.observePlaylists(com.convx.music.library.PlaylistOrder.NAME, false)
        ?: flowOf(emptyList())).stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun refresh(signedIn: Boolean) {
        request?.cancel()
        if (!signedIn) {
            mutablePlaylists.value = emptyList()
            return
        }
        request = scope.launch {
            val loaded = mutableListOf<CatalogEntry.Playlist>()
            val seen = mutableSetOf<String>()
            var token: String? = null
            do {
                val result = withContext(Dispatchers.Default) { catalog.library(CatalogLibraryCollection.PLAYLISTS, token) }
                if (result !is CatalogResult.Success) break
                loaded += result.value.entries.filterIsInstance<CatalogEntry.Playlist>()
                mutablePlaylists.value = loaded.distinctBy { it.id }
                token = result.value.continuationToken?.takeIf { seen.add(it) }
            } while (token != null)
        }
    }

    fun close() = scope.cancel()
}
