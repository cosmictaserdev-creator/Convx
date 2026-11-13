package com.convx.music.search

import kotlinx.coroutines.flow.Flow
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

data class SearchHistoryEntry(
    val id: Long,
    val query: String,
)

/** Search history contract independent of Room and Android UI types. */
interface SearchHistoryRepository {
    fun observeHistory(prefix: String = ""): Flow<List<SearchHistoryEntry>>
    suspend fun add(query: String)
    suspend fun delete(entryId: Long)
    suspend fun clear()
}

class SearchHistoryCommands(
    private val repository: SearchHistoryRepository,
) {
    suspend fun record(query: String) = repository.add(query)
    suspend fun delete(entryId: Long) = repository.delete(entryId)
    suspend fun clear() = repository.clear()
}

val searchHistoryModule = module {
    factoryOf(::SearchHistoryCommands)
}
