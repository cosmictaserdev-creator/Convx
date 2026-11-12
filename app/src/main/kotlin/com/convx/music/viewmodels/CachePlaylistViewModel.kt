/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cache.ContentMetadata
import com.convx.music.constants.HideExplicitKey
import com.convx.music.constants.HideVideoSongsKey
import com.convx.music.constants.DataSaverEnabledKey
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.Song
import com.convx.music.extensions.filterExplicit
import com.convx.music.extensions.filterVideoSongs
import com.convx.music.playback.cacheKeyBelongsToSong
import com.convx.music.playback.isPlaybackAudioCacheKey
import com.convx.music.playback.HlsAudioCache
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CachePlaylistViewModel(
    private val context: Context,
    private val database: MusicDatabase,
    private val playerCache: SimpleCache,
    private val downloadCache: SimpleCache,
    private val applicationScope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val thumbArg = savedStateHandle.get<String>("thumb")?.takeIf { it.isNotEmpty() }

    private val _cachedSongs = MutableStateFlow<List<Song>>(emptyList())
    val cachedSongs: StateFlow<List<Song>> = _cachedSongs
    private data class HlsSnapshot(val url: String, val lengths: List<Pair<String, Long>>, val spans: List<Triple<String, Long, Long>>)
    private data class HlsResult(val snapshot: HlsSnapshot, val complete: Boolean, val checkedAt: Long)
    private val hlsResults = mutableMapOf<String, HlsResult>()

    private fun hasCompleteHls(songId: String, keys: List<String>): Boolean {
        val url = HlsAudioCache.savedUrl(context, songId) ?: return false
        val hlsKeys = keys.filter { it.startsWith("$songId#hls-audio:") }.sorted()
        val spans = hlsKeys.asSequence()
            .flatMap { key -> playerCache.getCachedSpans(key).asSequence().map { Triple(key, it.position, it.length) } }
            .toList()
        val lengths = hlsKeys.map { it to ContentMetadata.getContentLength(playerCache.getContentMetadata(it)) }
        val snapshot = HlsSnapshot(url, lengths, spans)
        hlsResults[songId]?.takeIf { it.snapshot == snapshot &&
            (it.complete || SystemClock.elapsedRealtime() - it.checkedAt < 30_000L) }?.let { return it.complete }
        return HlsAudioCache.isComplete(songId, url, playerCache).also {
            hlsResults[songId] = HlsResult(snapshot, it, SystemClock.elapsedRealtime())
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val hideExplicit = context.dataStore.get(HideExplicitKey, false)
                val hideVideoSongs = context.dataStore.get(HideVideoSongsKey, false) || context.dataStore.get(DataSaverEnabledKey, false)
                // Player cache keys carry suffixes ("id#source-<hash>", "id#aac"), so group them by song id.
                val keysById = playerCache.keys.groupBy { it.substringBefore('#') }
                val downloadedIds = downloadCache.keys.asSequence()
                    .filter(::isPlaybackAudioCacheKey)
                    .filter { key ->
                        val length = ContentMetadata.getContentLength(downloadCache.getContentMetadata(key))
                        length > 0 && downloadCache.isCached(key, 0, length)
                    }
                    .map { it.substringBefore('#') }
                    .toSet()
                val pureCacheIds = keysById.keys.subtract(downloadedIds)
                hlsResults.keys.retainAll(pureCacheIds)

                val songs = if (pureCacheIds.isNotEmpty()) {
                    database.getSongsByIds(pureCacheIds.toList())
                } else {
                    emptyList()
                }

                val completeSongs = songs.filter { song ->
                    keysById[song.song.id].orEmpty().filter(::isPlaybackAudioCacheKey).any { key ->
                        val cachedLength = ContentMetadata.getContentLength(playerCache.getContentMetadata(key))
                        val length = cachedLength.takeIf { it > 0 }
                            ?: song.format?.contentLength?.takeIf { key == song.song.id && it > 0 }
                        length != null && playerCache.isCached(key, 0, length)
                    } || hasCompleteHls(song.song.id, keysById[song.song.id].orEmpty())
                }

                if (completeSongs.isNotEmpty()) {
                    database.query {
                        completeSongs.forEach {
                            if (it.song.dateDownload == null) {
                                update(it.song.copy(dateDownload = LocalDateTime.now()))
                            }
                        }
                    }
                }

                _cachedSongs.value = completeSongs
                    .filter { it.song.dateDownload != null }
                    .sortedByDescending { it.song.dateDownload }
                    .filterExplicit(hideExplicit)
                    .filterVideoSongs(hideVideoSongs)

                delay(1000)
            }
        }
    }

    fun removeSongFromCache(songId: String) {
        applicationScope.launch(Dispatchers.IO) {
            playerCache.keys.filter { cacheKeyBelongsToSong(it, songId) }.forEach(playerCache::removeResource)
            HlsAudioCache.forget(context, songId)
        }
    }
}
