/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.convxy.music.LocalPlayerAwareWindowInsets
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.BackdropEffectScope
import com.convxy.music.ui.component.backdrop.backdrops.rememberLayerBackdrop
import com.convxy.music.ui.component.backdrop.drawBackdrop
import com.convxy.music.ui.component.backdrop.effects.blur
import com.convxy.music.ui.component.backdrop.effects.colorControls
import com.convxy.music.ui.component.backdrop.effects.lens
import com.convxy.music.ui.component.backdrop.highlight.Highlight
import com.convxy.music.ui.component.backdrop.highlight.HighlightStyle
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported
import com.convxy.music.ui.component.shapes.ContinuousRoundedRectangle

/**
 * The long-press actions, as an overlay rather than a bottom sheet.
 *
 * Same [MenuState] and therefore the same call sites: every `menuState.show { … }` in the
 * app goes through here or through [BottomSheetMenu] depending on one preference, and
 * neither the menus themselves nor the ~30 places that open them know which is in use.
 *
 * A dim rather than a blur behind it: blurring the whole window every frame a menu is
 * open is real GPU cost for a surface that is about to be covered anyway, and the dim
 * reads the same and costs nothing.
 *
 * The panel itself IS glass when [GlassComponent.MENU] is on. It samples the app backdrop
 * through [rememberOuterBackdropSampler], which is safe from a root-level overlay: this
 * composable sits next to MainActivity's recorded box, not inside it, so the recording is
 * a sibling layer rather than an ancestor (the old note here claimed the opposite; the
 * ancestry audit of every glass surface disproved it). The panel then exports its own
 * painted surface as a backdrop and provides it as [LocalAppBackdrop] to its content, so
 * the pills and rows inside refract the sheet they sit on — the library's nested-glass
 * pattern, legal because an exported recording holds the surface's own paint and never
 * its children. Where sampling is unsafe the panel falls back to frosted glass and the
 * controls inside keep working, because the export is live either way.
 */
@Composable
fun OverlayMenu(
    state: MenuState,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    /**
     * What the panel refracts, when the caller records the stack behind the menu
     * (MainActivity does, while a menu is open). Null keeps the inherited backdrop.
     */
    sampleBackdrop: Backdrop? = null,
) {
    val focusManager = LocalFocusManager.current

    fun dismiss() {
        focusManager.clearFocus()
        state.isVisible = false
    }

    AnimatedVisibility(
        visible = state.isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ScrimColor)
                // No ripple and no indication: the scrim is a dismiss target, not a
                // button, and a ripple spreading across the whole window on every
                // outside tap looks like a bug.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = ::dismiss,
                ),
            contentAlignment = Alignment.BottomCenter,
        ) {
            BackHandler(onBack = ::dismiss)

            // The scrim fades (above); the menu itself rises. Two AnimatedVisibilities
            // rather than one enter spec, because a slide on the outer one would drag the
            // full-screen scrim up with it.
            AnimatedVisibility(
                visible = state.isVisible,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                    initialOffsetY = { it },
                ) + fadeIn(),
                exit = slideOutVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    targetOffsetY = { it },
                ) + fadeOut(),
            ) {
                val menuShape = ContinuousRoundedRectangle(28.dp)
                val config = LocalGlassEffectConfig.current
                val glassWanted = config.isEnabledFor(GlassComponent.MENU) && isGlassAllowed()
                val realGlass = glassWanted &&
                    !shouldUseTranslucentGlassFallback(config.style, isRenderEffectSupported())
                val menuBackdrop = rememberLayerBackdrop()
                // Passing null here would NOT fall back to the default: the
                // parameter default only applies when the argument is omitted, so
                // over the pages the sampler had no backdrop at all and the sheet
                // fell back to frosted glass. Null means "whatever is around".
                val outer = rememberOuterBackdropSampler(sampleBackdrop ?: LocalAppBackdrop.current)
                val density = LocalDensity.current
                // Heavier than the clear pills, like the player: a menu is a sheet of
                // material, not a control.
                val menuBlurDp = config.blurRadius * MenuBlurMultiplier
                val resolutionScale = glassResolutionScale(menuBlurDp).coerceIn(0.05f, 1f)
                val saturation = glassSaturation(config.vibrancy)
                val blurPx = with(density) { menuBlurDp.dp.toPx() } * resolutionScale
                // A menu usually opens over the player's artwork wash or a dimmed
                // list, i.e. material with little edge detail; the bend needs to be
                // stronger than on a control pill before it is visible at all.
                val lensHeightPx =
                    with(density) { (config.lensHeight * LENS_MAX_DP).dp.toPx() } * resolutionScale
                val lensAmountPx =
                    with(density) { (config.lensAmount * LENS_MAX_DP * 1.5f).dp.toPx() } *
                        resolutionScale
                // Remembered on exactly what they read: a fresh lambda each recomposition
                // makes the drawBackdrop element unequal and re-captures the backdrop.
                val effectsBlock: BackdropEffectScope.() -> Unit = remember(
                    saturation,
                    blurPx,
                    lensHeightPx,
                    lensAmountPx,
                    config.depthEffect,
                    config.chromaticAberration,
                ) {
                    {
                        if (saturation != 1f) colorControls(saturation = saturation)
                        if (blurPx > 0f) blur(blurPx)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            (lensHeightPx > 0f || lensAmountPx > 0f)
                        ) {
                            lens(
                                refractionHeight = lensHeightPx,
                                refractionAmount = lensAmountPx,
                                depthEffect = config.depthEffect,
                                chromaticAberration = config.chromaticAberration,
                            )
                        }
                    }
                }
                val tintBlock: DrawScope.() -> Unit = remember(background, config.surfaceOpacity) {
                    {
                        drawRect(
                            background.copy(
                                alpha = (config.surfaceOpacity * 0.8f).coerceIn(0f, 1f)
                            )
                        )
                        // Same brightness lift as the buttons: over a dark player
                        // wash the theme surface alone reads as a flat panel.
                        drawRect(Color.White.copy(alpha = 0.07f))
                    }
                }
                // The rim was disabled here as "a stray band of light" on large
                // surfaces; without it, over featureless material, the sheet lost
                // every cue that reads as glass. Dimmer than on the pills, not off.
                val rimBlock: () -> Highlight? = remember {
                    {
                        Highlight(
                            width = 0.8.dp,
                            style = HighlightStyle.Default(
                                color = Color.White.copy(alpha = 0.35f)
                            ),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .widthIn(max = MenuMaxWidth)
                        .fillMaxWidth()
                        .then(
                            when {
                                realGlass -> Modifier
                                    .then(outer.measureModifier)
                                    .drawBackdrop(
                                        backdrop = outer.effective,
                                        shape = { menuShape },
                                        effects = effectsBlock,
                                        highlight = rimBlock,
                                        exportedBackdrop = menuBackdrop,
                                        onDrawSurface = tintBlock,
                                        backdropScale = resolutionScale,
                                    )
                                glassWanted -> Modifier.background(
                                    background.copy(alpha = config.surfaceOpacity.coerceIn(0f, 1f)),
                                    menuShape,
                                )
                                else -> Modifier.background(background, menuShape)
                            }
                        )
                        // Swallows taps so a press on the menu itself does not reach the
                        // scrim's dismiss handler underneath.
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        )
                        // Deliberately NOT verticalScroll: the menus put a LazyColumn inside
                        // this Column, and a lazy list measured inside a scrolling parent gets
                        // an infinite height constraint and throws. The menus scroll
                        // themselves; this Column only has to stay bounded, which the
                        // fillMaxSize parent already guarantees.
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    content = {
                        // The sheet's own painted surface, exported above: the pills and
                        // rows inside refract the sheet they sit on, and stay legal glass
                        // everywhere because that recording cannot contain them.
                        CompositionLocalProvider(LocalAppBackdrop provides menuBackdrop) {
                            state.content(this)
                        }
                    },
                )
            }
        }
    }
}

/** Heavy enough that the list behind reads as dismissed, light enough to keep context. */
private val ScrimColor = Color.Black.copy(alpha = 0.55f)

/** Beyond this the action rows stretch into a very wide, hard-to-scan line on a tablet. */
private val MenuMaxWidth = 560.dp

/** Sheets of material blur more than the clear control pills, like the player does. */
private const val MenuBlurMultiplier = 2f
