package com.music.innertube

import com.music.innertube.utils.sessionAuthorization
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionAuthorizationTest {
    @Test fun secureCookieVariantsAuthenticateLikeTheUnprefixedCookie() {
        val expected = sessionAuthorization(mapOf("SAPISID" to "session"), 123L, "https://music.youtube.com")
        assertEquals(expected, sessionAuthorization(mapOf("__Secure-3PAPISID" to "session"), 123L, "https://music.youtube.com"))
        assertEquals(expected, sessionAuthorization(mapOf("__Secure-1PAPISID" to "session"), 123L, "https://music.youtube.com"))
        assertEquals(expected, sessionAuthorization(mapOf("SAPISID" to "", "__Secure-3PAPISID" to "session"), 123L, "https://music.youtube.com"))
    }

    @Test fun guestCookiesCannotCreateAnAuthorizationHeader() {
        assertNull(sessionAuthorization(mapOf("VISITOR_INFO1_LIVE" to "guest"), 123L, "https://music.youtube.com"))
    }
}
