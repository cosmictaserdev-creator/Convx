package com.convx.music.search

data class SearchSuggestionCandidates(
    val history: List<SearchHistoryEntry>,
    val providerQueries: List<String>,
)

/** Keeps recent searches ahead of provider suggestions and removes exact duplicates. */
fun mergeSearchSuggestions(
    history: List<SearchHistoryEntry>,
    providerQueries: List<String>,
): SearchSuggestionCandidates {
    val recentHistory = history.take(MAX_HISTORY_SUGGESTIONS)
    val recentQueries = recentHistory.mapTo(mutableSetOf()) { it.query }
    return SearchSuggestionCandidates(
        history = recentHistory,
        providerQueries = providerQueries.filterNot { it in recentQueries },
    )
}

private const val MAX_HISTORY_SUGGESTIONS = 3
