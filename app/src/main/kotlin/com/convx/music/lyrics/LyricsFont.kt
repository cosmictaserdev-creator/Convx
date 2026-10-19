package com.convx.music.lyrics

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import android.graphics.Typeface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Imported faces are loaded once, away from the UI thread, and reused by every line. */
@Composable
fun rememberLyricsFont(selection: String): FontFamily {
    val context = LocalContext.current
    val family by produceState<FontFamily>(FontFamily.Default, selection) {
        value = when (selection) {
            "serif" -> FontFamily.Serif
            "monospace" -> FontFamily.Monospace
            "rounded" -> FontFamily(Typeface.create("sans-serif-rounded", Typeface.NORMAL))
            else -> if (selection.startsWith("custom-")) withContext(Dispatchers.IO) {
                runCatching {
                    require(selection.matches(Regex("custom-\\d+\\.font")))
                    FontFamily(Typeface.createFromFile(context.filesDir.resolve("lyrics-fonts").resolve(selection)))
                }.getOrDefault(FontFamily.Default)
            } else FontFamily.Default
        }
    }
    return family
}
