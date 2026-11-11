/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package zone.ien.hig

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastRoundToInt
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.capsule.ContinuousCapsule
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.White
import zone.ien.hig.theme.isDark
import zone.ien.hig.theme.systemGray8
import zone.ien.hig.utils.DampedDragAnimation
import zone.ien.hig.utils.InteractiveHighlight
import zone.ien.hig.utils.rememberDefaultBackdrop

/**
 * Sliding segmented control
 *
 * @param selectedTabIndex index of the current selected tab
 * @param modifier control modifier
 * @param colors segmented control colors
 * @param shape shape of the segmented control and its indicator
 * @param backdrop external backdrop recorded from surrounding content
 * @param paddingValues outer paddings. Default values are equal to section paddings
 * @param indicator sliding indicator
 * @param tabs segmented control tabs. Usually [CupertinoSegmentedControlTab]
 *
 * @see CupertinoSegmentedControlTab
 * */
@Composable
@ExperimentalCupertinoApi
fun CupertinoSegmentedControl(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    colors: CupertinoSegmentedControlColors = CupertinoSegmentedControlDefaults.colors(),
    shape: Shape = CupertinoSegmentedControlDefaults.shape,
    backdrop: LayerBackdrop = rememberDefaultBackdrop(),
    paddingValues: PaddingValues = CupertinoSegmentedControlDefaults.PaddingValues,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        CupertinoSegmentedControlIndicator(
            selectedTabIndex = selectedTabIndex,
            tabPositions = tabPositions,
            color = colors.indicatorColor,
            shape = shape,
            separatorColor = colors.separatorColor,
            backdrop = backdrop,
        )
    },
    tabs: @Composable () -> Unit,
) {
    val registry = remember { SegmentedTabRegistry() }
    val tabCount = registry.tabs.size
    val animationScope = rememberCoroutineScope()
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    var dragSettlingIndex by remember { mutableStateOf<Int?>(null) }
    val didDrag = remember { mutableStateOf(false) }
    val dampedDragAnimation =
        if (tabCount > 0) {
            remember(animationScope, tabCount, isLtr) {
                DampedDragAnimation(
                    animationScope = animationScope,
                    initialValue = selectedTabIndex.toFloat(),
                    valueRange = 0f..(tabCount - 1).toFloat(),
                    visibilityThreshold = 0.001f,
                    initialScale = 1f,
                    pressedScale = 78f / 56f,
                    onDragStarted = {
                        didDrag.value = false
                    },
                    onDragStopped = {
                        if (didDrag.value) {
                            val targetIndex =
                                targetValue.fastRoundToInt().fastCoerceIn(0, tabCount - 1)
                            dragSettlingIndex = targetIndex
                            animateToValue(targetIndex.toFloat())
                            val token = registry.tabs.getOrNull(targetIndex)
                            if (token != null) {
                                registry.callbacks[token]?.invoke()
                            }
                        }
                    },
                    onDrag = { size, dragAmount ->
                        if (dragAmount != Offset.Zero) {
                            didDrag.value = true
                        }
                        val itemStep = size.width.toFloat() / tabCount
                        if (itemStep > 0f) {
                            updateValue(
                                (
                                    targetValue +
                                        dragAmount.x / itemStep * if (isLtr) 1f else -1f
                                ).fastCoerceIn(0f, (tabCount - 1).toFloat()),
                            )
                        }
                    },
                )
            }
        } else {
            null
        }
    val interactiveHighlight =
        dampedDragAnimation?.let { animation ->
            remember(animationScope, animation, tabCount, isLtr) {
                InteractiveHighlight(
                    animationScope = animationScope,
                    position = { size, _ ->
                        val itemWidth = size.width / tabCount
                        val center = itemWidth * (animation.value + 0.5f)
                        Offset(
                            if (isLtr) center else size.width - center,
                            size.height / 2f,
                        )
                    },
                )
            }
        }

    LaunchedEffect(selectedTabIndex, dampedDragAnimation) {
        val animation = dampedDragAnimation ?: return@LaunchedEffect
        if (dragSettlingIndex == selectedTabIndex) {
            dragSettlingIndex = null
        } else if (animation.targetValue != selectedTabIndex.toFloat()) {
            animation.animateToValue(selectedTabIndex.toFloat())
        }
    }

    CompositionLocalProvider(
        LocalContentColor provides colors.contentColor,
        LocalSelectedInteractionSource provides mutableStateOf(null),
        LocalSegmentedTabRegistry provides registry,
        LocalSegmentedDragAnimation provides dampedDragAnimation,
    ) {
        BoxWithConstraints(
            modifier =
                modifier
                    .padding(paddingValues)
                    .heightIn(min = CupertinoSegmentedControlTokens.MinHeight)
                    .then(interactiveHighlight?.gestureModifier ?: Modifier)
                    .then(dampedDragAnimation?.modifier ?: Modifier),
        ) {
            val itemWidth = if (tabCount > 0) maxWidth / tabCount else maxWidth
            val tabPositions =
                if (tabCount > 0) {
                    List(tabCount) { index -> TabPosition(itemWidth * index, itemWidth) }
                } else {
                    emptyList()
                }

            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(CupertinoSegmentedControlTokens.MinHeight)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = {
                                blur(8.dp.toPx())
                            },
                            highlight = null,
                            shadow = null,
                            onDrawSurface = {
                                drawRect(colors.containerColor)
                            },
                        ),
            )

            if (tabCount > 0 && dampedDragAnimation != null) {
                indicator(tabPositions)
            }

            CompositionLocalProvider(
                LocalSegmentedTabWidth provides itemWidth,
                LocalSegmentedOverlay provides false,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(CupertinoSegmentedControlTokens.MinHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    content = { tabs() },
                )
            }
        }
    }
}

/**
 * Sliding indicator of the [CupertinoSegmentedControl]
 *
 * @param selectedTabIndex index of the current selected tab
 * @param tabPositions positions of the [CupertinoSegmentedControl] tabs
 * @param shape indicator shape. Should be the same as [CupertinoSegmentedControl] shape.
 * @param color indicator color
 * @param backdrop external backdrop recorded from surrounding content
 * @param separatorColor color of the divider between tabs
 * */
@Composable
@ExperimentalCupertinoApi
fun CupertinoSegmentedControlIndicator(
    selectedTabIndex: Int,
    tabPositions: List<TabPosition>,
    modifier: Modifier = Modifier,
    shape: Shape = CupertinoSegmentedControlDefaults.shape,
    color: Color = CupertinoSegmentedControlDefaults.colors().indicatorColor,
    separatorColor: Color = CupertinoTheme.colorScheme.separator,
    backdrop: LayerBackdrop = rememberDefaultBackdrop(),
) {
    val isPressed = isTabSelectedAndPressed()
    val animationScope = rememberCoroutineScope()
    val sharedDragAnimation = LocalSegmentedDragAnimation.current
    val dampedDragAnimation =
        sharedDragAnimation ?: remember(animationScope, tabPositions.size) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedTabIndex.toFloat(),
                valueRange = 0f..tabPositions.lastIndex.coerceAtLeast(1).toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {},
                onDragStopped = {},
                onDrag = { _, _ -> },
            )
        }

    if (sharedDragAnimation == null) {
        LaunchedEffect(isPressed, dampedDragAnimation) {
            if (isPressed) {
                dampedDragAnimation.press()
            } else {
                dampedDragAnimation.release()
            }
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(CupertinoSegmentedControlTokens.MinHeight),
    ) {
        Spacer(
            modifier = Modifier
                .drawBehind {
                    tabPositions
                        .dropLast(1)
                        .fastForEach {
                            translate(
                                left = it.right.toPx(),
                                top = size.height * 0.2f,
                            ) {
                                drawLine(
                                    color = separatorColor,
                                    start = Offset.Zero,
                                    end = Offset(0f, size.height * 0.6f),
                                )
                            }
                        }
                }
                .cupertinoTabIndicatorOffset(
                    tabPositions = tabPositions,
                    selectedTabIndex = selectedTabIndex,
                    dragAnimation = sharedDragAnimation,
                ).padding(CupertinoSegmentedControlTokens.IndicatorPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .graphicsLayer {
                    clip = false
                    scaleX = dampedDragAnimation.scaleX
                    scaleY = dampedDragAnimation.scaleY
                    val velocity = dampedDragAnimation.velocity / 10f
                    scaleX /= 1f -
                        (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                    scaleY *= 1f -
                        (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    effects = {
                        val progress = dampedDragAnimation.pressProgress
                        lens(
                            refractionHeight = 10.dp.toPx() * progress,
                            refractionAmount = 14.dp.toPx() * progress,
                            chromaticAberration = true,
                        )
                    },
                    highlight = {
                        Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress)
                    },
                    shadow = {
                        Shadow(alpha = dampedDragAnimation.pressProgress)
                    },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(
                            radius = 8.dp * progress,
                            alpha = progress,
                        )
                    },
                    onDrawSurface = {
                        val progress = dampedDragAnimation.pressProgress
                        drawRect(
                            color,
                            alpha = 1f - progress,
                        )
                        drawRect(Color.Black.copy(alpha = 0.03f * progress))
                    },
                )
                .align(Alignment.CenterStart),
        )
    }
}

/**
 * Tab of the [CupertinoSegmentedControl]
 *
 * @param onClick tab click callback
 * @param modifier tab modifier
 * @param interactionSource tab interaction source
 * @param content tab content
 * */
@Composable
@ExperimentalCupertinoApi
fun CupertinoSegmentedControlTab(
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val isOverlay = LocalSegmentedOverlay.current
    val tabWidth = LocalSegmentedTabWidth.current
    val currentOnClick by rememberUpdatedState(onClick)
    val registry = LocalSegmentedTabRegistry.current
    val dragAnimation = LocalSegmentedDragAnimation.current
    val token = remember { Any() }
    val registeredIndex =
        if (isOverlay) {
            null
        } else {
            registry?.register(token, currentOnClick)
        }

    if (registry != null && !isOverlay) {
        DisposableEffect(registry, token, currentOnClick) {
            registry.register(token, currentOnClick)
            onDispose {
                registry.unregister(token)
            }
        }
    }

    val source = LocalSelectedInteractionSource.current

    LaunchedEffect(source, isSelected, isOverlay) {
        if (isSelected && !isOverlay) {
            source.value = interactionSource
        }
    }

    Box(
        modifier =
            modifier
                .width(tabWidth)
                .heightIn(min = CupertinoSegmentedControlTokens.MinHeight)
                .then(
                    if (isOverlay) {
                        Modifier.clearAndSetSemantics {}
                    } else {
                        Modifier.clickable(
                            onClick = {
                                val index = registry?.tabs?.indexOf(token)
                                if (index != null && index != -1) {
                                    dragAnimation?.animateToValue(index.toFloat())
                                }
                                currentOnClick()
                            },
                            indication = null,
                            interactionSource = interactionSource,
                            role = Role.Tab,
                        )
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        ProvideTextStyle(
            CupertinoTheme.typography.caption1.copy(
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Normal,
                textAlign = TextAlign.Center,
            ),
            content = content,
        )
    }
}

class CupertinoSegmentedControlColors internal constructor(
    val containerColor: Color,
    val contentColor: Color,
    val indicatorColor: Color,
    val separatorColor: Color,
)

internal object CupertinoSegmentedControlTokens {
    val MinHeight = 44.dp
    val IndicatorPadding = 4.dp
}

@Immutable
object CupertinoSegmentedControlDefaults {
    val PaddingValues: PaddingValues
        get() = CupertinoSectionDefaults.PaddingValues

    val shape: Shape
        @Composable
        @ReadOnlyComposable
        get() = ContinuousCapsule()

    @Composable
    @ReadOnlyComposable
    fun colors(
        containerColor: Color = CupertinoTheme.colorScheme.quaternarySystemFill,
        indicatorColor: Color =
            if (isDark()) {
                CupertinoColors.systemGray8(true)
            } else {
                CupertinoColors.White
            },
        contentColor: Color = CupertinoTheme.colorScheme.label,
        separatorColor: Color = Color.Transparent,
    ) = CupertinoSegmentedControlColors(
        containerColor = containerColor,
        contentColor = contentColor,
        indicatorColor = indicatorColor,
        separatorColor = separatorColor,
    )
}

private fun Modifier.cupertinoTabIndicatorOffset(
    tabPositions: List<TabPosition>,
    selectedTabIndex: Int,
    dragAnimation: DampedDragAnimation?,
): Modifier =
    composed(
        inspectorInfo =
            debugInspectorInfo {
                name = "tabIndicatorOffset"
                value = tabPositions[selectedTabIndex]
            },
    ) {
        if (tabPositions.isEmpty()) return@composed Modifier
        val safeSelectedIndex = selectedTabIndex.fastCoerceIn(0, tabPositions.lastIndex)
        val currentTabPosition = tabPositions[safeSelectedIndex]
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr

        fillMaxWidth()
            .wrapContentSize(Alignment.CenterStart)
            .width(currentTabPosition.width)
            .graphicsLayer {
                val index =
                    dragAnimation?.value
                        ?.fastCoerceIn(0f, tabPositions.lastIndex.toFloat())
                        ?: safeSelectedIndex.toFloat()
                val logicalLeft = currentTabPosition.width.toPx() * index
                translationX =
                    if (isLtr) {
                        logicalLeft
                    } else {
                        -logicalLeft
                    }
            }
    }

@Composable
private fun isTabSelectedAndPressed(): Boolean {
    val source = LocalSelectedInteractionSource.current.value ?: return false

    return source.collectIsPressedAsState().value
}

private val LocalSelectedInteractionSource =
    compositionLocalOf<MutableState<InteractionSource?>> {
        mutableStateOf(null)
    }

private class SegmentedTabRegistry {
    val tabs = mutableStateListOf<Any>()
    val callbacks = mutableMapOf<Any, () -> Unit>()

    fun register(token: Any, callback: () -> Unit): Int {
        if (token !in tabs) {
            tabs.add(token)
        }
        callbacks[token] = callback
        return tabs.indexOf(token)
    }

    fun unregister(token: Any) {
        tabs.remove(token)
        callbacks.remove(token)
    }
}

private val LocalSegmentedTabRegistry =
    compositionLocalOf<SegmentedTabRegistry?> { null }

private val LocalSegmentedDragAnimation =
    compositionLocalOf<DampedDragAnimation?> { null }

private val LocalSegmentedTabWidth =
    compositionLocalOf { 0.dp }

private val LocalSegmentedOverlay =
    compositionLocalOf { false }
