package com.convx.music.client.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

/** Exercise the Skia shader call that previously aborted the window's first frame. */
@OptIn(ExperimentalTestApi::class)
class DesktopHazeRenderingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun progressiveSidebarBlurRendersWithTheResolvedComposeRuntime() {
        compose.setContent {
            val state = rememberHazeState()
            Box(Modifier.size(240.dp).testTag("haze-fixture")) {
                Row(Modifier.fillMaxSize().hazeSource(state)) {
                    repeat(20) { index ->
                        Box(Modifier.width(12.dp).fillMaxHeight()
                            .background(if (index % 2 == 0) Color.Red else Color.Blue))
                    }
                }
                val style = HazeMaterials.ultraThin().then {
                    noiseFactor(0f)
                    progressive(HazeProgressive.verticalGradient(startIntensity = 0f, endIntensity = 1f))
                }
                Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(100.dp)
                    .hazeBlur(input = HazeInput.Sources(state), style = style))
            }
        }
        compose.waitForIdle()
        val pixels = compose.onNodeWithTag("haze-fixture").captureToImage().toPixelMap()
        val sharp = pixels[pixels.width / 4, pixels.height / 4]
        val blurred = pixels[pixels.width / 4, pixels.height * 9 / 10]
        assertTrue(sharp.red < .01f || sharp.blue < .01f, "Source stripes must remain sharp above the blur")
        assertTrue(blurred.red > .05f && blurred.blue > .05f,
            "Progressive Haze must draw mixed source colors rather than leaving a blank frame")
    }
}
