/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.component.floatingtabbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.convx.music.ui.component.backdrop.backdrops.LayerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.elasticStretchLayerBlock
import kotlin.math.roundToInt

/**
 * The bottom bar as one layout folded by one number: the tab group shrinks onto the
 * selected tab, search detaches into its own circle, and the mini player slides into the
 * row between them. Nothing changes parent mid-fold, so there is no shared-element overlay
 * to flash and the mini player stays one node for the player morph.
 *
 * Search is the last entry of [content]'s tabs; while open it sits in the group, folded it
 * becomes the circle on the right, and in [searchMode] it widens into [searchBarContent].
 */
@Composable
fun FoldingTabBar(
    selectedTabKey: Any?,
    scrollConnection: FloatingTabBarScrollConnection,
    rowHeight: Dp,
    tabBarContentModifier: Modifier,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    elevations: FloatingTabBarElevations,
    expandedTabs: @Composable (tabs: List<FoldingTab>, surfaceBackdrop: LayerBackdrop) -> Unit,
    modifier: Modifier = Modifier,
    accessory: (@Composable (Modifier, isInline: Boolean) -> Unit)? = null,
    searchMode: Boolean = false,
    searchBarContent: (@Composable (Modifier) -> Unit)? = null,
    content: FloatingTabBarScope.() -> Unit,
) {
    // Rebuilt each composition so tab lambdas capture the current selection and colors.
    val scope = FloatingTabBarScopeImpl().apply { content() }
    val isInline = scrollConnection.isInline
    val inSearch = searchMode && searchBarContent != null

    val fold = remember { Animatable(if (isInline) 1f else 0f) }
    LaunchedEffect(isInline) {
        fold.animateTo(
            if (isInline) 1f else 0f,
            if (isInline) {
                tween(CollapseMorphMs, easing = FoldCollapseSpring)
            } else {
                tween(ExpandMorphMs, easing = BarMotion.BarSurfaceExpandSpring)
            },
        )
    }
    val search = remember { Animatable(if (inSearch) 1f else 0f) }
    LaunchedEffect(inSearch) {
        search.animateTo(
            if (inSearch) 1f else 0f,
            spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow),
        )
    }

    val selected = scope.getInlineTab(selectedTabKey)
    val searchTab = scope.tabs.lastOrNull()
    val selectedIndex = scope.tabs.indexOf(selected).coerceAtLeast(0)
    val tabsCount = scope.tabs.size.coerceAtLeast(1)
    val surfaceBackdrop = rememberLayerBackdrop()
    val foldedOrSearching = isInline || inSearch
    val noTap = remember { MutableInteractionSource() }
    val glowScope = rememberCoroutineScope()
    val selectedGlow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }
    val searchGlow = remember(glowScope) { InteractiveHighlight(animationScope = glowScope) }

    Layout(
        modifier = modifier.fillMaxWidth(),
        content = {
            // 0: the navigation surface.
            Box(
                Modifier
                    .graphicsLayer(elasticStretchLayerBlock(selectedGlow))
                    .layerBackdrop(surfaceBackdrop)
                    .background(colors.backgroundColor, shapes.tabBarShape)
                    .clip(shapes.tabBarShape)
                    .then(tabBarContentModifier)
                    .then(selectedGlow.modifier)
            )
            // 1: the full tab group at its resting width, clipped to the shrinking surface.
            Box(
                Modifier
                    .graphicsLayer {
                        alpha = (1f - fold.value.coerceIn(0f, 1f) * 3f).coerceIn(0f, 1f) * (1f - search.value.coerceIn(0f, 1f))
                    }
                    .drawWithContent {
                        val p = fold.value.coerceIn(0f, 1f)
                        val s = search.value.coerceIn(0f, 1f)
                        if (p == 0f && s == 0f) {
                            drawContent()
                        } else {
                            val width = lerp(lerp(size.width, size.height, s), size.height, p)
                            val outline = Path().apply {
                                addRoundRect(RoundRect(0f, 0f, width, size.height, CornerRadius(size.height / 2f)))
                            }
                            clipPath(outline) { this@drawWithContent.drawContent() }
                        }
                    }
                    .then(if (foldedOrSearching) Modifier.clearAndSetSemantics {} else Modifier)
            ) {
                expandedTabs(scope.tabs.map { FoldingTab(it.key, it.title, it.icon, it.onClick, it.onLongClick) }, surfaceBackdrop)
            }
            // 2: the selected tab alone, shown while folded or searching.
            Box(
                Modifier
                    .graphicsLayer(elasticStretchLayerBlock(selectedGlow))
                    .graphicsLayer { alpha = maxOf(fold.value, search.value).coerceIn(0f, 1f) }
                    .then(
                        if (foldedOrSearching) {
                            Modifier.then(selectedGlow.gestureModifier).clickable(noTap, null) {
                                if (isInline) scrollConnection.expand() else selected?.onClick?.invoke()
                            }
                        } else {
                            Modifier.clearAndSetSemantics {}
                        }
                    )
            ) {
                FoldingTabContent(
                    icon = { selected?.icon?.invoke() },
                    title = { selected?.title?.invoke() },
                    titleFraction = { (1f - fold.value.coerceIn(0f, 1f)) * (1f - search.value.coerceIn(0f, 1f)) },
                )
            }
            // 3: search's own glass, only while detached from the group.
            Box(
                Modifier
                    .graphicsLayer(elasticStretchLayerBlock(searchGlow))
                    .graphicsLayer { alpha = maxOf(fold.value, search.value).coerceIn(0f, 1f) }
                    .background(colors.backgroundColor, shapes.standaloneTabShape)
                    .clip(shapes.standaloneTabShape)
                    .then(tabBarContentModifier)
                    .then(searchGlow.modifier)
            )
            // 4: the detached search control.
            Box(
                Modifier
                    .graphicsLayer(elasticStretchLayerBlock(searchGlow))
                    .graphicsLayer {
                        val p = fold.value.coerceIn(0f, 1f)
                        val s = search.value.coerceIn(0f, 1f)
                        alpha = maxOf(p, s) * (1f - s * (1f - p))
                    }
                    .then(
                        if (foldedOrSearching && (!inSearch || isInline)) {
                            Modifier.then(searchGlow.gestureModifier).clickable(noTap, null) { searchTab?.onClick?.invoke() }
                        } else {
                            Modifier.clearAndSetSemantics {}
                        }
                    )
            ) {
                FoldingTabContent(
                    icon = { searchTab?.icon?.invoke() },
                    title = { searchTab?.title?.invoke() },
                    titleFraction = { 1f - fold.value.coerceIn(0f, 1f) },
                )
            }
            // 5: the search field, grown out of the circle.
            Box(
                Modifier
                    .graphicsLayer { alpha = (search.value * (1f - fold.value)).coerceIn(0f, 1f) }
                    .then(if (!inSearch || isInline) Modifier.clearAndSetSemantics {} else Modifier)
            ) {
                if (inSearch || search.value > 0f) {
                    searchBarContent?.invoke(Modifier.fillMaxSize())
                }
            }
            // 6: the mini player, one node for every fold state.
            Box {
                accessory?.invoke(
                    Modifier
                        .fillMaxWidth()
                        .pillShadow(shapes.accessoryShape, if (isInline) elevations.inlineElevation else elevations.expandedElevation)
                        .clip(shapes.accessoryShape)
                        .background(colors.accessoryBackgroundColor, shapes.accessoryShape),
                    isInline,
                )
            }
        },
    ) { children, constraints ->
        val width = constraints.maxWidth
        val expandedHeight = rowHeight.roundToPx()
        val compactHeight = if (accessory != null) InlineRowHeight.roundToPx() else expandedHeight
        val spacing = sizes.componentSpacing.roundToPx()
        val p = fold.value
        val openHeight = lerp(expandedHeight.toFloat(), SearchRowHeight.toPx(), search.value.coerceIn(0f, 1f))
        val height = lerp(openHeight, compactHeight.toFloat(), p).roundToInt().coerceAtLeast(1)
        val circle = height.coerceAtMost(width)
        val playerWidth = lerp(width.toFloat(), (width - 2 * (compactHeight + spacing)).coerceAtLeast(0).toFloat(), p)
            .roundToInt().coerceIn(0, width)
        val player = children[6].measure(Constraints(minWidth = playerWidth, maxWidth = playerWidth))
        val geometry = foldGeometry(
            width = width,
            expandedRowHeight = openHeight.roundToInt(),
            collapsedRowHeight = compactHeight,
            spacing = spacing,
            playerHeight = player.height,
            hasPlayer = accessory != null,
            fold = p,
            search = search.value,
            tabsCount = tabsCount,
            selectedIndex = selectedIndex,
            inset = 4.dp.toPx(),
        )
        val surface = children[0].measure(Constraints.fixed(geometry.navWidth, height))
        val tabs = children[1].measure(Constraints.fixed(width, height))
        val selectedButton = children[2].measure(Constraints.fixed(circle, height))
        val searchSurface = children[3].measure(Constraints.fixed(geometry.searchWidth, height))
        val searchControl = children[4].measure(Constraints.fixed(circle, height))
        val field = children[5].measure(Constraints.fixed(geometry.searchWidth, height))
        layout(width, geometry.height) {
            val rowY = geometry.rowY
            surface.placeRelative(0, rowY)
            tabs.placeRelative(0, rowY)
            selectedButton.placeRelative(geometry.selectedX, rowY)
            searchSurface.placeRelative(geometry.searchX, rowY)
            searchControl.placeRelative(geometry.searchX, rowY)
            field.placeRelative(geometry.searchX, rowY)
            player.placeRelative(geometry.playerX, geometry.playerY)
        }
    }
}

/** A tab as the expanded group draws it. */
class FoldingTab(
    val key: Any,
    val title: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val onLongClick: ((androidx.compose.ui.geometry.Rect) -> Unit)?,
)

private val InlineRowHeight = 48.dp
private val SearchRowHeight = 48.dp

/** Collapse lands with a deeper overshoot than the expand. */
private val FoldCollapseSpring = SpringEasing(zeta = 0.55f)
private val FoldingIconSize = 32.dp

@Composable
private fun FoldingTabContent(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    titleFraction: () -> Float,
) {
    Layout(content = {
        Box(Modifier.size(FoldingIconSize), contentAlignment = Alignment.Center) { icon() }
        Box(Modifier.graphicsLayer { alpha = titleFraction() }) { title() }
    }) { children, constraints ->
        val iconSize = FoldingIconSize.roundToPx()
        val iconPlaceable = children[0].measure(Constraints.fixed(iconSize, iconSize))
        val titlePlaceable = children[1].measure(Constraints(maxWidth = constraints.maxWidth))
        val gap = 2.dp.roundToPx()
        val fullTop = (constraints.maxHeight - iconPlaceable.height - gap - titlePlaceable.height) / 2f
        val top = lerp((constraints.maxHeight - iconPlaceable.height) / 2f, fullTop, titleFraction()).roundToInt()
        layout(constraints.maxWidth, constraints.maxHeight) {
            iconPlaceable.placeRelative((constraints.maxWidth - iconPlaceable.width) / 2, top)
            titlePlaceable.placeRelative((constraints.maxWidth - titlePlaceable.width) / 2, top + iconPlaceable.height + gap)
        }
    }
}
