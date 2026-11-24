package com.convx.music.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class SearchHistoryRepositoryCommonTest {
    @Test
    fun sharedKoinModuleResolvesCommandsAndPreservesRepositoryOperations() = runTest {
        val repository = FakeSearchHistoryRepository()
        val app = koinApplication {
            modules(
                searchHistoryModule,
                module { single<SearchHistoryRepository> { repository } },
            )
        }

        try {
            val commands = app.koin.get<SearchHistoryCommands>()
            val history = repository.observeHistory("co")

            commands.record("convx")
            commands.delete(42)
            commands.clear()

            assertSame(repository.rows, history)
            assertEquals("co", repository.requestedPrefix)
            assertEquals(listOf("convx"), repository.recorded)
            assertEquals(listOf(42L), repository.deleted)
            assertEquals(1, repository.clearCount)
        } finally {
            app.close()
        }
    }

    private class FakeSearchHistoryRepository : SearchHistoryRepository {
        val rows = flowOf(listOf(SearchHistoryEntry(42, "convx")))
        val recorded = mutableListOf<String>()
        val deleted = mutableListOf<Long>()
        var clearCount = 0

        override fun observeHistory(prefix: String): Flow<List<SearchHistoryEntry>> {
            requestedPrefix = prefix
            return rows
        }

        var requestedPrefix: String? = null
            private set

        override suspend fun add(query: String) {
            recorded += query
        }

        override suspend fun delete(entryId: Long) {
            deleted += entryId
        }

        override suspend fun clear() {
            clearCount++
        }
    }
}
