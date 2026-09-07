package com.convxy.music.playback.audio

import com.convxy.music.playback.dj.BeatGrid
import org.junit.Assert.assertEquals
import org.junit.Test

class DjMixPlannerTest {

    @Test
    fun `a missing grid on either side falls back to a plain crossfade`() {
        assertEquals(DjMixTier.PLAIN_CROSSFADE, DjMixPlanner.plan(grid(128f), null).tier)
        assertEquals(DjMixTier.PLAIN_CROSSFADE, DjMixPlanner.plan(null, grid(128f)).tier)
        assertEquals(DjMixTier.PLAIN_CROSSFADE, DjMixPlanner.plan(grid(0f), grid(128f)).tier)
    }

    @Test
    fun `unconfident material falls back to a plain crossfade however close the tempos are`() {
        assertEquals(
            DjMixTier.PLAIN_CROSSFADE,
            DjMixPlanner.plan(grid(128f, confidence = 0.9f), grid(128f, confidence = 0.05f)).tier,
        )
    }

    @Test
    fun `confident and close gets the full treatment with a matching speed`() {
        val plan = DjMixPlanner.plan(grid(128f), grid(126f))

        assertEquals(DjMixTier.FULL_DJ, plan.tier)
        assertEquals(128f / 126f, plan.incomingSpeedAdjustment, 0.001f)
    }

    @Test
    fun `a half-time reading mixes at normal speed rather than being stretched to double`() {
        // A 128 BPM track whose kick sits on 1 and 3 is detected at 64. Its
        // audio is still 128, so the correct speed is 1.0 — anything else means
        // the octave ambiguity alone would ruin the mix.
        val plan = DjMixPlanner.plan(grid(128f), grid(64f))

        assertEquals(DjMixTier.FULL_DJ, plan.tier)
        assertEquals(1f, plan.incomingSpeedAdjustment, 0.001f)
    }

    @Test
    fun `middling confidence with close tempos gets the tempo nudge only`() {
        assertEquals(
            DjMixTier.SMART_CROSSFADE,
            DjMixPlanner.plan(grid(128f, confidence = 0.30f), grid(126f, confidence = 0.30f)).tier,
        )
    }

    @Test
    fun `tempos too far apart to stretch fall back to a plain crossfade`() {
        assertEquals(DjMixTier.PLAIN_CROSSFADE, DjMixPlanner.plan(grid(128f), grid(100f)).tier)
    }

    private fun grid(bpm: Float, confidence: Float = 0.9f) = BeatGrid(bpm, 0f, confidence)
}
