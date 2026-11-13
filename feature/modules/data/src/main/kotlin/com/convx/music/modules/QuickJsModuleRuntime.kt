package com.convx.music.modules

import com.dokar.quickjs.QuickJs
import com.dokar.quickjs.binding.AsyncFunctionBinding
import com.dokar.quickjs.binding.FunctionBinding
import com.dokar.quickjs.binding.define
import com.music.spine.ModuleManifest
import io.ktor.http.Url
import io.ktor.http.hostWithPortIfSpecified
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.io.encoding.Base64

/** Each invocation leases its own VM. No mutable JavaScript state crosses calls or modules. */
class QuickJsModuleRuntime(private val transport: ModuleTransport) : ModuleScriptRuntime {
    override suspend fun manifest(source: String, baseUrl: String): ModuleManifest =
        moduleJson.decodeFromString(evaluate(source, baseUrl, null, emptyList()))

    override suspend fun call(source: String, baseUrl: String, export: String, arguments: List<String>): String =
        evaluate(source, baseUrl, export, arguments)

    private suspend fun evaluate(source: String, baseUrl: String, export: String?, arguments: List<String>): String =
        withContext(Dispatchers.Default) {
            withTimeout(30_000) {
                val engine = QuickJs.create(Dispatchers.Default)
                try {
                    engine.memoryLimit = 64 * 1024 * 1024L
                    engine.maxStackSize = 512 * 1024L
                    engine.evaluationTimeoutMillis = 5_000
                    bind(engine, baseUrl)
                    engine.evaluate<Any?>(POLYFILLS)
                    engine.evaluate<Any?>("""
                        var __spine_mod = (function() {
                            var module = {exports: {}};
                            var exports = module.exports;
                            var self = {};
                            ${preprocess(source)}
                            return module.exports;
                        })();
                        if (typeof __spine_mod.searchTracks !== 'function' || typeof __spine_mod.getTrackStreamUrl !== 'function') {
                            throw new Error('Module must export searchTracks and getTrackStreamUrl');
                        }
                    """.trimIndent())
                    if (export == null) {
                        engine.evaluate<String>("JSON.stringify({settings: __spine_mod.settings || {}, noStreamCache: !!__spine_mod.noStreamCache})")
                    } else {
                        require(export in setOf("searchTracks", "getTrackStreamUrl"))
                        engine.evaluate<Any?>("""
                            var __spine_answer;
                            (async function() {
                                try {
                                    var result = await __spine_mod[${ModuleClient.quote(export)}](${arguments.joinToString(",")});
                                    __spine_answer = typeof result === 'string' ? result : JSON.stringify(result);
                                } catch(error) {
                                    __spine_answer = JSON.stringify({error: String(error && error.message || error)});
                                }
                            })();
                        """.trimIndent())
                        engine.evaluate<String>("__spine_answer").also { answer ->
                            val result = moduleJson.parseToJsonElement(answer) as? JsonObject
                                ?: error("The module returned an invalid response")
                            result["error"]?.let { error(it.jsonPrimitive.content) }
                        }
                    }
                } catch (error: Exception) {
                    // Native VM interruption may surface as a regular script error after cancellation.
                    currentCoroutineContext().ensureActive()
                    throw error
                } finally {
                    engine.close()
                }
            }
        }

    private fun bind(engine: QuickJs, baseUrl: String) {
        val timerLock = SynchronizedObject()
        val timers = mutableMapOf<Int, CompletableDeferred<Boolean>>()
        var nextTimerId = 0
        engine.define("console") {
            for (name in listOf("log", "warn", "error", "info", "debug")) {
                function(name, object : FunctionBinding<Unit> { override fun invoke(args: Array<Any?>) = Unit })
            }
        }
        engine.define("__spine") {
            asyncFunction("fetch", object : AsyncFunctionBinding<String> {
                override suspend fun invoke(args: Array<Any?>): String {
                    val rawUrl = args.firstOrNull()?.toString() ?: error("fetch requires a URL")
                    val headers = moduleJson.decodeFromString<Map<String, String>>(args.getOrNull(2)?.toString() ?: "{}")
                    val response = transport.request(ModuleClient.resolveUrl(baseUrl, rawUrl),
                        args.getOrNull(1)?.toString() ?: "GET", headers, args.getOrNull(3)?.toString())
                    return buildJsonObject {
                        put("status", response.status)
                        put("ok", response.status in 200..299)
                        put("body", response.body)
                        put("headers", JsonObject(response.headers.mapValues { JsonPrimitive(it.value) }))
                    }.toString()
                }
            })
            function("timer", object : FunctionBinding<Int> {
                override fun invoke(args: Array<Any?>): Int = synchronized(timerLock) {
                    val id = ++nextTimerId
                    timers[id] = CompletableDeferred()
                    id
                }
            })
            function("cancelTimer", object : FunctionBinding<Unit> {
                override fun invoke(args: Array<Any?>) {
                    val id = (args.firstOrNull() as? Number)?.toInt() ?: return
                    synchronized(timerLock) { timers[id]?.complete(false) }
                }
            })
            asyncFunction("waitTimer", object : AsyncFunctionBinding<Boolean> {
                override suspend fun invoke(args: Array<Any?>): Boolean {
                    val id = (args.firstOrNull() as? Number)?.toInt() ?: return false
                    val timer = synchronized(timerLock) { timers[id] } ?: return false
                    val ms = (args.getOrNull(1) as? Number)?.toLong()?.coerceIn(0, 30_000) ?: 0
                    return try {
                        withTimeoutOrNull(ms) { timer.await() } ?: true
                    } finally {
                        synchronized(timerLock) { timers.remove(id) }
                    }
                }
            })
            function("atob", object : FunctionBinding<String> {
                override fun invoke(args: Array<Any?>): String =
                    args.first().toString().filterNot(Char::isWhitespace).let { encoded ->
                        Base64.decode(encoded.padEnd((encoded.length + 3) / 4 * 4, '='))
                    }.map { (it.toInt() and 255).toChar() }.joinToString("")
            })
            function("url", object : FunctionBinding<String> {
                override fun invoke(args: Array<Any?>): String {
                    val url = Url(ModuleClient.resolveUrl(args.getOrNull(1)?.toString() ?: baseUrl, args.first().toString()))
                    return buildJsonObject {
                        put("href", url.toString()); put("hostname", url.host); put("pathname", url.encodedPath)
                        put("origin", "${url.protocol.name}://${url.hostWithPortIfSpecified}")
                    }.toString()
                }
            })
        }
    }

    internal companion object {
        fun preprocess(source: String): String {
            val code = source.trim()
            val start = Regex("""^export\s+const\s+\w+\s*=\s*`""").find(code)
            if (start != null) {
                var index = start.range.last + 1
                while (index < code.length) {
                    if (code[index] == '\\') { index += 2; continue }
                    if (code[index] == '`') return code.substring(start.range.last + 1, index).trim()
                    index++
                }
            }
            return code.replace(Regex("""\bexport\s+default\s+(?=function|class|const|let|var|async)"""), "")
                .replace(Regex("""\bexport\s+(const|let|var|function|class|async)\b"""), "$1")
                .replace(Regex("""\bexport\s*\{[^}]*\}\s*;?"""), "")
        }

        private const val POLYFILLS = """
            var AbortController = function() { this.signal = {aborted: false}; };
            AbortController.prototype.abort = function() { this.signal.aborted = true; };
            var atob = function(value) { return __spine.atob(value); };
            var URL = function(url, base) { Object.assign(this, JSON.parse(__spine.url(String(url), base))); };
            var fetch = async function(url, options) {
                options = options || {};
                if (options.signal && options.signal.aborted) throw new Error('Aborted');
                var headers = typeof options.headers === 'string' ? options.headers : JSON.stringify(options.headers || {});
                var body = options.body == null ? null : (typeof options.body === 'string' ? options.body : JSON.stringify(options.body));
                var raw = JSON.parse(await __spine.fetch(String(url), options.method || 'GET', headers, body));
                return {ok: raw.ok, status: raw.status, statusText: raw.ok ? 'OK' : 'Error',
                    text: function() {return raw.body;}, json: function() {return JSON.parse(raw.body);},
                    clone: function() {return this;}, headers: {get: function(key) {return raw.headers[String(key).toLowerCase()] || null;}}};
            };
            var __timers = {};
            var setTimeout = function(fn, ms) {
                var id = __spine.timer();
                __timers[id] = true;
                __spine.waitTimer(id, ms || 0).then(function(fire) {if (fire && __timers[id]) {delete __timers[id]; fn();}});
                return id;
            };
            var clearTimeout = function(id) {delete __timers[id]; __spine.cancelTimer(id);};
        """
    }
}
