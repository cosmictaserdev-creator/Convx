package com.convx.music.modules

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FixtureStore : ModuleStore {
    var value: String? = null
    var failWrites = false
    override suspend fun read() = value
    override suspend fun write(value: String) {
        check(!failWrites) { "Disk full" }
        this.value = value
    }
}

class ModuleRepositoryTest {
    private val source = "https://source.example/index.json"
    private val index = """{"category:modules":[
        {"id":"a","name":"A","download":"a.js"},
        {"id":"b","name":"B","download":"b.js"}
    ]}"""

    private fun transport() = FixtureTransport().apply {
        respond(source, index)
        respond("https://source.example/a.js", "source A")
        respond("https://source.example/b.js", "source B")
    }

    @Test fun persistsSettingsAndPriorityThenRemovesAllSourceState() = runTest {
        val store = FixtureStore()
        val scripts = FixtureScripts().apply {
            manifest = moduleJson.decodeFromString("""{"settings":{"quality":{"defaultValue":"low","options":[{"value":"low"},{"value":"high"}]}}}""")
        }
        val repository = ModuleRepository(ModuleClient(transport(), scripts), store)
        repository.addSource(source)
        val (a, b) = repository.preferences.value.modules.map(repository.preferences.value::key)
        repository.setEnabled(a, true)
        repository.setEnabled(b, true)
        repository.move(b, -1)
        repository.saveSettings(a, mapOf("quality" to "high"))
        val restored = ModuleRepository(ModuleClient(transport(), scripts), store)
        restored.restore()
        assertEquals(listOf(b, a), restored.preferences.value.enabled)
        assertEquals("high", restored.preferences.value.settings[a]?.get("quality"))
        val results = restored.search("Song")
        assertEquals(listOf(b, a), results.tracks.map { it.moduleKey })
        restored.removeSource(source)
        assertTrue(restored.preferences.value.modules.isEmpty())
        assertTrue(restored.preferences.value.enabled.isEmpty())
        assertTrue(restored.preferences.value.settings.isEmpty())
        assertFailsWith<IllegalArgumentException> { restored.stream(results.tracks.first()) }
    }

    @Test fun failedRefreshAndFailedPersistenceLeaveExistingStateIntact() = runTest {
        val transport = transport()
        val store = FixtureStore()
        val repository = ModuleRepository(ModuleClient(transport, FixtureScripts()), store)
        repository.addSource(source)
        val before = repository.preferences.value
        transport.responses.remove(source)
        assertFailsWith<ModuleHttpException> { repository.refreshSource(source) }
        assertEquals(before, repository.preferences.value)
        store.failWrites = true
        assertFailsWith<IllegalStateException> { repository.removeSource(source) }
        assertEquals(before, repository.preferences.value)
    }

    @Test fun sourceCollisionsDoNotEnableOrRemoveAnotherSourcesModule() = runTest {
        val other = "https://other.example/index.json"
        val transport = transport().apply {
            respond(other, index)
            respond("https://other.example/a.js", "other A")
        }
        val repository = ModuleRepository(ModuleClient(transport, FixtureScripts()), FixtureStore())
        repository.addSource(source)
        repository.addSource(other)
        val keys = repository.preferences.value.modules.filter { it.id == "a" }.map(repository.preferences.value::key)
        keys.forEach { repository.setEnabled(it, true) }
        repository.removeSource(source)
        assertEquals(listOf(keys[1]), repository.preferences.value.enabled)
    }

    @Test fun refreshRemovesStaleEnabledModulesAndKeepsSurvivingPriority() = runTest {
        val transport = transport()
        val repository = ModuleRepository(ModuleClient(transport, FixtureScripts()), FixtureStore())
        repository.addSource(source)
        val keys = repository.preferences.value.modules.map(repository.preferences.value::key)
        keys.forEach { repository.setEnabled(it, true) }
        transport.respond(source, """{"category:modules":[{"id":"b","name":"Updated B","download":"b.js"}]}""")
        repository.refreshSource(source)
        assertEquals(listOf(keys[1]), repository.preferences.value.enabled)
        assertEquals("Updated B", repository.preferences.value.modules.single().name)
    }

    @Test fun badSnapshotIsPreservedAndCancelledSearchDoesNotBecomeProviderFailure() = runTest {
        val store = FixtureStore().apply { value = "broken data" }
        val scripts = FixtureScripts()
        val repository = ModuleRepository(ModuleClient(transport(), scripts), store)
        assertFailsWith<IllegalArgumentException> { repository.restore() }
        assertEquals("broken data", store.value)
        store.value = null
        repository.addSource(source)
        val key = repository.preferences.value.key(repository.preferences.value.modules.first())
        repository.setEnabled(key, true)
        scripts.failure = CancellationException("cancelled")
        assertFailsWith<CancellationException> { repository.search("query") }
        scripts.failure = IllegalStateException("Provider unavailable")
        val result = repository.search("query")
        assertTrue(result.tracks.isEmpty())
        assertFalse(result.failures.isEmpty())
    }
}
