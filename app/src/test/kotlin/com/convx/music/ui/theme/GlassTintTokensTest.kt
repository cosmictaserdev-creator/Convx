/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlassTintTokensTest {

    @Test
    fun `baseSurfaceColor matches spec values`() {
        // §1: Dark #171717, Light #F5F5F7
        assertEquals(Color(0xFF171717), GlassTintTokens.baseSurfaceColor(isDark = true))
        assertEquals(Color(0xFFF5F5F7), GlassTintTokens.baseSurfaceColor(isDark = false))
    }

    @Test
    fun `neutralTintColor matches desaturated neutral gray spec`() {
        // §3.3: Dark carries no wash (the glass base lifts it), Light rgba(60, 60, 67, 0.05)
        val darkNeutral = GlassTintTokens.neutralTintColor(isDark = true)
        val lightNeutral = GlassTintTokens.neutralTintColor(isDark = false)

        // Compose packs alpha into 8 bits: 0.06*255 rounds to 15/255.
        assertEquals(0f, darkNeutral.alpha, 0.005f)
        assertEquals(0.05f, lightNeutral.alpha, 0.005f)

        // Must never have strong saturation/red hue
        assertNotEquals(Color.Red, darkNeutral)
        assertNotEquals(Color.Red, lightNeutral)
    }

    @Test
    fun `heroTintOpacity has plus 10 percent boost`() {
        // §3.2: 0.30 dark, 0.25 light
        assertEquals(0f, GlassTintTokens.heroTintOpacity(isDark = true), 0.005f)
        assertEquals(0.25f, GlassTintTokens.heroTintOpacity(isDark = false), 0.005f)
    }

    @Test
    fun `selectedRedColor matches iOS systemRed spec`() {
        // §4.4: Dark #FF453A, Light #FF3B30
        assertEquals(Color(0xFFFF453A), GlassTintTokens.selectedRedColor(isDark = true))
        assertEquals(Color(0xFFFF3B30), GlassTintTokens.selectedRedColor(isDark = false))
    }

    @Test
    fun `borderColor matches hairline spec`() {
        // §2 & §5B: Dark rgba(255,255,255,0.12), Light rgba(0,0,0,0.08)
        val darkBorder = GlassTintTokens.borderColor(isDark = true)
        val lightBorder = GlassTintTokens.borderColor(isDark = false)

        assertEquals(0.12f, darkBorder.alpha, 0.005f)
        assertEquals(0.08f, lightBorder.alpha, 0.005f)
    }

    @Test
    fun `glassContentColor for selected state uses system red with dimming`() {
        val selectedDark = GlassTintTokens.glassContentColor(
            isDark = true,
            isSelected = true,
            overrideRedForSelection = true
        )
        val selectedLight = GlassTintTokens.glassContentColor(
            isDark = false,
            isSelected = true,
            overrideRedForSelection = true
        )

        assertEquals(GlassTintTokens.AccentSelectedRedDark.copy(alpha = GlassTintTokens.IconDimOpacity), selectedDark)
        assertEquals(GlassTintTokens.AccentSelectedRedLight, selectedLight)
        assertEquals(1.0f, selectedLight.alpha, 0.005f)
    }

    @Test
    fun `glassContentColor in light mode produces opaque blackish with no transparency`() {
        val iconLight = GlassTintTokens.glassContentColor(isDark = false, isSelected = false)
        val titleLight = GlassTintTokens.glassContentColor(isDark = false, isSelected = false, isTitle = true)
        val tabLabelLight = GlassTintTokens.glassContentColor(isDark = false, isSelected = false, isUnselectedTabLabel = true)

        assertEquals(GlassTintTokens.IconOpaqueBlackishLight, iconLight)
        assertEquals(1.0f, iconLight.alpha, 0.005f)

        assertEquals(GlassTintTokens.TitleOpaqueBlackishLight, titleLight)
        assertEquals(1.0f, titleLight.alpha, 0.005f)

        assertEquals(GlassTintTokens.TabLabelOpaqueBlackishLight, tabLabelLight)
        assertEquals(1.0f, tabLabelLight.alpha, 0.005f)
    }

    @Test
    fun `glassContentColor for selected title and tab label uses selected tab label opacity in dark mode`() {
        val selectedTitle = GlassTintTokens.glassContentColor(
            isDark = true,
            isSelected = true,
            isTitle = true,
            overrideRedForSelection = true
        )
        assertEquals(0.92f, selectedTitle.alpha, 0.005f)
    }

    @Test
    fun `glassContentColor for unselected state does not produce red when tint is unspecified or neutral`() {
        val unselectedDark = GlassTintTokens.glassContentColor(
            isDark = true,
            activeTint = Color.Unspecified,
            isSelected = false
        )
        val unselectedLight = GlassTintTokens.glassContentColor(
            isDark = false,
            activeTint = Color.Unspecified,
            isSelected = false
        )

        assertEquals(GlassTintTokens.IconDimOpacity, unselectedDark.alpha, 0.005f)
        assertEquals(1.0f, unselectedLight.alpha, 0.005f)

        // Must not be red
        assertNotEquals(Color(0xFFFF453A), unselectedDark.copy(alpha = 1f))
        assertNotEquals(Color(0xFFFF3B30), unselectedLight.copy(alpha = 1f))
    }

    @Test
    fun `glassContentColor dim opacities match specification in dark mode`() {
        val iconColor = GlassTintTokens.glassContentColor(isDark = true, isSelected = false)
        val titleColor = GlassTintTokens.glassContentColor(isDark = true, isSelected = false, isTitle = true)
        val unselectedTabLabel = GlassTintTokens.glassContentColor(isDark = true, isSelected = false, isUnselectedTabLabel = true)

        assertEquals(0.85f, iconColor.alpha, 0.005f)
        assertEquals(0.92f, titleColor.alpha, 0.005f)
        assertEquals(0.75f, unselectedTabLabel.alpha, 0.005f)
    }

    @Test
    fun `pluckOpacity matches theme and hero context specification`() {
        assertEquals(0.30f, GlassTintTokens.pluckOpacity(isDark = true, isHeroContext = false), 0.005f)
        assertEquals(0.48f, GlassTintTokens.pluckOpacity(isDark = true, isHeroContext = true), 0.005f)
        assertEquals(0.40f, GlassTintTokens.pluckOpacity(isDark = false, isHeroContext = false), 0.005f)
        assertEquals(0.55f, GlassTintTokens.pluckOpacity(isDark = false, isHeroContext = true), 0.005f)
    }

    @Test
    fun `puckOpacity matches theme and hero context specification`() {
        assertEquals(0.55f, GlassTintTokens.puckOpacity(isDark = true, isHeroContext = false), 0.005f)
        assertEquals(0.80f, GlassTintTokens.puckOpacity(isDark = true, isHeroContext = true), 0.005f)
        assertEquals(0.45f, GlassTintTokens.puckOpacity(isDark = false, isHeroContext = false), 0.005f)
        assertEquals(0.70f, GlassTintTokens.puckOpacity(isDark = false, isHeroContext = true), 0.005f)
    }
}
