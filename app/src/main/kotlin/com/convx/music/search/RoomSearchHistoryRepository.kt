package com.convx.music.search

import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomSearchHistoryRepository(
    private val database: MusicDatabase,
) : SearchHistoryRepository {
    override fun observeHistory(prefix: String): Flow<List<SearchHistoryEntry>> =
        database.searchHistory(prefix).map { rows ->
            rows.map { SearchHistoryEntry(id = it.id, query = it.query) }
        }

    override suspend fun add(query: String) {
        withContext(Dispatchers.IO) {
            database.insert(SearchHistory(query = query))
        }
    }

    override suspend fun delete(entryId: Long) {
        withContext(Dispatchers.IO) {
            database.deleteSearchHistory(entryId)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clearSearchHistory()
        }
    }
}
