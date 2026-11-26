package com.convx.music.canvas

/** Stable across rotating CDN signatures, distinct for different video URLs and renditions. */
fun canvasCacheKey(mediaId: String, url: String): String {
    val noFragment = url.trim().substringBefore('#')
    val path = noFragment.substringBefore('?')
    val identityParameters = noFragment.substringAfter('?', "").split('&')
        .filter { part ->
            val name = part.substringBefore('=').lowercase()
            name.isNotEmpty() && name !in TRANSIENT_PARAMETERS && !name.startsWith("x-amz-")
        }
        .sorted()
    return "$mediaId#canvas-v2:$path" +
        if (identityParameters.isEmpty()) "" else "?${identityParameters.joinToString("&")}"
}

private val TRANSIENT_PARAMETERS = setOf(
    "token", "signature", "sig", "expires", "expiry", "exp", "policy", "key-pair-id",
)
