package com.convx.music.playback

/** Whole segments covering the opening fraction of a finite HLS rendition. */
fun hlsPrefetchSegmentIndices(startTimesUs: List<Long>, durationUs: Long, denominator: Int): List<Int> {
    val target = upcomingCacheTarget(durationUs, denominator) ?: return emptyList()
    return startTimesUs.indices.filter { startTimesUs[it] >= 0 && startTimesUs[it] < target }
}

/** Prefer the requested end of the known bitrate range; keep playlist order for unknown rates. */
fun hlsPrefetchVariantIndex(bitrates: List<Int>, preferLow: Boolean): Int? {
    val known = bitrates.indices.filter { bitrates[it] > 0 }
    if (known.isEmpty()) return bitrates.indices.firstOrNull()
    return if (preferLow) known.minByOrNull { bitrates[it] } else known.maxByOrNull { bitrates[it] }
}
