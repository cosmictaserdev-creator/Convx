/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convxy.music.comments.CommentRequestGate
import com.convxy.music.comments.CommentTrackRef
import com.convxy.music.comments.CommentsRepository
import com.convxy.music.comments.CommentsStatus
import com.convxy.music.comments.runCatchingSuspend
import com.convxy.music.comments.TimestampedComment
import com.convxy.music.utils.reportException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Everything the comments UI needs to render, for exactly one track.
 *
 * [trackId] is carried in the state itself so a composable can assert that what it is about to draw
 * belongs to the song that is playing — the failure mode this feature most has to avoid is a sheet
 * showing the previous track's comments for a frame after a skip.
 */
@Immutable
data class TrackCommentsUiState(
    val trackId: String? = null,
    val isLoading: Boolean = false,
    /** Null until something is known about the track. Drives which empty/error copy to show. */
    val status: CommentsStatus? = null,
    val comments: List<TimestampedComment> = emptyList(),
    val sourceName: String? = null,
    val fromCache: Boolean = false,
    val message: String? = null,
) {
    val hasComments: Boolean get() = comments.isNotEmpty()

    companion object {
        val IDLE = TrackCommentsUiState()
    }
}

/**
 * State holder for the timestamped-comments sheet and the seek bar's comment markers.
 *
 * One instance is shared by both (it is obtained in `Player` and handed down), so opening the sheet
 * never triggers a second fetch for a track the seek bar already loaded, and closing it does not
 * throw the result away.
 *
 * Lifetime: scoped to the Activity's `ViewModelStore`, which is what the player lives in. That is
 * deliberate — the player is a persistent sheet, not a navigation destination, so a nav-scoped store
 * would reset the comments every time the user moved between tabs.
 *
 * What this class does NOT own: the playhead. Highlighting the live comment is a pure function of
 * (comments, position) and position already exists as Compose state in the player, ticking at the
 * 100ms cadence the seek bar needs anyway. The UI folds the two together with `derivedStateOf`, which
 * recomposes only when the *answer* changes — routing the position through here instead would mean a
 * ViewModel write ten times a second to produce a value the UI could compute for free.
 */
@HiltViewModel
class TrackCommentsViewModel
@Inject
constructor(
    private val repository: CommentsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackCommentsUiState.IDLE)
    val uiState: StateFlow<TrackCommentsUiState> = _uiState.asStateFlow()

    private val gate = CommentRequestGate()
    private var loadJob: Job? = null
    private var boundKey: String? = null
    private var currentTrack: CommentTrackRef? = null

    /**
     * Points the state holder at the now-playing track.
     *
     * Idempotent: re-binding to the track that is already bound — which recomposition, the sheet
     * reopening and configuration changes all do — neither restarts an in-flight load nor discards a
     * result that has already landed. Only a real track change (or a changed duration, which is part
     * of the cache key) starts work.
     */
    fun bind(track: CommentTrackRef?) {
        if (track == null) {
            boundKey = null
            currentTrack = null
            loadJob?.cancel()
            loadJob = null
            gate.clear()
            _uiState.value = TrackCommentsUiState.IDLE
            return
        }

        val key = track.cacheKey
        if (key == boundKey && (loadJob?.isActive == true || _uiState.value.status != null)) return

        boundKey = key
        load(track, force = false)
    }

    /** Re-fetches, bypassing and overwriting the cache. The retry/refresh affordance. */
    fun refresh() {
        val track = currentTrack ?: return
        boundKey = track.cacheKey
        load(track, force = true)
    }

    private fun load(track: CommentTrackRef, force: Boolean) {
        currentTrack = track
        val token = gate.begin(track.id, force = force)
        loadJob?.cancel()

        // Cleared synchronously, before any suspension point. Skipping a track therefore wipes the
        // old comments on the same frame the new track is bound, so there is no window — however
        // short — in which the sheet is showing comments that belong to a song that already ended.
        _uiState.value = TrackCommentsUiState(trackId = track.id, isLoading = true)

        loadJob = viewModelScope.launch(Dispatchers.IO) {
            val result = runCatchingSuspend { repository.commentsFor(track, forceRefresh = force) }
                .onFailure { reportException(it) }
                .getOrNull()

            // A response for a track the user has already left is dropped, not shown. Cancellation
            // gets most of these, but a fetch that finished its network call before the skip still
            // resumes here, and the gate is what stops it landing.
            if (!gate.isCurrent(token, track.id)) return@launch

            _uiState.value = if (result == null) {
                TrackCommentsUiState(
                    trackId = track.id,
                    isLoading = false,
                    status = CommentsStatus.FAILED,
                    message = "comments could not be loaded",
                )
            } else {
                TrackCommentsUiState(
                    trackId = track.id,
                    isLoading = false,
                    status = result.status,
                    comments = result.comments,
                    sourceName = result.sourceName,
                    fromCache = result.fromCache,
                    message = result.message,
                )
            }
        }
    }
}
