/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convxy.music.constants.AppBarHeight
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.theme.LocalAccentTextColor

/**
 * A screen's large title, rendered as the first item of its list or grid.
 *
 * Pairs with [CollapsedTitleBar]: the large title scrolls away with the
 * content while the compact bar fades in over it, which is the iOS/Apple Music
 * large-title behaviour. It is built this way rather than on `LargeTopAppBar`
 * because these screens put a [HeroBackground] behind the list — the artwork
 * has to scroll *with* the content, which a top-bar slot cannot do.
 *
 * Owns the status-bar clearance so screens stop repeating `Spacer(40.dp)`.
 */
@Composable
fun LargeScreenTitle(
    title: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
) {
    Spacer(Modifier.height(40.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.ExtraBold,
        color = color ?: LocalAccentTextColor.current,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .padding(horizontal = AppleTokens.Gutter, vertical = 24.dp),
    )
}

/**
 * How far the large title has scrolled away, 0..1. Drives [CollapsedTitleBar]'s
 * fade-in.
 *
 * Same shape as [rememberChromeScrimProgress], but keyed to the title's own
 * height rather than a hero image's, so the compact bar appears exactly as the
 * large one leaves.
 */
@Composable
fun rememberTitleCollapseProgress(state: LazyListState, thresholdPx: Float = 220f): Float {
    val progress by remember(state) {
        derivedStateOf {
            val scrolled = if (state.firstVisibleItemIndex == 0) {
                state.firstVisibleItemScrollOffset.toFloat()
            } else {
                thresholdPx
            }
            (scrolled / thresholdPx).coerceIn(0f, 1f)
        }
    }
    return progress
}

@Composable
fun rememberTitleCollapseProgress(state: LazyGridState, thresholdPx: Float = 220f): Float {
    val progress by remember(state) {
        derivedStateOf {
            val scrolled = if (state.firstVisibleItemIndex == 0) {
                state.firstVisibleItemScrollOffset.toFloat()
            } else {
                thresholdPx
            }
            (scrolled / thresholdPx).coerceIn(0f, 1f)
        }
    }
    return progress
}

/**
 * The compact bar that replaces [LargeScreenTitle] once it has scrolled off.
 * Overlay it on top of the list — it draws nothing at [progress] 0, so it costs
 * only its own layout while the large title is still showing.
 */
@Composable
fun CollapsedTitleBar(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(AppBarHeight)
            .graphicsLayer { alpha = progress },
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color ?: LocalAccentTextColor.current,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = AppBarHeight),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppleTokens.ItemGap / 2),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .padding(end = AppleTokens.Gutter),
            content = actions,
        )
    }
}
