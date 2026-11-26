package com.convx.music.playback

/** Identifies audio cache entries without mistaking a canvas or other sidecar for audio. */
fun isPlaybackAudioCacheKey(key: String): Boolean {
    val suffix = key.substringAfter('#', "")
    return suffix.isEmpty() || suffix == "aac" || suffix == "hq" ||
        (suffix.startsWith("source-") &&
            (!suffix.contains('#') || suffix.substringAfter('#') == "aac"))
}

/** Includes every rendition and sidecar for this song, but not IDs with a common prefix. */
fun cacheKeyBelongsToSong(key: String, songId: String): Boolean =
    key == songId || key.startsWith("$songId#")

/** Audio analysis may also use its assembled HLS head and jam-specific renditions. */
fun isAnalysisAudioCacheKey(key: String, songId: String): Boolean =
    cacheKeyBelongsToSong(key, songId) && (isPlaybackAudioCacheKey(key) ||
        key == "$songId#hls-analysis" || key == "$songId#jam-yt" || key == "$songId#jam-yt#aac")

/** Finds a complete progressive rendition, preferring the one matching current settings. */
fun chooseCompleteAudioCacheKey(
    songId: String,
    preferredKey: String,
    keys: Iterable<String>,
    isComplete: (String) -> Boolean,
): String? {
    if (cacheKeyBelongsToSong(preferredKey, songId) && isPlaybackAudioCacheKey(preferredKey) && isComplete(preferredKey)) {
        return preferredKey
    }
    return keys.asSequence()
        .filter { it != preferredKey && cacheKeyBelongsToSong(it, songId) && isPlaybackAudioCacheKey(it) }
        .sorted()
        .firstOrNull(isComplete)
}
