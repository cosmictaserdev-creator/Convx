package com.music.innertube.account

import com.convx.music.account.MusicAccount
import com.convx.music.account.MusicAccountError
import com.convx.music.account.MusicAccountResult
import com.convx.music.account.MusicAccountState
import com.convx.music.account.MusicSignInResult
import com.convx.music.account.SecureSessionStore
import com.music.innertube.InnertubeRepository
import com.music.innertube.YouTubeRepository
import com.music.innertube.models.AccountInfo
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InnertubeMusicAccountRepositoryTest {
    @Test
    fun anUnavailableChannelSwitcherDoesNotRejectAValidatedAccount() = runBlocking {
        val innertube = FakeInnertubeRepository(accountChannelsResult = Result.failure(IllegalStateException("Switcher unavailable")))
        val repository = InnertubeMusicAccountRepository(innertube, FakeSecureSessionStore(null))
        assertEquals(MusicAccountResult.Success(MusicSignInResult.Authenticated(MusicAccount("", "Convx", "user@example.com"))),
            repository.completeWebSignIn("SID=session-cookie", "visitor-data", null))
    }

    @Test
    fun accountWithoutAChannelOrPageIdentityUsesTheValidatedProfileAndRestores() = runBlocking {
        val innertube = FakeInnertubeRepository()
        val store = FakeSecureSessionStore(null)
        val repository = InnertubeMusicAccountRepository(innertube, store)

        val result = repository.completeWebSignIn("__Secure-3PAPISID=session-cookie", "visitor-data", null)

        val account = MusicAccount("", "Convx", "user@example.com")
        assertEquals(MusicAccountResult.Success(MusicSignInResult.Authenticated(account)), result)
        assertEquals(MusicAccountState.SignedIn(account), repository.state.value)
        val restored = InnertubeMusicAccountRepository(innertube, store)
        assertEquals(MusicAccountResult.Success(Unit), restored.restore())
        assertEquals(MusicAccountState.SignedIn(account), restored.state.value)
        assertNull(innertube.dataSyncId)
    }

    @Test
    fun pageIdentityDoesNotBypassFailedAccountValidation() = runBlocking {
        val innertube = FakeInnertubeRepository(accountInfoResult = Result.failure(IllegalStateException("No profile")))
        val store = FakeSecureSessionStore(null)
        val repository = InnertubeMusicAccountRepository(innertube, store)
        val result = repository.completeWebSignIn("SID=session-cookie", "visitor-data", "page-identity")
        assertEquals(MusicAccountResult.Failure(MusicAccountError.Unknown), result)
        assertEquals(MusicAccountState.SignedOut, repository.state.value)
        assertNull(store.value)
    }

    @Test
    fun restoreValidatesTheStoredCookieBeforePublishingTheAccount() = runBlocking {
        val innertube = FakeInnertubeRepository()
        val store = FakeSecureSessionStore(VALID_STORED_SESSION)
        val repository = InnertubeMusicAccountRepository(innertube, store)

        val result = repository.restore()

        assertEquals(MusicAccountResult.Success(Unit), result)
        assertEquals(1, innertube.accountInfoCalls)
        assertEquals("SID=session-cookie", innertube.cookie)
        assertEquals("visitor-data", innertube.visitorData)
        assertEquals("channel-1", innertube.dataSyncId)
        assertEquals(
            MusicAccountState.SignedIn(MusicAccount("channel-1", "Convx", "user@example.com")),
            repository.state.value,
        )
        assertEquals(VALID_STORED_SESSION, store.value)
    }

    @Test
    fun failedRemoteValidationClearsRuntimeCredentialsButKeepsStoredSessionForRetry() = runBlocking {
        val innertube = FakeInnertubeRepository(
            accountInfoResult = Result.failure(IllegalStateException("Service unavailable")),
        )
        val store = FakeSecureSessionStore(VALID_STORED_SESSION)
        val repository = InnertubeMusicAccountRepository(innertube, store)

        val result = repository.restore()

        assertEquals(MusicAccountResult.Failure(MusicAccountError.Unknown), result)
        assertEquals(MusicAccountState.SignedOut, repository.state.value)
        assertNull(innertube.cookie)
        assertNull(innertube.visitorData)
        assertNull(innertube.dataSyncId)
        assertEquals(VALID_STORED_SESSION, store.value)
    }

    @Test
    fun signInPersistsOnlyAfterTheUserSelectsAnAccountChannel() = runBlocking {
        val channels = listOf(
            AccountInfo("Personal", null, "@personal", null, "channel-1"),
            AccountInfo("Family", null, "@family", null, "channel-2"),
        )
        val innertube = FakeInnertubeRepository(
            accountChannelsResult = Result.success(channels),
        )
        val store = FakeSecureSessionStore(null)
        val repository = InnertubeMusicAccountRepository(innertube, store)

        val signIn = repository.completeWebSignIn(
            cookie = "SID=session-cookie",
            visitorData = "visitor-data",
            pageDataSyncId = null,
        )

        assertEquals(MusicAccountResult.Success(MusicSignInResult.ChooseChannel(channels.map { it.toMusicAccount() })), signIn)
        assertEquals(MusicAccountState.ChoosingChannel(channels.map { it.toMusicAccount() }), repository.state.value)
        assertNull(store.value)

        val selected = repository.selectChannel("channel-2")

        assertEquals(MusicAccountResult.Success(channels[1].toMusicAccount()), selected)
        assertEquals("channel-2", innertube.dataSyncId)
        assertEquals(MusicAccountState.SignedIn(channels[1].toMusicAccount()), repository.state.value)
        assertEquals(true, store.value?.contains("\"dataSyncId\":\"channel-2\""))
    }

    private class FakeSecureSessionStore(
        var value: String?,
    ) : SecureSessionStore {
        override suspend fun read(): String? = value

        override suspend fun write(value: String) {
            this.value = value
        }

        override suspend fun clear() {
            value = null
        }
    }

    private class FakeInnertubeRepository(
        private val delegate: InnertubeRepository = YouTubeRepository(),
        private val accountInfoResult: Result<AccountInfo> = Result.success(
            AccountInfo("Convx", "user@example.com", null, null),
        ),
        private val accountChannelsResult: Result<List<AccountInfo>> = Result.success(emptyList()),
    ) : InnertubeRepository by delegate {
        override var cookie: String? = null
        override var visitorData: String? = null
        override var dataSyncId: String? = null
        var accountInfoCalls = 0
            private set

        override suspend fun accountInfo(): Result<AccountInfo> {
            accountInfoCalls += 1
            return accountInfoResult
        }

        override suspend fun getAccountChannels(): Result<List<AccountInfo>> = accountChannelsResult
    }

    private fun AccountInfo.toMusicAccount() = MusicAccount(
        dataSyncId = requireNotNull(dataSyncId),
        name = name,
        email = email,
        channelHandle = channelHandle,
        thumbnailUrl = thumbnailUrl,
    )

    private companion object {
        const val VALID_STORED_SESSION = """
            {"cookie":"SID=session-cookie","visitorData":"visitor-data","dataSyncId":"channel-1","accountName":"Convx","accountEmail":"user@example.com","channelHandle":null,"thumbnailUrl":null}
        """
    }
}
