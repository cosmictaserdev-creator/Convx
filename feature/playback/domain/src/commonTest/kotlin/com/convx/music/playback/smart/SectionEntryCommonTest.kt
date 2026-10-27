package com.convx.music.playback.smart

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SectionEntryCommonTest {
    private fun analysis(energy: (Int) -> Double) = TrackAnalysis(
        status = TrackAnalysis.STATUS_READY,
        duration = 200.0,
        bpm = 128.0,
        phraseBoundaries = (1..11).map { it * 16.0 },
        energyCurve = (0..199).map { EnergySample(it.toDouble(), energy(it)) },
    )

    @Test
    fun entersWhereEnergyStepsUpInsteadOfAtTheIntro() {
        val track = analysis { if (it < 64) 0.2 else 1.0 }

        assertEquals(64.0, sectionEntryCandidate(track)?.time)
        assertEquals(64.0, track.withSectionEntry().mixInTime)
    }

    @Test
    fun flatTrackKeepsItsNativeIntroCandidates() {
        val track = analysis { 0.8 }

        assertNull(sectionEntryCandidate(track))
        assertEquals(track, track.withSectionEntry())
    }
}
