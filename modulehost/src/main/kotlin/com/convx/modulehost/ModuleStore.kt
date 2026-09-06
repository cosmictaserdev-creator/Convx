package com.convx.modulehost

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * File-backed persistence for the declarative module host.
 *
 * Layout under [root]:
 *   registry.json                      - serialized [RegisteredModule] list
 *   modules/<id>/current.smod          - installed package bytes
 *   modules/<id>/previous.smod         - pre-upgrade package bytes (rollback point)
 *   modules/<id>/staged.smod           - in-progress install, removed on commit/rollback
 *
 * Every write goes through a temp file followed by an atomic-or-replacing move, so a
 * reader never observes a partially written file. Per-operation atomicity is guaranteed;
 * cross-file crash consistency (registry vs package bytes) is deliberately out of scope
 * for v0.1 and is repaired by revalidation on the next host start.
 */
class ModuleStore(private val root: Path) {
    private val registryFile = root.resolve("registry.json")
    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = true
    }

    fun saveRegistry(modules: List<RegisteredModule>) {
        val bytes = json.encodeToString(ListSerializer(RegisteredModule.serializer()), modules)
            .encodeToByteArray()
        writeAtomic(registryFile, bytes)
    }

    fun loadRegistry(): List<RegisteredModule> {
        if (!Files.exists(registryFile)) return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(RegisteredModule.serializer()), Files.readString(registryFile))
        }.getOrElse { error ->
            throw ModuleValidationException("module store registry is corrupt", error)
        }
    }

    fun currentPackage(moduleId: String): ByteArray? = readIfExists(packagePath(moduleId, "current.smod"))

    fun previousPackage(moduleId: String): ByteArray? = readIfExists(packagePath(moduleId, "previous.smod"))

    fun stagePackage(moduleId: String, bytes: ByteArray) = writeAtomic(packagePath(moduleId, "staged.smod"), bytes)

    fun commitPackage(moduleId: String) {
        val directory = moduleDir(moduleId)
        Files.createDirectories(directory)
        val current = directory.resolve("current.smod")
        val previous = directory.resolve("previous.smod")
        val staged = directory.resolve("staged.smod")
        if (Files.exists(current)) {
            moveReplacing(current, previous)
        }
        moveReplacing(staged, current)
    }

    /** Delete every stored artifact for a module (uninstall). */
    fun deleteModule(moduleId: String) {
        val dir = moduleDir(moduleId)
        if (Files.isDirectory(dir)) {
            Files.walk(dir).use { paths ->
                paths.sorted(Comparator.reverseOrder()).forEach(Files::delete)
            }
        }
    }

    fun rollbackPackage(moduleId: String) {
        val directory = moduleDir(moduleId)
        val previous = directory.resolve("previous.smod")
        // Restore the pre-upgrade bytes only when a backup exists; a failure before
        // commit must never remove the currently installed package.
        if (Files.exists(previous)) {
            moveReplacing(previous, directory.resolve("current.smod"))
        }
        val staged = directory.resolve("staged.smod")
        Files.deleteIfExists(staged)
        Files.deleteIfExists(staged.resolveSibling("staged.smod.tmp"))
    }

    private fun packagePath(moduleId: String, fileName: String): Path {
        require(SAFE_MODULE_ID.matches(moduleId)) { "unsafe module id for storage: $moduleId" }
        return moduleDir(moduleId).resolve(fileName)
    }

    private fun moduleDir(moduleId: String): Path = root.resolve("modules").resolve(moduleId)

    private fun writeAtomic(target: Path, bytes: ByteArray) {
        Files.createDirectories(target.parent)
        val temporary = target.resolveSibling(target.fileName.toString() + ".tmp")
        Files.write(temporary, bytes)
        moveReplacing(temporary, target)
    }

    private fun moveReplacing(source: Path, target: Path) {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        } catch (error: Exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun readIfExists(path: Path): ByteArray? =
        if (Files.exists(path)) Files.readAllBytes(path) else null

    private companion object {
        val SAFE_MODULE_ID = Regex("[a-z][a-z0-9-]{2,63}")
    }
}