/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.convxy.music.R

import androidx.compose.material3.LocalContentColor
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.LocalAccentTextColor
import androidx.compose.ui.graphics.Color

@Composable
fun NavigationTitle(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    thumbnail: (@Composable () -> Unit)? = null,
    color: Color? = null,
    onClick: (() -> Unit)? = null,
    onPlayAllClick: (() -> Unit)? = null,
) {
    // Headings take the accent-contrast colour rather than plain content colour.
    // Hero screens provide their own artwork tint into this local, so a section
    // title matches the screen it is on rather than the app-wide accent.
    val contentColor = color ?: LocalAccentTextColor.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .clickable(enabled = onClick != null) {
                onClick?.invoke()
            }
            .padding(horizontal = AppleTokens.Gutter, vertical = 12.dp)
    ) {
        thumbnail?.invoke()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                label?.let { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor.copy(alpha = 0.6f),
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = title,
                    fontSize = AppleTokens.SectionHeader,
                    lineHeight = AppleTokens.SectionHeaderLineHeight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.01).em,
                    color = contentColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }

        // "See All" / "Play all" as a plain accent text action rather than an
        // outlined button or a chevron: the design keeps every section header to
        // one weight of chrome, so the action reads as a link beside the title
        // instead of as a control stacked on it.
        //
        // Takes the same color as the title beside it. This was hardcoded to
        // AppleTokens.AccentRed, so it stayed red on every screen no matter what
        // the user picked in Theme settings — and on a hero screen it clashed with
        // the artwork tint the title next to it was already following.
        val action = onPlayAllClick ?: onClick
        if (action != null) {
            Text(
                text = stringResource(
                    if (onPlayAllClick != null) R.string.play_all else R.string.see_all
                ),
                fontSize = AppleTokens.ItemTitle,
                lineHeight = AppleTokens.ItemTitleLineHeight,
                fontWeight = FontWeight.Normal,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.clickable(onClick = action),
            )
        }
    }
}
