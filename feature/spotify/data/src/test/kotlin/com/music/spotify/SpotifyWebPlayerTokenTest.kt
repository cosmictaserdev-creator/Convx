package com.music.spotify

import org.junit.Assert.*
import org.junit.Test

class SpotifyWebPlayerTokenTest {
    @Test fun missingClientIdAndHeaderInjectionAreRejectedAndTokensAreRedacted() {
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"secret","accessTokenExpirationTimestampMs":2000}""", 1000))
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"secret\r\nheader","clientId":"client","accessTokenExpirationTimestampMs":2000}""", 1000))
        val token = SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"secret","clientId":"client","accessTokenExpirationTimestampMs":2000}""", 1000)
        assertNotNull(token)
        assertFalse(token.toString().contains("secret"))
    }
    @Test fun removesBothEmbeddedBrowserMarkers() {
        val userAgent = SpotifyWebPlayerSession.browserUserAgent("Mozilla/5.0 (Linux; Android 16; wv) AppleWebKit/537.36 Version/4.0 Chrome/140.0.0.0 Mobile Safari/537.36")
        assertFalse(userAgent.contains("; wv"))
        assertFalse(userAgent.contains("Version/4.0"))
        assertTrue(userAgent.contains("Chrome/140.0.0.0"))
    }

    @Test fun refusesAnonymousExpiredAndMalformedResponses() {
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"token","isAnonymous":true,"accessTokenExpirationTimestampMs":2000}""", 1000))
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"token","accessTokenExpirationTimestampMs":999}""", 1000))
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("{}", 1000))
        assertNull(SpotifyWebPlayerSession.parseWebPlayerToken("<html>error</html>", 1000))
        assertEquals("token", SpotifyWebPlayerSession.parseWebPlayerToken("""{"accessToken":"token","clientId":"client","accessTokenExpirationTimestampMs":2000}""", 1000)?.accessToken)
    }
}
