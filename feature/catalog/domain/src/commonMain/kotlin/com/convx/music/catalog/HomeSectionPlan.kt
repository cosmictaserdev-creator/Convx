package com.convx.music.catalog

/** Maximum number of recommendation sections included in the home feed. */
const val MaxSimilarSections = 4

/** Maximum number of browse sections included in the home feed. */
const val MaxHomePageSections = 6

/**
 * Which feeds have something to show, reduced to what deciding the section list needs. Every
 * client fills it from its own models, so the order rules below are written once.
 */
data class HomeAvailability(
    val hasQuickPicks: Boolean = false,
    val hasSpeedDial: Boolean = false,
    val hasArtistReleases: Boolean = false,
    val hasCommunityPlaylists: Boolean = false,
    val hasDailyDiscover: Boolean = false,
    val hasListenAgain: Boolean = false,
    val hasTopSongs: Boolean = false,
    val hasAccountPlaylists: Boolean = false,
    val hasForgottenFavorites: Boolean = false,
    val similarCount: Int = 0,
    val homePageCount: Int = 0,
    /** Original indices remaining after a client merges dedicated shelves into its feed. */
    val homePageIndices: List<Int>? = null,
    val hasLiveShows: Boolean = false,
    val hasMusicVideos: Boolean = false,
    val hasArtistList: Boolean = false,
    val hasAllTimeHits: Boolean = false,
    val hasMoodAndGenres: Boolean = false,
)

/** Builds Home's ordered section list: default weights, a saved custom order, or a seeded shuffle. */
fun planHomeSections(
    available: HomeAvailability,
    heroCardEnabled: Boolean,
    randomizeHomeOrder: Boolean,
    savedSectionOrder: String,
    hiddenSectionIds: String,
    randomSeed: Long,
    recommendationSections: List<HomeSection> = emptyList(),
): List<HomeSection> {
    val list = mutableListOf<HomeSection>()
    list.addAll(recommendationSections.distinct())

    if (heroCardEnabled && available.hasQuickPicks) list.add(HomeSection.Hero)
    // Speed Dial: pinned items (long-press menu > Pin), same compact shelf as Top Songs.
    if (available.hasSpeedDial) list.add(HomeSection.SpeedDial)
    if (available.hasArtistReleases) list.add(HomeSection.NewFromYourArtists)
    if (available.hasQuickPicks) list.add(HomeSection.QuickPicks)
    if (available.hasCommunityPlaylists) list.add(HomeSection.FromTheCommunity)
    if (available.hasDailyDiscover) list.add(HomeSection.DailyDiscover)
    if (available.hasListenAgain) list.add(HomeSection.ListenAgain)
    if (available.hasTopSongs) list.add(HomeSection.TopSongs)
    if (available.hasAccountPlaylists) list.add(HomeSection.AccountPlaylists)
    if (available.hasForgottenFavorites) list.add(HomeSection.ForgottenFavorites)

    // Capped: each extra section is ~12 more simultaneously composed rows, the single largest
    // driver of Home's frame cost.
    repeat(available.similarCount.coerceAtMost(MaxSimilarSections)) { list.add(HomeSection.SimilarRecommendation(it)) }
    val pageIndices = available.homePageIndices ?: (0 until available.homePageCount).toList()
    pageIndices.take(MaxHomePageSections).forEach { list.add(HomeSection.HomePageSection(it)) }

    if (available.hasLiveShows) list.add(HomeSection.LiveShows)
    if (available.hasMusicVideos) list.add(HomeSection.MusicVideos)
    if (available.hasArtistList) list.add(HomeSection.ArtistList)
    if (available.hasAllTimeHits) list.add(HomeSection.AllTimeHits)
    if (available.hasMoodAndGenres) list.add(HomeSection.MoodAndGenres)

    return HomeSectionOrder.arrange(list, randomizeHomeOrder, savedSectionOrder, hiddenSectionIds, randomSeed)
}
