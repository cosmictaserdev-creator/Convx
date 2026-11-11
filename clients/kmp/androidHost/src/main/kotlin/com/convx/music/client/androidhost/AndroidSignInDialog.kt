package com.convx.music.client.androidhost

import android.net.Uri
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

@Composable
internal fun AndroidSignInDialog(
    accountIsWorking: Boolean,
    onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF171719)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BasicText("Sign in to YouTube Music", style = TextStyle(color = Color.White))
                BasicText("Cancel", style = TextStyle(color = Color.White), modifier = Modifier.clickable(onClick = onDismiss))
            }
            if (accountIsWorking) {
                BasicText(
                    "Checking your YouTube account and saving the session securely…",
                    style = TextStyle(color = Color.White.copy(alpha = 0.82f)),
                )
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(640.dp),
                factory = { context ->
                    val visitorData = AtomicReference("")
                    val dataSyncId = AtomicReference("")
                    var completed = false
                    WebView(context).apply {
                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.userAgentString = settings.userAgentString.replace("; wv", "")
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                            WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, emptySet())
                        }
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun onSessionData(encodedVisitorData: String?, encodedDataSyncId: String?) {
                                encodedVisitorData?.takeIf(String::isNotBlank)
                                    ?.let { visitorData.set(Uri.decode(it)) }
                                encodedDataSyncId?.takeIf(String::isNotBlank)
                                    ?.let { dataSyncId.set(Uri.decode(it)) }
                            }
                        }, "ConvxSignIn")
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView, url: String?) {
                                view.evaluateJavascript(
                                    """(function(){const c=(window.yt&&window.yt.config_)||{};""" +
                                        """window.ConvxSignIn.onSessionData(encodeURIComponent(c.VISITOR_DATA||''),encodeURIComponent(c.DATASYNC_ID||''));})();""",
                                    null,
                                )
                                if (!url.isMusicOrigin() || completed) return
                                val cookie = CookieManager.getInstance().getCookie(MUSIC_URL).orEmpty()
                                if (!cookie.hasYouTubeAccountSession()) return
                                completed = true
                                scope.launch {
                                    var attempts = 0
                                    while (visitorData.get().isBlank() && attempts < MAX_BRIDGE_ATTEMPTS) {
                                        delay(BRIDGE_POLL_INTERVAL_MS)
                                        attempts += 1
                                    }
                                    val visitor = visitorData.get()
                                    if (visitor.isBlank()) {
                                        completed = false
                                        return@launch
                                    }
                                    onComplete(cookie, visitor, dataSyncId.get().takeIf(String::isNotBlank))
                                    clearWebSession()
                                }
                            }
                        }
                        loadUrl(LOGIN_URL)
                    }
                },
            )
        }
    }
}

private fun clearWebSession() {
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()
}

private fun String?.isMusicOrigin(): Boolean =
    this != null && Uri.parse(this).host?.equals(MUSIC_HOST, ignoreCase = true) == true

private fun String.hasYouTubeAccountSession(): Boolean {
    val authenticatedCookies = setOf(
        "SID", "HSID", "SSID", "APISID", "SAPISID", "__Secure-1PSID", "__Secure-3PSID",
        "__Secure-1PAPISID", "__Secure-3PAPISID",
    )
    return split(';').any { it.trim().substringBefore('=') in authenticatedCookies }
}

private const val LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com"
private const val MUSIC_URL = "https://music.youtube.com"
private const val MUSIC_HOST = "music.youtube.com"
private const val MAX_BRIDGE_ATTEMPTS = 50
private const val BRIDGE_POLL_INTERVAL_MS = 100L
