package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeGFill (monochrome)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Monochrome.SFPoweroutletTypeGFill: ImageVector
    get() {
        if (_sFPoweroutletTypeGFill != null) {
            return _sFPoweroutletTypeGFill!!
        }
        _sFPoweroutletTypeGFill = sfIcon(
            name = "Monochrome.SFPoweroutletTypeGFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M23.7793 3.78906L23.7793 19.2188C23.7793 21.709 22.5098 22.9785 19.9805 22.9785L3.79883 22.9785C1.2793 22.9785 0 21.7188 0 19.2188L0 3.78906C0 1.28906 1.2793 0.0292969 3.79883 0.0292969L19.9805 0.0292969C22.5098 0.0292969 23.7793 1.29883 23.7793 3.78906ZM5.50781 15.2344C5.07812 15.2344 4.79492 15.5273 4.79492 15.9375L4.79492 16.9238C4.79492 17.3438 5.06836 17.6172 5.50781 17.6172L9.0625 17.6172C9.48242 17.6172 9.76562 17.3438 9.76562 16.9238L9.76562 15.9375C9.76562 15.5176 9.48242 15.2344 9.0625 15.2344ZM14.7266 15.2344C14.2969 15.2344 14.0137 15.5176 14.0137 15.9375L14.0137 16.9238C14.0137 17.3438 14.2969 17.6172 14.7266 17.6172L18.2812 17.6172C18.7109 17.6172 18.9941 17.3438 18.9941 16.9238L18.9941 15.9375C18.9941 15.5273 18.7109 15.2344 18.2812 15.2344ZM11.3965 4.69727C10.9863 4.69727 10.7031 4.98047 10.7031 5.40039L10.7031 9.30664C10.7031 9.73633 10.9863 10.0195 11.3965 10.0195L12.373 10.0195C12.793 10.0195 13.0859 9.73633 13.0859 9.30664L13.0859 5.40039C13.0859 4.98047 12.793 4.69727 12.373 4.69727Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeGFill!!
    }

private var _sFPoweroutletTypeGFill: ImageVector? = null
