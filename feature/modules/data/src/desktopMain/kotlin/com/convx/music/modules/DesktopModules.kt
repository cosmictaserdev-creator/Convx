package com.convx.music.modules

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal actual fun moduleHttpClient() = HttpClient(OkHttp) { configureModuleHttp() }

class DesktopModuleStore(private val path: Path = defaultModulePath()) : ModuleStore {
    override suspend fun read(): String? = withContext(Dispatchers.IO) {
        if (Files.exists(path)) Files.readString(path) else null
    }
    override suspend fun write(value: String) = withContext(Dispatchers.IO) {
        val directory = path.toAbsolutePath().parent
        Files.createDirectories(directory)
        val temporary = Files.createTempFile(directory, "modules-", ".json")
        try {
            Files.writeString(temporary, value)
            try {
                Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temporary)
        }
        Unit
    }
}

private fun defaultModulePath(): Path {
    val home = System.getProperty("user.home")
    val directory = when {
        System.getProperty("os.name").startsWith("Windows") -> Path.of(System.getenv("APPDATA") ?: home, "Convx")
        System.getProperty("os.name").startsWith("Mac") -> Path.of(home, "Library", "Application Support", "Convx")
        else -> Path.of(System.getenv("XDG_CONFIG_HOME") ?: Path.of(home, ".config").toString(), "convx")
    }
    return directory.resolve("modules.json")
}
