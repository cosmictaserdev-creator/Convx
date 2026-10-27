package com.convx.music.playback

import com.convx.storage.domain.NetworkMediaUri
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkPlaybackRoutingTest {
    @Test fun smbTrackReachesTheRemoteDataSourceWithoutYouTubeResolution() {
        val mediaId = NetworkMediaUri.create("smb-nas", "Artist/Album/Song.flac")
        assertTrue("SMB playback must bypass the outer YouTube resolver", isDirectMediaSource(mediaId))
    }

    @Test fun webDavTrackReachesTheRemoteDataSourceWithoutYouTubeResolution() {
        val mediaId = NetworkMediaUri.create("webdav-cloud", "Artist/Album/Song.mp3")
        assertTrue("WebDAV playback must bypass the outer YouTube resolver", isDirectMediaSource(mediaId))
    }

    @Test fun ordinaryVideoIdsStillUseTheResolverAndDeviceFilesBypassIt() {
        assertFalse(isDirectMediaSource("youtubeVideo"))
        assertTrue(isDirectMediaSource("content://media/external/audio/media/1"))
        assertTrue(isDirectMediaSource("file:///storage/emulated/0/Music/song.mp3"))
    }
}
