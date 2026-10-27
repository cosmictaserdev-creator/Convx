package com.convx.music.client.watch

import com.convx.music.catalog.*
import com.convx.music.playback.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.random.Random

@Serializable enum class WatchCollection { PLAYLISTS, ARTISTS, ALBUMS, SONGS }
@Serializable enum class WatchEntryKind { TRACK, PLAYLIST, ALBUM, ARTIST }
@Serializable enum class WatchAction { SYNC, PLAY_PAUSE, NEXT, PREVIOUS, SEEK, VOLUME, SHUFFLE, REPEAT, QUEUE, BROWSE, OPEN, PLAY }

@Serializable
data class WatchEntry(
    val providerId: String,
    val id: String,
    val title: String,
    val kind: WatchEntryKind = WatchEntryKind.TRACK,
    val artists: List<String> = emptyList(),
    val artworkUrl: String? = null,
    val durationMs: Long? = null,
    val queueIndex: Int? = null,
) {
    fun request() = PlaybackRequestTrack(providerId, id, title, artists, durationMs, artworkUrl)
}

@Serializable
data class WatchCommand(
    val version: Int = 1,
    val action: WatchAction,
    val collection: WatchCollection? = null,
    val entry: WatchEntry? = null,
    val positionMs: Long? = null,
    val volume: Float? = null,
)

@Serializable
data class WatchSnapshot(
    val version: Int = 1,
    val sessionId: String,
    val sequence: Long = 0,
    val track: WatchEntry? = null,
    val isPlaying: Boolean = false,
    val canSkipPrevious: Boolean = false,
    val canSkipNext: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long? = null,
    val volume: Float = 1f,
    val canAdjustVolume: Boolean = true,
    val shuffle: Boolean = false,
    val repeatMode: String = "OFF",
    val queue: List<WatchEntry> = emptyList(),
    val libraryTitle: String = "Library",
    val library: List<WatchEntry> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
)

class WatchObservation internal constructor(private val job: Job) {
    fun close() = job.cancel()
}

/** The phone owns playback; watches exchange bounded metadata and explicit commands. */
class WatchCompanion(
    private val playback: PlaybackController,
    private val catalog: MusicCatalogRepository,
    private val settings: PlaybackSettingsRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val mutable = MutableStateFlow(WatchSnapshot(sessionId = Random.nextLong().toString()))
    val state: StateFlow<WatchSnapshot> = mutable.asStateFlow()
    private var browseJob: Job? = null
    private val refresh = MutableStateFlow(0L)

    init {
        scope.launch {
            combine(playback.playbackSnapshot, playback.queueSnapshot, settings.observeSettings()) { player, queue, preferences ->
                Triple(player, queue, preferences)
            }.collect { (player, queue, preferences) ->
                mutable.update { previous -> previous.copy(sequence = previous.sequence + 1,
                    track = player.track?.let { WatchEntry(it.providerId, it.id, it.title, artists = it.artists, artworkUrl = it.artworkUrl, durationMs = player.durationMs) },
                    isPlaying = player.isPlaying, canSkipPrevious = player.canSkipPrevious, canSkipNext = player.canSkipNext,
                    positionMs = player.positionMs, durationMs = player.durationMs, volume = preferences.volume, canAdjustVolume = !preferences.bitPerfectEnabled,
                    shuffle = player.shuffleEnabled, repeatMode = player.repeatMode.name,
                    queue = queue.entries.take(40).mapIndexed { index, item -> WatchEntry(item.providerId, item.id, item.title, artists = item.artists,
                        artworkUrl = item.artworkUrl, durationMs = item.durationMs, queueIndex = index) }) }
            }
        }
    }

    fun observeSnapshots(observer: (String) -> Unit): WatchObservation = WatchObservation(scope.launch {
        combine(state, refresh) { snapshot, revision -> snapshot to revision }
            .distinctUntilChangedBy { (snapshot, revision) -> snapshot.copy(positionMs = snapshot.positionMs / 1_000 * 1_000, sequence = 0) to revision }
            .collect { (snapshot, _) ->
                var outgoing = snapshot
                var encoded = json.encodeToString(outgoing)
                while (encoded.encodeToByteArray().size > 56 * 1024 && (outgoing.queue.isNotEmpty() || outgoing.library.isNotEmpty())) {
                    outgoing = outgoing.copy(queue = outgoing.queue.dropLast(1), library = outgoing.library.dropLast(1))
                    encoded = json.encodeToString(outgoing)
                }
                observer(encoded)
            }
    })

    fun receive(payload: String) = scope.launch {
        try {
            require(payload.length <= 32_768)
            val command = json.decodeFromString<WatchCommand>(payload)
            require(command.version == 1)
            when (command.action) {
                WatchAction.SYNC -> refresh.update { it + 1 }
                WatchAction.PLAY_PAUSE -> playback.togglePlayPause()
                WatchAction.NEXT -> if (playback.playbackSnapshot.value.canSkipNext) playback.requestSkip(true)
                WatchAction.PREVIOUS -> if (playback.playbackSnapshot.value.canSkipPrevious) playback.requestSkip(false)
                WatchAction.SEEK -> command.positionMs?.let { target ->
                    playback.playbackSnapshot.value.durationMs?.takeIf { it > 0 }?.let { playback.seekTo(target.coerceIn(0, it)) }
                }
                WatchAction.VOLUME -> command.volume?.takeIf { it.isFinite() }?.let { volume -> settings.updateSettings { if (it.bitPerfectEnabled) it else it.copy(volume = volume.coerceIn(0f, 1f)) } }
                WatchAction.SHUFFLE -> playback.toggleShuffle()
                WatchAction.REPEAT -> playback.cycleRepeatMode()
                WatchAction.QUEUE -> refresh.update { it + 1 }
                WatchAction.BROWSE -> command.collection?.let(::browse)
                WatchAction.OPEN -> command.entry?.let(::open)
                WatchAction.PLAY -> command.entry?.let { selected ->
                    val queue = playback.queueSnapshot.value.entries
                    val proposed = selected.queueIndex?.takeIf { queue.getOrNull(it)?.let { item -> item.id == selected.id && item.providerId == selected.providerId } == true }
                    val index = proposed ?: queue.indexOfFirst { it.id == selected.id && it.providerId == selected.providerId }
                    if (index >= 0) playback.playQueueItem(index)
                    else {
                        val tracks = state.value.library.filter { it.kind == WatchEntryKind.TRACK }
                        val libraryIndex = tracks.indexOfFirst { it.id == selected.id && it.providerId == selected.providerId }
                        if (libraryIndex >= 0) playback.playQueue(tracks.map(WatchEntry::request), libraryIndex)
                    }
                }
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { mutable.update { it.copy(sequence = it.sequence + 1, message = "Couldn't complete the watch action. Try again.") } }
    }

    private fun browse(collection: WatchCollection) = load(collection.name.lowercase().replaceFirstChar { it.uppercase() }) {
        val kind = when (collection) {
            WatchCollection.PLAYLISTS -> CatalogLibraryCollection.PLAYLISTS
            WatchCollection.ARTISTS -> CatalogLibraryCollection.ARTISTS
            WatchCollection.ALBUMS -> CatalogLibraryCollection.LIKED_ALBUMS
            WatchCollection.SONGS -> CatalogLibraryCollection.LIKED_TRACKS
        }
        when (val result = catalog.library(kind)) {
            is CatalogResult.Success -> result.value.entries
            is CatalogResult.Failure -> error("Library unavailable")
        }
    }

    private fun open(entry: WatchEntry) {
        val id = CatalogId(entry.providerId, entry.id)
        load(entry.title) {
            when (entry.kind) {
                WatchEntryKind.PLAYLIST -> when (val result = catalog.playlist(id)) {
                    is CatalogResult.Success -> result.value.tracks
                    is CatalogResult.Failure -> error("Playlist unavailable")
                }
                WatchEntryKind.ALBUM -> when (val result = catalog.album(id)) {
                    is CatalogResult.Success -> result.value.tracks
                    is CatalogResult.Failure -> error("Album unavailable")
                }
                WatchEntryKind.ARTIST -> when (val result = catalog.artist(id)) {
                    is CatalogResult.Success -> result.value.sections.flatMap { it.entries }
                    is CatalogResult.Failure -> error("Artist unavailable")
                }
                WatchEntryKind.TRACK -> emptyList()
            }
        }
    }

    private fun load(title: String, block: suspend () -> List<CatalogEntry>) {
        browseJob?.cancel()
        browseJob = scope.launch {
            mutable.update { it.copy(sequence = it.sequence + 1, libraryTitle = title, library = emptyList(), isLoading = true, message = null) }
            try {
                val entries = block().take(30).mapNotNull { it.watchEntry() }
                mutable.update { it.copy(sequence = it.sequence + 1, library = entries, isLoading = false) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutable.update { it.copy(sequence = it.sequence + 1, isLoading = false, message = "Open Convx on your phone to check your library connection.") } }
        }
    }

    fun close() = scope.cancel()
}

private fun CatalogEntry.watchEntry(): WatchEntry? = when (this) {
    is CatalogEntry.Track -> WatchEntry(id.providerId, id.value, title, artists = artists.map { it.name }, artworkUrl = artworkUrls.firstOrNull(), durationMs = durationMillis)
    is CatalogEntry.Playlist -> WatchEntry(id.providerId, id.value, title, WatchEntryKind.PLAYLIST, artworkUrl = artworkUrls.firstOrNull())
    is CatalogEntry.Album -> WatchEntry(id.providerId, id.value, title, WatchEntryKind.ALBUM, artists.map { it.name }, artworkUrls.firstOrNull())
    is CatalogEntry.Artist -> WatchEntry(id.providerId, id.value, title, WatchEntryKind.ARTIST, artworkUrl = artworkUrls.firstOrNull())
    is CatalogEntry.Browse -> null
}

fun watchCompanionModule() = module { factory { WatchCompanion(get(), get(), get()) } }
