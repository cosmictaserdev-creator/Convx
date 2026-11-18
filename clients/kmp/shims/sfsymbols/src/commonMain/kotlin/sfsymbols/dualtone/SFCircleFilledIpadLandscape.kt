package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpadLandscape (dualtone)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Dualtone.SFCircleFilledIpadLandscape: ImageVector
    get() {
        if (_sFCircleFilledIpadLandscape != null) {
            return _sFCircleFilledIpadLandscape!!
        }
        _sFCircleFilledIpadLandscape = sfIcon(
            name = "Dualtone.SFCircleFilledIpadLandscape",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L25.7812 22.9785C28.3105 22.9785 29.5898 21.709 29.5898 19.2188L29.5898 3.78906C29.5898 1.29883 28.3105 0.0292969 25.7812 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.83789 21.25C2.4707 21.25 1.72852 20.5273 1.72852 19.1309L1.72852 3.87695C1.72852 2.48047 2.4707 1.75781 3.83789 1.75781L25.752 1.75781C27.0898 1.75781 27.8516 2.48047 27.8516 3.87695L27.8516 19.1309C27.8516 20.5273 27.0898 21.25 25.752 21.25Z", fillAlpha = 0.425f)
            addSfPath("M14.7949 17.7441C18.2324 17.7441 21.0547 14.9414 21.0547 11.4844C21.0547 8.04688 18.2324 5.24414 14.7949 5.24414C11.3477 5.24414 8.53516 8.04688 8.53516 11.4844C8.53516 14.9414 11.3477 17.7441 14.7949 17.7441Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpadLandscape!!
    }

private var _sFCircleFilledIpadLandscape: ImageVector? = null
