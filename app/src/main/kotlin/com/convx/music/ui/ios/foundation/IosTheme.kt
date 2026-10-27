/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.backdrops.emptyBackdrop

/**
 * The backdrop glass samples. Null means no backdrop is wired up — glass then
 * degrades to a translucent fill rather than crashing.
 */
val LocalIosBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/** The nearest container's corner radius, for `ContainerRelativeShape`. */
val LocalIosContainerShape = compositionLocalOf<IosContainerShape?> { null }

@Composable
fun IosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = Color.Unspecified,
    typography: IosTypography = iosTypography(),
    backdrop: Backdrop? = LocalIosBackdrop.current,
    content: @Composable () -> Unit,
) {
    val colors = remember(darkTheme, accent) {
        when {
            darkTheme && accent != Color.Unspecified -> darkIosColors(accent)
            darkTheme -> darkIosColors()
            accent != Color.Unspecified -> lightIosColors(accent)
            else -> lightIosColors()
        }
    }
    CompositionLocalProvider(LocalIosBackdrop provides backdrop) {
        ProvideHigTheme(colors = colors, typography = typography) {
            ProvideIosTheme(colors = colors, typography = typography, content = content)
        }
    }
}

/** Declares the container radius children see through `ContainerRelativeShape`. */
@Composable
fun IosContainer(radius: Dp, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalIosContainerShape provides IosContainerShape(radius),
        content = content,
    )
}

@Composable
fun currentIosBackdrop(): Backdrop = LocalIosBackdrop.current ?: emptyBackdrop()
