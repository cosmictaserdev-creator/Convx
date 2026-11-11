/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.screens.settings

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.convx.music.LocalPlayerAwareWindowInsets
import com.convx.music.R
import com.convx.music.constants.DiscordTokenKey
import com.convx.music.ui.utils.backToMain
import com.convx.music.utils.rememberPreference
import org.json.JSONTokener

// Discord's web app removes window.localStorage from the page; an about:blank iframe
// shares the origin and still exposes the real storage.
private const val DISCORD_TOKEN_SCRIPT = """(() => {
    const read = () => {
        try { const v = window.localStorage.getItem('token'); if (v) return v; } catch (_) {}
        let frame;
        try {
            frame = document.body.appendChild(document.createElement('iframe'));
            return frame.contentWindow.localStorage.getItem('token') || '';
        } catch (_) { return ''; } finally { if (frame) frame.remove(); }
    };
    const value = read();
    if (!value) return '';
    try { return JSON.parse(value); } catch (_) { return value; }
})()"""

private const val MOTOROLA = "motorola"
private const val SAMSUNG_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 14; SM-S921U; Build/UP1A.231005.007) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Mobile Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscordLoginScreen(navController: NavController) {
    val discordTokenPref = rememberPreference(DiscordTokenKey, "")
    var webView by remember { mutableStateOf<WebView?>(null) }
    var tokenCaptured by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.apply {
                stopLoading()
                loadUrl("about:blank")
                removeAllViews()
                destroy()
            }
            webView = null
        }
    }

    SettingsScaffold(
        navController = navController,
        title = stringResource(R.string.action_login),
        // A login form, not a feed — nothing scrolls under the bar, so the
        // compact title is simply always there.
        titleAlpha = 1f,
        pageColor = AppTheme.colorScheme.background,
    ) {
        AndroidView(
            // Keep WebView out of the backdrop capture; recording it causes flicker.
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.setSupportMultipleWindows(false)
                    // Discord's 2FA challenge (and its captcha widget) runs on a
                    // separate origin and needs third-party cookies to complete —
                    // WebView blocks those by default, so 2FA accounts got stuck
                    // partway through login with no visible error.
                    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                    // Motorola WebView needs the Samsung user agent to complete Discord login.
                    // See: https://github.com/dead8309/Kizzy/issues/345#issuecomment-2699729072
                    settings.userAgentString = discordBrowserUserAgent(
                        if (Build.MANUFACTURER.equals(MOTOROLA, ignoreCase = true)) SAMSUNG_USER_AGENT
                        else settings.userAgentString,
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            super.onPageFinished(view, url)
                            val uri = url?.let { runCatching { Uri.parse(it) }.getOrNull() }
                            if (tokenCaptured || uri?.host != "discord.com" || uri.path?.trimEnd('/') != "/app") return

                            // Wait until Discord's app shell has finished loading before reading
                            // its own-origin storage. Keep the page visible if the token is not
                            // available yet so the user can recover or retry the login.
                            var attempts = 0
                            val captureTask = object : Runnable {
                                override fun run() {
                                    if (tokenCaptured || view.url?.let { current ->
                                            runCatching { Uri.parse(current) }.getOrNull()?.let {
                                                it.host == "discord.com" && it.path?.trimEnd('/') == "/app"
                                            }
                                        } != true
                                    ) return

                                    attempts++
                                    view.evaluateJavascript(DISCORD_TOKEN_SCRIPT) { encodedValue ->
                                        if (tokenCaptured) return@evaluateJavascript
                                        val storedValue = runCatching {
                                            JSONTokener(encodedValue.orEmpty()).nextValue() as? String
                                        }.getOrNull().orEmpty()
                                        val token = runCatching {
                                            JSONTokener(storedValue).nextValue() as? String
                                        }.getOrNull() ?: storedValue.removeSurrounding("\"")
                                        if (token.isNotBlank() && token != "null" && token != "undefined") {
                                            tokenCaptured = true
                                            discordTokenPref.value = token
                                            navController.navigateUp()
                                        } else if (attempts < 12) {
                                            view.postDelayed(this, 500)
                                        }
                                    }
                                }
                            }
                            view.post(captureTask)
                        }
                    }

                    webView = this
                    loadUrl("https://discord.com/login")
                }
            }
        )
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

internal fun discordBrowserUserAgent(agent: String?): String =
    (agent?.takeIf(String::isNotBlank) ?: SAMSUNG_USER_AGENT)
        .replace("; wv", "")
        .replace(Regex("\\s*Version/\\d+(?:\\.\\d+)*"), "")
        .trim()
