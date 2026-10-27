package com.convx.music.history

import kotlinx.coroutines.flow.Flow

/** Read-only listen-history count, independent of the database or client platform. */
interface ListenHistoryRepository {
    fun observeEventCount(): Flow<Int>
    suspend fun clear()
}
