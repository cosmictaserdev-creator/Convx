/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.screens

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import android.widget.Toast
import com.music.innertube.YouTube
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.DataSyncIdKey
import com.convxy.music.constants.InnerTubeCookieKey
import com.convxy.music.constants.SavedAccount
import com.convxy.music.constants.SavedAccountsKey
import com.convxy.music.constants.toJson
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import kotlinx.coroutines.launch

/**
 * Lets an already-logged-in user switch which YouTube channel on their Google
 * Account the app acts as, without repeating the whole sign-in flow.
 *
 * Deliberately has NO Compose UI drawn over the WebView (no button, no
 * overlay) — that's what made [LoginScreen] glitch and eat touches when it
 * sat over YouTube's own in-page content (e.g. the Premium upsell card).
 * Instead this just opens music.youtube.com (already logged in via the
 * existing cookie jar) and watches DATASYNC_ID: the moment the user taps
 * their own profile picture and picks a different channel — pure YouTube UI,
 * nothing of ours drawn on top of it — YouTube reloads the page with a new
 * DATASYNC_ID, which this detects and saves automatically. No manual
 * confirm step needed.
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchChannelScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var dataSyncId by rememberPreference(DataSyncIdKey, "")
    var innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    var savedAccountsJson by rememberPreference(SavedAccountsKey, "")
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    var switched by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .fillMaxSize(),
            factory = { webViewContext ->
                WebView(webViewContext).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")
                        }
                    }
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onRetrieveDataSyncId(newDataSyncIdRaw: String?) {
                            // Same WebView JS-bridge marshaling quirk as LoginScreen — can hand
                            // this back still percent-encoded ("%3D%3D" instead of "=="),
                            // corrupting every authenticated request's session context. Decode
                            // is a no-op on an already-clean value.
                            val newDataSyncId = newDataSyncIdRaw?.let { android.net.Uri.decode(it) }
                            if (newDataSyncId != null && newDataSyncId.isNotEmpty() && newDataSyncId != dataSyncId) {
                                dataSyncId = newDataSyncId
                                YouTube.dataSyncId = newDataSyncId

                                // Google can rotate the session cookie as part of a channel
                                // switch. The cookie stored at initial login was never
                                // refreshed after this point, so it went stale on the NEXT
                                // switch and every request looked logged-out — forcing a
                                // full re-login just to get a matching cookie again.
                                CookieManager.getInstance().getCookie("https://music.youtube.com")
                                    ?.takeIf { it.isNotBlank() }
                                    ?.let { freshCookie ->
                                        innerTubeCookie = freshCookie
                                        YouTube.cookie = freshCookie
                                    }

                                switched = true
                                Toast.makeText(context, context.getString(R.string.switch_channel_success), Toast.LENGTH_SHORT).show()

                                // Refresh the saved-account list so a channel discovered
                                // through this WebView flow is also available for instant
                                // tap-to-switch afterwards, without a WebView round-trip.
                                coroutineScope.launch {
                                    YouTube.getAccountChannels().getOrNull()
                                        ?.mapNotNull { c ->
                                            c.dataSyncId?.let { id ->
                                                SavedAccount(id, c.name, c.channelHandle, c.thumbnailUrl)
                                            }
                                        }
                                        ?.takeIf { it.isNotEmpty() }
                                        ?.let { channels -> savedAccountsJson = channels.toJson() }
                                }
                            }
                        }
                    }, "Android")
                    webViewRef.value = this
                    loadUrl("https://music.youtube.com")
                }
            }
        )

        TopAppBar(
            title = { Text(stringResource(if (switched) R.string.switch_channel_switched_title else R.string.switch_channel_title)) },
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            }
        )
    }

    BackHandler(enabled = webViewRef.value?.canGoBack() == true) {
        webViewRef.value?.goBack()
    }
}
