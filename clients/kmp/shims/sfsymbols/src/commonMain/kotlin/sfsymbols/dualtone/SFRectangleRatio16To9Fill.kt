package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio16To9Fill (dualtone)
 * Viewport: 23.3203 x 14.9512
 */
public val SfSymbols.Dualtone.SFRectangleRatio16To9Fill: ImageVector
    get() {
        if (_sFRectangleRatio16To9Fill != null) {
            return _sFRectangleRatio16To9Fill!!
        }
        _sFRectangleRatio16To9Fill = sfIcon(
            name = "Dualtone.SFRectangleRatio16To9Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 14.9512f
        ) {
            addSfPath("M0 3.83789L0 11.1426C0 13.6719 1.2793 14.9512 3.75977 14.9512L19.1895 14.9512C21.6797 14.9512 22.959 13.6719 22.959 11.1426L22.959 3.83789C22.959 1.31836 21.6895 0.0292969 19.1895 0.0292969L3.75977 0.0292969C1.25977 0.0292969 0 1.31836 0 3.83789Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio16To9Fill!!
    }

private var _sFRectangleRatio16To9Fill: ImageVector? = null
