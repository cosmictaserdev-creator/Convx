package com.convx.music.catalog

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeSectionPlanTest {
    private fun ids(sections: List<HomeSection>) = sections.map(HomeSection::id)

    private fun plan(
        available: HomeAvailability,
        hero: Boolean = false,
        randomize: Boolean = false,
        order: String = "",
        hidden: String = "",
        recommendations: List<HomeSection> = emptyList(),
    ) = planHomeSections(available, hero, randomize, order, hidden, 7L, recommendations)

    @Test
    fun `nothing available plans nothing`() {
        assertEquals(emptyList(), plan(HomeAvailability(), hero = true))
    }

    @Test
    fun `hero needs quick picks and the setting`() {
        val withPicks = HomeAvailability(hasQuickPicks = true)
        assertEquals(listOf("hero", "quick_picks"), ids(plan(withPicks, hero = true)))
        assertEquals(listOf("quick_picks"), ids(plan(withPicks, hero = false)))
    }

    @Test
    fun `browse and similar sections are capped`() {
        val sections = plan(HomeAvailability(similarCount = 99, homePageCount = 99))
        assertEquals(MaxSimilarSections + MaxHomePageSections, sections.size)
    }

    @Test
    fun `recommendation rails are listed once`() {
        val sections = plan(
            HomeAvailability(hasTopSongs = true),
            recommendations = listOf(HomeSection.TopPicks, HomeSection.TopPicks, HomeSection.Trending),
        )
        assertEquals(setOf("top_picks", "trending", "top_songs"), ids(sections).toSet())
        assertEquals(3, sections.size)
    }

    @Test
    fun `hidden sections are dropped and a saved order wins`() {
        val available = HomeAvailability(hasQuickPicks = true, hasTopSongs = true, hasForgottenFavorites = true)
        assertEquals(
            listOf("top_songs", "forgotten_favorites"),
            ids(plan(available, hidden = "quick_picks", order = "top_songs\nforgotten_favorites")),
        )
        assertEquals(
            listOf("forgotten_favorites", "top_songs"),
            ids(plan(available, hidden = "quick_picks", order = "forgotten_favorites\ntop_songs")),
        )
    }

    @Test
    fun `new shelves honor saved ordering and visibility`() {
        val available = HomeAvailability(
            hasLiveShows = true, hasMusicVideos = true, hasArtistList = true, hasAllTimeHits = true,
        )
        assertEquals(
            listOf("artist_list", "music_videos", "all_time_hits"),
            ids(plan(available, order = "artist_list\nmusic_videos\nall_time_hits\nlive_shows", hidden = "live_shows")),
        )
        assertEquals(
            listOf("live_shows", "all_time_hits"),
            ids(plan(available, order = "live_shows\nall_time_hits\nartist_list\nmusic_videos", hidden = "artist_list\nmusic_videos")),
        )
    }

    @Test
    fun `filtered feed preserves original indices and applies the shelf cap afterward`() {
        val available = HomeAvailability(homePageCount = 10, homePageIndices = listOf(1, 3, 4, 6, 7, 8, 9))
        assertEquals(
            listOf("home_page_section_1", "home_page_section_3", "home_page_section_4", "home_page_section_6", "home_page_section_7", "home_page_section_8"),
            ids(plan(available)),
        )
    }

    @Test
    fun `a seeded shuffle keeps existing sections in place when more arrive`() {
        fun order(count: Int) = ids(plan(HomeAvailability(homePageCount = count), randomize = true))
            .filter { it == "home_page_section_0" || it == "home_page_section_1" }
        assertEquals(order(2), order(4))
    }
}
