/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.jam.JamManager
import com.convx.music.jam.PartyPreview
import com.convx.music.jam.PartyStats
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class JamViewModel @Inject constructor(private val manager: JamManager) : ViewModel() {
    val session = manager.session
    val busy = manager.busy
    val message = manager.message
    val activity = manager.activity

    val invite = manager.invite

    fun dismissMessage() = manager.dismissMessage()

    fun consumeInvite() = manager.consumeInvite()

    fun warm() = viewModelScope.launch { manager.warm() }

    fun inviteLink(code: String) = manager.inviteLink(code)

    val lastCode: String? get() = manager.lastCode

    suspend fun preview(code: String): PartyPreview? = manager.preview(code)

    suspend fun stats(): PartyStats? = manager.stats()

    var serverUrl: String
        get() = manager.serverUrl
        set(value) { manager.serverUrl = value }

    var displayName: String
        get() = manager.displayName
        set(value) { manager.displayName = value }

    fun create(name: String, avatarUrl: String?) = viewModelScope.launch { manager.create(name, avatarUrl) }

    fun join(code: String, name: String, avatarUrl: String?) = viewModelScope.launch { manager.join(code, name, avatarUrl) }

    fun leave() = manager.leave()
    fun retryAudio() = manager.retryAudio()

    var consistentSource: Boolean
        get() = manager.consistentSource
        set(value) { manager.consistentSource = value }

    fun setHostOnlyControl(enabled: Boolean) = manager.setHostOnlyControl(enabled)

    fun setAutoplay(enabled: Boolean) = manager.setAutoplay(enabled)

    fun setMaxMembers(count: Int) = manager.setMaxMembers(count)

    fun kick(memberId: String) = manager.kick(memberId)
}
