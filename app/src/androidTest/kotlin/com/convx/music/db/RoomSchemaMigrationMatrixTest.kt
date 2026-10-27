package com.convx.music.db

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomSchemaMigrationMatrixTest {
    @Test
    fun everyCheckedInSchemaUpgradesToCurrentWithoutDestructiveFallback() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val helper = MigrationTestHelper(instrumentation, InternalDatabase::class.java)
        val arguments = InstrumentationRegistry.getArguments()
        val startVersion = arguments.getString("schemaStartVersion")?.toIntOrNull() ?: 1
        val endVersion =
            arguments.getString("schemaEndVersion")?.toIntOrNull() ?: INTERNAL_DATABASE_VERSION

        require(startVersion in 1 until INTERNAL_DATABASE_VERSION)
        require(endVersion in (startVersion + 1)..INTERNAL_DATABASE_VERSION)

        for (sourceVersion in startVersion until endVersion) {
            android.util.Log.i(TAG, "Starting production migration from schema $sourceVersion")
            val databaseName = "schema-$sourceVersion-to-current-matrix.db"
            context.deleteDatabase(databaseName)
            try {
                helper.createDatabase(databaseName, sourceVersion).close()
                val migratedDatabase = InternalDatabase.newInstance(
                    context = context,
                    databaseName = databaseName,
                    allowDestructiveMigrationOnDowngrade = false,
                )
                try {
                    assertEquals(
                        "Schema $sourceVersion did not reach the current schema",
                        INTERNAL_DATABASE_VERSION,
                        migratedDatabase.openHelper.writableDatabase.version,
                    )
                    android.util.Log.i(TAG, "Finished production migration from schema $sourceVersion")
                } finally {
                    migratedDatabase.close()
                }
            } catch (failure: Throwable) {
                throw AssertionError("Production migration failed from schema $sourceVersion", failure)
            } finally {
                context.deleteDatabase(databaseName)
            }
        }
    }

    private companion object {
        const val TAG = "RoomMigrationMatrix"
    }
}
