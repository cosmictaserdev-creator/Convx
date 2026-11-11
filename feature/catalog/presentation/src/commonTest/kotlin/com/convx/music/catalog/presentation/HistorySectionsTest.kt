package com.convx.music.catalog.presentation

import com.convx.music.catalog.CatalogSection
import com.convx.music.catalog.HomeSection
import com.convx.music.catalog.HomeSectionOrder
import com.convx.music.playback.HistoryPlay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistorySectionsTest {
    private val now = 100 * DAY
    private fun play(id: String, daysAgo: Int) = HistoryPlay(id, "yt", id, listOf("Artist"), atEpochMs = now - daysAgo * DAY)

    @Test fun `no plays means no shelves`() {
        assertTrue(HistorySections.sections(emptyList(), now).isEmpty())
    }

    @Test fun `old favourite that stopped playing is forgotten, a current one is not`() {
        val plays = List(10) { play("old", 60) } + List(10) { play("now", 60) } + List(5) { play("now", 1) }
        val forgotten = HistorySections.sections(plays, now).first { it.id == HomeSectionOrder.FORGOTTEN_ID }
        assertEquals(listOf("old"), forgotten.entries.map { it.id.value })
    }

    @Test fun `hero is the first quick pick and can be hidden`() {
        val shelves = HistorySections.sections(listOf(play("a", 1), play("b", 0)), now)
        assertEquals("b", shelves.first { it.id == HomeSectionOrder.HERO_ID }.entries.single().id.value)
        val arranged = HomeSectionOrder.arrangeShelves(emptyList(), shelves, false, "", HomeSection.Hero.id, 0L)
        assertTrue(arranged.none { it.id == HomeSectionOrder.HERO_ID })
    }

    @Test fun `shelves follow android order, saved order and hidden ids`() {
        fun feed(id: String) = CatalogSection(id, id, emptyList())
        val shelves = listOf(feed("home-extra-0-0"), feed("feed-a"), feed("feed-b"), feed(HomeSectionOrder.QUICK_PICKS_ID))
        fun ids(saved: String = "", hidden: String = "") =
            HomeSectionOrder.arrangeShelves(shelves.drop(1).take(2), listOf(shelves[0], shelves[3]), false, saved, hidden, 0L).map { it.id }
        assertEquals(listOf("home-extra-0-0", HomeSectionOrder.QUICK_PICKS_ID, "feed-a", "feed-b"), ids())
        assertEquals(listOf("feed-b", "feed-a", "home-extra-0-0", HomeSectionOrder.QUICK_PICKS_ID), ids(saved = "home_page_section_1\nhome_page_section_0"))
        assertEquals(listOf("home-extra-0-0", "feed-a", "feed-b"), ids(hidden = HomeSection.QuickPicks.id))
    }

    private companion object { const val DAY = 86_400_000L }
}

class SpeedDialSectionsTest {
    @Test fun `pinning twice unpins, and pins come back as the entries they were`() {
        val track = com.convx.music.catalog.CatalogEntry.Track(
            com.convx.music.catalog.CatalogId("yt", "s1"), "Song", listOf("a.jpg"),
            listOf(com.convx.music.catalog.CatalogArtist(null, "Artist")), null, null, false, false, true,
        )
        val pin = SpeedDialSections.pinOf(track)!!
        val pinned = com.convx.music.playback.SpeedDialSnapshot().toggled(pin)
        val shelf = SpeedDialSections.sections(pinned).single()
        assertEquals(HomeSectionOrder.SPEED_DIAL_ID, shelf.id)
        assertEquals(track.id, shelf.entries.single().id)
        assertTrue(SpeedDialSections.sections(pinned.toggled(pin)).isEmpty())
        assertEquals(pinned, com.convx.music.playback.SpeedDialSnapshot.parse(pinned.toJson()))
    }
}
