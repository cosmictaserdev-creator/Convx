package com.convx.modulehost

/**
 * In-memory module registry implementing the declared lifecycle state machine.
 *
 * [onChange] is invoked after every committed transition so an owner (the host) can
 * persist state. Kotlin `@Synchronized` locks are reentrant, so the callback may
 * safely call back into this registry (e.g. [snapshot]) without deadlock.
 *
 * Staged installs track the pre-install [RegisteredModule] so a failed commit can
 * restore the previous manifest/version and mark the module [ModuleState.ROLLED_BACK].
 */
class DeclarativeModuleRegistry(
    private val onChange: (List<RegisteredModule>) -> Unit = {},
) {
    private val modules = linkedMapOf<String, RegisteredModule>()
    private val stagedPrevious = mutableMapOf<String, RegisteredModule>()

    @Synchronized
    fun registerValidated(module: ValidatedModulePackage): RegisteredModule {
        val id = module.manifest.id
        check(id !in modules) { "module is already registered: $id" }
        val registered = RegisteredModule(module.manifest, ModuleState.VALIDATED)
        modules[id] = registered
        onChange(snapshot())
        return registered
    }

    /** Begin an install/upgrade: VALIDATED, INSTALLED, or ROLLED_BACK -> STAGED. */
    @Synchronized
    fun stage(module: ValidatedModulePackage): RegisteredModule {
        val id = module.manifest.id
        val current = modules[id] ?: error("module is not registered: $id")
        check(current.state in STAGEABLE) { "cannot stage ${current.state} module: $id" }
        stagedPrevious[id] = current
        val staged = RegisteredModule(module.manifest, ModuleState.STAGED)
        modules[id] = staged
        return staged
    }

    /** Commit a staged install: STAGED -> INSTALLED, keeping the pre-upgrade
     *  enabled/disabled state so an upgrade never silently disables a module. */
    @Synchronized
    fun commitInstall(moduleId: String): RegisteredModule {
        val staged = modules[moduleId] ?: error("module is not registered: $moduleId")
        check(staged.state == ModuleState.STAGED) { "cannot commit ${staged.state} module: $moduleId" }
        val previousState = stagedPrevious.remove(moduleId)?.state
        val installed = if (previousState == ModuleState.ENABLED || previousState == ModuleState.DISABLED) {
            staged.copy(state = previousState)
        } else {
            staged.copy(state = ModuleState.INSTALLED)
        }
        modules[moduleId] = installed
        onChange(snapshot())
        return installed
    }

    /**
     * Roll back a failed staged install. A failure while upgrading an installed
     * module restores the previous manifest as ROLLED_BACK; a fresh install that
     * never had a previous version becomes FAILED.
     */
    @Synchronized
    fun failInstall(moduleId: String): RegisteredModule {
        val current = modules[moduleId] ?: error("module is not registered: $moduleId")
        check(current.state == ModuleState.STAGED) { "cannot fail ${current.state} module: $moduleId" }
        val previous = stagedPrevious.remove(moduleId)
        val restored = if (previous != null && previous.state != ModuleState.VALIDATED) {
            previous.copy(state = ModuleState.ROLLED_BACK)
        } else {
            current.copy(state = ModuleState.FAILED)
        }
        modules[moduleId] = restored
        onChange(snapshot())
        return restored
    }

    /**
     * Restore the pre-upgrade version with the previous manifest:
     * INSTALLED/DISABLED/ENABLED -> ROLLED_BACK.
     */
    @Synchronized
    fun rollback(moduleId: String, previousManifest: ModuleManifest): RegisteredModule {
        val current = modules[moduleId] ?: error("module is not registered: $moduleId")
        check(current.state in ROLLBACKABLE) { "cannot roll back ${current.state} module: $moduleId" }
        val rolledBack = RegisteredModule(previousManifest, ModuleState.ROLLED_BACK)
        modules[moduleId] = rolledBack
        onChange(snapshot())
        return rolledBack
    }

    @Synchronized
    fun markInstalled(moduleId: String): RegisteredModule = transition(
        moduleId,
        allowed = setOf(ModuleState.VALIDATED),
        next = ModuleState.INSTALLED,
    )

    @Synchronized
    fun enable(moduleId: String): RegisteredModule = transition(
        moduleId,
        allowed = setOf(ModuleState.INSTALLED, ModuleState.DISABLED, ModuleState.ROLLED_BACK),
        next = ModuleState.ENABLED,
    )

    @Synchronized
    fun disable(moduleId: String): RegisteredModule = transition(
        moduleId,
        allowed = setOf(ModuleState.ENABLED),
        next = ModuleState.DISABLED,
    )

    /** Forget a module entirely (uninstall). No-op when it is not registered. */
    @Synchronized
    fun remove(moduleId: String): Boolean {
        val removed = modules.remove(moduleId) != null
        if (removed) {
            stagedPrevious.remove(moduleId)
            onChange(snapshot())
        }
        return removed
    }

    @Synchronized
    fun get(moduleId: String): RegisteredModule? = modules[moduleId]

    @Synchronized
    fun snapshot(): List<RegisteredModule> = modules.values.toList()

    @Synchronized
    fun restore(modules: List<RegisteredModule>) {
        this.modules.clear()
        modules.forEach { this.modules[it.manifest.id] = it }
    }

    private fun transition(
        moduleId: String,
        allowed: Set<ModuleState>,
        next: ModuleState,
    ): RegisteredModule {
        val current = modules[moduleId] ?: error("module is not registered: $moduleId")
        check(current.state in allowed) {
            "cannot transition $moduleId from ${current.state} to $next"
        }
        val updated = current.copy(state = next)
        modules[moduleId] = updated
        onChange(snapshot())
        return updated
    }

    private companion object {
        val STAGEABLE = setOf(
            ModuleState.VALIDATED,
            ModuleState.INSTALLED,
            ModuleState.ENABLED,
            ModuleState.DISABLED,
            ModuleState.ROLLED_BACK,
        )
        val ROLLBACKABLE = setOf(
            ModuleState.INSTALLED,
            ModuleState.DISABLED,
            ModuleState.ENABLED,
        )
    }
}