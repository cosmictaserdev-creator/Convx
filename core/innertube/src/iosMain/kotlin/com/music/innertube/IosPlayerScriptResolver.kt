@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.music.innertube

import com.music.innertube.models.YouTubeClient
import com.music.innertube.models.response.PlayerResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.parseQueryString
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Foundation.NSError
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Clock

/** Resolves YouTube player signatures in a private WebKit context on iOS. */
internal object IosPlayerScriptResolver {
    private val mutex = Mutex()
    private val client = HttpClient(Darwin)
    private var cachedScript: PlayerScript? = null

    suspend fun signatureTimestamp(videoId: String): Result<Int> = try {
        require(videoId.isNotBlank()) { "A video id is required to resolve the player script." }
        val timestamp = playerScript().signatureTimestamp
            ?: throw IllegalStateException("The YouTube player script has no signature timestamp.")
        Result.success(timestamp)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        invalidate()
        Result.failure(error)
    }

    suspend fun streamUrl(
        format: PlayerResponse.StreamingData.Format,
        videoId: String,
    ): String? {
        if (videoId.isBlank()) return null
        val directUrl = format.url
        val cipher = format.signatureCipher ?: format.cipher
        if (cipher.isNullOrBlank() && directUrl?.let(THROTTLING_PARAMETER_PATTERN::containsMatchIn) != true) {
            return directUrl
        }

        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val script = playerScript()
                var resultUrl = directUrl
                if (!cipher.isNullOrBlank()) {
                    val cipherParameters = parseQueryString(cipher)
                    val baseUrl = cipherParameters["url"]
                        ?: throw IllegalArgumentException("The stream cipher has no URL.")
                    val signature = cipherParameters["s"]
                        ?: throw IllegalArgumentException("The stream cipher has no encrypted signature.")
                    val signatureName = cipherParameters["sp"]?.takeIf(String::isNotBlank) ?: DEFAULT_SIGNATURE_PARAMETER
                    val decoded = script.webView.transformSignature(signature)
                    resultUrl = URLBuilder(baseUrl).apply { this.parameters[signatureName] = decoded }.buildString()
                }
                val resolvedUrl = resultUrl ?: return null
                val url = URLBuilder(resolvedUrl)
                val throttlingValue = url.parameters[THROTTLING_PARAMETER]
                if (throttlingValue != null) {
                    val decoded = script.webView.transformThrottlingParameter(throttlingValue)
                    url.parameters[THROTTLING_PARAMETER] = decoded
                }
                return url.buildString()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                invalidate()
                if (attempt == MAX_ATTEMPTS - 1) return null
            }
        }
        return null
    }

    private suspend fun playerScript(): PlayerScript = mutex.withLock {
        val now = Clock.System.now().toEpochMilliseconds()
        cachedScript?.takeIf { now < it.expiresAtMillis }?.let { return@withLock it }

        cachedScript?.webView?.close()
        cachedScript = null
        val hash = fetchPlayerHash()
        val source = fetchPlayerScript(hash)
        val webView = IosPlayerScriptWebView.create(source)
        val timestamp = PlayerJsFunctionExtractor.signatureTimestamp(source)
            ?: run {
                webView.close()
                throw IllegalStateException("The YouTube player script has no signature timestamp.")
            }
        PlayerScript(timestamp, now + PLAYER_SCRIPT_CACHE_MS, webView).also { cachedScript = it }
    }

    private suspend fun fetchPlayerHash(): String {
        val response = withTimeout(PLAYER_SCRIPT_REQUEST_TIMEOUT_MS) {
            client.get(IFRAME_API_URL) {
                header("User-Agent", YouTubeClient.USER_AGENT_WEB)
                header("Accept", "*/*")
            }
        }
        check(response.status == HttpStatusCode.OK) {
            "The YouTube player API returned HTTP ${response.status.value}."
        }
        val source = response.bodyAsText()
        return PLAYER_HASH_PATTERN.find(source)?.groupValues?.getOrNull(1)
            ?: throw IllegalStateException("Could not find the YouTube player script version.")
    }

    private suspend fun fetchPlayerScript(hash: String): String {
        val response = withTimeout(PLAYER_SCRIPT_REQUEST_TIMEOUT_MS) {
            client.get("$PLAYER_SCRIPT_URL_PREFIX$hash$PLAYER_SCRIPT_URL_SUFFIX") {
                header("User-Agent", YouTubeClient.USER_AGENT_WEB)
                header("Accept", "*/*")
            }
        }
        check(response.status == HttpStatusCode.OK) {
            "The YouTube player script returned HTTP ${response.status.value}."
        }
        return response.bodyAsText().also { check(it.isNotBlank()) { "The YouTube player script was empty." } }
    }

    private suspend fun invalidate() {
        val old = mutex.withLock { cachedScript.also { cachedScript = null } }
        old?.webView?.close()
    }

    private data class PlayerScript(
        val signatureTimestamp: Int,
        val expiresAtMillis: Long,
        val webView: IosPlayerScriptWebView,
    )

    private const val IFRAME_API_URL = "https://www.youtube.com/iframe_api"
    private const val PLAYER_SCRIPT_URL_PREFIX = "https://www.youtube.com/s/player/"
    private const val PLAYER_SCRIPT_URL_SUFFIX = "/player_ias.vflset/en_GB/base.js"
    private const val PLAYER_SCRIPT_CACHE_MS = 6 * 60 * 60 * 1_000L
    private const val PLAYER_SCRIPT_REQUEST_TIMEOUT_MS = 20_000L
    private const val MAX_ATTEMPTS = 2
    private const val DEFAULT_SIGNATURE_PARAMETER = "signature"
    private const val THROTTLING_PARAMETER = "n"
    private val PLAYER_HASH_PATTERN = Regex("""/s/player/([a-zA-Z0-9_-]+)/""")
    private val THROTTLING_PARAMETER_PATTERN = Regex("[?&]n=")
}

private class IosPlayerScriptWebView private constructor(
    private val playerJs: String,
    private val signatureInfo: SignatureFunctionInfo?,
    private val throttlingInfo: ThrottlingFunctionInfo?,
) {
    private val evaluationMutex = Mutex()
    private val initialized = CompletableDeferred<Unit>()
    private val webView: WKWebView
    private val navigationDelegate: PlayerScriptNavigationDelegate

    init {
        val configuration = WKWebViewConfiguration().apply {
            websiteDataStore = WKWebsiteDataStore.nonPersistentDataStore()
        }
        webView = WKWebView(CGRectMake(0.0, 0.0, 1.0, 1.0), configuration)
        navigationDelegate = PlayerScriptNavigationDelegate(initialized)
        webView.navigationDelegate = navigationDelegate
    }

    suspend fun initialize() {
        val player = exportPlayerFunctions(playerJs, signatureInfo, throttlingInfo)
        val safePlayer = player.replace(Regex("</script", RegexOption.IGNORE_CASE), "<\\/script")
        val page = """<!doctype html><html><head><meta charset="utf-8"><script>
            window.onerror = function(message) { window._convxPlayerError = String(message); };
            $safePlayer
            </script></head><body></body></html>"""
        withContext(Dispatchers.Main.immediate) {
            val baseUrl = NSURL.URLWithString(YOUTUBE_ORIGIN) ?: error("Invalid YouTube origin.")
            webView.loadHTMLString(page, baseUrl)
        }
        withTimeout(WEB_VIEW_LOAD_TIMEOUT_MS) { initialized.await() }
        if (signatureInfo != null) {
            check(evaluate("typeof window._cipherSigFunc === 'function'") == "true") {
                "The YouTube signature function was not available in its player script."
            }
        }
        evaluate(discoverThrottlingFunctionScript)
    }

    suspend fun transformSignature(signature: String): String = evaluationMutex.withLock {
        val info = signatureInfo ?: throw IllegalStateException("The YouTube signature function was not found.")
        val argument = signature.toJsLiteral()
        val invocation = info.constantArgument?.let { "$it, $argument" } ?: argument
        val result = evaluate(
            "(function(){ if(typeof window._cipherSigFunc !== 'function') throw new Error('signature function missing'); " +
                "return String(window._cipherSigFunc($invocation)); })()",
        )
        check(result.isNotBlank()) { "The YouTube signature function returned an empty value." }
        result
    }

    suspend fun transformThrottlingParameter(value: String): String = evaluationMutex.withLock {
        val argument = value.toJsLiteral()
        val result = evaluate(
            "(function(){ if(typeof window._nTransformFunc !== 'function') throw new Error('throttling function missing'); " +
                "return String(window._nTransformFunc($argument)); })()",
        )
        check(result.isNotBlank()) { "The YouTube throttling function returned an empty value." }
        result
    }

    suspend fun close() = evaluationMutex.withLock {
        withContext(Dispatchers.Main.immediate) {
            webView.stopLoading()
            webView.navigationDelegate = null
            webView.loadHTMLString("", null)
        }
    }

    private suspend fun evaluate(script: String): String = withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { continuation ->
            webView.evaluateJavaScript(script) { result, error ->
                if (!continuation.isActive) return@evaluateJavaScript
                when {
                    error != null -> continuation.resumeWithException(
                        IllegalStateException(error.localizedDescription),
                    )
                    result == null -> continuation.resume("")
                    else -> continuation.resume(result.toString())
                }
            }
        }
    }

    private fun exportPlayerFunctions(
        source: String,
        signature: SignatureFunctionInfo?,
        throttling: ThrottlingFunctionInfo?,
    ): String {
        val exports = buildList {
            signature?.let { add("window._cipherSigFunc = typeof ${it.name} !== 'undefined' ? ${it.name} : null;") }
            throttling?.let { info ->
                val function = info.arrayIndex?.let { "${info.name}[$it]" } ?: info.name
                add("window._nTransformFunc = typeof ${info.name} !== 'undefined' ? $function : null;")
            }
        }
        if (exports.isEmpty()) return source
        val injected = "; ${exports.joinToString(" ")} "
        val wrapper = "})(_yt_player);"
        return if (wrapper in source) source.replace(wrapper, "$injected$wrapper") else "$source$injected"
    }

    private val discoverThrottlingFunctionScript = """
        (function() {
            if (typeof window._nTransformFunc === 'function') {
                try {
                    var initial = window._nTransformFunc('T2Xw3pWQ_Wk0xbOg');
                    if (typeof initial === 'string' && initial.indexOf('_w8_') >= 0) return true;
                    window._nTransformFunc = null;
                } catch (error) { window._nTransformFunc = null; }
            }
            var probe = 'T2Xw3pWQ_Wk0xbOg';
            var keys = Object.getOwnPropertyNames(window);
            for (var i = 0; i < keys.length; i++) {
                try {
                    var key = keys[i];
                    if (key.indexOf('webkit') === 0 || key === '_cipherSigFunc' || key === '_nTransformFunc') continue;
                    var fn = window[key];
                    if (typeof fn !== 'function' || fn.length !== 1) continue;
                    var result = fn(probe);
                    if (typeof result === 'string' && result !== probe && result.indexOf('_w8_') >= 0) {
                        window._nTransformFunc = fn;
                        return true;
                    }
                } catch (error) { }
            }
            return typeof window._nTransformFunc === 'function';
        })()
    """.trimIndent()

    companion object {
        private const val YOUTUBE_ORIGIN = "https://www.youtube.com"
        private const val WEB_VIEW_LOAD_TIMEOUT_MS = 30_000L

        suspend fun create(source: String): IosPlayerScriptWebView {
            val session = withContext(Dispatchers.Main.immediate) {
                IosPlayerScriptWebView(
                    playerJs = source,
                    signatureInfo = PlayerJsFunctionExtractor.signatureFunction(source),
                    throttlingInfo = PlayerJsFunctionExtractor.throttlingFunction(source),
                )
            }
            try {
                session.initialize()
                return session
            } catch (failure: Throwable) {
                try {
                    session.close()
                } catch (_: Throwable) {
                    // Keep the initialization failure; the session is not cached or exposed.
                }
                throw failure
            }
        }
    }
}

private class PlayerScriptNavigationDelegate(
    private val initialized: CompletableDeferred<Unit>,
) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        initialized.complete(Unit)
    }

    override fun webView(
        webView: WKWebView,
        didFailProvisionalNavigation: WKNavigation?,
        withError: NSError,
    ) {
        initialized.completeExceptionally(IllegalStateException(withError.localizedDescription))
    }
}

private fun String.toJsLiteral(): String = buildString(length + 2) {
    append('"')
    for (character in this@toJsLiteral) {
        when (character) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> {
                if (character.code < 0x20) {
                    append("\\u")
                    append(character.code.toString(16).padStart(4, '0'))
                } else {
                    append(character)
                }
            }
        }
    }
    append('"')
}
