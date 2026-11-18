package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio9To16 (dualtone)
 * Viewport: 15.2734 x 22.9785
 */
public val SfSymbols.Dualtone.SFRectangleRatio9To16: ImageVector
    get() {
        if (_sFRectangleRatio9To16 != null) {
            return _sFRectangleRatio9To16!!
        }
        _sFRectangleRatio9To16 = sfIcon(
            name = "Dualtone.SFRectangleRatio9To16",
            viewportWidth = 15.2734f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L11.1133 22.9785C13.6426 22.9785 14.9121 21.709 14.9121 19.2188L14.9121 3.78906C14.9121 1.29883 13.6426 0.0292969 11.1133 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.83789 21.25C2.4707 21.25 1.72852 20.5273 1.72852 19.1309L1.72852 3.87695C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L11.084 1.75781C12.4121 1.75781 13.1836 2.48047 13.1836 3.87695L13.1836 19.1309C13.1836 20.5273 12.4121 21.25 11.084 21.25Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio9To16!!
    }

private var _sFRectangleRatio9To16: ImageVector? = null
