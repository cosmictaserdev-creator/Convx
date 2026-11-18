package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightswitchOff (dualtone)
 * Viewport: 18.3594 x 27.5879
 */
public val SfSymbols.Dualtone.SFLightswitchOff: ImageVector
    get() {
        if (_sFLightswitchOff != null) {
            return _sFLightswitchOff!!
        }
        _sFLightswitchOff = sfIcon(
            name = "Dualtone.SFLightswitchOff",
            viewportWidth = 18.3594f,
            viewportHeight = 27.5879f
        ) {
            addSfPath("M3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859L0 23.7891C0 26.3184 1.25977 27.5879 3.75977 27.5879L14.2383 27.5879C16.7285 27.5879 17.998 26.3184 17.998 23.7891L17.998 3.80859C17.998 1.28906 16.7285 0.00976562 14.2383 0.00976562ZM3.85742 1.73828L14.1406 1.73828C15.5469 1.73828 16.2695 2.48047 16.2695 3.83789L16.2695 23.7598C16.2695 25.1172 15.5469 25.8594 14.1406 25.8594L3.85742 25.8594C2.45117 25.8594 1.72852 25.1172 1.72852 23.7598L1.72852 3.83789C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828Z", fillAlpha = 0.425f)
            addSfPath("M4.51172 16.25C3.65234 16.25 3.27148 16.6211 3.27148 17.4805L3.27148 23.0762C3.27148 23.9355 3.65234 24.3066 4.51172 24.3066L13.4375 24.3066C14.3066 24.3066 14.6875 23.9355 14.6875 23.0762L14.6875 17.4805C14.6875 16.6211 14.3066 16.25 13.4375 16.25Z", fillAlpha = 0.85f)
        }
        return _sFLightswitchOff!!
    }

private var _sFLightswitchOff: ImageVector? = null
