package com.convx.music.ui.player

import android.view.TextureView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CanvasSurfaceHandoffTest {
    @Test
    fun changingSurfaceKeepsTheSamePlayerAndPosition() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val context = ApplicationProvider.getApplicationContext<android.content.Context>()
            val player = ExoPlayer.Builder(context).build()
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            val controller = CanvasVideoController(player, scope)
            try {
                val item = MediaItem.fromUri("https://example.test/canvas.mp4")
                player.setMediaItem(item)
                player.seekTo(1_234)
                val mini = TextureView(context)
                val full = TextureView(context)
                val miniToken = Any()
                val fullToken = Any()
                val overlayToken = Any()
                val overlay = TextureView(context)

                controller.attach(miniToken, mini)
                controller.onSurfaceFrame(mini)
                assertSame(mini, controller.surfaceWithFrame)

                controller.attach(fullToken, full)
                assertNull(controller.surfaceWithFrame)
                controller.onSurfaceFrame(full)
                assertSame(full, controller.surfaceWithFrame)
                controller.attach(overlayToken, overlay, priority = 2)
                controller.onSurfaceFrame(overlay)
                assertSame(overlay, controller.surfaceWithFrame)
                controller.attach(fullToken, full)
                controller.onSurfaceFrame(full)
                assertSame(overlay, controller.surfaceWithFrame)
                controller.detach(overlayToken)
                controller.onSurfaceFrame(full)
                assertSame(full, controller.surfaceWithFrame)
                assertSame(player, controller.exoPlayer)
                assertEquals(item, player.currentMediaItem)
                assertEquals(1_234L, player.currentPosition)

                controller.detach(fullToken)
                controller.onSurfaceFrame(mini)
                assertSame(mini, controller.surfaceWithFrame)
                assertEquals(1_234L, player.currentPosition)
                controller.release()
                controller.detach(miniToken)
            } finally {
                controller.release()
                scope.cancel()
            }
        }
    }
}
