package com.convx.music.client.desktop

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.convx.music.canvas.ArtworkColors
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.ProvideIosTheme
import com.convx.music.playback.presentation.PlaybackSettingsStateHolder
import org.koin.compose.koinInject

@Composable
internal fun DesktopArtistTheme(artist: Boolean, artwork: DesktopDetailArtwork, content: @Composable () -> Unit) {
    if (!artist) { content(); return }
    val settings: PlaybackSettingsStateHolder = koinInject()
    val preferences by settings.state.collectAsState()
    val rgb = ArtworkColors.pageBackground(artwork.backgroundRgb ?: 0x1C1C1E)
    val tint by animateColorAsState(Color(rgb or 0xFF000000.toInt()), tween(if (preferences.reducedMotion) 0 else 260), label = "Artist artwork color")
    val colors = IosTheme.colors.copy(isDark = true, systemBackground = tint,
        secondarySystemBackground = Color.White.copy(alpha = .06f).compositeOver(tint),
        label = Color.White, secondaryLabel = Color.White.copy(alpha = .65f),
        accent = Color.White, separator = Color.White.copy(alpha = .14f))
    val nucleus = dev.nucleusframework.macoscompose.theme.MacosTheme.colorScheme.copy(
        background = tint, onBackground = Color.White, surface = colors.secondarySystemBackground,
        onSurface = Color.White, textPrimary = Color.White, textSecondary = colors.secondaryLabel,
        accent = Color.White, onAccent = tint,
    )
    ProvideIosTheme(colors = colors, typography = IosTheme.typography) {
        CompositionLocalProvider(dev.nucleusframework.macoscompose.theme.LocalColorScheme provides nucleus, content = content)
    }
}
