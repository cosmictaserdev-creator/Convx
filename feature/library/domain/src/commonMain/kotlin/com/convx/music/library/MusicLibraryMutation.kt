package com.convx.music.library

enum class MusicLibraryMutationError {
    UNAUTHORIZED,
    OFFLINE,
    REJECTED,
    UNAVAILABLE,
}

sealed interface MusicLibraryMutationResult {
    data object Success : MusicLibraryMutationResult
    data class Failure(val error: MusicLibraryMutationError) : MusicLibraryMutationResult
}

sealed interface MusicPlaylistCreationResult {
    data class Success(val playlistId: String) : MusicPlaylistCreationResult
    data class Failure(val error: MusicLibraryMutationError) : MusicPlaylistCreationResult
}

/** Writes one track's saved state to the signed-in music provider. */
interface MusicLibraryRemoteDataSource {
    suspend fun setSongSaved(songId: String, isSaved: Boolean): MusicLibraryMutationResult
}

/** Adds a track to one editable playlist on the signed-in music provider. */
interface MusicPlaylistRemoteDataSource {
    suspend fun addSongToPlaylist(playlistId: String, songId: String): MusicLibraryMutationResult

    suspend fun createPlaylist(title: String): MusicPlaylistCreationResult
}

class SetMusicLibraryTrackSaved(
    private val remote: MusicLibraryRemoteDataSource,
) {
    suspend operator fun invoke(songId: String, isSaved: Boolean): MusicLibraryMutationResult =
        remote.setSongSaved(songId, isSaved)
}

class AddSongToMusicPlaylist(
    private val remote: MusicPlaylistRemoteDataSource,
) {
    suspend operator fun invoke(playlistId: String, songId: String): MusicLibraryMutationResult =
        remote.addSongToPlaylist(playlistId, songId)
}

class CreateMusicPlaylist(
    private val remote: MusicPlaylistRemoteDataSource,
) {
    suspend operator fun invoke(title: String): MusicPlaylistCreationResult = remote.createPlaylist(title)
}
