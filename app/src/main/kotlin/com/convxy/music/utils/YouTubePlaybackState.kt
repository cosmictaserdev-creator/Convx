/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Session flag marking "the YouTube watch screen is driving playback".
 *
 * While active, stream resolution prefers a muxed audio+video format. A session
 * flag (rather than per-video-id registration) is what makes *related videos*
 * work: the queue auto-advances inside ExoPlayer and the next stream is
 * resolved before any UI can react, so the only reliable gate is one that is
 * already true when resolution happens.
 *
 * [lastVideoId] survives the session so the global mini player can re-open the
 * watch screen for the video that is still playing; it is cleared by
 * PlayerConnection when a *different* item becomes current (i.e. the user has
 * moved on to non-video playback).
 *
 * MusicService consults this next to the global `WatchVideoKey` preference in
 * exactly two places that must agree: the stream cache key (`#video`
 * namespace) and the format selection inside
 * [YTPlayerUtils.playerResponseForPlayback] (whose `videoMode` flows from the
 * same computation in MusicService).
 */
object YouTubePlaybackState {
    private val _sessionActive = MutableStateFlow(false)

    /**
     * True while the watch screen is in immersive fullscreen. The activity
     * level floating nav bar / mini player dock read this to step out of the
     * video's way — they are drawn above every destination and would otherwise
     * float over the fullscreen player.
     */
    val isFullscreenActive = MutableStateFlow(false)

    /** Observable for debugging/tests; the hot path reads [isActive]. */
    val sessionActive: StateFlow<Boolean> = _sessionActive.asStateFlow()

    @Volatile
    var lastVideoId: String? = null
        private set

    /**
     * Metadata of the video the user just tapped, handed to the watch screen so
     * the queue can start IMMEDIATELY with real title/thumbnail instead of
     * waiting for the watch-page round trip. Always keyed by id on use, and
     * cleared after consumption — a stale entry can never apply elsewhere.
     */
    @Volatile
    var pendingVideo: com.music.innertube.models.WebVideo? = null

    fun isActive(): Boolean = _sessionActive.value

    /**
     * Marks video mode as active for the given video. Called by the watch
     * screen when it starts driving playback, and again on every queue
     * transition while it stays open.
     */
    fun begin(videoId: String) {
        lastVideoId = videoId
        _sessionActive.value = true
    }

    /** Called when the watch screen leaves composition. Playback continues. */
    fun end() {
        _sessionActive.value = false
    }

    /** Drops the mini-player restore marker once a *different* item becomes current. */
    fun onItemTransition(newMediaId: String) {
        val marker = lastVideoId
        if (marker != null && marker != newMediaId) lastVideoId = null
    }
}
