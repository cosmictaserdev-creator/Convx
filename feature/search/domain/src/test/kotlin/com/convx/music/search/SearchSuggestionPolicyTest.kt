package com.convx.music.search

import kotlin.test.Test
import kotlin.test.assertEquals

class SearchSuggestionPolicyTest {
    @Test
    fun keepsTheThreeMostRecentHistoryEntriesAheadOfProviderQueries() {
        val history = listOf(
            SearchHistoryEntry(5, "first"),
            SearchHistoryEntry(4, "second"),
            SearchHistoryEntry(3, "third"),
            SearchHistoryEntry(2, "fourth"),
        )

        val result = mergeSearchSuggestions(
            history = history,
            providerQueries = listOf("second", "fourth", "provider result"),
        )

        assertEquals(history.take(3), result.history)
        assertEquals(listOf("fourth", "provider result"), result.providerQueries)
    }

    @Test
    fun preservesProviderOrderAndExactMatching() {
        val result = mergeSearchSuggestions(
            history = listOf(SearchHistoryEntry(1, "Jazz")),
            providerQueries = listOf("jazz", "Soul", "Jazz"),
        )

        assertEquals(listOf("jazz", "Soul"), result.providerQueries)
    }
}
