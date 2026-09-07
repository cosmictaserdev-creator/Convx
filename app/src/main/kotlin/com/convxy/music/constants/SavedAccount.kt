/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.constants

import org.json.JSONArray
import org.json.JSONObject

/**
 * One YouTube channel seen under the logged-in Google account's cookie. The cookie
 * itself is shared across all channels of one account (stored separately under
 * [InnerTubeCookieKey]) — only [dataSyncId] differs per channel, so switching between
 * saved accounts is just swapping that value, no WebView round-trip needed.
 */
data class SavedAccount(
    val dataSyncId: String,
    val name: String,
    val channelHandle: String?,
    val thumbnailUrl: String?,
)

fun List<SavedAccount>.toJson(): String {
    val arr = JSONArray()
    forEach { acc ->
        arr.put(
            JSONObject().apply {
                put("dataSyncId", acc.dataSyncId)
                put("name", acc.name)
                put("channelHandle", acc.channelHandle.orEmpty())
                put("thumbnailUrl", acc.thumbnailUrl.orEmpty())
            }
        )
    }
    return arr.toString()
}

fun String.toSavedAccounts(): List<SavedAccount> = runCatching {
    val arr = JSONArray(this)
    (0 until arr.length()).map { i ->
        val obj = arr.getJSONObject(i)
        SavedAccount(
            dataSyncId = obj.getString("dataSyncId"),
            name = obj.getString("name"),
            channelHandle = obj.optString("channelHandle").ifEmpty { null },
            thumbnailUrl = obj.optString("thumbnailUrl").ifEmpty { null },
        )
    }
}.getOrElse { emptyList() }
