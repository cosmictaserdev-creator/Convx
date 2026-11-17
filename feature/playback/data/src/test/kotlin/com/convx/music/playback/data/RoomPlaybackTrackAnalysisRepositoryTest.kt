package com.convx.music.playback.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.playback.smart.EnergySample
import com.convx.music.playback.smart.MixCandidate
import com.convx.music.playback.smart.TrackAnalysis
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RoomPlaybackTrackAnalysisRepositoryTest {
    @Test
    fun usableAnalysisRoundTripsEveryTransitionFeature() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackTrackAnalysisRepository(database.playbackTrackAnalysisDao())
            val expected = analysis()

            repository.save(expected.trackId, expected)

            assertEquals(expected, repository.get(expected.trackId))
        } finally {
            database.close()
        }
    }

    @Test
    fun unusableOrMismatchedAnalysisIsNotStored() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackTrackAnalysisRepository(database.playbackTrackAnalysisDao())
            val expected = analysis()

            repository.save("another-track", expected)
            repository.save(expected.trackId, expected.copy(status = "pending"))

            assertNull(repository.get(expected.trackId))
            assertNull(database.playbackTrackAnalysisDao().get("another-track"))
        } finally {
            database.close()
        }
    }

    @Test
    fun corruptAndOutdatedRecordsAreDiscarded() = runTest {
        val database = inMemoryDatabase()
        try {
            val dao = database.playbackTrackAnalysisDao()
            val repository = RoomPlaybackTrackAnalysisRepository(dao)
            dao.upsert(
                PlaybackTrackAnalysisRecord(
                    trackIdentity = "youtube_music:corrupt",
                    storageVersion = 1,
                    analysisJson = "{",
                    storedAtEpochMs = 1L,
                ),
            )
            dao.upsert(
                PlaybackTrackAnalysisRecord(
                    trackIdentity = "youtube_music:old",
                    storageVersion = 0,
                    analysisJson = "{}",
                    storedAtEpochMs = 2L,
                ),
            )

            assertNull(repository.get("youtube_music:corrupt"))
            assertNull(repository.get("youtube_music:old"))
            assertNull(dao.get("youtube_music:corrupt"))
            assertNull(dao.get("youtube_music:old"))
        } finally {
            database.close()
        }
    }

    @Test
    fun removeDeletesStoredAnalysis() = runTest {
        val database = inMemoryDatabase()
        try {
            val repository = RoomPlaybackTrackAnalysisRepository(database.playbackTrackAnalysisDao())
            val expected = analysis()
            repository.save(expected.trackId, expected)

            repository.remove(expected.trackId)

            assertNull(repository.get(expected.trackId))
        } finally {
            database.close()
        }
    }

    private fun analysis() = TrackAnalysis(
        status = TrackAnalysis.STATUS_READY,
        trackId = "youtube_music:track",
        duration = 180.0,
        bpm = 128.0,
        beatInterval = 60.0 / 128.0,
        beatConfidence = 0.92,
        downbeats = listOf(0.4, 2.275, 4.15),
        phraseBoundaries = listOf(15.4, 30.4, 45.4),
        firstBeat = 0.4,
        key = "8A",
        keyConfidence = 0.87,
        audibleStartTime = 0.08,
        pickupTime = 0.4,
        introEndTime = 15.4,
        contentEndTime = 176.2,
        outroStartTime = 165.0,
        mixInTime = 15.4,
        mixOutTime = 165.0,
        mixInCandidates = listOf(MixCandidate(15.4, 0.9, "phrase")),
        mixOutCandidates = listOf(MixCandidate(165.0, 0.88, "outro")),
        energyCurve = listOf(EnergySample(0.0, 0.2), EnergySample(30.0, 0.75)),
        lowEnergyCurve = listOf(EnergySample(0.0, 0.1), EnergySample(30.0, 0.6)),
        vocalActivityMask = listOf(0.0, 0.25),
        vocalProbability = 0.72,
    )

    private fun inMemoryDatabase(): PlaybackSettingsDatabase =
        Room.inMemoryDatabaseBuilder<PlaybackSettingsDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
}
