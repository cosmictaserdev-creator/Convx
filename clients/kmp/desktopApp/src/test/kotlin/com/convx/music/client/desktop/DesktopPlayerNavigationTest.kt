package com.convx.music.client.desktop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.convx.music.playback.*
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import com.convx.music.playback.presentation.PlaybackSharedElements
import com.convx.music.playback.presentation.LyricsStateHolder
import com.convx.music.account.*
import com.convx.music.account.presentation.MusicAccountStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import java.lang.reflect.Proxy
import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DesktopPlayerNavigationTest {
    @get:Rule val compose = createComposeRule()
    @get:Rule val temporary = TemporaryFolder()

    @Test fun auxiliaryTabsToggleAndRemainMutuallyExclusiveAtNarrowWidths() {
        var outputClicks = 0
        compose.setContent {
            var page by remember { mutableStateOf(PlayerPage.COVER) }
            DesktopNucleusTheme(glass = false, reducedMotion = true) {
                Box(Modifier.width(320.dp)) {
                    DesktopPlayerTabs(page, { page = page.toggle(PlayerPage.LYRICS) },
                        { page = page.toggle(PlayerPage.QUEUE) }, { outputClicks++ }, "Built-in speakers")
                }
            }
        }
        compose.onNodeWithText("Lyrics").assertIsNotSelected().performClick().assertIsSelected()
        compose.onNodeWithText("Queue").performClick().assertIsSelected()
        compose.onNodeWithText("Lyrics").assertIsNotSelected()
        compose.onNodeWithText("Queue").performClick().assertIsNotSelected()
        compose.onNodeWithContentDescription("Audio output: Built-in speakers").performClick()
        compose.runOnIdle { assertEquals(1, outputClicks) }
    }

    @Test fun everySettingsCategoryIsReachableInTheNormalList() {
        var selected: String? = null
        compose.setContent {
            DesktopNucleusTheme(glass = false, reducedMotion = true) {
                Column(Modifier.size(360.dp, 500.dp).testTag("settings-list").background(desktopPalette().background).verticalScroll(rememberScrollState())) {
                    DesktopSettingsList { selected = it }
                }
            }
        }
        capture("settings-list", "settings-list")
        for (category in listOf("Account", "Playback", "Lyrics", "Appearance", "Player", "Storage", "Desktop", "Modules", "Network music", "Privacy")) {
            compose.onNodeWithText(category).performScrollTo().performClick()
            compose.runOnIdle { assertEquals(category, selected) }
        }
    }

    @Test fun seekKeyboardInputReachesTheSliderBeforeTheExpandedPlayerShortcuts() {
        val playback = MutableStateFlow(PlaybackSnapshot.EMPTY.copy(
            track = PlaybackTrackSnapshot("fixture", "Fixture song", listOf("Artist")),
            phase = PlaybackPhase.READY, durationMs = 120_000, positionMs = 30_000,
        ))
        var sought: Long? = null
        var toggles = 0
        var collapsed = 0
        var closedPage = 0
        var skipped: Boolean? = null
        val controller = Proxy.newProxyInstance(DesktopMusicController::class.java.classLoader,
            arrayOf(DesktopMusicController::class.java)) { _, method, args ->
            when (method.name) {
                "getPlaybackSnapshot" -> playback
                "getQueueSnapshot" -> MutableStateFlow(PlaybackQueueSnapshot.EMPTY)
                "seekTo" -> { sought = args[0] as Long; null }
                "togglePlayPause" -> { toggles++; null }
                "requestSkip" -> { skipped = args[0] as Boolean; null }
                "hashCode" -> 1
                "equals" -> false
                "toString" -> "Player fixture"
                else -> null
            }
        } as DesktopMusicController
        val settings = PlaybackSettingsStateHolder(FixedPlaybackSettings(PlaybackSettings(reducedMotion = true)))
        try {
            compose.setContent {
                KoinApplication(application = { modules(module {
                    single { settings }
                    single { DesktopPlayerAssets(temporary.root) }
                }) }) {
                    DesktopNucleusTheme(glass = false, reducedMotion = true) {
                        Box(Modifier.size(700.dp, 760.dp).testTag("expanded-player")) {
                            DesktopExpandedPlayer(controller, PlaybackSharedElements(), false, false, false,
                                {}, {}, {}, {}, {}, { collapsed++ }, Modifier, modifier = Modifier.fillMaxSize(),
                                onLyrics = {}, page = PlayerPage.LYRICS, onClosePage = { closedPage++ })
                        }
                    }
                }
            }
            compose.waitForIdle()
            capture("expanded-player", "expanded-player")
            val seek = compose.onNodeWithContentDescription("Playback position")
            seek.performScrollTo().performSemanticsAction(SemanticsActions.RequestFocus)
            seek.performKeyInput { pressKey(Key.DirectionRight) }
            compose.runOnIdle { assertEquals(35_000L, sought) }
            seek.performKeyInput { pressKey(Key.MoveHome) }
            compose.runOnIdle { assertEquals(0L, sought) }
            seek.performKeyInput { keyDown(Key.CtrlLeft); pressKey(Key.DirectionRight); keyUp(Key.CtrlLeft) }
            compose.runOnIdle { assertEquals(true, skipped); assertEquals(0L, sought) }
            seek.performKeyInput { pressKey(Key.Escape) }
            compose.runOnIdle { assertEquals(1, closedPage); assertEquals(0, collapsed) }
            compose.onNodeWithContentDescription("Play", useUnmergedTree = true).performScrollTo().performClick()
            compose.runOnIdle { assertEquals(1, toggles) }
        } finally { settings.close() }
    }

    @Test fun settingsBackNavigationKeepsSavedChangesAndStartsEachPageAtTheTop() {
        val stored = MutableStateFlow(PlaybackSettings(reducedMotion = true))
        val repository = object : PlaybackSettingsRepository {
            override fun observeSettings() = stored
            override suspend fun getSettings() = stored.value
            override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings {
                stored.value = transform(stored.value).normalized()
                return stored.value
            }
        }
        val settings = PlaybackSettingsStateHolder(repository)
        val accountRepository = Proxy.newProxyInstance(MusicAccountRepository::class.java.classLoader,
            arrayOf(MusicAccountRepository::class.java)) { _, method, _ ->
            when (method.name) {
                "getState" -> MutableStateFlow<MusicAccountState>(MusicAccountState.SignedOut)
                "restore" -> MusicAccountResult.Success(Unit)
                else -> null
            }
        } as MusicAccountRepository
        val account = MusicAccountStateHolder(accountRepository)
        val lyrics = LyricsStateHolder(emptyList())
        val controller = Proxy.newProxyInstance(DesktopMusicController::class.java.classLoader,
            arrayOf(DesktopMusicController::class.java)) { _, _, _ -> null } as DesktopMusicController
        try {
            compose.setContent {
                KoinApplication(application = { modules(module {
                    single<DesktopMusicController> { controller }
                    single { DesktopFileAssociations() }
                }) }) {
                    DesktopNucleusTheme(glass = false, reducedMotion = true) {
                        Box(Modifier.size(390.dp, 650.dp).testTag("settings-page").background(desktopPalette().background)) {
                            DesktopSettingsPage(settings, lyrics, account, {}, Modifier.fillMaxSize())
                        }
                    }
                }
            }
            compose.onNodeWithText("Playback").performClick()
            compose.onNodeWithContentDescription("Crossfade").performClick()
            compose.waitUntil { stored.value.crossfadeEnabled }
            capture("settings-page", "playback-settings-narrow")
            compose.onNodeWithContentDescription("SponsorBlock").performScrollTo().performClick()
            compose.waitUntil { stored.value.sponsorBlockEnabled }
            capture("settings-page", "sponsorblock-settings-narrow")
            compose.onNodeWithContentDescription("Back to settings").performScrollTo().performClick()
            compose.onNodeWithText("Privacy").performScrollTo().performClick()
            compose.onNodeWithContentDescription("Back to settings").assertIsDisplayed().performClick()
            compose.onNodeWithText("Playback").assertIsDisplayed().performClick()
            compose.onNodeWithContentDescription("Crossfade").assertIsOn()
            compose.onNodeWithContentDescription("SponsorBlock").performScrollTo().assertIsOn()
            compose.onNodeWithContentDescription("Back to settings").performKeyInput { pressKey(Key.Escape) }
            compose.onNodeWithText("Settings").assertIsDisplayed()
        } finally { lyrics.close(); account.close(); settings.close() }
    }

    @Test fun audioDeviceFailureCanBeRefreshedAndTheSystemDefaultIsAlwaysSelectable() {
        val stored = MutableStateFlow(PlaybackSettings(audioOutputDeviceId = "disconnected"))
        val repository = object : PlaybackSettingsRepository {
            override fun observeSettings() = stored
            override suspend fun getSettings() = stored.value
            override suspend fun updateSettings(transform: (PlaybackSettings) -> PlaybackSettings): PlaybackSettings {
                stored.value = transform(stored.value).normalized()
                return stored.value
            }
        }
        val settings = PlaybackSettingsStateHolder(repository)
        val attempts = java.util.concurrent.atomic.AtomicInteger()
        try {
            compose.setContent {
                DesktopNucleusTheme(glass = false, reducedMotion = true) {
                    DesktopAudioOutputDialog(settings, loadDevices = {
                        if (attempts.incrementAndGet() == 1) error("Fixture enumeration failure")
                        listOf(DesktopAudioDevice("speaker", "Test speakers"))
                    }, onDismiss = {})
                }
            }
            compose.waitUntil { compose.onAllNodesWithText("Couldn't load audio devices. You can still use the system default.").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("System default").assertIsEnabled().performClick()
            compose.waitUntil { stored.value.audioOutputDeviceId == null }
            compose.onNodeWithText("Refresh devices").performClick()
            compose.waitUntil { compose.onAllNodesWithText("Test speakers").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Test speakers").performClick()
            compose.waitUntil { stored.value.audioOutputDeviceId == "speaker" }
        } finally { settings.close() }
    }

    private fun capture(tag: String, name: String) {
        val image = compose.onNodeWithTag(tag).captureToImage()
        val file = File("build/reports/parity/$name.png").apply { parentFile.mkdirs() }
        org.jetbrains.skia.Image.makeFromBitmap(image.asSkiaBitmap()).use { skia ->
            skia.encodeToData()?.use { file.writeBytes(it.bytes) }
        }
    }
}
