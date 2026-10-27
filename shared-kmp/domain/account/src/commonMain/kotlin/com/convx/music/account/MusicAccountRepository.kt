package com.convx.music.account

import kotlinx.coroutines.flow.StateFlow

data class MusicAccount(
    val dataSyncId: String,
    val name: String,
    val email: String? = null,
    val channelHandle: String? = null,
    val thumbnailUrl: String? = null,
)

sealed interface MusicAccountState {
    data object SignedOut : MusicAccountState
    data class ChoosingChannel(val channels: List<MusicAccount>) : MusicAccountState
    data class SignedIn(val account: MusicAccount) : MusicAccountState
}

sealed interface MusicSignInResult {
    data class ChooseChannel(val channels: List<MusicAccount>) : MusicSignInResult
    data class Authenticated(val account: MusicAccount) : MusicSignInResult
}

sealed interface MusicAccountError {
    data object InvalidCredentials : MusicAccountError
    data object StorageUnavailable : MusicAccountError
    data object Unauthorized : MusicAccountError
    data object NetworkUnavailable : MusicAccountError
    data object InvalidResponse : MusicAccountError
    data object Unknown : MusicAccountError
}

sealed interface MusicAccountResult<out T> {
    data class Success<out T>(val value: T) : MusicAccountResult<T>
    data class Failure(val error: MusicAccountError) : MusicAccountResult<Nothing>
}

/** Stores provider credentials through an OS-backed secure store supplied by each client. */
interface SecureSessionStore {
    suspend fun read(): String?

    suspend fun write(value: String)

    suspend fun clear()
}

/** Account/session contract; raw cookies enter only during sign-in and never appear in public state. */
interface MusicAccountRepository {
    val state: StateFlow<MusicAccountState>

    suspend fun restore(): MusicAccountResult<Unit>

    suspend fun completeWebSignIn(
        cookie: String,
        visitorData: String,
        pageDataSyncId: String?,
    ): MusicAccountResult<MusicSignInResult>

    suspend fun selectChannel(dataSyncId: String): MusicAccountResult<MusicAccount>

    suspend fun cancelSignIn()

    suspend fun signOut(): MusicAccountResult<Unit>
}
