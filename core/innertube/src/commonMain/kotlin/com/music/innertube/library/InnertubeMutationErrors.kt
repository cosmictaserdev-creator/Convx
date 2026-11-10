package com.music.innertube.library

import com.convx.music.library.MusicLibraryMutationError
import com.convx.music.library.MusicLibraryMutationResult
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

internal fun Throwable.toMusicLibraryMutationFailure(): MusicLibraryMutationResult.Failure {
    if (this is CancellationException) throw this
    val mutationError = when (this) {
        is ResponseException -> when (response.status.value) {
            401, 403 -> MusicLibraryMutationError.UNAUTHORIZED
            else -> MusicLibraryMutationError.UNAVAILABLE
        }
        is HttpRequestTimeoutException, is IOException -> MusicLibraryMutationError.OFFLINE
        else -> MusicLibraryMutationError.UNAVAILABLE
    }
    return MusicLibraryMutationResult.Failure(mutationError)
}
