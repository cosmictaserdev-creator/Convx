package com.convx.music.catalog

import kotlin.random.Random

/** One shelf on Home. The [id] is stored in the saved order and hidden lists, so it must not change. */
sealed class HomeSection(val id: String, val baseWeight: Int) {
    data object MadeForYou : HomeSection("made_for_you", 150)
    data object NewFromYourArtists : HomeSection("artist_releases", 135)
    data object TopPicks : HomeSection("top_picks", 140)
    data object NewReleases : HomeSection("new_albums", 130)
    data object Trending : HomeSection("trending", 120)
    /** The "star of the day" card. Always first, never shuffled. */
    data object Hero : HomeSection("hero", 110)
    data object SpeedDial : HomeSection("speed_dial", 100)
    data object QuickPicks : HomeSection("quick_picks", 90)
    data object DailyDiscover : HomeSection("daily_discover", 80)
    data object ListenAgain : HomeSection("keep_listening", 50)
    data object TopSongs : HomeSection("top_songs", 55)
    data object AccountPlaylists : HomeSection("account_playlists", 40)
    data object ForgottenFavorites : HomeSection("forgotten_favorites", 30)
    data object FromTheCommunity : HomeSection("from_the_community", 20)
    data class SimilarRecommendation(val index: Int) : HomeSection("similar_recommendation_$index", 10)
    data class HomePageSection(val index: Int) : HomeSection("home_page_section_$index", 10)
    data object LiveShows : HomeSection("live_shows", 45)
    data object MusicVideos : HomeSection("music_videos", 45)
    data object ArtistList : HomeSection("artist_list", 45)
    data object AllTimeHits : HomeSection("all_time_hits", 45)
    data object MoodAndGenres : HomeSection("mood_and_genres", 5)
}

object HomeSectionOrder {
    const val HERO_ID = "home-hero"
    const val QUICK_PICKS_ID = "home-quick-picks"
    const val TOP_SONGS_ID = "home-top-songs"
    const val FORGOTTEN_ID = "home-forgotten-favorites"
    const val LISTEN_AGAIN_ID = "home-listen-again"
    const val SPEED_DIAL_ID = "home-speed-dial"
    const val MIXES_ID = "discovery-mixes"
    const val RELEASES_ID = "discovery-releases"
    private const val EXTRA_PREFIX = "home-extra-"

    /** The sections a listener can hide or move from settings, in default order. */
    val configurable: List<HomeSection> = listOf(
        HomeSection.MadeForYou, HomeSection.TopPicks, HomeSection.NewFromYourArtists, HomeSection.NewReleases,
        HomeSection.Trending, HomeSection.Hero, HomeSection.ListenAgain, HomeSection.QuickPicks, HomeSection.SpeedDial,
        HomeSection.FromTheCommunity, HomeSection.DailyDiscover, HomeSection.AccountPlaylists,
        HomeSection.ForgottenFavorites, HomeSection.TopSongs, HomeSection.LiveShows, HomeSection.MusicVideos,
        HomeSection.ArtistList, HomeSection.AllTimeHits, HomeSection.MoodAndGenres,
    )

    private val DEFAULT_ORDER = mapOf(
        HomeSection.MadeForYou to 150,
        HomeSection.TopPicks to 140,
        HomeSection.NewFromYourArtists to 135,
        HomeSection.NewReleases to 130,
        HomeSection.Trending to 120,
        HomeSection.Hero to 110,
        HomeSection.ListenAgain to 100,
        HomeSection.QuickPicks to 90,
        HomeSection.SpeedDial to 80,
        HomeSection.LiveShows to 85,
        HomeSection.MusicVideos to 84,
        HomeSection.ArtistList to 75,
        HomeSection.AllTimeHits to 65,
        HomeSection.FromTheCommunity to 70,
        HomeSection.DailyDiscover to 60,
        HomeSection.AccountPlaylists to 50,
        HomeSection.ForgottenFavorites to 40,
        HomeSection.MoodAndGenres to 10,
    )

    /** Orders [available] by default weights, a saved custom order, or a seeded shuffle, after dropping hidden ids. */
    fun arrange(
        available: List<HomeSection>,
        randomize: Boolean,
        savedOrder: String,
        hiddenIds: String,
        seed: Long,
    ): List<HomeSection> {
        val hidden = hiddenIds.lines().map(String::trim).filter(String::isNotEmpty).toSet()
        val list = available.filter { it.id !in hidden }
        val customOrder = savedOrder.lines().map(String::trim).filter(String::isNotEmpty)
        return if (randomize) {
            list.map { it to randomizedWeight(it, seed) }.sortedByDescending { it.second }.map { it.first }
        } else if (customOrder.isNotEmpty()) {
            val rank = customOrder.distinct().withIndex().associate { (index, id) -> id to index }
            // Sections the saved order does not list follow it, by default weight.
            list.sortedWith(compareBy({ rank[it.id] ?: Int.MAX_VALUE }, { -it.baseWeight }))
        } else {
            list.sortedByDescending {
                when (it) {
                    is HomeSection.SimilarRecommendation -> 30 - it.index
                    is HomeSection.HomePageSection -> 20 - it.index
                    else -> DEFAULT_ORDER[it] ?: 0
                }
            }
        }
    }

    /** Maps a catalog shelf to the Home section it stands for. [feedIndex] counts plain feed shelves seen so far. */
    fun classify(section: CatalogSection, feedIndex: Int): HomeSection {
        val rank = section.id.takeIf { it.startsWith(EXTRA_PREFIX) }
            ?.removePrefix(EXTRA_PREFIX)?.substringBefore('-')?.toIntOrNull()
        return when {
            section.id == MIXES_ID -> HomeSection.MadeForYou
            section.id == RELEASES_ID -> HomeSection.NewFromYourArtists
            section.id == HERO_ID -> HomeSection.Hero
            section.id == QUICK_PICKS_ID -> HomeSection.QuickPicks
            section.id == TOP_SONGS_ID -> HomeSection.TopSongs
            section.id == FORGOTTEN_ID -> HomeSection.ForgottenFavorites
            section.id == LISTEN_AGAIN_ID -> HomeSection.ListenAgain
            section.id == SPEED_DIAL_ID -> HomeSection.SpeedDial
            rank == 0 -> HomeSection.TopPicks
            rank == 20 -> HomeSection.NewReleases
            rank == 30 -> HomeSection.Trending
            rank == 40 -> HomeSection.ListenAgain
            rank == 110 -> HomeSection.AccountPlaylists
            rank == 120 -> HomeSection.MoodAndGenres
            rank == 130 -> HomeSection.DailyDiscover
            rank == 140 -> HomeSection.FromTheCommunity
            rank != null && rank in 150..199 -> HomeSection.SimilarRecommendation(feedIndex)
            else -> HomeSection.HomePageSection(feedIndex)
        }
    }

    /**
     * Puts [extras] ahead of [feed] and orders the whole page as Home does on Android.
     * Shelves that cannot be classified keep the feed's own order.
     */
    fun arrangeShelves(
        feed: List<CatalogSection>,
        extras: List<CatalogSection>,
        randomize: Boolean,
        savedOrder: String,
        hiddenIds: String,
        seed: Long,
    ): List<CatalogSection> {
        val groups = LinkedHashMap<String, Pair<HomeSection, MutableList<CatalogSection>>>()
        var feedIndex = 0
        var similarIndex = 0
        for (section in extras + feed) {
            val kind = when (val first = classify(section, similarIndex)) {
                is HomeSection.HomePageSection -> HomeSection.HomePageSection(feedIndex++)
                is HomeSection.SimilarRecommendation -> first.also { similarIndex++ }
                else -> first
            }
            groups.getOrPut(kind.id) { kind to mutableListOf() }.second += section
        }
        return arrange(groups.values.map { it.first }, randomize, savedOrder, hiddenIds, seed)
            .flatMap { groups.getValue(it.id).second }
    }

    private fun randomizedWeight(section: HomeSection, seed: Long): Int {
        // Seeded per section, so others appearing or disappearing do not reshuffle it.
        val random = Random(seed + section.id.hashCode())
        val base = when (section) {
            HomeSection.Hero -> 10_000
            HomeSection.SpeedDial, HomeSection.QuickPicks, HomeSection.DailyDiscover,
            HomeSection.LiveShows, HomeSection.MusicVideos -> 500
            HomeSection.ListenAgain, HomeSection.AccountPlaylists, HomeSection.ForgottenFavorites,
            HomeSection.FromTheCommunity, HomeSection.ArtistList, HomeSection.AllTimeHits -> 300
            else -> 100
        }
        val jitter = when (section) {
            HomeSection.SpeedDial, HomeSection.QuickPicks, HomeSection.DailyDiscover,
            HomeSection.LiveShows, HomeSection.MusicVideos -> random.nextInt(-200, 400)
            HomeSection.ListenAgain, HomeSection.AccountPlaylists, HomeSection.ForgottenFavorites,
            HomeSection.FromTheCommunity, HomeSection.ArtistList, HomeSection.AllTimeHits -> random.nextInt(-100, 400)
            else -> random.nextInt(-50, 50)
        }
        return base + jitter
    }
}
