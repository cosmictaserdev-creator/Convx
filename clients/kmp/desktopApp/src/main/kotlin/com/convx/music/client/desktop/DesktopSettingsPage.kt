package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.PushButton
import dev.nucleusframework.macoscompose.components.PushButtonStyle
import dev.nucleusframework.macoscompose.components.GroupBox
import dev.nucleusframework.macoscompose.components.Sidebar as NucleusSidebar
import dev.nucleusframework.macoscompose.components.SidebarItem as NucleusSidebarItem
import dev.nucleusframework.macoscompose.components.Switch as NucleusSwitch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.account.MusicAccountState
import com.convx.music.account.presentation.MusicAccountStateHolder
import com.convx.music.playback.presentation.LyricsStateHolder
import com.convx.music.playback.presentation.PlaybackEqualizerSheet
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import com.composables.sfsymbols.SfSymbol
import com.composables.sfsymbols.SfWeight
import com.convx.music.modules.ModuleRepository
import com.convx.music.modules.ModulesScreen
import com.convx.storage.domain.NetworkLibraryRepository
import com.convx.storage.presentation.NetworkLibraryScreen
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@Composable
internal fun DesktopSettingsPage(
    settings: PlaybackSettingsStateHolder,
    lyrics: LyricsStateHolder,
    account: MusicAccountStateHolder,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = desktopPalette()
    val moduleRepository = org.koin.compose.getKoin().getOrNull<ModuleRepository>()
    val playbackController: DesktopMusicController = koinInject()
    val fileAssociations: DesktopFileAssociations = koinInject()
    val scope = rememberCoroutineScope()
    var integrationMessage by remember { mutableStateOf<String?>(null) }
    var registering by remember { mutableStateOf(false) }
    val state by settings.state.collectAsState()
    val accountState by account.state.collectAsState()
    val error by settings.errorMessage.collectAsState()
    var equalizer by remember { mutableStateOf(false) }
    var diy by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Playback") }
    var categoryMenu by remember { mutableStateOf(false) }
    val sections = listOf("Account", "Playback", "Lyrics", "Appearance", "Player", "Storage", "Desktop", "Modules", "Network music", "Privacy")
    Box(modifier, contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 880.dp).fillMaxHeight().fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(start = 32.dp, top = 24.dp, end = 32.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                SettingText("Settings", 34, FontWeight.Bold)
                Box {
                    PushButton(onClick = { categoryMenu = true }, style = PushButtonStyle.Neutral,
                        modifier = Modifier.semantics { contentDescription = "Settings category" }) {
                        SettingText(category, 14, FontWeight.Medium)
                        DesktopGlyph(DesktopGlyphKind.COLLAPSE, palette.muted, 11.dp)
                    }
                    dev.nucleusframework.macoscompose.components.DropdownMenu(categoryMenu, { categoryMenu = false }) {
                        sections.forEach { section ->
                            dev.nucleusframework.macoscompose.components.DropdownMenuItem(onClick = { category = section; categoryMenu = false }) {
                                SettingText(section, 14, if (category == section) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
            key(category) {
                val section = category
                Text(if (section == "Account") "ACCOUNT & INTEGRATIONS" else section.uppercase(),
                    color = palette.muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 2.dp))
                Column(Modifier.fillMaxWidth().background(palette.surface, RoundedCornerShape(18.dp)).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (section) {
                    "Playback" -> {
                        SettingRow("Crossfade", "Blend the end of a song into the next one.") {
                            NucleusSwitch(state.crossfadeEnabled, settings::setCrossfadeEnabled)
                        }
                        SettingRow("Transition duration", "${state.crossfadeDurationMs / 1_000} seconds") {
                            SettingAction("−") { settings.setCrossfadeDuration(state.crossfadeDurationMs - 1_000L) }
                            SettingAction("+") { settings.setCrossfadeDuration(state.crossfadeDurationMs + 1_000L) }
                        }
                        SettingRow("AutoMix", "Choose transitions using the track's timing and analysis.") {
                            NucleusSwitch(state.autoMixEnabled, settings::setAutoMixEnabled)
                        }
                        SettingRow("Equalizer", if (state.equalizerEnabled) "Enabled" else "Off") {
                            SettingAction("Adjust") { equalizer = true }
                        }
                    }
                    "Lyrics" -> {
                        SettingRow("Text size", "${state.lyricsFontSize.toInt()} pt") {
                            SettingAction("−") { settings.setLyricsFontSize(state.lyricsFontSize - 2f) }
                            SettingAction("+") { settings.setLyricsFontSize(state.lyricsFontSize + 2f) }
                        }
                        SettingRow("Timing offset", "${state.lyricsOffsetMs} ms · positive values show lyrics sooner") {
                            SettingAction("−250") { settings.setLyricsOffset(state.lyricsOffsetMs - 250L) }
                            SettingAction("Reset") { settings.setLyricsOffset(0L) }
                            SettingAction("+250") { settings.setLyricsOffset(state.lyricsOffsetMs + 250L) }
                        }
                        SettingRow("Preferred provider", "Automatic looks for the best timing available.") {
                            SettingAction("Automatic", state.lyricsProviderId == null) { settings.setLyricsProvider(null) }
                        }
                        lyrics.sources.forEach { source ->
                            SettingRow(source.label, if (source.wordSynced) "Supports word timing" else "Line timing or plain text") {
                                SettingAction(if (state.lyricsProviderId == source.id) "Selected" else "Use", state.lyricsProviderId == source.id) { settings.setLyricsProvider(source.id) }
                            }
                        }
                    }
                    "Appearance" -> DesktopAppearanceSettings(settings)
                    "Player" -> SettingRow("Player customization", "Reorder sections, place stickers, replace control icons, and share a preset.") {
                        SettingAction("Open editor") { diy = true }
                    }
                    "Storage" -> DesktopStorageSettings(settings)
                    "Desktop" -> {
                        SettingRow("Default music player", "Open audio files from File Explorer with Convx.") {
                            PushButton(text = if (registering) "Opening…" else "Set as default", enabled = fileAssociations.isSupported && !registering,
                                onClick = { scope.launch {
                                    registering = true
                                    integrationMessage = null
                                    try { fileAssociations.openDefaultAppSettings() }
                                    catch (cancelled: CancellationException) { throw cancelled }
                                    catch (error: Exception) { integrationMessage = error.message ?: "Couldn't open default application settings." }
                                    finally { registering = false }
                                } })
                        }
                        SettingText("Select Convx for your audio formats in Windows Settings. Files opened while Convx is running use the existing player.", 13)
                        integrationMessage?.let { SettingText(it, 13) }
                    }
                    "Modules" -> if (moduleRepository != null) {
                        ModulesScreen(moduleRepository, playbackController::playTrack, embedded = true)
                    } else SettingText("Music sources are unavailable. Restart the full desktop application to load them.", 13)
                    "Network music" -> NetworkLibraryScreen(koinInject(), playbackController::playTrack, embedded = true)
                    "Privacy" -> {
                        SettingRow("Search history", "Keep recent searches on this device.") {
                            NucleusSwitch(state.recordSearchHistory, { enabled -> settings.setRecordSearchHistory(enabled) })
                        }
                        SettingRow("Recent searches", "${state.recentSearches.size} saved searches") {
                            SettingAction("Clear") { settings.clearSearchHistory() }
                        }
                    }
                    "Account" -> {
                        val signedIn = accountState.account as? MusicAccountState.SignedIn
                        SettingRow("YouTube Music", signedIn?.account?.name ?: "Not signed in") {
                            SettingAction(if (signedIn == null) "Sign in" else "Sign out") {
                                if (signedIn == null) onSignIn() else account.signOut()
                            }
                        }
                        accountState.message?.let { SettingText(it, 13) }
                    }
                }
                }
            }
            error?.let { SettingText(it, 13) }
        }
        if (equalizer) DesktopEqualizerDialog(state, settings, error) { equalizer = false }
        if (diy) DesktopDiyEditor { diy = false }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, actions: @Composable RowScope.() -> Unit) {
    val palette = desktopPalette()
    dev.nucleusframework.macoscompose.components.GroupedListItem(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        trailing = { Row(horizontalArrangement = Arrangement.spacedBy(10.dp), content = actions) },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            SettingText(title, 14, FontWeight.Medium)
            BasicText(subtitle, style = TextStyle(fontFamily = DesktopWebFont, color = palette.foreground.copy(alpha = 0.5f), fontSize = 12.sp))
        }
    }
}

@Composable
private fun SettingAction(label: String, selected: Boolean = false, onClick: () -> Unit) {
    PushButton(
        text = label, onClick = onClick, selected = selected,
        style = if (selected) PushButtonStyle.Default
            else PushButtonStyle.Neutral,
    )
}

@Composable
private fun SettingText(text: String, size: Int, weight: FontWeight = FontWeight.Normal) {
    val palette = desktopPalette()
    BasicText(text, style = TextStyle(fontFamily = DesktopWebFont, color = palette.foreground, fontSize = size.sp, fontWeight = weight))
}
