package com.convx.music.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Page gutter, card gap and shelf spacing shared by every client's tab screens. */
val PageGutter = 20.dp
val ShelfCardGap = 14.dp
val ShelfBottomGap = 26.dp
val ShelfCardCorner = 12.dp
val HeroCardCorner = 18.dp
val RowArtworkCorner = 7.dp
val RowArtworkSize = 48.dp

/** The strip the pinned profile button and the page wordmark share, below the status bar. */
val TabHeaderTop = 16.dp
val TabHeaderHeight = 44.dp

/** Where a tab's list starts: just under the status bar, level with the pinned profile button. */
@Composable
fun tabHeaderTopPadding(): Dp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + TabHeaderTop
