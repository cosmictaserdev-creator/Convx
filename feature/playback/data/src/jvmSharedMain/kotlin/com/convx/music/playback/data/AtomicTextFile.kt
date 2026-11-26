package com.convx.music.playback.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/** A text file written whole through a temporary file, so a crash never leaves half a file. */
internal class AtomicTextFile(private val file: File) {
    /** The text, or null when the file is missing or cannot be read. */
    suspend fun read(): String? = withContext(Dispatchers.IO) {
        try {
            file.takeIf { it.exists() }?.readText()
        } catch (_: IOException) {
            null
        }
    }

    suspend fun write(text: String) = withContext(Dispatchers.IO) {
        file.parentFile?.mkdirs()
        val temporary = File(file.parentFile, "${file.name}.tmp")
        temporary.writeText(text)
        if (!temporary.renameTo(file)) {
            file.delete()
            check(temporary.renameTo(file)) { "Could not replace ${file.name}" }
        }
    }
}
