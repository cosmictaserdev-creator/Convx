/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.viewmodels

import android.content.Context
import android.content.Intent
import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convxy.music.App
import com.convxy.music.constants.AccountChannelHandleKey
import com.convxy.music.constants.AccountEmailKey
import com.convxy.music.constants.AccountNameKey
import com.convxy.music.constants.DataSyncIdKey
import com.convxy.music.constants.InnerTubeCookieKey
import com.convxy.music.constants.SavedAccount
import com.convxy.music.constants.SavedAccountsKey
import com.convxy.music.constants.VisitorDataKey
import com.convxy.music.constants.toJson
import com.convxy.music.utils.SyncUtils
import com.convxy.music.utils.dataStore
import com.music.innertube.YouTube
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.datastore.preferences.core.edit

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val syncUtils: SyncUtils,
) : ViewModel() {

    /**
     * Logout user and clear all synced content to prevent data mixing between accounts
     */
    fun logoutAndClearSyncedContent(context: Context, onCookieChange: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // Clear all YouTube Music synced content first
            syncUtils.clearAllSyncedContent()

            // Then clear account preferences
            App.forgetAccount(context)

            // Clear cookie in UI
            onCookieChange("")
        }
    }

    /**
     * Just logout without clearing library data
     */
    fun logoutKeepData(context: Context, onCookieChange: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            App.forgetAccount(context)
            withContext(Dispatchers.Main) {
                onCookieChange("")
            }
        }
    }

    /**
     * Save token credentials atomically to DataStore, then restart the app.
     * This ensures all writes complete before the process is killed,
     * preventing the race condition where Runtime.exit(0) kills the process
     * before async DataStore coroutines finish writing.
     */
    fun saveTokenAndRestart(
        context: Context,
        cookie: String,
        visitorData: String,
        dataSyncId: String,
        accountName: String,
        accountEmail: String,
        accountChannelHandle: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStore.edit { settings ->
                settings[InnerTubeCookieKey] = cookie
                settings[VisitorDataKey] = visitorData
                settings[DataSyncIdKey] = dataSyncId
                settings[AccountNameKey] = accountName
                settings[AccountEmailKey] = accountEmail
                settings[AccountChannelHandleKey] = accountChannelHandle
            }
            withContext(Dispatchers.Main) {
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
                Runtime.getRuntime().exit(0)
            }
        }
    }

    /**
     * Finishes a fresh login once a channel has been chosen (or there was only one
     * to begin with). [cookie]/[visitorData] are the values the WebView just captured;
     * [chosenDataSyncId] selects which of [allChannels] is active. Looks up the
     * chosen channel's name/email/handle via [com.music.innertube.YouTube.accountInfo]
     * — that call reads whatever identity is currently set on the client, so
     * dataSyncId must be assigned first — then saves every channel for later
     * instant switching and restarts, same as [saveTokenAndRestart].
     */
    fun applyChannelAndRestart(
        context: Context,
        cookie: String,
        visitorData: String,
        chosenDataSyncId: String,
        allChannels: List<SavedAccount>,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.cookie = cookie
            YouTube.visitorData = visitorData
            YouTube.dataSyncId = chosenDataSyncId
            val info = YouTube.accountInfo().getOrNull()

            context.dataStore.edit { settings ->
                settings[InnerTubeCookieKey] = cookie
                settings[VisitorDataKey] = visitorData
                settings[DataSyncIdKey] = chosenDataSyncId
                settings[AccountNameKey] = info?.name.orEmpty()
                settings[AccountEmailKey] = info?.email.orEmpty()
                settings[AccountChannelHandleKey] = info?.channelHandle.orEmpty()
                settings[SavedAccountsKey] = allChannels.toJson()
            }
            withContext(Dispatchers.Main) {
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
                Runtime.getRuntime().exit(0)
            }
        }
    }

    /**
     * Re-reads the account's channel list and stores it for tap-to-switch.
     *
     * The list used to be written only by [applyChannelAndRestart], i.e. only by a
     * login that happened after the channel picker shipped — anyone signed in before
     * that, or whose channel lookup failed once at login, was left with an empty list
     * and no way to refill it, so the switcher never appeared. Cheap enough to run
     * whenever Account settings opens.
     */
    fun refreshSavedAccounts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (YouTube.cookie == null) return@launch
            val channels = YouTube.getAccountChannels().getOrNull()
                ?.mapNotNull { c ->
                    c.dataSyncId?.let { SavedAccount(it, c.name, c.channelHandle, c.thumbnailUrl) }
                }
                ?.takeIf { it.isNotEmpty() }
                ?: return@launch
            context.dataStore.edit { settings ->
                settings[SavedAccountsKey] = channels.toJson()
            }
        }
    }

    /**
     * Switches the active channel to an already-saved account. No WebView, no
     * restart — the cookie is unchanged (same Google login), only which channel
     * it acts as changes, same mechanism [com.convxy.music.ui.screens.SwitchChannelScreen]
     * uses when it detects a DATASYNC_ID change.
     */
    fun switchToSavedAccount(context: Context, account: SavedAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.dataSyncId = account.dataSyncId

            // Same staleness risk SwitchChannelScreen's WebView flow already
            // guards against: Google can rotate the session cookie as part of
            // a channel switch. This path has no WebView page load of its own
            // to pick up a fresh one, but CookieManager's jar is persistent —
            // pull whatever it currently holds rather than leaving the cookie
            // captured at initial login in place indefinitely.
            CookieManager.getInstance().getCookie("https://music.youtube.com")
                ?.takeIf { it.isNotBlank() }
                ?.let { freshCookie ->
                    YouTube.cookie = freshCookie
                    context.dataStore.edit { settings ->
                        settings[InnerTubeCookieKey] = freshCookie
                    }
                }

            context.dataStore.edit { settings ->
                settings[DataSyncIdKey] = account.dataSyncId
                settings[AccountNameKey] = account.name
                settings[AccountChannelHandleKey] = account.channelHandle.orEmpty()
            }
        }
    }
}
