package com.convxy.music.playback.dj

import com.convxy.music.playback.audio.DjMixTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TransitionSelectorTest {

    @Test
    fun `creative transitions off means nothing but a transparent mix, ever`() {
        val style = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = analysis(confidence = 0.9f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.9f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = false,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `a well-matched pair is left alone even with creative transitions on`() {
        val style = TransitionSelector.select(
            tier = DjMixTier.FULL_DJ,
            outgoing = analysis(confidence = 0.95f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.95f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `a mismatched pair ending at full energy stays transparent below full dj`() {
        // SMART_CROSSFADE carries no EQ/effects camouflage (DjMixPlanner's tier
        // contract). Echo used to be scored here too and won on every
        // mismatched pair — heard as the outgoing track stuttering rather than
        // a deliberate effect — which is why it is now a FULL_DJ-only move.
        val style = TransitionSelector.select(
            tier = DjMixTier.SMART_CROSSFADE,
            outgoing = analysis(confidence = 0.9f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.9f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `a track that fades out is not looped at smart crossfade`() {
        // Looping a fade-out just repeats the fade — and with echo restricted
        // to FULL_DJ, a fade-out below that tier mixes transparently.
        val style = TransitionSelector.select(
            tier = DjMixTier.SMART_CROSSFADE,
            outgoing = analysis(confidence = 0.9f, profile = fadesOut()),
            incoming = analysis(confidence = 0.9f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `hopeless tempos with a strong incoming intro get the brake`() {
        val style = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = analysis(confidence = 0.2f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.2f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TAPE_STOP, style)
    }

    @Test
    fun `the brake is withheld when the incoming track opens quietly`() {
        // Braking into an ambient intro is a brake into silence.
        val style = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = analysis(confidence = 0.2f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.2f, profile = quietIntro()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertNotEquals(TransitionStyle.TAPE_STOP, style)
    }

    @Test
    fun `the same move does not land twice in a row`() {
        // Uses TAPE_STOP, the effect that still fires at PLAIN_CROSSFADE on
        // hopeless tempos with a loud intro.
        val args = { recent: List<TransitionStyle> ->
            TransitionSelector.select(
                tier = DjMixTier.PLAIN_CROSSFADE,
                outgoing = analysis(confidence = 0.2f, profile = loudThroughout()),
                incoming = analysis(confidence = 0.2f, profile = loudThroughout()),
                outgoingDurationMs = DURATION_MS,
                creativeEnabled = true,
                recentStyles = recent,
            )
        }

        val first = args(emptyList())
        val second = args(listOf(first))

        assertEquals(TransitionStyle.TAPE_STOP, first)
        assertNotEquals(first, second)
    }

    @Test
    fun `variety never overrides leaving a matched pair alone`() {
        // TRANSPARENT is exempt from the repeat penalty: "do nothing clever"
        // getting repetitive is not a problem.
        val style = TransitionSelector.select(
            tier = DjMixTier.FULL_DJ,
            outgoing = analysis(confidence = 0.95f, profile = loudThroughout()),
            incoming = analysis(confidence = 0.95f, profile = loudThroughout()),
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
            recentStyles = listOf(TransitionStyle.TRANSPARENT, TransitionStyle.TRANSPARENT),
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `an effect stays locked out until several plain transitions have passed`() {
        val loud = analysis(confidence = 0.2f, profile = loudThroughout())
        fun select(recent: List<TransitionStyle>) = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = loud,
            incoming = loud,
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
            recentStyles = recent,
        )

        // An effect one transition ago locks the next few out entirely — an
        // effect on every song is a plugin, not a DJ.
        val justUsed = listOf(TransitionStyle.TAPE_STOP)
        assertEquals(TransitionStyle.TRANSPARENT, select(justUsed))
        assertEquals(
            TransitionStyle.TRANSPARENT,
            select(listOf(TransitionStyle.TRANSPARENT, TransitionStyle.TAPE_STOP)),
        )

        // Once enough transparent transitions have gone by, effects return.
        val cooledDown = List(4) { TransitionStyle.TRANSPARENT } + TransitionStyle.TAPE_STOP
        assertEquals(TransitionStyle.TAPE_STOP, select(cooledDown))
    }

    @Test
    fun `nothing measured means no effect, however eligible it looks`() {
        // Regression: with no analysis every score was computed from UNKNOWN
        // placeholders and ECHO_FREEZE won on guesswork, cutting the outgoing
        // track into a delay line on the strength of nothing at all.
        val blank = TrackAnalysis(
            grid = BeatGrid(bpm = 0f, anchorBeatMs = 0f, confidence = 0f),
            energyProfile = ByteArray(0),
        )

        val style = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = blank,
            incoming = blank,
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    @Test
    fun `a null outgoing analysis means no effect`() {
        val style = TransitionSelector.select(
            tier = DjMixTier.PLAIN_CROSSFADE,
            outgoing = null,
            incoming = null,
            outgoingDurationMs = DURATION_MS,
            creativeEnabled = true,
        )

        assertEquals(TransitionStyle.TRANSPARENT, style)
    }

    // --- fixtures -----------------------------------------------------------

    private fun analysis(confidence: Float, profile: ByteArray) = TrackAnalysis(
        grid = BeatGrid(bpm = 128f, anchorBeatMs = 0f, confidence = confidence),
        energyProfile = profile,
    )

    private fun loudThroughout() = ByteArray(SECONDS) { 220.toByte() }

    private fun fadesOut() = ByteArray(SECONDS) { second ->
        if (second < SECONDS - 25) 220.toByte() else (220 - (second - (SECONDS - 25)) * 8).coerceAtLeast(0).toByte()
    }

    private fun quietIntro() = ByteArray(SECONDS) { second -> (if (second < 25) 30 else 220).toByte() }

    private companion object {
        const val SECONDS = 180
        const val DURATION_MS = SECONDS * 1000L
    }
}
