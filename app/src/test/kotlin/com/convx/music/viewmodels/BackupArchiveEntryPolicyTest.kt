package com.convx.music.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackupArchiveEntryPolicyTest {
    @Test
    fun acceptsOnlyFlatGeneratedBackgroundNames() {
        assertEquals(
            "home_background_123.jpg",
            BackupArchiveEntryPolicy.backgroundFileName("home_background_files/home_background_123.jpg"),
        )

        listOf(
            "home_background_files/../datastore/settings.preferences_pb",
            "home_background_files/nested/home_background_123.jpg",
            "home_background_files\\..\\datastore\\settings.preferences_pb",
            "home_background_files/other_file.txt",
            "home_background_123.jpg",
        ).forEach { entryName ->
            assertNull("Unexpectedly accepted $entryName", BackupArchiveEntryPolicy.backgroundFileName(entryName))
        }
    }
}
