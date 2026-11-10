package com.convx.music.history

import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.EventWithSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomHistoryLocalDataSource(
    private val database: MusicDatabase,
) : HistoryLocalDataSource, ListenHistoryRepository {
    override suspend fun loadEvents(limit: Int, offset: Int, hideVideoSongs: Boolean, query: String): List<EventWithSong> =
        database.historyEventsPage(limit, offset, hideVideoSongs, query)

    override fun observeEventCount(): Flow<Int> = database.eventCount()

    override suspend fun clear() {
        withContext(Dispatchers.IO) { database.clearListenHistory() }
    }
}
