package com.convx.music.client.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.compose.ui.unit.dp
import com.convx.music.playback.presentation.PlaybackPlayerMorph
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DesktopPlayerInputTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun secondaryClickAndLongPressOpenOptionsWithoutPlayingTheRow() {
        var playCount = 0
        var menuCount = 0
        compose.setContent {
            Box(Modifier.size(300.dp, 54.dp).testTag("track")
                .desktopInteractive(onContextMenu = { menuCount++ }) { playCount++ })
        }
        compose.onNodeWithTag("track").performMouseInput { click(button = MouseButton.Secondary) }
        compose.runOnIdle { assertEquals(1, menuCount); assertEquals(0, playCount) }
        compose.onNodeWithTag("track").performMouseInput { click() }
        compose.runOnIdle { assertEquals(1, playCount) }
        compose.onNodeWithTag("track").performTouchInput { longClick() }
        compose.runOnIdle { assertEquals(2, menuCount); assertEquals(1, playCount) }
    }

    @Test
    fun compactPlayerAllowsPageClicksAndScrollingAfterRepeatedExpansion() {
        var clicks = 0
        var scrollPosition = 0
        compose.setContent {
            var expanded by remember { mutableStateOf(false) }
            val scroll = rememberScrollState()
            scrollPosition = scroll.value
            Box(Modifier.size(800.dp, 600.dp)) {
                Column(Modifier.fillMaxSize().verticalScroll(scroll).testTag("page")) {
                    BasicText("Browse", Modifier.testTag("browse").clickable { clicks++ })
                    Box(Modifier.height(1800.dp))
                }
                PlaybackPlayerMorph(
                    trackId = "track", expanded = expanded,
                    onExpand = { expanded = true }, onCollapse = { expanded = false },
                    modifier = Modifier.fillMaxSize(), dragGesturesEnabled = false,
                    compactContent = { _, expand ->
                        BasicText("Open player", Modifier.align(Alignment.BottomCenter).testTag("mini").clickable(onClick = expand))
                    },
                    expandedContent = { _, collapse ->
                        BasicText("Close player", Modifier.testTag("collapse").clickable(onClick = collapse))
                    },
                )
            }
        }
        repeat(3) {
            compose.onNodeWithTag("browse").performMouseInput { click() }
            compose.runOnIdle { assertEquals(it + 1, clicks) }
            compose.onNodeWithTag("mini").performMouseInput { click() }
            compose.waitForIdle()
            compose.onNodeWithTag("collapse").performMouseInput { click() }
            compose.waitForIdle()
        }
        compose.onNodeWithTag("page").performMouseInput { scroll(600f) }
        compose.waitForIdle()
        compose.runOnIdle { assertTrue(scrollPosition > 0, "The compact player must not intercept page wheel input") }
    }
}
