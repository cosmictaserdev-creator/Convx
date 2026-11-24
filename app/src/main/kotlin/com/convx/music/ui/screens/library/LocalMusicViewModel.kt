package com.convx.music.ui.screens.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.Song
import com.convx.music.utils.LocalAudioScanner
import com.convx.music.utils.LocalFolderIndex
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class LocalMusicViewModel(
    private val database: MusicDatabase,
) : ViewModel() {

    val songs: StateFlow<List<Song>> = database.localSongsByNameAsc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val albums: StateFlow<List<Album>> = database.albumsLocalByNameAsc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val artists: StateFlow<List<Artist>> = database.artistsLocalByNameAsc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val indexedFolders = MutableStateFlow<List<LocalFolderIndex.Folder>>(emptyList())
    val folders = combine(indexedFolders, songs) { folders, songs ->
        val ids = songs.mapTo(hashSetOf()) { it.id }
        folders.map { it.copy(songIds = it.songIds.filter(ids::contains)) }
            .filter { it.songIds.isNotEmpty() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanResult = MutableStateFlow<LocalAudioScanner.ScanResult?>(null)
    val scanResult: StateFlow<LocalAudioScanner.ScanResult?> = _scanResult.asStateFlow()

    /** Set once a scan finishes, so re-entering the screen doesn't rescan on every visit. */
    private var lastScanAt = 0L

    /**
     * [force] is what a tapped Rescan button passes; screen-entry auto-scans leave it
     * false so a quick trip to the player and back doesn't kick off a full MediaStore
     * sweep every time.
     */
    fun scanDevice(context: Context, force: Boolean = true) {
        if (_isScanning.value) return
        if (!force && System.currentTimeMillis() - lastScanAt < AUTO_SCAN_INTERVAL_MS) return
        viewModelScope.launch {
            _isScanning.value = true
            try {
                val result = LocalAudioScanner.scanAndInsert(context, database)
                _scanResult.value = result
                indexedFolders.value = LocalFolderIndex.load(context)
                lastScanAt = System.currentTimeMillis()
                Timber.tag("LocalMusicViewModel").i("Scan complete: $result")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.tag("LocalMusicViewModel").e(e, "Scan failed")
            } finally {
                _isScanning.value = false
            }
        }
    }

    private companion object {
        /** How stale an auto-scan may be before entering the screen triggers a fresh one. */
        const val AUTO_SCAN_INTERVAL_MS = 5 * 60 * 1000L
    }
}
