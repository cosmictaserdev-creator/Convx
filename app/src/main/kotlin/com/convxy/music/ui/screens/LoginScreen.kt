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
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
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
import com.music.innertube.YouTube
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.R
import com.convxy.music.constants.DataSyncIdKey
import com.convxy.music.constants.InnerTubeCookieKey
import com.convxy.music.constants.SavedAccount
import com.convxy.music.constants.VisitorDataKey
import com.convxy.music.ui.component.IconButton
import com.convxy.music.ui.utils.backToMain
import com.convxy.music.utils.rememberPreference
import com.convxy.music.utils.reportException
import com.convxy.music.viewmodels.AccountSettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Holds the cookie/visitorData/channel-list from a completed WebView login while
 * [ChannelPickerScreen] is on screen, when the account has more than one YouTube
 * channel. Transient, in-memory only — if the process dies mid-picker the user
 * just logs in again, same as any other interrupted login.
 */
object PendingChannelLogin {
    var cookie: String = ""
    var visitorData: String = ""
    var channels: List<SavedAccount> = emptyList()
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var visitorData by rememberPreference(VisitorDataKey, "")
    var dataSyncId by rememberPreference(DataSyncIdKey, "")
    var innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    var hasCompletedLogin by remember { mutableStateOf(false) }
    val accountSettingsViewModel: AccountSettingsViewModel = hiltViewModel()

    // The JS bridge values land here synchronously. The DataStore-backed prefs
    // above only settle after an async write plus a flow emission, so reading
    // them straight after login raced and could hand YouTube a blank
    // visitorData / dataSyncId — which then breaks search and playback for the
    // whole session, and stays broken because the blanks get persisted.
    val liveVisitorData = remember { AtomicReference("") }
    val liveDataSyncId = remember { AtomicReference("") }

    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    // Caps the reload-and-retry below so a persistent failure (e.g. an account with
    // no YouTube channel at all) can't hammer the network forever instead of just
    // leaving the user to back out.
    val validationAttempts = remember { AtomicInteger(0) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .fillMaxSize(),
            factory = { webViewContext ->
                WebView(webViewContext).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                            loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")

                            if (url?.startsWith("https://music.youtube.com") == true && !hasCompletedLogin) {
                                val cookie = CookieManager.getInstance().getCookie(url)
                                if (cookie.isNullOrBlank()) return  // not signed in yet; wait for the next page
                                innerTubeCookie = cookie
                                hasCompletedLogin = true

                                coroutineScope.launch {
                                    // Wait for the async JS bridge instead of guessing with a
                                    // fixed delay — a blank visitorData poisons every later request.
                                    var waitedMs = 0
                                    while (liveVisitorData.get().isBlank() && waitedMs < 5000) {
                                        delay(100)
                                        waitedMs += 100
                                    }
                                    val newVisitorData = liveVisitorData.get()
                                    if (newVisitorData.isBlank()) {
                                        Timber.e("Login: visitorData never arrived — aborting instead of storing a blank session")
                                        hasCompletedLogin = false
                                        return@launch
                                    }

                                    // Initialize YouTube object with new authentication data
                                    YouTube.cookie = cookie
                                    YouTube.dataSyncId = liveDataSyncId.get().ifBlank { null }
                                    YouTube.visitorData = newVisitorData

                                    Timber.d("Login: YouTube object initialized, validating...")

                                    // Validate via getAccountChannels, not accountInfo — its result
                                    // was never used below anyway (only success/failure mattered),
                                    // and it requires activeAccountHeaderRenderer, a field Google
                                    // omits for some accounts (multi-channel/brand accounts — same
                                    // failure class reported in sibling YouTube Music clients).
                                    // getAccountChannels hits a different endpoint that doesn't
                                    // depend on that field and is what actually gets used below.
                                    // An EMPTY list is a legitimate result here (a single-channel
                                    // account has nothing to switch to) — only a thrown exception
                                    // (a real network/parse failure) counts as login failing.
                                    YouTube.getAccountChannels().mapCatching { raw ->
                                        raw.mapNotNull { c ->
                                            c.dataSyncId?.let { id ->
                                                SavedAccount(id, c.name, c.channelHandle, c.thumbnailUrl)
                                            }
                                        }
                                    }.onSuccess { channels ->
                                        // Clean up WebView
                                        webViewRef.value?.apply {
                                            stopLoading()
                                            clearHistory()
                                            clearCache(true)
                                            clearFormData()
                                        }

                                        if (channels.size > 1) {
                                            Timber.d("Login: ${channels.size} channels on this account, showing picker")
                                            PendingChannelLogin.cookie = cookie
                                            PendingChannelLogin.visitorData = newVisitorData
                                            PendingChannelLogin.channels = channels
                                            navController.navigate("channel_picker") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            Timber.d("Login: single channel, restarting app...")
                                            // No picker shown means nothing needs disambiguating —
                                            // liveDataSyncId (straight from the page's own
                                            // window.yt.config_.DATASYNC_ID) IS the active identity
                                            // already. Preferring the channel-list-endpoint's parsed
                                            // value here was an unnecessary swap to a value sourced
                                            // from a completely different response shape, for no
                                            // benefit — only fall back to it if the bridge somehow
                                            // never fired.
                                            accountSettingsViewModel.applyChannelAndRestart(
                                                context = context,
                                                cookie = cookie,
                                                visitorData = newVisitorData,
                                                chosenDataSyncId = liveDataSyncId.get()
                                                    .ifBlank { channels.firstOrNull()?.dataSyncId.orEmpty() },
                                                allChannels = channels,
                                            )
                                        }
                                    }.onFailure {
                                        Timber.e(it, "Login: Authentication validation failed")
                                        hasCompletedLogin = false // Allow retry
                                        reportException(it)
                                        // "Allow retry" above did nothing on its own — the WebView
                                        // just sat on the already-loaded page with hasCompletedLogin
                                        // reset, so onPageFinished never refired and the app never
                                        // moved past the login screen. Reload so it does, up to a
                                        // few times in case this is a persistent failure.
                                        val attempt = validationAttempts.incrementAndGet()
                                        if (attempt <= 3) {
                                            android.widget.Toast.makeText(
                                                context,
                                                context.getString(R.string.login_validation_failed_retrying),
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                            webViewRef.value?.reload()
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                context.getString(R.string.login_validation_failed_final),
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        // Google's sign-in flow can detect and block an embedded WebView via
                        // two signals: the "wv" token Android stamps into the default WebView
                        // user agent, and the X-Requested-With header WebView sends with every
                        // request (identifies the calling app package). Clearing both is the
                        // standard fix for the "this browser or app may not be secure" block —
                        // androidx.webkit added setRequestedWithHeaderMode specifically for it.
                        userAgentString = userAgentString.replace("; wv", "")
                    }
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                        // Empty allow-list: no origin (accounts.google.com included) gets the
                        // X-Requested-With header at all.
                        WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, emptySet())
                    }
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onRetrieveVisitorData(newVisitorData: String?) {
                            if (!newVisitorData.isNullOrBlank() && newVisitorData != "null") {
                                // The WebView's JS-to-native bridge can hand this string back
                                // still percent-encoded (seen with trailing "%3D%3D" instead of
                                // the raw "==" base64 padding) — a WebView/Chromium marshaling
                                // quirk, not something YouTube's own page JS does. Sent as-is,
                                // that corrupts the X-Goog-Visitor-Id header on every single
                                // authenticated request. android.net.Uri.decode is a no-op on an
                                // already-clean string, so this is safe either way.
                                val decoded = android.net.Uri.decode(newVisitorData)
                                liveVisitorData.set(decoded)
                                visitorData = decoded
                            }
                        }
                        @JavascriptInterface
                        fun onRetrieveDataSyncId(newDataSyncId: String?) {
                            // Kept whole, not truncated at "||" — that suffix is
                            // what distinguishes a brand/second channel from the
                            // account's primary identity. Stripping it meant the
                            // app could only ever authenticate as the primary
                            // channel no matter which one was active.
                            if (!newDataSyncId.isNullOrBlank() && newDataSyncId != "null") {
                                val decoded = android.net.Uri.decode(newDataSyncId)
                                liveDataSyncId.set(decoded)
                                dataSyncId = decoded
                            }
                        }
                    }, "Android")
                    webViewRef.value = this
                    loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
                }
            }
        )

        TopAppBar(
            title = { Text(stringResource(R.string.login)) },
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
