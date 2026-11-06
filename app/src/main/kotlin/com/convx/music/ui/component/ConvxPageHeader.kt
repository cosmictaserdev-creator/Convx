package com.convx.music.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.convx.music.ui.theme.BdConsoleFontFamily

val PageGutter = 20.dp

/** The strip the pinned profile button and the page wordmark share. */
val TabHeaderHeight = 44.dp

/**
 * The heading of a tab: the CONVX logo on Home. It is the first item of the page's list, so it
 * scrolls away with the content; only the profile button stays pinned above it.
 */
@Composable
fun ConvxPageHeader(
    color: Color,
    modifier: Modifier = Modifier,
    title: String = "CONVX",
    horizontalPadding: Dp = PageGutter,
    fontFamily: FontFamily = BdConsoleFontFamily,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 30.sp,
) {
    Box(
        modifier.fillMaxWidth().height(TabHeaderHeight).padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicText(
            text = title,
            maxLines = 1,
            modifier = Modifier.semantics { heading() },
            style = TextStyle(
                fontFamily = fontFamily,
                color = color,
                fontWeight = fontWeight,
                fontSize = fontSize,
                lineHeight = fontSize * 1.2f,
            ),
        )
    }
}
