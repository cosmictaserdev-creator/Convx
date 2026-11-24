package com.convx.music.playback.presentation

import com.convx.music.lyrics.LyricsQuery
import com.convx.music.lyrics.SharedLyricsSource
import com.convx.music.lyrics.bitchord.LyricLine
import com.convx.music.lyrics.bitchord.LyricWord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LyricsStateHolderTest {
    private val query = LyricsQuery("track", "Song", "Artist", 180_000L)

    @Test fun automaticLookupPrefersRealWordsToLineOrPlainResults() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val plain = source("plain") { listOf(LyricLine(0L, "Plain")) }
        val timed = source("words") { listOf(LyricLine(500L, "Sing", listOf(LyricWord(500L, 1200L, "Sing")))) }
        val holder = LyricsStateHolder(listOf(plain, timed), dispatcher, dispatcher)
        try {
            holder.load(query)
            advanceUntilIdle()
            assertEquals("words", holder.state.value.document?.sourceId)
            assertTrue(requireNotNull(holder.state.value.document).hasWordTimings)
            assertFalse(holder.state.value.isLoading)
        } finally { holder.close() }
    }

    @Test fun selectingOneProviderDoesNotContactTheOtherProviders() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        var unwantedCalls = 0
        val holder = LyricsStateHolder(listOf(source("unwanted") { unwantedCalls++; null }, source("selected") { listOf(LyricLine(1L, "Selected")) }), dispatcher, dispatcher)
        try {
            holder.load(query, "selected")
            advanceUntilIdle()
            assertEquals(0, unwantedCalls)
            assertEquals("selected", holder.state.value.document?.sourceId)
        } finally { holder.close() }
    }

    @Test fun changingSongsCancelsThePreviousLookupAndCannotPublishStaleLyrics() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = LyricsStateHolder(listOf(source("source") {
            if (it.id == "track") delay(10_000L)
            listOf(LyricLine(1L, it.title))
        }), dispatcher, dispatcher)
        try {
            holder.load(query)
            runCurrent()
            holder.load(query.copy(id = "next", title = "Next"))
            advanceUntilIdle()
            assertEquals("Next", holder.state.value.document?.lines?.single()?.text)
            assertEquals("next", holder.state.value.query?.id)
        } finally { holder.close() }
    }

    @Test fun returningToACachedSongDoesNotRepeatNetworkWorkAndRefreshDoes() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        var calls = 0
        val holder = LyricsStateHolder(listOf(source("source") { calls++; listOf(LyricLine(1L, it.title)) }), dispatcher, dispatcher)
        try {
            holder.load(query); advanceUntilIdle()
            holder.load(query); advanceUntilIdle()
            assertEquals(1, calls)
            holder.retry(); advanceUntilIdle()
            assertEquals(2, calls)
        } finally { holder.close() }
    }

    @Test fun instrumentalMarkersSurviveLookup() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val holder = LyricsStateHolder(listOf(source("source") { listOf(LyricLine(1L, "Singing"), LyricLine(5000L, "")) }), dispatcher, dispatcher)
        try {
            holder.load(query); advanceUntilIdle()
            assertEquals(2, holder.state.value.document?.lines?.size)
        } finally { holder.close() }
    }

    private fun source(id: String, lookup: suspend (LyricsQuery) -> List<LyricLine>?) = object : SharedLyricsSource {
        override val id = id
        override val label = id
        override val wordSynced = true
        override suspend fun lyrics(query: LyricsQuery) = lookup(query)
    }
}
