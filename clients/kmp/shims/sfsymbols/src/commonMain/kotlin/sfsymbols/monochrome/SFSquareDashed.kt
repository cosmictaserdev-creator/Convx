package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareDashed (monochrome)
 * Viewport: 23.3203 x 22.9785
 */
public val SfSymbols.Monochrome.SFSquareDashed: ImageVector
    get() {
        if (_sFSquareDashed != null) {
            return _sFSquareDashed!!
        }
        _sFSquareDashed = sfIcon(
            name = "Monochrome.SFSquareDashed",
            viewportWidth = 23.3203f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 6.64062L1.72852 6.64062L1.72852 3.87695C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L6.58203 1.75781L6.58203 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906ZM8.13477 1.75781L14.8145 1.75781L14.8145 0.0292969L8.13477 0.0292969ZM21.2305 6.64062L22.959 6.64062L22.959 3.78906C22.959 1.29883 21.6699 0.0292969 19.1504 0.0292969L16.377 0.0292969L16.377 1.75781L19.1211 1.75781C20.459 1.75781 21.2305 2.48047 21.2305 3.87695ZM21.2305 14.8047L22.959 14.8047L22.959 8.20312L21.2305 8.20312ZM16.377 22.9785L19.1504 22.9785C21.6699 22.9785 22.959 21.709 22.959 19.2188L22.959 16.3672L21.2305 16.3672L21.2305 19.1309C21.2305 20.5273 20.459 21.25 19.1211 21.25L16.377 21.25ZM8.13477 22.9785L14.8145 22.9785L14.8145 21.25L8.13477 21.25ZM3.79883 22.9785L6.58203 22.9785L6.58203 21.25L3.83789 21.25C2.4707 21.25 1.72852 20.5273 1.72852 19.1309L1.72852 16.3672L0 16.3672L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM0 14.8047L1.72852 14.8047L1.72852 8.20312L0 8.20312Z", fillAlpha = 0.85f)
        }
        return _sFSquareDashed!!
    }

private var _sFSquareDashed: ImageVector? = null
