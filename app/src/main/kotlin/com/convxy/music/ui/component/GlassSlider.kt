/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.convxy.music.ui.component.backdrop.Backdrop
import com.convxy.music.ui.component.backdrop.isRenderEffectSupported

/**
 * Whether a value slider gets the liquid glass rendering or Material's. A plain
 * function so the decision is unit-testable without a Composition.
 *
 * `hasCustomColors` declines the glass path because [LiquidGlassSlider] takes
 * active/inactive rail colours, not a Material [SliderColors] bundle (which also
 * carries thumb, disabled and indicator colours it has no equivalent for). Rather
 * than silently drop half of a caller's colors, keep Material for that call.
 */
internal fun glassSliderEligible(
    componentEnabled: Boolean,
    glassAllowed: Boolean,
    translucentFallback: Boolean,
    hasCustomColors: Boolean,
): Boolean = componentEnabled && glassAllowed && !translucentFallback && !hasCustomColors

/**
 * The value slider used on settings screens and in dialogs: [LiquidGlassSlider]
 * when the effect is on and supported, Material [Slider] otherwise — same
 * signature as Material's, so a call site can move between them by changing the
 * import.
 *
 * This is the piece that makes glass safe inside the recorded app layer. The
 * slider's thumb refracts the rail the slider records itself, so it has real
 * content to bend even where the inherited backdrop has to be ignored; that
 * inherited backdrop is filtered per surface by [rememberOuterBackdropSampler]
 * inside [LiquidGlassSlider], which drops it exactly when the surface sits inside
 * the node recording it. Settings screens are all inside the root capture, so
 * there the thumb refracts its own rail and nothing else — the library's look, no
 * page refraction, and no RenderNode cycle.
 *
 * Detents follow Material's meaning of [steps]: 0 is continuous, 1 adds a single
 * midpoint stop, and values are snapped before they are reported.
 */
@Composable
fun GlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors? = null,
    activeColor: Color = Color.Unspecified,
    inactiveColor: Color = Color.Unspecified,
    backdrop: Backdrop? = LocalAppBackdrop.current,
    config: GlassEffectConfig = LocalGlassEffectConfig.current,
    component: GlassComponent = GlassComponent.SETTINGS_CONTROLS,
) {
    val eligible = glassSliderEligible(
        componentEnabled = config.isEnabledFor(component),
        glassAllowed = isGlassAllowed(),
        translucentFallback = shouldUseTranslucentGlassFallback(
            config.style,
            isRenderEffectSupported(),
        ),
        hasCustomColors = colors != null,
    )

    if (!eligible) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            colors = colors ?: SliderDefaults.colors(),
        )
        return
    }

    LiquidGlassSlider(
        value = { value },
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        onValueChangeFinished = onValueChangeFinished,
        enabled = enabled,
        steps = steps,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        config = config,
        backdrop = backdrop,
        component = component,
    )
}
