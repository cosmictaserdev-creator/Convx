package com.music.innertube.account

import com.convx.music.account.MusicAccount
import com.convx.music.account.MusicAccountError
import com.convx.music.account.MusicAccountRepository
import com.convx.music.account.MusicAccountResult
import com.convx.music.account.MusicAccountState
import com.convx.music.account.MusicSignInResult
import com.convx.music.account.SecureSessionStore
import com.music.innertube.InnertubeRepository
import com.music.innertube.InnerTubeRateLimitException
import com.music.innertube.models.AccountInfo
import com.music.innertube.utils.parseCookieString
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class InnertubeMusicAccountRepository(
    private val innertube: InnertubeRepository,
    private val secureSessionStore: SecureSessionStore,
) : MusicAccountRepository {
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }
    private val mutableState = MutableStateFlow<MusicAccountState>(MusicAccountState.SignedOut)
    override val state: StateFlow<MusicAccountState> = mutableState.asStateFlow()

    private var activeSession: StoredMusicSession? = null
    private var previousSession: StoredMusicSession? = null
    private var pendingSession: StoredMusicSession? = null
    private var pendingChannels: List<MusicAccount> = emptyList()

    override suspend fun restore(): MusicAccountResult<Unit> = mutex.withLock {
        val encoded = try {
            secureSessionStore.read()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            return@withLock MusicAccountResult.Failure(MusicAccountError.StorageUnavailable)
        }
        if (encoded.isNullOrBlank()) {
            activeSession = null
            clearRuntimeSession()
            mutableState.value = MusicAccountState.SignedOut
            return@withLock MusicAccountResult.Success(Unit)
        }

        val storedSession = try {
            json.decodeFromString<StoredMusicSession>(encoded)
        } catch (_: SerializationException) {
            clearInvalidStoredSession()
            return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidCredentials)
        } catch (_: IllegalArgumentException) {
            clearInvalidStoredSession()
            return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidCredentials)
        }
        if (!storedSession.isValid()) {
            clearInvalidStoredSession()
            return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidCredentials)
        }

        activeSession = null
        apply(storedSession)

        try {
            innertube.accountInfo().getOrThrow()
        } catch (cancellation: CancellationException) {
            clearRuntimeSession()
            throw cancellation
        } catch (error: Exception) {
            clearRuntimeSession()
            val failure = error.toMusicAccountFailure()
            if (failure.error == MusicAccountError.Unauthorized || failure.error == MusicAccountError.InvalidCredentials) {
                clearInvalidStoredSession()
            } else {
                mutableState.value = MusicAccountState.SignedOut
            }
            return@withLock failure
        }

        activeSession = storedSession
        mutableState.value = MusicAccountState.SignedIn(storedSession.account)
        MusicAccountResult.Success(Unit)
    }

    override suspend fun completeWebSignIn(
        cookie: String,
        visitorData: String,
        pageDataSyncId: String?,
    ): MusicAccountResult<MusicSignInResult> = mutex.withLock {
        if (!cookie.hasYouTubeAccountSession() || visitorData.isBlank()) {
            return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidCredentials)
        }

        previousSession = activeSession
        val normalizedPageDataSyncId = pageDataSyncId?.takeIf(String::isNotBlank)
        innertube.cookie = cookie
        innertube.visitorData = visitorData
        innertube.dataSyncId = normalizedPageDataSyncId

        val channelResponse = try {
            innertube.getAccountChannels().getOrThrow()
        } catch (cancellation: CancellationException) {
            restorePreviousSession()
            throw cancellation
        } catch (_: Exception) {
            // The switcher is optional; validate through the account menu if it is unavailable.
            emptyList()
        }

        val channels = channelResponse.mapNotNull(AccountInfo::toMusicAccount)
        if (channels.size > 1) {
            pendingSession = StoredMusicSession(
                cookie = cookie,
                visitorData = visitorData,
                dataSyncId = normalizedPageDataSyncId.orEmpty(),
                account = channels.first(),
            )
            pendingChannels = channels
            mutableState.value = MusicAccountState.ChoosingChannel(channels)
            return@withLock MusicAccountResult.Success(MusicSignInResult.ChooseChannel(channels))
        }

        val account = channels.firstOrNull()?.let { channel ->
            channel.copy(dataSyncId = normalizedPageDataSyncId ?: channel.dataSyncId)
        } ?: run {
            // Accounts without a channel can have a valid profile without a DATASYNC_ID.
            val profile = try {
                innertube.accountInfo().getOrThrow()
            } catch (cancellation: CancellationException) {
                restorePreviousSession()
                throw cancellation
            } catch (error: Exception) {
                restorePreviousSession()
                return@withLock error.toMusicAccountFailure()
            }
            MusicAccount(
                dataSyncId = normalizedPageDataSyncId ?: profile.dataSyncId.orEmpty(),
                name = profile.name,
                email = profile.email,
                channelHandle = profile.channelHandle,
                thumbnailUrl = profile.thumbnailUrl,
            )
        }

        val session = StoredMusicSession(cookie, visitorData, account.dataSyncId, account)
        persistSession(session).fold(
            onSuccess = { MusicAccountResult.Success(MusicSignInResult.Authenticated(account)) },
            onFailure = { error ->
                restorePreviousSession()
                error.toMusicAccountFailure()
            },
        )
    }

    override suspend fun selectChannel(dataSyncId: String): MusicAccountResult<MusicAccount> = mutex.withLock {
        val session = pendingSession
            ?: return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidCredentials)
        val account = pendingChannels.firstOrNull { it.dataSyncId == dataSyncId }
            ?: return@withLock MusicAccountResult.Failure(MusicAccountError.InvalidResponse)
        val selectedSession = session.copy(
            dataSyncId = account.dataSyncId,
            accountName = account.name,
            accountEmail = account.email,
            channelHandle = account.channelHandle,
            thumbnailUrl = account.thumbnailUrl,
        )
        innertube.dataSyncId = selectedSession.dataSyncId
        persistSession(selectedSession).fold(
            onSuccess = { MusicAccountResult.Success(account) },
            onFailure = { error ->
                restorePreviousSession()
                error.toMusicAccountFailure()
            },
        )
    }

    override suspend fun cancelSignIn() = mutex.withLock {
        restorePreviousSession()
    }

    override suspend fun signOut(): MusicAccountResult<Unit> = mutex.withLock {
        try {
            secureSessionStore.clear()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            return@withLock MusicAccountResult.Failure(MusicAccountError.StorageUnavailable)
        }

        activeSession = null
        previousSession = null
        pendingSession = null
        pendingChannels = emptyList()
        clearRuntimeSession()
        mutableState.value = MusicAccountState.SignedOut
        MusicAccountResult.Success(Unit)
    }

    private suspend fun persistSession(session: StoredMusicSession): Result<Unit> = try {
        secureSessionStore.write(json.encodeToString(session))
        activeSession = session
        pendingSession = null
        pendingChannels = emptyList()
        previousSession = null
        apply(session)
        mutableState.value = MusicAccountState.SignedIn(session.account)
        Result.success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        Result.failure(error)
    }

    private suspend fun restorePreviousSession() {
        pendingSession = null
        pendingChannels = emptyList()
        val session = previousSession ?: activeSession
        previousSession = null
        activeSession = session
        if (session == null) {
            clearRuntimeSession()
            mutableState.value = MusicAccountState.SignedOut
        } else {
            apply(session)
            mutableState.value = MusicAccountState.SignedIn(session.account)
        }
    }

    private suspend fun clearInvalidStoredSession() {
        try {
            secureSessionStore.clear()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            // The active client still clears its in-memory session if secure storage is unavailable.
        }
        activeSession = null
        clearRuntimeSession()
        mutableState.value = MusicAccountState.SignedOut
    }

    private fun apply(session: StoredMusicSession) {
        innertube.cookie = session.cookie
        innertube.visitorData = session.visitorData
        innertube.dataSyncId = session.dataSyncId.takeIf(String::isNotBlank)
    }

    private fun clearRuntimeSession() {
        innertube.cookie = null
        innertube.visitorData = null
        innertube.dataSyncId = null
    }

    private fun StoredMusicSession.isValid(): Boolean =
        cookie.hasYouTubeAccountSession() && visitorData.isNotBlank() && accountName.isNotBlank()
}

@Serializable
private data class StoredMusicSession(
    val cookie: String,
    val visitorData: String,
    val dataSyncId: String,
    val accountName: String,
    val accountEmail: String?,
    val channelHandle: String?,
    val thumbnailUrl: String?,
) {
    constructor(cookie: String, visitorData: String, dataSyncId: String, account: MusicAccount) : this(
        cookie = cookie,
        visitorData = visitorData,
        dataSyncId = dataSyncId,
        accountName = account.name,
        accountEmail = account.email,
        channelHandle = account.channelHandle,
        thumbnailUrl = account.thumbnailUrl,
    )

    val account: MusicAccount
        get() = MusicAccount(dataSyncId, accountName, accountEmail, channelHandle, thumbnailUrl)

    override fun toString(): String = "StoredMusicSession(<redacted>)"
}

private fun AccountInfo.toMusicAccount(): MusicAccount? = dataSyncId
    ?.takeIf(String::isNotBlank)
    ?.let { syncId ->
        MusicAccount(
            dataSyncId = syncId,
            name = name,
            email = email,
            channelHandle = channelHandle,
            thumbnailUrl = thumbnailUrl,
        )
    }

private fun String.hasYouTubeAccountSession(): Boolean {
    val cookies = parseCookieString(this)
    return AUTHENTICATED_COOKIE_NAMES.any(cookies::containsKey)
}

private fun Throwable.toMusicAccountFailure(): MusicAccountResult.Failure {
    if (this is CancellationException) throw this
    val exception = this as? Exception ?: throw this
    return MusicAccountResult.Failure(
        when (exception) {
            is HttpRequestTimeoutException -> MusicAccountError.NetworkUnavailable
            is IOException -> MusicAccountError.NetworkUnavailable
            is InnerTubeRateLimitException -> MusicAccountError.NetworkUnavailable
            is ResponseException -> when (exception.response.status.value) {
                401, 403 -> MusicAccountError.Unauthorized
                in 500..599 -> MusicAccountError.NetworkUnavailable
                else -> MusicAccountError.InvalidResponse
            }
            else -> MusicAccountError.Unknown
        },
    )
}

private val AUTHENTICATED_COOKIE_NAMES = setOf(
    "SID",
    "HSID",
    "SSID",
    "APISID",
    "SAPISID",
    "__Secure-1PSID",
    "__Secure-3PSID",
    "__Secure-1PAPISID",
    "__Secure-3PAPISID",
)
