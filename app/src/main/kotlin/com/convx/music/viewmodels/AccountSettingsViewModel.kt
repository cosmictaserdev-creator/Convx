/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.viewmodels

import android.content.Context
import android.content.Intent
import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.convx.music.App
import com.convx.music.constants.AccountChannelHandleKey
import com.convx.music.constants.AccountEmailKey
import com.convx.music.constants.AccountNameKey
import com.convx.music.constants.DataSyncIdKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.SavedAccount
import com.convx.music.constants.SavedAccountsKey
import com.convx.music.constants.VisitorDataKey
import com.convx.music.constants.toJson
import com.convx.music.utils.SyncUtils
import com.convx.music.utils.dataStore
import com.music.innertube.YouTube
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.datastore.preferences.core.edit

class AccountSettingsViewModel(
    private val syncUtils: SyncUtils,
) : ViewModel() {

    /** Logs out and clears synced content. */
    fun logoutAndClearSyncedContent(context: Context, onCookieChange: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            syncUtils.clearAllSyncedContent()

            App.forgetAccount(context)

            onCookieChange("")
        }
    }

    /** Logs out without clearing library data. */
    fun logoutKeepData(context: Context, onCookieChange: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            App.forgetAccount(context)
            withContext(Dispatchers.Main) {
                onCookieChange("")
            }
        }
    }

    /** Saves credentials before restarting so DataStore writes finish before process exit. */
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
            withContext(Dispatchers.Main) { restartApp(context) }
        }
    }

    /** Saves the selected channel and account list, then restarts with that identity. */
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
            // Read the profile after setting the selected channel.
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
            withContext(Dispatchers.Main) { restartApp(context) }
        }
    }

    /** Refreshes saved channels when Account settings opens. */
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

    /** Switches to a saved channel without changing the Google login or restarting. */
    fun switchToSavedAccount(context: Context, account: SavedAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            YouTube.dataSyncId = account.dataSyncId

            // Refresh the cookie in case Google rotated it during the switch.
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

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}
