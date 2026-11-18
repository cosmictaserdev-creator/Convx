package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOn (monochrome)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Monochrome.SFLightswitchOn: ImageVector
    get() {
        if (_sFLightswitchOn != null) {
            return _sFLightswitchOn!!
        }
        _sFLightswitchOn = sfIcon(
            name = "Monochrome.SFLightswitchOn",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M3.75977 27.5879L14.2383 27.5879C16.7285 27.5879 17.998 26.3086 17.998 23.7891L17.998 3.80859C17.998 1.2793 16.7285 0.00976562 14.2383 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.2793 0 3.80859L0 23.7891C0 26.3086 1.25977 27.5879 3.75977 27.5879ZM3.85742 25.8594C2.45117 25.8594 1.72852 25.1172 1.72852 23.7598L1.72852 3.83789C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L14.1406 1.73828C15.5469 1.73828 16.2695 2.48047 16.2695 3.83789L16.2695 23.7598C16.2695 25.1172 15.5469 25.8594 14.1406 25.8594Z", fillAlpha = 0.85f)
            addSfPath("M4.51172 11.3477L13.4375 11.3477C14.3066 11.3477 14.6875 10.9766 14.6875 10.1172L14.6875 4.52148C14.6875 3.66211 14.3066 3.29102 13.4375 3.29102L4.51172 3.29102C3.65234 3.29102 3.27148 3.66211 3.27148 4.52148L3.27148 10.1172C3.27148 10.9766 3.65234 11.3477 4.51172 11.3477Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOn!!
    }

private var _sFLightswitchOn: ImageVector? = null
