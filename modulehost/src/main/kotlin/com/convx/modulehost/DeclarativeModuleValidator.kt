package com.convx.modulehost

import java.net.URI
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class DeclarativeModuleValidator(currentConvxVersion: String) {
    private val currentConvxVersion = ModuleVersion.parse(currentConvxVersion, "Convx version")
    private val json = Json {
        ignoreUnknownKeys = false
        isLenient = false
        explicitNulls = true
    }

    fun validate(packageBytes: ByteArray): ValidatedModulePackage {
        val files = ModulePackageReader.read(packageBytes)
        val manifest = decode<ModuleManifest>(files.getValue("manifest.json"), "manifest.json")
        checkManifest(manifest)
        val module = decode<ModuleDocument>(files.getValue(manifest.content.module), manifest.content.module)
        checkModule(manifest, module)
        return ValidatedModulePackage(manifest, module, files)
    }

    private fun checkManifest(manifest: ModuleManifest) {
        checkSchemaReference(manifest.schema, "manifest.schema.json", "manifest schema")
        requireValid(manifest.schemaVersion == CONTRACT_VERSION, "unsupported module contract: ${manifest.schemaVersion}")
        requireValid(manifest.type == "declarative", "manifest.type must be declarative")
        requireValid(ID_PATTERN.matches(manifest.id), "manifest.id is invalid: ${manifest.id}")
        requireValid(manifest.name.isNotBlank() && manifest.name.length <= 80, "manifest.name is invalid")
        requireValid(manifest.description.isNotBlank() && manifest.description.length <= 280, "manifest.description is invalid")
        requireValid(manifest.author.isNotBlank() && manifest.author.length <= 120, "manifest.author is invalid")
        ModuleVersion.parse(manifest.version, "manifest.version")
        checkCompatibility(manifest.compatibility, "manifest.compatibility")
        requireValid(manifest.permissions.toSet() == SUPPORTED_PERMISSIONS && manifest.permissions.size == SUPPORTED_PERMISSIONS.size) {
            "v0.1 permits only the settings permission"
        }
        requireValid(manifest.content.module == "module/module.json", "manifest.content.module must be module/module.json")
        requireValid(manifest.content.icon == "assets/icon.svg", "manifest.content.icon must be assets/icon.svg")
        checkHttpsUrl(manifest.updates.releaseIndex, "manifest.updates.releaseIndex")
        requireValid(currentConvxVersion >= ModuleVersion.parse(manifest.compatibility.minConvxVersion, "manifest minimum")) {
            "module is incompatible with this Convx version"
        }
        manifest.compatibility.maxConvxVersion?.let {
            requireValid(currentConvxVersion <= ModuleVersion.parse(it, "manifest maximum")) {
                "module is incompatible with this Convx version"
            }
        }
    }

    private fun checkModule(manifest: ModuleManifest, module: ModuleDocument) {
        checkSchemaReference(module.schema, "module.schema.json", "module schema")
        requireValid(module.schemaVersion == CONTRACT_VERSION, "unsupported module contract: ${module.schemaVersion}")
        requireValid(module.moduleId == manifest.id, "module ID does not match manifest.id")
        val settings = module.contributions.settings
        requireValid(settings.section.isNotBlank() && settings.section.length <= 80, "Settings section is invalid")
        requireValid(settings.icon == manifest.content.icon, "Settings icon must use the manifest icon")
        requireValid(settings.actions.size == 1, "v0.1 requires exactly one Settings action")
        val action = settings.actions.single()
        requireValid(ACTION_ID_PATTERN.matches(action.id), "Settings action ID is invalid")
        requireValid(action.id == "check-updates" && action.action == "check-updates") {
            "v0.1 supports only the host-handled check-updates action"
        }
        requireValid(action.label.isNotBlank() && action.label.length <= 80, "Settings action label is invalid")
    }

    private fun checkCompatibility(value: ModuleCompatibility, label: String) {
        val minimum = ModuleVersion.parse(value.minConvxVersion, "$label.minConvxVersion")
        value.maxConvxVersion?.let {
            requireValid(ModuleVersion.parse(it, "$label.maxConvxVersion") >= minimum) {
                "$label maximum is older than minimum"
            }
        }
    }

    private fun checkSchemaReference(value: String, fileName: String, label: String) {
        requireValid(value == SCHEMA_BASE_URL + fileName) {
            "$label must reference the supplied $fileName schema"
        }
    }

    private fun checkHttpsUrl(value: String, label: String) {
        val uri = runCatching { URI(value) }.getOrNull()
        requireValid(
            uri != null && uri.scheme == "https" && !uri.host.isNullOrBlank() &&
                value.all { !it.isWhitespace() },
            "$label must be an HTTPS URL",
        )
    }

    private inline fun <reified T> decode(bytes: ByteArray, label: String): T {
        return try {
            json.decodeFromString(bytes.decodeToString())
        } catch (error: SerializationException) {
            throw ModuleValidationException("$label is not valid module JSON", error)
        }
    }

    private fun requireValid(condition: Boolean, message: String) {
        if (!condition) throw ModuleValidationException(message)
    }

    private inline fun requireValid(condition: Boolean, message: () -> String) {
        if (!condition) throw ModuleValidationException(message())
    }

    companion object {
        const val CONTRACT_VERSION = "0.1"
        private const val SCHEMA_BASE_URL =
            "https://raw.githubusercontent.com/N7T0-OF/Spacemusic/main/schemas/"
        private val ID_PATTERN = Regex("[a-z][a-z0-9-]{2,63}")
        private val ACTION_ID_PATTERN = Regex("[a-z][a-z0-9-]{0,63}")
        private val SUPPORTED_PERMISSIONS = setOf("settings")
    }
}
