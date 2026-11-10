package com.convx.music.client.desktop

import com.music.innertube.InnertubeWebPoTokenProvider
import com.music.innertube.InnertubeWebPoTokens
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.scene.Scene
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import javafx.stage.Stage
import netscape.javascript.JSObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Runs BotGuard in an isolated JavaFX JVM so WebKit does not share native state with Compose. */
internal class DesktopWebPoTokenProvider(
    private val onFailure: (String) -> Unit = {},
) : InnertubeWebPoTokenProvider {
    override suspend fun generate(videoId: String, visitorData: String): InnertubeWebPoTokens? =
        withContext(Dispatchers.IO) {
            val encodedVideoId = encodeArgument(videoId)
            val encodedVisitorData = encodeArgument(visitorData)
            val outputLines = ConcurrentLinkedQueue<String>()
            val process = runCatching {
                ProcessBuilder(
                    javaExecutable(),
                    "-cp",
                    System.getProperty("java.class.path"),
                    DesktopPoTokenWorker::class.java.name,
                    encodedVideoId,
                    encodedVisitorData,
                ).redirectErrorStream(true).start()
            }.getOrNull() ?: return@withContext null

            val outputReader = Thread({
                process.inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        if (line.startsWith(RESULT_PREFIX) || line.startsWith(ERROR_PREFIX)) outputLines += line
                    }
                }
            }, "convx-web-potoken-output").apply {
                isDaemon = true
                start()
            }

            try {
                if (!process.waitFor(WORKER_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    process.destroyForcibly()
                    return@withContext null
                }
                outputReader.join(1_000)
                if (process.exitValue() != 0) {
                    onFailure("Isolated BotGuard worker exited with code ${process.exitValue()}.")
                    return@withContext null
                }
                val result = outputLines.lastOrNull { it.startsWith(RESULT_PREFIX) }
                if (result == null) {
                    val error = outputLines.lastOrNull { it.startsWith(ERROR_PREFIX) }
                        ?.removePrefix(ERROR_PREFIX)
                        ?: "Isolated BotGuard worker returned no token."
                    onFailure(error)
                    return@withContext null
                }
                val (playerToken, streamToken) = result.removePrefix(RESULT_PREFIX).split('\t', limit = 2)
                if (playerToken.isBlank() || streamToken.isBlank()) null else InnertubeWebPoTokens(playerToken, streamToken)
            } catch (_: InterruptedException) {
                process.destroyForcibly()
                Thread.currentThread().interrupt()
                null
            } finally {
                if (process.isAlive) process.destroyForcibly()
            }
        }

    private fun encodeArgument(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.encodeToByteArray())

    private fun javaExecutable(): String {
        val executableName = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) "javaw.exe" else "java"
        val executable = File(System.getProperty("java.home"), "bin/$executableName")
        return if (executable.isFile) executable.absolutePath else executableName
    }

    private companion object {
        const val RESULT_PREFIX = "CONVX_PO_TOKENS\t"
        const val ERROR_PREFIX = "CONVX_PO_ERROR\t"
        const val WORKER_TIMEOUT_SECONDS = 75L
    }
}

/** Invoked in its own JVM by [DesktopWebPoTokenProvider]. */
internal object DesktopPoTokenWorker {
    @JvmStatic
    fun main(args: Array<String>) {
        val decoder = Base64.getUrlDecoder()
        val videoId = runCatching { decoder.decode(args[0]).decodeToString() }.getOrNull()
        val visitorData = runCatching { decoder.decode(args[1]).decodeToString() }.getOrNull()
        if (videoId.isNullOrBlank() || visitorData.isNullOrBlank()) {
            println("CONVX_PO_ERROR\tInvalid token request.")
            return
        }
        DesktopPoTokenWorkerSession(videoId, visitorData).run()
    }
}

internal class DesktopPoTokenWorkerSession(
    internal val videoId: String,
    internal val visitorData: String,
) {
    private val completed = CountDownLatch(1)
    private var stage: Stage? = null
    private var engine: WebEngine? = null
    private lateinit var bridge: DesktopPoTokenBridge

    fun run() {
        try {
            val html = DesktopPoTokenWorkerSession::class.java.getResourceAsStream("/po_token.html")
                ?.bufferedReader()?.use { it.readText() }
                ?: error("BotGuard page asset is missing.")
            Platform.startup {
                try {
                    Platform.setImplicitExit(false)
                    val webView = WebView()
                    val webEngine = webView.engine
                    engine = webEngine
                    bridge = DesktopPoTokenBridge(this)
                    webEngine.loadWorker.stateProperty().addListener { _, _, state ->
                        when (state) {
                            Worker.State.SUCCEEDED -> {
                                val window = webEngine.executeScript("window") as JSObject
                                window.setMember("PoTokenWorker", bridge)
                                bridge.downloadAndRunBotguard()
                            }
                            Worker.State.FAILED -> fail("BotGuard page could not load.")
                            else -> Unit
                        }
                    }
                    Stage().also { window ->
                        stage = window
                        window.opacity = 0.0
                        window.x = -10_000.0
                        window.y = -10_000.0
                        window.scene = Scene(webView, 320.0, 240.0)
                        window.show()
                    }
                    webEngine.loadContent(html)
                } catch (error: Throwable) {
                    fail(error.message ?: "BotGuard browser initialization failed.")
                }
            }
            if (!completed.await(WORKER_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                fail("BotGuard token generation timed out.")
            }
        } catch (error: Throwable) {
            fail(error.message ?: "BotGuard token generation failed.")
        } finally {
            runCatching { Platform.runLater { stage?.close() } }
            runCatching { Platform.exit() }
        }
    }

    fun execute(script: String) {
        Platform.runLater {
            runCatching { engine?.executeScript(script) }
                .onFailure { fail(it.message ?: "BotGuard JavaScript failed.") }
        }
    }

    fun complete(playerToken: String, streamingToken: String) {
        if (completed.count == 0L) return
        println("CONVX_PO_TOKENS\t$playerToken\t$streamingToken")
        completed.countDown()
    }

    fun fail(message: String) {
        if (completed.count == 0L) return
        println("CONVX_PO_ERROR\t${message.take(400).replace('\t', ' ').replace('\n', ' ')}")
        completed.countDown()
    }

    fun generateToken(index: String, identifier: String) {
        val bytes = identifier.encodeToByteArray().joinToString(",") { (it.toInt() and 0xff).toString() }
        execute(
            """obtainPoToken(new Uint8Array([$bytes])).then(function(token) {
                window.PoTokenWorker.onPoTokenGenerated('$index', Array.from(token).join(','));
            }).catch(function(error) {
                window.PoTokenWorker.onPoTokenError('$index', String(error));
            });""",
        )
    }

    private companion object {
        const val WORKER_TIMEOUT_SECONDS = 60L
    }
}

/** JavaScript bridge exposed to the isolated BotGuard WebView. */
class DesktopPoTokenBridge internal constructor(
    private val session: DesktopPoTokenWorkerSession,
) {
    private val networkExecutor = Executors.newSingleThreadExecutor { task ->
        Thread(task, "convx-botguard-network").apply { isDaemon = true }
    }
    private var streamingToken: String? = null

    fun downloadAndRunBotguard() {
        networkExecutor.execute {
            runCatching {
                val challenge = botguardRequest(CREATE_URL, "[ \"$REQUEST_KEY\" ]")
                val challengeData = parseChallengeData(challenge)
                session.execute(
                    """try {
                        window.data = $challengeData;
                        runBotGuard(window.data).then(function(result) {
                            window.webPoSignalOutput = result.webPoSignalOutput;
                            window.PoTokenWorker.onRunBotguardResult(result.botguardResponse);
                        }, function(error) {
                            window.PoTokenWorker.onJsInitializationError(String(error));
                        });
                    } catch (error) {
                        window.PoTokenWorker.onJsInitializationError(String(error));
                    }""",
                )
            }.onFailure { session.fail(it.message ?: "Could not load BotGuard challenge.") }
        }
    }

    fun onRunBotguardResult(botguardResponse: String) {
        networkExecutor.execute {
            runCatching {
                val response = botguardRequest(GENERATE_IT_URL, "[ \"$REQUEST_KEY\", \"$botguardResponse\" ]")
                val (integrityToken, _) = parseIntegrityTokenData(response)
                session.execute(
                    """try {
                        window.integrityToken = $integrityToken;
                        createPoTokenMinter(window.webPoSignalOutput, window.integrityToken).then(function() {
                            window.PoTokenWorker.onMinterCreated();
                        }).catch(function(error) {
                            window.PoTokenWorker.onJsInitializationError(String(error));
                        });
                    } catch (error) {
                        window.PoTokenWorker.onJsInitializationError(String(error));
                    }""",
                )
            }.onFailure { session.fail(it.message ?: "Could not create the BotGuard token minter.") }
        }
    }

    fun onMinterCreated() {
        session.generateToken("stream", visitorDataFromSession())
    }

    fun onPoTokenGenerated(index: String, bytes: String) {
        val token = runCatching { tokenFromByteList(bytes) }
            .getOrElse {
                session.fail("BotGuard returned an invalid token.")
                return
            }
        when (index) {
            "stream" -> {
                streamingToken = token
                session.generateToken("player", videoIdFromSession())
            }
            "player" -> {
                val stream = streamingToken ?: return session.fail("BotGuard omitted the streaming token.")
                session.complete(token, stream)
                networkExecutor.shutdownNow()
            }
            else -> session.fail("BotGuard returned an unknown token.")
        }
    }

    fun onPoTokenError(index: String, error: String) {
        session.fail("BotGuard token generation failed for $index: $error")
        networkExecutor.shutdownNow()
    }

    fun onJsInitializationError(error: String) {
        session.fail("BotGuard initialization failed: $error")
        networkExecutor.shutdownNow()
    }

    private fun visitorDataFromSession(): String = session.visitorData
    private fun videoIdFromSession(): String = session.videoId

    private companion object {
        const val REQUEST_KEY = "O43z0dpjhgX20SCx4KAo"
        const val GOOGLE_API_KEY = "AIzaSyDyT5W0Jh49F30Pqqtyfdf7pDLFKLJoAnw"
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.3"
        const val CREATE_URL = "https://www.youtube.com/api/jnn/v1/Create"
        const val GENERATE_IT_URL = "https://www.youtube.com/api/jnn/v1/GenerateIT"
    }

    private fun botguardRequest(url: String, body: String): String {
        val connection = URI(url).toURL().openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 15_000
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
            (scrambled[0] as JsonArray)
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
        val data = Json.parseToJsonElement(raw).jsonArray
        val bytes = decodeBase64(data[0].jsonPrimitive.content)
        val jsBytes = bytes.joinToString(",") { (it.toInt() and 0xff).toString() }
        return "new Uint8Array([$jsBytes])" to data[1].jsonPrimitive.long
    }

    private fun descramble(value: String): String =
        decodeBase64(value).map { (it + 97).toByte() }.toByteArray().decodeToString()

    private fun decodeBase64(value: String): ByteArray = Base64.getDecoder().decode(
        value.replace('-', '+').replace('_', '/').replace('.', '='),
    )

    private fun tokenFromByteList(value: String): String {
        val bytes = value.split(',').map { item ->
            val parsed = item.trim().toInt()
            require(parsed in 0..255)
            parsed.toByte()
        }.toByteArray()
        return Base64.getEncoder().encodeToString(bytes).replace('+', '-').replace('/', '_')
    }
}
