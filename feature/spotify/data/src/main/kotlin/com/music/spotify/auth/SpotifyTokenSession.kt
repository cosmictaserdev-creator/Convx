package com.music.spotify.auth

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SpotifyTokenSession(
    private val credentials: SpotifyCredentialStore,
    private val authorization: SpotifyAuthorizationService,
    private val purpose: SpotifyCredentialPurpose = SpotifyCredentialPurpose.PLAYBACK,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : SpotifySession {
    private val mutableState = MutableStateFlow<SpotifySessionState>(SpotifySessionState.Loading)
    override val state = mutableState.asStateFlow()
    private val mutex = Mutex()
    private var restored = false
    @Volatile private var tokens: SpotifyTokens? = null

    override suspend fun restore() = mutex.withLock { restoreLocked() }

    override suspend fun accept(tokens: SpotifyTokens) = mutex.withLock {
        if (!tokens.isFresh(nowMillis(), 0)) throw SpotifyAuthException(SpotifyAuthFailure.EXPIRED)
        withContext(NonCancellable) {
            persist(tokens)
            this@SpotifyTokenSession.tokens = tokens
            restored = true
            mutableState.value = SpotifySessionState.SignedIn(tokens.clientId)
        }
    }

    override suspend fun accessToken(forceRefresh: Boolean): String {
        val observedToken = tokens?.accessToken
        return mutex.withLock {
            restoreLocked()
            val current = tokens ?: throw SpotifyAuthException(SpotifyAuthFailure.EXPIRED)
            val alreadyRefreshed = observedToken != null && current.accessToken != observedToken
            if (current.isFresh(nowMillis()) && (!forceRefresh || alreadyRefreshed)) return@withLock current.accessToken
            val updated = try {
                authorization.refresh(current)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: SpotifyAuthException) {
                if (error.reason == SpotifyAuthFailure.EXPIRED) clearLocked()
                throw error
            }
            if (updated.clientId != current.clientId || !updated.isFresh(nowMillis(), 0)) throw SpotifyAuthException(SpotifyAuthFailure.INVALID_RESPONSE)
            val retained = updated.copy(refreshToken = updated.refreshToken ?: current.refreshToken)
            withContext(NonCancellable) {
                persist(retained)
                tokens = retained
                mutableState.value = SpotifySessionState.SignedIn(retained.clientId)
            }
            retained.accessToken
        }
    }

    override suspend fun signOut() = mutex.withLock { clearLocked() }

    private suspend fun restoreLocked() {
        if (restored) return
        try {
            tokens = credentials.read(purpose)
            restored = true
            mutableState.value = tokens?.let { SpotifySessionState.SignedIn(it.clientId) } ?: SpotifySessionState.SignedOut
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            mutableState.value = SpotifySessionState.Failed(SpotifyAuthFailure.STORAGE)
            throw SpotifyAuthException(SpotifyAuthFailure.STORAGE)
        }
    }

    private suspend fun persist(tokens: SpotifyTokens) {
        try {
            credentials.save(purpose, tokens)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            mutableState.value = SpotifySessionState.Failed(SpotifyAuthFailure.STORAGE)
            throw SpotifyAuthException(SpotifyAuthFailure.STORAGE)
        }
    }

    private suspend fun clearLocked() = withContext(NonCancellable) {
        try {
            credentials.clear(purpose)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            mutableState.value = SpotifySessionState.Failed(SpotifyAuthFailure.STORAGE)
            throw SpotifyAuthException(SpotifyAuthFailure.STORAGE)
        }
        tokens = null
        restored = true
        mutableState.value = SpotifySessionState.SignedOut
    }
}
