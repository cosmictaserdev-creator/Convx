/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.convx.music.ui.component.floatingtabbar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.elasticStretchLayerBlock
import com.convx.music.ui.component.shapes.shadowShape

@Composable
internal fun SharedTransitionScope.InlineBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    accessorySlots: AccessorySlots?,
    onInlineTabClick: () -> Unit,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    elevations: FloatingTabBarElevations,
    tabBarContentModifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val inlineTab = scope.getInlineTab(selectedTabKey)
    val standaloneTab = scope.standaloneTab
    val hasInlineTab = inlineTab != null

    // With an accessory the row spans the full width ([tab][accessory][standalone],
    // iOS 26 Apple Music style); without one, the cluster is packed and centered.
    Row(
        horizontalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (accessorySlots == null) Modifier.wrapContentWidth() else Modifier)
            .height(SearchBarRowHeight)
    ) {
        if (hasInlineTab) {
            InlineTab(
                inlineTab = inlineTab,
                onInlineTabClick = onInlineTabClick,
                shapes = shapes,
                sizes = sizes,
                colors = colors,
                elevations = elevations,
                animatedVisibilityScope = animatedVisibilityScope,
                tabBarContentModifier = tabBarContentModifier,
                // Pinned to the row height so the shrunk tab pill always
                // matches the mini + search pills at 48dp.
                // A true circle (row height square), like the search circle; the mini
                // player's weight(1f) takes whatever width is left between the two.
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .then(tabPillBounds(animatedVisibilityScope))
            )
        }

        if (accessorySlots != null) {
            // Empty slot: the accessory itself is drawn by AccessoryHost.
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(accessorySlot(accessorySlots, FloatingTabBarVisual.INLINE, animatedVisibilityScope))
            )
        }

        if (standaloneTab != null) {
            InlineStandaloneTab(
                standaloneTab = standaloneTab,
                shapes = shapes,
                sizes = sizes,
                colors = colors,
                elevations = elevations,
                animatedVisibilityScope = animatedVisibilityScope,
                tabBarContentModifier = tabBarContentModifier,
                // Keep the standalone circle at least as tall as the tab bar.
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .fillMaxHeight()
                    // Resolve the circle size from the bar height.
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
            )
        }
    }
}

@Composable
internal fun SharedTransitionScope.InlineTab(
    inlineTab: FloatingTabBarTab,
    onInlineTabClick: () -> Unit,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    colors: FloatingTabBarColors,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier,
    tabBarContentModifier: Modifier
) {
    // Same finger-tracking stretch as the standalone circle, so the shrunk
    // group tab distorts under the finger instead of sitting dead.
    val glowScope = rememberCoroutineScope()
    val glow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }

    Box(
        modifier = modifier
            // Outside sharedElement: draw-only, so bounds tracking is
            // unaffected, and no overlay clipping can cut the stretch.
            .graphicsLayer(elasticStretchLayerBlock(glow))
            .shadow(
                shape = shapes.tabBarShape.shadowShape,
                elevation = elevations.inlineElevation
            )
            .background(
                color = colors.backgroundColor,
                shape = shapes.tabBarShape
            )
            // No clip: the background above already draws the pill shape, and
            // any clip here would cut the outer elastic stretch back to bounds.
            .then(tabBarContentModifier)
            .then(glow.gestureModifier)
            .clipWhilePressed(glow, shapes.tabBarShape)
            .then(glow.modifier)
            .tapClickable(
                indication = inlineTab.indication?.invoke(),
                onLongClick = inlineTab.onLongClick,
                onClick = {
                    onInlineTabClick()
                    inlineTab.onClick()
                }
            )
            .padding(sizes.tabInlineContentPadding)
    ) {
        Tab(
            icon = {
                Box(
                    // Share icon bounds with the inline tab so the selected icon travels.
                    modifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState("navIcon"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = LocalBarBoundsTransforms.current.item,
                        // Just above its pill (z 1) -- at z 0 it was drawn UNDER its own pill's
                        // overlay copy and vanished mid-morph -- and under the mini (z 2), which
                        // it passes behind on its way down, as on iOS.
                        zIndexInOverlay = 1.5f,
                    ),
                ) { inlineTab.icon() }
            },
            title = { inlineTab.title() },
            isInline = true
        )
    }
}

@Composable
internal fun SharedTransitionScope.InlineStandaloneTab(
    standaloneTab: FloatingTabBarTab,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    colors: FloatingTabBarColors,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier,
    tabBarContentModifier: Modifier
) {
    // Same finger-tracking glow as the keyboard-active search pill
    // (NavBarSearchInputBar) — a soft radial light following the touch point.
    val glowScope = rememberCoroutineScope()
    val glow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }

    Tab(
        icon = standaloneTab.icon,
        title = standaloneTab.title,
        isInline = true,
        isStandalone = true,
        modifier = modifier
            // Let the draw-only stretch protrude beyond the bar without clipping.
            .graphicsLayer(elasticStretchLayerBlock(glow))
            .sharedElement(
                sharedContentState = rememberSharedContentState("standaloneTab"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = LocalBarBoundsTransforms.current.bar,
                zIndexInOverlay = 1f
            )
            .shadow(
                shape = shapes.standaloneTabShape,
                elevation = elevations.inlineElevation
            )
            .background(
                color = colors.backgroundColor,
                shape = shapes.standaloneTabShape
            )
            // No clip: the background above already draws the circle, and
            // any clip here would cut the outer elastic stretch back to bounds.
            .then(tabBarContentModifier)
            .then(glow.gestureModifier)
            .clipWhilePressed(glow, shapes.standaloneTabShape)
            .then(glow.modifier)
            .tapClickable(
                indication = standaloneTab.indication?.invoke(),
                onClick = standaloneTab.onClick,
                onLongClick = standaloneTab.onLongClick
            )
            // Unlike the regular tab pill (InlineTab, ExpandedTabs), this circle had
            // no inset at all — the icon sat flush against its edge.
            .padding(sizes.tabInlineContentPadding)
    )
}

@Composable
internal fun SharedTransitionScope.ExpandedBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    tabPosition: (() -> Float?)?,
    accessorySlots: AccessorySlots?,
    // Last width the tab row reported (held by the caller across remounts): seeding with it
    // keeps the accessory slot from spending its first frame at full width.
    initialTabRowWidthPx: Int?,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    elevations: FloatingTabBarElevations,
    tabBarContentModifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    backdrop: Backdrop?,
    accentColor: Color?,
    // Vendored addition: reports this row's own measured width upward so
    // SearchExpandedBar can hold the bar at the exact same overall width
    // instead of expanding to fill all available space in search mode.
    onWidthMeasured: ((Int) -> Unit)? = null,
) {
    val hasTabGroup = scope.tabs.isNotEmpty()
    val standaloneTab = scope.standaloneTab

    // Match the accessory width to the measured tab row.
    val density = LocalDensity.current
    var tabRowWidthPx by remember { mutableIntStateOf(initialTabRowWidthPx ?: 0) }

    // Keep Search as a separate circle in both collapsed and expanded layouts.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (accessorySlots != null) {
            Box(
                (if (tabRowWidthPx > 0) {
                    Modifier.width(with(density) { tabRowWidthPx.toDp() })
                } else {
                    Modifier.fillMaxWidth()
                })
                    .height(with(density) { accessorySlots.expandedHeightPx.toDp() })
                    .then(accessorySlot(accessorySlots, FloatingTabBarVisual.EXPANDED, animatedVisibilityScope))
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .onSizeChanged {
                    tabRowWidthPx = it.width
                    onWidthMeasured?.invoke(it.width)
                }
        ) {
            if (hasTabGroup) {
                ExpandedTabs(
                    scope = scope,
                    selectedTabKey = selectedTabKey,
                    tabPosition = tabPosition,
                    shapes = shapes,
                    sizes = sizes,
                    colors = colors,
                    elevations = elevations,
                    animatedVisibilityScope = animatedVisibilityScope,
                    tabBarContentModifier = tabBarContentModifier,
                    backdrop = backdrop,
                    accentColor = accentColor ?: colors.backgroundColor,
                    modifier = Modifier.then(tabPillBounds(animatedVisibilityScope))
                )
            }

            if (standaloneTab != null) {
                ExpandedStandaloneTab(
                    standaloneTab = standaloneTab,
                    shapes = shapes,
                    sizes = sizes,
                    colors = colors,
                    elevations = elevations,
                    animatedVisibilityScope = animatedVisibilityScope,
                    tabBarContentModifier = tabBarContentModifier,
                    modifier = Modifier
                        // Keep the circle at least as tall as the tab bar.
                        .defaultMinSize(minHeight = 48.dp)
                        .fillMaxHeight()
                        // Size the circle from the bar height.
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                )
            }
        }
    }
}

/**
 * Vendored addition: the search-active expanded shape. Same accessory-above
 * treatment as [ExpandedBar], but the row below it is the collapsed-style
 * single current-screen icon ([InlineTab], reused as-is) next to
 * [searchBarContent] filling the rest of the row — instead of the full
 * tab-group pill + circular standalone tab.
 */
@Composable
internal fun SharedTransitionScope.SearchExpandedBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    accessorySlots: AccessorySlots?,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    elevations: FloatingTabBarElevations,
    tabBarContentModifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    searchBarContent: @Composable (Modifier) -> Unit,
    // Reuse the expanded row width so search mode does not stretch the bar.
    targetWidthPx: Int?,
) {
    val inlineTab = scope.getInlineTab(selectedTabKey)
    val density = LocalDensity.current
    // Stretch tracker for the search pill itself. Only the layer + gesture
    // here, no glow draw — the placeholder inside paints its own light.
    val searchStretchScope = rememberCoroutineScope()
    val searchStretch = remember(searchStretchScope) { InteractiveHighlight(animationScope = searchStretchScope) }
    val targetWidthModifier = if (targetWidthPx != null && targetWidthPx > 0) {
        Modifier.width(with(density) { targetWidthPx.toDp() })
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (accessorySlots != null) {
            Box(
                targetWidthModifier
                    .height(with(density) { accessorySlots.expandedHeightPx.toDp() })
                    .then(accessorySlot(accessorySlots, FloatingTabBarVisual.SEARCH_EXPANDED, animatedVisibilityScope))
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
            verticalAlignment = Alignment.CenterVertically,
            // Fixed rather than IntrinsicSize.Max: matches the mini player's own
            // inline (shrunk) height exactly instead of letting a short
            // searchBarContent under-report its intrinsic height and shrink the row.
            modifier = Modifier.height(SearchBarRowHeight).then(targetWidthModifier)
        ) {
            if (inlineTab != null) {
                InlineTab(
                    inlineTab = inlineTab,
                    // Already expanded — a tap here should just navigate
                    // (inlineTab.onClick, fired inside InlineTab itself), not
                    // trigger the inline scroll-connection's expand().
                    onInlineTabClick = {},
                    shapes = shapes,
                    sizes = sizes,
                    colors = colors,
                    elevations = elevations,
                    animatedVisibilityScope = animatedVisibilityScope,
                    tabBarContentModifier = tabBarContentModifier,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            searchBarContent(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // Share the search tab's element key so the circle expands into this bar.
                    // Keep the draw-only stretch outside sharedElement so bounds stay stable.
                    .graphicsLayer(elasticStretchLayerBlock(searchStretch))
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("standaloneTab"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = LocalBarBoundsTransforms.current.bar,
                        zIndexInOverlay = 1f
                    )
                    .then(searchStretch.gestureModifier)
                    // clip = false: see ExpandedTabs -- no per-frame CPU path mask.
                    .shadow(shape = shapes.tabBarShape.shadowShape, elevation = elevations.expandedElevation, clip = false)
                    .background(color = colors.backgroundColor, shape = shapes.tabBarShape)
                    // No clip: the background above already draws the pill, and
                    // any clip here would cut the outer elastic stretch to bounds.
                    .then(tabBarContentModifier)
            )
        }
    }
}

@Composable
internal fun SharedTransitionScope.ExpandedStandaloneTab(
    standaloneTab: FloatingTabBarTab,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    colors: FloatingTabBarColors,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier,
    tabBarContentModifier: Modifier
) {
    // Same finger-tracking glow as the keyboard-active search pill
    // (NavBarSearchInputBar) — a soft radial light following the touch point.
    val glowScope = rememberCoroutineScope()
    val glow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }

    Tab(
        icon = standaloneTab.icon,
        title = standaloneTab.title,
        isInline = true,
        isStandalone = true,
        modifier = modifier
            // No outer inset: the circle holds the full row height so it is
            // exactly as tall as the nav bar (see InlineStandaloneTab).
            // Outside sharedElement: draw-only, so bounds tracking is
            // unaffected, and no overlay clipping can cut the stretch.
            .graphicsLayer(elasticStretchLayerBlock(glow))
            .sharedElement(
                sharedContentState = rememberSharedContentState("standaloneTab"),
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = LocalBarBoundsTransforms.current.bar,
                zIndexInOverlay = 1f
            )
            .shadow(
                shape = shapes.standaloneTabShape,
                elevation = elevations.expandedElevation
            )
            .background(
                color = colors.backgroundColor,
                shape = shapes.standaloneTabShape
            )
            // No clip: the background above already draws the circle, and
            // any clip here would cut the outer elastic stretch back to bounds.
            .then(tabBarContentModifier)
            .then(glow.gestureModifier)
            .clipWhilePressed(glow, shapes.standaloneTabShape)
            .then(glow.modifier)
            .tapClickable(
                indication = standaloneTab.indication?.invoke(),
                onClick = standaloneTab.onClick,
                onLongClick = standaloneTab.onLongClick
            )
            // Unlike the regular tab pill (ExpandedTabs), this circle had no inset
            // at all — the icon sat flush against its edge.
            .padding(sizes.tabExpandedContentPadding)
    )
}
