package com.convx.music.client.desktop

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

/** Atomic handoff from file associations to the one playback process for this profile. */
internal class DesktopFileOpenRequests(private val directory: File) {
    private val serializer = ListSerializer(String.serializer())

    fun submit(arguments: List<String>): Boolean {
        val paths = arguments.take(256).mapNotNull { argument ->
            runCatching { if (argument.startsWith("file:", true)) File(URI(argument)) else File(argument) }.getOrNull()
                ?.takeIf { it.isFile && it.extension.lowercase() in DesktopMediaLibrary.AUDIO_EXTENSIONS }
                ?.canonicalPath
        }.distinct()
        if (paths.isEmpty()) return false
        check(directory.isDirectory || directory.mkdirs()) { "Couldn't open the music request folder." }
        val request = File(directory, "${UUID.randomUUID()}.json")
        val temporary = File(directory, "${request.name}.tmp")
        try {
            temporary.writeText(Json.encodeToString(serializer, paths))
            try {
                Files.move(temporary.toPath(), request.toPath(), StandardCopyOption.ATOMIC_MOVE)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(temporary.toPath(), request.toPath())
            }
        } finally { temporary.delete() }
        return true
    }

    fun takePending(): List<List<File>> = directory.listFiles().orEmpty()
        .filter { it.extension == "json" && runCatching { UUID.fromString(it.nameWithoutExtension) }.isSuccess }
        .sortedWith(compareBy<File> { it.lastModified() }.thenBy { it.name }).take(16).mapNotNull { request ->
            try {
                if (request.length() > 1_048_576 || request.lastModified() < System.currentTimeMillis() - 120_000) null
                else Json.decodeFromString(serializer, request.readText()).take(256).map(::File)
            } catch (_: Exception) { null }
            finally { request.delete() }
        }
}
