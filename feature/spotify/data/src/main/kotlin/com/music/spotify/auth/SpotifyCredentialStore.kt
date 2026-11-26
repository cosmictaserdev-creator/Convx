package com.music.spotify.auth

import kotlinx.coroutines.flow.StateFlow

enum class SpotifyCredentialPurpose { PLAYBACK, CATALOG }

interface SpotifyCredentialStore {
    suspend fun read(purpose: SpotifyCredentialPurpose): SpotifyTokens?
    suspend fun save(purpose: SpotifyCredentialPurpose, tokens: SpotifyTokens)
    suspend fun clear(purpose: SpotifyCredentialPurpose)
}

interface SpotifyAuthorizationService {
    suspend fun exchange(clientId: String, code: String, verifier: String, redirectUri: String): SpotifyTokens
    suspend fun refresh(tokens: SpotifyTokens): SpotifyTokens
}

sealed interface SpotifySessionState {
    data object Loading : SpotifySessionState
    data object SignedOut : SpotifySessionState
    data class SignedIn(val clientId: String) : SpotifySessionState
    data class Failed(val reason: SpotifyAuthFailure) : SpotifySessionState
}

enum class SpotifyAuthFailure { DENIED, EXPIRED, CONNECTION, INVALID_RESPONSE, STORAGE, BUSY, TIMEOUT }

class SpotifyAuthException(val reason: SpotifyAuthFailure, cause: Throwable? = null) : Exception(reason.name, cause)

interface SpotifySession {
    val state: StateFlow<SpotifySessionState>
    suspend fun restore()
    suspend fun accept(tokens: SpotifyTokens)
    suspend fun accessToken(forceRefresh: Boolean = false): String
    suspend fun signOut()
}
