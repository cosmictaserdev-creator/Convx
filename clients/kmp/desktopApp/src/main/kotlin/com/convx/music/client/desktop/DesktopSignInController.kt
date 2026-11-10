package com.convx.music.client.desktop

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.Stage
import javafx.util.Duration
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.OutputStream
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.util.Base64
import java.security.SecureRandom
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/** Hosts the browser in another JVM so WebKit cannot destabilize Compose/Skiko. */
internal class DesktopSignInController(
    private val onStatusChanged: (String) -> Unit,
    private val onComplete: (cookie: String, visitorData: String, dataSyncId: String?) -> Unit,
    private val onDismiss: () -> Unit,
) : AutoCloseable {
    private val lock = Any()
    private val token = ByteArray(32).also(SecureRandom()::nextBytes).let {
        Base64.getUrlEncoder().withoutPadding().encodeToString(it)
    }
    private val completed = AtomicBoolean(false)
    private val dismissed = AtomicBoolean(false)
    private val retryAllowed = AtomicBoolean(false)
    private var server: HttpServer? = null
    private var child: Process? = null
    private var capturedSession: DesktopCapturedSession? = null
    private var closed = false

    fun start() = openWindow()

    fun openWindow() {
        synchronized(lock) {
            if (closed) return
            if (retryAllowed.compareAndSet(true, false)) {
                capturedSession?.let { session ->
                    onStatusChanged("Checking your signed-in account again…")
                    onComplete(session.cookie, session.visitorData, session.dataSyncId)
                    return
                }
            }
            if (child?.isAlive == true) return
            if (completed.get() && !retryAllowed.compareAndSet(true, false)) return
            completed.set(false)
            try {
                val callbackServer = server ?: createCallbackServer().also { server = it }
                val java = javaExecutable()
                val classpath = System.getProperty("java.class.path")
                val callbackBaseUrl = "http://127.0.0.1:${callbackServer.address.port}/callback/$token"
                val process = ProcessBuilder(
                    java,
                    "-cp",
                    classpath,
                    DesktopSignInWorker::class.java.name,
                    callbackBaseUrl,
                ).redirectErrorStream(true).start()
                child = process
                onStatusChanged("Sign-in window opened. Complete your Google sign-in there.")
                Thread({ process.inputStream.use { it.copyTo(OutputStream.nullOutputStream()) } }, "convx-sign-in-output").apply {
                    isDaemon = true
                    start()
                }
                Thread({ watchChild(process) }, "convx-sign-in-watch").apply {
                    isDaemon = true
                    start()
                }
            } catch (error: Exception) {
                onStatusChanged("Could not open the sign-in window: ${error.message ?: "desktop browser startup failed"}")
            }
        }
    }

    fun allowRetryAfterFailure() {
        retryAllowed.set(true)
    }

    override fun close() {
        val oldChild: Process?
        val oldServer: HttpServer?
        synchronized(lock) {
            if (closed) return
            closed = true
            oldChild = child
            child = null
            oldServer = server
            server = null
            capturedSession = null
        }
        oldChild?.destroy()
        oldServer?.stop(0)
    }

    private fun createCallbackServer(): HttpServer {
        val httpServer = HttpServer.create(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0)
        httpServer.executor = Executors.newSingleThreadExecutor { task ->
            Thread(task, "convx-sign-in-callback").apply { isDaemon = true }
        }
        httpServer.createContext("/callback/$token") { exchange -> handleCallback(exchange) }
        httpServer.start()
        return httpServer
    }

    private fun handleCallback(exchange: HttpExchange) {
        try {
            if (!exchange.remoteAddress.address.isLoopbackAddress || exchange.requestMethod != "POST") {
                respond(exchange, 403)
                return
            }
            when (exchange.requestURI.rawPath) {
                "/callback/$token/complete" -> handleComplete(exchange)
                "/callback/$token/status" -> {
                    val input = DataInputStream(ByteArrayInputStream(readBounded(exchange, 2_048)))
                    onStatusChanged(input.readUTF().take(1_000))
                    respond(exchange, 200)
                }
                "/callback/$token/cancel" -> {
                    respond(exchange, 200)
                    if (dismissed.compareAndSet(false, true) && !completed.get()) onDismiss()
                }
                else -> respond(exchange, 404)
            }
        } catch (_: Exception) {
            runCatching { respond(exchange, 400) }
        } finally {
            exchange.close()
        }
    }

    private fun handleComplete(exchange: HttpExchange) {
        val declaredLength = exchange.requestHeaders.getFirst("Content-Length")?.toLongOrNull()
        if (declaredLength != null && declaredLength !in 1..MAX_CALLBACK_BYTES.toLong()) {
            respond(exchange, 413)
            return
        }
        val body = readBounded(exchange, MAX_CALLBACK_BYTES)
        val input = DataInputStream(ByteArrayInputStream(body))
        val cookie = input.readUTF()
        val visitorData = input.readUTF()
        val dataSyncId = input.readUTF().takeIf(String::isNotBlank)
        if (input.available() != 0 || cookie.isBlank() || visitorData.isBlank() || !cookie.hasYouTubeAccountSession()) {
            respond(exchange, 422)
            return
        }
        if (!completed.compareAndSet(false, true)) {
            respond(exchange, 409)
            return
        }
        synchronized(lock) { capturedSession = DesktopCapturedSession(cookie, visitorData, dataSyncId) }
        respond(exchange, 200)
        onStatusChanged("Sign-in received. Saving the account securely…")
        onComplete(cookie, visitorData, dataSyncId)
    }

    private fun watchChild(process: Process) {
        val exitCode = runCatching { process.waitFor() }.getOrNull() ?: return
        synchronized(lock) {
            if (child === process) child = null
            if (closed || completed.get() || dismissed.get()) return
            if (retryAllowed.get()) completed.set(false)
        }
        if (exitCode != 0) {
            onStatusChanged("The sign-in window closed unexpectedly. You can open it again.")
        } else {
            onStatusChanged("The sign-in window closed before a session was received. You can open it again.")
        }
    }

    private fun javaExecutable(): String {
        val executableName = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "javaw.exe" else "java"
        val executable = File(System.getProperty("java.home"), "bin/$executableName")
        return if (executable.isFile) executable.absolutePath else executableName
    }

    private fun respond(exchange: HttpExchange, status: Int) {
        exchange.sendResponseHeaders(status, -1)
    }

    private companion object {
        const val MAX_CALLBACK_BYTES = 200_000
    }
}

/** Main class executed only by the isolated sign-in JVM. */
internal object DesktopSignInWorker {
    @JvmStatic
    fun main(args: Array<String>) {
        val callbackBaseUrl = args.singleOrNull() ?: return
        DesktopSignInWebSession(callbackBaseUrl).start()
    }
}

private class DesktopSignInWebSession(
    private val callbackBaseUrl: String,
) {
    private val completed = AtomicBoolean(false)
    private val cookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
    private var previousCookieHandler: CookieHandler? = null
    private var stage: Stage? = null
    private var engine: javafx.scene.web.WebEngine? = null
    private var sessionPoll: PauseTransition? = null

    fun start() {
        previousCookieHandler = CookieHandler.getDefault()
        CookieHandler.setDefault(cookieManager)
        try {
            Platform.startup {
                try {
                    Platform.setImplicitExit(false)
                    val webView = WebView()
                    val webEngine = webView.engine
                    engine = webEngine
                    webEngine.loadWorker.stateProperty().addListener { _, _, state ->
                        if (!completed.get()) when (state) {
                            Worker.State.SUCCEEDED -> {
                                collectSession(webEngine)
                                scheduleSessionPoll(webEngine)
                            }
                            Worker.State.FAILED -> {
                                val details = webEngine.loadWorker.exception?.localizedMessage
                                    ?.replace(Regex("[\\r\\n]+"), " ")?.take(220)
                                val message = details?.takeIf(String::isNotBlank)
                                    ?: "The sign-in page could not be loaded. Check your connection and try again."
                                sendStatus("Sign-in page failed to load: $message")
                                stage?.scene = Scene(VBox(16.0).apply {
                                    style = "-fx-padding: 36; -fx-background-color: #181818;"
                                    children.addAll(
                                        Label("YouTube sign-in could not load").apply {
                                            style = "-fx-font-size: 22px; -fx-text-fill: white;"
                                        },
                                        Label(message).apply {
                                            isWrapText = true
                                            style = "-fx-font-size: 14px; -fx-text-fill: #cccccc;"
                                        },
                                        Button("Retry sign-in").apply {
                                            setOnAction {
                                                stage?.scene = Scene(webView, 1000.0, 720.0)
                                                webEngine.load(LOGIN_URL)
                                            }
                                        },
                                    )
                                }, 1000.0, 720.0)
                            }
                            else -> Unit
                        }
                    }
                    Stage().also { window ->
                        stage = window
                        window.title = "Sign in to YouTube Music — Convx"
                        window.scene = Scene(webView, 1000.0, 720.0)
                        window.setOnCloseRequest { event ->
                            event.consume()
                            cancel()
                        }
                        window.show()
                    }
                    webEngine.load(LOGIN_URL)
                } catch (_: Throwable) {
                    sendStatus("The sign-in browser could not initialize. Rebuild Convx with the desktop browser runtime enabled.")
                    finish()
                }
            }
        } catch (_: Throwable) {
            sendStatus("The sign-in browser runtime could not start.")
            cookieManager.cookieStore.removeAll()
            CookieHandler.setDefault(previousCookieHandler)
            previousCookieHandler = null
        }
    }

    private fun collectSession(webEngine: javafx.scene.web.WebEngine) {
        val host = runCatching { URI(webEngine.location).host }.getOrNull()
        if (!host.equals(MUSIC_HOST, ignoreCase = true)) return
        val visitorData = readScript(webEngine, READ_VISITOR_DATA)
        val dataSyncId = readScript(webEngine, READ_DATA_SYNC_ID)
        val cookie = sequenceOf(MUSIC_URI)
            .flatMap { uri -> cookieManager.cookieStore.get(uri).asSequence() }
            .distinctBy(HttpCookie::getName)
            .joinToString("; ") { "${it.name}=${it.value}" }
        if (!cookie.hasYouTubeAccountSession()) return
        if (visitorData.isBlank()) {
            stage?.title = "Finishing sign-in — waiting for YouTube Music…"
            return
        }
        if (!completed.compareAndSet(false, true)) return

        Thread({
            val sent = post("/complete") {
                writeUTF(cookie)
                writeUTF(visitorData)
                writeUTF(dataSyncId)
            }
            if (sent) Platform.runLater { finish() }
            else {
                completed.set(false)
                Platform.runLater { sendStatus("Could not return the session to Convx. Keep this window open and retry.") }
            }
        }, "convx-sign-in-session-transfer").apply {
            isDaemon = true
            start()
        }
    }

    private fun scheduleSessionPoll(webEngine: javafx.scene.web.WebEngine) {
        if (completed.get() || stage?.isShowing != true) return
        sessionPoll?.stop()
        sessionPoll = PauseTransition(Duration.millis(750.0)).apply {
            setOnFinished {
                if (engine === webEngine && !completed.get()) {
                    collectSession(webEngine)
                    scheduleSessionPoll(webEngine)
                }
            }
            playFromStart()
        }
    }

    private fun cancel() {
        if (!completed.get()) post("/cancel") {}
        finish()
    }

    private fun finish() {
        sessionPoll?.stop()
        sessionPoll = null
        engine?.load(null)
        engine = null
        stage?.hide()
        stage = null
        cookieManager.cookieStore.removeAll()
        CookieHandler.setDefault(previousCookieHandler)
        previousCookieHandler = null
        Platform.exit()
    }

    private fun readScript(webEngine: javafx.scene.web.WebEngine, script: String): String =
        runCatching { webEngine.executeScript(script)?.toString().orEmpty().takeUnless { it == "null" }.orEmpty() }
            .getOrDefault("")

    private fun sendStatus(message: String) {
        post("/status") { writeUTF(message.take(1000)) }
    }

    private fun post(endpoint: String, writeBody: DataOutputStream.() -> Unit): Boolean {
        val connection = runCatching {
            URI("$callbackBaseUrl$endpoint").toURL().openConnection() as HttpURLConnection
        }.getOrNull() ?: return false
        return try {
            connection.connectTimeout = 3_000
            connection.readTimeout = 3_000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            DataOutputStream(connection.outputStream).use(writeBody)
            connection.responseCode in 200..299
        } catch (_: Exception) {
            false
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com"
        const val MUSIC_HOST = "music.youtube.com"
        val READ_VISITOR_DATA = desktopSessionConfigScript("VISITOR_DATA")
        val READ_DATA_SYNC_ID = desktopSessionConfigScript("DATASYNC_ID")
        val MUSIC_URI = URI("https://music.youtube.com")
        val GOOGLE_URI = URI("https://accounts.google.com")
    }
}

private class DesktopCapturedSession(val cookie: String, val visitorData: String, val dataSyncId: String?) {
    override fun toString(): String = "DesktopCapturedSession(<redacted>)"
}

internal fun desktopSessionConfigScript(key: String): String {
    require(key == "VISITOR_DATA" || key == "DATASYNC_ID")
    return """(function() {
        var cfg = window.ytcfg;
        var value = cfg && typeof cfg.get === 'function' ? cfg.get('$key') : null;
        value = value || (cfg && cfg.data_ && cfg.data_['$key']);
        value = value || (window.yt && window.yt.config_ && window.yt.config_['$key']);
        return typeof value === 'string' && value !== 'undefined' && value !== 'null' ? value : '';
    })()""".trimIndent()
}

private fun readBounded(exchange: HttpExchange, maximumBytes: Int): ByteArray {
    val output = ByteArrayOutputStream()
    exchange.requestBody.use { input ->
        val buffer = ByteArray(4096)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            if (output.size() + count > maximumBytes) error("Sign-in callback is too large.")
            output.write(buffer, 0, count)
        }
    }
    return output.toByteArray()
}

private fun String.hasYouTubeAccountSession(): Boolean = split(';').any { part ->
    part.trim().substringBefore('=') in AUTHENTICATED_COOKIE_NAMES
}

private val AUTHENTICATED_COOKIE_NAMES = setOf(
    "SID", "HSID", "SSID", "APISID", "SAPISID", "__Secure-1PSID", "__Secure-3PSID",
    "__Secure-1PAPISID", "__Secure-3PAPISID",
)
