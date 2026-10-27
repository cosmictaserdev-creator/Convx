package com.convx.music.playback.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.playback.PlaybackCheckpoint
import java.io.File
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlaybackSettingsDatabaseMigrationTest {
    @Test
    fun versionOneDatabaseMigratesWithoutLosingSettings() = kotlinx.coroutines.test.runTest {
        val path = File(
            System.getProperty("java.io.tmpdir"),
            "convx-playback-v1-${Random.nextLong()}.db",
        ).absolutePath
        createVersionOneDatabase(path)

        val migrated = Room.databaseBuilder<PlaybackSettingsDatabase>(path)
            .setDriver(BundledSQLiteDriver())
            .build()
        try {
            val settings = RoomPlaybackSettingsRepository(migrated.playbackSettingsDao()).getSettings()
            val checkpoint = RoomPlaybackCheckpointRepository(migrated.playbackCheckpointDao()).getCheckpoint()

            assertEquals(true, settings.crossfadeEnabled)
            assertEquals(PlaybackCheckpoint(), checkpoint)
        } finally {
            migrated.close()
            listOf(path, "$path-wal", "$path-shm").forEach { File(it).delete() }
        }
    }

    @Test
    fun versionTwoDatabaseMigratesWithTheAnalysisTable() = kotlinx.coroutines.test.runTest {
        val path = File(
            System.getProperty("java.io.tmpdir"),
            "convx-playback-v2-${Random.nextLong()}.db",
        ).absolutePath
        createVersionTwoDatabase(path)

        val migrated = Room.databaseBuilder<PlaybackSettingsDatabase>(path)
            .setDriver(BundledSQLiteDriver())
            .build()
        try {
            val settings = RoomPlaybackSettingsRepository(migrated.playbackSettingsDao()).getSettings()
            val checkpoint = RoomPlaybackCheckpointRepository(migrated.playbackCheckpointDao()).getCheckpoint()

            assertEquals(true, settings.crossfadeEnabled)
            assertEquals(PlaybackCheckpoint(), checkpoint)
            assertNull(migrated.playbackTrackAnalysisDao().get("missing"))
        } finally {
            migrated.close()
            listOf(path, "$path-wal", "$path-shm").forEach { File(it).delete() }
        }
    }

    private fun createVersionOneDatabase(path: String) {
        BundledSQLiteDriver().open(path).use { connection ->
            connection.exec(
                "CREATE TABLE IF NOT EXISTS playback_settings " +
                    "(id INTEGER NOT NULL, settingsJson TEXT NOT NULL, PRIMARY KEY(id))",
            )
            connection.exec(
                "INSERT INTO playback_settings (id, settingsJson) " +
                    "VALUES (1, '{\"crossfadeEnabled\":true}')",
            )
            connection.exec("CREATE TABLE IF NOT EXISTS room_master_table " + "(id INTEGER PRIMARY KEY, identity_hash TEXT)")
            connection.exec(
                "INSERT OR REPLACE INTO room_master_table (id, identity_hash) " +
                    "VALUES (42, 'c13dbad2b7cb1d8d0c8a1e841b85915f')",
            )
            connection.exec("PRAGMA user_version = 1")
        }
    }

    private fun createVersionTwoDatabase(path: String) {
        BundledSQLiteDriver().open(path).use { connection ->
            connection.exec(
                "CREATE TABLE IF NOT EXISTS playback_settings " +
                    "(id INTEGER NOT NULL, settingsJson TEXT NOT NULL, PRIMARY KEY(id))",
            )
            connection.exec(
                "INSERT INTO playback_settings (id, settingsJson) " +
                    "VALUES (1, '{\"crossfadeEnabled\":true}')",
            )
            connection.exec(
                "CREATE TABLE IF NOT EXISTS playback_checkpoint " +
                    "(id INTEGER NOT NULL, checkpointJson TEXT NOT NULL, PRIMARY KEY(id))",
            )
            connection.exec("CREATE TABLE IF NOT EXISTS room_master_table " + "(id INTEGER PRIMARY KEY, identity_hash TEXT)")
            connection.exec(
                "INSERT OR REPLACE INTO room_master_table (id, identity_hash) " +
                    "VALUES (42, '6a3285ef3df587fc1d757cd280d447a8')",
            )
            connection.exec("PRAGMA user_version = 2")
        }
    }

    private fun androidx.sqlite.SQLiteConnection.exec(sql: String) {
        prepare(sql).use { statement -> statement.step() }
    }
}
