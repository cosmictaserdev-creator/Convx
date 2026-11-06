/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.listentogether

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.music.innertube.YouTube
import com.music.innertube.models.WatchEndpoint
import com.convx.music.constants.ListenTogetherSmartResyncKey
import com.convx.music.constants.ListenTogetherSyncVolumeKey
import com.convx.music.extensions.currentMetadata
import com.convx.music.extensions.metadata
import com.convx.music.extensions.toMediaItem
import com.convx.music.models.MediaMetadata
import com.convx.music.models.MediaMetadata.Album
import com.convx.music.models.MediaMetadata.Artist
import com.convx.music.models.toMediaMetadata
import com.convx.music.playback.PlayerConnection
import com.convx.music.playback.queues.YouTubeQueue
import com.convx.music.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/** Connects Listen Together room events to local playback and sync. */
@Singleton
class ListenTogetherManager @Inject constructor(
    private val client: ListenTogetherClient,
    @ApplicationContext private val context: Context,
    private val playerConnectionBridge: ListenTogetherPlayerConnectionBridge,
) {
    companion object {
        private const val TAG = "ListenTogetherManager"
        // Debounce syncs to avoid frequent guest seeks.
        private const val SYNC_DEBOUNCE_THRESHOLD_MS = 1000L
        // Paused there is nothing to hear, so the playhead can be near exact.
        private const val POSITION_TOLERANCE_MS = 400L
        // Above two decoders' normal jitter; a seek mid-song is audible, so only real drift earns one.
        private const val PLAYBACK_POSITION_TOLERANCE_MS = 1500L

        /** Least time between two state requests from a guest found on the wrong song. */
        private const val RESYNC_COOLDOWN_MS = 5000L

        /** How long to wait for the buffer-complete message before starting playback. */
        private const val BUFFER_COMPLETE_TIMEOUT_MS = 6000L
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    @Volatile
    private var initialized = false
    
    private var playerConnection: PlayerConnection? = null
    private var eventCollectorJob: Job? = null
    private var playerConnectionObserverJob: Job? = null
    private var queueObserverJob: Job? = null
    private var volumeObserverJob: Job? = null
    private var playerListenerRegistered = false

    private val syncHostVolumeEnabled = MutableStateFlow(true)
    private val smartResyncEnabled = MutableStateFlow(true)
    private var lastSyncedVolume: Float? = null
    private var previousMuteState: Boolean? = null
    private var muteForcedByPreference = false

    private var lastRole: RoomRole = RoomRole.NONE
    
    @Volatile
    private var isSyncing = false
    
    private var lastSyncedIsPlaying: Boolean? = null
    private var lastSyncedTrackId: String? = null
    
    private var lastSyncActionTime: Long = 0L
    
    private var bufferingTrackId: String? = null
    
    private var activeSyncJob: Job? = null
    
    // Reject async work started for an earlier track.
    private var currentTrackGeneration: Int = 0

    private var pendingSyncState: SyncStatePayload? = null

    /** The room's last known play state, with [SyncStatePayload.lastUpdate] in device time. */
    private var roomPlayback: SyncStatePayload? = null

    /**
     * This phone stopped on its own: a guest without the controls pausing, or anyone losing the
     * audio to a call or another app. Room PLAYs must not resume it.
     */
    private var guestPausedLocally = false

    /** The host's new song, held paused until the guests have loaded it. See [holdForGuests]. */
    private var hostHeldTrackId: String? = null
    private var hostHoldApplied = false
    private var hostHold: Job? = null

    private var bufferCompleteReceivedForTrack: String? = null

    /** Starts playback if the server's buffer-complete message never arrives. */
    private var bufferCompleteWatchdog: Job? = null

    val connectionState = client.connectionState
    val roomState = client.roomState
    val role = client.role
    val userId = client.userId
    val pendingJoinRequests = client.pendingJoinRequests
    val bufferingUsers = client.bufferingUsers
    val logs = client.logs
    val events = client.events
    val blockedUsernames = client.blockedUsernames
    val pendingSuggestions = client.pendingSuggestions
    val controlMode = client.controlMode
    val canControl = combine(client.canControl, playerConnectionBridge.partyLocked) { room, locked -> room && !locked }
    val roomExpiresAt = client.roomExpiresAt
    val roomExpiring = client.roomExpiring
    val extensionsLeft = client.extensionsLeft

    val isInRoom: Boolean get() = client.isInRoom
    val isHost: Boolean get() = client.isHost

    /** True when this device may not drive the player: a room guest, or a guest of a host-only party. */
    val isGuest: Boolean get() = (isInRoom && !isHost) || playerConnectionBridge.partyLocked.value
    val hasPersistedSession: Boolean get() = client.hasPersistedSession
    
    private val _chatMessages = MutableStateFlow<List<ChatMessagePayload>>(emptyList())
    val chatMessages = _chatMessages
    
    private val playerListener = object : Player.Listener {
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            try {
                if (isSyncing || !isInRoom) return
                // A call, another app taking the audio or unplugged headphones stops this phone only.
                val notPressed = !playWhenReady && reason != Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST
                if (!canControlPlayback || guestPausedLocally || notPressed) {
                    onGuestPlayPause(playWhenReady)
                    return
                }

                val connection = playerConnection ?: return
                val player = connection.player
                if (player.currentMetadata?.isLocalFile == true) return

                Timber.tag(TAG).d("Play state changed: $playWhenReady (reason: $reason)")

                if (hostHeldTrackId != null) {
                    // The hold's own pause, or whatever started the held song turning playback on.
                    if (!playWhenReady || !hostHoldApplied) return
                    // Pressed play mid-hold: start everyone now.
                    clearHostHold()
                }

                // Sync the track before sending its play state.
                val currentTrackId = player.currentMediaItem?.mediaId
                if (currentTrackId != null && currentTrackId != lastSyncedTrackId) {
                    Timber.tag(TAG)
                        .d("[SYNC] Sending track change before play state: track = $currentTrackId")
                    player.currentMetadata?.let { metadata ->
                        sendTrackChangeInternal(metadata)
                        lastSyncedTrackId = currentTrackId
                        // The server resets play state when the track changes.
                        lastSyncedIsPlaying = false
                    }
                    if (holdForGuests(currentTrackId)) return
                    // Send PLAY again because the server reset the play state.
                    if (playWhenReady) {
                        Timber.tag(TAG).d("[SYNC] Host is playing, sending PLAY after track change")
                        lastSyncedIsPlaying = true
                        val position = player.currentPosition
                        client.sendPlaybackAction(PlaybackActions.PLAY, position = position)
                    }
                    return
                }
                
                // Send play state only after the track is synced.
                sendPlayState(playWhenReady, player)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in onPlayWhenReadyChanged")
            }
        }
        
        private fun sendPlayState(playWhenReady: Boolean, player: Player) {
            try {
                val position = player.currentPosition
                
                if (playWhenReady) {
                    Timber.tag(TAG).d("Host sending PLAY at position $position")
                    client.sendPlaybackAction(PlaybackActions.PLAY, position = position)
                    lastSyncedIsPlaying = true
                } else if (!playWhenReady && (lastSyncedIsPlaying == true)) {
                    Timber.tag(TAG).d("Host sending PAUSE at position $position")
                    client.sendPlaybackAction(PlaybackActions.PAUSE, position = position)
                    lastSyncedIsPlaying = false
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in sendPlayState")
            }
        }
        
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            try {
                if (isSyncing || !canControlPlayback || !isInRoom) return
                if (mediaItem == null) return
                
                val connection = playerConnection ?: return
                val player = connection.player
                
                val trackId = mediaItem.mediaId
                if (trackId == lastSyncedTrackId) return
                // A file on this phone means nothing to anyone else; the room carries on without it.
                if (player.currentMetadata?.isLocalFile == true) return

                lastSyncedTrackId = trackId
                // The server resets play state when the track changes.
                lastSyncedIsPlaying = false
                
                player.currentMetadata?.let { metadata ->
                    Timber.tag(TAG).d("Host sending track change: ${metadata.title}")
                    sendTrackChange(metadata)
                    if (holdForGuests(trackId)) return

                    // Send PLAY again because the server reset the play state.
                    val isPlaying = player.playWhenReady
                    if (isPlaying) {
                        Timber.tag(TAG).d("Host is playing during track change, sending PLAY")
                        lastSyncedIsPlaying = true
                        val position = player.currentPosition
                        client.sendPlaybackAction(PlaybackActions.PLAY, position = position)
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in onMediaItemTransition")
            }
        }
        
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            try {
                if (isSyncing || !canControlPlayback || !isInRoom || guestPausedLocally) return
                if (playerConnection?.player?.currentMetadata?.isLocalFile == true) return

                // Ignore automatic position changes.
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    Timber.tag(TAG).d("Host sending SEEK to ${newPosition.positionMs}")
                    client.sendPlaybackAction(PlaybackActions.SEEK, position = newPosition.positionMs)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error in onPositionDiscontinuity")
            }
        }
    }

    /** Sets the player connection used for playback synchronization. */
    fun setPlayerConnection(connection: PlayerConnection?) {
        Timber.tag(TAG).d("setPlayerConnection: ${connection != null}, isInRoom: $isInRoom")
        
        try {
            val oldConnection = playerConnection
            if (playerListenerRegistered && oldConnection != null) {
                try {
                    oldConnection.player.removeListener(playerListener)
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error removing old player listener")
                }
                playerListenerRegistered = false
            }
            oldConnection?.shouldBlockPlaybackChanges = null
            oldConnection?.onSkipPrevious = null
            oldConnection?.onSkipNext = null
            oldConnection?.onRestartSong = null
            
            playerConnection = connection
            
            // Set up playback blocking for members who may not drive playback.
            connection?.shouldBlockPlaybackChanges = {
                // Gates on control mode, not isHost, so "everyone" mode lets members drive playback.
                (isInRoom && !canControlPlayback) || playerConnectionBridge.partyLocked.value
            }
            
            if (connection != null && isInRoom) {
                try {
                    connection.player.addListener(playerListener)
                    playerListenerRegistered = true
                    Timber.tag(TAG).d("Added player listener for room sync")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to add player listener")
                    playerListenerRegistered = false
                }
                
                connection.onSkipPrevious = {
                    try {
                        if (canControlPlayback && !isSyncing) {
                            Timber.tag(TAG).d("Host Skip Previous triggered")
                            client.sendPlaybackAction(PlaybackActions.SKIP_PREV)
                        }
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e, "Error in onSkipPrevious")
                    }
                }
                connection.onSkipNext = {
                try {
                        if (canControlPlayback && !isSyncing) {
                            Timber.tag(TAG).d("Host Skip Next triggered")
                            client.sendPlaybackAction(PlaybackActions.SKIP_NEXT)
                        }
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e, "Error in onSkipNext")
                    }
                }
                
                connection.onRestartSong = {
                    try {
                        if (canControlPlayback && !isSyncing) {
                            Timber.tag(TAG).d("Host Restart Song triggered (sending 1ms as 0ms workaround)")
                            client.sendPlaybackAction(PlaybackActions.SEEK, position = 1L)
                        }
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e, "Error in onRestartSong")
                    }
                }
            }

            // Start/stop queue observation based on role
            if (connection != null && isInRoom && isHost) {
                startQueueSyncObservation()
                startHeartbeat()
                startVolumeSyncObservation()
            } else {
                stopQueueSyncObservation()
                stopHeartbeat()
                stopVolumeSyncObservation()
            }
            restoreGuestMuteState()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error in setPlayerConnection")
        }
    }

    private fun observePreferences() {
        scope.launch {
            context.dataStore.data
                .map { it[ListenTogetherSyncVolumeKey] ?: true }
                .distinctUntilChanged()
                .collect { enabled ->
                    syncHostVolumeEnabled.value = enabled
                }

            context.dataStore.data
                .map { it[ListenTogetherSmartResyncKey] ?: true }
                .distinctUntilChanged()
                .collect { enabled ->
                    smartResyncEnabled.value = enabled
                }
        }
    }

    /** Starts collecting room events. */
    @Synchronized
    fun initialize() {
        if (initialized) return
        initialized = true
        Timber.tag(TAG).d("Initializing ListenTogetherManager")
        observePreferences()
        playerConnectionObserverJob?.cancel()
        playerConnectionObserverJob = scope.launch {
            playerConnectionBridge.playerConnection.collect(::setPlayerConnection)
        }
        eventCollectorJob?.cancel()
        eventCollectorJob = scope.launch {
            client.events.collect { event ->
                try {
                    Timber.tag(TAG).d("Received event: $event")
                    handleEvent(event)
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error handling event: $event")
                }
            }
        }
        
        scope.launch {
            role.collect { newRole ->
                try {
                    val previousRole = lastRole
                    lastRole = newRole

                    val wasHost = previousRole == RoomRole.HOST
                    if (newRole == RoomRole.HOST && !wasHost) {
                        val connection = playerConnection
                        if (connection != null) {
                            Timber.tag(TAG).d("Role changed to HOST, starting sync services")
                            startQueueSyncObservation()
                            startHeartbeat()
                            startVolumeSyncObservation()
                            if (!playerListenerRegistered) {
                                try {
                                    connection.player.addListener(playerListener)
                                    playerListenerRegistered = true
                                } catch (e: Exception) {
                                    Timber.tag(TAG).e(e, "Failed to add player listener on role change")
                                }
                            }
                        }
                    } else if (newRole != RoomRole.HOST && wasHost) {
                        Timber.tag(TAG).d("Role changed from HOST, stopping sync services")
                        stopQueueSyncObservation()
                        stopHeartbeat()
                        stopVolumeSyncObservation()
                    }
                    restoreGuestMuteState()
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error in role change handler")
                }
            }
        }
    }

    private fun handleEvent(event: ListenTogetherEvent) {
        when (event) {
            is ListenTogetherEvent.Connected -> {
                Timber.tag(TAG).d("Connected to server with userId: ${event.userId}")
            }
            
            is ListenTogetherEvent.RoomCreated -> handleRoomCreated(event)
            
            is ListenTogetherEvent.JoinApproved -> {
                Timber.tag(TAG).d("Join approved for room: ${event.roomCode}")
                // Save mute state before joining so it can be restored on leave
                saveMuteStateOnJoin()
                val state = event.state
                applyPlaybackState(
                    currentTrack = state.currentTrack,
                    isPlaying = state.isPlaying,
                    position = livePosition(state.position, state.isPlaying, state.lastUpdate),
                    queue = state.queue,
                    // The room is already mid-song, so no buffer-complete will come for this track.
                    bypassBuffer = true,
                )
                applyHostVolumeIfNeeded(event.state.volume)
                restoreGuestMuteState()
                // A guest needs the listener too: it tracks their own pauses.
                if (!playerListenerRegistered) {
                    playerConnection?.player?.let {
                        it.addListener(playerListener)
                        playerListenerRegistered = true
                    }
                }
            }
            
            is ListenTogetherEvent.PlaybackSync -> handlePlaybackSyncEvent(event)
            
            is ListenTogetherEvent.UserJoined -> handleUserJoined(event)

            is ListenTogetherEvent.BufferWait -> {
                Timber.tag(TAG).d("BufferWait: waiting for ${event.waitingFor.size} users")
            }
            
            is ListenTogetherEvent.BufferComplete -> {
                Timber.tag(TAG).d("BufferComplete for track: ${event.trackId}")
                // Whoever changed the song holds it, which in everyone mode can be a guest.
                releaseHostHold(event.trackId)
                if (!isHost && bufferingTrackId == event.trackId) {
                    bufferCompleteReceivedForTrack = event.trackId
                    applyPendingSyncIfReady()
                }
            }
            
            is ListenTogetherEvent.SyncStateReceived -> {
                Timber.tag(TAG).d("SyncStateReceived: playing=${event.state.isPlaying}, pos=${event.state.position}, track=${event.state.currentTrack?.id}")
                if (!isHost) {
                    handleSyncState(event.state)
                }
            }
            
            is ListenTogetherEvent.Kicked -> {
                Timber.tag(TAG).d("Kicked from room: ${event.reason}")
                cleanup()
            }
            
            is ListenTogetherEvent.Disconnected -> {
                Timber.tag(TAG).d("Disconnected from server")
                // Keep room state during reconnect; intentional leave and kicks clean it up.
            }

            is ListenTogetherEvent.Reconnecting -> {
                Timber.tag(TAG).d("Reconnecting: attempt ${event.attempt}/${event.maxAttempts}")
            }
            
            is ListenTogetherEvent.Reconnected -> handleReconnected(event)
            
            is ListenTogetherEvent.UserReconnected -> {
                Timber.tag(TAG).d("User reconnected: ${event.username}")
                // Reconnect state already synced this user.
            }
            
            is ListenTogetherEvent.UserDisconnected -> {
                Timber.tag(TAG).d("User temporarily disconnected: ${event.username}")
                // Keep the session open during a temporary disconnect.
            }

            is ListenTogetherEvent.HostChanged -> handleHostChanged(event)
            
            is ListenTogetherEvent.JoinRequestReceived -> {
                Timber.tag(TAG).d("Join request received from ${event.username}")
                // UI already handles this via pendingJoinRequests flow
            }

            is ListenTogetherEvent.LocalSuggestionApproved -> {
                try {
                    val mediaMetadata = event.payload.trackInfo.toMediaMetadata()
                    val mediaItem = mediaMetadata.toMediaItem()
                    playerConnection?.playNext(mediaItem)
                    Timber.tag(TAG).d("Approved suggestion added to queue: ${mediaMetadata.title}")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error adding approved suggestion to queue")
                }
            }
            
            is ListenTogetherEvent.ConnectionError -> {
                Timber.tag(TAG).e("Connection error: ${event.error}")
                cleanup()
            }

            is ListenTogetherEvent.ChatMessageReceived -> {
                Timber.tag(TAG).d("Chat message received from ${event.payload.username}")
                _chatMessages.value = _chatMessages.value + event.payload
            }

            else -> { /* Other events handled by UI */ }
        }
    }
    
    private fun handleRoomCreated(event: ListenTogetherEvent.RoomCreated) {
        Timber.tag(TAG).d("Room created: ${event.roomCode}")
        try {
            val connection = playerConnection
            val player = connection?.player
            if (player != null && !playerListenerRegistered) {
                try {
                    player.addListener(playerListener)
                    playerListenerRegistered = true
                    Timber.tag(TAG).d("Added player listener as host")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to add player listener on room create")
                }
            }
            lastSyncedIsPlaying = player?.playWhenReady
            lastSyncedTrackId = player?.currentMediaItem?.mediaId

            // If there's already a track loaded, send it to the server
            player?.currentMetadata?.let { metadata ->
                Timber.tag(TAG).d("Room created with existing track: ${metadata.title}")
                sendTrackChangeInternal(metadata)
                val isPlaying = player.playWhenReady
                if (isPlaying) {
                    lastSyncedIsPlaying = true
                    val position = player.currentPosition
                    Timber.tag(TAG).d("Host already playing on room create, sending PLAY at $position")
                    client.sendPlaybackAction(PlaybackActions.PLAY, position = position)
                }
            }
            startQueueSyncObservation()
            startHeartbeat()
            startVolumeSyncObservation()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error handling RoomCreated event")
        }
    }

    private fun handlePlaybackSyncEvent(event: ListenTogetherEvent.PlaybackSync) {
        Timber.tag(TAG).d("PlaybackSync received: ${event.action.action}")
        val actionType = event.action.action
        val isQueueOperation = actionType == PlaybackActions.QUEUE_ADD ||
            actionType == PlaybackActions.QUEUE_REMOVE ||
            actionType == PlaybackActions.QUEUE_CLEAR
        val sender = event.action.fromUserId
        val shouldApply = if (sender != null) {
            sender != userId.value
        } else {
            // Older servers do not identify the sender; preserve the host echo rule.
            !isHost
        }

        // Queue operations must be applied even when echoed back to the sender.
        if (shouldApply || isQueueOperation) {
            handlePlaybackSync(event.action)
        } else {
            noteOwnEcho(event.action)
        }
    }

    /**
     * Our own PLAY, PAUSE or SEEK coming back from the server still moves the room, so a
     * controller that drops out (a call) knows where to rejoin. See [onGuestPlayPause].
     */
    private fun noteOwnEcho(action: PlaybackActionPayload) {
        val position = action.position ?: return
        val now = System.currentTimeMillis()
        val serverTime = action.serverTime ?: 0L
        when (action.action) {
            PlaybackActions.PLAY -> noteRoomPlayback(true, livePosition(position, true, serverTime), now)
            PlaybackActions.PAUSE -> noteRoomPlayback(false, position, now)
            PlaybackActions.SEEK -> roomPlayback?.let {
                noteRoomPlayback(it.isPlaying, livePosition(position, it.isPlaying, serverTime), now)
            }
        }
    }

    private fun handleUserJoined(event: ListenTogetherEvent.UserJoined) {
        Timber.tag(TAG).d("[SYNC] User joined: ${event.username}")
        if (isHost) {
            try {
                val connection = playerConnection
                val player = connection?.player
                // The join already carried the song and queue. Re-announcing it would reset the
                // room's position to 0:00 and make every guest report the song loaded again.
                // A fresh PLAY only sharpens the new guest's position.
                if (player != null && player.playWhenReady && player.currentMetadata?.isLocalFile == false) {
                    val pos = player.currentPosition
                    Timber.tag(TAG).d("[SYNC] Host playing, sending PLAY at $pos for new joiner")
                    client.sendPlaybackAction(PlaybackActions.PLAY, position = pos)
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error handling UserJoined event")
            }
        }
    }

    private fun handleReconnected(event: ListenTogetherEvent.Reconnected) {
        Timber.tag(TAG).d("Reconnected to room: ${event.roomCode}, isHost: ${event.isHost}")
        try {
            val connection = playerConnection
            val player = connection?.player
            if (player != null && !playerListenerRegistered) {
                try {
                    player.addListener(playerListener)
                    playerListenerRegistered = true
                    Timber.tag(TAG).d("Re-added player listener after reconnect")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to re-add player listener after reconnect")
                }
            }

            if (event.isHost) {
                lastSyncedIsPlaying = player?.playWhenReady
                lastSyncedTrackId = player?.currentMediaItem?.mediaId

                val currentMetadata = player?.currentMetadata
                if (currentMetadata != null) {
                    val serverTrackId = event.state.currentTrack?.id
                    if (serverTrackId != currentMetadata.id) {
                        Timber.tag(TAG).d("Reconnected as host, server track ($serverTrackId) differs from local (${currentMetadata.id}), syncing")
                        sendTrackChangeInternal(currentMetadata)
                    } else {
                        Timber.tag(TAG).d("Reconnected as host, server already has current track $serverTrackId")
                    }

                    // Small delay before sending play state to let connection stabilize
                    scope.launch {
                        delay(500)
                        try {
                            val currentPlayer = playerConnection?.player
                            if (currentPlayer?.playWhenReady == true) {
                                val pos = currentPlayer.currentPosition
                                Timber.tag(TAG)
                                    .d("Reconnected host is playing, sending PLAY at $pos")
                                client.sendPlaybackAction(PlaybackActions.PLAY, position = pos)
                            }
                        } catch (e: Exception) {
                            Timber.tag(TAG).e(e, "Error sending play state after reconnect")
                        }
                    }
                }
            } else {
                // Reapply host state after reconnect, even if the local state looks current.
                Timber.tag(TAG).d("Reconnected as guest, syncing to host's current state")
                val state = event.state
                applyPlaybackState(
                    currentTrack = state.currentTrack,
                    isPlaying = state.isPlaying,
                    position = livePosition(state.position, state.isPlaying, state.lastUpdate),
                    queue = state.queue,
                    bypassBuffer = true  // Reconnect: bypass buffer protocol
                )
                applyHostVolumeIfNeeded(event.state.volume)

                scope.launch {
                    delay(1000)
                    if (isInRoom && !isHost && smartResyncEnabled.value) {
                        Timber.tag(TAG).d("Requesting fresh sync after reconnect (Smart Resync)")
                        requestSync()
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error handling Reconnected event")
        }
    }

    private fun handleHostChanged(event: ListenTogetherEvent.HostChanged) {
        Timber.tag(TAG).d("Host changed: new host is ${event.newHostName} (${event.newHostId})")
        val wasHost = isHost
        val nowIsHost = event.newHostId == userId.value

        if (wasHost && !nowIsHost) {
            // Lost host role
            Timber.tag(TAG).d("Local user lost host role")
            stopQueueSyncObservation()
            stopVolumeSyncObservation()
            clearHostHold()
            // Restore guest mute state since we're now a guest
            restoreGuestMuteState()
        } else if (!wasHost && nowIsHost) {
            // Gained host role
            Timber.tag(TAG).d("Local user gained host role")
            guestPausedLocally = false
            restoreGuestMuteState()

            val connection = playerConnection
            val player = connection?.player
            if (player != null && !playerListenerRegistered) {
                try {
                    player.addListener(playerListener)
                    playerListenerRegistered = true
                    Timber.tag(TAG).d("Added player listener as new host")
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to add player listener on host transfer")
                }
            }

            startQueueSyncObservation()
            startVolumeSyncObservation()

            val metadata = player?.currentMetadata
            if (metadata != null) {
                Timber.tag(TAG).d("New host sending current track: ${metadata.title}")
                sendTrackChangeInternal(metadata)

                if (player.playWhenReady) {
                    val position = player.currentPosition
                    Timber.tag(TAG).d("New host is playing, sending PLAY at $position")
                    client.sendPlaybackAction(PlaybackActions.PLAY, position = position)
                }
            }
        }
    }

    private fun cleanup() {
        if (lastRole == RoomRole.GUEST) {
            restoreGuestMuteState()
        }
        if (playerListenerRegistered) {
            playerConnection?.player?.removeListener(playerListener)
            playerListenerRegistered = false
        }
        stopQueueSyncObservation()
        stopHeartbeat()
        stopVolumeSyncObservation()
        bufferCompleteWatchdog?.cancel()
        bufferCompleteWatchdog = null
        // Keep this callback; it checks isInRoom dynamically.
        lastSyncedIsPlaying = null
        lastSyncedTrackId = null
        bufferingTrackId = null
        pendingSyncState = null
        roomPlayback = null
        guestPausedLocally = false
        clearHostHold()
        isSyncing = false
        bufferCompleteReceivedForTrack = null
        lastRole = RoomRole.NONE
        lastSyncActionTime = 0L
        ++currentTrackGeneration  // Invalidates pending track-change coroutines
        _chatMessages.value = emptyList()
    }

    /** Saves the mute state before Listen Together changes it. */
    private fun saveMuteStateOnJoin() {
        val connection = playerConnection ?: return
        // Only save if we haven't already saved (avoid overwriting on role changes)
        if (previousMuteState == null) {
            previousMuteState = connection.isMuted.value
            Timber.tag(TAG).d("Saved mute state on join: ${previousMuteState}")
        }
    }

    /** Restores the saved mute state when the user no longer needs guest muting. */
    private fun restoreGuestMuteState() {
        val connection = playerConnection ?: return
        val savedState = previousMuteState
        
        if (savedState != null) {
            Timber.tag(TAG).d("Restoring mute state on leave: was muted=$savedState, currently muted=${connection.isMuted.value}")
            connection.setMuted(savedState)
        } else {
            // If no prior state was saved, clear any forced mute.
            if (connection.isMuted.value) {
                Timber.tag(TAG).d("No saved mute state on leave, unmuting player as fallback")
                connection.setMuted(false)
            }
        }
        
        previousMuteState = null
        muteForcedByPreference = false
    }

    private fun applyHostVolumeIfNeeded(volume: Float?) {
        if (!syncHostVolumeEnabled.value || isHost || !isInRoom) return
        val connection = playerConnection ?: return
        val target = volume?.coerceIn(0f, 1f) ?: return
        // A host that is muted or turned right down must not silence guests for good: this value is
        // saved and the system volume cannot raise it again.
        if (target < 0.05f) return
        connection.service.playerVolume.value = target
    }

    /** Where a room that was at [position] at server time [lastUpdate] is now. */
    private fun livePosition(position: Long, isPlaying: Boolean, lastUpdate: Long): Long =
        if (isPlaying && lastUpdate > 0) {
            position + client.serverClock.elapsedSince(lastUpdate, System.currentTimeMillis())
        } else {
            position
        }

    /**
     * Stores the state to apply once [track] has loaded. A PLAY or PAUSE that already arrived for
     * the same track while it was loading is newer than the load request, so it is kept.
     *
     * @param anchoredAt device time at which [position] was true.
     */
    private fun stagePendingSync(track: TrackInfo, isPlaying: Boolean, position: Long, anchoredAt: Long) {
        val existing = pendingSyncState
        if (existing?.currentTrack?.id == track.id && existing.lastUpdate >= anchoredAt) return
        pendingSyncState = SyncStatePayload(
            currentTrack = track,
            isPlaying = isPlaying,
            position = position,
            lastUpdate = anchoredAt,
        )
    }

    /** Starts playback anyway if the server's buffer-complete for [trackId] never arrives. */
    private fun armBufferCompleteWatchdog(trackId: String, generation: Int) {
        bufferCompleteWatchdog?.cancel()
        bufferCompleteWatchdog = scope.launch {
            delay(BUFFER_COMPLETE_TIMEOUT_MS)
            if (currentTrackGeneration != generation) return@launch
            if (pendingSyncState == null) return@launch
            if (bufferCompleteReceivedForTrack == trackId) return@launch
            Timber.tag(TAG).w("Buffer-complete never arrived for $trackId after ${BUFFER_COMPLETE_TIMEOUT_MS}ms — starting anyway")
            applyPendingSyncIfReady(force = true)
        }
    }

    private var lastResyncRequestAt = 0L

    /**
     * Room updates carry a position but not the song, so a guest that missed a song change would
     * stay on the wrong one until the next. Asks the server for the full state instead.
     */
    private fun resyncIfOnWrongTrack(player: Player, now: Long): Boolean {
        val roomTrackId = roomState.value?.currentTrack?.id ?: return false
        if (player.currentMediaItem?.mediaId == roomTrackId) return false
        if (now - lastResyncRequestAt >= RESYNC_COOLDOWN_MS) {
            lastResyncRequestAt = now
            Timber.tag(TAG).w("Guest is not on the room's song $roomTrackId, requesting state")
            requestSync()
        }
        return true
    }

    /** Remembers what the room is doing, for a guest who resumes later. */
    private fun noteRoomPlayback(isPlaying: Boolean, position: Long, at: Long) {
        lastSyncedIsPlaying = isPlaying
        roomPlayback = SyncStatePayload(
            currentTrack = roomState.value?.currentTrack,
            isPlaying = isPlaying,
            position = position,
            lastUpdate = at,
        )
    }

    /**
     * A pause that belongs to this phone alone stays on it; everyone else keeps listening.
     * Pressing play again rejoins the room where it is now, or stays paused if the room is
     * paused, and sends nothing: the gap is this phone's to close, not the room's.
     */
    private fun onGuestPlayPause(playWhenReady: Boolean) {
        if (!playWhenReady) {
            // A song still loading is paused by the sync itself, not by the guest.
            if (bufferingTrackId != null) return
            Timber.tag(TAG).d("Guest paused locally")
            guestPausedLocally = true
            return
        }
        Timber.tag(TAG).d("Guest resumed, rejoining the room")
        guestPausedLocally = false
        // A track still loading applies the room's state once it is ready.
        if (bufferingTrackId != null) return
        pendingSyncState = roomPlayback ?: return
        applyPendingSyncIfReady(force = true)
    }

    /**
     * Pauses the host on a new song until every guest has loaded it, so the room starts
     * together. Released by the server's buffer-complete or after [BUFFER_COMPLETE_TIMEOUT_MS];
     * the release plays, and the play-state listener sends that PLAY to the room.
     *
     * @return false when nobody else is in the room, so the caller starts as before.
     */
    private fun holdForGuests(trackId: String): Boolean {
        if ((roomState.value?.users?.size ?: 0) < 2) return false
        hostHold?.cancel()
        hostHeldTrackId = trackId
        hostHoldApplied = false
        hostHold = scope.launch {
            // Posted: whatever started this song may still be turning playback on.
            val connection = playerConnection
            if (connection == null || !connection.player.playWhenReady) {
                clearHostHold()
                return@launch
            }
            Timber.tag(TAG).d("Host holding $trackId until guests have loaded it")
            connection.pause()
            hostHoldApplied = true
            delay(BUFFER_COMPLETE_TIMEOUT_MS)
            releaseHostHold(trackId)
        }
        return true
    }

    private fun releaseHostHold(trackId: String) {
        if (hostHeldTrackId != trackId) return
        val applied = hostHoldApplied
        clearHostHold()
        val connection = playerConnection ?: return
        if (applied) {
            connection.play()
        } else if (connection.player.playWhenReady) {
            // Guests were ready before the hold paused anything; the PLAY it swallowed goes out now.
            lastSyncedIsPlaying = true
            client.sendPlaybackAction(PlaybackActions.PLAY, position = connection.player.currentPosition)
        }
    }

    private fun clearHostHold() {
        hostHold?.cancel()
        hostHold = null
        hostHeldTrackId = null
        hostHoldApplied = false
    }

    /** @param force applies the pending sync without waiting for its buffer-complete. */
    private fun applyPendingSyncIfReady(force: Boolean = false) {
        val pending = pendingSyncState ?: return
        val pendingTrackId = pending.currentTrack?.id ?: bufferingTrackId ?: return
        val completedTrackId = bufferCompleteReceivedForTrack

        if (!force && completedTrackId != pendingTrackId) return
        bufferCompleteWatchdog?.cancel()
        bufferCompleteWatchdog = null

        val connection = playerConnection ?: return
        val player = connection.player

        Timber.tag(TAG).d("Applying pending sync: track=$pendingTrackId, pos=${pending.position}, play=${pending.isPlaying}")
        isSyncing = true
        noteRoomPlayback(pending.isPlaying, pending.position, pending.lastUpdate)

        val shouldPlay = pending.isPlaying && !guestPausedLocally
        // The room kept playing while this track loaded.
        val targetPositionMs = if (shouldPlay) {
            pending.position + kotlin.math.max(0L, System.currentTimeMillis() - pending.lastUpdate)
        } else {
            pending.position
        }
        val positionDifferenceMs = kotlin.math.abs(player.currentPosition - targetPositionMs)
        val seekToleranceMs = if (shouldPlay && player.playWhenReady) {
            PLAYBACK_POSITION_TOLERANCE_MS
        } else {
            POSITION_TOLERANCE_MS
        }

        if (positionDifferenceMs > seekToleranceMs) {
            Timber.tag(TAG).d(
                "Applying pending sync: seeking ${player.currentPosition} -> $targetPositionMs " +
                    "(diff ${positionDifferenceMs}ms > ${seekToleranceMs}ms)"
            )
            connection.seekTo(targetPositionMs)
        } else {
            Timber.tag(TAG).d(
                "Applying pending sync: skipping seek " +
                    "(diff ${positionDifferenceMs}ms < ${seekToleranceMs}ms)"
            )
        }

        if (shouldPlay && !player.playWhenReady) {
            Timber.tag(TAG).d("Applying pending sync: starting playback")
            connection.play()
        } else if (!shouldPlay && player.playWhenReady) {
            Timber.tag(TAG).d("Applying pending sync: pausing playback")
            connection.pause()
        }

        scope.launch {
            delay(200)
            isSyncing = false
        }

        bufferingTrackId = null
        pendingSyncState = null
        bufferCompleteReceivedForTrack = null
    }

    private fun handlePlaybackSync(action: PlaybackActionPayload) {
        // Surfaced in Settings -> Listen Together -> logs. Without the song name
        // a desync reads as "it is broken"; with it you can see whether the two
        // devices disagree about the track or only about the position.
        val who = action.fromUserId?.let { id ->
            roomState.value?.users?.firstOrNull { it.userId == id }?.username ?: id.take(6)
        } ?: "host"
        val what = action.trackInfo?.let { "${it.title} — ${it.artist}" }
        client.logExternal(
            "Sync in: ${action.action}",
            listOfNotNull(
                "from $who",
                what?.let { "track: $it" },
                action.position?.let { "pos ${it / 1000}s" }
            ).joinToString(", ")
        )
        val connection = playerConnection
        if (connection == null) {
            Timber.tag(TAG).w("Cannot sync playback - no player connection")
            return
        }
        val player = connection.player
        
        Timber.tag(TAG).d("Handling playback sync: ${action.action}, position: ${action.position}")

        isSyncing = true

        try {
            when (action.action) {
                PlaybackActions.PLAY -> handlePlayAction(action, connection, player)
                PlaybackActions.PAUSE -> handlePauseAction(action, connection, player)
                PlaybackActions.SEEK -> handleSeekAction(action, connection, player)
                PlaybackActions.CHANGE_TRACK -> handleTrackChangeAction(action)
                
                PlaybackActions.SKIP_NEXT -> {
                    Timber.tag(TAG).d("Guest: SKIP_NEXT")
                    connection.seekToNext()
                }

                PlaybackActions.SKIP_PREV -> {
                    Timber.tag(TAG).d("Guest: SKIP_PREV")
                    connection.seekToPrevious()
                }

                PlaybackActions.QUEUE_ADD -> {
                    addQueueTrack(action, connection)
                }

                PlaybackActions.QUEUE_REMOVE -> removeQueueTrack(action.trackId, player)
                PlaybackActions.QUEUE_CLEAR -> clearQueueAfterCurrentTrack(player)

                PlaybackActions.SET_VOLUME -> {
                    applyHostVolumeIfNeeded(action.volume)
                }

                PlaybackActions.SYNC_QUEUE -> syncQueue(action, connection)
            }
        } finally {
            // Minimal delay to prevent feedback loops
            scope.launch {
                delay(200)
                isSyncing = false
            }
        }
    }

    private fun handlePlayAction(
        action: PlaybackActionPayload,
        connection: PlayerConnection,
        player: Player,
    ) {
        val basePosition = action.position ?: 0L
        val now = System.currentTimeMillis()
        val adjustedPosition = action.serverTime?.let { serverTime ->
            basePosition + client.serverClock.elapsedSince(serverTime, now)
        } ?: basePosition

        Timber.tag(TAG).d("Guest: PLAY at position $adjustedPosition, currently playing=${player.playWhenReady}")

        if (bufferingTrackId != null) {
            deferSyncUntilTrackReady(isPlaying = true, position = adjustedPosition, now = now)
            return
        }
        noteRoomPlayback(isPlaying = true, position = adjustedPosition, at = now)
        if (resyncIfOnWrongTrack(player, now)) return
        if (guestPausedLocally) return

        val positionDifference = kotlin.math.abs(player.currentPosition - adjustedPosition)
        val alreadyPlaying = player.playWhenReady
        val isDuplicatePlay = alreadyPlaying &&
            positionDifference < POSITION_TOLERANCE_MS &&
            now - lastSyncActionTime < SYNC_DEBOUNCE_THRESHOLD_MS
        if (isDuplicatePlay) {
            Timber.tag(TAG).d("Guest: PLAY debounced - already playing and in sync (diff ${positionDifference}ms)")
            return
        }

        if (alreadyPlaying) {
            // Avoid interrupting active playback for small position drift.
            if (positionDifference > PLAYBACK_POSITION_TOLERANCE_MS) {
                Timber.tag(TAG).d("Guest: PLAY seeking during playback ${player.currentPosition} -> $adjustedPosition (diff ${positionDifference}ms)")
                connection.seekTo(adjustedPosition)
            } else {
                Timber.tag(TAG).d("Guest: PLAY skipping seek - already playing, drift acceptable (${positionDifference}ms < ${PLAYBACK_POSITION_TOLERANCE_MS}ms)")
            }
        } else {
            // When paused, a smaller drift is safe to correct before playback starts.
            if (positionDifference > POSITION_TOLERANCE_MS) {
                Timber.tag(TAG).d("Guest: PLAY seeking while paused ${player.currentPosition} -> $adjustedPosition (diff ${positionDifference}ms)")
                connection.seekTo(adjustedPosition)
            }
            Timber.tag(TAG).d("Guest: Starting playback")
            connection.play()
        }
        lastSyncActionTime = now
    }

    private fun handlePauseAction(
        action: PlaybackActionPayload,
        connection: PlayerConnection,
        player: Player,
    ) {
        val position = action.position ?: 0L
        val now = System.currentTimeMillis()
        Timber.tag(TAG).d("Guest: PAUSE at position $position, currently playing=${player.playWhenReady}")

        if (bufferingTrackId != null) {
            deferSyncUntilTrackReady(isPlaying = false, position = position, now = now)
            return
        }
        noteRoomPlayback(isPlaying = false, position = position, at = now)

        val positionDifference = kotlin.math.abs(player.currentPosition - position)
        val alreadyPaused = !player.playWhenReady
        val isDuplicatePause = alreadyPaused &&
            positionDifference < POSITION_TOLERANCE_MS &&
            now - lastSyncActionTime < SYNC_DEBOUNCE_THRESHOLD_MS
        if (isDuplicatePause) {
            Timber.tag(TAG).d("Guest: PAUSE debounced - already paused and in sync (diff ${positionDifference}ms)")
            return
        }

        if (player.playWhenReady) {
            Timber.tag(TAG).d("Guest: Pausing playback")
            connection.pause()
        }

        if (positionDifference > POSITION_TOLERANCE_MS) {
            Timber.tag(TAG).d("Guest: PAUSE seeking ${player.currentPosition} -> $position (diff ${positionDifference}ms)")
            connection.seekTo(position)
        } else {
            Timber.tag(TAG).d("Guest: PAUSE skipping seek (diff ${positionDifference}ms < ${POSITION_TOLERANCE_MS}ms)")
        }
        lastSyncActionTime = now
    }

    private fun handleSeekAction(
        action: PlaybackActionPayload,
        connection: PlayerConnection,
        player: Player,
    ) {
        val now = System.currentTimeMillis()
        val isPlaying = roomPlayback?.isPlaying ?: player.playWhenReady
        // A playing room moved on while the seek was in flight.
        val position = livePosition(action.position ?: 0L, isPlaying, action.serverTime ?: 0L)
        if (bufferingTrackId != null) {
            deferSyncUntilTrackReady(isPlaying = isPlaying, position = position, now = now)
            return
        }
        noteRoomPlayback(isPlaying, position, now)
        if (guestPausedLocally) return
        // No time debounce: a seek is always deliberate, and one made just after a play was being lost.
        val positionDifference = kotlin.math.abs(player.currentPosition - position)
        if (positionDifference > POSITION_TOLERANCE_MS) {
            Timber.tag(TAG).d("Guest: SEEK to $position from ${player.currentPosition} (diff > ${POSITION_TOLERANCE_MS}ms)")
            connection.seekTo(position)
            lastSyncActionTime = now
        } else {
            Timber.tag(TAG).d("Guest: SEEK ignored (position diff < ${POSITION_TOLERANCE_MS}ms)")
        }
    }

    private fun handleTrackChangeAction(action: PlaybackActionPayload) {
        val track = action.trackInfo ?: return
        Timber.tag(TAG).d("Guest: CHANGE_TRACK to ${track.title}, queue size=${action.queue?.size}")
        // A track change starts a new sync cycle.
        lastSyncActionTime = 0L

        // The host re-announces its song when someone joins; reloading it would restart every guest.
        val loadedTrackId = bufferingTrackId ?: playerConnection?.player?.currentMediaItem?.mediaId
        if (track.id == loadedTrackId) {
            Timber.tag(TAG).d("Guest: CHANGE_TRACK to the loaded track, keeping playback")
            // The server still waits for every guest to report this track before it lets the room start.
            client.sendBufferReady(track.id)
            return
        }

        val queue = action.queue
        if (!queue.isNullOrEmpty()) {
            applyPlaybackState(
                currentTrack = track,
                // A later PLAY event or pending sync sets the actual play state.
                isPlaying = false,
                position = 0,
                queue = queue,
                queueTitle = action.queueTitle,
            )
        } else {
            // Without a queue, resolve the track through the existing network path.
            bufferingTrackId = track.id
            lastSyncedTrackId = track.id
            syncToTrack(track, false, 0)
        }
    }

    private fun removeQueueTrack(trackId: String?, player: Player) {
        if (trackId.isNullOrEmpty()) {
            Timber.tag(TAG).w("QUEUE_REMOVE missing trackId")
            return
        }

        val firstQueueIndex = player.currentMediaItemIndex + 1
        val removeIndex = (firstQueueIndex until player.mediaItemCount)
            .firstOrNull { player.getMediaItemAt(it).mediaId == trackId }

        if (removeIndex == null) {
            Timber.tag(TAG).w("QUEUE_REMOVE id not found in queue: $trackId")
            return
        }

        Timber.tag(TAG).d("Guest: QUEUE_REMOVE index=$removeIndex id=$trackId")
        player.removeMediaItem(removeIndex)
    }

    private fun clearQueueAfterCurrentTrack(player: Player) {
        val firstQueueIndex = player.currentMediaItemIndex + 1
        val itemCount = player.mediaItemCount
        val itemsAfterCurrent = itemCount - firstQueueIndex
        if (itemsAfterCurrent <= 0) return

        Timber.tag(TAG).d("Guest: QUEUE_CLEAR removing $itemsAfterCurrent items after current")
        player.removeMediaItems(firstQueueIndex, itemCount - firstQueueIndex)
    }

    private fun syncQueue(action: PlaybackActionPayload, connection: PlayerConnection) {
        val queue = action.queue ?: return
        Timber.tag(TAG).d("Guest: SYNC_QUEUE size=${queue.size}")

        // Replace pending smart sync with the server's authoritative queue.
        activeSyncJob?.cancel()

        scope.launch(Dispatchers.Main) {
            if (playerConnection !== connection) return@launch
            val player = connection.player
            val ids = queue.map { it.id }
            val newIndex = player.currentMediaItem?.mediaId?.let(ids::indexOf) ?: -1
            if (newIndex < 0) {
                // The room's queue no longer holds what is playing here, so a song change is on its
                // way and carries this queue. Starting the queue from its top would play the wrong song.
                Timber.tag(TAG).d("Guest: SYNC_QUEUE without the current song, waiting for the song change")
                return@launch
            }
            val currentIndex = player.currentMediaItemIndex
            val localIds = (0 until player.mediaItemCount).map { player.getMediaItemAt(it).mediaId }

            // Only the songs around the current one change, so it keeps playing without a reload.
            connection.allowInternalSync = true
            if (localIds.drop(currentIndex + 1) != ids.drop(newIndex + 1)) {
                player.replaceMediaItems(
                    currentIndex + 1,
                    player.mediaItemCount,
                    queue.drop(newIndex + 1).map { it.toMediaMetadata().toMediaItem() },
                )
            }
            if (localIds.take(currentIndex) != ids.take(newIndex)) {
                player.replaceMediaItems(0, currentIndex, queue.take(newIndex).map { it.toMediaMetadata().toMediaItem() })
            }
            connection.allowInternalSync = false

            try {
                connection.service.queueTitle = action.queueTitle
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to set queue title during SYNC_QUEUE")
            }
        }
    }
    
    private fun deferSyncUntilTrackReady(isPlaying: Boolean, position: Long, now: Long) {
        // The room state already names the track this action belongs to; an older pending may not.
        pendingSyncState = SyncStatePayload(
            currentTrack = roomState.value?.currentTrack ?: pendingSyncState?.currentTrack,
            isPlaying = isPlaying,
            position = position,
            lastUpdate = now,
        )
        applyPendingSyncIfReady()
    }

    private fun addQueueTrack(action: PlaybackActionPayload, connection: PlayerConnection) {
        val track = action.trackInfo
        if (track == null) {
            Timber.tag(TAG).w("QUEUE_ADD missing trackInfo")
            return
        }

        Timber.tag(TAG).d("Guest: QUEUE_ADD ${track.title}, insertNext=${action.insertNext == true}")
        scope.launch(Dispatchers.IO) {
            YouTube.queue(listOf(track.id)).onSuccess { list ->
                val mediaItem = list.firstOrNull()?.toMediaMetadata()?.copy(
                    suggestedBy = track.suggestedBy,
                )?.toMediaItem()
                if (mediaItem == null) {
                    Timber.tag(TAG).w("QUEUE_ADD failed to resolve media item for ${track.id}")
                    return@onSuccess
                }

                launch(Dispatchers.Main) {
                    connection.allowInternalSync = true
                    if (action.insertNext == true) {
                        connection.playNext(mediaItem)
                    } else {
                        connection.addToQueue(mediaItem)
                    }
                    connection.allowInternalSync = false
                }
            }.onFailure {
                Timber.tag(TAG).e(it, "QUEUE_ADD metadata fetch failed")
            }
        }
    }

    private fun handleSyncState(state: SyncStatePayload) {
        val adjustedPos = livePosition(state.position, state.isPlaying, state.lastUpdate)

        Timber.tag(TAG).d("handleSyncState: playing=${state.isPlaying}, pos=${state.position} -> adj=$adjustedPos, track=${state.currentTrack?.id}")
        
        applyPlaybackState(
            currentTrack = state.currentTrack,
            isPlaying = state.isPlaying,
            position = adjustedPos,
            queue = state.queue,
            bypassBuffer = true,
        )
        applyHostVolumeIfNeeded(state.volume)
    }

    private fun setSyncedMediaItems(
        player: Player,
        queue: List<TrackInfo>?,
        currentTrack: TrackInfo,
        position: Long,
    ) {
        val mediaItems = if (queue.isNullOrEmpty()) {
            emptyList()
        } else {
            queue.map { it.toMediaMetadata().toMediaItem() }
        }
        val startIndex = mediaItems.indexOfFirst { it.mediaId == currentTrack.id }

        if (mediaItems.isEmpty()) {
            Timber.tag(TAG).d("No queue in state, loading single track")
        } else if (startIndex == -1) {
            Timber.tag(TAG).w("Current track ${currentTrack.id} not found in queue, defaulting to 0")
        }

        val itemsToPlay = if (startIndex == -1) {
            listOf(currentTrack.toMediaMetadata().toMediaItem())
        } else {
            mediaItems
        }
        val itemIndex = startIndex.takeIf { it >= 0 } ?: 0
        player.setMediaItems(itemsToPlay, itemIndex, position)
    }

    private fun applyPlaybackState(
        currentTrack: TrackInfo?,
        isPlaying: Boolean,
        position: Long,
        queue: List<TrackInfo>?,
        queueTitle: String? = null,
        bypassBuffer: Boolean = false
    ) {
        val connection = playerConnection
        if (connection == null) {
            Timber.tag(TAG).w("Cannot apply playback state - no player")
            return
        }
        val player = connection.player

        Timber.tag(TAG).d("Applying playback state: track=${currentTrack?.id}, pos=$position, queue=${queue?.size}, bypassBuffer=$bypassBuffer")

        activeSyncJob?.cancel()

        if (currentTrack == null) {
            Timber.tag(TAG).d("No track in state, pausing")
            val generation = ++currentTrackGeneration
            scope.launch(Dispatchers.Main) {
                // Verify we're still on the same track generation (no newer track change arrived)
                if (currentTrackGeneration != generation) {
                    Timber.tag(TAG).d("Skipping stale track generation: $generation vs current $currentTrackGeneration")
                    return@launch
                }
                
                if (playerConnection !== connection) return@launch
                isSyncing = true
                connection.allowInternalSync = true
                if (queue != null && queue.isNotEmpty()) {
                    val mediaItems = queue.map { it.toMediaMetadata().toMediaItem() }
                    player.setMediaItems(mediaItems)
                } else if (queue != null) {
                    player.clearMediaItems()
                }
                connection.pause()
                try {
                    connection.service.queueTitle = queueTitle
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to set queue title for empty state")
                }
                connection.allowInternalSync = false
                isSyncing = false
            }
            return
        }

        bufferingTrackId = currentTrack.id
        // Stops this guest re-announcing the room's own song the first time it touches play.
        lastSyncedTrackId = currentTrack.id
        val generation = ++currentTrackGeneration
        val anchoredAt = System.currentTimeMillis()

        scope.launch(Dispatchers.Main) {
            // Verify we're still on the same track generation (no newer track change arrived)
            if (currentTrackGeneration != generation) {
                Timber.tag(TAG).d("Skipping stale track generation: $generation vs current $currentTrackGeneration (track ${currentTrack.id})")
                return@launch
            }
            
            if (playerConnection !== connection) return@launch
            isSyncing = true
            connection.allowInternalSync = true

            try {
                // Re-verify generation before applying media items (critical section)
                if (currentTrackGeneration != generation) {
                    Timber.tag(TAG).d("Stale generation detected before setMediaItems: $generation vs $currentTrackGeneration")
                    return@launch
                }
                
                setSyncedMediaItems(player, queue, currentTrack, position)
                
                // Apply the requested position before waiting for buffering.
                connection.seekTo(position)

                try {
                    connection.service.queueTitle = queueTitle ?: "Listen Together"
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Failed to set queue title during applyPlaybackState")
                }
                
                if (bypassBuffer) {
                    applyImmediateSync(player, currentTrack, isPlaying, position, anchoredAt, generation)
                } else {
                    queueBufferedSync(connection, currentTrack, isPlaying, position, anchoredAt, generation)
                }
                
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error applying playback state")
            } finally {
                connection.allowInternalSync = false
                delay(200)
                isSyncing = false
            }
        }
    }

    private suspend fun applyImmediateSync(
        player: Player,
        currentTrack: TrackInfo,
        isPlaying: Boolean,
        position: Long,
        anchoredAt: Long,
        generation: Int,
    ) {
        Timber.tag(TAG).d("Bypass buffer: immediately applying play=$isPlaying at pos=$position")

        var attempts = 0
        while (player.playbackState != Player.STATE_READY && attempts < 100) {
            delay(50)
            attempts++
        }
        if (currentTrackGeneration != generation) return
        if (player.playbackState != Player.STATE_READY) {
            Timber.tag(TAG).w("Player not ready after 5s timeout during bypass sync, applying anyway")
        }

        stagePendingSync(currentTrack, isPlaying, position, anchoredAt)
        applyPendingSyncIfReady(force = true)
    }

    private fun queueBufferedSync(
        connection: PlayerConnection,
        currentTrack: TrackInfo,
        isPlaying: Boolean,
        position: Long,
        anchoredAt: Long,
        generation: Int,
    ) {
        connection.pause()
        stagePendingSync(currentTrack, isPlaying, position, anchoredAt)
        applyPendingSyncIfReady()
        client.sendBufferReady(currentTrack.id)
        armBufferCompleteWatchdog(currentTrack.id, generation)
    }

    private fun syncToTrack(track: TrackInfo, shouldPlay: Boolean, position: Long) {
        Timber.tag(TAG).d("syncToTrack: ${track.title}, play: $shouldPlay, pos: $position")

        // Track which buffer-complete we expect for this load
        bufferingTrackId = track.id
        val generation = currentTrackGeneration
        
        activeSyncJob?.cancel()
        activeSyncJob = scope.launch(Dispatchers.IO) {
            try {
                // Ignore this load if a newer track change has started.
                if (currentTrackGeneration != generation) {
                    Timber.tag(TAG).d("Skipping stale syncToTrack for ${track.id} (generation $generation vs $currentTrackGeneration)")
                    isSyncing = false
                    return@launch
                }
                
                YouTube.queue(listOf(track.id)).onSuccess { queue ->
                    Timber.tag(TAG).d("Got queue for track ${track.id}")
                    applySyncedQueue(track, shouldPlay, position, generation, queue.firstOrNull()?.toMediaMetadata())
                }.onFailure { e ->
                    Timber.tag(TAG).e(e, "Failed to load track ${track.id}")
                    playerConnection?.allowInternalSync = false
                    isSyncing = false
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Error syncing to track")
                playerConnection?.allowInternalSync = false
                isSyncing = false
            }
        }
    }

    private fun applySyncedQueue(
        track: TrackInfo,
        shouldPlay: Boolean,
        position: Long,
        generation: Int,
        preloadItem: MediaMetadata?,
    ) {
        scope.launch(Dispatchers.Main) {
            if (currentTrackGeneration != generation) {
                Timber.tag(TAG).d("Skipping stale track application for ${track.id} (generation $generation vs $currentTrackGeneration)")
                isSyncing = false
                return@launch
            }

            val connection = playerConnection ?: run {
                isSyncing = false
                return@launch
            }
            if (playerConnection !== connection) {
                isSyncing = false
                return@launch
            }

            isSyncing = true
            connection.allowInternalSync = true
            connection.playQueue(
                YouTubeQueue(
                    endpoint = WatchEndpoint(videoId = track.id),
                    preloadItem = preloadItem,
                ),
            )
            try {
                connection.service.queueTitle = "Listen Together"
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to set queue title")
            }
            connection.allowInternalSync = false

            var waitCount = 0
            while (waitCount < 40) {
                if (currentTrackGeneration != generation) {
                    Timber.tag(TAG).d("Generation changed while waiting for player ready - aborting sync for ${track.id}")
                    isSyncing = false
                    return@launch
                }
                try {
                    if (connection.player.playbackState == Player.STATE_READY) {
                        Timber.tag(TAG).d("Player ready after ${waitCount * 50}ms")
                        break
                    }
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Error checking player state")
                    break
                }
                delay(50)
                waitCount++
            }

            connection.pause()
            stagePendingSync(track, shouldPlay, position, System.currentTimeMillis())
            applyPendingSyncIfReady()
            client.sendBufferReady(track.id)
            Timber.tag(TAG).d("Sent buffer ready for ${track.id}, pending sync stored: pos=$position, play=$shouldPlay")
            armBufferCompleteWatchdog(track.id, generation)

            delay(100)
            isSyncing = false
        }
    }

    /** Connects to the Listen Together server. */
    fun connect() {
        initialize()
        Timber.tag(TAG).d("Connecting to server")
        client.connect()
    }

    /** Disconnects and cleans up playback observers. */
    fun disconnect() {
        Timber.tag(TAG).d("Disconnecting from server")
        cleanup()
        client.disconnect()
    }

    fun createRoom(username: String) {
        initialize()
        Timber.tag(TAG).d("Creating room with username: $username")
        client.createRoom(username)
    }

    fun joinRoom(roomCode: String, username: String) {
        initialize()
        Timber.tag(TAG).d("Joining room $roomCode as $username")
        client.joinRoom(roomCode, username)
    }

    fun leaveRoom() {
        Timber.tag(TAG).d("Leaving room")
        cleanup()
        client.leaveRoom()
    }

    fun approveJoin(userId: String) = client.approveJoin(userId)

    fun rejectJoin(userId: String, reason: String? = null) = client.rejectJoin(userId, reason)

    fun kickUser(userId: String, reason: String? = null) = client.kickUser(userId, reason)

    /** Blocks a username from the room. */
    fun blockUser(username: String) = client.blockUser(username)

    fun unblockUser(username: String) = client.unblockUser(username)

    fun getBlockedUsernames(): Set<String> = blockedUsernames.value

    fun transferHost(newHostId: String) = client.transferHost(newHostId)

    /** Sends a track change when playback can be controlled and sync is idle. */
    fun sendTrackChange(metadata: MediaMetadata) {
        if (!canControlPlayback || isSyncing) return
        sendTrackChangeInternal(metadata)
    }
    
    /** Sends a track change during initial sync, even while [isSyncing]. */
    private fun sendTrackChangeInternal(metadata: MediaMetadata) {
        if (!canControlPlayback) return
        // Its id is a content URI only this phone can open; guests would stall trying to load it.
        if (metadata.isLocalFile) return

        // Supply a fallback duration when metadata has none.
        val durationMs = if (metadata.duration > 0) metadata.duration.toLong() * 1000 else 180000L
        
        val trackInfo = TrackInfo(
            id = metadata.id,
            title = metadata.title,
            artist = metadata.artists.joinToString(", ") { it.name },
            album = metadata.album?.title,
            duration = durationMs,
            thumbnail = metadata.thumbnailUrl,
            suggestedBy = metadata.suggestedBy
        )
        
        Timber.tag(TAG).d("Sending track change: ${trackInfo.title}, duration: $durationMs")
        client.logExternal(
            "Sync out: now playing",
            "${trackInfo.title} — ${trackInfo.artist} (${durationMs / 1000}s)"
        )
        
        // Send the current queue with the track change.
        val currentQueue = try {
            playerConnection?.queueWindows?.value
                ?.filterNot { it.mediaItem.metadata?.isLocalFile == true }
                ?.map { it.toTrackInfo() }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to get current queue")
            null
        }
        val currentTitle = try {
            playerConnection?.queueTitle?.value
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to get current title")
            null
        }
        
        client.sendPlaybackAction(
            PlaybackActions.CHANGE_TRACK,
            queueTitle = currentTitle,
            trackInfo = trackInfo,
            queue = currentQueue
        )
    }

    private fun startQueueSyncObservation() {
        if (queueObserverJob?.isActive == true) return
    
        Timber.tag(TAG).d("Starting queue sync observation")
        queueObserverJob = scope.launch {
            playerConnection?.queueWindows
                ?.map { windows ->
                    windows.filterNot { it.mediaItem.metadata?.isLocalFile == true }.map { it.toTrackInfo() }
                }
                ?.distinctUntilChanged()
                ?.collectLatest { tracks ->
                    if (!isHost || !isInRoom || isSyncing) return@collectLatest
                
                    delay(500) // Debounce rapid playlist manipulations
                
                    Timber.tag(TAG).d("Sending SYNC_QUEUE with ${tracks.size} items")
                    val queueTitle = try {
                        playerConnection?.queueTitle?.value
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e, "Failed to get queue title")
                        null
                    }
                    client.sendPlaybackAction(
                        PlaybackActions.SYNC_QUEUE,
                        queueTitle = queueTitle,
                        queue = tracks
                    )
                }
        }
    }

    private fun startVolumeSyncObservation() {
        if (volumeObserverJob?.isActive == true) return

        Timber.tag(TAG).d("Starting volume sync observation")
        volumeObserverJob = scope.launch {
            playerConnection?.service?.playerVolume
                ?.collectLatest { volume ->
                    if (!isHost || !isInRoom || !syncHostVolumeEnabled.value) return@collectLatest

                    val normalized = volume.coerceIn(0f, 1f)
                    val last = lastSyncedVolume
                    if (last != null && kotlin.math.abs(last - normalized) < 0.01f) return@collectLatest

                    lastSyncedVolume = normalized
                    client.sendPlaybackAction(PlaybackActions.SET_VOLUME, volume = normalized)
                }
        }
    }

    private fun stopVolumeSyncObservation() {
        volumeObserverJob?.cancel()
        volumeObserverJob = null
        lastSyncedVolume = null
    }

    private fun androidx.media3.common.Timeline.Window.toTrackInfo(): TrackInfo {
        val metadata = mediaItem.metadata ?: return TrackInfo("unknown", "Unknown", "Unknown", "", 0, "")
        val durationMs = if (metadata.duration > 0) metadata.duration.toLong() * 1000 else 180000L
        return TrackInfo(
            id = metadata.id,
            title = metadata.title,
            artist = metadata.artists.joinToString(", ") { it.name },
            album = metadata.album?.title,
            duration = durationMs,
            thumbnail = metadata.thumbnailUrl,
            suggestedBy = metadata.suggestedBy
        )
    }

    private fun stopQueueSyncObservation() {
        queueObserverJob?.cancel()
        queueObserverJob = null
    }

    private fun TrackInfo.toMediaMetadata(): MediaMetadata {
        return MediaMetadata(
            id = id,
            title = title,
            artists = listOf(Artist(id = "", name = artist)),
            album = if (album != null) Album(id = "", title = album) else null,
            duration = (duration / 1000).toInt(),
            thumbnailUrl = thumbnail,
            suggestedBy = suggestedBy
        )
    }

    /** Requests the current playback state for a guest. */
    fun requestSync() {
        if (!isInRoom || isHost) {
            Timber.tag(TAG).d("requestSync: not applicable (isInRoom=$isInRoom, isHost=$isHost)")
            return
        }
        Timber.tag(TAG).d("Requesting sync from server")
        client.requestSync()
    }

    /** Clears the room log. */
    fun clearLogs() = client.clearLogs()

    /** Suggests a track to the host. */
    fun suggestTrack(track: TrackInfo) = client.suggestTrack(track)

    /** Owner-only: `ControlModes.OWNER` or `ControlModes.EVERYONE`. */
    fun setControlMode(mode: String) = client.setControlMode(mode)

    /** Owner-only: push the room's closing time back. */
    fun extendSession() = client.extendSession()

    val canControlPlayback: Boolean get() = client.canControlPlayback

    /** Approves a suggestion as host. */
    fun approveSuggestion(suggestionId: String) {
        if (!isHost) return
        client.approveSuggestion(suggestionId)
    }

    /** Rejects a suggestion as host. */
    fun rejectSuggestion(suggestionId: String, reason: String? = null) = client.rejectSuggestion(suggestionId, reason)
    
    /** Forces reconnection for manual recovery. */
    fun forceReconnect() {
        Timber.tag(TAG).d("Forcing reconnection")
        client.forceReconnect()
    }
    
    fun getPersistedRoomCode(): String? = client.getPersistedRoomCode()
    
    /** Returns elapsed session time in milliseconds. */
    fun getSessionAge(): Long = client.getSessionAge()

    private var heartbeatJob: Job? = null

    private fun startHeartbeat() {
        if (heartbeatJob?.isActive == true) return
        heartbeatJob = scope.launch {
            while (heartbeatJob?.isActive == true && isInRoom && isHost) {
                delay(10000L)
                playerConnection?.player?.let { player ->
                    if (player.playWhenReady && player.playbackState == Player.STATE_READY &&
                        player.currentMetadata?.isLocalFile != true
                    ) {
                        val pos = player.currentPosition
                        Timber.tag(TAG).d("Host heartbeat: sending PLAY at pos $pos")
                        client.sendPlaybackAction(PlaybackActions.PLAY, position = pos)
                    }
                }
            }
        }
        Timber.tag(TAG).d("Host heartbeat started (10s interval)")
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        Timber.tag(TAG).d("Host heartbeat stopped")
    }

    /**
     * Send a chat message to the room
     */
    fun sendChatMessage(message: String, replyTo: RepliedMessage? = null) {
        if (message.isBlank()) return
        client.sendChatMessage(message, replyTo)
    }
}
