package com.convx.music.modules

import java.nio.file.Files
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DesktopModuleStoreTest {
    @Test fun snapshotSurvivesReopeningAndCanExceedJavaPreferencesValueLimit() = runTest {
        val directory = Files.createTempDirectory("convx-module-store-test")
        val path = directory.resolve("modules.json")
        try {
            assertNull(DesktopModuleStore(path).read())
            val largeSnapshot = "x".repeat(100_000)
            DesktopModuleStore(path).write(largeSnapshot)
            assertEquals(largeSnapshot, DesktopModuleStore(path).read())
            DesktopModuleStore(path).write("updated")
            assertEquals("updated", DesktopModuleStore(path).read())
            Files.list(directory).use { assertEquals(1L, it.count()) }
        } finally {
            Files.deleteIfExists(path)
            Files.deleteIfExists(directory)
        }
    }
}
