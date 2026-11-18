package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareRighthalfFilled (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareRighthalfFilled: ImageVector
    get() {
        if (_sFSquareRighthalfFilled != null) {
            return _sFSquareRighthalfFilled!!
        }
        _sFSquareRighthalfFilled = sfIcon(
            name = "Monochrome.SFSquareRighthalfFilled",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 19.2188C0 21.709 1.26953 22.9785 3.79883 22.9785L19.1504 22.9785C21.6699 22.9785 22.959 21.7188 22.959 19.2188L22.959 3.78906C22.959 1.28906 21.6699 0.0292969 19.1504 0.0292969L3.79883 0.0292969C1.26953 0.0292969 0 1.29883 0 3.78906ZM1.72852 19.1309L1.72852 3.87695C1.72852 2.48047 2.5 1.75781 3.83789 1.75781L11.4746 1.75781L11.4746 21.25L3.83789 21.25C2.5 21.25 1.72852 20.5273 1.72852 19.1309Z", fillAlpha = 0.85f)
        }
        return _sFSquareRighthalfFilled!!
    }

private var _sFSquareRighthalfFilled: ImageVector? = null
