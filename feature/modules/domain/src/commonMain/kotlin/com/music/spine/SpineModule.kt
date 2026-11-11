package com.music.spine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpineModule(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("author") val author: String = "",
    @SerialName("version") val version: String = "",
    @SerialName("code") val code: Int = 0,
    @SerialName("type") val type: String = "MODULE",
    @SerialName("description") val description: String = "",
    @SerialName("tags") private val declaredTags: List<String> = emptyList(),
    /** Some indexes publish module tags under `labels` instead of `tags`. */
    @SerialName("labels") private val declaredLabels: List<String> = emptyList(),
    @SerialName("size") val size: Long = 0,
    @SerialName("sizeLabel") val sizeLabel: String = "",
    @SerialName("download") val download: String = "",
    @SerialName("logo") val logo: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("lastUpdated") val lastUpdated: String = "",
    @SerialName("trusted") val trusted: Boolean = false,
    @SerialName("featured") val featured: Boolean = false,
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("sources") val sources: List<SpineSource> = emptyList(),
    @SerialName("sourceUrl") val sourceUrl: String = "",
) {
    val tags: List<String> get() = if (declaredTags.isNotEmpty()) declaredTags else declaredLabels

    val isLossless: Boolean get() = tags.any {
        it.contains("LOSSLESS", ignoreCase = true) || it.contains("HI-RES", ignoreCase = true) ||
            it.contains("FLAC", ignoreCase = true)
    }
    val hasHiRes: Boolean get() = tags.any { it.contains("HI-RES", ignoreCase = true) }
    val isDolbyAtmos: Boolean get() = tags.any {
        it.contains("ATMOS", ignoreCase = true) || it.contains("DOLBY", ignoreCase = true)
    }
}

@Serializable
data class SpineSource(
    @SerialName("name") val name: String = "",
    @SerialName("lang") val lang: String = "all",
    @SerialName("id") val id: String = "",
    @SerialName("baseUrl") val baseUrl: String = ".",
)
