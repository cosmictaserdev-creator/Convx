package com.convx.music.playback

import com.convx.music.playback.smart.TransitionStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlaybackTransitionPlannerTest {
    private val outgoing = PlaybackRequestTrack(
        providerId = "youtube_music",
        id = "outgoing",
        title = "Outgoing",
        artists = listOf("Artist"),
        durationMs = 180_000L,
    )
    private val incoming = PlaybackRequestTrack(
        providerId = "youtube_music",
        id = "incoming",
        title = "Incoming",
        artists = listOf("Artist"),
        durationMs = 190_000L,
    )

    @Test
    fun disabledTransitionsDoNotCreateAWindow() {
        assertNull(
            planPlaybackTransitionWindow(
                settings = PlaybackSettings(),
                outgoing = outgoing,
                incoming = incoming,
                currentPositionMs = 0L,
                outgoingDurationMs = 180_000L,
            ),
        )
    }

    @Test
    fun standardCrossfadeUsesConfiguredDuration() {
        val window = planPlaybackTransitionWindow(
            settings = PlaybackSettings(crossfadeEnabled = true, crossfadeDurationMs = 5_000L),
            outgoing = outgoing,
            incoming = incoming,
            currentPositionMs = 0L,
            outgoingDurationMs = 180_000L,
        )

        assertEquals(175_000L, window?.startAtMs)
        assertEquals(180_000L, window?.endAtMs)
        assertEquals(5_000L, window?.durationMs)
        assertEquals(0L, window?.incomingCuePositionMs)
        assertEquals(1.0, window?.incomingPlaybackRate)
        assertEquals(TransitionStyle.EQUAL_POWER, window?.style)
    }

    @Test
    fun autoMixFallsBackToTheConfiguredFadeWithoutTrackAnalysis() {
        val window = planPlaybackTransitionWindow(
            settings = PlaybackSettings(
                crossfadeEnabled = true,
                autoMixEnabled = true,
                crossfadeDurationMs = 7_000L,
            ),
            outgoing = outgoing,
            incoming = incoming,
            currentPositionMs = 0L,
            outgoingDurationMs = 180_000L,
        )

        assertEquals(173_000L, window?.startAtMs)
        assertEquals(180_000L, window?.endAtMs)
        assertEquals(7_000L, window?.durationMs)
        assertEquals(0L, window?.incomingCuePositionMs)
        assertEquals(1.0, window?.incomingPlaybackRate)
        assertEquals(TransitionStyle.EQUAL_POWER, window?.style)
    }

    @Test
    fun autoMixDoesNotPlanTracksRejectedByItsShortTrackPolicy() {
        val shortTrack = outgoing.copy(durationMs = 30_000L)

        assertNull(
            planPlaybackTransitionWindow(
                settings = PlaybackSettings(crossfadeEnabled = true, autoMixEnabled = true),
                outgoing = shortTrack,
                incoming = incoming,
                currentPositionMs = 0L,
                outgoingDurationMs = 30_000L,
            ),
        )
    }
}
