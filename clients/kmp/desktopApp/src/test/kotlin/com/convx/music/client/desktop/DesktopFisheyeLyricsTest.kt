package com.convx.music.client.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.convx.music.lyrics.*
import com.convx.music.lyrics.bitchord.LyricLine
import com.convx.music.lyrics.bitchord.LyricWord
import com.convx.music.playback.*
import com.convx.music.playback.presentation.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.lang.reflect.Proxy
import kotlin.test.*

@OptIn(ExperimentalTestApi::class)
class DesktopFisheyeLyricsTest {
    @get:Rule val compose = createComposeRule()
    private val words = listOf(LyricWord(0, 200, "I"), LyricWord(200, 3_200, "love"),
        LyricWord(3_200, 3_500, "this"), LyricWord(3_500, 3_900, "world"))
    private val line = LyricLine(timeMs = 0, text = "I love this world", words = words)

    @Test fun theRealLensShaderRendersAndWordsRemainSeekable() {
        compose.mainClock.autoAdvance = false
        var sought: Long? = null
        val entries = listOf(LyricsEntry(0, line.text, words.map { WordTimestamp(it.text, it.startMs / 1000.0, it.endMs / 1000.0) }))
        compose.setContent {
            Box(Modifier.size(800.dp, 650.dp).background(Color(0xFF101014)).testTag("fisheye")) {
                FisheyeLyrics(entries, FontFamily.Default, { 1_500L }, Color(0xFFFF5C88), { sought = it }, Modifier.fillMaxSize())
            }
        }
        compose.mainClock.advanceTimeBy(1_000)
        compose.onNodeWithText("world").performClick()
        compose.runOnIdle { assertEquals(3_500L, sought) }
        val image = compose.onNodeWithTag("fisheye").captureToImage()
        val pixels = image.toPixelMap()
        var visibleInk = 0
        for (y in 0 until pixels.height step 3) for (x in 0 until pixels.width step 3) {
            val pixel = pixels[x, y]
            if (pixel.red > .5f || pixel.green > .5f || pixel.blue > .5f) visibleInk++
        }
        assertTrue(visibleInk > 100, "The runtime shader must draw the lyrics instead of a blank layer")
        save("fisheye", "fisheye-lyrics")
        compose.onNodeWithTag("fisheye").performMouseInput { scroll(160f) }
        compose.mainClock.advanceTimeBy(5_000)
        compose.onNodeWithText("world").assertExists()
    }

    @Test fun ambientLyricsHideControlsRecoverOnPointerMovementAndExitWithEscape() {
        compose.mainClock.autoAdvance = false
        val snapshots = MutableStateFlow(PlaybackSnapshot.EMPTY.copy(track = PlaybackTrackSnapshot("fixture", "Ambient fixture", listOf("Artist")), durationMs = 8_000))
        var played = 0
        var exits = 0
        val controller = Proxy.newProxyInstance(DesktopMusicController::class.java.classLoader, arrayOf(DesktopMusicController::class.java)) { _, method, _ ->
            when (method.name) {
                "getPlaybackSnapshot" -> snapshots
                "togglePlayPause" -> { played++; null }
                "hashCode" -> 1
                "equals" -> false
                else -> null
            }
        } as DesktopMusicController
        val repository = object : PlaybackSettingsRepository {
            val value = MutableStateFlow(PlaybackSettings(lyricsAnimationStyle = LyricsAnimationStyle.FISHEYE))
            override fun observeSettings() = value
            override suspend fun getSettings() = value.value
            override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings {
                value.value = transform(value.value).normalized(); return value.value
            }
        }
        val settings = PlaybackSettingsStateHolder(repository)
        val lyrics = LyricsStateHolder(listOf(object : SharedLyricsSource {
            override val id = "fixture"
            override val label = "Fixture"
            override val wordSynced = true
            override suspend fun lyrics(query: LyricsQuery) = listOf(line)
        }))
        try {
            compose.setContent {
                com.convx.music.ui.ios.foundation.ProvideIosTheme(com.convx.music.ui.ios.foundation.darkIosColors()) {
                DesktopNucleusTheme(glass = false) {
                    DesktopAmbientLyrics(controller, lyrics, settings, { exits++ }, Modifier.size(900.dp, 700.dp).testTag("ambient"))
                }
                }
            }
            compose.runOnIdle { lyrics.load(LyricsQuery("fixture", "Fixture", "Artist", 8_000)) }
            compose.waitUntil { lyrics.state.value.document != null }
            compose.mainClock.advanceTimeBy(1_000)
            compose.onNodeWithContentDescription("Exit ambient mode").assertIsDisplayed()
            save("ambient", "ambient-fisheye")
            compose.mainClock.advanceTimeBy(5_000)
            compose.onNodeWithContentDescription("Exit ambient mode").assertDoesNotExist()
            compose.onNodeWithTag("ambient").performMouseInput { moveTo(center) }
            compose.mainClock.advanceTimeBy(300)
            compose.onNodeWithContentDescription("Exit ambient mode").assertIsDisplayed()
            compose.onNodeWithTag("ambient").performSemanticsAction(SemanticsActions.RequestFocus)
            compose.onNodeWithTag("ambient").performKeyInput { pressKey(Key.Spacebar); pressKey(Key.Escape) }
            compose.runOnIdle { assertEquals(1, played); assertEquals(1, exits) }
        } finally { lyrics.close(); settings.close() }
    }

    private fun save(tag: String, name: String) {
        val file = File("build/reports/parity/$name.png").apply { parentFile.mkdirs() }
        org.jetbrains.skia.Image.makeFromBitmap(compose.onNodeWithTag(tag).captureToImage().asSkiaBitmap()).use { image ->
            image.encodeToData()?.use { file.writeBytes(it.bytes) }
        }
    }
}
