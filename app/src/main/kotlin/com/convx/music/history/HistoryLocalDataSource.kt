package com.convx.music.history

import com.convx.music.db.entities.EventWithSong

interface HistoryLocalDataSource {
    suspend fun loadEvents(limit: Int, offset: Int, hideVideoSongs: Boolean, query: String): List<EventWithSong>
}
