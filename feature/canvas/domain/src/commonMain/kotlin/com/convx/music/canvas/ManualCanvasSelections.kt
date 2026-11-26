package com.convx.music.canvas

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Shared session ownership; platforms supply persistent storage operations. */
class ManualCanvasSelections {
    private val mutex = Mutex()
    private val session = mutableMapOf<String, StoredCanvasDecision>()
    private val changes = MutableStateFlow(0L)
    val revision: StateFlow<Long> = changes

    suspend fun resolve(songId: String, readStored: suspend () -> StoredCanvasDecision?): StoredCanvasDecision? =
        mutex.withLock { session[songId] ?: readStored() }

    suspend fun select(songId: String, decision: StoredCanvasDecision, persist: suspend () -> Unit) {
        withContext(NonCancellable) {
            mutex.withLock {
                persist()
                session[songId] = decision
                changes.value++
            }
        }
    }

    suspend fun clear(songId: String, removeStored: suspend () -> Unit) {
        withContext(NonCancellable) {
            mutex.withLock {
                removeStored()
                session.remove(songId)
                changes.value++
            }
        }
    }
}
