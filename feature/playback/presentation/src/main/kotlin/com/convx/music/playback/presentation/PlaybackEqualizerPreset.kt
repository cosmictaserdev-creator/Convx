package com.convx.music.playback.presentation

import com.convx.music.playback.EqualizerBand
import com.convx.music.playback.EqualizerProfile

enum class PlaybackEqualizerPreset(
    val title: String,
    private val gainsDb: List<Double>,
) {
    FLAT("Flat", listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)),
    BASS_BOOST("Bass", listOf(5.0, 4.0, 3.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)),
    VOCAL("Vocal", listOf(-2.0, -1.0, 0.0, 1.0, 2.0, 2.0, 1.0, 0.0, -1.0, -2.0)),
    TREBLE("Treble", listOf(-2.0, -1.0, 0.0, 0.0, 0.0, 1.0, 2.0, 3.0, 4.0, 4.0));

    fun profile(): EqualizerProfile = EqualizerProfile(
        preamp = 0.0,
        bands = CENTER_FREQUENCIES_HZ.mapIndexed { index, frequency ->
            EqualizerBand(frequency = frequency, gain = gainsDb[index])
        },
    )

    fun matches(candidate: EqualizerProfile): Boolean {
        val preset = profile()
        return candidate.preamp == preset.preamp && candidate.bands == preset.bands
    }

    companion object {
        private val CENTER_FREQUENCIES_HZ = listOf(
            31.0,
            62.0,
            125.0,
            250.0,
            500.0,
            1_000.0,
            2_000.0,
            4_000.0,
            8_000.0,
            16_000.0,
        )
    }
}
