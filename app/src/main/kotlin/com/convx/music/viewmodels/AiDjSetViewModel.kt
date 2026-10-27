/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.ai.AiDjSetBuilder
import com.convx.music.ai.AiDjSetResult
import com.convx.music.db.MusicDatabase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiDjSetState(
    val isRunning: Boolean = false,
    val logs: List<String> = emptyList(),
    val result: AiDjSetResult? = null,
    /** True when generation finishes without a result. */
    val failed: Boolean = false,
    val isSaving: Boolean = false,
    /** Playlist id after the generated set is saved. */
    val savedPlaylistId: String? = null,
)

class AiDjSetViewModel(
    private val context: Context,
    private val database: MusicDatabase,
) : ViewModel() {

    private val _state = MutableStateFlow(AiDjSetState())
    val state: StateFlow<AiDjSetState> = _state.asStateFlow()

    private var job: Job? = null

    fun generate(
        prompt: String?,
        seedSongId: String?,
        songCount: Int,
        targetDurationSeconds: Long? = null,
        concertMode: Boolean = false,
    ) {
        if (_state.value.isRunning) return
        _state.value = AiDjSetState(isRunning = true)

        job = viewModelScope.launch {
            val result = AiDjSetBuilder.buildSet(
                context = context,
                database = database,
                prompt = prompt,
                seedSongId = seedSongId,
                songCount = songCount,
                targetDurationSeconds = targetDurationSeconds,
                concertMode = concertMode,
                // Keep a Logcat copy because the running UI clears logs with the result.
                onLog = { line -> Log.d("AiDjSet", line); _state.update { it.copy(logs = it.logs + line) } },
            )
            _state.update { it.copy(isRunning = false, result = result, failed = result == null) }
        }
    }

    fun save() {
        val result = _state.value.result ?: return
        if (_state.value.isSaving || _state.value.savedPlaylistId != null) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val playlistId = AiDjSetBuilder.savePlaylist(database, result)
            _state.update { it.copy(isSaving = false, savedPlaylistId = playlistId) }
        }
    }

    override fun onCleared() {
        job?.cancel()
    }

    fun reset() {
        job?.cancel()
        _state.value = AiDjSetState()
    }
}
