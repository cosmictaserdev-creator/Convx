package com.convx.music.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.database.sqlite.SQLiteDatabase as AndroidSQLiteDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomSchemaMigrationTest {
    @Test
    fun schema39To40PreservesSongAndDefaultsPodcastFlag() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val databaseName = "schema-39-to-40-junit-proof.db"
        val context = instrumentation.targetContext
        context.deleteDatabase(databaseName)

        try {
            val helper = MigrationTestHelper(instrumentation, InternalDatabase::class.java)
            helper.createDatabase(databaseName, 39).use { oldDatabase ->
                oldDatabase.execSQL(
                    """
                    INSERT INTO song (id, title, duration, liked, totalPlayTime)
                    VALUES ('junit-migration-proof', 'Preserved song', 210000, 1, 321)
                    """.trimIndent()
                )
            }

            val migratedDatabase = InternalDatabase.newInstance(
                context = context,
                databaseName = databaseName,
                allowDestructiveMigrationOnDowngrade = false,
            )
            try {
                val database = migratedDatabase.openHelper.writableDatabase
                assertEquals(INTERNAL_DATABASE_VERSION, database.version)
                database.query(
                    "SELECT title, liked, totalPlayTime, isPodcastEpisode FROM song WHERE id = 'junit-migration-proof'"
                ).use { cursor ->
                    assertTrue("Song row disappeared during migration", cursor.moveToFirst())
                    assertEquals("Preserved song", cursor.getString(0))
                    assertEquals(1, cursor.getInt(1))
                    assertEquals(321L, cursor.getLong(2))
                    assertEquals(0, cursor.getInt(3))
                }
            } finally {
                migratedDatabase.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun rejectsFutureSchemaWithoutDeletingTheDatabase() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val databaseName = "future-schema-rejected-junit-proof.db"
        val context = instrumentation.targetContext
        val databaseFile = context.getDatabasePath(databaseName)
        context.deleteDatabase(databaseName)

        try {
            val helper = MigrationTestHelper(instrumentation, InternalDatabase::class.java)
            helper.createDatabase(databaseName, INTERNAL_DATABASE_VERSION).use { currentDatabase ->
                currentDatabase.execSQL(
                    "INSERT INTO song (id, title, duration, liked, totalPlayTime) VALUES ('future-proof', 'Keep me', 1, 1, 9)"
                )
            }
            val markedAsFuture = AndroidSQLiteDatabase.openDatabase(
                databaseFile.absolutePath,
                null,
                AndroidSQLiteDatabase.OPEN_READWRITE,
            )
            try {
                markedAsFuture.version = INTERNAL_DATABASE_VERSION + 1
            } finally {
                markedAsFuture.close()
            }

            val candidate = InternalDatabase.newInstance(
                context = context,
                databaseName = databaseName,
                allowDestructiveMigrationOnDowngrade = false,
            )
            try {
                try {
                    candidate.openHelper.writableDatabase
                    fail("A future backup schema must not be destructively downgraded")
                } catch (expected: IllegalStateException) {
                    // The staging configuration deliberately has no downgrade fallback.
                }
            } finally {
                candidate.close()
            }

            val restoredDatabase = AndroidSQLiteDatabase.openDatabase(
                databaseFile.absolutePath,
                null,
                AndroidSQLiteDatabase.OPEN_READONLY,
            )
            try {
                assertEquals(INTERNAL_DATABASE_VERSION + 1, restoredDatabase.version)
                restoredDatabase.rawQuery("SELECT title FROM song WHERE id = 'future-proof'", null).use { cursor ->
                    assertTrue(cursor.moveToFirst())
                    assertEquals("Keep me", cursor.getString(0))
                }
            } finally {
                restoredDatabase.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }
}
