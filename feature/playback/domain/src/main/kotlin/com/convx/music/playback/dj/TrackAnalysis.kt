package com.convx.music.playback.dj

/** Result of portable beat, key, and energy analysis for a track. */
class TrackAnalysis(
    val grid: BeatGrid,
    val key: MusicalKey? = null,
    val energyProfile: ByteArray = ByteArray(0),
) {
    fun copy(
        grid: BeatGrid = this.grid,
        key: MusicalKey? = this.key,
        energyProfile: ByteArray = this.energyProfile,
    ) = TrackAnalysis(grid, key, energyProfile)
}
