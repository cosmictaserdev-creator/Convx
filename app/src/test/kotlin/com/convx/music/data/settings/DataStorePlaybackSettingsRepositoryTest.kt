package com.convx.music.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.convx.music.playback.DjReverbType
import com.convx.music.playback.PlaybackSettings as SharedPlaybackSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File

class DataStorePlaybackSettingsRepositoryTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    private fun createStore(job: Job, file: File = temporaryFolder.newFolder().resolve("settings.preferences_pb")): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            // Android's SDK stub selects legacy renameTo, which cannot replace Windows files.
            storage = OkioStorage(FileSystem.SYSTEM, PreferencesSerializer) { file.absolutePath.toPath() },
            scope = CoroutineScope(Dispatchers.IO + job),
        )

    @Test
    fun existingKeysSurviveUpdatesAndReopening() = runBlocking {
        val file = temporaryFolder.newFolder().resolve("settings.preferences_pb")
        suspend fun withStore(block: suspend (DataStore<Preferences>) -> Unit) {
            val job = Job()
            val store = createStore(job, file)
            try {
                block(store)
            } finally {
                job.cancelAndJoin()
            }
        }
        withStore { store ->
            store.edit {
                it[floatPreferencesKey("crossfadeDuration")] = 8.5f
                it[booleanPreferencesKey("crossfadeEnabled")] = true
                it[floatPreferencesKey("lyricsTextSize")] = 36f
                it[floatPreferencesKey("playerVolume")] = .65f
                it[booleanPreferencesKey("localOnlyMode")] = true
                it[booleanPreferencesKey("pauseSearchHistory")] = true
                it[booleanPreferencesKey("sponsorBlockEnabled")] = true
                it[stringPreferencesKey("lyricsAnimationStyle")] = "FISHEYE"
                it[stringPreferencesKey("unrelated-key")] = "preserve me"
            }
            val repository = DataStorePlaybackSettingsRepository(store)
            val existing = repository.getSettings()
            assertEquals(8_500L, existing.crossfadeDurationMs)
            assertEquals(36f, existing.lyricsFontSize)
            assertEquals(.65f, existing.volume)
            assertTrue(existing.localMode)
            assertFalse(existing.recordSearchHistory)
            assertTrue(existing.sponsorBlockEnabled)
            assertEquals(com.convx.music.playback.LyricsAnimationStyle.FISHEYE, existing.lyricsAnimationStyle)
            repository.updateSettings { it.copy(crossfadeDurationMs = 9_000, reverbType = DjReverbType.PLATE) }
            val raw = store.data.first()
            assertEquals(9f, raw[floatPreferencesKey("crossfadeDuration")])
            assertEquals("PLATE", raw[stringPreferencesKey("djReverbType")])
            assertEquals("preserve me", raw[stringPreferencesKey("unrelated-key")])
            assertEquals(existing.copy(crossfadeDurationMs = 9_000, reverbType = DjReverbType.PLATE), repository.observeSettings().first())
            assertFalse(repository.updateSettings { it.copy(sponsorBlockEnabled = false) }.sponsorBlockEnabled)
            assertEquals(false, store.data.first()[booleanPreferencesKey("sponsorBlockEnabled")])
        }
        withStore { store ->
            val restored = DataStorePlaybackSettingsRepository(store).getSettings()
            assertEquals(9_000L, restored.crossfadeDurationMs)
            assertEquals(DjReverbType.PLATE, restored.reverbType)
            assertEquals(36f, restored.lyricsFontSize)
            assertFalse(restored.recordSearchHistory)
            assertFalse(restored.sponsorBlockEnabled)
            assertEquals(com.convx.music.playback.LyricsAnimationStyle.FISHEYE, restored.lyricsAnimationStyle)
        }
    }

    @Test
    fun atomicTransformsKeepConcurrentEditsAndNoOpDoesNotPopulateDefaults() = runBlocking {
        val job = Job()
        val store = createStore(job)
        try {
            val repository = DataStorePlaybackSettingsRepository(store)
            assertEquals(SharedPlaybackSettings(), repository.updateSettings { it })
            assertTrue(store.data.first().asMap().isEmpty())
            coroutineScope {
                repeat(10) { launch { repository.updateSettings { it.copy(crossfadeDurationMs = it.crossfadeDurationMs + 100) } } }
                launch { repository.updateSettings { it.copy(localMode = true) } }
            }
            assertEquals(6_000L, repository.getSettings().crossfadeDurationMs)
            assertTrue(repository.getSettings().localMode)
        } finally {
            job.cancelAndJoin()
        }
    }

    @Test
    fun unadaptedStoresCannotSilentlyLoseAnUpdate() = runBlocking {
        val job = Job()
        val store = createStore(job)
        try {
            val repository = DataStorePlaybackSettingsRepository(store)
            try {
                repository.updateSettings { it.copy(equalizerEnabled = true, localMode = true) }
                throw AssertionError("An unadapted EQ update must fail")
            } catch (_: UnsupportedOperationException) {
                assertEquals(SharedPlaybackSettings(), repository.getSettings())
                assertTrue(store.data.first().asMap().isEmpty())
            }
        } finally {
            job.cancelAndJoin()
        }
    }
}
