package com.convx.music.modulehost

import com.convx.modulehost.DeclarativeModuleHostLifecycle
import com.convx.modulehost.ModuleState
import com.convx.modulehost.ModuleValidationException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Contract for the host update-check flow: feed selection and digest
 * verification run through the real modulehost engine (ModuleUpdater +
 * ModuleVersion), fetching is injected, and applying an upgrade goes through
 * the same host install path the picker uses.
 */
class ModuleUpdateCoordinatorTest {

    @get:Rule
    val temporary = TemporaryFolder()

    private val feedUrl = "https://feed.example/releases/index.json"
    private val artifactUrl = { version: String -> "https://feed.example/SpaceMusic-$version.smod" }

    @Test
    fun `check reports a newer release when the feed is ahead`() = runBlocking {
        val host = hostWith("0.1.0")
        val artifact = packageBytes("0.1.1")
        val coordinator = coordinator(host, routes = mapOf(
            feedUrl to indexBytes("0.1.0", "0.1.1", artifact),
        ))

        val result = coordinator.checkForUpdates("spacemusic")

        assertTrue(result is ModuleUpdateCoordinator.CheckResult.Newer)
        val newer = result as ModuleUpdateCoordinator.CheckResult.Newer
        assertEquals("0.1.1", newer.release.version)
        assertEquals("0.1.0", newer.installedVersion)
    }

    @Test
    fun `check reports up to date when installed version equals the newest release`() = runBlocking {
        val artifact = packageBytes("0.1.1")
        val host = hostWith("0.1.1")
        val coordinator = coordinator(host, routes = mapOf(
            feedUrl to indexBytes("0.1.0", "0.1.1", artifact),
        ))

        assertEquals(
            ModuleUpdateCoordinator.CheckResult.UpToDate,
            coordinator.checkForUpdates("spacemusic"),
        )
    }

    @Test
    fun `check never suggests a downgrade when the feed is behind the installed version`() = runBlocking {
        val artifact = packageBytes("0.1.0")
        val host = hostWith("0.1.1")
        val coordinator = coordinator(host, routes = mapOf(
            feedUrl to indexBytes("0.1.0", "0.1.0", artifact),
        ))

        assertEquals(
            ModuleUpdateCoordinator.CheckResult.UpToDate,
            coordinator.checkForUpdates("spacemusic"),
        )
    }

    @Test
    fun `check reports no compatible release when every feed release needs a newer Convx`() = runBlocking {
        val artifact = packageBytes("0.1.1")
        val host = hostWith("0.1.0")
        val coordinator = coordinator(
            host,
            convxVersion = "1.5.2",
            routes = mapOf(feedUrl to indexBytes("0.1.0", "0.1.1", artifact, minConvx = "9.0.0")),
        )

        assertEquals(
            ModuleUpdateCoordinator.CheckResult.NoCompatibleRelease,
            coordinator.checkForUpdates("spacemusic"),
        )
    }

    @Test
    fun `check reports not installed when the module is absent`() = runBlocking {
        val host = ConvxDeclarativeModuleHost().apply { start() }
        val coordinator = coordinator(host, routes = emptyMap())

        assertEquals(
            ModuleUpdateCoordinator.CheckResult.ModuleNotInstalled,
            coordinator.checkForUpdates("spacemusic"),
        )
    }

    @Test
    fun `check rejects a malformed feed`() = runBlocking {
        val host = hostWith("0.1.0")
        val coordinator = coordinator(host, routes = mapOf(feedUrl to "{not json".encodeToByteArray()))

        val error = rejection { coordinator.checkForUpdates("spacemusic") }
        assertTrue(error is ModuleValidationException)
    }

    @Test
    fun `check propagates a fetch failure for an unreachable feed`() = runBlocking {
        val host = hostWith("0.1.0")
        val coordinator = ModuleUpdateCoordinator(host, "1.5.2") {
            throw IOException("HTTP 404 from $feedUrl")
        }

        val error = rejection { coordinator.checkForUpdates("spacemusic") }
        assertTrue(error is IOException)
    }

    @Test
    fun `applyUpdate downloads verifies digest and upgrades preserving state`() = runBlocking {
        val artifact = packageBytes("0.1.1")
        val host = hostWith("0.1.0")
        host.enable("spacemusic")
        val coordinator = coordinator(host, routes = mapOf(
            feedUrl to indexBytes("0.1.0", "0.1.1", artifact),
            artifactUrl("0.1.1") to artifact,
        ))

        val offer = coordinator.checkForUpdates("spacemusic") as ModuleUpdateCoordinator.CheckResult.Newer
        val snapshot = coordinator.applyUpdate(offer.release)

        val module = snapshot.modules.single()
        assertEquals("0.1.1", module.manifest.version)
        assertEquals(ModuleState.ENABLED, module.state)
    }

    @Test
    fun `applyUpdate refuses an artifact whose digest does not match the feed`() = runBlocking {
        val goodArtifact = packageBytes("0.1.1")
        val host = hostWith("0.1.0")
        val coordinator = coordinator(host, routes = mapOf(
            feedUrl to indexBytes("0.1.0", "0.1.1", goodArtifact),
            artifactUrl("0.1.1") to packageBytes("0.1.1", name = "Tampered"),
        ))

        val offer = coordinator.checkForUpdates("spacemusic") as ModuleUpdateCoordinator.CheckResult.Newer
        val error = rejection { coordinator.applyUpdate(offer.release) }
        assertTrue(error is ModuleValidationException)
        assertEquals("0.1.0", host.snapshot().modules.single().manifest.version)
    }

    private suspend fun rejection(block: suspend () -> Any?): Throwable? =
        try {
            block()
            null
        } catch (error: Throwable) {
            error
        }

    // --- fixtures ---------------------------------------------------------

    private fun hostWith(installed: String): ConvxDeclarativeModuleHost {
        val host = ConvxDeclarativeModuleHost()
        host.start(temporary.newFolder("store-$installed").toPath().toFile())
        assertEquals(DeclarativeModuleHostLifecycle.STARTED, host.snapshot().lifecycle)
        host.install(packageBytes(installed))
        return host
    }

    private fun coordinator(
        host: ConvxDeclarativeModuleHost,
        convxVersion: String = "1.5.2",
        routes: Map<String, ByteArray>,
    ): ModuleUpdateCoordinator = ModuleUpdateCoordinator(host, convxVersion) { url ->
        routes[url] ?: throw IOException("HTTP 404 from $url")
    }

    private fun indexBytes(oldest: String, newest: String, artifact: ByteArray, minConvx: String = "1.5.2"): ByteArray {
        val oldArtifact = packageBytes(oldest)
        return """
            {
              "${'$'}schema": "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/release-index.schema.json",
              "schemaVersion": "0.1",
              "moduleId": "spacemusic",
              "releases": [
                ${releaseJson(oldest, digestOf(oldArtifact), minConvx)},
                ${releaseJson(newest, digestOf(artifact), minConvx)}
              ]
            }
        """.trimIndent().encodeToByteArray()
    }

    private fun releaseJson(version: String, sha256: String, minConvx: String): String = """
        {
          "${'$'}schema": "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/release.schema.json",
          "schemaVersion": "0.1",
          "moduleId": "spacemusic",
          "version": "$version",
          "status": "published",
          "compatibility": {"minConvxVersion": "$minConvx", "maxConvxVersion": null},
          "artifact": "${artifactUrl(version)}",
          "sha256": "$sha256",
          "notes": "fixture $version"
        }
    """.trimIndent()

    private fun digestOf(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun packageBytes(version: String, name: String = "SpaceMusic"): ByteArray {
        val manifest = """
            {
              "${'$'}schema": "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/manifest.schema.json",
              "schemaVersion": "0.1",
              "id": "spacemusic",
              "name": "$name",
              "version": "$version",
              "type": "declarative",
              "description": "A declarative music module for Convx.",
              "author": "N7T0-OF",
              "compatibility": {"minConvxVersion": "1.5.2", "maxConvxVersion": null},
              "permissions": ["settings"],
              "content": {"module": "module/module.json", "icon": "assets/icon.svg"},
              "updates": {"releaseIndex": "$feedUrl"}
            }
        """.trimIndent()
        val module = """
            {
              "${'$'}schema": "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/module.schema.json",
              "schemaVersion": "0.1",
              "moduleId": "spacemusic",
              "contributions": {
                "settings": {
                  "section": "SpaceMusic",
                  "icon": "assets/icon.svg",
                  "actions": [{"id": "check-updates", "label": "Check for updates", "action": "check-updates"}]
                }
              }
            }
        """.trimIndent()
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(manifest.toByteArray())
            zip.closeEntry()
            zip.putNextEntry(ZipEntry("module/module.json"))
            zip.write(module.toByteArray())
            zip.closeEntry()
            zip.putNextEntry(ZipEntry("assets/icon.svg"))
            zip.write("<svg xmlns=\"http://www.w3.org/2000/svg\"/>".toByteArray())
            zip.closeEntry()
        }
        return output.toByteArray()
    }
}
