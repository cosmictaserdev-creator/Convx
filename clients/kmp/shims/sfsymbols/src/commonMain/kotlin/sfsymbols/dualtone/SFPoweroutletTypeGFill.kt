package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPoweroutletTypeGFill (dualtone)
 * Viewport: 24.1406 x 22.9785
 */
public val SfSymbols.Dualtone.SFPoweroutletTypeGFill: ImageVector
    get() {
        if (_sFPoweroutletTypeGFill != null) {
            return _sFPoweroutletTypeGFill!!
        }
        _sFPoweroutletTypeGFill = sfIcon(
            name = "Dualtone.SFPoweroutletTypeGFill",
            viewportWidth = 24.1406f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L19.9805 22.9785C22.5098 22.9785 23.7793 21.709 23.7793 19.2188L23.7793 3.78906C23.7793 1.29883 22.5098 0.0292969 19.9805 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785Z", fillAlpha = 0.2125f)
            addSfPath("M11.3965 10.0195C10.9863 10.0195 10.7031 9.73633 10.7031 9.30664L10.7031 5.40039C10.7031 4.98047 10.9863 4.69727 11.3965 4.69727L12.373 4.69727C12.793 4.69727 13.0859 4.98047 13.0859 5.40039L13.0859 9.30664C13.0859 9.73633 12.793 10.0195 12.373 10.0195ZM5.50781 17.6172C5.06836 17.6172 4.79492 17.3438 4.79492 16.9238L4.79492 15.9375C4.79492 15.5273 5.07812 15.2344 5.50781 15.2344L9.0625 15.2344C9.48242 15.2344 9.76562 15.5176 9.76562 15.9375L9.76562 16.9238C9.76562 17.3438 9.48242 17.6172 9.0625 17.6172ZM14.7266 17.6172C14.2969 17.6172 14.0137 17.3438 14.0137 16.9238L14.0137 15.9375C14.0137 15.5176 14.2969 15.2344 14.7266 15.2344L18.2812 15.2344C18.7109 15.2344 18.9941 15.5273 18.9941 15.9375L18.9941 16.9238C18.9941 17.3438 18.7109 17.6172 18.2812 17.6172Z", fillAlpha = 0.85f)
        }
        return _sFPoweroutletTypeGFill!!
    }

private var _sFPoweroutletTypeGFill: ImageVector? = null
