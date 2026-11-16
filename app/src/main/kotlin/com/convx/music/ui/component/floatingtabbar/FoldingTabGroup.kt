/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 *
 * Selector adapted from the Backdrop catalog's LiquidBottomTabs (Kyant0/AndroidLiquidGlass,
 * Apache-2.0). The group draws no shell: [FoldingTabBar] owns the surface.
 */

package com.convx.music.ui.component.floatingtabbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.convx.music.ui.component.backdrop.Backdrop
import com.convx.music.ui.component.backdrop.BackdropEffectScope
import com.convx.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convx.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convx.music.ui.component.backdrop.catalog.components.LocalLiquidBottomTabScale
import com.convx.music.ui.component.backdrop.catalog.utils.DampedDragAnimation
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.backdrop.drawBackdrop
import com.convx.music.ui.component.backdrop.effects.blur
import com.convx.music.ui.component.backdrop.effects.lens
import com.convx.music.ui.component.backdrop.effects.vibrancy
import com.convx.music.ui.component.backdrop.highlight.Highlight
import com.convx.music.ui.component.backdrop.shadow.InnerShadow
import com.convx.music.ui.component.backdrop.shadow.Shadow
import com.convx.music.ui.component.shapes.ContinuousCapsule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * The open bar's tabs with a draggable glass slot under the selected one. Press-and-drag
 * moves the slot; releasing selects the nearest tab, including the one already selected.
 */
@Composable
internal fun FoldingTabGroup(
    selectedTabIndex: () -> Int,
    onTabSelected: (Int) -> Unit,
    backdrop: Backdrop,
    surfaceBackdrop: Backdrop,
    tabsCount: Int,
    accentColor: Color,
    containerColor: Color,
    height: Dp,
    modifier: Modifier = Modifier,
    indicatorVisible: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val isLightTheme = !com.convx.music.ui.component.glassUsesDarkAppearance()
    val tabsBackdrop = rememberLayerBackdrop()
    val widthPx = remember { mutableFloatStateOf(0f) }
    val onSelected by rememberUpdatedState(onTabSelected)

    Box(
        modifier.onSizeChanged { widthPx.floatValue = it.width.toFloat() },
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val tabWidth: () -> Float = remember(density, tabsCount) {
            { with(density) { (widthPx.floatValue - 8.dp.toPx()) / tabsCount } }
        }

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density) {
            derivedStateOf {
                val width = widthPx.floatValue
                val fraction = if (width > 0f) (offsetAnimation.value / width).fastCoerceIn(-1f, 1f) else 0f
                with(density) { 4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction)) }
            }
        }

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()
        var currentIndex by remember { mutableIntStateOf(selectedTabIndex()) }
        val dampedDragAnimation = remember(animationScope, tabsCount) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedTabIndex().toFloat(),
                valueRange = 0f..(tabsCount - 1).coerceAtLeast(0).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {},
                onDragStopped = {
                    val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, tabsCount - 1)
                    // Releasing on the current tab still navigates: it is how a detail screen returns to its tab root.
                    if (targetIndex == currentIndex) onSelected(targetIndex)
                    currentIndex = targetIndex
                    animateToValue(targetIndex.toFloat())
                    animationScope.launch { offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f)) }
                },
                onDrag = { _, dragAmount ->
                    updateValue(
                        (targetValue + dragAmount.x / tabWidth() * if (isLtr) 1f else -1f)
                            .fastCoerceIn(0f, (tabsCount - 1).toFloat())
                    )
                    animationScope.launch { offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x) }
                },
            )
        }
        LaunchedEffect(Unit) {
            snapshotFlow { selectedTabIndex() }.collectLatest { currentIndex = it }
        }
        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { currentIndex }
                .drop(1)
                .collectLatest { index ->
                    dampedDragAnimation.animateToValue(index.toFloat())
                    if (index != selectedTabIndex()) onSelected(index)
                }
        }

        val interactiveHighlight = remember(animationScope) {
            InteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    Offset(
                        if (isLtr) (dampedDragAnimation.value + 0.5f) * tabWidth() + panelOffset
                        else size.width - (dampedDragAnimation.value + 0.5f) * tabWidth() + panelOffset,
                        size.height / 2f,
                    )
                },
            )
        }

        Row(
            Modifier
                .graphicsLayer { translationX = panelOffset }
                .clip(ContinuousCapsule)
                .then(interactiveHighlight.modifier)
                .height(height)
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )

        // The tabs again, tinted and invisible: what the slot shows the selected tab through.
        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides { lerp(1f, 1.2f, dampedDragAnimation.pressProgress) },
        ) {
            Row(
                Modifier
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer { translationX = panelOffset }
                    .height(height - 8.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }

        val indicatorAlpha by animateFloatAsState(
            targetValue = if (indicatorVisible) 1f else 0f,
            animationSpec = tween(220),
            label = "indicatorAlpha",
        )
        val slotPlacement = Modifier
            .padding(horizontal = 4.dp)
            .graphicsLayer {
                alpha = indicatorAlpha
                translationX =
                    if (isLtr) dampedDragAnimation.value * tabWidth() + panelOffset
                    else size.width - (dampedDragAnimation.value + 1f) * tabWidth() + panelOffset
            }
        val slotGrowth = Modifier.layout { measurable, constraints ->
            val velocity = dampedDragAnimation.velocity / 10f
            val scaleX = dampedDragAnimation.scaleX / (1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f))
            val scaleY = dampedDragAnimation.scaleY * (1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f))
            val width = constraints.maxWidth
            val slotHeight = constraints.maxHeight
            val grownWidth = (width * scaleX).roundToInt().coerceAtLeast(1)
            val grownHeight = (slotHeight * scaleY).roundToInt().coerceAtLeast(1)
            val child = measurable.measure(Constraints.fixed(grownWidth, grownHeight))
            layout(width, slotHeight) {
                child.placeRelative((width - grownWidth) / 2, (slotHeight - grownHeight) / 2)
            }
        }
        val slotLens: BackdropEffectScope.() -> Unit = {
            val progress = dampedDragAnimation.pressProgress
            lens(10.dp.toPx() * progress, 14.dp.toPx() * progress, chromaticAberration = true)
        }
        // While pressed the slot also refracts the bar's own surface, faded in so release doesn't pop.
        val pressedBackdrop = remember(backdrop, surfaceBackdrop, dampedDragAnimation) {
            object : Backdrop {
                private val surfacePaint = Paint()
                override val isCoordinatesDependent = true
                override fun DrawScope.drawBackdrop(
                    density: Density,
                    coordinates: LayoutCoordinates?,
                    layerBlock: (GraphicsLayerScope.() -> Unit)?,
                ) {
                    with(backdrop) { drawBackdrop(density, coordinates, layerBlock) }
                    val progress = dampedDragAnimation.pressProgress.coerceIn(0f, 1f)
                    if (progress > 0f) {
                        surfacePaint.alpha = progress
                        val canvas = drawContext.canvas
                        canvas.saveLayer(Rect(0f, 0f, size.width, size.height), surfacePaint)
                        try {
                            with(surfaceBackdrop) { drawBackdrop(density, coordinates, layerBlock) }
                        } finally {
                            canvas.restore()
                        }
                    }
                }
            }
        }

        Box(
            slotPlacement
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .height(height - 8.dp)
                .fillMaxWidth(1f / tabsCount)
                .then(slotGrowth)
                .drawBackdrop(
                    backdrop = pressedBackdrop,
                    shape = { ContinuousCapsule },
                    effects = {
                        vibrancy()
                        blur(8.dp.toPx() * (1f - dampedDragAnimation.pressProgress.coerceIn(0f, 1f)))
                        slotLens()
                    },
                    highlight = { Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress) },
                    shadow = { Shadow(alpha = dampedDragAnimation.pressProgress) },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(radius = 8.dp * progress, alpha = progress)
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(containerColor, alpha = 1f - progress)
                        drawRect(
                            if (isLightTheme) Color.Black.copy(0.1f) else Color.White.copy(0.1f),
                            alpha = 1f - progress,
                        )
                        // The pill is glass over whatever scrolls under the bar, so a busy or warm
                        // image (an orange cover under a red label) can swallow the accent ink.
                        // A dark veil keeps the backdrop calm enough for the label to read.
                        if (!isLightTheme) drawRect(Color.Black.copy(alpha = 0.34f * (1f - progress)))
                        drawRect(Color.Black.copy(alpha = 0.03f * progress))
                    },
                )
        )
        Box(
            slotPlacement
                .height(height - 8.dp)
                .fillMaxWidth(1f / tabsCount)
                .then(slotGrowth)
                .drawBackdrop(
                    backdrop = tabsBackdrop,
                    shape = { ContinuousCapsule },
                    effects = slotLens,
                    highlight = null,
                    shadow = null,
                )
        )
    }
}

/** One cell of [FoldingTabGroup]: icon over title, scaled with the slot while pressed. */
@Composable
internal fun RowScope.FoldingTabCell(
    onClick: () -> Unit,
    onLongClick: ((Rect) -> Unit)?,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    val scale = LocalLiquidBottomTabScale.current
    var bounds by remember { androidx.compose.runtime.mutableStateOf(Rect.Zero) }
    androidx.compose.foundation.layout.Column(
        Modifier
            .clip(ContinuousCapsule)
            .onGloballyPositioned { bounds = it.boundsInWindow() }
            .combinedClickable(
                interactionSource = null,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
                onLongClick = onLongClick?.let { { it(bounds) } },
            )
            .fillMaxHeight()
            .weight(1f)
            .graphicsLayer {
                val s = scale()
                scaleX = s
                scaleY = s
            },
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}
