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

import androidx.compose.animation.core.Animatable as FloatAnimatable
import androidx.compose.animation.Animatable as ColorAnimatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawPlainBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.runtimeShaderEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import zone.ien.hig.section.CupertinoSectionDefaults
import zone.ien.hig.theme.CupertinoTheme
import zone.ien.hig.theme.darkColorScheme
import zone.ien.hig.theme.lightColorScheme
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

/** True while the container cannot scroll backward. */
inline val ScrollableState.isTopBarTransparent: Boolean
    get() = !canScrollBackward

/** True while the first visible item remains within [topPadding]. */
@Composable
@ExperimentalCupertinoApi
fun LazyListState.isTopBarTransparent(topPadding: Dp = 0.dp): Boolean {
    val topPaddingPx =
        LocalDensity.current.run {
            remember(topPadding) {
                topPadding.toPx()
            }
        }

    layoutInfo.visibleItemsInfo.first().offset
    return remember {
        derivedStateOf {
            !canScrollBackward ||
                firstVisibleItemIndex == 0 &&
                firstVisibleItemScrollOffset < topPaddingPx
        }
    }.value
}

/**
 * Top app bar itself does not produce cupertino thin material glass effect.
 * This effect works only inside [CupertinoScaffold], [CupertinoBottomSheetScaffold], [CupertinoBottomSheetContent].
 * Use this function to achieve this effect with custom top app bar.
 * It will communicate with scaffold and return either [Color.Transparent] if color was
 * successfully applied to scaffold (and top bar itself should be transparent) or passed color
 * if scaffold wasn't found.
 *
 * @param color top bar container color. Alpha is controlled by the [CupertinoScaffold]
 * @param isTransparent if top bar currently should be transparent. See [CupertinoTopAppBar]
 * for use cases example.
 * */
@Composable
@ExperimentalCupertinoApi
fun cupertinoTranslucentTopBarColor(
    color: Color,
    isTranslucent: Boolean,
    isTransparent: Boolean,
): Color {
    if (!isTranslucent) {
        return color
    }

    val appBarsState = LocalAppBarsState.current ?: return color

    DisposableEffect(appBarsState, color) {
        appBarsState.topBarColor.value = color
        onDispose {
            appBarsState.topBarColor.value = Color.Unspecified
        }
    }

    DisposableEffect(isTransparent, appBarsState) {
        appBarsState.isTopBarTransparent.value = isTransparent
        onDispose {
            appBarsState.isTopBarTransparent.value = true
        }
    }
    return color.copy(alpha = 0.0f)
}

/**
 * Top app bar with center aligned title
 *
 * @param title the title to be displayed at the center of the top app bar.
 * @param modifier the [Modifier] to be applied to this top app bar.
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 * typically be an [CupertinoIconButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 * [CupertinoIconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param windowInsets a window insets that app bar will respect.
 * @param isTransparent top bar is usually transparent if scroll container reached or almost reached top.
 * [ScrollableState.isTopBarTransparent] and [LazyListState.isTopBarTransparent] can be used to track it
 * @param isTranslucent works only inside [CupertinoScaffold]. Blurred content behind top bar will be
 * visible if top bar is translucent. Simulates iOS app bars material.
 * @param colors [CupertinoTopAppBarColors] that will be used to resolve the colors used for this top app
 * bar in different states. See [CupertinoTopAppBarDefaults.topAppBarColors].
 */
@Composable
@ExperimentalCupertinoApi
fun CupertinoTopAppBar(
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    windowInsets: WindowInsets = LocalTopAppBarInsets.current ?: CupertinoTopAppBarDefaults.windowInsets,
    isCenterAligned: Boolean = true,
    isBackgroundAdaptive: Boolean = true,
    isBackgroundGradient: Boolean = false,
    backdrop: LayerBackdrop = rememberLayerBackdrop(),
    colors: CupertinoTopAppBarColors = CupertinoTopAppBarDefaults.topAppBarColors(),
) {
    InlineTopAppBar(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        windowInsets = windowInsets,
        isCenterAligned = isCenterAligned,
        colors = colors,
        isBackgroundAdaptive = isBackgroundAdaptive,
        isBackgroundGradient = isBackgroundGradient,
        backdrop = backdrop
    )
}

internal val LocalNavigationTitleVisible = compositionLocalOf { mutableStateOf(false) }

private class ClipShape(
    private val offsetDifference: Float,
): Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline =
        Outline.Rectangle(
            Rect(
                top = offsetDifference.coerceAtMost(size.height),
                left = 0f,
                right = size.width,
                bottom = size.height,
            ),
        )
}

private const val NavTitleMaxFontScale = 1.1f
private val NavTitleMaxFontScaleDistance = 150.dp

/**
 * Navigation Title.
 *
 * Should be the first element in the first scrollable container inside
 * [CupertinoScaffold]/[CupertinoBottomSheetScaffold]/[CupertinoBottomSheetContent] body.
 * One container can have only one title.
 * Can behave unexpectedly when this precondition is violated.
 *
 * Navigation title will automatically manage [CupertinoTopAppBar] title visibility
 * and background transparency when usage precondition is fulfilled.
 *
 * @param modifier modifier of the title container
 * @param maxFontScale maximum font scale. Must be >= 1
 * @param maxFontScaleDistance distance of the scroll overflow at which [maxFontScale] is reached
 * @param paddingValues title padding values
 * @param title title content
 * */
@Composable
fun CupertinoNavigationTitle(
    modifier: Modifier = Modifier,
    maxFontScale: Float = NavTitleMaxFontScale,
    maxFontScaleDistance: Dp = NavTitleMaxFontScaleDistance,
    paddingValues: PaddingValues = CupertinoSectionDefaults.PaddingValues,
    subtitle: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    require(maxFontScale >= 1) {
        "maxFontScale must be >= 1."
    }
    var visible by LocalNavigationTitleVisible.current

    val density = LocalDensity.current
    val scaffoldCoordinates by LocalScaffoldCoordinates.current
    val topBarHeightPx = (LocalTopBarHeight.current.value ?: 0f)
    val topAppBarExists = topBarHeightPx > 0//Float.MIN_VALUE

    var offsetDifference by remember { mutableStateOf(0f) }
    var actualTopBarHeight by remember { mutableStateOf(0f) }

    val maxSizeIncreaseDistancePx = density.run { maxFontScaleDistance.toPx() }

    val insets = LocalScaffoldInsets.current?.getTop(density) ?: 0
    val fontIncrease by remember(maxSizeIncreaseDistancePx, maxFontScale) {
        derivedStateOf {
            val d = offsetDifference + topBarHeightPx - insets
            if (d >= 0) {
                1f
            } else {
                1f + (-d / maxSizeIncreaseDistancePx).coerceIn(0f, 1f) * (maxFontScale - 1)
            }
        }
    }

    val font = CupertinoTheme.typography.largeTitle.copy(fontWeight = FontWeight.Bold)
    val subtitleFont = CupertinoTheme.typography.subhead

    val titleAlpha by remember {
        derivedStateOf {
                if (!topAppBarExists) {
                    1f // TopBar does not exist, always visible
                } else {
                    val d = offsetDifference - actualTopBarHeight + 50
                    // If d is negative, it's in Large Title area → alpha 1
                    // If d is positive, it's entering TopBar → alpha 0
                    if (d <= 0) {
                        1f
                    } else {
                        // Fade smoothly to 0 over fadeDistance
                        val fadeDistance = 100f // Adjustable (recommended: dp → px conversion)
                        (1f - (d / fadeDistance)).coerceIn(0f, 1f)
                    }
                }
        }
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = titleAlpha,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "NavigationTitleAlpha"
    )

    Column(
        modifier
            .padding(paddingValues)
            .alpha(animatedAlpha)
            .onGloballyPositioned {
                actualTopBarHeight = it.size.height.toFloat()
                val scaffoldTop = (scaffoldCoordinates?.boundsInWindow()?.top ?: 0f)

                offsetDifference = (topBarHeightPx - it.boundsInWindow().top) + scaffoldTop

                visible = !topAppBarExists || offsetDifference < it.size.height
            },
    ) {
        ProvideTextStyle(value = font.copy(fontSize = font.fontSize * fontIncrease)) {
            title()
        }
        ProvideTextStyle(value = subtitleFont.copy(fontSize = subtitleFont.fontSize * fontIncrease)) {
            CompositionLocalProvider(
                LocalContentColor provides CupertinoTheme.colorScheme.secondaryLabel,
            ) {
                subtitle?.invoke()
            }
        }
    }
}

@Stable
class CupertinoTopAppBarColors internal constructor(
    private val lightGradientColor: Color,
    private val darkGradientColor: Color,
    internal val navigationIconContentColor: Color,
    internal val lightTitleContentColor: Color,
    internal val darkTitleContentColor: Color,
    internal val lightSubtitleContentColor: Color,
    internal val darkSubtitleContentColor: Color,
    internal val actionIconContentColor: Color,
) {
    @Composable
    fun gradientColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> {
        return rememberUpdatedState(if (isDark) darkGradientColor else lightGradientColor)
    }
    @Composable
    fun titleContentColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> {
        return rememberUpdatedState(if (isDark) darkTitleContentColor else lightTitleContentColor)
    }

    @Composable
    fun subtitleContentColor(isDark: Boolean = isSystemInDarkTheme()): State<Color> {
        return rememberUpdatedState(if (isDark) darkSubtitleContentColor else lightSubtitleContentColor)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CupertinoTopAppBarColors) return false

        if (lightGradientColor != other.lightGradientColor) return false
        if (darkGradientColor != other.darkGradientColor) return false
        if (navigationIconContentColor != other.navigationIconContentColor) return false
        if (lightTitleContentColor != other.lightTitleContentColor) return false
        if (darkTitleContentColor != other.darkTitleContentColor) return false
        if (lightSubtitleContentColor != other.lightSubtitleContentColor) return false
        if (darkSubtitleContentColor != other.darkSubtitleContentColor) return false
        if (actionIconContentColor != other.actionIconContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lightGradientColor.hashCode()
        result = 31 * result + darkGradientColor.hashCode()
        result = 31 * result + navigationIconContentColor.hashCode()
        result = 31 * result + lightTitleContentColor.hashCode()
        result = 31 * result + darkTitleContentColor.hashCode()
        result = 31 * result + darkSubtitleContentColor.hashCode()
        result = 31 * result + darkSubtitleContentColor.hashCode()
        result = 31 * result + actionIconContentColor.hashCode()

        return result
    }
}

internal val LocalTopAppBarInsets = compositionLocalOf<WindowInsets?> { null }

@ExperimentalCupertinoApi
@Composable
private fun InlineTopAppBar(
    title: @Composable () -> Unit,
    subtitle: @Composable (() -> Unit)? = null,
    modifier: Modifier,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    windowInsets: WindowInsets,
    isCenterAligned: Boolean,
    colors: CupertinoTopAppBarColors,
    isBackgroundAdaptive: Boolean,
    isBackgroundGradient: Boolean,
    backdrop: LayerBackdrop
) {
    val navTitleVisible by LocalNavigationTitleVisible.current
    val isLightTheme = !isSystemInDarkTheme()
    val layer = rememberGraphicsLayer()
    var titleX by remember { mutableStateOf(0) }
    var titleWidth by remember { mutableStateOf(0) }
    val topAppBarHeightPx = LocalDensity.current.run { TopAppBarHeight.toPx() }

    val lightGradientColor by colors.gradientColor(isDark = false)
    val darkGradientColor by colors.gradientColor(isDark = true)
    val lightTitleColor by colors.titleContentColor(isDark = false)
    val darkTitleColor by colors.titleContentColor(isDark = true)
    val lightSubtitleColor by colors.subtitleContentColor(isDark = false)
    val darkSubtitleColor by colors.subtitleContentColor(isDark = true)

    val luminanceAnimation = remember { FloatAnimatable(if (isLightTheme) 1f else 0f) }
    val gradientColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightGradientColor else darkGradientColor) }
    val titleColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightTitleColor else darkTitleColor) }
    val subtitleColorAnimation = remember { ColorAnimatable(if (isLightTheme) lightSubtitleColor else darkSubtitleColor) }

    if (isBackgroundAdaptive) {
        val defaultColor = CupertinoTheme.colorScheme.systemBackground

        LaunchedEffect(layer) {
            while (isActive) {
                if (layer.size != IntSize.Zero) {
                    try {
                        val averageLuminance = layer.toImageBitmap().averageLuminance(cropX = titleX, cropWidth = titleWidth.takeIf { it != 0 } ?: layer.size.width, sampleWidth = 5, defaultColor = defaultColor)

                        launch {
                            gradientColorAnimation.animateTo(
                                if (averageLuminance > 0.5f) lightGradientColor else darkGradientColor,
                                tween(300)
                            )
                            titleColorAnimation.animateTo(
                                if (averageLuminance > 0.5f) lightTitleColor else darkTitleColor,
                                tween(300)
                            )
                            subtitleColorAnimation.animateTo(
                                if (averageLuminance > 0.5f) lightSubtitleColor else darkSubtitleColor,
                                tween(300)
                            )
                        }
                        luminanceAnimation.animateTo(
                            averageLuminance.toFloat(),
                            tween(300)
                        )
                    } catch (e: RuntimeException) {
                        // When layer is disposed → exit loop
                        break
                    }
                }

                delay(300.milliseconds)
            }
        }
    }

    Box {
        Box(
            modifier = Modifier
                .drawPlainBackdrop(
                    backdrop = backdrop,
                    shape = { RectangleShape },
                    effects = {
                        blur(8.dp.toPx())
                        runtimeShaderEffect(
                            "AlphaMask",
                            """
    uniform shader content;

    uniform float2 size;
    layout(color) uniform half4 tint;
    uniform float tintIntensity;

    half4 main(float2 coord) {
        float blurAlpha = smoothstep(size.y, size.y * 0.5, coord.y);
        float tintAlpha = smoothstep(size.y, size.y * 0.5, coord.y);
        return mix(content.eval(coord) * blurAlpha, tint * tintAlpha, tintIntensity);
    }""",
                            "content"
                        ) {
                            setFloatUniform("size", size.width, size.height)
                            setColorUniform("tint", gradientColorAnimation.value)
                            setFloatUniform("tintIntensity", if (isBackgroundGradient) 0.8f else 0f)
                        }
                    },
                    onDrawBackdrop = { drawBackdrop ->
                        drawBackdrop()
                        layer.record { drawBackdrop() }
                    }
                )
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(TopAppBarHeight)
        )
        TopAppBarLayout(
            modifier = modifier.windowInsetsPadding(windowInsets),
            heightPx = topAppBarHeightPx,
            navigationIconContentColor = colors.navigationIconContentColor,
            actionIconContentColor = colors.actionIconContentColor,
            title = {
                val blurRadius by animateFloatAsState(
                    targetValue = if (!navTitleVisible) 0f else 4f,
                    animationSpec = tween(700)
                )

                AnimatedVisibility(
                    visible = !navTitleVisible,
                    enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it / 2 },
                    exit = fadeOut(tween(700)) + slideOutVertically(tween(700)) { it / 2 },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = if (isCenterAligned) Alignment.CenterHorizontally else Alignment.Start,
                        modifier = Modifier
                            .blur(
                                radius = blurRadius.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded,
                            )
                            .onGloballyPositioned {
                                val position = it.positionInRoot()
                                val size = it.size

                                titleX = position.x.roundToInt()
                                titleWidth = size.width
                            }
                    ) {
                        ProvideTextStyle(value =
                            if (subtitle == null) CupertinoTheme.typography.headline
                            else CupertinoTheme.typography.subhead.copy(fontWeight = FontWeight.Bold)
                        ) {
                            CompositionLocalProvider(
                                LocalContentColor provides titleColorAnimation.value,
                            ) {
                                title()
                            }
                        }
                        ProvideTextStyle(value = CupertinoTheme.typography.subhead) {
                            CompositionLocalProvider(
                                LocalContentColor provides subtitleColorAnimation.value,
                            ) {
                                subtitle?.invoke()
                            }
                        }
                    }
                }
            },
            titleVerticalArrangement = Arrangement.Center,
            titleHorizontalArrangement = if (isCenterAligned) Arrangement.Center else Arrangement.Start,
            titleBottomPadding = LocalDensity.current.run { 16.dp.roundToPx() },
            hideTitleSemantics = false,
            navigationIcon = navigationIcon,
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    content = actions,
                )
            },
        )
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float,
    navigationIconContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    Layout(
        {
            Box(
                Modifier.layoutId("navigationIcon"),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon,
                )
            }
            Box(
                modifier =
                    Modifier
                        .layoutId("title")
                        .padding(horizontal = TopAppBarHorizontalPadding)
                        .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier),
            ) {
                title()
            }
            Box(
                modifier =
                    Modifier
                        .layoutId("actionIcons")
                        .padding(end = TopAppBarHorizontalPadding),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions,
                )
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val navigationIconPlaceable = measurables
            .first { it.layoutId == "navigationIcon" }
            .measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable = measurables
            .first { it.layoutId == "actionIcons" }
            .measure(constraints.copy(minWidth = 0))

        val maxTitleWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                 val actionReservedWidth = actionIconsPlaceable.width + TopAppBarHorizontalPadding.roundToPx() * 2 // or fixed value 72.dp, etc.
                (constraints.maxWidth - navigationIconPlaceable.width - actionReservedWidth)
                    .coerceAtLeast(0)
            }

        val layoutHeight = heightPx.roundToInt()

        val titlePlaceable = measurables
            .first { it.layoutId == "title" }
            .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        // Locate the title's baseline.
        val titleBaseline =
            if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                titlePlaceable[LastBaseline]
            } else {
                0
            }

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = (layoutHeight - navigationIconPlaceable.height) / 2,
            )
            val safeMargin = 4.dp.roundToPx()
            titlePlaceable.placeRelative(
                x =
                    when (titleHorizontalArrangement) {
                        Arrangement.Center -> if (constraints.maxWidth / 2 - (TopAppBarHorizontalPadding.roundToPx() + actionIconsPlaceable.width + safeMargin) >= titlePlaceable.width / 2) {
                            (constraints.maxWidth - titlePlaceable.width) / 2
                        } else {
                            constraints.maxWidth - (TopAppBarHorizontalPadding.roundToPx() + actionIconsPlaceable.width + safeMargin) - titlePlaceable.width
                        }
                        Arrangement.End ->
                            constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
                        // Arrangement.Start.
                        // An TopAppBarTitleInset will make sure the title is offset in case the
                        // navigation icon is missing.
                        else -> max(TopAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
                    },
                y =
                    when (titleVerticalArrangement) {
                        Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                        // Apply bottom padding from the title's baseline only when the Arrangement is
                        // "Bottom".
                        Arrangement.Bottom ->
                            if (titleBottomPadding == 0) {
                                layoutHeight - titlePlaceable.height
                            } else {
                                layoutHeight - titlePlaceable.height -
                                    max(
                                        0,
                                        titleBottomPadding - titlePlaceable.height + titleBaseline,
                                    )
                            }
                        // Arrangement.Top
                        else -> 0
                    },
            )

            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2,
            )
        }
    }
}

private val TopAppBarHorizontalPadding = 4.dp
private val TopAppBarHeight = 44.dp

// Title offset for medium/large bars; also sizes the spacer when no navigation icon is shown.
private val TopAppBarTitleInset = 16.dp - TopAppBarHorizontalPadding

@Immutable
object CupertinoTopAppBarDefaults {
    /**
     * Default insets to be used and consumed by the top app bars
     */
    val windowInsets: WindowInsets @Composable get() = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

    /** Creates the colors used by [CupertinoTopAppBar].
     *
     * @param lightGradientColor background gradient in light mode
     * @param darkGradientColor background gradient in dark mode
     * @param navigationIconContentColor navigation icon color
     * @param lightTitleContentColor title color in light mode
     * @param darkTitleContentColor title color in dark mode
     * @param lightSubtitleContentColor subtitle color in light mode
     * @param darkSubtitleContentColor subtitle color in dark mode
     * @param actionIconContentColor action icon color
     */
    @Composable
    @ReadOnlyComposable
    fun topAppBarColors(
        lightGradientColor: Color = lightColorScheme().tertiarySystemBackground,
        darkGradientColor: Color = darkColorScheme().tertiarySystemBackground,
        navigationIconContentColor: Color = CupertinoTheme.colorScheme.accent,
        lightTitleContentColor: Color = lightColorScheme().label,
        darkTitleContentColor: Color = darkColorScheme().label,
        lightSubtitleContentColor: Color = lightColorScheme().secondaryLabel,
        darkSubtitleContentColor: Color = darkColorScheme().secondaryLabel,
        actionIconContentColor: Color = CupertinoTheme.colorScheme.accent,
    ): CupertinoTopAppBarColors =
        CupertinoTopAppBarColors(
            lightGradientColor,
            darkGradientColor,
            navigationIconContentColor,
            lightTitleContentColor,
            darkTitleContentColor,
            lightSubtitleContentColor,
            darkSubtitleContentColor,
            actionIconContentColor,
        )
}
