/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.listentogether

import com.convx.music.playback.PlayerConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListenTogetherPlayerConnectionBridge @Inject constructor() {
    private val _playerConnection = MutableStateFlow<PlayerConnection?>(null)
    val playerConnection: StateFlow<PlayerConnection?> = _playerConnection.asStateFlow()

    /** True while a party's host holds the controls and this device is not the host. */
    private val _partyLocked = MutableStateFlow(false)
    val partyLocked: StateFlow<Boolean> = _partyLocked.asStateFlow()

    fun update(connection: PlayerConnection?) {
        _playerConnection.value = connection
    }

    fun setPartyLocked(locked: Boolean) {
        _partyLocked.value = locked
    }
}
