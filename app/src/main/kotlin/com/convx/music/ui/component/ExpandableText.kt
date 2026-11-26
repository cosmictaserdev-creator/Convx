/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.convx.music.R

@Composable
fun ExpandableText(
    text: String = "",
    runs: List<LinkSegment>? = null,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var hasOverflow by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val linkColor = AppTheme.colorScheme.primary
    val bodyColor = com.convx.music.ui.ios.compat.LocalContentColor.current

    val annotatedText: AnnotatedString = remember(text, runs, linkColor) {
        if (runs.isNullOrEmpty()) {
            AnnotatedString(text)
        } else {
            buildAnnotatedString {
                runs.forEach { segment ->
                    val url = segment.url
                    if (url != null) {
                        pushStringAnnotation(tag = "URL", annotation = url)
                        withStyle(SpanStyle(color = linkColor)) {
                            append(segment.text)
                        }
                        pop()
                    } else {
                        append(segment.text)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier.animateContentSize()
    ) {
        @Suppress("DEPRECATION")
        ClickableText(
            text = annotatedText,
            style = AppTheme.typography.bodyMedium.copy(color = bodyColor),
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { textLayoutResult ->
                hasOverflow = textLayoutResult.hasVisualOverflow || textLayoutResult.lineCount > collapsedMaxLines
            },
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                        return@ClickableText
                    }
                if (hasOverflow) {
                    isExpanded = !isExpanded
                }
            }
        )

        if (hasOverflow) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.colorScheme.secondaryContainer)
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(if (isExpanded) R.string.show_less else R.string.show_more),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
