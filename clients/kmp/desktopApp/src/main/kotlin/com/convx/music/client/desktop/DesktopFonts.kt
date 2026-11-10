package com.convx.music.client.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.SystemFont
import com.convx.music.playback.AppearanceSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.GraphicsEnvironment
import java.io.File
import java.security.MessageDigest

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun rememberDesktopFont(appearance: AppearanceSettings): FontFamily {
    val defaultFont = com.convx.music.designsystem.convxFontFamily()
    val family by produceState(defaultFont, appearance.fontFamily, appearance.customFontFile) {
        value = withContext(Dispatchers.IO) {
            try {
                appearance.customFontFile?.let(::File)?.takeIf { it.isFile }?.let { file ->
                    GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, file))
                }
                if (appearance.fontFamily == "System") defaultFont else FontFamily(
                    SystemFont(appearance.fontFamily, FontWeight.Normal), SystemFont(appearance.fontFamily, FontWeight.Medium),
                    SystemFont(appearance.fontFamily, FontWeight.SemiBold), SystemFont(appearance.fontFamily, FontWeight.Bold))
            } catch (_: Exception) { defaultFont }
        }
    }
    return family
}

internal object DesktopFonts {
    val installed: List<String> by lazy { GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.toList().sorted() }

    fun install(file: File, directory: File): Pair<String, String> {
        require(file.extension.lowercase() in setOf("ttf", "otf") && file.length() <= 32 * 1024 * 1024) { "Choose a TrueType or OpenType font under 32 MB." }
        val font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, file)
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)
        directory.mkdirs()
        val digest = MessageDigest.getInstance("SHA-256").digest(file.readBytes()).take(10).joinToString("") { "%02x".format(it) }
        val destination = File(directory, "$digest.${file.extension.lowercase()}")
        if (file.canonicalFile != destination.canonicalFile) file.copyTo(destination, overwrite = true)
        return font.family to destination.path
    }
}
