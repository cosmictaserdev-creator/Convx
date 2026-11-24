package com.convx.music.client.androidhost

import android.content.Context
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.music.innertube.InnertubeWebPoTokenProvider
import com.music.innertube.InnertubeWebPoTokens
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/** Android WebView implementation of the BotGuard token contract used by the shared resolver. */
internal class AndroidWebPoTokenProvider(context: Context) : InnertubeWebPoTokenProvider {
    private val appContext = context.applicationContext
    private val mutex = Mutex()
    private var tokenSession: AndroidBotGuardSession? = null

    override suspend fun generate(videoId: String, visitorData: String): InnertubeWebPoTokens? {
        if (videoId.isBlank() || visitorData.isBlank()) return null

        return mutex.withLock {
            try {
                var activeSession = tokenSession
                if (activeSession == null || activeSession.visitorData != visitorData || activeSession.isExpired) {
                    activeSession?.close()
                    activeSession = AndroidBotGuardSession(appContext, visitorData).also {
                        tokenSession = it
                        it.initialize()
                    }
                }

                val streamingToken = activeSession.streamingToken
                    ?: activeSession.generateToken(visitorData).also { activeSession.streamingToken = it }
                val playerToken = activeSession.generateToken(videoId)
                InnertubeWebPoTokens(playerRequest = playerToken, streamingData = streamingToken)
            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.w(TAG, "Android BotGuard token generation failed", error)
                tokenSession?.close()
                tokenSession = null
                null
            }
        }
    }

    /** Called by the Android host when its playback stack is disposed. */
    fun close() {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            mutex.withLock {
                tokenSession?.close()
                tokenSession = null
            }
        }
    }

    private companion object {
        const val TAG = "AndroidWebPoToken"
    }
}

private class AndroidBotGuardSession(
    private val context: Context,
    val visitorData: String,
) {
    private val bridgeScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val initialized = CompletableDeferred<Unit>()
    private val nextRequestId = AtomicInteger(0)
    private val tokenRequests = ConcurrentHashMap<Int, CompletableDeferred<String>>()
    private val bridge = AndroidPoTokenBridge(this)
    private var webView: WebView? = null
    var streamingToken: String? = null
    private var expiresAtMillis = Long.MAX_VALUE

    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAtMillis

    suspend fun initialize() {
        val html = withContext(Dispatchers.IO) {
            context.assets.open("po_token.html").bufferedReader().use { it.readText() }
        }

        withContext(Dispatchers.Main.immediate) {
            val view = WebView(context)
            webView = view
            view.settings.javaScriptEnabled = true
            view.settings.blockNetworkLoads = true
            view.settings.userAgentString = USER_AGENT
            view.addJavascriptInterface(bridge, JAVASCRIPT_BRIDGE)
            val bootstrap = html.replaceFirst(
                "</script>",
                "\n$JAVASCRIPT_BRIDGE.downloadAndRunBotguard()</script>",
            )
            view.loadDataWithBaseURL("https://www.youtube.com", bootstrap, "text/html", "utf-8", null)
        }

        withTimeout(INITIALIZATION_TIMEOUT_MS) { initialized.await() }
    }

    suspend fun generateToken(identifier: String): String {
        withTimeout(INITIALIZATION_TIMEOUT_MS) { initialized.await() }
        val requestId = nextRequestId.incrementAndGet()
        val result = CompletableDeferred<String>()
        tokenRequests[requestId] = result

        try {
            val byteList = identifier.encodeToByteArray()
                .joinToString(",") { (it.toInt() and 0xff).toString() }
            evaluateJavascript(
                """try {
                    obtainPoToken(new Uint8Array([$byteList])).then(function(token) {
                        window.$JAVASCRIPT_BRIDGE.onTokenGenerated($requestId, Array.from(token).join(','));
                    }).catch(function(error) {
                        window.$JAVASCRIPT_BRIDGE.onTokenFailed($requestId, String(error));
                    });
                } catch (error) {
                    window.$JAVASCRIPT_BRIDGE.onTokenFailed($requestId, String(error));
                }""",
            )
            return withTimeout(TOKEN_TIMEOUT_MS) { result.await() }
        } finally {
            tokenRequests.remove(requestId)
        }
    }

    fun completeToken(requestId: Int, bytes: String) {
        val decoded = runCatching {
            val values = bytes.split(',').map { it.trim().toInt().also { value -> require(value in 0..255) } }
            val raw = values.map(Int::toByte).toByteArray()
            Base64.encodeToString(raw, Base64.URL_SAFE or Base64.NO_WRAP)
        }
        decoded.fold(
            onSuccess = { tokenRequests[requestId]?.complete(it) },
            onFailure = { tokenRequests[requestId]?.completeExceptionally(it) },
        )
    }

    fun failToken(requestId: Int, message: String) {
        tokenRequests[requestId]?.completeExceptionally(IllegalStateException(message.take(MAX_ERROR_LENGTH)))
    }

    fun failInitialization(message: String) {
        val error = IllegalStateException(message.take(MAX_ERROR_LENGTH))
        initialized.completeExceptionally(error)
        tokenRequests.values.forEach { it.completeExceptionally(error) }
    }

    suspend fun evaluateJavascript(script: String) {
        withContext(Dispatchers.Main.immediate) {
            val view = checkNotNull(webView) { "BotGuard WebView has been closed." }
            view.evaluateJavascript(script, null)
        }
    }

    fun close() {
        bridgeScope.cancel()
        val view = webView
        webView = null
        if (view != null) {
            val dispose = {
                view.stopLoading()
                view.removeJavascriptInterface(JAVASCRIPT_BRIDGE)
                view.loadUrl("about:blank")
                view.removeAllViews()
                view.destroy()
            }
            if (Looper.myLooper() == Looper.getMainLooper()) dispose() else {
                CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch { dispose() }
            }
        }
        val cancellation = kotlinx.coroutines.CancellationException("BotGuard session closed.")
        initialized.completeExceptionally(cancellation)
        tokenRequests.values.forEach { it.completeExceptionally(cancellation) }
        tokenRequests.clear()
    }

    private companion object {
        const val JAVASCRIPT_BRIDGE = "ConvxPoToken"
        const val INITIALIZATION_TIMEOUT_MS = 60_000L
        const val TOKEN_TIMEOUT_MS = 30_000L
        const val TOKEN_EXPIRY_SAFETY_SECONDS = 10 * 60L
        const val MIN_TOKEN_LIFETIME_SECONDS = 60L
        const val MAX_ERROR_LENGTH = 500
        const val REQUEST_KEY = "O43z0dpjhgX20SCx4KAo"
        const val GOOGLE_API_KEY = "AIzaSyDyT5W0Jh49F30Pqqtyfdf7pDLFKLJoAnw"
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.3"
        const val CREATE_URL = "https://www.youtube.com/api/jnn/v1/Create"
        const val GENERATE_IT_URL = "https://www.youtube.com/api/jnn/v1/GenerateIT"
    }

    private fun requestBotGuard(url: String, body: String): String {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 20_000
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("User-Agent", USER_AGENT)
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Content-Type", "application/json+protobuf")
        connection.setRequestProperty("x-goog-api-key", GOOGLE_API_KEY)
        connection.setRequestProperty("x-user-agent", "grpc-web-javascript/0.1")
        val requestBytes = body.encodeToByteArray()
        connection.setFixedLengthStreamingMode(requestBytes.size)
        return try {
            connection.outputStream.use { it.write(requestBytes) }
            val status = connection.responseCode
            val responseStream = if (status == HttpURLConnection.HTTP_OK) connection.inputStream else connection.errorStream
            val response = responseStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            check(status == HttpURLConnection.HTTP_OK) { "BotGuard request returned HTTP $status" }
            response
        } finally {
            connection.disconnect()
        }
    }

    private fun parseChallengeData(raw: String): String {
        val scrambled = Json.parseToJsonElement(raw).jsonArray
        val challenge = if (scrambled.size > 1 && (scrambled[1] as? JsonPrimitive)?.isString == true) {
            Json.parseToJsonElement(descramble(scrambled[1].jsonPrimitive.content)).jsonArray
        } else {
            scrambled[0].jsonArray
        }
        val safeScript = (challenge[1] as? JsonArray)
            ?.firstOrNull { (it as? JsonPrimitive)?.isString == true } ?: JsonNull
        val trustedResourceUrl = (challenge[2] as? JsonArray)
            ?.firstOrNull { (it as? JsonPrimitive)?.isString == true } ?: JsonNull
        return Json.encodeToString(
            JsonObject.serializer(),
            JsonObject(
                mapOf(
                    "messageId" to challenge[0],
                    "interpreterJavascript" to JsonObject(
                        mapOf(
                            "privateDoNotAccessOrElseSafeScriptWrappedValue" to safeScript,
                            "privateDoNotAccessOrElseTrustedResourceUrlWrappedValue" to trustedResourceUrl,
                        ),
                    ),
                    "interpreterHash" to challenge[3],
                    "program" to challenge[4],
                    "globalName" to challenge[5],
                    "clientExperimentsStateBlob" to challenge[7],
                ),
            ),
        )
    }

    private fun parseIntegrityTokenData(raw: String): Pair<String, Long> {
        val fields = Json.parseToJsonElement(raw).jsonArray
        val encoded = fields[0].jsonPrimitive.content
        val normalized = encoded.replace('-', '+').replace('_', '/').replace('.', '=')
        val bytes = Base64.decode(normalized, Base64.DEFAULT)
        val jsBytes = "new Uint8Array([" + bytes.joinToString(",") { (it.toInt() and 0xff).toString() } + "])"
        return jsBytes to fields[1].jsonPrimitive.long
    }

    private fun descramble(value: String): String {
        val normalized = value.replace('-', '+').replace('_', '/').replace('.', '=')
        return Base64.decode(normalized, Base64.DEFAULT).map { (it + 97).toByte() }.toByteArray().decodeToString()
    }

    private inner class AndroidPoTokenBridge(private val session: AndroidBotGuardSession) {
        @JavascriptInterface
        fun downloadAndRunBotguard() {
            bridgeScope.launch {
                runCatching {
                    val challenge = requestBotGuard(CREATE_URL, "[ \"$REQUEST_KEY\" ]")
                    val payload = parseChallengeData(challenge)
                    session.evaluateJavascript(
                        """try {
                            window.data = $payload;
                            runBotGuard(window.data).then(function(result) {
                                window.webPoSignalOutput = result.webPoSignalOutput;
                                window.$JAVASCRIPT_BRIDGE.onRunBotguardResult(result.botguardResponse);
                            }, function(error) {
                                window.$JAVASCRIPT_BRIDGE.onInitializationFailed(String(error));
                            });
                        } catch (error) {
                            window.$JAVASCRIPT_BRIDGE.onInitializationFailed(String(error));
                        }""",
                    )
                }.onFailure { session.failInitialization("Could not load BotGuard challenge: ${it.message}") }
            }
        }

        @JavascriptInterface
        fun onRunBotguardResult(botguardResponse: String) {
            bridgeScope.launch {
                runCatching {
                    val response = requestBotGuard(
                        GENERATE_IT_URL,
                        "[ \"$REQUEST_KEY\", ${JsonPrimitive(botguardResponse)} ]",
                    )
                    val (integrityToken, lifetimeSeconds) = parseIntegrityTokenData(response)
                    expiresAtMillis = System.currentTimeMillis() +
                        (lifetimeSeconds - TOKEN_EXPIRY_SAFETY_SECONDS)
                            .coerceAtLeast(MIN_TOKEN_LIFETIME_SECONDS) * 1_000L
                    session.evaluateJavascript(
                        """try {
                            window.integrityToken = $integrityToken;
                            createPoTokenMinter(window.webPoSignalOutput, window.integrityToken).then(function() {
                                window.$JAVASCRIPT_BRIDGE.onMinterCreated();
                            }).catch(function(error) {
                                window.$JAVASCRIPT_BRIDGE.onInitializationFailed(String(error));
                            });
                        } catch (error) {
                            window.$JAVASCRIPT_BRIDGE.onInitializationFailed(String(error));
                        }""",
                    )
                }.onFailure { session.failInitialization("Could not create the BotGuard token minter: ${it.message}") }
            }
        }

        @JavascriptInterface
        fun onMinterCreated() {
            initialized.complete(Unit)
        }

        @JavascriptInterface
        fun onTokenGenerated(requestId: Int, bytes: String) {
            session.completeToken(requestId, bytes)
        }

        @JavascriptInterface
        fun onTokenFailed(requestId: Int, error: String) {
            session.failToken(requestId, "BotGuard token generation failed: $error")
        }

        @JavascriptInterface
        fun onInitializationFailed(error: String) {
            session.failInitialization("BotGuard initialization failed: $error")
        }
    }
}
