package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPadHeader (dualtone)
 * Viewport: 25.3809 x 22.9785
 */
public val SfSymbols.Dualtone.SFPadHeader: ImageVector
    get() {
        if (_sFPadHeader != null) {
            return _sFPadHeader!!
        }
        _sFPadHeader = sfIcon(
            name = "Dualtone.SFPadHeader",
            viewportWidth = 25.3809f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M3.79883 22.9785L21.2109 22.9785C23.7402 22.9785 25.0195 21.709 25.0195 19.2188L25.0195 3.78906C25.0195 1.29883 23.7402 0.0292969 21.2109 0.0292969L3.79883 0.0292969C1.2793 0.0292969 0 1.28906 0 3.78906L0 19.2188C0 21.7188 1.2793 22.9785 3.79883 22.9785ZM3.66211 21.25C2.41211 21.25 1.72852 20.5859 1.72852 19.2969L1.72852 7.64648C1.72852 6.34766 2.41211 5.69336 3.66211 5.69336L21.3379 5.69336C22.5781 5.69336 23.2812 6.34766 23.2812 7.64648L23.2812 19.2969C23.2812 20.5859 22.5781 21.25 21.3379 21.25Z", fillAlpha = 0.85f)
        }
        return _sFPadHeader!!
    }

private var _sFPadHeader: ImageVector? = null
