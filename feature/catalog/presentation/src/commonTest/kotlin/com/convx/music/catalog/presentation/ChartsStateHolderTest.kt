package com.convx.music.catalog.presentation

import com.music.innertube.pages.ChartsPage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChartsStateHolderTest {
    private fun page(title: String, continuation: String? = null) = ChartsPage(
        sections = listOf(ChartsPage.ChartSection(title, emptyList(), ChartsPage.ChartType.TOP)),
        continuation = continuation,
    )

    @Test fun paginationAppendsOnceAndStopsAtEnd() = runTest {
        val calls = mutableListOf<String?>()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = ChartsStateHolder(ChartsSource { token ->
            calls += token
            Result.success(if (token == null) page("First", "next") else page("Second"))
        }, dispatcher, dispatcher)
        holder.loadCharts()
        advanceUntilIdle()
        holder.loadMore()
        holder.loadMore()
        advanceUntilIdle()
        holder.loadMore()
        assertEquals(listOf(null, "next"), calls)
        assertEquals(listOf("First", "Second"), holder.state.value.page?.sections?.map { it.title })
        assertNull(holder.state.value.page?.continuation)
        assertFalse(holder.state.value.isLoading)
        holder.close()
    }

    @Test fun failedPaginationKeepsPageAndCanRetry() = runTest {
        var fail = true
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = ChartsStateHolder(ChartsSource { token ->
            if (token == null) Result.success(page("First", "next"))
            else if (fail) Result.failure(IllegalStateException("offline"))
            else Result.success(page("Second"))
        }, dispatcher, dispatcher)
        holder.loadCharts()
        advanceUntilIdle()
        holder.loadMore()
        advanceUntilIdle()
        assertEquals("Failed to load more: offline", holder.state.value.error)
        assertEquals("next", holder.state.value.page?.continuation)
        fail = false
        holder.loadMore()
        assertNull(holder.state.value.error)
        advanceUntilIdle()
        assertEquals(2, holder.state.value.page?.sections?.size)
        holder.close()
    }

    @Test fun refreshCancelsPaginationAndReplacesPage() = runTest {
        val waiting = CompletableDeferred<ChartsPage>()
        var loads = 0
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = ChartsStateHolder(ChartsSource { token ->
            if (token != null) Result.success(waiting.await())
            else Result.success(if (loads++ == 0) page("Old", "next") else page("Fresh"))
        }, dispatcher, dispatcher)
        holder.loadCharts()
        advanceUntilIdle()
        holder.loadMore()
        runCurrent()
        assertTrue(holder.state.value.isLoading)
        holder.loadCharts()
        advanceUntilIdle()
        waiting.complete(page("Stale"))
        advanceUntilIdle()
        assertEquals(listOf("Fresh"), holder.state.value.page?.sections?.map { it.title })
        assertFalse(holder.state.value.isLoading)
        holder.close()
    }
}
