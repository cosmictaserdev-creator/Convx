package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio3To4Fill (dualtone)
 * Viewport: 17.5879 x 22.9785
 */
public val SfSymbols.Dualtone.SFRectangleRatio3To4Fill: ImageVector
    get() {
        if (_sFRectangleRatio3To4Fill != null) {
            return _sFRectangleRatio3To4Fill!!
        }
        _sFRectangleRatio3To4Fill = sfIcon(
            name = "Dualtone.SFRectangleRatio3To4Fill",
            viewportWidth = 17.5879f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L13.418 22.9785C15.9473 22.9785 17.2266 21.709 17.2266 19.2188L17.2266 3.78906C17.2266 1.29883 15.9473 0.0292969 13.418 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio3To4Fill!!
    }

private var _sFRectangleRatio3To4Fill: ImageVector? = null
