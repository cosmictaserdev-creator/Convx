package com.convx.music.client.desktop

import dev.nucleusframework.macoscompose.components.Slider as NucleusSlider
import dev.nucleusframework.macoscompose.components.Switch as NucleusSwitch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.convx.music.playback.AppearanceSettings
import com.convx.music.playback.presentation.PlaybackSeekSlider
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.EventQueue
import java.awt.KeyboardFocusManager
import java.io.File
import javax.swing.JColorChooser
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.roundToInt

@Composable
internal fun DesktopAppearanceSettings(settings: PlaybackSettingsStateHolder) {
    val palette = desktopPalette()
    val preferences by settings.state.collectAsState()
    val appearance = preferences.appearance
    val scope = rememberCoroutineScope()
    var fonts by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Appearance", fontSize = 23.sp, fontWeight = FontWeight.Bold)
        ThemeToggle("Show Home filter tabs", appearance.showHomeFilters) { value -> settings.updateAppearance { it.copy(showHomeFilters = value) } }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MediaButton(if (appearance.followSystemTheme) "✓ System" else "System") { settings.updateAppearance { it.copy(followSystemTheme = true) } }
            MediaButton(if (!appearance.followSystemTheme && !preferences.darkTheme) "✓ Light" else "Light") { settings.setDarkTheme(false) }
            MediaButton(if (!appearance.followSystemTheme && preferences.darkTheme) "✓ Dark" else "Dark") { settings.setDarkTheme(true) }
        }
        ThemeColorRow("Accent color", appearance.accentArgb, { color -> settings.updateAppearance { it.copy(accentArgb = color) } }) {
            settings.updateAppearance { it.copy(accentArgb = 0xFFFA233B) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(0xFFFA233B, 0xFFFF9500, 0xFF34C759, 0xFF007AFF, 0xFFAF52DE, 0xFFFF2D55).forEach { color ->
                Box(Modifier.size(28.dp).background(Color(color), CircleShape).clickable { settings.updateAppearance { it.copy(accentArgb = color) } })
            }
        }
        ThemeColorRow("${if (palette.dark) "Dark" else "Light"} background", (if (palette.dark) appearance.darkBackgroundArgb else appearance.lightBackgroundArgb) ?: if (palette.dark) 0xFF000000 else 0xFFFFFFFF,
            { color -> settings.updateAppearance { if (palette.dark) it.copy(darkBackgroundArgb = color) else it.copy(lightBackgroundArgb = color) } }) {
            settings.updateAppearance { if (palette.dark) it.copy(darkBackgroundArgb = null) else it.copy(lightBackgroundArgb = null) }
        }
        ThemeColorRow("Text color", (if (palette.dark) appearance.darkTextArgb else appearance.lightTextArgb) ?: if (palette.dark) 0xFFE6E6E6 else 0xFF242424,
            { color -> settings.updateAppearance { if (palette.dark) it.copy(darkTextArgb = color) else it.copy(lightTextArgb = color) } }) {
            settings.updateAppearance { if (palette.dark) it.copy(darkTextArgb = null) else it.copy(lightTextArgb = null) }
        }
        ThemeRow("Font", appearance.fontFamily) {
            MediaButton("Choose") { fonts = true }
            MediaButton("Import font") {
                chooseMediaFile("Choose font", listOf("ttf", "otf")) { file -> scope.launch {
                    try {
                        val installed = withContext(Dispatchers.IO) { DesktopFonts.install(file, File(desktopLibraryDatabasePath()).parentFile.resolve("fonts")) }
                        settings.updateAppearance { it.copy(fontFamily = installed.first, customFontFile = installed.second) }
                        message = null
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (error: Exception) { message = error.message ?: "Couldn't load that font." }
                } }
            }
        }
        Text("Aa Bb Cc 0123456789", fontSize = 25.sp)
        message?.let { Text(it, color = palette.muted, fontSize = 12.sp) }
        ThemeNumber("Text size", appearance.fontScale, 0.8f..1.4f, suffix = "×") { value -> settings.updateAppearance { it.copy(fontScale = value) } }
        ThemeNumber("Sidebar width", appearance.sidebarWidth, 220f..320f, suffix = " dp") { value -> settings.updateAppearance { it.copy(sidebarWidth = value) } }
        ThemeNumber("Content corners", appearance.containerCornerRadius, 0f..24f, suffix = " dp") { value -> settings.updateAppearance { it.copy(containerCornerRadius = value) } }
        ThemeNumber("Player artwork corners", appearance.artworkCornerRadius, 0f..60f, suffix = " dp") { value -> settings.updateAppearance { it.copy(artworkCornerRadius = value) } }
        Text("Glass player", fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
        ThemeToggle("Liquid glass", appearance.glassEnabled) { value -> settings.updateAppearance { it.copy(glassEnabled = value) } }
        ThemeNumber("Blur", appearance.glassBlurRadius, 0f..40f, suffix = " px") { value -> settings.updateAppearance { it.copy(glassBlurRadius = value) } }
        ThemeNumber("Refraction", appearance.glassLensAmount, 0f..40f) { value -> settings.updateAppearance { it.copy(glassLensAmount = value) } }
        ThemeNumber("Tint opacity", appearance.glassOpacity, 0.1f..0.9f) { value -> settings.updateAppearance { it.copy(glassOpacity = value) } }
        ThemeNumber("Saturation", appearance.glassSaturation, 0f..3f) { value -> settings.updateAppearance { it.copy(glassSaturation = value) } }
        ThemeToggle("Chromatic edges", appearance.glassChromaticAberration) { value -> settings.updateAppearance { it.copy(glassChromaticAberration = value) } }
        ThemeToggle("Depth effect", appearance.glassDepthEffect) { value -> settings.updateAppearance { it.copy(glassDepthEffect = value) } }
        ThemeToggle("Reduced motion", preferences.reducedMotion, settings::setReducedMotion)
        MediaButton("Reset appearance") { settings.updateAppearance { AppearanceSettings() } }
    }
    if (fonts) FontPicker(appearance.fontFamily, { fonts = false }) { name ->
        settings.updateAppearance { it.copy(fontFamily = name, customFontFile = null) }; fonts = false
    }
}

@Composable
internal fun ThemeNumber(title: String, initial: Float, range: ClosedFloatingPointRange<Float>, suffix: String = "", onSave: (Float) -> Unit) {
    val palette = desktopPalette()
    var value by remember(initial) { mutableFloatStateOf(initial) }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 14.sp)
            Text("${(value * 100).roundToInt() / 100f}$suffix", color = palette.muted, fontSize = 12.sp)
        }
        NucleusSlider(value = value, onValueChange = { value = it }, valueRange = range,
            onValueChangeFinished = { onSave(value) }, modifier = Modifier.fillMaxWidth().height(28.dp))
    }
}

@Composable
internal fun ThemeToggle(title: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    ThemeRow(title) { NucleusSwitch(enabled, onToggle) }
}

@Composable
private fun ThemeRow(title: String, subtitle: String? = null, content: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            subtitle?.let { Text(it, color = DesktopMuted, fontSize = 12.sp) }
        }
        content()
    }
}

@Composable
private fun ThemeColorRow(title: String, color: Long, onColor: (Long) -> Unit, onReset: () -> Unit) {
    ThemeRow(title, "#${(color and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()}") {
        Box(Modifier.size(28.dp).background(Color(color), CircleShape).clickable { chooseThemeColor(title, color, onColor) })
        MediaButton("Choose") { chooseThemeColor(title, color, onColor) }
        MediaButton("Reset", onClick = onReset)
    }
}

@Composable
private fun FontPicker(selected: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val palette = desktopPalette()
    var query by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.width(460.dp).height(560.dp).background(palette.surface, androidx.compose.foundation.shape.RoundedCornerShape(18.dp)).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Choose font", fontSize = 23.sp, fontWeight = FontWeight.Bold)
            SearchField(query, { query = it }, Modifier.fillMaxWidth())
            val names = remember(query) { (listOf("System") + DesktopFonts.installed).distinct().filter { it.contains(query, true) } }
            LazyColumn(Modifier.weight(1f)) { itemsIndexed(names, key = { _, name -> name }) { _, name ->
                Text((if (name == selected) "✓  " else "") + name, Modifier.fillMaxWidth().clickable { onSelect(name) }.padding(vertical = 11.dp),
                    color = if (name == selected) palette.accent else palette.foreground, fontSize = 15.sp)
            } }
            MediaButton("Cancel", onClick = onDismiss)
        }
    }
}

private fun chooseThemeColor(title: String, initial: Long, onSelected: (Long) -> Unit) {
    EventQueue.invokeLater {
        val chooser = JColorChooser(java.awt.Color(initial.toInt()))
        val owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow
        val dialog = JColorChooser.createDialog(owner, title, true, chooser,
            { onSelected(chooser.color.rgb.toLong() and 0xFFFFFFFF) }, null)
        dialog.isAlwaysOnTop = owner == null
        dialog.setLocationRelativeTo(owner)
        dialog.isVisible = true
        dialog.dispose()
    }
}

internal fun chooseMediaFile(title: String, extensions: List<String>, onSelected: (File) -> Unit) {
    EventQueue.invokeLater {
        val chooser = object : JFileChooser() {
            override fun createDialog(parent: java.awt.Component?): javax.swing.JDialog =
                super.createDialog(parent).apply { isAlwaysOnTop = parent == null }
        }.apply { dialogTitle = title; fileFilter = FileNameExtensionFilter(title, *extensions.toTypedArray()) }
        if (chooser.showOpenDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow) == JFileChooser.APPROVE_OPTION) onSelected(chooser.selectedFile)
    }
}
