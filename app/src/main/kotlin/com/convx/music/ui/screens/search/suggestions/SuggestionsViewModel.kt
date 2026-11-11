/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.search.suggestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.YouTubeQueue
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.AlbumItem
import com.music.innertube.models.ArtistItem
import com.music.innertube.models.SongItem
import com.music.innertube.models.WatchEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

class SuggestionsViewModel : ViewModel() {
    private var currentLoadedRegion: String? = null
    
    private val _suggestionTracks = MutableStateFlow<List<SuggestionTrack>?>(null)
    val suggestionTracks: StateFlow<List<SuggestionTrack>?> = _suggestionTracks

    private val _suggestionArtists = MutableStateFlow<List<SuggestionArtist>?>(null)
    val suggestionArtists: StateFlow<List<SuggestionArtist>?> = _suggestionArtists

    private val _suggestionAlbums = MutableStateFlow<List<SuggestionAlbum>?>(null)
    val suggestionAlbums: StateFlow<List<SuggestionAlbum>?> = _suggestionAlbums

    private val _suggestionVideos = MutableStateFlow<List<SuggestionTrack>?>(null)
    val suggestionVideos: StateFlow<List<SuggestionTrack>?> = _suggestionVideos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isManualLoading = MutableStateFlow(false)
    val isManualLoading: StateFlow<Boolean> = _isManualLoading

    fun refresh(countryCode: String = "system", force: Boolean = false) {
        val resolvedCode = if (countryCode == "system") {
            Locale.getDefault().country.lowercase()
        } else {
            countryCode.lowercase()
        }

        if (_isLoading.value && !force && currentLoadedRegion == resolvedCode) return
        
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            if (force) _isManualLoading.value = true
            
            if (currentLoadedRegion != resolvedCode || force) {
                _suggestionTracks.value = null
                _suggestionArtists.value = null
                _suggestionAlbums.value = null
                _suggestionVideos.value = null
            }

            try {
                coroutineScope {
                    launch {
                        try {
                            val tracks = AppleMusicScraper.fetchTopSongs(resolvedCode)
                            if (tracks.isNotEmpty()) {
                                _suggestionTracks.value = tracks
                                _suggestionArtists.value = AppleMusicScraper.getTrendingArtists(tracks)
                            }
                        } catch (e: Exception) {
                            Timber.tag("SuggestionsViewModel").e(e, "Failed to fetch songs")
                        }
                    }

                    launch {
                        try {
                            val albums = AppleMusicScraper.fetchTopAlbums(resolvedCode)
                            if (albums.isNotEmpty()) {
                                _suggestionAlbums.value = albums
                            }
                        } catch (e: Exception) {
                            Timber.tag("SuggestionsViewModel").e(e, "Failed to fetch albums")
                        }
                    }

                    launch {
                        try {
                            val videos = AppleMusicScraper.fetchTopVideos(resolvedCode)
                            if (videos.isNotEmpty()) {
                                _suggestionVideos.value = videos
                            }
                        } catch (e: Exception) {
                            Timber.tag("SuggestionsViewModel").e(e, "Failed to fetch videos")
                        }
                    }
                }

                currentLoadedRegion = resolvedCode
            } catch (e: Exception) {
                Timber.tag("SuggestionsViewModel").e(e, "Failed to fetch suggestions")
            } finally {
                _isLoading.value = false
                _isManualLoading.value = false
            }
        }
    }

    fun playTrack(track: SuggestionTrack, playerConnection: PlayerConnection?) {
        playSuggestion(track.title, track.artist, playerConnection)
    }

    private fun playSuggestion(title: String, artist: String, playerConnection: PlayerConnection?) {
        viewModelScope.launch(Dispatchers.IO) {
            val query = "$title $artist"
            YouTube.search(query, SearchFilter.FILTER_SONG).onSuccess { result ->
                val songs = result.items.filterIsInstance<SongItem>()
                val bestMatch = findBestMatch(songs, title, artist) ?: return@onSuccess

                withContext(Dispatchers.Main) {
                    playerConnection?.playQueue(YouTubeQueue(WatchEndpoint(videoId = bestMatch.id)))
                }
            }
        }
    }

    private fun findBestMatch(songs: List<SongItem>, title: String, artist: String): SongItem? {
        fun SongItem.matchesArtist() = artists.any { artist.contains(it.name, ignoreCase = true) }

        return songs.firstOrNull { it.title.equals(title, ignoreCase = true) && it.matchesArtist() }
            ?: songs.firstOrNull { it.title.contains(title, ignoreCase = true) && it.matchesArtist() }
            ?: songs.firstOrNull { it.matchesArtist() }
            ?: songs.firstOrNull()
    }

    fun navigateToArtist(artist: SuggestionArtist, navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.search(artist.name, SearchFilter.FILTER_ARTIST)
                .onSuccess { searchResult ->
                    val firstArtist =
                        searchResult.items.filterIsInstance<ArtistItem>().firstOrNull()
                    if (firstArtist != null) {
                        withContext(Dispatchers.Main) {
                            navController.navigate("artist/${firstArtist.id}")
                        }
                    }
                }
        }
    }

    fun navigateToAlbum(album: SuggestionAlbum, navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            val query = "${album.title} ${album.artist}"
            YouTube.search(query, SearchFilter.FILTER_ALBUM)
                .onSuccess { searchResult ->
                    val firstAlbum =
                        searchResult.items.filterIsInstance<AlbumItem>().firstOrNull()
                    if (firstAlbum != null) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(
                                com.convx.music.ui.utils.albumRoute(
                                    firstAlbum.id,
                                    album.thumbnailUrl,
                                    album.sharedArtworkId,
                                ),
                            )
                        }
                    }
                }
        }
    }

    fun playVideo(video: SuggestionTrack, playerConnection: PlayerConnection?) {
        playSuggestion(video.title, video.artist, playerConnection)
    }
}
