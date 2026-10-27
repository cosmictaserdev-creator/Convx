package com.convx.music.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Covers the decision YTPlayerUtils.playbackData's retry-and-rotate path relies
 * on: whether a playback failure is worth retrying at all. A geo-restricted
 * video fails identically on retry (guest session rotation or a signed-in
 * bare retry can't fix a region block), so both retry paths gate on this.
 */
class BotDetectionMitigatorTest {

    @Test
    fun `geo error signatures are detected`() {
        assertTrue(BotDetectionMitigator.isGeoError("This video is not available in your country"))
        assertTrue(BotDetectionMitigator.isGeoError("Content is geo-restricted"))
        assertTrue(BotDetectionMitigator.isGeoError("NOT_AVAILABLE_IN_THIS_COUNTRY"))
    }

    @Test
    fun `geo error detection is case-insensitive`() {
        assertTrue(BotDetectionMitigator.isGeoError("NOT AVAILABLE IN YOUR REGION"))
    }

    @Test
    fun `null or unrelated messages are not geo errors`() {
        assertFalse(BotDetectionMitigator.isGeoError(null))
        assertFalse(BotDetectionMitigator.isGeoError("Missing stream expire time"))
        assertFalse(BotDetectionMitigator.isGeoError("Bad stream player response"))
    }

    @Test
    fun `bot detection signatures are detected`() {
        assertTrue(BotDetectionMitigator.isBotDetectionError("Sign in to confirm you're not a bot"))
        assertTrue(BotDetectionMitigator.isBotDetectionError("Error 2000"))
    }

    @Test
    fun `notifyPlaybackFailure returns false for logged-in users`() {
        // Signed-in playback uses a separate retry path without guest-session rotation.
        assertFalse(BotDetectionMitigator.notifyPlaybackFailure(isLoggedIn = true, errorMessage = "Bad stream player response"))
    }

    @Test
    fun `notifyPlaybackFailure returns false for a geo-restricted guest failure`() {
        assertFalse(BotDetectionMitigator.notifyPlaybackFailure(isLoggedIn = false, errorMessage = "geo-restricted"))
    }

    @Test
    fun `notifyPlaybackFailure returns true for a guest bot-detection failure`() {
        assertTrue(BotDetectionMitigator.notifyPlaybackFailure(isLoggedIn = false, errorMessage = "Sign in to confirm you're not a bot"))
        assertFalse(BotDetectionMitigator.notifyPlaybackFailure(isLoggedIn = false, errorMessage = "Missing stream expire time"))
    }
}
