package com.convx.music.playback.data

import androidx.room.ConstructedBy
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.playback.PlaybackSettings
import com.convx.music.playback.PlaybackCheckpoint
import com.convx.music.playback.PlaybackTrackAnalysisRepository
import com.convx.music.playback.PlaybackCheckpointRepository
import com.convx.music.playback.PlaybackSettingsRepository
import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import org.koin.core.module.Module
import org.koin.dsl.module

@Entity(tableName = "playback_settings")
data class PlaybackSettingsRecord(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val settingsJson: String,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}

@Dao
interface PlaybackSettingsDao {
    @Query("SELECT settingsJson FROM playback_settings WHERE id = ${PlaybackSettingsRecord.SINGLETON_ID} LIMIT 1")
    fun observeSettingsJson(): Flow<String?>

    @Query("SELECT settingsJson FROM playback_settings WHERE id = ${PlaybackSettingsRecord.SINGLETON_ID} LIMIT 1")
    suspend fun getSettingsJson(): String?

    @Upsert
    suspend fun upsert(record: PlaybackSettingsRecord)
}

@Entity(tableName = "playback_checkpoint")
data class PlaybackCheckpointRecord(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val checkpointJson: String,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}

@Dao
interface PlaybackCheckpointDao {
    @Query("SELECT checkpointJson FROM playback_checkpoint WHERE id = ${PlaybackCheckpointRecord.SINGLETON_ID} LIMIT 1")
    fun observeCheckpointJson(): Flow<String?>

    @Query("SELECT checkpointJson FROM playback_checkpoint WHERE id = ${PlaybackCheckpointRecord.SINGLETON_ID} LIMIT 1")
    suspend fun getCheckpointJson(): String?

    @Upsert
    suspend fun upsert(record: PlaybackCheckpointRecord)
}

@Entity(tableName = "playback_track_analysis")
data class PlaybackTrackAnalysisRecord(
    @PrimaryKey val trackIdentity: String,
    val storageVersion: Int,
    val analysisJson: String,
    val storedAtEpochMs: Long,
)

@Dao
interface PlaybackTrackAnalysisDao {
    @Query("SELECT * FROM playback_track_analysis WHERE trackIdentity = :trackIdentity LIMIT 1")
    suspend fun get(trackIdentity: String): PlaybackTrackAnalysisRecord?

    @Query("SELECT MAX(storedAtEpochMs) FROM playback_track_analysis")
    suspend fun latestStoredAtEpochMs(): Long?

    @Upsert
    suspend fun upsert(record: PlaybackTrackAnalysisRecord)

    @Query("DELETE FROM playback_track_analysis WHERE trackIdentity = :trackIdentity")
    suspend fun delete(trackIdentity: String)

    @Query(
        "DELETE FROM playback_track_analysis WHERE trackIdentity IN (" +
            "SELECT trackIdentity FROM playback_track_analysis " +
            "ORDER BY storedAtEpochMs DESC, trackIdentity ASC " +
            "LIMIT -1 OFFSET :maxEntries)",
    )
    suspend fun deleteBeyond(maxEntries: Int)
}

@Database(
    entities = [PlaybackSettingsRecord::class, PlaybackCheckpointRecord::class, PlaybackTrackAnalysisRecord::class],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)],
    exportSchema = true,
)
@ConstructedBy(PlaybackSettingsDatabaseConstructor::class)
abstract class PlaybackSettingsDatabase : RoomDatabase() {
    abstract fun playbackSettingsDao(): PlaybackSettingsDao

    abstract fun playbackCheckpointDao(): PlaybackCheckpointDao

    abstract fun playbackTrackAnalysisDao(): PlaybackTrackAnalysisDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PlaybackSettingsDatabaseConstructor : RoomDatabaseConstructor<PlaybackSettingsDatabase> {
    override fun initialize(): PlaybackSettingsDatabase
}

class RoomPlaybackSettingsRepository(
    private val dao: PlaybackSettingsDao,
) : PlaybackSettingsRepository {
    private val writeMutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun observeSettings(): Flow<PlaybackSettings> =
        dao.observeSettingsJson().map(::decodeSettings).distinctUntilChanged()

    override suspend fun getSettings(): PlaybackSettings = decodeSettings(dao.getSettingsJson())

    override suspend fun updateSettings(
        transform: (PlaybackSettings) -> PlaybackSettings,
    ): PlaybackSettings = writeMutex.withLock {
        val updated = transform(getSettings()).normalized()
        dao.upsert(
            PlaybackSettingsRecord(settingsJson = json.encodeToString(updated)),
        )
        updated
    }

    private fun decodeSettings(serialized: String?): PlaybackSettings {
        if (serialized == null) return PlaybackSettings()
        return try {
            json.decodeFromString<PlaybackSettings>(serialized).normalized()
        } catch (_: SerializationException) {
            PlaybackSettings()
        }
    }
}

class RoomPlaybackCheckpointRepository(
    private val dao: PlaybackCheckpointDao,
) : PlaybackCheckpointRepository {
    private val writeMutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun observeCheckpoint(): Flow<PlaybackCheckpoint> =
        dao.observeCheckpointJson().map(::decodeCheckpoint).distinctUntilChanged()

    override suspend fun getCheckpoint(): PlaybackCheckpoint = decodeCheckpoint(dao.getCheckpointJson())

    override suspend fun saveCheckpoint(checkpoint: PlaybackCheckpoint) = writeMutex.withLock {
        dao.upsert(
            PlaybackCheckpointRecord(checkpointJson = json.encodeToString(checkpoint.normalized())),
        )
    }

    private fun decodeCheckpoint(serialized: String?): PlaybackCheckpoint {
        if (serialized == null) return PlaybackCheckpoint()
        return try {
            json.decodeFromString<PlaybackCheckpoint>(serialized).normalized()
        } catch (_: SerializationException) {
            PlaybackCheckpoint()
        }
    }
}

class RoomPlaybackTrackAnalysisRepository(
    private val dao: PlaybackTrackAnalysisDao,
) : PlaybackTrackAnalysisRepository {
    private val writeMutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun get(trackIdentity: String): TrackAnalysis? {
        if (trackIdentity.isBlank()) return null
        val record = dao.get(trackIdentity) ?: return null
        val analysis = try {
            val stored = json.decodeFromString<StoredTrackAnalysis>(record.analysisJson)
            if (record.storageVersion != STORAGE_VERSION || stored.version != STORAGE_VERSION) {
                null
            } else {
                stored.analysis.takeIf { it.trackId == trackIdentity && it.isUsable }
            }
        } catch (_: SerializationException) {
            null
        }
        if (analysis == null) dao.delete(trackIdentity)
        return analysis
    }

    override suspend fun save(trackIdentity: String, analysis: TrackAnalysis) {
        if (trackIdentity.isBlank() || analysis.trackId != trackIdentity || !analysis.isUsable) return
        writeMutex.withLock {
            val now = Clock.System.now().toEpochMilliseconds()
            val nextTimestamp = maxOf(now, (dao.latestStoredAtEpochMs() ?: 0L) + 1L)
            dao.upsert(
                PlaybackTrackAnalysisRecord(
                    trackIdentity = trackIdentity,
                    storageVersion = STORAGE_VERSION,
                    analysisJson = json.encodeToString(StoredTrackAnalysis(analysis = analysis)),
                    storedAtEpochMs = nextTimestamp,
                ),
            )
            dao.deleteBeyond(MAX_ENTRIES)
        }
    }

    override suspend fun remove(trackIdentity: String) {
        if (trackIdentity.isBlank()) return
        dao.delete(trackIdentity)
    }

    @Serializable
    private data class StoredTrackAnalysis(
        val version: Int = STORAGE_VERSION,
        val analysis: TrackAnalysis,
    )

    private companion object {
        const val STORAGE_VERSION = 1
        const val MAX_ENTRIES = 6_000
    }
}

fun playbackDataModule(database: PlaybackSettingsDatabase): Module = module {
    single { database }
    single { database.playbackSettingsDao() }
    single { database.playbackCheckpointDao() }
    single { database.playbackTrackAnalysisDao() }
    single<PlaybackSettingsRepository> { RoomPlaybackSettingsRepository(get()) }
    single<PlaybackCheckpointRepository> { RoomPlaybackCheckpointRepository(get()) }
    single<PlaybackTrackAnalysisRepository> { RoomPlaybackTrackAnalysisRepository(get()) }
}

fun buildPlaybackSettingsDatabase(
    builder: RoomDatabase.Builder<PlaybackSettingsDatabase>,
): PlaybackSettingsDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .build()
