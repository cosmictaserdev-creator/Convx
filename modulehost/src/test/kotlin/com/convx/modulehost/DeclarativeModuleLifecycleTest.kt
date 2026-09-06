package com.convx.modulehost

import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DeclarativeModuleLifecycleTest {
    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `host persists registry and package bytes across restarts`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val bytes = TestModulePackages.packageBytes()

        val first = DeclarativeModuleHost("1.5.2", store)
        first.start()
        first.validateAndRegister(bytes)
        first.install("spacemusic", bytes)
        first.enable("spacemusic")
        assertEquals(ModuleState.ENABLED, first.registered("spacemusic")?.state)

        val reloaded = DeclarativeModuleHost("1.5.2", store)
        reloaded.start()
        assertEquals(ModuleState.ENABLED, reloaded.registered("spacemusic")?.state)
        assertTrue("package bytes must survive restart", store.currentPackage("spacemusic")!!.contentEquals(bytes))
    }

    @Test
    fun `install verifies sha256 and rejects a mismatched digest without side effects`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val host = DeclarativeModuleHost("1.5.2", store)
        host.start()
        val bytes = TestModulePackages.packageBytes()
        host.validateAndRegister(bytes)

        val expected = ModuleIntegrity.sha256Hex(bytes)
        assertEquals(ModuleState.INSTALLED, host.install("spacemusic", bytes, expected).state)

        val different = TestModulePackages.packageBytes(
            manifest = TestModulePackages.manifestJson(version = "0.1.1"),
        )
        assertRejected {
            host.install("spacemusic", different, expected)
        }
        assertEquals("failed upgrade must leave the installed version untouched", "0.1.0",
            host.registered("spacemusic")?.manifest?.version)
        assertTrue(store.currentPackage("spacemusic")!!.contentEquals(bytes))
    }

    @Test
    fun `install rejects a malformed sha256 before staging`() {
        val host = DeclarativeModuleHost("1.5.2")
        host.start()
        host.validateAndRegister(TestModulePackages.packageBytes())
        assertRejected {
            host.install("spacemusic", TestModulePackages.packageBytes(), "not-a-hex-digest")
        }
        assertEquals(ModuleState.VALIDATED, host.registered("spacemusic")?.state)
    }

    @Test
    fun `upgrade keeps the previous version and rollback restores it`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val host = DeclarativeModuleHost("1.5.2", store)
        host.start()
        val v0 = TestModulePackages.packageBytes()
        host.validateAndRegister(v0)
        host.install("spacemusic", v0)

        val v1 = TestModulePackages.packageBytes(
            manifest = TestModulePackages.manifestJson(version = "0.1.1"),
        )
        assertEquals("0.1.1", host.install("spacemusic", v1).manifest.version)
        assertTrue("previous package must be retained", store.previousPackage("spacemusic")!!.contentEquals(v0))

        val rolledBack = host.rollback("spacemusic")
        assertEquals(ModuleState.ROLLED_BACK, rolledBack.state)
        assertEquals("0.1.0", rolledBack.manifest.version)
        assertTrue("rollback must restore the previous bytes", store.currentPackage("spacemusic")!!.contentEquals(v0))
    }

    @Test
    fun `failed staged install rolls back to the previous version`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val host = DeclarativeModuleHost("1.5.2", store)
        host.start()
        val v0 = TestModulePackages.packageBytes()
        host.validateAndRegister(v0)
        host.install("spacemusic", v0)

        // Force the staging write to fail: a non-empty directory named staged.smod
        // cannot be replaced by the staged file on any platform.
        val moduleDir = temporary.root.toPath().resolve("store").resolve("modules").resolve("spacemusic")
        Files.createDirectories(moduleDir.resolve("staged.smod"))
        Files.writeString(moduleDir.resolve("staged.smod").resolve("blocker"), "x")

        val v1 = TestModulePackages.packageBytes(
            manifest = TestModulePackages.manifestJson(version = "0.1.1"),
        )
        assertRejected { host.install("spacemusic", v1) }
        assertEquals(ModuleState.ROLLED_BACK, host.registered("spacemusic")?.state)
        assertEquals("0.1.0", host.registered("spacemusic")?.manifest?.version)
        assertTrue("failed upgrade must keep the installed bytes", store.currentPackage("spacemusic")!!.contentEquals(v0))
    }

    @Test
    fun `rollback without a previous version is rejected`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val host = DeclarativeModuleHost("1.5.2", store)
        host.start()
        host.validateAndRegister(TestModulePackages.packageBytes())
        host.install("spacemusic", TestModulePackages.packageBytes())
        assertRejected { host.rollback("spacemusic") }
    }

    @Test
    fun `installNew registers a fresh package and remove uninstalls it`() {
        val store = ModuleStore(temporary.newFolder("store").toPath())
        val host = DeclarativeModuleHost("1.5.2", store)
        host.start()
        val bytes = TestModulePackages.packageBytes()

        assertEquals(ModuleState.INSTALLED, host.installNew(bytes).state)
        assertStateRejected { host.installNew(bytes) }
        assertTrue("remove must report the module was present", host.remove("spacemusic"))
        assertTrue(host.snapshot().modules.isEmpty())
        assertEquals("store must forget the package bytes", null, store.currentPackage("spacemusic"))
        assertTrue("removing an unknown module is a no-op", !host.remove("spacemusic"))
    }

    @Test
    fun `registry rejects invalid staged lifecycle transitions`() {
        val registry = DeclarativeModuleRegistry()
        val host = DeclarativeModuleHost("1.5.2")
        host.start()
        val validated = host.validate(TestModulePackages.packageBytes())
        registry.registerValidated(validated)

        // A brand-new module that fails while staged has no previous version: FAILED.
        registry.stage(validated)
        assertEquals(ModuleState.FAILED, registry.failInstall("spacemusic").state)
        assertStateRejected { registry.failInstall("spacemusic") }
        assertStateRejected { registry.commitInstall("spacemusic") }
    }

    @Test
    fun `updater selects the newest published compatible release`() {
        val updater = ModuleUpdater("1.5.2")
        val digest = "ab".repeat(32)
        val index = TestModulePackages.releaseIndex(
            releases = listOf(
                TestModulePackages.releaseJson(version = "0.1.0", sha256 = digest),
                TestModulePackages.releaseJson(
                    version = "0.2.0",
                    status = "draft",
                    artifact = null,
                    sha256 = null,
                ),
                TestModulePackages.releaseJson(
                    version = "0.3.0",
                    compatibility = """{"minConvxVersion":"2.0.0","maxConvxVersion":null}""",
                    sha256 = digest,
                ),
                TestModulePackages.releaseJson(version = "0.1.5", sha256 = digest),
            ),
        )
        val latest = updater.latestRelease(index.encodeToByteArray(), "spacemusic")
        assertEquals("0.1.5", latest?.version)
    }

    @Test
    fun `updater ignores other modules and malformed feeds`() {
        val updater = ModuleUpdater("1.5.2")
        val digest = "ab".repeat(32)
        val index = TestModulePackages.releaseIndex(
            moduleId = "other-module",
            releases = listOf(TestModulePackages.releaseJson(version = "0.1.0", sha256 = digest)),
        )
        assertNull(updater.latestRelease(index.encodeToByteArray(), "spacemusic"))
        assertRejected { updater.latestRelease("{not json".encodeToByteArray(), "spacemusic") }
    }

    @Test
    fun `updater verifies the artifact against the declared sha256`() {
        val updater = ModuleUpdater("1.5.2")
        val artifact = TestModulePackages.packageBytes()
        val digest = ModuleIntegrity.sha256Hex(artifact)
        val release = TestModulePackages.releaseJson(version = "0.1.0", sha256 = digest)
        val parsed = updater.latestRelease(
            TestModulePackages.releaseIndex(releases = listOf(release)).encodeToByteArray(),
            "spacemusic",
        ) ?: error("release not found")

        updater.verifyArtifact(parsed, artifact)
        assertRejected {
            updater.verifyArtifact(parsed.copy(sha256 = "cd".repeat(32)), artifact)
        }
        assertRejected {
            updater.verifyArtifact(parsed.copy(sha256 = null), artifact)
        }
    }

    private fun assertRejected(block: () -> Unit) {
        try {
            block()
        } catch (_: ModuleValidationException) {
            return
        }
        throw AssertionError("invalid module input was accepted")
    }

    private fun assertStateRejected(block: () -> Unit) {
        try {
            block()
        } catch (_: IllegalStateException) {
            return
        }
        throw AssertionError("invalid state transition was accepted")
    }
}