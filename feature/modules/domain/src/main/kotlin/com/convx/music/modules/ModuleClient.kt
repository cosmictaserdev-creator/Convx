package com.convx.music.modules

import com.music.spine.EclipseAddon
import com.music.spine.ModuleIndex
import com.music.spine.ModuleManifest
import com.music.spine.ModuleSearchResponse
import com.music.spine.ModuleStreamResponse
import com.music.spine.SpineModule
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.seconds

val moduleJson = Json { ignoreUnknownKeys = true; explicitNulls = false }

data class ModuleHttpResponse(val status: Int, val body: String, val headers: Map<String, String> = emptyMap(), val url: String = "")

fun interface ModuleTransport {
    suspend fun request(url: String, method: String, headers: Map<String, String>, body: String?): ModuleHttpResponse
}

/** The client owns redirects so every destination passes the same URL policy. */
class KtorModuleTransport(private val client: HttpClient) : ModuleTransport {
    override suspend fun request(url: String, method: String, headers: Map<String, String>, body: String?): ModuleHttpResponse {
        val response = client.request(url) {
            this.method = HttpMethod.parse(method)
            headers.forEach { (name, value) -> header(name, value) }
            if (body != null) setBody(body)
        }
        return ModuleHttpResponse(response.status.value, response.bodyAsText(),
            response.headers.entries().associate { it.key.lowercase() to it.value.joinToString(",") })
    }
}

interface ModuleScriptRuntime {
    suspend fun manifest(source: String, baseUrl: String): ModuleManifest
    suspend fun call(source: String, baseUrl: String, export: String, arguments: List<String>): String
}

data class LoadedModule(val module: SpineModule, val manifest: ModuleManifest, val source: String = "")

/** Protocol behavior shared by every host; engines and persistence are injected. */
class ModuleClient(
    private val transport: ModuleTransport,
    private val scripts: ModuleScriptRuntime,
    private val userAgent: String = "Convx/1.0",
) {
    private val rateLimitLock = Mutex()
    private val rateLimits = mutableMapOf<String, kotlin.time.TimeMark>()

    suspend fun fetchSource(sourceUrl: String): List<SpineModule> {
        require(EclipseAddon.isAllowedUrl(sourceUrl)) { "Enter an HTTPS module index or add-on manifest URL" }
        var resolved = sourceUrl
        var response = try { getDocument(resolved) } catch (error: ModuleHttpException) {
            if (error.status != 404 || Url(resolved).encodedPath.endsWith(".json")) throw error
            resolved = manifestUrl(resolved)
            getDocument(resolved)
        }
        resolved = response.url
        if (!response.body.trimStart().startsWith("{") && !Url(resolved).encodedPath.endsWith(".json")) {
            response = getDocument(manifestUrl(resolved))
            resolved = response.url
        }
        val body = response.body
        if (EclipseAddon.isManifest(moduleJson, body)) {
            return listOf(EclipseAddon.toModule(moduleJson, resolved, body).copy(sourceUrl = sourceUrl))
        }
        return ModuleIndex.parseModules(moduleJson, body).map { module ->
            module.copy(sourceUrl = sourceUrl, download = resolveUrl(resolved, module.download),
                logo = module.logo?.let { resolveUrl(resolved, it) },
                icon = module.icon?.let { resolveUrl(resolved, it) })
        }.also { require(it.isNotEmpty()) { "This source contains no supported modules" } }
    }

    suspend fun load(module: SpineModule): LoadedModule {
        require(EclipseAddon.isAllowedUrl(module.download)) { "Module downloads must use HTTPS" }
        return if (module.type == EclipseAddon.TYPE) {
            LoadedModule(module, EclipseAddon.moduleManifest(moduleJson, download(manifestUrl(module.download))))
        } else {
            val source = download(module.download)
            LoadedModule(module, scripts.manifest(source, scriptBase(module)), source)
        }
    }

    suspend fun search(loaded: LoadedModule, query: String, settings: Map<String, String>, limit: Int = 50): ModuleSearchResponse {
        require(query.isNotBlank()) { "Enter a search query" }
        require(limit in 1..100) { "Search limit must be between 1 and 100" }
        val values = loaded.manifest.defaults + settings
        return if (loaded.module.type == EclipseAddon.TYPE) {
            EclipseAddon.parseSearch(moduleJson, download(EclipseAddon.searchUrl(loaded.module.download, query,
                EclipseAddon.parameters(loaded.manifest, values))), limit)
        } else moduleJson.decodeFromString<ModuleSearchResponse>(scripts.call(loaded.source, scriptBase(loaded.module),
            "searchTracks", listOf(quote(query), limit.toString(), context(values)))).let {
                it.copy(tracks = it.tracks.take(limit))
            }
    }

    /** Always resolves afresh: expiring signed URLs and noStreamCache modules stay fresh. */
    suspend fun stream(loaded: LoadedModule, trackId: String, settings: Map<String, String>): ModuleStreamResponse {
        val values = loaded.manifest.defaults + settings
        val result = if (loaded.module.type == EclipseAddon.TYPE) {
            EclipseAddon.parseStream(moduleJson, trackId, download(EclipseAddon.streamUrl(loaded.module.download, trackId,
                EclipseAddon.parameters(loaded.manifest, values))))
        } else moduleJson.decodeFromString<ModuleStreamResponse>(scripts.call(loaded.source, scriptBase(loaded.module),
            "getTrackStreamUrl", listOf(quote(trackId), quote(values["quality"].orEmpty()), context(values))))
        val url = result.streamUrl
        require(!url.isNullOrBlank() && EclipseAddon.isAllowedUrl(url)) { "The module returned no supported stream URL" }
        return result
    }

    suspend fun request(url: String, method: String = "GET", headers: Map<String, String> = emptyMap(), body: String? = null): ModuleHttpResponse {
        var destination = url
        repeat(6) {
            require(EclipseAddon.isAllowedUrl(destination)) { "Module requests must use HTTPS" }
            val origin = Url(destination).let { "${it.protocol.name}://${it.host}:${it.port}" }
            rateLimitLock.withLock {
                rateLimits[origin]?.let { require(it.hasPassedNow()) { "This source is rate limited. Try again later" } }
            }
            val response = transport.request(destination, method, mapOf("User-Agent" to userAgent) + headers, body)
            if (response.status == 429) {
                val seconds = response.headers["retry-after"]?.toLongOrNull()?.coerceIn(1, 300) ?: 5L
                rateLimitLock.withLock { rateLimits[origin] = TimeSource.Monotonic.markNow() + seconds.seconds }
            }
            if (response.status in setOf(301, 302, 303, 307, 308)) {
                // Script POSTs may contain credentials; don't replay them on another origin.
                require(method == "GET" || method == "HEAD") { "Redirected module writes are unsupported" }
                val next = resolveUrl(destination, response.headers["location"] ?: error("Redirect without a destination"))
                require(Url(next).let { "${it.protocol.name}://${it.host}:${it.port}" } == origin || headers.isEmpty()) {
                    "Cannot forward module headers to another origin"
                }
                destination = next
            } else return response.copy(url = destination)
        }
        error("Too many module redirects")
    }

    private suspend fun getDocument(url: String): ModuleHttpResponse {
        val response = request(url)
        if (response.status !in 200..299) throw ModuleHttpException(response.status)
        return response
    }

    private suspend fun download(url: String): String = getDocument(url).body

    private fun scriptBase(module: SpineModule) = module.download.substringBeforeLast('/') + "/"
    private fun manifestUrl(base: String) = base.substringBefore('?').trimEnd('/') + "/manifest.json"

    companion object {
        fun quote(value: String) = JsonPrimitive(value).toString()
        fun context(settings: Map<String, String>) = buildJsonObject {
            putJsonObject("settings") {
                settings.entries.sortedBy { it.key }.forEach { (key, value) -> putJsonObject(key) { put("value", value) } }
            }
        }.toString()
        fun resolveUrl(base: String, relative: String): String {
            val builder = URLBuilder(base).takeFrom(relative)
            val path = builder.encodedPath
            val segments = mutableListOf<String>()
            path.split('/').forEach { segment ->
                when (segment) {
                    "." -> Unit
                    ".." -> if (segments.isNotEmpty()) segments.removeAt(segments.lastIndex)
                    else -> segments += segment
                }
            }
            var normalized = segments.joinToString("/")
            if (!normalized.startsWith('/')) normalized = "/$normalized"
            if (path.endsWith("/.") || path.endsWith("/..")) normalized += "/"
            builder.encodedPath = normalized
            return builder.buildString()
        }
    }
}

class ModuleHttpException(val status: Int) : IllegalStateException(
    if (status == 429) "This source is rate limited. Try again later" else "Module request failed: HTTP $status",
)
