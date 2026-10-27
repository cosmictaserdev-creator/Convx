/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.Artist
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import kotlinx.coroutines.flow.combine
import com.music.innertube.models.WatchEndpoint
import com.music.innertube.models.BrowseEndpoint
import com.music.innertube.models.YTItem
import com.music.innertube.models.filterExplicit
import com.music.innertube.models.filterVideoSongs
import com.music.innertube.models.filterYoutubeShorts
import com.music.innertube.pages.ExplorePage
import com.music.innertube.pages.HomePage
import com.music.innertube.utils.completed
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.constants.HideYoutubeShortsKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.QuickPicks
import com.convx.music.constants.QuickPicksKey
import com.convx.music.constants.ShowWrappedCardKey
import com.convx.music.constants.WrappedSeenKey
import com.convx.music.R
import com.convx.music.db.MusicDatabase
import com.convx.music.history.ListenHistoryRepository
import com.convx.music.history.ListenHistorySettingsRepository
import com.convx.music.history.ListenHistorySettingsState
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.LocalItem
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SpeedDialItem
import com.convx.music.extensions.filterVideoSongs
import com.convx.music.extensions.toEnum
import com.convx.music.models.SimilarRecommendation
import com.convx.music.ui.screens.wrapped.WrappedAudioService
import com.convx.music.ui.screens.wrapped.WrappedManager
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import com.convx.music.di.ValueProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.time.LocalDate
import kotlin.random.Random

data class DailyDiscoverItem(
    val seed: Song,
    val recommendation: YTItem,
    val relatedEndpoint: BrowseEndpoint?
)

data class CommunityPlaylistItem(
    val playlist: PlaylistItem,
    val songs: List<SongItem>
)

@Immutable
data class HomeUiState(
    val quickPicks: List<Song>? = null,
    val forgottenFavorites: List<Song>? = null,
    val listenAgain: HomePage.Section? = null,
    val topSongs: List<Song>? = null,
    val similarRecommendations: List<SimilarRecommendation>? = null,
    val accountPlaylists: List<PlaylistItem>? = null,
    val homePage: HomePage? = null,
    val explorePage: ExplorePage? = null,
    val dailyDiscover: List<DailyDiscoverItem>? = null,
    val communityPlaylists: List<CommunityPlaylistItem>? = null,
    val allLocalItems: List<LocalItem> = emptyList(),
    val allYtItems: List<YTItem> = emptyList(),
    val speedDialItems: List<YTItem> = emptyList(),
    val selectedChip: HomePage.Chip? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isRandomizing: Boolean = false,
    val accountName: String = "Guest",
    val accountImageUrl: String? = null,
    val showWrappedCard: Boolean = false,
)

private data class HomeUiFeedA(
    val quickPicks: List<Song>?,
    val forgottenFavorites: List<Song>?,
    val listenAgain: HomePage.Section?,
    val topSongs: List<Song>?,
    val similarRecommendations: List<SimilarRecommendation>?,
)

private data class HomeUiFeedB(
    val accountPlaylists: List<PlaylistItem>?,
    val homePage: HomePage?,
    val explorePage: ExplorePage?,
    val dailyDiscover: List<DailyDiscoverItem>?,
    val communityPlaylists: List<CommunityPlaylistItem>?,
)

private data class HomeUiFeedC(
    val allLocalItems: List<LocalItem>,
    val allYtItems: List<YTItem>,
    val speedDialItems: List<YTItem>,
    val selectedChip: HomePage.Chip?,
)

private data class HomeUiFlags(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val isRandomizing: Boolean,
    val accountName: String,
    val accountImageUrl: String?,
)

internal object AccountImageState {
    val url = MutableStateFlow<String?>(null)
}

private const val NEW_RELEASES_BROWSE_ID = "FEmusic_new_releases"
private val EXCLUDED_COMMUNITY_PLAYLIST_AUTHORS = setOf("YouTube Music", "YouTube", "Playlist")

private fun isCommunityPlaylist(playlist: PlaylistItem): Boolean =
    playlist.author?.name !in EXCLUDED_COMMUNITY_PLAYLIST_AUTHORS &&
        !playlist.id.startsWith("RD") &&
        !playlist.id.startsWith("OLAK")

private fun List<YTItem>.filterRecommendationItems(
    hideExplicit: Boolean,
    hideVideoSongs: Boolean,
): List<YTItem> = distinctBy { it.id }
    .filterExplicit(hideExplicit)
    .filterVideoSongs(hideVideoSongs)
    .shuffled()

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    val context: Context,
    val database: MusicDatabase,
    private val listenHistoryRepository: ValueProvider<ListenHistoryRepository>,
    private val listenHistorySettingsRepository: ValueProvider<ListenHistorySettingsRepository>,
    private val syncUtils: ValueProvider<SyncUtils>,
    val wrappedManager: WrappedManager,
    private val wrappedAudioService: WrappedAudioService,
) : ViewModel() {
    // Share the ViewModel job so background aggregation stops when it clears.
    private val feedStateScope = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.Default)

    // Interactive requests bypass this background recommendation limit.
    private val recommendationRequests = Semaphore(3)

    private suspend fun <T> recommendationRequest(block: suspend () -> T): T =
        recommendationRequests.withPermit {
            block().also { currentCoroutineContext().ensureActive() }
        }

    val isRefreshing = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)
    val isRandomizing = MutableStateFlow(false)

    val eventCount = listenHistorySettingsRepository.get().observe()
        .flatMapLatest { settingsState ->
            val isHistoryPaused =
                (settingsState as? ListenHistorySettingsState.Ready)?.settings?.paused == true
            if (isHistoryPaused) listenHistoryRepository.get().observeEventCount() else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val quickPicksEnum = context.dataStore.data.map {
        it[QuickPicksKey].toEnum(QuickPicks.QUICK_PICKS)
    }.distinctUntilChanged()

    val quickPicks = MutableStateFlow<List<Song>?>(null)
    val dailyDiscover = MutableStateFlow<List<DailyDiscoverItem>?>(null)
    val forgottenFavorites = MutableStateFlow<List<Song>?>(null)
    val keepListening = MutableStateFlow<List<LocalItem>?>(null)
    val listenAgain = MutableStateFlow<HomePage.Section?>(null)
    val topSongs = MutableStateFlow<List<Song>?>(null)
    val similarRecommendations = MutableStateFlow<List<SimilarRecommendation>?>(null)
    val accountPlaylists = MutableStateFlow<List<PlaylistItem>?>(null)
    val homePage = MutableStateFlow<HomePage?>(null)
    val explorePage = MutableStateFlow<ExplorePage?>(null)
    val communityPlaylists = MutableStateFlow<List<CommunityPlaylistItem>?>(null)
    val selectedChip = MutableStateFlow<HomePage.Chip?>(null)
    val pendingChip = MutableStateFlow<HomePage.Chip?>(null)
    val chipLoadFailures = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val previousHomePage = MutableStateFlow<HomePage?>(null)

    val allLocalItems = MutableStateFlow<List<LocalItem>>(emptyList())
    val allYtItems = MutableStateFlow<List<YTItem>>(emptyList())

    val speedDialItems: StateFlow<List<YTItem>> =
        combine(
            database.speedDialDao.getAll(),
            keepListening,
            quickPicks
        ) { pinned, keepListening, quick ->
            val pinnedItems = pinned.map { it.toYTItem() }
            val filled = pinnedItems.toMutableList()
            val targetSize = 27

            if (filled.size < targetSize) {
                keepListening?.let { k ->
                    val needed = targetSize - filled.size
                    val available = k.filter { item ->
                        filled.none { p -> p.id == item.id }
                    }.mapNotNull { item ->
                        when (item) {
                            is Song -> SongItem(
                                id = item.id,
                                title = item.title,
                                artists = item.artists.map { Artist(name = it.name, id = it.id) },
                                thumbnail = item.thumbnailUrl ?: "",
                                explicit = false
                            )
                            is Album -> AlbumItem(
                                browseId = item.id,
                                playlistId = item.album.playlistId ?: "",
                                title = item.title,
                                artists = item.artists.map { Artist(name = it.name, id = it.id) },
                                year = item.album.year,
                                thumbnail = item.thumbnailUrl ?: ""
                            )
                            else -> null
                        }
                    }
                    filled.addAll(available.take(needed))
                }
            }

            if (filled.size < targetSize) {
                quick?.let { q ->
                    val needed = targetSize - filled.size
                    val available = q.filter { song ->
                        filled.none { p -> p.id == song.id }
                    }.map { song ->
                        SongItem(
                            id = song.id,
                            title = song.title,
                            artists = song.artists.map { Artist(name = it.name, id = it.id) },
                            thumbnail = song.thumbnailUrl ?: "",
                            explicit = false
                        )
                    }
                    filled.addAll(available.take(needed))
                }
            }

            filled.take(targetSize)
        }.stateIn(feedStateScope, SharingStarted.Lazily, emptyList())

    suspend fun getRandomItem(): YTItem? {
        try {
            isRandomizing.value = true
            kotlinx.coroutines.delay(1000)

            val userSongs = mutableListOf<YTItem>()
            val otherSources = mutableListOf<YTItem>()

            quickPicks.value?.let { songs ->
                userSongs.addAll(songs.map { song ->
                    SongItem(
                        id = song.id,
                        title = song.title,
                        artists = song.artists.map { Artist(name = it.name, id = it.id) },
                        thumbnail = song.thumbnailUrl ?: "",
                        explicit = false
                    )
                })
            }

            keepListening.value?.let { items ->
                items.forEach { item ->
                    when (item) {
                        is Song -> userSongs.add(SongItem(
                            id = item.id,
                            title = item.title,
                            artists = item.artists.map { Artist(name = it.name, id = it.id) },
                            thumbnail = item.thumbnailUrl ?: "",
                            explicit = false
                        ))
                        is Album -> otherSources.add(AlbumItem(
                            browseId = item.id,
                            playlistId = item.album.playlistId ?: "",
                            title = item.title,
                            artists = item.artists.map { Artist(name = it.name, id = it.id) },
                            year = item.album.year,
                            thumbnail = item.thumbnailUrl ?: ""
                        ))
                        else -> {}
                    }
                }
            }

            otherSources.addAll(allYtItems.value)

            val item = if (userSongs.isNotEmpty() && (otherSources.isEmpty() || Random.nextFloat() < 0.8f)) {
                userSongs.distinctBy { it.id }.shuffled().firstOrNull()
            } else {
                otherSources.distinctBy { it.id }.shuffled().firstOrNull()
            } ?: userSongs.firstOrNull() ?: otherSources.firstOrNull()

            return item
        } finally {
            isRandomizing.value = false
        }
    }

    val accountName = MutableStateFlow("Guest")
    val accountImageUrl = AccountImageState.url

    val showWrappedCard: StateFlow<Boolean> = context.dataStore.data.map { prefs ->
        val showWrappedPref = prefs[ShowWrappedCardKey] ?: false
        val seen = prefs[WrappedSeenKey] ?: false
        val isBeforeDate = LocalDate.now().isBefore(LocalDate.of(2026, 2, 1))

        isBeforeDate && (!seen || showWrappedPref)
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val wrappedSeen: StateFlow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[WrappedSeenKey] ?: false
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val uiState: StateFlow<HomeUiState> = combine(
        combine(quickPicks, forgottenFavorites, listenAgain, topSongs, similarRecommendations) {
                quickPicks, forgottenFavorites, listenAgain, topSongs, similarRecommendations ->
            HomeUiFeedA(quickPicks, forgottenFavorites, listenAgain, topSongs, similarRecommendations)
        },
        combine(accountPlaylists, homePage, explorePage, dailyDiscover, communityPlaylists) {
                accountPlaylists, homePage, explorePage, dailyDiscover, communityPlaylists ->
            HomeUiFeedB(accountPlaylists, homePage, explorePage, dailyDiscover, communityPlaylists)
        },
        combine(allLocalItems, allYtItems, speedDialItems, selectedChip) {
                allLocalItems, allYtItems, speedDialItems, selectedChip ->
            HomeUiFeedC(allLocalItems, allYtItems, speedDialItems, selectedChip)
        },
        combine(
            combine(isLoading, isRefreshing, isRandomizing, accountName, accountImageUrl) {
                    isLoading, isRefreshing, isRandomizing, accountName, accountImageUrl ->
                HomeUiFlags(isLoading, isRefreshing, isRandomizing, accountName, accountImageUrl)
            },
            showWrappedCard,
        ) { flags, showWrappedCard -> flags to showWrappedCard },
    ) { a, b, c, flagsPair ->
        HomeUiState(
            quickPicks = a.quickPicks,
            forgottenFavorites = a.forgottenFavorites,
            listenAgain = a.listenAgain,
            topSongs = a.topSongs,
            similarRecommendations = a.similarRecommendations,
            accountPlaylists = b.accountPlaylists,
            homePage = b.homePage,
            explorePage = b.explorePage,
            dailyDiscover = b.dailyDiscover,
            communityPlaylists = b.communityPlaylists,
            allLocalItems = c.allLocalItems,
            allYtItems = c.allYtItems,
            speedDialItems = c.speedDialItems,
            selectedChip = c.selectedChip,
            isLoading = flagsPair.first.isLoading,
            isRefreshing = flagsPair.first.isRefreshing,
            isRandomizing = flagsPair.first.isRandomizing,
            accountName = flagsPair.first.accountName,
            accountImageUrl = flagsPair.first.accountImageUrl,
            showWrappedCard = flagsPair.second,
        )
    }.stateIn(feedStateScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun togglePin(item: YTItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val speedDialItem = SpeedDialItem.fromYTItem(item)
            val isPinned = database.speedDialDao.isPinned(speedDialItem.id).first()
            if (isPinned) {
                database.speedDialDao.delete(speedDialItem.id)
            } else {
                database.speedDialDao.insert(speedDialItem)
            }
        }
    }

    fun markWrappedAsSeen() {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.edit {
                it[WrappedSeenKey] = true
            }
        }
    }
    private var isProcessingAccountData = false

    private suspend fun updateAccountState(cookie: String?) {
        if (isProcessingAccountData) return

        // Reprocess repeated cookie values because account state may have changed independently.
        isProcessingAccountData = true
        try {
            if (!cookie.isNullOrEmpty()) {
                YouTube.cookie = cookie
                YouTube.accountInfo()
                    .onSuccess { info ->
                        accountName.value = info.name
                        accountImageUrl.value = info.thumbnailUrl
                    }
                    .onFailure { error ->
                        if (error.message != "Active account info not found in header") {
                            reportException(error)
                        }
                    }
            } else {
                accountName.value = "Guest"
                accountImageUrl.value = null
                accountPlaylists.value = null
            }
        } finally {
            isProcessingAccountData = false
        }
    }

    private suspend fun getDailyDiscover() {
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        val likedSongs = database.likedSongsByCreateDateAsc().first()
        if (likedSongs.isEmpty()) return

        val seeds = likedSongs.shuffled().take(5)

        val items = java.util.Collections.synchronizedList(mutableListOf<DailyDiscoverItem>())

        kotlinx.coroutines.coroutineScope {
            seeds.forEach { seed ->
                launch(Dispatchers.IO) {
                    val endpoint = recommendationRequest { YouTube.next(WatchEndpoint(videoId = seed.id)) }
                        .getOrNull()?.relatedEndpoint ?: return@launch
                    recommendationRequest { YouTube.related(endpoint) }.onSuccess { page ->
                        val recommendations = page.songs
                            .filter { item -> !item.explicit && (!hideVideoSongs || !item.isVideoSong) }
                            .shuffled()

                        val recommendation = recommendations.firstOrNull { it.id != seed.id }
                        if (recommendation != null) {
                            items.add(
                                DailyDiscoverItem(
                                    seed = seed,
                                    recommendation = recommendation,
                                    relatedEndpoint = endpoint,
                                )
                            )
                        }
                    }
                }
            }
        }

        dailyDiscover.value = items.toList().distinctBy { it.recommendation.id }.shuffled()
    }

    private suspend fun getQuickPicks() {
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        when (quickPicksEnum.first()) {
            QuickPicks.QUICK_PICKS -> {
                val relatedSongs = database.quickPicks().first()
                    .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }
                val forgotten = database.forgottenFavorites().first()
                    .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }.take(8)

                // Publish cached picks before optional network enrichment.
                quickPicks.value = (relatedSongs + forgotten)
                    .distinctBy { it.id }
                    .shuffled()
                    .take(20)
                publishAllLocalItems()

                val recentSong = database.latestEvent().first()?.song
                val ytSimilarSongs = mutableListOf<Song>()

                if (recentSong != null) {
                    val endpoint = recommendationRequest { YouTube.next(WatchEndpoint(videoId = recentSong.id)) }
                        .getOrNull()?.relatedEndpoint
                    if (endpoint != null) {
                        recommendationRequest { YouTube.related(endpoint) }.onSuccess { page ->
                            page.songs.take(10).forEach { ytSong ->
                                database.song(ytSong.id).first()?.let { localSong ->
                                    if (!localSong.song.isLocal && (!hideVideoSongs || !localSong.song.isVideo)) {
                                        ytSimilarSongs.add(localSong)
                                    }
                                }
                            }
                        }
                    }
                }

                // Keep visible picks stable if enrichment returns nothing.
                if (ytSimilarSongs.isNotEmpty()) {
                    quickPicks.value = (relatedSongs + forgotten + ytSimilarSongs)
                        .distinctBy { it.id }
                        .shuffled()
                        .take(20)
                }
            }
            QuickPicks.LAST_LISTEN -> {
                val song = database.latestEvent().first()?.song
                if (song != null && database.hasRelatedSongs(song.id)) {
                    quickPicks.value = database.getRelatedSongs(song.id).first()
                        .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }.shuffled().take(20)
                }
            }
        }
    }

    private suspend fun getCommunityPlaylists() {
        val fromTimeStamp = System.currentTimeMillis() - 86400000L * 7 * 4
        val artistSeeds = database.mostPlayedArtists(fromTimeStamp, limit = 10).first()
            .filter { it.artist.isYouTubeArtist }
            .shuffled().take(3)
        val songSeeds = database.mostPlayedSongs(fromTimeStamp, limit = 5).first()
            .shuffled().take(2)

        val candidatePlaylists = java.util.Collections.synchronizedList(mutableListOf<PlaylistItem>())

        kotlinx.coroutines.coroutineScope {
            artistSeeds.forEach { seed ->
                launch(Dispatchers.IO) {
                    recommendationRequest { YouTube.artist(seed.id) }.onSuccess { page ->
                        page.sections.forEach { section ->
                            section.items.filterIsInstance<PlaylistItem>().forEach { playlist ->
                                if (isCommunityPlaylist(playlist) && playlist.author?.name != seed.artist.name) {
                                    candidatePlaylists.add(playlist)
                                }
                            }
                        }
                    }
                }
            }

            songSeeds.forEach { seed ->
                launch(Dispatchers.IO) {
                    val endpoint = recommendationRequest { YouTube.next(WatchEndpoint(videoId = seed.id)) }
                        .getOrNull()?.relatedEndpoint
                    if (endpoint != null) {
                        recommendationRequest { YouTube.related(endpoint) }.onSuccess { page ->
                            page.playlists.forEach { playlist ->
                                if (isCommunityPlaylist(playlist)) {
                                    candidatePlaylists.add(playlist)
                                }
                            }
                        }
                    }
                }
            }
        }

        val uniqueCandidates = candidatePlaylists.distinctBy { it.id }.shuffled().take(5)

        val playlists = java.util.Collections.synchronizedList(mutableListOf<CommunityPlaylistItem>())

        kotlinx.coroutines.coroutineScope {
            uniqueCandidates.forEach { playlist ->
                launch(Dispatchers.IO) {
                    recommendationRequest { YouTube.playlist(playlist.id) }.onSuccess { page ->
                        val songs = page.songs.take(10)
                        if (songs.isNotEmpty()) {
                            val songCountText = page.playlist.songCountText ?: playlist.songCountText
                            val updatedPlaylist = playlist.copy(songCountText = songCountText)
                            playlists.add(CommunityPlaylistItem(updatedPlaylist, songs))
                        }
                    }
                }
            }
        }

        communityPlaylists.value = playlists.shuffled()
    }

    private suspend fun loadLocalDataPhase() {
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)

        forgottenFavorites.value = database.forgottenFavorites().first()
            .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }.shuffled().take(20)

        val fromTimeStamp = System.currentTimeMillis() - 86400000L * 7 * 2
        val keepListeningSongs = database.mostPlayedSongs(fromTimeStamp, limit = 15, offset = 5).first()
            .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }.shuffled().take(10)
        val keepListeningAlbums = database.mostPlayedAlbums(fromTimeStamp, limit = 8, offset = 2).first()
            .filter { !it.album.isLocal && it.album.thumbnailUrl != null }.shuffled().take(5)
        val keepListeningArtists = database.mostPlayedArtists(fromTimeStamp).first()
            .filter { it.artist.isYouTubeArtist && it.artist.thumbnailUrl != null }.shuffled().take(5)
        keepListening.value = (keepListeningSongs + keepListeningAlbums + keepListeningArtists).shuffled()

        topSongs.value = database.mostPlayedSongs(fromTimeStamp, limit = 10, offset = 0).first()
            .filterVideoSongs(hideVideoSongs).filterNot { it.song.isLocal }

        publishAllLocalItems()
    }

    private suspend fun loadQuickPicks() {
        getQuickPicks()
        publishAllLocalItems()
    }

    private fun publishAllLocalItems() {
        allLocalItems.value = (quickPicks.value.orEmpty() + forgottenFavorites.value.orEmpty() + keepListening.value.orEmpty())
            .filter { it is Song || it is Album }
    }

    private suspend fun loadSimilarRecommendations() {
        val hideExplicit = context.dataStore.get(HideExplicitKey, false)
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        val fromTimeStamp = System.currentTimeMillis() - 86400000L * 7 * 2

        coroutineScope {
            val artistDeferreds = database.mostPlayedArtists(fromTimeStamp, limit = 15).first()
                .filter { it.artist.isYouTubeArtist }
                .shuffled().take(4)
                .map { artist ->
                    async(Dispatchers.IO) {
                        val items = mutableListOf<YTItem>()
                        recommendationRequest { YouTube.artist(artist.id) }.onSuccess { page ->
                            page.sections.takeLast(3).forEach { section -> items += section.items }
                        }
                        SimilarRecommendation(
                            title = artist,
                            items = items.filterRecommendationItems(hideExplicit, hideVideoSongs)
                                .take(12)
                                .ifEmpty { return@async null }
                        )
                    }
                }

            val songDeferreds = database.mostPlayedSongs(fromTimeStamp, limit = 15).first()
                .filter { it.album != null }
                .shuffled().take(3)
                .map { song ->
                    async(Dispatchers.IO) {
                        val endpoint = recommendationRequest { YouTube.next(WatchEndpoint(videoId = song.id)) }
                            .getOrNull()?.relatedEndpoint
                            ?: return@async null
                        val page = recommendationRequest { YouTube.related(endpoint) }.getOrNull() ?: return@async null
                        SimilarRecommendation(
                            title = song,
                            items = (page.songs.shuffled().take(10) +
                                page.albums.shuffled().take(5) +
                                page.artists.shuffled().take(3) +
                                page.playlists.shuffled().take(3))
                                .filterRecommendationItems(hideExplicit, hideVideoSongs)
                                .ifEmpty { return@async null }
                        )
                    }
                }

            val albumDeferreds = database.mostPlayedAlbums(fromTimeStamp, limit = 10).first()
                .filter { it.album.thumbnailUrl != null }
                .shuffled().take(2)
                .map { album ->
                    async(Dispatchers.IO) {
                        val items = mutableListOf<YTItem>()
                        recommendationRequest { YouTube.album(album.id) }.onSuccess { page ->
                            page.otherVersions.let { items += it }
                        }
                        album.artists.firstOrNull()?.id?.let { artistId ->
                            recommendationRequest { YouTube.artist(artistId) }.onSuccess { page ->
                                page.sections.lastOrNull()?.items?.let { items += it }
                            }
                        }
                        SimilarRecommendation(
                            title = album,
                            items = items.filterRecommendationItems(hideExplicit, hideVideoSongs)
                                .take(10)
                                .ifEmpty { return@async null }
                        )
                    }
                }

            val results = (artistDeferreds + songDeferreds + albumDeferreds).awaitAll()
            similarRecommendations.value = results.filterNotNull().shuffled()
        }
    }

    private suspend fun loadNetworkDataPhase() {
        val hideExplicit = context.dataStore.get(HideExplicitKey, false)
        val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
        val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)

        coroutineScope {
            launch(Dispatchers.IO) { getDailyDiscover() }
            launch(Dispatchers.IO) { getCommunityPlaylists() }
            launch(Dispatchers.IO) { loadSimilarRecommendations() }
            launch(Dispatchers.IO) {
                loadHomeAndNewReleases(hideExplicit, hideVideoSongs, hideYoutubeShorts)
            }
            launch(Dispatchers.IO) {
                YouTube.explore().onSuccess { page ->
                    explorePage.value = page.copy(
                        newReleaseAlbums = page.newReleaseAlbums.filterExplicit(hideExplicit)
                    )
                    explorePage.value?.let(ExploreCache::put)
                }.onFailure { reportException(it) }
            }
            if (YouTube.cookie != null) {
                launch(Dispatchers.IO) { loadAccountPlaylists() }
            }
        }

        val recommendationItems = similarRecommendations.value.orEmpty().flatMap { it.items }
        val homeItems = homePage.value?.sections.orEmpty().flatMap { it.items } + listenAgain.value?.items.orEmpty()
        allYtItems.value = recommendationItems + homeItems
    }

    private suspend fun loadHomeAndNewReleases(
        hideExplicit: Boolean,
        hideVideoSongs: Boolean,
        hideYoutubeShorts: Boolean,
    ) = coroutineScope {
        val newReleases = async(Dispatchers.IO) {
            YouTube.home(browseId = NEW_RELEASES_BROWSE_ID).getOrNull()?.sections.orEmpty()
        }
        YouTube.home().onSuccess { page ->
            val seenTitles = mutableSetOf<String>()
            val sections = (page.sections + newReleases.await()).mapNotNull { section ->
                if (!seenTitles.add(section.title.lowercase())) return@mapNotNull null
                val filteredItems = filterHomeItems(
                    section.items,
                    hideExplicit,
                    hideVideoSongs,
                    hideYoutubeShorts,
                )
                if (filteredItems.isEmpty()) null else section.copy(items = filteredItems)
            }
            val listenAgainTitle = context.getString(R.string.keep_listening)
            val listenAgainSection = sections.firstOrNull {
                it.title.trim().equals(listenAgainTitle.trim(), ignoreCase = true) ||
                    it.title.trim().equals("Listen again", ignoreCase = true)
            }
            listenAgain.value = listenAgainSection
            homePage.value = page.copy(sections = sections.filterNot { it == listenAgainSection })
        }.onFailure {
            newReleases.cancel()
            reportException(it)
        }
    }

    private suspend fun load() {
        isLoading.value = true

        kotlinx.coroutines.coroutineScope {
            launch { loadQuickPicks() }
            loadLocalDataPhase()
            isLoading.value = false

            loadNetworkDataPhase()
        }
    }

    private fun filterHomeItems(
        items: List<YTItem>,
        hideExplicit: Boolean,
        hideVideoSongs: Boolean,
        hideYoutubeShorts: Boolean,
    ): List<YTItem> {
        if (!hideExplicit && !hideVideoSongs && !hideYoutubeShorts) return items

        return items.filter { item ->
            (!hideExplicit || !item.explicit) &&
                (!hideVideoSongs || item !is SongItem || !item.isVideoSong) &&
                (!hideYoutubeShorts || item !is PlaylistItem || !item.id.startsWith("SS"))
        }
    }

    @Volatile private var loadingContinuation: String? = null

    fun loadMoreYouTubeItems(continuation: String?) {
        if (continuation == null || continuation == loadingContinuation) return
        loadingContinuation = continuation

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
                val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
                val nextPage = YouTube.home(continuation).getOrElse {
                    reportException(it)
                    // The list remains near its end after a failed request. Clear the
                    // continuation so the scroll observer cannot hammer the same page.
                    homePage.value?.takeIf { it.continuation == continuation }?.let { current ->
                        homePage.value = current.copy(continuation = null)
                    }
                    return@launch
                }
                val currentPage = homePage.value
                // A chip switch or refresh replaced the page while this was in flight.
                if (currentPage == null || currentPage.continuation != continuation) return@launch
                val newSections = nextPage.sections.mapNotNull { section ->
                    val items = filterHomeItems(section.items, hideExplicit, hideVideoSongs, hideYoutubeShorts)
                    if (items.isEmpty()) null else section.copy(items = items)
                }
                // YouTube can return an empty terminal page or echo the same token.
                // In either case there is no next page to request; leaving the token
                // unchanged makes the near-end scroll observer retry forever.
                val nextContinuation = nextPage.continuation
                    ?.takeIf { it != continuation && newSections.isNotEmpty() }
                val extended = currentPage.copy(
                    sections = currentPage.sections + newSections,
                    continuation = nextContinuation,
                )
                homePage.value = extended
                selectedChip.value?.let { cacheChipPage(it, extended) }
            } finally {
                if (loadingContinuation == continuation) loadingContinuation = null
            }
        }
    }

    private var chipJob: Job? = null

    private val chipPages = object : LinkedHashMap<String, Pair<Long, HomePage>>(ChipCacheSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Pair<Long, HomePage>>) = size > ChipCacheSize
    }

    private fun HomePage.Chip.cacheKey() = "${endpoint?.browseId.orEmpty()}|${endpoint?.params.orEmpty()}|$title"

    private fun cachedChipPage(chip: HomePage.Chip): HomePage? = synchronized(chipPages) {
        chipPages[chip.cacheKey()]?.takeIf { System.currentTimeMillis() - it.first < ChipCacheTtlMs }?.second
    }

    private fun cacheChipPage(chip: HomePage.Chip, page: HomePage) = synchronized(chipPages) {
        chipPages[chip.cacheKey()] = System.currentTimeMillis() to page
    }

    fun toggleChip(chip: HomePage.Chip?) {
        if (chip != null && (chip.endpoint?.browseId.isNullOrBlank())) return
        chipJob?.cancel()
        val wasPending = chip != null && chip == pendingChip.value
        pendingChip.value = null
        if (chip == null || wasPending || chip == selectedChip.value) {
            previousHomePage.value?.let { homePage.value = it }
            previousHomePage.value = null
            selectedChip.value = null
            return
        }

        if (previousHomePage.value == null) {
            previousHomePage.value = homePage.value
        }
        cachedChipPage(chip)?.let { page ->
            homePage.value = page
            selectedChip.value = chip
            return
        }
        pendingChip.value = chip

        chipJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
                val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
                // A flaky request used to leave the chip looking untouched; try again before giving up.
                var page: HomePage? = null
                for (attempt in 0..2) {
                    page = YouTube.home(
                        params = chip.endpoint?.params,
                        browseId = chip.endpoint?.browseId ?: return@launch,
                    ).getOrNull()
                    if (page != null) break
                    kotlinx.coroutines.delay(400L * (attempt + 1))
                }
                if (page == null || page.sections.none { it.items.isNotEmpty() }) {
                    chipLoadFailures.tryEmit(Unit)
                    return@launch
                }
                val filtered = page.copy(
                    chips = previousHomePage.value?.chips,
                    sections = page.sections.mapNotNull { section ->
                        val items = filterHomeItems(section.items, hideExplicit, hideVideoSongs, hideYoutubeShorts)
                        if (items.isEmpty()) null else section.copy(items = items)
                    },
                )
                cacheChipPage(chip, filtered)
                homePage.value = filtered
                selectedChip.value = chip
            } finally {
                if (pendingChip.value == chip) pendingChip.value = null
            }
        }
    }

    private suspend fun loadAccountPlaylists() {
        val hideYoutubeShorts = context.dataStore.get(HideYoutubeShortsKey, false)
        YouTube.library("FEmusic_liked_playlists").completed().onSuccess {
            accountPlaylists.value = it.items.filterIsInstance<PlaylistItem>()
                .filterNot { it.id == "SE" }
                .filterYoutubeShorts(hideYoutubeShorts)
        }.onFailure {
            reportException(it)
        }
    }

    fun refresh() {
        if (!isRefreshing.compareAndSet(expect = false, update = true)) return
        synchronized(chipPages) { chipPages.clear() }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Refresh includes optional recommendations and account shelves. A
                // stalled auxiliary request must not leave the pull indicator spinning
                // forever after the main feed has already been refreshed.
                kotlinx.coroutines.withTimeoutOrNull(30_000L) { load() }
            } finally {
                isLoading.value = false
                isRefreshing.value = false
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            syncUtils.get().tryAutoSync()
        }
    }

    init {

        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.data
                .map { it[InnerTubeCookieKey] }
                .distinctUntilChanged()
                .first()
            load()
        }

        viewModelScope.launch(Dispatchers.IO) {
            showWrappedCard.collect { shouldShow ->
                if (shouldShow && !wrappedManager.state.value.isDataReady) {
                    try {
                        wrappedManager.prepare()
                        val state = wrappedManager.state.first { it.isDataReady }
                        val trackMap = state.trackMap
                        if (trackMap.isNotEmpty()) {
                            val firstTrackId = trackMap.entries.first().value
                            wrappedAudioService.prepareTrack(firstTrackId)
                        }
                    } catch (e: Exception) {
                        reportException(e)
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.data
                .map { it[InnerTubeCookieKey] }
                .collect { cookie -> updateAccountState(cookie) }
        }

        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.data
                .map { it[HideYoutubeShortsKey] ?: false }
                .distinctUntilChanged()
                .collect {
                    if (YouTube.cookie != null && accountPlaylists.value != null) {
                        loadAccountPlaylists()
                    }
                }
        }
    }
}

private const val ChipCacheSize = 8
private const val ChipCacheTtlMs = 10 * 60 * 1000L
