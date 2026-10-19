package com.music.spine

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.io.File
import java.net.URI
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/** Hosts the searchTracks / getTrackStreamUrl contract. Reuse [shared] for playback. */
class ModuleManager {
    private val json = Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false }
    private val indexes = SharedModuleCalls<List<SpineModule>>(10 * 60_000L)
    private val loads = SharedModuleCalls<LoadedModule>(0)
    private val searches = SharedModuleCalls<ModuleSearchResponse>(60_000L)
    private val streams = SharedModuleCalls<ModuleStreamResponse>(30_000L)
    private val liveStreams = SharedModuleCalls<ModuleStreamResponse>(0)
    private val loadedModules = ConcurrentHashMap<String, LoadedModule>()
    private val addonManifests = ConcurrentHashMap<String, ModuleManifest>()
    private val rateLimits = ConcurrentHashMap<String, Long>()

    data class LoadedModule(
        val module: SpineModule,
        val jsCode: String,
        val baseUrl: String,
        val engineId: String,
        val manifest: ModuleManifest = ModuleManifest(),
    )

    suspend fun fetchIndex(sourceUrl: String, refresh: Boolean = false): Result<List<SpineModule>> {
        if (refresh) indexes.remove(sourceUrl)
        return indexes.get(sourceUrl) {
            catching {
                require(EclipseAddon.isAllowedUrl(sourceUrl)) { "Module index must be https" }
                var resolvedSource = sourceUrl.trimEnd('/')
                var body = try { download(resolvedSource) } catch (error: Exception) {
                    if (error is CancellationException || resolvedSource.endsWith(".json")) throw error
                    resolvedSource = "$resolvedSource/manifest.json"
                    download(resolvedSource)
                }
                if (!EclipseAddon.isManifest(json, body) && !resolvedSource.endsWith(".json") && !body.trimStart().startsWith("{")) {
                    resolvedSource = "$resolvedSource/manifest.json"
                    body = download(resolvedSource)
                }
                // An Eclipse addon's manifest URL installs that one addon as a module.
                if (EclipseAddon.isManifest(json, body)) {
                    val module = EclipseAddon.toModule(json, resolvedSource, body).copy(sourceUrl = sourceUrl)
                    addonManifests[module.download] = EclipseAddon.moduleManifest(json, body)
                    return@catching listOf(module)
                }
                ModuleIndex.parseModules(json, body).map { module ->
                    fun imageUrl(value: String?): String? = value?.takeIf(String::isNotBlank)?.let {
                        URI(sourceUrl).resolve(it).toString()
                    }
                    module.copy(
                        download = URI(sourceUrl).resolve(module.download).toString(),
                        logo = imageUrl(module.logo),
                        icon = imageUrl(module.icon),
                        sourceUrl = sourceUrl,
                    )
                }
            }
        }
    }

    suspend fun loadModule(
        module: SpineModule,
        resolveBaseUrl: suspend (String) -> String = { it },
    ): Result<LoadedModule> = catching {
        if (module.type == EclipseAddon.TYPE) {
            val manifest = addonManifests[module.download] ?: EclipseAddon.moduleManifest(json, download("${module.download}/manifest.json"))
                .also { addonManifests[module.download] = it }
            return@catching LoadedModule(module, "", module.download, digest("eclipse|${module.download}|${module.version}"), manifest)
        }
        val downloadUrl = if (module.download.startsWith("https://")) module.download else {
            val base = resolveBaseUrl(module.download).trimEnd('/') + "/"
            URI(base).resolve(module.download).toString()
        }
        require(URI(downloadUrl).scheme == "https") { "Module download must be https" }
        val engineId = digest("$downloadUrl|${module.version}|${module.code}|${module.lastUpdated}")
        loadedModules[engineId]?.let { loaded ->
            val current = if (loaded.module == module) loaded else loaded.copy(module = module)
            loadedModules[engineId] = current
            return@catching current
        }
        loads.get(engineId) {
            catching {
                val cached = loadedModules[engineId]
                val sourceFile = cacheDir?.resolve("$engineId.js")
                val source = cached?.jsCode
                    ?: readCachedSource(sourceFile)
                    ?: download(downloadUrl)
                val baseUrl = downloadUrl.substringBeforeLast('/')
                val result = QuickJsExecutor.loadModule(engineId, source, baseUrl)
                if (result.isFailure) {
                    withContext(Dispatchers.IO) { sourceFile?.delete() }
                    loadedModules.remove(engineId)
                }
                val manifest = json.decodeFromString<ModuleManifest>(result.getOrThrow())
                val loaded = LoadedModule(module, source, baseUrl, engineId, manifest)
                if (cached == null) writeCachedSource(sourceFile, source)
                if (loadedModules.size >= 32 && !loadedModules.containsKey(engineId)) {
                    loadedModules.keys.firstOrNull()?.let(loadedModules::remove)
                }
                loadedModules[engineId] = loaded
                loaded
            }
        }.getOrThrow()
    }

    suspend fun searchTracks(
        loaded: LoadedModule,
        query: String,
        limit: Int = 50,
        settings: Map<String, String> = emptyMap(),
    ): Result<ModuleSearchResponse> {
        val context = contextArg(settings)
        return searches.get(keyOf(loaded.engineId, query, limit.toString(), context)) {
            catching {
                if (loaded.module.type == EclipseAddon.TYPE) {
                    EclipseAddon.parseSearch(json, download(EclipseAddon.searchUrl(loaded.baseUrl, query,
                        EclipseAddon.parameters(loaded.manifest, settings))), limit)
                } else json.decodeFromString<ModuleSearchResponse>(call(
                    loaded, "searchTracks", listOf(Json.encodeToString(query), limit.toString(), context),
                ))
            }
        }
    }

    suspend fun getStreamUrl(
        loaded: LoadedModule,
        trackId: String,
        settings: Map<String, String> = emptyMap(),
        quality: String = settings["quality"].orEmpty(),
    ): Result<ModuleStreamResponse> {
        val context = contextArg(settings)
        val calls = if (loaded.manifest.noStreamCache) liveStreams else streams
        return calls.get(keyOf(loaded.engineId, trackId, quality, context)) {
            catching {
                if (loaded.module.type == EclipseAddon.TYPE) {
                    EclipseAddon.parseStream(json, trackId, download(EclipseAddon.streamUrl(loaded.baseUrl, trackId,
                        EclipseAddon.parameters(loaded.manifest, settings, quality))))
                } else json.decodeFromString<ModuleStreamResponse>(call(
                    loaded, "getTrackStreamUrl",
                    listOf(Json.encodeToString(trackId), Json.encodeToString(quality), context),
                ))
            }
        }
    }

    private suspend fun call(loaded: LoadedModule, export: String, args: List<String>): String {
        // Recreate the engine if its pool was evicted while the module stayed cached.
        QuickJsExecutor.loadModule(loaded.engineId, loaded.jsCode, loaded.baseUrl).getOrThrow()
        return QuickJsExecutor.callExport(loaded.engineId, export, args).getOrThrow()
    }

    private suspend fun readCachedSource(file: File?): String? = withContext(Dispatchers.IO) {
        runCatching { file?.takeIf { it.isFile }?.readText() }.getOrNull()
    }

    private suspend fun writeCachedSource(file: File?, source: String) {
        withContext(Dispatchers.IO) {
            runCatching {
                file?.parentFile?.mkdirs()
                file?.writeText(source)
                file?.parentFile?.listFiles()
                    ?.filter { it.extension == "js" }
                    ?.sortedByDescending { it.lastModified() }
                    ?.drop(64)
                    ?.forEach { it.delete() }
            }
        }
    }

    fun unloadModule(moduleId: String) {
        loadedModules.entries.filter { it.value.module.id == moduleId }.forEach { (key, _) ->
            loadedModules.remove(key)
            loads.remove(key)
            QuickJsExecutor.unload(key)
        }
        searches.clear()
        streams.clear()
        liveStreams.clear()
    }

    fun unloadAll() {
        loadedModules.keys.forEach(QuickJsExecutor::unload)
        loadedModules.clear()
        addonManifests.clear()
        indexes.clear()
        loads.clear()
        searches.clear()
        streams.clear()
        liveStreams.clear()
    }

    private suspend fun download(url: String): String = withContext(Dispatchers.IO) {
        val origin = URI(url).let { "${it.scheme}://${it.authority}" }
        val waitMs = (rateLimits[origin] ?: 0L) - System.currentTimeMillis()
        require(waitMs <= 0L) { "This source is rate limited. Try again in ${(waitMs + 999) / 1000} seconds" }
        val response = client.get(url) { header(HttpHeaders.UserAgent, userAgent) }
        val body = response.bodyAsText()
        if (response.status.value == 429) {
            val retrySeconds = response.headers["Retry-After"]?.toLongOrNull()?.coerceIn(1, 300) ?: 5L
            rateLimits[origin] = System.currentTimeMillis() + retrySeconds * 1000
            error("This source is rate limited. Try again in $retrySeconds seconds")
        }
        if (response.status.value !in 200..299) {
            val reason = runCatching {
                json.decodeFromString(kotlinx.serialization.json.JsonObject.serializer(), body)["error"]
                    ?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
            }.getOrNull()
            error(reason ?: "Module request failed: HTTP ${response.status.value}")
        }
        body
    }

    private suspend fun <T> catching(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        val shared by lazy { ModuleManager() }
        @Volatile var cacheDir: File? = null
        /** Eclipse addons require `Convx/<app-version>`, supplied by the app at startup. */
        @Volatile var userAgent: String = "Convx"
        private val client by lazy {
            HttpClient(OkHttp) {
                install(HttpTimeout) {
                    requestTimeoutMillis = 15_000
                    connectTimeoutMillis = 10_000
                    socketTimeoutMillis = 15_000
                }
                expectSuccess = false
            }
        }

        internal fun contextArg(settings: Map<String, String>): String = buildJsonObject {
            putJsonObject("settings") {
                settings.toSortedMap().forEach { (key, value) ->
                    putJsonObject(key) { put("value", value) }
                }
            }
        }.toString()

        private fun keyOf(vararg parts: String) = parts.joinToString("|") { "${it.length}:$it" }
        private fun digest(value: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
            val digits = "0123456789abcdef"
            return buildString(bytes.size * 2) {
                for (byte in bytes) {
                    val value = byte.toInt() and 0xff
                    append(digits[value ushr 4])
                    append(digits[value and 0x0f])
                }
            }
        }
    }
}
