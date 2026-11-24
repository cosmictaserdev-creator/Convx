package com.convx.music.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.lyrics.LyricsSyncLevel
import kotlinx.coroutines.flow.combine
import com.convx.music.lyrics.LyricsProviderRegistry
import com.convx.music.lyrics.LyricsProviderRepository
import com.convx.music.lyrics.LyricsProviderStatus
import com.convx.music.lyrics.LyricsTranslationHelper
import com.convx.music.models.MediaMetadata
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LyricsProviderViewModel(
    private val repository: LyricsProviderRepository,
    private val database: MusicDatabase,
) : ViewModel() {
    private var song: MediaMetadata? = null
    private var observation: Job? = null
    private var selection: Job? = null
    private val _statuses = MutableStateFlow<Map<String, LyricsProviderStatus>>(emptyMap())
    val statuses = _statuses.asStateFlow()
    private val _syncLevels = MutableStateFlow<Map<String, LyricsSyncLevel>>(emptyMap())
    val syncLevels = _syncLevels.asStateFlow()
    private val _requested = MutableStateFlow<String?>(null)
    val requested = _requested.asStateFlow()
    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    fun bind(metadata: MediaMetadata, lyrics: LyricsEntity?) {
        val previous = song
        song = metadata
        repository.remember(metadata, lyrics)
        if (previous != null && previous.id == metadata.id && previous.title == metadata.title &&
            previous.artists == metadata.artists && previous.duration == metadata.duration && previous.album == metadata.album) return
        selection?.cancel()
        observation?.cancel()
        _requested.value = null
        _error.value = false
        _statuses.value = emptyMap()
        observation = viewModelScope.launch {
            combine(repository.statuses(metadata), repository.syncLevels(metadata)) { statuses, levels -> statuses to levels }
                .collect { (statuses, levels) -> _statuses.value = statuses; _syncLevels.value = levels }
        }
    }

    fun select(providerId: String, onSelected: () -> Unit) {
        val metadata = song ?: return
        val provider = LyricsProviderRegistry.getProvider(providerId) ?: return
        selection?.cancel()
        _requested.value = provider.id
        _error.value = false
        selection = viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.fetch(provider, metadata)
                if (song?.id != metadata.id || _requested.value != provider.id) return@launch
                val lyrics = result.getOrNull()
                if (lyrics != null) {
                    database.withTransaction { upsert(LyricsEntity(metadata.id, lyrics, provider.id)) }
                    LyricsTranslationHelper.triggerClearTranslations()
                    kotlinx.coroutines.withContext(Dispatchers.Main) { onSelected() }
                } else {
                    _error.value = true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _error.value = true
            } finally {
                if (_requested.value == provider.id) _requested.value = null
            }
        }
    }

    fun dismiss() {
        selection?.cancel()
        _requested.value = null
        _error.value = false
    }
}
