package com.convx.music.playback

/** Keep content-identifying parameters while ignoring credentials that rotate between requests. */
fun hlsAudioCacheKey(mediaId: String, url: String): String {
    val clean = url.trim().substringBefore('#')
    val parameters = clean.substringAfter('?', "").split('&').filter { part ->
        val name = part.substringBefore('=').lowercase()
        name.isNotEmpty() && name !in hlsAuthenticationParameters && !name.startsWith("x-amz-")
    }.sorted()
    return "$mediaId#hls-audio:${clean.substringBefore('?')}" +
        if (parameters.isEmpty()) "" else "?${parameters.joinToString("&")}"
}

private val hlsAuthenticationParameters = setOf("token", "signature", "sig", "expires", "expiry", "exp", "policy", "key-pair-id")
