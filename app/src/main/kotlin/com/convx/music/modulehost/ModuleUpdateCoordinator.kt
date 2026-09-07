package com.convx.music.modulehost

import com.convx.music.BuildConfig
import com.convx.modulehost.DeclarativeModuleHostSnapshot
import com.convx.modulehost.ModuleRelease
import com.convx.modulehost.ModuleUpdater
import com.convx.modulehost.ModuleValidationException
import com.convx.modulehost.ModuleVersion
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Host-side check/apply for a module's declared `updates.releaseIndex` feed.
 *
 * Feed fetching is injectable so the coordinator is unit-testable on the JVM;
 * the default implementation is a plain HTTPS GET. Selection and digest
 * verification run through the modulehost engine's [ModuleUpdater], and
 * applying an update goes through the host install path, which upgrades the
 * module while preserving its state.
 */
class ModuleUpdateCoordinator(
    private val host: ConvxDeclarativeModuleHost,
    private val currentConvxVersion: String = BuildConfig.VERSION_NAME,
    private val fetch: suspend (String) -> ByteArray = ModuleUpdateCoordinator::httpGet,
) {
    /** Outcome of an update check for one installed module. */
    sealed interface CheckResult {
        /** The feed has a newer published release compatible with this Convx version. */
        data class Newer(val release: ModuleRelease, val installedVersion: String) : CheckResult

        /** The feed parsed, but no published release is newer than the installed one. */
        data object UpToDate : CheckResult

        /** The feed parsed, but no published release is compatible with this Convx version. */
        data object NoCompatibleRelease : CheckResult

        /** The module is not currently installed. */
        data object ModuleNotInstalled : CheckResult
    }

    /**
     * Fetch the installed module's release index, select the newest compatible
     * published release via the engine's [ModuleUpdater], and report whether it
     * is newer than the installed version.
     */
    suspend fun checkForUpdates(moduleId: String): CheckResult {
        val installed = host.snapshot().modules.firstOrNull { it.manifest.id == moduleId }
            ?: return CheckResult.ModuleNotInstalled
        val feed = fetch(installed.manifest.updates.releaseIndex)
        val newest = ModuleUpdater(currentConvxVersion).latestRelease(feed, moduleId)
            ?: return CheckResult.NoCompatibleRelease
        val newestVersion = ModuleVersion.parse(newest.version, "feed release version")
        val installedVersion = ModuleVersion.parse(installed.manifest.version, "installed version")
        return if (newestVersion > installedVersion) {
            CheckResult.Newer(newest, installed.manifest.version)
        } else {
            CheckResult.UpToDate
        }
    }

    /** Download the release artifact, verify its declared SHA-256, then upgrade through the host. */
    suspend fun applyUpdate(release: ModuleRelease): DeclarativeModuleHostSnapshot {
        val artifactUrl = release.artifact
            ?: throw ModuleValidationException("published release ${release.version} has no artifact")
        val artifact = fetch(artifactUrl)
        ModuleUpdater(currentConvxVersion).verifyArtifact(release, artifact)
        return host.install(artifact)
    }

    companion object {
        private suspend fun httpGet(url: String): ByteArray {
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10_000
                connection.readTimeout = 20_000
                connection.instanceFollowRedirects = true
                val code = connection.responseCode
                if (code !in 200..299) {
                    throw IOException("HTTP $code from $url")
                }
                return connection.inputStream.use { it.readBytes() }
            } finally {
                connection.disconnect()
            }
        }
    }
}
