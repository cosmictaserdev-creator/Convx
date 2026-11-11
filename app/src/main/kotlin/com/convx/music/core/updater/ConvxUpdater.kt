package com.convx.music.core.updater

import android.content.Context
import com.tencent.mmkv.MMKV
import java.util.regex.Pattern

const val PREFS_NAME = "settings"
const val KEY_AUTO_UPDATE_CHECK = "auto_update_check"
const val KEY_LAST_CHECKED_TIME = "last_checked_time"
const val KEY_BETA_UPDATES = "beta_updates"
const val KEY_UPDATE_AVAILABLE = "update_available"
const val AUTOMATIC_UPDATE_ALERTS_PAUSED = false

const val KEY_LAST_NOTIFIED_TAG = "last_notified_update_tag"

fun getLastNotifiedUpdateTag(): String = updatePrefs().decodeString(KEY_LAST_NOTIFIED_TAG, "") ?: ""

fun saveLastNotifiedUpdateTag(tag: String) {
    updatePrefs().encode(KEY_LAST_NOTIFIED_TAG, tag)
}

private fun updatePrefs(): MMKV = MMKV.mmkvWithID(PREFS_NAME)

fun getUpdateAvailableState(context: Context): Boolean =
    !AUTOMATIC_UPDATE_ALERTS_PAUSED && updatePrefs().decodeBool(KEY_UPDATE_AVAILABLE, false)

fun saveUpdateAvailableState(context: Context, available: Boolean) {
    updatePrefs().encode(KEY_UPDATE_AVAILABLE, available)
}

fun getAutoUpdateCheckSetting(context: Context): Boolean =
    !AUTOMATIC_UPDATE_ALERTS_PAUSED && updatePrefs().decodeBool(KEY_AUTO_UPDATE_CHECK, true)

fun saveAutoUpdateCheckSetting(context: Context, enabled: Boolean) {
    updatePrefs().encode(KEY_AUTO_UPDATE_CHECK, enabled)
}

const val KEY_UPDATE_NOTIFICATIONS = "update_notifications"

fun getUpdateNotificationsSetting(context: Context): Boolean =
    !AUTOMATIC_UPDATE_ALERTS_PAUSED && updatePrefs().decodeBool(KEY_UPDATE_NOTIFICATIONS, false)

fun saveUpdateNotificationsSetting(context: Context, enabled: Boolean) {
    updatePrefs().encode(KEY_UPDATE_NOTIFICATIONS, enabled)
}

fun saveLastCheckedTime(context: Context, timestamp: String) {
    updatePrefs().encode(KEY_LAST_CHECKED_TIME, timestamp)
}

fun getLastCheckedTime(context: Context): String =
    updatePrefs().decodeString(KEY_LAST_CHECKED_TIME, "") ?: ""

fun getBetaUpdatesSetting(context: Context): Boolean =
    updatePrefs().decodeBool(KEY_BETA_UPDATES, false)

fun saveBetaUpdatesSetting(context: Context, enabled: Boolean) {
    updatePrefs().encode(KEY_BETA_UPDATES, enabled)
}

fun String.extractUrls(): List<Pair<IntRange, String>> {
    val urlPattern = Pattern.compile(
        "(?:^|[\\s])((https?://|www\\.|pic\\.)[\\w-]+(\\.[\\w-]+)+([/?].*)?)"
    )
    val matcher = urlPattern.matcher(this)
    val urlList = mutableListOf<Pair<IntRange, String>>()

    while (matcher.find()) {
        val url = matcher.group(1)?.trim() ?: continue
        val range = IntRange(matcher.start(1), matcher.end(1) - 1)
        val fullUrl = if (url.startsWith("http")) url else "https://$url"
        urlList.add(range to fullUrl)
    }

    return urlList
}
