package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio3To4 (monochrome)
 * Viewport: 17.5879 x 22.9785
 */
public val SfSymbols.Monochrome.SFRectangleRatio3To4: ImageVector
    get() {
        if (_sFRectangleRatio3To4 != null) {
            return _sFRectangleRatio3To4!!
        }
        _sFRectangleRatio3To4 = sfIcon(
            name = "Monochrome.SFRectangleRatio3To4",
            viewportWidth = 17.5879f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L13.418 22.9785C15.9473 22.9785 17.2266 21.709 17.2266 19.2188L17.2266 3.78906C17.2266 1.29883 15.9473 0.0292969 13.418 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.83789 21.25C2.4707 21.25 1.72852 20.5273 1.72852 19.1309L1.72852 3.87695C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L13.3887 1.75781C14.7266 1.75781 15.4883 2.48047 15.4883 3.87695L15.4883 19.1309C15.4883 20.5273 14.7266 21.25 13.3887 21.25Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio3To4!!
    }

private var _sFRectangleRatio3To4: ImageVector? = null
