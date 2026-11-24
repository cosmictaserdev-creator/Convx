package com.convx.music.playback

import kotlinx.serialization.Serializable

@Serializable
enum class EqualizerFilterType {
    PK,
    LSC,
    HSC,
    LPQ,
    HPQ,
}

@Serializable
data class EqualizerBand(
    val frequency: Double,
    val gain: Double,
    val q: Double = 1.41,
    val filterType: EqualizerFilterType = EqualizerFilterType.PK,
    val enabled: Boolean = true,
)

@Serializable
data class EqualizerProfile(
    val preamp: Double,
    val bands: List<EqualizerBand>,
    val metadata: Map<String, String> = emptyMap(),
) {
    companion object {
        const val MAX_BANDS = 20
    }
}
