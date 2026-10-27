/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TopAppBar
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
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.DataSyncIdKey
import com.convx.music.constants.InnerTubeCookieKey
import com.convx.music.constants.SavedAccount
import com.convx.music.constants.VisitorDataKey
import com.convx.music.ui.component.IconButton
import com.convx.music.ui.utils.backToMain
import com.convx.music.ui.component.FrostedTopBar
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.utils.rememberPreference
import com.convx.music.utils.reportException
import com.convx.music.viewmodels.AccountSettingsViewModel
import org.koin.androidx.compose.koinViewModel
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
    val accountSettingsViewModel: AccountSettingsViewModel = koinViewModel()

    // Read bridge values directly; DataStore may still return blank visitorData or dataSyncId
    // immediately after login.
    val liveVisitorData = remember { AtomicReference("") }
    val liveDataSyncId = remember { AtomicReference("") }

    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    // Bound retries so accounts without a YouTube channel cannot trigger endless requests.
    val validationAttempts = remember { AtomicInteger(0) }

    // Keep Chromium's hardware surface out of Haze captures; repeated capture can blank or
    // flicker the sign-in page.
    Column(
        modifier = Modifier.fillMaxSize().background(AppTheme.colorScheme.background),
    ) {
        FrostedTopBar(
            title = stringResource(R.string.login),
            titleAlpha = 1f,
            onBack = navController::navigateUp,
            onBackLongClick = navController::backToMain,
        )
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)),
            factory = { webViewContext ->
                WebView(webViewContext).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                            loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")

                            if (url?.startsWith("https://music.youtube.com") == true && !hasCompletedLogin) {
                                val cookie = CookieManager.getInstance().getCookie(url) ?: return
                                // Guest pages can set visitor cookies, so require an authenticated account.
                                if (!cookie.hasYouTubeAccountSession()) return
                                hasCompletedLogin = true

                                coroutineScope.launch {
                                    // Wait for bridge values instead of a fixed delay; missing values break later requests.
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

                                    innerTubeCookie = cookie

                                    YouTube.cookie = cookie
                                    YouTube.dataSyncId = liveDataSyncId.get().ifBlank { null }
                                    YouTube.visitorData = newVisitorData

                                    Timber.d("Login: YouTube object initialized, validating...")

                                    // Use channel lookup because some accounts lack accountInfo's required renderer.
                                    // An empty channel list is valid; only a request or parse failure rejects login.
                                    YouTube.getAccountChannels().mapCatching { raw ->
                                        raw.mapNotNull { c ->
                                            c.dataSyncId?.let { id ->
                                                SavedAccount(id, c.name, c.channelHandle, c.thumbnailUrl)
                                            }
                                        }
                                    }.onSuccess { channels ->
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
                                            // Prefer the login-page identity; use endpoint data only if it is blank.
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
                                        // Resetting this flag won't rerun onPageFinished; reload to retry validation.
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
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        // Remove WebView identifiers used by Google sign-in checks.
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
                                // Decode bridge values before sending them in authenticated requests.
                                val decoded = android.net.Uri.decode(newVisitorData)
                                liveVisitorData.set(decoded)
                                visitorData = decoded
                            }
                        }
                        @JavascriptInterface
                        fun onRetrieveDataSyncId(newDataSyncId: String?) {
                            // Preserve the `||` suffix because it identifies alternate channels.
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

    }

    BackHandler(enabled = webViewRef.value?.canGoBack() == true) {
        webViewRef.value?.goBack()
    }
}

private fun String.hasYouTubeAccountSession(): Boolean {
    if (isBlank()) return false
    val authenticatedCookieNames = setOf(
        "SID",
        "HSID",
        "SSID",
        "APISID",
        "SAPISID",
        "__Secure-1PSID",
        "__Secure-3PSID",
        "__Secure-1PAPISID",
        "__Secure-3PAPISID",
    )
    return split(';').any { part ->
        part.trim().substringBefore('=') in authenticatedCookieNames
    }
}
