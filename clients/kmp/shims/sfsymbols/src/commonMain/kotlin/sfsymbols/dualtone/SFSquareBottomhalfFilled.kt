package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareBottomhalfFilled (dualtone)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Dualtone.SFSquareBottomhalfFilled: ImageVector
    get() {
        if (_sFSquareBottomhalfFilled != null) {
            return _sFSquareBottomhalfFilled!!
        }
        _sFSquareBottomhalfFilled = sfIcon(
            name = "Dualtone.SFSquareBottomhalfFilled",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 19.1797C0 21.6992 1.25977 22.9785 3.75977 22.9785L19.1895 22.9785C21.6895 22.9785 22.959 21.6992 22.959 19.1797L22.959 3.82812C22.959 1.29883 21.6797 0.0292969 19.1895 0.0292969L3.75977 0.0292969C1.2793 0.0292969 0 1.29883 0 3.82812ZM1.72852 11.5039L1.72852 3.85742C1.72852 2.51953 2.45117 1.75781 3.85742 1.75781L19.1016 1.75781C20.5078 1.75781 21.2305 2.51953 21.2305 3.85742L21.2305 11.5039Z", fillAlpha = 0.85f)
        }
        return _sFSquareBottomhalfFilled!!
    }

private var _sFSquareBottomhalfFilled: ImageVector? = null
