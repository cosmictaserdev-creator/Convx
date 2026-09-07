package com.convxy.music.listentogether

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomPermissionsTest {

    private fun track(suggestedBy: String?) = TrackInfo(
        id = "t1",
        title = "Track",
        artist = "Artist",
        duration = 180_000,
        suggestedBy = suggestedBy,
    )

    // Playback control

    @Test
    fun `host controls playback in owner mode`() {
        assertTrue(RoomPermissions.canControlPlayback(isHost = true, controlMode = ControlModes.OWNER))
    }

    @Test
    fun `guest cannot control playback in owner mode`() {
        assertFalse(RoomPermissions.canControlPlayback(isHost = false, controlMode = ControlModes.OWNER))
    }

    @Test
    fun `guest controls playback in everyone mode`() {
        assertTrue(RoomPermissions.canControlPlayback(isHost = false, controlMode = ControlModes.EVERYONE))
    }

    // Skipping

    @Test
    fun `host may skip a track someone else queued`() {
        assertTrue(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = "guest-1"),
                userId = "host-1",
                isHost = true,
                controlMode = ControlModes.EVERYONE,
            )
        )
    }

    @Test
    fun `guest may skip their own track in everyone mode`() {
        assertTrue(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = "guest-1"),
                userId = "guest-1",
                isHost = false,
                controlMode = ControlModes.EVERYONE,
            )
        )
    }

    @Test
    fun `guest may not skip someone elses track`() {
        assertFalse(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = "guest-2"),
                userId = "guest-1",
                isHost = false,
                controlMode = ControlModes.EVERYONE,
            )
        )
    }

    @Test
    fun `guest may not skip their own track in owner mode`() {
        // Owner-only mode outranks track ownership.
        assertFalse(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = "guest-1"),
                userId = "guest-1",
                isHost = false,
                controlMode = ControlModes.OWNER,
            )
        )
    }

    @Test
    fun `a host-queued track has no guest owner`() {
        // suggestedBy == null means it came from the host's queue, not a request.
        assertFalse(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = null),
                userId = "guest-1",
                isHost = false,
                controlMode = ControlModes.EVERYONE,
            )
        )
    }

    @Test
    fun `unknown user cannot skip`() {
        assertFalse(
            RoomPermissions.canSkipTrack(
                track = track(suggestedBy = null),
                userId = null,
                isHost = false,
                controlMode = ControlModes.EVERYONE,
            )
        )
    }

    // Denial reasons

    @Test
    fun `allowed skip has no denial reason`() {
        assertNull(
            RoomPermissions.skipDeniedReason(
                track = track("guest-1"), userId = "guest-1",
                isHost = false, controlMode = ControlModes.EVERYONE,
            )
        )
    }

    @Test
    fun `owner mode denial is reported as owner-only`() {
        assertEquals(
            RoomPermissions.DeniedReason.OWNER_ONLY_MODE,
            RoomPermissions.skipDeniedReason(
                track = track("guest-1"), userId = "guest-1",
                isHost = false, controlMode = ControlModes.OWNER,
            )
        )
    }

    @Test
    fun `everyone mode denial is reported as not-your-track`() {
        assertEquals(
            RoomPermissions.DeniedReason.NOT_YOUR_TRACK,
            RoomPermissions.skipDeniedReason(
                track = track("guest-2"), userId = "guest-1",
                isHost = false, controlMode = ControlModes.EVERYONE,
            )
        )
    }
}
