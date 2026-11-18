package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio16To9 (dualtone)
 * Viewport: 23.3203 x 14.9512
 */
public val SfSymbols.Dualtone.SFRectangleRatio16To9: ImageVector
    get() {
        if (_sFRectangleRatio16To9 != null) {
            return _sFRectangleRatio16To9!!
        }
        _sFRectangleRatio16To9 = sfIcon(
            name = "Dualtone.SFRectangleRatio16To9",
            viewportWidth = 23.3203f,
            viewportHeight = 14.9512f
        ) {
            addSfPath("M0 3.83789L0 11.1426C0 13.6719 1.2793 14.9512 3.75977 14.9512L19.1895 14.9512C21.6797 14.9512 22.959 13.6719 22.959 11.1426L22.959 3.83789C22.959 1.31836 21.6895 0.0292969 19.1895 0.0292969L3.75977 0.0292969C1.25977 0.0292969 0 1.31836 0 3.83789ZM1.72852 3.86719C1.72852 2.50977 2.45117 1.76758 3.85742 1.76758L19.1016 1.76758C20.5078 1.76758 21.2305 2.50977 21.2305 3.86719L21.2305 11.1133C21.2305 12.4512 20.5078 13.2129 19.1016 13.2129L3.85742 13.2129C2.45117 13.2129 1.72852 12.4512 1.72852 11.1133Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio16To9!!
    }

private var _sFRectangleRatio16To9: ImageVector? = null
