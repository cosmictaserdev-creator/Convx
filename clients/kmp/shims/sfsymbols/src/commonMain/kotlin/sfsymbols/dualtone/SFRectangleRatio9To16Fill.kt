package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio9To16Fill (dualtone)
 * Viewport: 15.2734 x 22.9785
 */
public val SfSymbols.Dualtone.SFRectangleRatio9To16Fill: ImageVector
    get() {
        if (_sFRectangleRatio9To16Fill != null) {
            return _sFRectangleRatio9To16Fill!!
        }
        _sFRectangleRatio9To16Fill = sfIcon(
            name = "Dualtone.SFRectangleRatio9To16Fill",
            viewportWidth = 15.2734f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L11.1133 22.9785C13.6426 22.9785 14.9121 21.709 14.9121 19.2188L14.9121 3.78906C14.9121 1.29883 13.6426 0.0292969 11.1133 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio9To16Fill!!
    }

private var _sFRectangleRatio9To16Fill: ImageVector? = null
