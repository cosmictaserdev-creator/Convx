package com.convx.modulehost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ModuleRelease(
    @SerialName("${'$'}schema") val schema: String,
    val schemaVersion: String,
    val moduleId: String,
    val version: String,
    val status: String,
    val compatibility: ModuleCompatibility,
    val artifact: String?,
    val sha256: String?,
    val notes: String,
)

@Serializable
data class ReleaseIndex(
    @SerialName("${'$'}schema") val schema: String,
    val schemaVersion: String,
    val moduleId: String,
    val releases: List<ModuleRelease>,
)

/**
 * Minimal updater over the SpaceMusic release-index contract.
 *
 * Selection is declarative: only `published` releases whose compatibility range
 * includes the running Convx version are candidates, and the newest SemVer wins.
 * The host still owns downloading, network policy, and trusted-source policy;
 * this class only parses the feed and verifies an artifact's declared SHA-256.
 */
class ModuleUpdater(private val currentConvxVersion: String) {
    private val currentVersion = ModuleVersion.parse(currentConvxVersion, "Convx version")
    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = true
    }

    fun latestRelease(indexBytes: ByteArray, moduleId: String): ModuleRelease? {
        val index = try {
            json.decodeFromString<ReleaseIndex>(indexBytes.decodeToString())
        } catch (error: Exception) {
            throw ModuleValidationException("release index is not valid release JSON", error)
        }
        if (index.schemaVersion != "0.1") {
            throw ModuleValidationException("unsupported release index contract: ${index.schemaVersion}")
        }
        // A feed that does not describe this module has no candidates for it.
        if (index.moduleId != moduleId) return null
        return index.releases
            .filter { it.moduleId == moduleId }
            .filter { it.status == "published" }
            .filter { release ->
                val minimum = ModuleVersion.parse(release.compatibility.minConvxVersion, "release minimum")
                val maximum = release.compatibility.maxConvxVersion
                    ?.let { ModuleVersion.parse(it, "release maximum") }
                currentVersion >= minimum && (maximum == null || currentVersion <= maximum)
            }
            .maxByOrNull { ModuleVersion.parse(it.version, "release version") }
    }

    fun verifyArtifact(release: ModuleRelease, artifactBytes: ByteArray) {
        val digest = release.sha256
            ?: throw ModuleValidationException("published release ${release.version} has no sha256")
        ModuleIntegrity.verify(artifactBytes, digest)
    }
}