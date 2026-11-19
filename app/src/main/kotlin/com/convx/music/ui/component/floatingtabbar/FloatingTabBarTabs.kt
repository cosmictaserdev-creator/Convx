/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.convx.music.ui.component.floatingtabbar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.convx.music.LocalAmbientBackgroundColor
import com.convx.music.ui.component.LocalGlassEffectConfig
import com.convx.music.ui.component.asPuckTint
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberCombinedBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.catalog.utils.DampedDragAnimation
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.backdrop.drawBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.lens
import com.convx.music.ui.component.backdrop.effects.vibrancy
import com.convx.music.ui.component.backdrop.highlight.Highlight
import com.convx.music.ui.component.backdrop.shadow.InnerShadow
import com.convx.music.ui.component.shapes.shadowShape
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.theme.isHueless
import kotlin.math.abs
import kotlin.math.sign
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
internal fun SharedTransitionScope.ExpandedTabs(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    tabPosition: (() -> Float?)?,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    colors: FloatingTabBarColors,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier,
    tabBarContentModifier: Modifier,
    backdrop: Backdrop?,
    accentColor: Color
) {
    // The standalone tab (e.g. search) never appears here — it's always its
    // own floating circle, rendered as a sibling by ExpandedBar (see
    // ExpandedStandaloneTab). Only the regular tabs form this pill/puck group.
    val allTabs = scope.tabs
    val tabsCount = allTabs.size
    if (tabsCount == 0) return
    // Keep the drag callback pointed at the latest tabs and click handlers.
    val currentTabs = rememberUpdatedState(allTabs)

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isLtr = layoutDirection == LayoutDirection.Ltr
    val animationScope = rememberCoroutineScope()
    val tabLabelAlpha = animatedVisibilityScope.labelAlpha()
    val glassConfig = LocalGlassEffectConfig.current
    // Puck wash. Explicit user color wins; on hero-art screens the pill takes
    // the mesh hue lighter than the surface with just a hint of tint; unset
    // elsewhere follows the app accent so the pill tracks whatever is playing.
    val ambientPuckColor = LocalAmbientBackgroundColor.current.value
    val themeDark = AppTheme.colorScheme.surface.luminance() <= 0.5f
    val puckWash = if (glassConfig.puckColor.isSpecified) {
        glassConfig.puckColor
    } else if (ambientPuckColor.isSpecified && !ambientPuckColor.isHueless()) {
        ambientPuckColor.asPuckTint(themeDark)
    } else {
        com.convx.music.ui.theme.GlassTintTokens.neutralTintColor(themeDark)
    }
    // Spec Q3/Q4: select pill .80 dark / .67 light at rest; hero keeps .80/.70.
    val puckRestAlpha = if (!glassConfig.puckColor.isSpecified) {
        // Rest opacity 0.90 (was .80 dark / .67 light) so the selected pill stays visible.
        0.90f
    } else {
        glassConfig.puckOpacity.coerceIn(0f, 1f)
    }

    val tabWidthPx = with(density) { sizes.tabWidth.toPx() }
    // The row's own content padding insets the tabs from the pill's edges, so
    // the puck (a sibling, not a row child) has to account for it explicitly —
    // otherwise it drifts out of alignment with where the tabs actually sit.
    val paddingStartPx = with(density) { sizes.tabBarContentPadding.calculateStartPadding(layoutDirection).toPx() }
    val paddingEndPx = with(density) { sizes.tabBarContentPadding.calculateEndPadding(layoutDirection).toPx() }
    val totalWidthPx = tabWidthPx * tabsCount + paddingStartPx + paddingEndPx

    // Ported from LiquidBottomTabs: the whole pill nudges a few dp in the drag
    // direction (eased, clamped) so it feels like it's being tugged, not just
    // the puck sliding inside a static shell.
    val offsetAnimation = remember(tabsCount) { Animatable(0f) }
    val panelOffset by remember(density, totalWidthPx) {
        derivedStateOf {
            val fraction = (offsetAnimation.value / totalWidthPx).fastCoerceIn(-1f, 1f)
            with(density) {
                4f.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
            }
        }
    }

    var currentIndex by remember(tabsCount) {
        mutableIntStateOf(
            allTabs.indexOfFirst { it.key == selectedTabKey }.coerceIn(0, tabsCount - 1)
        )
    }
    // Break the construction-time self-reference: canDrag needs the animation's current value.
    class DampedDragHolder {
        var instance: DampedDragAnimation? = null
    }
    val holder = remember { DampedDragHolder() }
    val dampedDragAnimation = remember(animationScope, tabsCount, density, isLtr) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = currentIndex.toFloat(),
            valueRange = 0f..(tabsCount - 1).toFloat(),
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 78f / 56f,
            canDrag = { offset ->
                val anim = holder.instance ?: return@DampedDragAnimation true
                if (tabWidthPx == 0f) return@DampedDragAnimation false
                val indicatorX = anim.value * tabWidthPx
                val pad = with(density) { 4.dp.toPx() }
                val globalTouchX = if (isLtr) {
                    pad + indicatorX + offset.x
                } else {
                    totalWidthPx - pad - tabWidthPx - indicatorX + offset.x
                }
                globalTouchX in 0f..totalWidthPx
            },
            onDragStarted = {},
            onDragStopped = {
                val targetIndex = targetValue.fastRoundToInt().coerceIn(0, tabsCount - 1)
                if (currentIndex != targetIndex) {
                    currentIndex = targetIndex
                    currentTabs.value.getOrNull(targetIndex)?.onClick?.invoke()
                } else {
                    animateToValue(targetIndex.toFloat())
                }
                animationScope.launch {
                    offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                }
            },
            onDrag = { _, dragAmount ->
                if (tabWidthPx > 0f) {
                    updateValue(
                        (targetValue + dragAmount.x / tabWidthPx * if (isLtr) 1f else -1f)
                            .fastCoerceIn(0f, (tabsCount - 1).toFloat())
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            },
        ).also { holder.instance = it }
    }
    // Selection changes also arrive from navigation. They only move the indicator;
    // user taps and completed drags invoke navigation explicitly.
    LaunchedEffect(dampedDragAnimation) {
        snapshotFlow { currentIndex }
            .drop(1)
            .collectLatest { index ->
                dampedDragAnimation.animateToValue(index.toFloat())
            }
    }

    // While an external pager is moving, follow its fractional position directly.
    if (tabPosition != null) {
        LaunchedEffect(dampedDragAnimation, tabPosition) {
            snapshotFlow { tabPosition() }.collect { position ->
                if (position != null) dampedDragAnimation.snapValue(position)
            }
        }
    }

    // External navigation updates the indicator without navigating again.
    LaunchedEffect(selectedTabKey, tabsCount) {
        val index = allTabs.indexOfFirst { it.key == selectedTabKey }
        if (index != -1 && currentIndex != index) currentIndex = index
    }

    val interactiveHighlight = remember(animationScope) {
        InteractiveHighlight(
            animationScope = animationScope,
            position = { size, offset ->
                Offset(
                    if (isLtr) paddingStartPx + (dampedDragAnimation.value + 0.5f) * tabWidthPx + panelOffset
                    else size.width - paddingStartPx - (dampedDragAnimation.value + 0.5f) * tabWidthPx + panelOffset,
                    size.height / 2f
                )
            }
        )
    }
    // Press-only glass: see the hidden row below. rememberLayerBackdrop is just a
    // holder; nothing records into it until that row is composed.
    val tabsBackdrop = rememberLayerBackdrop()
    val combinedBackdrop = if (backdrop != null) rememberCombinedBackdrop(backdrop, tabsBackdrop) else null
    val pressed by remember(dampedDragAnimation) {
        derivedStateOf { dampedDragAnimation.pressProgress > 0f }
    }

    val pillWidth = with(density) { totalWidthPx.toDp() }
    // Keep tab icons fixed-width while the shared pill shrinks, then clip the row
    // during the morph. This preserves icon positions and allows elastic effects at rest.
    val morphing = remember(animatedVisibilityScope) {
        derivedStateOf {
            animatedVisibilityScope.transition.currentState !=
                animatedVisibilityScope.transition.targetState
        }
    }
    Box(
        modifier
            .width(pillWidth)
            .graphicsLayer {
                shape = shapes.tabBarShape
                clip = morphing.value
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            Modifier
                .requiredWidth(pillWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = panelOffset
                    val width = size.width.coerceAtLeast(1f)
                    val pressScale = lerp(
                        1f,
                        1f + 16.dp.toPx() / width,
                        dampedDragAnimation.pressProgress,
                    )
                    scaleX = pressScale
                    scaleY = pressScale
                }
                // No path clip on the G2 capsule (clip = false, no .clip): clipping to a path
                // made Skia rasterize a coverage mask on the CPU and upload it every frame the
                // glass re-rendered. The fill, shadow and glass mask all follow the exact
                // outline without it; only the press sheen needs the shape, while pressed.
                .shadow(
                    shape = shapes.tabBarShape.shadowShape,
                    elevation = elevations.expandedElevation,
                    clip = false,
                )
                .background(
                    color = colors.backgroundColor,
                    shape = shapes.tabBarShape
                )
                .then(tabBarContentModifier)
                .clipWhilePressed(interactiveHighlight, shapes.tabBarShape)
                .then(interactiveHighlight.modifier)
                .padding(sizes.tabBarContentPadding),
            horizontalArrangement = Arrangement.spacedBy(sizes.tabSpacing)
        ) {
            allTabs.forEachIndexed { index, tab ->
                Tab(
                    icon = {
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    // Unselected tabs sit back; the selected one is
                                    // never dimmed, so the puck must not darken it.
                                    alpha = if (tab.key == selectedTabKey) 1f else 0.6f
                                    // Modulate alpha directly into the draw to avoid an offscreen buffer.
                                    compositingStrategy = CompositingStrategy.ModulateAlpha
                                }
                                // Only the selected icon has a matching shared element in the inline tab.
                                .then(
                                    if (tab.key == selectedTabKey) {
                                        Modifier.sharedElement(
                                            sharedContentState = rememberSharedContentState("navIcon"),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            boundsTransform = LocalBarBoundsTransforms.current.item,
                                            zIndexInOverlay = 1.5f,
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        ) {
                            tab.icon()
                        }
                    },
                    title = {
                        Box(
                            // Labels leave a frame ahead of the icons and arrive a
                            // frame behind them -- see labelAlpha().
                            modifier = Modifier.graphicsLayer {
                                alpha = tabLabelAlpha.value
                                compositingStrategy = CompositingStrategy.ModulateAlpha
                            }
                        ) {
                            tab.title()
                        }
                    },
                    isInline = false,
                    isStandalone = false,
                    contentScale = if (index == currentIndex) {
                        lerp(1f, 1.12f, dampedDragAnimation.pressProgress)
                    } else {
                        1f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .skipToLookaheadSize()
                        .clip(shapes.tabShape)
                        .tapClickable(
                            indication = tab.indication?.invoke(),
                            onClick = {
                                currentIndex = index
                                currentTabs.value.getOrNull(index)?.onClick?.invoke()
                            },
                            onLongClick = tab.onLongClick
                        )
                        .padding(sizes.tabExpandedContentPadding)
                )
            }
        }

        // Compose the sampled tab row only while pressed to avoid an idle lens pass.
        if (backdrop != null && pressed) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer { translationX = panelOffset }
                    // Before drawBackdrop so it insets this row's own bounds, exactly
                    // once, matching the visible row -- or the sampled icons drift.
                    .padding(sizes.tabBarContentPadding)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { shapes.tabBarShape },
                        effects = {
                            vibrancy()
                            blur(4f.dp.toPx())
                            lens(24f.dp.toPx(), 24f.dp.toPx())
                        },
                        highlight = null,
                        shadow = null,
                        onDrawSurface = { drawRect(colors.backgroundColor) },
                    )
                    .then(interactiveHighlight.modifier)
                    // After drawBackdrop: tints only the icons, not the glass sample.
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                horizontalArrangement = Arrangement.spacedBy(sizes.tabSpacing)
            ) {
                allTabs.forEachIndexed { index, tab ->
                    Tab(
                        icon = tab.icon,
                        title = tab.title,
                        isInline = false,
                        isStandalone = false,
                        // Must zoom in sync with the visible row or the copy ghosts.
                        contentScale = if (index == currentIndex) {
                            lerp(1f, 1.12f, dampedDragAnimation.pressProgress)
                        } else {
                            1f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .skipToLookaheadSize()
                            .padding(sizes.tabExpandedContentPadding)
                    )
                }
            }
        }

        // The draggable selection puck. Position and press/velocity-driven
        // scale come from dampedDragAnimation; tapping a tab still works via
        // each tab's own clickable — the puck only intercepts drags.
        Box(
            Modifier
                // Use the same inset on every side so the puck stays centered in the bar.
                .padding(sizes.tabBarContentPadding)
                .graphicsLayer {
                    translationX =
                        if (isLtr) dampedDragAnimation.value * tabWidthPx + panelOffset
                        else size.width - (dampedDragAnimation.value + 1f) * tabWidthPx + panelOffset
                }
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .width(sizes.tabWidth)
                .fillMaxHeight()
                .then(
                    if (combinedBackdrop != null && pressed) {
                        // the reference app's pressed pill: lens + dispersion over the page and the
                        // tinted tab copy, specular rim and inner shadow, all scaled by
                        // press progress so it grows out of the flat rest state below.
                        Modifier.drawBackdrop(
                            backdrop = combinedBackdrop,
                            // The bar's own shape family: puck and bar concentric.
                            shape = { shapes.tabBarShape },
                            effects = {
                                val progress = dampedDragAnimation.pressProgress
                                lens(
                                    refractionHeight = 10f.dp.toPx() * progress,
                                    refractionAmount = 14f.dp.toPx() * progress,
                                    depthEffect = true,
                                    chromaticAberration = true,
                                    chromaticAberrationAmount = 0.5f,
                                )
                            },
                            highlight = {
                                Highlight(width = 1.dp, alpha = dampedDragAnimation.pressProgress)
                            },
                            shadow = null,
                            innerShadow = {
                                val progress = dampedDragAnimation.pressProgress
                                if (progress > 0f) {
                                    InnerShadow(
                                        radius = 8f.dp * progress,
                                        color = Color.Black.copy(alpha = 0.15f),
                                        alpha = progress,
                                    )
                                } else {
                                    null
                                }
                            },
                            layerBlock = {
                                scaleX = dampedDragAnimation.scaleX
                                scaleY = dampedDragAnimation.scaleY
                                val velocity = dampedDragAnimation.velocity / 10f
                                scaleX /= 1f - (velocity * 0.75f).coerceIn(-0.2f, 0.2f)
                                scaleY *= 1f - (velocity * 0.25f).coerceIn(-0.2f, 0.2f)
                            },
                            onDrawSurface = {
                                val progress = dampedDragAnimation.pressProgress
                                drawRect(
                                    if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                    alpha = 1f - progress,
                                )
                                drawRect(Color.Black.copy(alpha = 0.03f * progress))
                            },
                        )
                    } else Modifier
                        .graphicsLayer {
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            // Apply velocity-based stretch to the puck outline.
                            val velocity = dampedDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).coerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).coerceIn(-0.2f, 0.2f)
                        }
                        // The puck overlaps tab icons, so keep its surface translucent while pressed.
                        .drawWithCache {
                            // Bar's shape family (continuous on the nav bar), drawn not clipped.
                            val outline = shapes.tabBarShape.createOutline(size, layoutDirection, this)
                            val wash = if (themeDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)
                            onDrawBehind {
                                val progress = dampedDragAnimation.pressProgress
                                drawOutline(outline, color = wash, alpha = 1f - progress)
                                drawOutline(outline, color = Color.Black.copy(alpha = 0.03f * progress))
                            }
                        }
                )
        )
    }
}

@Composable
internal fun Tab(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    isInline: Boolean,
    modifier: Modifier = Modifier,
    isStandalone: Boolean = false,
    // Vendored addition: press-zoom scale for the icon+label, driven by the
    // drag puck's press progress (Kyant's LocalLiquidBottomTabScale trick).
    contentScale: Float = 1f
) {
    val showTitle = !isStandalone && !isInline
    Column(
        // A real gap between icon and title, centered as a group — rather than
        // nudging the icon down with a raw offset(), which shifts its drawn
        // position without growing the Column's measured height and gets it
        // clipped by the tab bar's own bounds.
        verticalArrangement = if (showTitle) {
            // Keep the icon and label close while centering them as one group.
            Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
        } else {
            Arrangement.Center
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .then(
                if (contentScale != 1f) {
                    Modifier.graphicsLayer {
                        scaleX = contentScale
                        scaleY = contentScale
                    }
                } else {
                    Modifier
                }
            )
    ) {
        icon()
        if (showTitle) {
            title()
        }
    }
}

/**
 * The tab labels' own clock, on top of whatever the container fade is doing.
 *
 * Reference (m2-res_854p.mp4, 30fps): on collapse the labels are already gone on the
 * FIRST frame of the move, a good two frames before the unselected icons finish washing
 * out and long before the pill has visibly narrowed; on expand they are the last thing
 * to arrive, after the pill has reached full width. One fast tween each way reproduces
 * that ordering without adding a transform clock -- alpha only, on a single text draw.
 */
@Composable
internal fun AnimatedVisibilityScope.labelAlpha(): State<Float> =
    transition.animateFloat(
        transitionSpec = {
            if (targetState == EnterExitState.Visible) {
                tween(
                    durationMillis = 80,
                    delayMillis = (ExpandMs - 90).coerceAtLeast(0),
                    easing = LinearEasing,
                )
            } else {
                tween(durationMillis = 45, easing = LinearEasing)
            }
        },
        label = "labelAlpha",
    ) { if (it == EnterExitState.Visible) 1f else 0f }

/**
 * A custom modifier that provides smooth enter/exit animations with fade and blur effects.
 */
@Composable
internal fun Modifier.animateEnterExitTab(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
): Modifier = with(sharedTransitionScope) {
    with(animatedVisibilityScope) {
        val enterStartFraction = 0.5f
        val enterEndFraction = 0.8f
        val durationMs = 150

        val animatedAlpha by transition.animateFloat(
            transitionSpec = {
                keyframes {
                    durationMillis = durationMs
                    if (targetState == EnterExitState.Visible) {
                        0f atFraction enterStartFraction using FastOutSlowInEasing
                        1f atFraction enterEndFraction
                    }
                }
            }
        ) { targetState ->
            when (targetState) {
                EnterExitState.Visible -> 1f
                else -> 0f
            }
        }

        graphicsLayer {
            // Let the shared bounds animation handle movement; fade labels without a second transform.
            alpha = animatedAlpha
        }
    }
}
