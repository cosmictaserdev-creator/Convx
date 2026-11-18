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
    fun schema40To41KeepsSongsAndStoresCanvasChoicesByMediaId() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val databaseName = "canvas-schema-40-to-41-proof.db"
        val helper = MigrationTestHelper(instrumentation, InternalDatabase::class.java)
        context.deleteDatabase(databaseName)

        try {
            helper.createDatabase(databaseName, 40).use { old ->
                old.execSQL("INSERT INTO song (id, title, duration, liked, totalPlayTime) VALUES ('one', 'Keep me', 1, 1, 9)")
            }
            val migrated = InternalDatabase.newInstance(context, databaseName, allowDestructiveMigrationOnDowngrade = false)
            try {
                val db = migrated.openHelper.writableDatabase
                assertEquals(INTERNAL_DATABASE_VERSION, db.version)
                db.query("SELECT title FROM song WHERE id = 'one'").use { row ->
                    assertTrue(row.moveToFirst())
                    assertEquals("Keep me", row.getString(0))
                }
                kotlinx.coroutines.runBlocking {
                    migrated.canvasDecisionDao.upsert(com.convx.music.db.entities.CanvasDecisionEntity("one", false))
                    migrated.canvasDecisionDao.upsert(com.convx.music.db.entities.CanvasDecisionEntity("stream-only", true))
                }
            } finally {
                migrated.close()
            }
            val reopened = InternalDatabase.newInstance(context, databaseName, allowDestructiveMigrationOnDowngrade = false)
            try {
                reopened.openHelper.readableDatabase.query("SELECT mediaId, enabled FROM canvas_decision ORDER BY mediaId").use { rows ->
                    assertTrue(rows.moveToFirst())
                    assertEquals("one", rows.getString(0))
                    assertEquals(0, rows.getInt(1))
                    assertTrue(rows.moveToNext())
                    assertEquals("stream-only", rows.getString(0))
                    assertEquals(1, rows.getInt(1))
                    assertEquals(2, rows.count)
                }
            } finally {
                reopened.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

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
