package com.music.innertube

import com.music.innertube.utils.parseCookieString
import org.junit.Assert.*
import org.junit.Test

class RetryPolicyTest {
    @Test fun parsesRetryAfterWithoutOverflow() {
        assertEquals(120_000L, RetryPolicy.retryAfterMillis("120"))
        assertEquals(120_000L, RetryPolicy.retryAfterMillis("Wed, 21 Oct 2015 07:28:00 GMT", 1445412360000L))
        assertEquals(0L, RetryPolicy.retryAfterMillis("Wed, 21 Oct 2015 07:28:00 GMT", 1445412600000L))
        assertNull(RetryPolicy.retryAfterMillis("-1"))
        assertNull(RetryPolicy.retryAfterMillis("broken"))
        assertTrue(RetryPolicy.retryAfterMillis(Long.MAX_VALUE.toString())!! > 0)
    }

    @Test fun cookiesSupportWebViewFormattingAndEqualsInValues() {
        val cookies = parseCookieString(" SAPISID=abc;__Secure-3PAPISID=def; padded = x== ; invalid; =empty ")
        assertEquals(mapOf("SAPISID" to "abc", "__Secure-3PAPISID" to "def", "padded" to "x=="), cookies)
        assertTrue(parseCookieString("").isEmpty())
    }
}
