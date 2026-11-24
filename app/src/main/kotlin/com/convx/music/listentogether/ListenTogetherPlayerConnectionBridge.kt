/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.listentogether

import com.convx.music.playback.PlayerConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListenTogetherPlayerConnectionBridge {
    private val _playerConnection = MutableStateFlow<PlayerConnection?>(null)
    val playerConnection: StateFlow<PlayerConnection?> = _playerConnection.asStateFlow()

    /** True while a party's host holds the controls and this device is not the host. */
    private val _partyLocked = MutableStateFlow(false)
    val partyLocked: StateFlow<Boolean> = _partyLocked.asStateFlow()

    private val _jamActive = MutableStateFlow(false)
    val jamActive: StateFlow<Boolean> = _jamActive.asStateFlow()

    /** Read by the playback service without constructing either room manager. */
    private val _jamRecovery = MutableStateFlow(JamMediaRecovery.NONE)
    val jamRecovery: StateFlow<JamMediaRecovery> = _jamRecovery.asStateFlow()
    @Volatile var jamConsistentSource: Boolean = true
    val requiresJamYouTube: Boolean get() = _jamActive.value && jamConsistentSource
    private val _jamTimingRevision = MutableStateFlow(0L)
    val jamTimingRevision: StateFlow<Long> = _jamTimingRevision.asStateFlow()

    fun invalidateJamTiming() { _jamTimingRevision.value += 1L }

    fun setJamActive(active: Boolean) {
        _jamActive.value = active
        if (!active) _jamRecovery.value = JamMediaRecovery.NONE
    }

    fun setJamRecovery(recovery: JamMediaRecovery) {
        _jamRecovery.value = if (_jamActive.value) recovery else JamMediaRecovery.NONE
    }

    fun update(connection: PlayerConnection?) {
        _playerConnection.value = connection
    }

    fun setPartyLocked(locked: Boolean) {
        _partyLocked.value = locked
    }
}

enum class JamMediaRecovery { NONE, RECOVERING, UNAVAILABLE }
