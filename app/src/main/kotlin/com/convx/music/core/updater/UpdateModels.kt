package com.convx.music.core.updater

import kotlinx.serialization.Serializable

@Serializable
data class ChangelogSection(val title: String, val items: List<String>)

@Serializable
data class UpdateAsset(
    val name: String,
    val url: String,
    val size: Long,
    val sha256: String,
    val versionCode: Long,
    val variant: String,
    val abis: List<String>,
    val minSdk: Int,
)

@Serializable
data class UpdateRelease(
    val id: String,
    val version: String,
    val publishedAt: String,
    val description: String,
    val imageUrl: String? = null,
    val changelog: List<ChangelogSection> = emptyList(),
    val asset: UpdateAsset,
    val releaseUrl: String = "",
)

enum class UpdatePhase { IDLE, CHECKING, AVAILABLE, QUEUED, DOWNLOADING, VERIFYING, READY, INSTALLING, AWAITING_APPROVAL, INSTALLED, FAILED }

data class UpdateUiState(
    val release: UpdateRelease? = null,
    val phase: UpdatePhase = UpdatePhase.IDLE,
    val progress: Float? = null,
    val error: UpdateFailure? = null,
    val floatingVisible: Boolean = false,
    val enabled: Boolean = true,
)

enum class UpdateFailure { NETWORK, RATE_LIMIT, METADATA, STORAGE, VERIFICATION, INCOMPATIBLE, INSTALL_BLOCKED, INSTALL_CANCELLED, INSTALL_FAILED, UNAVAILABLE }

class UpdateException(val failure: UpdateFailure, cause: Throwable? = null) : Exception(failure.name, cause)

object UpdatePolicy {
    const val SNOOZE_MILLIS = 24 * 60 * 60 * 1000L
    const val CHECK_INTERVAL_MILLIS = 6 * 60 * 60 * 1000L

    fun shouldPrompt(releaseId: String, dismissedId: String?, snoozedId: String?, snoozedUntil: Long, now: Long): Boolean =
        releaseId != dismissedId && (releaseId != snoozedId || now >= snoozedUntil)

    fun selectAsset(assets: List<UpdateAsset>, variant: String, abis: List<String>, sdk: Int, installedCode: Long): UpdateAsset? =
        assets.filter { it.variant == variant && it.minSdk <= sdk && it.versionCode > installedCode && it.abis.any(abis::contains) }
            .sortedWith(compareBy<UpdateAsset> { asset -> abis.indexOfFirst { it in asset.abis } }.thenBy { it.abis.size })
            .firstOrNull()

    fun validDigest(value: String): Boolean = value.matches(Regex("[a-fA-F0-9]{64}"))

    fun releaseVersionCode(version: String, nightlyRun: Int = 0): Long? {
        val clean = version.removePrefix("v").removePrefix("b")
        if (!clean.matches(Regex("\\d+\\.\\d+\\.\\d+")) || nightlyRun !in 0..9_998) return null
        val parts = clean.split('.').map { it.toLongOrNull() ?: return null }
        return (parts[0] * 10_000 + parts[1] * 100 + parts[2]) * 10_000 + if (nightlyRun == 0) 9_999 else nightlyRun
    }

    fun isTransfer(phase: UpdatePhase): Boolean = phase in setOf(UpdatePhase.QUEUED, UpdatePhase.DOWNLOADING, UpdatePhase.VERIFYING, UpdatePhase.INSTALLING, UpdatePhase.AWAITING_APPROVAL)
}
