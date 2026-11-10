package com.convx.music.client.desktop

import com.convx.music.account.MusicAccount
import com.convx.music.account.MusicAccountState
import com.convx.music.catalog.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DesktopTrackLikeStateHolderTest {
    private val account = MusicAccountState.SignedIn(MusicAccount("channel", "Listener"))
    private fun page(vararg ids: String, continuation: String? = null) = CatalogResult.Success(
        CatalogLibraryPage(CatalogLibraryCollection.LIKED_TRACKS, ids.map {
            CatalogEntry.Track(CatalogId(YOUTUBE_MUSIC_PROVIDER, it), it, emptyList(), emptyList(), null, null, false, false, true)
        }, continuation),
    )

    @Test fun loadsAllLikedPagesWithoutLoopingOnRepeatedContinuation() = runTest {
        val calls = mutableListOf<String?>()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = DesktopTrackLikeStateHolder({ token ->
            calls += token
            if (token == null) page("first", continuation = "more") else page("second", continuation = "more")
        }, { _, _ -> true }, dispatcher, dispatcher)
        holder.setAccount(account)
        advanceUntilIdle()
        assertEquals(listOf(null, "more"), calls)
        assertEquals(setOf("first", "second"), holder.state.value.likedIds)
        assertFalse(holder.state.value.loading)
        holder.close()
    }

    @Test fun repeatedPressesSubmitOnceAndFailedUnlikeRetainsTheStar() = runTest {
        val result = CompletableDeferred<Boolean>()
        var calls = 0
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = DesktopTrackLikeStateHolder({ page("song") }, { _, _ -> calls++; result.await() }, dispatcher, dispatcher)
        holder.setAccount(account)
        advanceUntilIdle()
        repeat(3) { holder.setLiked("song", false) }
        runCurrent()
        assertEquals(1, calls)
        assertEquals(setOf("song"), holder.state.value.pendingIds)
        result.complete(false)
        advanceUntilIdle()
        assertEquals(setOf("song"), holder.state.value.likedIds)
        assertTrue(holder.state.value.pendingIds.isEmpty())
        assertNotNull(holder.state.value.message)
        holder.close()
    }

    @Test fun accountChangeDiscardsLateMembershipAndWriteResults() = runTest {
        val result = CompletableDeferred<Boolean>()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = DesktopTrackLikeStateHolder({ page() }, { _, _ ->
            withContext(NonCancellable) { result.await() }
        }, dispatcher, dispatcher)
        holder.setAccount(account)
        advanceUntilIdle()
        holder.setLiked("song", true)
        runCurrent()
        holder.setAccount(MusicAccountState.SignedOut)
        result.complete(true)
        advanceUntilIdle()
        assertEquals(DesktopTrackLikeState(), holder.state.value)
        holder.close()
    }

    @Test fun successfulLikeAndUnlikeUpdateMembership() = runTest {
        val writes = mutableListOf<Pair<String, Boolean>>()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = DesktopTrackLikeStateHolder({ page() }, { id, liked -> writes += id to liked; true }, dispatcher, dispatcher)
        holder.setAccount(account)
        advanceUntilIdle()
        holder.setLiked("song", true)
        advanceUntilIdle()
        assertEquals(setOf("song"), holder.state.value.likedIds)
        holder.setLiked("song", false)
        advanceUntilIdle()
        assertTrue(holder.state.value.likedIds.isEmpty())
        assertEquals(listOf("song" to true, "song" to false), writes)
        holder.close()
    }
}
