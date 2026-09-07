package com.convx.music.modulehost

import com.convx.music.BuildConfig
import com.convx.modulehost.DeclarativeModuleHost
import com.convx.modulehost.DeclarativeModuleHostLifecycle
import com.convx.modulehost.DeclarativeModuleHostSnapshot
import com.convx.modulehost.ModuleStore
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thrown when the picked package is older than the version installed on the
 * device. Rolling back to an older version is not a user action in this build;
 * the module keeps its newer version.
 */
class ModuleDowngradeRejectedException(detail: String) :
    IllegalStateException(detail)

/**
 * App-owned wrapper around the declarative module host.
 *
 * The Application calls [start] with its private files dir once at startup; the
 * store is then opened (registry restored from disk) before the host starts.
 * All mutations return a fresh snapshot so callers never render stale state.
 */
@Singleton
class ConvxDeclarativeModuleHost @Inject constructor() {
    @Volatile
    private var host: DeclarativeModuleHost = DeclarativeModuleHost(BuildConfig.VERSION_NAME)

    @Synchronized
    fun start(storeRoot: File? = null) {
        if (storeRoot != null && !storeOpen) {
            host = DeclarativeModuleHost(BuildConfig.VERSION_NAME, ModuleStore(storeRoot.toPath()))
            storeOpen = true
        }
        if (host.snapshot().lifecycle == DeclarativeModuleHostLifecycle.NOT_STARTED) {
            host.start()
        }
    }

    fun snapshot(): DeclarativeModuleHostSnapshot = host.snapshot()

    /**
     * Install a package: fresh modules are installed, installed modules are
     * upgraded when the version is not older. Returns the fresh snapshot or
     * throws (validation failure, downgrade rejection).
     */
    @Synchronized
    fun install(packageBytes: ByteArray): DeclarativeModuleHostSnapshot {
        try {
            host.installNew(packageBytes)
        } catch (error: IllegalStateException) {
            // installNew rejects an older package after validation passed.
            throw ModuleDowngradeRejectedException(error.message ?: "older version")
        }
        return host.snapshot()
    }

    @Synchronized
    fun enable(moduleId: String): DeclarativeModuleHostSnapshot {
        host.enable(moduleId)
        return host.snapshot()
    }

    @Synchronized
    fun disable(moduleId: String): DeclarativeModuleHostSnapshot {
        host.disable(moduleId)
        return host.snapshot()
    }

    @Synchronized
    fun remove(moduleId: String): DeclarativeModuleHostSnapshot {
        host.remove(moduleId)
        return host.snapshot()
    }

    private var storeOpen = false
}

object DeclarativeModuleHostRoutes {
    const val SETTINGS = "settings/declarative-modules"

    /** Generic per-module detail page: rendered from the module's declared data. */
    const val DETAIL = "settings/declarative-modules/{moduleId}"

    fun detail(moduleId: String): String = "settings/declarative-modules/$moduleId"
}
