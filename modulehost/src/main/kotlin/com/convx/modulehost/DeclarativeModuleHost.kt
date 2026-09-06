package com.convx.modulehost

/**
 * Declarative module host.
 *
 * Without a [ModuleStore] the host is in-memory only: registry state is lost on
 * restart. With a store, every committed transition is persisted and package bytes
 * are staged/committed atomically under the store root.
 */
class DeclarativeModuleHost(
    val convxVersion: String,
    private val store: ModuleStore? = null,
) {
    private val validator = DeclarativeModuleValidator(convxVersion)
    private val registry = DeclarativeModuleRegistry(onChange = { modules -> store?.saveRegistry(modules) })
    private var lifecycle = DeclarativeModuleHostLifecycle.NOT_STARTED

    init {
        if (store != null) {
            registry.restore(store.loadRegistry())
        }
    }

    @Synchronized
    fun start() {
        check(lifecycle == DeclarativeModuleHostLifecycle.NOT_STARTED) { "declarative module host is already started" }
        lifecycle = DeclarativeModuleHostLifecycle.STARTED
    }

    @Synchronized
    fun snapshot(): DeclarativeModuleHostSnapshot = DeclarativeModuleHostSnapshot(
        lifecycle = lifecycle,
        convxVersion = convxVersion,
        modules = registry.snapshot(),
    )

    fun validate(packageBytes: ByteArray): ValidatedModulePackage = validator.validate(packageBytes)

    /**
     * Validate a package and register its module when not already registered.
     * Registering an already-registered module is a no-op (an upgraded install
     * keeps its existing registry entry until the staged commit replaces it).
     */
    @Synchronized
    fun validateAndRegister(packageBytes: ByteArray): RegisteredModule {
        checkStarted()
        val validated = validate(packageBytes)
        val current = registry.get(validated.manifest.id)
        return if (current == null) {
            registry.registerValidated(validated)
        } else {
            current
        }
    }

    /**
     * Install a package that is not yet installed, registering it when needed:
     * fresh package -> VALIDATED -> INSTALLED. Installing over an already
     * installed module upgrades it when the version is not older (see
     * [upgradeTo]).
     */
    @Synchronized
    fun installNew(packageBytes: ByteArray): RegisteredModule = upgradeTo(packageBytes)

    /**
     * Atomically install or upgrade a registered module.
     *
     * [expectedSha256], when given, is verified before staging. The package is
     * written to a staging slot, then committed; a failure restores the previous
     * package bytes and marks the module [ModuleState.ROLLED_BACK] (or FAILED when
     * there was no previous version). Version ordering is the caller's policy
     * (see [upgradeTo]); this method is mechanical staging/commit/rollback.
     */
    @Synchronized
    fun install(moduleId: String, packageBytes: ByteArray, expectedSha256: String? = null): RegisteredModule {
        checkStarted()
        expectedSha256?.let { ModuleIntegrity.verify(packageBytes, it) }
        val validated = validate(packageBytes)
        check(validated.manifest.id == moduleId) { "package manifest id does not match $moduleId" }

        registry.stage(validated)
        return try {
            store?.stagePackage(moduleId, packageBytes)
            store?.commitPackage(moduleId)
            registry.commitInstall(moduleId)
        } catch (error: Exception) {
            runCatching { store?.rollbackPackage(moduleId) }
            registry.failInstall(moduleId)
            throw ModuleValidationException("install of $moduleId failed and was rolled back", error)
        }
    }

    /**
     * Upgrade an installed module to a newer package, or install it when it is
     * not installed yet. Installing the same version (repair) is allowed;
     * installing an older version is rejected - use [rollback] to go back.
     */
    @Synchronized
    fun upgradeTo(packageBytes: ByteArray): RegisteredModule {
        checkStarted()
        val validated = validate(packageBytes)
        val moduleId = validated.manifest.id
        val current = registry.get(moduleId)
        if (current == null) {
            registry.registerValidated(validated)
        } else if (current.state != ModuleState.VALIDATED) {
            checkNotOlder(current, validated)
        }
        return install(moduleId, packageBytes)
    }

    private fun checkNotOlder(current: RegisteredModule, validated: ValidatedModulePackage) {
        val installed = ModuleVersion.parse(current.manifest.version, "installed version")
        val incoming = ModuleVersion.parse(validated.manifest.version, "package version")
        check(installed <= incoming) {
            "cannot install ${validated.manifest.version}: module ${current.manifest.id} is already at ${current.manifest.version}"
        }
    }

    /** Restore the pre-upgrade package bytes and mark the module rolled back. */
    @Synchronized
    fun rollback(moduleId: String): RegisteredModule {
        checkStarted()
        val previousBytes = store?.previousPackage(moduleId)
            ?: throw ModuleValidationException("module $moduleId has no previous version to roll back to")
        // Revalidate before restoring: never restore bytes that no longer validate.
        val previousManifest = validate(previousBytes).manifest
        val restored = registry.rollback(moduleId, previousManifest)
        store.rollbackPackage(moduleId)
        return restored
    }

    @Synchronized
    fun markInstalled(moduleId: String): RegisteredModule {
        checkStarted()
        return registry.markInstalled(moduleId)
    }

    @Synchronized
    fun enable(moduleId: String): RegisteredModule {
        checkStarted()
        return registry.enable(moduleId)
    }

    @Synchronized
    fun disable(moduleId: String): RegisteredModule {
        checkStarted()
        return registry.disable(moduleId)
    }

    /**
     * Uninstall a module: drop its registry entry and, when a store is present,
     * delete its package directory. Returns whether the module was registered.
     */
    @Synchronized
    fun remove(moduleId: String): Boolean {
        checkStarted()
        if (registry.get(moduleId) == null) return false
        registry.remove(moduleId)
        store?.deleteModule(moduleId)
        return true
    }

    @Synchronized
    fun registered(moduleId: String): RegisteredModule? = registry.get(moduleId)

    private fun checkStarted() {
        check(lifecycle == DeclarativeModuleHostLifecycle.STARTED) {
            "declarative module host has not been started"
        }
    }
}