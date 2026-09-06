package com.convx.modulehost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModuleManifest(
    @SerialName("${'$'}schema") val schema: String,
    val schemaVersion: String,
    val id: String,
    val name: String,
    val version: String,
    val type: String,
    val description: String,
    val author: String,
    val compatibility: ModuleCompatibility,
    val permissions: List<String>,
    val content: ModuleContent,
    val updates: ModuleUpdates,
)

@Serializable
data class ModuleCompatibility(
    val minConvxVersion: String,
    val maxConvxVersion: String?,
)

@Serializable
data class ModuleContent(
    val module: String,
    val icon: String,
)

@Serializable
data class ModuleUpdates(
    val releaseIndex: String,
)

@Serializable
data class ModuleDocument(
    @SerialName("${'$'}schema") val schema: String,
    val schemaVersion: String,
    val moduleId: String,
    val contributions: ModuleContributions,
)

@Serializable
data class ModuleContributions(
    val settings: SettingsContribution,
)

@Serializable
data class SettingsContribution(
    val section: String,
    val icon: String,
    val actions: List<SettingsAction>,
)

@Serializable
data class SettingsAction(
    val id: String,
    val label: String,
    val action: String,
)

data class ValidatedModulePackage(
    val manifest: ModuleManifest,
    val module: ModuleDocument,
    val files: Map<String, ByteArray>,
)

enum class DeclarativeModuleHostLifecycle {
    NOT_STARTED,
    STARTED,
}

data class DeclarativeModuleHostSnapshot(
    val lifecycle: DeclarativeModuleHostLifecycle,
    val convxVersion: String,
    val modules: List<RegisteredModule>,
)

enum class ModuleState {
    DISCOVERED,
    VALIDATED,
    STAGED,
    INSTALLED,
    ENABLED,
    DISABLED,
    INVALID,
    INCOMPATIBLE,
    FAILED,
    ROLLED_BACK,
}

@Serializable
data class RegisteredModule(
    val manifest: ModuleManifest,
    val state: ModuleState,
)

class ModuleValidationException(message: String, cause: Throwable? = null) :
    IllegalArgumentException(message, cause)
