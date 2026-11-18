package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareTophalfFilled (dualtone)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Dualtone.SFSquareTophalfFilled: ImageVector
    get() {
        if (_sFSquareTophalfFilled != null) {
            return _sFSquareTophalfFilled!!
        }
        _sFSquareTophalfFilled = sfIcon(
            name = "Dualtone.SFSquareTophalfFilled",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 19.1797C0 21.709 1.2793 22.9785 3.75977 22.9785L19.1895 22.9785C21.6797 22.9785 22.959 21.709 22.959 19.1797L22.959 3.82812C22.959 1.30859 21.6895 0.0292969 19.1895 0.0292969L3.75977 0.0292969C1.25977 0.0292969 0 1.30859 0 3.82812ZM1.72852 19.1504L1.72852 11.5039L21.2305 11.5039L21.2305 19.1504C21.2305 20.4883 20.5078 21.25 19.1016 21.25L3.85742 21.25C2.45117 21.25 1.72852 20.4883 1.72852 19.1504Z", fillAlpha = 0.85f)
        }
        return _sFSquareTophalfFilled!!
    }

private var _sFSquareTophalfFilled: ImageVector? = null
