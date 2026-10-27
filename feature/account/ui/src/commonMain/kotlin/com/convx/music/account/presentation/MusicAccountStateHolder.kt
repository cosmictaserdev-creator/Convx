package com.convx.music.account.presentation

import com.convx.music.account.MusicAccountError
import com.convx.music.account.MusicAccountRepository
import com.convx.music.account.MusicAccountResult
import com.convx.music.account.MusicAccountState
import com.convx.music.account.MusicSignInResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MusicAccountPresentationState(
    val account: MusicAccountState = MusicAccountState.SignedOut,
    val isRestoring: Boolean = true,
    val isWorking: Boolean = false,
    val message: String? = null,
)

class MusicAccountStateHolder(
    private val repository: MusicAccountRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow(MusicAccountPresentationState())
    private var operation: Job? = null

    val state: StateFlow<MusicAccountPresentationState> = mutableState.asStateFlow()

    init {
        scope.launch {
            repository.state.collect { account ->
                mutableState.update { it.copy(account = account) }
            }
        }
        restore()
    }

    fun completeWebSignIn(cookie: String, visitorData: String, pageDataSyncId: String?) {
        runOperation {
            when (val result = repository.completeWebSignIn(cookie, visitorData, pageDataSyncId)) {
                is MusicAccountResult.Success -> when (val signIn = result.value) {
                    is MusicSignInResult.Authenticated -> clearMessage()
                    is MusicSignInResult.ChooseChannel -> clearMessage()
                }
                is MusicAccountResult.Failure -> showFailure(result.error)
            }
        }
    }

    fun selectChannel(dataSyncId: String) {
        runOperation {
            when (val result = repository.selectChannel(dataSyncId)) {
                is MusicAccountResult.Success -> clearMessage()
                is MusicAccountResult.Failure -> showFailure(result.error)
            }
        }
    }

    fun cancelSignIn() {
        runOperation { repository.cancelSignIn(); clearMessage() }
    }

    fun signOut() {
        runOperation {
            when (val result = repository.signOut()) {
                is MusicAccountResult.Success -> clearMessage()
                is MusicAccountResult.Failure -> showFailure(result.error)
            }
        }
    }

    fun clearMessage() {
        mutableState.update { it.copy(message = null) }
    }

    fun close() {
        operation?.cancel()
        scope.cancel()
    }

    private fun restore() {
        runOperation(isRestoring = true) {
            when (val result = repository.restore()) {
                is MusicAccountResult.Success -> clearMessage()
                is MusicAccountResult.Failure -> showFailure(result.error)
            }
        }
    }

    private fun runOperation(isRestoring: Boolean = false, block: suspend () -> Unit) {
        operation?.cancel()
        operation = scope.launch {
            mutableState.update {
                it.copy(isRestoring = isRestoring, isWorking = !isRestoring, message = null)
            }
            try {
                block()
            } finally {
                mutableState.update { it.copy(isRestoring = false, isWorking = false) }
            }
        }
    }

    private fun showFailure(error: MusicAccountError) {
        mutableState.update { it.copy(message = error.message()) }
    }
}

private fun MusicAccountError.message(): String = when (this) {
    MusicAccountError.InvalidCredentials -> "That YouTube sign-in could not be validated. Try again."
    MusicAccountError.StorageUnavailable -> "Secure account storage is unavailable on this device."
    MusicAccountError.Unauthorized -> "YouTube rejected this sign-in. Sign in again."
    MusicAccountError.NetworkUnavailable -> "YouTube could not be reached. Check your connection."
    MusicAccountError.InvalidResponse -> "YouTube returned account data this client could not read."
    MusicAccountError.Unknown -> "Couldn't update the YouTube account right now."
}
