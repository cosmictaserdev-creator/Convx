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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import zone.ien.hig.LocalContentColor
import zone.ien.hig.Accessibility
import zone.ien.hig.isHighContrastEnabled

object CupertinoColors

val CupertinoColors.DefaultAlpha: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalContentColor.current.copy(alpha = .1f)

val CupertinoColors.systemRed: Color
    @Composable
    @ReadOnlyComposable
    get() = systemRed(isDark)

val CupertinoColors.systemOrange: Color
    @Composable
    @ReadOnlyComposable
    get() = systemOrange(isDark)

val CupertinoColors.systemYellow: Color
    @Composable
    @ReadOnlyComposable
    get() = systemYellow(isDark)

val CupertinoColors.systemGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGreen(isDark)

val CupertinoColors.systemBlue: Color
    @Composable
    @ReadOnlyComposable
    get() = systemBlue(isDark)

val CupertinoColors.systemMint: Color
    @Composable
    @ReadOnlyComposable
    get() = systemMint(isDark)

val CupertinoColors.systemTeal: Color
    @Composable
    @ReadOnlyComposable
    get() = systemTeal(isDark)

val CupertinoColors.systemCyan: Color
    @Composable
    @ReadOnlyComposable
    get() = systemCyan(isDark)

val CupertinoColors.systemIndigo: Color
    @Composable
    @ReadOnlyComposable
    get() = systemIndigo(isDark)

val CupertinoColors.systemPurple: Color
    @Composable
    @ReadOnlyComposable
    get() = systemPurple(isDark)

val CupertinoColors.systemPink: Color
    @Composable
    @ReadOnlyComposable
    get() = systemPink(isDark)

val CupertinoColors.systemBrown: Color
    @Composable
    @ReadOnlyComposable
    get() = systemBrown(isDark)

val CupertinoColors.systemGray: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray(isDark)

val CupertinoColors.systemGray2: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray2(isDark)

val CupertinoColors.systemGray3: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray3(isDark)

val CupertinoColors.systemGray4: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray4(isDark)

val CupertinoColors.systemGray5: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray5(isDark)

val CupertinoColors.systemGray6: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray6(isDark)

val CupertinoColors.systemGray7: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray7(isDark)

internal val CupertinoColors.systemGray8: Color
    @Composable
    @ReadOnlyComposable
    get() = systemGray8(isDark)

val CupertinoColors.Black: Color
    get() = Color.Black

val CupertinoColors.Blue: Color
    get() = Color.Blue

val CupertinoColors.Brown: Color
    get() = Color(0xff996633)

val CupertinoColors.Cyan: Color
    get() = Color.Cyan

val CupertinoColors.LightGray: Color
    get() = Color(0xffaaaaaa)

val CupertinoColors.DarkGray: Color
    get() = Color(0xff555555)

val CupertinoColors.Gray: Color
    get() = Color(0xff7f7f7f)

val CupertinoColors.Green: Color
    get() = Color.Green

val CupertinoColors.Magenta: Color
    get() = Color.Magenta

val CupertinoColors.Orange: Color
    get() = Color(0xffff7f00)

val CupertinoColors.Purple: Color
    get() = Color(0xff7f007f)

val CupertinoColors.Red: Color
    get() = Color.Red

val CupertinoColors.White: Color
    get() = Color.White

val CupertinoColors.Yellow: Color
    get() = Color.Yellow

private val isDark: Boolean
    @Composable
    @ReadOnlyComposable
    get() = CupertinoTheme.colorScheme.isDark

fun CupertinoColors.systemRed(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(255, 56, 60)
    } else {
        Color(255, 66, 69)
    }
} else {
    if (!dark) {
        Color(233, 21, 45)
    } else {
        Color(255, 97, 101)
    }
}

fun CupertinoColors.systemOrange(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(255, 141, 40)
    } else {
        Color(255, 146, 48)
    }
} else {
    if (!dark) {
        Color(197, 83, 0)
    } else {
        Color(255, 160, 86)
    }
}

fun CupertinoColors.systemYellow(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(255, 204, 0)
    } else {
        Color(255, 214, 0)
    }
} else {
    if (!dark) {
        Color(161, 106, 0)
    } else {
        Color(254, 223, 67)
    }
}

fun CupertinoColors.systemGreen(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(52, 199, 89)
    } else {
        Color(48, 209, 88)
    }
} else {
    if (!dark) {
        Color(0, 137, 50)
    } else {
        Color(74, 217, 104)
    }
}

fun CupertinoColors.systemMint(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(0, 200, 179)
    } else {
        Color(0, 218, 195)
    }
} else {
    if (!dark) {
        Color(0, 133, 117)
    } else {
        Color(84, 223, 203)
    }
}

fun CupertinoColors.systemTeal(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(0, 195, 208)
    } else {
        Color(0, 210, 224)
    }
} else {
    if (!dark) {
        Color(0, 129, 152)
    } else {
        Color(59, 221, 236)
    }
}

fun CupertinoColors.systemCyan(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(0, 192, 232)
    } else {
        Color(60, 211, 254)
    }
} else {
    if (!dark) {
        Color(0, 126, 174)
    } else {
        Color(109, 217, 255)
    }
}

fun CupertinoColors.systemBlue(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(0, 136, 255)
    } else {
        Color(0, 145, 255)
    }
} else {
    if (!dark) {
        Color(30, 110, 244)
    } else {
        Color(92, 184, 255)
    }
}

fun CupertinoColors.systemIndigo(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(97, 85, 245)
    } else {
        Color(109, 124, 255)
    }
} else {
    if (!dark) {
        Color(86, 74, 222)
    } else {
        Color(167, 170, 255)
    }
}

fun CupertinoColors.systemPurple(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(203, 48, 224)
    } else {
        Color(219, 52, 242)
    }
} else {
    if (!dark) {
        Color(176, 47, 194)
    } else {
        Color(234, 141, 255)
    }
}

fun CupertinoColors.systemPink(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(255, 45, 85)
    } else {
        Color(255, 55, 95)
    }
} else {
    if (!dark) {
        Color(231, 18, 77)
    } else {
        Color(255, 138, 196)
    }
}

fun CupertinoColors.systemBrown(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(172, 127, 94)
    } else {
        Color(183, 138, 102)
    }
} else {
    if (!dark) {
        Color(149, 109, 81)
    } else {
        Color(219, 166, 121)
    }
}

fun CupertinoColors.systemGray(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(142, 142, 147)
    } else {
        Color(142, 142, 147)
    }
} else {
    if (!dark) {
        Color(108, 108, 112)
    } else {
        Color(174, 174, 178)
    }
}

fun CupertinoColors.systemGray2(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(174, 174, 178)
    } else {
        Color(99, 99, 102)
    }
} else {
    if (!dark) {
        Color(142, 142, 147)
    } else {
        Color(124, 124, 128)
    }
}

fun CupertinoColors.systemGray3(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(199, 199, 204)
    } else {
        Color(72, 72, 74)
    }
} else {
    if (!dark) {
        Color(174, 174, 178)
    } else {
        Color(84, 84, 86)
    }
}

fun CupertinoColors.systemGray4(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(209, 209, 214)
    } else {
        Color(58, 58, 60)
    }
} else {
    if (!dark) {
        Color(188, 188, 192)
    } else {
        Color(68, 68, 70)
    }
}

fun CupertinoColors.systemGray5(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(229, 229, 234)
    } else {
        Color(44, 44, 46)
    }
} else {
    if (!dark) {
        Color(216, 216, 220)
    } else {
        Color(54, 54, 56)
    }
}

fun CupertinoColors.systemGray6(
    dark: Boolean,
    highContrast: Boolean = Accessibility.isHighContrastEnabled,
) = if (!highContrast) {
    if (!dark) {
        Color(242, 242, 247)
    } else {
        Color(28, 28, 30)
    }
} else {
    if (!dark) {
        Color(235, 235, 240)
    } else {
        Color(36, 36, 38)
    }
}

fun CupertinoColors.systemGray7(dark: Boolean) =
    if (dark) {
        Color(35, 35, 35)
    } else {
        Color(238, 238, 238)
    }

fun CupertinoColors.systemGray8(dark: Boolean) =
    if (dark) {
        Color(90, 90, 95)
    } else {
        Color(254, 254, 254)
    }
