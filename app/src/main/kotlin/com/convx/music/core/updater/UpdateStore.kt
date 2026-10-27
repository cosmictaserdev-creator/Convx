package com.convx.music.core.updater

import com.tencent.mmkv.MMKV
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateStore @Inject constructor() {
    private val prefs get() = MMKV.mmkvWithID("github_updater")
    private val json = Json { ignoreUnknownKeys = true }
    var release: UpdateRelease?
        get() = prefs.decodeString("release")?.let { runCatching { json.decodeFromString<UpdateRelease>(it) }.getOrNull() }
        set(value) { if (value == null) prefs.removeValueForKey("release") else prefs.encode("release", json.encodeToString(value)) }
    var lastCheck: Long
        get() = prefs.decodeLong("last_check")
        set(value) { prefs.encode("last_check", value) }
    var lastAttempt: Long
        get() = prefs.decodeLong("last_attempt")
        set(value) { prefs.encode("last_attempt", value) }
    var workId: String?
        get() = prefs.decodeString("work_id")
        set(value) { prefs.encode("work_id", value) }
    var readyPath: String?
        get() = prefs.decodeString("ready_path")
        set(value) { prefs.encode("ready_path", value) }
    var sessionId: Int
        get() = prefs.decodeInt("session_id", -1)
        set(value) { prefs.encode("session_id", value) }
    var installStatus: Int
        get() = prefs.decodeInt("install_status", Int.MIN_VALUE)
        set(value) { prefs.encode("install_status", value) }
    fun shouldPrompt(id: String, now: Long): Boolean = UpdatePolicy.shouldPrompt(id,
        prefs.decodeString("dismissed"), prefs.decodeString("snoozed"), prefs.decodeLong("snoozed_until"), now)
    fun dismiss(id: String) { prefs.encode("dismissed", id) }
    fun snooze(id: String, now: Long) {
        prefs.encode("snoozed", id)
        prefs.encode("snoozed_until", now + UpdatePolicy.SNOOZE_MILLIS)
    }
}
