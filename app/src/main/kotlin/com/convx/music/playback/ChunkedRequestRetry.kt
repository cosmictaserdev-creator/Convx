package com.convx.music.playback

internal fun <T> retryOnce(
    retryable: (Exception) -> Boolean,
    pause: (Long) -> Unit,
    operation: (attempt: Int) -> T,
): T {
    return try { operation(0) } catch (error: Exception) {
        if (!retryable(error)) throw error
        pause(350L)
        operation(1)
    }
}

internal fun String.withRequestNumber(number: Int): String {
    val fragment = substringAfter('#', "").takeIf { '#' in this }
    val base = substringBefore('#').substringBefore('?')
    val query = substringBefore('#').substringAfter('?', "").split('&')
        .filter { it.isNotEmpty() && it.substringBefore('=') != "rn" }
    return base + "?" + (query + "rn=$number").joinToString("&") +
        (fragment?.let { "#$it" } ?: "")
}
