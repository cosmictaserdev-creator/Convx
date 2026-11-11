package com.music.spotify.auth

import org.junit.Assert.*
import org.junit.Test

class SpotifyAuthorizationTest {
    @Test fun pkceMatchesTheRfc7636TestVector() {
        assertEquals("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM", SpotifyAuthorization.challenge("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"))
    }

    @Test fun grantsHaveIndependentStateAndUnpaddedUrlSafeVerifiers() {
        val first = SpotifyAuthorization.create()
        val second = SpotifyAuthorization.create()
        assertNotEquals(first.state, second.state)
        assertTrue(first.verifier.matches(Regex("[A-Za-z0-9_-]{43,128}")))
        assertTrue(first.challenge.matches(Regex("[A-Za-z0-9_-]{43}")))
        assertFalse(first.toString().contains(first.verifier))
        assertFalse(first.toString().contains(first.state))
    }

    @Test fun callbackDecodesCodeAndRequiresExactPathAndState() {
        assertEquals("one+two=", parseSpotifyCallback("/login?code=one%2Btwo%3D&state=ours", "ours")?.code)
        assertNull(parseSpotifyCallback("/login?code=ok&state=other", "ours"))
        assertNull(parseSpotifyCallback("/login-extra?code=ok&state=ours", "ours"))
        assertNull(parseSpotifyCallback("/favicon.ico", "ours"))
        assertNull(parseSpotifyCallback("http://other/login?code=ok&state=ours", "ours"))
        assertNull(parseSpotifyCallback("//other/login?code=ok&state=ours", "ours"))
    }

    @Test fun ambiguousOrMalformedCallbacksCannotCompleteAuthorization() {
        for (target in listOf(
            "/login?code=ok&state=ours&state=ours",
            "/login?code=one&code=two&state=ours",
            "/login?code=ok&%73tate=ours&state=ours",
            "/login?code=ok&error=denied&state=ours",
            "/login?code=&state=ours",
            "/login?code=ok&state=%ZZ",
            "/login?code=ok&state=ours#fragment",
        )) assertNull(target, parseSpotifyCallback(target, "ours"))
        assertNull(parseSpotifyCallback("/login?code=ok&state=", ""))
        assertNull(parseSpotifyCallback("/login?state=ours&code=" + "a".repeat(8192), "ours"))
        assertEquals("access_denied", parseSpotifyCallback("/login?state=ours&error=access_denied", "ours")?.error)
    }

    @Test fun expiryMarginDoesNotOverflowAndTokensDoNotLeakIntoDebugStrings() {
        val tokens = SpotifyTokens("0".repeat(32), "private-access", "private-refresh", Long.MAX_VALUE)
        assertTrue(tokens.isFresh(1))
        assertFalse(tokens.isFresh(Long.MAX_VALUE))
        assertFalse(tokens.isFresh(-1))
        assertFalse(tokens.isFresh(Long.MAX_VALUE - 60_000))
        assertFalse(tokens.toString().contains("private"))
        assertFalse(SpotifyAuthorizationCallback("private-code", null).toString().contains("private"))
    }
}
