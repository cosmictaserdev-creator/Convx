package com.convx.music.client.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.convx.music.ui.ios.foundation.IosTheme
import dev.nucleusframework.macoscompose.theme.MacosTheme
import dev.nucleusframework.macoscompose.theme.Typography
import dev.nucleusframework.macoscompose.theme.LocalTypography
import dev.nucleusframework.macoscompose.theme.LocalTextStyle
import dev.nucleusframework.macoscompose.theme.darkColorScheme
import dev.nucleusframework.macoscompose.theme.lightColorScheme
import dev.nucleusframework.macoscompose.theme.Animations
import dev.nucleusframework.macoscompose.components.LocalSidebarGlassRegionFactory

@Composable
internal fun DesktopNucleusTheme(glass: Boolean, reducedMotion: Boolean = false, micaBackdropActive: Boolean = false, content: @Composable () -> Unit) {
    val colors = IosTheme.colors
    val type = IosTheme.typography
    val typography = Typography(
        largeTitle = type.largeTitle, title1 = type.title, title2 = type.title2,
        title3 = type.title3, headline = type.headline, body = type.body,
        callout = type.callout, subheadline = type.subheadline, footnote = type.footnote,
        caption1 = type.caption, caption2 = type.caption2,
    )
    val scheme = (if (colors.isDark) darkColorScheme() else lightColorScheme()).copy(
        background = colors.systemBackground, onBackground = colors.label,
        surface = colors.secondarySystemBackground, onSurface = colors.label,
        textPrimary = colors.label, textSecondary = colors.secondaryLabel,
        accent = colors.accent, primary = colors.accent,
        onAccent = if (colors.accent.luminance() > .5f) Color.Black else Color.White,
    )
    MacosTheme(darkTheme = colors.isDark, colorScheme = scheme, typography = typography, liquidGlass = glass,
        animations = if (reducedMotion) Animations(0, 0, 0, 0, 0) else Animations()) {
        val sidebarMaterial = if (micaBackdropActive) remember {
            // Mica is applied to the whole native window; its sidebar region needs no additional layer.
            { _: Dp -> Modifier }
        } else LocalSidebarGlassRegionFactory.current
        // The toolkit resolves its own platform font; retain the user's selected Convx family.
        CompositionLocalProvider(LocalTypography provides typography, LocalTextStyle provides typography.body,
            LocalSidebarGlassRegionFactory provides sidebarMaterial, content = content)
    }
}
