package com.music.spotify

import org.junit.Assert.*
import org.junit.Test

class SpotifyWebPlayerTokenTest {
    @Test fun removesBothEmbeddedBrowserMarkers() {
        val userAgent = SpotifyAuth.browserUserAgent("Mozilla/5.0 (Linux; Android 16; wv) AppleWebKit/537.36 Version/4.0 Chrome/140.0.0.0 Mobile Safari/537.36")
        assertFalse(userAgent.contains("; wv"))
        assertFalse(userAgent.contains("Version/4.0"))
        assertTrue(userAgent.contains("Chrome/140.0.0.0"))
    }

    @Test fun refusesAnonymousExpiredAndMalformedResponses() {
        assertNull(SpotifyAuth.parseWebPlayerToken("""{"accessToken":"token","isAnonymous":true,"accessTokenExpirationTimestampMs":2000}""", 1000))
        assertNull(SpotifyAuth.parseWebPlayerToken("""{"accessToken":"token","accessTokenExpirationTimestampMs":999}""", 1000))
        assertNull(SpotifyAuth.parseWebPlayerToken("{}", 1000))
        assertNull(SpotifyAuth.parseWebPlayerToken("<html>error</html>", 1000))
        assertEquals("token", SpotifyAuth.parseWebPlayerToken("""{"accessToken":"token","clientId":"client","accessTokenExpirationTimestampMs":2000}""", 1000)?.accessToken)
    }
}
