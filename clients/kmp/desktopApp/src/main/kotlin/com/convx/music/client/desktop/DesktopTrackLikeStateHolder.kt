package com.convx.music.client.desktop

import com.convx.music.account.MusicAccountState
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogLibraryPage
import com.convx.music.catalog.CatalogResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal data class DesktopTrackLikeState(
    val likedIds: Set<String> = emptySet(),
    val pendingIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val message: String? = null,
)

internal class DesktopTrackLikeStateHolder(
    private val loadLiked: suspend (String?) -> CatalogResult<CatalogLibraryPage>,
    private val setRemoteLiked: suspend (String, Boolean) -> Boolean,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow(DesktopTrackLikeState())
    val state = mutableState.asStateFlow()
    private var account: MusicAccountState.SignedIn? = null
    private var generation = 0

    fun setAccount(value: MusicAccountState?) {
        val next = value as? MusicAccountState.SignedIn
        if (next == account) return
        account = next
        generation++
        scope.coroutineContext.cancelChildren()
        mutableState.value = DesktopTrackLikeState(loading = next != null)
        if (next == null) return
        val request = generation
        scope.launch {
            try {
                val ids = mutableSetOf<String>()
                val tokens = mutableSetOf<String>()
                var continuation: String? = null
                do {
                    val page = withContext(workDispatcher) { loadLiked(continuation) }
                    if (page !is CatalogResult.Success) error("Liked Music unavailable")
                    ids += page.value.entries.filterIsInstance<CatalogEntry.Track>().map { it.id.value }
                    continuation = page.value.continuationToken
                } while (continuation != null && tokens.add(continuation))
                if (request == generation) mutableState.value = DesktopTrackLikeState(likedIds = ids)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (request == generation) mutableState.value = DesktopTrackLikeState(message = "Couldn't load Liked Music.")
            }
        }
    }

    fun setLiked(id: String, liked: Boolean) {
        val current = state.value
        if (account == null || id.isBlank() || current.loading || id in current.pendingIds) return
        if ((id in current.likedIds) == liked && current.message == null) return
        val request = generation
        mutableState.update { it.copy(pendingIds = it.pendingIds + id, message = null) }
        scope.launch {
            val success = try {
                withContext(workDispatcher) { setRemoteLiked(id, liked) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) { false }
            if (request != generation) return@launch
            mutableState.update {
                it.copy(
                    likedIds = if (!success) it.likedIds else if (liked) it.likedIds + id else it.likedIds - id,
                    pendingIds = it.pendingIds - id,
                    message = if (success) null else "Couldn't update Liked Music. Try again.",
                )
            }
        }
    }

    fun close() = scope.cancel()
}
