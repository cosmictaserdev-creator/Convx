package com.convx.music.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.convx.music.lyrics.rememberLyricsFont
import com.convx.music.ui.ios.compat.*
import com.convx.music.viewmodels.LyricsAppearanceViewModel

@Composable
fun LyricsFontSettings(viewModel: LyricsAppearanceViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val family = rememberLyricsFont(state.font)
    val import = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(viewModel::importFont) }
    Column(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Lyrics font", style = AppTheme.typography.titleMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("default" to "Default", "rounded" to "Rounded", "serif" to "Serif", "monospace" to "Mono").forEach { (id, label) ->
                TextButton(onClick = { viewModel.selectFont(id) }) {
                    Text(if (state.font == id) "✓ $label" else label)
                }
            }
        }
        if (state.customFont.isNotBlank()) TextButton(onClick = { viewModel.selectFont(state.customFont) }) {
            Text(if (state.font == state.customFont) "✓ Imported font" else "Use imported font")
        }
        OutlinedButton(onClick = { import.launch(arrayOf("font/*", "application/x-font-ttf", "application/x-font-opentype", "application/octet-stream")) }, enabled = !state.importing) {
            Text(if (state.importing) "Importing font…" else "Import a TTF or OTF font")
        }
        if (state.font.startsWith("custom-")) Text("Custom font selected", style = AppTheme.typography.bodySmall)
        Text("Text size: ${state.size.toInt()} sp")
        Slider(value = state.size, onValueChange = viewModel::resize, onValueChangeFinished = viewModel::saveSize, valueRange = 18f..60f)
        Text("Your lyrics, your way", fontFamily = family, fontSize = state.size.sp, lineHeight = (state.size * 1.3f).sp)
        state.error?.let { Text(it, color = AppTheme.colorScheme.error) }
    }
}
