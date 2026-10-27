/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
import androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import com.convx.music.db.MusicDatabase
import com.convx.music.extensions.currentMetadata
import com.convx.music.extensions.getCurrentQueueIndex
import com.convx.music.extensions.getQueueWindows
import com.convx.music.extensions.metadata
import com.convx.music.extensions.togglePlayPause
import com.convx.music.playback.MusicService.MusicBinder
import com.convx.music.playback.PlaybackRepeatMode
import com.convx.music.constants.AskBeforeReplacingQueueKey
import com.convx.music.playback.queues.ListQueue
import com.convx.music.playback.queues.Queue
import com.convx.music.utils.dataStore
import com.convx.music.utils.get
import com.convx.music.utils.reportException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

sealed interface VideoStreamLookup {
    data object Idle : VideoStreamLookup
    data object Loading : VideoStreamLookup
    data object Unavailable : VideoStreamLookup
    data class Ready(val stream: com.convx.music.utils.YTPlayerUtils.VideoStream) : VideoStreamLookup
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerConnection(
    context: Context,
    binder: MusicBinder,
    val database: MusicDatabase,
    scope: CoroutineScope,
) : Player.Listener, PlaybackController {
    private val connectionJob = SupervisorJob(scope.coroutineContext[Job])
    private val scope = CoroutineScope(scope.coroutineContext + connectionJob)
    private companion object {
        private const val TAG = "PlayerConnection"
        private const val SKIP_COMMIT_DELAY_MS = 140L
    }

    val service = binder.service
    override val sleepTimer: PlaybackSleepTimer get() = service.sleepTimer.sharedPolicy
    private val appContext = context.applicationContext
    private val playerReadinessFlow = service.isPlayerReady
    
    private fun getPlayerOrThrow(): ExoPlayer {
        return try {
            if (!playerReadinessFlow.value) {
                Timber.tag(TAG).w("Player accessed before service initialization complete; returning best-effort reference")
            }
            service.player
        } catch (e: UninitializedPropertyAccessException) {
            Timber.tag(TAG).e(e, "Fatal: player property accessed but not initialized")
            throw IllegalStateException("MusicService.player not initialized; possible race condition in service startup", e)
        }
    }

    val player: ExoPlayer
        get() = getPlayerOrThrow()

    val playbackState: MutableStateFlow<Int>
    private val playWhenReady: MutableStateFlow<Boolean>
    val isPlaying: kotlinx.coroutines.flow.StateFlow<Boolean>
    
    init {
        Timber.tag(TAG).d("PlayerConnection init: playerReady=${playerReadinessFlow.value}")
        
        val initialPlayer = try {
            getPlayerOrThrow()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during PlayerConnection initialization, using defaults")
            null
        }
        
        playbackState = MutableStateFlow(initialPlayer?.playbackState ?: Player.STATE_IDLE)
        playWhenReady = MutableStateFlow(initialPlayer?.playWhenReady ?: false)
        isPlaying = combine(playbackState, playWhenReady) { state, ready ->
            ready && state != STATE_ENDED
        }.stateIn(
            scope,
            SharingStarted.Lazily,
            initialPlayer?.let { it.playWhenReady && it.playbackState != STATE_ENDED } ?: false
        )
        
        Timber.tag(TAG).d("PlayerConnection state flows initialized successfully")
    }
    
    val isEffectivelyPlaying = combine(
        isPlaying,
        service.castConnectionHandler?.isCasting ?: MutableStateFlow(false),
        service.castConnectionHandler?.castIsPlaying ?: MutableStateFlow(false)
    ) { localPlaying, isCasting, castPlaying ->
        if (isCasting) castPlaying else localPlaying
    }.stateIn(
        scope,
        SharingStarted.Lazily,
        player.playbackState != STATE_ENDED && player.playWhenReady
    )
    
    val mediaMetadata = MutableStateFlow(player.currentMetadata)
    private val requestedVideo = MutableStateFlow<Pair<String, Long>?>(null)
    private var videoRequestGeneration = 0L
    private val officialVideos = com.music.innertube.OfficialVideoResolver()
    private val requestedVideoAvailability = MutableStateFlow<String?>(null)

    val videoAvailability = combine(mediaMetadata, requestedVideoAvailability) { metadata, requestedId ->
        metadata?.takeIf { it.id == requestedId }
    }.flatMapLatest { metadata ->
        flow {
            emit(metadata?.let {
                officialVideos.resolve(it.id, it.title, it.artists.map { artist -> artist.name }, it.duration, it.musicVideoType).getOrNull()
            })
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(0, 0), null)

    fun requestVideoAvailability(mediaId: String?) {
        requestedVideoAvailability.value = mediaId?.takeIf { it == mediaMetadata.value?.id }
    }

    /** Video lookup starts only after the user asks to watch the current track. */
    val videoStreamLookup = combine(mediaMetadata, requestedVideo) { metadata, request ->
        metadata?.takeIf { it.id == request?.first }?.let { it to requireNotNull(request) }
    }.flatMapLatest { request ->
        flow {
            if (request == null) {
                emit(VideoStreamLookup.Idle)
            } else {
                emit(VideoStreamLookup.Loading)
                val metadata = request.first
                val video = officialVideos.resolve(metadata.id, metadata.title, metadata.artists.map { it.name }, metadata.duration, metadata.musicVideoType).getOrNull()
                val stream = video?.let { com.convx.music.utils.YTPlayerUtils.videoStream(it.videoId)?.copy(mediaId = metadata.id) }
                emit(stream?.let(VideoStreamLookup::Ready) ?: VideoStreamLookup.Unavailable)
            }
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(0, 0), VideoStreamLookup.Idle)

    fun requestVideoStream(mediaId: String) {
        if (mediaMetadata.value?.id == mediaId) {
            requestedVideo.value = mediaId to ++videoRequestGeneration
        }
    }

    fun clearVideoStreamRequest() {
        requestedVideo.value = null
    }
    val currentSong =
        mediaMetadata.flatMapLatest {
            database.song(it?.id)
        }
    val currentLyrics = mediaMetadata.flatMapLatest { mediaMetadata ->
        database.lyrics(mediaMetadata?.id)
    }
    val currentFormat =
        mediaMetadata.flatMapLatest { mediaMetadata ->
            database.format(mediaMetadata?.id)
        }

    /** BPM, key, tier and transition style, straight from the DJ engine. */
    val djState = service.djState

    val crossfadeDurationMs = service.crossfadeDurationMs

    val queueTitle = MutableStateFlow<String?>(null)
    val queueWindows = MutableStateFlow<List<Timeline.Window>>(emptyList())
    val currentMediaItemIndex = MutableStateFlow(-1)
    val currentWindowIndex = MutableStateFlow(-1)

    val shuffleModeEnabled = MutableStateFlow(false)
    val repeatMode = MutableStateFlow(REPEAT_MODE_OFF)

    val canSkipPrevious = MutableStateFlow(true)
    val canSkipNext = MutableStateFlow(true)

    private val mutablePlaybackSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY)
    override val playbackSnapshot: StateFlow<PlaybackSnapshot> = mutablePlaybackSnapshot.asStateFlow()
    private val mutableQueueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
    override val queueSnapshot: StateFlow<PlaybackQueueSnapshot> = mutableQueueSnapshot.asStateFlow()

    val requestShowLyrics = MutableStateFlow(false)
    val requestShowQueue = MutableStateFlow(false)

    val error = MutableStateFlow<PlaybackException?>(null)
    val isMuted = service.isMuted

    val waitingForNetworkConnection = service.waitingForNetworkConnection
    
    // Callback to check if playback changes should be blocked (e.g., Listen Together guest)
    var shouldBlockPlaybackChanges: (() -> Boolean)? = null
    
    // Flag to allow internal sync operations to bypass blocking (set by ListenTogetherManager)
    @Volatile
    var allowInternalSync: Boolean = false

    var onSkipPrevious: (() -> Unit)? = null
    var onSkipNext: (() -> Unit)? = null

    private var attachedPlayer: Player? = null

    init {
        try {
            scope.launch {
                service.playerFlow.collect { newPlayer ->
                    if (newPlayer != null && newPlayer != attachedPlayer) {
                        updateAttachedPlayer(newPlayer)
                    }
                }
            }
            
            if (attachedPlayer == null && service.isPlayerReady.value) {
                 updateAttachedPlayer(player)
            }

            scope.launch {
                while (true) {
                    publishPlaybackSnapshot()
                    val castHandler = service.castConnectionHandler
                    val castIsPlaying = castHandler?.isCasting?.value == true &&
                        castHandler.castIsPlaying.value
                    delay(if (attachedPlayer?.isPlaying == true || castIsPlaying) 250L else 1_000L)
                }
            }

            Timber.tag(TAG).d("PlayerConnection flow observer registered")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to initialize PlayerConnection listener or state")
            // Propagate the error so MainActivity can retry
            throw e
        }
    }

    private fun updateAttachedPlayer(newPlayer: Player) {
        attachedPlayer?.removeListener(this)
        attachedPlayer = newPlayer
        newPlayer.addListener(this)
        
        playbackState.value = newPlayer.playbackState
        playWhenReady.value = newPlayer.playWhenReady
        mediaMetadata.value = newPlayer.currentMetadata
        queueTitle.value = service.queueTitle
        queueWindows.value = newPlayer.getQueueWindows()
        currentWindowIndex.value = newPlayer.getCurrentQueueIndex()
        currentMediaItemIndex.value = newPlayer.currentMediaItemIndex
        shuffleModeEnabled.value = newPlayer.shuffleModeEnabled
        repeatMode.value = newPlayer.repeatMode
        publishQueueSnapshot(newPlayer)
        updateCanSkipPreviousAndNext()
        publishPlaybackSnapshot()
        
        Timber.tag(TAG).d("Attached to new player instance: $newPlayer")
    }

    val playbackBlockedByRoom = MutableStateFlow(false)

    /** True while playback controls must be inert: in a room, without control. */
    val playbackLockedByRoom: Boolean
        get() = !allowInternalSync && shouldBlockPlaybackChanges?.invoke() == true

    /** Replacement awaiting the user's keep-or-clear choice. */
    val pendingQueue = MutableStateFlow<Queue?>(null)

    /** Previous queue available to the undo action. */
    val queueReplacedForUndo = MutableStateFlow<Queue?>(null)

    /** [force] bypasses the queue replacement prompt. */
    fun playQueue(queue: Queue, force: Boolean = false, playWhenReady: Boolean = true) {
        if (playbackLockedByRoom) {
            Timber.tag(TAG).d("playQueue blocked - Listen Together guest without control")
            playbackBlockedByRoom.value = true
            return
        }
        if (!playerReadinessFlow.value) {
            Timber.tag(TAG).w("playQueue called before player ready; delegating to service")
        }
        val isReplace = hasLiveQueue() && !startsInsideCurrentQueue(queue)
        if (!force && appContext.dataStore.get(AskBeforeReplacingQueueKey, false) && isReplace) {
            pendingQueue.value = queue
            return
        }
        // Forced replacements already reflect a deliberate choice.
        val previousQueue = if (!force && isReplace) snapshotCurrentQueue() else null
        try {
            service.playQueue(queue, playWhenReady = playWhenReady)
            previousQueue?.let { queueReplacedForUndo.value = it }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in playQueue")
            throw e
        }
    }

    /** Captures what is currently playing as a replayable [ListQueue], for undo. */
    private fun snapshotCurrentQueue(): Queue? = try {
        if (player.mediaItemCount == 0) null else ListQueue(
            items = (0 until player.mediaItemCount).map { player.getMediaItemAt(it) },
            startIndex = player.currentMediaItemIndex,
            position = player.currentPosition,
        )
    } catch (e: Exception) {
        Timber.tag(TAG).w(e, "snapshotCurrentQueue failed")
        null
    }

    /** Appends [queue] to what is already playing instead of replacing it. */
    fun enqueueQueue(queue: Queue) {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        service.enqueueQueue(queue)
    }

    private fun hasLiveQueue(): Boolean = try {
        playerReadinessFlow.value &&
            player.mediaItemCount > 0 &&
            player.playbackState != Player.STATE_IDLE
    } catch (e: Exception) {
        Timber.tag(TAG).w(e, "hasLiveQueue check failed")
        false
    }

    private fun startsInsideCurrentQueue(queue: Queue): Boolean = try {
        val startId = when (queue) {
            is ListQueue -> queue.items.getOrNull(queue.startIndex)?.mediaId
            else -> queue.preloadItem?.id
        }
        startId != null && (0 until player.mediaItemCount).any { player.getMediaItemAt(it).mediaId == startId }
    } catch (e: Exception) {
        Timber.tag(TAG).w(e, "startsInsideCurrentQueue check failed")
        false
    }

    fun startRadioSeamlessly() {
        if (shouldBlockPlaybackChanges?.invoke() == true) {
            Timber.tag("PlayerConnection").d("startRadioSeamlessly blocked - Listen Together guest")
            return
        }
        if (!playerReadinessFlow.value) {
            Timber.tag(TAG).w("startRadioSeamlessly called before player ready; delegating to service")
        }
        try {
            service.startRadioSeamlessly()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in startRadioSeamlessly")
            throw e
        }
    }

    fun playNext(item: MediaItem) = playNext(listOf(item))

    fun playNext(items: List<MediaItem>) {
        if (playbackLockedByRoom) {
            Timber.tag("PlayerConnection").d("playNext blocked - Listen Together guest")
            playbackBlockedByRoom.value = true
            return
        }
        try {
            service.playNext(items)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in playNext")
            throw e
        }
    }

    fun addToQueue(item: MediaItem) = addToQueue(listOf(item))

    fun addToQueue(items: List<MediaItem>) {
        if (playbackLockedByRoom) {
            Timber.tag("PlayerConnection").d("addToQueue blocked - Listen Together guest")
            playbackBlockedByRoom.value = true
            return
        }
        try {
            service.addToQueue(items)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in addToQueue")
            throw e
        }
    }

    fun toggleLike() {
        try {
            service.toggleLike()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in toggleLike")
        }
    }

    fun toggleMute() {
        service.toggleMute()
    }

    fun setMuted(muted: Boolean) {
        service.setMuted(muted)
    }

    fun toggleLibrary() {
        try {
            service.toggleLibrary()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in toggleLibrary")
        }
    }

    /**
     * Toggle play/pause - handles Cast when active
     */
    override fun togglePlayPause() {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                if (castHandler.castIsPlaying.value) {
                    castHandler.pause()
                } else {
                    castHandler.play()
                }
            } else {
                player.togglePlayPause()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in togglePlayPause")
        }
    }
    
    /**
     * Start playback - handles Cast when active
     */
    override fun play() {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                castHandler.play()
            } else {
                when (player.playbackState) {
                    Player.STATE_IDLE -> player.prepare()
                    Player.STATE_ENDED -> player.seekTo(player.currentMediaItemIndex, 0)
                    else -> {}
                }
                player.playWhenReady = true
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in play")
        }
    }
    
    /**
     * Pause playback - handles Cast when active
     */
    override fun pause() {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                castHandler.pause()
            } else {
                player.playWhenReady = false
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in pause")
        }
    }

    /**
     * Seek to position - handles Cast when active
     */
    override fun seekTo(positionMs: Long) {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                castHandler.seekTo(positionMs)
            } else {
                player.seekTo(positionMs)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in seekTo")
        }
    }

    override fun setRepeatMode(mode: PlaybackRepeatMode) {
        try {
            player.repeatMode = when (mode) {
                PlaybackRepeatMode.OFF -> Player.REPEAT_MODE_OFF
                PlaybackRepeatMode.ONE -> Player.REPEAT_MODE_ONE
                PlaybackRepeatMode.ALL -> Player.REPEAT_MODE_ALL
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in setRepeatMode")
        }
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        try {
            player.shuffleModeEnabled = enabled
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in setShuffleEnabled")
        }
    }

    override fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        try {
            val currentPlayer = player
            if (
                fromIndex !in 0 until currentPlayer.mediaItemCount ||
                toIndex !in 0 until currentPlayer.mediaItemCount ||
                fromIndex == toIndex
            ) return
            if (currentPlayer.shuffleModeEnabled) {
                val indices = currentPlayer.getQueueWindows().map { it.firstPeriodIndex }
                val movedIndices = moveQueueIndices(indices, fromIndex, toIndex)
                currentPlayer.setShuffleOrder(
                    DefaultShuffleOrder(movedIndices.toIntArray(), System.currentTimeMillis()),
                )
            } else {
                currentPlayer.moveMediaItem(fromIndex, toIndex)
            }
            publishQueueSnapshot(currentPlayer)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error moving queue item from $fromIndex to $toIndex")
        }
    }

    override fun removeQueueItem(index: Int) {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        try {
            val currentPlayer = player
            if (index in 0 until currentPlayer.mediaItemCount) {
                currentPlayer.removeMediaItem(index)
                publishQueueSnapshot(currentPlayer)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error removing queue item at $index")
        }
    }

    override fun removeQueueItemsAfterCurrent() {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        try {
            val currentPlayer = player
            val firstRemovedIndex = currentPlayer.currentMediaItemIndex + 1
            if (firstRemovedIndex < currentPlayer.mediaItemCount) {
                currentPlayer.removeMediaItems(firstRemovedIndex, currentPlayer.mediaItemCount)
            }
            publishQueueSnapshot(currentPlayer)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error removing queue items after the current item")
        }
    }

    override fun playQueueItem(index: Int) {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        try {
            val currentPlayer = player
            if (index !in 0 until currentPlayer.mediaItemCount) return
            currentPlayer.seekTo(index, 0L)
            if (currentPlayer.playbackState == Player.STATE_IDLE || currentPlayer.playbackState == Player.STATE_ENDED) {
                currentPlayer.prepare()
            }
            currentPlayer.playWhenReady = true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error playing queue item at $index")
        }
    }

    /** Restores a removed Android media item for the queue screen's undo action. */
    fun restoreQueueItem(item: MediaItem, targetIndex: Int) {
        if (playbackLockedByRoom) {
            playbackBlockedByRoom.value = true
            return
        }
        try {
            player.addMediaItem(item)
            val appendedIndex = player.mediaItemCount - 1
            moveQueueItem(appendedIndex, targetIndex.coerceIn(0, appendedIndex))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error restoring queue item at $targetIndex")
        }
    }

    private var pendingSkip = 0
    private var skipCommitJob: kotlinx.coroutines.Job? = null

    /** Coalesces rapid transport taps into one timeline jump. */
    override fun requestSkip(forward: Boolean) {
        pendingSkip += if (forward) 1 else -1
        skipCommitJob?.cancel()
        skipCommitJob = scope.launch {
            kotlinx.coroutines.delay(SKIP_COMMIT_DELAY_MS)
            val steps = pendingSkip
            pendingSkip = 0
            when {
                steps == 1 -> seekToNext()
                steps == -1 -> seekToPrevious()
                steps != 0 -> jumpBy(steps)
            }
        }
    }

    /** Several skips at once: walk the timeline (repeat/shuffle aware), one seek. Also the
     *  swipe-to-skip path: a swiped-in PREVIOUS card must land on the previous track even
     *  3s into this one, where [seekToPrevious] would restart instead. */
    fun jumpBy(steps: Int) {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                repeat(kotlin.math.abs(steps)) { if (steps > 0) castHandler.skipToNext() else castHandler.skipToPrevious() }
                return
            }
            val timeline = player.currentTimeline
            if (timeline.isEmpty) return
            // A multi-skip advances through tracks even in repeat-one mode.
            val mode = if (player.repeatMode == Player.REPEAT_MODE_ONE) Player.REPEAT_MODE_OFF else player.repeatMode
            var index = player.currentMediaItemIndex
            for (step in 0 until kotlin.math.abs(steps)) {
                val next = if (steps > 0) {
                    timeline.getNextWindowIndex(index, mode, player.shuffleModeEnabled)
                } else {
                    timeline.getPreviousWindowIndex(index, mode, player.shuffleModeEnabled)
                }
                if (next == androidx.media3.common.C.INDEX_UNSET) break
                index = next
            }
            if (index == player.currentMediaItemIndex) return
            player.seekTo(index, 0)
            if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                player.prepare()
            }
            player.playWhenReady = true
            if (steps > 0) onSkipNext?.invoke() else onSkipPrevious?.invoke()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in jumpBy($steps)")
        }
    }

    fun seekToNext() {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                castHandler.skipToNext()
                return
            }
            Timber.tag(TAG).d(
                "SKIP_DEBUG seekToNext() called: beforeIndex=${player.currentMediaItemIndex} " +
                    "beforeId=${player.currentMediaItem?.mediaId} repeatMode=${player.repeatMode} " +
                    "shuffle=${player.shuffleModeEnabled} hasNext=${player.hasNextMediaItem()} " +
                    "nextIndex=${player.nextMediaItemIndex}"
            )
            player.seekToNext()
            Timber.tag(TAG).d(
                "SKIP_DEBUG seekToNext() returned: afterIndex=${player.currentMediaItemIndex} " +
                    "afterId=${player.currentMediaItem?.mediaId}"
            )
            if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                player.prepare()
            }
            player.playWhenReady = true
            onSkipNext?.invoke()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in seekToNext")
        }
    }

    var onRestartSong: (() -> Unit)? = null

    fun seekToPrevious() {
        try {
            val castHandler = service.castConnectionHandler
            if (castHandler?.isCasting?.value == true) {
                castHandler.skipToPrevious()
                return
            }

            val restartCurrentItem = player.currentPosition > 3000 || !player.hasPreviousMediaItem()
            if (restartCurrentItem) {
                player.seekTo(0)
            } else {
                player.seekToPreviousMediaItem()
            }
            if (player.playbackState == Player.STATE_IDLE || player.playbackState == Player.STATE_ENDED) {
                player.prepare()
            }
            player.playWhenReady = true
            if (restartCurrentItem) onRestartSong?.invoke() else onSkipPrevious?.invoke()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in seekToPrevious")
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        playbackState.value = state
        error.value = player.playerError
        publishPlaybackSnapshot()
    }

    override fun onPlayWhenReadyChanged(
        newPlayWhenReady: Boolean,
        reason: Int,
    ) {
        playWhenReady.value = newPlayWhenReady
        publishPlaybackSnapshot()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        publishPlaybackSnapshot()
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int,
    ) {
        publishPlaybackSnapshot()
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        mediaMetadata.value = mediaItem?.metadata
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        publishQueueSnapshot(player)
        updateCanSkipPreviousAndNext()
    }

    override fun onTimelineChanged(
        timeline: Timeline,
        reason: Int,
    ) {
        queueWindows.value = player.getQueueWindows()
        queueTitle.value = service.queueTitle
        currentMediaItemIndex.value = player.currentMediaItemIndex
        currentWindowIndex.value = player.getCurrentQueueIndex()
        publishQueueSnapshot(player)
        updateCanSkipPreviousAndNext()
    }

    override fun onShuffleModeEnabledChanged(enabled: Boolean) {
        shuffleModeEnabled.value = enabled
        queueWindows.value = player.getQueueWindows()
        currentWindowIndex.value = player.getCurrentQueueIndex()
        updateCanSkipPreviousAndNext()
    }

    override fun onRepeatModeChanged(mode: Int) {
        repeatMode.value = mode
        updateCanSkipPreviousAndNext()
    }

    override fun onPlayerErrorChanged(playbackError: PlaybackException?) {
        if (playbackError != null) {
            reportException(playbackError)
        }
        error.value = playbackError
    }

    private fun updateCanSkipPreviousAndNext() {
        if (!player.currentTimeline.isEmpty) {
            val window =
                player.currentTimeline.getWindow(player.currentMediaItemIndex, Timeline.Window())
            canSkipPrevious.value = player.isCommandAvailable(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM) ||
                    !window.isLive ||
                    player.isCommandAvailable(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
            canSkipNext.value = window.isLive &&
                    window.isDynamic ||
                    player.isCommandAvailable(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
        } else {
            canSkipPrevious.value = false
            canSkipNext.value = false
        }
        publishPlaybackSnapshot()
    }

    private fun publishPlaybackSnapshot() {
        val currentPlayer = attachedPlayer ?: return
        val metadata = currentPlayer.currentMediaItem?.metadata
        val activeCastHandler = service.castConnectionHandler?.takeIf { it.isCasting.value }
        val castIsPlaying = activeCastHandler?.castIsPlaying?.value == true
        val castIsBuffering = activeCastHandler?.castIsBuffering?.value == true
        val playerDuration = currentPlayer.duration.takeIf { it >= 0L }
        val metadataDuration = metadata?.duration
            ?.takeIf { it >= 0 }
            ?.toLong()
            ?.times(1_000L)
        val castDuration = activeCastHandler?.castDuration?.value?.takeIf { it > 0L }
        val durationMs = if (activeCastHandler != null) {
            castDuration ?: metadataDuration
        } else {
            playerDuration ?: metadataDuration
        }

        mutablePlaybackSnapshot.value = PlaybackSnapshot(
            track = metadata?.let {
                PlaybackTrackSnapshot(
                    id = it.id,
                    title = it.title,
                    artists = it.artists.map { artist -> artist.name },
                    artworkUrl = it.thumbnailUrl,
                )
            },
            phase = when {
                castIsBuffering -> PlaybackPhase.BUFFERING
                currentPlayer.playbackState == Player.STATE_BUFFERING -> PlaybackPhase.BUFFERING
                currentPlayer.playbackState == Player.STATE_READY -> PlaybackPhase.READY
                currentPlayer.playbackState == Player.STATE_ENDED -> PlaybackPhase.ENDED
                else -> PlaybackPhase.IDLE
            },
            playWhenReady = if (activeCastHandler != null) castIsPlaying else currentPlayer.playWhenReady,
            isPlaying = if (activeCastHandler != null) castIsPlaying && !castIsBuffering else currentPlayer.isPlaying,
            positionMs = if (activeCastHandler != null) {
                activeCastHandler.castPosition.value.coerceAtLeast(0L)
            } else {
                currentPlayer.currentPosition.coerceAtLeast(0L)
            },
            durationMs = durationMs,
            mediaItemIndex = currentPlayer.currentMediaItemIndex,
            mediaItemCount = currentPlayer.mediaItemCount,
            repeatMode = when (currentPlayer.repeatMode) {
                Player.REPEAT_MODE_ONE -> PlaybackRepeatMode.ONE
                Player.REPEAT_MODE_ALL -> PlaybackRepeatMode.ALL
                else -> PlaybackRepeatMode.OFF
            },
            shuffleEnabled = currentPlayer.shuffleModeEnabled,
            canSkipPrevious = canSkipPrevious.value,
            canSkipNext = canSkipNext.value,
        )
    }

    private fun publishQueueSnapshot(currentPlayer: Player) {
        mutableQueueSnapshot.value = PlaybackQueueSnapshot(
            entries = (0 until currentPlayer.mediaItemCount).map { index ->
                val item = currentPlayer.getMediaItemAt(index)
                val metadata = item.mediaMetadata
                PlaybackQueueEntry(
                    id = item.mediaId,
                    title = metadata.title?.toString()?.takeIf(String::isNotBlank) ?: item.mediaId,
                    artists = listOfNotNull(metadata.artist?.toString()?.takeIf(String::isNotBlank)),
                    artworkUrl = metadata.artworkUri?.toString(),
                )
            },
            currentIndex = currentPlayer.currentMediaItemIndex,
        )
    }

    fun dispose() {
        scope.cancel()
        skipCommitJob = null
        pendingSkip = 0
        try {
            attachedPlayer?.removeListener(this)
            attachedPlayer = null
            Timber.tag(TAG).d("PlayerConnection disposed successfully")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during PlayerConnection disposal")
        }
    }
}
