/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.LocalIosTextStyle

/**
 * SwiftUI's `Text`. Inherits the ambient font and foreground style, so
 * `IosFont(...) { IosText("hi") }` behaves like `.font(_:)` does on iOS.
 */
@Composable
fun IosText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalIosTextStyle.current,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val resolved = if (color.isSpecified) color else IosTheme.contentColor
    BasicText(
        text = text,
        modifier = modifier,
        style = style.merge(color = resolved, textAlign = textAlign ?: TextAlign.Unspecified),
        maxLines = maxLines,
        overflow = overflow,
    )
}

/**
 * SwiftUI's `Image(systemName:)` stand-in: a tinted glyph that inherits the ambient
 * foreground style. Pass [contentDescription] = null for a purely decorative glyph.
 */
@Composable
fun IosIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    val resolved = if (tint.isSpecified) tint else IosTheme.contentColor
    val semantics = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.then(semantics),
        colorFilter = ColorFilter.tint(resolved),
    )
}

@Composable
fun IosIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) = IosIcon(rememberVectorPainter(imageVector), contentDescription, modifier, tint)
