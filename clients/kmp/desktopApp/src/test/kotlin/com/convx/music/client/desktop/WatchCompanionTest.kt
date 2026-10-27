package com.convx.music.client.desktop

import com.convx.music.catalog.*
import com.convx.music.client.watch.WatchCompanion
import com.convx.music.playback.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Test
import java.lang.reflect.Proxy
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WatchCompanionTest {
    private fun catalog(): MusicCatalogRepository = Proxy.newProxyInstance(
        MusicCatalogRepository::class.java.classLoader, arrayOf(MusicCatalogRepository::class.java),
    ) { _, method, parameters ->
        if (method.name == "library") CatalogResult.Success(CatalogLibraryPage(parameters!![0] as CatalogLibraryCollection,
            listOf(CatalogEntry.Playlist(CatalogId("provider", "playlist"), "My playlist", emptyList(), null, 2, false))))
        else CatalogResult.Failure(CatalogError.Unavailable)
    } as MusicCatalogRepository

    @Test fun watchActionsUseThePhonePlaybackOwnerAndClampSeeking() = runTest {
        val player = WatchPlayer()
        val companion = WatchCompanion(player, catalog(), FixedPlaybackSettings(PlaybackSettings()), StandardTestDispatcher(testScheduler))
        try {
            companion.receive("""{"action":"PLAY_PAUSE"}""").join()
            assertEquals(1, player.toggles)
            companion.receive("""{"action":"SEEK","positionMs":999999}""").join()
            assertEquals(10_000L, player.seek)
            companion.receive("""{"version":2,"action":"PLAY_PAUSE"}""").join()
            assertEquals(1, player.toggles)
            assertNotNull(companion.state.value.message)
        } finally { companion.close() }
    }

    @Test fun staleQueueIndexSelectsTheRequestedTrackAndDuplicatesRemainAddressable() = runTest {
        val player = WatchPlayer()
        val a = PlaybackQueueEntry("a", "A", emptyList(), providerId = "provider")
        val b = PlaybackQueueEntry("b", "B", emptyList(), providerId = "provider")
        player.queueSnapshot.value = PlaybackQueueSnapshot(listOf(b, a), 0)
        val companion = WatchCompanion(player, catalog(), FixedPlaybackSettings(PlaybackSettings()), StandardTestDispatcher(testScheduler))
        try {
            companion.receive("""{"action":"PLAY","entry":{"providerId":"provider","id":"b","title":"B","queueIndex":1}}""").join()
            assertEquals(0, player.selected)
            player.queueSnapshot.value = PlaybackQueueSnapshot(listOf(a, a), 0)
            companion.receive("""{"action":"PLAY","entry":{"providerId":"provider","id":"a","title":"A","queueIndex":1}}""").join()
            assertEquals(1, player.selected)
        } finally { companion.close() }
    }

    @Test fun syncRepublishesAnUnchangedSnapshotAndBrowseUsesTheSharedCatalog() = runTest {
        val player = WatchPlayer()
        val companion = WatchCompanion(player, catalog(), FixedPlaybackSettings(PlaybackSettings()), StandardTestDispatcher(testScheduler))
        val frames = mutableListOf<String>()
        val observation = companion.observeSnapshots(frames::add)
        try {
            advanceUntilIdle()
            val before = frames.size
            companion.receive("""{"action":"SYNC"}""").join()
            advanceUntilIdle()
            assertTrue(frames.size > before)
            companion.receive("""{"action":"BROWSE","collection":"PLAYLISTS"}""").join()
            advanceUntilIdle()
            assertEquals("My playlist", companion.state.value.library.single().title)
            assertFalse(companion.state.value.isLoading)
        } finally { observation.close(); companion.close() }
    }

    @Test fun bitPerfectPlaybackKeepsItsFixedVolumeWhenAWatchSendsVolume() = runTest {
        val settings = FixedPlaybackSettings(PlaybackSettings(bitPerfectEnabled = true, volume = .8f))
        val companion = WatchCompanion(WatchPlayer(), catalog(), settings, StandardTestDispatcher(testScheduler))
        try {
            advanceUntilIdle()
            assertFalse(companion.state.value.canAdjustVolume)
            companion.receive("""{"action":"VOLUME","volume":0.2}""").join()
            assertEquals(.8f, settings.getSettings().volume)
        } finally { companion.close() }
    }
}

private class WatchPlayer : PlaybackController {
    override val playbackSnapshot = MutableStateFlow(PlaybackSnapshot.EMPTY.copy(durationMs = 10_000))
    override val queueSnapshot = MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
    var toggles = 0
    var seek: Long? = null
    var selected: Int? = null
    override fun togglePlayPause() { toggles++ }
    override fun seekTo(positionMs: Long) { seek = positionMs }
    override fun playQueueItem(index: Int) { selected = index }
    override fun play() = Unit
    override fun pause() = Unit
    override fun requestSkip(forward: Boolean) = Unit
    override fun setRepeatMode(mode: PlaybackRepeatMode) = Unit
    override fun setShuffleEnabled(enabled: Boolean) = Unit
    override fun moveQueueItem(fromIndex: Int, toIndex: Int) = Unit
    override fun removeQueueItem(index: Int) = Unit
    override fun removeQueueItemsAfterCurrent() = Unit
}
