package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogError
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogLibraryCollection
import com.convx.music.catalog.CatalogResult
import com.convx.music.catalog.MusicCatalogRepository
import com.convx.music.catalog.YOUTUBE_MUSIC_PROVIDER
import com.convx.music.library.AddSongToMusicPlaylist
import com.convx.music.library.CreateMusicPlaylist
import com.convx.music.library.MusicLibraryMutationError
import com.convx.music.library.MusicLibraryMutationResult
import com.convx.music.library.MusicPlaylistCreationResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CatalogPlaylistActionState(
    val selectedTrack: CatalogEntry.Track? = null,
    val playlists: List<CatalogEntry.Playlist> = emptyList(),
    val isLoadingPlaylists: Boolean = false,
    val isLoadingMorePlaylists: Boolean = false,
    val continuationToken: String? = null,
    val playlistError: String? = null,
    val pendingPlaylistIds: Set<CatalogId> = emptySet(),
    val isCreatingPlaylist: Boolean = false,
    val message: String? = null,
) {
    val isPickerVisible: Boolean get() = selectedTrack != null
}

/** Coordinates the editable remote playlists and add-track mutation for a single picker. */
class CatalogPlaylistActionStateHolder(
    private val catalog: MusicCatalogRepository,
    private val addTrackToPlaylist: AddSongToMusicPlaylist,
    private val createPlaylist: CreateMusicPlaylist,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutableState = MutableStateFlow(CatalogPlaylistActionState())
    private var playlistRequest: Job? = null

    val state: StateFlow<CatalogPlaylistActionState> = mutableState.asStateFlow()

    fun openPicker(track: CatalogEntry.Track) {
        if (track.id.providerId != YOUTUBE_MUSIC_PROVIDER || track.id.value.isBlank()) {
            mutableState.value = mutableState.value.copy(message = "This track can't be added to a playlist.")
            return
        }

        playlistRequest?.cancel()
        mutableState.value = CatalogPlaylistActionState(
            selectedTrack = track,
            isLoadingPlaylists = true,
        )
        playlistRequest = scope.launch { loadPlaylistPage() }
    }

    fun loadMorePlaylists() {
        val current = mutableState.value
        val token = current.continuationToken ?: return
        if (current.selectedTrack == null || current.isLoadingPlaylists || current.isLoadingMorePlaylists) return
        playlistRequest?.cancel()
        playlistRequest = scope.launch { loadPlaylistPage(token, append = true) }
    }

    fun retryPlaylists() {
        val current = mutableState.value
        if (current.selectedTrack == null || current.isLoadingPlaylists || current.isLoadingMorePlaylists) return
        playlistRequest?.cancel()
        playlistRequest = scope.launch {
            val append = current.playlists.isNotEmpty()
            loadPlaylistPage(current.continuationToken.takeIf { append }, append = append)
        }
    }

    fun addToPlaylist(playlist: CatalogEntry.Playlist) {
        val current = mutableState.value
        val track = current.selectedTrack ?: return
        if (current.pendingPlaylistIds.isNotEmpty() || current.isCreatingPlaylist) return
        if (!playlist.isEditable || playlist.id.providerId != YOUTUBE_MUSIC_PROVIDER || playlist.id.value.isBlank()) {
            mutableState.value = current.copy(message = "This playlist can't be changed.")
            return
        }

        mutableState.value = current.copy(
            pendingPlaylistIds = current.pendingPlaylistIds + playlist.id,
            message = null,
        )
        scope.launch {
            val result = try {
                withContext(workDispatcher) {
                    addTrackToPlaylist(playlist.id.value, track.id.value)
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                MusicLibraryMutationResult.Failure(MusicLibraryMutationError.UNAVAILABLE)
            }

            val latest = mutableState.value
            val pending = latest.pendingPlaylistIds - playlist.id
            mutableState.value = when (result) {
                MusicLibraryMutationResult.Success -> latest.copy(
                    selectedTrack = latest.selectedTrack?.takeUnless { it.id == track.id },
                    pendingPlaylistIds = pending,
                    message = "Added to ${playlist.title}.",
                )
                is MusicLibraryMutationResult.Failure -> latest.copy(
                    pendingPlaylistIds = pending,
                    message = result.error.toMessage(),
                )
            }
        }
    }

    fun createPlaylistAndAdd(title: String) {
        val current = mutableState.value
        val track = current.selectedTrack ?: return
        val normalizedTitle = title.trim()
        if (normalizedTitle.isBlank()) {
            mutableState.value = current.copy(message = "Enter a playlist name first.")
            return
        }
        if (current.pendingPlaylistIds.isNotEmpty() || current.isCreatingPlaylist) return

        mutableState.value = current.copy(isCreatingPlaylist = true, message = null)
        scope.launch {
            val created = try {
                withContext(workDispatcher) { createPlaylist(normalizedTitle) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                MusicPlaylistCreationResult.Failure(MusicLibraryMutationError.UNAVAILABLE)
            }

            when (created) {
                is MusicPlaylistCreationResult.Failure -> {
                    mutableState.value = mutableState.value.copy(
                        isCreatingPlaylist = false,
                        message = created.error.toMessage(),
                    )
                }
                is MusicPlaylistCreationResult.Success -> {
                    val playlist = CatalogEntry.Playlist(
                        id = CatalogId(YOUTUBE_MUSIC_PROVIDER, created.playlistId),
                        title = normalizedTitle,
                        artworkUrls = emptyList(),
                        description = null,
                        trackCount = 0,
                        isEditable = true,
                    )
                    val added = try {
                        withContext(workDispatcher) {
                            addTrackToPlaylist(playlist.id.value, track.id.value)
                        }
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (_: Exception) {
                        MusicLibraryMutationResult.Failure(MusicLibraryMutationError.UNAVAILABLE)
                    }

                    val latest = mutableState.value
                    val updatedPlaylists = (latest.playlists + playlist).distinctBy(CatalogEntry.Playlist::id)
                    mutableState.value = when (added) {
                        MusicLibraryMutationResult.Success -> latest.copy(
                            selectedTrack = latest.selectedTrack?.takeUnless { it.id == track.id },
                            playlists = updatedPlaylists,
                            isCreatingPlaylist = false,
                            message = "Created and added to $normalizedTitle.",
                        )
                        is MusicLibraryMutationResult.Failure -> latest.copy(
                            playlists = updatedPlaylists,
                            isCreatingPlaylist = false,
                            message = "Playlist created, but couldn't add the track. ${added.error.toMessage()}",
                        )
                    }
                }
            }
        }
    }

    fun dismissPicker() {
        playlistRequest?.cancel()
        playlistRequest = null
        mutableState.value = mutableState.value.copy(
            selectedTrack = null,
            playlists = emptyList(),
            isLoadingPlaylists = false,
            isLoadingMorePlaylists = false,
            continuationToken = null,
            playlistError = null,
        )
    }

    fun clearMessage() {
        mutableState.value = mutableState.value.copy(message = null)
    }

    fun close() {
        playlistRequest?.cancel()
        scope.cancel()
    }

    private suspend fun loadPlaylistPage(token: String? = null, append: Boolean = false) {
        val selectedTrackId = mutableState.value.selectedTrack?.id ?: return
        mutableState.value = mutableState.value.copy(
            isLoadingPlaylists = !append,
            isLoadingMorePlaylists = append,
            playlistError = null,
        )

        val result = try {
            withContext(workDispatcher) {
                catalog.library(CatalogLibraryCollection.PLAYLISTS, token)
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            CatalogResult.Failure(CatalogError.Unknown)
        }

        val latest = mutableState.value
        if (latest.selectedTrack?.id != selectedTrackId) return
        mutableState.value = when (result) {
            is CatalogResult.Success -> {
                val pagePlaylists = result.value.entries
                    .filterIsInstance<CatalogEntry.Playlist>()
                    .filter { it.isEditable && it.id.providerId == YOUTUBE_MUSIC_PROVIDER }
                val nextToken = result.value.continuationToken
                if (append && nextToken != null && nextToken == token) {
                    latest.copy(
                        isLoadingPlaylists = false,
                        isLoadingMorePlaylists = false,
                        playlistError = "Couldn't load more playlists. Try again.",
                    )
                } else {
                    val combinedPlaylists = if (append) {
                        (latest.playlists + pagePlaylists).distinctBy(CatalogEntry.Playlist::id)
                    } else {
                        pagePlaylists
                    }
                    latest.copy(
                        playlists = combinedPlaylists,
                        isLoadingPlaylists = false,
                        isLoadingMorePlaylists = false,
                        continuationToken = nextToken,
                        playlistError = if (combinedPlaylists.isEmpty() && nextToken == null) {
                            "No editable playlists found."
                        } else {
                            null
                        },
                    )
                }
            }
            is CatalogResult.Failure -> latest.copy(
                isLoadingPlaylists = false,
                isLoadingMorePlaylists = false,
                playlistError = result.error.toMessage(),
            )
        }
    }
}

private fun MusicLibraryMutationError.toMessage(): String = when (this) {
    MusicLibraryMutationError.UNAUTHORIZED -> "Sign in again to update your music library."
    MusicLibraryMutationError.OFFLINE -> "You're offline. Playlist changes need a connection."
    MusicLibraryMutationError.REJECTED -> "The music service couldn't update this playlist."
    MusicLibraryMutationError.UNAVAILABLE -> "Couldn't update your music library. Try again."
}
