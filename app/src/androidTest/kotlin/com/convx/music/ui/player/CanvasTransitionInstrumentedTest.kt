package com.convx.music.ui.player

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.convx.music.MainActivity
import com.convx.music.artistvideo.VideoFrameTextureView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CanvasTransitionInstrumentedTest {
    @Test fun outgoingVideoKeepsPlayingUntilIncomingVideoHasAFrame() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val video = context.cacheDir.resolve("canvas-transition-test.mp4")
        instrumentation.context.assets.open("canvas-test.mp4").use { input -> video.outputStream().use { input.copyTo(it) } }
        val activity = instrumentation.startActivitySync(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        val scope = CoroutineScope(SupervisorJob() + AndroidUiDispatcher.Main)
        lateinit var controller: CanvasVideoController
        lateinit var first: VideoFrameTextureView
        lateinit var second: VideoFrameTextureView
        lateinit var handoffFirst: VideoFrameTextureView
        lateinit var handoffSecond: VideoFrameTextureView
        lateinit var oldPlayer: ExoPlayer
        var sawIncomingWhileOldPlayed = false
        try {
            instrumentation.runOnMainSync {
                fun player() = ExoPlayer.Builder(context).build().apply { volume = 0f }
                fun standby() = player().apply {
                    addListener(object : androidx.media3.common.Player.Listener {
                        override fun onRenderedFirstFrame() {
                            if (controller.exoPlayer === oldPlayer && oldPlayer.isPlaying) sawIncomingWhileOldPlayed = true
                        }
                    })
                }
                controller = CanvasVideoController(player(), scope, ::standby)
                first = VideoFrameTextureView(activity)
                second = VideoFrameTextureView(activity).apply { alpha = 0f }
                handoffFirst = VideoFrameTextureView(activity)
                handoffSecond = VideoFrameTextureView(activity).apply { alpha = 0f }
                val root = FrameLayout(activity).apply {
                    addView(first, FrameLayout.LayoutParams(300, 300))
                    addView(second, FrameLayout.LayoutParams(300, 300))
                }
                val handoff = FrameLayout(activity).apply {
                    addView(handoffFirst, FrameLayout.LayoutParams(300, 300))
                    addView(handoffSecond, FrameLayout.LayoutParams(300, 300))
                }
                activity.addContentView(root, ViewGroup.LayoutParams(300, 300))
                activity.addContentView(handoff, ViewGroup.LayoutParams(300, 300))
                first.onFrame = { controller.onSurfaceFrame(first) }
                second.onFrame = {
                    if (controller.exoPlayer === oldPlayer && oldPlayer.isPlaying) sawIncomingWhileOldPlayed = true
                    controller.onSurfaceFrame(second)
                }
                handoffFirst.onFrame = { controller.onSurfaceFrame(handoffFirst) }
                handoffSecond.onFrame = { controller.onSurfaceFrame(handoffSecond) }
                controller.attach(Any(), first, alternate = second)
                controller.setPlaybackRequested(true)
                scope.launch { controller.load("one", Uri.fromFile(video).toString(), null, 300) }
            }
            waitUntil(instrumentation) { controller.isShowing("one") && controller.exoPlayer.isPlaying }
            instrumentation.runOnMainSync {
                oldPlayer = controller.exoPlayer
                scope.launch { controller.load("two", Uri.fromFile(video).toString(), null, 1_500) }
                assertSame(oldPlayer, controller.exoPlayer)
                assertTrue(oldPlayer.isPlaying)
            }
            waitUntil(instrumentation) { sawIncomingWhileOldPlayed }
            instrumentation.runOnMainSync {
                assertSame(oldPlayer, controller.exoPlayer)
                org.junit.Assert.assertEquals(1f, first.alpha, 0.001f)
                controller.attach(Any(), handoffFirst, priority = 2, alternate = handoffSecond)
                org.junit.Assert.assertEquals(1f, handoffFirst.alpha, 0.001f)
            }
            waitUntil(instrumentation) { controller.isShowing("two") && controller.exoPlayer !== oldPlayer }
            instrumentation.runOnMainSync {
                assertTrue(sawIncomingWhileOldPlayed)
                assertNotSame(oldPlayer, controller.exoPlayer)
                assertTrue(controller.exoPlayer.isPlaying)
            }
        } finally {
            instrumentation.runOnMainSync { runCatching { controller.release() }; activity.finish() }
            scope.cancel()
            video.delete()
        }
    }

    private fun waitUntil(instrumentation: android.app.Instrumentation, condition: () -> Boolean) {
        val deadline = android.os.SystemClock.uptimeMillis() + 12_000
        while (android.os.SystemClock.uptimeMillis() < deadline) {
            var ready = false
            instrumentation.runOnMainSync { ready = condition() }
            if (ready) return
            Thread.sleep(30)
        }
        throw AssertionError("Timed out waiting for video transition")
    }
}
