package com.convx.music.widget

import android.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

class OneUiBlurTest {
    @Test
    fun tintKeepsAlphaInsideTheRangeOneUiBlursFor() {
        for (night in listOf(true, false)) {
            val alpha = Color.alpha(OneUiBlur.tint(night))
            assertTrue("alpha $alpha would be skipped by the launcher", alpha in 1..254)
        }
    }
}
