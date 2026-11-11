@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.convx.music.client.ios

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.window.Dialog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSHTTPCookie
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.darwin.NSObject

@Composable
internal fun IosSignInDialog(
    accountIsWorking: Boolean,
    onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val signInWebView = remember { IosSignInWebView(onComplete) }
    Dialog(onDismissRequest = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BasicText("Sign in to YouTube Music")
                BasicText("Cancel", Modifier.clickable(onClick = onDismiss))
            }
            if (accountIsWorking) {
                BasicText("Checking your YouTube account and saving the session securely…")
            }
            UIKitView(
                modifier = Modifier.fillMaxWidth().height(650.dp),
                factory = signInWebView::create,
                onRelease = { signInWebView.close() },
            )
        }
    }
}

internal fun clearIosWebCookies() {
    val cookieStore = WKWebsiteDataStore.defaultDataStore().httpCookieStore
    cookieStore.getAllCookies { cookies ->
        cookies.orEmpty()
            .filterIsInstance<NSHTTPCookie>()
            .filter { cookie ->
                cookie.domain.isForDomain("youtube.com") || cookie.domain.isForDomain("google.com")
            }
            .forEach { cookie -> cookieStore.deleteCookie(cookie) {} }
    }
}

private class IosSignInWebView(
    private val onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
) {
    private var webView: WKWebView? = null
    private var navigationDelegate: IosSignInNavigationDelegate? = null

    fun create(): WKWebView = WKWebView(
        frame = CGRectMake(0.0, 0.0, 1.0, 1.0),
        configuration = WKWebViewConfiguration(),
    ).also { view ->
        val delegate = IosSignInNavigationDelegate(view, onComplete)
        view.navigationDelegate = delegate
        webView = view
        navigationDelegate = delegate
        val loginUrl = NSURL.URLWithString(LOGIN_URL) ?: error("The YouTube sign-in URL is invalid.")
        view.loadRequest(NSURLRequest.requestWithURL(loginUrl))
    }

    fun close() {
        webView?.stopLoading()
        webView?.navigationDelegate = null
        webView = null
        navigationDelegate = null
        clearIosWebCookies()
    }
}

private class IosSignInNavigationDelegate(
    private val webView: WKWebView,
    private val onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
) : NSObject(), WKNavigationDelegateProtocol {
    private var hasCompletedSignIn = false

    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        if (hasCompletedSignIn || webView.URL?.host?.equals(MUSIC_HOST, ignoreCase = true) != true) return
        hasCompletedSignIn = true
        webView.evaluateJavaScript(READ_ACCOUNT_SCRIPT) { result, _ ->
            val fields = runCatching {
                Json.parseToJsonElement(result as String).jsonArray.map { it.jsonPrimitive.content }
            }.getOrNull()
            val visitorData = fields?.getOrNull(0).orEmpty()
            if (visitorData.isBlank()) {
                hasCompletedSignIn = false
                return@evaluateJavaScript
            }
            webView.configuration.websiteDataStore.httpCookieStore.getAllCookies { cookies ->
                val cookieHeader = cookies.orEmpty()
                    .filterIsInstance<NSHTTPCookie>()
                    .toYouTubeCookieHeader()
                if (!cookieHeader.hasYouTubeAccountSession()) {
                    hasCompletedSignIn = false
                    return@getAllCookies
                }
                val dataSyncId = fields?.getOrNull(1)?.takeIf(String::isNotBlank)
                onComplete(cookieHeader, visitorData, dataSyncId)
                clearIosWebCookies()
            }
        }
    }
}

private fun List<NSHTTPCookie>.toYouTubeCookieHeader(): String =
    asSequence()
        .filter { cookie ->
            cookie.domain.isForDomain("youtube.com") || cookie.domain.isForDomain("google.com")
        }
        .joinToString("; ") { cookie -> "${cookie.name}=${cookie.value}" }

private fun String.isForDomain(domain: String): Boolean {
    val normalizedDomain = trimStart('.').lowercase()
    return normalizedDomain == domain || normalizedDomain.endsWith(".$domain")
}

private fun String.hasYouTubeAccountSession(): Boolean =
    split(';').any { it.trim().substringBefore('=') in AUTHENTICATED_COOKIE_NAMES }

private const val LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com"
private const val MUSIC_HOST = "music.youtube.com"
private const val READ_ACCOUNT_SCRIPT =
    "(function(){var c=(window.yt&&window.yt.config_)||{};return JSON.stringify([c.VISITOR_DATA||'',c.DATASYNC_ID||'']);})()"

private val AUTHENTICATED_COOKIE_NAMES = setOf(
    "SID", "HSID", "SSID", "APISID", "SAPISID", "__Secure-1PSID", "__Secure-3PSID",
    "__Secure-1PAPISID", "__Secure-3PAPISID",
)
