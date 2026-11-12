package com.convx.music.ui.screens.settings

import org.junit.Assert.*
import org.junit.Test

class DiscordBrowserUserAgentTest {
    @Test fun keepsTheActualBrowserVersionWhileRemovingLegacyWebViewMarkers() {
        val result = discordBrowserUserAgent("Mozilla/5.0 (Linux; Android 16; Device; wv) AppleWebKit/537.36 Version/4.0 Chrome/140.0.0.0 Mobile Safari/537.36")
        assertFalse(result.contains("; wv"))
        assertFalse(result.contains("Version/4.0"))
        assertTrue(result.contains("Chrome/140.0.0.0"))
        assertTrue(result.contains("Android 16"))
    }

    @Test fun ordinaryBrowserAgentsArePreservedAndMissingAgentsHaveAFallback() {
        val agent = "Mozilla/5.0 Chrome/140.0.0.0 Mobile Safari/537.36"
        assertEquals(agent, discordBrowserUserAgent(agent))
        assertTrue(discordBrowserUserAgent(null).contains("Chrome/"))
    }
}
