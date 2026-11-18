package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarWindowRight (dualtone)
 * Viewport: 26.3477 x 22.7441
 */
public val SfSymbols.Dualtone.SFCarWindowRight: ImageVector
    get() {
        if (_sFCarWindowRight != null) {
            return _sFCarWindowRight!!
        }
        _sFCarWindowRight = sfIcon(
            name = "Dualtone.SFCarWindowRight",
            viewportWidth = 26.3477f,
            viewportHeight = 22.7441f
        ) {
            addSfPath("M3.80859 22.7441L22.1777 22.7441C24.7754 22.7441 25.9863 21.543 25.9863 18.9941L25.9863 3.61328C25.9863 1.25977 24.7559 0.0292969 22.373 0.0292969L16.1816 0.0292969C14.2578 0.0292969 13.0078 0.46875 11.4258 1.66016L1.79688 8.96484C0.478516 9.9707 0 10.8691 0 12.5586L0 18.9941C0 21.543 1.23047 22.7441 3.80859 22.7441ZM3.59375 11.2402C2.87109 11.2402 2.71484 10.4688 3.25195 10.0586L12.5879 2.91992C13.7109 2.03125 14.6289 1.75781 16.1816 1.75781L22.3438 1.75781C23.5547 1.75781 24.2578 2.45117 24.2578 3.70117L24.2578 9.30664C24.2578 10.5273 23.5352 11.2402 22.3438 11.2402Z", fillAlpha = 0.85f)
        }
        return _sFCarWindowRight!!
    }

private var _sFCarWindowRight: ImageVector? = null
