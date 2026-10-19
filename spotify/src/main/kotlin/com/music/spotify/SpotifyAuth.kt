package com.music.spotify

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.music.spotify.models.SpotifyInternalToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json

/** Web player token capture adapted from BitChord's Android SpotifyToken, GPL-3.0. */
object SpotifyAuth {
    const val LOGIN_URL = "https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F"
    private const val ORIGIN = "https://open.spotify.com"
    private const val BRIDGE = "ConvxSpotifyToken"
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    @Volatile private var appContext: Context? = null

    fun initialize(context: Context) { appContext = context.applicationContext }

    fun browserUserAgent(agent: String): String = agent.replace("; wv", "")
        .replace(Regex("\\s*Version/\\d+(?:\\.\\d+)*"), "").trim()

    internal fun parseWebPlayerToken(payload: String, nowMillis: Long = System.currentTimeMillis()): SpotifyInternalToken? =
        runCatching { json.decodeFromString<SpotifyInternalToken>(payload) }.getOrNull()?.takeIf {
            !it.isAnonymous && it.accessToken.isNotBlank() && it.accessTokenExpirationTimestampMs > nowMillis
        }

    suspend fun clearWebSession() = withContext(Dispatchers.Main) {
        val cookies = CookieManager.getInstance()
        for (origin in listOf(ORIGIN, "https://accounts.spotify.com", "https://spotify.com")) {
            cookies.getCookie(origin).orEmpty().split(';').map { it.substringBefore('=').trim() }
                .filter(String::isNotBlank).forEach { name ->
                    val expired = "$name=; Max-Age=0; Path=/; Secure"
                    cookies.setCookie(origin, expired)
                    cookies.setCookie(origin, "$expired; Domain=.spotify.com")
                }
        }
        cookies.flush()
    }

    suspend fun fetchAccessToken(spDc: String, spKey: String = ""): Result<SpotifyInternalToken> = try {
        require(spDc.isNotBlank() && spDc.none { it == ';' || it == '\r' || it == '\n' }) { "Invalid Spotify session" }
        val context = checkNotNull(appContext) { "Spotify sign-in is not initialized" }
        Result.success(mutex.withLock { withContext(Dispatchers.Main) { harvest(context, spDc, spKey) } })
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun harvest(context: Context, spDc: String, spKey: String): SpotifyInternalToken {
        val result = CompletableDeferred<SpotifyInternalToken>()
        val cookies = CookieManager.getInstance().apply { setAcceptCookie(true) }
        suspend fun setCookie(value: String) {
            val ready = CompletableDeferred<Boolean>()
            cookies.setCookie(ORIGIN, "$value; Domain=.spotify.com; Path=/; Secure") { ready.complete(it) }
            check(ready.await()) { "Unable to save the Spotify browser session" }
        }
        setCookie("sp_dc=$spDc")
        if (spKey.isNotBlank() && spKey.none { it == ';' || it == '\r' || it == '\n' }) setCookie("sp_key=$spKey")
        cookies.flush()
        WebStorage.getInstance().deleteOrigin(ORIGIN)
        val view = WebView(context)
        try {
            view.settings.javaScriptEnabled = true
            view.settings.domStorageEnabled = true
            // The desktop player boots the token client even on phones; the
            // mobile landing page can show an app download prompt instead.
            view.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
            cookies.setAcceptThirdPartyCookies(view, true)
            check(WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) { "Please update Android System WebView to connect Spotify" }
            WebViewCompat.addWebMessageListener(view, BRIDGE, setOf(ORIGIN)) { _, message, origin, mainFrame, _ ->
                if (!mainFrame || origin.toString().trimEnd('/') != ORIGIN || result.isCompleted) return@addWebMessageListener
                parseWebPlayerToken(message.data.orEmpty())?.let(result::complete)
            }
            val hook = tokenHook
            val startScript = if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT))
                WebViewCompat.addDocumentStartJavaScript(view, hook, setOf(ORIGIN)) else null
            view.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
                    request.isForMainFrame && (request.url.scheme != "https" ||
                        request.url.host?.let { it != "open.spotify.com" && it != "accounts.spotify.com" } != false)

                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    if (Uri.parse(url.orEmpty()).host == "open.spotify.com") view.evaluateJavascript(hook, null)
                }
                override fun onPageFinished(view: WebView, url: String?) {
                    if (Uri.parse(url.orEmpty()).host == "open.spotify.com") view.evaluateJavascript(hook, null)
                }
            }
            view.loadUrl("$ORIGIN/")
            return try {
                withTimeoutOrNull(30_000) { result.await() }
                    ?: throw Spotify.SpotifyException(503, "Spotify could not finish connecting. Check your connection and sign in again.")
            } finally { startScript?.remove() }
        } finally {
            view.stopLoading()
            view.loadUrl("about:blank")
            view.destroy()
            result.cancel()
        }
    }

    private val tokenHook = """
        (function() {
          if (location.origin !== '$ORIGIN' || window.__convxSpotifyHook) return;
          window.__convxSpotifyHook = true;
          function report(body) { try { window.$BRIDGE.postMessage(body); } catch (_) {} }
          function isToken(url) { try { var u = new URL(String(url), location.href); return u.origin === '$ORIGIN' && u.pathname === '/api/token'; } catch (_) { return false; } }
          var fetchOriginal = window.fetch;
          window.fetch = function(input) {
            var response = fetchOriginal.apply(this, arguments);
            if (isToken(input && input.url ? input.url : input)) response.then(function(r) { r.clone().text().then(report).catch(function() {}); }).catch(function() {});
            return response;
          };
          var open = XMLHttpRequest.prototype.open, send = XMLHttpRequest.prototype.send;
          XMLHttpRequest.prototype.open = function(method, url) { this.__convxTokenUrl = url; return open.apply(this, arguments); };
          XMLHttpRequest.prototype.send = function() {
            var xhr = this;
            xhr.addEventListener('load', function() { if (isToken(xhr.__convxTokenUrl)) { try { report(xhr.responseText); } catch (_) {} } });
            return send.apply(this, arguments);
          };
          try { var session = document.getElementById('session'); if (session) report(session.textContent); } catch (_) {}
        })();
    """.trimIndent()
}
