package com.convx.music.ui.screens

import com.convx.music.catalog.HomeSection

import com.convx.music.viewmodels.HomeUiState
import com.music.innertube.pages.HomePage
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeSectionOrderTest {
    @Test
    fun `default order keeps browse sections in source order`() {
        assertEquals(
            listOf("home_page_section_0", "home_page_section_1"),
            sectionIds(buildHomeSections(homeState(2), false, false, "", "", 7L)),
        )
    }

    @Test
    fun `saved order takes precedence`() {
        val sections = buildHomeSections(
            homeState(2),
            heroCardEnabled = false,
            randomizeHomeOrder = false,
            savedSectionOrder = "home_page_section_1\nhome_page_section_0",
            hiddenSectionIds = "",
            randomSeed = 7L,
        )

        assertEquals(listOf("home_page_section_1", "home_page_section_0"), sectionIds(sections))
    }

    @Test
    fun `hidden sections are removed`() {
        val sections = buildHomeSections(
            homeState(2),
            heroCardEnabled = false,
            randomizeHomeOrder = false,
            savedSectionOrder = "",
            hiddenSectionIds = "home_page_section_0",
            randomSeed = 7L,
        )

        assertEquals(listOf("home_page_section_1"), sectionIds(sections))
    }

    @Test
    fun `adding sections does not reshuffle existing seeded sections`() {
        fun existingSectionOrder(count: Int) = sectionIds(
            buildHomeSections(
                homeState(count),
                heroCardEnabled = false,
                randomizeHomeOrder = true,
                savedSectionOrder = "",
                hiddenSectionIds = "",
                randomSeed = 42L,
            ),
        ).filter { it == "home_page_section_0" || it == "home_page_section_1" }

        assertEquals(existingSectionOrder(2), existingSectionOrder(4))
    }

    private fun homeState(sectionCount: Int) = HomeUiState(
        homePage = HomePage(
            chips = null,
            sections = List(sectionCount) { index ->
                HomePage.Section(
                    title = "Section $index",
                    label = null,
                    thumbnail = null,
                    endpoint = null,
                    items = emptyList(),
                )
            },
        ),
    )

    private fun sectionIds(sections: List<HomeSection>) = sections.map(HomeSection::id)
}
