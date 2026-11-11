package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogArtist
import com.convx.music.catalog.CatalogEntry
import com.convx.music.catalog.CatalogId
import com.convx.music.catalog.CatalogSection
import kotlin.test.Test
import kotlin.test.assertEquals

class ContinuationMergeTest {
    private fun album(id: String) = CatalogEntry.Album(
        id = CatalogId("youtube-music", id), title = id, artworkUrls = emptyList(),
        artists = listOf(CatalogArtist(null, "A")), year = null, trackCount = null,
    )

    private fun section(id: String, title: String, vararg ids: String) = CatalogSection(id, title, ids.map(::album))

    @Test
    fun repeatedTitleContinuesTheLastSectionInsteadOfAddingAHeading() {
        val merged = listOf(section("a", "Songs", "1", "2")).withContinuation(listOf(section("a-page-1-0", "songs", "3")))
        assertEquals(1, merged.size)
        assertEquals("a", merged.single().id)
        assertEquals(listOf("1", "2", "3"), merged.single().entries.map { it.id.value })
    }

    @Test
    fun differentOrBlankTitlesStaySeparateAndDuplicatesAreDropped() {
        val base = listOf(section("a", "Songs", "1"))
        assertEquals(2, base.withContinuation(listOf(section("b", "Albums", "9"))).size)
        assertEquals(2, base.withContinuation(listOf(section("b", "", "9"))).size)
        assertEquals(listOf("1", "2"), base.withContinuation(listOf(section("b", "Songs", "1", "2"))).single().entries.map { it.id.value })
    }
}
