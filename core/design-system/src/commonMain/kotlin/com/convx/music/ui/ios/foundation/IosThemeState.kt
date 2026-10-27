/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

val LocalIosColors = staticCompositionLocalOf { lightIosColors() }
val LocalIosTypography = staticCompositionLocalOf { iosTypography() }
val LocalIosControlSize = compositionLocalOf { IosControlSize.Regular }
val LocalIosTint = compositionLocalOf { Color.Unspecified }
val LocalIosTextStyle = compositionLocalOf { iosTypography().body }
val LocalIosContentColor = compositionLocalOf { Color.Unspecified }

object IosTheme {
    val colors: IosColors
        @Composable @ReadOnlyComposable get() = LocalIosColors.current

    val typography: IosTypography
        @Composable @ReadOnlyComposable get() = LocalIosTypography.current

    val controlSize: IosControlSize
        @Composable @ReadOnlyComposable get() = LocalIosControlSize.current

    val tint: Color
        @Composable @ReadOnlyComposable get() = LocalIosTint.current
            .takeIf { it != Color.Unspecified } ?: LocalIosColors.current.accent

    val contentColor: Color
        @Composable @ReadOnlyComposable get() = LocalIosContentColor.current
            .takeIf { it != Color.Unspecified } ?: LocalIosColors.current.label
}

@Composable
fun ProvideIosTheme(
    colors: IosColors,
    typography: IosTypography = iosTypography(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalIosColors provides colors,
        LocalIosTypography provides typography,
        LocalIosTextStyle provides typography.body,
        LocalIosContentColor provides colors.label,
        content = content,
    )
}

@Composable
fun IosControlSize(size: IosControlSize, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIosControlSize provides size, content = content)
}

@Composable
fun IosTint(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIosTint provides color, content = content)
}

@Composable
fun IosFont(style: TextStyle, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIosTextStyle provides style, content = content)
}

@Composable
fun IosForegroundStyle(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalIosContentColor provides color, content = content)
}
