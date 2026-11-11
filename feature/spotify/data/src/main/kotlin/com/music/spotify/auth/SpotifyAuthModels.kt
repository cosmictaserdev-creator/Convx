package com.music.spotify.auth

import kotlinx.serialization.Serializable
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

@Serializable
data class SpotifyTokens(
    val clientId: String,
    val accessToken: String,
    val refreshToken: String?,
    val expiresAtMillis: Long,
) {
    init {
        require(clientId.matches(Regex("[a-fA-F0-9]{32}")))
        require(accessToken.isNotBlank())
        require(expiresAtMillis > 0)
    }

    fun isFresh(nowMillis: Long, marginMillis: Long = 60_000): Boolean {
        require(marginMillis >= 0)
        return nowMillis >= 0 && nowMillis < expiresAtMillis && expiresAtMillis - nowMillis > marginMillis
    }

    override fun toString() = "SpotifyTokens(clientId=$clientId, expiresAtMillis=$expiresAtMillis)"
}

data class SpotifyAuthorization(val state: String, val verifier: String) {
    init {
        require(state.isNotBlank())
        require(verifier.matches(Regex("[A-Za-z0-9._~-]{43,128}")))
    }
    val challenge: String get() = challenge(verifier)

    override fun toString() = "SpotifyAuthorization"

    companion object {
        fun create(random: SecureRandom = SecureRandom()): SpotifyAuthorization = SpotifyAuthorization(
            encode(ByteArray(32).also(random::nextBytes)),
            encode(ByteArray(64).also(random::nextBytes)),
        )

        fun challenge(verifier: String): String = encode(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(StandardCharsets.US_ASCII)))

        private fun encode(bytes: ByteArray): String = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }
}

data class SpotifyAuthorizationCallback(val code: String?, val error: String?) {
    override fun toString() = "SpotifyAuthorizationCallback"
}

internal fun parseSpotifyCallback(target: String, expectedState: String): SpotifyAuthorizationCallback? {
    if (expectedState.isBlank() || !target.startsWith('/') || target.startsWith("//") || target.length > 8192) return null
    val uri = runCatching { URI(target) }.getOrNull() ?: return null
    if (uri.rawPath != "/login" || uri.rawFragment != null) return null
    val parameters = LinkedHashMap<String, String>()
    for (part in uri.rawQuery.orEmpty().split('&').filter(String::isNotEmpty)) {
        val pair = part.split('=', limit = 2)
        if (pair.size != 2) return null
        val name = runCatching { URLDecoder.decode(pair[0], "UTF-8") }.getOrNull() ?: return null
        val value = runCatching { URLDecoder.decode(pair[1], "UTF-8") }.getOrNull() ?: return null
        if (parameters.put(name, value) != null) return null
    }
    val state = parameters["state"] ?: return null
    if (!MessageDigest.isEqual(state.toByteArray(StandardCharsets.UTF_8), expectedState.toByteArray(StandardCharsets.UTF_8))) return null
    val code = parameters["code"]?.takeIf(String::isNotBlank)
    val error = parameters["error"]?.takeIf(String::isNotBlank)
    if ((code == null) == (error == null)) return null
    return SpotifyAuthorizationCallback(code, error)
}
