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



package zone.ien.hig.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import zone.ien.hig.Accessibility
import zone.ien.hig.InternalCupertinoApi
import zone.ien.hig.isHighContrastEnabled

/**
 * A color scheme for the Cupertino design system.
 *
 * @param isDark whether the theme is dark or light
 * @param accent the accent color used for interactive elements
 * @param label the primary label color
 * @param secondaryLabel the secondary label color
 * @param tertiaryLabel the tertiary label color
 * @param quaternaryLabel the quaternary label color
 * @param link the link color
 * @param placeholderText the placeholder text color
 * @param separator the separator color
 * @param opaqueSeparator the opaque separator color
 * @param systemBackground the system background color
 * @param secondarySystemBackground the secondary system background color
 * @param tertiarySystemBackground the tertiary system background color
 * @param systemGroupedBackground the system grouped background color
 * @param secondarySystemGroupedBackground the secondary system grouped background color
 * @param tertiarySystemGroupedBackground the tertiary system grouped background color
 * @param systemFill the system fill color
 * @param secondarySystemFill the secondary system fill color
 * @param tertiarySystemFill the tertiary system fill color
 * @param quaternarySystemFill the quaternary system fill color
 * @param lightText the light text color
 * @param darkText the dark text color
 */
@Immutable
class ColorScheme internal constructor(
    val isDark: Boolean,
    val accent: Color,
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val quaternaryLabel: Color,
    val link: Color,
    val placeholderText: Color,
    val separator: Color,
    val opaqueSeparator: Color,
    val systemBackground: Color,
    val secondarySystemBackground: Color,
    val tertiarySystemBackground: Color,
    val systemGroupedBackground: Color,
    val secondarySystemGroupedBackground: Color,
    val tertiarySystemGroupedBackground: Color,
    val systemFill: Color,
    val secondarySystemFill: Color,
    val tertiarySystemFill: Color,
    val quaternarySystemFill: Color,
    val lightText: Color,
    val darkText: Color
) {
    /**
     * Creates a copy of this ColorScheme with the specified values replaced.
     *
     * @param accent the new accent color
     * @param label the new label color
     * @param secondaryLabel the new secondary label color
     * @param tertiaryLabel the new tertiary label color
     * @param quaternaryLabel the new quaternary label color
     * @param link the new link color
     * @param placeholderText the new placeholder text color
     * @param separator the new separator color
     * @param opaqueSeparator the new opaque separator color
     * @param systemBackground the new system background color
     * @param secondarySystemBackground the new secondary system background color
     * @param tertiarySystemBackground the new tertiary system background color
     * @param systemGroupedBackground the new system grouped background color
     * @param secondarySystemGroupedBackground the new secondary system grouped background color
     * @param tertiarySystemGroupedBackground the new tertiary system grouped background color
     * @param systemFill the new system fill color
     * @param secondarySystemFill the new secondary system fill color
     * @param tertiarySystemFill the new tertiary system fill color
     * @param quaternarySystemFill the new quaternary system fill color
     * @param lightText the new light text color
     * @param darkText the new dark text color
     */
    fun copy(
        accent: Color = this.accent,
        label: Color = this.label,
        secondaryLabel: Color = this.secondaryLabel,
        tertiaryLabel: Color = this.tertiaryLabel,
        quaternaryLabel: Color = this.quaternaryLabel,
        link: Color = this.link,
        placeholderText: Color = this.placeholderText,
        separator: Color = this.separator,
        opaqueSeparator: Color = this.opaqueSeparator,
        systemBackground: Color = this.systemBackground,
        secondarySystemBackground: Color = this.secondarySystemBackground,
        tertiarySystemBackground: Color = this.tertiarySystemBackground,
        systemGroupedBackground: Color = this.systemGroupedBackground,
        secondarySystemGroupedBackground: Color = this.secondarySystemGroupedBackground,
        tertiarySystemGroupedBackground: Color = this.tertiarySystemGroupedBackground,
        systemFill: Color = this.systemFill,
        secondarySystemFill: Color = this.secondarySystemFill,
        tertiarySystemFill: Color = this.tertiarySystemFill,
        quaternarySystemFill: Color = this.quaternarySystemFill,
        lightText: Color = this.lightText,
        darkText: Color = this.darkText
    ) = ColorScheme(
        isDark = isDark,
        accent = accent,
        label = label,
        secondaryLabel = secondaryLabel,
        tertiaryLabel = tertiaryLabel,
        quaternaryLabel = quaternaryLabel,
        link = link,
        placeholderText = placeholderText,
        separator = separator,
        opaqueSeparator = opaqueSeparator,
        systemBackground = systemBackground,
        secondarySystemBackground = secondarySystemBackground,
        tertiarySystemBackground = tertiarySystemBackground,
        systemGroupedBackground = systemGroupedBackground,
        secondarySystemGroupedBackground = secondarySystemGroupedBackground,
        tertiarySystemGroupedBackground = tertiarySystemGroupedBackground,
        systemFill = systemFill,
        secondarySystemFill = secondarySystemFill,
        tertiarySystemFill = tertiarySystemFill,
        quaternarySystemFill = quaternarySystemFill,
        lightText = lightText,
        darkText = darkText
    )
}

fun lightColorScheme(
    accent: Color = ColorSchemeTokens.lightAccent,
    label: Color = ColorSchemeTokens.lightLabel,
    secondaryLabel: Color = ColorSchemeTokens.lightSecondaryLabel,
    tertiaryLabel: Color = ColorSchemeTokens.lightTertiaryLabel,
    quaternaryLabel: Color = ColorSchemeTokens.lightQuaternaryLabel,
    link: Color = ColorSchemeTokens.lightLink,
    placeholderText: Color = ColorSchemeTokens.lightPlaceholderText,
    separator: Color = ColorSchemeTokens.lightSeparator,
    opaqueSeparator: Color = ColorSchemeTokens.lightOpaqueSeparator,
    systemBackground: Color = ColorSchemeTokens.lightSystemBackground,
    secondarySystemBackground: Color = ColorSchemeTokens.lightSecondarySystemBackground,
    tertiarySystemBackground: Color = ColorSchemeTokens.lightTertiarySystemBackground,
    systemGroupedBackground: Color = ColorSchemeTokens.lightSystemGroupedBackground,
    secondarySystemGroupedBackground: Color = ColorSchemeTokens.lightSecondarySystemGroupedBackground,
    tertiarySystemGroupedBackground: Color = ColorSchemeTokens.lightTertiarySystemGroupedBackground,
    systemFill: Color = ColorSchemeTokens.lightSystemFill,
    secondarySystemFill: Color = ColorSchemeTokens.lightSecondarySystemFill,
    tertiarySystemFill: Color = ColorSchemeTokens.lightTertiarySystemFill,
    quaternarySystemFill: Color = ColorSchemeTokens.lightQuaternarySystemFill,
    lightText: Color = ColorSchemeTokens.lightText,
    darkText: Color = ColorSchemeTokens.darkText
): ColorScheme = ColorScheme(
    isDark = false,
    accent = accent,
    label = label,
    secondaryLabel = secondaryLabel,
    tertiaryLabel = tertiaryLabel,
    quaternaryLabel = quaternaryLabel,
    placeholderText = placeholderText,
    systemFill = systemFill,
    secondarySystemFill = secondarySystemFill,
    tertiarySystemFill = tertiarySystemFill,
    quaternarySystemFill = quaternarySystemFill,
    separator = separator,
    opaqueSeparator = opaqueSeparator,
    link = link,
    systemGroupedBackground = systemGroupedBackground,
    secondarySystemGroupedBackground =secondarySystemGroupedBackground,
    tertiarySystemGroupedBackground =tertiarySystemGroupedBackground,
    systemBackground = systemBackground,
    secondarySystemBackground = secondarySystemBackground,
    tertiarySystemBackground = tertiarySystemBackground,
    lightText = lightText,
    darkText = darkText

)

fun darkColorScheme(
    accent: Color = ColorSchemeTokens.darkAccent,
    label: Color = ColorSchemeTokens.darkLabel,
    secondaryLabel: Color = ColorSchemeTokens.darkSecondaryLabel,
    tertiaryLabel: Color = ColorSchemeTokens.darkTertiaryLabel,
    quaternaryLabel: Color = ColorSchemeTokens.darkQuaternaryLabel,
    link: Color = ColorSchemeTokens.darkLink,
    placeholderText: Color = ColorSchemeTokens.darkPlaceholderText,
    separator: Color = ColorSchemeTokens.darkSeparator,
    opaqueSeparator: Color = ColorSchemeTokens.darkOpaqueSeparator,
    systemBackground: Color = ColorSchemeTokens.darkSystemBackground,
    secondarySystemBackground: Color = ColorSchemeTokens.darkSecondarySystemBackground,
    tertiarySystemBackground: Color = ColorSchemeTokens.darkTertiarySystemBackground,
    systemGroupedBackground: Color = ColorSchemeTokens.darkSystemGroupedBackground,
    secondarySystemGroupedBackground: Color = ColorSchemeTokens.darkSecondarySystemGroupedBackground,
    tertiarySystemGroupedBackground: Color = ColorSchemeTokens.darkTertiarySystemGroupedBackground,
    systemFill: Color = ColorSchemeTokens.darkSystemFill,
    secondarySystemFill: Color = ColorSchemeTokens.darkSecondarySystemFill,
    tertiarySystemFill: Color = ColorSchemeTokens.darkTertiarySystemFill,
    quaternarySystemFill: Color = ColorSchemeTokens.darkQuaternarySystemFill,
    lightText: Color = ColorSchemeTokens.lightText,
    darkText: Color = ColorSchemeTokens.darkText
): ColorScheme = ColorScheme(
    isDark = true,
    accent = accent,
    label = label,
    secondaryLabel = secondaryLabel,
    tertiaryLabel = tertiaryLabel,
    quaternaryLabel = quaternaryLabel,
    link = link,
    placeholderText = placeholderText,
    separator = separator,
    opaqueSeparator = opaqueSeparator,
    systemBackground = systemBackground,
    secondarySystemBackground = secondarySystemBackground,
    tertiarySystemBackground = tertiarySystemBackground,
    systemGroupedBackground = systemGroupedBackground,
    secondarySystemGroupedBackground =secondarySystemGroupedBackground,
    tertiarySystemGroupedBackground =tertiarySystemGroupedBackground,
    systemFill = systemFill,
    secondarySystemFill = secondarySystemFill,
    tertiarySystemFill = tertiarySystemFill,
    quaternarySystemFill = quaternarySystemFill,
    lightText = lightText,
    darkText = darkText
)

private val defaultCupertinoColorScheme = lightColorScheme()

@InternalCupertinoApi
val LocalColorScheme = staticCompositionLocalOf {
    defaultCupertinoColorScheme
}

@Composable
@InternalCupertinoApi
fun isInitializedCupertinoTheme(): Boolean {
    return LocalColorScheme.current !== defaultCupertinoColorScheme
}


@Composable
@ReadOnlyComposable
internal fun isDark() =
    CupertinoTheme.colorScheme.isDark

internal object ColorSchemeTokens {
    val lightAccent: Color = CupertinoColors.systemBlue(false)
    val lightLabel: Color = Color.Black
    val lightSecondaryLabel: Color = color(
        accessible = Color(0xCC3C3C43),
        default = Color(0x993C3C43)
    )
    val lightTertiaryLabel: Color = color(
        accessible = Color(0xB23C3C43),
        default = Color(0x4C3C3C43)
    )
    val lightQuaternaryLabel: Color = color(
        accessible = Color(0x8C3C3C43),
        default =  Color(0x2D3C3C43)
    )
    val lightPlaceholderText: Color = color(
        accessible = Color(0xB23C3C43),
        default = Color(0x4C3C3C43)
    )
    val lightLink: Color = Color(0xFF007aFF)
    val lightSeparator: Color = color(
        accessible = Color(0x1E3C3C43),
        default = Color(0x1E3C3C43)
    )
    val lightOpaqueSeparator: Color = color(
        accessible = Color(0xffc6c6c8),
        default = Color(0xffc6c6c8)
    )
    val lightSystemBackground: Color = Color.White
    val lightSecondarySystemBackground: Color = color(
        accessible = Color(0xffebebf0),
        default = Color(0xfff2f2f7)
    )
    val lightTertiarySystemBackground: Color = Color.White
    val lightSystemGroupedBackground: Color = color(
        accessible = Color(0xffebebf0),
        default = Color(0xfff2f2f7)
    )
    val lightSecondarySystemGroupedBackground: Color = Color.White
    val lightTertiarySystemGroupedBackground: Color = color(
        accessible = Color(0xffebebf0),
        default = Color(0xfff2f2f7)
    )
    val lightSystemFill: Color = color(
        accessible = Color(0x47787880),
        default = Color(0x33787880)
    )
    val lightSecondarySystemFill: Color = color(
        accessible = Color(0x3D787880),
        default = Color(0x28787880)
    )
    val lightTertiarySystemFill: Color = color(
        accessible = Color(0x33767680),
        default = Color(0x1E767680)
    )
    val lightQuaternarySystemFill: Color = color(
        accessible = Color(0x28747480),
        default = Color(0x14747480)
    )

    val darkAccent: Color = CupertinoColors.systemBlue(true)
    val darkLabel: Color = Color.White
    val darkSecondaryLabel: Color = color(
        accessible = Color(0xb2ebebf5),
        default = Color(0x99ebebf5)
    )
    val darkTertiaryLabel: Color =  color(
        accessible = Color(0x8cebebf5),
        default = Color(0x4cebebf5)
    )
    val darkQuaternaryLabel: Color =  color(
        accessible = Color(0x66ebebf5),
        default = Color(0x28ebebf5)
    )
    val darkLink: Color = Color(0xff0984ff)
    val darkPlaceholderText: Color =  color(
        accessible = Color(0x8cebebf5),
        default = Color(0x4cebebf5)
    )
    val darkSeparator: Color = color(
        accessible = Color(0x7F545458),
        default = Color(0x7F545458)
    )
    val darkOpaqueSeparator: Color = color(
        accessible = Color(0xff38383a),
        default = Color(0xff38383a)
    )
    val darkSystemBackground: Color = Color.Black
    val darkSecondarySystemBackground: Color = color(
        accessible = Color(0xff242426),
        default = Color(0xff1c1c1e)
    )
    val darkTertiarySystemBackground: Color = color(
        accessible = Color(0xff363638),
        default = Color(0xff2c2c2e)
    )
    val darkSystemGroupedBackground: Color = Color.Black
    val darkSecondarySystemGroupedBackground: Color = color(
        accessible = Color(0xff242426),
        default = Color(0xff1c1c1e)
    )
    val darkTertiarySystemGroupedBackground: Color = color(
        accessible = Color(0xff363638),
        default = Color(0xff2c2c2e)
    )
    val darkSystemFill: Color = color(
        accessible = Color(0x70787880),
        default = Color(0x5b787880)
    )
    val darkSecondarySystemFill: Color = color(
        accessible = Color(0x66787880),
        default = Color(0x51787880)
    )
    val darkTertiarySystemFill: Color = color(
        accessible = Color(0x51767680),
        default = Color(0x3d767680)
    )
    val darkQuaternarySystemFill: Color = color(
        accessible = Color(0x42767680),
        default = Color(0x2d767680)
    )

    val lightText: Color = Color(0x99FFFFFF)
    val darkText: Color = Color(0xFF000000)

    private fun color(
        accessible: Color,
        default: Color
    ): Color = if (Accessibility.isHighContrastEnabled) accessible else default
}