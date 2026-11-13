/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.ios.foundation

import androidx.compose.ui.graphics.Color
import com.convx.music.designsystem.ConvxDesignTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IosDesignTokensTest {
    @Test
    fun lightAndDarkPalettesKeepPlatformBackgroundAndAccentRoles() {
        val light = lightIosColors()
        val dark = darkIosColors()

        assertEquals(Color.White, light.systemBackground)
        assertEquals(Color.Black, dark.systemBackground)
        assertEquals(ConvxDesignTokens.Colors.AccentRed, light.accent)
        assertEquals(ConvxDesignTokens.Colors.AccentRed, dark.accent)
        assertEquals(44f, IosMetrics.ListRowHeight.value)
        assertEquals(26f, IosMetrics.GroupedCornerRadius.value)
    }

    @Test
    fun thinMaterialsUseSaferLabelAndExpectedOpacity() {
        val colors = lightIosColors()

        assertEquals(colors.tertiaryLabel, colors.vibrantLabel(3, IosMaterial.Thin))
        assertEquals(colors.quaternaryLabel, colors.vibrantLabel(3, IosMaterial.Regular))
        assertEquals(0.28f, IosMaterial.UltraThin.fillAlpha())
        assertEquals(0.85f, IosMaterial.Thick.fillAlpha())
    }

    @Test
    fun contrastAndTypographyTokensArePlatformIndependent() {
        assertTrue(meetsContrast(Color.Black, Color.White))
        assertFalse(meetsContrast(Color(0xFF777777), Color.White))
        assertEquals(34f, iosTypography().largeTitle.fontSize.value)
        assertEquals(16f, iosTypography().body.fontSize.value)
    }
}
