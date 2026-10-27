package com.convx.music.modules

import com.music.spine.ModuleManifest
import com.music.spine.ModuleSearchResult
import com.music.spine.ModuleStreamResponse
import com.music.spine.SpineModule
import com.convx.music.playback.PlaybackRequestTrack
import kotlin.math.abs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

/** One serialized snapshot makes source removal, priority and settings changes atomic. */
interface ModuleStore {
    suspend fun read(): String?
    suspend fun write(value: String)
}

@Serializable
data class ModulePreferences(
    val sources: List<String> = emptyList(),
    val modules: List<SpineModule> = emptyList(),
    val enabled: List<String> = emptyList(),
    val settings: Map<String, Map<String, String>> = emptyMap(),
) {
    fun key(module: SpineModule) = module.sourceUrl + "\n" + module.id
    fun module(key: String) = modules.firstOrNull { key(it) == key }
}

data class ModuleTrack(val moduleKey: String, val moduleName: String, val track: ModuleSearchResult)
data class ModuleSearch(val tracks: List<ModuleTrack>, val failures: Map<String, String>)
data class ModuleResolvedTrack(val track: ModuleSearchResult, val stream: ModuleStreamResponse)

class ModuleRepository(private val client: ModuleClient, private val store: ModuleStore) {
    private val mutex = Mutex()
    private val mutablePreferences = MutableStateFlow(ModulePreferences())
    val preferences = mutablePreferences.asStateFlow()
    private val loaded = mutableMapOf<String, LoadedModule>()
    private var restored = false

    suspend fun restore() = mutex.withLock { restoreLocked() }

    private suspend fun restoreLocked() {
        if (restored) return
        val saved = store.read()
        // A corrupt snapshot is reported instead of silently overwriting the user's sources.
        val prefs = saved?.let { moduleJson.decodeFromString<ModulePreferences>(it) } ?: ModulePreferences()
        val keys = prefs.modules.map(prefs::key).toSet()
        mutablePreferences.value = prefs.copy(enabled = prefs.enabled.distinct().filter { it in keys })
        restored = true
    }

    suspend fun addSource(rawUrl: String) = mutex.withLock {
        restoreLocked()
        val url = rawUrl.trim().trimEnd('/')
        val prefs = preferences.value
        require(url !in prefs.sources) { "This source is already added" }
        val modules = client.fetchSource(url)
        persist(prefs.copy(sources = prefs.sources + url, modules = prefs.modules + modules))
    }

    suspend fun refreshSource(url: String) = mutex.withLock {
        restoreLocked()
        val prefs = preferences.value
        require(url in prefs.sources) { "Source was removed" }
        val modules = prefs.modules.filterNot { it.sourceUrl == url } + client.fetchSource(url)
        val available = modules.map(prefs::key).toSet()
        persist(prefs.copy(modules = modules, enabled = prefs.enabled.filter { it in available },
            settings = prefs.settings.filterKeys { it in available }))
        loaded.keys.filter { prefs.module(it)?.sourceUrl == url }.forEach(loaded::remove)
    }

    suspend fun removeSource(url: String) = mutex.withLock {
        restoreLocked()
        val prefs = preferences.value
        val removed = prefs.modules.filter { it.sourceUrl == url }.map(prefs::key).toSet()
        persist(prefs.copy(sources = prefs.sources - url, modules = prefs.modules.filterNot { it.sourceUrl == url },
            enabled = prefs.enabled.filterNot { it in removed }, settings = prefs.settings.filterKeys { it !in removed }))
        removed.forEach(loaded::remove)
    }

    suspend fun setEnabled(key: String, enabled: Boolean) = mutex.withLock {
        restoreLocked()
        val prefs = preferences.value
        requireNotNull(prefs.module(key)) { "Module was removed" }
        if (enabled) loadLocked(key) // Validate the runtime and manifest before enabling.
        persist(prefs.copy(enabled = if (enabled) (prefs.enabled + key).distinct() else prefs.enabled - key))
        if (!enabled) loaded.remove(key)
    }

    suspend fun move(key: String, offset: Int) = mutex.withLock {
        restoreLocked()
        val prefs = preferences.value
        val order = prefs.enabled.toMutableList()
        val from = order.indexOf(key)
        require(from >= 0) { "Enable the module before changing priority" }
        val to = (from + offset).coerceIn(0, order.lastIndex)
        order.removeAt(from)
        order.add(to, key)
        persist(prefs.copy(enabled = order))
    }

    suspend fun manifest(key: String): ModuleManifest = mutex.withLock {
        restoreLocked()
        loadLocked(key).manifest
    }

    suspend fun saveSettings(key: String, values: Map<String, String>) = mutex.withLock {
        restoreLocked()
        val manifest = loadLocked(key).manifest
        values.forEach { (name, value) ->
            val setting = manifest.settings[name] ?: error("Unknown module setting: $name")
            require(setting.options.isEmpty() || setting.options.any { it.value == value }) { "Choose a supported value for ${setting.label}" }
            require(setting.type.lowercase() !in setOf("boolean", "switch", "toggle") || value in setOf("true", "false")) { "${setting.label} must be true or false" }
        }
        val prefs = preferences.value
        persist(prefs.copy(settings = prefs.settings + (key to values)))
    }

    suspend fun search(query: String): ModuleSearch = mutex.withLock {
        restoreLocked()
        require(query.isNotBlank()) { "Enter a search query" }
        require(preferences.value.enabled.isNotEmpty()) { "Enable a module or add-on to search" }
        val tracks = mutableListOf<ModuleTrack>()
        val failures = mutableMapOf<String, String>()
        for (key in preferences.value.enabled) {
            try {
                val module = loadLocked(key)
                tracks += client.search(module, query, preferences.value.settings[key].orEmpty()).tracks.map {
                    ModuleTrack(key, module.module.name, it)
                }
            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (error: Exception) {
                failures[key] = error.message ?: "Module search failed"
            }
        }
        ModuleSearch(tracks, failures)
    }

    /** User priority wins; a failing provider never substitutes a different recording. */
    suspend fun preferredStream(request: PlaybackRequestTrack): ModuleResolvedTrack? = mutex.withLock {
        restoreLocked()
        for (key in preferences.value.enabled) {
            try {
                val module = loadLocked(key)
                val settings = preferences.value.settings[key].orEmpty()
                val query = (listOf(request.title) + request.artists).joinToString(" ")
                var tracks = client.search(module, query, settings, limit = 8).tracks
                if (tracks.isEmpty() && request.artists.isNotEmpty()) {
                    tracks = client.search(module, request.artists.first() + " " + request.title, settings, limit = 8).tracks
                }
                val expected = request.durationMs?.div(1_000L)
                val candidates = tracks.filter { it.matches(request) }
                    .sortedBy { abs(it.duration.toLong() - (expected ?: it.duration.toLong())) }.take(2)
                for (candidate in candidates) {
                    try {
                        return@withLock ModuleResolvedTrack(candidate, client.stream(module, candidate.id, settings))
                    } catch (cancelled: kotlinx.coroutines.CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        // Try the next exact recording or source before the original provider.
                    }
                }
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Source outages must not prevent playback through the original catalog.
            }
        }
        null
    }

    suspend fun stream(track: ModuleTrack): ModuleStreamResponse = mutex.withLock {
        restoreLocked()
        require(track.moduleKey in preferences.value.enabled) { "This module is disabled or removed" }
        client.stream(loadLocked(track.moduleKey), track.track.id, preferences.value.settings[track.moduleKey].orEmpty())
    }

    private suspend fun loadLocked(key: String): LoadedModule = loaded[key] ?: run {
        val module = preferences.value.module(key) ?: error("Module was removed")
        client.load(module).also { loaded[key] = it }
    }

    private suspend fun persist(prefs: ModulePreferences) {
        store.write(moduleJson.encodeToString(ModulePreferences.serializer(), prefs))
        mutablePreferences.value = prefs
    }
}
