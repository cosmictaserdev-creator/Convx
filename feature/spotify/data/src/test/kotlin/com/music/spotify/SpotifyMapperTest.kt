package com.music.spotify

import org.junit.Assert.assertEquals
import org.junit.Test

class SpotifyMapperTest {
    @Test
    fun `precomputed matching uses the same weighted score`() {
        val precomputed = SpotifyMapper.precompute("Song", "Artist", durationMs = 200_000)

        val directScore = SpotifyMapper.matchScore(
            spotifyTitle = "Song",
            spotifyArtist = "Artist",
            spotifyDurationMs = 200_000,
            candidateTitle = "Song",
            candidateArtist = "Artist",
            candidateDurationSec = 208,
        )
        val precomputedScore = SpotifyMapper.matchScorePrecomputed(
            precomputed = precomputed,
            candidateTitle = "Song",
            candidateArtist = "Artist",
            candidateDurationSec = 208,
        )

        assertEquals(0.9, directScore, 0.000001)
        assertEquals(directScore, precomputedScore, 0.0)
    }
}
