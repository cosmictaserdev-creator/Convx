package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogSection

internal fun List<CatalogSection>.withContinuation(next: List<CatalogSection>): List<CatalogSection> {
    val merged = toMutableList()
    for (section in next) {
        val previous = merged.lastOrNull()
        if (previous != null && section.title.isNotBlank() &&
            previous.title.trim().equals(section.title.trim(), ignoreCase = true)) {
            merged[merged.lastIndex] = previous.copy(
                entries = (previous.entries + section.entries).distinctBy { it.id },
            )
        } else merged += section.copy(entries = section.entries.distinctBy { it.id })
    }
    return merged
}
