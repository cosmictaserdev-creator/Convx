package com.convx.music.client.desktop

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.SystemFont
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.convx.music.ui.ios.foundation.IosTheme

internal data class DesktopPalette(
    val background: Color, val foreground: Color, val muted: Color, val accent: Color,
    val surface: Color, val separator: Color, val dark: Boolean,
)

@Composable
internal fun desktopPalette(): DesktopPalette {
    val colors = IosTheme.colors
    return remember(colors) { DesktopPalette(
        background = colors.systemBackground,
        foreground = colors.label,
        muted = colors.secondaryLabel,
        accent = colors.accent,
        surface = colors.secondarySystemBackground,
        separator = if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f),
        dark = colors.isDark,
    ) }
}

internal val DesktopBackground: Color @Composable get() = desktopPalette().background
internal val DesktopForeground: Color @Composable get() = desktopPalette().foreground
internal val DesktopMuted: Color @Composable get() = desktopPalette().muted
internal val DesktopAccent: Color @Composable get() = desktopPalette().accent
internal val DesktopSurface: Color @Composable get() = desktopPalette().surface
internal val DesktopSeparator: Color @Composable get() = desktopPalette().separator

internal val DefaultDesktopWebFont: FontFamily
    @Composable get() = com.convx.music.designsystem.convxFontFamily()

internal val DesktopWebFont: FontFamily @Composable get() = IosTheme.typography.body.fontFamily ?: DefaultDesktopWebFont
