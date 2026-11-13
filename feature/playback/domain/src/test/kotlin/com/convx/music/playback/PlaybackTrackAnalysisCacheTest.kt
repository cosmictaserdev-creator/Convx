package com.convx.music.playback

import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlaybackTrackAnalysisCacheTest {
    @Test
    fun preloadLoadsOffTheCallerAndCachesTheResult() = runTest {
        val expected = TrackAnalysis(
            status = TrackAnalysis.STATUS_READY,
            trackId = "youtube_music:track",
            bpm = 120.0,
        )
        var reads = 0
        val repository = object : PlaybackTrackAnalysisRepository {
            override suspend fun get(trackIdentity: String): TrackAnalysis? {
                reads += 1
                return expected.takeIf { it.trackId == trackIdentity }
            }

            override suspend fun save(trackIdentity: String, analysis: TrackAnalysis) = Unit

            override suspend fun remove(trackIdentity: String) = Unit
        }
        val cache = PlaybackTrackAnalysisCache(repository, this)

        cache.preload(expected.trackId)
        assertNull(cache.analysisFor(expected.trackId))
        runCurrent()
        cache.preload(expected.trackId)

        assertEquals(expected, cache.analysisFor(expected.trackId))
        assertEquals(1, reads)
    }
}
