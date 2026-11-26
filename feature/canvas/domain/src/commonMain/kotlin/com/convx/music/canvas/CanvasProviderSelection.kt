package com.convx.music.canvas

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class CanvasSelectionMode { PRIORITY, FIRST_AVAILABLE }

/** Resolves provider metadata concurrently, and returns one winner without later replacing it. */
suspend fun <T : Any> selectCanvasProvider(
    providers: List<suspend () -> T?>,
    mode: CanvasSelectionMode,
    priorityWaitMillis: Long = 2_000,
): T? = coroutineScope {
    if (providers.isEmpty()) return@coroutineScope null
    data class Result<T>(val index: Int, val value: T?, val error: Exception?)
    val results = Channel<Result<T>>(Channel.UNLIMITED)
    val jobs = providers.mapIndexed { index, provider ->
        launch {
            try {
                results.send(Result(index, provider(), null))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                results.send(Result(index, null, error))
            }
        }
    }
    val completed = BooleanArray(providers.size)
    val values = arrayOfNulls<Any>(providers.size)
    var remaining = providers.size
    var failure: Exception? = null
    fun record(result: Result<T>) {
        completed[result.index] = true
        values[result.index] = result.value
        if (result.error != null) failure = result.error
        remaining--
    }
    @Suppress("UNCHECKED_CAST")
    fun best(): T? = values.firstOrNull { it != null } as T?
    try {
        if (mode == CanvasSelectionMode.PRIORITY && priorityWaitMillis > 0) {
            withTimeoutOrNull(priorityWaitMillis) {
                while (remaining > 0) {
                    record(results.receive())
                    val bestIndex = values.indexOfFirst { it != null }
                    if (bestIndex >= 0 && (0 until bestIndex).all { completed[it] }) break
                }
            }
        }
        var winner = best()
        // If nothing is ready after the priority window, wait for the first success.
        while (winner == null && remaining > 0) {
            record(results.receive())
            winner = best()
        }
        if (winner == null) failure?.let { throw it }
        winner
    } finally {
        jobs.forEach { it.cancel() }
    }
}
