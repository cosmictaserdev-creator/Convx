package com.convx.music.catalog.presentation

import com.music.innertube.pages.ChartsPage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun interface ChartsSource {
    suspend fun load(continuation: String?): Result<ChartsPage>
}

data class ChartsState(
    val page: ChartsPage? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class ChartsStateHolder(
    private val source: ChartsSource,
    mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val workDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(SupervisorJob() + mainDispatcher)
    private val mutableState = MutableStateFlow(ChartsState())
    private var request: Job? = null
    val state = mutableState.asStateFlow()

    fun loadCharts() {
        request?.cancel()
        mutableState.value = mutableState.value.copy(isLoading = true, error = null)
        request = scope.launch { load(null) }
    }

    fun loadMore() {
        val current = mutableState.value
        val continuation = current.page?.continuation ?: return
        if (current.isLoading) return
        mutableState.value = current.copy(isLoading = true, error = null)
        request = scope.launch { load(continuation) }
    }

    private suspend fun load(continuation: String?) {
        val result = try {
            withContext(workDispatcher) { source.load(continuation) }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Result.failure(error)
        }
        val failure = result.exceptionOrNull()
        if (failure is CancellationException) throw failure
        mutableState.value = result.fold(
            onSuccess = { page ->
                val previous = mutableState.value.page
                val resultPage = if (continuation != null && previous != null) {
                    previous.copy(sections = previous.sections + page.sections, continuation = page.continuation)
                } else page
                ChartsState(page = resultPage)
            },
            onFailure = { error ->
                val prefix = if (continuation == null) "Failed to load charts" else "Failed to load more"
                mutableState.value.copy(isLoading = false, error = "$prefix: ${error.message}")
            },
        )
    }

    fun close() { scope.cancel() }
}
