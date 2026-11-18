package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCarWindowLeft (dualtone)
 * Viewport: 26.3477 x 22.7441
 */
public val SfSymbols.Dualtone.SFCarWindowLeft: ImageVector
    get() {
        if (_sFCarWindowLeft != null) {
            return _sFCarWindowLeft!!
        }
        _sFCarWindowLeft = sfIcon(
            name = "Dualtone.SFCarWindowLeft",
            viewportWidth = 26.3477f,
            viewportHeight = 22.7441f
        ) {
            addSfPath("M22.1777 22.7441C24.7656 22.7441 25.9863 21.543 25.9863 18.9941L25.9863 12.5586C25.9863 10.8691 25.5078 9.9707 24.1895 8.96484L14.5703 1.66016C12.9883 0.46875 11.7285 0.0292969 9.80469 0.0292969L3.61328 0.0292969C1.24023 0.0292969 0 1.25977 0 3.61328L0 18.9941C0 21.543 1.2207 22.7441 3.80859 22.7441ZM22.4023 11.2402L3.65234 11.2402C2.46094 11.2402 1.72852 10.5273 1.72852 9.30664L1.72852 3.70117C1.72852 2.45117 2.43164 1.75781 3.65234 1.75781L9.80469 1.75781C11.3672 1.75781 12.2852 2.03125 13.3984 2.91992L22.7344 10.0586C23.2812 10.4688 23.1152 11.2402 22.4023 11.2402Z", fillAlpha = 0.85f)
        }
        return _sFCarWindowLeft!!
    }

private var _sFCarWindowLeft: ImageVector? = null
