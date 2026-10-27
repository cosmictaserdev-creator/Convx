package com.convx.music.ui.utils

import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the motion single source of truth: every animation in the app routes
 * through [Motion], so a retune lands everywhere at once instead of drifting
 * per call site.
 */
class MotionTokensTest {

    @Test
    fun `morph enter is 340ms on the ios curve`() {
        val spec = Motion.morphEnter<Float>() as TweenSpec<Float>
        assertEquals(340, spec.durationMillis)
        assertEquals(Motion.MorphEnterEasing, spec.easing)
    }

    @Test
    fun `morph exit is 260ms on the same curve`() {
        val spec = Motion.morphExit<Float>() as TweenSpec<Float>
        assertEquals(260, spec.durationMillis)
        assertEquals(Motion.MorphEnterEasing, spec.easing)
    }

    @Test
    fun `push and appearExit share the nav clock`() {
        val push = Motion.push<Float>() as TweenSpec<Float>
        assertEquals(260, push.durationMillis)
        assertEquals(Motion.PushEasing, push.easing)
        val exit = Motion.appearExit<Float>() as TweenSpec<Float>
        assertEquals(Motion.AppearExitMillis, exit.durationMillis)
    }

    @Test
    fun `swap tokens are snappy with flamingo pop`() {
        val enter = Motion.swapEnter<Float>() as TweenSpec<Float>
        val exit = Motion.swapExit<Float>() as TweenSpec<Float>
        assertEquals(200, enter.durationMillis)
        assertEquals(160, exit.durationMillis)
        assertEquals(Motion.SwapEasing, enter.easing)
        assertEquals(Motion.SwapEasing, exit.easing)
        assertEquals(0.3f, Motion.SwapInitialScale, 0.001f)
    }

    @Test
    fun `select press appear are interruptible springs`() {
        val select = Motion.select<Float>() as SpringSpec<Float>
        assertEquals(750f, select.stiffness, 0.001f)
        val press = Motion.press<Float>() as SpringSpec<Float>
        assertEquals(1220f, press.stiffness, 0.001f)
        val appear = Motion.appear<Float>() as SpringSpec<Float>
        assertEquals(450f, appear.stiffness, 0.001f)
    }

    @Test
    fun `gate snaps under reduce motion and passes through otherwise`() {
        val spec = Motion.morphEnter<Float>()
        assertTrue(Motion.gate(true, spec) is SnapSpec)
        assertSame(spec, Motion.gate(false, spec))
    }
}
