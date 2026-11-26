package com.music.innertube

import com.music.innertube.mappers.AlbumTrackAlignment
import com.music.innertube.models.SongItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class AlbumTrackAlignmentTest {
    private fun song(id: String, title: String, duration: Int? = 200) =
        SongItem(id, title, emptyList(), duration = duration, musicVideoType = "MUSIC_VIDEO_TYPE_ATV", thumbnail = "")

    private fun video(id: String, title: String, duration: Int? = 200) =
        SongItem(id, title, emptyList(), duration = duration, musicVideoType = "MUSIC_VIDEO_TYPE_OMV", thumbnail = "")

    private fun ids(tracks: List<SongItem>) = tracks.map { it.id }

    @Test
    fun aListedVideoIsReplacedByTheSongWithTheSameTitle() {
        val listed = listOf(song("s1", "One"), video("v2", "Two"), song("s3", "Three"))
        val own = listOf(song("s1", "One"), song("s2", "Two"), song("s3", "Three"))
        assertEquals(listOf("s1", "s2", "s3"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun nothingChangesWhenNoTrackIsAVideoOrThePlaylistIsEmpty() {
        val songs = listOf(song("s1", "One"), song("s2", "Two"))
        assertSame(songs, AlbumTrackAlignment.withSongsForVideos(songs, listOf(song("x", "One"))))
        val withVideo = listOf(video("v1", "One"))
        assertSame(withVideo, AlbumTrackAlignment.withSongsForVideos(withVideo, emptyList()))
    }

    @Test
    fun titlesMatchIgnoringCasePunctuationAndSpacing() {
        val listed = listOf(video("v1", "Death & Romance!"))
        val own = listOf(song("s1", "death   &   romance"))
        assertEquals(listOf("s1"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun aBracketedTagOnOneSideStillMatches() {
        val listed = listOf(video("v1", "Espresso (Official Video)"))
        val own = listOf(song("s1", "Espresso"))
        assertEquals(listOf("s1"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun differentTrackCountsStillAlignByTitle() {
        val listed = listOf(video("v1", "One"), video("v2", "Two"), video("v3", "Three"))
        val own = listOf(song("s1", "One"), song("bonus", "Bonus"), song("s2", "Two"), song("s3", "Three"), song("s4", "Four"))
        assertEquals(listOf("s1", "s2", "s3"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun aVideoWithNoMatchingSongStaysAsListed() {
        val listed = listOf(video("v1", "Live Only"), video("v2", "Two"))
        val own = listOf(song("s2", "Two"))
        assertEquals(listOf("v1", "s2"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun repeatedTitlesMatchInOrderAndNeverReuseASong() {
        val listed = listOf(video("v1", "Intro"), video("v2", "Intro"), video("v3", "Outro"))
        val own = listOf(song("a", "Intro"), song("b", "Intro"), song("c", "Outro"))
        assertEquals(listOf("a", "b", "c"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun aVideoMuchLongerThanTheSongIsAnotherRecording() {
        val listed = listOf(video("v1", "Two", duration = 500))
        val own = listOf(song("s2", "Two", duration = 200))
        assertEquals(listOf("v1"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun aShortIntroOnTheVideoStillMatches() {
        val listed = listOf(video("v1", "Two", duration = 260))
        val own = listOf(song("s2", "Two", duration = 200))
        assertEquals(listOf("s2"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun anUnknownDurationDoesNotBlockAMatch() {
        val listed = listOf(video("v1", "Two", duration = null))
        val own = listOf(song("s2", "Two"))
        assertEquals(listOf("s2"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun emptyTitlesNeverMatchEachOther() {
        val listed = listOf(video("v1", ""))
        val own = listOf(song("s1", ""))
        assertEquals(listOf("v1"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun punctuationOnlyTitlesMatchOnTheirRawText() {
        val listed = listOf(video("v1", "!!!"), video("v2", "???"))
        val own = listOf(song("s1", "!!!"), song("s2", "???"))
        assertEquals(listOf("s1", "s2"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun aPlaylistOfOnlyVideosReplacesNothing() {
        val listed = listOf(video("v1", "One"))
        val own = listOf(video("v9", "One"))
        assertEquals(listOf("v1"), ids(AlbumTrackAlignment.withSongsForVideos(listed, own)))
    }

    @Test
    fun theResultKeepsEveryListedPosition() {
        val listed = listOf(song("s1", "One"), video("v2", "Two"), video("v3", "Gone"), song("s4", "Four"))
        val own = listOf(song("s1", "One"), song("s2", "Two"), song("s4", "Four"))
        val result = AlbumTrackAlignment.withSongsForVideos(listed, own)
        assertEquals(4, result.size)
        assertEquals(listOf("s1", "s2", "v3", "s4"), ids(result))
    }
}
