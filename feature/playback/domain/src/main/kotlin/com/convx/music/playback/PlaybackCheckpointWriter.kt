package com.convx.music.playback

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/** Coalesces frequent player updates into ordered, cancellation-aware checkpoint writes. */
class PlaybackCheckpointWriter(
    scope: CoroutineScope,
    private val repository: PlaybackCheckpointRepository,
    private val positionWriteIntervalMs: Long = POSITION_WRITE_INTERVAL_MS,
) {
    init {
        require(positionWriteIntervalMs > 0L)
    }

    private val pending = Channel<WriteRequest>(Channel.UNLIMITED)
    private val lastRequested = atomic<PlaybackCheckpoint?>(null)
    private val worker = scope.launch {
        for (request in pending) {
            try {
                repository.saveCheckpoint(request.checkpoint)
                request.completion?.complete(Unit)
            } catch (cancellation: CancellationException) {
                request.completion?.cancel(cancellation)
                throw cancellation
            } catch (failure: Exception) {
                lastRequested.compareAndSet(request.checkpoint, null)
                request.completion?.completeExceptionally(failure)
            }
        }
    }

    init {
        worker.invokeOnCompletion { cause ->
            if (cause != null) {
                pending.close(cause)
                while (true) {
                    val request = pending.tryReceive().getOrNull() ?: break
                    request.completion?.completeExceptionally(cause)
                }
            }
        }
    }

    fun save(checkpoint: PlaybackCheckpoint, force: Boolean = false) {
        val next = checkpoint.normalized()
        while (true) {
            val previous = lastRequested.value
            if (!force && previous != null && !needsWrite(previous, next)) return
            if (lastRequested.compareAndSet(previous, next)) {
                if (pending.trySend(WriteRequest(next)).isFailure) {
                    lastRequested.compareAndSet(next, null)
                }
                return
            }
        }
    }

    /** Queues a final checkpoint behind prior writes and returns only after it is saved. */
    suspend fun flush(checkpoint: PlaybackCheckpoint) {
        val next = checkpoint.normalized()
        while (true) {
            val previous = lastRequested.value
            if (lastRequested.compareAndSet(previous, next)) break
        }

        val completion = CompletableDeferred<Unit>()
        val result = pending.trySend(WriteRequest(next, completion))
        if (result.isFailure) {
            lastRequested.compareAndSet(next, null)
            throw result.exceptionOrNull() ?: IllegalStateException("Playback checkpoint writer is closed")
        }
        completion.await()
    }

    private fun needsWrite(previous: PlaybackCheckpoint, next: PlaybackCheckpoint): Boolean {
        if (
            previous.queue != next.queue ||
            previous.currentIndex != next.currentIndex ||
            previous.repeatMode != next.repeatMode ||
            previous.shuffleEnabled != next.shuffleEnabled
        ) {
            return true
        }
        return if (next.positionMs >= previous.positionMs) {
            next.positionMs - previous.positionMs >= positionWriteIntervalMs
        } else {
            previous.positionMs - next.positionMs >= positionWriteIntervalMs
        }
    }

    private companion object {
        const val POSITION_WRITE_INTERVAL_MS = 5_000L
    }

    private data class WriteRequest(
        val checkpoint: PlaybackCheckpoint,
        val completion: CompletableDeferred<Unit>? = null,
    )
}
