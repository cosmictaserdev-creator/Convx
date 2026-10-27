package com.convx.music.lyrics

/** Selects the main vocal row for a playback position, ignoring blank and background rows. */
inline fun <T> findPrimaryLyricLineIndex(
    lines: List<T>,
    positionMs: Long,
    timeMs: (T) -> Long,
    isBackground: (T) -> Boolean,
    text: (T) -> String,
): Int {
    var latestIndex = -1
    for (index in lines.indices) {
        val line = lines[index]
        if (!isBackground(line) && timeMs(line) <= positionMs && text(line).isNotBlank()) {
            latestIndex = index
        }
    }
    if (latestIndex < 0) return -1

    val latestTime = timeMs(lines[latestIndex])
    for (index in lines.indices) {
        val line = lines[index]
        if (!isBackground(line) && timeMs(line) == latestTime && text(line).isNotBlank()) {
            return index
        }
    }
    return -1
}
