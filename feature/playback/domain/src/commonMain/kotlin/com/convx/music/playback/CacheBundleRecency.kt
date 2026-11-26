package com.convx.music.playback

/** A bundle is recent if any of its resources was used recently. */
fun leastRecentlyUsedCacheBundle(resourceTouches: Sequence<Pair<String, Long>>): String? {
    val lastUse = mutableMapOf<String, Long>()
    resourceTouches.forEach { (songId, touchedAt) ->
        lastUse[songId] = maxOf(lastUse[songId] ?: Long.MIN_VALUE, touchedAt)
    }
    return lastUse.minByOrNull { it.value }?.key
}
