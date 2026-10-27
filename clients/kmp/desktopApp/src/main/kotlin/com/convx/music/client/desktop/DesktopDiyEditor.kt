package com.convx.music.client.desktop

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.convx.music.catalog.presentation.RemoteArtwork
import com.convx.music.playback.*
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import org.koin.compose.koinInject
import java.awt.EventQueue
import java.awt.KeyboardFocusManager
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.math.roundToInt

@Composable
internal fun DesktopDiyEditor(onDismiss: () -> Unit) {
    val settings: PlaybackSettingsStateHolder = koinInject()
    val holder: DesktopDiyStateHolder = koinInject()
    val controller: DesktopMusicController = koinInject()
    val preferences by settings.state.collectAsState()
    val ui by holder.state.collectAsState()
    val playback by controller.playbackSnapshot.collectAsState()
    val player = preferences.playerCustomization
    val palette = desktopPalette()
    val selected = player.diy.stickers.find { it.id == ui.selectedId }
    val density = LocalDensity.current
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.width(1160.dp).height(760.dp).background(palette.background, RoundedCornerShape(16.dp))
            .border(1.dp, palette.foreground.copy(alpha = .12f), RoundedCornerShape(16.dp)).padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Customize player", Modifier.weight(1f), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                MediaButton("Import preset") { chooseMediaFile("Import Convx preset", listOf("convxpreset", "zip"), holder::importPreset) }
                MediaButton("Export preset") { choosePresetDestination(holder::exportPreset) }
                MediaButton("Done", onClick = onDismiss)
            }
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MediaButton(if (ui.wide) "✓ Landscape" else "Landscape") { holder.orientation(true) }
                        MediaButton(if (!ui.wide) "✓ Portrait" else "Portrait") { holder.orientation(false) }
                    }
                    Box(Modifier.weight(1f).fillMaxWidth().background(palette.surface, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        BoxWithConstraints(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                            val width = if (ui.wide) maxWidth else minOf(maxWidth, maxHeight * .58f)
                            val height = if (ui.wide) minOf(maxHeight, width * .64f) else maxHeight
                            Box(Modifier.width(width).height(height).clip(RoundedCornerShape(10.dp)).background(palette.background)) {
                                CompositionLocalProvider(LocalDensity provides Density(density.density * width.value / if (ui.wide) 1280f else 460f, density.fontScale)) {
                                    DesktopExpandedPlayer(controller, com.convx.music.playback.presentation.PlaybackSharedElements(),
                                        preferences.crossfadeEnabled, preferences.autoMixEnabled, false,
                                        {}, {}, {}, {}, {}, onDismiss, Modifier, Modifier.fillMaxSize(), editor = holder)
                                }
                            }
                        }
                    }
                    Text("Live player · drag a sticker to place it. Portrait and landscape keep separate positions. Changes save automatically.", color = palette.muted, fontSize = 12.sp)
                    ui.message?.let { Text(it, color = palette.accent, fontSize = 12.sp) }
                }
                Column(Modifier.width(328.dp).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Layout", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlayerStyle.entries.forEach { style -> MediaButton((if (player.style == style) "✓ " else "") + style.name.lowercase().replaceFirstChar(Char::uppercase)) {
                            holder.edit { it.copy(style = style) }
                        } }
                    }
                    Text("Wide places artwork beside the other sections. Stacked follows the complete section order.", color = palette.muted, fontSize = 12.sp)
                    player.order.forEachIndexed { index, section ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(section.name.lowercase().replace('_', ' ').replaceFirstChar(Char::uppercase), Modifier.weight(1f), fontSize = 13.sp)
                            if (index > 0) MediaButton("↑") { holder.moveSection(section, -1) }
                            if (index < player.order.lastIndex) MediaButton("↓") { holder.moveSection(section, 1) }
                            if (section != PlayerSection.CONTROLS) MediaButton(if (section in player.hidden) "Show" else "Hide") {
                                holder.edit { it.copy(hidden = if (section in it.hidden) it.hidden - section else it.hidden + section) }
                            }
                        }
                    }
                    Text("Stickers · ${player.diy.stickers.size}/10", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(ui.emoji, holder::emoji, Modifier.width(58.dp).background(palette.surface, RoundedCornerShape(6.dp)).padding(8.dp),
                            textStyle = TextStyle(color = palette.foreground, fontFamily = DesktopWebFont, fontSize = 18.sp), singleLine = true)
                        MediaButton("Add emoji", onClick = holder::addEmoji)
                        MediaButton("Image") { chooseMediaFile("Choose sticker", listOf("png", "jpg", "jpeg", "gif", "bmp"), holder::addImage) }
                    }
                    player.diy.stickers.forEachIndexed { index, sticker ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text((if (sticker.id == ui.selectedId) "✓ " else "") + if (sticker.kind == DiyStickerKind.EMOJI) sticker.source else "Image ${index + 1}",
                                Modifier.weight(1f).clickable { holder.select(sticker.id) }.padding(vertical = 7.dp), fontSize = 14.sp)
                        }
                    }
                    selected?.let { sticker ->
                        val transform = sticker.transform(ui.wide)
                        ThemeNumber("Horizontal position", transform.x, -.5f..1.5f) { value -> holder.editSelected { it.withTransform(ui.wide, it.transform(ui.wide).copy(x = value)) } }
                        ThemeNumber("Vertical position", transform.y, -.5f..1.5f) { value -> holder.editSelected { it.withTransform(ui.wide, it.transform(ui.wide).copy(y = value)) } }
                        ThemeNumber("Size", transform.scale, .03f..2f) { value -> holder.editSelected { it.withTransform(ui.wide, it.transform(ui.wide).copy(scale = value)) } }
                        ThemeNumber("Rotation", transform.rotation, -180f..180f, "°") { value -> holder.editSelected { it.withTransform(ui.wide, it.transform(ui.wide).copy(rotation = value)) } }
                        ThemeNumber("Opacity", sticker.opacity, .05f..1f) { value -> holder.editSelected { it.copy(opacity = value) } }
                        ThemeNumber("Layer", sticker.z.toFloat(), -20f..20f) { value -> holder.editSelected { it.copy(z = value.roundToInt()) } }
                        ThemeNumber("Corners", sticker.cornerRadius, 0f..0.5f) { value -> holder.editSelected { it.copy(cornerRadius = value) } }
                        ThemeToggle("Flip horizontally", sticker.flipHorizontal) { value -> holder.editSelected { it.copy(flipHorizontal = value) } }
                        ThemeToggle("Shadow", sticker.shadow) { value -> holder.editSelected { it.copy(shadow = value) } }
                        MediaButton("Remove sticker", onClick = holder::deleteSelected)
                    }
                    Text("Control icons", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    PLAYER_ICON_SLOTS.forEach { slot ->
                        val override = player.icons[slot]
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(slot.lowercase().replaceFirstChar(Char::uppercase), Modifier.weight(1f), fontSize = 13.sp)
                            override?.let { AsyncImage(holder.assets.file(it.fileName), null, Modifier.size(26.dp), contentScale = ContentScale.Fit) }
                            MediaButton("Choose") { chooseMediaFile("Choose ${slot.lowercase()} icon", listOf("png", "jpg", "jpeg", "gif", "bmp")) { holder.setIcon(slot, it) } }
                            if (override != null) MediaButton("Reset") { holder.edit { it.copy(icons = it.icons - slot) } }
                        }
                        if (override != null) ThemeToggle("Tint ${slot.lowercase()}", override.tint) { tint -> holder.edit { it.copy(icons = it.icons + (slot to override.copy(tint = tint))) } }
                    }
                    MediaButton("Reset player") { holder.edit { PlayerCustomization() }; holder.select(null) }
                }
            }
        }
    }
}

@Composable
internal fun DesktopDiyLayer(stickers: List<DiySticker>, assets: DesktopPlayerAssets, wide: Boolean,
    selected: String? = null, editor: DesktopDiyStateHolder? = null, modifier: Modifier = Modifier) {
    val palette = desktopPalette()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        stickers.sortedBy { it.z }.forEach { sticker -> key(sticker.id) {
            val transform = sticker.transform(wide)
            val side = minOf(maxWidth, maxHeight) * transform.scale
            var drag by remember(sticker.id, wide) { mutableStateOf(Offset.Zero) }
            val latest by rememberUpdatedState(transform)
            val gesture = if (editor == null) Modifier else Modifier.pointerInput(sticker.id, wide, widthPx, heightPx) {
                detectDragGestures(onDragStart = { editor.select(sticker.id) }, onDragCancel = { drag = Offset.Zero },
                    onDragEnd = {
                        editor.move(sticker.id, wide, latest.copy(x = latest.x + drag.x / widthPx, y = latest.y + drag.y / heightPx))
                        drag = Offset.Zero
                    }, onDrag = { change, amount -> change.consume(); drag += amount })
            }.clickable { editor.select(sticker.id) }
            Box(Modifier.offset { IntOffset((widthPx * transform.x - with(density) { side.toPx() } / 2).roundToInt(),
                (heightPx * transform.y - with(density) { side.toPx() } / 2).roundToInt()) }.size(side)
                .graphicsLayer {
                    translationX = drag.x; translationY = drag.y; rotationZ = transform.rotation
                    alpha = sticker.opacity; scaleX = if (sticker.flipHorizontal) -1f else 1f
                    shadowElevation = if (sticker.shadow) 12.dp.toPx() else 0f
                }.then(gesture).then(if (selected == sticker.id) Modifier.border(1.dp, palette.accent) else Modifier)
                .clip(RoundedCornerShape((sticker.cornerRadius * 100).roundToInt())), contentAlignment = Alignment.Center) {
                if (sticker.kind == DiyStickerKind.IMAGE) AsyncImage(assets.file(sticker.source), null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                else Text(sticker.source, fontSize = with(density) { (side * .78f).toSp() }, maxLines = 1)
            }
        } }
    }
}

private fun choosePresetDestination(onSelected: (File) -> Unit) {
    EventQueue.invokeLater {
        val chooser = JFileChooser().apply { dialogTitle = "Export Convx preset"; fileFilter = FileNameExtensionFilter("Convx preset", "convxpreset"); selectedFile = File("My player.convxpreset") }
        if (chooser.showSaveDialog(KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow) == JFileChooser.APPROVE_OPTION) {
            val selected = chooser.selectedFile
            val target = if (selected.extension.equals("convxpreset", true)) selected else File(selected.absolutePath + ".convxpreset")
            if (!target.exists() || javax.swing.JOptionPane.showConfirmDialog(null, "Replace ${target.name}?", "Export preset", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) onSelected(target)
        }
    }
}
