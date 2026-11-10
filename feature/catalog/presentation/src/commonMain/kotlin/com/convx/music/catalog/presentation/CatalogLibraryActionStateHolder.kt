package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.YOUTUBE_MUSIC_PROVIDER
import com.convx.music.library.MusicLibraryMutationError
import com.convx.music.library.MusicLibraryMutationResult
import com.convx.music.library.SetMusicLibraryTrackSaved
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CatalogLibraryActionState(
    val savedOverrides: Map<CatalogId, Boolean> = emptyMap(),
    val pendingTrackIds: Set<CatalogId> = emptySet(),
    val message: String? = null,
)

class CatalogLibraryActionStateHolder(
    private val setTrackSaved: SetMusicLibraryTrackSaved,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutableState = MutableStateFlow(CatalogLibraryActionState())
    val state: StateFlow<CatalogLibraryActionState> = mutableState.asStateFlow()

    fun isSaved(track: CatalogEntry.Track): Boolean =
        isSaved(track.id, track.isInLibrary)

    fun isSaved(trackId: CatalogId, fallback: Boolean? = null): Boolean =
        mutableState.value.savedOverrides[trackId] ?: fallback ?: false

    fun isPending(track: CatalogEntry.Track): Boolean = isPending(track.id)

    fun isPending(trackId: CatalogId): Boolean = trackId in mutableState.value.pendingTrackIds

    fun setSaved(track: CatalogEntry.Track, isSaved: Boolean) = setSaved(track.id, track.isInLibrary, isSaved)

    fun setSaved(trackId: CatalogId, fallback: Boolean?, isSaved: Boolean) {
        if (trackId.providerId != YOUTUBE_MUSIC_PROVIDER || trackId.value.isBlank()) {
            mutableState.value = mutableState.value.copy(message = "This track can't be changed in your music library.")
            return
        }

        val current = mutableState.value
        if (trackId in current.pendingTrackIds || isSaved(trackId, fallback) == isSaved) return
        mutableState.value = current.copy(
            pendingTrackIds = current.pendingTrackIds + trackId,
            message = null,
        )

        scope.launch {
            val result = try {
                withContext(workDispatcher) { setTrackSaved(trackId.value, isSaved) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                MusicLibraryMutationResult.Failure(MusicLibraryMutationError.UNAVAILABLE)
            }

            val latest = mutableState.value
            val pending = latest.pendingTrackIds - trackId
            mutableState.value = when (result) {
                MusicLibraryMutationResult.Success -> latest.copy(
                    savedOverrides = latest.savedOverrides + (trackId to isSaved),
                    pendingTrackIds = pending,
                )
                is MusicLibraryMutationResult.Failure -> latest.copy(
                    pendingTrackIds = pending,
                    message = result.error.toMessage(),
                )
            }
        }
    }

    fun close() {
        scope.cancel()
    }
}

private fun MusicLibraryMutationError.toMessage(): String = when (this) {
    MusicLibraryMutationError.UNAUTHORIZED -> "Sign in again to update your music library."
    MusicLibraryMutationError.OFFLINE -> "You're offline. Library changes need a connection."
    MusicLibraryMutationError.REJECTED -> "The music service couldn't update this track."
    MusicLibraryMutationError.UNAVAILABLE -> "Couldn't update your music library. Try again."
}
