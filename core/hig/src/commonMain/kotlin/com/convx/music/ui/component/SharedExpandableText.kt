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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.hig.resources.Res
import com.convx.music.hig.resources.show_less
import com.convx.music.hig.resources.show_more
import org.jetbrains.compose.resources.stringResource

data class LinkSegment(
    val text: String,
    val url: String? = null,
)

@Composable
fun SharedExpandableText(
    text: String = "",
    runs: List<LinkSegment>? = null,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3,
    showMore: String = stringResource(Res.string.show_more),
    showLess: String = stringResource(Res.string.show_less),
    bodyColor: Color = IosTheme.contentColor,
    bodyStyle: TextStyle = IosTheme.typography.callout,
    expandButton: (@Composable (label: String, onClick: () -> Unit) -> Unit)? = null,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var hasOverflow by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val linkColor = IosTheme.tint

    val annotatedText: AnnotatedString = remember(text, runs, linkColor) {
        if (runs.isNullOrEmpty()) {
            AnnotatedString(text)
        } else {
            buildAnnotatedString {
                runs.forEach { segment ->
                    if (segment.url != null) {
                        pushStringAnnotation(tag = "URL", annotation = segment.url)
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
            style = bodyStyle.copy(color = bodyColor),
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
            val label = if (isExpanded) showLess else showMore
            val toggle = { isExpanded = !isExpanded }
            if (expandButton != null) {
                expandButton(label, toggle)
            } else {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IosTheme.colors.secondarySystemFill)
                        .clickable(onClick = toggle)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    BasicText(
                        text = label,
                        style = IosTheme.typography.caption2.copy(color = IosTheme.colors.label, fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}
