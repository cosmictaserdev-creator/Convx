/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convx.music.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.convx.music.ui.component.backdrop.catalog.utils.InteractiveHighlight
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import com.convx.music.ui.ios.foundation.IosMenuMaterial
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.menuVibrancy
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.component.liquidGlass
import com.convx.music.ui.component.isGlassAllowed
import kotlin.math.roundToInt

/**
 * Where the finger last went down, in window pixels. Written by MainActivity's
 * dispatchTouchEvent, which sees every touch before any composable does, so the menu
 * opened by a tap or long-press knows the point it should grow out of without the ~30
 * `menuState.show { }` call sites passing anything.
 */
object MenuAnchor {
    var lastDown: Offset = Offset.Unspecified
}

/**
 * Every long-press / "more" menu in the app, as iOS 27 presents it
 * (designRefrence/threadsdownloader.com_bcecf6(1).mp4): a liquid-glass panel that grows out
 * of the touch point, no dim and no page shrink behind it. While a finger moves over it
 * a glow follows the finger and the panel leans toward it (the same InteractiveHighlight +
 * elastic stretch the glass buttons use). A tap outside dismisses.
 *
 * Open, measured off the capture: a small blob at the touch point stretches into the panel
 * in ~200ms, width leading height -- two springs, the vertical one softer, is what reads as
 * liquid rather than a uniform scale. Close runs the same path back to the anchor, faster.
 *
 * [glass] false draws a translucent plate instead: over the expanded player the app backdrop
 * the glass samples is the page underneath the player, not what the user is looking at.
 */
@Composable
fun OverlayMenu(
    state: MenuState,
    modifier: Modifier = Modifier,
    glass: Boolean = true,
    /** What the glass samples: everything the panel can sit over, chrome included. */
    backdrop: com.convx.music.ui.component.backdrop.Backdrop? = null,
    liquid: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val scaleX = remember { Animatable(0f) }
    val scaleY = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    // Stays composed until the close animation has finished, then drops out.
    var shown by remember { mutableStateOf(false) }
    var anchor by remember { mutableStateOf(Offset.Unspecified) }

    LaunchedEffect(state.isVisible) {
        if (state.isVisible) {
            anchor = MenuAnchor.lastDown
            shown = true
            scaleX.snapTo(0.12f); scaleY.snapTo(0.12f); alpha.snapTo(0f)
            launch { alpha.animateTo(1f, tween(90)) }
            launch { scaleX.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = 700f)) }
            scaleY.animateTo(1f, spring(dampingRatio = 0.78f, stiffness = 420f))
        } else if (shown) {
            launch { scaleX.animateTo(0.12f, spring(dampingRatio = 1f, stiffness = 900f)) }
            launch { scaleY.animateTo(0.12f, spring(dampingRatio = 1f, stiffness = 900f)) }
            alpha.animateTo(0f, tween(140, delayMillis = 40))
            shown = false
        }
    }

    if (!shown) return

    // Glass must sample what is on screen, player included, not the page the player covers.
    val menuBackdrop = backdrop ?: LocalAppBackdrop.current
    CompositionLocalProvider(
        LocalAppBackdrop provides menuBackdrop,
        com.convx.music.ui.ios.foundation.LocalIosBackdrop provides menuBackdrop,
        com.convx.music.ui.ios.foundation.LocalMenuLiquid provides (liquid && glass),
        com.convx.music.ui.ios.foundation.LocalMenuLongPress provides true,
    ) {
        fun dismiss() {
            focusManager.clearFocus()
            state.isVisible = false
        }

        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                // A slight dim behind the menu (no blur), fading with it.
                .drawBehind { drawRect(Color.Black.copy(alpha = 0.18f * alpha.value)) }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = ::dismiss,
                ),
        ) {
            if (state.isVisible) BackHandler(onBack = ::dismiss)

            val screenW = with(density) { maxWidth.toPx() }
            val screenH = with(density) { maxHeight.toPx() }
            val margin = with(density) { 12.dp.toPx() }
            // Clamped against the insets here rather than padded: the anchor is in raw window
            // pixels, and padding the panel would shift it off the point it grows from.
            val safeTop = WindowInsets.safeDrawing.getTop(density) + margin
            val safeBottom = screenH - WindowInsets.safeDrawing.getBottom(density) - margin
            val touch = anchor.takeIf { it.isSpecified } ?: Offset(screenW - margin, screenH / 2f)
            var panelSize by remember { mutableStateOf(IntSize.Zero) }

            // Right edge just past the finger (the "…" buttons sit at the row's right end),
            // below the finger when it fits, above otherwise; always inside the safe area.
            val left = (touch.x + margin - panelSize.width).coerceIn(margin, (screenW - panelSize.width - margin).coerceAtLeast(margin))
            val below = touch.y + margin
            val top = (if (below + panelSize.height <= safeBottom) below else touch.y - margin - panelSize.height)
                .coerceIn(safeTop, (safeBottom - panelSize.height).coerceAtLeast(safeTop))
            val origin = if (panelSize.width > 0 && panelSize.height > 0) {
                TransformOrigin(
                    ((touch.x - left) / panelSize.width).coerceIn(0f, 1f),
                    ((touch.y - top) / panelSize.height).coerceIn(0f, 1f),
                )
            } else {
                TransformOrigin.Center
            }

            // iOS 27 finger bloom: brighter and tighter than a button's sheen.
            val highlight = remember(scope) {
                InteractiveHighlight(animationScope = scope, radiusScale = 0.6f, sheenAlpha = 0.06f, spotAlpha = 0.32f, releasePulse = true)
            }
            val shape = IosMenuMaterial.PanelShape
            val glassConfig = LocalGlassEffectConfig.current
            val useGlass = glass && liquid && isGlassAllowed()
            val darkAppearance = AppTheme.colorScheme.surface.luminance() <= 0.5f
            val menuGlassConfig = if (darkAppearance) {
                glassConfig.copy(surfaceTintColor = IosMenuMaterial.DarkPanelFill, surfaceOpacity = 0.82f, blurRadius = 8f)
            } else {
                glassConfig.copy(blurRadius = 8f)
            }
            val menuFallbackColor = if (darkAppearance) IosMenuMaterial.DarkPanelFill else AppTheme.colorScheme.surfaceContainerHigh

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .width(MenuWidth)
                    // Never taller than the safe area, or a long menu runs under the nav bar.
                    .heightIn(max = with(density) { (safeBottom - safeTop).coerceAtLeast(0f).toDp() })
                    .onSizeChanged { panelSize = it }
                    .graphicsLayer {
                        transformOrigin = origin
                        this.scaleX = scaleX.value
                        this.scaleY = scaleY.value
                        this.alpha = alpha.value
                    }
                    // Frosted menu material (liquid glass when [liquid]), stretch + bloom, glass rim;
                    // over the expanded player the fill is laid flat (its backdrop is the page
                    // under the player, not what is on screen).
                    .then(
                        if (useGlass) Modifier.liquidGlass(config = menuGlassConfig, shape = shape, highlightAlpha = 0.20f)
                        else Modifier.background(menuFallbackColor.copy(alpha = 0.94f), shape),
                    )
                    .then(highlight.modifier)
                    .then(highlight.gestureModifier)
                    .menuVibrancy()
                    // Swallows taps so a press on the panel does not reach the dismiss layer.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                // Deliberately NOT verticalScroll: the menus put a LazyColumn inside this Column,
                // and a lazy list inside a scrolling parent gets an infinite height and throws.
                CompositionLocalProvider(LocalInMenuPanel provides true) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        content = { state.content(this) },
                    )
                }
            }
        }
    }
}

/** iOS context menus are ~250pt wide; the rows are written for about this. */
private val MenuWidth = 270.dp

/**
 * True inside the menu panel: groups and action rows drop their own card backgrounds
 * and draw flat on the panel, as iOS menu items do.
 */
val LocalInMenuPanel = compositionLocalOf { false }

/**
 * Bottom padding a menu's list needs: the navigation bar inset as a sheet, nothing inside
 * the floating panel (which never reaches the bar), where it was just dead space.
 */
@Composable
fun menuBottomInset(): androidx.compose.ui.unit.Dp =
    if (LocalInMenuPanel.current) 0.dp
    else androidx.compose.foundation.layout.WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
