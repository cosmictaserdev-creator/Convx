/**
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

/**
 * Contains the implementation of Cupertino-style dropdown menus.
 */
package zone.ien.hig

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastSumBy
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.capsule.ContinuousRoundedRectangle
import zone.ien.hig.icons.CupertinoIcons
import zone.ien.hig.icons.outlined.Checkmark
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.section.CupertinoSectionTokens
import zone.ien.hig.section.SectionStyle
import zone.ien.hig.theme.BrightSeparatorColor
import zone.ien.hig.theme.CupertinoColors
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.systemGray5
import zone.ien.hig.utils.InteractiveHighlight
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.tanh

/** Scope for declaring dropdown menu items. */

sealed interface CupertinoMenuScope

/** Displays an anchored popup menu with optional backdrop and scroll state. */

@Composable
@ExperimentalCupertinoApi
fun CupertinoDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    paddingValues: PaddingValues = CupertinoDropdownMenuDefaults.PaddingValues,
    containerColor: Color = CupertinoDropdownMenuDefaults.ContainerColor,
    width: Dp = CupertinoDropdownMenuDefaults.DefaultWidth,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true, clippingEnabled = false),
    backdrop: Backdrop,
    content: @Composable CupertinoMenuScope.() -> Unit,
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded
    val safePadding = 32.dp

    if (expandedStates.currentState || expandedStates.targetState) {
        var transformOrigin by remember { mutableStateOf(TransformOrigin.Center) }
        val density = LocalDensity.current
        val popupPositionProvider = DropdownMenuPositionProvider(
            contentOffset = offset,
            safePadding = safePadding,
            verticalMargin = 0.dp,
            density = density
        ) { parentBounds, menuBounds ->
            transformOrigin = calculateTransformOrigin(parentBounds, menuBounds)
        }

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = properties,
        ) {
            val scope = remember { CupertinoMenuScopeImpl() }

            DropdownMenuContent(
                containerColor = containerColor,
                expandedStates = expandedStates,
                transformOriginState = transformOrigin,
                scrollState = scrollState,
                content = { scope.run { content() } },
                width = width,
                paddingValue = paddingValues,
                backdrop = backdrop,
                modifier = modifier.padding(safePadding)
            )
        }
    }
}

/** Adds a row with standard menu padding. */

@Composable
fun CupertinoMenuScope.MenuItem(
    modifier: Modifier = Modifier,
    minHeight: Dp = MinItemHeight,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
    this as CupertinoMenuScopeImpl

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.heightIn(minHeight)
    ) {
        content(
            MenuPaddingValues.let {
                if (!hasPicker) it
                else it.copy(
                    start = it.calculateStartPadding(LocalLayoutDirection.current) + SelectorSize,
                )
            },
        )
    }
}

/** Groups menu items under an optional title. */

@Composable
inline fun CupertinoMenuScope.MenuSection(
    noinline title: (@Composable () -> Unit)? = null,
    content: @Composable CupertinoMenuScope.() -> Unit,
) {
    if (title != null) MenuTitle(title = title)
    content()
}

/** Adds a section title to the menu. */

@Composable
fun CupertinoMenuScope.MenuTitle(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
) {
    MenuItem(modifier = modifier, minHeight = MinTitleHeight) {
        CompositionLocalProvider(
            LocalContentColor provides CupertinoTheme.colorScheme.secondaryLabel,
        ) {
            ProvideTextStyle(CupertinoTheme.typography.footnote) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 8.dp)
                ) { title() }
            }
        }
    }
}

/** Adds a clickable menu row. Leading icons share a reserved column. */

@Composable
fun CupertinoMenuScope.MenuAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    enabled: Boolean = true,
    contentColor: Color = CupertinoDropdownMenuDefaults.ContentColor,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    this as CupertinoMenuScopeImpl

    // Reserve the icon column for every row while any item has a leading icon.
    if (leadingIcon != null) {
        DisposableEffect(this) {
            val prev = hasIcon
            hasIcon = true
            onDispose { hasIcon = prev }
        }
    }

    ActionWithoutPadding(
        onClickLabel = onClickLabel,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        contentColor = contentColor,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    ) {
        Box(modifier = Modifier.padding(it)) { title() }
    }
}

/** Adds a selectable menu row with an optional selection icon. */

@Composable
fun CupertinoMenuScope.MenuPickerAction(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    enabled: Boolean = true,
    contentColor: Color = CupertinoDropdownMenuDefaults.ContentColor,
    selectionIcon: (@Composable () -> Unit) = { CupertinoDropdownMenuDefaults.PickerLeadingIcon() },
    leadingIcon: (@Composable () -> Unit) = {},
    trailingIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
) {
    this as CupertinoMenuScopeImpl

    DisposableEffect(this) {
        val prev = hasPicker
        hasPicker = true
        onDispose { hasPicker = prev }
    }

    ActionWithoutPadding(
        modifier = modifier.semantics(mergeDescendants = true) {
            selected = isSelected
            role = Role.DropdownList
        },
        onClickLabel = onClickLabel,
        onClick = onClick,
        enabled = enabled,
        contentColor = contentColor,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        title = { pv ->
            Box(contentAlignment = Alignment.CenterStart) {
                Box(
                    modifier = Modifier.size(MinItemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) selectionIcon()
                }
                Box(modifier = Modifier.padding(pv)) { title() }
            }
        },
    )
}

/** Adds a divider between menu rows. */

@Composable
fun CupertinoMenuScope.MenuDivider(
    modifier: Modifier = Modifier,
    color: Color? = null,
    height: Dp = DividerHeight,
) = MenuItem(minHeight = DividerHeight) {
    Spacer(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .padding(horizontal = MenuHorizontalMargin)
            .background(color ?: CupertinoDropdownMenuDefaults.DividerColor),
    )
}

/** Menu action without the default horizontal padding. */
@OptIn(ExperimentalCupertinoApi::class)
@Composable
private fun CupertinoMenuScope.ActionWithoutPadding(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLabel: String? = null,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    title: @Composable (PaddingValues) -> Unit,
) = MenuItem {
    this as CupertinoMenuScopeImpl

    val color = contentColor.takeOrElse { LocalContentColor.current }
        .let { if (enabled) it else it.copy(alpha = it.alpha / 4f) }

    ProvideTextStyle(CupertinoTheme.typography.callout) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SplitPadding),
            modifier = modifier
                .heightIn(min = CupertinoSectionTokens.MinHeight)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clip(ContinuousRoundedRectangle(24.dp))
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    onClickLabel = onClickLabel,
                    role = Role.DropdownList,
                )
        ) {
            CompositionLocalProvider(LocalContentColor provides color) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CupertinoSectionTokens.SplitPadding),
                    modifier = Modifier.padding(it)
                ) {
                    when {
                        // Has icon → display icon
                        leadingIcon != null -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(MinItemHeight / 2),
                            ) { leadingIcon() }
                        }
                        // No icon but other items have icons → reserve space
                        hasIcon -> {
                            Spacer(modifier = Modifier.size(MinItemHeight / 2))
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        title(it.copy(start = 0.dp))
                    }

                    trailingIcon?.let { icon ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(MinItemHeight / 2),
                        ) { icon() }
                    }
                }
            }
        }
    }
}

/** Default sizing, colors, and icons for dropdown menus. */

@Immutable
object CupertinoDropdownMenuDefaults {
    val DefaultWidth = 260.dp
    val SmallWidth = 160.dp
    val Elevation = 16.dp
    val PaddingValues = PaddingValues(0.dp)

    val Shape: Shape
        @Composable @ReadOnlyComposable
        get() = CupertinoSectionDefaults.shape(SectionStyle.InsetGrouped)

    val ContainerColor: Color
        @Composable @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.tertiarySystemBackground

    val ContentColor: Color
        @Composable @ReadOnlyComposable
        get() = CupertinoTheme.colorScheme.label

    val DividerColor: Color
        @Composable @ReadOnlyComposable
        get() = CupertinoColors.systemGray5

    @Composable
    fun PickerLeadingIcon() {
        CupertinoIcon(
            imageVector = CupertinoIcons.Default.Checkmark,
            modifier = Modifier.size(CupertinoIconDefaults.SmallSize),
            contentDescription = null,
        )
    }
}

/** Renders the popup menu content and its expansion animation. */

@Composable
private fun DropdownMenuContent(
    width: Dp,
    containerColor: Color,
    expandedStates: MutableTransitionState<Boolean>,
    transformOriginState: TransformOrigin,
    scrollState: ScrollState,
    paddingValue: PaddingValues,
    modifier: Modifier = Modifier,
    backdrop: Backdrop,
    content: @Composable () -> Unit,
) {
    val transition = rememberTransition(expandedStates, "DropDownMenu")
    val animationScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) MenuEnterTransition else MenuExitTransition
        },
    ) { if (it) 1f else .1f }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) MenuEnterTransition else MenuExitTransition
        },
    ) { if (it) 1f else 0f }

    val shape = CupertinoDropdownMenuDefaults.Shape
    val interactiveHighlight = remember(animationScope) { InteractiveHighlight(animationScope = animationScope) }

    CupertinoSurface(
        color = Color.Transparent,
        modifier = Modifier
            .padding(paddingValue)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                transformOrigin = transformOriginState
                clip = false
            }
            .widthIn(min = width)
    ) {
        CompositionLocalProvider(LocalSeparatorColor provides BrightSeparatorColor) {
            ProvideTextStyle(CupertinoTheme.typography.body) {
                SubcomposeLayout(
                    modifier = modifier
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = {
                                vibrancy()
                                blur(2.dp.toPx())
                                if (shape is ContinuousRoundedRectangle || shape is CornerBasedShape) {
                                    lens(12.dp.toPx(), 24.dp.toPx())
                                }
                            },
                            layerBlock = {
                                val w = this.size.width
                                val h = this.size.height
                                val progress = interactiveHighlight.pressProgress
                                val s = lerp(1f, 1f + 4.dp.toPx() / h, progress)
                                val maxOffset = this.size.minDimension
                                val initialDerivative = 0.05f
                                val offset = interactiveHighlight.offset

                                translationX = maxOffset * tanh(initialDerivative * offset.x / maxOffset)
                                translationY = maxOffset * tanh(initialDerivative * offset.y / maxOffset)

                                val maxDragScale = 4.dp.toPx() / h
                                val offsetAngle = atan2(offset.y, offset.x)

                                scaleX = s + maxDragScale * abs(cos(offsetAngle) * offset.x / this.size.maxDimension) * (w / h).fastCoerceAtMost(1f)
                                scaleY = s + maxDragScale * abs(sin(offsetAngle) * offset.y / this.size.maxDimension) * (h / w).fastCoerceAtMost(1f)
                            },
                            onDrawSurface = {
                                drawRect(containerColor.copy(alpha = 0.95f))
                            },
                        )
                        .padding(vertical = 8.dp)
                        .heightIn(max = MenuMaxHeight)
                        .verticalScroll(scrollState),
                ) { constraints ->
                    val minWidth = with(density) { width.roundToPx() }
                    val itemConstraints = constraints.copy(
                        minWidth = minWidth,
                        maxWidth = constraints.maxWidth
                    )
                    val itemPlaceables = subcompose(CupertinoDropdownMenuSlots.Item, content)
                        .fastMap { it.measure(itemConstraints) }
                    val allPlacements = buildList(itemPlaceables.size * 2) {
                        itemPlaceables.fastForEach { add(it) }
                    }

                    val layoutWidth = allPlacements.maxOfOrNull { it.width }
                        ?.coerceAtLeast(minWidth) ?: minWidth
                    val layoutHeight = allPlacements.fastSumBy { it.height }

                    layout(layoutWidth, layoutHeight) {
                        var y = 0
                        allPlacements.fastForEach {
                            it.placeRelative(0, y)
                            y += it.height
                        }
                    }
                }
            }
        }
    }
}

private enum class CupertinoDropdownMenuSlots { Section, Item, Separator }

/**
 * Calculates the transform origin for the dropdown menu animation.
 *
 * @param parentBounds Bounds of the parent element.
 * @param menuBounds Bounds of the menu.
 * @return The calculated transform origin.
 */
internal fun calculateTransformOrigin(parentBounds: IntRect, menuBounds: IntRect): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= parentBounds.right -> 0f
        menuBounds.right <= parentBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter = (max(parentBounds.left, menuBounds.left) + min(parentBounds.right, menuBounds.right)) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= parentBounds.bottom -> 0f
        menuBounds.bottom <= parentBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter = (max(parentBounds.top, menuBounds.top) + min(parentBounds.bottom, menuBounds.bottom)) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}

@Immutable
internal data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val safePadding: Dp = 0.dp,
    val verticalMargin: Dp = MenuVerticalMargin,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> },
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val verticalMargin = with(density) { verticalMargin.roundToPx() }
        val safePaddingPx = with(density) { safePadding.roundToPx() }
        val contentOffsetX = with(density) {
            contentOffset.x.roundToPx() * (if (layoutDirection == LayoutDirection.Ltr) 1 else -1)
        }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        val leftToAnchorLeft = anchorBounds.left + contentOffsetX - safePaddingPx
        val rightToAnchorRight = anchorBounds.right - popupContentSize.width + contentOffsetX + safePaddingPx
        val rightToWindowRight = windowSize.width - popupContentSize.width
        val leftToWindowLeft = 0

        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(leftToAnchorLeft, rightToAnchorRight, if (anchorBounds.left >= 0) rightToWindowRight else leftToWindowLeft)
        } else {
            sequenceOf(rightToAnchorRight, leftToAnchorLeft, if (anchorBounds.right <= windowSize.width) leftToWindowLeft else rightToWindowRight)
        }.firstOrNull {
            it >= -safePaddingPx && it + popupContentSize.width <= windowSize.width + safePaddingPx
        } ?: rightToAnchorRight

        val topToAnchorTop = anchorBounds.top + contentOffsetY - safePaddingPx
        val bottomToAnchorBottom = anchorBounds.bottom - popupContentSize.height + contentOffsetY + safePaddingPx
        val bottomToWindowBottom = windowSize.height - popupContentSize.height - verticalMargin + safePaddingPx

        val y = sequenceOf(topToAnchorTop, bottomToAnchorBottom, bottomToWindowBottom)
            .firstOrNull {
                it + popupContentSize.height - safePaddingPx <= windowSize.height - verticalMargin
            } ?: bottomToAnchorBottom

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height),
        )
        return IntOffset(x, y)
    }
}

internal class CupertinoMenuScopeImpl : CupertinoMenuScope {
    var hasPicker: Boolean by mutableStateOf(false)
    var hasIcon: Boolean by mutableStateOf(false)
}

private val MenuMaxHeight: Dp = 600.dp
private val SelectorSize = 20.dp
private val MenuHorizontalMargin = 24.dp
private val MenuVerticalMargin = 24.dp
private val MinItemHeight = 48.dp
private val DividerHeight = 1.dp
private val MinTitleHeight = 32.dp
private val SplitPadding = 16.dp
private val MenuPaddingValues = PaddingValues(16.dp, 8.dp)

private val MenuEnterTransition = spring<Float>(
    dampingRatio = .825f,
    stiffness = Spring.StiffnessMediumLow,
)

private val MenuExitTransition = tween<Float>(
    durationMillis = 350,
    easing = LinearOutSlowInEasing,
)
