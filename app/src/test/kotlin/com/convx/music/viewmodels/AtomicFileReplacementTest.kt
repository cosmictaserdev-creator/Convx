package com.convx.music.viewmodels

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class AtomicFileReplacementTest {
    @Test
    fun commitsAllStagedFiles() = withTemporaryDirectory { directory ->
        val existing = File(directory, "existing").apply { writeText("old") }
        val missing = File(directory, "new")
        val stagedExisting = File(directory, "existing.staged").apply { writeText("updated") }
        val stagedNew = File(directory, "new.staged").apply { writeText("created") }

        AtomicFileReplacement.commit(
            listOf(
                StagedFileReplacement(stagedExisting, existing),
                StagedFileReplacement(stagedNew, missing),
            ),
        )

        assertEquals("updated", existing.readText())
        assertEquals("created", missing.readText())
        assertFalse(stagedExisting.exists())
        assertFalse(stagedNew.exists())
    }

    @Test
    fun restoresEveryOriginalWhenALaterReplacementFails() = withTemporaryDirectory { directory ->
        val firstDestination = File(directory, "first").apply { writeText("first-original") }
        val createdDestination = File(directory, "created")
        val failingDestination = File(directory, "failing").apply { writeText("failing-original") }
        val firstStaged = File(directory, "first.staged").apply { writeText("first-restored") }
        val createdStaged = File(directory, "created.staged").apply { writeText("created-restored") }
        val failingStaged = File(directory, "failing.staged").apply { writeText("failing-restored") }

        try {
            AtomicFileReplacement.commit(
                listOf(
                    StagedFileReplacement(firstStaged, firstDestination),
                    StagedFileReplacement(createdStaged, createdDestination),
                    StagedFileReplacement(failingStaged, failingDestination),
                ),
            ) { source, destination ->
                if (source == failingStaged) throw IOException("simulated install failure")
                Files.move(
                    source.toPath(),
                    destination.toPath(),
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
            fail("Expected replacement to fail")
        } catch (expected: IOException) {
            assertEquals("simulated install failure", expected.message)
        }

        assertEquals("first-original", firstDestination.readText())
        assertFalse(createdDestination.exists())
        assertEquals("failing-original", failingDestination.readText())
        assertFalse(directory.listFiles().orEmpty().any { it.name.endsWith(".rollback") })
    }

    private inline fun withTemporaryDirectory(block: (File) -> Unit) {
        val directory = Files.createTempDirectory("convx-file-replacement-test").toFile()
        try {
            block(directory)
        } finally {
            directory.deleteRecursively()
        }
    }
}
