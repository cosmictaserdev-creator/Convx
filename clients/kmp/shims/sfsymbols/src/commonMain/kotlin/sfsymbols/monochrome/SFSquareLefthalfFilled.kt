package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareLefthalfFilled (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareLefthalfFilled: ImageVector
    get() {
        if (_sFSquareLefthalfFilled != null) {
            return _sFSquareLefthalfFilled!!
        }
        _sFSquareLefthalfFilled = sfIcon(
            name = "Monochrome.SFSquareLefthalfFilled",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785L19.1504 22.9785C21.6797 22.9785 22.959 21.709 22.959 19.2188L22.959 3.78906C22.959 1.29883 21.6797 0.0292969 19.1504 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906ZM11.4746 21.25L11.4746 1.75781L19.1211 1.75781C20.459 1.75781 21.2305 2.48047 21.2305 3.87695L21.2305 19.1309C21.2305 20.5273 20.459 21.25 19.1211 21.25Z", fillAlpha = 0.85f)
        }
        return _sFSquareLefthalfFilled!!
    }

private var _sFSquareLefthalfFilled: ImageVector? = null
