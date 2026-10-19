/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component

import com.convx.music.ui.utils.MenuItemChrome
import com.convx.music.ui.utils.MenuIconChrome
import com.convx.music.ui.utils.additiveChrome
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convx.music.ui.component.shapes.ContinuousCapsule
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import com.convx.music.ui.ios.component.IosText
import com.convx.music.ui.ios.foundation.IosMetrics
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.LocalIosContentColor

/**
 * The action strip at the top of a menu sheet, drawn as SwiftUI's ControlGroup does it:
 * ONE capsule split into equal segments by hairlines, not a row of separate cards.
 */
@Composable
fun NewActionGrid(
    actions: List<NewAction>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
) {
    if (actions.isEmpty()) return
    val colors = IosTheme.colors
    // Inside the menu panel the quick actions sit bare on the panel (iOS 27 clip).
    val inPanel = LocalInMenuPanel.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columnCount = minOf(columns.coerceAtLeast(1), (maxWidth / 88.dp).toInt().coerceAtLeast(1))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            actions.chunked(columnCount).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().then(
                        if (inPanel) Modifier else Modifier.clip(ContinuousCapsule())
                            .background(colors.secondarySystemGroupedBackground),
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    row.forEachIndexed { index, action ->
                        if (index > 0 && !inPanel) {
                            Box(Modifier.width(IosMetrics.SeparatorThickness).height(36.dp).background(colors.separator))
                        }
                        ActionSegment(action, Modifier.weight(1f))
                    }
                    repeat(columnCount - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun ActionSegment(action: NewAction, modifier: Modifier) {
    val colors = IosTheme.colors
    val contentColor = when {
        action.contentColor.isSpecified -> action.contentColor
        LocalInMenuPanel.current -> com.convx.music.ui.ios.foundation.IosMenuMaterial.colors.label
        else -> IosTheme.tint
    }

    // NewAction.onClick is a @Composable lambda (several call sites open dialogs from
    // it), so it has to be invoked from composition rather than the click callback.
    var performAction by remember { mutableStateOf(false) }
    if (performAction) {
        action.onClick()
        LaunchedEffect(Unit) { performAction = false }
    }

    Column(
        modifier = modifier
            .clickable(enabled = action.enabled, interactionSource = null, indication = null) { performAction = true }
            .alpha(if (action.enabled) 1f else 0.4f)
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .semantics { role = Role.Button },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        CompositionLocalProvider(
            LocalIosContentColor provides contentColor,
            com.convx.music.ui.ios.compat.LocalContentColor provides contentColor,
        ) {
            CompositionLocalProvider(LocalSymbolChrome provides MenuItemChrome, LocalSymbolIconSize provides 24.dp) {
                Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { action.icon() }
            }
        }
        IosText(
            text = action.text,
            style = IosTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** One segment of a [NewActionGrid]. */
data class NewAction(
    val icon: @Composable () -> Unit,
    val text: String,
    val onClick: @Composable () -> Unit,
    val enabled: Boolean = true,
    val backgroundColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
)

/** A standalone tinted action tile: rounded rect, icon over label. */
@Composable
fun NewActionButton(
    icon: @Composable () -> Unit,
    text: String,
    onClick: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    val colors = IosTheme.colors
    val fill = if (backgroundColor.isSpecified) backgroundColor else colors.secondarySystemGroupedBackground
    val content = if (contentColor.isSpecified) contentColor else IosTheme.tint

    var performAction by remember { mutableStateOf(false) }
    if (performAction) {
        onClick()
        LaunchedEffect(Unit) { performAction = false }
    }

    Column(
        modifier = modifier
            .clip(ContinuousRoundedRectangle(18.dp))
            .background(fill)
            .clickable(enabled = enabled) { performAction = true }
            .alpha(if (enabled) 1f else 0.4f)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        CompositionLocalProvider(
            LocalIosContentColor provides content,
            com.convx.music.ui.ios.compat.LocalContentColor provides content,
        ) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { icon() }
        }
        IosText(
            text = text,
            style = IosTheme.typography.caption,
            color = content,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee(),
        )
    }
}

/** A single iOS list row, for the places that build rows outside an [IosMenuGroup]. */
@Composable
fun NewMenuItem(
    headlineContent: @Composable () -> Unit,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val colors = IosTheme.colors
    val contentColor = if (enabled) colors.label else colors.tertiaryLabel

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .defaultMinSize(minHeight = IosMetrics.ListRowHeight)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.let {
            CompositionLocalProvider(
                LocalIosContentColor provides contentColor,
                com.convx.music.ui.ios.compat.LocalContentColor provides contentColor,
            ) { it() }
            Spacer(Modifier.width(16.dp))
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            CompositionLocalProvider(
                LocalIosContentColor provides contentColor,
                com.convx.music.ui.ios.compat.LocalContentColor provides contentColor,
            ) { headlineContent() }
            supportingContent?.let {
                CompositionLocalProvider(
                    LocalIosContentColor provides colors.secondaryLabel,
                    com.convx.music.ui.ios.compat.LocalContentColor provides colors.secondaryLabel,
                ) { it() }
            }
        }

        trailingContent?.let {
            Spacer(Modifier.width(8.dp))
            CompositionLocalProvider(
                LocalIosContentColor provides colors.secondaryLabel,
                com.convx.music.ui.ios.compat.LocalContentColor provides colors.secondaryLabel,
            ) { it() }
        }
    }
}

/** The grouped-list section header: sentence case, secondary label, inset to the text. */
@Composable
fun NewMenuSectionHeader(text: String, modifier: Modifier = Modifier) {
    IosText(
        text = text,
        style = IosTheme.typography.subheadline,
        color = IosTheme.colors.secondaryLabel,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    )
}

@Composable
fun NewMenuContent(
    headerContent: @Composable (() -> Unit)? = null,
    actionGrid: @Composable (() -> Unit)? = null,
    menuItems: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        headerContent?.invoke()
        actionGrid?.invoke()
        menuItems?.invoke()
    }
}

/** A circular icon button on the iOS fill, no elevation. */
@Composable
fun NewIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
) {
    val colors = IosTheme.colors
    val fill = if (backgroundColor.isSpecified) backgroundColor else colors.secondarySystemFill
    val content = if (contentColor.isSpecified) contentColor else IosTheme.tint

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(ContinuousCapsule())
            .background(fill)
            .clickable(enabled = enabled) { onClick() }
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalIosContentColor provides content,
            com.convx.music.ui.ios.compat.LocalContentColor provides content,
        ) { icon() }
    }
}

@Composable
fun NewMenuContainer(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        content()
    }
}
