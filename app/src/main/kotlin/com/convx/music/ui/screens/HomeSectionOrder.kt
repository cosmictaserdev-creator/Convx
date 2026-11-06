/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.random.Random

private val DEFAULT_HOME_SECTION_ORDER = mapOf(
    HomeSection.TopPicks to 140,
    HomeSection.NewReleases to 130,
    HomeSection.Trending to 120,
    HomeSection.Hero to 110,
    HomeSection.ListenAgain to 100,
    HomeSection.QuickPicks to 90,
    HomeSection.SpeedDial to 80,
    HomeSection.FromTheCommunity to 70,
    HomeSection.DailyDiscover to 60,
    HomeSection.AccountPlaylists to 50,
    HomeSection.ForgottenFavorites to 40,
    HomeSection.MoodAndGenres to 10,
)

/** Builds Home's ordered section list: default weights, a saved custom order, or a seeded shuffle. */
internal fun buildHomeSections(
    homeState: com.convx.music.viewmodels.HomeUiState,
    heroCardEnabled: Boolean,
    randomizeHomeOrder: Boolean,
    savedSectionOrder: String,
    hiddenSectionIds: String,
    randomSeed: Long,
): List<HomeSection> {
    val quickPicks = homeState.quickPicks
    val communityPlaylists = homeState.communityPlaylists
    val dailyDiscover = homeState.dailyDiscover
    val listenAgain = homeState.listenAgain
    val topSongs = homeState.topSongs
    val accountPlaylists = homeState.accountPlaylists
    val forgottenFavorites = homeState.forgottenFavorites
    val similarRecommendations = homeState.similarRecommendations
    val homePage = homeState.homePage
    val explorePage = homeState.explorePage
    val hidden = hiddenSectionIds.lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toSet()
    val list = mutableListOf<HomeSection>()

    list.add(HomeSection.TopPicks)
    list.add(HomeSection.NewReleases)
    list.add(HomeSection.Trending)
    if (heroCardEnabled && quickPicks?.isNotEmpty() == true) list.add(HomeSection.Hero)
    // Speed Dial: pinned items (long-press menu > Pin), same compact shelf as Top Songs.
    if (homeState.speedDialItems.isNotEmpty()) list.add(HomeSection.SpeedDial)
    if (quickPicks?.isNotEmpty() == true) list.add(HomeSection.QuickPicks)
    if (communityPlaylists?.isNotEmpty() == true) list.add(HomeSection.FromTheCommunity)
    if (dailyDiscover?.isNotEmpty() == true) list.add(HomeSection.DailyDiscover)
    if (listenAgain?.items?.isNotEmpty() == true) list.add(HomeSection.ListenAgain)
    if (topSongs?.isNotEmpty() == true) list.add(HomeSection.TopSongs)
    if (accountPlaylists?.isNotEmpty() == true) list.add(HomeSection.AccountPlaylists)
    if (forgottenFavorites?.isNotEmpty() == true) list.add(HomeSection.ForgottenFavorites)

    // Capped. The survey's most concrete complaint was "I have to see 4 to 6
    // similar-to sections that I don't want", and each extra section is ~12 more
    // simultaneously composed rows — the single largest driver of Home's frame cost
    // (content alone measured 14ms/frame against an 8.33ms budget).
    similarRecommendations?.indices?.take(MaxSimilarSections)?.forEach { i ->
        list.add(HomeSection.SimilarRecommendation(i))
    }

    homePage?.sections?.indices?.take(MaxHomePageSections)?.forEach { i ->
        list.add(HomeSection.HomePageSection(i))
    }

    if (explorePage?.moodAndGenres != null) list.add(HomeSection.MoodAndGenres)

    // Apply hidden-section filtering once after building the available sections.
    list.retainAll { it.id !in hidden }

    val customOrder = savedSectionOrder.lineSequence()
        .map(String::trim)
        .filter(String::isNotEmpty)
        .toList()

    return if (randomizeHomeOrder) {
        list.map { section -> section to randomizedSectionWeight(section, randomSeed) }
            .sortedByDescending { it.second }
            .map { it.first }
    } else if (customOrder.isNotEmpty()) {
        val customOrderRank = customOrder.distinct()
            .withIndex()
            .associate { (index, id) -> id to index }

        // Keep unlisted sections after the saved ones, ordered by their default weight.
        list.sortedWith(
            compareBy(
                { section -> customOrderRank[section.id] ?: if (section.isChartRail()) -1 else Int.MAX_VALUE },
                { section -> -section.baseWeight },
            ),
        )
    } else {
        list.sortedByDescending { section ->
            when (section) {
                is HomeSection.SimilarRecommendation -> 30 - section.index
                is HomeSection.HomePageSection -> 20 - section.index
                else -> DEFAULT_HOME_SECTION_ORDER[section] ?: 0
            }
        }
    }
}

/** Rails that sat above every other section before they became sections of their own. */
private fun HomeSection.isChartRail() =
    this === HomeSection.TopPicks || this === HomeSection.NewReleases || this === HomeSection.Trending

private fun randomizedSectionWeight(section: HomeSection, seed: Long): Int {
    // Seed each section independently so other sections appearing or disappearing won't reshuffle it.
    val sectionRandom = Random(seed + section.id.hashCode())

    // Keep main sections close enough to overlap after applying the random modifier.
    val base = when (section) {
        // The chart rails keep their place above the hero, even with shuffling on.
        HomeSection.TopPicks -> 20_000
        HomeSection.NewReleases -> 19_900
        HomeSection.Trending -> 19_800

        // Pinned above everything, even with shuffling on.
        HomeSection.Hero -> 10_000

        HomeSection.SpeedDial,
        HomeSection.QuickPicks,
        HomeSection.DailyDiscover -> 500

        HomeSection.ListenAgain,
        HomeSection.AccountPlaylists,
        HomeSection.ForgottenFavorites,
        HomeSection.FromTheCommunity -> 300

        else -> 100
    }

    val modifier = when (section) {
        HomeSection.SpeedDial,
        HomeSection.QuickPicks,
        HomeSection.DailyDiscover -> sectionRandom.nextInt(-200, 400)

        HomeSection.ListenAgain,
        HomeSection.AccountPlaylists,
        HomeSection.ForgottenFavorites,
        HomeSection.FromTheCommunity -> sectionRandom.nextInt(-100, 400)

        else -> sectionRandom.nextInt(-50, 50)
    }

    return base + modifier
}
