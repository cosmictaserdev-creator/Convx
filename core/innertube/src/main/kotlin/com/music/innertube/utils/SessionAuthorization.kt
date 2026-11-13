package com.music.innertube.utils

internal fun sessionAuthorization(cookies: Map<String, String>, epochSeconds: Long, origin: String): String? {
    val session = sequenceOf("SAPISID", "__Secure-3PAPISID", "__Secure-1PAPISID")
        .mapNotNull { cookies[it]?.takeIf(String::isNotBlank) }
        .firstOrNull() ?: return null
    return "SAPISIDHASH ${epochSeconds}_${sha1("$epochSeconds $session $origin")}"
}
