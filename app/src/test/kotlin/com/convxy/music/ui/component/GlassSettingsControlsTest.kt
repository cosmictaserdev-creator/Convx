package com.convxy.music.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the routing decisions behind [LiquidGlassSwitch] and
 * [GlassSlider] — the two entry points settings screens use to get the library's
 * liquid toggle and liquid slider.
 *
 * Both are gates with several independent reasons to say no, and getting one
 * wrong is user-visible in a way that is easy to miss: a read-out switch that
 * looks live and eats the row's click, a disabled toggle that can still be
 * dragged, a slider whose caller-supplied Material colours are silently dropped.
 */
class GlassSettingsControlsTest {

    private fun switchEligible(
        componentEnabled: Boolean = true,
        glassAllowed: Boolean = true,
        enabled: Boolean = true,
        hasHandler: Boolean = true,
        hasThumbContent: Boolean = false,
        hasColors: Boolean = false,
        translucentFallback: Boolean = false,
    ) = liquidSwitchEligible(
        componentEnabled = componentEnabled,
        glassAllowed = glassAllowed,
        enabled = enabled,
        hasHandler = hasHandler,
        hasThumbContent = hasThumbContent,
        hasColors = hasColors,
        translucentFallback = translucentFallback,
    )

    @Test
    fun `a plain enabled settings switch takes the liquid glass path`() {
        assertTrue(switchEligible())
    }

    @Test
    fun `switch declines when the user turned settings glass off`() {
        assertFalse(switchEligible(componentEnabled = false))
    }

    @Test
    fun `switch declines when the platform cannot render glass`() {
        assertFalse(switchEligible(glassAllowed = false))
    }

    @Test
    fun `switch declines when disabled, since the liquid toggle has no disabled state`() {
        assertFalse(switchEligible(enabled = false))
    }

    @Test
    fun `switch declines a read-out, whose row owns the click`() {
        // LiquidToggle's thumb installs a drag detector that consumes the down
        // event; with no handler it would swallow the row's click and call nothing.
        assertFalse(switchEligible(hasHandler = false))
    }

    @Test
    fun `a thumb icon or custom colors do not disqualify the liquid path`() {
        // SwitchPreference -- the row behind most settings toggles -- passes a
        // check/close icon. The fallback ignores icons and Material colors anyway
        // (GlassSwitchCompat says so in its own doc), so gating the liquid path on
        // them preserved nothing and left every one of those rows on the old
        // switch: the effect shipped and nothing on screen changed.
        assertTrue(switchEligible(hasThumbContent = true))
        assertTrue(switchEligible(hasColors = true))
    }

    @Test
    fun `switch declines the cheap style, where a glass thumb is just a white dot`() {
        assertFalse(switchEligible(translucentFallback = true))
    }

    private fun sliderEligible(
        componentEnabled: Boolean = true,
        glassAllowed: Boolean = true,
        translucentFallback: Boolean = false,
        hasCustomColors: Boolean = false,
    ) = glassSliderEligible(
        componentEnabled = componentEnabled,
        glassAllowed = glassAllowed,
        translucentFallback = translucentFallback,
        hasCustomColors = hasCustomColors,
    )

    @Test
    fun `a plain settings slider takes the liquid glass path`() {
        assertTrue(sliderEligible())
    }

    @Test
    fun `slider declines for the same three global reasons as the switch`() {
        assertFalse(sliderEligible(componentEnabled = false))
        assertFalse(sliderEligible(glassAllowed = false))
        assertFalse(sliderEligible(translucentFallback = true))
    }

    @Test
    fun `slider declines Material colors, which the glass rail has no equivalent for`() {
        assertFalse(sliderEligible(hasCustomColors = true))
    }

    @Test
    fun `menus are a glass component the user can turn off on their own`() {
        assertTrue(GlassEffectConfig().isEnabledFor(GlassComponent.MENU))
        assertFalse(GlassEffectConfig(menuEnabled = false).isEnabledFor(GlassComponent.MENU))
        assertFalse(
            GlassEffectConfig(globalEnabled = false).isEnabledFor(GlassComponent.MENU),
        )
    }

    @Test
    fun `an enabled menu alone makes capturing the app backdrop worth it`() {
        // Menus sample the app backdrop like the other chrome (the overlay sits next
        // to the recorded box), unlike settings controls, which refract a backdrop
        // they record themselves and are excluded from this decision.
        assertTrue(
            GlassEffectConfig(
                playerEnabled = false,
                miniPlayerEnabled = false,
                navBarEnabled = false,
                sidePanelEnabled = false,
                menuEnabled = true,
            ).anyComponentEnabled,
        )
    }
}
