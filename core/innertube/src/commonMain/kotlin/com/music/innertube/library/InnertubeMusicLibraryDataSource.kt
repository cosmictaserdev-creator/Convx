package com.music.innertube.library

import com.convx.music.library.MusicLibraryMutationError
import com.convx.music.library.MusicLibraryMutationResult
import com.convx.music.library.MusicLibraryRemoteDataSource
import com.music.innertube.InnertubeRepository
import kotlinx.coroutines.CancellationException

class InnertubeMusicLibraryDataSource(
    private val innertube: InnertubeRepository,
) : MusicLibraryRemoteDataSource {
    override suspend fun setSongSaved(songId: String, isSaved: Boolean): MusicLibraryMutationResult {
        if (songId.isBlank()) return MusicLibraryMutationResult.Failure(MusicLibraryMutationError.REJECTED)

        return try {
            val result = innertube.toggleSongLibrary(songId, addToLibrary = isSaved)
            result.fold(
                onSuccess = { processed ->
                    if (processed) MusicLibraryMutationResult.Success
                    else MusicLibraryMutationResult.Failure(MusicLibraryMutationError.REJECTED)
                },
                onFailure = { error -> error.toMusicLibraryMutationFailure() },
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            error.toMusicLibraryMutationFailure()
        }
    }
}
