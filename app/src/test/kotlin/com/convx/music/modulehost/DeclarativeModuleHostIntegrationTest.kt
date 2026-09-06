package com.convx.music.modulehost

import com.convx.modulehost.DeclarativeModuleHostLifecycle
import com.convx.modulehost.ModuleState
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DeclarativeModuleHostIntegrationTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `application host starts idempotently and exposes its settings route`() {
        val host = ConvxDeclarativeModuleHost()

        assertEquals(DeclarativeModuleHostLifecycle.NOT_STARTED, host.snapshot().lifecycle)
        host.start()
        host.start()

        assertEquals(DeclarativeModuleHostLifecycle.STARTED, host.snapshot().lifecycle)
        assertEquals("settings/declarative-modules", DeclarativeModuleHostRoutes.SETTINGS)
    }

    @Test
    fun `store-backed host persists an installed module across restart`() {
        val storeRoot = temporary.newFolder("modules").toPath()
        val bytes = smodPackage()

        val first = ConvxDeclarativeModuleHost()
        first.start(storeRoot.toFile())
        assertEquals(ModuleState.INSTALLED, first.install(bytes).modules.single().state)

        val reloaded = ConvxDeclarativeModuleHost()
        reloaded.start(storeRoot.toFile())
        val module = reloaded.snapshot().modules.single()
        assertEquals("spacemusic", module.manifest.id)
        assertEquals(ModuleState.INSTALLED, module.state)
    }

    @Test
    fun `installing a module that is already present reports the typed already-installed error`() {
        val host = ConvxDeclarativeModuleHost()
        host.start(temporary.newFolder("modules3").toPath().toFile())
        val bytes = smodPackage()
        host.install(bytes)

        try {
            host.install(bytes)
        } catch (error: ModuleAlreadyInstalledException) {
            // The engine detail is preserved for the dialog's secondary text; the
            // primary message is the localized already-installed string.
            assertEquals("module spacemusic is already installed; upgrade with install()", error.message)
            assertEquals(ModuleState.INSTALLED, host.snapshot().modules.single().state)
            return
        }
        throw AssertionError("a duplicate install must be rejected with ModuleAlreadyInstalledException")
    }

    @Test
    fun `module can be enabled disabled and removed through the app host`() {
        val storeRoot = temporary.newFolder("modules2").toPath()
        val host = ConvxDeclarativeModuleHost()
        host.start(storeRoot.toFile())
        host.install(smodPackage())

        assertEquals(ModuleState.ENABLED, host.enable("spacemusic").modules.single().state)
        assertEquals(ModuleState.DISABLED, host.disable("spacemusic").modules.single().state)

        assertTrue(host.remove("spacemusic").modules.isEmpty())
        assertTrue(Files.notExists(storeRoot.resolve("modules/spacemusic/current.smod")))
    }

    private fun smodPackage(): ByteArray {
        val manifest = """
            {
              "${'$'}schema":"https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/manifest.schema.json",
              "schemaVersion":"0.1",
              "id":"spacemusic",
              "name":"SpaceMusic",
              "version":"0.1.0",
              "type":"declarative",
              "description":"A declarative music module for Convx.",
              "author":"N7T0-OF",
              "compatibility":{"minConvxVersion":"1.5.2","maxConvxVersion":null},
              "permissions":["settings"],
              "content":{"module":"module/module.json","icon":"assets/icon.svg"},
              "updates":{"releaseIndex":"https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/releases/index.json"}
            }
        """.trimIndent()
        val module = """
            {
              "${'$'}schema":"https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/module.schema.json",
              "schemaVersion":"0.1",
              "moduleId":"spacemusic",
              "contributions":{
                "settings":{
                  "section":"SpaceMusic",
                  "icon":"assets/icon.svg",
                  "actions":[{"id":"check-updates","label":"Check for updates","action":"check-updates"}]
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
