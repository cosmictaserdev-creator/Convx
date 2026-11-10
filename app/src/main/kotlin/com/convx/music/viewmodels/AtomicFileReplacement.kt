package com.convx.music.viewmodels

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal data class StagedFileReplacement(
    val stagedFile: File,
    val destinationFile: File,
)

/** Replaces a set of files and restores every original if any replacement fails. */
internal object AtomicFileReplacement {
    private class ReplacementState(
        val replacement: StagedFileReplacement,
    ) {
        var backupFile: File? = null
        var originalBackedUp = false
        var stagedFileInstalled = false
    }

    fun commit(
        replacements: List<StagedFileReplacement>,
        moveFile: (File, File) -> Unit = ::moveAtomically,
    ) {
        require(replacements.map { it.destinationFile.canonicalFile }.distinct().size == replacements.size) {
            "A restore cannot replace the same destination more than once"
        }

        val states = mutableListOf<ReplacementState>()
        try {
            replacements.forEach { replacement ->
                require(replacement.stagedFile.isFile) { "Missing staged file: ${replacement.stagedFile}" }
                val destination = replacement.destinationFile
                val parent = destination.absoluteFile.parentFile
                    ?: error("Destination has no parent: $destination")
                check(parent.isDirectory || parent.mkdirs()) { "Could not create destination directory: $parent" }

                val state = ReplacementState(replacement)
                states += state

                if (destination.exists()) {
                    state.backupFile = Files.createTempFile(
                        parent.toPath(),
                        "restore-${destination.name}-",
                        ".rollback",
                    ).toFile()
                    Files.copy(
                        destination.toPath(),
                        state.backupFile!!.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                    )
                    state.originalBackedUp = true
                }

                moveFile(replacement.stagedFile, destination)
                state.stagedFileInstalled = true
            }
        } catch (failure: Throwable) {
            states.asReversed().forEach { state ->
                try {
                    if (state.stagedFileInstalled ||
                        (!state.originalBackedUp && !state.replacement.stagedFile.exists())
                    ) {
                        Files.deleteIfExists(state.replacement.destinationFile.toPath())
                    }
                    if (state.originalBackedUp) {
                        moveFile(state.backupFile!!, state.replacement.destinationFile)
                        state.originalBackedUp = false
                    } else {
                        state.backupFile?.delete()
                    }
                } catch (rollbackFailure: Throwable) {
                    failure.addSuppressed(rollbackFailure)
                }
            }
            throw failure
        }

        states.forEach { it.backupFile?.delete() }
    }

    private fun moveAtomically(source: File, destination: File) {
        Files.move(
            source.toPath(),
            destination.toPath(),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING,
        )
    }
}
