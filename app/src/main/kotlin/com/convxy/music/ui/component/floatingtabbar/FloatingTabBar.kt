@file:OptIn(ExperimentalSharedTransitionApi::class)

/*
 * FloatingTabBar v1.0.1 by Elyes Mansour
 * https://github.com/elyesmansour/compose-floating-tab-bar
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Vendored from the v1.0.1 tag: the published AAR is compiled against
 * Compose 1.8 and crashes with NoSuchMethodError on
 * SharedTransitionScope.sharedElement under the Compose version this app uses.
 * Compiling the source here keeps it binary-compatible.
 *
 * Local modifications on top of v1.0.1:
 * - Package renamed.
 * - InlineBar/ExpandedBar use Row/Column instead of ConstraintLayout, and the
 *   tab pill + standalone tab cluster is centered (iOS 26 style) instead of
 *   being pinned to opposite screen edges.
 * - The inline/expanded AnimatedContent is center-aligned so the collapse
 *   animation morphs around the center instead of the start edge.
 * - ExpandedTabs grew a draggable selection puck ported from Kyant0's
 *   AndroidLiquidGlass catalog (LiquidBottomTabs/LiquidBottomTab): equal-width
 *   tabs, a spring-damped indicator you can press-drag between tabs (release
 *   snaps to and navigates the nearest tab), a finger-tracking glow, and —
 *   when an optional [Backdrop] is supplied — chromatic lens refraction and
 *   an accent-tinted glass sample of the selected tab's icon showing through
 *   the puck. See [DampedDragAnimation] and [InteractiveHighlight].
 */

package com.convxy.music.ui.component.floatingtabbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import com.convxy.music.ui.component.LocalGlassEffectConfig
import com.convxy.music.ui.component.glassResolutionScale
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.backdrops.layerBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberCombinedBackdrop
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.backdrop.catalog.utils.DampedDragAnimation
import com.convxy.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convxy.music.ui.component.backdrop.drawBackdrop
import com.convxy.music.ui.component.backdrop.effects.blur
import com.convxy.music.ui.component.backdrop.effects.lens
import com.convxy.music.ui.component.backdrop.effects.vibrancy
import com.convxy.music.ui.component.backdrop.highlight.Highlight
import com.convxy.music.ui.component.backdrop.shadow.InnerShadow
import com.convxy.music.ui.component.backdrop.shadow.Shadow
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign



// Tuning for the gooey merge/split pulse across the inline<->expanded
// crossfade — see GooeyTransition.kt. Not derived from fadeIn()/fadeOut()'s
// own duration; just a reasonable match for it.
private val GooeyPeakBlur = 12.dp
private const val GooeyDurationMs = 300

// Rim/shadow the selection puck keeps when it is NOT being pressed. Both used to
// be driven straight off pressProgress, i.e. zero at rest, which left the puck
// defined only by its own dark surface wash — invisible against a dark bar.
private const val PuckRestHighlightAlpha = 0.5f
private const val PuckRestShadowAlpha = 0.35f

// Vendored addition: the three shapes the bar can render. INLINE is the
// scroll-collapsed row (unchanged by search mode); EXPANDED is the normal
// all-tabs pill; SEARCH_EXPANDED replaces EXPANDED while search mode is
// active (see SearchExpandedBar).
private enum class FloatingTabBarVisual { INLINE, EXPANDED, SEARCH_EXPANDED }

// Matches the established inline-row height (InlineStandaloneTab's own
// defaultMinSize floor at the FloatingNavBar call site, and the inline mini
// player's own ~48dp content height) so the search-expanded row doesn't read
// shorter than the bar's own shrunk state.
private val SearchBarRowHeight = 48.dp

/**
 * A floating tab bar that transitions between inline and expanded states based on scroll behavior.
 *
 * @param selectedTabKey the key of the currently selected tab
 * @param scrollConnection the scroll connection that handles state transitions
 * @param modifier the modifier to be applied to the tab bar
 * @param colors the colors used by the tab bar components
 * @param shapes the shapes used by the tab bar components
 * @param sizes the sizes and spacing used by the tab bar components
 * @param elevations the elevation values used by the tab bar components
 * @param tabBarContentModifier modifier applied to the tab bar sections containing the grouped tabs and standalone tab.
 * It is applied after the default styling (background, shadow, clip) but before any content padding.
 * @param inlineAccessory the accessory composable that appears in inline state (e.g., compact media player)
 * @param expandedAccessory the accessory composable that appears in expanded state (e.g., full media player)
 * @param content the content defining the tabs
 */
@Composable
fun FloatingTabBar(
    selectedTabKey: Any?,
    scrollConnection: FloatingTabBarScrollConnection,
    modifier: Modifier = Modifier,
    tabBarContentModifier: Modifier = Modifier,
    inlineAccessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)? = null,
    expandedAccessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)? = null,
    colors: FloatingTabBarColors = FloatingTabBarDefaults.colors(),
    shapes: FloatingTabBarShapes = FloatingTabBarDefaults.shapes(),
    sizes: FloatingTabBarSizes = FloatingTabBarDefaults.sizes(),
    elevations: FloatingTabBarElevations = FloatingTabBarDefaults.elevations(),
    // Vendored addition: when non-null, the expanded selection puck samples this
    // backdrop for lens refraction and an accent-tinted glass preview of the
    // selected tab's icon. Null falls back to a flat colored puck.
    backdrop: Backdrop? = null,
    accentColor: Color? = null,
    // Vendored addition: when true (and searchBarContent non-null), the
    // expanded state renders SearchExpandedBar instead of ExpandedBar — the
    // tab group shrinks to the current screen's own icon and the standalone
    // slot expands to fill the rest of the row with searchBarContent. The
    // inline (scrolled) state is unaffected — it stays the normal collapsed
    // icon+accessory+standalone row regardless of searchMode.
    // Vendored addition: the puck's live position, when something outside the bar is
    // already animating the selection -- the main tabs pager. Returns the fractional
    // tab index while that driver is moving, and null the rest of the time so the
    // ordinary selectedTabKey settle takes back over.
    //
    // Without this the puck waited for the pager to FINISH: `selectedTabKey` derives
    // from `pagerState.currentPage`, which only flips near the end of
    // `animateScrollToPage`, and only then did the puck start its own spring. Two
    // springs in series, so the puck arrived roughly a full animation after the page
    // it marks -- the "selected icon puck comes after a second" report.
    tabPosition: (() -> Float?)? = null,
    searchMode: Boolean = false,
    searchBarContent: (@Composable (Modifier) -> Unit)? = null,
    // Vendored addition: caller-held width (from a previous onExpandedWidthChanged
    // report) that SearchExpandedBar holds its row to, so the bar doesn't visibly
    // widen to fill available space when entering search mode. Null/0 falls back
    // to filling available space (e.g. first-ever composition in search mode).
    expandedContentWidthPx: Int? = null,
    onExpandedWidthChanged: ((Int) -> Unit)? = null,
    content: FloatingTabBarScope.() -> Unit
) {
    // Rebuilt every composition, deliberately not remembered. The tabs hold
    // @Composable lambdas that close over selection state and colours; caching
    // them behind a key meant every value they captured had to be listed in that
    // key by hand, and anything forgotten showed up as a stale icon or colour on
    // one navigation path only. Re-running the builder is a handful of
    // allocations for a few tabs, and it makes that whole class of bug
    // unrepresentable.
    val scope = FloatingTabBarScopeImpl().apply { content() }

    val isAccessoryShared = inlineAccessory != null && expandedAccessory != null

    val visual = when {
        scrollConnection.isInline -> FloatingTabBarVisual.INLINE
        searchMode && searchBarContent != null -> FloatingTabBarVisual.SEARCH_EXPANDED
        else -> FloatingTabBarVisual.EXPANDED
    }

    // updateTransition (rather than the sugared top-level AnimatedContent) so
    // the transition object is available here to drive the gooey blur pulse,
    // not just inside the content lambda.
    val transition = updateTransition(targetState = visual, label = "floatingTabBarVisual")
    val gooeyBlurPx = with(LocalDensity.current) { GooeyPeakBlur.toPx() }
    // Liquid "gooey" merge/split (see GooeyTransition.kt): peaks mid-crossfade
    // and returns to 0 at rest, on every transition regardless of direction —
    // the keyframes spec defines the peak explicitly rather than deriving it
    // from a start/end value, since the steady-state target is always 0.
    val gooeyProgress by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = GooeyDurationMs
                0f at 0
                1f atFraction 0.5f using FastOutSlowInEasing
                0f at GooeyDurationMs
            }
        },
        label = "gooeyProgress"
    ) { _ -> 0f }

    SharedTransitionLayout(modifier = modifier) {
        // Wrapping AnimatedContent itself (not SharedTransitionLayout's own
        // modifier) so the offscreen gooey capture stays under
        // SharedTransitionLayout's own overlay mechanism — sharedElement-
        // tracked content (like the search tab's shared "standaloneTab"
        // bounds) renders via that overlay, and capturing it into the gooey
        // layer at the wrong level made it visibly pop in a frame late when
        // re-expanding. This way the overlay draws normally, on top.
        // Held as a lambda rather than a Boolean so flipping it costs no
        // recomposition — the glass surfaces call it during draw. See
        // LocalTabBarBackdropFrozen.
        val frozenWhileAnimating = remember(transition) {
            { transition.currentState != transition.targetState }
        }
        CompositionLocalProvider(LocalTabBarBackdropFrozen provides frozenWhileAnimating) {
            Box {
                transition.AnimatedContent(
                    transitionSpec = {
                        fadeIn(tween(220, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(tween(180, easing = FastOutSlowInEasing))
                    },
                    contentAlignment = Alignment.BottomCenter
                ) { targetVisual ->
            when (targetVisual) {
                FloatingTabBarVisual.INLINE -> InlineBar(
                    scope = scope,
                    selectedTabKey = selectedTabKey,
                    accessory = inlineAccessory,
                    isAccessoryShared = isAccessoryShared,
                    onInlineTabClick = { scrollConnection.expand() },
                    colors = colors,
                    shapes = shapes,
                    sizes = sizes,
                    elevations = elevations,
                    tabBarContentModifier = tabBarContentModifier,
                    animatedVisibilityScope = this@AnimatedContent
                )
                FloatingTabBarVisual.EXPANDED -> ExpandedBar(
                    scope = scope,
                    selectedTabKey = selectedTabKey,
                    tabPosition = tabPosition,
                    accessory = expandedAccessory,
                    isAccessoryShared = isAccessoryShared,
                    colors = colors,
                    shapes = shapes,
                    sizes = sizes,
                    elevations = elevations,
                    tabBarContentModifier = tabBarContentModifier,
                    animatedVisibilityScope = this@AnimatedContent,
                    backdrop = backdrop,
                    accentColor = accentColor,
                    onWidthMeasured = onExpandedWidthChanged
                )
                FloatingTabBarVisual.SEARCH_EXPANDED -> SearchExpandedBar(
                    scope = scope,
                    selectedTabKey = selectedTabKey,
                    accessory = expandedAccessory,
                    isAccessoryShared = isAccessoryShared,
                    colors = colors,
                    shapes = shapes,
                    sizes = sizes,
                    elevations = elevations,
                    tabBarContentModifier = tabBarContentModifier,
                    animatedVisibilityScope = this@AnimatedContent,
                    searchBarContent = searchBarContent ?: {},
                    targetWidthPx = expandedContentWidthPx
                )
            }
            }
        }
        }
    }
}

/**
 * True for exactly the frames of a tab bar transition (inline <-> expanded <->
 * search). The bar's glass surfaces pass it to `drawBackdrop`'s `frozen`, which
 * makes them reuse their last backdrop capture instead of re-recording the whole
 * screen every frame while their bounds animate.
 *
 * A lambda, not a Boolean, so flipping it triggers no recomposition — the surfaces
 * call it during draw. Static local for the same reason: the lambda's identity is
 * stable, only what it reads changes.
 */
internal val LocalTabBarBackdropFrozen = staticCompositionLocalOf<() -> Boolean> { { false } }

/**
 * A [NestedScrollConnection] that handles scroll events to transition between inline and expanded states.
 *
 * @param initialIsInline Initial state of the tab bar (inline or expanded).
 * @param scrollThresholdPx The minimum scroll distance in pixels required to collapse to inline.
 * @param expandThresholdPx The minimum scroll distance in pixels required to expand back. Kept
 * well below [scrollThresholdPx] so the bar returns as soon as the user scrolls up.
 * @param inlineBehavior Defines when the tab bar should transition to inline state.
 */
class FloatingTabBarScrollConnection(
    initialIsInline: Boolean = false,
    private val scrollThresholdPx: Float,
    private val expandThresholdPx: Float = scrollThresholdPx,
    private val inlineBehavior: FloatingTabBarInlineBehavior = FloatingTabBarInlineBehavior.OnScrollDown
) : NestedScrollConnection {
    var isInline by mutableStateOf(initialIsInline)
        private set

    private var accumulatedScroll = 0f

    fun expand() {
        isInline = false
        accumulatedScroll = 0f
    }

    fun inline() {
        isInline = true
        accumulatedScroll = 0f
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // If behavior is Never, don't change state
        if (inlineBehavior == FloatingTabBarInlineBehavior.Never) {
            return Offset.Zero
        }

        val scrollDelta = available.y

        // Reset accumulated scroll if changing direction
        if ((accumulatedScroll > 0 && scrollDelta < 0) || (accumulatedScroll < 0 && scrollDelta > 0)) {
            accumulatedScroll = 0f
        }

        // Accumulate scroll
        accumulatedScroll += scrollDelta

        when (inlineBehavior) {
            FloatingTabBarInlineBehavior.OnScrollDown -> {
                // Asymmetric on purpose. Collapsing wants hysteresis so a jittery
                // downward scroll can't flicker the bar, and the distance is free
                // there — the user is already moving away. Expanding is the
                // opposite: the bar is what they are reaching for, so making them
                // drag the same distance back reads as the bar being slow to
                // return. Coming back is near-immediate, with just enough travel
                // required that a shaky finger mid-scroll can't oscillate it.
                if (accumulatedScroll <= -scrollThresholdPx && !isInline) {
                    // Scrolling down enough - transition to inline mode
                    isInline = true
                    accumulatedScroll = 0f // Reset after state change
                } else if (accumulatedScroll >= expandThresholdPx && isInline) {
                    // Scrolling up enough - transition to expanded mode
                    isInline = false
                    accumulatedScroll = 0f // Reset after state change
                }
            }

            FloatingTabBarInlineBehavior.OnScrollUp -> {
                // Check if we've scrolled enough to trigger state change
                if (accumulatedScroll >= scrollThresholdPx && !isInline) {
                    // Scrolling up enough - transition to inline mode
                    isInline = true
                    accumulatedScroll = 0f // Reset after state change
                } else if (accumulatedScroll <= -scrollThresholdPx && isInline) {
                    // Scrolling down enough - transition to expanded mode
                    isInline = false
                    accumulatedScroll = 0f // Reset after state change
                }
            }

            FloatingTabBarInlineBehavior.Never -> {
                // Already handled above, but included for completeness
            }
        }

        return Offset.Zero // Don't consume the scroll, let it pass through
    }
}

/**
 * Creates and remembers a [FloatingTabBarScrollConnection] instance.
 *
 * @param initialIsInline Initial state of the tab bar (inline or expanded). Default is false.
 * @param scrollThreshold The scroll distance required to collapse to inline. Default is 50.dp.
 * @param expandThreshold The scroll distance required to expand back. Default is 8.dp — small
 * enough that the bar returns immediately on scroll up, large enough that a shaky finger
 * mid-scroll cannot oscillate it.
 * @param inlineBehavior Defines when the tab bar should transition to inline state. Default is [FloatingTabBarInlineBehavior.OnScrollDown].
 * @return A remembered [FloatingTabBarScrollConnection] instance.
 */
@Composable
fun rememberFloatingTabBarScrollConnection(
    initialIsInline: Boolean = false,
    scrollThreshold: Dp = 50.dp,
    expandThreshold: Dp = 8.dp,
    inlineBehavior: FloatingTabBarInlineBehavior = FloatingTabBarInlineBehavior.OnScrollDown
): FloatingTabBarScrollConnection = with(LocalDensity.current) {
    val scrollThresholdPx = scrollThreshold.toPx()
    val expandThresholdPx = expandThreshold.toPx()
    remember(scrollThresholdPx, expandThresholdPx, inlineBehavior, initialIsInline) {
        FloatingTabBarScrollConnection(
            initialIsInline,
            scrollThresholdPx,
            expandThresholdPx,
            inlineBehavior,
        )
    }
}

/**
 * Defines when the floating tab bar should transition to inline state.
 */
enum class FloatingTabBarInlineBehavior {
    /** Never transition to inline - it stays in expanded state */
    Never,

    /** Transition to inline when scrolling down */
    OnScrollDown,

    /** Transition to inline when scrolling up */
    OnScrollUp
}

interface FloatingTabBarScope {
    /**
     * Adds a regular tab to the floating tab bar.
     *
     * @param key Unique identifier for the tab
     * @param title Composable content for the tab title
     * @param icon Composable content for the tab icon
     * @param onClick Callback invoked when the tab is clicked
     * @param indication Optional indication provider for touch feedback, defaults to LocalIndication.current
     */
    fun tab(
        key: Any,
        title: @Composable () -> Unit,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
        indication: (@Composable () -> Indication)? = { LocalIndication.current }
    )

    /**
     * Adds a standalone tab to the floating tab bar.
     *
     * Note: Calling this method more than once will override the previous standalone tab value.
     *
     * @param key Unique identifier for the standalone tab
     * @param icon Composable content for the tab icon
     * @param onClick Callback invoked when the tab is clicked
     * @param indication Optional indication provider for touch feedback, defaults to LocalIndication.current
     * @param title Composable content for the tab title. Never actually shown —
     * the standalone tab is always an icon-only floating circle, in both inline
     * and expanded states (see InlineStandaloneTab / ExpandedStandaloneTab).
     */
    fun standaloneTab(
        key: Any,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
        indication: (@Composable () -> Indication)? = { LocalIndication.current },
        title: @Composable () -> Unit = {}
    )
}

/**
 * Plain release-triggered [clickable]. Firing on finger-DOWN (the old behaviour)
 * meant any touch that turned into a scroll or a puck drag still navigated first —
 * "opens a screen even when I didn't drop the puck". Release-fire lets Compose's
 * touch-slop arbitration cancel the tap when the gesture becomes a drag/scroll, so
 * only a real tap navigates. Keeps the custom [indication].
 */
@Composable
private fun Modifier.tapClickable(
    indication: Indication?,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return clickable(
        onClick = onClick,
        indication = indication,
        interactionSource = interactionSource
    )
}

@Composable
private fun SharedTransitionScope.InlineBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    accessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)?,
    isAccessoryShared: Boolean,
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
            .then(if (accessory == null) Modifier.wrapContentWidth() else Modifier)
            .height(IntrinsicSize.Max)
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
                modifier = Modifier
            )
        }

        if (accessory != null) {
            InlineAccessory(
                accessory = accessory,
                isAccessoryShared = isAccessoryShared,
                shapes = shapes,
                colors = colors,
                elevations = elevations,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
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
                // fillMaxHeight() + the row's IntrinsicSize.Max is supposed to
                // match this to the docked mini player's height, but intrinsic
                // measurement can under-report the mini player's actual
                // (dynamically-measured) content height — floor it explicitly
                // so the standalone tab pill can't end up shorter.
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .fillMaxHeight()
                    // Height-first: aspectRatio resolves from maxWidth by default,
                    // so on a narrow bar the leftover width — not the row height —
                    // decided the circle's diameter and it came out smaller than the
                    // bar beside it.
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.InlineTab(
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
    Box(
        modifier = modifier
            .sharedElement(
                sharedContentState = rememberSharedContentState("tabGroup"),
                animatedVisibilityScope = animatedVisibilityScope,
                zIndexInOverlay = 1f
            )
            .shadow(
                shape = shapes.tabBarShape,
                elevation = elevations.inlineElevation
            )
            .background(
                color = colors.backgroundColor,
                shape = shapes.tabBarShape
            )
            .clip(shapes.tabBarShape)
            .then(tabBarContentModifier)
            .tapClickable(
                indication = inlineTab.indication?.invoke(),
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
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState("tab#${inlineTab.key}-icon"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        zIndexInOverlay = 1f
                    )
                ) {
                    inlineTab.icon()
                }
            },
            title = { inlineTab.title() },
            isInline = true
        )
    }
}

@Composable
private fun SharedTransitionScope.InlineStandaloneTab(
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
            .sharedElement(
                sharedContentState = rememberSharedContentState("standaloneTab"),
                animatedVisibilityScope = animatedVisibilityScope,
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
            .clip(shapes.standaloneTabShape)
            .then(tabBarContentModifier)
            .then(glow.gestureModifier)
            .then(glow.modifier)
            .tapClickable(
                indication = standaloneTab.indication?.invoke(),
                onClick = standaloneTab.onClick
            )
            // Unlike the regular tab pill (InlineTab, ExpandedTabs), this circle had
            // no inset at all — the icon sat flush against its edge.
            .padding(sizes.tabInlineContentPadding)
    )
}

@Composable
private fun SharedTransitionScope.InlineAccessory(
    accessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)?,
    isAccessoryShared: Boolean,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier
) {
    accessory?.let { accessory ->
        Box(
            modifier = modifier
                .then(
                    if (isAccessoryShared) {
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState("accessory"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    } else {
                        Modifier.animateEnterExitAccessory(
                            sharedTransitionScope = this,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                )
        ) {
            accessory(
                Modifier
                    .fillMaxSize()
                    .shadow(
                        shape = shapes.accessoryShape,
                        elevation = elevations.inlineElevation
                    )
                    .background(color = colors.accessoryBackgroundColor, shapes.accessoryShape)
                    .clip(shapes.accessoryShape),
                animatedVisibilityScope
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.ExpandedBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    tabPosition: (() -> Float?)?,
    accessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)?,
    isAccessoryShared: Boolean,
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

    // The accessory matches the tab row's own (content-sized, not full-bleed)
    // width, so both pills line up edge to edge. The tab row is measured first
    // (onSizeChanged below); the accessory falls back to fillMaxWidth until
    // that first measurement lands — a one-frame lag, same pattern as
    // FloatingMiniPlayer's measuredHeightPx.
    val density = LocalDensity.current
    var tabRowWidthPx by remember { mutableIntStateOf(0) }

    // The standalone tab (search) is always its own floating circle — same as
    // the inline (collapsed) state — never merged into the tab group pill, so
    // it reads as one consistent circular element through both inline and
    // expanded states (see ExpandedStandaloneTab / InlineStandaloneTab, tied
    // together by the shared "standaloneTab" element).
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(sizes.componentSpacing),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (accessory != null) {
            ExpandedAccessory(
                accessory = accessory,
                isAccessoryShared = isAccessoryShared,
                shapes = shapes,
                colors = colors,
                elevations = elevations,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = if (tabRowWidthPx > 0) {
                    Modifier.width(with(density) { tabRowWidthPx.toDp() })
                } else {
                    Modifier.fillMaxWidth()
                }
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
                    modifier = Modifier
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
                        // Same floor the inline row already needed: IntrinsicSize.Max
                        // can under-report the tab group's real height, and without a
                        // floor this circle shrinks to that under-reported value while
                        // the tab pill measures its true (taller) content — which is
                        // the search pill looking smaller than the nav bar.
                        .defaultMinSize(minHeight = 48.dp)
                        .fillMaxHeight()
                        // Height-first, as in the inline row: the default resolves the
                        // square from maxWidth, so leftover row width could decide the
                        // diameter instead of the bar height.
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
private fun SharedTransitionScope.SearchExpandedBar(
    scope: FloatingTabBarScopeImpl,
    selectedTabKey: Any?,
    accessory: (@Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit)?,
    isAccessoryShared: Boolean,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    sizes: FloatingTabBarSizes,
    elevations: FloatingTabBarElevations,
    tabBarContentModifier: Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    searchBarContent: @Composable (Modifier) -> Unit,
    // Vendored addition: the normal-expanded row's own last-measured width
    // (see ExpandedBar's onWidthMeasured) — holding this row to that same
    // width keeps the bar's overall footprint constant across search mode
    // instead of the searchBarContent's weight(1f) filling all available
    // space. Null/0 (nothing measured yet) falls back to filling available
    // width.
    targetWidthPx: Int?,
) {
    val inlineTab = scope.getInlineTab(selectedTabKey)
    val density = LocalDensity.current
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
        if (accessory != null) {
            ExpandedAccessory(
                accessory = accessory,
                isAccessoryShared = isAccessoryShared,
                shapes = shapes,
                colors = colors,
                elevations = elevations,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = targetWidthModifier
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
                    // Same "standaloneTab" key as ExpandedStandaloneTab/InlineStandaloneTab
                    // (the search circle) — without this the circle just pops out and the
                    // bar pops in as two unrelated fades instead of one smoothly growing
                    // pill; this is what makes the tab-group icon side already morph
                    // smoothly, and lets the search-inline (scrolled) state morph back
                    // down to a circle too.
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("standaloneTab"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        zIndexInOverlay = 1f
                    )
                    .shadow(shape = shapes.tabBarShape, elevation = elevations.expandedElevation)
                    .background(color = colors.backgroundColor, shape = shapes.tabBarShape)
                    .clip(shapes.tabBarShape)
                    .then(tabBarContentModifier)
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.ExpandedStandaloneTab(
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
            .sharedElement(
                sharedContentState = rememberSharedContentState("standaloneTab"),
                animatedVisibilityScope = animatedVisibilityScope,
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
            .clip(shapes.standaloneTabShape)
            .then(tabBarContentModifier)
            .then(glow.gestureModifier)
            .then(glow.modifier)
            .tapClickable(
                indication = standaloneTab.indication?.invoke(),
                onClick = standaloneTab.onClick
            )
            // Unlike the regular tab pill (ExpandedTabs), this circle had no inset
            // at all — the icon sat flush against its edge.
            .padding(sizes.tabExpandedContentPadding)
    )
}

@Composable
private fun SharedTransitionScope.ExpandedAccessory(
    accessory: @Composable SharedTransitionScope.(Modifier, AnimatedVisibilityScope) -> Unit,
    isAccessoryShared: Boolean,
    colors: FloatingTabBarColors,
    shapes: FloatingTabBarShapes,
    elevations: FloatingTabBarElevations,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .then(
                if (isAccessoryShared) {
                    Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState("accessory"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                } else {
                    Modifier.animateEnterExitAccessory(
                        sharedTransitionScope = this,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            )
    ) {
        accessory(
            Modifier
                .shadow(
                    shape = shapes.accessoryShape,
                    elevation = elevations.expandedElevation
                )
                .background(color = colors.accessoryBackgroundColor, shapes.accessoryShape)
                .clip(shapes.accessoryShape),
            animatedVisibilityScope
        )
    }
}
@Composable
private fun SharedTransitionScope.ExpandedTabs(
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
    // dampedDragAnimation is remember(...)-ed and its onDragStopped closure would
    // otherwise capture the FIRST composition's allTabs — freezing each tab's
    // onClick (and the isSelected it closed over). That stranded drag-to-Home:
    // Home started selected, so its frozen onClick did scroll-to-top forever
    // instead of navigating. Read the live tabs through this instead.
    val currentTabs = rememberUpdatedState(allTabs)

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val isLtr = layoutDirection == LayoutDirection.Ltr
    val animationScope = rememberCoroutineScope()
    val glassConfig = LocalGlassEffectConfig.current
    // Puck wash. Unset follows the theme rather than assuming a dark bar: a light
    // scheme gets a light wash, so the pill still reads as a raised surface instead
    // of a black patch.
    val puckWash = if (glassConfig.puckColor.isSpecified) {
        glassConfig.puckColor
    } else if (MaterialTheme.colorScheme.surface.luminance() > 0.5f) {
        Color(0xFFF2F2F2)
    } else {
        Color(28, 27, 28)
    }
    val puckRestAlpha = glassConfig.puckOpacity.coerceIn(0f, 1f)

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
    // Matches Kyant's LiquidBottomTabs behavior exactly: navigation fires ONLY
    // in onDragStopped (finger lifted, puck settling onto its target) — never
    // mid-drag. An earlier "predictive fire" here navigated while the puck was
    // still being dragged, which opened screens before the drop.
    // inspectDragGestures (vendored) fires onDragStart/onDrag/onDragEnd for a
    // plain tap-through too, with zero net movement, and never consumes the
    // touch — so a tap that lands on the puck while it's mid-sweep between
    // two tabs would otherwise re-navigate to targetValue (the sweep's
    // destination) on top of whatever the tapped tab's own tapClickable just
    // navigated to. Gate the puck's own navigate-on-release to genuine drags.
    var hasDraggedPuck = false
    val dampedDragAnimation = remember(animationScope, tabsCount) {
        DampedDragAnimation(
            animationScope = animationScope,
            initialValue = currentIndex.toFloat(),
            valueRange = 0f..(tabsCount - 1).toFloat(),
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            // Matches LiquidBottomTabs' literal ratio (78dp/56dp) rather than a
            // fraction of our own tab width — a proportional scale factor, not
            // an absolute size, so it should track the source value directly.
            pressedScale = 78f / 56f,
            onDragStarted = { hasDraggedPuck = false },
            onDragStopped = {
                val targetIndex = targetValue.fastRoundToInt().coerceIn(0, tabsCount - 1)
                currentIndex = targetIndex
                // updateValue (not animateToValue): the modifier's own
                // onDragStart/onDragEnd already own the press-grow + release
                // shrink. animateToValue pressed a SECOND time and, worse, ran
                // under mutatorMutex — a later sync could cancel it mid-settle
                // and strand the puck at the old index (puck stuck on Home while
                // a different screen is showing). updateValue just springs the
                // position, no press, no mutex.
                updateValue(targetIndex.toFloat())
                animationScope.launch {
                    offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f))
                }
                if (hasDraggedPuck) {
                    currentTabs.value.getOrNull(targetIndex)?.onClick?.invoke()
                }
            },
            onDrag = { _, dragAmount ->
                if (dragAmount != Offset.Zero) hasDraggedPuck = true
                updateValue(
                    (targetValue + dragAmount.x / tabWidthPx * if (isLtr) 1f else -1f)
                        .fastCoerceIn(0f, (tabsCount - 1).toFloat())
                )
                animationScope.launch {
                    offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                }
            },
            // Critically damped, so the stretch tracks the drag instead of ringing
            // after it: the library's underdamped default is what made the puck
            // wobble as it crossed each tab.
            velocityDampingRatio = 1f,
        )
    }
    // False until this bar has run its selection sync once. Expanding back from
    // inline builds a brand new ExpandedBar (it is its own AnimatedContent
    // branch), so everything here re-remembers and this effect fires on first
    // composition — with the puck ALREADY on the right tab, because
    // dampedDragAnimation starts at currentIndex. animateToValue would then
    // press-grow and release the puck without moving it anywhere, launched into a
    // coroutine under mutatorMutex so it lands a frame or two into the crossfade:
    // the indicator appears to pop in late and twitch. On that first pass the
    // position is already correct, so there is nothing to animate.
    var hasSyncedSelection by remember(tabsCount) { mutableStateOf(false) }

    // While the pager is moving, the puck IS the pager: same value, same frame, no
    // spring of its own. `tabPosition` returns null the moment that driver settles, so
    // the selectedTabKey effect below resumes ownership and a drag on the puck is never
    // fought over. One collector for the whole gesture, not a coroutine per frame --
    // see DampedDragAnimation.snapValue.
    //
    // Tab index and page index are deliberately the same number: `scope.tabs` here is
    // Home/Library/Settings and so is MainTabsScreens, because search was taken out of
    // the pager and made an overlay. Nothing has to map between them.
    if (tabPosition != null) {
        LaunchedEffect(dampedDragAnimation, tabPosition) {
            snapshotFlow { tabPosition() }.collect { position ->
                if (position != null) dampedDragAnimation.snapValue(position)
            }
        }
    }

    // Keeps the puck synced when selection changes from a tap (or external
    // navigation) rather than a drag on this bar.
    LaunchedEffect(selectedTabKey, tabsCount) {
        val index = allTabs.indexOfFirst { it.key == selectedTabKey }
        if (index != -1) {
            currentIndex = index
            if (!hasSyncedSelection) {
                hasSyncedSelection = true
                // Pin without the press animation; already in the right place.
                dampedDragAnimation.updateValue(index.toFloat())
                return@LaunchedEffect
            }
            // Re-pin on EVERY selection change, not only when currentIndex differs:
            // the puck's animated value can drift out from under a matching currentIndex
            // (a cancelled drag, an interrupted settle) and strand the indicator on the
            // wrong tab — the "selected icon doesn't update" bug. Forcing this here
            // always snaps it back to the actually-selected route.
            //
            // animateToValue (not updateValue): a tap should feel like a drag-release —
            // the same press/grow-then-settle the puck does when you actually drag it,
            // not just a bare position slide. This used to fall back to updateValue
            // because animateToValue's press-grow visibly doubled the selected icon
            // against the puck's glass copy — that's fixed now (the hidden backdrop row's
            // contentScale and padding are kept in sync with the visible row, see above).
            dampedDragAnimation.animateToValue(index.toFloat())
        }
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

    // Invisible tinted copy of the tabs, sampled by the puck below so the
    // selected icon shows through the glass in the accent color.
    val tabsBackdrop = rememberLayerBackdrop()

    Box(modifier.width(with(density) { totalWidthPx.toDp() })) {
        Row(
            Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = panelOffset }
                .sharedElement(
                    sharedContentState = rememberSharedContentState("tabGroup"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    zIndexInOverlay = 1f
                )
                .shadow(
                    shape = shapes.tabBarShape,
                    elevation = elevations.expandedElevation
                )
                .background(
                    color = colors.backgroundColor,
                    shape = shapes.tabBarShape
                )
                .clip(shapes.tabBarShape)
                .then(tabBarContentModifier)
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
                                }
                                .then(
                                    if (tab.key == selectedTabKey) {
                                        Modifier.sharedElement(
                                            sharedContentState = rememberSharedContentState("tab#${tab.key}-icon"),
                                            animatedVisibilityScope = animatedVisibilityScope,
                                            zIndexInOverlay = 1f
                                        )
                                    } else {
                                        Modifier.animateEnterExitTab(
                                            sharedTransitionScope = this@ExpandedTabs,
                                            animatedVisibilityScope = animatedVisibilityScope
                                        )
                                    }
                                )
                        ) {
                            tab.icon()
                        }
                    },
                    title = {
                        Box(
                            Modifier.animateEnterExitTab(
                                sharedTransitionScope = this@ExpandedTabs,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
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
                            onClick = tab.onClick
                        )
                        .padding(sizes.tabExpandedContentPadding)
                )
            }
        }

        if (backdrop != null) {
            // Fixed 1dp blur here (see the blur() call below), so this is a
            // stable fraction, not something that needs recomputing per frame.
            val tabsBackdropScale = glassResolutionScale(1f)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer { translationX = panelOffset }
                    // Padding here (not after drawBackdrop, like the tail
                    // .padding below) so it actually shrinks this row's own
                    // measured bounds — matching LiquidBottomTabs' explicit
                    // .height(56dp) vs the main row's 64dp: what tabsBackdrop
                    // captures needs to be genuinely inset, not full-bleed.
                    .padding(sizes.tabBarContentPadding)
                    // Ported from LiquidBottomTabs: this hidden row isn't just a
                    // tinted icon cutout, it's a real blurred/lensed glass sample
                    // of the screen behind the bar — that's what makes the puck
                    // below look like it's made of the *bar's* frosted material
                    // (not a flat color) when it slides underneath.
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { shapes.tabBarShape },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            vibrancy()
                            // Without any blur here, this hidden row's lens()
                            // distortion (below) samples the raw, sharp backdrop —
                            // that's what made the puck look like undistorted,
                            // "blur gone" background while dragging. Kyant's source
                            // uses a fixed 8dp; we drive it from the user's own
                            // glass blur-intensity setting instead of hardcoding.
                            blur(glassConfig.blurRadius.dp.toPx() * tabsBackdropScale)
                            lens(
                                15f.dp.toPx() * progress * tabsBackdropScale,
                                18f.dp.toPx() * progress * tabsBackdropScale
                            )
                        },
                        highlight = {
                            val progress = dampedDragAnimation.pressProgress
                            Highlight.Default.copy(alpha = progress)
                        },
                        onDrawSurface = { drawRect(colors.backgroundColor) },
                        // Same GPU-saving trick as Modifier.liquidGlass: record this
                        // hidden row at a fraction of surface resolution and let the
                        // blur hide the upscale — was left at the 1f (full-res)
                        // default here even though liquidGlass already uses this.
                        backdropScale = tabsBackdropScale,
                        frozen = LocalTabBarBackdropFrozen.current
                    )
                    .then(interactiveHighlight.modifier)
                    // NOT a second .padding(sizes.tabBarContentPadding) here — the one
                    // above (before drawBackdrop) already insets this row's measured
                    // bounds. A second one compounded the inset only on this hidden
                    // row (the visible row above applies it exactly once), shifting
                    // its tabs right of their real counterparts — the puck's sampled
                    // icon then visibly drifted from the real icon under it.
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                horizontalArrangement = Arrangement.spacedBy(sizes.tabSpacing)
            ) {
                allTabs.forEachIndexed { index, tab ->
                    Tab(
                        icon = tab.icon,
                        title = tab.title,
                        isInline = false,
                        isStandalone = false,
                        // Must match the visible row's per-tab contentScale (see
                        // above) — this hidden row is what the puck's glass samples,
                        // so if it doesn't zoom in sync with the real icon during a
                        // press/drag, the accent-tinted copy visibly drifts from the
                        // real icon on top, reading as a ghosted double image.
                        contentScale = if (index == currentIndex) {
                            lerp(1f, 1.12f, dampedDragAnimation.pressProgress)
                        } else {
                            1f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            // Must match the visible row's skipToLookaheadSize() too
                            // (see below) — without it this hidden row can measure a
                            // hair differently under the shared-transition lookahead
                            // pass, leaving the puck's sampled icon slightly off from
                            // the real one even at rest.
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
                    if (backdrop != null) {
                        Modifier.drawBackdrop(
                            backdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop),
                            shape = { shapes.tabShape },
                            effects = {
                                val progress = dampedDragAnimation.pressProgress
                                // Soft frosted puck at rest (constant blur), deepening
                                //
                                blur(3f.dp.toPx() * (1f - progress))

                                // Refraction is weakest at REST and deepens while the
                                // puck is held and dragged: at rest the sharp overlay
                                // icon sits on top at full alpha, and any real bend
                                // there splits it from its lensed copy underneath —
                                // the doubled/ghosted icon. That overlay fades out
                                // over the first third of the press ramp, so by the
                                // time the bend is at full strength there is only one
                                // copy left to warp.
                                lens(
                                    lerp(0f.dp.toPx(), 10f.dp.toPx(), progress),
                                    lerp(0f.dp.toPx(), 12f.dp.toPx(), progress),
                                    // Dispersion is a drag-only flourish. It costs a
                                    // second, heavier shader, so a puck sitting still
                                    // should not be paying for it.
                                    chromaticAberration = progress > 0.01f
                                )
                            },
                            highlight = {
                                val progress = dampedDragAnimation.pressProgress
                                // Floored, not 0-at-rest. With no rim the puck's
                                // shape came entirely from its dark wash below,
                                // so it vanished into the bar whenever the bar
                                // itself sat on dark content. A specular rim
                                // reads against light AND dark, and unlike
                                // lightening the wash it leaves the selected
                                // icon (white by default, drawn on top) legible.
                                Highlight.Default.copy(
                                    alpha = lerp(PuckRestHighlightAlpha, 1f, progress)
                                )
                            },
                            shadow = {
                                val progress = dampedDragAnimation.pressProgress
                                // Same reasoning: a little separation at rest so
                                // the puck reads as a raised element rather than
                                // a flat patch of the bar.
                                Shadow(alpha = lerp(PuckRestShadowAlpha, 1f, progress))
                            },
                            innerShadow = {
                                val progress = dampedDragAnimation.pressProgress
                                InnerShadow(radius = 8f.dp * progress, alpha = progress)
                            },
                            layerBlock = {
                                scaleX = dampedDragAnimation.scaleX
                                scaleY = dampedDragAnimation.scaleY
                                val velocity = dampedDragAnimation.velocity / 10f
                                scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                                scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                            },
                            onDrawSurface = {
                                // What shows inside the puck is the hidden tinted tab
                                // row sampled back through this glass, so anything
                                // painted here lands ON TOP of the selected icon.
                                // Black at any real strength crushes it — hence an
                                // accent wash that defines the pill's shape while
                                // leaving the icon at full strength, deepening only
                                // while pressed.
                                val progress = dampedDragAnimation.pressProgress

                                // Was a hardcoded near-black at 0.8 alpha, which on a
                                // dark bar made the whole puck read as a dark blob and
                                // on a light theme was simply wrong. Colour and resting
                                // opacity are configurable now, and the unset default
                                // follows the theme instead of assuming dark.
                                drawRect(puckWash.copy(alpha = puckRestAlpha * (1f - 0.75f * progress)))
                            },
                            frozen = LocalTabBarBackdropFrozen.current
                        )
                    } else {
                        Modifier
                            .graphicsLayer {
                                scaleX = dampedDragAnimation.scaleX
                                scaleY = dampedDragAnimation.scaleY
                            }
                            .shadow(
                                shape = shapes.tabShape,
                                elevation = elevations.expandedElevation * dampedDragAnimation.pressProgress
                            )
                            .background(
                                colors.backgroundColor.copy(
                                    alpha = 0.5f + 0.5f * dampedDragAnimation.pressProgress
                                ),
                                shapes.tabShape
                            )
                            .clip(shapes.tabShape)
                    }
                )
        )

        // The selected icon, drawn a second time above the puck.
        //
        // What shows *through* the puck is the hidden accent-tinted tab row
        // refracted by the glass, and the puck's surface tint necessarily paints
        // over it — so that copy can never be at full opacity. Redrawing the icon
        // here is the only way to keep it crisp.
        //
        // It rides the puck's own translation and squash, so the two cannot drift
        // apart mid-drag, and it always covers the dimmed copy underneath rather
        // than ghosting beside it. Which icon to draw is derived state, so this
        // recomposes when the puck crosses into a new tab, not every frame.
        val puckIndex by remember(tabsCount) {
            derivedStateOf {
                dampedDragAnimation.value
                    .fastRoundToInt()
                    .coerceIn(0, (tabsCount - 1).coerceAtLeast(0))
            }
        }
        Box(
            Modifier
                .padding(sizes.tabBarContentPadding)
                .graphicsLayer {
                    translationX =
                        if (isLtr) dampedDragAnimation.value * tabWidthPx + panelOffset
                        else size.width - (dampedDragAnimation.value + 1f) * tabWidthPx + panelOffset
                    scaleX = dampedDragAnimation.scaleX
                    scaleY = dampedDragAnimation.scaleY
                    // Settled state only. Fades roughly three times faster than the
                    // press ramp, so it is fully gone a third of the way into the
                    // gesture instead of lingering over the moving puck. Read in the
                    // draw phase, so it costs no recomposition.
                    alpha = (1f - dampedDragAnimation.pressProgress * 3f).fastCoerceIn(0f, 1f)
                }
                .width(sizes.tabWidth)
                .fillMaxHeight()
                // Purely decorative: the tab underneath keeps the click and the
                // semantics, and this must never swallow either.
                .clearAndSetSemantics {},
        ) {
            currentTabs.value.getOrNull(puckIndex)?.let { tab ->
                // Must go through Tab, not just tab.icon(): Tab is a Column of
                // icon THEN title, so drawing the icon alone centres it on the
                // puck while the row centres it on the icon+title pair — which is
                // exactly how far off it looked. Same props as the row's selected
                // tab, so the two line up.
                Tab(
                    // No tint override: the icon keeps the selected colour its
                    // caller gave it, which is the point of drawing it again.
                    icon = { tab.icon() },
                    title = { tab.title() },
                    isInline = false,
                    isStandalone = false,
                    // No press zoom: only visible at rest, where the row's own lerp
                    // lands on 1f. Leaving it out also stops the overlay recomposing
                    // on every frame of a drag.
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(sizes.tabExpandedContentPadding),
                )
            }
        }
    }
}

@Composable
private fun Tab(
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
            Arrangement.spacedBy((-2).dp, Alignment.CenterVertically)
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
 * A custom modifier that provides smooth enter/exit animations without clipping shadows or other content.
 * This is an alternative to animateEnterExit that uses renderInSharedTransitionScopeOverlay to prevent clipping.
 */
@Composable
private fun Modifier.animateEnterExitAccessory(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
): Modifier = with(sharedTransitionScope) {
    with(animatedVisibilityScope) {
        val animatedAlpha by transition.animateFloat { targetState ->
            when (targetState) {
                EnterExitState.Visible -> 1f
                else -> 0f
            }
        }

        this@animateEnterExitAccessory
            .renderInSharedTransitionScopeOverlay()
            .graphicsLayer(
                compositingStrategy = CompositingStrategy.ModulateAlpha,
                alpha = animatedAlpha
            )
    }
}

/**
 * A custom modifier that provides smooth enter/exit animations with fade and blur effects.
 */
@Composable
private fun Modifier.animateEnterExitTab(
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

        val blurRadius = with(LocalDensity.current) { 50.dp.toPx() }
        val animatedBlur by transition.animateFloat(
            transitionSpec = {
                keyframes {
                    durationMillis = durationMs
                    if (targetState == EnterExitState.Visible) {
                        blurRadius atFraction enterStartFraction using FastOutSlowInEasing
                        0f atFraction enterEndFraction
                    }
                }
            }
        ) { targetState ->
            when (targetState) {
                EnterExitState.Visible -> 0f
                else -> blurRadius
            }
        }

        graphicsLayer {
            alpha = animatedAlpha
            renderEffect = BlurEffect(
                radiusX = animatedBlur,
                radiusY = animatedBlur
            )
        }
    }
}

private class FloatingTabBarScopeImpl : FloatingTabBarScope {
    val tabs = mutableStateListOf<FloatingTabBarTab>()
    var standaloneTab: FloatingTabBarTab? by mutableStateOf(null)
        private set
    private var inlineTab: FloatingTabBarTab? = null

    fun getInlineTab(selectedTabKey: Any?): FloatingTabBarTab? {
        // Screen-accurate: only ever report a regular tab as "the inline tab"
        // when it's actually the current screen. selectedTabKey not matching
        // any regular tab (e.g. currently on the standalone tab) falls back to
        // the last regular tab that really was selected — never a hardcoded
        // default like tabs.firstOrNull(), which would misrepresent Home as
        // selected when the user has never actually been there.
        val selectedTab = tabs.find { it.key == selectedTabKey }
        if (selectedTab != null) {
            inlineTab = selectedTab
            return selectedTab
        }
        return inlineTab
    }

    override fun tab(
        key: Any,
        title: @Composable () -> Unit,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
        indication: (@Composable () -> Indication)?
    ) {
        tabs.add(
            FloatingTabBarTab(
                key = key,
                title = title,
                icon = icon,
                onClick = onClick,
                indication = indication
            )
        )
    }
    
    override fun standaloneTab(
        key: Any,
        icon: @Composable () -> Unit,
        onClick: () -> Unit,
        indication: (@Composable () -> Indication)?,
        title: @Composable () -> Unit
    ) {
        standaloneTab = FloatingTabBarTab(
            key = key,
            title = title,
            icon = icon,
            onClick = onClick,
            indication = indication
        )
    }
}

private data class FloatingTabBarTab(
    val key: Any,
    val title: @Composable () -> Unit,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val indication: (@Composable () -> Indication)?
)

/**
 * Represents the colors used in [FloatingTabBar].
 */
@Immutable
data class FloatingTabBarColors(
    val backgroundColor: Color,
    val accessoryBackgroundColor: Color,
)

/**
 * Represents the shapes used in [FloatingTabBar].
 */
@Immutable
data class FloatingTabBarShapes(
    val tabBarShape: Shape,
    val tabShape: Shape,
    val standaloneTabShape: Shape,
    val accessoryShape: Shape,
)

/**
 * Represents the elevations used in [FloatingTabBar].
 */
@Immutable
data class FloatingTabBarElevations(
    val inlineElevation: Dp,
    val expandedElevation: Dp,
)

/**
 * Represents the sizes and spacing used in [FloatingTabBar].
 */
@Immutable
data class FloatingTabBarSizes(
    val tabBarContentPadding: PaddingValues,
    val tabInlineContentPadding: PaddingValues,
    val tabExpandedContentPadding: PaddingValues,
    val componentSpacing: Dp,
    val tabSpacing: Dp,
    // Vendored addition: fixed width of one expanded tab slot. The drag-puck
    // indicator needs a known per-tab width to map drag offset to tab index,
    // so expanded tabs are equal-width instead of sized to their content.
    val tabWidth: Dp = FloatingTabBarDefaults.TabWidth,
)

/**
 * Contains the default values used by [FloatingTabBar].
 */
object FloatingTabBarDefaults {
    /**
     * Width of one expanded tab slot at full size. Callers that have to fit the
     * pill plus the standalone tab into a narrow screen shrink below this — see
     * [com.convxy.music.ui.component.AppFloatingNavBar] — so it is the ceiling,
     * not a guarantee.
     */
    val TabWidth: Dp = 88.dp

    /**
     * Creates a [FloatingTabBarColors] that represents the default colors used in a [FloatingTabBar].
     *
     * @param backgroundColor the color used for the tab bar background
     * @param accessoryBackgroundColor the color used for the accessory background
     */
    @Composable
    fun colors(
        backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        accessoryBackgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ): FloatingTabBarColors = FloatingTabBarColors(
        backgroundColor = backgroundColor,
        accessoryBackgroundColor = accessoryBackgroundColor,
    )

    /**
     * Creates a [FloatingTabBarShapes] that represents the default shapes used in a [FloatingTabBar].
     *
     * @param tabBarShape the shape used to clip the tab bar
     * @param tabShape the shape used to clip individual tabs. Can be useful for example to control the click ripple effect shape
     * @param standaloneTabShape the shape used to clip the standalone tab
     * @param accessoryShape the shape used to clip the accessory container
     */
    @Composable
    fun shapes(
        // ContinuousRoundedRectangle(percent = 50) instead of RoundedCornerShape/
        // CircleShape: same capsule/circle silhouette, but Kyant0/Capsule's
        // continuous (superellipse) corners instead of Android's circular-arc
        // ones — smoother, and lerp-able for the puck's drag-to-search morph.
        tabBarShape: Shape = ContinuousRoundedRectangle(percent = 50),
        tabShape: Shape = ContinuousRoundedRectangle(percent = 50),
        standaloneTabShape: Shape = ContinuousRoundedRectangle(percent = 50),
        accessoryShape: Shape = ContinuousRoundedRectangle(percent = 50),
    ): FloatingTabBarShapes = FloatingTabBarShapes(
        tabBarShape = tabBarShape,
        tabShape = tabShape,
        standaloneTabShape = standaloneTabShape,
        accessoryShape = accessoryShape,
    )

    /**
     * Creates a [FloatingTabBarSizes] that represents the default sizes used in a [FloatingTabBar].
     *
     * @param tabBarContentPadding the padding applied to the tab bar content. This also applies to the standalone tab content.
     * @param tabInlineContentPadding the padding applied to tabs in inline state
     * @param tabExpandedContentPadding the padding applied to tabs in expanded state
     * @param componentSpacing the spacing between components
     * @param tabSpacing the spacing between tabs in expanded state
     */
    @Composable
    fun sizes(
        tabBarContentPadding: PaddingValues = PaddingValues(vertical = 4.dp, horizontal = 4.dp),
        tabInlineContentPadding: PaddingValues = PaddingValues(10.dp),
        // horizontal was 20dp (the source lib's default for content-hugging
        // tabs); with fixed-width tabWidth cells that ate too much of the
        // budget and clipped labels like "Settings" — tightened to fit text.
        tabExpandedContentPadding: PaddingValues = PaddingValues(vertical = 6.dp, horizontal = 6.dp),
        componentSpacing: Dp = 8.dp,
        tabSpacing: Dp = 0.dp,
        tabWidth: Dp = TabWidth,
    ): FloatingTabBarSizes = FloatingTabBarSizes(
        tabBarContentPadding = tabBarContentPadding,
        tabInlineContentPadding = tabInlineContentPadding,
        tabExpandedContentPadding = tabExpandedContentPadding,
        componentSpacing = componentSpacing,
        tabSpacing = tabSpacing,
        tabWidth = tabWidth,
    )

    /**
     * Creates a [FloatingTabBarElevations] that represents the default elevations used in a [FloatingTabBar].
     *
     * @param inlineElevation the elevation used for tabs in inline state
     * @param expandedElevation the elevation used for tabs in expanded state
     */
    @Composable
    fun elevations(
        inlineElevation: Dp = 6.dp,
        expandedElevation: Dp = 12.dp,
    ): FloatingTabBarElevations = FloatingTabBarElevations(
        inlineElevation = inlineElevation,
        expandedElevation = expandedElevation,
    )
}