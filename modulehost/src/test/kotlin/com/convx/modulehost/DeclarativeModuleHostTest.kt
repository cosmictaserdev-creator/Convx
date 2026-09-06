package com.convx.modulehost

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeclarativeModuleHostTest {
    @Test
    fun `valid declarative package validates and follows registry lifecycle`() {
        val host = DeclarativeModuleHost("1.5.2")
        host.start()

        val registered = host.validateAndRegister(TestModulePackages.packageBytes())
        assertEquals("spacemusic", registered.manifest.id)
        assertEquals(ModuleState.VALIDATED, registered.state)

        assertEquals(ModuleState.INSTALLED, host.markInstalled("spacemusic").state)
        assertEquals(ModuleState.ENABLED, host.enable("spacemusic").state)
        assertEquals(ModuleState.DISABLED, host.disable("spacemusic").state)
        assertEquals(listOf(ModuleState.DISABLED), host.snapshot().modules.map { it.state })
    }

    @Test
    fun `archive reader accepts regular files and rejects unsafe or non-regular entries`() {
        val expected = TestModulePackages.packageEntries()
        assertPackageContents(expected, ModulePackageReader.read(TestModulePackages.zip(expected)))
        assertPackageContents(
            expected,
            ModulePackageReader.read(metadataArchive(expected, UNIX_REGULAR_MODE, "manifest.json")),
        )

        listOf(
            TestModulePackages.zip(expected + ("../escape.json" to "bad".encodeToByteArray())),
            TestModulePackages.zip(expected + ("/manifest.json" to "bad".encodeToByteArray())),
            TestModulePackages.zip(expected + ("module\\\\module.json" to "bad".encodeToByteArray())),
            duplicateManifestArchive(expected),
            TestModulePackages.zip(expected + ("README.md" to "bad".encodeToByteArray())),
            metadataArchive(expected, DOS_DIRECTORY_ATTRIBUTE, "manifest.json"),
            metadataArchive(expected, UNIX_SYMLINK_MODE, "assets/icon.svg"),
            malformedCentralDirectoryArchive(expected),
            centralDirectoryGapArchive(expected),
        ).forEach { archive ->
            assertRejected { ModulePackageReader.read(archive) }
        }
    }

    @Test
    fun `host lifecycle starts once and gates registration`() {
        val host = DeclarativeModuleHost("1.5.2")

        assertEquals(DeclarativeModuleHostLifecycle.NOT_STARTED, host.snapshot().lifecycle)
        assertStateRejected { host.validateAndRegister(TestModulePackages.packageBytes()) }

        host.start()
        assertEquals(DeclarativeModuleHostLifecycle.STARTED, host.snapshot().lifecycle)
        assertStateRejected { host.start() }
    }

    @Test
    fun `package reader rejects empty and malformed archives`() {
        assertRejected { ModulePackageReader.read(byteArrayOf()) }
        assertRejected { ModulePackageReader.read("not a zip".encodeToByteArray()) }
    }

    @Test
    fun `validator rejects non-declarative module types`() {
        assertRejected {
            DeclarativeModuleHost("1.5.2").validate(
                TestModulePackages.packageBytes(
                    manifest = TestModulePackages.manifestJson(type = "executable"),
                ),
            )
        }
    }

    @Test
    fun `validator rejects unsupported permissions and actions`() {
        assertRejected {
            DeclarativeModuleHost("1.5.2").validate(
                TestModulePackages.packageBytes(
                    manifest = TestModulePackages.manifestJson(permissions = "[\"settings\",\"network\"]"),
                ),
            )
        }
        assertRejected {
            DeclarativeModuleHost("1.5.2").validate(
                TestModulePackages.packageBytes(
                    module = TestModulePackages.moduleJson(action = "open"),
                ),
            )
        }
    }

    @Test
    fun `validator rejects module identity and incompatible Convx versions`() {
        assertRejected {
            DeclarativeModuleHost("1.5.2").validate(
                TestModulePackages.packageBytes(
                    module = TestModulePackages.moduleJson(moduleId = "other-module"),
                ),
            )
        }
        assertRejected {
            DeclarativeModuleHost("1.5.1").validate(TestModulePackages.packageBytes())
        }
    }

    @Test
    fun `validator rejects manifest identity compatibility and schema boundaries`() {
        listOf(
            TestModulePackages.manifestJson(id = "SpaceMusic"),
            TestModulePackages.manifestJson(compatibility = """{"minConvxVersion":"1.5.2","maxConvxVersion":"1.5.1"}"""),
            TestModulePackages.manifestJson(compatibility = """{"minConvxVersion":"1.0.0","maxConvxVersion":"1.5.1"}"""),
            TestModulePackages.manifestJson(schema = "${TestModulePackages.SCHEMA_BASE}other.schema.json"),
        ).forEach { manifest ->
            assertRejected {
                DeclarativeModuleHost("1.5.2").validate(TestModulePackages.packageBytes(manifest = manifest))
            }
        }
        assertRejected {
            DeclarativeModuleHost("1.5.2").validate(
                TestModulePackages.packageBytes(
                    module = TestModulePackages.moduleJson(schema = "${TestModulePackages.SCHEMA_BASE}other.schema.json"),
                ),
            )
        }
    }

    @Test
    fun `validator rejects malformed JSON SemVer and unknown manifest fields`() {
        listOf(
            "{",
            TestModulePackages.manifestJson(version = "0.1"),
            TestModulePackages.manifestJson(extra = ",\"unknown\":true"),
        ).forEach { manifest ->
            assertRejected {
                DeclarativeModuleHost("1.5.2").validate(TestModulePackages.packageBytes(manifest = manifest))
            }
        }
    }

    @Test
    fun `registry does not enable a module before installation`() {
        val host = DeclarativeModuleHost("1.5.2")
        host.start()
        host.validateAndRegister(TestModulePackages.packageBytes())

        assertStateRejected { host.enable("spacemusic") }
        assertTrue(host.registered("spacemusic")?.state == ModuleState.VALIDATED)
    }

    private fun assertPackageContents(expected: Map<String, ByteArray>, actual: Map<String, ByteArray>) {
        assertEquals(expected.keys, actual.keys)
        expected.forEach { (name, payload) ->
            assertTrue("archive payload differs for $name", payload.contentEquals(actual.getValue(name)))
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

    private fun metadataArchive(
        entries: Map<String, ByteArray>,
        externalAttributes: Int,
        target: String,
    ): ByteArray {
        val archive = TestModulePackages.zip(entries)
        val targetBytes = target.encodeToByteArray()
        var offset = 0
        var patched = false
        while (offset + CENTRAL_DIRECTORY_HEADER_SIZE <= archive.size) {
            if (readLittleEndianInt(archive, offset) != CENTRAL_DIRECTORY_SIGNATURE) {
                offset++
                continue
            }
            val nameLength = readLittleEndianShort(archive, offset + 28)
            val extraLength = readLittleEndianShort(archive, offset + 30)
            val commentLength = readLittleEndianShort(archive, offset + 32)
            val nameStart = offset + CENTRAL_DIRECTORY_HEADER_SIZE
            val entryEnd = nameStart + nameLength + extraLength + commentLength
            if (entryEnd > archive.size) break
            if (targetBytes.contentEquals(archive.copyOfRange(nameStart, nameStart + nameLength))) {
                writeLittleEndianInt(archive, offset + EXTERNAL_ATTRIBUTES_OFFSET, externalAttributes)
                patched = true
                break
            }
            offset = entryEnd
        }
        assertTrue("target archive entry was not found", patched)
        return archive
    }

    private fun readLittleEndianShort(bytes: ByteArray, offset: Int): Int =
        (bytes[offset].toInt() and 0xff) or
            ((bytes[offset + 1].toInt() and 0xff) shl 8)

    private fun readLittleEndianInt(bytes: ByteArray, offset: Int): Int =
        readLittleEndianShort(bytes, offset) or
            (readLittleEndianShort(bytes, offset + 2) shl 16)

    private fun writeLittleEndianInt(bytes: ByteArray, offset: Int, value: Int) {
        repeat(4) { index ->
            bytes[offset + index] = (value ushr (index * 8)).toByte()
        }
    }

    private fun malformedCentralDirectoryArchive(entries: Map<String, ByteArray>): ByteArray {
        val archive = TestModulePackages.zip(entries)
        var offset = 0
        while (offset + 4 <= archive.size && readLittleEndianInt(archive, offset) != CENTRAL_DIRECTORY_SIGNATURE) {
            offset++
        }
        assertTrue("central directory was not found", offset + 4 <= archive.size)
        writeLittleEndianInt(archive, offset, 0)
        return archive
    }

    private fun centralDirectoryGapArchive(entries: Map<String, ByteArray>): ByteArray {
        val archive = TestModulePackages.zip(entries)
        var endOffset = 0
        while (endOffset + 4 <= archive.size && readLittleEndianInt(archive, endOffset) != END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
            endOffset++
        }
        assertTrue("ZIP end record was not found", endOffset + 4 <= archive.size)
        val gap = byteArrayOf(0x55, 0x66, 0x77)
        return archive.copyOf(archive.size + gap.size).also { malformed ->
            archive.copyInto(malformed, endOffset + gap.size, endOffset)
            gap.copyInto(malformed, endOffset)
        }
    }

    private fun duplicateManifestArchive(entries: Map<String, ByteArray>): ByteArray {
        val archive = TestModulePackages.zipEntries(
            entries.entries.map { it.key to it.value } +
                ("manifest.jsox" to entries.getValue("manifest.json")),
        )
        val source = "manifest.jsox".encodeToByteArray()
        val replacement = "manifest.json".encodeToByteArray()
        return archive.copyOf().also { bytes ->
            for (offset in 0..bytes.size - source.size) {
                if (source.indices.all { index -> bytes[offset + index] == source[index] }) {
                    replacement.copyInto(bytes, offset)
                }
            }
        }
    }

    private companion object {
        const val CENTRAL_DIRECTORY_SIGNATURE = 0x02014b50
        const val END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50
        const val CENTRAL_DIRECTORY_HEADER_SIZE = 46
        const val EXTERNAL_ATTRIBUTES_OFFSET = 38
        const val DOS_DIRECTORY_ATTRIBUTE = 0x10
        val UNIX_REGULAR_MODE = (0x8000 or 0x1A4) shl 16
        val UNIX_SYMLINK_MODE = (0xA000 or 0x1FF) shl 16
    }
}