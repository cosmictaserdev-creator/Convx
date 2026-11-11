package com.convx.music.playback.data

import com.convx.music.playback.HistoryPlay
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonFilePlayHistoryTest {
    private val directory = createTempDirectory("play-history").toFile()

    @AfterTest
    fun cleanUp() {
        directory.deleteRecursively()
    }

    private fun play(i: Int) = HistoryPlay("s$i", "youtube-music", "T$i", listOf("A"), null, null, i.toLong())

    @Test
    fun recordedPlaysSurviveANewInstanceOnTheSameFile() = runTest {
        val file = File(directory, "history.json")
        JsonFilePlayHistory(file).apply { record(play(1)); record(play(2)) }
        assertEquals(listOf("s1", "s2"), JsonFilePlayHistory(file).read().plays.map { it.songId })
    }

    @Test
    fun aMissingOrCorruptFileStartsEmptyAndRecordingRepairsIt() = runTest {
        val file = File(directory, "history.json").apply { writeText("{oops") }
        val history = JsonFilePlayHistory(file)
        assertTrue(history.read().plays.isEmpty())
        history.record(play(1))
        assertEquals(1, JsonFilePlayHistory(file).read().plays.size)
    }

    @Test
    fun concurrentRecordingsAreAllKept() = runTest {
        val history = JsonFilePlayHistory(File(directory, "history.json"))
        (1..20).map { i -> async { history.record(play(i)) } }.forEach { it.await() }
        assertEquals(20, history.read().plays.size)
    }
}
