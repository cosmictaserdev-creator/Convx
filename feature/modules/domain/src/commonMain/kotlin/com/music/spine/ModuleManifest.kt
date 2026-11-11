package com.music.spine

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class ModuleManifest(
    val settings: Map<String, ModuleSetting> = emptyMap(),
    val noStreamCache: Boolean = false,
) {
    val defaults: Map<String, String> get() = settings.mapNotNull { (key, setting) ->
        (setting.defaultValue as? JsonPrimitive)?.takeUnless { it == JsonNull }?.content?.let { key to it }
    }.toMap()
}

@Serializable
data class ModuleSetting(
    val type: String = "text",
    val label: String = "",
    val description: String = "",
    val options: List<ModuleSettingOption> = emptyList(),
    val defaultValue: JsonElement? = null,
)

@Serializable
data class ModuleSettingOption(val label: String = "", val value: String = "")
