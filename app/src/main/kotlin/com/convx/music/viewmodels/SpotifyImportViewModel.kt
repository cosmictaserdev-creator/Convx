package com.convx.music.viewmodels

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.spotify.Spotify
import com.music.spotify.SpotifyAuth
import com.music.spotify.SpotifyHomeItem
import com.music.spotify.SpotifyHomeItemType
import com.music.spotify.SpotifyHomeShelf
import com.music.spotify.SpotifyMapper
import com.music.spotify.models.SpotifyPlaylist
import com.music.spotify.models.SpotifyImage
import com.music.spotify.models.SpotifyTrack
import com.convx.music.constants.SpotifySessionKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongEntity
import com.convx.music.models.MediaMetadata
import com.convx.music.models.toMediaMetadata
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.convx.music.utils.reportException
import com.convx.music.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@Serializable
data class SpotifySession(
    val spDc: String,
    val spKey: String? = null,
    val accessToken: String? = null,
    val expiresAt: Long = 0,
    val clientId: String? = null,
    val accountName: String? = null,
    val accountAvatarUrl: String? = null,
)

data class SpotifyImportUiState(
    val isAuthenticated: Boolean = false,
    val accountName: String = "",
    val accountAvatarUrl: String? = null,
    val playlists: List<SpotifyPlaylist> = emptyList(),
    val likedSongsCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val homeShelves: List<SpotifyHomeShelf> = emptyList(),
    val isHomeFeedLoading: Boolean = false,
    val homeFeedErrorMessage: String? = null,
)

data class SpotifyImportProgress(
    val playlistName: String,
    val currentSongIndex: Int,
    val totalSongs: Int,
    val percent: Float,
    val isFinished: Boolean = false
)

private data class PlaylistImportData(
    val title: String,
    val songs: List<Song>,
    val localPlaylistId: String,
    val thumbnailUrl: String?
)

@HiltViewModel
class SpotifyImportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MusicDatabase,
    private val syncUtils: SyncUtils,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpotifyImportUiState(isLoading = true))
    val uiState: StateFlow<SpotifyImportUiState> = _uiState.asStateFlow()

    private val _importProgress = MutableStateFlow<SpotifyImportProgress?>(null)
    val importProgress: StateFlow<SpotifyImportProgress?> = _importProgress.asStateFlow()

    private var importJob: Job? = null
    private var homeFeedJob: Job? = null
    private val authMutex = Mutex()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    init {
        restoreSession()
    }

    private suspend fun getSession(): SpotifySession? {
        val prefs = context.dataStore.data.first()
        val sessionJson = prefs[SpotifySessionKey]
        return if (!sessionJson.isNullOrBlank()) {
            json.decodeFromString<SpotifySession>(sessionJson)
        } else {
            null
        }
    }

    private suspend fun saveSession(session: SpotifySession) {
        context.dataStore.edit { prefs ->
            prefs[SpotifySessionKey] = json.encodeToString(session)
        }
    }

    private suspend fun ensureAuthenticated(forceRefresh: Boolean = false): SpotifySession = authMutex.withLock {
        val session = getSession() ?: throw IllegalStateException("Not connected to Spotify")
        if (!forceRefresh && session.accessToken != null && session.expiresAt > System.currentTimeMillis() + 60_000L) {
            Spotify.accessToken = session.accessToken
            Spotify.clientId = session.clientId.orEmpty()
            session
        } else {
            refreshSession(session.spDc, session.spKey.orEmpty())
        }
    }

    private suspend fun refreshWithCookies(spDc: String, spKey: String): SpotifySession =
        authMutex.withLock { refreshSession(spDc, spKey) }

    private suspend fun refreshSession(spDc: String, spKey: String): SpotifySession = withContext(Dispatchers.IO) {
        val token = SpotifyAuth.fetchAccessToken(spDc, spKey).getOrThrow()
        Spotify.accessToken = token.accessToken
        Spotify.clientId = token.clientId
        val profileResult = Spotify.me()
        if ((profileResult.exceptionOrNull() as? Spotify.SpotifyException)?.statusCode == 401) profileResult.getOrThrow()
        val profile = profileResult.getOrNull()

        val newSession = SpotifySession(
            spDc = spDc,
            spKey = spKey,
            accessToken = token.accessToken,
            expiresAt = token.accessTokenExpirationTimestampMs,
            clientId = token.clientId,
            accountName = profile?.displayName,
            accountAvatarUrl = profile?.images?.firstOrNull()?.url
        )
        saveSession(newSession)
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                accountName = newSession.accountName.orEmpty(),
                accountAvatarUrl = newSession.accountAvatarUrl,
                isLoading = false
            )
        }
        newSession
    }

    fun restoreSession() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val session = getSession()
                if (session == null) {
                    _uiState.update { it.copy(isAuthenticated = false, isLoading = false) }
                    return@launch
                }
                if (session.accessToken != null && session.expiresAt > System.currentTimeMillis() + 60_000L) {
                    Spotify.accessToken = session.accessToken
                    Spotify.clientId = session.clientId.orEmpty()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            accountName = session.accountName.orEmpty(),
                            accountAvatarUrl = session.accountAvatarUrl,
                            isLoading = false
                        )
                    }
                    loadSources()
                    loadHomeFeed()
                } else {
                    runCatching { refreshWithCookies(session.spDc, session.spKey.orEmpty()) }
                        .onSuccess {
                            loadSources()
                            loadHomeFeed()
                        }
                        .onFailure { error ->
                            if (error is kotlinx.coroutines.CancellationException) throw error
                            val expired = (error as? Spotify.SpotifyException)?.statusCode == 401
                            if (expired) {
                                context.dataStore.edit { it.remove(SpotifySessionKey) }
                                Spotify.accessToken = null
                                Spotify.clientId = ""
                            }
                            _uiState.update {
                                it.copy(
                                    isAuthenticated = false,
                                    isLoading = false,
                                    errorMessage = if (expired) "Session expired. Please log in again."
                                        else "Spotify is temporarily unavailable. Your saved session has been kept; try connecting again."
                                )
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to restore session"
                    )
                }
            }
        }
    }

    fun connectWithCookies(spDc: String, spKey: String) {
        if (spDc.isBlank()) return
        homeFeedJob?.cancel()
        homeFeedJob = null
        _uiState.update { SpotifyImportUiState(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { refreshWithCookies(spDc, spKey) }
                .onSuccess {
                    loadSources()
                    loadHomeFeed()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to connect to Spotify"
                        )
                    }
                }
        }
    }

    fun loadSources() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                ensureAuthenticated()
                val meResult = Spotify.me().getOrThrow()
                val likedSongsResult = Spotify.likedSongs(limit = 1, offset = 0).getOrThrow()

                val playlistsList = mutableListOf<SpotifyPlaylist>()
                var offset = 0
                val limit = 50
                while (true) {
                    val page = spotifyCallWithTokenRetry {
                        Spotify.myPlaylists(limit = limit, offset = offset).getOrThrow()
                    }
                    if (page.items.isEmpty()) break
                    playlistsList.addAll(page.items)
                    offset += page.items.size
                    if (offset >= page.total || page.items.size < limit) break
                }

                _uiState.update {
                    it.copy(
                        playlists = playlistsList.distinctBy { it.id },
                        likedSongsCount = likedSongsResult.total,
                        isLoading = false,
                        accountName = meResult.displayName.orEmpty(),
                        accountAvatarUrl = meResult.images.firstOrNull()?.url
                    )
                }
            } catch (e: Exception) {
                reportException(e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to fetch playlists"
                    )
                }
            }
        }
    }

    fun loadHomeFeed() {
        if (homeFeedJob?.isActive == true) return
        homeFeedJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isHomeFeedLoading = true, homeFeedErrorMessage = null) }
            try {
                var session = ensureAuthenticated()
                if (session.clientId.isNullOrBlank()) {
                    session = refreshWithCookies(session.spDc, session.spKey.orEmpty())
                }

                val language = Locale.getDefault().toLanguageTag().ifBlank { "en" }
                val shelves = try {
                    Spotify.homeFeed(
                        clientId = session.clientId.orEmpty(),
                        timeZone = ZoneId.systemDefault().id,
                        language = language,
                    ).getOrThrow()
                } catch (error: Exception) {
                    if ((error as? Spotify.SpotifyException)?.statusCode != 401) throw error
                    session = refreshWithCookies(session.spDc, session.spKey.orEmpty())
                    Spotify.homeFeed(
                        clientId = session.clientId.orEmpty(),
                        timeZone = ZoneId.systemDefault().id,
                        language = language,
                    ).getOrThrow()
                }

                val playlistShelves = shelves.mapNotNull { shelf ->
                    val playlists = shelf.items.filter { it.type == SpotifyHomeItemType.PLAYLIST }.distinctBy { it.uri }
                    shelf.copy(items = playlists).takeIf { playlists.isNotEmpty() }
                }
                _uiState.update {
                    it.copy(
                        homeShelves = playlistShelves,
                        isHomeFeedLoading = false,
                        homeFeedErrorMessage = null,
                    )
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    reportException(e)
                    _uiState.update {
                        it.copy(
                            isHomeFeedLoading = false,
                            homeFeedErrorMessage = e.message ?: "Failed to load Spotify recommendations",
                        )
                    }
                }
            }
        }
    }

    fun importHomePlaylist(item: SpotifyHomeItem) {
        val playlistId = item.playlistId ?: return
        startImport(listOf(playlistId))
    }

    fun logout() {
        homeFeedJob?.cancel()
        homeFeedJob = null
        viewModelScope.launch(Dispatchers.IO) {
            SpotifyAuth.clearWebSession()
            context.dataStore.edit { prefs ->
                prefs.remove(SpotifySessionKey)
            }
            Spotify.accessToken = null
            Spotify.clientId = ""
            _uiState.update {
                SpotifyImportUiState(
                    isAuthenticated = false,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun <T> spotifyCallWithTokenRetry(block: suspend () -> T): T =
        runCatching { block() }
            .getOrElse { error ->
                if ((error as? Spotify.SpotifyException)?.statusCode != 401) {
                    throw error
                }
                ensureAuthenticated(forceRefresh = true)
                block()
            }

    suspend fun fetchPlaylistTracks(playlistId: String): List<Song> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<SpotifyTrack>()
        var offset = 0
        val limit = 100
        while (true) {
            val page = spotifyCallWithTokenRetry {
                Spotify.playlistTracks(playlistId, limit = limit, offset = offset).getOrThrow()
            }
            if (page.items.isEmpty()) break
            tracks.addAll(page.items.mapNotNull { it.track })
            offset += page.items.size
            if (offset >= page.total || page.items.size < limit) break
        }

        return@withContext tracks.map { track ->
            Song(
                song = SongEntity(
                    id = "",
                    title = track.name,
                    duration = track.durationMs / 1000,
                    thumbnailUrl = SpotifyMapper.getTrackThumbnail(track),
                ),
                artists = track.artists.map { ArtistEntity(id = "", name = it.name) },
            )
        }
    }

    suspend fun fetchLikedSongsTracks(): List<Song> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<SpotifyTrack>()
        var offset = 0
        val limit = 50
        while (true) {
            val page = spotifyCallWithTokenRetry {
                Spotify.likedSongs(limit = limit, offset = offset).getOrThrow()
            }
            if (page.items.isEmpty()) break
            tracks.addAll(page.items.map { it.track })
            offset += page.items.size
            if (offset >= page.total || page.items.size < limit) break
        }

        return@withContext tracks.map { track ->
            Song(
                song = SongEntity(
                    id = "",
                    title = track.name,
                    duration = track.durationMs / 1000,
                    thumbnailUrl = SpotifyMapper.getTrackThumbnail(track),
                ),
                artists = track.artists.map { ArtistEntity(id = "", name = it.name) },
            )
        }
    }

    private suspend fun loadImportData(id: String): PlaylistImportData? {
        if (id == "liked_songs") {
            return PlaylistImportData(
                title = context.getString(R.string.spotify_liked_songs),
                songs = fetchLikedSongsTracks(),
                localPlaylistId = "SPOTIFY_LIKED_SONGS",
                thumbnailUrl = null,
            )
        }

        val playlist = _uiState.value.playlists.firstOrNull { it.id == id }
            ?: _uiState.value.homeShelves.asSequence()
                .flatMap { it.items.asSequence() }
                .firstOrNull { it.playlistId == id }
                ?.asSpotifyPlaylist()
            ?: return null
        return PlaylistImportData(
            title = playlist.name,
            songs = fetchPlaylistTracks(playlist.id),
            localPlaylistId = "SPOTIFY_PLAYLIST_${playlist.id}",
            thumbnailUrl = SpotifyMapper.getPlaylistThumbnail(playlist),
        )
    }

    private suspend fun matchSongs(
        importData: PlaylistImportData,
        totalSongs: Int,
    ): List<MediaMetadata> = coroutineScope {
        val completedCount = AtomicInteger(0)
        val semaphore = Semaphore(4)

        importData.songs.map { song ->
            async {
                semaphore.withPermit {
                    var matchedSong: MediaMetadata? = null
                    try {
                        val artist = song.artists.firstOrNull()?.name.orEmpty()
                        val query = if (artist.isEmpty()) song.title else "$artist ${song.title}"
                        val searchResult = YouTube.search(
                            query = query,
                            filter = SearchFilter.FILTER_SONG,
                        ).getOrNull()
                        val candidates = searchResult?.items
                            ?.filterIsInstance<SongItem>()
                            ?.distinctBy { it.id }
                            .orEmpty()
                        val bestMatch = candidates.maxByOrNull { candidate ->
                            SpotifyMapper.matchScore(
                                spotifyTitle = song.title,
                                spotifyArtist = song.artists.joinToString(" ") { it.name },
                                spotifyDurationMs = song.song.duration * 1000,
                                candidateTitle = candidate.title,
                                candidateArtist = candidate.artists.joinToString(" ") { it.name },
                                candidateDurationSec = candidate.duration,
                            )
                        }
                        matchedSong = bestMatch?.toMediaMetadata()
                    } catch (e: Exception) {
                        reportException(e)
                    } finally {
                        val completed = completedCount.incrementAndGet()
                        _importProgress.update {
                            SpotifyImportProgress(
                                playlistName = importData.title,
                                currentSongIndex = completed,
                                totalSongs = totalSongs,
                                percent = completed.toFloat() / totalSongs,
                            )
                        }
                    }
                    matchedSong
                }
            }
        }.awaitAll().filterNotNull()
    }

    fun startImport(selectedIds: List<String>) {
        importJob?.cancel()
        importJob = viewModelScope.launch(Dispatchers.IO) {
            _importProgress.update { null }

            try {
                for (id in selectedIds) {
                    val importData = loadImportData(id) ?: continue

                    val totalSongs = importData.songs.size
                    if (totalSongs == 0) {
                        database.withTransaction {
                            val now = LocalDateTime.now()
                            saveImportedPlaylistHeader(importData, now)
                            clearPlaylist(importData.localPlaylistId)
                        }

                        _importProgress.update {
                            SpotifyImportProgress(
                                playlistName = importData.title,
                                currentSongIndex = 0,
                                totalSongs = 0,
                                percent = 1.0f
                            )
                        }
                        continue
                    }

                    _importProgress.update {
                        SpotifyImportProgress(
                            playlistName = importData.title,
                            currentSongIndex = 0,
                            totalSongs = totalSongs,
                            percent = 0.0f
                        )
                    }

                    val matchedMedia = matchSongs(importData, totalSongs)

                    var hasRemoteCopy = false
                    database.withTransaction {
                        val now = LocalDateTime.now()
                        val entity = saveImportedPlaylistHeader(importData, now)

                        matchedMedia.forEach { metadata ->
                            insert(metadata)
                        }

                        clearPlaylist(importData.localPlaylistId)
                        matchedMedia.forEachIndexed { index, metadata ->
                            insert(
                                PlaylistSongMap(
                                    playlistId = importData.localPlaylistId,
                                    songId = metadata.id,
                                    position = index,
                                    setVideoId = metadata.setVideoId,
                                )
                            )
                        }
                        update(entity.copy(lastUpdateTime = now))
                        hasRemoteCopy = entity.browseId != null
                    }
                    // A playlist that already has a remote copy is left alone so re-importing does not duplicate its songs there.
                    if (!hasRemoteCopy) {
                        syncUtils.uploadPlaylistInBackground(importData.localPlaylistId, matchedMedia.map { it.id })
                    }
                }
                _importProgress.update { old ->
                    old?.copy(isFinished = true) ?: SpotifyImportProgress(
                        playlistName = "",
                        currentSongIndex = 0,
                        totalSongs = 0,
                        percent = 1.0f,
                        isFinished = true
                    )
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    reportException(e)
                    _uiState.update { it.copy(errorMessage = e.message ?: "Failed to import playlists") }
                }
            } finally {
                if (_importProgress.value?.isFinished != true) {
                    _importProgress.update { null }
                }
            }
        }
    }

    fun cancelImport() {
        importJob?.cancel()
        importJob = null
        _importProgress.update { null }
    }

    fun dismissImportProgress() {
        _importProgress.update { null }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

private fun SpotifyHomeItem.asSpotifyPlaylist(): SpotifyPlaylist = SpotifyPlaylist(
    id = playlistId.orEmpty(),
    name = title,
    images = artworkUrl?.let { listOf(SpotifyImage(url = it)) }.orEmpty(),
    uri = uri,
)

private suspend fun MusicDatabase.saveImportedPlaylistHeader(
    importData: PlaylistImportData,
    updatedAt: LocalDateTime,
): PlaylistEntity {
    val existing = playlist(importData.localPlaylistId).first()
    val entity = existing?.playlist?.copy(
        name = importData.title,
        bookmarkedAt = existing.playlist.bookmarkedAt ?: updatedAt,
        lastUpdateTime = updatedAt,
        thumbnailUrl = importData.thumbnailUrl,
        isEditable = true,
    ) ?: PlaylistEntity(
        id = importData.localPlaylistId,
        name = importData.title,
        bookmarkedAt = updatedAt,
        lastUpdateTime = updatedAt,
        thumbnailUrl = importData.thumbnailUrl,
        isEditable = true,
    )

    if (existing == null) insert(entity) else update(entity)
    return entity
}
